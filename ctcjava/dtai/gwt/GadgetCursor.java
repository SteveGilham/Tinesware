/****************************************************************
 **
 **  $Id: GadgetCursor.java,v 1.2 1997/08/06 23:27:03 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/GadgetCursor.java,v $
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

import java.awt.Frame;

/**
 * A class to encapsulate the cursor.
 *
 * @version 	1.1
 * @author 	DTAI, Incorporated
 * @see Gadget#setCursor
 */
public class GadgetCursor {

    public static final int DEFAULT_CURSOR = Frame.DEFAULT_CURSOR;
    public static final int CROSSHAIR_CURSOR = Frame.CROSSHAIR_CURSOR;
    public static final int TEXT_CURSOR = Frame.TEXT_CURSOR;
    public static final int WAIT_CURSOR = Frame.WAIT_CURSOR;
    public static final int SW_RESIZE_CURSOR = Frame.SW_RESIZE_CURSOR;
    public static final int SE_RESIZE_CURSOR = Frame.SE_RESIZE_CURSOR;
    public static final int NW_RESIZE_CURSOR = Frame.NW_RESIZE_CURSOR;
    public static final int NE_RESIZE_CURSOR = Frame.NE_RESIZE_CURSOR;
    public static final int N_RESIZE_CURSOR = Frame.N_RESIZE_CURSOR;
    public static final int S_RESIZE_CURSOR = Frame.S_RESIZE_CURSOR;
    public static final int W_RESIZE_CURSOR = Frame.W_RESIZE_CURSOR;
    public static final int E_RESIZE_CURSOR = Frame.E_RESIZE_CURSOR;
    public static final int HAND_CURSOR = Frame.HAND_CURSOR;
    public static final int MOVE_CURSOR = Frame.MOVE_CURSOR;

    protected static GadgetCursor predefined[] = {
        new GadgetCursor(DEFAULT_CURSOR),
        new GadgetCursor(CROSSHAIR_CURSOR),
        new GadgetCursor(TEXT_CURSOR),
        new GadgetCursor(WAIT_CURSOR),
        new GadgetCursor(SW_RESIZE_CURSOR),
        new GadgetCursor(SE_RESIZE_CURSOR),
        new GadgetCursor(NW_RESIZE_CURSOR),
        new GadgetCursor(NE_RESIZE_CURSOR),
        new GadgetCursor(N_RESIZE_CURSOR),
        new GadgetCursor(S_RESIZE_CURSOR),
        new GadgetCursor(W_RESIZE_CURSOR),
        new GadgetCursor(E_RESIZE_CURSOR),
        new GadgetCursor(HAND_CURSOR),
        new GadgetCursor(MOVE_CURSOR),
    };

    private int type;

    public GadgetCursor(int type) {
        this.type = type;
    }

    public static GadgetCursor getPredefinedCursor(int type) {
        return predefined[type];
    }

    public static GadgetCursor getDefaultCursor() {
        return predefined[DEFAULT_CURSOR];
    }

    public int getType() {
        return type;
    }
}
