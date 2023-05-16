/****************************************************************
 **
 **  $Id: ShowUser.java,v 1.15 1997/11/20 19:41:28 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/util/ShowUser.java,v $
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
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.io.PrintStream;
import java.util.Date;

/**
 * ShowUser - this shows the user things like error and warning messages, etc.
 *
 * You are NOT supposed to create a "new" object of type ShowUser.  Call it
 * using static functions only.  For example:
 *
 * ShowUser.error( excep, "There was a problem." );
 *
 * ShowUser.status( "Loading data..." );
 *
 * If you call ShowUser.setApplet( yourApplet ); it will use dialog windows
 * to show the messages, and will use the Applet's status window for status.
 *
 * @version	1.0b1
 * @author	DTAI, Incorporated
 *
 * $Id: ShowUser.java,v 1.15 1997/11/20 19:41:28 kadel Exp $
 *
 * $Source: /cvs/classes/dtai/util/ShowUser.java,v $
 */

public class ShowUser implements ShowUserHandler {

    static private Applet applet;
    static private Frame baseFrame;
    static private String default_status;
    static private String last_status;
    static private ShowUserHandler showUserHandler;
    static private StatusHandler statusHandler;

    private int waiting = 0;

    static {
        showUserHandler = new ShowUser();
    }

    /**
     * sets the default status
     * @param message  the message
     */
    static public void setDefaultStatus( String message ) {
        default_status = message;
        status( message );
    }

    static private synchronized ShowUserHandler getShowUserHandler() {
		if ( ShowUser.showUserHandler == null ) {
			ShowUser.showUserHandler = new ShowUser();
		}
		return ShowUser.showUserHandler;
    }

    /**
     * shows an error message
     * @param t  the error
     * @param message  the error message
     * @param helpTopic  the help topic
     */
    static public void error( Throwable t, String message, String helpTopic ) {
        getShowUserHandler().showError(t,message, helpTopic);
    }

    /**
     * shows an error message
     * @param t  the error
     * @param message  the error message
     */
    static public void error( Throwable t, String message ) {
        getShowUserHandler().showError(t,message,"");
    }

    /**
     * shows an error message
     * @param t  the error
     */
    static public void error( Throwable t ) {
		if ( t != null ) {
			error( t, t.getMessage(), "");
		}
		else {
		    error( null, "Unknown Error ???", "" );
		    new Exception().printStackTrace();
		}
    }

    /**
     * shows a warning message
     * @param message  the warning message
     */
    static public void warning( String message ) {
        getShowUserHandler().showWarning(message);
    }

    /**
     * shows an info message
     * @param message  the info message
     */
    static public void info( String message ) {
        getShowUserHandler().showInfo(message);
    }

    /**
     * shows an about message
     * @param what  TBD
     * @param message  the about message
     */
    static public void about( String what, String message ) {
        getShowUserHandler().showAbout(what, message);
    }

    /**
     * shows a please wait message
     */
    static public void pleaseWait() {
        getShowUserHandler().showPleaseWait();
    }

    /**
     * shows a finish waiting message
     */
    static public void finishedWait() {
        getShowUserHandler().showFinishedWait();
    }

    /**
     * hideError
     */
    static public void hideError() {
        ShowUserHandler showUserHandler = ShowUser.showUserHandler;
		if ( showUserHandler != null ) {
		    showUserHandler.doHideError();
		}
    }

    /**
     * hideWarning
     */
    static public void hideWarning() {
        ShowUserHandler showUserHandler = ShowUser.showUserHandler;
		if ( showUserHandler != null ) {
		    showUserHandler.doHideWarning();
		}
    }

    /**
     * hideInfo
     */
    static public void hideInfo() {
        ShowUserHandler showUserHandler = ShowUser.showUserHandler;
		if ( showUserHandler != null ) {
		    showUserHandler.doHideInfo();
		}
    }

    /**
     * hideAbout
     */
    static public void hideAbout() {
        ShowUserHandler showUserHandler = ShowUser.showUserHandler;
		if ( showUserHandler != null ) {
		    showUserHandler.doHideAbout();
		}
    }

    static private synchronized boolean sameAsLastStatus(String message) {
        if ( last_status == null ) {
            last_status = "";
        }
        if ( message.equals( last_status ) ) {
            return true;
        }
        last_status = message;
        return false;
    }

    /**
     * shows a status message
     * @param message  the status message
     */
    static public void status( String message ) {
        if ( message.equals( "" ) ) {
            message = default_status;
        }
        if ( message == null ) {
            message = "";
        }
        if (sameAsLastStatus(message)) {
            return;
        }
        StatusHandler statusHandler = ShowUser.statusHandler;
        if (statusHandler != null) {
            statusHandler.showStatus(message);
        } else {
            getShowUserHandler().showStatus(message);
        }
    }

    /**
     * shows the user an applet
     * @param applet  the applet to be shown
     */
    public static void setStatusHandler( StatusHandler statusHandler ) {
        ShowUser.statusHandler = statusHandler;
    }

    /**
     * shows the user an applet
     * @param applet  the applet to be shown
     */
    public void setShowUserApplet( Applet applet ) {
    }

    /**
     * shows user the outer frame
     * @param   the outer frame to be shown
     */
    public void setShowUserOuterFrame( Frame frame ) {
    }

    /**
     * shows user the base frame
     * @param   the base frame to be shown
     */
    public static void setBaseFrame( Frame frame ) {
        ShowUser.baseFrame = frame;
    }

    /**
     * getBaseFrame
     * @return Frame
     */
    public static Frame getBaseFrame() {
        return ShowUser.baseFrame;
    }

    private static Frame findBaseFrame( Component comp ) {
        if ( comp == null ) {
            return null;
        }
        Container last_parent = null;
        Container parent = comp.getParent();
        while ( parent != null ) {
            last_parent = parent;
            parent = parent.getParent();
        }
        return ((Frame)last_parent);
    }

    /**
     * gets an applet
     * @return  the applet
     */
    public static Applet getApplet() {
        return applet;
    }

    /**
     * setApplet
     * @param applet  the applet being set
     */
    public static void setApplet( Applet applet ) {
        ShowUser.applet = applet;
        ShowUser.baseFrame = findBaseFrame( applet );
        try {
            if (showUserHandler == null || showUserHandler instanceof ShowUser) {
                showUserHandler =
                    (ShowUserHandler)Class.forName("dtai.gui.ShowUserGui").newInstance();
            }
            showUserHandler.setShowUserApplet( applet );
        }
        catch ( Exception e ) {
            ShowUser.error(e,"Unable to load ShowUserGui");
        }
    }

    /**
     * suspend
     */
    public static void suspend() {
        showUserHandler.doSuspend();
    }

    /**
     * suspend
     */
    public static void resume() {
        showUserHandler.doResume();
    }

    /**
     * setOuterFrame
     * @param   the outer frame
     */
    public static void setOuterFrame( Frame outerframe ) {
        ShowUser.baseFrame = outerframe;
        if ( showUserHandler != null ) {
            showUserHandler.setShowUserOuterFrame( outerframe );
        }
    }

    /**
     * getDefaultApplet
     * @return Applet  the default applet
     */
    static public final Applet getDefaultApplet() {
        return applet;
    }

    /**
     * closeDialog
     */
    public static void closeDialog() {
/* WHY IS THIS HERE?
        System.out.println(ShowUser.getBaseFrame());
*/
    }

	/*************************
	 **
	 **
	 ** The following methods implement the ShowUserInterface for
	 ** non-GUI applications.  These functions are not to be called
	 ** directly.
	 **
	 *************************/

    /**
     * suspends showing error and warning (etc.) dialogs
     */
    public void doSuspend() {
    }

    /**
     * resumes showing error and warning (etc.) dialogs
     */
    public void doResume() {
    }

    /**
     * shows an error message
     * @param t - TBD
     * @param message - the error message
     * @param helpTopic - the help topic
     */
    public void showError( Throwable t, String message, String helpTopic ) {

        System.err.print("Error ["+new Date()+"]: "+message);
        if ( helpTopic.trim().equals("") ) {
            System.err.println();
        }
        else {
            System.err.println(". Help can be found at " + helpTopic  );
        }
		if ( t != null ) {
			t.printStackTrace();
		}
		else {
			new Exception().printStackTrace();
		}
    }

    /**
     * shows a warning message
     * @param message  the warning message
     */
    public void showWarning( String message ) {
        System.err.println("Warning ["+new Date()+"]: "+message);
    }

    /**
     * shows an info message
     * @param message  the info message
     */
    public void showInfo( String message ) {
        System.out.println( "Info: "+message );
    }

    /**
     * shows an about message
     * @param what  TBD
     * @param message  the about message
     */
    public void showAbout( String what, String message ) {
        System.out.println( "About "+what+": "+message );
    }

    /**
     * doHideError
     */
    public void doHideError() {
    }

    /**
     * doHideWarning
     */
    public void doHideWarning() {
    }

    /**
     * doHideInfo
     */
    public void doHideInfo() {
    }

    /**
     * doHideAbout
     */
    public void doHideAbout() {
    }

    /**
     * shows a please wait message
     */
    public void showPleaseWait() {
        waiting++;
        if ( waiting == 1 ) {
            System.out.print( "Please Wait..." );
            System.out.flush();
        }
    }

    /**
     * shows a finished waiting message
     */
    public void showFinishedWait() {
        if ( waiting > 0 ) {
            waiting--;
            if ( waiting == 0 ) {
                System.out.println( "done" );
            }
        }
    }

    /**
     * shows a status message
     * @param message  the status message
     */
    public void showStatus( String message ) {
        System.out.println( message );
    }

	/**
	 * printThrowable
	 * @param throwable
	 */
	public static void printThrowable(Throwable throwable) {
	}

	/****************************/
}
