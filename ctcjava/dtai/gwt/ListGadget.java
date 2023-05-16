/****************************************************************
 **
 **  $Id: ListGadget.java,v 1.29 1997/08/06 23:27:06 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/ListGadget.java,v $
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

import java.awt.Color;

/**
 * A scrolling list of text items.
 * @version 1.1
 * @author DTAI, Incorporated
 */
public class ListGadget extends ScrollListGadget {

    /**
     * Creates a new scrolling list initialized with no visible Lines or
	 * multiple selections.
     */
    public ListGadget() {
        this( 0, false );
    }

    /**
     * Creates a new scrolling list initialized with the specified number of
	 * visible lines.  Multiple selections are not allowed.
     *
     * @param rows - the number of items to show.
     */
    public ListGadget(int rows) {
        this( rows, false );
    }

    /**
     * Creates a new scrolling list initialized with the specified number of
	 * visible lines and a boolean stating whether multiple selections are
	 * allowed or not.
     *
     * @param rows - the number of items to show.
     * @param multipleMode - if true then multiple selections are allowed.
     */
    public ListGadget(int rows, boolean multipleMode) {
        super( rows, multipleMode );
        viewport.setBackground( Color.white );
        add( listgrid );
    }

    /**
     * sets the background color of the gadget
     * @param color	color for background
     * @param index index of gadget whose color is set
     */
    public void setBackground(Color color, int index ) {
        ((LabelGadget)listgrid.getItemGadget(index)).setBackground(color);
    }

    /**
     * sets the foreground color of the gadget
     * @param color	color for foreground
     * @param index index of gadget whose color is set
     */
    public void setForeground(Color color, int index ) {
        ((LabelGadget)listgrid.getItemGadget(index)).setForeground(color);
    }

    /**
     * Gets the item associated with the specified index.
     *
     * @param index - the position of the item
     * @return the specified LabelGadget
     * @see getItemCount
     */
    public String getItem(int index) {
        return ((LabelGadget)listgrid.getItemGadget(index)).getLabel();
    }

    /**
     * gets the index of the specified item
     * @param item	name of gadget whose index is sought
     * @return index of specified item
     */
    public int getItemIndex(String item) {
        Gadget[] gadgets = listgrid.getGadgets();
        for ( int i = 0; i < gadgets.length; i++ ) {
            if ( ((LabelGadget)((ListGridItem)gadgets[i]).getGadget()).getLabel().equals(item) ) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Adds the specified item to the end of scrolling list.
     *
     * @param item - the item to be added
     */
    public void add(String item) {
        add(item,-1);
    }

    /**
     * Adds the specified item to the scrolling list at the specified position.
     *
     * @param item - the item to be added
     * @param index - the position at which to put in the item. The index is zero-based.
     *                 If index is -1 then the item is added to the end.
     */
    public void add(String item, int index) {
		synchronized(getTreeLock()) {
            LabelGadget label = new LabelGadget( item );
            label.setHorizAlign( LabelGadget.HORIZ_ALIGN_LEFT );
            label.setVertAlign( LabelGadget.VERT_ALIGN_TOP );
            addGadget(label,index);
        }
    }

    /**
     * Replaces the item at the given index.
     *
     * @param newValue - the new value to replace the existing item
     * @param index - the position of the item to replace
     */
    public void replaceItem(String newValue, int index) {
		synchronized(getTreeLock()) {
            ((LabelGadget)listgrid.getItemGadget(index)).setLabel( newValue );
        }
    }

    /**
     * Remove the first occurrence of item from the list.
     *
     * @param item - the item to remove from the list
     * Throws: IllegalArgumentException
     *      If the item doesn't exist in the list.
     */
    public void remove(String item) {
		synchronized(getTreeLock()) {
            Gadget gadgets[] = listgrid.getGadgets();
            for ( int i = 0; i < gadgets.length; i++ ) {
                if ( ((LabelGadget)((ListGridItem)gadgets[i]).getGadget()).getLabel().equals(item) ) {
                    remove( i );
                }
            }
            listgrid.invalidate();
        }
    }
}
