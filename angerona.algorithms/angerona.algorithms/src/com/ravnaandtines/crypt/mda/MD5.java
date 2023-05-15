package com.ravnaandtines.crypt.mda;

/**<pre>
 ***********************************************************************
 ** Copyright (C) 1990, RSA Data Security, Inc. All rights reserved.  **
 **                                                                   **
 ** License to copy and use this software is granted provided that    **
 ** it is identified as the "RSA Data Security, Inc. MD5 Message-     **
 ** Digest Algorithm" in all material mentioning or referencing this  **
 ** software or this function.                                        **
 **                                                                   **
 ** License is also granted to make and use derivative works          **
 ** provided that such works are identified as "derived from the RSA  **
 ** Data Security, Inc. MD5 Message-Digest Algorithm" in all          **
 ** material mentioning or referencing the derived work.              **
 **                                                                   **
 ** RSA Data Security, Inc. makes no representations concerning       **
 ** either the merchantability of this software or the suitability    **
 ** of this software for any particular purpose.  It is provided "as  **
 ** is" without express or implied warranty of any kind.              **
 **                                                                   **
 ** These notices must be retained in any copies of any part of this  **
 ** documentation and/or software.                                    **
 ***********************************************************************
 *
 * Edited 7 May 93 by CP to change the interface to match that
 * of the MD5 routines in RSAREF.  Due to this alteration, this
 * code is "derived from the RSA Data Security, Inc. MD5 Message-
 * Digest Algorithm".  (See below.)  Also added argument names
 * to the prototypes.
 *
 * Interface modified for CTC - Mr. Tines Jun. 96
 * unit32_t standard 32-bit unsigned type - Mr. Tines 8-Jan-97
 * More interface tweaks - Mr. Tines 16-feb-97
 * Java port - Mr. Tines Sept 1998
 * Updates -- Mr. Tines Dec 2007
 * 
 MD5 test suite:
MD5 ("") = d41d8cd98f00b204e9800998ecf8427e
MD5 ("a") = 0cc175b9c0f1b6a831c399e269772661
MD5 ("abc") = 900150983cd24fb0d6963f7d28e17f72
MD5 ("message digest") = f96b697d7cb7938d525a2f31aaf161d0
MD5 ("abcdefghijklmnopqrstuvwxyz") = c3fcd3d76192e4007dfb496cca67e13b
MD5 ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789") =
d174ab98d277d9f5a5611c2c9f419d9f
MD5 ("123456789012345678901234567890123456789012345678901234567890123456
78901234567890") = 57edf4a22be3c955ac49da2e2107b67a
 * 
 * </pre>
 */
public final class MD5 implements MDA {

    /**
     * Digest SIZE in bytes
     */
    private static final int SIZE = 16;

    /* Data structure for MD5 (Message-Digest) computation */
    /**
     * scratch buffer
     */
    private int[] buf = new int[4];
    /**
     * number of _bits_ handled mod 2^64
     */
    private long runningBitCount;
    /**
     * input buffer
     */
    private byte[] input = new byte[64]; // NO-PMD short name

    /**
     * Feeds a  byte into the hash
     * @param data the byte value
     */
    public void update(final byte data) {
        final byte[] temp = {data};
        update(temp, 0, temp.length);
    }

    /**
     * consolidates the input, and reinitialises the hash
     * @return the hash value
     */
    public byte[] digest() {
        final byte[] buffer = new byte[SIZE];
        finish();
        System.arraycopy(input, 0, buffer, 0, SIZE);
        init();
        return buffer;
    }
    /**
     * MD strengthening padding
     */
    private static final byte[] PADDING = {
        (byte) 0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };

    /**
     * F, G, H and I are basic MD5 functions
     */
    private static int F(final int x, final int y, final int z) { //NOPMD names
        return (x & y) | ((~x) & z);
    }

    private static int G(final int x, final int y, final int z) { //NOPMD names
        return (x & z) | (y & (~z));
    }

    private static int H(final int x, final int y, final int z) { //NOPMD names
        return x ^ y ^ z;
    }

    private static int I(final int x, final int y, final int z) { //NOPMD names
        return y ^ (x | (~z));
    }

    /**
     * ROTATE_LEFT rotates x left n bits
     */
    private static int ROTATE_LEFT(final int x, final int n) { //NOPMD names
        return (x << n) | (x >>> (32 - n));
    }

    /**
     * FF, GG, HH, and II transformations for rounds 1, 2, 3, and 4
     * Rotation is separate from addition to prevent recomputation
     */
    private static int FF(int a, final int b, final int c, //NOPMD names
            final int d, final int x,final int s, final int ac) {  //NOPMD names
        a += F(b, c, d) + x + ac;
        a = ROTATE_LEFT(a, s);
        a += b;
        return a;
    }

    private static int GG(int a, final int b, final int c, //NOPMD names
            final int d, final int x,final int s, final int ac) {  //NOPMD names
        a += G(b, c, d) + x + ac;
        a = ROTATE_LEFT(a, s);
        a += b;
        return a;
    }

    private static int HH(int a, final int b, final int c, // NOPMD names
            final int d, final int x,final int s, final int ac) {  // NOPMD names
        a += H(b, c, d) + x + ac;
        a = ROTATE_LEFT(a, s);
        a += b;
        return a;
    }

    private static int II(int a, final int b, final int c, //NOPMD names
            final int d, final int x,final int s, final int ac) {  //NOPMD names
        a += I(b, c, d) + x + ac;
        a = ROTATE_LEFT(a, s);
        a += b;
        return a;
    }

    /**
     * Initializes the message-digest context
     * All fields are set to zero.
     */
    private void init() {
        runningBitCount = 0;

        //Load magic initialization constants.
        buf[0] = 0x67452301;
        buf[1] = 0xefcdab89;
        buf[2] = 0x98badcfe;
        buf[3] = 0x10325476;

        for (int l = 0; l < 64; ++l) {
            input[l] = 0;
        }
    }

    /**
     * default constructor
     */
    public MD5() {
        init();
    }

    /**
     * Feeds a batch of bytes into the hash
     * @param data the byte values
     * @param offset the first byte index to take
     * @param length the number of bytes to take
     */
    public void update(final byte[] data, int offset, int length) {//NO-PMD mutable
        if (null == data) {
            return;
        }


        /* compute number of bytes mod 64 */
        int mdi = (int) ((this.runningBitCount >> 3) & 0x3F); 

        /* update number of bits */
        this.runningBitCount += length << 3;
        int[] in2 = new int[16]; // NO-PMD DU, DD

        while (length-- != 0) {
            /* add new character to data, increment mdi */
            this.input[mdi++] = data[offset++];

            /* transform if necessary */
            if (mdi == 0x40) {
                for (int i = 0,  ii = 0; i < 16; i++, ii += 4) { 
                    in2[i] = // NO-PMD DU
                            ((this.input[ii + 3] & 0xFF) << 24) |
                            ((this.input[ii + 2] & 0xFF) << 16) |
                            ((this.input[ii + 1] & 0xFF) << 8) |
                            (this.input[ii] & 0xFF);
                }
                Transform(in2);
                mdi = 0; //NO-PMD DU
            }
        }
    }

    /**
     * Terminates the message-digest computation and
     * ends with the desired message digest in digest[0...15].
     */
    private void finish() {
        int[] in2 = new int[16]; //NO-PMD DU
        int padLen;

        /* save number of bits */
        in2[14] = (int) (this.runningBitCount & 0xFFFFFFFF);
        in2[15] = (int) ((this.runningBitCount >>> 32) & 0xFFFFFFFF); 

        /* compute number of bytes mod 64 */
        final int mdi = (int) ((this.runningBitCount >> 3) & 0x3F);

        /* pad out to 56 mod 64 */
        padLen = (mdi < 56) ? (56 - mdi) : (120 - mdi);
        update(PADDING, 0, padLen);

        /* append length in bits and transform */
        for (int i = 0, ii = 0; i < 14; i++, ii += 4) {
            in2[i] =  //NO-PMD DU
                    ((this.input[ii + 3] & 0xFF) << 24) |
                    ((this.input[ii + 2] & 0xFF) << 16) |
                    ((this.input[ii + 1] & 0xFF) << 8) |
                    (this.input[ii] & 0xFF);
        }
        Transform(in2);

        /* store buffer in digest */
        for (int i = 0, ii = 0; i < 4; i++, ii += 4) {
            this.input[ii] = (byte) (buf[i] & 0xFF);
            this.input[ii + 1] = (byte) ((buf[i] >> 8) & 0xFF);
            this.input[ii + 2] = (byte) ((buf[i] >> 16) & 0xFF);
            this.input[ii + 3] = (byte) ((buf[i] >> 24) & 0xFF);
        }
    }
    /**
     * round constants
     */
    private static final int S11 = 7;
    private static final int S12 = 12;
    private static final int S13 = 17;
    private static final int S14 = 22;
    private static final int S21 = 5;
    private static final int S22 = 9;
    private static final int S23 = 14;
    private static final int S24 = 20;
    private static final int S31 = 4;
    private static final int S32 = 11;
    private static final int S33 = 16;
    private static final int S34 = 23;
    private static final int S41 = 6;
    private static final int S42 = 10;
    private static final int S43 = 15;
    private static final int S44 = 21;

    /**
     * Basic MD5 step. Transforms buf based on in.  Note that if the
     * Mysterious Constants are arranged backwards in little-endian
     * order and decrypted with the DES they produce OCCULT MESSAGES!
     * @param in[] data buffer to transform
     */
    private void Transform(final int[] in) {//NOPMD names
        int a = buf[0], b = buf[1], c = buf[2], d = buf[3];  //NOPMD names

        /* Round 1 */
        a = FF(a, b, c, d, in[ 0], S11, 0xD76AA478); /* 1 */
        d = FF(d, a, b, c, in[ 1], S12, 0xE8C7B756); /* 2 */
        c = FF(c, d, a, b, in[ 2], S13, 0x242070DB); /* 3 */
        b = FF(b, c, d, a, in[ 3], S14, 0xC1BDCEEE); /* 4 */
        a = FF(a, b, c, d, in[ 4], S11, 0xF57C0FAF); /* 5 */
        d = FF(d, a, b, c, in[ 5], S12, 0x4787C62A); /* 6 */
        c = FF(c, d, a, b, in[ 6], S13, 0xA8304613); /* 7 */
        b = FF(b, c, d, a, in[ 7], S14, 0xFD469501); /* 8 */
        a = FF(a, b, c, d, in[ 8], S11, 0x698098D8); /* 9 */
        d = FF(d, a, b, c, in[ 9], S12, 0x8B44F7AF); /* 10 */
        c = FF(c, d, a, b, in[10], S13, 0xFFFF5BB1); /* 11 */
        b = FF(b, c, d, a, in[11], S14, 0x895CD7BE); /* 12 */
        a = FF(a, b, c, d, in[12], S11, 0x6B901122); /* 13 */
        d = FF(d, a, b, c, in[13], S12, 0xFD987193); /* 14 */
        c = FF(c, d, a, b, in[14], S13, 0xA679438E); /* 15 */
        b = FF(b, c, d, a, in[15], S14, 0x49B40821); /* 16 */

        /* Round 2 */
        a = GG(a, b, c, d, in[ 1], S21, 0xF61E2562); /* 17 */
        d = GG(d, a, b, c, in[ 6], S22, 0xC040B340); /* 18 */
        c = GG(c, d, a, b, in[11], S23, 0x265E5A51); /* 19 */
        b = GG(b, c, d, a, in[ 0], S24, 0xE9B6C7AA); /* 20 */
        a = GG(a, b, c, d, in[ 5], S21, 0xD62F105D); /* 21 */
        d = GG(d, a, b, c, in[10], S22, 0x02441453); /* 22 */
        c = GG(c, d, a, b, in[15], S23, 0xD8A1E681); /* 23 */
        b = GG(b, c, d, a, in[ 4], S24, 0xE7D3FBC8); /* 24 */
        a = GG(a, b, c, d, in[ 9], S21, 0x21E1CDE6); /* 25 */
        d = GG(d, a, b, c, in[14], S22, 0xC33707D6); /* 26 */
        c = GG(c, d, a, b, in[ 3], S23, 0xF4D50D87); /* 27 */
        b = GG(b, c, d, a, in[ 8], S24, 0x455A14ED); /* 28 */
        a = GG(a, b, c, d, in[13], S21, 0xA9E3E905); /* 29 */
        d = GG(d, a, b, c, in[ 2], S22, 0xFCEFA3F8); /* 30 */
        c = GG(c, d, a, b, in[ 7], S23, 0x676F02D9); /* 31 */
        b = GG(b, c, d, a, in[12], S24, 0x8D2A4C8A); /* 32 */

        /* Round 3 */
        a = HH(a, b, c, d, in[ 5], S31, 0xFFFA3942); /* 33 */
        d = HH(d, a, b, c, in[ 8], S32, 0x8771F681); /* 34 */
        c = HH(c, d, a, b, in[11], S33, 0x6D9D6122); /* 35 */
        b = HH(b, c, d, a, in[14], S34, 0xFDE5380C); /* 36 */
        a = HH(a, b, c, d, in[ 1], S31, 0xA4BEEA44); /* 37 */
        d = HH(d, a, b, c, in[ 4], S32, 0x4BDECFA9); /* 38 */
        c = HH(c, d, a, b, in[ 7], S33, 0xF6BB4B60); /* 39 */
        b = HH(b, c, d, a, in[10], S34, 0xBEBFBC70); /* 40 */
        a = HH(a, b, c, d, in[13], S31, 0x289B7EC6); /* 41 */
        d = HH(d, a, b, c, in[ 0], S32, 0xEAA127FA); /* 42 */
        c = HH(c, d, a, b, in[ 3], S33, 0xD4EF3085); /* 43 */
        b = HH(b, c, d, a, in[ 6], S34, 0x04881D05); /* 44 */
        a = HH(a, b, c, d, in[ 9], S31, 0xD9D4D039); /* 45 */
        d = HH(d, a, b, c, in[12], S32, 0xE6DB99E5); /* 46 */
        c = HH(c, d, a, b, in[15], S33, 0x1FA27CF8); /* 47 */
        b = HH(b, c, d, a, in[ 2], S34, 0xC4AC5665); /* 48 */

        /* Round 4 */
        a = II(a, b, c, d, in[ 0], S41, 0xF4292244); /* 49 */
        d = II(d, a, b, c, in[ 7], S42, 0x432AFF97); /* 50 */
        c = II(c, d, a, b, in[14], S43, 0xAB9423A7); /* 51 */
        b = II(b, c, d, a, in[ 5], S44, 0xFC93A039); /* 52 */
        a = II(a, b, c, d, in[12], S41, 0x655B59C3); /* 53 */
        d = II(d, a, b, c, in[ 3], S42, 0x8F0CCC92); /* 54 */
        c = II(c, d, a, b, in[10], S43, 0xFFEFF47D); /* 55 */
        b = II(b, c, d, a, in[ 1], S44, 0x85845DD1); /* 56 */
        a = II(a, b, c, d, in[ 8], S41, 0x6FA87E4F); /* 57 */
        d = II(d, a, b, c, in[15], S42, 0xFE2CE6E0); /* 58 */
        c = II(c, d, a, b, in[ 6], S43, 0xA3014314); /* 59 */
        b = II(b, c, d, a, in[13], S44, 0x4E0811A1); /* 60 */
        a = II(a, b, c, d, in[ 4], S41, 0xF7537E82); /* 61 */
        d = II(d, a, b, c, in[11], S42, 0xBD3AF235); /* 62 */
        c = II(c, d, a, b, in[ 2], S43, 0x2AD7D2BB); /* 63 */
        b = II(b, c, d, a, in[ 9], S44, 0xEB86D391); /* 64 */

        buf[0] += a;
        buf[1] += b;
        buf[2] += c;
        buf[3] += d;
    }
}
