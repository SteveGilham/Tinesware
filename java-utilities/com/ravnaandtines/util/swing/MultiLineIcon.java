package com.ravnaandtines.util.swing;
import javax.swing.Icon;
import javax.swing.SwingConstants;
import javax.swing.plaf.*;
import java.awt.*;

/**
*  Class MultiLineIcon - Emulates multi-line buttons and such by allowing
*  a Mnemonic to be used for each line, and stacking these together - but
*  made general purpose for stacking any sort of Icon
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
*
*/

public class MultiLineIcon implements Icon
{
    private Icon[] payload = null;
    private int align = SwingConstants.LEFT;

    /**
    * Takes an array of icons, and an alignment constant
    * @param values the Icons to stack
    * @param side the alignment of the icons in the stack (JLabel constant)
    */
    public MultiLineIcon(Icon[] values, int side)
    {
        payload = values;
        align = side;
    }

    /**
    * Takes an array of strings, and an alignment constant -
    * useful for multi-line labels
    * @param values the Strings to stack
    * @param side the alignment of the icons in the stack (JLabel constant)
    */
    public MultiLineIcon(String[] values, int side)
    {
        payload = new Icon[values.length];
        for(int i=0; i<values.length; ++i)
        {
            payload[i] = new com.ravnaandtines.util.text.Mnemonic(values[i]);
        }
        align = side;
    }

    /**
    * Takes an array of strings, a fonta, and an alignment constant -
    * useful for multi-line labels
    * @param values the Strings to stack
    * @param side the alignment of the icons in the stack (JLabel constant)
    */
    public MultiLineIcon(String[] values, Font f, int side)
    {
        payload = new Icon[values.length];
        for(int i=0; i<values.length; ++i)
        {
            payload[i] = new com.ravnaandtines.util.text.Mnemonic(values[i], f);
        }
        align = side;
    }

    /**
    * Implements the Icon interface for drawing the Icons
    */
    public int getIconWidth()
    {
        // can't cache this as the values can be variable
        int w = 0;
        for(int i=0; i<payload.length; ++i)
        {
            w = Math.max(w, payload[i].getIconWidth());
        }
        return w;
    }

    /**
    * Implements the Icon interface for drawing the Icons
    */
    public int getIconHeight()
    {
        int h = 2*(payload.length-1);
        for(int i=0; i<payload.length; ++i)
        {
            h += payload[i].getIconHeight();
        }
        return h;
    }

    /**
    * Implements the Icon interface for drawing the Icons
    * @see javax.swing.Icon
    */
    public void paintIcon(Component c, Graphics g, int x, int y)
    {
        int y2 = y;
        int w = getIconWidth();

        for(int i=0; i<payload.length; ++i)
        {
            int x2 = x;
            if(SwingConstants.RIGHT == align)
                x2 += w-payload[i].getIconWidth();
            else if(SwingConstants.CENTER == align)
                x2 += (w-payload[i].getIconWidth())/2;

            payload[i].paintIcon(c, g, x2, y2);
            y2 += payload[i].getIconHeight()+2;
        }
    }
}
