package com.ravnaandtines.crypt.cea;

/**
 *  Class Safer - The SAFER cypher input all its variants.
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

public final class Safer implements CEA {

    /**
     * maximum number of rounds (this value by default)
     */
    private static final int MAXROUNDS = 13;
    /**
     * block size input bytes
     */
    public static final int BLOCKSIZE = 8;
    /**
     * log table look-up array size
     */
    private static final int LOGTABSIZE = 256;

    /**
     * Slices the low byte value out of an int
     * @param value value to slice
     * @return the low byte
     */
    private static int toByte(final int value) {
        return value & 0xFF;
    }

    /**
     * Rotates the low byte
     * @param value value to rotate
     * @param shift number of bits of rotation
     * @return the rotated byte
     */
    private static byte rotl(final int value, final int shift) {
        return (byte) toByte(((value) << (shift)) | (((int) toByte(value)) >>> (8 - (shift))));
    }
    /**
     * anti-log table
     */
    private byte[] exp = new byte[LOGTABSIZE];
    /**
     * log table
     */
    private byte[] log = new byte[LOGTABSIZE];
    /**
     * initialisation tracking
     */
    private boolean logTabSet = false;

    /**
     * set up log and anti-log tables
     */
    private void setLogTable() {
        for (int l = 0, e = 1; !logTabSet && l < LOGTABSIZE; ++l) {
            exp[l] = (byte) toByte(e);
            log[exp[l] & 0xFF] = (byte) toByte(l);
            e = (e * 45) % 257;
        }
        logTabSet = true;
    }

    /**
     * round functions
     */
    private static void add(final byte[] w, final int a, final int b) { //NOPMD names
        w[b] += w[a];
        w[a] += w[b];
    }

    private static void sub(final byte[] w, final int a, final int b) { //NOPMD names
        w[a] -= w[b];
        w[b] -= w[a];
    }
    /**
     * size of full key schedule
     */
    private static final int EXPANDEDKEY =
            (1 + BLOCKSIZE * (2 * MAXROUNDS + 1));

    /**
     * the key schedule
     */
    private class Keyschedule { //NOPMD complex

        /**
         * the full key schedule bytes
         */
        private byte[] keyMaterial = new byte[EXPANDEDKEY];

        /**
         * wipes the whole keyschedule
         */
        public void destroy() {
            if (keyMaterial != null) {
                for (int i = 0; i < keyMaterial.length; ++i) {
                    keyMaterial[i] = 0;
                }
            }
            keyMaterial = null;
        }

        /**
         * performs forward operation
         * @param input the data to encrypt
         * @param offin the start of the input
         * @param out the result
         * @param offout the start of the output
         */
        public void encrypt(final byte[] input, final int offin, 
                final byte[] out, final int offout) {
            byte tmp;
            byte[] work = new byte[BLOCKSIZE];
            int round = Math.min(MAXROUNDS, keyMaterial[0]);
            int keyOffset = 0;
            System.arraycopy(input, offin, work, 0, work.length);

            while (round-- != 0) {
                /* part 1, simple mixing with key */
                work[0] ^= keyMaterial[++keyOffset];
                work[1] += keyMaterial[++keyOffset];
                work[2] += keyMaterial[++keyOffset];
                work[3] ^= keyMaterial[++keyOffset];
                work[4] ^= keyMaterial[++keyOffset];
                work[5] += keyMaterial[++keyOffset];
                work[6] += keyMaterial[++keyOffset];
                work[7] ^= keyMaterial[++keyOffset];

                /* part 2, logarithms input the group */
                work[0] = (byte) (exp[work[0] & 0xFF] + keyMaterial[++keyOffset]);
                work[1] = (byte) (log[work[1] & 0xFF] ^ keyMaterial[++keyOffset]);
                work[2] = (byte) (log[work[2] & 0xFF] ^ keyMaterial[++keyOffset]);
                work[3] = (byte) (exp[work[3] & 0xFF] + keyMaterial[++keyOffset]);
                work[4] = (byte) (exp[work[4] & 0xFF] + keyMaterial[++keyOffset]);
                work[5] = (byte) (log[work[5] & 0xFF] ^ keyMaterial[++keyOffset]);
                work[6] = (byte) (log[work[6] & 0xFF] ^ keyMaterial[++keyOffset]);
                work[7] = (byte) (exp[work[7] & 0xFF] + keyMaterial[++keyOffset]);

                /* part 3, mutual increment */
                add(work, 0, 1);
                add(work, 2, 3);
                add(work, 4, 5);
                add(work, 6, 7);

                add(work, 0, 2);
                add(work, 1, 3);
                add(work, 4, 6);
                add(work, 5, 7);

                add(work, 0, 4);
                add(work, 1, 5);
                add(work, 2, 6);
                add(work, 3, 7);

                /* part 4, permute */
                tmp = work[1];
                work[1] = work[4];
                work[4] = work[2];
                work[2] = tmp;
                tmp = work[3];
                work[3] = work[5];
                work[5] = work[6];
                work[6] = tmp;
            } /* end of round */

            /* mix to output */
            out[0 + offout] = (byte) (work[0] ^ keyMaterial[++keyOffset]);
            out[1 + offout] = (byte) (work[1] + keyMaterial[++keyOffset]);
            out[2 + offout] = (byte) (work[2] + keyMaterial[++keyOffset]);
            out[3 + offout] = (byte) (work[3] ^ keyMaterial[++keyOffset]);
            out[4 + offout] = (byte) (work[4] ^ keyMaterial[++keyOffset]);
            out[5 + offout] = (byte) (work[5] + keyMaterial[++keyOffset]);
            out[6 + offout] = (byte) (work[6] + keyMaterial[++keyOffset]);
            out[7 + offout] = (byte) (work[7] ^ keyMaterial[++keyOffset]);
            tmp = 0;
            for (int w = 0; w < BLOCKSIZE; ++w) {
                work[w] = 0;
            }
        }

        /**
         * performs return operation
         * @param input the data to decrypt
         * @param offin the start of the input
         * @param out the result
         * @param offout the start of the output
         */
        public void decrypt(final byte[] input, final int offin, 
                final byte[] out, final int offout) {
            byte[] work = new byte[BLOCKSIZE];
            byte tmp;
            int round = Math.min(MAXROUNDS, keyMaterial[0]);
            int keyOffset = (2 * round + 1) * BLOCKSIZE;

            /* undo mix to output */
            work[7] = (byte) (input[7 + offin] ^ keyMaterial[keyOffset--]);
            work[6] = (byte) (input[6 + offin] - keyMaterial[keyOffset--]);
            work[5] = (byte) (input[5 + offin] - keyMaterial[keyOffset--]);
            work[4] = (byte) (input[4 + offin] ^ keyMaterial[keyOffset--]);
            work[3] = (byte) (input[3 + offin] ^ keyMaterial[keyOffset--]);
            work[2] = (byte) (input[2 + offin] - keyMaterial[keyOffset--]);
            work[1] = (byte) (input[1 + offin] - keyMaterial[keyOffset--]);
            work[0] = (byte) (input[0 + offin] ^ keyMaterial[keyOffset--]);

            while (round-- != 0) {
                /* undo part 4, permute */
                tmp = work[2];
                work[2] = work[4];
                work[4] = work[1];
                work[1] = tmp;
                tmp = work[6];
                work[6] = work[5];
                work[5] = work[3];
                work[3] = tmp;

                /* undo part 3, mutual increment */
                sub(work, 0, 4);
                sub(work, 1, 5);
                sub(work, 2, 6);
                sub(work, 3, 7);

                sub(work, 0, 2);
                sub(work, 1, 3);
                sub(work, 4, 6);
                sub(work, 5, 7);

                sub(work, 0, 1);
                sub(work, 2, 3);
                sub(work, 4, 5);
                sub(work, 6, 7);

                /* undo parts 1&2, mixing and logarithms input the group */
                work[7] -= keyMaterial[keyOffset--];
                work[6] ^= keyMaterial[keyOffset--];
                work[5] ^= keyMaterial[keyOffset--];
                work[4] -= keyMaterial[keyOffset--];
                work[3] -= keyMaterial[keyOffset--];
                work[2] ^= keyMaterial[keyOffset--];
                work[1] ^= keyMaterial[keyOffset--];
                work[0] -= keyMaterial[keyOffset--];

                work[7] = (byte) (log[work[7] & 0xFF] ^ keyMaterial[keyOffset--]);
                work[6] = (byte) (exp[work[6] & 0xFF] - keyMaterial[keyOffset--]);
                work[5] = (byte) (exp[work[5] & 0xFF] - keyMaterial[keyOffset--]);
                work[4] = (byte) (log[work[4] & 0xFF] ^ keyMaterial[keyOffset--]);
                work[3] = (byte) (log[work[3] & 0xFF] ^ keyMaterial[keyOffset--]);
                work[2] = (byte) (exp[work[2] & 0xFF] - keyMaterial[keyOffset--]);
                work[1] = (byte) (exp[work[1] & 0xFF] - keyMaterial[keyOffset--]);
                work[0] = (byte) (log[work[0] & 0xFF] ^ keyMaterial[keyOffset--]);

            } /* end of round */

            /* copy to output */
            System.arraycopy(work, 0, out, offout, work.length);
            for (int w = 0; w < BLOCKSIZE; ++w) {
                work[w] = 0;
            }
        }

        /**
         * Initialises the key schedult
         * @param key1 the first 64 bits of key
         * @param key1off the start of the key
         * @param key2 the second 64 bits of key
         * @param key2off the start of the key
         * @param rounds the number of rounds to use
         * @param skVariant signals to use the strenthened version
         */
        public Keyschedule(final byte[] key1, final int key1off, //NOPMD complex
                final byte[] key2, final int key2off,
                final int rounds, final boolean skVariant) {

            byte[] worka = new byte[1 + BLOCKSIZE];
            byte[] workb = new byte[1 + BLOCKSIZE];
            int keyOffset = 0;

            keyMaterial[keyOffset++] = (byte) Math.min(MAXROUNDS, rounds);
            worka[BLOCKSIZE] = workb[BLOCKSIZE] = 0;
            for (int i = 0; i < BLOCKSIZE; ++i) {
                worka[BLOCKSIZE] ^= worka[i] = rotl(key1[i + key1off], 5);
                workb[BLOCKSIZE] ^= workb[i] = keyMaterial[keyOffset++] = key2[i + key2off];
            }

            if (!logTabSet) {
                setLogTable();
            }

            for (int i = 1; i <= keyMaterial[0]; ++i) {

                for (int j = 0; j < BLOCKSIZE + 1; ++j) {
                    worka[j] = rotl(worka[j], 6);
                    workb[j] = rotl(workb[j], 6);
                }
                for (int j = 0; j < BLOCKSIZE; ++j) {
                    if (skVariant) {
                        keyMaterial[keyOffset++] = (byte) toByte(
                                worka[(0xFF & (j + 2 * i - 1)) % (1 + BLOCKSIZE)] + exp[exp[0xFF & toByte(18 * i + j + 1)] & 0xFF]);
                    } else {
                        keyMaterial[keyOffset++] = (byte) toByte(worka[j] + exp[exp[0xFF & toByte(18 * i + j + 1)] & 0xFF]);
                    }
                }
                for (int j = 0; j < BLOCKSIZE; ++j) {
                    if (skVariant) {
                        keyMaterial[keyOffset++] = (byte) toByte(
                                workb[(0xFF & (j + 2 * i)) % (1 + BLOCKSIZE)] + exp[exp[0xFF & toByte(18 * i + j + 10)] & 0xFF]);
                    } else {
                        keyMaterial[keyOffset++] = (byte) toByte(workb[j] + exp[exp[0xFF & toByte(18 * i + j + 10)] & 0xFF]);
                    }
                }
            } /* next i */
            for (int i = 0; i <= BLOCKSIZE; ++i) {
                worka[i] = 0;
                workb[i] = 0;
            }
        }
    }// end inner class
    /**
     * defined default round counts
     */
    //private static final int[] defaultRounds = {6, 10, 8, 10};
    /**
     * the key size input bytes
     */
    private final int keysize;
    /**
     * the variant to use
     */
    private final boolean skVariant;
    /**
     * number of rounds chosen
     */
    private final int rounds;

    /**
     * default constructor SAFER-SK128 w/13 rounds
     */
    public Safer() {
        this(true, true, MAXROUNDS);
    }

    /**
     * Complex constructor
     * @param fullkeylen true for 128 bit key, false for 64 bit
     * @param strong true for SK variant
     * @param roundCount number of rounds to use
     */
    public Safer(final boolean fullkeylen, final boolean strong, int roundCount) {
        setLogTable();
        keysize = fullkeylen ? 16 : 8;
        skVariant = strong;

        int minr = 6;
        //if(strong) minr = 8;
        if (fullkeylen) {
            minr = 10;
        }

        if (roundCount < minr) {
            roundCount = minr;
        }
        if (roundCount > MAXROUNDS) {
            roundCount = MAXROUNDS;
        }
        rounds = roundCount;
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
        final int delta = (getKeysize() > 8) ? 8 : 0;

        keySchedule = new Keyschedule[keys];
        for (int i = 0; i < keys; i++) {
            keySchedule[i] = new Keyschedule(key, offset + i * getKeysize(), //NOPMD
                    key, offset + delta + i * getKeysize(), rounds, skVariant);
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
        boolean single = !triple; //NOPMD
        if (single) {
            if (encrypt) {
                keySchedule[0].encrypt(input, offin, out, offout);
            } else {
                keySchedule[0].decrypt(input, offin, out, offout);
            }
        } else {
            byte[] tmp = new byte[BLOCKSIZE];
            byte[] tmp2 = new byte[BLOCKSIZE];
            if (encrypt) {
                keySchedule[0].encrypt(input, offin, tmp, 0);
                keySchedule[1].encrypt(tmp, 0, tmp2, 0);
                keySchedule[2].encrypt(tmp2, 0, out, offout);
            } else {
                keySchedule[2].decrypt(input, offin, tmp, 0);
                keySchedule[1].decrypt(tmp, 0, tmp2, 0);
                keySchedule[0].decrypt(tmp2, 0, out, offout);
            }
            for (int i = 0; i < BLOCKSIZE; ++i) {
                tmp[i] = 0;
                tmp2[i] = 0;
            }
        }
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
     * Provide infomation of desired key size
     * @return byte length of key
     */
    public int getKeysize() {
        return keysize;
    }

    /**
     * Provide infomation of algorithm block size
     * @return byte length of block
     */
    public int getBlocksize() {
        return BLOCKSIZE;
    }

}



 