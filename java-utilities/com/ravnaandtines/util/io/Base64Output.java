package com.ravnaandtines.util.io;
import java.io.*;

/**
* Class BASE64Output - encodes as BASE64 a general bytestream
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

public class Base64Output extends FilterOutputStream
{
	private final static int MAX_LINE_SIZE = 64; /* expands to this plus \n\0 over*/
	private byte[] asciiBuffer;
	private int writeHead = 0;
   	private byte[] bin;
   	private int space=MAX_LINE_SIZE;
   	private int inBin = 0;

 	private static final String bintoasc
   		= "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
   	private final static byte PAD = (byte) '=';
   	private final static byte ZERO = (byte)'A';

	/**
	* Jackets a byte stream output
	* @param out DataOutputStream to which the encoded data are to be written
	*/
	public Base64Output(DataOutputStream out)
   	{
   		super((OutputStream) out);
      	asciiBuffer = new byte[MAX_LINE_SIZE+2];
      	bin = new byte[3];
   	}

	/**
	* writes an unencoded value to the file (e.g. header and footer information)
	* @param tag String value to output
	* @exception IOException passed upwards
	*/
	public void writeLiteral(String tag) throws IOException
	{
      	out.write(tag.getBytes());
	}

	/**
	* forces out any pending data that did not fill up a line
	* @exception IOException passed upwards
	*/
   	private void flushBuffer() throws IOException
   	{
   		if(writeHead == 0) return;
      	out.write(asciiBuffer, 0, writeHead);
        out.write(System.getProperty("line.separator").getBytes());
      	writeHead = 0;
      	space=MAX_LINE_SIZE;
   	}

	/**
	* encodes from the inBin a byte triple into 4 characters
	* @exception IOException passed upwards
	*/
   	private void encode()
   	{
   		if(inBin < 3)
      	{
      		bin[inBin] = 0;
         		asciiBuffer[writeHead+2] =
         		asciiBuffer[writeHead+3] = PAD;
      	}
      	asciiBuffer[writeHead]   =
            (byte) bintoasc.charAt((0xFF&bin[0])>>>2);
      	asciiBuffer[writeHead+1] =
            (byte) bintoasc.charAt(((bin[0]<<4) & 0x30)
            |((bin[1] >>> 4) &0x0F));
      	if(inBin > 1)
      	{
      		asciiBuffer[writeHead+2] =
                (byte) bintoasc.charAt(((bin[1] << 2) & 0x3C)
                | ((bin[2] >>> 6) & 0x03));
      		if(inBin > 2)
         		asciiBuffer[writeHead+3] =
                    (byte) bintoasc.charAt(bin[2] & 0x3F);
      	}
	}

	/**
	* encodes from the inBin a byte triple into 4 characters,
	* handling the buffer-full condition
	* @exception IOException passed upwards
	*/
   	private void push3bytes() throws IOException
   	{
		if(space < 4) flushBuffer();
      	encode();
      	inBin = 0;
      	writeHead+=4;
      	space -= 4;
   	}

	/**
	* Flushes out all data held in this object as well as that in the stream
	* @see FilterOutputStream#flush
	* @exception IOException passed upwards
	*/
	public void flush() throws IOException
   	{
   		if(inBin != 0) push3bytes();
      	flushBuffer();
      	out.flush();
   	}

	/**
	* @see FilterOutputStream#write
	* @exception IOException passed upwards
	*/
   	public void write(byte  b[]) throws IOException
	{
   		write(b, 0, b.length);
   	}


	/**
	* @see FilterOutputStream#write
	* @exception IOException passed upwards
	*/
   	public void write(byte  b[], int  off, int  len) throws IOException
   	{
   		for(int i=0; i<len && i+off < b.length; ++i) write(b[off+i]);
   	}

	/**
	* @see FilterOutputStream#write
	* @exception IOException passed upwards
	*/
   	public void write(int  b) throws IOException
   	{
   		bin[inBin] = (byte) b;
      	++inBin;
      	if(3 == inBin)
      	{
      		push3bytes();
            inBin=0;
      	}
   	}
}


