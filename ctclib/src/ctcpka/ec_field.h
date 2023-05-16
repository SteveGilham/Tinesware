/* ec_field.h
 **
 ** Elliptic curve public key operations; based on the public domain code
 ** in george.barwood@dial.pipex.com's Pegwit v8.1 PKE system
 **
 **  Specialised to the case with GF_L 15 and GF_K 17
 **
 **  Elliptic curve encryption support routines coded and copyright
 **  Mr. Tines <tines@windsong.demon.co.uk> 1997
 **  All rights reserved.  For full licence details see file licences.c
 */

#ifndef _ec_field
#define _ec_field

#include "ec_param.h"
#include "abstract.h"

#define GF_POINT_UNITS (2*(GF_K+1))

#define BITS_PER_LUNIT 16
typedef uint16_t lunit;
typedef uint16_t ltemp;

/* types are passed promiscuously about within the ec code */
typedef lunit gfPoint [GF_POINT_UNITS];

/* interface functions: */

boolean gfInit (void);
/* initialize the library ---> MUST be called before any other gf-function */

void gfQuit (void);
/* perform housekeeping for library termination */

boolean gfEqual (const gfPoint p, const gfPoint q);
/* evaluates to 1 if p == q, otherwise 0 (or an error code) */

void gfClear (gfPoint p);
/* sets p := 0, clearing entirely the content of p */

void gfCopy (gfPoint p, const gfPoint q);
/* sets p := q */

void gfAdd (gfPoint p, const gfPoint q, const gfPoint r);
/* sets p := q + r */

void gfMultiply (gfPoint r, const gfPoint p, const gfPoint q);

/* sets r := p * q mod (x^GF_K + x^GF_T + 1) */


void gfSmallDiv (gfPoint p, lunit b);

/* sets p := (b^(-1))*p mod (x^GF_K + x^GF_T + 1) for b != 0 (of course...) */



void gfSquare (gfPoint p, const gfPoint q);
/* sets p := q^2 mod (x^GF_K + x^GF_T + 1) */

int gfInvert (gfPoint p, const gfPoint q);
/* sets p := q^(-1) mod (x^GF_K + x^GF_T + 1) */
/* warning: p and q must not overlap! */

void gfSquareRoot (gfPoint p, lunit b);
/* sets p := sqrt(b) = b^(2^(GF_M-1)) */

boolean gfTrace (const gfPoint p);
/* quickly evaluates to the trace of p */

int gfQuadSolve (gfPoint p, const gfPoint q);
/* sets p to a solution of p^2 + p = q */

boolean gfYbit (const gfPoint p);
/* evaluates to the rightmost (least significant) bit of p (or an error code) */

void gfPack (const gfPoint p, bignump k, boolean clearLowBit);
/* packs a field point into a bignum, perhaps with low bit clear */

void gfUnpack (gfPoint p, bignum k, boolean skipLowBit);
/* unpacks a bignum into a field point
 *       perhaps starting with lowest but one bit */
#endif

/* end of file ec_field.h */

