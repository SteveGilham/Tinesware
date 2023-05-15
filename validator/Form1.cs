/*
 * $Id: Form1.cs,v 1.7 2003/05/17 07:12:43 Steve Exp $
 * 
 * XML Validator
 *
 * Public domain application implementation by Mr. Tines <tines@ravnaandtines.com>
 *
 * Version 1.0 (3-May-03)
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * $Log: Form1.cs,v $
 * Revision 1.7  2003/05/17 07:12:43  Steve
 * Version 1.1 - check for cp1252 naughties
 *
 * Revision 1.6  2003/05/06 18:19:51  Steve
 * Usability tweaks
 *
 * Revision 1.5  2003/05/05 15:26:49  Steve
 * Use OS style - so pick up Luna buttons on XP default.
 *
 * Revision 1.4  2003/05/05 14:21:25  Steve
 * Work around the work-around for the DTD
 *
 */


using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;
using System.Data;
using System.IO;
using System.Reflection;
using System.Xml;
using System.Xml.Schema;

namespace Validator
{
	/// <summary>
	/// Summary description for Form1.
	/// </summary>
	public class ValidatorForm : System.Windows.Forms.Form
	{
		private System.Windows.Forms.Label label1;
		private System.Windows.Forms.ComboBox schemaBox;
		private System.Windows.Forms.Label label2;
		private System.Windows.Forms.TextBox fileBox;
		private System.Windows.Forms.Button fileButton;
		private System.Windows.Forms.RichTextBox resultBox;
		private System.Windows.Forms.Button validateButton;
		private System.Windows.Forms.OpenFileDialog browser;
		private System.Windows.Forms.Label about;
		/// <summary>
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.Container components = null;

		public ValidatorForm()
		{
			//
			// Required for Windows Form Designer support
			//
			InitializeComponent();

			//
			// TODO: Add any constructor code after InitializeComponent call
			//

			// Fill in uI items
			schemaBox.Items.Add("1.0 Transitional");
			schemaBox.Items.Add("1.0 Strict");
			schemaBox.Items.Add("1.1");
			schemaBox.SelectedIndex = 1;

			validateButton.Enabled = false;

			browser.Filter = "XHTML Files(*.htm;*.html;*.xml)|*.htm;*.html;*.xml";
			browser.Multiselect = false;

			// build up title text
			Assembly x = Assembly.GetExecutingAssembly();
			String v = x.GetName().Version.ToString();
			about.Text += v;

			char[] separator = {'.'};
			String[] bits = v.Split(separator);
			int days = Int32.Parse(bits[2]);
			DateTime d = new DateTime(2000, 1, 1);
			d = d.AddDays(days);
			about.Text += " ("+d.ToLongDateString()+")";
		}

		/// <summary>
		/// Clean up any resources being used.
		/// </summary>
		protected override void Dispose( bool disposing )
		{
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
			System.Resources.ResourceManager resources = new System.Resources.ResourceManager(typeof(ValidatorForm));
			this.label1 = new System.Windows.Forms.Label();
			this.schemaBox = new System.Windows.Forms.ComboBox();
			this.label2 = new System.Windows.Forms.Label();
			this.fileBox = new System.Windows.Forms.TextBox();
			this.fileButton = new System.Windows.Forms.Button();
			this.resultBox = new System.Windows.Forms.RichTextBox();
			this.validateButton = new System.Windows.Forms.Button();
			this.browser = new System.Windows.Forms.OpenFileDialog();
			this.about = new System.Windows.Forms.Label();
			this.SuspendLayout();
			// 
			// label1
			// 
			this.label1.Location = new System.Drawing.Point(8, 32);
			this.label1.Name = "label1";
			this.label1.Size = new System.Drawing.Size(96, 16);
			this.label1.TabIndex = 0;
			this.label1.Text = "XHTML Schema:";
			this.label1.FlatStyle = FlatStyle.System;
			// 
			// schemaBox
			// 
			this.schemaBox.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
			this.schemaBox.Location = new System.Drawing.Point(104, 30);
			this.schemaBox.Name = "schemaBox";
			this.schemaBox.Size = new System.Drawing.Size(120, 21);
			this.schemaBox.TabIndex = 1;
			// 
			// label2
			// 
			this.label2.Location = new System.Drawing.Point(232, 32);
			this.label2.Name = "label2";
			this.label2.Size = new System.Drawing.Size(24, 16);
			this.label2.TabIndex = 2;
			this.label2.Text = "File:";
			this.label2.FlatStyle = FlatStyle.System;
			// 
			// fileBox
			// 
			this.fileBox.Location = new System.Drawing.Point(256, 30);
			this.fileBox.Name = "fileBox";
			this.fileBox.Size = new System.Drawing.Size(224, 20);
			this.fileBox.TabIndex = 3;
			this.fileBox.Text = "";
			this.fileBox.TextChanged += new System.EventHandler(this.fileBox_TextChanged);
			// 
			// fileButton
			// 
			this.fileButton.Location = new System.Drawing.Point(488, 28);
			this.fileButton.Name = "fileButton";
			this.fileButton.Size = new System.Drawing.Size(64, 24);
			this.fileButton.TabIndex = 4;
			this.fileButton.Text = "Browse...";
			this.fileButton.Click += new System.EventHandler(this.fileButton_Click);
			this.fileButton.FlatStyle = FlatStyle.System;
			// 
			// resultBox
			// 
			this.resultBox.Location = new System.Drawing.Point(8, 56);
			this.resultBox.Name = "resultBox";
			this.resultBox.ReadOnly = true;
			this.resultBox.Size = new System.Drawing.Size(624, 344);
			this.resultBox.TabIndex = 4;
			this.resultBox.Text = "";
			// 
			// validateButton
			// 
			this.validateButton.Location = new System.Drawing.Point(560, 28);
			this.validateButton.Name = "validateButton";
			this.validateButton.Size = new System.Drawing.Size(72, 24);
			this.validateButton.TabIndex = 6;
			this.validateButton.Text = "Validate";
			this.validateButton.Click += new System.EventHandler(this.validateButton_Click);
			this.validateButton.FlatStyle = FlatStyle.System;
			// 
			// about
			// 
			this.about.Location = new System.Drawing.Point(8, 8);
			this.about.Name = "about";
			this.about.Size = new System.Drawing.Size(608, 16);
			this.about.TabIndex = 7;
			this.about.Text = "Public domain application by Mr. Tines <tines@ravnaandtines.com>  Version ";
			this.about.FlatStyle = FlatStyle.System;
			// 
			// ValidatorForm
			// 
			this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
			this.ClientSize = new System.Drawing.Size(640, 406);
			this.Controls.AddRange(new System.Windows.Forms.Control[] {
																		  this.about,
																		  this.validateButton,
																		  this.resultBox,
																		  this.fileButton,
																		  this.fileBox,
																		  this.label2,
																		  this.schemaBox,
																		  this.label1});
			this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
			this.Name = "ValidatorForm";
			this.Text = "XHTML Validator";
			this.ResumeLayout(false);

		}
		#endregion

		/// <summary>
		/// The main entry point for the application.
		/// </summary>
		[STAThread]
		static void Main() 
		{
			Application.Run(new ValidatorForm());
		}

		// allow validation for real files only
		private void fileBox_TextChanged(object sender, System.EventArgs e)
		{
			try 
			{
				validateButton.Enabled = File.Exists(fileBox.Text);
			} 
			catch (Exception) 
			{
				validateButton.Enabled = false;
			}
		}

		// browse for files
		private void fileButton_Click(object sender, System.EventArgs e)
		{
			if(DialogResult.OK == browser.ShowDialog(this))
			{
				fileBox.Text = browser.FileName;
				validateButton_Click(sender, e);
			}
		}

		// pass file to DTD validating reader
		private void validateButton_Click(object sender, System.EventArgs e)
		{
			resultBox.Text = "";
			XmlValidatingReader vr = null;
			try 
			{
				XmlTextReader tr = Selected == 2 ? new XXmlTextReader(fileBox.Text)
					: new XmlTextReader(fileBox.Text);
				tr.XmlResolver = new LocalResolver(this);
				vr = new XmlValidatingReader(tr);

				vr.ValidationType = ValidationType.DTD;
				vr.ValidationEventHandler += new ValidationEventHandler (ValidationHandler);

				while(vr.Read())
				{
					if(vr.NodeType == XmlNodeType.Text)
						cp1252Check(vr);
				}

				if(resultBox.Text.Length == 0)
					resultBox.Text += "Success!\r\n";
				resultBox.Text += "\r\n"+fileBox.Text+" - Validation finished\r\n";
				tr.Close();
			}
			catch (Exception ex)
			{
				resultBox.Text += "\r\n"+ex.Message+"\r\n"+ex.GetType().ToString()+"\r\n";
				resultBox.Text += ex.StackTrace;
				vr.Close();
			}
		}



		private void cp1252Check(XmlValidatingReader vr)
		{
			string suspect = vr.Value;
			bool clean = true;
			string message = "";
			for(int i=0;i<suspect.Length; ++i)
			{
				if(suspect[i]<0x80 || suspect[i] >= 0xA0)
					continue;
				clean = false;
				message += "Non-SGML character detected; use "+unicode[suspect[i]-0x80]+" instead\r\n";
			}
			if(!clean)
			{
				IXmlLineInfo info = (IXmlLineInfo) vr;
				message += "in text at line "+info.LineNumber+" position "+info.LinePosition+"\r\n";
				resultBox.Text += "***Validation error\r\n";
				resultBox.Text += "\tSeverity:Error\r\n";
				resultBox.Text += message;
			}
		}

		private static string [] unicode = {
		"&euro;","?","&sbquo;","&fnof;",
		"&bdquo;","&hellip;","&dagger;","&Dagger;",
		"&circ;","&permil;","&Scaron;","&lsaquo;",
		"&OElig;","?","&#x017d;","?",
		"?","&lsquo;","&rsquo;","&ldquo;",
		"&rdquo;","&bull;","&ndash;","&mdash;",
		"&tilde;","&trade;","&scaron;","&rsaquo;",
		"&oelig;","?","&#x017e;","&Yuml;" };



		public void ValidationHandler(object sender, ValidationEventArgs args)
		{
			resultBox.Text += "***Validation error\r\n";
			resultBox.Text += "\tSeverity:"+args.Severity.ToString()+"\r\n";
			String line = args.Message.ToString();
			line = line.Replace(". ",".\r\n\t\t");
			resultBox.Text += "\tMessage  :"+ line+"\r\n";
		}

		public int Selected
		{
			get
			{
				return schemaBox.SelectedIndex;
			}
		}
	}


	public class LocalResolver : XmlResolver
	{
		private ValidatorForm form = null;
		public LocalResolver(ValidatorForm f)
		{
			form = f;
			baseUri = new Uri("file://./dtd/junk.dtd");
		}
		private Uri baseUri;
		System.Net.ICredentials cred;
		public override System.Net.ICredentials Credentials
		{
			set{ cred = value;}
		}

		override public object GetEntity(Uri absoluteUri, string role, Type ofObjectToReturn)
		{
			if(null == absoluteUri)
				MessageBox.Show("Null URI","Validator");

			Assembly x = Assembly.GetExecutingAssembly();
			FileInfo here = new FileInfo(x.Location.ToString());
			bool flag = false;

			String path = "";
			if(absoluteUri.Scheme != Uri.UriSchemeFile)
			{
				String[] n = absoluteUri.Segments;
				path = n[n.Length-1];
				flag = true;
				path = here.DirectoryName+"\\dtd\\"+path;
			}
			else
				path = absoluteUri.LocalPath;

			FileInfo info = new FileInfo(path);

			String file = path.ToLower();

			if(!file.EndsWith(".dtd"))
			{
				if(flag && !File.Exists(path))
				{
					MessageBox.Show("File not found - "+path,"Validator");
				}
				return new FileStream(path,FileMode.Open,FileAccess.Read,FileShare.Read);
			}


			switch(form.Selected)
			{
				case 0:
					return new FileStream(here.DirectoryName+"\\dtd\\xhtml1-transitional.dtd",
						FileMode.Open,FileAccess.Read,FileShare.Read);

				case 1:
					return new FileStream(here.DirectoryName+"\\dtd\\xhtml1-strict.dtd",
						FileMode.Open,FileAccess.Read,FileShare.Read);
				case 2:
					return new FileStream(here.DirectoryName+"\\dtd\\xhtml11.dtd",
						FileMode.Open,FileAccess.Read,FileShare.Read);
				default:
					break;
			}


			return null;
		}
		
		public override Uri ResolveUri(Uri baseUri, String relativeUri)
		{
			while(null == baseUri)
			{
				baseUri = new Uri("file://./dtd/junk.dtd");
			}
			return new Uri(baseUri,relativeUri);
		}
	}

	// work around problem that .NET has with an attribute name
	// that begins with "x"; used for full XHTML 1.1 validator
	public class XXmlTextReader : XmlTextReader
	{
		public XXmlTextReader(String s) : base(s)
		{
		}

		public override bool Read()
		{
			bool v = base.Read();
			return v;
		}

		// Swap the cases around (so an Xmlns in the
		// XHTML will fail to validate, but an xmlns will
		// be correctly matched aginst the mutilated DTD)
		public override string LocalName
		{
			get
			{
				if(base.LocalName.Equals("xmlns"))
					return "Xmlns";
				else if(base.LocalName.Equals("Xmlns"))
					return "xmlns";
				else
					return base.LocalName;
			}
		}

	}
}
