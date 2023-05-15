/* -*-mode:java; c-basic-offset:2; -*- */
/*
Copyright (c) 2000,2001,2002,2003 ymnk, JCraft,Inc. All rights reserved.
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright 
notice, this list of conditions and the following disclaimer in 
the documentation and/or other materials provided with the distribution.
3. The names of the authors may not be used to endorse or promote products
derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JCRAFT,
INC. OR ANY CONTRIBUTORS TO THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/*
 * This program is based on zlib-1.1.3, so all credit should go authors
 * Jean-loup Gailly(jloup@gzip.org) and Mark Adler(madler@alumni.caltech.edu)
 * and contributors of zlib.
 */
package com.jcraft.jzlib;

final class InfCodes { //NOPMD complex

    static final private int[] INFLATE_MASK = {
        0x00000000, 0x00000001, 0x00000003, 0x00000007, 0x0000000f,
        0x0000001f, 0x0000003f, 0x0000007f, 0x000000ff, 0x000001ff,
        0x000003ff, 0x000007ff, 0x00000fff, 0x00001fff, 0x00003fff,
        0x00007fff, 0x0000ffff
    };

    // waiting for "i:"=input,
    //             "o:"=output,
    //             "x:"=nothing
    static final private int START = 0;  // x: set up for LEN
    static final private int LEN = 1;    // i: get length/literal/eob next
    static final private int LENEXT = 2; // i: getting length extra (have base)
    static final private int DIST = 3;   // i: get distance next
    static final private int DISTEXT = 4;// i: getting distance extra
    static final private int COPY = 5;   // o: copying bytes in window, waiting for space
    static final private int LIT = 6;    // o: got literal, waiting for output space
    static final private int WASH = 7;   // o: got eob, possibly still output waiting
    static final private int END = 8;    // x: got eob and all data flushed
    static final private int BADCODE = 9;// x: got error
    private int mode;      // current inflate_codes mode

    // mode dependent information
    private int len;
    private int[] tree; // pointer into tree
    private int treeIndex = 0;   //NOPMD
    private int need;   //NOPMD bits needed
    private int lit;

    // if EXT or COPY, where and how much
    private int get;              // bits to get for extra
    private int dist;             // distance back to copy from
    private byte lbits;           // ltree bits decoded per branch
    private byte dbits;           // dtree bits decoder per branch
    private int[] ltree;          // literal/length/eob tree
    private int ltreeIndex;      // literal/length/eob tree
    private int[] dtree;          // distance tree
    private int dtreeIndex;      // distance tree

    InfCodes() { //NOPMD empty
    }

    public void init(
            final int bl, final int bd, //NOPMD
            final int[] tl, final int tlIndex, //NOPMD - direct shallow copy
            final int[] td, final int tdIndex, final ZStream z) { //NOPMD - direct shallow copy
        mode = START;
        lbits = (byte) bl;
        dbits = (byte) bd;
        ltree = tl;
        ltreeIndex = tlIndex;
        dtree = td;
        dtreeIndex = tdIndex;
        tree = null;
    }

    public int proc(final InfBlocks seed, final ZStream zStr, int result) { //NOPMD long, complex
        int j;              //NOPMD temporary storage
        int tindex;         // temporary pointer
        int e;              //NOPMD extra bits or operation
        int b = 0;          //NOPMD bit buffer
        int k = 0;          //NOPMD bits in bit buffer
        int p = 0;          //NOPMD input data pointer
        int n;              //NOPMD bytes available there
        int q;              //NOPMD output window write pointer
        int m;              //NOPMD bytes to end of window or read pointer
        int f;              //NOPMD pointer to copy strings from

        // copy input/output information to locals (UPDATE macro restores)
        p = zStr.nextInIndex;
        n = zStr.availIn;
        b = seed.bitb;
        k = seed.bitk;
        q = seed.write;
        m = q < seed.read ? seed.read - q - 1 : seed.end - q;

        // process input and output based on current state
        while (true) {
            switch (mode) { //refactor this
                // waiting for "i:"=input, "o:"=output, "x:"=nothing
                case START:         // x: set up for LEN
                    if (m >= 258 && n >= 10) {

                        seed.bitb = b;
                        seed.bitk = k;
                        zStr.availIn = n;
                        zStr.totalIn += p - zStr.nextInIndex;
                        zStr.nextInIndex = p;
                        seed.write = q;
                        result = inflateFast(lbits, dbits,
                                ltree, ltreeIndex,
                                dtree, dtreeIndex,
                                seed, zStr);

                        p = zStr.nextInIndex;
                        n = zStr.availIn;
                        b = seed.bitb;
                        k = seed.bitk;
                        q = seed.write;
                        m = q < seed.read ? seed.read - q - 1 : seed.end - q;

                        if (result != JZlib.Z_OK) {
                            mode = result == JZlib.Z_STREAM_END ? WASH : BADCODE;
                            break;
                        }
                    }
                    need = lbits;
                    tree = ltree;
                    treeIndex = ltreeIndex;

                    mode = LEN;
                case LEN:           // i: get length/literal/eob next
                    j = need;

                    while (k < (j)) {
                        if (n != 0) { //NOPMD C-ism
                            result = JZlib.Z_OK;
                        } else {

                            seed.bitb = b;
                            seed.bitk = k;
                            zStr.availIn = n;
                            zStr.totalIn += p - zStr.nextInIndex;
                            zStr.nextInIndex = p;
                            seed.write = q;
                            return seed.inflateFlush(zStr, result);
                        }
                        n--;
                        b |= (zStr.nextIn[p++] & 0xff) << k;
                        k += 8;
                    }

                    tindex = (treeIndex + (b & INFLATE_MASK[j])) * 3;

                    b >>>= (tree[tindex + 1]);
                    k -= (tree[tindex + 1]);

                    e = tree[tindex];

                    if (e == 0) {               // literal
                        lit = tree[tindex + 2];
                        mode = LIT;
                        break;
                    }
                    if ((e & 16) != 0) {          // length
                        get = e & 15;
                        len = tree[tindex + 2];
                        mode = LENEXT;
                        break;
                    }
                    if ((e & 64) == 0) {        // next table
                        need = e;
                        treeIndex = tindex / 3 + tree[tindex + 2];
                        break;
                    }
                    if ((e & 32) != 0) {               // end of block
                        mode = WASH;
                        break;
                    }
                    mode = BADCODE;        // invalid code
                    zStr.msg = "invalid literal/length code";
                    result = JZlib.Z_DATA_ERROR;

                    seed.bitb = b;
                    seed.bitk = k;
                    zStr.availIn = n;
                    zStr.totalIn += p - zStr.nextInIndex;
                    zStr.nextInIndex = p;
                    seed.write = q;
                    return seed.inflateFlush(zStr, result);

                case LENEXT:        // i: getting length extra (have base)
                    j = get;

                    while (k < (j)) {
                        if (n != 0) { //NOPMD C-ism
                            result = JZlib.Z_OK;
                        } else {

                            seed.bitb = b;
                            seed.bitk = k;
                            zStr.availIn = n;
                            zStr.totalIn += p - zStr.nextInIndex;
                            zStr.nextInIndex = p;
                            seed.write = q;
                            return seed.inflateFlush(zStr, result);
                        }
                        n--;
                        b |= (zStr.nextIn[p++] & 0xff) << k;
                        k += 8;
                    }

                    len += (b & INFLATE_MASK[j]);

                    b >>= j;
                    k -= j;

                    need = dbits;
                    tree = dtree;
                    treeIndex = dtreeIndex;
                    mode = DIST;
                case DIST:          // i: get distance next
                    j = need;

                    while (k < (j)) {
                        if (n != 0) { //NOPMD C-ism
                            result = JZlib.Z_OK;
                        } else {

                            seed.bitb = b;
                            seed.bitk = k;
                            zStr.availIn = n;
                            zStr.totalIn += p - zStr.nextInIndex;
                            zStr.nextInIndex = p;
                            seed.write = q;
                            return seed.inflateFlush(zStr, result);
                        }
                        n--;
                        b |= (zStr.nextIn[p++] & 0xff) << k;
                        k += 8;
                    }

                    tindex = (treeIndex + (b & INFLATE_MASK[j])) * 3;

                    b >>= tree[tindex + 1];
                    k -= tree[tindex + 1];

                    e = (tree[tindex]);
                    if ((e & 16) != 0) {               // distance
                        get = e & 15;
                        dist = tree[tindex + 2];
                        mode = DISTEXT;
                        break;
                    }
                    if ((e & 64) == 0) {        // next table
                        need = e;
                        treeIndex = tindex / 3 + tree[tindex + 2];
                        break;
                    }
                    mode = BADCODE;        // invalid code
                    zStr.msg = "invalid distance code";
                    result = JZlib.Z_DATA_ERROR;

                    seed.bitb = b;
                    seed.bitk = k;
                    zStr.availIn = n;
                    zStr.totalIn += p - zStr.nextInIndex;
                    zStr.nextInIndex = p;
                    seed.write = q;
                    return seed.inflateFlush(zStr, result);

                case DISTEXT:       // i: getting distance extra
                    j = get;

                    while (k < (j)) {
                        if (n != 0) { //NOPMD C-ism
                            result = JZlib.Z_OK;
                        } else {

                            seed.bitb = b;
                            seed.bitk = k;
                            zStr.availIn = n;
                            zStr.totalIn += p - zStr.nextInIndex;
                            zStr.nextInIndex = p;
                            seed.write = q;
                            return seed.inflateFlush(zStr, result);
                        }
                        n--;
                        b |= (zStr.nextIn[p++] & 0xff) << k;
                        k += 8;
                    }

                    dist += (b & INFLATE_MASK[j]);

                    b >>= j;
                    k -= j;

                    mode = COPY;
                case COPY:          // o: copying bytes in window, waiting for space
                    f = q - dist;
                    while (f < 0) {     // modulo window size-"while" instead
                        f += seed.end;     // of "if" handles invalid distances
                    }
                    while (len != 0) {

                        if (m == 0) {
                            if (q == seed.end && seed.read != 0) {
                                q = 0;
                                m = q < seed.read ? seed.read - q - 1 : seed.end - q;
                            }
                            if (m == 0) {
                                seed.write = q;
                                result = seed.inflateFlush(zStr, result);
                                q = seed.write;
                                m = q < seed.read ? seed.read - q - 1 : seed.end - q;

                                if (q == seed.end && seed.read != 0) {
                                    q = 0;
                                    m = q < seed.read ? seed.read - q - 1 : seed.end - q;
                                }

                                if (m == 0) {
                                    seed.bitb = b;
                                    seed.bitk = k;
                                    zStr.availIn = n;
                                    zStr.totalIn += p - zStr.nextInIndex;
                                    zStr.nextInIndex = p;
                                    seed.write = q;
                                    return seed.inflateFlush(zStr, result);
                                }
                            }
                        }

                        seed.window[q++] = seed.window[f++];
                        m--;

                        if (f == seed.end) {
                            f = 0;
                        }
                        len--;
                    }
                    mode = START;
                    break;
                case LIT:           // o: got literal, waiting for output space
                    if (m == 0) {
                        if (q == seed.end && seed.read != 0) {
                            q = 0;
                            m = q < seed.read ? seed.read - q - 1 : seed.end - q;
                        }
                        if (m == 0) {
                            seed.write = q;
                            result = seed.inflateFlush(zStr, result);
                            q = seed.write;
                            m = q < seed.read ? seed.read - q - 1 : seed.end - q;

                            if (q == seed.end && seed.read != 0) {
                                q = 0;
                                m = q < seed.read ? seed.read - q - 1 : seed.end - q;
                            }
                            if (m == 0) {
                                seed.bitb = b;
                                seed.bitk = k;
                                zStr.availIn = n;
                                zStr.totalIn += p - zStr.nextInIndex;
                                zStr.nextInIndex = p;
                                seed.write = q;
                                return seed.inflateFlush(zStr, result);
                            }
                        }
                    }
                    result = JZlib.Z_OK;

                    seed.window[q++] = (byte) lit;
                    m--;

                    mode = START;
                    break;
                case WASH:           // o: got eob, possibly more output
                    if (k > 7) {        // return unused byte, if any
                        k -= 8;
                        n++;
                        p--;             // can always return one
                    }

                    seed.write = q;
                    result = seed.inflateFlush(zStr, result);
                    q = seed.write;
                    m = q < seed.read ? seed.read - q - 1 : seed.end - q;

                    if (seed.read != seed.write) {
                        seed.bitb = b;
                        seed.bitk = k;
                        zStr.availIn = n;
                        zStr.totalIn += p - zStr.nextInIndex;
                        zStr.nextInIndex = p;
                        seed.write = q;
                        return seed.inflateFlush(zStr, result);
                    }
                    mode = END;
                case END:
                    result = JZlib.Z_STREAM_END;
                    seed.bitb = b;
                    seed.bitk = k;
                    zStr.availIn = n;
                    zStr.totalIn += p - zStr.nextInIndex;
                    zStr.nextInIndex = p;
                    seed.write = q;
                    return seed.inflateFlush(zStr, result);

                case BADCODE:       // x: got error

                    result = JZlib.Z_DATA_ERROR;

                    seed.bitb = b;
                    seed.bitk = k;
                    zStr.availIn = n;
                    zStr.totalIn += p - zStr.nextInIndex;
                    zStr.nextInIndex = p;
                    seed.write = q;
                    return seed.inflateFlush(zStr, result);

                default:
                    result = JZlib.Z_STREAM_ERROR;

                    seed.bitb = b;
                    seed.bitk = k;
                    zStr.availIn = n;
                    zStr.totalIn += p - zStr.nextInIndex;
                    zStr.nextInIndex = p;
                    seed.write = q;
                    return seed.inflateFlush(zStr, result);
            }
        }
    }

    //public void free(ZStream zStr) {
    //  ZFREE(zStr, c);
    //}

    // Called with number of bytes left to write in window at least 258
    // (the maximum string length) and number of input bytes available
    // at least ten.  The ten bytes are six bytes for the longest length/
    // distance pair plus four bytes for overloading the bit buffer.
    private static int inflateFast(  //NOPMD long, complex
            final int bl, final int bd, //NOPMD
            final int[] tl, final int tl_index, //NOPMD
            final int[] td, final int td_index, //NOPMD
            final InfBlocks seed, final ZStream zStr) {
        int t;                //NOPMD temporary pointer
        int[] tp;             //NOPMD temporary pointer
        int tp_index;         // temporary pointer
        int e;                //NOPMD extra bits or operation
        int b;                //NOPMD bit buffer
        int k;                //NOPMD bits in bit buffer
        int p;                //NOPMD input data pointer
        int n;                //NOPMD bytes available there
        int q;                //NOPMD output window write pointer
        int m;                //NOPMD bytes to end of window or read pointer
        int ml;               //NOPMD mask for literal/length tree
        int md;               //NOPMD mask for distance tree
        int c;                //NOPMD bytes to copy
        int d;                //NOPMD distance back to copy from
        int r;                //NOPMD copy source pointer

        int tp_index_t_3;     // (tp_index+t)*3

        // load input, output, bit values
        p = zStr.nextInIndex;
        n = zStr.availIn;
        b = seed.bitb;
        k = seed.bitk;
        q = seed.write;
        m = q < seed.read ? seed.read - q - 1 : seed.end - q;

        // initialize masks
        ml = INFLATE_MASK[bl];
        md = INFLATE_MASK[bd];

        // do until not enough input or output space for fast loop
        do {                          // assume called with m >= 258 && n >= 10
            // get literal/length code
            while (k < (20)) {              // max bits for literal/length code
                n--;
                b |= (zStr.nextIn[p++] & 0xff) << k;
                k += 8;
            }

            t = b & ml;
            tp = tl;
            tp_index = tl_index;
            tp_index_t_3 = (tp_index + t) * 3;
            if ((e = tp[tp_index_t_3]) == 0) {  //NOPMD C-ism
                b >>= (tp[tp_index_t_3 + 1]);
                k -= (tp[tp_index_t_3 + 1]);

                seed.window[q++] = (byte) tp[tp_index_t_3 + 2];
                m--;
                continue;
            }
            do {

                b >>= (tp[tp_index_t_3 + 1]);
                k -= (tp[tp_index_t_3 + 1]);

                if ((e & 16) != 0) {
                    e &= 15;
                    c = tp[tp_index_t_3 + 2] + ((int) b & INFLATE_MASK[e]);

                    b >>= e;
                    k -= e;

                    // decode distance base of block to copy
                    while (k < (15)) {           // max bits for distance code
                        n--;
                        b |= (zStr.nextIn[p++] & 0xff) << k;
                        k += 8;
                    }

                    t = b & md;
                    tp = td;
                    tp_index = td_index;
                    tp_index_t_3 = (tp_index + t) * 3;
                    e = tp[tp_index_t_3];

                    do {

                        b >>= (tp[tp_index_t_3 + 1]);
                        k -= (tp[tp_index_t_3 + 1]);

                        if ((e & 16) != 0) {  //NOPMD C-ism
                            // get extra bits to add to distance base
                            e &= 15;
                            while (k < (e)) {         // get extra bits (up to 13)
                                n--;
                                b |= (zStr.nextIn[p++] & 0xff) << k;
                                k += 8;
                            }

                            d = tp[tp_index_t_3 + 2] + (b & INFLATE_MASK[e]);

                            b >>= (e);
                            k -= (e);

                            // do the copy
                            m -= c;
                            if (q >= d) {                // offset before dest
                                //  just copy
                                r = q - d;
                                if (q - r > 0 && 2 > (q - r)) {
                                    seed.window[q++] = seed.window[r++]; // minimum count is three,
                                    seed.window[q++] = seed.window[r++]; // so unroll loop a little
                                    c -= 2;
                                } else {
                                    System.arraycopy(seed.window, r, seed.window, q, 2);
                                    q += 2;
                                    r += 2;
                                    c -= 2;
                                }
                            } else {                  // else offset after destination
                                r = q - d;
                                do {
                                    r += seed.end;          // force pointer in window
                                } while (r < 0);         // covers invalid distances
                                e = seed.end - r;
                                if (c > e) {             // if source crosses,
                                    c -= e;              // wrapped copy
                                    if (q - r > 0 && e > (q - r)) {
                                        do {
                                            seed.window[q++] = seed.window[r++];
                                        } while (--e != 0);
                                    } else {
                                        System.arraycopy(seed.window, r, seed.window, q, e);
                                        q += e;
                                        r += e;
                                        e = 0;
                                    }
                                    r = 0;                  // copy rest from start of window
                                }

                            }

                            // copy all or what'seed left
                            if (q - r > 0 && c > (q - r)) {
                                do {
                                    seed.window[q++] = seed.window[r++];
                                } while (--c != 0);
                            } else {
                                System.arraycopy(seed.window, r, seed.window, q, c);
                                q += c;
                                r += c;
                                c = 0;
                            }
                            break;
                        } else if ((e & 64) == 0) {
                            t += tp[tp_index_t_3 + 2];
                            t += (b & INFLATE_MASK[e]);
                            tp_index_t_3 = (tp_index + t) * 3;
                            e = tp[tp_index_t_3];
                        } else {
                            zStr.msg = "invalid distance code";

                            c = zStr.availIn - n;
                            c = (k >> 3) < c ? k >> 3 : c;
                            n += c;
                            p -= c;
                            k -= c << 3;

                            seed.bitb = b;
                            seed.bitk = k;
                            zStr.availIn = n;
                            zStr.totalIn += p - zStr.nextInIndex;
                            zStr.nextInIndex = p;
                            seed.write = q;

                            return JZlib.Z_DATA_ERROR;
                        }
                    } while (true);
                    break;
                }

                if ((e & 64) == 0) {
                    t += tp[tp_index_t_3 + 2];
                    t += (b & INFLATE_MASK[e]);
                    tp_index_t_3 = (tp_index + t) * 3;
                    if ((e = tp[tp_index_t_3]) == 0) { //NOPMD C-ism

                        b >>= (tp[tp_index_t_3 + 1]);
                        k -= (tp[tp_index_t_3 + 1]);

                        seed.window[q++] = (byte) tp[tp_index_t_3 + 2];
                        m--;
                        break;
                    }
                } else if ((e & 32) != 0) {  //NOPMD C-ism

                    c = zStr.availIn - n;
                    c = (k >> 3) < c ? k >> 3 : c;
                    n += c;
                    p -= c;
                    k -= c << 3;

                    seed.bitb = b;
                    seed.bitk = k;
                    zStr.availIn = n;
                    zStr.totalIn += p - zStr.nextInIndex;
                    zStr.nextInIndex = p;
                    seed.write = q;

                    return JZlib.Z_STREAM_END;
                } else {
                    zStr.msg = "invalid literal/length code";

                    c = zStr.availIn - n;
                    c = (k >> 3) < c ? k >> 3 : c;
                    n += c;
                    p -= c;
                    k -= c << 3;

                    seed.bitb = b;
                    seed.bitk = k;
                    zStr.availIn = n;
                    zStr.totalIn += p - zStr.nextInIndex;
                    zStr.nextInIndex = p;
                    seed.write = q;

                    return JZlib.Z_DATA_ERROR;
                }
            } while (true);
        } while (m >= 258 && n >= 10);

        // not enough input or output--restore pointers and return
        c = zStr.availIn - n;
        c = (k >> 3) < c ? k >> 3 : c;
        n += c;
        p -= c;
        k -= c << 3;

        seed.bitb = b;
        seed.bitk = k;
        zStr.availIn = n;
        zStr.totalIn += p - zStr.nextInIndex;
        zStr.nextInIndex = p;
        seed.write = q;

        return JZlib.Z_OK;
    }
}
