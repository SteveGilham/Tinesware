/********************************************/
/* ec_curve.c
 **
 ** Elliptic curve operations; based on the public domain code
 ** in george.barwood@dial.pipex.com's Pegwit v8.1 PKE system
 **
 **  Elliptic curve encryption support routines coded and copyright
 **  Mr. Tines <tines@windsong.demon.co.uk> 1997
 **  All rights reserved.  For full licence details see file licences.c
 */
/*           */
/* elliptic curves over GF(2^(GF_L*GF_K)) */
/*           */
/********************************************/

/*#include <stdlib.h>
 * #include <time.h>*/

#include "ec_curve.h"
#include "ec_field.h"
#include "ec_param.h"
/*
 *  const vlPoint prime_order = {
 *   16U, 0xcd31U, 0x42bbU, 0x2584U, 0x5e0dU, 0x2d8bU, 0x4bf7U, 0x840eU, 0x0547U,
 *     0xbec3U, 0xed9bU, 0x691cU, 0x2314U, 0x81b8U, 0xd850U, 0x026dU, 0x0001U,
 *  };
 *  Now defined in bignums.c
 */
const ecPoint curve_point = {
    {
        17U,
        0x38ccU, 0x052fU, 0x2510U, 0x45aaU, 0x1b89U, 0x4468U, 0x4882U, 0x0d67U,
        0x4febU, 0x55ceU, 0x0025U, 0x4cb7U, 0x0cc2U, 0x59dcU, 0x289eU, 0x65e3U,
        0x56fdU,         }
    ,
    {
        17U,
        0x31a7U, 0x65f2U, 0x18c4U, 0x3412U, 0x7388U, 0x54c1U, 0x539bU, 0x4a02U,
        0x4d07U, 0x12d6U, 0x7911U, 0x3b5eU, 0x4f0eU, 0x216fU, 0x2bf2U, 0x1974U,
        0x20daU,         }
};


void ecCopy (ecPoint *p, const ecPoint *q)
/* sets p := q */
{
    gfCopy (p->x, q->x);
    gfCopy (p->y, q->y);
}/* ecCopy */


boolean ecCalcY (ecPoint *p, boolean ybit)
/* given the x coordinate of p, evaluate y such that y^2 + x*y = x^3 + EC_B */
{
    gfPoint a, b, t;

    b[0] = 1;
    b[1] = EC_B;
    if (p->x[0] == 0)
    {
        /* elliptic equation reduces to y^2 = EC_B: */
        gfSquareRoot (p->y, EC_B);
        return TRUE;
    }
    /* evaluate alpha = x^3 + b = (x^2)*x + EC_B: */
    gfSquare (t, p->x);    /* keep t = x^2 for beta evaluation */
    gfMultiply (a, t, p->x);
    gfAdd (a, a, b);    /* now a == alpha */
    if (a[0] == 0)
    {
        p->y[0] = 0;
        /* destroy potentially sensitive data: */
        gfClear (a);
        gfClear (t);
        return TRUE;
    }
    /* evaluate beta = alpha/x^2 = x + EC_B/x^2 */
    gfSmallDiv (t, EC_B);
    gfInvert (a, t);
    gfAdd (a, p->x, a);    /* now a == beta */
    /* check if a solution exists: */
    if (gfTrace (a))
    {
        /* destroy potentially sensitive data: */
        gfClear (a);
        gfClear (t);
        return FALSE;        /* no solution */
    }
    /* solve equation t^2 + t + beta = 0 so that gfYbit(t) == ybit: */
    gfQuadSolve (t, a);
    if (gfYbit (t) != ybit)
    {
        t[1] ^= 1;
    }
    /* compute y = x*t: */
    gfMultiply (p->y, p->x, t);
    /* destroy potentially sensitive data: */
    gfClear (a);
    gfClear (t);
    return TRUE;
}/* ecCalcY */


void ecAdd (ecPoint *p, const ecPoint *q)
/* sets p := p + q */
{
    gfPoint lambda, t, tx, ty, x3;

    /* first check if there is indeed work to do (q != 0): */
    if (q->x[0] != 0 || q->y[0] != 0)
    {
        if (p->x[0] != 0 || p->y[0] != 0)
        {
            /* p != 0 and q != 0 */
            if (gfEqual (p->x, q->x))
            {
                /* either p == q or p == -q: */
                if (gfEqual (p->y, q->y))
                {
                    /* points are equal; double p: */
                    ecDouble (p);
                }
                else
                {
                    /* must be inverse: result is zero */
                    /* (should assert that q->y = p->x + p->y) */
                    p->x[0] = p->y[0] = 0;
                }
            }
            else
            {
                /* p != 0, q != 0, p != q, p != -q */
                /* evaluate lambda = (y1 + y2)/(x1 + x2): */
                gfAdd (ty, p->y, q->y);
                gfAdd (tx, p->x, q->x);
                gfInvert (t, tx);
                gfMultiply (lambda, ty, t);
                /* evaluate x3 = lambda^2 + lambda + x1 + x2: */
                gfSquare (x3, lambda);
                gfAdd (x3, x3, lambda);
                gfAdd (x3, x3, tx);
                /* evaluate y3 = lambda*(x1 + x3) + x3 + y1: */
                gfAdd (tx, p->x, x3);
                gfMultiply (t, lambda, tx);
                gfAdd (t, t, x3);
                gfAdd (p->y, t, p->y);
                /* deposit the value of x3: */
                gfCopy (p->x, x3);
            }
        }
        else
        {
            /* just copy q into p: */
            gfCopy (p->x, q->x);
            gfCopy (p->y, q->y);
        }
    }
}/* ecAdd */


void ecSub (ecPoint *p, const ecPoint *r)
/* sets p := p - r */
{
    ecPoint t;

    gfCopy (t.x, r->x);
    gfAdd (t.y, r->x, r->y);
    ecAdd (p, &t);
}/* ecSub */

void ecDouble (ecPoint *p)
/* sets p := 2*p */
{
    gfPoint lambda, t1, t2;

    /* evaluate lambda = x + y/x: */
    gfInvert (t1, p->x);
    gfMultiply (lambda, p->y, t1);
    gfAdd (lambda, lambda, p->x);
    /* evaluate x3 = lambda^2 + lambda: */
    gfSquare (t1, lambda);
    gfAdd (t1, t1, lambda);    /* now t1 = x3 */
    /* evaluate y3 = x^2 + lambda*x3 + x3: */
    gfSquare (p->y, p->x);
    gfMultiply (t2, lambda, t1);
    gfAdd (p->y, p->y, t2);
    gfAdd (p->y, p->y, t1);
    /* deposit the value of x3: */
    gfCopy (p->x, t1);
}/* ecDouble */


void ecMultiply (ecPoint *p, const bignum k)
/* sets p := k*p */
{
    bignum h;
    unsigned z, hi, ki;
    uint16_t i;
    ecPoint r;

    init_mpn(&h);

    gfCopy (r.x, p->x);
    p->x[0] = 0;
    gfCopy (r.y, p->y);
    p->y[0] = 0;
    triple_mpn (&h, k);
    z = length_mpn (h) - 1;    /* so vlTakeBit (h, z) == 1 */
    i = 1;
    for (;;)
    {
        hi = nthBitSet_mpn (h, i);
        ki = nthBitSet_mpn (k, i);
        if (hi == 1 && ki == 0)
        {
            ecAdd (p, &r);
        }
        if (hi == 0 && ki == 1)
        {
            ecSub (p, &r);
        }
        if ((unsigned)i == z) break;
        i++;
        ecDouble (&r);
    }
    clear_mpn(&h);
}/* ecMultiply */


boolean ecYbit (const ecPoint *p)
/* evaluates to 0 if p->x == 0, otherwise to gfYbit (p->y / p->x) */
{
    gfPoint t1, t2;

    if (p->x[0] == 0) {
        return FALSE;
    }
    else {
        gfInvert (t1, p->x);
        gfMultiply (t2, p->y, t1);
        return gfYbit (t2);
    }
}/* ecYbit */


void ecPack (const ecPoint *p, bignump k)
/* packs a curve point into a bignum */
{
    if (p->x[0])
    {
        gfPack (p->x, k, TRUE);
        set0thBit_mpn(*k, ecYbit(p));
    }
    else
    {
        uint32_t value = (p->y[0]) ? 1 : 0;
        init_mpn(k);
        set_mpn(value, k);
    }
}/* ecPack */


void ecUnpack (ecPoint *p, const bignum k)
/* unpacks a bignum into a curve point */
{
    boolean yb = nthBitSet_mpn(k, 0);

    gfUnpack (p->x, k, TRUE);

    if (p->x[0] || yb) ecCalcY (p, yb);
    else p->y[0] = 0;
}/* ecUnpack */

/* end of file ec_curve.c */
