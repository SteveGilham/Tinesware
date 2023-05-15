package com.ravnaandtines.util.awt102;
import java.awt.*;

/**
*  Class ActionCheckbox - A self-listening checkbox,, under JDK 1.0 events
* register an Actor with it to be notified of the state change
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
* @version 1.0 2-Aug-1997
*/
public class ActionCheckbox extends Checkbox
{
   private Actor target;

   /**
   * Create a Checkbox wired up ready to go
   * @see Checkbox#Checkbox
   * @param label String to put by the checkbox
   * @param a Actor to fire off when the checkbox is clicked
   */
   public ActionCheckbox(String label, Actor a)
   {
	  super(label);
      target = a;
   }

   /**
   * Listen for the action - JDK 1.0 style
   * @param e Event for action
   * @param o Object acted on#
   * @return boolean value describing whether the vent was handled
   */
   public boolean action(Event e, Object o)
   {
	  if(e.target != this) return false;
	  target.doIt(this);
	  return true;
   }

   /**
   * Alters the action
   * @param action Actor to use as new target
   */
   public void setActor(Actor action)
   {
	  target = action;
   }
}


