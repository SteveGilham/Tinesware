/*
 *      Test Vectors (from FIPS PUB 180-1)
 *      "abc"
 *      0164b8a9 14cd2a5e 74c4f7ff 082c4d97 f1edf880
 *      "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq"
 *      d2516ee1 acfa5baf 33dfc1c4 71e43844 9ef134c8
 *      A million repetitions of "a"
 *      3232affa 48628a26 653b5aaa 44541fd9 0d690603
 */
package com.ravnaandtines.crypt.mda;

import junit.framework.TestCase;

/**
 *
 * @author Steve
 */
public class SHA0Test extends TestCase {

    public SHA0Test(final String testName) {
        super(testName);
    }

    public void testabc() {
        final SHA0 hash = new SHA0();
        hash.update((byte) 'a');
        hash.update((byte) 'b');
        hash.update((byte) 'c');
        final int[] expected = {
            0x01, 0x64, 0xb8, 0xa9,
            0x14, 0xcd, 0x2a, 0x5e,
            0x74, 0xc4, 0xf7, 0xff,
            0x08, 0x2c, 0x4d, 0x97,
            0xf1, 0xed, 0xf8, 0x80
        };
        com.ravnaandtines.crypt.CryptTestUtils.checkVector(hash, expected);
    }

    public void testVector2() throws java.io.UnsupportedEncodingException {
        final SHA0 hash = new SHA0();
        final String vector = "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq";

        final int[] expected = {
            0xd2, 0x51, 0x6e, 0xe1,
            0xac, 0xfa, 0x5b, 0xaf,
            0x33, 0xdf, 0xc1, 0xc4,
            0x71, 0xe4, 0x38, 0x44,
            0x9e, 0xf1, 0x34, 0xc8
        };
        com.ravnaandtines.crypt.CryptTestUtils.checkStringVector(hash, 
            vector, expected);        
    }

    public void testMilliona() {
        final SHA0 hash = new SHA0();
        for (int i = 0; i < 1000000; i++) {
            hash.update((byte) 'a');
        }
        final int[] expected = {
            0x32, 0x32, 0xaf, 0xfa,
            0x48, 0x62, 0x8a, 0x26,
            0x65, 0x3b, 0x5a, 0xaa,
            0x44, 0x54, 0x1f, 0xd9,
            0x0d, 0x69, 0x06, 0x03
        };

        com.ravnaandtines.crypt.CryptTestUtils.checkVector(hash, expected);
    }
}
