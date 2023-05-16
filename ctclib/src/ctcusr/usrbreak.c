/* usrbreak.c - default version (ineffective).
 **
 **  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 **  Heimdall <heimdall@bifroest.demon.co.uk>  1996
 **  All rights reserved.  For full licence details see file licences.c
 */
#include <stdlib.h>
#include <stdio.h>
#include "basic.h"
#include "usrbreak.h"


int user_break(void)/* returns true if the user has requested operation abort */
{
    return FALSE; 
}

void bug_check(char * text)
{
    printf("%s\n",text);
    exit(1);
}

/* end of file usrbreak.c */

