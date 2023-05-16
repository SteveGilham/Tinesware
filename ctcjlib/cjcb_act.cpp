/***************************************************************************
                          cjcb_act.cpp  -  description
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
/* cjcb_act.cpp
*
*  This is an JNI-based implementation of CTC's ctccbk module.
*
*  These are complex user interaction callbacks
*
*  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1998
*  All rights reserved.  For full licence details see file licences.c
*

This code is in the context of namespace CTClib.

This has probably been fixed in the intervening years 
  // The basic_string<> template for g++ 2.95.2 is broken 
  // 1 - c_str has a hardcoded "" i.e. a char* which throws when we want a wchar_t*
  // 2 - deallocator is called with a void* but it expects wchar_t*

but in 2006 has not been tested against a current Linux gcc


*/
#include <jni.h>
#include <string>



#include "callback.h"
#include "ctcjlib.h"
#include "ctc.h"
#include "utfcode.h"
#include "usrbreak.h"

#include "port_io.h"
#include "cipher.h"
#include "hash.h"
#include "keyconst.h"
#include "callback.h"
#include "utils.h"
#include "hashpass.h"
#include "keyhash.h"
#include "pkcipher.h"

namespace CTClib {

    /*
     *     Debugging tool
     */
    void stickUpandDie(const char * message);

    enum MessageReasons {
        BADPHRASE=0,
        OUTOFTIME,
        EYESONLY
    };

    /*
     *     Status announcer
     */
    void stickUp(const int flag)
    {
        jclass fileclass = CTCjlib::getEnvironment()->FindClass("com/ravnaandtines/ctcjava/GlobalData");
        jmethodID s = CTCjlib::getEnvironment()->GetStaticMethodID(fileclass, "stickUp", "(I)V");
        CTCjlib::getEnvironment()->CallStaticVoidMethod(fileclass, s, (jint)flag);
    }

    /*
     *     Secret key disambiguator - rarely used
     */
    int selectID(seckey * keys[], int Nkeys)
    {
        //first off, create an array of Nkeys strings
        jclass javaLangString = CTCjlib::getEnvironment()->FindClass("java/lang/String");
        CTCjlib::autoDeref<jobjectArray> names (CTCjlib::getEnvironment()->NewObjectArray((jint)Nkeys, javaLangString, NULL));

        for(int kn = 0; kn < Nkeys; ++kn)
        {
            char name[256];
            name_from_key(publicKey(keys[kn]), name);
            jstring v = mbsToJstring(CTCjlib::getEnvironment(), name);
            CTCjlib::getEnvironment()->SetObjectArrayElement(names, kn, v);
            CTCjlib::getEnvironment()->DeleteLocalRef(v);
        }

        jclass fileclass = CTCjlib::getEnvironment()->FindClass("com/ravnaandtines/ctcjava/GlobalData");
        jmethodID select = CTCjlib::getEnvironment()->GetStaticMethodID(fileclass, "selectID", "([Ljava/lang/String;)I");
        jint result = CTCjlib::getEnvironment()->CallStaticIntMethod(fileclass, select, names);
        return (int) result;
    }

    /*
     *     get passphrase for a Secret key
     */
    int getPassphrase(char password[256])
    {
        jclass fileclass = CTCjlib::getEnvironment()->FindClass("com/ravnaandtines/ctcjava/GlobalData");
        jmethodID gpp = CTCjlib::getEnvironment()->GetStaticMethodID(fileclass, 
                            "getPassphrase", "(Ljava/lang/String;[B)I");

        CTCjlib::autoDeref<jstring> v (mbsToJstring(CTCjlib::getEnvironment(), password));
        CTCjlib::autoDeref<jbyteArray> r (CTCjlib::getEnvironment()->NewByteArray(256));

        jint len = CTCjlib::getEnvironment()->CallStaticIntMethod(fileclass, gpp, v, r);

        if(len < 0)
        {
            return -1;
        }

        //unpick the value
        if (len > 255) len = 255;

        if(len > 0)
        {
            std::vector<jbyte> temp(len);
            CTCjlib::getEnvironment()->GetByteArrayRegion(r, 0, len, &temp[0]);

            // allows for jbyte to be larger than a char
            for(int i=0; i<len; ++i)
            {
                password[i] = (char)temp[i];
                temp[i] = 0;
            }
            //wipe as best we can
            CTCjlib::getEnvironment()->SetByteArrayRegion(r, 0, len, &temp[0]);
        }

        password[len]=0;
        return len;
    }

    /*
     *     Standard callback - attempt to internalise a key
     */
    extern "C" int cb_need_key(seckey * keys[], int Nkeys)
    {
        int offset = 0;
        if(Nkeys > 1) offset = selectID(keys, Nkeys);

        if(offset < 0) return -1;        /*killed at this point*/

        char password[256];

        for(int i=0; i<3; ++i)
        {
            name_from_key(publicKey(keys[offset]), password);
            int len = getPassphrase(password);
            if(len < 0) return -1;            /*Cancel*/
            if(internalise_seckey(keys[offset], password))
            {
                CTCjlib::logInternalisedKey(keys[offset]);
                return offset;
            }
            if(i < 2)
            {
                stickUp(BADPHRASE);
            }
            else
            {
                stickUp(OUTOFTIME);
            }
        }
        return -1;
    }

    /*
     *     Copy text file line by line
     */
#ifdef __BORLANDC__
#pragma warn -aus
#endif
    static void copyTFile(DataFileP from, DataFileP to)
    {
        char buffer[256];
        long length;
        vf_setpos(from, 0);
        vf_setpos(to, 0);
        while((length = vf_readline(buffer, 256, from)) >= 0)
            vf_writeline(buffer, to);
    }
#ifdef __BORLANDC__
#pragma warn .aus
#endif


    /*
     *     Squirt result into a file frame
     */
    extern "C" void cb_result_file(DataFileP results, cb_details * details)
    {
        //get a new output document
        jclass fileclass = CTCjlib::getEnvironment()->FindClass("com/ravnaandtines/ctcjava/GlobalData");
        jclass documentClass = CTCjlib::getEnvironment()->FindClass("com/ravnaandtines/ctcjava/Document");

        jmethodID createDocument = CTCjlib::getEnvironment()->GetStaticMethodID(fileclass,
            "createDocument", "()Lcom/ravnaandtines/ctcjava/Document;");
        jobject document = CTCjlib::getEnvironment()->CallStaticObjectMethod(fileclass, createDocument);

        if(details->signatory)
        {
            /* the following addition assumes that the granularity of
             ** time_t is one second.  I am not sure if this is guaranteed */
            time_t timeStamp;
            char name[256];
            timeStamp = details->timestamp + datum_time();
            name_from_key(details->signatory, &name[0]);

            jstring jname = mbsToJstring(CTCjlib::getEnvironment(), name);
            jboolean jok = (jboolean)details->valid_sig;

            jmethodID ss = CTCjlib::getEnvironment()->GetMethodID(documentClass,
                                            "displaySignatureState", "(Ljava/lang/String;IZ)V");
            CTCjlib::getEnvironment()->CallVoidMethod(document, ss, jname, (jint)timeStamp, jok);

            CTCjlib::getEnvironment()->DeleteLocalRef(jname);
        }

        if(!results) return;

        if(details->fileName)
        {
            jstring fileName = mbsToJstring(CTCjlib::getEnvironment(), details->fileName);
            jmethodID setPath = CTCjlib::getEnvironment()->GetMethodID(documentClass,
                        "setPath", "(Ljava/lang/String;)V");
            CTCjlib::getEnvironment()->CallVoidMethod(document, setPath, fileName);
            CTCjlib::getEnvironment()->DeleteLocalRef(fileName);
        }

        if(0 == strcmp(details->fileName, "_CONSOLE"))
        {
            stickUp(EYESONLY);
        }

        {
            jmethodID st = CTCjlib::getEnvironment()->GetMethodID(documentClass,
            "setText", "(Z)V");
            CTCjlib::getEnvironment()->CallVoidMethod(document, st,
                                (jboolean)('t' == details->typeByte));
        }

        //have to transition between our local vf_tempfile and the
        //wide outside world. This is tedious and non-trivial
        switch(details->typeByte)
        {
        case 't':
            {
                //Create a StringBuffer;
                jclass jsbClass = CTCjlib::getEnvironment()->FindClass("java/lang/StringBuffer");
                jmethodID init = CTCjlib::getEnvironment()->GetMethodID(jsbClass,
                "<init>", "(I)V");
                jobject buff = CTCjlib::getEnvironment()->NewObject(jsbClass, init, (jint)vf_length(results));

                {
                    // do the copying as text
                    CTCjlib::StringBufferVirtualFile to(CTCjlib::getEnvironment(), buff);
                    UTF8decode(results, to);
                    vf_setpos(to, 0);
                }

                //pack it in
                jmethodID sa = CTCjlib::getEnvironment()->GetMethodID(documentClass, "setASCII", "(Ljava/lang/StringBuffer;)V");
                CTCjlib::getEnvironment()->CallVoidMethod(document, sa, buff);

                //release what we've created here
                CTCjlib::getEnvironment()->DeleteLocalRef(buff);
            }
            break;

        case 'b':
            {
                // get tempfile constructor and create one
                jclass tmpclass = CTCjlib::getEnvironment()->FindClass("com/ravnaandtines/ctcjava/InMemoryPortIOFile");
                jmethodID init = CTCjlib::getEnvironment()->GetMethodID(tmpclass,"<init>","()V");
                jobject sink = CTCjlib::getEnvironment()->NewObject(tmpclass, init);

                {
                    // do the copying as binary
                    CTCjlib::VirtualFile to(CTCjlib::getEnvironment(), sink, false);
                    CTCjlib::copyBFile(results, to);
                    vf_setpos(to, 0);
                }

                // pack it in
                jmethodID sb = CTCjlib::getEnvironment()->GetMethodID(documentClass,
                    "setBinary", "(Lcom/ravnaandtines/ctcjava/InMemoryPortIOFile;)V");
                CTCjlib::getEnvironment()->CallVoidMethod(document, sb, sink);

                // release what we've created here
                CTCjlib::getEnvironment()->DeleteLocalRef(sink);
            }
            break;
        }

        //release what we've created here
        CTCjlib::getEnvironment()->DeleteLocalRef(document);
        return;
    }

    static DataFileP getSplitPanel(void)  // ad hoc *text* output context
    {
        //get a new output document
        jclass fileclass = CTCjlib::getEnvironment()->FindClass("com/ravnaandtines/ctcjava/GlobalData");
        jmethodID createDocument = CTCjlib::getEnvironment()->GetStaticMethodID(fileclass,
            "createDocument", "()Lcom/ravnaandtines/ctcjava/Document;");
        jobject document = CTCjlib::getEnvironment()->CallStaticObjectMethod(fileclass, createDocument);

        // get the text area as a writeable text stream
        jclass documentClass = CTCjlib::getEnvironment()->FindClass("com/ravnaandtines/ctcjava/Document");
        jfieldID fileText = CTCjlib::getEnvironment()->GetFieldID(documentClass, "fileText", "L"TEXTAREACLASS";");
        jobject textArea = CTCjlib::getEnvironment()->GetObjectField(document, fileText);
        DataFileP to  = CTCjlib::vf_writeableTextArea(CTCjlib::getEnvironment(), textArea);

        // tidy up references and return wanted value
        CTCjlib::getEnvironment()->DeleteLocalRef(document);
        CTCjlib::getEnvironment()->DeleteLocalRef(textArea);
        return to;
    }

    /*
    std::vector<wchar_t> unpeelJavaString(jobject string)
    {
        jclass stringClass = CTCjlib::getEnvironment()->FindClass("java/lang/String");
        jmethodID length = CTCjlib::getEnvironment()->GetMethodID(stringClass, "length", "()I");

        jint nchars = CTCjlib::getEnvironment()->CallIntMethod(string, length);
        std::vector<wchar_t> buffer(nchars+1);
        buffer[nchars] = L'\0';

        jmethodID charAt = CTCjlib::getEnvironment()->GetMethodID(stringClass, "charAt", "(I)C");

        for(jint i=0; i<nchars; ++i)
        {
            buffer[i] = (wchar_t) CTCjlib::getEnvironment()->CallCharMethod(string, charAt, i);
        }
        return buffer;
    }
    */


    // used to get cyphertext to which a split key or signature applies
    static DataFileP getInputFile(cb_filedesc * filedesc, const cb_purpose purpose)
    {
        // stick up a file dialog,
        jclass globalClass = CTCjlib::getEnvironment()->FindClass("com/ravnaandtines/ctcjava/GlobalData");
        jmethodID getInputFileMethod = CTCjlib::getEnvironment()->GetStaticMethodID(globalClass,
            "getInputFile", "(I)Ljava/lang/String;");

        // get file name - null if cancelled
        jobject fileName = CTCjlib::getEnvironment()->CallStaticObjectMethod(globalClass,
            getInputFileMethod, (int) purpose);
        if(0 == fileName)
        {
            return 0; // safely aborts
        }

        // test the file type
        jmethodID isTextFile = CTCjlib::getEnvironment()->GetStaticMethodID(globalClass,
            "isTextFile", "(Ljava/lang/String;)Z");
        jboolean text = CTCjlib::getEnvironment()->CallStaticBooleanMethod(globalClass, isTextFile, fileName);

        // set filedesc.file_type to TEXTCYPHER or BINCYPHER
        filedesc->file_type = text ? TEXTCYPHER  : BINCYPHER;

        std::vector<char> mbsName;
        jstringToMbs(CTCjlib::getEnvironment(), static_cast<jstring>(fileName), mbsName);

        return vf_open(&mbsName[0], READ, filedesc->file_type);
    }

    /* cb_signedFile:
     **   Case of detached signature - request the file that has been signed
     **   from the user to compute and check signature */
    extern "C" DataFileP cb_getFile(cb_filedesc * filedesc)
    {
        switch(filedesc->purpose)
        {
        case SPLITKEY: // we are about to dump a session key
            return getSplitPanel();

        case CYPHERTEXTFILE: // we want to use the session key
            return getInputFile(filedesc, filedesc->purpose);

        case SIGNEDFILE: // we want to apply the signature
            {
                DataFileP grab = getInputFile(filedesc, filedesc->purpose);
                if(grab && BINCYPHER != filedesc->file_type)
                {
                    // need to get text into canonical form
                    DataFileP canon = vf_tempfile(vf_length(grab));
                    vf_CCmode(CANONICAL, canon);
                    copyTFile(grab, canon);
                    vf_close(grab);
                    grab = canon;
                }
                return grab;
            }

        default:
            stickUpandDie("cb_getFile: default branch taken");
        }
        /* this is a place-holder*/
        return 0;
    }

    /* cb_convKey:
     **   The message being examined has no public key encrypted parts; it is
     **   presumed conventionally encrypted, and a request is made for the
     **   (single) algorithm and key from hashed passphrase */
    extern "C" boolean cb_convKey(cv_details *details, void **key, size_t *keylen)
    {
        // pop up an enquiry
        jclass parametersClass = CTCjlib::getEnvironment()->FindClass("com/ravnaandtines/ctcjava/EncryptionParameters");

        jmethodID getConvAlg = CTCjlib::getEnvironment()->GetStaticMethodID(parametersClass,
                "getCEAParameters", "()Lcom/ravnaandtines/ctcjava/ByteArrayWrapper;");
        CTCjlib::autoDeref<jobject> alg(CTCjlib::getEnvironment()->CallStaticObjectMethod(parametersClass, getConvAlg));
        if(!alg)
        {
            return FALSE;
        }

        // unpick the algorithm details
        jclass arrayClass = CTCjlib::getEnvironment()->FindClass("com/ravnaandtines/ctcjava/ByteArrayWrapper");
        jfieldID data = CTCjlib::getEnvironment()->GetFieldID(arrayClass, "data", "[B");

        CTCjlib::autoDeref<jbyteArray> detailsA(static_cast<jbyteArray>
            (CTCjlib::getEnvironment()->GetObjectField(alg, data)));

        if(!detailsA)
        {
            return FALSE;
        }

        jboolean isCopy;
        int i;
        jbyte * detailData = CTCjlib::getEnvironment()->GetByteArrayElements(detailsA, &isCopy);
        jint    dsz = CTCjlib::getEnvironment()->GetArrayLength(detailsA) / 2;

        for(i = 0; i< dsz; ++i)
        {
            details[i].cv_algor = detailData[2*i];
            details[i].cv_mode  = detailData[2*i+1];
        }
        if(JNI_TRUE == isCopy)
        {
            CTCjlib::getEnvironment()->ReleaseByteArrayElements(detailsA, detailData, 0);
        }


        // now get the passphrase

        jmethodID getConvPhrase = CTCjlib::getEnvironment()->GetStaticMethodID(parametersClass,
                "getCEAPassphrase", "()Lcom/ravnaandtines/ctcjava/ByteArrayWrapper;");
        CTCjlib::autoDeref<jobject> phrase(CTCjlib::getEnvironment()->CallStaticObjectMethod(parametersClass, getConvPhrase));
        if(!phrase)
        {
            return FALSE;
        }
        CTCjlib::autoDeref<jbyteArray> phraseA(static_cast<jbyteArray>
            (CTCjlib::getEnvironment()->GetObjectField(alg, data)));
        if(!phraseA)
        {
            return FALSE;
        }
        dsz = CTCjlib::getEnvironment()->GetArrayLength(phraseA);
        if(dsz >= 255)
        {
            return FALSE;
        }

        detailData = CTCjlib::getEnvironment()->GetByteArrayElements(phraseA, &isCopy);

        char password[256];
        memset(password, 0, 256);
        for(i = 0; i<dsz && i<255; ++i)
        {
            password[i] = detailData[i];
            detailData[i] = 0;
        }

        // assume just one algorithm for conventional encryption
        *keylen = cipherKey(details[0].cv_algor);
        *key = zmalloc(*keylen);

        byte s2k[2];
        s2k[0] = 0;
        s2k[1] = MDA_MD5;
        hashpassEx(password, (byte*)*key, (int) *keylen, s2k, FALSE);
        memset(password, 0, 256);

        return TRUE;
    }

} // end of namespace CTClib
/* end of file */
