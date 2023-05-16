/****************************************************************
 **
 **  $Id: PopupMenuGadget.java,v 1.34 1998/02/04 16:48:58 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/PopupMenuGadget.java,v $
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
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.util.Vector;
import java11.awt.event.MouseEvent;
import java11.awt.event.KeyEvent;
import java11.awt.event.MouseListener;
import java11.util.EventObject;


/**
 * PopupMenuGadget
 * @version 1.1
 * @author DTAI, Incorporated
 */
public class PopupMenuGadget extends BorderGadget implements MouseListener {

    Vector menuItems = new Vector();
    int rows=1;

    String label;
    boolean tearOff;
    Gadget poppedUpFrom;

    /**
     * PopupMenuGadget
     * @param label	description
     * @param tearOff	description
     */
    public PopupMenuGadget(String label, boolean tearOff) {
        this.tearOff = tearOff;
        this.label = label;

        setBorderType(BorderGadget.THREED_OUT);
        setMargins(0,1,0,0);
        setBorderThickness(2);
    }

    /**
     * PopupMenuGadget
     */
    public PopupMenuGadget() {
        this("", false);
    }

    /**
     * PopupMenuGadget
     * @param label	description
     */
    public PopupMenuGadget(String label) {
        this(label, false);
    }

    /**
     * doLayout
     */
    public void doLayout() {
        calcLayout();
    }

    /**
     * calcLayout
     * @return Dimension
     */
    public Dimension calcLayout() {

        int calc_width = 0;
        int calc_height = 0;

        calc_width = findMaxWidth();

        Insets insets = getInsets();

        int top = insets.top;
		for (int i=0; i<menuItems.size(); i++) {
		    Gadget g = (Gadget) menuItems.elementAt(i);
		    if ( g.isVisible() ) {
    		    Dimension prefSize = g.getPreferredSize();
    		    g.setBounds(insets.left, top , calc_width, prefSize.height,false);
                top += (prefSize.height);
    	        calc_height += (prefSize.height);
    	    }
	     }
         return new Dimension(calc_width+insets.left+insets.right,
                              calc_height+insets.top+insets.bottom);
    }

    private int findMaxWidth() {

        int calc_width = 0;
        for (int i=0; i<menuItems.size(); i++) {
		    Gadget g = (Gadget) menuItems.elementAt(i);
            calc_width = Math.max(calc_width, g.getPreferredSize().width);
        }
        return calc_width;

    }

    /**
     * add
     * @param label	description
     * @return MenuItemGadget
     */
    public MenuItemGadget add(String label) {
        MenuItemGadget item = new MenuItemGadget(label);
        add(item);
        return item;
    }

    /**
     * MenuItemGadget
     * @param item	description
     * @return MenuItemGadget
     */
    public MenuItemGadget add(MenuItemGadget item) {

        menuItems.addElement(item);
        item.addMouseListener(this);
        super.add(item);
        item.invalidate();
        return item;
    }

    /**
     * SeparatorGadget
     * @param separator	description
     * @return SeparatorGadget
     */
    public SeparatorGadget add(SeparatorGadget separator) {
        separator.setVMargin(3);
        menuItems.addElement(separator);
        super.add(separator);
        separator.invalidate();
        return separator;
    }

    /**
     * addSeparator
     */
    public SeparatorGadget addSeparator() {
        SeparatorGadget sep = new SeparatorGadget();
        add( sep );
        return sep;
    }

	/**
	 * gets the preferred size
	 * @return Dimension
	 */
	public Dimension getPreferredSize() {
	    if ( valid && pref_width >= 0 && pref_height >= 0 ) {
    	    return new Dimension( pref_width, pref_height );
	    }
	    return calcLayout();
    }

    /**
     * getItem
     * @param pos	description
     * @return MenuItemGadget
     */
    public MenuItemGadget getItem(int pos) {
        return (MenuItemGadget)getGadget(pos);
    }

    /**
     * getItemCount
     * @return int
     */
    public int getItemCount() {
        return 0;
    }

    /**
     * insert
     * @param item	description
     * @param pos	description
     */
    public void insert(String item, int pos) {

    }

    /**
     * inserts a separator
     * @param pos	description
     */
    public void insertSeparator(int pos) {

    }

    /**
     * paramString
     * @return String
     */
    public String paramString() {
        return label;
    }

    /**
     * remove
     * @param pos	description
     */
    public void remove(int pos) {
        super.remove( pos );
    }

    /**
     * removeAll
     */
    public void removeAll() {
        for( int i = getGadgetCount() - 1; i >= 0; i--) {
            remove( i );
            menuItems.removeElementAt( i );
        }
    }

    /**
     * isTearOff
     * @return boolean
     */
    public boolean isTearOff() {
        return tearOff;
    }


    /**
     * mouseClicked
     * @param e	description
     */
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * mousePressed
     * @param e	description
     */
    public void mousePressed(MouseEvent e) {

    }

    /**
     * mouseReleased
     * @param e	description
     */
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * mouseEntered
     * @param e	description
     */
    public void mouseEntered(MouseEvent e) {
        ((Gadget) e.getSource()).setSelected(true);
    }

    /**
     * mouseExited
     * @param e	description
     */
    public void mouseExited(MouseEvent e) {
        ((Gadget) e.getSource()).setSelected(false);
	}


	/**
	 * Sets the parent of the component.
	 * @param parent	ContainerGadgaet
	 * @param invalidateParent - TBD
	 */
	protected void setParent( ContainerGadget parent, boolean invalidateParent) {
	    GadgetShell shell = getShell();
	    super.setParent(parent,invalidateParent);
	    if (shell == null) {
	        shell = getShell();
	    }
	    if (shell != null) {
    	    if (parent == null ) {
    		    shell.setWaitingForMouseUp(false);
    	    } else {
    		    shell.setWaitingForMouseUp(true);
    	    }
    	}
    }

    /**
     * showMenu
     * @param x	description
     * @param y	description
     * @param parent	description
     */
    public void showMenu(int x, int y, Gadget parent) {
		synchronized(getTreeLock()) {
            poppedUpFrom = parent;
            Color newbg = parent.getFinalNormalBackground();
            Gadget p = parent;
            while (p != null) {
                if (p instanceof ImagePanelGadget &&
                    ((ImagePanelGadget)p).getImage() != null) {
                    newbg = Gadget.defaultBackground;
                }
                Color pbg = p.getNormalBackgroundSetting();
                if (pbg != Gadget.transparent && pbg != null) {
                    break;
                }
                p = p.getParent();
            }
            setBackground(newbg);
            setFont(parent.getFont());
            Dimension pref = getPreferredSize();
    	    setBounds(parent.getOffset().x+x, parent.getOffset().y+y,pref.width,pref.height);
    	    clearAll();
    	    parent.getShell().disableTips();
    		parent.getOverlayPanel().add(this);
            parent.getOverlayPanel().setConsumingTransparentClicks(true);
            parent.getOverlayPanel().invalidate();
        }
	}

	/**
	 * returns the Gadget from which the menu was popped up
	 * @return the gadget passed to showMenu
	 */
	public Gadget getPoppedUpFrom() {
	    return poppedUpFrom;
	}

    /**
     * showMenu
     * @param e	description
     */
    public void showMenu(MouseEvent e) {
		synchronized(getTreeLock()) {
            showMenu(e.x, e.y, (Gadget) e.getSource());
        }
	}

	/**
	 * hideMenu
	 */
	public void hideMenu() {
	    GadgetShell shell = getShell();
	    if (shell != null) {
//    	    shell.enableTips();
    		parent.getOverlayPanel().remove(this);
            clearAll();
            shell.giveFocusTo(shell.getDefaultFocusGadget());
    	}
	}

    /**
     * clearAll
     */
    private void clearAll() {
        for (int i=0; i<menuItems.size(); i++) {
		    ((Gadget) menuItems.elementAt(i)).setSelected(false);
        }
    }

    /**
     * processKeyEvent
     * @param e	description
     */
    protected void processKeyEvent(KeyEvent e) {
    	if ( e.getID() == KeyEvent.KEY_PRESSED &&
    	     e.getKeyCode() == KeyEvent.ESCAPE ) {
    	    hideMenu();
    	    e.consume();
    	}
	    super.processKeyEvent( e );
	}
}
