/****************************************************************
 **
 **  $Id: GadgetLayoutManager.java,v 1.7 1997/08/25 03:38:56 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/GadgetLayoutManager.java,v $
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

import java.awt.Dimension;

/**
 * Defines the interface for classes that know how to layout GadgetContainers.
 *
 * @see ContainerGadget
 */
public interface GadgetLayoutManager {
	/**
     * returns true if children can overlap each other.
     * @return false
	 */
	public boolean childrenCanOverlap();

    /**
     * Adds the specified gadget with the specified name to
     * the layout.
     * @param name the gadget name
     * @param gadget the gadget to be added
     */
    void addLayoutGadget(String name, Gadget gadget);

    /**
     * Removes the specified gadget from the layout.
     * @param gadget the gadget ot be removed
     */
    void removeLayoutGadget(Gadget gadget);

    /**
     * Calculates the preferred size dimensions for the specified
     * panel given the gadgets in the specified parent container.
     * @param parent the gadget to be laid out
     *
     * @see #minimumLayoutSize
     */
    Dimension preferredLayoutSize(ContainerGadget parent);

    /**
     * Calculates the minimum size dimensions for the specified
     * panel given the gadgets in the specified parent container.
     * @param parent the gadget to be laid out
     * @see #preferredLayoutSize
     */
    Dimension minimumLayoutSize(ContainerGadget parent);

    /**
     * Lays out the container in the specified panel.
     * @param parent the gadget which needs to be laid out
     */
    void layoutContainerGadget(ContainerGadget parent);
}
