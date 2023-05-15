package com.ravnaandtines.util.image.icons;

/**
*  Class Cut - ImageProducer that produces a Cut Icon
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

public class Cut extends com.ravnaandtines.util.image.IconProducer
{
    /**
    * Compressed encoded image
    */
    private static final byte[] image = {
    31,-117,8,0,0,0,0,0,0,0,-107,-108,-35,17,-124,
    32,12,-124,-87,-49,98,44,-64,-89,-85,-124,2,44,-128,103,
    43,-80,0,11,-80,0,26,-32,88,-122,112,-127,75,-8,97,
    38,-93,-122,-27,19,-52,70,-13,49,-58,-60,8,-57,113,80,
    -104,-100,27,5,95,19,-50,-13,12,-41,117,-107,-4,-74,109,
    33,-54,82,76,48,-45,26,-46,-25,53,96,86,115,-9,125,
    -49,48,43,22,-122,-58,115,-50,-107,121,-123,-7,-57,-94,61,
    72,60,107,109,120,-33,87,99,-118,44,-20,65,-29,33,-30,
    55,-107,-104,93,86,-61,-101,98,46,-80,-122,76,-54,47,-80,
    -70,76,-17,125,57,-13,2,75,101,114,95,44,-78,68,38,
    -68,-113,125,-110,55,-10,125,79,-71,28,83,-3,35,-43,21,
    60,-18,55,-42,107,-61,-2,105,-49,-90,-51,-115,-68,66,-25,
    -128,-73,-103,-74,-46,96,-114,116,13,-77,-54,-31,-2,121,-98,
    84,3,92,-51,-81,63,42,38,-41,-76,-21,-7,51,6,116,
    24,-16,8,-29,85,-25,-32,-17,-27,117,-105,-10,-57,89,57,
    87,120,-109,90,-11,123,75,117,-29,-52,-90,38,-35,-6,-10,
    124,-43,50,-107,127,-27,-116,71,71,-38,47,-48,-4,33,3,
    64,6,0,0};

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
    public Cut()
    {
        super(image, width, height);
    }
} 