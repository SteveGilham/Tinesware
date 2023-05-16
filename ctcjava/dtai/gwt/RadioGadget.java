/****************************************************************
 **
 **  $Id: RadioGadget.java,v 1.14 1997/11/27 02:13:08 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/RadioGadget.java,v $
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

/**
 * RadioGadget
 * @version 1.1
 * @author DTAI, Incorporated
 */
public class RadioGadget extends CheckboxIndicatorGadget {
    private static int BOTTOMOFFSET = 2; // helps accomodate space below baseline
	/**
	 * RadioGadget
	 */
	public RadioGadget() {
	}

	/**
	 * resets the foreground size
	 * @param g	description
	 */
	protected void resetForegroundSize( GadgetGraphics g ) {
		fgHeight = 12+BOTTOMOFFSET;
		fgWidth = 12;
	}

    /**
     * showSelected
     * @param g	description
     */
    protected void showSelected( GadgetGraphics g ) {
    }

    /**
     * paintBorder
     * @param g	description
     * @param bg	description
     * @param thickness	description
     * @param x	description
     * @param y	description
     * @param width	description
     * @param height	description
     */
    protected void paintBorder( GadgetGraphics g, Color bg, int thickness,
                                int x, int y, int width, int height ) {
        g.setColor( GadgetGraphics.darker( bg ) );
        g.fillArc( x, y, width, height, 44, 182 );
        g.setColor( GadgetGraphics.brighter( bg ) );
        g.fillArc( x, y, width, height, 225, 180 );
	}

	/**
     * paintDisabledForeground
     * @param g	description
     * @param x	description
     * @param y	description
     * @param width	description
     * @param height	description
     */
	protected void paintDisabledForeground(
	    GadgetGraphics g, int x, int y, int width, int height) {
	    paintForeground(g,x,y,width,height);
	}

    /**
     * paints the foreground
     * @param g	description
     * @param x	description
     * @param y	description
     * @param width	description
     * @param height	description
     */
    protected void paintForeground(
            GadgetGraphics g, int x, int y, int width, int height) {
        if ( width > height ) {
            x += ( width - height ) / 2;
            width = height;
        }
        else if ( height > width ) {
            y += ( height - width ) / 2 - BOTTOMOFFSET;
            height = width;
        }
        Color bg = getFinalBackground(g);
        int borderThickness = width / 6;
        paintBorder( g, bg, borderThickness, x, y, width, height );
        if ( armed || ( ! isFunctioning() ) ) {
            g.setColor( bg );
        }
        else {
            g.setColor( Color.white );
        }
        g.fillArc( x + borderThickness, y + borderThickness,
                   width - ( borderThickness * 2 ),
                   height - ( borderThickness * 2 ), 0, 360 );
        if ( selected ) {
            g.setColor( Color.black );
            g.fillArc( x + ( width / 3 ), y + ( height / 3 ),
                       width - ( (int)( width / 3 ) * 2 ),
                       height - ( (int)( height / 3 ) * 2 ), 0, 360 );
        }
	}
}
