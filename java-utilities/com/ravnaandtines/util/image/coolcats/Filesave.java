package com.ravnaandtines.util.image.coolcats;

/**
*  Class Filesave - ImageProducer that produces a Folder Saven Icon
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

public class Filesave extends com.ravnaandtines.util.image.IconProducer
{
    /**
    * Compressed encoded image
    */
    private static final byte[] image = {
    31,-117,8,0,0,0,0,0,0,0,-35,-106,-115,14,-61,
    32,8,-124,121,-1,-105,102,91,-78,-76,-88,-4,-12,20,88,
    50,18,-101,-90,90,-66,-13,-92,86,34,98,50,-38,-4,96,
    119,-52,78,-69,111,-34,-15,-71,42,-7,-81,126,103,-52,17,
    -1,-54,-115,-67,-100,58,127,-87,-61,11,-54,-13,0,-30,38,
    104,112,-41,117,-56,-65,55,33,-40,-17,-103,-113,-82,-59,-32,
    7,-18,-59,-62,61,-115,-81,14,-88,-34,-78,-40,-126,111,105,
    -112,125,3,63,59,118,124,104,-26,-69,-11,-33,-96,-95,-108,
    29,-16,-53,-39,-126,-81,105,-24,-29,-77,-22,-63,47,-7,101,
    53,111,106,80,-8,93,-95,120,-16,55,-4,-91,-88,26,-7,
    50,7,-79,-99,-77,-110,15,105,40,-14,63,-30,87,-81,-1,
    35,-2,-72,7,-90,-13,5,-61,-30,-89,127,-1,-47,-36,-119,
    85,-17,-37,-10,-65,-32,124,92,-1,-33,103,117,-18,-27,30,
    76,-20,-16,-4,-111,86,-117,24,123,-47,-32,-42,15,-38,-98,
    -79,93,45,7,-51,-22,122,1,-40,0,92,-5,0,16,0,
    0};

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
    public Filesave()
    {
        super(image, width, height);
    }
} 