package com.ravnaandtines.crypt.cea;

/**
 *  Class ThreeWay
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


public final class ThreeWay implements CEA {

    /**
     * Default constructor
     */
    public ThreeWay() {
        destroy();
    }
    /**
     * round constant for encrypt round 1
     */
    private static final int STRT_E = 0x0b0b;
    /**
     * ditto decrypt
     */
    private static final int STRT_D = 0xb1b1;
    /**
     * round count as recommended
     */
    private static final int ROUNDS = 11;

    /**
     * Expanded key-schedule
     */
    private static class KeySchedule { //NOPMD default constructor

        private int[] givenKey = new int[3];
        private int[] inverseKey = new int[3];
        private int[] encryptRoundConstants = new int[ROUNDS + 1];
        private int[] decryptRoundConstants = new int[ROUNDS + 1];

        public void wipe() {
            for (int i1 = 0; i1 < 3; ++i1) {
                givenKey[i1] = inverseKey[i1] = 0;
            }
            for (int i2 = 0; i2 < ROUNDS + 1; ++i2) {
                encryptRoundConstants[i2] = decryptRoundConstants[i2] = 0;
            }
        }
    }

    /**
     * primitive operations within the algorithm
     * bitwise reverse 96 bits
     * @param a array of 3 ints
     */
    private static void mu(final int[] a) { //NOPMD names
        final int[] b = new int[3];         //NOPMD names

        /* reverse within each int */
        for (int i = 0; i < 3; i++) {
            b[i] = 0;
            for (int j = 0; j < 32; j++) {
                b[i] <<= 1;
                if ((a[i] & 1) != 0) {
                    b[i] |= 1;
                }
                a[i] >>>= 1;
            }
        }

        /* reverse the ints */
        for (int i = 0; i < 3; i++) {
            a[i] = b[2 - i];
            b[2 - i] = 0;
        }
    }

    /**
     * cyclic index increment
     */
    private static int add1(final int index) {
        return ((index + 1) % 3);
    }

    private static int add2(final int index) {
        return ((index + 2) % 3);
    }

    /**
     * primitive operations within the algorithm
     * non-linear mixing of 96 bits
     * @param a array of 3 ints
     */
    private static void gamma(final int[] a) {  //NOPMD names
        final int[] b = new int[3];             //NOPMD names

        for (int i = 0; i < 3; i++) {
            b[i] = a[i] ^ (a[add1(i)] | (~a[add2(i)]));
        }
        for (int i = 0; i < 3; i++) {
            a[i] = b[i];
            b[i] = 0;
        }
    }

    /**
     * primitive operations within the algorithm
     * linear mixing of 96 bits
     * @param a array of 3 ints
     */
    private static void theta(final int[] a) {  //NOPMD names
        final int[] b = new int[3];             //NOPMD names

        for (int i = 0; i < 3; i++) {
            b[i] = a[i] ^
                    (a[i] >>> 16) ^ (a[add1(i)] << 16) ^
                    (a[add1(i)] >>> 16) ^ (a[add2(i)] << 16) ^
                    (a[add1(i)] >>> 24) ^ (a[add2(i)] << 8) ^
                    (a[add2(i)] >>> 8) ^ (a[i] << 24) ^
                    (a[add2(i)] >>> 16) ^ (a[i] << 16) ^
                    (a[add2(i)] >>> 24) ^ (a[i] << 8);
        }
        for (int i = 0; i < 3; i++) {
            a[i] = b[i];
            b[i] = 0;
        }
    }

    /**
     * primitive operations within the algorithm
     * first permutation of 96 bits
     * @param a array of 3 ints
     */
    private static void pi1(final int[] a) { //NOPMD names
        a[0] = (a[0] >>> 10) ^ (a[0] << 22);
        a[2] = (a[2] << 1) ^ (a[2] >>> 31);
    }

    /**
     * primitive operations within the algorithm
     * second permutation of 96 bits
     * @param a array of 3 ints
     */
    private static void pi2(final int[] a) { //NOPMD names
        a[2] = (a[2] >>> 10) ^ (a[2] << 22);
        a[0] = (a[0] << 1) ^ (a[0] >>> 31);
    }

    /**
     * the round function
     * @param a array of 3 ints
     */
    private static void round(final int[] a) { //NOPMD names
        theta(a);
        pi1(a);
        gamma(a);
        pi2(a);
    }

    /**
     * set up round constants
     * @param init direction specific constant
     * @param table key schecule
     */
    private static void generateConstants(int init, final int[] table) {
        for (int i = 0; i <= ROUNDS; i++) {
            table[i] = init;
            init <<= 1;
            if ((init & 0x10000) != 0) {
                init ^= 0x11011;
            }
        }
    }

    /**
     * one block forward pass
     * @param k the expanded key
     * @param a array of 3 ints
     */
    private static void encipher(final KeySchedule k, final int[] a) {//NOPMD names
        for (int i = 0; i <= ROUNDS; i++) {
            a[0] ^= k.givenKey[0] ^
                    (k.encryptRoundConstants[i] << 16);
            a[1] ^= k.givenKey[1];
            a[2] ^= k.givenKey[2] ^
                    k.encryptRoundConstants[i];
            if (i < ROUNDS) {
                round(a);
            } else {
                theta(a);
            }
        }
    }

    /**
     * one block inverted pass
     * @param k the expanded key
     * @param a array of 3 ints
     */
    private static void decipher(final KeySchedule k, final int[] a) {//NOPMD names
        mu(a);
        for (int i = 0; i <= ROUNDS; i++) {
            a[0] ^= k.inverseKey[0] ^
                    (k.decryptRoundConstants[i] << 16);
            a[1] ^= k.inverseKey[1];
            a[2] ^= k.inverseKey[2] ^
                    k.decryptRoundConstants[i];
            if (i < ROUNDS) {
                round(a);
            } else {
                theta(a);
            }
        }
        mu(a);
    }

    /**
     * Exoands user 96 bit key expansion into key schedule
     * @param k the expanded key
     * @param a array of 3 ints
     */
    private static void initialiseKey(final KeySchedule k, final int[] a) {//NOPMD names

        for (int i = 0; i < 3; i++) {
            k.inverseKey[i] =
                    k.givenKey[i] = a[i];
        }
        theta(k.inverseKey);
        mu(k.inverseKey);
        generateConstants(STRT_E, k.encryptRoundConstants);
        generateConstants(STRT_D, k.decryptRoundConstants);
    }
    /**
     * Is the jacket doing triple encryption?
     */
    private boolean triple;
    /**
     * The key schedule data
     */
    private KeySchedule[] keySchedule = null;

    /**
     * Initialise the object with one or three key blocks
     * @param key array of key bytes, 1 or 3 key block lengths
     * @param triple true if three keys for triple application
     */
    public void init(final byte[] key, int offset, final boolean triple) {
        this.triple = triple;
        final int[] a = new int[getKeysize() / 4]; //NOPMD name
        final int keys = triple ? 3 : 1;

        keySchedule = new KeySchedule[keys];

        for (int k = 0; k < keys; k++) {
            /* assume key data is MSB first */
            for (int i = 0; i < (a.length); i++) {
                a[i] = ((key[0 + offset] & 0xFF) << 24) |
                        ((key[1 + offset] & 0xFF) << 16) |
                        ((key[2 + offset] & 0xFF) << 8) |
                        (key[3 + offset] & 0xFF);
                offset += 4;
            }
            keySchedule[k] = new KeySchedule(); //NOPMD
            initialiseKey(keySchedule[k], a);
        }

        /* purge sensitive info */
        for (int i = 0; i < a.length; i++) {
            a[i] = 0;
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
            final byte[] input, int offin,
            final byte[] out, int offout) {
        final int keys = (triple) ? 3 : 1;
        final int[] a = new int[getKeysize() / 4]; //NOPMD names

        /* reduce data from MSB-first form */
        for (int i = 0; i < a.length; i++) {
            a[i] = ((input[0 + offin] & 0xFF) << 24) |
                    ((input[1 + offin] & 0xFF) << 16) |
                    ((input[2 + offin] & 0xFF) << 8) |
                    (input[3 + offin] & 0xFF);
            offin += 4;
        }

        for (int i = 0; i < keys; i++) {
            if (encrypt) {
                encipher(keySchedule[i], a);
            } else {
                decipher(keySchedule[keys - (i + 1)], a);
            }
        }

        /* restore endianness - If I've got the theory right! */
        for (int i = 0; i < a.length; i++) {
            out[offout] = (byte) ((a[i] >> 24) & 0xFF);
            out[1 + offout] = (byte) ((a[i] >> 16) & 0xFF);
            out[2 + offout] = (byte) ((a[i] >> 8) & 0xFF);
            out[3 + offout] = (byte) (a[i] & 0xFF);
            offout += 4;
            a[i] = 0;
        }
    }

    /**
     * Wipe key schedule information
     */
    public void destroy() {
        triple = false;
        if (keySchedule == null) {
            return;
        }
        for (int i = 0; i < keySchedule.length; ++i) {
            keySchedule[i].wipe();
        }
        keySchedule = null;
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

    /**
     * Provide infomation of desired key size
     */
    public final static int KEYSIZE = 12;


    /**
     * Provide infomation of algorithm block size
     */
    public final static int BLOCKSIZE = 12;

    /*******************************************************************/
    /*
    private static void Tway_ECB_encrypt(KeySchedule k, int[] data, int blocks)
    {
    int[] ptr = new int[3];
    int index=blocks;
    while(index != 0)
    {
    int j;
    for(j=0; j<3;++j) ptr[j] = data[j+(blocks-index)*3];
    encipher(k, ptr);
    for(j=0; j<3;++j) data[j+(blocks-index)*3] = ptr[j];
    index--;
    }
    }
    private static void Tway_ECB_decrypt(KeySchedule k, int[] data, int blocks)
    {
    int[] ptr = new int[3];
    int index=blocks;
    while(index != 0)
    {
    int j;
    for(j=0; j<3;++j) ptr[j] = data[j+(blocks-index)*3];
    decipher(k, ptr);
    for(j=0; j<3;++j) data[j+(blocks-index)*3] = ptr[j];
    index--;
    }
    }
     */
}
