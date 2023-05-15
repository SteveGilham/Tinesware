/* pftp.cpp : passive-capable FTP class.
   Copyright 2002 Mr. Tines <Tines@RavnaAndTines.com>

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU Library General Public License as published by the
Free Software Foundation; either version 2, or (at your option) any
later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.  */

#ifdef WIN32
#include <winsock2.h>
#include <ctype.h>
#else
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <errno.h>
#include <unistd.h>
#include <netinet/in.h>
#include <netdb.h>
#include <ctype.h>
#include <time.h>

#define WSAGetLastError(x) errno
#define WSAEWOULDBLOCK EAGAIN
#define SD_BOTH SHUT_RDWR
#define closesocket(s) close(s)
typedef struct sockaddr * PSOCKADDR;
typedef struct sockaddr_in SOCKADDR_IN;
#define WSAENOTCONN ENOTCONN
#define ioctlsocket ioctl

void Sleep(int n)
{
  struct timespec value;
  value.tv_nsec = 1000000 * (n%1000);
  value.tv_sec = n/1000;
  nanosleep(&value, 0);
}

#endif

#include "pftp.h"
#ifdef _MSC_VER
#pragma warning (disable : 4244)
#endif
#include <fx.h>
#include <FXMemoryStream.h>
#ifdef _MSC_VER
#pragma warning (default : 4244)
#endif

PFTP::PFTP(const FXString & server, const FXString & user, const FXString & pass, const FXString & proxy, Logger * feedback)
    : monitor(feedback), csock(INVALID_SOCKET), dsock(INVALID_SOCKET), pauser(false), buffered(0), proxyServer(NULL), proxyPort(-1)
{
    FXString localServer(server);

    if(localServer.left(6)=="ftp://")
        localServer.remove(0, 6);

    FXString host(localServer);
    int i = host.find('/');
    if(i>=0)
        host = host.left(i);

	if(proxy.length() > 0)
	{
		FXString tmp = proxy.after(':');
		proxyPort = FX::FXIntVal(tmp);
		tmp = proxy.before(':');
		proxyServer = new char[tmp.length()+1];
		strcpy(proxyServer, tmp.text());
	}

    int value = connect(host);
    if(value)
    {
        monitor->error(value);
        return;
    }
    login(user, pass);

    bool first = true;
    while(i>=0)
    {
        localServer.remove(0,i);
        i = localServer.find('/', 1);
        FXString dirpart =(i>=0)
            ? localServer.left(i) : localServer;
        if(first)
            first = false;
        else
            dirpart.remove(0,1);
        setDir(dirpart);
    }
}

bool PFTP::isConnected() const
{
    return csock != INVALID_SOCKET;
}

void PFTP::download(const FXString & file, FXMemoryStream & buffer, bool asc)
{
    FXString dummy;

    setTransferType(asc);
    getDataSock();

    if(INVALID_SOCKET == dsock)
        return;

    sendCmd("RETR ", file, dummy);
    getAsBytes(file, dsock, buffer);
}

void PFTP::upload(const FXString & file, const uint8_t * what, uint32_t length, int throttle, bool asc)
{
    FXString dummy;

    setTransferType(asc);
    getDataSock();
    if(INVALID_SOCKET == dsock)
        return;

    sendCmd("STOR ", file, dummy);

    int n = length;
    int offset = 0;
    int delta = 128;
    int count = 0;
    int delay = 10;
    struct timeval waitfor = {1, 0};

    monitor->start(length, file);

    while(offset < n)
    {
        if(!monitor->update(offset, file))
            break;
        if(delta > n-offset) 
        {
            delta = n-offset;
        }

        FD_SET watcher;
        FD_ZERO(&watcher);
#pragma warning (disable : 4127)
        FD_SET(dsock, &watcher);
#pragma warning (default : 4127)
        int code = select(0, NULL, &watcher, NULL, &waitfor);
        if(SOCKET_ERROR == code)
        {
            int error = WSAGetLastError ();
            monitor->error(error);
            break;
        }
        else if(0 == code)
        {
            continue;
        }

        int sent = send(dsock, reinterpret_cast<const char *>(what+offset), delta, 0);
        if(throttle > 0)
            Sleep(1<<throttle);
        if(!monitor->update(offset, file))
            break;

        if(SOCKET_ERROR == sent)
        {
            int error = WSAGetLastError ();
            if(WSAEWOULDBLOCK == error)
			{
                /*
				Sleep(delay*(1<<throttle));
                ++count;
                delay *= 10;
                if(count >= 10)
                {
                    monitor->choke();
                    closesocket(csock);
                    csock = INVALID_SOCKET;
                    break;
                }
                */
                continue;
			}
            monitor->error(error);
            break;
        }
        else
        {
           offset += delta;
           delay = 10;
           count = 0;
        }
        if(!monitor->update(offset, file))
            break;
    }

    if(shutdown(dsock, SD_BOTH))
    {
        int error = WSAGetLastError ();
        monitor->error(error);
    }
    if(closesocket(dsock))
    {
        int error = WSAGetLastError ();
        monitor->error(error);
    }
    dsock = INVALID_SOCKET;

    monitor->end(offset, file);
}

void PFTP::getAsBytes(const FXString & file, SOCKET sock, FXMemoryStream & stream)
{
    char buffer[BUCKET];
    int c=0;

    monitor->start(0, file);
    uint32_t total = 0;
    int count = 0;
    int delay = 10;
    struct timeval waitfor = {1, 0};

    for(;;)
    {
        FD_SET watcher;
        FD_ZERO(&watcher);
#pragma warning (disable : 4127)
        FD_SET(sock, &watcher);
#pragma warning (default : 4127)
        int code = select(0, &watcher, NULL, NULL, &waitfor);
        if(SOCKET_ERROR == code)
        {
            int error = WSAGetLastError ();
            monitor->error(error);
            break;
        }
        else if(0 == code)
        {
            continue;
        }

        c = recv(sock, buffer, BUCKET, 0);
        if(0 == c)
            break;
        else if(c > 0)
        {
            count = 0;
            delay = 10;
            stream.save(buffer, c);
            total+=c;
            monitor->update(total, file);
        }
        else
        {
            int error = WSAGetLastError ();
            if(WSAEWOULDBLOCK == error)
			{
				Sleep(delay);
                ++count;
                delay *= 4;
                if(count >= 5)
                {
                    monitor->choke();
                    closesocket(csock);
                    csock = INVALID_SOCKET;
                    break;
                }
                continue;
			}
            monitor->error(error);
            break;
        }
    }
    monitor->end(total, file);

    if(shutdown(dsock, SD_BOTH))
    {
        int error = WSAGetLastError ();
        monitor->error(error);
    }
    if(closesocket(dsock))
    {
        int error = WSAGetLastError ();
        monitor->error(error);
    }
    dsock = INVALID_SOCKET;
}

int PFTP::connect(const FXString & server)
{
    // Set up socket, control streams, connect to ftp server
    // Open socket to server control port 21
    csock = socket(AF_INET, SOCK_STREAM, 0);
    if(INVALID_SOCKET == csock)
        return WSAGetLastError();

    SOCKADDR_IN saTemp;
    saTemp.sin_family = AF_INET;
    saTemp.sin_port = htons(CNTRL_PORT);

	struct hostent * host = NULL;
	if(proxyServer)
	{
		saTemp.sin_port =  htons((u_short)proxyPort);
		host  = gethostbyname(proxyServer);		
	}
	else
		host  = gethostbyname(server.text());

    if(NULL == host)
    {
        int err = WSAGetLastError();
        closesocket(csock);
        csock = INVALID_SOCKET;
        return err;
    }

    memcpy((char*)&(saTemp.sin_addr), host->h_addr,
      host->h_length);

    if (::connect(csock,(PSOCKADDR)&saTemp,sizeof(saTemp)) == SOCKET_ERROR)
    {
        int err = WSAGetLastError();
        closesocket(csock);
        csock = INVALID_SOCKET;
        return err;
    }

    FXString numerals;

	u_long arg = 1;
	int code = ioctlsocket(csock, FIONBIO, &arg);
	if(code)
		goto failure;
    int sndsize = 256 * 1024;
    code = setsockopt(csock, SOL_SOCKET, SO_SNDBUF, (char *)&sndsize,
        (int)sizeof(sndsize)); 
	if(code)
		goto failure;
    BOOL nagel = FALSE;
    code = setsockopt(csock, IPPROTO_TCP, TCP_NODELAY, (char *)&nagel,
        (int)sizeof(nagel)); 

	if(proxyServer)
	{
		FXString format = "CONNECT "+server+":%d HTTP/1.1\r\nHost: "+server+":%d\r\n\r\n";
		FXString command;
		command.format(format.text(), CNTRL_PORT, CNTRL_PORT);

		// push the connect
		send(csock, command.text(), command.length(), 0);

		// expect HTTP response plus FTP server if all goes well.
		readLine(numerals, false);
		
	}
	else
	{
		responseHandler("", numerals);
	}
        {
                const char * text = numerals.text();
                if('2' == text[0] && '2' == text[1] && '0' == text[2])
                        return 0; // ftp server alive
        }

failure:
    closesocket(csock);
    csock = INVALID_SOCKET;
    return WSAENOTCONN;
}

void PFTP::login(const FXString & user, const FXString & pass)
{
    FXString tmp;
    sendCmd("USER ", user, tmp);
    sendCmd("PASS ", pass, tmp);
}

void PFTP::setDir(const FXString & dir)
{
    FXString tmp;
    sendCmd("CWD ", dir, tmp);
}

void PFTP::mkDir(const FXString & dir)
{
    FXString tmp;
    sendCmd("MKD ", dir, tmp);
}

void PFTP::del(const FXString & file)
{
    FXString tmp;
    sendCmd("DELE ", file, tmp);
}

void PFTP::rmDir(const FXString & file)
{
    FXString tmp;
    sendCmd("RMD ", file, tmp);
}


void PFTP::pwd(FXString & dir)
{
    sendCmd("PWD", "", dir);
    dir = dir.right(dir.length()-5);
    dir = dir.left(dir.rfind("\""));
}

void PFTP::list(FXMemoryStream & stream)
{
    getDataSock();
    if(INVALID_SOCKET == dsock)
        return;
    FXString tmp;
    sendCmd("LIST", "", tmp);
    getAsBytes("", dsock, stream);
}

void PFTP::nlist(FXMemoryStream & stream)
{
    getDataSock();
    if(INVALID_SOCKET == dsock)
        return;
    FXString tmp;
    sendCmd("NLST", "", tmp);
    getAsBytes("", dsock, stream);
}

void PFTP::setTransferType(bool asc)
{
    FXString tmp;
    sendCmd("TYPE ",(asc? "A" : "I"), tmp);
}

void PFTP::readLine(FXString & line, bool pureFTP)
{
	bool justFTP = pureFTP;
    line = "";
    lineBuffer[buffered] = 0;
    int count = 0;
    int delay = 10;
    char * eol = strchr(lineBuffer, '\r');
    struct timeval waitfor = {1, 0};

    while(!eol)
    {

        FD_SET watcher;
        FD_ZERO(&watcher);
#pragma warning (disable : 4127)
        FD_SET(csock, &watcher);
#pragma warning (default : 4127)
        int code = select(0, &watcher, NULL, NULL, &waitfor);
        if(SOCKET_ERROR == code)
        {
            int error = WSAGetLastError ();
            monitor->error(error);
            break;
        }
        else if(0 == code)
        {
            continue;
        }

		int c = recv(csock, lineBuffer+buffered, BUCKET-(buffered+1), 0);
        if(0 == c)
        {
            eol = lineBuffer+buffered;
        }
        else if(c > 0)
        {
            count = 0;
            delay = 10;
            buffered += c;
            lineBuffer[buffered] = 0;
			if(justFTP) 
				eol = strchr(lineBuffer, '\r');
			else
			{
				char * eoh = strstr(lineBuffer, "\r\n\r\n");
				if(!eoh)
					continue;

				if(FX::compare(lineBuffer, "HTTP/1.", 7))
				{
					eol = eoh;
				}
				else
				{
					char * code = strchr(lineBuffer, ' ')+1;
					if('2' != code[0])
						eol = eoh;
					else
					{
						justFTP = true;
						size_t hlen = eoh+4 - lineBuffer;
						size_t left = buffered - hlen;
						memmove(lineBuffer, eoh+4, left);
						buffered = (int) left;
						lineBuffer[buffered] = 0;
						eol = strchr(lineBuffer, '\r');
					}
				}
			}
        }
        else
        {
            int error = WSAGetLastError ();
            if(WSAEWOULDBLOCK == error)
			{
				Sleep(delay);
                ++count;
                delay *= 10;
                if(count >= 5)
                {
                    monitor->choke();
                    closesocket(csock);
                    csock = INVALID_SOCKET;
                    break;
                }
                continue;
			}
            monitor->error(error);
            eol = lineBuffer+buffered;
        }
    }

    if(eol)
    {
        size_t n = eol - lineBuffer;
        *eol = 0;
        line += lineBuffer;
        if(n < (size_t) buffered)
        {
            buffered -= (int)(n+2);
            memmove(lineBuffer, eol+2, buffered);
		    lineBuffer[buffered] = 0;
        }
        else
        {
            buffered = 0;
        }
    }

    monitor->logLine(line);
}

void PFTP::getDataSock()
{
    FXString reply;
        // Go to PASV mode, capture server reply, parse for socket setup
        // V2.1: generalized port parsing, allows more server variations
    sendCmd("PASV","",reply);
    int n = reply.length();
    char * text = const_cast<char *>(reply.text());
    char * parts[6];// parts, incl. some garbage

    // New technique: just find numbers before and after ","!
    int index = 0;
    parts[index++] = text;
    for(int i=0; i<n && index < 6; ++i)
    {
        // stick pieces of host, port in String array
        if(text[i] != ',') continue;
        text[i] = 0;
        parts[index++] = text+i+1;
    }// end getting parts of host, port
    for(int i2=index; i2<6; ++i2) parts[i2] = 0;

    // Get rid of everything before first "," except digits
    FXString start(parts[0]);
    FXString possNum[3];
    for(int j = 0; j < 3; j++) {
        // Get 3 characters, inverse order, check if digit/character
        possNum[j] = start.mid(start.length() - (j + 1), 1); // next: digit or character?
        if(!isdigit(possNum[j].text()[0]))
            possNum[j] = "";
    }
    start = possNum[2] + possNum[1] + possNum[0];

    // Get only the digits after the last ","
    FXString porties[3];
    FXString end(parts[5]);
    for(int k = 0; k < 3; k++) {
        // Get 3 characters, in order, check if digit/character
        // May be less than 3 characters
        if((k + 1) <= end.length())
            porties[k] = end.mid(k, 1);
        else porties[k] = "$"; // definitely not a digit!
        // next: digit or character?
        if(!isdigit(porties[k].text()[0]))
                porties[k] = "";
    } // Have to do this one in order, not inverse order
    end = porties[0] + porties[1] + porties[2];

    // Determine port
    int bigend = FXIntVal(parts[4], 10) << 8;
    int smallend = FXIntVal(end, 10);
    int port = bigend + smallend; // port number

    // Set up socket, control streams, connect to ftp server
    // Open socket to server control port 21
    dsock = socket(AF_INET, SOCK_STREAM, 0);
    if(INVALID_SOCKET == dsock)
        return;

    SOCKADDR_IN saTemp;
    saTemp.sin_family = AF_INET;
    saTemp.sin_port =  htons((u_short)port);

    char * ip = (char*)&(saTemp.sin_addr);
    ip[0] = (char) FXIntVal(start, 10);
    ip[1] = (char) FXIntVal(parts[1], 10);
    ip[2] = (char) FXIntVal(parts[2], 10);
    ip[3] = (char) FXIntVal(parts[3], 10);

	if(proxyServer)
	{
		saTemp.sin_port =  htons((u_short)proxyPort);
		struct hostent * host = gethostbyname(proxyServer);		
		if(NULL == host)
		{
			closesocket(dsock);
			dsock = INVALID_SOCKET;
			return;
		}

		memcpy((char*)&(saTemp.sin_addr), host->h_addr,
			host->h_length);
	}

    if (::connect(dsock,(PSOCKADDR)&saTemp,sizeof(saTemp)) == SOCKET_ERROR)
    {
        monitor->error(WSAGetLastError());
        closesocket(dsock);
        dsock = INVALID_SOCKET;
		return;
    }

	u_long arg = 1;
	int code = ioctlsocket(dsock, FIONBIO, &arg);
	if(code)
    {
        monitor->error(WSAGetLastError());
        closesocket(dsock);
        dsock = INVALID_SOCKET;
		return;
    }
    int sndsize = 256 * 1024;
    code = setsockopt(dsock, SOL_SOCKET, SO_SNDBUF, (char *)&sndsize,
        (int)sizeof(sndsize)); 
	if(code)
    {
        monitor->error(WSAGetLastError());
        closesocket(dsock);
        dsock = INVALID_SOCKET;
		return;
    }


	if(proxyServer)
	{
		FXString numerals;
		FXString server = start +"."+parts[1]+"."+parts[2]+"."+parts[3];
		FXString format = "CONNECT "+server+":%d HTTP/1.0\r\nHost: "+server+":%d\r\n\r\n";
		FXString command;
		command.format(format.text(), port, port);

		// push the connect
		send(dsock, command.text(), command.length(), 0);

		// expect HTTP response plus FTP server if all goes well.
		char hdr[BUCKET];
		size_t got = 0;
		char * eoh = NULL;
		while(!eoh)
		{
			int c = recv(dsock, hdr+got, (int)(BUCKET-(got+1)), 0);
			if(0 == c)
			{
				goto failed;
			}
			if(c > 0)
			{
				got += c;
				hdr[got] = 0;
				eoh = strstr(hdr, "\r\n\r\n");
				if(!eoh)
					continue;

				if(FX::compare(hdr, "HTTP/1.", 7))
				{
					goto failed;
				}

				char * code = strchr(hdr, ' ')+1;
				if('2' != code[0])
						goto failed;

				continue;
			}

			int error = WSAGetLastError ();
			if(WSAEWOULDBLOCK == error)
			{
				Sleep(10);
                continue;
			}
			goto failed;
		}
	}

	return;

failed:
    monitor->error(WSAGetLastError());
    closesocket(dsock);
    dsock = INVALID_SOCKET;
}


void PFTP::sendCmd(const FXString cmd, const FXString & value, FXString & response)
{ // This sends a dialog string to the server, returns reply
    // V2.0 Updated to parse multi-string responses a la RFC 959
    // Prints out only last response string of the lot.
    if (pauser) // i.e. we already issued a request, and are
                // waiting for server to reply to it.
    {
        FXString discard;
        readLine(discard); // will block here
            pauser = false;
    }
    
    // assume no blocking here
    FXString line = cmd+value+"\r\n";

    send(csock, line.text(), line.length(), 0);

    responseHandler(cmd+value, response);
    if(response.text()[0] == '5')
    {
        monitor->error(response);
    }

}

     // new method to read multi-line responses
     // responseHandler: takes a String command or null and returns
     // just the last line of a possibly multi-line response
void PFTP::responseHandler(const FXString & , FXString & reply)
{ // handle more than one line returned

    readLine(reply);
    if(!responseParser(reply))
    {
        reply = "";
        return;
    }

    FXString numerals = reply.left(3);
    FXString hyph_test = reply.mid(3, 1);
    FXString next = "";
    if(hyph_test == "-") 
    {
        // Create "tester", marks end of multi-line output
        FXString tester = numerals + " ";
        bool done = false;
        while(!done) 
        { // read lines til finds last line
            readLine(next);
            // Read "over" blank line responses
            while (next == "" || next == "  ") 
            {
                readLine(next);
            }

            // If next starts with "tester", we're done
            if(next.left(4) == (tester))
                done = true;
        }
        reply = next;
    }
}

// responseParser: check first digit of first line of response
// and take action based on it; set up to read an extra line
// if the response starts with "1"
bool PFTP::responseParser(const FXString & resp)
{ // Check first digit of resp, take appropriate action.
    
    char digit1 = resp.text()[0];
    if(digit1=='1')
    {
        // server to act, then give response
        // set pauser
        pauser = true;
        return true;
    }
    else if(digit1=='2') { // do usual handling
        // clear pauser
        pauser = false;
        return true;
    }
    else if(digit1=='3' || digit1=='4'
        || digit1=='5') { // do usual handling
        return true;
    }
    else { // not covered, so return null
        return false;
    }
}

void PFTP::logout(void) 
{// logout, close streams

    if(INVALID_SOCKET != csock)
    {
        FXString tmp;
        sendCmd("QUIT", "", tmp);
        shutdown(csock, SD_BOTH);
        closesocket(csock);
        csock = INVALID_SOCKET;
    }
    if(INVALID_SOCKET != dsock)
    {
        shutdown(dsock, SD_BOTH);
        closesocket(dsock);
        csock = INVALID_SOCKET;
    }
}

PFTP::~PFTP()
{
    logout();
	if(proxyServer)
		delete[] proxyServer;
}
