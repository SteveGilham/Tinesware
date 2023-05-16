/* sha.h */
#ifndef SHA_H
#define SHA_H
#include "basic.h"

/* NIST Secure Hash Algorithm */
/* heavily modified from Peter C. Gutmann's implementation */
/* This code is in the public domain */

/* CTC compatibility Mr. Tines Jun '96 */
/* tweaks to the standalone test-harness bit; also adding
 SHA-1 Initialiser variant (Update and Final are common
 code) - Mr. Tines 1-1-97 */

/* Useful defines & typedefs */

#define SHA_BLOCKSIZE  64
#define SHAHASHSIZE  20

typedef struct {
    uint32_t digest[5]; /* message digest */
    uint32_t count_lo, count_hi; /* 64-bit bit count */
    uint32_t data[16]; /* SHA data buffer */
    int local; /* unprocessed amount in data */
    boolean SHA1;
}
SHA_INFO;

void SHAInit(SHA_INFO **md, size_t *length);
void SHA1Init(SHA_INFO **md, size_t *length);
void SHAUpdate(SHA_INFO *md, byte *buff, uint32_t count);
void SHAFinal(SHA_INFO **md, byte digest[SHAHASHSIZE], size_t length);

#ifdef STANDALONE_SHA
#include <stdio.h>
void sha_stream(byte digest[SHAHASHSIZE], FILE *);
void sha_print(byte digest[SHAHASHSIZE]);
#endif

#endif /* SHA_H */

/* end of file sha.h */
