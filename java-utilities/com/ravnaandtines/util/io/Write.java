package com.ravnaandtines.util.io;

/**
*  Interface Write - an interface defining primitive byte and integer output
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
*/

public interface Write
{
    /**
    * Writes the specified byte.
    * @param b the byte to be written.
    * @exception java.io.IOException if an I/O error occurs.
    */
    public abstract void write(int b) throws java.io.IOException;
    /**
    * Writes len bytes from the specified byte array starting at offset off.
    * @ param b the data.
    * @ param off the start offset in the data.
    * @ param len the number of bytes to write.
    * @exception java.io.IOException if an I/O error occurs.
    */
    public abstract void write(byte b[], int off, int len) throws java.io.IOException;
    /**
    * Writes the specified two bytes.
    * @param i the 16 bits to be written.
    * @exception java.io.IOException if an I/O error occurs.
    */
    public abstract void write16(int i) throws java.io.IOException;
    /**
    * Writes the specified four bytes.
    * @param i the 32 bits to be written.
    * @exception java.io.IOException if an I/O error occurs.
    */
    public abstract void write32(long i) throws java.io.IOException;
    /**
    * Writes the specified byte array as an MPI.
    * @param mpi the bytes to be written.
    * @exception java.io.IOException if an I/O error occurs.
    */
    public abstract void writeMPI(byte[] mpi) throws java.io.IOException;
}
