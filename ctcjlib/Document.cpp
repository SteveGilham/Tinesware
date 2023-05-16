/***************************************************************************
                          ctcjava_Document.cpp  -  description
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
/* ctcjava_ctcjava_Document.cpp
 *
 *  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1998
 *  All rights reserved.  For full licence details see file licences.c
 */
#include "com_ravnaandtines_ctcjava_Document.h"
#include "com_ravnaandtines_ctcjava_CTC.h"
#include "ctcjlib.h"
#include "ctc.h"
#include "port_io.h"
#include "utfcode.h"
#include <string.h>
#include <locale.h>

using CTCjlib::setEnvironment;
using CTCjlib::getContext;
using CTCjlib::getInsts;
using CTCjlib::backupKeyrings;
using CTCjlib::writeKeyrings;
using CTCjlib::wrapSeckey;
using CTCjlib::clearInternalisedKeys;
using CTCjlib::VirtualFile;

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


static JNIEnv * theEnv = 0;

namespace CTCjlib {

JNIEnv * getEnvironment(void)
{
    return theEnv;
}

void setEnvironment(JNIEnv * env)
{
    theEnv = env;
}

} // namespace CTCjlib

extern "C" JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_CTC_registerJVM
(JNIEnv * env, jclass)
{
    theEnv = env;
}

/*
 * Class:     ctcjava_Document
 * Method:    examine
 * Signature: (Lcom/ravnaandtines/ctcjava/InMemoryPortIOFile;)I
 */
extern "C" JNIEXPORT jint JNICALL Java_com_ravnaandtines_ctcjava_Document_examine
(JNIEnv * env, jclass, jobject jfile, jboolean split)
{
    // ensure default locale
    setlocale(LC_ALL, "");
    setEnvironment(env);
    VirtualFile input(env, jfile, false);
    getContext()->splitkey = (byte) (split?REVEALKEY:DECODE);
    int state = examine(input, getContext());
    return state;
}

/*
 * Class:     ctcjava_Document
 * Method:    examine_text
 * Signature: (Lcom/ravnaandtines/ctcjava/InMemoryPortIOFile;)I
 */
extern "C" JNIEXPORT jint JNICALL Java_com_ravnaandtines_ctcjava_Document_examine_1text
(JNIEnv * env, jclass, jobject jfile, jboolean split)
{
    // ensure default locale
    setlocale(LC_ALL, "");
    setEnvironment(env);
    VirtualFile input(env, jfile, true);

    getContext()->splitkey = (byte) (split?REVEALKEY:DECODE);
    int state = examine_text(input, getContext());

    return state;
}

/*
 * Class:     ctcjava_Document
 * Method:    encrypt
 * Signature: (Lcom/ravnaandtines/ctcjava/InMemoryPortIOFile;Lcom/ravnaandtines/ctcjava/InMemoryPortIOFile;)Z
 */
extern "C" JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_Document_encrypt
(JNIEnv * env, jclass, jobject jfile, jobject jout)
{
    // ensure default locale
    setlocale(LC_ALL, "");
    setEnvironment(env);
    VirtualFile input(env, jfile);
    VirtualFile output(env, jout);

    // get session data as required
    makeSessionData(input);

    jboolean state = encrypt(input, output, getInsts());

    return state;
}

/*
 * Class:     ctcjava_Document
 * Method:    signOnly
 * Signature: (Lcom/ravnaandtines/ctcjava/InMemoryPortIOFile;Lcom/ravnaandtines/ctcjava/InMemoryPortIOFile;)Z
 */
extern "C" JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_Document_signOnly
(JNIEnv * env, jclass, jobject jfile, jobject jout)
{
    // ensure default locale
    setlocale(LC_ALL, "");
    setEnvironment(env);
    VirtualFile input(env, jfile);
    VirtualFile output(env, jout);

    // get session data as required
    makeSessionData(input);

    jboolean state = signOnly(input, output, getInsts());

    return state;
}

/*
 * Class:     ctcjava_Document
 * Method:    makePKEkey
 * Signature: (BIILjava/lang/String;BB[B)Lcom/ravnaandtines/ctcjava/NativeSecretKey;
 */
extern "C" JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_Application_makePKEkey
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

    std::vector<char> name;
        
    jstringToMbs(env, jname, name);
    if(strlen(&name[0]) < 256)
    {
        strcpy(details.name, &name[0]);
    }
    else
    {
        strncpy(details.name, &name[0], 255);
        details.name[255] = 0;
    }

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

extern "C" JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_Application_lock
(JNIEnv *, jclass)
{
    clearInternalisedKeys();
}

extern "C" JNIEXPORT jstring JNICALL Java_com_ravnaandtines_ctcjava_GlobalData_libVersion
(JNIEnv * env, jclass)
{
    setlocale(LC_ALL, NULL); // ASCII text => 'C' locale
    return mbsToJstring(env, VERSIONNO);
}

