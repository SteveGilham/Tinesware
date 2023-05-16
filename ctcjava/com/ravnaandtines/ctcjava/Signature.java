
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1998
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;

import java.util.Date;
import java.text.DateFormat;

public class Signature
{
    private long pointer = 0;

    private static final int SIG_STATUS = 1001;
    private static final int SIG_TRUST = 1002;
    private static final int SIG_VERSION = 1003;
    private static final int SIG_CLASS = 1004;
    private static final int SIG_PKALG = 1005;
    private static final int SIG_MDALG = 1006;
    private static final int SIG_DATETIME = 1007;
    private static final int SIG_KEYID = 1008;

    private Signature()
    {
    }

    private Signature(long handle)
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

    public Signature nextSignature()
    {
        return getNextSignature(pointer);
    }
    private native Signature getNextSignature(long ptr);

    public String toString()
    {
        return getName();
    }

    public String getName()
    {
        StringBuffer buf = new StringBuffer("");
        long t = ((long)value(pointer,SIG_DATETIME))&0x00000000FFFFFFFFL;
        Date date = new Date(1000L*t);
        DateFormat fmt = DateFormat.getDateTimeInstance();
        buf.append(fmt.format(date));
        buf.append(" ");
        return buf.toString();
    }
    private native int value(long ptr, int key);

    public CJPubkey toKey()
    {
        return pub(pointer);
    }
    private native CJPubkey pub(long ptr);
}
