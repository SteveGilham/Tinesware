/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
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

final public class ZStream {

//    static final private int Z_NO_FLUSH = 0;
//    static final private int Z_PARTIAL_FLUSH = 1;
//    static final private int Z_SYNC_FLUSH = 2;
//    static final private int Z_FULL_FLUSH = 3;
//    static final private int Z_FINISH = 4;
//    static final private int MAX_MEM_LEVEL = 9;
//    static final private int Z_OK = 0;
//    static final private int Z_STREAM_END = 1;
//    static final private int Z_NEED_DICT = 2;
//    static final private int Z_ERRNO = -1;
    static final private int Z_STREAM_ERROR = -2;
//    static final private int Z_DATA_ERROR = -3;
//    static final private int Z_MEM_ERROR = -4;
//    static final private int Z_BUF_ERROR = -5;
//    static final private int Z_VERSION_ERROR = -6;
    public byte[] nextIn;     // next input byte
    public int nextInIndex;
    public int availIn;       // number of bytes available at next_in
    public long totalIn;      // total nb of input bytes read so far
    public byte[] nextOut;    // next output byte should be put there
    public int nextOutIndex;
    public int availOut;      // remaining free space at next_out
    public long totalOut;     // total nb of bytes output so far
    public String msg;
    Deflate dstate; //NOPMD package scope
    Inflate istate; //NOPMD package scope
    //int dataType; //NOPMD package scope // best guess about the data type: ascii or binary
    public long adler;
    Adler32 adlerObj = new Adler32(); //NOPMD package scope

    public int inflateInit() {
        return inflateInit(JZlib.DEF_WBITS);
    }

    public int inflateInit(final boolean nowrap) {
        return inflateInit(JZlib.DEF_WBITS, nowrap);
    }

    public int inflateInit(final int word) {
        return inflateInit(word, false);
    }

    public int inflateInit(final int word, final boolean nowrap) {
        istate = new Inflate();
        return istate.inflateInit(this, nowrap ? -word : word);
    }

    public int inflate(final int flag) {
        if (istate == null) {
            return Z_STREAM_ERROR;
        }
        return istate.inflate(this, flag);
    }

    public int inflateEnd() {
        if (istate == null) {
            return Z_STREAM_ERROR;
        }
        final int ret = istate.inflateEnd(this);
        istate = null; // NO-PMD
        return ret;
    }

    public int inflateSync() {
        if (istate == null) {
            return Z_STREAM_ERROR;
        }
        return istate.inflateSync(this);
    }

    public int inflateSetDictionary(final byte[] dictionary, final int dictLength) {
        if (istate == null) {
            return Z_STREAM_ERROR;
        }
        return istate.inflateSetDictionary(this, dictionary, dictLength);
    }

    public int deflateInit(final int level) {
        return deflateInit(level, JZlib.MAX_WBITS);
    }

    public int deflateInit(final int level, final boolean nowrap) {
        return deflateInit(level, JZlib.MAX_WBITS, nowrap);
    }

    public int deflateInit(final int level, final int bits) {
        return deflateInit(level, bits, false);
    }

    public int deflateInit(final int level, final int bits, final boolean nowrap) {
        dstate = new Deflate();
        return dstate.deflateInit(this, level, nowrap ? -bits : bits);
    }

    public int deflate(final int flush) {
        if (dstate == null) {
            return Z_STREAM_ERROR;
        }
        return dstate.deflate(this, flush);
    }

    public int deflateEnd() {
        if (dstate == null) {
            return Z_STREAM_ERROR;
        }
        final int ret = dstate.deflateEnd();
        dstate = null; // NO-PMD
        return ret;
    }

    public int deflateParams(final int level, final int strategy) {
        if (dstate == null) {
            return Z_STREAM_ERROR;
        }
        return dstate.deflateParams(this, level, strategy);
    }

    public int deflateSetDictionary(final byte[] dictionary, final int dictLength) {
        if (dstate == null) {
            return Z_STREAM_ERROR;
        }
        return dstate.deflateSetDictionary(this, dictionary, dictLength);
    }

    // Flush as much pending output as possible. All deflate() output goes
    // through this function so some applications may wish to modify it
    // to avoid allocating a large strm->next_out buffer and copying into it.
    // (See also read_buf()).
    public void flushPending() {
        int len = dstate.pending;

        if (len > availOut) {
            len = availOut;
        }
        if (len == 0) {
            return;
        }

        /*
        if (dstate.pending_buf.length <= dstate.pending_out ||
                next_out.length <= nextOutIndex ||
                dstate.pending_buf.length < (dstate.pending_out + len) ||
                next_out.length < (nextOutIndex + len)) {
            System.out.println(dstate.pending_buf.length + ", " + dstate.pending_out +
                    ", " + next_out.length + ", " + nextOutIndex + ", " + len);
            System.out.println("avail_out=" + availOut);
        }
        */

        System.arraycopy(dstate.pendingBuf, dstate.pendingOut,
                nextOut, nextOutIndex, len);

        nextOutIndex += len;
        dstate.pendingOut += len;
        totalOut += len;
        availOut -= len;
        dstate.pending -= len;
        if (dstate.pending == 0) {
            dstate.pendingOut = 0;
        }
    }

    // Read a new buffer from the current input stream, update the adler32
    // and total number of bytes read.  All deflate() input goes through
    // this function so some applications may wish to modify it to avoid
    // allocating a large strm->next_in buffer and copying from it.
    // (See also flush_pending()).
    public int readBuf(final byte[] buf, final int start, final int size) {
        int len = availIn;

        if (len > size) {
            len = size;
        }
        if (len == 0) {
            return 0;
        }

        availIn -= len;

        if (dstate.noheader == 0) {
            adler = adlerObj.adler32(adler, nextIn, nextInIndex, len);
        }
        System.arraycopy(nextIn, nextInIndex, buf, start, len);
        nextInIndex += len;
        totalIn += len;
        return len;
    }

    public void free() {
        nextIn = null; // NO-PMD
        nextOut = null; // NO-PMD
        msg = null; // NO-PMD
        adlerObj = null; // NO-PMD
    }
}
