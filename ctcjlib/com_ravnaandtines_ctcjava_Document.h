/***************************************************************************
 *                           com_ravnaandtines_ctcjava_Document.h  -  description
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
/* Header for class com_ravnaandtines_ctcjava_Document */

#ifndef _Included_com_ravnaandtines_ctcjava_Document
#define _Included_com_ravnaandtines_ctcjava_Document
#ifdef __cplusplus
extern "C" {
#endif
    /*
     * Class:     com_ravnaandtines_ctcjava_Document
     * Method:    examine
     * Signature: (Lcom/ravnaandtines/ctcjava/InMemoryPortIOFile;Z)I
     */
    JNIEXPORT jint JNICALL Java_com_ravnaandtines_ctcjava_Document_examine
        (JNIEnv *, jclass, jobject, jboolean);

    /*
     * Class:     com_ravnaandtines_ctcjava_Document
     * Method:    examine_text
     * Signature: (Lcom/ravnaandtines/ctcjava/InMemoryPortIOFile;Z)I
     */
    JNIEXPORT jint JNICALL Java_com_ravnaandtines_ctcjava_Document_examine_1text
        (JNIEnv *, jclass, jobject, jboolean);

    /*
     * Class:     com_ravnaandtines_ctcjava_Document
     * Method:    encrypt
     * Signature: (Lcom/ravnaandtines/ctcjava/InMemoryPortIOFile;Lcom/ravnaandtines/ctcjava/InMemoryPortIOFile;)Z
     */
    JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_Document_encrypt
        (JNIEnv *, jclass, jobject, jobject);

    /*
     * Class:     com_ravnaandtines_ctcjava_Document
     * Method:    signOnly
     * Signature: (Lcom/ravnaandtines/ctcjava/InMemoryPortIOFile;Lcom/ravnaandtines/ctcjava/InMemoryPortIOFile;)Z
     */
    JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_Document_signOnly
        (JNIEnv *, jclass, jobject, jobject);

    /*
     * Class:     com_ravnaandtines_ctcjava_Document
     * Method:    makePKEkey
     * Signature: (BIILjava/lang/String;BB[B)Lcom/ravnaandtines/ctcjava/NativeSecretKey;
     */
    JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_Application_makePKEkey
        (JNIEnv *, jclass, jbyte, jint, jint, jstring, jbyte, jbyte, jbyteArray);

    /*
     * Class:     com_ravnaandtines_ctcjava_Document
     * Method:    lock
     * Signature: ()V
     */
    JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_Application_lock
        (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
