/***************************************************************************
                          ctcjava_CJencryptInsts.cpp  -  description
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
/* ctcjava_CJencryptInsts.cpp
 *
 *  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 *  Heimdall <heimdall@bifroest.demon.co.uk>  1998
 *  All rights reserved.  For full licence details see file licences.c
 */
#include "armour.h"
#include "cipher.h"
#include "ctcjlib.h"
#include "ctc.h"
#include "com_ravnaandtines_ctcjava_CJencryptInsts.h"
#include "keyconst.h"
#include "utfcode.h"
#include <ctype.h>
#include <locale.h>

#ifdef __BORLANDC__
#else //VC++ needs more header files for the wide character stuff!
#ifdef _MSC_VER
#include <wchar.h>
#endif
#endif
#include <string.h>


namespace CTCjlib {
    using namespace CTClib;

    static char none[5] = "None";
    static int length = 0;
    static int numkey = 0;
    static pubkey **  to = 0;

    class Watcherctcjava_CJencryptInsts {
public:
        Watcherctcjava_CJencryptInsts() {
        };
        ~Watcherctcjava_CJencryptInsts()
        { 
            if (to) delete[] to;
        }
    };

    static Watcherctcjava_CJencryptInsts watch;

    static encryptInsts rawInsts = { 
        0, 3, 't',
        MDA_MD5,
        CPA_DEFLATE, ARM_NONE, 0,
        {
            {
                CEA_NONE, 0            }
            , {
                0,0            }
            ,{
                0,0            }
            , {
                0,0            }
            , {
                0,0            }
        }
        ,
        /*0,0,*/0,         /* cv_len, cv_data, to */
        0,0,0    };    /* signatory, comments, maxlines */

    static encryptInsts insts;

    extern "C" encryptInsts * getInsts()
    {
        return &insts;
    }

} // namespace

#if defined(CTC_NAMESPACE_SUPPORT)
using namespace CTCjlib;
#endif

static void freename()
{
    if(insts.filename && insts.filename != none) delete[] insts.filename;
}

extern "C" JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_CJencryptInsts_reset
(JNIEnv *, jclass)
{
    insts = rawInsts;
    insts.filename = none;
    for(int i=0; i<length; ++i) to[i] = 0;
    insts.to = to;
    numkey = 0;
    if(length < 20) 
    {
        to = new pubkey*[20];
        length = 20;
        to[0] = 0;
        insts.to = to;
    }
}


extern "C" JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_CJencryptInsts_setFile
(JNIEnv * env , jclass, jstring jfn, jchar jc)
{
    setlocale(LC_ALL, "");
    if('t' == jc) insts.fileType = 't';
    else insts.fileType = 'b'; // fail safe

    freename();
    insts.filename = jstringToMbs(env, jfn);
}

extern "C" JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_CJencryptInsts_setVersion
(JNIEnv *, jclass, jint j)
{
    insts.version = (byte)j;
}

extern "C" JNIEXPORT jboolean JNICALL Java_com_ravnaandtines_ctcjava_CJencryptInsts_addRecipient
(JNIEnv *, jclass, jlong jptr)
{
    if(0 == jptr) return JNI_TRUE;
    pubkey * pub = (pubkey *)jptr;
    if(completeKey(pub))
    {
        to[numkey++] = pub;
        to[numkey] = 0;
        return JNI_TRUE;
    }
    return JNI_FALSE;
}


extern "C" JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_CJencryptInsts_setSignatory
(JNIEnv *, jclass, jlong jptr)
{
    if(0 == jptr) return;
    seckey * sec = (seckey *)jptr;

    // get passphrase for signature
    if(sec && keyStatus(sec) != INTERNAL)
    {
        seckey * keys[1];
        keys[0] = sec;
        if ( cb_need_key(keys, 1)  < 0 ) sec = 0;
    }
    insts.signatory = sec;
}


extern "C" JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_CJencryptInsts_setAlgs
(JNIEnv *, jclass, jint cea, jint cem, jint mda, jint cpa, jint arm)
{
    insts.md_algor = (byte) mda;
    insts.cp_algor = (byte) cpa;
    insts.armour   = (byte) arm;
    if(CEM_CFB == cem || 0 == cem)
    {
        insts.cv_algor[0].cv_algor = (byte)cea;
        insts.cv_algor[0].cv_mode = (byte)((cea&CEA_FLEX_FLAG) ? CEM_CFB : 0);
    }
    else
    {
        insts.cv_algor[0].cv_algor = flexAlg((byte)cea);
        insts.cv_algor[0].cv_mode = (byte) cem;
    }

}
