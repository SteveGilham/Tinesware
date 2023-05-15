package com.ravnaandtines.net.socks5;
import com.ravnaandtines.net.*;
import java.net.*;
import java.io.*;

/**
*  Class Socks5Socket - a socket that does a non-authenticating SOCKS5 CONNECT
*                       using its setDestination method.
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
public class Socks5Socket extends Socket
{
  public static final int SOCKSport = 1080;
  private HostPort endpoint = null;
  //private InetAddress[] chain = null;
  private Object[] proxies = null;
    /**
     * Creates a stream socket and connects it to the SOCKS port
     * on the named host.
     * <p>
     * If the application has specified a server socket factory, that 
     * factory's <code>createSocketImpl</code> method is called to create 
     * the actual socket implementation. Otherwise a "plain" socket is created.
     *
     * @param      host   the SOCKS proxy name.
     * @exception  IOException  if an I/O error occurs when creating the socket.
     */
   public Socks5Socket(String host) throws UnknownHostException, IOException
   {
      super(host, SOCKSport);
      doHandshake();
   }

    /** 
     * Creates a stream socket and connects it to the SOCKS port
     * at the specified IP address.
     * <p>
     * If the application has specified a socket factory, that factory's 
     * <code>createSocketImpl</code> method is called to create the 
     * actual socket implementation. Otherwise a "plain" socket is created.
     *
     * @param      address   the IP address.
     * @exception  IOException  if an I/O error occurs when creating the socket.
     */
    public Socks5Socket(InetAddress address) throws IOException
   {
      super(address, SOCKSport);
      doHandshake();
   }

    /** 
     * Creates a socket and connects it to the specified remote host on
     * the SOCKS port. The Socket will also bind() to the local
     * address and port supplied.
     * @param host the name of the remote host
     * @param localAddr the local address the socket is bound to
     * @param localPort the local port the socket is bound to
     */
    public Socks5Socket(String host,
               InetAddress localAddr,
               int localPort) throws IOException
   {
      super(host, SOCKSport, localAddr, localPort);
      doHandshake();
   }

    /** 
     * Creates a socket and connects it to the specified remote address on
     * the SOCKS port. The Socket will also bind() to the local
     * address and port supplied.
     * @param address the remote address
     * @param port the remote port
     * @param localAddr the local address the socket is bound to
     * @param localPort the local port the socket is bound to
     */
    public Socks5Socket(InetAddress address,
               InetAddress localAddr,
               int localPort) throws IOException
   {
      super(address, SOCKSport, localAddr, localPort);
      doHandshake();
   }

   private void doHandshake() throws IOException
   {
      byte[] auth = {0}; // not authenticated
      byte[] shake = Packet.makeClientHandshake(auth);
      super.getOutputStream().write(shake);
      byte method = Packet.readHandshakeAsClient(super.getInputStream());
      if(method != 0) throw new Socks5Exception("No mutually agreed authentication");
   }

   /**
     * Returns an input stream for this socket.
     *
     * @return     an input stream for reading bytes from this socket.
     * @exception  IOException  if an I/O error occurs when creating the
     *               input stream.
     */
    public InputStream getInputStream() throws IOException
    {
      if(null == endpoint)
        throw new Socks5Exception("No final destination set");
	    return super.getInputStream();
    }

    /**
     * Returns an output stream for this socket.
     *
     * @return     an output stream for writing bytes to this socket.
     * @exception  IOException  if an I/O error occurs when creating the
     *               output stream.
     */
    public OutputStream getOutputStream() throws IOException
    {
      if(null == endpoint)
        throw new Socks5Exception("No final destination set");
	    return super.getOutputStream();
    }

    public void setDestination(HostPort to) throws IOException
    {
      if(null != endpoint) throw new Socks5Exception("Final destination already set");
      setDestination(null, to);
    }

    public void setDestination(Object[] k, int l, HostPort to) throws IOException
    {
      setDestination(k, 0, l, to);
    }

    public void setDestination(Object[] k, int offset,
      int l, HostPort to) throws IOException
    {
      if(null != endpoint) throw new Socks5Exception("Final destination already set");
      endpoint = to;
      proxies = k;

      try {
        for(int i = offset; k!=null && i<k.length && i<l+offset; ++i)
        {
          // do a connect method to this
          if(k[i] instanceof InetAddress)
            doConnect(new HostPort((InetAddress)k[i], SOCKSport));
          else
            doConnect(new HostPort(k[i].toString(), SOCKSport));
          doHandshake();
        }
        doConnect(to);
      } catch (IOException any) {
        close();
        throw any;
      }
    }
    public void setDestination(Object[] k, HostPort to) throws IOException
    {
      int l = (null == k)?0:k.length;
      setDestination(k, l, to);
    }

    public HostPort getDestination()
    {
       return endpoint;
    }

    public Object[] getIntermediates()
    {
      return proxies;
    }

    void doConnect(HostPort dest) throws IOException
    {
      super.getOutputStream().write(
        Packet.makeRequest(Packet.CONNECT, dest.getAddress(), dest.getPort())
      );
      byte [] stuff = Packet.readCommandAsServer(super.getInputStream());
      if(Packet.getCommand(stuff) != Packet.SUCCESS)
        throw new Socks5Exception("Proxy request for "+dest.getAddress()
          +":"+dest.getPort()+" denied : reason code "+Packet.getCommand(stuff));
    }

    byte [] doAssociate(HostPort dest) throws IOException
    {
      super.getOutputStream().write(
        Packet.makeRequest(Packet.UDPASSOC, dest.getAddress(), dest.getPort())
      );
      byte [] stuff = Packet.readCommandAsServer(super.getInputStream());
      if(Packet.getCommand(stuff) != Packet.SUCCESS)
        throw new Socks5Exception("Proxy request for "+dest.getAddress()
          +":"+dest.getPort()+" denied : reason code "+Packet.getCommand(stuff));
      return stuff;
    }
}
