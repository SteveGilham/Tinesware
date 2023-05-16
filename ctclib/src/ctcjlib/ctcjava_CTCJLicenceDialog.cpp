/***************************************************************************
                          ctcjava_CTCJLicenceDialog.cpp  -  description
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
/* ctcjava_CTCLicenceDialog.cpp 
 *
 *  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 *  Heimdall <heimdall@bifroest.demon.co.uk>  1998
 *  All rights reserved.  For full licence details see file licences.c
 */
#include "com_ravnaandtines_ctcjava_CTCJLicenceDialog.h"
#include "basic.h"
#include "licences.h"
#include "utfcode.h"
#include <locale.h>

#if defined(CTC_NAMESPACE_SUPPORT)

using CTClib::licence_text;

#endif


/*
 * Class:     ctcjava_CTCLicenceDialog
 * Method:    getGPL
 * Signature: ()Ljava/lang/String;
 */
extern "C" JNIEXPORT jstring JNICALL Java_com_ravnaandtines_ctcjava_CTCJLicenceDialog_getGPL
(JNIEnv * env, jobject)
{
    setlocale(LC_ALL, NULL); // ASCII text => 'C' locale
    const char * data = licence_text(CTClib::GNU_GPL);
    return mbsToJstring(env, data);
}

/*
 * Class:     ctcjava_CTCLicenceDialog
 * Method:    getIDEA
 * Signature: ()Ljava/lang/String;
 */
extern "C" JNIEXPORT jstring JNICALL Java_com_ravnaandtines_ctcjava_CTCJLicenceDialog_getIDEA
(JNIEnv * env, jobject)
{
    setlocale(LC_ALL, NULL); // ASCII text => 'C' locale
    const char * data = licence_text(CTClib::IDEA);
    return mbsToJstring(env, data);
}
