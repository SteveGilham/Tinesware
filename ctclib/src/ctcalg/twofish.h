/* twofish.h - header file for twofish.c

   Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 29-Dec-2000
   All rights reserved.  For full licence details see file licences.c
*/
#ifndef _twofish
#define _twofish
#include "basic.h"

/* This is another variable keysize cyper as per blowfish; instantiate
   just the PGP-5 compatible version for now */

#define TWOFISH128KEYSIZE 16
#define TWOFISH192KEYSIZE 24
#define TWOFISH256KEYSIZE 32

#define TWOFISHBLOCKSIZE 16

/* standard things needed for cipher.c */
void initTwofish128(byte *key, int triple, void **keysched, size_t *length);
void initTwofish192(byte *key, int triple, void **keysched, size_t *length);
void initTwofish256(byte *key, int triple, void **keysched, size_t *length);
#define ecbTwofish128 ecbTwofish
#define ecbTwofish192 ecbTwofish
#define ecbTwofish256 ecbTwofish
void ecbTwofish(void *keysched, int triple, int encrypt, byte *in, byte *out);

#define destroyTwofish128 destroyTwofish
#define destroyTwofish192 destroyTwofish
#define destroyTwofish256 destroyTwofish
void destroyTwofish(void **keysched, size_t length);

#endif
