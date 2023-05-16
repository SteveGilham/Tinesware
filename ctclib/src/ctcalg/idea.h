/* idea.h - header file for idea.c as used in CTC
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
**  Heimdall <heimdall@bifroest.demon.co.uk>  1996
**  All rights reserved.  For full licence details see file licences.c
*/
#ifndef _idea
#define _idea
#include "basic.h"  
#define IDEAKEYSIZE 16
#define IDEABLOCKSIZE 8

/* N.B. Initialisation vector argument removed because it is always zero */
void initIDEA(byte *key, int triple, void **keysched, size_t *length);
void ecbIDEA(void *keysched, int triple, int encrypt, byte *in, byte *out);
void destroyIDEA(void **keysched, size_t length);

#endif 
/* end of file idea.h */
