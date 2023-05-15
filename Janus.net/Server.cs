using System;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Collections;
using System.Windows.Forms;

namespace com.ravnaandtines.janus
{
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

			Socket server = new Socket(AddressFamily.InterNetwork,
				SocketType.Stream, ProtocolType.Tcp );

			int index = 0;
			int num = proxies.Count;

			// try to attach to the far end
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
				handler.Close();
				return;
			}
			Intermediate prev = i;
			bool prevIsHTTP = i.IsHTTP;
			
			for(index = 1;index<num;++index)
			{
				i = (Intermediate)proxies[index];
				if (prevIsHTTP)
				{
					string command = "CONNECT "+i.Host+":"+i.Port+"  HTTP/1.1\r\nHost: "+i.Host+":"+i.Port+"\r\n\r\n";
					byte[] body = new byte[command.Length];
					for(int x=0; x<command.Length; ++x)
						body[x] = (byte) (command[x]&0xFF);
					server.Send(body);

					string result = "";
					byte[] one = new byte[1];
					int l = 0;

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
							handler.Close();
							server.Close();
							return;
						}
						result += (char)(one[0]&0xFF);
						++l;
						if(one[0] == '\n' && result[l-4] == '\r')
							break;
					}

					// HTTP/1.x<space>###
					if(result[9] != '2')
					{
						if(first)
						{
							MessageBox.Show(Form1.instance,
								prev.Host+" is not an open HTTP proxy", Form1.instance.Text,
								MessageBoxButtons.OK,
								MessageBoxIcon.Stop);
						}
						handler.Close();
						server.Close();
						return;
					}

				}
				else // SOCKS 5
				{
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
							handler.Close();
							return;
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
						handler.Close();
						server.Close();
						return;
					}

					server.Send(handshake);
					uint x = Form1.inet_addr(i.Host);
					if(x == 0xffffffff)
					{
						byte[] body = new byte[i.Host.Length+4];
						body[0] = 3;
						body[1] = (byte) i.Host.Length;
						for(int j=0; j<body[1]; ++j)
						{
							body[2+j] = (byte)(i.Host[j]);
						}
						body[2+i.Host.Length] = (byte)(0xff&(i.Port>>8));
						body[3+i.Host.Length] = (byte)(0xff&i.Port);
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
						body[5] = (byte)(0xff&(i.Port>>8));
						body[6] = (byte)(0xff&i.Port);
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
									"Connection refused to "+i.Host, Form1.instance.Text,
									MessageBoxButtons.OK,
									MessageBoxIcon.Stop);
							}
							handler.Close();
							server.Close();
							return;
						}
					}
					if(workspace[0] != 5 || workspace[1] != 0 || workspace[2] != 0)
					{
						if(first)
						{
							MessageBox.Show(Form1.instance,
								"Connection refused to "+i.Host, Form1.instance.Text,
								MessageBoxButtons.OK,
								MessageBoxIcon.Stop);
						}
						handler.Close();
						server.Close();
						return;
					}

					if(workspace[3] == 3)
					{
						int chunk = server.Receive(workspace, got, 5-got, SocketFlags.None);
						if(0 == chunk)
						{
							if(first)
							{
								MessageBox.Show(Form1.instance,
									"Connection refused to "+i.Host, Form1.instance.Text,
									MessageBoxButtons.OK,
									MessageBoxIcon.Stop);
							}
							handler.Close();
							server.Close();
							return;
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
									"Connection refused to "+i.Host, Form1.instance.Text,
									MessageBoxButtons.OK,
									MessageBoxIcon.Stop);
							}
							handler.Close();
							server.Close();
							return;
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
									"Connection refused to "+i.Host, Form1.instance.Text,
									MessageBoxButtons.OK,
									MessageBoxIcon.Stop);
							}
							handler.Close();
							server.Close();
							return;
						}
					}
					// all done...

				}
				prevIsHTTP = i.IsHTTP;
				prev = i;
			}

			first = false;

			Connection c = new Connection(handler, server);
			connections.Add(c);
		}
	
	}
}
