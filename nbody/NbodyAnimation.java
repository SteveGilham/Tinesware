//package UK.co.demon.windsong.tines.util
//import package UK.co.demon.windsong.tines.?
/** 
*  Class NbodyAnimation
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

public class NbodyAnimation implements Animated
{
   private Dimension size;
   private int side, halfSide;
   private NbodyEnsemble e0, e1;

   double theta = 0.0;  // parametrise
   double r1 = 1.0;
   double r2;
   boolean begun=false;
   String smashed = "";
   int lost = -1;

   double rad0, radA, radB;

   public NbodyAnimation()
   {
       reset();
       r2 = 825/587.4;
   }


   public void begin()
   {
       /*
         Lambda Serpentis II mass 6.799e24 kg
            diameter 13,750 km
         Moon a mass 9.827e23 kg,
            orbit radius 587400km period 47 days
            diameter 7,243 km
         Moon b mass 3.03e23 kg,
            orbit radius 825000km period 78.4 days
            diameter 4,960 km

         Earth mass = 5.976e24 kg, diameter 12,800 km
         G = 6.672e-8 cm3g-1s-2
       */

       rad0 = 13750.0/(2.0*587400.0);
       radA =  7243.0/(2.0*587400.0);
       radB =  4960.0/(2.0*587400.0);

       // in our system G = 1, and so given mass, distance scale
       // appropriately to get the real world numbers


       // very light body test case
       NbodyMass [] x = new NbodyMass[3];
       x[0] = new NbodyMass();
       x[0].set(1.0, new Vector3(0.0,0.0,0.0), new Vector3(0.0,0.0,0.0));
       x[1] = new NbodyMass();
       x[1].set(0.0, new Vector3(1.0,0.0,0.0), new Vector3(0.0,1.0,0.0));

       double v = Math.sqrt(1.0/r2);
       x[2] = new NbodyMass();
       x[2].set(0.0, new Vector3(r2*Math.cos(theta),r2*Math.sin(theta),0.0),
              new Vector3(-v*Math.sin(theta),v*Math.cos(theta),0.0));
       e0 = new NbodyEnsemble(x, 0.0);


       // massive moons
       NbodyMass [] y = new NbodyMass[3];
       y[1] = new NbodyMass();
       y[1].set(0.9827/6.799,
            new Vector3(1.0,0.0,0.0),
            new Vector3(0.0,1.0,0.0));

       r2 = 825/587.4;
       y[2] = new NbodyMass();
       y[2].set(0.303/6.799,
            new Vector3(r2*Math.cos(theta),r2*Math.sin(theta),0.0),
            new Vector3(-v*Math.sin(theta),v*Math.cos(theta),0.0));

       y[0] = new NbodyMass();
       Vector3 v0 = new Vector3(y[1].x0dot);
       v0.maddm(-0.9827/6.799, y[2].x0dot, -0.303/6.799);
       y[0].set(1.0, new Vector3(0.0,0.0,0.0), v0);
       e1 = new NbodyEnsemble(y, 0.0);

       begun = true;
   }

   public void reset()
   {
       begun = false;
       lost = -1;
       smashed = "";
   }

   /**
   * This draws the clock face and the times.
   * @param g Graphics to draw to
   */
   public void paint(Graphics g)
   {
      g.setColor(Color.black);
      g.fillRect(0,0,size.width,size.height);

      side = (size.width/2<size.height) ? size.width/2 :size.height;
      halfSide = (int) (0.45*(double)side);
      int margin = side/2 - halfSide;
      int x1 = margin, y1,x2,y2;
      int d1 = 2*halfSide;

      double energy0=0.0;
      double energy1=0.0;
      Vector3 v0 ,v1, v2, w0, w1, w2;

      g.setColor(Color.lightGray);
      String str = "Moons of negligible mass";
      g.drawChars(str.toCharArray() , 0, str.length(), 0, margin);

      str = "Moons of stated mass";
      g.drawChars(str.toCharArray() , 0, str.length(), side, margin);


      if(begun)
      {
          energy0 = e0.advance(0.05);
          energy1 = e1.advance(0.05);
          v0 = new Vector3(e0.ensemble[0].x0);
          v1 = new Vector3(e0.ensemble[1].x0);
          v2 = new Vector3(e0.ensemble[2].x0);
          w0 = new Vector3(e1.ensemble[0].x0);
          w1 = new Vector3(e1.ensemble[1].x0);
          w2 = new Vector3(e1.ensemble[2].x0);
      }
      else
      {
          v0 = new Vector3(0.0,0.0,0.0);
          v1 = new Vector3(1.0,0.0,0.0);
          v2 = new Vector3(r2*Math.cos(theta), r2*Math.sin(theta), 0.0);
          w0 = new Vector3(0.0,0.0,0.0);
          w1 = new Vector3(1.0,0.0,0.0);
          w2 = new Vector3(r2*Math.cos(theta), r2*Math.sin(theta), 0.0);
      }

      double scale = v0.dot(v0);
      double s1 = v1.dot(v1);
      if(s1 > scale) scale = s1;
      s1 = v2.dot(v2);
      if(s1 > scale) scale = s1;
      s1 = w0.dot(w0);
      if(s1 > scale) scale = s1;
      s1 = w1.dot(w1);
      if(s1 > scale) scale = s1;
      s1 = w2.dot(w2);
      if(s1 > scale) scale = s1;

      scale = Math.sqrt(scale);
      if(scale < 2.0) scale = 2.0; // circle goes to +/- scale
      else if(scale < 5.0) scale = 5.0;
      else if(scale < 10.0) scale = 10.0;
      else if(scale < 20.0) scale = 20.0;
      else if(scale < 50.0) scale = 50.0;
      else if(scale < 100.0) scale = 100.0;
      else if(scale < 200.0) scale = 200.0;
      else if(scale < 500.0) scale = 500.0;
      else if(scale < 1000.0) scale = 1000.0;
      else if(scale < 2000.0) scale = 2000.0;
      else if(scale < 5000.0) scale = 5000.0;
      else if(scale < 10000.0) scale = 10000.0;

      g.setColor(Color.cyan);
      double r = ((double)halfSide)/scale;  // unit distance

      x1 = (side/2) - (int)(r1*r);
      d1 = (int)(2.0*r*r1);
      g.drawOval(x1,x1,d1,d1); // scale
      g.drawOval(x1+side,x1,d1,d1); // scale

      x1 = (side/2) - (int)(r2*r);
      d1 = (int)(2.0*r*r2);
      g.drawOval(x1,x1,d1,d1); // scale
      g.drawOval(x1+side,x1,d1,d1); // scale

      g.setColor(Color.blue);
      x1 = (side/2) + (int)(v0.data[0]*r);
      y1 = (side/2) - (int)(v0.data[1]*r);
      g.fillOval(x1-8,y1-8,16,16);
      x1 = (side/2) + (int)(w0.data[0]*r);
      y1 = (side/2) - (int)(w0.data[1]*r);
      g.fillOval(side+x1-8,y1-8,16,16);

      g.setColor(Color.green);
      x1 = (side/2) + (int)(v1.data[0]*r);
      y1 = (side/2) - (int)(v1.data[1]*r);
      g.fillOval(x1-6,y1-6,12,12);
      if(lost != 1)
      {
         x1 = (side/2) + (int)(w1.data[0]*r);
         y1 = (side/2) - (int)(w1.data[1]*r);
         g.fillOval(side+x1-6,y1-6,12,12);
      }

      g.setColor(Color.lightGray);
      x1 = (side/2) + (int)(v2.data[0]*r);
      y1 = (side/2) - (int)(v2.data[1]*r);
      g.fillOval(x1-5,y1-5,10,10);
      if(lost != 2)
      {
         x1 = (side/2) + (int)(w2.data[0]*r);
         y1 = (side/2) - (int)(w2.data[1]*r);
         g.fillOval(side+x1-5,y1-5,10,10);
      }

      // Moon a mass 9.827e23 kg, orbit radius 587400km period 47 days
      double l = 1.0/0.5874;
      int pl = (int) (l*r);
      x1 = side-(pl/2);
      x2 = x1+pl;
      g.drawLine(x1, side-margin, x2, side-margin);
      g.drawLine(x1, side-margin-3, x1, side-margin+3);
      g.drawLine(x2, side-margin-3, x2, side-margin+3);

      if(begun)
      {
          double t = 47.0*e0.time/(2.0*Math.PI);
          String s = "Time = "+t+"days energy = "+energy1
                       +" scale line represents = 1 million km";

          Vector3 work = new Vector3(w0);
          work.sub(w1);

          double radXX = Math.sqrt(work.dot(work));
          if(radXX < rad0+radA)
          {
               smashed = " Moon A has hit the planet";
               lost = 1;
               e1 = save(2, e1);
          }

          work.sub(w2);
          radXX = Math.sqrt(work.dot(work));
          if(radXX < rad0+radB)
          {
               smashed = " Moon B has hit the planet";
               lost = 2;
               e1 = save(1, e1);
          }

          work.set(w1);
          work.sub(w2);
          radXX = Math.sqrt(work.dot(work));
          if(radXX < radA+radB)
          {
               smashed = " Moons have collided";
               lost = 2;
               e1 = save(0, e1);
          }
          g.drawChars(s.toCharArray() , 0, s.length(), 0, side);
          s = "     ";
          s += smashed;

          if(scale > 25 && smashed.length()==0)
          {
              if(e1.ensemble[0].energy > 0
              && e1.ensemble[0].energy > e1.ensemble[2].energy
              && e1.ensemble[0].energy > e1.ensemble[1].energy
		) s += " Moons have escaped";
              else if (e1.ensemble[1].energy > 0
              && e1.ensemble[1].energy > e1.ensemble[0].energy
              && e1.ensemble[1].energy > e1.ensemble[2].energy
		) s+= " Moon A has escaped";
              else if (e1.ensemble[2].energy > 0
              && e1.ensemble[2].energy > e1.ensemble[0].energy
              && e1.ensemble[2].energy > e1.ensemble[1].energy
		) s+= " Moon B has escaped";
          }
          g.drawChars(s.toCharArray() , 0, s.length(), 0, side - 12);
      }
   }

   /**
   * The instantiator must have some handle to the size
   * of the area where output is to be sent.
   * @param d Dimension to draw to
   */
   public void setSize(Dimension d)
   {
       size = d;
   }

   private NbodyEnsemble save(int keepIndex, NbodyEnsemble proto)
   {
       // massive moons
       NbodyMass [] y = new NbodyMass[3];
       y[keepIndex] = proto.ensemble[keepIndex];

       int i = (keepIndex+1)%3;
       int j = (keepIndex+2)%3;

       double sumMass = proto.ensemble[i].mass + proto.ensemble[j].mass;
       double posx = (proto.ensemble[i].mass*proto.ensemble[i].x0.data[0] +
          proto.ensemble[j].mass*proto.ensemble[j].x0.data[0]);
       double posy = (proto.ensemble[i].mass*proto.ensemble[i].x0.data[1] +
          proto.ensemble[j].mass*proto.ensemble[j].x0.data[1]);
       double posz = (proto.ensemble[i].mass*proto.ensemble[i].x0.data[2] +
          proto.ensemble[j].mass*proto.ensemble[j].x0.data[2]);
       double posxdot = (proto.ensemble[i].mass*proto.ensemble[i].x0dot.data[0] +
                         proto.ensemble[j].mass*proto.ensemble[j].x0dot.data[0]);
       double posydot = (proto.ensemble[i].mass*proto.ensemble[i].x0dot.data[1] +
                         proto.ensemble[j].mass*proto.ensemble[j].x0dot.data[1]);
       double poszdot = (proto.ensemble[i].mass*proto.ensemble[i].x0dot.data[2] +
                         proto.ensemble[j].mass*proto.ensemble[j].x0dot.data[2]);


       y[i] = new NbodyMass();
       y[i].set(sumMass,
            new Vector3(posx/sumMass,posy/sumMass,posz/sumMass),
            new Vector3(posxdot/sumMass,posydot/sumMass,poszdot/sumMass));
       y[j] = new NbodyMass();
       y[j].set(0,
            new Vector3(posx/sumMass,posy/sumMass,posz/sumMass),
            new Vector3(posxdot/sumMass,posydot/sumMass,poszdot/sumMass));
       return new NbodyEnsemble(y, 0.001);
   }

}
/* end of file NbodyAnimation.java */

