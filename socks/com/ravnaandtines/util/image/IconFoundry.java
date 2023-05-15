package com.ravnaandtines.util.image;

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import com.ravnaandtines.util.lang.Plugin;
import com.ravnaandtines.util.image.icons.Tines;

/**
*  Class IconFoundry - serves up a standard icon set
* <p>
* Copyright Mr. Tines &lt;tines@windsong.demon.co.uk&gt; 1998
* All rights reserved.
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
* Library General Public License for more details."+NL+
* <p>
* You should have received a copy of the GNU Library General Public
* License along with this library; if not, write to the Free"
* Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*<p>
* @author Mr. Tines
* @version 1.0 08-Nov-1998
*
*/

public class IconFoundry
{
    /**
    * Copy Icon tag
    */
    public static final int COPY = 0;
    private static ResourceBundle res =
        ResourceBundle.getBundle("com.ravnaandtines.util.image.IconClasses");
    /**
    * Incoming Mail Icon tag
    */
    public static final int MAILIN = 1;
    /**
    * Cut Icon tag
    */
    public static final int CUT = 2;
    /**
    * Outgoing Mail Icon tag
    */
    public static final int MAILOUT = 3;
    /**
    * New document Icon tag
    */
    public static final int FILENEW = 4;
    /**
    * Folder open Icon tag
    */
    public static final int FILEOPEN = 5;
    /**
    * Diskette save Icon tag
    */
    public static final int FILESAVE = 6;
    /**
    * Help Icon tag
    */
    public static final int HELP = 7;
    /**
    * small Tines Icon tag
    */
    public static final int ICON = 8;
    /**
    * Info Icon tag (CTC/Angerona overlaods to mean IDEA)
    */
    public static final int INFO = 9;
    /**
    * Paste Icon tag
    */
    public static final int PASTE = 10;
    /**
    * Padlock Icon tag
    */
    public static final int LOCK = 11;
    /**
    * Printer Icon tag
    */
    public static final int PRINT = 12;
    /**
    * Warning triangle Icon tag
    */
    public static final int WARN = 13;
    /**
    * open Padlock Icon tag
    */
    public static final int UNLOCK = 14;
    /**
    * yale key Icon tag
    */
    public static final int KEY = 15;
    /**
    * Green Ok check Icon tag
    */
    public static final int TICK = 16;
    /**
    * Red not-ok check Icon tag
    */
    public static final int CROSS = 17;
    /**
    * Person Icon tag
    */
    public static final int USER = 18;
    /**
    * number of images
    */
    private static final int MAX = 19;

    /**
    * local static array of already constructed images
    * "here is one I prepared earlier"
    */
    private static Image[] icons = new Image[MAX];

    /**
    * local static array of already constructed classes
    * "here is one I prepared earlier"
    */
    private static Class[] iconClasses = new Class[MAX];

    /**
    * have we loaded the classes yet?
    */
    private static boolean loaded = false;

    /**
    * finds a Class for each of the icon of interest if possible
    */
    private static synchronized void load()
    {
        if(loaded) return;
        iconClasses[WARN] = Plugin.find(res, "Warn");
        iconClasses[FILENEW] = Plugin.find(res, "Filenew");
        iconClasses[COPY] = Plugin.find(res, "Copy");
        iconClasses[CUT] = Plugin.find(res, "Cut");
        iconClasses[FILEOPEN] = Plugin.find(res, "Fileopen");
        iconClasses[FILESAVE] = Plugin.find(res, "Filesave");
        iconClasses[HELP] = Plugin.find(res, "Help");
//        iconClasses[ICON] = find("Tines");
        iconClasses[INFO] = Plugin.find(res, "Info");
        iconClasses[PASTE] = Plugin.find(res, "Paste");
        iconClasses[PRINT] = Plugin.find(res, "Print");
        iconClasses[MAILIN] = Plugin.find(res, "Mailin");
        iconClasses[MAILOUT] = Plugin.find(res, "Mailout");
        iconClasses[LOCK] = Plugin.find(res, "Lock");
        iconClasses[UNLOCK] = Plugin.find(res, "Unlock");
        iconClasses[KEY] = Plugin.find(res, "Key");
        iconClasses[TICK] = Plugin.find(res, "Tick");
        iconClasses[CROSS] = Plugin.find(res, "Cross");
        iconClasses[USER] = Plugin.find(res, "User");
        loaded = true;
    }


    /**
    * Obtain a desired image by its tag
    * @see ImageProducer#isConsumer
    * @param ic ImageConsumer to query
    * @return boolean value, true if a registered consumer
    */
    public static Image getIcon(int num)
    {
        load();
        if(num < 0 || num >= MAX) num = WARN;

        if(null != icons[num])
        {
            return icons[num];
        }

        IconProducer im = null;

        if(num == ICON) im = new Tines(); else
        im = (IconProducer) Plugin.build(iconClasses[num]);

        return icons[num] = im.getImage();
	}
}

/* end of file IconImage.java */

