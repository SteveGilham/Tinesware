/****************************************************************
 **
 **  $Id: TabPaneGadget.java,v 1.31 1997/12/03 02:21:52 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/TabPaneGadget.java,v $
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
import java.awt.Insets;
import dtai.gwt.GadgetLayoutManager;
import java.awt.Rectangle;
import java.util.Vector;
import java11.awt.ItemSelectable;
import java11.awt.event.AWTEvent;
import java11.awt.event.FocusEvent;
import java11.awt.event.FocusListener;
import java11.awt.event.ItemEvent;
import java11.awt.event.ItemListener;
import java11.awt.event.MouseEvent;
import java11.awt.event.MouseListener;
import java11.util.EventObject;

/**
 * TabPaneGadget
 * @version 1.1
 * @author DTAI, Incorporated
 */
public class TabPaneGadget extends BorderGadget
    implements java11.awt.ItemSelectable, MouseListener, FocusListener {

    ItemListener itemListener;

    private PanelGadget cardManager;
    private GadgetCardLayout cards;

    private ButtonGadget topTab;

    private boolean initialized = false;

    /**
     * Create a new tab pane
     */
    public TabPaneGadget() {

        super.setLayout( null );

        setBorderType( BorderGadget.THREED_OUT );
        setBorderThickness( 2 );

        super.add( cardManager = new PanelGadget(), -1 );
        cardManager.setLayout( cards = new GadgetCardLayout() );
        cards.setTransparent(false);
        initialized = true;
    }

    /**
     * returns the card layout used by the tab manager.  Be default, it
     * is not a transparent card layout manager, meaning that only the top
     * card will show, even if the top card is transparent.  You can use
     * the given card layout to change that setting if you want to.
     * @return GadgetCardLayout
     */
    public GadgetCardLayout getCardLayout() {
        return cards;
    }

    /**
     * add
     * @param gadget - TBD
     * @param pos - TBD
     * @return Gadget -- returns null
     */
    public Gadget add( Gadget gadget, int pos ) {
        return null;
    }

    /**
     * setTab
     * @param name - TBD
     */
    public void setTab( String name ) {
        int count = getGadgetCount();
        for ( int i = 0; i < count; i++ ) {
            Gadget gadget = getGadget(i);
            if ( gadget != cardManager ) {
                String label = ((ButtonGadget)gadget).getLabel();
                if ( label.equals(name) ) {
                    if ( topTab != gadget ) {
                        topTab = (ButtonGadget)gadget;
                        setLastGroupedFocus(topTab);
                        cards.show(name);
                        repaint();
                		processEvent( new ItemEvent( this, null,
                		                             ItemEvent.ITEM_STATE_CHANGED,
                		                             this, ItemEvent.SELECTED ) );
                    }
                }
            }
        }
    }

    /**
     * remove
     * @param gadget - TBD
     * @param i - TBD
     */
    protected void remove( Gadget gadget, int i ) {
		synchronized(getTreeLock()) {
            if ( gadget == topTab ) {
                super.remove(gadget,i);
                int count = getGadgetCount();
                for ( i = 0; i < count; i++ ) {
                    Gadget tab = getGadget(i);
                    if ( ( tab != topTab ) &&
                         ( tab != cardManager ) ) {
                        setTab( ((ButtonGadget)tab).getLabel() );
                    }
                }
            }
        }
    }

    /**
     * Adds the specified item to the end of scrolling list.
     *
     * @param name - TBD
     * @param gadget - TBD
     * @return Gadget
     */
    public Gadget add(String name, Gadget gadget) {
		synchronized(getTreeLock()) {
            cardManager.add( name, gadget );
            ButtonGadget button = new ButtonGadget( name );
            button.addMouseListener(this);
            button.addFocusListener(this);
            button.setMargins(2);
            button.setBorderThickness(0);
            button.setDefaultThickness(0);
            Gadget lastButton = getGadget(getGadgetCount()-1);
            if ( lastButton instanceof ButtonGadget ) {
                lastButton.setNextFocusGadget(button);
            }
            super.add( button, -1 );
            button.setNextFocusGadget(cardManager);
            button.setFocusGroup(this);
            button.setArg(gadget);
            if ( topTab == null ) {
                topTab = button;
                setLastGroupedFocus(topTab);
            }
            invalidate();
            return gadget;
        }
    }

    /**
     * Returns the number of items in the list.
     *
     * @see getItem
     * @return int
     */

    public final int getItemCount() {
        int count = getGadgetCount();
        if ( cardManager != null ) {
            count--;
        }
        return count;
    }

    private int getGadgetIndexOfItem(int index) {
        int count = getGadgetCount();
        if ( cardManager != null ) {
            for ( int i = 0; i < count; i++ ) {
                Gadget gadget = getGadget(index);
                if ( gadget == cardManager ) {
                    index++;
                    break;
                }
            }
        }
        return index;
    }

    private int getItemIndexOfGadget(Gadget find) {
        if ( find == null ) {
            return -1;
        }
        int index = 0;
        int count = getItemCount();
        for ( int i = 0; i < count; i++ ) {
            Gadget gadget = getGadget(index);
            if ( gadget == find ) {
                return index;
            }
            if ( gadget != cardManager ) {
                index++;
            }
        }
        return -1;
    }

    /**
     * Gets the item associated with the specified index.
     *
     * @param index - the position of the item
     * @see getItemCount
     * @return String
     */

    public String getItem(int index) {
        index = getGadgetIndexOfItem(index);
        return ((ButtonGadget)getGadget(index)).getLabel();
    }

    /**
     * Returns the items in the list.
     *
     * @see select, deselect, isIndexSelected
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
     * Removes all items from the list.
     *
     * @see remove
     * @see delItems
     */

    public void removeAll() {
		synchronized(getTreeLock()) {
            int count = getGadgetCount();
            for ( int i = 0; i < count; i++ ) {
                Gadget gadget = getGadget(i);
                if ( gadget != cardManager ) {
                    super.remove(gadget);
                }
            }
            cardManager.removeAll();
        }
    }

    /**
     * Removes an item from the list.
     * @param index - TBD
     */

    public void removeItem(int index) {
		synchronized(getTreeLock()) {
            index = getGadgetIndexOfItem(index);
            Gadget gadget = getGadget(index);
            super.remove(index);
            gadget = (Gadget)gadget.getArg();
            cardManager.remove(gadget);
        }
    }

    /**
     * Get the selected item on the list or -1 if no item is selected.
     *
     * @see select
     * @see deselect
     * @see isIndexSelected
     * @return int
     */

    public int getSelectedIndex() {
		synchronized(getTreeLock()) {
            return getItemIndexOfGadget(topTab);
        }
    }

    /**
     * Returns the selected indexes on the list.
     *
     * @see select
     * @see deselect
     * @see isIndexSelected
     * @return int[]
     */

    public int[] getSelectedIndexes() {
		synchronized(getTreeLock()) {
            int index = getItemIndexOfGadget(topTab);
            if ( index == -1 ) {
                return null;
            }
            else {
                int indexes[] = new int[1];
                indexes[0] = index;
                return indexes;
            }
        }
    }

    /**
     * Returns the selected item on the list or null if no item is selected.
     *
     * @see select
     * @see deselect
     * @see isIndexSelected
     * @return String
     */

    public String getSelectedItem() {
		synchronized(getTreeLock()) {
            if ( topTab == null ) {
                return null;
            }
            else {
                return topTab.getLabel();
            }
        }
    }

    /**
     * Returns the selected items on the list.
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
                strings[i] = getItem(i);
            }
            return strings;
        }
    }

    /**
     * Returns the preferred dimensions needed for the list with the specified amount of rows.
     *
     * @param rows - amount of rows in list.
     * @return Dimension
     */

    public Dimension getPreferredSize() {
	    if ( valid && ( pref_width >= 0 ) && ( pref_height >= 0 ) ) {
	        return new Dimension( pref_width, pref_height );
	    }
        Dimension pref = cardManager.getPreferredSize();
        pref.height += 10;
        int max_height = 0;
        int total_tab_width = 4;
        int count = getGadgetCount();
        for ( int i = 0; i < count; i++ ) {
            Gadget gadget = getGadget(i);
            if ( gadget != cardManager ) {
                int tabwidth = gadget.pref_width;
                int tabheight = gadget.pref_height;
                if ( ( tabwidth < 0 ) ||
                     ( tabheight < 0 ) ) {
                    Dimension tabpref = gadget.getPreferredSize();
                    tabwidth = tabpref.width;
                    tabheight = tabpref.height;
                }
                total_tab_width += tabwidth+6;
                max_height = Math.max(max_height,tabheight+3);
            }
        }
        pref.width = Math.max( pref.width, total_tab_width );
        pref.height += max_height;
        return pref;
    }

    /**
     * Sets the layout manager for this container. This method is overridden to
     * prevent the layout mgr from being set.
     * Overrides:
     *      setLayout in class Container
     *
     * @param mgr - the specified layout manager
     *
     */

    public final void setLayout( GadgetLayoutManager mgr ) {
        if (initialized) {
            throw new IllegalArgumentException();
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
     * Lays out this container by resizing its child to its preferred size. If the new preferred size of the child causes the current scroll position to be invalid, the scroll
     * position is set to the closest valid position.
     *
     * Overrides:
     *      doLayout in class Container
     * @see validate
     */

    public void doLayout() {
        Dimension pref = cardManager.getPreferredSize();
        int max_height = 0;
        int total_tab_width = 1;
        int count = getGadgetCount();
        for ( int i = 0; i < count; i++ ) {
            Gadget gadget = getGadget(i);
            if ( gadget != cardManager ) {
                int tabwidth = gadget.pref_width;
                int tabheight = gadget.pref_height;
                if ( ( tabwidth < 0 ) ||
                     ( tabheight < 0 ) ) {
                    Dimension tabpref = gadget.getPreferredSize();
                    tabwidth = tabpref.width;
                    tabheight = tabpref.height;
                }
                max_height = Math.max(max_height,tabheight+3);
                if ( gadget == topTab ) {
                    gadget.setBounds( total_tab_width + 3, 2, tabwidth, tabheight, false );
                }
                else {
                    gadget.setBounds( total_tab_width + 3, 4, tabwidth, tabheight, false );
                }
                total_tab_width += tabwidth+6;
            }
        }

        Insets insets = getInsets();
        cardManager.setBounds( insets.left, max_height + insets.top,
                   width - ( insets.left + insets.right ),
                   height - ( insets.bottom + max_height + insets.top ), false );
    }

	/**
	 * paints the gadget
	 * @param g - TBD
	 */
	public void paint( GadgetGraphics g ) {
	    Rectangle clip = g.getClipRect();
	    boolean paintingAll = g.isPaintingAll();
        int count = getGadgetCount();
        int max_height = 0;
        int total_tab_width = 1;
        for ( int i = 0; i < count; i++ ) {
            Gadget gadget = getGadget(i);
            if ( gadget != cardManager ) {
                int tabwidth = gadget.pref_width;
                int tabheight = gadget.pref_height;
                if ( ( tabwidth < 0 ) ||
                     ( tabheight < 0 ) ) {
                    Dimension tabpref = gadget.getPreferredSize();
                    tabwidth = tabpref.width;
                    tabheight = tabpref.height;
                }
                total_tab_width += tabwidth+6;
                max_height = Math.max(max_height,tabheight+3);
            }
        }

        Color foreground = getForeground(g);
        Color background = getBackground(g);

        Rectangle gadgetclip = new Rectangle( total_tab_width+1, max_height,
                                              width - total_tab_width-1, height - max_height );
        Rectangle newclip = clip.intersection( gadgetclip );
        g.clipRect( newclip.x, newclip.y,
                         newclip.width, newclip.height );
        paintBorder( g, 0, max_height, width, height - max_height, background );

        gadgetclip = new Rectangle( 0, max_height + getBorderThickness(),
                                              total_tab_width+1, height - max_height );
        newclip = clip.intersection( gadgetclip );
        if (paintingAll) {
            GadgetGraphics nextg = g.create();
            g.dispose();
            g = nextg;
            g.clipRect(newclip.x,newclip.y,newclip.width,newclip.height);
        } else {
            //Vector damageList = g.getDamageList();
            g.dispose();
            g = getGadgetGraphics(newclip.x,newclip.y,newclip.width,newclip.height );
            //g.setDamageList(damageList);
        }
        paintBorder( g, 0, max_height, width, height - max_height, background );

        if (paintingAll) {
            GadgetGraphics nextg = g.create();
            g.dispose();
            g = nextg;
            g.clipRect(clip.x,clip.y,clip.width,clip.height);
        } else {
            //Vector damageList = g.getDamageList();
            g.dispose();
            g = getGadgetGraphics(clip.x,clip.y,clip.width,clip.height);
            //g.setDamageList(damageList);
        }

        Color border;
        if ( background == null ) {
            border = Color.lightGray;
        }
        else {
            border = background;
        }
        Color top = GadgetGraphics.brighter(border);
        Color bottom = GadgetGraphics.darker(border);

        count = getGadgetCount();
        total_tab_width = 1;
        int index = 0;
        for ( int i = 0; i < count; i++ ) {
            Gadget gadget = getGadget(i);
            if ( gadget != cardManager ) {
                int tabwidth = gadget.pref_width;
                if ( tabwidth < 0 ) {
                    Dimension tabpref = gadget.getPreferredSize();
                    tabwidth = tabpref.width;
                }
                if ( gadget != topTab ) {
                    g.setColor(top);
                    if ( index == 0 ) {
                        g.drawLine(1,max_height+1,3,max_height+1);
                        g.drawLine(1,max_height+1,1,max_height+1);
                    }
                    g.drawLine(total_tab_width+1,4,total_tab_width+1,max_height+1);
                    g.drawLine(total_tab_width+1, 4, total_tab_width+3,2);
                    g.drawLine(total_tab_width+2,2,total_tab_width+tabwidth+3,2);
                    g.drawLine(total_tab_width+1,max_height+1,total_tab_width+tabwidth+6,max_height+1);
                    g.setColor(bottom);
                    g.drawLine(total_tab_width+tabwidth+3, 2, total_tab_width+tabwidth+5, 4);
                    g.drawLine(total_tab_width+tabwidth+3, 3, total_tab_width+tabwidth+4, 4);
                    g.drawLine(total_tab_width+tabwidth+5,4,total_tab_width+tabwidth+5,max_height);
                    g.drawLine(total_tab_width+tabwidth+4,4,total_tab_width+tabwidth+4,max_height);
                }
                total_tab_width += tabwidth+6;
                index++;
            }
        }

        count = getGadgetCount();
        total_tab_width = 1;
        index = 0;
        for ( int i = 0; i < count; i++ ) {
            Gadget gadget = getGadget(i);
            if ( gadget != cardManager ) {
                int tabwidth = gadget.pref_width;
                if ( tabwidth < 0 ) {
                    Dimension tabpref = gadget.getPreferredSize();
                    tabwidth = tabpref.width;
                }
                if ( gadget == topTab ) {
                    g.setColor(top);
                    if ( index == 0 ) {
                        g.drawLine(total_tab_width,2,total_tab_width,max_height+1);
                    }
                    else {
                        g.drawLine(total_tab_width,2,total_tab_width,max_height+1);
                    }
                    g.drawLine(total_tab_width,2,total_tab_width+2,0);
                    g.drawLine(total_tab_width+1,0,total_tab_width+tabwidth+5,0);
                    g.setColor(bottom);
                    g.drawLine(total_tab_width+tabwidth+5, 0, total_tab_width+tabwidth+7, 2);
                    g.drawLine(total_tab_width+tabwidth+5, 1, total_tab_width+tabwidth+6, 2);
                    g.drawLine(total_tab_width+tabwidth+7,2,total_tab_width+tabwidth+7,max_height);
                    g.drawLine(total_tab_width+tabwidth+6,2,total_tab_width+tabwidth+6,max_height);
                }
                total_tab_width += tabwidth+6;
                index++;
            }
        }
        g.dispose();
	}

    /**
     * mouseClicked
     * @param e - TBD
     */
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * mouseReleased
     * @param e - TBD
     */
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * mouseEntered
     * @param e - TBD
     */
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * mouseExited
     * @param e - TBD
     */
    public void mouseExited(MouseEvent e) {
    }

	/**
     * mousePressed
     * @param e - TBD
     */
	public void mousePressed(MouseEvent e) {
	    processTabEvent(e);
	}

	/**
     * focusLost
     * @param e - TBD
     */
	public void focusLost(FocusEvent e) {
	}

	/**
     * focusGained
     * @param e - TBD
     */
	public void focusGained(FocusEvent e) {
	    processTabEvent(e);
	}

	/**
     * processTabEvent
     * @param e - TBD
     */
	public void processTabEvent(AWTEvent e) {
        setTab( ((ButtonGadget)e.getSource()).getLabel() );
	}
}
