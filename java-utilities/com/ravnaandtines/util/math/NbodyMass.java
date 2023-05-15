package com.ravnaandtines.util.math;

/**
*  Class NbodyMass - A gravitating mass, based on the Fortran code by Sverre Aarseth,
*  published in Binney & Tremaine's <cite>Galactic Dynamics</cite>
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
public class NbodyMass
{
   double mass, t0, step, t1, t2, t3;
   Vector3 x, x0, x0dot, f, fdot, d1,d2,d3;
   double energy;

   /**
   * Default constructor
   */
   public NbodyMass()
   {
	    x = new Vector3();
        x0 = new Vector3();
	    x0dot = new Vector3();
	    f = new Vector3();
	    fdot = new Vector3();
	    d1 = new Vector3();
	    d2 = new Vector3();
	    d3 = new Vector3();
	    mass = 0;
	    energy = 0;
   }

   /**
   * Set initial conditions
   * @param m double mass
   * @param v Vector3 initial position
   * @param vdot vector3 initial velocity
   */
   public void set(double m, Vector3 v, Vector3 vdot)
   {
	    mass = m;
        x0.set(v);
        x0dot.set(vdot);
   }

   /**
   * Initialise integration steps
   */
   public void setStep()
   {
	    step = Math.sqrt(0.01*Math.sqrt(
		    f.dot(f)/d2.dot(d2)
		));
	    t0=0; // initial time
	    t1=t0-step;
	    t2=t1-step;
        t3=t2-step;

	    d1.set(fdot);
	    d1.addm(d3, step*step/6.0);
        d1.addm(d2, -step/2.0);
	    d2.addm(d3, -step);
	    d2.div(2.0);
	    d3.div(6.0);
	    f.div(2.0);
	    fdot.div(6.0);
   }

   /**
   * Compute kinetic energy; and set position at time
   * @param tnext double time for computation
   * @return double kinetic energy
   */
   public double kinetic(double tnext)
   {
	double dt = tnext - t0;
	Vector3 f2dot = new Vector3(d2);
	f2dot.addm(d3, (t0-t1) + (t0-t2));

	x.set(d3);
	x.maddv(0.05*dt, f2dot, 12.0);
	x.madd(dt, fdot);
	x.madd(dt, f);
	x.madd(dt, x0dot);
	x.madd(dt, x0);

	Vector3 a = new Vector3(d3);
	a.maddv(0.25*dt, f2dot, 3.0);
	a.maddm(dt, fdot, 3.0);
	a.maddm(dt, f, 2.0);
	a.madd(dt, x0dot);

	return energy = mass*a.dot(a)/2.0;
   }
   /**
   * Compute time at which particle is to be evaluated for consistency
   * @return timescale
   */
   public double timescale()
   {
	return t0+step;
   }

   /**
   * Compute position to first order in force
   * @param t double time of evaluation
   */
   public void firstorder(double t)
   {
	double s = t-t0;
	x.set(fdot);
    x.madd(s, f);
	x.madd(s, x0dot);
	x.madd(s, x0);
   }

   /**
   * Compute position to all orders in force
   * @param t double time of evaluation
   */
   public void thirdorder(double t)
   {
	double s = t-t0;
	Vector3 f2dot = new Vector3(d3);
	f2dot.madd( (t0-t1)+(t0-t2), d2);

	Vector3 temp = new Vector3(d3);
	temp.maddv(0.05*s,f2dot,12.0);
	x.addm(temp,s*s*s*s);

	temp.set(d3);
	temp.maddv(s/4.0,f2dot,3.0);
	temp.maddm(s,fdot,3.0);
	temp.maddm(s, f, 2.0);
	x0dot.addm(temp, s);
   }

   /**
   * Compute all parameters for updated force, position
   * @param t double time of evaluation
   * @param f1 vector3 force at that time
   */
   public void update(double t, Vector3 f1)
   {
	// tick the clock
	double dt  = t - t0;
	double dt1 = t - t1;
	double dt2 = t - t2;
	double dt3 = t - t3;
	double t1pr = t0-t1;
	double t2pr = t0-t2;
	double t3pr = t0-t3;
	t3 = t2;
	t2 = t1;
	t1 = t0;
	t0 = t;

	// new differences and 4th order semi-iteration
	Vector3 a = new Vector3(f1);
	a.addm(f, -2.0);
	a.div(dt);
	Vector3 a3 = new Vector3(a);
	a3.sub(d1);
	a3.div(dt1);
	Vector3 a6 = new Vector3(a3);
	a6.sub(d2);
	a6.div(dt2);
	Vector3 a9 = new Vector3(a6);
	a9.sub(d3);
	a9.div(dt3);

	d1.set(a);
	d2.set(a3);
	d3.set(a6);

	double k1dot = t1pr*t2pr*t3pr;
	double k2dot = (t1pr*t2pr+t3pr*(t1pr+t2pr));
	double k3dot = (t1pr+t2pr+t3pr);
	double x0fac = (((dt/30.0+0.05*k3dot)*dt+k2dot/12.0)*dt+k1dot/6.0)*dt*dt*dt;

	x0.set(x);
	x0.addm(a9,x0fac);

	double vfac = (((0.2*dt+0.25*k3dot)*dt+k2dot/3.0)*dt+0.5*k1dot)*dt*dt;
	x0dot.addm(a9, vfac);

	// set f, fdot and new timestep
	f.set(f1);
	f.div(2.0);

	fdot.set(d3);
	fdot.madd(dt1, d2);
	fdot.madd(dt, d1);
	fdot.div(6.0);

	a3.set(d3);
	a3.madd(dt+dt1, d2);
	a3.mult(2.0);

	step = Math.sqrt(0.01*Math.sqrt(
		f1.dot(f1)/a3.dot(a3)
		));

   }

   /**
   * Acessor for the current position
   * @return position 3-Vector
   */
   public Vector3 getPosition()
   {
      return x0;
   }

   /**
   * Acessor for the current velocity
   * @return position 3-Vector
   */
   public Vector3 getVelocity()
   {
      return x0dot;
   }
}

