/****************************************************************
 **
 **  $Id: CellEvent.java,v 1.13 1997/08/06 23:27:00 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/CellEvent.java,v $
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
import java11.awt.event.AWTEvent;
import java11.util.EventObject;

/**
 * CellEvent
 * @version 1.1
 * @author DTAI, Incorporated
 */
public class CellEvent extends AWTEvent {

	/**
	 * The cell state changed event type.
	 */
    public final static int CELL_STATE_CHANGED = 125;

    /**
     * The cell state first event type.
     */
    public final static int CELL_FIRST = 125;

    /**
     * The cell state final event type.
     */
    public final static int CELL_LAST = 125;

	/**
	 * The cell selected state change type.
	 */
    public final static int SELECTED = 1;

	/**
	 * The cell de-selected state change type.
	 */
    public final static int DESELECTED = 0;

    private int stateChange;

    /**
     * CellEvent constructor.
     */
    public CellEvent() {
    }

    /**
     * CellEvent constructor.
     *
     * @param source		event source
     * @param evt			Event fed to AWTEvent constructor
     * @param id			id fed to AWTEvent constructor
     * @param stateChange	new value for stateChange
     */
    public CellEvent( CellSelectable source, Event evt, int id, int stateChange ) {
        super( evt, id );
        setSource( source );
        this.stateChange = stateChange;
    }

	/**
	 * Returns the CellSelectable object where this event originated.
	 * @return		CellSelectable - object where this event originated
	 */
    public CellSelectable getCellSelectable() {
        return (CellSelectable)getSource();
    }

	/**
	 * Returns the state change type which generated the event.
	 * Possible values:  SELECTED, DESELECTED
	 *
	 * @return int - state change type which generated the event
	 */
    public int getStateChange() {
        return stateChange;
    }
}
