package com.ravnaandtines.crypt.mda;

/**<PRE>
 * SHA0.java - An implementation of the SHA-0 Algorithm
 *  <P>
 *  Coded Mr. Tines &lt;tines@windsong.demon.co.uk&gt; 1998
 *  <P>
 * THIS SOFTWARE IS PROVIDED BY THE AUTHORS ''AS IS'' AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHORS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  <PRE>
 *
 *      Test Vectors (from FIPS PUB 180-1)
 *      "abc"
 *      0164b8a9 14cd2a5e 74c4f7ff 082c4d97 f1edf880
 *      "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq"
 *      d2516ee1 acfa5baf 33dfc1c4 71e43844 9ef134c8
 *      A million repetitions of "a"
 *      3232affa 48628a26 653b5aaa 44541fd9 0d690603
 *
 *</PRE>
 * @author Mr. Tines
 * @version 1.0 23-Dec-1998
 * @version 2.0 25-Dec-2007
 */
 /* This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details. */

public final class SHA0 implements MDA {
    private final SHABase worker;

    /**
     * simple constructor
     */
    public SHA0() {
        worker = new SHABase(false);
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
