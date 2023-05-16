/****************************************************************
 **  Copyright 1997 by DTAI Incorporated, All Rights Reserved  **
 ****************************************************************
 **
 **  $Id: ShellPaintListener.java,v 1.1 1997/10/01 16:51:33 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/ShellPaintListener.java,v $
 **
 ****************************************************************/

package dtai.gwt;

import java11.util.EventListener;

/**
 * Interface ShellPaintListener
 * @version 1.1
 * @author  DTAI, inc.
 */
public interface ShellPaintListener extends EventListener {

	/**
	 * @param e     The ShellPaint event
	 */
	public abstract void regionUpdated(ShellPaintEvent e);
}
