/*  hashpass.h
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1996
**  All rights reserved.  For full licence details see file licences.c
** DLL support Mr. Tines 14-Sep-1997                  
**  Namespace CTClib Mr. Tines 5-5-98
*/
#ifndef _hashpass
#define _hashpass
#include "abstract.h"

#ifndef CTCALG_DLL
#define CTCALG_DLL
#endif

/*
** hashpass - Hash pass phrase down by using the given algorithm
** Adjust the result to fit the appropriate key
*/
#ifdef __cplusplus
NAMESPACE_CTCLIB
extern "C" {
#endif

    /* Obsolete, pre-OpenPGP passphrase hashing */
    void CTCALG_DLL hashpass (char *keystring, byte *key, int keylen, byte hashalg);

    /* OpenPGP passphrase hashing, as best as we can interpret the spec */
    /* Assumes that keystring is no more than 255 characters plus nul */
    void CTCALG_DLL hashpassEx (char *keystring, byte *key,
    int keylen, byte * s2k, boolean convertToUTF8);


#ifdef __cplusplus
}
END_NAMESPACE
#endif

#endif

/* end of file hashpass.h */
