/* abstract.h
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
**  Heimdall <heimdall@bifroest.demon.co.uk>  1996
**  All rights reserved.  For full licence details see file licences.c
**
** Abstract type definitions file.
**
**  This includes various abstract type definitions 
**  adequate for prototype definitions and manipulation
**  of pointers to the types, but providing no details of
**  the internals of the structures.
**
**  Many of these structures are used only quite locally.
**  However as we are leaking no information about their
**  internals there is no danger in making their names widely
**  available.  Indeed it has the effect of globally reserving
**  the name, preventing accidently clashs.
*/
#ifndef _abstract
#define _abstract
#include "basic.h"

/* Borland C++ 4.52 refuses to take the suppression of incomplete
 definition warnings from the IDE, and warns even if the type is
 not used.  So suppress it via pragma in the file that causes this
 At the same time we can get rid of the PORT_IO_PRIVATE dodge which
 had the same goals - Mr. Tines 1-Jan-1997
**  Namespace CTClib Mr. Tines 5-5-98
   */
#ifdef __BORLANDC__
#pragma warn -stu
#endif

NAMESPACE_CTCLIB

/* ASCII armour structures. 
** see armour.h for full definitions */
typedef struct armour_params_T armour_params;
typedef struct armour_info_T armour_info;

/* Definition of bignum types for bignum.c 
**  multiple precision integer module 
**  See bignum.h for full definitions */
typedef struct mp_number_T mp_number;
typedef mp_number * bignum;
typedef bignum * bignump;

/* Portable IO file handles
** See port_io.h for full definitions */
typedef struct DataFile_T DataFile;
typedef DataFile * DataFileP;
#ifdef READ
#undef READ
#endif
#ifdef WRITE
#undef WRITE
#endif
typedef enum
{ 
    READ, /* Read only */
    READWIPE, /* Read then wipe */
    WRITE, /* Write only */
    UPDATE, /* Read/write (may mix) */
    UPDATEWIPE, /* Read/write then wipe on close */
    WRITEREAD, /* Write then read */
    WRITEREADWIPE/* Write then read then wipe*/
}
accessMode;

typedef enum 
{ 
    TEXTCYPHER, /* Text (armoured) cyphertext */
    BINCYPHER, /* Binary cyphertext */
    TEXTPLAIN, /* Text plaintext */
    BINPLAIN, /* Binary plaintext */
    PUBLICRING, /* Public keyring  */
    SECRETRING /* Secret keyring  */
}
fileType;

typedef enum
{
    SIMPLE_SCAN = 1,
    JUMP_SCAN = 2,
    SOPHIE_GERMAIN = 3
}
prime_method;

/* Top level communications structures
** see ctc.h for full definitions */
typedef struct decode_context_T decode_context;
typedef struct encryptInsts_T encryptInsts;

/* PKE key structure types
** See keyutils.h for full definitions */
typedef struct keyType_T keyType;
typedef struct pubkey_T pubkey; 
typedef struct seckey_T seckey;
typedef struct username_T username;
typedef struct signature_T signature;

/* File (CTB) block handling 
** See keyio.h for full definitions */
typedef struct recordSummary_T recordSummary;
typedef struct keyDetails_T keyDetails; 
typedef struct sigDetails_T sigDetails; 

/* Public key-ring types
** See keyhash.c for full definitions */
typedef struct hashtable_T hashtable;
typedef struct keyringContext_T keyringContext;

/* Number of bytes in Key Id. */
#define KEYFRAGSIZE 8
#define KEYPRINTFRAGSIZE 4
#define SIZEOF_TIMESTAMP 4 /* 32-bit timestamp */
#define SIZEOF_VALIDITY  2  /* Size of validity period */
#define MAX_DEPTH 8  /* max. value of max_cert_depth */

/* Message Digest structure 
** See hash.h/.c for full definitions */
#define MAXHASHSIZE  32  /* largest digest size in use (bytes) */
typedef void * md_context; /* context structure is private */

/* Bulk (symmetric) cypher types
** See cipher.h for full definitions */
#define MAXCVALGS   5  /* quintuple encryption is probably enough */
#define MAXBLOCKSIZE  16 /* largest block size in use (bytes) */
#define MAXKEYSIZE     32 /* largest key size in use (bytes) */
typedef struct cv_details_T cv_details;
typedef void * cv_keysched; 

/* callback functions return code */
typedef enum { 
    CB_CONTINUE = 1, CB_SKIP = 2, CB_ABORT = 4}
continue_action;
END_NAMESPACE

#endif
/* end of file abstract.h */

