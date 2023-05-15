package com.ravnaandtines.util.io;

/**
*  Interface Read - an interface defining primitive byte and integer input
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

public interface Read
{
    /**
    * Reads a byte of data from this file. This method blocks if no input is
    * yet available.
    * @return the next byte of data, or -1 if the end of the file is reached.
    * @exception IOException if an I/O error occurs.
    */
    public abstract int read() throws java.io.IOException;
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
    public abstract int read(byte b[], int off, int len) throws java.io.IOException;
    /**
    * Read a 16-bit integer quantity or -1 for EOF
    * @exception java.io.IOException if an I/O error occurs.
    * @return int unsigned value of next two bytes
    */
    public abstract int read16() throws java.io.IOException;
    /**
    * Read a 32-bit integer quantity or -1 for EOF
    * @exception java.io.IOException if an I/O error occurs.
    * @return long unsigned value of next two bytes
    */
    public abstract long read32() throws java.io.IOException;
    /**
    * Read a multiple precision integer quantity or null for EOF
    * @exception java.io.IOException if an I/O error occurs.
    * @return byte array for OpenPGP format MPI
    */
    public abstract byte[] readMPI() throws java.io.IOException;
    /**
    * Closes this stream and releases any system resources associated with it.
    * @exception java.io.IOException if an I/O error occurs.
    */
    public abstract void close() throws java.io.IOException;
    /**
    * Skips exactly n bytes of input.
    * This method blocks until all the bytes are skipped, the end of the stream
    * is detected, or an exception is thrown.
    * @param n the number of bytes to be skipped.
    * @return the number of bytes skipped, which is always n.
    * @exception java.io.EOFException if this file reaches the end before skipping all the bytes.
    * @exception java.io.IOException if an I/O error occurs.
    */
    public abstract int skipBytes(int n) throws java.io.IOException;
}
