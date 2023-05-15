package com.ravnaandtines.util.image;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.zip.*;
import java.util.*;

/**
*  Class ZipImage - takes an Image and writes it as a GZipped byte stream
* as used by IconProducer
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
* @version 1.0 08-Nov-1998
*
*/

public class ZipImage
{
    /**
    * Returns the image as a raw byte stream
    * @param map the image
    * @param c an image observer that is reading the image
    * @return the bytestream, or null if the image is still loading or an IO exception occurs
    */
	public static byte[] getImage(Image map, ImageObserver c)
	{

        int rows = map.getHeight(c);
        int cols = map.getWidth(c);

        if(rows <= 0 || cols <= 0) return null;

		int[] pix = new int[rows*cols];
		PixelGrabber grab = new PixelGrabber(map, 0, 0, cols, rows, pix, 0, cols);
		try {grab.grabPixels();} catch(Exception e)
		{System.out.println("Grab exception ");}

        try{
		    ByteArrayOutputStream out = new ByteArrayOutputStream(4*pix.length);
		    GZIPOutputStream zip = new GZIPOutputStream(out);
		    DataOutputStream dat = new DataOutputStream(zip);

		    int i;
		    for(i=0; i<rows*cols; ++i) dat.writeInt(pix[i]);
		    dat.flush();
		    dat.close();

		    // now we've got a bucket of bytes
		    byte[] stuff = out.toByteArray();
            return stuff;
        } catch (IOException ex) {return null;}
    }

    /**
    * Write the image as raw byte stream that can be used in code
    * @param w the output channel to use
    * @param map the image
    * @param c an image observer that is reading the image
    */
    public static void writeImage(PrintWriter w, Image map, ImageObserver c)
    {
        byte[] stuff = getImage(map, c);
        if(stuff == null) return;

        w.println("//Byte count ="+stuff.length);

		for(int i = 0; i<stuff.length; ++i)
		{
			if(0 == i%15)
			{
				w.println("");
				w.print("    ");
			}
			w.print(""+stuff[i]+",");
		}
		w.println("");
	}
}

