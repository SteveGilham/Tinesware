//package UK.co.demon.windsong.tines.awt102;
//import package UK.co.demon.windsong.tines.util;

import java.awt.*;

/** 
*  Class ActionButton
*
*  A self-listening button, this version under JDK 1.0 events 
*
*  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1997
*  All rights reserved.  For full licence details see file Main.java
*
* @author Mr. Tines
* @version 1.0 21-Jul-1997
*
*/

public class ActionButton extends Button
{
   Actor target;

   /**
   * Creates a button wired up ready to go
   * @see Button#Button
   * @param label String to put on the button
   * @param a Actor to fire off when the button is pressed
   */
   public ActionButton(String label, Actor a)
   {
	super(label);
      target = a;
   }

   /**
   * Listens for the action - JDK 1.0 style
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

/* end of file ActionButton.java */

