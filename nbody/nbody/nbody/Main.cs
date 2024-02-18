//package UK.co.demon.windsong.tines.util;

import java.applet.*;
import java.awt.*;

/**
* Template for applets that compute indefinitely
* Pushes applet working into a separate thread.
* This is something that Applet ought really to
* have done directly in the language!
* Also offers a template main program for direct application
* use of the same code, plus standard licensing blurb.
*
* Copyright Mr. Tines <tines@windsong.demon.co.uk>
*  All rights reserved.  For full licence details see GNU_General_Public_Licence()
*
* This distribution is free software; if it contains a class which has a main() 
* method with signature "public static main(String[] args)", or a class which
* extends java.applet.Applet and is referenced from an associated HTML file (the Main
* class), you can redistribute it and/or modify it under the terms of the GNU General 
* Public License version 2 as published by the Free Software Foundation.
*
* If the supplied distribution contains no such Main class, it is ipso facto
* a library and is covered by the GNU Library General Public Licence version 2.
*
* If there is a Main class, you may extract classes under the terms of the GNU 
* Library General Public Licence where those classes are not marked as either being 
* explicitly public domain (in which case that code remains public domain, and you
* may do with it whatever you wish) or where the effect of the copying is to retain 
* interoperability with the program from which you are deriving (in which case the 
* copying falls under the standard GNU General Public License).
*
* The Main class file will contain specific notices for the program
* as a whole which amend these default conditions.  
*
* The full text of the GNU General Public Licence is included in the
* source of (and returned by) the function GNU_General_Public_Licence() listed below.
*
* The full text of the GNU Library General Public Licence is included in the
* source of (and returned by) the function GNU_Library_General_Public_Licence() listed below.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details. 
*
*
* @author Mr. Tines (after numerous texts)
*/

public class Main extends Applet implements Runnable, Paged, Actor
{
	Thread appletDynamic;

   /**
   * Main initialization
   * @see Applet#init
   */

   public void init()
   {
   }

   /**
   * Main (re-)start; thread is
   * fired up at this point.
   */
   public void start()
   {
   	if(null == appletDynamic)
      {
      	appletDynamic = new Thread(this);
         appletDynamic.start();
      }
   }

   /**
   * Main graceful suppression (iconise,
   * leave page or whatever).  Called before
   * destroy()
   */
   public void stop()
   {
   	if(null != appletDynamic)
      {
      	//appletDynamic.stop();
         appletDynamic = null;
      }
   }

   /**
   * Main final termination and tidy
   */
   public void destroy()
   {
   }

   /**
   * Output to screen
   * @param g Graphic to which to draw
   */
   public void paint(Graphics g)
   {
   }

   /**
   * work routine
   */
   public void run()
   {
   }

   /**
   * Applicationizer function; just write something like this
   * and call int "main"
   */
   public static void exampleMain(String [] args)
   {
      	Frame window;
      	Main self = new Main();
      	window = new Frame("Main program Frame");
		window.add("Center",self);
      	self.init();
		window.pack();
      	window.show();
      	self.mainloop();
      	self.stop();
      	self.destroy();
   }

   /**
   * equivalent to browser being up
   */
   ActionButton close;
   Dialog d;

   public void doIt(Component b)
   {
	if(b == close) d.hide();
   }

   public void mainloop()
   {
	d = new Dialog((Frame)getParent(),"License text", false);
	d.setLayout(new BorderLayout(5,5));

	TextPager p = new TextPager(this);
	d.add("Center",p);
	d.pack();
	close = new ActionButton("Close", this);
	d.add("South", close);
	d.show();

	TextArea t = new TextArea(GNU_General_Public_Licence(), 20, 60);
	add(t);
	
      for(;;)
      {
         try{Thread.sleep(1000);}
         catch(InterruptedException e) {return;}
      }
   }

   public String getPage(int i)
   {
	return GNU_General_Public_Licence(i);
   }
   public int maxPages() {return 13;}

   public static String GNU_General_Public_Licence()
   {
	return 
		GNU_General_Public_Licence(0) +
		GNU_General_Public_Licence(1) +
		GNU_General_Public_Licence(2) +
		GNU_General_Public_Licence(3) +
		GNU_General_Public_Licence(4) +
		GNU_General_Public_Licence(5) +
		GNU_General_Public_Licence(6) +
		GNU_General_Public_Licence(7) +
		GNU_General_Public_Licence(8) +
		GNU_General_Public_Licence(9) +
		GNU_General_Public_Licence(10) +
		GNU_General_Public_Licence(11) +
		GNU_General_Public_Licence(12);
   }

   public static String GNU_General_Public_Licence(int i)
  { 
	switch(i)
	{
	default:
	case 0: return
 "GNU General Public License\r\n"
+"Copyright (C) 1989, 1991 Free Software Foundation, Inc.  675\r\n"
+"Mass Ave, Cambridge, MA 02139, USA\r\n"
+"\r\n"
+"TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION\r\n"
+"\r\n"
+"0. This License applies to any program or other work which contains a notice\r\n"
+"placed by the copyright holder saying it may be distributed under the terms\r\n"
+"of this General Public License. The \"Program\", below, refers to any such\r\n"
+"program or work, and a \"work based on the Program\" means either the Program\r\n"
+"or any derivative work under copyright law: that is to say, a work\r\n"
+"containing the Program or a portion of it, either verbatim or with\r\n"
+"modifications and/or translated into another language. (Hereinafter,\r\n"
+"translation is included without limitation in the term \"modification\".) Each\r\n"
+"licensee is addressed as \"you\"\r\n"
+"\r\n"
+"Activities other than copying, distribution and modification are not covered\r\n"
+"by this License; they are outside its scope. The act of running the Program\r\n"
+"is not restricted, and the output from the Program is covered only if its\r\n"
+"contents constitute a work based on the Program (independent of having been\r\n"
+"made by running the Program). Whether that is true depends on what the\r\n"
+"Program does.\r\n"
+"\r\n";
	case 1: return
 "1. You may copy and distribute verbatim copies of the Program's source code\r\n"
+"as you receive it, in any medium, provided that you conspicuously and\r\n"
+"appropriately publish on each copy an appropriate copyright notice and\r\n"
+"disclaimer of warranty; keep intact all the notices that refer to this\r\n"
+"License and to the absence of any warranty; and give any other recipients of\r\n"
+"the Program a copy of this License along with the Program.\r\n"
+"\r\n"
+"You may charge a fee for the physical act of transferring a copy, and you\r\n"
+"may at your option offer warranty protection in exchange for a fee.\r\n"
+"\r\n";
	case 2: return
 "2. You may modify your copy or copies of the Program or any portion of it,\r\n"
+"thus forming a work based on the Program, and copy and distribute such\r\n"
+"modifications or work under the terms of Section 1 above, provided that you\r\n"
+"also meet all of these conditions:\r\n"
+"\r\n"
+"        o a) You must cause the modified files to carry prominent notices\r\n"
+"          stating that you changed the files and the date of any change.\r\n"
+"\r\n"
+"        o b) You must cause any work that you distribute or publish, that in\r\n"
+"          whole or in part contains or is derived from the Program or any\r\n"
+"          part thereof, to be licensed as a whole at no charge to all third\r\n"
+"          parties under the terms of this License.\r\n"
+"\r\n"
+"        o c) If the modified program normally reads commands interactively\r\n"
+"          when run, you must cause it, when started running for such\r\n"
+"          interactive use in the most ordinary way, to print or display an\r\n"
+"          announcement including an appropriate copyright notice and a\r\n"
+"          notice that there is no warranty (or else, saying that you provide\r\n"
+"          a warranty) and that users may redistribute the program under\r\n"
+"          these conditions, and telling the user how to view a copy of this\r\n"
+"          License. (Exception: if the Program itself is interactive but does\r\n"
+"          not normally print such an announcement, your work based on the\r\n"
+"          Program is not required to print an announcement.)\r\n"
+"\r\n"
+"These requirements apply to the modified work as a whole. If identifiable\r\n"
+"sections of that work are not derived from the Program, and can be\r\n"
+"reasonably considered independent and separate works in themselves, then\r\n"
+"this License, and its terms, do not apply to those sections when you\r\n"
+"distribute them as separate works. But when you distribute the same sections\r\n"
+"as part of a whole which is a work based on the Program, the distribution of\r\n"
+"the whole must be on the terms of this License, whose permissions for other\r\n"
+"licensees extend to the entire whole, and thus to each and every part\r\n"
+"regardless of who wrote it.\r\n"
+"\r\n"
+"Thus, it is not the intent of this section to claim rights or contest your\r\n"
+"rights to work written entirely by you; rather, the intent is to exercise\r\n"
+"the right to control the distribution of derivative or collective works\r\n"
+"based on the Program.\r\n"
+"\r\n"
+"In addition, mere aggregation of another work not based on the Program with\r\n"
+"the Program (or with a work based on the Program) on a volume of a storage\r\n"
+"or distribution medium does not bring the other work under the scope of this\r\n"
+"License.\r\n"
+"\r\n";
	case 3: return
 "3. You may copy and distribute the Program (or a work based on it, under\r\n"
+"Section 2) in object code or executable form under the terms of Sections 1\r\n"
+"and 2 above provided that you also do one of the following:\r\n"
+"\r\n"
+"        o a) Accompany it with the complete corresponding machine-readable\r\n"
+"          source code, which must be distributed under the terms of Sections\r\n"
+"          1 and 2 above on a medium customarily used for software\r\n"
+"          interchange; or,\r\n"
+"\r\n"
+"        o b) Accompany it with a written offer, valid for at least three\r\n"
+"          years, to give any third party, for a charge no more than your\r\n"
+"          cost of physically performing source distribution, a complete\r\n"
+"          machine-readable copy of the corresponding source code, to be\r\n"
+"          distributed under the terms of Sections 1 and 2 above on a medium\r\n"
+"          customarily used for software interchange; or,\r\n"
+"\r\n"
+"        o c) Accompany it with the information you received as to the offer\r\n"
+"          to distribute corresponding source code. (This alternative is\r\n"
+"          allowed only for noncommercial distribution and only if you\r\n"
+"          received the program in object code or executable form with such\r\n"
+"          an offer, in accord with Subsection b above.)\r\n"
+"\r\n"
+"The source code for a work means the preferred form of the work for making\r\n"
+"modifications to it. For an executable work, complete source code means all\r\n"
+"the source code for all modules it contains, plus any associated interface\r\n"
+"definition files, plus the scripts used to control compilation and\r\n"
+"installation of the executable. However, as a special exception, the source\r\n"
+"code distributed need not include anything that is normally distributed (in\r\n"
+"either source or binary form) with the major components (compiler, kernel,\r\n"
+"and so on) of the operating system on which the executable runs, unless that\r\n"
+"component itself accompanies the executable.\r\n"
+"\r\n"
+"If distribution of executable or object code is made by offering access to\r\n"
+"copy from a designated place, then offering equivalent access to copy the\r\n"
+"source code from the same place counts as distribution of the source code,\r\n"
+"even though third parties are not compelled to copy the source along with\r\n"
+"the object code.\r\n"
+"\r\n";
	case 4: return
 "4. You may not copy, modify, sublicense, or distribute the Program except as\r\n"
+"expressly provided under this License. Any attempt otherwise to copy,\r\n"
+"modify, sublicense or distribute the Program is void, and will automatically\r\n"
+"terminate your rights under this License. However, parties who have received\r\n"
+"copies, or rights, from you under this License will not have their licenses\r\n"
+"terminated so long as such parties remain in full compliance.\r\n"
+"\r\n";
	case 5: return
 "5. You are not required to accept this License, since you have not signed\r\n"
+"it. However, nothing else grants you permission to modify or distribute the\r\n"
+"Program or its derivative works. These actions are prohibited by law if you\r\n"
+"do not accept this License. Therefore, by modifying or distributing the\r\n"
+"Program (or any work based on the Program), you indicate your acceptance of\r\n"
+"this License to do so, and all its terms and conditions for copying,\r\n"
+"distributing or modifying the Program or works based on it.\r\n"
+"\r\n";
	case 6: return
 "6. Each time you redistribute the Program (or any work based on the\r\n"
+"Program), the recipient automatically receives a license from the original\r\n"
+"licensor to copy, distribute or modify the Program subject to these terms\r\n"
+"and conditions. You may not impose any further restrictions on the\r\n"
+"recipients' exercise of the rights granted herein. You are not responsible\r\n"
+"for enforcing compliance by third parties to this License.\r\n"
+"\r\n";
	case 7: return
 "7. If, as a consequence of a court judgment or allegation of patent\r\n"
+"infringement or for any other reason (not limited to patent issues),\r\n"
+"conditions are imposed on you (whether by court order, agreement or\r\n"
+"otherwise) that contradict the conditions of this License, they do not\r\n"
+"excuse you from the conditions of this License. If you cannot distribute so\r\n"
+"as to satisfy simultaneously your obligations under this License and any\r\n"
+"other pertinent obligations, then as a consequence you may not distribute\r\n"
+"the Program at all. For example, if a patent license would not permit\r\n"
+"royalty-free redistribution of the Program by all those who receive copies\r\n"
+"directly or indirectly through you, then the only way you could satisfy both\r\n"
+"it and this License would be to refrain entirely from distribution of the\r\n"
+"Program.\r\n"
+"\r\n"
+"If any portion of this section is held invalid or unenforceable under any\r\n"
+"particular circumstance, the balance of the section is intended to apply and\r\n"
+"the section as a whole is intended to apply in other circumstances.\r\n"
+"\r\n"
+"It is not the purpose of this section to induce you to infringe any patents\r\n"
+"or other property right claims or to contest validity of any such claims;\r\n"
+"this section has the sole purpose of protecting the integrity of the free\r\n"
+"software distribution system, which is implemented by public license\r\n"
+"practices. Many people have made generous contributions to the wide range of\r\n"
+"software distributed through that system in reliance on consistent\r\n"
+"application of that system; it is up to the author/donor to decide if he or\r\n"
+"she is willing to distribute software through any other system and a\r\n"
+"licensee cannot impose that choice.\r\n"
+"\r\n"
+"This section is intended to make thoroughly clear what is believed to be a\r\n"
+"consequence of the rest of this License.\r\n"
+"\r\n";
	case 8: return
 "8. If the distribution and/or use of the Program is restricted in certain\r\n"
+"countries either by patents or by copyrighted interfaces, the original\r\n"
+"copyright holder who places the Program under this License may add an\r\n"
+"explicit geographical distribution limitation excluding those countries, so\r\n"
+"that distribution is permitted only in or among countries not thus excluded.\r\n"
+"In such case, this License incorporates the limitation as if written in the\r\n"
+"body of this License.\r\n"
+"\r\n";
	case 9: return
 "9. The Free Software Foundation may publish revised and/or new versions of\r\n"
+"the General Public License from time to time. Such new versions will be\r\n"
+"similar in spirit to the present version, but may differ in detail to\r\n"
+"address new problems or concerns.\r\n"
+"\r\n"
+"Each version is given a distinguishing version number. If the Program\r\n"
+"specifies a version number of this License which applies to it and \"any\r\n"
+"later version\", you have the option of following the terms and conditions\r\n"
+"either of that version or of any later version published by the Free\r\n"
+"Software Foundation. If the Program does not specify a version number of\r\n"
+"this License, you may choose any version ever published by the Free Software\r\n"
+"Foundation.\r\n"
+"\r\n";
	case 10: return
 "10. If you wish to incorporate parts of the Program into other free programs\r\n"
+"whose distribution conditions are different, write to the author to ask for\r\n"
+"permission. For software which is copyrighted by the Free Software\r\n"
+"Foundation, write to the Free Software Foundation; we sometimes make\r\n"
+"exceptions for this. Our decision will be guided by the two goals of\r\n"
+"preserving the free status of all derivatives of our free software and of\r\n"
+"promoting the sharing and reuse of software generally.\r\n"
+"\r\n";
	case 11: return
 "NO WARRANTY\r\n"
+"\r\n"
+"11. BECAUSE THE PROGRAM IS LICENSED FREE OF CHARGE, THERE IS NO WARRANTY FOR\r\n"
+"THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN\r\n"
+"OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES\r\n"
+"PROVIDE THE PROGRAM \"AS IS\" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED\r\n"
+"OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF\r\n"
+"MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO\r\n"
+"THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM\r\n"
+"PROVE DEFECTIVE, YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR\r\n"
+"CORRECTION.\r\n"
+"\r\n";
	case 12: return
 "12. IN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW OR AGREED TO IN WRITING\r\n"
+"WILL ANY COPYRIGHT HOLDER, OR ANY OTHER PARTY WHO MAY MODIFY AND/OR\r\n"
+"REDISTRIBUTE THE PROGRAM AS PERMITTED ABOVE, BE LIABLE TO YOU FOR DAMAGES,\r\n"
+"INCLUDING ANY GENERAL, SPECIAL, INCIDENTAL OR CONSEQUENTIAL DAMAGES ARISING\r\n"
+"OUT OF THE USE OR INABILITY TO USE THE PROGRAM (INCLUDING BUT NOT LIMITED TO\r\n"
+"LOSS OF DATA OR DATA BEING RENDERED INACCURATE OR LOSSES SUSTAINED BY YOU OR\r\n"
+"THIRD PARTIES OR A FAILURE OF THE PROGRAM TO OPERATE WITH ANY OTHER\r\n"
+"PROGRAMS), EVEN IF SUCH HOLDER OR OTHER PARTY HAS BEEN ADVISED OF THE\r\n"
+"POSSIBILITY OF SUCH DAMAGES.\r\n";
	}
   } // end function GNU_General_Public_Licence()

   public static String GNU_Library_General_Public_Licence()
{
	return
 "		  GNU LIBRARY GENERAL PUBLIC LICENSE\n"
+"		       Version 2, June 1991\n"
+"\n"
+" Copyright (C) 1991 Free Software Foundation, Inc.\n"
+"                    675 Mass Ave, Cambridge, MA 02139, USA\n"
+" Everyone is permitted to copy and distribute verbatim copies\n"
+" of this license document, but changing it is not allowed.\n"
+"\n"
+"[This is the first released version of the library GPL.  It is\n"
+" numbered 2 because it goes with version 2 of the ordinary GPL.]\n"
+"\n"
+"			    Preamble\n"
+"\n"
+"  The licenses for most software are designed to take away your\n"
+"freedom to share and change it.  By contrast, the GNU General Public\n"
+"Licenses are intended to guarantee your freedom to share and change\n"
+"free software--to make sure the software is free for all its users.\n"
+"\n"
+"  This license, the Library General Public License, applies to some\n"
+"specially designated Free Software Foundation software, and to any\n"
+"other libraries whose authors decide to use it.  You can use it for\n"
+"your libraries, too.\n"
+"\n"
+"  When we speak of free software, we are referring to freedom, not\n"
+"price.  Our General Public Licenses are designed to make sure that you\n"
+"have the freedom to distribute copies of free software (and charge for\n"
+"this service if you wish), that you receive source code or can get it\n"
+"if you want it, that you can change the software or use pieces of it\n"
+"in new free programs; and that you know you can do these things.\n"
+"\n"
+"  To protect your rights, we need to make restrictions that forbid\n"
+"anyone to deny you these rights or to ask you to surrender the rights.\n"
+"These restrictions translate to certain responsibilities for you if\n"
+"you distribute copies of the library, or if you modify it.\n"
+"\n"
+"  For example, if you distribute copies of the library, whether gratis\n"
+"or for a fee, you must give the recipients all the rights that we gave\n"
+"you.  You must make sure that they, too, receive or can get the source\n"
+"code.  If you link a program with the library, you must provide\n"
+"complete object files to the recipients so that they can relink them\n"
+"with the library, after making changes to the library and recompiling\n"
+"it.  And you must show them these terms so they know their rights.\n"
+"\n"
+"  Our method of protecting your rights has two steps: (1) copyright\n"
+"the library, and (2) offer you this license which gives you legal\n"
+"permission to copy, distribute and/or modify the library.\n"
+"\n"
+"  Also, for each distributor's protection, we want to make certain\n"
+"that everyone understands that there is no warranty for this free\n"
+"library.  If the library is modified by someone else and passed on, we\n"
+"want its recipients to know that what they have is not the original\n"
+"version, so that any problems introduced by others will not reflect on\n"
+"the original authors' reputations.\n"
+"\n"
+"\n"
+"  Finally, any free program is threatened constantly by software\n"
+"patents.  We wish to avoid the danger that companies distributing free\n"
+"software will individually obtain patent licenses, thus in effect\n"
+"transforming the program into proprietary software.  To prevent this,\n"
+"we have made it clear that any patent must be licensed for everyone's\n"
+"free use or not licensed at all.\n"
+"\n"
+"  Most GNU software, including some libraries, is covered by the ordinary\n"
+"GNU General Public License, which was designed for utility programs.  This\n"
+"license, the GNU Library General Public License, applies to certain\n"
+"designated libraries.  This license is quite different from the ordinary\n"
+"one; be sure to read it in full, and don't assume that anything in it is\n"
+"the same as in the ordinary license.\n"
+"\n"
+"  The reason we have a separate public license for some libraries is that\n"
+"they blur the distinction we usually make between modifying or adding to a\n"
+"program and simply using it.  Linking a program with a library, without\n"
+"changing the library, is in some sense simply using the library, and is\n"
+"analogous to running a utility program or application program.  However, in\n"
+"a textual and legal sense, the linked executable is a combined work, a\n"
+"derivative of the original library, and the ordinary General Public License\n"
+"treats it as such.\n"
+"\n"
+"  Because of this blurred distinction, using the ordinary General\n"
+"Public License for libraries did not effectively promote software\n"
+"sharing, because most developers did not use the libraries.  We\n"
+"concluded that weaker conditions might promote sharing better.\n"
+"\n"
+"  However, unrestricted linking of non-free programs would deprive the\n"
+"users of those programs of all benefit from the free status of the\n"
+"libraries themselves.  This Library General Public License is intended to\n"
+"permit developers of non-free programs to use free libraries, while\n"
+"preserving your freedom as a user of such programs to change the free\n"
+"libraries that are incorporated in them.  (We have not seen how to achieve\n"
+"this as regards changes in header files, but we have achieved it as regards\n"
+"changes in the actual functions of the Library.)  The hope is that this\n"
+"will lead to faster development of free libraries.\n"
+"\n"
+"  The precise terms and conditions for copying, distribution and\n"
+"modification follow.  Pay close attention to the difference between a\n"
+"\"work based on the library\" and a \"work that uses the library\".  The\n"
+"former contains code derived from the library, while the latter only\n"
+"works together with the library.\n"
+"\n"
+"\n"
+"  Note that it is possible for a library to be covered by the ordinary\n"
+"General Public License rather than by this special one.\n"
+"\n"
+"\n"
+"		  GNU LIBRARY GENERAL PUBLIC LICENSE\n"
+"   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION\n"
+"\n"
+"  0. This License Agreement applies to any software library which\n"
+"contains a notice placed by the copyright holder or other authorized\n"
+"party saying it may be distributed under the terms of this Library\n"
+"General Public License (also called \"this License\").  Each licensee is\n"
+"addressed as \"you\".\n"
+"\n"
+"  A \"library\" means a collection of software functions and/or data\n"
+"prepared so as to be conveniently linked with application programs\n"
+"(which use some of those functions and data) to form executables.\n"
+"\n"
+"  The \"Library\", below, refers to any such software library or work\n"
+"which has been distributed under these terms.  A \"work based on the\n"
+"Library\" means either the Library or any derivative work under\n"
+"copyright law: that is to say, a work containing the Library or a\n"
+"portion of it, either verbatim or with modifications and/or translated\n"
+"straightforwardly into another language.  (Hereinafter, translation is\n"
+"included without limitation in the term \"modification\".)\n"
+"\n"
+"  \"Source code\" for a work means the preferred form of the work for\n"
+"making modifications to it.  For a library, complete source code means\n"
+"all the source code for all modules it contains, plus any associated\n"
+"interface definition files, plus the scripts used to control compilation\n"
+"and installation of the library.\n"
+"\n"
+"  Activities other than copying, distribution and modification are not\n"
+"covered by this License; they are outside its scope.  The act of\n"
+"running a program using the Library is not restricted, and output from\n"
+"such a program is covered only if its contents constitute a work based\n"
+"on the Library (independent of the use of the Library in a tool for\n"
+"writing it).  Whether that is true depends on what the Library does\n"
+"and what the program that uses the Library does.\n"
+"\n"
+"  1. You may copy and distribute verbatim copies of the Library's\n"
+"complete source code as you receive it, in any medium, provided that\n"
+"you conspicuously and appropriately publish on each copy an\n"
+"appropriate copyright notice and disclaimer of warranty; keep intact\n"
+"all the notices that refer to this License and to the absence of any\n"
+"warranty; and distribute a copy of this License along with the\n"
+"Library.\n"
+"\n"
+"  You may charge a fee for the physical act of transferring a copy,\n"
+"and you may at your option offer warranty protection in exchange for a\n"
+"fee.\n"
+"\n"
+"\n"
+"  2. You may modify your copy or copies of the Library or any portion\n"
+"of it, thus forming a work based on the Library, and copy and\n"
+"distribute such modifications or work under the terms of Section 1\n"
+"above, provided that you also meet all of these conditions:\n"
+"\n"
+"    a) The modified work must itself be a software library.\n"
+"\n"
+"    b) You must cause the files modified to carry prominent notices\n"
+"    stating that you changed the files and the date of any change.\n"
+"\n"
+"    c) You must cause the whole of the work to be licensed at no\n"
+"    charge to all third parties under the terms of this License.\n"
+"\n"
+"    d) If a facility in the modified Library refers to a function or a\n"
+"    table of data to be supplied by an application program that uses\n"
+"    the facility, other than as an argument passed when the facility\n"
+"    is invoked, then you must make a good faith effort to ensure that,\n"
+"    in the event an application does not supply such function or\n"
+"    table, the facility still operates, and performs whatever part of\n"
+"    its purpose remains meaningful.\n"
+"\n"
+"    (For example, a function in a library to compute square roots has\n"
+"    a purpose that is entirely well-defined independent of the\n"
+"    application.  Therefore, Subsection 2d requires that any\n"
+"    application-supplied function or table used by this function must\n"
+"    be optional: if the application does not supply it, the square\n"
+"    root function must still compute square roots.)\n"
+"\n"
+"These requirements apply to the modified work as a whole.  If\n"
+"identifiable sections of that work are not derived from the Library,\n"
+"and can be reasonably considered independent and separate works in\n"
+"themselves, then this License, and its terms, do not apply to those\n"
+"sections when you distribute them as separate works.  But when you\n"
+"distribute the same sections as part of a whole which is a work based\n"
+"on the Library, the distribution of the whole must be on the terms of\n"
+"this License, whose permissions for other licensees extend to the\n"
+"entire whole, and thus to each and every part regardless of who wrote\n"
+"it.\n"
+"\n"
+"Thus, it is not the intent of this section to claim rights or contest\n"
+"your rights to work written entirely by you; rather, the intent is to\n"
+"exercise the right to control the distribution of derivative or\n"
+"collective works based on the Library.\n"
+"\n"
+"In addition, mere aggregation of another work not based on the Library\n"
+"with the Library (or with a work based on the Library) on a volume of\n"
+"a storage or distribution medium does not bring the other work under\n"
+"the scope of this License.\n"
+"\n"
+"  3. You may opt to apply the terms of the ordinary GNU General Public\n"
+"License instead of this License to a given copy of the Library.  To do\n"
+"this, you must alter all the notices that refer to this License, so\n"
+"that they refer to the ordinary GNU General Public License, version 2,\n"
+"instead of to this License.  (If a newer version than version 2 of the\n"
+"ordinary GNU General Public License has appeared, then you can specify\n"
+"that version instead if you wish.)  Do not make any other change in\n"
+"these notices.\n"
+"\n"
+"\n"
+"  Once this change is made in a given copy, it is irreversible for\n"
+"that copy, so the ordinary GNU General Public License applies to all\n"
+"subsequent copies and derivative works made from that copy.\n"
+"\n"
+"  This option is useful when you wish to copy part of the code of\n"
+"the Library into a program that is not a library.\n"
+"\n"
+"  4. You may copy and distribute the Library (or a portion or\n"
+"derivative of it, under Section 2) in object code or executable form\n"
+"under the terms of Sections 1 and 2 above provided that you accompany\n"
+"it with the complete corresponding machine-readable source code, which\n"
+"must be distributed under the terms of Sections 1 and 2 above on a\n"
+"medium customarily used for software interchange.\n"
+"\n"
+"  If distribution of object code is made by offering access to copy\n"
+"from a designated place, then offering equivalent access to copy the\n"
+"source code from the same place satisfies the requirement to\n"
+"distribute the source code, even though third parties are not\n"
+"compelled to copy the source along with the object code.\n"
+"\n"
+"  5. A program that contains no derivative of any portion of the\n"
+"Library, but is designed to work with the Library by being compiled or\n"
+"linked with it, is called a \"work that uses the Library\".  Such a\n"
+"work, in isolation, is not a derivative work of the Library, and\n"
+"therefore falls outside the scope of this License.\n"
+"\n"
+"  However, linking a \"work that uses the Library\" with the Library\n"
+"creates an executable that is a derivative of the Library (because it\n"
+"contains portions of the Library), rather than a \"work that uses the\n"
+"library\".  The executable is therefore covered by this License."
+"Section 6 states terms for distribution of such executables.\n"
+"\n"
+"  When a \"work that uses the Library\" uses material from a header file\n"
+"that is part of the Library, the object code for the work may be a\n"
+"derivative work of the Library even though the source code is not.\n"
+"Whether this is true is especially significant if the work can be\n"
+"linked without the Library, or if the work is itself a library.  The\n"
+"threshold for this to be true is not precisely defined by law.\n"
+"\n"
+"  If such an object file uses only numerical parameters, data\n"
+"structure layouts and accessors, and small macros and small inline\n"
+"functions (ten lines or less in length), then the use of the object\n"
+"file is unrestricted, regardless of whether it is legally a derivative\n"
+"work.  (Executables containing this object code plus portions of the\n"
+"Library will still fall under Section 6.)\n"
+"\n"
+"  Otherwise, if the work is a derivative of the Library, you may\n"
+"distribute the object code for the work under the terms of Section 6.\n"
+"Any executables containing that work also fall under Section 6,\n"
+"whether or not they are linked directly with the Library itself.\n"
+"\n"
+"\n"
+"  6. As an exception to the Sections above, you may also compile or\n"
+"link a \"work that uses the Library\" with the Library to produce a\n"
+"work containing portions of the Library, and distribute that work\n"
+"under terms of your choice, provided that the terms permit\n"
+"modification of the work for the customer's own use and reverse\n"
+"engineering for debugging such modifications.\n"
+"\n"
+"  You must give prominent notice with each copy of the work that the\n"
+"Library is used in it and that the Library and its use are covered by\n"
+"this License.  You must supply a copy of this License.  If the work\n"
+"during execution displays copyright notices, you must include the\n"
+"copyright notice for the Library among them, as well as a reference\n"
+"directing the user to the copy of this License.  Also, you must do one\n"
+"of these things:\n"
+"\n"
+"    a) Accompany the work with the complete corresponding\n"
+"    machine-readable source code for the Library including whatever\n"
+"    changes were used in the work (which must be distributed under\n"
+"    Sections 1 and 2 above); and, if the work is an executable linked\n"
+"    with the Library, with the complete machine-readable \"work that\n"
+"    uses the Library\", as object code and/or source code, so that the\n"
+"    user can modify the Library and then relink to produce a modified\n"
+"    executable containing the modified Library.  (It is understood\n"
+"    that the user who changes the contents of definitions files in the\n"
+"    Library will not necessarily be able to recompile the application\n"
+"    to use the modified definitions.)\n"
+"\n"
+"    b) Accompany the work with a written offer, valid for at\n"
+"    least three years, to give the same user the materials\n"
+"    specified in Subsection 6a, above, for a charge no more\n"
+"    than the cost of performing this distribution.\n"
+"\n"
+"    c) If distribution of the work is made by offering access to copy\n"
+"    from a designated place, offer equivalent access to copy the above\n"
+"    specified materials from the same place.\n"
+"\n"
+"    d) Verify that the user has already received a copy of these\n"
+"    materials or that you have already sent this user a copy.\n"
+"\n"
+"  For an executable, the required form of the \"work that uses the\n"
+"Library\" must include any data and utility programs needed for\n"
+"reproducing the executable from it.  However, as a special exception,\n"
+"the source code distributed need not include anything that is normally\n"
+"distributed (in either source or binary form) with the major\n"
+"components (compiler, kernel, and so on) of the operating system on\n"
+"which the executable runs, unless that component itself accompanies\n"
+"the executable.\n"
+"\n"
+"  It may happen that this requirement contradicts the license\n"
+"restrictions of other proprietary libraries that do not normally\n"
+"accompany the operating system.  Such a contradiction means you cannot\n"
+"use both them and the Library together in an executable that you\n"
+"distribute.\n"
+"\n"
+"\n"
+"  7. You may place library facilities that are a work based on the\n"
+"Library side-by-side in a single library together with other library\n"
+"facilities not covered by this License, and distribute such a combined\n"
+"library, provided that the separate distribution of the work based on\n"
+"the Library and of the other library facilities is otherwise\n"
+"permitted, and provided that you do these two things:\n"
+"\n"
+"    a) Accompany the combined library with a copy of the same work\n"
+"    based on the Library, uncombined with any other library\n"
+"    facilities.  This must be distributed under the terms of the\n"
+"    Sections above.\n"
+"\n"
+"    b) Give prominent notice with the combined library of the fact\n"
+"    that part of it is a work based on the Library, and explaining\n"
+"    where to find the accompanying uncombined form of the same work.\n"
+"\n"
+"  8. You may not copy, modify, sublicense, link with, or distribute\n"
+"the Library except as expressly provided under this License.  Any\n"
+"attempt otherwise to copy, modify, sublicense, link with, or\n"
+"distribute the Library is void, and will automatically terminate your\n"
+"rights under this License.  However, parties who have received copies,\n"
+"or rights, from you under this License will not have their licenses\n"
+"terminated so long as such parties remain in full compliance.\n"
+"\n"
+"  9. You are not required to accept this License, since you have not\n"
+"signed it.  However, nothing else grants you permission to modify or\n"
+"distribute the Library or its derivative works.  These actions are\n"
+"prohibited by law if you do not accept this License.  Therefore, by\n"
+"modifying or distributing the Library (or any work based on the\n"
+"Library), you indicate your acceptance of this License to do so, and\n"
+"all its terms and conditions for copying, distributing or modifying\n"
+"the Library or works based on it.\n"
+"\n"
+"  10. Each time you redistribute the Library (or any work based on the\n"
+"Library), the recipient automatically receives a license from the\n"
+"original licensor to copy, distribute, link with or modify the Library\n"
+"subject to these terms and conditions.  You may not impose any further\n"
+"restrictions on the recipients' exercise of the rights granted herein.\n"
+"You are not responsible for enforcing compliance by third parties to\n"
+"this License.\n"
+"\n"
+"\n"
+"  11. If, as a consequence of a court judgment or allegation of patent\n"
+"infringement or for any other reason (not limited to patent issues),\n"
+"conditions are imposed on you (whether by court order, agreement or\n"
+"otherwise) that contradict the conditions of this License, they do not\n"
+"excuse you from the conditions of this License.  If you cannot\n"
+"distribute so as to satisfy simultaneously your obligations under this\n"
+"License and any other pertinent obligations, then as a consequence you\n"
+"may not distribute the Library at all.  For example, if a patent\n"
+"license would not permit royalty-free redistribution of the Library by\n"
+"all those who receive copies directly or indirectly through you, then\n"
+"the only way you could satisfy both it and this License would be to\n"
+"refrain entirely from distribution of the Library.\n"
+"\n"
+"If any portion of this section is held invalid or unenforceable under any\n"
+"particular circumstance, the balance of the section is intended to apply,\n"
+"and the section as a whole is intended to apply in other circumstances.\n"
+"\n"
+"It is not the purpose of this section to induce you to infringe any\n"
+"patents or other property right claims or to contest validity of any\n"
+"such claims; this section has the sole purpose of protecting the\n"
+"integrity of the free software distribution system which is\n"
+"implemented by public license practices.  Many people have made\n"
+"generous contributions to the wide range of software distributed\n"
+"through that system in reliance on consistent application of that\n"
+"system; it is up to the author/donor to decide if he or she is willing\n"
+"to distribute software through any other system and a licensee cannot\n"
+"impose that choice.\n"
+"\n"
+"This section is intended to make thoroughly clear what is believed to\n"
+"be a consequence of the rest of this License.\n"
+"\n"
+"  12. If the distribution and/or use of the Library is restricted in\n"
+"certain countries either by patents or by copyrighted interfaces, the\n"
+"original copyright holder who places the Library under this License may add\n"
+"an explicit geographical distribution limitation excluding those countries,\n"
+"so that distribution is permitted only in or among countries not thus\n"
+"excluded.  In such case, this License incorporates the limitation as if\n"
+"written in the body of this License.\n"
+"\n"
+"  13. The Free Software Foundation may publish revised and/or new\n"
+"versions of the Library General Public License from time to time.\n"
+"Such new versions will be similar in spirit to the present version,\n"
+"but may differ in detail to address new problems or concerns.\n"
+"\n"
+"Each version is given a distinguishing version number.  If the Library\n"
+"specifies a version number of this License which applies to it and\n"
+"\"any later version\", you have the option of following the terms and\n"
+"conditions either of that version or of any later version published by\n"
+"the Free Software Foundation.  If the Library does not specify a\n"
+"license version number, you may choose any version ever published by\n"
+"the Free Software Foundation.\n"
+"\n"
+"\n"
+"  14. If you wish to incorporate parts of the Library into other free\n"
+"programs whose distribution conditions are incompatible with these,\n"
+"write to the author to ask for permission.  For software which is\n"
+"copyrighted by the Free Software Foundation, write to the Free\n"
+"Software Foundation; we sometimes make exceptions for this.  Our\n"
+"decision will be guided by the two goals of preserving the free status\n"
+"of all derivatives of our free software and of promoting the sharing\n"
+"and reuse of software generally.\n"
+"\n"
+"			    NO WARRANTY\n"
+"\n"
+"  15. BECAUSE THE LIBRARY IS LICENSED FREE OF CHARGE, THERE IS NO\n"
+"WARRANTY FOR THE LIBRARY, TO THE EXTENT PERMITTED BY APPLICABLE LAW.\n"
+"EXCEPT WHEN OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR\n"
+"OTHER PARTIES PROVIDE THE LIBRARY \"AS IS\" WITHOUT WARRANTY OF ANY\n"
+"KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE\n"
+"IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR\n"
+"PURPOSE.  THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE\n"
+"LIBRARY IS WITH YOU.  SHOULD THE LIBRARY PROVE DEFECTIVE, YOU ASSUME\n"
+"THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.\n"
+"\n"
+"  16. IN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW OR AGREED TO IN\n"
+"WRITING WILL ANY COPYRIGHT HOLDER, OR ANY OTHER PARTY WHO MAY MODIFY\n"
+"AND/OR REDISTRIBUTE THE LIBRARY AS PERMITTED ABOVE, BE LIABLE TO YOU\n"
+"FOR DAMAGES, INCLUDING ANY GENERAL, SPECIAL, INCIDENTAL OR\n"
+"CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR INABILITY TO USE THE\n"
+"LIBRARY (INCLUDING BUT NOT LIMITED TO LOSS OF DATA OR DATA BEING\n"
+"RENDERED INACCURATE OR LOSSES SUSTAINED BY YOU OR THIRD PARTIES OR A\n"
+"FAILURE OF THE LIBRARY TO OPERATE WITH ANY OTHER SOFTWARE), EVEN IF\n"
+"SUCH HOLDER OR OTHER PARTY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH\n"
+"DAMAGES.\n"
+"\n"
+"		     END OF TERMS AND CONDITIONS\n"
+"\n"
+"\n"
+"     Appendix: How to Apply These Terms to Your New Libraries\n"
+"\n"
+"  If you develop a new library, and you want it to be of the greatest\n"
+"possible use to the public, we recommend making it free software that\n"
+"everyone can redistribute and change.  You can do so by permitting\n"
+"redistribution under these terms (or, alternatively, under the terms of the\n"
+"ordinary General Public License).\n"
+"\n"
+"  To apply these terms, attach the following notices to the library.  It is\n"
+"safest to attach them to the start of each source file to most effectively\n"
+"convey the exclusion of warranty; and each file should have at least the\n"
+"\"copyright\" line and a pointer to where the full notice is found.\n"
+"\n"
+"    <one line to give the library's name and a brief idea of what it does.>\n"
+"    Copyright (C) <year>  <name of author>\n"
+"\n"
+"    This library is free software; you can redistribute it and/or\n"
+"    modify it under the terms of the GNU Library General Public\n"
+"    License as published by the Free Software Foundation; either\n"
+"    version 2 of the License, or (at your option) any later version.\n"
+"\n"
+"    This library is distributed in the hope that it will be useful,\n"
+"    but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
+"    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU\n"
+"    Library General Public License for more details.\n"
+"\n"
+"    You should have received a copy of the GNU Library General Public\n"
+"    License along with this library; if not, write to the Free\n"
+"    Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.\n"
+"\n"
+"Also add information on how to contact you by electronic and paper mail.\n"
+"\n"
+"You should also get your employer (if you work as a programmer) or your\n"
+"school, if any, to sign a \"copyright disclaimer\" for the library, if\n"
+"necessary.  Here is a sample; alter the names:\n"
+"\n"
+"  Yoyodyne, Inc., hereby disclaims all copyright interest in the\n"
+"  library `Frob' (a library for tweaking knobs) written by James Random Hacker.\n"
+"\n"
+"  <signature of Ty Coon>, 1 April 1990\n"
+"  Ty Coon, President of Vice\n"
+"\n"
+"That's all there is to it!\n";
	} // end of GNU_General_Library_Public_Licence()
}
