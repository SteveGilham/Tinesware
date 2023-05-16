/****************************************************************
 **
 **  $Id: EventObject.java,v 1.5 1997/08/06 23:25:17 cvs Exp $
 **
 **  $Source: /cvs/classes/java11/util/EventObject.java,v $
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

package java11.util;

public class EventObject extends Object {

    protected Object source;

    public EventObject() {
    }

    public EventObject(Object source) {
        this.source = source;
    }

    public void setSource( Object source ) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }

    public String toString() {
        return super.toString();
    }
}
