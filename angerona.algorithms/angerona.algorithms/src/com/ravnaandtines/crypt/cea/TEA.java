package com.ravnaandtines.crypt.cea;

/**
 *  Class TEA - The Tiny Encryption Algorithm
 *  <P>
 *  Coded Mr. Tines &lt;tines@windsong.demon.co.uk&gt; 1998
 *  <p>
 *  The Tiny Encryption Algorithm of Wheeler and Needham
 *  based on the code on the Vader web site at Bradford UK
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
 * @version 1.1  8-Dec-2007
 * @version 2.0 25-Dec-2007
 */
 /* This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details. */

public final class TEA implements CEA {

    /**
     * key size input bytes
     */
    public static final int KEYSIZE = 16;
    /**
     * block size input bytes
     */
    public static final int BLOCKSIZE = 8;
    /**
     *  delta is 1/phi * 2^32 where
     *    phi is the Golden ratio, 1.618...
     */
    private static final int DELTA = 0x9E3779B9;
    /**
     * round count
     */
    private static final int NROUNDS = 32;

    /**
     * key schedule - really equals the key
     */
    private final static class Keyschedule {

        /**
         * key as big-endian integers 
         */
        public int[] key = new int[KEYSIZE / 4];

        /**
         * Encipher two int input place
         * @param left first half of block
         * @param right second half of block
         */
        public void encipher(final int[] left, final int[] right) {
            int y = left[0], z = right[0], sum = 0; //NOPMD names
            //final int delta = DELTA;
            int n = NROUNDS; //NOPMD names

            while (n-- > 0) {
                sum += DELTA;
                y   += ((z << 4) + key[0]) ^ (z + sum) ^ ((z >>> 5) + key[1]);
                z   += ((y << 4) + key[2]) ^ (y + sum) ^ ((y >>> 5) + key[3]);		/* end cycle */
            }
            left[0] = y;
            right[0] = z;
        }

        /**
         * Decipher two int input place
         * @param left first half of block
         * @param right second half of block
         */
        public void decipher(final int[] left, final int[] right) {
            int y = left[0], z = right[0], sum = DELTA * NROUNDS; //NOPMD names
            //final int delta = DELTA;
            int n = NROUNDS; //NOPMD names

            while (n-- > 0) {
                z -= ((y << 4) + key[2]) ^ (y + sum) ^ ((y >>> 5) + key[3]);
                y -= ((z << 4) + key[0]) ^ (z + sum) ^ ((z >>> 5) + key[1]);
                sum -= DELTA;
            }
            left[0] = y;
            right[0] = z;
        }

        /**
         * Pack key bytes into local ints
         * @param key byte array
         * @param offset start of key data
         */
        public Keyschedule(final byte[] key, final int offset) {
            /* Re-order the key bytes to local architecture */
            for (int i = 0, j = offset; i < KEYSIZE / 4; ++i, j += 4) {
                this.key[i] = ((key[j] & 0xFF) << 24) |
                        ((key[j + 1] & 0xFF) << 16) |
                        ((key[j + 2] & 0xFF) << 8) |
                        (key[j + 3] & 0xFF);
            }
        }

        /**
         * Wipe key schedule information
         */
        public void destroy() {
            if (key != null) {
                for (int i = 0; i < key.length; ++i) {
                    key[i] = 0;
                }
            }
            key = null;
        }
    }
    /**
     * Is the jacket doing triple encryption?
     */
    private boolean triple = false;
    /**
     * The key schedule data
     */
    private Keyschedule[] keySchedule = null;

    /**
     * Initialise the object with one or three key blocks
     * @param key array of key bytes, 1 or 3 key block lengths
     * @param triple true if three keys for triple application
     */
    public void init(final byte[] key, final int offset, final boolean triple) {
        this.triple = triple;
        final int keys = triple ? 3 : 1;

        keySchedule = new Keyschedule[keys];
        for (int i = 0; i < keys; i++) {
            keySchedule[i] = new Keyschedule(key, offset + i * KEYSIZE); //NOPMD
        }
    }

    /**
     * Transform one block input ecb mode
     * @param encrypt true if forwards transformation
     * @param input input block
     * @param offin offset into block of input data
     * @param out output block
     * @param offout offset into block of output data
     */
    public void ecb(final boolean encrypt, 
            final byte[] input, final int offin, 
            final byte[] out, final int offout) {
        final int keys = triple ? 3 : 1;
        int[] text0 = new int[1];
        int[] text1 = new int[1];
        int blocks = 0;
        if (triple & !encrypt) {
            blocks = 2;
        }
        final int delta = encrypt ? 1 : -1;

        /* pack byte streams input MSB-first form into the 32bit integers */
        /* so that the MSB becomes the high byte, regardless of architecture */
        text0[0] = ((input[0 + offin] & 0xFF) << 24) |
                ((input[1 + offin] & 0xFF) << 16) |
                ((input[2 + offin] & 0xFF) << 8) |
                (input[3 + offin] & 0xFF);
        text1[0] = ((input[4 + offin] & 0xFF) << 24) |
                ((input[5 + offin] & 0xFF) << 16) |
                ((input[6 + offin] & 0xFF) << 8) |
                (input[7 + offin] & 0xFF);

        for (int i = 0; i < keys; i++, blocks += delta) {
            if (encrypt) {
                keySchedule[blocks].encipher(text0, text1);
            } else {
                keySchedule[blocks].decipher(text0, text1);
            }
        }

        /* and unpack back into MSB-first format*/
        out[0 + offout] = (byte) ((text0[0] >> 24) & 0xFF);
        out[1 + offout] = (byte) ((text0[0] >> 16) & 0xFF);
        out[2 + offout] = (byte) ((text0[0] >> 8) & 0xFF);
        out[3 + offout] = (byte) (text0[0] & 0xFF);
        out[4 + offout] = (byte) ((text1[0] >> 24) & 0xFF);
        out[5 + offout] = (byte) ((text1[0] >> 16) & 0xFF);
        out[6 + offout] = (byte) ((text1[0] >> 8) & 0xFF);
        out[7 + offout] = (byte) (text1[0] & 0xFF);
    }

    /**
     * Wipe key schedule information
     */
    public void destroy() {
        if (keySchedule != null) {
            for (int i = 0; i < keySchedule.length; ++i) {
                keySchedule[i].destroy();
                keySchedule[i] = null;
            }
            keySchedule = null;
        }
        triple = false;
    }

    /**
     * default and only constructor
     */
    public TEA() { //NOPMD
        // empty
    }

    /**
     * Provide infomation of desired key size
     * @return byte length of key
     */
    public int getKeysize() {
        return KEYSIZE;
    }

    /**
     * Provide infomation of algorithm block size
     * @return byte length of block
     */
    public int getBlocksize() {
        return BLOCKSIZE;
    }
}
