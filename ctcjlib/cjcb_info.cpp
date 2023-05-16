/***************************************************************************
                          cjcb_info.cpp  -  description
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
/* cjcb_info.cpp
 *
 *  This is an JNI-based implementation of CTC's ctcfyi module.
 *
 *  These are simple informational callbacks
 *
 *  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1998
 *  All rights reserved.  For full licence details see file licences.c
 */

#include <jni.h>
#include "callback.h"
#include "ctcjlib.h"
#include "utfcode.h"

namespace CTClib {

    using CTCjlib::getEnvironment;

    /* These should both also expand code and context */
    extern "C" continue_action cb_exception(cb_condition * condition)
    {
        jclass fileclass = getEnvironment()->FindClass("com/ravnaandtines/ctcjava/GlobalData");
        jmethodID jcb  = getEnvironment()->GetStaticMethodID(fileclass, 
        "cbException", "(ILjava/lang/String;Lcom/ravnaandtines/ctcjava/NativePublicKey;)Z");

        jint code = (condition->severity<<24)
            |(condition->module<<16)
                |(condition->code << 8)
                    |(condition->context); // assumed all in the 0-255 range

        jstring v = mbsToJstring(getEnvironment(), condition->text? condition->text : "");

        // construct a NativePublicKey to wrap the key with
        jlong key = (jlong)condition->pub_key;
        jclass pubkeyClass = getEnvironment()->FindClass("com/ravnaandtines/ctcjava/NativePublicKey");
        jmethodID init = getEnvironment()->GetMethodID(pubkeyClass, 
        "<init>", "(J)V");
        jobject pubkey = getEnvironment()->NewObject(pubkeyClass, init, key);

        // continue or abort
        jboolean gotOK = getEnvironment()->CallStaticBooleanMethod(fileclass, jcb,
        code, v, pubkey);

        getEnvironment()->DeleteLocalRef(v);
        getEnvironment()->DeleteLocalRef(pubkey);
        return gotOK ? CB_CONTINUE : CB_ABORT;
    }

    extern "C" void cb_information(cb_condition * condition)
    {
        jclass fileclass = getEnvironment()->FindClass("com/ravnaandtines/ctcjava/GlobalData");
        jmethodID jcb  = getEnvironment()->GetStaticMethodID(fileclass, 
        "cbInformation", "(ILjava/lang/String;Lcom/ravnaandtines/ctcjava/NativePublicKey;)V");
        // just do it


        jint code = (condition->severity<<24)
            |(condition->module<<16)
                |(condition->code << 8)
                    |(condition->context); // assumed all in the 0-255 range
        jstring v = mbsToJstring(getEnvironment(), condition->text? condition->text : "");

        // construct a NativePublicKey to wrap the key with
        jlong key = (jlong)condition->pub_key;
        jclass pubkeyClass = getEnvironment()->FindClass("com/ravnaandtines/ctcjava/NativePublicKey");
        jmethodID init = getEnvironment()->GetMethodID(pubkeyClass, 
        "<init>", "(J)V");
        jobject pubkey = getEnvironment()->NewObject(pubkeyClass, init, key);

        getEnvironment()->CallStaticVoidMethod(fileclass, jcb,
        code, v, pubkey);

        getEnvironment()->DeleteLocalRef(v);
        getEnvironment()->DeleteLocalRef(pubkey);
    }

} // end namespace CTClib
/* end of file cbinfo.c */




