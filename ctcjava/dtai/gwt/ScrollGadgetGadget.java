/****************************************************************
 **
 **  $Id: ScrollGadgetGadget.java,v 1.18 1997/08/06 23:27:08 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/ScrollGadgetGadget.java,v $
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
import java.awt.Dimension;
import java.awt.Event;
import java.util.Vector;
import java11.awt.Adjustable;
import java11.awt.ItemSelectable;
import java11.awt.event.ActionListener;
import java11.awt.event.AdjustmentEvent;
import java11.awt.event.AWTEvent;
import java11.awt.event.InputEvent;
import java11.awt.event.ItemEvent;
import java11.awt.event.ItemListener;

/**
 * A scrolling list of text items.
 * @version 1.1
 * @author DTAI, Incorporated
 */
public class ScrollGadgetGadget extends ScrollPaneGadget implements java11.awt.ItemSelectable, ListManager {

    ItemListener itemListener;

    private int visibleIndex = 0;
    ListGridGadget listgrid;
    private int prefWidth = -1;

    /**
     * Creates a new scrolling list initialized with no visible Lines or multiple selections.
     */
    public ScrollGadgetGadget() {
        this( 0, false );
    }

    /**
     * Creates a new scrolling list initialized with the specified
	 * number of visible lines and a boolean stating whether multiple selections are allowed or not.
     *
     * @param rows - the number of items to show.
     */
    public ScrollGadgetGadget(int rows) {
        this( rows, false );
    }

    /**
     * Creates a new scrolling list prefized with the specified number
     * of visible lines and a boolean stating whether multiple selections
     * are allowed or not.
     *
     * @param rows - the number of items to show.
     * @param multipleMode - if true then multiple selections are allowed.
     */
    public ScrollGadgetGadget(int rows, boolean multipleMode) {
        viewport.setBackground( Color.white );
        setStretchingHorizontal( true );
        setStretchingVertical( true );
        setShrinkingVertical(true);
        listgrid = new ListGridGadget( rows, multipleMode );

        // it's real possible that this will need to change, considering
        // that we don't know what gadgets will be in here
        LabelGadget sample = new LabelGadget("W");
        listgrid.setSample(sample);
        listgrid.setListManager( this );
        add( listgrid );
    }

    /**
     * setChoiceList
     * @param flag	description
     */
    protected void setChoiceList(boolean flag) {
        listgrid.setChoiceList(flag);
    }

    /**
     * getGadgetIndex
     * @param gadget	description
     * @return int
     */
    public int getGadgetIndex(Gadget gadget) {
        return listgrid.getGadgetIndex(gadget);
    }

    /**
     * Gets the number of gadgets in the list.
     *
     * @see  getItem
     * @return int
     */
    public final int getListGadgetCount() {
        return listgrid.getGadgetCount();
    }

    /**
     * getListGadget
     *
     * @param row	description
     * @see getItemCount
     * @return Gadget
     */
	public Gadget getListGadget(int row) {
		return listgrid.getItemGadget( row );
	}

    /**
     * Returns the gadgets in the list.
     *
     * @see select
     * @see deselect
     * @see isIndexSelected
     * @return Gadget[]
     */
    public Gadget[] getListGadgets() {
		synchronized(getTreeLock()) {
            int count = getListGadgetCount();
            Gadget gadgets[] = new Gadget[count];
            for ( int i = 0; i < count; i++ ) {
                gadgets[i] = listgrid.getItemGadget(i);
            }
            return gadgets;
        }
    }

    /**
     * Removes all gadgets from the list.
     *
     * @see remove
     * @see delItems
     */
    public void removeAll() {
		synchronized(getTreeLock()) {
            listgrid.removeAll();
        }
    }

    /**
     * Removes an gadget from the list.
     * @param position	description
     */
    public void remove(int position) {
		synchronized(getTreeLock()) {
            listgrid.remove(position);
        }
    }

	/**
	 * getIgnoreDefaultAction
	 * @return boolean
	 */
	public boolean getIgnoreDefaultAction() {
		return listgrid.getIgnoreDefaultAction();
	}

	/**
	 * setIgnoreDefaultAction
	 * @param ignoreDefaultAction	description
	 */
	public void setIgnoreDefaultAction( boolean ignoreDefaultAction ) {
		listgrid.setIgnoreDefaultAction(ignoreDefaultAction);
	}

    /**
     * Get the selected gadget on the list or -1 if no gadget is selected.
     *
     * @see select
     * @see deselect
     * @see isIndexSelected
     * @return int
     */
    public final int getSelectedIndex() {
		synchronized(getTreeLock()) {
            return listgrid.getSelectedIndex();
        }
    }

    /**
     * Gets the selected indexes on the list.
     *
     * @see select
     * @see deselect
     * @see isIndexSelected
     * @return int[]
     */
    public final int[] getSelectedIndexes() {
		synchronized(getTreeLock()) {
            return listgrid.getSelectedIndexes();
        }
    }

    /**
     * getNumSelected
     * @return numSelected
     */
    public final int getNumSelected() {
        return listgrid.getNumSelected();
    }

    /**
     * Gets the selected gadget on the list or null if no item is selected.
     *
     * @see select
     * @see deselect
     * @see isIndexSelected
     * @return Gadget
     */
    public Gadget getSelectedGadget() {
		synchronized(getTreeLock()) {
            return listgrid.getSelectedGadget();
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
                strings[i] = listgrid.getItemGadget(indexes[i]).toString();
            }
            return strings;
        }
    }

    /**
     * Selects the item at the specified index.
     *
     * @param index - the position of the item to select
     * @see getSelectedItem
     * @see deselect
     * @see isIndexSelected
     */
    public void select(int index) {
		synchronized(getTreeLock()) {
		    int count = getListGadgetCount();
		    if ( count > 0 ) {
    		    if ( index >= count ) {
    		        index = count-1;
    		    }
                listgrid.select( index );
            }
        }
    }

    /**
     * Deselects the item at the specified index.
     *
     * @param index - the position of the item to deselect
     * @see select
     * @see getSelectedItem
     * @see isIndexSelected
     */
    public void deselect(int index) {
		synchronized(getTreeLock()) {
            listgrid.deselect( index );
        }
    }

    /**
     * Returns true if the item at the specified index has been selected; false otherwise.
     *
     * @param index - the item to be checked
     * @see select
     * @see deselect
     * @return boolean
     */
    public boolean isIndexSelected(int index) {
        return listgrid.isIndexSelected( index );
    }

    /**
     * Returns the number of visible rows in this list.
     * @return int
     */
    public int getRows() {
        return listgrid.getNumVisible();
    }

    /**
     * Returns true if this list allows multiple selections.
     *
     * @see setMultipleMode
     * @return boolean
     */
    public boolean isMultipleMode() {
        return listgrid.isMultipleMode();
    }

    /**
     * Sets whether this list should allow multiple selections or not.
     *
     * @param b - the boolean to allow multiple selections
     * @see isMultipleMode
     */
    public void setMultipleMode(boolean b) {
		synchronized(getTreeLock()) {
            listgrid.setMultipleMode( b );
        }
    }

    /**
     * Gets the index of the item that was last made visible by the method makeVisible.
     * @return int
     */
    public int getVisibleIndex() {
        return visibleIndex;
    }

    /**
     * Gets the index of the top item.
     * @return int
     */
    public int getTopIndex() {
        return listgrid.getTopIndex();
    }

    /**
     * Forces the item at the specified index to be visible.
     *
     * @param index - the position of the item
     * @see getVisibleIndex
     */
    public void makeVisible(int index) {
		synchronized(getTreeLock()) {
            visibleIndex = index;
            listgrid.makeVisible( index );
        }
    }


    /**
     * Adds the specified gadget to the end of scrolling list.
     *
     * @param gadget - the gadget to be added
     */
    public void addGadget(Gadget gadget) {
        addGadget(gadget,-1);
    }

    /**
     * Adds the specified item to the scrolling list at the specified position.
     *
     * @param gadget - the item to be added
     * @param index - the position at which to put in the item. The index is zero-based.
     *                If index is -1 then the item is added to the end.
     */
	public void addGadget(Gadget gadget, int index) {
		synchronized(getTreeLock()) {
			listgrid.add( gadget, index );
			listgrid.invalidate();
		}
	}

    /**
     * Replaces the item at the given index.
     *
     * @param newValue - the new value to replace the existing item
     * @param index - the position of the item to replace
     */
	public void replaceGadget(Gadget newValue, int index) {
		synchronized(getTreeLock()) {
			listgrid.replace( newValue, index );
			listgrid.invalidate();
		}
	}

    /**
     * Remove the first occurrence of item from the list.
     * Throws: IllegalArgumentException
     *      If the item doesn't exist in the list.
     *
     * @param gadget - the gadget to remove from the list
     */
    public void removeGadget(Gadget gadget) {
		synchronized(getTreeLock()) {
            Gadget gadgets[] = listgrid.getGadgets();
            for ( int i = 0; i < gadgets.length; i++ ) {
                if ( gadgets[i].equals( gadget ) ) {
                    remove( i );
                }
            }
            listgrid.invalidate();
        }
    }

    /**
     * Returns the preferred dimensions needed for the list with the specified amount of rows.
     *
     * @param rows - amount of rows in list.
     * @return Dimension
     */
    public Dimension getPreferredSize(int rows) {
	    Dimension pref = getOuterPreferredSize();
        Dimension scrolledSize = listgrid.getPreferredSize( rows );
        pref.width += scrolledSize.width;
        pref.height += scrolledSize.height;
        return pref;
    }

    /**
     * Returns the minimum dimensions needed for the amount of rows in the list.
     *
     * @param rows - minimum amount of rows in the list
     * @return Dimension
     */
    public Dimension getMinimumSize(int rows) {
        return getMinimumSize();
    }

    /**
     * getPreferredSize
     * @return Dimension
     */
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        if ( prefWidth >= 0 ) {
            size.width = prefWidth;
        }
        return size;
    }

    /**
     * getScrolledPreferredSize
     * @return Dimension
     */
    public Dimension getScrolledPreferredSize() {
        return listgrid.getPreferredSize(listgrid.getPrefRows());
    }

    /**
     * setPrefWidth
     * @param prefWidth	description
     */
    public void setPrefWidth(int prefWidth) {
        this.prefWidth = prefWidth;
    }

    /**
     * doLayout
     */
    public void doLayout() {
        super.doLayout();
        if ( ! listgrid.isValid() ) {
            valid = false;
            return;
        }
        if ( vScrollbar.isVisible() ) {
            int numVisible = listgrid.getNumVisible();
            int numGadgets = listgrid.getGadgetCount();
            int topIndex = listgrid.getTopIndex();
            if ( numGadgets > numVisible ) {
                vScrollbar.setMaximum( numGadgets - listgrid.getNumVisible() );
                vScrollbar.setVisibleAmount( numVisible );
                vScrollbar.setBlockIncrement( numVisible - 1 );
                vScrollbar.setUnitIncrement( 1 );
            }
        }
    }

    /**
     * adjustmentValueChanged
     * @param e	description
     */
    public void adjustmentValueChanged(AdjustmentEvent e) {
        if ( e.getAdjustable() == getHAdjustable() ) {
            listgrid.setLocation( -hScrollbar.getValue(), listgrid.y );
        }
        else {
            listgrid.setTopIndex( vScrollbar.getValue() );
        }
    }

    /**
     * Adds the specified action listener to receive action
	 * events from this list. Action events occur when a list item is double-clicked.
     *
     * @param l - the action listener
     */
    public void addActionListener(ActionListener l) {
        listgrid.addActionListener(l);
    }

    /**
     * Removes the specified action listener so it no longer
	 * receives action events from this list.
     *
     * @param l - the action listener
     */
    public void removeActionListener(ActionListener l) {
        listgrid.removeActionListener(l);
    }

	/**
	 * Adds the specified listener to be notified when component
	 * events occur on this component.
	 *
	 * @param l 	the listener to receive the events
	 */
	public synchronized void addItemListener(ItemListener l) {
        itemListener = GWTEventMulticaster.add(itemListener, l);
	}

	/**
	 * Removes the specified listener so it no longer receives
	 * item events on this item.
	 *
	 * @param l 		the listener to remove
	 */
	public synchronized void removeItemListener(ItemListener l) {
        itemListener = GWTEventMulticaster.remove(itemListener, l);
	}

	/**
	 * processEvent
	 *
	 * @param e		a ItemEvent
	 * @return boolean result
	 */
	protected void processEvent(AWTEvent e) {
		if (e instanceof ItemEvent) {
		    processItemEvent((ItemEvent)e);
		} else {
		    super.processEvent(e);
		}
	}

	protected void processItemEvent(ItemEvent e) {
		if (itemListener != null) {
    		if(e.getID() == ItemEvent.ITEM_STATE_CHANGED) {
    			itemListener.itemStateChanged(e);
    		}
    	}
	}

	/**
	 * selectionChanged
	 * @param event	description
	 * @param selected	description
	 */
	public void selectionChanged( AWTEvent event, boolean selected ) {
	    Event evt;
	    if ( event != null ) {
            evt = event.getEvent();
        }
        else {
            evt = new Event( this, -1, null );
        }
        int stateChange;
        if ( selected ) {
            stateChange = ItemEvent.SELECTED;
        }
        else {
            stateChange = ItemEvent.DESELECTED;
        }
		processEvent( new ItemEvent( this, evt,
		                             ItemEvent.ITEM_STATE_CHANGED,
		                             this, stateChange ) );
	}

	/**
	 * moveLeft
	 * @param event	description
	 */
	public void moveLeft( InputEvent event ) {
        java11.awt.Adjustable horiz = getHAdjustable();
        horiz.setValue( horiz.getValue() - horiz.getUnitIncrement() );
    }

	/**
	 * moveRight
	 * @param event	description
	 */
	public void moveRight( InputEvent event ) {
        java11.awt.Adjustable horiz = getHAdjustable();
        horiz.setValue( horiz.getValue() + horiz.getUnitIncrement() );
	}

	/**
	 * topChanged
	 * @param topIndex	description
	 */
	public void topChanged( int topIndex ) {
	    getVAdjustable().setValue( topIndex );
	}

	/**
	 * setFocusTraversable
	 * @param in	description
     */
	public void setFocusTraversable(boolean in) {
	    if (listgrid != null) {
	        listgrid.setFocusTraversable(in);
	    }
	}

}
