/**
*  Class ClassName 
*
*  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1998
*  All rights reserved.  For full licence details see file Main.java
*
* @author Mr. Tines
* @version 1.0 4-Apr-98
*
*/

public class E2Param
{
    public static int domeRadius = 20;   // e6 meters
    public static boolean yuthu = false;  // sun overhead in yuthuppa midsummer
    public static double winterTilt = -10.6; //deg
    public static double equinoxTilt = 0;
    public static double summerTilt = 9.0;

    public static double summerDay = 0.1;       // deviation from 0.5 days
    public static double winterDay = -1.06/9.0;

    public static boolean uleria = false; // sidereal period

    public static double shargashRise = 0.0;
    public static double twinRise = 0.0;
    public static double artiaRise = 0.0;

    public static double getHarmonic(int week, int day, double hour)
	  {
        double dday = (7.0*week+day);
        dday+=hour; // days since midnight on 1st day of year
		    // Spring equinox is dday == 0.5
		    // Summer solstice is dday == 68.0, 9 deg N after 67.5 days
		    // Autumn equinox is dday == 135.5 after 67.5 days
		    // Winter solstice is dday == 215 10.6 deg S after 79.5 days
		    // Spring equinox is dday == 294.5 after 79.5 days
        // We could fix up a smoother function some time

        double cycle, amplitude, harmonic;
        if(dday < 0.5) dday += 294;
        if(dday < 135.5)
        {
            cycle = (dday-0.5)*Math.PI/135.0;
            harmonic = Math.sin(cycle);
        }
        else
        {
            cycle = (dday-135.5)*Math.PI/159.0;
            harmonic = -Math.sin(cycle);
        }
		    return harmonic;
    }

    public static double getTilt(double harmonic) // deg
    {
       if(!yuthu)
       {
          if(harmonic >= 0) return equinoxTilt + (summerTilt-equinoxTilt)*harmonic;
          else return equinoxTilt - (winterTilt-equinoxTilt)*harmonic;
       }
       else
       {
          double plus = Math.asin(3.6/domeRadius)*180.0/Math.PI;
          if(harmonic >= 0) return plus*harmonic;
          else return plus*harmonic*10.6/9;
       }
    }

    public static double getDayLength(double harmonic) // days
    {
       if(harmonic >= 0) return 0.5 + summerDay*harmonic;
       else return 0.5 - winterDay*harmonic;
    }
}

/* end of file E2Param.java */
