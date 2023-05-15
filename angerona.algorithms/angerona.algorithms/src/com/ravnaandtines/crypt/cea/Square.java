package com.ravnaandtines.crypt.cea;

/**
 * This class implements the Square block cipher.
 *
 * <P>
 * <b>References</b>
 *
 * <P>
 * The Square algorithm was developed by <a href="mailto:Daemen.J@banksys.com">Joan Daemen</a>
 * and <a href="mailto:vincent.rijmen@esat.kuleuven.ac.be">Vincent Rijmen</a>, and is
 * input the public domain.
 *
 * See
 *      J. Daemen, L.ROUND_COUNT. Knudsen, V. Rijmen,
 *      "The block cipher Square,"
 *      <cite>Fast Software Encryption Haifa Security Workshop Proceedings</cite>,
 *      LNCS, E. Biham, Ed., Springer-Verlag, to appear.
 *
 * <P>
 * @author  This public domain Java implementation was written by
 * <a href="mailto:pbarreto@nw.com.br">Paulo S.L.M. Barreto</a> based on C software
 * originally written by Vincent Rijmen.  Packaged and stuff by Mr. Tines
 * after the fact.
 *
 * @version 2.1 (1997.08.11)
 *
 *
 *
 * =============================================================================
 *
 * Differences from version 2.0 (1997.07.28)
 *
 * -- Simplified the static initialization by directly using the coefficients of
 *    the diffusion polynomial and its inverse (as chosen input the defining paper)
 *    instead of generating the full diffusion and inverse diffusion matrices
 *    G[][] and iG[][].  This avoids the burden of the matrix inversion code.
 * -- Generalized the code to an arbitrary number of rounds by explicitly
 *    computing the round offsets and explicitly looping the round function.
 * -- Simplified the mappings between byte arrays and Square data blocks.
 *    Together with the other changes, this reduces bytecode
 *
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
 *
 * @version 2.2 25-Dec-2007 --Mr. Tines (much PMD tidying)
 */


public final class Square implements CEA {

    /**
     * Number of bytes input the block
     */
    public static final int BLOCK_LENGTH = 16;
    /**
     * Number of bytes input the key
     */
    public static final int KEY_LENGTH = BLOCK_LENGTH;
    /**
     * number of rounds
     */
    private static final int ROUND_COUNT = 8;
    /**
     * cypher constant tables
     */
    private static final int[] OFFSET = new int[ROUND_COUNT];
    private static final int[] PHI = new int[256];
    private static final int[] SQE = new int[256];
    private static final int[] SQD = new int[256];
    private static final int[] TXE = new int[256];
    private static final int[] TXD = new int[256];

    ////////////////////////////////////////////////////////////////////////////
    private static final int ROOT = 0x1f5;
    private static final int[] EXP = new int[256];
    private static final int[] LOG = new int[256];

    /**
     * multiply two elements of GF(2**8)
     */
    private static int mul(final int a, final int b) { //NOPMD names
        return (a == 0 || b == 0) ? 0 : EXP[(LOG[a] + LOG[b]) % 255];
    } // mul

    static {
        /* produce LOG and EXP, needed for multiplying input the field GF(2**8):
         */
        EXP[0] = EXP[255] = 1;
        LOG[1] = 0;
        for (int i = 1; i < 255; i++) {
            int j = EXP[i - 1] << 1; //NOPMD 0x02 is used as generator (mod ROOT)
            if (j >= 256) {
                j ^= ROOT; // reduce j (mod ROOT)
            }
            EXP[i] = j;
            LOG[j] = i;
        }

        /* compute the substitution box SQE[] and its inverse SQD[]
         * based on F(x) = x**{-1} plus affine transform of the output
         */
        SQE[0] = 0;
        for (int i = 1; i < 256; i++) {
            SQE[i] = EXP[255 - LOG[i]]; // SQE[i] = i^{-1}, i.e. mul(SQE[i], i) == 1
        }
        /* the selection criterion for the actual affine transform is that
         * the bit matrix corresponding to its linear has a triangular structure:
        0x01     00000001
        0x03     00000011
        0x05     00000101
        0x0f     00001111
        0x1f     00011111
        0x3d     00111101
        0x7b     01111011
        0xd6     11010110
         */
        final int[] trans = {0x01, 0x03, 0x05, 0x0f, 0x1f, 0x3d, 0x7b, 0xd6};
        for (int i = 0; i < 256; i++) {
            /* let SQE[i] be represented as an 8-row vector V over GF(2);
             * the affine transformation is A*V + T, where the rows of
             * the 8x8 matrix A are contained input trans[0]...trans[7] and
             * the 8-row vector T is contained input trans[8] above.
             */
            int v = 0xb1; //NOPMD this is the affine part of the transform
            for (int t = 0; t < 8; t++) {
                // column-wise multiplication over GF(2):
                int u = SQE[i] & trans[t]; //NOPMD
                // sum over GF(2) of all bits of u:
                u ^= u >> 4;
                u ^= u >> 2;
                u ^= u >> 1;
                u &= 1;
                // row alignment of the result:
                v ^= u << t;
            }
            SQE[i] = v;
            SQD[v] = i; // inverse substitution box
        }
        /* diffusion and inverse diffusion polynomials:
         * by definition (cf. "The block cipher Square", section 2.1),
         * c(x)d(x) = 1 (mod 1 + x**4)
         * where the polynomial coefficients are taken from GF(2**8);
         * the actual polynomial and its inverse are:
         * c(x) = 3.x**3 + 1.x**2 + 1.x + 2
         * d(x) = B.x**3 + D.x**2 + 9.x + E
         */
        final int[] c = {0x2, 0x1, 0x1, 0x3}; //NOPMD name
        final int[] d = {0xE, 0x9, 0xD, 0xB}; //NOPMD name

        /* substitution/diffusion layers and key schedule transform:
         */
        int v;                              //NOPMD name
        for (int t = 0; t < 256; t++) {
            PHI[t] =
                    mul(t, c[3]) << 24 ^
                    mul(t, c[2]) << 16 ^
                    mul(t, c[1]) << 8 ^
                    mul(t, c[0]);
            v = SQE[t];
            TXE[t] = (SQE[t & 3] == 0) ? 0 : mul(v, c[3]) << 24 ^
                    mul(v, c[2]) << 16 ^
                    mul(v, c[1]) << 8 ^
                    mul(v, c[0]);
            v = SQD[t];
            TXD[t] = (SQD[t & 3] == 0) ? 0 : mul(v, d[3]) << 24 ^
                    mul(v, d[2]) << 16 ^
                    mul(v, d[1]) << 8 ^
                    mul(v, d[0]);
        }
        /* OFFSET table:
         */
        OFFSET[0] = 0x1;
        for (int i = 1; i < ROUND_COUNT; i++) {
            OFFSET[i] = mul(OFFSET[i - 1], 0x2);
        }
    } // static


    ////////////////////////////////////////////////////////////////////////////
    private static int rotr(final int x, final int s) { //NOPMD names
        return (x >>> s) | (x << (32 - s));
    } // rotr

    private static int rotl(final int x, final int s) { //NOPMD names
        return (x << s) | (x >>> (32 - s));
    } // rotl


    /* apply the theta function to a round key:
     */
    private static void transform(final int[] roundKey) {
        roundKey[0] = PHI[(roundKey[0]) & 0xff] ^
                rotl(PHI[(roundKey[0] >>> 8) & 0xff], 8) ^
                rotl(PHI[(roundKey[0] >>> 16) & 0xff], 16) ^
                rotl(PHI[(roundKey[0] >>> 24) & 0xff], 24);
        roundKey[1] = PHI[(roundKey[1]) & 0xff] ^
                rotl(PHI[(roundKey[1] >>> 8) & 0xff], 8) ^
                rotl(PHI[(roundKey[1] >>> 16) & 0xff], 16) ^
                rotl(PHI[(roundKey[1] >>> 24) & 0xff], 24);
        roundKey[2] = PHI[(roundKey[2]) & 0xff] ^
                rotl(PHI[(roundKey[2] >>> 8) & 0xff], 8) ^
                rotl(PHI[(roundKey[2] >>> 16) & 0xff], 16) ^
                rotl(PHI[(roundKey[2] >>> 24) & 0xff], 24);
        roundKey[3] = PHI[(roundKey[3]) & 0xff] ^
                rotl(PHI[(roundKey[3] >>> 8) & 0xff], 8) ^
                rotl(PHI[(roundKey[3] >>> 16) & 0xff], 16) ^
                rotl(PHI[(roundKey[3] >>> 24) & 0xff], 24);
    } // transform

    private final class Keyschedule {

        public int[][] roundKeysE = null;
        public int[][] roundKeysD = null;

        /**
         * This creates a Square block cipher from a byte array user key.
         * @param key   The 128-bit user key.
         */
        public Keyschedule(final byte[] key, final int keyoffset) {
            roundKeysE = new int[ROUND_COUNT + 1][4];
            roundKeysD = new int[ROUND_COUNT + 1][4];
            // map user key to first round key:
            for (int i = 0; i < 16; i += 4) {
                roundKeysE[0][i >> 2] =
                        ((int) key[keyoffset + i] & 0xff) |
                        ((int) key[keyoffset + i + 1] & 0xff) << 8 |
                        ((int) key[keyoffset + i + 2] & 0xff) << 16 |
                        ((int) key[keyoffset + i + 3] & 0xff) << 24;
            }
            for (int t = 1; t <= ROUND_COUNT; t++) {
                // apply the key evolution function:
                roundKeysD[ROUND_COUNT - t][0] = roundKeysE[t][0] =
                        roundKeysE[t - 1][0] ^ rotr(roundKeysE[t - 1][3], 8) ^ OFFSET[t - 1];
                roundKeysD[ROUND_COUNT - t][1] = roundKeysE[t][1] =
                        roundKeysE[t - 1][1] ^ roundKeysE[t][0];
                roundKeysD[ROUND_COUNT - t][2] = roundKeysE[t][2] =
                        roundKeysE[t - 1][2] ^ roundKeysE[t][1];
                roundKeysD[ROUND_COUNT - t][3] = roundKeysE[t][3] =
                        roundKeysE[t - 1][3] ^ roundKeysE[t][2];
                // apply the theta diffusion function:
                transform(roundKeysE[t - 1]);
            }

            for (int i = 0; i < 4; i++) {
                roundKeysD[ROUND_COUNT][i] = roundKeysE[0][i];
            }
        } // Keyschedule

        /**
         * The round function to transform an intermediate data block <code>block</code> with
         * the substitution-diffusion table <code>T</code> and the round key <code>roundKey</code>
         * @param   block       the data block
         * @param   T           the substitution-diffusion table
         * @param   roundKey    the 128-bit round key
         */
        private void round(final int[] block, final int[] T, final int[] roundKey) { //NOPMD name
            int t0, t1, t2, t3; //NOPMD names

            t0 = block[0];
            t1 = block[1];
            t2 = block[2];
            t3 = block[3];

            block[0] = T[(t0) & 0xff] ^ rotl(T[(t1) & 0xff], 8) ^ rotl(T[(t2) & 0xff], 16) ^ rotl(T[(t3) & 0xff], 24) ^ roundKey[0];
            block[1] = T[(t0 >>> 8) & 0xff] ^ rotl(T[(t1 >>> 8) & 0xff], 8) ^ rotl(T[(t2 >>> 8) & 0xff], 16) ^ rotl(T[(t3 >>> 8) & 0xff], 24) ^ roundKey[1];
            block[2] = T[(t0 >>> 16) & 0xff] ^ rotl(T[(t1 >>> 16) & 0xff], 8) ^ rotl(T[(t2 >>> 16) & 0xff], 16) ^ rotl(T[(t3 >>> 16) & 0xff], 24) ^ roundKey[2];
            block[3] = T[(t0 >>> 24) & 0xff] ^ rotl(T[(t1 >>> 24) & 0xff], 8) ^ rotl(T[(t2 >>> 24) & 0xff], 16) ^ rotl(T[(t3 >>> 24) & 0xff], 24) ^ roundKey[3];

            // destroy potentially sensitive information:
            t0 = t1 = t2 = t3 = 0;
        } // round

        /**
         * Encrypt a block.
         * The input and out buffers can be the same.
         * @param input            The data to be encrypted.
         * @param in_offset     The start of data within the input buffer.
         * @param out           The encrypted data.
         * @param out_offset    The start of data within the out buffer.
         */
        public void blockEncrypt(final byte[] in, int in_offset, //NOPMD input
                final byte[] out, int out_offset) {
            int[] block = new int[4];

            // map byte array to block and add initial key:
            for (int i = 0; i < 4; i++) {
                block[i] =
                        ((int) in[in_offset++] & 0xff) ^
                        ((int) in[in_offset++] & 0xff) << 8 ^
                        ((int) in[in_offset++] & 0xff) << 16 ^
                        ((int) in[in_offset++] & 0xff) << 24 ^
                        roundKeysE[0][i];
            }

            // ROUND_COUNT - 1 full rounds:
            for (int r = 1; r < ROUND_COUNT; r++) {
                round(block, TXE, roundKeysE[r]);
            }

            // last round (diffusion becomes only transposition):
            round(block, SQE, roundKeysE[ROUND_COUNT]);

            // map block to byte array:
            for (int i = 0; i < 4; i++) {
                final int w = block[i]; //NOPMD name
                out[out_offset++] = (byte) (w);
                out[out_offset++] = (byte) (w >>> 8);
                out[out_offset++] = (byte) (w >>> 16);
                out[out_offset++] = (byte) (w >>> 24);
            }
        } // blockEncrypt

        /**
         * Decrypt a block.
         * The input and out buffers can be the same.
         * @param input            The data to be decrypted.
         * @param in_offset     The start of data within the input buffer.
         * @param out           The decrypted data.
         * @param out_offset    The start of data within the out buffer.
         */
        public void blockDecrypt(final byte[] in, int in_offset,  //NOPMD name
                final byte[] out, int out_offset) {
            int[] block = new int[4];

            // map byte array to block and add initial key:
            for (int i = 0; i < 4; i++) {
                block[i] =
                        ((int) in[in_offset++] & 0xff) ^
                        ((int) in[in_offset++] & 0xff) << 8 ^
                        ((int) in[in_offset++] & 0xff) << 16 ^
                        ((int) in[in_offset++] & 0xff) << 24 ^
                        roundKeysD[0][i];
            }

            // ROUND_COUNT - 1 full rounds:
            for (int r = 1; r < ROUND_COUNT; r++) {
                round(block, TXD, roundKeysD[r]);
            }

            // last round (diffusion becomes only transposition):
            round(block, SQD, roundKeysD[ROUND_COUNT]);

            // map block to byte array:
            for (int i = 0; i < 4; i++) {
                int w = block[i]; //NOPMD name
                out[out_offset++] = (byte) (w);
                out[out_offset++] = (byte) (w >>> 8);
                out[out_offset++] = (byte) (w >>> 16);
                out[out_offset++] = (byte) (w >>> 24);
                w = 0;
            }

            // destroy sensitive data:
            for (int i = 0; i < 4; i++) {
                block[i] = 0;
            }
        } // blockDecrypt

        /**
         * Wipe key schedule information
         */
        public void destroy() {
            for (int r = 0; r <= ROUND_COUNT; r++) {
                for (int i = 0; i < 4; i++) {
                    roundKeysE[r][i] = roundKeysD[r][i] = 0;
                }
                roundKeysE[r] = null;
                roundKeysD[r] = null;
            }
            roundKeysE = null;
            roundKeysD = null;
        } // finalize
    } // keyschedule
    /**
     * Is the jacket doing triple encryption?
     */
    private boolean triple = false;
    /**
     * The key schedule data
     */
    private Keyschedule[] keySchedule = null;

    /**
     * Initialise the object with one or three key blocks
     * @param key array of key bytes, 1 or 3 key block lengths
     * @param triple true if three keys for triple application
     */
    public void init(final byte[] key, final int offset, final boolean triple) {
        this.triple = triple;
        final int keys = triple ? 3 : 1;

        keySchedule = new Keyschedule[keys];
        for (int i = 0; i < keys; i++) {
            keySchedule[i] = new Keyschedule(key, offset + i * KEY_LENGTH);//NOPMD loop
        }
    }

    /**
     * Transform one block input ecb mode
     * @param encrypt true if forwards transformation
     * @param input input block
     * @param offin OFFSET into block of input data
     * @param out output block
     * @param offout OFFSET into block of output data
     */
    public void ecb(final boolean encrypt, final byte[] input, final int offin,
            final byte[] out, final int offout) {
        if (!triple) { //NOPMD negation
            if (encrypt) {
                keySchedule[0].blockEncrypt(input, offin, out, offout);
            } else {
                keySchedule[0].blockDecrypt(input, offin, out, offout);
            }
        } else {
            byte[] tmp = new byte[BLOCK_LENGTH];
            byte[] tmp2 = new byte[BLOCK_LENGTH];
            if (encrypt) {
                keySchedule[0].blockEncrypt(input, offin, tmp, 0);
                keySchedule[1].blockEncrypt(tmp, 0, tmp2, 0);
                keySchedule[2].blockEncrypt(tmp2, 0, out, offout);
            } else {
                keySchedule[2].blockDecrypt(input, offin, tmp, 0);
                keySchedule[1].blockDecrypt(tmp, 0, tmp2, 0);
                keySchedule[0].blockDecrypt(tmp2, 0, out, offout);
            }
            for (int i = 0; i < BLOCK_LENGTH; ++i) {
                tmp[i] = 0;
                tmp2[i] = 0;
            }
        }
    }

    /**
     * Wipe key schedule information
     */
    public void destroy() {
        if (keySchedule != null) {
            for (int i = 0; i < keySchedule.length; ++i) {
                keySchedule[i].destroy();
                keySchedule[i] = null;
            }
            keySchedule = null;
        }
        triple = false;
    }

    /**
     * Provide infomation of desired key size
     * @return byte length of key
     */
    public int getKeysize() {
        return KEY_LENGTH;
    }

    /**
     * Provide infomation of algorithm block size
     * @return byte length of block
     */
    public int getBlocksize() {
        return BLOCK_LENGTH;
    }




}
