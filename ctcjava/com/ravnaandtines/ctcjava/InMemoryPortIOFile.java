
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;
import java.io.*;

public class InMemoryPortIOFile extends ByteArrayOutputStream {
    private boolean writeable;
    private ByteArrayInputStream out;

    //inherits
    //protected byte buf[]
    //protected int count

    // called by
    // - cjcb_act.cpp cb_result_file
    public InMemoryPortIOFile()
    {
        writeable = true;
        out = null;
    }

    public InMemoryPortIOFile(String s)
    {
        this();
        writeString(s);
        swap();
    }

    public InMemoryPortIOFile(InputStream s)
    {
        this();
        fillFromStream(s);
        swap();
    }

    public synchronized void fillFromStream(InputStream s)
    {
        byte[] bucket = new byte[1024];
        int n = 0;
        do
        {
            try{
                n = s.read(bucket);
            }
            catch(IOException ioe){
                break;
            }
            if(n > 0) write(bucket, 0, n);
        } 
        while (n>0);
    }

    public synchronized void saveToStream(OutputStream s)
    {
        byte[] bucket = new byte[1024];
        int n = 0;
        swap();
        out.reset();
        do
        {
            n = read(bucket, 0, bucket.length);
            try{
                if(n>0) s.write(bucket, 0, n);
            }
            catch(IOException ioe){
                break;
            }
        } 
        while (n>0);
    }

    public synchronized void writeString(String s)
    {
        try{
            boolean written = false;
            if(null != GlobalData.encoding) try {
                write(s.getBytes(GlobalData.encoding));
                written = true;
            } 
            catch ( UnsupportedEncodingException ex ) {
            }
            if(! written) write(s.getBytes());
            flush();
        }
        catch(IOException e){
        }
    }

    public synchronized void write(int b)
    {
        if(writeable) super.write(b);
    }
    public synchronized void write(byte b[], int off, int len)
    {
        if(writeable) super.write(b, off, len);
    }

    private final void swap()
    {
        if(writeable)
        {
            try{
                flush();
            }
            catch(IOException e) {
            }

            // allocate array of the correct length
            byte [] tmp = toByteArray();

            // wipe the old and swap in the new
            close();
            buf = tmp;
            writeable = false;
            out = new ByteArrayInputStream(buf);
        }
    }

    public synchronized int read()
    {
        swap();
        return out.read();
    }

    public synchronized int read(byte b[], int off, int len)
    {
        swap();
        return out.read(b, off, len);
    }

    public synchronized void setpos(int l)
    {
        if(writeable)
        {
            count = l;
        }
        else
        {
            out.reset();
            out.skip(l);
        }
    }

    public synchronized int length()
    {
        if(writeable) return count;
        else return buf.length;
    }

    public synchronized void close() // implement this method here
    {
        for(int i=0; i<buf.length; ++i) buf[i] = 0;
        out = null;
        System.gc();
    }

    public Reader getReader()
    {
        swap();
        out.reset();
        if(null != GlobalData.encoding)
        {
            try {
                return new InputStreamReader(out, GlobalData.encoding);
            } 
            catch ( UnsupportedEncodingException ex ) {
            }
        }
        // couldn't hack the new encoding...
        return new InputStreamReader(out);
    }
}
