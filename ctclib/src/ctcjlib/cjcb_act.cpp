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
*/
#include <jni.h>
#include <string>

#if (defined(__BORLANDC__) && __BORLANDC__ < 0x530)
//for some reason, this gets #ifdef'd out in the standard Borland headers!
namespace std {
    typedef basic_string<wchar_t, string_char_traits<wchar_t>, RWSTD_ALLOC_TYPE(wchar_t) >
        wstring;
}
#elif defined(__GNUC__)
#include <wctype.h>

namespace std {
  // The STL for g++ 2.95.2 is broken 
  // 1 - c_str has a hardcoded "" i.e. a char* which throws when we want a wchar_t*
  // 2 - deallocator is called with a void* but it expects wchar_t*
  /*
    typedef basic_string<wchar_t, string_char_traits<wchar_t>, allocator<wchar_t> >
        wstring;
  */

  class wstring 
  {
    wchar_t * data;
  public:
    wstring(const wchar_t *in) {
      data = new wchar_t[wcslen(in)+1];
      wcscpy(data, in);
    }
    ~wstring() {
      if(data) delete [] data;
    }
    size_t length() const {return data ? wcslen(data) : 0;}
    const wchar_t * c_str() const {return data;}
    wstring& operator+(const wstring & rhs)
    {
      wchar_t * newdata = new wchar_t[length()+rhs.length()+1];
      wcscpy(newdata, data);
      wcscpy(newdata+length(), rhs.data);
      delete [] data;
      data = newdata;
      return *this;
    }
    wstring(const wstring & rhs)
    {
      data = new wchar_t[wcslen(rhs.data)+1];
      wcscpy(data, rhs.data);
    }

  private:
    void operator=(const wstring & rhs) {}
    static size_t wcslen(const wchar_t * in)
    {
      size_t result = 0;
      while(*in) {++in; ++result;}
      return result;
    }
    static void wcscpy(wchar_t * out, const wchar_t * in)
    {
      do{
	*out++ = *in++;
      }while(*in);
    }
  };
}

#endif

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

#if defined(CTC_NAMESPACE_SUPPORT)
namespace CTClib {
    using CTCjlib::getEnvironment;
    using CTCjlib::vf_java;
    using CTCjlib::vf_unjava;
    using CTCjlib::copyBFile;
    using CTCjlib::vf_writeableStringBuffer;
    using CTCjlib::logInternalisedKey;
    using CTCjlib::vf_writeableTextArea;
#endif

    /*
     *     Debugging tool
     */
    void stickUpandDie(const char * message);

#define BADPHRASE 0
#define OUTOFTIME 1
#define EYESONLY  2

    /*
     *     Status announcer
     */
    void stickUp(const int flag)
    {
        jclass fileclass = getEnvironment()->FindClass("com/ravnaandtines/ctcjava/CJGlobals");
        jmethodID s = getEnvironment()->GetStaticMethodID(fileclass, 
        "stickUp", "(I)V");

        getEnvironment()->CallStaticVoidMethod(fileclass, s, (jint)flag);
    }

    /*
     *     Secret key disambiguator - rarely used
     */
    int selectID(seckey * keys[], int Nkeys)
    {
        //first off, create an array of Nkeys strings
        jclass javaLangString = getEnvironment()->FindClass("java/lang/String");
        jobjectArray names = getEnvironment()->NewObjectArray(
        (jint)Nkeys, javaLangString, NULL);

        for(int kn = 0; kn < Nkeys; ++kn)
        {
            char name[256];
            name_from_key(publicKey(keys[kn]), name);
            jstring v = mbsToJstring(getEnvironment(), name);
            getEnvironment()->SetObjectArrayElement(names, kn, v);
            getEnvironment()->DeleteLocalRef(v);
        }

        jclass fileclass = getEnvironment()->FindClass("com/ravnaandtines/ctcjava/CJGlobals");
        jmethodID select = getEnvironment()->GetStaticMethodID(fileclass, 
        "selectID", "([Ljava/lang/String;)I");

        jint result = getEnvironment()->CallStaticIntMethod(fileclass, select, names);

        getEnvironment()->DeleteLocalRef(names);
        return (int) result;
    }

    /*
     *     get passphrase for a Secret key
     */
    int getPassphrase(char password[256])
    {
        jclass fileclass = getEnvironment()->FindClass("com/ravnaandtines/ctcjava/CJGlobals");
        jmethodID gpp = getEnvironment()->GetStaticMethodID(fileclass, 
        "getPassphrase", "(Ljava/lang/String;[B)I");

        jstring v = mbsToJstring(getEnvironment(), password);
        jbyteArray r = getEnvironment()->NewByteArray(256);

        jint len = getEnvironment()->CallStaticIntMethod(fileclass, gpp, v, r);
        getEnvironment()->DeleteLocalRef(v);

        if(len < 0)
        {
            getEnvironment()->DeleteLocalRef(r);
            return -1;
        }

        //unpick the value
        if (len > 255) len = 255;

        if(len > 0)
        {
            jbyte * temp = new jbyte[len];
            getEnvironment()->GetByteArrayRegion(r, 0, len, temp);

            for(int i=0; i<len; ++i)
            {
                password[i] = (char)temp[i];
                temp[i] = 0;
            }
            //wipe as best we can
            getEnvironment()->SetByteArrayRegion(r, 0, len, temp);
            delete [] temp;
        }

        password[len]=0;
        getEnvironment()->DeleteLocalRef(r);

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
                logInternalisedKey(keys[offset]);
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
        //get main frame
        jclass fileclass = getEnvironment()->FindClass("com/ravnaandtines/ctcjava/CJGlobals");
        jmethodID gf = getEnvironment()->GetStaticMethodID(fileclass,
        "getFrame", "()Lcom/ravnaandtines/ctcjava/CTCJMainFrame;");

        jobject mainFrame = getEnvironment()->CallStaticObjectMethod(fileclass, gf);

        //get file frame constructor and create one
        jclass frameclass = getEnvironment()->FindClass("com/ravnaandtines/ctcjava/CJFileFrame");
        jmethodID init = getEnvironment()->GetMethodID(frameclass,
        "<init>", "(Lcom/ravnaandtines/ctcjava/CTCJMainFrame;)V");

        jobject frame = getEnvironment()->NewObject(frameclass, init, mainFrame);

        //lodge new file frame with main
        jmethodID notify = getEnvironment()->GetStaticMethodID(fileclass,
        "notify", "(Lcom/ravnaandtines/ctcjava/CJFileFrame;)V");
        getEnvironment()->CallStaticVoidMethod(fileclass, notify, frame);


        if(details->signatory)
        {
            /* the following addition assumes that the granularity of
             ** time_t is one second.  I am not sure if this is guaranteed */
            time_t timeStamp;
            char name[256];
            timeStamp = details->timestamp + datum_time();
            name_from_key(details->signatory, name);
            //printf("File from: %s\n with %s signature @ %s\n", name,
            //details->valid_sig ? "good" : "bad", ctime(&timeStamp));

            jstring jname = mbsToJstring(getEnvironment(), name);
            //jstring jtime = mbsToJstring(getEnvironment(), ctime(&timeStamp));
            jboolean jok = (jboolean)details->valid_sig;

            jmethodID ss = getEnvironment()->GetMethodID(frameclass,
            "showSig", "(Ljava/lang/String;IZ)V");
            getEnvironment()->CallVoidMethod(frame, ss, jname, (jint)timeStamp, jok);

            getEnvironment()->DeleteLocalRef(jname);
            //getEnvironment()->DeleteLocalRef(jtime);
        }

        if(!results) return;

        if(details->fileName)
        {
            jstring dir;
            jstring fn;

            char * leaf = strrchr(details->fileName, '/');
            if(leaf)
            {
                *leaf = 0;
                dir = mbsToJstring(getEnvironment(), details->fileName);
                fn = mbsToJstring(getEnvironment(), leaf+1);
                *leaf = '/';
            }
            else
            {
                dir = mbsToJstring(getEnvironment(), "");
                fn = mbsToJstring(getEnvironment(), details->fileName);
            }
            jmethodID sn = getEnvironment()->GetMethodID(frameclass,
            "setPath", "(Ljava/lang/String;Ljava/lang/String;)V");
            getEnvironment()->CallVoidMethod(frame, sn, dir, fn);

            getEnvironment()->DeleteLocalRef(dir);
            getEnvironment()->DeleteLocalRef(fn);
        }

        if(0 == strcmp(details->fileName, "_CONSOLE"))
        {
            stickUp(EYESONLY);
        }

        {
            jmethodID st = getEnvironment()->GetMethodID(frameclass,
            "setText", "(Z)V");
            getEnvironment()->CallVoidMethod(frame, st,
            (jboolean)('t' == details->typeByte));
        }

        {
            jfieldID isText = getEnvironment()->GetFieldID(frameclass, "isText", "Z");
            getEnvironment()->SetBooleanField(frame, isText, (jboolean)(details->typeByte != 'b'));
        }

        //have to transition between our local vf_tempfile and the
        //wide outside world. This is tedious and non-trivial
        switch(details->typeByte)
        {
        case 't':
            {
                //Create a StringBuffer;
                jclass jsbClass = getEnvironment()->FindClass("java/lang/StringBuffer");
                jmethodID init = getEnvironment()->GetMethodID(jsbClass,
                "<init>", "(I)V");
                jobject buff = getEnvironment()->NewObject(jsbClass, init, (jint)vf_length(results));

                //do the copying as text
                DataFileP to = vf_writeableStringBuffer(getEnvironment(), buff);
                UTF8decode(results, to);
                vf_unjava(to);

                //pack it in
                jmethodID sa = getEnvironment()->GetMethodID(frameclass, "setASCII", "(Ljava/lang/StringBuffer;)V");
                getEnvironment()->CallVoidMethod(frame, sa, buff);

                //release what we've created here
                getEnvironment()->DeleteLocalRef(buff);
            }
            break;

        case 'b':
            {
                // get tempfile constructor and create one
                jclass tmpclass = getEnvironment()->FindClass("com/ravnaandtines/ctcjava/CJTempfile");
                jmethodID init = getEnvironment()->GetMethodID(tmpclass,"<init>","()V");
                jobject sink = getEnvironment()->NewObject(tmpclass, init);

                // do the copying as binary
                DataFileP to  = vf_java(getEnvironment(), sink);
                copyBFile(results, to);
                vf_setpos(to, 0);
                vf_unjava(to);

                // pack it in
                jmethodID sb = getEnvironment()->GetMethodID(frameclass,
                    "setBinary", "(Lcom/ravnaandtines/ctcjava/CJTempfile;)V");
                getEnvironment()->CallVoidMethod(frame, sb, sink);

                // release what we've created here
                getEnvironment()->DeleteLocalRef(sink);
            }
            break;
        }

        //release what we've created here
        getEnvironment()->DeleteLocalRef(frame);
        getEnvironment()->DeleteLocalRef(mainFrame);
        return;
    }

    static DataFileP getSplitPanel(void)  // ad hoc *text* output context
    {
        // get main frame
        jclass fileclass = getEnvironment()->FindClass("com/ravnaandtines/ctcjava/CJGlobals");
        jmethodID gf = getEnvironment()->GetStaticMethodID(fileclass,
        "getFrame", "()Lcom/ravnaandtines/ctcjava/CTCJMainFrame;");

        jobject mainFrame = getEnvironment()->CallStaticObjectMethod(fileclass, gf);

        // get file frame constructor and create one
        jclass frameclass = getEnvironment()->FindClass("com/ravnaandtines/ctcjava/CJFileFrame");
        jmethodID init = getEnvironment()->GetMethodID(frameclass,
        "<init>", "(Lcom/ravnaandtines/ctcjava/CTCJMainFrame;)V");

        jobject frame = getEnvironment()->NewObject(frameclass, init, mainFrame);

        // lodge new file frame with main
        jmethodID notify = getEnvironment()->GetStaticMethodID(fileclass,
        "notify", "(Lcom/ravnaandtines/ctcjava/CJFileFrame;)V");
        getEnvironment()->CallStaticVoidMethod(fileclass, notify, frame);

        // get the text area as a writeable text stream
        jfieldID fileText = getEnvironment()->GetFieldID(frameclass, "fileText", "L"TEXTAREACLASS";");
        jobject textArea = getEnvironment()->GetObjectField(frame, fileText);
        DataFileP to  = vf_writeableTextArea(getEnvironment(), textArea);

        // tidy up references and return wanted value
        getEnvironment()->DeleteLocalRef(frame);
        getEnvironment()->DeleteLocalRef(textArea);
        return to;
    }

    wchar_t * unpeelJavaString(jobject string)
    {
        jclass stringClass = getEnvironment()->FindClass("java/lang/String");
        jmethodID length = getEnvironment()->GetMethodID(stringClass,
        "length", "()I");

        jint nchars = getEnvironment()->CallIntMethod(string, length);
        wchar_t * buffer = new wchar_t[nchars+1];
        buffer[nchars] = L'\0';

        jmethodID charAt = getEnvironment()->GetMethodID(stringClass,
        "charAt", "(I)C");

        for(jint i=0; i<nchars; ++i)
        {
            buffer[i] = (wchar_t) getEnvironment()->CallCharMethod(string, charAt, i);
        }
        return buffer;
    }


    static DataFileP getInputFile(cb_filedesc * filedesc, const char * caption)
    {
        // stick up a file dialog,

        // get main frame
        jclass fileclass = getEnvironment()->FindClass("com/ravnaandtines/ctcjava/CJGlobals");
        jmethodID gf = getEnvironment()->GetStaticMethodID(fileclass,
        "getFrame", "()Lcom/ravnaandtines/ctcjava/CTCJMainFrame;");

        jobject mainFrame = getEnvironment()->CallStaticObjectMethod(fileclass, gf);

        // get FileDialog class
        jclass fileDialogClass = getEnvironment()->FindClass("java/awt/FileDialog");
        jmethodID init = getEnvironment()->GetMethodID(fileDialogClass,
        "<init>", "(Ljava/awt/Frame;Ljava/lang/String;I)V");

        jstring open = mbsToJstring(getEnvironment(), caption);
        jfieldID loadID = getEnvironment()->GetStaticFieldID(fileDialogClass,"LOAD","I");
        jint load = getEnvironment()->GetStaticIntField(fileDialogClass,loadID);

        // create dialog
        jobject filer = getEnvironment()->NewObject(fileDialogClass, init, mainFrame, open, load);
        getEnvironment()->DeleteLocalRef(open);

        // show it
        jmethodID show = getEnvironment()->GetMethodID(fileDialogClass,
        "show", "()V");
        getEnvironment()->CallVoidMethod(filer, show);

        // get file name - null if cancelled
        jmethodID getFile = getEnvironment()->GetMethodID(fileDialogClass,
        "getFile", "()Ljava/lang/String;");

        jobject fileName = getEnvironment()->CallObjectMethod(filer, getFile);
        if(0 == fileName)
        {
            getEnvironment()->DeleteLocalRef(filer);
            return 0; // safely aborts
        }

        jmethodID getDirectory = getEnvironment()->GetMethodID(fileDialogClass,
        "getDirectory", "()Ljava/lang/String;");

        jobject dirName = getEnvironment()->CallObjectMethod(filer, getDirectory);
        getEnvironment()->DeleteLocalRef(filer);  // done with now

        // get the two jstrings into one std::string
        // this is less efficient than it might be
        wchar_t * fnw = unpeelJavaString(fileName);
        std::wstring fn(fnw);
        delete[] fnw;

        wchar_t * dnw = unpeelJavaString(dirName);
        std::wstring dn(dnw);
        delete[] dnw;

        std::wstring path = dn+fn;
        jstring jpath = getEnvironment()->NewString( (const jchar *)path.c_str(), (jsize) path.length());

        // test the file type
        jmethodID isTextFile = getEnvironment()->GetStaticMethodID(fileclass,
        "isTextFile", "(Ljava/lang/String;)Z");
        jboolean text = getEnvironment()->CallStaticBooleanMethod(fileclass, isTextFile, jpath);
        getEnvironment()->DeleteLocalRef(jpath);  // done with now

        // set filedesc.file_type to TEXTCYPHER or BINCYPHER
        filedesc->file_type = text ? TEXTCYPHER  : BINCYPHER;

        // return the handle (null aborts OK)
        // it is simplest to set this up via the stdio vf_open
        size_t nmbs = (MB_CUR_MAX*path.length())+1;
        char * mbstr = new char [nmbs];
        memset(mbstr, 0, nmbs);
        char *m = mbstr;
        const wchar_t *w = path.c_str();
        for(; *w; ++w)
        {
            int k = wctomb(m, *w);
            if(k>0) m+=k;
        }
        std::string mbsname(mbstr);
        delete[] mbstr;

        return vf_open(mbsname.c_str(), READ, filedesc->file_type);
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
            return getInputFile(filedesc, "Open cyphertext...");

        case SIGNEDFILE: // we want to apply the signature
            {
                DataFileP grab = getInputFile(filedesc, "Open file...");
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
        jclass fileclass = getEnvironment()->FindClass("com/ravnaandtines/ctcjava/CJGlobals");

        jmethodID getConvAlg = getEnvironment()->GetStaticMethodID(fileclass,
        "getConvAlg", "()Lcom/ravnaandtines/ctcjava/CJBytes;");
        jobject alg = getEnvironment()->CallStaticObjectMethod(fileclass, getConvAlg);
        if(!alg)
        {
            return FALSE;
        }

        // unpick the algorithm details
        jclass algclass = getEnvironment()->FindClass("com/ravnaandtines/ctcjava/CJBytes");
        jfieldID detailf = getEnvironment()->GetFieldID(algclass, "data", "[B");

        jbyteArray detailsA = static_cast<jbyteArray>
            (getEnvironment()->GetObjectField(alg, detailf));
        if(!detailsA)
        {
            return FALSE;
        }

        jboolean isCopy;
        int i;
        jbyte * detailData = getEnvironment()->GetByteArrayElements(detailsA, &isCopy);
        jint    dsz = getEnvironment()->GetArrayLength(detailsA) / 2;

        for(i = 0; i< dsz; ++i)
        {
            details[i].cv_algor = detailData[2*i];
            details[i].cv_mode  = detailData[2*i+1];
        }
        if(JNI_TRUE == isCopy)
        {
            getEnvironment()->ReleaseByteArrayElements(detailsA, detailData, 0);
        }
        getEnvironment()->DeleteLocalRef(alg);

        // now get the passphrase
        char password[256];
        password[0] = 0;
        *keylen = getPassphrase(password);

        if(((int)*keylen) < 0) return FALSE;

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

#if defined(CTC_NAMESPACE_SUPPORT)
} // end of namespace
#endif
/* end of file callback.c */
