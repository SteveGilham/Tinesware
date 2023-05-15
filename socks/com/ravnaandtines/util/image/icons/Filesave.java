package com.ravnaandtines.util.image.icons;

/**
*  Class Filesave - ImageProducer that produces a Folder Save Icon
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

public class Filesave extends com.ravnaandtines.util.image.IconProducer
{
    /**
    * Compressed encoded image
    */
    private static final byte[] image = {
    31,-117,8,0,0,0,0,0,0,0,-51,-108,65,13,4,
    33,12,69,81,-122,0,4,32,0,1,8,-32,-76,26,16,
    -128,0,4,32,0,1,8,-64,12,51,-97,-92,27,-122,-52,
    -78,25,-104,3,36,47,-19,-95,125,105,19,-96,104,-83,-117,
    16,-94,-126,124,-122,-74,-97,114,-64,24,-101,-94,117,16,-58,
    -104,98,-83,125,60,-101,115,-82,-10,-10,115,-83,-6,-38,57,
    119,-14,-123,16,94,-11,-59,24,-73,-10,-115,-10,37,103,-113,
    -9,-66,-126,94,2,115,-111,107,-28,-101,-27,-105,79,74,57,
    68,41,117,97,-28,67,61,-99,-100,-13,45,-40,-109,115,-2,
    117,-113,-18,31,-7,80,-121,28,125,56,41,-91,-22,65,-124,
    -13,-119,15,-11,-67,-113,92,-120,111,-8,118,-104,15,-111,124,
    116,-33,0,28,45,-1,124,-24,89,-7,75,-69,127,-107,125,
    -50,72,-50,85,-50,-9,119,0,120,96,-107,-102,64,6,0,
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
    public Filesave()
    {
        super(image, width, height);
    }
} 