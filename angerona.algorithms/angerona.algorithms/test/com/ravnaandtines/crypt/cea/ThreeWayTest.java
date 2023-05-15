/*
 * NOTE triples are reversed in the canonical print method
Test 1********
KEY =  : 00000000 00000000 00000000
PLAIN =  : 00000001 00000001 00000001
CIPHER =  : ad21ecf7 83ae9dc4 4059c76e
RECOVERED =  : 00000001 00000001 00000001
Test 2********
KEY =  : 00000004 00000005 00000006
PLAIN =  : 00000001 00000002 00000003
CIPHER =  : cab920cd d6144138 d2f05b5e
RECOVERED =  : 00000001 00000002 00000003
Test 3********
KEY =  : bcdef012 456789ab def01234
PLAIN =  : 01234567 9abcdef0 23456789
CIPHER =  : 7cdb76b2 9cdddb6d 0aa55dbb
RECOVERED =  : 01234567 9abcdef0 23456789
Test 4********
KEY =  : cab920cd d6144138 d2f05b5e
PLAIN =  : ad21ecf7 83ae9dc4 4059c76e
CIPHER =  : 15b155ed 6b13f17c 478ea871
RECOVERED =  : ad21ecf7 83ae9dc4 4059c76e
Block 0 set to 00000000 00000001 00000002
Block 1 set to 00000003 00000004 00000005
Block 2 set to 00000006 00000007 00000008
Block 0 encrypts to f1142732 27743185 c438576e
Block 1 encrypts to 725eee4c 2be2ea36 660c2e8f
Block 2 encrypts to 5f4a9483 2bb1bf7f 08bb7068
Block 0 decrypts to 00000000 00000001 00000002
Block 1 decrypts to 00000003 00000004 00000005
Block 2 decrypts to 00000006 00000007 00000008
API tests
ad21ecf7 83ae9dc4 4059c76e
ad21ecf7 83ae9dc4 4059c76e is expected result
cab920cd d6144138 d2f05b5e
cab920cd d6144138 d2f05b5e is expected result
7cdb76b2 9cdddb6d 0aa55dbb
7cdb76b2 9cdddb6d 0aa55dbb is expected result
15b155ed 6b13f17c 478ea871
15b155ed 6b13f17c 478ea871 is expected result
 * */
package com.ravnaandtines.crypt.cea;

import junit.framework.TestCase;

/**
 *
 * @author Steve
 */
public class ThreeWayTest extends TestCase { //NOPMD complex

    public ThreeWayTest(final String testName) {
        super(testName);
    }

    private static byte[] convertMSB(final int[] input) {
        byte[] result = new byte[4 * input.length];
        for (int i = 0; i < input.length; ++i) {
            final int index = 4 * i;
            result[index] = (byte) ((input[i] >> 24) & 0xFF);
            result[index + 1] = (byte) ((input[i] >> 16) & 0xFF);
            result[index + 2] = (byte) ((input[i] >> 8) & 0xFF);
            result[index + 3] = (byte) (input[i] & 0xFF);
        }
        return result;
    }

    /**
     * Test of init method, of class ThreeWay.
     */
    public void testInit() { //NOPMD complex

        final int[] userKey = new int[3];
        final int[] data = new int[9];
        {
            // test 1 
            for (int i = 0; i < 3; i++) {
                userKey[i] = 0;
                data[i] = 1;
            }
            byte[] key = convertMSB(userKey);
            byte[] text = convertMSB(data);
            final byte[] work1 = new byte[ThreeWay.BLOCKSIZE];
            final byte[] work2 = new byte[ThreeWay.BLOCKSIZE];

            ThreeWay cypher = new ThreeWay();
            cypher.init(key, 0, false);
            cypher.ecb(true, text, 0, work1, 0);
            cypher.ecb(false, work1, 0, work2, 0);
            cypher.destroy();

            /*
            Test 1********
            KEY =  : 00000000 00000000 00000000
            PLAIN =  : 00000001 00000001 00000001
            CIPHER =  : ad21ecf7 83ae9dc4 4059c76e
            RECOVERED =  : 00000001 00000001 00000001
             */
            final int[] expected1 = {
                0x40, 0x59, 0xc7, 0x6e,
                0x83, 0xae, 0x9d, 0xc4,
                0xad, 0x21, 0xec, 0xf7
            ,
                };

            for (int i = 0; i < ThreeWay.BLOCKSIZE; ++i) {
                assertEquals("test 1", expected1[i], work1[i] & 0xff);
            }

            for (int i = 0; i < ThreeWay.BLOCKSIZE; ++i) {
                assertEquals("round trip 1", text[i], work2[i] & 0xff);
            }

            // test 2 
            for (int i = 0; i < 3; i++) {
                userKey[i] = 6 - i;
                data[i] = 3 - i;
            }
            key = convertMSB(userKey);
            text = convertMSB(data);

            cypher = new ThreeWay();
            cypher.init(key, 0, false);
            cypher.ecb(true, text, 0, work1, 0);
            cypher.ecb(false, work1, 0, work2, 0);
            cypher.destroy();
            /*
            Test 2********
            KEY =  : 00000004 00000005 00000006
            PLAIN =  : 00000001 00000002 00000003
            CIPHER =  : cab920cd d6144138 d2f05b5e
            RECOVERED =  : 00000001 00000002 00000003
             */
            final int[] expected2 = {
                0xd2, 0xf0, 0x5b, 0x5e,
                0xd6, 0x14, 0x41, 0x38,
                0xca, 0xb9, 0x20, 0xcd
            };

            for (int i = 0; i < ThreeWay.BLOCKSIZE; ++i) {
                assertEquals("test 2", expected2[i], work1[i] & 0xff);
            }

            for (int i = 0; i < ThreeWay.BLOCKSIZE; ++i) {
                assertEquals("round trip 2", text[i], work2[i] & 0xff);
            }


            // test 3 
            userKey[2] = 0xbcdef012;
            userKey[1] = 0x456789ab;
            userKey[0] = 0xdef01234;
            data[2] = 0x01234567;
            data[1] = 0x9abcdef0;
            data[0] = 0x23456789;

            key = convertMSB(userKey);
            text = convertMSB(data);

            cypher = new ThreeWay();
            cypher.init(key, 0, false);
            cypher.ecb(true, text, 0, work1, 0);
            cypher.ecb(false, work1, 0, work2, 0);
            cypher.destroy();

            /*
            Test 3********
            KEY =  : bcdef012 456789ab def01234
            PLAIN =  : 01234567 9abcdef0 23456789
            CIPHER =  : 7cdb76b2 9cdddb6d 0aa55dbb
            RECOVERED =  : 01234567 9abcdef0 23456789
             */
            final int[] expected3 = {
                0x0a, 0xa5, 0x5d, 0xbb,
                0x9c, 0xdd, 0xdb, 0x6d,
                0x7c, 0xdb, 0x76, 0xb2
            };

            for (int i = 0; i < ThreeWay.BLOCKSIZE; ++i) {
                assertEquals("test 3", expected3[i], work1[i] & 0xff);
            }

            for (int i = 0; i < ThreeWay.BLOCKSIZE; ++i) {
                assertEquals("round trip 3", text[i] & 0xff, work2[i] & 0xff);
            }

            // test 4
            userKey[2] = 0xcab920cd;
            userKey[1] = 0xd6144138;
            userKey[0] = 0xd2f05b5e;
            data[2] = 0xad21ecf7;
            data[1] = 0x83ae9dc4;
            data[0] = 0x4059c76e;

            key = convertMSB(userKey);
            text = convertMSB(data);

            cypher = new ThreeWay();
            cypher.init(key, 0, false);
            cypher.ecb(true, text, 0, work1, 0);
            cypher.ecb(false, work1, 0, work2, 0);
            cypher.destroy();

            /*
            Test 4********
            KEY =  : cab920cd d6144138 d2f05b5e
            PLAIN =  : ad21ecf7 83ae9dc4 4059c76e
            CIPHER =  : 15b155ed 6b13f17c 478ea871
            RECOVERED =  : ad21ecf7 83ae9dc4 4059c76e
             */
            final int[] expected4 = {
                0x47, 0x8e, 0xa8, 0x71,
                0x6b, 0x13, 0xf1, 0x7c,
                0x15, 0xb1, 0x55, 0xed
            };

            for (int i = 0; i < ThreeWay.BLOCKSIZE; ++i) {
                assertEquals("test 4", expected4[i], work1[i] & 0xff);
            }

            for (int i = 0; i < ThreeWay.BLOCKSIZE; ++i) {
                assertEquals("round trip 4", text[i] & 0xff, work2[i] & 0xff);
            }
        }
        // Packaged API tests 
        {
            final byte[] key = new byte[ThreeWay.KEYSIZE];
            final byte[] input = new byte[ThreeWay.BLOCKSIZE];
            final byte[] out = new byte[ThreeWay.BLOCKSIZE];
            final byte[] savekey = new byte[ThreeWay.KEYSIZE];
            final byte[] savetext = new byte[ThreeWay.BLOCKSIZE];

            ThreeWay keysched = new ThreeWay();

            for (int i = 0; i < key.length; i++) {
                key[i] = 0;
            }
            for (int i = 0; i < input.length; i++) {
                input[i] = 0;
            }
            input[3] = input[7] = input[11] = 1;

            keysched.init(key, 0, false);
            keysched.ecb(true, input, 0, out, 0);
            keysched.destroy();

            final int[] expected1 = {
                0x40, 0x59, 0xc7, 0x6e,
                0x83, 0xae, 0x9d, 0xc4,
                0xad, 0x21, 0xec, 0xf7
            ,
                };
            for (int i = 0; i < out.length; i++) { //NOPMD
                assertEquals("API 1", expected1[i], 0xff & out[i]);
                savetext[i] = out[i];
            }

            for (int i = 0; i < key.length; i++) {
                key[i] = 0;
            }
            key[3] = 6;
            key[7] = 5;
            key[11] = 4;

            for (int i = 0; i < input.length; i++) {
                input[i] = 0;
            }
            input[3] = 3;
            input[7] = 2;
            input[11] = 1;

            keysched = new ThreeWay();
            keysched.init(key, 0, false);
            keysched.ecb(true, input, 0, out, 0);
            keysched.destroy();

            final int[] expected2 = {
                0xd2, 0xf0, 0x5b, 0x5e,
                0xd6, 0x14, 0x41, 0x38,
                0xca, 0xb9, 0x20, 0xcd
            ,
                };

            for (int i = 0; i < out.length; i++) { //NOPMD
                assertEquals("API 2", expected2[i], 0xff & out[i]);
                savekey[i] = out[i];
            }

            key[8] = (byte) 0xbc;
            key[9] = (byte) 0xde;
            key[10] = (byte) 0xf0;
            key[11] = (byte) 0x12;
            key[4] = (byte) 0x45;
            key[5] = (byte) 0x67;
            key[6] = (byte) 0x89;
            key[7] = (byte) 0xab;
            key[0] = (byte) 0xde;
            key[1] = (byte) 0xf0;
            key[2] = (byte) 0x12;
            key[3] = (byte) 0x34;

            input[8] = (byte) 0x01;
            input[9] = (byte) 0x23;
            input[10] = (byte) 0x45;
            input[11] = (byte) 0x67;
            input[4] = (byte) 0x9a;
            input[5] = (byte) 0xbc;
            input[6] = (byte) 0xde;
            input[7] = (byte) 0xf0;
            input[0] = (byte) 0x23;
            input[1] = (byte) 0x45;
            input[2] = (byte) 0x67;
            input[3] = (byte) 0x89;

            keysched = new ThreeWay();
            keysched.init(key, 0, false);
            keysched.ecb(true, input, 0, out, 0);
            keysched.destroy();

            final int[] expected3 = {
                0x0a, 0xa5, 0x5d, 0xbb,
                0x9c, 0xdd, 0xdb, 0x6d,
                0x7c, 0xdb, 0x76, 0xb2
            ,
                };

            for (int i = 0; i < out.length; i++) {
                assertEquals("API 3", expected3[i], 0xff & out[i]);
            }

            for (int i = 0; i < ThreeWay.KEYSIZE; i++) { //NOPMD
                key[i] = savekey[i];
            }
            for (int i = 0; i < ThreeWay.BLOCKSIZE; i++) { //NOPMD
                input[i] = savetext[i];
            }

            keysched = new ThreeWay();
            keysched.init(key, 0, false);
            keysched.ecb(true, input, 0, out, 0);
            keysched.destroy();

            final int[] expected4 = {
                0x47, 0x8e, 0xa8, 0x71,
                0x6b, 0x13, 0xf1, 0x7c,
                0x15, 0xb1, 0x55, 0xed,
                };
            for (int i = 0; i < out.length; i++) {
                assertEquals("API 4", expected4[i], 0xff & out[i]);
            }

        }
    }
}
