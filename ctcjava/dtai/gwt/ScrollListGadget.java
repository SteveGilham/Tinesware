/****************************************************************
 **
 **  $Id: ScrollListGadget.java,v 1.24 1997/08/06 23:27:08 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/ScrollListGadget.java,v $
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


/**
 * A scrolling list of text items.
 * @version 1.1
 * @author DTAI, Incorporated
 */

public abstract class ScrollListGadget extends ScrollGadgetGadget {

    /**
     * Creates a new scrolling list prefized with the specified number
     * of visible lines and a boolean stating whether multiple selections
     * are allowed or not.
     *
     * @param rows - the number of items to show.
     * @param multipleMode - if true then multiple selections are allowed.
     */
    public ScrollListGadget(int rows, boolean multipleMode) {
		super( rows, multipleMode );
    }

    /**
     * Returns the number of items in the list.
     *
     * @see getItem
     * @return int
     */
	public final int getItemCount() {
		return getListGadgetCount();
	}

    /**
     * Gets the item associated with the specified index.
     *
     * @param row - the row of the item
     * @see getItemCount
     * @return String
     */
    public abstract String getItem(int row);

    /**
     * Returns the items in the list.
     *
     * @see select
     * @see deselect,
     * @see isIndexSelected
     * @return String[]
     */
    public String[] getItems() {
		synchronized(getTreeLock()) {
            int count = getItemCount();
            String strings[] = new String[count];
            for ( int i = 0; i < count; i++ ) {
                strings[i] = getItem(i);
            }
            return strings;
        }
    }

    /**
     * gets the selected item on the list or null if no item is selected.
     *
     * @see select
     * @see deselect
     * @see isIndexSelected
     * @return String
     */
    public String getSelectedItem() {
		synchronized(getTreeLock()) {
            int index = getSelectedIndex();
            if ( index < 0 ) {
                return null;
            }
            else {
                return getItem(getSelectedIndex());
            }
        }
    }

    /**
     * Gets the selected items on the list.
     *
     * @see select
     * @see deselect
     * @see isIndexSelected
     * @return String[]
     */
    public String[] getSelectedItems() {
		synchronized(getTreeLock()) {
            int indexes[] = getSelectedIndexes();
            String strings[] = new String[indexes.length];
            for ( int i = 0; i < indexes.length; i++ ) {
                strings[i] = getItem(indexes[i]);
            }
            return strings;
        }
    }
}
