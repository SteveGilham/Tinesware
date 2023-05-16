/****************************************************************
 **
 **  $Id: ListGridGadget.java,v 1.59 1997/09/03 02:47:38 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/ListGridGadget.java,v $
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
import java.awt.Rectangle;
import java.util.Vector;
import java11.awt.event.ActionEvent;
import java11.awt.event.ActionListener;
import java11.awt.event.AWTEvent;
import java11.awt.event.FocusEvent;
import java11.awt.event.InputEvent;
import java11.awt.event.KeyEvent;
import java11.awt.event.MouseEvent;

/**
 * A scrolling list of text items.
 * @version 1.1
 * @author DTAI, Incorporated
 */
public class ListGridGadget extends BorderGadget implements ListManager, Runnable {

    private final static int NONE = 0;
    private final static int UP = 1;
    private final static int DOWN = 2;

    ActionListener actionListener;

    private Gadget sample;

    private boolean multipleMode;
    private int numSelected = 0;

    private Gadget startSelectionGadget;
    Gadget focusGadget;

    private ListManager listManager;

    private int topIndex = 0;
    private int bottomIndex = -1;
    private int numVisible = 0;
    private int prefRows = 0;

    private Thread thread;
    private int dragScrollDirection = NONE;
    private MouseEvent curDragEvent;

    private Gadget lastDragGadget;

	private boolean choiceList = false;
    private boolean allowFocus = true;
    private boolean ignoreDefaultAction = false;
    private boolean ignoreEvents = false;
    private BorderGadget itemBorder;
    private boolean bottomPartiallyHidden = false;

    /**
     * Creates a new scrolling list initialized with no visible Lines
	 * and no multiple selections allowed.
     */
    public ListGridGadget() {
        this( 0, false );
    }

    /**
     * Creates a new scrolling list initialized with
	 * the given number of visible lines, and no multiple
	 * selections allowed.
     *
     * @param rows	number of initial visible lines
     */
    public ListGridGadget(int rows) {
        this( rows, false );
    }

    /**
     * ListGridGadget
     * @param rows	number of initial visible lines
     * @param multipleMode - if true then multiple selections are allowed.
     */
    public ListGridGadget(int rows, boolean multipleMode) {
        prefRows = rows;
        this.multipleMode = multipleMode;
        listManager = this;
        setLayout( null );
		itemBorder = new BorderGadget();
        itemBorder.setMargins(1);//for item focus
		setMargins(1);//for focus when no items
    }

	/**
     * returns true if children can overlap each other.
     * @return false
	 */
	public boolean childrenCanOverlap() {
	    return false;
	}

    /**
     * getItemBorder
     * @return itemBorder
     */
    public BorderGadget getItemBorder() {
        return itemBorder;
    }

    /**
     * run
     */
    public void run() {
        Thread thisThread = Thread.currentThread();

        while ( thread == thisThread ) {
            Gadget gadget = null;
            if ( ( dragScrollDirection == UP ) &&
                 ( topIndex > 0 ) ) {
                gadget = gadgets[topIndex-1];
            }
            else if ( ( dragScrollDirection == DOWN ) &&
                      ( ( topIndex + numVisible ) < gadgetCount ) ) {
                gadget = gadgets[topIndex+numVisible];
            }
            if ( ( gadget != null ) &&
                 ( gadget != focusGadget ) ) {
                select( gadget, curDragEvent, true, curDragEvent.isControlDown() );
            }
            try {
                thread.sleep( 100 );
            }
            catch ( InterruptedException ie ) {
            }
        }
    }

	/**
	 * getIgnoreDefaultAction
	 * @return ignoreDefaultAction
	 */
	public boolean getIgnoreDefaultAction() {
		return ignoreDefaultAction;
	}

	/**
	 * setIgnoreDefaultAction
	 * @param ignoreDefaultAction new value for ignoreDefaultAction
	 */
	public void setIgnoreDefaultAction( boolean ignoreDefaultAction ) {
	    this.ignoreDefaultAction = ignoreDefaultAction;
	}

    /**
     * setSample
     * @param sample new value for sample
     */
    public void setSample( Gadget sample ) {
        this.sample = sample;
        sample.setVisible(false);
        sample.setParent(this); // not really, but used to calc inherited font
    }

    /**
     * setFocusTraversable
     * @param b	new value for allowFocus
     */
    public void setFocusTraversable(boolean b) {
        allowFocus = b;
    }

	/**
	 * isFocusTraversable
	 * @return is/is not Focus Traversable
	 */
	public boolean isFocusTraversable() {
		return allowFocus && ( ! ignoreEvents );
	}

    /**
     * setIgnoreEvents
     * @param b	new value for ignoreEvents
     */
    public void setIgnoreEvents(boolean b) {
        ignoreEvents = b;
    }

	/**
	 * getIgnoreEvents
	 * @return ignoreEvents
	 */
	public boolean getIgnoreEvents() {
		return ignoreEvents;
	}

    /**
     * setListManager
     * @param listManager	new value for listManager
     */
    public void setListManager( ListManager listManager ) {
        this.listManager = listManager;
    }

    /**
     * doLayout
     */
    public void doLayout() {
        synchronized(getTreeLock()) {
            if ( bottomIndex >= 0 ) {
                setBottomIndex(bottomIndex);
            }
            bottomPartiallyHidden = false;
            int childX = 0;
            int childY = 0;
            int childWidth = width;
            int bottom = height;

            int oldNumVisible = numVisible;
            numVisible = 0;
            boolean belowBottom = false;
            for ( int i = 0; i < gadgetCount; i++ ) {
                Gadget child = gadgets[i];
                if ( ( i < topIndex ) ||
                     ( belowBottom ) ) {
                    child.setVisible( false, false );
                }
                else {
                    int childHeight = child.pref_height;
                    if ( ( childHeight < 0 ) || ( ! child.isValid() ) ) {
                        childHeight = child.getPreferredSize().height;
                    }
                    if ( childY >= bottom ) {
                        belowBottom = true;
                        child.setVisible( false, false );
                    }
                    else {
                        if ( childY + childHeight > bottom ) {
                            bottomPartiallyHidden = true;
                        }
                        child.setBounds( childX, childY, childWidth, childHeight, false );
                        child.setVisible( true, false );
                        addPaintIdx(i);
                        numVisible++;
                        childY += childHeight;
                    }
                }
            }
            if ( numVisible != oldNumVisible ) {
                if ( parent != null ) {
                    parent.invalidate();
                }
            }
        }
    }

    /**
     * getNumSelected
     * @return numSelected
     */
    public final int getNumSelected() {
        return numSelected;
    }

    /**
     * getNumVisible
     * @return numVisible, unless the bottom is parially hidden,
	 *		in which case returns numVisible - 1.
     */
    public final int getNumVisible() {
        if ( bottomPartiallyHidden ) {
            return numVisible-1;
        }
        return numVisible;
    }

    /**
     * getNumPartlyVisible
     * @return  numVisible
     */
    public final int getNumPartlyVisible() {
        return numVisible;
    }

    /**
     * getTopIndex
     * @return topIndex
     */
    public final int getTopIndex() {
        return topIndex;
    }

    /**
     * setTopIndex
     * @param topIndex	new value for topIndex
     */
    public void setTopIndex( int topIndex ) {
        bottomIndex = -1;
        if ( topIndex != this.topIndex ) {
            this.topIndex = topIndex;
            invalidate(false);
            listManager.topChanged( topIndex );
        }
    }

    /**
     * setBottomIndex
     * @param bottomIndex	new value for bottomIndex
     */
    public final void setBottomIndex( int bottomIndex ) {
        if ( isValid() ) {
            if (gadgetCount > bottomIndex) {
                int remHeight = height;
                int newTopIndex = topIndex;
                for ( int i = bottomIndex; i >= 0; i-- ) {
                    int childHeight = gadgets[i].pref_height;
                    if ( childHeight < 0 ) {
                        childHeight = gadgets[i].getPreferredSize().height;
                    }
                    remHeight -= childHeight;
                    if ( remHeight >= 0 ) {
                        newTopIndex = i;
                    }
                    else {
                        break;
                    }
                }
                setTopIndex( newTopIndex );
            }
            else {
                bottomIndex = gadgetCount-1;
            }
        }
        this.bottomIndex = bottomIndex;
    }

    /**
     * gets the preffered size
     * @param rows	item in list in question
     * @return Dimension
     */
    public Dimension getPreferredSize( int rows ) {
        synchronized(getTreeLock()) {

            int prefHeight = 0;
            int maxWidth = 0;
            int count = gadgetCount;
            if ( ( rows > 0 ) && ( rows < gadgetCount ) ) {
                count = rows;
            }

            int lastHeight = 0;
            int firstChildHeight = 0;
            for ( int i = 0; i < gadgetCount; i++ ) {
                int childWidth = gadgets[i].pref_width;
                int childHeight = gadgets[i].pref_height;
                if ( ! gadgets[i].valid || ( childWidth < 0 ) || ( childHeight < 0 ) ) {
                    Dimension pref = gadgets[i].getPreferredSize();
                    childWidth = pref.width;
                    childHeight = pref.height;
                }
                if ( i == 0 ) {
                    firstChildHeight = childHeight;
                }
                if ( i < count ) {
                    prefHeight += childHeight;
                }
                maxWidth = Math.max( maxWidth, childWidth );
            }
            if ( gadgetCount < rows ) {
                if ( gadgetCount > 0 ) {
                    prefHeight += ( rows - gadgetCount ) * firstChildHeight;
                }
                else if ( sample != null ) {
                    if ( sample.pref_height < 0 ) {
                        prefHeight = rows * sample.getPreferredSize().height;
                    }
                    else {
                        prefHeight = rows * sample.pref_height;
                    }
                }
            }
            return new Dimension( maxWidth, prefHeight );
        }
    }

    /**
     * gets the preferred size
     * @return Dimension
     */
    public Dimension getPreferredSize() {
	    if ( valid && ( pref_width >= 0 ) && ( pref_height >= 0 ) ) {
	        return new Dimension( pref_width, pref_height );
	    }
        Dimension pref = getPreferredSize( Math.max(gadgetCount,prefRows) );
        return pref;
    }

    /**
     * requiresVertScrollbar
     * @return does/does not require vertical scroll bar
     */
    protected boolean requiresVertScrollbar() {
        if ( gadgetCount > getNumVisible() ) {
            return true;
        }
        return false;
    }

    /**
     * gets the minimum size
     * @param rows	item in list in question
     * @return Dimension
     */
    public Dimension getMinimumSize( int rows ) {
        int topHeight = 0;
        if ( gadgetCount > 0 ) {
            if ( gadgets[topIndex].pref_height < 0 ) {
                topHeight = gadgets[topIndex].getPreferredSize().height;
            }
            else {
                topHeight = gadgets[topIndex].pref_height;
            }
        }
        else if ( sample != null ) {
            if ( sample.pref_height < 0 ) {
                topHeight = sample.getPreferredSize().height;
            }
            else {
                topHeight = sample.pref_height;
            }
        }
        return new Dimension( 0, topHeight );
    }

    /**
     * gets the minimum size
     * @return Dimension
     */
    public Dimension getMinimumSize() {
        return getMinimumSize( prefRows );
    }

    /**
     * getPrefRows
     * @return prefRows
     */
    public int getPrefRows() {
        return prefRows;
    }

    /**
     * getGridItem
     * @param i	index of Gadget to get
     * @return Gadget at index i
     */
    public final Gadget getGridItem(int i) {
        return ((ListGridItem)getGadget(i));
    }

    /**
     * getItemGadget
     * @param i	index of Gadget to get
     * @return Gadget at index i
     */
    public final Gadget getItemGadget(int i) {
        return ((ListGridItem)getGadget(i)).getGadget();
    }

    /**
     * getGadgetIndex
     * @param gadget Gadget whose index is sought
     * @return index of specified gadget
     */
    public int getGadgetIndex(Gadget gadget) {
        if ( gadget.parent == this ) {
            return super.getGadgetIndex(gadget);
        }
        else {
            return super.getGadgetIndex(gadget.parent);
        }
    }

    /**
     * adds a gadget to the list
     * @param gadget	Gadget to be added
     * @param i			index of Gadget being added
     * @return Gadget being added
     */
    public Gadget add( Gadget gadget, int i ) {
        Gadget rtn;
        if ( gadget instanceof ListGridItem ) {
            rtn = super.add(gadget,i);
        }
        else {
            rtn = super.add(new ListGridItem(this,gadget,itemBorder),i);
        }
        invalidate();
        return rtn;
    }

    /**
     * replace
     * @param gadget	Gadget to be replaced
     * @param i			index of Gadget being replaced
     * @return Gadget being replaced
     */
    public Gadget replace( Gadget gadget, int i ) {
        if ( gadget.parent == this ) {
            super.replace(gadget,i);
        }
        else {
            ListGridItem gridItem = (ListGridItem)getGadget(i);
            gridItem.replace(gadget,0);
        }
        invalidate();
        return gadget;
    }

    /**
     * removes a gadget from the list
     * @param gadget	Gadget to be removed
     */
    public void remove( Gadget gadget ) {
        if (gadget.isSelected()) {
            numSelected--;
        }
        if ( gadget.parent == this ) {
            super.remove(gadget);
        }
        else {
            super.remove(gadget.parent);
        }
    }

    /**
     * removes a gadget from the list
     * @param gadget	Gadget to be removed
     * @param i			index of gadget being removed`
     */
    protected void remove( Gadget gadget, int i ) {
        if (gadget.isSelected()) {
            numSelected--;
        }
        if ( gadget.parent == this ) {
            super.remove( gadget, i );
            if ( topIndex == i ) {
                if ( ( topIndex == ( gadgetCount - 1 ) ) &&
                     ( topIndex > 0 ) ) {
                    topIndex--;
                }
            }
            invalidate();
        }
        else {
            super.remove(gadget.parent,i);
        }
    }

    /**
     * removeAll
     */
    public void removeAll() {
		synchronized(getTreeLock()) {
            int count = gadgetCount;
            for (int i=0; i<count; i++) {
                remove(0);
            }
            setTopIndex(0);
            numSelected = 0;
        }
     }

    /**
     * Get the selected item on the list or -1 if no item is selected.
     *
     * @return index of selected item, or -1 if none selected
     * @see select, deselect, isIndexSelected
     */
    public int getSelectedIndex() {
		synchronized(getTreeLock()) {
            for ( int i = 0; i < gadgetCount; i++ ) {
                if (gadgets[i].isSelected()) {
                    return i;
                }
            }
            return -1;
        }
    }

    /**
     * Returns the selected indexes on the list.
     *
     * @return array of indexes of selected items
     * @see select, deselect, isIndexSelected
     */
    public int[] getSelectedIndexes() {
		synchronized(getTreeLock()) {
            int[] idxs = new int[numSelected];
            int cnt = 0;
            for ( int i = 0; i < gadgetCount; i++ ) {
                if ( gadgets[i].isSelected() ) {
                    idxs[cnt++] = i;
                }
            }
            return idxs;
        }
    }

    /**
     * Returns the selected item on the list or null if no item is selected.
     *
     * @return selected Gadget, or null if none selected
     * @see select, deselect, isIndexSelected
     */
    public Gadget getSelectedGadget() {
		synchronized(getTreeLock()) {
            for ( int i = 0; i < gadgetCount; i++ ) {
                if ( gadgets[i].isSelected() ) {
                    return gadgets[i];
                }
            }
            return null;
        }
    }

    /**
     * Returns the selected items on the list.
     *
     * @return array of selected Gadgets
     * @see select, deselect, isIndexSelected
     */
    public Gadget[] getSelectedGadgets() {
		synchronized(getTreeLock()) {
            Gadget[] selected = new Gadget[numSelected];
            int cnt = 0;
            for ( int i = 0; i < gadgetCount; i++ ) {
                if ( gadgets[i].isSelected() ) {
                    selected[cnt++] = gadgets[i];
                }
            }
            return selected;
        }
    }

    /**
     * makeVisible
     * @param index	index of gadget to make visible
     */
    public void makeVisible(int index) {
		synchronized(getTreeLock()) {
            if ( index < topIndex ) {
                setTopIndex( index );
            }
            else if ( ( index >= ( topIndex + numVisible ) ) ||
                      ( ( index == ( topIndex + numVisible - 1 ) ) &&
                        bottomPartiallyHidden ) ) {
                setBottomIndex( index );
            }
        }
    }

    /**
     * Selects the gadget
     *
     * @param gadget - the gadget to select
     * @see getSelectedItem
     * @see deselect
     * @see isIndexSelected
     */
    public void select(Gadget gadget) {
        lastDragGadget = null;
        select( gadget, null );
    }

    /**
     * select
     *
     * @param gadget - the gadget to select
     * @param event - the InputEvent to key on
     */
    public void select(Gadget gadget, InputEvent event) {
        if ( event != null ) {
            select( gadget, event, event.isShiftDown(), event.isControlDown() );
        }
        else {
            select( gadget, event, false, true );
        }
    }

    /**
     * select
     * @param gadget the gadget to select
     * @param event  the InputEvent to key on
     * @param shift	 shift key is/is not down
     * @param control control key is/is not down
     */
    public void select(Gadget gadget, InputEvent event, boolean shift, boolean control) {
        if ( ! ignoreEvents ) {
            setFocusGadget( gadget );
        }
        if ( doSelect( gadget, shift, control ) ) {
            listManager.selectionChanged( event, true );
        }
        makeVisible( getGadgetIndex(gadget) );
    }

    /**
     * setFocusGadget
     * @param gadget	new value for focusGadget
     */
    public void setFocusGadget( Gadget gadget ) {
        if ( focusGadget != gadget ) {
            Gadget was = focusGadget;
            focusGadget = gadget;
            if ( focusGadget != null ) {
                focusGadget.repaint();
            }
            if ( was != null ) {
                was.repaint();
            }
        }
    }

	/**
	 * processFocusEvent
	 * @param e		the firing FocusEvent
	 */
	protected void processFocusEvent(FocusEvent e) {
		synchronized(getTreeLock()) {
		    if ( gadgetCount == 0 ) {
		        repaint();
		    }
		    else {
		        if (e.getID() == FocusEvent.FOCUS_GAINED) {
            	    if ( focusGadget == null ) {
            	        setFocusGadget( gadgets[topIndex] );
            	    }
            	    if ( focusGadget != null ) {
                        focusGadget.repaint();
                    }
                } else {
            	    if ( focusGadget != null ) {
                        focusGadget.repaint();
                    }
                }
            }
    	}
	    super.processFocusEvent( e );
	}

    private boolean doSelect(Gadget select, boolean shift, boolean control) {
		synchronized(getTreeLock()) {
            boolean change = false;
            if ( ( ! multipleMode ) ||
                 ( ! shift ) ||
                 ( startSelectionGadget == null ) ) {
                startSelectionGadget = select;
            }

            boolean selecting = false;

            for ( int i = 0; i < gadgetCount; i++ ) {

                Gadget gadget = gadgets[i];

    			if ( ( gadget == select ) ||
    				 ( gadget == startSelectionGadget ) ) {
    				if ( ! gadget.isSelected() ) {
    					gadget.setSelected( true );
    					numSelected++;
    					change = true;
    				}
    				if ( select != startSelectionGadget ) {
    					selecting = ! selecting;
    				}
    			}
    			else if ( selecting ) {
    				if ( ! gadget.isSelected() ) {
    					gadget.setSelected( true );
    					numSelected++;
    					change = true;
    				}
    			}
    			else if ( ( ! control ) || ( ! multipleMode ) ) {
    				if ( gadget.isSelected() ) {
    					gadget.setSelected( false );
    					numSelected--;
    					change = true;
    				}
    			}
    		}

            return change;
        }
    }

    private boolean doSelectAll() {
		synchronized(getTreeLock()) {
            boolean change = false;
            for ( int i = 0; i < gadgetCount; i++ ) {
                Gadget gadget = gadgets[i];
                if ( ! gadget.isSelected() ) {
                    gadget.setSelected( true );
                    change = true;
                }
            }
            numSelected = gadgetCount;
            startSelectionGadget = null;
            return change;
        }
    }

    /**
     * selectAll
     */
    public void selectAll() {
        if ( multipleMode ) {
            if ( doSelectAll() ) {
                listManager.selectionChanged( null, true );
            }
        }
    }

    private boolean doDeselectAll() {
		synchronized(getTreeLock()) {
            boolean change = false;
            for ( int i = 0; i < gadgetCount; i++ ) {
                Gadget gadget = gadgets[i];
                if ( gadget.isSelected() ) {
                    gadget.setSelected( false );
                    change = true;
                }
            }
            startSelectionGadget = null;
            numSelected = 0;
            return change;
        }
    }

    /**
     * deselectAll
     */
    public void deselectAll() {
        if ( doDeselectAll() ) {
            listManager.selectionChanged( null, false );
        }
    }

    /**
     * Deselects the item
     *
     * @param gadget	Gadget to de-select
     */
    public void deselect(Gadget gadget) {
        deselect( gadget, null );
    }

    /**
     * deselect
     * @param gadget	Gadget to de-select
     * @param event		InputEvent to key on
     */
    public void deselect(Gadget gadget, InputEvent event) {
        if ( doDeselect( gadget ) ) {
            listManager.selectionChanged( event, false );
        }
    }

    /**
     * doDeselect
     * @param gadget	Gadget to de-select
     * @return boolean result
     */
    public boolean doDeselect(Gadget gadget) {
		synchronized(getTreeLock()) {
            startSelectionGadget = null;
            if ( gadget.isSelected() ) {
                gadget.setSelected( false );
                numSelected--;
                return true;
            }
            return false;
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
        select( gadgets[index] );
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
        deselect( gadgets[index] );
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
        return gadgets[index].isSelected();
    }

    /**
     * Returns true if this list allows multiple selections.
     *
     * @see setMultipleMode
     * @return multipleMode
     */
    public boolean isMultipleMode() {
        return multipleMode;
    }

    /**
     * Sets whether this list should allow multiple selections or not.
     *
     * @param b - the boolean to allow multiple selections
     * @see isMultipleMode
     */
    public void setMultipleMode(boolean b) {
		synchronized(getTreeLock()) {
            if ( b != multipleMode ) {
                multipleMode = b;
                if ( ! multipleMode ) {
                    int idx = getSelectedIndex();
                    if ( idx >= 0 ) {
                        select( idx );
                    }
                }
            }
        }
    }

	/**
	 * Adds the specified listener to be notified when component
	 * events occur on this component.
	 *
	 * @param l 	the listener to receive the events
	 */
	public synchronized void addActionListener(ActionListener l) {
        actionListener = GWTEventMulticaster.add(actionListener, l);
	}

	/**
	 * Removes the specified listener so it no longer receives
	 * action events on this action.
	 *
	 * @param l 		the listener to remove
	 */
	public synchronized void removeActionListener(ActionListener l) {
        actionListener = GWTEventMulticaster.remove(actionListener, l);
	}

	/**
	 * processEvent
	 *
	 * @param e		a ActionEvent
	 * @return boolean result
	 */
	protected void processEvent(AWTEvent e) {
		if (e instanceof ActionEvent) {
		    processActionEvent((ActionEvent)e);
		} else {
		    super.processEvent(e);
		}
	}

	protected void processActionEvent(ActionEvent e) {
	    if (actionListener != null) {
    		if(e.getID() == ActionEvent.ACTION_PERFORMED) {
    			actionListener.actionPerformed(e);
    		}
    	}
	}

    private synchronized void stopThread() {
        if ( thread != null ) {
            thread = null;
        }
    }

    /**
     * processMouseEvent
     * @param mouse the MouseEvent
     */
    public void processMouseEvent( MouseEvent mouse ) {
        if (ignoreEvents) {
            return;
        }
        if (mouse.getID() == MouseEvent.MOUSE_PRESSED) {
      		triggerSelection(mouse);
      	} else if (mouse.getID() == MouseEvent.MOUSE_RELEASED) {
    		if (choiceList) {
    			triggerAction(mouse);
    		}
    		stopThread();
      	}
        super.processMouseEvent( mouse );
    }

    /**
     * processMouseMotionEvent
     * @param mouse the MouseEvent
     */
    public void processMouseMotionEvent( MouseEvent e ) {
        if (ignoreEvents) {
            return;
        }
        if (e.getID() == MouseEvent.MOUSE_MOVED) {
    		if (choiceList) {
    			selectUnderMouse((MouseEvent)e);
    		}
      	} else if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
    		selectUnderMouse(e);
      	}
        super.processMouseMotionEvent( e );
    }

    /**
     * getMouseItem
     * @param mouse the MouseEvent
     * @return Gadget
     */
    private Gadget getMousedItem(MouseEvent mouse) {
        Gadget gadget = getGadgetAt( 1, mouse.getY() );
        if ( gadget == this ) {
            return null;
        }
        while ( ( gadget != null ) &&
                ( gadget.parent != this ) ) {
            gadget = gadget.parent;
        }
        return gadget;
    }

	/**
	 * invokeAction
	 * @param e the AWTEvent
	 */
    public void invokeAction(AWTEvent e) {
        ActionEvent action = new ActionEvent( listManager, e.getEvent(),
		                               ActionEvent.ACTION_PERFORMED );
		processEvent( action );
		if ( ! action.isConsumed() &&
	         ( ! ignoreDefaultAction ) ) {
	        GadgetShell shell = getShell();
	        if ( shell != null ) {
	            Gadget defaultGadget = shell.getDefaultGadget();
	            if ( defaultGadget != null ) {
	                defaultGadget.doDefaultAction(e);
	            }
	        }
	    }
	}

	/**
	 * triggerSelection
	 * @param mouse	the MouseEvent
	 */
	private void triggerSelection(MouseEvent mouse) {
	    Gadget gadget = getMousedItem(mouse);
        if ( gadget == null ) {
            return;
        }
        if ( mouse.getClickCount() == 2 ) {
            invokeAction(mouse);
        }
        else {
            if ( mouse.isControlDown() && gadget.isSelected() ) {
                deselect( gadget, mouse );
            }
            else {
                select( gadget, mouse );
            }
        }
	}

	/**
	 * triggerAction
	 * @param mouse	the MouseEvent
	 */
	private void triggerAction(MouseEvent mouse) {
	    if ( getMousedItem(mouse) != null ) {
	        invokeAction(mouse);
    	}
	}

	/**
	 * selectUnderMouse
	 * @param mouse	the MouseEvent
	 */
	private void selectUnderMouse(MouseEvent mouse) {

		if ( startSelectionGadget != null ) {
            if ( gadgetCount > 0 ) {
                curDragEvent = mouse;
                int y = mouse.getY();
                Gadget gadget = null;
                dragScrollDirection = NONE;
                if ( ( y < 0 ) && ( topIndex > 0 ) ) {
                    dragScrollDirection = UP;
                }
                else if ( ( y > ( height - 1 ) ) &&
                          ( ( topIndex + numVisible ) < gadgetCount ) ) {
                    dragScrollDirection = DOWN;
                }
                else {
            	    gadget = getMousedItem(mouse);
                }
                if ( dragScrollDirection != NONE ) {
                    if ( thread == null ) {
                        thread = new Thread(this, "dtai.gwt.ListGridGadget");
                        thread.start();
                    }
                }
                else {
                    stopThread();
                    if ( getGadgetAt( 1, mouse.getY() ) == this ) {
                        if ( topIndex+numVisible >= 1 ) {
                            if ( mouse.getY() > gadgets[topIndex+numVisible-1].y ) {
                                gadget = gadgets[topIndex+numVisible-1];
                            }
                        }
                    }
                    if ( ( gadget != null ) &&
                         ( gadget != lastDragGadget ) ) {
                        select( gadget, mouse, true, mouse.isControlDown() );
                    }
                    lastDragGadget = gadget;
                }
            }
        }

	}

	/**
	 * moveUp
	 * @param event	the InputEvent
	 */
	public void moveUp( InputEvent event ) {
	    int lastidx = -1;
	    if ( lastDragGadget != null ) {
	        lastidx = getGadgetIndex( lastDragGadget );
	    }
	    else if ( startSelectionGadget != null ) {
	        lastidx = getGadgetIndex( startSelectionGadget );
	    }
	    Gadget gadget = null;
	    if ( lastidx > 0 ) {
	        gadget = gadgets[lastidx-1];
	    }
	    else if ( gadgetCount > 0 ) {
	        gadget = gadgets[0];
	    }
	    lastDragGadget = gadget;
	    if ( gadget != null ) {
            if ( event.isControlDown() && gadget.isSelected() ) {
                deselect( gadget, event );
            }
            else {
                select( gadget, event );
            }
        }
    }

	/**
	 * moveDown
	 * @param event	the InputEvent
	 */
	public void moveDown( InputEvent event ) {
	    int lastidx = -1;
	    if ( lastDragGadget != null ) {
	        lastidx = getGadgetIndex( lastDragGadget );
	    }
	    else if ( startSelectionGadget != null ) {
	        lastidx = getGadgetIndex( startSelectionGadget );
	    }
	    Gadget gadget = null;
	    if ( lastidx < 0 ) {
	        gadget = gadgets[0];
	    }
	    else if ( lastidx < gadgetCount-1 ) {
	        gadget = gadgets[lastidx+1];
	    }
	    lastDragGadget = gadget;
	    if ( gadget != null ) {
            if ( event.isControlDown() && gadget.isSelected() ) {
                deselect( gadget, event );
            }
            else {
                select( gadget, event );
            }
        }
	}

    /**
     * processKeyEvent
     * @param e	the KeyEvent
     */
    protected void processKeyEvent(KeyEvent e) {
        if ( ignoreEvents ) {
            return;
        }
        if (e.getID() == KeyEvent.KEY_PRESSED) {
    	    boolean handled = true;
    		if ( ! e.isActionKey() ) {
    			if ( e.isControlDown() ||
    				 e.isMetaDown() ) {

    				switch ( e.getKeyCode() ) {

    					case 3:    // Ctrl-C
    					case 24: { // Ctrl-X
    						//copy();
    						break;
    					}

    					default: {
    					    handled = false;
    					}
    				}
    			}
    			else {
    			    handled = false;
        		}
    		}
    		else {
    			int nextPos = -1;

    			switch( e.getKeyCode() ) {
    				case KeyEvent.UP: {
    					moveUp( e );
    					break;
    				}
    				case KeyEvent.DOWN: {
    					moveDown( e );
    					break;
    				}
    				case KeyEvent.LEFT: {
    					listManager.moveLeft( e );
    					break;
    				}
    				case KeyEvent.RIGHT: {
    					listManager.moveRight( e );
    					break;
    				}
    				case KeyEvent.HOME: {
    				    if ( gadgetCount > 0 ) {
        					select( gadgets[0], e );
        				}
    					break;
    				}
    				case KeyEvent.END: {
    				    if ( gadgetCount > 0 ) {
        					select( gadgets[gadgetCount-1], e );
        				}
    					break;
    				}
    				case KeyEvent.PGUP: {
    				    int next = Math.max(0,topIndex-(numVisible-1));
    					select( gadgets[next], e );
    					break;
    				}
    				case KeyEvent.PGDN: {
    				    int next = Math.min(gadgetCount-1,
    				                        topIndex+numVisible+(numVisible-3));
    					select( gadgets[next], e );
    					break;
    				}
    				case KeyEvent.ENTER: {
    				    invokeAction(e);
    					break;
    				}
    				default: {
    					handled = false;
    				}
    			}
    		}
        	if (handled) {
        	    e.consume();
        	}
    	}
	    super.processKeyEvent( e );
	}

    /**
     * update
     * @param g	the GadgetGraphics object for drawing
     */
    public void update( GadgetGraphics g ) {
        synchronized(getTreeLock()) {
    	    if ((gadgetCount == 0) && hasFocus()) {
                drawFocus(g,getForeground(g),1);
            }
            super.update(g);
        }
    }

    /**
     * setChoiceList
     * @param in	new value for choiceList
     */
    protected void setChoiceList(boolean in) {
        choiceList = in;

    }

    /**
     * selectionChanged
     * @param event 	the AWTEvent
     * @param selected	is/is not selected
     */
    public void selectionChanged(AWTEvent event, boolean selected) {
        // don't do anything...ListManager is implemented so this object
        // can act as a placeholder.  You can't rely on this function getting called
        // because there is only one ListManager
    }

    /**
     * topChanged
     * @param topIndex	top index
     */
    public void topChanged( int topIndex ) {
        // don't do anything...ListManager is implemented so this object
        // can act as a placeholder.  You can't rely on this function getting called
        // because there is only one ListManager
    }

	/**
	 * moveLeft
	 * @param event	InputEvent
	 */
	public void moveLeft( InputEvent event ) {
        // don't do anything...ListManager is implemented so this object
        // can act as a placeholder.  You can't rely on this function getting called
        // because there is only one ListManager
    }

	/**
     * moveRight
     * @param event	InputEvent
     */
	public void moveRight( InputEvent event ) {
        // don't do anything...ListManager is implemented so this object
        // can act as a placeholder.  You can't rely on this function getting called
        // because there is only one ListManager
	}
}
