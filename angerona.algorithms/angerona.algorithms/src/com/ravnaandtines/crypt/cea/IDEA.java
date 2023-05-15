package com.ravnaandtines.crypt.cea;

/**
 * <pre>
 * 
 *<h2>The IDEA patent</h2>
 *
 * IDEA has a horrible software patent on it that is not scheduled to run out 
 * any time soon.
 * <table><thead>
 * <tr><th>where</th><th>number</th><th>application</th><th>issued</th><th>expiration</th></tr>
 * </thead><tbody>
 * <tr><td>USA</td><td>5,214,703</td><td>1991-05-16</td><td>1993-05-25</td><td>2010-05-25</td></tr>
 * <tr><td>Europe</td><td>0482154</td><td>1991-05-16</td><td>1993-06-30</td><td>2011-05-16</td></tr>
 * <tr><td>Japan</td><td>508119/1991</td><td>1991-05-16</td><td>2001-08-31</td><td>????-??-??</td></tr>
 * </tbody></table>
 *
 * (The european patent only covers Austria, France, Germany, Italy, 
 * The Netherlands, Spain, Sweden, Switzerland and The United Kingdom.)
 *  
 * <hr>
 *  Class IDEA -
 *	based upon idea.c - C source code for IDEA block cipher.
 *	IDEA (International Data Encryption Algorithm), formerly known as
 *	IPES (Improved Proposed Encryption Standard).
 *	Algorithm developed by Xuejia Lai and James L. Massey, of ETH Zurich.
 *	This implementation modified and derived from original C code
 *	developed by Xuejia Lai.
 *	Zero-based indexing added, names changed from IPES to IDEA.
 *	CFB functions added.  Random number routines added.
 *
 *  Optimized for speed 21 Oct 92 by Colin Plumb.
 *  Very minor speedup on 23 Feb 93 by Colin Plumb.
 *  idearand() given a separate expanded key on 25 Feb 93, Colin Plumb.
 *
 *	There are two adjustments that can be made to this code to
 *	speed it up.  Defaults may be used for PCs.  Only the -DIDEA32
 *	pays off significantly if selectively set or not set.
 *	Experiment to see what works better for you.
 *
 *	Multiplication: default is inline, -DAVOID_JUMPS uses a
 *		different version that does not do any conditional
 *		jumps (a few percent worse on a SPARC), while
 *		-DSMALL_CACHE takes it out of line to stay
 *		within a small on-chip code cache.
 *	Variables: normally, 16-bit variables are used, but some
 *		machines (notably RISCs) do not have 16-bit registers,
 *		so they do a great deal of masking.  -DIDEA32 uses "int"
 *		register variables and masks explicitly only where
 *		necessary.  On a SPARC, for example, this boosts
 *		performace by 30%.
 *
 *	The IDEA(tm) block cipher is covered by a patent held by ETH and a
 *	Swiss company called Ascom-Tech AG.  The Swiss patent number is
 *	PCT/CH91/00117.  International patents are pending. IDEA(tm) is a
 *	trademark of Ascom-Tech AG.  There is no license fee required for
 *	noncommercial use.  Commercial users may obtain licensing details
 *	from Dieter Profos, Ascom Tech AG, Solothurn Lab, Postfach 151, 4502
 *	Solothurn, Switzerland, Tel +41 65 242885, Fax +41 65 235761.
 *
 *	The IDEA block cipher uses a 64-bit block size, and a 128-bit key
 *	size.  It breaks the 64-bit cipher block into four 16-bit words
 *	because all of the primitive inner operations are done with 16-bit
 *	arithmetic.  It likewise breaks the 128-bit cipher key into eight
 *	16-bit words.
 *
 *	For further information on the IDEA cipher, see these papers:
 *	1) Xuejia Lai, "Detailed Description and a Software Implementation of
 *  	   the IPES Cipher", Institute for Signal and Information
 *   	   Processing, ETH-Zentrum, Zurich, Switzerland, 1991
 *	2) Xuejia Lai, James L. Massey, Sean Murphy, "Markov Ciphers and
 *   	   Differential Cryptanalysis", Advances in Cryptology- EUROCRYPT'91
 *
 *	This code assumes that each pair of 8-bit bytes comprising a 16-bit
 *	word in the key and in the cipher block are externally represented
 *	with the Most Significant Byte (MSB) first, regardless of the
 *	internal native byte order of the target CPU.
 * Modified 6 Mar 1996 by Heimdall to copy the IV rather than copying
 *   the pointer to the IV.
 *
 *   Modified 14 Apr 1996 by Mr. Tines to separate the CFB mode from the
 *   IDEA algorithm; add the NO_IDEA bracketing to allow simple compilation
 *   of patent-free option.  External routines are in the NO_IDEA case set
 *   to be no-ops.
 *
 *   Localise keyschedule deallocation with
 *   the allocation.  Mr. Tines 16-Feb-97
 *
 *   Java port Mr. Tines 16-Nov-1998
 * 
 * </pre>
 * @author Mr. Tines
 * @version 1.0 16-Nov-1998
 * @version 2.0 25-Dec-2007
 */

/*
Encryption key subblocks:
round 1:         1      2      3      4      5      6
round 2:         7      8   1024   1536   2048   2560
round 3:      3072   3584   4096    512     16     20
round 4:        24     28     32      4      8     12
round 5:     10240  12288  14336  16384   2048   4096
round 6:      6144   8192    112    128     16     32
round 7:        48     64     80     96      0   8192
round 8:     16384  24576  32768  40960  49152  57345
round 9:       128    192    256    320
Decryption key subblocks:
round 1:     65025  65344  65280  26010  49152  57345
round 2:     65533  32768  40960  52428      0   8192
round 3:     42326  65456  65472  21163     16     32
round 4:     21835  65424  57344  65025   2048   4096
round 5:     13101  51200  53248  65533      8     12
round 6:     19115  65504  65508  49153     16     20
round 7:     43670  61440  61952  65409   2048   2560
round 8:     18725  64512  65528  21803      5      6
round 9:         1  65534  65533  49153
Encrypting 1024 KBytes (65536 blocks)...1.997 seconds = 525338 bytes per second
X      0        1       2       3
Y  52859    23597    6202   63282
T      0        1       2       3
Get    Cypher: af db be c8 35 45 b1 95
Expect Plain : 01 23 45 67 89 AB CD EF
Get    Plain : 01 23 45 67 89 ab cd ef
Key: 00 01 00 02 00 03 00 04 00 05 00 06 00 07 00 08
Expect Cypher: 11 fb ed 2b 01 98 6d e5
Get    Cypher: 11 fb ed 2b 01 98 6d e5
Expect Plain : 00 00 00 01 00 02 00 03
Get    Plain : 00 00 00 01 00 02 00 03
Key: 3a 98 4e 20 00 19 5d b3 2e e5 01 c8 c4 7c ea 60
Expect Cypher: 97 bc d8 20 07 80 da 86
Get    Cypher: 97 bc d8 20 07 80 da 86
Expect Plain : 01 02 03 04 05 06 07 08
Get    Plain : 01 02 03 04 05 06 07 08
Key: 00 64 00 c8 01 2c 01 90 01 f4 02 58 02 bc 03 20
Expect Cypher: 65 be 87 e7 a2 53 8a ed
Get    Cypher: 65 be 87 e7 a2 53 8a ed
Expect Plain : 05 32 0a 64 14 c8 19 fa
Get    Plain : 05 32 0a 64 14 c8 19 fa
Normal exit.
 */
public final class IDEA implements CEA { //NOPMD complex

    public IDEA() {
        destroy();
    }

    /**
     * Signal that the algorithm is included (a skeleton implementation
     * would have this routine return false, return a null licence string
     * and nothing else
     */
    public static boolean isAvailable() {
        return true;
    }
    /**
     * Evaluates the local end of line once
     */
    private static final String NEWLINE = System.getProperty("line.separator");

    /**
     * Return the algorithm licencse as a string
     * @return IDEA licence
     */
    public static String getLicence() {
        return "This Software/Hardware product contains the algorithm IDEA as described and " +
                "claimed in US Patent No. 5,214,703, EPO Patent No. 0482154 and filed " +
                "Japanese Patent Application No. 508119/1991 \"Device for the conversion of " +
                "a digital block and use of same\" (hereinafter referred to as \"Algorithm\"). " +
                "Any use of the Algorithm for Commercial Purposes is thus subject to a " +
                "license from Ascom Systec Ltd. of CH-5506 Mgenwil (Switzerland), being the " +
                "patentee and sole owner of all rights, including the term IDEA.  Commercial " +
                "Purposes shall mean any revenue generating purpose including but not limited " +
                "to" + NEWLINE +
                "i) using the Algorithm for company internal purposes (subject to a Site " +
                "License)." + NEWLINE +
                "ii) incorporating an application software containing the Algorithm into any " +
                "hardware and/or software and distributing such hardware and/or software " +
                "and/or providing services related thereto to others (subject to a Product " +
                "License)." + NEWLINE +
                "iii) using a product containing an application software that uses the " +
                "Algorithm (subject to an End-User License), except in case where such " +
                "End-User has acquired an implied license by purchasing the said product " +
                "from an authorized licensee or where the End-User has already signed up for " +
                "a Site License.  All such commercial license agreements are available " +
                "exclusively from Ascom Systec Ltd. and may be requested via the Internet " +
                "World Wide Web at http://www.ascom.ch/systec or by sending an electronic " +
                "mail to IDEA@ascom.ch. " + NEWLINE +
                "Any misuse will be prosecuted." + NEWLINE +
                "Use other than for Commercial Purposes is strictly limited to data transfer " +
                "between private individuals and not serving Commercial Purposes. The use by " +
                "government agencies, non-profit organizations etc. is considered as use for " +
                "Commercial Purposes but may be subject to special conditions. Requests for " +
                "waivers for non-commercial use (e.g. by software developers) are welcome. " + NEWLINE;
    }
    public static final int ROUNDS = 8;		/* Don't change this value, should be 8 */
    

    private static final int KEYLEN = (6 * ROUNDS + 4);	/* length of key schedule */


    static class Key { // NOPMD no need for constructor

        public short[] key = new short[KEYLEN]; //NOPMD is short

        public void wipe() {
            for (int i = 0; i < key.length; ++i) {
                key[i] = 0;
            }
        }
    }

    static int low16(final int x) { //NOPMD name
        return (x & 0xFFFF);
    }

    /*
     *	Compute multiplicative inverse of x, modulo (2**16)+1,
     *	using Euclid's GCD algorithm.  It is unrolled twice to
     *	avoid swapping the meaning of the registers each iteration,
     *	and some subtracts of t have been changed to adds.
     */
    private static short inv(final int xi) { //NOPMD short, name
        int t0, t1;         //NOPMD name
        int q, y;           //NOPMD name
        int x = low16(xi);  //NOPMD name

        if (x <= 1) {
            return (short) x;//NOPMD short
        }	/* 0 and 1 are self-inverse */
        t1 = low16(0x10001 / x);	/* Since x >= 2, this fits into 16 bits */
        y = low16(0x10001 % x);
        if (y == 1) {
            return (short) low16(1 - t1); //NOPMD short
        }
        t0 = 1;
        do {
            q = low16(x / y);
            x = low16(x % y);
            t0 += low16(q * t1);
            t0 = low16(t0);
            if (x == 1) {
                return (short) t0; //NOPMD short
            }
            q = low16(y / x);
            y = low16(y % x);
            t1 += low16(q * t0);
            t1 = low16(t1);
        } while (y != 1);
        return (short) low16(1 - t1); //NOPMD short
    } /* inv */

    /*	Compute IDEA encryption subkeys Z */

    static void enKey(final short[] userkey, final short[] Z) { //NOPMD short, names
        int i, j; //NOPMD names
        for (j = 0; j < 8; j++) {
            Z[j] = userkey[j];
        }

        int z0 = 0;  //NOPMD names
        for (i = 0; j < KEYLEN; j++) {
            i++;
            Z[z0 + i + 7] = (short) low16(((Z[z0 + (i & 7)] << 9) |  //NOPMD short
                    ((Z[z0 + (i + 1 & 7)] & 0xFFFF) >>> 7)));
            z0 += i & 8;
            i &= 7;
        }
    }        /* en_key_idea */

    /*	Compute IDEA decryption subkeys DK from encryption subkeys Z */
    /* Note: these buffers *may* overlap! */

    static void deKey(final Key Z, final Key DK) { //NOPMD names
        int j;              //NOPMD names
        short t1, t2, t3;   //NOPMD names, short
        final Key T = new Key(); //NOPMD name
        int p = KEYLEN; //	uint16_t *p = T + KEYLEN; //NOPMD name

        int z0 = 0;         //NOPMD name

        t1 = inv(Z.key[z0++]);
        t2 = (short) low16(-Z.key[z0++]); //NOPMD short
        t3 = (short) low16(-Z.key[z0++]); //NOPMD short
        T.key[--p] = inv(Z.key[z0++]);
        T.key[--p] = t3;
        T.key[--p] = t2;
        T.key[--p] = t1;

        for (j = 1; j < ROUNDS; j++) {
            t1 = Z.key[z0++];
            T.key[--p] = Z.key[z0++];
            T.key[--p] = t1;

            t1 = inv(Z.key[z0++]);
            t2 = (short) low16(-Z.key[z0++]); //NOPMD short
            t3 = (short) low16(-Z.key[z0++]); //NOPMD short
            T.key[--p] = inv(Z.key[z0++]);
            T.key[--p] = t2;
            T.key[--p] = t3;
            T.key[--p] = t1;
        }
        t1 = Z.key[z0++];
        T.key[--p] = Z.key[z0++];
        T.key[--p] = t1;

        t1 = inv(Z.key[z0++]);
        t2 = (short) low16(-Z.key[z0++]); //NOPMD short
        t3 = (short) low16(-Z.key[z0++]); //NOPMD short
        T.key[--p] = inv(Z.key[z0++]);
        T.key[--p] = t3;
        T.key[--p] = t2;
        T.key[--p] = t1;

        /* Copy and destroy temp copy */
        for (j = 0; j < KEYLEN; j++) {
            DK.key[j] = T.key[j];
            T.key[j] = 0;
        }
    } /* de_key_idea */

    /*
     * MUL(x,y) computes x = x*y, modulo 0x10001.
     */

    private static short multiply(short a, short b) { //NOPMD names, short

        if (a != 0) {  //NOPMD c
            if (b != 0) {  //NOPMD c
                final int p = low16(a) * low16(b);   //NOPMD name
                b = (short) low16(p);                //NOPMD short
                a = (short) low16(p >> 16);          //NOPMD short
                return (short) (b - a + (((b & 0xFFFF) < (a & 0xFFFF)) ? 1 : 0)); //NOPMD short
            } else {
                return (short) (1 - a); //NOPMD short
            }
        } else {
            return (short) (1 - b); //NOPMD short
        }
    }        /* mul */

    /*	IDEA encryption/decryption algorithm */
    /* Note that in and out can be the same buffer */

    static void cipher(final short[] in, final short[] out, final Key Z) { //NOPMD names, short
        short x1, x2, x3, x4, s2, s3;   //NOPMD names
        int r = ROUNDS;                 //NOPMD names

        x1 = in[0];
        x2 = in[1];
        x3 = in[2];
        x4 = in[3];
        int z0 = 0;                     //NOPMD names

        do {
            x1 = multiply(x1, Z.key[z0++]);
            x2 += Z.key[z0++];
            x3 += Z.key[z0++];
            x4 = multiply(x4, Z.key[z0++]);

            s3 = x3;
            x3 ^= x1;
            x3 = multiply(x3, Z.key[z0++]);
            s2 = x2;
            x2 ^= x4;
            x2 += x3;
            x2 = multiply(x2, Z.key[z0++]);
            x3 += x2;

            x1 ^= x2;
            x4 ^= x3;
            x2 ^= s3;
            x3 ^= s2;
        } while (--r > 0);
        x1 = multiply(x1, Z.key[z0++]);

        out[0] = x1;
        out[1] = (short) low16(x3 + Z.key[z0++]); //NOPMD short
        out[2] = (short) low16(x2 + Z.key[z0++]); //NOPMD short
        x4 = multiply(x4, Z.key[z0++]);
        out[3] = x4;
    } /* cipher_idea */

    /*-------------------------------------------------------------*/

    /*
     * This is the number of Kbytes of test data to encrypt.
     * It defaults to 1 MByte.
     */

    private Key[] keyschedule = null;

    /**
     * Initialise the object with one or three key blocks
     * @param key array of key bytes, 1 or 3 key block lengths
     * @param triple true if three keys for triple application
     */
    public void init(final byte[] key, final int offset, final boolean triple) {
        short[] userkey = new short[getKeysize() / 2]; //NOPMD short
        int i, k;                   //NOPMD names
        final int keys = triple ? 3 : 1;
        keyschedule = new Key[2 * keys];

        int index = offset;
        for (k = 0; k < keys; k++) {
            /* Assume each pair of bytes comprising a word is ordered MSB-first. */
            for (i = 0; i < (getKeysize() / 2); i++) {
                userkey[i] = (short) ((key[index] << 8) + (key[index + 1] & 0xFF)); //NOPMD short
                index += 2;
            }
            keyschedule[2 * k] = new Key(); //NOPMD need a bunch
            enKey(userkey, keyschedule[2 * k].key);
            keyschedule[1 + 2 * k] = new Key(); //NOPMD need a bunch
            deKey(keyschedule[2 * k], keyschedule[1 + 2 * k]);	/* compute inverse key schedule DK */
        }

        for (i = 0; i < (getKeysize() / 2); i++) /* Erase dangerous traces */ {
            userkey[i] = 0;
        }
    } /* initkey_idea */


    /**
     * Transform one block in ecb mode
     * @param encrypt true if forwards transformation
     * @param in input block
     * @param offin offset into block of input data
     * @param out output block
     * @param offout offset into block of output data
     */
    public void ecb(final boolean encrypt, final byte[] input, final int offin, //NOPMD names, complex
            final byte[] out, final int offout) {
        final boolean triple = keyschedule.length > 3;
        final int keys = (triple) ? 3 : 1;

        short[] sin = new short[getBlocksize() / 2]; //NOPMD short
        short[] sout = new short[getBlocksize() / 2]; //NOPMD short

        int sked = encrypt ? 0 : (triple ? 5 : 1);
        final int delta = encrypt ? 2 : -2;


        for (int iin = 0; iin < getBlocksize() / 2; ++iin) {
            sin[iin] = (short) ((input[offin + 2 * iin] << 8) + (0xFF & input[offin + 2 * iin + 1])); //NOPMD short
        }
        for (int i = 0; i < keys; ++i) {
            cipher(sin, sout, keyschedule[sked]);
            sked += delta;
            System.arraycopy(sout, 0, sin, 0, sin.length);
        }
        for (int iout = 0; iout < getBlocksize() / 2; ++iout) {
            out[offout + 2 * iout] = (byte) ((sout[iout] >>> 8) & 0xFF);
            out[offout + 2 * iout + 1] = (byte) (sout[iout] & 0xFF);
        }
    }

    /**
     * Wipe key schedule information
     */
    public void destroy() {
        if (keyschedule == null) {
            return;
        }
        for (int i = 0; i < keyschedule.length; ++i) {
            keyschedule[i].wipe();
        }
    }

    /**
     * Provide infomation of desired key size
     * @return byte length of key
     */
    public int getKeysize() {
        return 16;
    }

    /**
     * Provide infomation of algorithm block size
     * @return byte length of block
     */
    public int getBlocksize() {
        return 8;
    }
}
