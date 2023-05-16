/****************************************************************
 **
 **  $Id: TipGadget.java,v 1.20 1997/08/06 23:27:13 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/TipGadget.java,v $
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

/**
 * TipGadget
 * @version 1.1
 * @author DTAI, Incorporated
 */
public class TipGadget extends BorderGadget {

    private static Color defaultBackground = new Color( 255, 255, 204 );
    private LabelGadget labelGadget;

    /**
     * Constructs a Label with no label.
     */
	public TipGadget() {
		this( "" );
	}

    /**
     * Constructs a Label with the specified label.
     * @param label - the label of the button
     */

	public TipGadget(String label) {
		setBorderType( BorderGadget.LINE );
		setBorderThickness( 1 );
		setMargins( 1 );
		setNormalBackground( defaultBackground );
		setForeground( Color.black );
		add("Center", labelGadget = new LabelGadget(label) );
	}

	/**
	 * sets the label of the button
	 * @param label - TBD
	 */
	public void setLabel( String label ) {
	    labelGadget.setLabel(label);
	}

	/**
	 * getLabels the label of the button
	 * @return String
	 */
	public String getLabel() {
	    return labelGadget.getLabel();
	}

	/**
	 * contains
	 * @param x - TBD
	 * @param y - TBD
	 * @param exclude - TBD
	 * @return boolean
	 */
	public boolean contains(int x, int y, Gadget exclude) {
	    return false;
	}

}
