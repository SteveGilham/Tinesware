/****************************************************************
 **
 **  $Id: CheckboxGadgetGroup.java,v 1.10 1997/08/06 23:27:00 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/CheckboxGadgetGroup.java,v $
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

import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * This class is used to create a multiple-exclusion scope for a set
 * of Checkbox buttons. For example, creating a set of Checkbox buttons
 * with the same CheckboxGroup object means that only one of those Checkbox
 * buttons will be allowed to be "on" at a time.
 *
 * @version	1.1
 * @author	DTAI, Incorporated
 */
public class CheckboxGadgetGroup {

    Vector checkboxes = new Vector();
    CheckboxGadget selectedCheckbox;

    /**
     * Adds the given CheckboxGadget to the checkboxes vector.
	 * If the selectedCheckbox object is null, the given
	 * CheckboxGadget is selected, else selectedCheckbox
	 * remains unchanged.
     *
     * @param checkbox	a CheckboxGadget to be added to this
	 *                  CheckboxGadgetGroup.
     */
    protected void add( CheckboxGadget checkbox ) {
        checkboxes.addElement( checkbox );
        if ( selectedCheckbox == null ) {
            selectedCheckbox = checkbox;
        }
    }

    /**
     * Removes the given CheckboxGadget from the checkboxes vector.
	 * If the CheckboxGadget to be removed is the selected one,
	 * then the selected one is set to the first element of the
	 * checkboxes vector, if the vector has any elements.  If it
	 * is empty, then selectedCheckbox becomes null.
     *
     * @param checkbox	a CheckboxGadget to be removed from this
	 * CheckboxGadgetGroup.
     */
    protected void remove( CheckboxGadget checkbox ) {
        checkboxes.removeElement( checkbox );
        if ( checkbox == selectedCheckbox ) {
            try {
                selectedCheckbox = (CheckboxGadget)checkboxes.firstElement();
            }
            catch ( NoSuchElementException nsee ) {
                selectedCheckbox = null;
            }
        }
    }

    /**
     * Gets the selectedCheckbox.
     *
     * @return CheckboxGadget
     */
    public CheckboxGadget getSelectedCheckbox() {
        return selectedCheckbox;
    }

    /**
	 * If the given CheckboxGadget is in the checkboxes vecter,
	 * then selectedCheckbox is set to it.
     *
     * @param checkbox	new value for selectedCheckbox
     */
    public void setSelectedCheckbox( CheckboxGadget checkbox ) {
        if ( checkboxes.contains( checkbox ) ) {
            selectedCheckbox = checkbox;
        }
    }
}
