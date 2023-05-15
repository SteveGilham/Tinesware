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
public class BlowfishTest extends TestCase {

    public BlowfishTest(final String testName) {
        super(testName);
    }

    /**
     * Test of init_NN method, of class Blowfish.
     */
    public void testBlowfish() throws java.io.UnsupportedEncodingException {
        byte[] plaintext = "BLOWFISH".getBytes("ASCII");
        final byte[] key1 = "abcdefghijklmnopqrstuvwxyz".getBytes("ASCII");
        final byte[] key2 = "Who is John Galt?".getBytes("ASCII");

        Blowfish fish = new Blowfish(key1.length);
        final byte[] cipher = new byte[fish.getBlocksize()];
        final byte[] decode = new byte[fish.getBlocksize()];

        fish.init(key1, 0, false);
        fish.ecb(true, plaintext, 0, cipher, 0);
        fish.ecb(false, cipher, 0, decode, 0);
        fish.destroy();

        for (int i = 0; i < plaintext.length; i++) {
            assertEquals("round-trip error", plaintext[i], decode[i]&0xFF);
        }

//#define PL 0x424c4f57l
//#define PR 0x46495348l
//#define CL 0x324ed0fel
//#define CR 0xf413a203l

        final int[] expect1 = {
            0x32, 0x4e, 0xd0, 0xfe, 0xf4, 0x13, 0xa2, 0x03
        };
        for (int i = 0; i < 8; i++) {
            assertEquals("encypher error", expect1[i], cipher[i]&0xFF);
        }

        plaintext[0] = (byte) 0xfe;
        for (int i = 1; i < 8; i++) {
            plaintext[i] = (byte) ((plaintext[i - 1] - 0x22) & 0xFF);
        }

        fish = new Blowfish(key2.length);
        fish.init(key2, 0, false);
        fish.ecb(true, plaintext, 0, cipher, 0);
        fish.ecb(false, cipher, 0, decode, 0);
        fish.destroy();

//#define PL2 0xfedcba98l
//#define PR2 0x76543210l
//#define CL2 0xcc91732bl
//#define CR2 0x8022f684l

        final int[] expect2 = {
            0xcc, 0x91, 0x73, 0x2b, 0x80, 0x22, 0xf6, 0x84
        };
        for (int i = 0; i < 8; i++) {
            assertEquals("encypher error 2", expect2[i], cipher[i]&0xFF);
        }
        for (int i = 0; i < plaintext.length; i++) {
            assertEquals("round-trip error 2", plaintext[i]&0xFF, decode[i]&0xFF);
        }
        fish.destroy();
        
        fish = new Blowfish();
        assertEquals("default key length", 16, fish.getKeysize());        
        fish.destroy();
        
        boolean caught = false;
        try {
            fish = new Blowfish(-1);            
        } catch (IllegalArgumentException iae1) {
            caught = true;
        }
        assertTrue("-ve argument", caught);
        caught = false;
        try {
            fish = new Blowfish(Blowfish.MAXKEYBYTES+1);            
        } catch (IllegalArgumentException iae2) {
            caught = true;
        }
        assertTrue("big argument", caught);
    }
}
