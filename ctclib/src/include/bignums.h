/* bignums.h
**
** Multiple precision unsigned integer arithmetic
**
**  Coded & copyright Heimdall <heimdall@bifroest.demon.co.uk>  1996
**  All rights reserved.  For full licence details see file licences.c
**
** The BIGNUMS module is a multiple precision arithmetic module intended for
** the implementation of Public Key Encryption systems.  Note that it is NOT
** a general purpose multiple precision module.  Specifically:-
**   1) It handles only non-negative integers.
**   2) It is primarily concerned with (and optimised for) modular arithmetic.
**   3) It does not handle short integers efficiently.
**
** To minimise cross platform maintenance costs the use of assembler is kept to
** a minimum.  Only two simple macros are required.  One is a long*long=>two longs
** multiply, and the other a two longs (denominator) by long (numerator) to long
** (quotient) divide operation.  68020 macros and standard C macros are provided.
**
** Many routines return booleans indicating success.  In the case of any of these
** routines failing it is due to failure to allocated memory.
** mod_power_mpn alone can also fail due to a user interrupt.
**  DLL support Mr. Tines 14-Sep-1997
*/
#ifndef _bignums
#define _bignums
#include "abstract.h"

#ifndef CTCUTL_DLL
#define CTCUTL_DLL
#endif

/* The following are actually defined in abstract.h */
/* typedef struct mp_number_T mp_number;
** typedef mp_number * bignum;
** typedef bignum * bignump;   */

#define EXTRACT_SHORT(x) ((uint16_t)((x)[0] * 0x100 + (x)[1]))
#define EXTRACT_LONG(x)  ((x)[0] * 0x1000000L + (x)[1] * 0x10000L + (x)[2] * 0x100L + (x)[3])
uint16_t CTCUTL_DLL byte_length(byte * number);

/* N.B. init_mpn should not be called on an existing bignum
**      as this would lose the already allocated storage. */
void CTCUTL_DLL init_mpn(bignump num);

/* All initialise bignums must have their storage released
** with clear_mpn before they go out of scope */
void CTCUTL_DLL clear_mpn(bignump num);

/* N.B. get_mpn has an implied init_mpn */
boolean CTCUTL_DLL get_mpn(bignump internal, byte * external);

/* convert an internal MP to a file format MPN.
** Actual number of bytes (length field excluded)
** Returns -1 if the length of provided MPN buffer is inadequate */
short CTCUTL_DLL put_mpn(byte * external, bignump internal);
boolean CTCUTL_DLL set_mpn(uint32_t value, bignump number);
boolean CTCUTL_DLL read_mpn(char * input, bignump number);
void CTCUTL_DLL format_mpn(char * output, bignum number);

void CTCUTL_DLL wipe_mpn(bignump internal); /* trash data in number */
uint16_t CTCUTL_DLL length_mpn(bignum number); /* length in bits */
short CTCUTL_DLL size_mpn(bignum internal); /* length in bytes */
boolean CTCUTL_DLL copy_mpn(bignump result, bignum input);
boolean CTCUTL_DLL gt_mpn(bignum left, bignum right);
boolean CTCUTL_DLL eq_mpn(bignum left, bignum right);

common_error CTCUTL_DLL mod_power_mpn( bignump result, bignum multi,
bignum expon, bignum modulus);

/* If the final argument to 'remainder' is true, the operation is mod (modulus - 1) */
void CTCUTL_DLL remainder_mpn(bignum result, bignum modulus, boolean minus1);
uint16_t CTCUTL_DLL mod_short(bignum number, uint16_t modulus);
boolean CTCUTL_DLL divide_mpn( bignump quotient, bignump remainder,
bignum numerator, bignum denominator);
boolean CTCUTL_DLL add_mpn(bignump result, bignum add);
boolean CTCUTL_DLL multiply_mpn(bignump result, bignum left, bignum right);
void CTCUTL_DLL subtract_mpn(bignum result, bignum minus);
boolean CTCUTL_DLL HCF(bignump result, bignum left, bignum right);
boolean CTCUTL_DLL sieve(bignum number); /* Returns true if could be a prime */

/* routines developed to support elliptic curve encryption */
/* The operations are abstracted from what are used in George Barwood's
   public domain Pegwit PKE system, version 8.1
*/

/* true if bitnumth bit is set */
boolean CTCUTL_DLL nthBitSet_mpn( bignum value, uint16_t bitnum );
/* if bit, set 0th bit, else clear it */
void CTCUTL_DLL set0thBit_mpn (bignum value, boolean bit);

/* pack the lowest bitsUsed bits from datalen uint16_t's into a bignum
with a number of clear bits below this.  implicitly init_mpn()s value */
boolean CTCUTL_DLL pack16_mpn(bignump value, uint16_t *data, uint16_t datalen,
byte bitsUsed, byte freeLowBits);
/* the converse operation - fails if datalen is too small */
boolean CTCUTL_DLL unpack16_mpn(bignum value, uint16_t *data, uint16_t *datalen,
byte bitsUsed, byte freeLowBits);
/* special case operation */
boolean CTCUTL_DLL triple_mpn (bignump result, bignum input);

/* initialise a bignum to the GF 2^255 prime order... ; implicit init_mpn*/
boolean CTCUTL_DLL set_prime_order(bignump value);

/*extern const CTCUTL_DLL bignum real_prime_order;*/ /* NOT heap allocated! */

#endif

/* end of file bignums.h */

