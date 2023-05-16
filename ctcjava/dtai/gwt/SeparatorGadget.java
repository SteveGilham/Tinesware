/****************************************************************
 **
 **  $Id: SeparatorGadget.java,v 1.20 1997/11/20 19:37:53 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/SeparatorGadget.java,v $
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

/**
 * SeparatorGadget
 * @version 1.1
 * @author DTAI, Incorporated
 */
public class SeparatorGadget extends CanvasGadget {

    public static final int LINE = 0;
    public static final int ETCHED = 1;
    public static final int EMBOSSED = 2;

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private int hmargin = 0;
    private int vmargin = 0;
    private int separatorThickness = 0;
    private int separatorType = 0;
    private int	orientation;

    /**
     *   Constructs a Separator
     */
	public SeparatorGadget() {
		this( HORIZONTAL );
	}

	/**
	 * SeparatorGadget
	 * @param orientation	description
	 */
	public SeparatorGadget( int orientation ) {
		setOrientation( orientation );
        setSeparatorType( SeparatorGadget.ETCHED );
        setSeparatorThickness( 2 );
    }

    /**
     * Gets the orientation of the button.
     * @see setOrientation
     * @return int
     */

	public final int getOrientation() {
		return orientation;
	}

    /**
     * Sets the button with the specified orientation.
     * @param orientation - orientation label to set the button with
     * @see getOrientation
     */
	public void setOrientation( int orientation ) {
	    synchronized(getTreeLock()) {
            if ( ( orientation != VERTICAL ) &&
                 ( orientation != HORIZONTAL ) ) {
                throw new IllegalArgumentException();
            }
    	    if ( orientation != this.orientation ) {
        	    this.orientation = orientation;
        	}
        }
	}

	/**
	 * Gets the horizontal Margin
	 * @return int
	 * @see setHMargin
	 */
	public final int getHMargin() {
		return hmargin;
	}

	/**
     * Sets the Horizontal Margin
     * @param hmargin	description
     * @see getHMargin
     */
	public void setHMargin( int hmargin ) {
	    synchronized(getTreeLock()) {
    	    if ( hmargin != this.hmargin ) {
        	    this.hmargin = hmargin;
        	}
        }
	}

	/**
	 * Gets the Vertical Margin
	 * @return int
	 * @see setVMargin
	 */
	public final int getVMargin() {
		return vmargin;
	}

	/**
	 * Sets the Vertical Margin
	 * @param vmargin	description
	 * @see getVMargin
	 */
	public void setVMargin( int vmargin ) {
	    synchronized(getTreeLock()) {
    	    if ( vmargin != this.vmargin ) {
        	    this.vmargin = vmargin;
        	}
        }
	}

	/**
	 * Gets the Separator Type
	 * @return int
	 * @see setSeparatorType
	 */
	public final int getSeparatorType() {
		return separatorType;
	}

	/**
	 * Sets the Separator Type
	 * @param separatorType	description
	 * @see getSeparatorType
	 */
	public void setSeparatorType( int separatorType ) {
	    synchronized(getTreeLock()) {
    	    if ( separatorType != this.separatorType ) {
        	    this.separatorType = separatorType;
        	}
        }
	}

	/**
	 * Gets the Separator thickness
	 * @return int
	 * @see setSeparatorThickness
	 */
	public final int getSeparatorThickness() {
		return separatorThickness;
	}

	/**
	 * Sets the Separator thickness
	 * @param separatorThickness	description
	 * @see getSeparatorThickness
	 */
	public void setSeparatorThickness( int separatorThickness ) {
	    synchronized(getTreeLock()) {
    	    if ( separatorThickness != this.separatorThickness ) {
        	    this.separatorThickness = separatorThickness;
        	}
        }
	}

	/**
	 * getMinimumSize
	 * @return Dimension
	 */
	public Dimension getMinimumSize() {
	    return getPreferredSize();
	}

	/**
	 * getPreferredSize
	 * @return Dimension
	 */
	public Dimension getPreferredSize() {
	    if ( valid && ( pref_width >= 0 ) && ( pref_height >= 0 ) ) {
	        return new Dimension( pref_width, pref_height );
	    }
	    int width = 2*hmargin;
	    int height = 2*vmargin;
	    if ( orientation == VERTICAL ) {
	        width += separatorThickness;
	    }
	    else {
	        height += separatorThickness;
	    }
	    return new Dimension( (2*hmargin)+separatorThickness,
	                          (2*vmargin) );
	}

	/**
	 * paints the gadget
	 * @param g	description
	 */
	public void paint( GadgetGraphics g) {
	    Color color;
	    int x = hmargin;
	    int y = vmargin;
	    int width = this.width-(2*hmargin);
	    int height = this.height-(2*vmargin);
	    if ( orientation == VERTICAL ) {
	        x = (this.width - separatorThickness)/2;
	        width = separatorThickness;
	    }
	    else {
	        y =(this.height - separatorThickness)/2;
	        height = separatorThickness;
	    }
	    if ( separatorType == LINE ) {
	        g.setColor( getForeground(g) );
	        g.fillRect(x,y,width,height);
	    }
	    else {
	        g.setColor( getFinalBackground(g) );
    	    g.draw3DRect(x,y,width,height,
    	                 (separatorType==EMBOSSED),false,((separatorThickness+2)/3));
	    }
	}
}
