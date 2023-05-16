/****************************************************************
 **
 **  $Id: ShowUserHandler.java,v 1.5 1997/10/02 01:57:27 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/util/ShowUserHandler.java,v $
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

import java.applet.Applet;
import java.awt.Frame;

/**
 * ShowUserHandler - this shows the user things like error and warning messages, etc.
 *
 * @version	1.0b1
 * @author	DTAI, Incorporated
 *
 * $Id: ShowUserHandler.java,v 1.5 1997/10/02 01:57:27 cvs Exp $
 *
 * $Source: /cvs/classes/dtai/util/ShowUserHandler.java,v $
 */

public interface ShowUserHandler {

    /**
     * shows the user an applet
     * @param applet  the applet to be shown
     */
    public void setShowUserApplet( Applet applet );

    /**
     * suspends showing error and warning (etc.) dialogs
     */
    public void doSuspend();

    /**
     * resumes showing error and warning (etc.) dialogs
     */
    public void doResume();

    /**
     * shows user the base frame
     * @param   the base frame to be shown
     */
    public void setShowUserOuterFrame( Frame outerframe );

    /**
     * shows an error message
     * @param t - TBD
     * @param message - the error message
     * @param helpTopic - the help topic
     */
    public void showError( Throwable t, String message, String helpTopic);

    /**
     * doHideError
     */
    public void doHideError();

    /**
     * shows a warning message
     * @param message  the warning message
     */
    public void showWarning( String message );

    /**
     * doHideWarning
     */
    public void doHideWarning();

    /**
     * shows an info message
     * @param message  the info message
     */
    public void showInfo( String message );

    /**
     * doHideInfo
     */
    public void doHideInfo();

    /**
     * shows an about message
     * @param what  TBD
     * @param message  the about message
     */
    public void showAbout( String what, String message );

    /**
     * doHideInfo
     */
    public void doHideAbout();

    /**
     * shows a please wait message
     */
    public void showPleaseWait();

    /**
     * shows a finished waiting message
     */
    public void showFinishedWait();

    /**
     * shows a status message
     * @param message  the status message
     */
    public void showStatus( String message );
}
