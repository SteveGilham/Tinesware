/****************************************************************
 **
 **  $Id: KeyEvent.java,v 1.8 1997/08/06 23:25:17 cvs Exp $
 **
 **  $Source: /cvs/classes/java11/awt/event/KeyEvent.java,v $
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

public class KeyEvent extends InputEvent {

    public final static int KEY_TYPED = 12;
    public final static int KEY_PRESSED = 13;
    public final static int KEY_RELEASED = 14;

    public final static int KEY_FIRST = 12;
    public final static int KEY_LAST = 14;

    public final static int HOME = Event.HOME;
    public final static int END = Event.END;
    public final static int PGUP = Event.PGUP;
    public final static int PGDN = Event.PGDN;
    public final static int UP = Event.UP;
    public final static int DOWN = Event.DOWN;
    public final static int LEFT = Event.LEFT;
    public final static int RIGHT = Event.RIGHT;
    public final static int F1 = Event.F1;
    public final static int F2 = Event.F2;
    public final static int F3 = Event.F3;
    public final static int F4 = Event.F4;
    public final static int F5 = Event.F5;
    public final static int F6 = Event.F6;
    public final static int F7 = Event.F7;
    public final static int F8 = Event.F8;
    public final static int F9 = Event.F9;
    public final static int F10 = Event.F10;
    public final static int F11 = Event.F11;
    public final static int F12 = Event.F12;

    private final static int CR = 13;

    public final static int ENTER = 10;
    public final static int BACK_SPACE = 8;
    public final static int TAB = 9;
    public final static int ESCAPE = 27;
    public final static int DELETE = 127;
    /*
    public final static int PRINT_SCREEN = Event.PRINT_SCREEN;
    public final static int SCROLL_LOCK = Event.SCROLL_LOCK;
    public final static int CAPS_LOCK = Event.CAPS_LOCK;
    public final static int NUM_LOCK = Event.NUM_LOCK;
    public final static int PAUSE = Event.PAUSE;
    public final static int INSERT = Event.INSERT;
    */

	private boolean action;

    public KeyEvent() {
    }

    public KeyEvent(Event evt, int id, boolean action) {
        super( evt, id );
        this.action = action;

		if ( ! action ) {
		    if ( evt.key == CR ) {
		        evt.key = ENTER;
		    }
		    if ( ( evt.key == ENTER ) ||
		         ( evt.key == BACK_SPACE ) ||
		         ( evt.key == TAB ) ||
		         ( evt.key == ESCAPE ) ||
		         ( evt.key == DELETE ) ) {
		        this.action = true;
		    }
		}
	}

    public int getKeyCode() {
		return evt.key;
	}

    public char getKeyChar() {
		return (char)evt.key;
	}

    public boolean isActionKey() {
		return action;
	}
}
