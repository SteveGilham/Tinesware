/****************************************************************
 **
 **  $Id: MenuGadget.java,v 1.14 1998/01/24 19:55:07 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/MenuGadget.java,v $
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

import java.awt.Point;
import java.util.Vector;
import java11.awt.event.MouseEvent;
import java11.awt.event.KeyEvent;
import java11.awt.event.FocusEvent;
import java11.awt.event.ComponentEvent;
import java11.awt.event.ComponentListener;
import java11.awt.event.KeyListener;

/**
 * A MenuGadget that is a component of a menu bar.
 * @version 1.1
 * @author DTAI, Incorporated
 */
public class MenuGadget extends MenuItemGadget implements ComponentListener, KeyListener {
    private PopupMenuGadget popup;
    boolean isHelpMenu;
    boolean tearOff;
    private boolean fakedit;

    /**
     * Constructs a new MenuGadget with a blank label.
     *
     * @param label the label to be added to this menu
     */
    public MenuGadget() {
    	this("",false);
    }

    /**
     * Constructs a new MenuGadget with the specified label.  This menu can
     * not be torn off - the menu will still appear on screen after
     * the the mouse button has been released.
     *
     * @param label the label to be added to this menu
     */
    public MenuGadget(String label) {
    	this(label,false);
    }

    /**
     * Can't do this yet.
     *
     * Constructs a new MenuGadget with the specified label. If tearOff is
     * true, the menu can be torn off - the menu will still appear on
     * screen after the the mouse button has been released.
     *
     * @param label the label to be added to this menu
     * @param tearOff the boolean indicating whether or not the menu will be
     * able to be torn off.
     */
    private /*public*/ MenuGadget(String label, boolean tearOff) {
    	this(label,tearOff,new PopupMenuGadget());
    }

    /**
     * Constructs a new MenuGadget with the specified label and the given
     * popup menu.
     *
     * @param label the label to be added to this menu
     * @param popup the pre-existing popup menu
     */
    public MenuGadget(String label, PopupMenuGadget popup) {
    	this(label,false,popup);
    }

    /**
     * Can't do this yet either.
     *
     * Constructs a new MenuGadget with the specified label, given
     * popup menu, and tear-off option. If tearOff is
     * true, the menu can be torn off - the menu will still appear on
     * screen after the the mouse button has been released.
     *
     * @param label the label to be added to this menu
     * @param tearOff the boolean indicating whether or not the menu will be
     * able to be torn off.
     * @param popup the pre-existing popup menu
     */
    private /*public*/ MenuGadget(String label, boolean tearOff, PopupMenuGadget popup) {
    	super(label);
    	this.tearOff = tearOff;
    	this.popup = popup;
    	popup.addComponentListener(this);
    	popup.addKeyListener(this);
    }

    /**
     * Returns true if this is a tear-off menu.
     * @return tearOff
     */
    public boolean isTearOff() {
    	return tearOff;
    }

    /**
      * Returns the number of elements in this menu.
      * @return the number of elements in this menu.
      */
    public int getItemCount() {
    	return popup.getItemCount();
    }

    /**
     * Returns the item located at the specified index of this menu.
     * @param index the position of the item to be returned
     * @return the item located at the specified index of this menu.
     */
    public MenuItemGadget getItem(int index) {
    	return popup.getItem(index);
    }

    /**
     * Adds the specified item to this menu.
     * @param mi the item to be added
     * @return MenuItemGadget
     */
    public MenuItemGadget add(MenuItemGadget mi) {
	    synchronized(getTreeLock()) {
            return popup.add(mi);
        }
    }

    /**
     * SeparatorGadget
     * Adds the specified item to this menu.
     * @param s the item to be added
     * @return SeparatorGadget
     */
    public SeparatorGadget add(SeparatorGadget s) {
	    synchronized(getTreeLock()) {
            return popup.add(s);
        }
    }

    /**
     * Adds an item with with the specified label to this menu.
     * @param label the text on the item
     * @return MenuItemGadget
     */
    public MenuItemGadget add(String label) {
    	return popup.add(label);
    }

    /**
     * Adds a separator line, or a hypen, to the menu at the current position.
     */
    public SeparatorGadget addSeparator() {
        return popup.addSeparator();
    }

    /**
     * Deletes the item from this menu at the specified index.
     * @param index the position of the item to be removed
     */
    public void remove(int index) {
	    synchronized(getTreeLock()) {
            popup.remove(index);
        }
    }

    /**
     * Deletes the specified item from this menu.
     * @param item the item to be removed from the menu
     */
     /*
    public synchronized void remove(MenuComponent item) {
        popup.remove(item);
    }*/

    /**
     * showMenu
     */
    public void showMenu() {
	    popup.showMenu(0,height,this);
    }

    /**
     * hideMenu
     */
    public void hideMenu() {
	    popup.hideMenu();
    }

    /**
     * processMouseEvent
     * @param mouse the MouseEvent
     */
    public void processMouseEvent( MouseEvent mouse ) {
        if (mouse.getID() == MouseEvent.MOUSE_PRESSED) {
    	    showMenu();
    	    fakedit = false;
      		mouse.consume();
      	}
        super.processMouseEvent( mouse );
    }

    /**
     * processMouseMotionEvent
     * @param mouse the MouseEvent
     */
    public void processMouseMotionEvent( MouseEvent e ) {
        if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
    	    if (!fakedit &&
    	        (e.getY() > height || e.getX() < 0 || e.getX() > width)) {
        	    getShell().fakeMouseUp();
        	    fakedit = true;
        	}
      	}
        super.processMouseMotionEvent( e );
    }

	/**
	 * processFocusEvent
	 * @param e		the firing FocusEvent
	 */
	protected void processFocusEvent(FocusEvent e) {
        if (e.getID() == FocusEvent.FOCUS_GAINED) {
    	    setSelected(true);
        } else {
    	    if (!popup.isShowing()) {
        	    setSelected(false);
        	}
        }
	    super.processFocusEvent( e );
	}

    /**
     * processKeyEvent
     * @param e	the KeyEvent
     */
    protected void processKeyEvent(KeyEvent e) {
        if (!e.isConsumed()) {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                if ((e.getKeyCode() == KeyEvent.DOWN) ||
                    (e.getKeyCode() == KeyEvent.UP)) {
                    if (!popup.isShowing()) {
                        showMenu();
                    }
                    popup.requestFocus();
                    e.consume();
            	}
            	else if (e.getKeyCode() == KeyEvent.ESCAPE) {
            	    hideMenu();
                    e.consume();
            	}
            	else {
                    handleLeftRightKey(e);
                }
            }
        }
	    super.processKeyEvent(e);
	}

	/**
	 * PopupMenuGadget
	 * @return popup
	 */
	public PopupMenuGadget getPopup() {
	    return popup;
	}

    /**
     * componentResized
     * @param e	firing ComponentEvent
     */
    public void componentResized(ComponentEvent e) {
    }

    /**
     * componentMoved
     * @param e	firing ComponentEvent
     */
    public void componentMoved(ComponentEvent e) {
    }

    /**
     * componentShown
     * @param e	firing ComponentEvent
     */
    public void componentShown(ComponentEvent e) {
        if (popup.getPoppedUpFrom() == this) {
            setSelected(true);
        }
    }

    /**
     * componentHidden
     * @param e	firing ComponentEvent
     */
    public void componentHidden(ComponentEvent e) {
        setSelected(false);
    }

    /**
     * keyTyped
     * @param e	firing KeyEvent
     */
    public void keyTyped(KeyEvent e) {
    }

    /**
     * keyPressed
     * @param e	firing KeyEvent
     */
    public void keyPressed(KeyEvent e) {
        handleLeftRightKey(e);
    }

    /**
     * handleLeftRightKey
     * @param e	firing KeyEvent
     */
    public void handleLeftRightKey(KeyEvent e) {
        ContainerGadget p = parent;
        if (p instanceof MenuBarGadget) {
            MenuBarGadget menubar = (MenuBarGadget)p;
        	if (e.getKeyCode() == KeyEvent.LEFT) {
        	    menubar.prevMenu(this);
                e.consume();
            }
        	else if (e.getKeyCode() == KeyEvent.RIGHT) {
        	    menubar.nextMenu(this);
                e.consume();
        	}
        }
    }

    /**
     * keyReleased
     * @param e	firing KeyEvent
     */
    public void keyReleased(KeyEvent e) {
    }
}
