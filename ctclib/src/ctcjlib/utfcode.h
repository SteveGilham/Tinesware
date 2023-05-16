/***************************************************************************
 *                           utfcode.h  -  description
 *                              -------------------
 *     copyright            : (C) 1998 by Mr. Tines
 *     email                : tines@ravnaandtines.com
 ***************************************************************************/

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
/* utfcode.h
 *
 *  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 *  Heimdall <heimdall@bifroest.demon.co.uk>  1998
 *  All rights reserved.  For full licence details see file licences.c
 */


#ifndef _utfcode_h
#define _utfcode_h

#ifndef __cplusplus
#error "This include, utfcode.h,  file is C++ only"
#endif

#include <jni.h>

//utf code conversion
wchar_t * utfToUnicode(const char * utf);
char * utfToMbs(const char * utf);

//jstring to multi-byte interconversion
//assumes that jchar and wchar_t are identical
char * jstringToMbs(JNIEnv * env, jstring s);
jstring mbsToJstring(JNIEnv *env, const char *mbstr);


#endif // ndef _utfcode_h

