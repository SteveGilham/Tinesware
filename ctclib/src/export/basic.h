/* basic.h
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
**  Heimdall <heimdall@bifroest.demon.co.uk>  1996
**  All rights reserved.  For full licence details see file licences.c
**
**  Common data type and macro definitions.
**
**  This source is derived from the PGP2.3 source file usuals.h:-
**          ASCII/binary encoding/decoding based partly on PEM RFC1113.
**          PGP: Pretty Good(tm) Privacy - public key cryptography for the masses.
**          (c) Copyright 1990-1992 by Philip Zimmermann.  All rights reserved.
**
*/
#ifndef _basic 
#define _basic
#include <stdlib.h>

#ifndef VERSIONNO
#define CTCLIB_PRODUCT_VERSION "2.3"
#define VERSIONNO "CTC"CTCLIB_PRODUCT_VERSION" "
#endif
typedef unsigned char boolean; 
typedef unsigned char byte;
/*typedef unsigned short ushort;
typedef unsigned long ulong;*/

/*
 Follow proposed C++ standard and provide names for
 (unsigned) integer types of specific bit length, to
 replace all the WORD32 or UINT4 or similar things
 elsewhere in the crypto code.
 - Mr. Tines 8-Jan-97
*/
typedef unsigned char uint8_t;
typedef unsigned short uint16_t;
#ifndef __alpha
typedef unsigned long uint32_t;
#else
typedef unsigned int uint32_t;
#endif


#ifndef TRUE
#define FALSE 0
#define TRUE  1
#endif

#ifndef min 
#define min(a,b) (((a)<(b)) ? (a) : (b) )
#define max(a,b) (((a)>(b)) ? (a) : (b) )
#endif 

#ifdef VOID
#undef VOID
#endif

#if defined(__STDC__) || defined(__GNUC__) || defined(MSDOS) || defined(ATARI)
#define VOID void
#else
#define VOID char
#endif

/* Centralise some of the machine/compiler dependency where it makes sense to */

/* Namespaces */
#ifdef CTC_NAMESPACE_SUPPORT
#undef CTC_NAMESPACE_SUPPORT
#endif

#ifdef __cplusplus
/*        BC++5.0 */
#if (defined(__BORLANDC__) && __BORLANDC__ >= 0x500)
#define CTC_NAMESPACE_SUPPORT

/* MSVC++ 5.0 */
#elif (defined(_MSC_VER) && _MSC_VER >= 1100)
#define CTC_NAMESPACE_SUPPORT

/* some version of g++ */
#elif defined(__GNUC__)
#define CTC_NAMESPACE_SUPPORT
#endif

#endif

#ifdef CTC_NAMESPACE_SUPPORT
#define NAMESPACE_CTCLIB namespace CTClib {
#define END_NAMESPACE }
#else
#define NAMESPACE_CTCLIB 
#define END_NAMESPACE
#endif


typedef enum
{
    CE_OKAY = 0,
    CE_USER_BREAK = 1,
    CE_NO_MEMORY = 2,
    CE_OTHER = 3
}
common_error;
#endif
/* end of file basic.h */

