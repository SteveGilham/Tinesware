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
public class SquareTest extends TestCase { //NOPMD complex
    
    public SquareTest(final String testName) {
        super(testName);
    }

    /**
     * Test of class Square.
     */
    public void testTest() { //NOPMD complex

        final byte[] key = {
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
            0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
            0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
        };
        final byte[] plaintext = {
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
            0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
            };
    	final byte[] ciphertext = {
    		(byte)0x7c, (byte) 0x34, (byte) 0x91, (byte) 0xd9,
            (byte) 0x49, (byte) 0x94, (byte) 0xe7, (byte) 0x0f,
            (byte) 0x0e, (byte) 0xc2, (byte) 0xe7, (byte) 0xa5,
            (byte) 0xcc, (byte) 0xb5, (byte) 0xa1, (byte) 0x4f
        ,
            
          };
        final byte[] data = new byte[Square.BLOCK_LENGTH];
        final Square crypto = new Square();
        crypto.init(key, 0, false);
        crypto.ecb(true, plaintext, 0, data, 0);
        CHECK_ENCRYPTION:
        {
            for (int i = 0; i < Square.BLOCK_LENGTH; i++) {
                assertEquals("encrypted(ERROR)", ciphertext[i], data[i]);
            }
        }
        crypto.ecb(false, data, 0, data, 0);

        for (int i = 0; i < Square.BLOCK_LENGTH; i++) {
            assertEquals("encrypted(ERROR)", plaintext[i], data[i]);
        }


        /*
        final int iterations = 10000;
        if (iterations > 0) {
            long elapsed;
            float secs;
            // measure encryption speed:
            elapsed = -System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                crypto.ecb(true, data, 0, data, 0);
            }
            elapsed += System.currentTimeMillis();
            secs = (elapsed > 1) ? (float) elapsed / 1000 : 1;
            System.out.println("Encryption elapsed time = " + secs +  //NOPMD
                    ", speed = " +
                    ((double) iterations * Square.BLOCK_LENGTH / 1024 / secs)
                    + " kbytes/s");
            // measure encryption speed:
            elapsed = -System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                crypto.ecb(false, data, 0, data, 0);
            }
            elapsed += System.currentTimeMillis();
            secs = (elapsed > 1) ? (float) elapsed / 1000 : 1;
            System.out.println("Decryption elapsed time = " + secs + //NOPMD
                    ", speed = " + 
                    ((double) iterations * Square.BLOCK_LENGTH / 1024 / secs) 
                    + " kbytes/s");
        }
         */
    }


}
