/*
// Copyright in this code is held by Dr B.R. Gladman but free direct or
// derivative use is permitted subject to acknowledgement of its origin
// and subject to any constraints placed on the use of the algorithm by
// its designers (if such constraints may exist, this will be indicated
// below).
//
// Dr. B. R. Gladman (brian.gladman@btinternet.com). 25th January 2000.
//
// This is an implementation of Twofish, an encryption algorithm designed
// by Bruce Schneier and colleagues and submitted as a candidate for the
// Advanced Encryption Standard programme of the US National Institute of
// Standards and Technology.
//
// The designers of Twofish have not placed any constraints on the use of
// this algorithm.

 Modified to CTClib API Mr.Tines <tines@ravnaandtines.com> Dec '00

*/

#include "twofish.h"
#include "utils.h"
#include <string.h>

typedef struct Twofish_t {
    uint32_t k_len;
    uint32_t l_key[40];
    uint32_t s_key[4];
}
Twofish, *pTwofish;
#define TWOFISH(x) ks->x

#define rotr(x,n)   (((x) >> ((int)((n) & 0x1f))) | ((x) << ((int)((32 - ((n) & 0x1f))))))
#define rotl(x,n)   (((x) << ((int)((n) & 0x1f))) | ((x) >> ((int)((32 - ((n) & 0x1f))))))

/*Extract byte from a 32 bit quantity (little endian notation)*/

#define getbyte(x,n)   ((byte)((x) >> (8 * (n))))

/*finite field arithmetic for GF(2**8) with the modular
**polynomial x^8 + x^6 + x^5 + x^3 + 1 (0x169) */

#define G_M 0x0169

static byte tab_5b[4] = { 
    0, G_M >> 2, G_M >> 1, (G_M >> 1) ^ (G_M >> 2) };
static byte tab_ef[4] = { 
    0, (G_M >> 1) ^ (G_M >> 2), G_M >> 1, G_M >> 2 };

#define ffm_01(x)    (x)
#define ffm_5b(x)   ((x) ^ ((x) >> 2) ^ tab_5b[(x) & 3])
#define ffm_ef(x)   ((x) ^ ((x) >> 1) ^ ((x) >> 2) ^ tab_ef[(x) & 3])

static byte qt[2][256] = {
    { 
        0xa9,0x67,0xb3,0xe8,0x04,0xfd,0xa3,0x76,0x9a,0x92,0x80,0x78,0xe4,0xdd,0xd1,0x38,
        0x0d,0xc6,0x35,0x98,0x18,0xf7,0xec,0x6c,0x43,0x75,0x37,0x26,0xfa,0x13,0x94,0x48,
        0xf2,0xd0,0x8b,0x30,0x84,0x54,0xdf,0x23,0x19,0x5b,0x3d,0x59,0xf3,0xae,0xa2,0x82,
        0x63,0x01,0x83,0x2e,0xd9,0x51,0x9b,0x7c,0xa6,0xeb,0xa5,0xbe,0x16,0x0c,0xe3,0x61,
        0xc0,0x8c,0x3a,0xf5,0x73,0x2c,0x25,0x0b,0xbb,0x4e,0x89,0x6b,0x53,0x6a,0xb4,0xf1,
        0xe1,0xe6,0xbd,0x45,0xe2,0xf4,0xb6,0x66,0xcc,0x95,0x03,0x56,0xd4,0x1c,0x1e,0xd7,
        0xfb,0xc3,0x8e,0xb5,0xe9,0xcf,0xbf,0xba,0xea,0x77,0x39,0xaf,0x33,0xc9,0x62,0x71,
        0x81,0x79,0x09,0xad,0x24,0xcd,0xf9,0xd8,0xe5,0xc5,0xb9,0x4d,0x44,0x08,0x86,0xe7,
        0xa1,0x1d,0xaa,0xed,0x06,0x70,0xb2,0xd2,0x41,0x7b,0xa0,0x11,0x31,0xc2,0x27,0x90,
        0x20,0xf6,0x60,0xff,0x96,0x5c,0xb1,0xab,0x9e,0x9c,0x52,0x1b,0x5f,0x93,0x0a,0xef,
        0x91,0x85,0x49,0xee,0x2d,0x4f,0x8f,0x3b,0x47,0x87,0x6d,0x46,0xd6,0x3e,0x69,0x64,
        0x2a,0xce,0xcb,0x2f,0xfc,0x97,0x05,0x7a,0xac,0x7f,0xd5,0x1a,0x4b,0x0e,0xa7,0x5a,
        0x28,0x14,0x3f,0x29,0x88,0x3c,0x4c,0x02,0xb8,0xda,0xb0,0x17,0x55,0x1f,0x8a,0x7d,
        0x57,0xc7,0x8d,0x74,0xb7,0xc4,0x9f,0x72,0x7e,0x15,0x22,0x12,0x58,0x07,0x99,0x34,
        0x6e,0x50,0xde,0x68,0x65,0xbc,0xdb,0xf8,0xc8,0xa8,0x2b,0x40,0xdc,0xfe,0x32,0xa4,
        0xca,0x10,0x21,0xf0,0xd3,0x5d,0x0f,0x00,0x6f,0x9d,0x36,0x42,0x4a,0x5e,0xc1,0xe0     }
    ,

    { 
        0x75,0xf3,0xc6,0xf4,0xdb,0x7b,0xfb,0xc8,0x4a,0xd3,0xe6,0x6b,0x45,0x7d,0xe8,0x4b,
        0xd6,0x32,0xd8,0xfd,0x37,0x71,0xf1,0xe1,0x30,0x0f,0xf8,0x1b,0x87,0xfa,0x06,0x3f,
        0x5e,0xba,0xae,0x5b,0x8a,0x00,0xbc,0x9d,0x6d,0xc1,0xb1,0x0e,0x80,0x5d,0xd2,0xd5,
        0xa0,0x84,0x07,0x14,0xb5,0x90,0x2c,0xa3,0xb2,0x73,0x4c,0x54,0x92,0x74,0x36,0x51,
        0x38,0xb0,0xbd,0x5a,0xfc,0x60,0x62,0x96,0x6c,0x42,0xf7,0x10,0x7c,0x28,0x27,0x8c,
        0x13,0x95,0x9c,0xc7,0x24,0x46,0x3b,0x70,0xca,0xe3,0x85,0xcb,0x11,0xd0,0x93,0xb8,
        0xa6,0x83,0x20,0xff,0x9f,0x77,0xc3,0xcc,0x03,0x6f,0x08,0xbf,0x40,0xe7,0x2b,0xe2,
        0x79,0x0c,0xaa,0x82,0x41,0x3a,0xea,0xb9,0xe4,0x9a,0xa4,0x97,0x7e,0xda,0x7a,0x17,
        0x66,0x94,0xa1,0x1d,0x3d,0xf0,0xde,0xb3,0x0b,0x72,0xa7,0x1c,0xef,0xd1,0x53,0x3e,
        0x8f,0x33,0x26,0x5f,0xec,0x76,0x2a,0x49,0x81,0x88,0xee,0x21,0xc4,0x1a,0xeb,0xd9,
        0xc5,0x39,0x99,0xcd,0xad,0x31,0x8b,0x01,0x18,0x23,0xdd,0x1f,0x4e,0x2d,0xf9,0x48,
        0x4f,0xf2,0x65,0x8e,0x78,0x5c,0x58,0x19,0x8d,0xe5,0x98,0x57,0x67,0x7f,0x05,0x64,
        0xaf,0x63,0xb6,0xfe,0xf5,0xb7,0x3c,0xa5,0xce,0xe9,0x68,0x44,0xe0,0x4d,0x43,0x69,
        0x29,0x2e,0xac,0x15,0x59,0xa8,0x0a,0x9e,0x6e,0x47,0xdf,0x34,0x35,0x6a,0xcf,0xdc,
        0x22,0xc9,0xc0,0x9b,0x89,0xd4,0xed,0xab,0x12,0xa2,0x0d,0x52,0xbb,0x02,0x2f,0xa9,
        0xd7,0x61,0x1e,0xb4,0x50,0x04,0xf6,0xc2,0x16,0x25,0x86,0x56,0x55,0x09,0xbe,0x91     }
};

static boolean qt_gen = 0;
static uint32_t q2_tab[256];
static uint32_t q3_tab[256];
static uint32_t q4_tab[256];

#define qs(m,n) ((uint32_t)qt[m][i] << 8 * n)

static void gen_qtab(void)
{
    uint32_t i;
    for(i = 0; i < 256; ++i)
    {
        q2_tab[i] = qs(0, 0) | qs(1, 1) | qs(0, 2) | qs(1, 3);
        q3_tab[i] = qs(1, 0) | qs(1, 1) | qs(0, 2) | qs(0, 3);
        q4_tab[i] = qs(1, 0) | qs(0, 1) | qs(0, 2) | qs(1, 3);
    }
}

/*Q_mn is the q_box applied for byte n on round m, where the rounds
* are numbered from k_len down to 0 (4..0), (3..0) or (2..0)*/

#define Q_00    qt[1]
#define Q_01    qt[0]
#define Q_02    qt[1]
#define Q_03    qt[0]

#define Q_10    qt[0]
#define Q_11    qt[0]
#define Q_12    qt[1]
#define Q_13    qt[1]

#define Q_20    qt[0]
#define Q_21    qt[1]
#define Q_22    qt[0]
#define Q_23    qt[1]

#define Q_30    qt[1]
#define Q_31    qt[1]
#define Q_32    qt[0]
#define Q_33    qt[0]

#define Q_40    qt[1]
#define Q_41    qt[0]
#define Q_42    qt[0]
#define Q_43    qt[1]

#define bval(x,n)   (((byte*)(&x))[n])
#define  q8(x,m,n)  Q_##m##n[x] ^ bval(key[m - 1],n)

static boolean mt_gen = 0;
static uint32_t m_tab[4][256];

static void gen_mtab(void)
{
    uint32_t i, f01, f5b, fef;

    for(i = 0; i < 256; ++i)
    {
        f01 = qt[1][i]; 
        f5b = ffm_5b(f01); 
        fef = ffm_ef(f01);
        m_tab[0][i] = f01 + (f5b << 8) + (fef << 16) + (fef << 24);
        m_tab[2][i] = f5b + (fef << 8) + (f01 << 16) + (fef << 24);

        f01 = qt[0][i]; 
        f5b = ffm_5b(f01); 
        fef = ffm_ef(f01);
        m_tab[1][i] = fef + (fef << 8) + (f5b << 16) + (f01 << 24);
        m_tab[3][i] = f5b + (f01 << 8) + (fef << 16) + (f5b << 24);
    }
}

#define mds(n,x)    m_tab[n][x]

/*My thanks to Bill and John Worley of HP Labs for the speed up of this function
* for the key schedule by using large tables for the first qt substitution*/
#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (disable: 4701)
#endif

static uint32_t h_fun(const uint32_t x, const uint32_t key[], uint32_t k_len)
{
    uint32_t b0, b1, b2, b3;
    switch(k_len)
    { 
        uint32_t xx;
    case 4:
        xx = q4_tab[x] ^ key[3];
        b0 = getbyte(xx, 0); 
        b1 = getbyte(xx, 1); 
        b2 = getbyte(xx, 2); 
        b3 = getbyte(xx, 3);
        b0 = q8(q8(q8(b0, 3, 0), 2, 0), 1, 0); 
        b1 = q8(q8(q8(b1, 3, 1), 2, 1), 1, 1);
        b2 = q8(q8(q8(b2, 3, 2), 2, 2), 1, 2); 
        b3 = q8(q8(q8(b3, 3, 3), 2, 3), 1, 3);
        break;

    case 3:
        xx = q3_tab[x] ^ key[2];
        b0 = getbyte(xx, 0); 
        b1 = getbyte(xx, 1); 
        b2 = getbyte(xx, 2); 
        b3 = getbyte(xx, 3);
        b0 = q8(q8(b0, 2, 0), 1, 0); 
        b1 = q8(q8(b1, 2, 1), 1, 1);
        b2 = q8(q8(b2, 2, 2), 1, 2); 
        b3 = q8(q8(b3, 2, 3), 1, 3);
        break;

    case 2:
        xx = q2_tab[x] ^ key[1];
        b0 = getbyte(xx, 0); 
        b1 = getbyte(xx, 1); 
        b2 = getbyte(xx, 2); 
        b3 = getbyte(xx, 3);
        b0 = q8(b0, 1, 0); 
        b1 = q8(b1, 1, 1); 
        b2 = q8(b2, 1, 2); 
        b3 = q8(b3, 1, 3);
    }

    return mds(0, b0) ^ mds(1, b1) ^ mds(2, b2) ^ mds(3, b3);
}
#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (default: 4701)
#endif


static uint32_t mk_tab[4][256];

#define q2(x,n) q8(q8(x, 2, n), 1, n)
#define q3(x,n) q8(q8(q8(x, 3, n), 2, n), 1, n)
#define q4(x,n) q8(q8(q8(q8(x, 4, n), 3 ,n), 2, n), 1, n)

static void gen_mk_tab(uint32_t key[], uint32_t k_len)
{
    uint32_t i;
    byte by;

    switch(k_len)
    {
    case 2:
        for(i = 0; i < 256; ++i)
        {
            by = (byte)i;
            mk_tab[0][i] = mds(0, q2(by, 0)); 
            mk_tab[1][i] = mds(1, q2(by, 1));
            mk_tab[2][i] = mds(2, q2(by, 2)); 
            mk_tab[3][i] = mds(3, q2(by, 3));
        }
        break;

    case 3:
        for(i = 0; i < 256; ++i)
        {
            by = (byte)i;
            mk_tab[0][i] = mds(0, q3(by, 0)); 
            mk_tab[1][i] = mds(1, q3(by, 1));
            mk_tab[2][i] = mds(2, q3(by, 2)); 
            mk_tab[3][i] = mds(3, q3(by, 3));
        }
        break;

    case 4:
        for(i = 0; i < 256; ++i)
        {
            by = (byte)i;
            mk_tab[0][i] = mds(0, q4(by, 0)); 
            mk_tab[1][i] = mds(1, q4(by, 1));
            mk_tab[2][i] = mds(2, q4(by, 2)); 
            mk_tab[3][i] = mds(3, q4(by, 3));
        }
    }
}


#define g0_fun(x) ( mk_tab[0][getbyte(x,0)] ^ mk_tab[1][getbyte(x,1)] \
^ mk_tab[2][getbyte(x,2)] ^ mk_tab[3][getbyte(x,3)] )
#define g1_fun(x) ( mk_tab[1][getbyte(x,0)] ^ mk_tab[2][getbyte(x,1)] \
^ mk_tab[3][getbyte(x,2)] ^ mk_tab[0][getbyte(x,3)] )

/*
//The (12,8) Reed Soloman code has the generator polynomial
//
//g(x) = x^4 + (a + 1/a) * x^3 + a * x^2 + (a + 1/a) * x + 1
//
//where the coefficients are in the finite field GF(2^8) with a
//modular polynomial a^8 + a^6 + a^3 + a^2 + 1. To generate the
//remainder we have to start with a 12th order polynomial with our
//eight input bytes as the coefficients of the 4th to 11th terms.
//That is:
//
//m[7] * x^11 + m[6] * x^10 ... + m[0] * x^4 + 0 * x^3 +... + 0
//
//We then multiply the generator polynomial by m[7] * x^7 and subtract
//it - xor in GF(2^8) - from the above to eliminate the x^7 term (the
//artihmetic on the coefficients is done in GF(2^8). We then multiply
//the generator polynomial by x^6 * coeff(x^10) and use this to remove
//the x^10 term. We carry on in this way until the x^4 term is removed
//so that we are left with:
//
//r[3] * x^3 + r[2] * x^2 + r[1] 8 x^1 + r[0]
//
//which give the resulting 4 bytes of the remainder. This is equivalent
//to the matrix multiplication in the Twofish description but much faster
//to implement.
*/

#define G_MOD   0x0000014d

static uint32_t mds_rem(uint32_t p0, uint32_t p1)
{
    uint32_t i, t, u;
    for(i = 0; i < 8; ++i)
    {
        t = p1 >> 24; /*get most significant coefficient*/
        p1 = (p1 << 8) | (p0 >> 24);
        p0 <<= 8; /*shift others up*/
        /*multiply t by a (the primitive element - i.e. left shift)*/
        u = (t << 1);

        if(t & 0x80) /*subtract modular polynomial on overflow*/
            {
            u ^= G_MOD;
        }
        p1 ^= t ^ (u << 16); /*remove t * (a * x^2 + 1)*/
        u ^= (t >> 1); /*form u = a * t + t /a = t * (a + 1 /a);*/

        if(t & 0x01) /*add the modular polynomial on underflow*/
            {
            u ^= G_MOD >> 1;
        }
        p1 ^= (u << 24) | (u << 8); /*remove t * (a + 1/a) * (x^3 + x)*/
    }

    return p1;
}


/*initialise the key schedule from the user supplied key*/
#define usb(b,n) (b[n]&0xFF)
#define bx(b,n) (usb(b,n)<<(8*n))
#define uint32_t_in(b) ( bx((b),0)|bx((b),1)|bx((b),2)|bx((b),3) )

static void TWOFISHset_key(pTwofish ks, const byte in_key[],
const uint32_t key_len)
{
    uint32_t i, a, b, me_key[4], mo_key[4];

    if(!qt_gen)
        {
        gen_qtab();
        qt_gen = 1;
    }

    if(!mt_gen)
        {
        gen_mtab();
        mt_gen = 1;
    }

    TWOFISH(k_len) = key_len /64; /* 2, 3 or 4 */

    for(i = 0; i < TWOFISH(k_len); ++i)
    {
        a = uint32_t_in(in_key + 8 * i);
        me_key[i] = a;
        b = uint32_t_in(in_key + 8 * i + 4);
        mo_key[i] = b;
        TWOFISH(s_key)[TWOFISH(k_len) - i - 1] = mds_rem(a, b);
    }

    for(i = 0; i < 40; i += 2)
    {
        a = i;
        b = i + 1;
        a = h_fun(a, me_key, TWOFISH(k_len));
        b = rotl(h_fun(b, mo_key, TWOFISH(k_len)), 8);
        TWOFISH(l_key)[i] = a + b;
        TWOFISH(l_key)[i + 1] = rotl(a + 2 * b, 9);
    }


    gen_mk_tab(TWOFISH(s_key), TWOFISH(k_len));

    return;
}

/*encrypt a block of text*/
#define f_rnd(i)                                \
    t1 = g1_fun(blk[1]); t0 = g0_fun(blk[0]);   \
    blk[2] = rotr(blk[2] ^ (t0 + t1 + TWOFISH(l_key)[4 * (i) + 8]), 1);      \
    blk[3] = rotl(blk[3], 1) ^ (t0 + 2 * t1 + TWOFISH(l_key)[4 * (i) + 9]);  \
    t1 = g1_fun(blk[3]); t0 = g0_fun(blk[2]);   \
    blk[0] = rotr(blk[0] ^ (t0 + t1 + TWOFISH(l_key)[4 * (i) + 10]), 1);     \
    blk[1] = rotl(blk[1], 1) ^ (t0 + 2 * t1 + TWOFISH(l_key)[4 * (i) + 11])

#define pb(b,n) (b)[n]=getbyte(t0,n)
#define uint32_t_out(b, v) {t0=v; pb(b,0); pb(b,1); pb(b,2); pb(b,3);}

static void TwofishEncrypt(const byte in_blk[16], byte out_blk[16], const pTwofish ks)
{
    uint32_t t0, t1, blk[4];
    blk[0] = uint32_t_in(in_blk ) ^ TWOFISH(l_key)[0];
    blk[1] = uint32_t_in(in_blk + 4) ^ TWOFISH(l_key)[1];
    blk[2] = uint32_t_in(in_blk + 8) ^ TWOFISH(l_key)[2];
    blk[3] = uint32_t_in(in_blk + 12) ^ TWOFISH(l_key)[3];

    f_rnd(0);
    f_rnd(1);
    f_rnd(2);
    f_rnd(3);
    f_rnd(4);
    f_rnd(5);
    f_rnd(6);
    f_rnd(7);

    uint32_t_out(out_blk, blk[2] ^ TWOFISH(l_key)[4]);
    uint32_t_out(out_blk + 4, blk[3] ^ TWOFISH(l_key)[5]);
    uint32_t_out(out_blk + 8, blk[0] ^ TWOFISH(l_key)[6]);
    uint32_t_out(out_blk + 12, blk[1] ^ TWOFISH(l_key)[7]);
}

/*decrypt a block of text*/
#define i_rnd(i)                                    \
        t1 = g1_fun(blk[1]); t0 = g0_fun(blk[0]);   \
        blk[2] = rotl(blk[2], 1) ^ (t0 + t1 + TWOFISH(l_key)[4 * (i) + 10]);     \
        blk[3] = rotr(blk[3] ^ (t0 + 2 * t1 + TWOFISH(l_key)[4 * (i) + 11]), 1); \
        t1 = g1_fun(blk[3]); t0 = g0_fun(blk[2]);   \
        blk[0] = rotl(blk[0], 1) ^ (t0 + t1 + TWOFISH(l_key)[4 * (i) +  8]);     \
        blk[1] = rotr(blk[1] ^ (t0 + 2 * t1 + TWOFISH(l_key)[4 * (i) +  9]), 1)

static void TwofishDecrypt(const byte in_blk[16], byte out_blk[16], const pTwofish ks)
{
    uint32_t t0, t1, blk[4];
    blk[0] = uint32_t_in(in_blk ) ^ TWOFISH(l_key)[4];
    blk[1] = uint32_t_in(in_blk + 4) ^ TWOFISH(l_key)[5];
    blk[2] = uint32_t_in(in_blk + 8) ^ TWOFISH(l_key)[6];
    blk[3] = uint32_t_in(in_blk + 12) ^ TWOFISH(l_key)[7];

    i_rnd(7);
    i_rnd(6); 
    i_rnd(5); 
    i_rnd(4);
    i_rnd(3); 
    i_rnd(2); 
    i_rnd(1); 
    i_rnd(0);

    uint32_t_out(out_blk, blk[2] ^ TWOFISH(l_key)[0]);
    uint32_t_out(out_blk + 4, blk[3] ^ TWOFISH(l_key)[1]);
    uint32_t_out(out_blk + 8, blk[0] ^ TWOFISH(l_key)[2]);
    uint32_t_out(out_blk + 12, blk[1] ^ TWOFISH(l_key)[3]);
}


/**
* CTClib standard interface
*/

static void initTwofish(byte *key, int size, int triple, void **keysched, size_t *length)
{
    int keys = triple ? 3 : 1;
    int i;
    pTwofish b;

    *length = sizeof(Twofish)*keys;
    *keysched = zmalloc(*length);
    if(!(*keysched)) return;

    b = (pTwofish) *keysched;

    for(i=0; i < keys; i++)
    {
        TWOFISHset_key(b+i, key, size*8);
        key+=(size_t)size;
    }
}

void initTwofish128(byte *key, int triple, void **keysched, size_t *length)
{
    initTwofish(key, TWOFISH128KEYSIZE, triple, keysched, length);
}

void initTwofish192(byte *key, int triple, void **keysched, size_t *length)
{
    initTwofish(key, TWOFISH192KEYSIZE, triple, keysched, length);
}

void initTwofish256(byte *key, int triple, void **keysched, size_t *length)
{
    initTwofish(key, TWOFISH256KEYSIZE, triple, keysched, length);
}

void ecbTwofish(void *keysched, int triple, int encrypt, byte *in, byte *out)
{
    pTwofish b = (pTwofish)keysched;

    if(!triple)
        {
        if(encrypt) TwofishEncrypt(in, out, b);
        else TwofishDecrypt(in, out, b);
    }
    else
    {
        if(encrypt)
            {
            TwofishEncrypt(in, out, &b[0]);
            TwofishEncrypt(out, in, &b[1]);
            TwofishEncrypt(in, out, &b[2]);
        }
        else
            {
            TwofishDecrypt(in, out, &b[2]);
            TwofishDecrypt(out, in, &b[1]);
            TwofishDecrypt(in, out, &b[0]);
        }
    }
}

void destroyTwofish(void **keysched, size_t length)
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
    Twofish ks;


    for(i=0; i<n;++i)
    {
        int size = textToBytes(vector[i].key, key);
        textToBytes(vector[i].plain, plaintext);
        printf("\n%d\n", vector[i].i);

        printf("key=%s length %d\n", vector[i].key, size);

        TWOFISHset_key(&ks, key, size*8);

        for(j=0; j<10000; ++j)
        {
            TwofishEncrypt(plaintext, result, &ks);
            memcpy(plaintext, result, MAXBLOCKSIZE);
        }


        printf("plaintext  = %s\n", vector[i].plain);
        printf("ciphertext = ");
        for(j=0; j<TWOFISHBLOCKSIZE; ++j) printf("%02x", result[j]);
        printf("\n");
        printf("expect     = %s\n", vector[i].cipher);

        for(j=0; j<10000; ++j)
        {
            TwofishDecrypt(result, plaintext, &ks);
            memcpy(result, plaintext, MAXBLOCKSIZE);
        }

        printf("invert     = ");
        for(j=0; j<TWOFISHBLOCKSIZE; ++j) printf("%02x", plaintext[j]);
        printf("\n");


        textToBytes(vector[i].cipher, cipher);
        for(j=0; j<10000; ++j)
        {
            TwofishDecrypt(cipher, result, &ks);
            memcpy(cipher, result, MAXBLOCKSIZE);
        }

        printf("decrypt    = ");
        for(j=0; j<TWOFISHBLOCKSIZE; ++j) printf("%02x", result[j]);
        printf("\n");

    }
}

/* a subset of the real vectors */
static tv vector128[] = {
    {
        0,
        "00000000000000000000000000000000",
        "00000000000000000000000000000000",
        "282be7e4fa1fbdc29661286f1f310b7e"     }
    ,
    {
        1,
        "282be7e4fa1fbdc29661286f1f310b7e",
        "282be7e4fa1fbdc29661286f1f310b7e",
        "c8e1d477621acc37742bd16032075654"     }
    ,
    {
        2,
        "e0ca3393980571f5e24af90f2d365d2a",
        "c8e1d477621acc37742bd16032075654",
        "d5187e7d6b8be9517dac4a8af4a552ea"     }
    ,
    {
        3,
        "35d24deef38e98a49fe6b385d9930fc0",
        "d5187e7d6b8be9517dac4a8af4a552ea",
        "211b6f0c6061068d203440adefc45bab"     }
    ,
};

static tv vector192[] = {
    {
        0,
        "000000000000000000000000000000000000000000000000",
        "00000000000000000000000000000000",
        "9ab71d7f280ff79f0d135bbd5fab7e37"     }
    ,
    {
        1,
        "e5a08a78b0b4cc579ab71d7f280ff79f0d135bbd5fab7e37",
        "9ab71d7f280ff79f0d135bbd5fab7e37",
        "a1a3c49fd659216172cde292ce5f5226"     }
    ,
    {
        2,
        "6ffdd6d82a0ed3ed3b14d9e0fe56d6fe7fdeb92f91f42c11",
        "a1a3c49fd659216172cde292ce5f5226",
        "056e89a3d7be7634b640b843da265d9c"     }
    ,
    {
        3,
        "3bf80777c05eb0f23e7a504329e8a0cac99e016c4bd2718d",
        "056e89a3d7be7634b640b843da265d9c",
        "66c7c98f2d871d060303e0841fd7e691"     }
    ,
};

static tv vector256[] = {
    {
        0,
        "0000000000000000000000000000000000000000000000000000000000000000",
        "00000000000000000000000000000000",
        "04f2f36ca927ae506931de8f78b2513c"     }
    ,
    {
        1,
        "3b5ee901de5ac4078b9540a9b861fde304f2f36ca927ae506931de8f78b2513c",
        "04f2f36ca927ae506931de8f78b2513c",
        "04ead2a58c67fefcd4485d231c7ae4d7"     }
    ,
    {
        2,
        "b76f87f9314582425bc54dd51e780980001821c9254050acbd7983ac64c8b5eb",
        "04ead2a58c67fefcd4485d231c7ae4d7",
        "01bf7f83a2cf4d8f31b5f913fb372389"     }
    ,
    {
        3,
        "a4ec3215e31d4cd74258c80f76ff751f01a75e4a878f1d238ccc7abf9fff9662",
        "01bf7f83a2cf4d8f31b5f913fb372389",
        "41c1a5a2ceb3f7095e795dbcee0f90f2"     }
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

/* EOF twofish.c */

