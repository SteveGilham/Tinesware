/* pkcipher.h
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> March 1997
**  All rights reserved.  For full licence details see file licences.c
**
**
**  common utilities for public key encryption algorithms.
**  This one is not as tidily separated as cipher.* and hash.* due
**  to the depth to which RSA was embedded in the PGP and early CTC design
**  The need to understand that there are such things as keyring files
**  also complicates matters.  We thus have some bleeding into keyio.c and
**  keyutils.c
** DLL support Mr. Tines 14-Sep-1997
**  Namespace CTClib Mr. Tines 5-5-98
*/
#ifndef _pkcipher
#define _pkcipher
#include "abstract.h"
#include "pkautils.h"

#ifndef CTCPKA_DLL
#define CTCPKA_DLL
#endif

NAMESPACE_CTCLIB

typedef enum
{
    PKE_NO_MEMORY = 1,
    PKE_USER_BREAK = 2,
    PKE_FILE_ERROR = 3,
    PKE_BAD_CHECKSUM = 4,
    PKE_UNKN_VERSION = 5,
    PKE_UNIMP_BULK_CYPHER = 6,
    PKE_UNIMP_MODE_OPER = 7,
    PKE_BAD_SIG_FORMAT = 8,
    PKE_KEY_TOO_SHORT = 9,
    PKE_BAD_RETURN_CODE = 10,
    PKE_KEYGEN_SETUP = 11,
    PKE_KEYGEN_1STPRIME = 12,
    PKE_KEYGEN_2NDPRIME = 13,
    PKE_KEYGEN_FINISHING= 14,
    PKE_UNIMP_PKE_CYPHER = 15
}
pke_errors;


#define MAXECBLOCKS 1
#define MAXCYPHERMPIS 2*MAXECBLOCKS
struct keyDetails_T
{
    byte version;
    byte pk_algor;
    byte keyId[KEYFRAGSIZE];
    bignum cypherKey[MAXCYPHERMPIS];
}/*keyDetails*/
;

#define MAXDIGESTEXTRAS 100 /* a reasonable maximum */
#define V2DIGESTEXTRAS (SIZEOF_TIMESTAMP + 1)
/*typedef*/
struct sigDetails_T
{
    byte version; /* = 0 implies invalid or missing
                 ** = 2 PGP 2.5 and earlier
                 ** = 3 PGP 2.6 to ??? 
                 ** = 4 PGP 3 or later */
    byte sigClass; /* signature class; see SIG_* in keyconst.h */
    byte pk_algor; /* public key authentication algorithm */
    byte md_algor; /* Message digest algorithm  */
    uint32_t timestamp;
    uint16_t lenDigestBytes;
    byte digestBytes[MAXDIGESTEXTRAS]; /* packed class and timestamp fields */
    byte checkBytes[2];
    byte keyId[KEYFRAGSIZE];
    pubkey * pub_key;
    union
        {
        bignum nums[MAXSIGNUMS];
    }
    signature;
    byte digest[MAXHASHSIZE];
}/*sigDetails*/
;



boolean CTCPKA_DLL valid_PKE_algor(byte algor);
boolean CTCPKA_DLL recognised_PKE_algor(byte algor);

void CTCPKA_DLL release_pubkey(pubkey * pub_key); /* free associated storage */
void CTCPKA_DLL release_signature (sigDetails *sig);
void CTCPKA_DLL release_seckey(seckey * sec_key);

void CTCPKA_DLL prepare_pubkey(pubkey * pub_key);
void CTCPKA_DLL prepare_signature(sigDetails * sig);
void CTCPKA_DLL prepare_seckey(seckey * sec_key);

void CTCPKA_DLL assign_pubkey(pubkey * to, pubkey * from);
void CTCPKA_DLL assign_seckey(seckey * to, seckey * from);

boolean CTCPKA_DLL read_mpn_pubkey(pubkey * pub_key, DataFileP file);
boolean CTCPKA_DLL read_mpn_summary(byte alg, recordSummary *summary, DataFileP file);
boolean CTCPKA_DLL read_mpn_signature(sigDetails *sig, DataFileP file);
boolean CTCPKA_DLL read_mpn_seckey(seckey * sec_key, DataFileP file);

uint16_t CTCPKA_DLL length_pubkey(pubkey * pub_key); /* how many bytes of buffer needed */
uint16_t CTCPKA_DLL length_signature(sigDetails *sig);
uint16_t CTCPKA_DLL length_seckey(seckey * sec_key);

void CTCPKA_DLL put_pubkey(byte * buffer, pubkey * pub_key);
boolean CTCPKA_DLL write_signature(sigDetails * sig, DataFileP file);
boolean CTCPKA_DLL write_seckey(seckey * sec_key, DataFileP file);

boolean CTCPKA_DLL equate_pubkey(pubkey * right, pubkey * left);
void CTCPKA_DLL move_pubkey(pubkey * to, pubkey * from);

void CTCPKA_DLL extractKeyfrag(pubkey * pub_key);
void CTCPKA_DLL sizePubkey(pubkey * pub_key); /* set key significance in bits */

boolean CTCPKA_DLL read_CKcypher(bignump cypher, DataFileP file, byte algor);
uint16_t CTCPKA_DLL length_CKcypher(keyDetails *details);
boolean CTCPKA_DLL write_CKcypher(keyDetails *details, DataFileP file);

boolean CTCPKA_DLL newPKAkey(keyType * keyType, seckey * sec_key, pubkey * pub_key);
boolean CTCPKA_DLL verifySecKey(seckey * sec_key);

/*common_error rsa_public_operation(bignump outbuf, bignump inbuf, pubkey * pub_key);*/
/*common_error rsa_private_operation(bignump outbuf, bignump inbuf, seckey * sec_key);*/
boolean CTCPKA_DLL unpackConvKey(bignum* cyphertextkey, byte pk_algor,
seckey * sec_key, cv_details * algor,
size_t *keylen, byte * * convKey);
common_error CTCPKA_DLL packConvKey(keyDetails *details, byte version,
byte algorithm, pubkey * pub_key,
cv_details * algor, size_t keylen,
byte *convKey);

/* N.B.  */
boolean CTCPKA_DLL getSignature(sigDetails * sig);
boolean CTCPKA_DLL verifySignature(sigDetails * sig, byte * digest);
boolean CTCPKA_DLL putSignature(sigDetails * sig, seckey * sec_key);

END_NAMESPACE


#endif
/* end of file pkcipher.h */
