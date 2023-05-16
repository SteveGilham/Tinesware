/****************************************************************
 **
 **  $Id: GadgetManager.java,v 1.13 1997/08/06 23:27:05 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/GadgetManager.java,v $
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

public interface GadgetManager {

    /**
     * Returns the panel in which overlays (menus, tips) are to be drawn.
     * @see #getGadget
     * @return OverlayPanelGadget
     */
    public OverlayPanelGadget getOverlayPanel();

    /**
     * Returns the panel in which all other gadgets (non-overlays) are to be drawn.
     * @see #getGadget
     * @return PanelGadget
     */
    public PanelGadget getMainPanel();

    /**
     * Returns the number of gadgets in this panel.
     * @see #getGadget
     * @return int
     */
    public int getGadgetCount();

    /**
     * Gets the nth gadget in this container.
     * @param n the number of the gadget to get
     * @exception ArrayIndexOutOfBoundsException If the nth value does not
     * exist.
     * @return Gadget
     */
    public Gadget getGadget(int n);

    /**
     * Gets all the gadgets in this container.
     * @return Gadget[]
     */
    public Gadget[] getGadgets();

    /**
     * Adds the specified gadget to this container.
     * @param gadget the gadget to be added
     * @return Gadget
     */
    public Gadget add(Gadget gadget);

    /**
     * Adds the specified gadget to this container at the given position.
     * @param gadget the gadget to be added
     * @param pos the position at which to insert the gadget. -1
     * means insert at the end.
     * @see #remove
     * @return Gadget
     */
    public Gadget add(Gadget gadget, int pos);

    /**
     * Adds the specified gadget to this container. The gadget
     * is also added to the layout manager of this container using the
     * name specified.
     * @param name the gadget name
     * @param gadget the gadget to be added
     * @see #remove
     * @see GadgetLayoutManager
     * @return Gadget
     */
    public Gadget add(String name, Gadget gadget);

    /**
     * Removes the specified gadget from this container.
     * @param gadget the gadget to be removed
     * @see #add
     */
    public void remove(Gadget gadget);

    /**
     * Sets the layout manager for this container.
     * @param mgr the specified layout manager
     * @see dtai.gwt.ContainerGadget#layout
     * @see dtai.gwt.ContainerGadget#getLayout
     */
    public void setLayout(GadgetLayoutManager mgr);

    /**
     * Sets the gadget help for this container.
     * @param gadgetHelp	new GadgetHelp for this container
     */
    public void setGadgetHelp(GadgetHelp gadgetHelp);

    /**
     * Locates the gadget that contains the x,y position.
     * @param x the x coordinate
     * @param y the y coordinate
     * @return null if the gadget is not within the x and y
     * coordinates; returns the gadget otherwise.
     * @see Gadget#contains
     */
    public Gadget getGadgetAt(int x, int y);

	/**
	 * Locates the gadget that contains the specified point.
	 * @param p 		the point
	 * @return  null if the gadget does not contain the point;
	 * returns the gadget otherwise.
	 * @see Gadget#contains
	 */
    public Gadget getGadgetAt(Point p);
}
