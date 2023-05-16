
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1998
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;

public class NativeUsername
{
    private long pointer = 0;

    private NativeUsername()
    {
    }

    private NativeUsername(long handle)
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

    public NativeUsername nextUsername()
    {
        return getNextUsername(pointer);
    }
    private native NativeUsername getNextUsername(long ptr);

    public NativeSignature getSignature()
    {
        return NativeSignature(pointer);
    }
    private native NativeSignature NativeSignature(long ptr);

    public String toString()
    {
        return getName();
    }

    public String getName()
    {
        return name(pointer);
    }
    private native String name(long ptr);

    public void sign(SecretKey sk, PublicKey owner)
    {
        KeyConstants.MDA alg = Root.instance().getMDA();
        keySign(pointer, sk.cHandle(), owner.cHandle(), alg.value());
    }
    private native void keySign(long ptr, long signer, long pubkey, byte mdalg);
}
