/****************************************************************
 **
 **  $Id: Lock.java.orig,v 1.1 1998/03/10 20:30:42 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/Lock.java.orig,v $
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
 * This class provides the API to return the global lock object.
 * It must be compiled under JDK 1.1, but can be used in 1.0.2
 * environments.  Why require 1.1 to compile?  Because we need to
 * use the standard JDK 1.1 AWT Component LOCK, if it is available.
 * Otherwise, there is a potential deadlock situation if two locks
 * are maintained (one for AWT and one for GWT).  If you are running
 * with 1.0.2, an exception will be caught and a new object will be
 * returned.
 *
 * @version 	1.1
 * @author 	DTAI, Incorporated
 */
public class Lock {
    private static Object LOCK;

    static {
        try {
            LOCK = new java.awt.Canvas().getTreeLock();
        } catch (Throwable t) {
            LOCK = new Object();
        }
    }

    public static final Object getTreeLock() {
        return LOCK;
    }
}
