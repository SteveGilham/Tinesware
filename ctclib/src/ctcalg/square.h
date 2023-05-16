/* square.h - header file for square.c
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1996
**  All rights reserved.  For full licence details see file licences.c
*/
#ifndef _square
#define _square
#include "basic.h"

#define SQUAREKEYSIZE 16
#define SQUAREBLOCKSIZE 16

/* N.B. Initialisation vector argument removed because it is always zero */
void initSquare(byte *key, int triple, void **keysched, size_t *length);
void ecbSquare(void *keysched, int triple, int encrypt, byte *in, byte *out);
void destroySquare(void **keysched, size_t length);

#endif

/* end of file tea.h */

