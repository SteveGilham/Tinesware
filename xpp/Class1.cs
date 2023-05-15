using System;
using System.Collections;
using System.IO;
using System.Xml;

namespace xpp
{
	/// <summary>
	/// Summary description for Class1.
	/// </summary>
	class Class1
	{
		static FileInfo baseFile = null;

		/// <summary>
		/// The main entry point for the application.
		/// </summary>
		[STAThread]
		static void Main(string[] args)
		{
			if(args.Length != 3)
			{
				Console.WriteLine("Usage: xpp \"directives\" in-filename out-location");
				return;
			}
			FileInfo from = new FileInfo(args[1]);
			baseFile = from;
			FileInfo to = new FileInfo(args[2]);
			Console.WriteLine (to.FullName);
			if(Directory.Exists(to.FullName))
			{
				to = new FileInfo(args[2]+'/'+from.Name);
				Console.WriteLine("Creating file "+to.FullName);
			}

			String [] tokens = args[0].Split(space);
			Hashtable parts = new Hashtable();
			int i=0;
			for(i=0; i<tokens.Length; ++i)
			{
				parts.Add(tokens[i],tokens[i]);
			}

			StreamReader r = new StreamReader(from.FullName);
			XmlDocument doc = new XmlDocument();
			doc.XmlResolver = null;

			String s = r.ReadToEnd();
			s = s.Replace("&","&amp;");
			doc.PreserveWhitespace=true;
			doc.LoadXml(s);
			scan(doc, parts);

			Console.WriteLine(doc.DocumentType.OuterXml);

			s = doc.InnerXml;

			i = s.IndexOf("[]");

			s = s.Substring(0,i)+s.Substring(i+2);
			s = s.Replace(" xmlns=\"\"", "");

			StreamWriter w = new StreamWriter(to.FullName);
			w.Write(s.Replace("&amp;","&"));
			w.Close();
		}
		static char[] space = {' '};

		static void scan(XmlNode node, Hashtable parts)
		{
			XmlNode deep;
			for(deep=node.FirstChild; deep != null; )
			{
				if(deep.Attributes != null && deep.Attributes.GetNamedItem("class") != null)
				{
					XmlNode classAttr = deep.Attributes.GetNamedItem("class");
					Console.WriteLine("class = " + classAttr.Value);
					if(parts.ContainsKey("+"+classAttr.Value))
					{
						Console.WriteLine("removing attribute...");
						deep.Attributes.RemoveNamedItem("class");
					}
					else if(parts.ContainsKey("-"+classAttr.Value))
					{
						Console.WriteLine("removing node...");
						XmlNode next = deep.NextSibling;
						node.RemoveChild(deep);
						deep = next;
						continue;
					}
					else
					{
						String [] tokens = classAttr.Value.Split(space);
						if(parts.ContainsKey(tokens[0]) && tokens.Length>1)
						{
							Console.WriteLine("inserting XML...");
							String name = "";
							for(int i=1; i<tokens.Length; ++i)
							{
								if(i>1) name+='/';
								name += tokens[i];
								Console.WriteLine(tokens[i]);
							}
							XmlElement elt = (XmlElement) deep;
							Console.WriteLine(elt.InnerText);
							FileInfo f = new FileInfo(baseFile.DirectoryName + '/' + name);
							Console.WriteLine(f.FullName);

							StreamReader r = new StreamReader(f.FullName);
							deep.InnerXml = r.ReadToEnd();
							deep.Attributes.RemoveNamedItem("class");
						}
						else if (parts.ContainsKey("*"+tokens[0]) && tokens.Length>1)
						{
							Console.WriteLine("replacing XML...");
							String name = "";
							for(int i=1; i<tokens.Length; ++i)
							{
								if(i>1) name+='/';
								name += tokens[i];
								Console.WriteLine(tokens[i]);
							}
							XmlElement elt = (XmlElement) deep;
							Console.WriteLine(elt.OuterXml);
							FileInfo f = new FileInfo(baseFile.DirectoryName + '/' + name);
							Console.WriteLine(f.FullName);

							StreamReader r = new StreamReader(f.FullName);
							XmlDocumentFragment frag = deep.OwnerDocument.CreateDocumentFragment();
							frag.InnerXml = r.ReadToEnd();
							XmlNode parent = deep.ParentNode;
							XmlNode insertAt = deep;
							while(frag.FirstChild != null)
							{
								insertAt = parent.InsertAfter(frag.FirstChild, insertAt);
							}
							insertAt = deep;
							deep = deep.NextSibling;
							parent.RemoveChild(insertAt);

							continue;
						}
					}
				}
				scan(deep, parts);
				deep=deep.NextSibling;
			}
		}
	}
}
