/* idea.c - C source code for IDEA block cipher.
 * IDEA (International Data Encryption Algorithm), formerly known as
 * IPES (Improved Proposed Encryption Standard).
 * Algorithm developed by Xuejia Lai and James L. Massey, of ETH Zurich.
 * This implementation modified and derived from original C code
 * developed by Xuejia Lai.
 * Zero-based indexing added, names changed from IPES to IDEA.
 * CFB functions added.  Random number routines added.
 *
 *  Optimized for speed 21 Oct 92 by Colin Plumb.
 *  Very minor speedup on 23 Feb 93 by Colin Plumb.
 *  idearand() given a separate expanded key on 25 Feb 93, Colin Plumb.
 *
 * There are two adjustments that can be made to this code to
 * speed it up.  Defaults may be used for PCs.  Only the -DIDEA32
 * pays off significantly if selectively set or not set.
 * Experiment to see what works better for you.
 *
 * Multiplication: default is inline, -DAVOID_JUMPS uses a
 *  different version that does not do any conditional
 *  jumps (a few percent worse on a SPARC), while
 *  -DSMALL_CACHE takes it out of line to stay
 *  within a small on-chip code cache.
 * Variables: normally, 16-bit variables are used, but some
 *  machines (notably RISCs) do not have 16-bit registers,
 *  so they do a great deal of masking.  -DIDEA32 uses "int"
 *  register variables and masks explicitly only where
 *  necessary.  On a SPARC, for example, this boosts
 *  performace by 30%.
 *
 * The IDEA(tm) block cipher is covered by a patent held by ETH and a
 * Swiss company called Ascom-Tech AG.  The Swiss patent number is
 * PCT/CH91/00117.  International patents are pending. IDEA(tm) is a
 * trademark of Ascom-Tech AG.  There is no license fee required for
 * noncommercial use.  Commercial users may obtain licensing details
 * from Dieter Profos, Ascom Tech AG, Solothurn Lab, Postfach 151, 4502
 * Solothurn, Switzerland, Tel +41 65 242885, Fax +41 65 235761.
 *
 * The IDEA block cipher uses a 64-bit block size, and a 128-bit key 
 * size.  It breaks the 64-bit cipher block into four 16-bit words
 * because all of the primitive inner operations are done with 16-bit
 * arithmetic.  It likewise breaks the 128-bit cipher key into eight
 * 16-bit words.
 *
 * For further information on the IDEA cipher, see these papers:
 * 1) Xuejia Lai, "Detailed Description and a Software Implementation of
 *      the IPES Cipher", Institute for Signal and Information
 *       Processing, ETH-Zentrum, Zurich, Switzerland, 1991
 * 2) Xuejia Lai, James L. Massey, Sean Murphy, "Markov Ciphers and
 *       Differential Cryptanalysis", Advances in Cryptology- EUROCRYPT'91
 *
 * This code assumes that each pair of 8-bit bytes comprising a 16-bit
 * word in the key and in the cipher block are externally represented
 * with the Most Significant Byte (MSB) first, regardless of the
 * internal native byte order of the target CPU.
 */

/* Modified 6 Mar 1996 by Heimdall to copy the IV rather than copying
    the pointer to the IV.

    Modified 14 Apr 1996 by Mr. Tines to separate the CFB mode from the
    IDEA algorithm; add the NO_IDEA bracketing to allow simple compilation
    of patent-free option.  External routines are in the NO_IDEA case set
    to be no-ops.

    Localise keyschedule deallocation with
    the allocation.  Mr. Tines 16-Feb-97

    extra parentheses to silence g++  Mr. Tines 25-Jan-01

 */

#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (disable: 4244) // VC++ bug with +=,-=
#endif

#include <string.h>
#include "idea.h"
#include "utils.h"
typedef byte * byteptr;

#ifndef NO_IDEA


#ifdef TEST
#include <stdio.h>
#include <time.h>
#endif

#define ROUNDS 8  /* Don't change this value, should be 8 */
#define KEYLEN (6*ROUNDS+4) /* length of key schedule */

typedef uint16_t IDEAkey[KEYLEN];

#ifdef IDEA32 /* Use >16-bit temporaries */
#define low16(x) ((x) & 0xFFFF)
typedef uint32_t uint16; /* at LEAST 16 bits, maybe more */
#else
#define low16(x) (x) /* this is only ever applied to uint16's */
typedef uint16_t uint16;
#endif

#ifdef _GNUC_
/* __const__ simply means there are no side effects for this function,
 * which is useful info for the gcc optimizer */
#define CONST __const__
#else
#define CONST
#endif

static void en_key_idea(uint16_t *userkey, uint16_t *Z);
static void de_key_idea(IDEAkey Z, IDEAkey DK);
static void cipher_idea(uint16_t in[4], uint16_t out[4], CONST IDEAkey Z);

/*
 * Multiplication, modulo (2**16)+1
 * Note that this code is structured like this on the assumption that
 * untaken branches are cheaper than taken branches, and the compiler
 * doesn't schedule branches.
 */
#ifdef SMALL_CACHE
/* #ifdef MACTC5  with ThinkC V7 the assembler insert is 20% slower! */

CONST static uint16 mul(register uint16 a, register uint16 b)
{
    register uint32_t p;

    if (a)
    { 
        if (b)
        { 
            p = (uint32_t)a * b;
            b = low16(p);
            a = p>>16;
            return b - a + (b < a);
        }
        else
            { 
            return 1-a;
        }
    }
    else
        { 
        return 1-b;
    }
}/* mul */
#endif /* SMALL_CACHE */

/*
 * Compute multiplicative inverse of x, modulo (2**16)+1,
 * using Euclid's GCD algorithm.  It is unrolled twice to
 * avoid swapping the meaning of the registers each iteration,
 * and some subtracts of t have been changed to adds.
 */
CONST static uint16 inv(uint16 x) 
{
    uint16 t0, t1;
    uint16 q, y;

    if (x <= 1)
        return x; /* 0 and 1 are self-inverse */
    t1 = (uint16)(0x10001L /x); /* Since x >= 2, this fits into 16 bits */
    y = (uint16)(0x10001L % x);
    if (y == 1)
        return (uint16) low16(1-t1);
    t0 = 1;
    do
        { 
        q = (uint16) (x /y);
        x = (uint16) (x % y);
        t0 += (uint16)(q * t1);
        if (x == 1)
            return t0;
        q = (uint16)(y /x);
        y = (uint16)(y % x);
        t1 += (uint16)(q * t0);
    }
    while (y != 1);
    return (uint16)low16(1-t1);
}/* inv */

/* Compute IDEA encryption subkeys Z */
static void en_key_idea(uint16_t *userkey, uint16_t *Z)
{
    int i,j;

    for (j=0; j<8; j++)
        Z[j] = *userkey++;

    for (i=0; j<KEYLEN; j++)
    { 
        i++;
        Z[i+7] = (uint16_t)((Z[i & 7] << 9) | (Z[(i+1) & 7] >> 7));
        Z += i & 8;
        i &= 7;
    }
}/* en_key_idea */

/* Compute IDEA decryption subkeys DK from encryption subkeys Z */
/* Note: these buffers *may* overlap! */
static void de_key_idea(IDEAkey Z, IDEAkey DK)
{
    int j;
    uint16 t1, t2, t3;
    IDEAkey T;
    uint16_t *p = T + KEYLEN;

    t1 = inv(*Z++);
    t2 = (uint16)-*Z++;
    t3 = (uint16)-*Z++;
    *--p = inv(*Z++);
    *--p = t3;
    *--p = t2;
    *--p = t1;

    for (j = 1; j < ROUNDS; j++)
    {
        t1 = *Z++;
        *--p = *Z++;
        *--p = t1;

        t1 = inv(*Z++);
        t2 = (uint16)-*Z++;
        t3 = (uint16)-*Z++;
        *--p = inv(*Z++);
        *--p = t2;
        *--p = t3;
        *--p = t1;
    }
    t1 = *Z++;
    *--p = *Z++;
    *--p = t1;

    t1 = inv(*Z++);
    t2 = (uint16)-*Z++;
    t3 = (uint16)-*Z++;
    *--p = inv(*Z++);
    *--p = t3;
    *--p = t2;
    *--p = t1;
    /* Copy and destroy temp copy */
    for (j = 0, p = T; j < KEYLEN; j++)
    {
        *DK++ = *p;
        *p++ = 0;
    }
}/* de_key_idea */

/*
 * MUL(x,y) computes x = x*y, modulo 0x10001.  Requires two temps,
 * t16 and t32.  x must me a side-effect-free lvalue.  y may be
 * anything, but unlike x, must be strictly 16 bits even if low16()
 * is #defined.
 * All of these are equivalent - see which is faster on your machine
 */
#ifdef SMALL_CACHE
#define MUL(x,y) (x = mul(low16(x),y))
#else
#ifdef AVOID_JUMPS
#define MUL(x,y) (x = (uint16) low16(x-1), t16 = (uint16) low16((y)-1), \
t32 = (uint32_t)x*t16+x+t16+1, x = (uint16) low16(t32), \
 t16 = (uint16) (t32>>16), x = (uint16)(x-t16+(x<t16)) )
#else
#define MUL(x,y) (((t16 = (y)) != 0) ? ((x=low16(x)) != 0) ? \
t32 = (uint32_t)x*t16, x = (uint16)low16(t32), t16 = (uint16)(t32>>16), \
 x = (uint16)(x-t16+(x<t16)) :  (x = (uint16)(1-t16)) :  (x = (uint16)(1-x)))
#endif
#endif

/* IDEA encryption/decryption algorithm */
/* Note that in and out can be the same buffer */
static void cipher_idea(uint16_t in[4], uint16_t out[4], register CONST IDEAkey Z)
{
    register uint16 x1, x2, x3, x4, s2, s3;
#ifndef SMALL_CACHE
    register uint16 t16;
    register uint32_t t32;
#endif

    int r = ROUNDS;

    x1 = *in++; 
    x2 = *in++;
    x3 = *in++; 
    x4 = *in;
    do
        {
        MUL(x1,*Z++);
        x2 += *Z++;
        x3 += *Z++;
        MUL(x4, *Z++);

        s3 = x3;
        x3 ^= x1;
        MUL(x3, *Z++);
        s2 = x2;
        x2 ^= x4;
        x2 += x3;
        MUL(x2, *Z++);
        x3 += x2;

        x1 ^= x2;
        x4 ^= x3;

        x2 ^= s3;
        x3 ^= s2;
    }
    while (--r);
    MUL(x1, *Z++);
    *out++ = x1;
    *out++ = (uint16_t)(x3 + *Z++);
    *out++ = (uint16_t)(x2 + *Z++);
    MUL(x4, *Z);
    *out = x4;
}/* cipher_idea */

/*-------------------------------------------------------------*/

#ifdef TEST
/*
 * This is the number of Kbytes of test data to encrypt.
 * It defaults to 1 MByte.
 */
#ifndef KBYTES
#define KBYTES 1024
#endif

void main(void)
{ /* Test driver for IDEA cipher */

    int i, j, k; 
    IDEAkey Z, DK;
    uint16_t XX[4], TT[4], YY[4]; 
    uint16_t userkey[8];
    clock_t start, end;
    long l;

    /* Make a sample user key for testing... */
    for(i=0; i<8; i++)
        userkey[i] = i+1;

    /* Compute encryption subkeys from user key... */
    en_key_idea(userkey,Z);
    printf("\nEncryption key subblocks: ");
    for(j=0; j<ROUNDS+1; j++)
    {
        printf("\nround %d:   ", j+1);
        if (j==ROUNDS)
            for(i=0; i<4; i++)
            printf(" %6u", Z[j*6+i]);
        else
            for(i=0; i<6; i++)
            printf(" %6u", Z[j*6+i]);
    }

    /* Compute decryption subkeys from encryption subkeys... */
    de_key_idea(Z,DK);
    printf("\nDecryption key subblocks: ");
    for(j=0; j<ROUNDS+1; j++)
    {
        printf("\nround %d:   ", j+1);
        if (j==ROUNDS)
            for(i=0; i<4; i++)
            printf(" %6u", DK[j*6+i]);
        else
            for(i=0; i<6; i++)
            printf(" %6u", DK[j*6+i]);
    }

    /* Make a sample plaintext pattern for testing... */
    for (k=0; k<4; k++)
        XX[k] = k;

    printf("\n Encrypting %d KBytes (%ld blocks)...", KBYTES, KBYTES*64l);
    fflush(stdout);
    start = clock();
    cipher_idea(XX,YY,Z); /* encrypt plaintext XX, making YY */

    for (l = 1; l < 64*KBYTES; l++)
        cipher_idea(YY,YY,Z); /* repeated encryption */
    cipher_idea(YY,TT,DK); /* decrypt ciphertext YY, making TT */

    for (l = 1; l < 64*KBYTES; l++)
        cipher_idea(TT,TT,DK); /* repeated decryption */
    end = clock() - start;
    l = end * 1000. /CLOCKS_PER_SEC + 1;
    i = l/1000;
    j = l%1000;
    l = KBYTES * 1024. * CLOCKS_PER_SEC /end;
    printf("%d.%03d seconds = %ld bytes per second\n", i, j, l);

    printf("\nX %6u   %6u  %6u  %6u \n", 
    XX[0], XX[1], XX[2], XX[3]);
    printf("Y %6u   %6u  %6u  %6u \n",
    YY[0], YY[1], YY[2], YY[3]);
    printf("T %6u   %6u  %6u  %6u \n",
    TT[0], TT[1], TT[2], TT[3]);

    /* Now decrypted TT should be same as original XX */
    for (k=0; k<4; k++)
        if (TT[k] != XX[k])
        {
        printf("\n\07Error!  Noninvertable encryption.\n");
        exit(-1); /* error exit */

    }
    printf("\nNormal exit.\n");
    exit(0); /* normal exit */

}/* main */


#endif  /* TEST */


/*************************************************************************/

static boolean littleEndian;
#endif /* NO_IDEA */

/* initkey_idea initializes IDEA for ECB mode operations */
#ifdef NO_IDEA
#ifdef __BORLANDC__
#pragma warn -par
#endif
#endif
void initIDEA(byte *key, int triple, void **keysched, size_t *length)
{
#ifndef NO_IDEA
    uint16_t userkey[IDEAKEYSIZE/2];
    int i,k;
    int keys = triple ? 3 : 1;
    uint16_t *Z;

    *length = 4*KEYLEN;
    if(triple) (*length) *= 3;

    *keysched = zmalloc (*length);
    if(!(*keysched)) return;

    littleEndian = little_endian();
    Z = *keysched;

    for(k=0; k<keys; k++, Z+=2*KEYLEN)
    {
        /* Assume each pair of bytes comprising a word is ordered MSB-first. */
        for (i=0; i<(IDEAKEYSIZE/2); i++)
        {
            userkey[i] = (uint16_t)((key[0]<<8) + key[1]);
            key += 2;
        }
        en_key_idea(userkey,Z);
        de_key_idea(Z,Z+KEYLEN); /* compute inverse key schedule DK */
    }

    for (i=0; i<(IDEAKEYSIZE/2); i++) /* Erase dangerous traces */
        userkey[i] = 0;
#else
    *length = 0;
    *keysched = 0;
#endif

}/* initkey_idea */
#ifdef NO_IDEA
#ifdef __BORLANDC__
#pragma warn .par
#endif
#endif

#ifndef NO_IDEA
/* Run a 64-bit block thru IDEA in ECB (Electronic Code Book) mode,
 using the currently selected key schedule.
*/
static void idea_ecb(uint16_t *inbuf, uint16_t *outbuf, IDEAkey Z)
{
    uint16_t x;
    /* Assume each pair of bytes comprising a word is ordered MSB-first. */
    if(littleEndian)
        {
        /* Invert the byte order for each 16-bit word for internal use. */
        x = inbuf[0]; 
        outbuf[0] = (uint16_t)((x >> 8) | (x << 8));
        x = inbuf[1]; 
        outbuf[1] = (uint16_t)((x >> 8) | (x << 8));
        x = inbuf[2]; 
        outbuf[2] = (uint16_t)((x >> 8) | (x << 8));
        x = inbuf[3]; 
        outbuf[3] = (uint16_t)((x >> 8) | (x << 8));
        cipher_idea(outbuf, outbuf, Z);
        x = outbuf[0]; 
        outbuf[0] = (uint16_t)((x >> 8) | (x << 8));
        x = outbuf[1]; 
        outbuf[1] = (uint16_t)((x >> 8) | (x << 8));
        x = outbuf[2]; 
        outbuf[2] = (uint16_t)((x >> 8) | (x << 8));
        x = outbuf[3]; 
        outbuf[3] = (uint16_t)((x >> 8) | (x << 8));
    }
    else
        {
        /* Byte order for internal and external representations is the same. */
        cipher_idea(inbuf, outbuf, Z);
    }
}/* idea_ecb */
#endif

#ifdef NO_IDEA
#ifdef __BORLANDC__
#pragma warn -par
#endif
#endif
void ecbIDEA(void *keysched, int triple, int encrypt, byte *in, byte *out)
{
#ifndef NO_IDEA
    int keys = (triple) ? 3 : 1;
    int i;
    byte *key = ((byte*)(keysched));
    IDEAkey Z;
    uint16_t sin[IDEABLOCKSIZE/2], sout[IDEABLOCKSIZE/2];

    int delta = 4*KEYLEN;

    if(!encrypt)
        {
        key += 2*KEYLEN;
        if(triple) key += 2*delta;
        delta *= -1;
    }

    memmove(sin, in, IDEABLOCKSIZE);
    for(i=0; i < keys; i++, key+=delta)
    {
        memmove(Z, key, 2*KEYLEN);
        idea_ecb(sin, sout, Z);
        memmove(sin, sout, IDEABLOCKSIZE);
    }

    memmove(out, sout, IDEABLOCKSIZE);
#else
    memmove (out, in, IDEABLOCKSIZE);
#endif
}
#ifdef NO_IDEA
#ifdef __BORLANDC__
#pragma warn .par
#endif
#endif

void destroyIDEA(void **keysched, size_t length)
{
    /* match memory deallocation to allocation locally! */
    if(length>0) zfree(keysched, length);
}

/* end of idea.c */

