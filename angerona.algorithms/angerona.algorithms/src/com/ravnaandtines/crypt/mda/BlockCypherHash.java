package com.ravnaandtines.crypt.mda;

import com.ravnaandtines.crypt.cea.*;

/**
 *  Class BlockCypherHash<p>
 *  -----------------------------------------------------------------------<p>
 * Routines to support a simple secure hash based on 3-Way, with MD
 *	strengthening as per MD5.  The algorithm is the first from Table 18.1
 *	of Schneier, H[i] = E{H[i-1]}(M[i]) ^ M[i]
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
 *  <P>
 * @author Mr. Tines
 * @version 1.0 23-Dec-1998
 * @version 2.0 25-Dec-2007
 */
 /* This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details. */


public final class BlockCypherHash implements MDA {

    /**
     * Block cypher with keysize = blocksize
     */
    private CEA cypher = null;
    /**
     * working buffer for hash intermediates
     */
    private byte[] buf = null;
    /**
     * bits hashed mod 2^64
     */
    private long bits;
    /**
     * batched up input
     */
    private byte[] input = null;
    /**
     * bytes batched up
     */
    private int over;
    /**
     * intermediate buffer
     */
    private byte[] work = null;
    /**
     * Magic numbers to start the hash - taken from Blowfish S-boxes
     */
    private static final byte[] MAGIC = {
        (byte) 0xd1, 0x31, 0x0b, (byte) 0xa6,
        (byte) 0x98, (byte) 0xdf, (byte) 0xb5, (byte) 0xac,
        0x2f, (byte) 0xfd, 0x72, (byte) 0xdb,
        (byte) 0xd0, 0x1a, (byte) 0xdf, (byte) 0xb7,
        (byte) 0xb8, (byte) 0xe1, (byte) 0xaf, (byte) 0xed,
        0x6a, 0x26, 0x7e, (byte) 0x96,
        (byte) 0xba, 0x7c, (byte) 0x90, 0x45,
        (byte) 0xf1, 0x2c, 0x7f, (byte) 0x99
    }; //256 bits

    /**
     * Default constructor uses 3-Way
     */
    public BlockCypherHash() {
        this(new ThreeWay());
    }

    /**
     * Constructs the hash engine from the cypher
     * @param block block cypher used
     */
    public BlockCypherHash(final CEA block) {
        if (null == block) {
            throw new IllegalArgumentException("block");
        }
        if (block.getKeysize() != block.getBlocksize()) {
            throw new IllegalStateException(block.getClass().toString());
        }
        cypher = block;
        buf = new byte[cypher.getBlocksize()];
        input = new byte[cypher.getBlocksize()];
        work = new byte[cypher.getBlocksize()];
        init();
    }

    /**
     * zero all counts and load buffer with MAGIC numbers
     */
    private void init() {
        for (int i = 0; i < buf.length; ++i) {
            buf[i] = MAGIC[i % MAGIC.length];
        }
        /* initialise the count */
        bits = 0;
        over = 0;
    }

    /**
     * H[i] = E{H[i-1]}(M[i]) ^ M[i]
     * @param h the H value
     * @param oh offset into h where our values start
     * @param m the M value
     * @param om offset into m where our values start
     */
    private void hash(final byte[] hash, final int hashOffset,
            final byte[] messageBlock, final int messageOffset) {
        cypher.init(hash, hashOffset, false);
        cypher.ecb(true, messageBlock, messageOffset, work, 0);
        cypher.destroy();

        for (int i = 0; i < work.length; i++) {
            hash[i + hashOffset] = (byte) (work[i] ^ messageBlock[i + messageOffset]);
        }
    }

    /**
     * Feeds a batch of bytes into the hash
     * @param data the byte values
     * @param offset the first byte index to take
     * @param length the number of bytes to take
     */
    public void update(final byte[] data, int offset, int length) {
        bits += length << 3;

        /* handle any left-over bytes in scratch space */
        int space = over; //NO-PMD name
        if (over > 0) {
            space = cypher.getBlocksize() - space; /* space left */
            /* if there's still not enough, exit */
            if (length < space) {
                System.arraycopy(data, offset, input, over, length);
                over += length;
                return;
            }
            System.arraycopy(data, offset, input, over, space);
            hash(buf, 0, input, 0);

            offset += space;
            length -= space;
        }

        /* the body of the input data */
        for (; length >= cypher.getBlocksize();
                offset += cypher.getBlocksize(), length -= cypher.getBlocksize()) {
            hash(buf, 0, data, offset);
        }
        /* tuck left-overs away */
        over = length;
        if (length > 0) {
            System.arraycopy(data, offset, input, 0, length);
        }
    }

    /**
     * Feeds a  byte into the hash
     * @param data the byte value
     */
    public void update(final byte data) {
        final byte[] temp = {data};
        update(temp, 0, temp.length);
    }

    /**
     * consolidates the input, and reinitialises the hash
     * @return the hash value
     */
    public byte[] digest() {
        int count = over;
        //byte *p = md->in + count;

        /* there is always a free byte by construction */
        input[count] = (byte) 0x80;
        count = cypher.getBlocksize() - (count + 1); /* free bytes in buffer */

        if (count < 8) /* not enough space for the count */ {
            for (int i = 0; i < count; ++i) {
                input[over + 1 + i] = 0;
            }
            hash(buf, 0, input, 0);  /*so hash and start again*/
            for (int j = 0; j < cypher.getBlocksize() - 8; ++j) {
                input[j] = 0;
            }
        } else {
            for (int k = 0; k < count - 8; ++k) {
                input[over + 1 + k] = 0;
            }
        }

        final int index = cypher.getBlocksize() - 8;
        /* pack high byte first */
        input[index] = (byte) ((bits >> 56) & 0xFF);
        input[index + 1] = (byte) ((bits >> 48) & 0xFF);
        input[index + 2] = (byte) ((bits >> 40) & 0xFF);
        input[index + 3] = (byte) ((bits >> 32) & 0xFF);

        input[index + 4] = (byte) ((bits >> 24) & 0xFF);
        input[index + 5] = (byte) ((bits >> 16) & 0xFF);
        input[index + 6] = (byte) ((bits >> 8) & 0xFF);
        input[index + 7] = (byte) ((bits) & 0xFF);

        hash(buf, 0, input, 0);
        final byte[] out = new byte[cypher.getBlocksize()];
        System.arraycopy(buf, 0, out, 0, buf.length);
        init();
        return out;
    }
}



