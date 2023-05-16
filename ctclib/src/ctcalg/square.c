/* square.c
 * This implements the Square block cipher.
 *
 * <P>
 * <b>References</b>
 *
 * <P>
 * The Square algorithm was developed by
 * <a href="mailto:Daemen.J@banksys.com">Joan Daemen</a>
 * and <a href="mailto:vincent.rijmen@esat.kuleuven.ac.be">Vincent Rijmen</a>,
 * and is in the public domain.
 *
 * See
 *      J. Daemen, L.R. Knudsen, V. Rijmen,
 *      "The block cipher Square,"
 *      <cite>Fast Software Encryption Haifa Security Workshop Proceedings</cite>,
 *      LNCS, E. Biham, Ed., Springer-Verlag, to appear.
 *
 * <P>
 * This 'C' code is based upon the public domain Java implementation
 * written by <a href="mailto:pbarreto@nw.com.br">Paulo S.L.M. Barreto</a>
 * based on C software originally written by Vincent Rijmen.
 *
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> August 1997
**  All rights reserved.  For full licence details see file licences.c
*/

#include "square.h"
#include "utils.h"
#include <string.h>

/*Number of rounds*/
#define R 8
/* Number of uint32_t's in an expanded key */
#define KEYINTS 4*(R+1)
/* Size of a block (or key) in uint32_t's */
#define BLOCKINTS SQUAREBLOCKSIZE/4

static boolean initialised=FALSE;
typedef struct {
    uint8_t log[256];
    uint8_t exp[256];
    uint32_t Se[256]; /* needs only 8, but trade space for */
    uint32_t Sd[256]; /* not having to duplicate the reound() function */
    uint8_t offset[R];
    uint32_t Te[256];
    uint32_t Td[256];
    uint32_t phi[256];
}
sqtable;
static sqtable sqtab;
#define ROOT 0x1f5

typedef struct {
    uint32_t roundKeys_e[KEYINTS];
    uint32_t roundKeys_d[KEYINTS];
}
Square, *pSquare;

#define KEYSCHEDLEN sizeof(Square)

static uint32_t mul (uint8_t a, uint8_t b)/* multiply two elements of GF(2**8)*/
{
    int temp;
    if(a==0 || b==0) return 0;
    temp = sqtab.log[a]+sqtab.log[b];
    if(temp >= 255) temp -= 255;
    return (uint32_t) sqtab.exp[ (uint8_t)(temp&0xFF) ];
}

static uint32_t sqRotr (uint32_t x, int s)
{
    return (x >> s) | (x << (32 - s));
}

static uint32_t sqRotl (uint32_t x, int s)
{
    return (x << s) | (x >> (32 - s));
}

/* apply the theta function to a round key: */
static void transform (uint32_t *roundKey)
{
    uint32_t *phi = sqtab.phi;
    roundKey[0] = phi[(size_t)((roundKey[0] ) & 0xff)] ^
        sqRotl (phi[(size_t)((roundKey[0] >> 8) & 0xff)], 8) ^
        sqRotl (phi[(size_t)((roundKey[0] >> 16) & 0xff)], 16) ^
        sqRotl (phi[(size_t)((roundKey[0] >> 24) & 0xff)], 24);
    roundKey[1] = phi[(size_t)((roundKey[1] ) & 0xff)] ^
        sqRotl (phi[(size_t)((roundKey[1] >> 8) & 0xff)], 8) ^
        sqRotl (phi[(size_t)((roundKey[1] >> 16) & 0xff)], 16) ^
        sqRotl (phi[(size_t)((roundKey[1] >> 24) & 0xff)], 24);
    roundKey[2] = phi[(size_t)((roundKey[2] ) & 0xff)] ^
        sqRotl (phi[(size_t)((roundKey[2] >> 8) & 0xff)], 8) ^
        sqRotl (phi[(size_t)((roundKey[2] >> 16) & 0xff)], 16) ^
        sqRotl (phi[(size_t)((roundKey[2] >> 24) & 0xff)], 24);
    roundKey[3] = phi[(size_t)((roundKey[3] ) & 0xff)] ^
        sqRotl (phi[(size_t)((roundKey[3] >> 8) & 0xff)], 8) ^
        sqRotl (phi[(size_t)((roundKey[3] >> 16) & 0xff)], 16) ^
        sqRotl (phi[(size_t)((roundKey[3] >> 24) & 0xff)], 24);
}


static void computeTables(void)
{
    int i,t;
    uint8_t trans[8] = {
        0x01, 0x03, 0x05, 0x0f, 0x1f, 0x3d, 0x7b, 0xd6     };
    /* diffusion and inverse diffusion polynomials:
            * by definition (cf. "The block cipher Square", section 2.1),
            * c(x)d(x) = 1 (mod 1 + x**4)
            * where the polynomial coefficients are taken from GF(2**8);
            * the actual polynomial and its inverse are:
            * c(x) = 3.x**3 + 1.x**2 + 1.x + 2
            * d(x) = B.x**3 + D.x**2 + 9.x + E
            */
    uint8_t c[4] = {
        0x2, 0x1, 0x1, 0x3     };
    uint8_t d[4] = {
        0xE, 0x9, 0xD, 0xB     };
    /*set up logs and anti-logs on GF(2^8)*/
    sqtab.exp[0] = sqtab.exp[255] = 1;
    sqtab.log[1] = sqtab.log[0] = 0;

    for(i=1; i<255; ++i)
    {
        uint32_t j = sqtab.exp[i-1]<<1;
        if(j>0xFF) j^=ROOT; /* reduce mod ROOT */
        sqtab.exp[i] = (uint8_t)(j&0xFF);
        sqtab.log[sqtab.exp[i]] = (uint8_t)(i&0xFF);
    }

    /* compute the substitution box Se[] and its inverse Sd[]
            * based on F(x) = x**{-1} plus affine transform of the output
            */
    sqtab.Se[0] = 0;
    for ( i = 1; i < 256; i++)
    {
        sqtab.Se[i] = sqtab.exp[(255 - sqtab.log[i])&0xFF];
        /* Se[i] = i^{-1}, i.e. mul(Se[i], i) == 1 */
    }
    /* the selection criterion for the actual affine transform is that
            * the bit matrix corresponding to its linear has a triangular structure:
              0x01     00000001
              0x03     00000011
              0x05     00000101
              0x0f     00001111
              0x1f     00011111
              0x3d     00111101
              0x7b     01111011
              0xd6     11010110
            */
    for (i = 0; i < 256; i++)
        /* let Se[i] be represented as an 8-row vector V over GF(2);
                        * the affine transformation is A*V + T, where the rows of
                        * the 8x8 matrix A are contained in trans[0]...trans[7] and
                        * the 8-row vector T is contained in trans[8] above.
                        */
    {
        uint32_t v = 0xb1; /* this is the affine part of the transform */
        int t;
        for (t = 0; t < 8; t++)
        {
            /* column-wise multiplication over GF(2): */
            uint32_t u = sqtab.Se[i] & trans[t];
            /* sum over GF(2) of all bits of u: */
            u ^= u >> 4; 
            u ^= u >> 2; 
            u ^= u >> 1; 
            u &= 1;
            /* row alignment of the result: */
            v ^= u << t;
        }
        sqtab.Se[i] = (uint8_t)(v&0xFF);
        sqtab.Sd[(size_t)v] = (uint8_t)(i&0xFF); /* inverse substitution box */
    }

    /* substitution/diffusion layers and key schedule transform:
                 */
    for (t = 0; t < 256; t++)
    {
        uint8_t v;
        sqtab.phi[t] =
            (mul ((uint8_t)t, c[3]) << 24) |
            (mul ((uint8_t)t, c[2]) << 16) |
            (mul ((uint8_t)t, c[1]) << 8) |
            (mul ((uint8_t)t, c[0]));
        v = (uint8_t) sqtab.Se[t];
        sqtab.Te[t] = (sqtab.Se[t & 3] == 0) ? 0 :
        (mul (v, c[3]) << 24) |
            (mul (v, c[2]) << 16) |
            (mul (v, c[1]) << 8) |
            (mul (v, c[0]));
        v = (uint8_t) sqtab.Sd[t];
        sqtab.Td[t] = (sqtab.Sd[t & 3] == 0) ? 0 :
        (mul (v, d[3]) << 24) |
            (mul (v, d[2]) << 16) |
            (mul (v, d[1]) << 8) |
            (mul (v, d[0]));
    }
    /* offset table */
    sqtab.offset[0] = 0x1;
    for (i = 1; i < R; ++i)
    {
        sqtab.offset[i] = (uint8_t) mul (sqtab.offset[i - 1], 0x2);
    }
    initialised = TRUE;
}

/**
 * This creates a Square block cipher from a byte array user key.
 * @param key   The 128-bit user key.
 */
static void setupSquare(byte *key, pSquare s)
{
    int i, t;

    if(!initialised) computeTables();
    /* map user key to first round key: */
    for (i = 0; i < 16; i += 4)
    {
        s->roundKeys_e[i >> 2] =
            (((uint32_t)key[i ] & 0xff)) |
            (((uint32_t)key[i + 1] & 0xff) << 8) |
            (((uint32_t)key[i + 2] & 0xff) << 16) |
            (((uint32_t)key[i + 3] & 0xff) << 24);
    }
    for (t = 1; t <= R; ++t)
    {
        /* apply the key evolution function: */
        s->roundKeys_d[(R-t)*4+0] = s->roundKeys_e[t*4+0]
            = s->roundKeys_e[(t-1)*4+0] ^ sqRotr (s->roundKeys_e[(t-1)*4+3], 8)
            ^ sqtab.offset[t-1];
        s->roundKeys_d[(R-t)*4+1] = s->roundKeys_e[t*4+1]
            = s->roundKeys_e[(t-1)*4+1] ^ s->roundKeys_e[t*4+0];
        s->roundKeys_d[(R-t)*4+2] = s->roundKeys_e[t*4+2]
            = s->roundKeys_e[(t-1)*4+2] ^ s->roundKeys_e[t*4+1];
        s->roundKeys_d[(R-t)*4+3] = s->roundKeys_e[t*4+3]
            = s->roundKeys_e[(t-1)*4+3] ^ s->roundKeys_e[t*4+2];
        /* apply the theta diffusion function:*/
        transform (s->roundKeys_e+(t-1)*4);
    }
    for (i = 0; i < 4; i++)
    {
        s->roundKeys_d[R*4+i] = s->roundKeys_e[i];
    }

}


/**
 * The round function to transform an intermediate data block
 * <code>block</code> with the substitution-diffusion table <code>T</code>
 * and the round key <code>roundKey</code>
 * @param   block       the data block
 * @param   T           the substitution-diffusion table
 * @param   roundKey    the 128-bit round key
*/
#ifdef __BORLANDC__
#pragma warn -aus
#endif
static void round (uint32_t *block, uint32_t *T, uint32_t *roundKey)
{
    uint32_t t0, t1, t2, t3;

    t0 = block[0];
    t1 = block[1];
    t2 = block[2];
    t3 = block[3];

    block[0] = T[(size_t)((t0 ) & 0xff)]
        ^ sqRotl (T[(size_t)((t1 ) & 0xff)], 8)
        ^ sqRotl (T[(size_t)((t2 ) & 0xff)], 16)
            ^ sqRotl (T[(size_t)((t3 ) & 0xff)], 24)
                ^ roundKey[0];
    block[1] = T[(size_t)((t0 >> 8) & 0xff)]
        ^ sqRotl (T[(size_t)((t1 >> 8) & 0xff)], 8)
        ^ sqRotl (T[(size_t)((t2 >> 8) & 0xff)], 16)
            ^ sqRotl (T[(size_t)((t3 >> 8) & 0xff)], 24)
                ^ roundKey[1];
    block[2] = T[(size_t)((t0 >> 16) & 0xff)]
        ^ sqRotl (T[(size_t)((t1 >> 16) & 0xff)], 8)
        ^ sqRotl (T[(size_t)((t2 >> 16) & 0xff)], 16)
            ^ sqRotl (T[(size_t)((t3 >> 16) & 0xff)], 24)
                ^ roundKey[2];
    block[3] = T[(size_t)((t0 >> 24) & 0xff)]
        ^ sqRotl (T[(size_t)((t1 >> 24) & 0xff)], 8)
        ^ sqRotl (T[(size_t)((t2 >> 24) & 0xff)], 16)
            ^ sqRotl (T[(size_t)((t3 >> 24) & 0xff)], 24)
                ^ roundKey[3];

    /* destroy potentially sensitive information: */
    t0 = t1 = t2 = t3 = 0;
}
#ifdef __BORLANDC__
#pragma warn .aus
#endif

/**
 * Encrypt a block.
 * The in and out buffers can be the same.
 * @param in            The data to be encrypted.
 * @param out           The encrypted data.
 * @param s             The keyschedule
     */
static void blockEncrypt (byte *in, byte *out, pSquare s)
{
    uint32_t block[4];
    int i;
    int out_offset = 0;

    /* map byte array to block and add initial key: */
    for (i = 0; i < 4; i++)
    {
        block[i] =
            (((uint32_t)in[4*i+0] & 0xff)) ^
            (((uint32_t)in[4*i+1] & 0xff) << 8) ^
            (((uint32_t)in[4*i+2] & 0xff) << 16) ^
            (((uint32_t)in[4*i+3] & 0xff) << 24) ^
            s->roundKeys_e[i];
    }
    /* R - 1 full rounds: */
    for (i = 1; i < R; ++i)
    {
        round (block, sqtab.Te, s->roundKeys_e+i*4);
    }

    /* last round (diffusion becomes only transposition): */
    round (block, sqtab.Se, s->roundKeys_e+R*4);

    /* map block to byte array: */
    for (i = 0; i < 4; ++i)
    {
        out[out_offset++] = (byte)(block[i] );
        out[out_offset++] = (byte)(block[i] >> 8);
        out[out_offset++] = (byte)(block[i] >> 16);
        out[out_offset++] = (byte)(block[i] >> 24);
    }
    for (i = 0; i < 4; i++)
    {
        block[i] = 0;
    }
}

/**
 * Decrypt a block.
 * The in and out buffers can be the same.
 * @param in            The data to be decrypted.
 * @param out           The decrypted data.
 * @param s             The key schedule.
*/
static void blockDecrypt (byte *in, byte *out, pSquare s)
{
    uint32_t block[4];
    int i;
    int out_offset = 0;

    /* map byte array to block and add initial key: */
    for (i = 0; i < 4; i++)
    {
        block[i] =
            (((uint32_t)in[4*i+0] & 0xff)) ^
            (((uint32_t)in[4*i+1] & 0xff) << 8) ^
            (((uint32_t)in[4*i+2] & 0xff) << 16) ^
            (((uint32_t)in[4*i+3] & 0xff) << 24) ^
            s->roundKeys_d[i];
    }

    /* R - 1 full rounds: */
    for (i = 1; i < R; ++i)
    {
        round (block, sqtab.Td, s->roundKeys_d+i*4);
    }

    /* last round (diffusion becomes only transposition): */
    round (block, sqtab.Sd, s->roundKeys_d+R*4);

    /* map block to byte array: */
    for (i = 0; i < 4; ++i)
    {
        out[out_offset++] = (byte)(block[i] );
        out[out_offset++] = (byte)(block[i] >> 8);
        out[out_offset++] = (byte)(block[i] >> 16);
        out[out_offset++] = (byte)(block[i] >> 24);
    }
    for (i = 0; i < 4; i++)
    {
        block[i] = 0;
    }
}/* blockDecrypt */



/***************************************************************************/
void initSquare(byte *key, int triple, void **keysched, size_t *length)
{
    int keys = triple ? 3 : 1;
    int i;
    pSquare b;

    *length = KEYSCHEDLEN*keys;
    *keysched = zmalloc(*length);
    if(!(*keysched)) return;

    b = (pSquare) *keysched;

    for(i=0; i < keys; i++)
    {
        setupSquare(key, b+i );
        key+=SQUAREKEYSIZE;
    }
}

void ecbSquare(void *keysched, int triple, int encrypt,
byte *in, byte *out)
{
    int i, keys = triple ? 3 : 1;
    pSquare b = (pSquare)keysched;
    uint8_t work[SQUAREBLOCKSIZE];

    if(triple && (!encrypt)) b += 2;

    memcpy(work, in, SQUAREBLOCKSIZE);

    for(i=0; i<keys; i++)
    {
        if(encrypt) blockEncrypt(work, out, b+i );
        else blockDecrypt(work, out, b-i );
        memcpy(work, out, SQUAREBLOCKSIZE);
    }

    memset(work, 0, SQUAREBLOCKSIZE);
}

void destroySquare(void **keysched, size_t length)
{
    /* match memory deallocation to allocation locally! */
    if(length>0) zfree(keysched, length);
}
/***************************************************************************/
#ifdef TEST
/* test harness */
#include <stdio.h>
main()
{
    byte key[SQUAREKEYSIZE*2] = {
        0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
        0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
    };

    byte plaintext[SQUAREKEYSIZE] = {
        0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
        0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
    };
    byte ciphertext[SQUAREKEYSIZE] = {
        0x7c, 0x34, 0x91, 0xd9, 0x49, 0x94, 0xe7, 0x0f,
        0x0e, 0xc2, 0xe7, 0xa5, 0xcc, 0xb5, 0xa1, 0x4f,
    };
    byte work[SQUAREKEYSIZE], junk[SQUAREBLOCKSIZE];
    Square s;
    int i;

    setupSquare(key, &s);

    blockEncrypt(plaintext, work, &s);
    for(i=0; i<16; ++i)
    {
        printf("%02x", ciphertext[i]);
        if(7==i) printf(" ");
    }
    printf(" is expected\n");
    for(i=0; i<16; ++i)
    {
        printf("%02x", work[i]);
        if(7==i) printf(" ");
    }
    printf(" is generated\n");
    blockDecrypt(work, junk, &s);
    for(i=0; i<16; ++i)
    {
        printf("%02x", junk[i]);
        if(7==i) printf(" ");
    }
    printf(" is restored\n\n");

    blockDecrypt(ciphertext, work, &s);
    for(i=0; i<16; ++i)
    {
        printf("%02x", plaintext[i]);
        if(7==i) printf(" ");
    }
    printf(" is expected\n");
    for(i=0; i<16; ++i)
    {
        printf("%02x", work[i]);
        if(7==i) printf(" ");
    }
    printf(" is generated\n");
    blockEncrypt(work, junk, &s);
    for(i=0; i<16; ++i)
    {
        printf("%02x", junk[i]);
        if(7==i) printf(" ");
    }
    printf(" is restored\n");
    return 0;
}

#endif
/* end of file Square.c */

