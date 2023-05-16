/****************************************************************
 **
 **  $Id: StatusHandler.java,v 1.1 1997/09/30 03:15:56 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/util/StatusHandler.java,v $
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

package dtai.util;

/**
 * StatusHandler - this shows status messages
 *
 * @version	1.0b1
 * @author	DTAI, Incorporated
 *
 * $Id: StatusHandler.java,v 1.1 1997/09/30 03:15:56 cvs Exp $
 *
 * $Source: /cvs/classes/dtai/util/StatusHandler.java,v $
 */

public interface StatusHandler {
    /**
     * shows a status message
     * @param message  the status message
     */
    public void showStatus(String message);
}
