/* safer.h - header file for tea.c
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1998
**  All rights reserved.  For full licence details see file licences.c
*/
#ifndef _safer
#define _safer
#include "basic.h"

#define SAFERBLOCKSIZE 8


typedef enum {
    k64 = 0,
    k128,
    sk64,
    sk128
}
SAFER_TYPE;

/* N.B. Initialisation vector argument removed because it is always zero */
void initSAFER(byte *key, int triple, void **keysched,
size_t *length, SAFER_TYPE type);
void ecbSAFER(void *keysched, int triple, int encrypt, byte *in, byte *out);
void destroySAFER(void **keysched, size_t length);
int SAFERkeySize(SAFER_TYPE type);

#endif

/* end of file tea.h */

