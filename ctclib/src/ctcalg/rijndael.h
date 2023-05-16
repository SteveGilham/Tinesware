/* rijndael.h - header file for rijndael.c

   Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 22-Oct-2000
   All rights reserved.  For full licence details see file licences.c
*/
#ifndef _rijndael
#define _rijndael
#include "basic.h"

/* This is another variable keysize cyper as per blowfish; instantiate
   just the PGP-5 compatible version for now */

#define AES128KEYSIZE 16
#define AES192KEYSIZE 24
#define AES256KEYSIZE 32

#define AESBLOCKSIZE 16

/* standard things needed for cipher.c */
void initAES128(byte *key, int triple, void **keysched, size_t *length);
void initAES192(byte *key, int triple, void **keysched, size_t *length);
void initAES256(byte *key, int triple, void **keysched, size_t *length);
#define ecbAES128 ecbAES
#define ecbAES192 ecbAES
#define ecbAES256 ecbAES
void ecbAES(void *keysched, int triple, int encrypt, byte *in, byte *out);

#define destroyAES128 destroyAES
#define destroyAES192 destroyAES
#define destroyAES256 destroyAES
void destroyAES(void **keysched, size_t length);

#endif
