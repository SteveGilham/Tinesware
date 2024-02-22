//package UK.co.demon.windsong.tines.util
//import package UK.co.demon.windsong.tines.?

using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using System.Windows.Shapes;

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

namespace nbody
{
  public class NbodyAnimation
  {
    private Size size;
    private int side, halfSide;
    private NbodyEnsemble e0, e1;

    internal double theta = 0.0;  // parametrise
    private double r1 = 1.0;
    private double r2;
    internal bool begun = false;
    private String smashed = "";
    private int lost = -1;

    private double rad0, radA, radB;

    public NbodyAnimation()
    {
      reset();
      r2 = 825 / 587.4;
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

      rad0 = 13750.0 / (2.0 * 587400.0);
      radA = 7243.0 / (2.0 * 587400.0);
      radB = 4960.0 / (2.0 * 587400.0);

      // in our system G = 1, and so given mass, distance scale
      // appropriately to get the real world numbers

      // very light body test case
      NbodyMass[] x = new NbodyMass[3];
      x[0] = new NbodyMass();
      x[0].set(1.0, new Vector3(0.0, 0.0, 0.0), new Vector3(0.0, 0.0, 0.0));
      x[1] = new NbodyMass();
      x[1].set(0.0, new Vector3(1.0, 0.0, 0.0), new Vector3(0.0, 1.0, 0.0));

      double v = Math.Sqrt(1.0 / r2);
      x[2] = new NbodyMass();
      x[2].set(0.0, new Vector3(r2 * Math.Cos(theta), r2 * Math.Sin(theta), 0.0),
             new Vector3(-v * Math.Sin(theta), v * Math.Cos(theta), 0.0));
      e0 = new NbodyEnsemble(x, 0.0);

      // massive moons
      NbodyMass[] y = new NbodyMass[3];
      y[1] = new NbodyMass();
      y[1].set(0.9827 / 6.799,
           new Vector3(1.0, 0.0, 0.0),
           new Vector3(0.0, 1.0, 0.0));

      r2 = 825 / 587.4;
      y[2] = new NbodyMass();
      y[2].set(0.303 / 6.799,
           new Vector3(r2 * Math.Cos(theta), r2 * Math.Sin(theta), 0.0),
           new Vector3(-v * Math.Sin(theta), v * Math.Cos(theta), 0.0));

      y[0] = new NbodyMass();
      Vector3 v0 = new Vector3(y[1].x0dot);
      v0.maddm(-0.9827 / 6.799, y[2].x0dot, -0.303 / 6.799);
      y[0].set(1.0, new Vector3(0.0, 0.0, 0.0), v0);
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

    public void paint(Panel g)
    {
      size = g.RenderSize;

      side = (int)((size.Width / 2 < size.Height) ? size.Width / 2 : size.Height);
      if (side < 300) return;

      halfSide = (int)(0.45 * side);
      int margin = side / 2 - halfSide;
      int x1, y1, x2;
      int d1;

      double energy0 = 0.0;
      double energy1 = 0.0;
      Vector3 v0, v1, v2, w0, w1, w2;

      //g.setColor(Color.lightGray);
      //String str = "Moons of negligible mass";
      //g.drawChars(str.toCharArray(), 0, str.length(), 0, margin);

      //str = "Moons of stated mass";
      //g.drawChars(str.toCharArray(), 0, str.length(), side, margin);

      if (begun)
      {
        energy0 = e0.advance(0.003);
        energy1 = e1.advance(0.003);
        v0 = new Vector3(e0.ensemble[0].x0);
        v1 = new Vector3(e0.ensemble[1].x0);
        v2 = new Vector3(e0.ensemble[2].x0);
        w0 = new Vector3(e1.ensemble[0].x0);
        w1 = new Vector3(e1.ensemble[1].x0);
        w2 = (e1.ensemble.Length == 3) ? new Vector3(e1.ensemble[2].x0) : w1;
      }
      else
      {
        v0 = new Vector3(0.0, 0.0, 0.0);
        v1 = new Vector3(1.0, 0.0, 0.0);
        v2 = new Vector3(r2 * Math.Cos(theta), r2 * Math.Sin(theta), 0.0);
        w0 = new Vector3(0.0, 0.0, 0.0);
        w1 = new Vector3(1.0, 0.0, 0.0);
        w2 = new Vector3(r2 * Math.Cos(theta), r2 * Math.Sin(theta), 0.0);
      }

      double scale = v0.dot(v0);
      double s1 = v1.dot(v1);
      if (s1 > scale) scale = s1;
      s1 = v2.dot(v2);
      if (s1 > scale) scale = s1;
      s1 = w0.dot(w0);
      if (s1 > scale) scale = s1;
      s1 = w1.dot(w1);
      if (s1 > scale) scale = s1;
      s1 = w2.dot(w2);
      if (s1 > scale) scale = s1;

      scale = Math.Sqrt(scale);
      if (scale < 2.0) scale = 2.0; // circle goes to +/- scale
      else if (scale < 5.0) scale = 5.0;
      else if (scale < 10.0) scale = 10.0;
      else if (scale < 20.0) scale = 20.0;
      else if (scale < 50.0) scale = 50.0;
      else if (scale < 100.0) scale = 100.0;
      else if (scale < 200.0) scale = 200.0;
      else if (scale < 500.0) scale = 500.0;
      else if (scale < 1000.0) scale = 1000.0;
      else if (scale < 2000.0) scale = 2000.0;
      else if (scale < 5000.0) scale = 5000.0;
      else if (scale < 10000.0) scale = 10000.0;

      double r = halfSide / scale;  // unit distance

      x1 = (side / 2);
      d1 = (int)(r * r1);

      var o1 = (EllipseGeometry)((Path)g.FindName("OrbitAL")).Data;
      o1.Center = new Point(x1, x1);
      o1.RadiusX = o1.RadiusY = d1;

      var o2 = (EllipseGeometry)((Path)g.FindName("OrbitAR")).Data;
      o2.Center = new Point(x1 + side, x1);
      o2.RadiusX = o2.RadiusY = d1;

      x1 = (side / 2);
      d1 = (int)(r * r2);

      o1 = (EllipseGeometry)((Path)g.FindName("OrbitBL")).Data;
      o1.Center = new Point(x1, x1);
      o1.RadiusX = o1.RadiusY = d1;

      o2 = (EllipseGeometry)((Path)g.FindName("OrbitBR")).Data;
      o2.Center = new Point(x1 + side, x1);
      o2.RadiusX = o2.RadiusY = d1;

      var planet = (EllipseGeometry)((Path)g.FindName("PlanetL")).Data;
      x1 = (side / 2) + (int)(v0.data[0] * r);
      y1 = (side / 2) - (int)(v0.data[1] * r);
      planet.Center = new Point(x1, y1);
      planet.RadiusX = planet.RadiusY = 8;

      planet = (EllipseGeometry)((Path)g.FindName("PlanetR")).Data;
      x1 = (side / 2) + (int)(w0.data[0] * r);
      y1 = (side / 2) - (int)(w0.data[1] * r);
      planet.Center = new Point(x1 + side, y1);
      planet.RadiusX = planet.RadiusY = 8;

      planet = (EllipseGeometry)((Path)g.FindName("MoonBL")).Data;
      x1 = (side / 2) + (int)(v1.data[0] * r);
      y1 = (side / 2) - (int)(v1.data[1] * r);
      planet.Center = new Point(x1, y1);
      planet.RadiusX = planet.RadiusY = 6;

      ((Path)g.FindName("MoonBR")).Visibility = (lost != 1) ? Visibility.Visible : Visibility.Collapsed;
      if (lost != 1)
      {
        planet = (EllipseGeometry)((Path)g.FindName("MoonBR")).Data;
        x1 = (side / 2) + (int)(w1.data[0] * r);
        y1 = (side / 2) - (int)(w1.data[1] * r);
        planet.Center = new Point(x1 + side, y1);
        planet.RadiusX = planet.RadiusY = 6;
      }

      planet = (EllipseGeometry)((Path)g.FindName("MoonAL")).Data;
      x1 = (side / 2) + (int)(v2.data[0] * r);
      y1 = (side / 2) - (int)(v2.data[1] * r);
      planet.Center = new Point(x1, y1);
      planet.RadiusX = planet.RadiusY = 5;

      ((Path)g.FindName("MoonAR")).Visibility = (lost != 2) ? Visibility.Visible : Visibility.Collapsed;
      if (lost != 2)
      {
        planet = (EllipseGeometry)((Path)g.FindName("MoonAR")).Data;
        x1 = (side / 2) + (int)(w2.data[0] * r);
        y1 = (side / 2) - (int)(w2.data[1] * r);
        planet.Center = new Point(x1 + side, y1);
        planet.RadiusX = planet.RadiusY = 5;
      }

      // Moon a mass 9.827e23 kg, orbit radius 587400km period 47 days
      double l = 1.0 / 0.5874;
      int pl = (int)(l * r);
      x1 = side - (pl / 2);
      x2 = x1 + pl;

      var tl = (Line)g.FindName("tl");
      var tr = (Line)g.FindName("tr");
      var scaled = (Line)g.FindName("scale");
      scaled.X1 = tl.X1 = tl.X2 = x1;
      scaled.X2 = tr.X1 = tr.X2 = x2;
      tl.Y1 = tr.Y1 = side - margin - 3;
      tl.Y2 = tr.Y2 = side - margin + 3;
      scaled.Y1 = scaled.Y2 = side - margin;

      var elapsed = (Label)g.FindName("Elapsed");
      var energy = (Label)g.FindName("Energy");
      var status = (Label)g.FindName("Status");

      if (!begun)
      {
        elapsed.Content = "0";
        energy.Content = "n/a      ";
        status.Content = String.Empty;
      }
      else
      {
        double t = 47.0 * e0.time / (2.0 * Math.PI);
        elapsed.Content = t.ToString("F3");
        energy.Content = energy1.ToString("F8");
        Vector3 work = new Vector3(w0);
        work.sub(w1);

        double radXX = Math.Sqrt(work.dot(work));
        var moon1 = radXX;
        if (radXX < rad0 + radA)
        {
          smashed = " Moon A has hit the planet";
          lost = 1;
          e1 = save(2, e1);
        }

        work.sub(w2);
        radXX = Math.Sqrt(work.dot(work));
        var moon2 = radXX;
        if (radXX < rad0 + radB)
        {
          smashed = " Moon B has hit the planet";
          lost = 2;
          e1 = save(1, e1);
        }

        work.set(w1);
        work.sub(w2);
        radXX = Math.Sqrt(work.dot(work));
        if (radXX < radA + radB)
        {
          smashed = " Moons have collided";
          lost = 2;
          e1 = save(0, e1);
        }

        var s = String.Empty;
        s += smashed;

        var farmoon = moon1 > moon2 ? "A" : "B";

        if (scale > 25 && smashed.Length == 0)
        {
          if (e1.ensemble[0].energy > 0
          && e1.ensemble[0].energy > e1.ensemble[2].energy
          && e1.ensemble[0].energy > e1.ensemble[1].energy
              ) { s += " Moons have escaped"; }
          else if (e1.ensemble[1].energy > 0
          && e1.ensemble[1].energy > e1.ensemble[0].energy
          && e1.ensemble[1].energy > e1.ensemble[2].energy
              ) { s += " Moon " + farmoon + " has escaped"; }
          else if (e1.ensemble[2].energy > 0
          && e1.ensemble[2].energy > e1.ensemble[0].energy
          && e1.ensemble[2].energy > e1.ensemble[1].energy
              ) { s += " Moon " + farmoon + " has escaped"; }
        }

        status.Content = s;
      }
    }

    private NbodyEnsemble save(int keepIndex, NbodyEnsemble proto)
    {
      // massive moons
      if (proto.ensemble.Length < 3)
        return proto;

      NbodyMass[] y = new NbodyMass[2];
      y[keepIndex] = proto.ensemble[keepIndex];
      int other = 0;

      if (keepIndex == 0)
      {
        other = 1;
      }

      int i = (keepIndex + 1) % 3;
      int j = (keepIndex + 2) % 3;

      double sumMass = proto.ensemble[i].mass + proto.ensemble[j].mass;
      double posx = (proto.ensemble[i].mass * proto.ensemble[i].x0.data[0] +
         proto.ensemble[j].mass * proto.ensemble[j].x0.data[0]);
      double posy = (proto.ensemble[i].mass * proto.ensemble[i].x0.data[1] +
         proto.ensemble[j].mass * proto.ensemble[j].x0.data[1]);
      double posz = (proto.ensemble[i].mass * proto.ensemble[i].x0.data[2] +
         proto.ensemble[j].mass * proto.ensemble[j].x0.data[2]);
      double posxdot = (proto.ensemble[i].mass * proto.ensemble[i].x0dot.data[0] +
                        proto.ensemble[j].mass * proto.ensemble[j].x0dot.data[0]);
      double posydot = (proto.ensemble[i].mass * proto.ensemble[i].x0dot.data[1] +
                        proto.ensemble[j].mass * proto.ensemble[j].x0dot.data[1]);
      double poszdot = (proto.ensemble[i].mass * proto.ensemble[i].x0dot.data[2] +
                        proto.ensemble[j].mass * proto.ensemble[j].x0dot.data[2]);

      y[other] = new NbodyMass();
      y[other].set(sumMass,
           new Vector3(posx / sumMass, posy / sumMass, posz / sumMass),
           new Vector3(posxdot / sumMass, posydot / sumMass, poszdot / sumMass));
      return new NbodyEnsemble(y, 0.001);
    }
  }
}

/* end of file NbodyAnimation.java */