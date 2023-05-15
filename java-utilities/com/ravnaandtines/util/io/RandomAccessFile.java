package com.ravnaandtines.util.io;

/**
*  Class RandomAccessFile - The java.io class with the assertion that it
* implements the RandomAccessRead and Write interfaces.
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

public class RandomAccessFile extends java.io.RandomAccessFile
implements RandomAccessRead
{
    /**
    * Construct a new random accessfile
    * @param file java.io.File handle to file to open
    * @param mode String indicating read or write or both mode
    * @exception java.io.IOException if an I/O error occurs.
    * @see java.io.RandomAccessFile#RandomAccessFile
    */
    public RandomAccessFile(java.io.File file, String mode) throws java.io.IOException
    {
        super(file, mode);
    }
    /**
    * Construct a new random accessfile
    * @param file String name of file to open
    * @param mode String indicating read or write or both mode
    * @exception java.io.IOException if an I/O error occurs.
    * @see java.io.RandomAccessFile#RandomAccessFile
    */
    public RandomAccessFile(String file, String mode) throws java.io.IOException
    {
        super(file, mode);
    }

    /**
    * Read a 16-bit integer quanity or -1 for EOF
    * @exception java.io.IOException if an I/O error occurs.
    * @return int unsigned value of next two bytes
    */
    public int read16() throws java.io.IOException
    {
        int val;
        try{
             val = readUnsignedShort() & 0xFFFF;
        } catch (java.io.EOFException eofEx) {
        return -1;
        }
        return val;
    }

    /**
    * Read a 32-bit integer quanity or -1 for EOF
    * @exception java.io.IOException if an I/O error occurs.
    * @return long unsigned value of next two bytes
    */
    public long read32() throws java.io.IOException
    {
        long val;
        try{
             val = readInt() & 0xFFFFFFFF;
        } catch (java.io.EOFException eofEx) {
        return -1L;
        }
        return val;
    }

    /**
    * Read a multiple precision integer quanity or null for EOF
    * @exception java.io.IOException if an I/O error occurs.
    * @return byte array for OpenPGP format MPI
    */
    public byte[] readMPI() throws java.io.IOException
    {
        byte[] val = null;
        int length = readShort();

        if(length > 0)
        {
            val = new byte[(length+7)/8];
            int n = read(val);
            if(n != val.length)
            {
                val = null;
            }
        }
        return val;
    }
}