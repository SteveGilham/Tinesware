/****************************************************************
 **
 **  $Id: AppletFrame.java,v 1.16 1998/02/21 21:17:33 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/AppletFrame.java,v $
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

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.TextField;
import java.awt.Window;
import java.util.Vector;
import java11.awt.event.AWTEvent;
import java11.awt.event.WindowEvent;
import java11.awt.event.WindowListener;


/**
 * AppletFrame - provides a Frame (window) in which to host an applet.
 *
 * @version	1.1
 * @author	DTAI, Incorporated
 */
public class AppletFrame extends Frame {

    private AppletWrapper appletWrapper;

    private TextField statusBar;

    private WindowListener windowListener;

	/**
	 * AppletFrame
	 *
	 * @param appletWrapper	wrapper object to pass applet
	 */
    public AppletFrame( AppletWrapper appletWrapper ) {
        this.appletWrapper = appletWrapper;
        appletWrapper.setFrame(this);
        setLayout( new BorderLayout() );
        add( "Center", appletWrapper.getApplet() );
    }

	/**
	 * AppletWrapper
	 * @return  AppletWrapper
	 */
    public AppletWrapper getAppletWrapper() {
        return appletWrapper;
    }

	/**
	 * Returns the status bar text field component.
	 *
	 * @return  the status bar
	 */
    public TextField getStatusBar() {
        if ( statusBar == null ) {
            statusBar = new TextField();
            statusBar.setEditable(false);
            add( "South", statusBar );
        }
        return statusBar;
    }

	/**
	 * Set the default size, which might be overridden by command line
	 * arguments or an HTML file.
	 *
	 * @param default_width     the default width of the window
	 * @param default_height    the default width of the window
	 */
    public void setDefaultSize( int default_width, int default_height ) {
        appletWrapper.setDefaultSize(default_width,default_height);
    }

	/**
	 * Set the default location, which might be overridden by command
	 * line arguments or an HTML file.
	 *
	 * @param default_x         the default x of the window
	 * @param default_y         the default y of the window
	 */
    public void setDefaultLocation( int default_x, int default_y ) {
        appletWrapper.setDefaultLocation(default_x,default_y);
    }

	/**
	 * hides the applet.
	 */
	public void hide() {
	    super.hide();
	    processEvent( new WindowEvent( this, new Event( this, -1, this ), WindowEvent.WINDOW_CLOSED ) );
	}

	/**
	 * shows the applet.
	 */
    public void show() {

        int initial_x = appletWrapper.getInitialX();
        int initial_y = appletWrapper.getInitialY();
        int initial_width = appletWrapper.getInitialWidth();
        int initial_height = appletWrapper.getInitialHeight();

        appletResize( initial_width, initial_height );

        super.show();
	    processEvent( new WindowEvent( this, new Event( this, -1, this ), WindowEvent.WINDOW_OPENED ) );

        if ( ( initial_x == -1 ) &&
             ( initial_y != -1 ) ) {
            initial_x = 0;
        }
        if ( ( initial_y == -1 ) &&
             ( initial_x != -1 ) ) {
            initial_y = 0;
        }
        if ( ( initial_x != -1 ) &&
             ( initial_y != -1 ) ) {
            move( initial_x, initial_y );
        }
    }

	/**
	 * Called when the applet wants to be resized.  This causes the
	 * Frame (window) to be resized to accomodate the new Applet size.
	 *
	 * @param width     the new width of the applet
	 * @param height    the new height of the applet
	 */
    public void appletResize( int width, int height ) {

        Insets insets = insets();
        int statusHeight = 0;

        if ( statusBar != null ) {
            statusHeight = statusBar.preferredSize().height;
        }

        resize( ( width + insets.left + insets.right ),
                ( height + statusHeight +
                  insets.top + insets.bottom ) );
    }

	/**
	 * Catch keyDown event and, if tab, go to next focus component
	 *
	 * @param evt   the Key Event
	 * @param key   the key value
	 * @return boolean result
	 */
    public boolean keyDown( Event evt, int key ) {
        if ( ( key == '\t' ) &&
             ( statusBar != null ) ) {
            /*
            if ( evt.shiftDown() ) {
                statusBar.prevFocus();
            }
            else {
            */
                statusBar.nextFocus();
            /*
            }
            */
        }
        return true;
    }

	/**
	 * Adds the specified listener to be notified when component
	 * events occur on this component.
	 *
	 * @param l 	the listener to receive the events
	 */
	public synchronized void addWindowListener(WindowListener l) {
        windowListener = GWTEventMulticaster.add(windowListener, l);
	}

	/**
	 * Removes the specified listener so it no longer receives
	 * window events on this window.
	 *
	 * @param l 		the listener to remove
	 */
	public synchronized void removeWindowListener(WindowListener l) {
        windowListener = GWTEventMulticaster.remove(windowListener, l);
	}

	/**
	 * handleEvent
	 * @param evt 		the Event to handle
	 * @return			boolean result of event handling
	 */
	public boolean handleEvent( Event evt ) {

        if ( evt.id == Event.WINDOW_DESTROY ) {
            processEvent( new WindowEvent( this, evt, WindowEvent.WINDOW_CLOSING ) );
            appletWrapper.destroy();
            return true;
        }
        if ( evt.id == Event.WINDOW_ICONIFY ) {
            processEvent( new WindowEvent( this, evt, WindowEvent.WINDOW_ICONIFIED ) );
            return true;
        }
        if ( evt.id == Event.WINDOW_DEICONIFY ) {
            processEvent( new WindowEvent( this, evt, WindowEvent.WINDOW_DEICONIFIED ) );
            return true;
        }
        return false;
	}

	/**
	 * processEvent
	 *
	 * @param e		a WindowEvent- we handle WINDOW_CLOSING
	 *										 WINDOW_CLOSED
	 *										 WINDOW_OPENED
	 *										 WINDOW_ICONIFIED
	 *										 WINDOW_DEICONIFIED
	 * @return boolean result
	 */
	protected void processEvent(AWTEvent e) {
		if (e instanceof WindowEvent) {
		    processWindowEvent((WindowEvent)e);
		}
	}

	protected void processWindowEvent(WindowEvent e) {
	    if (windowListener != null) {
    		switch( e.getID() ) {

    			case WindowEvent.WINDOW_CLOSING: {
    				windowListener.windowClosing(e);
    				break;
    			}
    			case WindowEvent.WINDOW_CLOSED: {
    				windowListener.windowClosed(e);
    				break;
    			}
    			case WindowEvent.WINDOW_OPENED: {
    				windowListener.windowOpened(e);
    				break;
    			}
    			case WindowEvent.WINDOW_ICONIFIED: {
    				windowListener.windowIconified(e);
    				break;
    			}
    			case WindowEvent.WINDOW_DEICONIFIED: {
    				windowListener.windowDeiconified(e);
    				break;
    			}
    			case WindowEvent.WINDOW_ACTIVATED: {
    				windowListener.windowActivated(e);
    				break;
    			}
    			case WindowEvent.WINDOW_DEACTIVATED: {
    				windowListener.windowDeactivated(e);
    				break;
    			}
    		}
		}
	}
}
