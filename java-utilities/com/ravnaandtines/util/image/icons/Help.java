package com.ravnaandtines.util.image.icons;

/**
*  Class Help - ImageProducer that produces a Help Icon
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

public class Help extends com.ravnaandtines.util.image.IconProducer
{
    /**
    * Compressed encoded image
    */
    private static final byte[] image = {
    31,-117,8,0,0,0,0,0,0,0,-83,-108,-63,13,-125,
    48,12,69,61,7,-9,50,0,3,48,3,51,112,69,98,
    6,-90,96,-120,-34,122,65,98,-127,78,-63,-87,-125,-32,-26,
    83,28,57,77,90,57,0,-46,-105,72,-14,-3,-30,4,108,
    26,-120,120,-102,38,110,-37,-10,-108,28,-125,52,-117,-36,-5,
    25,-19,76,-49,106,26,98,-66,-33,-104,-97,93,-98,92,12,
    98,-123,41,60,-49,122,61,-14,-27,98,35,-98,-80,-4,-45,
    -15,60,19,-113,-29,-57,87,-41,53,15,3,109,115,88,-13,
    66,-116,-117,-3,-49,-21,120,93,-55,51,48,22,95,81,20,
    -68,44,-108,-51,67,12,-26,-85,-86,10,-58,80,-112,-93,-111,
    7,-127,-123,-11,-85,120,90,56,55,124,-72,-125,35,-25,-3,
    86,89,-106,-101,55,96,-103,120,49,-85,-17,-5,-8,-69,30,
    -28,33,31,-100,17,-1,-51,21,60,93,-117,87,-16,-112,-105,
    124,-121,44,-98,-44,91,42,-26,23,103,103,-91,-22,-51,-9,
    3,-55,81,-5,-19,-3,96,-21,89,-63,29,-23,58,87,123,
    27,-5,21,120,1,83,-17,43,94,-36,-99,-79,-97,-58,-68,
    -104,-93,125,86,69,61,58,-79,-89,69,111,-94,-76,-59,21,
    64,6,0,0};

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
    public Help()
    {
        super(image, width, height);
    }
} 