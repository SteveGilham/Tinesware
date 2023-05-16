/****************************************************************
 **
 **  $Id: GadgetFlowLayout.java,v 1.15 1997/11/25 01:58:04 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/GadgetFlowLayout.java,v $
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
 * Flow layout is used to layout gadgets in a panel. It will arrange
 * gadgets left to right until no more gadgets fit on the same line.
 * Each line is centered.
 * @version 1.1
 * @author DTAI, Incorporated
 */
public class GadgetFlowLayout implements GadgetLayoutManager {

    /**
     * The left alignment variable.
     */
    public static final int LEFT 	= 0;

    /**
     * The center alignment variable.
     */
    public static final int CENTER 	= 1;

    /**
     * The right alignment variable.
     */
    public static final int RIGHT 	= 2;

    int align;
    int hgap;
    int vgap;

    /**
     * Constructs a new Flow Layout with a centered alignment.
     */
    public GadgetFlowLayout() {
	    this(CENTER, 5, 5);
    }

    /**
     * Constructs a new Flow Layout with the specified alignment.
     * @param align the alignment value
     */
    public GadgetFlowLayout(int align) {
	    this(align, 5, 5);
    }

    /**
     * Constructs a new Flow Layout with the specified alignment and gap
     * values.
     * @param align the alignment value
     * @param hgap the horizontal gap variable
     * @param vgap the vertical gap variable
     */
    public GadgetFlowLayout(int align, int hgap, int vgap) {
    	this.align = align;
    	this.hgap = hgap;
    	this.vgap = vgap;
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
     * Adds the specified component to the layout. Not used by this class.
     * @param name the name of the component
     * @param gadget the the component to be added
     */
    public void addLayoutGadget(String name, Gadget gadget) {
    }

    /**
     * Removes the specified component from the layout. Not used by
     * this class.
     * @param gadget the component to remove
     */
    public void removeLayoutGadget(Gadget gadget) {
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
    	int nmembers = target.getGadgetCount();

    	for (int i = 0 ; i < nmembers ; i++) {
    	    Gadget m = target.getGadget(i);
    	    if (m.visible) {
    	        int childWidth = m.pref_width;
    	        int childHeight = m.pref_height;
    	        if ( childWidth < 0 || childHeight < 0 ) {
            		Dimension d = m.getPreferredSize();
            		childWidth = d.width;
            		childHeight = d.height;
            	}
        		dim.height = Math.max(dim.height, childHeight);
        		if (i > 0) {
        		    dim.width += hgap;
        		}
        		dim.width += childWidth;
    	    }
    	}
    	Insets insets = target.getInsets();
    	dim.width += insets.left + insets.right + hgap*2;
    	dim.height += insets.top + insets.bottom + vgap*2;
    	return dim;
    }

    /**
     * Returns the minimum dimensions needed to layout the components
     * contained in the specified target container.
     * @param target the component which needs to be laid out
     * @see #preferredLayoutSize
     * @return Dimension
     */
    public Dimension minimumLayoutSize(ContainerGadget target) {
    	Dimension dim = new Dimension(0, 0);
    	int nmembers = target.getGadgetCount();

    	for (int i = 0 ; i < nmembers ; i++) {
    	    Gadget m = target.getGadget(i);
    	    if (m.visible) {
        		Dimension d = m.getMinimumSize();
        		dim.height = Math.max(dim.height, d.height);
        		if (i > 0) {
        		    dim.width += hgap;
        		}
        		dim.width += d.width;
    	    }
    	}
    	Insets insets = target.getInsets();
    	dim.width += insets.left + insets.right + hgap*2;
    	dim.height += insets.top + insets.bottom + vgap*2;
    	return dim;
    }

    /**
     * Centers the elements in the specified row, if there is any slack.
     * @param target the component which needs to be moved
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width dimensions
     * @param height the height dimensions
     * @param rowStart the beginning of the row
     * @param rowEnd the the ending of the row
     */
    private void moveGadgets(ContainerGadget target, int x, int y, int width, int height, int rowStart, int rowEnd) {
    	switch (align) {
    	case LEFT:
    	    break;
    	case CENTER:
    	    x += width / 2;
    	    break;
    	case RIGHT:
    	    x += width;
    	    break;
    	}
    	for (int i = rowStart ; i < rowEnd ; i++) {
    	    Gadget m = target.getGadget(i);
    	    if (m.visible) {
        		m.setLocation(x, y + (height - m.height) / 2);
        		x += hgap + m.width;
    	    }
    	}
    }

    /**
     * Lays out the container. This method will actually reshape the
     * components in the target in order to satisfy the constraints of
     * the BorderLayout object.
     * @param target the specified component being laid out.
     * @see ContainerGadget
     */
    public void layoutContainerGadget(ContainerGadget target) {
    	Insets insets = target.getInsets();
    	int maxwidth = target.width - (insets.left + insets.right + hgap*2);
    	int nmembers = target.getGadgetCount();
    	int x = 0, y = insets.top + vgap;
    	int rowh = 0, start = 0;

    	for (int i = 0 ; i < nmembers ; i++) {
    	    Gadget m = target.getGadget(i);
    	    if (m.visible) {
    	        int childWidth = m.pref_width;
    	        int childHeight = m.pref_height;
    	        if ( childWidth < 0 || childHeight < 0 ) {
            		Dimension d = m.getPreferredSize();
            		childWidth = d.width;
            		childHeight = d.height;
            	}
        		m.setSize(childWidth, childHeight, false);

        		if ((x == 0) || ((x + childWidth) <= maxwidth)) {
        		    if (x > 0) {
            			x += hgap;
        		    }
        		    x += childWidth;
        		    rowh = Math.max(rowh, childHeight);
        		} else {
        		    moveGadgets(target, insets.left + hgap, y, maxwidth - x, rowh, start, i);
        		    x = childWidth;
        		    y += vgap + rowh;
        		    rowh = childHeight;
        		    start = i;
        		}
    	    }
    	}
    	moveGadgets(target, insets.left + hgap, y, maxwidth - x, rowh, start, nmembers);
    }

    /**
     * Returns the String representation of this GadgetFlowLayout's values.
     * @return String
     */
    public String toString() {
    	String str = "";
    	switch (align) {
    	  case LEFT:    str = ",align=left"; break;
    	  case CENTER:  str = ",align=center"; break;
    	  case RIGHT:   str = ",align=right"; break;
    	}
    	return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + str + "]";
    }
}
