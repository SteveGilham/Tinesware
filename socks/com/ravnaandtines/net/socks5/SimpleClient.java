package com.ravnaandtines.net.socks5;
import com.ravnaandtines.net.HostPort;
import java.net.*;
import java.io.*;
/**
*  Class SimpleClient - emits a TCP or UDP packet containing a line
*                       input from the terminal to a predesignated host:port
*                       via a SOCKS5 proxy 
* <p>
* Copyright Mr. Tines &lt;tines@windsong.demon.co.uk&gt; 2000
* <p>
* For the purposes of the licence, this source file and the minimum set of
* other source files in package com.ravnaandtines and its sub-
* packages required to link together to satisfy all class
* references not belonging to standard Javasoft-published APIs constitute
* the library.  Thus it is not necessary to distribute source to classes
* that you do not actually use.
* <p>
* Note that Java's dynamic class loading means that the distribution of class
* files (as is, or in jar or zip form) which use this library is sufficient to
* allow run-time binding to any interface compatible version of this library.
* The GLPL is thus far less onerous for Java than for the usual run of 'C'/C++
* library.
* <p>
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Library General Public
* License as published by the Free Software Foundation; either
* version 2 of the License, or (at your option) any later version.
* <p>
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Library General Public License for more details.
* <p>
* You should have received a copy of the GNU Library General Public
* License along with this library; if not, write to the Free
* Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*<p>
* @author Mr. Tines
* @version 1.0 12-Feb-2000
*
*/

public class SimpleClient
{
    static int port = 0;
    static int udpport = 0;
    static InetAddress server;
    static InetAddress socks;
    public static void main(String[] args)
    {
        if(args.length < 1)
        {
            System.out.println("SimpleClient port[:udpport] [serverhost] [sockshost]");
            return;
        }
        try {
            String ports = args[0];
            int colon = ports.indexOf(':');
            String tcp = colon >= 0 ?
              ports.substring(0,colon) : ports;
            port = 0xFFFF & Integer.parseInt(tcp);
            udpport = colon >= 0 ?
              0xFFFF & Integer.parseInt(ports.substring(colon+1)) : port;
            } catch (NumberFormatException nfex) {
            System.out.println("\""+args[0]+"\" is not a port number");
            return;
        }

        try {
        if(args.length > 1)
        {
            server = InetAddress.getByName(args[1]);
        }
        else
        {
            server = InetAddress.getLocalHost();
        }
        } catch (Exception uhex) {
            System.out.println("Could not resolve server"+uhex);
            return;
        }
        try {
        if(args.length > 2)
        {
            socks = InetAddress.getByName(args[2]);
        }
        else
        {
            socks = InetAddress.getLocalHost();
        }
        } catch (Exception uhex) {
            System.out.println("Could not resolve SOCKS server"+uhex);
            return;
        }

        try {
            DataInputStream in = null;
            PrintStream out = null;
            BufferedReader stdin = new BufferedReader(
                new InputStreamReader(System.in));

            DatagramSocket udp = new DatagramSocket();
            byte[] get = new byte[1024];

            InetAddress udpAddr = null;
            int udpPort = 0;

            for(;;)
            {
                String l = stdin.readLine();
                if(null == l) break;
                //out.write(l);
                //out.newLine();

                if(l.charAt(0) != 'u')
                {
                  Socks5Socket socksSocket = new Socks5Socket(socks);
                  socksSocket.setDestination(
                    new HostPort(server, port));
                  in = new DataInputStream(socksSocket.getInputStream());
                  out = new PrintStream(socksSocket.getOutputStream());

                  // TCP connection
                  out.println(l);
                  l = in.readLine();
                  socksSocket.close();
                }
                else
                {
                    int k;
                    // now connect a socket
                    Socket socket = new Socket(socks, 1080);
                    if(!doHandshake(socket)) break;
                    System.out.println("handholding on port "+
                      socket.getLocalPort());

                    byte[] req =
                        Packet.makeRequest(Packet.UDPASSOC,
                          udp.getLocalAddress(), udp.getLocalPort());

                    System.out.print("UDPASSOCing");
                    for(k=0; k<req.length;++k)
                          System.out.print(" "+(0xFF&req[k]));
                    System.out.println("");
                    socket.getOutputStream().write(req);
                    byte[] reply = new byte[10];
                    socket.getInputStream().read(reply);
                    System.out.print("got reply");
                    for(k=0; k<reply.length;++k)
                          System.out.print(" "+(0xFF&reply[k]));
                    System.out.println(" to port "+Packet.getPort(reply));
                    if(Packet.getCommand(reply) != Packet.SUCCESS)
                    {
                      socket.close();
                      System.out.println("Failed - status = "+Packet.getCommand(reply));
                      break;
                    }
                    udpAddr = Packet.getAddress(reply);
                    udpPort = Packet.getPort(reply);

                    // UDP connection
                    byte[] raw = l.getBytes("ASCII");

                    byte[] hdr = Packet.makeRequest(Packet.UDPASSOC,
                          server, udpport);

                    byte[] message = new byte[raw.length+hdr.length];
                    int i;
                    for(i=0; i<raw.length; ++i)
                    {
                        message[i+hdr.length] = raw[i];
                    }
                    for(i=0; i<3; ++i) message[i] = 0;
                    for(i=3; i<hdr.length; ++i) message[i] = hdr[i];

                    DatagramPacket packet
                        = new DatagramPacket(message, message.length,
                        udpAddr, udpPort);
                    System.out.print("Sending "+message.length+" bytes");
                        for(k=0; k<hdr.length;++k)
                          System.out.print(" "+(0xFF&message[k]));
                        System.out.println(" (port "+Packet.getPort(message)+")");
                        System.out.println(new String(message, k, raw.length,"ASCII"));
                    udp.send(packet);
                    udp.setSoTimeout(0x1000);
                    packet = new DatagramPacket(get, get.length);
                    try {
                        udp.receive(packet);
                        byte [] data = packet.getData();
                        // could be fancier
                        int portal = ((data[8]&0xFF)<<8)|(data[9]&0xFF);
                        System.out.print("from "
                          +(data[4]&0xFF)+"."
                          +(data[5]&0xFF)+"."
                          +(data[6]&0xFF)+"."
                          +(data[7]&0xFF)+":"+portal+" ");
                        byte [] addr = packet.getAddress().getAddress();
                        System.out.println("via "
                          +(addr[0]&0xFF)+"."
                          +(addr[1]&0xFF)+"."
                          +(addr[2]&0xFF)+"."
                          +(addr[3]&0xFF)+":"+packet.getPort());


                        l = new String(data, 10, packet.getLength()-10, "ASCII");
                    } catch (InterruptedIOException iiox) {
                        l = iiox.toString();
                    }
System.out.println("closing "+socket);
                    socket.setSoTimeout(1);
                    socket.setSoLinger(false, 1);
                    socket.close();
System.out.println("closed "+socket);
                }
                System.out.println(l);
            }
            } catch (IOException iox) {
            System.out.println(iox);
        }
    }

    private static boolean doHandshake(Socket socksSocket)
    {
        byte[] reply = new byte[2];
        byte[] shake = Packet.makeClientHandshake(Packet.NO_AUTH);
        System.out.println("sending handshake");
        try {
            socksSocket.getOutputStream().write(shake);
            int i = socksSocket.getInputStream().read();
            reply[0] = (byte) i;
            i = socksSocket.getInputStream().read();
            reply[1] = (byte) i;
            } catch (IOException ex) {
            System.out.println(ex);
            return false;
        }

        System.out.println("got reply");
        if(Packet.getServerMethod(reply) != Packet.NO_AUTH)
        {
            System.out.println("bad handshake");
            return false;
        }
        return true;
    }
}
