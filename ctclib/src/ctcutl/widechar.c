/* widechar.c
 **
 **  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 **  Heimdall <heimdall@bifroest.demon.co.uk>  1996
 **  All rights reserved.  For full licence details see file licences.c
 **
 **  Unicode support routines : I know that Borland 4.52 doesn't have
 **  Unicode, I'm not sure about earlier versions than VC++5
 */

#include <stdlib.h> /* mbtowc should be defined here */
#include "basic.h"
#include "widechar.h" /* self header */


#ifdef CTC_WCHAR_SUPPORT

/*
 *  declarations for isw...() routines.
 *  Should be wctype.h strictly, if we were following the 'C' language
 *  standard, but Borland C++ doesn't adhere to the standard
 *  and VC++ includes the definitions here too
 */
#include <ctype.h>

/* define aliases for use later */
#ifndef CTC_WCTYPE_9X_LOCALE_CONTINGENT
#define isWspace iswspace
#define isWpunct iswpunct
#define isWprint iswprint
#else /* emulate these routines for multi-byte */

static boolean wok = FALSE;
static boolean initialised = FALSE;

#define DOINIT {wok = (boolean) iswspace((wint_t)32); initialised = TRUE;}

static int isWspace(wint_t c)
{
    if(!initialised) DOINIT
    if(wok)
    {
        return(iswspace(c));
    }
    else
    {
        unsigned int buf = 0;
        wctomb((char*)&buf, c);
        return (isMBspace(buf));
    }
}

static int isWprint(wint_t c)
{
    if(!initialised) DOINIT
    if(wok)
    {
        return(iswprint(c));
    }
    else
    {
        unsigned int buf = 0;
        wctomb((char*)&buf, c);
        return (isMBprint(buf));
    }
}

static int isWpunct(wint_t c)
{
    if(!initialised) DOINIT
    if(wok)
    {
        return(iswpunct(c));
    }
    else
    {
        unsigned int buf = 0;
        wctomb((char*)&buf, c);
        return (isMBpunct(buf));
    }
}

#endif

#else  /* emulate these routines for single-byte */

/*
 *  these values gleaned from running the following java code
 *  public class isw
 *  {
 *    public static void main(String[] args)
 *    {
 *      char c = 0;
 *      int n = 0;
 *      do {
 *       if(Character.isWhitespace(c))
 *       {
 *         System.out.println("0x"+Integer.toHexString(c)+",");
 *       }
 *      } while(++c != 0);
 *    }
 *  }
 */
#include <ctype.h> /* for simple is...() routines */

static int isWspace(wint_t c)
{
    if(((uint32_t)c&0xFFFFL) < 256) return isspace((int)c);
    else if(c>=0x2000 && c<=0x200b) return 1;
    else if(0x2028==c) return 1;
    else if(0x2029==c) return 1;
    else if(0x3000==c) return 1;
    return 0;
}

/*
 * Java selection by
 * 
 *   int x = Character.getType(c);
 *   if(
 *      x == Character.CONNECTOR_PUNCTUATION ||
 *      x == Character.DASH_PUNCTUATION ||
 *      x == Character.END_PUNCTUATION ||
 *      x == Character.LINE_SEPARATOR ||
 *      x == Character.OTHER_PUNCTUATION ||
 *      x == Character.PARAGRAPH_SEPARATOR ||
 *      x == Character.SPACE_SEPARATOR ||
 *      x == Character.START_PUNCTUATION
 *   )
 */

/* this could be simplified */
static const wchar_t punct[] = {
    0x374, 0x375, 0x37e, 0x387, 0x55a, 0x55b, 0x55c,
    0x55d, 0x55e, 0x55f, 0x589, 0x5be, 0x5c0, 0x5c3, 0x5f3, 0x5f4,
    0x60c, 0x61b, 0x61f, 0x66a, 0x66b, 0x66c, 0x66d, 0x6d4, 0x964,
    0x965, 0x970, 0xe2f, 0xe5a, 0xe5b, 0xeaf, 0xf04, 0xf05, 0xf06,
    0xf07, 0xf08, 0xf09, 0xf0a, 0xf0b, 0xf0c, 0xf0d, 0xf0e, 0xf0f,
    0xf10, 0xf11, 0xf12, 0xf3a, 0xf3b, 0xf3c, 0xf3d, 0xf85, 0x10fb,
    0x2000, 0x2001, 0x2002, 0x2003, 0x2004, 0x2005, 0x2006, 0x2007,
    0x2008, 0x2009, 0x200a, 0x200b, 0x2010, 0x2011, 0x2012, 0x2013,
    0x2014, 0x2015, 0x2016, 0x2017, 0x2018, 0x2019, 0x201a, 0x201b,
    0x201c, 0x201d, 0x201e, 0x201f, 0x2020, 0x2021, 0x2022, 0x2023,
    0x2024, 0x2025, 0x2026, 0x2027, 0x2028, 0x2029, 0x2030, 0x2031,
    0x2032, 0x2033, 0x2034, 0x2035, 0x2036, 0x2037, 0x2038, 0x2039,
    0x203a, 0x203b, 0x203c, 0x203d, 0x203e, 0x203f, 0x2040, 0x2041,
    0x2042, 0x2043, 0x2045, 0x2046, 0x207d, 0x207e, 0x208d, 0x208e,
    0x2329, 0x232a, 0x3000, 0x3001, 0x3002, 0x3003, 0x3006, 0x3008,
    0x3009, 0x300a, 0x300b, 0x300c, 0x300d, 0x300e, 0x300f, 0x3010,
    0x3011, 0x3014, 0x3015, 0x3016, 0x3017, 0x3018, 0x3019, 0x301a,
    0x301b, 0x301c, 0x301d, 0x301e, 0x301f, 0x3030, 0x30fb, 0xfd3e,
    0xfd3f, 0xfe30, 0xfe31, 0xfe32, 0xfe33, 0xfe34, 0xfe35, 0xfe36,
    0xfe37, 0xfe38, 0xfe39, 0xfe3a, 0xfe3b, 0xfe3c, 0xfe3d, 0xfe3e,
    0xfe3f, 0xfe40, 0xfe41, 0xfe42, 0xfe43, 0xfe44, 0xfe49, 0xfe4a,
    0xfe4b, 0xfe4c, 0xfe4d, 0xfe4e, 0xfe4f, 0xfe50, 0xfe51, 0xfe52,
    0xfe54, 0xfe55, 0xfe56, 0xfe57, 0xfe58, 0xfe59, 0xfe5a, 0xfe5b,
    0xfe5c, 0xfe5d, 0xfe5e, 0xfe5f, 0xfe60, 0xfe61, 0xfe63, 0xfe68,
    0xfe6a, 0xfe6b, 0xff01, 0xff02, 0xff03, 0xff05, 0xff06, 0xff07,
    0xff08, 0xff09, 0xff0a, 0xff0c, 0xff0d, 0xff0e, 0xff0f, 0xff1a,
    0xff1b, 0xff1f, 0xff20, 0xff3b, 0xff3c, 0xff3d, 0xff3f, 0xff5b,
    0xff5d, 0xff61, 0xff62, 0xff63, 0xff64, 0xff65};

static int isWpunct(wint_t c)
{
    if(((uint32_t)c&0xFFFFL) < 256) return ispunct((int)c);
    else
    {
        int i;
        for(i=0; i<sizeof(punct); ++i)
        {
            if(punct[i] == c) return 1;
        }
    }
    return 0;
}

/*
 *  This is not a short list, so as all control characters are
 *  single-byte, accept even unassigned character values as >0xff
 *  as printable.
 */
static int isWprint(wint_t c)
{
    if(((uint32_t)c&0xFFFFL) < 256) return isprint((int)c);
    else return 1;    /* fail safe */
}

int mbtowc( wchar_t *wchar, const char *mbchar, size_t count )
{
    /*mbtowc returns the length in bytes of the multibyte character.
     *  If mbchar is NULL or the object that it points to is a wide-character
     *  null character (L'\0'), the function returns 0. If the object that mbchar
     *  points to does not form a valid multibyte character within the first count
     *  characters, it returns -1. */

    if(!mbchar || !count || !*mbchar) return 0;
    if(wchar) *wchar = (wchar_t)(*mbchar);
    return 1;
}

int wctomb( char *mbchar, wchar_t wchar )
{
    /*If wctomb converts the wide character to a multibyte character,
     *  it returns the number of bytes (which is never greater than MB_CUR_MAX)
     *  in the wide character. If wchar is the wide-character null character (L'\0'),
     *  wctomb returns 1. If the conversion is not possible in the current locale,
     *  wctomb returns -1.
     */

    if(((uint32_t)wchar&0xFFFFL) > 255) return -1;
    *mbchar = (char)(wchar &0xff);
    return 1;

}

#endif /* end emulation */

/*
 *  These routines are const to indicate that they don't muck
 *  about with the string themselves; we explicitly case away
 *  const when we are entitled to.
 */
const char * seekTrailingWhitespace(const char * buffer)
{
    /* return a pointer to the start of trailing whitespace */
    /* or null if none */
    const char * space = 0;
    const char * c = buffer;
#ifdef CTC_WCHAR_SUPPORT
setlocale(LC_ALL, "");
#endif

    while(*c)
    {
        wchar_t charVal=0;
        int clen = mbtowc(&charVal, c, MB_CUR_MAX);

        if(clen < 1) break;        /* abort on invalid character or NULL */
        if(isWspace(charVal) ||
          !isWprint(charVal))
        {
            if(0 == space) space = c;            /* set on hitting first space */
        }
        else space = 0;        /* reset on hitting non-space */
        c += clen;
    }
    return space;
}

const char * breakPoint(const char * buffer, int minSize, int maxSize)
{
    /* Look for a delimiter character to break line at *
     ** The returned value is the first character _not_ included */
    const char * mark = 0;
    const char * safe = 0;
    const char * c = buffer;
#ifdef CTC_WCHAR_SUPPORT
setlocale(LC_ALL, "");
#endif
    while(*c && c-buffer < maxSize)
    {
        wchar_t charVal=0;
        int clen = mbtowc(&charVal, c, MB_CUR_MAX);

        safe = c;        /* we know that there is a character here */

        if(clen < 1)        /* abort on invalid character or NULL */
        {
            safe = c+1;            /* avoid getting stuck */
            break;
        }
        c += clen;

        if(isWspace(charVal) ||
           isWpunct(charVal) ||
          !isWprint(charVal))
        {
            mark = c;            /* points to the *next* character */
        }
    }

    if(mark && (mark-buffer)>minSize)
    {
        return mark;
    }
    /* No suitable breakpoints so break at end of last whole character */
    /* This should be OK for ASCII (7-bit), ISO-Latin and similar 8-bit */
    /* character sets, and EUC or Shift-JIS for Kanji; is it probably */
    /* in hideous error in the Middle East where words might be split to */
    /* detach marks from the glyph they are to modify and other such */
    /* infelicitous behaviour. */
    return safe;
}

long mbstoUTF8(const char * mbs, byte * utf8)
{
    wchar_t buffer[256];    /* size as per canonicalise's buffer */
    const char *x = mbs;
    wchar_t *w = buffer, *p=buffer;
    byte * c = utf8;

    while(*x)
    {
        int clen = mbtowc(w, (const char *)x, MB_CUR_MAX);
        if(clen < 1) break;
        x += clen;
        ++w;
    }
    /* w now points to just after the last wide character */
    while(p < w)
    {
        if(*p < 0x80)        /* 7 data bits */
        {
            *c = (byte)(*p & 0x7F);
            ++c;
        }
        else if (*p < 0x800)        /* 11 data bits */
        {
            c[0] = (byte)(0xC0 | (((*p)>>6) & 0x1F) );
            c[1] = (byte)(0x80 | ((*p)&0x3F) );
            c+=2;
        }
        else
        {
            c[0] = (byte)(0xE0 | (((*p)>>12) & 0x0F) );
            c[1] = (byte)(0x80 | (((*p)>>6) & 0x3F) );
            c[2] = (byte)(0x80 | ((*p)&0x3F) );
            c+=3;
        }
        ++p;
    }
    return (long)(c - utf8);
}

/*
 *  A line of text may well contain multi-byte characters; this routine
 *  returns the byte length until the ternumating NUL *character*.
 *  Because of how we're handling standard control characters for EOL,
 *  we assume we're not running with Unicode as our standard encoding,
 *  and assume that no valid multi-byte character starts with a zero byte.
 */
int mbStrlen(const char * buffer)
{
    const char * c = buffer;
    int bytes = 0;
    while(*c)
    {
        int thisChar = mblen(c, MB_CUR_MAX);
        if(thisChar<1)        /* invalid byte sequence rejected */
        {
            break;
        }
        c += thisChar;
        bytes+=thisChar;
    }
    return bytes;
}

