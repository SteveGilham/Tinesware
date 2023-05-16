
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


// Sub-sets the interface of java.lang.StringBuffer, but provides
// the facility to keep the text secure and wipe before release

package com.ravnaandtines.ctcjava;

public final class CryptoTextBuffer {
    private char[] text = null;
    private int    used = -1;

    public char[] getText()
    {
        char[] v = new char[used];
        for(int i=0; i<used; ++i) v[i] = text[i];
        return v;
    }

    public void wipe() // what it's all about!
    {
        if(null == text) return;
        // **NOT** used as upper bound - see setLength()!!
        for(int i=0; i<text.length; ++i) text[i] = 0;
    }

    protected void finalize() throws Throwable
    {
        //System.out.println("CryptoTextBuffer finalizing");
        wipe();
        super.finalize();
        //System.out.println("CryptoTextBuffer finalized");
    }

    public CryptoTextBuffer()
    {
        this(64); // sensible line length
    }

    public CryptoTextBuffer(int size)
    {
        text = new char[size];
        used = 0;
    }

    public CryptoTextBuffer(char[] source, int start, int end)
    {
        this((end-start+1) < 32 ? 64 : 2*(end-start+1));
        append(source, start, end);
    }

    public CryptoTextBuffer(char[] source, int start)
    {
        this(source, start, source.length);
    }

    // for what it's worth as the string already leaks...
    public CryptoTextBuffer(String str)
    {
        this(str.length() < 32 ? 64 : 2*str.length());
        append(str);
    }

    public synchronized CryptoTextBuffer append(String str)
    {
        if (str == null)str = String.valueOf(str);
        int len = str.length();
        ensureCapacity(used + len);
        str.getChars(0, len, text, used);
        used += len;
        return this;
    }

    // end included here
    public synchronized CryptoTextBuffer append(char[] str, int start, int end)
    {
        if (str == null)
        {
            return append(String.valueOf(str));
        }
        int len = end-start;
        ensureCapacity(used + len);
        for(int i=0; i<len; ++i) text[used+i] = str[start+i];
        used += len;
        return this;
    }

    public synchronized CryptoTextBuffer append(char[] str, int start)
    {
        return append(str, start, str.length);
    }

    public synchronized CryptoTextBuffer append(char[] str)
    {
        return append(str, 0, str.length);
    }


    public synchronized CryptoTextBuffer append(char c)
    {
        ensureCapacity(used + 1);
        text[used++] = c;
        return this;
    }


    public int length()
    {
        return used;
    }

    public int capacity()
    {
        return text.length;
    }

    public synchronized void ensureCapacity(int min)
    {
        if(min > text.length)
        {
            // allocate at least enough space
            int l = text.length*2;
            if(l < min) l = min;
            char[] enough = new char[l];

            // copy under our own watchful eyes
            for(int i=0; i<used; ++i) enough[i] = text[i];

            // zap the old store and replace with the new
            wipe();
            text = enough;
        }
    }

    public synchronized void setLength(int l)
    {
        if (l < 0) {
            throw new StringIndexOutOfBoundsException(l);
        }
        ensureCapacity(l);
        for(; used < l; ++used) text[used] = 0;
        used = l;
    }

    public synchronized char charAt(int i)
    {
        if (i < 0 || i >= used)
        {
            throw new StringIndexOutOfBoundsException(i);
        }
        return text[i];
    }

    public synchronized void setCharAt(int i, char c)
    {
        if (i < 0 || i >= used)
        {
            throw new StringIndexOutOfBoundsException(i);
        }
        text[i] = c;
    }

    public synchronized CryptoTextBuffer insert(int offset, String str)
    {
        if ((offset < 0) || (offset > used))
        {
            throw new StringIndexOutOfBoundsException();
        }
        int len = str.length();
        ensureCapacity(used + len);
        for(int i=used-1; i>=offset; --i) text[i+len] = text[i];
        str.getChars(0, len, text, offset);
        used += len;
        return this;
    }

    public synchronized CryptoTextBuffer insert(int offset, char c) {
        ensureCapacity(used + 1);
        for(int i=used-1; i>=offset; --i) text[i+1] = text[i];
        text[offset] = c;
        ++used;
        return this;
    }

    public synchronized CryptoTextBuffer insert(int offset, char[] str, int start, int end)
    {
        if ((offset < 0) || (offset > used))
        {
            throw new StringIndexOutOfBoundsException();
        }
        int len = end-start+1;
        ensureCapacity(used + len);
        for(int i=used-1; i>=offset; --i) text[i+len] = text[i];
        for(int j=0; j<len; ++j) text[offset+j] = str[start+j];
        used += len;
        return this;
    }

    public synchronized CryptoTextBuffer insert(int offset, char[] str, int start)
    {
        return insert(offset, str, start, str.length);
    }

    public synchronized CryptoTextBuffer insert(int offset, char[] str)
    {
        return insert(offset, str, 0, str.length);
    }

    public String toString() {
        int result = 0;
        if(used < 10)
        {
            return "<TOO SHORT>";
        }

        for(int i=0; i<used; ++i)
        {
            // allow for multi-byte encodings; the series of
            // fragmented 1-character strings are less of a leak
            String tmp1 = new String(text, i, 1);
            byte[] tmp = null;
            if(null != CJGlobals.encoding)
            {
                try {
                    tmp = tmp1.getBytes(CJGlobals.encoding);
                } 
                catch ( java.io.UnsupportedEncodingException ex ) {
                }
            }
            if(null==tmp) tmp = tmp1.getBytes();
            for(int k=0; k<tmp.length; ++k)
            {
                result = result * 104729 + tmp[k];
                tmp[k] = 0;
            }
        }
        result &= 0xFFFF;
        return Integer.toHexString(result);
    }
}
