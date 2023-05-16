/* ziputils.c -- utility functions for gzip support
 * Copyright (C) 1992-1993 Jean-loup Gailly
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License, see the file COPYING.
 *
 * N.B. This slightly modified version is included in the CTC which does not 
 * include file COPYING.  For full text of GNU G.P.Licence see licences.c instead.
 *  -- Heimdall 2 Feb 1997 
 *
 * Modified and simplified and renamed (from utils.c) by Heimdall March 1996
 * Ported to MS VC++ and Borland C++ Mr. Tines April 1996
 * Variables unused or conditonally used in deflate_file() tidied;
 * Argument types matched in allocation, MAXSEG_64K accepted for 16-bit
 * MSDOS, and deflate() return converted safely to boolean
 *                                   Mr. Tines 1-Jan-1997
 * Splay tree compression added : this is the file for expansion
 * algorithm selection; compression is in compress() in ctc.c.
 * exam_decompress() in the same file also tests algorithm range.
 *                                   Mr. Tines 24-Mar-1997
 *
 * Move all algorithm choice to ctc.c - Mr. Tines 20-Apr-1997
 * Make the crc table conditonally far - Mr. Tines 12-Mar-1998
 * Explicitly return standard value EOF from file_read() when the
 *   call to vf_read indicates error or end of file - Mr. Tines 3-Jul-1998
 * 18-Mar-2001: gzip.h has multi-line macros, so guard against
 *                             \r\n line-ends
 * 24-Mar-2001: apply a namespace qualifier for inflate and deflate
 *              to avoid symbol clash when linking with stdc++-3-libc6.1-2-2.10.0
 *              using gcc/g++
 *  2-Mar-2002: Use ANSI declarations for VC++ high warning level
 */

#include <ctype.h>
#include <errno.h>
#include "tailor.h"

#ifdef unix
  #include "gzip.h_ux"
#else
  #include "gzip.h" 
#endif
#include "keyconst.h"
#include "port_io.h"
#include "splay.h"
#include "utils.h"
#include "usrbreak.h"
#include "ziputils.h"

extern ulg crc_32_tab[]; /* crc table, defined below */
unsigned insize; /* valid bytes in inbuf */
unsigned inptr; /* index of next byte to be processed in inbuf */
unsigned outcnt; /* bytes in output buffer */
static DataFileP inputFile;
static DataFileP outputFile;
int level = 5; /* Default (never overridden?) compression level */
#define BITS 15
#ifndef LIT_BUFSIZE
#define LIT_BUFSIZE DIST_BUFSIZE
#endif
DECLARE(ush, tab_prefix, 1L<<BITS);
DECLARE(uch, inbuf, INBUFSIZ +INBUF_EXTRA);
DECLARE(uch, outbuf, OUTBUFSIZ +OUTBUF_EXTRA);
DECLARE(uch, window, 2L*WSIZE);
DECLARE(ush, d_buf, DIST_BUFSIZE);

/* forward reference */
ulg updcrc(
uch *s, /* pointer to bytes to pump through */
unsigned n); /* number of bytes in s[] */


boolean reflate(DataFileP in, DataFileP out)
{
    boolean result;

    inputFile = in;
    outputFile = out;
#ifdef DYN_ALLOC
    inbuf = qmalloc(INBUFSIZ + INBUF_EXTRA);
    window = zmalloc((size_t)(2L*WSIZE));
#endif

    if(inbuf && window)
    {
        updcrc(NULL, 0); /* initialize crc */
        result = (boolean)(0 == CTClib_inflate());
        flush_window();
    }
    else
        result = FALSE;
#ifdef DYN_ALLOC
    qfree(inbuf);
    qfree(window);
#endif
    return result;
}

int deflate_file(DataFileP in, DataFileP out)
/* Compress the file fileName and write it to the file *y. Return an error
   code in the ZE_ class.  Also, update tempzn by the number of bytes written. */
{
    int method = DEFLATED; /* method for this entry */
    /*long q = -1L;*/ /* size returned by filetime */
    ush att; /* internal file attributes (dummy only) */
    ush flg; /* gp compresion flags (dummy only) */
    int result = -1;

#ifdef DYN_ALLOC
    long outbuf_size, d_buf_size, l_buf_size, prev_size;
#endif

    inputFile = in;
    outputFile = out;

    bi_init(1);
    att = UNKNOWN;
#ifndef MSDOS
#ifdef MAXSEG_64K
#error allocate assume MAXSEG_64K not set
#endif
#endif
#define LIT_BUFSIZE DIST_BUFSIZE
#ifdef DYN_ALLOC
    outbuf_size = WSIZE * 2L * sizeof(uch) + 16;
    prev_size = WSIZE * 2L * sizeof(ush) + 16;
    d_buf_size = DIST_BUFSIZE * (long)sizeof(ush);
    l_buf_size = LIT_BUFSIZE * (long)sizeof(ush);
    window = zmalloc((size_t)(2L*WSIZE));
    outbuf = (uch *)qmalloc((size_t)outbuf_size);
    prev = (ush *)qmalloc((size_t)prev_size);
    d_buf = (ush *)qmalloc((size_t)d_buf_size);
    inbuf = (uch *)qmalloc((size_t)l_buf_size); /* used as l_buf */
#endif
    if(window && outbuf && prev && d_buf && inbuf)
    {
        ct_init(&att, &method);
        lm_init(level, &flg);
        result = (boolean)(CTClib_deflate() != 0);
        flush_outbuf();
    }
#ifdef DYN_ALLOC
    qfree(window);
    qfree(outbuf);
    qfree(prev);
    qfree(d_buf);
    qfree(inbuf);
#endif
    return result;
}

/* ===========================================================================
 * Run a set of bytes through the crc shift register.  If s is a NULL
 * pointer, then initialize the crc shift register contents instead.
 * Return the current crc in either case.
 */
ulg updcrc(
uch *s, /* pointer to bytes to pump through */
unsigned n) /* number of bytes in s[] */
{
    register ulg c; /* temporary variable */

    static ulg crc = (ulg)0xffffffffL; /* shift register contents */

    if (s == NULL) 
    {
        c = 0xffffffffL;
    }
    else 
        {
        c = crc;
        if (n) do 
            {
            c = crc_32_tab[((int)c ^ (*s++)) & 0xff] ^ (c >> 8);
        }
        while (--n);
    }
    crc = c;
    return c ^ 0xffffffffL; /* (instead of ~c for 64-bit machines) */
}

/* ===========================================================================
 * Fill the input buffer. This is called only when the buffer is empty.
 */

#ifdef __BORLANDC__
#pragma warn -par
#endif
#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (disable: 4100)
#endif
int fill_inbuf(int eof_ok)
{
    long block;

    insize = 0;
    while( insize < INBUFSIZ &&
        (block = vf_read(inbuf + insize, INBUFSIZ - insize, inputFile)) > 0)
        insize += (unsigned int) block;
    inptr = 1;
    return inbuf[0];
}
#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (default: 4100)
#endif
#ifdef __BORLANDC__
#pragma warn .par
#endif

int file_read(char * buffer, unsigned len)
{
    int bytes = (int)vf_read(buffer, (long)len, inputFile);
    return (bytes >= 0) ? bytes : EOF;
}

/* ===========================================================================
 * Write the output window window[0..outcnt-1] and update crc and bytes_out.
 * (Used for the decompressed data only.)
 */
void flush_window()
{
    if (outcnt == 0) return;
    updcrc(window, outcnt);

    vf_write(window, outcnt, outputFile);

    /*    bytes_out += (ulg)outcnt;  */
    outcnt = 0;
}

void flush_outbuf()
{
    if (outcnt == 0) return;

    vf_write(outbuf, outcnt, outputFile);

    /*    bytes_out += (ulg)outcnt;  */
    outcnt = 0;
}

void error(char * m)
{
    bug_check(m);
}

/* ========================================================================
 * Table of CRC-32's of all single-byte values (made by makecrc.c)
 */
#if defined( __BORLANDC__ )
#if (2 == sizeof(int))
#define P32 far
#else
#define P32
#endif
#else
#define P32
#endif
ulg P32 crc_32_tab[] = {
    0x00000000L, 0x77073096L, 0xee0e612cL, 0x990951baL, 0x076dc419L,
    0x706af48fL, 0xe963a535L, 0x9e6495a3L, 0x0edb8832L, 0x79dcb8a4L,
    0xe0d5e91eL, 0x97d2d988L, 0x09b64c2bL, 0x7eb17cbdL, 0xe7b82d07L,
    0x90bf1d91L, 0x1db71064L, 0x6ab020f2L, 0xf3b97148L, 0x84be41deL,
    0x1adad47dL, 0x6ddde4ebL, 0xf4d4b551L, 0x83d385c7L, 0x136c9856L,
    0x646ba8c0L, 0xfd62f97aL, 0x8a65c9ecL, 0x14015c4fL, 0x63066cd9L,
    0xfa0f3d63L, 0x8d080df5L, 0x3b6e20c8L, 0x4c69105eL, 0xd56041e4L,
    0xa2677172L, 0x3c03e4d1L, 0x4b04d447L, 0xd20d85fdL, 0xa50ab56bL,
    0x35b5a8faL, 0x42b2986cL, 0xdbbbc9d6L, 0xacbcf940L, 0x32d86ce3L,
    0x45df5c75L, 0xdcd60dcfL, 0xabd13d59L, 0x26d930acL, 0x51de003aL,
    0xc8d75180L, 0xbfd06116L, 0x21b4f4b5L, 0x56b3c423L, 0xcfba9599L,
    0xb8bda50fL, 0x2802b89eL, 0x5f058808L, 0xc60cd9b2L, 0xb10be924L,
    0x2f6f7c87L, 0x58684c11L, 0xc1611dabL, 0xb6662d3dL, 0x76dc4190L,
    0x01db7106L, 0x98d220bcL, 0xefd5102aL, 0x71b18589L, 0x06b6b51fL,
    0x9fbfe4a5L, 0xe8b8d433L, 0x7807c9a2L, 0x0f00f934L, 0x9609a88eL,
    0xe10e9818L, 0x7f6a0dbbL, 0x086d3d2dL, 0x91646c97L, 0xe6635c01L,
    0x6b6b51f4L, 0x1c6c6162L, 0x856530d8L, 0xf262004eL, 0x6c0695edL,
    0x1b01a57bL, 0x8208f4c1L, 0xf50fc457L, 0x65b0d9c6L, 0x12b7e950L,
    0x8bbeb8eaL, 0xfcb9887cL, 0x62dd1ddfL, 0x15da2d49L, 0x8cd37cf3L,
    0xfbd44c65L, 0x4db26158L, 0x3ab551ceL, 0xa3bc0074L, 0xd4bb30e2L,
    0x4adfa541L, 0x3dd895d7L, 0xa4d1c46dL, 0xd3d6f4fbL, 0x4369e96aL,
    0x346ed9fcL, 0xad678846L, 0xda60b8d0L, 0x44042d73L, 0x33031de5L,
    0xaa0a4c5fL, 0xdd0d7cc9L, 0x5005713cL, 0x270241aaL, 0xbe0b1010L,
    0xc90c2086L, 0x5768b525L, 0x206f85b3L, 0xb966d409L, 0xce61e49fL,
    0x5edef90eL, 0x29d9c998L, 0xb0d09822L, 0xc7d7a8b4L, 0x59b33d17L,
    0x2eb40d81L, 0xb7bd5c3bL, 0xc0ba6cadL, 0xedb88320L, 0x9abfb3b6L,
    0x03b6e20cL, 0x74b1d29aL, 0xead54739L, 0x9dd277afL, 0x04db2615L,
    0x73dc1683L, 0xe3630b12L, 0x94643b84L, 0x0d6d6a3eL, 0x7a6a5aa8L,
    0xe40ecf0bL, 0x9309ff9dL, 0x0a00ae27L, 0x7d079eb1L, 0xf00f9344L,
    0x8708a3d2L, 0x1e01f268L, 0x6906c2feL, 0xf762575dL, 0x806567cbL,
    0x196c3671L, 0x6e6b06e7L, 0xfed41b76L, 0x89d32be0L, 0x10da7a5aL,
    0x67dd4accL, 0xf9b9df6fL, 0x8ebeeff9L, 0x17b7be43L, 0x60b08ed5L,
    0xd6d6a3e8L, 0xa1d1937eL, 0x38d8c2c4L, 0x4fdff252L, 0xd1bb67f1L,
    0xa6bc5767L, 0x3fb506ddL, 0x48b2364bL, 0xd80d2bdaL, 0xaf0a1b4cL,
    0x36034af6L, 0x41047a60L, 0xdf60efc3L, 0xa867df55L, 0x316e8eefL,
    0x4669be79L, 0xcb61b38cL, 0xbc66831aL, 0x256fd2a0L, 0x5268e236L,
    0xcc0c7795L, 0xbb0b4703L, 0x220216b9L, 0x5505262fL, 0xc5ba3bbeL,
    0xb2bd0b28L, 0x2bb45a92L, 0x5cb36a04L, 0xc2d7ffa7L, 0xb5d0cf31L,
    0x2cd99e8bL, 0x5bdeae1dL, 0x9b64c2b0L, 0xec63f226L, 0x756aa39cL,
    0x026d930aL, 0x9c0906a9L, 0xeb0e363fL, 0x72076785L, 0x05005713L,
    0x95bf4a82L, 0xe2b87a14L, 0x7bb12baeL, 0x0cb61b38L, 0x92d28e9bL,
    0xe5d5be0dL, 0x7cdcefb7L, 0x0bdbdf21L, 0x86d3d2d4L, 0xf1d4e242L,
    0x68ddb3f8L, 0x1fda836eL, 0x81be16cdL, 0xf6b9265bL, 0x6fb077e1L,
    0x18b74777L, 0x88085ae6L, 0xff0f6a70L, 0x66063bcaL, 0x11010b5cL,
    0x8f659effL, 0xf862ae69L, 0x616bffd3L, 0x166ccf45L, 0xa00ae278L,
    0xd70dd2eeL, 0x4e048354L, 0x3903b3c2L, 0xa7672661L, 0xd06016f7L,
    0x4969474dL, 0x3e6e77dbL, 0xaed16a4aL, 0xd9d65adcL, 0x40df0b66L,
    0x37d83bf0L, 0xa9bcae53L, 0xdebb9ec5L, 0x47b2cf7fL, 0x30b5ffe9L,
    0xbdbdf21cL, 0xcabac28aL, 0x53b39330L, 0x24b4a3a6L, 0xbad03605L,
    0xcdd70693L, 0x54de5729L, 0x23d967bfL, 0xb3667a2eL, 0xc4614ab8L,
    0x5d681b02L, 0x2a6f2b94L, 0xb40bbe37L, 0xc30c8ea1L, 0x5a05df1bL,
    0x2d02ef8dL
};

/* end of file ziputils.c */
