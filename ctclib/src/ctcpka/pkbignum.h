/* pkbignum.h
 **
 **  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 **  Heimdall <heimdall@bifroest.demon.co.uk>  1996
 **  All rights reserved.  For full licence details see file licences.c
 */
#ifndef _pkbignum
#define _pkbignum
#include "abstract.h"
#include "pkcipher.h"

int modularInverse(bignump result, bignum input, bignum modulus);
common_error findPrime(bignump prime, uint16_t length, uint16_t topBits,
prime_method method, bignum mutual);
byte mixedRandom(void);
boolean longRandom(uint16_t length, uint16_t topBits, bignump result);
uint16_t randomShort(void);

#endif

/* end of file pkbignum.h */
