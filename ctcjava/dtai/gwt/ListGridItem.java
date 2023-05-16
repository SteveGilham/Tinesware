/****************************************************************
 **
 **  $Id: ListGridItem.java,v 1.2 1997/08/06 23:27:07 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/ListGridItem.java,v $
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

/**
 * ListGridItem
 * @version 1.1
 * @author DTAI, Incorporated
 */
class ListGridItem extends BorderGadget {

    ListGridGadget listgrid;

    /**
     * ListGridItem
     * @param listgrid
     * @param child
     * @param border
     */
    public ListGridItem( ListGridGadget listgrid, Gadget child, BorderGadget border ) {
        this.listgrid = listgrid;
        add("Center",child);
        setBorder(border);
		setSaveGraphicsForChildren(true);
		setPaintAnyOrder(true);
		setConsumingTransparentClicks(true);
    }

    /**
     * paint
     * @param g
     */
    public void paint( GadgetGraphics g ) {
        super.paint(g);
        if ( this == listgrid.focusGadget && listgrid.hasFocus() ) {
            drawFocus(g,getForeground(g),1);
        }
    }

    /**
     * getGadget
     * @return Gadget
     */
    public final Gadget getGadget() {
        return gadgets[0];
    }

    /**
     * hasFocus
     * @return boolean
     */
    public boolean hasFocus() {
        return false;
    }
}
