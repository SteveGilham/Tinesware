/*
MD5 ("") = d41d8cd98f00b204e9800998ecf8427e
MD5 ("a") = 0cc175b9c0f1b6a831c399e269772661
MD5 ("abc") = 900150983cd24fb0d6963f7d28e17f72
MD5 ("message digest") = f96b697d7cb7938d525a2f31aaf161d0
MD5 ("abcdefghijklmnopqrstuvwxyz") = c3fcd3d76192e4007dfb496cca67e13b
MD5 ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789") =
d174ab98d277d9f5a5611c2c9f419d9f
MD5 ("123456789012345678901234567890123456789012345678901234567890123456
78901234567890") = 57edf4a22be3c955ac49da2e2107b67a
 */

package com.ravnaandtines.crypt.mda;

import junit.framework.TestCase;

/**
 *
 * @author Steve
 */
public class MD5Test extends TestCase {
    
    public MD5Test(final String testName) {
        super(testName);
    }
    public void testEmpty() { 
        final MD5 hash = new MD5();
        hash.update(null, 0, 0);
        final int[] expected = {
            0xd4, 0x1d, 0x8c, 0xd9,
            0x8f, 0x00, 0xb2, 0x04,
            0xe9, 0x80, 0x09, 0x98,
            0xec, 0xf8, 0x42, 0x7e,
        };
        com.ravnaandtines.crypt.CryptTestUtils.checkVector(hash, expected);
    }
    public void testa() { 
        final MD5 hash = new MD5();
        hash.update((byte) 'a');

        final int[] expected = {
            0x0c, 0xc1, 0x75, 0xb9,
            0xc0, 0xf1, 0xb6, 0xa8,
            0x31, 0xc3, 0x99, 0xe2,
            0x69, 0x77, 0x26, 0x61,
        };
        com.ravnaandtines.crypt.CryptTestUtils.checkVector(hash, expected);
    }
    public void testabc() { 
        final MD5 hash = new MD5();
        hash.update((byte) 'a');
        hash.update((byte) 'b');
        hash.update((byte) 'c');
        final int[] expected = {
            0x90, 0x01, 0x50, 0x98,
            0x3c, 0xd2, 0x4f, 0xb0,
            0xd6, 0x96, 0x3f, 0x7d,
            0x28, 0xe1, 0x7f, 0x72,
        };
        com.ravnaandtines.crypt.CryptTestUtils.checkVector(hash, expected);
    }
    public void testMessageDigest() throws java.io.UnsupportedEncodingException {
        final MD5 hash = new MD5();

        final int[] expected = {
            0xf9, 0x6b, 0x69, 0x7d,
            0x7c, 0xb7, 0x93, 0x8d,
            0x52, 0x5a, 0x2f, 0x31,
            0xaa, 0xf1, 0x61, 0xd0,
        };
        com.ravnaandtines.crypt.CryptTestUtils.checkStringVector(hash, 
            "message digest", expected);        
    }
    public void testAlpha() throws java.io.UnsupportedEncodingException {
        final MD5 hash = new MD5();

        final int[] expected = {
            0xc3, 0xfc, 0xd3, 0xd7,
            0x61, 0x92, 0xe4, 0x00,
            0x7d, 0xfb, 0x49, 0x6c,
            0xca, 0x67, 0xe1, 0x3b,
        };
        com.ravnaandtines.crypt.CryptTestUtils.checkStringVector(hash, 
            "abcdefghijklmnopqrstuvwxyz", expected);        
    }
    public void testAlphameric() throws java.io.UnsupportedEncodingException {
        final MD5 hash = new MD5();

        final int[] expected = {
            0xd1, 0x74, 0xab, 0x98,
            0xd2, 0x77, 0xd9, 0xf5,
            0xa5, 0x61, 0x1c, 0x2c,
            0x9f, 0x41, 0x9d, 0x9f,
        };
        com.ravnaandtines.crypt.CryptTestUtils.checkStringVector(hash, 
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789", 
            expected);        
    }
    public void testNumeric() throws java.io.UnsupportedEncodingException {
        final MD5 hash = new MD5();

        final int[] expected = {
            0x57, 0xed, 0xf4, 0xa2,
            0x2b, 0xe3, 0xc9, 0x55,
            0xac, 0x49, 0xda, 0x2e,
            0x21, 0x07, 0xb6, 0x7a,
        };
        com.ravnaandtines.crypt.CryptTestUtils.checkStringVector(hash, 
            "12345678901234567890123456789012345678901234567890123456789012345678901234567890", 
            expected);        
    }
}
