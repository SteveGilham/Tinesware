package com.ravnaandtines.util.text;

/**
*  Class CryptoString - a mutable String-like class that can be wiped and
* is wiped on finalizing
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
* @version 1.0 17-Nov-1998
*/

public final class CryptoString {

    /**
    * Zeroes a char[]
    * @param xtext char array to clear
    */
    static void wipe(char[] xtext)
    {
        for(int i=0; i<xtext.length; ++i) xtext[i] = 0;
    }

    private char [] text = null;

    /**
    * Default constructor
    */
    public CryptoString()
    {
    }

    /**
    * Takes over management of a char[]
    * @param xtext char array to handle
    */
    public CryptoString(char[] xtext)
    {
        text = xtext; // take over management
    }

    /**
    * Acesses contents as char[]
    * @return char array of content
    */
    public char [] getText()
    {
        return text;
    }

    /**
    * Wipes contents
    */
    public void wipe()
    {
        if(text != null)
         CryptoString.wipe(text);
    }

    /**
    * Wipes contents
    * @exception Throwable from superclass
    */
    protected final void finalize() throws Throwable
    {
        wipe();
        super.finalize();
    }

    /**
    * returns number of characters in content
    * @return content length
    */
    public int length()
    {
        return text.length;
    }

    /**
    * returns number of bytes in character's UTF representation
    * @param c character to encode
    * @return content length
    */
    private static int utf8length(char c)
    {
        if(c>=0 && c<=0x7F) return 1;
        else if(c>=0x80 && c<=0x7FF) return 2;
        return 3;
    }

    /**
    * packs the UTF8 byte representation of the character into
    * a byte array
    * @param c character to encode
    * @param b byte array to encode to
    * @param i starting index for output
    * @return number of bytes in character's UTF representation
    * or 0 if this would overflow the buffer
    */
    private static int utfencode(char c, byte[] b, int i)
    {
        if(i >= b.length) return 0;
        if(c>=0 && c<=0x7F)
        {
            b[i] = (byte)(c&0xFF);
            return 1;
        }
        else if(c>=0x80 && c<=0x7FF)
        {
            if(i+1 >= b.length) return 0;
            b[i] = (byte)(0xC0 | ((c>>6)&0x1F));
            b[i+1] = (byte)(0x80 | (c&0x3F));
            return 2;
        }
        if(i+2 >= b.length) return 0;
        b[i] = (byte)(0xE0 | (((c)>>12) & 0x0F) );
        b[i+1] = (byte)(0x80 | (((c)>>6) & 0x3F) );
        b[i+2] = (byte)(0x80 | ((c)&0x3F) );
        return 3;
    }

    /**
    * returns number of bytes in string's UTF representation
    * @return content length
    */
    public int utf8length()
    {
        int l = 0;
        for(int i = 0; i<text.length;++i) l+=utf8length(text[i]);
        return l;
    }

    /**
    * returns string's UTF representation
    * @param utf buffer for output
    */
    public void getUTF8(byte[] utf)
    {
        int l = 0;
        for(int i=0; i<text.length; ++i)
        {
            l += utfencode(text[i], utf, l);
        }
    }

    /**
    * returns character at given index position
    * @param i index 0 to length-1
    * @return character value
    * @exception StringIndexOutOfBoundsException for invalid index
    */
    public char charAt(int i)
    {
        if ((i < 0) || (i >= text.length))
        {
	        throw new StringIndexOutOfBoundsException(i);
	    }
        return text[i];
    }
}
