/****************************************************************
 **
 **  $Id: CheckGadget.java,v 1.18 1997/08/06 23:27:00 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/CheckGadget.java,v $
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
import java.awt.Polygon;

/**
 * CheckGadget
 *
 * @version 1.1
 * @author  DTAI, Incorporated
 */
public class CheckGadget extends CheckboxIndicatorGadget {

    private static final int NPOINTS = 4;
    private Polygon left = new Polygon( new int[NPOINTS], new int[NPOINTS], NPOINTS );
    private Polygon right = new Polygon( new int[NPOINTS], new int[NPOINTS], NPOINTS );

	/**
	 * Constructs a Label with no label.
	 */
	public CheckGadget() {
	}

	/**
	 * Resets foreground height and width to 13.
	 *
	 * @param g		GadgetGraphics object for painting.
	 */
	protected void resetForegroundSize( GadgetGraphics g ) {
		fgHeight = 13;
		fgWidth = 13;
	}

    /**
     * showSelected - empth method.
     *
     * @param g		GadgetGraphics object for painting.
     */
    protected void showSelected( GadgetGraphics g ) {
    }

    /**
     * drawCheck
     *
     * @param g		GadgetGraphics object for painting.
     * @param x		x coordinate
     * @param y		y coordinate
     * @param width  width
     * @param height height
     * @param unit	 int
     */
    protected void drawCheck( GadgetGraphics g, int x, int y,
                                           int width, int height, int unit ) {
	    synchronized(getTreeLock()) {
            g.setColor( Color.black );
            if ( unit <= 0 ) {
                g.fillRect( x, y, width, height );
                return;
            }
            width -= unit * 2;
            height -= unit * 2;
            x += unit + ( width - ( unit * 6 ) ) / 2;
            y += unit + ( height - ( unit * 6 ) ) / 2;
            left.xpoints[0] = x;
            left.ypoints[0] = y + ( 2 * unit ) - 1;
            left.xpoints[1] = x;
            left.ypoints[1] = y + ( 4 * unit );
            left.xpoints[2] = x + ( 2 * unit ) + 1;
            left.ypoints[2] = y + ( 6 * unit ) + 1;
            left.xpoints[3] = x + ( 2 * unit ) + 1;
            left.ypoints[3] = y + ( 4 * unit );
            g.fillPolygon( left );

            right.xpoints[0] = x + ( 2 * unit ) + 1;
            right.ypoints[0] = y + ( 4 * unit ) - 1;
            right.xpoints[1] = x + ( 2 * unit ) + 1;
            right.ypoints[1] = y + ( 6 * unit );
            right.xpoints[2] = x + ( 6 * unit ) + 1;
            right.ypoints[2] = y + ( 2 * unit );
            right.xpoints[3] = x + ( 6 * unit ) + 1;
            right.ypoints[3] = y - 1;
            g.fillPolygon( right );
        }
	}

	/**
	 * paintDisabledForeground
	 *
     * @param g		GadgetGraphics object for painting.
     * @param x		x coordinate
     * @param y		y coordinate
     * @param width  width
     * @param height height
	 */
	protected void paintDisabledForeground(
	    GadgetGraphics g, int x, int y, int width, int height) {
	    paintForeground(g,x,y,width,height);
	}


    /**
     * paints specified object in foreground
     *
     * @param g		GadgetGraphics object for painting.
     * @param x		x coordinate
     * @param y		y coordinate
     * @param width  width
     * @param height height
     */
    protected void paintForeground(
            GadgetGraphics g,
            int x, int y, int width, int height) {
        if ( width > height ) {
            x += ( width - height ) / 2;
            width = height;
        }
        else if ( height > width ) {
            y += ( height - width ) / 2;
            height = width;
        }
        int unit = width / 13;
        int borderThickness = Math.max( 1, unit * 2 );
        Color bg = getFinalBackground(g);
        if ( armed || ( ! isFunctioning() ) ) {
            g.setColor( bg );
        }
        else {
            g.setColor( Color.white );
        }
        g.fillRect( x, y, width, height );
        if ( bg == Gadget.transparent ) {
            g.setColor( Color.lightGray );
        }
        else {
            g.setColor( bg );
        }
        g.draw3DRect( x, y, width, height, false, false, borderThickness );
        if ( selected ) {
            drawCheck( g, x + borderThickness, y + borderThickness,
                       width - ( borderThickness * 2 ), height - ( borderThickness * 2 ), unit );
        }
	}
}
