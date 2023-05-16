/* haval.h
 * The HAVAL hashing function
 *
 * Public domain implementation by Paulo S.L.M. Barreto <pbarreto@uninet.com.br>
 * Modified for CTC compatibility Mr. Tines, Feb. 1998
*/

#ifndef __HAVAL_H
#define __HAVAL_H

#include "abstract.h"
#ifndef __HAVAL_I_H
typedef struct havalContext_T havalContext;
#endif

/* decipher EBP byte value 97-111 or 0 if out of range */
int havalGetPasses(byte ebpType);
int havalGetSize(byte epbType);

void havalInit (havalContext **hcp, size_t *length,
int passes, int hashsize, boolean ebp); /* EBP gets it mildly wrong... */
/* Initialize a HAVAL hashing context according to the desired */
/* number of passes and hash length.  Returns: void if error  */
/* number of passes must be 3, 4, or 5.                  */
/* hash length must be 128, 160, 192, 224, or 256.     */

void havalUpdate (havalContext *hcp, byte *dataBuffer, uint32_t dataLength);
/* Updates a HAVAL hashing context with a data block dataBuffer */
/* of length dataLength.                */

void havalFinal (havalContext **hcp, byte digest[MAXHASHSIZE], size_t length);
/* Finished evaluation of a HAVAL digest, clearing the context. */
/* The digest buffer must be large enough to hold the desired  */
/* hash length.                   */

#endif /* __HAVAL_H */

