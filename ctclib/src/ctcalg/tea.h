/* tea.h - header file for tea.c  
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1996
**  All rights reserved.  For full licence details see file licences.c
*/
#ifndef _tea
#define _tea
#include "basic.h"

#define TEAKEYSIZE 16
#define TEABLOCKSIZE 8

/* N.B. Initialisation vector argument removed because it is always zero */
void initTEA(byte *key, int triple, void **keysched, size_t *length);
void ecbTEA(void *keysched, int triple, int encrypt, byte *in, byte *out);
void destroyTEA(void **keysched, size_t length);

#endif

/* end of file tea.h */

