//package UK.co.demon.windsong.tines.util

import java.awt.*;
/** 
*  Class Animation
*
*  A double-buffered Canvas
*
*  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1997
*  All rights reserved.  For full licence details see file Main.java
*
* @author Mr. Tines
* @version 1.0 21-Jul-1997
*
*/

public class Animation extends Canvas
{
   private Dimension imageSize;
   private Image offscreen;
   protected Animated movingImage;

   private Animation()
   {
   }

   /**
   * The constructor sets everything up
   * @param m Animated thing to display with double buffering
   */
   public Animation(Animated m)
   {
	    super();
      movingImage = m;
      imageSize = new Dimension(0,0);
      offscreen = null;
   }

   /**
   * The double buffered action
   * @param g Graphics context to draw to
   */
   public void update(Graphics g)
   {
	    Dimension d = getSize();
	    if( (d.width != imageSize.width) || (d.height != imageSize.height))
	    {
		    imageSize = d;
		    if(offscreen != null) offscreen.flush();
		    offscreen = createImage(d.width, d.height);
		    System.gc();
	    }
	    if(offscreen == null)
	    {
		    g.setColor(getBackground());
		    g.fillRect(0,0,imageSize.width, imageSize.height);
		    return;
	    }
	    movingImage.setSize(imageSize);
	    offscreen.flush();
	    System.gc();

	    Graphics off = offscreen.getGraphics();
	    off.setColor(getBackground());
	    off.fillRect(0,0,imageSize.width, imageSize.height);
	    movingImage.paint(off);
	    g.drawImage(offscreen,0,0,this);
   }
}

/* end of file Animation.java */

