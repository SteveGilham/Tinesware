package com.ravnaandtines.net.socks5;

import com.ravnaandtines.net.*;
import java.net.*;
import java.io.*;
/**
*  Class Socks5DatagramSocket - a UDP socket that does a non-authenticating
*                               SOCKS5 UDP association using its setProxies method
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
* @version 1.0 18-Feb-2000
*
*/
public class Socks5DatagramSocket extends DatagramSocket
{
  Object[] chain = null;
  Socket[] tig = null;
  byte[] header = null;
  InetAddress firstProxy = null;
  int firstPort = -1;

  /**
  * Constructs a datagram socket and binds it to any available port
  * on the local host machine.
  *
  * @exception  SocketException  if the socket could not be opened,
  *               or the socket could not bind to the specified local port.
  */
  public Socks5DatagramSocket() throws SocketException
  {
    super();
  }
  /**
  * Constructs a datagram socket and binds it to the specified port
  * on the local host machine.
  *
  * @param      local   port to use.
  * @exception  SocketException  if the socket could not be opened,
  *               or the socket could not bind to the specified local port.
  */
  public Socks5DatagramSocket(int port) throws SocketException
  {
	  super(port&0xFFFF);
  }
  /**
  * Creates a datagram socket, bound to the specified local
  * address.  The local port must be between 0 and 65535 inclusive.
  * @param port local port to use
  * @param laddr local address to bind
  */
  public Socks5DatagramSocket(int port, InetAddress laddr) throws SocketException
  {
    super(port&0xFFFF, laddr);
  }

  /**
  * Sends a datagram packet from this socket. The
  * <code>DatagramPacket</code> includes information indicating the
  * data to be sent, its length, the IP address of the remote host,
  * and the port number on the remote host.
  *
  * @param      p   the <code>DatagramPacket</code> to be sent.
  * @exception  IOException  if an I/O error occurs.
  */
  public void send(DatagramPacket p) throws IOException
  {
    if(null == chain) throw new Socks5Exception("No proxies set");
    // build header + final SOCKS header + data and
    // send to first

    byte[] hdr = Packet.makeRequest(Packet.UDPASSOC,
      p.getAddress(), p.getPort());
    int i;
    for(i=0; i<3; ++i) hdr[i] = 0;
    int base = (null==header)?0:header.length;

    byte[] message = new byte[base+hdr.length+p.getLength()];
    if(header != null) System.arraycopy(header,0, message, 0, base);
    System.arraycopy(hdr,0,message,base,hdr.length);
    System.arraycopy(p.getData(), 0, message, base+hdr.length, p.getLength());

    // build new, bigger, packet
    p = new DatagramPacket(message, message.length,
      firstProxy,firstPort);
    // and send it
    super.send(p);
  }

  /**
  * Receives a datagram packet from this socket. When this method
  * returns, the <code>DatagramPacket</code>'s buffer is filled with
  * the data received. The datagram packet also contains the sender's
  * IP address, and the port number on the sender's machine.
  * <p>
  * This method blocks until a datagram is received. The
  * <code>length</code> field of the datagram packet object contains
  * the length of the received message. If the message is longer than
  * the buffer length, the message is truncated.
  *
  * @param      p   the <code>DatagramPacket</code> into which to place
  *                 the incoming data.
  * @exception  IOException  if an I/O error occurs.
  */
  public void receive(DatagramPacket p) throws IOException
  {
    if(null == chain) throw new Socks5Exception("No proxies set");
    super.receive(p);

    // need to strip away the headers
    byte [] payload = p.getData();
    int total = p.getLength();
    int i = 0;
    int offset = 0;
    for(i=0; i<chain.length-1; ++i)
    {
      offset += Packet.getPacketLength(payload, offset);
    }
    InetAddress ultimate= Packet.getAddress(payload, offset);
    int port = Packet.getPort(payload, offset);
    offset += Packet.getPacketLength(payload, offset);
    byte[] out = new byte[total-offset];
    System.arraycopy(payload, offset, out, 0, out.length);
    p = new DatagramPacket(out, out.length, ultimate, port);
  }


  public Object[] getProxies() { return chain; }

  public void setProxies(Object[] proxies) throws IOException
  {
    if(null != chain)  throw new Socks5Exception("Proxies already set");
    chain = proxies;
    tig = new Socket[proxies.length];
    ByteArrayOutputStream buf = new ByteArrayOutputStream(10*proxies.length);

    // build up stonking great ladder of UDP associations...
    // AAAaaaauuugghh!!
    for(int i=0; i<proxies.length; ++i)
    {
      // make TCP chain
      if(0==i)
      {
        if(proxies[0] instanceof InetAddress) tig[0] =
          new Socket((InetAddress)proxies[0], Socks5Socket.SOCKSport);
        else tig[0] =
          new Socket(proxies[0].toString(), Socks5Socket.SOCKSport);
      }
      else
      {
        Socks5Socket x;
        if(proxies[0] instanceof InetAddress) tig[i] = x =
          new Socks5Socket((InetAddress)proxies[0]);
        else tig[i] = x =
          new Socks5Socket(proxies[0].toString());
        if(proxies[i] instanceof InetAddress)
        {
          x.setDestination(proxies, i-1,
            new HostPort((InetAddress)proxies[i], Socks5Socket.SOCKSport));
        }
        else
        {
          x.setDestination(proxies, i-1,
            new HostPort(proxies[i].toString(), Socks5Socket.SOCKSport));
        }
      }

      // send UDP associate message along each TCP chain
      // This follows the "emit strictly, receive generously" style
      // favoured for the 'net, but will afll foul of implementations
      // that don't handle all-zero requests properly
      byte[] req = Packet.makeRequest(Packet.UDPASSOC,
        ((0==i)?getLocalAddress():proxies[i-1]),
        ((0==i)?getLocalPort():0));

      tig[i].getOutputStream().write(req);
      tig[i].getOutputStream().flush();

      // read the response
      byte[] chunk = Packet.readCommandAsServer(tig[i].getInputStream());

      // assuming we're still here, we're in business to build the header
      if(0==i)
      {
        firstProxy = Packet.getAddress(chunk);
        firstPort = Packet.getPort(chunk);
      }
      else // what we need to take from i-1 to i
      {
        chunk[0] = chunk[1] = chunk[2] = 0;
        buf.write(chunk);
        buf.flush();
      }
    }// for
    if(proxies.length > 1) header = buf.toByteArray();
  }

  /**
  * Closes this datagram socket.
  */
  public void close()
  {
    for(int i=0; i<tig.length; ++i)
    {
      if(tig[i] != null)
      {try{tig[i].close();} catch(IOException ex){}}
    }
	  super.close();
  }

}