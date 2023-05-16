/* Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1996
   All rights reserved.  For full licence details see file licences.c
    
        The 3-Way cypher, inspired, inter alia, by
 code from Schneier's _Applied Cryptography_ 2nd edition

 Standard test vectors ported to API format, and tentative hash
 test vectors; also change of integer types to proposed uint##_t
 Mr. Tines 8-Jan-97

   Localise keyschedule deallocation and hash-vector deallocation with
   the allocation.  Mr. Tines 16-Feb-97

   In hash3WayFinal, replace "&& 0xFF" with the intended "& 0xFF" which
   extracts byte-slices from the 64-bit count of bits processed, rather
   than just equation to 1 for every non-zero byte.  Change the test
   vector output to match the new results, and add the test vectors
   from SHA "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq"
   and a million of the letter "a", and their results.
   Mr. Tines 15-Aug-1997

*/
/*--------------------------------------------------------------------*/

#include "3way.h"
#include "utils.h"
#include <string.h>

#define STRT_E 0x0b0b /* round constant for encrypt round 1 */
#define STRT_D 0xb1b1 /* ditto decrypt */
#define ROUNDS 11 /* as recommended */

typedef struct TWAY_KEYSCHED_t {
    uint32_t givenKey[3];
    uint32_t inverseKey[3];
    uint32_t encryptRoundConstants[ROUNDS+1];
    uint32_t decryptRoundConstants[ROUNDS+1];
}
TWAY_KEYSCHED;

/*---------------------------------------------------------------------*/
/* start of 3way specific code */
/* primitive operations within the algorithm */
static void mu(uint32_t *a) /* bitwise reverse 96 bits */
{
    uint32_t b[3];
    int i, j;

    /* reverse within each uint32_t */
    for(i=0; i<3; i++)
    {
        b[i]=0;
        for(j=0; j<32; j++)
        {
            b[i] <<= 1;
            if(a[i]&1) b[i] |=1;
            a[i] >>= 1;
        }
    }
    /* reverse the WORD32s */
    for(i=0;i<3;i++) a[i] = b[2-i];
}

#define ADD1(i) ((i+1)%3)
#define ADD2(i) ((i+2)%3)

static void gamma(uint32_t *a) /* non-linear mixing */
{
    uint32_t b[3];
    int i;
    for(i=0; i<3; i++)
        b[i] = a[i] ^ (a[ADD1(i)] |(~a[ADD2(i)]));
    for(i=0; i<3; i++)
        a[i] = b[i];
}

static void theta(uint32_t *a) /* linear mixing */
{
    uint32_t b[3];
    int i;
    for(i=0; i<3; i++)
    {
        b[i] = a[i] ^
            (a[i]>>16) ^ (a[ADD1(i)]<<16) ^
            (a[ADD1(i)]>>16) ^ (a[ADD2(i)]<<16) ^
            (a[ADD1(i)]>>24) ^ (a[ADD2(i)]<<8); 
        /* This expression is too much for Symantec C++ 8.5; it has to be split in two */
        b[i] = b[i] ^ 
            (a[ADD2(i)]>> 8) ^ (a[i]<<24) ^
            (a[ADD2(i)]>>16) ^ (a[i]<<16) ^
            (a[ADD2(i)]>>24) ^ (a[i]<<8);
    }
    for(i=0; i<3; i++)
        a[i] = b[i];
}

static void pi1(uint32_t *a) /* first permutation */
{
    a[0] = (a[0]>>10) ^ (a[0]<<22);
    a[2] = (a[2]<<1) ^ (a[2]>>31);
}

static void pi2(uint32_t *a) /* second permutation */
{
    a[2] = (a[2]>>10) ^ (a[2]<<22);
    a[0] = (a[0]<<1) ^ (a[0]>>31);
}

static void tway_round(uint32_t *a) /* the round function */
{
    theta(a); 
    pi1(a); 
    gamma(a); 
    pi2(a);
}

/* set up round constants */
static void tway_generate_const(uint32_t init, uint32_t *table)
{
    int i;
    for(i=0; i<=ROUNDS; i++)
    {
        table[i] = init;
        init <<= 1;
        if(init & 0x10000L) init ^= 0x11011L;
    }
}

/* one block forward pass */
static void Tway_encipher(TWAY_KEYSCHED *k, uint32_t *a)
{
    int i;
    for(i=0; i<=ROUNDS; i++)
    {
        a[0] ^= k->givenKey[0] ^
            (k->encryptRoundConstants[i]<<16);
        a[1] ^= k->givenKey[1];
        a[2] ^= k->givenKey[2] ^
            k->encryptRoundConstants[i];
        if(i<ROUNDS)tway_round(a);
        else theta(a);
    }
}

/* one block inverted */
static void Tway_decipher(TWAY_KEYSCHED *k, uint32_t *a)
{
    int i;
    mu(a);
    for(i=0; i<=ROUNDS; i++)
    {
        a[0] ^= k->inverseKey[0] ^
            (k->decryptRoundConstants[i]<<16);
        a[1] ^= k->inverseKey[1];
        a[2] ^= k->inverseKey[2] ^
            k->decryptRoundConstants[i];
        if(i<ROUNDS)tway_round(a);
        else theta(a);
    }
    mu(a);
}

/* user 96 bit key expansion into key schedule */
static void Tway_Key_Init(TWAY_KEYSCHED *k, uint32_t *a)
{
    int i;
    for(i=0; i<3;i++) k->inverseKey[i] =
        k->givenKey[i] = a[i];
    theta(k->inverseKey);
    mu(k->inverseKey);
    tway_generate_const((uint32_t)STRT_E, k->encryptRoundConstants);
    tway_generate_const((uint32_t)STRT_D, k->decryptRoundConstants);
}
/* end 3-way specific code - begin interface and test code */

void init3WAY(byte *key, int triple, void **keysched, size_t *length)
{
    uint32_t a[TWAYKEYSIZE/4];
    int i, k;
    int keys = triple ? 3 : 1;
    TWAY_KEYSCHED *ks;

    *length = sizeof(TWAY_KEYSCHED);
    if(triple) (*length) *= 3;

    *keysched = zmalloc(*length);
    if(!(*keysched)) return;

    ks = *keysched;

    for(k=0; k < keys; k++, ks++)
    {
        /* assume key data is MSB first */
        for(i=0; i<(TWAYKEYSIZE/4); i++)
        {
            a[i] = ((uint32_t)key[0]<<24) + ((uint32_t)key[1]<<16) +
                ((uint32_t)key[2]<<8) + (uint32_t)key[3];
            key+=4;
        }
        Tway_Key_Init(ks, a);
    }

    /* purge sensitive info */
    for(i=0; i<(TWAYKEYSIZE/4); i++)
        a[i] = 0;

}

void ecb3WAY(void *keysched, int triple, int encrypt, byte *in, byte *out)
{
    int keys = (triple) ? 3 : 1;
    int i;
    TWAY_KEYSCHED *ks = (TWAY_KEYSCHED*)keysched;
    uint32_t a[TWAYBLOCKSIZE/4];

    /* reduce data from MSB-first form */
    for(i=0; i<(TWAYBLOCKSIZE/4); i++)
    {
        a[i] = ((uint32_t)in[0]<<24) + ((uint32_t)in[1]<<16) +
            ((uint32_t)in[2]<<8) + (uint32_t)in[3];
        in+=4;
    }

    if(triple && (!encrypt)) ks += 2;

    for(i=0; i<keys; i++)
    {
        if(encrypt)
        {
            Tway_encipher(ks, a);
            ks++;
        }
        else
        {
            Tway_decipher(ks, a);
            ks--;
        }
    }

    /* restore endianness - If I've got the theory right! */
    for(i=0; i<(TWAYBLOCKSIZE/4); i++)
    {
        out[0] = (byte)((a[i]>>24) & 0xFF);
        out[1] = (byte)((a[i]>>16) & 0xFF);
        out[2] = (byte)((a[i]>>8) & 0xFF);
        out[3] = (byte) (a[i] & 0xFF);
        out+=4;
    }
}

void destroy3WAY(void **keysched, size_t length)
{
    /* match memory deallocation to allocation locally! */
    if(length>0) zfree(keysched, length);
}

/*-----------------------------------------------------------------------*/
/* Routines to support a simple secure hash based on 3-Way, with MD
 strengthening as per MD5.  The algorithm is the first from Table 18.1
 of Schneier, H[i] = E<H[i-1]>(M[i]) ^ M[i]
*/

static void hash(byte h[TWAYKEYSIZE], byte m[TWAYBLOCKSIZE])
{
    void *keysched;
    size_t len, i;
    byte work[TWAYBLOCKSIZE];

    init3WAY(h, FALSE, &keysched, &len);
    ecb3WAY(keysched, FALSE, TRUE, m, work);
    zfree(&keysched, len);

    for(i=0; i<TWAYKEYSIZE; i++)
        h[i] = work[i] ^ m[i];
}

void hash3WayInit(Hash3Way **md, size_t *length)
{
    byte *buf;

    *length = sizeof(Hash3Way);
    *md = zmalloc(*length);
    if(!(*md)) return;

    /* Assign magic numbers to start the hash - taken from Blowfish S-boxes */
    buf = (*md)->buf;
    buf[0]=0xd1; 
    buf[1]=0x31; 
    buf[2]=0x0b; 
    buf[3]=0xa6;
    buf[4]=0x98; 
    buf[5]=0xdf; 
    buf[6]=0xb5; 
    buf[7]=0xac;
    buf[8]=0x2f; 
    buf[9]=0xfd;
    buf[10]=0x72;
    buf[11]=0xdb;
    /* initialise the count */
    (*md)->bits[0]= (*md)->bits[1]= (*md)->over=0;
}

void hash3WayUpdate(Hash3Way *md, byte *buf, uint32_t len)
{
    uint32_t t = md->bits[0];
    if ((md->bits[0] = t + ((uint32_t) len << 3)) < t)
        md->bits[1]++; /* carry */
    md->bits[1] += len>>29;

    /* handle any left-over bytes in scratch space */
    t = md->over;
    if(t)
    {
        byte *p = (md->in)+(size_t)t; /* just after old data */
        t = TWAYBLOCKSIZE - t; /* space left */
        /* if there's still not enough, exit */
        if(len < t) {
            memcpy(p, buf, (size_t)len); 
            return;
        }

        memcpy(p, buf, (size_t)t);
        hash(md->buf, md->in);

        buf += (size_t)t;
        len -= t;
    }

    /* the body of the input data */
    for(;len >= TWAYBLOCKSIZE;
 buf+=TWAYBLOCKSIZE, len-=TWAYBLOCKSIZE)
            hash(md->buf, buf);
    /* tuck left-overs away */
    md->over = (int) len;
    if(len) memcpy(md->in, buf, (size_t)len);
}

void hash3WayFinal(Hash3Way **pmd, byte digest[TWAYHASHSIZE], size_t length)
{
    Hash3Way *md = *pmd;
    unsigned count = md->over;
    byte *p = md->in + count;

    /* there is always a free byte by construction */
    *p++ = 0x80;
    count = TWAYBLOCKSIZE - (count+1); /* free bytes in buffer */

    if(count<8) /* not enough space for the count */
    {
        memset(p,0,count);
        hash(md->buf, md->in); /*so hash and start again*/
        memset(md->in, 0, TWAYBLOCKSIZE-8);
    }
    else memset(p, 0, count-8);

    /* pack high byte first */
    md->in[4] = (byte)((md->bits[1] >> 24) & 0xFF);
    md->in[5] = (byte)((md->bits[1] >> 16) & 0xFF);
    md->in[6] = (byte)((md->bits[1] >> 8) & 0xFF);
    md->in[7] = (byte)((md->bits[1] ) & 0xFF);

    md->in[8] = (byte)((md->bits[0] >> 24) & 0xFF);
    md->in[9] = (byte)((md->bits[0] >> 16) & 0xFF);
    md->in[10]= (byte)((md->bits[0] >> 8) & 0xFF);
    md->in[11]= (byte)((md->bits[0] ) & 0xFF);

    hash(md->buf, md->in);
    memcpy(digest, md->buf, TWAYHASHSIZE);

    /* burn this information */
    if(length > 0) zfree((void**)pmd, length);
}

/*-----------------------------------------------------------------------*/
/* semi-generic routines - simply rattle down a buffer for testing*/
/* note 96 bit block size is only real 3-way specific bit */
#ifdef TEST
void Tway_ECB_encrypt(TWAY_KEYSCHED *k, uint32_t *data, int blocks)
{
    uint32_t *ptr = data;
    int i=blocks;
    while(i)
    {
        Tway_encipher(k, ptr);
        ptr += 3;
        i--;
    }
}

void Tway_ECB_decrypt(TWAY_KEYSCHED *k, uint32_t *data, int blocks)
{
    uint32_t *ptr = data;
    int i=blocks;
    while(i)
    {
        Tway_decipher(k, ptr);
        ptr += 3;
        i--;
    }
}

/*-----------------------------------------------------------------------*/
/* test harness */
#include <stdio.h>

static void printvec(char *text, uint32_t *vector)
{
    printf("%20s : %08lx %08lx %08lx \n",
    text, vector[2], vector[1], vector[0]);
}

main ()
{
    /* ideally, these would be malloc()d and destoryed */
    TWAY_KEYSCHED ks;

    /* locals */
    uint32_t userKey[3], data[9];
    int i;

    /* test 1 */
    for(i=0;i<3;i++)
    {
        userKey[i] = 0;
        data[i] = 1;
    }

    Tway_Key_Init(&ks, userKey);

    printf("Test 1********\n");
    printvec("KEY = ", userKey);
    printvec("PLAIN = ", data);
    Tway_encipher(&ks, data);
    printvec("CIPHER = ", data);
    Tway_decipher(&ks, data);
    printvec("RECOVERED = ", data);

    /* test 2 */
    for(i=0;i<3;i++)
    {
        userKey[i] = 6-i;
        data[i] = 3-i;
    }

    Tway_Key_Init(&ks, userKey);

    printf("Test 2********\n");
    printvec("KEY = ", userKey);
    printvec("PLAIN = ", data);
    Tway_encipher(&ks, data);
    printvec("CIPHER = ", data);
    Tway_decipher(&ks, data);
    printvec("RECOVERED = ", data);

    /* test 3 */
    userKey[2] = 0xbcdef012L;
    userKey[1] = 0x456789abL;
    userKey[0] = 0xdef01234L;
    data[2] = 0x01234567L;
    data[1] = 0x9abcdef0L;
    data[0] = 0x23456789L;

    Tway_Key_Init(&ks, userKey);

    printf("Test 3********\n");
    printvec("KEY = ", userKey);
    printvec("PLAIN = ", data);
    Tway_encipher(&ks, data);
    printvec("CIPHER = ", data);
    Tway_decipher(&ks, data);
    printvec("RECOVERED = ", data);

    /* test 4 */
    userKey[2] = 0xcab920cdL;
    userKey[1] = 0xd6144138L;
    userKey[0] = 0xd2f05b5eL;
    data[2] = 0xad21ecf7L;
    data[1] = 0x83ae9dc4L;
    data[0] = 0x4059c76eL;

    Tway_Key_Init(&ks, userKey);

    printf("Test 4********\n");
    printvec("KEY = ", userKey);
    printvec("PLAIN = ", data);
    Tway_encipher(&ks, data);
    printvec("CIPHER = ", data);
    Tway_decipher(&ks, data);
    printvec("RECOVERED = ", data);

    /* block test */
    for(i=0; i<9; i++) data[i] = i;
    for(i=0; i<9; i+=3)
        printf("Block %0d set to %08lx %08lx %08lx\n",
        i/3,data[i],data[i+1],data[i+2]);

    Tway_ECB_encrypt(&ks, data, 3);
    for(i=0; i<9; i+=3)
        printf("Block %0d encrypts to %08lx %08lx %08lx\n",
        i/3,data[i],data[i+1],data[i+2]);

    Tway_ECB_decrypt(&ks, data, 3);
    for(i=0; i<9; i+=3)
        printf("Block %0d decrypts to %08lx %08lx %08lx\n",
        i/3,data[i],data[i+1],data[i+2]);


    /* Packaged API tests */
    printf("API tests\n");
    {
        byte key[TWAYKEYSIZE];
        byte in[TWAYBLOCKSIZE], out[TWAYBLOCKSIZE];
        byte savekey[TWAYKEYSIZE];
        byte savetext[TWAYBLOCKSIZE];
        size_t length;
        void *keysched;

        for(i=0; i<TWAYKEYSIZE; i++) key[i] = 0;
        for(i=0; i<TWAYBLOCKSIZE; i++) in[i]=0;
        in[3] = in[7] = in[11] = 1;

        init3WAY(key, (int)0, &keysched, &length);
        ecb3WAY(keysched, (int)0, (int)1, in, out);
        destroy3WAY(&keysched, length);

        for(i=0; i<TWAYBLOCKSIZE; i++)
        {
            printf("%02x", out[i+8*(1 - (i/4))]);
            if(3 == i%4) printf(" ");
            savetext[i] = out[i];
        }
        printf("\n");
        printf("ad21ecf7 83ae9dc4 4059c76e is expected result\n");
        /***/
        for(i=0; i<TWAYKEYSIZE; i++) key[i] = 0;
        key[3] = 6; 
        key[7] = 5; 
        key[11] = 4;
        for(i=0; i<TWAYBLOCKSIZE; i++) in[i]=0;
        in[3] = 3; 
        in[7] = 2; 
        in[11] = 1;

        init3WAY(key, (int)0, &keysched, &length);
        ecb3WAY(keysched, (int)0, (int)1, in, out);
        destroy3WAY(&keysched, length);

        for(i=0; i<TWAYBLOCKSIZE; i++)
        {
            printf("%02x", out[i+8*(1 - (i/4))]);
            if(3 == i%4) printf(" ");
            savekey[i] = out[i];
        }
        printf("\n");
        printf("cab920cd d6144138 d2f05b5e is expected result\n");
        /***/
        key[8] = 0xbc; 
        key[9] = 0xde; 
        key[10] = 0xf0; 
        key[11] = 0x12;
        key[4] = 0x45; 
        key[5] = 0x67; 
        key[6] = 0x89; 
        key[7] = 0xab;
        key[0] = 0xde; 
        key[1] = 0xf0; 
        key[2] = 0x12; 
        key[3] = 0x34;

        in[8] = 0x01; 
        in[9] = 0x23; 
        in[10] = 0x45; 
        in[11] = 0x67;
        in[4] = 0x9a; 
        in[5] = 0xbc; 
        in[6] = 0xde; 
        in[7] = 0xf0;
        in[0] = 0x23; 
        in[1] = 0x45; 
        in[2] = 0x67; 
        in[3] = 0x89;

        init3WAY(key, (int)0, &keysched, &length);
        ecb3WAY(keysched, (int)0, (int)1, in, out);
        destroy3WAY(&keysched, length);

        for(i=0; i<TWAYBLOCKSIZE; i++)
        {
            printf("%02x", out[i+8*(1 - (i/4))]);
            if(3 == i%4) printf(" ");
        }
        printf("\n");
        printf("7cdb76b2 9cdddb6d 0aa55dbb is expected result\n");
        /***/
        for(i=0; i<TWAYKEYSIZE; i++) key[i] = savekey[i];
        for(i=0; i<TWAYBLOCKSIZE; i++) in[i] = savetext[i];

        init3WAY(key, (int)0, &keysched, &length);
        ecb3WAY(keysched, (int)0, (int)1, in, out);
        destroy3WAY(&keysched, length);

        for(i=0; i<TWAYBLOCKSIZE; i++)
        {
            printf("%02x", out[i+8*(1 - (i/4))]);
            if(3 == i%4) printf(" ");
        }
        printf("\n");
        printf("15b155ed 6b13f17c 478ea871 is expected result\n");


    }


    /* Hash mode test */
    printf("Hash mode test\n");
    {
        Hash3Way *digest;
        size_t length;
        byte output[TWAYHASHSIZE];

        hash3WayInit(&digest, &length);
        hash3WayUpdate(digest, (byte*)"abc", (uint32_t)3);
        hash3WayFinal(&digest, output, length);

        printf("\nabc\n");
        for(i=0; i<TWAYHASHSIZE; i++)
        {
            printf("%02x", output[i]);
            if(3 == i%4) printf(" ");
        }
        printf("\n");
        printf("8d19d8bd 5dd7e47f 18e0b785  expected\n");
        /* result when && rather than & in hash3WayFinal() */
        /*printf("efb40963 0231ed3b e8a4e69e is test result\n");*/

        hash3WayInit(&digest, &length);
        hash3WayUpdate(digest,
        (byte*)"The quick brown fox jumps over the lazy dog.", (uint32_t)44);
        hash3WayFinal(&digest, output, length);

        printf("\nThe quick brown fox jumps over the lazy dog.\n");
        for(i=0; i<TWAYHASHSIZE; i++)
        {
            printf("%02x", output[i]);
            if(3 == i%4) printf(" ");
        }
        printf("\n");
        printf("f1c8c367 4d4c4ea0 93a53875 expected\n");
        /* result when && rather than & in hash3WayFinal() */
        /*printf("8a17ccb1 b60666be 5a446ebd is test result\n");*/


        hash3WayInit(&digest, &length);
        hash3WayUpdate(digest,
        (byte*)"Vext cwm fly zing! jabs Kurd qoph.", (uint32_t)34);
        hash3WayFinal(&digest, output, length);

        printf("\nVext cwm fly zing! jabs Kurd qoph.\n");
        for(i=0; i<TWAYHASHSIZE; i++)
        {
            printf("%02x", output[i]);
            if(3 == i%4) printf(" ");
        }
        printf("\n");
        printf("2f730c5b 49941218 20542151 expected\n");
        /* result when && rather than & in hash3WayFinal() */
        /*printf("4f88508a ffec0118 5c7457b4 is test result\n");*/


        hash3WayInit(&digest, &length);
        hash3WayUpdate(digest,
        (byte*)"abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq",
        (uint32_t)56);
        hash3WayFinal(&digest, output, length);

        printf("\nabcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq\n");
        for(i=0; i<TWAYHASHSIZE; i++)
        {
            printf("%02x", output[i]);
            if(3 == i%4) printf(" ");
        }
        printf("\n");
        printf("21a88aea 2ca7bf3d 19d9fb67 expected\n");

        hash3WayInit(&digest, &length);
        {
            byte a[8192];
            long count;
            long as = 0;

            for(i=0;i<8192;i++) a[i] = 'a';

            printf("\n");
            for(count=1000000L; count > 0; count -=8192)
            {
                int l = (count > 8192) ? 8192 : (int) count;
                hash3WayUpdate(digest, a, l);
                as += l;
                fprintf(stderr, "%d \"a\"s\r", as);
            }
            printf("1000000 \"a\"s\n");
        }
        hash3WayFinal(&digest, output, length);

        for(i=0; i<TWAYHASHSIZE; i++)
        {
            printf("%02x", output[i]);
            if(3 == i%4) printf(" ");
        }
        printf("\n");
        printf("3c05ae8f b7176193 7c04348d expected\n");

    }
    return 0;

}

#endif
/* end of file 3-way.c */
