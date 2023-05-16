/***************************************************************************
 *                           com_ravnaandtines_ctcjava_CJPubkey.h  -  description
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
/* Header for class com_ravnaandtines_ctcjava_CJPubkey */

#ifndef _Included_com_ravnaandtines_ctcjava_CJPubkey
#define _Included_com_ravnaandtines_ctcjava_CJPubkey
#ifdef __cplusplus
extern "C" {
#endif
    /*
     * Class:     com_ravnaandtines_ctcjava_CJPubkey
     * Method:    getNextPubkey
     * Signature: (J)Lcom/ravnaandtines/ctcjava/CJPubkey;
     */
    JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_getNextPubkey
        (JNIEnv *, jobject, jlong);

    /*
     * Class:     com_ravnaandtines_ctcjava_CJPubkey
     * Method:    name_from_key
     * Signature: (J)Ljava/lang/String;
     */
    JNIEXPORT jstring JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_name_1from_1key
        (JNIEnv *, jobject, jlong);

    /*
     * Class:     com_ravnaandtines_ctcjava_CJPubkey
     * Method:    isEnabled
     * Signature: (J)Z
     */
    JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_isEnabled
        (JNIEnv *, jobject, jlong);

    /*
     * Class:     com_ravnaandtines_ctcjava_CJPubkey
     * Method:    isRevoked
     * Signature: (J)Z
     */
    JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_isRevoked
        (JNIEnv *, jobject, jlong);

    /*
     * Class:     com_ravnaandtines_ctcjava_CJPubkey
     * Method:    keyExtract
     * Signature: (J)Z
     */
    JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_keyExtract
        (JNIEnv *, jobject, jlong);

    /*
     * Class:     com_ravnaandtines_ctcjava_CJPubkey
     * Method:    keySign
     * Signature: (JJB)V
     */
    JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_keySign
        (JNIEnv *, jobject, jlong, jlong, jbyte);

    /*
     * Class:     com_ravnaandtines_ctcjava_CJPubkey
     * Method:    keyAble
     * Signature: (J)V
     */
    JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_keyAble
        (JNIEnv *, jobject, jlong);

    /*
     * Class:     com_ravnaandtines_ctcjava_CJPubkey
     * Method:    keyDelete
     * Signature: (J)V
     */
    JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_keyDelete
        (JNIEnv *, jobject, jlong);

    /*
     * Class:     com_ravnaandtines_ctcjava_CJPubkey
     * Method:    keySize
     * Signature: (J)I
     */
    JNIEXPORT jint JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_keySize
        (JNIEnv *, jobject, jlong);

    /*
     * Class:     com_ravnaandtines_ctcjava_CJPubkey
     * Method:    keyAlg
     * Signature: (J)I
     */
    JNIEXPORT jint JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_keyAlg
        (JNIEnv *, jobject, jlong);

    /*
     * Class:     com_ravnaandtines_ctcjava_CJPubkey
     * Method:    keyID
     * Signature: (J)J
     */
    JNIEXPORT jlong JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_keyID
        (JNIEnv *, jobject, jlong);

    /*
     * Class:     com_ravnaandtines_ctcjava_CJPubkey
     * Method:    keyDate
     * Signature: (J)I
     */
    JNIEXPORT jint JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_keyDate
        (JNIEnv *, jobject, jlong);

    /*
     * Class:     com_ravnaandtines_ctcjava_CJPubkey
     * Method:    keyPrint
     * Signature: (J)Ljava/lang/String;
     */
    JNIEXPORT jstring JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_keyPrint
        (JNIEnv *, jobject, jlong);

    /*
     * Class:     com_ravnaandtines_ctcjava_CJPubkey
     * Method:    sec
     * Signature: (J)Lcom/ravnaandtines/ctcjava/CJSeckey;
     */
    JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_sec
        (JNIEnv *, jobject, jlong);

    /*
     * Class:     com_ravnaandtines_ctcjava_CJPubkey
     * Method:    signature
     * Signature: (J)Lcom/ravnaandtines/ctcjava/Signature;
     */
    JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_signature
        (JNIEnv *, jobject, jlong);

    /*
     * Class:     com_ravnaandtines_ctcjava_CJPubkey
     * Method:    subkey
     * Signature: (J)Lcom/ravnaandtines/ctcjava/CJPubkey;
     */
    JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_subkey
        (JNIEnv *, jobject, jlong);

    /*
     * Class:     com_ravnaandtines_ctcjava_CJPubkey
     * Method:    username
     * Signature: (J)Lcom/ravnaandtines/ctcjava/CJPubkey;
     */
    JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_username
        (JNIEnv *, jobject, jlong);

    /*
     * Class:     com_ravnaandtines_ctcjava_CJPubkey
     * Method:    check
     * Signature: (JJJ)Z
     */
    JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_check
        (JNIEnv *, jobject, jlong, jlong, jlong);

#ifdef __cplusplus
}
#endif
#endif
