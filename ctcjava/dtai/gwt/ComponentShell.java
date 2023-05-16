/****************************************************************
 **
 **  $Id: ComponentShell.java,v 1.27 1997/11/20 19:37:51 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/ComponentShell.java,v $
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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;

/**
 * ComponentShell
 *
 * @version	1.1
 * @author	DTAI, Incorporated
 */
public class ComponentShell extends Gadget {

    Component component;

    /**
     * ComponentShell
     */
    public ComponentShell() {
    }

    /**
     * ComponentShell
     *
     * @param component	value for Component
     */
    public ComponentShell( Component component ) {
        add( component );
    }

    /**
     * add
     *
     * @param component	value for Component, and if there
	 * is a gadgetShell, it adds the given component
     */
    public void add( Component component ) {
        this.component = component;
        GadgetShell shell = getShell();
        if ( shell != null ) {
            shell.add( component );
        }
    }

    /**
     * Gets the component variable.
     *
     * @return Component
     */
    public Component getComponent() {
        return component;
    }

    /**
     * Sets the GadgetShell
     *
     * @param shell	new value for the GadgetShell
     */
    protected void setShell( GadgetShell shell ) {

		super.setShell( shell );
		if (shell != null) {
           getShell().add( component );
		}
    }

	/**
	 * Enables a component.
	 *
	 * @param b		is/is not Enabled
	 * @see Gadget#isEnabled
	 */
	public void setEnabled(boolean b) {
	    super.setEnabled( b );
	    if ( component != null ) {
	        if ( b ) {
    	        component.enable();
    	    }
    	    else {
    	        component.disable();
    	    }
	    }
	}

	/**
	 * Shows or hides the component depending on the boolean flag b.
	 *
	 * @param b 		if true, show the component; otherwise, hide the component.
	 * @param invalidateParent 	boolean
	 * @see Gadget#isVisible
	 */
	public void setVisible(boolean b, boolean invalidateParent) {
	    super.setVisible( b, invalidateParent );
	    if ( component != null ) {
	        if ( b ) {
    	        component.show();
    	    }
    	    else {
    	        component.hide();
    	    }
	    }
	}

	/**
	 * Sets the foreground color.
	 *
	 * @param c		the Color
	 * @see Gadget#getForeground
	 */
	public void setForeground(Color c) {
	    super.setForeground( c );
	    if ( component != null ) {
	        component.setForeground( c );
	    }
	}

	/**
	 * Sets the background color.
	 *
	 * @param c 	the Color
	 * @see Gadget#getBackground
	 */
	public void setNormalBackground(Color c) {
	    super.setNormalBackground( c );
	    if ( component != null ) {
	        component.setBackground( c );
	    }
	}

	/**
	 * Sets the font of the component.
	 *
	 * @param f 	the font
	 * @see Gadget#getFont
	 */
	public void setFont(Font f) {
	    synchronized(getTreeLock()) {
    	    super.setFont( f );
    	    if ( component != null ) {
    	        component.setFont( f );
    	    }
    	}
	}

	/**
	 * Returns the preferred size of this component.
	 *
	 * @return	the preferred size as a Dimension
	 * @see #getMinimumSize
	 * @see dtai.gwt.GadgetLayoutManager
	 */
	public Dimension getPreferredSize() {
	    if ( component == null ) {
	        return null;
	    }
	    if ( valid && ( pref_width >= 0 ) && ( pref_height >= 0 ) ) {
	        return new Dimension( pref_width, pref_height );
	    }
	    Dimension pref = component.preferredSize();
		return pref;
	}

	/**
	 * Returns the mininimum size of this component.
	 *
	 * @return	the minimum size as a Dimension
	 * @see #getPreferredSize
	 * @see dtai.gwt.GadgetLayoutManager
	 */
	public Dimension getMinimumSize() {
	    if ( component == null ) {
	        return null;
	    }
	    Dimension min = component.minimumSize();
		return min;
	}

	/**
	 * Lays out the component. This is usually called when
	 * the component (more specifically, container) is validated.
	 *
	 * @param g		the GadgetGraphics object to do the painting.
	 * @see Gadget#validate
	 * @see dtai.gwt.GadgetLayoutManager
	 */
	public void paint(GadgetGraphics g) {
	}

	/**
	 * Sets the boundaries of the component
	 *
	 * @param x			x coordinate
	 * @param y			y coordinate
	 * @param width		width
	 * @param height	height
	 * @param invalidateParent	boolean
	 */
	public void setBounds(int x, int y, int width, int height, boolean invalidateParent) {
		super.setBounds(x,y,width,height,invalidateParent);
    	Point offset = getOffset();
    	component.reshape( offset.x, offset.y, width, height );
	}

	/**
	 * repaint
	 *
	 * @param x			x coordinate
	 * @param y			y coordinate
	 * @param width		width
	 * @param height	height
	 * @param setpaintFlag	boolean
	 * @param forced	boolean
	 */
	public void repaint(int x, int y, int width, int height, boolean setPaintFlag,boolean forced) {
	    // does nothing
	}

	/**
	 * Returns whether this component can be traversed
	 * using Tab or Shift-Tab keyboard focus traversal.
	 *
	 * @return	is/is not traversable
	 */
	public boolean isFocusTraversable() {
        GadgetShell shell = getShell();
        if ( shell != null ) {
            if ( shell.isFocusAllowedToEscape() ) {
                return true;
            }
        }
        return false;
	}

	/**
	 * notifyStateChange
	 */
	public void notifyStateChange() {

		if (component != null) {

		    if ( isShowing(false) ) {
        		component.show();
        	}
        	else {
    			component.hide();
    		}

		    if ( isFunctioning() ) {
        		component.enable();
        	}
        	else {
    			component.disable();
    		}
    	}
	}

}


