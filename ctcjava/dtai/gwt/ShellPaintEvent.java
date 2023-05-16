/****************************************************************
 **  Copyright 1997 by DTAI Incorporated, All Rights Reserved  **
 ****************************************************************
 **
 **  $Id: ShellPaintEvent.java,v 1.2 1997/10/01 17:06:54 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/ShellPaintEvent.java,v $
 **
 ****************************************************************/

package dtai.gwt;

import java.awt.Rectangle;
import java11.util.EventObject;

/**
 * Class ShellPaintEvent
 * @version 1.1
 * @author  DTAI, inc.
 */
public class ShellPaintEvent extends EventObject {

	public static final int REGION_UPDATED = 0;

	private int id;
	private Rectangle region;

	/**
	 * Constructor for ShellPaintEvent
	 * @param source        The object that generated the event
	 * @param id            This event's id
	 * @param region
	 */
	public ShellPaintEvent(Object source, int id, Rectangle region) {
		super(source);
		this.id = id;
		this.region = region;
	}

	/**
	 * Returns the Region variable
	 * @return region
	 */
	public final int getID() {
		return id;
	}

	/**
	 * Returns the Region variable
	 * @return region
	 */
	public final Rectangle getRegion() {
		return region;
	}
}
