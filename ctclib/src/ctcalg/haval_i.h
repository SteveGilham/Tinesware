/* haval_i.h
 * The HAVAL hashing function
 *
 * Public domain implementation by Paulo S.L.M. Barreto <pbarreto@uninet.com.br>
 * Modified for CTC compatibility Mr. Tines, Feb. 1998
*/

#ifndef __HAVAL_I_H
#define __HAVAL_I_H

/* we need to disable optimisations and enable hardware rotations on VC++ */
/* At least some revisions of VC++5 get the software rotations wrong; and
   compilations take inordinate times on optimised settings */
#ifdef __BORLANDC__
#define HARDWARE_ROTATIONS
#pragma option -Od
#endif
#ifdef _MSC_VER
#define HARDWARE_ROTATIONS
#pragma optimize( "", off )
#endif

#include "abstract.h"

#ifdef __HAVAL_H 
#error "haval_i.h must precede haval.h when both are present"
#endif
typedef struct havalContext_T {
    uint16_t passes, hashLength; /* HAVAL parameters */
    uint32_t digest[8]; /* message digest (fingerprint) */
    byte block[128]; /* context data block */
    size_t occupied; /* number of occupied bytes in the data block */
    uint32_t bitCount[2]; /* 64-bit message bit count */
    uint32_t temp[8]; /* temporary buffer */
    boolean ebp;
}
havalContext;

#define HAVAL_VERSION 1

/* Borland needs more parentheses than the 'C' language ought to require */

/*#define F1(X6, X5, X4, X3, X2, X1, X0) \
 ((X1) & (X4) ^ (X2) & (X5) ^ (X3) & (X6) ^ (X0) & (X1) ^ (X0))*/
#define F1(X6, X5, X4, X3, X2, X1, X0) \
( ((X1) & ((X4) ^ (X0))) ^ ((X2) & (X5)) ^ ((X3) & (X6)) ^ (X0)) 

/*#define F2(X6, X5, X4, X3, X2, X1, X0) \
 ((X1) & (X2) & (X3) ^ (X2) & (X4) & (X5) ^ \
 (X1) & (X2) ^ (X1) & (X4) ^ (X2) & (X6) ^ (X3) & (X5) ^ \
 (X4) & (X5) ^ (X0) & (X2) ^ (X0))*/
#define F2(X6, X5, X4, X3, X2, X1, X0) \
( ((X2) & ( ((X1) & (~(X3))) ^ ((X4) & (X5)) ^ (X6) ^ (X0))) ^ \
 (((X4) & ((X1) ^ (X5))) ^ ((X3) & (X5)) ^ (X0)))

/*#define F3(X6, X5, X4, X3, X2, X1, X0) \
 ((X1) & (X2) & (X3) ^ (X1) & (X4) ^ (X2) & (X5) ^ (X3) & (X6) ^ (X0) & (X3) ^ (X0))*/
#define F3(X6, X5, X4, X3, X2, X1, X0) \
( ((X3) & (((X1) & (X2)) ^ (X6) ^ (X0))) ^ ((X1) & (X4)) ^ ((X2) & (X5)) ^ (X0))

/*#define F4(X6, X5, X4, X3, X2, X1, X0) \
 ((X1) & (X2) & (X3) ^ (X2) & (X4) & (X5) ^ (X3) & (X4) & (X6) ^ \
 (X1) & (X4) ^ (X2) & (X6) ^ (X3) & (X4) ^ (X3) & (X5) ^ \
 (X3) & (X6) ^ (X4) & (X5) ^ (X4) & (X6) ^ (X0) & (X4) ^(X0))*/
#define F4(X6, X5, X4, X3, X2, X1, X0) \
( ((X4) & ( ((~(X2)) & (X5)) ^ ((X3) | (X6)) ^ (X1) ^ (X0) )) ^ \
 ((X3) & (((X1) & (X2)) ^ (X5) ^ (X6))) ^ ((X2) & (X6)) ^ (X0) )

/*#define F5(X6, X5, X4, X3, X2, X1, X0) \
 ((X1) & (X4) ^ (X2) & (X5) ^ (X3) & (X6) ^ \
 (X0) & (X1) & (X2) & (X3) ^ (X0) & (X5) ^ (X0))*/
#define F5(X6, X5, X4, X3, X2, X1, X0) \
( ( (X1) & ( (X4) ^ ((X0) & (X2) & (X3)) ) ) ^ (((X2) ^ (X0)) & (X5)) ^ ((X3) & (X6)) ^ (X0) )

#ifdef HARDWARE_ROTATIONS
#define ROTR(v, n) (_lrotr ((v), (n)))
#else  /* !HARDWARE_ROTATIONS */
#define ROTR(v, n) (rot_tmp = (v), (rot_tmp >> (n)) | (rot_tmp << ( 32-(n))))
#endif /* ?HARDWARE_ROTATIONS */

/*void haval_byte_reverse(uint32_t *buffer, int count);*/
void havalTransform3 (uint32_t E[8], const byte D[128], uint32_t T[8]);
void havalTransform4 (uint32_t E[8], const byte D[128], uint32_t T[8]);
void havalTransform5 (uint32_t E[8], const byte D[128], uint32_t T[8]);

#endif /* __HAVAL_I_H */

