//package UK.co.demon.windsong.tines.util

import java.awt.*;
/** 
*  Interface Actor 
*
*  A JDK-version-independent equivalent to ActionListener
*
*  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1997
*  All rights reserved.  For full licence details see file Main.java
*
* @author Mr. Tines
* @version 1.0 21-Jul-1997
*
*/

public interface Actor
{
   /**
   * The instantiator must have some obvious action
   * such as that by which it will respond to a button press.
   * @param b Component which triggered the action
   */
   public void doIt(Component b);
}

/* end of file Actor.java */

