/* CAST5.h - header file for cast5.c

   Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1997
   All rights reserved.  For full licence details see file licences.c
*/
#ifndef _cast5
#define _cast5
#include "basic.h"

/* This is another variable keysize cyper as per blowfish; instantiate
   just the PGP-5 compatible version for now */

#define CAST5128KEYSIZE 16
#define CAST5128BLOCKSIZE 8

/* standard things needed for cipher.c */
void initCAST5_128(byte *key, int triple, void **keysched, size_t *length);
void ecbCAST5_128(void *keysched, int triple, int encrypt, byte *in, byte *out);
void destroyCAST5_128(void **keysched, size_t length);

#endif
