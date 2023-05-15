package com.ravnaandtines.net.socks5;
import java.io.*;
import java.net.*;

/**
*  Class UDPProxy - a non-authenticating SOCKS5 proxy that supports the BIND, UDPASSOC &
*                       CONNECT commands.  Because it is non-authentricating it is
*                       strictly in violation of the last paragraph of section 3
*                       of RFC 1928
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

public class UDPProxy extends BindProxy
{
  public static void main (String[] args) throws IOException {
    ServerSocket server = new ServerSocket (port);
    verbose = args.length > 0;
if(verbose) System.out.println("debug mode");
    while (true) {
      Socket client = server.accept ();
      UDPProxy relay = new UDPProxy (client);
      relay.start ();
    }
  }
  private boolean associated = false;
  protected DatagramSocket udp = null;

  protected void doUDP(byte[] command) throws /*InterruptedException, */IOException
  {
    if(Packet.UDPASSOC != command[1]) {doError(command, Packet.FAIL); return;}
    try {
      // parse the packet source
      InetAddress source = null;
      int sourcePort = Packet.getPort(command);
      switch(command[3])
      {
        case Packet.IPV4:
          if(( 0 == command[4] ) &&
        ( 0 == command[5] ) &&
        ( 0 == command[6] ) &&
        ( 0 == command[7] )) source = socket.getInetAddress();
        else source = Packet.getAddress(command);
        break;
        case Packet.DOMAINNAME:
           source = Packet.getAddress(command);
        break;
        default:
        doError(command, Packet.ADDRESSNOTSUPPORTED);
        return;
      }

      // ready to listen
      udp = new DatagramSocket();

      // say all OK
      byte[] response = Packet.makeResponse(Packet.SUCCESS,
        /*udp.getLocalAddress()*/
        InetAddress.getLocalHost(), udp.getLocalPort());
      doError(response, Packet.SUCCESS);
      associated = true;
      Thread monitor = new Thread( new Runnable() {
        public void run() {
          try { while(in.read()>0) {;}}
          catch (IOException ex) {if(verbose)ex.printStackTrace(System.out);}
          finally {
if(verbose)System.out.println("Client TCP Connection dropped");
            associated = false; udp.close();}
      }});
      monitor.start();

      byte[] get = new byte[0x10001];
      while(associated)
      {
        DatagramPacket packet = new DatagramPacket(get, get.length);
        udp.setSoTimeout(0x7FFF);
        udp.receive(packet);

        InetAddress from = packet.getAddress();
        int fromport = packet.getPort();
if(verbose)
{
  System.out.println("Got UDP packet from "+from+":"+fromport);
  System.out.println("Expect UDP packet from "+source+":"+sourcePort);
  if(!source.equals(from)) System.out.println("address mismatch");
  if((sourcePort >= 1) && (sourcePort != fromport)) System.out.println("port mismatch");
}
        if(!source.equals(from))continue;
        if((sourcePort >= 1) && (sourcePort != fromport)) continue;

        int length = packet.getLength();
if(verbose) System.out.println("UDP packet length = "+length);

        if(length < 5) continue;
        byte[] contents = packet.getData();
if(verbose)
{
  for(int v1=0; v1 < length; ++v1)
  {
    int v2 = contents[v1]&0xFF;
    System.out.print(" ");
    if(v2 < 0x10) System.out.print("0");
    System.out.print(Integer.toHexString(v2));
  }
  System.out.println("");
}
        // 2 zero bytes
        if(contents[0] != 0) continue;
        if(contents[1] != 0) continue;
        // not handling fragmentation
        if(contents[2] != 0) continue;

        InetAddress to = Packet.getAddress(contents);
        if(null == to) continue;
        int toport = Packet.getPort(contents);

        int header = Packet.getPacketLength(contents);
        byte[] payload = new byte[length-header];
if(verbose) System.out.println("payload length = "+payload.length);

        System.arraycopy(contents, header, payload, 0, payload.length);

        DatagramPacket onbound = new DatagramPacket(payload, payload.length,
          to, toport);
if(verbose) System.out.println("onbound length = "+onbound.getLength());

        Thread proxy = new Thread (
          new Relay(onbound, from, fromport, udp, proxies));
        proxy.start();
      }

    } catch (UnknownHostException uhe) {
      doError(command, Packet.HOSTUNREACH); return;
    } finally {
      udp.close();
    }
  }

  private class Relay implements Runnable {
    DatagramPacket onbound;
    InetAddress from;
    int fromport;
    DatagramSocket udp;
    DatagramSocket echo;
    Object[] proxies;

    public Relay(DatagramPacket onbound, InetAddress from, int fromport,
      DatagramSocket udp, Object[] proxies)
    {
      this.onbound=onbound;
      this.from=from;
      this.fromport=fromport;
      this.udp=udp;
      this.proxies=proxies;
    }
    public void run() {
      try {
        if(null == proxies)
          echo = new DatagramSocket();
        else
        {
          Socks5DatagramSocket x = new Socks5DatagramSocket();
          x.setProxies(proxies);
          echo = x;
        }
        echo.send(onbound);
        //udp.send(onbound);
        byte[] get = new byte[0x10001];
        DatagramPacket packet = new DatagramPacket(get, get.length);
        try {
        echo.setSoTimeout(0x7FFF);
        //udp.setSoTimeout(0x7FFF);
        while (associated) {
          echo.receive(packet);
          //udp.receive(packet);
          int length = packet.getLength();
          byte [] payload = packet.getData();
if(verbose)
{
  System.out.println("received length = "+length);
  for(int v1=0; v1 < length; ++v1)
  {
    int v2 = payload[v1]&0xFF;
    System.out.print(" ");
    if(v2 < 0x10) System.out.print("0");
    System.out.print(Integer.toHexString(v2));
  }
  System.out.println("");
}

          byte [] back = new byte[length+10];
          System.arraycopy(payload, 0, back, 10, length);
          back[0] = back[1] = back[2] = 0;
          back[3] = Packet.IPV4;
          byte [] addr = packet.getAddress().getAddress();
          System.arraycopy(addr, 0, back, 4, 4);
          back[8] = (byte)((packet.getPort()>>8)&0xFF);
          back[9] = (byte)(packet.getPort()&0xFF);
          packet = new DatagramPacket(back, back.length,from,fromport);
          udp.send(packet);
        }} catch (InterruptedIOException intex) {}
      } catch (IOException intex) {}
      finally {
        echo.close();
        udp.close();
      }
    }
  }

  public UDPProxy(Socket socket) throws IOException
  {
    super(socket);
  }
}
