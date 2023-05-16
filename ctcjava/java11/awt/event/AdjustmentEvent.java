/****************************************************************
 **
 **  $Id: AdjustmentEvent.java,v 1.6 1997/08/06 23:25:16 cvs Exp $
 **
 **  $Source: /cvs/classes/java11/awt/event/AdjustmentEvent.java,v $
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
import java11.awt.Adjustable;
import java11.util.EventObject;

public class AdjustmentEvent extends AWTEvent {

    public final static int UNIT_DECREMENT = 18;
    public final static int UNIT_INCREMENT = 19;
    public final static int BLOCK_DECREMENT = 20;
    public final static int BLOCK_INCREMENT = 21;
    public final static int TRACK = 22;
    public final static int ADJUSTMENT_VALUE_CHANGED = 22;
    public final static int BEGIN = 23;
    public final static int END = 24;

    public final static int ADJUSTMENT_FIRST = 18;
    public final static int ADJUSTMENT_LAST = 24;

    private java11.awt.Adjustable adjustable;
    private int value = 0;

    public AdjustmentEvent() {
    }

    public AdjustmentEvent( Event evt, int id, java11.awt.Adjustable source, int value ) {
        super( evt, id );
        setSource( source );
        adjustable = source;
        this.value = value;
    }

/**
 *   Returns the Adjustable object where this event originated.
 */

    public java11.awt.Adjustable getAdjustable() {
        return adjustable;
    }

/**
 *   Returns the current value in the adjustment event.
 */

    public int getValue() {
        return value;
    }
}
