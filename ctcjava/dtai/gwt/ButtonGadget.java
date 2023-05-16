
/****************************************************************
 **
 **  $Id: ButtonGadget.java,v 1.52 1997/11/20 19:37:51 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/ButtonGadget.java,v $
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

import java.awt.Event;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.Vector;
import java11.awt.event.ActionEvent;
import java11.awt.event.ActionListener;
import java11.awt.event.AWTEvent;
import java11.awt.event.FocusEvent;

/**
 * A class that produces a labeled button component.
 *
 * @version	1.1
 * @author	DTAI, Incorporated
 */
public class ButtonGadget extends ClickableGadget {

    protected LabelGadget label;
    protected ImageGadget image;
    private boolean blackToForeground = false;
    Image unarmedImage;
    Image armedImage;
    Image overImage;
    Image disabledImage;

    ActionListener actionListener;

    /**
     * Constructs a Button with no label.
     */
	public ButtonGadget() {
		init();
	}

	/**
	 * Constructs a Button with a label.
	 *
	 * @param label   label must be a String
	 */
	public ButtonGadget( String label ) {
		init();
		setLabel( label );
	}

	/**
	 * Constructs a Button with an unarmedImage.
	 *
	 * @param unarmedImage   the unarmedImage for the Button
	 */
	public ButtonGadget( Image unarmedImage ) {
		init();
		setUnarmedImage( unarmedImage );
	}

	/**
	 * Constructs a Button with an unarmedImage, an armedImage,
	 * an overImage, and a disabledImage.
	 *
	 * @param unarmedImage   the unarmedImage for the Button, null if not set
	 * @param armedImage     the armedImage for the Button, null if not set
	 * @param overImage      the overImage for the Button, null if not set
	 * @param disabledImage  the disabledImage for the Button, null if not set
	 */
	public ButtonGadget( Image unarmedImage, Image armedImage,
						Image overImage, Image disabledImage ) {
		init();
		if ( unarmedImage != null ) setUnarmedImage( unarmedImage );
		if ( armedImage != null ) setArmedImage( armedImage );
		if ( overImage != null ) setOverImage( overImage );
		if ( disabledImage != null ) setDisabledImage( disabledImage );
	}

	/**
	 * Constructs a Button with a label and an image.
	 *
	 * @param label     the String label for the Button
	 * @param image     the image for the Button
	 */
	public ButtonGadget( String label, Image image ) {
		init();
		setLabel( label );
		setUnarmedImage( image );
	}

	/**
	 * Constructs a Button with a label, an unarmedImage, an armedImage,
	 * an overImage, and a disabledImage.
	 *
	 * @param label          the String label for the Button
	 * @param unarmedImage   the unarmedImage for the Button, null if not set
	 * @param armedImage     the armedImage for the Button, null if not set
	 * @param overImage      the overImage for the Button, null if not set
	 * @param disabledImage  the disabledImage for the Button, null if not set
	 */
	public ButtonGadget( String label, Image unarmedImage, Image armedImage,
	                     Image overImage, Image disabledImage ) {
		init();
		setLabel( label );
		if ( unarmedImage != null ) setUnarmedImage( unarmedImage );
		if ( armedImage != null ) setArmedImage( armedImage );
		if ( overImage != null ) setOverImage( overImage );
		if ( disabledImage != null ) setDisabledImage( disabledImage );
	}

	private void init() {
		BorderGadget unarmed = new BorderGadget();
        unarmed.setCloseShaved( true );
        unarmed.setDefaultThickness( 1 );
        unarmed.setBorderThickness( 2 );
		unarmed.setMargins( 3 ); // leave some room for focus
		unarmed.setFocusThickness(1);
		unarmed.setBorderType( BorderGadget.THREED_OUT );
	    setUnarmedBorder( unarmed );

		BorderGadget armed = (BorderGadget)unarmed.clone();
		armed.setMargins( 4, 4, 2, 2 );
		armed.setBorderType( BorderGadget.THREED_IN );
	    setArmedBorder( armed );

	    setConsumingTransparentClicks( true );
	}

	/**
	 * Returns the name of this Button, if it has one, else "Unmamed Button."
	 *
	 * @return the name of this Button
	 */
	 public String toString() {
	    if ( label == null ) {
	        return "Unnamed Button";
	    }
		return label.toString();
	}

	/**
	 * Sets the value of the parent's FocusAllowed variable to
	 * the supplied value.  If the given parameter is false,
	 * default values are set for FocusThickness(0), DefaultThickness(0),
	 * margins(1) and the margins of the ArmedBorder(2,2,0,0).
	 *
	 * @param focusAllowed	new value for parent's focusAllowed.
	 */
	 public void setFocusAllowed(boolean focusAllowed) {
	    super.setFocusAllowed(focusAllowed);
	    if ( ! focusAllowed ) {
            setDefaultThickness(0);
            setFocusThickness(0);
            setMargins(1);
            getArmedBorder().setMargins(2,2,0,0);
        }
	}

    /**
	 * Calls click.
     *
	 * @param e			an AWTEvent fed to method click
	 * @see #click
     */
    public void doDefaultAction(AWTEvent e) {
        click(e);
    }

    /**
     * Gets the label of the Button.
	 *
     * @return  String
     * @see #setLabel
     */
	public String getLabel() {
		return ( ( label == null ) ? null : label.getLabel() );
	}

	/**
	 * Sets the button with the specified label.
	 *
	 * @param label 	the label to set the button with
	 * @see #getLabel
	 */
	public void setLabel( String label ) {
	    synchronized(getTreeLock()) {
    	    if ( label == null ) {
    	        if ( this.label != null ) {
    	            remove( this.label );
    	            this.label = null;
        	    }
    	    }
    	    else if ( this.label == null ) {
    	        this.label = new LabelGadget( label );
    	        this.label.setPossiblyDisabled( true );
    	        if ( image == null ) {
    	            add( "Center", this.label );
    	        }
    	        else {
    	            add( "South", this.label );
    	        }
    	    }
    	    else {
        	    this.label.setLabel( label );
        	}
        }
	}

	/**
	 * Gets the UnarmedImage variable.
	 *
	 * @return Image - the UnarmedImage variable
	 */
	public Image getUnarmedImage() {
		return ( unarmedImage );
	}

	/**
	 * Gets the armedImage variable.
	 *
	 * @return Image - the armedImage variable
	 */
	public Image getArmedImage() {
		return ( armedImage );
	}

	/**
	 * Gets the overImage variable.
	 *
	 * @return Image - the overImage variable
	 */
	public Image getOverImage() {
		return ( overImage );
	}

	/**
	 * Gets the disabledImage variable.
	 * @return Image - the disabledImage variable
	 */
	public Image getDisabledImage() {
		return ( disabledImage );
	}


	/**
	 * Sets image to the supplied value.
	 * If the given Image parameter is null, this
	 * image is set to null, and this label is removed.
	 *
	 * @param image	- new value for image
	 */
	public void setImage( Image image ) {
	    synchronized(getTreeLock()) {
    	    if ( ( this.image != null ) &&
    	         ( image == this.image.getImage() ) ) {
    	        return;
    	    }
    	    if ( image == null ) {
    	        if ( this.image != null ) {
    	            remove( this.image );
    	            this.image = null;
        	        if ( label != null ) {
        	            remove( label );
        	            add( "Center", label );
        	        }
        	    }
    	    }
    	    else if ( this.image == null ) {
    	        this.image = new ImageGadget( image );
    	        this.image.setBlackToForeground(blackToForeground);
    	        if ( blackToForeground ) {
        	        this.image.setPossiblyDisabled( true );
        	    }
    	        if ( label != null ) {
    	            remove( label );
    	            add( "South", label );
    	        }
    	        add( "Center", this.image );
    	    }
    	    else {
        	    this.image.setImage( image );
        	}
        }
	}

	/**
	 * Set this to true if you want the black pixels to be converted to the
	 * current foreground color.
	 * @param blackToForeground		convert/not convert
	 */
	public void setBlackToForeground( boolean blackToForeground ) {
	    this.blackToForeground = blackToForeground;
	    if ( image != null ) {
	        image.setBlackToForeground(blackToForeground);
	        image.setPossiblyDisabled( true );
	    }
	}

	/**
	 * Gets the blacktoForeground value.
	 *
	 * @return boolean - the blacktoForeground value
	 */
	public boolean getBlackToForeground() {
	    return blackToForeground;
	}

	/**
	 * Sets the unarmedImage variable.
	 *
	 * @param unarmedImage - new Image for unarmedImage
	 */
	 public void setUnarmedImage( Image unarmedImage ) {
	    synchronized(getTreeLock()) {
    	    if ( this.unarmedImage != unarmedImage ) {
    	        this.unarmedImage = unarmedImage;
        	    if ( getMouseState() == UNARMED ) {
            	    setImage( unarmedImage );
            	}
            	else if ( unarmedImage != null ) {
            	    prepareImage( unarmedImage, this );
            	}
            }
        }
	}

	/**
	 * Sets the armedImage variable.
	 *
	 * @param armedImage - new Image for armedImage
	 */
	public void setArmedImage( Image armedImage ) {
	    synchronized(getTreeLock()) {
    	    if ( this.armedImage != armedImage ) {
    	        this.armedImage = armedImage;
        	    if ( getMouseState() == ARMED ) {
            	    setImage( armedImage );
            	}
            	else if ( armedImage != null ) {
            	    prepareImage( armedImage, this );
            	}
            }
        }
	}

	/**
	 * Sets the overImage variable.
	 *
	 * @param overImage	- new Image for overImage
	 */
	public void setOverImage( Image overImage ) {
	    synchronized(getTreeLock()) {
    	    if ( this.overImage != overImage ) {
    	        this.overImage = overImage;
        	    if ( getMouseState() == OVER ) {
            	    setImage( overImage );
            	}
            	else if ( overImage != null ) {
            	    prepareImage( overImage, this );
            	}
            }
        }
	}

	/**
	 * Sets the disabledImage variable.
	 *
	 * @param disabledImage	new Image for disabledImage
	 */
	public void setDisabledImage( Image disabledImage ) {
	    synchronized(getTreeLock()) {
    	    if ( this.disabledImage != disabledImage ) {
    	        this.disabledImage = disabledImage;

        	    if ( this.image == null ) {
        	        setImage( disabledImage );
        	    }
        	    if ( this.image != null ) {
            	    this.image.setDisabledImage( disabledImage );
            	}
            }
        }
	}

	/**
	 * Parent's mouse state is set with the given value,
	 * and if the parameter is ARMED or OVER the appropriate
	 * Image is set, and for any other non-null state, the
	 * unarmedImage is set.
	 *
	 * @param state	new mouse state
	 */
	public void setMouseState( int state ) {
	    if ( state != getMouseState() ) {
    		super.setMouseState( state );
            if ( state == ARMED ) {
                if ( armedImage != null ) {
            	    setImage( armedImage );
                }
            }
            else if ( state == OVER ) {
                if ( overImage != null ) {
            	    setImage( overImage );
                }
            }
            else {
                if ( unarmedImage != null ) {
            	    setImage( unarmedImage );
                }
            }
    	}
	}

	/**
	 * Adds the specified listener to be notified when component
	 * events occur on this component.
	 *
	 * @param l 	the listener to receive the events
	 */
	public synchronized void addActionListener(ActionListener l) {
        actionListener = GWTEventMulticaster.add(actionListener, l);
	}

	/**
	 * Removes the specified listener so it no longer receives
	 * action events on this action.
	 *
	 * @param l 		the listener to remove
	 */
	public synchronized void removeActionListener(ActionListener l) {
        actionListener = GWTEventMulticaster.remove(actionListener, l);
	}

	/**
	 * processEvent
	 *
	 * @param e		a ActionEvent
	 * @return boolean result
	 */
	protected void processEvent(AWTEvent e) {
		if (e instanceof ActionEvent) {
		    processActionEvent((ActionEvent)e);
		} else {
		    super.processEvent(e);
		}
	}

	protected void processActionEvent(ActionEvent e) {
	    if (actionListener != null) {
    		if(e.getID() == ActionEvent.ACTION_PERFORMED) {
    			actionListener.actionPerformed(e);
    		}
    	}
	}

	/**
	 * Processes events on this button
	 *
	 * @param mouse		the firing AWTEvent
	 */
    public void click ( AWTEvent mouse ) {
		processEvent( new ActionEvent( this, mouse.getEvent(),
		                               ActionEvent.ACTION_PERFORMED ) );
	}

	/**
	 * The shell's NormalDefaultGadget repaints (if there is one)
	 * and the parent's handleFocusGained method is called.
	 *
	 * @param e		the firing FocusEvent
	 * @return boolean
	 */
	 protected void processFocusEvent(FocusEvent e) {
	    GadgetShell shell = getShell();
	    if (shell != null) {
	        Gadget defaultGadget = shell.getNormalDefaultGadget();
	        if (defaultGadget != null) {
	            defaultGadget.repaint();
	        }
	    }
	    super.processFocusEvent( e );
	}

}
