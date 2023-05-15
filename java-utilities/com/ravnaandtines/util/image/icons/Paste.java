package com.ravnaandtines.util.image.icons;

/**
*  Class Paste - ImageProducer that produces a Paste Icon
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

public class Paste extends com.ravnaandtines.util.image.IconProducer
{
    /**
    * Compressed encoded image
    */
    private static final byte[] image = {
    31,-117,8,0,0,0,0,0,0,0,-19,-108,-63,17,-125,
    32,16,69,-87,-116,2,82,6,5,112,-55,-115,83,42,-56,
    49,125,112,97,38,77,80,-128,5,80,0,13,24,63,3,
    100,65,8,42,-41,48,-13,70,-59,-27,-7,5,-123,61,24,
    99,-111,-43,24,83,-80,117,55,-87,-22,24,113,20,46,33,
    68,6,-29,82,75,125,-53,-78,4,112,-113,-42,86,-50,-20,
    -94,25,-100,115,-69,-68,-67,-20,-107,-77,-16,-67,21,-53,-39,
    -48,-81,-108,106,66,107,71,-66,-105,104,-49,27,-27,126,-5,
    -42,29,-15,1,-116,-7,-59,21,-33,40,39,-112,82,-98,-14,
    61,49,-103,90,-81,26,-121,8,-25,60,-84,53,92,56,39,
    -50,-95,15,-9,-31,-77,-42,-122,117,-9,-34,7,7,90,114,
    -58,-9,-98,-54,-105,26,-7,38,47,-25,67,38,-54,108,-66,
    -102,-39,124,-76,-31,-6,-97,111,-17,75,-1,-45,76,-66,22,
    39,-13,53,-9,83,-70,-81,30,-123,-8,-102,-5,125,-17,25,
    61,-74,-6,15,99,97,-21,-125,64,6,0,0};

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
    public Paste()
    {
        super(image, width, height);
    }
} 