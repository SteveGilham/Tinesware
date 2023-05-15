package com.ravnaandtines.util.image.coolcats;

/**
*  Class Filenew - ImageProducer that produces a New Document Icon
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

public class Filenew extends com.ravnaandtines.util.image.IconProducer
{
    /**
    * Compressed encoded image
    */
    private static final byte[] image = {
    31,-117,8,0,0,0,0,0,0,0,-19,-108,-115,14,-62,
    48,8,-124,-5,-2,47,-115,102,58,101,-12,-128,107,45,-115,
    38,-110,24,101,101,124,87,126,108,-83,73,-5,-78,-49,-35,
    26,124,-82,-100,52,-57,64,44,-61,127,59,34,89,94,-99,
    40,-115,69,44,-13,-20,-107,-21,-76,-89,6,-105,13,98,71,
    52,104,-65,-49,-57,23,-111,-47,-101,-42,-36,114,89,-77,-15,
    19,26,-36,-5,-48,26,-92,-101,27,-74,124,-80,-10,-77,70,
    -52,64,25,-37,104,64,53,87,-89,53,108,71,3,-44,83,
    -59,54,26,-88,-3,-115,102,111,102,46,21,-120,-18,57,-36,
    -25,73,-10,-15,45,-2,44,68,119,-115,-4,33,29,63,-56,
    95,101,-96,-23,91,-7,-121,6,-119,-17,-65,101,-1,-60,-35,
    -63,125,-5,-1,-8,-67,-107,127,-71,59,-26,-105,105,-48,108,
    -27,35,-2,114,13,-106,77,-16,-105,105,64,108,117,-26,-18,
    64,119,-16,1,55,120,63,-68,-17,-91,95,-125,58,-56,-8,
    -4,-65,87,76,-33,-126,-122,77,-44,-117,-22,-77,-41,-65,5,
    -10,-25,15,-10,-65,-120,-97,-50,-1,6,62,-36,-1,98,-69,
    1,112,-108,-80,73,0,16,0,0};

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
    public Filenew()
    {
        super(image, width, height);
    }
} 