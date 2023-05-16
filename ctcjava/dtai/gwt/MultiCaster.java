/****************************************************************
 **
 **  $Id: MultiCaster.java,v 1.1 1997/10/01 17:06:53 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/MultiCaster.java,v $
 **
 ****************************************************************/

package dtai.gwt;

import java11.awt.event.*;
import java11.util.EventListener;

public class MultiCaster implements EventListener {

    protected EventListener a, b;

    public MultiCaster(EventListener a, EventListener b) {
        this.a = a;
        this.b = b;
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
    	return new MultiCaster(a, b);
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
    	} else if (l instanceof MultiCaster) {
    	    return ((MultiCaster)l).remove(oldl);
    	} else {
    	    return l;		// it's not here
    	}
    }
}
