/* pkbignum.c
 **
 **  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 **  Heimdall <heimdall@bifroest.demon.co.uk>  1996
 **  All rights reserved.  For full licence details see file licences.c
 */

/*
 *  This file contains the bignum operations associated with public
 *  key generation and such, including primality, and random generation
 */

#include "random.h"
#include "bignums.h"
#include "usrbreak.h"
#include "utils.h"
#include "pkbignum.h"

#define FERMATLIMIT 10

byte mixedRandom(void)
{
    byte result = 0;

    while(result == 0 || result == 0xFF) result = randombyte();
    return result;
}

uint16_t randomShort(void)
{
    return (uint16_t)(mixedRandom() * 256 + mixedRandom());
}

static int mutuallyPrime(bignum left, bignum right, boolean * success)
{
    bignum hcf, one;
    int result;

    init_mpn(&hcf);
    init_mpn(&one);
    if(!HCF(&hcf, left, right) || !read_mpn("1", &one))
    {
        *success = FALSE;
        result = TRUE;
    }
    else if(gt_mpn(hcf, one))
        result = FALSE;
    else
        result = TRUE;
    clear_mpn(&hcf);
    clear_mpn(&one);
    return result;
}

int modularInverse(bignump result, bignum input, bignum modulus)
{
    bignum remainder, target, factor, try, temp, one;
    boolean success = FALSE;

    init_mpn(&one);
    init_mpn(&remainder);
    init_mpn(&target);
    init_mpn(&factor);
    init_mpn(&try);
    init_mpn(&temp);
    if( read_mpn("1", &one) &&
        copy_mpn(result, one) &&
        copy_mpn(&remainder, input) &&
        copy_mpn(&target, modulus) &&
        copy_mpn(&factor, one) &&
        copy_mpn(&try, modulus))
    {
        success = TRUE;
        do
        {
            do
            {
                if( !divide_mpn(&factor, NULL, target, remainder) ||
                    !add_mpn(&factor, one) ||
                    !multiply_mpn(&try, factor, remainder)) success = FALSE;
                remainder_mpn(try, modulus, FALSE);
                if( !add_mpn(&target, modulus)) success = FALSE;
                if(user_break()) success = FALSE;
            }
            while (success && (!gt_mpn(remainder, try) ||
                !mutuallyPrime(try, modulus, &success)));
            if( !multiply_mpn(&temp, factor, *result)) success = FALSE;
            remainder_mpn(temp, modulus, FALSE);
            if( !copy_mpn(result, temp) ||
                !copy_mpn(&remainder, try) ||
                !copy_mpn(&target, modulus)) success = FALSE;
        }
        while(success && gt_mpn(remainder, one));
    }
    clear_mpn(&remainder);
    clear_mpn(&target);
    clear_mpn(&factor);
    clear_mpn(&try);
    clear_mpn(&one);
    clear_mpn(&temp);
    return success;
}

boolean longRandom(uint16_t length, uint16_t topBits, bignump result)
{
    uint16_t Nbytes = (uint16_t)((length + 7) /8 + 2);    /* bytes including length */
    uint16_t bits = (uint16_t) ((length - 1) % 8);    /* one less number of bits in top byte */
    byte mask = (byte)(0xFF >> (7 - bits));    /* mask for random data in third data byte */
    short offset = 4;
    byte * buffer;
    boolean ok;

    if(topBits == 0) topBits = (uint16_t) (0x8000 | randomShort());    /* treat topBits==0 as "don't care" */
    if(!(topBits & 0x8000) || length < 24) return FALSE;
    buffer = (byte *)qmalloc(Nbytes);
    if(!buffer) return FALSE;
    buffer[0] = (byte)(length /256);
    buffer[1] = (byte)(length % 256);
    buffer[2] = (byte)(topBits >> (15 - bits));
    buffer[3] = (byte)(( topBits >> (7 - bits)) & 0xFF);
    buffer[4] = (byte)(((topBits << (bits + 1)) & 0xFF) | (randombyte() & mask));
    while(offset++ < Nbytes - 1) { 
        buffer[offset] = randombyte(); 
    }

    ok = get_mpn(result, buffer);
    qfree(buffer);
    return ok;
}

static boolean are_primes(bignum candidates[], common_error * errCode)
{
    uint16_t offset = 0;
    boolean answer = TRUE;
    bignum result = NULL;
    bignum factor = NULL; 
    bignump number = candidates;
    uint32_t primes[FERMATLIMIT] = { 
        2, 3, 5, 7, 11, 13, 17, 19, 23, 29         };

    while(*number) { 
        if(!sieve(*number++)) return FALSE; 
    }
    while((*errCode == CE_OKAY) && answer && offset < FERMATLIMIT)
    {
        if(!set_mpn(primes[offset], &factor)) *errCode = CE_NO_MEMORY;
        number = candidates;
        while((*errCode == CE_OKAY) && answer && *number)
        {
            /* Test p for primality by applying Fermat's theorem:
             * For any x, if ((x**(p-1)) mod p) != 1, then p is not prime.
             * We actually test (x**p) mod p against x
             */
            *errCode = mod_power_mpn(&result, factor, *number, *number);
            if(!eq_mpn(result, factor)) answer = FALSE;
            number++;
        }
        offset++;
    }
    clear_mpn(&result);
    clear_mpn(&factor);
    return answer;
}

/* Strictly finds 2N+1 where N is a Sophie Germain prime */
static common_error next_Sophie_Germain_prime(bignump SGprime, uint32_t increment_requested)
{
    uint32_t increment = increment_requested;
    bignum candidates[3] = { 
        NULL, NULL, NULL         };
    bignump number = &candidates[0];
    bignump half = &candidates[1];
    /*       candidates[2] is a null terminator */
    bignum temp = NULL;
    bignum incr = NULL;
    bignum halfincr = NULL;
    bignum one = NULL;
    common_error errCode = CE_OKAY;

    /* As the result of this routine has to equal 11 modulo 12,
     ** the initial set-up establishes
     ** starting values with the following properties:-
     ** number = 11 [mod 12]
     ** half = (number - 1)/2 
     ** increment = 0 [mod 12]
     ** halfincr  = increment / 2
     ** number & increment are mutually prime
     ** half & halfincr are mutually prime
     ** */

    /* first set up number to be 11 modulo 12 by adding the necessary
     ** smaller integer and set up a few simple values */
    if( !copy_mpn(number, *SGprime) ||
        !set_mpn((uint32_t)(11 - mod_short(*number, 12)), &temp) ||
        !add_mpn(number, temp) ||
        !set_mpn(1, &one) ||
        !set_mpn(2, &temp) ||
        !divide_mpn(half, NULL, *number, temp))
    {
        clear_mpn(&temp); 
        clear_mpn(&one); 
        clear_mpn(half); 
        return CE_NO_MEMORY;
    }

    /* adjust increment upwards to be the next multiple of 12 */
    if(increment % 12 != 0) 
        increment = ((increment /12) + 1) * 12;

    /* Predecrement increment as we increment it at the start of the loop;
     ** this is probably unimportant in most cases, but just in case the caller
     ** had a particular reason for wanting the particular increment value
     ** provided.  */
    increment -= 12;

    /* This nested loop trys potential values of increment until one meeting
     ** all criteria is encountered. */
    do
    {
        do
        {
            increment += 12;
            if(increment < 12) errCode = CE_OTHER;            /* Overflow check */
            if( !set_mpn(increment, &incr) ||
                !HCF(&temp, incr, *number) ) errCode = CE_NO_MEMORY;
        }
        while((errCode == CE_OKAY) && gt_mpn(temp, one));
        if( !set_mpn(increment /2, &halfincr) ||
            !HCF(&temp, halfincr, *half)) errCode = CE_NO_MEMORY;
    }
    while((errCode == CE_OKAY) && gt_mpn(temp, one));
    clear_mpn(&one); 
    clear_mpn(&temp);

    /* ready to search for candidates */
    while((errCode == CE_OKAY) && !are_primes(candidates, &errCode))
    {
        if( user_break())
            errCode = CE_USER_BREAK;
        else if(!add_mpn(number, incr) || !add_mpn(half, halfincr)) 
            errCode = CE_NO_MEMORY;
    }
    clear_mpn(half); 
    clear_mpn(&halfincr); 
    clear_mpn(&incr);

    if(errCode == CE_OKAY)
    {
        clear_mpn(SGprime);
        *SGprime = *number;
    }
    else
        clear_mpn(number);
    return errCode;
}

static common_error next_prime(bignump prime, uint32_t increment_requested)
{
    uint32_t increment = increment_requested;
    bignum candidates[2] = {
        NULL, NULL         };
    bignump number = &candidates[0];
    /*       candidates[1] is a null terminator */
    bignum temp = NULL;
    bignum incr = NULL;
    bignum one = NULL;
    common_error errCode = CE_OKAY;

    /* The initial set-up that follows establishes
     ** starting values with the following properties:-
     ** number is odd
     ** increment is even
     ** number & increment are mutually prime
     ** */

    /* first set up number to be odd by adding one if necessary */
    if(!copy_mpn(number, *prime) || !set_mpn(1, &one)) return CE_NO_MEMORY;
    if(mod_short(*number, 2) != 1) add_mpn(number, one);

    /* adjust increment upwards to be the next multiple of 2 */
    if(increment % 2 != 0) increment++;

    /* Predecrement increment as we increment it at the start of the loop;
     ** this is probably unimportant in most cases, but just in case the caller
     ** had a particular reason for wanting the particular increment value
     ** provided.  */
    increment -= 2;

    /* This nested loop trys potential values of increment until one meeting
     ** all criteria is encountered. */
    do
    {
        increment += 2;
        if(increment < 2) errCode = CE_OTHER;        /* Overflow check */
        if( !set_mpn(increment, &incr) ||
            !HCF(&temp, incr, *number) ) errCode = CE_NO_MEMORY;
    }
    while((errCode == CE_OKAY) && gt_mpn(temp, one));
    clear_mpn(&one); 
    clear_mpn(&temp);

    /* ready to search for candidates */
    while((errCode == CE_OKAY) && !are_primes(candidates, &errCode))
    {
        if( user_break())
            errCode = CE_USER_BREAK;
        else if(!add_mpn(number, incr)) 
            errCode = CE_NO_MEMORY;
    }
    clear_mpn(&incr);

    if(errCode == CE_OKAY)
    {
        clear_mpn(prime);
        *prime = *number;
    }
    else
        clear_mpn(number);
    return errCode;
}

static int mutuallyPrimeM1(bignum left, bignum right, boolean * success)
{
    int result;

    set0thBit_mpn(left, FALSE);
    result = mutuallyPrime(left, right, success);
    set0thBit_mpn(left, TRUE);
    return result;
}

/* find a prime of length 'length' with most significant bits defined by topBits using method 'method' where
 * N-1 is mutually prime with 'mutual' */
common_error findPrime(bignump prime, uint16_t length, uint16_t topBits,
prime_method method, bignum mutual)
{
    common_error result = CE_NO_MEMORY;
    uint32_t increment;
    bignum two;
    boolean success = TRUE;

    if(randload((short)(length + 32)) < 0) return CE_USER_BREAK;

    increment = randombyte() + 
        (randombyte() * 0x100L) +
        (randombyte() * 0x10000L) +
        ((randombyte() & 0x7F) * 0x1000000L);
    if(randload(length) && 
        longRandom(length, topBits, prime))
    {
        switch(method)
        {
        case SIMPLE_SCAN:
            increment = 1;
            /*deliberate drop through to next case */
        case JUMP_SCAN:
            init_mpn(&two);
            set_mpn(2, &two);
            if(success)
            {
                do 
                {
                    do
                    {
                        add_mpn(prime, two);
                    }
                    while(!mutuallyPrimeM1(*prime, mutual, &success));
                    result = next_prime(prime, increment);
                }
                while(result == CE_OKAY && !mutuallyPrimeM1(*prime, mutual, &success));
            }
            clear_mpn(&two);
            if(!success) result = CE_NO_MEMORY;
            break;

        case SOPHIE_GERMAIN:
            result = next_Sophie_Germain_prime(prime,increment);
            break;

        default:
            result = CE_OTHER;
        }
    }
    return result;
}

/* end of file pkbignum.c */

