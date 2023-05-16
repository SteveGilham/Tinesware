/****************************************************************
 **
 **  $Id: WindowEvent.java,v 1.4 1997/08/06 23:25:17 cvs Exp $
 **
 **  $Source: /cvs/classes/java11/awt/event/WindowEvent.java,v $
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
import java.awt.Window;
import java11.util.EventObject;

public class WindowEvent extends AWTEvent {

    public final static int WINDOW_OPENED = 226;
    public final static int WINDOW_CLOSING = 227;
    public final static int WINDOW_CLOSED = 228;
    public final static int WINDOW_ICONIFIED = 229;
    public final static int WINDOW_DEICONIFIED = 230;
    public final static int WINDOW_ACTIVATED = 231;
    public final static int WINDOW_DEACTIVATED = 232;

    public final static int WINDOW_FIRST = 226;
    public final static int WINDOW_LAST = 232;

    public WindowEvent() {
    }

    public WindowEvent( Object source, Event evt, int id ) {
        super( evt, id );
        setSource( source );
    }

    /**
     * Returns the window where this event originated.
     */

    public Window getWindow() {
        return (Window)getSource();
    }
}
