/***************************************************************************
 *                           com_ravnaandtines_ctcjava_NativeSecretKey.h  -  description
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
/* Header for class com_ravnaandtines_ctcjava_NativeSecretKey */

#ifndef _Included_com_ravnaandtines_ctcjava_NativeSecretKey
#define _Included_com_ravnaandtines_ctcjava_NativeSecretKey
#ifdef __cplusplus
extern "C" {
#endif
    /*
     * Class:     com_ravnaandtines_ctcjava_NativeSecretKey
     * Method:    getNextSeckey
     * Signature: (J)Lcom/ravnaandtines/ctcjava/NativeSecretKey;
     */
    JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_NativeSecretKey_getNextSeckey
        (JNIEnv *, jobject, jlong);

    /*
     * Class:     com_ravnaandtines_ctcjava_NativeSecretKey
     * Method:    pub
     * Signature: (J)Lcom/ravnaandtines/ctcjava/NativePublicKey;
     */
    JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_NativeSecretKey_pub
        (JNIEnv *, jobject, jlong);

    /*
     * Class:     com_ravnaandtines_ctcjava_NativeSecretKey
     * Method:    keyRevoke
     * Signature: (JZ)Z
     */
    JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_NativeSecretKey_keyRevoke
        (JNIEnv *, jobject, jlong, jboolean);

    /*
     * Class:     com_ravnaandtines_ctcjava_NativeSecretKey
     * Method:    internalise
     * Signature: (J)V
     */
    JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_NativeSecretKey_internalise
        (JNIEnv *, jobject, jlong);

    /*
     * Class:     com_ravnaandtines_ctcjava_NativeSecretKey
     * Method:    externalise
     * Signature: (J)Z
     */
    JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_NativeSecretKey_externalise
        (JNIEnv *, jobject, jlong);

    /*
     * Class:     com_ravnaandtines_ctcjava_NativeSecretKey
     * Method:    isExternalised
     * Signature: (J)Z
     */
    JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_NativeSecretKey_isExternalised
        (JNIEnv *, jobject, jlong);

    /*
     * Class:     com_ravnaandtines_ctcjava_NativeSecretKey
     * Method:    addSignUser
     * Signature: (JLjava/lang/String;)V
     */
    JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_NativeSecretKey_addSignUser
        (JNIEnv *, jobject, jlong, jstring);

    /*
     * Class:     com_ravnaandtines_ctcjava_NativeSecretKey
     * Method:    updatePassphrase
     * Signature: (JBB[B)V
     */
    JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_NativeSecretKey_updatePassphrase
        (JNIEnv *, jobject, jlong, jbyte, jbyte, jbyteArray);

#ifdef __cplusplus
}
#endif
#endif
