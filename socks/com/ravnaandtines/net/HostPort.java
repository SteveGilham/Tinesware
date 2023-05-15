package com.ravnaandtines.net;
import java.net.*;

/**
*  Class HostPort - a port number associated with a hostname
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

public class HostPort
{
  private Object addr;
  private int port;

  public HostPort(InetAddress addr, int port)
  {
    this.addr = addr;
    this.port = port&0xFFFF;
  }
  public InetAddress getInetAddress() throws UnknownHostException
  {
    return (addr instanceof InetAddress) ? (InetAddress) addr :
      InetAddress.getByName((String)addr);
  }

  public int getPort() {return port;}

  public HostPort(String addr, int port)
  {
    this.addr = addr;
    this.port = port&0xFFFF;
  }
  public Object getAddress() {return addr;}
} 