package com.ravnaandtines.util.image.coolcats;

/**
*  Class Paste - ImageProducer that produces a Paste Icon
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

public class Paste extends com.ravnaandtines.util.image.IconProducer
{
    /**
    * Compressed encoded image
    */
    private static final byte[] image = {
    31,-117,8,0,0,0,0,0,0,0,-27,-105,1,15,-124,
    32,8,-123,-3,-1,127,-102,91,-45,26,33,60,65,-79,-74,
    -114,-115,-101,103,-7,-66,103,-119,85,41,-123,-118,-110,-4,-113,
    -43,47,-113,101,101,-3,105,113,-76,26,-25,-42,47,-114,-19,
    -29,-113,7,64,-83,106,114,-40,7,-39,86,92,82,-70,15,
    118,-62,-59,-85,109,34,-53,-125,16,-16,-123,-31,67,-14,-17,
    73,54,127,54,-124,-121,40,127,-119,45,60,-116,-7,45,-7,
    -11,-54,-32,19,-111,-9,30,116,-9,60,43,68,93,34,118,
    -6,-36,21,62,-9,-111,-78,-34,39,-7,-18,61,39,-60,-102,
    -37,-105,82,-8,65,6,100,47,-52,127,-88,11,-82,73,-73,
    -33,38,121,80,-75,-108,117,-111,-62,63,-57,54,109,-88,-45,
    -43,-90,-94,-77,-24,97,-119,-49,117,102,-40,103,123,-123,31,
    -15,-95,-99,99,-115,97,-49,-55,-48,20,97,-15,-128,49,29,
    -105,-6,-11,-73,51,60,-11,-9,68,-128,-3,-8,-13,124,-16,
    126,-8,36,63,-70,124,51,-39,111,-13,103,-54,55,-121,-3,
    -17,124,-52,-34,-58,119,-50,125,11,-33,-8,22,-77,-14,7,
    -61,8,34,-78,0,16,0,0};

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
    public Paste()
    {
        super(image, width, height);
    }
} 