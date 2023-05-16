/* bignums.c
 **
 ** Multiple precision unsigned integer arithmetic
 **
 **  Coded & copyright Heimdall <heimdall@bifroest.demon.co.uk>  1996
 **  All rights reserved.  For full licence details see file licences.c
 **
 **  Elliptic curve encryption support routines coded and copyright
 **  Mr. Tines <tines@windsong.demon.co.uk> 1997
 */

#include <ctype.h>
#include <string.h>
#include "bignums.h"
#include "usrbreak.h"
#include "utils.h"
#if defined(THINKC) || defined(SYMANTEC_C)
#define assert(okay) { if(!(okay)) Debugger(); }
#else
#include <assert.h>
#endif

#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (disable: 4127) // conditional expression is constant
#pragma warning (disable: 4244) // conversion from int to short/uint16_t
#endif


typedef mp_number * mp_numptr;
typedef unsigned long unit;
#ifdef __alpha
typedef unsigned int halfunit;
#else
typedef unsigned short halfunit;
#endif
#define MAXUNIT (~(unit)0)
#define UNITBITS (8 * sizeof(unit))
#define HALFBITS (8 * sizeof(halfunit))
#define MAXHALF ((1L<<HALFBITS)-1)

#if defined(THINK_C) && !defined(_POWER_PC)
#define multUnitxUnit(high, low, op1, op2) \
{register uint32_t temp = (op1);\
 low = (op2);\
 asm 68030 {mulu.l temp,high:low}\
}
#define divUnitByHalf(high, low, divisor, quotient, remainder) \
{remainder = high;\
 quotient = low;\
 asm 68030 {divu.l divisor,remainder:quotient} \
}
#endif

/* The following standard C fallback macros are derived from
 ** Free Software Foundation, Inc. GNU MP Library  */
#ifndef multUnitxUnit
#define multUnitxUnit(high, low, op1, op2)  \
{\
 halfunit L1 = (halfunit)((op1) & MAXHALF);\
 halfunit L2 = (halfunit)((op2) & MAXHALF);\
 halfunit H1 = (halfunit)((op1) >> HALFBITS);\
 halfunit H2 = (halfunit)((op2) >> HALFBITS);\
 unit P1 = (unit)L1 * (unit)L2;\
 unit P2A = (unit)H1 * (unit)L2;\
 unit P2B = (unit)L1 * (unit)H2;\
 unit P2 = (unit)P2A + (unit)P2B;\
 unit P3 = (unit)H1 * (unit)H2;\
 if(P2 < P2A) P3 += (1L<<HALFBITS);\
 (low) = P1 + ((P2 & MAXHALF) << HALFBITS);\
 if((low) < P1) P3++; \
 (high) = P3 + (P2 >> HALFBITS);  \
}
#endif
#ifndef divUnitByHalf
#define divUnitByHalf(high, low, divisor, quotient,  remainder) \
{unit DH = (divisor) >> HALFBITS;\
 unit DL = (divisor) & MAXHALF;\
 unit RH = (high) % DH;\
 unit RL;\
 unit QH = (high) /DH;\
 unit QL;\
 unit M = QH * DL;\
 \
 RH = RH * (1L<<HALFBITS) + ((low) >> HALFBITS);\
 if(RH < M) \
 {\
  QH--;\
  RH += (divisor);\
  if(RH >= (divisor)) \
  {\
   if (RH < M) \
   {\
    QH--;\
    RH += (divisor);\
   }\
  }\
 }\
 RH -= M;\
 RL = RH % DH;\
 QL = RH /DH;\
 M = QL * DL;\
 RL = RL * (1L<<HALFBITS) | ((low) & MAXHALF);\
 if(RL < M) \
 {\
  QL--;\
  RL += (divisor);\
  if(RL >= (divisor)) \
  {\
    if(RL < M)\
    {\
     QL--;\
     RL += (divisor);\
    }\
  }\
 }\
 RL -= M;\
 (quotient) = QH * (1L<<HALFBITS) | QL;\
 (remainder) = RL;\
}
#endif

/* #define MPADEBUG
 *    #define DETAIL1 */
#ifdef MPADEBUG
#include <stdio.h>
#endif 

struct mp_number_T
{
    uint16_t alloc;
    uint16_t used;
    unit digit[1];
};
#define BYTESNEEDED(x) ((x) * sizeof(unit) + 2 * sizeof(uint16_t))

typedef struct
{
    uint16_t length;
    uint16_t shift;
    unit topUnit;
}
modulusCache;


static uint32_t stats[20];
#ifdef MPADEBUG
static FILE * file; 
#endif

/* Schneier recommends sieving with primes up to 2000; there are just over
 ** 300 primes under 2000  */
#define NPRIMES 300
static unsigned short primes[NPRIMES] = {
    0 };

/* The following function calculates the first NPRIMES prime number for
 ** use in the initial prime number sieve screening of candidate primes. */
static void calculatePrimes(void)
{
    unsigned short square[NPRIMES];
    unsigned short candidate = 5;
    unsigned short index = 1;
    unsigned short found = 2;

    primes[0] = 2;
    square[0] = 2 * 2;
    primes[1] = 3;
    square[1] = 3 * 3;
    while(found < NPRIMES)
    {
        while(candidate >= square[index])
        {
            if(candidate % primes[index++] == 0)
            {
                candidate += (unsigned short)2;                /* reject candidate -> next candidate */
                index = 1;
            }
        }
        primes[found] = candidate;        /* accept candidate */
        square[found++] = (unsigned short)(candidate * candidate);
        candidate += (unsigned short)2;
        index = 1;
    }
}


uint16_t mod_short(mp_numptr number, uint16_t modulus)
{
    unit accum = 0;
    unit factor = ((MAXUNIT - (unit)modulus) + 1) % modulus;
    short offset = number->used;

    while(offset-- > 0)
    {
        accum = (accum * factor) % modulus + (number->digit[offset] % modulus);
    }
    return (uint16_t)(accum % modulus);
}


boolean HCF(bignump result, bignum left, bignum right)
{
    bignum temp_left, temp_right;
    bignump swap, large, small;
    boolean success = FALSE;    /* Initially assume failure */

    init_mpn(&temp_left);
    init_mpn(&temp_right);
    if(copy_mpn(&temp_left, left) && copy_mpn(&temp_right, right))
    {
        if(gt_mpn(left, right))
        {
            large = &temp_left;
            small = &temp_right;
        }
        else
        {
            small = &temp_left;
            large = &temp_right;
        }

        while((*small)->used > 0)
        {
            remainder_mpn(*large, *small, FALSE);
            swap = large;
            large = small;
            small = swap;
        }
        success = copy_mpn(result, *large);        /* final potential failure */
    }
    clear_mpn(&temp_left);
    clear_mpn(&temp_right);
    return success;
}

/* returns TRUE if the argument could be a prime */
boolean sieve(bignum number)
{
    uint16_t offset = 0;

    if(primes[0] == 0) calculatePrimes();    /* executed first time only */
    while(offset < NPRIMES)
    {
        if(mod_short(number, primes[offset++]) == 0)
        {
            /* candidate is a multiple of a known small prime number;
             ** to be properly general we test if the candidate IS that prime number */
            return (boolean)(number->used == 1 &&
                (unsigned short) number->digit[0] == primes[--offset]);
        }
    }
    return TRUE;
}


static void normalise(mp_numptr number)
{
    while(number->used > 0 &&
        number->digit[number->used - 1] == 0)
        number->used--;
}

static const char * hexDigit = "0123456789ABCDEF";
static char * formatNumber(char * output, unit number[], uint16_t length)
{
    char * ptr = output + length * sizeof(unit) * 2;
    short index = -1;
    short count;
    unit value;

    *ptr-- = '\0';
    while(++index < length)
    {
        value = number[index];
        count = sizeof(unit) * 2;
        while(count-- > 0)
        {
            *ptr-- = hexDigit[(size_t)(value & 0xF)];
            value = value >> 4;
        }
    }
    ptr = output;
    while(*ptr == '0') ptr++;
    return ptr;
}

void format_mpn(char * output, mp_numptr number)
{
    formatNumber(output, number->digit, number->used);
}


#ifdef MPADEBUG
static void outputNumber(FILE * file, char * name, mp_numptr number)
{
    char buffer[2050];

    formatMPNumber(buffer, number);
    fprintf(file, "%s: #x%s\n", name, buffer);
}
#endif

static boolean setSize(mp_numptr * number, int size, boolean copy)
{
    size_t oldbytes = *number ? BYTESNEEDED((*number)->alloc) : 0;
    size_t newbytes = BYTESNEEDED(size + 1);

    if(oldbytes < newbytes)
    {
        mp_numptr temp = (mp_numptr)qmalloc(newbytes);

        if(!temp) return FALSE;
        if(copy)
        {
            if(*number && (*number)->used > 0) memcpy(temp, *number, oldbytes);
            memset((char*)temp + oldbytes, 0, newbytes - oldbytes);
        }
        temp->alloc = (uint16_t)(size + 1);
        wipe_mpn(number);
        *number = temp;
    }
    (*number)->used = (uint16_t) size;
    return TRUE;
}


boolean set_mpn(uint32_t value, mp_numptr * number)
{
    if(setSize(number, 1, FALSE))
    {
        (*number)->digit[0] = value;
        (*number)->digit[1] = 0;
        if(value == 0) (*number)->used = 0;
        return TRUE;
    }
    else
        return FALSE;
}


boolean read_mpn(char * input, mp_numptr * number)
{
    uint16_t charLen = (uint16_t) strlen(input);
    uint16_t wordLen = (uint16_t)((charLen + sizeof(unit) * 2 - 1) /(sizeof(unit) * 2));
    char * ptr = input + charLen;
    unit factor = 1;
    char * digit;
    uint16_t index = 0;

    if(!setSize(number, wordLen, FALSE)) return FALSE;

    while(ptr-- > input)
    {
        if(factor == 1) (*number)->digit[index] = 0;
        digit = strchr(hexDigit, toupper(*ptr));
        if(!digit) return FALSE;
        (*number)->digit[index] += factor * (digit - hexDigit);
        factor = factor * 16;
        if(!factor)
        {
            index++;
            factor = 1;
        }
    }
    (*number)->digit[wordLen] = 0;
    (*number)->used = wordLen;
    normalise(*number);
    return TRUE;
}


void init_mpn(bignump num) {
    *num = NULL;
}


void clear_mpn(bignump num)
{
    qfree(*num);
    *num = NULL;
}


uint16_t byte_length(byte * number)
{
    return (uint16_t)((EXTRACT_SHORT(number) + 7) /8);
}


boolean get_mpn(bignump internal, byte * external)
{
    uint16_t bytecount = byte_length(external);
    uint16_t wordcount = (uint16_t)((bytecount + sizeof(unit) -1)
        /sizeof(unit));
    unit * word;
    byte * ptr;
    /* char  debug[1000]; */

    if(!setSize(internal, wordcount, FALSE)) return FALSE;
    word = (*internal)->digit + wordcount - 1;
    ptr = external + 2;
    (*internal)->used = wordcount;
    memset((*internal)->digit, 0, (wordcount + 1) * sizeof(unit));
    while(bytecount > 0)
    {
        *word = *word * 256 + *ptr++;
        if(--bytecount % sizeof(unit) == 0) word--;
    }
    /* formatMPNumber(debug, result); */
    return TRUE;
}


static uint16_t topBit(unit value)
{
    static uint16_t lookup[16] = {
        0,1,2,2, 3,3,3,3, 4,4,4,4, 4,4,4,4         };
    uint16_t guess = sizeof(unit) * 4;
    uint16_t incrm = sizeof(unit) * 2;

    stats[4]++;
    while(incrm >= 2)
    {
        if(value >= ((unit)1 << guess))
            guess += (uint16_t) incrm;
        else
            guess -= (uint16_t) incrm;
        incrm /= (uint16_t) 2;
    }
    guess -= (uint16_t) 2;
    return (uint16_t)(lookup[(size_t)(value >> guess)] + guess);
}


/*  Test routine to ensure that topBit always returns the correct answer **/
#ifdef TESTTOPBIT
static void testTopBit(void)
{
    uint16_t result = 0;

    assert(topBit(0) == 0);
    while(++result < UNITBITS)
    {
        assert(topBit(((unit)1 << result) - 1 ) == result);
        assert(topBit( (unit)1 << (result - 1) ) == result);
    }
    assert(topBit(MAXUNIT) == UNITBITS);
}
#endif

uint16_t length_mpn(mp_numptr number)
{
    stats[13]++;
    if(number && number->used > 0)
        return (uint16_t)( (number->used - 1) * UNITBITS
            + topBit(number->digit[number->used - 1]));
    else
        return 0;
}


short put_mpn(byte * external, bignump internal)
{
    uint16_t numBits = length_mpn(*internal);
    short bytecount = (short)((numBits + 7) /8);
    unit * word = (*internal)->digit;
    unit value = *word++;
    byte * ptr = external + bytecount + 2;
    short count = sizeof(unit);

    external[0] = (byte)(numBits /256);
    external[1] = (byte)(numBits % 256);
    while(ptr > external + 2)
    {
        *--ptr = (byte)(value & 0xFF);
        value = value >> 8;
        if(--count == 0)
        {
            value = *word++;
            count = sizeof(unit);
        }
    }
    return bytecount;
}




void wipe_mpn(bignump number)
{
    if(*number)
    {
        size_t bytes = BYTESNEEDED((*number)->alloc);

        memset(*number, 0, bytes);
        qfree(*number);
        *number = NULL;
    }
}

short size_mpn(bignum num) {
    return (short)((length_mpn(num) + 7)/8);
}

/* s_m_multi multiplies a multiple precision number by a single precision
 * the result and the multiple precision operand may be the same location
 * length is the nominal length, the result must be at least one length   */
static void s_m_mult_add(unit result[], unit multi[], unit scalar, unit length)
{
    unit carry = 0;
    register unit high, low1, low2, low3;
    unit temp;
    unit index = 0;

    stats[0]++;
    /* Is basically practical => proceed */
    while(index < length)
    {
        multUnitxUnit(high, low1, scalar, multi[(size_t)index]);
        low2 = low1 + result[(size_t)index];
        if(low2 < low1) high++;
        low3 = low2 + carry;
        if(low3 < low2)
            carry = high + 1;
        else
            carry = high;
        result[(size_t)(index++)] = low3;
    }
    temp = result[(size_t)index];
    result[(size_t)index] = carry + temp;
    if(result[(size_t)index] < temp) result[(size_t)(index+1)]++;
}


/* s_m_multi multiplies a multiple precision number by a single precision
 * the result and the multiple precision operand may be the same location
 * length is the nominal length, the result must be at least one length   */
static void s_m_mult_sub(unit result[], unit multi[], unit scalar, unit length)
{
    register unit high, low1, low2;
    unit borrow = 0;
    unit index = 0;

    stats[1]++;
    /* Is basically practical => proceed */
    while(index < length)
    {
        multUnitxUnit(high, low1, scalar, multi[(size_t)index]);
        low2 = low1 + borrow;
        if(low2 < low1) high++;
        if(low2 > result[(size_t)index])
            borrow = high + 1;
        else
            borrow = high;
        result[(size_t)(index++)] -= low2;
    }
    assert(result[(size_t)index] >= borrow);
    result[(size_t)index] -= borrow;
}

/* m_m_sub subtracts one multiple precision number from another, any or all of the arguments
 * may be the same location; returns true if there is overall borrow */
static int m_m_sub(unit result[], unit minus[], unit length)
{
    register unit difference;
    register unit positive;
    unit index = 0;

    stats[3]++;
    while(TRUE)
    {
        /* no borrow sub-loop */
        do
        {
            positive = result[(size_t)index];
            difference = positive - minus[(size_t)index];
            result[(size_t)index] = difference;
            index++;
            if(index >= length) return (difference > positive);
        }
        while(difference <= positive);

        /* borrow sub-loop */
        do
        {
            positive = result[(size_t)index];
            difference = result[(size_t)index] - 1 - minus[(size_t)index];
            result[(size_t)index] = difference;
            index++;
            if(index >= length) return (difference >= positive);
        }
        while(difference >= positive);
    }
}


static boolean mp_gt(unit left[], unit right[], unit length)
{
    uint16_t offset = (uint16_t) length;

    stats[6]++;
    while(offset--)
    {
        if(left[offset] != right[offset])
            return (boolean)(left[offset] > right[offset]);
    }
    return FALSE;
}

/* Locally suppress warning for assigned but not used - variable 'ignored' */
#ifdef __BORLANDC__
#pragma warn -aus
#endif
static unit s_m_mod(unit operand[], unit modulus[], modulusCache * modSummary)
{
    uint16_t length = modSummary->length;
    uint16_t shift = modSummary->shift;
    unit topUnit = modSummary->topUnit;
    register unit scalar, ignored;
    int first = TRUE;
    unit firstUnit;
    unit secondUnit;

    if(shift)
    {
        firstUnit = (operand[length ] << shift)
            + (operand[length - 1] >> (UNITBITS - shift));
        secondUnit = (operand[length - 1] << shift);

        if(length > 1)
            secondUnit += (operand[length - 2] >> (UNITBITS - shift));
    }
    else
    {    /* This is code path is not merely for efficency.  Some machines
     ** execute shifts by UNITBITS as no-ops rather than set to zero.
     ** e.g. DEC Alpha   */
        firstUnit = operand[length];
        secondUnit = operand[length - 1];
    }

    stats[7]++;
    /* The divisor for the approximate single unit factor is (modSummary->topUnit + 1)
     ** The +1 could cause overflow.  This needs special case treatment. */
    if(topUnit == MAXUNIT)
        scalar = firstUnit;    /* this division in this case is trivial */
    else
    {
        register unit divisor = topUnit + 1;

        assert(firstUnit < divisor);        /* required for udiv_qrnnd */
        divUnitByHalf(firstUnit, secondUnit, divisor, scalar, ignored);
    }
    if(scalar > 1)
        s_m_mult_sub(operand, modulus, scalar, length);
    else
        scalar = 0;
    while(!mp_gt(modulus, operand, length + 1))
    {
        stats[8]++;
        if(!first) stats[9]++;
        first = FALSE;
        {
            boolean ok = (boolean) !m_m_sub(operand, modulus, length + 1);
            assert(ok);
        }
        scalar++;
    }
    assert(operand[length] == 0);
    return scalar;
}
#ifdef __BORLANDC__
#pragma warn .aus
#endif


static void m_m_mult_mod( unit result[], unit left[], unit right[], unit modulus[],
unit work[], modulusCache * modSummary)
{
    uint16_t length = modSummary->length;
    uint16_t index = 0;
    unit * buffer = work + length + 2;
#ifdef MPADEBUG
    char debug[2000];
    char * ptr;
#endif

    stats[10]++;
    memcpy(buffer, left, (length + 2) * sizeof(unit));
    assert(buffer[length] == 0);
    assert(buffer[length + 1] == 0);
    assert(right[length] == 0);
    assert(right[length + 1] == 0);
    memset(result, 0, (length + 2) * sizeof(unit));
    memset(work, 0, (length + 2) * sizeof(unit));
    s_m_mult_add(result, buffer, right[index++], length);
    result[length + 1] = 0;
    /* s_m_mod(result, modulus, modSummary); */
    while(index < length)
    {
#if defined(MPADEBUG) && defined(DETAIL1)
        ptr = formatNumber(debug, result, length + 2);
        fprintf(file, "(defvar R%04lx-%04x #x%s)\n", stats[10], index, ptr);
#endif
        buffer--;        /* effectively multiplication by 0x10000  */
        s_m_mod(buffer, modulus, modSummary);
#if defined(MPADEBUG) && defined(DETAIL1)
        ptr = formatNumber(debug, buffer, length + 2);
        fprintf(file, "(defvar B%04lx-%04x #x%s)\n", stats[10], index, ptr);
#endif
        s_m_mult_add(result, buffer, right[index++], length);
        /*  s_m_mod(result, modulus, modSummary); */
    }
    s_m_mod(result + 1, modulus, modSummary);
    s_m_mod(result, modulus, modSummary);
}


static void setBuffer(unit buffer[], mp_numptr number, uint16_t length)
{
    uint16_t index = length;

    stats[11]++;
    assert(number->used <= length);
    while(index > number->used) buffer[--index] = 0;
    while(index-- > 0) buffer[index] = number->digit[index];
}


static boolean bitSet(unit number[], uint16_t bitNumber)
{
    uint16_t word = (uint16_t)(bitNumber /UNITBITS);
    uint16_t bit = (uint16_t)(bitNumber % UNITBITS);

    stats[12]++;
    return (boolean)((number[word] & ((unit)1 << bit)) > 0);
}


static void summarise(modulusCache * summary, mp_numptr modulus)
{
    uint16_t length = modulus->used;
    uint16_t modulusShift = (uint16_t)(UNITBITS -
        topBit(modulus->digit[length - 1]));

    assert(modulus->digit[length] == 0);
    summary->length = length;
    summary->shift = modulusShift;
    summary->topUnit = (modulus->digit[length - 1] << modulusShift);
    if(length > 1 && modulusShift > 0)
        summary->topUnit += (modulus->digit[length - 2] >> (UNITBITS - modulusShift)) ;
}


boolean divide_mpn( bignump quotient, bignump remainder,
bignum numerator, bignum denominator)
{
    modulusCache modSummary;
    short offset = (short)(numerator->used - denominator->used);
    uint16_t d_length;
    size_t n_bytes = (numerator->used + 1) * sizeof(unit);
    unit * result = (unit*)qmalloc(n_bytes);

    assert(numerator->digit[numerator->used] == 0);
    memcpy(result, numerator->digit, n_bytes);
    summarise(&modSummary, denominator);
    d_length = modSummary.length;
    if(mp_gt(denominator->digit, result + offset, d_length)) offset--;
    if(!setSize(quotient, offset + 1, FALSE)) return FALSE;
    if(remainder && !setSize(remainder, d_length, FALSE)) return FALSE;
    (*quotient)->digit[offset + 1] = 0;
    while(offset >= 0)
    {
        (*quotient)->digit[offset] =
            s_m_mod(result + offset, denominator->digit, &modSummary);
        offset--;
    }
    normalise(*quotient);
    if(remainder)
    {
        memcpy((*remainder)->digit, result, (d_length + 1) * sizeof(unit));
        normalise(*remainder);
    }
    zfree((void*)&result, n_bytes);
    return TRUE;
}

void remainder_mpn(mp_numptr result, mp_numptr modulus, boolean minus1)
{
    modulusCache modSummary;
    short offset;
    uint16_t length;

    summarise(&modSummary, modulus);
    length = modSummary.length;
    offset = (short)(result->used - modulus->used);
    assert(result->alloc > result->used);
    assert(result->digit[result->used] == 0);
    if(minus1) modulus->digit[0]--;
    if(mp_gt(modulus->digit, result->digit + offset, length)) offset--;
    while(offset >= 0)
    {
        s_m_mod(result->digit + offset--, modulus->digit, &modSummary);
    }
    normalise(result);
    if(minus1) modulus->digit[0]++;
}


boolean multiply_mpn(mp_numptr * result, mp_numptr left, mp_numptr right)
{
    uint16_t length = (uint16_t)(left->used + right->used);    /* could be 1 shorter */
    uint16_t offset = 0;

    if(!setSize(result, length, TRUE)) return FALSE;
    memset((*result)->digit, 0, (length + 1) * sizeof(unit));
    while(offset < left->used)
    {
        s_m_mult_add((*result)->digit + offset, right->digit,
        left->digit[offset], right->used);
        offset++;
    }
    normalise(*result);
    return TRUE;
}

/* This routine is not very well written, especially with respect to
 * extending the result.  Consider a rewrite.  */
boolean add_mpn(mp_numptr * result, mp_numptr add)
{
    boolean carry = FALSE;
    uint16_t index = 0;
    uint16_t length;

    /* First determine the length of the sum */
    if((*result)->used > add->used)
    {
        length = (*result)->used;
        if((*result)->digit[(*result)->used - 1] == MAXUNIT) length++;
    }
    else
    {
        length = add->used;
        if((*result)->used < add->used)
        {
            if(add->digit[add->used - 1] == MAXUNIT) length++;
        }
        else
        {
            /* N.B. Assuming that the numbers are normalised neither top unit
             ** can be zero. */
            if(add->digit[length] < add->digit[length] + (*result)->digit[length] + 1)
                length++;
        }
    }
    if(!setSize(result, length, TRUE)) return FALSE;

    while(index < add->used)
    {
        if(carry && ++(*result)->digit[index] == 0)
            carry = TRUE;
        else
            carry = FALSE;
        (*result)->digit[index] += add->digit[index];
        if((*result)->digit[index] < add->digit[index]) carry = TRUE;
        index++;
    }

    /* propagate carry as far as necessary */
    if(carry) {
        while(++(*result)->digit[index++] == 0) {
        }
    }

    assert(index <= (*result)->alloc);
    normalise(*result);
    return TRUE;
}


void subtract_mpn(mp_numptr result, mp_numptr minus)
{
    uint16_t offset = minus->used;

    assert(result->used >= minus->used);
    if(m_m_sub(result->digit, minus->digit, minus->used))
    {
        /* overall borrow => propagate it */
        while(result->digit[offset++]-- == 0) {
        }
    }
    assert(offset <= result->used + 1);
    normalise(result);
}


boolean copy_mpn(mp_numptr * result, mp_numptr input)
{
    uint16_t length = input->used;

    if(!setSize(result, length, FALSE)) return FALSE;
    memcpy((*result)->digit, input->digit, length * sizeof(unit));
    memset((*result)->digit + length, 0, sizeof(unit));
    return TRUE;
}


boolean gt_mpn(mp_numptr left, mp_numptr right)
{
    if(!left)
        return FALSE;
    else if(!right)
        return TRUE;
    else if(left->used != right->used)
        return (boolean)(left->used > right->used);
    else
        return mp_gt(left->digit, right->digit, left->used);
}


boolean eq_mpn(mp_numptr left, mp_numptr right)
{
    uint16_t offset;

    if(!left || !right || left->used != right->used)
        return FALSE;
    else
    {
        offset = left->used;
        while(offset--)
        {
            if(left->digit[offset] != right->digit[offset]) return FALSE;
        }
        return TRUE;
    }
}


#define SWAP(a, b) { temp = a; a = b; b = temp; }
common_error mod_power_mpn( mp_numptr * result, mp_numptr multi,
mp_numptr expon, mp_numptr modulus)
{
    uint16_t length = modulus->used;
    modulusCache modSummary;
    uint16_t exponBits;
    uint16_t exponBit = 0;
    common_error errorCode = CE_OKAY;

    unit * workSpace = (unit *)zmalloc(5 * (length + 2) * sizeof(unit));
    unit * power2 = workSpace;
    unit * accum = power2 + length + 2;
    unit * intermed = accum + length + 2;
    unit * work = intermed + length + 2;
    unit * temp;
#ifdef MPADEBUG
    char debug[1000];

    file = fopen("intermed.vals", "w");
    outputNumber(file, "xfactor", multi);
    outputNumber(file, "xexpon", expon);
    outputNumber(file, "xmodulus", modulus);
#endif
    /* testTopBit(); */
    if(!workSpace) return CE_NO_MEMORY;
    memset(stats, 0, sizeof(stats));
    summarise(&modSummary, modulus);
    setBuffer(power2, multi, (uint16_t)(length + 1));
    exponBits = length_mpn(expon);
    if(bitSet(expon->digit, 0))
    {
        memcpy(accum, power2, sizeof(unit) * (length + 1));
    }
    else
    {    /* Even exponent; this isn't really expected (for RSA at least) */
        memset(accum, 0, sizeof(unit) * (length + 1));
        accum[0] = 1;
    }
    while(++exponBit < exponBits && (errorCode == CE_OKAY))
    {
        m_m_mult_mod(intermed, power2, power2, modulus->digit, work, &modSummary);
#ifdef MPADEBUG
        formatNumber(debug, intermed, length);
        fprintf(file, "P: %s\n", debug);
#endif
        SWAP(intermed, power2);
        /*  formatNumber(debug, power2, length); */
        if(bitSet(expon->digit, exponBit))
        {
            m_m_mult_mod(intermed, power2, accum, modulus->digit, work, &modSummary);
#ifdef MPADEBUG
            formatNumber(debug, intermed, length);
            fprintf(file, "R: %s\n", debug);
#endif
            SWAP(intermed, accum);
        }
        if(exponBit % 8 == 0 && user_break()) errorCode = CE_USER_BREAK;
    }
    if(errorCode == CE_OKAY)
    {
        if(!setSize(result, length, FALSE))
            errorCode = CE_NO_MEMORY;
        else
        {
            memcpy((*result)->digit, accum, length * sizeof(unit));
            (*result)->digit[length] = 0;
            normalise(*result);
        }
    }
#ifdef MPADEBUG
    outputNumber(file, "xresult", *result);
    fclose(file);
#endif
    zfree((void*)&workSpace, 5 * (length + 2) * sizeof(unit));
    return errorCode;
}

/* routines developed to support elliptic curve encryption */
/* The operations are abstracted from what are used in George Barwood's
 * public domain Pegwit PKE system, version 8.1
 */

boolean nthBitSet_mpn( mp_numptr value, uint16_t bitnum )
{
    if ((uint32_t) bitnum >= (uint32_t) (value->used*UNITBITS)) return FALSE;
    return bitSet(value->digit, bitnum);
}/* true if bitnumth bit is set */

/* makes lowest bit equal the boolean value 0 or 1 */
void set0thBit_mpn (bignum value, boolean bit)
{
    if(bit) value->digit[0] |= 1;
    else value->digit[0] &= (unit)(MAXUNIT - 1);
}


/* pack the lowest bitsUsed bits from datalen uint16_t's into a bignum
 * with a number of clear bits below this.  implicitly init_mpn()s value */
boolean pack16_mpn(bignump value, uint16_t *data, uint16_t datalen,
byte bitsUsed, byte freeLowBits)
{
    uint32_t numbits = (bitsUsed*datalen)+freeLowBits;
    uint16_t numunits = (uint16_t)((numbits + UNITBITS - 1)/UNITBITS);
    uint16_t mask = (uint16_t)((1 << bitsUsed) - 1);
    unit *ptr;
    uint16_t i;
    short basebits;

    /* Allocate space */
    init_mpn(value);
    if(!setSize(value, numunits, FALSE)) return FALSE;

    /* and clear it */
    for(i=0; i<(*value)->alloc; ++i) (*value)->digit[i] = 0;

    /* Copy data across */
    for(i=0,ptr = (*value)->digit,basebits = freeLowBits;
        i<datalen;
        i++, basebits += (short) bitsUsed)
    {
        short nextBit = (short)(basebits + bitsUsed);
        /* coerce to make long enough in 16 bit case! */
        *ptr |= (((unit)(data[i]&mask)) << basebits);

        if(nextBit > UNITBITS)        /* have to carry */
        {
            /* step along one */
            ++ptr;
            basebits -= (short) UNITBITS;
            nextBit -= (short) UNITBITS;

            (*ptr) |= ((data[i]&mask) >>
                (bitsUsed - nextBit));
        }
    }
    /* allow for empty top bits */
    normalise(*value);
    return TRUE;
}
/* the converse operation - fails if datalen is too small */
boolean unpack16_mpn(bignum value, uint16_t *data, uint16_t *datalen,
byte bitsUsed, byte freeLowBits)
{
    uint16_t bitsValid = (uint16_t)(length_mpn(value) - freeLowBits);
    uint16_t needed = (uint16_t)((bitsValid + bitsUsed - 1)/bitsUsed);
    uint16_t i;
    uint16_t mask = (uint16_t)((1 << bitsUsed) - 1);
    unit *ptr;
    short basebits;

    if(needed > *datalen) return FALSE;
    if(needed < *datalen)    /* ensure unwanted stuff is false */
    {
        for(i=needed; i<*datalen; i++) data[i] = 0;
    }
    for(i=0, ptr = value->digit, basebits = freeLowBits;
        i<needed;
        i++, basebits += (short) bitsUsed)
    {
        short nextBit = (short) (basebits + bitsUsed);
        data[i] = (uint16_t)(((*ptr)>>basebits) & mask);

        if(nextBit > UNITBITS)
        {
            /* step along one */
            ++ptr;
            basebits -= (short)UNITBITS;
            nextBit -= (short)UNITBITS;
            data[i] |= (uint16_t)(((*ptr)&((1<<nextBit)-1))
                << (bitsUsed - nextBit));
        }
    }
    *datalen = needed;
    return TRUE;
}

/* special case operation */
boolean triple_mpn (bignump result, bignum input)
{
    /* compute size needed for result */
    uint16_t wordlen = input->used;
    uint16_t i;
    register unit high, low, carry;

    if(input->digit[input->used - 1] >= (MAXUNIT/3) ) wordlen++;

    if(!setSize(result, wordlen, FALSE)) return FALSE;
    (*result)->digit[(*result)->used - 1] = 0;

    for(i=0, carry=0;i<input->used;i++)
    {
        /* probably overkill, but at least I'm sure it catches
         * all cases of carry for all sizes of unit */
        multUnitxUnit(high, low, ((unit)3), input->digit[i]);
        (*result)->digit[i] = low + carry;
        if((*result)->digit[i] < low) ++high;
        carry = high;
    }
    if(carry) (*result)->digit[i] = carry;

    normalise(*result);
    return TRUE;
}

#ifdef __alpha
#define PRIMESIZE 4u
#define ARRAYSIZE 5u
#else
#define PRIMESIZE 8u
#define ARRAYSIZE 9u
#endif

struct mp_prime
{
    uint16_t alloc;
    uint16_t used;
    unit digit[ARRAYSIZE];
};

static const struct mp_prime prime_order_value = {
    ARRAYSIZE, PRIMESIZE, {
#ifdef __alpha
        0x5e0d258442bbcd31UL,
        0x0547840e4bf72d8bUL,
        0x2314691ced9bbec3UL,
        0x0001026dd85081b8UL,
        0x0000000000000000UL        }
#else
    0x42bbcd31UL,
    0x5e0d2584UL,
    0x4bf72d8bUL,
    0x0547840eUL,
    0xed9bbec3UL,
    0x2314691cUL,
    0xd85081b8UL,
    0x0001026dUL,
    0x00000000UL}
#endif
};

static const bignum real_prime_order = (bignum)&prime_order_value;
boolean set_prime_order(mp_numptr * number)
{
    init_mpn(number);
    return copy_mpn(number, real_prime_order);
}


/* end of file bignums.c */

