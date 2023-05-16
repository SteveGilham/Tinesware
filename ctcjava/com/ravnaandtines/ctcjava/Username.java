
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1998
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;

public class Username
{
    private long pointer = 0;

    private Username()
    {
    }

    private Username(long handle)
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

    public Username nextUsername()
    {
        return getNextUsername(pointer);
    }
    private native Username getNextUsername(long ptr);

    public Signature getSignature()
    {
        return signature(pointer);
    }
    private native Signature signature(long ptr);

    public String toString()
    {
        return getName();
    }

    public String getName()
    {
        return name(pointer);
    }
    private native String name(long ptr);

    public void sign(CJSeckey sk, CJPubkey owner)
    {
        CJAlgPanel ap = CJGlobals.mainFrame.algPanel;
        byte alg = (byte) ap.mdan[ap.MDAbox.getSelectedIndex()];
        keySign(pointer, sk.cHandle(), owner.cHandle(), alg);
    }
    private native void keySign(long ptr, long signer, long pubkey, byte mdalg);
}
