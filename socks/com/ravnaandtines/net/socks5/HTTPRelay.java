package com.ravnaandtines.net.socks5;
import java.io.*;
import java.net.*;
import com.ravnaandtines.net.*;

/**
*  Class HTTPRelay - sends what was received as a UDP packet as an HTTP POST
*                    to tunnel the UDP - CLIENT SIDE
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

public class HTTPRelay implements Runnable {
  private DatagramPacket onbound;
  private InetAddress from;
  private int fromport;
  private DatagramSocket udp = null;
  private InetAddress HTTPServer;
  private int HTTPport;
  private boolean running = false;
  private Socket echo = null;
  protected static boolean verbose = false;
  private Object[] proxies;

    public HTTPRelay(DatagramPacket onbound, InetAddress from,
      int fromport, DatagramSocket udp,
      InetAddress HTTPServer,int HTTPport,
      Object[] proxies)
    {
      this.onbound=onbound;
      this.from=from;
      this.fromport=fromport;
      this.udp=udp;
      this.HTTPServer = HTTPServer;
      this.HTTPport = HTTPport;
      this.proxies = proxies;
    }

    public void stop()
    {
      running = false;
      try {
      if(null != echo) echo.close();
      } catch (IOException ex) {}
      if(null != udp) udp.close();
    }

    public void run() {
      running = true;
      try {
        if(null == proxies)
          echo = new Socket(HTTPServer, HTTPport);
        else {
          Socks5Socket x;
          if(proxies[0] instanceof InetAddress) echo = x =
            new Socks5Socket((InetAddress)proxies[0]);
          else echo = x =
            new Socks5Socket(proxies[0].toString());
          x.setDestination(proxies, 1, proxies.length-1,
            new HostPort(HTTPServer, HTTPport));
        }
        byte[] v4 = onbound.getAddress().getAddress();
        String header =
        "POST data:,"
        +(v4[0]&0xFF)+"."
        +(v4[1]&0xFF)+"."
        +(v4[2]&0xFF)+"."
        +(v4[3]&0xFF)+"/"
        +onbound.getPort()
        +" HTTP/1.1\r\n"
        +"Content-Type: application/octet-stream\r\n"
        +"Content-Length: "+onbound.getLength()
        +"\r\n\r\n";

        OutputStream out = echo.getOutputStream();
        out.write(header.getBytes("ASCII"));
if(verbose)System.out.println("HTTP relay header = "+header);
        out.write(onbound.getData());

        InputStream in = echo.getInputStream();
        DataInputStream din = new DataInputStream(in);
        // expect to get an ACK "HTTP/1.1 100 Continue"
        // Currently not too forgiving of garbage
        String code = din.readLine();
if(verbose) System.out.println("got header = "+code);
        if(!code.equals("HTTP/1.1 100 Continue")) return;

        // drain header
        do{
          code = din.readLine();
        } while (code.length() > 0);

        // expect to get a "HTTP/1.1 200 OK"
        code = din.readLine();
if(verbose) System.out.println("got header = "+code);
        if(!code.equals("HTTP/1.1 200 OK")) return;
        // drain header
        do{
          code = din.readLine();
        } while (code.length() > 0);

        // now we expect a lot of chunks
        while(running)
        {
          // chunk length in hex
          code = din.readLine();
if(verbose) System.out.println("chunksize = "+code);
          int packetsize = 0;
          try {
            packetsize = Integer.parseInt(code, 16);
          } catch (NumberFormatException nfex) {return;}
          if(0==packetsize) return;

          byte[] data = new byte[packetsize];
          int got = 0;
          while(got < packetsize)
          {
            int delta = in.read(data, got, packetsize-got);
            got += delta;
          }

if(verbose)
{
  for(int v1=0; v1 < data.length; ++v1)
  {
    int v2 = data[v1]&0xFF;
    System.out.print(" ");
    if(v2 < 0x10) System.out.print("0");
    System.out.print(Integer.toHexString(v2));
  }
  System.out.println("");
}

          byte [] back = new byte[data.length+10];
          System.arraycopy(data, 0, back, 10, data.length);
          back[0] = back[1] = back[2] = 0;
          back[3] = Packet.IPV4;
          System.arraycopy(v4, 0, back, 4, 4);
          back[8] = (byte)((onbound.getPort()>>8)&0xFF);
          back[9] = (byte)(onbound.getPort()&0xFF);
          // hand on packet
          DatagramPacket packet =
            new DatagramPacket(back, back.length,from,fromport);
          udp.send(packet);

          // drain trailing crlf
          code = din.readLine();
        }

      } catch (IOException intex) {
if(verbose) intex.printStackTrace(System.out);
      } finally {
        if(null != udp) udp.close();
        try {if(null != echo) echo.close();}
        catch (IOException bogus) {}
      }
    }
  }

