/* 3way.h - header file for 3way.c   

   Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1996
   All rights reserved.  For full licence details see file licences.c 
*/
#ifndef _3way
#define _3way
#include "basic.h"
#define TWAYKEYSIZE 12
#define TWAYBLOCKSIZE 12
#define TWAYHASHSIZE 12

/* standard things needed for cipher.c */
void init3WAY(byte *key, int triple, void **keysched, size_t *length);
void ecb3WAY(void *keysched, int triple, int encrypt, byte *in, byte *out);
void destroy3WAY(void **keysched, size_t length);

/* hash function support */
typedef struct Hash3Way_t {
    byte buf[TWAYKEYSIZE]; /* scratch buffer */
    uint32_t bits[2]; /* bits mod 2^64 */
    byte in[TWAYBLOCKSIZE]; /* input block size */
    int over;
}
Hash3Way;

void hash3WayInit(Hash3Way **mdContext, size_t *length);
void hash3WayUpdate(Hash3Way *mdContext, byte *buf, uint32_t len);
void hash3WayFinal(Hash3Way **mdContext, byte digest[TWAYHASHSIZE], size_t length);

#endif
