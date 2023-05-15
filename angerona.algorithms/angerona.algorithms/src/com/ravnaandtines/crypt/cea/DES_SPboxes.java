package com.ravnaandtines.crypt.cea;

/**
 *  Class DES_SPBoxes - DES box generation
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


final class DES_SPboxes { //NOPMD complex

    private static final int BOXLEN = 64;
    public static final int KDDESEXTRA = 6;
    
    // These are exposed to DESengine
    public final int[] SP1 = new int[BOXLEN];
    public final int[] SP2 = new int[BOXLEN];
    public final int[] SP3 = new int[BOXLEN];
    public final int[] SP4 = new int[BOXLEN];
    public final int[] SP5 = new int[BOXLEN];
    public final int[] SP6 = new int[BOXLEN];
    public final int[] SP7 = new int[BOXLEN];
    public final int[] SP8 = new int[BOXLEN];

    /* P-box definition */
    private static final int[] BITS1 =
            {0x00000004, 0x00000400, 0x00010000, 0x01000000};
    private static final int[] BITS2 =
            {0x00008000, 0x80000000, 0x00000020, 0x00100000};
    private static final int[] BITS3 =
            {0x08000000, 0x00000008, 0x00020000, 0x00000200};
    private static final int[] BITS4 =
            {0x00000001, 0x00800000, 0x00002000, 0x00000080};
    private static final int[] BITS5 =
            {0x40000000, 0x00000100, 0x00080000, 0x02000000};
    private static final int[] BITS6 =
            {0x00004000, 0x00400000, 0x00000010, 0x20000000};
    private static final int[] BITS7 =
            {0x04000000, 0x00000800, 0x00200000, 0x00000002};
    private static final int[] BITS8 =
            {0x00001000, 0x00040000, 0x00000040, 0x10000000};

    /* S-box definitions : classic DES*/
    private static final byte[] DS1 = {
        14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7,
        0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8,
        4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0,
        15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13
    };
    private static final byte[] DS2 = {
        15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10,
        3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5,
        0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15,
        13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9
    };
    private static final byte[] DS3 = {
        10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8,
        13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1,
        13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7,
        1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12
    };
    private static final byte[] DS4 = {
        7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15,
        13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9,
        10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4,
        3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14
    };
    private static final byte[] DS5 = {
        2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9,
        14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6,
        4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14,
        11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3
    };
    private static final byte[] DS6 = {
        12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11,
        10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8,
        9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6,
        4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13
    };
    private static final byte[] DS7 = {
        4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1,
        13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6,
        1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2,
        6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12
    };
    private static final byte[] DS8 = {
        13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7,
        1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2,
        7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8,
        2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11
    };
    /* S-box definitions, s3' des */
    private static final byte[] S31 = {
        13, 14, 0, 3, 10, 4, 7, 9, 11, 8, 12, 6, 1, 15, 2, 5,
        8, 2, 11, 13, 4, 1, 14, 7, 5, 15, 0, 3, 10, 6, 9, 12,
        14, 9, 3, 10, 0, 7, 13, 4, 8, 5, 6, 15, 11, 12, 1, 2,
        1, 4, 14, 7, 11, 13, 8, 2, 6, 3, 5, 10, 12, 0, 15, 9
    };
    private static final byte[] S32 = {
        15, 8, 3, 14, 4, 2, 9, 5, 0, 11, 10, 1, 13, 7, 6, 12,
        6, 15, 9, 5, 3, 12, 10, 0, 13, 8, 4, 11, 14, 2, 1, 7,
        9, 14, 5, 8, 2, 4, 15, 3, 10, 7, 6, 13, 1, 11, 12, 0,
        10, 5, 3, 15, 12, 9, 0, 6, 1, 2, 8, 4, 11, 14, 7, 13
    };
    private static final byte[] S33 = {
        13, 3, 11, 5, 14, 8, 0, 6, 4, 15, 1, 12, 7, 2, 10, 9,
        4, 13, 1, 8, 7, 2, 14, 11, 15, 10, 12, 3, 9, 5, 0, 6,
        6, 5, 8, 11, 13, 14, 3, 0, 9, 2, 4, 1, 10, 7, 15, 12,
        1, 11, 7, 2, 8, 13, 4, 14, 6, 12, 10, 15, 3, 0, 9, 5
    };
    private static final byte[] S34 = {
        9, 0, 7, 11, 12, 5, 10, 6, 15, 3, 1, 14, 2, 8, 4, 13,
        5, 10, 12, 6, 0, 15, 3, 9, 8, 13, 11, 1, 7, 2, 14, 4,
        10, 7, 9, 12, 5, 0, 6, 11, 3, 14, 4, 2, 8, 13, 15, 1,
        3, 9, 15, 0, 6, 10, 5, 12, 14, 2, 1, 7, 13, 4, 8, 11
    };
    private static final byte[] S35 = {
        5, 15, 9, 10, 0, 3, 14, 4, 2, 12, 7, 1, 13, 6, 8, 11,
        6, 9, 3, 15, 5, 12, 0, 10, 8, 7, 13, 4, 2, 11, 14, 1,
        15, 0, 10, 9, 3, 5, 4, 14, 8, 11, 1, 7, 6, 12, 13, 2,
        12, 5, 0, 6, 15, 10, 9, 3, 7, 2, 14, 11, 8, 1, 4, 13
    };
    private static final byte[] S36 = {
        4, 3, 7, 10, 9, 0, 14, 13, 15, 5, 12, 6, 2, 11, 1, 8,
        14, 13, 11, 4, 2, 7, 1, 8, 9, 10, 5, 3, 15, 0, 12, 6,
        13, 0, 10, 9, 4, 3, 7, 14, 1, 15, 6, 12, 8, 5, 11, 2,
        1, 7, 4, 14, 11, 8, 13, 2, 10, 12, 3, 5, 6, 15, 0, 9
    };
    private static final byte[] S37 = {
        4, 10, 15, 12, 2, 9, 1, 6, 11, 5, 0, 3, 7, 14, 13, 8,
        10, 15, 6, 0, 5, 3, 12, 9, 1, 8, 11, 13, 14, 4, 7, 2,
        2, 12, 9, 6, 15, 10, 4, 1, 5, 11, 3, 0, 8, 7, 14, 13,
        12, 6, 3, 9, 0, 5, 10, 15, 2, 13, 4, 14, 7, 11, 1, 8
    };
    private static final byte[] S38 = {
        13, 10, 0, 7, 3, 9, 14, 4, 2, 15, 12, 1, 5, 6, 11, 8,
        2, 7, 13, 1, 4, 14, 11, 8, 15, 12, 6, 10, 9, 5, 0, 3,
        4, 13, 14, 0, 9, 3, 7, 10, 1, 8, 2, 11, 15, 5, 12, 6,
        8, 11, 7, 14, 2, 4, 13, 1, 6, 5, 9, 0, 12, 15, 3, 10
    };


    /* basic compounding of permutation and companding */
    private static void spbox(final byte[] box, final int[] mask, final int[] compiled) {

        for (int i = 0; i < BOXLEN; i++) {
            int entry = 0;
            int spindex;

            spindex = (i / 2) + 16 * (i % 2) + ((i & 32) >>> 1);

            for (int j = 0; j < 4; j++) {
                if (0 != (box[spindex] & (1 << j))) {
                    entry |= mask[j];
                }
            }

            compiled[i] = entry;
        }
    }

    /* Set up SP boxes as per DES-classic*/
    private void initDesSPboxes() {
        spbox(DS1, BITS1, SP1);
        spbox(DS2, BITS2, SP2);
        spbox(DS3, BITS3, SP3);
        spbox(DS4, BITS4, SP4);
        spbox(DS5, BITS5, SP5);
        spbox(DS6, BITS6, SP6);
        spbox(DS7, BITS7, SP7);
        spbox(DS8, BITS8, SP8);
    }

    /* Set up SP boxes as per the modified s3-DES in Schneier */
    private void inits3DesSPboxes() {
        spbox(S31, BITS1, SP1);
        spbox(S32, BITS2, SP2);
        spbox(S33, BITS3, SP3);
        spbox(S34, BITS4, SP4);
        spbox(S35, BITS5, SP5);
        spbox(S36, BITS6, SP6);
        spbox(S37, BITS7, SP7);
        spbox(S38, BITS8, SP8);
    }


    /* Key dependency shuffling of SP box permutations */
    private static void initKDspbox(final byte[] box, final int[] mask, //NOPMD complex
            final int[] compiled, final byte[] key, 
            final int offset, final int index) {

        final int rowswap = key[0 + offset] & (1 << index);
        final int colswap = key[1 + offset] & (1 << index);

        final byte xor = (byte) (((key[2] != 0 && (1 << index) != 0) ? 1 : 0) | //NOPMD ternary
                ((key[3] != 0 && (1 << index) != 0) ? 2 : 0) | //NOPMD ternary
                ((key[4] != 0 && (1 << index) != 0) ? 4 : 0) | //NOPMD ternary
                ((key[5] != 0 && (1 << index) != 0) ? 8 : 0)); //NOPMD ternary

        for (int i = 0; i < BOXLEN; i++) {
            int entry = 0;
            int spindex;
            byte value;

            spindex = (i / 2) + 16 * (i % 2) + ((i & 32) >>> 1);

            if (rowswap != 0) {
                spindex = (spindex + 32) % 64;
            }

            if (colswap != 0) {
                int k = spindex / 32; //NOPMD name
                spindex = (32 * k) + (((spindex - (32 * k)) + 16) % 32);
            }

            value = (byte) ((xor ^ box[spindex]) & 0xF);

            for (int j = 0; j < 4; j++) {
                if (0 != (value & (1 << j))) {
                    entry |= mask[j];
                }
            }

            compiled[i] = entry;
        }
    }

    /* S-box order for Key-dependent DES */
    /* static char scramble[8] =
    {2, 4, 6, 7, 3, 1, 5, 8}; */

    /* Set up Biham's key-dependent DES */
    private void initKDdesSPboxes(final byte[] key, final int offset) {
        initKDspbox(DS2, BITS1, SP1, key, offset, 0);
        initKDspbox(DS4, BITS2, SP2, key, offset, 1);
        initKDspbox(DS6, BITS3, SP3, key, offset, 2);
        initKDspbox(DS7, BITS4, SP4, key, offset, 3);
        initKDspbox(DS3, BITS5, SP5, key, offset, 4);
        initKDspbox(DS1, BITS6, SP6, key, offset, 5);
        initKDspbox(DS5, BITS7, SP7, key, offset, 6);
        initKDspbox(DS8, BITS8, SP8, key, offset, 7);
    }

    public DES_SPboxes() {
        initDesSPboxes();
    }

    public DES_SPboxes(final int dummy) { //NOPMD discriminant
        inits3DesSPboxes();
    }

    public DES_SPboxes(final byte[] key, final int offset) {
        initKDdesSPboxes(key, offset);
    }
}
