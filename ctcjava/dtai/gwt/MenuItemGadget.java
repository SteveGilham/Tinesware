/****************************************************************
 **
 **  $Id: MenuItemGadget.java,v 1.28 1998/01/24 19:55:07 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/MenuItemGadget.java,v $
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

import java.awt.Event;
import java.util.Vector;
import java11.awt.event.ActionEvent;
import java11.awt.event.ActionListener;
import java11.awt.event.AWTEvent;
import java11.awt.event.MouseEvent;
import java11.awt.event.FocusEvent;
import java11.awt.event.KeyEvent;
import java11.util.EventObject;


/**
 * MenuItemGadget
 * @version 1.1
 * @author DTAI, Incorporated
 */
public class MenuItemGadget extends BorderGadget {

    ActionListener actionListener;
    private LabelGadget labelGadget;

    /**
     * creates a menu item with no label
     */
    public MenuItemGadget() {
        this("");
    }

    /**
     * @param label	description
     */
    public MenuItemGadget(String label) {
        labelGadget = new LabelGadget( label );
        labelGadget.setHorizAlign(DisplayGadget.HORIZ_ALIGN_LEFT);
	    labelGadget.setPossiblyDisabled( true );
	    add("Center",labelGadget);
        setConsumingTransparentClicks(true);
    }

    public String toString() {
        return labelGadget.toString();
    }

    /**
     * setParent
     * @param parent	description
     * @param notify	description
     */
    public void setParent(ContainerGadget parent, boolean notify) {
        super.setParent(parent, notify);
        setFocusGroup(parent);
        if (parent instanceof PopupMenuGadget) {
            setMargins( 2, 10, 2, 10 );
        }
        else {
            setMargins( 2 );
        }
    }

    /**
     * isFocusTraversable
     * @return boolean
     */
    public boolean isFocusTraversable() {
        return true;
    }

    /**
     * sets the label with a specified string
     * @param label	description
     */
    public void setLabel(String label) {
        labelGadget.setLabel(label);
    }

    /**
     * getLabel
     * @return String
     */
    public String getLabel() {
        return labelGadget.getLabel();
    }

    /**
     * processMouseEvent
     * @param mouse the MouseEvent
     */
    public void processMouseEvent( MouseEvent mouse ) {
        if (mouse.getID() == MouseEvent.MOUSE_RELEASED) {
            invokeAction(mouse);
      		mouse.consume();
      	} else if (mouse.getID() == MouseEvent.MOUSE_PRESSED) {
      	    mouse.consume();
      	}
        super.processMouseEvent( mouse );
    }

    /**
     * invokeAction
     * @param e	description
     */
    public void invokeAction(AWTEvent e) {
        if ( parent != null && parent instanceof PopupMenuGadget ) {
            GadgetShell shell = getShell();
            ((PopupMenuGadget)parent).hideMenu();
            if (shell != null) {
        	    shell.flushGraphics();
        	}
        }
        ActionEvent aEvent = new ActionEvent(e.getSource(), e.getEvent(),
                                             ActionEvent.ACTION_PERFORMED);
	    processEvent(aEvent);
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


	/**
	 * processFocusEvent
	 * @param e		the firing FocusEvent
	 */
	protected void processFocusEvent(FocusEvent e) {
        if (e.getID() == FocusEvent.FOCUS_GAINED) {
    	    setSelected(true);
        } else {
    	    if (!(this instanceof MenuGadget)) {
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
        	    boolean handled = false;
            	if ( e.getKeyCode() == KeyEvent.TAB ) {
            	    if (this instanceof MenuGadget) {
                	    ((MenuGadget)this).hideMenu();
                	}
                	else if (parent instanceof PopupMenuGadget) {
                	    ((PopupMenuGadget)parent).hideMenu();
                	}
              	    handled = false; // must still handle focus
            	}
            	else if (e.getKeyCode() == KeyEvent.ENTER) {
            	    invokeAction(e);
            	    handled = true;
            	}
            	else if (e.getKeyCode() == KeyEvent.UP) {
            	    handled = prevGroupFocus();
                }
            	else if (e.getKeyCode() == KeyEvent.DOWN) {
            	    handled = nextGroupFocus();
            	}
            	if (handled) {
            	    e.consume();
            	}
            }
        }
        super.processKeyEvent( e );
	}
}
