package com.ravnaandtines.util.image.coolcats;

/**
*  Class Cut - ImageProducer that produces a Cut Icon
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

public class Cut extends com.ravnaandtines.util.image.IconProducer
{
    /**
    * Compressed encoded image
    */
    private static final byte[] image = {
    31,-117,8,0,0,0,0,0,0,0,-59,-107,11,18,-125,
    48,8,68,115,116,111,78,39,85,91,64,96,-55,87,102,
    -76,-109,-110,-16,22,72,34,81,-87,86,-33,68,84,95,-17,
    -115,119,63,-67,-20,115,-103,-17,-117,-4,67,122,127,3,50,
    53,32,-1,72,13,-82,-55,-89,25,-15,-111,-33,99,103,-8,
    50,54,12,-102,-42,-48,-108,59,-113,127,-108,-61,-3,85,-117,
    -89,-11,28,113,29,29,35,-3,-17,-30,26,58,122,-6,-113,
    -5,27,-8,4,95,-18,3,94,-105,84,-18,-38,52,75,-5,
    -72,41,126,118,63,8,-122,-114,-119,124,-102,15,-6,96,-43,
    0,-26,-104,-51,95,-44,-31,121,46,-68,-2,-117,-4,-68,62,
    -93,-3,97,-16,51,-71,67,-66,-82,3,98,127,-25,82,-70,
    -9,-31,-34,-21,-75,34,-11,-90,-6,-65,-124,-1,-41,17,-11,
    127,-86,6,-63,-26,-49,-77,22,75,-7,-113,-1,8,127,-97,
    70,53,88,108,-83,65,-43,-65,-23,92,69,113,-61,-60,-40,
    92,-58,-121,119,14,60,99,108,-18,-67,-50,-117,103,-123,-74,
    -26,-21,26,-62,-61,107,115,-68,120,-51,-4,22,75,-59,51,
    -22,111,-99,-107,17,126,24,47,-79,-1,70,12,-60,91,126,
    -9,53,-54,-101,-110,115,-85,-126,87,53,44,-66,-1,49,-35,
    -65,123,55,104,40,-58,119,-16,30,-17,-32,127,0,5,101,
    63,97,0,16,0,0};

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
    public Cut()
    {
        super(image, width, height);
    }
} 