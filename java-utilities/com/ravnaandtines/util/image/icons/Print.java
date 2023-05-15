package com.ravnaandtines.util.image.icons;

/**
*  Class Print - ImageProducer that produces a printer Icon
*  <P>
*  Coded & copyright Mr. Tines &lt;tines@windsong.demon.co.uk&gt; 1998
*  All rights reserved.
*  <p>
*  This class is free software; the icon is derived
*  by taking the appropriate .gif images from the emerging
*  Java Freeware standard from
*  <a href="http://www.javalobby.org/jfa/projects/icons/index.html">
*  http://www.javalobby.org/jfa/projects/icons/index.html</a> and replacing
*  the encoding by local GZIP'd bytestreams, and is thus
*  derivative of that source for copyright purposes.
* <p>
*  The original icon images are noted as Copyright(C) 1998  by  Dean S. Jones
*  &ltdean@gallant.com&gt;, and are offered for free use in Java freeware.
*  There are no more restrictions placed on this version of the image than
*  are placed on the original
*  <P>
* THIS SOFTWARE IS PROVIDED BY THE AUTHORS ''AS IS'' AND ANY EXPRESS
* OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
* ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHORS OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
* BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
* WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
* OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
* EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*  <p>
* @author Mr. Tines
* @version 1.0 09-Nov-1998
*/

public class Print extends com.ravnaandtines.util.image.IconProducer
{
    /**
    * Compressed encoded image
    */
    private static final byte[] image = {
    31,-117,8,0,0,0,0,0,0,0,-99,-108,77,10,-62,
    48,16,70,115,-90,-98,66,60,70,-74,66,15,-32,-54,19,
    116,-95,-73,-88,11,-23,-90,-40,109,69,-60,77,65,112,41,
    8,46,122,6,119,99,-65,-40,-119,-51,52,-3,-119,-123,71,
    -76,78,-97,95,50,105,-44,70,41,-27,-127,-14,60,39,-83,
    53,53,95,39,-119,-94,8,-11,62,-113,-29,67,109,-102,-90,
    68,-105,-116,-86,-86,-94,-70,-82,13,-14,66,29,-2,123,-64,
    -23,100,75,-16,64,-29,-61,56,4,-22,-30,56,-10,-7,-116,
    107,44,-101,-52,55,50,95,-101,-53,-82,-37,-29,97,124,24,
    37,-35,108,-83,-81,-21,-20,-83,63,103,43,-53,-46,34,115,
    -94,110,-71,-4,-11,-91,-103,115,-49,101,-99,69,65,-76,79,
    -52,104,-111,57,91,-33,110,-3,69,121,92,-17,87,102,106,
    -26,114,58,46,-52,58,-98,83,-27,-28,97,23,126,15,-127,
    93,-121,-83,-21,-5,-9,-126,11,57,-92,15,-9,-26,-14,-68,
    39,6,118,-31,-13,-112,-17,118,93,-11,-32,-25,-127,-42,-54,
    -64,62,-66,-17,-13,77,-71,24,-12,-78,-21,-125,75,-6,124,
    -82,41,47,-9,1,125,-31,-3,-62,62,-34,67,-95,112,-97,
    -7,61,-23,-18,-105,-112,125,-126,108,-20,19,-17,-100,-29,68,
    -35,28,60,-82,-65,-50,-50,-103,103,-87,115,-74,-124,48,114,
    46,-37,-7,7,32,29,31,14,58,-21,-23,64,6,0,0};

    /**
    * image width in pixels
    */
    private static final int width = 20;

    /**
    * image height in pixels
    */
    private static final int height = 20;

    /**
    * parameterless constructor only
    */
    public Print()
    {
        super(image, width, height);
    }
} 