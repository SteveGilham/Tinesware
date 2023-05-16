/****************************************************************
 **
 **  $Id: GadgetCardLayout.java,v 1.20 1997/11/12 18:33:41 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/GadgetCardLayout.java,v $
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
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * A layout manager for a container that contains several
 * 'cards'. Only one card is visible at a time,
 * allowing you to flip through the cards.
 * @version 1.1
 * @author   DTAI, Incorporated
 */

public class GadgetCardLayout implements GadgetLayoutManager {
    Hashtable tab = new Hashtable();
    int hgap;
    int vgap;
	boolean transparent = true;
    Gadget firstcard;
    Gadget lastcard;
	Gadget topGadget;

    /**
     * Creates a new opaque card layout.
     */
    public GadgetCardLayout() {
    	this(0, 0);
    }

    /**
     * Creates a card layout with the specified gaps.
     * @param hgap the horizontal gap
     * @param vgap the vertical gap
     */
    public GadgetCardLayout(int hgap, int vgap) {
    	this.hgap = hgap;
    	this.vgap = vgap;
    }

	/**
     * returns true if children can overlap each other.
     *
     * @return true
	 */
	public boolean childrenCanOverlap() {
	    return true;
    }

    /**
     * By default, a GadgetCardLayout allows multiple transparent cards to show
     * through each other.  Setting this to false will hide all but the top card,
     * even if the top card(s) is transparent.
     * @param transparent the transparency flag
     */

	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
	}

    private void setVisibilities( ContainerGadget parent ) {
        synchronized (parent.getTreeLock()) {
    	    boolean hide = false;

			int ngadgets = parent.getGadgetCount();
    	    for (int i = ngadgets-1 ; i >= 0 ; i--) {
        		Gadget gadget = parent.getGadget(i);
        		if ((!transparent) && (gadget != topGadget)) {
        		    gadget.setVisible( false );
    			}
    			else if (hide) {
        		    gadget.setVisible( false );
        		}
        		else {
        		    gadget.setVisible( true );
           		    if (!gadget.isTransparent()) {
            		    hide = true;
            		}
            	}
    	    }
    	}
    }


	/**
     * Adds the specified gadget with the specified name to the layout.
     * @param name the name of the gadget
     * @param gadget the gadget to be added
     */
    public void addLayoutGadget(String name, Gadget gadget) {
        ContainerGadget parent = gadget.getParent();
	    if ( tab.size() == 0 ) {
	        firstcard = gadget;
			topGadget = gadget;
	    }
	    else {
            int gadgetCount = parent.getGadgetCount();
	        if ( gadget == parent.gadgets[gadgetCount-1] ) {
    		    Gadget newgadgets[] = new Gadget[parent.gadgets.length];
    		    System.arraycopy( parent.gadgets, 0, newgadgets, 1, gadgetCount - 1 );
    		    newgadgets[0] = gadget;
      		    parent.gadgets = newgadgets;
      		}
  		}
	    lastcard = gadget;
    	tab.put(name, gadget);
		setVisibilities( parent );
    }

    /**
     * Removes the specified gadget from the layout.
     * @param gadget the gadget to be removed
     */
    public void removeLayoutGadget(Gadget gadget) {
    	for (Enumeration e = tab.keys() ; e.hasMoreElements() ; ) {
    	    String key = (String)e.nextElement();
    	    if (tab.get(key) == gadget) {
        		tab.remove(key);
        	    setVisibilities( gadget.getParent() );
        		return;
    	    }
    	}
    }

    /**
     * Calculates the preferred size for the specified panel.
     * @param parent the name of the parent container
     * @return the dimensions of this panel.
     * @see #minimumLayoutSize
     */
    public Dimension preferredLayoutSize(ContainerGadget parent) {
    	Insets insets = parent.getInsets();
    	int ngadgets = parent.getGadgetCount();
    	int w = 0;
    	int h = 0;

    	for (int i = 0 ; i < ngadgets ; i++) {
    	    Gadget gadget = parent.getGadget(i);
	        int childWidth = gadget.pref_width;
	        int childHeight = gadget.pref_height;
	        if ( childWidth < 0 || childHeight < 0 ) {
        		Dimension d = gadget.getPreferredSize();
        		childWidth = d.width;
        		childHeight = d.height;
        	}
    	    if (childWidth > w) {
        		w = childWidth;
    	    }
    	    if (childHeight > h) {
        		h = childHeight;
    	    }
    	}
    	return new Dimension(insets.left + insets.right + w + hgap*2,
    			     insets.top + insets.bottom + h + vgap*2);
    }

    /**
     * Calculates the minimum size for the specified panel.
     * @param parent the name of the parent container
     * @return the dimensions of this panel.
     * @see #preferredLayoutSize
     */
    public Dimension minimumLayoutSize(ContainerGadget parent) {
    	Insets insets = parent.getInsets();
    	int ngadgets = parent.getGadgetCount();
    	int w = 0;
    	int h = 0;

    	for (int i = 0 ; i < ngadgets ; i++) {
    	    Gadget gadget = parent.getGadget(i);
    	    Dimension d = gadget.getMinimumSize();
    	    if (d.width > w) {
        		w = d.width;
    	    }
    	    if (d.height > h) {
        		h = d.height;
    	    }
    	}
    	return new Dimension(insets.left + insets.right + w + hgap*2,
    			     insets.top + insets.bottom + h + vgap*2);
    }

    /**
     * Performs a layout in the specified panel.
     * @param parent the name of the parent container
     */
    public void layoutContainerGadget(ContainerGadget parent) {
        setVisibilities( parent );
	    Insets insets = parent.getInsets();
	    int ngadgets = parent.getGadgetCount();
	    for (int i = 0 ; i < ngadgets ; i++) {
    		Gadget gadget = parent.getGadget(i);
    		if ( gadget.visible ) {
    		    gadget.setBounds(hgap + insets.left, vgap + insets.top,
    				 parent.width - (hgap*2 + insets.left + insets.right),
    				 parent.height - (vgap*2 + insets.top + insets.bottom), false);
	        }
	    }
    }

    /**
     * Make sure that the ContainerGadget really has a GadgetCardLayout installed.
     * Otherwise havoc can ensue!
     */
    void checkLayout(ContainerGadget parent) {
    	if (parent.getLayout() != this) {
    	    throw new IllegalArgumentException("wrong parent for GadgetCardLayout");
    	}
    }

    private void pop( Gadget gadget ) {
        if ( gadget != null ) {
            ContainerGadget parent = gadget.getParent();
            synchronized (parent.getTreeLock()) {
                int gadgetCount = parent.getGadgetCount();
                if ( gadget != parent.getGadget( gadgetCount-1 ) ) {
            	    for (int i = 0 ; i < gadgetCount ; i++) {
                		if (parent.gadgets[i] == gadget) {
                		    Gadget newgadgets[] = new Gadget[parent.gadgets.length];
                		    System.arraycopy( parent.gadgets, i + 1, newgadgets, 0, gadgetCount - i - 1 );
                		    System.arraycopy( parent.gadgets, 0, newgadgets, gadgetCount - i - 1, i + 1 );
                		    parent.gadgets = newgadgets;
                		    break;
                		}
            	    }
					topGadget = gadget;
                    setVisibilities( parent );
        		    if (parent.valid) {
        			    parent.invalidate();
        		    }
                }
            }
        }
    }

    /**
     * Flip to the first card.
     * @param parent the name of the parent container
     */
    public void first(ContainerGadget parent) {
        first();
    }

    /**
     * Flip to the first card.
     */
    public void first() {
        pop( firstcard );
    }

    /**
     * Flips to the next card of the specified container.
     * @param parent the name of the container
     */
    public void next(ContainerGadget parent) {
        next();
    }

    /**
     * Flips to the next card of the specified container.
     */
    public void next() {
        if ( firstcard != null ) {
            ContainerGadget parent = firstcard.getParent();
            if ( parent != null ) {
                synchronized (parent.getTreeLock()) {
            	    int ngadgets = parent.getGadgetCount();
            	    if ( ngadgets > 1 ) {
            	        pop( parent.getGadget( ngadgets-2 ) );
            	    }
            	}
            }
        }
    }

    /**
     * Flips to the previous card of the specified container.
     * @param parent the name of the parent container
     */
    public void previous(ContainerGadget parent) {
        previous();
    }

    /**
     * Flips to the previous card of the specified container.
     */
    public void previous() {
        if ( firstcard != null ) {
            ContainerGadget parent = firstcard.getParent();
            if ( parent != null ) {
                synchronized (parent.getTreeLock()) {
            	    int ngadgets = parent.getGadgetCount();
            	    if ( ngadgets > 1 ) {
            	        pop( parent.getGadget( 0 ) );
            	    }
            	}
            }
    	}
    }

    /**
     * Flips to the last card of the specified container.
     * @param parent the name of the parent container
     */
    public void last(ContainerGadget parent) {
   	    last();
    }

    /**
     * Flips to the last card of the specified container.
     */
    public void last() {
   	    pop( lastcard );
    }

    /**
     * shows the specified gadget name in the specified container.
     * @param parent the name of the parent container
     * @param name the gadget name
     */
    public void show(ContainerGadget parent, String name) {
        show( name );
    }

    /**
     * shows the specified gadget name in the specified container.
     * @param name the gadget name
     */
    public void show(String name) {
        if ( firstcard != null ) {
            ContainerGadget parent = firstcard.getParent();
            if ( parent != null ) {
                synchronized (parent.getTreeLock()) {
            	    checkLayout(parent);
            	    Gadget next = (Gadget)tab.get(name);
            	    pop( next );
            	}
            }
    	}
    }

    /**
     * Returns the String representation of this GadgetCardLayout's values.
     * @return String
     */
    public String toString() {
    	return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + "]";
    }
}
