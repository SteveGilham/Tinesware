/* utils.h
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
**  Heimdall <heimdall@bifroest.demon.co.uk>  1996
**  All rights reserved.  For full licence details see file licences.c
**  DLL support Mr. Tines 14-Sep-1997
**  Namespace CTClib Mr. Tines 5-5-98
*/
#ifndef _utils_h
#define _utils_h

#include "basic.h"
#include <time.h>

#ifdef __cplusplus
NAMESPACE_CTCLIB
extern "C" {
#endif

#ifndef CTCUTL_DLL
#define CTCUTL_DLL
#endif

    void CTCUTL_DLL convert_byteorder(byte * buffer, uint16_t length);
    unsigned short CTCUTL_DLL calcchecksum(byte * buffer, unsigned short length);
    boolean CTCUTL_DLL little_endian(void); /* Returns TRUE on little endian machines */
    void CTCUTL_DLL flip32(byte * out, byte * in, size_t nbytes);
    void CTCUTL_DLL byte2hex(char hex[2], byte value);
    time_t CTCUTL_DLL datum_time(void);
    void * CTCUTL_DLL zmalloc(size_t size);
    void CTCUTL_DLL zfree (void ** buffer, size_t n);
    void * CTCUTL_DLL qmalloc(size_t n);
    void CTCUTL_DLL qmark(void * ptr);
    void * CTCUTL_DLL qcalloc(size_t m, size_t n);
    void * CTCUTL_DLL qrealloc(void * ptr, size_t size);
    void CTCUTL_DLL qcheck(void * ptr);
    void CTCUTL_DLL qfree(void * ptr);

#ifdef __cplusplus
}
END_NAMESPACE
#endif

#endif

/* end of file utils.h */
