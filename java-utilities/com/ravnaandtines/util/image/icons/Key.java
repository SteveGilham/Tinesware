package com.ravnaandtines.util.image.icons;

/**
*  Class Key - ImageProducer that produces a Key Icon
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

public class Key extends com.ravnaandtines.util.image.IconProducer
{
    /**
    * Compressed encoded image
    */
    private static final byte[] image = {
    31,-117,8,0,0,0,0,0,0,0,-99,-108,-63,21,-124,
    32,12,68,83,-103,5,120,-78,6,91,-80,-127,-83,-64,-125,
    7,-113,-10,-80,71,-5,-40,126,-78,14,56,-104,21,87,-120,
    -121,60,30,-127,-4,76,8,32,47,17,-39,77,-105,101,-47,
    -66,-17,51,-37,-4,98,-10,-43,88,-120,107,-102,70,-73,105,
    102,-16,111,-21,46,86,-37,-74,33,118,93,69,85,15,-61,
    -68,-21,34,-73,-126,25,106,-92,46,-78,-26,57,50,48,-110,
    91,-55,12,-38,44,-117,53,-46,127,48,-57,-64,68,-18,-101,
    -13,76,113,-44,37,81,115,-74,6,30,114,22,52,-2,-60,
    32,-65,-39,-101,-50,20,44,-43,-113,-101,71,125,121,127,-44,
    -51,-77,-67,-32,25,90,-106,-22,59,-11,-28,-122,-103,-6,-117,
    -67,-10,-98,-60,26,15,22,-17,13,-4,37,38,53,114,63,
    -49,-117,-84,-40,-29,-40,-113,97,-16,51,17,71,-29,-4,41,
    -13,-33,-101,123,-54,-68,-6,19,-104,3,-15,15,-104,55,119,
    106,44,49,-85,120,-45,52,37,-26,-71,118,-116,96,-18,-9,
    -84,-6,15,58,51,-83,78,-2,31,14,94,-79,118,71,-67,
    -103,78,91,-73,121,51,94,-42,37,-13,-60,-6,2,-53,49,
    -65,3,64,6,0,0};

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
    public Key()
    {
        super(image, width, height);
    }
} 