/****************************************************************
 **
 **  $Id: GadgetCell.java,v 1.7 1997/08/06 23:27:03 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/GadgetCell.java,v $
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
 * GadgetCell
 * @version 1.1
 * @author   DTAI, Incorporated
 */
public final class GadgetCell {
    private int row;
    private int column;
    private Gadget gadget;

    /**
     * GadgetCell
     * @param row - TBD
     * @param column - TBD
     * @param gadget - TBD
     */
    public GadgetCell(int row, int column, Gadget gadget) {
        this.row = row;
        this.column = column;
        this.gadget = gadget;
    }

    /**
     * GadgetCell
     * @param cell - TBD
     */
    public GadgetCell(GadgetCell cell) {
        this.row = cell.row;
        this.column = cell.column;
        this.gadget = cell.gadget;
    }

    /**
     * toString
     * @return String
     */
    public String toString() {
        return "[row="+row+", column="+column+", gadget="+gadget+"]";
    }

    /**
     * gets the row
     * @return int
     */
    public final int getRow() {
        return row;
    }

    /**
     * gets the column
     * @return int
     */

    public final int getColumn() {
        return column;
    }

    /**
     * gets the gadget
     * @return Gadget
     */
    public final Gadget getGadget() {
        return gadget;
    }
}
