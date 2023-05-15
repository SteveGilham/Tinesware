package com.ravnaandtines.util.image.coolcats;

/**
*  Class Copy - ImageProducer that produces a Copy Icon
*  <P>
*  Coded Mr. Tines &lt;tines@windsong.demon.co.uk&gt; 1998
*  <p>
*  This class is free software; the icons is derived
*  by taking the appropriate .gif images from work by MUSASHI(Jun Kitamikado)
*  <p>
*  "copyright=Copyright (c) 1998 Jun Kitamikado. No rights reserved.<br>
*  info=Come to see more! www.asahi-net.or.jp/~ld8j-ktmk"
*  <p>
*  and replacing the encoding by local GZIP'd bytestreams, and is thus
*  derivative of that source for copyright purposes.  There are no
*  more restrictions placed on this version of the image than are placed
*  on the original
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
* @version 1.0 17-Nov-1998
*/

public class Copy extends com.ravnaandtines.util.image.IconProducer
{
    /**
    * Compressed encoded image
    */
    private static final byte[] image = {
    31,-117,8,0,0,0,0,0,0,0,-59,-107,11,14,-60,
    32,8,68,-67,-1,-91,-35,79,98,-105,-46,1,6,-107,-107,
    -92,105,-75,-62,27,17,-75,-75,-42,-37,-31,103,124,-24,-74,
    -20,-85,102,-9,-9,-89,55,-80,-98,-1,105,0,19,-70,-104,
    24,83,124,-49,84,126,92,127,99,-116,-53,71,44,-39,6,
    121,112,3,37,53,-64,-7,-22,-73,-73,62,-113,60,-27,-42,
    33,-116,55,99,-85,-4,21,-10,-41,-65,-121,53,27,-26,127,
    -121,17,-75,112,49,-11,-70,-61,120,65,94,38,106,49,21,
    95,-114,99,-6,8,13,-26,126,-5,23,127,-8,-18,92,-9,
    7,-33,-42,112,-45,95,-91,35,-30,-77,117,-107,-47,6,96,
    -31,-4,-83,24,106,30,52,91,-5,1,13,38,31,-51,-73,
    25,-3,-108,95,-57,-9,-105,100,-53,119,80,-49,-12,3,124,
    -61,-3,-25,-83,-59,108,13,24,121,48,125,116,-33,-118,-35,
    114,70,-16,61,45,91,116,-4,52,-104,-4,-30,-77,-64,-27,
    87,-99,-123,64,-61,17,-66,-44,112,-126,125,-15,23,-39,-39,
    61,-119,-50,-59,93,-4,-56,-41,60,-109,54,-36,123,81,12,
    -26,-33,-86,-114,89,-2,-8,-49,-116,-85,-26,31,-80,23,27,
    -41,6,-81,0,16,0,0};

    /**
    * image width in pixels
    */
    private static final int width = 32;

    /**
    * image height in pixels
    */
    private static final int height = 32;

    /**
    * parameterless constructor only
    */
    public Copy()
    {
        super(image, width, height);
    }
} 