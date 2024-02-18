//package UK.co.demon.windsong.tines.util
//import package UK.co.demon.windsong.tines.?

using System;

/**
*  Class NbodyEnsemble
*
*  An ensemble of gravitating masses, based on the Fortran code by Sverre Aarseth,
*  published in Binney & Tremaine's _Galactic Dynamics_
*
*  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1997
*  All rights reserved.  For full licence details see file Main.java
*
* @author Mr. Tines
* @version 1.0 dd-mmm-yyyy
*
*/

namespace nbody
{
  public class NbodyEnsemble
  {
    private NbodyMass[] ensemble;
    private double eps;
    private double energy;
    private double time, tnext;

    /**
    * Constructor; assumes masses constructed and
    * initial conditions set in the argument
    * @param x NbodyMass[] to manage
    * @param e double softening parameter
    */

    public NbodyEnsemble(NbodyMass[] x, double e)
    {
      ensemble = x;
      eps = e;
      int N = ensemble.Length;
      time = 0;
      tnext = 0;

      // set up force and first derivative for each body
      for (int i = 0; i < N; ++i)
      {
        Vector3 a, adot;
        for (int j = 0; j < N; ++j)
        {
          if (i == j) continue;

          a = new Vector3(ensemble[j].x0);
          a.sub(ensemble[i].x0);
          adot = new Vector3(ensemble[j].x0dot);
          adot.sub(ensemble[i].x0dot);
          double r = 1.0 / (a.dot(a) + eps);
          double a8 = ensemble[j].mass * r * Math.Sqrt(r);
          double a9 = 3.0 * r * adot.dot(a);
          ensemble[i].f.addm(a, a8);
          ensemble[i].fdot.addm(adot, a8);
          ensemble[i].fdot.addm(a, -a9 * a8);
        }
      }

      // set up 2nd and 3rd force derivatives
      for (int i = 0; i < N; ++i)
      {
        Vector3 a, adot, f, fdot;
        for (int j = 0; j < N; ++j)
        {
          if (i == j) continue;

          a = new Vector3(ensemble[j].x0);
          a.sub(ensemble[i].x0);
          adot = new Vector3(ensemble[j].x0dot);
          adot.sub(ensemble[i].x0dot);
          f = new Vector3(ensemble[j].f);
          f.sub(ensemble[i].f);
          fdot = new Vector3(ensemble[j].fdot);
          fdot.sub(ensemble[i].fdot);

          double r = 1.0 / (a.dot(a) + eps);
          double a14 = ensemble[j].mass * r * Math.Sqrt(r);
          double a15 = r * adot.dot(a);
          double a16 = (adot.dot(adot) + a.dot(f)) * r + a15 * a15;
          double a17 = (9.0 * adot.dot(f) + 3.0 * a.dot(fdot)) * r +
              a15 * (9.0 * a16 - 12 * a15 * a15);

          Vector3 f1dot = new Vector3(adot);
          f1dot.addm(a, -3.0 * a15);

          Vector3 f2dot = new Vector3(f);
          f2dot.addm(f1dot, -6.0 * a15);
          f2dot.addm(a, -3.0 * a16);
          f2dot.mult(a14);
          ensemble[i].d2.add(f2dot);

          Vector3 f3dot = new Vector3(fdot);
          f3dot.addm(f1dot, -9.0 * a16);
          f3dot.addm(a, -a17);
          f3dot.mult(a14);
          ensemble[i].d3.add(f3dot);
          ensemble[i].d3.addm(f2dot, -9.0 * a15);
        } // j
        ensemble[i].setStep();
      }
    }

    /**
    * construct from primitive type
    * @param deltat double for time increment
    * @return double energy at start of time step
    */

    public double advance(double deltat)
    {
      // compute initial energy
      energy = 0.0;
      int N = ensemble.Length;
      int i, j;
      for (i = 0; i < N; ++i)
      {
        energy += ensemble[i].kinetic(tnext);
        for (j = 0; j < i; ++j)
        {
          Vector3 s = new Vector3(ensemble[i].x);
          s.sub(ensemble[j].x);
          double pe = ensemble[i].mass * ensemble[j].mass / Math.Sqrt(s.dot(s) + eps);
          energy -= pe;
          ensemble[i].energy -= pe / 2.0;
          ensemble[j].energy -= pe / 2.0;
        }
      }
      tnext += deltat;
      bool end = false;

      do
      {
        // find body with shortest timescale
        time = 1.0e38;
        for (j = 0; j < N; ++j)
        {
          double t = ensemble[j].timescale();
          if (time < t) continue;
          i = j;
          time = t;
        }
        if (time > tnext)
        {
          time = tnext;
          end = true;
        }

        // predict coordinates to 1st order in force
        for (j = 0; j < N; ++j)
        {
          ensemble[j].firstorder(time);
        }

        // set the critical body accurately
        ensemble[i].thirdorder(time);

        // get the force on this body
        Vector3 f1 = new Vector3();
        for (j = 0; j < N; ++j)
        {
          if (j == i) continue;
          Vector3 a = new Vector3(ensemble[j].x);
          a.sub(ensemble[i].x);
          double r = 1.0 / (a.dot(a) + eps);
          double c = ensemble[j].mass * r * Math.Sqrt(r);
          f1.addm(a, c);
        }

        // and update all associated quantities
        ensemble[i].update(time, f1);
      } while (!end);
      return energy;
    }
  }
}

/* end of file NbodyEnsemble.java */