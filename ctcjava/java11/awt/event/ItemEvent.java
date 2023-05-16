/****************************************************************
 **
 **  $Id: ItemEvent.java,v 1.5 1997/08/06 23:25:17 cvs Exp $
 **
 **  $Source: /cvs/classes/java11/awt/event/ItemEvent.java,v $
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
import java11.awt.ItemSelectable;
import java11.util.EventObject;

public class ItemEvent extends AWTEvent {

/**
 *    The item state changed event type.
 */
    public final static int ITEM_STATE_CHANGED = 25;

    public final static int ITEM_FIRST = 25;
    public final static int ITEM_LAST = 25;

/**
 *    The item selected state change type.
 */
    public final static int SELECTED = 1;
/**
 *    The item de-selected state change type.
 */
    public final static int DESELECTED = 0;

    private Object item;
    private int stateChange;

    public ItemEvent() {
    }

    public ItemEvent( java11.awt.ItemSelectable source, Event evt, int id,
                      Object item, int stateChange ) {
        super( evt, id );
        setSource( source );
        this.item = item;
        this.stateChange = stateChange;
    }

/**
 *    Returns the ItemSelectable object where this event originated.
 */

    public java11.awt.ItemSelectable getItemSelectable() {
        return (java11.awt.ItemSelectable)getSource();
    }

/**
 *    Returns the item where the event occurred.
 */

    public Object getItem() {
        return item;
    }

/**
 *    Returns the state change type which generated the event.
 *
 *    See Also:
 *         SELECTED, DESELECTED
 */

    public int getStateChange() {
        return stateChange;
    }
}
