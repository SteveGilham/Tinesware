/***************************************************************************
                          ctcjava_CJdecodeContext.cpp  -  description
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
/* ctcjava_CJdecodeContext.cpp
 *
 *  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1998
 *  All rights reserved.  For full licence details see file licences.c
 */
#include "com_ravnaandtines_ctcjava_CJdecodeContext.h"
#include "ctc.h"
#include "ctcjlib.h"

#if defined(CTC_NAMESPACE_SUPPORT)

using CTClib::decode_context;
using CTClib::seckey;
using CTClib::pubkey;
using CTClib::firstSecKey;
using CTClib::firstPubKey;
using CTCjlib::setEnvironment;

#endif

/*
 * Class:     ctcjava_CJdecodeContext
 * Method:    getFirstPubkey
 * Signature: (J)Lcom/ravnaandtines/ctcjava/CJPubkey;
 */
extern "C" JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_CJdecodeContext_getFirstPubkey
(JNIEnv * env, jobject , jlong jptr)
{
    setEnvironment(env);
    decode_context * context = (decode_context *)jptr;
    pubkey * pub_key = firstPubKey(context->keyRings);

    // construct a CJPubkey to wrap the key with
    jlong key = (jlong)pub_key;
    jclass pubkeyClass = env->FindClass("com/ravnaandtines/ctcjava/CJPubkey");
    jmethodID init = env->GetMethodID(pubkeyClass, 
    "<init>", "(J)V");
    return env->NewObject(pubkeyClass, init, key);
}

/*
 * Class:     ctcjava_CJdecodeContext
 * Method:    getFirstSeckey
 * Signature: (J)Lcom/ravnaandtines/ctcjava/CJSeckey;
 */
extern "C" JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_CJdecodeContext_getFirstSeckey
(JNIEnv * env, jobject, jlong jptr)
{
    setEnvironment(env);
    decode_context * context = (decode_context *)jptr;
    seckey * sec_key = firstSecKey(context->keyRings);

    // construct a CJSeckey to wrap the key with
    jlong key = (jlong)sec_key;
    jclass seckeyClass = env->FindClass("com/ravnaandtines/ctcjava/CJSeckey");
    jmethodID init = env->GetMethodID(seckeyClass, 
    "<init>", "(J)V");
    return env->NewObject(seckeyClass, init, key);
}
