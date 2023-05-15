package com.ravnaandtines.util.image;

import java.io.*;
import java.awt.Image;
import java.awt.Color;
import java.awt.image.MemoryImageSource;

/**
 * Class IcoImage - takes a Windows .ico file and creates a MemoryImageSource
 * from it.  This doesn't try to cover all possible cases (e.g. compression,
 * 16 or 32 bit depth images), but works with the standard output from DevStudio.
 * It contains inner classes which match some of the native structures, but are
 * probably more than are actually needed for function.
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
 * @version 1.2 25-May-2002 add transparent pixel handling.
 *
 */

public class IcoImage {
    private static final int BI_RGB  =0;
    private IconImage[]	iconImages = null;                   // Image entries
    
    class IconImage {
        int 			Width, Height, Colors; // Width, Height and bpp
        int[]			lpBits = null;                // ptr to DIB bits
        int 			dwNumBytes;            // how many bytes?
        BitmapInfo  	bmiHeader;                  // ptr to header
        //byte			lpXOR;                 // ptr to XOR image bits
        //byte		    lpAND;                 // ptr to AND image bits
    }
    
    class BitmapInfo {
        int biSize;
        int biWidth;
        int biHeight;
        short biPlanes;
        short biBitCount;
        int biCompression;
        int biSizeImage;
        int biXPelsPerMeter;
        int biYPelsPerMeter;
        int biClrUsed;
        int biClrImportant;
    }
    
    class IconDirEntry {
        byte 	bWidth;               // Width of the image
        byte 	bHeight;              // Height of the image (times 2)
        byte 	bColorCount;          // Number of colors in image (0 if >=8bpp)
        byte 	bReserved;            // Reserved
        short	wPlanes;              // Color Planes
        short 	wBitCount;            // Bits per pixel
        int	dwBytesInRes;         // how many bytes in this resource?
        int	dwImageOffset;        // where in the file is this image
    }
    
    /**
     * Retuns the number of icons that have been extracted from the file -
     * the file format is of a number of BMP-like images after a header
     * with directory entries
     * @return number of icons
     */
    public int getIconCount() {
        return iconImages.length;
    }
    
    private MemoryImageSource[] mis = null;
    
    /**
     * Returns one of the images in a useful form
     * @param i the index number of the image
     * @return MemoryImageSource holding the image
     */
    public MemoryImageSource getMemoryImageSource(int i) {
        java.awt.image.DirectColorModel cm = new
                java.awt.image.DirectColorModel(32,
                0xFF0000, 0xFF00, 0xFF, 0xFF000000);
        
        if(null == mis[i]) {
            mis[i] = new MemoryImageSource(iconImages[i].Width,
                    iconImages[i].Height,   cm,
                    iconImages[i].lpBits,
                    0,
                    iconImages[i].Width);
        }
        return mis[i];
    }
    
    /**
     * Displays a the images from an .ICO file
     * @param String[] containing file name as first member
     */
    public static void main(String[] args) {
        try {
            com.ravnaandtines.util.gui.XFrame frame = new
                    com.ravnaandtines.util.gui.XFrame("Icon Gallery");
            javax.swing.JPanel p = new javax.swing.JPanel();
            frame.add("Center", p);
            p.setLayout(new java.awt.FlowLayout());
            
            frame.addNotify();
            IcoImage test = new IcoImage(args[0]);
            for(int i = 0; i<test.getIconCount(); ++i) {
                java.awt.Image im =
                        frame.createImage(test.getMemoryImageSource(i));
                p.add(new javax.swing.JLabel(new javax.swing.ImageIcon(im)));
            }
            p.revalidate();
            
            // Plonk it on center of screen
            frame.pack();
            java.awt.Dimension WindowSize=frame.getSize(),
                    ScreenSize=java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            frame.setBounds((ScreenSize.width-WindowSize.width)/2,
                    (ScreenSize.height-WindowSize.height)/2,
                    WindowSize.width,WindowSize.height);
            
            frame.setIconImage(frame.createImage(test.getMemoryImageSource(0)));
            frame.setVisible(true);
            
            
        } catch (Exception e) { e.printStackTrace();}
    }
    
    /**
     * Unpacks a the images from an .ICO file, transparent areas
     * mapped to transparent
     * @param filename the .ico file
     */
    public IcoImage(String filename) throws IOException {
        this(filename, null);
    }
    /**
     * Unpacks a the images from an .ICO file, transparent areas mapped to a
     * supplied colour
     * @param filename the .ico file
     * @param transparent the colour to use for transparent pixels.
     */
    public IcoImage(String filename, Color transparent) throws IOException {
        RandomAccessFile fileIn = new RandomAccessFile(filename, "r");
        int numImages = readHeader(fileIn);
        if(numImages < 1) throw new IOException("Empty icon file");
        iconImages = new IconImage[numImages];
        IconDirEntry[] ide = new IconDirEntry[numImages];
        
        // read directory
        int i;
        for(i=0; i<ide.length; ++i) {
            ide[i] = new IconDirEntry();
            readIDE(fileIn, ide[i]);
        }
        mis = new MemoryImageSource[ide.length];
        
        // read images
        for(i=0; i<ide.length; ++i) {
            iconImages[i] = new IconImage();
            iconImages[i].bmiHeader = new BitmapInfo();
            iconImages[i].dwNumBytes = ide[i].dwBytesInRes;
            fileIn.seek(0xFFFFFFFFL&ide[i].dwImageOffset);
            iconImages[i].bmiHeader = new BitmapInfo();
            readBMIHeader(fileIn, iconImages[i].bmiHeader);
            AdjustIconImagePointers(iconImages[i]);
            
            int Width = iconImages[i].Width;
            int Height = iconImages[i].Height;
            int BitCount = iconImages[i].bmiHeader.biBitCount;
            
            
            Color[] cmap = null;
            
            if (BitCount < 24)		// colormap for 1 or 4 or 8
            {
                int cmaplen;
                cmaplen = (iconImages[i].bmiHeader.biClrUsed!=0) ?
                    iconImages[i].bmiHeader.biClrUsed : 1 << BitCount;
                cmap = new Color[cmaplen+1];
                int r,g,b,ix;
                for (ix=0; ix<cmaplen; ix++) {
                    b = fileIn.readByte();
                    g = fileIn.readByte();
                    r = fileIn.readByte();
                    // skip pad byte
                    fileIn.readByte();
                    cmap[ix] = new Color(r&0xFF,g&0xFF,b&0xFF);
                }
                cmap[ix] = new Color(127,127,127);
            }
            
            int factor = (BitCount + 7)/8;
            int limit = Width*Height*factor;
            
            byte [] rev = new byte[limit];
            if (BitCount == 1)
                rdBMP1(fileIn,rev,Width,Height);
            else if (BitCount == 4)
                rdBMP4(fileIn,rev,Width,Height,iconImages[i].bmiHeader.biCompression);
            else if (BitCount == 8)
                rdBMP8(fileIn,rev,Width,Height,iconImages[i].bmiHeader.biCompression);
            else if (BitCount == 24)
                rdBMP24(fileIn,rev,Width,Height);
            else
                rdBMP32(fileIn,rev,Width,Height);
            
            int[] image = new int[Width*Height];
            iconImages[i].lpBits = image;
            int ix;
            
            if(BitCount == 24) {
                for(ix=0; ix<image.length; ++ix) {
                    image[ix] = ((rev[3*ix]&0xFF)<<16) |
                            ((rev[3*ix+1]&0xFF)<<8)|
                            (rev[3*ix+2]&0xFF);
                }
            }
            
            else if(BitCount == 32) {
                for(ix=0; ix<image.length; ++ix) {
                    image[ix] = ((rev[4*ix]&0xFF)<<16) |
                            ((rev[4*ix+1]&0xFF)<<8)|
                            (rev[4*ix+2]&0xFF) |
                            ((rev[4*ix+3]&0xFF)<<24);
                }
            }
            else {
                for(ix=0; ix<image.length; ++ix) {
                    image[ix] = cmap[rev[ix]&0xFF].getRGB();
                }
            }

            if(BitCount < 32) {
                rdBMP1(fileIn,rev,Width,Height);
                for(ix=0; ix<image.length; ++ix) {
                    if(rev[ix] != 0) {
                        if(transparent != null)
                            //opaque supplied colour
                            iconImages[i].lpBits[ix] = transparent.getRGB() | 0xFF000000;
                        else
                            //transparent
                            iconImages[i].lpBits[ix] &= 0xFFFFFF;
                    } else {
                        //opaque
                        iconImages[i].lpBits[ix] |= 0xFF000000;
                    }
                    
                }
            }
            
        } // next Icon
    }
    
    private static void AdjustIconImagePointers( IconImage lpImage )
    throws IOException {
        // Sanity check
        if( lpImage==null )throw new IOException("Unexpected format");
        
        // Width - simple enough
        lpImage.Width = lpImage.bmiHeader.biWidth;
        // Icons are stored in funky format where height is doubled - account for it
        lpImage.Height = (lpImage.bmiHeader.biHeight)/2;
        // How many colors?
        lpImage.Colors = 1 << lpImage.bmiHeader.biBitCount;
        
        // XOR bits follow the header and color table
        //lpImage->lpXOR = FindDIBBits((LPSTR)lpImage->lpbi);
        // AND bits follow the XOR bits
        //lpImage->lpAND = lpImage->lpXOR + (lpImage->Height*BytesPerLine((LPBITMAPINFOHEADER)(lpImage->lpbi)));
    }
    
    private int readHeader(DataInput in)throws IOException {
        // Read the 'reserved' WORD
        int reserved = rdInt16(in);
        if(reserved != 0) throw new IOException("Unexpected format");
        
        // Read the type WORD
        int type = rdInt16(in);
        if(type != 1) throw new IOException("Unexpected format");
        
        int count = rdInt16(in);
        return count;
    }
    private static int rdInt16(DataInput fileIn) throws IOException {
        int c, c1;
        c = fileIn.readUnsignedByte(); c1 = fileIn.readUnsignedByte();
        return c | (c1 << 8);
    }
    private static int rdInt32(DataInput fileIn) throws IOException {
        int c, c1, c2, c3;
        c = fileIn.readUnsignedByte(); c1 = fileIn.readUnsignedByte();
        c2 = fileIn.readUnsignedByte(); c3 = fileIn.readUnsignedByte();
        return c | (c1<<8) | (c2<<16) | (c3<<24);
    }
    private static void readIDE(DataInput in, IconDirEntry ide)
    throws IOException {
        ide.bWidth = in.readByte();
        ide.bHeight = in.readByte();
        ide.bColorCount = in.readByte();
        ide.bReserved = in.readByte();
        
        ide.wPlanes = (short)rdInt16(in);
        ide.wBitCount = (short)rdInt16(in);
        
        ide.dwBytesInRes = rdInt32(in);
        ide.dwImageOffset = rdInt32(in);
    }
    
    private static void readBMIHeader(DataInput in, BitmapInfo bmi)
    throws IOException {
        bmi.biSize = rdInt32(in);
        bmi.biWidth = rdInt32(in);
        bmi.biHeight = rdInt32(in);
        bmi.biPlanes = (short)rdInt16(in);
        bmi.biBitCount = (short)rdInt16(in);
        bmi.biCompression = rdInt32(in);
        bmi.biSizeImage = rdInt32(in);
        bmi.biXPelsPerMeter = rdInt32(in);
        bmi.biYPelsPerMeter = rdInt32(in);
        bmi.biClrUsed = rdInt32(in);
        bmi.biClrImportant = rdInt32(in);
    }
//=========================>> rdBMP1 <<=============================
    private static void rdBMP1(DataInput fileIn, byte[] rev,
            int w, int h) throws IOException {
        int i,j,c,bitnum,padw;
        c = 0;
        padw = ((w + 31)/32) * 32;  /* 'w', padded to be a multiple of 32 */
        for (i=h-1; i>=0; i--) {
            for (j=bitnum=0; j<padw; j++,bitnum++) {
                if ((bitnum&7) == 0) { /* read the next byte */
                    c = fileIn.readByte();
                    bitnum = 0;
                }
                
                if (j<w) {
                    rev[j+(i*w)] = (byte)((c & 0x80)!=0 ? 1 : 0);
                    c <<= 1;
                }
            }
        }
    }
    
//=========================>> rdBMP4 <<=============================
    private static void rdBMP4(DataInput fileIn, byte[] rev,
            int w, int h, int comp) throws IOException {
        int   i,j,c,c1,x,y,nybnum,padw;
        c = c1 = 0;
        
        if (comp == BI_RGB)			// read uncompressed data
        {
            padw = ((w + 7)/8) * 8; 	// 'w' padded to a multiple of 8pix (32 bits)
            for (i=h-1; i>=0; i--) {
                for (j=nybnum=0; j<padw; j++,nybnum++) {
                    if ((nybnum & 1) == 0)	// read next byte
                    { /* read next byte */
                        c = fileIn.readByte();
                        nybnum = 0;
                    }
                    if (j<w) {
                        rev[j+(i*w)] = (byte)((c & 0xf0) >>> 4);
                        c <<= 4;
                    }
                }
            }
        } else throw new IOException("Can't take compressed input");
    }
    
//=========================>> rdBMP8 <<=============================
    private static void rdBMP8(DataInput fileIn, byte[] rev,
            int w, int h, int comp) throws IOException {
        int   i,j,c,c1,padw,x,y;
        if (comp == BI_RGB)		// read uncompressed data
        {
            padw = ((w + 3)/4) * 4; /* 'w' padded to a multiple of 4pix (32 bits) */
            for (i=h-1; i>=0; i--) {
                for (j=0; j<padw; j++) {
                    c = fileIn.readByte();
                    if (j<w) {
                        rev[j+(i*w)] = (byte)c;
                    }
                }
            }
        } else throw new IOException("Can't take compressed input");
    }
    
//=========================>> rdBMP24 <<=============================
    private static void rdBMP24(DataInput fileIn,  byte[] rev,
            int w, int h) throws IOException {
        int   i,j,padb,rv;
        int r,g,b;
        
        padb = (4 - ((w*3) % 4)) & 0x03;  // # of pad bytes to read at EOscanline
        
        for (i=h-1; i>=0; i--) {
            for (j=0; j<w; j++) {
                b=fileIn.readByte();   // blue
                g=fileIn.readByte();   // green
                r=fileIn.readByte();   // red
                rev[3*(j+(i*w))] = (byte)r;
                rev[3*(j+(i*w))+1] = (byte)g;
                rev[3*(j+(i*w))+2] = (byte)b;
            }
            
            for (j=0; j<padb; j++)
                fileIn.readByte();
        }
    }
//=========================>> rdBMP32 <<=============================
    private static void rdBMP32(DataInput fileIn,  byte[] rev,
            int w, int h) throws IOException {
        int   i,j;
        int r,g,b,a;
        
        System.out.println("Array length 32 = "+rev.length);
        
        for (i=h-1; i>=0; i--) {
            for (j=0; j<w; j++) {
                b=fileIn.readByte();   // blue
                g=fileIn.readByte();   // green
                r=fileIn.readByte();   // red
                a=fileIn.readByte();   // alpha
                rev[4*(j+(i*w))] = (byte)r;
                rev[4*(j+(i*w))+1] = (byte)g;
                rev[4*(j+(i*w))+2] = (byte)b;
                rev[4*(j+(i*w))+3] = (byte)a;
            }
        }
        
    }
    
}



