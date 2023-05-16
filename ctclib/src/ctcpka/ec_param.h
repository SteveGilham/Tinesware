/* ec_param.h
 **
 ** Elliptic curve parameters public key operations; based on the public
 ** domain code in george.barwood@dial.pipex.com's Pegwit v8.1 PKE system
 **
 **  Elliptic curve encryption support routines coded and copyright
 **  Mr. Tines <tines@windsong.demon.co.uk> 1997
 **  All rights reserved.  For full licence details see file licences.c
 */
#ifndef _ec_param
#define _ec_param

/* elliptic curve parameters: */

#define GF_L   15
#define GF_K   17
#define GF_M  (GF_K*GF_L)
#define GF_T    3
#define GF_ROOT    3
#define EC_B  161
#define SIMPLE_TRACE_MASK

#endif

/* end of file ec_param.h */
