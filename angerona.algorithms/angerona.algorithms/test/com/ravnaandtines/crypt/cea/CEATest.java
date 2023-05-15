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
public class CEATest extends TestCase {
    
    public CEATest(final String testName) {
        super(testName);
    }


    /**
     * Test of offsets
     */
    public void testMetaTest() throws InstantiationException, IllegalAccessException {
        final java.util.Vector classes =
                com.ravnaandtines.crypt.CryptTestUtils.find(
                "com.ravnaandtines.crypt.cea");
        
        final byte[] raw = new byte[256];
        for(int i=0; i<raw.length; ++i)
        {
            raw[i] = (byte)i;
        }

        final byte[] work1 = new byte[256];
        final byte[] work2 = new byte[256];
        final byte[] work3 = new byte[256];
        final byte[] work4 = new byte[256];
        final byte[] key   = new byte[256];
        final byte[] plain = new byte[256];

        final int enough = 64;
        
        final int keyoffset = 23;
        System.arraycopy(raw, keyoffset, key, 0, enough);
        
        final int inoffset = 17;
        System.arraycopy(raw, inoffset, plain, 0, enough);

        final int outoffset = 29;
        final int backoffset = 7;
        
        for (final java.util.Enumeration iter = classes.elements(); 
            iter.hasMoreElements();) {
            final Class underTest = (Class) iter.nextElement();

            final Object test = underTest.newInstance();
            if (!(test instanceof com.ravnaandtines.crypt.cea.CEA)) {
                continue;
            }

            System.out.println(underTest.toString());
            
            final CEA crypto1 = (CEA) test;
            final CEA crypto2 = (CEA) underTest.newInstance();
            
            final int keysize = crypto1.getKeysize();
            assertEquals("key size is constant", keysize, crypto2.getKeysize());

            final int blocksize = crypto1.getBlocksize();
            assertEquals("block size is constant", blocksize, crypto2.getBlocksize());
            
            crypto1.init(key, 0, false);
            crypto2.init(raw, keyoffset, false);
            
            crypto1.ecb(true, plain, 0, work1, 0);
            crypto2.ecb(true, raw, inoffset, work2, outoffset);
            
            for(int i=0; i<blocksize; ++i)
            {
                assertEquals("same ciphertext "+underTest.toString(),
                        work1[i], work2[outoffset+i]);
            }
            
            crypto1.ecb(false, work1, 0, work3, 0);
            crypto2.ecb(false, work2, outoffset, work4, backoffset);
            
            crypto1.destroy();
            crypto2.destroy();
            crypto2.destroy();
            crypto2.destroy();
            
            for(int i=0; i<blocksize; ++i)
            {
                assertEquals("same plaintext "+underTest.toString(), 
                        work3[i], work4[backoffset+i]);
                assertEquals("round trip "+underTest.toString(), 
                        work3[i], plain[i]);
            }
            

        }
    }

    /**
     * Test of offsets and triple mode
     */
    public void testMetaTest2() throws InstantiationException, IllegalAccessException 
    {
        final java.util.Vector classes =
                com.ravnaandtines.crypt.CryptTestUtils.find(
                "com.ravnaandtines.crypt.cea");
        
        final byte[] raw = new byte[256];
        for(int i=0; i<raw.length; ++i)
        {
            raw[i] = (byte)i;
        }

        final byte[] work1 = new byte[256];
        final byte[] work2 = new byte[256];
        final byte[] work3 = new byte[256];
        final byte[] work4 = new byte[256];
        final byte[] key   = new byte[256];
        final byte[] plain = new byte[256];

        final int enough = 128;
        
        final int keyoffset = 23;
        System.arraycopy(raw, keyoffset, key, 0, enough);
        
        final int inoffset = 17;
        System.arraycopy(raw, inoffset, plain, 0, enough);

        final int outoffset = 29;
        final int backoffset = 7;
        
        for (final java.util.Enumeration iter = classes.elements(); 
            iter.hasMoreElements();) {
            final Class underTest = (Class) iter.nextElement();

            final Object test = underTest.newInstance();
            if (!(test instanceof com.ravnaandtines.crypt.cea.CEA)) {
                continue;
            }
            if (test instanceof com.ravnaandtines.crypt.cea.SpecialTriple) {
                continue;
            }

            System.out.println(underTest.toString());
            
            final CEA crypto1 = (CEA) test;
            final CEA crypto2 = (CEA) underTest.newInstance();
            
            final int keysize = crypto1.getKeysize();
            assertEquals("key size is constant", keysize, crypto2.getKeysize());

            final int blocksize = crypto1.getBlocksize();
            assertEquals("block size is constant", blocksize, crypto2.getBlocksize());
            
            crypto1.init(key, 0, true);
            crypto2.init(raw, keyoffset, true);
            
            crypto1.ecb(true, plain, 0, work1, 0);
            crypto2.ecb(true, raw, inoffset, work2, outoffset);
            
            for(int i=0; i<blocksize; ++i)
            {
                assertEquals("same ciphertext "+underTest.toString(),
                        work1[i], work2[outoffset+i]);
            }
            
            crypto1.ecb(false, work1, 0, work3, 0);
            crypto2.ecb(false, work2, outoffset, work4, backoffset);
            crypto1.destroy();
            crypto2.destroy();
            
            for(int i=0; i<blocksize; ++i)
            {
                assertEquals("same plaintext "+underTest.toString(), 
                        work3[i], work4[backoffset+i]);
                assertEquals("round trip "+underTest.toString(), 
                        work3[i], plain[i]);
            }
            

        }


    }
    
    /**
     * Test of triple mode
     */
    public void testMetaTest3() throws InstantiationException, IllegalAccessException 
    {
        final java.util.Vector classes =
                com.ravnaandtines.crypt.CryptTestUtils.find(
                "com.ravnaandtines.crypt.cea");
        
        final byte[] raw = new byte[256];
        for(int i=0; i<raw.length; ++i)
        {
            raw[i] = (byte)i;
        }

        final byte[] work1 = new byte[256];
        final byte[] work2 = new byte[256];
        final byte[] work3 = new byte[256];
        final byte[] work4 = new byte[256];
        final byte[] key   = new byte[256];
        final byte[] plain = new byte[256];

        final int enough = 128;
        
        final int keyoffset = 23;
        System.arraycopy(raw, keyoffset, key, 0, enough);
        
        final int inoffset = 17;
        System.arraycopy(raw, inoffset, plain, 0, enough);

        final int outoffset = 29;
        final int backoffset = 7;
        
        for (final java.util.Enumeration iter = classes.elements(); 
            iter.hasMoreElements();) {
            final Class underTest = (Class) iter.nextElement();

            final Object test = underTest.newInstance();
            if (!(test instanceof com.ravnaandtines.crypt.cea.CEA)) {
                continue;
            }
            if (test instanceof com.ravnaandtines.crypt.cea.SpecialTriple) {
                continue;
            }

            System.out.println(underTest.toString());
            
            final CEA crypto0 = (CEA) test;
            final CEA crypto1 = (CEA) underTest.newInstance();
            final CEA crypto2 = (CEA) underTest.newInstance();
            final CEA crypto3 = (CEA) underTest.newInstance();
            
            final int keysize = crypto1.getKeysize();
            final int blocksize = crypto1.getBlocksize();
            
            crypto0.init(key, 0, true);
            crypto1.init(raw, keyoffset, false);
            crypto2.init(raw, keyoffset+keysize, false);
            crypto3.init(raw, keyoffset+2*keysize, false);
            
            crypto0.ecb(true, plain, 0, work1, 0);
            crypto1.ecb(true, raw, inoffset, work2, outoffset);
            crypto2.ecb(true, work2, outoffset, work3, backoffset);
            crypto3.ecb(true, work3, backoffset, work2, outoffset);
                        
            for(int i=0; i<blocksize; ++i)
            {
                assertEquals("same ciphertext "+underTest.toString(),
                        work1[i], work2[outoffset+i]);
            }
            
            crypto0.ecb(false, work1, 0, work3, 0);
            crypto3.ecb(false, work2, outoffset, work4, backoffset);
            crypto2.ecb(false, work4, backoffset, work2, outoffset);
            crypto1.ecb(false, work2, outoffset, work4, backoffset);
            
            for(int i=0; i<blocksize; ++i)
            {
                assertEquals("same plaintext "+underTest.toString(), 
                        work3[i], work4[backoffset+i]);
                assertEquals("round trip "+underTest.toString(), 
                        work3[i], plain[i]);
            }
            
            crypto3.destroy();
            crypto2.destroy();
            crypto1.destroy();
            crypto0.destroy();
            

        }


    }
    
}
