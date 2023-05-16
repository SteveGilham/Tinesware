/****************************************************************
 **
 **  $Id: ItemSelectable.java,v 1.3 1997/08/06 23:25:16 cvs Exp $
 **
 **  $Source: /cvs/classes/java11/awt/ItemSelectable.java,v $
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

package java11.awt;

import java11.awt.event.ItemListener;

public interface ItemSelectable {

/**
 *   Returns the selected indexes or null if no items are selected.
 */

    public abstract int[] getSelectedIndexes();

/**
 *   Returns the selected items or null if no items are selected.
 */

    public abstract String[] getSelectedItems();

/**
 *    Add a listener to recieve item events when the state of an item changes.
 *
 *    Parameters:
 *         l - the listener to recieve events
 *    See Also:
 *         ItemEvent
 */

    public abstract void addItemListener(ItemListener l);

/**
 *    Removes an item listener.
 *
 *    Parameters:
 *         l - the listener being removed
 *    See Also:
 *         ItemEvent
 */

    public abstract void removeItemListener(ItemListener l);
}
