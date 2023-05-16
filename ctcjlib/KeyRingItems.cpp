/***************************************************************************
                          KeyRingItems.cpp  -  description
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
/* ctcjava_NativePublicKey.cpp 
 *
 *  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 *  Heimdall <heimdall@bifroest.demon.co.uk>  1998
 *  All rights reserved.  For full licence details see file licences.c
 */
#include "com_ravnaandtines_ctcjava_NativePublicKey.h"
#include "com_ravnaandtines_ctcjava_NativeSecretKey.h"
#include "com_ravnaandtines_ctcjava_NativeUsername.h"
#include "com_ravnaandtines_ctcjava_NativeSignature.h"
#include "ctc.h"
#include "ctcjlib.h"
#include "utfcode.h"
#include "port_io.h"
#include "callback.h"
#include "keyhash.h"
#include "keyutils.h"
#include "utils.h"
#include <string.h>
#include <locale.h>

using namespace CTClib;

namespace CTCjlib {

static jobject wrapPubkey(JNIEnv * env, pubkey * pub_key)
{
    if(0 == pub_key) return (jobject)0;

    // construct a NativePublicKey to wrap the key with
    jlong key = (jlong)pub_key;
    jclass pubkeyClass = env->FindClass("com/ravnaandtines/ctcjava/NativePublicKey");
    jmethodID init = env->GetMethodID(pubkeyClass,
    "<init>", "(J)V");
    return env->NewObject(pubkeyClass, init, key);
}

    jobject wrapSeckey(JNIEnv * env, seckey * sec_key)
    {
        // construct a NativePublicKey to wrap the key with
        jlong key = (jlong)sec_key;
        jclass seckeyClass = env->FindClass("com/ravnaandtines/ctcjava/NativeSecretKey");
        jmethodID init = env->GetMethodID(seckeyClass,
        "<init>", "(J)V");
        return env->NewObject(seckeyClass, init, key);
    }

} // namespace CTCjlib


static jobject wrapNativeSignature(JNIEnv * env, signature * sig)
{
    jclass NativeSignatureClass = env->FindClass("com/ravnaandtines/ctcjava/NativeSignature");
    jmethodID init = env->GetMethodID(NativeSignatureClass, "<init>", "(J)V");
    return env->NewObject(NativeSignatureClass, init, (jlong)sig);
}

static jobject wrapNativeUsername(JNIEnv * env, username * name)
{
    jclass NativeUsernameClass = env->FindClass("com/ravnaandtines/ctcjava/NativeUsername");
    jmethodID init = env->GetMethodID(NativeUsernameClass, "<init>", "(J)V");
    return env->NewObject(NativeUsernameClass, init, (jlong)name);
}


//---Pubkey operations-------------------------------------------------

extern "C" JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_NativePublicKey_getNextPubkey
(JNIEnv * env, jobject, jlong jptr)
{
    CTCjlib::setEnvironment(env);
    pubkey * old = (pubkey *)jptr;
    pubkey * pub_key = nextPubKey(old);

    return CTCjlib::wrapPubkey(env, pub_key);
}

extern "C" JNIEXPORT jstring JNICALL Java_com_ravnaandtines_ctcjava_NativePublicKey_name_1from_1key
(JNIEnv * env, jobject, jlong jptr)
{
    setlocale(LC_ALL, "");
    CTCjlib::setEnvironment(env);
    pubkey * pub = (pubkey *)jptr;

    char name[256];
    if(pub)
    {
        name_from_key(pub, name);
    }
    else name[0] = 0;

    return mbsToJstring(env, name);
}


extern "C" JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_NativePublicKey_isEnabled
(JNIEnv *, jobject, jlong jptr)
{
    pubkey * pub = (pubkey *)jptr;
    if(0 == pub) return JNI_FALSE;
    return (jboolean) !(ownTrust(pub) &  KTB_ENABLE_MASK);
}

extern "C" JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_NativePublicKey_isRevoked
(JNIEnv *, jobject, jlong jptr)
{
    pubkey * pub = (pubkey *)jptr;
    if(0 == pub) return JNI_FALSE;

    signature * revoked = revocation(pub);

    if(NULL == revoked) return JNI_FALSE;

    // check this is a revocation (key compromise) NativeSignature
    if(SIG_KEY_COMPROM != getSignatureValue(revoked, SIG_CLASS, CTCjlib::getContext()->keyRings))
    {
        return JNI_FALSE;
    }

    sigValid check = checkSignature(pub, NULL, revoked);
    return (jboolean) ((check == SIG_OKAY) ? JNI_TRUE : JNI_FALSE);
}



extern "C" JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_NativePublicKey_keyExtract
(JNIEnv * env, jobject, jlong pub_key)
{
    if(0 == pub_key) return JNI_FALSE;
    CTCjlib::setEnvironment(env);
    DataFileP pub = vf_tempfile(1000);
    if(key_extract(pub, (pubkey *)pub_key))
    {
      cb_details details;
      details.signatory = 0;
      details.valid_sig = FALSE;
      details.addressee = 0;
      details.timestamp = 0;
      details.typeByte  = 't';
      details.fileName[0] = 0;
      cb_result_file(pub, &details);
      return JNI_TRUE;
    }
    return JNI_FALSE;
}

extern "C" JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_NativePublicKey_keySign
(JNIEnv *, jobject, jlong jptr, jlong jptr2, jbyte alg)
{
    pubkey * pub = (pubkey *)jptr;
    if(0 == pub) return;
    seckey * sec = (seckey *)jptr2;
    if(0 == sec) return;
    username * fn = firstName(pub);
    if(0 != fn)
    {
        addSignature(pub, fn, SIG_KEY_CERT, sec, alg);
    }
}

extern "C" JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_NativePublicKey_keyAble
(JNIEnv *, jobject, jlong jptr)
{
    pubkey * pub = (pubkey *)jptr;
    if(0 == pub) return;
    setTrust(pub, (byte)(ownTrust(pub)^KTB_ENABLE_MASK));
}

extern "C" JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_NativePublicKey_keyDelete
(JNIEnv *, jobject, jlong jptr)
{
    pubkey * pub = (pubkey *)jptr;
    if(0 == pub) return;
    removePubkey(CTCjlib::getContext()->keyRings, pub);
}

extern "C" JNIEXPORT jint JNICALL Java_com_ravnaandtines_ctcjava_NativePublicKey_keySize
(JNIEnv *, jobject, jlong jptr)
{
    pubkey * pub = (pubkey *)jptr;
    if(0 == pub) return 0;
    return (jint)keyLength(pub);
}

extern "C" JNIEXPORT jint JNICALL Java_com_ravnaandtines_ctcjava_NativePublicKey_keyAlg
(JNIEnv *, jobject, jlong jptr)
{
    pubkey * pub = (pubkey *)jptr;
    if(0 == pub) return 0;
    completeKey(pub);
    return (jint)getPubkeyAlg(pub);
}

extern "C" JNIEXPORT jlong JNICALL Java_com_ravnaandtines_ctcjava_NativePublicKey_keyID
(JNIEnv *, jobject, jlong jptr)
{
    pubkey * pub = (pubkey *)jptr;
    if(0 == pub) return (jlong) 0;

    byte keyID[KEYFRAGSIZE];
    extract_keyID(keyID, pub);
    jlong v = 0;
    for(int i=0; i<KEYFRAGSIZE; ++i)
    {
        v = (v<<8)|keyID[i];
    }
    return v;
}

extern "C" JNIEXPORT jint JNICALL Java_com_ravnaandtines_ctcjava_NativePublicKey_keyDate
(JNIEnv *, jobject, jlong jptr)
{
    pubkey * pub = (pubkey *)jptr;
    if(0 == pub) return 0;
    //return creationDate(pub);
    return keyDate(pub);
}

extern "C" JNIEXPORT jstring JNICALL Java_com_ravnaandtines_ctcjava_NativePublicKey_keyPrint
(JNIEnv * env, jobject, jlong jptr)
{
    setlocale(LC_ALL, "");
    pubkey * pub = (pubkey *)jptr;
    char text[64];
    byte md[MAXHASHSIZE];
    memset(text, 0, 64);

    if(pub)
    {
        fingerPrint(md, pub); // v3 keyprint
        int limit = (getPubkeyVersion(pub) < VERSION_3) ? 16 : 20;
        byte * mdptr = md;
        char * outptr = text;
        int i = 0;

        while(mdptr < md + limit)
        {
            byte2hex(outptr, *mdptr++);
            ++i;

            outptr += 2;

            if(0==i%2)
            {
                *outptr = ' ';
                ++outptr;
            }
            if(limit/2 == i)
            {
                *outptr = ' ';
                ++outptr;
            }
        }
        while(*(--outptr) == ' ') *outptr = 0;
    }
    return mbsToJstring(env, text);
}


extern "C" JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_NativePublicKey_sec
(JNIEnv * env, jobject, jlong jptr)
{
    pubkey * pub = (pubkey *)jptr;
    seckey * sec = pub ? secretKey(pub) : 0;
    return CTCjlib::wrapSeckey(env, sec);
}

extern "C" JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_NativePublicKey_NativeSignature
(JNIEnv * env, jobject, jlong jptr)
{
    pubkey * pub = (pubkey *)jptr;
    signature * revoked = pub ? revocation(pub) : 0;
    return wrapNativeSignature(env, revoked);
}

extern "C" JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_NativePublicKey_subkey
(JNIEnv * env, jobject, jlong jptr)
{
    pubkey * pub = (pubkey *)jptr;
    pubkey * sub = pub ? subKey(pub) : 0;
    return CTCjlib::wrapPubkey(env, sub);
}

extern "C" JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_NativePublicKey_NativeUsername
(JNIEnv * env, jobject, jlong jptr)
{
    pubkey * pub = (pubkey *)jptr;
    username * name = pub ? firstName(pub) : 0;
    return wrapNativeUsername(env, name);
}

extern "C" JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_NativePublicKey_check
(JNIEnv *, jobject, jlong key, jlong name, jlong sig)
{
    sigValid check = checkSignature((pubkey *)key, (username *)name, (signature *)sig);
    return check == SIG_OKAY;
}

//---Seckey operations-------------------------------------------------
extern "C" JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_NativeSecretKey_getNextSeckey
(JNIEnv * env, jobject, jlong jptr)
{
    CTCjlib::setEnvironment(env);
    seckey * old = (seckey *)jptr;
    seckey * sec_key = nextSecKey(old);


    if(0 == sec_key) return (jobject)0;

    // construct a NativeSecretKey to wrap the key with
    jlong key = (jlong)sec_key;
    jclass seckeyClass = env->FindClass("com/ravnaandtines/ctcjava/NativeSecretKey");
    jmethodID init = env->GetMethodID(seckeyClass,
    "<init>", "(J)V");
    return env->NewObject(seckeyClass, init, key);
}

extern "C" JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_NativeSecretKey_unlock
(JNIEnv * env, jobject, jlong jptr, jobject array)
{
    CTCjlib::setEnvironment(env);
    seckey * sec_key = (seckey *)jptr;
    if(!sec_key)
        return JNI_FALSE;
    if(keyStatus(sec_key) == INTERNAL)
        return JNI_TRUE;

    jclass byteclass = CTCjlib::getEnvironment()->FindClass("com/ravnaandtines/ctcjava/ByteArrayWrapper");
    jfieldID dataf = CTCjlib::getEnvironment()->GetFieldID(byteclass, "data", "[B");

    jbyteArray dataA = static_cast<jbyteArray>
        (CTCjlib::getEnvironment()->GetObjectField(array, dataf));

    jboolean isCopy;
    jbyte * detailData = CTCjlib::getEnvironment()->GetByteArrayElements(dataA, &isCopy);
    jint    dsz = CTCjlib::getEnvironment()->GetArrayLength(dataA);

    std::vector<char> password(dsz);
    for(jint i = 0; i< dsz; ++i)
    {
        password[i] = (char)detailData[i];
        detailData[i] = 0;
    }
    password.push_back((char)0);

    if(JNI_TRUE == isCopy)
    {
        CTCjlib::getEnvironment()->ReleaseByteArrayElements(dataA, detailData, 0);
    }

    // clear passphrase
    boolean result = internalise_seckey(sec_key, &password[0]);
    memset(&password[0], 0, dsz);

    return result ? JNI_TRUE : JNI_FALSE;
}


extern "C" JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_NativeSecretKey_pub
(JNIEnv * env, jobject, jlong jptr)
{
    CTCjlib::setEnvironment(env);
    seckey * sec_key = (seckey *)jptr;
    return CTCjlib::wrapPubkey(env, sec_key? publicKey(sec_key): 0);
}

extern "C" JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_NativeSecretKey_keyRevoke
(JNIEnv * env, jobject, jlong sec_key, jboolean perm)
{
    CTCjlib::setEnvironment(env);
    DataFileP sec = vf_tempfile(1000);
    if(key_revoke(sec, (seckey*)sec_key, (boolean)perm))
    {
      cb_details details;
      details.signatory = 0;
      details.valid_sig = FALSE;
      details.addressee = 0;
      details.timestamp = 0;
      details.typeByte  = 't';
      details.fileName[0] = 0;
      cb_result_file(sec, &details);
      return JNI_TRUE;
    }
    return JNI_FALSE;
}

extern "C" JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_NativeSecretKey_internalise
(JNIEnv * env, jobject, jlong jptr)
{
    CTCjlib::setEnvironment(env);
    seckey * sec_key = (seckey *)jptr;
    CTCjlib::clearInternalisedKey(sec_key);
}

extern "C" JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_NativeSecretKey_externalise
(JNIEnv *, jobject, jlong jptr)
{
    if(0 == jptr) return JNI_FALSE;
    seckey * sec = (seckey *)jptr;

    // get passphrase for NativeSignature
    if(sec && keyStatus(sec) != INTERNAL)
    {
        seckey * keys[1];
        keys[0] = sec;
        if ( cb_need_key(keys, 1)  < 0 ) sec = 0;
    }
    return sec != 0;
}

extern "C" JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_NativeSecretKey_isExternalised
(JNIEnv *, jobject, jlong jptr)
{
    if(0 == jptr) return JNI_FALSE;
    seckey * sec = (seckey *)jptr;
    return keyStatus(sec) != INTERNAL;
}

extern "C" JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_NativeSecretKey_addSignUser
(JNIEnv * env, jobject, jlong jptr, jstring jname)
{
    if(0 == jptr) return;
    seckey * sec = (seckey *)jptr;
    if(!sec->publicKey) return;
    if(!completeKey(sec->publicKey)) return;


    std::vector<char> userName;
    jstringToMbs(env, jname, userName);
    if(userName.empty()) return;

    username * namerecord = addUsername(sec->publicKey, &userName[0]);

    // hardcode the NativeSignature as MD5 as we only have PGP2.x secret keys
    /*sig =*/(void) addSignature(sec->publicKey, namerecord, SIG_KEY_CERT,
    sec, MDA_MD5);
}

extern "C" JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_NativeSecretKey_updatePassphrase
(JNIEnv * env, jobject, jlong jptr, jbyte hash, jbyte kpalg, jbyteArray passphrase)
{
    if(0 == jptr) return;
    seckey * sec = (seckey *)jptr;
    if(!sec->publicKey) return;
    if(!completeKey(sec->publicKey)) return;
    if(INTERNAL != sec->skstat) return;

    sec->kpalg.cv_algor = kpalg;
    sec->kpalg.cv_mode  = CEM_CFB;

    sec->hashalg = hash;    /* keep default MDA_MD5 */
    if(MDA_MD5 != hash)
    {
        sec->kpalg.cv_algor |= CEA_MORE_FLAG;
    }

    {
        char phrase[256];
        jint sz = env->GetArrayLength(passphrase);
        memset(phrase, 0, 256);
        if(sz > 255) sz = 255;
        env->GetByteArrayRegion(passphrase, 0, sz, (jbyte*)phrase);

        set_passphrase_UTF8(sec, phrase, FALSE);
        memset(phrase, 0, 256); // wipe
    }
}

//---NativeUsername operations-------------------------------------------------
extern "C" JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_NativeUsername_getNextUsername
(JNIEnv * env, jobject, jlong jptr)
{
    username * name = (username *)jptr;
    username * next = name ? nextName(name) : 0;
    return wrapNativeUsername(env, next);
}

extern "C" JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_NativeUsername_NativeSignature
(JNIEnv * env, jobject, jlong jptr)
{
    username * name = (username *)jptr;
    signature * sig = name ? firstSig(name) : 0;
    return wrapNativeSignature(env, sig);
}

extern "C" JNIEXPORT jstring JNICALL Java_com_ravnaandtines_ctcjava_NativeUsername_name
(JNIEnv * env, jobject, jlong jptr)
{
    setlocale(LC_ALL, "");
    char text[256];
    memset(text, 0, 256);
    username * name = (username *)jptr;
    if(name)
    {
        text_from_name(CTCjlib::getContext()->keyRings, name, text);
    }
    return mbsToJstring(env, text);
}

extern "C" JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_NativeUsername_keySign
(JNIEnv *, jobject, jlong name, jlong sec, jlong pub, jbyte alg)
{
    if(0 == pub) return;
    if(0 == sec) return;
    if(0 == name) return;
    addSignature((pubkey*)pub, (username*)name, SIG_KEY_CERT, (seckey*)sec, alg);
}

//---NativeSignature operations-------------------------------------------------

extern "C" JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_NativeSignature_getNextSignature
(JNIEnv * env, jobject, jlong jptr)
{
    signature * sig = (signature *)jptr;
    signature * next = sig ? nextSig(sig) : 0;
    return wrapNativeSignature(env, next);
}

extern "C" JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_NativeSignature_pub
(JNIEnv * env, jobject, jlong jptr)
{
    signature * sig = (signature *)jptr;
    pubkey * pub = sig ? signatory(sig) : 0;
    return CTCjlib::wrapPubkey(env, pub);
}

extern "C" JNIEXPORT jint JNICALL Java_com_ravnaandtines_ctcjava_NativeSignature_value
(JNIEnv *, jobject, jlong jptr, jint sel)
{
    signature * sig = (signature *)jptr;
    return sig ? getSignatureValue(sig, sel, CTCjlib::getContext()->keyRings) : 0;
}

