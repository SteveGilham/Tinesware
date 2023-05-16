/* random.c - C source code for random number generation - 2 Mar 96
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
**  Heimdall <heimdall@bifroest.demon.co.uk>  1996
**  All rights reserved.  For full licence details see file licences.c
**
** Plug compatible random number generator for PGP separating 
** true entropy collection from hashing it.  
** DLL support Mr. Tines 14-Sep-1997                  
**  Namespace CTClib Mr. Tines 5-5-98
**
***********************************************/

#ifndef _random
#define _random
#include "basic.h"

#ifndef CTCALG_DLL
#define CTCALG_DLL
#endif

/*  PSEUDORANDOM not implemented */

#ifdef __cplusplus
NAMESPACE_CTCLIB
extern "C" {
#endif

    short CTCALG_DLL try_randombyte(void); /* returns truly random byte, or -1 */
    byte CTCALG_DLL randombyte(void); /* returns truly random byte from pool if available or
                                ** a pseudo-random byte if not. Changed return from
                                **  short to byte -- Tines 24-Mar-1997 */

    short CTCALG_DLL randload(short bitcount);
    /* Get fresh load of raw random bits into recyclepool for key generation */
    /* This routine returns -1 if it fails but current the application ignores this. */

    void CTCALG_DLL randflush(void); /* flush recycled random bytes */

#ifdef __cplusplus
}
END_NAMESPACE
#endif


#endif

/* end of file random.h */
