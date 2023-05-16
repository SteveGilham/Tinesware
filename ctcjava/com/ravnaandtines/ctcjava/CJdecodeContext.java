
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;

public class CJdecodeContext {

    private long pointer;

    private CJdecodeContext() {
    }

    private CJdecodeContext(long handle)
    {
        pointer = handle;
    }

    public CJPubkey firstPubKey()
    {
        return getFirstPubkey(pointer);
    }
    private native CJPubkey getFirstPubkey(long ptr);

    public CJSeckey firstSecKey()
    {
        return getFirstSeckey(pointer);
    }
    private native CJSeckey getFirstSeckey(long ptr);

}
