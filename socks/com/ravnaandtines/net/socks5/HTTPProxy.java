package com.ravnaandtines.net.socks5;
import java.io.*;
import java.net.*;
import java.util.*;

/**
*  Class HTTPProxy - sends what was received as an HTTP POST as a UDP packet
*                    to tunnel the UDP - SERVER SIDE
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

public class HTTPProxy  extends Thread {

  protected Socket socket;
  protected static int port = 80;
  protected boolean associated;
  private InputStream in = null;
  protected static byte[] crlf = null;
  protected static byte[] tail = null;
  protected static boolean verbose = false;
  protected DatagramSocket udp = null;
  public static void setVerbose(boolean b) {verbose = b;}

  public HTTPProxy (Socket socket) {
if(verbose) System.out.println("got connection");
    this.socket = socket;
    associated = true;
  }

  public static void main (String[] args) throws IOException {
    if(args.length >0) {
      try {
        port = Integer.parseInt(args[0]);
      } catch (NumberFormatException nfex) {
      System.out.println("\""+args[0]+"\" is not a port number");
      return;
      }
    }
    port &= 0xFFFF;
    verbose = args.length > 1;
if(verbose) System.out.println("debug mode");
    ServerSocket server = new ServerSocket (port);
    while (true) {
      Socket client = server.accept ();
      HTTPProxy echo = new HTTPProxy (client);
      echo.start ();
    }
  }

  private String prezero(int i)
  {
    return (i<10)? "0"+i : ""+i;
  }
  private String zone(int i)
  {
    if(0==i) return "GMT";
    String x = (i>=0)? "+" : "-";
    i = Math.abs(i)/60000;
    x += prezero(i/60)+prezero(i%60);
    return x;
  }
  private static final String [] day =  {"Mon"  , "Tue" ,  "Wed"  , "Thu"
                 ,  "Fri"  , "Sat" ,  "Sun"};

  private static final String [] month =  {"Jan"  ,  "Feb" ,  "Mar"  ,  "Apr"
                 ,  "May"  ,  "Jun" ,  "Jul"  ,  "Aug"
                 ,  "Sep"  ,  "Oct" ,  "Nov"  ,  "Dec"};
// Calendar is bad enough, but DateFormat is effectively undocumented
  private String RFC1123() 
  {                                                                    
    Date d = new Date();
    Calendar cal = Calendar.getInstance(Locale.US); // natch
    cal.setTime(d);
    String date =
      day[(cal.get(Calendar.DAY_OF_WEEK)+5)%7]+", "
      + cal.get(Calendar.DAY_OF_MONTH)+" "
      + month[(cal.get(Calendar.MONTH)+0)%12]+" "
      + cal.get(Calendar.YEAR)+" "
      + prezero(cal.get(Calendar.HOUR_OF_DAY))+":"
      + prezero(cal.get(Calendar.MINUTE))+":"
      + prezero(cal.get(Calendar.SECOND))+" "
      + zone(cal.get(Calendar.ZONE_OFFSET));
    return date;
  }

  private void emitHeader(String type, int loaded) throws IOException
  {
    // munge Date.toString() into RFC 1123 form
    // TODO - do this properly
	  String s = (new java.util.Date()).toString();
	  int sp1 = s.indexOf(' ');
	  int sp2 = s.indexOf(' ', sp1+1);
	  int sp3 = s.indexOf(' ', sp2+1);

    String header =
         "HTTP/1.1 "+type+"\r\n"
        +"Server: com.ravnaandtines.net.socks5.HTTPProxy (Pollux)\r\n"
        +"Date: "+RFC1123();
        if(loaded < 0) header +=
            "\r\nContent-Type: application/octet-stream\r\nTransfer-coding: chunked";
        if(loaded > 0) header +=
            "\r\nContent-Type: text/html\r\nContent-length: "+loaded;
        header +="\r\n\r\n";
if(verbose) System.out.println(header);
        socket.getOutputStream().write(header.getBytes("ASCII"));
  }
  private static final String BadRequest =
  "<HTML><HEAD><TITLE>400 Bad Request</TITLE>"+
  "</HEAD><BODY><H1>400 Bad Request</H1>This isn't a generic web server,"+
  " it's the HTTP to UDP converter component of a two-part SOCKS proxy"+
  " which tunnels the UDP as HTTP.<P>For more information see "+
  "<A HREF=\"http://www.ravnaandtines.com/\">www.ravnaandtines.com</A></P></BODY></HTML>";

  private void NACK() throws IOException
  {
   byte[] emit = BadRequest.getBytes("ASCII");
   emitHeader("400 Bad Request", emit.length);
   socket.getOutputStream().write(emit);}
  private void ACK() throws IOException
  {emitHeader("100 Continue", 0);}

  public void run() {
    try {
        if(null == crlf)
        {
            String s = "\r\n";
            crlf = s.getBytes("ASCII");
            s = "0\r\n\r\n";
            tail = s.getBytes("ASCII");
        }// null == crlf
        in = socket.getInputStream();
        DataInputStream din = new DataInputStream(in);
        // expect to get an ACK "POST data:,###.###.###.###/#### HTTP/1.1"
        // Currently not too forgiving of garbage
        String code = din.readLine();
if(verbose) System.out.println(code);
        if(!code.startsWith("POST data:,")) {NACK(); return;}
        if(!code.endsWith(" HTTP/1.1")) {NACK(); return;}

        // parse out the data
        int comma = code.indexOf(',');
        if(comma < 0) {NACK(); return;}
        int slash = code.indexOf('/', comma);
        if(slash < 0) {NACK(); return;}
        int space = code.indexOf(' ', slash);
        if(space < 0) {NACK(); return;}

        String hostname = code.substring(comma+1, slash);
        String portname = code.substring(slash+1, space);

        InetAddress target = InetAddress.getByName(hostname);
        if(null == target) {NACK(); return;}
        int targetport = -1;
        try {
          targetport = Integer.parseInt(portname);
        } catch (NumberFormatException nfex) {NACK(); return;}
        targetport &= 0xFFFF;

        int content = 0;

        // drain header
        do{
          code = din.readLine();
if(verbose) System.out.println(code);
          if(code.startsWith("Content-Length: "))
          {
            int eoh = code.indexOf(' ');
            String trail = code.substring(eoh+1).trim();
            try {
              content = Integer.parseInt(trail);
            } catch (NumberFormatException nfex) {NACK(); return;}
          }
        } while (code.length() > 0);

        if(content <= 0) {NACK(); return;}

        // read packet
        byte[] data = new byte[content];
        int got = 0;
        while(got < content)
        {
          int delta = in.read(data, got, content-got);
          got += delta;
        } // (got < content)
if(verbose)
{
  for(int v1=0; v1 < content; ++v1)
  {
    int v2 = data[v1]&0xFF;
    System.out.print(" ");
    if(v2 < 0x10) System.out.print("0");
    System.out.print(Integer.toHexString(v2));
  }
  System.out.println("");
}
        // ack and check if link is alive
        ACK();
        udp = new DatagramSocket();
        Thread monitor = new Thread( new Runnable() {
          public void run() {
          try { while(in.read()>0) {;}}
          catch (IOException ex) {
if(HTTPProxy.verbose)ex.printStackTrace(System.out);
          }
          finally {
if(HTTPProxy.verbose)System.out.println("Tunnelling Connection dropped");
          associated = false; udp.close();}
        }});
        monitor.start();

        DatagramPacket onbound = new DatagramPacket(data, data.length,
            target, targetport);
        udp.send(onbound);
if(verbose)System.out.println("relayed");

        byte[] get = new byte[0x10001];
        DatagramPacket packet = new DatagramPacket(get, get.length);
        OutputStream out = socket.getOutputStream();
        emitHeader("200 OK", -1);

        try {
          udp.setSoTimeout(0x7FFF);
        while (associated && monitor.isAlive()) {
          udp.receive(packet);
          int l = 0;
          String size = Integer.toHexString(l=packet.getLength())+"\r\n";
if(verbose) System.out.print(size);
          out.write(size.getBytes("ASCII"));
          out.write(packet.getData(), 0, l);
if(verbose)
{
  for(int v3=0; v3 < packet.getLength(); ++v3)
  {
    int v4 = packet.getData()[v3]&0xFF;
    System.out.print(" ");
    if(v4 < 0x10) System.out.print("0");
    System.out.print(Integer.toHexString(v4));
  }
  System.out.println("");
}
          out.write(crlf);
        }} catch (InterruptedIOException intex) {}
        out.write(tail);

      } catch (IOException intex) {}
      finally{ try {
if(verbose) System.out.println("closed socket");
        if(null != socket)socket.close();
        } catch (IOException bogus) {}
        if(null != udp)udp.close();
      }
    }
}


