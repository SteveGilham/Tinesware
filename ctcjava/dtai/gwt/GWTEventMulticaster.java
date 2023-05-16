/****************************************************************
 **
 **  $Id: GWTEventMulticaster.java,v 1.3 1997/08/06 23:27:02 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/GWTEventMulticaster.java,v $
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
 **  History
 **  TextListener added
 **              -- Mr. Tines, May 98.
 ****************************************************************/

package dtai.gwt;

import java11.awt.event.*;
import java11.util.EventListener;
import dtai.gwt.TreeListener;
import dtai.gwt.TreeEvent;
import dtai.gwt.CellListener;
import dtai.gwt.CellEvent;

/**
 * A class which implements efficient and thread-safe multi-cast event
 * dispatching for the GWT events defined in the java.awt.event package.
 * This class will manage an immutable structure consisting of a chain of
 * event listeners and will dispatch events to those listeners.  Because
 * the structure is immutable, it is safe to use this API to add/remove
 * listeners during the process of an event dispatch operation.
 *
 * An example of how this class could be used to implement a new
 * component which fires "action" events:
 *
 * <pre><code>
 * public myGadget extends Gadget {
 *     ActionListener actionListener = null;
 *
 *     public void addActionListener(ActionListener l) {
 *	   actionListener = GWTEventMulticaster.add(actionListener, l);
 *     }
 *     public void removeActionListener(ActionListener l) {
 *  	   actionListener = GWTEventMulticaster.remove(actionListener, l);
 *     }
 *     public void processEvent(AWTEvent e) {
 *         // when event occurs which causes "action" semantic
 *         if (actionListener != null) {
 *             actionListener.actionPerformed(new ActionEvent());
 *         }
 * }
 * </code></pre>
 *
 * @version 	1.12, 03/03/97
 * @author      John Rose
 * @author 	Amy Fowler
 */

public class GWTEventMulticaster implements
    ComponentListener, FocusListener, KeyListener,
    MouseListener, MouseMotionListener, WindowListener,
    ActionListener, ItemListener, AdjustmentListener,
    TreeListener, CellListener, TextListener {

    protected EventListener a, b;

    /**
     * Creates an event multicaster instance which chains listener-a
     * with listener-b.
     * @param a listener-a
     * @param b listener-b
     */
    protected GWTEventMulticaster(EventListener a, EventListener b) {
    	this.a = a; this.b = b;
    }

    /**
     * Removes a listener from this multicaster and returns the
     * resulting multicast listener.
     * @param oldl the listener to be removed
     */
    protected EventListener remove(EventListener oldl) {
    	if (oldl == a)  return b;
    	if (oldl == b)  return a;
    	EventListener a2 = removeInternal(a, oldl);
    	EventListener b2 = removeInternal(b, oldl);
    	if (a2 == a && b2 == b) {
    	    return this;	// it's not here
    	}
    	return addInternal(a2, b2);
    }

    /**
     * Handles the componentResized event by invoking the
     * componentResized methods on listener-a and listener-b.
     * @param e the component event
     */
    public void componentResized(ComponentEvent e) {
        ((ComponentListener)a).componentResized(e);
        ((ComponentListener)b).componentResized(e);
    }

    /**
     * Handles the componentMoved event by invoking the
     * componentMoved methods on listener-a and listener-b.
     * @param e the component event
     */
    public void componentMoved(ComponentEvent e) {
        ((ComponentListener)a).componentMoved(e);
        ((ComponentListener)b).componentMoved(e);
    }

    /**
     * Handles the componentShown event by invoking the
     * componentShown methods on listener-a and listener-b.
     * @param e the component event
     */
    public void componentShown(ComponentEvent e) {
        ((ComponentListener)a).componentShown(e);
        ((ComponentListener)b).componentShown(e);
    }

    /**
     * Handles the componentHidden event by invoking the
     * componentHidden methods on listener-a and listener-b.
     * @param e the component event
     */
    public void componentHidden(ComponentEvent e) {
        ((ComponentListener)a).componentHidden(e);
        ((ComponentListener)b).componentHidden(e);
    }

    /**
     * Handles the focusGained event by invoking the
     * focusGained methods on listener-a and listener-b.
     * @param e the focus event
     */
    public void focusGained(FocusEvent e) {
        ((FocusListener)a).focusGained(e);
        ((FocusListener)b).focusGained(e);
    }

    /**
     * Handles the focusLost event by invoking the
     * focusLost methods on listener-a and listener-b.
     * @param e the focus event
     */
    public void focusLost(FocusEvent e) {
        ((FocusListener)a).focusLost(e);
        ((FocusListener)b).focusLost(e);
    }

    /**
     * Handles the keyTyped event by invoking the
     * keyTyped methods on listener-a and listener-b.
     * @param e the key event
     */
    public void keyTyped(KeyEvent e) {
        if (!e.isConsumed()) ((KeyListener)a).keyTyped(e);
        if (!e.isConsumed()) ((KeyListener)b).keyTyped(e);
    }

    /**
     * Handles the keyPressed event by invoking the
     * keyPressed methods on listener-a and listener-b.
     * @param e the key event
     */
    public void keyPressed(KeyEvent e) {
        if (!e.isConsumed()) ((KeyListener)a).keyPressed(e);
        if (!e.isConsumed()) ((KeyListener)b).keyPressed(e);
    }

    /**
     * Handles the keyReleased event by invoking the
     * keyReleased methods on listener-a and listener-b.
     * @param e the key event
     */
    public void keyReleased(KeyEvent e) {
        if (!e.isConsumed()) ((KeyListener)a).keyReleased(e);
        if (!e.isConsumed()) ((KeyListener)b).keyReleased(e);
    }

    /**
     * Handles the mouseClicked event by invoking the
     * mouseClicked methods on listener-a and listener-b.
     * @param e the mouse event
     */
    public void mouseClicked(MouseEvent e) {
        if (!e.isConsumed()) ((MouseListener)a).mouseClicked(e);
        if (!e.isConsumed()) ((MouseListener)b).mouseClicked(e);
    }

    /**
     * Handles the mousePressed event by invoking the
     * mousePressed methods on listener-a and listener-b.
     * @param e the mouse event
     */
    public void mousePressed(MouseEvent e) {
        if (!e.isConsumed()) ((MouseListener)a).mousePressed(e);
        if (!e.isConsumed()) ((MouseListener)b).mousePressed(e);
    }

    /**
     * Handles the mouseReleased event by invoking the
     * mouseReleased methods on listener-a and listener-b.
     * @param e the mouse event
     */
    public void mouseReleased(MouseEvent e) {
        if (!e.isConsumed()) ((MouseListener)a).mouseReleased(e);
        if (!e.isConsumed()) ((MouseListener)b).mouseReleased(e);
    }

    /**
     * Handles the mouseEntered event by invoking the
     * mouseEntered methods on listener-a and listener-b.
     * @param e the mouse event
     */
    public void mouseEntered(MouseEvent e) {
        if (!e.isConsumed()) ((MouseListener)a).mouseEntered(e);
        if (!e.isConsumed()) ((MouseListener)b).mouseEntered(e);
    }

    /**
     * Handles the mouseExited event by invoking the
     * mouseExited methods on listener-a and listener-b.
     * @param e the mouse event
     */
    public void mouseExited(MouseEvent e) {
        if (!e.isConsumed()) ((MouseListener)a).mouseExited(e);
        if (!e.isConsumed()) ((MouseListener)b).mouseExited(e);
    }

    /**
     * Handles the mouseDragged event by invoking the
     * mouseDragged methods on listener-a and listener-b.
     * @param e the mouse event
     */
    public void mouseDragged(MouseEvent e) {
        if (!e.isConsumed()) ((MouseMotionListener)a).mouseDragged(e);
        if (!e.isConsumed()) ((MouseMotionListener)b).mouseDragged(e);
    }

    /**
     * Handles the mouseMoved event by invoking the
     * mouseMoved methods on listener-a and listener-b.
     * @param e the mouse event
     */
    public void mouseMoved(MouseEvent e) {
        if (!e.isConsumed()) ((MouseMotionListener)a).mouseMoved(e);
        if (!e.isConsumed()) ((MouseMotionListener)b).mouseMoved(e);
    }

    /**
     * Handles the windowOpened event by invoking the
     * windowOpened methods on listener-a and listener-b.
     * @param e the window event
     */
    public void windowOpened(WindowEvent e) {
        ((WindowListener)a).windowOpened(e);
        ((WindowListener)b).windowOpened(e);
    }

    /**
     * Handles the windowClosing event by invoking the
     * windowClosing methods on listener-a and listener-b.
     * @param e the window event
     */
    public void windowClosing(WindowEvent e) {
        ((WindowListener)a).windowClosing(e);
        ((WindowListener)b).windowClosing(e);
    }

    /**
     * Handles the windowClosed event by invoking the
     * windowClosed methods on listener-a and listener-b.
     * @param e the window event
     */
    public void windowClosed(WindowEvent e) {
        ((WindowListener)a).windowClosed(e);
        ((WindowListener)b).windowClosed(e);
    }

    /**
     * Handles the windowIconified event by invoking the
     * windowIconified methods on listener-a and listener-b.
     * @param e the window event
     */
    public void windowIconified(WindowEvent e) {
        ((WindowListener)a).windowIconified(e);
        ((WindowListener)b).windowIconified(e);
    }

    /**
     * Handles the windowDeiconfied event by invoking the
     * windowDeiconified methods on listener-a and listener-b.
     * @param e the window event
     */
    public void windowDeiconified(WindowEvent e) {
        ((WindowListener)a).windowDeiconified(e);
        ((WindowListener)b).windowDeiconified(e);
    }

    /**
     * Handles the windowActivated event by invoking the
     * windowActivated methods on listener-a and listener-b.
     * @param e the window event
     */
    public void windowActivated(WindowEvent e) {
        ((WindowListener)a).windowActivated(e);
        ((WindowListener)b).windowActivated(e);
    }

    /**
     * Handles the windowDeactivated event by invoking the
     * windowDeactivated methods on listener-a and listener-b.
     * @param e the window event
     */
    public void windowDeactivated(WindowEvent e) {
        ((WindowListener)a).windowDeactivated(e);
        ((WindowListener)b).windowDeactivated(e);
    }

    /**
     * Handles the actionPerformed event by invoking the
     * actionPerformed methods on listener-a and listener-b.
     * @param e the action event
     */
    public void actionPerformed(ActionEvent e) {
        ((ActionListener)a).actionPerformed(e);
        ((ActionListener)b).actionPerformed(e);
    }

    /**
     * Handles the itemStateChanged event by invoking the
     * itemStateChanged methods on listener-a and listener-b.
     * @param e the item event
     */
    public void itemStateChanged(ItemEvent e) {
        ((ItemListener)a).itemStateChanged(e);
        ((ItemListener)b).itemStateChanged(e);
    }

    /**
     * Handles the treeStateChanged event by invoking the
     * treeStateChanged methods on listener-a and listener-b.
     * @param e the tree event
     */
    public void treeStateChanged(TreeEvent e) {
        ((TreeListener)a).treeStateChanged(e);
        ((TreeListener)b).treeStateChanged(e);
    }

    /**
     * Handles the treeNodeExpanded event by invoking the
     * treeNodeExpanded methods on listener-a and listener-b.
     * @param e the item event
     */
    public void treeNodeExpanded(TreeEvent e) {
        ((TreeListener)a).treeNodeExpanded(e);
        ((TreeListener)b).treeNodeExpanded(e);
    }

    /**
     * Handles the treeNodeCondensed event by invoking the
     * treeNodeCondensed methods on listener-a and listener-b.
     * @param e the item event
     */
    public void treeNodeCondensed(TreeEvent e) {
        ((TreeListener)a).treeNodeCondensed(e);
        ((TreeListener)b).treeNodeCondensed(e);
    }

    /**
     * Handles the cellStateChanged event by invoking the
     * cellStateChanged methods on listener-a and listener-b.
     * @param e the cell event
     */
    public void cellStateChanged(CellEvent e) {
        ((CellListener)a).cellStateChanged(e);
        ((CellListener)b).cellStateChanged(e);
    }

    /**
     * Handles the adjustmentValueChanged event by invoking the
     * adjustmentValueChanged methods on listener-a and listener-b.
     * @param e the adjustment event
     */
    public void adjustmentValueChanged(AdjustmentEvent e) {
        ((AdjustmentListener)a).adjustmentValueChanged(e);
        ((AdjustmentListener)b).adjustmentValueChanged(e);
    }

    /**
     * Adds component-listener-a with component-listener-b and
     * returns the resulting multicast listener.
     * @param a component-listener-a
     * @param b component-listener-b
     */
    public static ComponentListener add(ComponentListener a, ComponentListener b) {
        return (ComponentListener)addInternal(a, b);
    }

    /**
     * Adds focus-listener-a with focus-listener-b and
     * returns the resulting multicast listener.
     * @param a focus-listener-a
     * @param b focus-listener-b
     */
    public static FocusListener add(FocusListener a, FocusListener b) {
        return (FocusListener)addInternal(a, b);
    }

    /**
     * Adds key-listener-a with key-listener-b and
     * returns the resulting multicast listener.
     * @param a key-listener-a
     * @param b key-listener-b
     */
    public static KeyListener add(KeyListener a, KeyListener b) {
        return (KeyListener)addInternal(a, b);
    }

    /**
     * Adds mouse-listener-a with mouse-listener-b and
     * returns the resulting multicast listener.
     * @param a mouse-listener-a
     * @param b mouse-listener-b
     */
    public static MouseListener add(MouseListener a, MouseListener b) {
        return (MouseListener)addInternal(a, b);
    }

    /**
     * Adds mouse-motion-listener-a with mouse-motion-listener-b and
     * returns the resulting multicast listener.
     * @param a mouse-motion-listener-a
     * @param b mouse-motion-listener-b
     */
    public static MouseMotionListener add(MouseMotionListener a, MouseMotionListener b) {
        return (MouseMotionListener)addInternal(a, b);
    }

    /**
     * Adds window-listener-a with window-listener-b and
     * returns the resulting multicast listener.
     * @param a window-listener-a
     * @param b window-listener-b
     */
    public static WindowListener add(WindowListener a, WindowListener b) {
        return (WindowListener)addInternal(a, b);
    }

    /**
     * Adds action-listener-a with action-listener-b and
     * returns the resulting multicast listener.
     * @param a action-listener-a
     * @param b action-listener-b
     */
    public static ActionListener add(ActionListener a, ActionListener b) {
        return (ActionListener)addInternal(a, b);
    }

    /**
     * Adds item-listener-a with item-listener-b and
     * returns the resulting multicast listener.
     * @param a item-listener-a
     * @param b item-listener-b
     */
    public static ItemListener add(ItemListener a, ItemListener b) {
        return (ItemListener)addInternal(a, b);
    }

    /**
     * Adds item-listener-a with item-listener-b and
     * returns the resulting multicast listener.
     * @param a item-listener-a
     * @param b item-listener-b
     */
    public static TreeListener add(TreeListener a, TreeListener b) {
        return (TreeListener)addInternal(a, b);
    }

    /**
     * Adds item-listener-a with item-listener-b and
     * returns the resulting multicast listener.
     * @param a item-listener-a
     * @param b item-listener-b
     */
    public static CellListener add(CellListener a, CellListener b) {
        return (CellListener)addInternal(a, b);
    }

    /**
     * Adds adjustment-listener-a with adjustment-listener-b and
     * returns the resulting multicast listener.
     * @param a adjustment-listener-a
     * @param b adjustment-listener-b
     */
    public static AdjustmentListener add(AdjustmentListener a, AdjustmentListener b) {
        return (AdjustmentListener)addInternal(a, b);
    }

    /**
     * Removes the old component-listener from component-listener-l and
     * returns the resulting multicast listener.
     * @param l component-listener-l
     * @param oldl the component-listener being removed
     */
    public static ComponentListener remove(ComponentListener l, ComponentListener oldl) {
    	return (ComponentListener) removeInternal(l, oldl);
    }

    /**
     * Removes the old focus-listener from focus-listener-l and
     * returns the resulting multicast listener.
     * @param l focus-listener-l
     * @param oldl the focus-listener being removed
     */
    public static FocusListener remove(FocusListener l, FocusListener oldl) {
    	return (FocusListener) removeInternal(l, oldl);
    }

    /**
     * Removes the old key-listener from key-listener-l and
     * returns the resulting multicast listener.
     * @param l key-listener-l
     * @param oldl the key-listener being removed
     */
    public static KeyListener remove(KeyListener l, KeyListener oldl) {
    	return (KeyListener) removeInternal(l, oldl);
    }

    /**
     * Removes the old mouse-listener from mouse-listener-l and
     * returns the resulting multicast listener.
     * @param l mouse-listener-l
     * @param oldl the mouse-listener being removed
     */
    public static MouseListener remove(MouseListener l, MouseListener oldl) {
    	return (MouseListener) removeInternal(l, oldl);
    }

    /**
     * Removes the old mouse-motion-listener from mouse-motion-listener-l
     * and returns the resulting multicast listener.
     * @param l mouse-motion-listener-l
     * @param oldl the mouse-motion-listener being removed
     */
    public static MouseMotionListener remove(MouseMotionListener l, MouseMotionListener oldl) {
    	return (MouseMotionListener) removeInternal(l, oldl);
    }

    /**
     * Removes the old window-listener from window-listener-l and
     * returns the resulting multicast listener.
     * @param l window-listener-l
     * @param oldl the window-listener being removed
     */
    public static WindowListener remove(WindowListener l, WindowListener oldl) {
    	return (WindowListener) removeInternal(l, oldl);
    }

    /**
     * Removes the old action-listener from action-listener-l and
     * returns the resulting multicast listener.
     * @param l action-listener-l
     * @param oldl the action-listener being removed
     */
    public static ActionListener remove(ActionListener l, ActionListener oldl) {
    	return (ActionListener) removeInternal(l, oldl);
    }

    /**
     * Removes the old item-listener from item-listener-l and
     * returns the resulting multicast listener.
     * @param l item-listener-l
     * @param oldl the item-listener being removed
     */
    public static ItemListener remove(ItemListener l, ItemListener oldl) {
    	return (ItemListener) removeInternal(l, oldl);
    }

    /**
     * Removes the old item-listener from item-listener-l and
     * returns the resulting multicast listener.
     * @param l item-listener-l
     * @param oldl the item-listener being removed
     */
    public static TreeListener remove(TreeListener l, TreeListener oldl) {
    	return (TreeListener) removeInternal(l, oldl);
    }

    /**
     * Removes the old item-listener from item-listener-l and
     * returns the resulting multicast listener.
     * @param l item-listener-l
     * @param oldl the item-listener being removed
     */
    public static CellListener remove(CellListener l, CellListener oldl) {
    	return (CellListener) removeInternal(l, oldl);
    }

    /**
     * Removes the old adjustment-listener from adjustment-listener-l and
     * returns the resulting multicast listener.
     * @param l adjustment-listener-l
     * @param oldl the adjustment-listener being removed
     */
    public static AdjustmentListener remove(AdjustmentListener l, AdjustmentListener oldl) {
    	return (AdjustmentListener) removeInternal(l, oldl);
    }

    /**
     * Returns the resulting multicast listener from adding listener-a
     * and listener-b together.
     * If listener-a is null, it returns listener-b;
     * If listener-b is null, it returns listener-a
     * If neither are null, then it creates and returns
     * a new GWTEventMulticaster instance which chains a with b.
     * @param a event listener-a
     * @param b event listener-b
     */
    protected static EventListener addInternal(EventListener a, EventListener b) {
    	if (a == null)  return b;
    	if (b == null)  return a;
    	return new GWTEventMulticaster(a, b);
    }

    /**
     * Returns the resulting multicast listener after removing the
     * old listener from listener-l.
     * If listener-l equals the old listener OR listener-l is null,
     * returns null.
     * Else if listener-l is an instance of GWTEventMulticaster,
     * then it removes the old listener from it.
     * Else, returns listener l.
     * @param l the listener being removed from
     * @param oldl the listener being removed
     */
    protected static EventListener removeInternal(EventListener l, EventListener oldl) {
    	if (l == oldl || l == null) {
    	    return null;
    	} else if (l instanceof GWTEventMulticaster) {
    	    return ((GWTEventMulticaster)l).remove(oldl);
    	} else {
    	    return l;		// it's not here
    	}
    }

    /**
     * Handles the textValueChange event by invoking the
     * textValueChange methods on listener-a and listener-b.
     * @param e the window event
     */
    public void textValueChanged(TextEvent e) {
        ((TextListener)a).textValueChanged(e);
        ((TextListener)b).textValueChanged(e);
    }

    /**
     * Adds text-listener-a with text-listener-b and
     * returns the resulting multicast listener.
     * @param a text-listener-a
     * @param b text-listener-b
     */
    public static TextListener add(TextListener a, TextListener b) {
        return (TextListener)addInternal(a, b);
    }

    /**
     * Removes the old component-listener from component-listener-l and
     * returns the resulting multicast listener.
     * @param l component-listener-l
     * @param oldl the component-listener being removed
     */
    public static TextListener remove(TextListener l, TextListener oldl) {
    	return (TextListener) removeInternal(l, oldl);
    }

}
