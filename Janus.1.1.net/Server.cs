using System;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Collections;
using System.Windows.Forms;
using System.Reflection;
using System.IO;

namespace com.ravnaandtines.janus
{
	public enum AuthenticationScheme 
	{
		UNKNOWN,
		BASIC,
		NTLM,
		NTLM_RESPONSE
	}

	/// <summary>
	/// Summary description for Server.
	/// </summary>
	public class Server
	{
		public Server(int port, ArrayList intermediates)
		{
			thePort = port;
			proxies = intermediates;
		}
		private int thePort;
		private ArrayList proxies;
		private bool running = true;
		private ArrayList connections = ArrayList.Synchronized(new ArrayList());
		private bool first = true;

		private byte [] minimal = 
						{
							0x4e, 0x54, 0x4c, 0x4d, // NTLM
							0x53, 0x53, 0x50, 0x00, // SSP<NUL>
							0x01, 0x00, 0x00, 0x00, // type 1
							0x05, 0x02, 0x08, 0x20  // negotiate Unicode NTLM, request target, strong NTLM2
						};


		// Thread signal.
		private ManualResetEvent allDone = new ManualResetEvent(false);

		public void Kill()
		{
			running = false;
		}

		public void StartListening()
		{
			// Create a TCP/IP  socket.
			Socket listener = new Socket(AddressFamily.InterNetwork,
				SocketType.Stream, ProtocolType.Tcp );

			// Bind the  socket to the local endpoint and listen for incoming connections.
			try 
			{
				IPEndPoint localEndPoint = new IPEndPoint(IPAddress.Any, thePort);
				listener.Bind(localEndPoint);
				listener.Listen(100);

				while (running) 
				{
					// Set the event to  nonsignaled state.
					allDone.Reset();

					// Start  an asynchronous socket to listen for connections.
					listener.BeginAccept( 
						new AsyncCallback(AcceptCallback),
						listener );

					// Wait until a connection is made before continuing.
					allDone.WaitOne();
				}

			} 
			catch (ThreadAbortException ex) 
			{
				running = false;
				listener.Close();
				Console.WriteLine(ex.ToString());

				if(connections.Count == 0)
					return;

				foreach(Connection c in connections)
				{
					c.Terminate();
				}

			}
			catch (Exception e) 
			{
				running = false;
				listener.Close();
				Console.WriteLine(e.ToString());
			}
		}

		public bool getCredentials(int index, String context)
		{
			PWDialog pwDialog = new PWDialog();
			ExtendedIntermediate i = (ExtendedIntermediate)proxies[index];

			pwDialog.SetText(i.Host, i.Port, context, i.userID, i.password);

			DialogResult dr = pwDialog.ShowDialog(Form1.instance);
			if(DialogResult.OK == dr)
			{
				i.userID = pwDialog.GetUser();
				i.password = pwDialog.GetPassword();
			}
			pwDialog.Dispose();
			return DialogResult.OK == dr;
		}

		public object getProxy(int index)
		{
			if(index < 0 || index >= proxies.Count)
				return null;
			return proxies[index];
		}


		public static void sendASCIIString(Socket server, string command)
		{
			byte[] body = new byte[command.Length];
			for(int x=0; x<command.Length; ++x)
				body[x] = (byte) (command[x]&0xFF);
			server.Send(body);
		}

		static string encode = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

		// assumes ASCII
		public static string encodeString(string s)
		{
			byte[] body = new byte[s.Length];
			for(int x=0; x<s.Length; ++x)
				body[x] = (byte) (s[x]&0xFF);
			return encodeBytes(body);
		}

		public static string encodeBytes(byte[] s)
		{
			int left = s.Length;
			int index = 0;
			string result = "";
			while(left > 0)
			{
				if(left >= 3)
				{
					byte a = (byte)(s[index] >> 2);
					result += encode[a];
					a = (byte)( ((s[index] << 4) & 0x30) | ((s[index+1] >> 4) & 0xf) );
					result += encode[a];
					a = (byte)( ((s[index+1] << 2) & 0x3c) | ((s[index+2] >> 6) & 0x3) );
					result += encode[a];
					a = (byte) (s[index+2] & 0x3F);
					result += encode[a];
					left -= 3;
					index += 3;
				}
				else if (2 == left)
				{
					byte a = (byte)(s[index] >> 2);
					result += encode[a];
					a = (byte)( ((s[index] << 4) & 0x30) | ((s[index+1] >> 4) & 0xf) );
					result += encode[a];
					a = (byte)((s[index+1] << 2) & 0x3c);
					result += encode[a];
					result += '=';
					left = 0;
				}
				else
				{
					byte a = (byte)(s[index] >> 2);
					result += encode[a];
					a = (byte)((s[index] << 4) & 0x30);
					result += encode[a];
					result += "==";
					left = 0;
				}
			}
			return result;
		}

		static byte invert(char c)
		{
			if('A'<= c && c <= 'Z')
				return (byte)(c-'A');
			if('a'<= c && c <= 'z')
				return (byte)(26+c-'a');
			if('0'<= c && c <= '9')
				return (byte)(52+c-'0');
			if('+' == c)
				return 62;
			if('/' == c)
				return 63;
			return 0;
		}

		public static byte[] decodeString(string s)
		{
			byte[] result = new byte[3*((s.Length+3)/4)];
			int index = 0;
			int left = s.Length;
			int l = left;
			int outdex = 0;
			while(left > 0)
			{
				left -= 4;
				byte a = invert(s[index]);
				byte b = index+1 < l ? invert(s[index+1]) : (byte)0;
				byte c = index+2 < l ? invert(s[index+2]) : (byte)0;
				byte d = index+3 < l ? invert(s[index+3]) : (byte)0;
				index += 4;

				result[outdex] = (byte)( (a << 2) | (b >> 4) );
				result[outdex+1] = (byte)( (b << 4) | (c >> 2) );
				result[outdex+2] = (byte) ( (c << 6) | d );

				outdex += 3;
			}
			return result;
		}

		public Socket ConnectTo(int index)
		{
			Socket server = null;
			if(0 == index)
			{
				server = new Socket(AddressFamily.InterNetwork,
					SocketType.Stream, ProtocolType.Tcp );

				Intermediate i = (Intermediate)proxies[0];
				try 
				{
					IPHostEntry local = Dns.Resolve(i.Host);
					IPEndPoint localEndPoint = new IPEndPoint(local.AddressList[0], i.Port);
					server.Connect(localEndPoint);
				}
				catch (Exception e)
				{
					if(first)
					{
						MessageBox.Show(Form1.instance,
							e.ToString(), Form1.instance.Text,
							MessageBoxButtons.OK,
							MessageBoxIcon.Stop);
					}
					return null;
				}
			}
			else
			{
				server = ConnectTo(index - 1);
				if(null == server)
					return null;
				ExtendedIntermediate prev = (ExtendedIntermediate)proxies[index-1];
				ExtendedIntermediate inter = (ExtendedIntermediate)proxies[index];
				if (prev.IsHTTP)
				{
					string command = "CONNECT "+inter.Host+":"+inter.Port+"  HTTP/1.1\r\nHost: "
						+inter.Host+":"+inter.Port+"\r\nAccept: */*\r\nProxy-connection: Keep-Alive\r\n\r\n";

					sendASCIIString(server, command);

					AuthenticationScheme authMethod = AuthenticationScheme.UNKNOWN;
					string context = "";
					bool sentCredentials = false;
					while(true)
					{
						string result = "";
						byte[] one = new byte[1];
						int l = 0;

						ArrayList lineEnds = new ArrayList();

						// draw down the header
						while(true)
						{
							int get = server.Receive(one);
							if(0 == get)
							{
								if(first)
								{
									MessageBox.Show(Form1.instance,
										prev.Host+" is not an open HTTP proxy", Form1.instance.Text,
										MessageBoxButtons.OK,
										MessageBoxIcon.Stop);
								}
								server.Close();
								return null;
							}
							result += (char)(one[0]&0xFF);
							if('\r' == one[0])
							{
								lineEnds.Add(l);
							}
							++l;
							if(one[0] == '\n' && result[l-4] == '\r')
								break;
						}

						//parse header return code - HTTP/1.x<space>###
						if('2' == result[9])
							return server; // OK 

						// Look for authentication required code
						int num = 100*(result[9]-'0') + 10*(result[10]-'0') + (result[11]-'0');
						if(num != 401 && num != 407)
						{
							if(first)
							{
								MessageBox.Show(Form1.instance,
									prev.Host+" is not an open HTTP proxy\nHTTP error code "+num.ToString(), 
									Form1.instance.Text,
									MessageBoxButtons.OK,
									MessageBoxIcon.Stop);
							}
							server.Close();
							return null;
						}

						//Drain message body - assume it's not chunked
						string header = "Content-Length:";
						int cl = result.IndexOf(header);
						int need = 0;
						if(cl > 0)
						{
							cl += header.Length;
							while(Char.IsWhiteSpace(result[cl]))
								++cl;
							while(Char.IsNumber(result[cl]))
							{
								need *= 10;
								need += result[cl]-'0';
							}
						}
						if(need > 0)
						{
							byte[] buf = new byte[need];
							while(need > 0)
							{
								int got = server.Receive(buf);
								need -= got;
							}
						}

						if(result.IndexOf("Connection: close") > 0 || result.IndexOf("Connection:") < 0)
						{
							server.Close();
						}
													   
						if(!server.Connected)
						{
							server = ConnectTo(index - 1);
						}

						if(null == server)
						{
							if(first)
							{
								MessageBox.Show(Form1.instance,
									"Could not reconnect to "+prev.Host, 
									Form1.instance.Text,
									MessageBoxButtons.OK,
									MessageBoxIcon.Stop);
							}
							return null;
						}

						if(AuthenticationScheme.UNKNOWN == authMethod)
						{
							// is there a Basic auth?
							string basic = "Proxy-Authenticate: Basic";
							string ntlm = "Proxy-Authenticate: NTLM";
							int ni = result.IndexOf(ntlm);
							int bi = result.IndexOf(basic);
							if(ni > 0)
							{
								authMethod = AuthenticationScheme.NTLM;
								context = "NTLM";
							}
							else if(bi > 0)
							{
								bi += basic.Length;
								string realm = "";
								while(result[bi] != '\r')
								{
									realm += result[bi];
									++bi;
								}
								authMethod = AuthenticationScheme.BASIC;
								context = realm;
							}
							else 
							{
								if(first)
								{
									int ai = result.IndexOf("-Authenticate");
									string method = "Unknown";
									if(ai > 0)
									{
										int start = ai;
										while(result[start] != '\n')
											--start;
										++start;
										StringBuilder s = new StringBuilder();
										while(result[start] != '\r')
										{
											s.Append((char)result[start]);
											++start;
										}
										method = s.ToString();
									}

									MessageBox.Show(Form1.instance,
										method + " authentication method\nfor"+
										prev.Host+" is not supported", Form1.instance.Text,
										MessageBoxButtons.OK,
										MessageBoxIcon.Stop);
								}
								server.Close();
								return null;
							}
						}

						string retry = command.Substring(0, command.Length-2);

						if(AuthenticationScheme.BASIC == authMethod)
						{
							if(0 == prev.password.Length || sentCredentials)
							{
								if(!getCredentials(index-1, context))
								{
									server.Close();
									return null;
								}
							}
							retry += "Authorization: Basic " + encodeString(prev.userID+":"+prev.password) +"\r\n\r\n";
							sendASCIIString(server, retry);
							sentCredentials = true;
						}
						else if (AuthenticationScheme.NTLM == authMethod)
						{
							retry += "Authorization: NTLM " + encodeBytes(minimal) +"\r\n\r\n";
							sendASCIIString(server, retry);
							authMethod = AuthenticationScheme.NTLM_RESPONSE;
						}
						else
						{
							string ntlm = "-Authenticate: NTLM";
							int ni = result.IndexOf(ntlm);
							ni += ntlm.Length;
							string cue = "";
							while(Char.IsWhiteSpace(result[ni]))
								++ni;
							while(result[ni] != '\r')
							{
								cue += result[ni];
								++ni;
							}
							byte [] response = decodeString(cue);

							// unpick header NTLMSSP<null> plus 0x00000002
							if(response[0] != 0x4e || response[1] != 0x54 || response[2] != 0x4c || response[3] != 0x4d
								||response[4] != 0x53 || response[5] != 0x53 || response[6] != 0x50 || response[7] != 0x0
								||response[8] != 0x2  || response[9] != 0x0  || response[10] != 0x0 || response[11] != 0x0)
							{
								MessageBox.Show(null,
									"The server sent a malformed NTLM response\n, so this program cannot continue", "driver",
									MessageBoxButtons.OK,
									MessageBoxIcon.None);
								server.Close();
								return null;
							}

							// check flags - this is what my IIS returns
							uint flags = (uint)((response[20]&0xff) | ((response[21]&0xff)<<8) | 
								((response[22]&0xff)<<16) | ((response[23]&0xff)<<24));
							if(flags != 0x208a0205)
							{
								MessageBox.Show(null,
									"Please e-mail the author of this program\n requesting support for a Type-2 NTLM message\n with flags 0x"+flags.ToString("X"), "driver",
									MessageBoxButtons.OK,
									MessageBoxIcon.None);
								server.Close();
								return null;
							}

							/*
							A "security buffer" is a structure used to point to a buffer of binary data. It consists of: 

							A short containing the length of the buffer in bytes. 
							A short containing the allocated space for the buffer in bytes (typically, though not necessarily, the same as the length). 
							A long containing the offset to the start of the buffer in bytes (from the beginning of the NTLM message). 
							*/

							// Target name security buffer is at offset 12
							ushort buflen = (ushort)((response[12]&0xff) | ((response[13]&0xff)<<8));
							uint offset = (uint)((response[16]&0xff) | ((response[17]&0xff)<<8) | 
								((response[18]&0xff)<<16) | ((response[19]&0xff)<<24));
							StringBuilder s = new StringBuilder();

							while(buflen > 0)
							{
								char c = (char)((response[offset]&0xff) | ((response[offset+1]&0xff)<<8));
								s.Append(c);
								buflen -= 2;
								offset += 2;
							}

							context = "NTLM for target " + s.ToString();
							if(0 == prev.password.Length || sentCredentials)
							{
								if(!getCredentials(index-1, context))
								{
									server.Close();
									return null;
								}
							}

							// Target info for NTLM v2 is at security buffer starting at byte 40
							buflen = (ushort)((response[40]&0xff) | ((response[41]&0xff)<<8));
							offset = (uint)((response[44]&0xff) | ((response[45]&0xff)<<8) | 
								((response[46]&0xff)<<16) | ((response[47]&0xff)<<24));

							//The NTLMv2 response is calculated as follows: 

							//The NTLM password hash is obtained - this is the MD4 digest of the Unicode mixed-case password. 
							byte[] pw = new byte[2*prev.password.Length];
							int i;
							for(i=0; i<prev.password.Length; ++i)
							{
								pw[2*i] = (byte)(prev.password[i]&0xff);
								pw[(2*i)+1] = (byte)((prev.password[i]>>8)&0xff);
							}
							gnu.crypto.hash.MD4 hash = new gnu.crypto.hash.MD4();
							hash.update(pw, 0, pw.Length);
							byte[] pwhash = hash.digest();

							//The Unicode uppercase username is concatenated with the Unicode uppercase 
							//authentication target (domain or server name). The HMAC-MD5 message authentication 
							//code algorithm (described in RFC 2104) is applied to this value using the 16-byte NTLM 
							//hash as the key. This results in a 16-byte value - the NTLMv2 hash. 

							i = prev.userID.IndexOf('\\');
							string user = prev.userID;
							string domain = "";
							if(i >= 0)
							{
								user = prev.userID.Substring(index+1);
								if(i > 0)
									domain = prev.userID.Substring(0, index);
							}
							string principal = user+domain;
							principal = principal.ToUpper();
							byte[] prin = new byte[2*principal.Length];
							for(i=0; i<principal.Length; ++i)
							{
								prin[2*i] = (byte)(principal[i]&0xff);
								prin[(2*i)+1] = (byte)((principal[i]>>8)&0xff);
							}
							byte[] v2Hash = new HMACMD5(pwhash,prin).digest();
				
							/*
							A block of data known as the "blob" is constructed.
							0 Blob Signature 0x01010000 
							4 Reserved long (0x00000000) 
							8 Timestamp Little-endian, 64-bit signed value representing the number of tenths of a microsecond since January 1, 1601.  
							16 Client Challenge 8 bytes 
							24 Unknown 4 bytes 
							28 Target Information Target Information block (from the Type 2 message). 
							(variable) Unknown 4 bytes 
							*/

							byte[] blob = new byte[32+buflen];
							for(i=0; i<blob.Length; ++i)
								blob[i] = 0;

							// signature
							blob[0] = blob[1] = 0x01;

							// timestamp
							long stamp = DateTime.Now.ToFileTime();
							for(i=0; i<8; ++i)
							{
								blob[8+i] = (byte) ((stamp>>(i*8))&0xff);
							}

							// challenge
							for(i=16; i<24; ++i)
								blob[i] = (byte)i;

							// target info
							for(i=0; i<buflen;++i)
								blob[i+28] = response[i+offset];

							//The challenge from the Type 2 message is concatenated with the blob. 
							//The HMAC-MD5 message authentication code algorithm is applied to this value 
							//using the 16-byte NTLMv2 hash (calculated in step 2) as the key. 
							//This results in a 16-byte output value. 
							byte[] data = new byte[8+blob.Length];
							// nonce is at bytes 24-31
							System.Array.Copy(response, 24, data, 0, 8);
							System.Array.Copy(blob, 0, data, 8, blob.Length);
							byte[] output = new HMACMD5(v2Hash, data).digest();


							//This value is concatenated with the blob to form the NTLMv2 response. 
							data = new byte[output.Length+blob.Length];
							System.Array.Copy(response, 24, data, 0, output.Length);
							System.Array.Copy(blob, 0, data, output.Length, blob.Length);


							/*
							The Type 3 Message
							Description Content 
							0 NTLMSSP Signature Null-terminated ASCII "NTLMSSP" (0x4e544c4d53535000) 
							8 NTLM Message Type long (0x03000000) 
							12 LM/LMv2 Response security buffer [try empty]
							20 NTLM/NTLMv2 Response security buffer 
							28 Domain Name security buffer [from domain\user response]
							36 User Name security buffer [from domain\user response]
							44 Workstation Name security buffer [try empty]
							*/

							byte[] type3 = new byte[52 + 0 + data.Length + 2*domain.Length + 2*user.Length + 0];
							for(i=0; i<type3.Length; ++i)
								type3[i] = 0;
							System.Array.Copy(minimal, 0, type3, 0, 8);
							type3[8] = 3;

							// NTLM/NTLMv2 Response security buffer
							type3[20] = type3[22] = (byte)(data.Length&0xFF);
							type3[21] = type3[23] = (byte)((data.Length>>8)&0xFF);
							type3[24] = (byte)52;
							System.Array.Copy(data, 0, type3, 52, data.Length);

							// Domain Name security buffer [from domain\user response]
							int len = 2*domain.Length;
							int at = 52+data.Length;
							type3[28] = type3[30] = (byte)(len&0xff);
							type3[29] = type3[31] = (byte)((len>>8)&0xff);
							type3[32] = (byte)(at & 0xff);
							type3[33] = (byte)((at>>8) & 0xff);
							for(i=0; i<domain.Length; ++i)
							{
								type3[at+2*i] = (byte)(domain[i]&0xFF);
								type3[1+at+2*i] = (byte)((domain[i]>>8)&0xFF);
							}

							// User Name security buffer [from domain\user response]
							at += len;
							len = 2*user.Length;
							type3[36] = type3[38] = (byte)(len&0xff);
							type3[37] = type3[39] = (byte)((len>>8)&0xff);
							type3[40] = (byte)(at & 0xff);
							type3[41] = (byte)((at>>8) & 0xff);
							for(i=0; i<user.Length; ++i)
							{
								type3[at+2*i] = (byte)(user[i]&0xFF);
								type3[1+at+2*i] = (byte)((user[i]>>8)&0xFF);
							}

							retry += "Authorization: NTLM " + encodeBytes(type3) +"\r\n\r\n";
							sendASCIIString(server, retry);
							authMethod = AuthenticationScheme.NTLM;
							sentCredentials = true;
						}

						//while status is not 200 OK or 403/502 fail
					}
				}
				else // SOCKS 5
				{
					// TODO - RFC 1929 username/password authentication
					byte[] handshake = {5,1,0};

					server.Send(handshake);
					byte[] workspace = new byte[1024];
					int got = 0;
					while(got < 2)
					{
						int chunk = server.Receive(workspace, got, 2-got, SocketFlags.None);
						if(0 == chunk)
						{
							if(first)
							{
								MessageBox.Show(Form1.instance,
									prev.Host+" is not a SOCKS proxy", Form1.instance.Text,
									MessageBoxButtons.OK,
									MessageBoxIcon.Stop);
							}
							server.Close();
							return null;
						}
					}
					if(workspace[0] != 5 || workspace[1] != 0)
					{
						if(first)
						{
							MessageBox.Show(Form1.instance,
								prev.Host+" is not an open SOCKS proxy", Form1.instance.Text,
								MessageBoxButtons.OK,
								MessageBoxIcon.Stop);
						}
						server.Close();
						return null;
					}

					server.Send(handshake);
					uint x = Form1.inet_addr(inter.Host);
					if(x == 0xffffffff)
					{
						byte[] body = new byte[inter.Host.Length+4];
						body[0] = 3;
						body[1] = (byte) inter.Host.Length;
						for(int j=0; j<body[1]; ++j)
						{
							body[2+j] = (byte)(inter.Host[j]);
						}
						body[2+inter.Host.Length] = (byte)(0xff&(inter.Port>>8));
						body[3+inter.Host.Length] = (byte)(0xff&inter.Port);
						server.Send(body);
					}
					else
					{
						byte[] body = new byte[7];
						body[0] = 1;
						body[4] = (byte)(0xff&(x>>24));
						body[3] = (byte)(0xff&(x>>16));
						body[2] = (byte)(0xff&(x>>8));
						body[1] = (byte)(0xff&x);
						body[5] = (byte)(0xff&(inter.Port>>8));
						body[6] = (byte)(0xff&inter.Port);
						server.Send(body);
					}

					got = 0;
					while(got < 4)
					{
						int chunk = server.Receive(workspace, got, 4-got, SocketFlags.None);
						if(0 == chunk)
						{
							if(first)
							{
								MessageBox.Show(Form1.instance,
									"Connection refused to "+inter.Host, Form1.instance.Text,
									MessageBoxButtons.OK,
									MessageBoxIcon.Stop);
							}
							server.Close();
							return null;
						}
					}
					if(workspace[0] != 5 || workspace[1] != 0 || workspace[2] != 0)
					{
						if(first)
						{
							MessageBox.Show(Form1.instance,
								"Connection refused to "+inter.Host, Form1.instance.Text,
								MessageBoxButtons.OK,
								MessageBoxIcon.Stop);
						}
						server.Close();
						return null;
					}

					if(workspace[3] == 3)
					{
						int chunk = server.Receive(workspace, got, 5-got, SocketFlags.None);
						if(0 == chunk)
						{
							if(first)
							{
								MessageBox.Show(Form1.instance,
									"Connection refused to "+inter.Host, Form1.instance.Text,
									MessageBoxButtons.OK,
									MessageBoxIcon.Stop);
							}
							server.Close();
							return null;
						}
					}

					int need = 0;
					switch (workspace[3])
					{
						case 1: need = 6; break;
						case 3: need = 2 + workspace[4]; break;
						case 4: need = 18; break;
						default:
						{
							if(first)
							{
								MessageBox.Show(Form1.instance,
									"Connection refused to "+inter.Host, Form1.instance.Text,
									MessageBoxButtons.OK,
									MessageBoxIcon.Stop);
							}
							server.Close();
							return null;
						}
					}
					got = 0;
					while(got < need)
					{
						int chunk = server.Receive(workspace, got, need-got, SocketFlags.None);
						if(0 == chunk)
						{
							if(first)
							{
								MessageBox.Show(Form1.instance,
									"Connection refused to "+inter.Host, Form1.instance.Text,
									MessageBoxButtons.OK,
									MessageBoxIcon.Stop);
							}
							server.Close();
							return null;
						}
					}
					// all done...

				}

			}

			return server;
		}


		public void AcceptCallback(IAsyncResult ar) 
		{
			// Signal the main thread to continue.
			allDone.Set();

			// Get the socket that handles the client request.
			Socket listener = (Socket) ar.AsyncState;

			Socket handler = null;
			
			try 
			{
				handler = listener.EndAccept(ar);
			} 
			catch (Exception ) 
			{
				return;
			}

			Socket server = ConnectTo(proxies.Count - 1);
			first = false;

			if(null == server)
			{
				handler.Close();
				return;
			}

			Connection c = new Connection(handler, server);
			connections.Add(c);
		}
	
	}
}
