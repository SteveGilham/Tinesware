package com.ravnaandtines.net.test;
import java.io.*;
import java.net.*;


/**
*  Class RechoServer - listens for a TCP connect and reverse-echoes it line by line;
*                      or reverse-echoes UDP packets
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

public class RechoServer extends Thread
{
  protected Socket socket;
  static int port = 0;
  static int udpport = 0;

  public RechoServer(Socket socket) {
    this.socket = socket;
  }

  public void run () {
    try {
      DataInputStream in = new DataInputStream(socket.getInputStream());
      PrintStream out = new PrintStream(socket.getOutputStream());
      System.out.println("Streams acquired");

      for(;;)
      {
        System.out.println("Reading");
        String l = in.readLine();
        if(null == l) break;
        if(l.length() == 0) break;
        System.out.println("Got \""+l+"\"");
        StringBuffer work = new StringBuffer(l);
        work.reverse();
        out.println("TCP recho: "+work.toString());
      }
    } catch (IOException ex) {
      ex.printStackTrace ();
    } finally {
      try {
        socket.close ();
      } catch (IOException ignored) {
      }
    }
  }

  public static void main (String[] args) throws IOException, InterruptedException
  {
    if(args.length < 1)
    {
      System.out.println("RechoServer port [udp-port]");
      return;
    }
    try {
      udpport = port = Integer.parseInt(args[0]);
    } catch (NumberFormatException nfex) {
      System.out.println("\""+args[0]+"\" is not a port number");
      return;
    }
    port &= 0xFFFF;
    if(args.length > 1) { try {
      udpport = Integer.parseInt(args[1]);
    } catch (NumberFormatException nfex) {
      System.out.println("\""+args[1]+"\" is not a port number");
      return;
    }}
    udpport &= 0xFFFF;

    // TCP
    Thread tcp = new Thread ( new Runnable () {
      public void run() { try {
        ServerSocket server = new ServerSocket (RechoServer.port);
        while (true) {
          Socket client = server.accept ();
          RechoServer echo = new RechoServer (client);
          echo.start ();
        } } catch (IOException tcpex) {}
      }});
    tcp.start();
    // UDP
    try {
      DatagramSocket udp = new DatagramSocket(udpport);
      byte[] get = new byte[0x10001];
      for(;;)
      {
        DatagramPacket packet = new DatagramPacket(get, get.length);
        udp.receive(packet);
        byte [] data = packet.getData();
        String l = new String(data, 0, packet.getLength(), "ASCII");
        InetAddress client = packet.getAddress();
        int client_port = packet.getPort();
        System.out.println(l+" : "+packet.getLength()+ " bytes from "+client+":"+client_port);
        StringBuffer work = new StringBuffer(l);
        work.reverse();
        l = "UDP recho: "+work.toString();
        byte[] message = l.getBytes("ASCII");
        packet = new DatagramPacket(message, message.length,
                client, client_port);
        udp.send(packet);
      }} catch (IOException iox) {
        System.out.println(iox);
    }

    // finish
    tcp.join();
  }
}