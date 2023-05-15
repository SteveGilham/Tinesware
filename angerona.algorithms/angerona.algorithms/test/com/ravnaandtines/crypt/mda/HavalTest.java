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
public class HavalTest extends TestCase {
    
    public HavalTest(final String testName) {
        super(testName);
    }


    private final class Wrapper {
        private final MDA hashContext;
        
        public Wrapper(int passes, int length)
        {
            hashContext = new Haval(passes, length);
        }
        
        public void ascii(final String vector) throws java.io.UnsupportedEncodingException
        {
            final byte[] data = vector.getBytes("ASCII");
            hashContext.update(data, 0, data.length);
        }
        
        public String printDigest() {
            final byte[] digest = hashContext.digest();
            final StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < digest.length / 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if ((0xFF & digest[i * 4 + j]) < 0x10) {
                        buffer.append('0');
                    }
                    buffer.append(Integer.toHexString(0xFF & digest[i * 4 + j]));
                }
                buffer.append(' ');
            }
            return buffer.toString().trim().toUpperCase();
        }
        
        
    }
    
    private void kernel(final Wrapper hash, 
            final String vector, final String expected)
             throws java.io.UnsupportedEncodingException
    {
	hash.ascii(vector);
	final String result = hash.printDigest ();
        //System.out.println("||"+expected+"||");
        //System.out.println("||"+result+"||");
        assertEquals(vector, expected.length(), result.length());
        assertEquals(vector, expected, result);                
    }

    public void testHaval()  throws java.io.UnsupportedEncodingException           
    {
        kernel( new Wrapper(2,128),
        "", "C68F3991 3F901F3D DF44C707 357A7D70");
        

        kernel( new Wrapper(3,160),
        "a", "4DA08F51 4A7275DB C4CECE4A 34738598 3983A830");

        kernel( new Wrapper(3,192),
	"HAVAL", "8DA26DDA B4317B39 2B22B638 998FE65B 0FBE4610 D345CF89");

        kernel( new Wrapper(3,224),
	"0123456789", "EE345C97 A58190BF 0F38BF7C E890231A A5FCF986 2BF8E7BE BBF76789");

        kernel( new Wrapper(3,256), 
	"abcdefghijklmnopqrstuvwxyz", 
                "72FAD4BD E1DA8C83 32FB6056 1A780E7F 504F2154 7B986868 24FC33FC 796AFA76");

        kernel( new Wrapper(3,256), 
	"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789",
                "899397D9 6489281E 9E76D5E6 5ABAB751 F312E06C 06C07C9C 1D42ABD3 1BB6A404");

        kernel( new Wrapper(4, 128), 
	"", "EE6BBF4D 6A46A679 B3A856C8 8538BB98");

        kernel( new Wrapper(4, 160),
	"a", "E0A5BE29 62733203 4D4DD8A9 10A1A0E6 FE04084D");

        kernel( new Wrapper(4, 192),
	"HAVAL", "0C1396D7 772689C4 6773F3DA ACA4EFA9 82ADBFB2 F1467EEA");

        kernel( new Wrapper(4, 224),
	"0123456789", "BEBD7816 F09BAEEC F8903B1B 9BC672D9 FA428E46 2BA699F8 14841529");

        kernel( new Wrapper(4, 256),
	"abcdefghijklmnopqrstuvwxyz",
        "124F6EB6 45DC4076 37F8F719 CC312500 89C89903 BF1DB8FA C21EA461 4DF4E99A");

        kernel( new Wrapper(4, 256),
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789",
        "46A3A1DF E867EDE6 52425CCD 7FE80065 37EAD263 72251686 BEA286DA 152DC35A");

        kernel( new Wrapper(5, 128), 
        "", "184B8482 A0C050DC A54B59C7 F05BF5DD");

        kernel( new Wrapper(5, 160), 
	"a", "F5147DF7 ABC5E3C8 1B031268 927C2B57 61B5A2B5");

        kernel( new Wrapper(5, 192),
	"HAVAL", "794A896D 1780B76E 2767CC40 11BAD888 5D5CE6BD 835A71B8");

        kernel( new Wrapper(5, 224),
	"0123456789", "59836D19 269135BC 815F37B2 AEB15F89 4B5435F2 C698D577 16760F2B");

        kernel( new Wrapper(5, 256),
	"abcdefghijklmnopqrstuvwxyz",
        "C9C7D8AF A159FD9E 965CB83F F5EE6F58 AEDA352C 0EFF0055 48153A61 551C38EE");

        kernel( new Wrapper(6, 256),
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789",
        "B45CB6E6 2F2B1320 E4F8F1B0 B273D45A DD47C321 FD23999D CF403AC3 7636D963");
    }
}
