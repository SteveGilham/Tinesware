package com.ravnaandtines.util.text;
import java.text.*;

/**
*  Class Format - Text format operations
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

public class Format
{
    private Format()
    {
    }

    /**
    * Evaluates the local end of line once
    */
    private static String NL = System.getProperty("line.separator");
    /**
    * Given a string, safely word wrap it to a number of chars per line
    * lines being separated by the local line separator string.
    * Note that a word break iterator parses whitespace gaps as words.
    * @param in is the string to format
    * @param chars is the number of characters allowed per line
    * @return the processed string.
    */
    public static String wrapText(String in, int chars)
    {
        BreakIterator bi = BreakIterator.getWordInstance();
        bi.setText(in);
        StringBuffer wrap = new StringBuffer();
        StringBuffer line = new StringBuffer();
        int start = bi.first();
        for (int end = bi.next(); end != BreakIterator.DONE;
            start=end, end=bi.next())
        {
            String word = in.substring(start,end);
            int delta = word.length();
            int l = line.length();
            if(l>0)
            {
                // the line is occupied, 
                if(l+delta > chars)
                {
                    // and will be too long with the new word - so break it now
                    wrap.append(line.toString().trim()+NL);
                    l = 0;
                    line = new StringBuffer();
                }
                else
                {
                    line.append(word);
                }
            }
            if(l == 0) // need to start a new line for this word
            {
                // remove leading spaces in this simple wrap
                word = word.trim();
                delta = word.length();

                if(delta >= chars) // very long word
                {
                    wrap.append(word+NL);
                }
                else
                {
                    line.append(word);
                }
            }
        } // end of scan
        // post any last line
        if(line.length() > 0)
        {
            wrap.append(line.toString()+NL);
        }
        return wrap.toString();
    }

}