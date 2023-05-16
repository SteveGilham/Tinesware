/****************************************************************
 **
 **  $Id: ClickableGadget.java,v 1.34 1997/11/20 19:37:51 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/ClickableGadget.java,v $
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
import java11.awt.event.AWTEvent;
import java11.awt.event.FocusEvent;
import java11.awt.event.InputEvent;
import java11.awt.event.KeyEvent;
import java11.awt.event.MouseEvent;

/**
 * Clickable Gadget
 *
 * @version	1.1
 * @author	DTAI, Incorporated
 */
public class ClickableGadget extends BorderGadget {

    public static final int UNARMED = 0;
    public static final int ARMED = 1;
    public static final int OVER = 2;

    private int mouseState = UNARMED;
    private boolean armable = false;
    private boolean focusAllowed = true;

    private BorderGadget unarmedBorder;
    private BorderGadget armedBorder;
    private BorderGadget overBorder;

	/**
	 * Constructs a Button with no label.
 	 */
	public ClickableGadget() {
	    init();
	}

	private void init() {
	    setLayout( new GadgetBorderLayout() );
	}

	/**
	 * sets the button as enabled
	 *
	 * @param enabled	description
	 */
	public void setEnabled(boolean enabled) {
	    super.setEnabled(enabled);
	    if ( ! enabled ) {
	        setMouseState(UNARMED);
	    }
	}

	/**
	 * setMouseState
	 *
	 * @param mouseState	description
	 */
	public void setMouseState( int mouseState ) {
	    if ( this.mouseState != mouseState ) {
	        this.mouseState = mouseState;
            if ( mouseState == ARMED ) {
                if ( armedBorder != null ) {
                    setBorder( armedBorder );
                }
            }
            else if ( mouseState == OVER ) {
                if ( overBorder != null ) {
                    setBorder( overBorder );
                }
                else if ( unarmedBorder != null ) {
                    setBorder( unarmedBorder );
                }
            }
            else {
                if ( unarmedBorder != null ) {
                    setBorder( unarmedBorder );
                }
            }
	    }
	}

	/**
	 * getMouseState
	 *
	 * @return int
	 */
	public int getMouseState() {
	    return mouseState;
	}

	/**
	 * getMinimumSize
	 *
	 * @return Dimension
	 */

	public Dimension getMinimumSize() {
	    return getPreferredSize();
	}

	/**
	 * setFocusAllowed
	 *
	 * @param focusAlloed	description
	 */
	public void setFocusAllowed( boolean focusAllowed ) {
	    if ( this.focusAllowed != focusAllowed ) {
	        this.focusAllowed = focusAllowed;
	        repaint();
	    }
	}

	/**
	 * isFocusAllowed
	 *
	 * @return boolean
	 */
	public boolean isFocusAllowed() {
	    return focusAllowed;
	}

	/**
	 * isFocusTraversable
	 *
	 * @return boolean
	 */
	public boolean isFocusTraversable() {
		return focusAllowed;
	}

	/**
	 * getUnarmedBorder
	 *
	 * @return BorderGadget
	 */
	public BorderGadget getUnarmedBorder() {
		return unarmedBorder;
	}

	/**
	 * setUnarmedBorder
	 *
	 * @param unarmedBorder	description
	 */
	public void setUnarmedBorder(BorderGadget unarmedBorder) {
	    synchronized(getTreeLock()) {
    		this.unarmedBorder = unarmedBorder;
    		if ( ( mouseState == UNARMED ) ||
    		     ( ( mouseState == ARMED ) &&
    		       ( armedBorder == null ) ) ||
    		     ( ( mouseState == OVER ) &&
    		       ( overBorder == null ) ) ) {
    		    setBorder( unarmedBorder );
    		}
    	}
	}

	/**
	 * getArmedBorder
	 *
	 * @return BorderGadget
	 */
	public BorderGadget getArmedBorder() {
		return armedBorder;
	}

	/**
	 * setArmedBorder
	 *
	 * @param armedBorder	description
	 */
	public void setArmedBorder(BorderGadget armedBorder) {
	    synchronized(getTreeLock()) {
    		this.armedBorder = armedBorder;
    		if ( mouseState == ARMED ) {
    		    setBorder( armedBorder );
    		}
    	}
	}

	/**
	 * getOverBorder
	 *
	 * @return BorderGadget
	 */
	public BorderGadget getOverBorder() {
		return overBorder;
	}

    /**
     * setOverBorder
     *
     * @param overBorder	description
     */
	public void setOverBorder(BorderGadget overBorder) {
	    synchronized(getTreeLock()) {
    		this.overBorder = overBorder;
    		if ( mouseState == OVER ) {
    		    setBorder( overBorder );
    		}
    	}
	}

	/**
	 * Sets the background color.
	 * @param c		the Color
	 * @see Gadget#getBackground
	 */
	public void setNormalBackground(Color c) {
	    super.setNormalBackground( c );
	    if ( armedBorder != null ) {
            armedBorder.normalBackground = c;
        }
        if ( unarmedBorder != null ) {
            unarmedBorder.normalBackground = c;
        }
        if ( overBorder != null ) {
            overBorder.normalBackground = c;
        }
	}

    /**
     * processKeyEvent
     *
     * @param e	description
     */
    protected void processKeyEvent(KeyEvent e) {
	    if (e.getID() == KeyEvent.KEY_PRESSED) {
    	    if ( e.getKeyChar() == ' ' ) {
        	    setMouseState( ARMED );
        	    e.consume();
        	}
        	else if ( ( e.getKeyCode() == KeyEvent.UP ) ||
        	          ( e.getKeyCode() == KeyEvent.LEFT ) ) {
        	    if(prevGroupFocus()) {
        	        e.consume();
        	    }
            }
        	else if ( ( e.getKeyCode() == KeyEvent.DOWN ) ||
        	          ( e.getKeyCode() == KeyEvent.RIGHT ) ) {
        	    if(nextGroupFocus()) {
        	        e.consume();
        	    }
        	}
        } else if (e.getID() == KeyEvent.KEY_RELEASED) {
    	    if ( ( ! e.isActionKey() ) &&
    	         ( e.getKeyChar() == ' ' ) ) {
    	        if ( hasMouse() ) {
            	    setMouseState( OVER );
            	}
            	else {
            	    setMouseState( UNARMED );
            	}
    	        click( e );
        	    e.consume();
        	}
        }
	    super.processKeyEvent( e );
	}

	/**
	 * processFocusEvent
	 *
	 * @param e	description
	 */
	protected void processFocusEvent(FocusEvent e) {
	    repaint();
	    super.processFocusEvent( e );
	}

	/**
	 * processMouseEvent
	 *
	 * @param e	description
	 * @return boolean
	 */
	protected void processMouseEvent(MouseEvent e) {
	    switch(e.getID()) {
	        case MouseEvent.MOUSE_PRESSED: {
        	    armable = true;
        	    setMouseState( ARMED );
        	    if ( e.getClickCount() > 1 ) {
        	        multiClick( e, e.getClickCount() );
        	    }
        	    e.consume();
        	    break;
        	}
	        case MouseEvent.MOUSE_RELEASED: {
        	    armable = false;
        	    if ( contains( e.getX(), e.getY() ) ) {
        	        int oldState = getMouseState();
            	    setMouseState( OVER );
            	    if (oldState == ARMED) {
                	    getShell().flushGraphics();
            	        click( e );
            	    }
            	}
            	else {
            	    setMouseState( UNARMED );
            	}
        	    e.consume();
        	    break;
        	}
	        case MouseEvent.MOUSE_ENTERED: {
        	    if ( mouseState != ARMED ) {
            	    setMouseState( OVER );
            	}
        	    e.consume();
        	    break;
        	}
	        case MouseEvent.MOUSE_EXITED: {
        		setMouseState( UNARMED );
        	    e.consume();
        	    break;
        	}
        }
	    super.processMouseEvent( e );
	}

	/**
	 * processMouseEvent
	 *
	 * @param e	description
	 * @return boolean
	 */
	protected void processMouseMotionEvent(MouseEvent e) {
	    if(e.getID() == MouseEvent.MOUSE_DRAGGED) {
    	    if ( armable ) {
        	    if ( contains( e.getX(), e.getY() ) ) {
        	        setMouseState( ARMED );
        	    }
        	    else {
        	        setMouseState( UNARMED );
        	    }
        	    e.consume();
        	}
	    }
	    super.processMouseMotionEvent(e);
	}

	/**
	 * click
	 *
	 * @param e	description
	 */
    protected void click( AWTEvent e ) {
	}

	/**
	 * multiClick
	 *
	 * @param e	description
	 * @param clickCount	description
	 */
    protected void multiClick( AWTEvent e, int clickCount ) {
	}

	/**
	 * sets the border type
	 *
	 * @param type	description
	 */
     public void setBorderType( int type ) {
		super.setBorderType(type);
		if ( unarmedBorder != null ) {
			unarmedBorder.setBorderType(type);
		}
		if ( armedBorder != null ) {
			armedBorder.setBorderType(type);
		}
		if ( overBorder != null ) {
			overBorder.setBorderType(type);
		}
	}

	/**
	 * sets the border color
	 *
	 * @param border	description
	 */
    public void setBorderColor(Color border) {
		super.setBorderColor(border);
		if ( unarmedBorder != null ) {
			unarmedBorder.setBorderColor(border);
		}
		if ( armedBorder != null ) {
			armedBorder.setBorderColor(border);
		}
		if ( overBorder != null ) {
			overBorder.setBorderColor(border);
		}
	}

	/**
	 * sets the default thickness of the border
	 *
	 * @param defaultThickness	description
	 */
    public void setDefaultThickness(int defaultThickness) {
		super.setDefaultThickness(defaultThickness);
		if ( unarmedBorder != null ) {
			unarmedBorder.setDefaultThickness(defaultThickness);
		}
		if ( armedBorder != null ) {
			armedBorder.setDefaultThickness(defaultThickness);
		}
		if ( overBorder != null ) {
			overBorder.setDefaultThickness(defaultThickness);
		}
	}

	/**
	 * sets border is/is not closeShaved
	 *
	 * @param closeShaved	description
	 */
    public void setCloseShaved(boolean closeShaved) {
		super.setCloseShaved(closeShaved);
		if ( unarmedBorder != null ) {
			unarmedBorder.setCloseShaved(closeShaved);
		}
		if ( armedBorder != null ) {
			armedBorder.setCloseShaved(closeShaved);
		}
		if ( overBorder != null ) {
			overBorder.setCloseShaved(closeShaved);
		}
	}

	/**
	 * sets the shadow color of the border
	 *
	 * @param shadow	description
	 */
     public void setShadowColor(Color shadow) {
		super.setShadowColor(shadow);
		if ( unarmedBorder != null ) {
			unarmedBorder.setShadowColor(shadow);
		}
		if ( armedBorder != null ) {
			armedBorder.setShadowColor(shadow);
		}
		if ( overBorder != null ) {
			overBorder.setShadowColor(shadow);
		}
	}

	/**
	 * sets the border with no insets
	 *
	 */
     public void setNoInsets() {
		super.setNoInsets();
		if ( unarmedBorder != null ) {
			unarmedBorder.setNoInsets();
		}
		if ( armedBorder != null ) {
			armedBorder.setNoInsets();
		}
		if ( overBorder != null ) {
			overBorder.setNoInsets();
		}
	}

	/**
	 * sets the specified top, left, bottom, and right margins of the border
	 *
	 * @param top	description
	 * @param left	description
	 * @param bottom	description
	 * @param right	description
	 */
    public void setMargins( int top, int left, int bottom, int right ) {
	    synchronized(getTreeLock()) {
    		super.setMargins(top, left, bottom, right);
    		if ( unarmedBorder != null ) {
    			unarmedBorder.setMargins(top, left, bottom, right);
    		}
    		if ( armedBorder != null ) {
    			armedBorder.setMargins(top, left, bottom, right);
    		}
    		if ( overBorder != null ) {
    			overBorder.setMargins(top, left, bottom, right);
    		}
    	}
	}

	/**
	 * sets the specified thickness of the border
	 *
	 * @param borderThickness	description
	 */
    public void setBorderThickness( int borderThickness ) {
		super.setBorderThickness(borderThickness);
		if ( unarmedBorder != null ) {
			unarmedBorder.setBorderThickness(borderThickness);
		}
		if ( armedBorder != null ) {
			armedBorder.setBorderThickness(borderThickness);
		}
		if ( overBorder != null ) {
			overBorder.setBorderThickness(borderThickness);
		}
	}

	/**
	 * sets the specified focus thickness of the border
	 *
	 * @param focusThickness	description
	 */
     public void setFocusThickness( int focusThickness ) {
		super.setFocusThickness(focusThickness);
		if ( unarmedBorder != null ) {
			unarmedBorder.setFocusThickness(focusThickness);
		}
		if ( armedBorder != null ) {
			armedBorder.setFocusThickness(focusThickness);
		}
		if ( overBorder != null ) {
			overBorder.setFocusThickness(focusThickness);
		}
	}

	/**
	 * sets the X Offset of the border shadow
	 *
	 * @param shadowXOffset	description
	 */
	public void setShadowXOffset( int shadowXOffset ) {
		super.setShadowXOffset(shadowXOffset);
		if ( unarmedBorder != null ) {
			unarmedBorder.setShadowXOffset(shadowXOffset);
		}
		if ( armedBorder != null ) {
			armedBorder.setShadowXOffset(shadowXOffset);
		}
		if ( overBorder != null ) {
			overBorder.setShadowXOffset(shadowXOffset);
		}
	}

	/**
	 * sets the Y Offset of the border shadow
	 *
	 * @param shadowYOffset	description
	 */
	public void setShadowYOffset( int shadowYOffset ) {
		super.setShadowYOffset(shadowYOffset);
		if ( unarmedBorder != null ) {
			unarmedBorder.setShadowYOffset(shadowYOffset);
		}
		if ( armedBorder != null ) {
			armedBorder.setShadowYOffset(shadowYOffset);
		}
		if ( overBorder != null ) {
			overBorder.setShadowYOffset(shadowYOffset);
		}
	}
}
