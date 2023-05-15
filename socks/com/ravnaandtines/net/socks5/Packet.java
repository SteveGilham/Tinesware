package com.ravnaandtines.net.socks5;
import java.io.*;
import java.net.*;

/**
*  Class Packet - SOCKS5 packet management
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

public class Packet
{
    public static final byte NO_AUTH = 0x00;
    public static final byte GSSAPI = 0x01;
    public static final byte UNPW   = 0x02;
    public static final byte BAD_AUTH = (byte)0xFF;

    public static final byte VERSION = 0x05;

    public static final byte CONNECT = 0x01;
    public static final byte BIND = 0x02;
    public static final byte UDPASSOC = 0x03;

    public static final byte RSV = 0x00;

    public static final byte IPV4 = 0x01;
    public static final byte DOMAINNAME = 0x03;
    public static final byte IPV6 = 0x04;

    public static final byte SUCCESS = 0x00;
    public static final byte FAIL = 0x01;
    public static final byte NOTALLOWED = 0x02;
    public static final byte NETUNREACH = 0x03;
    public static final byte HOSTUNREACH = 0x04;
    public static final byte REFUSED = 0x05;
    public static final byte TTLEXPIRED = 0x06;
    public static final byte COMMANDNOTSUPPORTED = 0x07;
    public static final byte ADDRESSNOTSUPPORTED = 0x08;
    public static final int  BADFLAG = 0x100;


    static byte[] makeClientHandshake(byte[] methods)
    {
        byte[] res = new byte[methods.length+2];
        res[0] = VERSION;
        res[1] = (byte) methods.length;
        for(int i=0; i<methods.length; ++i) res[2+i] = methods[i];
        return res;
    }
    static byte[] makeClientHandshake(byte method)
    {
        byte[] meth = new byte[1];
        meth[0] = method;
        return makeClientHandshake(meth);
    }
    static boolean isMethodInHandshake(byte[] packet, byte method)
    {
        if(VERSION != packet[0]) return false;
        for(int i=0; i<packet[1]; ++i)
        {
            if(packet[2+i] == method) return true;
        }
        return false;
    }

    static byte[] readCommandAsServer(InputStream in) throws IOException
    {
      int ver = in.read();
      if(ver != Packet.VERSION) return null;
      int cmd = in.read();
      if(cmd < 0) return null;
      int rsv = in.read();
      if(rsv != 0) return null;
      int atyp = in.read();
      if(atyp < 0) return null;
      int addrlen = 0;
      int index = 0;

      byte[] packet = null;
      switch(atyp)
      {
        case IPV4:
          packet = new byte[6+4];
          addrlen = (byte)4;
          index = 4; break;
        case DOMAINNAME:
          addrlen = in.read();
          if(addrlen<0) return null;
          packet = new byte[6+addrlen+1];
          packet[4] = (byte)addrlen;
          index = 5;
          break;
        case IPV6:
          packet = new byte[6+16];
          addrlen = (byte)16;
          index = 4; break;
        default:
          return null;
      }

      packet[0] = (byte) ver;
      packet[1] = (byte) cmd;
      packet[2] = (byte) rsv;
      packet[3] = (byte) atyp;
      for(int ia=0; ia<addrlen; ++ia)
      {
        int b = in.read();
        if(b < 0) return null;
        packet[index+ia] = (byte)b;
      }
      for(int ip=0; ip<2;++ip)
      {
        int p = in.read();
        if(p < 0) return null;
        packet[index+addrlen+ip] = (byte)p;
      }
      return packet;
    }

    static byte[] readHandshakeAsServer(InputStream in) throws IOException
    {
      int b1 = in.read();
      if(b1 != Packet.VERSION) return null;
      int b2 = in.read();
      if(b2 < 0) return null;

      byte[] shake = new byte[b2+2];
      shake[0] = (byte)b1;
      shake[1] = (byte)b2;
      for(int meth = 0; meth<b2; ++meth)
      {
        int nmeth = in.read();
        if(meth < 0) return null;
        shake[meth+2] = (byte)meth;
      }
      return shake;
    }

    static byte readHandshakeAsClient(InputStream in) throws IOException
    {
      int b1 = in.read();
      if(b1 < 0) throw new Socks5Exception("No response to handshake");
      if(b1 != Packet.VERSION) throw new Socks5Exception("Handshake gave bad version "+b1);
      int b2 = in.read();
      if(b2 < 0) throw new Socks5Exception("No method in response to handshake");
      return (byte)b2;
    }

    static byte[] makeServerHandshake(byte method)
    {
        byte[] res = new byte[2];
        res[0] = VERSION;
        res[1] = method;
        return res;
    }
    static byte getServerMethod(byte[] packet)
    {
        if(VERSION != packet[0] || packet.length!=2) return BAD_AUTH;
        return packet[1];
    }

    static byte[] makeRequest(byte command, InetAddress dst, int port)
    {
        // assumes no encapsulation
        byte[] res = new byte[10];
        res[0] = VERSION;
        res[1] = command;
        res[2] = RSV;
        res[3] = IPV4;
        res[4] = dst.getAddress()[0];
        res[5] = dst.getAddress()[1];
        res[6] = dst.getAddress()[2];
        res[7] = dst.getAddress()[3];
        res[8] = (byte)((port>>>8)&0xFF);
        res[9] = (byte)(port&0xFF);
        return res;
    }
    static byte[] makeRequest(byte command, Object dst, int port)
      throws UnsupportedEncodingException
    {
        // assumes no encapsulation
        if(dst instanceof InetAddress)
          return makeRequest(command, (InetAddress) dst, port);
        byte[] name = dst.toString().getBytes("ASCII");
        byte[] res = new byte[name.length+7];
        res[0] = VERSION;
        res[1] = command;
        res[2] = RSV;
        res[3] = DOMAINNAME;
        res[4] = (byte)(name.length & 0xFF);
        System.arraycopy(name, 0, res, 5, name.length);
        res[res.length-2] = (byte)((port>>>8)&0xFF);
        res[res.length-1] = (byte)(port&0xFF);
        return res;
    }


    static byte[] makeResponse(byte status, InetAddress dst, int port)
    {
        // assumes no encapsulation
        byte[] res = new byte[10];
        res[0] = VERSION;
        res[1] = status;
        res[2] = RSV;
        res[3] = IPV4;
        res[4] = dst.getAddress()[0];
        res[5] = dst.getAddress()[1];
        res[6] = dst.getAddress()[2];
        res[7] = dst.getAddress()[3];
        res[8] = (byte)((port>>>8)&0xFF);
        res[9] = (byte)(port&0xFF);
        return res;
    }

    static int getCommand(byte[] packet)
    {
        if(packet[0] != VERSION) {packet[0] = VERSION; return FAIL|BADFLAG;}
        if(packet[3] != IPV4 && packet[3] != DOMAINNAME)
          return ADDRESSNOTSUPPORTED|BADFLAG;
        return packet[1];
    }

    static int getPort(byte[] packet)
    {
      return getPort(packet, 0);
    }

    static int getPort(byte[] packet, int offset)
    {
        if(packet[3+offset] == IPV4)
          return ((packet[8+offset]&0xFF)<<8) | (packet[9+offset]&0xFF);
        else if(packet[3+offset] == DOMAINNAME)
        {
            int len = 1 + packet[4+offset]&0xFF;
            return ((packet[len+4+offset]&0xFF)<<8) | (packet[len+5+offset]&0xFF);
        }
        else return -1;
    }

    static InetAddress getAddress(byte[] packet)  throws UnknownHostException, IOException
    {
      return getAddress(packet, 0);
    }

    static InetAddress getAddress(byte[] packet, int offset) throws UnknownHostException, IOException
    {
      switch(packet[3+offset])
      {
        case IPV4:
        {
          String dottedQuad = ""+(packet[4+offset]&0xFF)
                    +"."+(packet[5+offset]&0xFF)
                    +"."+(packet[6+offset]&0xFF)
                    +"."+(packet[7+offset]&0xFF);
//System.out.println("got address "+dottedQuad);
          return InetAddress.getByName(dottedQuad);
        }
        case DOMAINNAME:
        {
          int addrlen = packet[4+offset]&0xFF;
          String dnsName = new String(packet, 5+offset, addrlen, "ASCII");
//System.out.println("got address "+dnsName);
          return InetAddress.getByName(dnsName);
        }
        default:
          return null;
      }
    }
    static int getPacketLength(byte[] packet)
    {
      return getPacketLength(packet, 0);
    }

    static int getPacketLength(byte[] packet, int offset)
    {
      switch(packet[offset+3])
      {
        case IPV4:
          return (6+4);
        case DOMAINNAME:
          return (6+(packet[4]&0xFF)+1);
        case IPV6:
          return (6+16);
        default:
          return -1;
      }
    }

}
