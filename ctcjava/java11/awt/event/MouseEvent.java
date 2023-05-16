/****************************************************************
 **
 **  $Id: MouseEvent.java,v 1.10 1997/08/06 23:25:17 cvs Exp $
 **
 **  $Source: /cvs/classes/java11/awt/event/MouseEvent.java,v $
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
import java.awt.Point;

public class MouseEvent extends InputEvent {

    public final static int MOUSE_CLICKED = 5;
    public final static int MOUSE_PRESSED = 6;
    public final static int MOUSE_RELEASED = 7;
    public final static int MOUSE_MOVED = 8;
    public final static int MOUSE_ENTERED = 9;
    public final static int MOUSE_EXITED = 10;
    public final static int MOUSE_DRAGGED = 11;

    public final static int MOUSE_FIRST = 5;
    public final static int MOUSE_LAST = 11;

    public int x;
    public int y;

    public MouseEvent() {
    }

    public MouseEvent( Event evt, int id ) {
        super( evt, id );
        x = evt.x;
        y = evt.y;
    }

    public final int getX() {
        return x;
    }

    public final int getY() {
        return y;
    }

    public synchronized final Point getPoint() {
        return new Point( x, y );
    }

    public synchronized void translatePoint( int x, int y ) {
        this.x += x;
        this.y += y;
    }

    public final int getButton() {
        return 1;
    }

    public final int getClickCount() {
        return evt.clickCount;
    }
}
