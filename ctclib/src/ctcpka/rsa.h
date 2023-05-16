/* rsa.h
 **
 **  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 **  Heimdall <heimdall@bifroest.demon.co.uk>  1996
 **  All rights reserved.  For full licence details see file licences.c
 */
#ifndef _rsa
#define _rsa
#include "abstract.h"

common_error rsa_public_operation(bignump outbuf, bignump inbuf, pubkey * pub_key);
common_error rsa_private_operation(bignump output, bignump input, seckey * sec_key);
boolean newRSAkey(keyType * keyType, seckey * sec_key, pubkey * pub_key);
boolean verifySecKeyRSA(seckey * sec_key);

#if defined (RSA_NONDLL_TEST)
/* RSA test routine */
int mutualFactor(pubkey * left, pubkey * right, char buffer[1000]);
#endif

#endif

/* end of file rsa.h */
