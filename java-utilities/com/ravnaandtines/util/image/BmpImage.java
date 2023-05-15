package com.ravnaandtines.util.image;

import java.io.*;
import java.awt.Image;
import java.awt.Color;
import java.awt.image.MemoryImageSource;

/**
*  Class BmpImage - takes a Windows Bitmap and creates a MemoryImageSource
* from it.  Based on GLPL'd code in the V toolkit (a cross platform C++
* GUI interface toolkit), itself LGPL'd.  It handles most
* uncompressed BMP formats
* <p>
* As the MS JVM doesn't handle MemoryImageSource with a negative per-line
* offset sensibly, manually invert the lines in the image when reading it
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
* @version 1.0 05-May-1999
* @version 1.1 15-May-1999
*
*/

public class BmpImage
{
    private static final int BI_RGB  =0;
    private static final int BI_RLE8 =1;
    private static final int BI_RLE4 =2;
    private static final int WIN =40;
    private static final int OS2 =64;
    private MemoryImageSource handle = null;
    /**
    * Builds a memory based interpretation of the BMP
    * @param filename name of file holding the image
    */
    public BmpImage(String filename) throws IOException
    {
        InputStream fileIn = new FileInputStream(filename);

        int          i, c, c1, rv;
        int bfSize, bfOffBits, Size, Width, Height, Planes;
        int BitCount, Compression, SizeImage, XPelsPerMeter;
        int YPelsPerMeter, ClrUsed, ClrImportant;
        int bPad;

        // First two bytes must be "BM"

        c = fileIn.read();  c1 = fileIn.read();
        if (c !='B' || c1 != 'M')		// anything not BM fails
            throw new IOException("Bad format");
        bfSize = rdInt32(fileIn);
        rdInt16(fileIn);         		// reserved: ignore
        rdInt16(fileIn);
        bfOffBits = rdInt32(fileIn);

        Size = rdInt32(fileIn);

        if (Size == WIN || Size == OS2)  // read header info
        {
	        Width         = rdInt32(fileIn);
	        Height        = rdInt32(fileIn);
	        Planes        = rdInt16(fileIn);
	        BitCount      = rdInt16(fileIn);
	        Compression   = rdInt32(fileIn);
	        SizeImage     = rdInt32(fileIn);
	        XPelsPerMeter = rdInt32(fileIn);
	        YPelsPerMeter = rdInt32(fileIn);
	        ClrUsed       = rdInt32(fileIn);
	        ClrImportant  = rdInt32(fileIn);
        }
        else
	        throw new IOException("Bad format");	// old format, we won't handle!

        // Check to see if things are as they should be
        if ((BitCount!=1 && BitCount!=4 && BitCount!=8 && BitCount!=24) ||
            Planes!=1 || Compression > BI_RLE4)
	        throw new IOException("Bad format");
        if (((BitCount==1 || BitCount==24) && Compression != BI_RGB) ||
	        Compression != BI_RGB)	// only uncompressed for now
//         (BitCount==4 && Compression==BI_RLE8) ||
//         (BitCount==8 && Compression==BI_RLE4))
	        throw new IOException("Bad format");

        // Skip to color map
        c = Size - 40;    // 40 bytes read from Size to ClrImportant
        for (i=0; i<c; i++)
	        if(fileIn.read()<0) throw new EOFException();

        bPad = bfOffBits - (Size + 14);	// padding after color map

        Color[] cmap = null;

        if (BitCount != 24)		// colormap for 1 or 4 or 8
        {
	        int cmaplen;
	        cmaplen = (ClrUsed!=0) ? ClrUsed : 1 << BitCount;
            cmap = new Color[cmaplen+1];
	        int r,g,b;
	        for (i=0; i<cmaplen; i++)
	        {
	            b = fileIn.read();
	            g = fileIn.read();
	            r = fileIn.read();
                // skip pad byte
                if(r < 0 || g < 0 || b < 0 || fileIn.read()<0)
                    throw new EOFException();
	            bPad -= 4;			// we just read 4 bytes
                cmap[i] = new Color(r,g,b);
            }
            cmap[i] = new Color(127,127,127);
        }

        // Now, skip over any unused bytes between the color map (if there
        // was one, and the start of the bitmap data.
        while (bPad > 0)
        {
	        if(fileIn.read()<0) throw new EOFException();
	        bPad--;
        }

        // Now, read rest of file

        int limit = (BitCount == 24) ? Width*Height*3 : Width*Height;

        byte [] rev = new byte[limit];

        if (BitCount == 1)
	        rdBMP1(fileIn,rev,Width,Height);
        else if (BitCount == 4)
	        rdBMP4(fileIn,rev,Width,Height,Compression);
        else if (BitCount == 8)
	        rdBMP8(fileIn,rev,Width,Height,Compression);
        else
	        rdBMP24(fileIn,rev,Width,Height);

        int[] image = new int[Width*Height];
        int ix;

        if(BitCount == 24)
        {
            for(ix=0; ix<image.length; ++ix)
            {
                image[ix] = ((rev[3*ix]&0xFF)<<16) |
                            ((rev[3*ix+1]&0xFF)<<8)|
                            (rev[3*ix+2]&0xFF);
            }
        }
        else
        {
            for(ix=0; ix<image.length; ++ix)
            {
                image[ix] = cmap[rev[ix]&0xFF].getRGB();
            }
        }

        handle = new MemoryImageSource(Width, Height,
            image, 0, Width);
  }

    /**
    * Returns the image in a useful form
    * @return MemoryImageSource holding the image
    */
    public MemoryImageSource getMemoryImageSource(){return handle;}

//=========================>> rdBMP1 <<=============================
  private static void rdBMP1(InputStream fileIn, byte[] rev,
	int w, int h) throws IOException
  {
    int i,j,c,bitnum,padw;
    c = 0;
    padw = ((w + 31)/32) * 32;  /* 'w', padded to be a multiple of 32 */

    for (i=h-1; i>=0; i--)
    {
	    for (j=bitnum=0; j<padw; j++,bitnum++)
	    {
	        if ((bitnum&7) == 0)
	        { /* read the next byte */
		        c = fileIn.read();
                if(c<0) throw new EOFException();
                bitnum = 0;
	        }

	        if (j<w)
	        {
		        rev[j+(i*w)] = (byte)((c & 0x80)!=0 ? 1 : 0);
		        c <<= 1;
	        }
	    }
    }
  }

//=========================>> rdBMP4 <<=============================
  private static void rdBMP4(InputStream fileIn, byte[] rev,
		    int w, int h, int comp) throws IOException
  {
    int   i,j,c,c1,x,y,nybnum,padw;
    c = c1 = 0;

    if (comp == BI_RGB)			// read uncompressed data
    {
	    padw = ((w + 7)/8) * 8; 	// 'w' padded to a multiple of 8pix (32 bits)
	    for (i=h-1; i>=0; i--)
	    {
	        for (j=nybnum=0; j<padw; j++,nybnum++)
	        {
		        if ((nybnum & 1) == 0)	// read next byte
		        { /* read next byte */
		            c = fileIn.read();
                    if(c<0) throw new EOFException();
    		        nybnum = 0;
		        }
		        if (j<w)
		        {
		            rev[j+(i*w)] = (byte)((c & 0xf0) >>> 4);
        		    c <<= 4;
		        }
	        }
	    }
    }
    else throw new IOException("Can't take compressed input");
  }
//=========================>> rdBMP8 <<=============================
  private static void rdBMP8(InputStream fileIn, byte[] rev,
		    int w, int h, int comp) throws IOException
  {
    int   i,j,c,c1,padw,x,y;
    if (comp == BI_RGB)		// read uncompressed data
    {
	    padw = ((w + 3)/4) * 4; /* 'w' padded to a multiple of 4pix (32 bits) */
	    for (i=h-1; i>=0; i--)
	    {
	        for (j=0; j<padw; j++)
	        {
		        c = fileIn.read();
		        if (c<0) throw new EOFException();
		        if (j<w)
		        {
		            rev[j+(i*w)] = (byte)c;
                }
	        }
	    }
    }
    else throw new IOException("Can't take compressed input");
  }

//=========================>> rdBMP24 <<=============================
  private static void rdBMP24(InputStream fileIn,  byte[] rev,
		     int w, int h) throws IOException
  {
    int   i,j,padb,rv;
    int r,g,b;

    padb = (4 - ((w*3) % 4)) & 0x03;  // # of pad bytes to read at EOscanline

    for (i=h-1; i>=0; i--)
      {
	for (j=0; j<w; j++)
	  {
	    b=fileIn.read();   // blue
	    g=fileIn.read();   // green
	    r=fileIn.read();   // red
        if(r < 0 || g < 0 || b < 0) throw new EOFException();
	    rev[3*(j+(i*w))] = (byte)r;
        rev[3*(j+(i*w))+1] = (byte)g;
        rev[3*(j+(i*w))+2] = (byte)b;
	  }

	for (j=0; j<padb; j++)
	    fileIn.read();
      }
  }

//=============================>>> rdInt16 <<<=============================
  private static int rdInt16(InputStream fileIn) throws IOException
  {
    int c, c1;
    c = fileIn.read(); c1 = fileIn.read();
    if(c < 0 || c1 < 0) throw new EOFException();
    return c | (c1 << 8);
  }

//=============================>>> rdInt32 <<<=============================
  private static int rdInt32(InputStream fileIn) throws IOException
  {
    int c, c1, c2, c3;
    c = fileIn.read();  c1 = fileIn.read();  c2 = fileIn.read();  c3 = fileIn.read();
    if(c < 0 || c1 < 0 || c2 < 0 || c3 < 0) throw new EOFException();
    return c | (c1<<8) | (c2<<16) | (c3<<24);
  }

    /**
    * Displays a .BMP image
    * @param String[] containing file name as first member
    */
    public static void main(String[] args)
    {
        try {
        BmpImage test = new BmpImage(args[0]);
        com.ravnaandtines.util.gui.XFrame frame = new
            com.ravnaandtines.util.gui.XFrame("BMP viewer");
        javax.swing.JPanel p = new javax.swing.JPanel();
        frame.add("Center",p);
        p.setLayout(new java.awt.BorderLayout());

        frame.addNotify();
        java.awt.Image im = frame.createImage(test.getMemoryImageSource());
        p.add("Center", new javax.swing.JLabel(new
            javax.swing.ImageIcon(im)));
        p.revalidate();

		// Plonk it on center of screen
		frame.pack();
		java.awt.Dimension WindowSize = frame.getSize(),
            ScreenSize=java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		frame.setBounds((ScreenSize.width-WindowSize.width)/2,
            (ScreenSize.height-WindowSize.height)/2,
            WindowSize.width,WindowSize.height);

        frame.setIconImage(frame.createImage(test.getMemoryImageSource()));
		frame.setVisible(true);
        } catch (Exception e) { e.printStackTrace();}
    }

}

