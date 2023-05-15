/*
 *      Test Vectors (from FIPS PUB 180-1)
 *      "abc"
 *      A9993E36 4706816A BA3E2571 7850C26C 9CD0D89D
 *      "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq"
 *      84983E44 1C3BD26E BAAE4AA1 F95129E5 E54670F1
 *      A million repetitions of "a"
 *      34AA973C D4C4DAA4 F61EEB2B DBAD2731 6534016F
 */
package com.ravnaandtines.crypt.mda;

import junit.framework.TestCase;

/**
 *
 * @author Steve
 */
public class SHA1Test extends TestCase {

    public SHA1Test(final String testName) {
        super(testName);
    }

    public void testabc() {
        final SHA1 hash = new SHA1();
        hash.update((byte) 'a');
        hash.update((byte) 'b');
        hash.update((byte) 'c');
        final int[] expected = {
            0xA9, 0x99, 0x3E, 0x36,
            0x47, 0x06, 0x81, 0x6A,
            0xBA, 0x3E, 0x25, 0x71,
            0x78, 0x50, 0xC2, 0x6C,
            0x9C, 0xD0, 0xD8, 0x9D
        };
        com.ravnaandtines.crypt.CryptTestUtils.checkVector(hash, expected);
    }

    public void testVector2() throws java.io.UnsupportedEncodingException {
        final SHA1 hash = new SHA1();
        final String vector = "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq";

        final int[] expected = {
            0x84, 0x98, 0x3E, 0x44,
            0x1C, 0x3B, 0xD2, 0x6E,
            0xBA, 0xAE, 0x4A, 0xA1,
            0xF9, 0x51, 0x29, 0xE5,
            0xE5, 0x46, 0x70, 0xF1
        };
        com.ravnaandtines.crypt.CryptTestUtils.checkStringVector(hash, 
            vector, expected);        
    }

    public void testMilliona() {
        final SHA1 hash = new SHA1();
        for (int i = 0; i < 1000000; i++) {
            hash.update((byte) 'a');
        }
        final int[] expected = {
            0x34, 0xAA, 0x97, 0x3C,
            0xD4, 0xC4, 0xDA, 0xA4,
            0xF6, 0x1E, 0xEB, 0x2B,
            0xDB, 0xAD, 0x27, 0x31,
            0x65, 0x34, 0x01, 0x6F
        };
        com.ravnaandtines.crypt.CryptTestUtils.checkVector(hash, expected);
    }
}
