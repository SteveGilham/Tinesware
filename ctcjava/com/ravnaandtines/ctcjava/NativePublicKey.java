
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;

import java.util.Date;
import java.text.*;

public class NativePublicKey {

    private long pointer = 0;

    private NativePublicKey()
    {
    }

    private NativePublicKey(long handle)
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

    public NativePublicKey nextPubkey()
    {
        return getNextPubkey(pointer);
    }
    private native NativePublicKey getNextPubkey(long ptr);

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

    public void sign(SecretKey sk)
    {
        KeyConstants.MDA alg = Root.instance().getMDA();
        keySign(pointer, sk.cHandle(), alg.value());
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
        KeyConstants.PKA alg = KeyConstants.PKAfromByte((byte)keyAlg(pointer));
        String id = Long.toHexString(keyID(pointer));
        String print = keyPrint(pointer);
        long t = ((long)keyDate(pointer))&0x00000000FFFFFFFFL;
        Date date = new Date(1000L*t);
        
        StringBuffer buf = new StringBuffer("");
        if(!isEnabled()) buf.append(GlobalData.getResourceString("DISABLED_KEY_"));
        boolean valid = true;
        if(null == alg) {
            buf.append(GlobalData.getResourceString("Unknown_pkalg"));
            valid = false;
        } else {
            switch(alg) {
                case EBP_RSA:
                case RSA:
                    buf.append(GlobalData.getResourceString("RSA"));
                    break;
                case RSA_ENCRYPT_ONLY:
                    buf.append(GlobalData.getResourceString("RSAencrypt"));
                    break;
                case RSA_SIGN_ONLY:
                    buf.append(GlobalData.getResourceString("RSAsign"));
                    break;
                case ELGAMAL:
                    buf.append(GlobalData.getResourceString("DH"));
                    break;
                case DSA:
                    buf.append(GlobalData.getResourceString("DSA"));
                    break;
                case GF2255:
                    buf.append(GlobalData.getResourceString("Elliptic_curve"));
                    break;
                case EBP_RABIN:
                    buf.append(GlobalData.getResourceString("Rabin"));
                    break;
                default:
                    buf.append(GlobalData.getResourceString("Unknown_pkalg"));
                    valid = false;
                    break;
            }
            buf.append(size);
        }
        buf.append("/[");
        
        int l = id.length();
        for(int i=l; i<16; ++i) buf.append("0");
        for(int j=0; j<l; ++j) {
            buf.append(Character.toUpperCase(id.charAt(j)));
            if(9 == l-j) buf.append("]");
        }
        
        if(valid) {
            buf.append(" ");
            DateFormat fmt = DateFormat.getDateTimeInstance();
            buf.append(fmt.format(date));
            buf.append(GlobalData.getResourceString("keyprint"));
            buf.append(print);
        }
        return buf.toString();
    }
    private native int keySize(long ptr);
    private native int keyAlg(long ptr);
    private native long keyID(long ptr);
    private native int keyDate(long ptr);
    private native String keyPrint(long ptr);

    public NativeSecretKey getSeckey()
    {
        return sec(pointer);
    }
    private native NativeSecretKey sec(long ptr);

    public NativeSignature getSignature()
    {
        return NativeSignature(pointer);
    }
    private native NativeSignature NativeSignature(long ptr);

    public NativePublicKey getSubkey()
    {
        return subkey(pointer);
    }
    private native NativePublicKey subkey(long ptr);

    public NativeUsername getUsername()
    {
        return NativeUsername(pointer);
    }
    private native NativeUsername NativeUsername(long ptr);

    public boolean checkSignature(NativeUsername name, NativeSignature sig)
    {
        return check(pointer, name==null?0L:name.cHandle(), sig.cHandle());
    }
    private native boolean check(long ptr, long name, long sig);
}
