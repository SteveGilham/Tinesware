/***************************************************************************
                          ctcjlib.cpp  -  description
                             -------------------
    copyright            : (C) 1998 by Mr. Tines
    email                : tines@ravnaandtines.com
 ***************************************************************************/

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
//---------------------------------------------------------------------------
#include <vcl.h>
#pragma hdrstop
//---------------------------------------------------------------------------
//Important note about DLL memory management when your DLL uses the
//static version of the RunTime Library:
//
//If your DLL exports any functions that pass String objects (or structs/
//classes containing nested Strings) as parameter or function results,
//you will need to add the library MEMMGR.LIB to both the DLL project and
//any other projects that use the DLL. You will also need to use MEMMGR.LIB
//if any other projects which use the DLL will be perfomring new or delete
//operations on any non-TObject-derived classes which are exported from the
//DLL. Adding MEMMGR.LIB to your project will change the DLL and its calling
//EXE's to use the BORLNDMM.DLL as their memory manager.  In these cases,
//   the file BORLNDMM.DLL should be deployed along with your DLL.
//
//   To avoid using BORLNDMM.DLL, pass string information using "char *" or
//   ShortString parameters.
//
//   If your DLL uses the dynamic version of the RTL, you do not need to
//   explicitly add MEMMGR.LIB as this will be done implicitly for you
//---------------------------------------------------------------------------
USEUNIT("..\ctcalg\tea.c");
USEUNIT("..\ctcalg\blowfish.c");
USEUNIT("..\ctcalg\cast5.c");
USEUNIT("..\ctcalg\cipher.c");
USEUNIT("..\ctcalg\des.c");
USEUNIT("..\ctcalg\des3keys.c");
USEUNIT("..\ctcalg\des3port.c");
USEUNIT("..\ctcalg\hash.c");
USEUNIT("..\ctcalg\hashpass.c");
USEUNIT("..\ctcalg\haval.c");
USEUNIT("..\ctcalg\haval_i3.c");
USEUNIT("..\ctcalg\haval_i4.c");
USEUNIT("..\ctcalg\haval_i5.c");
USEUNIT("..\ctcalg\idea.c");
USEUNIT("..\ctcalg\md5.c");
USEUNIT("..\ctcalg\random.c");
USEUNIT("..\ctcalg\rmd160.c");
USEUNIT("..\ctcalg\safer.c");
USEUNIT("..\ctcalg\sha.c");
USEUNIT("..\ctcalg\square.c");
USEUNIT("..\ctcalg\3way.c");
USEUNIT("..\ctccpa\ziputils.c");
USEUNIT("..\ctccpa\compand.c");
USEUNIT("..\ctccpa\deflate.c");
USEUNIT("..\ctccpa\inflate.c");
USEUNIT("..\ctccpa\splay.c");
USEUNIT("..\ctccpa\trees.c");
USEUNIT("..\ctccpa\bits.c");
USEUNIT("..\ctckey\keyutils.c");
USEUNIT("..\ctckey\keyio.c");
USEUNIT("..\ctckey\keyhash.c");
USEUNIT("..\ctclib\cleave.c");
USEUNIT("..\ctclib\ctc.c");
USEUNIT("..\ctclib\uuencode.c");
USEUNIT("..\ctclib\armour.c");
USEUNIT("..\ctcpka\rsa.c");
USEUNIT("..\ctcpka\ec_curve.c");
USEUNIT("..\ctcpka\ec_field.c");
USEUNIT("..\ctcpka\pkautils.c");
USEUNIT("..\ctcpka\pkbignum.c");
USEUNIT("..\ctcpka\pkcipher.c");
USEUNIT("..\ctcpka\pkops.c");
USEUNIT("..\ctcpka\ec_crypt.c");
USEUNIT("..\ctcutl\utils.c");
USEUNIT("..\ctcutl\licences.c");
USEUNIT("..\ctcutl\bignums.c");
USEUNIT("utfcode.cpp");
USEUNIT("cjcb_info.cpp");
USEUNIT("cjport_io.cpp");
USEUNIT("ctcjava_CJctclib.cpp");
USEUNIT("ctcjava_CJdecodeContext.cpp");
USEUNIT("ctcjava_CJencryptInsts.cpp");
USEUNIT("ctcjava_CJFilesPanel.cpp");
USEUNIT("ctcjava_CJPubkey.cpp");
USEUNIT("ctcjava_CTCIKeyConst.cpp");
USEUNIT("ctcjava_CTCJava.cpp");
USEUNIT("ctcjava_CTCJLicenceDialog.cpp");
USEUNIT("rawrand.cpp");
USEUNIT("usrbreak.cpp");
USEUNIT("cjcb_act.cpp");
USEUNIT("..\ctcfyi\keywords.c");
USEUNIT("..\ctcutl\widechar.c");
USEUNIT("..\ctcalg\rijndael.c");
USEUNIT("..\ctcalg\twofish.c");
USERES("ctcjlib.res");
USERC("ctcjlib.rc");
//---------------------------------------------------------------------------
int WINAPI DllEntryPoint(HINSTANCE , unsigned long , void*)
{
    /* ctcjlib.cpp
     *
     *  This is an auto-generated file used by Borland C++Builder.
     *
     *  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1998
     *  All rights reserved.  For full licence details see file licences.c
     */

    return 1;
}
//---------------------------------------------------------------------------
