/****************************************************************
 **
 **  $Id: SpecialFontMetrics.java,v 1.1 1997/11/12 18:33:04 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/SpecialFontMetrics.java,v $
 **
 ****************************************************************
 **
 **  Gadget Windowing Toolkit (GWT) Java Class Library
 **  Copyright (C) 1997  DTAI, Incorporated (http://www.dtai.com)
 **
 **  This library is free software; you can redistribute it and/or
 **  modify it under the terms of the GNU Library General Public
 **  License as published by the Free Software Foundation; either
 **  version 2 of the License, or (at your option) any later version.
 **
 **  This library is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 **  Library General Public License for more details.
 **
 **  You should have received a copy of the GNU Library General Public
 **  License along with this library (file "COPYING.LIB"); if not,
 **  write to the Free Software Foundation, Inc.,
 **  59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 **
 ****************************************************************/

package dtai.gwt;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;

/**
 * SpecialFontMetrics is an abstract class through which GWT can support self-rendered fonts
 * (like those used for SignWriting for the deaf).
 *
 * @version 1.1
 * @author DTAI, Incorporated
 */
public abstract class SpecialFontMetrics extends FontMetrics {

    protected FontMetrics normalMetrics;

    public SpecialFontMetrics(SpecialFont font) {
        super(font);
        normalMetrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
    }

    /**
     * Gets the standard leading, or line spacing, for the font.
     * This is the logical amount of space to be reserved between the
     * descent of one line of text and the ascent of the next line.
     * The ascent metric is calculated to include this extra space.
     */
    public int getLeading() {
    	return normalMetrics.getLeading();
    }

    /**
     * Gets the font descent. The font descent is the distance from the
     * base line to the bottom of most Alphanumeric characters.  Note,
     * however, that some characters in the font may extend below this
     * ascent.
     * @see #getMaxDescent
     */
    public int getDescent() {
    	return normalMetrics.getDescent();
    }

    /**
     * Returns the maximum ascent for showing the specified String
     * in this Font.
     * @param str the String to be measured
     * @see #charsAscent
     * @see #bytesAscent
     */
    public int stringAscent(String str) {
    	int len = str.length();
    	char data[] = new char[len];
    	str.getChars(0, len, data, 0);
    	return charsAscent(data, 0, len);
    }

    /**
     * Returns the maximum ascent for showing the specified array
     * of characters in this Font.
     * The advance ascent is the amount by which the current point is
     * moved from one character to the next in a line of text.
     * @param data the array of characters to be measured
     * @param off the start offset of the characters in the array
     * @param len the number of characters to be measured from the array
     * @see #stringAscent
     * @see #bytesAscent
     */
    public int charsAscent(char data[], int off, int len) {
    	return stringAscent(new String(data, off, len));
    }

    /**
     * Returns the maximum ascent for showing the specified array
     * of bytes in this Font.
     * @param data the array of bytes to be measured
     * @param off the start offset of the bytes in the array
     * @param len the number of bytes to be measured from the array
     * @see #stringAscent
     * @see #charsAscent
     */
    public int bytesAscent(byte data[], int off, int len) {
    	return stringAscent(new String(data, 0, off, len));
    }
}
