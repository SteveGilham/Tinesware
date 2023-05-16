/****************************************************************
 **
 **  $Id: GadgetHelp.java,v 1.7 1997/08/06 23:27:05 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/GadgetHelp.java,v $
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
 * GadgetHelp
 */
public interface GadgetHelp {

    /**
     * Shows the help information for the Gadget.
     * @param gadget	the Gadget the help is for
     */
    public abstract void showHelp( Gadget gadget );

    /**
     * Shows any help information.
     * @param helpId	the arbitrary helpId
     */
    public abstract void showHelp( String helpId );
}
