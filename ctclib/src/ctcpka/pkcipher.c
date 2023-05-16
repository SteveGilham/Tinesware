/* pkcipher.c
 **
 **  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> March 1997
 **  All rights reserved.  For full licence details see file licences.c
 **
 **  This one is not as tidily separated as cipher.* and hash.* due
 **  to the depth to which RSA was embedded in the PGP and early CTC design#
 **
 **  This code also has to know about keyring files; thus has consumed some
 **  of the simpler functions of keyio.c into itself.
 **
 **  There are some other PKA-specific bits of code in keyutils.c; it would
 **  be possible to separate decryption context and key structure, but it
 **  would mean poassing a lot of context (and probably function pointers)
 **  back and forth.  The names of some of the routines would also get laboured
 **  as all the obvious ones are taken. *sigh*
 **
 **  This and pkops.c are the sort-of algorithm independent interface
 */

#include <assert.h>
#include <string.h>
#include "bignums.h"
#include "hash.h"
#include "keyio.h"
#include "keyhash.h"
#include "pkautils.h"
#include "pkcipher.h"
#include "port_io.h"
#include "utils.h"

#if defined(_MSC_VER) && !defined (__BORLANDC__) 
// BUG in VS .NET result += (uint16_t) value triggers this
#pragma warning (disable: 4244) // conversion from int to short/uint16_t
#endif


/*---------------------- Algorithm availability ----------------------*/

boolean valid_PKE_algor(byte algor)
{
    switch (algor)
    {
#ifndef NO_RSA
    case PKA_RSA:
    case PKA_RSA_ENCRYPT_ONLY:
    case PKA_RSA_SIGN_ONLY:
    case PKA_EBP_RSA:
#endif
#ifndef NO_PEGWIT8
    case PKA_GF2255:
#endif
    case PKA_ELGAMAL:
    case PKA_DSA:
        return (boolean)TRUE;

    default:
        return (boolean)FALSE;
    }
}

boolean CTCPKA_DLL recognised_PKE_algor(byte algor)
{
    switch (algor)
    {
    case PKA_RSA:
    case PKA_RSA_ENCRYPT_ONLY:
    case PKA_RSA_SIGN_ONLY:
    case PKA_EBP_RSA:
    case PKA_GF2255:
    case PKA_PEGWIT9:
        return TRUE;
    default:
        return valid_PKE_algor(algor);
    }
}


int PKEsize(byte algor, PKEsizeParam purpose)
{/* PUB_SIZE  SEC_SIZE  CIPH_SIZE SIG_SIZE  CHAR_NUM */
#ifndef NO_RSA
    static const PKEparams rsa = {
        2, 4, 1, 1, 0         };
#endif
#ifndef NO_PEGWIT8
    static const PKEparams ec = {
        1, 1, 2, 2, 0         };
#endif
    static const PKEparams elgamal = {
        3, 1, 2, 2, 2         };
    static const PKEparams dsa = {
        4, 1, 2, 2, 3         };
    static const PKEparams maxima = {
        MAXPUBNUMS, MAXSECNUMS, MAXCYPHNUMS,
        MAXSIGNUMS, MAXPUBNUMS-1         };
    int result;

    assert(purpose >= 0 && purpose < PKE_SIZE_ARRAY_SIZE);
    switch (algor)
    {
#ifndef NO_RSA
    case PKA_RSA:
    case PKA_RSA_ENCRYPT_ONLY:
    case PKA_RSA_SIGN_ONLY:
    case PKA_EBP_RSA:
        result = rsa[purpose];
        break;
#endif
#ifndef NO_PEGWIT8
    case PKA_GF2255:
        result = ec[purpose];
        break;
#endif
    case PKA_ELGAMAL:
        result = elgamal[purpose];
        break;

    case PKA_DSA:
        result = dsa[purpose];
        break;

    default:
        result = -1;
    }
    assert(result <= maxima[purpose]);
    return result;
}


/*---------------------- I/O Utilities ------------------------*/

static boolean read_mpn_pub(bignump number, DataFileP file)
/* Read a multiprecision integer from a file.  */
{
    byte buffer[MAXBUFFERSIZE + 2];
    uint16_t bytecount;

    if(vf_read(buffer, 2, file) < 2) return FALSE;
    bytecount = byte_length(buffer);
    if(bytecount > MAXBUFFERSIZE) return FALSE;
    if(vf_read(buffer + 2, bytecount, file) < bytecount) return FALSE;    /*
     *     eof */
    get_mpn(number, buffer);
    return TRUE;
}


static byte * read_mpn_sec(DataFileP file)
/* Read a multiprecision integer from a file.  */
{
    byte length[2];
    uint16_t bytecount;
    byte * result;

    if(vf_read(length, 2, file) < 2) return NULL;
    bytecount = byte_length(length);
    result = (byte *)qmalloc(bytecount + 2);
    if(result)
    {
        memcpy(result, length, 2);
        if(vf_read(result + 2, bytecount, file) < bytecount)
        {
            qfree(result);
            return NULL;
        }
    }
    return result;
}

static boolean write_mpn_pub(bignump n, DataFileP file)
/* Write a multiprecision integer to a file. */
{
    byte buffer[MAXBUFFERSIZE + 2];
    short bytecount;

    bytecount = put_mpn(buffer, n);
    return (boolean)(vf_write(buffer, bytecount + 2, file) == bytecount + 2);
}

static boolean write_mpn_sec(byte * data, DataFileP file)
{
    short bytecount;

    bytecount = byte_length(data);
    return (boolean)(vf_write(data, bytecount + 2, file) == bytecount + 2);
}

/*---------------------- record MPI management  ----------------------*/


void release_pubkey(pubkey * pub_key)/* free associated storage */
{
    int Nnums = PKEsize(pub_key->pkalg, PKE_PUB_SIZE);
    int offset = -1;

    if(Nnums >= 0)
    {
        while(++offset < Nnums)
            clear_mpn(&pub_key->pkdata.nums[offset]);
    }
}

void release_signature(sigDetails *sig)/* free associated storage */
{
    int Nnums = PKEsize(sig->pk_algor, PKE_SIG_SIZE);
    int offset = -1;

    if(Nnums >= 0)
    {
        while(++offset < Nnums)
        {
            clear_mpn(&sig->signature.nums[offset]);
        }
    }
}

void release_seckey(seckey * sec_key)/* free associated storage */
{
    int Nnums = PKEsize(sec_key->publicKey->pkalg, PKE_SEC_SIZE);
    int offset = -1;

    if(Nnums >= 0)
    {
        while(++offset < Nnums)
        {
            clear_mpn(&sec_key->pkdata.nums[offset].plain);
            qfree(sec_key->pkdata.nums[offset].cypher);
        }
    }
}


void prepare_pubkey(pubkey * pub_key)
{
    int Nnums = PKEsize(pub_key->pkalg, PKE_PUB_SIZE);
    int offset = -1;

    if(Nnums >= 0)
    {
        while(++offset < Nnums)
            init_mpn(&pub_key->pkdata.nums[offset]);
    }
}

void prepare_signature(sigDetails * sig)
{
    int Nnums = PKEsize(sig->pk_algor, PKE_SIG_SIZE);
    int offset = -1;

    if(Nnums >= 0)
    {
        while(++offset < Nnums)
            init_mpn(&sig->signature.nums[offset]);
    }
}

void prepare_seckey(seckey * sec_key)
{
    int Nnums = PKEsize(sec_key->publicKey->pkalg, PKE_SEC_SIZE);
    int offset = -1;

    if(Nnums >= 0)
    {
        while(++offset < Nnums)
        {
            clear_mpn(&sec_key->pkdata.nums[offset].plain);
        }
    }
}

void assign_pubkey(pubkey * to, pubkey * from)
{
    int Nnums = PKEsize(from->pkalg, PKE_PUB_SIZE);
    int offset = -1;

    to->pkalg = from->pkalg;
    if(Nnums >= 0)
    {
        while(++offset < Nnums)
            to->pkdata.nums[offset] = from->pkdata.nums[offset];
    }
}

void assign_seckey(seckey * to, seckey * from)
{
    int Nnums = PKEsize(from->publicKey->pkalg, PKE_SEC_SIZE);
    int offset = -1;

    assert(from->publicKey->pkalg == to->publicKey->pkalg);
    if(Nnums >= 0)
    {
        while(++offset < Nnums)
            to->pkdata.nums[offset].plain = from->pkdata.nums[offset].plain;
    }
}

boolean read_mpn_pubkey(pubkey * pub_key, DataFileP file)
{
    int Nnums = PKEsize(pub_key->pkalg, PKE_PUB_SIZE);
    int offset = -1;

    if(Nnums >= 0)
    {
        while(++offset < Nnums)
        {
            if(!read_mpn_pub(&pub_key->pkdata.nums[offset], file)) return FALSE;
        }
        return TRUE;
    }
    return FALSE;
}

boolean read_mpn_summary(byte alg, recordSummary *summary, DataFileP file)
{
    bignum n;
    int count = PKEsize(alg, PKE_CHAR_NUM) + 1;

    init_mpn(&n);

    while(count-- > 0)
    {
        if(!read_mpn_pub(&n, file))
        {
            clear_mpn(&n);            /* Added to deallocate n */
            return (boolean)FALSE;
        }
    }

    switch (alg)
    {
#ifndef NO_RSA
    case PKA_RSA:
    case PKA_RSA_ENCRYPT_ONLY:
    case PKA_RSA_SIGN_ONLY:
    case PKA_EBP_RSA:
        extractLSB(summary->itemID, n);
        summary->size = length_mpn(n);
        break;
#endif
#ifndef NO_PEGWIT8
    case PKA_GF2255:
        extractLSB(summary->itemID, n);
        summary->size=240;
        break;
#endif
    default:
        memset(summary->itemID, 0, sizeof(summary->itemID));
        summary->size = length_mpn(n);
    }
    clear_mpn(&n);
    return TRUE;
}

boolean read_mpn_signature(sigDetails * sig, DataFileP file)
{
    int Nnums = PKEsize(sig->pk_algor, PKE_SIG_SIZE);
    int offset = -1;

    if(Nnums >= 0)
    {
        while(++offset < Nnums)
            if(!read_mpn_pub(&sig->signature.nums[offset], file)) return FALSE;
        return TRUE;
    }
    return FALSE;
}

boolean read_mpn_seckey(seckey * sec_key, DataFileP file)
{
    int Nnums = PKEsize(sec_key->publicKey->pkalg, PKE_SEC_SIZE);
    int offset = -1;

    if(Nnums >= 0)
    {
        while(++offset < Nnums)
            if((sec_key->pkdata.nums[offset].cypher = read_mpn_sec(file)) == 0) return FALSE;
        return TRUE;
    }
    return (boolean)FALSE;
}


uint16_t length_pubkey(pubkey * pub_key)
{
    int Nnums = PKEsize(pub_key->pkalg, PKE_PUB_SIZE);
    int offset = -1;
    uint16_t result = 0;

    if(Nnums >= 0)
    {
        while(++offset < Nnums)
            result += (uint16_t) (size_mpn(pub_key->pkdata.nums[offset]) + (uint16_t)2);
    }
    return result;
}

uint16_t length_signature(sigDetails * sig)
{
    int Nnums = PKEsize(sig->pk_algor, PKE_SIG_SIZE);
    int offset = -1;
    uint16_t result = 0;

    if(Nnums >= 0)
    {
        while(++offset < Nnums)
            result += (uint16_t) (size_mpn(sig->signature.nums[offset]) + (uint16_t)2);
    }
    return result;
}

uint16_t length_seckey(seckey * sec_key)
{
    int Nnums = PKEsize(sec_key->publicKey->pkalg, PKE_SEC_SIZE);
    int offset = -1;
    uint16_t result = 0;

    if(Nnums >= 0)
    {
        while(++offset < Nnums)
            result += (uint16_t) (byte_length(sec_key->pkdata.nums[offset].cypher) + (uint16_t)2);
    }
    return result;
}

void put_pubkey(byte * buffer, pubkey * pub_key)
{
    int Nnums = PKEsize(pub_key->pkalg, PKE_PUB_SIZE);
    int offset = -1;

    if(Nnums >= 0)
    {
        while(++offset < Nnums)
            buffer += put_mpn(buffer, &pub_key->pkdata.nums[offset]) + 2;
    }
}

boolean write_signature(sigDetails * sig, DataFileP file)
{
    int Nnums = PKEsize(sig->pk_algor, PKE_SIG_SIZE);
    int offset = -1;

    if(Nnums >= 0)
    {
        while(++offset < Nnums)
            if(!write_mpn_pub(&sig->signature.nums[offset], file)) return FALSE;
        return TRUE;
    }
    return FALSE;
}

boolean write_seckey(seckey * sec_key, DataFileP file)
{
    int Nnums = PKEsize(sec_key->publicKey->pkalg, PKE_SEC_SIZE);
    int offset = -1;

    if(Nnums >= 0)
    {
        while(++offset < Nnums)
            if(!write_mpn_sec(sec_key->pkdata.nums[offset].cypher, file)) return FALSE;
        return TRUE;
    }
    return FALSE;
}



static byte algtype(pubkey *p)
{
    if(PKA_RSA_ENCRYPT_ONLY == p->pkalg) return PKA_RSA;
    else if (PKA_RSA_SIGN_ONLY == p->pkalg) return PKA_RSA;
    else if (PKA_EBP_RSA == p->pkalg) return PKA_RSA;
    else return p->pkalg;
}

boolean equate_pubkey(pubkey *right, pubkey * left)
{
    int Nnums = PKEsize(right->pkalg, PKE_PUB_SIZE);
    int offset = -1;

    if(algtype(left) != algtype(right)) return FALSE;
    if(Nnums >= 0)
    {
        while(++offset < Nnums)
            if(!eq_mpn(left->pkdata.nums[offset], right->pkdata.nums[offset])) return FALSE;
        return TRUE;
    }
    return FALSE;
}


static void reassignSig(signature * sig, pubkey * to, pubkey * from)
{
    if(sig)
    {
        if(sig->from == from) sig->from = to;
        reassignSig(sig->next, to, from);
    }
}


static void reassignName(username * name, pubkey * to, pubkey * from)
{
    if(name)
    {
        reassignSig(name->signatures, to, from);
        reassignName(name->next, to, from);
    }
}

static void reassignSubkey(pubkey * subkey, pubkey * to, pubkey * from)
{
    if(subkey)
    {
        if(subkey->superkey == from) subkey->superkey = to;
        reassignSig(subkey->directSig, to, from);
        reassignSubkey(subkey->subkeys, to, from);
    }
}

void move_pubkey(pubkey * to, pubkey * from)
{
    assign_pubkey(to, from);
    /* Reassign any self-signature records */
    reassignName(to->userids, to, from);
    reassignSig(to->directSig, to, from);
    reassignSubkey(to->subkeys, to, from);
    prepare_pubkey(from);
}

void extractKeyfrag(pubkey * pub_key)
{
    if(pub_key->version < VERSION_3)
    {
        int offset = PKEsize(pub_key->pkalg, PKE_CHAR_NUM);

        extractLSB(pub_key->keyId, pub_key->pkdata.nums[offset]);
    }
    else
    {
        byte hashAlgor = MDA_PGP5_SHA1;        /* Currently hard-wired; no real need to be */
        md_context context;
        byte hash[MAXHASHSIZE];

        context = hashInit(hashAlgor);
        if(context && keyHashUpdate(pub_key, context))
        {
            hashFinal(&context, hash);
            memcpy(pub_key->keyId, hash + hashDigest(hashAlgor) - KEYFRAGSIZE, KEYFRAGSIZE);
        }

    }
}


void sizePubkey(pubkey * pub_key)
{
    int offset = PKEsize(pub_key->pkalg, PKE_CHAR_NUM);

    switch (pub_key->pkalg)
    {
    case PKA_GF2255:
        pub_key->size = 240;        /*leading zeroes count, though are not stored */
        break;

    default:
        if(offset >= 0)
            pub_key->size = length_mpn(pub_key->pkdata.nums[offset]);
    }
}


/*----------------------Algorithm data management----------------------*/

boolean read_CKcypher(bignump cypher, DataFileP file, byte algor)
{
    int Nnums = PKEsize(algor, PKE_CIPH_SIZE);
    int offset = -1;
#ifndef NO_PEGWIT8
    if(algor == PKA_GF2255)
    {
        byte ecBlockPairs = 0;
        if((1 != vf_read(&ecBlockPairs, 1, file)) ||
            (1 != ecBlockPairs)) return FALSE;
    }
#endif
    if(Nnums >= 0)
    {
        while(++offset < Nnums)
            if(!read_mpn_pub(&cypher[offset], file)) return FALSE;
        return TRUE;
    }
    return FALSE;
}


uint16_t length_CKcypher(keyDetails *details)
{
    uint16_t result = 0;
    int Nnums = PKEsize(details->pk_algor, PKE_CIPH_SIZE);
    int offset = -1;
#ifndef NO_PEGWIT8
    if(details->pk_algor == PKA_GF2255) result += (uint16_t) 1;
#endif
    if(Nnums >= 0)
    {
        while(++offset < Nnums)
            result += (uint16_t) (size_mpn(details->cypherKey[offset]) + (uint16_t)2);
    }
    return result;
}


boolean write_CKcypher(keyDetails *details, DataFileP file)
{
    int Nnums = PKEsize(details->pk_algor, PKE_CIPH_SIZE);
    int offset = -1;
#ifndef NO_PEGWIT8
    if(details->pk_algor == PKA_GF2255)
    {
        byte ecBlockPairs = 1;

        if(1 != vf_write(&ecBlockPairs, 1, file)) return FALSE;
    }
#endif
    if(Nnums >= 0)
    {
        while(++offset < Nnums)
            if(!write_mpn_pub(&details->cypherKey[offset], file)) return FALSE;
        return TRUE;
    }
    return FALSE;
}

/* end of pkcipher.c */
