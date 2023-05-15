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

final class Adler32 {

    // largest prime smaller than 65536
    static final private int BASE = 65521;
    // NMAX is the largest n such that 255n(n+1)/2 + (n+1)(BASE-1) <= 2^32-1
    static final private int NMAX = 5552;

    public long adler32(final long adler, final byte[] buf, 
            int index, int len) { //NO-PMD mutable
        if (buf == null) {
            return 1L;
        }

        long low = adler & 0xffff;
        long high = (adler >> 16) & 0xffff;
        int count;

        while (len > 0) {
            count = len < NMAX ? len : NMAX;
            len -= count;
            while (count >= 16) {
                low += buf[index++] & 0xff;
                high += low;
                low += buf[index++] & 0xff;
                high += low;
                low += buf[index++] & 0xff;
                high += low;
                low += buf[index++] & 0xff;
                high += low;
                low += buf[index++] & 0xff;
                high += low;
                low += buf[index++] & 0xff;
                high += low;
                low += buf[index++] & 0xff;
                high += low;
                low += buf[index++] & 0xff;
                high += low;
                low += buf[index++] & 0xff;
                high += low;
                low += buf[index++] & 0xff;
                high += low;
                low += buf[index++] & 0xff;
                high += low;
                low += buf[index++] & 0xff;
                high += low;
                low += buf[index++] & 0xff;
                high += low;
                low += buf[index++] & 0xff;
                high += low;
                low += buf[index++] & 0xff;
                high += low;
                low += buf[index++] & 0xff;
                high += low;
                count -= 16;
            }
            if (count != 0) {
                do {
                    low += buf[index++] & 0xff;
                    high += low;
                } while (--count != 0);
            }
            low %= BASE;
            high %= BASE;
        }
        return (high << 16) | low;
    }
}
