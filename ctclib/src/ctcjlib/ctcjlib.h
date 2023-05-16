/***************************************************************************
 *                           ctcjlib.h  -  description
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
/* ctcjlib.h
 *
 *  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 *  Heimdall <heimdall@bifroest.demon.co.uk>  1998
 *  All rights reserved.  For full licence details see file licences.c
 */

#include <jni.h>
#include "abstract.h"

#ifndef _Included_ctcjlib
#define _Included_ctcjlib
#if defined(CTC_NAMESPACE_SUPPORT)

namespace CTCjlib {

    using CTClib::DataFileP;
    using CTClib::decode_context;
    using CTClib::encryptInsts;
    using CTClib::seckey;

#endif 

    extern "C" {

        JNIEnv * getEnvironment(void);
        void setEnvironment(JNIEnv *);

        void copyBFile(DataFileP from, DataFileP to);
        decode_context *getContext();
        encryptInsts * getInsts();

        DataFileP vf_java(JNIEnv * env, jobject jfile, bool trim = false);
        void vf_unjava(DataFileP file);

        DataFileP vf_writeableTextArea(JNIEnv * env, jobject jfile);
        DataFileP vf_writeableStringBuffer(JNIEnv * env, jobject jfile);

    };

    bool writeKeyrings();
    bool backupKeyrings();

    void logInternalisedKey(seckey * key);
    void clearInternalisedKeys();
    void clearInternalisedKey(seckey * key);

    jobject wrapSeckey(JNIEnv * env, seckey * sec_key);

#if defined(CTC_NAMESPACE_SUPPORT)
} // namespace CTCjlib
namespace CTClib {
#endif

    void makeSessionData(DataFileP source);

#if defined(CTC_NAMESPACE_SUPPORT)
}
#endif

#define TEXTAREACLASS "data/gwt/CJTextAreaGadget"


#endif //_Included_ctcjlib
