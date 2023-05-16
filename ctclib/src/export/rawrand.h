/* rawrand.h
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
**  Heimdall <heimdall@bifroest.demon.co.uk>  1996
**  All rights reserved.  For full licence details see file licences.c
**
** Plug compatible random number generator for PGP separating 
** true entropy collection for hashing this.  This is the definition
** file for the (machine dependent) raw random data collector.
** DLL support Mr. Tines 14-Sep-1997                  
**  Namespace CTClib Mr. Tines 5-5-98
*/
#ifndef _rawrandom
#define _rawrandom
#include "basic.h"
#ifdef __cplusplus
NAMESPACE_CTCLIB
extern "C" {
#endif

#ifndef CTCRAN_DLL
#define CTCRAN_DLL
#endif

    void CTCRAN_DLL getRawRandom(unsigned char * data, int length); /* get N bytes of data */

    /* ensureRawRandom Returns true if (and when) the number of bytes requested  */
    /* is available.  Returns false if the user is not prepared to wait, or the  */
    /* data is otherwise not forthcoming.  */
    boolean CTCRAN_DLL ensureRawRandom(int bytes); 

    /* getSessionData should return as much session specific data as is available */
    /* or the original value of *length whichever is shorter.  */
    void CTCRAN_DLL getSessionData(unsigned char * data, int * length);

    /* setSessionData is provided only for the purposes of the demonstration,
           minimal function version of this module; it sets a buffer which
           is returned by getSessionData() */
    void CTCRAN_DLL setSessionData(unsigned char * data, int *length);

#ifdef __cplusplus
}
END_NAMESPACE
#endif
#endif
