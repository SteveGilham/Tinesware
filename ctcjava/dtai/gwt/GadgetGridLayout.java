/****************************************************************
 **
 **  $Id: GadgetGridLayout.java,v 1.14 1997/08/25 03:38:56 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/GadgetGridLayout.java,v $
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
 * A layout manager for a container that lays out grids.
 * @version 1.1
 * @author DTAI, Incorporated
 */
public class GadgetGridLayout implements GadgetLayoutManager {
    int hgap;
    int vgap;
    int rows;
    int cols;
    int minWidth;
    int minHeight;

    /**
     * Creates a grid layout with the specified rows and columns.
     * @param rows the rows
     * @param cols the columns
     */
    public GadgetGridLayout(int rows, int cols) {
    	this(rows, cols, 0, 0, 0, 0);
    }

    /**
     * Creates a grid layout with the specified rows, columns,
     * horizontal gap, and vertical gap.
     * @param rows the rows; zero means 'any number.'
     * @param cols the columns; zero means 'any number.'  Only one of 'rows'
     * and 'cols' can be zero, not both.
     * @param hgap the horizontal gap variable
     * @param vgap the vertical gap variable
     * @exception IllegalArgumentException If the rows and columns are invalid.
     */
    public GadgetGridLayout(int rows, int cols, int hgap, int vgap) {
    	this( rows, cols, hgap, vgap, 0, 0 );
    }

    /**
     * Creates a grid layout with the specified rows, columns,
     * horizontal gap, vertical gap, and minimum width and height.
     * @param rows the rows; zero means 'any number.'
     * @param cols the columns; zero means 'any number.'  Only one of 'rows'
     * and 'cols' can be zero, not both.
     * @param hgap the horizontal gap variable
     * @param vgap the vertical gap variable
     * @param minWidth the minimum width of any child
     * @param minHeight the minimum height of any child
     * @exception IllegalArgumentException If the rows and columns are invalid.
     */
    public GadgetGridLayout( int rows, int cols, int hgap, int vgap,
                             int minWidth, int minHeight ) {
    	if ((rows == 0) && (cols == 0)) {
    	    throw new IllegalArgumentException("invalid rows,cols");
    	}

    	this.rows = rows;
    	this.cols = cols;
    	this.hgap = hgap;
    	this.vgap = vgap;
    	this.minWidth = minWidth;
    	this.minHeight = minHeight;
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
     * Adds the specified gadget with the specified name to the layout.
     * @param name the name of the gadget
     * @param gadget the gadget to be added
     */
    public void addLayoutGadget(String name, Gadget gadget) {
    }

    /**
     * Removes the specified gadget from the layout. Does not apply.
     * @param gadget the gadget to be removed
     */
    public void removeLayoutGadget(Gadget gadget) {
    }

    /**
     * Returns the preferred dimensions for this layout given the gadgets
     * int the specified panel.
     * @param parent the gadget which needs to be laid out
     * @see #minimumLayoutSize
     * @return Dimension
     */
    public Dimension preferredLayoutSize(ContainerGadget parent) {
    	Insets insets = parent.getInsets();
    	int ngadgets = parent.getGadgetCount();
    	int nrows = rows;
    	int ncols = cols;
    	int nvisgadgets = 0;

    	for (int i = 0 ; i < ngadgets ; i++) {
    	    Gadget gadget = parent.getGadget(i);
    	    if ( gadget.isVisible() ) {
    	        nvisgadgets++;
    	    }
    	}

    	if (nrows > 0) {
    	    ncols = (nvisgadgets + nrows - 1) / nrows;
    	} else {
    	    nrows = (nvisgadgets + ncols - 1) / ncols;
    	}
    	int w = minWidth;
    	int h = minHeight;
    	for (int i = 0 ; i < ngadgets ; i++) {
    	    Gadget gadget = parent.getGadget(i);
    	    if ( gadget.isVisible() ) {
    	        int childWidth = gadget.pref_width;
    	        int childHeight = gadget.pref_height;
    	        if ( childWidth < 0 || childHeight < 0 ) {
            		Dimension d = gadget.getPreferredSize();
            		childWidth = d.width;
            		childHeight = d.height;
            	}
        	    if (w < childWidth) {
            		w = childWidth;
        	    }
        	    if (h < childHeight) {
            		h = childHeight;
        	    }
        	}
    	}
    	return new Dimension(insets.left + insets.right + ncols*w + (ncols-1)*hgap,
    			     insets.top + insets.bottom + nrows*h + (nrows-1)*vgap);
    }

    /**
     * Returns the minimum dimensions needed to layout the gadgets
     * contained in the specified panel.
     * @param parent the gadget which needs to be laid out
     * @see #preferredLayoutSize
     * @return Dimension
     */
    public Dimension minimumLayoutSize(ContainerGadget parent) {
    	Insets insets = parent.getInsets();
    	int ngadgets = parent.getGadgetCount();
    	int nrows = rows;
    	int ncols = cols;

    	int nvisgadgets = 0;

    	for (int i = 0 ; i < ngadgets ; i++) {
    	    Gadget gadget = parent.getGadget(i);
    	    if ( gadget.isVisible() ) {
    	        nvisgadgets++;
    	    }
    	}

    	if (nrows > 0) {
    	    ncols = (nvisgadgets + nrows - 1) / nrows;
    	} else {
    	    nrows = (nvisgadgets + ncols - 1) / ncols;
    	}
    	int w = minWidth;
    	int h = minHeight;
    	for (int i = 0 ; i < ngadgets ; i++) {
    	    Gadget gadget = parent.getGadget(i);
    	    if ( gadget.isVisible() ) {
        	    Dimension d = gadget.getMinimumSize();
        	    if (w < d.width) {
            		w = d.width;
        	    }
        	    if (h < d.height) {
            		h = d.height;
        	    }
        	}
    	}

    	return new Dimension(insets.left + insets.right + ncols*w + (ncols-1)*hgap,
    			     insets.top + insets.bottom + nrows*h + (nrows-1)*vgap);
    }

    /**
     * Lays out the container in the specified panel.
     * @param parent the specified gadget being laid out
     * @see ContainerGadget
     */
    public void layoutContainerGadget(ContainerGadget parent) {
    	Insets insets = parent.getInsets();
    	int ngadgets = parent.getGadgetCount();
    	int nrows = rows;
    	int ncols = cols;

    	int nvisgadgets = 0;

    	for (int i = 0 ; i < ngadgets ; i++) {
    	    Gadget gadget = parent.getGadget(i);
    	    if ( gadget.isVisible() ) {
    	        nvisgadgets++;
    	    }
    	}

    	if (ngadgets == 0) {
    	    return;
    	}
    	if (nrows > 0) {
    	    ncols = (nvisgadgets + nrows - 1) / nrows;
    	} else {
    	    nrows = (nvisgadgets + ncols - 1) / ncols;
    	}
    	int w = parent.width - (insets.left + insets.right);
    	int h = parent.height - (insets.top + insets.bottom);
    	w = (w - (ncols - 1) * hgap) / ncols;
    	h = (h - (nrows - 1) * vgap) / nrows;

    	for (int c = 0, x = insets.left ; c < ncols ; c++, x += w + hgap) {
    	    for (int r = 0, y = insets.top ; r < nrows ; r++, y += h + vgap) {
        		int countdown = r * ncols + c;
        		int i = 0;
            	for (i = 0 ; i < ngadgets ; i++) {
            	    Gadget gadget = parent.getGadget(i);
            	    if ( gadget.isVisible() ) {
            	        countdown--;
            	        if ( countdown < 0 ) {
            	            break;
            	        }
            	    }
            	}
        		if (i < ngadgets) {
        		    parent.getGadget(i).setBounds(x, y, w, h, false);
        		}
    	    }
    	}
    }

    /**
     * Returns the String representation of this GadgetGridLayout's values.
     * @return String
     */
    public String toString() {
    	return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap +
    	    			       ",rows=" + rows + ",cols=" + cols + "]";
    }
}
