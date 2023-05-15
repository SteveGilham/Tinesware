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

public final class ZInputStream extends FilterInputStream { //NOPMD complexity

    private final ZStream zStream = new ZStream();
    private final static int BUFSIZE = 512;
    private int flush = JZlib.Z_NO_FLUSH;
    private final byte[] buf = new byte[BUFSIZE];
    private final byte[] buf1 = new byte[1];
    private boolean compress;
    private InputStream input = null;

    public ZInputStream(final InputStream inStream) {
        this(inStream, false);
    }

    public ZInputStream(final InputStream inStream, final boolean nowrap) {
        super(inStream);
        this.input = inStream;
        zStream.inflateInit(nowrap);
        compress = false;
        zStream.nextIn = buf;
        zStream.nextInIndex = 0;
        zStream.availIn = 0;
    }

    public ZInputStream(final InputStream inStream, final int level) {
        super(inStream);
        this.input = inStream;
        zStream.deflateInit(level);
        compress = true;
        zStream.nextIn = buf;
        zStream.nextInIndex = 0;
        zStream.availIn = 0;
    }

    /*public int available() throws IOException {
    return inf.finished() ? 0 : 1;
    }*/
    public int read() throws IOException {
        if (read(buf1, 0, 1) == -1) {
            return (-1);
        }
        return (buf1[0] & 0xFF);
    }
    private boolean nomoreinput = false;

    public int read(final byte[] buffer, final int off,  //NOPMD buffer, complexity
            final int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        int err;
        zStream.nextOut = buffer;
        zStream.nextOutIndex = off;
        zStream.availOut = len;
        do {
            if ((zStream.availIn == 0) && (!nomoreinput)) { // if buffer is empty and more input is avaiable, refill it
                zStream.nextInIndex = 0;
                zStream.availIn = input.read(buf, 0, BUFSIZE);//(BUFSIZE<z.avail_out ? BUFSIZE : z.avail_out));
                if (zStream.availIn == -1) {
                    zStream.availIn = 0;
                    nomoreinput = true;
                }
            }
            if (compress) {
                err = zStream.deflate(flush);
            } else {
                err = zStream.inflate(flush);
            }
            if (nomoreinput && (err == JZlib.Z_BUF_ERROR)) {
                return (-1);
            }
            if (err != JZlib.Z_OK && err != JZlib.Z_STREAM_END) {
                throw new ZStreamException((compress ? "de" : "in") + "flating: " + zStream.msg);
            }
            if ((nomoreinput || err == JZlib.Z_STREAM_END) && (zStream.availOut == len)) {
                return (-1);
            }
        } while (zStream.availOut == len && err == JZlib.Z_OK);
        //System.err.print("("+(len-z.avail_out)+")");
        return (len - zStream.availOut);
    }

    public long skip(final long count) throws IOException {
        int len = 512;
        if (count < len) {
            len = (int) count;
        }
        final byte[] tmp = new byte[len];
        return ((long) read(tmp));
    }

    public int getFlushMode() {
        return flush;
    }

    public void setFlushMode(final int flush) {
        this.flush = flush;
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

    public void close() throws IOException {
        input.close();
    }
}
