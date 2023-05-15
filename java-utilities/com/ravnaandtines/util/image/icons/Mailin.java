package com.ravnaandtines.util.image.icons;

/**
*  Class Mailin - ImageProducer that produces an incoming mail Icon
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

public class Mailin extends com.ravnaandtines.util.image.IconProducer
{
    /**
    * Compressed encoded image
    */
    private static final byte[] image = {
    31,-117,8,0,0,0,0,0,0,0,-27,-44,49,14,-126,
    48,20,6,-32,119,25,47,-64,1,-104,9,-57,-24,106,-62,
    1,-100,60,1,3,-34,-94,-117,113,99,113,114,114,52,97,
    52,49,-15,4,-114,110,-43,31,120,-49,-10,65,-125,67,19,
    7,73,-2,-48,2,-3,104,75,41,109,-119,-24,-113,-30,-84,
    -75,-50,24,-109,36,108,-67,-23,36,97,23,-27,-32,120,116,
    -110,-25,-3,48,-101,91,87,-9,-71,-100,-41,-18,-44,22,19,
    47,-53,40,112,98,22,59,-66,-123,-74,-38,-37,109,6,19,
    -9,-115,-95,62,49,71,91,104,-85,-67,-93,93,-119,-55,-13,
    -127,-14,55,22,-38,-78,-105,-25,-7,88,-2,4,-11,-94,117,
    98,46,89,-2,-4,-59,-66,21,60,-33,-124,19,-77,-76,87,
    119,78,-38,-49,5,-49,-108,37,-119,-123,49,-64,98,31,-25,
    -96,127,-51,117,18,-79,80,31,61,-98,15,-12,-83,-86,42,
    -23,-13,-60,-117,-116,-105,45,-18,19,-100,125,51,68,-101,75,
    -33,-61,-73,48,54,127,-82,80,-42,-90,94,47,-72,-50,-17,
    -9,-41,11,27,58,-38,-44,94,-86,-1,55,-15,-2,-14,-21,
    -3,114,41,47,-34,-63,-63,34,64,6,0,0};

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
    public Mailin()
    {
        super(image, width, height);
    }
} 