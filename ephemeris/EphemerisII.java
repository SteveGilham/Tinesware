//import package UK.co.demon.windsong.tines.?

import java.awt.*;
import java.applet.*;
import java.io.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/** 
*  Class EphemerisII
*
*  This is a simple Gloranthan planetarium
*  It is implemented as a stand-alone Application, which has been
*  derived from Applet, along the lines of the framework in Main.java
*
*
*  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1997
*  All rights reserved.  For full licence details see file Main.java
*
* @author Mr. Tines
* @version 1.0 11-Oct-1997
*
*/

class E2HitAnimate extends Animation implements MouseListener, MouseMotionListener{
	public E2HitAnimate(Animated m)
	{
		super(m);
	}

   private Frame hitframe = new Frame("Star finder");
   private TextField hitfield = null;

   public void doHitfield(MouseEvent e)
   {
	    if(null == hitfield)
	    {
		    hitfield = new TextField(30);
		    hitframe.add("Center",hitfield);
		    hitframe.pack();
      }
	    if(((EphemerisIIAnimation)movingImage).hit(e, hitfield))
	 	    hitframe.show();
	    else
		    hitframe.setVisible(false);
   }

  public void mouseClicked(MouseEvent e) {
    //TODO: implement this java.awt.event.MouseListener method;
  }

  public void mousePressed(MouseEvent e) {
    //TODO: implement this java.awt.event.MouseListener method;
  }

  public void mouseReleased(MouseEvent e) {
    //TODO: implement this java.awt.event.MouseListener method;
  }

  public void mouseEntered(MouseEvent e) {
    doHitfield(e);
  }

  public void mouseExited(MouseEvent e) {
    hitframe.setVisible(false);
  }

  public void mouseDragged(MouseEvent e) {
    //TODO: implement this java.awt.event.MouseMotionListener method;
  }

  public void mouseMoved(MouseEvent e) {
    doHitfield(e);
  }
}

public class EphemerisII extends Applet implements Runnable
{
   Thread appletDynamic;
   E2HitAnimate engine;
   EphemerisIIAnimation ac;
	double spin = 0.0;
	EphemerisIIControlPanel controls;
   boolean master = false;
   static EphemerisIIFrame window;
   EphemerisIIFrame display;

   /**
   * Main initialization
   * @see Applet#init
   */
   public void init()
   {
	     ac = new EphemerisIIAnimation();
	     engine = new E2HitAnimate(ac);
	     Panel buttonbar = new Panel();

	     Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	     controls = new EphemerisIIControlPanel(buttonbar);
	     ac.setAngles(spin,0.0);
	     ac.associate(controls);
	     Panel dummy = new Panel();
	     dummy.add(new Label(" "));
	     dummy.setBackground(Color.lightGray);

	     if(master)
	     {
		       window.setSize(d.width/2, d.height/2);
		       window.show();

		       // needed for java in JDK 1.1
		       engine.setSize(d.width/2, d.height/2);

		       window.setLayout(new BorderLayout());
		       window.add("Center",engine);
		       window.add("East",controls);
		       window.add("South",buttonbar);
		       window.add("North",dummy);
		       window.associate(ac);
           controls.associate(window);
		       display = null;
       }
	     else
	     {
		       display = new EphemerisIIFrame("Gloranthan Ephemeris", false);
		       display.setSize(d.width/2, d.height/2);
		       display.show();

		       // needed for java in JDK 1.1
		       engine.setSize(d.width/2, d.height/2);

		       display.setLayout(new BorderLayout());
		       display.add("Center",engine);
		       display.add("East",controls);
		       display.add("South",buttonbar);
		       display.add("North",dummy);
		       display.associate(ac);
           controls.associate(display);
       }
   }

   /**
   * Main (re-)start; thread is
   * fired up at this point.
   */
   public void start()
   {
	if(display != null) display.show();
   	if(null == appletDynamic)
      {
      	appletDynamic = new Thread(this);
         	appletDynamic.start();
      }
   }

   /**
   * Main graceful suppression (iconise,
   * leave page or whatever).  Called before
   * destroy()
   */
   public void stop()
   {
	    if(display != null) display.setVisible(false);
   	  if(null != appletDynamic)
      {
         appletDynamic.stop();
         appletDynamic = null;
      }
   }

   /**
   * Main final termination and tidy
   */
   public void destroy()
   {
   }

   /**
   * Output to screen
   * @param g Graphic to which to draw
   */
   public void paint(Graphics g)
   {
	engine.repaint();
   }

   /**
   * work routine
   */
   public void run()
   {
      for(;;)
      {
	      ac.setSize(getSize());
	      controls.tick();
	      engine.repaint();
        try{ Thread.sleep(1000);}
        catch(InterruptedException ignored){}
      }
   }

   /**
   * Return parameter details
   * @see Applet.getParameterInfo
   */
   public String[][] getParameterInfo()
   {
	String[][]t = {{"None"},{"N/A"},{"This applet is purely GUI driven"}};
	return t;
   }

   /**
   * Applicationizer function
   */
   public static void main(String [] args)
   {
      	EphemerisII self = new EphemerisII();
		    self.master = true;
		    window = new EphemerisIIFrame("Gloranthan Ephemeris", true);

		    window.setLayout(new BorderLayout());
		    window.setBackground(Color.lightGray);
		    window.add("North", self);
      	self.init();
		    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		    window.setSize(d.width/2, d.height/2);
      	window.show();
      	self.run();
      	self.stop();
      	self.destroy();
   }

}

/* end of file EphemerisII.java */

