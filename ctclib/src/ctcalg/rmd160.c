/* rmd160.c  - RIPE-MD160
 * Copyright (C) 1998 Free Software Foundation, Inc.
 *
 * This file is part of GNUPG.
 *
 * GNUPG is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * GNUPG is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA
 *
 *
 * Modified for CTC-interface Mr. Tines, 28-Feb-1998
 */
#include <stdlib.h>
#include <string.h>

#include "utils.h"

#include "rmd.h"

/*********************************
 * RIPEMD-160 is not patented, see (as of 25.10.97)
 *   http://www.esat.kuleuven.ac.be/~bosselae/ripemd160.html
 * Note that the code uses Little Endian byteorder, which is good for
 * 386 etc, but we must add some conversion when used on a big endian box.
 *
 *
 * Pseudo-code for RIPEMD-160
 *
 * RIPEMD-160 is an iterative hash function that operates on 32-bit words.
 * The round function takes as input a 5-word chaining variable and a 16-word
 * message block and maps this to a new chaining variable. All operations are
 * defined on 32-bit words. Padding is identical to that of MD4.
 *
 *
 * RIPEMD-160: definitions
 *
 *
 *   nonlinear functions at bit level: exor, mux, -, mux, -
 *
 *   f(j, x, y, z) = x XOR y XOR z    (0 <= j <= 15)
 *   f(j, x, y, z) = (x AND y) OR (NOT(x) AND z)  (16 <= j <= 31)
 *   f(j, x, y, z) = (x OR NOT(y)) XOR z   (32 <= j <= 47)
 *   f(j, x, y, z) = (x AND z) OR (y AND NOT(z))  (48 <= j <= 63)
 *   f(j, x, y, z) = x XOR (y OR NOT(z))   (64 <= j <= 79)
 *
 *
 *   added constants (hexadecimal)
 *
 *   K(j) = 0x00000000     (0 <= j <= 15)
 *   K(j) = 0x5A827999    (16 <= j <= 31) int(2**30 x sqrt(2))
 *   K(j) = 0x6ED9EBA1    (32 <= j <= 47) int(2**30 x sqrt(3))
 *   K(j) = 0x8F1BBCDC    (48 <= j <= 63) int(2**30 x sqrt(5))
 *   K(j) = 0xA953FD4E    (64 <= j <= 79) int(2**30 x sqrt(7))
 *   K'(j) = 0x50A28BE6     (0 <= j <= 15)      int(2**30 x cbrt(2))
 *   K'(j) = 0x5C4DD124    (16 <= j <= 31)      int(2**30 x cbrt(3))
 *   K'(j) = 0x6D703EF3    (32 <= j <= 47)      int(2**30 x cbrt(5))
 *   K'(j) = 0x7A6D76E9    (48 <= j <= 63)      int(2**30 x cbrt(7))
 *   K'(j) = 0x00000000    (64 <= j <= 79)
 *
 *
 *   selection of message word
 *
 *   r(j)      = j        (0 <= j <= 15)
 *   r(16..31) = 7, 4, 13, 1, 10, 6, 15, 3, 12, 0, 9, 5, 2, 14, 11, 8
 *   r(32..47) = 3, 10, 14, 4, 9, 15, 8, 1, 2, 7, 0, 6, 13, 11, 5, 12
 *   r(48..63) = 1, 9, 11, 10, 0, 8, 12, 4, 13, 3, 7, 15, 14, 5, 6, 2
 *   r(64..79) = 4, 0, 5, 9, 7, 12, 2, 10, 14, 1, 3, 8, 11, 6, 15, 13
 *   r0(0..15) = 5, 14, 7, 0, 9, 2, 11, 4, 13, 6, 15, 8, 1, 10, 3, 12
 *   r0(16..31)= 6, 11, 3, 7, 0, 13, 5, 10, 14, 15, 8, 12, 4, 9, 1, 2
 *   r0(32..47)= 15, 5, 1, 3, 7, 14, 6, 9, 11, 8, 12, 2, 10, 0, 4, 13
 *   r0(48..63)= 8, 6, 4, 1, 3, 11, 15, 0, 5, 12, 2, 13, 9, 7, 10, 14
 *   r0(64..79)= 12, 15, 10, 4, 1, 5, 8, 7, 6, 2, 13, 14, 0, 3, 9, 11
 *
 *
 *   amount for rotate left (rol)
 *
 *   s(0..15)  = 11, 14, 15, 12, 5, 8, 7, 9, 11, 13, 14, 15, 6, 7, 9, 8
 *   s(16..31) = 7, 6, 8, 13, 11, 9, 7, 15, 7, 12, 15, 9, 11, 7, 13, 12
 *   s(32..47) = 11, 13, 6, 7, 14, 9, 13, 15, 14, 8, 13, 6, 5, 12, 7, 5
 *   s(48..63) = 11, 12, 14, 15, 14, 15, 9, 8, 9, 14, 5, 6, 8, 6, 5, 12
 *   s(64..79) = 9, 15, 5, 11, 6, 8, 13, 12, 5, 12, 13, 14, 11, 8, 5, 6
 *   s'(0..15) = 8, 9, 9, 11, 13, 15, 15, 5, 7, 7, 8, 11, 14, 14, 12, 6
 *   s'(16..31)= 9, 13, 15, 7, 12, 8, 9, 11, 7, 7, 12, 7, 6, 15, 13, 11
 *   s'(32..47)= 9, 7, 15, 11, 8, 6, 6, 14, 12, 13, 5, 14, 13, 13, 7, 5
 *   s'(48..63)= 15, 5, 8, 11, 14, 14, 6, 14, 6, 9, 12, 9, 12, 5, 15, 8
 *   s'(64..79)= 8, 5, 12, 9, 12, 5, 14, 6, 8, 13, 6, 5, 15, 13, 11, 11
 *
 *
 *   initial value (hexadecimal)
 *
 *   h0 = 0x67452301; h1 = 0xEFCDAB89; h2 = 0x98BADCFE; h3 = 0x10325476;
 *       h4 = 0xC3D2E1F0;
 *
 *
 * RIPEMD-160: pseudo-code
 *
 *   It is assumed that the message after padding consists of t 16-word blocks
 *   that will be denoted with X[i][j], with 0 <= i <= t-1 and 0 <= j <= 15.
 *   The symbol [+] denotes addition modulo 2**32 and rol_s denotes cyclic left
 *   shift (rotate) over s positions.
 *
 *
 *   for i := 0 to t-1 {
 *  A := h0; B := h1; C := h2; D = h3; E = h4;
 *  A' := h0; B' := h1; C' := h2; D' = h3; E' = h4;
 *  for j := 0 to 79 {
 *      T := rol_s(j)(A [+] f(j, B, C, D) [+] X[i][r(j)] [+] K(j)) [+] E;
 *      A := E; E := D; D := rol_10(C); C := B; B := T;
 *      T := rol_s'(j)(A' [+] f(79-j, B', C', D') [+] X[i][r'(j)]
             [+] K'(j)) [+] E';
 *      A' := E'; E' := D'; D' := rol_10(C'); C' := B'; B' := T;
 *  }
 *  T := h1 [+] C [+] D'; h1 := h2 [+] D [+] E'; h2 := h3 [+] E [+] A';
 *  h3 := h4 [+] A [+] B'; h4 := h0 [+] B [+] C'; h0 := T;
 *   }
 */

/* Some examples:
 * ""                    9c1185a5c5e9fc54612808977ee8f548b2258d31
 * "a"                   0bdc9d2d256b3ee9daae347be6f4dc835a467ffe
 * "abc"                 8eb208f7e05d987a9b044a8e98c6b087f15a0bfc
 * "message digest"      5d0689ef49d2fae572b881b123a85ffa21595f36
 * "a...z"               f71c27109c692c1b56bbdceb5b9d2865b3708dbc
 * "abcdbcde...nopq"     12a053384a9c0c88e405a06c27dcf49ada62eb2b
 * "A...Za...z0...9"     b0e20b6e3116640286ed3a87a5713079b21f5189
 * 8 times "1234567890"  9b752e45573d4b39f4dbd3323cab82bf63326bfb
 * 1 million times "a"   52783243c1697bdbe16d37f97f68f08325dc1528
 */


void rmd160Init( RMD160_CONTEXT **c, size_t *length )
{
    RMD160_CONTEXT *hd;

    *length = sizeof(RMD160_CONTEXT);
    *c = zmalloc(*length);

    if(!(*c)) return;



    hd = *c;
    hd->h0 = 0x67452301UL;
    hd->h1 = 0xEFCDAB89UL;
    hd->h2 = 0x98BADCFEUL;
    hd->h3 = 0x10325476UL;
    hd->h4 = 0xC3D2E1F0UL;
    hd->nblocks = 0;
    hd->count = 0;
}


#ifdef HARDWARE_ROTATIONS
#define rol(v, n) (_lrotl ((v), (n)))
#else
#define rol(n,x) ( ((x) << (n)) | ((x) >> (32-(n))) )
#endif


/****************
 * Transform the message X which consists of 16 32-bit-words
 */
static void transform( RMD160_CONTEXT *hd, byte *data )
{
    static int r[80] = {
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
        7, 4, 13, 1, 10, 6, 15, 3, 12, 0, 9, 5, 2, 14, 11, 8,
        3, 10, 14, 4, 9, 15, 8, 1, 2, 7, 0, 6, 13, 11, 5, 12,
        1, 9, 11, 10, 0, 8, 12, 4, 13, 3, 7, 15, 14, 5, 6, 2,
        4, 0, 5, 9, 7, 12, 2, 10, 14, 1, 3, 8, 11, 6, 15, 13     };
    static int rr[80] = {
        5, 14, 7, 0, 9, 2, 11, 4, 13, 6, 15, 8, 1, 10, 3, 12,
        6, 11, 3, 7, 0, 13, 5, 10, 14, 15, 8, 12, 4, 9, 1, 2,
        15, 5, 1, 3, 7, 14, 6, 9, 11, 8, 12, 2, 10, 0, 4, 13,
        8, 6, 4, 1, 3, 11, 15, 0, 5, 12, 2, 13, 9, 7, 10, 14,
        12, 15, 10, 4, 1, 5, 8, 7, 6, 2, 13, 14, 0, 3, 9, 11     };
    static int s[80] = {
        11, 14, 15, 12, 5, 8, 7, 9, 11, 13, 14, 15, 6, 7, 9, 8,
        7, 6, 8, 13, 11, 9, 7, 15, 7, 12, 15, 9, 11, 7, 13, 12,
        11, 13, 6, 7, 14, 9, 13, 15, 14, 8, 13, 6, 5, 12, 7, 5,
        11, 12, 14, 15, 14, 15, 9, 8, 9, 14, 5, 6, 8, 6, 5, 12,
        9, 15, 5, 11, 6, 8, 13, 12, 5, 12, 13, 14, 11, 8, 5, 6     };
    static int ss[80] = {
        8, 9, 9, 11, 13, 15, 15, 5, 7, 7, 8, 11, 14, 14, 12, 6,
        9, 13, 15, 7, 12, 8, 9, 11, 7, 7, 12, 7, 6, 15, 13, 11,
        9, 7, 15, 11, 8, 6, 6, 14, 12, 13, 5, 14, 13, 13, 7, 5,
        15, 5, 8, 11, 14, 14, 6, 14, 6, 9, 12, 9, 12, 5, 15, 8,
        8, 5, 12, 9, 12, 5, 14, 6, 8, 13, 6, 5, 15, 13, 11, 11     };
    uint32_t a,b,c,d,e,aa,bb,cc,dd,ee,t;
    int rbits, j;
    uint32_t x[16];

#define K(a)   ( (a) < 16 ? 0x00000000UL :       \
    (a) < 32 ? 0x5A827999UL : \
 (a) < 48 ? 0x6ED9EBA1UL : \
 (a) < 64 ? 0x8F1BBCDCUL : 0xA953FD4EUL )
#define KK(a)  ( (a) < 16 ? 0x50A28BE6UL :       \
        (a) < 32 ? 0x5C4DD124UL : \
 (a) < 48 ? 0x6D703EF3UL : \
 (a) < 64 ? 0x7A6D76E9UL : 0x00000000UL )

#define F0(x,y,z)   ( (x) ^ (y) ^ (z) )
#define F1(x,y,z)   ( ((x) & (y)) | (~(x) & (z)) )
#define F2(x,y,z)   ( ((x) | ~(y)) ^ (z) )
#define F3(x,y,z)   ( ((x) & (z)) | ((y) & ~(z)) )
#define F4(x,y,z)   ( (x) ^ ((y) | ~(z)) )
#define F(a,x,y,z)  ( (a) < 16 ? F0((x),(y),(z)) : \
    (a) < 32 ? F1((x),(y),(z)) : \
 (a) < 48 ? F2((x),(y),(z)) : \
 (a) < 64 ? F3((x),(y),(z)) : \
 F4((x),(y),(z)) )

    if(!little_endian())
        {
        int i;
        byte *p2, *p1;
        for(i=0, p1=data, p2=(byte*)x; i < 16; i++, p2 += 4 )
        {
            p2[3] = *p1++;
            p2[2] = *p1++;
            p2[1] = *p1++;
            p2[0] = *p1++;
        }
    }
    else
    {
        memcpy(x, data, 64);
    }

    a = aa = hd->h0;
    b = bb = hd->h1;
    c = cc = hd->h2;
    d = dd = hd->h3;
    e = ee = hd->h4;

    for(j=0; j < 80; j++ )
    {
        t = a + F( j, b, c, d ) + x[ r[j] ] + K(j);
        rbits = s[j];
        a = rol(rbits, t) + e;
        c = rol(10,c);
        t = a; 
        a = e; 
        e = d; 
        d = c; 
        c = b; 
        b = t;

        t = aa + F(79-j, bb, cc, dd ) + x[ rr[j] ] + KK(j);
        rbits = ss[j];
        aa = rol(rbits, t) + ee;
        cc = rol(10,cc);
        t = aa; 
        aa = ee; 
        ee = dd; 
        dd = cc; 
        cc = bb; 
        bb = t;
    }

    t = hd->h1 + c + dd;
    hd->h1 = hd->h2 + d + ee;
    hd->h2 = hd->h3 + e + aa;
    hd->h3 = hd->h4 + a + bb;
    hd->h4 = hd->h0 + b + cc;
    hd->h0 = t;
}



/* Update the message digest with the contents
 * of INBUF with length INLEN.
 */
void rmd160Update( RMD160_CONTEXT *hd, byte *inbuf, uint32_t inlen)
{
    if( hd->count == 64 )
        { /* flush the buffer */
        transform( hd, hd->buf );
        hd->count = 0;
        hd->nblocks++;
    }
    if( !inbuf ) return;

    if( hd->count )
        {
        for( ; inlen && hd->count < 64; inlen-- )
            hd->buf[hd->count++] = *inbuf++;
        rmd160Update( hd, NULL, 0 ); /* just do the above buffer flush */
        if( !inlen ) return;
    }

    while( inlen >= 64 )
        {
        transform( hd, inbuf );
        hd->count = 0;
        hd->nblocks++;
        inlen -= 64;
        inbuf += 64;
    }
    for( ; inlen && hd->count < 64; inlen-- )
        hd->buf[hd->count++] = *inbuf++;
}


/* The routine terminates the computation
 */

void rmd160Final(RMD160_CONTEXT **phd,
byte digest[RMD160HASHSIZE], size_t length)
{
    RMD160_CONTEXT *hd = *phd;
    uint32_t t, msb, lsb;
    byte *p;

    rmd160Update(hd, NULL, 0); /* flush */

    msb = 0;
    t = hd->nblocks;
    if( (lsb = (t << 6)) < t ) msb++;/* multiply by 64 to make a byte count */

    msb += t >> 26;
    t = lsb;
    if( (lsb = (t + hd->count)) < t ) msb++;/* add the count */

    t = lsb;
    if( (lsb = (t << 3)) < t ) msb++; /* multiply by 8 to make a bit count */

    msb += t >> 29;

    if( hd->count < 56 )
        { /* enough room */
        hd->buf[hd->count++] = 0x80; /* pad */
        while( hd->count < 56 )
            hd->buf[hd->count++] = 0; /* pad */
    }
    else
        { /* need one extra block */
        hd->buf[hd->count++] = 0x80; /* pad character */
        while( hd->count < 64 )
            hd->buf[hd->count++] = 0;
        rmd160Update(hd, NULL, 0); /* flush */
        ;
        memset(hd->buf, 0, 56 ); /* fill next block with zeroes */
    }
    /* append the 64 bit count */
    hd->buf[56] = (byte)(lsb );
    hd->buf[57] = (byte)(lsb >> 8);
    hd->buf[58] = (byte)(lsb >> 16);
    hd->buf[59] = (byte)(lsb >> 24);
    hd->buf[60] = (byte)(msb );
    hd->buf[61] = (byte)(msb >> 8);
    hd->buf[62] = (byte)(msb >> 16);
    hd->buf[63] = (byte)(msb >> 24);
    transform( hd, hd->buf );

    /* and unpack the result */
    p = digest;

    if(!little_endian())
        {
        *p++ = (byte)(hd->h0 );
        *p++ = (byte)(hd->h0 >> 8);
        *p++ = (byte)(hd->h0 >> 16);
        *p++ = (byte)(hd->h0 >> 24);

        *p++ = (byte)(hd->h1 );
        *p++ = (byte)(hd->h1 >> 8);
        *p++ = (byte)(hd->h1 >> 16);
        *p++ = (byte)(hd->h1 >> 24);

        *p++ = (byte)(hd->h2 );
        *p++ = (byte)(hd->h2 >> 8);
        *p++ = (byte)(hd->h2 >> 16);
        *p++ = (byte)(hd->h2 >> 24);

        *p++ = (byte)(hd->h3 );
        *p++ = (byte)(hd->h3 >> 8);
        *p++ = (byte)(hd->h3 >> 16);
        *p++ = (byte)(hd->h3 >> 24);

        *p++ = (byte)(hd->h4 );
        *p++ = (byte)(hd->h4 >> 8);
        *p++ = (byte)(hd->h4 >> 16);
        *p = (byte)(hd->h4 >> 24);
    }
    else
    { /* allow for misalignment */
        memcpy(p, &hd->h0, 4); 
        p+= 4;
        memcpy(p, &hd->h1, 4); 
        p+= 4;
        memcpy(p, &hd->h2, 4); 
        p+= 4;
        memcpy(p, &hd->h3, 4); 
        p+= 4;
        memcpy(p, &hd->h4, 4);
    }
    if(length > 0) zfree((void**)phd, length);
}


#ifdef SELF_TESTING
#include <stdio.h>

void * zmalloc(size_t n)
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

boolean little_endian()
{
#if !defined(__BORLANDC__) && !defined( _MSC_VER)
#error "ensure little_endian() correct!"
#endif
    return TRUE;
}

/* print a digest */
void rmd160_print(byte digest[RMD160HASHSIZE])
{
    int i,j;
    for (i = 0; i < 5; i++) {
        for (j = 0; j < 4; j++) {
            printf("%02X", digest[i*4+j]);
        }
        putchar(' ');
    }
    putchar('\n');
}

#ifdef __BORLANDC__
#pragma warn -pia
#endif
int main()
{
    unsigned char digest[RMD160HASHSIZE];
    size_t length;
    RMD160_CONTEXT *rmd160_info;
    byte a[8192];
    long count;
    int i;
    for(i=0;i<8192;i++) a[i] = 'a';

    printf("9c1185a5 c5e9fc54 61280897 7ee8f548 b2258d31 expected\n");
    rmd160Init(&rmd160_info, &length);
    rmd160Update(rmd160_info, (byte*)"", 0);
    rmd160Final(&rmd160_info, digest, length);
    rmd160_print(digest);

    printf("0bdc9d2d 256b3ee9 daae347b e6f4dc83 5a467ffe expected\n");
    rmd160Init(&rmd160_info, &length);
    rmd160Update(rmd160_info, (byte*)"a", 1);
    rmd160Final(&rmd160_info, digest, length);
    rmd160_print(digest);

    printf("8eb208f7 e05d987a 9b044a8e 98c6b087 f15a0bfc expected\n");
    rmd160Init(&rmd160_info, &length);
    rmd160Update(rmd160_info, (byte*)"abc", 3);
    rmd160Final(&rmd160_info, digest, length);
    rmd160_print(digest);

    printf("5d0689ef 49d2fae5 72b881b1 23a85ffa 21595f36 expected\n");
    rmd160Init(&rmd160_info, &length);
    rmd160Update(rmd160_info, (byte*)"message digest", strlen("message digest"));
    rmd160Final(&rmd160_info, digest, length);
    rmd160_print(digest);

    printf("f71c2710 9c692c1b 56bbdceb 5b9d2865 b3708dbc expected\n");
    rmd160Init(&rmd160_info, &length);
    rmd160Update(rmd160_info, (byte*)"abcdefghijklmnopqrstuvwxyz", 26);
    rmd160Final(&rmd160_info, digest, length);
    rmd160_print(digest);

    printf("12a05338 4a9c0c88 e405a06c 27dcf49a da62eb2b expected\n");
    rmd160Init(&rmd160_info, &length);
    rmd160Update(rmd160_info, (byte*)
    "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq",56);
    rmd160Final(&rmd160_info, digest, length);
    rmd160_print(digest);

    printf("b0e20b6e 31166402 86ed3a87 a5713079 b21f5189 expected\n");
    rmd160Init(&rmd160_info, &length);
    rmd160Update(rmd160_info, (byte*)
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789",62);
    rmd160Final(&rmd160_info, digest, length);
    rmd160_print(digest);

    printf("9b752e45 573d4b39 f4dbd332 3cab82bf 63326bfb expected\n");
    rmd160Init(&rmd160_info, &length);
    rmd160Update(rmd160_info, (byte*)
    "12345678901234567890123456789012345678901234567890123456789012345678901234567890",80);
    rmd160Final(&rmd160_info, digest, length);
    rmd160_print(digest);

    printf("52783243 c1697bdb e16d37f9 7f68f083 25dc1528 expected\n");
    rmd160Init(&rmd160_info, &length);
    for(count=1000000L; count > 0; count -=8192)
    {
        int l = (count > 8192) ? 8192 : (int) count;
        rmd160Update(rmd160_info, a, l);
        printf(".");
    }
    printf("\n");
    rmd160Final(&rmd160_info, digest, length);
    rmd160_print(digest);

    return 0;
}
#endif
/* end of file rmd160.c */

