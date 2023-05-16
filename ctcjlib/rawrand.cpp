/***************************************************************************
                          rawrand.cpp  -  description
                             -------------------
    copyright            : (C) 1996 by Mr. Tines
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
/* rawrand.c
 *
 *  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 *  Heimdall <heimdall@bifroest.demon.co.uk>  1996
 *  All rights reserved.  For full licence details see file licences.c
 *
 */
#include "rawrand.h"
#include "ctcjlib.h"
#include <string.h>
#include <time.h>
#include "port_io.h"
#include "keyconst.h"
#include "hash.h"
#include "md5.h"
#include "utils.h"

#define BUFFERSIZE 1024
#define BKSZ   (MD5HASHSIZE*2)
#define CHUNK   MD5HASHSIZE

static byte buffer[BUFFERSIZE];
static int bufflen = 0;
int realRandom = 0;
int bucket = 0;

namespace CTClib {
    using CTCjlib::getEnvironment;

    void stickUp(const int flag);

    extern "C" void getRawRandom(unsigned char * data, int length)
    {
        //how much have we got and how much do we need?
        int chunks = (length+CHUNK-1)/CHUNK;
        if(chunks > realRandom/CHUNK) chunks = realRandom/CHUNK;
        int move = chunks*CHUNK;
        if(move > length) move = length;
        int end = chunks*CHUNK;

        //use what we have
        if(move > 0) memcpy(data, buffer, move);
        realRandom -= end;

        //and expend it
        for(int i=0; i<end && i+end<bufflen; ++i)
        {
            buffer[i] ^= buffer[i+end];
        }
        bufflen -= end;
        bucket = bufflen%BUFFERSIZE;

        //rely on pass by value to update what needs filling in
        data += move;
        length -= move;
        if(length <= 0) return;

        //now go and get random
        jclass fileclass = getEnvironment()->FindClass("com/ravnaandtines/ctcjava/GlobalData");
        jmethodID getRaw = getEnvironment()->GetStaticMethodID(fileclass,
                "getRawRandom", "(I)Lcom/ravnaandtines/ctcjava/ByteArrayWrapper;");
        jobject raw = getEnvironment()->CallStaticObjectMethod(fileclass, getRaw, length);

        jclass byteclass = getEnvironment()->FindClass("com/ravnaandtines/ctcjava/ByteArrayWrapper");
        jfieldID dataf = getEnvironment()->GetFieldID(byteclass, "data", "[B");

        jbyteArray dataA = static_cast<jbyteArray>
            (getEnvironment()->GetObjectField(raw, dataf));

        jboolean isCopy;
        jbyte * detailData = getEnvironment()->GetByteArrayElements(dataA, &isCopy);
        jint dsz = getEnvironment()->GetArrayLength(dataA);
        memcpy(data, detailData, dsz);

        if(JNI_TRUE == isCopy)
        {
            getEnvironment()->ReleaseByteArrayElements(dataA, detailData, 0);
        }
        getEnvironment()->DeleteLocalRef(raw);
        return;
    }

    extern "C" boolean ensureRawRandom(int bytes)
    {
        if(bytes < realRandom) return JNI_TRUE;

        jclass fileclass = getEnvironment()->FindClass("com/ravnaandtines/ctcjava/GlobalData");
        jmethodID ensure = getEnvironment()->GetStaticMethodID(fileclass, "ensure", "(I)Z");
        return (boolean) getEnvironment()->CallStaticBooleanMethod(fileclass, ensure, bytes-realRandom);
    }

    extern "C" void getSessionData(unsigned char * data, int *length)
    {
        *length = min(*length, bufflen);
        memcpy(data, buffer, *length);
        if(0 == *length) stickUp(-3);
        return;
    }
    /*
     *     extern "C" void setSessionData(unsigned char * data, int *length)
     *     {
     *        bufflen = *length;
     *        memcpy(buffer, data, bufflen);
     *     }
     */
    void makeSessionData(DataFileP source)
    {
        size_t dummy;
        MD5_CTX *temp, MD5context, *pcontext = &MD5context;

        MD5Init(&temp, &dummy);
        MD5context = *temp;
        zfree((void**)&temp, dummy);

        //assume ~1/2 bit entropy per byte
        byte xbuffer[BKSZ];
        long length;

        //season with current time
        time_t timeNow = time(NULL);
        MD5Update(pcontext, (byte*)&timeNow, sizeof(time_t));

        //shuffle stuff in
        while((length = vf_read(xbuffer, BKSZ, source)) > 0)
        {
            //two digest lengths of input data
            MD5Update(pcontext, xbuffer, (uint32_t)length);
            //whatever's there in the buffer
            MD5Update(pcontext, buffer+bucket, CHUNK);
            // mix it all up into that chunk
            MD5Final(&pcontext, buffer+bucket, (size_t)0);

            // increment counts - wrap the folding in,
            // but highwatermark what's been filled.
            if(bufflen < BUFFERSIZE)
                bufflen += CHUNK;
            bucket += CHUNK;
            if(bucket >= BUFFERSIZE)
                bucket = 0;

            //And note that we have a supply of raw-ish random
            //but only include full buckets!
            if(realRandom < BUFFERSIZE && BKSZ == length)
                realRandom += CHUNK;
        }
        /* reset */
        vf_setpos(source,0l);
        /* wipe */
        MD5Update(pcontext, (byte*)pcontext, sizeof(MD5context));
    }

}/*end of namespace CTClib*/
/* end of file rawrand. c */
