/* pkautils.c
 **
 **  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 **  Heimdall <heimdall@bifroest.demon.co.uk>  1996-7
 **  All rights reserved.  For full licence details see file licences.c
 **
 **  This file may include some modified code from
 **  PGP: Pretty Good(tm) Privacy - public key cryptography for the masses.
 ** (c) Copyright 1990-1992 by Philip Zimmermann.
 **
 **  Note that this file along with pkcipher.c contains some branches
 **  conditioned on the public key algorithm in use.  -- Mr. Tines 25-Mar-1997
 **
 **  This file has been split from the previous keyutils.c in order to
 **  split out the PKA-dependent branches  --Mr. Tines 14-Sep-1997
 **
 **  This file generalised to operate on arrays off bignums, rather than
 **  on per-algorithm structures.  The option to add other behaviour as exceptions
 **  remains, but algorithms using fixed numbers of bignums as keys, ciphertexts and
 **  signatures can use the default behaviour through out. -- Heimdall 20-Sep-1997
 */
#include <assert.h>
#include <string.h>
#include "bignums.h"
#include "pkautils.h"
#include "hash.h"
#include "hashpass.h"
#include "pkcipher.h"
#include "random.h"
#include "usrbreak.h"
#include "utils.h"

#if defined(_MSC_VER) && !defined (__BORLANDC__) 
// BUG in VS .NET result += (uint16_t) value triggers this
#pragma warning (disable: 4244) // conversion from int to short/uint16_t
#endif


static void internalise_mpn(bignump internal, byte * external,
uint16_t * checksum, cv_keysched context)
/* Read a mutiprecision integer from a file.  Optionally decrypting it.
 ** N.B. The encrypted copy is treated as READ-ONLY; the output functions
 **      assume it is safe to write it unmodified to disc. */
{
    uint16_t bytecount;
    byte buffer[MAXBUFFERSIZE + 2];

    bytecount = byte_length(external);
    if(bytecount > MAXBUFFERSIZE) return;
    memcpy(buffer, external, bytecount + 2);
    if(context) cipherDo(context, buffer + 2, bytecount);
    *checksum += calcchecksum(buffer, (uint16_t)(bytecount + (uint16_t)2));
    init_mpn(internal);
    get_mpn(internal, buffer);
}


static int externalise_mpn( byte * * external, bignump internal,
uint16_t * checksum, cv_keysched context)
{
    uint16_t bytecount;
    uint16_t length;

    bytecount = size_mpn(*internal);
    if(*external)
    {
        /* external buffer already allocated => check it is big enough */
        length = byte_length(*external);
        if(bytecount > length) return FALSE;
    }
    else
    {
        *external = qmalloc(bytecount + 2);
        if(!*external) return FALSE;
        /* length will be filled in by put_mpn */
    }
    put_mpn(*external, internal);
    *checksum += calcchecksum(*external, (uint16_t)(bytecount + (uint16_t)2));
    if(context) cipherDo(context, *external + 2, bytecount);
    return TRUE;
}

boolean internalise_seckey(seckey * sec_key, const char * password)
{
    int Nnums;
    int offset = -1;
    /* This converts an external format secret key to an internal
     *  format one.  This involves three operations:-
     *  1) Changing to canonical external format
     *  2) Taking a checksum (used to check for correct decryption)
     *  3) Encrypting    */
    uint16_t checksum = 0;
    byte iv[MAXBLOCKSIZE];
    cv_keysched context = 0;

    if(!sec_key) return FALSE;
    if(sec_key->skstat == INTERNAL) return TRUE;
    if(sec_key->skstat != EXTERNAL) return FALSE;
    if(!valid_PKE_algor(sec_key->publicKey->pkalg)) return FALSE;

    /* take rollback copy  */
    if(sec_key->kpalg.cv_algor != CEA_NONE)
    {
        int blockSize = cipherBlock(sec_key->kpalg.cv_algor);
        /* it will take a significant rewrite to allow deferred
         * encryption modes such as CBC or even ECB which need
         * to hold data back for ciphertext stealing.  So bomb
         * if we get bad data.  In future, the fact that we have
         * a block's worth of IV will enable us to do CTS, though */
        assert(CEM_CFB == sec_key->kpalg.cv_mode);
        if(!cipherAlgAvail(sec_key->kpalg.cv_algor)) return FALSE;
        if(!password) return FALSE;
        {
            byte s2k[2];
            s2k[0] = 0;
            s2k[1] = sec_key->hashalg;
            hashpassEx(password, sec_key->cea_key,
            cipherKey(sec_key->kpalg.cv_algor), s2k, TRUE);
        }
        context = cipherInit(&sec_key->kpalg,
        sec_key->cea_key, TRUE         /*decrypt*/);
        if(!context) return FALSE;
        memcpy(iv, sec_key->iv, blockSize);
        cipherDo(context, iv, blockSize);
    }

    Nnums = PKEsize(sec_key->publicKey->pkalg, PKE_SEC_SIZE);
    /* N.B. case of non-bignum key not handled */
    while(++offset < Nnums)
    {
        internalise_mpn(&sec_key->pkdata.nums[offset].plain,
        sec_key->pkdata.nums[offset].cypher,
        &checksum, context);
    }

    /* we can safely assume none left over */
    cipherEnd(&context, (byte*)0);

    if(checksum == sec_key->checksum && verifySecKey(sec_key))
    {
        sec_key->skstat = INTERNAL;
        return TRUE;
    }

    /* wrong pass-phrase -> wipe internal copy */
    offset = -1;
    while(++offset < Nnums) clear_mpn(&sec_key->pkdata.nums[offset].plain);
    sec_key->skstat = EXTERNAL;
    return FALSE;
}

void encrypt_seckey(seckey * sec_key)
{
    int Nnums = PKEsize(sec_key->publicKey->pkalg, PKE_SEC_SIZE);
    int offset = -1;
    cv_keysched context = 0;

    /* This generate an external format secret key from an
     * internal format one.  This needs to be done for new keys
     * or whenever the key encryption key is changed.
     * This involves three operations:-
     *  1) Changing to canonical external format
     *  2) Taking a checksum (used to check for correct decryption)
     *  3) Encrypting
     */

    if(sec_key->skstat != INTERNAL) return;
    if(sec_key->kpalg.cv_algor != CEA_NONE)
    {
        /* it will take a significant rewrite to allow deferred
         * encryption modes such as CBC or even ECB which need
         * to hold data back for ciphertext stealing.  So bomb
         * if we get bad data.  In future, the fact that we have
         * a block's worth of IV will enable us to do CTS, though */
        assert(CEM_CFB == sec_key->kpalg.cv_mode);
        context = cipherInit(&sec_key->kpalg,
        sec_key->cea_key, FALSE         /*encrypt*/);
        cipherDo(context, sec_key->iv, cipherBlock(sec_key->kpalg.cv_algor));
    }
    sec_key->checksum = 0;
    if(Nnums >= 0)
    {
        while(++offset < Nnums)
        {
            if(!externalise_mpn(&sec_key->pkdata.nums[offset].cypher,
            &sec_key->pkdata.nums[offset].plain,
            &sec_key->checksum, context))
            {
                sec_key->skstat = CORRUPT;
                break;
            }
        }
    }
    else
        sec_key->skstat = CORRUPT;

    cipherEnd(&context, (byte*)0);
    return;
}


void externalise_seckey(seckey * sec_key)
{
    /* This destroys the internal format key*/
    int Nnums = PKEsize(sec_key->publicKey->pkalg, PKE_SEC_SIZE);
    int offset = -1;

    if(!sec_key) return;
    if(sec_key->skstat != INTERNAL) return;

    if(Nnums >= 0)
    {
        while(++offset < Nnums)
            wipe_mpn(&sec_key->pkdata.nums[0].plain);
    }
    memset(sec_key->cea_key, 0, MAXKEYSIZE);    /* trash the key */
    /* The IV still remains encrypted if not deliberately decrypted earlier */
    /* memset(sec_key->iv, 0, MAXBLOCKSIZE); */
    sec_key->skstat = EXTERNAL;
    return;
}

/*  Calculate 'Fingerprint' (the only truely unique key id).  Others depend
 *  only on the Modulus.    Note that for the GF(2^255) option there *is*
 *  only one component to hash... */

void fingerPrint(byte *hash, pubkey * pub_key)/* PGP-style... */
{
    if(pub_key->version < VERSION_3)
        fingerPrintPlus(hash, pub_key, (byte)MDA_MD5);
    else
        fingerPrintOPGP(hash, pub_key, (byte)MDA_PGP5_SHA1);
}

void fingerPrintOPGP(byte *hash, pubkey * pub_key, byte algor)
{
    md_context context;
    context = hashInit(algor);
    if(context && keyHashUpdate(pub_key, context))
    {
        hashFinal(&context, hash);
    }
    else memset(hash, 0, hashDigest(algor));
}

/* ... and, as you like it! */
void fingerPrintPlus(byte *hash, pubkey * pub_key, byte algor)
{
    int Nnums = PKEsize(pub_key->pkalg, PKE_PUB_SIZE);
    int offset = -1;
    md_context mdContext;
    byte buffer[MAXBUFFERSIZE + 2];
    uint16_t length;

    if(!hashAlgAvail(algor)) return;

    mdContext = hashInit(algor);
    if(Nnums >= 0)
    {
        while(++offset < Nnums)
        {
            length = put_mpn(buffer, &pub_key->pkdata.nums[offset]);
            hashUpdate(mdContext, buffer + 2, (uint32_t)length);
        }
    }
    hashFinal(&mdContext, hash);
}

void extractLSB(byte keyID[KEYFRAGSIZE], bignum n)
{
    byte buffer[MAXBUFFERSIZE + 2];
    uint16_t length;

    length = put_mpn(buffer, &n);

    /* Now it might so happen that for en elliptic curve, the bignum
     * has a lot of leading zeroes, which have been omitted.  This
     * is unlikely, but not impossible - so cater for this 1 in
     * 2^(240-KEYFRAGSIZE) event */
    if(length >= KEYFRAGSIZE)
        memcpy(keyID, buffer + length + 2 - KEYFRAGSIZE, KEYFRAGSIZE);
    else
    {
        memset(keyID, 0, KEYFRAGSIZE);
        memcpy(keyID+KEYFRAGSIZE-length, buffer+2, length);
    }
}

/* PGP 5.0 fingerprint equivalent */
boolean keyHashUpdate(pubkey * pub_key, md_context context)
{
    uint16_t bufferSize = formatKeySize(pub_key);
    byte * buffer = qmalloc((size_t)bufferSize);

    if(!buffer) return FALSE;
    buffer[0] = CTB_DESIGNATOR + 4 * CTB_CERT_PUBKEY + 1;
    buffer[1] = (byte)(bufferSize /256);
    buffer[2] = (byte)(bufferSize % 256);
    hashUpdate(context, buffer, 3);
    formatPubkey(pub_key, buffer);
    hashUpdate(context, buffer, bufferSize);
    qfree(buffer);
    return TRUE;
}

uint16_t formatKeySize(pubkey * pub_key)
{
    return (uint16_t)
        (1 +
            SIZEOF_TIMESTAMP +
            ((pub_key->version < VERSION_3) ? SIZEOF_VALIDITY : 0) +
            1 +
            length_pubkey(pub_key));
}

void formatPubkey(pubkey * pub_key, byte * buffer)
{
    *buffer++ = pub_key->version;

    memcpy(buffer, pub_key->timestamp, SIZEOF_TIMESTAMP);
    convert_byteorder(buffer, SIZEOF_TIMESTAMP);
    buffer += SIZEOF_TIMESTAMP;

    if(pub_key->version < VERSION_3)
    {
        memcpy(buffer, pub_key->validity, SIZEOF_VALIDITY);
        buffer += SIZEOF_VALIDITY;
    }

    *buffer++ = pub_key->pkalg;

    put_pubkey(buffer, pub_key);
}

/* end of file pkautils.c */
