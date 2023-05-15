package com.ravnaandtines.crypt.cea;

/**
*  Class TripleDES
* <P>
*   The 3-Way cypher, inspired, inter alia, by
*	code from Schneier's <cite>Applied Cryptography</cite 2nd edition
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


public final class TripleDES extends TripleDESprototype   {
    public TripleDES() {  
        super();
        spBoxes = new DES_SPboxes();
    }  
}

class TripleDESprototype implements CEA, SpecialTriple {

    protected DES_SPboxes spBoxes;
    private int[] ekeysched;
    private int[] dkeysched;


    /**
    * Initialise the object with one or three key blocks
    * @param key array of key bytes, 1 or 3 key block lengths
    * @param triple true if three keys for triple application
    */
    public void init(final byte[] key, final int offset, final boolean triple)
    {
        if (triple) {
            throw new java.lang.IllegalArgumentException("Use Triple-DES directly");
        }
        ekeysched = new int[3*DESkeys.LONGS];
        dkeysched = new int[3*DESkeys.LONGS];

        DESkeys.triple(key, offset, DESkeys.EN0, ekeysched, 0);
        DESkeys.triple(key, offset, DESkeys.DE1, dkeysched, 0);
    }

    /**
    * Transform one block input ecb mode
    * @param encrypt true if forwards transformation
    * @param input input block
    * @param offin offset into block of input data
    * @param out output block
    * @param offout offset into block of output data
    */
    public void ecb(final boolean encrypt, final byte[] input, final int offin, 
            final byte[] out, final int offout)
    {
        if(encrypt)
        {
            DESengine.triple(input, offin, out, offout,
                ekeysched, spBoxes  );
        }
        else
        {
            DESengine.triple(input, offin, out, offout,
                dkeysched, spBoxes  );
        }
    }

    /**
    * Wipe key schedule information
    */
    public void destroy()
    {
        if (ekeysched != null) {
            for (int e = 0; e < ekeysched.length; ++e) {
                ekeysched[e] = 0;
            }
        }
        ekeysched = null;
        if (dkeysched != null) {
            for (int d = 0; d < dkeysched.length; ++d) {
                dkeysched[d] = 0;
            }
        }
        dkeysched = null;
    }

    /**
    * Provide infomation of desired key size
    * @return byte length of key
    */
    public int getKeysize()
    {
        return 3*DESkeys.KEYSIZE;
    }

    /**
    * Provide infomation of algorithm block size
    * @return byte length of block
    */
    public int getBlocksize()
    {
        return DESengine.BLOCKSIZE;
    }

}