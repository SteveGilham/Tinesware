//package UK.co.demon.windsong.tines.util
//import package UK.co.demon.windsong.tines.?
/**
*  Class StarDome & associated private classes
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

class E2RandomFaintStar extends Random {
	int seed;
	public E2RandomFaintStar(int i) {super((long)i);}
	Dimension next()
	{
		double r = 300.0 * Math.sqrt(nextDouble());
		double theta = 2.0 * Math.PI * nextDouble();
		return new Dimension(
			(int) Math.rint(r * Math.cos(theta)),
			(int) Math.rint(r * Math.sin(theta)));
	}
}

class E2doublet {
	public double r;
	public double t;
}

class DomeStar{
      private double x,y,mag;
      private char[] name, marker;
      private Color col;
      private double radius;
      private int diam;
	  private Dimension [] scatter;
	  private double r,theta;
	  private E2doublet[] polar;
	private static int nfaint = 12;

//"Seed",129,1565,20,"Tree", Color.green
      public DomeStar(String a, int b, int c, int d, String e, Color f)
      {
         name = a.toCharArray();
         x=b;
         y=c;
         mag = d;
         marker= e.toCharArray();
         col = f;
         if(mag > 0)
         {
          radius = Math.sqrt((double)mag);
          diam = (int)Math.rint(radius);
          radius /=2;
	      r = Math.sqrt((double)(x*x+y*y)) / StarDome.radian;
	      theta = Math.atan2((double)y, (double)x);
         }
         else
         {
		     E2RandomFaintStar ran = new E2RandomFaintStar((b<<16)+(c&0xFFFF));
		     scatter = new Dimension[nfaint];
		     polar = new E2doublet [nfaint];
		     for(int i=0; i<nfaint; ++i)
		     {
			  scatter[i] = ran.next();
			  polar[i] = new E2doublet();
			  double xx = (double) scatter[i].width + x;
			  double yy = (double) scatter[i].height + y;
			  polar[i].r = Math.sqrt(xx*xx+yy*yy)/StarDome.radian;
			  polar[i].t = Math.atan2(yy, xx);
             }
         }
      }
      public DomeStar(String a, int b, int c, int d, String e)
      {
       this(a,b,c,d,e, (d<0) ? Color.cyan : Color.white);
      }


	/**
	* Draws this star
    * @param g Graphics to draw to
    * @param spin double giving rotation angle about pole star
    * @param slide double tilt of the dome
    * @param radius int pixels from overhead to horizon
    * @param x0 int pixel location of overhead (x coord)
    * @param y0 int pixel location of overhead (y coord)
    * @param names boolean if names are to be plotted
    * @param radialOffset double fraction offset of observer from M's pool to dome
    * @param bearing double angle of radius vector from M's pool to observer
    * @param projection int style of projection of dome
    */
      public void draw(Graphics g, double spin,
             double slide, int radius, int x0, int y0, boolean names,
             double radialOffset, double bearing,
             boolean up, double look)
      {
String s1 = new String(marker);
          g.setColor(col);
	      int px, py, nx=0, ny=0;
	      double cx,cy;
          boolean name = names;
          if(mag > 0)
          {
             // get resultant 3D position
             // i) spin the dome
             double th = - theta - spin;

             // ii) generate 3D coordinate if untilted
             double x = Math.cos(th)*Math.sin(r);
             double y = Math.sin(th)*Math.sin(r);
             double z = Math.cos(r);

             // iii) apply tilt about x axis
             double ty = y;
             double tx = x*Math.cos(slide)+z*Math.sin(slide);
             double tz = -x*Math.sin(slide)+z*Math.cos(slide);

             if(tz < 0) return; // quit if below horizon

             // iv) apply offset viewing position
             tx -= radialOffset*Math.cos(bearing);
             ty -= radialOffset*Math.sin(bearing);

             if(up)
             {
                double azimuth = Math.atan2(tx, ty);
                double altitude = Math.atan2(Math.sqrt(tx*tx+ty*ty), tz);
                altitude /= (Math.PI/2.0);
                nx = px = x0 + (int)Math.rint(radius*altitude*Math.cos(azimuth));
       	        ny = py = y0 - (int)Math.rint(radius*altitude*Math.sin(azimuth));
                g.fillOval(px,py,diam,diam);
		g.drawLine(px+diam/2,py+diam/2,px+diam/2,py+diam/2);
             }
             else
             {
                // look along line with angle = look as pole
                // rotate this to x axis (need to frig signs)
                double c = Math.cos(look);
                double s = Math.sin(look);
                x = tx*c+ty*s;
                y = tx*s-ty*c;
                z = tz;
                // now use the x-axis as if it were z, z as y, y as x
                if(x < 0) return;
                double azimuth = Math.atan2(y, z) + Math.PI/2.0;
                double altitude = Math.atan2(Math.sqrt(y*y+z*z), x);
                altitude /= (Math.PI/2.0);
                nx = px = x0 - (int)Math.rint(radius*altitude*Math.cos(azimuth));
       	        ny = py = y0 - (int)Math.rint(radius*altitude*Math.sin(azimuth));
                g.fillOval(px,py,diam,diam);
		g.drawLine(px+diam/2,py+diam/2,px+diam/2,py+diam/2);

             }
       }
       else
       {
        name = false;
		for(int i=0; i<nfaint;++i)
		{
             // get resultant 3D position
             // i) spin the dome
             double th = - polar[i].t - spin;

             // ii) generate 3D coordinate if untilted
             double x = Math.cos(th)*Math.sin(polar[i].r);
             double y = Math.sin(th)*Math.sin(polar[i].r);
             double z = Math.cos(polar[i].r);

             // iii) apply tilt about x axis
             double ty = y;
             double tx = x*Math.cos(slide)+z*Math.sin(slide);
             double tz = -x*Math.sin(slide)+z*Math.cos(slide);

             if(tz < 0) continue; // quit if below horizon
             // name = names;

             // iv) apply offset viewing position
             tx -= radialOffset*Math.cos(bearing);
             ty -= radialOffset*Math.sin(bearing);

             if(up)
             {
              name = names;
              double azimuth = Math.atan2(tx, ty);
              double altitude = Math.atan2(Math.sqrt(tx*tx+ty*ty), tz);
              altitude /= (Math.PI/2.0);
       	  nx = px = x0 + (int)Math.rint(radius*altitude*Math.cos(azimuth));
       	  ny = py = y0 - (int)Math.rint(radius*altitude*Math.sin(azimuth));
              g.drawLine(px,py,px,py);
             }
             else
             {
                // look along line with angle = look as pole
                // rotate this to x axis (need to frig signs)
                double c = Math.cos(look);
                double s = Math.sin(look);
                x = tx*c+ty*s;
                y = tx*s-ty*c;
                z = tz;
                // now use the x-axis as if it were z, z as y, y as x
                if(x < 0) continue;
                name = names;
                double azimuth = Math.atan2(y, z) + Math.PI/2.0;
                double altitude = Math.atan2(Math.sqrt(y*y+z*z), x);
                altitude /= (Math.PI/2.0);
       	        nx = px = x0 - (int)Math.rint(radius*altitude*Math.cos(azimuth));
       	        ny = py = y0 - (int)Math.rint(radius*altitude*Math.sin(azimuth));
                g.drawLine(px,py,px,py);
             }
          }
       }
       if(name && marker.length>0)
       {
	    g.setColor(Color.cyan);
        g.drawChars(marker, 0, marker.length, nx, ny);
       }

      }

	public double locate(int ex, int ey, int rad, int cen, 
		double spin, double slide, double radialOffset, 
		double bearing, boolean up, double look)
	{
         double azimuth;
         double altitude;
	   int nx, ny;

          if(mag > -3000)
          {
             // get resultant 3D position
             // i) spin the dome
             double th = - theta - spin;

             // ii) generate 3D coordinate if untilted
             double x = Math.cos(th)*Math.sin(r);
             double y = Math.sin(th)*Math.sin(r);
             double z = Math.cos(r);

             // iii) apply tilt about x axis
             double ty = y;
             double tx = x*Math.cos(slide)+z*Math.sin(slide);
             double tz = -x*Math.sin(slide)+z*Math.cos(slide);

             if(tz < 0) return 20000.0; // quit if below horizon

             // iv) apply offset viewing position
             tx -= radialOffset*Math.cos(bearing);
             ty -= radialOffset*Math.sin(bearing);

             if(up)
             {
                azimuth = Math.atan2(tx, ty);
                altitude = Math.atan2(Math.sqrt(tx*tx+ty*ty), tz);
                altitude /= (Math.PI/2.0);
         	    nx = cen + (int)Math.rint(rad*altitude*Math.cos(azimuth));
       	    ny = cen - (int)Math.rint(rad*altitude*Math.sin(azimuth));
             }
             else
             {
                // look along line with angle = look as pole
                // rotate this to x axis (need to frig signs)
                double c = Math.cos(look);
                double s = Math.sin(look);
                x = tx*c+ty*s;
                y = tx*s-ty*c;
                z = tz;
                // now use the x-axis as if it were z, z as y, y as x
                if(x < 0) return 20000.0;
                azimuth = Math.atan2(y, z) + Math.PI/2.0;
                altitude = Math.atan2(Math.sqrt(y*y+z*z), x);
                altitude /= (Math.PI/2.0);
         	    nx = cen - (int)Math.rint(rad*altitude*Math.cos(azimuth));
       	    ny = cen - (int)Math.rint(rad*altitude*Math.sin(azimuth));
             }
		 x = nx-ex;
		 y = ny-ey;
		 double crit = 5.0;
		 if(mag > 0) crit += diam;
		 double margin = Math.sqrt(x*x+y*y);
		 return (margin < crit) ? margin : 20000.0;
       }
       else
       {
		for(int i=0; i<nfaint;++i)
		{
             // get resultant 3D position
             // i) spin the dome
             double th = - polar[i].t - spin;

             // ii) generate 3D coordinate if untilted
             double x = Math.cos(th)*Math.sin(polar[i].r);
             double y = Math.sin(th)*Math.sin(polar[i].r);
             double z = Math.cos(polar[i].r);

             // iii) apply tilt about x axis
             double ty = y;
             double tx = x*Math.cos(slide)+z*Math.sin(slide);
             double tz = -x*Math.sin(slide)+z*Math.cos(slide);

             if(tz < 0) continue; // quit if below horizon

             // iv) apply offset viewing position
             tx -= radialOffset*Math.cos(bearing);
             ty -= radialOffset*Math.sin(bearing);

             if(up)
             {
              azimuth = Math.atan2(tx, ty);
              altitude = Math.atan2(Math.sqrt(tx*tx+ty*ty), tz);
              altitude /= (Math.PI/2.0);
             }
             else
             {
                // look along line with angle = look as pole
                // rotate this to x axis (need to frig signs)
                double c = Math.cos(look);
                double s = Math.sin(look);
                x = tx*c+ty*s;
                y = tx*s-ty*c;
                z = tz;
                // now use the x-axis as if it were z, z as y, y as x
                if(x < 0) continue;
                azimuth = Math.atan2(y, z) + Math.PI/2.0;
                altitude = Math.atan2(Math.sqrt(y*y+z*z), x);
                altitude /= (Math.PI/2.0);
             }
          }
       }
	 return 20000.0;
	}

	public String id()
	{
		String s1 = new String(name);
		String s2 = new String(marker);
		return "\""+s1+"\" "+x+" "+y+" "+mag+" \""+s2+"\"";
	}
}


public class StarDome
{
   private Vector host = new Vector(100,20);

   /**
   * The star "Youth", at x,y = 113,2540 is on the eastern horizon
   * at sunset on the equinox.  The equinox happens at midday that day;
   * the tilt angle can be neglected (<~0.1 degree) for this model
   * so the scale to 90 degrees from the zenith is 2542.5 units
   */
   public final static double radian = 1618.614;
   /**
   * The rotation of "Youth" from the x axis is
   */
   double skew = 0.04445887;

   /**
   * Draws all the stars according to the Dome configuration
   * and canvas size.
   * @param g Graphics to draw to
   * @param angle double rotation of dome from dusk on spring equinox
   * @param slide double tilt of the dome
   * @param radius int pixels from overhead to horizon
   * @param x0 int pixel location of overhead (x coord)
   * @param y0 int pixel location of overhead (y coord)
   * @param names boolean if names are to be plotted
   * @param radialOffset double fraction offset of observer from M's pool to dome
   * @param bearing double angle of radius vector from M's pool to observer
   * @param projection int style of projection of dome
   */
 public void draw(Graphics g, double angle, double slide, int radius,
        int x0, int y0, boolean names,
        double radialOffset, double bearing,
        boolean up, double look)
 {
       int n = host.size();
       for(int i=0; i<n; ++i)
       {
        ((DomeStar)host.elementAt(i)).draw(g, skew-angle, slide, radius,
                     x0,y0, names,radialOffset, bearing,
                     up, look);
       }
 }
 
	public String locate(int ex, int ey, int rad, int cen,
		double angle, double slide, double radialOffset, 
		double bearing, boolean up, double look)
	{
		String s;
		double r = 2500;
       int n = host.size();
		int in=n;
       for(int i=0; i<n; ++i)
       {
        	double r1 = ((DomeStar)host.elementAt(i)).locate(ex, ey, rad, cen,
			skew-angle, slide,
			radialOffset, bearing, up, look);
		if(r1 < r)
		{
			in = i;
			r = r1;
		}
       }
	 if(in == n) return("*");
	 return ""+in+" "+((DomeStar)host.elementAt(in)).id();
	}



  /**
   * Default constructor - assumes that the stars all rotate with the
   * dome at the current moment.
   */
   public StarDome()
   {
      host.addElement(new DomeStar("Pole Star",1,1,50,"Pole Star"));
      host.addElement(new DomeStar("Arraz",-425,185,35,"Arraz"));
      host.addElement(new DomeStar("Ourania",355,-325,20,""));
      host.addElement(new DomeStar("Evandal",446,-1285,20,"Oropum"));
      host.addElement(new DomeStar("Everina",655,-760,20,"Rice"));
      host.addElement(new DomeStar("Conspirator",1451,-969,20,"Whisperers"));
      host.addElement(new DomeStar("Maw",910,-70,20,""));
      host.addElement(new DomeStar("Eye",1120,-189,20,""));
      host.addElement(new DomeStar("Neck",895,96,20,""));
      host.addElement(new DomeStar("Chest",805,305,20,"Star Dragon"));
      host.addElement(new DomeStar("Wing",835,576,20,""));
      host.addElement(new DomeStar("Belly",565,665,20,""));
      host.addElement(new DomeStar("Tail",459,935,20,""));
      host.addElement(new DomeStar("Stinger",-6,1070,20,""));
      host.addElement(new DomeStar("Tail",219,1085,20,""));
      host.addElement(new DomeStar("Seed",129,1565,20,"Tree", Color.green));
      host.addElement(new DomeStar("Erkonus",-1445,544,20,"Erkonus"));
      host.addElement(new DomeStar("Dove",-1355,-611,20,"Dove"));
      host.addElement(new DomeStar("Harp",-574,-850,20,"Harp"));
      host.addElement(new DomeStar("Steward",-185,-850,20,"Steward"));
      host.addElement(new DomeStar("(Tongue)",-49,-1600,5,""));
      host.addElement(new DomeStar("Eye",71,-2005,20,"Lorion"));
      host.addElement(new DomeStar("Tail",0,-2500,20,""));
      host.addElement(new DomeStar("Leg",866,-2185,20,"Thunderer"));
      host.addElement(new DomeStar("Leg",761,-2350,20,""));
      host.addElement(new DomeStar("Arm",926,-2380,20,""));
      host.addElement(new DomeStar("Arm",1166,-2154,20,""));
      host.addElement(new DomeStar("Varnaga",1241,-1614,20,"Crocodile"));
      host.addElement(new DomeStar("Vergenari",1961,-1479,20,"Sow"));
      host.addElement(new DomeStar("Bull",2140,-819,20,"Bull"));
      host.addElement(new DomeStar("Oasis",2140,51,20,"Oasis"));
      host.addElement(new DomeStar("Thasus",1780,666,20,"Thasus"));
      host.addElement(new DomeStar("Pincer",1359,1506,20,""));
      host.addElement(new DomeStar("Womb",1269,1716,20,"Scorpion"));
      host.addElement(new DomeStar("Jewel Flower",474,1910,20,"Flowers"));
      host.addElement(new DomeStar("Youth",113,2540,20,"Youth"));
      host.addElement(new DomeStar("(Shafesora)",-1911,1354,5,"Marsh"));
      host.addElement(new DomeStar("Lion",-2285,544,20,"Lion"));
      host.addElement(new DomeStar("Swan",-1955,-206,20,"Swan"));
      host.addElement(new DomeStar("Bow",-1459,-1331,20,""));
      host.addElement(new DomeStar("Heart",-1339,-1571,20,"Hunter"));
      host.addElement(new DomeStar("Hip",-1459,-1661,20,""));
      host.addElement(new DomeStar("Knee",-1609,-1601,20,""));
      host.addElement(new DomeStar("Pot",-814,-1795,20,"Pot"));
      host.addElement(new DomeStar("Fan",-1024,-2336,20,"Fan"));
      host.addElement(new DomeStar("Eye",-94,-1930,20,""));
      host.addElement(new DomeStar("Marker",-94,-2290,20,""));
//      host.addElement(new DomeStar("Kalikos",-378,-2785,20,"Kalikos", Color.red));
      host.addElement(new DomeStar("Chorus",-260,410,10,"Chorus"));
      host.addElement(new DomeStar("",-380,320,10,""));
      host.addElement(new DomeStar("",-470,290,10,""));
      host.addElement(new DomeStar("",-545,65,10,""));
      host.addElement(new DomeStar("",-440,95,10,""));
      host.addElement(new DomeStar("",-380,-10,10,""));
      host.addElement(new DomeStar("",-485,-40,10,""));
      host.addElement(new DomeStar("Cook",-110,-265,10,"Cook"));
      host.addElement(new DomeStar("",-80,-340,10,""));
      host.addElement(new DomeStar("",-155,-340,10,""));
      host.addElement(new DomeStar("",-170,-400,10,""));
      host.addElement(new DomeStar("",-245,-415,10,""));
      host.addElement(new DomeStar("",-140,-445,10,""));
      host.addElement(new DomeStar("",430,-400,10,""));
      host.addElement(new DomeStar("",280,-400,10,""));
      host.addElement(new DomeStar("",190,-340,10,""));
      host.addElement(new DomeStar("Officers",190,110,10,"Officers"));
      host.addElement(new DomeStar("",160,245,10,""));
      host.addElement(new DomeStar("",130,440,10,""));
      host.addElement(new DomeStar("",325,50,10,""));
      host.addElement(new DomeStar("",401,-1015,10,""));
      host.addElement(new DomeStar("",596,-1450,10,""));
      host.addElement(new DomeStar("",701,-1435,10,""));
      host.addElement(new DomeStar("",446,-1540,10,""));
      host.addElement(new DomeStar("",386,-1345,10,""));
      host.addElement(new DomeStar("",566,-1150,10,""));
      host.addElement(new DomeStar("",535,-775,10,""));
      host.addElement(new DomeStar("",805,-700,10,""));
      host.addElement(new DomeStar("",715,-520,10,""));
      host.addElement(new DomeStar("",610,-535,10,""));
      host.addElement(new DomeStar("",565,-475,10,""));
      host.addElement(new DomeStar("",475,-535,10,""));
      host.addElement(new DomeStar("",445,-685,10,""));
      host.addElement(new DomeStar("",385,-595,10,""));
      host.addElement(new DomeStar("",295,-535,10,""));
      host.addElement(new DomeStar("",1180,-909,10,""));
      host.addElement(new DomeStar("",1301,-999,10,""));
      host.addElement(new DomeStar("",1225,-744,10,""));
      host.addElement(new DomeStar("",1330,-849,10,""));
      host.addElement(new DomeStar("",1600,-969,10,""));
      host.addElement(new DomeStar("",865,186,10,""));
      host.addElement(new DomeStar("",820,440,10,""));
      host.addElement(new DomeStar("",970,621,10,""));
      host.addElement(new DomeStar("",970,726,10,""));
      host.addElement(new DomeStar("",939,861,10,""));
      host.addElement(new DomeStar("",774,1055,10,""));
      host.addElement(new DomeStar("",1030,726,10,""));
      host.addElement(new DomeStar("",564,830,10,""));
      host.addElement(new DomeStar("",460,800,10,""));
      host.addElement(new DomeStar("",159,995,10,""));
      host.addElement(new DomeStar("",-81,1655,10,""));
      host.addElement(new DomeStar("",54,1715,10,""));
      host.addElement(new DomeStar("",204,1790,10,""));
      host.addElement(new DomeStar("",264,1655,10,""));
      host.addElement(new DomeStar("",114,1925,10,""));
      host.addElement(new DomeStar("",159,2015,10,""));
      host.addElement(new DomeStar("Yoke",-590,905,10,"Yoke"));
      host.addElement(new DomeStar("",-546,1010,10,""));
      host.addElement(new DomeStar("",-486,995,10,""));
      host.addElement(new DomeStar("",-545,845,10,""));
      host.addElement(new DomeStar("",-500,860,10,""));
      host.addElement(new DomeStar("",-1370,469,10,""));
      host.addElement(new DomeStar("",-1490,394,10,""));
      host.addElement(new DomeStar("Hawk",-860,-101,10,"Hawk"));
      host.addElement(new DomeStar("",-830,-146,10,""));
      host.addElement(new DomeStar("",-800,-220,10,""));
      host.addElement(new DomeStar("",-920,-146,10,""));
      host.addElement(new DomeStar("",-890,-221,10,""));
      host.addElement(new DomeStar("",-845,-280,10,""));
      host.addElement(new DomeStar("",-1355,-356,10,""));
      host.addElement(new DomeStar("",-1475,-716,10,""));
      host.addElement(new DomeStar("",-1310,-701,10,""));
      host.addElement(new DomeStar("Quail",-1399,-896,10,"Quail"));
      host.addElement(new DomeStar("",-1294,-986,10,""));
      host.addElement(new DomeStar("",-1414,-1001,10,""));
      host.addElement(new DomeStar("",-1309,-1076,10,""));
      host.addElement(new DomeStar("",-709,-895,10,""));
      host.addElement(new DomeStar("",-649,-955,10,""));
      host.addElement(new DomeStar("",-334,-970,10,""));
      host.addElement(new DomeStar("",-289,-955,10,""));
      host.addElement(new DomeStar("",-214,-940,10,""));
      host.addElement(new DomeStar("",56,-1390,10,""));
      host.addElement(new DomeStar("",251,-1345,10,""));
      host.addElement(new DomeStar("",41,-1225,10,""));
      host.addElement(new DomeStar("",10,-1000,10,""));
      host.addElement(new DomeStar("",145,-1000,10,""));
      host.addElement(new DomeStar("Fishes",26,-1480,10,"Fishes"));
      host.addElement(new DomeStar("",101,-1600,10,""));
      host.addElement(new DomeStar("",56,-1750,10,""));
      host.addElement(new DomeStar("",-64,-1735,10,""));
      host.addElement(new DomeStar("",131,-2170,10,""));
      host.addElement(new DomeStar("",-109,-2155,10,""));
      host.addElement(new DomeStar("",851,-2305,10,""));
      host.addElement(new DomeStar("",956,-2259,10,""));
      host.addElement(new DomeStar("",1046,-2214,10,""));
      host.addElement(new DomeStar("",1316,-1689,10,""));
      host.addElement(new DomeStar("",1286,-1539,10,""));
      host.addElement(new DomeStar("",1346,-1479,10,""));
      host.addElement(new DomeStar("",1091,-1584,10,""));
      host.addElement(new DomeStar("",1181,-1389,10,""));
      host.addElement(new DomeStar("",1946,-1659,10,""));
      host.addElement(new DomeStar("",2126,-1374,10,""));
      host.addElement(new DomeStar("",2155,-939,10,""));
      host.addElement(new DomeStar("",2156,-1029,10,""));
      host.addElement(new DomeStar("",2215,-714,10,""));
      host.addElement(new DomeStar("",2260,-804,10,""));
      host.addElement(new DomeStar("",1990,531,10,""));
      host.addElement(new DomeStar("",1554,846,10,""));
      host.addElement(new DomeStar("",1659,1056,10,""));
      host.addElement(new DomeStar("",1389,1821,10,""));
      host.addElement(new DomeStar("",1334,2048,10,""));
      host.addElement(new DomeStar("",1134,2091,10,""));
      host.addElement(new DomeStar("",1149,1656,10,""));
      host.addElement(new DomeStar("",999,1716,10,""));
      host.addElement(new DomeStar("",909,1896,10,""));
      host.addElement(new DomeStar("",609,1970,10,""));
      host.addElement(new DomeStar("",489,2135,10,""));
      host.addElement(new DomeStar("",98,2675,10,""));
      host.addElement(new DomeStar("",23,2435,10,""));
      host.addElement(new DomeStar("",-51,2345,10,""));
      host.addElement(new DomeStar("",84,2390,10,""));
      host.addElement(new DomeStar("",189,2465,10,""));
      host.addElement(new DomeStar("",248,2375,10,""));
      host.addElement(new DomeStar("",-456,2045,10,""));
      host.addElement(new DomeStar("",-366,1940,10,""));
      host.addElement(new DomeStar("Willows",-351,1745,10,"Willows"));
      host.addElement(new DomeStar("",-456,1655,10,""));
      host.addElement(new DomeStar("",-276,1670,10,""));
      host.addElement(new DomeStar("",-366,1580,10,""));
      host.addElement(new DomeStar("Plough",-1101,1864,10,"Plough"));
      host.addElement(new DomeStar("",-1116,1759,10,""));
      host.addElement(new DomeStar("",-1056,1759,10,""));
      host.addElement(new DomeStar("",-981,1819,10,""));
      host.addElement(new DomeStar("",-996,1759,10,""));
      host.addElement(new DomeStar("",-1071,1699,10,""));
      host.addElement(new DomeStar("Veridna",-1656,2384,10,"Veridna"));
      host.addElement(new DomeStar("",-1461,2474,10,""));
      host.addElement(new DomeStar("",-1611,2504,10,""));
      host.addElement(new DomeStar("",-1716,2474,10,""));
      host.addElement(new DomeStar("",-1701,2594,10,""));
      host.addElement(new DomeStar("",-1686,2699,10,""));
      host.addElement(new DomeStar("",-2495,574,10,""));
      host.addElement(new DomeStar("",-2390,424,10,""));
      host.addElement(new DomeStar("",-2225,454,10,""));
      host.addElement(new DomeStar("",-2165,394,10,""));
      host.addElement(new DomeStar("",-2210,-71,10,""));
      host.addElement(new DomeStar("",-2120,-116,10,""));
      host.addElement(new DomeStar("",-2000,-41,10,""));
      host.addElement(new DomeStar("",-1880,-161,10,""));
      host.addElement(new DomeStar("",-1865,-206,10,""));
      host.addElement(new DomeStar("",-2030,-341,10,""));
      host.addElement(new DomeStar("Hag",-2615,-417,10,"Hag"));
      host.addElement(new DomeStar("",-2705,-462,10,""));
      host.addElement(new DomeStar("",-2885,-447,10,""));
      host.addElement(new DomeStar("",-2825,-627,10,""));
      host.addElement(new DomeStar("",-2570,-762,10,""));
      host.addElement(new DomeStar("",-2614,-852,10,""));
      host.addElement(new DomeStar("",-2540,-822,10,""));
      host.addElement(new DomeStar("",-2464,-777,10,""));
      host.addElement(new DomeStar("Borna",-2674,-927,10,"Borna"));
      host.addElement(new DomeStar("",-2404,-852,10,""));
      host.addElement(new DomeStar("",-2254,-1016,10,""));
      host.addElement(new DomeStar("",-2224,-1121,10,""));
      host.addElement(new DomeStar("Deer",-2254,-1211,10,"Deer"));
      host.addElement(new DomeStar("",-2119,-1256,10,""));
      host.addElement(new DomeStar("",-2119,-1316,10,""));
      host.addElement(new DomeStar("Firestick",-2614,-1362,10,"Firestick"));
      host.addElement(new DomeStar("",-2614,-1437,10,""));
      host.addElement(new DomeStar("",-2599,-1512,10,""));
      host.addElement(new DomeStar("",-2584,-1572,10,""));
      host.addElement(new DomeStar("",-2599,-1617,10,""));
      host.addElement(new DomeStar("",-2494,-1602,10,""));
      host.addElement(new DomeStar("",-1399,-1481,10,""));
      host.addElement(new DomeStar("",-1594,-1391,10,""));
      host.addElement(new DomeStar("",-1684,-1436,10,""));
      host.addElement(new DomeStar("",-1774,-1511,10,""));
      host.addElement(new DomeStar("",-2029,-1676,10,""));
      host.addElement(new DomeStar("Groundhog",-2029,-1766,10,"Groundhog"));
      host.addElement(new DomeStar("",-1909,-2051,10,""));
      host.addElement(new DomeStar("",-1774,-2036,10,""));
      host.addElement(new DomeStar("Rabbits",-1744,-2126,10,"Rabbits"));
      host.addElement(new DomeStar("",-1744,-2216,10,""));
      host.addElement(new DomeStar("Raven",-589,-2125,10,"Raven"));
      host.addElement(new DomeStar("",-574,-2215,10,""));
      host.addElement(new DomeStar("",-649,-2230,10,""));
      host.addElement(new DomeStar("",-679,-2305,10,""));
      host.addElement(new DomeStar("",-529,-2260,10,""));
      host.addElement(new DomeStar("",-603,-2320,10,""));
      host.addElement(new DomeStar("Love Stars",-1835,1647,-1,""));
      host.addElement(new DomeStar("Love Stars",-1881,1459,-1,""));
      host.addElement(new DomeStar("Love Stars",-1967,1552,-1,""));
      host.addElement(new DomeStar("Love Stars",-2000,1678,-1,""));
      host.addElement(new DomeStar("Love Stars",-2013,1859,-1,""));
      host.addElement(new DomeStar("Love Stars",-2166,1714,-1,""));
      host.addElement(new DomeStar("Love Stars",-2222,1981,-1,""));
      host.addElement(new DomeStar("Love Stars",-2239,1771,-1,""));
      host.addElement(new DomeStar("Love Stars",-2403,1862,-1,""));
      host.addElement(new DomeStar("War Stars",-1991,1003,-1,""));
      host.addElement(new DomeStar("War Stars",-2091,1069,-1,""));
      host.addElement(new DomeStar("War Stars",-2125,1125,-1,""));
      host.addElement(new DomeStar("War Stars",-2231,1021,-1,""));
      host.addElement(new DomeStar("War Stars",-2289,1201,-1,""));
      host.addElement(new DomeStar("War Stars",-2437,1398,-1,""));
      host.addElement(new DomeStar("War Stars",-2484,1173,-1,""));
      host.addElement(new DomeStar("War Stars",-2511,1279,-1,""));
      host.addElement(new DomeStar("War Stars",-2708,1325,-1,""));
      host.addElement(new DomeStar("River",53,3035,-1,""));
      host.addElement(new DomeStar("River",83,2765,-1,""));
      host.addElement(new DomeStar("River",98,2450,-1,""));
      host.addElement(new DomeStar("River",99,2225,-1,""));
      host.addElement(new DomeStar("River",99,2045,-1,""));
      host.addElement(new DomeStar("River",129,1790,-1,""));
      host.addElement(new DomeStar("River",189,1520,-1,""));
      host.addElement(new DomeStar("River",219,1340,-1,""));
      host.addElement(new DomeStar("River",354,1100,-1,""));
      host.addElement(new DomeStar("River",534,875,-1,""));
      host.addElement(new DomeStar("River",715,650,-1,""));
      host.addElement(new DomeStar("River",880,486,-1,""));
      host.addElement(new DomeStar("River",955,261,-1,""));
      host.addElement(new DomeStar("River",1015,-115,-1,""));
      host.addElement(new DomeStar("River",910,-325,-1,""));
      host.addElement(new DomeStar("River",715,-610,-1,""));
      host.addElement(new DomeStar("River",415,-850,-1,""));
      host.addElement(new DomeStar("River",281,-1120,-1,""));
      host.addElement(new DomeStar("River",146,-1375,-1,""));
      host.addElement(new DomeStar("River",26,-1645,-1,""));
      host.addElement(new DomeStar("River",-34,-1960,-1,""));
      host.addElement(new DomeStar("River",-34,-2215,-1,""));
      host.addElement(new DomeStar("River",-63,-2455,-1,""));
      host.addElement(new DomeStar("River",-78,-2755,-1,""));
      host.addElement(new DomeStar("River",-78,-2980,-1,""));
      host.addElement(new DomeStar("Love Stars",-1923,1619,3,"Love Stars"));
      host.addElement(new DomeStar("War Stars",-2238,1167,3,"War Stars"));
      host.addElement(new DomeStar("Forest",-2770,723,-2,""));
      host.addElement(new DomeStar("Forest",-2305,453,-2,""));
      host.addElement(new DomeStar("Forest",-1915,169,-2,""));
      host.addElement(new DomeStar("Forest",-1645,-176,-2,""));
      host.addElement(new DomeStar("Forest",-1390,-461,-2,""));
      host.addElement(new DomeStar("Forest",-1180,-806,-2,""));
      host.addElement(new DomeStar("Forest",-1164,-1286,-2,""));
      host.addElement(new DomeStar("Forest",-1284,-1691,-2,""));
      host.addElement(new DomeStar("Forest",-1389,-1991,-2,""));
      host.addElement(new DomeStar("Forest",-1614,-2321,-2,""));
      host.addElement(new DomeStar("Forest",-2800,243,-2,""));
      host.addElement(new DomeStar("Forest",-2845,-191,-2,""));
      host.addElement(new DomeStar("Forest",-2785,-762,-2,""));
      host.addElement(new DomeStar("Forest",-2679,-1181,-2,""));
      host.addElement(new DomeStar("Forest",-2469,-1676,-2,""));
      host.addElement(new DomeStar("Forest",-2139,-2006,-2,""));
      host.addElement(new DomeStar("Forest",-1764,-1766,-2,""));
      host.addElement(new DomeStar("Forest",-2350,79,-2,""));
      host.addElement(new DomeStar("Forest",-2335,-446,-2,""));
      host.addElement(new DomeStar("Forest",-2200,-866,-2,""));
      host.addElement(new DomeStar("Forest",-2034,-1271,-2,""));
      host.addElement(new DomeStar("Forest",-1614,-1256,-2,""));
      host.addElement(new DomeStar("Forest",-1795,-896,-2,""));
      host.addElement(new DomeStar("Forest",-1765,-536,-2,""));
      host.addElement(new DomeStar("Fields",-282,2810,-3,""));
      host.addElement(new DomeStar("Fields",-1361,2539,-3,""));
      host.addElement(new DomeStar("Fields",-1406,1714,-3,""));
      host.addElement(new DomeStar("Fields",-671,2120,-3,""));
      host.addElement(new DomeStar("Fields",-326,1715,-3,""));
      host.addElement(new DomeStar("Fields",-1436,934,-3,""));
      host.addElement(new DomeStar("Fields",-1315,49,-3,""));
      host.addElement(new DomeStar("Fields",-700,80,-3,""));
      host.addElement(new DomeStar("Fields",-1046,1054,-3,""));
      host.addElement(new DomeStar("Fields",-596,1505,-3,""));
      host.addElement(new DomeStar("Fields",-715,665,-3,""));
      host.addElement(new DomeStar("Fields",-730,-550,-3,""));
      host.addElement(new DomeStar("Fields",-564,-1075,-3,""));
      host.addElement(new DomeStar("Fields",-324,-1675,-3,""));
      host.addElement(new DomeStar("Fields",-849,-2785,-3,""));
      host.addElement(new DomeStar("Fields",-594,-2200,-3,""));
      host.addElement(new DomeStar("City",-205,228,-4,""));
      host.addElement(new DomeStar("City",18,259,-4,""));
      host.addElement(new DomeStar("City",215,150,-4,""));
      host.addElement(new DomeStar("City",247,-40,-4,""));
      host.addElement(new DomeStar("City",139,-203,-4,""));
      host.addElement(new DomeStar("City",-39,-276,-4,""));
      host.addElement(new DomeStar("City",-228,-153,-4,""));
      host.addElement(new DomeStar("City",-281,30,-4,""));
   }
}

/* end of file StarDome.java */


