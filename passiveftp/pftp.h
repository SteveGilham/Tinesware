/* pftp.h : passive-capable FTP class.
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

#ifndef pftp_h
#define pftp_h

#ifndef WIN32
typedef int SOCKET;
#define INVALID_SOCKET (-1)
#define SOCKET_ERROR (-1)
#endif


namespace FX {
    class FXString;
    class FXMemoryStream;
}
using FX::FXString;
using FX::FXMemoryStream;

/*
 Follow proposed C++ standard and provide names for
 (unsigned) integer types of specific bit length, to
 replace all the WORD32 or UINT4 or similar things
 elsewhere in the crypto code.
 - Mr. Tines 8-Jan-97
*/
#ifdef WIN32
typedef unsigned char uint8_t;
typedef unsigned short uint16_t;
#ifndef __alpha
typedef unsigned long uint32_t;
#else
typedef unsigned int uint32_t;
#endif
#else
#include <stdint.h>
#endif

#define PASSIVE_FTP_PRODUCT_VERSION "1.11.2971.37821"

class Logger {
public:
    virtual void start(uint32_t bytes, const FXString & name) = 0;
    virtual bool update(uint32_t bytes, const FXString & name) = 0;
    virtual void end(uint32_t bytes, const FXString & name) = 0;
    virtual void error(int error) = 0;
    virtual void error(FXString & error) = 0;
    virtual void choke() = 0;
    virtual void logLine(FXString & line) = 0;
    virtual void logLine(const char * line) = 0;
};

class PFTP 
{
public:
    PFTP(const FXString & server, const FXString & user, const FXString & pass, const FXString & proxy, Logger * feedback);

    // need to provide a feedback callback
    void download(const FXString & file, FXMemoryStream & buffer, bool asc=false);
    void upload(const FXString & file, const uint8_t * what, uint32_t length, int throttle, bool asc=false);

    void setDir(const FXString & dir);
    void mkDir(const FXString & dir);
    void del(const FXString & file);
    void rmDir(const FXString & dir);
    void pwd(FXString & dir);
    void list(FXMemoryStream & stream);
    void nlist(FXMemoryStream & stream);
    void logout(void);

    bool isConnected() const;

    ~PFTP();

private:

    enum {
        CNTRL_PORT = 21,
        BUCKET = 4096
    };

    Logger * monitor;
    SOCKET csock;
    SOCKET dsock;

    bool pauser;  // it's a hack. We're going to
          // stall (refuse further requests) till we get a reply back
          // from server for the current request.

    char lineBuffer[BUCKET];
    int buffered;
	char * proxyServer;
	int proxyPort;


    int connect(const FXString & server);
    void getAsBytes(const FXString & file, SOCKET sock, FXMemoryStream & buffer);
    void login(const FXString & user, const FXString & pass);
    void setTransferType(bool asc);

    void getDataSock(void);
    void sendCmd(const FXString cmd, const FXString & value, FXString & response);
    void responseHandler(const FXString & cmd, FXString & result);

    // responseParser: check first digit of first line of response
    // and take action based on it; set up to read an extra line
    // if the response starts with "1"
    bool responseParser(const FXString & resp);
    void readLine(FXString & line, bool pureFTP=true);


};

#endif

