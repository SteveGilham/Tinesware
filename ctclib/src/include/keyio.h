/* keyio.h
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
**  Heimdall <heimdall@bifroest.demon.co.uk>  1996
**  All rights reserved.  For full licence details see file licences.c
*/
#ifndef _keyio
#define _keyio

#include "abstract.h"
#include "keyconst.h"
#include "pkcipher.h"

#ifndef CTCKEY_DLL
#define CTCKEY_DLL
#endif


/*typedef*/
/*
#define MAXECBLOCKS 1
#define MAXCYPHERMPIS 2*MAXECBLOCKS
struct keyDetails_T
{
 byte version;
 byte pk_algor;
 byte keyId[KEYFRAGSIZE];
 bignum cypherKey[MAXCYPHERMPIS];
}*/ /*keyDetails*/ /*;*/

typedef enum
{
    KIO_OKAY = 0,
    KIO_BAD_VERSION = 1,
    KIO_BAD_ALGOR = 2,
    KIO_FILE_ERROR = 3,
    KIO_BAD_LENGTH = 4,
    KIO_WRONG_REC_TYPE = 5,
    KIO_NOT_CTB = 6,
    KIO_NO_POINTER = 7, /* readkeypacket trying to read secret key into a null pointer */
    KIO_NO_MEMORY = 8,
    KIO_EOF = 9
}
keyio_error;

keyio_error readusername(DataFileP file, long position, char username[256]);
keyio_error readkeypacket(DataFileP file, byte *ctb, pubkey * pub_key, seckey * sec_key);
keyio_error CTCKEY_DLL readsummary(DataFileP file, recordSummary * summary);
keyio_error writekeypacket(DataFileP file, pubkey * pub_key, seckey * sec_key);
keyio_error writeuserpacket(DataFileP file, char userid[256]);
keyio_error write_trust(DataFileP file, byte trustbyte);

boolean CTCKEY_DLL writepacketheader(DataFileP file, byte type, long length);
keyio_error readPKEpacket(DataFileP file, long offset, bignump cypher,
byte *pkalg);
keyio_error writePKEpacket(DataFileP file, keyDetails * details);
/* N.B. readSKEpacket initialises the sig->signature bignum so this should _not_
**      already contain an initialised number or there will be a storage leak */
keyio_error CTCKEY_DLL readSKEpacket(DataFileP file, long offset, sigDetails * sig);
keyio_error CTCKEY_DLL writeSKEpacket(DataFileP file, sigDetails * sig);


boolean CTCKEY_DLL getConvKey(DataFileP file, long offset, seckey * sec_key,
cv_details * algor, size_t *keylen, byte * * convKey);
boolean CTCKEY_DLL putConvKey(DataFileP file, byte version,
pubkey * pub_key, cv_details * algor, size_t keylen, byte *convKey);
pubkey * checkSubkeys(hashtable * keyRings, pubkey * subkey, pubkey * mainkey);


#endif

/* end of file keyio.h */
