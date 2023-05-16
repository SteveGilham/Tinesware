/* des3port.c
 * Graven Imagery 1996
 * v1.0 1996/10/25 20:49:48 EDT
 *
 * Another portable, public domain, version of the Data Encryption Standard.
 *
 * Written with Symantec's THINK (Lightspeed) C by Richard Outerbridge.
 * Thanks to Dan Hoey for the Initial and Inverse permutation code.
 * This is substantially the same engine that appears in Schneier, after
 * ASCII-fication.  The des3 engine has been split out to speed it up. 
 *
 * THIS SOFTWARE PLACED IN THE PUBLIC DOMAIN BY THE AUTHOR
 * 1996/11/10 18:00:00 EST
 *
 * OBLIGATORY IMPORTANT DISCLAIMERS, WARNINGS AND RESTRICTIONS
 * ===========================================================
 *
 * [1] This software is provided "as is" without warranty of fitness for use
 * or suitability for any purpose, express or implied.  Use at your own risk
 * or not at all.  It does, however, "do" DES.  To check your implementation
 * compare against the validation triples at the end of des3port.c
 *
 * [2] This software is "freeware".  It may be freely used and distributed.
 * My copyright in this software has not been abandoned, and is hereby asserted.
 *
 * [3] Exporting encryption software may require an export licence or permit.
 * Consult the appropriate branch(es) of your federal government.
 *
 * Copyright (c) 1988,1989,1990,1991,1992,1996 by Richard Outerbridge.
 * (outer@interlog.com; CIS : [71755,204]) Graven Imagery, 1996.
 *
 * Ported to compile clean on Borland C++ November 15 1997 --Mr.Tines
 * Added procedural initialisation of the SP-box arrays to allow
 * for eventual use of DES variants.
 *
 * Standardise unsigned integer types; remark that the Public Domain declaration
 * above and note [2] contradict each other -- 6-Dec-1997, Mr. Tines 
 *
 */

#include <stddef.h>
#include "des3_c.h"

/* P-box definition */
static uint32_t bits1[4] =
{ 
    0x00000004uL, 0x00000400uL, 0x00010000uL, 0x01000000uL};
static uint32_t bits2[4] =
{ 
    0x00008000uL, 0x80000000uL, 0x00000020uL, 0x00100000uL};
static uint32_t bits3[4] =
{ 
    0x08000000uL, 0x00000008uL, 0x00020000uL, 0x00000200uL};
static uint32_t bits4[4] =
{ 
    0x00000001uL, 0x00800000uL, 0x00002000uL, 0x00000080uL};
static uint32_t bits5[4] =
{ 
    0x40000000uL, 0x00000100uL, 0x00080000uL, 0x02000000uL};
static uint32_t bits6[4] =
{ 
    0x00004000uL, 0x00400000uL, 0x00000010uL, 0x20000000uL};
static uint32_t bits7[4] =
{ 
    0x04000000uL, 0x00000800uL, 0x00200000uL, 0x00000002uL};
static uint32_t bits8[4] =
{ 
    0x00001000uL, 0x00040000uL, 0x00000040uL, 0x10000000uL};

/* S-box definitions : classic DES*/
static byte ds1[64] = {
    14, 4, 13, 1,2, 15, 11, 8, 3, 10, 6, 12, 5 ,9 ,0, 7,
    0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8,
    4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0,
    15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13};

static byte ds2[64] = {
    15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10,
    3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5,
    0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15,
    13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9};

static byte ds3[64] = {
    10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8,
    13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1,
    13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7,
    1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12};

static byte ds4[64] = {
    7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15,
    13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9,
    10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4,
    3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14};

static byte ds5[64] = {
    2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9,
    14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6,
    4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14,
    11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3};

static byte ds6[64] = {
    12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11,
    10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8,
    9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6,
    4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13};

static byte ds7[64] = {
    4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1,
    13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6,
    1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2,
    6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12};

static byte ds8[64] = {
    13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7,
    1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2,
    7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8,
    2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11};


/* S-box definitions, s3' des */
static byte s31[64] = {
    13, 14, 0, 3, 10, 4, 7, 9, 11, 8, 12, 6, 1, 15, 2, 5,
    8, 2, 11, 13, 4, 1, 14, 7, 5, 15, 0, 3, 10, 6, 9, 12,
    14, 9, 3, 10, 0, 7, 13, 4, 8, 5, 6, 15, 11, 12, 1, 2,
    1, 4, 14, 7, 11, 13, 8, 2, 6, 3, 5, 10, 12, 0, 15, 9};

static byte s32[64] = {
    15, 8, 3, 14, 4, 2, 9, 5, 0, 11, 10, 1, 13, 7, 6, 12,
    6, 15, 9, 5, 3, 12, 10, 0, 13, 8, 4, 11, 14, 2, 1, 7,
    9, 14, 5, 8, 2, 4, 15, 3, 10, 7, 6, 13, 1, 11, 12, 0,
    10, 5, 3, 15, 12, 9, 0, 6, 1, 2, 8, 4, 11, 14, 7, 13};

static byte s33[64] = {
    13, 3, 11, 5, 14, 8, 0, 6, 4, 15, 1, 12, 7, 2, 10, 9,
    4, 13, 1, 8, 7, 2, 14, 11, 15, 10, 12, 3, 9, 5, 0, 6,
    6, 5, 8, 11, 13, 14, 3, 0, 9, 2, 4, 1, 10, 7, 15, 12,
    1, 11, 7, 2, 8, 13, 4, 14, 6, 12, 10, 15, 3, 0, 9, 5};

static byte s34[64] = {
    9, 0, 7, 11, 12, 5, 10, 6, 15, 3, 1, 14, 2, 8, 4, 13,
    5, 10, 12, 6, 0, 15, 3, 9, 8, 13, 11, 1, 7, 2, 14, 4,
    10, 7, 9, 12, 5, 0, 6, 11, 3, 14, 4, 2, 8, 13, 15, 1,
    3, 9, 15, 0, 6, 10, 5, 12, 14, 2, 1, 7, 13, 4, 8, 11};

static byte s35[64] = {
    5, 15, 9, 10, 0, 3, 14, 4, 2, 12, 7, 1, 13, 6, 8, 11,
    6, 9, 3, 15, 5, 12, 0, 10, 8, 7, 13, 4, 2, 11, 14, 1,
    15, 0, 10, 9, 3, 5, 4, 14, 8, 11, 1, 7, 6, 12, 13, 2,
    12, 5, 0, 6, 15, 10, 9, 3, 7, 2, 14, 11, 8, 1, 4, 13};

static byte s36[64] = {
    4, 3, 7, 10, 9, 0, 14, 13, 15, 5, 12, 6, 2, 11, 1, 8,
    14, 13, 11, 4, 2, 7, 1, 8, 9, 10, 5, 3, 15, 0, 12, 6,
    13, 0, 10, 9, 4, 3, 7, 14, 1, 15, 6, 12, 8, 5, 11, 2,
    1, 7, 4, 14, 11, 8, 13, 2, 10, 12, 3, 5, 6, 15, 0, 9};

static byte s37[64] = {
    4, 10, 15, 12, 2, 9, 1, 6, 11, 5, 0, 3, 7, 14, 13, 8,
    10, 15, 6, 0, 5, 3, 12, 9, 1, 8, 11, 13, 14, 4, 7, 2,
    2, 12, 9, 6, 15, 10, 4, 1, 5, 11, 3, 0, 8, 7, 14, 13,
    12, 6, 3, 9, 0, 5, 10, 15, 2, 13, 4, 14, 7, 11, 1, 8};

static byte s38[64] = {
    13, 10, 0, 7, 3, 9, 14, 4, 2, 15, 12, 1, 5, 6, 11, 8,
    2, 7, 13, 1, 4, 14, 11, 8, 15, 12, 6, 10, 9, 5, 0, 3,
    4, 13, 14, 0, 9, 3, 7, 10, 1, 8, 2, 11, 15, 5, 12, 6,
    8, 11, 7, 14, 2, 4, 13, 1, 6, 5, 9, 0, 12, 15, 3, 10};


/* basic compounding of permutation and companding */
static void spbox( byte *box, uint32_t *mask, uint32_t *compiled)
{
    int i;

    for(i=0; i<64; i++)
    {
        uint32_t entry = 0;
        int spindex, j;

        spindex = (i/2) + 16*(i%2) + ((i&32)>>1);

        for(j=0; j<4; j++)
        {
            if(box[spindex] & (1<<j)) entry |= mask[j];
        }

        compiled[i] = entry;
    }
}

/* Set up SP boxes as per DES-classic*/
void initDesSPboxes(pDES boxes)
{
    spbox(ds1, bits1, boxes->SP1);
    spbox(ds2, bits2, boxes->SP2);
    spbox(ds3, bits3, boxes->SP3);
    spbox(ds4, bits4, boxes->SP4);
    spbox(ds5, bits5, boxes->SP5);
    spbox(ds6, bits6, boxes->SP6);
    spbox(ds7, bits7, boxes->SP7);
    spbox(ds8, bits8, boxes->SP8);
}

/* Set up SP boxes as per the modified s3-DES in Schneier */
void inits3DesSPboxes(pDES boxes)
{
    spbox(s31, bits1, boxes->SP1);
    spbox(s32, bits2, boxes->SP2);
    spbox(s33, bits3, boxes->SP3);
    spbox(s34, bits4, boxes->SP4);
    spbox(s35, bits5, boxes->SP5);
    spbox(s36, bits6, boxes->SP6);
    spbox(s37, bits7, boxes->SP7);
    spbox(s38, bits8, boxes->SP8);
}


/* Key dependency shuffling of SP box permutations */
static void KDspbox(byte *box, uint32_t *mask, uint32_t *compiled,
byte key[KDDESEXTRA], int index)
{
    int i;

    int rowswap = key[0] & (1 << index);
    int colswap = key[1] & (1 << index);

    byte xor = (byte) (
    ( (key[2] && (1 << index)) ? 1 : 0) |
        ( (key[3] && (1 << index)) ? 2 : 0) |
        ( (key[4] && (1 << index)) ? 4 : 0) |
        ( (key[5] && (1 << index)) ? 8 : 0) );

    for(i=0; i<64; i++)
    {
        uint32_t entry = 0;
        int spindex, j;
        byte value;

        spindex = (i/2) + 16*(i%2) + ((i&32)>>1);

        if(rowswap) spindex = (spindex+32) % 64;

        if(colswap)
        {
            int k = spindex/32;
            spindex = (32*k) + ( ( (spindex-(32*k)) + 16 ) % 32 );
        }

        value = (byte)((xor ^ box[spindex]) & 0xF);

        for(j=0; j<4; j++)
        {
            if(value & (1<<j)) entry |= mask[j];
        }

        compiled[i] = entry;
    }
}
/* S-box order for Key-dependent DES */
/* static char scramble[8] =
{2, 4, 6, 7, 3, 1, 3, 8}; */
/*                 ^ 5?? */

/* Set up Biham's key-dependent DES */
void initKDdesSPboxes(byte key[KDDESEXTRA],pDES boxes)
{
    KDspbox(ds2, bits1, boxes->SP1, key, 0);
    KDspbox(ds4, bits2, boxes->SP2, key, 1);
    KDspbox(ds6, bits3, boxes->SP3, key, 2);
    KDspbox(ds7, bits4, boxes->SP4, key, 3);
    KDspbox(ds3, bits5, boxes->SP5, key, 4);
    KDspbox(ds1, bits6, boxes->SP6, key, 5);
    KDspbox(ds5, bits7, boxes->SP7, key, 6);
    KDspbox(ds8, bits8, boxes->SP8, key, 7);
}

void des( byte *inblock, byte *outblock, pDES key )
{
    uint32_t fval, work, right, leftt, * keys = key->key;
    int round;


    leftt = ((uint32_t)inblock[0] << 24)
        | ((uint32_t)inblock[1] << 16)
            | ((uint32_t)inblock[2] << 8)
                | (uint32_t)inblock[3];
    right = ((uint32_t)inblock[4] << 24)
        | ((uint32_t)inblock[5] << 16)
            | ((uint32_t)inblock[6] << 8)
                | (uint32_t)inblock[7];
    work = ((leftt >> 4) ^ right) & 0x0f0f0f0fuL;
    right ^= work;
    leftt ^= (work << 4);
    work = ((leftt >> 16) ^ right) & 0x0000ffffuL;
    right ^= work;
    leftt ^= (work << 16);
    work = ((right >> 2) ^ leftt) & 0x33333333uL;
    leftt ^= work;
    right ^= (work << 2);
    work = ((right >> 8) ^ leftt) & 0x00ff00ffuL;
    leftt ^= work;
    right ^= (work << 8);
    right = ((right << 1) | ((right >> 31) & 1uL)) & 0xffffffffuL;
    work = (leftt ^ right) & 0xaaaaaaaauL;
    leftt ^= work;
    right ^= work;
    leftt = ((leftt << 1) | ((leftt >> 31) & 1uL)) & 0xffffffffuL;

    for( round = 0; round < 8; round++ ) {
        work = ((right << 28) | (right >> 4)) ^ *keys++;
        fval = key->SP7[(size_t)( work & 0x3fuL)];
        fval |= key->SP5[(size_t)((work >> 8) & 0x3fuL)];
        fval |= key->SP3[(size_t)((work >> 16) & 0x3fuL)];
        fval |= key->SP1[(size_t)((work >> 24) & 0x3fuL)];
        work = right ^ *keys++;
        fval |= key->SP8[(size_t)( work & 0x3fuL)];
        fval |= key->SP6[(size_t)((work >> 8) & 0x3fuL)];
        fval |= key->SP4[(size_t)((work >> 16) & 0x3fuL)];
        fval |= key->SP2[(size_t)((work >> 24) & 0x3fuL)];
        leftt ^= fval;
        work = ((leftt << 28) | (leftt >> 4)) ^ *keys++;
        fval = key->SP7[(size_t)( work & 0x3fuL)];
        fval |= key->SP5[(size_t)((work >> 8) & 0x3fuL)];
        fval |= key->SP3[(size_t)((work >> 16) & 0x3fuL)];
        fval |= key->SP1[(size_t)((work >> 24) & 0x3fuL)];
        work = leftt ^ *keys++;
        fval |= key->SP8[(size_t)( work & 0x3fuL)];
        fval |= key->SP6[(size_t)((work >> 8) & 0x3fuL)];
        fval |= key->SP4[(size_t)((work >> 16) & 0x3fuL)];
        fval |= key->SP2[(size_t)((work >> 24) & 0x3fuL)];
        right ^= fval;
    }

    right = (right << 31) | (right >> 1);
    work = (leftt ^ right) & 0xaaaaaaaauL;
    leftt ^= work;
    right ^= work;
    leftt = (leftt << 31) | (leftt >> 1);
    work = ((leftt >> 8) ^ right) & 0x00ff00ffuL;
    right ^= work;
    leftt ^= (work << 8);
    work = ((leftt >> 2) ^ right) & 0x33333333uL;
    right ^= work;
    leftt ^= (work << 2);
    work = ((right >> 16) ^ leftt) & 0x0000ffffuL;
    leftt ^= work;
    right ^= (work << 16);
    work = ((right >> 4) ^ leftt) & 0x0f0f0f0fuL;
    leftt ^= work;
    right ^= (work << 4);
    outblock[0] = (byte)((right >> 24) & 0xFF);
    outblock[1] = (byte)((right >> 16) & 0xFF);
    outblock[2] = (byte)((right >> 8) & 0xFF);
    outblock[3] = (byte)((right ) & 0xFF);
    outblock[4] = (byte)((leftt >> 24) & 0xFF);
    outblock[5] = (byte)((leftt >> 16) & 0xFF);
    outblock[6] = (byte)((leftt >> 8) & 0xFF);
    outblock[7] = (byte)((leftt ) & 0xFF);
    return;
}

void des3( byte *inblock, byte *outblock, pDES3 key )
{
    uint32_t fval, work, right, leftt, *keys = key->key;
    int round, iterate;

    leftt = ((uint32_t)inblock[0] << 24)
        | ((uint32_t)inblock[1] << 16)
            | ((uint32_t)inblock[2] << 8)
                | (uint32_t)inblock[3];
    right = ((uint32_t)inblock[4] << 24)
        | ((uint32_t)inblock[5] << 16)
            | ((uint32_t)inblock[6] << 8)
                | (uint32_t)inblock[7];
    work = ((leftt >> 4) ^ right) & 0x0f0f0f0fuL;
    right ^= work;
    leftt ^= (work << 4);
    work = ((leftt >> 16) ^ right) & 0x0000ffffuL;
    right ^= work;
    leftt ^= (work << 16);
    work = ((right >> 2) ^ leftt) & 0x33333333uL;
    leftt ^= work;
    right ^= (work << 2);
    work = ((right >> 8) ^ leftt) & 0x00ff00ffuL;
    leftt ^= work;
    right ^= (work << 8);
    right = ((right << 1) | ((right >> 31) & 1uL)) & 0xffffffffuL;
    work = (leftt ^ right) & 0xaaaaaaaauL;
    leftt ^= work;
    right ^= work;
    leftt = ((leftt << 1) | ((leftt >> 31) & 1uL)) & 0xffffffffuL;

#ifdef NASTY_GOTO
    iterate = 1;
    goto Des0; /* Mr. Tines says - Yuk!! all this saves is one swap
                        of leftt and right */
#else
    iterate = 0;
    work = right;
    right = leftt;
    leftt = work;
#endif

    while( iterate < 3 ) {
        work = right;
        right = leftt;
        leftt = work;
        iterate++;
#ifdef NASTY_GOTO
Des0:
#endif
        for( round = 0; round < 8; round++ ) {
            work = ((right << 28) | (right >> 4)) ^ *keys++;
            fval = key->SP7[(size_t)( work & 0x3fuL)];
            fval |= key->SP5[(size_t)((work >> 8) & 0x3fuL)];
            fval |= key->SP3[(size_t)((work >> 16) & 0x3fuL)];
            fval |= key->SP1[(size_t)((work >> 24) & 0x3fuL)];
            work = right ^ *keys++;
            fval |= key->SP8[(size_t)( work & 0x3fuL)];
            fval |= key->SP6[(size_t)((work >> 8) & 0x3fuL)];
            fval |= key->SP4[(size_t)((work >> 16) & 0x3fuL)];
            fval |= key->SP2[(size_t)((work >> 24) & 0x3fuL)];
            leftt ^= fval;
            work = ((leftt << 28) | (leftt >> 4)) ^ *keys++;
            fval = key->SP7[(size_t)( work & 0x3fuL)];
            fval |= key->SP5[(size_t)((work >> 8) & 0x3fuL)];
            fval |= key->SP3[(size_t)((work >> 16) & 0x3fuL)];
            fval |= key->SP1[(size_t)((work >> 24) & 0x3fuL)];
            work = leftt ^ *keys++;
            fval |= key->SP8[(size_t)( work & 0x3fuL)];
            fval |= key->SP6[(size_t)((work >> 8) & 0x3fuL)];
            fval |= key->SP4[(size_t)((work >> 16) & 0x3fuL)];
            fval |= key->SP2[(size_t)((work >> 24) & 0x3fuL)];
            right ^= fval;
        }
    }

    right = (right << 31) | (right >> 1);
    work = (leftt ^ right) & 0xaaaaaaaauL;
    leftt ^= work;
    right ^= work;
    leftt = (leftt << 31) | (leftt >> 1);
    work = ((leftt >> 8) ^ right) & 0x00ff00ffuL;
    right ^= work;
    leftt ^= (work << 8);
    work = ((leftt >> 2) ^ right) & 0x33333333uL;
    right ^= work;
    leftt ^= (work << 2);
    work = ((right >> 16) ^ leftt) & 0x0000ffffuL;
    leftt ^= work;
    right ^= (work << 16);
    work = ((right >> 4) ^ leftt) & 0x0f0f0f0fuL;
    leftt ^= work;
    right ^= (work << 4);
    outblock[0] = (byte)((right >> 24) & 0xFF);
    outblock[1] = (byte)((right >> 16) & 0xFF);
    outblock[2] = (byte)((right >> 8) & 0xFF);
    outblock[3] = (byte)((right ) & 0xFF);
    outblock[4] = (byte)((leftt >> 24) & 0xFF);
    outblock[5] = (byte)((leftt >> 16) & 0xFF);
    outblock[6] = (byte)((leftt >> 8) & 0xFF);
    outblock[7] = (byte)((leftt ) & 0xFF);
    return;
}

/* Validation set:
 *
 * Single-length key, single-length plaintext -
 * Key    : 0123 4567 89ab cdef
 * Plain  : 0123 4567 89ab cde7
 * Cipher : c957 4425 6a5e d31d
 *
 * Double-length key, single-length plaintext -
 * Key    : 0123 4567 89ab cdef fedc ba98 7654 3210
 * Plain  : 0123 4567 89ab cde7
 * Cipher : 7f1d 0a77 826b 8aff
 *
 * Triple-length key, single-length plaintext -
 * Key    : 0123 4567 89ab cdef fedc ba98 7654 3210 89ab cdef 0123 4567
 * Plain  : 0123 4567 89ab cde7
 * Cipher : de0b 7c06 ae5e 0ed5
 *
 * des3port.c
 */
