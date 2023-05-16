/***************************************************************************
                          utfcode.cpp  -  description
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
/* utfcode.cpp
 *
 *  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 *  Heimdall <heimdall@bifroest.demon.co.uk>  1998
 *  All rights reserved.  For full licence details see file licences.c
 *
 *  This module gathers together all the encoding-based transformations
 *  between bytes and jchars done on the 'C' side of the barrier, using
 *  the ANSI wcstombs() and mbstowcs(0 functions from <stdlib.h>
 *
 *  Note that these transformations are locale dependent; but are used
 *  only for informational messages, never for the message text.  That
 *  is all done on the Java side of the barrier by the encoding value
 *  supplied for Reader/Writer jacketing of java.io stream i/o.
 *
 * Version 1.1 Mr.Tines <tines@ravnaandtines.com> July 99 - remove
 * unused UTF routines; keep the name; let Java do thge locale work for
 * us rather than need a setlocale()
 *
 */

#include <stdlib.h>
#include <string>
#include "utfcode.h"

class JstringWrapper
{
    JNIEnv * environment;
    jstring string;
    const jchar * j_str_base;
    jsize length;
    JstringWrapper(JNIEnv * env, jstring s) : 
        environment(env), string(s), j_str_base(NULL), length(0)
    {
        j_str_base = env->GetStringChars(s, 0);
        length = env->GetStringLength(s);
    }

    ~JstringWrapper()
    {
        environment->ReleaseStringChars(string, j_str_base);
    }

private:
    JstringWrapper(const JstringWrapper&);
    JstringWrapper& operator=(const JstringWrapper&);
};

/*
 * Class:  None
 *
 * Method: jstringToMbs
 *
 * Aguments:
 *     [i] JNIEnv * env  -  JNI environment
 *  [i] jstring s  -  the Java string
 *
 * Return: new[]'d buffer of chars containing the string
 *
 * Comment:
 */
void jstringToMbs(JNIEnv * env, jstring s, std::vector<char> & mbstr)
{
    /* Obtain a local safe copy of the Java string */
    const jchar *str = env->GetStringChars(s, 0);
    jsize len = env->GetStringLength(s);

    std::vector<jchar> temp(str, str+len);

    /* Now we are done with str */
    env->ReleaseStringChars(s, str);

    mbstr.clear();

    try {
        jclass stringClass = env->FindClass("java/lang/String");
        jmethodID init = env->GetMethodID(stringClass, "<init>", "([C)V");        /*default encoding from bytes*/

        //build char array
        jcharArray c = env->NewCharArray(len);
        jboolean isCopy = JNI_FALSE;
        jchar * ptr = env->GetCharArrayElements(c, &isCopy);
        jsize i=0;
        for(; i<len; ++i)
        {
            ptr[i] = (jchar)temp[i];
        }
        if(JNI_TRUE == isCopy)
        {
            env->ReleaseCharArrayElements(c, ptr, 0);
        }

        //create string
        jobject string = env->NewObject(stringClass, init, c);
        env->DeleteLocalRef(c);

        //now get the byte array
        jmethodID getBytes = env->GetMethodID(stringClass,
        "getBytes", "()[B");

        jbyteArray b = (jbyteArray) env->CallObjectMethod(string, getBytes);

        isCopy = JNI_FALSE;
        jbyte * ptrb = env->GetByteArrayElements(b, &isCopy);
        len = env->GetArrayLength(b);

        for(i=0; i<len; ++i)
        {
            mbstr.push_back((char)ptrb[i]);
        }
        mbstr.push_back((char)0);

        if(JNI_TRUE == isCopy)
        {
            env->ReleaseByteArrayElements(b, ptrb, 0);
        }
        env->DeleteLocalRef(b);

    }
    catch(void*) {
        mbstr.clear();
        mbstr.push_back((char)0);
    }
}

/*
 * Class:  None
 *
 * Method: mbsToJstring
 *
 * Aguments:
 *     [i] JNIEnv * env  -  JNI environment
 *  [i] const char * mbstr - multi-byte string.
 *
 * Return: new String
 *
 * Comment:
 */
jstring mbsToJstring(JNIEnv *env, const std::vector<char> &mbstr)
{
    return mbsToJstring(env, &mbstr[0]);
}


jstring mbsToJstring(JNIEnv *env, const char *mbstr)
{
    //assumes no embedded nulls in the default encoding 
    //i.e. assume not a Unicode based machine, and get
    //byte length (excluding terminal null);
    size_t max = strlen(mbstr);

    try {
        jclass stringClass = env->FindClass("java/lang/String");

        jmethodID init = env->GetMethodID(stringClass, "<init>", "([B)V");        /*default encoding from bytes*/

        //build byte array
        jbyteArray b = env->NewByteArray((jsize)max);
        jboolean isCopy = JNI_FALSE;
        jbyte * ptr = env->GetByteArrayElements(b, &isCopy);

        for(size_t i=0; i<max; ++i)
        {
            ptr[i] = (jbyte)mbstr[i];
        }
        if(JNI_TRUE == isCopy)
        {
            env->ReleaseByteArrayElements(b, ptr, 0);
        }

        //create string
        jobject theString = env->NewObject(stringClass, init, b);
        env->DeleteLocalRef(b);

        jmethodID chars = env->GetMethodID(stringClass,
        "toCharArray", "()[C");

        jcharArray unicode = reinterpret_cast<jcharArray>(
        env->CallObjectMethod(theString, chars));
        env->DeleteLocalRef(theString);

        jsize len = env->GetArrayLength(unicode);

        jboolean copy;
        jchar * buff = env->GetCharArrayElements(unicode, &copy);
        jstring result = env->NewString(buff, len);
        env->ReleaseCharArrayElements(unicode, buff, 0);
        env->DeleteLocalRef(unicode);

        return result;
    }
    catch(void*) {
        return (jstring)0;
    }

}


//EOF
