/*
 * http://www.cix.co.uk/~klockstone/teavect.htm
 */
package com.ravnaandtines.crypt.cea;

import junit.framework.TestCase;

/**
 *
 * @author Steve
 */
public class TEATest extends TestCase {

    public TEATest(final String testName) {
        super(testName);
    }

    private final static int[][] expected = {
    { 0x41ea3a0a, 0x94baa940,          0,          0,          0,          0},
    { 0x4e8e7829, 0x7d8236d8,          0,          0,          0, 0x41ea3a0a},
    { 0xb84e28af, 0xb6b62088,          0, 0x41ea3a0a, 0x4e8e7829, 0xc88ba95e},
    { 0x5ddf75d9, 0x7a4ce68f, 0xb84e28af, 0xa0a47295, 0xed650698, 0x1024eea0},
    { 0x4ec5d2e2, 0x5ada1d89, 0xa9c3801a, 0x32a1e654,  0x8b63bb9, 0x21410574},
    { 0x7d2c6c57, 0x7a6adb4d, 0x69c53e0f, 0x60388ada, 0xdf70a1f5, 0xd9cb4e09},
    { 0x2bb0f1b3, 0xc023ed11, 0x5c60bff2, 0x7072d01c, 0x4513c5eb, 0x8f3a38ab},
    };


    /**
     * Test of test class TEA.
     */
    public void testTea() {
        final byte[] workspace = new byte[4096];
        for(int i=0; i<workspace.length; ++i)
        {
            workspace[i] = 0;
        }

        int power = 0;

        //System.out.println("TEA test");
        for (int n = 1; n < 65; n++) {
            final TEA code = new TEA(); //NOPMD yes, in a loop

            code.init(workspace, 4*(n+2), false);
            code.ecb(true, workspace, 4*n, workspace, 4*n);
            code.destroy();

            if (n == (n & -n)) /* if n power of 2 */ {
                for(int i=0; i<6; ++i)
                {
                    final int index = 4*(n+i);
                    final int value = ((workspace[index]&0xff)<<24)
                            |((workspace[index+1]&0xff)<<16)
                            |((workspace[index+2]&0xff)<< 8)
                            |((workspace[index+3]&0xff));
                    assertEquals("intermediate value", expected[power][i], value);
                }
                ++power;
            }
            System.arraycopy(workspace, 4*n, workspace, 4*(n+6), 4);
        }

        for (int n = 64; n > 0; n--) {
            final TEA code = new TEA(); //NOPMD yes, in a loop
            code.init(workspace, 4*(n+2), false);
            code.ecb(false, workspace, 4*n, workspace, 4*n);
            code.destroy();

        }

        for(int i=0; i<24; ++i)
        {
            assertEquals("back to nothing", 0, workspace[i]);
        }
        //System.out.println("TEA test done");      
    }
}
