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

final class Inflate { // NOPMD complexity

    //static final private int MAX_WBITS = 15; // 32K LZ77 window

    // preset dictionary flag in zlib header
    static final private int PRESET_DICT = 0x20;

    static final private int METHOD = 0;   // waiting for method byte
    static final private int FLAG = 1;     // waiting for flag byte
    static final private int DICT4 = 2;    // four dictionary check bytes to go
    static final private int DICT3 = 3;    // three dictionary check bytes to go
    static final private int DICT2 = 4;    // two dictionary check bytes to go
    static final private int DICT1 = 5;    // one dictionary check byte to go
    static final private int DICT0 = 6;    // waiting for inflateSetDictionary
    static final private int BLOCKS = 7;   // decompressing blocks
    static final private int CHECK4 = 8;   // four check bytes to go
    static final private int CHECK3 = 9;   // three check bytes to go
    static final private int CHECK2 = 10;  // two check bytes to go
    static final private int CHECK1 = 11;  // one check byte to go
    static final private int DONE = 12;    // finished check, done
    static final private int BAD = 13;     // got an error--stay here
    private int mode;                            // current inflate mode

    // mode dependent information
    private int method;        // if FLAGS, method byte

    // if CHECK, check values to compare
    private final long[] was = new long[1]; // computed check value
    private long need;               // stream check value

    // if BAD, inflateSync's marker bytes count
    private int marker;

    // mode independent information
    private int nowrap;          // flag for no wrapper
    private int wbits;            // log2(window size)  (8..15, defaults to 15)
    private InfBlocks blocks;     // current inflate_blocks state

    public int inflateReset(final ZStream zStream) {
        if (zStream == null || zStream.istate == null) {
            return JZlib.Z_STREAM_ERROR;
        }

        zStream.totalIn = zStream.totalOut = 0;
        zStream.msg = null;
        zStream.istate.mode = zStream.istate.nowrap != 0 ? BLOCKS : METHOD; //NOPMD C-ism
        zStream.istate.blocks.reset(zStream, null);
        return JZlib.Z_OK;
    }

    public int inflateEnd(final ZStream zStream) {
        if (blocks != null) {
            blocks.free(zStream);
        }
        blocks = null; // NO-PMD
        //    ZFREE(z, z->state);
        return JZlib.Z_OK;
    }

    public int inflateInit(final ZStream zStream, int word) { //NO-PMD mutable
        zStream.msg = null;
        blocks = null; //NO-PMD

        // handle undocumented nowrap option (no zlib header or check)
        nowrap = 0;
        if (word < 0) {
            word = -word;
            nowrap = 1;
        }

        // set window size
        if (word < 8 || word > 15) {
            inflateEnd(zStream);
            return JZlib.Z_STREAM_ERROR;
        }
        wbits = word;

        zStream.istate.blocks = new InfBlocks(zStream,
                zStream.istate.nowrap != 0 ? null : this, //NOPMD C-ism
                1 << word);

        // reset state
        inflateReset(zStream);
        return JZlib.Z_OK;
    }

    public int inflate(final ZStream zStream, int flag) { //NOPMD complex, long. mutable arg
        int indexR;
        int indexB;

        if (zStream == null || zStream.istate == null || zStream.nextIn == null) {
            return JZlib.Z_STREAM_ERROR;
        }
        flag = flag == JZlib.Z_FINISH ? JZlib.Z_BUF_ERROR : JZlib.Z_OK;
        indexR = JZlib.Z_BUF_ERROR;  //NO-PMD DU dataflow lolwut?
        while (true) {
//System.out.println("mode: "+z.istate.mode);
            switch (zStream.istate.mode) {
                case METHOD:

                    if (zStream.availIn == 0) {
                        return indexR;
                    }
                    indexR = flag;  //NO-PMD DU dataflow lolwut?

                    zStream.availIn--;
                    zStream.totalIn++;
                    if (((zStream.istate.method = //NOPMD assign in operand
                            zStream.nextIn[zStream.nextInIndex++]) & 0xf) != 
                            JZlib.Z_DEFLATED) { 
                        zStream.istate.mode = BAD;
                        zStream.msg = "unknown compression method";
                        zStream.istate.marker = 5;       // can't try inflateSync
                        break;
                    }
                    if ((zStream.istate.method >> 4) + 8 > zStream.istate.wbits) {
                        zStream.istate.mode = BAD;
                        zStream.msg = "invalid window size";
                        zStream.istate.marker = 5;       // can't try inflateSync
                        break;
                    }
                    zStream.istate.mode = FLAG;
                case FLAG:

                    if (zStream.availIn == 0) {
                        return indexR;
                    }
                    indexR = flag;  //NO-PMD DU dataflow lolwut?

                    zStream.availIn--;
                    zStream.totalIn++;
                    indexB = (zStream.nextIn[zStream.nextInIndex++]) & 0xff;

                    if ((((zStream.istate.method << 8) + indexB) % 31) != 0) {
                        zStream.istate.mode = BAD;
                        zStream.msg = "incorrect header check";
                        zStream.istate.marker = 5;       // can't try inflateSync
                        break;
                    }

                    if ((indexB & PRESET_DICT) == 0) {
                        zStream.istate.mode = BLOCKS;
                        break;
                    }
                    zStream.istate.mode = DICT4;
                case DICT4:

                    if (zStream.availIn == 0) {
                        return indexR;
                    }
                    indexR = flag;   //NO-PMD DD dataflow lolwut?

                    zStream.availIn--;
                    zStream.totalIn++;
                    zStream.istate.need = ((zStream.nextIn[zStream.nextInIndex++] & 0xff) << 24) & 0xff000000L;
                    zStream.istate.mode = DICT3;
                case DICT3:

                    if (zStream.availIn == 0) {
                        return indexR;
                    }
                    indexR = flag;  //NO-PMD DD dataflow lolwut?

                    zStream.availIn--;
                    zStream.totalIn++;
                    zStream.istate.need += ((zStream.nextIn[zStream.nextInIndex++] & 0xff) << 16) & 0xff0000L;
                    zStream.istate.mode = DICT2;
                case DICT2:

                    if (zStream.availIn == 0) {
                        return indexR;
                    }
                    indexR = flag;  //NO-PMD DD dataflow lolwut?

                    zStream.availIn--;
                    zStream.totalIn++;
                    zStream.istate.need += ((zStream.nextIn[zStream.nextInIndex++] & 0xff) << 8) & 0xff00L;
                    zStream.istate.mode = DICT1;
                case DICT1:

                    if (zStream.availIn == 0) {
                        return indexR;
                    }
                    indexR = flag;  //NO-PMD DD dataflow lolwut?

                    zStream.availIn--;
                    zStream.totalIn++;
                    zStream.istate.need += (zStream.nextIn[zStream.nextInIndex++] & 0xffL);
                    zStream.adler = zStream.istate.need;
                    zStream.istate.mode = DICT0;
                    return JZlib.Z_NEED_DICT;
                case DICT0:
                    zStream.istate.mode = BAD;
                    zStream.msg = "need dictionary";
                    zStream.istate.marker = 0;       // can try inflateSync
                    return JZlib.Z_STREAM_ERROR;
                case BLOCKS:

                    indexR = zStream.istate.blocks.proc(zStream/*, indexR*/);
                    if (indexR == JZlib.Z_DATA_ERROR) {
                        zStream.istate.mode = BAD;
                        zStream.istate.marker = 0;     // can try inflateSync
                        break;
                    }
                    if (indexR == JZlib.Z_OK) {
                        indexR = flag;
                    }
                    if (indexR != JZlib.Z_STREAM_END) {
                        return indexR;
                    }
                    indexR = flag;  //NO-PMD DU dataflow lolwut?
                    zStream.istate.blocks.reset(zStream, zStream.istate.was);
                    if (zStream.istate.nowrap != 0) {
                        zStream.istate.mode = DONE;
                        break;
                    }
                    zStream.istate.mode = CHECK4;
                case CHECK4:

                    if (zStream.availIn == 0) {
                        return indexR;
                    }
                    indexR = flag;  //NO-PMD DD dataflow lolwut?

                    zStream.availIn--;
                    zStream.totalIn++;
                    zStream.istate.need = ((zStream.nextIn[zStream.nextInIndex++] & 0xff) << 24) & 0xff000000L;
                    zStream.istate.mode = CHECK3;
                case CHECK3:

                    if (zStream.availIn == 0) {
                        return indexR;
                    }
                    indexR = flag; //NO-PMD DD dataflow lolwut?

                    zStream.availIn--;
                    zStream.totalIn++;
                    zStream.istate.need += ((zStream.nextIn[zStream.nextInIndex++] & 0xff) << 16) & 0xff0000L;
                    zStream.istate.mode = CHECK2;
                case CHECK2:

                    if (zStream.availIn == 0) {
                        return indexR;
                    }
                    indexR = flag;  //NO-PMD DD dataflow lolwut?

                    zStream.availIn--;
                    zStream.totalIn++;
                    zStream.istate.need += ((zStream.nextIn[zStream.nextInIndex++] & 0xff) << 8) & 0xff00L;
                    zStream.istate.mode = CHECK1;
                case CHECK1:

                    if (zStream.availIn == 0) {
                        return indexR;
                    }
                    indexR = flag;  //NO-PMD DU dataflow lolwut?

                    zStream.availIn--;
                    zStream.totalIn++;
                    zStream.istate.need += (zStream.nextIn[zStream.nextInIndex++] & 0xffL);

                    if (((int) (zStream.istate.was[0])) != ((int) (zStream.istate.need))) {
                        zStream.istate.mode = BAD;
                        zStream.msg = "incorrect data check";
                        zStream.istate.marker = 5;       // can't try inflateSync
                        break;
                    }

                    zStream.istate.mode = DONE;
                case DONE:
                    return JZlib.Z_STREAM_END;
                case BAD:
                    return JZlib.Z_DATA_ERROR;
                default:
                    return JZlib.Z_STREAM_ERROR;
            }
        }
    }

    public int inflateSetDictionary(final ZStream zStream, final byte[] dictionary, final int dictLength) {
        int index = 0;   //NO-PMD DD&DU dataflow lolwut?
        int length = dictLength;   //NO-PMD DU dataflow lolwut?
        if (zStream == null || zStream.istate == null || zStream.istate.mode != DICT0) {
            return JZlib.Z_STREAM_ERROR;
        }

        if (zStream.adlerObj.adler32(1L, dictionary, 0, dictLength) != zStream.adler) {
            return JZlib.Z_DATA_ERROR;
        }

        zStream.adler = zStream.adlerObj.adler32(0, null, 0, 0);

        if (length >= (1 << zStream.istate.wbits)) {
            length = (1 << zStream.istate.wbits) - 1;
            index = dictLength - length;
        }
        zStream.istate.blocks.setDictionary(dictionary, index, length);
        zStream.istate.mode = BLOCKS;
        return JZlib.Z_OK;
    }
    static private byte[] mark = {(byte) 0, (byte) 0, (byte) 0xff, (byte) 0xff};

    public int inflateSync(final ZStream zStream) { // NOPMD complexity
        int numBytesToInspect;       // number of bytes to look at
        int pointerToBytes;       // pointer to bytes
        int markerRunCount;       // number of marker bytes found in a row
        long tempIn, tempOut;   // temporaries to save total_in and total_out

        // set up
        if (zStream == null || zStream.istate == null) {
            return JZlib.Z_STREAM_ERROR;
        }
        if (zStream.istate.mode != BAD) {
            zStream.istate.mode = BAD;
            zStream.istate.marker = 0;
        }
        if ((numBytesToInspect = zStream.availIn) == 0) {   //NOPMD assignment DU dataflow lolwut?
            return JZlib.Z_BUF_ERROR;
        }
        pointerToBytes = zStream.nextInIndex;
        markerRunCount = zStream.istate.marker;

        // search
        while (numBytesToInspect != 0 && markerRunCount < 4) {
            if (zStream.nextIn[pointerToBytes] == mark[markerRunCount]) {
                markerRunCount++;
            } else if (zStream.nextIn[pointerToBytes] != 0) { // NOPMD C-ism
                markerRunCount = 0;
            } else {
                markerRunCount = 4 - markerRunCount;
            }
            pointerToBytes++;
            numBytesToInspect--;
        }

        // restore
        zStream.totalIn += pointerToBytes - zStream.nextInIndex;
        zStream.nextInIndex = pointerToBytes;
        zStream.availIn = numBytesToInspect;
        zStream.istate.marker = markerRunCount;

        // return no joy or set up to restart on a new block
        if (markerRunCount != 4) {
            return JZlib.Z_DATA_ERROR;
        }
        tempIn = zStream.totalIn;
        tempOut = zStream.totalOut;
        inflateReset(zStream);
        zStream.totalIn = tempIn;
        zStream.totalOut = tempOut;
        zStream.istate.mode = BLOCKS;
        return JZlib.Z_OK;
    }

    // Returns true if inflate is currently at the end of a block generated
    // by Z_SYNC_FLUSH or Z_FULL_FLUSH. This function is used by one PPP
    // implementation to provide an additional safety check. PPP uses Z_SYNC_FLUSH
    // but removes the length bytes of the resulting empty stored block. When
    // decompressing, PPP checks that at the end of input packet, inflate is
    // waiting for these length bytes.
    public int inflateSyncPoint(final ZStream zStream) {
        if (zStream == null || zStream.istate == null || zStream.istate.blocks == null) {
            return JZlib.Z_STREAM_ERROR;
        }
        return zStream.istate.blocks.syncPoint();
    }
}
