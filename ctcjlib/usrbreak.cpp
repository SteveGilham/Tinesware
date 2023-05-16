/***************************************************************************
                          usrbreak.cpp  -  description
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
/* usrbreak.c - java version
 *
 *  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 *  Heimdall <heimdall@bifroest.demon.co.uk>  1996
 *  All rights reserved.  For full licence details see file licences.c
 */
#include <jni.h>
#include "ctcjlib.h"
#include "utfcode.h"

namespace CTClib {
    using CTCjlib::getEnvironment;


    static int log = 0;

    extern "C" int user_break(void)    /* returns true if the user has requested operation abort */
    {
        jclass fileclass = getEnvironment()->FindClass("com/ravnaandtines/ctcjava/GlobalData");
        jfieldID userbreak = getEnvironment()->GetStaticFieldID(fileclass, 
        "userbreak", "Z");

        int result = getEnvironment()->GetStaticBooleanField(fileclass, userbreak);

        
        jmethodID s  = getEnvironment()->GetStaticMethodID(fileclass, 
                       "pulse", "()V"); 
        getEnvironment()->CallStaticVoidMethod(fileclass, s);

        ++log;
        if(0 == log%0x100)
        {
#if 0
            // this is garbage - not clear which class it's trying to repaint.
            // plus, it needs the whole threading model sorted out.
            jmethodID gf = getEnvironment()->GetStaticMethodID(fileclass,
                "getFrame", "()Lcom/ravnaandtines/ctcjava/Application;"); // mehtod removed

            jobject mainFrame = getEnvironment()->CallStaticObjectMethod(fileclass, gf);
            jclass frameclass = getEnvironment()->FindClass("com/ravnaandtines/ctcjava/Document");
            jmethodID rp = getEnvironment()->GetMethodID(frameclass, "repaint", "(J)V");
            getEnvironment()->CallVoidMethod(mainFrame, rp, (jlong)1); //TODO ???
#endif
        }

        //clear flag after use!
        if(result)
        {
            getEnvironment()->SetStaticBooleanField(fileclass, userbreak, JNI_FALSE);
        }

        return result; 
    }

    void stickUpandDie(const char * message)
    {
        jclass fileclass = getEnvironment()->FindClass("com/ravnaandtines/ctcjava/GlobalData");
        jmethodID jcb = getEnvironment()->GetStaticMethodID(fileclass, 
                                    "stickUpandDie", "(Ljava/lang/String;)V");

        jstring v = mbsToJstring(getEnvironment(), message); 

        getEnvironment()->CallStaticVoidMethod(fileclass, jcb, v);
        getEnvironment()->DeleteLocalRef(v);        /*FWIW*/
    }

    // called by the CTC core code
    extern "C" void bug_check(const char * text)
    {
        stickUpandDie(text);
    }

}/*end of namespace CTClib */
/* end of file usrbreak.c */

