/****************************************************************
 **
 **  $Id: LabelGadget.java,v 1.46 1998/02/11 22:40:35 ccp Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/LabelGadget.java,v $
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
import java.util.StringTokenizer;

/**
 * LabelGadget
 * @version 1.1
 * @author DTAI, Incorporated
 */
public class LabelGadget extends DisplayGadget {

    private String label;
    private String wrappedLabel;
    private int wrapLength = 0;
    private boolean possiblyDisabled = false;

/**
 * Constructs a Label with no label.
 */
	public LabelGadget() {
		this( "", HORIZ_ALIGN_CENTER );
	}

/**
 * Constructs a Label with the specified label.
 * @param label - the label of the button
 */
	public LabelGadget(String label) {
		this( label, HORIZ_ALIGN_CENTER );
	}

/**
 * Constructs a LabelGadget with the specified label and horizontal alignment
 * @param label - the label of the button
 * @param horizAlign - horizontal alignment
 */
	public LabelGadget(String label, int horizAlign) {
	    setClippingRequired(false);
		setLabel( label );
		setHorizAlign( horizAlign );
	}

/**
 * Gets the label of the button.
 * @return label
 * @see setLabel
 */
	public String getLabel() {
		return label;
	}

/**
 * Sets the button with the specified label.
 * @param label - the label to set the button with
 * @see getLabel
 */
	public void setLabel( String label ) {
	    synchronized(getTreeLock()) {
    	    if ( ( ( this.label == null ) &&
    	           ( label != null ) ) ||
    	         ( ( this.label != null ) &&
    	           ( ! this.label.equals( label ) ) ) ) {
        		this.label = label;
        		computeWrappedLabel();
        		invalidate();
        	}
        }
	}

	/**
	 * toString
     * @return String
	 */
	public String toString() {
	    if ( label == null ) {
	        return "Blank Label";
	    }
	    return getLabel();
	}

/**
 * isPossiblyDisabled
 * @return boolean
 */
	public boolean isPossiblyDisabled() {
		return possiblyDisabled;
	}

/**
 * setPossiblyDisabled
 * @param possiblyDisabled new value for possiblyDisabled
 */
	public void setPossiblyDisabled( boolean possiblyDisabled ) {
	    synchronized(getTreeLock()) {
    	    if ( this.possiblyDisabled != possiblyDisabled ) {
        		this.possiblyDisabled = possiblyDisabled;
        		invalidate();
        	}
        }
	}

/**
 * Gets the wrap lenth of the button.
 * @return wrapLength
 * @see setWrapLength
 */
	public int getWrapLength() {
		return wrapLength;
	}

/**
 * Sets the button with the specified wrap lenth.
 * @param wrapLength	new value for wrapLength
 * @see getWrapLength
 */
	public void setWrapLength( int wrapLength ) {
		synchronized(getTreeLock()) {
    	    if ( this.wrapLength != wrapLength ) {
        		this.wrapLength = wrapLength;
        		computeWrappedLabel();
        		invalidate();
        	}
        }
	}

	/**
	 * compueWrappedLabel
	 */
	public void computeWrappedLabel() {
		synchronized(getTreeLock()) {
    	    String wrappedLabel = label;
    	    if ((label != null) && (wrapLength > 0)) {

                int col;

                StringBuffer buf = new StringBuffer();

                int oldidx = 0;

                int lastword = 0;
                boolean newline_inserted = true;
                boolean inspace = false;

                while ( oldidx < label.length() ) {
                    oldidx = lastword;

                    for ( col = 1;
                          ( ( oldidx ) < label.length() ) && ( col <= wrapLength+1 );
            	          col++ ) {

                        if ( Character.isSpace( label.charAt( oldidx ) ) ) {
                    	    if ( ! inspace ) {
                                if ( ! newline_inserted ) {
                                    buf.append( '\n' );
                                    newline_inserted = true;
                                }
                                while ( lastword < oldidx ) {
                                    buf.append( label.charAt( lastword ) );
                                    lastword++;
                                }
                                inspace = true;
                        	}

                        	if ( label.charAt( oldidx ) == '\n' ) {
                                buf.append( '\n' );
                                lastword++;
                                inspace = false;
                                col = 0;
                        	}
                        }
                        else {
                            inspace = false;
                        }

                        oldidx++;
                    }

                    if ( oldidx < label.length() ) {
                        while ( ( lastword < label.length() ) &&
                                ( Character.isSpace( label.charAt( lastword ) ) ) ) {
                        	lastword++;
                        }

                        if ( ( oldidx - lastword ) >= wrapLength ) {

                            // break in a long word
                            /////////////////////////

                            if ( ! newline_inserted ) {
                                buf.append( '\n' );
                                newline_inserted = true;
                            }

                            while ( lastword < oldidx ) {
                                buf.append( label.charAt( lastword ) );
                                lastword++;
                            }

                            while ( Character.isSpace( label.charAt( lastword ) ) ) {
                                lastword++;
                            }
                        }

                        inspace = false;
                        newline_inserted = false;
                    }
                }

                if ( lastword < oldidx ) {

                    if ( ! newline_inserted ) {
                        buf.append( '\n' );
                        newline_inserted = true;
                    }

                    while ( lastword < oldidx ) {
                        buf.append( label.charAt( lastword ) );
                        lastword++;
                    }
                }

                wrappedLabel = buf.toString();
            }

    	    if ( ( ( this.wrappedLabel == null ) &&
    	           ( wrappedLabel != null ) ) ||
    	         ( ( this.wrappedLabel != null ) &&
    	           ( ! this.wrappedLabel.equals( wrappedLabel ) ) ) ) {
        		this.wrappedLabel = wrappedLabel;
        		invalidate();
        		repaint();
        	}
        }
	}

    /**
     * drawLines
     * @param g		GadgetGraphics object for drawing
     * @param x		x coordinate
     * @param y		y coordinate
     * @param font	font for lines
     */
    private void drawLines( GadgetGraphics g, int x, int y, Font font ) {
		FontMetrics metrics = g.getFontMetrics();
        int lineX = x;
        int lineY = y;
        int height = metrics.getHeight();
        int ascent = metrics.getAscent();
        int descent = metrics.getDescent();
        int leading = metrics.getLeading();
        boolean newline = false;
        StringTokenizer st = new StringTokenizer( wrappedLabel, "\n", true );
        while ( st.hasMoreTokens() ) {
            String line = st.nextToken();
            if (line.equals("\n")) {
                if (!newline) {
                    newline = true;
                    continue;
                }
                line = "";
            } else {
                newline = false;
            }
            int stringWidth = metrics.stringWidth( line );
            int tempFgWidth = fgWidth;
            int realFgWidth = width;
            if ( possiblyDisabled ) {
                tempFgWidth--;
                realFgWidth--;
            }
            if (metrics instanceof SpecialFontMetrics) {
                ascent = ((SpecialFontMetrics)metrics).stringAscent(line);
                height = ascent + descent + leading;
            }
            if ( stringWidth <= realFgWidth ) {
                if ( getHorizAlign() == HORIZ_ALIGN_RIGHT ) {
                    lineX = x + ( tempFgWidth - stringWidth );
                }
                else if ( getHorizAlign() == HORIZ_ALIGN_CENTER ) {
                    lineX = x + ( ( tempFgWidth / 2 ) - ( stringWidth / 2 ) );
					if (tempFgWidth % 2 == 0 && lineX > 0)  {
						lineX--;
					}
                }
                g.drawString( line, lineX, lineY+ascent-1 );
            }
            else {
                int dotsWidth = metrics.stringWidth("...");
                int dotsX = realFgWidth - dotsWidth;
                g.drawString( "...", dotsX, lineY+ascent-1 );
                GadgetGraphics clipG = g.create();
                clipG.clipRect(0, lineY,
                           ( realFgWidth - dotsWidth ), height);
                clipG.drawString( line, 0, lineY+ascent-1 );
                clipG.dispose();
            }
            lineY += height;
        }
    }

    /**
     * paints the foreground
     * @param g		GadgetGraphics object for drawing
     * @param x		x coordinate
     * @param y		y coordinate
     * @param width	width
     * @param height height
     */
    protected void paintForeground(
            GadgetGraphics g, int x, int y, int width, int height ) {
		if ( wrappedLabel.length() > 0 ) {
			drawLines( g, x, y, getFont(g) );
		}
	}

    /**
     * paintDisabledForeground
     * @param g		GadgetGraphics object for drawing
     * @param x		x coordinate
     * @param y		y coordinate
     * @param width	width
     * @param height height
     */
    protected void paintDisabledForeground(
            GadgetGraphics g, int x, int y, int width, int height) {
        if ( ! possiblyDisabled ) {
            paintForeground( g, x, y, width, height );
            return;
        }

		if ( wrappedLabel.length() > 0 ) {

    		Color fg = getForeground(g);
    		Color bg = getFinalBackground(g);
    		Font font = getFont(g);

			g.setColor( getDisabledBottom(fg,bg) );
			drawLines( g, x + 1, y + 1, font );
			g.setColor( getDisabledTop(fg,bg) );
			drawLines( g, x, y, font );
		}
	}

	/**
	 * resets the foreground size
     * @param g		GadgetGraphics object for drawing
	 */
	protected void resetForegroundSize( GadgetGraphics g ) {
		if ( (pref_width == -1 || pref_height == -1) ) { // need to recalculate
            FontMetrics metrics = getFontMetrics( getFont(g) );
            int widestLine = 0;
            int numLines = 0;
            boolean newline = false;
            StringTokenizer st = new StringTokenizer( wrappedLabel, "\n", true );
            int totalHeight = 0;
            int leading = metrics.getLeading();
            int descent = metrics.getDescent();
            while ( st.hasMoreTokens() ) {
                String line = st.nextToken();
                if (line.equals("\n")) {
                    if (!newline) {
                        newline = true;
                        continue;
                    }
                    line = "";
                } else {
                    newline = false;
                }
                widestLine = Math.max( widestLine, metrics.stringWidth( line ) );
                if (metrics instanceof SpecialFontMetrics) {
                    if (totalHeight > 0) {
                        totalHeight += leading;
                    }
                    totalHeight += ((SpecialFontMetrics)metrics).stringAscent(line);
                    totalHeight += descent;
                }
                numLines++;
            }
            if (numLines == 0) {
                numLines = 1;
            }
            if (totalHeight > 0) {
                fgHeight = totalHeight;
            } else {
        		fgHeight = ( ( metrics.getAscent() + descent ) * numLines ) +
        		           ( leading * (numLines - 1) );
        	}
    		fgWidth = widestLine + 1;

    		if ( possiblyDisabled ) {
    		    fgHeight++;
    		    fgWidth++;
    		}
        }
	}
}
