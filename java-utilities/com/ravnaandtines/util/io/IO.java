package com.ravnaandtines.util.io;
import java.io.*;

/**
*  Class IO - simple I/O relatued utilities
* <p>
* Copyright Mr. Tines &lt;tines@windsong.demon.co.uk&gt; 1998
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
* @version 1.0 18-Nov-1998
*
*/

public class IO
{
    private IO()
    {
    }

    /**
    * Given a file name, ensure it exists and is writeable - creating
    * it if necessary and if not possible, return false
    * @param name name of file whose existence is to be forced
    * @return false if could not be opened for write
    */
    public static boolean ensureRWFile(String name)
    {
        // empty name can't be made
        if(null == name) return false;
        if(name.length() == 0) return false;

        // if it exists and isn't a directory or something else odd
        // try for write access
        File wantFile = new File(name);
        if(wantFile.exists())
        {
            if(!wantFile.isFile()) return false;
            return (wantFile.canWrite() && wantFile.canRead());
        }

        // Otherwise, try to create it empty
        try {
            FileOutputStream stubber = new FileOutputStream(wantFile);
            byte [] dummy = new byte[1];
            stubber.write(dummy,0,0);
            stubber.close();
            wantFile = new File(name);
            return (wantFile.canWrite() && wantFile.canRead());
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return false;
    } // ensureRWFile

} 