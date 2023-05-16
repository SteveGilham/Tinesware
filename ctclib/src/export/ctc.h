/* ctc.h
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
**  Heimdall <heimdall@bifroest.demon.co.uk>  1996
**  All rights reserved.  For full licence details see file licences.c
**
** 
** DLL support Mr. Tines 14-Sep-1997                  
**  Namespace CTClib Mr. Tines 5-5-98
**
*/
#ifndef _ctc_h
#define _ctc_h
#include "keyhash.h" /* Need full definition of recordSummary */
#include "callback.h" /* Need full definition of cb_context */
#include "cipher.h"  /* Need full definitions of cv_details */

#ifndef CTCLIB_DLL
#define CTCLIB_DLL
#endif

#ifdef __cplusplus
NAMESPACE_CTCLIB
extern "C" {
#endif

    typedef enum
        {
        /* The following values are never returned but are using by CTC for
         ** callback conditions */
        /* First status codes (just telling the user what we are doing) */
        CTC_SEARCHING = 1, /* Looking for an armoured block */
        CTC_DEARMOURING = 2, /* Unarmouring a block */
        CTC_ARMOURING = 3, /* Armouring a block   */
        CTC_COMPRESSING = 4, /* Compressing data */
        CTC_DECOMPRESSING = 5, /* Decompressing data */
        CTC_DIGESTING = 6, /* Calculating digest  */
        CTC_BULK_ENCRYPTING = 7, /* Encrypting with symmetric cypher */
        CTC_BULK_DECRYPTING = 8, /* Decrypting with symmetric cypher */
        CTC_PKE_ENCRYPTING = 9, /* Encrypting with public key */
        CTC_PKE_DECRYPTING = 10, /* Decrypting with secret key */
        CTC_PKE_SIGNING = 11, /* Signing with secret key */
        CTC_PKE_VERIFY = 12, /* Verifying signature with public key */
        CTC_NOTHING_FOUND = 13, /* File contained no armoured blocked */
        CTC_UNIMP_MSG_DIGEST= 14, /* Unimplemented Message Digest algorithm */
        CTC_UNIMP_COMPRESS = 15, /* Unimplemented Compression algorithm */
        CTC_UNIMP_DECOMPRESS= 16, /* Unimplemented Decompression algorithm */
        CTC_UNIMP_BULK_CYPH = 17, /* Unimplemented Bulk Cypher */
        CTC_UNIMP_PKE_CYPHER= 18, /* Unimplemented PKE algorithm */
        CTC_NO_TEMP_FILE = 19, /* Unable to create temporary file */
        CTC_UNIMP_MULTI_PASS= 20, /* Multiple data passes for symmetric encryption not implemented */
        CTC_UNIMP_REV_PASS = 21, /* Reverse data passes for sym. cypher */
        CTC_WRONG_BULK_KEY = 22, /* Attempt to decrypt with wrong symmetric key */
        CTC_READ_FILE_ERR = 23,
        CTC_WRITE_FILE_ERR = 24,
        CTC_SECKEY_UNAVAIL = 25, /* Incoming is message is encrypted with this key. */
        CTC_PUBKEY_UNAVAIL = 26,
        CTC_UNKNWN_CTB = 27, /* unknown cypher (block) type byte */
        CTC_UNIMPL_CTB = 28,
        CTC_BAD_PLAIN_SIG = 29,
        CTC_GOOD_PLAIN_SIG = 30,
        CTC_OUT_OF_MEMORY = 31,
        CTC_COMMENT_FOUND = 32,
        CTC_NO_KEY_AVAIL = 33,
        CTC_BAD_VERSION = 34, /* Unrecognised packet version */
        CTC_NOT_SIGNATURE = 35 /* Non-signature packet found trying to verify signature */
    }
    ctc_codes;

    typedef enum {
        DECODE = 0,
        REVEALKEY = 1,
        USEKEY = 2
    }
    decode_operation;



    /*typedef*/ struct decode_context_T
        {
        hashtable * keyRings;
        recordSummary current;
        cb_context cbContext;
        byte splitkey; /* takes a decode_operation value */
        size_t cv_len;
        cv_details cv_algor[MAXCVALGS];
        byte * cv_data;
    }/*decode_context*/
    ;

    /*typedef*/ struct encryptInsts_T
        {
        char * filename; 
        byte version; /* PGP version values are 2 for up to 2.5, 3 for 2.6 */
        char fileType; /* 't' for text or 'b' for binary */
        byte md_algor; /* Message digest algorithm */
        byte cp_algor; /* Compression algorithm */
        byte armour; /* Armour format; see armour_style defined in armour.h */
        byte padding; /* re-establish 4-byte alignment */
        cv_details cv_algor[MAXCVALGS]; /* conventional key algorithm details */
        /* size_t cv_len;
         void * cv_data;*/ /* keys &c for the above */
        pubkey * * to; /* NULL terminated list of recipients - empty=>conventional*/
        seckey * signatory;
        char **comments;
        int max_lines;
    }/*encryptInsts*/
    ;

    /* Utility to turn a UTF8 encoded file with canonical line ends
    ** into a text file in the current locale with local line ends.
    ** Intended to be called from cb_result_file if it should encounter
    ** a file of type '8' (as opposed to 't' or 'b');
    */
    void UTF8decode(DataFileP from, DataFileP to);

    /* Examine and decypher a binary input file */
    continue_action CTCLIB_DLL examine(DataFileP input, decode_context * context);

    /* Examine and decypher an armoured text input file */
    continue_action CTCLIB_DLL examine_text(DataFileP input, decode_context * context);

    /* Encrypt file according to instructions (closes source file) */
    boolean CTCLIB_DLL encrypt(DataFileP source, DataFileP output, encryptInsts * instructions);

    /* Sign file according to instructions (closes source file) */
    boolean CTCLIB_DLL signOnly(DataFileP source, DataFileP output, encryptInsts * instructions);

    /* Write public key block to file */
    boolean CTCLIB_DLL key_extract(DataFileP file, pubkey * pub_key);

    /* Write a key revocation certificate and optionally keep the revocation record
    ** (typically the revocation record is not kept as the revocation certificate is 
    **  normally written on key generation)  */
    boolean CTCLIB_DLL key_revoke(DataFileP file, seckey * sec_key, boolean permanently);


#ifdef __cplusplus
}
END_NAMESPACE
#endif

#endif       

/* end of file ctc.h */
