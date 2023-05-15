package com.ravnaandtines.util.math;

/**
*  Class Vector3 - represents a cartesian 3-vector
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
public class Vector3
{
	double[] data;

   /**
   * Default constructor
   */
   public Vector3()
   {
	    data = new double[3];
	    for(int i=0; i<3; ++i) data[i] = 0.0;
   }

   /**
   * set values from primitive type
   * @param a,b,c double for x,y,z component
   */
   public void set(double a, double b, double c)
   {
	    data[0] = a;
	    data[1] = b;
	    data[2] = c;
   }

   /**
   * construct from primitive type
   * @param a,b,c double for x,y,z component
   */
   public Vector3(double a, double b, double c)
   {
	    this();
	    data[0] = a;
	    data[1] = b;
	    data[2] = c;
   }

   /**
   * copy construct
   * @param a Vector3 for x,y,z component
   */
   public Vector3(Vector3 a)
   {
	    this();
	    set(a);
   }

   /**
   * set values from another vector3
   * @param x Vector3 for x,y,z component
   */
   public void set(Vector3 x)
   {
      for(int i=0; i<3; ++i) data[i] = x.data[i];
   }

   /**
   * add values from another vector3
   * @param x Vector3 for x,y,z component
   */
   public void add(Vector3 x)
   {
      for(int i=0; i<3; ++i) data[i] += x.data[i];
   }

   /**
   * add values from another vector3 scaled
   * @param x Vector3 for x,y,z component
   * @param m double scale factor
   * @return None
   */
   public void addm(Vector3 x, double m)
   {
      for(int i=0; i<3; ++i) data[i] += x.data[i]*m;
   }


   /**
   * scale then add values from another vector3
   * @param m double scale factor
   * @param x Vector3 for x,y,z component
   */
   public void madd(double m, Vector3 x)
   {
      for(int i=0; i<3; ++i)
      {
        data[i] *= m;
		data[i] += x.data[i];
      }
   }

   /**
   * scale then add values from another vector3 scaled
   * @param m double scale factor
   * @param x Vector3 for x,y,z component
   * @param m2 double other scale factor
   */
   public void maddv(double m, Vector3 x, double m2)
   {
      for(int i=0; i<3; ++i)
      {
		data[i] *= m;
		data[i] += x.data[i]/m2;
      }
   }
   /**
   * scale then add values from another vector3 scaled
   * @param m double scale factor
   * @param x Vector3 for x,y,z component
   * @param m2 double other scale factor
   */
   public void maddm(double m, Vector3 x, double m2)
   {
      for(int i=0; i<3; ++i) 
      {
		data[i] *= m;
		data[i] += x.data[i]*m2;
      }
   }

   /**
   * subtract values from another vector3
   * @param x Vector3 for x,y,z component
   */
   public void sub(Vector3 x)
   {
      for(int i=0; i<3; ++i) data[i] -= x.data[i];
   }

   /**
   * multiply by a scalar
   * @param x double scale
   */
   public void mult(double x)
   {
      for(int i=0; i<3; ++i) data[i] *= x;
   }

   /**
   * divide by a scalar
   * @param x double scale
   */
   public void div(double x)
   {
      for(int i=0; i<3; ++i) data[i] /= x;
   }

   /**
   * dot product with another vector3
   * @param x Vector3 for x,y,z component
   * @return double dot product
   */
   public double dot(Vector3 x)
   {
      double v = 0;
      for(int i=0; i<3; ++i) v+= data[i]*x.data[i];
	 return v;
   }
}
/* end of file vector3.java */

