using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;
using System.Data;
using System.Threading;
using System.IO;
using System.Runtime.Serialization.Formatters.Soap;
using System.Runtime.InteropServices;

namespace com.ravnaandtines.janus
{
	[ Serializable ]
	public class Destination
	{
		string host;
		ushort port;
		string protocol;

		public Destination()
		{
			port = 0;
		}

		public ushort Port
		{
			get
			{
				return port;
			}
			set
			{
				port = value;
			}
		}

		public string Host
		{
			get
			{
				return host;
			}
			set
			{
				host = value;
			}
		}

		public string Protocol
		{
			get
			{
				return protocol;
			}
			set
			{
				protocol = value;
			}
		}

		public override string ToString()
		{
			string s = host+"\t"+port+"\t"+protocol;
			return s;
		}

	}

	[ Serializable ]
	public class Intermediate 
	{
		string host;
		ushort port;
		bool isHttp;

		public Intermediate()
		{
			port = 0;
			isHttp = true;
		}

		public ushort Port
		{
			get
			{
				return port;
			}
			set
			{
				port = value;
			}
		}

		public string Host
		{
			get
			{
				return host;
			}
			set
			{
				host = value;
			}
		}

		public bool IsHTTP
		{
			get
			{
				return isHttp;
			}
			set
			{
				isHttp = value;
			}
		}

		public override string ToString()
		{
			string s = host+"\t"+port+"\t";
			if(isHttp) s += "HTTP";
			else s += "SOCKS5";
			return s;
		}

	}

	public class ExtendedIntermediate : Intermediate
	{
		public string userID;
		public string password;
		public ExtendedIntermediate()
		{
			Port = 0;
			IsHTTP = true;
			Host = "";
			userID = "";
			password = "";
		}
		public ExtendedIntermediate(Intermediate i)
		{
			Host = i.Host;
			Port = i.Port;
			IsHTTP = i.IsHTTP;
		}
		public Intermediate GetIntermediate()
		{
			Intermediate i = new Intermediate();
			i.Host = Host;
			i.Port = Port;
			i.IsHTTP = IsHTTP;
			return i;
		}
	}


	/// <summary>
	/// Summary description for Form1.
	/// </summary>
	public class Form1 : System.Windows.Forms.Form
	{
		private System.Windows.Forms.MainMenu mainMenu1;
		private System.Windows.Forms.MenuItem menuItem1;
		private System.Windows.Forms.MenuItem menuItem2;
		private System.Windows.Forms.MenuItem menuItem7;
		private System.Windows.Forms.MenuItem menuItem8;
		private System.Windows.Forms.Button runButton;
		private System.Windows.Forms.GroupBox groupBox1;
		private System.Windows.Forms.ListView listBox1;
		private System.Windows.Forms.Button httpButton;
		private System.Windows.Forms.Button socksButton;
		private System.Windows.Forms.Button delButton;
		private System.Windows.Forms.GroupBox groupBox2;
		private System.Windows.Forms.Label label2;
		private System.Windows.Forms.TextBox smtpHost;
		private System.Windows.Forms.Label label3;
		private System.Windows.Forms.NumericUpDown smtpPort;
		private System.Windows.Forms.NumericUpDown pop3Port;
		private System.Windows.Forms.Label label4;
		private System.Windows.Forms.TextBox pop3Host;
		private System.Windows.Forms.Label label5;
		/// <summary>
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.Container components = null;
		private Server httpProxy = null;
		private Server pop3Proxy = null;
		private Server smtpProxy = null;
		private System.Windows.Forms.Label label1;
		private System.Windows.Forms.Label label6;
		private System.Windows.Forms.NumericUpDown proxyPort;
		private System.Windows.Forms.TextBox proxyHost;
		private System.Windows.Forms.TextBox httpHost;
		private System.Windows.Forms.NumericUpDown httpPort;
		private Thread http = null;
		private Thread pop3 = null;
		private Thread smtp = null;
		private System.Windows.Forms.MenuItem menuItem3;
		private System.Windows.Forms.MenuItem menuItem4;
		private System.Windows.Forms.Label runningText;
		public static Form1 instance = null;

		private bool running = false;

		public Form1()
		{
			//
			// Required for Windows Form Designer support
			//
			InitializeComponent();

			System.Resources.ResourceManager resources = new System.Resources.ResourceManager("com.ravnaandtines.janus.Destination", System.Reflection.Assembly.GetExecutingAssembly());
			this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));

			delButton.Enabled = listBox1.SelectedIndices.Count == 1;
			int w = listBox1.Width;
			int host = (w*3)/5;
			int over = w-host;
			int last = over/2;
			listBox1.Columns.Add("Host", host, HorizontalAlignment.Left);
			listBox1.Columns.Add("Port", over-last, HorizontalAlignment.Left);
			listBox1.Columns.Add("Type", last, HorizontalAlignment.Left);
			httpButton.Enabled = false;
			socksButton.Enabled = false;
			proxyHost_TextChanged(null, null);

			instance = this;
		}

		/// <summary>
		/// Clean up any resources being used.
		/// </summary>
		protected override void Dispose( bool disposing )
		{
			if(http != null)
				httpProxy.Kill();
			if( disposing )
			{
				if (components != null) 
				{
					components.Dispose();
				}
			}
			base.Dispose( disposing );
		}

		#region Windows Form Designer generated code
		/// <summary>
		/// Required method for Designer support - do not modify
		/// the contents of this method with the code editor.
		/// </summary>
		private void InitializeComponent()
		{
			System.Resources.ResourceManager resources = new System.Resources.ResourceManager(typeof(Form1));
			this.mainMenu1 = new System.Windows.Forms.MainMenu();
			this.menuItem1 = new System.Windows.Forms.MenuItem();
			this.menuItem3 = new System.Windows.Forms.MenuItem();
			this.menuItem4 = new System.Windows.Forms.MenuItem();
			this.menuItem7 = new System.Windows.Forms.MenuItem();
			this.menuItem2 = new System.Windows.Forms.MenuItem();
			this.menuItem8 = new System.Windows.Forms.MenuItem();
			this.runButton = new System.Windows.Forms.Button();
			this.groupBox1 = new System.Windows.Forms.GroupBox();
			this.proxyPort = new System.Windows.Forms.NumericUpDown();
			this.label1 = new System.Windows.Forms.Label();
			this.delButton = new System.Windows.Forms.Button();
			this.socksButton = new System.Windows.Forms.Button();
			this.httpButton = new System.Windows.Forms.Button();
			this.listBox1 = new System.Windows.Forms.ListView();
			this.proxyHost = new System.Windows.Forms.TextBox();
			this.groupBox2 = new System.Windows.Forms.GroupBox();
			this.httpPort = new System.Windows.Forms.NumericUpDown();
			this.httpHost = new System.Windows.Forms.TextBox();
			this.label6 = new System.Windows.Forms.Label();
			this.pop3Port = new System.Windows.Forms.NumericUpDown();
			this.label4 = new System.Windows.Forms.Label();
			this.pop3Host = new System.Windows.Forms.TextBox();
			this.label5 = new System.Windows.Forms.Label();
			this.smtpPort = new System.Windows.Forms.NumericUpDown();
			this.label3 = new System.Windows.Forms.Label();
			this.smtpHost = new System.Windows.Forms.TextBox();
			this.label2 = new System.Windows.Forms.Label();
			this.runningText = new System.Windows.Forms.Label();
			this.groupBox1.SuspendLayout();
			((System.ComponentModel.ISupportInitialize)(this.proxyPort)).BeginInit();
			this.groupBox2.SuspendLayout();
			((System.ComponentModel.ISupportInitialize)(this.httpPort)).BeginInit();
			((System.ComponentModel.ISupportInitialize)(this.pop3Port)).BeginInit();
			((System.ComponentModel.ISupportInitialize)(this.smtpPort)).BeginInit();
			this.SuspendLayout();
			// 
			// mainMenu1
			// 
			this.mainMenu1.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
																					  this.menuItem1,
																					  this.menuItem2});
			// 
			// menuItem1
			// 
			this.menuItem1.Index = 0;
			this.menuItem1.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
																					  this.menuItem3,
																					  this.menuItem4,
																					  this.menuItem7});
			this.menuItem1.Text = "&File";
			// 
			// menuItem3
			// 
			this.menuItem3.Index = 0;
			this.menuItem3.Text = "&Open...";
			this.menuItem3.Click += new System.EventHandler(this.menuItem3_Click);
			// 
			// menuItem4
			// 
			this.menuItem4.Index = 1;
			this.menuItem4.Text = "&Save As...";
			this.menuItem4.Click += new System.EventHandler(this.menuItem4_Click);
			// 
			// menuItem7
			// 
			this.menuItem7.Index = 2;
			this.menuItem7.Text = "E&xit";
			this.menuItem7.Click += new System.EventHandler(this.menuItem7_Click);
			// 
			// menuItem2
			// 
			this.menuItem2.Index = 1;
			this.menuItem2.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
																					  this.menuItem8});
			this.menuItem2.Text = "&Help";
			// 
			// menuItem8
			// 
			this.menuItem8.Index = 0;
			this.menuItem8.Text = "About...";
			this.menuItem8.Click += new System.EventHandler(this.menuItem8_Click);
			// 
			// runButton
			// 
			this.runButton.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.runButton.Location = new System.Drawing.Point(440, 232);
			this.runButton.Name = "runButton";
			this.runButton.Size = new System.Drawing.Size(72, 24);
			this.runButton.TabIndex = 0;
			this.runButton.Text = "&Run";
			this.runButton.Click += new System.EventHandler(this.runButton_Click);
			// 
			// groupBox1
			// 
			this.groupBox1.Controls.AddRange(new System.Windows.Forms.Control[] {
																					this.proxyPort,
																					this.label1,
																					this.delButton,
																					this.socksButton,
																					this.httpButton,
																					this.listBox1});
			this.groupBox1.Location = new System.Drawing.Point(8, 8);
			this.groupBox1.Name = "groupBox1";
			this.groupBox1.Size = new System.Drawing.Size(272, 248);
			this.groupBox1.TabIndex = 1;
			this.groupBox1.TabStop = false;
			this.groupBox1.Text = "Intermediate Proxies";
			// 
			// proxyPort
			// 
			this.proxyPort.Location = new System.Drawing.Point(208, 184);
			this.proxyPort.Maximum = new System.Decimal(new int[] {
																	  65535,
																	  0,
																	  0,
																	  0});
			this.proxyPort.Minimum = new System.Decimal(new int[] {
																	  1,
																	  0,
																	  0,
																	  0});
			this.proxyPort.Name = "proxyPort";
			this.proxyPort.Size = new System.Drawing.Size(56, 20);
			this.proxyPort.TabIndex = 11;
			this.proxyPort.Value = new System.Decimal(new int[] {
																	80,
																	0,
																	0,
																	0});
			this.proxyPort.TextChanged += new System.EventHandler(this.proxyPort_TextChanged);
			// 
			// label1
			// 
			this.label1.Location = new System.Drawing.Point(192, 184);
			this.label1.Name = "label1";
			this.label1.Size = new System.Drawing.Size(8, 16);
			this.label1.TabIndex = 10;
			this.label1.Text = ":";
			// 
			// delButton
			// 
			this.delButton.Location = new System.Drawing.Point(192, 216);
			this.delButton.Name = "delButton";
			this.delButton.Size = new System.Drawing.Size(72, 24);
			this.delButton.TabIndex = 3;
			this.delButton.Text = "&Delete";
			this.delButton.Click += new System.EventHandler(this.delButton_Click);
			// 
			// socksButton
			// 
			this.socksButton.Location = new System.Drawing.Point(88, 216);
			this.socksButton.Name = "socksButton";
			this.socksButton.Size = new System.Drawing.Size(88, 24);
			this.socksButton.TabIndex = 2;
			this.socksButton.Text = "Add &SOCKS";
			this.socksButton.Click += new System.EventHandler(this.socksButton_Click);
			// 
			// httpButton
			// 
			this.httpButton.Location = new System.Drawing.Point(8, 216);
			this.httpButton.Name = "httpButton";
			this.httpButton.Size = new System.Drawing.Size(72, 24);
			this.httpButton.TabIndex = 1;
			this.httpButton.Text = "Add &HTTP";
			this.httpButton.Click += new System.EventHandler(this.httpButton_Click);
			// 
			// listBox1
			// 
			this.listBox1.FullRowSelect = true;
			this.listBox1.Location = new System.Drawing.Point(8, 16);
			this.listBox1.MultiSelect = false;
			this.listBox1.Name = "listBox1";
			this.listBox1.Size = new System.Drawing.Size(256, 160);
			this.listBox1.TabIndex = 0;
			this.listBox1.View = System.Windows.Forms.View.Details;
			this.listBox1.SelectedIndexChanged += new System.EventHandler(this.listBox1_SelectedIndexChanged);
			// 
			// proxyHost
			// 
			this.proxyHost.Location = new System.Drawing.Point(16, 192);
			this.proxyHost.Name = "proxyHost";
			this.proxyHost.Size = new System.Drawing.Size(184, 20);
			this.proxyHost.TabIndex = 2;
			this.proxyHost.Text = "";
			this.proxyHost.KeyPress += new System.Windows.Forms.KeyPressEventHandler(this.proxyHost_KeyPress);
			this.proxyHost.TextChanged += new System.EventHandler(this.proxyHost_TextChanged);
			// 
			// groupBox2
			// 
			this.groupBox2.Controls.AddRange(new System.Windows.Forms.Control[] {
																					this.httpPort,
																					this.httpHost,
																					this.label6,
																					this.pop3Port,
																					this.label4,
																					this.pop3Host,
																					this.label5,
																					this.smtpPort,
																					this.label3,
																					this.smtpHost,
																					this.label2});
			this.groupBox2.Location = new System.Drawing.Point(288, 16);
			this.groupBox2.Name = "groupBox2";
			this.groupBox2.Size = new System.Drawing.Size(224, 160);
			this.groupBox2.TabIndex = 3;
			this.groupBox2.TabStop = false;
			this.groupBox2.Text = "Targets";
			// 
			// httpPort
			// 
			this.httpPort.Location = new System.Drawing.Point(160, 128);
			this.httpPort.Maximum = new System.Decimal(new int[] {
																	 65535,
																	 0,
																	 0,
																	 0});
			this.httpPort.Minimum = new System.Decimal(new int[] {
																	 1,
																	 0,
																	 0,
																	 0});
			this.httpPort.Name = "httpPort";
			this.httpPort.Size = new System.Drawing.Size(56, 20);
			this.httpPort.TabIndex = 12;
			this.httpPort.Value = new System.Decimal(new int[] {
																   80,
																   0,
																   0,
																   0});
			this.httpPort.TextChanged += new System.EventHandler(this.proxyPort_TextChanged);
			this.httpPort.ValueChanged += new System.EventHandler(this.smtpPort_ValueChanged);
			// 
			// httpHost
			// 
			this.httpHost.Location = new System.Drawing.Point(8, 128);
			this.httpHost.Name = "httpHost";
			this.httpHost.Size = new System.Drawing.Size(128, 20);
			this.httpHost.TabIndex = 11;
			this.httpHost.Text = "";
			this.httpHost.KeyPress += new System.Windows.Forms.KeyPressEventHandler(this.proxyHost_KeyPress);
			this.httpHost.TextChanged += new System.EventHandler(this.smtpHost_TextChanged);
			// 
			// label6
			// 
			this.label6.Location = new System.Drawing.Point(8, 112);
			this.label6.Name = "label6";
			this.label6.Size = new System.Drawing.Size(160, 16);
			this.label6.TabIndex = 10;
			this.label6.Text = "&HTTP Proxy host:port:";
			// 
			// pop3Port
			// 
			this.pop3Port.Location = new System.Drawing.Point(160, 80);
			this.pop3Port.Maximum = new System.Decimal(new int[] {
																	 65535,
																	 0,
																	 0,
																	 0});
			this.pop3Port.Minimum = new System.Decimal(new int[] {
																	 1,
																	 0,
																	 0,
																	 0});
			this.pop3Port.Name = "pop3Port";
			this.pop3Port.Size = new System.Drawing.Size(56, 20);
			this.pop3Port.TabIndex = 9;
			this.pop3Port.Value = new System.Decimal(new int[] {
																   110,
																   0,
																   0,
																   0});
			this.pop3Port.TextChanged += new System.EventHandler(this.proxyPort_TextChanged);
			this.pop3Port.ValueChanged += new System.EventHandler(this.smtpPort_ValueChanged);
			// 
			// label4
			// 
			this.label4.Location = new System.Drawing.Point(144, 80);
			this.label4.Name = "label4";
			this.label4.Size = new System.Drawing.Size(8, 16);
			this.label4.TabIndex = 8;
			this.label4.Text = ":";
			// 
			// pop3Host
			// 
			this.pop3Host.Location = new System.Drawing.Point(8, 80);
			this.pop3Host.Name = "pop3Host";
			this.pop3Host.Size = new System.Drawing.Size(128, 20);
			this.pop3Host.TabIndex = 7;
			this.pop3Host.Text = "";
			this.pop3Host.KeyPress += new System.Windows.Forms.KeyPressEventHandler(this.proxyHost_KeyPress);
			this.pop3Host.TextChanged += new System.EventHandler(this.smtpHost_TextChanged);
			// 
			// label5
			// 
			this.label5.Location = new System.Drawing.Point(8, 64);
			this.label5.Name = "label5";
			this.label5.Size = new System.Drawing.Size(160, 16);
			this.label5.TabIndex = 6;
			this.label5.Text = "POP&3 Server host:port:";
			// 
			// smtpPort
			// 
			this.smtpPort.Location = new System.Drawing.Point(160, 32);
			this.smtpPort.Maximum = new System.Decimal(new int[] {
																	 65535,
																	 0,
																	 0,
																	 0});
			this.smtpPort.Minimum = new System.Decimal(new int[] {
																	 1,
																	 0,
																	 0,
																	 0});
			this.smtpPort.Name = "smtpPort";
			this.smtpPort.Size = new System.Drawing.Size(56, 20);
			this.smtpPort.TabIndex = 5;
			this.smtpPort.Value = new System.Decimal(new int[] {
																   25,
																   0,
																   0,
																   0});
			this.smtpPort.TextChanged += new System.EventHandler(this.proxyPort_TextChanged);
			this.smtpPort.ValueChanged += new System.EventHandler(this.smtpPort_ValueChanged);
			// 
			// label3
			// 
			this.label3.Location = new System.Drawing.Point(144, 32);
			this.label3.Name = "label3";
			this.label3.Size = new System.Drawing.Size(8, 16);
			this.label3.TabIndex = 4;
			this.label3.Text = ":";
			// 
			// smtpHost
			// 
			this.smtpHost.Location = new System.Drawing.Point(8, 32);
			this.smtpHost.Name = "smtpHost";
			this.smtpHost.Size = new System.Drawing.Size(128, 20);
			this.smtpHost.TabIndex = 3;
			this.smtpHost.Text = "";
			this.smtpHost.KeyPress += new System.Windows.Forms.KeyPressEventHandler(this.proxyHost_KeyPress);
			this.smtpHost.TextChanged += new System.EventHandler(this.smtpHost_TextChanged);
			// 
			// label2
			// 
			this.label2.Location = new System.Drawing.Point(8, 16);
			this.label2.Name = "label2";
			this.label2.Size = new System.Drawing.Size(160, 16);
			this.label2.TabIndex = 2;
			this.label2.Text = "S&MTP Server host:port:";
			// 
			// runningText
			// 
			this.runningText.Location = new System.Drawing.Point(440, 232);
			this.runningText.Name = "runningText";
			this.runningText.Size = new System.Drawing.Size(72, 24);
			this.runningText.TabIndex = 4;
			this.runningText.Text = "Running";
			this.runningText.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
			this.runningText.Visible = false;
			// 
			// Form1
			// 
			this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
			this.ClientSize = new System.Drawing.Size(520, 266);
			this.Controls.AddRange(new System.Windows.Forms.Control[] {
																		  this.runningText,
																		  this.groupBox2,
																		  this.proxyHost,
																		  this.groupBox1,
																		  this.runButton});
			this.MaximizeBox = false;
			this.MaximumSize = new System.Drawing.Size(528, 321);
			this.Menu = this.mainMenu1;
			this.MinimumSize = new System.Drawing.Size(528, 321);
			this.Name = "Form1";
			this.Text = "Janus/.NET";
			this.Closing += new System.ComponentModel.CancelEventHandler(this.Form1_Closing);
			this.groupBox1.ResumeLayout(false);
			((System.ComponentModel.ISupportInitialize)(this.proxyPort)).EndInit();
			this.groupBox2.ResumeLayout(false);
			((System.ComponentModel.ISupportInitialize)(this.httpPort)).EndInit();
			((System.ComponentModel.ISupportInitialize)(this.pop3Port)).EndInit();
			((System.ComponentModel.ISupportInitialize)(this.smtpPort)).EndInit();
			this.ResumeLayout(false);

		}
		#endregion

		/// <summary>
		/// The main entry point for the application.
		/// </summary>
		[STAThread]
		static void Main() 
		{
			Application.Run(new Form1());
		}

		private void menuItem7_Click(object sender, System.EventArgs e)
		{
			Application.Exit();
		}

		private void runButton_Click(object sender, System.EventArgs e)
		{
			runButton.Enabled = false;
			runButton.Visible = false;
			runningText.Visible = true;
			running = true;


			if(httpHost.Text.Length > 0)
			{
				ArrayList proxies = new ArrayList();
				int num = listBox1.Items.Count;
				for(int i=0; i< num; ++i)
					proxies.Add(listBox1.Items[i].Tag);
				ExtendedIntermediate end = new ExtendedIntermediate();
				end.Host = httpHost.Text;
				end.Port = (ushort)httpPort.Value;
				proxies.Add(end);

				httpProxy = new Server((int)httpPort.Value, proxies);
				http = new Thread(new ThreadStart(httpProxy.StartListening));
				http.Start();
			}

			if(smtpHost.Text.Length > 0)
			{
				ArrayList proxies = new ArrayList();
				int num = listBox1.Items.Count;
				for(int i=0; i< num; ++i)
					proxies.Add(listBox1.Items[i].Tag);
				ExtendedIntermediate end = new ExtendedIntermediate();
				end.Host = smtpHost.Text;
				end.Port = (ushort)smtpPort.Value;
				proxies.Add(end);

				smtpProxy = new Server((int)smtpPort.Value, proxies);
				smtp = new Thread(new ThreadStart(smtpProxy.StartListening));
				smtp.Start();
			}

			if(pop3Host.Text.Length > 0)
			{
				ArrayList proxies = new ArrayList();
				int num = listBox1.Items.Count;
				for(int i=0; i< num; ++i)
					proxies.Add(listBox1.Items[i].Tag);
				ExtendedIntermediate end = new ExtendedIntermediate();
				end.Host = pop3Host.Text;
				end.Port = (ushort)pop3Port.Value;
				proxies.Add(end);

				pop3Proxy = new Server((int)pop3Port.Value, proxies);
				pop3 = new Thread(new ThreadStart(pop3Proxy.StartListening));
				pop3.Start();
			}

		}

		private void menuItem8_Click(object sender, System.EventArgs e)
		{
			About abtDialog = new About();

			abtDialog.ShowDialog(this);
			abtDialog.Dispose();
		}

		private void listBox1_SelectedIndexChanged(object sender, System.EventArgs e)
		{
			delButton.Enabled = !running && (listBox1.SelectedIndices.Count == 1);
		}

		private void proxyHost_KeyPress(object sender, System.Windows.Forms.KeyPressEventArgs e)
		{
			char c = e.KeyChar;
			if(c >= 128)
			{
				e.Handled = true;
				return;
			}

			if(0x08 == c || '.' == c || '-' == c || Char.IsDigit(c) || Char.IsLetter(c))
				return;
			else
				e.Handled = true;
		}

		private void httpButton_Click(object sender, System.EventArgs e)
		{
			ExtendedIntermediate i = new ExtendedIntermediate();
			i.Host = proxyHost.Text;
			i.Port = (ushort) proxyPort.Value;
			i.IsHTTP = true;
			ListViewItem item = new ListViewItem(i.Host);
			item.SubItems.Add(""+i.Port);
			item.SubItems.Add("HTTP");
			item.Tag = i;

			listBox1.Items.Add(item);
			proxyHost_TextChanged(null, null);
		}

		private void socksButton_Click(object sender, System.EventArgs e)
		{
			ExtendedIntermediate i = new ExtendedIntermediate();
			i.Host = proxyHost.Text;
			i.Port = (ushort) proxyPort.Value;
			i.IsHTTP = false;
			ListViewItem item = new ListViewItem(i.Host);
			item.SubItems.Add(""+i.Port);
			item.SubItems.Add("SOCKS");
			item.Tag = i;

			listBox1.Items.Add(item);
			proxyHost_TextChanged(null, null);
		}

		private void proxyHost_TextChanged(object sender, System.EventArgs e)
		{
			httpButton.Enabled = false;
			socksButton.Enabled = false;
			smtpHost_TextChanged(null, null);

			if(running)
				return;

			if(null == sender)
			{
				return;
			}

			TextBox box = (TextBox) sender;
			string t = box.Text;

			if(t.Equals(""))
			{
				return;
			}

			if(!plausibleHost(t))
				return;

			int num = listBox1.Items.Count;
			for(int j=0; j<num; ++j)
			{
				ExtendedIntermediate i = (ExtendedIntermediate) listBox1.Items[j].Tag;
				if(i.Host.ToLower().Equals(t.ToLower()) &&
					i.Port==proxyPort.Value)
				return;
			}

			httpButton.Enabled = true;
			socksButton.Enabled = true;
			
		}

		private void delButton_Click(object sender, System.EventArgs e)
		{
			int index = listBox1.SelectedIndices[0];
			listBox1.Items.RemoveAt(index);
			if(0 == listBox1.Items.Count)
				return;
			if(index > 0)
				--index;
			listBox1.Items[index].Selected = true;
		}

		private void ensurePortsDiffer()
		{
			if(smtpHost.Text.Length > 0 && pop3Host.Text.Length > 0)
			{
				if(smtpPort.Value == pop3Port.Value)
					runButton.Enabled = false;
			}
			if(smtpHost.Text.Length > 0 && httpHost.Text.Length > 0)
			{
				if(smtpPort.Value == httpPort.Value)
					runButton.Enabled = false;
			}
			if(httpHost.Text.Length > 0 && pop3Host.Text.Length > 0)
			{
				if(httpPort.Value == pop3Port.Value)
					runButton.Enabled = false;
			}
		}

		private void proxyPort_TextChanged(object sender, System.EventArgs e)
		{
			NumericUpDown ud = (NumericUpDown)sender;
			if(ud.Value < 1) ud.Value = 1;
			else if (ud.Value > 65535) ud.Value = 65535;

			if(sender.Equals(proxyPort))
			{
				int num = listBox1.Items.Count;
				for(int j=0; j<num; ++j)
				{
					ExtendedIntermediate i = (ExtendedIntermediate) listBox1.Items[j].Tag;
					if(i.Host.ToLower().Equals(proxyHost.Text.ToLower()) &&
						i.Port==proxyPort.Value)
						return;
				}
			}
			ensurePortsDiffer();

		}


		private bool plausibleHost(string t)
		{
			if(t.Equals(""))
			{
				return true;
			}

			if(t[0] == '.' || t[t.Length-1] == '.')
			{
				return false;
			}

			int ndot = 0;
			int index = -1;
			int next = t.IndexOf(".");
			while(next >= 0)
			{
				++ndot;
				if(t[next+1] == '.')
					return false;
				index = next;
				next = t.IndexOf(".", index+1);
			}
			if(index > 0)
			{
				if(Char.IsDigit(t[index+1]))
					return (3 == ndot) ? isDottedQuad(t) : false;
			}	
			return true;
		}

		[DllImport("Ws2_32.dll", CharSet=CharSet.Ansi)]
		public static extern uint inet_addr(String quad);

		public bool isDottedQuad(string t)
		{
			uint x = inet_addr(t);
			return x > 0 && x != 0xffffffff;
		}

		private void smtpHost_TextChanged(object sender, System.EventArgs e)
		{
			runButton.Enabled = !running &&
				plausibleHost(smtpHost.Text)
				&& plausibleHost(pop3Host.Text)
				&& plausibleHost(httpHost.Text)
				&& listBox1.Items.Count > 0 
				&& (smtpHost.Text.Length > 0 ||
				    pop3Host.Text.Length > 0 ||
					httpHost.Text.Length > 0);
			ensurePortsDiffer();		
		}

		private void menuItem3_Click(object sender, System.EventArgs e)
		{
			Stream myStream ;
			OpenFileDialog openFileDialog1 = new OpenFileDialog();
 
			openFileDialog1.Filter = "jnt files (*.jnt)|*.jnt|All files (*.*)|*.*"  ;
			openFileDialog1.FilterIndex = 1 ;
			openFileDialog1.RestoreDirectory = true ;
 
			if(openFileDialog1.ShowDialog() == DialogResult.OK)
			{
				SoapFormatter formatter = new SoapFormatter();

				if((myStream = openFileDialog1.OpenFile()) != null)
				{
					Destination d = (Destination) formatter.Deserialize(myStream);
					smtpHost.Text = d.Host;
					smtpPort.Value = d.Port;
					
					d = (Destination) formatter.Deserialize(myStream);
					pop3Host.Text = d.Host;
					pop3Port.Value = d.Port;

					d = (Destination) formatter.Deserialize(myStream);
					httpHost.Text = d.Host;
					httpPort.Value = d.Port;

					listBox1.Items.Clear();

					Int32 n = (Int32) formatter.Deserialize(myStream);
					for(int i=0; i<n; ++i)
					{
						Intermediate x = (Intermediate) formatter.Deserialize(myStream);//, (Intermediate)listBox1.Items[i].Tag);
						ListViewItem item = new ListViewItem(x.Host);
						item.SubItems.Add(""+x.Port);
						if(x.IsHTTP) item.SubItems.Add("HTTP");
						else item.SubItems.Add("SOCKS");
						item.Tag = new ExtendedIntermediate(x);
						listBox1.Items.Add(item);
					}

					myStream.Close();
					proxyHost_TextChanged(null, null);
				}
			}
			
		}

		// save
		private void menuItem4_Click(object sender, System.EventArgs e)
		{
			Stream myStream ;
			SaveFileDialog saveFileDialog1 = new SaveFileDialog();
 
			saveFileDialog1.Filter = "jnt files (*.jnt)|*.jnt|All files (*.*)|*.*"  ;
			saveFileDialog1.FilterIndex = 1 ;
			saveFileDialog1.RestoreDirectory = true ;
 
			if(saveFileDialog1.ShowDialog() == DialogResult.OK)
			{
				SoapFormatter formatter = new SoapFormatter();

				if((myStream = saveFileDialog1.OpenFile()) != null)
				{
					Destination d = new Destination();
					d.Host = smtpHost.Text;
					d.Port = (ushort) smtpPort.Value;
					d.Protocol = "SMTP";
					
					formatter.Serialize(myStream, d);

					d.Host = pop3Host.Text;
					d.Port = (ushort) pop3Port.Value;
					d.Protocol = "POP3";

					formatter.Serialize(myStream, d);

					d.Host = httpHost.Text;
					d.Port = (ushort) httpPort.Value;
					d.Protocol = "HTTP";

					formatter.Serialize(myStream, d);

					Int32 n = listBox1.Items.Count;
					formatter.Serialize(myStream, n);

					for(int i=0; i<n; ++i)
					{
						formatter.Serialize(myStream, 
							((ExtendedIntermediate)listBox1.Items[i].Tag).GetIntermediate());
					}

					myStream.Close();
				}
			}
			
		}

		private void smtpPort_ValueChanged(object sender, System.EventArgs e)
		{
			ensurePortsDiffer();		
		}

		private void Form1_Closing(object sender, System.ComponentModel.CancelEventArgs e)
		{
			if(smtp != null)
				smtp.Abort();
			if(http != null)
				http.Abort();
			if(pop3 != null)
				pop3.Abort();
		}

	}
}
