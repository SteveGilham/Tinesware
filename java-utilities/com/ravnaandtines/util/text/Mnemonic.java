package com.ravnaandtines.util.text;
import javax.swing.Icon;
import javax.swing.plaf.*;
import java.awt.*;

/**
*  Class Mnemonic - Emulates the useful Window's idiom for labels with included
*  mnemonic info.
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
* <p>
* @author Mr. Tines
* @version 1.0 19-Nov-1998
* @version 1.1  9-May-1999 - font argument, handling && case
*
*/

public class Mnemonic implements Icon
{
    private String label = null;
    private char mnem = 0;
    private int index = -1;
    // assumes LightBlackMetalTheme
    private static final Font controlFontX =
        new FontUIResource("sansserif", Font.PLAIN, 11);
    private static final FontMetrics fmX =
        Toolkit.getDefaultToolkit().getFontMetrics(controlFontX);
    private int hi;
    private int dy;
    private int xmin, xmax;
    private Font controlFont;
    private FontMetrics fm;

    /**
    * Takes a string like "&File" and parses it to
    * "File" and 'F' for memonic labelling of GUI components
    * @param s string giving mnemonic
    */
    public Mnemonic(String s)
    {
        this(s, controlFontX);
    }

    /**
    * Takes a string like "&File" and parses it to
    * "File" and 'F' for memonic labelling of GUI components
    * @param s string giving mnemonic
    * @param f Font in which to render the Icon
    */
    public Mnemonic(String s, Font f)
    {
        controlFont = f;
        if(controlFontX == f)
        {
            fm = fmX;
        }
        else
        {
            fm = Toolkit.getDefaultToolkit().getFontMetrics(controlFont);
        }
        hi = fm.getMaxAscent()+fm.getMaxDescent();
        dy = fm.getMaxDescent();

        index = s.indexOf('&');
        do{
            if(index < 0)
            {
                label = s;  //degenerate - no &
            }
            else if(index == s.length()-1)
            {
                label = s.substring(0, index-1); //degenerate - trailing &
                index = -1;
            }
            else if(index == 0)
            {
                label = s.substring(index+1, s.length()); // leading &
                mnem = s.charAt(index+1);
                if(s.charAt(index+1) != '&')
                {
                    mnem = s.charAt(index+1);
                }
                s = label;
                index = s.indexOf('&', index+1);
            }
            else
            {
                label = s.substring(0,index)+s.substring(index+1, s.length());
                if(s.charAt(index+1) != '&')
                {
                    mnem = s.charAt(index+1);
                }
                s = label;
                index = s.indexOf('&', index+1);
            }
        } while (index >= 0 && mnem == 0);

        if(index >= 0)
        {
            xmin = index==0 ? 0 : fm.stringWidth(label.substring(0,index));
            xmax = fm.stringWidth(label.substring(0,index+1));
        }
    }

    /**
    * returns the &-free label
    * @return string for main label use
    */
    public String getLabel()
    {
        return label;
    }

    /**
    * returns the &-labelled character
    * @return character for mnemonic use
    */
    public char getMnemonic()
    {
        return mnem;
    }

    /**
    * Implements the Icon interface fordrawing the underlined string as an Icon
    */
    public int getIconWidth()
    {
        return fm.stringWidth(label) - 4;
    }

    /**
    * Implements the Icon interface fordrawing the underlined string as an Icon
    */
    public int getIconHeight()
    {
        return hi+2;
    }

    /**
    * Implements the Icon interface fordrawing the underlined string as an Icon
    * @see javax.swing.Icon
    */
    public void paintIcon(Component c, Graphics g, int x, int y)
    {
        g.setFont(controlFont);
        g.setColor(Color.black);
        g.drawString(label, x-2, y+hi-dy);
        g.drawLine(x-2+xmin, y+hi-dy/2, x-2+xmax, y+hi-dy/2);
    }
}