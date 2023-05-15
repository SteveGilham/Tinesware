package com.ravnaandtines.crypt.mda;

/** RIPE-MD hash functions
 *<p>
 *	Copyright (C) 1998 Free Software Foundation, Inc.
 *<p>
 * This file is derivative of orignal 'C' code that formed part of GNUPG.
 * <p>
 * It is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * It is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA
 * <p>
 * Java port and MDA-interface Mr. Tines &lt;tines@windsong.demon.co.uk&gt;
 * 26-Dec-1998
 * Tidying and refactoringe Mr. Tines &lt;tines@windsong.demon.co.uk&gt;
 * 25-Dec-2007
 *<P>
 * RIPEMD-160 is not patented, see (as of 25.10.97)
 *   <a href="http://www.esat.kuleuven.ac.be/~bosselae/ripemd160.html">
 *   http://www.esat.kuleuven.ac.be/~bosselae/ripemd160.html</a>
 *<p>
 * Note that the code uses Little Endian byteorder, which is good for
 * 386 etc, but we must add some conversion when used on a big endian box.
 * <pre>
 *
 * Pseudo-code for RIPEMD-160
 *
 * RIPEMD-160 is an iterative hash function that operates on 32-bit words.
 * The round function takes as input a 5-word chaining variable and a 16-word
 * message block and maps this to a new chaining variable. All operations are
 * defined on 32-bit words. Padding is identical to that of MD4.
 *
 *
 * RIPEMD-160: definitions
 *
 *
 *   nonlinear functions at bit level: exor, mux, -, mux, -
 *
 *   f(j, x, y, z) = x XOR y XOR z		  (0 <= j <= 15)
 *   f(j, x, y, z) = (x AND y) OR (NOT(x) AND z)  (16 <= j <= 31)
 *   f(j, x, y, z) = (x OR NOT(y)) XOR z	  (32 <= j <= 47)
 *   f(j, x, y, z) = (x AND z) OR (y AND NOT(z))  (48 <= j <= 63)
 *   f(j, x, y, z) = x XOR (y OR NOT(z))	  (64 <= j <= 79)
 *
 *
 *   added constants (hexadecimal)
 *
 *   K(j) = 0x00000000	    (0 <= j <= 15)
 *   K(j) = 0x5A827999	   (16 <= j <= 31)	int(2**30 x sqrt(2))
 *   K(j) = 0x6ED9EBA1	   (32 <= j <= 47)	int(2**30 x sqrt(3))
 *   K(j) = 0x8F1BBCDC	   (48 <= j <= 63)	int(2**30 x sqrt(5))
 *   K(j) = 0xA953FD4E	   (64 <= j <= 79)	int(2**30 x sqrt(7))
 *   K'(j) = 0x50A28BE6     (0 <= j <= 15)      int(2**30 x cbrt(2))
 *   K'(j) = 0x5C4DD124    (16 <= j <= 31)      int(2**30 x cbrt(3))
 *   K'(j) = 0x6D703EF3    (32 <= j <= 47)      int(2**30 x cbrt(5))
 *   K'(j) = 0x7A6D76E9    (48 <= j <= 63)      int(2**30 x cbrt(7))
 *   K'(j) = 0x00000000    (64 <= j <= 79)
 *
 *
 *   selection of message word
 *
 *   R(j)      = j		      (0 <= j <= 15)
 *   R(16..31) = 7, 4, 13, 1, 10, 6, 15, 3, 12, 0, 9, 5, 2, 14, 11, 8
 *   R(32..47) = 3, 10, 14, 4, 9, 15, 8, 1, 2, 7, 0, 6, 13, 11, 5, 12
 *   R(48..63) = 1, 9, 11, 10, 0, 8, 12, 4, 13, 3, 7, 15, 14, 5, 6, 2
 *   R(64..79) = 4, 0, 5, 9, 7, 12, 2, 10, 14, 1, 3, 8, 11, 6, 15, 13
 *   r0(0..15) = 5, 14, 7, 0, 9, 2, 11, 4, 13, 6, 15, 8, 1, 10, 3, 12
 *   r0(16..31)= 6, 11, 3, 7, 0, 13, 5, 10, 14, 15, 8, 12, 4, 9, 1, 2
 *   r0(32..47)= 15, 5, 1, 3, 7, 14, 6, 9, 11, 8, 12, 2, 10, 0, 4, 13
 *   r0(48..63)= 8, 6, 4, 1, 3, 11, 15, 0, 5, 12, 2, 13, 9, 7, 10, 14
 *   r0(64..79)= 12, 15, 10, 4, 1, 5, 8, 7, 6, 2, 13, 14, 0, 3, 9, 11
 *
 *
 *   amount for rotate left (rol)
 *
 *   S(0..15)  = 11, 14, 15, 12, 5, 8, 7, 9, 11, 13, 14, 15, 6, 7, 9, 8
 *   S(16..31) = 7, 6, 8, 13, 11, 9, 7, 15, 7, 12, 15, 9, 11, 7, 13, 12
 *   S(32..47) = 11, 13, 6, 7, 14, 9, 13, 15, 14, 8, 13, 6, 5, 12, 7, 5
 *   S(48..63) = 11, 12, 14, 15, 14, 15, 9, 8, 9, 14, 5, 6, 8, 6, 5, 12
 *   S(64..79) = 9, 15, 5, 11, 6, 8, 13, 12, 5, 12, 13, 14, 11, 8, 5, 6
 *   S'(0..15) = 8, 9, 9, 11, 13, 15, 15, 5, 7, 7, 8, 11, 14, 14, 12, 6
 *   S'(16..31)= 9, 13, 15, 7, 12, 8, 9, 11, 7, 7, 12, 7, 6, 15, 13, 11
 *   S'(32..47)= 9, 7, 15, 11, 8, 6, 6, 14, 12, 13, 5, 14, 13, 13, 7, 5
 *   S'(48..63)= 15, 5, 8, 11, 14, 14, 6, 14, 6, 9, 12, 9, 12, 5, 15, 8
 *   S'(64..79)= 8, 5, 12, 9, 12, 5, 14, 6, 8, 13, 6, 5, 15, 13, 11, 11
 *
 *
 *   initial value (hexadecimal)
 *
 *   h0 = 0x67452301; h1 = 0xEFCDAB89; h2 = 0x98BADCFE; h3 = 0x10325476;
 *							h4 = 0xC3D2E1F0;
 *
 *
 * RIPEMD-160: pseudo-code
 *
 *   It is assumed that the message after padding consists of t 16-word blocks
 *   that will be denoted with X[i][j], with 0 <= i <= t-1 and 0 <= j <= 15.
 *   The symbol [+] denotes addition modulo 2**32 and rol_s denotes cyclic left
 *   shift (rotate) over S positions.
 *
 *
 *   for i := 0 to t-1 {
 *	 A := h0; B := h1; C := h2; D = h3; E = h4;
 *	 A' := h0; B' := h1; C' := h2; D' = h3; E' = h4;
 *	 for j := 0 to 79 {
 *	     T := rol_s(j)(A [+] f(j, B, C, D) [+] X[i][R(j)] [+] K(j)) [+] E;
 *	     A := E; E := D; D := rol_10(C); C := B; B := T;
 *	     T := rol_s'(j)(A' [+] f(79-j, B', C', D') [+] X[i][R'(j)]
[+] K'(j)) [+] E';
 *	     A' := E'; E' := D'; D' := rol_10(C'); C' := B'; B' := T;
 *	 }
 *	 T := h1 [+] C [+] D'; h1 := h2 [+] D [+] E'; h2 := h3 [+] E [+] A';
 *	 h3 := h4 [+] A [+] B'; h4 := h0 [+] B [+] C'; h0 := T;
 *   }
 * Some examples:
 * ""                    9c1185a5c5e9fc54612808977ee8f548b2258d31
 * "a"                   0bdc9d2d256b3ee9daae347be6f4dc835a467ffe
 * "abc"                 8eb208f7e05d987a9b044a8e98c6b087f15a0bfc
 * "message digest"      5d0689ef49d2fae572b881b123a85ffa21595f36
 * "a...z"               f71c27109c692c1b56bbdceb5b9d2865b3708dbc
 * "abcdbcde...nopq"     12a053384a9c0c88e405a06c27dcf49ada62eb2b
 * "A...Za...z0...9"     b0e20b6e3116640286ed3a87a5713079b21f5189
 * 8 times "1234567890"  9b752e45573d4b39f4dbd3323cab82bf63326bfb
 * 1 million times "a"   52783243c1697bdbe16d37f97f68f08325dc1528
 *</pre>
 */
public final class RIPEM160 implements MDA 
{

    private int h0,  h1,  h2,  h3,  h4; // NOPMD names
    private int nblocks;
    private byte[] buf = new byte[64];
    private int count;

    /**
     * Sole and default constructor
     */
    public RIPEM160() {
        init();
    }

    /**
     * Set up zero counts and magic numbers
     */
    private void init() {
        h0 = 0x67452301;
        h1 = 0xEFCDAB89;
        h2 = 0x98BADCFE;
        h3 = 0x10325476;
        h4 = 0xC3D2E1F0;
        nblocks = 0;
        count = 0;
    }

    /**
     * another bitwise rotation left
     * @param n number of bytes to shift
     * @param x number to rotate
     * @return result of rotation
     */
    private static int rol(final int n, final int x)// NOPMD name
    {
        return (x << n) | (x >>> (32 - n));
    }
    /**
     * shift constants
     */
    private static final byte[] R = { // NOPMD name
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
        7, 4, 13, 1, 10, 6, 15, 3, 12, 0, 9, 5, 2, 14, 11, 8,
        3, 10, 14, 4, 9, 15, 8, 1, 2, 7, 0, 6, 13, 11, 5, 12,
        1, 9, 11, 10, 0, 8, 12, 4, 13, 3, 7, 15, 14, 5, 6, 2,
        4, 0, 5, 9, 7, 12, 2, 10, 14, 1, 3, 8, 11, 6, 15, 13
    };
    private static final byte[] RR = { // NOPMD name
        5, 14, 7, 0, 9, 2, 11, 4, 13, 6, 15, 8, 1, 10, 3, 12,
        6, 11, 3, 7, 0, 13, 5, 10, 14, 15, 8, 12, 4, 9, 1, 2,
        15, 5, 1, 3, 7, 14, 6, 9, 11, 8, 12, 2, 10, 0, 4, 13,
        8, 6, 4, 1, 3, 11, 15, 0, 5, 12, 2, 13, 9, 7, 10, 14,
        12, 15, 10, 4, 1, 5, 8, 7, 6, 2, 13, 14, 0, 3, 9, 11
    };
    private static final byte[] S = { // NOPMD name
        11, 14, 15, 12, 5, 8, 7, 9, 11, 13, 14, 15, 6, 7, 9, 8,
        7, 6, 8, 13, 11, 9, 7, 15, 7, 12, 15, 9, 11, 7, 13, 12,
        11, 13, 6, 7, 14, 9, 13, 15, 14, 8, 13, 6, 5, 12, 7, 5,
        11, 12, 14, 15, 14, 15, 9, 8, 9, 14, 5, 6, 8, 6, 5, 12,
        9, 15, 5, 11, 6, 8, 13, 12, 5, 12, 13, 14, 11, 8, 5, 6
    };
    private static final byte[] SS = { // NOPMD name
        8, 9, 9, 11, 13, 15, 15, 5, 7, 7, 8, 11, 14, 14, 12, 6,
        9, 13, 15, 7, 12, 8, 9, 11, 7, 7, 12, 7, 6, 15, 13, 11,
        9, 7, 15, 11, 8, 6, 6, 14, 12, 13, 5, 14, 13, 13, 7, 5,
        15, 5, 8, 11, 14, 14, 6, 14, 6, 9, 12, 9, 12, 5, 15, 8,
        8, 5, 12, 9, 12, 5, 14, 6, 8, 13, 6, 5, 15, 13, 11, 11
    };

    /**
     * magic number generators
     */
    private static int K(final int a)// NOPMD name
    {
        return a < 16 ? 0x00000000 : a < 32 ? 0x5A827999 : a < 48 ? 0x6ED9EBA1 : a < 64 ? 0x8F1BBCDC : 0xA953FD4E;
    }

    private static int KK(final int a)// NOPMD name
    {
        return a < 16 ? 0x50A28BE6 : a < 32 ? 0x5C4DD124 : a < 48 ? 0x6D703EF3 : a < 64 ? 0x7A6D76E9 : 0x00000000;
    }

    /**
     * mixer functions
     */
    private static int F0(final int x, final int y, final int z)// NOPMD name
    {
        return x ^ y ^ z;
    }

    private static int F1(final int x, final int y, final int z)// NOPMD name
    {
        return (x & y) | (~x & z);
    }

    private static int F2(final int x, final int y, final int z)// NOPMD name
    {
        return (x | ~y) ^ z;
    }

    private static int F3(final int x, final int y, final int z)// NOPMD name
    {
        return (x & z) | (y & ~z);
    }

    private static int F4(final int x, final int y, final int z)// NOPMD name
    {
        return x ^ (y | ~z);
    }

    private static int F(final int a, final int x, // NOPMD name
            final int y, final int z)// NOPMD name
    {
        return a < 16 ? F0(x, y, z) : a < 32 ? F1(x, y, z) : a < 48 ? F2(x, y, z) : a < 64 ? F3(x, y, z) : F4(x, y, z);
    }

    /**
     * Transforms the message which consists of 16 32-bit-words
     * @param data byte array of input
     * @param offset start of data
     */
    private void transform(final byte[] data, final int offset) {
        int aa, bb, cc, dd, ee, t;// NOPMD name
        int rbits;
        int[] x = new int[16]; // NOPMD name

        for (int i = 0; i < 16; ++i) {
            x[i] = // NO-PMD DU
                    ((data[offset + 4 * i] & 0xFF)) |
                    ((data[offset + 4 * i + 1] & 0xFF) << 8) |
                    ((data[offset + 4 * i + 2] & 0xFF) << 16) |
                    ((data[offset + 4 * i + 3] & 0xFF) << 24);
        }

        int a = aa = h0;// NOPMD name
        int b = bb = h1;// NOPMD name
        int c = cc = h2;// NOPMD name
        int d = dd = h3;// NOPMD name
        int e = ee = h4;// NOPMD name

        for (int j = 0; j < 80; j++) {
            t = a + F(j, b, c, d) + x[R[j]] + K(j);
            rbits = S[j];
            a = rol(rbits, t) + e;
            c = rol(10, c);
            t = a;
            a = e;
            e = d;
            d = c;
            c = b;
            b = t;

            t = aa + F(79 - j, bb, cc, dd) + x[RR[j]] + KK(j);
            rbits = SS[j];
            aa = rol(rbits, t) + ee;
            cc = rol(10, cc);
            t = aa;
            aa = ee;
            ee = dd;
            dd = cc;
            cc = bb;
            bb = t;
        }

        t = h1 + c + dd;
        h1 = h2 + d + ee;
        h2 = h3 + e + aa;
        h3 = h4 + a + bb;
        h4 = h0 + b + cc;
        h0 = t;
    }

    /**
     * Feeds a  byte into the hash
     * @param data the byte value
     */
    public void update(final byte data) {
        final byte[] temp = {data};
        update(temp, 0, temp.length);
    }
    
    /**
     * Feeds a batch of bytes into the hash
     * @param data the byte values
     * @param offset the first byte index to take
     * @param length the number of bytes to take
     */
    public void update( // NO-PMD complex
            final byte[] data, 
            int offset, // NO-PMD mutable
            int length) { // NO-PMD mutable
        if (count == 64) { /* flush the buffer */
            transform(buf, 0);
            count = 0;
            nblocks++;
        }
        if (null == data) {
            return;
        }

        // finish a partial block
        if (count > 0) {
            for (; length != 0 && count < 64; length--) {
                buf[count++] = data[offset++];
            }
            update(null, 0, 0); /* just do the above buffer flush */
            if (0 == length) {
                return;
            }
        }

        // for each whole block remaining
        while (length >= 64) {
            transform(data, offset);
            count = 0;
            nblocks++;
            length -= 64;
            offset += 64;
        }

        // and the dregs (can assert count==0 and length < 64 here)
        //if(0 != count || length >= 64) 
        //{ throw new IllegalStateException(":"+count+":"+length);}
        for (; length > 0 /*&& count < 64*/; length--) {
            buf[count++] = data[offset++];
        }
    }

    /**
     * consolidates the input, and reinitialises the hash
     * @return the hash value
     */
    public byte[] digest() {
        int lsb;
        update(null, 0, 0); /* flush */

        int msb = 0;
        int t = nblocks; // NOPMD name
        /* multiply by 64 to make a byte count */
        if ((lsb = (t << 6)) < t) { // NOPMD assignment
            msb++; // only for very long datasets
        }

        msb += t >>> 26;
        t = lsb;
        if ((lsb = (t + count)) < t) {// NOPMD assignment
            msb++; // NO-PMD DD  // only for very long datasets
        }/* add the count */

        t = lsb;
        /* multiply by 8 to make a bit count */
        if ((lsb = (t << 3)) < t) {// NOPMD assignment
            msb++;  // only for very long datasets
        }

        msb += t >> 29;

        if (count < 56) { /* enough room */
            buf[count++] = (byte) 0x80; /* pad */
            while (count < 56) {
                buf[count++] = 0;
            }  /* pad */
        } else { /* need one extra block */
            buf[count++] = (byte) 0x80; /* pad character */
            while (count < 64) {
                buf[count++] = 0;
            }
            update(null, 0, 0);  /* flush */

            for (int k = 0; k < 56; ++k) {
                buf[k] = 0;
            } /* fill next block with zeroes */
        }
        /* append the 64 bit count */
        buf[56] = (byte) (0xff & (lsb));
        buf[57] = (byte) (0xff & (lsb >>> 8));
        buf[58] = (byte) (0xff & (lsb >>> 16));
        buf[59] = (byte) (0xff & (lsb >>> 24));
        buf[60] = (byte) (0xff & (msb));
        buf[61] = (byte) (0xff & (msb >>> 8));
        buf[62] = (byte) (0xff & (msb >>> 16));
        buf[63] = (byte) (0xff & (msb >>> 24));
        transform(buf, 0);

        /* and unpack the result */
        byte[] p = new byte[20]; // NOPMD name
        int j = 0; // NOPMD name

        p[j++] = (byte) (0xff & (h0));
        p[j++] = (byte) (0xff & (h0 >> 8));
        p[j++] = (byte) (0xff & (h0 >> 16));
        p[j++] = (byte) (0xff & (h0 >> 24));

        p[j++] = (byte) (0xff & (h1)); 
        p[j++] = (byte) (0xff & (h1 >> 8)); 
        p[j++] = (byte) (0xff & (h1 >> 16)); 
        p[j++] = (byte) (0xff & (h1 >> 24)); 

        p[j++] = (byte) (0xff & (h2)); 
        p[j++] = (byte) (0xff & (h2 >> 8)); 
        p[j++] = (byte) (0xff & (h2 >> 16)); 
        p[j++] = (byte) (0xff & (h2 >> 24)); 

        p[j++] = (byte) (0xff & (h3)); 
        p[j++] = (byte) (0xff & (h3 >> 8)); 
        p[j++] = (byte) (0xff & (h3 >> 16)); 
        p[j++] = (byte) (0xff & (h3 >> 24)); 

        p[j++] = (byte) (0xff & (h4)); 
        p[j++] = (byte) (0xff & (h4 >> 8)); 
        p[j++] = (byte) (0xff & (h4 >> 16)); 
        p[j++] = (byte) (0xff & (h4 >> 24)); 

        init();
        return p;
    }

}
