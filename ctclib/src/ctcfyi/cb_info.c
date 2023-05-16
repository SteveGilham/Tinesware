/***************************************************************************
                          callback.c  -  description
                             -------------------
    copyright            : (C) 1996 by Mr. Tines & Heimdall
    email                : tines@ravnaandtines.com
                           heimdall@bifroest.demon.co.uk
 ***************************************************************************/

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
/* cbinfo.c
**
** Contains stub code for informationalcallback use
*/
#include "callback.h"
#include <string.h>
#include <stdio.h>

#define BUFFERSIZE 1024


char *cbmodule(int i)
{
    switch(i)
    {
    case 1:
        return "CB_CTC_ENGINE";
    case 2:
        return "CB_ARMOUR";
    case 3:
        return "CB_COMPAND";
    case 4:
        return "CB_MSG_DIGEST";
    case 5:
        return "CB_BULK_CYPHER";
    case 6:
        return "CB_PKE";
    case 7:
        return "CB_PK_MANAGE";
    case 8:
        return "CB_CTB_IO";
    case 9:
        return "CB_RANDOM";
    case 10:
        return "CB_FILING";
    default:
        return "Unknown module";
    }
}

char *cbseverity(int i)
{
    switch(i)
    {
    case 1:
        return "CB_CRASH   ";
    case 2:
        return "CB_FATAL   ";
    case 3:
        return "CB_ERROR   ";
    case 4:
        return "CB_WARNING ";
    case 5:
        return "CB_INFO    ";
    case 6:
        return "CB_STATUS  ";
    default:
        return "Unknown status";
    }
}

/* These should both also expand code and context */
continue_action cb_exception(cb_condition * condition)
{
    printf("status %s :", cbseverity(condition->severity));
    printf("exception %s/%d/%d \"%s\"\n",
    cbmodule(condition->module),
    condition->code, condition->context, condition->text);
    fflush(stdout);
    return CB_CONTINUE;
}
void cb_information(cb_condition * condition)
{
    if(condition->severity > 3) return;
    printf("status %s :", cbseverity(condition->severity));
    if(condition->text)
        printf("information %s/%d/%d \"%s\"\n",
        cbmodule(condition->module),
        condition->code, condition->context, condition->text);
    else
        printf("information %s/%d/%d \"%s\"\n",
    cbmodule(condition->module), condition->code, condition->context,
    "cb_information() called with no text");
    fflush(stdout);
}

/* end of file cbinfo.c */




