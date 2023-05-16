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
 *
 * As this is 2006, and even the oldest compiler I'm using for compatibility
 * is namespace compatible, I'm going to require namespaces.
 */

#include <jni.h>
#include "abstract.h"

#ifndef _Included_ctcjlib
#define _Included_ctcjlib

#ifndef CTC_NAMESPACE_SUPPORT
#error "C++ namespace support and a decent STL is assumed; sorry!"
#endif

namespace CTCjlib {

    using CTClib::DataFileP;
    using CTClib::decode_context;
    using CTClib::encryptInsts;
    using CTClib::seckey;

    void copyBFile(DataFileP from, DataFileP to);
    decode_context *getContext();
    encryptInsts * getInsts();

    class AbstractVirtualFile
    {
    private:
        CTClib::DataFileP file;
    protected:
        AbstractVirtualFile();
        void setVFHandle(CTClib::DataFileP);
    public:
        virtual ~AbstractVirtualFile();
        operator CTClib::DataFileP () {return file;}
    private:
        AbstractVirtualFile(const AbstractVirtualFile &);
        AbstractVirtualFile& operator=(const AbstractVirtualFile &);
    };
    

    class VirtualFile : public AbstractVirtualFile
    {
    public:
        VirtualFile(JNIEnv * env, jobject jfile, bool trim=true);
        ~VirtualFile() {}
    };

    class StringBufferVirtualFile : public AbstractVirtualFile
    {
    public:
        StringBufferVirtualFile(JNIEnv * env, jobject jfile);
        ~StringBufferVirtualFile() {}
    };

    DataFileP vf_writeableTextArea(JNIEnv * env, jobject jfile);

    JNIEnv * getEnvironment(void);
    void setEnvironment(JNIEnv *);

    bool writeKeyrings();
    bool backupKeyrings();

    void logInternalisedKey(seckey * key);
    void clearInternalisedKeys();
    void clearInternalisedKey(seckey * key);

    jobject wrapSeckey(JNIEnv * env, seckey * sec_key);

    template<typename T> class autoDeref {
    private: 
        T j;
    public:
        autoDeref(T jthing) : j(jthing) {}
        ~autoDeref() {getEnvironment()->DeleteLocalRef(j);}
        operator T () {return j;}
    private:
        autoDeref(const autoDeref &);
        autoDeref & operator=(const autoDeref &);
    };


} // namespace CTCjlib

namespace CTClib {
    void makeSessionData(DataFileP source);
} // namespace CTClib

#define TEXTAREACLASS "javax/swing/JTextArea"

#endif //_Included_ctcjlib
