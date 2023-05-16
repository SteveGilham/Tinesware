/****************************************************************
 **
 **  $Id: DisplayGadget.java,v 1.47 1998/03/05 17:10:24 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/DisplayGadget.java,v $
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
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

/**
 * DisplayGadget
 * @version	1.1
 * @author	DTAI, Incorporated
 */
public abstract class DisplayGadget extends Gadget {

    public static final int HORIZ_ALIGN_LEFT = 1;
    public static final int HORIZ_ALIGN_CENTER = 2;
    public static final int HORIZ_ALIGN_RIGHT = 3;

    public static final int VERT_ALIGN_TOP = 4;
    public static final int VERT_ALIGN_MIDDLE = 5;
    public static final int VERT_ALIGN_BOTTOM = 6;

    Image disabledForeground;
    private int horizAlign = HORIZ_ALIGN_CENTER;
    private int vertAlign = VERT_ALIGN_MIDDLE;

    int fgWidth;
    int fgHeight;

	/**
	 * gets the horizontal alignment
	 * @return int
	 */
	public final int getHorizAlign() {
		return horizAlign;
	}

	/**
	 * sets the horizontal alignment
	 * @param horizAlign	new value for horizAlign
	 */
	public void setHorizAlign( int horizAlign ) {
	    synchronized(getTreeLock()) {
    	    if ( this.horizAlign != horizAlign ) {
        		this.horizAlign = horizAlign;
        		invalidate();
        		repaint();
        	}
        }
	}

	/**
	 * gets the vertical alignment
	 * @return int
	 */
    public final int getVertAlign() {
		return vertAlign;
	}

	/**
	 * seta the vertical alignment
	 * @param vertAlign	new value for vertAlign
	 */
    public void setVertAlign( int vertAlign ) {
	    synchronized(getTreeLock()) {
    	    if ( this.vertAlign != vertAlign ) {
        		this.vertAlign = vertAlign;
        		invalidate();
        		repaint();
        	}
        }
	}

	/**
	 * paints a specified area of the foreground
	 * @param g		the GadgetGraphics window
	 * @param x		x coordinate
	 * @param y		y coordinate
	 * @param width	 width of window
	 * @param height height of window
	 */
	protected abstract void paintForeground(
    	    GadgetGraphics g,
    	    int x, int y, int width, int height);

	private void paintImage( GadgetGraphics g, int x, int y, int width, int height,
	                         Image image ) {
		if ( image != null ) {
			g.drawImage( image, x, y, width, height, this );
		}
	}

	private void makeDisabledForeground( GadgetGraphics g, int width, int height ) {
	    if ( ( width > 0 ) &&
	         ( height > 0 ) ) {
            Image normal_foreground = createImage( width, height );
            GadgetGraphics fg_graphics = new GadgetGraphics( normal_foreground.getGraphics() );
            Color bg = getFinalBackground(g);
            fg_graphics.setColor( bg );
            fg_graphics.fillRect( 0, 0, width, height );
            paintForeground( fg_graphics, 0, 0, width, height );
            fg_graphics.dispose();
            disabledForeground =
                createImage( new FilteredImageSource(
                    normal_foreground.getSource(),
                    new DisplayImageFilter( null, bg, false ) ) );
        }
	}

	/**
	 * paintDisabledForeground
	 * @param g		the GadgetGraphics window
	 * @param x		x coordinate
	 * @param y		y coordinate
	 * @param width	 width of window
	 * @param height height of window
	 */
	protected void paintDisabledForeground(
	    GadgetGraphics g, int x, int y, int width, int height) {

	    if ( disabledForeground == null ) {
	        makeDisabledForeground( g, width, height );
	    }
	    paintImage( g, x, y, width, height, disabledForeground );
	}

	/**
	 * resets the foreground size
	 * @param g		the GadgetGraphics window
	 */
	protected abstract void resetForegroundSize( GadgetGraphics g );

	/**
	 * gets the minimum dimensions
	 * @return Dimension
	 */
	public Dimension getMinimumSize() {
        resetForegroundSize( null );
		return new Dimension( fgWidth, fgHeight );
	}

	/**
	 * gets the preffered dimensions
	 * @return Dimension
	 */
     public Dimension getPreferredSize() {
	    int pw = pref_width;
	    int ph = pref_height;
	    if (pw == -1 || ph == -1) {
            resetForegroundSize(null);
            pw = pref_width = fgWidth;
            ph = pref_height = fgHeight;
        }
		return new Dimension(pw, ph);
	}

	/**
	 * validate
	 */
	public void validate() {
	    if ( ! valid ) {
    	    super.validate();
    	    disabledForeground = null;
    	}
	}

	/**
	 * getInnerRect
	 * @return Rectangle
	 */
	public Rectangle getInnerRect() {
        int x = 0;
        int y = 0;
		if ( vertAlign == VERT_ALIGN_BOTTOM ) {
            y += height - fgHeight;
		}
		else if ( vertAlign == VERT_ALIGN_MIDDLE ) {
            y += ( height - fgHeight ) / 2;
		}
		if ( horizAlign == HORIZ_ALIGN_RIGHT ) {
		    x += width - fgWidth;
		}
		else if ( horizAlign == HORIZ_ALIGN_CENTER ) {
            x += ( width - fgWidth ) / 2;
		}
		return new Rectangle( x, y, fgWidth, fgHeight );
	}

	/**
	 * paints the GadgetGraphics window
	 * @param g	the GadgetGraphics window
	 */
	public void paint( GadgetGraphics g ) {
        resetForegroundSize( g );
        Rectangle innerRect = getInnerRect();
        if ( clippingRequired ) {
	        g.clipRect( innerRect.x, innerRect.y,
	                    innerRect.width, innerRect.height );
	    }
	    if ( isFunctioning() ) {
    	    paintForeground( g, innerRect.x, innerRect.y,
    	                     innerRect.width, innerRect.height );
    	}
    	else {
    	    paintDisabledForeground( g, innerRect.x, innerRect.y,
    	                             innerRect.width, innerRect.height );
    	}
	}

	/**
	 * getDisabledBottom
	 * @param fg	Color
	 * @param bg	Color fed to GadgetGraphics.brighter
	 * @return GadgetGraphics.brighter(bg)
	 */
	protected Color getDisabledBottom( Color fg, Color bg ) {
		return GadgetGraphics.brighter(bg);
    }

	/**
	 * getDisabledTop
	 * @param fg	Color
	 * @param bg	Color fed to GadgetGraphics.darker
	 * @return GadgetGraphics.darker(bg)
	 */
	protected Color getDisabledTop( Color fg, Color bg ) {
		return GadgetGraphics.darker(bg);
    }

	/**
     * Checks whether this component "contains" the specified (x, y)
	 * location, where x and y are defined to be relative to the
	 * coordinate system of this component.
     * @param x 	the x coordinate
     * @param y 	the y coordinate
	 * @param exclude	the Gadget to exclude
	 * @return 		inside/not inside
	 */
     public boolean contains( int x, int y, Gadget exclude ) {
		synchronized(getTreeLock()) {
    		if ( ( ! isShowing() ) ||
    		     ( this == exclude ) ||
    		     ( ! ( (x >= 0) &&
    		           (x <= width) &&
    		           (y >= 0) &&
    		           (y <= height) ) ) ) {
                return false;
            }
            if ( ( ! isTransparent() ) ||
                 ( consumingTransparentClicks ) ) {
                return true;
            }
            /* contradictory and redundant with above?
    		if ( ! ( (x >= 0) &&
    	             (x <= width) &&
    	             (y >= 0) &&
    	             (y <= height) ) ) {
                return true;
            }
            */

            return getInnerRect().inside( x, y );
        }
	}

    /**
     * Repaints the component when the image has changed.
     * @return true if image has changed; false otherwise.
     */
    /*
    public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h) {
		if ( ( ( flags & (WIDTH|HEIGHT|FRAMEBITS|ALLBITS) ) != 0 ) &&
    		 ( img != disabledForeground ) ) {
    		int prefWidth = pref_width;
    		int prefHeight = pref_height;
    		if ( ( prefWidth < 0 ) || ( prefHeight < 0 ) ) {
        		Dimension pref = getPreferredSize();
        		prefWidth = pref.width;
        		prefHeight = pref.height;
        	}
    		if ( ( prefWidth != width ) ||
    		     ( prefHeight != height ) ) {
    			invalidate();
    	    }
		}
		return ( super.imageUpdate( img, flags, x, y, w, h ) );
	}
	*/
}
