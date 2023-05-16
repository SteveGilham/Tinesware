/* ec_curve.h
 **
 ** Elliptic curve public key operations; based on the public domain code
 ** in george.barwood@dial.pipex.com's Pegwit v8.1 PKE system
 **
 **  Elliptic curve encryption support routines coded and copyright
 **  Mr. Tines <tines@windsong.demon.co.uk> 1997
 **  All rights reserved.  For full licence details see file licences.c
 */

#ifndef _ec_curve
#define _ec_curve

/*#include <stddef.h>*/

#include "bignums.h"
#include "ec_field.h"

typedef struct {/* move or remove? */
    gfPoint x, y;
}
ecPoint;

/* extern const vlPoint prime_order;  -> a bignum in bignums.h */
extern const ecPoint curve_point;

void ecCopy (ecPoint *p, const ecPoint *q);
/* sets p := q */

boolean ecCalcY (ecPoint *p, boolean ybit);
/* given the x coordinate of p, evaluate y such that y^2 + x*y = x^3 + EC_B */

void ecAdd (ecPoint *p, const ecPoint *r);
/* sets p := p + r */

void ecSub (ecPoint *p, const ecPoint *r);
/* sets p := p - r */

void ecDouble (ecPoint *p);
/* sets p := 2*p */

void ecMultiply (ecPoint *p, const bignum k);
/* sets p := k*p */

boolean ecYbit (const ecPoint *p);
/* evaluates to 0 if p->x == 0, otherwise to gfYbit (p->y / p->x) */

void ecPack (const ecPoint *p, bignump k);
/* packs a curve point into a bignum */

void ecUnpack (ecPoint *p, const bignum k);
/* unpacks a bignum into a curve point */
#endif

/* end of file ec_curve.h */
