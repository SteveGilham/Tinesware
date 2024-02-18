//package UK.co.demon.windsong.tines.util

import java.awt.*;
/** 
*  Interface Animated
*
*  A thing that can be painted
*
*  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1997
*  All rights reserved.  For full licence details see file Main.java
*
* @author Mr. Tines
* @version 1.0 21-Jul-1997
*
*/

public interface Animated
{
   /**
   * The instantiator must have some obvious graphical
   * output to be sent to this place.
   * @param g Graphics to draw to
   */
   public void paint(Graphics g);
   /**
   * The instantiator must have some handle to the size 
   * of the area where output is to be sent.
   * @param d Dimension to draw to
   */
   public void setSize(Dimension d);
}

/* end of file Actor.java */

