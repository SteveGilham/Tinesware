/* ec_field.c
 **
 ** Elliptic curve public key operations; based on the public domain code
 ** in george.barwood@dial.pipex.com's Pegwit v8.1 PKE system.
 **
 **  Specialised to the case with GF_L 15 and GF_K 17
 **
 **  Elliptic curve encryption support routines coded and copyright
 **  Mr. Tines <tines@windsong.demon.co.uk> 1997
 **  All rights reserved.  For full licence details see file licences.c
 */
/********************************************/
/*                                          */
/* field operations on GF(2^(GF_L*GF_K))    */
/*                                          */
/********************************************/

/*#include <assert.h>
 *  #include <stdio.h>
 *  #include <stdlib.h>*/
#include <string.h>
/*#include <time.h>*/

#include "bignums.h"
#include "ec_field.h"
#include "ec_param.h"
#include "utils.h"
#include "usrbreak.h"

#if defined(_MSC_VER) && !defined (__BORLANDC__) 
// BUG in VS .NET result += (uint16_t) value triggers this
#pragma warning (disable: 4244) // conversion from int to short/uint16_t
#endif

#define BASE (1U << GF_L)
#define TOGGLE (BASE-1)

static boolean initialised = FALSE;

#ifdef __BORLANDC__
#ifdef __MSDOS__
#define MAX64K   /* 16 bit segmented memory - need to compensate */
#endif
#endif

#ifndef MAX64K /*i.e. not 16-bit segmented */
/*#error 32-bit are you sure*/
static lunit *expt=0;/* index range is [0..(BASE-2)] */
static lunit *logt=0;/* index range is [1..(BASE-1)],
 *                        but logt[0] is set to (BASE-1) */
#define EXPT(i) expt[(i)]
#define LOGT(i) logt[(i)]
#else
/*#error 16-bit - are you sure*/
#define HBASE (BASE>>1)
static lunit *expt0=0;
static lunit *expt1=0;
static lunit *logt0=0;
static lunit *logt1=0;
static ltemp EXPT(unsigned int i)
{
    return (i<(unsigned int)HBASE) ? expt0[i] : expt1[i-HBASE];
}
static ltemp LOGT(unsigned int i)
{
    return (i<(unsigned int)HBASE) ? logt0[i] : logt1[i-HBASE];
}
#endif /*16 bit*/


/* Ensure field initialised : the failure option should be more graceful! */
#define INITIALISE \
{if(!initialised){initialised = gfInit();}\
if(!initialised){bug_check("Out of memory in elliptic curve initialisation");}\
}

boolean gfInit (void)
/* initialize the library ---> MUST be called before any
 *       other serious gf-function */
{
    ltemp root= BASE | GF_ROOT, i, j;
#ifndef MAX64K
#ifndef __BORLANDC__
#if defined(_MSC_VER) 
#pragma warning (disable: 4127) // conditional expression is constant
#endif
	if(sizeof(size_t) <= 2)
    {
        bug_check("2-byte size_t requires MAX64k");
        return FALSE;
    }
#else
#if sizeof(size_t) <= 2
#error "2-byte size_t requires MAX64k"
#endif
#endif

    if ((logt = (lunit *) qmalloc (BASE * sizeof (lunit))) == NULL) {
        return FALSE;        /* not enough memory */
    }
    if ((expt = (lunit *) qmalloc (BASE * sizeof (lunit))) == NULL) {
        free (logt);
        return FALSE;        /* not enough memory */
    }
    expt[0] = 1;
#else
    if ((logt0 = (lunit *) qmalloc (HBASE * sizeof (lunit))) == NULL) {
        return FALSE;        /* not enough memory */
    }
    if ((expt0 = (lunit *) qmalloc (HBASE * sizeof (lunit))) == NULL) {
        free (logt0);
        return FALSE;        /* not enough memory */
    }
    if ((logt1 = (lunit *) qmalloc (HBASE * sizeof (lunit))) == NULL) {
        free(logt0); 
        free(expt0);
        return FALSE;        /* not enough memory */
    }
    if ((expt1 = (lunit *) qmalloc (HBASE * sizeof (lunit))) == NULL) {
        free(logt0); 
        free(expt0); 
        free (logt1);
        return FALSE;        /* not enough memory */
    }
    expt0[0] = 1;
#endif

    for (i = 1; i < BASE; i++)
    {
        j = (ltemp)(EXPT(i-1) << 1);
        if (j & BASE) j ^= root;

#ifndef MAX64K
        expt[i] = (lunit)j;
#else
        if(i<HBASE) expt0[i] = (lunit)j;
        else expt1[i-HBASE] = (lunit)j;
#endif

    }
    for (i = 0; i < TOGGLE; i++)
    {

#ifndef MAX64K
        logt[EXPT(i)] = (lunit)i;
#else
        if(EXPT(i)<HBASE) logt0[EXPT(i)] = (lunit)i;
        else logt1[EXPT(i)-HBASE] = (lunit)i;
#endif

    }

#ifndef MAX64K
    logt[0] = TOGGLE;    /* a trick used by gfMultiply, gfSquare, gfAddMul */
#else
    logt0[0] = TOGGLE;
#endif

    return TRUE;
}/* gfInit */


void gfQuit (void)
/* perform housekeeping for library termination */
{
#ifndef MAX64K
    if (expt) {
        qfree (expt); 
        expt = 0;
    }
    if (logt) {
        qfree (logt); 
        logt = 0;
    }
#else
    if(expt0) {
        qfree(expt0); 
        expt0 = 0;
    }
    if(expt1) {
        qfree(expt1); 
        expt1 = 0;
    }
    if(logt0) {
        qfree(logt0); 
        logt0 = 0;
    }
    if(logt1) {
        qfree(logt1); 
        logt1 = 0;
    }
#endif
    initialised = FALSE;
}/* gfQuit */


boolean gfEqual (const gfPoint p, const gfPoint q)
/* evaluates to TRUE if p == q, otherwise FALSE */
{
    return (boolean)(memcmp (p, q, p[0] + 1) ? FALSE : TRUE);
}/* gfEqual */


void gfClear (gfPoint p)
/* sets p := 0, clearing entirely the content of p */
{
    memset (p, 0, sizeof (gfPoint));
}/* gfClear */


void gfCopy (gfPoint p, const gfPoint q)
/* sets p := q */
{
    memcpy (p, q, (q[0] + 1) * sizeof (lunit));
}/* gfCopy */


void gfAdd (gfPoint p, const gfPoint q, const gfPoint r)
/* sets p := q + r */
{
    ltemp i;

    if (q[0] > r[0])
    {
        /* xor the the common-degree coefficients: */
        for (i = 1; i <= r[0]; i++)
        {
            p[i] = q[i] ^ r[i];
        }
        /* invariant: i == r[0] + 1 */
        memcpy (&p[i], &q[i], (q[0] - r[0]) * sizeof (lunit));
        /* deg(p) inherits the value of deg(q): */
        p[0] = q[0];
    }
    else if (q[0] < r[0])
    {
        /* xor the the common-degree coefficients: */
        for (i = 1; i <= q[0]; i++)
        {
            p[i] = q[i] ^ r[i];
        }
        /* invariant: i == q[0] + 1 */
        memcpy (&p[i], &r[i], (r[0] - q[0]) * sizeof (lunit));
        /* deg(p) inherits the value of deg(r): */
        p[0] = r[0];
    }
    else
    {    /* deg(q) == deg(r) */
        /* scan to determine deg(p): */
        for (i = q[0]; i > 0; i--)
        {
            if (q[i] ^ r[i]) break;
        }
        /* xor the the common-degree coefficients, if any is left: */
        for (p[0] = (lunit)i; i > 0; i--)
        {
            p[i] = q[i] ^ r[i];
        }
    }
}/* gfAdd */


static void gfReduce (gfPoint p)
/* reduces p mod the irreducible trinomial x^GF_K + x^GF_T + 1 */
{
    int i;

    for (i = p[0]; i > GF_K; i--)
    {
        p[i - GF_K] ^= p[i];
        p[i + GF_T - GF_K] ^= p[i];
        p[i] = 0;
    }
    if (p[0] > GF_K)
    {
        /* scan to update deg(p): */
        p[0] = GF_K;
        while (p[0] && p[p[0]]==0)
        {
            p[0]--;
        }
    }
}/* gfReduce */


void gfMultiply (gfPoint r, const gfPoint p, const gfPoint q)
/* sets r := p * q mod (x^GF_K + x^GF_T + 1) */
{
    int i, j;
    ltemp x, log_pi, log_qj;
    lunit lg[GF_K + 2];    /* this table should be cleared after use */

    INITIALISE

    if (p[0] && q[0]) {
        /* precompute logt[q[j]] to reduce table lookups: */
        for (j = q[0]; j; j--)
        {
            lg[j] = LOGT(q[j]);
        }
        /* perform multiplication: */
        gfClear (r);
        for (i = p[0]; i; i--)
        {
            if ((log_pi = LOGT(p[i])) != TOGGLE)
            {            /* p[i] != 0 */
                for (j = q[0]; j; j--)
                {
                    if ((log_qj = lg[j]) != TOGGLE)
                    {
                        r[i+j-1] ^= EXPT((x = (ltemp)(log_pi + log_qj))
                            >= TOGGLE ? x - TOGGLE : x);
                    }
                }
            }
        }
        r[0] = (lunit)(p[0] + q[0] - 1);
        /* reduce r mod (x^GF_K + x^GF_T + 1): */
        gfReduce (r);
    }
    else
    {
        /* set r to the null polynomial: */
        r[0] = 0;
    }
    /* destroy potentially sensitive data: */
    x = log_pi = log_qj = 0;
    memset (lg, x, sizeof (lg));
}/* gfMultiply */


void gfSquare (gfPoint r, const gfPoint p)
/* sets r := p^2 mod (x^GF_K + x^GF_T + 1) */
{
    int i;
    ltemp x;

    INITIALISE

    if (p[0]) {
        /* in what follows, note that (x != 0) =>
         *                  (x^2 = exp((2 * log(x)) % TOGGLE)): */
        i = p[0];
        if ((x = LOGT(p[i])) != TOGGLE)
        {        /* p[i] != 0 */
            r[2*i - 1] = EXPT((x += x) >= TOGGLE ? x - TOGGLE : x);
        }
        else
        {
            r[2*i - 1] = 0;
        }
        for (i = p[0] - 1; i; i--)
        {
            r[2*i] = 0;
            if ((x = LOGT(p[i])) != TOGGLE)
            {            /* p[i] != 0 */
                r[2*i - 1] = EXPT((x += x) >= TOGGLE ? x - TOGGLE : x);
            }
            else
            {
                r[2*i - 1] = 0;
            }
        }
        r[0] = (lunit)(2*p[0] - 1);
        /* reduce r mod (x^GF_K + x^GF_T + 1): */
        gfReduce (r);
    }
    else
    {
        r[0] = 0;
    }
}/* gfSquare */


void gfSmallDiv (gfPoint p, lunit b)
/* sets p := (b^(-1))*p mod (x^GF_K + x^GF_T + 1) */
/* b non-zero */
{
    int i;
    ltemp x, lb = LOGT(b);

    for (i = p[0]; i; i--)
    {
        if ((x = LOGT(p[i])) != TOGGLE)
        {        /* p[i] != 0 */
            p[i] = EXPT((x += (ltemp)(TOGGLE - lb)) >= TOGGLE ? x - TOGGLE : x);
        }
    }
}/* gfSmallDiv */


static void gfAddMul (gfPoint a, ltemp alpha, ltemp j, gfPoint b)
{
    ltemp i, x, la = LOGT(alpha);
    lunit *aj = &a[j];

    while (a[0] < j + b[0])
    {
        a[0]++; 
        a[a[0]] = 0;
    }
    for (i = b[0]; i; i--)
    {
        if ((x = LOGT(b[i])) != TOGGLE)
        {        /* b[i] != 0 */
            aj[i] ^= EXPT((x += la) >= TOGGLE ? x - TOGGLE : x);
        }
    }
    while (a[0] && a[a[0]]==0)
    {
        a[0]--;
    }
}/* gfAddMul */


int gfInvert (gfPoint b, const gfPoint a)
/* sets b := a^(-1) mod (x^GF_K + x^GF_T + 1) */
/* warning: a and b must not overlap! */
{
    gfPoint c, f, g;
    ltemp x, j, alpha;

    INITIALISE
        if (a[0] == 0)
    {
        /* a is not invertible */
        return 1;
    }

    /* initialize b := 1; c := 0; f := p; g := x^GF_K + x^GF_T + 1: */
    b[0] = 1; 
    b[1] = 1;
    c[0] = 0;
    gfCopy (f, a);
    gfClear (g);
    g[0] = GF_K + 1; 
    g[1] = 1; 
    g[GF_T + 1] = 1; 
    g[GF_K + 1] = 1;

    for (;;)
    {
        if (f[0] == 1)
        {
            /*assert (f[1] != 0);*/
            gfSmallDiv (b, f[1]);
            /* destroy potentially sensitive data: */
            gfClear (c); 
            gfClear (f); 
            gfClear (g); 
            x = j = alpha = 0;
            return (int)x;            /* zero, done this way to shut up the compiler */
        }
        if (f[0] < g[0]) goto SWAP_FG;

SWAP_GF:
        j = (ltemp)(f[0] - g[0]);
        x = (ltemp)(LOGT(f[f[0]]) - LOGT(g[g[0]]) + TOGGLE);
        alpha = EXPT(x >= TOGGLE ? x - TOGGLE : x);
        gfAddMul (f, alpha, j, g);
        gfAddMul (b, alpha, j, c);
    }

    /* basically same code with b,c,f,g swapped */
    for (;;)
    {
        if (g[0] == 1)
        {
            /*assert (g[1] != 0);*/
            gfSmallDiv (c, g[1]);
            gfCopy (b, c);
            /* destroy potentially sensitive data: */
            gfClear (c); 
            gfClear (f); 
            gfClear (g); 
            x = j = alpha = 0;
            return (int)x;            /* zero, done this way to shut up the compiler */
        }
        if (g[0] < f[0]) goto SWAP_GF;

SWAP_FG:
        j = (ltemp)(g[0] - f[0]);
        x = (ltemp)(LOGT(g[g[0]]) - LOGT(f[f[0]]) + TOGGLE);
        alpha = EXPT(x >= TOGGLE ? x - TOGGLE : x);
        gfAddMul (g, alpha, j, f);
        gfAddMul (c, alpha, j, b);
    }
}/* gfInvert */


void gfSquareRoot (gfPoint p, lunit b)
/* sets p := sqrt(b) = b^(2^(GF_M-1)) */
{
    int i;
    gfPoint q;

    INITIALISE

    q[0] = 1; 
    q[1] = b;
    /* GF_M - 1 is even */
    gfCopy (p, q);
    i = GF_M - 1;

    while (i)
    {
        gfSquare (p, p);
        gfSquare (p, p);
        i -= 2;
    }
}/* gfSquareRoot */

boolean gfTrace (const gfPoint p)
/* quickly evaluates to the trace of p */
{
    /*
     *      Let GF(2^m) be considered as a space vector over GF(2).
     *      The trace function Tr: GF(2^m) -> GF(2) is linear:
     *      Tr(p + q) = Tr(p) + Tr(q) and Tr(k*p) = k*Tr(p) for k in GF(2).
     *     
     *      Hence, the trace of any field element can be efficiently computed
     *      if the trace is known for a basis of GF(2^m).
     *     
     *      In other terms, let p(x) = SUM {p_i * x^i} for i = 0...m-1;
     *      then Tr(p) = SUM {p_i * Tr(x^i)} for i = 0...m-1.
     *     
     *      Surprisingly enough (at least for me :-), it is often the case that
     *      Tr(p) is simply Tr(p_0).
     *     
     *      These properties are exploited in this fast algorithm by George Barwood.
     */
    return (boolean)(p[0] ? p[1] & 1 : 0);
}/* gfTrace */


int gfQuadSolve (gfPoint p, const gfPoint beta)
/* sets p to a solution of p^2 + p = beta */
{
    int i;

    INITIALISE
    /* check if a solution exists: */
    if (gfTrace (beta) != 0)
    {
        return 1;        /* no solution */
    }
    /* GF_M is odd: compute half-trace */
    gfCopy (p, beta);
    for (i = 0; i < GF_M/2; i++) {
        gfSquare (p, p);
        gfSquare (p, p);
        gfAdd (p, p, beta);
    }
    return 0;
}/* gfQuadSolve */


boolean gfYbit (const gfPoint p)
/* evaluates to the rightmost (least significant) bit of p (or an error code) */
{
    return (boolean)(p[0] ? (p[1] & 1) : FALSE);
}/* gfYbit */


void gfPack (const gfPoint p, bignump k, boolean clearLowBit)
/* packs a field point into a bignum, perhaps with bottom bit clear */
{
    byte freeLength = (byte)(clearLowBit ? 1 : 0);
    if(! pack16_mpn (k, (uint16_t *)(p+1), p[0], (byte)GF_L, freeLength))
    {
        bug_check("Insufficient memory while packing field point");
    }
}/* gfPack */


void gfUnpack (gfPoint p, bignum k, boolean skipLowBit)
/* unpacks a bignum into a field point, skipping lowest bit */
{
    byte freeLength = (byte)(skipLowBit ? 1 : 0);
    p[0] = GF_POINT_UNITS - 1;
    if(! unpack16_mpn(k, p+1, p, (byte)GF_L, freeLength))
    {
        bug_check("Bignum too long to be a field point");
    }
}/* gfUnpack */


/* end of file ec_field.c */
