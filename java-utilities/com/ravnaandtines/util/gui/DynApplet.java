package com.ravnaandtines.util.gui;

import java.applet.*;
import java.awt.*;

/**
*  Class DynApplet -  Template for applets that compute indefinitely
* Pushes applet working into a separate thread.
* This is something that Applet ought really to
* have done directly!
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
* @version 1.0 21-Jul-1997
*/
public abstract class DynApplet extends Applet implements Runnable
{
	Thread appletDynamic = null;

   /**
   * DynApplet initialization
   * @see Applet#init
   */
   public void init()
   {
   	    appletDynamic = null;
   }

   /**
   * DynApplet (re-)start; thread is
   * fired up at this point.
   */
   public void start()
   {
   	    if(null == appletDynamic)
        {
      	    appletDynamic = new Thread(this);
            appletDynamic.start();
        }
   }

   /**
   * DynApplet graceful suppression (iconise,
   * leave page or whatever).  Called before
   * destroy()
   */
   public void stop()
   {
   	    if(null != appletDynamic)
        {
      	    appletDynamic.stop();
            appletDynamic = null;
        }
   }

   /**
   * Dynapplet final termination and tidy
   */
   public void destroy()
   {
   }

   /**
   * Applet output to screen
   * @param g Graphic to which to draw
   */
   public abstract void paint(Graphics g);

   /**
   * DynApplet work routine
   */
   public abstract void run();
}
