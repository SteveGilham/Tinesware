/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/*
Copyright (c) 2001 Lapo Luchini.
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
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHORS
OR ANY CONTRIBUTORS TO THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT,
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

import java.io.*;

public final class ZOutputStream extends OutputStream {

    private ZStream zStream = new ZStream();
    private static final int BUFSIZE = 512;
    private int flushLevel = JZlib.Z_NO_FLUSH;
    private final byte[] buf = new byte[BUFSIZE];
    private byte[] buf1 = new byte[1]; //NOPMD
    private boolean compress;
    private OutputStream out;

    public ZOutputStream(final OutputStream out) {
        super();
        this.out = out;
        zStream.inflateInit();
        compress = false;
    }

    public ZOutputStream(final OutputStream out, final int level) {
        this(out, level, false);
    }

    public ZOutputStream(final OutputStream out, final int level, final boolean nowrap) {
        this(out, level, JZlib.MAX_WBITS, nowrap);
    }

    public ZOutputStream(final OutputStream out, final int level, final int bits, final boolean nowrap) {
        super();
        this.out = out;
        int actualBits = bits;
        if(bits < 0 || bits > JZlib.MAX_WBITS)
        {
            actualBits = JZlib.DEF_WBITS;
        }
        zStream.deflateInit(level, actualBits, nowrap);
        compress = true;
    }
    
    public void write(final int b) throws IOException { //NOPMD
        buf1[0] = (byte) b;
        write(buf1, 0, 1);
    }

    public void write(final byte[] buffer, // NOPMD -- array passed w/o copying
            final int offset, final int count) throws IOException {
        if (count == 0) {
            return;
        }
        int err;
        zStream.nextIn = buffer;
        zStream.nextInIndex = offset;
        zStream.availIn = count;
        do {
            zStream.nextOut = buf;
            zStream.nextOutIndex = 0;
            zStream.availOut = BUFSIZE;
            if (compress) {
                err = zStream.deflate(flushLevel);
            } else {
                err = zStream.inflate(flushLevel);
            }
            if (err != JZlib.Z_OK) {
                throw new ZStreamException((compress ? "de" : "in") + "flating: " + zStream.msg);
            }
            out.write(buf, 0, BUFSIZE - zStream.availOut);
        } while (zStream.availIn > 0 || zStream.availOut == 0);
    }

    public int getFlushMode() {
        return flushLevel;
    }

    public void setFlushMode(final int flush) {
        this.flushLevel = flush;
    }

    public void finish() throws IOException {
        int err;
        do {
            zStream.nextOut = buf;
            zStream.nextOutIndex = 0;
            zStream.availOut = BUFSIZE;
            if (compress) {
                err = zStream.deflate(JZlib.Z_FINISH);
            } else {
                err = zStream.inflate(JZlib.Z_FINISH);
            }
            if (err != JZlib.Z_STREAM_END && err != JZlib.Z_OK) {
                throw new ZStreamException((compress ? "de" : "in") + "flating: " + zStream.msg);
            }
            if (BUFSIZE - zStream.availOut > 0) {
                out.write(buf, 0, BUFSIZE - zStream.availOut);
            }
        } while (zStream.availIn > 0 || zStream.availOut == 0);
        flush();
    }

    public void end() {
        if (zStream == null) {
            return;
        }
        if (compress) {
            zStream.deflateEnd();
        } else {
            zStream.inflateEnd();
        }
        zStream.free();
        zStream = null; // NO-PMD
    }

    public void close() throws IOException {
        try {
            try {
                finish();
            } catch (IOException ignored) { // NOPMD
            }
        } finally {
            end();
            out.close();
            out = null;
        }
    }

    /**
     * Returns the total number of bytes input so far.
     */
    public long getTotalIn() {
        return zStream.totalIn;
    }

    /**
     * Returns the total number of bytes output so far.
     */
    public long getTotalOut() {
        return zStream.totalOut;
    }

    public void flush() throws IOException {
        out.flush();
    }
}
