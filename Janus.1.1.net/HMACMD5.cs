using System;
using System.Security.Cryptography;

namespace com.ravnaandtines.janus
{
	/// <summary>
	/// Summary description for HMACMD5.
	/// </summary>
	public class HMACMD5
	{
		private byte[] result = null;

		public HMACMD5(byte[] key, byte[] text)
		{
    //(1) append zeros to the end of K to create a B byte string
    //    (e.g., if K is of length 20 bytes and B=64, then K will be
    //     appended with 44 zero bytes 0x00)
	// here B = 64;
    //(2) XOR (bitwise exclusive-OR) the B byte string computed in step
    //    (1) with ipad

			byte[] extkey = new byte[64];
			int i;
			for(i=0; i<64; ++i)
			{
				if(i<key.Length)
					extkey[i] = (byte)(key[i] ^ 0x36);
				else
					extkey[i] = 0x36;
			}

    //(3) append the stream of data 'text' to the B byte string resulting
    //    from step (2)

			byte[] work = new byte[text.Length+64];
			System.Array.Copy(extkey, 0, work, 0, 64);
			System.Array.Copy(text, 0, work, 64, text.Length);

    //(4) apply H to the stream generated in step (3)

			MD5 hash1 = MD5.Create();
			hash1.Initialize();
			byte[] partial = hash1.ComputeHash(work, 0, work.Length);
			hash1.Clear();

    //(5) XOR (bitwise exclusive-OR) the B byte string computed in
    //    step (1) with opad

			for(i=0; i<64; ++i)
			{
				if(i<key.Length)
					extkey[i] = (byte)(key[i] ^ 0x5c);
				else
					extkey[i] = 0x5c;
			}

    //(6) append the H result from step (4) to the B byte string
    //    resulting from step (5)

			work = new byte[partial.Length+64];
			System.Array.Copy(extkey, 0, work, 0, 64);
			System.Array.Copy(partial, 0, work, 64, partial.Length);


    //(7) apply H to the stream generated in step (6) and output
    //    the result
			hash1 = MD5.Create();
			hash1.Initialize();
			result = hash1.ComputeHash(work, 0, work.Length);
			hash1.Clear();

		}

		public byte[] digest()
		{
			return result;
		}
	}
}
