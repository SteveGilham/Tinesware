/****************************************************************
 **
 **  $Id: GadgetShell.java,v 1.109 1998/03/10 20:30:41 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/GadgetShell.java,v $
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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.net.URL;
import java.util.Vector;
import java11.awt.event.AWTEvent;
import java11.awt.event.InputEvent;
import java11.awt.event.FocusEvent;
import java11.awt.event.KeyEvent;
import java11.awt.event.MouseEvent;
import dtai.util.Debug;
import java11.util.EventObject;

/**
 * GadgetShell
 * @version 1.1
 * @author DTAI, Incorporated
 */
public class GadgetShell extends Panel {

    private static GadgetShell focusShell;

    private static Vector modals = new Vector();
	private ShellPaintListener shellPaintListener;

    private Gadget gadget;
    /*
    private Vector damageList;
    */
    private Rectangle damageRect;
    private Image image;
    private Dimension imageSize;
    private OverlayPanelGadget overlayPanel;
    private boolean checkAllValid = true;

    private static byte TIMEOUT_EVENT = 1;
    private static byte INTERRUPT_EVENT = 2;
    private GadgetTimer timer;
    private boolean stopping = false;

    private Vector subgadgets = new Vector();
    private Gadget lastFocusOwner;
    private Gadget focusOwner;
    private Gadget mouseOwner;
    private Gadget helpOwner;
    private Gadget tipOwner;
    private Gadget tipMouseOwner;
    private Gadget defaultGadget;
    private boolean showedTipRecently = false;
    private TipGadget tipGadget;
    private boolean showTips = true;

    private boolean haveFocus = false;
    private boolean focusAllowedToEscape = false;
    private boolean mouseDown = false;
    private boolean metaMouse = false;

    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private int lastDownX = 0;
    private int lastDownY = 0;
    private Event lastMouseDown;
    private int clickCount;
    private long windowOpened = 0;
    private static long lastEventTime = 0;
    
    private Gadget defaultFocusGadget;

    private boolean waitingForMouseUp = false;

    private GadgetCursor cursor = GadgetCursor.getDefaultCursor();
    private GadgetCursor overrideCursor;

    private Graphics graphicsOverride;
    private int widthOverride;
    private int heightOverride;

    /**
     * GadgetShell
     */
    public GadgetShell() {
        setLayout( null );
        setFont(Gadget.defaultFont);
    }

    /**
     * getGadget
     * @return Gadget
     */
    public Gadget getGadget() {
        return gadget;
    }

    public void setWaitingForMouseUp(boolean waitingForMouseUp) {
        this.waitingForMouseUp = waitingForMouseUp;
    }

    public static void addModal(GadgetShell modal) {
        modals.removeElement(modal);
        modals.addElement(modal);
    }

    public static void removeModal(GadgetShell modal) {
        modals.removeElement(modal);
    }

    /**
     * setDefaultGadget
     * @param gadget - TBD
     */
    public void setDefaultGadget(Gadget gadget) {
        if (gadget != defaultGadget) {
            if (defaultGadget != null) {
                defaultGadget.repaint();
            }
            defaultGadget = gadget;
            if (defaultGadget != null) {
                defaultGadget.repaint();
            }
        }
    }

    /**
     * getDefaultGadget
     * @return Gadget
     */
    public Gadget getDefaultGadget() {
        if ( focusOwner instanceof ButtonGadget &&
             ((ButtonGadget)focusOwner).getDefaultThickness() > 0 ) {
            return focusOwner;
        }
        return getNormalDefaultGadget();
    }

    /**
     * setDefaultGadget
     * @param gadget - TBD
     */
    public void setDefaultFocusGadget(Gadget gadget) {
        defaultFocusGadget = gadget; // this may not be working!
    }

    /**
     * getDefaultGadget
     * @return Gadget
     */
    public Gadget getDefaultFocusGadget() {
        return defaultFocusGadget; // this may not be working!
    }

    /**
     * getNormalDefaultGadget()
     * @return Gadget
     */
    public Gadget getNormalDefaultGadget() {
        return defaultGadget;
    }

    /**
     * invokeDefault
     * @param e - TBD
     */
    public void invokeDefault(AWTEvent e) {
        Gadget defaultGadget = getDefaultGadget();
        if ( defaultGadget != null ) {
            defaultGadget.doDefaultAction(e);
        }
	}

    private synchronized void startTimer() {
        stopping = false;
        if ( timer == null ) {
            timer = new GadgetTimer( this, TIMEOUT_EVENT, INTERRUPT_EVENT, 150, 600 );
        }
    }

    private synchronized void interruptTimer() {
        if ( timer != null ) {
            timer.interrupt();
        }
    }

    private void stopTimer() {
        if ( timer != null ) {
            timer.stopThread();
            timer = null;
        }
    }

    /**
     * processTimerEvent
     * @param type - TBD
     */
    protected void processTimerEvent( byte type ) {
        handleTip(type);
    }

    /**
     * getGadgetTreeLock
     * @return Object
     */
    public final Object getGadgetTreeLock() {
    	return Lock.getTreeLock();
	}

    private void handleTip( byte type ) {
        synchronized(getGadgetTreeLock()) {
            if ( type == TIMEOUT_EVENT ) {
                showedTipRecently = false;
            }
            if ( ( ( tipOwner != null ) ||
                   ( type == TIMEOUT_EVENT ) ||
                   ( showedTipRecently ) ) &&
                 ( ! mouseDown ) &&
                 ( mouseOwner != tipMouseOwner ) ) {
                synchronized(getGadgetTreeLock()) {//this used to be "this"!
                    Gadget newTipOwner = findTipOwner();
                    if ( newTipOwner != tipOwner ) {
                        tipOwner = newTipOwner;
                        tipMouseOwner = mouseOwner;
                        OverlayPanelGadget overlays = getOverlayPanel();
                        if ( ( overlays != null ) && (showTips) ) {
                            if ( tipGadget != null ) {
                                if (tipGadget != null) {
                                    overlays.remove( tipGadget );
                                }
                                tipGadget = null;
                                showedTipRecently = true;
                            }
                            if ( tipOwner != null ) {
                                String tip = tipOwner.getTip();
                                if (tip != null) {
                                    tipGadget = new TipGadget( tipOwner.getTip() );
                                }
                                tipGadget.setLocation( lastMouseX, lastMouseY + 20 );
                                overlays.add( tipGadget , -1 );
                            }
                        }
                    }
                }
            }
            if ( type == TIMEOUT_EVENT && tipGadget == null ) {
                stopTimer();
            }
        }
    }

    /**
     * fakeMouseUp
     */
    protected void fakeMouseUp() {
        handleEvent(new Event(
            this, lastEventTime, Event.MOUSE_UP,
            lastDownX, lastDownY, 0, 0, this));
    }

    /**
     * fakeMouseDown
     * @param x - TBD
     * @param y - TBD
     * @param when - TBD
     */
    private void fakeMouseDown(int x, int y, long when) {
        handleEvent(new Event(
            this, when, Event.MOUSE_DOWN,
            x, y, 0, 0, this));
    }

    /**
     * fakeMouseExit
     * @param when - TBD
     */
    private void fakeMouseExit(long when) {
        handleEvent(new Event(
            this, when, Event.MOUSE_EXIT,
            0, 0, 0, 0, this));
    }

    /**
     * handleEvent
     * @param evt - TBD
     * @return boolean result
     */
    public boolean handleEvent( Event evt ) {
        long startpaint = 0;
        lastEventTime = evt.when;
        if( Math.abs(evt.when - windowOpened) < 300 ) {
            return true;
        }

        if (modals.size() > 0 && modals.lastElement() != this &&
            (!mouseDown || evt.id != Event.MOUSE_UP) &&
            (mouseOwner == null || evt.id != Event.MOUSE_EXIT)) {
            if (mouseDown) {
                fakeMouseUp();
            }
            if (mouseOwner != null) {
                fakeMouseExit(evt.when);
            }
            if (evt.id == Event.MOUSE_DOWN) {
                GadgetShell modal = (GadgetShell)modals.lastElement();
                if (modal != null) {
                    Dialog dialog = modal.getDialog();
                    if (dialog != null) {
                        dialog.hide();
                        dialog.show();
                    } else {
                        Frame frame = modal.getFrame();
                        if (frame != null) {
                            frame.hide();
                            frame.show();
                        }
                    }
                }
            }
            return true;
        }

        AWTEvent evtobj = null;
        Gadget target = null;

        if ( gadget.enabled && gadget.visible ) {
            target = gadget;
        }

        if (!mouseDown) {
            if (evt.id == Event.MOUSE_DRAG) {
                evt.id = Event.MOUSE_MOVE;
            } else if (evt.id == Event.MOUSE_UP) {
                if (waitingForMouseUp) {
                    fakeMouseDown(evt.x, evt.y, evt.when);
                }
                //return true; // instead of faking mouse down...why was I doing this before?
                // for the menus when you drag to a menu item...but why did I want to take it
                // out?  Was it because of the dialog windows getting clicks?
            }
        }

		switch ( evt.id ) {

			case Event.KEY_ACTION: {
			    if ( ! haveFocus ) {
    			    haveFocus = true;
    			    if ( focusOwner == null ) {
    			        regainFocus();
    		        }
    		    }
		        if ( focusOwner != null &&
		             focusOwner.isFunctioning() &&
		             focusOwner.isShowing() ) {
    			    target = focusOwner;
    				evtobj = new KeyEvent( evt, KeyEvent.KEY_PRESSED, true );
    			}
				break;
			}

			case Event.KEY_ACTION_RELEASE: {
			    if ( ! haveFocus ) {
    			    haveFocus = true;
    			    if ( focusOwner == null ) {
    			        regainFocus();
    		        }
    		    }
		        if ( focusOwner != null &&
		             focusOwner.isFunctioning() &&
		             focusOwner.isShowing() ) {
    			    target = focusOwner;
				    evtobj = new KeyEvent( evt, KeyEvent.KEY_RELEASED, true );
    			}
				break;
			}

			case Event.KEY_PRESS: {
			    if ( ! haveFocus ) {
    			    haveFocus = true;
    			    if ( focusOwner == null ) {
    			        regainFocus();
    		        }
    		    }
		        if ( focusOwner != null &&
		             focusOwner.isFunctioning() &&
		             focusOwner.isShowing() ) {
    			    target = focusOwner;
				    evtobj = new KeyEvent( evt, KeyEvent.KEY_PRESSED, false );
    			}
				break;
			}

			case Event.KEY_RELEASE: {
			    if ( ! haveFocus ) {
    			    haveFocus = true;
    			    if ( focusOwner == null ) {
    			        regainFocus();
    		        }
    		    }
		        if ( focusOwner != null &&
		             focusOwner.isFunctioning() &&
		             focusOwner.isShowing() ) {
    			    target = focusOwner;
    				evtobj = new KeyEvent( evt, KeyEvent.KEY_RELEASED, false );
    			}
				break;
			}

			case Event.GOT_FOCUS: {
			    haveFocus = true;
			    if ( focusOwner == null ) {
			        regainFocus();
		        }
		        if ( focusOwner != null &&
		             focusOwner.isFunctioning() &&
		             focusOwner.isShowing() ) {
    			    target = focusOwner;
    				evtobj = new FocusEvent( evt, FocusEvent.FOCUS_GAINED );
    			}
				break;
			}

			case Event.LOST_FOCUS: {
			    overlayPanel.popdownOverlay();
			    haveFocus = false;
			    target = focusOwner;
			    setFocusOwner((Gadget)null);
			    helpOwner = mouseOwner;
				evtobj = new FocusEvent( evt, FocusEvent.FOCUS_LOST );
				break;
			}

			case Event.MOUSE_DOWN: {
                metaMouse = ( ( evt.modifiers & Event.META_MASK ) != 0 );

			    if ( ( imageSize == null ) ||
			         ( evt.x < 0 ) ||
			         ( evt.y < 0 ) ||
			         ( evt.x >= imageSize.width ) ||
			         ( evt.y >= imageSize.height ) ) {
		            return true;
		        }
			    mouseDown = true;
                if ( ( lastMouseDown != null ) &&
                     ( ( evt.when - lastMouseDown.when ) < 500 ) && // 1/2 second
                     ( evt.modifiers == lastMouseDown.modifiers ) &&
                     ( Math.abs( evt.x - lastDownX ) < 4 ) &&
                     ( Math.abs( evt.y - lastDownY ) < 4 ) ) {
    			    if ( ( clickCount > 1 ) &&
    			         ( lastMouseDown.when == evt.when ) ) {
    			        return true;
    			    }
                    clickCount++;
                    if (clickCount == 4) {
                        clickCount = 2;
                    }
                }
                else {
                    clickCount = 1;
                }
		        /*  If these two lines are not there, I don't get
		         * focus in text fields when I click, but I don't
		         * understand why I have commented this out before. */
	            requestFocus();
			    haveFocus = true;
			    /* */
                evt.clickCount = clickCount;
                lastMouseDown = evt;
                lastDownX = evt.x;
                lastDownY = evt.y;
				evtobj = new MouseEvent( evt, MouseEvent.MOUSE_PRESSED );
				break;
			}

			case Event.MOUSE_DRAG: {
			    if ( ( imageSize == null ) ||
			         ( evt.x == -1 ) ||
			         ( evt.y == -1 ) ) {
		            return true;
		        }
				evtobj = new MouseEvent( evt, MouseEvent.MOUSE_DRAGGED );
                lastMouseX = evt.x;
                lastMouseY = evt.y;
				break;
			}

			case Event.MOUSE_ENTER: {
			    if ( ( imageSize == null ) ||
			         ( evt.x < 0 ) ||
			         ( evt.y < 0 ) ||
			         ( evt.x >= imageSize.width ) ||
			         ( evt.y >= imageSize.height ) ) {
		            return true;
		        }
				evtobj = new MouseEvent( evt, MouseEvent.MOUSE_ENTERED );
                Gadget newMouseOwner = getSubgadgetAt( evt.x, evt.y );
				changeMouseOwner( newMouseOwner, evt );
				break;
			}

			case Event.MOUSE_EXIT: {
			    if ( imageSize == null ) {
		            return true;
		        }
				evtobj = new MouseEvent( evt, MouseEvent.MOUSE_EXITED );
				changeMouseOwner( null, evt );
			    interruptTimer();
				break;
			}

			case Event.MOUSE_MOVE: {
			    if ( ( imageSize == null ) ||
			         ( evt.x < 0 ) ||
			         ( evt.y < 0 ) ||
			         ( evt.x >= imageSize.width ) ||
			         ( evt.y >= imageSize.height ) ) {
		            return true;
		        }
				evtobj = new MouseEvent( evt, MouseEvent.MOUSE_MOVED );
                Gadget newMouseOwner = getSubgadgetAt( evt.x, evt.y );
				if ( newMouseOwner != mouseOwner ) {
    				changeMouseOwner( newMouseOwner, evt );
    			}
                lastMouseX = evt.x;
                lastMouseY = evt.y;
			    interruptTimer();
				break;
			}

			case Event.MOUSE_UP: {
			    if ( mouseDown ) {
    				if (metaMouse) {
    				   evt.modifiers = evt.modifiers | Event.META_MASK;
    				}
    			    if ( ( imageSize == null ) ||
    			         ( evt.x == -1 ) ||
    			         ( evt.y == -1 ) ) {
    		            return true;
    		        }
    			    mouseDown = false;
    				evtobj = new MouseEvent( evt, MouseEvent.MOUSE_RELEASED );
    			}
    			else {
    			    target = null; // throw away event
    			}

				break;
			}

			case Event.LIST_DESELECT:
			case Event.LIST_SELECT:
			case Event.LOAD_FILE:
			case Event.SAVE_FILE:
			case Event.SCROLL_ABSOLUTE:
			case Event.SCROLL_LINE_DOWN:
			case Event.SCROLL_LINE_UP:
			case Event.SCROLL_PAGE_DOWN:
			case Event.SCROLL_PAGE_UP:
			case Event.WINDOW_DEICONIFY:
			case Event.WINDOW_DESTROY:
			case Event.WINDOW_EXPOSE:
			case Event.WINDOW_ICONIFY:
			case Event.WINDOW_MOVED:
			default: {
			    // Unexpected event type, but may have been
			    // passed up from a nested AWT
			}
		}

		if ( ( tipGadget != null ) &&
		     ( ( ! ( evtobj instanceof MouseEvent ) ) ||
    		   ( evt.id != Event.MOUSE_MOVE ) ) ) {
    		tipGadget.setVisible( false );
		}

		if ( ( evtobj != null ) &&
		     ( target != null ) ) {
    		int evtid = evtobj.getID();
	        evtobj.setSource( target );
			target.processEvent( evtobj );

			// Pass the keyboard events up the parent chain, if not handled
		    target = target.getParent();
		    while ( ( ! evtobj.isConsumed() ) && ( target != null ) ) {
				target.processEvent( evtobj );
                target = target.getParent();
		    }
/* THIS SEEMS TO HAPPEN AFTER A DIALOG POPS UP...SHOULD NOT HAVE FOCUS AT THAT POINT
		    if (evtid == MouseEvent.MOUSE_PRESSED && focusOwner == null) {
		        regainFocus();
		    }
*/
            if ( ! evtobj.isConsumed() ) {
                if ( evtobj.getID() == KeyEvent.KEY_PRESSED ) {
				    KeyEvent key = (KeyEvent)evtobj;
			        if ( key.getKeyCode() == KeyEvent.ENTER ) {
			            Gadget defaultGadget = getDefaultGadget();
			            if ( defaultGadget != null ) {
			                defaultGadget.doDefaultAction(key);
			                ((InputEvent)evtobj).consume();
			            }
			        }
				    else if ( key.getKeyCode() == KeyEvent.TAB ) {
            			if ( key.isShiftDown() ) {
                			gadget.prevFocus();
                		}
                		else {
                			gadget.nextFocus();
                		}
		                ((InputEvent)evtobj).consume();
			        }
				    else if ( key.isActionKey() ) {
				        if ( key.getKeyCode() == KeyEvent.F1 ) {
    				        Gadget g = helpOwner;
    				        boolean handled = false;
    				        while ( g != null && ! handled) {
        				        GadgetHelp help = g.getGadgetHelp();
        				        if ( ( help != null ) &&
        				             ( g.getHelpId() != null ) ) {
        				            help.showHelp( g );
        				            handled = true;
        				        }
        				        g = g.getParent();
        				    }
        				    if (handled) {
        		                ((InputEvent)evtobj).consume();
        		            }
        				}
				    }
				}
            }
            return evtobj.isConsumed();
    	}
    	return false;
    }

    private void regainFocus() {
        if ( lastFocusOwner != null ) {
            giveFocusTo( lastFocusOwner );
        }
        else {
            focusForward(false);
            if (focusOwner instanceof dtai.gwt.MenuGadget &&
                focusOwner.parent != null &&
                focusOwner.parent instanceof MenuBarGadget) {
                focusForward(false);
            }
        }
    }

    private Gadget findTipOwner() {
        Gadget g = mouseOwner;
        while ( g != null ) {
            if ( g.getTip() != null ) {
                break;
            }
            g = g.getParent();
        }
        return g;
    }

    /**
     * overlayPanel
     */
    public final void setOverlayPanel( OverlayPanelGadget overlayPanel ) {
        this.overlayPanel = overlayPanel;
    }

    /**
     * getOverlayPanel
     * @return OverlayPanelGadget
     */
    public final OverlayPanelGadget getOverlayPanel() {
        return overlayPanel;
    }

    /**
     * add
     * @param gadget - TBD
     * @return Gadget
     */
    public Gadget add( Gadget gadget ) {
        if ( this.gadget != null ) {
            this.gadget.setShell( null );
        }
        this.gadget = gadget;
        if ( gadget != null ) {
            gadget.setShell( this );
        }
        return gadget;
    }

    /**
     * addSubGadget
     * @param gadget - TBD
     */
    public final void addSubGadget( Gadget gadget ) {
        if ( gadget.isFocusTraversable() ) {
            subgadgets.addElement( gadget );
        }
    }

    /**
     * removeSubGadget
     * @param gadget - TBD
     */
    public final void removeSubGadget( Gadget gadget ) {
        subgadgets.removeElement( gadget );
    }

    /**
     * minimumSize
     * @return Dimension
     */
    public Dimension minimumSize() {
        if ( gadget != null ) {
            return gadget.getMinimumSize();
        }
        else {
            return new Dimension( 1, 1 );
        }
    }


    /**
     * prefferedSize
     * @return Dimension
     */
    public Dimension preferredSize() {
        if ( gadget != null ) {
            return gadget.getPreferredSize();
        }
        else {
            return new Dimension( 1, 1 );
        }
    }

    /**
     * getSubgadgetAt
     * @param x - TBD
     * @param y - TBD
     * @return Gadget
     */
    public final Gadget getSubgadgetAt( int x, int y ) {
        int x_offset = 0;
        int y_offset = 0;
        Gadget g = gadget;
        while ( g != null ) {
			Gadget subgadget = null;
			subgadget = g.getGadgetAt( x + x_offset, y + y_offset );
            if ( ( subgadget == g ) ||
                 ( subgadget == null ) ) {
                break;
            }
            g = subgadget;
            x_offset -= g.x;
            y_offset -= g.y;
        }
        return g;
    }

    /**
     * clearDamaged
     * @return Rectangle
     */
    public Rectangle clearDamaged() {
        synchronized(getGadgetTreeLock()) {
            /*
            Vector toPaint = damageList;
            damageList = null;
            return toPaint;
            */
            Rectangle toPaint = damageRect;
            damageRect = null;
            return toPaint;
        }
    }

    /**
     * reshape
     * @param x - TBD
     * @param y - TBD
     * @param width - TBD
     * @param height - TBD
     */
    public void reshape(int x, int y, int width, int height) {
        Dimension dim = size();
        super.reshape(x,y,width,height);
        if ( gadget != null ) {
            if ( width > 0 && height > 0 ) {
                gadget.setBounds(0,0,width,height);
                if ( width > dim.width ) {
                    if ( height > dim.height ) {
                        addDamagedArea(0,0,width,height);
                    }
                    else {
                        addDamagedArea(0,dim.width,width-dim.width,height);
                    }
                }
                else if ( height > dim.height ) {
                    addDamagedArea(dim.height,0,width,height-dim.height);
                }
            }
        }
    }

	/**
	 * addShellPaintListener         Adds the listener through the multiCaster
	 *
	 * @param l   The ShellPaintListener
	 */
	public synchronized void addShellPaintListener(ShellPaintListener l) {
		shellPaintListener = ShellPaintMultiCaster.add(shellPaintListener, l);
	}

	/**
	 * removeShellPaintListener      Removes the listener through the multiCaster
	 *
	 * @param l   The ShellPaintListener
	 */
	public synchronized void removeShellPaintListener(ShellPaintListener l) {
		shellPaintListener = ShellPaintMultiCaster.remove(shellPaintListener, l);
	}

	/**
	 * processEvent      Processes the generic event
	 *
	 * @param e   The event object
	 */
	public void processEvent(EventObject e) {
		if (e instanceof ShellPaintEvent) {
			processShellPaintEvent((ShellPaintEvent)e);
		}/* else {
			super.processEvent(e);  // this line may not apply
		}*/
	}

	/**
	 * processShellPaintEvent        Processes the specific event
	 *
	 * @param e   The ShellPaintEvent
	 */
	public void processShellPaintEvent(ShellPaintEvent e) {
		ShellPaintListener shellPaintListener = this.shellPaintListener;
		if (shellPaintListener != null) {
			switch(e.getID()) {
				case ShellPaintEvent.REGION_UPDATED:
					shellPaintListener.regionUpdated(e);
					break;
			}
		}
	}

	public void validateAll() {
	    synchronized(getGadgetTreeLock()) {
        	Gadget mainGadget = gadget;
        	if (mainGadget != null) {
                while ( checkAllValid ) {
                    checkAllValid = false;
                    long startTime = 0;
                    long startMemory = 0;
                    if (Debug.getLevel() == 100) {
                        startTime = System.currentTimeMillis();
                        startMemory = Runtime.getRuntime().freeMemory();
                    }
                    mainGadget.validate();
                    if (startTime > 0) {
                        System.err.print("validating took "+
                            (System.currentTimeMillis()-startTime)+
                            " milliseconds");
                        long consumed = startMemory - Runtime.getRuntime().freeMemory();
                        if (consumed < 0) { // gc was performed
                            System.err.println();
                        } else {
                            System.err.println(" and probably consumed "+consumed+" bytes");
                        }
                    }
                }
            }
        }
    }

    /**
     * paint
     * @param g - TBD
     */
    public void paint( Graphics g ) {
        synchronized(getGadgetTreeLock()) {
            // I have to do this to fix Netscape 3.01 on Win32
            Rectangle origClip = g.getClipRect();
            g.dispose();

        	Gadget mainGadget = gadget;
            if ( mainGadget == null ||
                 ( ! isShowing() ) ||
                 ( ! mainGadget.isVisible() ) ||
                 ( mainGadget.isFrozen() ) ) {
                return;
            }
            validateAll();

            GadgetGraphics gadget_g = mainGadget.getGadgetGraphics();

            if ( gadget_g != null ) {

                Rectangle clip = clearDamaged();
                if (clip != null) {
                /*
                Vector curDamageList = clearDamaged();
                int size = 0;
                Rectangle clip = null;
                if (curDamageList != null) {
                    int numrects = curDamageList.size();
                    for (int i = 0; i < numrects; i++) {
                        Rectangle damaged = (Rectangle)curDamageList.elementAt(i);
                        if (clip == null) {
                            clip = new Rectangle(damaged.x,damaged.y,damaged.width,damaged.height);
                        } else {
                            clip = clip.union(damaged);
                        }
                    }
                    if ( clip != null ) {
                    */
                    // I have to do this union to fix Netscape 4.01!
                    gadget_g.clipRect( clip.x, clip.y, clip.width, clip.height );
                    //gadget_g.setDamageList(curDamageList);
                    //synchronized(getGadgetTreeLock()) {
                        long startTime = 0;
                        long startMemory = 0;
                        if (Debug.getLevel() == 100) {
                            startTime = System.currentTimeMillis();
                            startMemory = Runtime.getRuntime().freeMemory();
                        }
                        mainGadget.update( gadget_g );
                        if (startTime > 0) {
                            System.err.print("updating took "+
                                (System.currentTimeMillis()-startTime)+
                                " milliseconds");
                            long consumed = startMemory - Runtime.getRuntime().freeMemory();
                            if (consumed < 0) { // gc was performed
                                System.err.println();
                            } else {
                                System.err.println(" and probably consumed "+consumed+" bytes");
                            }
                        }
                    //}
                }
                // I have to do this to fix Netscape 3.01 on Win32
                gadget_g.dispose();
                expose(origClip,clip/*,curDamageList*/);
        	}
        }
    }

    public void exposeGraphics() {
        Graphics g = getGraphics();
		g.drawImage( image, 0, 0, this );
		g.dispose();
    }

    private void expose(Rectangle origClip,Rectangle toPaint/*,Vector damageList*/) {
        Graphics g = getGraphics();
        if (toPaint != null) {
            origClip = origClip.union(toPaint);
        }
        g.clipRect( origClip.x, origClip.y, origClip.width, origClip.height );

		g.drawImage( image, 0, 0, this );

		processEvent(
		    new ShellPaintEvent(this,ShellPaintEvent.REGION_UPDATED,origClip));

		/*  DO NOT REMOVE!!!  GREAT FOR TESTING...
		g.setColor(java.awt.Color.red);
		g.drawRect(origClip.x,origClip.y,origClip.width-1,origClip.height-1);
		g.drawLine(origClip.x,origClip.y,origClip.x+origClip.width,origClip.y+origClip.height);
		g.drawLine(origClip.x,origClip.y+origClip.height,origClip.x+origClip.width,origClip.y);
		if (toPaint != null) {
    		g.setColor(java.awt.Color.yellow);
    		g.drawRect(toPaint.x,toPaint.y,toPaint.width-1,toPaint.height-1);
    		g.drawLine(toPaint.x,toPaint.y,toPaint.x+toPaint.width,toPaint.y+toPaint.height);
    		g.drawLine(toPaint.x,toPaint.y+toPaint.height,toPaint.x+toPaint.width,toPaint.y);
            //int numrects = damageList.size();
            //for (int i = 0; i < numrects; i++) {
            //    Rectangle damaged = (Rectangle)damageList.elementAt(i);
        	//	g.setColor(java.awt.Color.green);
        	//	g.drawRect(damaged.x,damaged.y,damaged.width-1,damaged.height-1);
        	//	g.drawLine(damaged.x,damaged.y,damaged.x+damaged.width,damaged.y+damaged.height);
        	//	g.drawLine(damaged.x,damaged.y+damaged.height,damaged.x+damaged.width,damaged.y);
            //}
    	}
    	*/
		g.dispose();
    }

    /**
     * flushGraphics
     */
    public final void flushGraphics() {
        Graphics g = getGraphics();
        Rectangle clip = damageRect;
        if ( clip != null ) {
        /*
        Vector curDamageList = damageList;
        if ( curDamageList != null ) {
            Rectangle clip = null;
            int size = curDamageList.size();
            for (int i = 0; i < size; i++) {
                Rectangle damaged = (Rectangle)curDamageList.elementAt(i);
                if (clip == null) {
                    clip = new Rectangle(damaged.x,damaged.y,damaged.width,damaged.height);
                } else {
                    clip = clip.union(damaged);
                }
            }
            */
            g.clipRect( clip.x, clip.y, clip.width, clip.height );
            paint( g );
        }
    }

    /**
     * for painting (printing) to a different kind of graphics object,
     * with potentially different font sizes, you need to lock the gadget
     * tree (with synchronized(getGadgetTreeLock()) {... on this shell),
     * and call this function with the graphics you want to use.
     * Then resize the gadget you want to paint as necessary, i.e. by calling
	 * fontChanged, then call validateAll() on the GadgetShell,
	 * then paint the gadget, i.e. by calling update with
	 * the gadget's GadgetGraphics object (you can call "getGadgetGraphics"
     * on the object, and it will return the appropriate translated object
     * for the graphics you passed it).  Be sure to set the paintingAll flag
	 * to true BEFORE calling gadget.update(gg)!  Finally, call this function with
     * "null, 0, 0", set the size back to normal, call setFontChanged on the object, 
     * and validate  the gadget and you should be done.
     * @param g the graphics object to use in all getGadgetGraphics calls
     * @param width the width of the graphics "page"
     * @param height the height of the graphics "page"
     */
    public void setGraphicsOverride(Graphics g, int width, int height) {
        graphicsOverride = g;
        widthOverride = width;
        heightOverride = height;
    }

    /**
     * @returns true if graphicsOverride != null
     */
    public boolean isOverridingGraphics() {
        return graphicsOverride != null;
    }

    /**
     * getPermGraphics
     * @return GadgetGraphics
     */
    public final GadgetGraphics getPermGraphics() {
        synchronized(getGadgetTreeLock()) {
            if (graphicsOverride != null) {
				return new GadgetGraphics(graphicsOverride.create(),
                                          widthOverride, heightOverride);
            }
            Dimension size = size();

            if ( ( imageSize == null ) ||
                 ( imageSize.width != size.width ) ||
                 ( imageSize.height != size.height ) ) {
                imageSize = size;
                image = null;
            }

            Graphics g = null;
            if ( image == null && imageSize.width > 0 && imageSize.height > 0 ) {
                image = createImage( imageSize.width,imageSize.height );
            }

            if ( image != null ) {
                g = image.getGraphics();
                if ( g != null ) {
                    return new GadgetGraphics(g/*.create(0,0,imageSize.width,imageSize.height)*/,
                                              imageSize.width,imageSize.height);
                }
            }

            return null;
        }
    }

    /**
     * getTempGraphics
     * @return GadgetGraphics
     */
    public final GadgetGraphics getTempGraphics() {
        Graphics g = getGraphics();
        if (g == null) {
            return null;
        }
 // for Netscape's sake I really should be disposing the return from getGraphics(), but that
 // might break under other JVMs...leave it in unless font issues come into play on Netscape 3.01
        return new GadgetGraphics(getGraphics().create());
           // have to create a copy using create() because of bugs in some
           // JVMs
    }

    /**
     * update
     * @param g - TBD
     */
    public void update( Graphics g ) {
        paint( g );
    }

    /**
     * notifyInvalid
     */
    protected final void notifyInvalid() {
        //synchronized(getGadgetTreeLock()) { // removed so I can wait for image in ImageGadget
            //if ( ! checkAllValid ) { this too
                checkAllValid = true;
//                repaint(0,0,1,1);
                invalidate();
            //}
        //}
    }

    private static final int DAMAGE_THRESHOLD = 25;
    /**
     * checks if they intersect within a threshold
     */
    private boolean shouldMerge(Rectangle a, Rectangle b) {
    	return !((a.x + a.width + DAMAGE_THRESHOLD <= b.x - DAMAGE_THRESHOLD) ||
    		 (a.y + a.height + DAMAGE_THRESHOLD <= b.y - DAMAGE_THRESHOLD) ||
    		 (a.x - DAMAGE_THRESHOLD >= b.x + b.width + DAMAGE_THRESHOLD) ||
    		 (a.y - DAMAGE_THRESHOLD >= b.y + b.height + DAMAGE_THRESHOLD));
    }

    /**
     * addDamagedArea
     * @param x - TBD
     * @param y - TBD
     * @param width - TBD
     * @param height - TBD
     */
    protected final void addDamagedArea( int x, int y, int width, int height ) {
        synchronized(getGadgetTreeLock()) {
            x = Math.max(0,x);
            y = Math.max(0,y);
            Dimension shellSize = size();
            width = Math.min((shellSize.width-x),width);
            height = Math.min((shellSize.height-y),height);
            if ( ( width > 0 ) &&
                 ( height > 0 ) ) {
                if (damageRect == null) {
                    damageRect = new Rectangle(x, y, width, height);
                /*
                if (damageList == null) {
                    damageList = new Vector();
                    damageList.addElement(new Rectangle( x, y, width, height ));
                */
                } else {
                    damageRect = damageRect.union(new Rectangle( x, y, width, height ));
                    /*
                    Rectangle newDamage = new Rectangle( x, y, width, height );
                    Rectangle mergeDamage = null;
                    int size = damageList.size();
                    for (int i = 0; i < size; i++) {
                        Rectangle damaged = (Rectangle)damageList.elementAt(i);
                        if (damaged.intersects(newDamage)) {
                            mergeDamage = damaged;
                            if (damaged.intersection(newDamage).equals(newDamage)) {
                                return;
                            }
                        }
                    }
                    if (mergeDamage == null) {
                        for (int i = 0; i < size; i++) {
                            Rectangle damaged = (Rectangle)damageList.elementAt(i);
                            if (shouldMerge(damaged,newDamage)) {
                                mergeDamage = damaged;
                                break;
                            }
                        }
                    }
                    if (mergeDamage != null) {
                        damageList.removeElement(mergeDamage);
                        newDamage = mergeDamage.union(newDamage);
                    }
                    damageList.addElement(newDamage);
                    */
                }
            }
        }
        repaint(x,y,width,height);
    }

    /**
     * getApplet
     * @return Applet
     */
    public Applet getApplet() {
        Container parent = getParent();
        while ( ( parent != null ) &&
                ( ! ( parent instanceof Applet ) ) ) {
            parent = parent.getParent();
        }
        return (Applet)parent;
    }

    /**
     * getDialog
     * @return Dialog
     */
    public Dialog getDialog() {
        Container parent = getParent();
        while ( ( parent != null ) &&
                ( ! ( parent instanceof Dialog ) ) ) {
            parent = parent.getParent();
        }
        return (Dialog)parent;
    }

    /**
     * getFrame
     * @return Frame
     */
    public Frame getFrame() {
        Container parent = getParent();
        while ( ( parent != null ) &&
                ( ! ( parent instanceof Frame ) ) ) {
            parent = parent.getParent();
        }
        return (Frame)parent;
    }

    /**
     * getImag
     * @param url - TBD
     * @return Image
     */
    public final Image getImage( URL url ) {
        Applet applet = getApplet();
		return (applet != null)
			? applet.getImage( url )
			: Toolkit.getDefaultToolkit().getImage( url );
    }

    /**
     * isFocusAllowedToEscape
     * @return boolean
     */
    public final boolean isFocusAllowedToEscape() {
        return focusAllowedToEscape;
    }

    /**
     * setFocusAllowedToEscape
     * @param focusAllowedToEscape - TBD
     */
    public final void setFocusAllowedToEscape( boolean focusAllowedToEscape ) {
        this.focusAllowedToEscape = focusAllowedToEscape;
    }

    /**
     * giveFocusTo
     * @param g - TBD
     */
    public void giveFocusTo( Gadget g ) {
        giveFocusTo(g,false,true);
    }

    /**
     * giveFocusTo
     * @param g - TBD
     * @param group - TBD
     * @param forcefocus - TBD
     */
    public void giveFocusTo( Gadget g, boolean group, boolean forceFocus ) {

        synchronized(getGadgetTreeLock()) {
            if (focusShell != this && g != null) {
                if (focusShell != null) {
                    focusShell.giveFocusTo(null);
                }
                focusShell = this;
            }
            if ( g == focusOwner ) {
                //lastFocusOwner = null;
                return;
            }
            if ( ( g != null ) &&
    		     ( ( ! g.isShowing() ) ||
                     ( ! g.isFunctioning() ) ) ) {
                return;
            }
            ContainerGadget lastFocusGroup = null;
            if ( haveFocus &&
                 ( focusOwner != null ) ) {
                lastFocusGroup = focusOwner.getFocusGroup();
                if ( lastFocusGroup != null ) {
                    lastFocusGroup.setLastGroupedFocus(focusOwner);
                }
    			FocusEvent focus = new FocusEvent( null, FocusEvent.FOCUS_LOST );
    			focus.setSource( focusOwner );
    			Gadget target = focusOwner;
			    setFocusOwner(g);
    		    helpOwner = mouseOwner;
                target.processEvent( focus );
            }
		    setFocusOwner(g);
            if ( ! group && focusOwner != null ) {
                ContainerGadget focusGroup = focusOwner.getFocusGroup();
                if ( focusGroup != null && focusGroup != lastFocusGroup && ! forceFocus ) {
                    Gadget lastFocus = focusGroup.getLastGroupedFocus();
                    if ( lastFocus != null &&
                         lastFocus.isShowing() && lastFocus.isFunctioning() ) {
        			    setFocusOwner(lastFocus);
                    }
                }
            }
    	    helpOwner = focusOwner;
            if ( haveFocus &&
                 ( focusOwner != null ) ) {
    			FocusEvent focus = new FocusEvent( null, FocusEvent.FOCUS_GAINED );
    			focus.setSource( focusOwner );
                focusOwner.processEvent( focus );
            }
        }
    }

    /**
     * forceFocus
     */
	private void setFocusOwner(Gadget focusOwner) {
	    this.focusOwner = focusOwner;
	    if (focusOwner != null) {
	        lastFocusOwner = focusOwner;
	    }
	}

    /**
     * forceFocus
     */
    public void forceFocus() {
        haveFocus = true;
    }

    /**
     * focusForward
     * @param group - TBD
     * @return boolean result
     */
    public boolean focusForward(boolean group) {
        Gadget focusGroup = null;
        if ( focusOwner != null ) {
            focusGroup = focusOwner.getFocusGroup();
        }
        if ( focusGroup == null ) {
            if ( group ) {
                return false;
            }
        }
        focusForward(group,focusGroup);
        return true;
    }

    /**
     * focusForward
     * @param group - TBD
     * @param focusGroup - TBD
     */
    public void focusForward(boolean group,Gadget focusGroup) {
        synchronized(getGadgetTreeLock()) {
            Gadget next_owner = null;
            int next_x = 0;
            int next_y = 0;
            int prev_x = -1;
            int prev_y = -1;

            Point offset;

            if ( focusOwner != null ) {
                Gadget nextFocusGadget = focusOwner.getNextFocusGadget();
                if ( ( nextFocusGadget != null ) &&
                     ( ( group || focusGroup == null || ( nextFocusGadget.getFocusGroup() != focusGroup ) ) &&
                       ( ! group || ( nextFocusGadget.getFocusGroup() == focusGroup ) ) ) &&
                     ( nextFocusGadget.isFocusTraversable() ) &&
                     ( nextFocusGadget.isFunctioning() ) &&
                     ( nextFocusGadget.isShowing() ) ) {
                    giveFocusTo( nextFocusGadget, group, false );
                    return;
                }
                offset = focusOwner.getFocusOffset();
                prev_x = offset.x;
                prev_y = offset.y;
            }

            for ( int i = 0; i < subgadgets.size(); i++ ) {
                Gadget g = (Gadget)subgadgets.elementAt( i );
                offset = g.getFocusOffset();
                if ( ( ( group || focusGroup == null || ( g.getFocusGroup() != focusGroup ) ) &&
                       ( ! group || ( g.getFocusGroup() == focusGroup ) ) ) &&
                     g.isFocusTraversable() &&
                     g.isFunctioning() &&
                     g.isShowing() ) {
                    if ( ( ( offset.y > prev_y ) ||
                         ( ( offset.y == prev_y ) &&
                           ( offset.x > prev_x ) ) ) ) {
                        if ( ( next_owner == null ) ||
                             ( offset.y < next_y ) ||
                             ( ( offset.y == next_y ) &&
                               ( offset.x < next_x ) ) ) {
                            next_owner = g;
                            next_x = offset.x;
                            next_y = offset.y;
                        }
                    }
                }
            }

            if ( ( next_owner == null ) &&
                 ( focusOwner != null ) ) {
                giveFocusTo( null, group, false );
                if ( focusAllowedToEscape ) {
                    nextFocus();
                }
                else {
                    focusForward(group, focusGroup);
                }
            }
            else {
                giveFocusTo( next_owner, group, false );
            }
        }
    }

    /**
     * focusBackward
     * @param group - TBD
     * @return boolean result
     */
    public boolean focusBackward(boolean group) {
        Gadget focusGroup = null;
        if ( focusOwner != null ) {
            focusGroup = focusOwner.getFocusGroup();
        }
        if ( focusGroup == null ) {
            if ( group ) {
                return false;
            }
        }
        focusBackward(group,focusGroup);
        return true;
    }

    /**
     * focusBackward
     * @param group - TBD
     * @param focusGroup - TBD
     */
    public void focusBackward(boolean group,Gadget focusGroup) {
        synchronized(getGadgetTreeLock()) {
            Gadget next_owner = null;
            int next_x = 0;
            int next_y = 0;
            int prev_x = Integer.MAX_VALUE;
            int prev_y = Integer.MAX_VALUE;

            Point offset;
            if ( focusOwner != null ) {
                for ( int i = 0; i < subgadgets.size(); i++ ) {
                    Gadget g = (Gadget)subgadgets.elementAt( i );
                    Gadget nextFocusGadget = g.getNextFocusGadget();
                    if ( nextFocusGadget == focusOwner ) {
                        if ( ( ( group || focusGroup == null || ( g.getFocusGroup() != focusGroup ) ) &&
                               ( ! group || ( g.getFocusGroup() == focusGroup ) ) ) &&
                             ( g.isFocusTraversable() ) &&
                             ( g.isFunctioning() ) &&
                             ( g.isShowing() ) ) {
                            giveFocusTo( g, group, false );
                            return;
                        }
                    }
                }
                offset = focusOwner.getFocusOffset();
                prev_x = offset.x;
                prev_y = offset.y;
            }

            for ( int i = 0; i < subgadgets.size(); i++ ) {
                Gadget g = (Gadget)subgadgets.elementAt( i );
                offset = g.getFocusOffset();
                if ( ( ( group || focusGroup == null || ( g.getFocusGroup() != focusGroup ) ) &&
                       ( ! group || ( g.getFocusGroup() == focusGroup ) ) ) &&
                     g.isFocusTraversable() &&
                     g.isFunctioning() &&
                     g.isShowing() ) {
                    if ( ( ( offset.y < prev_y ) ||
                         ( ( offset.y == prev_y ) &&
                           ( offset.x < prev_x ) ) ) ) {
                        if ( ( next_owner == null ) ||
                             ( offset.y > next_y ) ||
                             ( ( offset.y == next_y ) &&
                               ( offset.x > next_x ) ) ) {
                            next_owner = g;
                            next_x = offset.x;
                            next_y = offset.y;
                        }
                    }
                }
            }

            if ( ( next_owner == null ) &&
                 ( focusOwner != null ) ) {
                giveFocusTo( null, group, false );
                focusBackward(group, focusGroup);
            }
            else {
                giveFocusTo( next_owner, group, false );
            }
        }
    }

    /**
     * resetFocus
     */
    public final void resetFocus() {
        if (lastFocusOwner == null) {
            giveFocusTo(null);
            focusForward(false);
        } else {
            giveFocusTo(lastFocusOwner);
        }
    }

    /**
     * getFocusOwner
     * @return Gadget
     */
    public final Gadget getFocusOwner() {
        return focusOwner;
    }

    /**
     * getMouseOwner
     * @return Gadget
     */
    public final Gadget getMouseOwner() {
        return mouseOwner;
    }

    /**
     * findCommonAncestor
     * @param a - TBD
     * @param b - TBD
     * @return Gadget
     */
    public Gadget findCommonAncestor( Gadget a, Gadget b ) {
        while ( ( a != null ) && ( b != null ) ) {
            if ( ( a instanceof ContainerGadget ) &&
                 ( b.isDescendentOf( ((ContainerGadget)a) ) ) ) {
                return a;
            }
            if ( ( b instanceof ContainerGadget ) &&
                 ( a.isDescendentOf( ((ContainerGadget)b) ) ) ) {
                return b;
            }
            a = a.getParent();
            b = b.getParent();
        }
        return null;
    }

    /**
     * recursiveSendMouseEnter
     * @param g - TBD
     * @param stopGadget - TBD
     * @param entered - TBD
     */

    private void recursiveSendMouseEnter( Gadget g, Gadget stopGadget, MouseEvent entered ) {
        if ( ( g != null ) &&
             ( g != stopGadget ) ) {
            recursiveSendMouseEnter( g.getParent(), stopGadget, entered );
            if (entered.isConsumed()) {
                entered = new MouseEvent(entered.getEvent(), MouseEvent.MOUSE_ENTERED);
            }
            entered.setSource( g );
            g.processEvent( entered );
        }
    }

    /**
     * changeMouseOwner
     * @param newMouseOwner - TBD
     * @param evt - TBD
     */
    public void changeMouseOwner( Gadget newMouseOwner, Event evt ) {

        synchronized(getGadgetTreeLock()) {
            if ( newMouseOwner != mouseOwner ) {

                Gadget common = findCommonAncestor( mouseOwner, newMouseOwner );

                MouseEvent entered = new MouseEvent( evt, MouseEvent.MOUSE_ENTERED );
                MouseEvent exited = new MouseEvent( evt, MouseEvent.MOUSE_EXITED );

                if ( mouseOwner != null ) {
                    Gadget g = mouseOwner;
                    Point offset = g.getOffset();
                    exited.translatePoint( -offset.x, -offset.y );
                    while ( ( g != null ) &&
                            ( g != common ) ) {
                        if (exited.isConsumed()) {
                            exited = new MouseEvent(exited.getEvent(), MouseEvent.MOUSE_EXITED);
                        }
                        exited.setSource( g );
                        g.processEvent( exited );
                        exited.translatePoint( g.x, g.y );
                        g = g.getParent();
                    }
                }

                if ( newMouseOwner != null ) {
                    recursiveSendMouseEnter( newMouseOwner, common, entered );
                }

                mouseOwner = newMouseOwner;
                if (mouseOwner != null) {
                    setGadgetCursor(mouseOwner.getCursor());
                } else {
                    //setGadgetCursor(null); // some OSs can't deal with this!
                    // instead, let's just set it to null
                    cursor = null;
                }

                if ( findTipOwner() != null ) {
                    startTimer();
                }

                if ( mouseOwner == null ) {
            	    helpOwner = focusOwner;
            	}
            	else {
            	    helpOwner = mouseOwner;
            	}
            }
        }
    }

    public void notifyCursorChanged() {
        synchronized(getGadgetTreeLock()) {
            if (mouseOwner != null) {
                setGadgetCursor(mouseOwner.getCursor());
            }
        }
    }

    private void setGadgetCursor(GadgetCursor cursor) {
        synchronized (getGadgetTreeLock()) {
            if (cursor != this.cursor) {
                this.cursor = cursor;
                Frame frame = getFrame();
                if (frame != null) {
                    if (overrideCursor == null) {
                        if (cursor == null || cursor.getType() == GadgetCursor.DEFAULT_CURSOR) {
                            if (this instanceof GadgetDialog) { // fix netscape problem
                                frame.setCursor(
                                    GadgetCursor.getPredefinedCursor(
                                    GadgetCursor.TEXT_CURSOR).getType());
                            }
                            frame.setCursor(GadgetCursor.getDefaultCursor().getType());
                        } else {
                            frame.setCursor(cursor.getType());
                        }
                    }
                }
            }
        }
    }

    public void showWaitCursor() {
        synchronized (getGadgetTreeLock()) {
            GadgetCursor waitCursor =
                GadgetCursor.getPredefinedCursor(GadgetCursor.WAIT_CURSOR);
            if (overrideCursor != waitCursor) {
                overrideCursor = waitCursor;
                Frame frame = getFrame();
                if (frame != null) {
                    frame.setCursor(waitCursor.getType());
                }
            }
        }
    }

    public void showWaitCursor(int forSeconds) {
        showWaitCursor();
        HideWaitCursor hideWait = new HideWaitCursor(this,forSeconds);
        hideWait.start();
    }

    public void hideWaitCursor() {
        synchronized (getGadgetTreeLock()) {
            GadgetCursor waitCursor =
                GadgetCursor.getPredefinedCursor(GadgetCursor.WAIT_CURSOR);
            if (overrideCursor == waitCursor) {
                overrideCursor = null;
                Frame frame = getFrame();
                if (frame != null) {
                    if (cursor == null) {
                        frame.setCursor(GadgetCursor.getDefaultCursor().getType());
                    } else {
                        frame.setCursor(cursor.getType());
                    }
                }
            }
        }
    }

    /**
     * enableTips
     */
    public void enableTips() {
        showTips = true;
    }

    /**
     * disableTips
     */
    public void disableTips() {
        showTips = false;
    }

    /**
     * notifyWindowOpened
     */
    public void notifyWindowOpened() {
        windowOpened = lastEventTime;
        if (focusShell != null) {
            focusShell.giveFocusTo(null);
        }
    }

}

class HideWaitCursor extends Thread {

    private GadgetShell shell;
    private int forSeconds;

    public HideWaitCursor(GadgetShell shell, int forSeconds) {
		super("dtai.gwt.HideWaitCursor");
        this.shell = shell;
        this.forSeconds = forSeconds;
    }

    public void run() {
        try {
            sleep(forSeconds*1000);
        } catch (InterruptedException ie) {
        }
        shell.hideWaitCursor();
    }
}
