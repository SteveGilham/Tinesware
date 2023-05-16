/* widechar.h
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
**  Heimdall <heimdall@bifroest.demon.co.uk>  1998
**  All rights reserved.  For full licence details see file licences.c
**
**
*/
#ifndef _widechar_h
#define _widechar_h
#include "basic.h"

#ifndef CTCLIB_DLL
#define CTCLIB_DLL
#endif

#ifdef __cplusplus
NAMESPACE_CTCLIB
extern "C" {
#endif

    /* Internationalised line parsing routines */
    const char * CTCLIB_DLL breakPoint(const char * buffer, int minSize, int maxSize);
    const char * CTCLIB_DLL seekTrailingWhitespace(const char * buffer);

    /* character set conversion - external to UTF8 default */
    /* mbs buffers are assumed to be no more than 255 bytes;
         and output is assumed large enough; returns bytes written */
    long mbstoUTF8(const char * mbs, byte * utf8);

    /* strlen equivalent for multi-byte charsets that should work
        for most compiler/library sets. We assume we're not running with Unicode
        as our standard encoding, and assume that no valid multi-byte character
        starts with a zero byte.*/
    int mbStrlen(const char * buffer);

#if defined(__BORLANDC__) && __BORLANDC__ >= 0x500
#if sizeof(int) == 4
#define CTC_WCHAR_SUPPORT
/* For some reason, on win98, with non-'C' locale,
   iswspace() just flat our fails to work.  */
#define CTC_WCTYPE_9X_LOCALE_CONTINGENT

#include <mbstring.h>
#define isMBspace _ismbcspace
#define isMBprint _ismbcprint
#define isMBpunct _ismbcpunct

#ifdef isspace
#undef isspace
#endif
#ifdef iswspace
#undef iswspace
#endif


#endif
#endif

#if defined(_MSC_VER) && _MSC_VER >= 1100
#define CTC_WCHAR_SUPPORT
#elif defined(__GNUC__) /* some version of g++ */
#define CTC_WCHAR_SUPPORT
/* This is the standard location - unlike Borland */
#include <wctype.h>
#endif

#ifndef CTC_WCHAR_SUPPORT
    /* supply extra definitions if no real wide character support */
#ifndef MB_CUR_MAX
#define MB_CUR_MAX 1
#endif

#if defined(__BORLANDC__)
#if sizeof(int) == 4
    typedef unsigned long wint_t;
#endif
#endif

    int mbtowc( wchar_t *wchar, const char *mbchar, size_t count );
    int mctomb( char *mbchar, wchar_t wchar );

#else
#include <locale.h>
#endif

#ifdef __cplusplus
}
END_NAMESPACE
#endif

#endif /*_widechar_h*/

/* end of file widechar.h */
