/***************************************************************************
 *                           com_ravnaandtines_ctcjava_NativeSignature.h  -  description
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
/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_ravnaandtines_ctcjava_NativeSignature */

#ifndef _Included_com_ravnaandtines_ctcjava_NativeSignature
#define _Included_com_ravnaandtines_ctcjava_NativeSignature
#ifdef __cplusplus
extern "C" {
#endif
    /*
     * Class:     com_ravnaandtines_ctcjava_NativeSignature
     * Method:    getNextNativeSignature
     * NativeSignature: (J)Lcom/ravnaandtines/ctcjava/NativeSignature;
     */
    JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_NativeSignature_getNextSignature
        (JNIEnv *, jobject, jlong);

    /*
     * Class:     com_ravnaandtines_ctcjava_NativeSignature
     * Method:    name
     * NativeSignature: (J)Ljava/lang/String;
     */
    JNIEXPORT jstring JNICALL Java_com_ravnaandtines_ctcjava_NativeSignature_name
        (JNIEnv *, jobject, jlong);

    /*
     * Class:     com_ravnaandtines_ctcjava_NativeSignature
     * Method:    pub
     * NativeSignature: (J)Lcom/ravnaandtines/ctcjava/NativePublicKey;
     */
    JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_NativeSignature_pub
        (JNIEnv *, jobject, jlong);

#ifdef __cplusplus
}
#endif
#endif
