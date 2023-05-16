/****************************************************************
 **
 **  $Id: ComboBoxGadget.java,v 1.49 1997/12/09 03:22:30 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/ComboBoxGadget.java,v $
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
import java.awt.Font;
import java.awt.Point;
import java.util.Vector;
import java11.awt.ItemSelectable;
import java11.awt.event.ActionEvent;
import java11.awt.event.ActionListener;
import java11.awt.event.AWTEvent;
import java11.awt.event.ItemEvent;
import java11.awt.event.ItemListener;
import java11.awt.event.KeyEvent;
import java11.awt.event.KeyListener;
import java11.util.EventObject;


/**
 * ComboBoxGadget
 *
 * @version 1.1
 * @author  DTAI, Incorporated
 */
public class ComboBoxGadget extends BorderGadget implements java11.awt.ItemSelectable, KeyListener {

    ArrowButtonGadget button = new ArrowButtonGadget(ArrowButtonGadget.DOWN);
    ListGadget list;
    TextFieldGadget text;
    ItemListener itemListener;
    private ActionListener actionListener;
    String searchText;

	private int visibleItems;
    private ComboActionListener comboActionListener;
    boolean sorted = false;

    /**
     * ComboBoxGadget
     *
     * @param visibleItems	number of visible items in this ComboBoxGadget
     */
    public ComboBoxGadget(int visibleItems) {
        this(visibleItems,new TextFieldGadget());
    }

    /**
     * ComboBoxGadget
     *
     * @param visibleItems	number of visible items in this ComboBoxGadget
     * @param text	the TextFieldGadget for this ComboBox
     */
    public ComboBoxGadget(int visibleItems, TextFieldGadget text) {
        this.text = text;
        text.setNoInsets();

		this.visibleItems = visibleItems;
		list = new ListGadget(visibleItems, false);
		list.setChoiceList(true);
		list.setFocusTraversable(false);

		list.setHorizontalScrollbarDisplayPolicy(ListGadget.SCROLLBAR_NEVER);

        comboActionListener = new ComboActionListener(this);
        button.addActionListener(comboActionListener);
		list.addActionListener(comboActionListener);
		text.addActionListener(comboActionListener);
        text.addKeyListener(this);
        list.addKeyListener(this);

	    setLayout(new GadgetBorderLayout());

		setBorderType(BorderGadget.THREED_IN);
		setBorderThickness(2);

	    list.setBorderThickness(1);
	    list.setBorderColor( Color.black );
	    list.setBorderType(BorderGadget.LINE);

	    add("East", button);
	    add("Center", text);

	    button.setFocusAllowed(false);
    }

    /**
     * isAllowedToShrink
     *
     * @return  the boolean indicating whether this
	 * TextComponent is allowedToShrink or not.
     * @see TextFieldGadget#setEditable
     */
    public boolean isAllowedToShrink() {
        return text.isAllowedToShrink();
    }

    /**
     * Sets the specified boolean to indicate whether or
	 * not this TextComponent should be allowedToShrink.
     *
     * @param b 	the boolean to be set
     * @see TextFieldGadget#isEditable
     */
    public void setAllowedToShrink(boolean b) {
        text.setAllowedToShrink(b);
    }

    /**
     * Gets the TextFieldGadget.
     *
     * @return TextFieldGadget
     */
    public TextFieldGadget getTextField() {
        return text;
    }

    /**
     * Gets the ListGadget.
     *
     * @return ListGadget
     */
    public ListGadget getList() {
        return list;
    }

    /**
     * Sets the background color of the LabelGadet at the
	 * specified index.
     *
     * @param color	color of new Background
     * @param index	index of the LabelGadget in the list that will
	 * have its background color set.
     */
    public void setBackground(Color color, int index ) {
        list.setBackground(color, index);
    }

    /**
     * Sets the background color of the LabelGadet with the
	 * specified name.
     *
     * @param color	color of new Background
     * @param item	name of the LabelGadget in the list that
	 * will have its background color set.
     */
    public void setBackground(Color color, String item) {
	    synchronized(getTreeLock()) {
            int index = list.getItemIndex(item);
            if ( index >= 0 ) {
                setBackground(color, index);
            }
        }
    }

    /**
     * Sets the foreground color of the LabelGadet at the
	 * specified index.
     *
     * @param color	color of new foreground
     * @param index	index of the LabelGadget in the list that will
	 * have its foreground color set.
     */
    public void setForeground(Color color, int index ) {
        list.setForeground(color, index);
    }

    /**
     * Sets the foreground color of the LabelGadet with the
	 * specified name.
     *
     * @param color	color of new foreground
     * @param item	name of the LabelGadget in the list that
	 * will have its foreground color set.
     * setForeground
     *
     * @param color	description
     * @param item	description
     */
    public void setForeground(Color color, String item) {
	    synchronized(getTreeLock()) {
            int index = list.getItemIndex(item);
            if ( index >= 0 ) {
                setForeground(color, index);
            }
        }
    }
    /**
     * setNextFocusGadget
     *
     * @param nextFocusGadget	description
     */
    public void setNextFocusGadget( Gadget nextFocusGadget ) {
	    text.setNextFocusGadget(nextFocusGadget);
	    list.setNextFocusGadget(nextFocusGadget);
	}

    /**
     * Sets the boolean sorted flag.
     *
     * @param in	boolean sorted status
     */
    public void setSorted(boolean in) {
        sorted = in;
    }

    /**
     * Gets the boolean sorted flag.
     *
     * @return boolean
     */
    public boolean isSorted() {
        return sorted;
    }

	/**
	 * Sets the text in the TextFieldGadget.
	 *
	 * @param textval	new text for the TextFieldGadget
	 */
	public void setText(String textval) {
	    text.setText(textval);
    }

    /**
	 * Gets the text in the TextFieldGadget.
     *
     * @return text in the TextFieldGadget
     */
    public String getText() {
        return text.getText();
    }

    /**
	 * Returns the number of items in the ListGadget.
     *
     * @return number of items in the list
     */
    public int countItems() {
        return list.getItemCount();
    }

    /**
     * Gets the item in the list at the specified index.
     *
     * @param index	index of item in the list to be retrieved
     * @return item in the list at the given index
     */
    public String getItem(int index) {
         return list.getItem(index);
    }

    /**
     * Adds the given item to the list.
	 * If the list is currently unsorted, the new item is
	 * simply added to the list.  If the list is currently
	 * sorted, the new item is inserted in its properly
	 * sorted spot.
     *
     * @param item	name of the item to be added to the list
     */
    public void add(String item) {
		synchronized(getTreeLock()) {
            invalidate();
		    if (!sorted) {
                list.add(item);
            }
            else {
                String[] items = list.getItems();
                for (int i = 0; i < items.length; i++) {
                    if (items[i].compareTo(item) > 0) {
                        add(item,i);
                        return;
                    }
                }
                add(item,items.length);
            }
        }
    }

    /**
     * Adds the given item to the list at the given index.
     *
     * @param item	name of the item to be added to the list
     * @param index	where in the list to insert given item
     */
    public void add(String item, int index) {
		synchronized(getTreeLock()) {
            list.add(item, index);
            invalidate();
        }
    }

    /**
     * Replace the item at the specified index with the given item.
     *
     * @param item	new value to replace the existing item
     * @param index	the position of the item to replace
     */
    public void replace(String item, int index) {
		synchronized(getTreeLock()) {
            list.replaceItem(item, index);
            invalidate();
        }
    }

    /**
     * Removes the specified item from the list.
     *
     * @param item	item to be removed from the list
     */
    public void remove(String item) {
		synchronized(getTreeLock()) {
            try {
                list.remove(item);
                invalidate();
            }
            catch ( IllegalArgumentException e ) {}
        }
    }

    /**
     * Removes the item at the given position from the list.
     *
     * @param pos	index of item to be removed from the list
     */
    public void remove(int pos) {
		synchronized(getTreeLock()) {
            try {
                list.remove(pos);
                invalidate();
            }
            catch ( IllegalArgumentException e ) {}
        }
    }

    /**
     * Adds the given item to the list at the given index.
     *
     * @param item	name of the item to be added to the list
     * @param index	where in the list to insert given item
     */
    public void insert(String item,int index) {
        add(item,index);
    }

    /**
	 * Gets the text in the TextFieldGadget.
     *
     * @return the text in the TextFieldGadget
     */
    public String getSelectedItem() {
        return getText();
    }

    /**
	 * Returns the index in the list of the selected item,
	 * or -1 if the selected item is not in the list.
     *
     * @return int
     */
    public int getSelectedIndex() {
        String text = getText();
        String selected = list.getSelectedItem();
        if ( selected != null && selected.equals(text) ) {
            return list.getSelectedIndex();
        }
        String[] items = list.getItems();
        for ( int i = 0; i < items.length; i++ ) {
            if ( items[i].equals(text) ) {
                list.select(i);
                return i;
            }
        }
        return -1;
    }

    /**
	 * Returns the preferred width and height, if both >= 0,
	 * else compares the parent's preferred size to the list's,
	 * and returns the Dimension with the larger width.
     *
     * @return a Dimension object
     */
    public Dimension getPreferredSize() {
	    if ( valid && ( pref_width >= 0 ) && ( pref_height >= 0 ) ) {
	        return new Dimension( pref_width, pref_height );
	    }
	    Dimension pref = super.getPreferredSize();
	    list.setFont(text.getFont());
        Dimension listSize = list.getPreferredSize();
        pref.width = Math.max(pref.width,listSize.width+2);
        return pref;
    }

    /**
	 * Calls getPreferredSize().
     *
     * @return Dimension
     */
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    // Over load to remove list from overlay

    /**
     * setParent
     *
     * @param parent	description
     * @param notifyParent	description
     */
    public void setParent( ContainerGadget parent, boolean notifyParent) {
      if (list.isShowing()) {
           hideList();
      }
      super.setParent(parent, notifyParent);
    }

    /**
     * Returns the TextFieldGadget.
     *
     * @return TextFieldGadget
     */
    public TextFieldGadget getTextFieldGadget() {
        return text;
    }

    /**
     * Returns an array of integers made up of the indexes of the
	 * selected items from the list.
     *
     * @return int[]
     */
    public int[] getSelectedIndexes() {
        return list.getSelectedIndexes();
    }

    /**
     * Returns an array of Strings made up of the names of the
	 * selected items from the list.
     *
     * @return String[]
     */
    public String[] getSelectedItems() {
        return list.getSelectedItems();
    }

    /**
     * Calls text.requestFous().
	 * @see Gadget#requestFocus
     */
    public void requestFocus() {
        text.requestFocus();
    }

    /**
	 * Makes the given item the selected one, if it is not
	 * already.
     *
     * @param item	name of the item to select
     */
    public void select(String item) {
	    synchronized(getTreeLock()) {
            int index = list.getItemIndex(item);
            if ( index >= 0 &&
                index != getSelectedIndex() ) {
                select(index);
            }
        }
    }

    /**
     * Selects the item at the given index in the list.
     *
     * @param pos	index of item in list to select
     */
    public void select(int pos) {
	    synchronized(getTreeLock()) {
            list.select(pos);
    		text.setText(list.getSelectedItem());
    		hideList();
    		ItemEvent iteme;
    		processEvent( iteme = new ItemEvent (this, new Event( this, -1, null ),
                            ItemEvent.ITEM_STATE_CHANGED, list.getSelectedItem(),
                            ItemEvent.SELECTED));
            text.invokeAction(iteme);
        }
    }

    /**
     * processActionPerformed
     *
     * @param e	 the firing ActionEvent
     */
    protected void processActionPerformed(ActionEvent e) {
		if (e.getSource() == button) {
			notifyList();
		}
		else if ( e.getSource() == list ) {
			select(list.getSelectedIndex());
		}
		else if ( e.getSource() == text ) {
			text.selectAll();
    		processEvent( new ActionEvent( this, e.getEvent(),
    		                               ActionEvent.ACTION_PERFORMED ) );
		}
	}

	/**
	 * Hides the list, if it is showing, and shows
	 * the list if it is hiding.
	 * @see #showList
	 * @see #hideList
	 */
	protected void notifyList() {
		if (list.isShowing()) {
			hideList();
        }
        else {
			showList();
        }

	}

	/**
	 * Shows the list
	 */
	protected void showList() {
		if (list.getItemCount() > 0) {
		    list.setBackground(parent.getFinalBackground());
		    list.setFont(text.getFont());
            list.select(0);
			OverlayPanelGadget overlays = getOverlayPanel();
			if ( overlays != null ) {
    			overlays.add(list);
                overlays.setConsumingTransparentClicks(true);
    			Dimension textSize = getSize();
    			Dimension listSize = list.getPreferredSize(visibleItems);
    			listSize.width = Math.max(listSize.width,textSize.width);
    			Point offset = getOffset();
    			list.setBounds(offset.x, offset.y + textSize.height,
    			               listSize.width, listSize.height);
                if ( ( ( offset.y+textSize.height+listSize.height ) > overlays.height ) &&
                     ( offset.y > listSize.height ) ) {
        			list.setBounds(offset.x, offset.y-listSize.height,
        			               listSize.width, listSize.height);
                }
                else {
        			list.setBounds(offset.x, offset.y + textSize.height,
        			               listSize.width, listSize.height);
                }
            }
    	    findItemInList(text.getText());
			list.setVisible(true);
	        requestFocus();
		}
	}

	public void setVisible(boolean in, boolean invalidateParent) {
	    super.setVisible(in, invalidateParent);
	    if (in == false) list.setVisible(false);
	}

	/**
	 * Hides the list
	 */
	protected void hideList() {
		OverlayPanelGadget overlays = getOverlayPanel();
	    if ( list.isVisible() && list.getParent() == overlays ) {
    		if ( overlays != null ) {
    		    if ( list.isDescendentOf(overlays) ) {
            		overlays.remove(list);
            	}
        	}
        	text.requestFocus();
        }
        GadgetShell shell = getShell();
        if (shell != null) {
            shell.flushGraphics();
        }
	}

	private void findItemInList(String find) {
	    for (int i=0; i<list.getItemCount(); i++) {
	        if (list.getItem(i).equals(find)) {
	            list.select(i);
	            return;
	        }
	    }
	    if (sorted) {
            searchText = text.getText().toUpperCase();
            findClosestMatch();
        }
	}

    private void findClosestMatch() {
	    for (int i=0; i<list.getItemCount(); i++) {
	        String listString = list.getItem(i).toUpperCase();
	        int compare = listString.compareTo(searchText);

	        if (compare >= 0) {
	            list.select(i);
	            return;
	        }
	    }
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
	 * @param e		a ItemEvent
	 * @return boolean result
	 */
	protected void processEvent(AWTEvent e) {
		if (e instanceof ItemEvent) {
		    processItemEvent((ItemEvent)e);
		} else if (e instanceof ActionEvent) {
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

	protected void processItemEvent(ItemEvent e) {
		if (itemListener != null) {
    		if(e.getID() == ItemEvent.ITEM_STATE_CHANGED) {
    			itemListener.itemStateChanged(e);
    		}
    	}
	}

    /**
     * keyTyped - empty method.
     *
     * @param e	the firing KeyEvent
     */
    public void keyTyped(KeyEvent e) {
    }

    /**
	 * If ComboBox is sorted, get text from the TextFieldGadget, and
	 * find the closest match in the list. If ComboBox isn't sorted,
	 * handle appropriate key-press possibilities: ESCAPE, UP, DOWN,
	 * ENTER, or TAB.
     *
     * @param e	the firing KeyEvent
     */
    public void keyPressed(KeyEvent e) {

		if ( ! e.isActionKey() ) {

            if (sorted) {
                fillSearchText(e);
                findClosestMatch();
            }
		    return;
		}

        switch (e.getKeyCode()) {

            case KeyEvent.ESCAPE:
                hideList();
                break;

            case KeyEvent.UP:
            case KeyEvent.DOWN:
                if (!list.isShowing()) {
                    showList();
                }
                else if ( (list.getSelectedIndex() < list.getItemCount()-1) &&
                          (e.getKeyCode() == KeyEvent.DOWN) ) {
                    list.select(list.getSelectedIndex() + 1);
                    text.setText(list.getSelectedItem());
                    text.selectAll();
                }
                else if ((list.getSelectedIndex() > 0) &&  (e.getKeyCode() == KeyEvent.UP) ) {
                    list.select(list.getSelectedIndex() - 1);
                    text.setText(list.getSelectedItem());
                    text.selectAll();
                }
                break;

            case KeyEvent.ENTER:
                if ( list.isShowing() ) {
                    if ( list.getSelectedIndex() > -1 ) {
                        select(list.getSelectedIndex());
                    }
                    else {
                        hideList();
                    }
                }
                break;

            case KeyEvent.TAB:
                if (list.isShowing()) {
                    hideList();
                }
                break;
        }
    }

    /**
	 * Sets the searchText variable to be the text in the
	 * TextFieldGadget (in upper case);
     *
     * @param e	firing KeyEvent
     */
    public void fillSearchText(KeyEvent e) {
        searchText = text.getText().toUpperCase();
    }

    /**
     * keyReleased - empty method.
     *
     * @param e	firing KeyEvent
     */
    public void keyReleased(KeyEvent e) {
    }
}

/**
 * ComboActionListener
 *
 * @version	1.1
 * @author	DTAI, Incorporated
 */
class ComboActionListener implements ActionListener {

    ComboBoxGadget combo;

    public ComboActionListener( ComboBoxGadget combo ) {
        this.combo = combo;
    }

    public void actionPerformed( ActionEvent e ) {
        combo.processActionPerformed( e );
    }
}
