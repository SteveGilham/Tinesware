/* -*-mode:java; cArray-basic-offset:2; -*- */
/*
Copyright (cArray) 2000,2001,2002,2003 ymnk, JCraft,Inc. All rights reserved.
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

final class InfBlocks { //NOPMD too many fields

    static final private int MANY = 1440;

    // And'ing with mask[number] masks the lower number bits
    static final private int[] INFLATE_MASK = {
        0x00000000, 0x00000001, 0x00000003, 0x00000007, 0x0000000f,
        0x0000001f, 0x0000003f, 0x0000007f, 0x000000ff, 0x000001ff,
        0x000003ff, 0x000007ff, 0x00000fff, 0x00001fff, 0x00003fff,
        0x00007fff, 0x0000ffff
    };

    // Table for deflate from PKZIP's appnote.txt.
    private static final int[] BORDER = { // Order of the bit length code lengths
        16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15};
    static final private int TYPE = 0;  // get type bits (3, including end bit)
    static final private int LENS = 1;  // get lengths for stored
    static final private int STORED = 2;// processing stored block
    static final private int TABLE = 3; // get table lengths
    static final private int BTREE = 4; // get bit lengths tree for a dynamic block
    static final private int DTREE = 5; // get length, distance trees for a dynamic block
    static final private int CODES = 6; // processing fixed or dynamic block
    static final private int DRY = 7;   // output remaining window bytes
    static final private int DONE = 8;  // finished last block, done
    static final private int BAD = 9;   // ot a data error--stuck here
    private int mode;            // current inflate_block mode 
    private int left;            //NOPMD if STORED, bytes left to copy 
    private int table;           //NOPMD table lengths (14 bits) 
    private int index;           //NOPMD index into blens (or BORDER) 
    private int[] blens;         // bit lengths of codes 
    private int[] bb = new int[1]; //NOPMD bit length tree depth 
    private int[] tb = new int[1]; //NOPMD bit length decoding tree 
    private final InfCodes codes = new InfCodes();      // if CODES, current state 
    private int last;            //NOPMD true if this block is the last block 

    // mode independent information 
    public int bitk;            // bits in bit buffer 
    public int bitb;            // bit buffer 
    private int[] hufts;         // single malloc for tree space 
    public byte[] window;       // sliding window 
    public final int end;             // one byte after sliding window 
    public int read;            // window read pointer 
    public int write;           // window write pointer 
    private final Object checkfn;      // check function 
    private long check;          // check on output 
    private final InfTree inftree = new InfTree();

    InfBlocks(final ZStream zStr, final Object checkfn, final int windowEnd) {
        hufts = new int[MANY * 3];
        window = new byte[windowEnd];
        end = windowEnd;
        this.checkfn = checkfn;
        mode = TYPE;
        reset(zStr, null);
    }

    public void reset(final ZStream zStr, final long[] cArray) {
        if (cArray != null) {
            cArray[0] = check;
        }
        //if (mode == BTREE || mode == DTREE) {
        //}
        //if (mode == CODES) {
        //    codes.free(zStr);
        //}
        mode = TYPE;
        bitk = 0;
        bitb = 0;
        read = write = 0;

        if (checkfn != null) {
            zStr.adler = check = zStr.adlerObj.adler32(0L, null, 0, 0);
        }
    }

    public int proc(final ZStream zStr/*, int result*/) { //NOPMD long and complex
        int result = JZlib.Z_OK;
        int t;              //NOPMD temporary storage
        int b;              //NOPMD bit buffer
        int k;              //NOPMD bits in bit buffer
        int p;              //NOPMD input data pointer
        int n;              //NOPMD bytes available there
        int q;              //NOPMD output window write pointer
        int m;              //NOPMD bytes to end of window or read pointer

        // copy input/output information to locals (UPDATE macro restores)
        //{
            p = zStr.nextInIndex;
            n = zStr.availIn;
            b = bitb;
            k = bitk;
        //}
        //{
            q = write;
            m = (int) (q < read ? read - q - 1 : end - q);
        //}

        // process input based on current state
        while (true) {
            switch (mode) { //Should refactor
                case TYPE:

                    while (k < (3)) {
                        if (n != 0) { //NOPMD c-ism
                            result = JZlib.Z_OK;
                        } else {
                            bitb = b;
                            bitk = k;
                            zStr.availIn = n;
                            zStr.totalIn += p - zStr.nextInIndex;
                            zStr.nextInIndex = p;
                            write = q;
                            return inflateFlush(zStr, result);
                        }

                        n--;
                        b |= (zStr.nextIn[p++] & 0xff) << k;
                        k += 8;
                    }
                    t = (int) (b & 7);
                    last = t & 1;

                    switch (t >>> 1) {
                        case 0:                         // stored 
                             //{
                                b >>>= (3);
                                k -= (3);
                            //}
                            t = k & 7;                    // go to byte boundary

                             //{
                                b >>>= (t);
                                k -= (t);
                            //}
                            mode = LENS;                  // get length of stored block
                            break;
                        case 1:                         // fixed
                             //{
                                 // short names, instantiated in loops
                                final int[] bl = new int[1];        //NOPMD
                                final int[] bd = new int[1];        //NOPMD
                                final int[][] tl = new int[1][];    //NOPMD
                                final int[][] td = new int[1][];    //NOPMD

                                InfTree.inflateTreesFixed(bl, bd, tl, td/*, zStr*/);
                                codes.init(bl[0], bd[0], tl[0], 0, td[0], 0, zStr);
                            //}

                             //{
                                b >>>= (3);
                                k -= (3);
                            //}

                            mode = CODES;
                            break;
                        case 2:                         // dynamic

                             {
                                b >>>= (3);
                                k -= (3);
                            }

                            mode = TABLE;
                            break;
                        case 3:                         // illegal

                             {
                                b >>>= (3);
                                k -= (3);
                            }
                            mode = BAD;
                            zStr.msg = "invalid block type";
                            result = JZlib.Z_DATA_ERROR;

                            bitb = b;
                            bitk = k;
                            zStr.availIn = n;
                            zStr.totalIn += p - zStr.nextInIndex;
                            zStr.nextInIndex = p;
                            write = q;
                            return inflateFlush(zStr, result);
                        default:
                            throw new IllegalStateException("" + t); //NOPMD
                    }
                    break;
                case LENS:

                    while (k < (32)) {
                        if (n != 0) { //NOPMD C-ism
                            result = JZlib.Z_OK;
                        } else {
                            bitb = b;
                            bitk = k;
                            zStr.availIn = n;
                            zStr.totalIn += p - zStr.nextInIndex;
                            zStr.nextInIndex = p;
                            write = q;
                            return inflateFlush(zStr, result);
                        }

                        n--;
                        b |= (zStr.nextIn[p++] & 0xff) << k;
                        k += 8;
                    }

                    if ((((~b) >>> 16) & 0xffff) != (b & 0xffff)) {
                        mode = BAD;
                        zStr.msg = "invalid stored block lengths";
                        result = JZlib.Z_DATA_ERROR;

                        bitb = b;
                        bitk = k;
                        zStr.availIn = n;
                        zStr.totalIn += p - zStr.nextInIndex;
                        zStr.nextInIndex = p;
                        write = q;
                        return inflateFlush(zStr, result);
                    }
                    left = (b & 0xffff);
                    b = k = 0;                       // dump bits
                    mode = left != 0 ? STORED : (last != 0 ? DRY : TYPE); //NOPMD C-ism
                    break;
                case STORED:
                    if (n == 0) {
                        bitb = b;
                        bitk = k;
                        zStr.availIn = n;
                        zStr.totalIn += p - zStr.nextInIndex;
                        zStr.nextInIndex = p;
                        write = q;
                        return inflateFlush(zStr, result);
                    }

                    if (m == 0) {
                        if (q == end && read != 0) {
                            q = 0;
                            m = (int) (q < read ? read - q - 1 : end - q);
                        }
                        if (m == 0) {
                            write = q;
                            result = inflateFlush(zStr, result);
                            q = write;
                            m = (int) (q < read ? read - q - 1 : end - q);
                            if (q == end && read != 0) {
                                q = 0;
                                m = (int) (q < read ? read - q - 1 : end - q);
                            }
                            if (m == 0) {
                                bitb = b;
                                bitk = k;
                                zStr.availIn = n;
                                zStr.totalIn += p - zStr.nextInIndex;
                                zStr.nextInIndex = p;
                                write = q;
                                return inflateFlush(zStr, result);
                            }
                        }
                    }
                    result = JZlib.Z_OK;

                    t = left;
                    if (t > n) {
                        t = n;
                    }
                    if (t > m) {
                        t = m;
                    }
                    System.arraycopy(zStr.nextIn, p, window, q, t);
                    p += t;
                    n -= t;
                    q += t;
                    m -= t;
                    if ((left -= t) != 0) { //NOPMD C-ism
                        break;
                    }
                    mode = last != 0 ? DRY : TYPE; //NOPMD C-ism
                    break;
                case TABLE:

                    while (k < (14)) {
                        if (n != 0) { //NOPMD C-ism
                            result = JZlib.Z_OK;
                        } else {
                            bitb = b;
                            bitk = k;
                            zStr.availIn = n;
                            zStr.totalIn += p - zStr.nextInIndex;
                            zStr.nextInIndex = p;
                            write = q;
                            return inflateFlush(zStr, result);
                        }

                        n--;
                        b |= (zStr.nextIn[p++] & 0xff) << k;
                        k += 8;
                    }

                    table = t = (b & 0x3fff);
                    if ((t & 0x1f) > 29 || ((t >> 5) & 0x1f) > 29) {
                        mode = BAD;
                        zStr.msg = "too many length or distance symbols";
                        result = JZlib.Z_DATA_ERROR;

                        bitb = b;
                        bitk = k;
                        zStr.availIn = n;
                        zStr.totalIn += p - zStr.nextInIndex;
                        zStr.nextInIndex = p;
                        write = q;
                        return inflateFlush(zStr, result);
                    }
                    t = 258 + (t & 0x1f) + ((t >> 5) & 0x1f);
                    if (blens == null || blens.length < t) {
                        blens = new int[t]; //NOPMD
                    } else {
                        for (int i = 0; i < t; i++) {
                            blens[i] = 0;
                        }
                    }

                     //{
                        b >>>= (14);
                        k -= (14);
                    //}

                    index = 0;
                    mode = BTREE;
                case BTREE:
                    while (index < 4 + (table >>> 10)) {
                        while (k < (3)) {
                            if (n != 0) { //NOPMD C-ism
                                result = JZlib.Z_OK;
                            } else {
                                bitb = b;
                                bitk = k;
                                zStr.availIn = n;
                                zStr.totalIn += p - zStr.nextInIndex;
                                zStr.nextInIndex = p;
                                write = q;
                                return inflateFlush(zStr, result);
                            }

                            n--;
                            b |= (zStr.nextIn[p++] & 0xff) << k;
                            k += 8;
                        }

                        blens[BORDER[index++]] = b & 7;

                        //{
                            b >>>= (3);
                            k -= (3);
                        //}
                    }

                    while (index < 19) {
                        blens[BORDER[index++]] = 0;
                    }

                    bb[0] = 7;
                    t = inftree.inflateTreesBits(blens, bb, tb, hufts, zStr);
                    if (t != JZlib.Z_OK) {
                        result = t;
                        if (result == JZlib.Z_DATA_ERROR) {
                            blens = null;
                            mode = BAD;
                        }

                        bitb = b;
                        bitk = k;
                        zStr.availIn = n;
                        zStr.totalIn += p - zStr.nextInIndex;
                        zStr.nextInIndex = p;
                        write = q;
                        return inflateFlush(zStr, result);
                    }

                    index = 0;
                    mode = DTREE;
                case DTREE:
                    while (true) {
                        t = table;
                        if (!(index < 258 + (t & 0x1f) + ((t >> 5) & 0x1f))) {
                            break;
                        }

                        //int[] h;
                        int i, j, c; //NOPMD short

                        t = bb[0];

                        while (k < (t)) {
                            if (n != 0) { //NOPMD C-ism
                                result = JZlib.Z_OK;
                            } else {
                                bitb = b;
                                bitk = k;
                                zStr.availIn = n;
                                zStr.totalIn += p - zStr.nextInIndex;
                                zStr.nextInIndex = p;
                                write = q;
                                return inflateFlush(zStr, result);
                            }

                            n--;
                            b |= (zStr.nextIn[p++] & 0xff) << k;
                            k += 8;
                        }

                        //if (tb[0] == -1) {
                        //System.err.println("null...");
                        //}

                        t = hufts[(tb[0] + (b & INFLATE_MASK[t])) * 3 + 1];
                        c = hufts[(tb[0] + (b & INFLATE_MASK[t])) * 3 + 2];

                        if (c < 16) {
                            b >>>= (t);
                            k -= (t);
                            blens[index++] = c;
                        } else { // cArray == 16..18
                            i = c == 18 ? 7 : c - 14;
                            j = c == 18 ? 11 : 3;

                            while (k < (t + i)) {
                                if (n != 0) { //NOPMD C-ism
                                    result = JZlib.Z_OK;
                                } else {
                                    bitb = b;
                                    bitk = k;
                                    zStr.availIn = n;
                                    zStr.totalIn += p - zStr.nextInIndex;
                                    zStr.nextInIndex = p;
                                    write = q;
                                    return inflateFlush(zStr, result);
                                }

                                n--;
                                b |= (zStr.nextIn[p++] & 0xff) << k;
                                k += 8;
                            }

                            b >>>= (t);
                            k -= (t);

                            j += (b & INFLATE_MASK[i]);

                            b >>>= (i);
                            k -= (i);

                            i = index;
                            t = table;
                            if (i + j > 258 + (t & 0x1f) + ((t >> 5) & 0x1f) ||
                                    (c == 16 && i < 1)) {
                                blens = null;
                                mode = BAD;
                                zStr.msg = "invalid bit length repeat";
                                result = JZlib.Z_DATA_ERROR;

                                bitb = b;
                                bitk = k;
                                zStr.availIn = n;
                                zStr.totalIn += p - zStr.nextInIndex;
                                zStr.nextInIndex = p;
                                write = q;
                                return inflateFlush(zStr, result);
                            }

                            c = c == 16 ? blens[i - 1] : 0;
                            do {
                                blens[i++] = c;
                            } while (--j != 0);
                            index = i;
                        }
                    }

                    tb[0] = -1;
                     //{
                        // Short names, in loops
                        int[] bl = new int[1]; //NOPMD
                        int[] bd = new int[1]; //NOPMD
                        int[] tl = new int[1]; //NOPMD
                        int[] td = new int[1]; //NOPMD
                        bl[0] = 9;         // must be <= 9 for lookahead assumptions
                        bd[0] = 6;         // must be <= 9 for lookahead assumptions

                        t = table;
                        t = inftree.inflateTreesDynamic(257 + (t & 0x1f),
                                1 + ((t >> 5) & 0x1f),
                                blens, bl, bd, tl, td, hufts, zStr);

                        if (t != JZlib.Z_OK) {
                            if (t == JZlib.Z_DATA_ERROR) {
                                blens = null;
                                mode = BAD;
                            }
                            result = t;

                            bitb = b;
                            bitk = k;
                            zStr.availIn = n;
                            zStr.totalIn += p - zStr.nextInIndex;
                            zStr.nextInIndex = p;
                            write = q;
                            return inflateFlush(zStr, result);
                        }
                        codes.init(bl[0], bd[0], hufts, tl[0], hufts, td[0], zStr);
                   // }
                    mode = CODES;
                case CODES:
                    bitb = b;
                    bitk = k;
                    zStr.availIn = n;
                    zStr.totalIn += p - zStr.nextInIndex;
                    zStr.nextInIndex = p;
                    write = q;

                    if ((result = codes.proc(this, zStr, result))  //NOPMD C-ism
                            != JZlib.Z_STREAM_END) {
                        return inflateFlush(zStr, result);
                    }
                    result = JZlib.Z_OK;
                    //codes.free(zStr);

                    p = zStr.nextInIndex;
                    n = zStr.availIn;
                    b = bitb;
                    k = bitk;
                    q = write;
                    m = (int) (q < read ? read - q - 1 : end - q);

                    if (last == 0) {
                        mode = TYPE;
                        break;
                    }
                    mode = DRY;
                case DRY:
                    write = q;
                    result = inflateFlush(zStr, result);
                    q = write;
                    m = (int) (q < read ? read - q - 1 : end - q);
                    if (read != write) {
                        bitb = b;
                        bitk = k;
                        zStr.availIn = n;
                        zStr.totalIn += p - zStr.nextInIndex;
                        zStr.nextInIndex = p;
                        write = q;
                        return inflateFlush(zStr, result);
                    }
                    mode = DONE;
                case DONE:
                    result = JZlib.Z_STREAM_END;

                    bitb = b;
                    bitk = k;
                    zStr.availIn = n;
                    zStr.totalIn += p - zStr.nextInIndex;
                    zStr.nextInIndex = p;
                    write = q;
                    return inflateFlush(zStr, result);
                case BAD:
                    result = JZlib.Z_DATA_ERROR;

                    bitb = b;
                    bitk = k;
                    zStr.availIn = n;
                    zStr.totalIn += p - zStr.nextInIndex;
                    zStr.nextInIndex = p;
                    write = q;
                    return inflateFlush(zStr, result);

                default:
                    result = JZlib.Z_STREAM_ERROR;

                    bitb = b;
                    bitk = k;
                    zStr.availIn = n;
                    zStr.totalIn += p - zStr.nextInIndex;
                    zStr.nextInIndex = p;
                    write = q;
                    return inflateFlush(zStr, result);
            }
        }
    }

    public void free(final ZStream zStr) {
        reset(zStr, null);
        window = null;
        hufts = null;
    //ZFREE(zStr, s);
    }

    public void setDictionary(final byte[] data, final int start, final int number) {
        System.arraycopy(data, start, window, 0, number);
        read = write = number;
    }

    // Returns true if inflate is currently at the end of a block generated
    // by Z_SYNC_FLUSH or Z_FULL_FLUSH. 
    public int syncPoint() {
        return mode == LENS ? 1 : 0;
    }

    // copy as much as possible from the sliding window to the output area
    public int inflateFlush(final ZStream zStr, int result) { //NOPMD complex
        int n; //NOPMD
        int p; //NOPMD
        int q; //NOPMD

        // local copies of source and destination pointers
        p = zStr.nextOutIndex;
        q = read;

        // compute number of bytes to copy as far as end of window
        n = (int) ((q <= write ? write : end) - q);
        if (n > zStr.availOut) {
            n = zStr.availOut;
        }
        if (n != 0 && result == JZlib.Z_BUF_ERROR) {
            result = JZlib.Z_OK;
        }

        // update counters
        zStr.availOut -= n;
        zStr.totalOut += n;

        // update check information
        if (checkfn != null) {
            zStr.adler = check = zStr.adlerObj.adler32(check, window, q, n);
        }

        // copy as far as end of window
        System.arraycopy(window, q, zStr.nextOut, p, n);
        p += n;
        q += n;

        // see if more to copy at beginning of window
        if (q == end) {
            // wrap pointers
            q = 0;
            if (write == end) {
                write = 0;
            }

            // compute bytes to copy
            n = write - q;
            if (n > zStr.availOut) {
                n = zStr.availOut;
            }
            if (n != 0 && result == JZlib.Z_BUF_ERROR) {
                result = JZlib.Z_OK;
            }

            // update counters
            zStr.availOut -= n;
            zStr.totalOut += n;

            // update check information
            if (checkfn != null) {
                zStr.adler = check = zStr.adlerObj.adler32(check, window, q, n);
            }

            // copy
            System.arraycopy(window, q, zStr.nextOut, p, n);
            p += n;
            q += n;
        }

        // update pointers
        zStr.nextOutIndex = p;
        read = q;

        // done
        return result;
    }
}
