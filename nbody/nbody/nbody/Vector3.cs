//package UK.co.demon.windsong.tines.util
//import package UK.co.demon.windsong.tines.?
/**
*  Class Vector3
*
*  represents a cartesian 3-vector
*
*  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1997
*  All rights reserved.  For full licence details see file Main.java
*
* @author Mr. Tines
* @version 1.0 23-Nov-1997
*
*/

namespace nbody
{
  public class Vector3
  {
    internal double[] data;

    /**
    * Default constructor
    */

    public Vector3()
    {
      data = new double[3];
      for (int i = 0; i < 3; ++i) data[i] = 0.0;
    }

    /**
    * set values from primitive type
    * @param a,b,c double for x,y,z component
    * @return None
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
      set(a);
    }

    /**
    * set values from another vector3
    * @param x Vector3 for x,y,z component
    * @return None
    */

    public void set(Vector3 x)
    {
      for (int i = 0; i < 3; ++i) data[i] = x.data[i];
    }

    /**
    * add values from another vector3
    * @param x Vector3 for x,y,z component
    * @return None
    */

    public void add(Vector3 x)
    {
      for (int i = 0; i < 3; ++i) data[i] += x.data[i];
    }

    /**
    * add values from another vector3 scaled
    * @param x Vector3 for x,y,z component
    * @param m double scale factor
    * @return None
    */

    public void addm(Vector3 x, double m)
    {
      for (int i = 0; i < 3; ++i) data[i] += x.data[i] * m;
    }

    /**
    * scale then add values from another vector3
    * @param m double scale factor
    * @param x Vector3 for x,y,z component
    * @return None
    */

    public void madd(double m, Vector3 x)
    {
      for (int i = 0; i < 3; ++i)
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
    * @return None
    */

    public void maddv(double m, Vector3 x, double m2)
    {
      for (int i = 0; i < 3; ++i)
      {
        data[i] *= m;
        data[i] += x.data[i] / m2;
      }
    }

    /**
    * scale then add values from another vector3 scaled
    * @param m double scale factor
    * @param x Vector3 for x,y,z component
    * @param m2 double other scale factor
    * @return None
    */

    public void maddm(double m, Vector3 x, double m2)
    {
      for (int i = 0; i < 3; ++i)
      {
        data[i] *= m;
        data[i] += x.data[i] * m2;
      }
    }

    /**
    * subtract values from another vector3
    * @param x Vector3 for x,y,z component
    * @return None
    */

    public void sub(Vector3 x)
    {
      for (int i = 0; i < 3; ++i) data[i] -= x.data[i];
    }

    /**
    * multiply by a scalar
    * @param x double scale
    * @return None
    */

    public void mult(double x)
    {
      for (int i = 0; i < 3; ++i) data[i] *= x;
    }

    /**
    * divide by a scalar
    * @param x double scale
    * @return None
    */

    public void div(double x)
    {
      for (int i = 0; i < 3; ++i) data[i] /= x;
    }

    /**
    * dot product with another vector3
    * @param x Vector3 for x,y,z component
    * @return double dot product
    */

    public double dot(Vector3 x)
    {
      double v = 0;
      for (int i = 0; i < 3; ++i) v += data[i] * x.data[i];
      return v;
    }
  }
}

/* end of file vector3.java */