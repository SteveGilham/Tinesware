/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ravnaandtines.crypt;

import junit.framework.TestCase;
import com.ravnaandtines.crypt.mda.MDA;

/**
 *
 * @author Steve
 */
public final class CryptTestUtils {

    private CryptTestUtils() {
    }

    public static void checkVector(final MDA hash, final int[] expected) {
        final byte[] result = hash.digest();
        TestCase.assertEquals("Size mismatch", expected.length, result.length);
        for (int i = 0; i < expected.length; ++i) {
            TestCase.assertEquals("mismatch at offset " + i, expected[i], 0xFF & result[i]);
        }
    }

    public static void checkStringVector(final MDA hash,
            final String vector, final int[] expected) throws java.io.UnsupportedEncodingException {
        final byte[] data = vector.getBytes("ASCII");

        hash.update(data, 0, data.length);
        hash.update(null, 0, data.length);
        com.ravnaandtines.crypt.CryptTestUtils.checkVector(hash, expected);
    }

    public static java.util.Vector find(final String pckgname) { //NO-PMD stuck with 1.1

        // Translate the package name into an absolute path
        String name = pckgname;
        if (!name.startsWith("/")) {
            name = "/" + name;
        }
        name = name.replace('.', '/');
        // Get a File object for the package
        final java.net.URL url =
                new CryptTestUtils().getClass().getResource(name);

        final String dirpath = java.net.URLDecoder.decode(
                url.getFile());

        final java.io.File directory = new java.io.File(dirpath);
        final java.util.Vector contents = new java.util.Vector(); //NO-PMD 1.1
        // New code
        // ======

        if (directory.exists()) {
            // Get the list of the files contained in the package
            final String[] files = directory.list();
            for (int i = 0; i < files.length; i++) {

                // we are only interested in .class files
                if (files[i].endsWith(".class")) {
                    // removes the .class extension
                    final String classname = 
                            files[i].substring(0, files[i].length() - 6);
                    try {
                        // Try to create an instance of the object
                        final Object obj = 
                                Class.forName(pckgname + "." + classname).newInstance();
                        contents.add(obj.getClass());
                    } catch (ClassNotFoundException cnfex) { //NO-PMD
                    } catch (InstantiationException iex) { //NO-PMD
                    // We try to instantiate an interface
                    // or an object that does not have a 
                    // default constructor
                    } catch (IllegalAccessException iaex) { //NO-PMD
                    // The class is not public
                    }
                }
            }
        }
            return contents;
    }
}
