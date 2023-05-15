package com.ravnaandtines.util.event;

import java.util.*;
/**
*  Class StatusEvent - custom informational event class
* <p>
* Copyright Mr. Tines &lt;tines@windsong.demon.co.uk&gt; 1998
* <p>
* For the purposes of the licence, this source file and the minimum set of
* other source files in package com.ravnaandtines.util and its sub-
* packages required to link together to satisfy all class
* references not belonging to standard Javasoft-published APIs constitute
* the library.  Thus it is not necessary to distribute source to classes
* that you do not actually use.
* <p>
* Note that Java's dynamic class loading means that the distribution of class
* files (as is, or in jar or zip form) which use this library is sufficient to
* allow run-time binding to any interface compatible version of this library.
* The GLPL is thus far less onerous for Java than for the usual run of 'C'/C++
* library.
* <p>
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Library General Public
* License as published by the Free Software Foundation; either
* version 2 of the License, or (at your option) any later version.
* <p>
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Library General Public License for more details.
* <p>
* You should have received a copy of the GNU Library General Public
* License along with this library; if not, write to the Free
* Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*<p>
* @author Mr. Tines
* @version 1.0 28-Dec-1998
*/

public class StatusEvent extends EventObject
{
    private static int max = 0;
    private static ResourceBundle res =
        ResourceBundle.getBundle("com.ravnaandtines.util.event.Status");

    /**
    * How many degrees of status event are there?
    * @return the number of event types
    */
    public static final int size() { return max; }

    /**
    * Status event type pseudo-enumeration class
    */
    public static final class Level {
        private String id;
        public final int ord;
        Level(String anID)
        {
            id = anID;
            ord = max++;
        }
        /**
        * Status event type to string conversion
        */
        public String toString() { return id; }
    }

    /**
    * On-going state information (no urgency)
    */
    public static final Level STATUS = new Level(res.getString("Status"));
    /**
    * On-going state information to which the user should respond
    */
    public static final Level INFO = new Level(res.getString("Info"));
    /**
    * Something has happened that may require user intervention
    */
    public static final Level WARN = new Level(res.getString("Warning"));
    /**
    * Something has gone badly wrong, and the user should be notified
    */
    public static final Level ERROR = new Level(res.getString("Error"));
    /**
    * Something has happened that prevents further useful operation
    */
    public static final Level FATAL = new Level(res.getString("Fatal_Error"));
    /**
    * Something has happened that prevents further operation
    */
    public static final Level CRASH = new Level(res.getString("Crash"));

    /**
    * Associated message
    */
    protected String message = null;

    /**
    * level of event
    */
    protected Level level = WARN;

    /**
    * Constructs a new status event object
    * @param source the event is in the context of this object
    * @param level severity of event to raise
    * @param message associated user readable data
    */
    public StatusEvent(Object source, Level level, String message)
    {
        super(source);
        this.level = level;
        this.message = message;
    }

    /**
    * Acessor for event message
    * @return event source information
    */
    public String getMessage()
    {
        return message;
    }

    /**
    * Acessor for event severity
    * @return event severity
    */
    public Level getLevel()
    {
        return level;
    }

    /**
    * Gets a text representation of the object 
    * @return event severity and source information
    */
    public String toString()
    {
        return super.toString()+" : "+getMessage();
    }
} 