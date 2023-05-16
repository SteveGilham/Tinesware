/***************************************************************************
                          CTC.cpp  -  description
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
/* CTC.cpp 
 *
 *  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 *  Heimdall <heimdall@bifroest.demon.co.uk>  1998
 *  All rights reserved.  For full licence details see file licences.c
 */

#include "com_ravnaandtines_ctcjava_CTC.h"
#include "ctcjlib.h"

static JNIEnv * theEnv = 0;

extern "C" JNIEXPORT void JNICALL Java_com_ravnaandtines_ctcjava_CTC_registerJVM
(JNIEnv * env, jclass)
{
    theEnv = env;
}


extern "C" JNIEnv * getEnvironment(void)
{
    return theEnv;
}

extern "C"  void setEnvironment(JNIEnv * env)
{
    theEnv = env;
}
