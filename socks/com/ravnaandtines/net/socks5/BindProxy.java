package com.ravnaandtines.net.socks5;
import java.io.*;
import java.net.*;

/**
*  Class BindProxy - a non-authenticating SOCKS5 proxy that supports the BIND &
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

public class BindProxy extends ConnectProxy
{
  public static void main (String[] args) throws IOException {
    ServerSocket server = new ServerSocket (port);
    while (true) {
      Socket client = server.accept ();
      BindProxy relay = new BindProxy (client);
      relay.start ();
    }
  }

  // this should actually be an implementation!!
  protected void doBind(byte[] command) throws InterruptedException, IOException
  {
    if(Packet.BIND != command[1]) throw new InterruptedException();
    command[1] = Packet.COMMANDNOTSUPPORTED;
    out.write(command);
    out.flush();
    return;
  }


  public BindProxy(Socket socket) throws IOException
  {
    super(socket);
  }
} 