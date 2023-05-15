package com.ravnaandtines.util.image.coolcats;

/**
*  Class Fileopen - ImageProducer that produces a Folder Open Icon
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

public class Fileopen extends com.ravnaandtines.util.image.IconProducer
{
    /**
    * Compressed encoded image
    */
    private static final byte[] image = {
    31,-117,8,0,0,0,0,0,0,0,-19,-107,81,14,-60,
    32,8,68,-71,-1,-91,-23,-113,73,81,7,28,41,-18,102,
    -77,37,49,-87,41,-14,-58,50,90,17,81,-7,-111,97,39,
    -107,-71,123,108,-43,85,-35,-5,97,-99,75,-77,109,52,13,
    59,-71,89,13,115,61,118,33,-89,-105,102,-93,-70,81,-116,
    -7,9,13,-18,126,104,13,58,-7,-26,-47,-73,-49,70,-62,
    3,101,-20,65,67,-38,115,-47,60,-87,1,-23,-127,-75,-85,
    -8,70,3,127,126,43,-39,55,-120,-18,121,-39,-73,111,-21,
    68,93,47,-108,123,14,-22,-8,99,62,104,122,25,31,26,
    10,-27,105,-68,-1,104,109,-60,106,-11,12,0,-1,51,12,
    63,-70,-113,86,-105,-44,-30,117,-81,97,-30,-117,-116,18,-85,
    7,-28,-53,23,-7,-106,-83,58,-75,-24,40,127,100,31,-26,
    -9,-123,1,-37,104,96,125,-73,-49,-18,125,-50,28,37,-41,
    -85,-63,-103,35,4,-123,-31,-78,-111,-122,29,29,100,-124,108,
    79,67,97,-68,-4,-51,-2,31,-30,47,-3,-1,1,62,60,
    -1,-121,-29,2,-90,-30,-73,-78,0,16,0,0};

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
    public Fileopen()
    {
        super(image, width, height);
    }
} 