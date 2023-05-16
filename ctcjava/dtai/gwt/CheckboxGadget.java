/****************************************************************
 **
 **  $Id: CheckboxGadget.java,v 1.37 1997/11/27 02:13:07 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/CheckboxGadget.java,v $
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
import java.awt.Image;
import java.awt.Insets;
import java.util.Vector;
import java11.awt.ItemSelectable;
import java11.awt.event.AWTEvent;
import java11.awt.event.FocusEvent;
import java11.awt.event.ItemEvent;
import java11.awt.event.ItemListener;

/**
 * A Checkbox object is a graphical user interface element that has a boolean
 * state.
 *
 * @version	1.1
 * @author 	DTAI, Incorporated
 */
 public class CheckboxGadget extends ClickableGadget implements java11.awt.ItemSelectable {

    public static final int CHECK = 0;
    public static final int RADIO = 1;

    private GadgetGridBagLayout gridbag;
    private GadgetGridBagConstraints constraints;

    private CheckboxIndicatorGadget indicator;
    private LabelGadget label;
    private ImageGadget image;
    private CheckboxGadgetGroup checkboxGroup;

    private ItemListener itemListener;

    private BorderGadget checkBorder;
    private BorderGadget values;

	/**
	 * Constructs a Checkbox with no label.
	 */
	public CheckboxGadget() {
		super();
		init();
	}

	/**
	 * Constructs a Checkbox with the given label.
     * @param label the label of the Checkbox.
     */
	public CheckboxGadget( String label ) {
	    this( label, false, null );
	}

	/**
	 * Constructs a Checkbox with the given label and state.
     * @param label label of the Checkbox
     * @param state	boolean state of the Checkbox
     */
	public CheckboxGadget( String label, boolean state ) {
	    this( label, state, null );
	}

	/**
	 * Constructs a Checkbox with the given label, state and group.
     * @param label label of the Checkbox
     * @param state	boolean state of the Checkbox
	 * @param group group of the Checkbox
	 */
	public CheckboxGadget( String label, boolean state, CheckboxGadgetGroup group ) {
		init();
		setLabel( label );
		setCheckboxGadgetGroup( group );
		setState( state );
	}

	/**
	 * CheckboxGadget
	 * @param image image of the Checkbox
	 */
	 public CheckboxGadget( Image image ) {
		init();
		setImage( image );
	}

	/**
	 * CheckboxGadget
     * @param label - label of the Checkbox
	 * @param image - image of the Checkbox
	 */
	public CheckboxGadget( String label, Image image ) {
		init();
		setLabel( label );
		setImage( image );
	}

	private void init() {
	    gridbag = new GadgetGridBagLayout();
	    setLayout( gridbag );

	    checkBorder = new BorderGadget();
	    checkBorder.setMargins(1);

        constraints = new GadgetGridBagConstraints ();
	    constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GadgetGridBagConstraints.NONE;
        constraints.anchor = GadgetGridBagConstraints.WEST;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.insets = new Insets( 0,0,0,0 );
        gridbag.setConstraints ( checkBorder, constraints );
        add ( checkBorder );

	    values = new BorderGadget();
	    values.setMargins(2);

        constraints.gridx = 1;
        constraints.gridwidth = GadgetGridBagConstraints.REMAINDER;
        constraints.weightx = 1.0;
        gridbag.setConstraints ( values, constraints );
        add ( values );

	    setBackground(Gadget.transparent);

	    setConsumingTransparentClicks( false );
	    setType( CHECK );
	}

	/**
	 * setType
	 * @param type - either CHECK, in which case indicator gets
	 * a new CheckGadget, or something else, in which case indicator
	 * gets a new RadioGadget.
	 */
	 public void setType( int type ) {
	    if ( indicator != null ) {
	        remove ( indicator );
	    }
	    if ( type == CHECK ) {
    	    indicator = new CheckGadget();
    	}
    	else {
    	    indicator = new RadioGadget();
    	}
        checkBorder.add ( "Center", indicator );
	}

	/**
	 * getType
	 *
	 * @return CHECK or RADIO, depending on indicator
	 */
	public int getType() {
	    if ( indicator instanceof CheckGadget ) {
	        return CHECK;
	    }
	    else {
	        return RADIO;
	    }
	}

	/**
	 * setState
	 * @param state	boolean SELECTED/DESELECTED
	 */
	public void setState( boolean state ) {
	    setState( state, null );
	}

	/**
	 * setState
	 * @param state	boolean SELECTED/DESELECTED
	 * @param mouse	the firing AWTEvent
	 */
	protected void setState( boolean state, AWTEvent mouse ) {
	    if ( indicator.isSelected() != state ) {
    		indicator.setSelected( state );
    		if ( state && ( checkboxGroup != null ) ) {
    		    CheckboxGadget current = checkboxGroup.getSelectedCheckbox();
    		    if ( ( current != null ) &&
    		         ( current != this ) ) {
    		        current.setState( false );
    		    }
    		    checkboxGroup.setSelectedCheckbox( this );
    		}
    	    int stateChange;
    	    if ( state ) {
    	        stateChange = ItemEvent.SELECTED;
    	    }
    	    else {
    	        stateChange = ItemEvent.DESELECTED;
    	    }
    	    Event event = null;
    	    if ( mouse != null ) {
    	        event = mouse.getEvent();
    	    }
    		processEvent( new ItemEvent( this, event,
    		                             ItemEvent.ITEM_STATE_CHANGED,
    		                             this, stateChange ) );
	    }
	}

	/**
	 * Returns the boolean state of the Checkbox.
	 * @see #setState
	 * @return boolean
	 */
	 public boolean getState() {
	    return indicator.isSelected();
	}

	/**
	 * setCheckboxGadgetGroup
	 *
	 * @param group	the CheckboxGadgetGroup to add this gadget to, or null
	 */
	 public void setCheckboxGadgetGroup( CheckboxGadgetGroup group ) {
	    if ( group != checkboxGroup ) {
	        if ( checkboxGroup != null ) {
	            checkboxGroup.remove( this );
	        }
    	    checkboxGroup = group;
    	    group.add( this );
    	    if ( group == null ) {
    	        setType( CHECK );
    	    }
    	    else {
    	        setType( RADIO );
    	    }
	        setState( group.getSelectedCheckbox() == this );
	        if ( checkboxGroup != null ) {
    	        setFocusGroup(parent);
    	    }
    	    else {
    	        setFocusGroup(null);
    	    }
    	}
    }

    /**
     * setParent
     *
     * @param parent	the parent ContainerGadget
     * @param notifyParent	to notify the parent or not
     */
    public void setParent(ContainerGadget parent, boolean notifyParent) {
        super.setParent(parent, notifyParent);
        if ( checkboxGroup != null ) {
            setFocusGroup(parent);
        }
    }

    /**
     * Returns the checkbox group.

     * @return CheckboxGadgetGroup
     */
    public CheckboxGadgetGroup getCheckboxGadgetGroup() {
        return checkboxGroup;
    }

	/**
	 * Gets the label of the button.
	 * @return the button label
	 * @see #setLabel
	 */
	public String getLabel() {
		return ( ( label == null ) ? null : label.getLabel() );
	}

	/**
	 * Sets the button with the specified label.
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
    	        this.label.setHorizAlign( LabelGadget.HORIZ_ALIGN_LEFT );
    	        if ( image == null ) {
    	            values.add( "Center", this.label );
    	        }
    	        else {
    	            values.add( "South", this.label );
    	        }
    	    }
    	    else {
        	    this.label.setLabel( label );
        	}
        	if (image == null && ( label == null || label.length() == 0 )) {
        	    checkBorder.setFocusThickness(1);
        	    values.setFocusThickness(0);
        	} else {
        	    checkBorder.setFocusThickness(0);
        	    values.setFocusThickness(1);
        	}
        }
	}

	/**
	 * setImage - sets the button with the specified Image
	 *
	 * @param image 	the Image to set
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
        	            values.add( "Center", label );
        	        }
        	    }
    	    }
    	    else if ( this.image == null ) {
    	        this.image = new ImageGadget( image );
    	        if ( label != null ) {
    	            remove( label );
    	            values.add( "South", label );
    	        }
    	        values.add( "Center", this.image );
    	    }
    	    else {
        	    this.image.setImage( image );
        	}
        }
	}

    /**
     * setMouseState
     *
     * @param state		new state value
     */
    public void setMouseState( int state ) {
		super.setMouseState( state );
	    indicator.setArmed( state == ARMED );
	    repaint();
	}


	/**
	 * click
	 *
	 * @param mouse		the firing AWTEvent
	 */
	public void click ( AWTEvent mouse ) {
	    if ( ( checkboxGroup == null ) ||
	         ( this != checkboxGroup.getSelectedCheckbox() ) ) {
    	    setState( ! getState(), mouse );
        }
        repaint();
	}

	/**
	 * Returns the selected indexes or null if no items are selected.
	 *
	 * @return the selected indexes, or null
	 */
	 public int[] getSelectedIndexes() {
        if ( getState() ) {
            int idxs[] = { 0 };
            return idxs;
        }
        else {
            return null;
        }
    }

	/**
	 * Returns the selected items or null if no items are selected.
	 *
	 * @return the selected items, or null
	 */
    public String[] getSelectedItems() {
        if ( getState() ) {
            String items[] = { getLabel() };
            return items;
        }
        else {
            return null;
        }
    }

	/**
	 * processFocusEvent
	 *
	 * @param e the firing FocusEvent
	 * @return boolean
	 */
	protected void processFocusEvent(FocusEvent e) {
	    if (e.getID() == FocusEvent.FOCUS_GAINED &&
	        getType() == RADIO &&
	        (getFocusGroup() != null)) {
	        setState(true);
	    }
	    super.processFocusEvent( e );
	}

	/**
	 * Adds the specified listener to be notified when component
	 * events occur on this component.
	 *
	 * @param l 	the listener to receive the events
	 */
	public synchronized void addItemListener(ItemListener l) {
        itemListener = GWTEventMulticaster.add(itemListener, l);
	}

	/**
	 * Removes the specified listener so it no longer receives
	 * item events on this item.
	 *
	 * @param l 		the listener to remove
	 */
	public synchronized void removeItemListener(ItemListener l) {
        itemListener = GWTEventMulticaster.remove(itemListener, l);
	}

	/**
	 * processEvent
	 *
	 * @param e		a ItemEvent
	 * @return boolean result
	 */
	protected void processEvent(AWTEvent e) {
		if (e instanceof ItemEvent) {
		    processItemEvent((ItemEvent)e);
		} else {
		    super.processEvent(e);
		}
	}

	protected void processItemEvent(ItemEvent e) {
		if (itemListener != null) {
    		if(e.getID() == ItemEvent.ITEM_STATE_CHANGED) {
    			itemListener.itemStateChanged(e);
    		}
    	}
	}

}
