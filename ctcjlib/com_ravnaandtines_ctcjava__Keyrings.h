/***************************************************************************
 *                           com_ravnaandtines_ctcjava__Keyrings.h  -  description
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

#include <jni.h>
/* Header for class com_ravnaandtines_ctcjava_PublicKeyRoot and class com_ravnaandtines_ctcjava_SecretKeyRoot */

#ifndef _Included_com_ravnaandtines_ctcjava__Keyrings
#define _Included_com_ravnaandtines_ctcjava__Keyrings
#ifdef __cplusplus
extern "C" {
#endif

    /*
    * Class:     com_ravnaandtines_ctcjava_PublicKeyRoot
    * Method:    clearKeyrings
    * Signature: ()Z
    */
    JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_PublicKeyRoot_clearKeyrings
    (JNIEnv *, jobject);

    /*
    * Class:     com_ravnaandtines_ctcjava_PublicKeyRoot
    * Method:    readPubring
    * Signature: (Ljava/lang/String;)J
    */
    JNIEXPORT jlong JNICALL Java_com_ravnaandtines_ctcjava_PublicKeyRoot_readPubring
    (JNIEnv *, jobject, jstring);

    /*
    * Class:     com_ravnaandtines_ctcjava_PublicKeyRoot
    * Method:    getFirstPubkey
    * Signature: (J)Lcom/ravnaandtines/ctcjava/NativePublicKey;
    */
    JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_PublicKeyRoot_getFirstPubkey
    (JNIEnv *, jobject, jlong);

    /*
    * Class:     com_ravnaandtines_ctcjava_PublicKeyRoot
    * Method:    getFirstSeckey
    * Signature: (J)Lcom/ravnaandtines/ctcjava/NativeSecretKey;
    */
    JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_PublicKeyRoot_getFirstSeckey
    (JNIEnv *, jobject, jlong);








    /*
     * Class:     com_ravnaandtines_ctcjava_SecretKeyRoot
     * Method:    readSecring
     * Signature: (Ljava/lang/String;)Z
     */
    JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_SecretKeyRoot_readSecring
        (JNIEnv *, jobject, jstring);



#ifdef __cplusplus
}
#endif
#endif
