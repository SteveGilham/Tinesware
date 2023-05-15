/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ravnaandtines.crypt.mda;

import junit.framework.TestCase;

/**
 *
 * @author Steve
 */
public class BlockCypherHashTest extends TestCase {

    public BlockCypherHashTest(final String testName) {
        super(testName);
    }

    public void ascii(final BlockCypherHash hash,
            final String vector) throws java.io.UnsupportedEncodingException {
        final byte[] data = vector.getBytes("ASCII");
        hash.update(data, 0, data.length);
    }

    public String printDigest(final BlockCypherHash hash) {
        final byte[] digest = hash.digest();
        final StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < digest.length / 4; i++) {
            for (int j = 0; j < 4; j++) {
                if ((0xFF & digest[i * 4 + j]) < 0x10) {
                    buffer.append('0');
                }
                buffer.append(Integer.toHexString(0xFF & digest[i * 4 + j]));
                buffer.append(' ');
            }
        }
        return buffer.toString().trim();
    }

    private void kernel(final BlockCypherHash hash,
            final String vector, final String expected)
            throws java.io.UnsupportedEncodingException {
        ascii(hash, vector);
        final String result = printDigest(hash);
        //System.out.println("||"+expected+"||");
        //System.out.println("||"+result+"||");
        assertEquals(vector, expected.length(), result.length());
        assertEquals(vector, expected, result);
    }

    /**
     * Test of update method, of class BlockCypherHash.
     */
    public void testThing() throws java.io.UnsupportedEncodingException {
        {
            kernel(new BlockCypherHash(),
                    "abc", "8d 19 d8 bd 5d d7 e4 7f 18 e0 b7 85");

            kernel(new BlockCypherHash(),
                    "The quick brown fox jumps over the lazy dog.",
                    "f1 c8 c3 67 4d 4c 4e a0 93 a5 38 75");

            kernel(new BlockCypherHash(),
                    "Vext cwm fly zing! jabs Kurd qoph.",
                    "2f 73 0c 5b 49 94 12 18 20 54 21 51");

            kernel(new BlockCypherHash(),
                    "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq",
                    "21 a8 8a ea 2c a7 bf 3d 19 d9 fb 67");

            BlockCypherHash hash = new BlockCypherHash();
            byte[] aArray = new byte[8192];
            int count;

            for (int i = 0; i < 8192; i++) {
                aArray[i] = (byte) 'a';
            }

            for (count = 1000000; count > 0; count -= 8192) {
                final int length = (count > 8192) ? 8192 : (int) count;
                hash.update(aArray, 0, length);
            }
            kernel(hash, "",
                    "3c 05 ae 8f b7 17 61 93 7c 04 34 8d");

            try {
                final com.ravnaandtines.crypt.cea.CEA failure = new com.ravnaandtines.crypt.cea.CAST5();
                hash = new BlockCypherHash(failure);
            } catch (IllegalStateException ise) {
                return;
            }
            fail("should have thrown");
        }

    }
}
