/****************************************************************
 **
 **  $Id: GadgetPanel.java,v 1.19 1998/03/10 20:30:41 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/GadgetPanel.java,v $
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

import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.Point;

/**
 * GadgetPanel
 * @version 1.1
 * @author DTAI, Incorporated
 */
public class GadgetPanel extends GadgetShell implements GadgetManager {

    private OverlayPanelGadget overlayPanel;
    private PanelGadget mainPanel;
    private boolean constructed = false;

    /**
     * Creates a new panel. The default layout for all panels is
     * FlowLayout.
     */
    public GadgetPanel() {

        // set up the CardLayout managed panel for the main gadgets and overlays

        PanelGadget cards = new PanelGadget();
        cards.setLayout( new GadgetCardLayout() );
        super.add( cards );

        // add the transparent panel for overlays first so it's on top

        cards.add( "overlayPanel", overlayPanel = new OverlayPanelGadget() );
        setOverlayPanel( overlayPanel );

        // add the main panel
        cards.add( "mainPanel", mainPanel = new PanelGadget() );

        // start error checking for invalid use by programmers

        constructed = true;
    }

    /**
     * Overridden to make it an error to add components to it.
     * @param comp the component to be added
     * @return Component
     */
/*
    public Component add(Component comp) {
        if ( ! constructed ) {
            return super.add( comp );
        }
        throw new IllegalArgumentException( "Programmer error. Invalid to add a Component to a GadgetApplet" );
    }
*/
    /**
     * Overridden to make it an error to add components to it.
     * @param comp the component to be added
     * @param pos the position at which to insert the component. -1
     * means insert at the end.
     * @see #remove
     * @return Component
     */
/*
    public synchronized Component add(Component comp, int pos) {
        if ( ! constructed ) {
            return super.add( comp, pos );
        }
        throw new IllegalArgumentException( "Programmer error. Invalid to add a Component to a GadgetApplet" );
    }
*/
    /**
     * Overridden to make it an error to add components to it.
     * @param name the component name
     * @param comp the component to be added
     * @see #remove
     * @see LayoutManager
     * @return Component
     */
/*
    public synchronized Component add(String name, Component comp) {
        if ( ! constructed ) {
            return super.add( name, comp );
        }
        throw new IllegalArgumentException( "Programmer error. Invalid to add a Component to a GadgetApplet" );
    }
*/
    /**
     * Overridden to make it an error to add a Component layout manager to it.
     * @param mgr the specified layout manager
     * @see dtai.gwt.ContainerGadget#layout
     * @see dtai.gwt.ContainerGadget#getLayout
     */
    public void setLayout(LayoutManager mgr) {
        if ( ! constructed ) {
            super.setLayout( mgr );
        }
        else {
            throw new IllegalArgumentException( "Programmer error. Invalid to add a Component layout manager to a GadgetApplet" );
        }
    }

    /**
     * Returns the panel in which overlays (menus, tips) are to be drawn.
     * @see #getGadget
     * @return PanelGadget
     */
    //public PanelGadget getOverlayPanel() {
    //    return overlayPanel;
    //}

    /**
     * Returns the panel in which all other gadgets (non-overlays) are to be drawn.
     * @see #getGadget
     * @return PanelGadget
     */
    public PanelGadget getMainPanel() {
        return mainPanel;
    }

   /**
     * Returns the number of gadgets in this panel.
     * @see #getGadget
     * @return int
     */
    public int getGadgetCount() {
	    return mainPanel.getGadgetCount();
    }

    /**
     * Gets the nth gadget in this container.
     * @param n the number of the gadget to get
     * @exception ArrayIndexOutOfBoundsException If the nth value does not
     * exist.
     * @return Gadget
     */
    public Gadget getGadget(int n) {
        synchronized(getGadgetTreeLock()) {
    	    return mainPanel.getGadget(n);
    	}
    }

    /**
     * Gets all the gadgets in this container.
     * @return Gadget[]
     */
    public Gadget[] getGadgets() {
        synchronized(getGadgetTreeLock()) {
    	    return mainPanel.getGadgets();
    	}
    }

    /**
     * Adds the specified gadget to this container.
     * @param gadget the gadget to be added
     * @return Gadget
     */
    public Gadget add(Gadget gadget) {
	    return mainPanel.add( gadget );
    }

    /**
     * Adds the specified gadget to this container at the given position.
     * @param gadget the gadget to be added
     * @param pos the position at which to insert the gadget. -1
     * means insert at the end.
     * @see #remove
     * @return Gadget
     */
    public Gadget add(Gadget gadget, int pos) {
        synchronized(getGadgetTreeLock()) {
    	    return mainPanel.add( gadget, pos );
    	}
    }

    /**
     * Adds the specified gadget to this container. The gadget
     * is also added to the layout manager of this container using the
     * name specified
.
     * @param name the gadget name
     * @param gadget the gadget to be added
     * @see #remove
     * @see GadgetLayoutManager
     * @return Gadget
     */
    public Gadget add(String name, Gadget gadget) {
        synchronized(getGadgetTreeLock()) {
    	    return mainPanel.add( name, gadget );
    	}
    }

    /**
     * Removes the specified gadget from this container.
     * @param gadget the gadget to be removed
     * @see #add
     */
    public void remove(Gadget gadget) {
        synchronized(getGadgetTreeLock()) {
    	    mainPanel.remove( gadget );
    	}
    }

    /**
     * Sets the gadget help for this container.
     * @param gadgetHelp
     */
    public void setGadgetHelp(GadgetHelp gadgetHelp) {
	    mainPanel.setGadgetHelp( gadgetHelp );
    }

    /**
     * Sets the layout manager for this container.
     * @param mgr the specified layout manager
     * @see dtai.gwt.ContainerGadget#layout
     * @see dtai.gwt.ContainerGadget#getLayout
     */
    public void setLayout(GadgetLayoutManager mgr) {
	    mainPanel.setLayout( mgr );
    }

    /**
     * Locates the gadget that contains the x,y position.
     * @param x the x coordinate
     * @param y the y coordinate
     * @return null if the gadget is not within the x and y
     * coordinates; returns the gadget otherwise.
     * @see Gadget#inside
     */
    public Gadget getGadgetAt(int x, int y) {
        synchronized(getGadgetTreeLock()) {
    	    return mainPanel.getGadgetAt( x, y );
    	}
    }

/**
 * Locates the gadget that contains the specified point.
 * Overrides:
 *        getGadgetAt in class Gadget
 * @param p - the point
 * @return null if the gadget does not contain the point; returns the gadget otherwise.
 * @see contains
 */

    public Gadget getGadgetAt(Point p) {
        return getGadgetAt( p.x, p.y );
    }
}
