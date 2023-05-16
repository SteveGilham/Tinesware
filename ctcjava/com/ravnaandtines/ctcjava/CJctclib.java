
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;

public class CJctclib {

    /* Examine and decypher a binary input file */
    static native int examine(CJTempfile input, boolean split);

    /* Examine and decypher an armoured text input file */
    static native int examine_text(CJTempfile input, boolean split);

    /* Encrypt file according to instructions (closes source file) */
    static native boolean encrypt(CJTempfile source, CJTempfile output);

    /* Sign file according to instructions (closes source file) */
    static native boolean signOnly(CJTempfile source, CJTempfile output);

    /* Generate keypair according to instructions */
    static native CJSeckey makePKEkey(byte algorithm, int keylength,
    int primeMethod, String name, byte selfSignAlg, byte kpAlg, byte[] passphrase);

    /* externalise all secret keys */
    static native void lock();
}
