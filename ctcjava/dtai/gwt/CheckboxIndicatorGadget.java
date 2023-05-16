/****************************************************************
 **
 **  $Id: CheckboxIndicatorGadget.java,v 1.15 1997/08/06 23:27:00 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/CheckboxIndicatorGadget.java,v $
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


/**
 * CheckboxIndicatorGadget
 *
 * @version	1.1
 * @author	DTAI, Incorporated
 */
public abstract class CheckboxIndicatorGadget extends DisplayGadget {

    boolean armed = false;

	/**
	 * Constructs a Label with no label.
	 */
	public CheckboxIndicatorGadget() {
	}

	/**
	 * isShowingSelected
	 * @return	boolean
	 */
	public boolean isShowingSelected() {
	    return false;
	}

	/**
	 * Returns true if armed.
	 * @return boolean
	 */
	public boolean isArmed() {
		return armed;
	}

	/**
	 * Sets the indicator to armed.
	 * @param armed		boolean armed/not armed
	 */
	public void setArmed( boolean armed ) {
	    synchronized(getTreeLock()) {
    	    if ( this.armed != armed ) {
        		this.armed = armed;
        		repaint();
        	}
        }
	}

	/**
	 * Sets the indicator to selected.
	 */
	public void toggle() {
	    synchronized(getTreeLock()) {
    	    setSelected( ! selected );
    	}
	}
}
