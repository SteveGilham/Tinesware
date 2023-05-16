
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware

// Sub-sets the interface of java.util.StringTokenizer, but provides
// the facility to keep the text secure and wipe before release

package com.ravnaandtines.ctcjava;

public final class CryptoTextTokenizer {

    private int index;
    private int end;
    private char[] stream;
    private char[] delimiters;

    public CryptoTextTokenizer(char[] text, char[] delim)
    {
        index = 0;
        stream = new char[text.length];
        //        for (int i=0; i<text.length;++i) stream[i] = text[i];
        stream = text;
        end = stream.length;
        delimiters = delim;          // just keep a reference
    }
    /*
     *     public void wipe()
     *     {
     *        for(int i=0; i<stream.length; ++i) stream[i] = 0;
     *     }
     * 
     *     protected void finalize() throws Throwable
     *     {
     *        wipe();
     *        super.finalize();
     *     }
     */

    protected void finalize() throws Throwable
    {
        //System.out.println("CryptoTextTokenizer finalizing");
        stream = null;
        super.finalize();
        //System.out.println("CryptoTextTokenizer finalized");
    }

    private boolean isDelimiter(char c)
    {
        for(int i=0; i<delimiters.length; ++i)
        {
            if(delimiters[i]==c) return true;
        }
        return false;
    }

    private void skipDelimiters()
    {
        while (index < end && isDelimiter(stream[index]))
            ++index;
    }

    private void skipToken()
    {
        while (index < end && !isDelimiter(stream[index]))
            ++index;
    }


    public boolean hasMoreTokens()
    {
        skipDelimiters();
        boolean result = index < end;
        //        if(!result) wipe();
        return result;
    }

    public char[] nextToken()
    {
        skipDelimiters();

        if (index >= end)
        {
            throw new java.util.NoSuchElementException();
        }
        int here = index;
        skipToken();
        // token is [here,index), length index-here
        char[] result = new char[index-here];
        for(int i=0; i<result.length; ++i) result[i] = stream[here+i];
        return result;
    }


}
