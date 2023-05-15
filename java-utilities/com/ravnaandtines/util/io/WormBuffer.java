package com.ravnaandtines.util.io;

/**
*  Class WormBuffer - A Write once/ Read mostly class based on the
* ByteArray<type>Streams, implementing the Worm interface.
* <p>
* Copyright Mr. Tines &lt;tines@windsong.demon.co.uk&gt; 1998
*  and released into the public domain
*  <P>
* THIS SOFTWARE IS PROVIDED BY THE AUTHORS ''AS IS'' AND ANY EXPRESS
* OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
* ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHORS OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
* BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
* WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
* OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
* EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*<p>
* @author Mr. Tines
* @version 1.0 08-Nov-1998
*
*/


// write once, read many
public class WormBuffer extends java.io.ByteArrayOutputStream implements Worm {
    private class ByteArrayInputStream extends java.io.ByteArrayInputStream
    {
        public ByteArrayInputStream(byte buf[])
        {
            super(buf);
        }
        int getPos() {return pos;}
    }
    private boolean writeable;
    private ByteArrayInputStream out;

//inherits
//protected byte buf[]
//protected int count

    /**
    * Creates an empty buffer ready to write
    */
    public WormBuffer()
    {
      writeable = true;
      out = null;
    }

    /**
    * Writes a string to the buffer, and sets it ready to read
    * @param s String to write to buffer
    */
    public WormBuffer(String s)
    {
        this();
        writeString(s);
        swap();
    }

    /**
    * Writes a stream to the buffer, and sets it ready to read
    * @param s Stream to write to buffer
    */
    public WormBuffer(java.io.InputStream s)
    {
        this();
        fillFromStream(s);
        swap();
    }

    /**
    * Appends the contents of a stream to the buffer
    * @param s Stream to write to buffer
    */
    public synchronized void fillFromStream(java.io.InputStream s)
    {
        byte[] bucket = new byte[1024];
        int n = 0;
        do
        {
            try{
                n = s.read(bucket);
                }catch(java.io.IOException ioe){
                break;
            }
            if(n > 0) write(bucket, 0, n);
        } while (n>0);
    }

    /**
    * Writes the contents of the buffer to a stream
    * @param s Stream to write to
    */
    public synchronized void saveToStream(java.io.OutputStream s)
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
                }catch(java.io.IOException ioe){
                break;
            }
        } while (n>0);
    }

    /**
    * Writes the contents of the buffer to a string (uses default encoding)
    * @param s Stream to write to
    */
    public synchronized void writeString(String s)
    {
        try{
            // let's just use the default encoding here.
        /*
            boolean written = false;
            if(null != CJGlobals.encoding) try
            {
                write(s.getBytes(CJGlobals.encoding));
                written = true;
                } catch ( UnsupportedEncodingException ex ) {
            }
            if(! written)*/ write(s.getBytes());
            flush();
            }catch(java.io.IOException e){
        }
    }

    /**
    * Writes the specified byte.
    * @param b the byte to be written.
    */
    public synchronized void write(int b)
    {
        if(writeable) super.write(b);
    }

    /**
    * Writes len bytes from the specified byte array starting at offset off.
    * @ param b the data.
    * @ param off the start offset in the data.
    * @ param len the number of bytes to write.
    */
    public synchronized void write(byte b[], int off, int len)
    {
        if(writeable) super.write(b, off, len);
    }

    /**
    * Flushes the input, and readies for reading if not already done
    */
    private final void swap()
    {
        if(writeable)
        {
            try{
                flush();
                }catch(java.io.IOException e) {
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

    /**
    * Reads a byte of data from this file. This method blocks if no input is
    * yet available.
    * @return the next byte of data, or -1 if the end of the file is reached.
    * @exception IOException if an I/O error occurs.
    */
    public synchronized int read()
    {
        swap();
        return out.read();
    }

    /**
    * Reads up to len bytes of data from this file into an array of bytes.
    * This method blocks until at least one byte of input is available.
    * @param b the buffer into which the data is read.
    * @param off the start offset of the data.
    * @param len the maximum number of bytes read.
    * @return the total number of bytes read into the buffer, or -1
    * if there is no more data because the end of the file has been reached.
    * @exception IOException if an I/O error occurs.
    */
    public synchronized int read(byte b[], int off, int len)
    {
        swap();
        return out.read(b, off, len);
    }

    /**
    * Sets the offset from the beginning of this file at which the next
    * read or write occurs.
    * @param pos long giving the absolute position.
    * @exception IOException if an I/O error occurs.
    */
    public synchronized void seek(long l)
    {
        if(writeable)
        {
            count = (int)l;
        }
        else
        {
            out.reset();
            out.skip(l);
        }
    }

    /**
    * Returns the length of this file.
    * @return the length of this file.
    * @exception IOException if an I/O error occurs.
    */
    public synchronized long length()
    {
        if(writeable) return count;
        else return buf.length;
    }

    /**
    * Closes this stream and releases any system resources associated with it.
    * @exception java.io.IOException if an I/O error occurs.
    */
    public synchronized void close() // implement this method here
    {
        for(int i=0; i<buf.length; ++i) buf[i] = 0;
        out = null;
        System.gc();
    }

    /**
    * Returns a reader wrapped around the raw input stream; currently
    * uses default encoding only.
    * @return a new Reader
    * @exception IOException if an I/O error occurs.
    */
    public java.io.Reader getReader()
    {
        swap();
        out.reset();
        /*if(null != CJGlobals.encoding)
        {
            try {
                return new InputStreamReader(out, CJGlobals.encoding);
                } catch ( UnsupportedEncodingException ex ) {
            }
        }*/
        // couldn't hack the new encoding...
        return new java.io.InputStreamReader(out);
    }

    /**
    * Returns a reader wrapped around the raw input stream
    * uses supplied encoding only.
    * @param encoding String giving desired encoding
    * @return a new Reader
    * @exception IOException if an I/O error occurs.
    */
    public java.io.Reader getReader(String encoding)
    {
        swap();
        out.reset();
        if(null != encoding)
        {
            try {
                return new java.io.InputStreamReader(out, encoding);
                } catch ( java.io.UnsupportedEncodingException ex ) {
            }
        }
        // couldn't hack the new encoding...
        return new java.io.InputStreamReader(out);
    }
    /**
    * Returns the current offset in this file.
    * @return the offset from the beginning of the file, in bytes, at which the next read or write occurs.
    * @exception IOException if an I/O error occurs.
    */
    public long getFilePointer()
    {
        if(writeable) return count;
        return (long) out.getPos();
    }

    /**
    * Skips exactly n bytes of input.
    * This method blocks until all the bytes are skipped, the end of the stream
    * is detected, or an exception is thrown.
    * @param n - the number of bytes to be skipped.
    * @return the number of bytes skipped, which is always n.
    * @exception java.io.EOFException if this file reaches the end before skipping all the bytes.
    * @exception java.io.IOException if an I/O error occurs.
    */
    public int skipBytes(int n) throws java.io.IOException
    {
        swap();
        return (int)out.skip(n);
    }

    /**
    * Read a 16-bit integer quanity or -1 for EOF
    * @exception java.io.IOException if an I/O error occurs.
    * @return int unsigned value of next two bytes
    */
    public int read16() throws java.io.IOException
    {
        int b1 = read();
        if(b1 < 0) return -1;
        int b2 = read();
        if(b2 < 0) return -1;
        return ((b1&0xFF)<<8) | (b2&0xFF);
    }
    /**
    * Read a 32-bit integer quanity or -1 for EOF
    * @exception java.io.IOException if an I/O error occurs.
    * @return long unsigned value of next two bytes
    */
    public long read32() throws java.io.IOException
    {
        int s1 = read16();
        if(s1 < 0) return -1;
        int s2 = read16();
        if(s2 < 0) return -1;
        long l = (s1&0xFFFF) << 8;
        return l | (s2&0xFFFF);
    }
    /**
    * Read a multiple precision integer quanity or null for EOF
    * @exception java.io.IOException if an I/O error occurs.
    * @return byte array for OpenPGP format MPI
    */
    public byte[] readMPI() throws java.io.IOException
    {
        int length = read16();
        if(length<0) return null;
        byte[] val = new byte[(length+7)/8];
        int n = read(val, 0, val.length);
        if(n != val.length) val = null;
        return val;
    }

    /**
    * Writes the specified two bytes.
    * @param i the 16 bits to be written.
    * @exception java.io.IOException if an I/O error occurs.
    */
    public void write16(int i) throws java.io.IOException
    {
        byte top = (byte)((i>>8)&0xFF);
        write(top);
        write(i&0xFF);
    }
    /**
    * Writes the specified four bytes.
    * @param i the 32 bits to be written.
    * @exception java.io.IOException if an I/O error occurs.
    */
    public void write32(long i) throws java.io.IOException
    {
        int top = (int)((i>>16)&0xFFFF);
        write16(top);
        write16((int)(i&0xFFFF));
    }
    /**
    * Writes the specified byte array as an MPI.
    * @param mpi the bytes to be written.
    * @exception java.io.IOException if an I/O error occurs.
    */
    public void writeMPI(byte[] mpi) throws java.io.IOException
    {
        int length = mpi.length * 8;
        int x = 1<<7;
        while((mpi[0] & x) == 0)
        {
            length--;
            x >>= 1;
        }

        write16(mpi.length);
        write(mpi, 0, mpi.length);
    }

}
