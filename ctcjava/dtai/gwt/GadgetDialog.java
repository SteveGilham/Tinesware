/****************************************************************
 **
 **  $Id: GadgetDialog.java,v 1.61 1998/02/28 17:44:41 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/GadgetDialog.java,v $
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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.Vector;
import java11.awt.event.KeyEvent;
import java11.awt.event.KeyListener;
import java11.awt.event.AWTEvent;
import java11.awt.event.WindowEvent;
import java11.awt.event.WindowListener;

/**
 * GadgetDialog
 *
 * Modal dialogs, by default, don't act like AWT modal dialogs.  This was
 * mainly an oversight, because so many early JVMs had real problems with
 * modal dialogs (e.g., the JVM would completely hang).  I didn't realize
 * what was intended at first.  AWT modal dialogs, by default, do not return
 * from the show() call until hide() is called from another thread.  In GWT,
 * the show() call returns immediately, so you have to process the logic
 * through events instead of slightly simpler mechanisms.  But GWT modal
 * dialogs should never hang the JVM.  (Although stupid Macintosh JVMs sometimes
 * do hang on GWT dialogs, which ticks me off.  Apple still makes a lousy
 * JVM.)  Anyway, by setting the "trueModal" flag in the constructor, you can
 * force GWT dialogs to act like AWT dialogs now.
 *
 * @version 1.1
 * @author DTAI, Incorporated
 */
public class GadgetDialog extends GadgetPanel implements KeyListener {

    public static final int CENTER = 0;
    public static final int UPPER = 1;
    public static final int LOWER = 2;
    public static final int LEFT = 3;
    public static final int RIGHT = 4;

    private int vertPosition;
    private int horizPosition;

    private WindowListener windowListener;

    private Rectangle oldBounds;
    private boolean modal;
    private DialogWrapper wrapper;
    private Frame frame;

    /**
     * GadgetDialog
     * @param parent - TBD
     * @param modal - TBD
     */
    public GadgetDialog( Frame parent, boolean modal ) {
        this( parent, null, modal, false );
    }

    /**
     * GadgetDialog
     * @param parent - TBD
     * @param modal - TBD
     * @param trueModal show() does not return to caller until hide() is called
     */
    public GadgetDialog( Frame parent, boolean modal, boolean trueModal ) {
        this( parent, null, modal, trueModal );
    }

    /**
     * GadgetDialog
     * @param parent - TBD
     * @param title - TBD
     * @param modal - TBD
     */
    public GadgetDialog( Frame frame, String title, boolean modal ) {
        this(frame, title, modal, false);
    }
    
    /**
     */
    /**
     * GadgetDialog constructor.
     *
     * "trueModal" IS DANGEROUS.  Many JVMs don't handle truly
     * modal dialogs without locking up.  GWT handles modal dialogs
     * by blocking input to windows below them, without adverse
     * reactions.  AWT modal dialogs block (on purpose) in the "show()"
     * call, but this does not always work.  USE WITH CAUTION only
     * if you know what JVM you are targeting!
     *
     * @param frame the parent frame
     * @param title the window title
     * @param modal if true, blocks input to all other windows
     * @param trueModal show() does not return to caller until hide() is called
     */
    public GadgetDialog( Frame frame, String title, boolean modal, boolean trueModal ) {
        super();
        setFrame(frame);
        this.modal = modal;

        Component comp[] = frame.getComponents();
        if (comp.length == 0) {
            setBackground(Color.lightGray );
        } else {
            setBackground(comp[0].getBackground());
        }

        setInitialPosition(CENTER,CENTER);

        wrapper = new DialogWrapper(frame,title,trueModal,this);

        // set help if we can
        Component[] comps = frame.getComponents();
        for (int i = 0; i < comps.length; i++) {
            if (comps[i] instanceof GadgetShell) {
                GadgetShell parentShell = (GadgetShell)comps[i];
                getGadget().setGadgetHelp(parentShell.getGadget().getGadgetHelp());
                break;
            } else if (comps[i] instanceof Container ) {
                Component[] subcomps = ((Container)comps[i]).getComponents();
                if (subcomps.length > 0 && subcomps[0] instanceof GadgetShell) {
                    GadgetShell parentShell = (GadgetShell)subcomps[0];
                    getGadget().setGadgetHelp(parentShell.getGadget().getGadgetHelp());
                    break;
                }
            }
        }

        getMainPanel().addKeyListener(this);
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.ESCAPE) {
            this.processEvent( new WindowEvent( this, e.getEvent(), WindowEvent.WINDOW_CLOSING ) );
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void setFrame(Frame frame) {
        if (frame != this.frame) {
            this.frame = frame;
            if (isVisible() && wrapper != null) {
                hide();
                show();
            }
        }
    }

    public void show() {
        if (frame != wrapper.getParent()) {
            DialogWrapper oldWrapper = wrapper;
            oldBounds = wrapper.bounds();
            wrapper = new DialogWrapper(frame,wrapper.getTitle(),false,this);
            oldWrapper.dispose();
        }
        if (modal) {
            GadgetShell.addModal(this);
        }
        wrapper.show();
    }

    public void hide() {
        wrapper.hide();
        if (modal) {
            GadgetShell.removeModal(this);
        }
    }

    /**
     * sets the initial horizontal and vertical position
     * @param vertPosition - TBD
     * @param horizPosition - TBD
     */
    public void setInitialPosition(int vertPosition, int horizPosition) {
        this.vertPosition = vertPosition;
        this.horizPosition = horizPosition;
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

		    int id = e.getID();
		    if (id == WindowEvent.WINDOW_OPENED ||
		        id == WindowEvent.WINDOW_DEICONIFIED) {
				notifyWindowOpened();
		    }
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

    /**
     * positionDialog
     */
    protected void positionDialog(Frame frame, DialogWrapper dialog) {
        if (oldBounds != null) {
            dialog.reshape(oldBounds.x,oldBounds.y,oldBounds.width,oldBounds.height);
            return;
        }

        dialog.pack();

        Point position = frame.location();
        Dimension frame_size = frame.size();
        Dimension dlg_size = dialog.size();

        if (position.x > frame_size.width) { // have to overcome Communicator bug...
            position.x = 0;
        }

        if ( horizPosition == LEFT ) {
            // good place
        }
        else if ( horizPosition == RIGHT ) {
            position.x += ( frame_size.width - dlg_size.width );
        }
        else {
            position.x += ( frame_size.width - dlg_size.width ) / 2;
        }

        if ( vertPosition == UPPER ) {
            // good place
        }
        else if ( vertPosition == LOWER ) {
            position.y += ( frame_size.height - dlg_size.height );
        }
        else {
            position.y += ( frame_size.height - dlg_size.height ) / 2;
        }
        dialog.move( Math.max( 0, position.x ), Math.max( 0, position.y ) );
    }

    public void pack() {
        wrapper.pack();
    }

    public void dispose() {
        wrapper.dispose();
    }

    public void toFront() {
        wrapper.toFront();
    }

    public void toBack() {
        wrapper.toBack();
    }

    public final String getWarningString() {
    	return wrapper.getWarningString();
    }

    public boolean isModal() {
    	return modal;
    }

    public String getTitle() {
    	return wrapper.getTitle();
    }

    public void setTitle(String title) {
        wrapper.setTitle(title);
    }

    public boolean isResizable() {
    	return wrapper.isResizable();
    }

    public void setResizable(boolean resizable) {
        wrapper.setResizable(resizable);
    }
}

class DialogWrapper extends Dialog {

    private Frame frame;
    private boolean badMac = false;
    private boolean wasRepositioned = false;
    GadgetDialog shell;

    public DialogWrapper(Frame frame, String title, boolean trueModal, GadgetDialog shell) {
        super(frame,title,trueModal);
        setBackground(shell.getBackground());
        this.frame = frame;
        this.shell = shell;
        setupEventGrabberKludge(shell);
        add( "Center", shell );
        hide();
    }

    private void setupEventGrabberKludge( GadgetShell shell ) {
        String osName = "";
        double version = 0.0;
        double mrjVersion = 0.0;
        try {
            osName = System.getProperty("os.name");
        }
        catch ( SecurityException se ) {
        }
        if (osName.startsWith("Mac")) {
            try {
                String vs = System.getProperty("java.version").trim();
                int dot = vs.indexOf('.');
                if (dot >= 0) {
                    dot = vs.indexOf('.',dot+1);
                }
                if (dot >= 0) {
                    vs = vs.substring(0,dot);
                }
                version = Double.valueOf(vs).doubleValue();
            }
            catch ( SecurityException se ) {
            }
            catch ( NumberFormatException nfe ) {
            }
            try {
                String vs = System.getProperty("mrj.version");
    			if (vs == null)
    				vs = "";
                vs = vs.trim();
                int dot = vs.indexOf('.');
                if (dot >= 0) {
                    dot = vs.indexOf('.',dot+1);
                }
                if (dot >= 0) {
                    vs = vs.substring(0,dot);
                }
                version = Double.valueOf(vs).doubleValue();
            }
            catch ( SecurityException se ) {
            }
            catch ( NumberFormatException nfe ) {
            }
            if ((version < 1.1) && (mrjVersion < 1.5)) {
                badMac = true;
            }
        }
	}

	/**
     * handleEvent
     * @param evt - TBD
     * @return boolean result
     */
	public boolean handleEvent( Event evt ) {
        if ( evt.id == Event.WINDOW_DESTROY ) {
            shell.processEvent( new WindowEvent( shell, evt, WindowEvent.WINDOW_CLOSING ) );
            return true;
        }
        else if ( evt.id == Event.WINDOW_ICONIFY ) {
            shell.processEvent( new WindowEvent( shell, evt, WindowEvent.WINDOW_ICONIFIED ) );
            return true;
        }
        else if ( evt.id == Event.WINDOW_DEICONIFY ) {
            shell.processEvent( new WindowEvent( shell, evt, WindowEvent.WINDOW_DEICONIFIED ) );
            return true;
        }
        else if ( evt.id == Event.WINDOW_EXPOSE ) {
            return false;
        }
        else if ( evt.id == Event.WINDOW_MOVED ) {
            return false;
        }
        else if ( badMac ) {
            if ( evt.id == Event.KEY_RELEASE ) {
                evt.id = Event.KEY_PRESS;
                shell.handleEvent( evt );
                evt.id = Event.KEY_RELEASE;
                shell.handleEvent( evt );
            }
            else if ( evt.id == Event.KEY_ACTION_RELEASE ) {
                evt.id = Event.KEY_ACTION;
                shell.handleEvent( evt );
                evt.id = Event.KEY_ACTION_RELEASE;
                shell.handleEvent( evt );
            }
            else if ( ( evt.id == Event.GOT_FOCUS ) ||
                      ( evt.id == Event.LOST_FOCUS ) ) {
                shell.handleEvent( evt );
            }
        }
        return false;
	}

	/**
	 * hide
	 */
	public void hide() {
	    super.hide();
	    Event e = new Event( shell, -1, shell );
	    WindowEvent we = new WindowEvent( shell, e, WindowEvent.WINDOW_CLOSED );
	    shell.processEvent( we );
	    if (frame != null) {
            frame.requestFocus();
        }
	}

	/**
	 * show
	 */
	public void show() {
		/* There's a lot of redundant packing and positioning here,
		 * and in the thread, if this is the first time...(wasRepositioned
		 * is false).  But that's because some platforms don't handle
		 * it right the first time.
		 */
	    if ( ! wasRepositioned ) {
			positionDialog();
		}
		if (super.isModal()) {
    	    shell.processEvent( new WindowEvent( shell, new Event( shell, -1, shell ),
    	                                         WindowEvent.WINDOW_OPENED ) );
    	    super.show();
		} else {
    	    super.show();
    	    shell.processEvent( new WindowEvent( shell, new Event( shell, -1, shell ),
    	                                         WindowEvent.WINDOW_OPENED ) );
    	    if ( ! wasRepositioned ) {
    	        positionDialog();
    	        shell.forceFocus();
                shell.resetFocus();
                shell.requestFocus();
    			new PackThread( this ).run();
    	        wasRepositioned = true;
    	    }
    	    else {
        	    shell.resetFocus();
        	    shell.requestFocus();
        	}
        }
	}

	void positionDialog() {
	    shell.positionDialog(frame, this);
	}
}

/**
 * PackThread
 * @version 1.1
 * @author   DTAI, Incorporated
 */
class PackThread extends Thread {

    DialogWrapper dialog;

    /**
     * PackThread
     * @param dialog - TBD
     */
    public PackThread( DialogWrapper dialog ) {
		super("dtai.gwt.PackThread");
        this.dialog = dialog;
    }

    /**
     * run
     */
    public void run() {
        try {
            Graphics test;
            while ( (test = dialog.getGraphics()) == null ) {
                sleep( 50 );
            }
            test.dispose();
            dialog.positionDialog();
            sleep( 50 );
            dialog.shell.forceFocus();
            dialog.shell.resetFocus();
            dialog.shell.requestFocus();
            sleep( 300 );
            dialog.positionDialog();
            dialog.shell.getGadget().repaint();
            sleep( 300 );
            dialog.shell.getGadget().repaint();
        }
        catch ( InterruptedException ie ) {
        }
    }
}
