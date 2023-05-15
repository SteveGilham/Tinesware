/*
 * Some examples:
 * ""                    9c1185a5c5e9fc54612808977ee8f548b2258d31
 * "a"                   0bdc9d2d256b3ee9daae347be6f4dc835a467ffe
 * "abc"                 8eb208f7e05d987a9b044a8e98c6b087f15a0bfc
 * "message digest"      5d0689ef49d2fae572b881b123a85ffa21595f36
 * "a...z"               f71c27109c692c1b56bbdceb5b9d2865b3708dbc
 * "abcdbcde...nopq"     12a053384a9c0c88e405a06c27dcf49ada62eb2b
 * "A...Za...z0...9"     b0e20b6e3116640286ed3a87a5713079b21f5189
 * 8 times "1234567890"  9b752e45573d4b39f4dbd3323cab82bf63326bfb
 * 1 million times "a"   52783243c1697bdbe16d37f97f68f08325dc1528
 */

package com.ravnaandtines.crypt.mda;

import junit.framework.TestCase;

/**
 *
 * @author Steve
 */
public class RIPEM160Test extends TestCase {
    
    public RIPEM160Test(final String testName) {
        super(testName);
    }

    public void testEmpty() {
        final RIPEM160 hash = new RIPEM160();
        hash.update(null, 0, 0);
        final int[] expected = {
            0x9c, 0x11, 0x85, 0xa5, 
            0xc5, 0xe9, 0xfc, 0x54,
            0x61, 0x28, 0x08, 0x97,
            0x7e, 0xe8, 0xf5, 0x48,
            0xb2, 0x25, 0x8d, 0x31
        };
        com.ravnaandtines.crypt.CryptTestUtils.checkVector(hash, expected);
    }

    public void testa() {
        final RIPEM160 hash = new RIPEM160();
        hash.update((byte)'a');
        final int[] expected = {
            0x0b, 0xdc, 0x9d, 0x2d,
            0x25, 0x6b, 0x3e, 0xe9,
            0xda, 0xae, 0x34, 0x7b,
            0xe6, 0xf4, 0xdc, 0x83,
            0x5a, 0x46, 0x7f, 0xfe
        };
        com.ravnaandtines.crypt.CryptTestUtils.checkVector(hash, expected);
    }
    
    public void testabc() {
        final RIPEM160 hash = new RIPEM160();
        hash.update((byte)'a');
        hash.update((byte)'b');
        hash.update((byte)'c');
        final int[] expected = {
            0x8e, 0xb2, 0x08, 0xf7,
            0xe0, 0x5d, 0x98, 0x7a,
            0x9b, 0x04, 0x4a, 0x8e,
            0x98, 0xc6, 0xb0, 0x87,
            0xf1, 0x5a, 0x0b, 0xfc,
        };
        com.ravnaandtines.crypt.CryptTestUtils.checkVector(hash, expected);
    }
    
    public void testMessageDigest() throws java.io.UnsupportedEncodingException {
        final RIPEM160 hash = new RIPEM160();
        final String vector = "message digest";

        final int[] expected = {
            0x5d, 0x06, 0x89, 0xef,
            0x49, 0xd2, 0xfa, 0xe5,
            0x72, 0xb8, 0x81, 0xb1,
            0x23, 0xa8, 0x5f, 0xfa,
            0x21, 0x59, 0x5f, 0x36,
        };
        
        com.ravnaandtines.crypt.CryptTestUtils.checkStringVector(hash, 
            vector, expected);        

    }
    
    public void testAlpha() throws java.io.UnsupportedEncodingException {
        final RIPEM160 hash = new RIPEM160();
        final String vector = "abcdefghijklmnopqrstuvwxyz";

        final int[] expected = {
            0xf7, 0x1c, 0x27, 0x10,
            0x9c, 0x69, 0x2c, 0x1b,
            0x56, 0xbb, 0xdc, 0xeb,
            0x5b, 0x9d, 0x28, 0x65,
            0xb3, 0x70, 0x8d, 0xbc,
        };
        com.ravnaandtines.crypt.CryptTestUtils.checkStringVector(hash, 
            vector, expected);        
    }
    
    public void testAlpha2() throws java.io.UnsupportedEncodingException {
        final RIPEM160 hash = new RIPEM160();
        final String vector = "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq";

        final int[] expected = {
            0x12, 0xa0, 0x53, 0x38,
            0x4a, 0x9c, 0x0c, 0x88,
            0xe4, 0x05, 0xa0, 0x6c,
            0x27, 0xdc, 0xf4, 0x9a,
            0xda, 0x62, 0xeb, 0x2b,
        };
        com.ravnaandtines.crypt.CryptTestUtils.checkStringVector(hash, 
            vector, expected);        
    }
    
    public void testAlphameric() throws java.io.UnsupportedEncodingException {
        final RIPEM160 hash = new RIPEM160();
        final String vector = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        final int[] expected = {
            0xb0, 0xe2, 0x0b, 0x6e,
            0x31, 0x16, 0x64, 0x02,
            0x86, 0xed, 0x3a, 0x87,
            0xa5, 0x71, 0x30, 0x79,
            0xb2, 0x1f, 0x51, 0x89,
        };
        com.ravnaandtines.crypt.CryptTestUtils.checkStringVector(hash, 
            vector, expected);        
    }
    
    public void testNumeric() throws java.io.UnsupportedEncodingException {
        final RIPEM160 hash = new RIPEM160();
        final String vector = "1234567890";
        final byte[] data = vector.getBytes("ASCII");

        for(int i=0; i<8; ++i)
        {
            hash.update(data, 0, data.length);
        }

        final int[] expected = {
            0x9b, 0x75, 0x2e, 0x45,
            0x57, 0x3d, 0x4b, 0x39,
            0xf4, 0xdb, 0xd3, 0x32,
            0x3c, 0xab, 0x82, 0xbf,
            0x63, 0x32, 0x6b, 0xfb,
        };
        com.ravnaandtines.crypt.CryptTestUtils.checkVector(hash, expected);
    }
              
    public void testMilliona() {
        final RIPEM160 hash = new RIPEM160();
        final byte[] block = new byte[100];
        for (int i = 0; i < 100; i++) {
            block[i] = ((byte) 'a');
        }
        
        hash.update(block, 0, 30);
        hash.update(block, 0, 30);
        hash.update(block, 0, 30);
        hash.update(block, 0, 10);        
        for (int i = 0; i < 9999; i++) {
            hash.update(block, 0, block.length);
        }

        final int[] expected = {
            0x52, 0x78, 0x32, 0x43,
            0xc1, 0x69, 0x7b, 0xdb,
            0xe1, 0x6d, 0x37, 0xf9,
            0x7f, 0x68, 0xf0, 0x83,
            0x25, 0xdc, 0x15, 0x28,
        };
        com.ravnaandtines.crypt.CryptTestUtils.checkVector(hash, expected);
    }  
}
