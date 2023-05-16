/****************************************************************
 **
 **  $Id: ListManager.java,v 1.9 1997/08/06 23:27:07 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/ListManager.java,v $
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

import java11.awt.event.AWTEvent;
import java11.awt.event.InputEvent;

/**
 * ListManager
 *
 * @author		DTAI, Inc.
 */
public interface ListManager {

    /**
     * selectionChanged
     * @param event		AWTEvent
     * @param selected	boolean
	 */
    public abstract void selectionChanged( AWTEvent event, boolean selected );

	/**
	 * moveLeft
	 * @param evt		InputEvent
	 */
	public abstract void moveLeft( InputEvent evt );

	/**
	 * moveRight
	 * @param evt		InputEvent
	 */
	public abstract void moveRight( InputEvent evt );

	/**
	 * topChanged
	 * @param topIndex 	int
	 */
	public abstract void topChanged( int topIndex );
}
