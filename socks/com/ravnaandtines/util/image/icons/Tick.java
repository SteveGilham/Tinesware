package com.ravnaandtines.util.image.icons;

/**
*  Class Tick - ImageProducer that produces a Tick Icon
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

public class Tick extends com.ravnaandtines.util.image.IconProducer
{
    /**
    * Compressed encoded image
    */
    private static final byte[] image = {
    31,-117,8,0,0,0,0,0,0,0,-35,-44,-67,17,-128,
    32,12,6,-48,44,-29,0,-70,-123,84,118,-10,-84,-32,2,
    86,-116,-32,46,-18,-30,46,17,-30,15,114,65,16,-80,-47,
    34,-89,-25,125,-66,11,92,0,70,0,-8,105,-95,-108,18,
    -11,43,10,33,-118,45,109,-112,5,83,93,106,90,107,105,
    17,102,-3,28,-86,92,-109,91,-90,63,83,-101,73,25,-91,
    -44,-103,-45,123,-110,107,-47,126,94,-83,10,-5,-69,-66,-93,
    -42,-2,-113,-51,-51,123,-103,-68,107,62,-75,40,75,57,-13,
    -3,-56,-72,102,-118,-27,-52,-112,-49,108,58,-96,122,104,-15,
    -11,-8,-6,76,-77,-30,102,-70,21,54,-13,-84,-80,-103,63,
    -1,-36,44,-77,-72,-7,-50,-3,113,-50,82,-32,-4,125,-79,
    86,74,-58,101,-117,64,6,0,0};

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
    public Tick()
    {
        super(image, width, height);
    }
} 