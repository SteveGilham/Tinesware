/* blowfish.h - header file for blowfish.c  
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1996
**  All rights reserved.  For full licence details see file licences.c
*/
#ifndef _blowfish
#define _blowfish
#include "basic.h"

/* This is how the variable key-size can be handled, by defining the byte
 value of the blocks.  The repeat unit starts here... */

#define BLOW16KEYSIZE 16
#define BLOW16BLOCKSIZE 8

void initBlow16(byte *key, int triple, void **keysched, size_t *length);
void ecbBlow16(void *keysched, int triple, int encrypt, byte *in, byte *out);
void destroyBlow16(void **keysched, size_t length);

/* Provide 40-bit blowfish so that EC-PKE can handle triple encryption */
#define BLOW5KEYSIZE 5
#define BLOW5BLOCKSIZE 8

void initBlow5(byte *key, int triple, void **keysched, size_t *length);
void ecbBlow5(void *keysched, int triple, int encrypt, byte *in, byte *out);
void destroyBlow5(void **keysched, size_t length);

/* Provide 160-bit blowfish for GPG compatibility */
#define BLOW20KEYSIZE 20
#define BLOW20BLOCKSIZE 8

void initBlow20(byte *key, int triple, void **keysched, size_t *length);
void ecbBlow20(void *keysched, int triple, int encrypt, byte *in, byte *out);
void destroyBlow20(void **keysched, size_t length);

/* Repeat as required for different keylengths */

#endif

/* end of file blowfish.h */

