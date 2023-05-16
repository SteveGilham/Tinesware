/* uuencode.c
 **
 **  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 **  Heimdall <heimdall@bifroest.demon.co.uk>  1996
 **  All rights reserved.  For full licence details see file licences.c
 **
 ** ASCII-armouring module.  This module provides routines to armour files and recover
 ** them from armoured files.  The routines support both many armoured blocks in one
 ** text file and one binary file split into many armour blocks.  However, it has NO
 ** file handling.  The open/creating/closing of files is the responsibility of
 ** calling program.  */

#include <string.h>
#include "usrbreak.h"
#include "armour.h"
#include "port_io.h"
#include "uuencode.h"

#define shave_byte(b) (byte) (((b)-' ') & 0x3F)

static boolean decode_line(char *in, byte *out, int *length)
/* length input as number of 4character packets = 3 bytes out */
{
    byte *bp = (byte *)in;
    int k = shave_byte(*bp);
    int i=0;
    byte c1,c2,c3,c4;
    int available = *length;

    *length = k;
    ++bp;
    --available;

    while(i < k)
    {
        if(available <= 0) return FALSE;

        c1=shave_byte(*bp);
        ++bp;
        --available;
        c2=shave_byte(*bp);
        ++bp;
        --available;
        c3=shave_byte(*bp);
        ++bp;
        --available;
        c4=shave_byte(*bp);
        ++bp;
        --available;

        out[i++] = (byte)((c1 << 2) | (c2 >> 4));
        if(i == k) break;
        out[i++] = (byte)((c2 << 4) | (c3 >> 2));
        if(i == k) break;
        out[i++] = (byte)((c3 << 6) | c4);
    }
    return TRUE;
}

#define low_six(b) ((b) & 0x3F)
#define fix_byte(b) (byte)((low_six(b) ? low_six(b) : 0x40) + ' ')

static boolean encode_line(byte *buffer, int length, DataFileP output)
{
    char outBuf[64], *p=outBuf;
    int i;

    *p = fix_byte(length);
    ++p;

    /* pad short buffer with nulls to a multiple of 3 bytes */
    for(i=length; length < 45 && i < 3*((length+2)/3); ++i) buffer[i] = 0;

    for(i=0; i<length; i+=3, buffer +=3, p+=4)
    {
        p[0] = fix_byte(buffer[0] >> 2);
        p[1] = fix_byte(((buffer[0] << 4) & 0x30) | ((buffer[1] >> 4) & 0x0F));
        p[2] = fix_byte(((buffer[1] << 2) & 0x3C) | ((buffer[2] >> 6) & 0x03));
        p[3] = fix_byte(buffer[2] & 0x3F);
    }
    p[0] = '\0';
    return (boolean) (vf_writeline(outBuf, output) > 0);
}


#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (disable: 4127) // conditional expression is constant
#endif

armour_return uudecode_block(DataFileP in, DataFileP out)
{
    char inBuf[64];
    byte outBuf[45];
    int binarylength;
    long armourlength;

    while(TRUE)
    {
        armourlength = vf_readline(inBuf, 64, in);
        if(armourlength <= 0) return ARM_FORMAT_ERR;
        binarylength = (int) armourlength;
        if(!decode_line(inBuf, outBuf, &binarylength)) return ARM_FORMAT_ERR;
        if(0 == binarylength) return ARM_SUCCESS;
        if(vf_write(outBuf, binarylength, out) < 0) return ARM_FILE_ERROR;
        if(user_break()) return ARM_USER_BREAK;
    }
}

#define STARTLINE "begin 644 DOOMWADS.ZIP"
#define ENDLINE1 "`"
#define ENDLINE2 "end"

armour_return uuencode_block(DataFileP in, DataFileP out,
armour_params * params)
{
    long lines_left = params->max_lines ? params->max_lines : 1000000000L;
    byte buffer[45];
    long length;
    char startline[80];

    strcpy(startline, STARTLINE);
    /* fill in supplied file name over the default */
    if(params->name) strcpy(startline+10, params->name);

    if(params->of > 1) return ARM_UNIMPLEMENTED;
    if(vf_writeline(startline, out) <= 0) return ARM_FILE_ERROR;
    while(lines_left-- > 0 && (length = vf_read(buffer, 45, in)) > 0)
    {
        if(!encode_line(buffer, (int)length, out)) return ARM_FILE_ERROR;
        if(user_break()) return ARM_USER_BREAK;
    }
    if(vf_writeline(ENDLINE1, out) <= 0) return ARM_FILE_ERROR;
    if(vf_writeline(ENDLINE2, out) <= 0) return ARM_FILE_ERROR;

    /* return true if EOF rather than line count stop us */
    return (lines_left >= 0 ? ARM_SUCCESS : ARM_LINE_LIMIT);
}



/*end of file uuencode.c */
