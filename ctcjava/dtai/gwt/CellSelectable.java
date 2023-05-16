/****************************************************************
 **
 **  $Id: CellSelectable.java,v 1.4 1997/08/06 23:27:00 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/CellSelectable.java,v $
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

public interface CellSelectable {

	/**
	 * Returns the number of selected cells.
	 * @return	int
	 */
    public abstract int getSelectedCount();

	/**
	 * Returns the selected indexes or null if no items are selected.
	 * @return   an array of GadgetCells
	 */
    public abstract GadgetCell[] getSelectedCells();

	/**
	 * Add a listener to recieve cell events when the state of an cell changes.
	 *
	 * @param l 	the listener to recieve events
	 */
    public abstract void addCellListener(CellListener l);

	/**
	 * Removes an cell listener.
	 *
	 * @param l		the listener being removed
	 */
    public abstract void removeCellListener(CellListener l);
}
