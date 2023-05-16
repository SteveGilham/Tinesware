/****************************************************************
 **
 **  $Id: BorderGadget.java,v 1.21 1998/03/10 20:30:41 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/BorderGadget.java,v $
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
import java.awt.Insets;

/**
 * BorderGadget
 *
 * @version	1.1
 * @author	DTAI, Incorporated
 */
public class BorderGadget extends PanelGadget implements Cloneable {

    public static final int NONE = 0;
    public static final int LINE = 1;
    public static final int THREED_IN = 2;
    public static final int THREED_OUT = 3;
    public static final int ETCHED_IN = 4;
    public static final int EMBOSSED_OUT = 5;
    public static final int ROUND_RECT = 6;

    private Color realNormalBg;
    private Color realSelectedBg;

    private int type = NONE;
    private Color border = Gadget.defaultColor;
    private Color shadow = Color.gray;
    private int topMargin = 0;
    private int leftMargin = 0;
    private int bottomMargin = 0;
    private int rightMargin = 0;
    private int minMargin = 0;
    private int maxMargin = 0;
    private int borderThickness = 0;
    private int focusThickness = 0;
    private int shadowXOffset = 0;
    private int shadowYOffset = 0;
    private int defaultThickness = 0;
    private boolean closeShaved = false;

	/**
	 * BorderGadget constructor.
	 *
	 */
	public BorderGadget() {
	    setLayout( new GadgetBorderLayout() );
	}

    /**
     * Returns a new BorderGadget "equal" to this one.
	 *
     * @return	a new BorderGadget clone of this one.
     */
    public Object clone() {
        BorderGadget newborder = new BorderGadget();
        newborder.type = type;
        newborder.border = border;
        newborder.shadow = shadow;
        newborder.topMargin = topMargin;
        newborder.leftMargin = leftMargin;
        newborder.bottomMargin = bottomMargin;
        newborder.rightMargin = rightMargin;
        newborder.minMargin = minMargin;
        newborder.maxMargin = maxMargin;
        newborder.borderThickness = borderThickness;
        newborder.focusThickness = focusThickness;
        newborder.shadowXOffset = shadowXOffset;
        newborder.shadowYOffset = shadowYOffset;
        newborder.defaultThickness = defaultThickness;
        newborder.closeShaved = closeShaved;

        return newborder;
    }

    /**
     * Sets this BorderGadget to be equal to the given one.
     *
     * @param rhs 	the BorderGadget that this BorderGadget will
	 * be made equal to, if it is not already.
     */
    public void setBorder( BorderGadget rhs ) {
        if ( this.equals(rhs) ) {
            return;
        }
        Insets before = getInsets();
        type = rhs.type;
        border = rhs.border;
        shadow = rhs.shadow;
        topMargin = rhs.topMargin;
        leftMargin = rhs.leftMargin;
        bottomMargin = rhs.bottomMargin;
        rightMargin = rhs.rightMargin;
        minMargin = rhs.minMargin;
        maxMargin = rhs.maxMargin;
        borderThickness = rhs.borderThickness;
        focusThickness = rhs.focusThickness;
        shadowXOffset = rhs.shadowXOffset;
        shadowYOffset = rhs.shadowYOffset;
        defaultThickness = rhs.defaultThickness;
        closeShaved = rhs.closeShaved;
        repaint();
        Insets after = getInsets();
        if ( ( after.left+after.right != before.left+before.right ) ||
             ( after.top+after.bottom != before.top+before.bottom ) ) {
            invalidate(true);
        }
        else {
            invalidate(false);
        }
    }


    /**
	 * Does an equality check between this BorderGadget
	 * and the given one.
     *
     * @param rhs	a BorderGadget for comparison
     * @return boolean result of comparison between the
	 * supplied Border Gadget and this one.
     */
    public boolean equals( BorderGadget rhs ) {
        if ( ( type == rhs.type ) &&
             ( border == rhs.border ) &&
             ( shadow == rhs.shadow ) &&
             ( topMargin == rhs.topMargin ) &&
             ( leftMargin == rhs.leftMargin ) &&
             ( bottomMargin == rhs.bottomMargin ) &&
             ( rightMargin == rhs.rightMargin ) &&
             ( minMargin == rhs.minMargin ) &&
             ( maxMargin == rhs.maxMargin ) &&
             ( borderThickness == rhs.borderThickness ) &&
             ( focusThickness == rhs.focusThickness ) &&
             ( defaultThickness == rhs.defaultThickness ) &&
             ( closeShaved == rhs.closeShaved ) &&
             ( shadowXOffset == rhs.shadowXOffset ) &&
             ( shadowYOffset == rhs.shadowYOffset ) ) {
            return true;
        }
        return false;
    }

	/**
	 * Sets type variable to the supplied value.
	 *
	 * @param type new value for type
	 */
	public void setBorderType( int type ) {
	    if ( type != this.type ) {
    		this.type = type;
    		repaint();
    	}
	}

	/**
	 * Returns the value of the type variable.
	 *
	 * @return int - the value of the type variable
	 */
	public int getBorderType() {
		return type;
	}

	/**
	 * Sets border variable to the supplied color.
	 *
	 * @param border	new value for border color
	 */
	public void setBorderColor(Color border) {
	    if ( border != this.border ) {
    		this.border = border;
    		repaint();
    	}
	}

	/**
	 * Returns the value of the border color variable.
	 *
	 * @return border color - the value of the border color variable
	 */
	 public Color getBorderColor() {
		return border;
	}

	/**
	 * Sets defaultThickness variable to the supplied value.
	 *
	 * @param defaultThickness - new value for defaultThickness
	 */
	public void setDefaultThickness(int defaultThickness) {
	    if ( defaultThickness != this.defaultThickness ) {
	        this.defaultThickness = defaultThickness;
	    }
	}

	/**
	 * Returns the value of the defaultThickness variable.
	 *
	 * @return int - the value of the defaultThickness variable
	 */
	public int getDefaultThickness() {
	    return defaultThickness;
	}

	/**
	 * Sets closeShaved variable to the supplied value.
	 *
	 * @param closeShaved new value for closeShaved
	 */
	public void setCloseShaved(boolean closeShaved) {
	    if ( closeShaved != this.closeShaved ) {
	        this.closeShaved = closeShaved;
	    }
	}

	/**
	 * Returns the value of the closeShaved variable.
	 *
	 * @return boolean - the value of the closeShaved variable
	 */
	public boolean getCloseShaved() {
	    return closeShaved;
	}

	/**
	 * Sets shadow variable to the supplied color.
	 *
	 * @param shadow  new color for shadow
	 */
	public void setShadowColor(Color shadow) {
	    if ( shadow != this.shadow ) {
    		this.shadow = shadow;
    		repaint();
    	}
	}

	/**
	 * Returns the value of the shadow color variable.
	 *
	 * @return Color - value for the shadow color variable
	 */
	public Color getShadowColor() {
		return shadow;
	}

	/**
	 * setNoInsets sets all margins, thicknesses and ofsets to zero.
	 *
	 */
	public void setNoInsets() {
        if ( ( topMargin != 0 ) ||
             ( leftMargin != 0 ) ||
             ( bottomMargin != 0 ) ||
             ( rightMargin != 0 ) ||
             ( minMargin != 0 ) ||
             ( maxMargin != 0 ) ||
             ( borderThickness != 0 ) ||
             ( defaultThickness != 0 ) ||
             ( focusThickness != 0 ) ||
             ( shadowXOffset != 0 ) ||
             ( shadowYOffset != 0 ) ) {
            topMargin = 0;
            leftMargin = 0;
            bottomMargin = 0;
            rightMargin = 0;
            minMargin = 0;
            maxMargin = 0;
            borderThickness = 0;
            focusThickness = 0;
            defaultThickness = 0;
            shadowXOffset = 0;
            shadowYOffset = 0;
            repaint();
            invalidate();
        }
	}

	/**
	 * Returns false if some insets are nonzero.  If all insets
	 * are zero, returns true.
	 *
     * @return  true if maxMargin, borderThickness, focusThickness
	 * 			defaultThickness, shadowXOffset, and shadowYOffset
	 *			are all zero, else returns false.
     */
	public boolean hasNoInsets() {
        return ( maxMargin == 0 &&
                 borderThickness == 0 &&
                 focusThickness == 0 &&
                 defaultThickness == 0 &&
                 shadowXOffset == 0 &&
                 shadowYOffset == 0 );
	}

	private void resetMinMaxMargin() {
        maxMargin = topMargin;
        maxMargin = Math.max( maxMargin, leftMargin );
        maxMargin = Math.max( maxMargin, bottomMargin );
        maxMargin = Math.max( maxMargin, rightMargin );
        minMargin = topMargin;
        minMargin = Math.min( minMargin, leftMargin );
        minMargin = Math.min( minMargin, bottomMargin );
        minMargin = Math.min( minMargin, rightMargin );
    }

	/**
	 * Sets top, bottom, left, and right margins to the
	 * one supplied value.
	 *
	 * @param margin	new value for all four margins
	 */
	public final void setMargins( int margin ) {
	    synchronized(getTreeLock()) {
    	    setMargins(margin,margin,margin,margin);
    	}
	}

	/**
	 * Sets top, bottom, left, and right margins to the
	 * supplied values.
	 *
	 * @param top		new value for top margin
	 * @param left		new value for left margin
	 * @param bottom	new value for bottom margin
	 * @param right		new value for right margin
	 */
	public void setMargins( int top, int left, int bottom, int right ) {
	    synchronized(getTreeLock()) {
    	    if ( top != topMargin ||
    	         left != leftMargin ||
    	         bottom != bottomMargin ||
    	         right != rightMargin ) {
                Insets before = getInsets();
        	    topMargin = top;
        	    leftMargin = left;
        	    bottomMargin = bottom;
        	    rightMargin = right;
        	    resetMinMaxMargin();
        	    if ( minMargin < focusThickness ) {
        	        focusThickness = minMargin;
        	    }
                Insets after = getInsets();
                if ( ( after.left+after.right != before.left+before.right ) ||
                     ( after.top+after.bottom != before.top+before.bottom ) ) {
                    invalidate(true);
                }
                else {
                    invalidate(false);
                }
            }
        }
	}

	/**
	 * Gets the value of the BorderThickness variable.
	 *
	 * @return int - the value of the BorderThickness variable
	 */
	public int getBorderThickness() {
	    return borderThickness;
	}

	/**
	 * Sets the BorderThickness variable to the supplied value.
	 *
	 * @param borderThickness new value for borderThickness
	 */
	public void setBorderThickness( int borderThickness ) {
	    if ( borderThickness != this.borderThickness ) {
    	    this.borderThickness = borderThickness;
    	    invalidate();
    	}
	}

	/**
	 * Gets the value of the focusThickness variable.
	 *
	 * @return int - value of the focusThickness variable
     */
	public int getFocusThickness() {
	    return focusThickness;
	}

	/**
	 * Sets the focusThickness variable to the supplied value.
	 *
	 * @param focusThickness new value for focusThickness
	 */
	public void setFocusThickness( int focusThickness ) {
	    if ( focusThickness != this.focusThickness ) {
    	    this.focusThickness = focusThickness;
    	    if ( focusThickness > minMargin ) {
    	        setMargins(focusThickness);
    	    }
    	    else {
        	    repaint();
        	}
    	}
	}

	/**
	 * Gets the value of the shadowXOffset variable.
	 *
	 * @return int - value of the shadowXOffset variable
	 */
	public int getShadowXOffset() {
	    return shadowXOffset;
	}

	/**
	 * Sets the shadowXOffset variable to the supplied value.
	 *
	 * @param shadowXOffset new value for shadowXOffset
	 */
	public void setShadowXOffset( int shadowXOffset ) {
	    if ( shadowXOffset != this.shadowXOffset ) {
    	    this.shadowXOffset = shadowXOffset;
    	    invalidate();
    	}
	}

	/**
	 * Gets the value of the shadowYOffset variable.
	 *
	 * @return int - value of the shadowYOffset variable
	 */
	public int getShadowYOffset() {
	    return shadowYOffset;
	}

	/**
	 * Sets the shadowYOffset variable to the supplied value.
	 *
	 * @param shadowYOffset new value for shadowYOffset
	 */
	public void setShadowYOffset( int shadowYOffset ) {
	    if ( shadowYOffset != this.shadowYOffset ) {
    	    this.shadowYOffset = shadowYOffset;
    	    invalidate();
    	}
	}

	/**
	 * Gets the value of the MinMargin varaible.
	 *
	 * @return int - value of the MinMargin variable
	 */
	public int getMinMargin() {
	    return minMargin;
	}

    /**
	 * Gets the value of the MaxMargin varaible.
	 *
	 * @return int - value of the MaxMargin variable
	 */
	public int getMaxMargin() {
	    return maxMargin;
	}

	/**
	 * Returns the left, right, top, and bottom margins.
	 *
	 * @return Insets   an Insets object
	 */
    public Insets getMargins() {
        return new Insets( topMargin, leftMargin,
                           bottomMargin, rightMargin );
	}

	/**
	 * Returns the left, right, top, and bottom insets of the background of the
	 * current style.
	 *
	 * @return Insets	an Insets object
	 */
    public Insets getInsets() {
        Insets insets = getMargins();
        int outerEdge = borderThickness + defaultThickness;
        insets.top += outerEdge;
        insets.left += outerEdge;
        insets.bottom += outerEdge;
        insets.right += outerEdge;
        if ( shadowXOffset > 0 ) {
            insets.right += shadowXOffset;
        }
        else {
            insets.left -= shadowXOffset;
        }
        if ( shadowYOffset > 0 ) {
            insets.bottom += shadowYOffset;
        }
        else {
            insets.top -= shadowYOffset;
        }
        return insets;
    }

    private void paintShadow( GadgetGraphics g, int x, int y, int width, int height ) {
        if ( shadow != Gadget.transparent && shadow != null ) {
            g.setColor( shadow );
            if ( type == ROUND_RECT ) {

                int arcSize = minMargin * 8;

                g.fillRoundRect( x + shadowXOffset,
                                 y + shadowYOffset,
                                 width, height,
                                 arcSize, arcSize );
            }
            else {
                g.fillRect( x + shadowXOffset,
                            y + shadowYOffset,
                            width, height );
            }
        }
    }

    private void paintBackground( GadgetGraphics g,
                                  int x, int y, int width, int height,
                                  Color background ) {
		if ( background == Gadget.transparent ) {
		    return;
		}
		g.setColor( background );
		if ( type == ROUND_RECT ) {
		    int arcSize = minMargin*8;
		    if ( borderThickness > 0 ) {
    			g.fillRoundRect( x+1, y+1, width-2, height-2, arcSize, arcSize );
		    }
		    else {
    			g.fillRoundRect( x, y, width, height, arcSize, arcSize );
		    }
		}
		else {
			g.fillRect( x, y, width, height );
        }
    }

    /**
	 * Paints the border.
     *
     * @param g				the GadgetGraphics object
     * @param x				x coordinate
     * @param y				y coordinate
     * @param width			width
     * @param height		height
     * @param background	background color
     */
    protected void paintBorder(
		GadgetGraphics g, int x, int y, int width, int height, Color background ) {

        paintBorder( g, x, y, width, height, type,
                     background, border, borderThickness, minMargin, closeShaved );
    }

    /**
     * Paints the border
     *
     * @param g				the GadgetGraphics object
     * @param x				x coordinate
     * @param y				y coordinate
     * @param width			width
     * @param height		height
     * @param type			type
     * @param background	background color
     * @param border		border color
     * @param borderThickness	border thickness
     */
    public static void paintBorder(
        GadgetGraphics g, int x, int y, int width, int height,
        int type, Color background, Color border, int borderThickness ) {

        paintBorder( g, x, y, width, height, type,
                     background, border, borderThickness, 0, false );
    }

    /**
	 * Paints the border.
     *
     * @param g				the GadgetGraphics object
     * @param x				x coordinate
     * @param y				y coordinate
     * @param width			width
     * @param height		height
     * @param type			type
     * @param background	background color
     * @param border		border color
     * @param borderThickness	border thickness
     * @param minMargin		mimimum margin
     * @param closeShaved	is/is not closeShaved
     */
    public static void paintBorder(
        GadgetGraphics g, int x, int y, int width, int height, int type,
        Color background, Color border, int borderThickness, int minMargin, boolean closeShaved ) {

        if ( border == Gadget.defaultColor ) {
            if ( ( type == LINE ) ||
                 ( type == ROUND_RECT ) ) {
                if ( background == Color.black ) {
                    border = Color.white;
                }
                else {
                    border = Color.black;
                }
            }
            else {
                if ( background == Gadget.transparent ) {
                    border = Color.lightGray;
                }
                else {
                    border = background;
                }
            }
        }

        if ( border != Gadget.transparent && border != null ) {
            g.setColor( border );

            boolean raised = false;
            if ( ( type == THREED_OUT ) ||
                 ( type == EMBOSSED_OUT ) ) {
                raised = true;
            }

            boolean etched = false;
            if ( ( type == ETCHED_IN ) ||
                 ( type == EMBOSSED_OUT ) ) {
                etched = true;
            }

            if ( ( type == THREED_IN ) ||
                 ( type == THREED_OUT ) ||
                 ( type == ETCHED_IN ) ||
                 ( type == EMBOSSED_OUT ) ) {
                g.draw3DRect(x,y,width,height,raised,etched,borderThickness,closeShaved);
            }
            else if ( type == ROUND_RECT ) {
    		    int arcSize = minMargin*8;
                g.drawRoundRect(x,y,width-1,height-1,arcSize,arcSize,borderThickness );
            }
            else if ( type == LINE ) {
                g.drawRect(x,y,width-1,height-1,borderThickness);
            }
        }
    }

    /**
     * Draws a rectangle based on the current margins and insets.
	 * Rectangle uses the thickness parameter for dashOnLen,
	 * dashOfflen, and alignment.
     *
     * @param g			GadgetGraphics object to do the painting
     * @param fg		color
     * @param thickness thickness
	 * @see GadgetGraphics#drawRect
     */
    public void drawFocus(GadgetGraphics g, Color fg, int thickness) {
	    g.setColor(fg);
        Insets margins = getMargins();
        Insets insets = getInsets();
        int fx = insets.left - ( 1 + (int)( (margins.left) / 2 ));
        int fy = insets.top - ( 1 + (int)( (margins.top) / 2 ));
        int fw = width - (2*fx);
        int fh = height - (2*fy);
        g.drawRect(fx,fy,fw-1,fh-1,
                   thickness,thickness,thickness);
    }

    /**
     * drawFocus(g) method calls the three argument drawFocus method
	 * with g, the foreground color, and the focusThickness integer.
     *
     * @param g		the GadgetGraphics object to do the painting.
     */
    public void drawFocus(GadgetGraphics g) {
        drawFocus(g, getForeground(g), focusThickness);
    }

	/**
     * @param g		the GadgetGraphics object to do the painting.
	 */
	public void update( GadgetGraphics g ) {
        realNormalBg = normalBackground;
        realSelectedBg = selectedBackground;
        Color bg = getBackground(g);
        if ( ( type == ROUND_RECT ) ||
             ( ( ( shadowXOffset != 0 ) ||
                 ( shadowYOffset != 0 ) ) &&
               ( shadow != null ) &&
               ( shadow != Gadget.transparent ) &&
               ( bg != null ) &&
               ( bg != Gadget.transparent ) ) ) {
            normalBackground = Gadget.transparent;
            selectedBackground = Gadget.transparent;
        }
        super.update(g);
        normalBackground = realNormalBg;
        selectedBackground = realSelectedBg;
    }

    /**
	 * Paints the shadow, background, and border, and draws the focus.
     * @param g		the GadgetGraphics object to do the painting.
     */
    public void paint( GadgetGraphics g ) {

        normalBackground = realNormalBg;
        selectedBackground = realSelectedBg;

        int mainWidth = width - shadowXOffset;
        int mainHeight = height - shadowYOffset;
        int mainX = 0;
        int mainY = 0;
        if ( shadowXOffset < 0 ) {
            mainX += shadowXOffset;
        }
        if ( shadowYOffset < 0 ) {
            mainY += shadowYOffset;
        }

        Color fg = getForeground(g);
        Color bg = getBackground(g);

        if ( ( ( shadowXOffset != 0 ) ||
               ( shadowYOffset != 0 ) ) &&
             ( shadow != null ) &&
             ( shadow != Gadget.transparent ) &&
             ( bg != null ) &&
             ( bg != Gadget.transparent ) ) {
            paintShadow( g, mainX, mainY, mainWidth, mainHeight );
            paintBackground( g, mainX, mainY, mainWidth, mainHeight, bg );
        }
        else if ( type == ROUND_RECT ) {
            paintBackground( g, mainX, mainY, mainWidth, mainHeight, bg );
        }

        if ( ( defaultThickness > 0 ) &&
             ( isNowDefault() ) ) {
    	    g.setColor(fg);
	        g.drawRect(mainX,mainY,mainWidth-1,mainHeight-1);
            mainX += defaultThickness;
            mainY += defaultThickness;
            int defaultThickness2 = defaultThickness*2;
            mainWidth -= defaultThickness2;
            mainHeight -= defaultThickness2;
        }

        if ( type != NONE &&
             border != null &&
             border != Gadget.transparent &&
             borderThickness > 0 ) {
            paintBorder( g, mainX, mainY, mainWidth, mainHeight, bg );
        }

	    if ( ( focusThickness > 0 ) &&
	         ( getMinMargin() > 0 ) &&
	         hasFocus() ) {
    	    drawFocus(g,fg,focusThickness);
	    }
    }
}
