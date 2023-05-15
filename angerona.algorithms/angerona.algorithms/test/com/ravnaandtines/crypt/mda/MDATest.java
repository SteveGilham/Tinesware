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
public class MDATest extends TestCase {

    public MDATest(final String testName) {
        super(testName);
    }

    /**
     * Test of update method, of class MDA.
     */
    public void testMetaTest() throws InstantiationException, IllegalAccessException {
        final java.util.Vector classes =
                com.ravnaandtines.crypt.CryptTestUtils.find(
                "com.ravnaandtines.crypt.mda");
        
        final byte[] baseline = new byte[3];
        for (int i = 0; i < 3; ++i) {
            baseline[i] = (byte) (i & 0xFF);
        }
        final byte[] offset = new byte[12];
        for (int i = 0; i < 12; ++i) {
            offset[i] = (byte) ((i - 4) & 0xFF);
        }

        for (final java.util.Enumeration iter = classes.elements(); 
            iter.hasMoreElements();) {
            final Class underTest = (Class) iter.nextElement();

            final Object test = underTest.newInstance();
            if (!(test instanceof com.ravnaandtines.crypt.mda.MDA)) {
                continue;
            }

            MDA hash = (MDA) test;
            hash.update(baseline, 0, baseline.length);
            final byte[] result1 = hash.digest();

            hash = (MDA) underTest.newInstance();
            hash.update(offset[4]);
            hash.update(offset[5]);
            hash.update(offset[6]);
            byte[] result2 = hash.digest();

            assertEquals("part 1 " + underTest.getName(),
                    result1.length, result2.length);
            for (int i = 0; i < result1.length; ++i) {
                assertEquals("part 1b " + underTest.getName(),
                        result1[i], result2[i]);
            }

            hash = (MDA) underTest.newInstance();
            hash.update(offset, 4, 3);
            result2 = hash.digest();

            assertEquals("part 2 " + underTest.getName(),
                    result1.length, result2.length);
            for (int i = 0; i < result1.length; ++i) {
                assertEquals("part 2b " + underTest.getName(),
                        result1[i], result2[i]);
            }

        }



    }
}
