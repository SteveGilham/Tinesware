package com.ravnaandtines.util.image.icons;

/**
*  Class Cross - ImageProducer that produces a Cross Icon
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

public class Cross extends com.ravnaandtines.util.image.IconProducer
{
    /**
    * Compressed encoded image
    */
    private static final byte[] image = {
    31,-117,8,0,0,0,0,0,0,0,-75,-44,-71,13,-128,
    48,12,5,80,-81,-60,24,100,-116,-76,44,-63,6,116,76,
    -57,46,-127,32,57,-28,-16,-119,-96,112,1,124,61,41,-79,
    13,-84,0,-16,83,37,120,74,-51,-122,16,-92,108,-79,-46,
    -78,105,102,-79,-104,108,99,29,48,75,102,99,49,-39,-26,
    59,22,-107,-21,45,-84,29,-90,-63,-53,-107,-33,83,102,-116,
    -15,46,-63,98,-49,-52,-103,14,-53,100,58,45,-43,124,97,
    21,19,-17,-118,50,-47,-70,50,-26,-71,-26,-18,-66,-21,-111,
    107,71,40,75,-104,37,-105,-107,-49,40,-12,-35,109,73,125,
    39,76,-43,-86,-25,-39,96,-34,-49,-122,-103,40,-5,70,-11,
    -67,-9,-22,-100,48,95,-125,105,-39,55,-91,119,-115,105,-23,
    -119,50,7,-106,-1,-23,87,117,2,-123,-13,-106,20,64,6,
    0,0};

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
    public Cross()
    {
        super(image, width, height);
    }
} 