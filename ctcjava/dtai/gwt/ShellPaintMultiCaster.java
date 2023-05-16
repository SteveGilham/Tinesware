/****************************************************************
 **  Copyright 1997 by DTAI Incorporated, All Rights Reserved  **
 ****************************************************************
 **
 **  $Id: ShellPaintMultiCaster.java,v 1.2 1997/10/01 17:06:54 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/ShellPaintMultiCaster.java,v $
 **
 ****************************************************************/

package dtai.gwt;

import java11.util.EventListener;

/**
 * Class ShellPaintMultiCaster
 * @version 1.1
 * @author  DTAI, inc.
 */
public class ShellPaintMultiCaster extends MultiCaster implements ShellPaintListener{

	/**
	 * Constructor for ShellPaintMultiCaster
	 * @param a           The first listener
	 * @param b           The second listener
	 */
	public ShellPaintMultiCaster(EventListener a, EventListener b) {
		super(a, b);
	}

	/**
	 * @param e   The ShellPaintMult event
	 */
	public void regionUpdated(ShellPaintEvent e) {
		((ShellPaintListener)a).regionUpdated(e);
		((ShellPaintListener)b).regionUpdated(e);
	}

	/**
	 * Adds the two listeners
	 * @param a           The first listener
	 * @param b           The second listener
	 * @return            New multiCaster listener after adding a + b
	 */
	public static ShellPaintListener add(ShellPaintListener a, ShellPaintListener b) {
		return (ShellPaintListener)addInternal(a, b);
	}

	/**
	 * Removes listener old from listener l
	 * @param l           The first listener
	 * @param old         The old listener
	 * @return            New multiCaster listener after removing old from l
	 */
	public static ShellPaintListener remove(ShellPaintListener l, ShellPaintListener oldl) {
		return (ShellPaintListener)removeInternal(l, oldl);
	}
}
