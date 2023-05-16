
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;

import java.util.Date;
import java.text.*;

public class CJPubkey {

    private long pointer = 0;
    java.util.ResourceBundle res = java.util.ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res");

    private CJPubkey()
    {
    }

    private CJPubkey(long handle)
    {
        pointer = handle;
    }

    public boolean isNull()
    {
        return pointer == 0;
    }

    public long cHandle()
    {
        return pointer;
    }

    public CJPubkey nextPubkey()
    {
        return getNextPubkey(pointer);
    }
    private native CJPubkey getNextPubkey(long ptr);

    public String toString()
    {
        return getName();
    }

    public String getName()
    {
        return name_from_key(pointer);
    }
    private native String name_from_key(long ptr);

    public boolean isEnabled()
    {
        return isEnabled(pointer);
    }
    private native boolean isEnabled(long ptr);

    public boolean isRevoked()
    {
        return isRevoked(pointer);
    }
    private native boolean isRevoked(long ptr);

    public boolean extract()
    {
        return keyExtract(pointer);
    }
    private native boolean keyExtract(long ptr);

    public void sign(CJSeckey sk)
    {
        CJAlgPanel ap = CJGlobals.mainFrame.algPanel;
        byte alg = (byte) ap.mdan[ap.MDAbox.getSelectedIndex()];
        keySign(pointer, sk.cHandle(), alg);
    }
    private native void keySign(long ptr, long signer, byte mdalg);

    public void able()
    {
        keyAble(pointer);
    }
    private native void keyAble(long ptr);

    public void delete()
    {
        keyDelete(pointer);
    }
    private native void keyDelete(long ptr);

    public String data()
    {
        int size = keySize(pointer);
        byte alg = (byte) keyAlg(pointer);
        String id = Long.toHexString(keyID(pointer));
        String print = keyPrint(pointer);
        long t = ((long)keyDate(pointer))&0x00000000FFFFFFFFL;
        Date date = new Date(1000L*t);

        StringBuffer buf = new StringBuffer("");
        if(!isEnabled()) buf.append(res.getString("DISABLED_KEY_"));
        boolean valid = true;
        switch(alg)
        {
        case CTCIKeyConst.PKA_EBP_RSA:
        case CTCIKeyConst.PKA_RSA: 
            buf.append(res.getString("RSA")); 
            break;
        case CTCIKeyConst.PKA_RSA_ENCRYPT_ONLY: 
            buf.append(res.getString("RSAencrypt")); 
            break;
        case CTCIKeyConst.PKA_RSA_SIGN_ONLY: 
            buf.append(res.getString("RSAsign")); 
            break;
        case CTCIKeyConst.PKA_ELGAMAL: 
            buf.append(res.getString("DH")); 
            break;
        case CTCIKeyConst.PKA_DSA: 
            buf.append(res.getString("DSA")); 
            break;
        case CTCIKeyConst.PKA_GF2255: 
            buf.append(res.getString("Elliptic_curve")); 
            break;
        case CTCIKeyConst.PKA_EBP_RABIN: 
            buf.append(res.getString("Rabin")); 
            break;
        default: 
            buf.append(res.getString("Unknown_pkalg")); 
            valid = false; 
            break;
        }
        buf.append(size);
        buf.append("/[");

        int l = id.length();
        for(int i=l; i<16; ++i) buf.append("0");
        for(int j=0; j<l; ++j)
        {
            buf.append(Character.toUpperCase(id.charAt(j)));
            if(9 == l-j) buf.append("]");
        }

        if(valid)
        {
            buf.append(" ");
            DateFormat fmt = DateFormat.getDateTimeInstance();
            buf.append(fmt.format(date));
            buf.append(res.getString("keyprint"));
            buf.append(print);
        }
        return buf.toString();
    }
    private native int keySize(long ptr);
    private native int keyAlg(long ptr);
    private native long keyID(long ptr);
    private native int keyDate(long ptr);
    private native String keyPrint(long ptr);

    public CJSeckey getSeckey()
    {
        return sec(pointer);
    }
    private native CJSeckey sec(long ptr);

    public Signature getSignature()
    {
        return signature(pointer);
    }
    private native Signature signature(long ptr);

    public CJPubkey getSubkey()
    {
        return subkey(pointer);
    }
    private native CJPubkey subkey(long ptr);

    public Username getUsername()
    {
        return username(pointer);
    }
    private native Username username(long ptr);

    public boolean checkSignature(Username name, Signature sig)
    {
        return check(pointer, name==null?0L:name.cHandle(), sig.cHandle());
    }
    private native boolean check(long ptr, long name, long sig);
}
