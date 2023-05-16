/***************************************************************************
                          ctcjava_CJctclib.cpp  -  description
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
/* ctcjava_ctcjava_CJctclib.cpp
 *
 *  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1998
 *  All rights reserved.  For full licence details see file licences.c
 */
#include "com_ravnaandtines_ctcjava_CJctclib.h"
#include "ctcjlib.h"
#include "ctc.h"
#include "port_io.h"
#include "utfcode.h"
#include <string.h>
#include <locale.h>

#if defined(CTC_NAMESPACE_SUPPORT)

using CTCjlib::setEnvironment;
using CTCjlib::getContext;
using CTCjlib::getInsts;
using CTCjlib::vf_java;
using CTCjlib::vf_unjava;
using CTCjlib::backupKeyrings;
using CTCjlib::writeKeyrings;
using CTCjlib::wrapSeckey;
using CTCjlib::clearInternalisedKeys;


using CTClib::DataFileP;
using CTClib::examine;
using CTClib::examine_text;

using CTClib::REVEALKEY;
using CTClib::DECODE;
using CTClib::makeSessionData;
using CTClib::encrypt;
using CTClib::signOnly;

using CTClib::makePKEkey;
using CTClib::seckey;
using CTClib::insertSeckey;
using CTClib::removeSeckey;
using CTClib::keyType;
using CTClib::prime_method;
using CTClib::seckey;

#endif

/*
 * Class:     ctcjava_CJctclib
 * Method:    examine
 * Signature: (Lcom/ravnaandtines/ctcjava/CJTempfile;)I
 */
extern "C" JNIEXPORT jint JNICALL Java_com_ravnaandtines_ctcjava_CJctclib_examine
(JNIEnv * env, jclass, jobject jfile, jboolean split)
{
    // ensure default locale
    setlocale(LC_ALL, "");
    setEnvironment(env);
    DataFileP input = vf_java(env, jfile);
    getContext()->splitkey = (byte) (split?REVEALKEY:DECODE);
    int state = examine(input, getContext());
    vf_unjava(input);
    return state;
}

/*
 * Class:     ctcjava_CJctclib
 * Method:    examine_text
 * Signature: (Lcom/ravnaandtines/ctcjava/CJTempfile;)I
 */
extern "C" JNIEXPORT jint JNICALL Java_com_ravnaandtines_ctcjava_CJctclib_examine_1text
(JNIEnv * env, jclass, jobject jfile, jboolean split)
{
    // ensure default locale
    setlocale(LC_ALL, "");
    setEnvironment(env);
    DataFileP input = vf_java(env, jfile, true);

    getContext()->splitkey = (byte) (split?REVEALKEY:DECODE);
    int state = examine_text(input, getContext());
    vf_unjava(input);
    return state;
}

/*
 * Class:     ctcjava_CJctclib
 * Method:    encrypt
 * Signature: (Lcom/ravnaandtines/ctcjava/CJTempfile;Lcom/ravnaandtines/ctcjava/CJTempfile;)Z
 */
extern "C" JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_CJctclib_encrypt
(JNIEnv * env, jclass, jobject jfile, jobject jout)
{
    // ensure default locale
    setlocale(LC_ALL, "");
    setEnvironment(env);
    DataFileP input = vf_java(env, jfile, true);
    DataFileP output = vf_java(env, jout, true);

    // get session data as required
    makeSessionData(input);

    jboolean state = encrypt(input, output, getInsts());

    vf_unjava(input);
    vf_unjava(output);

    return state;
}

/*
 * Class:     ctcjava_CJctclib
 * Method:    signOnly
 * Signature: (Lcom/ravnaandtines/ctcjava/CJTempfile;Lcom/ravnaandtines/ctcjava/CJTempfile;)Z
 */
extern "C" JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_CJctclib_signOnly
(JNIEnv * env, jclass, jobject jfile, jobject jout)
{
    // ensure default locale
    setlocale(LC_ALL, "");
    setEnvironment(env);
    DataFileP input = vf_java(env, jfile, true);
    DataFileP output = vf_java(env, jout, true);

    // get session data as required
    makeSessionData(input);

    jboolean state = signOnly(input, output, getInsts());

    vf_unjava(input);
    vf_unjava(output);

    return state;
}

/*
 * Class:     ctcjava_CJctclib
 * Method:    makePKEkey
 * Signature: (BIILjava/lang/String;BB[B)Lcom/ravnaandtines/ctcjava/CJSeckey;
 */
extern "C" JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_CJctclib_makePKEkey
(JNIEnv * env, jclass, jbyte pkalg, jint keylength, jint prime,
jstring jname, jbyte selfSignAlg, jbyte kpAlg, jbyteArray passphrase)
{
    // ensure default locale
    setlocale(LC_ALL, "");
    setEnvironment(env);
    keyType details;
    details.algorithm = (byte)pkalg;
    details.keyLength = (uint16_t) keylength;
    details.method = (prime_method) prime;
    details.publicExponent = 17;
    details.selfSignAlg = (byte) selfSignAlg;
    details.kpAlg = (byte) kpAlg;

    char * name = jstringToMbs(env, jname);
    if(strlen(name) < 256)
    {
        strcpy(details.name, name);
    }
    else
    {
        strncpy(details.name, name, 255);
        details.name[255] = 0;
    }
    delete [] name;

    jint sz = env->GetArrayLength(passphrase);
    memset(details.passphrase, 0, 256);
    if(sz > 255) sz = 255;
    env->GetByteArrayRegion(passphrase, 0, sz, (jbyte*)details.passphrase);

    seckey * newKey = makePKEkey(getContext()->keyRings, &details);
    if(newKey)
    {
        backupKeyrings(); // do backup of unchanged ring
        insertSeckey(getContext()->keyRings, newKey);
    }
    return wrapSeckey(env, newKey);
}

extern "C" JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_CJctclib_lock
(JNIEnv *, jclass)
{
    clearInternalisedKeys();
}

extern "C" JNIEXPORT jstring JNICALL Java_com_ravnaandtines_ctcjava_CJGlobals_libVersion
(JNIEnv * env, jclass)
{
    setlocale(LC_ALL, NULL); // ASCII text => 'C' locale
    return mbsToJstring(env, VERSIONNO);
}

