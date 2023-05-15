package com.ravnaandtines.util.image.icons;

/**
*  Class User - ImageProducer that produces a User Icon
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

public class User extends com.ravnaandtines.util.image.IconProducer
{
    /**
    * Compressed encoded image
    */
    private static final byte[] image = {
    31,-117,8,0,0,0,0,0,0,0,-75,-108,-67,17,-62,
    48,12,-123,-75,12,11,48,64,106,38,-56,0,30,-125,-118,
    9,40,-96,-49,0,110,-24,114,52,84,-84,-112,42,13,43,
    112,89,64,-28,-39,-89,-96,-104,24,34,-33,-111,-69,119,78,
    100,-7,-53,-13,-113,76,7,34,-54,-120,-67,-9,-20,-100,-29,
    -86,-86,120,12,-123,22,-33,99,60,55,-26,43,75,56,-50,
    17,-97,-9,-79,21,-82,-127,-55,109,-37,6,31,24,123,57,
    17,-13,-77,-29,71,119,12,2,55,-2,-61,33,111,53,47,
    -50,45,-78,-16,-128,117,-65,-18,-8,-26,55,33,-114,126,11,
    15,30,-32,69,124,105,-98,120,44,-15,39,28,97,97,-2,
    70,127,-127,41,-21,7,47,-32,8,75,-83,-97,105,127,-101,
    -90,-103,-104,-23,-2,34,62,-10,-101,124,105,-23,-13,-105,-10,
    -3,-16,57,-15,-122,97,-8,80,-33,-9,51,89,120,-56,-57,
    -103,94,98,33,94,-62,-85,-21,122,-47,23,-30,37,-68,-19,
    -106,-78,42,-27,-55,25,-47,42,-31,-119,-124,-89,-39,-106,-3,
    -107,-69,64,-33,7,75,60,-55,89,91,115,41,47,125,-73,
    -42,-57,-65,120,-88,-79,119,-99,-51,106,-82,-120,-105,83,-122,
    -9,2,34,36,125,50,64,6,0,0};

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
    public User()
    {
        super(image, width, height);
    }
} 