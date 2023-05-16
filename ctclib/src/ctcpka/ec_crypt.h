/* ec_crypt.h
 **
 ** Elliptic curve public key operations; based on the public domain code
 ** in george.barwood@dial.pipex.com's Pegwit v8.1 PKE system
 **
 **  Elliptic curve encryption support routines coded and copyright
 **  Mr. Tines <tines@windsong.demon.co.uk> 1997
 **  All rights reserved.  For full licence details see file licences.c
 */
#ifndef _ec_crypt
#define _ec_crypt

#define ECPUB 0

#define ECSEC 0

#define ECSIG_R 0
#define ECSIG_S 1

boolean newGF2255key(seckey * sec_key, pubkey * pub_key);

void cpMakePublicKey (bignump vlPublicKey, bignum vlPrivateKey);
void cpEncodeSecret (bignum vlPublicKey,
bignump vlMessage, bignump vlSecret);

void cpDecodeSecret (bignum vlPrivateKey, bignum vlMessage, bignump d);

void cpSign(bignum vlPrivateKey, bignum secret,
bignum mac, bignum cpSig[]);
boolean cpVerify(bignum vlPublicKey, bignum vlMac, bignum cpSig[] );

#endif

/* end of file ec_crypt.h */
