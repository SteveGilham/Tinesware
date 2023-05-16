/* pkops.c
 **
 **  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 **  Heimdall <heimdall@bifroest.demon.co.uk>  1996
 **  All rights reserved.  For full licence details see file licences.c
 */

/*
 *  This file contains the generic public key operations of {public|private}
 *  {encrypt|decrypt} of the message digest or session key, plus padding
 *  information.  Note that the underlying datastructures which interoperate
 *  with PGP are predicated on long ( >48 byte, typically 128byte ) keys and
 *  hence message packets that can be well stuffed with random noise (even
 *  though this padding could in theory be used as a subliminal channel, by
 *  choosing values to make some of the output bits leaky).
 * 
 *  If we implement any other public key system, this file will have to be torn
 *  into two and RSA and the other scheme hidden behind a netral interface such
 *  as is provided by cipher.c or hash.c.  In addition, if we implement a short
 *  key asymmetric system such as elliptic curve, the conventional keys plus
 *  impedimenta may in principle be longer than the block size; in which case we
 *  will have to cope with multiple public key blocks (perhaps in CFB mode?)
 * 
 *  (Later) The interrelation of the PK algorithm and the keyrings has made it
 *  awkward, short of a complete ground up rewrite, to make as clean a separation
 *  of public key algorithms as it has been for message digest and conventional
 *  secret key encryption.  The same is true for a lesser degree for compression;
 *  but that's partly due to it already being concentrated into two points
 *  already.
 */

#include "hash.h"
#include "random.h"
#include "bignums.h"
#include "pkbignum.h"
#include "pkops.h"
#include "callback.h"
#include "utils.h"
#include "pkcipher.h"
#include "keyhash.h"
#ifndef NO_PEGWIT8
#include "ec_crypt.h"
#endif
#ifndef NO_RSA
#include "rsa.h"
#endif

#include <string.h>

#define MAXENTROPY  200

static byte MD5_asn1_string[] =
{
    0x30, 0x20, 0x30, 0x0C, 0x06, 0x08,
    0x2a, 0x86, 0x48, 0x86, 0xf7, 0x0D,
    0x02, 0x05, 0x05, 0x00, 0x04, 0x10 };

static byte RIPEMD160_asn1_string[] =
{
    0x30, 0x21, 0x30, 0x09, 0x06, 0x05,
    43, 36, 0x03, 0x02, 0x01, 0x05,
    0x00, 0x04, 0x14 };

static byte SHA1_asn1_string[] =
{
    0x30, 0x21, 0x30, 0x09, 0x06, 0x05,
    43, 14, 0x03, 0x02, 26, 0x05,
    0x00, 0x04, 0x14 };
/*---------------------- Housekeeping ----------------------*/

static byte * getASNstring(byte mdalg, byte * length)
{
    switch(mdalg)
    {
    case MDA_MD5:
        *length = sizeof(MD5_asn1_string);
        return MD5_asn1_string;

    case MDA_PGP5_SHA1:
    case MDA_SHA1:
        *length = sizeof(SHA1_asn1_string);
        return SHA1_asn1_string;

    case MDA_PGP5_RIPEM160:
        *length = sizeof(RIPEMD160_asn1_string);
        return RIPEMD160_asn1_string;

    case MDA_EBP_HAVAL_MIN:
    case MDA_EBP_HAVAL_MAX:
    case MDA_3WAY:
    case MDA_SHA:

    default:
        *length = 0;
        return NULL;
    }
}
static void simpleCondition(short severity, short code, cb_context context)
{
    cb_condition condition = {
        0, 0, 0, 0, NULL, NULL         };

    condition.severity = severity;
    condition.module = CB_PKE;
    condition.code = code;
    condition.context = (short) context;
    cb_information(&condition);
}



/*----------------------PGP packet manipulation----------------------*/

static boolean extractConvKey(byte * buffer, short length,
cv_details * algor, size_t *keylen, byte * * convKey)
/* This routine is useable by both RSA and GF2^255 modes, since we have */
/* to be able to communicate such a packet across the link, by some */
/* means or other - it is thus candidate for moving to pkcipher.c */
{
    uint16_t check_sum;
    int keysize, version;
    byte *base = buffer;

    /* strip leading zeroes - case of elliptic curve may have these */
    while(!*base) ++base;
    version = *base;
    ++base;

    switch(version)
    {
    case 1:        /* PGP version 2.2 or earlier format */
        keysize = cipherKey(CEA_IDEA);
        if(!cipherAlgAvail(CEA_IDEA))
        {
            simpleCondition(CB_ERROR, PKE_UNIMP_BULK_CYPHER, CB_DECRYPTION);
            return FALSE;
        }
        if( buffer[1 + keysize + 2] != 0
            || buffer[length - 1] != 2)
        {
            simpleCondition(CB_ERROR, PKE_BAD_CHECKSUM, CB_DECRYPTION);
            return FALSE;
        }
        /*   base++; */
        algor[0].cv_algor = CEA_IDEAFLEX;
        algor[0].cv_mode = CEM_CFB;
        keysize = cipherKey(CEA_IDEA);
        break;

    case 2:        /* PGP version 2.3 or later format + our extensions */
        {
            /* skip non-zero bytes */
            int alg = 0;
            while(*base && ((base-buffer)<length)) base++;
            base++;            /* skip the zero 'heads-up' byte */

            /* special case 1st algorithm */
            if(!(CEA_FLEX_FLAG & *base))
            {
                algor[alg].cv_algor = flexAlg(*base);
                algor[alg].cv_mode = CEM_CFB;
                base++;
                keysize = cipherKey(algor[alg].cv_algor);
            }
            else
            {
                algor[alg].cv_algor = *base;
                base++;
                algor[alg].cv_mode = *base;
                base++;
                keysize = cipherKey(algor[alg].cv_algor) *
                    (algor[alg].cv_mode & CEM_TRIPLE_FLAG ? 3 : 1);
            }
            if(!cipherAlgAvail(algor[alg].cv_algor))
            {
                simpleCondition(CB_ERROR, PKE_UNIMP_BULK_CYPHER, CB_DECRYPTION);
                return FALSE;
            }
            if(!cipherModeAvail(algor[alg].cv_mode, algor[alg].cv_algor))
            {
                simpleCondition(CB_ERROR, PKE_UNIMP_MODE_OPER, CB_DECRYPTION);
                return FALSE;
            }
            /* read 2nd and subsequent algorithm+mode details */
            /* this will need tweaking if we want to embed more data here */
            while(algor[alg].cv_algor & CEA_MORE_FLAG)
            {
                alg++;
                if (MAXCVALGS <= alg) return FALSE;
                if(base-buffer >= length) return FALSE;
                algor[alg].cv_algor = *base;
                base++;
                algor[alg].cv_mode = *base;
                base++;
                keysize += cipherKey(algor[alg].cv_algor) *
                    (algor[alg].cv_mode & CEM_TRIPLE_FLAG ? 3 : 1);
                if(!cipherAlgAvail(algor[alg].cv_algor))
                {
                    simpleCondition(CB_ERROR, PKE_UNIMP_BULK_CYPHER, CB_DECRYPTION);
                    return FALSE;
                }
                if(!cipherModeAvail(algor[alg].cv_mode, algor[alg].cv_algor)) 
                {
                    simpleCondition(CB_ERROR, PKE_UNIMP_MODE_OPER, CB_DECRYPTION);
                    return FALSE;
                }
            }
            /* is there room for the key here ? */
            if( (base-buffer)+keysize+2 > length) return FALSE;
        }
        break;

    default:        /* unrecognised format */
        simpleCondition(CB_ERROR, PKE_UNKN_VERSION, CB_DECRYPTION);
        return FALSE;
    }
    *keylen = keysize;
    *convKey = zmalloc(*keylen);
    memcpy(*convKey, base, keysize);
    check_sum = EXTRACT_SHORT(base+keysize);
    if(check_sum != calcchecksum(*convKey, (uint16_t) keysize))
    {
        simpleCondition(CB_ERROR, PKE_BAD_CHECKSUM, CB_DECRYPTION);
        return FALSE;
    }
    return TRUE;
}

#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (disable: 4706)
#endif

static boolean formatSessionKey(byte * buffer, short length, byte version,
cv_details * algor, size_t keylen, byte *key)
/* This routine is useable by both RSA and GF2^255 modes, since we have */
/* to be able to communicate such a packet across the link, by some */
/* means or other - it is thus candidate for moving to pkcipher.c */
{
    /* note minimum length checks require 8 bytes of random data */
    uint16_t check_sum = calcchecksum(key,(uint16_t)keylen);
    unsigned int offset;
    boolean ideacfb = (boolean) !(algor[0].cv_algor & CEA_FLEX_FLAG);
    short size = 1;

    convert_byteorder((byte*)&check_sum, 2);
    if(length < 30)    /* and that only for PGP-classic style */
    {
        simpleCondition(CB_ERROR, PKE_KEY_TOO_SHORT, CB_ENCRYPTION);
        return FALSE;
    }
    buffer[0] = 0;

    /* ensure if it's not IDEA-CFB we're doing that version is overridden */
    if(!ideacfb)    /* literally - if high (flex) bit is set */
    {
        if((algor[0].cv_algor!= CEA_IDEAFLEX) ||
            (algor[0].cv_mode != CEM_CFB))
        {
            if (2 == version) version = 3;
        }
        else
        {
            ideacfb = TRUE;            /* make it real IDEA+default CFB*/
            algor[0].cv_algor = CEA_IDEA;
        }
    }

    if(!ideacfb)
    {
        int i = 0;
        size = 2;
        while(algor[i].cv_algor & CEA_MORE_FLAG)
        {
            if((MAXCVALGS-1)==i) break;
            size+= (short)2;
            i++;
        }
    }

    if(length < (long)(5+keylen+size+8)    /*at least this much padding*/)
    {    /* leading 0,2 + heads-up byte + checksum = 5 */
        simpleCondition(CB_ERROR, PKE_KEY_TOO_SHORT, CB_ENCRYPTION);
        return FALSE;
    }    /* a 128 bit key and 2 bytes of algorithm+mode =18, +13 = 31bytes! */

    switch(version)
    {
    case 2:        /* PGP up to version 2.2 */
        buffer[1] = 1;
        memcpy(buffer + 2, key, 16);
        memcpy(buffer + 18, &check_sum, 2);
        buffer[20] = 0;
        offset = 20;
        while(++offset < (unsigned int) length - 1)
            while(!(buffer[offset] = mixedRandom())){
                ;
            }
        buffer[length - 1] = 2;
        return TRUE;

    case 3:        /* PGP 2.3 to ??? */
        buffer[1] = 2;
        offset = 1;
        while(++offset < length - (keylen+size+3))
            while(!(buffer[offset] = mixedRandom())){
                ;
            }
        buffer[offset] = 0; 
        offset++;
        memcpy(buffer+offset, algor, size); 
        offset+=size;
        memcpy(buffer + offset, key, keylen);
        memcpy(buffer + length - 2, &check_sum, 2);
        return TRUE;

    default:
        simpleCondition(CB_ERROR, PKE_UNKN_VERSION, CB_ENCRYPTION);
        return FALSE;
    }
}
#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (default: 4706)
#endif


static boolean formatSignature(byte * buffer, short length,
byte version, byte signature[MAXHASHSIZE],
byte mdalg, boolean useASN)
/* This routine is useable by both RSA and GF2^255 modes, since we have */
/* to agree a duplicable format for the hash packet which is signed */
/* - it is thus candidate for moving to pkcipher.c */
{
    byte fixed = 0;
    byte * asn1_string = useASN ? getASNstring(mdalg, &fixed) : NULL;
    byte siglen = (byte) hashDigest(mdalg);

    switch(version)
    {
    case 2:        /* PGP up to version 2.5 */
        if(length < 20) return FALSE;
        buffer[0] = 0;
        buffer[1] = 1;
        memcpy(buffer + 2, signature, 16);
        buffer[18] = 0;
        memset(buffer + 19, 0xFF, length - 20);
        buffer[length - 1] = 1;
        return TRUE;

    case 3:        /* PGP 2.6 to ??? */
    case 4:        /* This aspect of format unchanged at PGP 3 */
        if(length < 3+fixed+siglen) return FALSE;
        buffer[0] = 0;
        buffer[1] = 1;
        memset(buffer + 2, 0xFF, length - (fixed + siglen + 3));
        buffer[length - (fixed + siglen + 1)] = 0;
        if(fixed > 0) memcpy(buffer + length - (fixed + siglen), asn1_string, fixed);
        memcpy(buffer + length - siglen, signature, siglen);
        return TRUE;

    default:
        simpleCondition(CB_ERROR, PKE_UNKN_VERSION, CB_SIGNING);
        return FALSE;
    }
}
/*-----------------algorithm sensitive formats -----------------*/
#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (disable: 4701)
#endif
boolean unpackConvKey(bignum* cyphertextkey, byte pk_algor,
seckey * sec_key, cv_details * algor,
size_t *keylen, byte * * convKey)
{
    bignum plaintextkey;
    byte keybuffer[MAXBUFFERSIZE+2];
    short bytes, b2;
    common_error result;
    int j;
    if(sec_key->skstat != INTERNAL) return FALSE;    /* key still encrypted */

    init_mpn(&plaintextkey);
    switch(pk_algor)
    {
#ifndef NO_RSA
    case PKA_RSA:
    case PKA_RSA_ENCRYPT_ONLY:
    case PKA_RSA_SIGN_ONLY:
    case PKA_EBP_RSA:

        /* RSA Decrypt the key */
        result = rsa_private_operation(&plaintextkey, &cyphertextkey[0], sec_key);
        /* Extract the decrypted key byte string */
        if(result == CE_OKAY) bytes = put_mpn(keybuffer, &plaintextkey);
        break;
#endif

#ifndef NO_PEGWIT8
    case PKA_GF2255:

        /* Perform EC key exchange and recover CKE block by XOR */
        cpDecodeSecret(sec_key->pkdata.nums[ECSEC].plain, cyphertextkey[0], &plaintextkey);
        bytes = put_mpn(keybuffer+100, &plaintextkey);        /* should check */
        b2 = put_mpn(keybuffer+200, &cyphertextkey[1]);        /* max32 bytes each */

        memset(keybuffer, 0, 66);
        keybuffer[0] = (byte) 1;
        keybuffer[1] = (byte) 0;        /* 256 bits = 32 bytes */
        memcpy(keybuffer+2+(32-bytes), keybuffer+102, bytes);
        memcpy(keybuffer+2+32+(32-b2), keybuffer+202, b2);
        memset(keybuffer+66, 0, 202+b2-66);

        for(j=0; j<32; ++j)
        {
            keybuffer[2+j] ^= keybuffer[2+j+32];
            keybuffer[2+j+32] = 0;
        }
        result = CE_OKAY;        /* we hope */
        break;
#endif

    default:

        simpleCondition(CB_CRASH, PKE_UNIMP_PKE_CYPHER, CB_DECRYPTION);
        result = CE_OTHER;
        break;
    }
    for(j=0; j<MAXCYPHERMPIS; j++) clear_mpn(&cyphertextkey[j]);
    clear_mpn(&plaintextkey);


    switch(result)
    {
    case CE_OKAY:
        return extractConvKey(keybuffer + 2, bytes, algor, keylen, convKey);

    case CE_NO_MEMORY:
        simpleCondition(CB_ERROR, PKE_NO_MEMORY, CB_DECRYPTION);
        break;

    case CE_USER_BREAK:
        simpleCondition(CB_ERROR, PKE_USER_BREAK, CB_DECRYPTION);
        break;

    default:
        simpleCondition(CB_CRASH, PKE_BAD_RETURN_CODE, CB_DECRYPTION);
    }
    return FALSE;
}
#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (default: 4701)
#endif

common_error packConvKey(keyDetails *details, byte version, byte pk_algor,
pubkey * pub_key, cv_details * algor, size_t keylen, byte *convKey)
{
    common_error result;
    bignum plaintextkey;
    byte keybuffer[MAXBUFFERSIZE+2];
    short length, bytes;
    uint16_t bitcount;
    int j;
    /* only plain RSA is allowed a version number < 3 */
    details->version = (byte)((PKA_RSA == pk_algor) ? version :
    (version < 3 ? 3 : version) );
    details->pk_algor = pk_algor;

    /* Initialise cyphertext before any potential error exits as the
     ** calling routine will clear them anyway and fails if they aren't
     ** initialised. */
    for(j=0; j<MAXCYPHERMPIS; j++) init_mpn(&details->cypherKey[j]);
    switch(pk_algor)
    {
#ifndef NO_RSA
    case PKA_RSA:
    case PKA_RSA_ENCRYPT_ONLY:
    case PKA_RSA_SIGN_ONLY:
    case PKA_EBP_RSA:
        length = size_mpn(pub_key->pkdata.nums[RSAPUB_N]);
        break;
#endif

    case PKA_ELGAMAL:
        length = size_mpn(pub_key->pkdata.nums[EGPUB_P]);
        break;

#ifndef NO_PEGWIT8
    case PKA_GF2255:
        length = 32;        /* bytes */
        break;
#endif

    default:
        simpleCondition(CB_CRASH, PKE_UNIMP_PKE_CYPHER, CB_ENCRYPTION);
        return CE_OTHER;        /* bail out now if we're still here! */
    }
    bitcount = (uint16_t) (length * 8);
    /* in fact this an overestimate by about 15 bits given the 0,1 start */


    if(!formatSessionKey(keybuffer + 2, length, version,
        algor, keylen, convKey)) return CE_OTHER;

    keybuffer[0] = (byte)(bitcount /256);
    keybuffer[1] = (byte)(bitcount % 256);

    init_mpn(&plaintextkey);

    switch(pk_algor)
    {
#ifndef NO_RSA
    case PKA_RSA:
    case PKA_RSA_ENCRYPT_ONLY:
    case PKA_RSA_SIGN_ONLY:
    case PKA_EBP_RSA:

        get_mpn(&plaintextkey, keybuffer);
        result = rsa_public_operation(&details->cypherKey[0],
        &plaintextkey, pub_key);
        break;
#endif
    case PKA_ELGAMAL:
        {
            /* See Schneier page 478 */
            bignum k;
            bignum ss;            /* shared secret */
            bignum * a = &details->cypherKey[EGCYP_A];
            bignum * b = &details->cypherKey[EGCYP_B];
            long length = length_mpn(pub_key->pkdata.nums[EGPUB_P]) - 1;

            init_mpn(&k); 
            init_mpn(&ss);
            result = CE_OKAY;            /* we hope */
            get_mpn(&plaintextkey, keybuffer);
            if(!randload((short)min(MAXENTROPY,length))) result = CE_USER_BREAK; 
            if(CE_OKAY == result &&
                !longRandom((uint16_t)length, (uint16_t) 0, &k))
                result = CE_NO_MEMORY;
            if(CE_OKAY == result)
                result = mod_power_mpn(a, pub_key->pkdata.nums[EGPUB_G],
                k, pub_key->pkdata.nums[EGPUB_P]);
            if(CE_OKAY == result)
                result = mod_power_mpn(&ss, pub_key->pkdata.nums[EGPUB_Y],
                k, pub_key->pkdata.nums[EGPUB_P]);
            if(CE_OKAY == result && !multiply_mpn(b, plaintextkey, ss)) 
                result = CE_NO_MEMORY;
            if(CE_OKAY == result)
                remainder_mpn(*b, pub_key->pkdata.nums[EGPUB_P], FALSE);
            clear_mpn(&k); 
            clear_mpn(&ss);
            break;
        }

#ifndef NO_PEGWIT8
    case PKA_GF2255:
        /* generate random number for key exchange */
        keybuffer[100] = 0;
        keybuffer[101] = 240;
        for(j=0; j<30; ++j) keybuffer[102+j] = randombyte();
        get_mpn(&plaintextkey, keybuffer+100);

        /* put intermediate (transmission) value into cypherKey[0],
         ** anf munge plaintextkey to be the exchanged value */
        cpEncodeSecret(pub_key->pkdata.nums[ECPUB], &details->cypherKey[0], &plaintextkey);
        bytes = put_mpn(keybuffer+200, &plaintextkey);

        /* XOR secret value with data packet, save details*/
        for(j=0; j<bytes; ++j)
        {
            keybuffer[2+(32-bytes)+j] ^= keybuffer[202+j];
        }
        get_mpn(&details->cypherKey[1], keybuffer);
        result = CE_OKAY;        /* we hope */
        break;
#endif

    default:
        simpleCondition(CB_CRASH, PKE_UNIMP_PKE_CYPHER, CB_ENCRYPTION);
        result = CE_OTHER;
        break;
    }

    memset(keybuffer, 0, MAXBUFFERSIZE);
    clear_mpn(&plaintextkey);
    return result;
}

/* This routine is vestigial for PK algorithms like RSA that are invertible; */
/* but for most PK algorithms this routine is where the PK verification */
/* work is actually done */
boolean verifySignature(sigDetails * sig, byte * digest)
{

    switch(sig->pk_algor)
    {
#ifndef NO_RSA
    case PKA_RSA:
    case PKA_RSA_ENCRYPT_ONLY:
    case PKA_RSA_SIGN_ONLY:
    case PKA_EBP_RSA:
        return (boolean) !memcmp(digest, sig->digest,
        hashDigest(sig->md_algor));
#endif
#ifndef NO_PEGWIT8
    case PKA_GF2255:
        {
            /* have to regenerate what was signed, then use the public key */
            /* for validation that the signature matches */

            byte sigbuffer[32];
            bignum plaintextsig;
            boolean state;

            /* test for simpleminded tampering - the two bytes that were carried */
            /* en clair in the signature packet should at least match those */
            /* we have computed (any *real* adversary woulkd pass this test! */
            if(memcmp(digest, sig->checkBytes, sizeof(sig->checkBytes)))
            {
                simpleCondition(CB_ERROR, PKE_BAD_CHECKSUM, CB_VERIFYING);
                return FALSE;
            }

            sigbuffer[0] = 0;
            sigbuffer[1] = 240;            /**/
            if(!formatSignature(sigbuffer + 2, 30, 3, digest,
            sig->md_algor, FALSE )) return FALSE;
            init_mpn(&plaintextsig);
            get_mpn(&plaintextsig, sigbuffer);            /* This should be what was signed */

            /* Now we can do the public key operation! */
            state = cpVerify(sig->pub_key->pkdata.nums[ECPUB], plaintextsig,
            sig->signature.nums);


            /* Actually, the value of the digest *is* recovered inside cpVerify
             * so we could have done just as is done for RSA - but we'd've had to
             * make this architectural change for El Gamal, for example.  Overall,
             * the tests here are equivalent either way around */

            clear_mpn(&plaintextsig);
            return state;
        }
#endif
    case PKA_DSA:
        {
            byte sigbuffer[MAXHASHSIZE + 2];
            long length;
            uint16_t bitcount;
            boolean result = TRUE;

            bignum w, u1, u2, f1, f2, v, H;
            bignum p = sig->pub_key->pkdata.nums[DSAPUB_P];
            bignum q = sig->pub_key->pkdata.nums[DSAPUB_Q];
            bignum g = sig->pub_key->pkdata.nums[DSAPUB_G];
            bignum y = sig->pub_key->pkdata.nums[DSAPUB_Y];
            bignum r = sig->signature.nums[DSASIG_R];
            bignum s = sig->signature.nums[DSASIG_S];

            init_mpn(&w);
            init_mpn(&u1);
            init_mpn(&u2);
            init_mpn(&f1);
            init_mpn(&f2);
            init_mpn(&v);
            init_mpn(&H);

            length = hashDigest(sig->md_algor);
            bitcount = (uint16_t)(length * 8);
            sigbuffer[0] = (byte)(bitcount /256);
            sigbuffer[1] = (byte)(bitcount % 256);
            memcpy(sigbuffer + 2, digest, (size_t)length);            /* was 20 */
            if(!get_mpn(&H, sigbuffer) ||
                !modularInverse(&w, s, q) ||
                !multiply_mpn(&u1, H, w) ||
                !multiply_mpn(&u2, r, w))
                result = FALSE;
            else
            {
                remainder_mpn(u1, q, FALSE);
                remainder_mpn(u2, q, FALSE);
                if(mod_power_mpn(&f1, g, u1, p) != CE_OKAY ||
                    mod_power_mpn(&f2, y, u2, p) != CE_OKAY ||
                    !multiply_mpn(&v, f1, f2) )
                    result = FALSE;
                else
                {
                    remainder_mpn(v, p, FALSE);
                    remainder_mpn(v, q, FALSE);
                    if(!eq_mpn(v, r)) result = FALSE;
                }
            }

            clear_mpn(&w);
            clear_mpn(&u1);
            clear_mpn(&u2);
            clear_mpn(&f1);
            clear_mpn(&f2);
            clear_mpn(&v);
            clear_mpn(&H);
            return result;
        }

    case PKA_ELGAMAL:
        return FALSE;
    }
    /* default */
    return FALSE;
}

#ifndef NO_RSA
/* This routine is only of use for PK algorithms that are invertible */
static boolean all_ff(byte * buffer, int length)
{
    while(length-- > 0)
        if(buffer[length] != 0xFF) return FALSE;
    return TRUE;
}

/* N.B. The PGP documentation refers to the MSB (first) byte of
 *      the buffer being a zero.  However this is a normalise
 *      MP _number_ has the leading zeros are stripped.
 *      This routine is not strictly RSA-only, but it is for
 *      PKsigning that restores the exact signature
 */
static boolean extractSignature(byte * buffer, short length,
byte signature[MAXHASHSIZE], byte mdalg)
{
    byte siglen = (byte) hashDigest(mdalg);
    byte asnlen;
    byte * asn1_string = getASNstring(mdalg, &asnlen);

    /* PGP 2.2 format - 16 - MD5 digest length in bytes */
    if( buffer[0] == 1 &&
        buffer[17] == 0 &&
        all_ff(buffer + 18, length - 19) &&
        buffer[length - 1] == 1)
    {
        memcpy(signature, buffer + 1, 16);
        return TRUE;
    }
    /* PGP 2.3 format */
    if( buffer[0] == 1 &&
        all_ff(buffer + 1, length - (2 + asnlen + siglen)) &&
        buffer[length - (asnlen + siglen + 1)] == 0 &&
        !memcmp(buffer + (length - (asnlen + siglen)), asn1_string, asnlen))
    {
        memcpy(signature, buffer + length - siglen, siglen);
        return TRUE;
    }
    simpleCondition(CB_ERROR, PKE_BAD_SIG_FORMAT, CB_VERIFYING);
    return FALSE;
}
#endif

#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (disable: 4701)
#endif
boolean getSignature(sigDetails * sig)
/* This routine is only of use for PK algorithms that are invertible */
/* such as RSA.  El Gamal signature and other key-exchange style algorithms */
/* don't allow this; although in fact GF(2^255) does .  For these types of */
/* algorithm, we have simply to return TRUE.  The real work is in */
/* verifySignature, elsewhere in this file in such cases. */
{

    /* It is a weakness of the way this routine is defined that it cannot read in
     ** the signing key as it does not have access to the key-ring in use.  All it
     ** can do is check if it has been given the right key and that the key has
     ** already been completely read.
     ** (Strictly this is no longer necessary as public key now contain a reference to
     **  the key-ring, so this could now be changed) */
    if(!sig->pub_key || sig->pub_key->status != KS_COMPLETE ||
        memcmp(sig->pub_key->keyId, sig->keyId, KEYFRAGSIZE)) return FALSE;

    /* Actually, the value of the digest *is* recovered inside cpVerify
     ** so we could have done just as is done for RSA - but we'd've had to
     ** make this architectural change for El Gamal, for example.  Overall,
     ** the tests here are equivalent either way around */
    if(PKA_RSA != sig->pk_algor &&
        PKA_RSA_ENCRYPT_ONLY != sig->pk_algor &&
        PKA_RSA_SIGN_ONLY != sig->pk_algor &&
        PKA_EBP_RSA != sig->pk_algor) return TRUE;

#ifndef NO_RSA
    {
        short bytes;
        common_error result;
        byte sigbuffer[MAXBUFFERSIZE+2];

        /* RSA Decrypt the signature */
        bignum plaintextsig;
        init_mpn(&plaintextsig);
        result = rsa_public_operation(&plaintextsig, &sig->signature.nums[RSASIG],
        sig->pub_key);

        /* Extract the decrypted hash value */
        if(result == CE_OKAY) bytes = put_mpn(sigbuffer, &plaintextsig);
        clear_mpn(&plaintextsig);
        switch(result)
        {
        case CE_NO_MEMORY:
            simpleCondition(CB_ERROR, PKE_NO_MEMORY, CB_VERIFYING);
            break;

        case CE_USER_BREAK:
            simpleCondition(CB_ERROR, PKE_USER_BREAK, CB_VERIFYING);
            break;

        case CE_OKAY:
            result = CE_OTHER;
            if(bytes <= hashDigest(sig->md_algor) + 6)
            {
                simpleCondition(CB_ERROR, PKE_KEY_TOO_SHORT, CB_VERIFYING);
                break;
            }
            if(!extractSignature(sigbuffer + 2, bytes, sig->digest, sig->md_algor)) break;
            if(memcmp(sig->digest, sig->checkBytes, sizeof(sig->checkBytes)))
            {
                simpleCondition(CB_ERROR, PKE_BAD_CHECKSUM, CB_VERIFYING);
                break;
            }
            result = CE_OKAY;
            break;

        default:
            simpleCondition(CB_CRASH, PKE_BAD_RETURN_CODE, CB_VERIFYING);
        }
        if(result != CE_OKAY) sig->version = 0;
        return (boolean)(result == CE_OKAY);
    }
#else
    simpleCondition(CB_CRASH, PKE_UNIMP_PKE_CYPHER, CB_VERIFYONG);
    return FALSE;
#endif
}
#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (default: 4706)
#endif

boolean putSignature(sigDetails * sig, seckey * sec_key)
{
    common_error result;
    bignum plaintextsig;
    byte sigbuffer[MAXBUFFERSIZE+2];
    short length;
    uint16_t bitcount;
    boolean useASN;

    if(sec_key->skstat != INTERNAL) return FALSE;    /* key still encrypted */

    if(sig->md_algor != MDA_MD5) sig->version = 3;
    if(sec_key->publicKey->pkalg != PKA_RSA) sig->version = 3;

    switch(sec_key->publicKey->pkalg)
    {
#ifndef NO_RSA
    case PKA_RSA:
    case PKA_RSA_ENCRYPT_ONLY:
    case PKA_RSA_SIGN_ONLY:
    case PKA_EBP_RSA:
        length = size_mpn(sec_key->publicKey->pkdata.nums[RSAPUB_N]);
        useASN = TRUE;
        break;
#endif
#ifndef NO_PEGWIT8
    case PKA_GF2255:
        length = 30;        /* bytes */
        useASN = FALSE;
        break;
#endif
    default:
        length = 0;
        useASN = FALSE;
    }
    bitcount = (uint16_t)(length * 8);
    /* in fact this an overestimate by about 15 bits given the 0,1 start */

    /* Note the initialisation and release of the MPN is all done within
     ** the routine to make sure it is done.  Using an uninitialised MPN
     ** can cause program crashs. */
    if(!formatSignature(sigbuffer + 2, length, sig->version, sig->digest,
    sig->md_algor, useASN )) return FALSE;
    sigbuffer[0] = (byte)(bitcount /256);
    sigbuffer[1] = (byte)(bitcount % 256);

    init_mpn(&plaintextsig);
    get_mpn(&plaintextsig, sigbuffer);

    switch(sec_key->publicKey->pkalg)
    {
#ifndef NO_RSA
    case PKA_RSA:
    case PKA_RSA_ENCRYPT_ONLY:
    case PKA_RSA_SIGN_ONLY:
    case PKA_EBP_RSA:

        init_mpn(&sig->signature.nums[RSASIG]);
        result = rsa_private_operation(&sig->signature.nums[RSASIG],
        &plaintextsig, sec_key);
        break;
#endif
#ifndef NO_PEGWIT8
    case PKA_GF2255:
        init_mpn(&sig->signature.nums[ECSIG_R]);
        init_mpn(&sig->signature.nums[ECSIG_S]);
        do
        {
            int j;
            byte buffer[32];
            bignum k;

            buffer[0] = 0;
            buffer[1] = 240;
            for(j=2; j<32; ++j) buffer[j] = randombyte();

            init_mpn(&k);
            get_mpn(&k, buffer);

            cpSign(sec_key->pkdata.nums[ECSEC].plain, k, plaintextsig,
            sig->signature.nums);
            clear_mpn(&k);
        }
        while (0 == length_mpn(sig->signature.nums[ECSIG_R]));
        result = CE_OKAY;        /* we hope */
        break;
#endif

    default:
        simpleCondition(CB_CRASH, PKE_UNIMP_PKE_CYPHER, CB_SIGNING);
        result = CE_OTHER;
    }
    clear_mpn(&plaintextsig);

    switch(result)
    {
    case CE_OKAY:
        return TRUE;

    case CE_NO_MEMORY:
        simpleCondition(CB_ERROR, PKE_NO_MEMORY, CB_SIGNING);
        break;

    case CE_USER_BREAK:
        simpleCondition(CB_ERROR, PKE_USER_BREAK, CB_SIGNING);
        break;

    default:
        simpleCondition(CB_CRASH, PKE_BAD_RETURN_CODE, CB_SIGNING);
    }
    release_signature(sig);
    return (boolean)(result == CE_OKAY);
}


boolean newPKAkey(keyType * keyType, seckey * sec_key, pubkey * pub_key)
{
    /* no passphrase yet, so no key protection algorithm */
    memset((char*)&sec_key->kpalg, 0, sizeof(sec_key->kpalg));

    /* nor any passphrase-to-key hashing algorithm! (default here)*/
    sec_key->hashalg=MDA_MD5;

    switch (keyType->algorithm)
    {
#ifndef NO_RSA
    case PKA_RSA:
    case PKA_RSA_ENCRYPT_ONLY:
    case PKA_RSA_SIGN_ONLY:
    case PKA_EBP_RSA:
        return newRSAkey(keyType, sec_key, pub_key);
#endif
#ifndef NO_PEGWIT8
    case PKA_GF2255:
        return newGF2255key(sec_key, pub_key);
#endif
    default:
        return FALSE;
    }
}


boolean verifySecKey(seckey * sec_key)
{
    bignum modulus;
    boolean result = FALSE;

    init_mpn(&modulus);
    switch(sec_key->publicKey->pkalg)
    {
#ifndef NO_RSA
    case PKA_RSA:
    case PKA_RSA_ENCRYPT_ONLY:
    case PKA_RSA_SIGN_ONLY:
    case PKA_EBP_RSA:
        /* Checksum okay however there is a one in 64k chance of this with
         ** the wrong pass-phrase => check we have the factors of N. */
        result = verifySecKeyRSA(sec_key);
        break;
#endif
#ifndef NO_PEGWIT8
    case PKA_GF2255:        /* similarly regenerate and check the secret key */
        cpMakePublicKey(&modulus, sec_key->pkdata.nums[ECSEC].plain);
        if(eq_mpn(modulus, sec_key->publicKey->pkdata.nums[ECPUB]))
            result = TRUE;
        break;
#endif
    default:
        result = TRUE;        /* If we don't know how to verify it; assume it is okay */
    }
    clear_mpn(&modulus);
    return result;
}

/* end of file pkops.c */

