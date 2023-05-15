package com.ravnaandtines.util.text;

/**
*  Class CryptoTextBuffer - Sub-sets the interface of
*  java.lang.StringBuffer, but provides the facility to wipe before release
* <p>
* Copyright Mr. Tines &lt;tines@windsong.demon.co.uk&gt; 1998
* <p>
* For the purposes of the licence, this source file and the minimum set of
* other source files in package com.ravnaandtines.util and its sub-
* packages required to link together to satisfy all class
* references not belonging to standard Javasoft-published APIs constitute
* the library.  Thus it is not necessary to distribute source to classes
* that you do not actually use.
* <p>
* Note that Java's dynamic class loading means that the distribution of class
* files (as is, or in jar or zip form) which use this library is sufficient to
* allow run-time binding to any interface compatible version of this library.
* The GLPL is thus far less onerous for Java than for the usual run of 'C'/C++
* library.
* <p>
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Library General Public
* License as published by the Free Software Foundation; either
* version 2 of the License, or (at your option) any later version.
* <p>
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Library General Public License for more details.
* <p>
* You should have received a copy of the GNU Library General Public
* License along with this library; if not, write to the Free
* Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*<p>
* @author Mr. Tines
* @version 1.0 17-Nov-1998
*/

public final class CryptoTextBuffer
{
    private char[] text = null;
    private int    used = -1;

    private static String shortString = "<TOO SHORT>";

    /**
    * Sets the string value for toString to return for strings of < 20 characters
    * @param message value (default is "<TOO SHORT>" unless set explicitly).
    */
    public static void setWarningString(String s)
    {
        shortString = s;
    }

    /**
    * Acesses contents as char[]
    * @return char array of content
    */
    public char[] getText()
    {
        char[] v = new char[used];
        for(int i=0; i<used; ++i) v[i] = text[i];
        return v;
    }

    /**
    * Wipes contents - what it's all about!
    */
    public void wipe()
    {
        if(null == text) return;
        // **NOT** used as upper bound - see setLength()!!
        for(int i=0; i<text.length; ++i) text[i] = 0;
    }

    /**
    * Wipes contents
    * @exception Throwable from superclass
    */
    protected void finalize() throws Throwable
    {
        wipe();
        super.finalize();
    }

    /**
    * Constructs a buffer with no characters in it
    * and an initial capacity of 64 characters.
    */
    public CryptoTextBuffer()
    {
        this(64); // sensible line length
    }

    /**
    * Constructs a buffer with no characters in it
    * and an initial capacity as desired.
    * @param size initial capacity
    */
    public CryptoTextBuffer(int size)
    {
        text = new char[size];
        used = 0;
    }

    /**
    * Constructs a buffer so that it represents the same sequence of
    * characters as the indicated array subset. The initial capacity of the
    * buffer is 64 or twice the length input.
    * @param source character source
    * @param start start character position
    * @param end end character position <strong>NOT the slice length</strong>
    */
    public CryptoTextBuffer(char[] source, int start, int end)
    {
        this((end-start+1) < 32 ? 64 : 2*(end-start+1));
        append(source, start, end);
    }

    /**
    * Constructs a buffer so that it represents the same sequence of
    * characters as the indicated array subset. The initial capacity of the
    * buffer is 64 or twice the length input.
    * @param source character source
    * @param start start character position - the rest of the array is used
    */
    public CryptoTextBuffer(char[] source, int start)
    {
        this(source, start, source.length);
    }

    /**
    * Constructs, for what it's worth as the string already leaks, a
    * buffer so that it represents the same sequence of characters as
    * string. The initial capacity of the buffer is 64 or twice the
    * length input.
    * @param str character source
    */
    public CryptoTextBuffer(String str)
    {
        this(str.length() < 32 ? 64 : 2*str.length());
        append(str);
    }

    /**
    * Appends, for what it's worth as the string already leaks, the string
    * @param str character source
    */
    public synchronized CryptoTextBuffer append(String str)
    {
        if (str == null)str = String.valueOf(str);
        int len = str.length();
        ensureCapacity(used + len);
        str.getChars(0, len, text, used);
	    used += len;
	    return this;
    }

    /**
    * Appends the indicated array subset.
    * @param str character source
    * @param start start character position
    * @param end last included character position <strong>NOT the slice length</strong>
    */
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

    /**
    * Appends the indicated array subset.
    * @param str character source
    * @param start start character position, from here to end
    */
    public synchronized CryptoTextBuffer append(char[] str, int start)
    {
        return append(str, start, str.length);
    }

    /**
    * Appends the indicated array.
    * @param str character source
    */
    public synchronized CryptoTextBuffer append(char[] str)
    {
        return append(str, 0, str.length);
    }

    /**
    * Appends the indicated character.
    * @param c character to add
    */
    public synchronized CryptoTextBuffer append(char c)
    {
        ensureCapacity(used + 1);
        text[used++] = c;
        return this;
    }

    /**
    * Gives the number of active characters in the buffer.
    * @return number of active characters
    */
    public int length()
    {
        return used;
    }

    /**
    * Gives the number of possible characters in the buffer.
    * @return buffer capacity
    */
    public int capacity()
    {
        return text.length;
    }

    /**
    * Ensures the number of possible characters in the buffer
    * matches what we want to hold.
    * @param min desired minimum buffer capacity
    */
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

    /**
    * Ensures the number of active characters in the buffer
    * matches what we want to have - truncates or null pads as required.
    * @param l desired buffer length
    * @exception StringIndexOutOfBoundsException for negative lengths
    */
    public synchronized void setLength(int l)
    {
	    if (l < 0)
        {
	        throw new StringIndexOutOfBoundsException(l);
	    }
        ensureCapacity(l);
        for(; used < l; ++used) text[used] = 0;
        used = l;
    }

    /**
    * Returns the indicated character
    * @param i desired character index
    * @exception StringIndexOutOfBoundsException for out of range values of i
    */
    public synchronized char charAt(int i)
    {
	    if (i < 0 || i >= used)
        {
	        throw new StringIndexOutOfBoundsException(i);
	    }
	    return text[i];
    }

    /**
    * Sets the indicated character
    * @param i desired character index
    * @aparm c the value to set
    * @exception StringIndexOutOfBoundsException for out of range values of i
    */
    public synchronized void setCharAt(int i, char c)
    {
        if (i < 0 || i >= used)
        {
	        throw new StringIndexOutOfBoundsException(i);
	    }
	    text[i] = c;
    }

    /**
    * inserts the indicated string
    * @param offset desired character index for insertion
    * @aparm s the value to set
    * @exception StringIndexOutOfBoundsException for out of range values of offset
    */
    public synchronized CryptoTextBuffer insert(int offset, String str)
    {
        if ((offset < 0) || (offset > used))
        {
	        throw new StringIndexOutOfBoundsException(offset);
	    }
	    int len = str.length();
	    ensureCapacity(used + len);
        for(int i=used-1; i>=offset; --i) text[i+len] = text[i];
	    str.getChars(0, len, text, offset);
	    used += len;
	    return this;
    }

    /**
    * inserts the indicated character
    * @param offset desired character index for insertion
    * @aparm c the value to set
    * @exception StringIndexOutOfBoundsException for out of range values of offset
    */
    public synchronized CryptoTextBuffer insert(int offset, char c)
    {
        if ((offset < 0) || (offset > used))
        {
	        throw new StringIndexOutOfBoundsException(offset);
	    }
        ensureCapacity(used + 1);
        for(int i=used-1; i>=offset; --i) text[i+1] = text[i];
        text[offset] = c;
        ++used;
        return this;
    }

    /**
    * Inserts the indicated array subset.
    * @param offset desired character index for insertion
    * @param str character source
    * @param start start character position
    * @param end last included character position <strong>NOT the slice length</strong>
    */
    public synchronized CryptoTextBuffer insert(int offset, char[] str,
        int start, int end)
    {
	    if ((offset < 0) || (offset > used))
        {
	        throw new StringIndexOutOfBoundsException(offset);
	    }
	    int len = end-start+1;
	    ensureCapacity(used + len);
        for(int i=used-1; i>=offset; --i) text[i+len] = text[i];
        for(int j=0; j<len; ++j) text[offset+j] = str[start+j];
	    used += len;
	    return this;
    }

    /**
    * Inserts the indicated array subset.
    * @param offset desired character index for insertion
    * @param str character source
    * @param start start character position - from here to the end
    */
    public synchronized CryptoTextBuffer insert(int offset, char[] str, int start)
    {
        return insert(offset, str, start, str.length);
    }

    /**
    * Inserts the indicated array.
    * @param offset desired character index for insertion
    * @param str character source
    */
    public synchronized CryptoTextBuffer insert(int offset, char[] str)
    {
        return insert(offset, str, 0, str.length);
    }

    /**
    * Returns a simple hashed value from the string buffer.  Intended for
    * passphrase input feedback
    * @return hash
    */
    public String toString()
    {
	    int result = 0;
        if(used < 20)
        {
            return shortString;
        }

        byte[] b = new byte[3];
        int l = 0;

        for(int i=0; i<used; ++i)
	    {
            if(text[i]>=0 && text[i]<=0x7F)
            {
                b[0] = (byte)(text[i]&0xFF);
                l = 1;
            }
            else if(text[i]>=0x80 && text[i]<=0x7FF)
            {
                b[0] = (byte)(0xC0 | ((text[i]>>6)&0x1F));
                b[1] = (byte)(0x80 | (text[i]&0x3F));
                l = 2;
            }
            else
            {
                b[0] = (byte)(0xE0 | ((text[i]>>12) & 0x0F) );
                b[1] = (byte)(0x80 | ((text[i]>>6) & 0x3F) );
                b[2] = (byte)(0x80 | (text[i]&0x3F) );
                l = 3;
            }
            for(int k=0; k<l; ++k)
            {
                result = result * 104729 + b[k];
                b[k] = 0;
            }
	    }
        result &= 0xFFFF;
        return Integer.toHexString(result);
    }
}
