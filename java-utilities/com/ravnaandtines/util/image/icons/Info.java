package com.ravnaandtines.util.image.icons;

/**
*  Class Info - ImageProducer that produces a Help Icon
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

public class Info extends com.ravnaandtines.util.image.IconProducer
{
    /**
    * Compressed encoded image
    */
    private static final byte[] image = {
    31,-117,8,0,0,0,0,0,0,0,-75,-108,61,17,-124,
    48,16,-123,-93,3,25,8,64,0,2,16,-128,0,4,80,
    93,-123,0,20,80,92,-123,0,4,32,0,25,8,-64,64,
    -18,-66,29,-106,9,-103,-124,80,64,-15,-122,76,-10,-19,99,
    -1,-78,-26,99,-116,-15,96,-89,105,18,-116,-29,104,-69,-82,
    -77,77,-45,8,56,115,-73,-37,125,-65,16,-124,-117,95,89,
    -106,54,-49,115,-5,-65,-106,-81,123,-58,6,39,-95,41,90,
    85,85,-119,95,93,-41,71,60,-13,60,11,52,94,108,112,
    -32,70,52,79,90,125,-33,-37,101,89,4,-37,-74,-99,-96,
    -9,112,34,-102,39,45,-2,-17,-22,-84,-21,122,-28,58,12,
    -33,-109,46,92,79,-13,-88,-105,27,-105,27,15,118,108,-22,
    -25,-57,-86,113,-18,-11,20,80,95,-72,-95,-4,-120,15,123,
    -106,101,-62,-11,-19,-44,-107,122,-62,-47,-103,32,31,-12,125,
    -18,29,104,-34,104,104,-65,-120,-105,115,-120,-113,-67,40,10,
    1,-71,-123,56,-8,106,-50,-52,41,-38,-60,29,-5,-65,-42,
    -81,109,-37,32,7,95,52,116,-18,-97,-44,75,-27,123,71,
    -49,-51,55,-43,-113,-108,-98,-33,15,-99,23,122,30,123,15,
    87,122,-2,-68,-92,-26,-7,74,47,48,-49,-105,-17,-115,123,
    52,-104,101,-64,-52,-24,76,71,-34,91,116,31,-60,-6,-83,
    57,94,-20,-125,55,-10,-43,27,-5,-12,-119,125,-1,3,117,
    -41,49,69,64,6,0,0};

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
    public Info()
    {
        super(image, width, height);
    }
} 