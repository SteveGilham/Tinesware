/* keyhash.h - Key Hash table entry points
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
**  Heimdall <heimdall@bifroest.demon.co.uk>  1996
**  All rights reserved.  For full licence details see file licences.c
**
**  This include file may be included by non-library code and defines the
**  supported entry points to this library for key and key-ring manipulation
** 
** DLL support Mr. Tines 14-Sep-1997                  
**  Namespace CTClib Mr. Tines 5-5-98
**
*/
#ifndef _keyhash
#define _keyhash

#ifndef CTCKEY_DLL
#define CTCKEY_DLL
#endif

#include "abstract.h"
#include "keyconst.h"

#ifdef __cplusplus
NAMESPACE_CTCLIB
extern "C" {
#endif

    typedef enum
        {
        KEY_NO_NUMBS = 1, /* Key data (key itself) not available */
        KEY_DEADBEEF = 2, /* Two different keys with the same Id! */
        KEY_BAD_STATUS = 3, /* corrupt secret key status value */
        KEY_RING_FAILURE = 4, /* failure to read from public key-ring */
        KEY_READ_PUBKEY = 5, /* Just read a complete public key, add to ring? */
        KEY_READ_SECKEY = 6, /* Just read a complete secret key, add to ring? */
        KEY_PUBKEY_FOUND = 7, /* Public key block (not in a key-ring) */
        KEY_SECKEY_FOUND = 8, /* Secret key block (not in a key-ring) */
        KEY_USERID_FOUND = 9, /* User-id block (not in a key-ring) */
        KEY_KEYSIG_FOUND = 10, /* (good) Key Signature block (not in a key-ring) */
        KEY_NO_USERID = 11, /* (non-revocation) signature without userid */
        KEY_REVOKED = 12, /* Key revocation certificate found and checked */
        KEY_UNCK_REVOKE = 13, /* Failed to checked revocation certificate */
        KEY_BAD_REVOKE = 14, /* Revocation certificate is a bad signature */
        KEY_WRONG_REVOKE = 15, /* Revocation certificate signed with wrong key */
        KEY_BADSIG_FOUND = 16, /* Bad key Signature block found */
        KEY_NOKEY_SIG_FND = 17, /* Key signature by unknown key found */
        KEY_RECOVERY_KEY = 18
    }
    key_man_conds;

    typedef enum
        {
        SIG_OKAY = 0,
        SIG_NO_KEY, /* No public key to check signature with */
        SIG_ERROR, /* checking process failed (e.g. no memory) */
        SIG_BAD /* The signature is WRONG */
    }
    sigValid;

    /*typedef*/ struct keyringContext_T
        {
        pubkey * last_mainkey; /* last pub-key (excluding sub-keys) found */
        pubkey * last_key; /* last pub-key found (may be sub-key) */
        seckey * last_seckey;
        username * last_name;
        byte * last_trust;
        hashtable * keyRings;
    }/*keyringContext*/
    ;

    typedef enum
        {
        SIG_STATUS = 1001,
        SIG_TRUST,
        SIG_VERSION,
        SIG_CLASS,
        SIG_PKALG,
        SIG_MDALG,
        SIG_DATETIME,
        SIG_KEYID
    }
    signature_param; /* Any SUBPKT values may also be supplied */


    /*typedef*/
    struct recordSummary_T
        {
        long position; /* position of packet header; byte offset */
        long start; /* position of contents; byte offset */
        long length; /* length of packet excluding header */
        long next; /* file offset of the packet following */
        uint16_t size; /* Key size (if a PKE key certificate) */
        byte type;
        byte version;
        byte trust;
        byte sigClass; /* (valid only for SKE packets) */
        byte itemID[KEYFRAGSIZE];
        boolean multipart; /* this is an OpenPGP indefinite length packet */
            /* for such packets, next does what it says, but length is the 
               first section length only. */
    }/*recordSummary*/
    ;



    /*typedef*/
    struct keyType_T
        {
        byte algorithm;
        uint16_t keyLength;
        prime_method method;
        uint16_t publicExponent;
        char name[256];
        char passphrase[256];
        byte selfSignAlg; /* message digest algorithm for selfsignature : 0=don't */
        byte kpAlg;
    }/*keyType*/
    ;


    /* Generate a new PKE key */
    seckey * CTCKEY_DLL makePKEkey(hashtable * table, keyType * keyType);

    boolean CTCKEY_DLL writePubKey(DataFileP file, pubkey * pub_key,
            boolean overwriting, boolean writeTrust, hashtable * keyringDefault);
    boolean CTCKEY_DLL writeSecKey(DataFileP file, seckey * sec_key);

    /* writePubRing overwrites the open keyRing, if file is NULL. */
    boolean CTCKEY_DLL writePubRing(DataFileP file, hashtable * keyRings);
    boolean CTCKEY_DLL writeSecRing(DataFileP file, hashtable * keyRings);

    /* Return printable public key fragment. */
#define IDPRINTSIZE (2 * KEYPRINTFRAGSIZE + 3)
    void CTCKEY_DLL formatKeyID(char text[IDPRINTSIZE], byte keyID[KEYFRAGSIZE]);

    /* Public Key enquiry functions */
    uint16_t CTCKEY_DLL keyLength(pubkey * pub_key);
    uint32_t CTCKEY_DLL keyDate(pubkey * pub_key);
    boolean CTCKEY_DLL keySecret(pubkey * pub_key);
    void CTCKEY_DLL extract_keyID(byte keyID[KEYFRAGSIZE], pubkey * pub_key);/* fragment from modulus */
    byte CTCKEY_DLL ownTrust(pubkey * pub_key);
    void CTCKEY_DLL name_from_key(pubkey * key, char name[256]); /* first UserId text*/
    byte CTCKEY_DLL getPubkeyAlg(pubkey * pub_key);
    byte CTCKEY_DLL getPubkeyVersion(pubkey * pub_key);
    /* Key subordinate records (userId and signature) enquiries */
    void CTCKEY_DLL text_from_name(hashtable * keyRings, username * nameRec, char name[256]); 
    pubkey * CTCKEY_DLL signatory(signature * sig);
    /*uint32_t CTCKEY_DLL creationDate(pubkey * pub_key);*/
    uint16_t CTCKEY_DLL validity(pubkey * pub_key);

    /* Key location functions */
    pubkey * CTCKEY_DLL key_from_keyID(hashtable * keyRings, byte * keyID);
    seckey * CTCKEY_DLL seckey_from_keyID(hashtable * keyRings, byte * keyID);
    pubkey * CTCKEY_DLL publicKey(seckey * sec_key);
    seckey * CTCKEY_DLL secretKey(pubkey * pub_key);
    pubkey * CTCKEY_DLL subKey(pubkey * pub_key);

    /* Key-ring exhaustive scan operations */
    pubkey * CTCKEY_DLL firstPubKey(hashtable * keyRings);
    pubkey * CTCKEY_DLL nextPubKey(pubkey * last);
    signature * CTCKEY_DLL revocation(pubkey * pub_key);
    username * CTCKEY_DLL firstName(pubkey * pub_key);
    username * CTCKEY_DLL nextName(username * name);
    signature * CTCKEY_DLL firstSig(username * name);
    signature * CTCKEY_DLL nextSig(signature * sig);
    seckey * CTCKEY_DLL firstSecKey(hashtable * keyRings);
    seckey * CTCKEY_DLL nextSecKey(seckey * last);

    /* Subordinate record manipulations */
    username * CTCKEY_DLL addUsername(pubkey * key, char * text);
    void CTCKEY_DLL removeUsername(pubkey * key, username * userid);
    /* For a revocation signature userid should be NULL, and
                       algorithm MDA_MD5 - *everyone* needs to be able to read them -
                       at least if the key doing it is RSA */
    signature * CTCKEY_DLL addSignature(pubkey * key, username * userid, byte sigType,
    seckey * signing, byte algorithm);
    void CTCKEY_DLL removeSignature(pubkey * key, username * userid, signature * sig);
    uint32_t CTCKEY_DLL getSignatureValue(signature * sig, int valueKey, hashtable * keyRings);
    size_t CTCKEY_DLL getSignatureArray(signature * sig, int valueKey, byte * value, size_t maxSize, hashtable * keyRings);
    boolean CTCKEY_DLL revoke(seckey * sec_key);
    void CTCKEY_DLL unrevoke(pubkey * pub_key);

    /* Control of secret key encryption */

    boolean CTCKEY_DLL set_passphrase(seckey * sec_key, char * passphrase);
    Skstat CTCKEY_DLL keyStatus(seckey * sec_key);

    boolean CTCKEY_DLL sameKey(pubkey * left, pubkey * right); /* Both keys must be 'complete' before calling */
    boolean CTCKEY_DLL completeKey(pubkey * pub_key);
    void CTCKEY_DLL incompleteKey(pubkey * pub_key);
    sigValid CTCKEY_DLL checkSignature(pubkey * pub_key, username * name, signature * sig);

    /* Key-ring file access */
    hashtable * CTCKEY_DLL init_userhash(DataFileP);
    boolean CTCKEY_DLL read_secring(DataFileP secring, hashtable * pubring);
    continue_action CTCKEY_DLL keyringPacket(DataFileP input, recordSummary * summary, keyringContext * context);
    void CTCKEY_DLL destroy_userhash(hashtable * keyRings);

    /* Changed functions indicate if there have been modifications since the last read/save */
    boolean CTCKEY_DLL publicChanged(hashtable * table);
    boolean CTCKEY_DLL secretChanged(hashtable * table);

    /* Keyring manipulation */
    void CTCKEY_DLL setTrust(pubkey * pub_key, byte trust);
    boolean CTCKEY_DLL insertPubkey(hashtable * keyRings, pubkey * * pub_key);
    void CTCKEY_DLL removePubkey(hashtable * keyRings, pubkey * pub_key);
    boolean CTCKEY_DLL insertSeckey(hashtable * keyRings, seckey * sec_key);
    void CTCKEY_DLL removeSeckey(hashtable * keyRings, seckey * sec_key);
    void CTCKEY_DLL free_pubkey(pubkey * * pub);
    void CTCKEY_DLL free_seckey(seckey * * sec);

    /* Security check:  This routine is a type of mutual crack.  It looks for common
        ** factors in the moduli of all the keys */
    int CTCKEY_DLL mutualFactor(pubkey * left, pubkey * right, char commonFactor[1000]);
    /* Return: 0 if mutually prime, 1 if a commonFactor, -1 if an error */


#ifdef __cplusplus
}
END_NAMESPACE
#endif

#endif

/* end of file keyhash.h */
