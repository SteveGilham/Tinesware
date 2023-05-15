package com.ravnaandtines.util.text;

/**
* Class Hexprint - formats integral values as full width Hex output
* without truncation of leading zero nybbles.
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
* @version 1.0 16-Nov-1998
*/


public class Hexprint
{
    private static final String hex = "0123456789ABCDEF";
    private static char[] work = new char[16];

    private Hexprint()
    {
    }


    /**
    * Format a byte as a hex string with leading zeroes
    * @param b byte value to format
    * @return Formatted value
    */
    public static String fmt(byte b)
    {
        work[0] = hex.charAt( (b>>4)&0xF );
        work[1] = hex.charAt( b&0xF );
        return new String(work, 0, 2);
    }

    /**
    * Format a short as a hex string with leading zeroes
    * @param b shorte value to format
    * @return Formatted value
    */
    public static String fmt(short b)
    {
        for(int i=0; i<4; ++i)
        {
            work[i] = hex.charAt( (b>>(4*(3-i)))&0xF );
        }
        return new String(work, 0, 4);
    }

    /**
    * Format an int as a hex string with leading zeroes
    * @param b int value to format
    * @return Formatted value
    */
    public static String fmt(int b)
    {
        for(int i=0; i<8; ++i)
        {
            work[i] = hex.charAt( (b>>(4*(7-i)))&0xF );
        }
        return new String(work, 0, 8);
    }

    /**
    * Format a long as a hex string with leading zeroes
    * @param b int value to format
    * @return Formatted value
    */
    public static String fmt(long b)
    {
        for(int i=0; i<16; ++i)
        {
            work[i] = hex.charAt( (int)(b>>(4*(15-i)))&0xF );
        }
        return new String(work, 0, 16);
    }
}