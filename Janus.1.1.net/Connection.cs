using System;
using System.Net;
using System.Net.Sockets;
using System.Text;

namespace com.ravnaandtines.janus
{

	// State object for reading client data asynchronously
	public class StateObject 
	{
		public Socket workSocket = null;       // Client  socket.
		public BucketizedPipe pipe = null;
		public byte[] buffer = new byte[1]; // Receive buffer.
	}

	public class OneWayPipe
	{
		private Socket source = null;
		private Socket sink = null;
		private String ssource = "";
		private String ssink = "";
		private BucketizedPipe pipe = new BucketizedPipe();
		private bool isClosed = false;
		private bool sending = false;

		public OneWayPipe(Socket from, String fname, Socket to, String tname)
		{
			source = from;
			ssource = fname;
			sink = to;
			ssink = tname;
			Read();
		}
	
		private void Read()
		{
			StateObject state = new StateObject();
			state.workSocket = source;
			state.pipe = pipe;

			state.workSocket.BeginReceive( state.buffer, 0, 1, 0,
				new AsyncCallback(ReadCallback), state);
		}

		public void ReadCallback(IAsyncResult ar) 
		{
			String content = String.Empty;
      
			// Retrieve the state object and the handler socket
			// from the async state object.
			StateObject state = (StateObject) ar.AsyncState;
			Socket handler = state.workSocket;

			int avail = 0;
			try 
			{
				avail = handler.Available;
			} 
			catch (Exception) 
			{
				avail = 0;
			}

			if(avail > 4095)
				avail=4095;

			byte[] tranche = new byte[avail+1] ;
			if(avail > 0)
			{
				try 
				{
					avail = handler.Receive(tranche, 1, avail, 0);
				} 
				catch (Exception) 
				{
					avail = 0;
				}
			}

			int got = 0;
			try 
			{
				got = (ar != null) ? handler.EndReceive(ar) : 0;
			}
			catch (Exception) 
			{
				// treat this as a shutdown
				got = 0;
			}

			if (got > 0 && !state.pipe.Closed) 
			{
				tranche[0] = state.buffer[0];
				state.pipe.Enqueue(tranche);

				// Not all data received. Get more.
				try 
				{
					state.workSocket.BeginReceive(state.buffer, 0, 1, 0,
						new AsyncCallback(ReadCallback), state);
				} 
				catch (Exception e) 
				{
					Console.WriteLine(e.ToString());
					Console.WriteLine(ssource+" socket failed");

					byte[] poison = new byte[0];
					state.pipe.Enqueue(poison);
					isClosed = true;
				}
			}
			else
			{
				byte[] poison = new byte[0];
				state.pipe.Enqueue(poison);
				isClosed = true;
			}

			lock(this)
			{
				if(! sending)
				{
					Send();
				}
			}

		}


		private void Send()
		{
			lock(this)
			{
				sending = true;
				byte[] tranche = pipe.Dequeue();
				if(isClosed)
				{
					try 
					{
#if DEBUG
						Console.WriteLine("Shutting down "+ssink+" because of "+ssource);
#endif
						sink.Shutdown(SocketShutdown.Both);
						sink.Close();
					} 
					catch (Exception e) 
					{
						Console.WriteLine(e.ToString());
						Console.WriteLine("Failure while shutting down "+ssink+" because of "+ssource);
					}
				}
				else if(null == tranche)
				{
					sending = false;
				}
				else if(tranche.Length == 0)
				{
					try 
					{
#if DEBUG
						Console.WriteLine("Shutting down "+ssink+" because of "+ssource);
#endif
						sink.Shutdown(SocketShutdown.Both);
						sink.Close();
					} 
					catch (Exception e) 
					{
						Console.WriteLine(e.ToString());
						Console.WriteLine("Failure while shutting down "+ssink+" because of "+ssource);
					}
				}
				else
				{
#if DEBUG
					Console.Write("To "+ssink+" - sending "+tranche.Length+" bytes\r\n");
					for(int i=0; i<tranche.Length; ++i)
					{
						Console.Write((char)tranche[i]);
					}
					Console.Write("\r\n\r\n");
#endif
					sink.BeginSend(tranche, 0, tranche.Length, SocketFlags.None,
						new AsyncCallback(SendCallback), sink);
				}
			}
		}

		private void SendCallback(IAsyncResult ar) 
		{
			try 
			{
				Socket s = (Socket) ar.AsyncState;
				int bytesSent = s.EndSend(ar);
#if DEBUG
				Console.Write("To "+ssink+" - sent "+bytesSent+" bytes\r\n");
#endif
				Send();
			} 
			catch (Exception e) 
			{
				Console.WriteLine(e.ToString());
			}
		}

	}


	/// <summary>
	/// Summary description for Connection.
	/// </summary>
	public class Connection
	{
		public Connection(Socket client, Socket server)
		{
			clientFacing = client;
			serverFacing = server;


			//ClientRead();
			//ServerRead();

			inbound = new OneWayPipe(server, "Server", client, "Client");
			outbound = new OneWayPipe(client, "Client", server, "Server");
		}

		private OneWayPipe inbound = null;
		private OneWayPipe outbound = null;
		private Socket clientFacing = null;
		private Socket serverFacing = null;

		public void Terminate()
		{
			clientFacing.Shutdown(SocketShutdown.Both);
			serverFacing.Shutdown(SocketShutdown.Both);
			clientFacing.Close();
			serverFacing.Close();
		}

	}
}
