package com.ravnaandtines.util.image.icons;

/**
*  Class Copy - ImageProducer that produces a Copy Icon
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

public class Copy extends com.ravnaandtines.util.image.IconProducer
{
    /**
    * Compressed encoded image
    */
    private static final byte[] image = {
    31,-117,8,0,0,0,0,0,0,0,-67,-108,-47,13,-61,
    32,12,68,-103,-116,-111,-104,-95,19,116,23,126,24,-125,1,
    24,-128,1,88,-64,-27,-84,16,-95,20,48,53,82,45,-99,
    32,-63,-100,94,28,48,-123,16,-56,57,39,-86,-26,-103,-105,
    49,-110,56,-73,78,69,109,122,-34,126,111,-86,-31,61,121,
    12,-105,-84,-75,-108,82,-30,28,-52,55,60,111,63,124,55,
    -4,-80,-65,-108,2,119,-10,64,52,-49,13,-50,41,-97,-78,
    -82,83,62,101,93,-89,124,-54,-70,-118,124,-19,125,-116,-111,
    114,-50,-68,-74,-88,-85,-102,-81,5,60,71,126,-65,-16,61,
    -1,-55,41,-33,83,-89,124,125,-32,-7,31,124,-93,115,-91,
    -31,91,-35,3,5,31,-97,-63,-26,-37,75,-55,-89,-22,59,
    2,-33,81,95,-100,-36,-29,47,-65,-35,-2,-46,-11,25,-79,
    55,-114,-22,-70,16,-10,124,0,-43,-38,63,119,64,6,0,
    0};

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
    public Copy()
    {
        super(image, width, height);
    }
} 