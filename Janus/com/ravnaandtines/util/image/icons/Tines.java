package com.ravnaandtines.util.image.icons;

/**
*  Class Tines - ImageProducer that produces a Tine-pack 16x15 icon
*  <P>
*  Coded & copyright Mr. Tines &lt;tines@windsong.demon.co.uk&gt; 1998
*  All rights reserved.
*  <P>
*  This application is free software; you can redistribute it and/or
*  modify it under the terms of the GNU General Public
*  License as published by the Free Software Foundation; either
*  version 2 of the License, or (at your option) any later version.
*  <P>
*  This application is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
*  General Public License for more details.
*  <P>
*  You should have received a copy of the GNU General Public
*  License along with this library (file "COPYING"); if not,
*  write to the Free Software Foundation, Inc.,
*  59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*  <P>
*  This application is free software; the icons is derived
*  from artwork by Angela Taylor, based on a picture by Vernor Vinge
*  depicting the character Flenser from _A Fire upon the Deep_
*  <p>
* @author Mr. Tines
* @version 1.0 08-Nov-1998
*
*/

public class Tines extends com.ravnaandtines.util.image.IconProducer
{
    /**
    * Compressed encoded image
    */
    private static final byte[] image = {
    31,-117,8,0,0,0,0,0,0,0,-5,-1,127,-112,1,
    6,6,6,56,-90,-106,126,82,-51,-61,-90,-105,84,-3,48,
    -11,-60,-24,69,-73,3,-103,79,-118,126,108,-26,-31,-109,-61,
    -25,94,108,-6,9,-103,-119,79,63,62,-65,16,-93,-121,80,
    56,-112,-85,-105,20,-69,41,73,-105,52,2,0,-99,88,-41,
    -71,-64,3,0,0};

    /**
    * image width in pixels
    */
    private static final int width = 16;
    /**
    * image height in pixels
    */
    private static final int height = 15;

    /**
    * parameterless constructor only
    */
    public Tines()
    {
        super(image, width, height);
    }
} 