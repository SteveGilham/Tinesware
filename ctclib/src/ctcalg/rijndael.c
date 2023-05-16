/*
// Copyright in this code is held by Dr B.R. Gladman but free direct or
// derivative use is permitted subject to acknowledgement of its origin
// and subject to any constraints placed on the use of the algorithm by
// its designers (if such constraints may exist, this will be indicated
// below).
//
// Dr. B. R. Gladman (brian.gladman@btinternet.com). 25th January 2000.
//
// This is an implementation of Rijndael, an encryption algorithm designed
// by Daemen and Rijmen and submitted as a candidate algorithm for the
// Advanced Encryption Standard programme of the US National Institute of
// Standards and Technology.
//
// The designers of Rijndael have not placed any constraints on the use of
// this algorithm.

 Modified to CTClib API Mr.Tines <tines@ravnaandtines.com> Nov '00

*/

#include "rijndael.h"
#include "utils.h"
#include <string.h>

typedef struct Rijndael_t {
    uint32_t k_len;
    uint32_t e_key[64];
    uint32_t d_key[64];
}
Rijndael, *pRijndael;
#define RIJNDAEL(x) ks->x

#define rotr(x,n)   (((x) >> ((int)((n) & 0x1f))) | ((x) << ((int)((32 - ((n) & 0x1f))))))
#define rotl(x,n)   (((x) << ((int)((n) & 0x1f))) | ((x) >> ((int)((32 - ((n) & 0x1f))))))

/* Invert byte order in a 32 bit variable */

#define bswap(x)    ((rotl(x, 8) & 0x00ff00ff) | (rotr(x, 8) & 0xff00ff00))
#define selfswap(x)    ((x) = bswap(x))

#define LARGE_TABLES

static uint8_t pow_tab[256];
static uint8_t log_tab[256];
static uint8_t sbx_tab[256];
static uint8_t isb_tab[256];
static uint32_t rco_tab[ 10];
static uint32_t ft_tab[4][256];
static uint32_t it_tab[4][256];

#ifdef  LARGE_TABLES
static uint32_t fl_tab[4][256];
static uint32_t il_tab[4][256];
#endif

static uint32_t tab_gen = 0;

static uint8_t f_mult(uint8_t a, uint8_t b)
{
    uint8_t aa = log_tab[a], cc = (uint8_t) (aa + log_tab[b]);
    return pow_tab[cc + (cc < aa ? 1 : 0)];
}

/* Extract byte from a 32 bit quantity (little endian notation) */

#define getbyte(x,n)   ((uint8_t)((x) >> (8 * (n))))

#define ff_mult(a,b)    (a && b ? f_mult(a, b) : 0)

#define f_rn(bo, bi, n, k)                          \
bo[n] = ft_tab[0][getbyte(bi[n],0)] ^ \
 ft_tab[1][getbyte(bi[(n + 1) & 3],1)] ^ \
 ft_tab[2][getbyte(bi[(n + 2) & 3],2)] ^ \
 ft_tab[3][getbyte(bi[(n + 3) & 3],3)] ^ *(k + n)

#define i_rn(bo, bi, n, k)                          \
bo[n] = it_tab[0][getbyte(bi[n],0)] ^ \
 it_tab[1][getbyte(bi[(n + 3) & 3],1)] ^ \
 it_tab[2][getbyte(bi[(n + 2) & 3],2)] ^ \
 it_tab[3][getbyte(bi[(n + 1) & 3],3)] ^ *(k + n)

#ifdef LARGE_TABLES

#define ls_box(x)                \
( fl_tab[0][getbyte(x, 0)] ^ \
 fl_tab[1][getbyte(x, 1)] ^ \
 fl_tab[2][getbyte(x, 2)] ^ \
 fl_tab[3][getbyte(x, 3)] )

#define f_rl(bo, bi, n, k)                          \
bo[n] = fl_tab[0][getbyte(bi[n],0)] ^ \
 fl_tab[1][getbyte(bi[(n + 1) & 3],1)] ^ \
 fl_tab[2][getbyte(bi[(n + 2) & 3],2)] ^ \
 fl_tab[3][getbyte(bi[(n + 3) & 3],3)] ^ *(k + n)

#define i_rl(bo, bi, n, k)                          \
bo[n] = il_tab[0][getbyte(bi[n],0)] ^ \
 il_tab[1][getbyte(bi[(n + 3) & 3],1)] ^ \
 il_tab[2][getbyte(bi[(n + 2) & 3],2)] ^ \
 il_tab[3][getbyte(bi[(n + 1) & 3],3)] ^ *(k + n)

#else

#define ls_box(x)                            \
((uint32_t)sbx_tab[getbyte(x, 0)] << 0) ^ \
 ((uint32_t)sbx_tab[getbyte(x, 1)] << 8) ^ \
 ((uint32_t)sbx_tab[getbyte(x, 2)] << 16) ^ \
 ((uint32_t)sbx_tab[getbyte(x, 3)] << 24)

#define f_rl(bo, bi, n, k)                                      \
bo[n] = (uint32_t)sbx_tab[getbyte(bi[n],0)] ^ \
 rotl(((uint32_t)sbx_tab[getbyte(bi[(n + 1) & 3],1)]), 8) ^ \
 rotl(((uint32_t)sbx_tab[getbyte(bi[(n + 2) & 3],2)]), 16) ^ \
 rotl(((uint32_t)sbx_tab[getbyte(bi[(n + 3) & 3],3)]), 24) ^ *(k + n)

#define i_rl(bo, bi, n, k)                                      \
bo[n] = (uint32_t)isb_tab[getbyte(bi[n],0)] ^ \
 rotl(((uint32_t)isb_tab[getbyte(bi[(n + 3) & 3],1)]), 8) ^ \
 rotl(((uint32_t)isb_tab[getbyte(bi[(n + 2) & 3],2)]), 16) ^ \
 rotl(((uint32_t)isb_tab[getbyte(bi[(n + 1) & 3],3)]), 24) ^ *(k + n)

#endif

static void gen_tabs(void)
{
    uint32_t i, t;
    uint8_t p, q;

    /* log and power tables for GF(2**8) finite field with
    ** 0x011b as modular polynomial - the simplest primitive
    ** root is 0x03, used here to generate the tables */

    for(i = 0,p = 1; i < 256; ++i)
    {
        pow_tab[i] = (uint8_t)p; 
        log_tab[p] = (uint8_t)i;
        p ^= (uint8_t)((p << 1) ^ (p & 0x80 ? 0x01b : 0));
    }

    log_tab[1] = 0;
    for(i = 0,p = 1; i < 10; ++i)
    {
        rco_tab[i] = p;
        p = (uint8_t)((p << 1) ^ (p & 0x80 ? 0x01b : 0));
    }

    for(i = 0; i < 256; ++i)
    {
        p = (uint8_t)((i ? pow_tab[255 - log_tab[i]] : 0));
        q = (uint8_t)(((p >> 7) | (p << 1)) ^ ((p >> 6) | (p << 2)));
        p ^= (uint8_t) (0x63 ^ q ^ ((q >> 6) | (q << 2)));
        sbx_tab[i] = p; 
        isb_tab[p] = (uint8_t)i;
    }

    for(i = 0; i < 256; ++i)
    {
        p = sbx_tab[i];
#ifdef  LARGE_TABLES
        t = p; 
        fl_tab[0][i] = t;
        fl_tab[1][i] = rotl(t, 8);
        fl_tab[2][i] = rotl(t, 16);
        fl_tab[3][i] = rotl(t, 24);
#endif
        t = ((uint32_t)ff_mult(2, p)) |
            ((uint32_t)p << 8) |
            ((uint32_t)p << 16) |
            ((uint32_t)ff_mult(3, p) << 24);

        ft_tab[0][i] = t;
        ft_tab[1][i] = rotl(t, 8);
        ft_tab[2][i] = rotl(t, 16);
        ft_tab[3][i] = rotl(t, 24);

        p = isb_tab[i];

#ifdef  LARGE_TABLES
        t = p; 
        il_tab[0][i] = t;
        il_tab[1][i] = rotl(t, 8);
        il_tab[2][i] = rotl(t, 16);
        il_tab[3][i] = rotl(t, 24);
#endif
        t = ((uint32_t)ff_mult(14, p)) |
            ((uint32_t)ff_mult( 9, p) << 8) |
            ((uint32_t)ff_mult(13, p) << 16) |
            ((uint32_t)ff_mult(11, p) << 24);

        it_tab[0][i] = t;
        it_tab[1][i] = rotl(t, 8);
        it_tab[2][i] = rotl(t, 16);
        it_tab[3][i] = rotl(t, 24);
    }

    tab_gen = 1;
}

#define star_x(x) (((x) & 0x7f7f7f7f) << 1) ^ ((((x) & 0x80808080) >> 7) * 0x1b)

#define imix_col(y,x)       \
    u   = star_x(x);        \
    v   = star_x(u);        \
    w   = star_x(v);        \
    t   = w ^ (x);          \
   (y)  = u ^ v ^ w;        \
   (y) ^= rotr(u ^ t,  8) ^ \
          rotr(v ^ t, 16) ^ \
          rotr(t,24)

/* initialise the key schedule from the user supplied key */
#define loop4(i)                                    \
{   t = rotr(t,  8); t = ls_box(t) ^ rco_tab[i];    \
    t ^= RIJNDAEL(e_key)[4 * i];     RIJNDAEL(e_key)[4 * i + 4] = t;    \
    t ^= RIJNDAEL(e_key)[4 * i + 1]; RIJNDAEL(e_key)[4 * i + 5] = t;    \
    t ^= RIJNDAEL(e_key)[4 * i + 2]; RIJNDAEL(e_key)[4 * i + 6] = t;    \
    t ^= RIJNDAEL(e_key)[4 * i + 3]; RIJNDAEL(e_key)[4 * i + 7] = t;    \
}

#define loop6(i)                                    \
{   t = rotr(t,  8); t = ls_box(t) ^ rco_tab[i];    \
    t ^= RIJNDAEL(e_key)[6 * i];     RIJNDAEL(e_key)[6 * i + 6] = t;    \
    t ^= RIJNDAEL(e_key)[6 * i + 1]; RIJNDAEL(e_key)[6 * i + 7] = t;    \
    t ^= RIJNDAEL(e_key)[6 * i + 2]; RIJNDAEL(e_key)[6 * i + 8] = t;    \
    t ^= RIJNDAEL(e_key)[6 * i + 3]; RIJNDAEL(e_key)[6 * i + 9] = t;    \
    t ^= RIJNDAEL(e_key)[6 * i + 4]; RIJNDAEL(e_key)[6 * i + 10] = t;   \
    t ^= RIJNDAEL(e_key)[6 * i + 5]; RIJNDAEL(e_key)[6 * i + 11] = t;   \
}

#define loop8(i)                                    \
{   t = rotr(t,  8); ; t = ls_box(t) ^ rco_tab[i];  \
    t ^= RIJNDAEL(e_key)[8 * i];     RIJNDAEL(e_key)[8 * i + 8] = t;    \
    t ^= RIJNDAEL(e_key)[8 * i + 1]; RIJNDAEL(e_key)[8 * i + 9] = t;    \
    t ^= RIJNDAEL(e_key)[8 * i + 2]; RIJNDAEL(e_key)[8 * i + 10] = t;   \
    t ^= RIJNDAEL(e_key)[8 * i + 3]; RIJNDAEL(e_key)[8 * i + 11] = t;   \
    t  = RIJNDAEL(e_key)[8 * i + 4] ^ ls_box(t);    \
    RIJNDAEL(e_key)[8 * i + 12] = t;                \
    t ^= RIJNDAEL(e_key)[8 * i + 5]; RIJNDAEL(e_key)[8 * i + 13] = t;   \
    t ^= RIJNDAEL(e_key)[8 * i + 6]; RIJNDAEL(e_key)[8 * i + 14] = t;   \
    t ^= RIJNDAEL(e_key)[8 * i + 7]; RIJNDAEL(e_key)[8 * i + 15] = t;   \
}

static void makeRijndaelKey(const uint8_t in_key[],
const uint32_t key_len,
pRijndael ks)
{
    uint32_t i, t;

    if(!tab_gen)
    {
        gen_tabs();
    }

    memset(ks, 0, sizeof(Rijndael));
    RIJNDAEL(k_len) = (key_len*8 + 31) /32;

    memcpy(RIJNDAEL(e_key), in_key, 16);
    if(!little_endian())
    {
        selfswap(RIJNDAEL(e_key)[0]);
        selfswap(RIJNDAEL(e_key)[1]);
        selfswap(RIJNDAEL(e_key)[2]);
        selfswap(RIJNDAEL(e_key)[3]);
    }

    switch(RIJNDAEL(k_len))
    {
    case 4:
        t = RIJNDAEL(e_key)[3];
        for(i = 0; i < 10; ++i)
            loop4(i);
        break;

    case 6:
        memcpy(RIJNDAEL(e_key)+4, in_key+16, 8);
        if(!little_endian())
        {
            selfswap(RIJNDAEL(e_key)[4]);
            selfswap(RIJNDAEL(e_key)[5]);
        }
        t = RIJNDAEL(e_key)[5];
        for(i = 0; i < 8; ++i)
            loop6(i);
        break;

    case 8:
        memcpy(RIJNDAEL(e_key)+4, in_key+16, 16);
        if(!little_endian())
        {
            selfswap(RIJNDAEL(e_key)[4]);
            selfswap(RIJNDAEL(e_key)[5]);
            selfswap(RIJNDAEL(e_key)[7]);
            selfswap(RIJNDAEL(e_key)[8]);
        }
        t = RIJNDAEL(e_key)[7];
        for(i = 0; i < 7; ++i)
            loop8(i);
        break;
    }

    RIJNDAEL(d_key)[0] = RIJNDAEL(e_key)[0]; 
    RIJNDAEL(d_key)[1] = RIJNDAEL(e_key)[1];
    RIJNDAEL(d_key)[2] = RIJNDAEL(e_key)[2]; 
    RIJNDAEL(d_key)[3] = RIJNDAEL(e_key)[3];

    for(i = 4; i < 4 * RIJNDAEL(k_len) + 24; ++i)
    {
        uint32_t u, v, w;
        imix_col(RIJNDAEL(d_key)[i], RIJNDAEL(e_key)[i]);
    }
    return;
}

/*encrypt a block of text*/
#define f_nround(bo, bi, k) \
    f_rn(bo, bi, 0, k);     \
    f_rn(bo, bi, 1, k);     \
    f_rn(bo, bi, 2, k);     \
    f_rn(bo, bi, 3, k);     \
    k += 4

#define f_lround(bo, bi, k) \
    f_rl(bo, bi, 0, k);     \
    f_rl(bo, bi, 1, k);     \
    f_rl(bo, bi, 2, k);     \
    f_rl(bo, bi, 3, k)

static void rijndaelEncrypt(const uint8_t in_blk[16], uint8_t out_blk[16], pRijndael ks)
{
    uint32_t b0[4], b1[4], *kp;
    memcpy(b0, in_blk, 16);
    if(!little_endian())
    {
        selfswap(b0[0]);
        selfswap(b0[1]);
        selfswap(b0[2]);
        selfswap(b0[3]);
    }


    b0[0] ^= RIJNDAEL(e_key)[0];
    b0[1] ^= RIJNDAEL(e_key)[1];
    b0[2] ^= RIJNDAEL(e_key)[2];
    b0[3] ^= RIJNDAEL(e_key)[3];

    kp = RIJNDAEL(e_key) + 4;

    if(RIJNDAEL(k_len) > 6)
    {
        f_nround(b1, b0, kp); 
        f_nround(b0, b1, kp);
    }

    if(RIJNDAEL(k_len) > 4)
    {
        f_nround(b1, b0, kp); 
        f_nround(b0, b1, kp);
    }

    f_nround(b1, b0, kp); 
    f_nround(b0, b1, kp);
    f_nround(b1, b0, kp); 
    f_nround(b0, b1, kp);
    f_nround(b1, b0, kp); 
    f_nround(b0, b1, kp);
    f_nround(b1, b0, kp); 
    f_nround(b0, b1, kp);
    f_nround(b1, b0, kp); 
    f_lround(b0, b1, kp);

    if(!little_endian())
    {
        selfswap(b0[0]);
        selfswap(b0[1]);
        selfswap(b0[2]);
        selfswap(b0[3]);
    }
    memcpy(out_blk, b0, 16);
}

/*decrypt a block of text*/
#define i_nround(bo, bi, k) \
    i_rn(bo, bi, 0, k);     \
    i_rn(bo, bi, 1, k);     \
    i_rn(bo, bi, 2, k);     \
    i_rn(bo, bi, 3, k);     \
    k -= 4

#define i_lround(bo, bi, k) \
    i_rl(bo, bi, 0, k);     \
    i_rl(bo, bi, 1, k);     \
    i_rl(bo, bi, 2, k);     \
    i_rl(bo, bi, 3, k)

static void rijndaelDecrypt(const uint8_t in_blk[16], uint8_t out_blk[16], pRijndael ks)
{
    uint32_t b0[4], b1[4], *kp;

    memcpy(b0, in_blk, 16);
    if(!little_endian())
    {
        selfswap(b0[0]);
        selfswap(b0[1]);
        selfswap(b0[2]);
        selfswap(b0[3]);
    }

    b0[0] ^= RIJNDAEL(e_key)[4 * RIJNDAEL(k_len) + 24];
    b0[1] ^= RIJNDAEL(e_key)[4 * RIJNDAEL(k_len) + 25];
    b0[2] ^= RIJNDAEL(e_key)[4 * RIJNDAEL(k_len) + 26];
    b0[3] ^= RIJNDAEL(e_key)[4 * RIJNDAEL(k_len) + 27];

    kp = RIJNDAEL(d_key) + 4 * (RIJNDAEL(k_len) + 5);

    if(RIJNDAEL(k_len) > 6)
    {
        i_nround(b1, b0, kp); 
        i_nround(b0, b1, kp);
    }

    if(RIJNDAEL(k_len) > 4)
    {
        i_nround(b1, b0, kp); 
        i_nround(b0, b1, kp);
    }

    i_nround(b1, b0, kp); 
    i_nround(b0, b1, kp);
    i_nround(b1, b0, kp); 
    i_nround(b0, b1, kp);
    i_nround(b1, b0, kp); 
    i_nround(b0, b1, kp);
    i_nround(b1, b0, kp); 
    i_nround(b0, b1, kp);
    i_nround(b1, b0, kp); 
    i_lround(b0, b1, kp);

    if(!little_endian())
    {
        selfswap(b0[0]);
        selfswap(b0[1]);
        selfswap(b0[2]);
        selfswap(b0[3]);
    }
    memcpy(out_blk, b0, 16);
}


/**
* CTClib standard interface
*/

static void initAES(byte *key, int size, int triple, void **keysched, size_t *length)
{
    int keys = triple ? 3 : 1;
    int i;
    pRijndael b;

    *length = sizeof(Rijndael)*keys;
    *keysched = zmalloc(*length);
    if(!(*keysched)) return;

    b = (pRijndael) *keysched;

    for(i=0; i < keys; i++)
    {
        makeRijndaelKey(key, size, b+i );
        key+=(size_t)size;
    }
}

void initAES128(byte *key, int triple, void **keysched, size_t *length)
{
    initAES(key, AES128KEYSIZE, triple, keysched, length);
}

void initAES192(byte *key, int triple, void **keysched, size_t *length)
{
    initAES(key, AES192KEYSIZE, triple, keysched, length);
}

void initAES256(byte *key, int triple, void **keysched, size_t *length)
{
    initAES(key, AES256KEYSIZE, triple, keysched, length);
}

void ecbAES(void *keysched, int triple, int encrypt, byte *in, byte *out)
{
    pRijndael b = (pRijndael)keysched;

    if(!triple)
    {
        if(encrypt) rijndaelEncrypt(in, out, b);
        else rijndaelDecrypt(in, out, b);
    }
    else
    {
        if(encrypt)
        {
            rijndaelEncrypt(in, out, &b[0]);
            rijndaelEncrypt(out, in, &b[1]);
            rijndaelEncrypt(in, out, &b[2]);
        }
        else
            {
            rijndaelDecrypt(in, out, &b[2]);
            rijndaelDecrypt(out, in, &b[1]);
            rijndaelDecrypt(in, out, &b[0]);
        }
    }
}

void destroyAES(void **keysched, size_t length)
{
    /* match memory deallocation to allocation locally! */
    if(length>0) zfree(keysched, length);
}



/* ******** END OF CTC MODULE rijndael-api-fst.C ******************************* */

#ifdef TEST

/*
 Enough code to run the above with a couple of know test vectors to ensure
 that 1) it is invertible and 2) it is Twofish
*/

static void * zmalloc(size_t n)
{ 
    void * result = malloc(n);

    if(result) memset(result, 0, n);
    return result; 
}

#include <stdio.h>
#include <abstract.h>

typedef struct testvector_t {
    int i;
    char * key;
    char * plain;
    char * cipher;
}
tv, *ptv;


static int eval(char c)
{
    if('0'<= c && '9' >= c) return c-'0';
    else if('A' <= c && 'F' >= c) return 10+c-'A';
    else if('a' <= c && 'f' >= c) return 10+c-'a';
    return 0;
}

static int textToBytes(const char * text, byte * output)
{
    const char * tt = text;
    int i, n;
    while(*tt)
    {
        int t = tt-text;
        if(!(t%2)) output[t/2] = (byte)(eval(*tt)<<4);
        else output[t/2] |= (byte) eval(*tt);
        ++tt;
    }
    n = (tt-text)/2;
    if(!little_endian())
    {
        for(i=0; i<n; ++i)
        {
            byte tmp = output[i*4];
            output[i*4] = output[3+i*4];
            output[3+i*4] = tmp;

            tmp = output[1+i*4];
            output[1+i*4] = output[2+i*4];
            output[2+i*4] = tmp;
        }
    }
    return n;
}

/* run some standard test vectors */
void doTest(tv * vector, int n)
{
    byte plaintext[MAXBLOCKSIZE];
    byte cipher[MAXBLOCKSIZE];
    byte result[MAXBLOCKSIZE];
    byte key[MAXKEYSIZE];
    int i, j;
    Rijndael ks;


    for(i=0; i<n;++i)
    {
        int size = textToBytes(vector[i].key, key);
        textToBytes(vector[i].plain, plaintext);
        printf("\n%d\n", vector[i].i);

        printf("key=%s length %d\n", vector[i].key, size);

        makeRijndaelKey(key, size, &ks);

        for(j=0; j<10000; ++j)
        {
            rijndaelEncrypt(plaintext, result, &ks);
            memcpy(plaintext, result, MAXBLOCKSIZE);
        }


        printf("plaintext  = %s\n", vector[i].plain);
        printf("ciphertext = ");
        for(j=0; j<AESBLOCKSIZE; ++j) printf("%02x", result[j]);
        printf("\n");
        printf("expect     = %s\n", vector[i].cipher);

        for(j=0; j<10000; ++j)
        {
            rijndaelDecrypt(result, plaintext, &ks);
            memcpy(result, plaintext, MAXBLOCKSIZE);
        }

        printf("invert     = ");
        for(j=0; j<AESBLOCKSIZE; ++j) printf("%02x", plaintext[j]);
        printf("\n");


        textToBytes(vector[i].cipher, cipher);
        for(j=0; j<10000; ++j)
        {
            rijndaelDecrypt(cipher, result, &ks);
            memcpy(cipher, result, MAXBLOCKSIZE);
        }

        printf("decrypt    = ");
        for(j=0; j<AESBLOCKSIZE; ++j) printf("%02x", result[j]);
        printf("\n");

    }
}

/* a subset of the real vectors */
static tv vector128[] = {
    {
        0,
        "00000000000000000000000000000000",
        "00000000000000000000000000000000",
        "c34c052cc0da8d73451afe5f03be297f"     }
    ,
    {
        1,
        "c34c052cc0da8d73451afe5f03be297f",
        "c34c052cc0da8d73451afe5f03be297f",
        "0ac15a9afbb24d54ad99e987208272e2"     }
    ,
    {
        2,
        "c98d5fb63b68c027e88317d8233c5b9d",
        "0ac15a9afbb24d54ad99e987208272e2",
        "a3d43bffa65d0e80092f67a314857870"     }
    ,
    {
        3,
        "6a5964499d35cea7e1ac707b37b923ed",
        "a3d43bffa65d0e80092f67a314857870",
        "355f697e8b868b65b25a04e18d782afa"     }
    ,
};

static tv vector192[] = {
    {
        0,
        "000000000000000000000000000000000000000000000000",
        "00000000000000000000000000000000",
        "f3f6752ae8d7831138f041560631b114"     }
    ,
    {
        1,
        "aafe47ee82411a2bf3f6752ae8d7831138f041560631b114",
        "f3f6752ae8d7831138f041560631b114",
        "77ba00ed5412dff27c8ed91f3c376172"     }
    ,
    {
        2,
        "a92b07597b52873c844c75c7bcc55ce3447e98493a06d066",
        "77ba00ed5412dff27c8ed91f3c376172",
        "2d92de893574463412bd7d121a94952f"     }
    ,
    {
        3,
        "5fd632da76165edba9deab4e89b11ad756c3e55b20924549",
        "2d92de893574463412bd7d121a94952f",
        "96650f835912f5e748422727802c6ce1"     }
    ,
};

static tv vector256[] = {
    {
        0,
        "0000000000000000000000000000000000000000000000000000000000000000",
        "00000000000000000000000000000000",
        "8b79eecc93a0ee5dff30b4ea21636da4"     }
    ,
    {
        1,
        "ad3965683e6fa98b5f38ac26653679288b79eecc93a0ee5dff30b4ea21636da4",
        "8b79eecc93a0ee5dff30b4ea21636da4",
        "c737317fe0846f132b23c8c2a672ce22"     }
    ,
    {
        2,
        "28e79e2afc5f7745fccabe2f6257c2ef4c4edfb37324814ed4137c288711a386",
        "c737317fe0846f132b23c8c2a672ce22",
        "e58b82bfba53c0040dc610c642121168"     }
    ,
    {
        3,
        "0721e93eacf9dc6c870d8133376b7c0da9c55d0cc977414ad9d56ceec503b2ee",
        "e58b82bfba53c0040dc610c642121168",
        "10b296abb40504995db71dda0b7e26fb"     }
    ,
};

int main()
{
    int n = sizeof(vector128)/sizeof(vector128[0]);
    doTest(vector128, n);
    n = sizeof(vector192)/sizeof(vector192[0]);
    doTest(vector192, n);
    n = sizeof(vector256)/sizeof(vector256[0]);
    doTest(vector256, n);
    return 0;
}

#endif

/* EOF rijndael.c */

