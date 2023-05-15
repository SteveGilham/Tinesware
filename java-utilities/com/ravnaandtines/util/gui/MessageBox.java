package com.ravnaandtines.util.gui;

import java.awt.*;
import java.util.*;

/**
*  Class MessageBox - Generic modal dialog box utility class for warnings and such
*  Puts a Label's worth of text up, along with some decorations
*  and buttons to act on the information.  Pure AWT, no Swing
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
* @version 1.0 31-Jul-1997
* @version 1.1 1-Dec-1997 misc tidying
* @version 1.2 8-May-1998 Java 1.1, tidying, CTC
* @version 1.3 28-Dec-1998 raw AWT - easy enough to revert to 1.0.2 ActionButton/Actor
* @version 1.4 3-Jan-1999 Icon control, not just big letters
*/
public class MessageBox
{
    private static ResourceBundle res =
        ResourceBundle.getBundle("com.ravnaandtines.util.gui.MBox");

    /**
    * Dialog button type
    */
    public static final int YES = 0;
    /**
    * Dialog button type
    */
    public static final int NO = 1;
    /**
    * Dialog with YES and NO button types
    */
    public static final int YES_NO = 2;
    /**
    * Dialog with YES, NO and CANCEL button types
    */
    public static final int YES_NO_CANCEL = 3;
    /**
    * Dialog type with simple OK button
    */
    public static final int INFO = 4;
    /**
    * Dialog type with simple OK button
    */
    public static final int WARN = 5;
    /**
    * Dialog type with simple OK button
    */
    public static final int ERROR = 6;
    private static String[] yn = {res.getString("Yes"), res.getString("No")};

    private static String[] ync = {res.getString("Yes"),
            res.getString("No"), res.getString("Cancel")};

    private static String[] ok = {res.getString("OK")};

    private Component innerC = null;

    private int type = ERROR;

    private Frame f = null;
    private String title = null;
    private String message = null;
    private int result = -1;
    private Component icon = null;

    private StickUp box = null;

    /**
    * default constructor
    */
    public MessageBox()
    {
    }

    /**
    * @param parent frame with respect to which to be modal
    */
    public void setFrame(Frame parent)
    {
        f = parent;
    }

    /**
    * @param t String to use for dialog caption
    */
    public void setTitle(String t)
    {
        title = t;
    }

    /**
    * @param i Component to use for message box icon
    * (instead of i,?,!, or X depending on severity)
    */
    public void setIcon(Component c)
    {
        icon = c;
    }

    /**
    * @param t String to use for dialog message
    */
    public void setMessage(String m)
    {
        message = m;
    }

    /**
    * @return String used for dialog caption
    */
    public String getTitle()
    {
        return title;
    }

    /**
    * @return String used for dialog message
    */
    public String getMessage()
    {
        return message;
    }

    /**
    * @param t Type of icon to use
    */
    public void setType(int t)
    {
        if(t>=YES_NO && t <=ERROR) type = t;
    }

    /**
    * @param c Additional component to use as well as the message
    */
 	public void addInnerComponent(Component c)
    {
        innerC = c;
    }

    /**
    * makes the message box visible
    */
    public void show()
    {
        int boxtype = StickUp.ERROR;
        String [] label = ok;
        switch(type)
        {
            case YES_NO:
             label = yn;
             boxtype = StickUp.QUERY;
             break;
            case YES_NO_CANCEL:
             label = ync;
             boxtype = StickUp.QUERY;
        }
        box = new StickUp(f, boxtype, message, label, icon);
        if(null != title) box.setTitle(title);
        if(null != innerC) box.addInnerComponent(innerC);

        box.show();
        result = box.getResult();
        box.dispose();
        box = null;
    }

    /**
    * @return Button index used to dismiss dialog (-1 for frame dismissal)
    */
    public int getResult()
    {
        return result;
    }
}


