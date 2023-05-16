/* safer.c
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> March 1998
**  All rights reserved.  For full licence details see file licences.c
**
*/
/*--------------------------------------------------------------------*/

#include "safer.h"
#include "utils.h"
#include <string.h>
#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (disable: 4244) // VC++ bug with +=,-=
#endif

/*-----------------------------Housekeeping---------------------------*/

/* static const int defaultRounds[4] = {6, 10, 8, 10}; */
#define MAXROUNDS 13

int SAFERkeySize(SAFER_TYPE type)
{
    switch(type)
    {
    case k64:
    case sk64:
        return 8;
    case k128:
    case sk128:
        return 16;
    default:
        return 0;
    }
}

/*-----------------------------Log table-----------------------------*/

#define LOGTABSIZE 256
#define BYTE(v) ((v) & 0xFF)
static byte exp[LOGTABSIZE];
static byte log[LOGTABSIZE];
static boolean logTabSet = FALSE;

static void setLogTable(void)
{
    size_t l, e;
    for(l=0, e=1; !logTabSet && l < LOGTABSIZE; ++l)
    {
        exp[l] = (byte)BYTE(e);
        log[exp[l]] = (byte)BYTE(l);
        e = (e*45) % 257;
    }
    logTabSet = TRUE;
}

#define EXPANDEDKEY (1 + SAFERBLOCKSIZE * ( 2*MAXROUNDS + 1))
typedef struct {
    byte k[EXPANDEDKEY];
}
SAFER, *pSAFER;

#define KEYSCHEDLEN  sizeof(SAFER)

#define ADD(a,b) {b+=a; a+=b;}
#define SUB(a,b) {a-=b; b-=a;}

static void SAFERencrypt(byte *in, byte *out, pSAFER key)
{
    byte work[SAFERBLOCKSIZE], tmp, *k = key->k;
    int round = min(MAXROUNDS, key->k[0]);
    memcpy(work, in, SAFERBLOCKSIZE);

    while(round--)
    {
        /* part 1, simple mixing with key */
        work[0] ^= *++k; 
        work[1] += *++k;
        work[2] += *++k; 
        work[3] ^= *++k;
        work[4] ^= *++k; 
        work[5] += *++k;
        work[6] += *++k; 
        work[7] ^= *++k;

        /* part 2, logarithms in the group */
        work[0] = (byte)(exp[work[0]] + *++k); 
        work[1] = (byte)(log[work[1]] ^ *++k);
        work[2] = (byte)(log[work[2]] ^ *++k); 
        work[3] = (byte)(exp[work[3]] + *++k);
        work[4] = (byte)(exp[work[4]] + *++k); 
        work[5] = (byte)(log[work[5]] ^ *++k);
        work[6] = (byte)(log[work[6]] ^ *++k); 
        work[7] = (byte)(exp[work[7]] + *++k);

        /* part 3, mutual increment */
        ADD(work[0], work[1]);
        ADD(work[2], work[3]);
        ADD(work[4], work[5]);
        ADD(work[6], work[7]);

        ADD(work[0], work[2]);
        ADD(work[1], work[3]);
        ADD(work[4], work[6]);
        ADD(work[5], work[7]);

        ADD(work[0], work[4]);
        ADD(work[1], work[5]);
        ADD(work[2], work[6]);
        ADD(work[3], work[7]);

        /* part 4, permute */
        tmp = work[1];
        work[1] = work[4];
        work[4] = work[2];
        work[2] = tmp;
        tmp = work[3];
        work[3] = work[5];
        work[5] = work[6];
        work[6] = tmp;
    }/* end of round */

    /* mix to output */
    out[0] = (byte)(work[0] ^ *++k); 
    out[1] = (byte)(work[1] + *++k);
    out[2] = (byte)(work[2] + *++k); 
    out[3] = (byte)(work[3] ^ *++k);
    out[4] = (byte)(work[4] ^ *++k); 
    out[5] = (byte)(work[5] + *++k);
    out[6] = (byte)(work[6] + *++k); 
    out[7] = (byte)(work[7] ^ *++k);
    tmp = 0;
    memset(work, tmp, SAFERBLOCKSIZE);
}

static void SAFERdecrypt(byte *in, byte *out, pSAFER key)
{
    byte work[SAFERBLOCKSIZE], tmp, *k;
    int round = min(MAXROUNDS, key->k[0]);
    k = key->k + (2*round+1)*SAFERBLOCKSIZE;

    /* undo mix to output */
    work[7] = (byte)(in[7] ^ *k--); 
    work[6] = (byte)(in[6] - *k--);
    work[5] = (byte)(in[5] - *k--); 
    work[4] = (byte)(in[4] ^ *k--);
    work[3] = (byte)(in[3] ^ *k--); 
    work[2] = (byte)(in[2] - *k--);
    work[1] = (byte)(in[1] - *k--); 
    work[0] = (byte)(in[0] ^ *k--);

    while(round--)
    {
        /* undo part 4, permute */
        tmp = work[2];
        work[2] = work[4];
        work[4] = work[1];
        work[1] = tmp;
        tmp = work[6];
        work[6] = work[5];
        work[5] = work[3];
        work[3] = tmp;

        /* undo part 3, mutual increment */
        SUB(work[0], work[4]);
        SUB(work[1], work[5]);
        SUB(work[2], work[6]);
        SUB(work[3], work[7]);

        SUB(work[0], work[2]);
        SUB(work[1], work[3]);
        SUB(work[4], work[6]);
        SUB(work[5], work[7]);

        SUB(work[0], work[1]);
        SUB(work[2], work[3]);
        SUB(work[4], work[5]);
        SUB(work[6], work[7]);

        /* undo parts 1&2, mixing and logarithms in the group */
        work[7] -= *k--; 
        work[6] ^= *k--;
        work[5] ^= *k--; 
        work[4] -= *k--;
        work[3] -= *k--; 
        work[2] ^= *k--;
        work[1] ^= *k--; 
        work[0] -= *k--;

        work[7] = (byte)(log[work[7]] ^ *k--); 
        work[6] = (byte)(exp[work[6]] - *k--);
        work[5] = (byte)(exp[work[5]] - *k--); 
        work[4] = (byte)(log[work[4]] ^ *k--);
        work[3] = (byte)(log[work[3]] ^ *k--); 
        work[2] = (byte)(exp[work[2]] - *k--);
        work[1] = (byte)(exp[work[1]] - *k--); 
        work[0] = (byte)(log[work[0]] ^ *k--);

    }/* end of round */

    /* copy to output */
    memcpy(out, work, SAFERBLOCKSIZE);
    tmp = 0;
    memset(work, tmp, SAFERBLOCKSIZE);
}

#define ROTL(v, n) (byte)BYTE( ((v)<<(n)) | ( ((int)BYTE(v)) >> (8-(n))) )

static void SAFERgetKeysched(byte *key1, byte *key2, byte rounds,
boolean skVariant, pSAFER key)
{
    int i;
    byte worka[1+SAFERBLOCKSIZE], workb[1+SAFERBLOCKSIZE];
    byte *k = key->k;
    *k++ = (byte)min(MAXROUNDS, rounds);
    worka[SAFERBLOCKSIZE] = workb[SAFERBLOCKSIZE] = 0;
    for(i=0; i<SAFERBLOCKSIZE; ++i)
    {
        worka[SAFERBLOCKSIZE] ^= worka[i] = ROTL(key1[i], 5);
        workb[SAFERBLOCKSIZE] ^= workb[i] = *k++ = key2[i];
    }

    if(!logTabSet) setLogTable();

    for (i=1; i<=key->k[0]; ++i)
    {
        int j;
        for(j=0; j<SAFERBLOCKSIZE+1; ++j)
        {
            worka[j] = ROTL(worka[j], 6);
            workb[j] = ROTL(workb[j], 6);
        }
        for(j=0; j<SAFERBLOCKSIZE; ++j)
        {
            if(skVariant)
            {
                *k++ = (byte)BYTE( worka[ (j + 2*i -1) % sizeof(worka)]
                    +exp[exp[BYTE(18 * i + j + 1)]] );
            }
            else
            {
                *k++ = (byte)BYTE( worka[j]
                    +exp[exp[BYTE(18 * i + j + 1)]] );
            }
        }
        for(j=0; j<SAFERBLOCKSIZE; ++j)
        {
            if(skVariant)
            {
                *k++ = (byte)BYTE( workb[ (j + 2*i) % sizeof(worka)]
                    +exp[exp[BYTE(18 * i + j + 10)]] );
            }
            else
            {
                *k++ = (byte)BYTE( workb[j]
                    +exp[exp[BYTE(18 * i + j + 10)]] );
            }
        }
    }/* next i */
    memset(worka, 0, sizeof(worka));
    memset(workb, 0, sizeof(workb));
}

void initSAFER(byte *key, int triple, void **keysched,
size_t *length, SAFER_TYPE type)
{
    int keys = triple ? 3 : 1;
    int i;
    pSAFER b;
    int delta = (k128==type)||(sk128==type) ? SAFERBLOCKSIZE : 0;


    *length = KEYSCHEDLEN*keys;
    *keysched = zmalloc(*length);
    if(!(*keysched)) return;

    b = (pSAFER) *keysched;

    for(i=0; i < keys; i++) /* EBP uses maximal rounds - we might as well */
    { /* hard code this too                         */
        SAFERgetKeysched(key, key+delta, MAXROUNDS,
        (boolean)((sk64==type)||(sk128==type)), b+i);
        key+=(delta+SAFERBLOCKSIZE);
    }

}


void ecbSAFER(void *keysched, int triple, int encrypt, byte *in, byte *out)
{
    pSAFER b = (pSAFER)keysched;

    if(!triple)
    {
        if(encrypt) SAFERencrypt(in, out, b);
        else SAFERdecrypt(in, out, b);
    }
    else
    {
        byte tmp[SAFERBLOCKSIZE], tmp2[SAFERBLOCKSIZE];
        if(encrypt)
        {
            SAFERencrypt(in, tmp, b); 
            ++b;
            SAFERencrypt(tmp, tmp2, b); 
            ++b;
            SAFERencrypt(tmp2, out, b);
        }
        else
        {
            b+=2; 
            SAFERdecrypt(in, tmp, b);
            --b; 
            SAFERdecrypt(tmp, tmp2, b);
            --b; 
            SAFERdecrypt(tmp2, out, b);
        }
        memset(tmp, 0, sizeof(tmp));
        memset(tmp2, 0, sizeof(tmp2));
    }
}


void destroySAFER(void **keysched, size_t length)
{
    /* match memory deallocation to allocation locally! */
    if(length>0) zfree(keysched, length);
}



/* ******** END OF CTC MODULE SAFER.C ******************************* */

#ifdef SELF_TESTING

/*
 Enough code to run the above with a couple of known test vectors to ensure
 that 1) it is invertible and 2) it is SAFER - when we have the test vectors!
*/

static void * zmalloc(size_t n)
{
    void * result = malloc(n);

    if(result) memset(result, 0, n);
    return result;
}
static void zfree(void** v, size_t length)
{
    memset(*v, 0, length);
    free(*v);
}
#include <stdio.h>
/* SAFER-SK64 test vectors, from the ETH reference implementation */
/* The data structure for the ( key, plaintext, ciphertext ) triplets */typedef struct { 
    byte rounds; 
    byte offset; 
    byte key[ 16 ]; 
    byte plaintext[ SAFERBLOCKSIZE ]; 
    byte ciphertext[ SAFERBLOCKSIZE ]; 
}
SAFER_TEST;
static SAFER_TEST testSafer[] = { 
    { /*SAFER_K64_DEFAULT_NOF_ROUNDS,*/
        6,0, { 
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01         }
        , { 
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08         }
        , { 
            0x15, 0x1B, 0xFF, 0x02, 0xAD, 0x11, 0xBF, 0x2D         }
    }
    , { /*SAFER_K64_DEFAULT_NOF_ROUNDS,*/
        6,0, { 
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08         }
        , { 
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08         }
        , { 
            0x5F, 0xCE, 0x9B, 0xA2, 0x05, 0x84, 0x38, 0xC7         }
    }
    , { /*SAFER_K128_DEFAULT_NOF_ROUNDS,*/
        10,8, { 
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01         }
        , { 
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08         }
        , { 
            0x41, 0x4C, 0x54, 0x5A, 0xB6, 0x99, 0x4A, 0xF7         }
    }
    , { /*SAFER_K128_DEFAULT_NOF_ROUNDS,*/
        10,8, { 
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00         }
        , { 
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08         }
        , { 
            0xFF, 0x78, 0x11, 0xE4, 0xB3, 0xA7, 0x2E, 0x71         }
    }
    , { /*SAFER_K128_DEFAULT_NOF_ROUNDS,*/
        10,8, { 
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08         }
        , { 
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08         }
        , { 
            0x49, 0xC9, 0x9D, 0x98, 0xA5, 0xBC, 0x59, 0x08         }
    }
    , };

/*static const int defaultRounds[4] = {6, 10, 8, 10};*/
#if 0
static void dumpKeysched(byte * key)
{
    byte * k = key;
    int rounds = *k++;
    int i;
    printf("Safer on %d rounds\n", rounds);
    for(i=0; i<8; ++i)
    {
        printf("%02x", *k++);
        if(3==i) printf(" ");
    }
    printf("\n");
    for(i=0; i<rounds; ++i)
    {
        int j;
        for(j=0; j<16;++j)
        {
            printf("%02x", *k++);
            if(3==(j%4)) printf(" ");
        }
        printf("\n");
    }
}

static void dumpTable(byte * key)
{
    byte * k =key;
    int i;
    for(i=0; i<16; ++i)
    {
        int j;
        for(j=0; j<16;++j)
        {
            printf("%02x", *k++);
            if(3==(j%4)) printf(" ");
        }
        printf("\n");
    }
}
#endif

/* some standard test vectors */
int main()
{
    SAFER s;
    byte work[SAFERBLOCKSIZE];
    int i,j;
    setLogTable();


    for(j=0; j<5; ++j)
    {
        SAFERgetKeysched(testSafer[j].key,
        testSafer[j].key+testSafer[j].offset,
        testSafer[j].rounds, (boolean)TRUE, &s);

        SAFERencrypt(testSafer[j].plaintext, work, &s);
        for(i=0; i<8; ++i)
        {
            printf("%02x", testSafer[j].ciphertext[i]);
            if(3==i) printf(" ");
        }
        printf(" is expected\n");
        for(i=0; i<8; ++i)
        {
            printf("%02x", work[i]);
            if(3==i) printf(" ");
        }
        printf(" is generated\n");
#if 0
        {
            byte junk[SAFERBLOCKSIZE]
                SAFERdecrypt(work, junk, &s);
            for(i=0; i<8; ++i)
            {
                printf("%02x", junk[i]);
                if(3==i) printf(" ");
            }
            printf(" is decrypted\n");
        }
#endif

        SAFERdecrypt(testSafer[j].ciphertext, work, &s);
        for(i=0; i<8; ++i)
        {
            printf("%02x", testSafer[j].plaintext[i]);
            if(3==i) printf(" ");
        }
        printf(" is expected\n");
        for(i=0; i<8; ++i)
        {
            printf("%02x", work[i]);
            if(3==i) printf(" ");
        }
        printf(" is generated\n");
    }
    return 0;
}
#endif

/* EOF safer.c */

