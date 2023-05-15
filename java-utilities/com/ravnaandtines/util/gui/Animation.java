package com.ravnaandtines.util.gui;
import java.awt.*;

/**
*  Class Animation - A double-buffered Canvas used to display an Animated
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
public class Animation extends Canvas
{
   private Dimension imageSize;
   private Image offscreen;
   private Animated movingImage;

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

