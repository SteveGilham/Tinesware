/****************************************************************
 **
 **  $Id: ArrowButtonGadget.java,v 1.21 1998/02/04 03:16:09 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/ArrowButtonGadget.java,v $
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

import java.awt.Image;
import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;

/**
 * ArrowButtonGadget
 *
 * @version	1.1
 * @author	DTAI, Incorporated
 */
public class ArrowButtonGadget extends ButtonGadget {


    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;

    private static final int WIDTH = 7;
    private static final int HEIGHT = 7;

    private static final int X = 0xFF000000; // black pixel
    private static final int Y = 0x00C0C0C0; // light gray transparent pixel

    private static final int up_pixels[] = {
        Y, Y, Y, Y, Y, Y, Y,
        Y, Y, Y, Y, Y, Y, Y,
        Y, Y, Y, X, Y, Y, Y,
        Y, Y, X, X, X, Y, Y,
        Y, X, X, X, X, X, Y,
        X, X, X, X, X, X, X,
        Y, Y, Y, Y, Y, Y, Y,
    };

    private static final int down_pixels[] = {
        Y, Y, Y, Y, Y, Y, Y,
        X, X, X, X, X, X, X,
        Y, X, X, X, X, X, Y,
        Y, Y, X, X, X, Y, Y,
        Y, Y, Y, X, Y, Y, Y,
        Y, Y, Y, Y, Y, Y, Y,
        Y, Y, Y, Y, Y, Y, Y,
    };

    private static final int left_pixels[] = {
        Y, Y, Y, Y, Y, X, Y,
        Y, Y, Y, Y, X, X, Y,
        Y, Y, Y, X, X, X, Y,
        Y, Y, X, X, X, X, Y,
        Y, Y, Y, X, X, X, Y,
        Y, Y, Y, Y, X, X, Y,
        Y, Y, Y, Y, Y, X, Y,
    };

    private static final int right_pixels[] = {
        Y, X, Y, Y, Y, Y, Y,
        Y, X, X, Y, Y, Y, Y,
        Y, X, X, X, Y, Y, Y,
        Y, X, X, X, X, Y, Y,
        Y, X, X, X, Y, Y, Y,
        Y, X, X, Y, Y, Y, Y,
        Y, X, Y, Y, Y, Y, Y,
    };

    private static final int pixels[][] = {
        up_pixels,
        down_pixels,
        left_pixels,
        right_pixels,
    };

    private static final Image defaultArrow[] = new Image[4];


	/**
     * Constructs an up ArrowButton
	 *
	 */
	public ArrowButtonGadget() {
	    this( UP );
	}


	/**
     * Constructs an ArrowButton with a particular arrow image.
	 *
	 * @param direction        Possible directions are:
	 *                         ArrowButtonGadget.UP
	 *                         ArrowButtonGadget.DOWN
	 *                         ArrowButtonGadget.LEFT
	 *                         ArrowButtonGadget.RIGHT
	 */
	public ArrowButtonGadget( int direction ) {
	    setDirection( direction );
	    setBlackToForeground(true);
        setCloseShaved(false);
	}

	/**
	 * setFocusAllowed
	 *
	 * @param focusAllowed		whether or not focus is allowed
	 */
	public void setFocusAllowed(boolean focusAllowed) {
	    super.setFocusAllowed(focusAllowed);
	    if ( ! focusAllowed ) {
            setMargins(3);
            getArmedBorder().setMargins(4,4,2,2);
        }
	}

	/**
	 * Sets the direction of the arrow for the ArrowButton
	 *
	 * @param direction        Possible directions are:
	 *                         ArrowButtonGadget.UP
	 *                         ArrowButtonGadget.DOWN
	 *                         ArrowButtonGadget.LEFT
	 *                         ArrowButtonGadget.RIGHT
	 */
	public void setDirection( int direction ) {
		setImage( getArrow( this, direction ) );
	}

	/**
	 * getArrow - Gets the direction of the arrow for the Arrowbutton
	 * @param g
	 * @param direction
	 * @return Image
	 */
	private static synchronized Image getArrow( Gadget g, int direction ) {
	    if ( defaultArrow[direction] == null ) {
            defaultArrow[direction] = g.createImage(
                new MemoryImageSource ( WIDTH, HEIGHT, pixels[direction],
                                        0, WIDTH ) );
        }

        return defaultArrow[direction];
	}
}

