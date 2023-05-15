package com.ravnaandtines.crypt.cea;

/**
*  Interface CEA - standard stripped down encryption interface
*  <P>
*  Coded Mr. Tines &lt;tines@windsong.demon.co.uk&gt; 1998
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
*  <P>
* @author Mr. Tines
* @version 1.0 23-Dec-1998
 * @version 2.0 25-Dec-2007
 */
 /* This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details. */



public interface CEA
{
    /**
    * Initialise the object with one or three key blocks
    * @param key array of key bytes, 1 or 3 key block lengths
    * @param triple true if three keys for triple application
    */
    void init(byte[] key, int offset, boolean triple);
    
    /**
    * Transform one block in ecb mode
    * @param encrypt true if forwards transformation
    * @param input input block
    * @param offin offset into block of input data
    * @param out output block
    * @param offout offset into block of output data
    */
    void ecb(boolean encrypt, byte[] input, int offin,
        byte[] out, int offout);
    /**
    * Wipe key schedule information
    */
    void destroy();

    /**
    * Provide infomation of desired key size
    * @return byte length of key
    */
    int getKeysize();

    /**
    * Provide infomation of algorithm block size
    * @return byte length of block
    */
    int getBlocksize();
}

interface SpecialTriple {
}

