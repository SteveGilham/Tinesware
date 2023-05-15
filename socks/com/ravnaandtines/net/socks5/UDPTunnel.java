package com.ravnaandtines.net.socks5;
import java.io.*;
import java.net.*;

/**
*  Class UDPTunnel - a non-authenticating SOCKS5 proxy that supports the BIND, UDPASSOC &
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

public class UDPTunnel extends BindProxy
{
  protected static boolean verbose = false;

  public static void main (String[] args) throws IOException {
    int hport = 0;
    InetAddress hserver = null;
    if(args.length < 1)
    {
      System.out.println("UDPTunnel port [serverhost]");
      return;
    }
    try {
      hport = 0xFFFF & Integer.parseInt(args[0]);
    } catch (NumberFormatException nfex) {
      System.out.println("\""+args[0]+"\" is not a port number");
      return;
    }

    try {
      if(args.length > 1)
      {
        hserver = InetAddress.getByName(args[1]);
      }
      else
      {
        hserver = InetAddress.getLocalHost();
      }
    } catch (Exception uhex) {
      System.out.println("Could not resolve server"+args[1]);
      return;
    }
    ServerSocket serversock = new ServerSocket (port);
    HTTPRelay.verbose = ConnectProxy.verbose = verbose = args.length > 2;
if(verbose) System.out.println("debug mode");
    while (true) {
      Socket client = serversock.accept ();
      UDPTunnel relay = new UDPTunnel (client, hserver, hport);
      relay.start ();
    }
  }

  private InetAddress HTTPserver= null;
  private int HTTPport = -1;
  private boolean associated = false;
  private Thread proxy = null;
  private HTTPRelay partner = null;
  protected DatagramSocket udp = null;

  protected void doUDP(byte[] command) throws IOException
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

        proxy = new Thread ( partner =
          new HTTPRelay(onbound, from, fromport,
            udp, HTTPserver, HTTPport, proxies));
        proxy.start();
      }

    } catch (UnknownHostException uhe) {
      doError(command, Packet.HOSTUNREACH);
    } finally {
if(verbose) System.out.println("UDP Tunnelling closed");
      if(null != partner) partner.stop();
      udp.close();
    }
  }

  public UDPTunnel(Socket socket, InetAddress HTTPserver, int HTTPport)
    throws IOException
  {
    super(socket);
    this.HTTPserver = HTTPserver;
    this.HTTPport = HTTPport;
if(verbose) System.out.println("got connection");
  }
}
