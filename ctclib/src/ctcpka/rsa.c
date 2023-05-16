/* rsa.c
 **
 **  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 **  Heimdall <heimdall@bifroest.demon.co.uk>  1996
 **  All rights reserved.  For full licence details see file licences.c
 */

/*
 *  This file encapsulates the pure RSA operations
 */
#ifndef NO_RSA

#include "random.h"
#include "pkops.h"
#include "rsa.h"
#include "bignums.h"
#include "pkbignum.h"
#include "callback.h"
#include "pkcipher.h"
#include "keyhash.h"

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

/*----------------------RSA Algorithm interface ----------------------*/
common_error rsa_public_operation(bignump outbuf, bignump inbuf, pubkey * pub_key)
/* Execute a public key operation (verification or encryption)   */
{
    return mod_power_mpn(outbuf, *inbuf, pub_key->pkdata.nums[RSAPUB_E], pub_key->pkdata.nums[RSAPUB_N]);
}

common_error rsa_private_operation(bignump output, bignump input, seckey * sec_key)
/* Execute a private key operation (signing or decryption)     */
{
    bignum expon, base, temp, OP;
    common_error result = CE_OKAY;

    if(sec_key->skstat != INTERNAL) return CE_OTHER;    /* fail hard as key still encrypted */

    init_mpn(&expon);
    init_mpn(&base);
    init_mpn(&temp);
    init_mpn(&OP);

    if(!copy_mpn(&expon, sec_key->pkdata.nums[RSASEC_D].plain)) result = CE_NO_MEMORY;
    if(CE_OKAY == result)
    {
        remainder_mpn(expon, sec_key->pkdata.nums[RSASEC_P].plain, TRUE);
        if(!copy_mpn(&base, *input)) result = CE_NO_MEMORY;
    }
    if(CE_OKAY == result)
    {
        remainder_mpn(base, sec_key->pkdata.nums[RSASEC_P].plain, FALSE);
        result = mod_power_mpn(&OP, base, expon, sec_key->pkdata.nums[RSASEC_P].plain);
    }

    if(CE_OKAY == result)
    {
        if(!copy_mpn(&expon, sec_key->pkdata.nums[RSASEC_D].plain)) result = CE_NO_MEMORY;
    }

    if(CE_OKAY == result)
    {
        remainder_mpn(expon, sec_key->pkdata.nums[RSASEC_Q].plain, TRUE);
        if(!copy_mpn(&base, *input)) result = CE_NO_MEMORY;
    }
    if(CE_OKAY == result)
    {
        remainder_mpn(base, sec_key->pkdata.nums[RSASEC_Q].plain, FALSE);
        result = mod_power_mpn(output, base, expon, sec_key->pkdata.nums[RSASEC_Q].plain);
    }
    wipe_mpn(&base);
    wipe_mpn(&expon);
    /* The next addition is to ensure that the result of the following
     ** subtraction is positive.  (The routines in use do not handle
     ** negative numbers.)  The subsequent remainder operation means it
     ** has no effect on the ultimate result. */
    if(CE_OKAY == result)
    {
        if(!add_mpn(output, sec_key->pkdata.nums[RSASEC_Q].plain)) result = CE_NO_MEMORY;
    }
    if(CE_OKAY == result)
    {
        subtract_mpn(*output, OP);
        if(!multiply_mpn(&temp, *output, sec_key->pkdata.nums[RSASEC_U].plain)) result = CE_NO_MEMORY;
    }
    if(CE_OKAY == result)
    {
        remainder_mpn(temp, sec_key->pkdata.nums[RSASEC_Q].plain, FALSE);
        if(!multiply_mpn(output, temp, sec_key->pkdata.nums[RSASEC_P].plain)) result = CE_NO_MEMORY;
    }
    if(CE_OKAY == result)
    {
        if(!add_mpn(output, OP)) result = CE_NO_MEMORY;
    }
    wipe_mpn(&temp);
    wipe_mpn(&OP);
    return result;
}

boolean newRSAkey(keyType * keyType, seckey * sec_key, pubkey * pub_key)
{
    uint16_t topOfP, topOfQ;
    boolean oddLength = (boolean)((keyType->keyLength % 2) == 1);
    uint16_t primeLength = (uint16_t)((keyType->keyLength + 1) /2);
    common_error status;
    bignum p,q, phi, one;
    uint32_t modulus;

    randload((short)(keyType->keyLength + 200));
    simpleCondition(CB_STATUS, PKE_KEYGEN_SETUP, CB_KEYGEN);
    do
    {
        uint16_t A = (uint16_t)(randomShort() | 0x8000);
        uint16_t B = (uint16_t)(randomShort() | 0x8000);

        topOfP = min(A,B);
        topOfQ = max(A,B);
        modulus = (uint32_t)topOfP * (uint32_t)topOfQ;
        /* check values okay */
    }
    while( topOfQ < topOfP + 16 ||     /* 1) sufficiently different */
    topOfQ == 0xFFFF ||     /* 2) topOfQ not in danger of overflowing */
    /* 3a) if odd that multiplication will loose a bit in length */
    ( oddLength && modulus >= (uint32_t)0x7FFFFFFFL) ||
    /* 3b) if even that multiplication will not loose a bit in length */
    (!oddLength && modulus < (uint32_t)0x80000000L));
    init_mpn(&p);
    init_mpn(&q);
    init_mpn(&pub_key->pkdata.nums[RSAPUB_N]);
    init_mpn(&pub_key->pkdata.nums[RSAPUB_E]);
    init_mpn(&sec_key->pkdata.nums[RSASEC_D].plain);
    init_mpn(&sec_key->pkdata.nums[RSASEC_U].plain);
    init_mpn(&phi);
    init_mpn(&one);

    /* note we are forcing the public exponent to be odd not testing for this. */
    set_mpn(keyType->publicExponent | 1, &pub_key->pkdata.nums[RSAPUB_E]);
    simpleCondition(CB_STATUS, PKE_KEYGEN_1STPRIME, CB_KEYGEN);
    status = findPrime(&p, primeLength, topOfP, keyType->method, pub_key->pkdata.nums[RSAPUB_E]);
    if(status == CE_OKAY)
    {
        simpleCondition(CB_STATUS, PKE_KEYGEN_2NDPRIME, CB_KEYGEN);
        status = findPrime(&q, primeLength, topOfQ, keyType->method, pub_key->pkdata.nums[RSAPUB_E]);
    }
    if(status == CE_OKAY)
    {
        simpleCondition(CB_STATUS, PKE_KEYGEN_FINISHING, CB_KEYGEN);
        if( !multiply_mpn(&pub_key->pkdata.nums[RSAPUB_N], p, q) ||
            !copy_mpn(&phi, pub_key->pkdata.nums[RSAPUB_N]) ||
            !set_mpn(1L, &one) ) status = CE_NO_MEMORY;
    }
    pub_key->size = length_mpn(pub_key->pkdata.nums[RSAPUB_N]);
    if(status == CE_OKAY)
    {
        subtract_mpn(phi, p);
        subtract_mpn(phi, q);
        add_mpn(&phi, one);
        if( !modularInverse(&sec_key->pkdata.nums[RSASEC_D].plain, pub_key->pkdata.nums[RSAPUB_E], phi) ||
            !modularInverse(&sec_key->pkdata.nums[RSASEC_U].plain, p, q)) status = CE_NO_MEMORY;
    }
    clear_mpn(&phi);
    clear_mpn(&one);
    if(status == CE_OKAY)
    {
        sec_key->pkdata.nums[RSASEC_P].plain = p;
        sec_key->pkdata.nums[RSASEC_Q].plain = q;

    }
    else
    {
        clear_mpn(&p);
        clear_mpn(&q);
        clear_mpn(&pub_key->pkdata.nums[RSAPUB_N]);
        clear_mpn(&pub_key->pkdata.nums[RSAPUB_E]);
        clear_mpn(&sec_key->pkdata.nums[RSASEC_D].plain);
        clear_mpn(&sec_key->pkdata.nums[RSASEC_U].plain);
    }
    switch(status)
    {
    case CE_OKAY:
        break;
    case CE_NO_MEMORY:
        simpleCondition(CB_ERROR, PKE_NO_MEMORY, CB_KEYGEN);
        break;
    case CE_USER_BREAK:
        simpleCondition(CB_WARNING, PKE_USER_BREAK, CB_KEYGEN);
        break;
    case CE_OTHER:
        simpleCondition(CB_ERROR, PKE_FILE_ERROR, CB_KEYGEN);
        break;
    default:
        simpleCondition(CB_CRASH, PKE_BAD_RETURN_CODE, CB_KEYGEN);
        break;
    }
    return (boolean)(status == CE_OKAY);
}


boolean verifySecKeyRSA(seckey * sec_key)
{
    bignum modulus;
    boolean result;

    init_mpn(&modulus);
    /* Checksum okay however there is a one in 64k chance of this with
     ** the wrong pass-phrase => check we have the factors of N. */
    result = (boolean)
        (multiply_mpn(&modulus, sec_key->pkdata.nums[RSASEC_P].plain, sec_key->pkdata.nums[RSASEC_Q].plain) &&
            eq_mpn(modulus, sec_key->publicKey->pkdata.nums[RSAPUB_N]));
    clear_mpn(&modulus);
    return result;
}

#if defined (RSA_NONDLL_TEST)
/* RSA test routine */
int mutualFactor(pubkey * left, pubkey * right, char buffer[1000])
{
    bignum commonFactor;
    bignum one;
    int result = 0;

    init_mpn(&commonFactor);
    init_mpn(&one);
    set_mpn(1L, &one);
    if(!completeKey(right) || !completeKey(left))
    {
        strcpy(buffer, "failed to complete key");
        result = -1;
    }
    else if(!HCF(&commonFactor, left->pkdata.nums[RSAPUB_N], right->pkdata.nums[RSAPUB_N]))
    {
        strcpy(buffer, "HCF() failed.");
        result = -1;
    }
    else if(!eq_mpn(commonFactor, one))
    {
        format_mpn(buffer, commonFactor);
        result = 1;
    }
    clear_mpn(&commonFactor);
    clear_mpn(&one);
    return result;
}
#endif /* def RSA_NONDLL_TEST */
#endif /* NO_RSA */
/* end of file rsa.c */

