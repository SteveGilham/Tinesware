/****************************************************************
 **
 **  $Id: ScrollbarGadget.java,v 1.43 1998/01/19 20:32:32 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/ScrollbarGadget.java,v $
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
import java.awt.Event;
import java.util.Vector;
import java11.awt.Adjustable;
import java11.awt.event.AdjustmentEvent;
import java11.awt.event.AdjustmentListener;
import java11.awt.event.AWTEvent;
import java11.awt.event.MouseEvent;
import java11.awt.event.MouseListener;
import java11.awt.event.MouseMotionListener;
import java11.util.EventObject;

/**
 * ScrollbarGadget
 * @version 1.1
 * @author DTAI, Incorporated
 */
public class ScrollbarGadget extends PanelGadget
    implements java11.awt.Adjustable, MouseListener, MouseMotionListener, Runnable {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private static final long SCROLL_TIME_DELTA = 25;

    AdjustmentListener adjustmentListener;

    private ArrowButtonGadget decrArrow;
    private ArrowButtonGadget incrArrow;
    private ScrollBubbleGadget bubble;
    private boolean autoScrolling = true;

    /**
     * The value of the Scrollbar.
     */
    private int	value;

    /**
     * The maximum value of the Scrollbar.
     */
    private int	maximum;

    /**
     * The minimum value of the Scrollbar.
     */
    private int	minimum;

    /**
     * The size of the visible portion of the Scrollbar.
     */
    private int	visibleAmount;

    /**
     * The Scrollbar's orientation--being either horizontal or vertical.
     */
    private int	orientation = -1;

    /**
     * The amount by which the scrollbar value will change when going
     * up or down by a line.
     */
    private int unitIncrement = 1;

    /**
     * The amount by which the scrollbar value will change when going
     * up or down by a block.
     */
    private int blockIncrement = 10;

    private int anchorMousePos = 0;
    private int anchorBubblePos = 0;
    private int gutterLength = 0;
    private int bubbleLength = 0;
    private Color gutterColor = null;
    private boolean activeUpdate = true;

    private Thread thread;
    private int curDelta;
    private Event curEvt;
    private int curEventType;
    private long curTime;
    private long prevTime;
    private int threadValue;

/**
 *   Constructs a Scrollbar with no label.
 */

	public ScrollbarGadget() {
		this( HORIZONTAL );
	}

	/**
	 * ScrollbarGadget
	 * @param orientation	description
	 */
	public ScrollbarGadget( int orientation ) {
	    this( orientation, 0, 100, 0, 0 );
	}

    /**
     * ScrollbarGadget
     * @param orientation	description
     * @param value	description
     * @param visibleAmount	description
     * @param minumum	description
     * @param maximum	description
     */
    public ScrollbarGadget( int orientation,
                            int value,
                            int visibleAmount,
                            int minimum,
                            int maximum) {
        if ( ( orientation != VERTICAL ) &&
             ( orientation != HORIZONTAL ) ) {
            throw new IllegalArgumentException();
        }
        add( bubble = new ScrollBubbleGadget() );
        bubble.addMouseListener( this );
        bubble.addMouseMotionListener( this );
		setValues( value, visibleAmount, minimum, maximum );
	    setLayout( new GadgetBorderLayout() );
		setOrientation( orientation );
        setConsumingTransparentClicks(true);
    }

	/**
	 * run
	 */
	public void run() {
        Thread thisThread = Thread.currentThread();

	    delay();
	    prevTime = System.currentTimeMillis();
	    while ( thread == thisThread ) {
	        scroll(thisThread,activeUpdate);
    	}
	}

	/**
	 * delay
	 */
	public void delay() {
	    try {
	        thread.sleep( 500 );
	    }
	    catch ( InterruptedException ie ) {
	    }
	}

    /**
     * Sets the scrollbar's active update flag
     * @param activeUpdate if true (usually the default) the scrollbar will not
     * update anything while dragging.
     */
    public void setActiveUpdate(boolean activeUpdate) {
        this.activeUpdate = activeUpdate;
    }

	/**
     * scroll
     * @param thisThread	description
     */
	public void scroll(Thread thisThread, boolean update) {
	    curTime = System.currentTimeMillis();
	    if ( ( curTime - prevTime ) > SCROLL_TIME_DELTA ) {
	        long numHops = ( curTime - prevTime ) / SCROLL_TIME_DELTA;
// Let's  allow the scrollbar to jump if there is a long delay...
            threadValue = goodValue((int)( threadValue + ( curDelta * numHops ) ));
            if (update) {
                setValue( threadValue, curEvt, curEventType );
            } else {
                adjustBubble(threadValue);
            }

            if (thread == thisThread) {
                prevTime += numHops * SCROLL_TIME_DELTA;
            }
        }
        if (thread == thisThread) {
            long sleepTime = SCROLL_TIME_DELTA - ( curTime - prevTime );
    	    try {
    	        thread.sleep( sleepTime );
    	    }
    	    catch ( InterruptedException ie ) {
    	    }
    	}
	}

    /**
     * startAdjust
     * @param delta	description
     * @param evt	description
     * @param eventType	description
     */
    public void startAdjust( int delta, Event evt, int eventType ) {
		synchronized(getTreeLock()) {
            stopAdjust();
            curDelta = delta;
            curEvt = evt;
            curEventType = eventType;
            threadValue = goodValue(value+curDelta);
            if (activeUpdate) {
                setValue( threadValue, curEvt, curEventType );
            } else {
                adjustBubble(threadValue);
            }
            if (autoScrolling) {
                thread = new Thread(this, "dtai.gwt.ScrollbarGadget");
                thread.start();
            }
        }
    }

    /**
     * stopAdjust
     */
    public void stopAdjust() {
		synchronized(getTreeLock()) {
            if ( thread != null ) {
                thread = null;
            }
        	if (!activeUpdate && threadValue >= 0) {
                setValue( threadValue, curEvt, curEventType );
                threadValue = -1;
            }
        }
    }

/**
 * True if holding down mouse causes scrollbar to scroll automatically
 * @see   setAutoScrolling
 * @return boolean
 */

	public final boolean isAutoScrolling() {
		return autoScrolling;
	}

/**
 * True if holding down mouse causes scrollbar to scroll automatically
 * @param autoScrolling - the autoScrolling to set the button with
 */

	public void setAutoScrolling( boolean autoScrolling ) {
	    this.autoScrolling = autoScrolling;
    }

/**
 * Gets the orientation of the button.
 * @see   setOrientation
 * @return int
 */

	public final int getOrientation() {
		return orientation;
	}

/**
 * Sets the button with the specified orientation.
 * @param orientation - the orientation to set the button with
 * @see getLabel
 */

	public void setOrientation( int orientation ) {
		synchronized(getTreeLock()) {
    	    if ( orientation == this.orientation ) {
    	        return;
    	    }
    	    this.orientation = orientation;
    	    if ( ( decrArrow == null ) ||
    	         ( incrArrow == null ) ) {
                decrArrow = new ArrowButtonGadget();
                decrArrow.addMouseListener( this );
                decrArrow.setFocusAllowed( false );
                incrArrow = new ArrowButtonGadget();
                incrArrow.addMouseListener( this );
                incrArrow.setFocusAllowed( false );
            }
            else {
                remove( decrArrow );
                remove( incrArrow );
            }
    	    if ( orientation == VERTICAL ) {
        	    decrArrow.setDirection( ArrowButtonGadget.UP );
        	    incrArrow.setDirection( ArrowButtonGadget.DOWN );
        	    add( "North", decrArrow );
        	    add( "South", incrArrow );
        	}
        	else {
        	    decrArrow.setDirection( ArrowButtonGadget.LEFT );
        	    incrArrow.setDirection( ArrowButtonGadget.RIGHT );
        	    add( "West", decrArrow );
        	    add( "East", incrArrow );
        	}
            adjustBubble(value);
        }
	}

    /**
     * Returns the current value of this Scrollbar.
     * @see #getMinimum
     * @see #getMaximum
     * @return int
     */
    public final int getValue() {
    	return value;
    }

    /**
     * Sets the value of this Scrollbar to the specified value.
     * @param value the new value of the Scrollbar. If this value is
     * below the current minimum or above the current maximum, it becomes the
     * new one of those values, respectively.
     * @see #getValue
     */
    public void setValue( int value ) {
		synchronized(getTreeLock()) {
            setValue( value, null, AdjustmentEvent.TRACK );
        }
    }

    private int goodValue(int value) {
		synchronized(getTreeLock()) {
        	if ( value < minimum ) {
        	    value = minimum;
        	}
        	if ( value > maximum ) {
        	    value = maximum;
        	}
        	return value;
        }
    }

    /**
     * setValue
     * @param value	description
     * @param evt	description
     * @param eventType	description
     */
    public void setValue( int value, Event evt, int eventType ) {
		synchronized(getTreeLock()) {
		    value = goodValue(value);
        	if ( value != this.value ) {
        	    this.value = value;
                adjustBubble(value);
        		processEvent( new AdjustmentEvent( evt,
    	                               eventType, this, value ) );
        	}
        }
    }

    /**
     * Returns the minimum value of this Scrollbar.
     * @see #getMaximum
     * @see #getValue
     * @return int
     */
    public final int getMinimum() {
    	return minimum;
    }

    /**
     * Returns the maximum value of this Scrollbar.
     * @see #getMinimum
     * @see #getValue
     * @return int
     */
    public final int getMaximum() {
    	return maximum;
    }

    /**
     * Returns the visibleAmount amount of the Scrollbar.
     * @return int
     */
    public final int getVisibleAmount() {
    	return visibleAmount;
    }

    /**
     * Sets the line increment for this scrollbar. This is the value
     * that will be added (subtracted) when the user hits the line down
     * (up) gadgets.
     */
    public void setUnitIncrement(int l) {
    	unitIncrement = l;
    }

    /**
     * Gets the line increment for this scrollbar.
     * @return int
     */
    public int getUnitIncrement() {
    	return unitIncrement;
    }

    /**
     * Sets the block increment for this scrollbar. This is the value
     * that will be added (subtracted) when the user hits the block down
     * (up) gadgets.
     */
    public void setBlockIncrement(int p) {
    	blockIncrement = p;
    }

    /**
     * Gets the block increment for this scrollbar.
     * @return int
     */
    public int getBlockIncrement() {
    	return blockIncrement;
    }

    /**
     * Sets the values for this Scrollbar.
     * @param value is the position in the current window.
     * @param visibleAmount is the amount visibleAmount per block
     * @param minimum is the minimum value of the scrollbar
     * @param maximum is the maximum value of the scrollbar
     */
    public void setValues( int value, int visibleAmount,
                                        int minimum, int maximum ) {
		synchronized(getTreeLock()) {
            if ( ( value == this.value ) &&
                 ( minimum == this.minimum ) &&
                 ( maximum == this.maximum ) &&
                 ( visibleAmount == this.visibleAmount ) ) {
                return;
            }

        	if (maximum < minimum) {
        	    maximum = minimum;
        	}

        	this.visibleAmount = visibleAmount;
        	this.minimum = minimum;
        	this.maximum = maximum;

        	if (value < minimum) {
        	    setValue( minimum );
        	}
        	if (value > maximum) {
        	    setValue( maximum );
        	}
            adjustBubble(value);
        }
    }

    /**
     * setMinumum
     * @param minumum	description
     */
    public void setMinimum( int minimum ) {
		synchronized(getTreeLock()) {
            if ( minimum == this.minimum ) {
                return;
            }
        	if (maximum < minimum) {
        	    minimum = maximum;

        	}
        	this.minimum = minimum;

        	if (value < minimum) {
        	    setValue( minimum );
        	}
            adjustBubble(value);
        }
    }

    /**
     * setMaximum
     * @param maximum	description
     */
    public void setMaximum( int maximum ) {
		synchronized(getTreeLock()) {
            if ( maximum == this.maximum ) {
                return;
            }
        	if (maximum < minimum) {
        	    maximum = minimum;

        	}
        	this.maximum = maximum;

        	if (value > maximum) {
        	    setValue( maximum );
        	}
            adjustBubble(value);
        }
    }

    /**
     * setVisibleAmount
     * @param visibleAmount	description
     */
    public void setVisibleAmount( int visibleAmount ) {
		synchronized(getTreeLock()) {
            if ( visibleAmount == this.visibleAmount ) {
                return;
            }
            if ( visibleAmount < 1 ) {
                visibleAmount = 1;
            }
        	this.visibleAmount = visibleAmount;
            adjustBubble(value);
        }
    }

    /**
     * setGutterColor
     * @param g	description
     */
    public void setGutterColor( Color g ) {
        gutterColor = g;
        repaint();
    }

    /**
     * getGutterColor
     * @return Color
     */
    public Color getGutterColor() {
        return gutterColor;
    }

    /**
     * posToValue
     * @param pos	description
     * @return int
     */
    public int posToValue( int pos ) {
        int offset = incrArrow.height;
        if ( orientation == HORIZONTAL ) {
            offset = decrArrow.width;
        }
        if ( ( maximum > minimum ) &&
             ( gutterLength > bubbleLength ) ) {
            int value = (int)( ( (double)( pos - offset ) /
                            (double)( gutterLength - bubbleLength ) ) *
                          ( maximum - minimum ) );
            return value;
        }
        else {
            return 0;
        }
    }

    /**
     * valueToPos
     * @param value	description
     * @return int
     */
    public int valueToPos( int value ) {
        int offset = incrArrow.height;
        if ( orientation == HORIZONTAL ) {
            offset = decrArrow.width;
        }
        if ( ( value > 0 ) &&
             ( maximum > minimum ) ) {
            return offset + (int)( ( gutterLength - bubbleLength ) *
                                   ( (double)value /
                                     (double)( maximum - minimum ) ) );
        }
        else {
            return offset;
        }
    }

    /**
     * adjustBubble
     */
    protected void adjustBubble(int bubbleValue) {

		synchronized(getTreeLock()) {
            if ( ( incrArrow == null ) ||
                 ( decrArrow == null ) ) {
               return;
            }

            if ( orientation == HORIZONTAL ) {
                gutterLength = width - incrArrow.width - decrArrow.width;
            }
            else {
                gutterLength = height - incrArrow.height - decrArrow.height;
            }
            bubbleLength = gutterLength;
            if ( maximum > minimum ) {
                bubbleLength = (int)( gutterLength * ((double)visibleAmount /
                                       (double)( visibleAmount + (maximum-minimum) ) ) );
            }
            bubble.setVisible(true,false);
            if ( orientation == HORIZONTAL ) {
                bubbleLength = Math.max( bubbleLength, Math.min(height,(int)(gutterLength/2)) );
                bubble.setBounds( valueToPos( bubbleValue ), 0, bubbleLength, height, false );
            }
            else {
                bubbleLength = Math.max( bubbleLength, Math.min(width,(int)(gutterLength/2)) );
                bubble.setBounds( 0, valueToPos( bubbleValue ), width, bubbleLength, false );
            }
            if (bubbleLength > gutterLength) {
                bubble.setVisible(false,false);
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
            adjustBubble(value);
        }
    }

    /**
     * Paints the gadget.
     * @param g - the specified GadgetGraphics window
     */

	public void paint( GadgetGraphics g ) {

	    Color gutterColor = this.gutterColor;
	    if ( gutterColor == null ) {
    	    Color background = getBackground(g);
            if ( background == Gadget.transparent ) {
                gutterColor = Gadget.transparent;
            }
            else {
                gutterColor = GadgetGraphics.brighter( background, 0.85 );
            }
        }

	    if ( gutterColor != Gadget.transparent ) {
    	    g.setColor( gutterColor );
    	    g.fillRect( 0, 0, width, height );
	    }
	    else {
	        g.setColor( getForeground( g) );
    	    g.drawRect( 0, 0, width, height );
	    }
	}

	/**
	 * Adds the specified listener to be notified when component
	 * events occur on this component.
	 *
	 * @param l 	the listener to receive the events
	 */
	public synchronized void addAdjustmentListener(AdjustmentListener l) {
        adjustmentListener = GWTEventMulticaster.add(adjustmentListener, l);
	}

	/**
	 * Removes the specified listener so it no longer receives
	 * adjustment events on this adjustment.
	 *
	 * @param l 		the listener to remove
	 */
	public synchronized void removeAdjustmentListener(AdjustmentListener l) {
        adjustmentListener = GWTEventMulticaster.remove(adjustmentListener, l);
	}

/**
 * Processes events occurring on this action. By default this method will call the appropriate "handleXXX" method for the type of event.
 * @param e - TBD
 * @return boolean
 */

	protected void processEvent(AWTEvent e) {

		if (e instanceof AdjustmentEvent) {
		    processAdjustmentEvent((AdjustmentEvent)e);
		} else {
		    super.processEvent(e);
		}
    }

	protected void processAdjustmentEvent(AdjustmentEvent e) {
	    if (adjustmentListener != null) {
    		switch( e.getID() ) {

    			case AdjustmentEvent.UNIT_DECREMENT:
    			case AdjustmentEvent.UNIT_INCREMENT:
    			case AdjustmentEvent.BLOCK_DECREMENT:
    			case AdjustmentEvent.BLOCK_INCREMENT:
    			case AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED:
    			case AdjustmentEvent.BEGIN:
    			case AdjustmentEvent.END: {
        			adjustmentListener.adjustmentValueChanged(e);
    				break;
    			}
    		}
    	}
	}

    /**
     * processMouseEvent
     * @param e	description
     */
    protected void processMouseEvent(MouseEvent e) {
    	if ( e.getID() == MouseEvent.MOUSE_PRESSED) {
    		synchronized(getTreeLock()) {
        	    if ( e.getSource() == this ) {
            	    if ( orientation == HORIZONTAL ) {
                	    int clickPos = e.getX();
                	    if ( ( clickPos > ( decrArrow.x + decrArrow.width ) ) &&
                	         ( clickPos < bubble.x ) ) {
                            startAdjust( -blockIncrement, e.getEvent(), AdjustmentEvent.BLOCK_DECREMENT );
                	    }
                	    else if ( ( clickPos < incrArrow.x ) &&
                	              ( clickPos > ( bubble.x + bubble.width ) ) ) {
                            startAdjust( blockIncrement, e.getEvent(), AdjustmentEvent.BLOCK_INCREMENT );
                	    }
                	}
                	else {
                	    int clickPos = e.getY();
                	    if ( ( clickPos > ( decrArrow.y + decrArrow.height ) ) &&
                	         ( clickPos < bubble.y ) ) {
                            startAdjust( -blockIncrement, e.getEvent(), AdjustmentEvent.BLOCK_DECREMENT );
                	    }
                	    else if ( ( clickPos < incrArrow.y ) &&
                	              ( clickPos > ( bubble.y + bubble.height ) ) ) {
                            startAdjust( blockIncrement, e.getEvent(), AdjustmentEvent.BLOCK_INCREMENT );
                	    }
                	}
            	}
            	e.consume();
        	}
        } else if ( e.getID() == MouseEvent.MOUSE_RELEASED) {
            stopAdjust();
        	e.consume();
        }
	    super.processMouseEvent( e );
	}

    /**
     * mouseClicked
     * @param e	description
     */
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * mousePressed
     * @param e	description
     */
    public void mousePressed(MouseEvent e) {
        if ( e.getSource() == decrArrow ) {
            startAdjust( -unitIncrement, e.getEvent(), AdjustmentEvent.UNIT_DECREMENT );
        }
        else if ( e.getSource() == incrArrow ) {
            startAdjust( unitIncrement, e.getEvent(), AdjustmentEvent.UNIT_INCREMENT );
        }
    	else if ( e.getSource() == bubble ) {
    	    if ( orientation == HORIZONTAL ) {
        	    anchorMousePos = e.getX();
            	anchorBubblePos = bubble.x;
        	}
        	else {
        	    anchorMousePos = e.getY();
            	anchorBubblePos = bubble.y;
        	}
    	}
    }

    /**
     * mouseReleased
     * @param e	description
     */
    public void mouseReleased(MouseEvent e) {
        stopAdjust();
    	if ( e.getSource() == bubble ) {
    	    moveBubble(e, true);
    	}
    }

    /**
     * mouseEntered
     * @param e	description
     */
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * mouseExcited
     * @param e	description
     */
    public void mouseExited(MouseEvent e) {
    }

    /**
     * mouseDragged
     * @param e	description
     */
    public void mouseDragged(MouseEvent e) {
    	if ( e.getSource() == bubble ) {
    	    moveBubble(e, activeUpdate);
    	}
    }

    private void moveBubble(MouseEvent e, boolean update) {
    	if ( e.getSource() == bubble ) {
        	if ( ( maximum > minimum ) &&
        	     ( gutterLength > bubbleLength ) ) {
        	    int newPos;
        	    int bubblePos;
        	    if ( orientation == HORIZONTAL ) {
            	    if ( Math.abs( e.getY() - ( bubble.y + ( bubble.height / 2 ) ) )
            	         > 50 ) {
            	        newPos = anchorMousePos;
            	        bubblePos = anchorBubblePos;
            	    }
            	    else {
                	    newPos = e.getX();
                	    bubblePos = bubble.x;
                	}
            	}
            	else {
            	    if ( Math.abs( e.getX() - ( bubble.x + ( bubble.width / 2 ) ) )
            	         > 50 ) {
            	        newPos = anchorMousePos;
            	        bubblePos = anchorBubblePos;
            	    }
            	    else {
                	    newPos = e.getY();
                	    bubblePos = bubble.y;
                	}
            	}
    	        int newBubblePos = bubblePos + ( newPos - anchorMousePos );
    	        int newValue = posToValue( newBubblePos );
    	        newValue = goodValue(newValue);
                if (update) {
                    setValue( newValue,
                              e.getEvent(), AdjustmentEvent.TRACK );
                } else {
                    adjustBubble(newValue);
                }
            }
    	}
    }

    /**
     * mouseMoved
     * @param e	description
     */
    public void mouseMoved(MouseEvent e) {
    }
}

/**
 * ScrollBubbleGadged
 * @version 1.1
 * @author DTAI, Incorporated
 */
class ScrollBubbleGadget extends BorderGadget {

    ScrollBubbleGadget() {
    	setBorderType( BorderGadget.THREED_OUT );
    	setBorderThickness( 2 );
        setConsumingTransparentClicks(true);
    }
}
