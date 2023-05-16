/****************************************************************
 **
 **  $Id: SpecialFont.java,v 1.1 1997/11/12 18:33:04 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/SpecialFont.java,v $
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

/**
 * SpecialFont is an abstract class through which GWT can support self-rendered fonts
 * (like those used for SignWriting for the deaf).
 *
 * @version 1.1
 * @author DTAI, Incorporated
 */
public abstract class SpecialFont extends Font {

    public SpecialFont(String fontName, int fontStyle, int fontSize) {
        super(fontName, fontStyle, fontSize);
    }

    public abstract SpecialFontMetrics getMetrics();

    public abstract void drawString(GadgetGraphics g, String str, int x, int y);
}
