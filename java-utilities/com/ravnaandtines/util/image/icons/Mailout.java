package com.ravnaandtines.util.image.icons;

/**
*  Class Mailout - ImageProducer that produces an outgoing mail Icon
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

public class Mailout extends com.ravnaandtines.util.image.IconProducer
{
    /**
    * Compressed encoded image
    */
    private static final byte[] image = {
    31,-117,8,0,0,0,0,0,0,0,-27,-108,61,10,-62,
    48,20,-128,-33,101,-68,64,15,-112,-71,-25,-56,42,-28,0,
    78,-98,-96,67,-67,69,22,-23,-42,-59,-55,-55,81,-24,40,
    8,-98,-64,-47,45,-26,-91,-68,52,125,125,-87,8,1,7,
    11,31,73,11,-17,-21,-5,105,3,123,0,-8,35,-100,-75,
    -42,105,-83,-117,64,46,-81,46,2,121,113,63,-69,-98,67,
    -28,-11,-24,68,-18,67,19,-72,94,-74,-18,-36,-41,11,95,
    85,-63,-52,-109,115,-111,39,117,97,44,-9,29,118,-93,83,
    -21,-111,53,15,119,97,44,-9,-99,-20,38,-66,7,-7,-58,
    -123,-79,82,-67,-72,-42,-67,-117,-7,114,114,46,-34,63,-91,
    84,-72,111,124,-37,-56,39,-49,15,68,87,-22,-29,-82,28,
    -28,-93,30,-93,-117,114,-59,-107,-5,-96,-67,77,-79,126,-65,
    0,-90,89,97,110,-58,-104,-32,33,-92,122,-55,-71,86,-17,
    -79,29,-31,-50,-36,60,40,23,105,30,-44,43,-84,-107,59,
    -71,15,-97,-89,-33,11,-59,-26,-32,78,-18,43,-11,-1,22,
    62,95,126,125,94,126,-30,13,77,107,16,12,64,6,0,
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
    public Mailout()
    {
        super(image, width, height);
    }
} 