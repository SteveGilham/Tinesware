package com.ravnaandtines.util.gui;
import java.awt.Color;

/**
*  Class Colour - This is a class that contains some colour manipulations
* - conversions with HLS, HSV and RGB
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
* @version 1.0 24-Jul-1997
*/


public class Colour
{
    private Colour()
    {
        rgbSet=true;
        red = green = blue = 0;
        hlsSet=false;
        hsvSet=false;
    }

    /**
    * Constructs and returns a new colour with the given RGB
    * values in the range 0-1.  These statics all have the same argument types
    * so we cannot just make them constructors.
    * @param r red component
    * @param g green component
    * @param b blue component
    * @return Colour thus described
    */
    public static Colour rgb (double r, double g, double b)
    {
        return new Colour(r,g,b);
    }
    /**
    * Constructs and returns a new colour with the given HSV H in range 0-360, other
    * values in the range 0-1.  These statics all have the same argument types
    * so we cannot just make them constructors.
    * @param h hue component
    * @param s saturation component
    * @param v brightness component
    * @return Colour thus described
    */
    public static Colour hsv (double h, double s, double v)
    {
        return new Colour(h,s,v,Color.black);
    }
    /**
    * Constructs and returns a new colour with the given HLS H in range 0-360, other
    * values in the range 0-1.  These statics all have the same argument types
    * so we cannot just make them constructors.
    * @param h hue component
    * @param l lightness component
    * @param s saturation component
    * @return Colour thus described
    */
    public static Colour hls (double h, double l, double s)
    {
        return new Colour(h,l,s,0);
    }

    /**
    * Constructs and returns a new colour with rgb values of the Color
    * @param c Color to match
    * @return Colour thus described
    */
    public static Colour build (Color c)
    {
        return rgb(c.getRed()/255.0, c.getGreen()/255.0, c.getBlue()/255.0);
    }

    /**
    * Constructs and returns a new Color with rgb values of this Colour
    * @return Color thus described
    */
    public Color  color()
    {
        int rr = (int)(255*r()+0.5);
        if(rr > 255) rr = 255;
        else if (rr < 0) rr = 0;

        int gg = (int)(255*g()+0.5);
        if(gg > 255) gg = 255;
        else if (gg < 0) gg = 0;

        int bb = (int)(255*b()+0.5);
        if(bb > 255) bb = 255;
        else if (bb < 0) bb = 0;

        return new Color( rr, gg, bb);
    }

    /**
    * Returns the red component of the colour (0-1)
    * @return red
    */
    public double r ()
    {
        if (!rgbSet) calcRGB ();
        return red;
    }
    /**
    * Returns the green component of the colour (0-1)
    * @return green
    */
    public double g ()
    {
        if (!rgbSet) calcRGB ();
        return green;
    }
    /**
    * Returns the blue component of the colour (0-1)
    * @return blue
    */
    public double b ()
    {
        if (!rgbSet) calcRGB ();
        return blue;
    }



    /**
    * Returns the hue component of the colour (0-360) in HLS model
    * @return hue
    */
    public double h_hls ()
    {
        if (!hlsSet) calcHLS ();
        return hls_h;
    }
    /**
    * Returns the lightness component of the colour (0-1)
    * @return lightness
    */
    public double l_hls ()
    {
        if (!hlsSet) calcHLS ();
        return hls_l;
    }
    /**
    * Returns the saturation component of the colour (0-1)
    * @return saturation
    */
    public double s_hls ()
    {
        if (!hlsSet) calcHLS ();
        return hls_s;
    }

    /**
    * Returns the hue component of the colour (0-360) in HSV model
    * @return hue
    */
    public double h_hsv ()
    {
        if (!hsvSet) calcHSV ();
        return hsv_h;
    }
    /**
    * Returns the saturation component of the colour (0-1)
    * @return saturation
    */
    public double s_hsv ()
    {
        if (!hsvSet) calcHSV ();
        return hsv_s;
    }
    /**
    * Returns the vrightness component of the colour (0-1)
    * @return brightness
    */
    public double v_hsv ()
    {
        if (!hsvSet) calcHSV ();
        return hsv_v;
    }

    private Colour (double r, double g, double b)
    {
        rgbSet = true;
        red = r;
        green = g;
        blue = b;
        hlsSet = hsvSet = false;
    }
    private Colour (double h, double s, double v, Color dummy)
    {
        rgbSet = hlsSet = false;
        hsvSet = true;
        hsv_h = h;
        hsv_s = s;
        hsv_v = v;
    }
    private Colour (double h, double l, double s, int dummy)
    {
        rgbSet = hsvSet;
        hlsSet = true;
        hls_h = h;
        hls_l = l;
        hls_s = s;
    }

// Internals
    private void calcHSV()
    {
        if (hsvSet)
            return;

        if (hlsSet)
        {
            hsv_h = hls_h;

            if (hls_s == 0 || hls_l == 0)
            {
                hsv_s = 0;
                hsv_v = hls_l;
            }
            else
            {
                // chromatic case
                double cMax = (hls_l<=0.5) ? hls_l*(1 + hls_s) :
                    hls_l + hls_s - (hls_l*hls_s);
                double cMin = 2*hls_l-cMax;
                double delta = cMax - cMin;
                hsv_s = delta/cMax;
                hsv_v = cMax;
            }
        }
        else // rgbSet
        {
            /* calculate lightness */
            double cMax = blue>green ? (blue>red ? blue : red) : (green>red ? green : red);
            double cMin = blue<green ? (blue<red ? blue : red) : (green<red ? green : red);

            // Value
            hsv_v = cMax;

            // Hue and saturation
            if (cMax == cMin)
            {
                hsv_s = 0;
                hsv_h = 0;
            }
            else
            {
                // Chromatic case
                double delta = cMax - cMin;
                hsv_s = delta/cMax;

                if (red==cMax)
                    hsv_h=(60*(green-blue)/delta); //degrees
                else if(green==cMax)
                    hsv_h=(120+60*(blue-red)/delta);
                else
                    hsv_h=(240+60*(red-green)/delta);
            }
        }
        hsvSet=true;
    }

    private void calcHLS()
    {
        if (hlsSet)
            return;

        if (hsvSet)
        {
            hls_h = hsv_h;

            if (hsv_s == 0 || hsv_v == 0)
            {
                hls_s = 0;
                hls_l = hsv_v;
            }
            else
            {
                double cMax=hsv_v;
                double cMin=hsv_v*(1-hsv_s);
                double delta = cMax - cMin;

                hls_l = (cMax+cMin)/2;
                if( hls_l <= 0.5 )
                    hls_s = delta / (cMax+cMin);
                else
                    hls_s = delta / (2-cMax-cMin);
            }
        }
        else // rgbSet
        {
            /* calculate lightness */
            double cMax = blue>green ? (blue>red ? blue : red) : (green>red ? green : red);
            double cMin = blue<green ? (blue<red ? blue : red) : (green<red ? green : red);

            hls_l = (cMax+cMin)/2;

            if (cMax == cMin)
            {
                hls_s = 0;
                hls_h = 0;
            }
            else
            {
                /* chromatic case */
                double delta = cMax - cMin;
                /* saturation */
                if( hls_l <= 0.5 )
                    hls_s = delta / (cMax+cMin);
                else
                    hls_s = delta / (2-cMax-cMin);

                /* hue */
                if( red == cMax )
                    hls_h = (60*(green-blue)/delta);
                else if ( green == cMax )
                    hls_h = (120+60*(blue-red)/delta);
                else
                    hls_h = (240+60*(red-green)/delta);
            }
        }
        hlsSet=true;
    }

    private void calcRGB()
    {
        if (rgbSet)
            return;

        if (hlsSet)
        {
            if (hls_s == 0 || hls_l == 0)
            {
                red = green = blue = hls_l;
            }
            else
            {
                // chromatic case
                double cMax = (hls_l<=0.5) ? hls_l*(1 + hls_s) : hls_l + hls_s - (hls_l*hls_s);
                double cMin = 2*hls_l-cMax;

                double hue=hls_h/60+6;
                int i= (int)hue;
                double f = hue - i;

                switch (i%6)
                {
                case 0:
                    red   = cMax;
                    green = cMin + (cMax-cMin)*f;
                    blue  = cMin;
                    break;
                case 1:
                    red  = cMin + (cMax-cMin)*(1-f);
                    green = cMax;
                    blue   = cMin;
                    break;
                case 2:
                    red  = cMin;
                    green = cMax;
                    blue   = cMin + (cMax-cMin)*f;
                    break;
                case 3:
                    red  = cMin;
                    green = cMin + (cMax-cMin)*(1-f);
                    blue   = cMax;
                    break;
                case 4:
                    red  = cMin + (cMax-cMin)*f;
                    green = cMin;
                    blue   = cMax;
                    break;
                case 5:
                    red  = cMax;
                    green = cMin;
                    blue   = cMin + (cMax-cMin)*(1-f);
                    break;
                }
            }
        }
        else // hsvSet
        {
            if (hsv_s == 0 || hsv_v == 0)
            {
                red = green = blue = hsv_v;
            }
            else
            {
                double cMax=hsv_v;
                double cMin=hsv_v*(1-hsv_s);

                double hue=hsv_h/60+6;
                // chromatic case
                int i=(int)hue;
                double f = hue - i;

                switch (i%6)
                {
                case 0:
                    red   = cMax;
                    green = cMin + (cMax-cMin)*f;
                    blue  = cMin;
                    break;
                case 1:
                    red  = cMin + (cMax-cMin)*(1-f);
                    green = cMax;
                    blue   = cMin;
                    break;
                case 2:
                    red  = cMin;
                    green = cMax;
                    blue   = cMin + (cMax-cMin)*f;
                    break;
                case 3:
                    red  = cMin;
                    green = cMin + (cMax-cMin)*(1-f);
                    blue   = cMax;
                    break;
                case 4:
                    red  = cMin + (cMax-cMin)*f;
                    green = cMin;
                    blue   = cMax;
                    break;
                case 5:
                    red  = cMax;
                    green = cMin;
                    blue   = cMin + (cMax-cMin)*(1-f);
                    break;
                }
            }
        }
        rgbSet=true;
    }

    private boolean rgbSet;
    private double red;
    private double green;
    private double blue;
    private boolean hlsSet;
    private double hls_h;
    private double hls_l;
    private double hls_s;
    private boolean hsvSet;
    private double hsv_h;
    private double hsv_s;
    private double hsv_v;
}
