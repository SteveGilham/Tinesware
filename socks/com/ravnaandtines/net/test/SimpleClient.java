package com.ravnaandtines.net.test;
import java.net.*;
import java.io.*;
/**
*  Class SimpleClient - emits a TCP or UDP packet containing a line
*                       input from the terminal to a predesignated host:port
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
    public static void main(String[] args)
    {
        if(args.length < 1)
        {
            System.out.println("SimpleClient port[:udpport] [serverhost]");
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

            Socket socket = new Socket(server, port);
            //BufferedReader in = new BufferedReader(
            //new InputStreamReader(socket.getInputStream()));
            DataInputStream in = new DataInputStream(socket.getInputStream());
            //BufferedWriter out = new BufferedWriter(
            //new OutputStreamWriter(socket.getOutputStream()));
            PrintStream out = new PrintStream(socket.getOutputStream());
            BufferedReader stdin = new BufferedReader(
                new InputStreamReader(System.in));

            DatagramSocket udp = new DatagramSocket();
            byte[] get = new byte[1024];
            udp.setSoTimeout(1000);

            for(;;)
            {
                String l = stdin.readLine();
                if(null == l) break;
                //out.write(l);
                //out.newLine();

                if(l.charAt(0) != 'u')
                {
                    // TCP connection
                    out.println(l);
                    l = in.readLine();
                }
                else
                {
                    // UDP connection
                    byte[] message = l.getBytes("ASCII");
                    DatagramPacket packet
                        = new DatagramPacket(message, message.length,
                        server, udpport);
                    System.out.println("Sending "+message.length+" bytes");
                    udp.send(packet);
                    packet = new DatagramPacket(get, get.length);
                    try {
                        udp.receive(packet);
                        byte [] data = packet.getData();
                        l = new String(data, 0, packet.getLength(),"ASCII");
                    } catch (InterruptedIOException iiox) {
                    l = iiox.toString();
                    }
                }
                System.out.println(l);
            }
            } catch (IOException iox) {
            System.out.println(iox);
        }
    }
}
