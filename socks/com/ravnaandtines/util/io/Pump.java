package com.ravnaandtines.util.io;
import java.io.*;

/**
*  Class Pump - reads from one input stream and passes it straight to an
*               output stream, stopping on end of file or exception
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

public class Pump extends Thread
{
  private InputStream in = null;
  private OutputStream out = null;
  private boolean running = true;
  private boolean verbose = false;

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
    try {for(char c = (char)(0xFFFF&in.read());running && c<0x100;
                  c = (char)(0xFFFF&in.read()))
    {
if(verbose) System.out.print(c);
      out.write((byte)c);
      yield();
    }} catch (java.io.IOException ex){
        ex.printStackTrace(System.err);
    }
if(verbose) System.out.println("pump terminated");
  }

  public void halt()
  {
    running = false;
  }
}
