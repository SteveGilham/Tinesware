/***************************************************************************
                          ctcjava_CJPubkey.cpp  -  description
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
/* ctcjava_CJPubkey.cpp 
 *
 *  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 *  Heimdall <heimdall@bifroest.demon.co.uk>  1998
 *  All rights reserved.  For full licence details see file licences.c
 */
#include "com_ravnaandtines_ctcjava_CJPubkey.h"
#include "com_ravnaandtines_ctcjava_CJSeckey.h"
#include "com_ravnaandtines_ctcjava_Username.h"
#include "com_ravnaandtines_ctcjava_Signature.h"
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

#if defined(CTC_NAMESPACE_SUPPORT)

using CTCjlib::setEnvironment;
using CTCjlib::getContext;
using CTCjlib::clearInternalisedKey;
using CTCjlib::wrapSeckey;

using CTClib::pubkey;
using CTClib::seckey;
using CTClib::nextPubKey;
using CTClib::nextSecKey;
using CTClib::name_from_key;
using CTClib::ownTrust;
using CTClib::publicKey;
using CTClib::secretKey;
using CTClib::subKey;
using CTClib::DataFileP;
using CTClib::key_extract;
using CTClib::key_revoke;
using CTClib::cb_details;
using CTClib::cb_need_key;
using CTClib::vf_tempfile;
using CTClib::cb_result_file;
using CTClib::decode_context;
using CTClib::firstPubKey;
using CTClib::completeKey;
using CTClib::extract_keyID;
using CTClib::seckey_from_keyID;
using CTClib::keyStatus;
using CTClib::signature;
using CTClib::sigValid;
using CTClib::revocation;
using CTClib::checkSignature;
using CTClib::SIG_OKAY;
using CTClib::SIG_CLASS;
using CTClib::username;
using CTClib::signatory;
using CTClib::firstName;
using CTClib::nextName;
using CTClib::firstSig;
using CTClib::nextSig;
using CTClib::addSignature;
using CTClib::setTrust;
using CTClib::removePubkey;
using CTClib::keyLength;
using CTClib::getPubkeyAlg;
//using CTClib::creationDate;
using CTClib::keyDate;
using CTClib::getPubkeyVersion;
using CTClib::fingerPrint;
using CTClib::byte2hex;
using CTClib::getSignatureValue;
using CTClib::text_from_name;
using CTClib::addUsername;
using CTClib::set_passphrase_UTF8;

#endif

static jobject wrapPubkey(JNIEnv * env, pubkey * pub_key)
{
    if(0 == pub_key) return (jobject)0;

    // construct a CJPubkey to wrap the key with
    jlong key = (jlong)pub_key;
    jclass pubkeyClass = env->FindClass("com/ravnaandtines/ctcjava/CJPubkey");
    jmethodID init = env->GetMethodID(pubkeyClass,
    "<init>", "(J)V");
    return env->NewObject(pubkeyClass, init, key);
}

#if defined(CTC_NAMESPACE_SUPPORT)
namespace CTCjlib {
#endif

    jobject wrapSeckey(JNIEnv * env, seckey * sec_key)
    {
        // construct a CJPubkey to wrap the key with
        jlong key = (jlong)sec_key;
        jclass seckeyClass = env->FindClass("com/ravnaandtines/ctcjava/CJSeckey");
        jmethodID init = env->GetMethodID(seckeyClass,
        "<init>", "(J)V");
        return env->NewObject(seckeyClass, init, key);
    }

#if defined(CTC_NAMESPACE_SUPPORT)
}
#endif


static jobject wrapSignature(JNIEnv * env, signature * sig)
{
    jclass signatureClass = env->FindClass("com/ravnaandtines/ctcjava/Signature");
    jmethodID init = env->GetMethodID(signatureClass, "<init>", "(J)V");
    return env->NewObject(signatureClass, init, (jlong)sig);
}

static jobject wrapUsername(JNIEnv * env, username * name)
{
    jclass usernameClass = env->FindClass("com/ravnaandtines/ctcjava/Username");
    jmethodID init = env->GetMethodID(usernameClass, "<init>", "(J)V");
    return env->NewObject(usernameClass, init, (jlong)name);
}


//---Pubkey operations-------------------------------------------------

extern "C" JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_getNextPubkey
(JNIEnv * env, jobject, jlong jptr)
{
    setEnvironment(env);
    pubkey * old = (pubkey *)jptr;
    pubkey * pub_key = nextPubKey(old);

    return wrapPubkey(env, pub_key);
}

extern "C" JNIEXPORT jstring JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_name_1from_1key
(JNIEnv * env, jobject, jlong jptr)
{
    setlocale(LC_ALL, "");
    setEnvironment(env);
    pubkey * pub = (pubkey *)jptr;

    char name[256];
    if(pub)
    {
        name_from_key(pub, name);
    }
    else name[0] = 0;

    return mbsToJstring(env, name);
}


extern "C" JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_isEnabled
(JNIEnv *, jobject, jlong jptr)
{
    pubkey * pub = (pubkey *)jptr;
    if(0 == pub) return JNI_FALSE;
    return (jboolean) !(ownTrust(pub) &  KTB_ENABLE_MASK);
}

extern "C" JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_isRevoked
(JNIEnv *, jobject, jlong jptr)
{
    pubkey * pub = (pubkey *)jptr;
    if(0 == pub) return JNI_FALSE;

    signature * revoked = revocation(pub);

    if(NULL == revoked) return JNI_FALSE;

    // check this is a revocation (key compromise) signature
    if(SIG_KEY_COMPROM != getSignatureValue(revoked, SIG_CLASS, getContext()->keyRings))
    {
        return JNI_FALSE;
    }

    sigValid check = checkSignature(pub, NULL, revoked);
    return (jboolean) ((check == SIG_OKAY) ? JNI_TRUE : JNI_FALSE);
}



extern "C" JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_keyExtract
(JNIEnv * env, jobject, jlong pub_key)
{
    if(0 == pub_key) return JNI_FALSE;
    setEnvironment(env);
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

extern "C" JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_keySign
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

extern "C" JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_keyAble
(JNIEnv *, jobject, jlong jptr)
{
    pubkey * pub = (pubkey *)jptr;
    if(0 == pub) return;
    setTrust(pub, (byte)(ownTrust(pub)^KTB_ENABLE_MASK));
}

extern "C" JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_keyDelete
(JNIEnv *, jobject, jlong jptr)
{
    pubkey * pub = (pubkey *)jptr;
    if(0 == pub) return;
    removePubkey(getContext()->keyRings, pub);
}

extern "C" JNIEXPORT jint JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_keySize
(JNIEnv *, jobject, jlong jptr)
{
    pubkey * pub = (pubkey *)jptr;
    if(0 == pub) return 0;
    return (jint)keyLength(pub);
}

extern "C" JNIEXPORT jint JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_keyAlg
(JNIEnv *, jobject, jlong jptr)
{
    pubkey * pub = (pubkey *)jptr;
    if(0 == pub) return 0;
    completeKey(pub);
    return (jint)getPubkeyAlg(pub);
}

extern "C" JNIEXPORT jlong JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_keyID
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

extern "C" JNIEXPORT jint JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_keyDate
(JNIEnv *, jobject, jlong jptr)
{
    pubkey * pub = (pubkey *)jptr;
    if(0 == pub) return 0;
    //return creationDate(pub);
    return keyDate(pub);
}

extern "C" JNIEXPORT jstring JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_keyPrint
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


extern "C" JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_sec
(JNIEnv * env, jobject, jlong jptr)
{
    pubkey * pub = (pubkey *)jptr;
    seckey * sec = pub ? secretKey(pub) : 0;
    return wrapSeckey(env, sec);
}

extern "C" JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_signature
(JNIEnv * env, jobject, jlong jptr)
{
    pubkey * pub = (pubkey *)jptr;
    signature * revoked = pub ? revocation(pub) : 0;
    return wrapSignature(env, revoked);
}

extern "C" JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_subkey
(JNIEnv * env, jobject, jlong jptr)
{
    pubkey * pub = (pubkey *)jptr;
    pubkey * sub = pub ? subKey(pub) : 0;
    return wrapPubkey(env, sub);
}

extern "C" JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_username
(JNIEnv * env, jobject, jlong jptr)
{
    pubkey * pub = (pubkey *)jptr;
    username * name = pub ? firstName(pub) : 0;
    return wrapUsername(env, name);
}

extern "C" JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_CJPubkey_check
(JNIEnv *, jobject, jlong key, jlong name, jlong sig)
{
    sigValid check = checkSignature((pubkey *)key, (username *)name, (signature *)sig);
    return check == SIG_OKAY;
}

//---Seckey operations-------------------------------------------------
extern "C" JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_CJSeckey_getNextSeckey
(JNIEnv * env, jobject, jlong jptr)
{
    setEnvironment(env);
    seckey * old = (seckey *)jptr;
    seckey * sec_key = nextSecKey(old);


    if(0 == sec_key) return (jobject)0;

    // construct a CJSeckey to wrap the key with
    jlong key = (jlong)sec_key;
    jclass seckeyClass = env->FindClass("com/ravnaandtines/ctcjava/CJSeckey");
    jmethodID init = env->GetMethodID(seckeyClass,
    "<init>", "(J)V");
    return env->NewObject(seckeyClass, init, key);
}

extern "C" JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_CJSeckey_pub
(JNIEnv * env, jobject, jlong jptr)
{
    setEnvironment(env);
    seckey * sec_key = (seckey *)jptr;
    return wrapPubkey(env, sec_key? publicKey(sec_key): 0);
}

extern "C" JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_CJSeckey_keyRevoke
(JNIEnv * env, jobject, jlong sec_key, jboolean perm)
{
    setEnvironment(env);
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

extern "C" JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_CJSeckey_internalise
(JNIEnv * env, jobject, jlong jptr)
{
    setEnvironment(env);
    seckey * sec_key = (seckey *)jptr;
    clearInternalisedKey(sec_key);
}

extern "C" JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_CJSeckey_externalise
(JNIEnv *, jobject, jlong jptr)
{
    if(0 == jptr) return JNI_FALSE;
    seckey * sec = (seckey *)jptr;

    // get passphrase for signature
    if(sec && keyStatus(sec) != INTERNAL)
    {
        seckey * keys[1];
        keys[0] = sec;
        if ( cb_need_key(keys, 1)  < 0 ) sec = 0;
    }
    return sec != 0;
}

extern "C" JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_CJSeckey_isExternalised
(JNIEnv *, jobject, jlong jptr)
{
    if(0 == jptr) return JNI_FALSE;
    seckey * sec = (seckey *)jptr;
    return keyStatus(sec) != INTERNAL;
}

extern "C" JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_CJSeckey_addSignUser
(JNIEnv * env, jobject, jlong jptr, jstring jname)
{
    if(0 == jptr) return;
    seckey * sec = (seckey *)jptr;
    if(!sec->publicKey) return;
    if(!completeKey(sec->publicKey)) return;

    char * userName = jstringToMbs(env, jname);
    if(0 == userName) return;

    username * namerecord = addUsername(sec->publicKey, userName);
    delete[] userName;

    // hardcode the signature as MD5 as we only have PGP2.x secret keys
    /*sig =*/(void) addSignature(sec->publicKey, namerecord, SIG_KEY_CERT,
    sec, MDA_MD5);
}

extern "C" JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_CJSeckey_updatePassphrase
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

//---Username operations-------------------------------------------------
extern "C" JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_Username_getNextUsername
(JNIEnv * env, jobject, jlong jptr)
{
    username * name = (username *)jptr;
    username * next = name ? nextName(name) : 0;
    return wrapUsername(env, next);
}

extern "C" JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_Username_signature
(JNIEnv * env, jobject, jlong jptr)
{
    username * name = (username *)jptr;
    signature * sig = name ? firstSig(name) : 0;
    return wrapSignature(env, sig);
}

extern "C" JNIEXPORT jstring JNICALL Java_com_ravnaandtines_ctcjava_Username_name
(JNIEnv * env, jobject, jlong jptr)
{
    setlocale(LC_ALL, "");
    char text[256];
    memset(text, 0, 256);
    username * name = (username *)jptr;
    if(name)
    {
        text_from_name(getContext()->keyRings, name, text);
    }
    return mbsToJstring(env, text);
}

extern "C" JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_Username_keySign
(JNIEnv *, jobject, jlong name, jlong sec, jlong pub, jbyte alg)
{
    if(0 == pub) return;
    if(0 == sec) return;
    if(0 == name) return;
    addSignature((pubkey*)pub, (username*)name, SIG_KEY_CERT, (seckey*)sec, alg);
}

//---Signature operations-------------------------------------------------

extern "C" JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_Signature_getNextSignature
(JNIEnv * env, jobject, jlong jptr)
{
    signature * sig = (signature *)jptr;
    signature * next = sig ? nextSig(sig) : 0;
    return wrapSignature(env, next);
}

extern "C" JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_Signature_pub
(JNIEnv * env, jobject, jlong jptr)
{
    signature * sig = (signature *)jptr;
    pubkey * pub = sig ? signatory(sig) : 0;
    return wrapPubkey(env, pub);
}

extern "C" JNIEXPORT jint JNICALL Java_com_ravnaandtines_ctcjava_Signature_value
(JNIEnv *, jobject, jlong jptr, jint sel)
{
    signature * sig = (signature *)jptr;
    return sig ? getSignatureValue(sig, sel, getContext()->keyRings) : 0;
}

