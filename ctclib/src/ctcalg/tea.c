/* tea.c
 *
 *  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> October 1996
 *  All rights reserved.  For full licence details see file licences.c
 *
 *  The Tiny Encryption Algorithm of Wheeler and Needham
 *  based on the code on the Vader web site at Bradford UK
 *  Brought into the CTC context
 *  25-Jan-01 tines : parentheses for silencing g++
 */
/*--------------------------------------------------------------------*/

#include "tea.h"
#include "utils.h"
#include <string.h>

/*  delta is 1/phi * 2^32 where
   phi is the Golden ratio, 1.618... */
#define NROUNDS 32
#define DELTA 0x9E3779B9L


typedef struct {
    uint32_t k[TEAKEYSIZE/4]; /* MSB-first values => local endianness */
}
TEA, *pTEA;

/* length to malloc for a whole keyschedule structure */
#define KEYSCHEDLEN sizeof(TEA)

static void TEA_encipher(uint32_t *Xl, uint32_t *Xr, pTEA ks)
{
    register uint32_t y=*Xl, z=*Xr, sum=0, delta=DELTA, n=NROUNDS,
    a=ks->k[0], b=ks->k[1], c=ks->k[2], d=ks->k[3];

    while(n--)
    {
        sum += delta;
        y += ((z<<4)+a) ^ (z+sum) ^ ((z>>5)+b);
        z += ((y<<4)+c) ^ (y+sum) ^ ((y>>5)+d);
    }

    *Xl = y;
    *Xr = z;
}

#ifdef _MSC_VER
#pragma warning (disable: 4307)
#endif /* silence noise about sum overflowing */
static void TEA_decipher(uint32_t *Xl, uint32_t *Xr, pTEA ks)
{
    register uint32_t y=*Xl, z=*Xr, sum=DELTA*NROUNDS, delta=DELTA, n=NROUNDS,
    a=ks->k[0], b=ks->k[1], c=ks->k[2], d=ks->k[3];

    while(n--)
    {
        z -= ((y<<4)+c) ^ (y+sum) ^ ((y>>5)+d);
        y -= ((z<<4)+a) ^ (z+sum) ^ ((z>>5)+b);
        sum -= delta;
    }

    *Xl = y;
    *Xr = z;
}

static void convertTEAkey(uint8_t key[], pTEA keysched)
{
    int i, j; /* counters */

    /* Re-order the key bytes to local architecture */
    for (i=j=0; i < TEAKEYSIZE/4; ++i)
    {
        keysched->k[i] = ((uint32_t)key[j]<<24)+((uint32_t)key[j+1]<<16)+
            ((uint32_t)key[j+2]<<8)+(uint32_t)key[j+3];
        j+=4;
    }
}

void initTEA(byte *key, int triple, void **keysched, size_t *length)
{
    int keys = triple ? 3 : 1;
    int i;
    pTEA b;

    *length = KEYSCHEDLEN*keys;
    *keysched = zmalloc(*length);
    if(!(*keysched)) return;

    b = (pTEA) *keysched;

    for(i=0; i < keys; i++)
    {
        convertTEAkey(key, b+i );
        key+=TEAKEYSIZE;
    }
}

void ecbTEA(void *keysched, int triple, int encrypt,
byte *in, byte *out)
{
    int i, keys = triple ? 3 : 1;
    uint32_t text[2];
    pTEA b = (pTEA)keysched;
    if(triple && (!encrypt)) b+=2;

    /* pack byte streams in MSB-first form into the 32bit integers */
    /* so that the MSB becomes the high byte, regardless of architecture */
    text[0] = ((uint32_t)in[0]<<24)+((uint32_t)in[1]<<16)
        +((uint32_t)in[2]<<8)+in[3];
    text[1] = ((uint32_t)in[4]<<24)+((uint32_t)in[5]<<16)
        +((uint32_t)in[6]<<8)+in[7];

    for(i=0; i<keys; i++)
    {
        if(encrypt) TEA_encipher(text, text+1, b+i );
        else TEA_decipher(text, text+1, b-i );
    }

    /* and unpack back into MSB-first format*/
    out[0] = (byte)((text[0]>>24) & 0xFF); 
    out[1] = (byte)((text[0]>>16) & 0xFF);
    out[2] = (byte)((text[0]>> 8) & 0xFF); 
    out[3] = (byte)( text[0] & 0xFF);
    out[4] = (byte)((text[1]>>24) & 0xFF); 
    out[5] = (byte)((text[1]>>16) & 0xFF);
    out[6] = (byte)((text[1]>> 8) & 0xFF); 
    out[7] = (byte)( text[1] & 0xFF);
}

void destroyTEA(void **keysched, size_t length)
{
    /* match memory deallocation to allocation locally! */
    if(length>0) zfree(keysched, length);
}
/* ******** END OF CTC MODULE TEA.C ******************************* */

#ifdef test

/*
 Enough code to run the above with a couple of known test vectors to ensure
 that 1) it is invertible and 2) it is TEA - when we have the test vectors!
*/

static void * zmalloc(size_t n)
{ 
    void * result = malloc(n);

    if(result) memset(result, 0, n);
    return result; 
}

#include <stdio.h>

/* some standard test vectors */
int main()
{

}
#endif

/* EOF tea.c */

