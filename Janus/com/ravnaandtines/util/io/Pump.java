package com.ravnaandtines.util.io;
import java.io.*;

/**
*  Class Pump - reads from one input stream and passes it straight to an
*               output stream, stopping on end of file or exception
* <p>
* Copyright Mr. Tines &lt;tines@windsong.demon.co.uk&gt; 2000
* <p>
* For the purposes of the licence, this source file and the minimum set of
* other source files in package com.ravnaandtines.util and its sub-
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
* @author Shelby Cain <alyandon@yahoo.com>
* @version 1.1 10-Dec-2002
*
*/

public class Pump extends Thread
{
  // Use a buffer to transfer whole packets, rather than
  // spin, one byte at a time -- SC, Dec '02
  private static final int DEFAULT_BUF_SIZE = 32768;
  private InputStream in = null;
  private OutputStream out = null;
  private boolean running = true;
  private boolean verbose = false;
  private byte m_Buffer[] = null;
  

  /**
  * Connect the two streams
  * @param in source of bytes
  * @param out sink for bytes
  */
  public Pump(InputStream in, OutputStream out)
  {
    this.in = in;
    this.out=out;
  }

  public Pump(InputStream in, OutputStream out, boolean verbose)
  {
    this(in,out);
    this.verbose = verbose;
  }

  /**
  * Pump bytes between the streams
  */
  public void run()
  {
     m_Buffer = new byte[DEFAULT_BUF_SIZE];
     int dlen = 0;
     try {
       do {
         dlen = in.read(m_Buffer, 0, DEFAULT_BUF_SIZE);
         if (dlen>0)
         {
             out.write(m_Buffer, 0, dlen);
             out.flush();
             if(verbose) System.out.print("Bytes pumped: " + dlen);
             yield();
         }
       } while (running && dlen>0);
     } catch (java.io.IOException ex){
        ex.printStackTrace(System.err);
    }
    if(verbose) System.out.println("pump terminated");
  }

  public void halt()
  {
    running = false;
  }
}
