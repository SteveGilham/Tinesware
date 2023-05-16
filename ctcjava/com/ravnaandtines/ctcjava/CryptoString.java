
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;

public final class CryptoString {

    static void wipe(char[] xtext)
    {
        for(int i=0; i<xtext.length; ++i) xtext[i] = 0;
    }

    private char [] text = null;

    public CryptoString()
    {
    }

    public CryptoString(char[] xtext)
    {
        text = xtext; // take over management
    }

    public char [] getText()
    {
        return text;
    }

    public void wipe()
    {
        if(text != null)
            CryptoString.wipe(text);
    }

    protected final void finalize() throws Throwable
    {
        //System.out.println("CryptoString finalizing");
        wipe();
        super.finalize();
        //System.out.println("CryptoString finalized");
    }

    public int length()
    {
        return text.length;
    }

    private int utf8length(char c)
    {
        if(c>=0 && c<=0x7F) return 1;
        else if(c>=0x80 && c<=0x7FF) return 2;
        return 3;
    }

    private int utfencode(char c, byte[] b, int i)
    {
        if(i >= b.length) return 0;
        if(c>=0 && c<=0x7F)
        {
            b[i] = (byte)(c&0xFF);
            return 1;
        }
        else if(c>=0x80 && c<=0x7FF)
        {
            if(i+1 >= b.length) return 0;
            b[i] = (byte)(0xC0 | ((c>>6)&0x1F));
            b[i+1] = (byte)(0x80 | (c&0x3F));
            return 2;
        }
        if(i+2 >= b.length) return 0;
        b[i] = (byte)(0xE0 | (((c)>>12) & 0x0F) );
        b[i+1] = (byte)(0x80 | (((c)>>6) & 0x3F) );
        b[i+2] = (byte)(0x80 | ((c)&0x3F) );
        return 3;
    }

    public int utf8length()
    {
        int l = 0;
        for(int i = 0; i<text.length;++i) l+=utf8length(text[i]);
        return l;
    }

    public void getUTF8(byte[] utf)
    {
        int l = 0;
        for(int i=0; i<text.length; ++i)
        {
            l += utfencode(text[i], utf, l);
        }
    }

    public char charAt(int i)
    {
        if ((i < 0) || (i >= text.length))
        {
            throw new StringIndexOutOfBoundsException(i);
        }
        return text[i];
    }
}
