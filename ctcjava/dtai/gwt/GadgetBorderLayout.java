/****************************************************************
 **
 **  $Id: GadgetBorderLayout.java,v 1.16 1997/08/25 03:38:55 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/GadgetBorderLayout.java,v $
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
/* DERIVED FROM...
 *
 * @(#)GridBagConstraints.java	1.5 95/12/01 Doug Stein
 *
 * Copyright (c) 1995 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

package dtai.gwt;

import java.awt.Dimension;
import java.awt.Insets;

/**
 * A TNT style border bag layout. It will layout a container
 * using members named "North", "South", "East", "West" and
 * "Center".
 *
 * The "North", "South", "East" and "West" components get layed out
 * according to their preferred sizes and the constraints of the
 * container's size. The "Center" component will get any space left
 * over.
 * @version 1.1
 * @author   DTAI, Incorporated
 */
public class GadgetBorderLayout implements GadgetLayoutManager {
    int hgap = 0;
    int vgap = 0;

    Gadget north;
    Gadget west;
    Gadget east;
    Gadget south;
    Gadget center;

    /**
     * Constructs a new BorderLayout.
     */
    public GadgetBorderLayout() {
	}

	/**
     * returns true if children can overlap each other.
     *
     * @return false
	 */
	public boolean childrenCanOverlap() {
	    return false;
    }

    /**
     * Constructs a BorderLayout with the specified gaps.
     * @param hgap the horizontal gap
     * @param vgap the vertical gap
     */
    public GadgetBorderLayout(int hgap, int vgap) {
    	this.hgap = hgap;
    	this.vgap = vgap;
    }

    /**
     * Adds the specified named component to the layout.
     * @param name the String name
     * @param gadget the component to be added
     */
    public void addLayoutGadget(String name, Gadget gadget) {
    	if ("Center".equals(name)) {
    	    center = gadget;
    	} else if ("North".equals(name)) {
    	    north = gadget;
    	} else if ("South".equals(name)) {
    	    south = gadget;
    	} else if ("East".equals(name)) {
    	    east = gadget;
    	} else if ("West".equals(name)) {
    	    west = gadget;
    	}
    }

    /**
     * Removes the specified component from the layout.
     * @param gadget the component to be removed
     */
    public void removeLayoutGadget(Gadget gadget) {
    	if (gadget == center) {
    	    center = null;
    	} else if (gadget == north) {
    	    north = null;
    	} else if (gadget == south) {
    	    south = null;
    	} else if (gadget == east) {
    	    east = null;
    	} else if (gadget == west) {
    	    west = null;
    	}
    }

    /**
     * Returns the minimum dimensions needed to layout the components
     * contained in the specified target container.
     * @param target the ContainerGadget on which to do the layout
     * @see ContainerGadget
     * @see #preferredLayoutSize
     * @return Dimension
     */
    public Dimension minimumLayoutSize(ContainerGadget target) {
    	Dimension dim = new Dimension(0, 0);

    	if ((east != null) && east.visible) {
    	    Dimension d = east.getMinimumSize();
    	    dim.width += d.width + hgap;
    	    dim.height = Math.max(d.height, dim.height);
    	}
    	if ((west != null) && west.visible) {
    	    Dimension d = west.getMinimumSize();
    	    dim.width += d.width + hgap;
    	    dim.height = Math.max(d.height, dim.height);
    	}
    	if ((center != null) && center.visible) {
    	    Dimension d = center.getMinimumSize();
    	    dim.width += d.width;
    	    dim.height = Math.max(d.height, dim.height);
    	}
    	if ((north != null) && north.visible) {
    	    Dimension d = north.getMinimumSize();
    	    dim.width = Math.max(d.width, dim.width);
    	    dim.height += d.height + vgap;
    	}
    	if ((south != null) && south.visible) {
    	    Dimension d = south.getMinimumSize();
    	    dim.width = Math.max(d.width, dim.width);
    	    dim.height += d.height + vgap;
    	}

    	Insets insets = target.getInsets();
    	dim.width += insets.left + insets.right;
    	dim.height += insets.top + insets.bottom;

    	return dim;
    }

    /**
     * Returns the preferred dimensions for this layout given the components
     * in the specified target container.
     * @param target the component which needs to be laid out
     * @see ContainerGadget
     * @see #minimumLayoutSize
     * @return Dimension
     */
    public Dimension preferredLayoutSize(ContainerGadget target) {
    	Dimension dim = new Dimension(0, 0);

    	int childWidth;
    	int childHeight;
    	if ((east != null) && east.visible) {
    	    childWidth = east.pref_width;
    	    childHeight = east.pref_height;
    	    if ( childWidth < 0 || childHeight < 0 ) {
        	    Dimension d = east.getPreferredSize();
        	    childWidth = d.width;
        	    childHeight = d.height;
    	    }
    	    dim.width += childWidth + hgap;
    	    dim.height = Math.max(childHeight, dim.height);
    	}
    	if ((west != null) && west.visible) {
    	    childWidth = west.pref_width;
    	    childHeight = west.pref_height;
    	    if ( childWidth < 0 || childHeight < 0 ) {
        	    Dimension d = west.getPreferredSize();
        	    childWidth = d.width;
        	    childHeight = d.height;
    	    }
    	    dim.width += childWidth + hgap;
    	    dim.height = Math.max(childHeight, dim.height);
    	}
    	if ((center != null) && center.visible) {
    	    childWidth = center.pref_width;
    	    childHeight = center.pref_height;
    	    if ( childWidth < 0 || childHeight < 0 ) {
        	    Dimension d = center.getPreferredSize();
        	    childWidth = d.width;
        	    childHeight = d.height;
    	    }
    	    dim.width += childWidth;
    	    dim.height = Math.max(childHeight, dim.height);
    	}
    	if ((north != null) && north.visible) {
    	    childWidth = north.pref_width;
    	    childHeight = north.pref_height;
    	    if ( childWidth < 0 || childHeight < 0 ) {
        	    Dimension d = north.getPreferredSize();
        	    childWidth = d.width;
        	    childHeight = d.height;
    	    }
    	    dim.width = Math.max(childWidth, dim.width);
    	    dim.height += childHeight + vgap;
    	}
    	if ((south != null) && south.visible) {
    	    childWidth = south.pref_width;
    	    childHeight = south.pref_height;
    	    if ( childWidth < 0 || childHeight < 0 ) {
        	    Dimension d = south.getPreferredSize();
        	    childWidth = d.width;
        	    childHeight = d.height;
    	    }
    	    dim.width = Math.max(childWidth, dim.width);
    	    dim.height += childHeight + vgap;
    	}

    	Insets insets = target.getInsets();
    	dim.width += insets.left + insets.right;
    	dim.height += insets.top + insets.bottom;
    	return dim;
    }

    /**
     * Lays out the specified container. This method will actually setBounds the
     * components in the specified target container in order to satisfy the
     * constraints of the BorderLayout object.
     * @param target the component being laid out
     * @see ContainerGadget
     */
    public void layoutContainerGadget(ContainerGadget target) {
    	Insets insets = target.getInsets();
    	int top = insets.top;
    	int bottom = target.height - insets.bottom;
    	int left = insets.left;
    	int right = target.width - insets.right;

    	int childWidth;
    	int childHeight;

    	if ((north != null) && north.visible) {
    	    childHeight = north.pref_height;
    	    if ( childHeight < 0 ) {
        	    Dimension d = north.getPreferredSize();
        	    childHeight = d.height;
    	    }
    	    north.setBounds(left, top, right - left, childHeight, false);
    	    top += childHeight + vgap;
    	}
    	if ((south != null) && south.visible) {
    	    childHeight = south.pref_height;
    	    if ( childHeight < 0 ) {
        	    Dimension d = south.getPreferredSize();
        	    childHeight = d.height;
    	    }
    	    south.setBounds(left, bottom - childHeight, right - left, childHeight, false);
    	    bottom -= childHeight + vgap;
    	}
    	if ((east != null) && east.visible) {
    	    childHeight = east.pref_height;
    	    childWidth = east.pref_width;
    	    if ( childHeight < 0 || childWidth < 0 ) {
        	    Dimension d = east.getPreferredSize();
        	    childHeight = d.height;
        	    childWidth = d.width;
    	    }
    	    east.setBounds(right - childWidth, top, childWidth, bottom - top, false);
    	    right -= childWidth + hgap;
    	}
    	if ((west != null) && west.visible) {
    	    childHeight = west.pref_height;
    	    childWidth = west.pref_width;
    	    if ( childHeight < 0 || childWidth < 0 ) {
        	    Dimension d = west.getPreferredSize();
        	    childHeight = d.height;
        	    childWidth = d.width;
    	    }
    	    west.setBounds(left, top, childWidth, bottom - top, false);
    	    left += childWidth + hgap;
    	}
    	if ((center != null) && center.visible) {
    	    center.setBounds(left, top, right - left, bottom - top, false);
    	}
    }
}
