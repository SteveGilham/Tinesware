package com.ravnaandtines.util.io;

import java.io.*;
/**
*  Class WriteOutputStream - A filter output stream that implements
* the Write interface
*  <P>
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


public class WriteOutputStream extends FilterOutputStream
implements Write
{

    /**
    * Wraps an OutputStream with this filter
    * @param s the output stream to wrap.
    */
    public WriteOutputStream(OutputStream s)
    {
        super(s);
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
        write(mpi, 0, mpi.length);
    }
}
