package com.ravnaandtines.util.image;

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;
import java.util.zip.*;

/**
*  Class IconProducer
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

public class IconProducer implements ImageProducer
{
    /**
    * The array of pixel data, 32-bits of ARGB
    */
	protected int[] data;

    /**
    * The ImageConsumers wired up to this producer
    */
	protected Hashtable ix = new Hashtable(5);
    /**
    * The colour model used
    */
	protected ColorModel cm;
    /**
    * image width in pixels
    */
    protected int width;
    /**
    * image height in pixels
    */
	protected int height;

    /**
    * Create an instance of ImageProducer from the given data
    * @see ImageProducer#ImageProducer
    * @param dataI int[] of pixel values
    * @param widthI int width of image in pixels
    * @param heightI int height of image in pixels
    * dataI.length must be >= widthI*heightI
    */
	public IconProducer(int [] dataI, int widthI, int heightI)
	{
		data = dataI;
		cm = ColorModel.getRGBdefault();
		width = widthI;
		height = heightI;
	}

    /**
    * Create an instance of ImageProducer from the given data
    * @see ImageProducer#ImageProducer
    * @param dataI byte[] of compressed pixel values
    * @param widthI int width of image in pixels
    * @param heightI int height of image in pixels
    * the byte array input must be a GZipped version of the image data
    */
	public IconProducer(byte [] dataI, int widthI, int heightI)
	{
        ByteArrayInputStream in = new ByteArrayInputStream(dataI);
        GZIPInputStream inf = null;
        DataInputStream get = null;
        int [] pix = new int[widthI*heightI];

        try{
            inf = new GZIPInputStream(in);
            get = new DataInputStream(inf);
            try{
                for(int i=0; i<pix.length; ++i) pix[i] = get.readInt();
                } catch(EOFException eof) {
                for(int i2=0; i2<pix.length; ++i2) pix[i2] = 0;
            }
            } catch(IOException io) {
            for(int i2=0; i2<pix.length; ++i2) pix[i2] = 0;
        }
		data = pix;
		cm = ColorModel.getRGBdefault();
		width = widthI;
		height = heightI;
    }

    /**
    * Register an instance of ImageConsumer
    * @see ImageProducer#addConsumer
    * @param ic ImageConsumer to add
    */
	public void addConsumer(ImageConsumer  ic)
	{
		ix.put(ic, ic);
	}


    /**
    * Register an instance of ImageConsumer
    * @see ImageProducer#isConsumer
    * @param ic ImageConsumer to query
    * @return boolean value, true if a registered consumer
    */
    public boolean isConsumer(ImageConsumer  ic)
	{
		return ix.contains(ic);
	}

    /**
    * Remove an instance of ImageConsumer
    * @see ImageProducer#removeConsumer
    * @param ic ImageConsumer to remove
    */
    public void removeConsumer(ImageConsumer  ic)
	{
		ix.remove(ic);
	}

    /**
    * Produce image data for an instance of ImageConsumer
    * @see ImageProducer#startProduction
    * @param ic ImageConsumer to satisfy
    */
    public void startProduction(ImageConsumer  ic)
	{
		addConsumer(ic);
		Enumeration scan = ix.elements();
		while(scan.hasMoreElements())
		{
			requestTopDownLeftRightResend(
				(ImageConsumer) scan.nextElement()
			);
		}
	}

    /**
    * Produce image data for an instance of ImageConsumer
    * @see ImageProducer#requestTopDownLeftRightResend
    * @param ic ImageConsumer to remove
    */
    public void requestTopDownLeftRightResend(ImageConsumer  ic)
	{
		ic.setHints(
			ImageConsumer.TOPDOWNLEFTRIGHT |
			ImageConsumer.SINGLEFRAME |
			ImageConsumer.SINGLEPASS |
			ImageConsumer.COMPLETESCANLINES );
		ic.setColorModel(cm);
		ic.setDimensions(width, height);

		for(int y=0; y<height; ++y)
		{
			ic.setPixels(0,y,width,1, cm, data, y*width, width);
		}

		ic.imageComplete( ImageConsumer.STATICIMAGEDONE );
	}

    /**
    * Produce a fully-fledged image
    * @return Image based on ths contents of this class
    */
    public Image getImage()
    {
        return Toolkit.getDefaultToolkit().createImage(this);
    }

}
