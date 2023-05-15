package com.ravnaandtines.util.math;

/**
*  Class NbodyEnsemble -  An ensemble of gravitating masses, based on
*  the Fortran code by Sverre Aarseth, published in Binney & Tremaine's
* <cite>Galactic Dynamics</cite>
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
* @version 1.0 23-Nov-1997
*/
public class NbodyEnsemble
{
   NbodyMass[] ensemble;
   double eps;
   double energy;
   double time, tnext;

   /**
   * Constructor; assumes masses constructed and 
   * initial conditions set in the arguments
   * @param x NbodyMass[] to manage
   * @param e double softening parameter
   */
   public NbodyEnsemble(NbodyMass[] x, double e)
   {
      ensemble = x;
      eps = e;
      int N = ensemble.length;
      time = 0;
      tnext = 0;

      // set up force and first derivative for each body
      for(int i=0; i<N; ++i)
      {
		Vector3 a, adot;
		for(int j=0; j<N; ++j)
		{
			if(i==j) continue;

			a = new Vector3(ensemble[j].x0);
			a.sub(ensemble[i].x0);
			adot = new Vector3(ensemble[j].x0dot);
			adot.sub(ensemble[i].x0dot);
			double r = 1.0/(a.dot(a)+eps);
			double a8 = ensemble[j].mass * r *Math.sqrt(r);
			double a9 = 3.0*r*adot.dot(a);
			ensemble[i].f.addm(a, a8);
			ensemble[i].fdot.addm(adot, a8);
			ensemble[i].fdot.addm(a, -a9*a8);
		}
      }

      // set up 2nd and 3rd force derivatives
      for(int i=0; i<N; ++i)
      {
		Vector3 a, adot, f, fdot;
		for(int j=0; j<N; ++j)
		{
			if(i==j) continue;

			a = new Vector3(ensemble[j].x0);
			a.sub(ensemble[i].x0);
			adot = new Vector3(ensemble[j].x0dot);
			adot.sub(ensemble[i].x0dot);
			f = new Vector3(ensemble[j].f);
			f.sub(ensemble[i].f);
			fdot = new Vector3(ensemble[j].fdot);
			fdot.sub(ensemble[i].fdot);

			double r = 1.0/(a.dot(a)+eps);
			double a14 = ensemble[j].mass * r *Math.sqrt(r);
			double a15 = r*adot.dot(a);
			double a16 = (adot.dot(adot) + a.dot(f))*r + a15*a15;
			double a17 = (9.0*adot.dot(f) + 3.0*a.dot(fdot))*r +
				a15*(9.0*a16 - 12*a15*a15);

			Vector3 f1dot = new Vector3(adot);
			f1dot.addm(a, -3.0*a15);

			Vector3 f2dot = new Vector3(f);
			f2dot.addm(f1dot, -6.0*a15);
			f2dot.addm(a, -3.0*a16);
			f2dot.mult(a14);
			ensemble[i].d2.add(f2dot);

			Vector3 f3dot = new Vector3(fdot);
			f3dot.addm(f1dot, -9.0*a16);
			f3dot.addm(a, -a17);
			f3dot.mult(a14);
			ensemble[i].d3.add(f3dot);
			ensemble[i].d3.addm(f2dot, -9.0*a15);
		} // j
		ensemble[i].setStep();
      }
   }

   /**
   * Performs time increment of system
   * @param deltat double for time increment
   * @return double total system energy at start of time step
   */
   public double advance(double deltat)
   {
      // compute initial energy
      energy = 0.0;
      int N = ensemble.length;
      int i, j;
      for(i=0; i<N; ++i)
      {
		energy += ensemble[i].kinetic(tnext);
		for(j=0; j<i; ++j)
		{
			Vector3 s = new Vector3(ensemble[i].x);
			s.sub(ensemble[j].x);
			double pe = ensemble[i].mass*ensemble[j].mass/Math.sqrt(s.dot(s)+eps);
			energy -= pe;
			ensemble[i].energy -= pe/2.0;
			ensemble[j].energy -= pe/2.0;
		}
      }
      tnext += deltat;
      boolean end = false;

      do{
		// find body with shortest timescale
		time = 1.0e38;
		for(j=0; j<N; ++j)
		{
			double t = ensemble[j].timescale();
			if(time < t) continue;
			i = j;
			time = t;
		}
		if(time > tnext)
		{
			time = tnext;
			end = true;
		}

		// predict coordinates to 1st order in force
		for(j=0; j<N; ++j)
		{
			ensemble[j].firstorder(time);
		}

		// set the critical body accurately
		ensemble[i].thirdorder(time);

		// get the force on this body
		Vector3 f1 = new Vector3();
		for(j=0; j<N; ++j)
		{
			if(j==i) continue;
			Vector3 a = new Vector3(ensemble[j].x);
			a.sub(ensemble[i].x);
			double r = 1.0/(a.dot(a)+eps);
			double c = ensemble[j].mass*r*Math.sqrt(r);
			f1.addm(a, c);
		}

		// and update all associated quantities
		ensemble[i].update(time, f1);

      }while(!end);
      return energy;
   }
}

/* end of file NbodyEnsemble.java */

