/****************************************************************
 **
 **  $Id: GadgetClipboard.java,v 1.6 1997/08/06 23:27:03 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/GadgetClipboard.java,v $
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
 * GadgetClipboard
 * @version 1.1
 * @author DTAI, Incorporated
 */
public class GadgetClipboard {

    static private String clipboardText;

    /**
     * gets the text in the clipboard
     * @return String
     */
    public static String getText() {
        if ( clipboardText == null ) {
            return "";
        }
        return clipboardText;
    }

    /**
     * setText
     * @param text - TBD
     */
    public static synchronized void setText( String text ) {
        clipboardText = text;
    }
}
