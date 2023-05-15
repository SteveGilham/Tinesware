//package UK.co.demon.windsong.tines.util
//import package UK.co.demon.windsong.tines.?
/** 
*  Class EphemerisIIAnimation
*
*  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1997
*  All rights reserved.  For full licence details see file Main.java
*
* @author Mr. Tines
* @version 1.0 11-Oct-1997
*
*/

import java.awt.*;
import java.util.*;
import java.awt.event.*;

public class EphemerisIIAnimation implements Animated
{
   private Dimension size;
   private int side, halfSide;
   private double spinAngle, tiltVector;
   private StarDome sky = new StarDome();
   private EphemerisIIControlPanel cf;
   private boolean fontData = false;
   private Font defFont, nFont;
   private char [] cardinal = {'N','E','W','S'};
   private int nfw[], nfh;
   private Graphics gc;
   //private int projection;
   private boolean up;
   private double look; 
   private double offset, bearing;
   private EphemerisIIFrame frame;
   private boolean start = true;

   /**
   * Allows the animation to find out what the date it
   * should display is
   * @param x EphemerisIIControlPanel giving the time
   */
   public void associate(EphemerisIIControlPanel x) {cf=x;}
   public void associate(EphemerisIIFrame x) {frame=x;}

   /**
   * default Constructor
   */
   public EphemerisIIAnimation()
   {
   }

   /**
   * Sets the current Dome configuration by two angles in radians
   * @param spin double gives the rotation about the pole
   * @param tilt double gives the tilt of the dome
   */
   public void setAngles(double spin, double tilt)
   {
          spinAngle = spin;
          tiltVector = tilt;
   }

   private Dimension sunpath(double angle)
   {
    // i) 3D coordinate is
    double y =  - Math.cos(angle) - offset*Math.sin(bearing);
    double x = Math.sin(angle)*Math.sin(tiltVector)
             - offset*Math.cos(bearing);
    double z = Math.sin(angle)*Math.cos(tiltVector);
    // ii) extract altitude and azimuth
    return getAltAz(x,y,z);
   }

   private Dimension southpath(double angle, double dm, double em)
   {
      double northpoint = (dm + Math.PI - em)/2.0; // where bisector is northmost
      double subtend = northpoint - dm;            // angle from bisector

      double ty = Math.cos(subtend);
      double tx = -Math.sin(subtend)*Math.cos(angle);
      double tz = Math.sin(subtend)*Math.sin(angle);

      double y = ty*Math.sin(northpoint)-tx*Math.cos(northpoint)
               - offset*Math.sin(bearing);
      double x = ty*Math.cos(northpoint)+tx*Math.sin(northpoint)
               - offset*Math.cos(bearing);
      return getAltAz(y,x,tz);
   }

   private Dimension getAltAz(double x, double y, double z)
   {
    double altitude, azimuth;
    int xx=1;
    if(up)
    {
          azimuth = Math.atan2(x, y);
          altitude = Math.atan2(Math.sqrt(x*x+y*y), z);
          altitude /= (Math.PI/2.0);
    }
    else
    {
         // look along line with angle = look as pole
         // rotate this to x axis (need to frig signs)
         double c = Math.cos(look);
         double s = Math.sin(look);
         double tx = x*c+y*s;
         double ty = x*s-y*c;
         double tz = z;
         // now use the x-axis as if it were z, z as y, y as x
         azimuth = Math.atan2(ty, tz) + Math.PI/2.0;
         altitude = Math.atan2(Math.sqrt(ty*ty+tz*tz), tx);
         altitude /= (Math.PI/2.0);
         if(tx < 0) return new Dimension(-1,-1);
		     xx = -1;
     }
     return new Dimension(
           side/2 + xx*(int)Math.rint(halfSide*altitude*Math.cos(azimuth)),
       	   side/2 - (int)Math.rint(halfSide*altitude*Math.sin(azimuth)));
   }


   private Dimension primitive(double pA, double pB)
   {
    // This is a thing that is distant pA from the axis,
    // with a given bearing pB from the north

    // 3D axis-relative coordinate is
    double sx = Math.sin(pA)*Math.sin(pB);
    double sy = Math.sin(pA)*Math.cos(pB);
    double sz = Math.cos(pA);

    // Apply tilt
    double ty = sy;
    double tx = sx*Math.cos(tiltVector)+sz*Math.sin(tiltVector);
    double tz =-sx*Math.sin(tiltVector)+sz*Math.cos(tiltVector);

    // apply offset viewing position
    tx -= offset*Math.cos(bearing);
    ty -= offset*Math.sin(bearing);

    return getAltAz(tx, ty, tz);
   }

   private Dimension spinpath(double angle, double twist)
   {
    // i) raw 3D coordinate is
    double tx = - Math.cos(angle);
    double ty = 0;
    double tz = Math.sin(angle);

    // ii) unapply tilt about x axis
    double y = ty;
    double x = tx*Math.cos(tiltVector)-tz*Math.sin(tiltVector);
    double z = tx*Math.sin(tiltVector)+tz*Math.cos(tiltVector);

    // iii) spin
    x = tx * Math.cos(twist) + ty * Math.sin(twist);
    y =-tx * Math.sin(twist) + ty * Math.cos(twist);
    z = tz;

    // iv) reapply tilt about x axis
    ty = y;
    tx = x*Math.cos(tiltVector)+z*Math.sin(tiltVector);
    tz =-x*Math.sin(tiltVector)+z*Math.cos(tiltVector);

    // v) apply offset viewing position
    tx -= offset*Math.cos(bearing);
    ty -= offset*Math.sin(bearing);
      return getAltAz(tx, ty, tz);
   }

   public boolean hit(MouseEvent e, TextField t)
   {
      int dx = e.getX()-(side/2);
	    int dy = e.getY()-(side/2);
	    if(!up && dy > 0) return false;
	    int r2 = dx*dx + dy*dy;
	    if(r2 > halfSide*halfSide) return false;

	    // Now reverse engineer location
	    setAngles(cf.spin(), cf.slide());
	    t.setText(sky.locate(e.getX(), e.getY(), halfSide, side/2,
		  spinAngle, tiltVector, offset, bearing,up,look));
	    return true;
   }

   private void rescale(double[] v)
   {
     // normalise
     double l = Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]);

     // so (0, yuthu, 0) + k*v = 1 at intersection
     // with the dome means that
     // 1 = (kvx)^2 + (kvz^2) + (kvy+yuthu)^2
     //   = (k^2)(vx^2+vz^2+vy^2) + 2k(vy.yuthu) + yuthu^2
     // k^2 +2k(vy.yuthu) -(1-yuthu^2) = 0
     // k = -(vy.yuthu) + _/(vy^2yuthu^2)+(1-yuthu^2)

     double b = v[1]*yuthu/l;
     double k = Math.sqrt(b*b+1.0-yuthu*yuthu) - b;

     // so position on the sky is
     v[0] = k*v[0]/l;
     v[1] = yuthu+k*v[1]/l;
     v[2] = k*v[2]/l;
   }

   private double yuthu;

   private void drawFrame(Graphics g)
	 {
        if(E2Param.yuthu) yuthu = Math.sin(Math.PI/20.0);
        else yuthu = (3.6/E2Param.domeRadius);

        Dimension df1=null, dt1, df2=null, dt2, df3=null, dt3;
		    g.setColor(Color.lightGray);

        // yuthu-centric coordinates of pole
        //double polex = 0.0;
        double poley = Math.sin(tiltVector)-yuthu;
        double polez = Math.cos(tiltVector);
        double yudist = Math.sqrt(/*polex*polex+*/poley*poley + polez*polez);
        double[] v = new double[3];

        // normalise
        poley/=yudist;
        polez/=yudist;

        // Northward unit vector
        //double nx = 0;
        double ny = polez;
        double nz = -poley;

        //Eastward unit vector
        // ex = 1, ey=0, ez=0

        // the first ring of the frame is 18 degrees radius
        // about the pole from yuthu, due north of centre,
        // the next, 63, the last 90
        double s1 = Math.sin(Math.PI/10.0);
        double s2 = Math.sin(7.0*Math.PI/20.0);
        double s3 = Math.sin(Math.PI/2.0);

        double c1 = Math.cos(Math.PI/10.0);
        double c2 = Math.cos(7.0*Math.PI/20.0);
        double c3 = Math.cos(Math.PI/2.0);
        double offx = offset*Math.cos(bearing);
        double offy = offset*Math.sin(bearing);

        for(int i=0; i<65; ++i)
        {

            char [] number = new char[3];

            double angle = i*Math.PI/32.0;
            double s = Math.sin(angle);
            double c = Math.cos(angle);

            // first ring vector
            v[0] = /*polex*c1*/ + s1*(c/* *ex + s * nx */);
            v[1] = poley*c1 + s1*(/* c*ey + */ s*ny);
            v[2] = polez*c1 + s1*(/* c*ez + */ s*nz);

            rescale(v);
            dt1 = getAltAz(v[1]-offx, v[0]-offy, v[2]);
            if(0 < i)
            {
               if(df1.width>0 && df1.height>0 && dt1.width>0 && dt1.height>0)
                   g.drawLine(df1.width, df1.height, dt1.width, dt1.height);
            } //0<i
            df1 = dt1;
            // draw line from axis to horizon and number wedges
            if(i%8 == 0)
			      {
               Dimension df = null, dt;
               for(int j=0; j<16; ++j)
               {
                   double sn = Math.sin(j/8.0);
                   double cn = Math.cos(j/8.0);
                   v[0] = sn*c; v[1] = poley*cn+sn*s*ny; v[2] = polez*cn+sn*s*nz;
                   rescale(v);
                   dt = getAltAz(v[1]-offx, v[0]-offy, v[2]);
                   if(0 < j)
                   {
                       if(df.width>0 && df.height>0 && dt.width>0 && dt.height>0)
                           g.drawLine(df.width, df.height, dt.width, dt.height);
                   }
                   df = dt;
               }// next j
            } // i%8 == 0
            if(i%8 == 4) // this gets to be rape&paste at its finest
            {
               Dimension dt;
               double sn = Math.sin(Math.PI/20.0);
               double cn = Math.cos(Math.PI/20.0);
               v[0] = -sn*c; v[1] = poley*cn-sn*s*ny; v[2] = polez*cn-sn*s*nz;
               rescale(v);
               dt = getAltAz(v[1]-offx, v[0]-offy, v[2]);
				       number[0] = (char)('1'+i/8);
               if(dt.width >0 && dt.height > 0)
                       g.drawChars(number,0,1,dt.width,dt.height);
            }

            // second ring vector
            v[0] = /*polex*c2*/ + s2*(c/* *ex + s * nx */);
            v[1] = poley*c2 + s2*(/* c*ey + */ s*ny);
            v[2] = polez*c2 + s2*(/* c*ez + */ s*nz);
            rescale(v);
            dt2 = getAltAz(v[1]-offx, v[0]-offy, v[2]);
            if(0 < i)
            {
              if(df2.width>0 && df2.height>0 && dt2.width>0 && dt2.height>0)
                 g.drawLine(df2.width, df2.height, dt2.width, dt2.height);
            }
            df2 = dt2;
            // draw line from axis to horizon and number wedges
            if(i%4 == 0)
			      {
                double delta = (2.0-Math.PI/10.0)/16.0;
                Dimension df = null, dt;
                for(int j=0; j<16; ++j)
                {
                    double sn = Math.sin(j*delta+Math.PI/10.0);
                    double cn = Math.cos(j*delta+Math.PI/10.0);
                    v[0] = sn*c; v[1] = poley*cn+sn*s*ny; v[2] = polez*cn+sn*s*nz;
                    rescale(v);
                    dt = getAltAz(v[1]-offx, v[0]-offy, v[2]);
                    if(0 < j)
                    {
                        if(df.width>0 && df.height>0 && dt.width>0 && dt.height>0)
                            g.drawLine(df.width, df.height, dt.width, dt.height);
                    }
                    df = dt;
                } // next j
            }//i%4 == 0
            if(i%4 == 2)
            {
                Dimension dt;
                double sn = Math.sin(Math.PI/5.0);
                double cn = Math.cos(Math.PI/5.0);
                v[0] = -sn*c; v[1] = poley*cn-sn*s*ny; v[2] = polez*cn-sn*s*nz;
                rescale(v);
                dt = getAltAz(v[1]-offx, v[0]-offy, v[2]);
  				      int house = (i/4)+9;
  				      number[0] = (char)('0'+house/10);
                if(house < 10) number[0] = ' ';
  				      number[1] = (char)('0'+house%10);
                if(dt.width >0 && dt.height > 0)
                       g.drawChars(number,0,2,dt.width,dt.height);
            }

            // third ring vector
            v[0] = /*polex*c3*/ + s3*(c/* *ex + s * nx */);
            v[1] = poley*c3 + s3*(/* c*ey + */ s*ny);
            v[2] = polez*c3 + s3*(/* c*ez + */ s*nz);
            rescale(v);
            dt3 = getAltAz(v[1]-offx, v[0]-offy, v[2]);
            if(0 < i)
            {
              if(df3.width>0 && df3.height>0 && dt3.width>0 && dt3.height>0)
                 g.drawLine(df3.width, df3.height, dt3.width, dt3.height);
            }
            df3 = dt3;
            // draw line from axis to horizon and number wedges
            if(i%2 == 0)
			      {
                double delta = (2.0-7.0*Math.PI/20.0)/16.0;
                Dimension df = null, dt;
                for(int j=0; j<16; ++j)
                {
                    double sn = Math.sin(j*delta+7.0*Math.PI/20.0);
                    double cn = Math.cos(j*delta+7.0*Math.PI/20.0);
                    v[0] = sn*c; v[1] = poley*cn+sn*s*ny; v[2] = polez*cn+sn*s*nz;
                    rescale(v);
                    dt = getAltAz(v[1]-offx, v[0]-offy, v[2]);
                    if(0 < j)
                    {
                        if(df.width>0 && df.height>0 && dt.width>0 && dt.height>0)
                            g.drawLine(df.width, df.height, dt.width, dt.height);
                    }
                    df = dt;
                }
            }
            if(i%2 == 1)
            {
                Dimension dt;
                double sn = Math.sin(17.0*Math.PI/40.0);
                double cn = Math.cos(17.0*Math.PI/40.0);
                v[0] = -sn*c; v[1] = poley*cn-sn*s*ny; v[2] = polez*cn-sn*s*nz;
                rescale(v);
                dt = getAltAz(v[1]-offx, v[0]-offy, v[2]);
  				      int house = (i/2)+25;
  				      number[0] = (char)('0'+house/10);
  				      number[1] = (char)('0'+house%10);
                if(dt.width >0 && dt.height > 0)
                       g.drawChars(number,0,2,dt.width,dt.height);
                sn = Math.sin(1.0+Math.PI/4.0);
                cn = Math.cos(1.0+Math.PI/4.0);
                v[0] = -sn*c; v[1] = poley*cn-sn*s*ny; v[2] = polez*cn-sn*s*nz;
                rescale(v);
                dt = getAltAz(v[1]-offx, v[0]-offy, v[2]);
  				      house = (i/2)+57;
  				      number[0] = (char)('0'+house/10);
  				      number[1] = (char)('0'+house%10);
                if(dt.width >0 && dt.height > 0)
                       g.drawChars(number,0,2,dt.width,dt.height);
            } // i%2 == 1
        }// next i
   } // end drawFrame

   /**
   * This draws the clock face and the times.
   * @param g Graphics to draw to
   */
   public synchronized void paint(Graphics g)
   {
       cf.setTime();
       gc = g;
       // get a large size font for cardinal points
       if(!fontData)
       {
           defFont = g.getFont();
           String fname = defFont.getName();
           int fsize = defFont.getSize()*2;
           nFont = new Font(fname, Font.BOLD, fsize);
           g.setFont(nFont);
           FontMetrics fm = g.getFontMetrics(nFont);
           nfh = fm.getHeight();
           nfw = new int[4];
           for(int nf =0; nf<4; ++nf)
              nfw[nf] = fm.charWidth(cardinal[nf]);
           g.setFont(defFont);
           fontData = true;
       }
       // draw the black background
       g.setColor(Color.black);
       g.fillRect(0,0,size.width,size.height);

       if(null == frame) return;
       // tweak the list box on the control panel
       // to ensure the default selection is visible

       if(start)
       {
           cf.adjust();
           start=false;
       }

       // get type of display
       up = cf.getUp();
       look = -cf.getLook();

       // Get crucial size information for display
       if(up) side = (size.width<size.height) ? size.width :size.height;
       else side = (size.width<2*size.height) ? size.width : 2*size.height;
       halfSide = (int) (0.45*(double)side);
       int margin = side/2 - halfSide;
       int x1 = margin, y1,x2,y2;
       int d1 = 2*halfSide;

       // Get the current Dome orientation
       setAngles(cf.spin(), cf.slide());
       int x,y;

       // Assume the Sun is not up
       boolean day = false;

       // Day length is 15/25 Dara Happan hours with 9 degrees tilt
       // and offset from 12.5 is proportional to tilt.
       // So as a fraction of a day +0.1 per 9 degrees or +1 for 90deg
       double dayLength =  E2Param.getDayLength(cf.getBaseHarmonic());
       double dawn = (1.0-dayLength)/2.0; // fraction of day after midnight


       // compute day/night state and colour dome appropriately
       int blue = 0;
       double twilength = 0.1*dayLength;

       if( cf.hour < (dawn-twilength)) {;}
       else if(cf.hour < dawn)
       {
	         blue = (int)(255.0*(1.0+(cf.hour - dawn)/twilength));
       }
       else if((1.0-cf.hour) > dawn)
       {
	         blue = 255;
    	     day=true;
       }
       else if( (1.0-cf.hour) > (dawn-twilength))
       {
	         blue = (int)(255.0*(1.0+(1.0-cf.hour - dawn)/twilength));
       }
       if(blue > 0 )
       {
           g.setColor(new Color(0,0,blue));
           if(up)g.fillOval(x1,x1,d1,d1);
           else g.fillArc(x1,x1,d1,d1,0,180);
       }

       // are stars to be shown? - should really fade these in
       boolean stars = (blue < 127) || frame.cbobscure.getState();

       g.setColor(Color.green);
       if(up)
       {
          // draw horizon and cardinal points
          for(int i=0; i<5;++i)
          {
             g.drawOval(x1,x1,d1,d1);
    	       --x1;
    	       d1+=2;
          }
          g.setFont(nFont);
          g.setColor(Color.white);
          x = (side-nfw[0])/2;
          y = margin-nfh/4;
          g.drawChars(cardinal, 0, 1, x, y);

          g.setColor(Color.yellow);
          x = margin-3*nfw[1]/2;
          y = (side+nfh)/2;
          g.drawChars(cardinal, 1, 1, x, y);

          g.setColor(Color.red);
          x = (side-margin+nfw[2]/2);
          y = (side+nfh)/2;
          g.drawChars(cardinal, 2, 1, x, y);

          g.setColor(Color.green);
          x = (side-nfw[3])/2;
          y = (side-margin + 3*nfh/4);
          g.drawChars(cardinal, 3, 1, x, y);

          // reset to small default font
          g.setFont(defFont);
       }
       else
       {
           for(int i=0; i<5;++i)
           {
    	         g.drawArc(x1,x1,d1,d1,0,180);
               g.drawLine(x1,x1+d1/2,x1+d1,x1+d1/2);
    	         --x1;
    	         d1+=2;
           }
           //   private char [] cardinal = {'N','E','W','S'};
           int span = 2*d1;
           int offset = -(int)Math.round(span*look/(Math.PI*2.0));
           int base = offset-span;
           int go;
           g.setFont(nFont);
           y = x1+d1/2+nfh+2;

           for(go=0; go<3; ++go, base+=span)
           {
		           if (Math.abs(base) <= d1/2+nfw[0])
		           {
     			         g.setColor(Color.white);
     			         x = x1+d1/2-(base)-nfw[0]/2;
     			         g.drawChars(cardinal, 0, 1, x, y);
               }
		           if (Math.abs(base-d1/2) <= d1/2+nfw[1])
		           {
     			         g.setColor(Color.yellow);
     			         x = x1+d1/2-(base-d1/2)-nfw[1]/2;
     			         g.drawChars(cardinal, 1, 1, x, y);
               }
		           if (Math.abs(base+d1/2) <= d1/2+nfw[2])
		           {
                   g.setColor(Color.red);
     			         x = x1+d1/2-(base+d1/2)-nfw[2]/2;
     			         g.drawChars(cardinal, 2, 1, x, y);
               }
		           if (Math.abs(base+d1) <= d1/2+nfw[3])
		           {
     			         g.setColor(Color.green);
     			         x = x1+d1/2-(base+d1)-nfw[3]/2;
     			         g.drawChars(cardinal, 3, 1, x, y);
               }
           } // next go

           g.setFont(defFont);
       }

       // drawing type
       //projection = cf.getProj();
       offset = cf.getOffset()*20.0/E2Param.domeRadius;
       bearing = -cf.getBearing();

       // draw star dome as required
       boolean names = frame.cbnames.getState();
       if(stars) sky.draw(g, spinAngle, tiltVector, halfSide,
                 side/2, side/2, names,
                 offset, bearing,up,look);


       // planets and such
       // Sunpath
       g.setColor(Color.yellow);
       if(frame.cbsunpath.getState())
       {
           Dimension p, c;
           p = sunpath(0.0);
           for(int i=1; i<51; ++i)
           {
               c = sunpath(i*Math.PI/50.0);
               if(p.width >0 && c.width>0 && p.height>0 && c.height>0)
                     g.drawLine(p.width,p.height,c.width,c.height);
               p = c;
           }
       }
       double hourAngle, h;
       Dimension xy;
       // Yelm & Lightfore
       if(day)//Yelm
       {
    	     hourAngle = Math.PI * (cf.hour-dawn)/dayLength;
           xy = sunpath(hourAngle);
           if(xy.width > 0 && xy.height > 0)
           {
               int sunsize = cf.cbring.getState() ?
                   Math.max(1, (int)Math.round(halfSide/180.0))
                   :12;
    	         g.fillOval(xy.width-sunsize/2,xy.height-sunsize/2,sunsize,sunsize);
               g.drawLine(xy.width,xy.height,xy.width,xy.height);
               if(names) label("Yelm", xy.width+12, xy.height);
           }
       }
       else
       {
    	     h = cf.hour;
    	     if(h > 0.5)
           {
               hourAngle = (h-(1.0-dawn))/(1.0-dayLength);
           }
           else
           {
               hourAngle = (h+dawn)/(1.0-dayLength);
           }
           hourAngle *= Math.PI;
           xy = sunpath(hourAngle);
           if(xy.width > 0 && xy.height > 0)
           {
               g.fillOval(xy.width-2,xy.height-2,4,4);
	             if(names) label("Lightfore", xy.width+4, xy.height);
           }

           // lightfore path
           if(frame.cblight.getState())
           {
               // current position is hA; each step represents (1.0-daylength)/50
               // of a turn of the sky; or a twist in radians of
               double k1 = (1.0-dayLength)*Math.PI/25.0;

               // we are a fraction (1.0-daylength)*hourAngle/PI through the
               // night; so that we have a twist since rising of
               double t0 = -2.0*(1.0-dayLength)*hourAngle - Math.PI/2.0;

               Dimension p, c;
               p = spinpath(0.0,t0);
               g.setColor(Color.lightGray);
               if(p.width >0 && p.height >0) g.fillOval(p.width-2,p.height-2,4,4);
               for(int i=1; i<51; ++i)
               {
                   c = spinpath(i*Math.PI/50.0,t0+i*k1);
                   if(p.width >0 && p.height >0 && c.width>0 && c.height >0)
                         g.drawLine(p.width,p.height,c.width,c.height);
                   p = c;
               }
           }
       } // sun
       double cycle;
       //Definition: 1 AU is 1/10 of the distance from the horizon to the Pole
       //Star on the equitilt days. I.e., it is 9 degrees = pi/20

       // Theya begins to rise exactly 5 Dara Happan hours (Theyalan: 4 hrs 48 min)
       // before Dawn, in all seasons. She travels upwards at a constant speed of
       // 1/2 AU per Dara Happan hour (Dara Happan: 4.5 degrees/hour, Theyalan:
       // 4.6875 degrees/hour), and is no longer visible once the Sun has risen.
       // pi/40 per DHhour or 25*pi/40 = 5pi/8 per day = (5/16)cycle per day

//       if((cf.hour >= dawn-0.20002) && (cf.hour <= dawn))
//       {
//         //cycle = (5/16)*(cf.hour-dawn+0.2)
//           cycle = (5*(cf.hour-dawn)+1.0)*Math.PI/8;
//           g.setColor(Color.white);
//		       xy = sunpath(cycle);
//           if(xy.width > 0 && xy.height > 0)
//           {
//             g.fillOval(xy.width-2,xy.height-2,4,4);
//		         if(names) label("Theya", xy.width+4, xy.height);
//           }
//       }

       //Assuming she sets at dusk and flips half way
       if(!day || frame.cbobscure.getState())
       {
          double fliptime = 0.4; //= ((dawn-0.2)+(1-dawn))/2.0;
          double cf_hour = cf.hour;
          if(cf_hour > 0.99999) cf_hour -= 1.0;
          if(cf_hour > fliptime) cf_hour = fliptime*2.0 - cf_hour;
           cycle = (5*(cf_hour-dawn)+1.0)*Math.PI/8;
           g.setColor(Color.white);
		       xy = sunpath(cycle);
           if(xy.width > 0 && xy.height > 0 && cf_hour >= (dawn-0.2))
           {
             g.fillOval(xy.width-2,xy.height-2,4,4);
		         if(names) label("Theya", xy.width+4, xy.height);
           }
       }

       //RAUSA, the Dusk Star
       //Rausa travels at the same speed as Theya. However, she always sets at
       //midnight, regardless of the season. When she is visible, even at
       //midwinter, she is always seen only when she is falling. Thus, on
       //Midwinter's night her height at Dusk (when she becomes visible) can be
       //calculated based on how far she could fall during 1/2 the night.
//       if((cf.hour > 1.0-dawn) || (cf.hour < 0.00001))
//       {
//           double cf_hour = cf.hour; if (cf_hour < 0.5) cf_hour +=1.0;
//         //cycle = (5/16)(cf.hour-1)+0.5;
//           cycle = (1+ (5*(cf_hour-1)/8)) * Math.PI;
//           g.setColor(Color.red);
//		       xy = sunpath(cycle);
//           if(xy.width > 0 && xy.height > 0)
//           {
//             g.fillOval(xy.width-2,xy.height-2,4,4);
//		         if(names) label("Rausa", xy.width+4, xy.height);
//           }
//       }

       // Assuming she rises at dawn and flips half-way
       if(!day || frame.cbobscure.getState())
       {
           double fliptime = dawn + (1.0-dawn)/2.0;
           double cf_hour = cf.hour;
           if(cf_hour < 0.00001) cf_hour += 1.0;
           if(cf_hour < fliptime) cf_hour = fliptime*2.0 - cf_hour;
           cycle = (1+ (5*(cf_hour-1)/8)) * Math.PI;
           g.setColor(Color.red);
		       xy = sunpath(cycle);
           if(xy.width > 0 && xy.height > 0 && cf_hour <=1.00001)
           {
             g.fillOval(xy.width-2,xy.height-2,4,4);
		         if(names) label("Rausa", xy.width+4, xy.height);
           }
       }

    // Kalikos - as per Elder secrets
    // Assume jumper rate, max at midnight
    if(stars)
    {
        double upAngle = -1;
        if(cf.hour < dawn) upAngle = (dawn-cf.hour)*5*Math.PI/8;
        else if (cf.hour > (1.0-dawn)) upAngle = (cf.hour-1.0+dawn)*5*Math.PI/8;
        if(upAngle > 0)
        {
            // i) 3D coordinate is
            double qy =  - Math.cos(upAngle)*Math.cos(0.2) - offset*Math.sin(bearing);
            double qx = Math.cos(upAngle)*Math.sin(0.2) - offset*Math.cos(bearing);
            double qz = Math.sin(upAngle);
            // ii) extract altitude and azimuth
            xy = getAltAz(-qy,qx,qz);
		        g.setColor(Color.white);
		        g.fillOval(xy.width-2,xy.height-2,4,4);
		        if(names) label("Kalikos", xy.width+4, xy.height);
        }
    }
       // entekos/moskalf - white; 31up, 31down
       // sets about dusk at autmn equinox yr 5
       // Autumn equinox is 135.5 days after midnight, dusk 135.75

       long dayno = cf.year*294+cf.week*7+cf.day;

	     long entekosOffset = 5*294+135;
	     cycle = 0.5+(((double)((dayno-entekosOffset)%62))+cf.hour-0.75)/62.0;
       if(cycle > 1.0) cycle -= 1.0;
	     if(stars && 0<=cycle && cycle <= 0.5)
	     {
		       g.setColor(Color.lightGray);
		       xy = sunpath(cycle*2.0*Math.PI);
           if(xy.width > 0 && xy.height > 0)
           {
		           g.fillOval(xy.width-3,xy.height-3,6,6);
		           if(names) label("Entekos", xy.width+6, xy.height);
           }
       }

	     // wagon/lokarnos 98up, 98 down
       //Lokarnos/Wagon rises at dusk on Freezeday of Disorder Week in all
       //seasons. He takes 7 days to cross the sky, and then spends another 7 days
       //in the Underworld. This is the case until sometime in the 900s, when his
       //speed begins to change.
       // in Year 1, rising is at midnight Fire/Movement/Earth (24:00h, that is)

       // if dayno before slowing...
       long wagonOffset = 152;
	     cycle = (((double)((dayno-wagonOffset)%14))+cf.hour)/14.0;
	     if(stars && 0<=cycle && cycle <= 0.5)
	     {
           g.setColor(Color.lightGray);
		       xy = sunpath(cycle*2.0*Math.PI);
           if(xy.width > 0 && xy.height > 0)
           {
                g.fillOval(xy.width-4,xy.height-4,8,8);
		            if(names) label("Lokarnos", xy.width+8, xy.height);
           }
       }


	     // uleria/mastakos 8hr up, 0 down; rises at 6pm at autumn equinox
       if(!E2Param.uleria)
       {
	        cycle = (cf.hour-0.75);  // cycle zero at 18:00
       }
       else  // perhaps 1/3 of a sidereal day as period.
       {
           cycle = cf.week*7+cf.day+cf.hour-135.75;
           cycle *= 294.0/293.0; // now sidereal days;
           cycle -= Math.floor(cycle);
       }
	     if(cycle < 0) cycle += 1.0;
	     cycle *= 3.0;
	     while(cycle > 1.0) cycle -= 1.0;
	     if(cycle < 0) cycle += 1.0;
	     cycle *= Math.PI;

       if(stars)
       {
	          g.setColor(blue>127 ? Color.cyan : Color.blue);
            xy = sunpath(cycle);
            if(xy.width > 0 && xy.height > 0)
            {
	              g.fillOval(xy.width-2,xy.height-2,4,4);
	              if(names) label("Uleria", xy.width+4, xy.height);
            }
       }

	// White orbiter goes round 29 times in 28 days
	if(cf.year > 1725)
	{
    drawOrbiter(g);
	}


	// South path and south path planets
	double dm,em;
  Dimension p;
	if(frame.cbsouthpath.getState())
	{
		em = eastMouth(dayno, cf.hour);
		dm = dodgeMouth(dayno, cf.hour);

    p = southpath(0.0, dm, em);
		g.setColor(Color.red);
    for(int i=1; i<51; ++i)
    {
        Dimension c = southpath(i*Math.PI/50.0, dm, em);
        if(p.width >0 && c.width>0 && p.height>0 && c.height>0)
                     g.drawLine(p.width,p.height,c.width,c.height);
        p = c;
    }
	}

	// Shargash 14d/14d - visible at day
	cycle = (((double)(dayno%28))+cf.hour-E2Param.shargashRise)/28.0;
  if(cycle < 0) cycle += 1.0;
	if(0<=cycle && cycle <= 0.5)
	{
		// mouth location at rising and setting
		em = eastMouth(dayno, cf.hour-28*cycle);
		dm = dodgeMouth(14 + dayno, cf.hour-28*cycle);
    p = southpath(cycle*2.0*Math.PI, dm, em);
    if(p.width > 0 && p.height > 0)
    {
       g.setColor(Color.red);
		   g.fillOval(p.width-4,p.height-4,8,8);
		   if(names) label("Shargash", p.width+8, p.height);
    }
	}

	// TwinStar 3d/3d
	cycle = (((double)(dayno%6))+cf.hour-E2Param.twinRise)/6.0;
  if(cycle < 0) cycle += 1.0;
	if(stars && 0<=cycle && cycle <= 0.5)
	{
		// mouth location at rising and setting
		em = eastMouth(dayno, cf.hour-6*cycle);
		dm = dodgeMouth(3 + dayno, cf.hour-6*cycle);
    p = southpath(cycle*2.0*Math.PI, dm, em);
    if(p.width > 0 && p.height > 0)
    {
		   g.setColor(Color.yellow);
		   g.fillOval(p.width-4,p.height,4,4);
		   g.setColor(Color.white);
		   g.fillOval(p.width,p.height+4,4,4);
		   if(names) label("TwinStars", p.width+8, p.height);
    }
	}

	// Artia 56d/56d but not in sacred time.
	// Interpret as season on, season off
  if(cf.week < 40)
  {
	    int parity = cf.year % 2;
      long qdayno = (cf.week*7+cf.day + 280*parity);
      cycle = (((double)(qdayno%112))+cf.hour-E2Param.artiaRise)/112.0;
      if(cycle < 0) cycle += 1.0;
	    if(stars && 0<=cycle && cycle <= 0.5)
	    {
         // mouth location at rising and setting
         qdayno = cf.year*294;
         double riseday = cf.week*7+cf.day+cf.hour - cycle*112.0;
         double setday = riseday + 56.0;

         if(riseday < 0) riseday -= 14.0;
         em = eastMouth(qdayno, riseday);

         if(setday > 280.0) setday += 14.0;
         dm = dodgeMouth(qdayno, setday);
         p = southpath(cycle*2.0*Math.PI, dm, em);
         if(p.width > 0 && p.height > 0)
         {
		        g.setColor(Color.red);
		        g.fillOval(p.width-2,p.height-2,4,4);
		        if(names) label("Artia", p.width+4, p.height);
         }
	    }
  }

	// Quasi-fixed quantities

  if(frame.cbframe.getState()) // Buserian's frame
  {
      drawFrame(g);
  }


	  // Zenith
    if(stars)
	  {
        double pA = 0.15*Math.PI;
        double pB = 4.0;
        double sx = Math.sin(pA)*Math.sin(pB);
        double sy = Math.sin(pA)*Math.cos(pB);
        double sz = Math.cos(pA);

        // apply offset viewing position
        sx -= offset*Math.cos(bearing);
        sy -= offset*Math.sin(bearing);

        xy = getAltAz(sx, sy, sz);
		    g.setColor(Color.lightGray);
		    g.fillOval(xy.width-2,xy.height-2,4,4);
		    if(names) label("Zenith", xy.width+4, xy.height);
	  }


	  // Stormgate
    if(stars && ( // visible only for 24 hours about launch
		(cf.week%2==1 && cf.day==0 && cf.hour < 0.5) ||
		(cf.week%2==0 && cf.day==6 && cf.hour > 0.5)
      ))
	  {
       xy = primitive(0.35*Math.PI, 0.5);
		   g.setColor(Color.lightGray);
       if(xy.height > 0 && xy.width > 0)
       {
		      g.fillOval(xy.width-2,xy.height-2,4,4);
		      if(names) label("Stormgate", xy.width+4, xy.height);
       }
    }


	  // Special effects
	  // Orlanth's ring : visible week on, week off
    if(stars)
    {
       cycle = (cf.week*7 + cf.day)%14 + cf.hour;
       double start = 6.0 + 23.0/24.0 + 13.0/1440.0;
       cycle -= start;
       if(cycle < 0) cycle+=14;
       double top = 7+1.0/24.0;
       if(cycle >= 0 && cycle <= top)
       {
           double polarAngle = 0.05*Math.PI*(7.0-cycle);
           double polarBearing = 0.5 - (294.0/293.0)*2.0*Math.PI*cycle;

           xy = primitive(polarAngle, polarBearing);

           // period is 16/7 hours = 16/24*7 = 2/21 days
           double period = 2.0/21.0;
           double rotations = cycle/period;

           if(xy.height > 0 && xy.width > 0)
           {
		           g.setColor(Color.orange);
               if(cf.cbring.getState())
               {
                   int size = Math.max(1, (int)Math.round(halfSide/240.0));
                   g.fillOval(xy.width-size/2,xy.height-size/2,size,size);
                   g.drawLine(xy.width,xy.height,xy.width,xy.height);
               }
               else
               for(int i=0; i<8 && i<=16.0*rotations; ++i)
               {
		              if(7==i) g.setColor(Color.green);
                  if(cycle > 7)
                  {
                      double del = (cycle-7.0)*168;
                      if(i < del) continue;
                  }
                  double da = -polarBearing + Math.PI*(rotations*2-i/4.0);
                  int dx = (int)Math.rint(10.0*Math.cos(da));
                  int dy = (int)Math.rint(10.0*Math.sin(da));
                  g.fillOval(xy.width-2+dx,xy.height-2+dy,4,4);
               }
		           if(names) label("Orlanth's Ring", xy.width+10, xy.height);
           }
       }
    }

	// Red Moon - visible at day
  if(cf.year >=1247 && cf.year <= 1725)
	{
       drawMoon(g);
	}

   } // end paint

	// A possible interpretation of the south path...
	// Regular - but should be a hell of a thing to reverse engineer
	// by buseri-style observations, with such sporadic sampling
/*	private double eastMouth(long dayno, double hour)
	{
		double c17 = Math.PI*2.0* ((double)((dayno+5)%17)+hour)/17.0;
		double c43 = Math.PI*2.0* ((double)((dayno+29)%43)+hour)/43.0;
		double c73 = Math.PI*2.0* ((double)((dayno+59)%73)+hour)/73.0;

		return (Math.cos(c17)+Math.cos(c43)+Math.cos(c73)+3.0)/20.0;
	}
	private double dodgeMouth(long dayno, double hour)
	{
		double c29 = Math.PI*2.0* ((double)((dayno+11)%29)+hour)/29.0;
		double c71 = Math.PI*2.0* ((double)((dayno+47)%71)+hour)/71.0;
		double c113= Math.PI*2.0* ((double)((dayno+97)%113)+hour)/113.0;

		return (Math.cos(c29)+Math.cos(c71)+Math.cos(c113)+3.0)/6.0;
	}
*/

    private double eastMouth(long dayno, double hour)
    {
        /* East mouth goes from about 10deg N to 30 deg S*/
        /* Period of Major oscillation 4 weeks for Tolat/Shargash */
        /* who rises about 9deg N of east */
        double baseDate = E2Param.shargashRise-7.0;
        double cycle = Math.PI*((dayno%28)+hour - baseDate)/14.0;

        // basic oscillation.
        //double angle = (Math.sin(cycle)-0.5)/3.0;
        // some wobble on top
        //double c17 = Math.PI*2.0* ((double)((dayno+5)%17)+hour)/17.0;
        //double c43 = Math.PI*2.0* ((double)((dayno+29)%43)+hour)/43.0;
        //double c73 = Math.PI*2.0* ((double)((dayno+59)%73)+hour)/73.0;

        //angle += (Math.cos(c17)+Math.cos(c43)+Math.cos(c73)+3.0)/60.0;

        //formula of abf@cs.ucc.ie
        double angle = Math.PI/18*(Math.sin(2*cycle)+Math.sin(cycle)-0.85);

        return angle;
    }

    private double dodgeMouth(long dayno, double hour)
    {
       /* West mouth goes from about 40N to 45S */
		   double c29 = Math.PI*2.0* ((double)((dayno+11)%29)+hour)/29.0;
		   double c71 = Math.PI*2.0* ((double)((dayno+47)%71)+hour)/71.0;
		   double c113= Math.PI*2.0* ((double)((dayno+97)%113)+hour)/113.0;

       //double wobble = Math.cos(c29)+Math.cos(c71)+Math.cos(c113);
       //double nmax = 2.0*Math.PI/9.0;
       //double smax = -Math.PI/4.0;
       //wobble *= (smax-nmax)/6.0;
       //wobble += (nmax+smax)/2.0;
       //return wobble;

        //formula of abf@cs.ucc.ie
       double cycle = 2.0*Math.PI*((dayno%81)+hour)/81.0;
       double angle = Math.cos (cycle) + Math.cos (2*cycle) +
                            Math.cos (3*cycle) + Math.cos (5*cycle) - 1.0;

       double nmax = 2.0*Math.PI/9.0;
       double smax = -Math.PI/4.0;

       angle *= (smax-nmax)/-6.0;
       angle += (nmax+smax)/2.0;

       return angle;
    }

	private void label(String s, int x, int y)
	{
		char [] c = s.toCharArray();
		gc.setColor(Color.lightGray);
		gc.drawChars(c,0,c.length, x, y);
	}

   /**
   * The instantiator must have some handle to the size
   * of the area where output is to be sent.
   * @param d Dimension to draw to
   */
   public synchronized void setSize(Dimension d)
   {
	size = d;
   }

   public synchronized Dimension getSize()
   {
    return size;
   }

   public synchronized void drawOrbiter(Graphics g)
   {
		double days = (294*(cf.year-1725)+cf.week*7)%28+cf.day+cf.hour; // how far into 28 day cycle
		double orbits = days*29.0/28.0;		          // begins at observed zenith
		while(orbits > 0.5) orbits -= 1.0;
		double cycle = orbits + 0.25;
		if(0<=cycle && cycle <= 0.5)
		{
      Dimension xy = sunpath(cycle*Math.PI*2.0);
      int x = xy.width;
      int y = xy.height;

			g.setColor(Color.lightGray);
			g.fillOval(x-6,y-6,12,12);
			g.setColor(Color.black);
			days = 28-days;
			if(days < 14)
				g.fillArc(x-7,y-7,14,14,90,-180);
			else
				g.fillArc(x-7,y-7,14,14,90,180);

			if(Math.abs(days-14) > 7) g.setColor(Color.lightGray);
			if(days>14) days -= 14;
			int width = (int)Math.rint(days-7);
			if(width<0) width = -width;
			g.fillOval(x-width, y-7, 2*width, 14);

			g.setColor(Color.lightGray);
			if(frame.cbnames.getState()) label("White Orbiter", x+14, y);
		}
	}
  public synchronized void drawMoon(Graphics g)
  {
       double myOffset = cf.getOffset(); // distance from centre/20,000km
       double mx = myOffset*Math.cos(bearing);
       double my = myOffset*Math.sin(bearing);
       double moony = 0.150/20.0;
       double moonx = 3.5/20.0;

       mx -= moonx;
       my -= moony; // cratercenter coordinates

       // at day 3.75 of the week, moon is full at 30deg N of E

       double norm = Math.sqrt(mx*mx+my*my);
       double nx = mx/norm;
       double ny = my/norm;
       mx = -Math.cos(0.35*Math.PI)*mx/norm;
       my = -Math.cos(0.35*Math.PI)*my/norm;
       double mz = Math.sin(0.35*Math.PI);

       double ang = 30+Math.atan2(nx, ny)*180.0/Math.PI;
       double doy = (cf.day+cf.hour-3.75);
       if(doy < 0) doy += 7.0;
       ang -= 360*doy/7.0;
       ang -= 180.0;


       Dimension xy = getAltAz(mx,my,mz);
       int moonsize = cf.cbring.getState() ?
          Math.max(1, (int)Math.round(halfSide/180.0))
          :12;
       g.setColor(Color.red);
       g.drawLine(xy.width,xy.height,xy.width,xy.height);
		   g.fillOval(xy.width-moonsize/2,xy.height-moonsize/2,moonsize,moonsize);
       while (ang < 0) ang+= 360.0;
       while (ang > 360) ang -= 360;
       int x=xy.width;
       int y=xy.height;
	     ang = 360 - ang;
       if(ang > 360) ang -= 360;

       double tang = ang;
       if(tang>180) tang -= 180;
       double frac = (Math.abs(tang-90) < 1) ? 100 : 90.0/(tang-90.0);

       if(up)
       {
          int i,j;
          for(i=0; i<moonsize; ++i)
          {
              x = -moonsize/2 + i;
              for(j=0; j<moonsize; ++j)
              {
                  g.setColor(Color.black);
                  y = -moonsize/2 + j;
                  if(x*x+y*y >= moonsize*moonsize/4.0) continue;

                  double cross = nx*x+ny*y;
                  if(ang<180) cross *= -1;

                  if(cross < 0) g.drawLine(x+xy.width,y+xy.height,
                           x+xy.width,y+xy.height);

                  if(Math.abs(ang-180) > 90) g.setColor(Color.red);
                  if(Math.abs(tang-90) > 1)
                  {
                      double dot = nx*y-ny*x;
                      cross *= frac;
                      if(cross*cross + dot*dot < moonsize*moonsize/4.0)
                      {
                         g.drawLine(x+xy.width,y+xy.height,
                           x+xy.width,y+xy.height);
                      }
                  } 
              }
          }
       }
       else
       {
          g.setColor(Color.black);
			    if(ang < 180)
				     g.fillArc(x-moonsize/2,y-moonsize/2,moonsize,moonsize,90,-180);
          else
				     g.fillArc(x-moonsize/2,y-moonsize/2,moonsize,moonsize,90,180);

          if(Math.abs(ang-180) > 90) g.setColor(Color.red);
			    if(ang>180) ang -= 180;
			    int width = (int)Math.floor(moonsize*(ang-90)/180.0);
			    if(width<0) width = -width;
			    g.fillOval(x-width, y-moonsize/2, 2*width, moonsize);
        }
		   if(frame.cbnames.getState()) label("Red moon", xy.width+moonsize, xy.height);
    }
}

/* end of file EphemerisIIAnimation.java */

