
//Title:        Janus
//Version:      
//Copyright:    Copyright (c) 2000
//Author:       Mr. Tines
//Company:      Ravna&Tines
//Description:  A multi-hop SOCKS relay service


package com.ravnaandtines.janus;
/**
*  Class ServerSocket
*  <P>
*  Coded & copyright Mr. Tines &lt;tines@windsong.demon.co.uk&gt; 2000
*  All rights reserved.
*  <P>
*  This application is free software; you can redistribute it and/or
*  modify it under the terms of the GNU General Public
*  License as published by the Free Software Foundation; either
*  version 2 of the License, or (at your option) any later version.
*  <P>
*  This application is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
*  General Public License for more details.
*  <P>
*  You should have received a copy of the GNU General Public
*  License along with this library (file "COPYING"); if not,
*  write to the Free Software Foundation, Inc.,
*  59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*  <P>
* @author Mr. Tines
* @version 1.0 10-Feb-2000
*
*/
import java.net.*;
import java.lang.Runnable;
import javax.swing.*;
import java.util.*;

public class ServerSocket extends Thread
{
  String target;
  String[] proxylist;
  java.net.ServerSocket server = null;
  int port;
  boolean squid;

  public ServerSocket(String host, int port, String[] proxies, boolean squid)
  throws java.io.IOException
  {
    target = host;
    server = new java.net.ServerSocket(port);
    proxylist = proxies;
    this.port = port;
    this.squid = squid;
  }

  public void run()
  {
    while (true) {
      try {
      Socket client = server.accept();
      Proxy echo = new Proxy (this, client, target, port, squid);
      echo.start ();
      } catch (java.io.IOException ex1)
      {
          Object[] args = { new Integer(port), target };
          JOptionPane.showMessageDialog(null,
          java.text.MessageFormat.format (
            com.ravnaandtines.Janus.res.getString("Failure_accepting_on"),
            args),
          "Janus", JOptionPane.ERROR_MESSAGE);
      }
    }
  }
}