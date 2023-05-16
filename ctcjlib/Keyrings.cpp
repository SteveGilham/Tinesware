/***************************************************************************
                          Keyrings.cpp  -  description
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
/* Keyrings.cpp 
 *
 *  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 *  Heimdall <heimdall@bifroest.demon.co.uk>  1998
 *  All rights reserved.  For full licence details see file licences.c
 */
#ifdef _MSC_VER
#pragma warning(disable : 4786)
#endif

#if defined(__BORLANDC__) && __BORLANDC__ < 0x530
#include <list>
#else
#include <set>
#endif

#include <string>
#include <stdio.h>

#include "com_ravnaandtines_ctcjava__Keyrings.h"

#include "abstract.h"
#include "ctc.h"
#include "pkautils.h"
#include "port_io.h"
#include "keyhash.h"
#include "utfcode.h"
#include "ctcjlib.h"
#include <locale.h>

extern "C" void gfQuit(void);

using CTClib::decode_context;
using CTClib::init_userhash;
using CTClib::read_secring;
using CTClib::publicChanged;
using CTClib::writePubRing;
using CTClib::secretChanged;
using CTClib::writeSecRing;
using CTClib::destroy_userhash;
using CTClib::firstSecKey;
using CTClib::firstPubKey;

using CTClib::DataFileP;
using CTClib::vf_open;
using CTClib::vf_write;
using CTClib::vf_setpos;
using CTClib::vf_read;
using CTClib::vf_close;
using CTClib::vf_tempfile;
using CTClib::seckey;
using CTClib::pubkey;

using CTClib::externalise_seckey;
using CTClib::READ;
using CTClib::WRITE;
using CTClib::PUBLICRING;
using CTClib::SECRETRING;

using CTCjlib::writeKeyrings;

static decode_context theContext = {
    NULL}; // ensure hashtable nulled

#if defined(__BORLANDC__) && __BORLANDC__ < 0x530
typedef std::list<CTClib::seckey *> keylist;
#else
struct SecKeyLess {
    bool operator() (const seckey * x, const seckey * y) const
    { 
        return x < y; 
    }
};

typedef std::set<CTClib::seckey *, SecKeyLess> keylist;
#endif

static keylist internals;

static std::string pubName("");
static std::string secName("");

static bool pubRingBacked = false;
static bool secRingBacked = false;


namespace CTCjlib {

    decode_context *getContext()
    {
        return &theContext;
    }

    void logInternalisedKey(seckey * key)
    {
#if defined(__BORLANDC__) && __BORLANDC__ < 0x530
        internals.insert(internals.begin(), key);
#else
        // std::pair<keylist::iterator, bool> status = // while not yet used
            internals.insert(key);
#endif
        //TODO:: if(pair.second) set timer else reset
    }

    void clearInternalisedKeys()
    {
        for(keylist::iterator i = internals.begin();
     i!= internals.end();
  i = internals.begin())
        {
            externalise_seckey(*i);
            internals.erase(i);
        }
    }

    void clearInternalisedKey(seckey * key)
    {
#if defined(__BORLANDC__) && __BORLANDC__ < 0x530
        keylist::iterator i = internals.begin();
        for(; i != internals.end(); ++i)
        {
            if(*i == key) break;
        }
#else
        keylist::iterator i = internals.find(key);
#endif
        if(i!= internals.end())
        {
            externalise_seckey(*i);
            internals.erase(i);
        }
    }


} // namespace CTCjlib

using CTCjlib::copyBFile;
using CTCjlib::clearInternalisedKeys;
using CTCjlib::setEnvironment;

static bool readPubkeys(char * publicName)
{
    pubRingBacked = false;
    secRingBacked = false;
    clearInternalisedKeys();
    if(theContext.keyRings)
    {
        destroy_userhash(theContext.keyRings);
    }
    theContext.keyRings = NULL;
    DataFileP pubFile = vf_open(publicName, READ, PUBLICRING);
    if(pubFile)
    {
        theContext.keyRings = init_userhash(pubFile);
        pubName = std::string(publicName);
        return true;
    }
    return false;
}

static bool readSeckeys(char * secretName)
{
    secRingBacked = false;
    DataFileP secFile = vf_open(secretName, READ, SECRETRING);
    if(secFile && read_secring(secFile, theContext.keyRings))
    {
        vf_close(secFile);
        secName = std::string(secretName);
        return true;
    }
    destroy_userhash(theContext.keyRings);
    theContext.keyRings = NULL;
    return false;
}

extern "C"     JNIEXPORT jlong JNICALL Java_com_ravnaandtines_ctcjava_PublicKeyRoot_readPubring
    (JNIEnv * env, jobject, jstring param)
{
    setlocale(LC_ALL, "");
    setEnvironment(env);
    memset(&theContext, 0, sizeof(theContext));
    std::vector<char> par;
    jstringToMbs(env, param, par);
    bool success = readPubkeys(&par[0]);

    if(!success) return (jlong)0;

    // return the handle to keyrings
    return (jlong)(&theContext);
}

extern "C" JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_SecretKeyRoot_readSecring
    (JNIEnv * env, jobject, jstring param)
{
    setlocale(LC_ALL, "");
    setEnvironment(env);
    std::vector<char> par;
    jstringToMbs(env, param, par);
    bool success = readSeckeys(&par[0]);
    return success;
}

extern "C" JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_PublicKeyRoot_clearKeyrings
(JNIEnv * env, jobject)
{
    setEnvironment(env);
    clearInternalisedKeys();
    if(!writeKeyrings()) return JNI_FALSE;
    if(NULL != theContext.keyRings)
    {
        destroy_userhash(theContext.keyRings);
    }
    gfQuit();
    return JNI_TRUE;
}


/*
 * Class:     ctcjava_DecryptionContext
 * Method:    getFirstPubkey
 * Signature: (J)Lcom/ravnaandtines/ctcjava/NativePublicKey;
 */
extern "C" JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_PublicKeyRoot_getFirstPubkey
(JNIEnv * env, jobject , jlong jptr)
{
    setEnvironment(env);
    decode_context * context = (decode_context *)jptr;
    pubkey * pub_key = firstPubKey(context->keyRings);

    // construct a NativePublicKey to wrap the key with
    jlong key = (jlong)pub_key;
    jclass pubkeyClass = env->FindClass("com/ravnaandtines/ctcjava/NativePublicKey");
    jmethodID init = env->GetMethodID(pubkeyClass, 
    "<init>", "(J)V");
    return env->NewObject(pubkeyClass, init, key);
}

/*
 * Class:     ctcjava_DecryptionContext
 * Method:    getFirstSeckey
 * Signature: (J)Lcom/ravnaandtines/ctcjava/NativeSecretKey;
 */
extern "C" JNIEXPORT jobject JNICALL Java_com_ravnaandtines_ctcjava_PublicKeyRoot_getFirstSeckey
(JNIEnv * env, jobject, jlong jptr)
{
    setEnvironment(env);
    decode_context * context = (decode_context *)jptr;
    seckey * sec_key = firstSecKey(context->keyRings);

    // construct a NativeSecretKey to wrap the key with
    jlong key = (jlong)sec_key;
    jclass seckeyClass = env->FindClass("com/ravnaandtines/ctcjava/NativeSecretKey");
    jmethodID init = env->GetMethodID(seckeyClass, 
    "<init>", "(J)V");
    return env->NewObject(seckeyClass, init, key);
}



// alas there is no portable version of access()!
static bool fileExists(const char * path)
{
    FILE * f = fopen(path, "r");
    bool result = f != NULL;
    if(f) fclose(f);
    return result;
}

std::string & backupName(std::string & rawpath)
{
    static std::string workspace;
    workspace = rawpath + ".bak";
    if(!fileExists(workspace.c_str())) return workspace;

    for(uint32_t num = 1; num; ++num)
    {
        char buff[12];
        sprintf(buff, "%ld", num);
        std::string temp = workspace + std::string(buff);
        if(!fileExists(temp.c_str())) return (workspace = temp);
    }
    return workspace;
}


namespace CTCjlib {

    bool backupKeyrings()
    {
        DataFileP fromring, toring;

        if(publicChanged(theContext.keyRings) && !pubRingBacked)
        {
            std::string backupFile = backupName(pubName);

            toring = vf_open(backupFile.c_str(), WRITE, PUBLICRING);
            if(!toring) return false;
            fromring = vf_open(pubName.c_str(), READ, PUBLICRING);
            if(!fromring) return false;
            copyBFile(fromring, toring);
            vf_close(toring);
            vf_close(fromring);
            pubRingBacked = true;
        }

        if(secretChanged(theContext.keyRings) && !secRingBacked)
        {
            std::string backupFile = backupName(secName);
            toring = vf_open(backupFile.c_str(), WRITE, SECRETRING);
            if(!toring) return false;
            fromring = vf_open(secName.c_str(), READ, SECRETRING);
            if(!fromring) return false;
            copyBFile(fromring, toring);
            vf_close(toring);
            vf_close(fromring);
            secRingBacked = true;
        }
        return true;
    }

#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (disable: 4701) 
#endif
    bool writeKeyrings()
    {
        if(!(theContext.keyRings)) return true;
        if(!backupKeyrings()) return false;

        DataFileP pring, sring, ring;
        bool cp=false, cs=false;

        if(publicChanged(theContext.keyRings))
        {
            pring = vf_tempfile(0);
            if(!pring) return false;
            if(!writePubRing(pring, theContext.keyRings)) return false;
            cp = true;
        }

        if(secretChanged(theContext.keyRings))
        {
            sring = vf_tempfile(0);
            if(!sring) return false;
            if(!writeSecRing(sring, theContext.keyRings)) return false;
            cs = true;
        }

        // shut down old keyring records as file offsets &c. will
        // in general be stale; also close the old keyring handle down
        // must not close this file before this as writePubRing() will read
        // from this file!  Nor, for similar reasons, can we directly
        // write to the old file names.  This is in lieu of the
        // proper implementation of vf_replacewith() and vf_toreplace()
        if(cp)
        {
            clearInternalisedKeys();
            destroy_userhash(theContext.keyRings);
            theContext.keyRings = NULL;
            ring = vf_open(pubName.c_str(), WRITE, PUBLICRING);
            if(!ring) return false;
            copyBFile(pring, ring);
            vf_close(pring);
        }

        if(cs)
        {
            ring = vf_open(secName.c_str(), WRITE, SECRETRING);
            if(!ring) return false;
            copyBFile(sring, ring);
            vf_close(sring);
        }

        return true;
    }
#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (default: 4701)
#endif


#define BUFFERSIZE 1024
    void copyBFile(DataFileP from, DataFileP to)
    {
        byte buffer[BUFFERSIZE];
        long length;

        vf_setpos(from, 0);
        vf_setpos(to,   0);
        while((length = vf_read(buffer, BUFFERSIZE, from)) > 0)
            vf_write(buffer, length, to);
    }

}


