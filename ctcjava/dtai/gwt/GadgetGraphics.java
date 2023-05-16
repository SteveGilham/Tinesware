/****************************************************************
 **
 **  $Id: GadgetGraphics.java,v 1.33 1998/02/03 16:10:02 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/GadgetGraphics.java,v $
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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.util.Vector;
import dtai.util.Debug;

/**
 * GadgetGraphics is the abstract base class for all graphic
 * contexts for various devices.
 *
 * It was derived from java.awt.Graphics
 * @version 1.1
 * @author DTAI, Incorporated
 */
public class GadgetGraphics {

    public static final int LEFT = -1;
    public static final int CENTER = 0;
    public static final int RIGHT = 1;

    private Graphics awtGraphics;
    private int xOffset;
    private int yOffset;
    private int origWidth;
    private int origHeight;
    //private Vector damageList;
    private Gadget gadget;
    private boolean paintingAll;
    //private boolean noClipping = false;
    private boolean disposed = false;

    public Color ancestorNormalBackground = null;
    public Color ancestorSelectedBackground = null;
    public Color ancestorNormalForeground = null;
    public Color ancestorSelectedForeground = null;
    public Font ancestorFont = null;

    private static final double BRIGHTER_FACTOR = 0.8;
    private static final double DARKER_FACTOR = 0.65;
    // awt Color.brighter AND darker used FACTOR = 0.7

    /**
     * Constructs a new GadgetGraphics Object. Graphic contexts cannot be
     * created directly. They must be obtained from another graphics
     * context or created by a Gadget.
     * @param awtGraphics the Graphics from which this was created
     * @see Gadget#getGadgetGraphics
     * @see dtai.gwt.Gadget#getGadgetGraphics
     * @see #create
     */
    protected GadgetGraphics( Graphics awtGraphics ) {
        this(awtGraphics,0,0,-1,-1,null,true/*,null*/);
    }

    /**
     * Constructs a new GadgetGraphics Object. Graphic contexts cannot be
     * created directly. They must be obtained from another graphics
     * context or created by a Gadget.
     * @param Graphics the Graphics from which this was created
     * @param origWidth the original width
     * @param origHeight the original height
     * @see dtai.gwt.Gadget#getGadgetGraphics
     * @see #create
     */
    public GadgetGraphics( Graphics awtGraphics, int origWidth, int origHeight ) {
        this(awtGraphics,0,0,origWidth,origHeight,null,true/*,null*/);
    }

    /**
     * Constructs a new GadgetGraphics Object. Graphic contexts cannot be
     * created directly. They must be obtained from another graphics
     * context or created by a Gadget.
     * @param Graphics the Graphics from which this was created
     * @param xOffset the x offset
     * @param yOffset the y offset
     * @param origWidth the original width
     * @param origHeight the original height
     * @param Gadget the Gadget from which this was created
     * @see Gadget#getGadgetGraphics
     * @see #create
     */
    public GadgetGraphics(Graphics awtGraphics, int xOffset, int yOffset,
                          int origWidth, int origHeight, Gadget gadget) {
        this(awtGraphics,xOffset,yOffset,origWidth,origHeight,gadget,true/*,null*/);
    }

    /**
     * Constructs a new GadgetGraphics Object. Graphic contexts cannot be
     * created directly. They must be obtained from another graphics
     * context or created by a Gadget.
     * @param Graphics the Graphics from which this was created
     * @param xOffset the x offset
     * @param yOffset the y offset
     * @param origWidth the original width
     * @param origHeight the original height
     * @param Gadget the Gadget from which this was created
     * @param paintingAll if false, only paint's things that were marked as needing to be painted
     * @see Gadget#getGadgetGraphics
     * @see #create
     */

// for debugging Navigator 3.01 workaround where we can't allow multiple
// active Graphics objects at the same time
/*
static int numactive=0;
int localnumactive = 1;
Exception active;
static Vector actives = new Vector();
*/

    protected GadgetGraphics(Graphics awtGraphics, int xOffset, int yOffset,
                             int origWidth, int origHeight,
                             Gadget gadget, boolean paintingAll/*, Vector damageList*/) {
/*
synchronized(actives) {
numactive++;
if(numactive>1) {
    int count = actives.size();
    for (int i = 0; i < count; i++) {
        Exception e = (Exception)actives.elementAt(i);
        e.printStackTrace();
    }
    System.err.println("numactive "+numactive);
}
actives.addElement(active=new Exception());
}
*/
        this.awtGraphics = awtGraphics;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.origWidth = origWidth;
        this.origHeight = origHeight;
        this.gadget = gadget;
        this.paintingAll = paintingAll;
        //this.damageList = damageList;
        if ( origWidth >= 0 && origHeight >= 0 ) {
            clipRect(0,0,origWidth,origHeight);
        }
    }

    /**
     * Creates a new GadgetGraphics Object that is a copy of the original GadgetGraphics Object.
     * @return GadgetGraphics
     */
    public GadgetGraphics create() {
/*
if (disposed) {
    disposeEvent.printStackTrace();
    System.err.println();
}
*/
        return new GadgetGraphics(
                                  /*(noClipping ? awtGraphics : awtGraphics.create()),*/
                                  awtGraphics.create(),
                                  xOffset,yOffset,origWidth,origHeight,
                                  gadget,paintingAll/*,damageList*/);
    }

    /**
     * Creates a new Graphics Object with the specified parameters, based on the original
     * Graphics Object.
     * This method translates the specified parameters, x and y, to
     * the proper origin coordinates and then clips the Graphics Object to the
     * area.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the area
     * @param height the height of the area
     * @see #translate
     * @return GadgetGraphics
     */
    public GadgetGraphics create(int x, int y, int width, int height) {
        return new GadgetGraphics(awtGraphics.create(),
                                  this.xOffset+x,
                                  this.yOffset+y,
                                  width,height,
                                  gadget,paintingAll/*,damageList*/);
    }

    /**
     * If false, only paints things that were marked as needing to be painted.
     * @param paintingAll the flag
     */
    public void setPaintingAll(boolean paintingAll) {
        this.paintingAll = paintingAll;
    }

    /**
     * If false, only paints things that were marked as needing to be painted.
     * @return the flag
     */
    public boolean isPaintingAll() {
        return paintingAll;
    }

    /**
     * This is a special function for printing with Graphics that don't support clipping.
     * Don't call it otherwise.  If true, it disables clipping.
     * @param noClipping the flag
     */
     /*
    public void setNoClipping(boolean noClipping) {
        this.noClipping = noClipping;
    }
    */

    /**
     * If true, the Graphics resources have been disposed of and the GadgetGraphics should
     * not be used.
     * @return the flag
     */
    public boolean isDisposed() {
        return disposed;
    }

    /**
     * Called by GadgetShell to set a vector of Rectangles indicating damaged areas
     * @param damageList the flag
    public void setDamageList(Vector damageList) {
        this.damageList = damageList;
    }
     */

    /**
     * returns a vector of Rectangles indicating damaged areas
     * @param the vector of damage Rectangles
    public Vector getDamageList() {
        return damageList;
    }
     */

    /**
     * tests intersection of two rectangles, taking into account the x/y offsets of the
     * left rectangle
     */
    private boolean intersects(Rectangle myRect, Rectangle rect) {
    	return !((myRect.x + myRect.width - xOffset <= rect.x) ||
    		 (myRect.y + myRect.height - yOffset <= rect.y) ||
    		 (myRect.x - xOffset >= rect.x + rect.width) ||
    		 (myRect.y - yOffset >= rect.y + rect.height));
    }

    /**
     * returns true if the given rectangle intersects any of the damage rects
     * @param rect the test rect
     * @return true if it intersects
    public boolean damageIntersects(Rectangle rect) {
        int size = damageList.size();
        for (int j = 0; j < size; j++) {
            Rectangle damaged = (Rectangle)damageList.elementAt(j);
            if (intersects(damaged,rect)) {
                return true;
            }
        }
        return false;
    }
     */

    /**
     * Translates the specified parameters into the origin of the graphics context. All subsequent
     * operations on this graphics context will be relative to this origin.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void translate(int x, int y) {
        xOffset += x;
        yOffset += y;
    }

    /**
     * Gets the offset as translated from the original point.
     * @return Point
     */
    public Point getOffset() {
        return new Point(xOffset,yOffset);
    }

    /**
     * Gets the original AWT graphics from which this GadgetGraphics object was built.
     * @return Graphics
     */
    public Graphics getOrigGraphics() {
		return awtGraphics;
    }

    /**
     * Gets the current color.
     * @see #setColor
     * @return Color
     */
    public Color getColor() {
        return awtGraphics.getColor();
    }

    /**
     * Sets the current color to the specified color. All subsequent graphics operations
     * will use this specified color.
     * @param c the color to be set
     * @see Color
     * @see #getColor
     */
    public void setColor(Color c) {
        awtGraphics.setColor(c);
    }

    /**
     * Sets the paint mode to overwrite the destination with the
     * current color.
     */
    public void setPaintMode() {
        awtGraphics.setPaintMode();
    }

    /**
     * Sets the paint mode to alternate between the current color
     * and the new specified color.  When drawing operations are
     * performed, pixels which are the current color will be changed
     * to the specified color and vice versa.  Pixels of colors other
     * than those two colors will be changed in an unpredictable, but
     * reversible manner - if you draw the same figure twice then all
     * pixels will be restored to their original values.
     * @param c1 the second color
     */
    public void setXORMode(Color c1) {
        awtGraphics.setXORMode(c1);
    }

    /**
     * Gets the current font.
     * @see #setFont
     * @return Font
     */
    public Font getFont() {
        return awtGraphics.getFont();
    }

    /**
     * Sets the font for all subsequent text-drawing operations.
     * @param font the specified font
     * @see Font
     * @see #getFont
     * @see #drawString
     * @see #drawBytes
     * @see #drawChars
    */
    public void setFont(Font font) {
        awtGraphics.setFont(font);
    }

    /**
     * Gets the current font metrics.
     * @see #getFont
     * @return FontMetrics
     */
    public FontMetrics getFontMetrics() {
		return getFontMetrics(getFont());
    }

    /**
     * Gets the current font metrics for the specified font.
     * @param f the specified font
     * @see #getFont
     * @see #getFontMetrics
     * @return FontMetrics
     */
    public FontMetrics getFontMetrics(Font f) {
        if (f instanceof SpecialFont) {
            return ((SpecialFont)f).getMetrics();
        } else {
    		return awtGraphics.getFontMetrics(f);
    	}
    }


    /**
     * Returns the bounding rectangle of the current clipping area.
     * @see #clipRect
     * @return Rectangle
     */
    public Rectangle getClipRect() {
        Rectangle clip = awtGraphics.getClipRect();
		// I can't believe it, but JavaStation does not give me
		// a copy!!!
		clip = new Rectangle(clip.x,clip.y,clip.width,clip.height);
        clip.x -= xOffset;
        clip.y -= yOffset;
        return clip;
    }

    /**
     * Clips to a rectangle. The resulting clipping area is the
     * intersection of the current clipping area and the specified
     * rectangle. Graphic operations have no effect outside of the
     * clipping area.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @see #getClipRect
     */
    public void clipRect(int x, int y, int width, int height) {
        /*if (!noClipping) {*/
            awtGraphics.clipRect(x+xOffset,y+yOffset,width,height);
        /*}*/
    }

    /**
     * Copies an area of the screen.
     * @param x the x-coordinate of the source
     * @param y the y-coordinate of the source
     * @param width the width
     * @param height the height
     * @param dx the horizontal distance
     * @param dy the vertical distance
     */
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        awtGraphics.copyArea(x+xOffset,y+yOffset,width,height,dx,dy);
    }

    /**
     * Draws a line between the coordinates (x1,y1) and (x2,y2). The line is drawn
     * below and to the left of the logical coordinates.
     * @param x1 the first point's x coordinate
     * @param y1 the first point's y coordinate
     * @param x2 the second point's x coordinate
     * @param y2 the second point's y coordinate
     */
    public void drawLine(int x1, int y1, int x2, int y2) {
        awtGraphics.drawLine(x1+xOffset,y1+yOffset,x2+xOffset,y2+yOffset);
    }

    /**
     * Draws a line with thickness between the coordinates
     * (x1,y1) and (x2,y2). The line is drawn
     * below and to the left of the logical coordinates.
     * @param x1 the first point's x coordinate
     * @param y1 the first point's y coordinate
     * @param x2 the second point's x coordinate
     * @param y2 the second point's y coordinate
     * @param thickness the line thickness, to be distributed around both sides of the line
     */
    public void drawLine(int x1, int y1, int x2, int y2,int thickness) {
        drawLine(x1,y1,x2,y2,thickness,1,0,CENTER,0);
    }

    /**
     * Draws a line with thickness and possible dash between the coordinates
     * (x1,y1) and (x2,y2). The line is drawn
     * below and to the left of the logical coordinates.
     * @param x1 the first point's x coordinate
     * @param y1 the first point's y coordinate
     * @param x2 the second point's x coordinate
     * @param y2 the second point's y coordinate
     * @param thickness the line thickness, to be distributed around both sides of the line
     * @param dashOnLen the pixel length of the dash (must be greater than zero)
     * @param dashOffLen the pixel length of the space between dashes (set to zero if no dashes)
     */
    public void drawLine(int x1, int y1, int x2, int y2,
                         int thickness, int dashOnLen, int dashOffLen) {
        drawLine(x1,y1,x2,y2,thickness,dashOnLen,dashOffLen,CENTER,0);
    }

    private Point calcLineSide( int idx, double sintheta, double costheta,
                                int align ) {
        int delta;
        int dir;
        if ( align == CENTER ) {
            delta = ( idx + 1 ) / 2;
            dir = ( ( idx % 2 ) == 0 ) ? -1 : 1;
        }
        else {
            delta = idx;
            if ( align == RIGHT ) {
                dir = 1;
            }
            else {
                dir = -1;
            }
        }

        double roundoff;
        if ( sintheta > 0 ) {
            roundoff = 0.5;
        }
        else {
            roundoff = -0.5;
        }

        int x_offset = -(int)( roundoff +
                              ( sintheta * delta * dir ) );

        if ( costheta > 0 ) {
            roundoff = 0.5;
        }
        else {
            roundoff = -0.5;
        }

        int y_offset = (int)( roundoff +
                              ( costheta * delta * dir ) );

        return new Point( x_offset, y_offset );
    }

    /**
     * Draws a line with thickness and possible dash between the coordinates
     * (x1,y1) and (x2,y2). The line is drawn
     * below and to the left of the logical coordinates.
     * @param x1 the first point's x coordinate
     * @param y1 the first point's y coordinate
     * @param x2 the second point's x coordinate
     * @param y2 the second point's y coordinate
     * @param thickness the line thickness, to be distributed around both sides of the line
     * @param dashOnLen the pixel length of the dash (must be greater than zero)
     * @param dashOffLen the pixel length of the space between dashes (set to zero if no dashes)
     * @param dashStart pixel length to skip from beginning of first dash
     * @param align CENTER thick lines over the line from point to point, or put it
     * to the RIGHT or to the LEFT.  IMPORTANT:  RIGHT does NOT always mean right.  If the line
     * goes up (y2 < y1), then RIGHT is right.  If the line goes down, RIGHT is left.  If the line
     * goes from left to right, RIGHT is down, etc.  It is really based on the direction of motion
     * from the first point to the next.
     * @return dashEnd pixel length from beginning of last dash to beginning of what would be
     * the next dash, if the line were longer. Pass this as dashStart for next line if it
     * connects to this line's end
     */
    public int drawLine(int x1, int y1, int x2, int y2,
                        int thickness, int dashOnLen, int dashOffLen,
                        int align, int dashStart ) {
        if ( ( dashOnLen == 0 ) ||
             ( thickness < 1 ) ||
             ( ( x2 == x1 ) &&
               ( y2 == y1 ) ) ) {
            return dashStart;
        }
        int newDashStart = 0;
        if ( thickness == 1 ) {
            newDashStart = drawSplinter( x1, y1, x2, y2,
                                             dashOnLen, dashOffLen, dashStart );
            return newDashStart;
        }
        double delta;
        int dir = 0;
        if ( x2 == x1 ) {
            if ( align != CENTER ) {
                if ( ( ( align == RIGHT ) && ( y2 > y1 ) ) ||
                     ( ( align == LEFT ) && ( y2 < y1 ) ) ) {
                    dir = -1;
                }
                else {
                    dir = 1;
                }
            }
            for ( int idx = 0; idx < thickness; idx++ ) {
                if ( align == CENTER ) {
                    delta = ( idx + 1 ) / 2;
                    dir = ( ( idx % 2 ) == 0 ) ? -1 : 1;
                }
                else {
                    delta = idx;
                }
                int xoff = (int)(delta*dir);
                newDashStart = drawSplinter( x1 + xoff, y1, x2 + xoff, y2,
                              dashOnLen, dashOffLen, dashStart );
            }
            return newDashStart;
        }
        else if ( y2 == y1 ) {
            if ( align != CENTER ) {
                if ( ( ( align == RIGHT ) && ( x2 > x1 ) ) ||
                     ( ( align == LEFT ) && ( x2 < x1 ) ) ) {
                    dir = 1;
                }
                else {
                    dir = -1;
                }
            }
            for ( int idx = 0; idx < thickness; idx++ ) {
                if ( align == CENTER ) {
                    delta = ( idx + 1 ) / 2;
                    dir = ( ( idx % 2 ) == 0 ) ? -1 : 1;
                }
                else {
                    delta = idx;
                }
                int yoff = (int)(delta*dir);
                newDashStart = drawSplinter( x1, y1 + yoff, x2, y2 + yoff,
                              dashOnLen, dashOffLen, dashStart );
            }
            return newDashStart;
        }

        int min_idx;
        int max_idx;
        if ( align == CENTER ) {
            max_idx = thickness-1;
            if ( thickness > 1 ) {
                min_idx = thickness-2;
            }
            else {
                min_idx = max_idx;
            }
        }
        else {
            min_idx = 0;
            max_idx = thickness-1;
        }

        double theta = Math.atan2( (double)( y2 - y1 ),
                                   (double)( x2 - x1 ) );
        double sintheta = Math.sin( theta );
        double costheta = Math.cos( theta );

        Point offset1 = calcLineSide( min_idx, sintheta, costheta, align );
        Point offset2 = calcLineSide( max_idx, sintheta, costheta, align );
        newDashStart = drawThickLine( x1 + offset1.x, y1 + offset1.y,
                                      x2 + offset1.x, y2 + offset1.y,
                                      x1 + offset2.x, y1 + offset2.y,
                                      x2 + offset2.x, y2 + offset2.y, false,
                                      dashOnLen, dashOffLen, dashStart );

        return newDashStart;
    }

    private int drawSplinter( int x1, int y1, int x2, int y2,
                              int dashOnLen, int dashOffLen, int dashStart ) {
        if ( dashOffLen <= 0 ) {
            drawLine( x1, y1, x2, y2 );
            return 0;
        }
        else {
            return drawThickLine( x1, y1, x2, y2, x1, y1, x2, y2, true,
                                  dashOnLen, dashOffLen, dashStart );
        }
    }

    private int drawThickLine( int x1, int y1, int x2, int y2,
                               int x3, int y3, int x4, int y4,
                               boolean lineOnly,
                               int dashOnLen, int dashOffLen, int dashStart ) {
        if ( dashOffLen <= 0 ) {
            int x[] = { x1, x2, x4, x3 };
            int y[] = { y1, y2, y4, y3 };
            fillPolygon( x, y, 4 );
            return 0;
        }
        // THIS IS ALGORITHM FOR DRAWING A DASHED LINE.
        // IT IS KIND OF AN APPROXIMATION.  SOMEBODY SMARTER THAN ME COULD
        // COME IN HERE ONE DAY AND REALLY GET IT RIGHT.
        //    -- RWK
        int xdist = x2 - x1;
        int ydist = y2 - y1;
        double len = Math.sqrt( (double)( ( xdist*xdist ) + ( ydist*ydist ) ) );
        double xnorm = xdist / len;
        double ynorm = ydist / len;
        int dashlen = dashOnLen + dashOffLen;
        int numdashes = (int)( (len+dashStart)/dashlen );
        int startXNorm = (int)(dashStart * xnorm);
        int startYNorm = (int)(dashStart * ynorm);
        int prevx1 = x1 - startXNorm;
        int prevy1 = y1 - startYNorm;
        int prevx3 = x3 - startXNorm;
        int prevy3 = y3 - startYNorm;
        for ( int idx = 1; idx <= ( numdashes + 1 ); idx++ ) {
            int nextx1 = (int)( prevx1 + ( xnorm * dashOnLen ) );
            int nexty1 = (int)( prevy1 + ( ynorm * dashOnLen ) );
            if ( Math.abs( nextx1 - x1 ) > Math.abs( xdist ) ) {
                nextx1 = x2;
            }
            if ( Math.abs( nexty1 - y1 ) > Math.abs( ydist ) ) {
                nexty1 = y2;
            }
            if ( nextx1 > prevx1 ) {
                nextx1--;
            }
            if ( nextx1 < prevx1 ) {
                nextx1++;
            }
            if ( nexty1 > prevy1 ) {
                nexty1--;
            }
            if ( nexty1 < prevy1 ) {
                nexty1++;
            }
            int nextx3 = 0;
            int nexty3 = 0;
            if ( ! lineOnly ) {
                nextx3 = (int)( prevx3 + ( xnorm * dashOnLen ) );
                nexty3 = (int)( prevy3 + ( ynorm * dashOnLen ) );
                if ( Math.abs( nextx3 - x3 ) > Math.abs( xdist ) ) {
                    nextx3 = x4;
                }
                if ( Math.abs( nexty3 - y3 ) > Math.abs( ydist ) ) {
                    nexty3 = y4;
                }
            }
            if ( ( idx > 1 ) ||
                 ( dashStart < dashOnLen ) ) {
                if ( idx == 1 ) {
                    prevx1 -= startXNorm;
                    prevy1 -= startYNorm;
                    if ( ! lineOnly ) {
                        prevx3 -= startXNorm;
                        prevy3 -= startYNorm;
                    }
                }
                if ( lineOnly ) {
                    if ( idx == 1 ) {
                        drawLine( x1, y1, nextx1, nexty1 );
                    }
                    else {
                        drawLine( prevx1, prevy1, nextx1, nexty1 );
                    }
                }
                else {
                    if ( idx == 1 ) {
                        int x[] = { x1, nextx1, nextx3, x3 };
                        int y[] = { y1, nexty1, nexty3, y3 };
                        fillPolygon( x, y, 4 );
                    }
                    else {
                        int x[] = { prevx1, nextx1, nextx3, prevx3 };
                        int y[] = { prevy1, nexty1, nexty3, prevy3 };
                        fillPolygon( x, y, 4 );
                    }
                }
            }
            prevx1 = (int)( (x1 - startXNorm) + ( ( idx * dashlen ) * xnorm ) );
            prevy1 = (int)( (y1 - startYNorm) + ( ( idx * dashlen ) * ynorm ) );
            if ( ! lineOnly ) {
                prevx3 = (int)( (x3 - startXNorm) + ( ( idx * dashlen ) * xnorm ) );
                prevy3 = (int)( (y3 - startYNorm) + ( ( idx * dashlen ) * ynorm ) );
            }
        }
        dashStart = ((int)( len % dashlen )) + dashStart;
        if ( dashStart > dashlen ) {
            dashStart -= dashlen;
        }
        return dashStart;
    }

    /**
     * Fills the specified rectangle with the current color.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @see #drawRect
     * @see #clearRect
     */
    public void fillRect(int x, int y, int width, int height) {
        Gadget gadget = this.gadget;
        if (gadget != null) {
            GadgetShell shell = gadget.getShell();
            int level = Debug.getLevel();
            if (shell != null && (level >= 50 && level <= 60)) {
                Color saveColor = getColor();
                setColor(Color.red);
                awtGraphics.fillRect(x+xOffset,y+yOffset,width,height);
                shell.exposeGraphics();
                setColor(saveColor);
                delay(level-50);
                Rectangle rect = new Rectangle(x+xOffset,y+yOffset,width,height);
                System.err.println("fill rect for "+gadget.getClass().getName()+
                   " "+rect);
                if (!rect.intersects(awtGraphics.getClipRect())) {
                    System.err.println("************* does not intersect *******");
                }
            }
        }
        awtGraphics.fillRect(x+xOffset,y+yOffset,width,height);
    }

    public void setGadget(Gadget gadget) {
        this.gadget = gadget;
    }

    public Gadget getGadget() {
        return gadget;
    }

    private void delay(int delayVal) {
        synchronized(this) {
            try {
                wait(50*delayVal);
            } catch (InterruptedException ie) {
            }
        }
    }

    /**
     * Draws the outline of the specified rectangle using the current color.
     * Use drawRect(x, y, width-1, height-1) to draw the outline inside the specified
     * rectangle.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @see #fillRect
     * @see #clearRect
     */
    public void drawRect(int x, int y, int width, int height) {
    	drawLine(x, y, x + width, y);
    	drawLine(x + width, y, x + width, y + height);
    	drawLine(x, y, x, y + height);
    	drawLine(x, y + height, x + width, y + height);
    }

    /**
     * Draws the outline of the specified rectangle using the current color.
     * Use drawRect(x, y, width-1, height-1) to draw the outline inside the specified
     * rectangle.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param thickness the line thickness, drawn inside
     * @see #fillRect
     * @see #clearRect
     */
    public void drawRect(int x, int y, int width, int height, int thickness) {
        for ( int idx = 0; idx < thickness; idx++ ) {
            int idx2 = idx*2;
            drawRect( x + idx, y + idx,
                      ( width - idx2 ),
                      ( height - idx2 ) );
        }
    }

    /**
     * Draws the outline of the specified rectangle using the current color.
     * Use drawRect(x, y, width-1, height-1) to draw the outline inside the specified
     * rectangle.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param thickness the line thickness, drawn inside
     * @param dashOnLen the pixel length of the dash (must be greater than zero)
     * @param dashOffLen the pixel length of the space between dashes (set to zero if no dashes)
     * @see #fillRect
     * @see #clearRect
     */
    public void drawRect(int x, int y, int width, int height, int thickness,
                         int dashOnLen, int dashOffLen) {
        drawRect(x,y,width,height,thickness,dashOnLen,dashOffLen,RIGHT);
    }

    /**
     * Draws the outline of the specified rectangle using the current color.
     * Use drawRect(x, y, width-1, height-1) to draw the outline inside the specified
     * rectangle.
     * @param x 		the x coordinate
     * @param y 		the y coordinate
     * @param width 	the width of the rectangle
     * @param height 	the height of the rectangle
     * @param thickness the line thickness, drawn inside
     * @param dashOnLen the pixel length of the dash (must be greater than zero)
     * @param dashOffLen the pixel length of the space between dashes (set to zero if no dashes)
     * @param align 	CENTER thick lines over the line from point to point, or put it
     * to the RIGHT or to the LEFT.  IMPORTANT:  RIGHT does NOT always mean right.  If the line
     * goes up (y2 < y1), then RIGHT is right.  If the line goes down, RIGHT is left.  If the line
     * goes from left to right, RIGHT is down, etc.  It is really based on the direction of motion
     * from the first point to the next.  The rectangle is drawn in a clockwise direction, so RIGHT
     * means the lines are drawn inside, LEFT outside.
     * @see #fillRect
     * @see #clearRect
     */
    public void drawRect(int x, int y, int width, int height, int thickness,
                         int dashOnLen, int dashOffLen, int align) {
        int dashStart = 0;
    	dashStart = drawLine(x, y, x + width, y,thickness,
    	                     dashOnLen,dashOffLen,align,dashStart);
    	dashStart = drawLine(x + width, y, x + width, y + height,thickness,
    	                     dashOnLen,dashOffLen,align,dashStart);
    	dashStart = drawLine(x + width, y + height,x, y + height, thickness,
    	                     dashOnLen,dashOffLen,align,dashStart);
    	dashStart = drawLine(x, y + height,x, y, thickness,
    	                     dashOnLen,dashOffLen,align,dashStart);
    }

    /**
     * Clears the specified rectangle by filling it with the current background color
     * of the current drawing surface.
     * Which drawing surface it selects depends on how the graphics context
     * was created.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @see #fillRect
     * @see #drawRect
     */
    public void clearRect(int x, int y, int width, int height) {
        if ( gadget != null ) {
            Color cur = getColor();
            setColor(gadget.getFinalBackground());
            fillRect(x,y,width,height);
            setColor(cur);
        }
    }

    /**
     * Draws an outlined rounded corner rectangle using the current color.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param arcWidth the horizontal diameter of the arc at the four corners
     * @param arcHeight the horizontal diameter of the arc at the four corners
     * @see #fillRoundRect
     */
    public void drawRoundRect(int x, int y, int width, int height,
                                       int arcWidth, int arcHeight) {
        awtGraphics.drawRoundRect(x+xOffset,y+yOffset,width,height,arcWidth,arcHeight);
    }

    /**
     * Draws an outlined rounded corner rectangle using the current color.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param arcWidth the horizontal diameter of the arc at the four corners
     * @param arcHeight the horizontal diameter of the arc at the four corners
     * @param thickness the line thickness, drawn inside
     * @see #fillRoundRect
     */
    public void drawRoundRect(int x, int y, int width, int height,
                              int arcWidth, int arcHeight,int thickness) {
        for ( int idx = 0; idx < thickness; idx++ ) {
            int idx2 = idx*2;
            int tx = x + idx;
            int ty = y + idx;
            int tw = width - idx2;
            int th = height - idx2;
            int taw = arcWidth - idx2;
            int tah = arcHeight - idx2;

            drawRoundRect( x + idx, y + idx, tw, th, taw, tah );
            if ( ( idx + 1 ) < thickness ) {
                drawRoundRect( tx, ty, tw, th - 1, taw - 1, tah );
                drawRoundRect( tx + 1, ty, tw, th - 1, taw - 1, tah );
                drawRoundRect( tx, ty, tw - 1, th, taw, tah - 1 );
                drawRoundRect( tx, ty + 1, tw - 1, th, taw, tah - 1 );
            }
        }
    }

    /**
     * Draws a rounded rectangle filled in with the current color.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param arcWidth the horizontal diameter of the arc at the four corners
     * @param arcHeight the horizontal diameter of the arc at the four corners
     * @see #drawRoundRect
     */
    public void fillRoundRect(int x, int y, int width, int height,
                                       int arcWidth, int arcHeight) {
        awtGraphics.fillRoundRect(x+xOffset,y+yOffset,width,height,arcWidth,arcHeight);
    }

    /**
     * Draws a highlighted 3-D rectangle.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param raised a boolean that states whether the rectangle is raised or not
     */
    public void draw3DRect(int x, int y, int width, int height, boolean raised) {
    	Color c = getColor();
    	Color brighter = brighter(c);
    	Color darker = darker(c);

    	setColor(raised ? brighter : darker);
    	drawLine(x, y, x, y + height);
    	drawLine(x + 1, y, x + width - 1, y);
    	setColor(raised ? darker : brighter);
    	drawLine(x + 1, y + height, x + width, y + height);
    	drawLine(x + width, y, x + width, y + height - 1);
    	setColor(c);
    }

    /**
     * Paints a highlighted 3-D rectangle using the current color.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param raised a boolean that states whether the rectangle is raised or not
     */
    public void fill3DRect(int x, int y, int width, int height, boolean raised) {
    	Color c = getColor();
    	Color brighter = brighter(c);
    	Color darker = darker(c);

    	if (!raised) {
    	    setColor(darker);
    	}
    	fillRect(x+1, y+1, width-2, height-2);
    	setColor(raised ? brighter : darker);
    	drawLine(x, y, x, y + height - 1);
    	drawLine(x + 1, y, x + width - 2, y);
    	setColor(raised ? darker : brighter);
    	drawLine(x + 1, y + height - 1, x + width - 1, y + height - 1);
    	drawLine(x + width - 1, y, x + width - 1, y + height - 2);
    	setColor(c);
    }

    /**
     * Draws a highlighted 3-D rectangle.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param raised a boolean that states whether the rectangle is raised or not
     * @param etched a boolean that states whether the rectangle is to be drawn
     * as etched or not.  In fact, if "raised" is true, this has an embossed effect.
     * This requires the thickness to be greater than 1 for the effect to work.
     * @param thickness the line thickness, drawn inside
     */
    public void draw3DRect(int x, int y, int width, int height,
                           boolean raised, boolean etched, int thickness) {
        draw3DRect(x,y,width,height,raised,etched,thickness,false);
    }

    /**
     * Draws a highlighted 3-D rectangle.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param raised a boolean that states whether the rectangle is raised or not
     * @param etched a boolean that states whether the rectangle is to be drawn
     * as etched or not.  In fact, if "raised" is true, this has an embossed effect.
     * This requires the thickness to be greater than 1 for the effect to work.
     * @param thickness the line thickness, drawn inside
     * @param closeShaved draws the button with thin topshadow
     */
    public void draw3DRect(int x, int y, int width, int height,
                           boolean raised, boolean etched, int thickness, boolean closeShaved) {
        Color c = getColor();
        Color brighter = brighter(c);
        Color darker = darker(c);
        for ( int idx = 0; idx < thickness; idx++ ) {
            Color top = brighter;
            Color bottom = darker;

            if ( ! raised ) {
                top = darker;
                bottom = brighter;
            }

            if ( etched ) {
                if ( idx >= ( thickness / 2 ) ) {
                    Color temp = top;
                    top = bottom;
                    bottom = temp;
                }
            }

            if ( raised && ( ! etched ) && ( ! closeShaved ) ) {
                if ( idx == 0 && thickness > 1 ) {
                    setColor(c);
                }
                else {
                    setColor(top);
                }
            }
            else {
                if ( ( idx == ( thickness - 1 ) ) &&
                     ( thickness > 1) &&
                     ( ! raised ) &&
                     ( ! etched ) ) {
                    setColor( darker(top) );
                }
                else {
                    setColor( top );
                }
            }

            if ( ! closeShaved || etched || idx < thickness-1 ) {
                drawLine( x + idx, y + idx,
                          x + idx,
                          y + ( ( height - 1 ) - ( idx ) ) );
                drawLine( x + idx, y + idx,
                          x + ( ( width - 1 ) - ( idx ) ),
                          y + idx );
            }

            if ( ( ! etched ) &&
                 ( ( idx == ( thickness - 1 ) ) &&
                   ( thickness > 1) &&
                   ( ! raised ) ) ||
                 ( ( idx == 0 ) &&
                   ( raised ) ) ) {
                setColor( darker(bottom) );
            }
            else {
                setColor( bottom );
            }

            if ( ! closeShaved || raised || etched || idx < thickness-1 ) {
                drawLine( x + idx,
                          y + ( ( height - 1 ) - ( idx ) ),
                          x + ( ( width - 1 ) - ( idx ) ),
                          y + ( ( height - 1 ) - ( idx ) ) );
                drawLine( x + ( ( width - 1 ) - ( idx ) ),
                          y + idx,
                          x + ( ( width - 1 ) - ( idx ) ),
                          y + ( ( height - 1 ) - ( idx ) ) );
            }
        }
    }

    /**
     * Draws an oval inside the specified rectangle using the current color.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @see #fillOval
     */
    public void drawOval(int x, int y, int width, int height) {
        awtGraphics.drawOval(x+xOffset,y+yOffset,width,height);
    }

    /**
     * Draws an oval inside the specified rectangle using the current color.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param thickness the line thickness, drawn inside
     * @see #fillOval
     */
    public void drawOval(int x, int y, int width, int height,int thickness) {
        for ( int idx = 0; idx < thickness; idx++ ) {
            int idx2 = idx*2;
            int tx = x + idx;
            int ty = y + idx;
            int tw = width - idx2;
            int th = height - idx2;

            drawOval( x + idx, y + idx, tw, th );
            if ( ( idx + 1 ) < thickness ) {
                drawOval( tx, ty, tw, th - 1 );
                drawOval( tx + 1, ty, tw, th - 1 );
                drawOval( tx, ty, tw - 1, th );
                drawOval( tx, ty + 1, tw - 1, th );
            }
        }
    }

    /**
     * Fills an oval inside the specified rectangle using the current color.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @see #drawOval
     */
    public void fillOval(int x, int y, int width, int height) {
        awtGraphics.fillOval(x+xOffset,y+yOffset,width,height);
    }

    /**
     * Draws an arc bounded by the specified rectangle starting at
     * startAngle, where 0 degrees is at the 3-o'clock position, and
     * extending for arcAngle degrees.  Positive values for arcAngle
     * indicate counter-clockwise rotations, negative values indicate
     * clockwise rotations.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param startAngle the beginning angle
     * @param arcAngle the angle of the arc (relative to startAngle).
     * @see #fillArc
     */
    public void drawArc(int x, int y, int width, int height,
				        int startAngle, int arcAngle) {
        awtGraphics.drawArc(x+xOffset,y+yOffset,width,height,startAngle,arcAngle);
    }

    /**
     * Draws an arc bounded by the specified rectangle starting at
     * startAngle, where 0 degrees is at the 3-o'clock position, and
     * extending for arcAngle degrees.  Positive values for arcAngle
     * indicate counter-clockwise rotations, negative values indicate
     * clockwise rotations.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param startAngle the beginning angle
     * @param arcAngle the angle of the arc (relative to startAngle).
     * @param thickness the line thickness, drawn inside
     * @see #fillArc
     */
    public void drawArc(int x, int y, int width, int height,
				        int startAngle, int arcAngle, int thickness) {
        for ( int idx = 0; idx < thickness; idx++ ) {
            int idx2 = idx*2;
            int tx = x + idx;
            int ty = y + idx;
            int tw = width - idx2;
            int th = height - idx2;

            drawArc( x + idx, y + idx, tw, th, startAngle, arcAngle );
            if ( ( idx + 1 ) < thickness ) {
                drawArc( tx, ty, tw, th - 1, startAngle, arcAngle );
                drawArc( tx + 1, ty, tw - 1, th - 1, startAngle, arcAngle );
                drawArc( tx, ty, tw - 1, th, startAngle, arcAngle );
                drawArc( tx, ty + 1, tw - 1, th - 1, startAngle, arcAngle );
            }
        }
    }

    /**
     * Fills an arc using the current color. This generates a pie shape.
     * The extent of the arc is the same as is described for the drawArc
     * method.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the arc
     * @param height the height of the arc
     * @param startAngle the beginning angle
     * @param arcAngle the angle of the arc (relative to startAngle).
     * @see #drawArc
     */
    public void fillArc(int x, int y, int width, int height,
				 int startAngle, int arcAngle) {
        awtGraphics.fillArc(x+xOffset,y+yOffset,width,height,startAngle,arcAngle);
    }

    /**
     * Draws a polygon defined by an array of x points and y points.
     * @param xPoints an array of x points
     * @param yPoints an array of y points
     * @param nPoints the total number of points
     * @see #fillPolygon
     */
    public void drawPolygon(int xPoints[], int yPoints[], int nPoints) {
        if ( xOffset != 0 ) {
            for ( int i = 0; i < nPoints; i++ ) {
                xPoints[i] += xOffset;
            }
        }
        if ( yOffset != 0 ) {
            for ( int i = 0; i < nPoints; i++ ) {
                yPoints[i] += yOffset;
            }
        }
        awtGraphics.drawPolygon(xPoints,yPoints,nPoints);
		//Draw final closing segment
        drawLine(xPoints[nPoints - 1], yPoints[nPoints - 1], xPoints[0], yPoints[0]);
        if ( xOffset != 0 ) {
            for ( int i = 0; i < nPoints; i++ ) {
                xPoints[i] -= xOffset;
            }
        }
        if ( yOffset != 0 ) {
            for ( int i = 0; i < nPoints; i++ ) {
                yPoints[i] -= yOffset;
            }
        }
    }

    /**
     * Draws a polygon defined by an array of x points and y points.
     * @param xPoints an array of x points
     * @param yPoints an array of y points
     * @param nPoints the total number of points
     * @param thickness the line thickness, drawn on either side of the line
     * @see #fillPolygon
     */
    public void drawPolygon(int xPoints[], int yPoints[], int nPoints, int thickness) {
        drawPoly(xPoints, yPoints, nPoints, thickness, 1, 0, CENTER, true);
    }

    /**
     * Draws a polygon defined by an array of x points and y points.
     * @param xPoints an array of x points
     * @param yPoints an array of y points
     * @param nPoints the total number of points
     * @param thickness the line thickness, drawn on either side of the line
     * @param dashOnLen the pixel length of the dash (must be greater than zero)
     * @param dashOffLen the pixel length of the space between dashes (set to zero if no dashes)
     * @see #fillPolygon
     */
    public void drawPolygon(int xPoints[], int yPoints[], int nPoints,
                            int thickness, int dashOnLen, int dashOffLen) {
        drawPoly(xPoints, yPoints, nPoints, thickness, dashOnLen, dashOffLen, CENTER, true);
    }

    /**
     * Draws a polygon defined by an array of x points and y points.
     * @param xPoints an array of x points
     * @param yPoints an array of y points
     * @param nPoints the total number of points
     * @param thickness the line thickness, drawn on either side of the line
     * @param dashOnLen the pixel length of the dash (must be greater than zero)
     * @param dashOffLen the pixel length of the space between dashes (set to zero if no dashes)
     * @param align CENTER thick lines over the line from point to point, or put it
     * to the RIGHT or to the LEFT.  IMPORTANT:  RIGHT does NOT always mean right.  If the line
     * goes up (y2 < y1), then RIGHT is right.  If the line goes down, RIGHT is left.  If the line
     * goes from left to right, RIGHT is down, etc.  It is really based on the direction of motion
     * from the first point to the next.
     * @see #fillPolygon
     */
    public void drawPolygon(int xPoints[], int yPoints[], int nPoints,
                            int thickness, int dashOnLen, int dashOffLen, int align) {
        drawPoly(xPoints, yPoints, nPoints, thickness, dashOnLen, dashOffLen, align, true);
    }

    /**
     * Draws a polygon defined by the specified point.
     * @param p the specified polygon
     * @see #fillPolygon
     */
    public void drawPolygon(Polygon p) {
        drawPolygon(p.xpoints,p.ypoints,p.npoints);
    }

    /**
     * Draws a polygon defined by the specified point.
     * @param p the specified polygon
     * @param thickness the line thickness, drawn on either side of the line
     * @see #fillPolygon
     */
    public void drawPolygon(Polygon p, int thickness) {
		drawPoly(p.xpoints, p.ypoints, p.npoints, thickness, 1, 0, CENTER, true);
    }

    /**
     * Draws a polygon defined by the specified point.
     * @param p the specified polygon
     * @param thickness the line thickness, drawn on either side of the line
     * @param dashOnLen the pixel length of the dash (must be greater than zero)
     * @param dashOffLen the pixel length of the space between dashes (set to zero if no dashes)
     * @see #fillPolygon
     */
    public void drawPolygon(Polygon p, int thickness, int dashOnLen, int dashOffLen) {
        drawPoly(p.xpoints, p.ypoints, p.npoints, thickness, dashOnLen, dashOffLen, CENTER, true);
    }

    /**
     * Draws a polygon defined by the specified point.
     * @param p the specified polygon
     * @param thickness the line thickness, drawn on either side of the line
     * @param dashOnLen the pixel length of the dash (must be greater than zero)
     * @param dashOffLen the pixel length of the space between dashes (set to zero if no dashes)
     * @see #fillPolygon
     */
    public void drawPolygon(Polygon p, int thickness, int dashOnLen, int dashOffLen,int align) {
        drawPoly(p.xpoints, p.ypoints, p.npoints, thickness, dashOnLen, dashOffLen, align, true);
    }

    /**
     * Fills a polygon with the current color using an
     * even-odd fill rule (otherwise known as an alternating rule).
     * @param xPoints an array of x points
     * @param yPoints an array of y points
     * @param nPoints the total number of points
     * @see #drawPolygon
     */
    public void fillPolygon(int xPoints[], int yPoints[], int nPoints) {
        if ( xOffset != 0 ) {
            for ( int i = 0; i < nPoints; i++ ) {
                xPoints[i] += xOffset;
            }
        }
        if ( yOffset != 0 ) {
            for ( int i = 0; i < nPoints; i++ ) {
                yPoints[i] += yOffset;
            }
        }
        awtGraphics.fillPolygon(xPoints,yPoints,nPoints);
        if ( xOffset != 0 ) {
            for ( int i = 0; i < nPoints; i++ ) {
                xPoints[i] -= xOffset;
            }
        }
        if ( yOffset != 0 ) {
            for ( int i = 0; i < nPoints; i++ ) {
                yPoints[i] -= yOffset;
            }
        }
    }

    /**
     * Fills the specified polygon with the current color using an
     * even-odd fill rule (otherwise known as an alternating rule).
     * @param p the polygon
     * @see #drawPolygon
     */
    public void fillPolygon(Polygon p) {
        fillPolygon(p.xpoints,p.ypoints,p.npoints);
    }

    /**
     * Draws the specified String using the current font and color.
     * The x,y position is the starting point of the baseline of the String.
     * @param str the String to be drawn
     * @param x the x coordinate
     * @param y the y coordinate
     * @see #drawChars
     * @see #drawBytes
     */
    public void drawString(String str, int x, int y) {
        Font font = getFont();
        if (font instanceof SpecialFont) {
            ((SpecialFont)font).drawString(this,str,x,y);
        } else {
            awtGraphics.drawString(str,x+xOffset,y+yOffset);
        }
    }

    /**
     * Draws the specified characters using the current font and color.
     * @param data the array of characters to be drawn
     * @param offset the start offset in the data
     * @param length the number of characters to be drawn
     * @param x the x coordinate
     * @param y the y coordinate
     * @see #drawString
     * @see #drawBytes
     */
    public void drawChars(char data[], int offset, int length, int x, int y) {
    	drawString(new String(data, offset, length), x, y);
    }

    /**
     * Draws the specified bytes using the current font and color.
     * @param data the data to be drawn
     * @param offset the start offset in the data
     * @param length the number of bytes that are drawn
     * @param x the x coordinate
     * @param y the y coordinate
     * @see #drawString
     * @see #drawChars
     */
    public void drawBytes(byte data[], int offset, int length, int x, int y) {
    	drawString(new String(data, 0, offset, length), x, y);
    }

    /**
     * Draws the specified image at the specified coordinate (x, y). If the image is
     * incomplete the image observer will be notified later.
     * @param img the specified image to be drawn
     * @param x the x coordinate
     * @param y the y coordinate
     * @param observer notifies if the image is complete or not
     * @see Image
     * @see ImageObserver
     * @return boolean
     */
    public boolean drawImage(Image img, int x, int y,
				      ImageObserver observer) {
	    if (img instanceof Sprite) {
            return ((Sprite)img).drawImage(this,x,y);
	    }
	    else {
            return awtGraphics.drawImage(img,x+xOffset,y+yOffset,observer);
        }
    }

    /**
     * Draws the specified image inside the specified rectangle. The image is
     * scaled if necessary. If the image is incomplete the image observer will be
     * notified later.
     * @param img the specified image to be drawn
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param observer notifies if the image is complete or not
     * @see Image
     * @see ImageObserver
     * @return boolean
     */
    public boolean drawImage(Image img, int x, int y,
				      int width, int height,
				      ImageObserver observer) {
	    if (img instanceof Sprite) {
            return ((Sprite)img).drawImage(this,x,y,width,height);
        }
        else {
            return awtGraphics.drawImage(img,x+xOffset,y+yOffset,width,height,observer);
        }
    }

    /**
     * Draws the specified image at the specified coordinate (x, y),
     * with the given solid background Color.  If the image is
     * incomplete the image observer will be notified later.
     * @param img the specified image to be drawn
     * @param x the x coordinate
     * @param y the y coordinate
     * @param observer notifies if the image is complete or not
     * @see Image
     * @see ImageObserver
     * @return booleam
     */
    public boolean drawImage(Image img, int x, int y,
				      Color bgcolor,
				      ImageObserver observer) {
        if (img instanceof Sprite) {
            return ((Sprite)img).drawImage(this,x,y,bgcolor);
        }
        else {
            return awtGraphics.drawImage(img,x+xOffset,y+yOffset,bgcolor,observer);
        }
    }

    /**
     * Draws the specified image inside the specified rectangle,
     * with the given solid background Color. The image is
     * scaled if necessary. If the image is incomplete the image
     * observer will be notified later.
     * @param img the specified image to be drawn
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param observer notifies if the image is complete or not
     * @see Image
     * @see ImageObserver
     * @return boolean
     */
    public boolean drawImage(Image img, int x, int y,
				      int width, int height,
				      Color bgcolor,
				      ImageObserver observer) {
	   if (img instanceof Sprite) {
            return ((Sprite)img).drawImage(this,x,y,width,height,bgcolor);
        }
        else {
            return awtGraphics.drawImage(img,x,y,width,height,bgcolor,observer);
        }
    }

    /**
     * drawPolyline
     * Draws a polyline defined by an array of x points and y points.
     * @param xpointsp[] the array of x coordinates
     * @param ypoints[]  the array of y coordinates
     * @param npoints    the number of points in the polyline
     */
    public void drawPolyline(int xpoints[], int ypoints[], int npoints) {
        //drawPolyline(xpoints,ypoints,npoints); // this is a 1.1 function
        drawPoly(xpoints, ypoints, npoints, false);
    }

    /**
     * drawPolyline
     * Draws a polyline defined by an array of x points and y points.
     * @param xpointsp[] the array of x coordinates
     * @param ypoints[]  the array of y coordinates
     * @param npoints    the number of points in the polyline
     * @param thickness the line thickness, drawn on either side of the line
     */
    public void drawPolyline(int xpoints[], int ypoints[], int npoints, int thickness) {
		drawPoly(xpoints, ypoints, npoints, thickness, 1, 0, CENTER, false );
	}

    /**
     * drawPolyline
     * Draws a polyline defined by an array of x points and y points.
     * @param xpointsp[] the array of x coordinates
     * @param ypoints[]  the array of y coordinates
     * @param npoints    the number of points in the polyline
     * @param thickness the line thickness, drawn on either side of the line
     * @param dashOnLen the pixel length of the dash (must be greater than zero)
     * @param dashOffLen the pixel length of the space between dashes (set to zero if no dashes)
     */
    public void drawPolyline(int xpoints[], int ypoints[], int npoints,
								int thickness, int dashOnLen, int dashOffLen) {
		drawPoly(xpoints, ypoints, npoints, thickness, dashOnLen, dashOffLen, CENTER, false );
	}

    /**
     * drawPolyline
     * Draws a polyline defined by an array of x points and y points.
     * @param xpointsp[] the array of x coordinates
     * @param ypoints[]  the array of y coordinates
     * @param npoints    the number of points in the polyline
     * @param thickness the line thickness, drawn on either side of the line
     * @param dashOnLen the pixel length of the dash (must be greater than zero)
     * @param dashOffLen the pixel length of the space between dashes (set to zero if no dashes)
     * @param align CENTER thick lines over the line from point to point, or put it
     * to the RIGHT or to the LEFT.  IMPORTANT:  RIGHT does NOT always mean right.  If the line
     * goes up (y2 < y1), then RIGHT is right.  If the line goes down, RIGHT is left.  If the line
     * goes from left to right, RIGHT is down, etc.  It is really based on the direction of motion
     * from the first point to the next.
     */
    public void drawPolyline(int xpoints[], int ypoints[], int npoints,
								int thickness, int dashOnLen, int dashOffLen, int align) {
		drawPoly(xpoints, ypoints, npoints, thickness, dashOnLen, dashOffLen, align, false );
	}

    /**
     * drawPolyline
     * Draws a polyline defined by an array of x points and y points.
     * @param p    the polygon of points to draw.
     */
    public void drawPolyline(Polygon p) {
		drawPoly(p.xpoints, p.ypoints, p.npoints, false);
	}

    /**
     * drawPolyline
     * Draws a polyline defined by an array of x points and y points.
     * @param p    the polygon of points to draw.
     * @param thickness the line thickness, drawn on either side of the line
     */
    public void drawPolyline(Polygon p, int thickness) {
		drawPoly(p.xpoints, p.ypoints, p.npoints, thickness, 1, 0, CENTER, false );
	}

    /**
     * drawPolyline
     * Draws a polyline defined by an array of x points and y points.
     * @param p    the polygon of points to draw.
     * @param thickness the line thickness, drawn on either side of the line
     * @param dashOnLen the pixel length of the dash (must be greater than zero)
     * @param dashOffLen the pixel length of the space between dashes (set to zero if no dashes)
     */
    public void drawPolyline(Polygon p, int thickness, int dashOnLen, int dashOffLen) {
		drawPoly(p.xpoints, p.ypoints, p.npoints, thickness, dashOnLen, dashOffLen, CENTER, false );
	}

    /**
     * drawPolyline
     * Draws a polyline defined by an array of x points and y points.
     * @param p    the polygon of points to draw.
     * @param thickness the line thickness, drawn on either side of the line
     * @param dashOnLen the pixel length of the dash (must be greater than zero)
     * @param dashOffLen the pixel length of the space between dashes (set to zero if no dashes)
     * @param align CENTER thick lines over the line from point to point, or put it
     * to the RIGHT or to the LEFT.  IMPORTANT:  RIGHT does NOT always mean right.  If the line
     * goes up (y2 < y1), then RIGHT is right.  If the line goes down, RIGHT is left.  If the line
     * goes from left to right, RIGHT is down, etc.  It is really based on the direction of motion
     * from the first point to the next.
     */
    public void drawPolyline(Polygon p, int thickness, int dashOnLen, int dashOffLen, int align) {
		drawPoly(p.xpoints, p.ypoints, p.npoints, thickness, dashOnLen, dashOffLen, align, false );
	}

    /**
     * drawPoly
     * Draws a polyline defined by an array of x points and y points.
     * @param xpointsp[] the array of x coordinates
     * @param ypoints[]  the array of y coordinates
     * @param npoints    the number of points in the polyline
	 * @param CLOSED	 if true, the polyline closes itself, with the end
	 * point being the starting point.  If false, the polygon is not closed.
     */
    public void drawPoly(int xpoints[], int ypoints[], int npoints, boolean CLOSED) {
        //drawPoly(xpoints,ypoints,npoints); // this is a 1.1 function
        for (int i = 1; i < npoints; i++) {
            drawLine(xpoints[i-1],ypoints[i-1],xpoints[i],ypoints[i]);
        }
		if (CLOSED)
            drawLine(xpoints[npoints - 1],ypoints[npoints - 1],xpoints[0],ypoints[0]);
    }

    /**
     * drawPoly
     * Draws a polyline defined by an array of x points and y points.
     * @param xpointsp[] the array of x coordinates
     * @param ypoints[]  the array of y coordinates
     * @param npoints    the number of points in the polyline
     * @param thickness the line thickness, drawn on either side of the line
	 * @param CLOSED	 if true, the polyline closes itself, with the end
	 * point being the starting point.  If false, the polygon is not closed.
     */
    public void drawPoly(int xpoints[], int ypoints[], int npoints,
						 int thickness, boolean CLOSED) {
		drawPoly(xpoints, ypoints, npoints, thickness, 1, 0, CENTER, CLOSED );
	}

    /**
     * drawPoly
     * Draws a polyline defined by an array of x points and y points.
     * @param xpointsp[] the array of x coordinates
     * @param ypoints[]  the array of y coordinates
     * @param npoints    the number of points in the polyline
     * @param thickness the line thickness, drawn on either side of the line
     * @param dashOnLen the pixel length of the dash (must be greater than zero)
     * @param dashOffLen the pixel length of the space between dashes (set to zero if no dashes)
	 * @param CLOSED	 if true, the polyline closes itself, with the end
	 * point being the starting point.  If false, the polygon is not closed.
     */
    public void drawPoly(int xpoints[], int ypoints[], int npoints,
						 int thickness, int dashOnLen, int dashOffLen, boolean CLOSED) {
		drawPoly(xpoints, ypoints, npoints, thickness, dashOnLen, dashOffLen, CENTER, CLOSED );
	}

    /**
     * drawPoly
     * Draws a polyline defined by an array of x points and y points.
     * @param xpointsp[] the array of x coordinates
     * @param ypoints[]  the array of y coordinates
     * @param npoints    the number of points in the polyline
     * @param thickness the line thickness, drawn on either side of the line
     * @param dashOnLen the pixel length of the dash (must be greater than zero)
     * @param dashOffLen the pixel length of the space between dashes (set to zero if no dashes)
     * @param align CENTER thick lines over the line from point to point, or put it
     * to the RIGHT or to the LEFT.  IMPORTANT:  RIGHT does NOT always mean right.  If the line
     * goes up (y2 < y1), then RIGHT is right.  If the line goes down, RIGHT is left.  If the line
     * goes from left to right, RIGHT is down, etc.  It is really based on the direction of motion
     * from the first point to the next.
	 * @param CLOSED	 if true, the polyline closes itself, with the end
	 * point being the starting point.  If false, the polygon is not closed.
     */
    public void drawPoly(int xpoints[], int ypoints[], int npoints, int thickness,
						 int dashOnLen, int dashOffLen, int align, boolean CLOSED) {
        int dashStart = 0;
        for ( int i = 1; i < npoints; i++ ) {
            dashStart = drawLine(xpoints[i-1], ypoints[i-1], xpoints[i], ypoints[i],
                                 thickness, dashOnLen, dashOffLen, align, dashStart);
        }
		if (CLOSED)
            drawLine(xpoints[npoints - 1], ypoints[npoints - 1], xpoints[0], ypoints[0],
                     thickness, dashOnLen, dashOffLen, align, dashStart);
	}

    /**
     * drawPoly
     * Draws a polyline defined by an array of x points and y points.
     * @param p    the polygon of points to draw.
	 * @param CLOSED	 if true, the polyline closes itself, with the end
	 * point being the starting point.
     */
    public void drawPoly(Polygon p, boolean CLOSED) {
		drawPoly(p.xpoints, p.ypoints, p.npoints, CLOSED);
	}

    /**
     * drawPoly
     * Draws a polyline defined by an array of x points and y points.
     * @param p    the polygon of points to draw.
     * @param thickness the line thickness, drawn on either side of the line
	 * @param CLOSED	 if true, the polyline closes itself, with the end
	 * point being the starting point.  If false, the polygon is not closed.
     */
    public void drawPoly(Polygon p, int thickness, boolean CLOSED) {
		drawPoly(p.xpoints, p.ypoints, p.npoints, thickness, 1, 0, CENTER, CLOSED );
	}

    /**
     * drawPoly
     * Draws a polyline defined by an array of x points and y points.
     * @param p    the polygon of points to draw.
     * @param thickness the line thickness, drawn on either side of the line
     * @param dashOnLen the pixel length of the dash (must be greater than zero)
     * @param dashOffLen the pixel length of the space between dashes (set to zero if no dashes)
	 * @param CLOSED	 if true, the polyline closes itself, with the end
	 * point being the starting point.  If false, the polygon is not closed.
     */
    public void drawPoly(Polygon p, int thickness, int dashOnLen, int dashOffLen, boolean CLOSED) {
		drawPoly(p.xpoints, p.ypoints, p.npoints, thickness, dashOnLen, dashOffLen, CENTER, CLOSED );
	}

    /**
     * drawPoly
     * Draws a polyline defined by an array of x points and y points.
     * @param p    the polygon of points to draw.
     * @param thickness the line thickness, drawn on either side of the line
     * @param dashOnLen the pixel length of the dash (must be greater than zero)
     * @param dashOffLen the pixel length of the space between dashes (set to zero if no dashes)
     * @param align CENTER thick lines over the line from point to point, or put it
     * to the RIGHT or to the LEFT.  IMPORTANT:  RIGHT does NOT always mean right.  If the line
     * goes up (y2 < y1), then RIGHT is right.  If the line goes down, RIGHT is left.  If the line
     * goes from left to right, RIGHT is down, etc.  It is really based on the direction of motion
     * from the first point to the next.
	 * @param CLOSED	 if true, the polyline closes itself, with the end
	 * point being the starting point.  If false, the polygon is not closed.
     */
    public void drawPoly(Polygon p, int thickness, int dashOnLen,
						 int dashOffLen, int align, boolean CLOSED) {
		drawPoly(p.xpoints, p.ypoints, p.npoints, thickness, dashOnLen, dashOffLen, align, CLOSED );
	}

    /**
     * drawImage
     * @param img Not implemented until fully JDK 1.1
     * @param dx1 Not implemented until fully JDK 1.1
     * @param dy1 Not implemented until fully JDK 1.1
     * @param dx2 Not implemented until fully JDK 1.1
     * @param dy2 Not implemented until fully JDK 1.1
     * @param sx1 Not implemented until fully JDK 1.1
     * @param sy1 Not implemented until fully JDK 1.1
     * @param sx2 Not implemented until fully JDK 1.1
     * @param sy2 Not implemented until fully JDK 1.1
     * @param observer Not implemented until fully JDK 1.1
     * @return boolean
     */
    public boolean drawImage(Image img,
				      int dx1, int dy1, int dx2, int dy2,
				      int sx1, int sy1, int sx2, int sy2,
				      ImageObserver observer) {
        throw new IllegalArgumentException( "Not implemented until fully JDK 1.1");
    }

    /**
     * drawImage
     * @param img Not implemented until fully JDK 1.1
     * @param dx1 Not implemented until fully JDK 1.1
     * @param dy1 Not implemented until fully JDK 1.1
     * @param dx2 Not implemented until fully JDK 1.1
     * @param dy2 Not implemented until fully JDK 1.1
     * @param sx1 Not implemented until fully JDK 1.1
     * @param sy1 Not implemented until fully JDK 1.1
     * @param sx2 Not implemented until fully JDK 1.1
     * @param sy2 Not implemented until fully JDK 1.1
     * @param bgcolor Not implemented until fully JDK 1.1
     * @param observer Not implemented until fully JDK 1.1
     * @return boolean
     */
    public boolean drawImage(Image img,
				      int dx1, int dy1, int dx2, int dy2,
				      int sx1, int sy1, int sx2, int sy2,
				      Color bgcolor,
				      ImageObserver observer) {
        throw new IllegalArgumentException("Not implemented until fully JDK 1.1");
    }
/**
 * getClip
 * @return Shape
 */
/*
    public Shape getClip() {
        return getClipRect();
    }

    /**
     * setClip
     * @param shape

    public void setClip(Shape shape) {
        Rectangle rect = (Rectangle)shape;
        clipRect(rect.x,rect.y,rect.width,rect.height);
    }
*/
    /**
     * setClip
     * @param x	x coordinate of upper left corner
     * @param y	y coordinate of upper left corner
     * @param width width of clipping rectangle
     * @param height height of clipping rectangle
     */
    public void setClip(int x, int y, int width, int height) {
        clipRect(x,y,width,height);
    }

    /**
     * getClipBounds
     * @return Rectangle
     */
    public Rectangle getClipBounds() {
        return getClipRect();
    }
    /**
     * Disposes of this graphics context.  The GadgetGraphics context cannot be used after
     * being disposed of.
     * @see #finalize
     */
/*
private Exception disposeEvent;
*/
    public void dispose() {
        if (!disposed) {
            awtGraphics.dispose();
            disposed = true;
/*
disposeEvent = new Exception();
synchronized(actives) {
numactive--;
actives.removeElement(active);
if (numactive < 0){
    System.err.println("numactive "+numactive);
}
localnumactive--;
if (localnumactive<0) {
    System.err.println("localnumactive "+localnumactive);
}
}
*/
        }
    }

    /**
     * Disposes of this graphics context once it is no longer referenced.
     * @see #dispose
     */
    public void finalize() {
    	dispose();
    }

    /**
     * Returns a String object representing this Graphic's value.
     * @return String
     */
    public String toString() {
    	return getClass().getName()+"[font="+getFont()+",color="+getColor()+
    	                            ",xOffset="+xOffset+",yOffset="+yOffset+" clip="+getClipRect()+"]";
    }


    /**
     * Returns a brighter version of this color.
     * @return Color
     */
    public static final Color brighter( Color color ) {
        return brighter(color,BRIGHTER_FACTOR);
    }

    /**
     * Returns a brighter version of this color, given the factor.
     * @param color	the color to brighten
     * @param factor the factor to increase brightness
     * @return Color
     */
    public static Color brighter( Color color, double factor ) {
	    Color newcolor;
	    if ( color.equals(Color.black) ) {
	        newcolor = Color.lightGray;
	    }
	    else if ( color.equals(Color.white) ) {
	        newcolor = Color.lightGray;
	    }
	    else {
    	    newcolor = new Color(Math.min((int)(color.getRed()  *(1/factor)), 255),
    			 Math.min((int)(color.getGreen()*(1/factor)), 255),
    			 Math.min((int)(color.getBlue() *(1/factor)), 255));
		}
	    if ( newcolor.equals( color ) ) {
	        return Color.white;
	    }
	    return newcolor;
    }

    /**
     * Returns a darker version of this color.
     * @param color the color to darken
     * @return Color
     */
    public static final Color darker( Color color ) {
        return darker(color,DARKER_FACTOR);
    }

    /**
     * Returns a darker version of this color, given the factor.
     * @param color the color to darken
     * @param factor the factor to decrease brightness
     * @return Color
     */
    public static Color darker( Color color, double factor ) {
	    Color newcolor;
	    if ( color.equals(Color.black) ) {
	        newcolor = Color.gray;
	    }
	    else {
    	    newcolor = new Color(Math.max((int)(color.getRed()*factor), 0),
    			 Math.max((int)(color.getGreen()*factor), 0),
    			 Math.max((int)(color.getBlue() *factor), 0));
    	}
	    if ( newcolor.equals( color ) ) {
	        return Color.black;
	    }
	    return newcolor;
    }
}
