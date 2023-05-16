/* rawrand.c
 **
 ** N.B.  THIS IS A DUMMY IMPLEMENTATION AND MUST _NOT_ BE USED
 **         LIVE.  (There is a #error to help prevent this being done by accident.)
 **
 **  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 **  Heimdall <heimdall@bifroest.demon.co.uk>  1996
 **  All rights reserved.  For full licence details see file licences.c
 **
 ***********************************************/
#include "rawrand.h"
#include <string.h>

/* If you are only testing you may comment out the next line;
 ** however note that this version is WHOLLY suitable for live use*/
//#error "You are using a dummy RAWRAND implementation"

/* AS YET UNIMPLEMENTED ROUTINES */
/* so silence the complaints about parameters not used */

#include <stdio.h>
#define BLEAT fprintf(stderr, "\007\007\007You should not be using DUMMY RAWRAND!\007\007\n");

#ifdef __BORLANDC__
#pragma warn -par
#endif

static byte buffer[1024];
static int bufflen = 0;

void getRawRandom(unsigned char * data, int length) {
    BLEAT return;
}
boolean ensureRawRandom(int bytes) {
    BLEAT return TRUE;
}/* Not true */
void getSessionData(unsigned char * data, int *length)
{
    /* For test purposes; to make tests reproducable */
    /* this is currently returning constant data */
    /*  byte fixed_data[8] = { 1, 2, 3, 4, 5, 6, 7, 8};
     * 
     *length = min(*length, 8);
     *       memcpy(data, fixed_data, *length);
     */
    *length = min(*length, bufflen);
    memcpy(data, buffer, *length);
    BLEAT
        return;
}

void setSessionData(unsigned char * data, int *length)
{
    bufflen = *length;
    memcpy(buffer, data, bufflen);
}

/* end of file rawrand. c */
