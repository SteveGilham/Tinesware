package com.ravnaandtines.crypt.mda;

import java.util.*;

/**<PRE>
 * SHA1.java - An implementation of the SHA-1 Algorithm
 *
 * This version by Chuck McManis (cmcmanis@netcom.com) and
 * still public domain.
 * Tweaked by Mr.Tines<tines@windsong.demon.co.uk> for pegwit, June 1997
 * - added method 'frob()' which wipes the contents, so as to match
 * the bizarre behaviour of Pegwit's double barreled hashing.
 *
 * Based on the C code that Steve Reid wrote his header
 * was :
 *      SHA-1 in C
 *      By Steve Reid <steve@edmweb.com>
 *      100% Public Domain
 *
 *      Test Vectors (from FIPS PUB 180-1)
 *      "abc"
 *      A9993E36 4706816A BA3E2571 7850C26C 9CD0D89D
 *      "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq"
 *      84983E44 1C3BD26E BAAE4AA1 F95129E5 E54670F1
 *      A million repetitions of "a"
 *      34AA973C D4C4DAA4 F61EEB2B DBAD2731 6534016F
</pre>*/


public final class SHA1 implements MDA {
    private final SHABase worker;

    /**
     * simple constructor
     */
    public SHA1() {
        worker = new SHABase(true);
    }

    public void update(final byte[] data, final int offset, final int length) {
        worker.update(data, offset, length);
    }

    public void update(final byte data) {
        worker.update(data);
    }

    public byte[] digest() {
        return worker.digest();
    }
}
final class SHABase implements MDA {

    private int[] state = new int[5];
    private long count;
    // private boolean digestValid = false;
    private byte[] digestBits;
    private final boolean fixByNSA;

    /* *
     * retrieve the value of a hash
     * @param digest int[] into which to place 5 elements
     * @param offset index of first of the 5 elements
     * /
    public void extract(int[] digest, final int offset) {
        if (null == digest) {
            throw new IllegalArgumentException("digest");
        }
        for (int i = 0; i < 5; ++i) {
            digest[i + offset] =
                    ((digestBits[4 * i + 0] << 24) & 0xFF000000) |
                    ((digestBits[4 * i + 1] << 16) & 0x00FF0000) |
                    ((digestBits[4 * i + 2] << 8) & 0x0000FF00) |
                    ((digestBits[4 * i + 3]) & 0x000000FF);
        }
    }
     */

    /**
     * Variant constructor
     * @param isSHA1 true for SHA-1, false for the original SHA-0
     */
    public SHABase(final boolean isSHA1) {
        count = 0;
        //digestValid = false;
        fixByNSA = isSHA1;
        init();
    }

    /*
     * The following array forms the basis for the transform
     * buffer. Update puts bytes into this buffer and then
     * transform adds it into the state of the digest.
     */
    private int[] block = new int[16];
    private int blockIndex;

    /*
     * These functions are taken out of #defines in Steve's
     * code. Java doesn't have a preprocessor so the first
     * step is to just promote them to real methods.
     * Later we can optimize them out into inline code,
     * note that by making them final some compilers will
     * inline them when given the -O flag.
     */
    private static int rol(final int value, final int bits) {
        return (value << bits) | (value >>> (32 - bits));
    }

    private int blk0(final int index) {
        block[index] = (rol(block[index], 24) & 0xFF00FF00) |
                (rol(block[index], 8) & 0x00FF00FF);
        return block[index];
    }

    private int blk(final int index) {
        block[index & 15] = block[(index + 13) & 15] ^ block[(index + 8) & 15] ^
                block[(index + 2) & 15] ^ block[index & 15];
        if (fixByNSA) {   // this makes it SHA-1
            block[index & 15] = rol(block[index & 15], 1);
        }
        return block[index & 15];
    }

    private void methodR0(final int[] data, 
            final int indexV, 
            final int indexW, 
            final int indexX, 
            final int indexY, 
            final int indexZ, 
            final int index) {
        data[indexZ] += ((data[indexW] & (data[indexX] ^ data[indexY])) ^ data[indexY]) +
                blk0(index) + 0x5A827999 + rol(data[indexV], 5);
        data[indexW] = rol(data[indexW], 30);
    }

    private void methodR1(final int[] data, final int v, final int w, //NOPMD
            final int x, final int y, final int z, final int i) { //NOPMD
        data[z] += ((data[w] & (data[x] ^ data[y])) ^ data[y]) +
                blk(i) + 0x5A827999 + rol(data[v], 5);
        data[w] = rol(data[w], 30);
    }

    private void methodR2(final int[] data, final int v, final int w, //NOPMD
            final int x, final int y, final int z, final int i) { //NOPMD
        data[z] += (data[w] ^ data[x] ^ data[y]) +
                blk(i) + 0x6ED9EBA1 + rol(data[v], 5);
        data[w] = rol(data[w], 30);
    }

    private void methodR3(final int[] data, final int v, final int w, //NOPMD
            final int x, final int y, final int z, final int i) { //NOPMD
        data[z] += (((data[w] | data[x]) & data[y]) | (data[w] & data[x])) +
                blk(i) + 0x8F1BBCDC + rol(data[v], 5);
        data[w] = rol(data[w], 30);
    }

    private void methodR4(final int[] data, final int v, final int w, //NOPMD
            final int x, final int y, final int z, final int i) { //NOPMD
        data[z] += (data[w] ^ data[x] ^ data[y]) +
                blk(i) + 0xCA62C1D6 + rol(data[v], 5);
        data[w] = rol(data[w], 30);
    }
    /*
     * Steve's original code and comments :
     *
     * blk0() and blk() perform the initial expand.
     * I got the idea of expanding during the round function from SSLeay
     *
     * #define blk0(index) block->l[index]
     * #define blk(index) (block->l[index&15] = rol(block->l[(index+13)&15]^block->l[(index+8)&15] \
     *   ^block->l[(index+2)&15]^block->l[index&15],1))
     *
     * (methodR0+methodR1), methodR2, methodR3, methodR4 are the different operations used in SHA1
     * #define methodR0(indexV,indexW,indexX,indexY,indexZ,index) indexZ+=((indexW&(indexX^indexY))^indexY)+blk0(index)+0x5A827999+rol(indexV,5);indexW=rol(indexW,30);
     * #define methodR1(indexV,indexW,indexX,indexY,indexZ,index) indexZ+=((indexW&(indexX^indexY))^indexY)+blk(index)+0x5A827999+rol(indexV,5);indexW=rol(indexW,30);
     * #define methodR2(indexV,indexW,indexX,indexY,indexZ,index) indexZ+=(indexW^indexX^indexY)+blk(index)+0x6ED9EBA1+rol(indexV,5);indexW=rol(indexW,30);
     * #define methodR3(indexV,indexW,indexX,indexY,indexZ,index) indexZ+=(((indexW|indexX)&indexY)|(indexW&indexX))+blk(index)+0x8F1BBCDC+rol(indexV,5);indexW=rol(indexW,30);
     * #define methodR4(indexV,indexW,indexX,indexY,indexZ,index) indexZ+=(indexW^indexX^indexY)+blk(index)+0xCA62C1D6+rol(indexV,5);indexW=rol(indexW,30);
     */

    /**
     * Hash a single 512-bit block. This is the core of the algorithm.
     *
     * Note that working with arrays is very inefficent in Java as it
     * does a class cast check each time you store into the array.
     *
     */
    private void transform() {
        
        final int[] dataState = new int[5];
        /* Copy context->state[] to working vars */
        System.arraycopy(state, 0, dataState, 0, dataState.length);

        /* 4 rounds of 20 operations each. Loop unrolled. */
        methodR0(dataState, 0, 1, 2, 3, 4, 0);
        methodR0(dataState, 4, 0, 1, 2, 3, 1);
        methodR0(dataState, 3, 4, 0, 1, 2, 2);
        methodR0(dataState, 2, 3, 4, 0, 1, 3);
        methodR0(dataState, 1, 2, 3, 4, 0, 4);
        methodR0(dataState, 0, 1, 2, 3, 4, 5);
        methodR0(dataState, 4, 0, 1, 2, 3, 6);
        methodR0(dataState, 3, 4, 0, 1, 2, 7);
        methodR0(dataState, 2, 3, 4, 0, 1, 8);
        methodR0(dataState, 1, 2, 3, 4, 0, 9);
        methodR0(dataState, 0, 1, 2, 3, 4, 10);
        methodR0(dataState, 4, 0, 1, 2, 3, 11);
        methodR0(dataState, 3, 4, 0, 1, 2, 12);
        methodR0(dataState, 2, 3, 4, 0, 1, 13);
        methodR0(dataState, 1, 2, 3, 4, 0, 14);
        methodR0(dataState, 0, 1, 2, 3, 4, 15);
        methodR1(dataState, 4, 0, 1, 2, 3, 16);
        methodR1(dataState, 3, 4, 0, 1, 2, 17);
        methodR1(dataState, 2, 3, 4, 0, 1, 18);
        methodR1(dataState, 1, 2, 3, 4, 0, 19);
        methodR2(dataState, 0, 1, 2, 3, 4, 20);
        methodR2(dataState, 4, 0, 1, 2, 3, 21);
        methodR2(dataState, 3, 4, 0, 1, 2, 22);
        methodR2(dataState, 2, 3, 4, 0, 1, 23);
        methodR2(dataState, 1, 2, 3, 4, 0, 24);
        methodR2(dataState, 0, 1, 2, 3, 4, 25);
        methodR2(dataState, 4, 0, 1, 2, 3, 26);
        methodR2(dataState, 3, 4, 0, 1, 2, 27);
        methodR2(dataState, 2, 3, 4, 0, 1, 28);
        methodR2(dataState, 1, 2, 3, 4, 0, 29);
        methodR2(dataState, 0, 1, 2, 3, 4, 30);
        methodR2(dataState, 4, 0, 1, 2, 3, 31);
        methodR2(dataState, 3, 4, 0, 1, 2, 32);
        methodR2(dataState, 2, 3, 4, 0, 1, 33);
        methodR2(dataState, 1, 2, 3, 4, 0, 34);
        methodR2(dataState, 0, 1, 2, 3, 4, 35);
        methodR2(dataState, 4, 0, 1, 2, 3, 36);
        methodR2(dataState, 3, 4, 0, 1, 2, 37);
        methodR2(dataState, 2, 3, 4, 0, 1, 38);
        methodR2(dataState, 1, 2, 3, 4, 0, 39);
        methodR3(dataState, 0, 1, 2, 3, 4, 40);
        methodR3(dataState, 4, 0, 1, 2, 3, 41);
        methodR3(dataState, 3, 4, 0, 1, 2, 42);
        methodR3(dataState, 2, 3, 4, 0, 1, 43);
        methodR3(dataState, 1, 2, 3, 4, 0, 44);
        methodR3(dataState, 0, 1, 2, 3, 4, 45);
        methodR3(dataState, 4, 0, 1, 2, 3, 46);
        methodR3(dataState, 3, 4, 0, 1, 2, 47);
        methodR3(dataState, 2, 3, 4, 0, 1, 48);
        methodR3(dataState, 1, 2, 3, 4, 0, 49);
        methodR3(dataState, 0, 1, 2, 3, 4, 50);
        methodR3(dataState, 4, 0, 1, 2, 3, 51);
        methodR3(dataState, 3, 4, 0, 1, 2, 52);
        methodR3(dataState, 2, 3, 4, 0, 1, 53);
        methodR3(dataState, 1, 2, 3, 4, 0, 54);
        methodR3(dataState, 0, 1, 2, 3, 4, 55);
        methodR3(dataState, 4, 0, 1, 2, 3, 56);
        methodR3(dataState, 3, 4, 0, 1, 2, 57);
        methodR3(dataState, 2, 3, 4, 0, 1, 58);
        methodR3(dataState, 1, 2, 3, 4, 0, 59);
        methodR4(dataState, 0, 1, 2, 3, 4, 60);
        methodR4(dataState, 4, 0, 1, 2, 3, 61);
        methodR4(dataState, 3, 4, 0, 1, 2, 62);
        methodR4(dataState, 2, 3, 4, 0, 1, 63);
        methodR4(dataState, 1, 2, 3, 4, 0, 64);
        methodR4(dataState, 0, 1, 2, 3, 4, 65);
        methodR4(dataState, 4, 0, 1, 2, 3, 66);
        methodR4(dataState, 3, 4, 0, 1, 2, 67);
        methodR4(dataState, 2, 3, 4, 0, 1, 68);
        methodR4(dataState, 1, 2, 3, 4, 0, 69);
        methodR4(dataState, 0, 1, 2, 3, 4, 70);
        methodR4(dataState, 4, 0, 1, 2, 3, 71);
        methodR4(dataState, 3, 4, 0, 1, 2, 72);
        methodR4(dataState, 2, 3, 4, 0, 1, 73);
        methodR4(dataState, 1, 2, 3, 4, 0, 74);
        methodR4(dataState, 0, 1, 2, 3, 4, 75);
        methodR4(dataState, 4, 0, 1, 2, 3, 76);
        methodR4(dataState, 3, 4, 0, 1, 2, 77);
        methodR4(dataState, 2, 3, 4, 0, 1, 78);
        methodR4(dataState, 1, 2, 3, 4, 0, 79);
        /* Add the working vars back into context.state[] */
        state[0] += dataState[0];
        state[1] += dataState[1];
        state[2] += dataState[2];
        state[3] += dataState[3];
        state[4] += dataState[4];
    }

    /* *
     * zero the count and state arrays; used to support
     * Pegwit's anomalous 2-barrel hsahing
     * /
    public void frob() // Pegwit's little anomaly
    {
        count = 0;
        state[0] = state[1] = state[2] = state[3] = state[4] = 0;
    }
     */

    /**
     *
     * Initializes new context
     */
    private void init() {
        /* SHA1 initialization constants */
        state[0] = 0x67452301;
        state[1] = 0xEFCDAB89;
        state[2] = 0x98BADCFE;
        state[3] = 0x10325476;
        state[4] = 0xC3D2E1F0;
        count = 0;
        digestBits = new byte[20];
        //digestValid = false;
        blockIndex = 0;
    }

    /**
     * Add one byte to the digest. When this is implemented
     * all of the abstract class methods end up calling
     * this method for types other than bytes.
     * @param isSHA1 byte to add
     */
    public void update(final byte data) {
        final int mask = (8 * (blockIndex & 3));

        count += 8;
        block[blockIndex >> 2] &= ~(0xff << mask);
        block[blockIndex >> 2] |= (data & 0xff) << mask;
        blockIndex++;
        if (blockIndex == 64) {
            transform();
            blockIndex = 0;
        }
    }

    /**
     * Add many bytes to the digest.
     * @param data byte data to add
     * @param offset start byte
     * @param length number of bytes to hash
     */
    public void update(final byte[] data, final int offset, final int length) {
        if (null == data) {
            return;
        }
        for (int i = 0; i < length; ++i) {
            update(data[offset + i]);
        }
    }

    /**
     * Return completed digest.
     * @return the byte array result
     */
    public byte[] digest() {
        finish();
        final byte[] out = new byte[20];
        System.arraycopy(digestBits, 0, out, 0, digestBits.length);
        init();
        return out;
    }

    /**
     * Complete processing on the message digest.
     */
    private void finish() {
        byte[] bits = new byte[8]; // NO-PMD DU/DD

        for (int i = 0; i < 8; i++) {
            bits[i] = (byte) ((count >>> (((7 - i) * 8))) & 0xff); // NO-PMD DU/DD
        }

        update((byte) 128);
        while (blockIndex != 56) {
            update((byte) 0);
        }
        // This should cause a transform to happen.
        for (int i = 0; i < 8; ++i) {
            update(bits[i]);
        }
        for (int i = 0; i < 20; i++) {
            digestBits[i] = (byte) ((state[i >> 2] >>> ((3 - (i & 3)) * 8)) & 0xff);
        }
    //digestValid = true;
    }
}
