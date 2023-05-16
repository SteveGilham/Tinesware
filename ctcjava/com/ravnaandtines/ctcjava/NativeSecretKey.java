
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;

public class NativeSecretKey {

    private long pointer = 0;

    private NativeSecretKey()
    {
    }

    private NativeSecretKey(long handle)
    {
        pointer = handle;
    }
    public NativeSecretKey nextSeckey()
    {
        return getNextSeckey(pointer);
    }
    private native NativeSecretKey getNextSeckey(long ptr);

    public NativePublicKey getPubkey()
    {
        return pub(pointer);
    }
    private native NativePublicKey pub(long ptr);

    public boolean isNull()
    {
        return pointer == 0;
    }
    public long cHandle()
    {
        return pointer;
    }

    public boolean revoke(boolean permanently)
    {
        return keyRevoke(pointer, permanently);
    }
    private native boolean keyRevoke(long ptr, boolean permanently);

    public void lock()
    {
        internalise(pointer);
    }
    private native void internalise(long ptr);

    public boolean unlock()
    {
        return externalise(pointer);
    }
    private native boolean externalise(long ptr);
    
    public boolean unlock(ByteArrayWrapper passphrase)
    {
        return unlock(pointer, passphrase);
    }
    private native boolean unlock(long ptr, ByteArrayWrapper passphrase);

    public boolean isLocked()
    {
        return (pointer==0) || isExternalised(pointer);
    }
    private native boolean isExternalised(long ptr);

    public void addUser(String name)
    {
        addSignUser(pointer, name);
    }
    private native void addSignUser(long ptr, String name);

    public void changePassphrase(KeyConstants.MDA hash, KeyConstants.CEA kpalg, byte[] phrase)
    {
        updatePassphrase(pointer, hash.value(), kpalg.value(), phrase);
        for(int i=0; i<phrase.length; ++i) phrase[i] = 0; // wipe
    }
    private native void updatePassphrase(long ptr, byte hash, byte kpalg, byte[] phrase);
}
