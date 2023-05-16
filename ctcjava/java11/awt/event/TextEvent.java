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
 **  History
 **  TextEvent created from skeleton of WindowEvent
 **              -- Mr. Tines, May 98.
 **
 ****************************************************************/

package java11.awt.event;

import java.awt.Event;
import dtai.gwt.TextGadget;
import java11.util.EventObject;

public class TextEvent extends AWTEvent {

    public final static int TEXT_VALUE_CHANGED = 240;

    public final static int TEXT_FIRST = 240;
    public final static int TEXT_LAST = 240;

    public TextEvent() {
    }

    public TextEvent( Object source, Event evt, int id ) {
        super( evt, id );
        setSource( source );
    }

    /**
     * Returns the window where this event originated.
     */

    public TextGadget getTextGadget() {
        return (TextGadget)getSource();
    }
}
