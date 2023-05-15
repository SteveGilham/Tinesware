
//Title:        Janus
//Version:      
//Copyright:    Copyright (c) 2000
//Author:       Mr. Tines
//Company:      Ravna&Tines
//Description:  A multi-hop SOCKS relay service


package com.ravnaandtines.janus;
/**
*  Class Proxy
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
* @version 1.1 22-Dec-2002
*
*/
import java.net.*;
import java.io.*;
import javax.swing.*;
import java.util.*;
import com.ravnaandtines.util.io.Pump;

public class Proxy extends Thread
{
  static final int port = 1080;
  static final int squidport = 3128;
  private static final ResourceBundle res = com.ravnaandtines.Janus.res;
  static final byte[] handshake = {5,1,0};
  static final byte[] connect = {5,1,0};
  static final byte[] socks = {4,56};
  protected Socket socket = null;
  protected Socket relay = null;
  String [] proxies;
  String endserver;
  int endport;
  InputStream input = null;
  InputStream in = null;
  OutputStream out = null;
  OutputStream output = null;
  boolean squid;
  static final byte[] tail = {0x0D, 0x0A, 0x0D, 0x0A};

  String [] errors = {res.getString("succeeded"),
    res.getString("General_SOCKS_server"),
    res.getString("Connection_not"),
    res.getString("Network_unreachable"),
    res.getString("Host_unreachable"),
    res.getString("Connection_refused"),
    res.getString("TTL_expired"),
    res.getString("Command_not_supported"),
    res.getString("Address_type_not"),
    res.getString("unassigned_error_code")};

  String replyCode(int b)
  {
    int x = b&0xFF;
    if(x >= errors.length) x = errors.length-1;
    return errors[x];
  }


  public Proxy(com.ravnaandtines.janus.ServerSocket server, Socket client,
    String end, int endport, boolean squid)
  {
     proxies = server.proxylist;
     socket = client;
     endserver = end;
     this.endport = endport;
     this.squid = squid;
  }

  private void writeConnect(OutputStream output, String servername) throws IOException
  {
    output.write(connect);
    int dot = servername.lastIndexOf('.');
    if(dot < 0 || dot+1 >= servername.length() ||
      servername.charAt(dot+1) < '0' || servername.charAt(dot+1) > '9')
    {
      // not a dotted quad so assume DNS name
        byte[] proxyname = servername.getBytes("ASCII");
        output.write((byte)3); // DNS name
        output.write(proxyname.length);
        output.write(proxyname);
    }
    else
    {
        output.write((byte)1); // IPv4 name
        InetAddress i = InetAddress.getByName(servername);
        output.write(i.getAddress());
    } // can't handle IPv6 from Java yet...
  }


  public void run()
  {
//System.out.println("run: squid="+squid);
     if(squid)
     {
//System.out.println("runsquid call next");
       runSquid();
       return;
     }

     try {
     // assume 1080 and SOCKS5
//System.out.println("Socks proxy value = "+proxies[0]);
    int ix = proxies[0].indexOf(":");
    String host = proxies[0];
    int pport = port;
    if(ix > 0 )
    {
      host = proxies[0].substring(0, ix);
      try {
        String p = proxies[0].substring(ix+1);
        pport = Integer.parseInt(p);
      } catch (Exception exj4) {pport = port;}
    }
    InetAddress proxy = InetAddress.getByName(host);
//System.out.println(proxy);
     relay = new Socket(proxy, pport);
//System.out.println(relay);
     input = relay.getInputStream();
     output = relay.getOutputStream();
     output.write(handshake);

     int b1 = input.read();
     if(b1 != 5)
     {
        Object[] args1 = {proxies[0]};
        relay.close();
        signal(java.text.MessageFormat.format(res.getString("Not_a_SOCKS5_server_"),args1));
        return;
     }
     b1 = input.read();
     if(0xff == b1 || b1 < 0)
     {
        Object[] args2 = {proxies[0]};
        relay.close();
        signal(java.text.MessageFormat.format(res.getString("requires"),args2));
        return;
     }
     Object[] args3 = {proxies[0]};
     MainFrame.status.setText(java.text.MessageFormat.format(res.getString("SOCKS_Handshake_OK"),args3));

     int i=1;
     for(; i<proxies.length;++i)
     {
        Object[] args4 = {proxies[i-1]};
        host = proxies[i];
        byte[] portBytes = socks;
        if(ix > 0 )
        {
          host = proxies[i].substring(0, ix);
          try {
            String p = proxies[i].substring(ix+1);
            pport = Integer.parseInt(p);
          } catch (Exception exj4) {pport = port;}
          portBytes = new byte[2];
          portBytes[0] = (byte)((pport>>8)&0xFF);
          portBytes[1] = (byte)(pport & 0xFF);
        }
        writeConnect(output, proxies[i]);
        output.write(socks);

        b1 = input.read();
        if(b1 != 5) {relay.close();
          signal(java.text.MessageFormat.format(res.getString("connect_fail_"),args4)); return;}
        b1 = input.read();
        if(b1 != 0) {relay.close();
        Object[] args5 = {proxies[i-1], replyCode(b1)};
          signal(java.text.MessageFormat.format(res.getString("connect_fail_2"), args5)); return;}
        b1 = input.read();
        if(b1 != 0) {relay.close();
          signal(java.text.MessageFormat.format(res.getString("connect_fail_"),args4)); return;}
        b1 = input.read();
        switch(b1)
        {
          case 1: for(int sinkv4=0; sinkv4<6;++sinkv4)
          {b1 = input.read(); if(b1 < 0)
            {relay.close();
              signal(java.text.MessageFormat.format(res.getString("connect_fail_"),args4)); return;}} break;

          case 3: {int limit = input.read(); if (limit <0) {relay.close(); return;}
          for(int sinkdns=0; sinkdns<limit+2;++sinkdns)
            {b1 = input.read(); if(b1 < 0)
              {relay.close();
                signal(java.text.MessageFormat.format(res.getString("connect_fail_"),args4)); return;}}} break;

          case 4: for(int sinkv6=0; sinkv6<18;++sinkv6)
          {b1 = input.read(); if(b1 < 0)
            {relay.close();
              signal(java.text.MessageFormat.format(res.getString("connect_fail_"),args4)); return;}} break;

          default: {relay.close();
            signal(java.text.MessageFormat.format(res.getString("connect_fail_"),args4)); return;}
		    }
          Object[] args6 = {proxies[i-1],proxies[i]};
          MainFrame.status.setText(java.text.MessageFormat.format(res.getString("connected_to"),args6));
          output.write(handshake);

          b1 = input.read();
          if(b1 != 5)
          {
            Object[] args7 = {proxies[i]};
            relay.close();
            signal(java.text.MessageFormat.format(res.getString("Not_a_SOCKS5_server_"),args7));
            return;
          }
          b1 = input.read();
          if(0xff == b1 || b1 < 0)
        {
          Object[] args8 = {proxies[i]};
          relay.close();
          signal(java.text.MessageFormat.format(res.getString("requires"),args8));
          return;
        }
        Object[] args9 = {proxies[i]};
        MainFrame.status.setText(java.text.MessageFormat.format(res.getString("SOCKS_Handshake_OK"),args9));
      } // loop
      writeConnect(output, endserver);
      output.write((endport>>>8)&0xFF);
      output.write(endport&0xFF);
      Object[] args10 = {proxies[i-1]};

      b1 = input.read();
      if(b1 != 5) {relay.close();
          signal(java.text.MessageFormat.format(res.getString("connect_fail_"),args10)); return;}
      b1 = input.read();
      if(b1 != 0) {relay.close();
          Object[] args11 = {proxies[i-1], replyCode(b1)};
          signal(java.text.MessageFormat.format(res.getString("connect_fail_2"), args11)); return;}
      b1 = input.read();
      if(b1 != 0) {relay.close();
          signal(java.text.MessageFormat.format(res.getString("connect_fail_"),args10)); return;}
      b1 = input.read();
      switch(b1)
      {
        case 1: for(int sinkv4=0; sinkv4<6;++sinkv4)
        {b1 = input.read(); if(b1 < 0)
          {relay.close();
            signal(java.text.MessageFormat.format(res.getString("connect_fail_"),args10)); return;}} break;

        case 3: {int limit = input.read(); if (limit <0) {relay.close(); return;}
        for(int sinkdns=0; sinkdns<limit+2;++sinkdns)
          {b1 = input.read(); if(b1 < 0)
            {relay.close();
              signal(java.text.MessageFormat.format(res.getString("connect_fail_"),args10)); return;}}} break;

        case 4: for(int sinkv6=0; sinkv6<18;++sinkv6)
          {b1 = input.read(); if(b1 < 0)
            {relay.close();
              signal(java.text.MessageFormat.format(res.getString("connect_fail_"),args10)); return;}} break;

        default: {relay.close();
          signal(java.text.MessageFormat.format(res.getString("connect_fail_"),args10)); return;}
		    }
        Object[] args12 = {proxies[i-1],endserver};
        MainFrame.status.setText(java.text.MessageFormat.format(res.getString("connected_to"),args12));
        out = socket.getOutputStream ();
        in = socket.getInputStream();

        Pump a = new Pump(in, output);
        Pump b = new Pump(input, out);
        a.start();
        b.start();
        for(;;)
        {
          try {Thread.sleep(500);} catch (InterruptedException iex) {}
          if(!a.isAlive())
          {
            System.out.println(res.getString("client_closed_connection"));
            break;
          }
          if(!b.isAlive())
          {
            System.out.println(res.getString("server_closed_connection"));
            break;
          }
        }
     }
     catch(java.io.IOException ex)
     {
        Object [] args14 = {endserver+":"+endport};
        signal(
              java.text.MessageFormat.format(res.getString("IO_Failure_relaying"), args14));
     }
     finally {
        try {
          if(null != relay) relay.close();
          if(null != socket) socket.close();
        } catch (java.io.IOException exex) {}
     }
  }

  public void runSquid()
  {
     try {
     // assume 3128 and HTTP CONNECT
//System.out.println("proxy value = "+proxies[0]);
    int ix = proxies[0].indexOf(":");
    String host = proxies[0];
    int pport = squidport;
    if(ix > 0 )
    {
      host = proxies[0].substring(0, ix);
      try {
        String p = proxies[0].substring(ix+1);
        pport = Integer.parseInt(p);
      } catch (Exception exj4) {pport = squidport;}
    }
    InetAddress proxy = InetAddress.getByName(host);
//System.out.println(proxy);
     relay = new Socket(proxy, pport);
//System.out.println(relay);
     input = relay.getInputStream();
     output = relay.getOutputStream();
//System.out.println("run squid");
     int i;
     for(i=0; i<proxies.length-1; ++i)
     {
        ix = proxies[i+1].indexOf(":");
        String stub = (ix < 0) ? ":3128" : "";

         String connectStr = "CONNECT "+proxies[i+1]+stub+" HTTP/1.0";
//System.out.println("connect via "+connectStr);
         byte[] connect = connectStr.getBytes("ASCII");
         output.write(connect);
         output.write(tail);

         ByteArrayOutputStream echo = new ByteArrayOutputStream();
         int oa = 0;
         int b1;
         for(b1=input.read(); b1 > 0 && oa < 2;b1=input.read())
         {
            echo.write(b1);
            if(0x0A==b1) ++oa;
            if(2 == oa) break;
         }
         if(b1 < 0 || oa < 2)
         {
          relay.close();
          signal(res.getString("failed")+proxies[i+1]);
          return;
         }
         byte[] got = echo.toByteArray();
         String result = new String(got, 0, got.length-4, "ASCII");
         ix = result.indexOf(' ');
         if(result.charAt(ix+1) != '2')
         {
          relay.close();
          signal(res.getString("failed")+proxies[i+1]+" "+result);
          return;
         }
         MainFrame.status.setText(proxies[i+1]+" "+result);
     }

     String econnectStr = "CONNECT "+endserver+
      ":"+endport+" HTTP/1.0";
//System.out.println("connect to "+econnectStr);
     byte[] econnect = econnectStr.getBytes("ASCII");
     output.write(econnect);
     output.write(tail);

     ByteArrayOutputStream eecho = new ByteArrayOutputStream();
     int eoa = 0;
     int eb1;
//System.out.println("getting header");
     for(eb1=input.read(); eb1 > 0 ;eb1=input.read())
     {
//System.out.print(eb1);
      eecho.write(eb1);
      if(0x0A==eb1) ++eoa;
      if(2 == eoa) break;
     }
//System.out.println("");
//System.out.println("got header - final eb1 = "+eb1+" eoa="+eoa);
     byte[] egot = eecho.toByteArray();
     if(eb1 < 0 || eoa < 2)
     {
//System.out.println(new String(egot, 0, egot.length, "ASCII"));
      relay.close();
      signal(res.getString("failed")+endserver);
      return;
     }
//System.out.println("egot length = "+egot.length);
     String eresult = new String(egot, 0, egot.length-4, "ASCII");
     int eix = eresult.indexOf(' ');
     if(eresult.charAt(eix+1) != '2')
     {
          relay.close();
          signal(res.getString("failed")+endserver+" "+eresult);
          return;
     }
     MainFrame.status.setText(endserver+" "+eresult);
     out = socket.getOutputStream ();
     in = socket.getInputStream();

     Pump a = new Pump(in, output);
     Pump b = new Pump(input, out);
     a.start();
     b.start();
     for(;;)
     {
       try {Thread.sleep(500);} catch (InterruptedException iex) {}
       if(!a.isAlive())
       {
         System.out.println(res.getString("client_closed_connection"));
         break;
       }
       if(!b.isAlive())
       {
         System.out.println(res.getString("server_closed_connection"));
         break;
       }
     }
   } catch(java.io.IOException ex) {
     Object [] args14 = {endserver+":"+endport};
     signal(java.text.MessageFormat.format(res.getString("IO_Failure_relaying"), args14));
   } finally {
     try {
       if(null != relay) relay.close();
       if(null != socket) socket.close();
     } catch (java.io.IOException exex) {}
   }
  }

  private void signal(String x)
  {
    JOptionPane.showMessageDialog(null,
    x,
    "Janus", JOptionPane.ERROR_MESSAGE);
  }
}