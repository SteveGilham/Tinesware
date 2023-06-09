package com.ravnaandtines.util;

/**
* Licence.java - serves up copies of the GNU General Public Licence and
* General Library public licence.
* Copyright Mr. Tines &lt;tines@windsong.demon.co.uk&gt; 1998
* <p>
* For the purposes of the licence, this source file and the minimum set of
* other source files in package com.ravnaandtines.util and its sub-
* packages required to link together to satisfy all class
* references not belonging to standard Javasoft-published APIs constitute
* the library.  Thus it is not necessary to distribute source to classes
* that you do not actually use.
* <p>
* Note that Java's dynamic class loading means that the distribution of class
* files (as is, or in jar or zip form) which use this library is sufficient to
* allow run-time binding to any interface compatible version of this library.
* The GLPL is thus far less onerous for Java than for the usual run of 'C'/C++
* library.
* <p>
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Library General Public
* License as published by the Free Software Foundation; either
* version 2 of the License, or (at your option) any later version.
* <p>
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Library General Public License for more details.
* <p>
* You should have received a copy of the GNU Library General Public
* License along with this library; if not, write to the Free
* Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*<p>
* @author Mr. Tines
* @version 1.0 20-Nov-1998
*/

public class Licence
{
    /**
    * Evaluates the local end of line once
    */
    private static String NL = System.getProperty("line.separator");
    /**
    * Private constructor it makes no sense to instantiate this class
    */
    private Licence() {} // 
    /**
    * @return returns the text of the GNU General Public License as a String
    */
    public static String GNU_General_Public_Licence()
    { return
        "GNU General Public License" +NL+
        "Copyright (C) 1989, 1991 Free Software Foundation, Inc.  675" +NL+
        "Mass Ave, Cambridge, MA 02139, USA" +NL+
        "" +NL+
        "TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION" +NL+
        "" +NL+
        "0. This License applies to any program or other work which contains a notice " +
        "placed by the copyright holder saying it may be distributed under the terms " +
        "of this General Public License. The \"Program\", below, refers to any such " +
        "program or work, and a \"work based on the Program\" means either the Program " +
        "or any derivative work under copyright law: that is to say, a work " +
        "containing the Program or a portion of it, either verbatim or with " +
        "modifications and/or translated into another language. (Hereinafter, " +
        "translation is included without limitation in the term \"modification\".) Each " +
        "licensee is addressed as \"you\"" +NL+
        "" +NL+
        "Activities other than copying, distribution and modification are not covered " +
        "by this License; they are outside its scope. The act of running the Program " +
        "is not restricted, and the output from the Program is covered only if its " +
        "contents constitute a work based on the Program (independent of having been " +
        "made by running the Program). Whether that is true depends on what the " +
        "Program does." +NL+
        "" +NL+
        "1. You may copy and distribute verbatim copies of the Program's source code " +
        "as you receive it, in any medium, provided that you conspicuously and " +
        "appropriately publish on each copy an appropriate copyright notice and " +
        "disclaimer of warranty; keep intact all the notices that refer to this " +
        "License and to the absence of any warranty; and give any other recipients of " +
        "the Program a copy of this License along with the Program." +NL+
        "" +NL+
        "You may charge a fee for the physical act of transferring a copy, and you " +
        "may at your option offer warranty protection in exchange for a fee." +NL+
        "" +NL+
        "2. You may modify your copy or copies of the Program or any portion of it, " +
        "thus forming a work based on the Program, and copy and distribute such " +
        "modifications or work under the terms of Section 1 above, provided that you " +
        "also meet all of these conditions:" +NL+
        "" +NL+
        "        o a) You must cause the modified files to carry prominent notices" +NL+
        "          stating that you changed the files and the date of any change." +NL+
        "" +NL+
        "        o b) You must cause any work that you distribute or publish, that in" +NL+
        "          whole or in part contains or is derived from the Program or any" +NL+
        "          part thereof, to be licensed as a whole at no charge to all third" +NL+
        "          parties under the terms of this License." +NL+
        "" +NL+
        "        o c) If the modified program normally reads commands interactively" +NL+
        "          when run, you must cause it, when started running for such" +NL+
        "          interactive use in the most ordinary way, to print or display an" +NL+
        "          announcement including an appropriate copyright notice and a" +NL+
        "          notice that there is no warranty (or else, saying that you provide" +NL+
        "          a warranty) and that users may redistribute the program under" +NL+
        "          these conditions, and telling the user how to view a copy of this" +NL+
        "          License. (Exception: if the Program itself is interactive but does" +NL+
        "          not normally print such an announcement, your work based on the" +NL+
        "          Program is not required to print an announcement.)" +NL+
        "" +NL+
        "These requirements apply to the modified work as a whole. If identifiable " +
        "sections of that work are not derived from the Program, and can be " +
        "reasonably considered independent and separate works in themselves, then " +
        "this License, and its terms, do not apply to those sections when you " +
        "distribute them as separate works. But when you distribute the same sections " +
        "as part of a whole which is a work based on the Program, the distribution of " +
        "the whole must be on the terms of this License, whose permissions for other " +
        "licensees extend to the entire whole, and thus to each and every part " +
        "regardless of who wrote it." +NL+
        "" +NL+
        "Thus, it is not the intent of this section to claim rights or contest your " +
        "rights to work written entirely by you; rather, the intent is to exercise " +
        "the right to control the distribution of derivative or collective works " +
        "based on the Program." +NL+
        "" +NL+
        "In addition, mere aggregation of another work not based on the Program with " +
        "the Program (or with a work based on the Program) on a volume of a storage " +
        "or distribution medium does not bring the other work under the scope of this " +
        "License." +NL+
        "" +NL+
        "3. You may copy and distribute the Program (or a work based on it, under " +
        "Section 2) in object code or executable form under the terms of Sections 1 " +
        "and 2 above provided that you also do one of the following:" +NL+
        "" +NL+
        "        o a) Accompany it with the complete corresponding machine-readable" +NL+
        "          source code, which must be distributed under the terms of Sections" +NL+
        "          1 and 2 above on a medium customarily used for software" +NL+
        "          interchange; or," +NL+
        "" +NL+
        "        o b) Accompany it with a written offer, valid for at least three" +NL+
        "          years, to give any third party, for a charge no more than your" +NL+
        "          cost of physically performing source distribution, a complete" +NL+
        "          machine-readable copy of the corresponding source code, to be" +NL+
        "          distributed under the terms of Sections 1 and 2 above on a medium" +NL+
        "          customarily used for software interchange; or," +NL+
        "" +NL+
        "        o c) Accompany it with the information you received as to the offer" +NL+
        "          to distribute corresponding source code. (This alternative is" +NL+
        "          allowed o+NL+y for noncommercial distribution and o+NL+y if you" +NL+
        "          received the program in object code or executable form with such" +NL+
        "          an offer, in accord with Subsection b above.)" +NL+
        "" +NL+
        "The source code for a work means the preferred form of the work for making " +
        "modifications to it. For an executable work, complete source code means all " +
        "the source code for all modules it contains, plus any associated interface " +
        "definition files, plus the scripts used to control compilation and " +
        "installation of the executable. However, as a special exception, the source " +
        "code distributed need not include anything that is normally distributed (in " +
        "either source or binary form) with the major components (compiler, kernel, " +
        "and so on) of the operating system on which the executable runs, unless that " +
        "component itself accompanies the executable." +NL+
        "" +NL+
        "If distribution of executable or object code is made by offering access to " +
        "copy from a designated place, then offering equivalent access to copy the " +
        "source code from the same place counts as distribution of the source code, " +
        "even though third parties are not compelled to copy the source along with " +
        "the object code." +NL+
        "" +NL+
        "4. You may not copy, modify, sublicense, or distribute the Program except as " +
        "expressly provided under this License. Any attempt otherwise to copy, " +
        "modify, sublicense or distribute the Program is void, and will automatically " +
        "terminate your rights under this License. However, parties who have received " +
        "copies, or rights, from you under this License will not have their licenses " +
        "terminated so long as such parties remain in full compliance." +NL+
        "" +NL+
        "5. You are not required to accept this License, since you have not signed " +
        "it. However, nothing else grants you permission to modify or distribute the " +
        "Program or its derivative works. These actions are prohibited by law if you " +
        "do not accept this License. Therefore, by modifying or distributing the " +
        "Program (or any work based on the Program), you indicate your acceptance of " +
        "this License to do so, and all its terms and conditions for copying, " +
        "distributing or modifying the Program or works based on it." +NL+
        "" +NL+
        "6. Each time you redistribute the Program (or any work based on the " +
        "Program), the recipient automatically receives a license from the original " +
        "licensor to copy, distribute or modify the Program subject to these terms " +
        "and conditions. You may not impose any further restrictions on the " +
        "recipients' exercise of the rights granted herein. You are not responsible " +
        "for enforcing compliance by third parties to this License." +NL+
        "" +NL+
        "7. If, as a consequence of a court judgment or allegation of patent " +
        "infringement or for any other reason (not limited to patent issues), " +
        "conditions are imposed on you (whether by court order, agreement or " +
        "otherwise) that contradict the conditions of this License, they do not " +
        "excuse you from the conditions of this License. If you cannot distribute so " +
        "as to satisfy simultaneously your obligations under this License and any " +
        "other pertinent obligations, then as a consequence you may not distribute " +
        "the Program at all. For example, if a patent license would not permit " +
        "royalty-free redistribution of the Program by all those who receive copies " +
        "directly or indirectly through you, then the only way you could satisfy both " +
        "it and this License would be to refrain entirely from distribution of the " +
        "Program." +NL+
        "" +NL+
        "If any portion of this section is held invalid or unenforceable under any " +
        "particular circumstance, the balance of the section is intended to apply and " +
        "the section as a whole is intended to apply in other circumstances." +NL+
        "" +NL+
        "It is not the purpose of this section to induce you to infringe any patents " +
        "or other property right claims or to contest validity of any such claims; " +
        "this section has the sole purpose of protecting the integrity of the free " +
        "software distribution system, which is implemented by public license " +
        "practices. Many people have made generous contributions to the wide range of " +
        "software distributed through that system in reliance on consistent " +
        "application of that system; it is up to the author/donor to decide if he or " +
        "she is willing to distribute software through any other system and a " +
        "licensee cannot impose that choice." +NL+
        "" +NL+
        "This section is intended to make thoroughly clear what is believed to be a " +
        "consequence of the rest of this License." +NL+
        "" +NL+
        "8. If the distribution and/or use of the Program is restricted in certain " +
        "countries either by patents or by copyrighted interfaces, the original " +
        "copyright holder who places the Program under this License may add an " +
        "explicit geographical distribution limitation excluding those countries, so " +
        "that distribution is permitted only in or among countries not thus excluded. " +
        "In such case, this License incorporates the limitation as if written in the " +
        "body of this License." +NL+
        "" +NL+
        "9. The Free Software Foundation may publish revised and/or new versions of " +
        "the General Public License from time to time. Such new versions will be " +
        "similar in spirit to the present version, but may differ in detail to " +
        "address new problems or concerns." +NL+
        "" +NL+
        "Each version is given a distinguishing version number. If the Program " +
        "specifies a version number of this License which applies to it and \"any " +
        "later version\", you have the option of following the terms and conditions " +
        "either of that version or of any later version published by the Free " +
        "Software Foundation. If the Program does not specify a version number of " +
        "this License, you may choose any version ever published by the Free Software " +
        "Foundation." +NL+
        "" +NL+
        "10. If you wish to incorporate parts of the Program into other free programs " +
        "whose distribution conditions are different, write to the author to ask for " +
        "permission. For software which is copyrighted by the Free Software " +
        "Foundation, write to the Free Software Foundation; we sometimes make " +
        "exceptions for this. Our decision will be guided by the two goals of "+
        "preserving the free status of all derivatives of our free software and of " +
        "promoting the sharing and reuse of software generally." +NL+
        "" +NL+
        "NO WARRANTY" +NL+
        "" +NL+
        "11. BECAUSE THE PROGRAM IS LICENSED FREE OF CHARGE, THERE IS NO WARRANTY FOR " +
        "THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN " +
        "OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES " +
        "PROVIDE THE PROGRAM \"AS IS\" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED " +
        "OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF " +
        "MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO " +
        "THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM " +
        "PROVE DEFECTIVE, YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR " +
        "CORRECTION." +NL+
        "" +NL+
        "12. IN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW OR AGREED TO IN WRITING " +
        "WILL ANY COPYRIGHT HOLDER, OR ANY OTHER PARTY WHO MAY MODIFY AND/OR " +
        "REDISTRIBUTE THE PROGRAM AS PERMITTED ABOVE, BE LIABLE TO YOU FOR DAMAGES, " +
        "INCLUDING ANY GENERAL, SPECIAL, INCIDENTAL OR CONSEQUENTIAL DAMAGES ARISING " +
        "OUT OF THE USE OR INABILITY TO USE THE PROGRAM (INCLUDING BUT NOT LIMITED TO " +
        "LOSS OF DATA OR DATA BEING RENDERED INACCURATE OR LOSSES SUSTAINED BY YOU OR " +
        "THIRD PARTIES OR A FAILURE OF THE PROGRAM TO OPERATE WITH ANY OTHER " +
        "PROGRAMS), EVEN IF SUCH HOLDER OR OTHER PARTY HAS BEEN ADVISED OF THE " +
        "POSSIBILITY OF SUCH DAMAGES." +NL;
    }

    /**
    * @return returns the text of the GNU Library General Public License as a String
    */
   public static String GNU_Library_General_Public_Licence()
    {
	    return
         "		  GNU LIBRARY GENERAL PUBLIC LICENSE"+NL
        +"		       Version 2, June 1991"+NL
        +NL
        +" Copyright (C) 1991 Free Software Foundation, Inc."+NL
        +"                    675 Mass Ave, Cambridge, MA 02139, USA"+NL
        +" Everyone is permitted to copy and distribute verbatim copies"+NL
        +" of this license document, but changing it is not allowed."+NL
        +NL
        +"[This is the first released version of the library GPL.  It is"
        +" numbered 2 because it goes with version 2 of the ordinary GPL.]"+NL
        +NL
        +"			    Preamble"+NL
        +NL
        +"  The licenses for most software are designed to take away your"
        +"freedom to share and change it.  By contrast, the GNU General Public"
        +"Licenses are intended to guarantee your freedom to share and change"
        +"free software--to make sure the software is free for all its users."+NL
        +NL
        +"  This license, the Library General Public License, applies to some"
        +"specially designated Free Software Foundation software, and to any"
        +"other libraries whose authors decide to use it.  You can use it for"
        +"your libraries, too."+NL
        +NL
        +"  When we speak of free software, we are referring to freedom, not"
        +"price.  Our General Public Licenses are designed to make sure that you"
        +"have the freedom to distribute copies of free software (and charge for"
        +"this service if you wish), that you receive source code or can get it"
        +"if you want it, that you can change the software or use pieces of it"
        +"in new free programs; and that you know you can do these things."
        +NL
        +"  To protect your rights, we need to make restrictions that forbid"
        +"anyone to deny you these rights or to ask you to surrender the rights."
        +"These restrictions translate to certain responsibilities for you if"
        +"you distribute copies of the library, or if you modify it."+NL
        +NL
        +"  For example, if you distribute copies of the library, whether gratis"
        +"or for a fee, you must give the recipients all the rights that we gave"
        +"you.  You must make sure that they, too, receive or can get the source"
        +"code.  If you link a program with the library, you must provide"
        +"complete object files to the recipients so that they can relink them"
        +"with the library, after making changes to the library and recompiling"
        +"it.  And you must show them these terms so they know their rights."+NL
        +NL
        +"  Our method of protecting your rights has two steps: (1) copyright"
        +"the library, and (2) offer you this license which gives you legal"
        +"permission to copy, distribute and/or modify the library."+NL
        +NL
        +"  Also, for each distributor's protection, we want to make certain"
        +"that everyone understands that there is no warranty for this free"
        +"library.  If the library is modified by someone else and passed on, we"
        +"want its recipients to know that what they have is not the original"
        +"version, so that any problems introduced by others will not reflect on"
        +"the original authors' reputations."+NL
        +NL
        +NL
        +"  Finally, any free program is threatened constantly by software"
        +"patents.  We wish to avoid the danger that companies distributing free"
        +"software will individually obtain patent licenses, thus in effect"
        +"transforming the program into proprietary software.  To prevent this,"
        +"we have made it clear that any patent must be licensed for everyone's"
        +"free use or not licensed at all."+NL
        +NL
        +"  Most GNU software, including some libraries, is covered by the ordinary"
        +"GNU General Public License, which was designed for utility programs.  This"
        +"license, the GNU Library General Public License, applies to certain"
        +"designated libraries.  This license is quite different from the ordinary"
        +"one; be sure to read it in full, and don't assume that anything in it is"
        +"the same as in the ordinary license."+NL
        +NL
        +"  The reason we have a separate public license for some libraries is that"
        +"they blur the distinction we usually make between modifying or adding to a"
        +"program and simply using it.  Linking a program with a library, without"
        +"changing the library, is in some sense simply using the library, and is"
        +"analogous to running a utility program or application program.  However, in"
        +"a textual and legal sense, the linked executable is a combined work, a"
        +"derivative of the original library, and the ordinary General Public License"
        +"treats it as such."+NL
        +NL
        +"  Because of this blurred distinction, using the ordinary General"
        +"Public License for libraries did not effectively promote software"
        +"sharing, because most developers did not use the libraries.  We"
        +"concluded that weaker conditions might promote sharing better."+NL
        +NL
        +"  However, unrestricted linking of non-free programs would deprive the"
        +"users of those programs of all benefit from the free status of the"
        +"libraries themselves.  This Library General Public License is intended to"
        +"permit developers of non-free programs to use free libraries, while"
        +"preserving your freedom as a user of such programs to change the free"
        +"libraries that are incorporated in them.  (We have not seen how to achieve"
        +"this as regards changes in header files, but we have achieved it as regards"
        +"changes in the actual functions of the Library.)  The hope is that this"
        +"will lead to faster development of free libraries."+NL
        +NL
        +"  The precise terms and conditions for copying, distribution and"
        +"modification follow.  Pay close attention to the difference between a"
        +"\"work based on the library\" and a \"work that uses the library\".  The"
        +"former contains code derived from the library, while the latter only"
        +"works together with the library."+NL
        +NL
        +NL
        +"  Note that it is possible for a library to be covered by the ordinary"
        +"General Public License rather than by this special one."+NL
        +NL
        +NL
        +"		  GNU LIBRARY GENERAL PUBLIC LICENSE"+NL
        +"   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION"+NL
        +NL
        +"  0. This License Agreement applies to any software library which"
        +"contains a notice placed by the copyright holder or other authorized"
        +"party saying it may be distributed under the terms of this Library"
        +"General Public License (also called \"this License\").  Each licensee is"
        +"addressed as \"you\"."+NL
        +NL
        +"  A \"library\" means a collection of software functions and/or data"
        +"prepared so as to be conveniently linked with application programs"
        +"(which use some of those functions and data) to form executables."+NL
        +NL
        +"  The \"Library\", below, refers to any such software library or work"
        +"which has been distributed under these terms.  A \"work based on the"
        +"Library\" means either the Library or any derivative work under"
        +"copyright law: that is to say, a work containing the Library or a"
        +"portion of it, either verbatim or with modifications and/or translated"
        +"straightforwardly into another language.  (Hereinafter, translation is"
        +"included without limitation in the term \"modification\".)"+NL
        +NL
        +"  \"Source code\" for a work means the preferred form of the work for"
        +"making modifications to it.  For a library, complete source code means"
        +"all the source code for all modules it contains, plus any associated"
        +"interface definition files, plus the scripts used to control compilation"
        +"and installation of the library."+NL
        +NL
        +"  Activities other than copying, distribution and modification are not"
        +"covered by this License; they are outside its scope.  The act of"
        +"running a program using the Library is not restricted, and output from"
        +"such a program is covered only if its contents constitute a work based"
        +"on the Library (independent of the use of the Library in a tool for"
        +"writing it).  Whether that is true depends on what the Library does"
        +"and what the program that uses the Library does."+NL
        +NL
        +"  1. You may copy and distribute verbatim copies of the Library's"
        +"complete source code as you receive it, in any medium, provided that"
        +"you conspicuously and appropriately publish on each copy an"
        +"appropriate copyright notice and disclaimer of warranty; keep intact"
        +"all the notices that refer to this License and to the absence of any"
        +"warranty; and distribute a copy of this License along with the"
        +"Library."+NL
        +NL
        +"  You may charge a fee for the physical act of transferring a copy,"
        +"and you may at your option offer warranty protection in exchange for a"
        +"fee."+NL
        +NL
        +NL
        +"  2. You may modify your copy or copies of the Library or any portion"
        +"of it, thus forming a work based on the Library, and copy and"
        +"distribute such modifications or work under the terms of Section 1"
        +"above, provided that you also meet all of these conditions:"+NL
        +NL
        +"    a) The modified work must itself be a software library."+NL
        +NL
        +"    b) You must cause the files modified to carry prominent notices"+NL
        +"    stating that you changed the files and the date of any change."+NL
        +NL
        +"    c) You must cause the whole of the work to be licensed at no"+NL
        +"    charge to all third parties under the terms of this License."+NL
        +NL
        +"    d) If a facility in the modified Library refers to a function or a"+NL
        +"    table of data to be supplied by an application program that uses"+NL
        +"    the facility, other than as an argument passed when the facility"+NL
        +"    is invoked, then you must make a good faith effort to ensure that,"+NL
        +"    in the event an application does not supply such function or"+NL
        +"    table, the facility still operates, and performs whatever part of"+NL
        +"    its purpose remains meaningful."+NL
        +NL
        +"    (For example, a function in a library to compute square roots has"+NL
        +"    a purpose that is entirely well-defined independent of the"+NL
        +"    application.  Therefore, Subsection 2d requires that any"+NL
        +"    application-supplied function or table used by this function must"+NL
        +"    be optional: if the application does not supply it, the square"+NL
        +"    root function must still compute square roots.)"+NL
        +NL
        +"These requirements apply to the modified work as a whole.  If"
        +"identifiable sections of that work are not derived from the Library,"
        +"and can be reasonably considered independent and separate works in"
        +"themselves, then this License, and its terms, do not apply to those"
        +"sections when you distribute them as separate works.  But when you"
        +"distribute the same sections as part of a whole which is a work based"
        +"on the Library, the distribution of the whole must be on the terms of"
        +"this License, whose permissions for other licensees extend to the"
        +"entire whole, and thus to each and every part regardless of who wrote"
        +"it."+NL
        +NL
        +"Thus, it is not the intent of this section to claim rights or contest"
        +"your rights to work written entirely by you; rather, the intent is to"
        +"exercise the right to control the distribution of derivative or"
        +"collective works based on the Library."+NL
        +NL
        +"In addition, mere aggregation of another work not based on the Library"
        +"with the Library (or with a work based on the Library) on a volume of"
        +"a storage or distribution medium does not bring the other work under"
        +"the scope of this License."+NL
        +NL
        +"  3. You may opt to apply the terms of the ordinary GNU General Public"
        +"License instead of this License to a given copy of the Library.  To do"
        +"this, you must alter all the notices that refer to this License, so"
        +"that they refer to the ordinary GNU General Public License, version 2,"
        +"instead of to this License.  (If a newer version than version 2 of the"
        +"ordinary GNU General Public License has appeared, then you can specify"
        +"that version instead if you wish.)  Do not make any other change in"
        +"these notices."+NL
        +NL
        +NL
        +"  Once this change is made in a given copy, it is irreversible for"
        +"that copy, so the ordinary GNU General Public License applies to all"
        +"subsequent copies and derivative works made from that copy."+NL
        +NL
        +"  This option is useful when you wish to copy part of the code of"
        +"the Library into a program that is not a library."+NL
        +NL
        +"  4. You may copy and distribute the Library (or a portion or"
        +"derivative of it, under Section 2) in object code or executable form"
        +"under the terms of Sections 1 and 2 above provided that you accompany"
        +"it with the complete corresponding machine-readable source code, which"
        +"must be distributed under the terms of Sections 1 and 2 above on a"
        +"medium customarily used for software interchange."+NL
        +NL
        +"  If distribution of object code is made by offering access to copy"
        +"from a designated place, then offering equivalent access to copy the"
        +"source code from the same place satisfies the requirement to"
        +"distribute the source code, even though third parties are not"
        +"compelled to copy the source along with the object code."+NL
        +NL
        +"  5. A program that contains no derivative of any portion of the"
        +"Library, but is designed to work with the Library by being compiled or"
        +"linked with it, is called a \"work that uses the Library\".  Such a"
        +"work, in isolation, is not a derivative work of the Library, and"
        +"therefore falls outside the scope of this License."+NL
        +NL
        +"  However, linking a \"work that uses the Library\" with the Library"
        +"creates an executable that is a derivative of the Library (because it"
        +"contains portions of the Library), rather than a \"work that uses the"
        +"library\".  The executable is therefore covered by this License."
        +"Section 6 states terms for distribution of such executables."+NL
        +NL
        +"  When a \"work that uses the Library\" uses material from a header file"
        +"that is part of the Library, the object code for the work may be a"
        +"derivative work of the Library even though the source code is not."
        +"Whether this is true is especially significant if the work can be"
        +"linked without the Library, or if the work is itself a library.  The"
        +"threshold for this to be true is not precisely defined by law."+NL
        +NL
        +"  If such an object file uses only numerical parameters, data"
        +"structure layouts and accessors, and small macros and small inline"
        +"functions (ten lines or less in length), then the use of the object"
        +"file is unrestricted, regardless of whether it is legally a derivative"
        +"work.  (Executables containing this object code plus portions of the"
        +"Library will still fall under Section 6.)"+NL
        +NL
        +"  Otherwise, if the work is a derivative of the Library, you may"
        +"distribute the object code for the work under the terms of Section 6."
        +"Any executables containing that work also fall under Section 6,"
        +"whether or not they are linked directly with the Library itself."+NL
        +NL
        +NL
        +"  6. As an exception to the Sections above, you may also compile or"
        +"link a \"work that uses the Library\" with the Library to produce a"
        +"work containing portions of the Library, and distribute that work"
        +"under terms of your choice, provided that the terms permit"
        +"modification of the work for the customer's own use and reverse"
        +"engineering for debugging such modifications."+NL
        +NL
        +"  You must give prominent notice with each copy of the work that the"
        +"Library is used in it and that the Library and its use are covered by"
        +"this License.  You must supply a copy of this License.  If the work"
        +"during execution displays copyright notices, you must include the"
        +"copyright notice for the Library among them, as well as a reference"
        +"directing the user to the copy of this License.  Also, you must do one"
        +"of these things:"+NL
        +NL
        +"    a) Accompany the work with the complete corresponding"+NL
        +"    machine-readable source code for the Library including whatever"+NL
        +"    changes were used in the work (which must be distributed under"+NL
        +"    Sections 1 and 2 above); and, if the work is an executable linked"+NL
        +"    with the Library, with the complete machine-readable \"work that"+NL
        +"    uses the Library\", as object code and/or source code, so that the"+NL
        +"    user can modify the Library and then relink to produce a modified"+NL
        +"    executable containing the modified Library.  (It is understood"+NL
        +"    that the user who changes the contents of definitions files in the"+NL
        +"    Library will not necessarily be able to recompile the application"+NL
        +"    to use the modified definitions.)"+NL
        +NL
        +"    b) Accompany the work with a written offer, valid for at"+NL
        +"    least three years, to give the same user the materials"+NL
        +"    specified in Subsection 6a, above, for a charge no more"+NL
        +"    than the cost of performing this distribution."+NL
        +NL
        +"    c) If distribution of the work is made by offering access to copy"+NL
        +"    from a designated place, offer equivalent access to copy the above"+NL
        +"    specified materials from the same place."+NL
        +NL
        +"    d) Verify that the user has already received a copy of these"+NL
        +"    materials or that you have already sent this user a copy."+NL
        +NL
        +"  For an executable, the required form of the \"work that uses the"
        +"Library\" must include any data and utility programs needed for"
        +"reproducing the executable from it.  However, as a special exception,"
        +"the source code distributed need not include anything that is normally"
        +"distributed (in either source or binary form) with the major"
        +"components (compiler, kernel, and so on) of the operating system on"
        +"which the executable runs, unless that component itself accompanies"
        +"the executable."+NL
        +NL
        +"  It may happen that this requirement contradicts the license"
        +"restrictions of other proprietary libraries that do not normally"
        +"accompany the operating system.  Such a contradiction means you cannot"
        +"use both them and the Library together in an executable that you"
        +"distribute."+NL
        +NL
        +NL
        +"  7. You may place library facilities that are a work based on the"
        +"Library side-by-side in a single library together with other library"
        +"facilities not covered by this License, and distribute such a combined"
        +"library, provided that the separate distribution of the work based on"
        +"the Library and of the other library facilities is otherwise"
        +"permitted, and provided that you do these two things:"+NL
        +NL
        +"    a) Accompany the combined library with a copy of the same work"+NL
        +"    based on the Library, uncombined with any other library"+NL
        +"    facilities.  This must be distributed under the terms of the"+NL
        +"    Sections above."+NL
        +NL
        +"    b) Give prominent notice with the combined library of the fact"+NL
        +"    that part of it is a work based on the Library, and explaining"+NL
        +"    where to find the accompanying uncombined form of the same work."+NL
        +NL
        +"  8. You may not copy, modify, sublicense, link with, or distribute"
        +"the Library except as expressly provided under this License.  Any"
        +"attempt otherwise to copy, modify, sublicense, link with, or"
        +"distribute the Library is void, and will automatically terminate your"
        +"rights under this License.  However, parties who have received copies,"
        +"or rights, from you under this License will not have their licenses"
        +"terminated so long as such parties remain in full compliance."
        +NL
        +"  9. You are not required to accept this License, since you have not"
        +"signed it.  However, nothing else grants you permission to modify or"
        +"distribute the Library or its derivative works.  These actions are"
        +"prohibited by law if you do not accept this License.  Therefore, by"
        +"modifying or distributing the Library (or any work based on the"
        +"Library), you indicate your acceptance of this License to do so, and"
        +"all its terms and conditions for copying, distributing or modifying"
        +"the Library or works based on it."+NL
        +NL
        +"  10. Each time you redistribute the Library (or any work based on the"
        +"Library), the recipient automatically receives a license from the"
        +"original licensor to copy, distribute, link with or modify the Library"
        +"subject to these terms and conditions.  You may not impose any further"
        +"restrictions on the recipients' exercise of the rights granted herein."
        +"You are not responsible for enforcing compliance by third parties to"
        +"this License."+NL
        +NL
        +NL
        +"  11. If, as a consequence of a court judgment or allegation of patent"
        +"infringement or for any other reason (not limited to patent issues),"
        +"conditions are imposed on you (whether by court order, agreement or"
        +"otherwise) that contradict the conditions of this License, they do not"
        +"excuse you from the conditions of this License.  If you cannot"
        +"distribute so as to satisfy simultaneously your obligations under this"
        +"License and any other pertinent obligations, then as a consequence you"
        +"may not distribute the Library at all.  For example, if a patent"
        +"license would not permit royalty-free redistribution of the Library by"
        +"all those who receive copies directly or indirectly through you, then"
        +"the only way you could satisfy both it and this License would be to"
        +"refrain entirely from distribution of the Library."+NL
        +NL
        +"If any portion of this section is held invalid or unenforceable under any"
        +"particular circumstance, the balance of the section is intended to apply,"
        +"and the section as a whole is intended to apply in other circumstances."
        +NL
        +"It is not the purpose of this section to induce you to infringe any"+NL
        +"patents or other property right claims or to contest validity of any"
        +"such claims; this section has the sole purpose of protecting the"
        +"integrity of the free software distribution system which is"
        +"implemented by public license practices.  Many people have made"
        +"generous contributions to the wide range of software distributed"
        +"through that system in reliance on consistent application of that"
        +"system; it is up to the author/donor to decide if he or she is willing"
        +"to distribute software through any other system and a licensee cannot"
        +"impose that choice."+NL
        +NL
        +"This section is intended to make thoroughly clear what is believed to"
        +"be a consequence of the rest of this License."+NL
        +NL
        +"  12. If the distribution and/or use of the Library is restricted in"
        +"certain countries either by patents or by copyrighted interfaces, the"
        +"original copyright holder who places the Library under this License may add"
        +"an explicit geographical distribution limitation excluding those countries,"
        +"so that distribution is permitted only in or among countries not thus"
        +"excluded.  In such case, this License incorporates the limitation as if"
        +"written in the body of this License."+NL
        +NL
        +"  13. The Free Software Foundation may publish revised and/or new"
        +"versions of the Library General Public License from time to time."
        +"Such new versions will be similar in spirit to the present version,"
        +"but may differ in detail to address new problems or concerns."+NL
        +NL
        +"Each version is given a distinguishing version number.  If the Library"
        +"specifies a version number of this License which applies to it and"
        +"\"any later version\", you have the option of following the terms and"
        +"conditions either of that version or of any later version published by"
        +"the Free Software Foundation.  If the Library does not specify a"
        +"license version number, you may choose any version ever published by"
        +"the Free Software Foundation."+NL
        +NL
        +NL
        +"  14. If you wish to incorporate parts of the Library into other free"
        +"programs whose distribution conditions are incompatible with these,"
        +"write to the author to ask for permission.  For software which is"
        +"copyrighted by the Free Software Foundation, write to the Free"
        +"Software Foundation; we sometimes make exceptions for this.  Our"
        +"decision will be guided by the two goals of preserving the free status"
        +"of all derivatives of our free software and of promoting the sharing"
        +"and reuse of software generally."+NL
        +NL
        +"			    NO WARRANTY"+NL
        +NL
        +"  15. BECAUSE THE LIBRARY IS LICENSED FREE OF CHARGE, THERE IS NO"
        +"WARRANTY FOR THE LIBRARY, TO THE EXTENT PERMITTED BY APPLICABLE LAW."
        +"EXCEPT WHEN OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR"
        +"OTHER PARTIES PROVIDE THE LIBRARY \"AS IS\" WITHOUT WARRANTY OF ANY"
        +"KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE"
        +"IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR"
        +"PURPOSE.  THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE"
        +"LIBRARY IS WITH YOU.  SHOULD THE LIBRARY PROVE DEFECTIVE, YOU ASSUME"
        +"THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION."+NL
        +NL
        +"  16. IN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW OR AGREED TO IN"
        +"WRITING WILL ANY COPYRIGHT HOLDER, OR ANY OTHER PARTY WHO MAY MODIFY"
        +"AND/OR REDISTRIBUTE THE LIBRARY AS PERMITTED ABOVE, BE LIABLE TO YOU"
        +"FOR DAMAGES, INCLUDING ANY GENERAL, SPECIAL, INCIDENTAL OR"
        +"CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR INABILITY TO USE THE"
        +"LIBRARY (INCLUDING BUT NOT LIMITED TO LOSS OF DATA OR DATA BEING"
        +"RENDERED INACCURATE OR LOSSES SUSTAINED BY YOU OR THIRD PARTIES OR A"
        +"FAILURE OF THE LIBRARY TO OPERATE WITH ANY OTHER SOFTWARE), EVEN IF"
        +"SUCH HOLDER OR OTHER PARTY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH"
        +"DAMAGES."+NL
        +NL
        +"		     END OF TERMS AND CONDITIONS"+NL
        +NL
        +NL
        +"     Appendix: How to Apply These Terms to Your New Libraries"+NL
        +NL
        +"  If you develop a new library, and you want it to be of the greatest"
        +"possible use to the public, we recommend making it free software that"
        +"everyone can redistribute and change.  You can do so by permitting"
        +"redistribution under these terms (or, alternatively, under the terms of the"
        +"ordinary General Public License)."
        +NL
        +"  To apply these terms, attach the following notices to the library.  It is"
        +"safest to attach them to the start of each source file to most effectively"
        +"convey the exclusion of warranty; and each file should have at least the"
        +"\"copyright\" line and a pointer to where the full notice is found."+NL
        +NL
        +"    <one line to give the library's name and a brief idea of what it does.>"+NL
        +"    Copyright (C) <year>  <name of author>"+NL
        +NL
        +"    This library is free software; you can redistribute it and/or"+NL
        +"    modify it under the terms of the GNU Library General Public"+NL
        +"    License as published by the Free Software Foundation; either"+NL
        +"    version 2 of the License, or (at your option) any later version."+NL
        +NL
        +"    This library is distributed in the hope that it will be useful,"+NL
        +"    but WITHOUT ANY WARRANTY; without even the implied warranty of"+NL
        +"    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU"+NL
        +"    Library General Public License for more details."+NL
        +NL
        +"    You should have received a copy of the GNU Library General Public"+NL
        +"    License along with this library; if not, write to the Free"+NL
        +"    Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA."+NL
        +NL
        +"Also add information on how to contact you by electronic and paper mail."+NL
        +NL
        +"You should also get your employer (if you work as a programmer) or your"
        +"school, if any, to sign a \"copyright disclaimer\" for the library, if"
        +"necessary.  Here is a sample; alter the names:"+NL
        +NL
        +"  Yoyodyne, Inc., hereby disclaims all copyright interest in the"+NL
        +"  library `Frob' (a library for tweaking knobs) written by James Random Hacker."+NL
        +NL
        +"  <signature of Ty Coon>, 1 April 1990"+NL
        +"  Ty Coon, President of Vice"+NL
        +NL
        +"That's all there is to it!";
	} // end of GNU_General_Library_Public_Licence()
}

