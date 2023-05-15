/*
 * $Id: Form1.cs,v 1.12 2003/06/17 19:37:07 Steve Exp $
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
 * Revision 1.12  2003/06/17 19:37:07  Steve
 * Multi-file scanning - including bad HTML
 *
 * Revision 1.11  2003/06/17 19:30:01  Steve
 * Multi-file scanning - including bad HTML
 *
 * Revision 1.10  2003/05/19 19:43:42  Steve
 * Echo doctype
 *
 * Revision 1.9  2003/05/18 10:20:59  Steve
 * Polish
 *
 * Revision 1.8  2003/05/17 20:58:14  Steve
 * Add CSS level2 support
 *
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
using Microbion.SHBrowseFolders;

namespace Validator
{
	/// <summary>
	/// Validator UI.
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
		private System.Windows.Forms.Button folderButton;
		private System.Windows.Forms.CheckBox recurseCheck;
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

			// Fill in UI items
			schemaBox.Items.Add("1.0 Transitional");
			schemaBox.Items.Add("1.0 Strict");
			schemaBox.Items.Add("1.1");
			schemaBox.Items.Add("By document");
			schemaBox.SelectedIndex = 3;

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
			this.folderButton = new System.Windows.Forms.Button();
			this.recurseCheck = new System.Windows.Forms.CheckBox();
			this.SuspendLayout();
			// 
			// label1
			// 
			this.label1.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.label1.Location = new System.Drawing.Point(8, 32);
			this.label1.Name = "label1";
			this.label1.Size = new System.Drawing.Size(96, 16);
			this.label1.TabIndex = 0;
			this.label1.Text = "XHTML Schema:";
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
			this.label2.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.label2.Location = new System.Drawing.Point(232, 32);
			this.label2.Name = "label2";
			this.label2.Size = new System.Drawing.Size(24, 16);
			this.label2.TabIndex = 2;
			this.label2.Text = "File:";
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
			this.fileButton.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.fileButton.Location = new System.Drawing.Point(488, 28);
			this.fileButton.Name = "fileButton";
			this.fileButton.Size = new System.Drawing.Size(64, 24);
			this.fileButton.TabIndex = 4;
			this.fileButton.Text = "Browse...";
			this.fileButton.Click += new System.EventHandler(this.fileButton_Click);
			// 
			// resultBox
			// 
			this.resultBox.Anchor = (((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
				| System.Windows.Forms.AnchorStyles.Left) 
				| System.Windows.Forms.AnchorStyles.Right);
			this.resultBox.Location = new System.Drawing.Point(8, 56);
			this.resultBox.Name = "resultBox";
			this.resultBox.ReadOnly = true;
			this.resultBox.Size = new System.Drawing.Size(624, 320);
			this.resultBox.TabIndex = 4;
			this.resultBox.Text = "";
			// 
			// validateButton
			// 
			this.validateButton.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.validateButton.Location = new System.Drawing.Point(560, 28);
			this.validateButton.Name = "validateButton";
			this.validateButton.Size = new System.Drawing.Size(72, 24);
			this.validateButton.TabIndex = 6;
			this.validateButton.Text = "Validate";
			this.validateButton.Click += new System.EventHandler(this.validateButton_Click);
			// 
			// about
			// 
			this.about.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.about.Location = new System.Drawing.Point(8, 8);
			this.about.Name = "about";
			this.about.Size = new System.Drawing.Size(608, 16);
			this.about.TabIndex = 7;
			this.about.Text = "Public domain application by Mr. Tines <tines@ravnaandtines.com>  Version ";
			// 
			// folderButton
			// 
			this.folderButton.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.folderButton.Location = new System.Drawing.Point(8, 380);
			this.folderButton.Name = "folderButton";
			this.folderButton.Size = new System.Drawing.Size(120, 24);
			this.folderButton.TabIndex = 8;
			this.folderButton.Text = "Validate Folder...";
			this.folderButton.Click += new System.EventHandler(this.folderButton_Click);
			// 
			// recurseCheck
			// 
			this.recurseCheck.Location = new System.Drawing.Point(144, 380);
			this.recurseCheck.Name = "recurseCheck";
			this.recurseCheck.Size = new System.Drawing.Size(144, 24);
			this.recurseCheck.TabIndex = 9;
			this.recurseCheck.Text = "check sub-folders";
			// 
			// ValidatorForm
			// 
			this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
			this.ClientSize = new System.Drawing.Size(640, 406);
			this.Controls.AddRange(new System.Windows.Forms.Control[] {
																		  this.recurseCheck,
																		  this.folderButton,
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

		/// <summary>
		/// allow validation for real files only
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
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

		/// <summary>
		/// browse for files
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void fileButton_Click(object sender, System.EventArgs e)
		{
			if(DialogResult.OK == browser.ShowDialog(this))
			{
				fileBox.Text = browser.FileName;
				validateButton_Click(sender, e);
			}
		}
		bool allClear = true;

		/// <summary>
		/// pass file to DTD validating reader
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void validateButton_Click(object sender, System.EventArgs e)
		{
			resultBox.Text = "";
			allClear = true;
			XmlValidatingReader vr = null;
			try 
			{
				XmlTextReader tr = Selected > 1 ? new XXmlTextReader(fileBox.Text)
					: new XmlTextReader(fileBox.Text);
				tr.XmlResolver = new LocalResolver(this);
				vr = new XmlValidatingReader(tr);

				vr.ValidationType = ValidationType.DTD;
				vr.ValidationEventHandler += new ValidationEventHandler (ValidationHandler);

				while(vr.Read())
				{
					if(vr.NodeType == XmlNodeType.Text)
						cp1252Check(vr);
					else if(vr.NodeType == XmlNodeType.DocumentType)
					{
						for(int i=0; i<vr.AttributeCount; ++i)
							resultBox.Text += vr.GetAttribute(i)+"\r\n";
						resultBox.Text += "\r\n";
					}
				}

				if(allClear)
					checkStyles(fileBox.Text);

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

		/// <summary>
		/// check CSS validity
		/// </summary>
		private void checkStyles(string file)
		{
			XmlTextReader tr = Selected > 1 ? new XXmlTextReader(file)
				: new XmlTextReader(file);
			tr.XmlResolver = new LocalResolver(this);

			org.w3c.css.css.StyleSheetCom style = new org.w3c.css.css.StyleSheetCom(file);
			resultBox.Text += style.xmlRequest(tr);
			tr.Close();
		}

		/// <summary>
		/// Look out for sneaky invalid Windows derived character values
		/// </summary>
		/// <param name="vr"></param>
		private void cp1252Check(XmlValidatingReader vr)
		{
			string suspect = vr.Value;
			bool clean = true;
			string message = "";
			for(int i=0;i<suspect.Length; ++i)
			{
				if(suspect[i]<0x80 || suspect[i] >= 0xA0)
					continue;
				allClear = clean = false;
				message += "Non-SGML character \""+codepoints[suspect[i]-0x80]+ 
					"\" (maybe in the form \"&#"+((int)suspect[i])+";\" or \"&#x"+((int)suspect[i]).ToString("x")+
					";\" ) detected; use "+unicode[suspect[i]-0x80]+" instead\r\n";
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

		/// <summary>
		/// Substitutions for cp1252
		/// </summary>
		public static System.Char[] codepoints = {
			(char)0x20AC, '?'/*0x0081*/, (char)0x201A, (char) 0x0192, 
			(char)0x201E, (char)0x2026, (char)0x2020, (char)0x2021, 
			(char)0x02C6, (char)0x2030, (char)0x0160, (char)0x2039, 
			(char)0x0152, '?'/*0x008D*/, (char)0x017D, '?'/*0x008F*/, 
			'?'/*0x0090*/, (char)0x2018, (char)0x2019, (char)0x201C, 
			(char)0x201D, (char)0x2022, (char)0x2013, (char)0x2014, 
			(char)0x02DC, (char)0x2122, (char)0x0161, (char)0x203A, 
			(char)0x0153, '?'/*0x009D*/, (char)0x017E, (char)0x0178
		};


		/// <summary>
		/// Substitutions for cp1252
		/// </summary>
		private static string [] unicode = {
		"&euro;","?","&sbquo;","&fnof;",
		"&bdquo;","&hellip;","&dagger;","&Dagger;",
		"&circ;","&permil;","&Scaron;","&lsaquo;",
		"&OElig;","?","&#x017d;","?",
		"?","&lsquo;","&rsquo;","&ldquo;",
		"&rdquo;","&bull;","&ndash;","&mdash;",
		"&tilde;","&trade;","&scaron;","&rsaquo;",
		"&oelig;","?","&#x017e;","&Yuml;" };


		/// <summary>
		/// If something happens when validating, log it
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="args"></param>
		public void ValidationHandler(object sender, ValidationEventArgs args)
		{
			resultBox.Text += "***Validation error\r\n";
			resultBox.Text += "\tSeverity:"+args.Severity.ToString()+"\r\n";
			String line = args.Message.ToString();
			line = line.Replace(". ",".\r\n\t\t");
			resultBox.Text += "\tMessage  :"+ line+"\r\n";
			allClear = false;
		}

		private void folderButton_Click(object sender, System.EventArgs e)
		{
			SHBrowser br = new  SHBrowser();
			string folder = br.DoBrowse(this.Text, 0, this.Handle);
			if(null == folder || 0 == folder.Length)
				return;
			int z = folder.IndexOf((char)0);
			folder = folder.Substring(0, z);

			DirectoryInfo info = new DirectoryInfo(folder);
			resultBox.Text = "";

			this.Cursor = Cursors.WaitCursor;

			FileInfo[] members = info.GetFiles();
			foreach(FileInfo f in members) 
			{
				process(f);
			}

			if(recurseCheck.Checked)
			{
				descend(info);
			}

			this.Cursor = Cursors.Default;
		}

		private void descend(DirectoryInfo info)
		{
			DirectoryInfo[] dirs = info.GetDirectories();
			foreach(DirectoryInfo dir in dirs)
			{
				FileInfo[] members = dir.GetFiles();
				foreach(FileInfo f in members) 
				{
					process(f);
				}
				descend(dir);
			}
		}

		private void process(FileInfo f)
		{
			string ext = f.Extension.ToLower();
			if(ext != ".htm" && ext != ".html" && ext != ".xml")
				return;

			//resultBox.AppendText(f.FullName+"\r\n");
			allClear = true;

			XmlValidatingReader vr = null;
			try 
			{
				XmlTextReader tr = Selected > 1 ? new XXmlTextReader(f.FullName)
					: new XmlTextReader(f.FullName);
				tr.XmlResolver = new LocalResolver(this);
				vr = new XmlValidatingReader(tr);

				vr.ValidationType = ValidationType.DTD;
				vr.ValidationEventHandler += new ValidationEventHandler (ValidationHandler);

				while(vr.Read())
				{
					if(vr.NodeType == XmlNodeType.Text)
						cp1252Check(vr);
						/*
					else if(vr.NodeType == XmlNodeType.DocumentType)
					{
						for(int i=0; i<vr.AttributeCount; ++i)
							resultBox.Text += vr.GetAttribute(i)+"\r\n";
						resultBox.Text += "\r\n";
					}
						*/
				}

				if(allClear)
					checkStyles(f.FullName);

				resultBox.Text += f.FullName+" - Validation finished\r\n\r\n";
				tr.Close();
			}
			catch (Exception ex)
			{
				resultBox.AppendText(f.FullName+"\r\n");
				resultBox.Text += ex.Message+"\r\n"+ex.GetType().ToString()+"\r\n";
				resultBox.Text += ex.StackTrace+"\r\n\r\n";
				resultBox.Text += f.FullName+" - Validation failed\r\n\r\n";
				vr.Close();
			}


		}

		/// <summary>
		/// DTD of choice
		/// </summary>
		public int Selected
		{
			get
			{
				return schemaBox.SelectedIndex;
			}
		}
	}

	/// <summary>
	/// Looks for file references locally
	/// </summary>
	public class LocalResolver : XmlResolver
	{
		private ValidatorForm form = null;
		/// <summary>
		/// Tie it up to the UI selection of DTD
		/// </summary>
		/// <param name="f"></param>
		public LocalResolver(ValidatorForm f)
		{
			form = f;
			baseUri = new Uri("file://./dtd/junk.dtd");
		}
		private Uri baseUri;
		System.Net.ICredentials cred;
		/// <summary>
		/// 
		/// </summary>
		public override System.Net.ICredentials Credentials
		{
			set{ cred = value;}
		}
		/// <summary>
		/// 
		/// </summary>
		/// <param name="absoluteUri"></param>
		/// <param name="role"></param>
		/// <param name="ofObjectToReturn"></param>
		/// <returns></returns>
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


			XXmlTextReader.swap = false;
			switch(form.Selected)
			{
				case 0:
					return new FileStream(here.DirectoryName+"\\dtd\\xhtml1-transitional.dtd",
						FileMode.Open,FileAccess.Read,FileShare.Read);

				case 1:
					return new FileStream(here.DirectoryName+"\\dtd\\xhtml1-strict.dtd",
						FileMode.Open,FileAccess.Read,FileShare.Read);
				case 2:
					XXmlTextReader.swap = true;
					return new FileStream(here.DirectoryName+"\\dtd\\xhtml11.dtd",
						FileMode.Open,FileAccess.Read,FileShare.Read);

				case 3:
					string test = here.DirectoryName+"\\dtd\\xhtml11.dtd";
					XXmlTextReader.swap = file.Equals(test.ToLower());
					if(File.Exists(file))
						return new FileStream(file,
							FileMode.Open,FileAccess.Read,FileShare.Read);
					return new FileStream(here.DirectoryName+"\\dtd\\xhtml1-transitional.dtd",
						FileMode.Open,FileAccess.Read,FileShare.Read);

				default:
					break;
			}


			return null;
		}
		/// <summary>
		/// 
		/// </summary>
		/// <param name="baseUri"></param>
		/// <param name="relativeUri"></param>
		/// <returns></returns>
		public override Uri ResolveUri(Uri baseUri, String relativeUri)
		{
			while(null == baseUri)
			{
				baseUri = new Uri("file://./dtd/junk.dtd");
			}
			return new Uri(baseUri,relativeUri);
		}
	}

	/// <summary>
	/// work around problem that .NET has with an attribute name
	/// that begins with "x"; used for full XHTML 1.1 validator
	/// </summary>
	public class XXmlTextReader : XmlTextReader
	{
		public static bool swap = false;

		public XXmlTextReader(String s) : base(s)
		{
			swap = true;
		}

		public override bool Read()
		{
			bool v = base.Read();
			return v;
		}

		/// <summary>
		/// Swap the cases around (so an Xmlns in the XHTML will fail to 
		/// validate, but an xmlns will be correctly matched aginst the mutilated DTD)
		/// </summary>
		public override string LocalName
		{
			get
			{
				if(!swap)
					return base.LocalName;
				else if(base.LocalName.Equals("xmlns"))
					return "Xmlns";
				else if(base.LocalName.Equals("Xmlns"))
					return "xmlns";
				else
					return base.LocalName;
			}
		}

	}
}
