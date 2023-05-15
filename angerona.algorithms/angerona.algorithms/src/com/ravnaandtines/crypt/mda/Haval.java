package com.ravnaandtines.crypt.mda;

/**
 * The HAVAL hashing function
 * Public domain implementation by Paulo S.L.M. Barreto &lt;pbarreto@uninet.com.br&gt;
 * Version 1.1 (1997.04.07)
 *<count>
 * Modified for MDA interface & Java port Mr. Tines &lt;tines@windsong.demon.co.uk&gt;
 * 26-Dec-1998
 *  <count>
 * =============================================================================
 * <count>
 * Differences from version 1.0 (1997.04.03):
 * <count>
 * - Replaced function F5 by an optimized version (saving a boolean operation).
 *   Thanks to Wei Dai &lt;weidai@eskimo.com&gt; for this improvement.
 *  <count>
 * =============================================================================
 *  <count>
 * Reference: Zheng, Y., Pieprzyk, J., Seberry, J.:
 * "HAVAL - a one-way hashing algorithm with variable length of output",
 * Advances in Cryptology (AusCrypt'92), LNCS 718 (1993), 83-104, Springer-Verlag.
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
 * @version 1.0 27-Dec-1998
 * @version 2.0 25-Dec-2007
 */

public final class Haval implements MDA {  //NOPMD complex

    /**
     * Initialise a HAVAL hashing context as per EBP
     */
    public Haval() {
        this(97);
    }

    public Haval(final int passes, final int length) {
        this(true, passes, length);
    }

    /**
     * Initializes a HAVAL hashing context according to the desired
     * number of passes and hash length.
     * @param fixed if true use 0x01 as padding not 0x08
     * @param passes number of passes must be 3, 4, or 5.
     * @param length hash length must be 128, 160, 192, 224, or 256.
     */
    private Haval(final boolean fixed, int passes, int length) {
        if (passes < 3) {
            this.passes = 3;
        } else if (passes > 5) {
            this.passes = 5;
        } else {
            this.passes = passes;
        }

        if (length == 128 || length == 160 || 
                length == 192 || length == 224) {
            this.hashLength = length;
        } else {
            this.hashLength = 256;
        }
        ebp = !fixed;

        init();
    }

    /**
     * Initialise a HAVAL hashing context as per EBP
     * @param type byte value 97+
     */
    public Haval(final int type) {
        this((type > 0x80), getPasses(type), getSize(type));
    }

    /**
     * sets initial state and magic numbers
     */
    private void init() {
        messageDigest[0] = 0x243F6A88;
        messageDigest[1] = 0x85A308D3;
        messageDigest[2] = 0x13198A2E;
        messageDigest[3] = 0x03707344;
        messageDigest[4] = 0xA4093822;
        messageDigest[5] = 0x299F31D0;
        messageDigest[6] = 0x082EFA98;
        messageDigest[7] = 0xEC4E6C89;

        for (int i = 0; i < block.length; ++i) {
            block[i] = 0;
        }
        occupied = 0;
        bitCount = 0;
        for (int i = 0; i < temp.length; ++i) {
            temp[i] = 0;
        }
    }

    /**
     * Feeds a batch of bytes into the hash
     * @param data the byte values
     * @param offset the first byte index to take
     * @param length the number of bytes to take
     */
    public void update(final byte[] data,  //NOPMD complex
            int offset, int length) {

        if (data == null || length == 0) {
            return; /* nothing to do */
        }

        /* update bit count: */
        bitCount += length << 3;

        /* if the data buffer is not enough to complete */
        /* the context data block, just append it: */
        if (occupied + length < 128) {
            System.arraycopy(data, offset, block, occupied, length);
            occupied += length;
            return; /* delay processing */
        }

        /* complete the context data block: */
        System.arraycopy(data, offset, block, occupied, 128 - occupied);
        offset += 128 - occupied;
        length -= 128 - occupied;

        switch (passes) {
            case 3:
                /* process the completed context data block: */
                transform3(block, 0);
                /* process data in chunks of 128 bytes: */
                while (length >= 128) {
                    transform3(data, offset);
                    offset += 128;
                    length -= 128;
                }
                break;
            case 4:
                /* process the completed context data block: */
                transform4(block, 0);
                /* process data in chunks of 128 bytes: */
                while (length >= 128) {
                    transform4(data, offset);
                    offset += 128;
                    length -= 128;
                }
                break;
            case 5:
                /* process the completed context data block: */
                transform5(block, 0);
                /* process data in chunks of 128 bytes: */
                while (length >= 128) {
                    transform5(data, offset);
                    offset += 128;
                    length -= 128;
                }
                break;
            default:
                throw new IllegalStateException(":"+passes);
        }

        System.arraycopy(data, offset, block, 0, length);
        occupied = length;
    }

    /**
     * Feeds a  byte into the hash
     * @param data the byte value
     */
    public void update(final byte data) {
        final byte[] buffer = {data};
        update(buffer, 0, buffer.length);
    }

    public byte[] digest() { // NOPMD complex
        /* append toggle to the context data block: */
        if (ebp) {
            block[occupied] = (byte) 0x80;  /* EBP gets this wrong */
        } else {
            block[occupied] = (byte) 0x01; /* corrected from 0x80 */
        }

        /* pad the message with null bytes to make it 944 (mod 1024) bits long: */
        if (occupied++ >= 118) {
            /* no room for tail data on the current context block */
            for (int i = occupied; i < 128; ++i) {
                block[i] = 0;
            }

            /* process the completed context data block: */
            switch (passes) {
                case 3:
                    transform3(block, 0);
                    break;
                case 4:
                    transform4(block, 0);
                    break;
                case 5:
                    transform5(block, 0);
                    break;
                default:
                    throw new IllegalStateException(":" + passes);
            }
            for (int j = 0; j < 118; ++j) {
                block[j] = 0;
            }
        } else {
            for (int k = occupied; k < 118; ++k) {
                block[k] = 0;
            }
        }
        /* append tail data and process last (padded) message block: */
        if (ebp) /* EBP got this wrong too!, hardcoding it at the max */ {
            block[118] = (byte) (((256 & 0x03) << 6) |
                    ((5 & 0x07) << 3) |
                    (HAVAL_VERSION & 0x07));
            block[119] = (byte) (256 >>> 2);
        } else {
            block[118] = (byte) (((hashLength & 0x03) << 6) |
                    ((passes & 0x07) << 3) |
                    (HAVAL_VERSION & 0x07));
            block[119] = (byte) (hashLength >>> 2);
        }
        for (int ix = 0; ix < 8; ++ix) {
            block[120 + ix] = (byte) (0xFF & (bitCount >> (8 * ix)));
        }

        switch (passes) {
            case 3:
                transform3(block, 0);
                break;
            case 4:
                transform4(block, 0);
                break;
            case 5:
                transform5(block, 0);
                break;
            default:
                throw new IllegalStateException(":" + passes);
        }

        /* fold 256-bit digest to fit the desired hash length (blaargh!): */
        switch (hashLength) {
            case 128:
                messageDigest[3] +=
                        ((messageDigest[7] & 0xFF000000) | (messageDigest[6] & 0x00FF0000) | (messageDigest[5] & 0x0000FF00) | (messageDigest[4] & 0x000000FF));
                messageDigest[2] +=
                        (((messageDigest[7] & 0x00FF0000) | (messageDigest[6] & 0x0000FF00) | (messageDigest[5] & 0x000000FF)) << 8) |
                        ((messageDigest[4] & 0xFF000000) >>> 24);
                messageDigest[1] +=
                        (((messageDigest[7] & 0x0000FF00) | (messageDigest[6] & 0x000000FF)) << 16) |
                        (((messageDigest[5] & 0xFF000000) | (messageDigest[4] & 0x00FF0000)) >>> 16);
                messageDigest[0] +=
                        (((messageDigest[6] & 0xFF000000) | (messageDigest[5] & 0x00FF0000) | (messageDigest[4] & 0x0000FF00)) >>> 8) |
                        ((messageDigest[7] & 0x000000FF) << 24);
                break;
            case 160:
                messageDigest[4] +=
                        ((messageDigest[7] & 0xFE000000) | (messageDigest[6] & 0x01F80000) |
                        (messageDigest[5] & 0x0007F000)) >>> 12;
                messageDigest[3] +=
                        ((messageDigest[7] & 0x01F80000) | (messageDigest[6] & 0x0007F000) |
                        (messageDigest[5] & 0x00000FC0)) >>> 6;
                messageDigest[2] +=
                        ((messageDigest[7] & 0x0007F000) | (messageDigest[6] & 0x00000FC0) |
                        (messageDigest[5] & 0x0000003F));
                messageDigest[1] +=
                        ROTR(((messageDigest[7] & 0x00000FC0) | (messageDigest[6] & 0x0000003F) |
                        (messageDigest[5] & 0xFE000000)), 25);
                messageDigest[0] +=
                        ROTR((messageDigest[7] & 0x0000003F) | (messageDigest[6] & 0xFE000000) |
                        (messageDigest[5] & 0x01F80000), 19);
                break;
            case 192:
                messageDigest[5] +=
                        ((messageDigest[7] & 0xFC000000) | (messageDigest[6] & 0x03E00000)) >>> 21;
                messageDigest[4] +=
                        ((messageDigest[7] & 0x03E00000) | (messageDigest[6] & 0x001F0000)) >>> 16;
                messageDigest[3] +=
                        ((messageDigest[7] & 0x001F0000) | (messageDigest[6] & 0x0000FC00)) >>> 10;
                messageDigest[2] +=
                        ((messageDigest[7] & 0x0000FC00) | (messageDigest[6] & 0x000003E0)) >>> 5;
                messageDigest[1] +=
                        ((messageDigest[7] & 0x000003E0) | (messageDigest[6] & 0x0000001F));
                messageDigest[0] +=
                        ROTR((messageDigest[7] & 0x0000001F) | (messageDigest[6] & 0xFC000000), 26);
                break;
            case 224:
                messageDigest[6] += (messageDigest[7]) & 0x0000000F;
                messageDigest[5] += (messageDigest[7] >>> 4) & 0x0000001F;
                messageDigest[4] += (messageDigest[7] >>> 9) & 0x0000000F;
                messageDigest[3] += (messageDigest[7] >>> 13) & 0x0000001F;
                messageDigest[2] += (messageDigest[7] >>> 18) & 0x0000000F;
                messageDigest[1] += (messageDigest[7] >>> 22) & 0x0000001F;
                messageDigest[0] += (messageDigest[7] >>> 27) & 0x0000001F;
                break;
            case 256:
                break;
            default:
                throw new IllegalStateException(":" + passes);
        }

        final byte[] output = new byte[hashLength / 8];
        flip32(output);
        init();
        return output;
    }

    /**
     * flips each 32-bit section from md array to output
     * @param output byte array with hash in
     */
    private void flip32(final byte[] output) {
        final int count = output.length / 4;
        for (int i = 0; i < count; ++i) {
            output[4 * i + 3] = (byte) ((messageDigest[i] >>> 24) & 0xFF);
            output[4 * i + 2] = (byte) ((messageDigest[i] >>> 16) & 0xFF);
            output[4 * i + 1] = (byte) ((messageDigest[i] >>> 8) & 0xFF);
            output[4 * i + 0] = (byte) ((messageDigest[i]) & 0xFF);
        }
    }
    /**
     * HAVAL parameters */
    private int passes,  hashLength;
    /**
     * message digest (fingerprint)
     */
    private int[] messageDigest = new int[8]; //NO-PMD name
    /**
     * context data block
     */
    private byte[] block = new byte[128];
    /**
     * number of occupied bytes in the data block
     */
    private int occupied;
    /**
     * 64-bit message bit count
     */
    private long bitCount;
    /**
     * temporary buffer
     */
    private int[] temp = new int[8];
    /**
     * controls padding style
     */
    private boolean ebp;
    /**
     * magic number
     */
    private static final int HAVAL_VERSION = 1;

    /**
     * round functions
     */
    private static int F1(int X6, int X5, int X4, int X3, int X2, int X1, int X0) { //NOPMD
        return (((X1) & ((X4) ^ (X0))) ^ ((X2) & (X5)) ^ ((X3) & (X6)) ^ (X0));
    }

    private static int F2(int X6, int X5, int X4, int X3, int X2, int X1, int X0) { //NOPMD
        return (((X2) & (((X1) & (~(X3))) ^ ((X4) & (X5)) ^ (X6) ^ (X0))) ^
                (((X4) & ((X1) ^ (X5))) ^ ((X3) & (X5)) ^ (X0)));
    }

    private static int F3(int X6, int X5, int X4, int X3, int X2, int X1, int X0) { //NOPMD
        return (((X3) & (((X1) & (X2)) ^ (X6) ^ (X0))) ^ ((X1) & (X4)) ^ ((X2) & (X5)) ^ (X0));
    }

    private static int F4(int X6, int X5, int X4, int X3, int X2, int X1, int X0) { //NOPMD
        return (((X4) & (((~(X2)) & (X5)) ^ ((X3) | (X6)) ^ (X1) ^ (X0))) ^
                ((X3) & (((X1) & (X2)) ^ (X5) ^ (X6))) ^ ((X2) & (X6)) ^ (X0));
    }

    private static int F5(int X6, int X5, int X4, int X3, int X2, int X1, int X0) { //NOPMD
        return (((X1) & ((X4) ^ ((X0) & (X2) & (X3)))) ^ (((X2) ^ (X0)) & (X5)) ^ ((X3) & (X6)) ^ (X0));
    }

    private static int ROTR(final int v, final int n) { //NOPMD
        return (v >>> n) | (v << (32 - n));
    }

    /**
     * decode number of passes from algorithm byte value
     * @param ebpTypeIn byte value 97-111 as int
     */
    private static int getPasses(final int ebpTypeIn) {
        int count;
        int ebpType = ebpTypeIn;
        if (ebpType > 128) {
            ebpType = 97 + (ebpType - 133);
        }
        if (ebpType < 97) {
            return 5;
        } else if (ebpType <= 111) {
            count = (ebpType - 111);
        } else {
            return 5;
        }
        return 5 - (count / 5);
    }

    /**
     * decode length of hash from algorithm byte value
     * @param ebpType byte value 97-111 as int
     */
    private static int getSize(final int ebpTypeIn) {
        int size;
        int ebpType = ebpTypeIn;
        if (ebpType > 128) {
            ebpType = 97 + (ebpType - 133);
        }
        if (ebpType < 97) {
            return 256;
        } else if (ebpType <= 111) {
            size = 5 + ((ebpType - 111) % 5);
        } else {
            return 256;
        }

        switch (size) {
            case 0:
                return 256;
            case 1:
                return 224;
            case 2:
                return 192;
            case 3:
                return 160;
            case 4:
                return 128;
            default:
                throw new IllegalStateException(":" + size);
        }
    }

    private void transform3(final byte[] D, final int offset) { //NOPMD complex
        final int[] W = new int[32]; //NOPMD name
        for (int i = 0; i < 32; ++i) {
            W[i] =
                    ((D[offset + 3 + 4 * i] & 0xFF) << 24) |
                    ((D[offset + 2 + 4 * i] & 0xFF) << 16) |
                    ((D[offset + 1 + 4 * i] & 0xFF) << 8) |
                    ((D[offset + 0 + 4 * i] & 0xFF));
        }


        /* PASS 1: */
        temp[7] = ROTR(F1(messageDigest[1], messageDigest[0], messageDigest[3], messageDigest[5], messageDigest[6], messageDigest[2], messageDigest[4]), 7) + ROTR(messageDigest[7], 11) + W[ 0];
        temp[6] = ROTR(F1(messageDigest[0], temp[7], messageDigest[2], messageDigest[4], messageDigest[5], messageDigest[1], messageDigest[3]), 7) + ROTR(messageDigest[6], 11) + W[ 1];
        temp[5] = ROTR(F1(temp[7], temp[6], messageDigest[1], messageDigest[3], messageDigest[4], messageDigest[0], messageDigest[2]), 7) + ROTR(messageDigest[5], 11) + W[ 2];
        temp[4] = ROTR(F1(temp[6], temp[5], messageDigest[0], messageDigest[2], messageDigest[3], temp[7], messageDigest[1]), 7) + ROTR(messageDigest[4], 11) + W[ 3];
        temp[3] = ROTR(F1(temp[5], temp[4], temp[7], messageDigest[1], messageDigest[2], temp[6], messageDigest[0]), 7) + ROTR(messageDigest[3], 11) + W[ 4];
        temp[2] = ROTR(F1(temp[4], temp[3], temp[6], messageDigest[0], messageDigest[1], temp[5], temp[7]), 7) + ROTR(messageDigest[2], 11) + W[ 5];
        temp[1] = ROTR(F1(temp[3], temp[2], temp[5], temp[7], messageDigest[0], temp[4], temp[6]), 7) + ROTR(messageDigest[1], 11) + W[ 6];
        temp[0] = ROTR(F1(temp[2], temp[1], temp[4], temp[6], temp[7], temp[3], temp[5]), 7) + ROTR(messageDigest[0], 11) + W[ 7];

        temp[7] = ROTR(F1(temp[1], temp[0], temp[3], temp[5], temp[6], temp[2], temp[4]), 7) + ROTR(temp[7], 11) + W[ 8];
        temp[6] = ROTR(F1(temp[0], temp[7], temp[2], temp[4], temp[5], temp[1], temp[3]), 7) + ROTR(temp[6], 11) + W[ 9];
        temp[5] = ROTR(F1(temp[7], temp[6], temp[1], temp[3], temp[4], temp[0], temp[2]), 7) + ROTR(temp[5], 11) + W[10];
        temp[4] = ROTR(F1(temp[6], temp[5], temp[0], temp[2], temp[3], temp[7], temp[1]), 7) + ROTR(temp[4], 11) + W[11];
        temp[3] = ROTR(F1(temp[5], temp[4], temp[7], temp[1], temp[2], temp[6], temp[0]), 7) + ROTR(temp[3], 11) + W[12];
        temp[2] = ROTR(F1(temp[4], temp[3], temp[6], temp[0], temp[1], temp[5], temp[7]), 7) + ROTR(temp[2], 11) + W[13];
        temp[1] = ROTR(F1(temp[3], temp[2], temp[5], temp[7], temp[0], temp[4], temp[6]), 7) + ROTR(temp[1], 11) + W[14];
        temp[0] = ROTR(F1(temp[2], temp[1], temp[4], temp[6], temp[7], temp[3], temp[5]), 7) + ROTR(temp[0], 11) + W[15];

        temp[7] = ROTR(F1(temp[1], temp[0], temp[3], temp[5], temp[6], temp[2], temp[4]), 7) + ROTR(temp[7], 11) + W[16];
        temp[6] = ROTR(F1(temp[0], temp[7], temp[2], temp[4], temp[5], temp[1], temp[3]), 7) + ROTR(temp[6], 11) + W[17];
        temp[5] = ROTR(F1(temp[7], temp[6], temp[1], temp[3], temp[4], temp[0], temp[2]), 7) + ROTR(temp[5], 11) + W[18];
        temp[4] = ROTR(F1(temp[6], temp[5], temp[0], temp[2], temp[3], temp[7], temp[1]), 7) + ROTR(temp[4], 11) + W[19];
        temp[3] = ROTR(F1(temp[5], temp[4], temp[7], temp[1], temp[2], temp[6], temp[0]), 7) + ROTR(temp[3], 11) + W[20];
        temp[2] = ROTR(F1(temp[4], temp[3], temp[6], temp[0], temp[1], temp[5], temp[7]), 7) + ROTR(temp[2], 11) + W[21];
        temp[1] = ROTR(F1(temp[3], temp[2], temp[5], temp[7], temp[0], temp[4], temp[6]), 7) + ROTR(temp[1], 11) + W[22];
        temp[0] = ROTR(F1(temp[2], temp[1], temp[4], temp[6], temp[7], temp[3], temp[5]), 7) + ROTR(temp[0], 11) + W[23];

        temp[7] = ROTR(F1(temp[1], temp[0], temp[3], temp[5], temp[6], temp[2], temp[4]), 7) + ROTR(temp[7], 11) + W[24];
        temp[6] = ROTR(F1(temp[0], temp[7], temp[2], temp[4], temp[5], temp[1], temp[3]), 7) + ROTR(temp[6], 11) + W[25];
        temp[5] = ROTR(F1(temp[7], temp[6], temp[1], temp[3], temp[4], temp[0], temp[2]), 7) + ROTR(temp[5], 11) + W[26];
        temp[4] = ROTR(F1(temp[6], temp[5], temp[0], temp[2], temp[3], temp[7], temp[1]), 7) + ROTR(temp[4], 11) + W[27];
        temp[3] = ROTR(F1(temp[5], temp[4], temp[7], temp[1], temp[2], temp[6], temp[0]), 7) + ROTR(temp[3], 11) + W[28];
        temp[2] = ROTR(F1(temp[4], temp[3], temp[6], temp[0], temp[1], temp[5], temp[7]), 7) + ROTR(temp[2], 11) + W[29];
        temp[1] = ROTR(F1(temp[3], temp[2], temp[5], temp[7], temp[0], temp[4], temp[6]), 7) + ROTR(temp[1], 11) + W[30];
        temp[0] = ROTR(F1(temp[2], temp[1], temp[4], temp[6], temp[7], temp[3], temp[5]), 7) + ROTR(temp[0], 11) + W[31];

        /* PASS 2: */

        temp[7] = ROTR(F2(temp[4], temp[2], temp[1], temp[0], temp[5], temp[3], temp[6]), 7) + ROTR(temp[7], 11) + W[ 5] + 0x452821E6;
        temp[6] = ROTR(F2(temp[3], temp[1], temp[0], temp[7], temp[4], temp[2], temp[5]), 7) + ROTR(temp[6], 11) + W[14] + 0x38D01377;
        temp[5] = ROTR(F2(temp[2], temp[0], temp[7], temp[6], temp[3], temp[1], temp[4]), 7) + ROTR(temp[5], 11) + W[26] + 0xBE5466CF;
        temp[4] = ROTR(F2(temp[1], temp[7], temp[6], temp[5], temp[2], temp[0], temp[3]), 7) + ROTR(temp[4], 11) + W[18] + 0x34E90C6C;
        temp[3] = ROTR(F2(temp[0], temp[6], temp[5], temp[4], temp[1], temp[7], temp[2]), 7) + ROTR(temp[3], 11) + W[11] + 0xC0AC29B7;
        temp[2] = ROTR(F2(temp[7], temp[5], temp[4], temp[3], temp[0], temp[6], temp[1]), 7) + ROTR(temp[2], 11) + W[28] + 0xC97C50DD;
        temp[1] = ROTR(F2(temp[6], temp[4], temp[3], temp[2], temp[7], temp[5], temp[0]), 7) + ROTR(temp[1], 11) + W[ 7] + 0x3F84D5B5;
        temp[0] = ROTR(F2(temp[5], temp[3], temp[2], temp[1], temp[6], temp[4], temp[7]), 7) + ROTR(temp[0], 11) + W[16] + 0xB5470917;

        temp[7] = ROTR(F2(temp[4], temp[2], temp[1], temp[0], temp[5], temp[3], temp[6]), 7) + ROTR(temp[7], 11) + W[ 0] + 0x9216D5D9;
        temp[6] = ROTR(F2(temp[3], temp[1], temp[0], temp[7], temp[4], temp[2], temp[5]), 7) + ROTR(temp[6], 11) + W[23] + 0x8979FB1B;
        temp[5] = ROTR(F2(temp[2], temp[0], temp[7], temp[6], temp[3], temp[1], temp[4]), 7) + ROTR(temp[5], 11) + W[20] + 0xD1310BA6;
        temp[4] = ROTR(F2(temp[1], temp[7], temp[6], temp[5], temp[2], temp[0], temp[3]), 7) + ROTR(temp[4], 11) + W[22] + 0x98DFB5AC;
        temp[3] = ROTR(F2(temp[0], temp[6], temp[5], temp[4], temp[1], temp[7], temp[2]), 7) + ROTR(temp[3], 11) + W[ 1] + 0x2FFD72DB;
        temp[2] = ROTR(F2(temp[7], temp[5], temp[4], temp[3], temp[0], temp[6], temp[1]), 7) + ROTR(temp[2], 11) + W[10] + 0xD01ADFB7;
        temp[1] = ROTR(F2(temp[6], temp[4], temp[3], temp[2], temp[7], temp[5], temp[0]), 7) + ROTR(temp[1], 11) + W[ 4] + 0xB8E1AFED;
        temp[0] = ROTR(F2(temp[5], temp[3], temp[2], temp[1], temp[6], temp[4], temp[7]), 7) + ROTR(temp[0], 11) + W[ 8] + 0x6A267E96;

        temp[7] = ROTR(F2(temp[4], temp[2], temp[1], temp[0], temp[5], temp[3], temp[6]), 7) + ROTR(temp[7], 11) + W[30] + 0xBA7C9045;
        temp[6] = ROTR(F2(temp[3], temp[1], temp[0], temp[7], temp[4], temp[2], temp[5]), 7) + ROTR(temp[6], 11) + W[ 3] + 0xF12C7F99;
        temp[5] = ROTR(F2(temp[2], temp[0], temp[7], temp[6], temp[3], temp[1], temp[4]), 7) + ROTR(temp[5], 11) + W[21] + 0x24A19947;
        temp[4] = ROTR(F2(temp[1], temp[7], temp[6], temp[5], temp[2], temp[0], temp[3]), 7) + ROTR(temp[4], 11) + W[ 9] + 0xB3916CF7;
        temp[3] = ROTR(F2(temp[0], temp[6], temp[5], temp[4], temp[1], temp[7], temp[2]), 7) + ROTR(temp[3], 11) + W[17] + 0x0801F2E2;
        temp[2] = ROTR(F2(temp[7], temp[5], temp[4], temp[3], temp[0], temp[6], temp[1]), 7) + ROTR(temp[2], 11) + W[24] + 0x858EFC16;
        temp[1] = ROTR(F2(temp[6], temp[4], temp[3], temp[2], temp[7], temp[5], temp[0]), 7) + ROTR(temp[1], 11) + W[29] + 0x636920D8;
        temp[0] = ROTR(F2(temp[5], temp[3], temp[2], temp[1], temp[6], temp[4], temp[7]), 7) + ROTR(temp[0], 11) + W[ 6] + 0x71574E69;

        temp[7] = ROTR(F2(temp[4], temp[2], temp[1], temp[0], temp[5], temp[3], temp[6]), 7) + ROTR(temp[7], 11) + W[19] + 0xA458FEA3;
        temp[6] = ROTR(F2(temp[3], temp[1], temp[0], temp[7], temp[4], temp[2], temp[5]), 7) + ROTR(temp[6], 11) + W[12] + 0xF4933D7E;
        temp[5] = ROTR(F2(temp[2], temp[0], temp[7], temp[6], temp[3], temp[1], temp[4]), 7) + ROTR(temp[5], 11) + W[15] + 0x0D95748F;
        temp[4] = ROTR(F2(temp[1], temp[7], temp[6], temp[5], temp[2], temp[0], temp[3]), 7) + ROTR(temp[4], 11) + W[13] + 0x728EB658;
        temp[3] = ROTR(F2(temp[0], temp[6], temp[5], temp[4], temp[1], temp[7], temp[2]), 7) + ROTR(temp[3], 11) + W[ 2] + 0x718BCD58;
        temp[2] = ROTR(F2(temp[7], temp[5], temp[4], temp[3], temp[0], temp[6], temp[1]), 7) + ROTR(temp[2], 11) + W[25] + 0x82154AEE;
        temp[1] = ROTR(F2(temp[6], temp[4], temp[3], temp[2], temp[7], temp[5], temp[0]), 7) + ROTR(temp[1], 11) + W[31] + 0x7B54A41D;
        temp[0] = ROTR(F2(temp[5], temp[3], temp[2], temp[1], temp[6], temp[4], temp[7]), 7) + ROTR(temp[0], 11) + W[27] + 0xC25A59B5;
//        for (i = 0; i < 8; ++i) {
//            System.out.print(" " + Integer.toHexString(temp[i]));
//        }
//        System.out.println("");

        /* PASS 3: */

        temp[7] = ROTR(F3(temp[6], temp[1], temp[2], temp[3], temp[4], temp[5], temp[0]), 7) + ROTR(temp[7], 11) + W[19] + 0x9C30D539;
        temp[6] = ROTR(F3(temp[5], temp[0], temp[1], temp[2], temp[3], temp[4], temp[7]), 7) + ROTR(temp[6], 11) + W[ 9] + 0x2AF26013;
        temp[5] = ROTR(F3(temp[4], temp[7], temp[0], temp[1], temp[2], temp[3], temp[6]), 7) + ROTR(temp[5], 11) + W[ 4] + 0xC5D1B023;
        temp[4] = ROTR(F3(temp[3], temp[6], temp[7], temp[0], temp[1], temp[2], temp[5]), 7) + ROTR(temp[4], 11) + W[20] + 0x286085F0;
        temp[3] = ROTR(F3(temp[2], temp[5], temp[6], temp[7], temp[0], temp[1], temp[4]), 7) + ROTR(temp[3], 11) + W[28] + 0xCA417918;
        temp[2] = ROTR(F3(temp[1], temp[4], temp[5], temp[6], temp[7], temp[0], temp[3]), 7) + ROTR(temp[2], 11) + W[17] + 0xB8DB38EF;
        temp[1] = ROTR(F3(temp[0], temp[3], temp[4], temp[5], temp[6], temp[7], temp[2]), 7) + ROTR(temp[1], 11) + W[ 8] + 0x8E79DCB0;
        temp[0] = ROTR(F3(temp[7], temp[2], temp[3], temp[4], temp[5], temp[6], temp[1]), 7) + ROTR(temp[0], 11) + W[22] + 0x603A180E;

        temp[7] = ROTR(F3(temp[6], temp[1], temp[2], temp[3], temp[4], temp[5], temp[0]), 7) + ROTR(temp[7], 11) + W[29] + 0x6C9E0E8B;
        temp[6] = ROTR(F3(temp[5], temp[0], temp[1], temp[2], temp[3], temp[4], temp[7]), 7) + ROTR(temp[6], 11) + W[14] + 0xB01E8A3E;
        temp[5] = ROTR(F3(temp[4], temp[7], temp[0], temp[1], temp[2], temp[3], temp[6]), 7) + ROTR(temp[5], 11) + W[25] + 0xD71577C1;
        temp[4] = ROTR(F3(temp[3], temp[6], temp[7], temp[0], temp[1], temp[2], temp[5]), 7) + ROTR(temp[4], 11) + W[12] + 0xBD314B27;
        temp[3] = ROTR(F3(temp[2], temp[5], temp[6], temp[7], temp[0], temp[1], temp[4]), 7) + ROTR(temp[3], 11) + W[24] + 0x78AF2FDA;
        temp[2] = ROTR(F3(temp[1], temp[4], temp[5], temp[6], temp[7], temp[0], temp[3]), 7) + ROTR(temp[2], 11) + W[30] + 0x55605C60;
        temp[1] = ROTR(F3(temp[0], temp[3], temp[4], temp[5], temp[6], temp[7], temp[2]), 7) + ROTR(temp[1], 11) + W[16] + 0xE65525F3;
        temp[0] = ROTR(F3(temp[7], temp[2], temp[3], temp[4], temp[5], temp[6], temp[1]), 7) + ROTR(temp[0], 11) + W[26] + 0xAA55AB94;

        temp[7] = ROTR(F3(temp[6], temp[1], temp[2], temp[3], temp[4], temp[5], temp[0]), 7) + ROTR(temp[7], 11) + W[31] + 0x57489862;
        temp[6] = ROTR(F3(temp[5], temp[0], temp[1], temp[2], temp[3], temp[4], temp[7]), 7) + ROTR(temp[6], 11) + W[15] + 0x63E81440;
        temp[5] = ROTR(F3(temp[4], temp[7], temp[0], temp[1], temp[2], temp[3], temp[6]), 7) + ROTR(temp[5], 11) + W[ 7] + 0x55CA396A;
        temp[4] = ROTR(F3(temp[3], temp[6], temp[7], temp[0], temp[1], temp[2], temp[5]), 7) + ROTR(temp[4], 11) + W[ 3] + 0x2AAB10B6;
        temp[3] = ROTR(F3(temp[2], temp[5], temp[6], temp[7], temp[0], temp[1], temp[4]), 7) + ROTR(temp[3], 11) + W[ 1] + 0xB4CC5C34;
        temp[2] = ROTR(F3(temp[1], temp[4], temp[5], temp[6], temp[7], temp[0], temp[3]), 7) + ROTR(temp[2], 11) + W[ 0] + 0x1141E8CE;
        temp[1] = ROTR(F3(temp[0], temp[3], temp[4], temp[5], temp[6], temp[7], temp[2]), 7) + ROTR(temp[1], 11) + W[18] + 0xA15486AF;
        temp[0] = ROTR(F3(temp[7], temp[2], temp[3], temp[4], temp[5], temp[6], temp[1]), 7) + ROTR(temp[0], 11) + W[27] + 0x7C72E993;

        messageDigest[7] += temp[7] = ROTR(F3(temp[6], temp[1], temp[2], temp[3], temp[4], temp[5], temp[0]), 7) + ROTR(temp[7], 11) + W[13] + 0xB3EE1411;
        messageDigest[6] += temp[6] = ROTR(F3(temp[5], temp[0], temp[1], temp[2], temp[3], temp[4], temp[7]), 7) + ROTR(temp[6], 11) + W[ 6] + 0x636FBC2A;
        messageDigest[5] += temp[5] = ROTR(F3(temp[4], temp[7], temp[0], temp[1], temp[2], temp[3], temp[6]), 7) + ROTR(temp[5], 11) + W[21] + 0x2BA9C55D;
        messageDigest[4] += temp[4] = ROTR(F3(temp[3], temp[6], temp[7], temp[0], temp[1], temp[2], temp[5]), 7) + ROTR(temp[4], 11) + W[10] + 0x741831F6;
        messageDigest[3] += temp[3] = ROTR(F3(temp[2], temp[5], temp[6], temp[7], temp[0], temp[1], temp[4]), 7) + ROTR(temp[3], 11) + W[23] + 0xCE5C3E16;
        messageDigest[2] += temp[2] = ROTR(F3(temp[1], temp[4], temp[5], temp[6], temp[7], temp[0], temp[3]), 7) + ROTR(temp[2], 11) + W[11] + 0x9B87931E;
        messageDigest[1] += temp[1] = ROTR(F3(temp[0], temp[3], temp[4], temp[5], temp[6], temp[7], temp[2]), 7) + ROTR(temp[1], 11) + W[ 5] + 0xAFD6BA33;
        messageDigest[0] += temp[0] = ROTR(F3(temp[7], temp[2], temp[3], temp[4], temp[5], temp[6], temp[1]), 7) + ROTR(temp[0], 11) + W[ 2] + 0x6C24CF5C;

        for (int i = 0; i < 32; ++i) {
            W[i] = 0;
        }
    } /* havalTransform3 */


    private void transform4(final byte[] D, final int offset) { //NOPMD complex
        final int[] W = new int[32]; // NOPMD name
        for (int i = 0; i < 32; ++i) {
            W[i] =
                    ((D[offset + 3 + 4 * i] & 0xFF) << 24) |
                    ((D[offset + 2 + 4 * i] & 0xFF) << 16) |
                    ((D[offset + 1 + 4 * i] & 0xFF) << 8) |
                    ((D[offset + 0 + 4 * i] & 0xFF));
        }

        /* PASS 1: */

        temp[7] = ROTR(F1(messageDigest[2], messageDigest[6], messageDigest[1], messageDigest[4], messageDigest[5], messageDigest[3], messageDigest[0]), 7) + ROTR(messageDigest[7], 11) + W[ 0];
        temp[6] = ROTR(F1(messageDigest[1], messageDigest[5], messageDigest[0], messageDigest[3], messageDigest[4], messageDigest[2], temp[7]), 7) + ROTR(messageDigest[6], 11) + W[ 1];
        temp[5] = ROTR(F1(messageDigest[0], messageDigest[4], temp[7], messageDigest[2], messageDigest[3], messageDigest[1], temp[6]), 7) + ROTR(messageDigest[5], 11) + W[ 2];
        temp[4] = ROTR(F1(temp[7], messageDigest[3], temp[6], messageDigest[1], messageDigest[2], messageDigest[0], temp[5]), 7) + ROTR(messageDigest[4], 11) + W[ 3];
        temp[3] = ROTR(F1(temp[6], messageDigest[2], temp[5], messageDigest[0], messageDigest[1], temp[7], temp[4]), 7) + ROTR(messageDigest[3], 11) + W[ 4];
        temp[2] = ROTR(F1(temp[5], messageDigest[1], temp[4], temp[7], messageDigest[0], temp[6], temp[3]), 7) + ROTR(messageDigest[2], 11) + W[ 5];
        temp[1] = ROTR(F1(temp[4], messageDigest[0], temp[3], temp[6], temp[7], temp[5], temp[2]), 7) + ROTR(messageDigest[1], 11) + W[ 6];
        temp[0] = ROTR(F1(temp[3], temp[7], temp[2], temp[5], temp[6], temp[4], temp[1]), 7) + ROTR(messageDigest[0], 11) + W[ 7];

        temp[7] = ROTR(F1(temp[2], temp[6], temp[1], temp[4], temp[5], temp[3], temp[0]), 7) + ROTR(temp[7], 11) + W[ 8];
        temp[6] = ROTR(F1(temp[1], temp[5], temp[0], temp[3], temp[4], temp[2], temp[7]), 7) + ROTR(temp[6], 11) + W[ 9];
        temp[5] = ROTR(F1(temp[0], temp[4], temp[7], temp[2], temp[3], temp[1], temp[6]), 7) + ROTR(temp[5], 11) + W[10];
        temp[4] = ROTR(F1(temp[7], temp[3], temp[6], temp[1], temp[2], temp[0], temp[5]), 7) + ROTR(temp[4], 11) + W[11];
        temp[3] = ROTR(F1(temp[6], temp[2], temp[5], temp[0], temp[1], temp[7], temp[4]), 7) + ROTR(temp[3], 11) + W[12];
        temp[2] = ROTR(F1(temp[5], temp[1], temp[4], temp[7], temp[0], temp[6], temp[3]), 7) + ROTR(temp[2], 11) + W[13];
        temp[1] = ROTR(F1(temp[4], temp[0], temp[3], temp[6], temp[7], temp[5], temp[2]), 7) + ROTR(temp[1], 11) + W[14];
        temp[0] = ROTR(F1(temp[3], temp[7], temp[2], temp[5], temp[6], temp[4], temp[1]), 7) + ROTR(temp[0], 11) + W[15];

        temp[7] = ROTR(F1(temp[2], temp[6], temp[1], temp[4], temp[5], temp[3], temp[0]), 7) + ROTR(temp[7], 11) + W[16];
        temp[6] = ROTR(F1(temp[1], temp[5], temp[0], temp[3], temp[4], temp[2], temp[7]), 7) + ROTR(temp[6], 11) + W[17];
        temp[5] = ROTR(F1(temp[0], temp[4], temp[7], temp[2], temp[3], temp[1], temp[6]), 7) + ROTR(temp[5], 11) + W[18];
        temp[4] = ROTR(F1(temp[7], temp[3], temp[6], temp[1], temp[2], temp[0], temp[5]), 7) + ROTR(temp[4], 11) + W[19];
        temp[3] = ROTR(F1(temp[6], temp[2], temp[5], temp[0], temp[1], temp[7], temp[4]), 7) + ROTR(temp[3], 11) + W[20];
        temp[2] = ROTR(F1(temp[5], temp[1], temp[4], temp[7], temp[0], temp[6], temp[3]), 7) + ROTR(temp[2], 11) + W[21];
        temp[1] = ROTR(F1(temp[4], temp[0], temp[3], temp[6], temp[7], temp[5], temp[2]), 7) + ROTR(temp[1], 11) + W[22];
        temp[0] = ROTR(F1(temp[3], temp[7], temp[2], temp[5], temp[6], temp[4], temp[1]), 7) + ROTR(temp[0], 11) + W[23];

        temp[7] = ROTR(F1(temp[2], temp[6], temp[1], temp[4], temp[5], temp[3], temp[0]), 7) + ROTR(temp[7], 11) + W[24];
        temp[6] = ROTR(F1(temp[1], temp[5], temp[0], temp[3], temp[4], temp[2], temp[7]), 7) + ROTR(temp[6], 11) + W[25];
        temp[5] = ROTR(F1(temp[0], temp[4], temp[7], temp[2], temp[3], temp[1], temp[6]), 7) + ROTR(temp[5], 11) + W[26];
        temp[4] = ROTR(F1(temp[7], temp[3], temp[6], temp[1], temp[2], temp[0], temp[5]), 7) + ROTR(temp[4], 11) + W[27];
        temp[3] = ROTR(F1(temp[6], temp[2], temp[5], temp[0], temp[1], temp[7], temp[4]), 7) + ROTR(temp[3], 11) + W[28];
        temp[2] = ROTR(F1(temp[5], temp[1], temp[4], temp[7], temp[0], temp[6], temp[3]), 7) + ROTR(temp[2], 11) + W[29];
        temp[1] = ROTR(F1(temp[4], temp[0], temp[3], temp[6], temp[7], temp[5], temp[2]), 7) + ROTR(temp[1], 11) + W[30];
        temp[0] = ROTR(F1(temp[3], temp[7], temp[2], temp[5], temp[6], temp[4], temp[1]), 7) + ROTR(temp[0], 11) + W[31];

        /* PASS 2: */

        temp[7] = ROTR(F2(temp[3], temp[5], temp[2], temp[0], temp[1], temp[6], temp[4]), 7) + ROTR(temp[7], 11) + W[ 5] + 0x452821E6;
        temp[6] = ROTR(F2(temp[2], temp[4], temp[1], temp[7], temp[0], temp[5], temp[3]), 7) + ROTR(temp[6], 11) + W[14] + 0x38D01377;
        temp[5] = ROTR(F2(temp[1], temp[3], temp[0], temp[6], temp[7], temp[4], temp[2]), 7) + ROTR(temp[5], 11) + W[26] + 0xBE5466CF;
        temp[4] = ROTR(F2(temp[0], temp[2], temp[7], temp[5], temp[6], temp[3], temp[1]), 7) + ROTR(temp[4], 11) + W[18] + 0x34E90C6C;
        temp[3] = ROTR(F2(temp[7], temp[1], temp[6], temp[4], temp[5], temp[2], temp[0]), 7) + ROTR(temp[3], 11) + W[11] + 0xC0AC29B7;
        temp[2] = ROTR(F2(temp[6], temp[0], temp[5], temp[3], temp[4], temp[1], temp[7]), 7) + ROTR(temp[2], 11) + W[28] + 0xC97C50DD;
        temp[1] = ROTR(F2(temp[5], temp[7], temp[4], temp[2], temp[3], temp[0], temp[6]), 7) + ROTR(temp[1], 11) + W[ 7] + 0x3F84D5B5;
        temp[0] = ROTR(F2(temp[4], temp[6], temp[3], temp[1], temp[2], temp[7], temp[5]), 7) + ROTR(temp[0], 11) + W[16] + 0xB5470917;

        temp[7] = ROTR(F2(temp[3], temp[5], temp[2], temp[0], temp[1], temp[6], temp[4]), 7) + ROTR(temp[7], 11) + W[ 0] + 0x9216D5D9;
        temp[6] = ROTR(F2(temp[2], temp[4], temp[1], temp[7], temp[0], temp[5], temp[3]), 7) + ROTR(temp[6], 11) + W[23] + 0x8979FB1B;
        temp[5] = ROTR(F2(temp[1], temp[3], temp[0], temp[6], temp[7], temp[4], temp[2]), 7) + ROTR(temp[5], 11) + W[20] + 0xD1310BA6;
        temp[4] = ROTR(F2(temp[0], temp[2], temp[7], temp[5], temp[6], temp[3], temp[1]), 7) + ROTR(temp[4], 11) + W[22] + 0x98DFB5AC;
        temp[3] = ROTR(F2(temp[7], temp[1], temp[6], temp[4], temp[5], temp[2], temp[0]), 7) + ROTR(temp[3], 11) + W[ 1] + 0x2FFD72DB;
        temp[2] = ROTR(F2(temp[6], temp[0], temp[5], temp[3], temp[4], temp[1], temp[7]), 7) + ROTR(temp[2], 11) + W[10] + 0xD01ADFB7;
        temp[1] = ROTR(F2(temp[5], temp[7], temp[4], temp[2], temp[3], temp[0], temp[6]), 7) + ROTR(temp[1], 11) + W[ 4] + 0xB8E1AFED;
        temp[0] = ROTR(F2(temp[4], temp[6], temp[3], temp[1], temp[2], temp[7], temp[5]), 7) + ROTR(temp[0], 11) + W[ 8] + 0x6A267E96;

        temp[7] = ROTR(F2(temp[3], temp[5], temp[2], temp[0], temp[1], temp[6], temp[4]), 7) + ROTR(temp[7], 11) + W[30] + 0xBA7C9045;
        temp[6] = ROTR(F2(temp[2], temp[4], temp[1], temp[7], temp[0], temp[5], temp[3]), 7) + ROTR(temp[6], 11) + W[ 3] + 0xF12C7F99;
        temp[5] = ROTR(F2(temp[1], temp[3], temp[0], temp[6], temp[7], temp[4], temp[2]), 7) + ROTR(temp[5], 11) + W[21] + 0x24A19947;
        temp[4] = ROTR(F2(temp[0], temp[2], temp[7], temp[5], temp[6], temp[3], temp[1]), 7) + ROTR(temp[4], 11) + W[ 9] + 0xB3916CF7;
        temp[3] = ROTR(F2(temp[7], temp[1], temp[6], temp[4], temp[5], temp[2], temp[0]), 7) + ROTR(temp[3], 11) + W[17] + 0x0801F2E2;
        temp[2] = ROTR(F2(temp[6], temp[0], temp[5], temp[3], temp[4], temp[1], temp[7]), 7) + ROTR(temp[2], 11) + W[24] + 0x858EFC16;
        temp[1] = ROTR(F2(temp[5], temp[7], temp[4], temp[2], temp[3], temp[0], temp[6]), 7) + ROTR(temp[1], 11) + W[29] + 0x636920D8;
        temp[0] = ROTR(F2(temp[4], temp[6], temp[3], temp[1], temp[2], temp[7], temp[5]), 7) + ROTR(temp[0], 11) + W[ 6] + 0x71574E69;

        temp[7] = ROTR(F2(temp[3], temp[5], temp[2], temp[0], temp[1], temp[6], temp[4]), 7) + ROTR(temp[7], 11) + W[19] + 0xA458FEA3;
        temp[6] = ROTR(F2(temp[2], temp[4], temp[1], temp[7], temp[0], temp[5], temp[3]), 7) + ROTR(temp[6], 11) + W[12] + 0xF4933D7E;
        temp[5] = ROTR(F2(temp[1], temp[3], temp[0], temp[6], temp[7], temp[4], temp[2]), 7) + ROTR(temp[5], 11) + W[15] + 0x0D95748F;
        temp[4] = ROTR(F2(temp[0], temp[2], temp[7], temp[5], temp[6], temp[3], temp[1]), 7) + ROTR(temp[4], 11) + W[13] + 0x728EB658;
        temp[3] = ROTR(F2(temp[7], temp[1], temp[6], temp[4], temp[5], temp[2], temp[0]), 7) + ROTR(temp[3], 11) + W[ 2] + 0x718BCD58;
        temp[2] = ROTR(F2(temp[6], temp[0], temp[5], temp[3], temp[4], temp[1], temp[7]), 7) + ROTR(temp[2], 11) + W[25] + 0x82154AEE;
        temp[1] = ROTR(F2(temp[5], temp[7], temp[4], temp[2], temp[3], temp[0], temp[6]), 7) + ROTR(temp[1], 11) + W[31] + 0x7B54A41D;
        temp[0] = ROTR(F2(temp[4], temp[6], temp[3], temp[1], temp[2], temp[7], temp[5]), 7) + ROTR(temp[0], 11) + W[27] + 0xC25A59B5;

        /* PASS 3: */

        temp[7] = ROTR(F3(temp[1], temp[4], temp[3], temp[6], temp[0], temp[2], temp[5]), 7) + ROTR(temp[7], 11) + W[19] + 0x9C30D539;
        temp[6] = ROTR(F3(temp[0], temp[3], temp[2], temp[5], temp[7], temp[1], temp[4]), 7) + ROTR(temp[6], 11) + W[ 9] + 0x2AF26013;
        temp[5] = ROTR(F3(temp[7], temp[2], temp[1], temp[4], temp[6], temp[0], temp[3]), 7) + ROTR(temp[5], 11) + W[ 4] + 0xC5D1B023;
        temp[4] = ROTR(F3(temp[6], temp[1], temp[0], temp[3], temp[5], temp[7], temp[2]), 7) + ROTR(temp[4], 11) + W[20] + 0x286085F0;
        temp[3] = ROTR(F3(temp[5], temp[0], temp[7], temp[2], temp[4], temp[6], temp[1]), 7) + ROTR(temp[3], 11) + W[28] + 0xCA417918;
        temp[2] = ROTR(F3(temp[4], temp[7], temp[6], temp[1], temp[3], temp[5], temp[0]), 7) + ROTR(temp[2], 11) + W[17] + 0xB8DB38EF;
        temp[1] = ROTR(F3(temp[3], temp[6], temp[5], temp[0], temp[2], temp[4], temp[7]), 7) + ROTR(temp[1], 11) + W[ 8] + 0x8E79DCB0;
        temp[0] = ROTR(F3(temp[2], temp[5], temp[4], temp[7], temp[1], temp[3], temp[6]), 7) + ROTR(temp[0], 11) + W[22] + 0x603A180E;

        temp[7] = ROTR(F3(temp[1], temp[4], temp[3], temp[6], temp[0], temp[2], temp[5]), 7) + ROTR(temp[7], 11) + W[29] + 0x6C9E0E8B;
        temp[6] = ROTR(F3(temp[0], temp[3], temp[2], temp[5], temp[7], temp[1], temp[4]), 7) + ROTR(temp[6], 11) + W[14] + 0xB01E8A3E;
        temp[5] = ROTR(F3(temp[7], temp[2], temp[1], temp[4], temp[6], temp[0], temp[3]), 7) + ROTR(temp[5], 11) + W[25] + 0xD71577C1;
        temp[4] = ROTR(F3(temp[6], temp[1], temp[0], temp[3], temp[5], temp[7], temp[2]), 7) + ROTR(temp[4], 11) + W[12] + 0xBD314B27;
        temp[3] = ROTR(F3(temp[5], temp[0], temp[7], temp[2], temp[4], temp[6], temp[1]), 7) + ROTR(temp[3], 11) + W[24] + 0x78AF2FDA;
        temp[2] = ROTR(F3(temp[4], temp[7], temp[6], temp[1], temp[3], temp[5], temp[0]), 7) + ROTR(temp[2], 11) + W[30] + 0x55605C60;
        temp[1] = ROTR(F3(temp[3], temp[6], temp[5], temp[0], temp[2], temp[4], temp[7]), 7) + ROTR(temp[1], 11) + W[16] + 0xE65525F3;
        temp[0] = ROTR(F3(temp[2], temp[5], temp[4], temp[7], temp[1], temp[3], temp[6]), 7) + ROTR(temp[0], 11) + W[26] + 0xAA55AB94;

        temp[7] = ROTR(F3(temp[1], temp[4], temp[3], temp[6], temp[0], temp[2], temp[5]), 7) + ROTR(temp[7], 11) + W[31] + 0x57489862;
        temp[6] = ROTR(F3(temp[0], temp[3], temp[2], temp[5], temp[7], temp[1], temp[4]), 7) + ROTR(temp[6], 11) + W[15] + 0x63E81440;
        temp[5] = ROTR(F3(temp[7], temp[2], temp[1], temp[4], temp[6], temp[0], temp[3]), 7) + ROTR(temp[5], 11) + W[ 7] + 0x55CA396A;
        temp[4] = ROTR(F3(temp[6], temp[1], temp[0], temp[3], temp[5], temp[7], temp[2]), 7) + ROTR(temp[4], 11) + W[ 3] + 0x2AAB10B6;
        temp[3] = ROTR(F3(temp[5], temp[0], temp[7], temp[2], temp[4], temp[6], temp[1]), 7) + ROTR(temp[3], 11) + W[ 1] + 0xB4CC5C34;
        temp[2] = ROTR(F3(temp[4], temp[7], temp[6], temp[1], temp[3], temp[5], temp[0]), 7) + ROTR(temp[2], 11) + W[ 0] + 0x1141E8CE;
        temp[1] = ROTR(F3(temp[3], temp[6], temp[5], temp[0], temp[2], temp[4], temp[7]), 7) + ROTR(temp[1], 11) + W[18] + 0xA15486AF;
        temp[0] = ROTR(F3(temp[2], temp[5], temp[4], temp[7], temp[1], temp[3], temp[6]), 7) + ROTR(temp[0], 11) + W[27] + 0x7C72E993;

        temp[7] = ROTR(F3(temp[1], temp[4], temp[3], temp[6], temp[0], temp[2], temp[5]), 7) + ROTR(temp[7], 11) + W[13] + 0xB3EE1411;
        temp[6] = ROTR(F3(temp[0], temp[3], temp[2], temp[5], temp[7], temp[1], temp[4]), 7) + ROTR(temp[6], 11) + W[ 6] + 0x636FBC2A;
        temp[5] = ROTR(F3(temp[7], temp[2], temp[1], temp[4], temp[6], temp[0], temp[3]), 7) + ROTR(temp[5], 11) + W[21] + 0x2BA9C55D;
        temp[4] = ROTR(F3(temp[6], temp[1], temp[0], temp[3], temp[5], temp[7], temp[2]), 7) + ROTR(temp[4], 11) + W[10] + 0x741831F6;
        temp[3] = ROTR(F3(temp[5], temp[0], temp[7], temp[2], temp[4], temp[6], temp[1]), 7) + ROTR(temp[3], 11) + W[23] + 0xCE5C3E16;
        temp[2] = ROTR(F3(temp[4], temp[7], temp[6], temp[1], temp[3], temp[5], temp[0]), 7) + ROTR(temp[2], 11) + W[11] + 0x9B87931E;
        temp[1] = ROTR(F3(temp[3], temp[6], temp[5], temp[0], temp[2], temp[4], temp[7]), 7) + ROTR(temp[1], 11) + W[ 5] + 0xAFD6BA33;
        temp[0] = ROTR(F3(temp[2], temp[5], temp[4], temp[7], temp[1], temp[3], temp[6]), 7) + ROTR(temp[0], 11) + W[ 2] + 0x6C24CF5C;

        /* PASS 4: */

        temp[7] = ROTR(F4(temp[6], temp[4], temp[0], temp[5], temp[2], temp[1], temp[3]), 7) + ROTR(temp[7], 11) + W[24] + 0x7A325381;
        temp[6] = ROTR(F4(temp[5], temp[3], temp[7], temp[4], temp[1], temp[0], temp[2]), 7) + ROTR(temp[6], 11) + W[ 4] + 0x28958677;
        temp[5] = ROTR(F4(temp[4], temp[2], temp[6], temp[3], temp[0], temp[7], temp[1]), 7) + ROTR(temp[5], 11) + W[ 0] + 0x3B8F4898;
        temp[4] = ROTR(F4(temp[3], temp[1], temp[5], temp[2], temp[7], temp[6], temp[0]), 7) + ROTR(temp[4], 11) + W[14] + 0x6B4BB9AF;
        temp[3] = ROTR(F4(temp[2], temp[0], temp[4], temp[1], temp[6], temp[5], temp[7]), 7) + ROTR(temp[3], 11) + W[ 2] + 0xC4BFE81B;
        temp[2] = ROTR(F4(temp[1], temp[7], temp[3], temp[0], temp[5], temp[4], temp[6]), 7) + ROTR(temp[2], 11) + W[ 7] + 0x66282193;
        temp[1] = ROTR(F4(temp[0], temp[6], temp[2], temp[7], temp[4], temp[3], temp[5]), 7) + ROTR(temp[1], 11) + W[28] + 0x61D809CC;
        temp[0] = ROTR(F4(temp[7], temp[5], temp[1], temp[6], temp[3], temp[2], temp[4]), 7) + ROTR(temp[0], 11) + W[23] + 0xFB21A991;

        temp[7] = ROTR(F4(temp[6], temp[4], temp[0], temp[5], temp[2], temp[1], temp[3]), 7) + ROTR(temp[7], 11) + W[26] + 0x487CAC60;
        temp[6] = ROTR(F4(temp[5], temp[3], temp[7], temp[4], temp[1], temp[0], temp[2]), 7) + ROTR(temp[6], 11) + W[ 6] + 0x5DEC8032;
        temp[5] = ROTR(F4(temp[4], temp[2], temp[6], temp[3], temp[0], temp[7], temp[1]), 7) + ROTR(temp[5], 11) + W[30] + 0xEF845D5D;
        temp[4] = ROTR(F4(temp[3], temp[1], temp[5], temp[2], temp[7], temp[6], temp[0]), 7) + ROTR(temp[4], 11) + W[20] + 0xE98575B1;
        temp[3] = ROTR(F4(temp[2], temp[0], temp[4], temp[1], temp[6], temp[5], temp[7]), 7) + ROTR(temp[3], 11) + W[18] + 0xDC262302;
        temp[2] = ROTR(F4(temp[1], temp[7], temp[3], temp[0], temp[5], temp[4], temp[6]), 7) + ROTR(temp[2], 11) + W[25] + 0xEB651B88;
        temp[1] = ROTR(F4(temp[0], temp[6], temp[2], temp[7], temp[4], temp[3], temp[5]), 7) + ROTR(temp[1], 11) + W[19] + 0x23893E81;
        temp[0] = ROTR(F4(temp[7], temp[5], temp[1], temp[6], temp[3], temp[2], temp[4]), 7) + ROTR(temp[0], 11) + W[ 3] + 0xD396ACC5;

        temp[7] = ROTR(F4(temp[6], temp[4], temp[0], temp[5], temp[2], temp[1], temp[3]), 7) + ROTR(temp[7], 11) + W[22] + 0x0F6D6FF3;
        temp[6] = ROTR(F4(temp[5], temp[3], temp[7], temp[4], temp[1], temp[0], temp[2]), 7) + ROTR(temp[6], 11) + W[11] + 0x83F44239;
        temp[5] = ROTR(F4(temp[4], temp[2], temp[6], temp[3], temp[0], temp[7], temp[1]), 7) + ROTR(temp[5], 11) + W[31] + 0x2E0B4482;
        temp[4] = ROTR(F4(temp[3], temp[1], temp[5], temp[2], temp[7], temp[6], temp[0]), 7) + ROTR(temp[4], 11) + W[21] + 0xA4842004;
        temp[3] = ROTR(F4(temp[2], temp[0], temp[4], temp[1], temp[6], temp[5], temp[7]), 7) + ROTR(temp[3], 11) + W[ 8] + 0x69C8F04A;
        temp[2] = ROTR(F4(temp[1], temp[7], temp[3], temp[0], temp[5], temp[4], temp[6]), 7) + ROTR(temp[2], 11) + W[27] + 0x9E1F9B5E;
        temp[1] = ROTR(F4(temp[0], temp[6], temp[2], temp[7], temp[4], temp[3], temp[5]), 7) + ROTR(temp[1], 11) + W[12] + 0x21C66842;
        temp[0] = ROTR(F4(temp[7], temp[5], temp[1], temp[6], temp[3], temp[2], temp[4]), 7) + ROTR(temp[0], 11) + W[ 9] + 0xF6E96C9A;

        messageDigest[7] += temp[7] = ROTR(F4(temp[6], temp[4], temp[0], temp[5], temp[2], temp[1], temp[3]), 7) + ROTR(temp[7], 11) + W[ 1] + 0x670C9C61;
        messageDigest[6] += temp[6] = ROTR(F4(temp[5], temp[3], temp[7], temp[4], temp[1], temp[0], temp[2]), 7) + ROTR(temp[6], 11) + W[29] + 0xABD388F0;
        messageDigest[5] += temp[5] = ROTR(F4(temp[4], temp[2], temp[6], temp[3], temp[0], temp[7], temp[1]), 7) + ROTR(temp[5], 11) + W[ 5] + 0x6A51A0D2;
        messageDigest[4] += temp[4] = ROTR(F4(temp[3], temp[1], temp[5], temp[2], temp[7], temp[6], temp[0]), 7) + ROTR(temp[4], 11) + W[15] + 0xD8542F68;
        messageDigest[3] += temp[3] = ROTR(F4(temp[2], temp[0], temp[4], temp[1], temp[6], temp[5], temp[7]), 7) + ROTR(temp[3], 11) + W[17] + 0x960FA728;
        messageDigest[2] += temp[2] = ROTR(F4(temp[1], temp[7], temp[3], temp[0], temp[5], temp[4], temp[6]), 7) + ROTR(temp[2], 11) + W[10] + 0xAB5133A3;
        messageDigest[1] += temp[1] = ROTR(F4(temp[0], temp[6], temp[2], temp[7], temp[4], temp[3], temp[5]), 7) + ROTR(temp[1], 11) + W[16] + 0x6EEF0B6C;
        messageDigest[0] += temp[0] = ROTR(F4(temp[7], temp[5], temp[1], temp[6], temp[3], temp[2], temp[4]), 7) + ROTR(temp[0], 11) + W[13] + 0x137A3BE4;

        for (int i = 0; i < 32; ++i) {
            W[i] = 0;
        }
    }

    private void transform5(final byte[] D, final int offset) { //NOPMD complex
        final int[] W = new int[32]; // NOPMD name

        for (int i = 0; i < 32; ++i) {
            W[i] =
                    ((D[offset + 3 + 4 * i] & 0xFF) << 24) |
                    ((D[offset + 2 + 4 * i] & 0xFF) << 16) |
                    ((D[offset + 1 + 4 * i] & 0xFF) << 8) |
                    ((D[offset + 0 + 4 * i] & 0xFF));
        }

        /* PASS 1: */

        temp[7] = ROTR(F1(messageDigest[3], messageDigest[4], messageDigest[1], messageDigest[0], messageDigest[5], messageDigest[2], messageDigest[6]), 7) + ROTR(messageDigest[7], 11) + W[ 0];
        temp[6] = ROTR(F1(messageDigest[2], messageDigest[3], messageDigest[0], temp[7], messageDigest[4], messageDigest[1], messageDigest[5]), 7) + ROTR(messageDigest[6], 11) + W[ 1];
        temp[5] = ROTR(F1(messageDigest[1], messageDigest[2], temp[7], temp[6], messageDigest[3], messageDigest[0], messageDigest[4]), 7) + ROTR(messageDigest[5], 11) + W[ 2];
        temp[4] = ROTR(F1(messageDigest[0], messageDigest[1], temp[6], temp[5], messageDigest[2], temp[7], messageDigest[3]), 7) + ROTR(messageDigest[4], 11) + W[ 3];
        temp[3] = ROTR(F1(temp[7], messageDigest[0], temp[5], temp[4], messageDigest[1], temp[6], messageDigest[2]), 7) + ROTR(messageDigest[3], 11) + W[ 4];
        temp[2] = ROTR(F1(temp[6], temp[7], temp[4], temp[3], messageDigest[0], temp[5], messageDigest[1]), 7) + ROTR(messageDigest[2], 11) + W[ 5];
        temp[1] = ROTR(F1(temp[5], temp[6], temp[3], temp[2], temp[7], temp[4], messageDigest[0]), 7) + ROTR(messageDigest[1], 11) + W[ 6];
        temp[0] = ROTR(F1(temp[4], temp[5], temp[2], temp[1], temp[6], temp[3], temp[7]), 7) + ROTR(messageDigest[0], 11) + W[ 7];

        temp[7] = ROTR(F1(temp[3], temp[4], temp[1], temp[0], temp[5], temp[2], temp[6]), 7) + ROTR(temp[7], 11) + W[ 8];
        temp[6] = ROTR(F1(temp[2], temp[3], temp[0], temp[7], temp[4], temp[1], temp[5]), 7) + ROTR(temp[6], 11) + W[ 9];
        temp[5] = ROTR(F1(temp[1], temp[2], temp[7], temp[6], temp[3], temp[0], temp[4]), 7) + ROTR(temp[5], 11) + W[10];
        temp[4] = ROTR(F1(temp[0], temp[1], temp[6], temp[5], temp[2], temp[7], temp[3]), 7) + ROTR(temp[4], 11) + W[11];
        temp[3] = ROTR(F1(temp[7], temp[0], temp[5], temp[4], temp[1], temp[6], temp[2]), 7) + ROTR(temp[3], 11) + W[12];
        temp[2] = ROTR(F1(temp[6], temp[7], temp[4], temp[3], temp[0], temp[5], temp[1]), 7) + ROTR(temp[2], 11) + W[13];
        temp[1] = ROTR(F1(temp[5], temp[6], temp[3], temp[2], temp[7], temp[4], temp[0]), 7) + ROTR(temp[1], 11) + W[14];
        temp[0] = ROTR(F1(temp[4], temp[5], temp[2], temp[1], temp[6], temp[3], temp[7]), 7) + ROTR(temp[0], 11) + W[15];

        temp[7] = ROTR(F1(temp[3], temp[4], temp[1], temp[0], temp[5], temp[2], temp[6]), 7) + ROTR(temp[7], 11) + W[16];
        temp[6] = ROTR(F1(temp[2], temp[3], temp[0], temp[7], temp[4], temp[1], temp[5]), 7) + ROTR(temp[6], 11) + W[17];
        temp[5] = ROTR(F1(temp[1], temp[2], temp[7], temp[6], temp[3], temp[0], temp[4]), 7) + ROTR(temp[5], 11) + W[18];
        temp[4] = ROTR(F1(temp[0], temp[1], temp[6], temp[5], temp[2], temp[7], temp[3]), 7) + ROTR(temp[4], 11) + W[19];
        temp[3] = ROTR(F1(temp[7], temp[0], temp[5], temp[4], temp[1], temp[6], temp[2]), 7) + ROTR(temp[3], 11) + W[20];
        temp[2] = ROTR(F1(temp[6], temp[7], temp[4], temp[3], temp[0], temp[5], temp[1]), 7) + ROTR(temp[2], 11) + W[21];
        temp[1] = ROTR(F1(temp[5], temp[6], temp[3], temp[2], temp[7], temp[4], temp[0]), 7) + ROTR(temp[1], 11) + W[22];
        temp[0] = ROTR(F1(temp[4], temp[5], temp[2], temp[1], temp[6], temp[3], temp[7]), 7) + ROTR(temp[0], 11) + W[23];

        temp[7] = ROTR(F1(temp[3], temp[4], temp[1], temp[0], temp[5], temp[2], temp[6]), 7) + ROTR(temp[7], 11) + W[24];
        temp[6] = ROTR(F1(temp[2], temp[3], temp[0], temp[7], temp[4], temp[1], temp[5]), 7) + ROTR(temp[6], 11) + W[25];
        temp[5] = ROTR(F1(temp[1], temp[2], temp[7], temp[6], temp[3], temp[0], temp[4]), 7) + ROTR(temp[5], 11) + W[26];
        temp[4] = ROTR(F1(temp[0], temp[1], temp[6], temp[5], temp[2], temp[7], temp[3]), 7) + ROTR(temp[4], 11) + W[27];
        temp[3] = ROTR(F1(temp[7], temp[0], temp[5], temp[4], temp[1], temp[6], temp[2]), 7) + ROTR(temp[3], 11) + W[28];
        temp[2] = ROTR(F1(temp[6], temp[7], temp[4], temp[3], temp[0], temp[5], temp[1]), 7) + ROTR(temp[2], 11) + W[29];
        temp[1] = ROTR(F1(temp[5], temp[6], temp[3], temp[2], temp[7], temp[4], temp[0]), 7) + ROTR(temp[1], 11) + W[30];
        temp[0] = ROTR(F1(temp[4], temp[5], temp[2], temp[1], temp[6], temp[3], temp[7]), 7) + ROTR(temp[0], 11) + W[31];

        /* PASS 2: */

        temp[7] = ROTR(F2(temp[6], temp[2], temp[1], temp[0], temp[3], temp[4], temp[5]), 7) + ROTR(temp[7], 11) + W[ 5] + 0x452821E6;
        temp[6] = ROTR(F2(temp[5], temp[1], temp[0], temp[7], temp[2], temp[3], temp[4]), 7) + ROTR(temp[6], 11) + W[14] + 0x38D01377;
        temp[5] = ROTR(F2(temp[4], temp[0], temp[7], temp[6], temp[1], temp[2], temp[3]), 7) + ROTR(temp[5], 11) + W[26] + 0xBE5466CF;
        temp[4] = ROTR(F2(temp[3], temp[7], temp[6], temp[5], temp[0], temp[1], temp[2]), 7) + ROTR(temp[4], 11) + W[18] + 0x34E90C6C;
        temp[3] = ROTR(F2(temp[2], temp[6], temp[5], temp[4], temp[7], temp[0], temp[1]), 7) + ROTR(temp[3], 11) + W[11] + 0xC0AC29B7;
        temp[2] = ROTR(F2(temp[1], temp[5], temp[4], temp[3], temp[6], temp[7], temp[0]), 7) + ROTR(temp[2], 11) + W[28] + 0xC97C50DD;
        temp[1] = ROTR(F2(temp[0], temp[4], temp[3], temp[2], temp[5], temp[6], temp[7]), 7) + ROTR(temp[1], 11) + W[ 7] + 0x3F84D5B5;
        temp[0] = ROTR(F2(temp[7], temp[3], temp[2], temp[1], temp[4], temp[5], temp[6]), 7) + ROTR(temp[0], 11) + W[16] + 0xB5470917;

        temp[7] = ROTR(F2(temp[6], temp[2], temp[1], temp[0], temp[3], temp[4], temp[5]), 7) + ROTR(temp[7], 11) + W[ 0] + 0x9216D5D9;
        temp[6] = ROTR(F2(temp[5], temp[1], temp[0], temp[7], temp[2], temp[3], temp[4]), 7) + ROTR(temp[6], 11) + W[23] + 0x8979FB1B;
        temp[5] = ROTR(F2(temp[4], temp[0], temp[7], temp[6], temp[1], temp[2], temp[3]), 7) + ROTR(temp[5], 11) + W[20] + 0xD1310BA6;
        temp[4] = ROTR(F2(temp[3], temp[7], temp[6], temp[5], temp[0], temp[1], temp[2]), 7) + ROTR(temp[4], 11) + W[22] + 0x98DFB5AC;
        temp[3] = ROTR(F2(temp[2], temp[6], temp[5], temp[4], temp[7], temp[0], temp[1]), 7) + ROTR(temp[3], 11) + W[ 1] + 0x2FFD72DB;
        temp[2] = ROTR(F2(temp[1], temp[5], temp[4], temp[3], temp[6], temp[7], temp[0]), 7) + ROTR(temp[2], 11) + W[10] + 0xD01ADFB7;
        temp[1] = ROTR(F2(temp[0], temp[4], temp[3], temp[2], temp[5], temp[6], temp[7]), 7) + ROTR(temp[1], 11) + W[ 4] + 0xB8E1AFED;
        temp[0] = ROTR(F2(temp[7], temp[3], temp[2], temp[1], temp[4], temp[5], temp[6]), 7) + ROTR(temp[0], 11) + W[ 8] + 0x6A267E96;

        temp[7] = ROTR(F2(temp[6], temp[2], temp[1], temp[0], temp[3], temp[4], temp[5]), 7) + ROTR(temp[7], 11) + W[30] + 0xBA7C9045;
        temp[6] = ROTR(F2(temp[5], temp[1], temp[0], temp[7], temp[2], temp[3], temp[4]), 7) + ROTR(temp[6], 11) + W[ 3] + 0xF12C7F99;
        temp[5] = ROTR(F2(temp[4], temp[0], temp[7], temp[6], temp[1], temp[2], temp[3]), 7) + ROTR(temp[5], 11) + W[21] + 0x24A19947;
        temp[4] = ROTR(F2(temp[3], temp[7], temp[6], temp[5], temp[0], temp[1], temp[2]), 7) + ROTR(temp[4], 11) + W[ 9] + 0xB3916CF7;
        temp[3] = ROTR(F2(temp[2], temp[6], temp[5], temp[4], temp[7], temp[0], temp[1]), 7) + ROTR(temp[3], 11) + W[17] + 0x0801F2E2;
        temp[2] = ROTR(F2(temp[1], temp[5], temp[4], temp[3], temp[6], temp[7], temp[0]), 7) + ROTR(temp[2], 11) + W[24] + 0x858EFC16;
        temp[1] = ROTR(F2(temp[0], temp[4], temp[3], temp[2], temp[5], temp[6], temp[7]), 7) + ROTR(temp[1], 11) + W[29] + 0x636920D8;
        temp[0] = ROTR(F2(temp[7], temp[3], temp[2], temp[1], temp[4], temp[5], temp[6]), 7) + ROTR(temp[0], 11) + W[ 6] + 0x71574E69;

        temp[7] = ROTR(F2(temp[6], temp[2], temp[1], temp[0], temp[3], temp[4], temp[5]), 7) + ROTR(temp[7], 11) + W[19] + 0xA458FEA3;
        temp[6] = ROTR(F2(temp[5], temp[1], temp[0], temp[7], temp[2], temp[3], temp[4]), 7) + ROTR(temp[6], 11) + W[12] + 0xF4933D7E;
        temp[5] = ROTR(F2(temp[4], temp[0], temp[7], temp[6], temp[1], temp[2], temp[3]), 7) + ROTR(temp[5], 11) + W[15] + 0x0D95748F;
        temp[4] = ROTR(F2(temp[3], temp[7], temp[6], temp[5], temp[0], temp[1], temp[2]), 7) + ROTR(temp[4], 11) + W[13] + 0x728EB658;
        temp[3] = ROTR(F2(temp[2], temp[6], temp[5], temp[4], temp[7], temp[0], temp[1]), 7) + ROTR(temp[3], 11) + W[ 2] + 0x718BCD58;
        temp[2] = ROTR(F2(temp[1], temp[5], temp[4], temp[3], temp[6], temp[7], temp[0]), 7) + ROTR(temp[2], 11) + W[25] + 0x82154AEE;
        temp[1] = ROTR(F2(temp[0], temp[4], temp[3], temp[2], temp[5], temp[6], temp[7]), 7) + ROTR(temp[1], 11) + W[31] + 0x7B54A41D;
        temp[0] = ROTR(F2(temp[7], temp[3], temp[2], temp[1], temp[4], temp[5], temp[6]), 7) + ROTR(temp[0], 11) + W[27] + 0xC25A59B5;

        /* PASS 3: */

        temp[7] = ROTR(F3(temp[2], temp[6], temp[0], temp[4], temp[3], temp[1], temp[5]), 7) + ROTR(temp[7], 11) + W[19] + 0x9C30D539;
        temp[6] = ROTR(F3(temp[1], temp[5], temp[7], temp[3], temp[2], temp[0], temp[4]), 7) + ROTR(temp[6], 11) + W[ 9] + 0x2AF26013;
        temp[5] = ROTR(F3(temp[0], temp[4], temp[6], temp[2], temp[1], temp[7], temp[3]), 7) + ROTR(temp[5], 11) + W[ 4] + 0xC5D1B023;
        temp[4] = ROTR(F3(temp[7], temp[3], temp[5], temp[1], temp[0], temp[6], temp[2]), 7) + ROTR(temp[4], 11) + W[20] + 0x286085F0;
        temp[3] = ROTR(F3(temp[6], temp[2], temp[4], temp[0], temp[7], temp[5], temp[1]), 7) + ROTR(temp[3], 11) + W[28] + 0xCA417918;
        temp[2] = ROTR(F3(temp[5], temp[1], temp[3], temp[7], temp[6], temp[4], temp[0]), 7) + ROTR(temp[2], 11) + W[17] + 0xB8DB38EF;
        temp[1] = ROTR(F3(temp[4], temp[0], temp[2], temp[6], temp[5], temp[3], temp[7]), 7) + ROTR(temp[1], 11) + W[ 8] + 0x8E79DCB0;
        temp[0] = ROTR(F3(temp[3], temp[7], temp[1], temp[5], temp[4], temp[2], temp[6]), 7) + ROTR(temp[0], 11) + W[22] + 0x603A180E;

        temp[7] = ROTR(F3(temp[2], temp[6], temp[0], temp[4], temp[3], temp[1], temp[5]), 7) + ROTR(temp[7], 11) + W[29] + 0x6C9E0E8B;
        temp[6] = ROTR(F3(temp[1], temp[5], temp[7], temp[3], temp[2], temp[0], temp[4]), 7) + ROTR(temp[6], 11) + W[14] + 0xB01E8A3E;
        temp[5] = ROTR(F3(temp[0], temp[4], temp[6], temp[2], temp[1], temp[7], temp[3]), 7) + ROTR(temp[5], 11) + W[25] + 0xD71577C1;
        temp[4] = ROTR(F3(temp[7], temp[3], temp[5], temp[1], temp[0], temp[6], temp[2]), 7) + ROTR(temp[4], 11) + W[12] + 0xBD314B27;
        temp[3] = ROTR(F3(temp[6], temp[2], temp[4], temp[0], temp[7], temp[5], temp[1]), 7) + ROTR(temp[3], 11) + W[24] + 0x78AF2FDA;
        temp[2] = ROTR(F3(temp[5], temp[1], temp[3], temp[7], temp[6], temp[4], temp[0]), 7) + ROTR(temp[2], 11) + W[30] + 0x55605C60;
        temp[1] = ROTR(F3(temp[4], temp[0], temp[2], temp[6], temp[5], temp[3], temp[7]), 7) + ROTR(temp[1], 11) + W[16] + 0xE65525F3;
        temp[0] = ROTR(F3(temp[3], temp[7], temp[1], temp[5], temp[4], temp[2], temp[6]), 7) + ROTR(temp[0], 11) + W[26] + 0xAA55AB94;

        temp[7] = ROTR(F3(temp[2], temp[6], temp[0], temp[4], temp[3], temp[1], temp[5]), 7) + ROTR(temp[7], 11) + W[31] + 0x57489862;
        temp[6] = ROTR(F3(temp[1], temp[5], temp[7], temp[3], temp[2], temp[0], temp[4]), 7) + ROTR(temp[6], 11) + W[15] + 0x63E81440;
        temp[5] = ROTR(F3(temp[0], temp[4], temp[6], temp[2], temp[1], temp[7], temp[3]), 7) + ROTR(temp[5], 11) + W[ 7] + 0x55CA396A;
        temp[4] = ROTR(F3(temp[7], temp[3], temp[5], temp[1], temp[0], temp[6], temp[2]), 7) + ROTR(temp[4], 11) + W[ 3] + 0x2AAB10B6;
        temp[3] = ROTR(F3(temp[6], temp[2], temp[4], temp[0], temp[7], temp[5], temp[1]), 7) + ROTR(temp[3], 11) + W[ 1] + 0xB4CC5C34;
        temp[2] = ROTR(F3(temp[5], temp[1], temp[3], temp[7], temp[6], temp[4], temp[0]), 7) + ROTR(temp[2], 11) + W[ 0] + 0x1141E8CE;
        temp[1] = ROTR(F3(temp[4], temp[0], temp[2], temp[6], temp[5], temp[3], temp[7]), 7) + ROTR(temp[1], 11) + W[18] + 0xA15486AF;
        temp[0] = ROTR(F3(temp[3], temp[7], temp[1], temp[5], temp[4], temp[2], temp[6]), 7) + ROTR(temp[0], 11) + W[27] + 0x7C72E993;

        temp[7] = ROTR(F3(temp[2], temp[6], temp[0], temp[4], temp[3], temp[1], temp[5]), 7) + ROTR(temp[7], 11) + W[13] + 0xB3EE1411;
        temp[6] = ROTR(F3(temp[1], temp[5], temp[7], temp[3], temp[2], temp[0], temp[4]), 7) + ROTR(temp[6], 11) + W[ 6] + 0x636FBC2A;
        temp[5] = ROTR(F3(temp[0], temp[4], temp[6], temp[2], temp[1], temp[7], temp[3]), 7) + ROTR(temp[5], 11) + W[21] + 0x2BA9C55D;
        temp[4] = ROTR(F3(temp[7], temp[3], temp[5], temp[1], temp[0], temp[6], temp[2]), 7) + ROTR(temp[4], 11) + W[10] + 0x741831F6;
        temp[3] = ROTR(F3(temp[6], temp[2], temp[4], temp[0], temp[7], temp[5], temp[1]), 7) + ROTR(temp[3], 11) + W[23] + 0xCE5C3E16;
        temp[2] = ROTR(F3(temp[5], temp[1], temp[3], temp[7], temp[6], temp[4], temp[0]), 7) + ROTR(temp[2], 11) + W[11] + 0x9B87931E;
        temp[1] = ROTR(F3(temp[4], temp[0], temp[2], temp[6], temp[5], temp[3], temp[7]), 7) + ROTR(temp[1], 11) + W[ 5] + 0xAFD6BA33;
        temp[0] = ROTR(F3(temp[3], temp[7], temp[1], temp[5], temp[4], temp[2], temp[6]), 7) + ROTR(temp[0], 11) + W[ 2] + 0x6C24CF5C;

        /* PASS 4: */

        temp[7] = ROTR(F4(temp[1], temp[5], temp[3], temp[2], temp[0], temp[4], temp[6]), 7) + ROTR(temp[7], 11) + W[24] + 0x7A325381;
        temp[6] = ROTR(F4(temp[0], temp[4], temp[2], temp[1], temp[7], temp[3], temp[5]), 7) + ROTR(temp[6], 11) + W[ 4] + 0x28958677;
        temp[5] = ROTR(F4(temp[7], temp[3], temp[1], temp[0], temp[6], temp[2], temp[4]), 7) + ROTR(temp[5], 11) + W[ 0] + 0x3B8F4898;
        temp[4] = ROTR(F4(temp[6], temp[2], temp[0], temp[7], temp[5], temp[1], temp[3]), 7) + ROTR(temp[4], 11) + W[14] + 0x6B4BB9AF;
        temp[3] = ROTR(F4(temp[5], temp[1], temp[7], temp[6], temp[4], temp[0], temp[2]), 7) + ROTR(temp[3], 11) + W[ 2] + 0xC4BFE81B;
        temp[2] = ROTR(F4(temp[4], temp[0], temp[6], temp[5], temp[3], temp[7], temp[1]), 7) + ROTR(temp[2], 11) + W[ 7] + 0x66282193;
        temp[1] = ROTR(F4(temp[3], temp[7], temp[5], temp[4], temp[2], temp[6], temp[0]), 7) + ROTR(temp[1], 11) + W[28] + 0x61D809CC;
        temp[0] = ROTR(F4(temp[2], temp[6], temp[4], temp[3], temp[1], temp[5], temp[7]), 7) + ROTR(temp[0], 11) + W[23] + 0xFB21A991;

        temp[7] = ROTR(F4(temp[1], temp[5], temp[3], temp[2], temp[0], temp[4], temp[6]), 7) + ROTR(temp[7], 11) + W[26] + 0x487CAC60;
        temp[6] = ROTR(F4(temp[0], temp[4], temp[2], temp[1], temp[7], temp[3], temp[5]), 7) + ROTR(temp[6], 11) + W[ 6] + 0x5DEC8032;
        temp[5] = ROTR(F4(temp[7], temp[3], temp[1], temp[0], temp[6], temp[2], temp[4]), 7) + ROTR(temp[5], 11) + W[30] + 0xEF845D5D;
        temp[4] = ROTR(F4(temp[6], temp[2], temp[0], temp[7], temp[5], temp[1], temp[3]), 7) + ROTR(temp[4], 11) + W[20] + 0xE98575B1;
        temp[3] = ROTR(F4(temp[5], temp[1], temp[7], temp[6], temp[4], temp[0], temp[2]), 7) + ROTR(temp[3], 11) + W[18] + 0xDC262302;
        temp[2] = ROTR(F4(temp[4], temp[0], temp[6], temp[5], temp[3], temp[7], temp[1]), 7) + ROTR(temp[2], 11) + W[25] + 0xEB651B88;
        temp[1] = ROTR(F4(temp[3], temp[7], temp[5], temp[4], temp[2], temp[6], temp[0]), 7) + ROTR(temp[1], 11) + W[19] + 0x23893E81;
        temp[0] = ROTR(F4(temp[2], temp[6], temp[4], temp[3], temp[1], temp[5], temp[7]), 7) + ROTR(temp[0], 11) + W[ 3] + 0xD396ACC5;

        temp[7] = ROTR(F4(temp[1], temp[5], temp[3], temp[2], temp[0], temp[4], temp[6]), 7) + ROTR(temp[7], 11) + W[22] + 0x0F6D6FF3;
        temp[6] = ROTR(F4(temp[0], temp[4], temp[2], temp[1], temp[7], temp[3], temp[5]), 7) + ROTR(temp[6], 11) + W[11] + 0x83F44239;
        temp[5] = ROTR(F4(temp[7], temp[3], temp[1], temp[0], temp[6], temp[2], temp[4]), 7) + ROTR(temp[5], 11) + W[31] + 0x2E0B4482;
        temp[4] = ROTR(F4(temp[6], temp[2], temp[0], temp[7], temp[5], temp[1], temp[3]), 7) + ROTR(temp[4], 11) + W[21] + 0xA4842004;
        temp[3] = ROTR(F4(temp[5], temp[1], temp[7], temp[6], temp[4], temp[0], temp[2]), 7) + ROTR(temp[3], 11) + W[ 8] + 0x69C8F04A;
        temp[2] = ROTR(F4(temp[4], temp[0], temp[6], temp[5], temp[3], temp[7], temp[1]), 7) + ROTR(temp[2], 11) + W[27] + 0x9E1F9B5E;
        temp[1] = ROTR(F4(temp[3], temp[7], temp[5], temp[4], temp[2], temp[6], temp[0]), 7) + ROTR(temp[1], 11) + W[12] + 0x21C66842;
        temp[0] = ROTR(F4(temp[2], temp[6], temp[4], temp[3], temp[1], temp[5], temp[7]), 7) + ROTR(temp[0], 11) + W[ 9] + 0xF6E96C9A;

        temp[7] = ROTR(F4(temp[1], temp[5], temp[3], temp[2], temp[0], temp[4], temp[6]), 7) + ROTR(temp[7], 11) + W[ 1] + 0x670C9C61;
        temp[6] = ROTR(F4(temp[0], temp[4], temp[2], temp[1], temp[7], temp[3], temp[5]), 7) + ROTR(temp[6], 11) + W[29] + 0xABD388F0;
        temp[5] = ROTR(F4(temp[7], temp[3], temp[1], temp[0], temp[6], temp[2], temp[4]), 7) + ROTR(temp[5], 11) + W[ 5] + 0x6A51A0D2;
        temp[4] = ROTR(F4(temp[6], temp[2], temp[0], temp[7], temp[5], temp[1], temp[3]), 7) + ROTR(temp[4], 11) + W[15] + 0xD8542F68;
        temp[3] = ROTR(F4(temp[5], temp[1], temp[7], temp[6], temp[4], temp[0], temp[2]), 7) + ROTR(temp[3], 11) + W[17] + 0x960FA728;
        temp[2] = ROTR(F4(temp[4], temp[0], temp[6], temp[5], temp[3], temp[7], temp[1]), 7) + ROTR(temp[2], 11) + W[10] + 0xAB5133A3;
        temp[1] = ROTR(F4(temp[3], temp[7], temp[5], temp[4], temp[2], temp[6], temp[0]), 7) + ROTR(temp[1], 11) + W[16] + 0x6EEF0B6C;
        temp[0] = ROTR(F4(temp[2], temp[6], temp[4], temp[3], temp[1], temp[5], temp[7]), 7) + ROTR(temp[0], 11) + W[13] + 0x137A3BE4;

        /* PASS 5: */

        temp[7] = ROTR(F5(temp[2], temp[5], temp[0], temp[6], temp[4], temp[3], temp[1]), 7) + ROTR(temp[7], 11) + W[27] + 0xBA3BF050;
        temp[6] = ROTR(F5(temp[1], temp[4], temp[7], temp[5], temp[3], temp[2], temp[0]), 7) + ROTR(temp[6], 11) + W[ 3] + 0x7EFB2A98;
        temp[5] = ROTR(F5(temp[0], temp[3], temp[6], temp[4], temp[2], temp[1], temp[7]), 7) + ROTR(temp[5], 11) + W[21] + 0xA1F1651D;
        temp[4] = ROTR(F5(temp[7], temp[2], temp[5], temp[3], temp[1], temp[0], temp[6]), 7) + ROTR(temp[4], 11) + W[26] + 0x39AF0176;
        temp[3] = ROTR(F5(temp[6], temp[1], temp[4], temp[2], temp[0], temp[7], temp[5]), 7) + ROTR(temp[3], 11) + W[17] + 0x66CA593E;
        temp[2] = ROTR(F5(temp[5], temp[0], temp[3], temp[1], temp[7], temp[6], temp[4]), 7) + ROTR(temp[2], 11) + W[11] + 0x82430E88;
        temp[1] = ROTR(F5(temp[4], temp[7], temp[2], temp[0], temp[6], temp[5], temp[3]), 7) + ROTR(temp[1], 11) + W[20] + 0x8CEE8619;
        temp[0] = ROTR(F5(temp[3], temp[6], temp[1], temp[7], temp[5], temp[4], temp[2]), 7) + ROTR(temp[0], 11) + W[29] + 0x456F9FB4;

        temp[7] = ROTR(F5(temp[2], temp[5], temp[0], temp[6], temp[4], temp[3], temp[1]), 7) + ROTR(temp[7], 11) + W[19] + 0x7D84A5C3;
        temp[6] = ROTR(F5(temp[1], temp[4], temp[7], temp[5], temp[3], temp[2], temp[0]), 7) + ROTR(temp[6], 11) + W[ 0] + 0x3B8B5EBE;
        temp[5] = ROTR(F5(temp[0], temp[3], temp[6], temp[4], temp[2], temp[1], temp[7]), 7) + ROTR(temp[5], 11) + W[12] + 0xE06F75D8;
        temp[4] = ROTR(F5(temp[7], temp[2], temp[5], temp[3], temp[1], temp[0], temp[6]), 7) + ROTR(temp[4], 11) + W[ 7] + 0x85C12073;
        temp[3] = ROTR(F5(temp[6], temp[1], temp[4], temp[2], temp[0], temp[7], temp[5]), 7) + ROTR(temp[3], 11) + W[13] + 0x401A449F;
        temp[2] = ROTR(F5(temp[5], temp[0], temp[3], temp[1], temp[7], temp[6], temp[4]), 7) + ROTR(temp[2], 11) + W[ 8] + 0x56C16AA6;
        temp[1] = ROTR(F5(temp[4], temp[7], temp[2], temp[0], temp[6], temp[5], temp[3]), 7) + ROTR(temp[1], 11) + W[31] + 0x4ED3AA62;
        temp[0] = ROTR(F5(temp[3], temp[6], temp[1], temp[7], temp[5], temp[4], temp[2]), 7) + ROTR(temp[0], 11) + W[10] + 0x363F7706;

        temp[7] = ROTR(F5(temp[2], temp[5], temp[0], temp[6], temp[4], temp[3], temp[1]), 7) + ROTR(temp[7], 11) + W[ 5] + 0x1BFEDF72;
        temp[6] = ROTR(F5(temp[1], temp[4], temp[7], temp[5], temp[3], temp[2], temp[0]), 7) + ROTR(temp[6], 11) + W[ 9] + 0x429B023D;
        temp[5] = ROTR(F5(temp[0], temp[3], temp[6], temp[4], temp[2], temp[1], temp[7]), 7) + ROTR(temp[5], 11) + W[14] + 0x37D0D724;
        temp[4] = ROTR(F5(temp[7], temp[2], temp[5], temp[3], temp[1], temp[0], temp[6]), 7) + ROTR(temp[4], 11) + W[30] + 0xD00A1248;
        temp[3] = ROTR(F5(temp[6], temp[1], temp[4], temp[2], temp[0], temp[7], temp[5]), 7) + ROTR(temp[3], 11) + W[18] + 0xDB0FEAD3;
        temp[2] = ROTR(F5(temp[5], temp[0], temp[3], temp[1], temp[7], temp[6], temp[4]), 7) + ROTR(temp[2], 11) + W[ 6] + 0x49F1C09B;
        temp[1] = ROTR(F5(temp[4], temp[7], temp[2], temp[0], temp[6], temp[5], temp[3]), 7) + ROTR(temp[1], 11) + W[28] + 0x075372C9;
        temp[0] = ROTR(F5(temp[3], temp[6], temp[1], temp[7], temp[5], temp[4], temp[2]), 7) + ROTR(temp[0], 11) + W[24] + 0x80991B7B;

        messageDigest[7] += temp[7] = ROTR(F5(temp[2], temp[5], temp[0], temp[6], temp[4], temp[3], temp[1]), 7) + ROTR(temp[7], 11) + W[ 2] + 0x25D479D8;
        messageDigest[6] += temp[6] = ROTR(F5(temp[1], temp[4], temp[7], temp[5], temp[3], temp[2], temp[0]), 7) + ROTR(temp[6], 11) + W[23] + 0xF6E8DEF7;
        messageDigest[5] += temp[5] = ROTR(F5(temp[0], temp[3], temp[6], temp[4], temp[2], temp[1], temp[7]), 7) + ROTR(temp[5], 11) + W[16] + 0xE3FE501A;
        messageDigest[4] += temp[4] = ROTR(F5(temp[7], temp[2], temp[5], temp[3], temp[1], temp[0], temp[6]), 7) + ROTR(temp[4], 11) + W[22] + 0xB6794C3B;
        messageDigest[3] += temp[3] = ROTR(F5(temp[6], temp[1], temp[4], temp[2], temp[0], temp[7], temp[5]), 7) + ROTR(temp[3], 11) + W[ 4] + 0x976CE0BD;
        messageDigest[2] += temp[2] = ROTR(F5(temp[5], temp[0], temp[3], temp[1], temp[7], temp[6], temp[4]), 7) + ROTR(temp[2], 11) + W[ 1] + 0x04C006BA;
        messageDigest[1] += temp[1] = ROTR(F5(temp[4], temp[7], temp[2], temp[0], temp[6], temp[5], temp[3]), 7) + ROTR(temp[1], 11) + W[25] + 0xC1A94FB6;
        messageDigest[0] += temp[0] = ROTR(F5(temp[3], temp[6], temp[1], temp[7], temp[5], temp[4], temp[2]), 7) + ROTR(temp[0], 11) + W[15] + 0x409F60C4;

        for (int i = 0; i < 32; ++i) {
            W[i] = 0;
        }
    }
}
