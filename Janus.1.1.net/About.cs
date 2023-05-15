using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;
using System.Reflection;

namespace com.ravnaandtines.janus
{
	/// <summary>
	/// Summary description for About.
	/// </summary>
	public class About : System.Windows.Forms.Form
	{
		private System.Windows.Forms.TabControl tabControl1;
		private System.Windows.Forms.TabPage tabPage1;
		private System.Windows.Forms.TabPage tabPage2;
		private System.Windows.Forms.Button button1;
		private System.Windows.Forms.RichTextBox richTextBox1;
		private System.Windows.Forms.PictureBox pictureBox1;
		private System.Windows.Forms.Panel panel1;
		private System.Windows.Forms.Label label2;
		private System.Windows.Forms.Label label3;
		private System.Windows.Forms.Label label1;
		private System.Windows.Forms.Label versionLabel;
		/// <summary>
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.Container components = null;

		public About()
		{
			//
			// Required for Windows Form Designer support
			//
			InitializeComponent();

			//
			// TODO: Add any constructor code after InitializeComponent call
			//
			richTextBox1.Text = GNU_General_Public_Licence();
			versionLabel.Text += Assembly.GetExecutingAssembly().GetName().Version.ToString();
		}

		private static String NL = "\r\n";

		public static String GNU_General_Public_Licence()
		{
				return
			  "GNU General Public License" +NL+
			  "Copyright (C) 1989, 1991 Free Software Foundation, Inc.  675" +NL+
			  "Mass Ave, Cambridge, MA 02139, USA" +NL
			  +NL+
			  "TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION" +NL
			  +NL+
			  "0. This License applies to any program or other work which contains a notice " +NL+
			  "placed by the copyright holder saying it may be distributed under the terms " +NL+
			  "of this General Public License. The \"Program\", below, refers to any such " +NL+
			  "program or work, and a \"work based on the Program\" means either the Program " +NL+
			  "or any derivative work under copyright law: that is to say, a work " +NL+
			  "containing the Program or a portion of it, either verbatim or with " +NL+
			  "modifications and/or translated into another language. (Hereinafter, " +NL+
			  "translation is included without limitation in the term \"modification\".) Each " +NL+
			  "licensee is addressed as \"you\"" +NL
			  +NL+
			  "Activities other than copying, distribution and modification are not covered " +NL+
			  "by this License; they are outside its scope. The act of running the Program " +NL+
			  "is not restricted, and the output from the Program is covered only if its " +NL+
			  "contents constitute a work based on the Program (independent of having been " +NL+
			  "made by running the Program). Whether that is true depends on what the " +NL+
			  "Program does." +NL
			  +NL+
			  "1. You may copy and distribute verbatim copies of the Program's source code " +NL+
			  "as you receive it, in any medium, provided that you conspicuously and " +NL+
			  "appropriately publish on each copy an appropriate copyright notice and " +NL+
			  "disclaimer of warranty; keep intact all the notices that refer to this " +NL+
			  "License and to the absence of any warranty; and give any other recipients of " +NL+
			  "the Program a copy of this License along with the Program." +NL
			  +NL+
			  "You may charge a fee for the physical act of transferring a copy, and you " +NL+
			  "may at your option offer warranty protection in exchange for a fee." +NL
			  +NL+
			  "2. You may modify your copy or copies of the Program or any portion of it, " +NL+
			  "thus forming a work based on the Program, and copy and distribute such " +NL+
			  "modifications or work under the terms of Section 1 above, provided that you " +NL+
			  "also meet all of these conditions:" +NL
			  +NL+
			  "        o a) You must cause the modified files to carry prominent notices" +NL+
			  "          stating that you changed the files and the date of any change." +NL
			  +NL+
			  "        o b) You must cause any work that you distribute or publish, that in" +NL+
			  "          whole or in part contains or is derived from the Program or any" +NL+
			  "          part thereof, to be licensed as a whole at no charge to all third" +NL+
			  "          parties under the terms of this License." +NL
			  +NL+
			  "        o c) If the modified program normally reads commands interactively" +NL+
			  "          when run, you must cause it, when started running for such" +NL+
			  "          interactive use in the most ordinary way, to print or display an" +NL+
			  "          announcement including an appropriate copyright notice and a" +NL+
			  "          notice that there is no warranty (or else, saying that you provide" +NL+
			  "          a warranty) and that users may redistribute the program under" +NL+
			  "          these conditions, and telling the user how to view a copy of this" +NL+
			  "          License. (Exception: if the Program itself is interactive but does" +NL+
			  "          not normally print such an announcement, your work based on the" +NL+
			  "          Program is not required to print an announcement.)" +NL
			  +NL+
			  "These requirements apply to the modified work as a whole. If identifiable " +NL+
			  "sections of that work are not derived from the Program, and can be " +NL+
			  "reasonably considered independent and separate works in themselves, then " +NL+
			  "this License, and its terms, do not apply to those sections when you " +NL+
			  "distribute them as separate works. But when you distribute the same sections " +NL+
			  "as part of a whole which is a work based on the Program, the distribution of " +NL+
			  "the whole must be on the terms of this License, whose permissions for other " +NL+
			  "licensees extend to the entire whole, and thus to each and every part " +NL+
			  "regardless of who wrote it." +NL
			  +NL+
			  "Thus, it is not the intent of this section to claim rights or contest your " +NL+
			  "rights to work written entirely by you; rather, the intent is to exercise " +NL+
			  "the right to control the distribution of derivative or collective works " +NL+
			  "based on the Program." +NL
			  +NL+
			  "In addition, mere aggregation of another work not based on the Program with " +NL+
			  "the Program (or with a work based on the Program) on a volume of a storage " +NL+
			  "or distribution medium does not bring the other work under the scope of this " +NL+
			  "License." +NL
			  +NL+
			  "3. You may copy and distribute the Program (or a work based on it, under " +NL+
			  "Section 2) in object code or executable form under the terms of Sections 1 " +NL+
			  "and 2 above provided that you also do one of the following:" +NL
			  +NL+
			  "        o a) Accompany it with the complete corresponding machine-readable" +NL+
			  "          source code, which must be distributed under the terms of Sections" +NL+
			  "          1 and 2 above on a medium customarily used for software" +NL+
			  "          interchange; or," +NL
			  +NL+
			  "        o b) Accompany it with a written offer, valid for at least three" +NL+
			  "          years, to give any third party, for a charge no more than your" +NL+
			  "          cost of physically performing source distribution, a complete" +NL+
			  "          machine-readable copy of the corresponding source code, to be" +NL+
			  "          distributed under the terms of Sections 1 and 2 above on a medium" +NL+
			  "          customarily used for software interchange; or," +NL
			  +NL+
			  "        o c) Accompany it with the information you received as to the offer" +NL+
			  "          to distribute corresponding source code. (This alternative is" +NL+
			  "          allowed only for noncommercial distribution and only if you" +NL+
			  "          received the program in object code or executable form with such" +NL+
			  "          an offer, in accord with Subsection b above.)" +NL
			  +NL+
			  "The source code for a work means the preferred form of the work for making " +NL+
			  "modifications to it. For an executable work, complete source code means all " +NL+
			  "the source code for all modules it contains, plus any associated interface " +NL+
			  "definition files, plus the scripts used to control compilation and " +NL+
			  "installation of the executable. However, as a special exception, the source " +NL+
			  "code distributed need not include anything that is normally distributed (in " +NL+
			  "either source or binary form) with the major components (compiler, kernel, " +NL+
			  "and so on) of the operating system on which the executable runs, unless that " +NL+
			  "component itself accompanies the executable." +NL
			  +NL+
			  "If distribution of executable or object code is made by offering access to " +NL+
			  "copy from a designated place, then offering equivalent access to copy the " +NL+
			  "source code from the same place counts as distribution of the source code, " +NL+
			  "even though third parties are not compelled to copy the source along with " +NL+
			  "the object code." +NL
			  +NL+
			  "4. You may not copy, modify, sublicense, or distribute the Program except as " +NL+
			  "expressly provided under this License. Any attempt otherwise to copy, " +NL+
			  "modify, sublicense or distribute the Program is void, and will automatically " +NL+
			  "terminate your rights under this License. However, parties who have received " +NL+
			  "copies, or rights, from you under this License will not have their licenses " +NL+
			  "terminated so long as such parties remain in full compliance." +NL
			  +NL+
			  "5. You are not required to accept this License, since you have not signed " +NL+
			  "it. However, nothing else grants you permission to modify or distribute the " +NL+
			  "Program or its derivative works. These actions are prohibited by law if you " +NL+
			  "do not accept this License. Therefore, by modifying or distributing the " +NL+
			  "Program (or any work based on the Program), you indicate your acceptance of " +NL+
			  "this License to do so, and all its terms and conditions for copying, " +NL+
			  "distributing or modifying the Program or works based on it." +NL
			  +NL+
			  "6. Each time you redistribute the Program (or any work based on the " +NL+
			  "Program), the recipient automatically receives a license from the original " +NL+
			  "licensor to copy, distribute or modify the Program subject to these terms " +NL+
			  "and conditions. You may not impose any further restrictions on the " +NL+
			  "recipients' exercise of the rights granted herein. You are not responsible " +NL+
			  "for enforcing compliance by third parties to this License." +NL
			  +NL+
			  "7. If, as a consequence of a court judgment or allegation of patent " +NL+
			  "infringement or for any other reason (not limited to patent issues), " +NL+
			  "conditions are imposed on you (whether by court order, agreement or " +NL+
			  "otherwise) that contradict the conditions of this License, they do not " +NL+
			  "excuse you from the conditions of this License. If you cannot distribute so " +NL+
			  "as to satisfy simultaneously your obligations under this License and any " +NL+
			  "other pertinent obligations, then as a consequence you may not distribute " +NL+
			  "the Program at all. For example, if a patent license would not permit " +NL+
			  "royalty-free redistribution of the Program by all those who receive copies " +NL+
			  "directly or indirectly through you, then the only way you could satisfy both " +NL+
			  "it and this License would be to refrain entirely from distribution of the " +NL+
			  "Program." +NL
			  +NL+
			  "If any portion of this section is held invalid or unenforceable under any " +NL+
			  "particular circumstance, the balance of the section is intended to apply and " +NL+
			  "the section as a whole is intended to apply in other circumstances." +NL
			  +NL+
			  "It is not the purpose of this section to induce you to infringe any patents " +NL+
			  "or other property right claims or to contest validity of any such claims; " +NL+
			  "this section has the sole purpose of protecting the integrity of the free " +NL+
			  "software distribution system, which is implemented by public license " +NL+
			  "practices. Many people have made generous contributions to the wide range of " +NL+
			  "software distributed through that system in reliance on consistent " +NL+
			  "application of that system; it is up to the author/donor to decide if he or " +NL+
			  "she is willing to distribute software through any other system and a " +NL+
			  "licensee cannot impose that choice." +NL
			  +NL+
			  "This section is intended to make thoroughly clear what is believed to be a " +NL+
			  "consequence of the rest of this License." +NL
			  +NL+
			  "8. If the distribution and/or use of the Program is restricted in certain " +NL+
			  "countries either by patents or by copyrighted interfaces, the original " +NL+
			  "copyright holder who places the Program under this License may add an " +NL+
			  "explicit geographical distribution limitation excluding those countries, so " +NL+
			  "that distribution is permitted only in or among countries not thus excluded. " +NL+
			  "In such case, this License incorporates the limitation as if written in the " +NL+
			  "body of this License." +NL
			  +NL+
			  "9. The Free Software Foundation may publish revised and/or new versions of " +NL+
			  "the General Public License from time to time. Such new versions will be " +NL+
			  "similar in spirit to the present version, but may differ in detail to " +NL+
			  "address new problems or concerns." +NL
			  +NL+
			  "Each version is given a distinguishing version number. If the Program " +NL+
			  "specifies a version number of this License which applies to it and \"any " +NL+
			  "later version\", you have the option of following the terms and conditions " +NL+
			  "either of that version or of any later version published by the Free " +NL+
			  "Software Foundation. If the Program does not specify a version number of " +NL+
			  "this License, you may choose any version ever published by the Free Software " +NL+
			  "Foundation." +NL
			  +NL+
			  "10. If you wish to incorporate parts of the Program into other free programs " +NL+
			  "whose distribution conditions are different, write to the author to ask for " +NL+
			  "permission. For software which is copyrighted by the Free Software " +NL+
			  "Foundation, write to the Free Software Foundation; we sometimes make " +NL+
			  "exceptions for this. Our decision will be guided by the two goals of "+NL+
			  "preserving the free status of all derivatives of our free software and of " +NL+
			  "promoting the sharing and reuse of software generally." +NL
			  +NL+
			  "NO WARRANTY" +NL
			  +NL+
			  "11. BECAUSE THE PROGRAM IS LICENSED FREE OF CHARGE, THERE IS NO WARRANTY FOR " +NL+
			  "THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN " +NL+
			  "OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES " +NL+
			  "PROVIDE THE PROGRAM \"AS IS\" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED " +NL+
			  "OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF " +NL+
			  "MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO " +NL+
			  "THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM " +NL+
			  "PROVE DEFECTIVE, YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR " +NL+
			  "CORRECTION." +NL
			  +NL+
			  "12. IN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW OR AGREED TO IN WRITING " +NL+
			  "WILL ANY COPYRIGHT HOLDER, OR ANY OTHER PARTY WHO MAY MODIFY AND/OR " +NL+
			  "REDISTRIBUTE THE PROGRAM AS PERMITTED ABOVE, BE LIABLE TO YOU FOR DAMAGES, " +NL+
			  "INCLUDING ANY GENERAL, SPECIAL, INCIDENTAL OR CONSEQUENTIAL DAMAGES ARISING " +NL+
			  "OUT OF THE USE OR INABILITY TO USE THE PROGRAM (INCLUDING BUT NOT LIMITED TO " +NL+
			  "LOSS OF DATA OR DATA BEING RENDERED INACCURATE OR LOSSES SUSTAINED BY YOU OR " +NL+
			  "THIRD PARTIES OR A FAILURE OF THE PROGRAM TO OPERATE WITH ANY OTHER " +NL+
			  "PROGRAMS), EVEN IF SUCH HOLDER OR OTHER PARTY HAS BEEN ADVISED OF THE " +NL+
			  "POSSIBILITY OF SUCH DAMAGES." +NL;
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
			System.Resources.ResourceManager resources = new System.Resources.ResourceManager(typeof(About));
			this.tabControl1 = new System.Windows.Forms.TabControl();
			this.tabPage1 = new System.Windows.Forms.TabPage();
			this.versionLabel = new System.Windows.Forms.Label();
			this.label3 = new System.Windows.Forms.Label();
			this.label2 = new System.Windows.Forms.Label();
			this.panel1 = new System.Windows.Forms.Panel();
			this.label1 = new System.Windows.Forms.Label();
			this.pictureBox1 = new System.Windows.Forms.PictureBox();
			this.tabPage2 = new System.Windows.Forms.TabPage();
			this.richTextBox1 = new System.Windows.Forms.RichTextBox();
			this.button1 = new System.Windows.Forms.Button();
			this.tabControl1.SuspendLayout();
			this.tabPage1.SuspendLayout();
			this.tabPage2.SuspendLayout();
			this.SuspendLayout();
			// 
			// tabControl1
			// 
			this.tabControl1.AccessibleDescription = ((string)(resources.GetObject("tabControl1.AccessibleDescription")));
			this.tabControl1.AccessibleName = ((string)(resources.GetObject("tabControl1.AccessibleName")));
			this.tabControl1.Alignment = ((System.Windows.Forms.TabAlignment)(resources.GetObject("tabControl1.Alignment")));
			this.tabControl1.Anchor = ((System.Windows.Forms.AnchorStyles)(resources.GetObject("tabControl1.Anchor")));
			this.tabControl1.Appearance = ((System.Windows.Forms.TabAppearance)(resources.GetObject("tabControl1.Appearance")));
			this.tabControl1.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("tabControl1.BackgroundImage")));
			this.tabControl1.Controls.AddRange(new System.Windows.Forms.Control[] {
																					  this.tabPage1,
																					  this.tabPage2});
			this.tabControl1.Dock = ((System.Windows.Forms.DockStyle)(resources.GetObject("tabControl1.Dock")));
			this.tabControl1.Enabled = ((bool)(resources.GetObject("tabControl1.Enabled")));
			this.tabControl1.Font = ((System.Drawing.Font)(resources.GetObject("tabControl1.Font")));
			this.tabControl1.ImeMode = ((System.Windows.Forms.ImeMode)(resources.GetObject("tabControl1.ImeMode")));
			this.tabControl1.ItemSize = ((System.Drawing.Size)(resources.GetObject("tabControl1.ItemSize")));
			this.tabControl1.Location = ((System.Drawing.Point)(resources.GetObject("tabControl1.Location")));
			this.tabControl1.Name = "tabControl1";
			this.tabControl1.Padding = ((System.Drawing.Point)(resources.GetObject("tabControl1.Padding")));
			this.tabControl1.RightToLeft = ((System.Windows.Forms.RightToLeft)(resources.GetObject("tabControl1.RightToLeft")));
			this.tabControl1.SelectedIndex = 0;
			this.tabControl1.ShowToolTips = ((bool)(resources.GetObject("tabControl1.ShowToolTips")));
			this.tabControl1.Size = ((System.Drawing.Size)(resources.GetObject("tabControl1.Size")));
			this.tabControl1.TabIndex = ((int)(resources.GetObject("tabControl1.TabIndex")));
			this.tabControl1.Text = resources.GetString("tabControl1.Text");
			this.tabControl1.Visible = ((bool)(resources.GetObject("tabControl1.Visible")));
			// 
			// tabPage1
			// 
			this.tabPage1.AccessibleDescription = ((string)(resources.GetObject("tabPage1.AccessibleDescription")));
			this.tabPage1.AccessibleName = ((string)(resources.GetObject("tabPage1.AccessibleName")));
			this.tabPage1.Anchor = ((System.Windows.Forms.AnchorStyles)(resources.GetObject("tabPage1.Anchor")));
			this.tabPage1.AutoScroll = ((bool)(resources.GetObject("tabPage1.AutoScroll")));
			this.tabPage1.AutoScrollMargin = ((System.Drawing.Size)(resources.GetObject("tabPage1.AutoScrollMargin")));
			this.tabPage1.AutoScrollMinSize = ((System.Drawing.Size)(resources.GetObject("tabPage1.AutoScrollMinSize")));
			this.tabPage1.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("tabPage1.BackgroundImage")));
			this.tabPage1.Controls.AddRange(new System.Windows.Forms.Control[] {
																				   this.versionLabel,
																				   this.label3,
																				   this.label2,
																				   this.panel1,
																				   this.label1,
																				   this.pictureBox1});
			this.tabPage1.Dock = ((System.Windows.Forms.DockStyle)(resources.GetObject("tabPage1.Dock")));
			this.tabPage1.Enabled = ((bool)(resources.GetObject("tabPage1.Enabled")));
			this.tabPage1.Font = ((System.Drawing.Font)(resources.GetObject("tabPage1.Font")));
			this.tabPage1.ImageIndex = ((int)(resources.GetObject("tabPage1.ImageIndex")));
			this.tabPage1.ImeMode = ((System.Windows.Forms.ImeMode)(resources.GetObject("tabPage1.ImeMode")));
			this.tabPage1.Location = ((System.Drawing.Point)(resources.GetObject("tabPage1.Location")));
			this.tabPage1.Name = "tabPage1";
			this.tabPage1.RightToLeft = ((System.Windows.Forms.RightToLeft)(resources.GetObject("tabPage1.RightToLeft")));
			this.tabPage1.Size = ((System.Drawing.Size)(resources.GetObject("tabPage1.Size")));
			this.tabPage1.TabIndex = ((int)(resources.GetObject("tabPage1.TabIndex")));
			this.tabPage1.Text = resources.GetString("tabPage1.Text");
			this.tabPage1.ToolTipText = resources.GetString("tabPage1.ToolTipText");
			this.tabPage1.Visible = ((bool)(resources.GetObject("tabPage1.Visible")));
			// 
			// versionLabel
			// 
			this.versionLabel.AccessibleDescription = ((string)(resources.GetObject("versionLabel.AccessibleDescription")));
			this.versionLabel.AccessibleName = ((string)(resources.GetObject("versionLabel.AccessibleName")));
			this.versionLabel.Anchor = ((System.Windows.Forms.AnchorStyles)(resources.GetObject("versionLabel.Anchor")));
			this.versionLabel.AutoSize = ((bool)(resources.GetObject("versionLabel.AutoSize")));
			this.versionLabel.Dock = ((System.Windows.Forms.DockStyle)(resources.GetObject("versionLabel.Dock")));
			this.versionLabel.Enabled = ((bool)(resources.GetObject("versionLabel.Enabled")));
			this.versionLabel.Font = ((System.Drawing.Font)(resources.GetObject("versionLabel.Font")));
			this.versionLabel.Image = ((System.Drawing.Image)(resources.GetObject("versionLabel.Image")));
			this.versionLabel.ImageAlign = ((System.Drawing.ContentAlignment)(resources.GetObject("versionLabel.ImageAlign")));
			this.versionLabel.ImageIndex = ((int)(resources.GetObject("versionLabel.ImageIndex")));
			this.versionLabel.ImeMode = ((System.Windows.Forms.ImeMode)(resources.GetObject("versionLabel.ImeMode")));
			this.versionLabel.Location = ((System.Drawing.Point)(resources.GetObject("versionLabel.Location")));
			this.versionLabel.Name = "versionLabel";
			this.versionLabel.RightToLeft = ((System.Windows.Forms.RightToLeft)(resources.GetObject("versionLabel.RightToLeft")));
			this.versionLabel.Size = ((System.Drawing.Size)(resources.GetObject("versionLabel.Size")));
			this.versionLabel.TabIndex = ((int)(resources.GetObject("versionLabel.TabIndex")));
			this.versionLabel.Text = resources.GetString("versionLabel.Text");
			this.versionLabel.TextAlign = ((System.Drawing.ContentAlignment)(resources.GetObject("versionLabel.TextAlign")));
			this.versionLabel.Visible = ((bool)(resources.GetObject("versionLabel.Visible")));
			// 
			// label3
			// 
			this.label3.AccessibleDescription = ((string)(resources.GetObject("label3.AccessibleDescription")));
			this.label3.AccessibleName = ((string)(resources.GetObject("label3.AccessibleName")));
			this.label3.Anchor = ((System.Windows.Forms.AnchorStyles)(resources.GetObject("label3.Anchor")));
			this.label3.AutoSize = ((bool)(resources.GetObject("label3.AutoSize")));
			this.label3.Dock = ((System.Windows.Forms.DockStyle)(resources.GetObject("label3.Dock")));
			this.label3.Enabled = ((bool)(resources.GetObject("label3.Enabled")));
			this.label3.Font = ((System.Drawing.Font)(resources.GetObject("label3.Font")));
			this.label3.Image = ((System.Drawing.Image)(resources.GetObject("label3.Image")));
			this.label3.ImageAlign = ((System.Drawing.ContentAlignment)(resources.GetObject("label3.ImageAlign")));
			this.label3.ImageIndex = ((int)(resources.GetObject("label3.ImageIndex")));
			this.label3.ImeMode = ((System.Windows.Forms.ImeMode)(resources.GetObject("label3.ImeMode")));
			this.label3.Location = ((System.Drawing.Point)(resources.GetObject("label3.Location")));
			this.label3.Name = "label3";
			this.label3.RightToLeft = ((System.Windows.Forms.RightToLeft)(resources.GetObject("label3.RightToLeft")));
			this.label3.Size = ((System.Drawing.Size)(resources.GetObject("label3.Size")));
			this.label3.TabIndex = ((int)(resources.GetObject("label3.TabIndex")));
			this.label3.Text = resources.GetString("label3.Text");
			this.label3.TextAlign = ((System.Drawing.ContentAlignment)(resources.GetObject("label3.TextAlign")));
			this.label3.Visible = ((bool)(resources.GetObject("label3.Visible")));
			// 
			// label2
			// 
			this.label2.AccessibleDescription = ((string)(resources.GetObject("label2.AccessibleDescription")));
			this.label2.AccessibleName = ((string)(resources.GetObject("label2.AccessibleName")));
			this.label2.Anchor = ((System.Windows.Forms.AnchorStyles)(resources.GetObject("label2.Anchor")));
			this.label2.AutoSize = ((bool)(resources.GetObject("label2.AutoSize")));
			this.label2.Dock = ((System.Windows.Forms.DockStyle)(resources.GetObject("label2.Dock")));
			this.label2.Enabled = ((bool)(resources.GetObject("label2.Enabled")));
			this.label2.Font = ((System.Drawing.Font)(resources.GetObject("label2.Font")));
			this.label2.Image = ((System.Drawing.Image)(resources.GetObject("label2.Image")));
			this.label2.ImageAlign = ((System.Drawing.ContentAlignment)(resources.GetObject("label2.ImageAlign")));
			this.label2.ImageIndex = ((int)(resources.GetObject("label2.ImageIndex")));
			this.label2.ImeMode = ((System.Windows.Forms.ImeMode)(resources.GetObject("label2.ImeMode")));
			this.label2.Location = ((System.Drawing.Point)(resources.GetObject("label2.Location")));
			this.label2.Name = "label2";
			this.label2.RightToLeft = ((System.Windows.Forms.RightToLeft)(resources.GetObject("label2.RightToLeft")));
			this.label2.Size = ((System.Drawing.Size)(resources.GetObject("label2.Size")));
			this.label2.TabIndex = ((int)(resources.GetObject("label2.TabIndex")));
			this.label2.Text = resources.GetString("label2.Text");
			this.label2.TextAlign = ((System.Drawing.ContentAlignment)(resources.GetObject("label2.TextAlign")));
			this.label2.Visible = ((bool)(resources.GetObject("label2.Visible")));
			// 
			// panel1
			// 
			this.panel1.AccessibleDescription = ((string)(resources.GetObject("panel1.AccessibleDescription")));
			this.panel1.AccessibleName = ((string)(resources.GetObject("panel1.AccessibleName")));
			this.panel1.Anchor = ((System.Windows.Forms.AnchorStyles)(resources.GetObject("panel1.Anchor")));
			this.panel1.AutoScroll = ((bool)(resources.GetObject("panel1.AutoScroll")));
			this.panel1.AutoScrollMargin = ((System.Drawing.Size)(resources.GetObject("panel1.AutoScrollMargin")));
			this.panel1.AutoScrollMinSize = ((System.Drawing.Size)(resources.GetObject("panel1.AutoScrollMinSize")));
			this.panel1.BackColor = System.Drawing.SystemColors.ControlText;
			this.panel1.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("panel1.BackgroundImage")));
			this.panel1.Dock = ((System.Windows.Forms.DockStyle)(resources.GetObject("panel1.Dock")));
			this.panel1.Enabled = ((bool)(resources.GetObject("panel1.Enabled")));
			this.panel1.Font = ((System.Drawing.Font)(resources.GetObject("panel1.Font")));
			this.panel1.ImeMode = ((System.Windows.Forms.ImeMode)(resources.GetObject("panel1.ImeMode")));
			this.panel1.Location = ((System.Drawing.Point)(resources.GetObject("panel1.Location")));
			this.panel1.Name = "panel1";
			this.panel1.RightToLeft = ((System.Windows.Forms.RightToLeft)(resources.GetObject("panel1.RightToLeft")));
			this.panel1.Size = ((System.Drawing.Size)(resources.GetObject("panel1.Size")));
			this.panel1.TabIndex = ((int)(resources.GetObject("panel1.TabIndex")));
			this.panel1.Text = resources.GetString("panel1.Text");
			this.panel1.Visible = ((bool)(resources.GetObject("panel1.Visible")));
			// 
			// label1
			// 
			this.label1.AccessibleDescription = ((string)(resources.GetObject("label1.AccessibleDescription")));
			this.label1.AccessibleName = ((string)(resources.GetObject("label1.AccessibleName")));
			this.label1.Anchor = ((System.Windows.Forms.AnchorStyles)(resources.GetObject("label1.Anchor")));
			this.label1.AutoSize = ((bool)(resources.GetObject("label1.AutoSize")));
			this.label1.Dock = ((System.Windows.Forms.DockStyle)(resources.GetObject("label1.Dock")));
			this.label1.Enabled = ((bool)(resources.GetObject("label1.Enabled")));
			this.label1.Font = ((System.Drawing.Font)(resources.GetObject("label1.Font")));
			this.label1.Image = ((System.Drawing.Image)(resources.GetObject("label1.Image")));
			this.label1.ImageAlign = ((System.Drawing.ContentAlignment)(resources.GetObject("label1.ImageAlign")));
			this.label1.ImageIndex = ((int)(resources.GetObject("label1.ImageIndex")));
			this.label1.ImeMode = ((System.Windows.Forms.ImeMode)(resources.GetObject("label1.ImeMode")));
			this.label1.Location = ((System.Drawing.Point)(resources.GetObject("label1.Location")));
			this.label1.Name = "label1";
			this.label1.RightToLeft = ((System.Windows.Forms.RightToLeft)(resources.GetObject("label1.RightToLeft")));
			this.label1.Size = ((System.Drawing.Size)(resources.GetObject("label1.Size")));
			this.label1.TabIndex = ((int)(resources.GetObject("label1.TabIndex")));
			this.label1.Text = resources.GetString("label1.Text");
			this.label1.TextAlign = ((System.Drawing.ContentAlignment)(resources.GetObject("label1.TextAlign")));
			this.label1.Visible = ((bool)(resources.GetObject("label1.Visible")));
			// 
			// pictureBox1
			// 
			this.pictureBox1.AccessibleDescription = ((string)(resources.GetObject("pictureBox1.AccessibleDescription")));
			this.pictureBox1.AccessibleName = ((string)(resources.GetObject("pictureBox1.AccessibleName")));
			this.pictureBox1.Anchor = ((System.Windows.Forms.AnchorStyles)(resources.GetObject("pictureBox1.Anchor")));
			this.pictureBox1.BackgroundImage = ((System.Drawing.Bitmap)(resources.GetObject("pictureBox1.BackgroundImage")));
			this.pictureBox1.Dock = ((System.Windows.Forms.DockStyle)(resources.GetObject("pictureBox1.Dock")));
			this.pictureBox1.Enabled = ((bool)(resources.GetObject("pictureBox1.Enabled")));
			this.pictureBox1.Font = ((System.Drawing.Font)(resources.GetObject("pictureBox1.Font")));
			this.pictureBox1.Image = ((System.Drawing.Image)(resources.GetObject("pictureBox1.Image")));
			this.pictureBox1.ImeMode = ((System.Windows.Forms.ImeMode)(resources.GetObject("pictureBox1.ImeMode")));
			this.pictureBox1.Location = ((System.Drawing.Point)(resources.GetObject("pictureBox1.Location")));
			this.pictureBox1.Name = "pictureBox1";
			this.pictureBox1.RightToLeft = ((System.Windows.Forms.RightToLeft)(resources.GetObject("pictureBox1.RightToLeft")));
			this.pictureBox1.Size = ((System.Drawing.Size)(resources.GetObject("pictureBox1.Size")));
			this.pictureBox1.SizeMode = ((System.Windows.Forms.PictureBoxSizeMode)(resources.GetObject("pictureBox1.SizeMode")));
			this.pictureBox1.TabIndex = ((int)(resources.GetObject("pictureBox1.TabIndex")));
			this.pictureBox1.TabStop = false;
			this.pictureBox1.Text = resources.GetString("pictureBox1.Text");
			this.pictureBox1.Visible = ((bool)(resources.GetObject("pictureBox1.Visible")));
			// 
			// tabPage2
			// 
			this.tabPage2.AccessibleDescription = ((string)(resources.GetObject("tabPage2.AccessibleDescription")));
			this.tabPage2.AccessibleName = ((string)(resources.GetObject("tabPage2.AccessibleName")));
			this.tabPage2.Anchor = ((System.Windows.Forms.AnchorStyles)(resources.GetObject("tabPage2.Anchor")));
			this.tabPage2.AutoScroll = ((bool)(resources.GetObject("tabPage2.AutoScroll")));
			this.tabPage2.AutoScrollMargin = ((System.Drawing.Size)(resources.GetObject("tabPage2.AutoScrollMargin")));
			this.tabPage2.AutoScrollMinSize = ((System.Drawing.Size)(resources.GetObject("tabPage2.AutoScrollMinSize")));
			this.tabPage2.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("tabPage2.BackgroundImage")));
			this.tabPage2.Controls.AddRange(new System.Windows.Forms.Control[] {
																				   this.richTextBox1});
			this.tabPage2.Dock = ((System.Windows.Forms.DockStyle)(resources.GetObject("tabPage2.Dock")));
			this.tabPage2.Enabled = ((bool)(resources.GetObject("tabPage2.Enabled")));
			this.tabPage2.Font = ((System.Drawing.Font)(resources.GetObject("tabPage2.Font")));
			this.tabPage2.ImageIndex = ((int)(resources.GetObject("tabPage2.ImageIndex")));
			this.tabPage2.ImeMode = ((System.Windows.Forms.ImeMode)(resources.GetObject("tabPage2.ImeMode")));
			this.tabPage2.Location = ((System.Drawing.Point)(resources.GetObject("tabPage2.Location")));
			this.tabPage2.Name = "tabPage2";
			this.tabPage2.RightToLeft = ((System.Windows.Forms.RightToLeft)(resources.GetObject("tabPage2.RightToLeft")));
			this.tabPage2.Size = ((System.Drawing.Size)(resources.GetObject("tabPage2.Size")));
			this.tabPage2.TabIndex = ((int)(resources.GetObject("tabPage2.TabIndex")));
			this.tabPage2.Text = resources.GetString("tabPage2.Text");
			this.tabPage2.ToolTipText = resources.GetString("tabPage2.ToolTipText");
			this.tabPage2.Visible = ((bool)(resources.GetObject("tabPage2.Visible")));
			// 
			// richTextBox1
			// 
			this.richTextBox1.AccessibleDescription = ((string)(resources.GetObject("richTextBox1.AccessibleDescription")));
			this.richTextBox1.AccessibleName = ((string)(resources.GetObject("richTextBox1.AccessibleName")));
			this.richTextBox1.Anchor = ((System.Windows.Forms.AnchorStyles)(resources.GetObject("richTextBox1.Anchor")));
			this.richTextBox1.AutoSize = ((bool)(resources.GetObject("richTextBox1.AutoSize")));
			this.richTextBox1.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("richTextBox1.BackgroundImage")));
			this.richTextBox1.BulletIndent = ((int)(resources.GetObject("richTextBox1.BulletIndent")));
			this.richTextBox1.Dock = ((System.Windows.Forms.DockStyle)(resources.GetObject("richTextBox1.Dock")));
			this.richTextBox1.Enabled = ((bool)(resources.GetObject("richTextBox1.Enabled")));
			this.richTextBox1.Font = ((System.Drawing.Font)(resources.GetObject("richTextBox1.Font")));
			this.richTextBox1.ImeMode = ((System.Windows.Forms.ImeMode)(resources.GetObject("richTextBox1.ImeMode")));
			this.richTextBox1.Location = ((System.Drawing.Point)(resources.GetObject("richTextBox1.Location")));
			this.richTextBox1.MaxLength = ((int)(resources.GetObject("richTextBox1.MaxLength")));
			this.richTextBox1.Multiline = ((bool)(resources.GetObject("richTextBox1.Multiline")));
			this.richTextBox1.Name = "richTextBox1";
			this.richTextBox1.ReadOnly = true;
			this.richTextBox1.RightMargin = ((int)(resources.GetObject("richTextBox1.RightMargin")));
			this.richTextBox1.RightToLeft = ((System.Windows.Forms.RightToLeft)(resources.GetObject("richTextBox1.RightToLeft")));
			this.richTextBox1.ScrollBars = ((System.Windows.Forms.RichTextBoxScrollBars)(resources.GetObject("richTextBox1.ScrollBars")));
			this.richTextBox1.Size = ((System.Drawing.Size)(resources.GetObject("richTextBox1.Size")));
			this.richTextBox1.TabIndex = ((int)(resources.GetObject("richTextBox1.TabIndex")));
			this.richTextBox1.Text = resources.GetString("richTextBox1.Text");
			this.richTextBox1.Visible = ((bool)(resources.GetObject("richTextBox1.Visible")));
			this.richTextBox1.WordWrap = ((bool)(resources.GetObject("richTextBox1.WordWrap")));
			this.richTextBox1.ZoomFactor = ((System.Single)(resources.GetObject("richTextBox1.ZoomFactor")));
			// 
			// button1
			// 
			this.button1.AccessibleDescription = ((string)(resources.GetObject("button1.AccessibleDescription")));
			this.button1.AccessibleName = ((string)(resources.GetObject("button1.AccessibleName")));
			this.button1.Anchor = ((System.Windows.Forms.AnchorStyles)(resources.GetObject("button1.Anchor")));
			this.button1.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("button1.BackgroundImage")));
			this.button1.DialogResult = System.Windows.Forms.DialogResult.Cancel;
			this.button1.Dock = ((System.Windows.Forms.DockStyle)(resources.GetObject("button1.Dock")));
			this.button1.Enabled = ((bool)(resources.GetObject("button1.Enabled")));
			this.button1.FlatStyle = ((System.Windows.Forms.FlatStyle)(resources.GetObject("button1.FlatStyle")));
			this.button1.Font = ((System.Drawing.Font)(resources.GetObject("button1.Font")));
			this.button1.Image = ((System.Drawing.Image)(resources.GetObject("button1.Image")));
			this.button1.ImageAlign = ((System.Drawing.ContentAlignment)(resources.GetObject("button1.ImageAlign")));
			this.button1.ImageIndex = ((int)(resources.GetObject("button1.ImageIndex")));
			this.button1.ImeMode = ((System.Windows.Forms.ImeMode)(resources.GetObject("button1.ImeMode")));
			this.button1.Location = ((System.Drawing.Point)(resources.GetObject("button1.Location")));
			this.button1.Name = "button1";
			this.button1.RightToLeft = ((System.Windows.Forms.RightToLeft)(resources.GetObject("button1.RightToLeft")));
			this.button1.Size = ((System.Drawing.Size)(resources.GetObject("button1.Size")));
			this.button1.TabIndex = ((int)(resources.GetObject("button1.TabIndex")));
			this.button1.Text = resources.GetString("button1.Text");
			this.button1.TextAlign = ((System.Drawing.ContentAlignment)(resources.GetObject("button1.TextAlign")));
			this.button1.Visible = ((bool)(resources.GetObject("button1.Visible")));
			// 
			// About
			// 
			this.AcceptButton = this.button1;
			this.AccessibleDescription = ((string)(resources.GetObject("$this.AccessibleDescription")));
			this.AccessibleName = ((string)(resources.GetObject("$this.AccessibleName")));
			this.Anchor = ((System.Windows.Forms.AnchorStyles)(resources.GetObject("$this.Anchor")));
			this.AutoScaleBaseSize = ((System.Drawing.Size)(resources.GetObject("$this.AutoScaleBaseSize")));
			this.AutoScroll = ((bool)(resources.GetObject("$this.AutoScroll")));
			this.AutoScrollMargin = ((System.Drawing.Size)(resources.GetObject("$this.AutoScrollMargin")));
			this.AutoScrollMinSize = ((System.Drawing.Size)(resources.GetObject("$this.AutoScrollMinSize")));
			this.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("$this.BackgroundImage")));
			this.CancelButton = this.button1;
			this.ClientSize = ((System.Drawing.Size)(resources.GetObject("$this.ClientSize")));
			this.ControlBox = false;
			this.Controls.AddRange(new System.Windows.Forms.Control[] {
																		  this.button1,
																		  this.tabControl1});
			this.Dock = ((System.Windows.Forms.DockStyle)(resources.GetObject("$this.Dock")));
			this.Enabled = ((bool)(resources.GetObject("$this.Enabled")));
			this.Font = ((System.Drawing.Font)(resources.GetObject("$this.Font")));
			this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
			this.ImeMode = ((System.Windows.Forms.ImeMode)(resources.GetObject("$this.ImeMode")));
			this.Location = ((System.Drawing.Point)(resources.GetObject("$this.Location")));
			this.MaximizeBox = false;
			this.MaximumSize = ((System.Drawing.Size)(resources.GetObject("$this.MaximumSize")));
			this.MinimizeBox = false;
			this.MinimumSize = ((System.Drawing.Size)(resources.GetObject("$this.MinimumSize")));
			this.Name = "About";
			this.RightToLeft = ((System.Windows.Forms.RightToLeft)(resources.GetObject("$this.RightToLeft")));
			this.SizeGripStyle = System.Windows.Forms.SizeGripStyle.Hide;
			this.StartPosition = ((System.Windows.Forms.FormStartPosition)(resources.GetObject("$this.StartPosition")));
			this.Text = resources.GetString("$this.Text");
			this.Visible = ((bool)(resources.GetObject("$this.Visible")));
			this.tabControl1.ResumeLayout(false);
			this.tabPage1.ResumeLayout(false);
			this.tabPage2.ResumeLayout(false);
			this.ResumeLayout(false);

		}
		#endregion

	}
}
