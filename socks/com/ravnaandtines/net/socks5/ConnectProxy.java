package com.ravnaandtines.net.socks5;
import java.io.*;
import java.net.*;
import com.ravnaandtines.util.io.Pump;
import com.ravnaandtines.net.HostPort;

/**
*  Class ConnectProxy - a non-authenticating SOCKS5 proxy that supports only the
*                       CONNECT command.  Because it is non-authentricating it is
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

public class ConnectProxy extends Thread
{
  protected Socket socket = null;
  public static final int port = 1080;
  protected Socket relay = null;
  protected static boolean verbose = false;
  protected Object[] proxies;
  public static void setVerbose(boolean b) {HTTPRelay.verbose = verbose = b;}

  public static void main (String[] args) throws IOException {
    ServerSocket server = new ServerSocket (port);
    while (true) {
      Socket client = server.accept ();
      ConnectProxy cproxy = new ConnectProxy (client);
      cproxy.start ();
    }
  }

  public ConnectProxy(Socket socket) throws IOException
  {
    super();
    this.socket = socket;
  }

  public void setProxies(Object[] proxies)
  {
    this.proxies = proxies;
  }

  protected InputStream input = null;
  protected OutputStream output = null;
  protected InputStream in = null;
  protected OutputStream out = null;

  protected void doError(byte[] command, byte err) throws IOException
  {
    command[1] = err;
    out.write(command);
    out.flush();
    return;
  }

  protected void doConnect(byte[] command) throws InterruptedException, IOException
  {
    // attempt to make the connection through...
    if(Packet.CONNECT != command[1]) {doError(command, Packet.FAIL); return;}
    try {
      InetAddress target = Packet.getAddress(command);
      if(null == target) {doError(command, Packet.HOSTUNREACH); return;}
      // now try to connect
      try {
        if(null == proxies)
          relay = new Socket(target, Packet.getPort(command));
        else {
          Socks5Socket x;
          if(proxies[0] instanceof InetAddress) relay = x =
            new Socks5Socket((InetAddress)proxies[0]);
          else relay = x =
            new Socks5Socket(proxies[0].toString());
          x.setDestination(proxies, 1, proxies.length-1,
            new HostPort(target, Packet.getPort(command)));
        }
if(verbose) System.out.println("new relay "+relay);
      } catch (IOException ioe) {
        doError(command, Packet.REFUSED); return;
      }
      input = relay.getInputStream();
      output = relay.getOutputStream();
      byte[] response = Packet.makeResponse(Packet.SUCCESS,
        relay.getLocalAddress(), relay.getLocalPort());
      doError(response, Packet.SUCCESS);
    } catch (UnknownHostException uhe) {
      doError(command, Packet.HOSTUNREACH); return;
    }
    Pump a = new Pump(in, output, verbose);
    Pump b = new Pump(input, out, verbose);
    a.start();
    b.start();
if(verbose) System.out.println("pumping");
    for(;;)
    {
      try {Thread.sleep(500);} catch (InterruptedException iex) {}
      if(!a.isAlive())
      {
if(verbose) System.out.println("client closed connection");
        break;
      }
      if(!b.isAlive())
      {
if(verbose) System.out.println("server closed connection");
        break;
      }
    }
  }

  protected void doBind(byte[] command) throws InterruptedException, IOException
  {
    if(Packet.BIND != command[1]) throw new InterruptedException();
    command[1] = Packet.COMMANDNOTSUPPORTED;
    out.write(command);
    out.flush();
    return;
  }

  protected void doUDP(byte[] command) throws InterruptedException, IOException
  {
    if(Packet.UDPASSOC != command[1]) throw new InterruptedException();
    command[1] = Packet.COMMANDNOTSUPPORTED;
    out.write(command);
    out.flush();
    return;
  }

  // match methods
  protected byte[] recogniseAuth(byte [] shake)
  {
    return Packet.makeServerHandshake(
           Packet.isMethodInHandshake(shake, Packet.NO_AUTH)?
           Packet.NO_AUTH : Packet.BAD_AUTH);
  }
  // perform method
  protected boolean doAuth(byte method)
  {
    return Packet.NO_AUTH == method;
  }

  public void run()
  {
     try {
       in = socket.getInputStream();
       out = socket.getOutputStream();

       // read and parse the handshake
       byte[] shake = Packet.readHandshakeAsServer(in);
       // drop like a stone if malformed input
       if(null == shake) return;
       // accept only non-auth handshake
       byte[] response = recogniseAuth(shake);
       out.write(response);
       out.flush();

       if(!doAuth(response[1])) return;

       // now expect a command packet
       byte[] command = Packet.readCommandAsServer(in);
       // drop like a stone if malformed input
       if(null == command) return;

       int cmd = Packet.getCommand(command);
       if((cmd & Packet.BADFLAG) != 0)
       {
          // unsupported address - bounce it back
          command[1] = (byte)cmd;
          out.write(response);
          out.flush();
          return;
       }

       switch (cmd)
       {
          case Packet.CONNECT:
            doConnect(command); break;
          case Packet.BIND:
            doBind(command); break;
          case Packet.UDPASSOC:
            doUDP(command); break;
          default:
            command[1] = Packet.COMMANDNOTSUPPORTED;
            out.write(response);
            out.flush();
            return;
       }
     }
     catch(java.lang.InterruptedException ex)
     {
     }
     catch(java.io.IOException ex)
     {
     }
     finally {
if(verbose) System.out.println("ConnectProxy.run() closing sockets");
        try {
if(verbose) System.out.println("CP closing relay "+relay);
          if(null != relay) relay.close();
        } catch (java.io.IOException exex) {}
        try {
if(verbose) System.out.println("CP closing socket "+socket);
          if(null != socket) socket.close();
        } catch (java.io.IOException exex) {}
     }
  }

}