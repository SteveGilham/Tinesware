package com.ravnaandtines.util.image.icons;

/**
*  Class Lock - ImageProducer that produces a lock Icon
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

public class Lock extends com.ravnaandtines.util.image.IconProducer
{
    /**
    * Compressed encoded image
    */
    private static final byte[] image = {
    31,-117,8,0,0,0,0,0,0,0,-67,-109,-51,17,-124,
    32,20,-125,-87,-52,2,40,-64,2,44,-61,-45,-98,44,-61,
    2,44,-61,2,40,-64,2,104,-125,37,-49,-115,-61,34,-65,
    30,100,38,35,10,-7,8,-16,84,31,-91,-44,11,114,-37,
    -74,-71,101,89,68,-45,52,-119,-8,-18,-57,-70,88,100,-8,
    -41,-92,126,-20,102,86,-24,91,-41,85,-78,66,-24,-121,-21,
    84,-104,-30,-63,-68,97,24,-60,-117,102,-83,117,-58,-104,63,
    97,12,115,-44,-23,-55,-14,-72,54,-98,104,-12,-114,-29,40,
    66,-97,-52,96,110,-106,7,15,-42,69,78,-28,-102,-25,-7,
    -30,-45,-113,111,97,70,-17,-55,-14,-72,-41,-29,56,-60,67,
    22,-49,-113,76,-116,-17,-5,-50,61,23,-17,33,-63,-69,-99,
    7,88,1,47,-66,-105,106,125,-44,20,-43,-113,-45,90,-13,
    -82,-82,-70,40,-119,117,77,-87,-109,113,-29,-59,13,-5,-114,
    21,-41,14,-49,37,-59,107,-55,-10,52,95,107,-74,82,-66,
    -34,92,45,-7,82,-71,106,-22,-51,-105,-53,-11,86,62,-84,
    65,-26,19,-63,-101,-5,71,-98,40,96,125,1,-74,29,-27,
    -72,64,6,0,0};

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
    public Lock()
    {
        super(image, width, height);
    }
} 