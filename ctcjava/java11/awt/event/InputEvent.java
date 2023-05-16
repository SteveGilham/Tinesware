/****************************************************************
 **
 **  $Id: InputEvent.java,v 1.7 1997/08/06 23:25:17 cvs Exp $
 **
 **  $Source: /cvs/classes/java11/awt/event/InputEvent.java,v $
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

package java11.awt.event;

import java.awt.Event;

public class InputEvent extends ComponentEvent {

    public final static int SHIFT_MASK = Event.SHIFT_MASK;
    public final static int CTRL_MASK = Event.CTRL_MASK;
    public final static int META_MASK = Event.META_MASK;
    public final static int ALT_MASK = Event.ALT_MASK;

    private boolean consumed = false;

    public InputEvent() {
    }

    public InputEvent( Event evt, int id ) {
        super( evt, id );
    }

    public final boolean isShiftDown() {
        return ( ( evt.modifiers & SHIFT_MASK ) != 0 );
    }

    public final boolean isControlDown() {
        return ( ( evt.modifiers & CTRL_MASK ) != 0 );
    }

    public final boolean isMetaDown() {
        return ( ( evt.modifiers & META_MASK ) != 0 );
    }

    public final long getWhen() {
        return evt.when;
    }

    public final int getModifiers() {
        return evt.modifiers;
    }

    public void consume() {
        consumed = true;
    }

    public boolean isConsumed() {
        return consumed;
    }
}
