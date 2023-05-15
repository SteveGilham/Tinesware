package com.ravnaandtines.util.io;

/**
*  Interface RandomAccessRead - - an interface defining primitive byte
*  and integer intput with random access
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

public interface RandomAccessRead extends Read
{
    /**
    * Sets the offset from the beginning of this file at which the next
    * read or write occurs.
    * @param pos long giving the absolute position.
    * @exception IOException if an I/O error occurs.
    */
    public abstract void seek(long l) throws java.io.IOException;

    /**
    * Returns the length of this file.
    * @return the length of this file.
    * @exception IOException if an I/O error occurs.
    */
    public abstract long length() throws java.io.IOException;

    /**
    * Returns the current offset in this file.
    * @return the offset from the beginning of the file, in bytes, at which the next read or write occurs.
    * @exception IOException if an I/O error occurs.
    */
    public abstract long getFilePointer() throws java.io.IOException;
}
