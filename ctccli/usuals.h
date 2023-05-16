/* usuals.h - The usual typedefs, etc.
** CTClib mods Mr. Tines 23-Feb-98
*/
#ifndef USUALS /* Assures no redefinitions of usual types...*/
#define USUALS

#include "basic.h"

typedef byte *byteptr;	/* pointer to byte */
typedef char *string;	/* pointer to ASCII character string */
//typedef unsigned short word16;	/* values are 0-65535 */
//typedef unsigned long word32;	/* values are 0-4294967295 */

	/* Zero-fill the byte buffer. */
#define fill0(buffer,count)	memset( buffer, 0, (size_t)count )

	/* This macro is for burning sensitive data.  Many of the
	   file I/O routines use it for zapping buffers */
#define burn(x) fill0((VOID *)&(x),sizeof(x))

#define CONST_CAST(type, value) (type)(value)
#define REINTERPRET_CAST(type, value) (type)(value)

#endif	/* if USUALS not already defined */
