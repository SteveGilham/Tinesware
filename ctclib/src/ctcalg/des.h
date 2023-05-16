/* des.h - header file for des.c as used in CTC
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
**  Heimdall <heimdall@bifroest.demon.co.uk>  1997
**  All rights reserved.  For full licence details see file licences.c
*/
#ifndef _des
#define _des
#include "basic.h"  
#define DESKEYSIZE 8
#define KDDESEXTRA 6
#define DESBLOCKSIZE 8

void initDES(byte *key, int triple, boolean decrypt, void **keysched, size_t *length);
void ecbDES(void *keysched, int triple, byte *in, byte *out);
void destroyDES(void **keysched, size_t length);

void initS3DES(byte *key, int triple, boolean decrypt, void **keysched, size_t *length);
void ecbS3DES(void *keysched, int triple, byte *in, byte *out);
void destroyS3DES(void **keysched, size_t length);

void initKDDES(byte *key, int triple, boolean decrypt, void **keysched, size_t *length);
void ecbKDDES(void *keysched, int triple, int encrypt, byte *in, byte *out);
void destroyKDDES(void **keysched, size_t length);

#endif
/* end of file des.h */
