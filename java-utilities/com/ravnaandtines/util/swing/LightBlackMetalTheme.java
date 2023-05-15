package com.ravnaandtines.util.swing;

import javax.swing.plaf.metal.*;
import javax.swing.plaf.*;
import java.awt.SystemColor;
import java.awt.Font;

/**
*  Class LightBlackMetalTheme - a java Look and Feel theme that does away
* with indiscriminate use of bold text and lilac text.
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
* @version 1.0 08-Nov-1998
*/

public class LightBlackMetalTheme extends DefaultMetalTheme
{
    /**
    * Give a name to this Metal L&F variant
    * @return String giving theme name
    */
    public String getName() { return "Light Black Metal Theme"; }

    /**
    * A normal weight font
    */
    private final FontUIResource controlFont = new FontUIResource("sansserif", Font.PLAIN, 11);

    /**
    * Make control fonts normal weight
    * @return FontUIResource the normal weight font defined locally
    */
    public FontUIResource getControlTextFont() { return controlFont;}
    /**
    * Make menu items normal weight
    * @return FontUIResource the normal weight font defined locally
    */
    public FontUIResource getMenuTextFont() { return controlFont;}
    /**
    * Make System text normal weight
    * @return FontUIResource the normal weight font defined locally
    */
    public FontUIResource getSystemTextFont() { return controlFont;}
    /**
    * Make user text normal weight
    * @return FontUIResource the normal weight font defined locally
    */
    public FontUIResource getUserTextFont() { return controlFont;}

    /**
    * no lilac text!  Strewth!
    * @return ColorUIResource the control text colout
    */
    public ColorUIResource getSystemTextColor() {return getControlTextColor();}
}
