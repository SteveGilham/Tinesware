/* armour.c      
 **
 **  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 **  Heimdall <heimdall@bifroest.demon.co.uk>  1996
 **  All rights reserved.  For full licence details see file licences.c
 **
 **  This source is derived from the PGP2.3 source file armor.c:-
 **          ASCII/binary encoding/decoding based partly on PEM RFC1113.
 **          PGP: Pretty Good(tm) Privacy - public key cryptography for the masses.
 **          (c) Copyright 1990-1992 by Philip Zimmermann.  All rights reserved.
 **
 **  Whereas largely recoded this file retains some original PGP code
 **  notably the CRC calculation.
 */
#include <string.h>
#include "usrbreak.h"
#include "armour.h"
#include "port_io.h"
#include "uuencode.h"
#include "widechar.h"

#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (disable: 4127) // conditional expression is constant
#endif


#define MAX_LINE_SIZE 256 /* largest line accepted for plainsigning */
#define LINE_LEN 48L /* binary bytes per armour line */

/* text for Armour headers and footers.  Only STARTLINE is really */
/* important as it is the only necessary one.                     */
#define STARTLINE "-----BEGIN PGP "
#define SIGNEDMESS "SIGNED MESSAGE-----"
#define ENDLINE  "-----END PGP "
#define LINEEND  "-----"
#define SIGBLOCK STARTLINE "SIGNATURE" LINEEND
#define VERSIONLINE "Version: "
#define COMMENTLINE "Comment: "
#define PARTS  ", PART "

/* Simpleminded UUencode support */
#define UUSTARTLINE "begin "

/* Index this array by a 6 bit value to get the character corresponding
 * to that value.  */
static unsigned char bintoasc[]
= "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

/* Index this array by a 7 bit value to get the 6-bit binary field
 * corresponding to that value.  Any illegal characters return high bit set.
 */
static
unsigned char asctobin[] = {
    0200,0200,0200,0200,0200,0200,0200,0200,
    0200,0200,0200,0200,0200,0200,0200,0200,
    0200,0200,0200,0200,0200,0200,0200,0200,
    0200,0200,0200,0200,0200,0200,0200,0200,
    0200,0200,0200,0200,0200,0200,0200,0200,
    0200,0200,0200,0076,0200,0200,0200,0077,
    0064,0065,0066,0067,0070,0071,0072,0073,
    0074,0075,0200,0200,0200,0200,0200,0200,
    0200,0000,0001,0002,0003,0004,0005,0006,
    0007,0010,0011,0012,0013,0014,0015,0016,
    0017,0020,0021,0022,0023,0024,0025,0026,
    0027,0030,0031,0200,0200,0200,0200,0200,
    0200,0032,0033,0034,0035,0036,0037,0040,
    0041,0042,0043,0044,0045,0046,0047,0050,
    0051,0052,0053,0054,0055,0056,0057,0060,
    0061,0062,0063,0200,0200,0200,0200,0200
};

/************************************************************************/

/* CRC Routines. */
/* These CRC functions are derived from code in chapter 19 of the book
 *  "C Programmer's Guide to Serial Communications", by Joe Campbell.
 *  Generalized to any CRC width by Philip Zimmermann.
 */

#ifdef __alpha
#define crcword unsigned int  /* if CRCBITS is 24 or 32 */
#else
#define crcword unsigned long  /* if CRCBITS is 24 or 32 */
#endif

#define CRCBITS 24 /* may be 16, 24, or 32 */
/* #define maskcrc(crc) ((crcword)(crc)) */ /* if CRCBITS is 16 or 32 */
#define maskcrc(crc) ((crc) & 0xffffffL) /* if CRCBITS is 24 */
#define CRCHIBIT ((crcword) (1L<<(CRCBITS-1))) /* 0x8000 if CRCBITS is 16 */
#define CRCSHIFTS (CRCBITS-8)

/* Notes on making a good 24-bit CRC--
 *  The primitive irreducible polynomial of degree 23 over GF(2),
 *  040435651 (octal), comes from Appendix C of "Error Correcting Codes,
 *  2nd edition" by Peterson and Weldon, page 490.  This polynomial was
 *  chosen for its uniform density of ones and zeros, which has better
 *  error detection properties than polynomials with a minimal number of
 *  nonzero terms.  Multiplying this primitive degree-23 polynomial by
 *  the polynomial x+1 yields the additional property of detecting any
 *  odd number of bits in error, which means it adds parity.  This
 *  approach was recommended by Neal Glover.
 * 
 *  To multiply the polynomial 040435651 by x+1, shift it left 1 bit and
 *  bitwise add (xor) the unshifted version back in.  Dropping the unused
 *  upper bit (bit 24) produces a CRC-24 generator bitmask of 041446373
 *  octal, or 0x864cfb hex.
 * 
 *  You can detect spurious leading zeros or framing errors in the
 *  message by initializing the CRC accumulator to some agreed-upon
 *  nonzero "random-like" value, but this is a bit nonstandard.
 */

#define CCITTCRC 0x1021 /* CCITT's 16-bit CRC generator polynomial */
#define PRZCRC 0x864cfbL /* PRZ's 24-bit CRC generator polynomial */
#define CRCINIT 0xB704CEL /* Init value for CRC accumulator */

/* This table is read-only so it is not a problem it being static */
static crcword crctable[256];/* Table for speeding up CRC's */
static boolean crctable_initialised = FALSE;

/* mk_crctbl derives a CRC lookup table from the CRC polynomial.
 *  The table is used later by the crcbytes function given below.
 *  mk_crctbl only needs to be called once at the dawn of time.
 * 
 *  The theory behind mk_crctbl is that table[i] is initialized
 *  with the CRC of i, and this is related to the CRC of i>>1,
 *  so the CRC of i>>1 (pointed to by p) can be used to derive
 *  the CRC of i (pointed to by q).
 */
static void mk_crctbl(crcword poly)
{
    int i;
    crcword t, *p, *q;
    p = q = crctable;
    *q++ = 0;
    *q++ = poly;
    for (i = 1; i < 128; i++)
    {
        t = *++p;
        if (t & CRCHIBIT)
        {
            t <<= 1;
            *q++ = t ^ poly;
            *q++ = t;
        }
        else
        {
            t <<= 1;
            *q++ = t;
            *q++ = t ^ poly;
        }
    }
}


/* Accumulate a buffer's worth of bytes into a CRC accumulator,
 * returning the new CRC value.  */
static crcword crcbytes(byte *buf, unsigned len, register crcword accum)
{
    do {
        accum = (accum<<8) ^ crctable[(byte)(accum>>CRCSHIFTS) ^ *buf++];
    }
    while (--len);
    return maskcrc(accum);
}/* crcbytes */

/* Initialize the CRC table using our codes */
static void init_crc(void)
{
    if(!crctable_initialised) {
        mk_crctbl(PRZCRC);
        crctable_initialised = TRUE;
    }
}

#define PAD  '='
/* the armoured value corresponding to no bits set */
#define ZERO 'A'

/* output one group of up to 3 bytes, pointed at by p, on file f. */
static void outdec(byte p[3], char buffer[4], int count)
{
    if(count < 3)
    {
        p[count] = 0;        /* some bits from this byte may be used */
        buffer[2] = buffer[3] = PAD;
    }
    buffer[0] = bintoasc[p[0] >> 2];
    buffer[1] = bintoasc[((p[0] << 4) & 0x30) | ((p[1] >> 4) & 0x0F)];
    if(count > 1)
    {
        buffer[2] = bintoasc[((p[1] << 2) & 0x3C) | ((p[2] >> 6) & 0x03)];
        if(count > 2) buffer[3] = bintoasc[p[2] & 0x3F];
    }
}/* outdec */


static boolean armourLine(byte * p, int count, DataFileP output)
{
    char buffer[MAX_LINE_SIZE + 1];
    char * ptr = buffer;

    while(count > 0)
    {
        outdec(p, ptr, min(count, 3));
        p += 3;
        ptr += 4;
        count -= 3;
    }
    *ptr = '\0';
    return (boolean) (vf_writeline(buffer, output) > 0);
}


static armour_return armour_block2(DataFileP in, DataFileP out, int max_lines)
{
    byte buffer[LINE_LEN];
    long length;
    long lines_left;
    crcword crc = CRCINIT;
    char crcoutput[6];

    init_crc();
    lines_left = max_lines ? max_lines : 1000000000L;
    while(lines_left-- > 0 && (length = vf_read(buffer, LINE_LEN, in)) > 0)
    {
        crc = crcbytes(buffer, (unsigned)length, crc);
        if(!armourLine(buffer, (int)length, out)) return ARM_FILE_ERROR;
        if(user_break()) return ARM_USER_BREAK;
    }
    buffer[0] = (byte)((crc>>16) & 0xff);
    buffer[1] = (byte)((crc>>8) & 0xff);
    buffer[2] = (byte)((crc>>0) & 0xff);
    crcoutput[0] = PAD;
    outdec(buffer, crcoutput + 1, 3);
    crcoutput[5] = '\0';
    vf_writeline(crcoutput, out);

    /* return true if EOF rather than line count stop us */
    return (lines_left >= 0 ? ARM_SUCCESS : ARM_LINE_LIMIT);
}


static void intToChar(char * buffer, int n)
{
    char * start = buffer + strlen(buffer);
    int digits = 0;
    int temp = n;

    while(temp > 0) {
        temp /= 10;
        digits++;
    }
    digits = max(digits, 2);
    start[digits] = '\0';
    while(digits-- > 0)
    {
        start[digits] = (char)( '0' + (n % 10));
        n /= 10;
    }
}

/* N.B.  'to' must be one longer than MAX_LINE_SIZE for the terminating null */
static void safecat(char * to, char * from)
{
    size_t toLen = strlen(to);
    char * toEnd = to + toLen;
    size_t left = MAX_LINE_SIZE - toLen;

    strncpy(toEnd, from, left);
    to[MAX_LINE_SIZE] = '\0';
}


boolean armour_mode_avail(armour_style style)
{
    switch(style)
    {
    case ARM_PGP:
    case ARM_PGP_PLAIN:
    case ARM_UUENCODE:
        return TRUE;

    default:
        return FALSE;
    }
}

#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (disable: 4701)
#endif
armour_return armour_block(DataFileP in, DataFileP out, armour_params * params)
{
    char buffer[MAX_LINE_SIZE + 3];
    armour_return status;
    boolean limited=FALSE;

    if(params->style == ARM_PGP_PLAIN)
    {    /* Plain armouring; (a bit of a non-event really) the file is copied as text with the potential
     ** implied carriage control change depending on file setup and with the start-line prefixed.
     ** There is no end line.  The end is designated by the start of the signature block.
     ** N.B.  The comparitively short buffer size will wrap short lines.   It is important that the
     **         signing code has taken this into account!   */
        int length;

        buffer[0] = '-';
        buffer [1] = ' ';        /* escape sequence */

        strcpy(buffer+2, STARTLINE);
        safecat(buffer+2, SIGNEDMESS);
        if(vf_writeline(buffer+2, out) <= 0) return ARM_FILE_ERROR;
        if(vf_writeline("", out) < 0) return ARM_FILE_ERROR;

        while((length = (int) vf_readline(buffer+2, MAX_LINE_SIZE + 1, in)) >= 0)
        {        /* if the plaintext starts with 'from ' or '-', prepend escape */
            /* These exact byte values should be Multi-byte safe */
            if('-' == buffer[2] || !strncmp("From ", buffer+2, 5))
                vf_writeline(buffer, out);
            else
                vf_writeline(buffer+2, out);
        }
        if(length < 0 && length != ENDOFFILE)
            return ARM_FILE_ERROR;
        else
            if(vf_writeline("", out) < 0) return ARM_FILE_ERROR;
        return ARM_SUCCESS;
    }

    if(params->style == ARM_UUENCODE) return uuencode_block(in,out, params);

    if(params->style != ARM_PGP) return ARM_UNIMPLEMENTED;

    /* compute here the number of blocks required */
    if(params->max_lines > 0)
    {
        unsigned long bytesPerSection = 48UL*params->max_lines;
        params->of = (int)((vf_length(in)+bytesPerSection-1)/bytesPerSection);
        if(params->of > 99) return ARM_FORMAT_ERR;
    }
    else
    {
        params->of = 1;
    }

    for(params->number = 1;
        params->number <= params->of;
        params->number++)
    {
        strcpy(buffer, STARTLINE);
        safecat(buffer, params->block_type);
        if(params->of > 1)
        {
            safecat(buffer, PARTS);
            intToChar(buffer, params->number);
            safecat(buffer, "/");
            intToChar(buffer, params->of);
        }
        safecat(buffer, LINEEND);
        if(vf_writeline(buffer, out) <= 0) return ARM_FILE_ERROR;

        strcpy(buffer, VERSIONLINE);
        safecat(buffer, params->version);
        if(vf_writeline(buffer, out) <= 0) return ARM_FILE_ERROR;

        if(params->comments)
        {
            char * * comment = params->comments;
            while(*comment)
            {
                strcpy(buffer, COMMENTLINE);
                safecat(buffer, *comment++);
                if(vf_writeline(buffer, out) <= 0) return ARM_FILE_ERROR;
            }

        }

        if(vf_writeline("", out) <= 0) return ARM_FILE_ERROR;

        status = armour_block2(in, out, params->max_lines);
        if(ARM_LINE_LIMIT == status) limited = TRUE;

        if(status == ARM_SUCCESS || status == ARM_LINE_LIMIT)
        {
            strcpy(buffer, ENDLINE);
            safecat(buffer, params->block_type);
            safecat(buffer, LINEEND);
            if(vf_writeline(buffer, out) <= 0) return ARM_FILE_ERROR;
        }
        else return status;
    }    /* end of for */
    /* if we have line-limited in practise, then signal this up on success */
    return limited && (status==ARM_SUCCESS) ? ARM_LINE_LIMIT : status;

}
#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (default: 4701)
#endif


/* unarmour routines. */

static boolean dearmour_buffer(char *inbuf, byte *outbuf, int *outlength)
{
    byte *bp;
    int length;
    unsigned int c1,c2,c3,c4;
    boolean hit_padding = FALSE;

    length = 0;
    bp = (byte *)inbuf;

    /* FOUR input characters go into each THREE output charcters */

    while(*bp != '\0' && !hit_padding)
    {
        /* check for padding */
        if(bp[3] == PAD)
        {
            hit_padding = TRUE;
            if(bp[2] == PAD || !strcmp((char*)bp + 2, "=3D=3D"))
            {
                length += 1;
                bp[2] = ZERO;
            }
            else
                length += 2;
            bp[3] = ZERO;
        }
        else
            length += 3;        /* unpadded */

        if( bp[0] & 0x80 || (c1 = asctobin[bp[0]]) & 0x80 ||
            bp[1] & 0x80 || (c2 = asctobin[bp[1]]) & 0x80 ||
            bp[2] & 0x80 || (c3 = asctobin[bp[2]]) & 0x80 ||
            bp[3] & 0x80 || (c4 = asctobin[bp[3]]) & 0x80) return FALSE;
        bp += 4;
        *outbuf++ = (byte)((c1 << 2) | (c2 >> 4));
        *outbuf++ = (byte)((c2 << 4) | (c3 >> 2));
        *outbuf++ = (byte)((c3 << 6) | c4);
    }

    *outlength = length;
    return TRUE;    /* normal return */

}/* dearmour_buffer */

armour_return unarmour_block(DataFileP in, DataFileP out, armour_info *info)
{
    char inBuf[MAX_LINE_SIZE + 3];
    byte outBuf[64];
    byte crcBuf[4];
    long armourlength;
    int binarylength;
    int crclength;
    long chkcrc;
    long crc;
    int part;
    int maxpart = info->of;

    if(info->style == ARM_PGP_PLAIN)
    {
        long pos;
        char prevline[MAX_LINE_SIZE + 3];
        boolean prevSet = FALSE;
        boolean textCopied = FALSE;

        /* Copies and canonicalises text until start of signature block */
        /* This just moves the user text without inspection assuming sane line size */
        /* So no multi-byte worries here */
        vf_CCmode(CANONICAL, out);
        while(TRUE)
        {
            pos = vf_where(in);
            armourlength = vf_readline(inBuf, MAX_LINE_SIZE + 3, in);
            if(armourlength < 0) return ARM_FILE_ERROR;
            if(!strncmp(inBuf, SIGBLOCK, strlen(SIGBLOCK)))
            {
                /* reset to start of -----BEGIN PGP SIGNATURE-----*/
                vf_setpos(in, pos);
                if(prevSet)
                {                /* write final line without terminator */
                    if(textCopied)vf_writeline("", out);
                    if(strlen(prevline) > 0)vf_writeline_xt(prevline, out);
                }
                return ARM_SUCCESS;
            }
            else
            {
                /* we defer writing so as to place \r\n between lines only as a separator */
                if(prevSet)
                {
                    if(textCopied)vf_writeline("", out);
                    vf_writeline_xt(prevline, out);
                    textCopied = TRUE;
                }
                /*
                 *                             this we inherit from PGP's determinedly ASCII world -
                 *                             it corrupts any multi-byte character with a trailing
                 *                             byte of 0x20.
                 */
                while(armourlength > 0 &&
                    inBuf[(size_t)(armourlength - 1)] == ' ')
                {
                    armourlength--;
                }
                inBuf[(size_t)armourlength] = '\0';

                /* remove any leading escape sequence from the plaintext */
                if('-' == inBuf[0] && ' ' == inBuf[1])
                    strcpy(prevline, inBuf+2);
                else
                    strcpy(prevline, inBuf);
                prevSet = TRUE;
            }
        }
    }

    /* intercept other armourings here! */
    if(info->style == ARM_UUENCODE)
        return uudecode_block(in, out);
    else if(info->style != ARM_PGP &&
        info->style != ARM_PGP_MULTI)
        return ARM_UNIMPLEMENTED;

    /* classic format, perhaps multipart */

    init_crc();
    for(part=1; part<=maxpart; ++part)
    {
        if(info->number != part || info->of != maxpart)
            return ARM_FORMAT_ERR;        /* out of order */
        crc = CRCINIT;

        while(TRUE)
        {
            while(0 ==             /* no multi-byte worries with armoured code by assumption */
            (armourlength = vf_readline(inBuf, MAX_LINE_SIZE + 1, in))){
                ;
            }

            if(armourlength < 0) return ARM_FILE_ERROR;

            /*
             *                Trim trailing spaces.  This is not really enough to be OpenPGP compliant,
             *                but is likely to work for most sane implementations...
             */
            {
                char * space = (char*) seekTrailingWhitespace(inBuf);
                if(space != 0) *space = 0;
            }

            if(inBuf[0] == PAD)
            {
                /* checksum line found */
                if(strlen(inBuf) == 7 && inBuf[1] == '3' && inBuf[2] == 'D')
                {
                    if(!dearmour_buffer(inBuf + 3, crcBuf, &crclength))
                        return ARM_FORMAT_ERR;
                }
                else if(strlen(inBuf) == 5)
                {
                    if(!dearmour_buffer(inBuf + 1, crcBuf, &crclength))
                        return ARM_FORMAT_ERR;
                }
                else
                    return ARM_FORMAT_ERR;
                chkcrc = (((long)crcBuf[0]<<16) & 0xFF0000L) +
                    ((crcBuf[1]<<8) & 0xFF00L) + (crcBuf[2] & 0xFFL);

                if(crc != chkcrc)
                    return ARM_CRC_FAILURE;
                else
                    break;
            }

            if(!dearmour_buffer(inBuf, outBuf, &binarylength)) return ARM_FORMAT_ERR;
            crc = crcbytes(outBuf, binarylength, crc);
            if(vf_write(outBuf, binarylength, out) < 0) return ARM_FILE_ERROR;
            if(user_break()) return ARM_USER_BREAK;
        }        /* wend */

        if(part == maxpart) break;        /* no next section to find */
        if(next_armour(in, info) != ARM_PGP_MULTI)
            return ARM_FORMAT_ERR;        /* bizarresville ! */

    }    /*end for */
    return ARM_SUCCESS;
}

static int charToInt(char * number)
{
    int result = 0;

    while('0' <= *number && *number <= '9')
        result = 10 * result + (*number++ - '0');
    return result;
}

/* This routine advances the file pointer of inFile to the next armoured block */
/* It returns false if no block is found.                                      */
/* If a block is found number and of are set to the section <number>/<of> values */
/* of the header.  If it is a selfcontained block both are set to one.           */
armour_style next_armour(DataFileP inFile, armour_info * info)
{
    char inBuf[MAX_LINE_SIZE + 1];
    long lineLength;

    info->number = info->of = 1;    /* defaults */
    info->name[0] = '\0';
    info->style = ARM_PGP;    /* only format currently supported */
    /* no multi-byte worries with armour */
    while(vf_readline(inBuf, MAX_LINE_SIZE + 1, inFile) >= 0)
    {
        if(!strncmp(inBuf, STARTLINE, strlen(STARTLINE)))
        {
            char * slash = strchr(inBuf, '/');
            char * numPtr;
            char * block_type = inBuf + strlen(STARTLINE);
            if(slash)
            {            /* multi-part section numbers present */
                *slash = '\0';                /* cut the string in two */
                numPtr = strrchr(inBuf, ' ') + 1;
                info->number = charToInt(numPtr);
                info->of = charToInt(slash + 1);
                if(info->of > 1) info->style = ARM_PGP_MULTI;
            }
            else
                numPtr = strchr(block_type, '-');
            /* numPtr should the character after the block_type field */
            if(numPtr)
            {
                size_t length = (size_t)(numPtr - block_type);

                strncpy(info->name, block_type, length);
                info->name[length] = '\0';
            }
            /* skip version line & comment lines */
            do
            {
                lineLength = vf_readline(inBuf, MAX_LINE_SIZE, inFile);
            }
            while(lineLength > 0);
            if(lineLength < 0)
                return ARM_NONE;
            else if(!strncmp(block_type, SIGNEDMESS, strlen(SIGNEDMESS)))
                return (info->style = ARM_PGP_PLAIN);
            else
                return info->style;
        }

        /* single part simple UUENCODE */
        if(!strncmp(inBuf, UUSTARTLINE, strlen(UUSTARTLINE)))
        {
            /*strcpy(info->name, inBuf+10);*/             /* simple extraction of file name */
            strtok(inBuf+6, " \t");            /* return Access control */
            strcpy(info->name, strtok(NULL, " \t"));            /* next token is filename */
            return (info->style = ARM_UUENCODE);
        }
    }
    return ARM_NONE;
}

/* end of file armour.h */

