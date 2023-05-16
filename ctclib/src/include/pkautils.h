/* pkautils.h
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
**  Heimdall <heimdall@bifroest.demon.co.uk>  1997
**  All rights reserved.  For full licence details see file licences.c
**  Namespace CTClib Mr. Tines 5-5-98
*/
#ifndef _pkautils
#define _pkautils

#ifndef CTCPKA_DLL
#define CTCPKA_DLL
#endif

#include <ctype.h>
#include "cipher.h" /* need full definition of cv_details */
#include "keyconst.h"

#ifdef __cplusplus
NAMESPACE_CTCLIB
extern "C" {
#endif

    typedef enum
        {
        KS_MISSING = 0, /* Only encountered as an Id. */
        KS_ON_FILE = 1, /* Bignums are in the public key-ring file */
        KS_COMPLETE = 2 /* Bignums are in memory */
    }
    KeyStat;

    typedef enum
        {
        SS_BAD = -2, /* Signature does not match (confirmed this run) */
        SS_SUSPECT = -1, /* Signature does not match (according to file)  */
        SS_UNKNOWN = 0, /* Signature has not been checked */
        SS_PROBABLE = 1, /* Signature matchs (according of file) */
        SS_VERIFIED = 2 /* Verified this program run */
    }
    SigStat;

#define MAXPUBNUMS 4 /* Maximum number of bignums in a public key (ElGamal) */
#define MAXSECNUMS 4 /* Maximum number of bignums in a secret key (RSA) */
#define MAXCYPHNUMS 2 /* Maximum number of bignums in a cyphertext */
#define MAXSIGNUMS 2 /* Maximum number of bignums in a signature */

    typedef enum 
        {
        PKE_PUB_SIZE, /* Number of MPNs in a public key */
        PKE_SEC_SIZE, /* Number of MPNs in a secret key */
        PKE_CIPH_SIZE, /* Number of MPNs in a cyphertext */
        PKE_SIG_SIZE, /* Number of MPNs in a signature */
        PKE_CHAR_NUM, /* Character MPNs i.e. one defining the key size */
        PKE_SIZE_ARRAY_SIZE
    }
    PKEsizeParam;

    typedef int PKEparams[PKE_SIZE_ARRAY_SIZE];

    /*typedef*/ 
    struct pubkey_T {
        pubkey * next_alias; /* NULL if not in hash table */
        hashtable * keyRing; 
        username * userids;
        signature * directSig; /* Signature(s) on this key not incorporating a userid. */
        pubkey * subkeys; /* Where the key is in a subkey it points to the next subkey */
        /* There are places in the code where it is assumed that 
                 ** there is not more than one directSig or subkey. */
        pubkey * superkey;
        byte keyId[KEYFRAGSIZE];
        uint16_t size;
        byte version; /* Some of these fields are merely preserved for signatures*/
        byte timestamp[SIZEOF_TIMESTAMP];
        byte validity[SIZEOF_VALIDITY];
        byte trust;
        byte depth; /* shortest cert. path to buckstop key */
        long fileOffset; /* 0 if not in public keyring file */
        KeyStat status;
        union /* This union is to allow for extension to other PKE algorithms. */
        {
            bignum nums[MAXPUBNUMS];
        }
        pkdata;
        byte pkalg; /* public key algorithm */
    }/*pubkey*/
    ;


    /*typedef*/ 
    struct username_T {
        username * next;
        signature * signatures;
        char * text; /* Normally NULL; only set if the key is NOT on disc */
        long fileOffset;
        byte trust;
        byte legit;
    }/*username*/
    ;

    /*typedef*/ 
    struct signature_T {
        signature * next; /* list of signatures on a userid */
        pubkey * from; /* key that made this signature */
        sigDetails* details; /* Normally NULL; only set if NOT on disc */
        SigStat sigStat;
        long fileOffset;
        byte trust;
    }/*signature*/
    ;


    typedef struct seckey_num_T
        {
        bignum plain;
        byte * cypher;
    }
    seckey_num;

    /*typedef*/ 
    struct seckey_T {
        pubkey * publicKey;
        seckey * next_in_file;
        union /* This union is to allow for extension to handle other
                      PKE algorithms */
        {
            seckey_num nums[MAXSECNUMS];
        }
        pkdata;
        uint16_t checksum;
        Skstat skstat; /* i.e. is in external format */
        byte iv[MAXBLOCKSIZE];
        byte cea_key[MAXKEYSIZE]; /* no need to allow for triple encrypt */
        cv_details kpalg; /* key protection algorithm */
        byte hashalg; /*passphrase hashing algorithm */
    }/*seckey*/
    ;

    int CTCPKA_DLL PKEsize(byte algor, PKEsizeParam purpose);
    void CTCPKA_DLL extractLSB(byte keyID[KEYFRAGSIZE], bignum n);
    boolean CTCPKA_DLL internalise_seckey(seckey * sec_key, const char * passphrase);
    void CTCPKA_DLL encrypt_seckey(seckey * sec_key);
    void CTCPKA_DLL externalise_seckey(seckey * sec_key);
    void CTCPKA_DLL fingerPrint(byte *hash, pubkey * pub_key);
    void CTCPKA_DLL fingerPrintPlus(byte *hash, pubkey * pub_key, byte algor);
    void CTCPKA_DLL fingerPrintOPGP(byte *hash, pubkey * pub_key, byte algor);

    /* PGP 5.0 fingerprints */
    boolean CTCPKA_DLL keyHashUpdate(pubkey * pub_key, md_context context);
    uint16_t CTCPKA_DLL formatKeySize(pubkey * pub_key);
    void CTCPKA_DLL formatPubkey(pubkey * pub_key, byte * buffer);
    /*
        ** Convert to or from external byte order.
        ** Note that convert_byteorder does nothing if the external byteorder
        **  is the same as the internal byteorder.
        */
#define CONVERT(x) (convert_byteorder((byte *)&(x), sizeof(x)))

#ifdef __cplusplus
}
END_NAMESPACE
#endif

#endif         
