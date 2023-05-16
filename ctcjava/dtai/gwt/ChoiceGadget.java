/****************************************************************
 **
 **  $Id: ChoiceGadget.java,v 1.27 1997/08/27 05:55:10 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/ChoiceGadget.java,v $
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

import java11.awt.event.FocusEvent;
import java11.awt.event.InputEvent;
import java11.awt.event.KeyEvent;
import java11.awt.event.MouseEvent;
import java11.awt.event.MouseListener;

/**
 * The Choice class is a pop-up menu of choices. The current choice is
 * displayed as the title of the menu.
 *
 * @version	1.1
 * @author 	DTAI, Incorporated
 */
public class ChoiceGadget extends ComboBoxGadget implements MouseListener {

    private long timeLastTyped = 0;

    /**
     * brings up pop-up menu of choices with selected number of visible items
     *
     * @param visibleItems	number of visible items for this ChoiceGadget
     */
     public ChoiceGadget(int visibleItems) {
        super(visibleItems);
        text.setEditable(false);
        text.addMouseListener(this);
    }

    public void setText(String text) {
        if (!text.equals(getText())) {
            super.setText(text);
            select(text);
        }
    }

    /**
     * keyPressed
     *
     * @param e	the firing KeyEvent
     */
    public void keyPressed(KeyEvent e) {

        if ( (e.getKeyCode() != KeyEvent.UP) &&
            (e.getKeyCode() != KeyEvent.DOWN) &&
            (e.getKeyCode() != KeyEvent.ENTER) &&
            (!list.isShowing()) && sorted) {
                super.showList();
        }
        super.keyPressed(e);
    }

    /**
     * fillSearchText
     *
     * @param e	the firing KeyEvent
     */
    public void fillSearchText(KeyEvent e) {

        if (sorted) {

	        if ((System.currentTimeMillis() - timeLastTyped) > 1500) {
                searchText = "";
            }
            searchText = searchText + e.getKeyChar();
            searchText = searchText.toUpperCase();

            timeLastTyped = System.currentTimeMillis();
        }

    }

    /**
     * mousePressed
     *
     * @param e	the firing MouseEvent
     */
    public void mousePressed(MouseEvent e) {
        notifyList();

    }

    /**
     * mouseClicked
     *
     * @param e	the firing MouseEvent
     */
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * mouseReleased
     *
     * @param e	the firing MouseEvent
     */
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * mouseEntered
     *
     * @param e	the firing MouseEvent
     */
    public void mouseEntered(MouseEvent e) {

    }

    /**
     * mouseExcited
     *
     * @param e	the firing MouseEvent
     */
    public void mouseExited(MouseEvent e) {

    }

	/**
	 * shows the list of items
	 */
	protected void showList() {

	    timeLastTyped = System.currentTimeMillis();
	    if ((text.getText().equals("")) && (list.getItemCount() > 0)) {
            list.select(0);
	    }
	    super.showList();
	}

	/**
	 * hides the list of items
	 */
     protected void hideList() {
        timeLastTyped = System.currentTimeMillis();
        super.hideList();
    }

    /**
     * adds a specified item to the list
     *
     * @param item	name of the item to be added
     */
    public void add(String item) {
		synchronized(getTreeLock()) {
            super.add(item);
            if (text.getText().trim().equals("")) {
                select(0);
            }
        }
    }

    /**
     * select - selects a specified item
     *
     * @param pos	index of the item to be selected
     */
    public void select(int pos) {
        super.select(pos);
        text.selectAll();
    }

	/**
	 * processFocusEvent
	 *
	 * @param e	the firing FocusEvent
	 * @return boolean
	 */
	public void processFocusEvent(FocusEvent e) {
	    if (e.getID() == FocusEvent.FOCUS_GAINED) {
            text.selectAll();
        } else {
            text.select(0);
        }
        repaint();
	    super.processFocusEvent(e);
	}
}
