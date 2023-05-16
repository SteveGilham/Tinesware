/* licences.c
 **
 ** Copyright (C)  Heimdall <heimdall@bifroest.demon.co.uk> 1997
 **
 ** This program is free software; you can redistribute it and/or
 ** modify it under the terms of the GNU General Public License
 ** version 2 as published by the Free Software Foundation.
 **
 ** The full text of the GNU General Public Licence is included in the
 ** source of (and returned by) the function GNU_General_Public_Licence()
 ** listed below.
 **
 ** This program is distributed in the hope that it will be useful,
 ** but WITHOUT ANY WARRANTY; without even the implied warranty of
 ** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 ** GNU General Public License for more details.
 **
 */
#include "licences.h"
#if defined(THINK_C) || defined(SYMANTEC_C)
#define NL "\r"
#define XNL
#else
#if defined (_OWLDLL) || defined(_MSC_VER) || defined(SHORT_LINES)
#define NL "\r\n"
#define XNL "\r\n"
#else
#define NL "\n"
#define XNL
#endif
#endif
static char * GNU_General_Public_Licence(void)
{
    return
        "GNU General Public License" NL
        "Copyright (C) 1989, 1991 Free Software Foundation, Inc.  675" NL
        "Mass Ave, Cambridge, MA 02139, USA" NL
        "" NL
        "TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION" NL
        "" NL
        "0. This License applies to any program or other work which contains a notice " XNL
        "placed by the copyright holder saying it may be distributed under the terms " XNL
        "of this General Public License. The \"Program\", below, refers to any such " XNL
        "program or work, and a \"work based on the Program\" means either the Program " XNL
        "or any derivative work under copyright law: that is to say, a work " XNL
        "containing the Program or a portion of it, either verbatim or with " XNL
        "modifications and/or translated into another language. (Hereinafter, " XNL
        "translation is included without limitation in the term \"modification\".) Each " XNL
        "licensee is addressed as \"you\"" NL
        "" NL
        "Activities other than copying, distribution and modification are not covered " XNL
        "by this License; they are outside its scope. The act of running the Program " XNL
        "is not restricted, and the output from the Program is covered only if its " XNL
        "contents constitute a work based on the Program (independent of having been " XNL
        "made by running the Program). Whether that is true depends on what the " XNL
        "Program does." NL
        "" NL
        "1. You may copy and distribute verbatim copies of the Program's source code " XNL
        "as you receive it, in any medium, provided that you conspicuously and " XNL
        "appropriately publish on each copy an appropriate copyright notice and " XNL
        "disclaimer of warranty; keep intact all the notices that refer to this " XNL
        "License and to the absence of any warranty; and give any other recipients of " XNL
        "the Program a copy of this License along with the Program." NL
        "" NL
        "You may charge a fee for the physical act of transferring a copy, and you " XNL
        "may at your option offer warranty protection in exchange for a fee." NL
        "" NL
        "2. You may modify your copy or copies of the Program or any portion of it, " XNL
        "thus forming a work based on the Program, and copy and distribute such " XNL
        "modifications or work under the terms of Section 1 above, provided that you " XNL
        "also meet all of these conditions:" NL
        "" NL
        "        o a) You must cause the modified files to carry prominent notices" NL
        "          stating that you changed the files and the date of any change." NL
        "" NL
        "        o b) You must cause any work that you distribute or publish, that in" NL
        "          whole or in part contains or is derived from the Program or any" NL
        "          part thereof, to be licensed as a whole at no charge to all third" NL
        "          parties under the terms of this License." NL
        "" NL
        "        o c) If the modified program normally reads commands interactively" NL
        "          when run, you must cause it, when started running for such" NL
        "          interactive use in the most ordinary way, to print or display an" NL
        "          announcement including an appropriate copyright notice and a" NL
        "          notice that there is no warranty (or else, saying that you provide" NL
        "          a warranty) and that users may redistribute the program under" NL
        "          these conditions, and telling the user how to view a copy of this" NL
        "          License. (Exception: if the Program itself is interactive but does" NL
        "          not normally print such an announcement, your work based on the" NL
        "          Program is not required to print an announcement.)" NL
        "" NL
        "These requirements apply to the modified work as a whole. If identifiable " XNL
        "sections of that work are not derived from the Program, and can be " XNL
        "reasonably considered independent and separate works in themselves, then " XNL
        "this License, and its terms, do not apply to those sections when you " XNL
        "distribute them as separate works. But when you distribute the same sections " XNL
        "as part of a whole which is a work based on the Program, the distribution of " XNL
        "the whole must be on the terms of this License, whose permissions for other " XNL
        "licensees extend to the entire whole, and thus to each and every part " XNL
        "regardless of who wrote it." NL
        "" NL
        "Thus, it is not the intent of this section to claim rights or contest your " XNL
        "rights to work written entirely by you; rather, the intent is to exercise " XNL
        "the right to control the distribution of derivative or collective works " XNL
        "based on the Program." NL
        "" NL
        "In addition, mere aggregation of another work not based on the Program with " XNL
        "the Program (or with a work based on the Program) on a volume of a storage " XNL
        "or distribution medium does not bring the other work under the scope of this " XNL
        "License." NL
        "" NL
        "3. You may copy and distribute the Program (or a work based on it, under " XNL
        "Section 2) in object code or executable form under the terms of Sections 1 " XNL
        "and 2 above provided that you also do one of the following:" NL
        "" NL
        "        o a) Accompany it with the complete corresponding machine-readable" NL
        "          source code, which must be distributed under the terms of Sections" NL
        "          1 and 2 above on a medium customarily used for software" NL
        "          interchange; or," NL
        "" NL
        "        o b) Accompany it with a written offer, valid for at least three" NL
        "          years, to give any third party, for a charge no more than your" NL
        "          cost of physically performing source distribution, a complete" NL
        "          machine-readable copy of the corresponding source code, to be" NL
        "          distributed under the terms of Sections 1 and 2 above on a medium" NL
        "          customarily used for software interchange; or," NL
        "" NL
        "        o c) Accompany it with the information you received as to the offer" NL
        "          to distribute corresponding source code. (This alternative is" NL
        "          allowed only for noncommercial distribution and only if you" NL
        "          received the program in object code or executable form with such" NL
        "          an offer, in accord with Subsection b above.)" NL
        "" NL
        "The source code for a work means the preferred form of the work for making " XNL
        "modifications to it. For an executable work, complete source code means all " XNL
        "the source code for all modules it contains, plus any associated interface " XNL
        "definition files, plus the scripts used to control compilation and " XNL
        "installation of the executable. However, as a special exception, the source " XNL
        "code distributed need not include anything that is normally distributed (in " XNL
        "either source or binary form) with the major components (compiler, kernel, " XNL
        "and so on) of the operating system on which the executable runs, unless that " XNL
        "component itself accompanies the executable." NL
        "" NL
        "If distribution of executable or object code is made by offering access to " XNL
        "copy from a designated place, then offering equivalent access to copy the " XNL
        "source code from the same place counts as distribution of the source code, " XNL
        "even though third parties are not compelled to copy the source along with " XNL
        "the object code." NL
        "" NL
        "4. You may not copy, modify, sublicense, or distribute the Program except as " XNL
        "expressly provided under this License. Any attempt otherwise to copy, " XNL
        "modify, sublicense or distribute the Program is void, and will automatically " XNL
        "terminate your rights under this License. However, parties who have received " XNL
        "copies, or rights, from you under this License will not have their licenses " XNL
        "terminated so long as such parties remain in full compliance." NL
        "" NL
        "5. You are not required to accept this License, since you have not signed " XNL
        "it. However, nothing else grants you permission to modify or distribute the " XNL
        "Program or its derivative works. These actions are prohibited by law if you " XNL
        "do not accept this License. Therefore, by modifying or distributing the " XNL
        "Program (or any work based on the Program), you indicate your acceptance of " XNL
        "this License to do so, and all its terms and conditions for copying, " XNL
        "distributing or modifying the Program or works based on it." NL
        "" NL
        "6. Each time you redistribute the Program (or any work based on the " XNL
        "Program), the recipient automatically receives a license from the original " XNL
        "licensor to copy, distribute or modify the Program subject to these terms " XNL
        "and conditions. You may not impose any further restrictions on the " XNL
        "recipients' exercise of the rights granted herein. You are not responsible " XNL
        "for enforcing compliance by third parties to this License." NL
        "" NL
        "7. If, as a consequence of a court judgment or allegation of patent " XNL
        "infringement or for any other reason (not limited to patent issues), " XNL
        "conditions are imposed on you (whether by court order, agreement or " XNL
        "otherwise) that contradict the conditions of this License, they do not " XNL
        "excuse you from the conditions of this License. If you cannot distribute so " XNL
        "as to satisfy simultaneously your obligations under this License and any " XNL
        "other pertinent obligations, then as a consequence you may not distribute " XNL
        "the Program at all. For example, if a patent license would not permit " XNL
        "royalty-free redistribution of the Program by all those who receive copies " XNL
        "directly or indirectly through you, then the only way you could satisfy both " XNL
        "it and this License would be to refrain entirely from distribution of the " XNL
        "Program." NL
        "" NL
        "If any portion of this section is held invalid or unenforceable under any " XNL
        "particular circumstance, the balance of the section is intended to apply and " XNL
        "the section as a whole is intended to apply in other circumstances." NL
        "" NL
        "It is not the purpose of this section to induce you to infringe any patents " XNL
        "or other property right claims or to contest validity of any such claims; " XNL
        "this section has the sole purpose of protecting the integrity of the free " XNL
        "software distribution system, which is implemented by public license " XNL
        "practices. Many people have made generous contributions to the wide range of " XNL
        "software distributed through that system in reliance on consistent " XNL
        "application of that system; it is up to the author/donor to decide if he or " XNL
        "she is willing to distribute software through any other system and a " XNL
        "licensee cannot impose that choice." NL
        "" NL
        "This section is intended to make thoroughly clear what is believed to be a " XNL
        "consequence of the rest of this License." NL
        "" NL
        "8. If the distribution and/or use of the Program is restricted in certain " XNL
        "countries either by patents or by copyrighted interfaces, the original " XNL
        "copyright holder who places the Program under this License may add an " XNL
        "explicit geographical distribution limitation excluding those countries, so " XNL
        "that distribution is permitted only in or among countries not thus excluded. " XNL
        "In such case, this License incorporates the limitation as if written in the " XNL
        "body of this License." NL
        "" NL
        "9. The Free Software Foundation may publish revised and/or new versions of " XNL
        "the General Public License from time to time. Such new versions will be " XNL
        "similar in spirit to the present version, but may differ in detail to " XNL
        "address new problems or concerns." NL
        "" NL
        "Each version is given a distinguishing version number. If the Program " XNL
        "specifies a version number of this License which applies to it and \"any " XNL
        "later version\", you have the option of following the terms and conditions " XNL
        "either of that version or of any later version published by the Free " XNL
        "Software Foundation. If the Program does not specify a version number of " XNL
        "this License, you may choose any version ever published by the Free Software " XNL
        "Foundation." NL
        "" NL
        "10. If you wish to incorporate parts of the Program into other free programs " XNL
        "whose distribution conditions are different, write to the author to ask for " XNL
        "permission. For software which is copyrighted by the Free Software " XNL
        "Foundation, write to the Free Software Foundation; we sometimes make " XNL
        "exceptions for this. Our decision will be guided by the two goals of "XNL
        "preserving the free status of all derivatives of our free software and of " XNL
        "promoting the sharing and reuse of software generally." NL
        "" NL
        "NO WARRANTY" NL
        "" NL
        "11. BECAUSE THE PROGRAM IS LICENSED FREE OF CHARGE, THERE IS NO WARRANTY FOR " XNL
        "THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN " XNL
        "OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES " XNL
        "PROVIDE THE PROGRAM \"AS IS\" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED " XNL
        "OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF " XNL
        "MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO " XNL
        "THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM " XNL
        "PROVE DEFECTIVE, YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR " XNL
        "CORRECTION." NL
        "" NL
        "12. IN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW OR AGREED TO IN WRITING " XNL
        "WILL ANY COPYRIGHT HOLDER, OR ANY OTHER PARTY WHO MAY MODIFY AND/OR " XNL
        "REDISTRIBUTE THE PROGRAM AS PERMITTED ABOVE, BE LIABLE TO YOU FOR DAMAGES, " XNL
        "INCLUDING ANY GENERAL, SPECIAL, INCIDENTAL OR CONSEQUENTIAL DAMAGES ARISING " XNL
        "OUT OF THE USE OR INABILITY TO USE THE PROGRAM (INCLUDING BUT NOT LIMITED TO " XNL
        "LOSS OF DATA OR DATA BEING RENDERED INACCURATE OR LOSSES SUSTAINED BY YOU OR " XNL
        "THIRD PARTIES OR A FAILURE OF THE PROGRAM TO OPERATE WITH ANY OTHER " XNL
        "PROGRAMS), EVEN IF SUCH HOLDER OR OTHER PARTY HAS BEEN ADVISED OF THE " XNL
        "POSSIBILITY OF SUCH DAMAGES." NL;
}

#ifndef NO_IDEA
static char * IDEA_licence(void)
{ 
    return
        "This Software/Hardware product contains the algorithm IDEA as described and " XNL
        "claimed in US Patent No. 5,214,703, EPO Patent No. 0482154 and filed " XNL
        "Japanese Patent Application No. 508119/1991 \"Device for the conversion of " XNL
        "a digital block and use of same\" (hereinafter referred to as \"Algorithm\"). " XNL
        "Any use of the Algorithm for Commercial Purposes is thus subject to a " XNL
        "license from Ascom Systec Ltd. of CH-5506 Mgenwil (Switzerland), being the " XNL
        "patentee and sole owner of all rights, including the term IDEA.  Commercial " XNL
        "Purposes shall mean any revenue generating purpose including but not limited " XNL
        "to" NL
        "i) using the Algorithm for company internal purposes (subject to a Site " XNL
        "License)." NL
        "ii) incorporating an application software containing the Algorithm into any " XNL
        "hardware and/or software and distributing such hardware and/or software " XNL
        "and/or providing services related thereto to others (subject to a Product " XNL
        "License)." NL
        "iii) using a product containing an application software that uses the " XNL
        "Algorithm (subject to an End-User License), except in case where such " XNL
        "End-User has acquired an implied license by purchasing the said product " XNL
        "from an authorized licensee or where the End-User has already signed up for " XNL
        "a Site License.  All such commercial license agreements are available " XNL
        "exclusively from Ascom Systec Ltd. and may be requested via the Internet " XNL
        "World Wide Web at http://www.ascom.ch/systec or by sending an electronic " XNL
        "mail to IDEA@ascom.ch. " NL
        "Any misuse will be prosecuted." NL
        "Use other than for Commercial Purposes is strictly limited to data transfer " XNL
        "between private individuals and not serving Commercial Purposes. The use by " XNL
        "government agencies, non-profit organizations etc. is considered as use for " XNL
        "Commercial Purposes but may be subject to special conditions. Requests for " XNL
        "waivers for non-commercial use (e.g. by software developers) are welcome. " NL;
}
#endif

char * licence_text(licence_id which)
{
    switch(which)
    {
    case GNU_GPL:
        return GNU_General_Public_Licence();
#ifndef NO_IDEA
    case IDEA:
        return IDEA_licence();
#endif
    default:
        return (char*)0;
    }
}

/* end of file licences.c */
