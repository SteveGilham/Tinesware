/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ravnaandtines.crypt.cea;

import junit.framework.TestCase;

/**
 *
 * @author Steve
 */
public class CAST5Test extends TestCase {

    public CAST5Test(final String testName) {
        super(testName);
    }
    private static final byte[] plain = {0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB,
        (byte) 0xCD, (byte) 0xEF
    };
    private static final byte[] in = new byte[8]; //NOPMD name
    private static final byte[] out = new byte[8];

    private void validate(final int[] expect) {
        for (int i = 0; i < 8; i++) {
            assertEquals("encypher error", expect[i], out[i] & 0xFF);
        }
        for (int i = 0; i < 8; i++) {
            assertEquals("decypher error", plain[i] & 0xFF, in[i] & 0xFF);
        }
    }

    /**
     * Test of init_NN method, of class CAST5.
     */
    public void testCast5() { //NOPMD long
        {
            final byte[] key = {0x01, 0x23, 0x45, 0x67, 0x12, 0x34, 0x56, 0x78,
                0x23, 0x45, 0x67, (byte) 0x89, 0x34, 0x56, 0x78, (byte) 0x9A
            };

            /**<pre>
             * Appendix B. Test Vectors
             *
             *    This appendix provides test vectors for the CAST-128 cipher described
             *    this document.
             *
             * B.1. Single Plaintext-Key-Ciphertext Sets
             *
             *    In order to ensure that the algorithm is implemented correctly, the
             *    following test vectors can be used for verification (values given in
             *    hexadecimal notation).
             *
             *    128-bit key         = 01 23 45 67 12 34 56 78 23 45 67 89 34 56 78 9A
             *            plaintext   = 01 23 45 67 89 AB CD EF
             *            ciphertext  = 23 8B 4F E5 84 7E 44 B2
             *
             *    80-bit  key         = 01 23 45 67 12 34 56 78 23 45
             *                        = 01 23 45 67 12 34 56 78 23 45 00 00 00 00 00 00
             *            plaintext   = 01 23 45 67 89 AB CD EF
             *            ciphertext  = EB 6A 71 1A 2C 02 27 1B
             *
             *    40-bit  key         = 01 23 45 67 12
             *                        = 01 23 45 67 12 00 00 00 00 00 00 00 00 00 00 00
             *            plaintext   = 01 23 45 67 89 AB CD EF
             *            ciphertext  = 7A C8 16 D1 6E 9B 30 2E
            </pre>*/
            System.arraycopy(plain, 0, in, 0, 8);
            CAST5 cast = new CAST5();
            cast.initNN(key, 0, 16, false);
            cast.ecb(true, in, 0, out, 0);
            cast.ecb(false, out, 0, in, 0);
            cast.destroy();

            final int[] expect1 = {
                0x23, 0x8B, 0x4F, 0xE5, 0x84, 0x7E, 0x44, 0xB2
            };
            validate(expect1);


            //=========================================

            cast = new CAST5();
            cast.initNN(key, 0, 10, false);
            cast.ecb(true, in, 0, out, 0);
            cast.ecb(false, out, 0, in, 0);
            cast.destroy();

            final int[] expect2 = {
                0xEB, 0x6A, 0x71, 0x1A, 0x2C, 0x02, 0x27, 0x1B
            };
            validate(expect2);

            //=========================================

            cast = new CAST5();
            cast.initNN(key, 0, 5, false);
            cast.ecb(true, in, 0, out, 0);
            cast.ecb(false, out, 0, in, 0);
            cast.destroy();

            final int[] expect3 = {
                0x7A, 0xC8, 0x16, 0xD1, 0x6E, 0x9B, 0x30, 0x2E
            };
            validate(expect3);

            /**<pre>
             * B.2. Full Maintenance Test
             *
             *    A maintenance test for CAST-128 has been defined to verify the
             *    correctness of implementations.  It is defined in pseudo-code as
             *    follows, where a and b are 128-bit vectors, aL and aR are the
             *    leftmost and rightmost halves of a, bL and bR are the leftmost and
             *    rightmost halves of b, and encrypt(d,k) is the encryption in ECB mode
             *    of block d under key k.
             *
             *    Initial a = 01 23 45 67 12 34 56 78 23 45 67 89 34 56 78 9A (hex)
             *    Initial b = 01 23 45 67 12 34 56 78 23 45 67 89 34 56 78 9A (hex)
             *
             *    do 1,000,000 times
             *    {
             *        aL = encrypt(aL,b)
             *        aR = encrypt(aR,b)
             *        bL = encrypt(bL,a)
             *        bR = encrypt(bR,a)
             *    }
             *
             * Verify a == EE A9 D0 A2 49 FD 3B A6 B3 43 6F B8 9D 6D CA 92 (hex)
             * Verify b == B2 C9 5E B0 0C 31 AD 71 80 AC 05 B8 E8 3D 69 6E (hex)
             *
            </pre>*/
            /*// takes about a quarter of an hour to run in Java with cobertura; 
			  // under 40s in J# and NCover.  Each take under 10s run without coverage.
			long now = java.lang.System.currentTimeMillis();
            final byte[] a = {0x01, 0x23, 0x45, 0x67, 0x12, 0x34, 0x56, 0x78, //NOPMD name
                0x23, 0x45, 0x67, (byte) 0x89, 0x34, 0x56, 0x78, (byte) 0x9A};
            final byte[] b = {0x01, 0x23, 0x45, 0x67, 0x12, 0x34, 0x56, 0x78, //NOPMD name
                0x23, 0x45, 0x67, (byte) 0x89, 0x34, 0x56, 0x78, (byte) 0x9A};
            for (int i = 0; i < 1000; ++i) {
                System.out.print("" + i + "/1000 complete\r\n"); //NOPMD 
                System.out.flush();
                for (int j = 0; j < 1000; ++j) {
                    cast = new CAST5(); //NOPMD
                    cast.initNN(b, 0, 16, false);
                    cast.ecb(true, a, 0, a, 0);
                    cast.ecb(true, a, 8, a, 8);
                    cast.destroy();

                    cast = new CAST5(); //NOPMD
                    cast.initNN(a, 0, 16, false);
                    cast.ecb(true, b, 0, b, 0);
                    cast.ecb(true, b, 8, b, 8);

                    cast.destroy();
                }
            }
            
            final int[] expectA = {
                0xEE, 0xA9, 0xD0, 0xA2,
                0x49, 0xFD, 0x3B, 0xA6,
                0xB3, 0x43, 0x6F, 0xB8,
                0x9D, 0x6D, 0xCA, 0x92
            };
           
            for (int i = 0; i < 16; ++i) {
                assertEquals("vector a", expectA[i], a[i] & 0xFF);
            }
            final int[] expectB = {
                0xB2, 0xC9, 0x5E, 0xB0,
                0x0C, 0x31, 0xAD, 0x71,
                0x80, 0xAC, 0x05, 0xB8,
                0xE8, 0x3D, 0x69, 0x6E
            };
            for (int i = 0; i < 16; ++i) {
                assertEquals("vector b", expectB[i], b[i] & 0xFF);
            }
			long delta = (java.lang.System.currentTimeMillis()-now)/1000;
			System.out.println("Took " + delta + "s");
            */
        }

    }
}
