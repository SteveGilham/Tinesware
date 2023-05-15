package com.ravnaandtines.util.io;
import java.io.*;

/**
* Class ASCIIInputStream - Nasty hacky machine specific class to filter out
* stuff that C's ascii mode does (normalising line-ends and such).
* <p>
* Public domain code
* <P>
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
* <p>
* @author Mr. Tines
* @version 1.0, 6-Jul-1997.
*
*/
public class ASCIIInputStream extends FilterInputStream
{
	String sep;
	int hold;
	boolean dos;	

	/**
    * Constructs a new Input stream emulating 'C' non-binary I/O
    * @param in - the input stream to filter
    */
	public ASCIIInputStream(InputStream in)
	{
		super(in);
		sep = System.getProperty("line.separator");
		hold = 0;
		dos = sep.equals("\r\n");
	}

	/**
	* nasty hacky filter for the DOS end-of-file character
	* @param value int byte value to screen
	* @return what cane in, or EOF if ^Z detected
	*/
	private final int filter(int value)
	{
		if(dos && (value == 26)) return -1;
		else return value;
	}

	/**
	* general read operation; filters line and file ends
	* @see FilterInputStream#read
	* @exception IOException as superclass
	* @return byte value read
	*/
	public int read() throws IOException
	{
		int retval;
		if(hold != 0) {retval = hold; hold = 0;}
		else retval = in.read();
		if(-1 == retval) return retval;
		if(retval!=sep.charAt(0)) return filter(retval);
		if(1 == sep.length()) return (int)'\n';
		else if (2 == sep.length())
		{
			hold = in.read();
			if(hold == sep.charAt(1))
			{
				hold=0;
				return (int) '\n';
			}
			else return retval;
		}
		else throw new IOException("Line terminator > 2 bytes");
	}

	/**
	* no-op
	* @see FilterInputStream#mark
	* @param readLimit int dummy argument
	*/
   	public void mark(int readlimit)
   	{
   	}

	/**
	* Does not support mark
	* @see FilterInputStream#markSupported
	* @return false - to show not supported
	*/
   	public boolean markSupported()
   	{
   		return false;
   	}

	/**
	* @see FilterInputStream#read
	*/
   	public int read(byte[] b) throws IOException
   	{
   		return read(b, 0, b.length);
   	}

	/**
	* @see FilterInputStream#read
	*/
   	public int read(byte[] b, int offset, int length) throws IOException
   	{
   		int retval = 0;
      	for(;retval<length;++retval)
      	{
         	int k = read();
         	if(k == -1) break;
          	b[retval+offset] = (byte) k;
      	}
		return retval;
   	}

	/**
	* @see FilterInputStream#reset
	* @exception IOException - to show not supported
	*/
	public void reset() throws IOException
   	{
   		throw new IOException("ASCIIInputStream does not support mark/reset.");
   	}

	/**
	* @see FilterInputStream#skip
	* @exception IOException - to show not supported
	*/
   	public long skip(long n) throws IOException
   	{
   		throw new IOException("ASCIIInputStream does not support skip");
   	}
}

