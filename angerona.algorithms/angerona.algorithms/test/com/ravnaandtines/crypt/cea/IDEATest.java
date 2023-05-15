/*
 * To change this template, choose Tools | Templates
 * and open the template input the editor.
 */
package com.ravnaandtines.crypt.cea;

import junit.framework.TestCase;

/**
 *
 * @author Steve
 */
public class IDEATest extends TestCase { //NOPMD complex

    public IDEATest(final String testName) {
        super(testName);
    }
    private static final int KBYTES = 1024;

    /**
     * Test of isAvailable method, of class IDEA.
     */
    public void testIsAvailable() {
        final boolean expResult = true;
        final boolean result = IDEA.isAvailable();
        assertEquals("availability",expResult, result);
    }
    
    public void testIDEAinternals() { //NOPMD complex

        final IDEA.Key Z = new IDEA.Key();  //NOPMD name
        final IDEA.Key DK = new IDEA.Key(); //NOPMD name
        final short[] XX = new short[4];    //NOPMD name and type
        final short[] TT = new short[4];    //NOPMD name and type
        final short[] YY = new short[4];    //NOPMD name and type
        final short[] userkey = new short[8];  //NOPMD short type
        long start, end;

        // Make a sample user key for testing... 
        for (int i = 0; i < 8; i++) {
            userkey[i] = (short) (i + 1); //NOPMD short type
        }

        // Compute encryption subkeys from user key... 
        IDEA.enKey(userkey, Z.key);

        final int[] expected = { //Encryption key subblocks:
            1, 2, 3, 4, 5, 6,
            7, 8, 1024, 1536, 2048, 2560,
            3072, 3584, 4096, 512, 16, 20,
            24, 28, 32, 4, 8, 12,
            10240, 12288, 14336, 16384, 2048, 4096,
            6144, 8192, 112, 128, 16, 32,
            48, 64, 80, 96, 0, 8192,
            16384, 24576, 32768, 40960, 49152, 57345,
            128, 192, 256, 320
        ,
            };
        
        for (int j = 0; j < IDEA.ROUNDS + 1; j++) {
            final int limit = IDEA.ROUNDS == j ? 4 : 6;
            for (int i = 0; i < limit; i++) {
                final int index = j * 6 + i;
                assertEquals("encryption sub-blocks",
                        expected[index],
                        IDEA.low16(Z.key[index]));
            }
        }

        // Compute decryption subkeys from encryption subkeys... 
        IDEA.deKey(Z, DK);
        final int[] expected1 = {
            65025, 65344, 65280, 26010, 49152, 57345,
            65533, 32768, 40960, 52428, 0, 8192,
            42326, 65456, 65472, 21163, 16, 32,
            21835, 65424, 57344, 65025, 2048, 4096,
            13101, 51200, 53248, 65533, 8, 12,
            19115, 65504, 65508, 49153, 16, 20,
            43670, 61440, 61952, 65409, 2048, 2560,
            18725, 64512, 65528, 21803, 5, 6,
            1, 65534, 65533, 49153
        ,
            };
        
        for (int j = 0; j < IDEA.ROUNDS + 1; j++) {
            final int limit = IDEA.ROUNDS == j ? 4 : 6;
            for (int i = 0; i < limit; i++) {
                final int index = j * 6 + i;
                assertEquals("decryption sub-blocks",
                        expected1[index],
                        IDEA.low16(DK.key[index]));
            }
        }


        // Make a sample plaintext pattern for testing... 
        for (int k = 0; k < 4; k++) {
            XX[k] = (short) k; //NOPMD short type
        }

        final int blockCount = KBYTES * 64;
        start = System.currentTimeMillis();
        IDEA.cipher(XX, YY, Z);       // encrypt plaintext XX, making YY
        for (int l = 1; l < blockCount; l++) {
            IDEA.cipher(YY, YY, Z);
        }	// repeated encryption
        IDEA.cipher(YY, TT, DK);      // decrypt ciphertext YY, making TT
        for (int l = 1; l < blockCount; l++) {
            IDEA.cipher(TT, TT, DK);
        }	// repeated decryption
        end = System.currentTimeMillis() - start;
        final double elapsed = end / 1000.0;
        final double rate = KBYTES / elapsed;
        System.out.println("" + elapsed + " seconds = " + rate + " kbytes per second"); //NOPMD

        final int[] expected2 = {52859, 23597, 6202, 63282};
        for (int i = 0; i < 4; ++i) {
            assertEquals("XX[" + i + "]", i, XX[i]);
            assertEquals("YY[" + i + "]", expected2[i], IDEA.low16(YY[i]));
            assertEquals("TT[" + i + "]", i, TT[i]);
            assertEquals("inversion error", XX[i], TT[i]);
        }
    }

    public void testIDEA() { //NOPMD it's long

        final IDEA keysched = new IDEA();
        final byte[][] key =
                {{0x01, 0x23, 0x45, 0x67, 0x12, 0x34, 0x56, 0x78,
        0x23, 0x45, 0x67, (byte) 0x89, 0x34, 0x56, 0x78, (byte) 0x9A
    },
            {0x00, 0x01, 0x00, 0x02, 0x00, 0x03, 0x00, 0x04,
        0x00, 0x05, 0x00, 0x06, 0x00, 0x07, 0x00, 0x08
    },
            {0x3a, (byte) 0x98, 0x4e, 0x20, 0x00, 0x19, 0x5d, (byte) 0xb3,
        0x2e, (byte) 0xe5, 0x01, (byte) 0xc8, (byte) 0xc4, 0x7c, (byte) 0xea, 0x60
    },
            {0x00, 0x64, 0x00, (byte) 0xc8, 0x01, 0x2c, 0x01, (byte) 0x90,
        0x01, (byte) 0xf4, 0x02, 0x58, 0x02, (byte) 0xbc, 0x03, 0x20
    },
            {0x00, 0x01, 0x00, 0x02, 0x00, 0x03, 0x00, 0x04,
        0x00, 0x05, 0x00, 0x06, 0x00, 0x07, 0x00, 0x08
    },
            {0x00, 0x01, 0x00, 0x02, 0x00, 0x03, 0x00, 0x04,
        0x00, 0x05, 0x00, 0x06, 0x00, 0x07, 0x00, 0x08
    },
            {0x00, 0x01, 0x00, 0x02, 0x00, 0x03, 0x00, 0x04,
        0x00, 0x05, 0x00, 0x06, 0x00, 0x07, 0x00, 0x08
    },
            {0x00, 0x01, 0x00, 0x02, 0x00, 0x03, 0x00, 0x04,
        0x00, 0x05, 0x00, 0x06, 0x00, 0x07, 0x00, 0x08
    },
            {0x00, 0x01, 0x00, 0x02, 0x00, 0x03, 0x00, 0x04,
        0x00, 0x05, 0x00, 0x06, 0x00, 0x07, 0x00, 0x08
    },
            {0x00, 0x01, 0x00, 0x02, 0x00, 0x03, 0x00, 0x04,
        0x00, 0x05, 0x00, 0x06, 0x00, 0x07, 0x00, 0x08
    },
            {0x00, 0x01, 0x00, 0x02, 0x00, 0x03, 0x00, 0x04,
        0x00, 0x05, 0x00, 0x06, 0x00, 0x07, 0x00, 0x08
    },
            {0x00, 0x05, 0x00, 0x0A, 0x00, 0x0F, 0x00, 0x14,
        0x00, 0x19, 0x00, 0x1E, 0x00, 0x23, 0x00, 0x28
    },
            {0x3A, (byte) 0x98, 0x4E, 0x20, 0x00, 0x19, 0x5D, (byte) 0xB3,
        0x2E, (byte) 0xE5, 0x01, (byte) 0xC8, (byte) 0xC4, 0x7C, (byte) 0xEA, 0x60
    },
            {0x00, 0x64, 0x00, (byte) 0xC8, 0x01, 0x2C, 0x01, (byte) 0x90,
        0x01, (byte) 0xF4, 0x02, 0x58, 0x02, (byte) 0xBC, 0x03, 0x20
    },
            {(byte) 0x9D, 0x40, 0x75, (byte) 0xC1, 0x03, (byte) 0xBC, 0x32, 0x2A,
        (byte) 0xFB, 0x03, (byte) 0xE7, (byte) 0xBE, 0x6A, (byte) 0xB3, 0x00, 0x06
    }
        };
        final byte[][] input =
                {{0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF},
            {0x00, 0x00, 0x00, 0x01, 0x00, 0x02, 0x00, 0x03},
            {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08},
            {0x05, 0x32, 0x0a, 0x64, 0x14, (byte) 0xc8, 0x19, (byte) 0xfa},
            {0x00, 0x00, 0x00, 0x01, 0x00, 0x02, 0x00, 0x03},
            {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08},
            {0x00, 0x19, 0x32, 0x4B, 0x64, 0x7D, (byte) 0x96, (byte) 0xAF},
            {(byte) 0xF5, 0x20, 0x2D, 0x5B, (byte) 0x9C, 0x67, 0x1B, 0x08},
            {(byte) 0xFA, (byte) 0xE6, (byte) 0xD2, (byte) 0xBE, (byte) 0xAA, (byte) 0x96, (byte) 0x82, 0x6E},
            {0x0A, 0x14, 0x1E, 0x28, 0x32, 0x3C, 0x46, 0x50},
            {0x05, 0x0A, 0x0F, 0x14, 0x19, 0x1E, 0x23, 0x28},
            {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08},
            {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08},
            {0x05, 0x32, 0x0A, 0x64, 0x14, (byte) 0xC8, 0x19, (byte) 0xFA},
            {0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08}};
        final byte[][] out =
                {{(byte) 0xaf, (byte) 0xdb, (byte) 0xbe, (byte) 0xc8, 0x35, 0x45, (byte) 0xb1, (byte) 0x95},
            {0x11, (byte) 0xfb, (byte) 0xed, 0x2b, 0x01, (byte) 0x98, 0x6d, (byte) 0xe5},
            {(byte) 0x97, (byte) 0xbc, (byte) 0xd8, 0x20, 0x07, (byte) 0x80, (byte) 0xda, (byte) 0x86},
            {0x65, (byte) 0xbe, (byte) 0x87, (byte) 0xe7, (byte) 0xa2, 0x53, (byte) 0x8a, (byte) 0xed},
            {0x11, (byte) 0xFB, (byte) 0xED, 0x2B, 0x01, (byte) 0x98, 0x6D, (byte) 0xe5},
            {0x54, 0x0E, 0x5F, (byte) 0xEA, 0x18, (byte) 0xC2, (byte) 0xF8, (byte) 0xB1},
            {(byte) 0x9F, 0x0A, 0x0A, (byte) 0xB6, (byte) 0xE1, 0x0C, (byte) 0xED, 0x78},
            {(byte) 0xCF, 0x18, (byte) 0xFD, 0x73, 0x55, (byte) 0xE2, (byte) 0xC5, (byte) 0xC5},
            {(byte) 0x85, (byte) 0xDF, 0x52, 0x00, 0x56, 0x08, 0x19, 0x3D},
            {0x2F, 0x7D, (byte) 0xE7, 0x50, 0x21, 0x2F, (byte) 0xB7, 0x34},
            {0x7B, 0x73, 0x14, (byte) 0x92, 0x5D, (byte) 0xE5, (byte) 0x9C, 0x09},
            {0x3E, (byte) 0xC0, 0x47, (byte) 0x80, (byte) 0xBE, (byte) 0xFF, 0x6E, 0x20},
            {(byte) 0x97, (byte) 0xBC, (byte) 0xD8, 0x20, 0x07, (byte) 0x80, (byte) 0xDA, (byte) 0x86},
            {0x65, (byte) 0xBE, (byte) 0x87, (byte) 0xE7, (byte) 0xA2, 0x53, (byte) 0x8A, (byte) 0xED},
            {(byte) 0xF5, (byte) 0xDB, 0x1A, (byte) 0xC4, 0x5E, 0x5E, (byte) 0xF9, (byte) 0xF9}};

        final byte[] work = new byte[8];
        final byte[] back = new byte[8];
        for (int i2 = 0; i2 < 15; ++i2) {
            keysched.init(key[i2], 0, false);
            keysched.ecb(true, input[i2], 0, work, 0);
            for(int i=0; i<out[i2].length; ++i)
            {
                assertEquals("encrypted", out[i2][i], work[i]);
            }

            keysched.ecb(false, work, 0, back, 0);
            for(int i=0; i<input[i2].length; ++i)
            {
                assertEquals("decrypted", input[i2][i], back[i]);
            }
            keysched.destroy();
        }
    }

}
