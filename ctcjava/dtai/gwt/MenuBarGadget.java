/****************************************************************
 **
 **  $Id: MenuBarGadget.java,v 1.17 1997/11/20 19:37:53 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/MenuBarGadget.java,v $
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
import java11.awt.event.MouseMotionListener;
import java11.awt.event.MouseEvent;

/**
 * A class that encapsulates the platform's concept of a menu bar bound
 * to a Frame. In order to associate the MenuBarGadget with an actual Frame,
 * the Frame.setMenuBar() method should be called.
 * @version 1.1
 * @author DTAI, Incorporated
 */
public class MenuBarGadget extends BorderGadget implements MouseMotionListener {
    Vector menus = new Vector();
    MenuGadget helpMenu;

    /**
     * Creates a new menu bar.
     */
    public MenuBarGadget() {
        setLayout(new GadgetFlowLayout(GadgetFlowLayout.LEFT,10,0));
        setBorderType(BorderGadget.THREED_OUT);
        setBorderThickness(1);
    }

    /**
     * Gets the help menu on the menu bar.
     * @return helpMenu
     */
    public MenuGadget getHelpMenu() {
    	return helpMenu;
    }

    /**
     * doLayout
     */
    public void doLayout() {
        super.doLayout();
        GadgetShell shell = getShell();
        if (shell != null) {
            Gadget topGadget = shell.getGadget();
            topGadget.removeMouseMotionListener(this);
            topGadget.addMouseMotionListener(this);
        }
    }

    /**
     * Sets the help menu to the specified menu on the menu bar.
     * @param m the menu to be set
     */
    public void setHelpMenu(MenuGadget m) {
	    synchronized(getTreeLock()) {
        	if (helpMenu == m) {
        	    return;
        	}
        	if (helpMenu != null) {
        	    helpMenu.parent = null;
        	}
        	if (m.parent != this) {
        	    add(m);
        	}
        	helpMenu = m;
        	if (m != null) {
        	    m.isHelpMenu = true;
        	    m.parent = this;
        	}
        }
    }

    /**
     * MenuGadget
     * @param label	the label of the MenuGadget to be added
     * @return  the MenuGadget being added
     */
    public MenuGadget add(String label) {
        MenuGadget menu = new MenuGadget(label);
        add(menu);
        return menu;
    }

    /**
     * add
     * @param menu 	the MenuGadget to be added
     * @return  the MenuGadget being added
     */
    public MenuGadget add(MenuGadget menu) {
        add((Gadget)menu);
        return menu;
    }

    /**
     * Counts the number of menus on the menu bar.
     * @return getGadgetCount()
     */
    public int getMenuCount() {
    	return getGadgetCount();
    }

    /**
     * Gets the specified menu.
     * @param i		the index of the desired menu
	 * @return 		the menu at the specified index
     */
    public MenuGadget getMenu(int i) {
    	return (MenuGadget)getGadget(i);
    }

    /**
     * mouseDragged
     * @param e		the firing MouseEvent
     */
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    /**
     * getVisibleMenu
     * @return the visible MenuGadget, else null
     */
    public MenuGadget getVisibleMenu() {
        Gadget[] gadgets = getGadgets();
        for (int i = 0; i < gadgets.length; i++) {
            MenuGadget menu = (MenuGadget)gadgets[i];
            PopupMenuGadget popup = menu.getPopup();
            if (popup.isVisible()&&popup.getShell() != null) {
                return menu;
            }
        }
        return null;
    }

    /**
     * nextMenu
     * @param cur	the MenuGadget whose successor is to be shown
     */
    public void nextMenu(MenuGadget cur) {
        Gadget[] gadgets = getGadgets();
        boolean found = false;
        if (gadgets[gadgets.length-1] == cur) {
            found = true;
        }
        for (int i = 0; i < gadgets.length; i++) {
            MenuGadget menu = (MenuGadget)gadgets[i];
            if (found) {
                menu.showMenu();
                menu.requestFocus();
                break;
            }
            if (menu == cur) {
                found = true;
            }
        }
    }

    /**
     * prevMenu
     * @param cur	the MenuGadget whose predacessor is to be shown
     */
    public void prevMenu(MenuGadget cur) {
        Gadget[] gadgets = getGadgets();
        boolean found = false;
        if (gadgets[0] == cur) {
            found = true;
        }
        for (int i = gadgets.length-1; i >= 0; i--) {
            MenuGadget menu = (MenuGadget)gadgets[i];
            if (found) {
                menu.showMenu();
                menu.requestFocus();
                break;
            }
            if (menu == cur) {
                found = true;
            }
        }
    }

    /**
     * mouseMoved
     * @param e	the firing MouseEvent
     */
    public void mouseMoved(MouseEvent e) {
        MenuGadget vismenu = getVisibleMenu();
        if (getVisibleMenu() != null ) {
            int mouseX = e.getX();
            int mouseY = e.getY();
            Point mbOffset = getOffset();
            if (contains(mouseX-mbOffset.x,mouseY-mbOffset.y)) {
                if (!vismenu.contains(mouseX-(mbOffset.x+vismenu.x),mouseY-(mbOffset.y+vismenu.y))) {
                    Gadget[] gadgets = getGadgets();
                    for (int i = 0; i < gadgets.length; i++) {
                        MenuGadget menu = (MenuGadget)gadgets[i];
                        if (menu.contains(mouseX-(mbOffset.x+menu.x),mouseY-(mbOffset.y+menu.y))) {
                            vismenu.hideMenu();
                            menu.showMenu();
                        }
                    }
                }
            }
        }
    }
}
