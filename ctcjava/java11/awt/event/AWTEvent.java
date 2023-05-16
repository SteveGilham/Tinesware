/****************************************************************
 **
 **  $Id: AWTEvent.java,v 1.2 1997/08/06 23:25:16 cvs Exp $
 **
 **  $Source: /cvs/classes/java11/awt/event/AWTEvent.java,v $
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

package java11.awt.event;

import java.awt.Event;
import java11.util.EventObject;

public class AWTEvent extends EventObject {

    private int id;
    protected Event evt;

    public AWTEvent() {
    }

    public AWTEvent( Event evt, int id ) {
        super( ( evt == null ) ? null : evt.target );
        this.id = id;
        this.evt = evt;
    }

    public final int getID() {
        return id;
    }

    public final Event getEvent() {
        if ( evt == null ) {
            return null;
        }
        return new Event( evt.target, evt.when, evt.id, evt.x, evt.y,
                          evt.key, evt.modifiers, evt.arg );
    }

    public boolean isConsumed() {
        return true;
    }
}
