using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;

namespace com.ravnaandtines.janus
{
	/// <summary>
	/// Summary description for PWDialog.
	/// </summary>
	public class PWDialog : System.Windows.Forms.Form
	{
		private System.Windows.Forms.Label label1;
		private System.Windows.Forms.Label label2;
		private System.Windows.Forms.Label label3;
		private System.Windows.Forms.Label label4;
		private System.Windows.Forms.TextBox textBox1;
		private System.Windows.Forms.TextBox textBox2;
		private System.Windows.Forms.Button button1;
		private System.Windows.Forms.Button button2;
		private System.Windows.Forms.Label label5;
		/// <summary>
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.Container components = null;

		public PWDialog()
		{
			//
			// Required for Windows Form Designer support
			//
			InitializeComponent();

			//
			// TODO: Add any constructor code after InitializeComponent call
			//
		}

		public void SetText(string proxy, int port, string context, string user, string password)
		{
			label1.Text = "Authentication required for "+proxy+":"+port;
			label2.Text = "Context: "+context;
			textBox1.Text = user;
			textBox2.Text = password;
		}

		public string GetUser()
		{
			return textBox1.Text;
		}

		public string GetPassword()
		{
			return textBox2.Text;
		}

		/// <summary>
		/// Clean up any resources being used.
		/// </summary>
		protected override void Dispose( bool disposing )
		{
			if( disposing )
			{
				if(components != null)
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
			System.Resources.ResourceManager resources = new System.Resources.ResourceManager(typeof(PWDialog));
			this.label1 = new System.Windows.Forms.Label();
			this.label2 = new System.Windows.Forms.Label();
			this.label3 = new System.Windows.Forms.Label();
			this.label4 = new System.Windows.Forms.Label();
			this.textBox1 = new System.Windows.Forms.TextBox();
			this.textBox2 = new System.Windows.Forms.TextBox();
			this.button1 = new System.Windows.Forms.Button();
			this.button2 = new System.Windows.Forms.Button();
			this.label5 = new System.Windows.Forms.Label();
			this.SuspendLayout();
			// 
			// label1
			// 
			this.label1.Location = new System.Drawing.Point(16, 8);
			this.label1.Name = "label1";
			this.label1.Size = new System.Drawing.Size(488, 16);
			this.label1.TabIndex = 0;
			this.label1.Text = "label1";
			// 
			// label2
			// 
			this.label2.Location = new System.Drawing.Point(16, 40);
			this.label2.Name = "label2";
			this.label2.Size = new System.Drawing.Size(488, 16);
			this.label2.TabIndex = 1;
			this.label2.Text = "label2";
			// 
			// label3
			// 
			this.label3.Location = new System.Drawing.Point(16, 72);
			this.label3.Name = "label3";
			this.label3.Size = new System.Drawing.Size(88, 16);
			this.label3.TabIndex = 2;
			this.label3.Text = "Username:";
			// 
			// label4
			// 
			this.label4.Location = new System.Drawing.Point(16, 136);
			this.label4.Name = "label4";
			this.label4.Size = new System.Drawing.Size(88, 16);
			this.label4.TabIndex = 3;
			this.label4.Text = "Password:";
			// 
			// textBox1
			// 
			this.textBox1.Location = new System.Drawing.Point(104, 70);
			this.textBox1.Name = "textBox1";
			this.textBox1.Size = new System.Drawing.Size(400, 20);
			this.textBox1.TabIndex = 4;
			this.textBox1.Text = "textBox1";
			// 
			// textBox2
			// 
			this.textBox2.Location = new System.Drawing.Point(104, 134);
			this.textBox2.Name = "textBox2";
			this.textBox2.PasswordChar = '*';
			this.textBox2.Size = new System.Drawing.Size(400, 20);
			this.textBox2.TabIndex = 5;
			this.textBox2.Text = "textBox2";
			// 
			// button1
			// 
			this.button1.DialogResult = System.Windows.Forms.DialogResult.OK;
			this.button1.Location = new System.Drawing.Point(328, 168);
			this.button1.Name = "button1";
			this.button1.Size = new System.Drawing.Size(80, 24);
			this.button1.TabIndex = 6;
			this.button1.Text = "OK";
			// 
			// button2
			// 
			this.button2.DialogResult = System.Windows.Forms.DialogResult.Cancel;
			this.button2.Location = new System.Drawing.Point(424, 168);
			this.button2.Name = "button2";
			this.button2.Size = new System.Drawing.Size(80, 24);
			this.button2.TabIndex = 7;
			this.button2.Text = "Cancel";
			// 
			// label5
			// 
			this.label5.Location = new System.Drawing.Point(16, 96);
			this.label5.Name = "label5";
			this.label5.Size = new System.Drawing.Size(488, 16);
			this.label5.TabIndex = 8;
			this.label5.Text = "For NTLM, give domain\\username rather than just username";
			// 
			// PWDialog
			// 
			this.AcceptButton = this.button1;
			this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
			this.CancelButton = this.button2;
			this.ClientSize = new System.Drawing.Size(528, 198);
			this.Controls.AddRange(new System.Windows.Forms.Control[] {
																		  this.label5,
																		  this.button2,
																		  this.button1,
																		  this.textBox2,
																		  this.textBox1,
																		  this.label4,
																		  this.label3,
																		  this.label2,
																		  this.label1});
			this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
			this.Name = "PWDialog";
			this.Text = "Proxy Authentication required";
			this.ResumeLayout(false);

		}
		#endregion
	}
}
