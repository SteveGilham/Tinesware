/****************************************************************
 **
 **  $Id: OverlayPanelGadget.java,v 1.31 1998/02/27 18:02:23 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/OverlayPanelGadget.java,v $
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
import java.awt.Point;
import java.awt.Rectangle;
import java11.awt.event.AWTEvent;
import java11.awt.event.MouseEvent;

/**
 * OverlayPanelGadget
 * @version 1.1
 * @author DTAI, Incorporated
 */
public class OverlayPanelGadget extends PanelGadget {

    private Gadget overlayGadget;

    /**
     * OverlayPanelGadget
     */
    public OverlayPanelGadget() {
        setLayout( null );
	    setBackground(Gadget.transparent);
    }

    private Rectangle getBadRect( Rectangle bounds, Component comps[] ) {
        for ( int i = 0; i < comps.length; i++ ) {
            if ( comps[i].isVisible() ) {
                Rectangle compRect = comps[i].bounds();
                if ( bounds.intersects( compRect ) ) {
                    return compRect;
                }
            }
        }
        return null;
    }

    private final boolean overTheEdge( Rectangle bounds ) {
         return ( ( ( bounds.x + bounds.width ) > width ) ||
                  ( bounds.x < 0 ) ||
                  ( ( bounds.y + bounds.height ) > height ) ||
                  ( bounds.y < 0 ) );
    }

    private void awtDeconflict( Rectangle bounds ) {

        GadgetShell shell = getShell();
        if ( shell != null ) {
	        Component comps[] = shell.getComponents();

	        if ( comps.length > 0 ) {

    	        Rectangle bad = getBadRect( bounds, comps );

    	        if ( bad != null ) {

    	            // try left
    	            int saveX = bounds.x;
    	            int saveY = bounds.y;

    	            bounds.x = bad.x - bounds.width;

    	            if ( ( getBadRect( bounds, comps ) != null ) ||
            	         overTheEdge( bounds ) ) {

            	        // try right
            	        bounds.x = bad.x + bad.width;

        	            if ( ( getBadRect( bounds, comps ) != null ) ||
                	         overTheEdge( bounds ) ) {

                	        // try down
                	        bounds.x = saveX;
                	        bounds.y = bad.y + bad.height;

            	            if ( ( getBadRect( bounds, comps ) != null ) ||
                    	         overTheEdge( bounds ) ) {

                    	        // try up
                    	        bounds.y = bad.y - bounds.width;
                	            bad = getBadRect( bounds, comps );

                	            if ( ( bad != null ) ||
                        	         overTheEdge( bounds ) ) {
                    	            bounds.y = saveY;
                    	        }
                    	    }
                	    }
            	    }
    	        }
    	    }
	    }
    }

    /**
     * Does a layout on this Container.
     * @see ContainerGadget#setLayout
     */
    public void doLayout() {
		synchronized(getTreeLock()) {
            super.doLayout();
        	for ( int i = 0 ; i < gadgetCount ; i++ ) {
        	    Gadget gadget = gadgets[i];
        	    if ( gadget != null ) {
        	        Rectangle bounds = gadget.getBounds();
        	        if ( bounds.width <= 0 || bounds.height <= 0 ) {
        	            gadget.setSize( gadget.getPreferredSize() );
        	            bounds = gadget.getBounds();
        	        }
        	        if ( ( bounds.x + gadget.width ) > width ) {
        	            bounds.x = width - gadget.width;
        	        }
        	        if ( bounds.x < 0 ) {
        	            bounds.x = 0;
        	        }
        	        if ( ( bounds.y + gadget.height ) > height ) {
        	            bounds.y = height - gadget.height;
        	        }
        	        if ( bounds.y < 0 ) {
        	            bounds.y = 0;
        	        }

					if ( ! ( gadget instanceof ComponentShell ) ) {
						awtDeconflict( bounds );
					}
        	        gadget.setBounds( bounds, false );
        	    }
        	}
        }
    }

    /**
     * add
     * @param g - TBD
     * @param pos - TBD
     * @return Gadget
     */
    public Gadget add(Gadget g, int pos) {
		synchronized(getTreeLock()) {
    		if ( overlayGadget != g ) {
				if (overlayGadget != null) {
				   remove(overlayGadget);
				}
				overlayGadget = g;
				setOverlayGadgetVisible(true);
				invalidate();
				return super.add(g, pos);
    		}
    		else {
    			return g;
    		}
		}
    }

    /**
     * remove
     * @param g - TBD
     * @param position - TBD
     */
    public void remove(Gadget g, int position) {
		synchronized(getTreeLock()) {
            super.remove(g,position);
            Gadget overlayGadget = this.overlayGadget;
		    if ( g == overlayGadget && overlayGadget != null) {
		        this.overlayGadget = null;
		        overlayGadget.setVisible(false);
        	    setConsumingTransparentClicks(false);
        	    getShell().enableTips();
        	}
        }
    }

    /**
     * setOverlayGadgetVisible
     * @param state - TBD
     */
    private void setOverlayGadgetVisible (boolean state) {
        Gadget g = overlayGadget;
        if (g != null) {
            g.setVisible(state);
        }
    }

    /**
     * popdownOverlay
     */
    public void popdownOverlay() {
        Gadget g = overlayGadget;
        if (g != null) {
            remove(g);
        }
    }

    /**
     * processMouseEvent
     * @param mouse the MouseEvent
     */
    public void processMouseEvent( MouseEvent mouse ) {
        if (mouse.getID() == MouseEvent.MOUSE_RELEASED) {
            Gadget g = overlayGadget;
            if (g != null) {
                Point loc = g.getLocation();
                mouse.translatePoint(-loc.x, -loc.y);
                if (!g.contains(mouse.getX(),mouse.getY())) {
                    remove(g);
                }
                mouse.translatePoint(loc.x, loc.y);
            }
        }
        mouse.consume();
        super.processMouseEvent( mouse );
    }
}
