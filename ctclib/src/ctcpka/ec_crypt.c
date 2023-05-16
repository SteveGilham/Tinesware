/* ec_crypt.c
 **
 ** Elliptic curve public key operations; based on the public domain code
 ** in george.barwood@dial.pipex.com's Pegwit v8.1 PKE system
 **
 **  Elliptic curve encryption support routines coded and copyright
 **  Mr. Tines <tines@windsong.demon.co.uk> 1997
 **  All rights reserved.  For full licence details see file licences.c
 **
 ** DLL related fiddling with prime order from global value to function return
 **  -- Mr. Tines 15-Sep-97
 */
/********************************************/
/* cryptographic primitives and schemes */
/********************************************/

/*#define TEST*/
#ifdef TEST
#undef TEST
#endif

#include "bignums.h"
#include "callback.h"
#include "ec_curve.h"
#include "ec_crypt.h"
#include "pkautils.h"
#include "random.h"
#include "pkcipher.h"
#include <assert.h>

#ifdef TEST
#include <stdio.h> /* for test case */

static void dumpState(bignump x)
{
    int i,l;
    byte buff2[40];
    l = put_mpn(buff2, x);
    printf("Byte count = %d\n", l);
    fflush(stdout);
    for(i=0; i<l; ++i) printf("%02x",buff2[i+2]);
    printf("\n");
    fflush(stdout);
}
#ifdef DUMP
#define gdump(p) dumpGFPoint(p)
static void dumpGFPoint(lunit *p)
{
    int i;
    for(i=0; i<GF_POINT_UNITS; ++i)
        printf("%04x", p[i]);
    printf("\n");
    fflush(stdout);
}
#define edump(p) dumpECPoint(p)
static void dumpECPoint(ecPoint *p)
{
    printf("x = ");
    dumpGFPoint((lunit*)p->x);
    printf("y = ");
    dumpGFPoint((lunit*)p->y);
}
#else
#define gdump(p)
#define edump(p);
#endif
#endif

static void simpleCondition(short severity, short code, cb_context context)
{
    cb_condition condition = {
        0, 0, 0, 0, NULL, NULL             };

    condition.severity = severity;
    condition.module = CB_PKE;
    condition.code = code;
    condition.context = (short) context;
    cb_information(&condition);
}


void cpMakePublicKey (bignump vlPublicKey, bignum vlPrivateKey)
{
    ecPoint ecPublicKey;    /* constructed as CP*private */

    ecCopy (&ecPublicKey, &curve_point);
    ecMultiply (&ecPublicKey, vlPrivateKey);
    ecPack (&ecPublicKey, vlPublicKey);
}/* cpMakePublicKey */


void cpEncodeSecret (bignum vlPublicKey,
bignump vlMessage, bignump vlSecret)
{
    ecPoint q;

    ecCopy (&q, &curve_point);
    ecMultiply (&q, *vlSecret);
    ecPack (&q, vlMessage);    /* CP * random is sent */


    ecUnpack (&q, vlPublicKey);    /* (CP * private) * random used */
    ecMultiply (&q, *vlSecret);

    wipe_mpn (vlSecret);
    clear_mpn(vlSecret);
    gfPack (q.x, vlSecret, FALSE);
}/* cpEncodeSecret */


void cpDecodeSecret (bignum vlPrivateKey,
bignum vlMessage, bignump d)
{
    ecPoint q;

    ecUnpack (&q, vlMessage);    /* CP * random */
    ecMultiply (&q, vlPrivateKey);    /* CP * private * random recovered */
    gfPack(q.x, d, FALSE);
}/* ecDecodeSecret */

void cpSign(bignum vlPrivateKey, bignum k, bignum vlMac, bignum sig[])
{
    ecPoint q;
    bignum tmp, prime_order;
    boolean ok = set_prime_order(&prime_order);
    assert(ok);

    init_mpn(&tmp);


    ecCopy( &q, &curve_point );
    ecMultiply( &q, k);    /* CP * arbitrary */
    gfPack(q.x, &sig[ECSIG_R], FALSE);
    add_mpn( &sig[ECSIG_R], vlMac );
    remainder_mpn( sig[ECSIG_R], prime_order, FALSE );
    if (length_mpn(sig[ECSIG_R]) != 0)    /* 0 => bad choice of arbitrary */
    {
        multiply_mpn(&tmp, vlPrivateKey, sig[ECSIG_R]);
        remainder_mpn(tmp, prime_order, FALSE );

        copy_mpn( &sig[ECSIG_S], k );
        if ( gt_mpn( tmp, sig[ECSIG_S]) ) add_mpn( &sig[ECSIG_S], prime_order );
        subtract_mpn( sig[ECSIG_S], tmp );
    }
    wipe_mpn(&tmp);
    clear_mpn(&prime_order);
}/* cpSign */

boolean cpVerify(bignum vlPublicKey, bignum vlMac, bignum sig[])
{
    ecPoint t1,t2;
    bignum t3,t4, prime_order;
    boolean result;
    boolean ok = set_prime_order(&prime_order);
    assert(ok);

    init_mpn(&t3);
    init_mpn(&t4);

    ecCopy( &t1, &curve_point );
    ecMultiply( &t1, sig[ECSIG_S]);
    ecUnpack( &t2, vlPublicKey );
    ecMultiply( &t2, sig[ECSIG_R]);
    ecAdd( &t1, &t2 );

    gfPack( t1.x, &t4, FALSE );
    remainder_mpn( t4, prime_order, FALSE );

    copy_mpn( &t3, sig[ECSIG_R]);
    if ( gt_mpn( t4, t3 ) ) add_mpn( &t3, prime_order );
    subtract_mpn( t3, t4 );
    result = eq_mpn( t3, vlMac );

    clear_mpn(&t3);
    clear_mpn(&t4);
    clear_mpn(&prime_order);
    return result;
}/* cpVerify */


boolean newGF2255key(seckey * sec_key, pubkey * pub_key)
{
    int i;
    byte buffer[32] = {
        0, 240,                     /*bits in key*/
        0xc9, 0xfc, 0x99, 0x5b,     /*32*/
        0x7d, 0xce, 0xff, 0x11,     /*64*/
        0xd0, 0x67, 0x78, 0x39,     /*96*/
        0xf5, 0xea, 0x0e, 0xd3,     /*128*/
        0xff, 0x82, 0x0b, 0x5b,     /*160*/
        0x22, 0x19, 0x75, 0x06,     /*192*/
        0x48, 0x4e, 0x8e, 0xf0,     /*224*/
        0xab, 0x7b            };    /*240*/

    simpleCondition(CB_STATUS, PKE_KEYGEN_SETUP, CB_KEYGEN);

#ifdef TEST
    printf("\007\007\007You are generating the Pegwit test key - not for live use\n");
    fflush(stdout);
#else
    randload((short) 256);    /* overkill! */
    for(i=0; i<30; ++i) buffer[i+2] = randombyte();
#endif

    init_mpn(&sec_key->pkdata.nums[ECSEC].plain);
    init_mpn(&pub_key->pkdata.nums[ECPUB]);

    get_mpn(&sec_key->pkdata.nums[ECSEC].plain, buffer);
    for(i=0; i<30; ++i) buffer[i+2] = randombyte();    /* splat! */
    cpMakePublicKey(&pub_key->pkdata.nums[ECPUB], sec_key->pkdata.nums[ECSEC].plain);
    pub_key->size=240;    /* Notional */
#ifdef TEST
    printf("Key generated\n");
    fflush(stdout);
    dumpState(&pub_key->pkdata.nums[ECPUB]);
    printf("cc23ea8bc28aac71ee19befcb2beba4b349cbdc020965e2411d48f6dfa28f4fd\n");
    fflush(stdout);
#endif
    return TRUE;
}
/* end of file ec_crypt.c */
