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
public class IDEAfreeTest extends TestCase {
    
    public IDEAfreeTest(final String testName) {
        super(testName);
    }


    /**
     * Test of isAvailable method, of class IDEAfree.
     */
    public void testIsAvailable() {
        final boolean expResult = false;
        final boolean result = IDEAfree.isAvailable();
        assertEquals("availability",expResult, result);
    }

    /**
     * Test of IDEA_licence method, of class IDEAfree.
     */
    public void testLlicence() {
        final String expResult = "";
        final String result = IDEAfree.getLicence();
        assertEquals("empty", expResult, result);
    }

    /**
     * Test of init method, of class IDEAfree.
     */
    public void testInit() {
        final byte[] key = null;
        final int offset = 0;
        final boolean triple = false;
        final IDEAfree instance = new IDEAfree();
        instance.init(key, offset, triple);
        assertNotNull("stuff", instance);
    }

    /**
     * Test of ecb method, of class IDEAfree.
     */
    public void testEcb() throws java.io.UnsupportedEncodingException
    {
        final boolean encrypt = false;
        final byte[] input = "IDEAfree".getBytes("ASCII");
        final byte[] out = new byte[8];
        final IDEAfree instance = new IDEAfree();
        
        instance.ecb(encrypt, input, 0, out, 0);
        for(int i=0; i<8; ++i)
        {
            assertEquals("Is no-op", input[i], out[i]);
        }
    }

    /**
     * Test of destroy method, of class IDEAfree.
     */
    public void testDestroy() {
        final IDEAfree instance = new IDEAfree();
        instance.destroy();
        instance.destroy();        
        assertNotNull("stuff", instance);
    }

}
