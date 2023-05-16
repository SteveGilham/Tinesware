/* des.c - driver to Richard Outerbridge's DES implementation
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
**  Heimdall <heimdall@bifroest.demon.co.uk>  1997
**  All rights reserved.  For full licence details see file licences.c
**
**  Modified
**  6-DEC-1997 - use standardised integer types.  Note that at this point,
**  triple-DES uses specialised three-in-one code, rather than doing three
**  DES passes on a block; and this code has not been generalised to allow for
**  Key-dependent DES; that will need to explicitly do triple passage.
**  The history of the code is such that it requires a bit of mangling
**  to remove the excessive optimisation (computation now being cheap) in
**  favour of clarity and extensibility.
*/
#include "des.h"
#include "des3_c.h"
#include "utils.h"
#include <string.h>

/* DES classic */

void initDES(byte *key, int triple, boolean decrypt, void **keysched, size_t *length)
{

    short mode = (short) (decrypt ? DE1 : EN0);
    pDES d;

    if(triple)
    {
        *length = DES3KEYSCHEDLEN;
        d = (pDES)( *keysched = qmalloc(*length) );
        if(!d) return;
        /* [24], 1, [96] */
        des3key(key, mode, d->key);
    }
    else
        {
        *length = DESKEYSCHEDLEN;
        d = (pDES)( *keysched = qmalloc(*length) );
        if(!d) return;
        /* [8], 1, [32] */
        deskey(key, mode, d->key);
    }
    initDesSPboxes(d);
}

void ecbDES(void *keysched, int triple, byte *in, byte *out)
{
    if(triple)
        des3(in, out, keysched);
    else
        des(in, out, keysched);
}

void destroyDES(void **keysched, size_t length)
{
    /* match memory deallocation to allocation locally! */
    if(length>0) zfree(keysched, length);
}


/* s3'-des */
void initS3DES(byte *key, int triple, boolean decrypt, void **keysched, size_t *length)
{

    short mode = (short) (decrypt ? DE1 : EN0);
    pDES d;

    if(triple)
    {
        *length = DES3KEYSCHEDLEN;
        d = (pDES)( *keysched = qmalloc(*length) );
        if(!d) return;
        /* [24], 1, [96] */
        des3key(key, mode, d->key);
    }
    else
        {
        *length = DESKEYSCHEDLEN;
        d = (pDES)( *keysched = qmalloc(*length) );
        if(!d) return;
        /* [8], 1, [32] */
        deskey(key, mode, d->key);
    }
    inits3DesSPboxes(d);
}

void ecbS3DES(void *keysched, int triple, byte *in, byte *out)
{
    if(triple)
        des3(in, out, keysched);
    else
        des(in, out, keysched);
}

void destroyS3DES(void **keysched, size_t length)
{
    /* match memory deallocation to allocation locally! */
    if(length>0) zfree(keysched, length);
}

/* key dependent DES*/
void initKDDES(byte *key, int triple, boolean decrypt, void **keysched, size_t *length)
{

    short mode = (short) (decrypt ? DE1 : EN0);
    pDES d;
    int keys = triple ? 3 : 1;
    int i;

    *length = keys*DESKEYSCHEDLEN;
    d = (pDES)( *keysched = qmalloc(*length) );
    if(!d) return;

    for(i=0; i<keys; ++i)
    {
        /* [8], 1, [96] */
        deskey(key, mode, d->key);
        initKDdesSPboxes(key+DESKEYSIZE, d);
        key+=(DESKEYSIZE+KDDESEXTRA);
    }
}

void ecbKDDES(void *keysched, int triple, int encrypt, byte *in, byte *out)
{
    byte temp[DESBLOCKSIZE];

    pDES d = (pDES)keysched;
    if(triple)
    {
        int i = (encrypt) ? 0 : 2;
        int k = (encrypt) ? 1 : -1;
        des(in, out, d+i);
        des(out, temp, d+i+k);
        des(temp, out, d+i+2*k);
        memset(temp, 0, DESBLOCKSIZE);
    }
    else
        des(in, out, d);
}

void destroyKDDES(void **keysched, size_t length)
{
    /* match memory deallocation to allocation locally! */
    if(length>0) zfree(keysched, length);
}

/* ******** END OF CTC MODULE DES.C ******************************* */

#ifdef TEST

const static uint32_t SP1[64] = {
    0x01010400uL, 0x00000000uL, 0x00010000uL, 0x01010404uL,
    0x01010004uL, 0x00010404uL, 0x00000004uL, 0x00010000uL,
    0x00000400uL, 0x01010400uL, 0x01010404uL, 0x00000400uL,
    0x01000404uL, 0x01010004uL, 0x01000000uL, 0x00000004uL,
    0x00000404uL, 0x01000400uL, 0x01000400uL, 0x00010400uL,
    0x00010400uL, 0x01010000uL, 0x01010000uL, 0x01000404uL,
    0x00010004uL, 0x01000004uL, 0x01000004uL, 0x00010004uL,
    0x00000000uL, 0x00000404uL, 0x00010404uL, 0x01000000uL,
    0x00010000uL, 0x01010404uL, 0x00000004uL, 0x01010000uL,
    0x01010400uL, 0x01000000uL, 0x01000000uL, 0x00000400uL,
    0x01010004uL, 0x00010000uL, 0x00010400uL, 0x01000004uL,
    0x00000400uL, 0x00000004uL, 0x01000404uL, 0x00010404uL,
    0x01010404uL, 0x00010004uL, 0x01010000uL, 0x01000404uL,
    0x01000004uL, 0x00000404uL, 0x00010404uL, 0x01010400uL,
    0x00000404uL, 0x01000400uL, 0x01000400uL, 0x00000000uL,
    0x00010004uL, 0x00010400uL, 0x00000000uL, 0x01010004uL };

const static uint32_t SP2[64] = {
    0x80108020uL, 0x80008000uL, 0x00008000uL, 0x00108020uL,
    0x00100000uL, 0x00000020uL, 0x80100020uL, 0x80008020uL,
    0x80000020uL, 0x80108020uL, 0x80108000uL, 0x80000000uL,
    0x80008000uL, 0x00100000uL, 0x00000020uL, 0x80100020uL,
    0x00108000uL, 0x00100020uL, 0x80008020uL, 0x00000000uL,
    0x80000000uL, 0x00008000uL, 0x00108020uL, 0x80100000uL,
    0x00100020uL, 0x80000020uL, 0x00000000uL, 0x00108000uL,
    0x00008020uL, 0x80108000uL, 0x80100000uL, 0x00008020uL,
    0x00000000uL, 0x00108020uL, 0x80100020uL, 0x00100000uL,
    0x80008020uL, 0x80100000uL, 0x80108000uL, 0x00008000uL,
    0x80100000uL, 0x80008000uL, 0x00000020uL, 0x80108020uL,
    0x00108020uL, 0x00000020uL, 0x00008000uL, 0x80000000uL,
    0x00008020uL, 0x80108000uL, 0x00100000uL, 0x80000020uL,
    0x00100020uL, 0x80008020uL, 0x80000020uL, 0x00100020uL,
    0x00108000uL, 0x00000000uL, 0x80008000uL, 0x00008020uL,
    0x80000000uL, 0x80100020uL, 0x80108020uL, 0x00108000uL };

const static uint32_t SP3[64] = {
    0x00000208uL, 0x08020200uL, 0x00000000uL, 0x08020008uL,
    0x08000200uL, 0x00000000uL, 0x00020208uL, 0x08000200uL,
    0x00020008uL, 0x08000008uL, 0x08000008uL, 0x00020000uL,
    0x08020208uL, 0x00020008uL, 0x08020000uL, 0x00000208uL,
    0x08000000uL, 0x00000008uL, 0x08020200uL, 0x00000200uL,
    0x00020200uL, 0x08020000uL, 0x08020008uL, 0x00020208uL,
    0x08000208uL, 0x00020200uL, 0x00020000uL, 0x08000208uL,
    0x00000008uL, 0x08020208uL, 0x00000200uL, 0x08000000uL,
    0x08020200uL, 0x08000000uL, 0x00020008uL, 0x00000208uL,
    0x00020000uL, 0x08020200uL, 0x08000200uL, 0x00000000uL,
    0x00000200uL, 0x00020008uL, 0x08020208uL, 0x08000200uL,
    0x08000008uL, 0x00000200uL, 0x00000000uL, 0x08020008uL,
    0x08000208uL, 0x00020000uL, 0x08000000uL, 0x08020208uL,
    0x00000008uL, 0x00020208uL, 0x00020200uL, 0x08000008uL,
    0x08020000uL, 0x08000208uL, 0x00000208uL, 0x08020000uL,
    0x00020208uL, 0x00000008uL, 0x08020008uL, 0x00020200uL };

const static uint32_t SP4[64] = {
    0x00802001uL, 0x00002081uL, 0x00002081uL, 0x00000080uL,
    0x00802080uL, 0x00800081uL, 0x00800001uL, 0x00002001uL,
    0x00000000uL, 0x00802000uL, 0x00802000uL, 0x00802081uL,
    0x00000081uL, 0x00000000uL, 0x00800080uL, 0x00800001uL,
    0x00000001uL, 0x00002000uL, 0x00800000uL, 0x00802001uL,
    0x00000080uL, 0x00800000uL, 0x00002001uL, 0x00002080uL,
    0x00800081uL, 0x00000001uL, 0x00002080uL, 0x00800080uL,
    0x00002000uL, 0x00802080uL, 0x00802081uL, 0x00000081uL,
    0x00800080uL, 0x00800001uL, 0x00802000uL, 0x00802081uL,
    0x00000081uL, 0x00000000uL, 0x00000000uL, 0x00802000uL,
    0x00002080uL, 0x00800080uL, 0x00800081uL, 0x00000001uL,
    0x00802001uL, 0x00002081uL, 0x00002081uL, 0x00000080uL,
    0x00802081uL, 0x00000081uL, 0x00000001uL, 0x00002000uL,
    0x00800001uL, 0x00002001uL, 0x00802080uL, 0x00800081uL,
    0x00002001uL, 0x00002080uL, 0x00800000uL, 0x00802001uL,
    0x00000080uL, 0x00800000uL, 0x00002000uL, 0x00802080uL };

const static uint32_t SP5[64] = {
    0x00000100uL, 0x02080100uL, 0x02080000uL, 0x42000100uL,
    0x00080000uL, 0x00000100uL, 0x40000000uL, 0x02080000uL,
    0x40080100uL, 0x00080000uL, 0x02000100uL, 0x40080100uL,
    0x42000100uL, 0x42080000uL, 0x00080100uL, 0x40000000uL,
    0x02000000uL, 0x40080000uL, 0x40080000uL, 0x00000000uL,
    0x40000100uL, 0x42080100uL, 0x42080100uL, 0x02000100uL,
    0x42080000uL, 0x40000100uL, 0x00000000uL, 0x42000000uL,
    0x02080100uL, 0x02000000uL, 0x42000000uL, 0x00080100uL,
    0x00080000uL, 0x42000100uL, 0x00000100uL, 0x02000000uL,
    0x40000000uL, 0x02080000uL, 0x42000100uL, 0x40080100uL,
    0x02000100uL, 0x40000000uL, 0x42080000uL, 0x02080100uL,
    0x40080100uL, 0x00000100uL, 0x02000000uL, 0x42080000uL,
    0x42080100uL, 0x00080100uL, 0x42000000uL, 0x42080100uL,
    0x02080000uL, 0x00000000uL, 0x40080000uL, 0x42000000uL,
    0x00080100uL, 0x02000100uL, 0x40000100uL, 0x00080000uL,
    0x00000000uL, 0x40080000uL, 0x02080100uL, 0x40000100uL };

const static uint32_t SP6[64] = {
    0x20000010uL, 0x20400000uL, 0x00004000uL, 0x20404010uL,
    0x20400000uL, 0x00000010uL, 0x20404010uL, 0x00400000uL,
    0x20004000uL, 0x00404010uL, 0x00400000uL, 0x20000010uL,
    0x00400010uL, 0x20004000uL, 0x20000000uL, 0x00004010uL,
    0x00000000uL, 0x00400010uL, 0x20004010uL, 0x00004000uL,
    0x00404000uL, 0x20004010uL, 0x00000010uL, 0x20400010uL,
    0x20400010uL, 0x00000000uL, 0x00404010uL, 0x20404000uL,
    0x00004010uL, 0x00404000uL, 0x20404000uL, 0x20000000uL,
    0x20004000uL, 0x00000010uL, 0x20400010uL, 0x00404000uL,
    0x20404010uL, 0x00400000uL, 0x00004010uL, 0x20000010uL,
    0x00400000uL, 0x20004000uL, 0x20000000uL, 0x00004010uL,
    0x20000010uL, 0x20404010uL, 0x00404000uL, 0x20400000uL,
    0x00404010uL, 0x20404000uL, 0x00000000uL, 0x20400010uL,
    0x00000010uL, 0x00004000uL, 0x20400000uL, 0x00404010uL,
    0x00004000uL, 0x00400010uL, 0x20004010uL, 0x00000000uL,
    0x20404000uL, 0x20000000uL, 0x00400010uL, 0x20004010uL };

const static uint32_t SP7[64] = {
    0x00200000uL, 0x04200002uL, 0x04000802uL, 0x00000000uL,
    0x00000800uL, 0x04000802uL, 0x00200802uL, 0x04200800uL,
    0x04200802uL, 0x00200000uL, 0x00000000uL, 0x04000002uL,
    0x00000002uL, 0x04000000uL, 0x04200002uL, 0x00000802uL,
    0x04000800uL, 0x00200802uL, 0x00200002uL, 0x04000800uL,
    0x04000002uL, 0x04200000uL, 0x04200800uL, 0x00200002uL,
    0x04200000uL, 0x00000800uL, 0x00000802uL, 0x04200802uL,
    0x00200800uL, 0x00000002uL, 0x04000000uL, 0x00200800uL,
    0x04000000uL, 0x00200800uL, 0x00200000uL, 0x04000802uL,
    0x04000802uL, 0x04200002uL, 0x04200002uL, 0x00000002uL,
    0x00200002uL, 0x04000000uL, 0x04000800uL, 0x00200000uL,
    0x04200800uL, 0x00000802uL, 0x00200802uL, 0x04200800uL,
    0x00000802uL, 0x04000002uL, 0x04200802uL, 0x04200000uL,
    0x00200800uL, 0x00000000uL, 0x00000002uL, 0x04200802uL,
    0x00000000uL, 0x00200802uL, 0x04200000uL, 0x00000800uL,
    0x04000002uL, 0x04000800uL, 0x00000800uL, 0x00200002uL };

const static uint32_t SP8[64] = {
    0x10001040uL, 0x00001000uL, 0x00040000uL, 0x10041040uL,
    0x10000000uL, 0x10001040uL, 0x00000040uL, 0x10000000uL,
    0x00040040uL, 0x10040000uL, 0x10041040uL, 0x00041000uL,
    0x10041000uL, 0x00041040uL, 0x00001000uL, 0x00000040uL,
    0x10040000uL, 0x10000040uL, 0x10001000uL, 0x00001040uL,
    0x00041000uL, 0x00040040uL, 0x10040040uL, 0x10041000uL,
    0x00001040uL, 0x00000000uL, 0x00000000uL, 0x10040040uL,
    0x10000040uL, 0x10001000uL, 0x00041040uL, 0x00040000uL,
    0x00041040uL, 0x00040000uL, 0x10041000uL, 0x00001000uL,
    0x00000040uL, 0x10040040uL, 0x00001000uL, 0x00041040uL,
    0x10001000uL, 0x00000040uL, 0x10000040uL, 0x10040000uL,
    0x10040040uL, 0x10000000uL, 0x00040000uL, 0x10001040uL,
    0x00000000uL, 0x10041040uL, 0x00040040uL, 0x10000040uL,
    0x10040000uL, 0x10001000uL, 0x10001040uL, 0x00000000uL,
    0x10041040uL, 0x00041000uL, 0x00041000uL, 0x00001040uL,
    0x00001040uL, 0x00040040uL, 0x10000000uL, 0x10041000uL };


/*
 Enough code to run the above with a couple of know test vectors to ensure
 that 1) it is invertible and 2) it is DES
*/

void * qmalloc(size_t n)
{
    void * result = malloc(n);

    if(result) memset(result, 0, n);
    return result;
}

void zfree (void ** buffer, size_t n)
{
    memset(*buffer, 0, n);
    free(*buffer);
    *buffer = 0;
}



#include <stdio.h>
/* standard test vectors */

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
*/

static void printvec(char *s, byte vec[8])
{
    int i;
    printf("%s:",s);
    for(i=0; i<8; i+=2)
    {
        printf(" %02x%02x", vec[i], vec[i+1]);
    }
    printf("\n");
}

int main()
{
    byte key1[8] = {
        0x01, 0x23, 0x45, 0x67, 0x89, 0xab, 0xcd, 0xef     };
    byte plain[8] = {
        0x01, 0x23, 0x45, 0x67, 0x89, 0xab, 0xcd, 0xe7     };
    byte in[8], out[8];
    byte key2[16] = {
        0x01, 0x23, 0x45, 0x67, 0x89, 0xab, 0xcd, 0xef,
        0xfe, 0xdc, 0xba, 0x98, 0x76, 0x54, 0x32, 0x10     };
    byte key3[24] = {
        0x01, 0x23, 0x45, 0x67, 0x89, 0xab, 0xcd, 0xef,
        0xfe, 0xdc, 0xba, 0x98, 0x76, 0x54, 0x32, 0x10,
        0x89, 0xab, 0xcd, 0xef, 0x01, 0x23, 0x45, 0x67     };


    DES3 d;
    memcpy(in, plain, 8);
    initDesSPboxes((pDES) &d);

    printf("** static SP boxes**\n");
    memcpy(d.SP1, SP1, 64*sizeof(uint32_t));
    memcpy(d.SP2, SP2, 64*sizeof(uint32_t));
    memcpy(d.SP3, SP3, 64*sizeof(uint32_t));
    memcpy(d.SP4, SP4, 64*sizeof(uint32_t));
    memcpy(d.SP5, SP5, 64*sizeof(uint32_t));
    memcpy(d.SP6, SP6, 64*sizeof(uint32_t));
    memcpy(d.SP7, SP7, 64*sizeof(uint32_t));
    memcpy(d.SP8, SP8, 64*sizeof(uint32_t));


    deskey(key1, EN0, d.key);
    des(in, out, (pDES) &d);
    printf( "Expect Cipher : c957 4425 6a5e d31d\n");
    printvec("Get    Cipher ",out);

    deskey(key1, DE1, d.key);
    des(out, in, (pDES) &d);
    printf( "Expect Plain  : 0123 4567 89ab cde7\n");
    printvec("Get    Plain  ",in);
    printf("\n");

    des2key(key2, EN0, d.key);
    des3(in, out, &d);
    printf( "Expect Cipher : 7f1d 0a77 826b 8aff\n");
    printvec("Get    Cipher ",out);

    des2key(key2, DE1, d.key);
    des3(out, in, &d);
    printf( "Expect Plain  : 0123 4567 89ab cde7\n");
    printvec("Get    Plain  ",in);
    printf("\n");

    des3key(key3, EN0, d.key);
    des3(in, out, &d);
    printf( "Expect Cipher : de0b 7c06 ae5e 0ed5\n");
    printvec("Get    Cipher ",out);

    des3key(key3, DE1, d.key);
    des3(out, in, &d);
    printf( "Expect Plain  : 0123 4567 89ab cde7\n");
    printvec("Get    Plain  ",in);


    printf("\n\n** procedural SP boxes**\n");
    initDesSPboxes((pDES) &d);

    /* test these match up */
    {
        int i;
        int ok = 1;
        uint32_t test[2];

        test[0] = test[1] = 0;
        for(i=0; ok && i<64; ++i)
        {
            if(SP1[i] != d.SP1[i])
            {
                if(ok)
                {
                    ok = 0;
                    printf("SP1[%d] want %08x, get %08x\n",
                    i, SP1[i], d.SP1[i]);
                }
                if(i<32) test[0] |= 1<<(31-i);
                else test[1] |= 1<<(63-i);
            }
        }
        if(!ok) printf("test1 not OK\n");
        ok = 1;
        for(i=0; ok && i<64; ++i)
        {
            if(SP2[i] != d.SP2[i])
            {
                ok = 0;
                printf("SP2[%d] want %08x, get %08x\n",
                i, SP2[i], d.SP2[i]);
            }
        }
        if(!ok) printf("test2 not OK\n");
        ok = 1;
        for(i=0; ok && i<64; ++i)
        {
            if(SP3[i] != d.SP3[i])
            {
                ok = 0;
                printf("SP3[%d] want %08x, get %08x\n",
                i, SP3[i], d.SP3[i]);
            }
        }
        if(!ok) printf("test3 not OK\n");
        ok = 1;
        for(i=0; ok && i<64; ++i)
        {
            if(SP4[i] != d.SP4[i])
            {
                ok = 0;
                printf("SP4[%d] want %08x, get %08x\n",
                i, SP4[i], d.SP4[i]);
            }
        }
        if(!ok) printf("test4 not OK\n");
        ok = 1;
        for(i=0; ok && i<64; ++i)
        {
            if(SP5[i] != d.SP5[i])
            {
                ok = 0;
                printf("SP5[%d] want %08x, get %08x\n",
                i, SP5[i], d.SP5[i]);
            }
        }
        if(!ok) printf("test5 not OK\n");
        ok = 1;
        for(i=0; ok && i<64; ++i)
        {
            if(SP6[i] != d.SP6[i])
            {
                ok = 0;
                printf("SP6[%d] want %08x, get %08x\n",
                i, SP6[i], d.SP6[i]);
            }
        }
        if(!ok) printf("test6 not OK\n");
        ok = 1;
        for(i=0; ok && i<64; ++i)
        {
            if(SP7[i] != d.SP7[i])
            {
                ok = 0;
                printf("SP7[%d] want %08x, get %08x\n",
                i, SP7[i], d.SP7[i]);
            }
        }
        if(!ok) printf("test7 not OK\n");
        ok = 1;
        for(i=0; ok && i<64; ++i)
        {
            if(SP8[i] != d.SP8[i])
            {
                ok = 0;
                printf("SP8[%d] want %08x, get %08x\n",
                i, SP8[i], d.SP8[i]);
            }
        }
        if(!ok) printf("test8 not OK\n");

    }

    deskey(key1, EN0, d.key);
    des(in, out, (pDES) &d);
    printf( "Expect Cipher : c957 4425 6a5e d31d\n");
    printvec("Get    Cipher ",out);

    deskey(key1, DE1, d.key);
    des(out, in, (pDES) &d);
    printf( "Expect Plain  : 0123 4567 89ab cde7\n");
    printvec("Get    Plain  ",in);
    printf("\n");

    des2key(key2, EN0, d.key);
    des3(in, out, &d);
    printf( "Expect Cipher : 7f1d 0a77 826b 8aff\n");
    printvec("Get    Cipher ",out);

    des2key(key2, DE1, d.key);
    des3(out, in, &d);
    printf( "Expect Plain  : 0123 4567 89ab cde7\n");
    printvec("Get    Plain  ",in);
    printf("\n");

    des3key(key3, EN0, d.key);
    des3(in, out, &d);
    printf( "Expect Cipher : de0b 7c06 ae5e 0ed5\n");
    printvec("Get    Cipher ",out);

    des3key(key3, DE1, d.key);
    des3(out, in, &d);
    printf( "Expect Plain  : 0123 4567 89ab cde7\n");
    printvec("Get    Plain  ",in);
    return 0;
}


#endif /*TEST*/




/* end of file des.c */
