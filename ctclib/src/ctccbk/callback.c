/***************************************************************************
                          callback.c  -  description
                             -------------------
    copyright            : (C) 1996 by Mr. Tines & Heimdall
    email                : tines@ravnaandtines.com
                           heimdall@bifroest.demon.co.uk
 ***************************************************************************/

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
/* callback.c
**
** Contains stub code for callback use
*/
#include "port_io.h"
#include "cipher.h"
#include "keyconst.h"
#include "callback.h"
#include "utils.h"
#include "hashpass.h"
#include "keyhash.h"
#include "pkcipher.h"
#include <string.h>
#include <stdio.h>

#define BUFFERSIZE 1024

#if defined(MSDOS) || defined (__MSDOS__)
#include <conio.h>
#else
#define putch(c) putc(c, stderr)
//#error getch() from conio.h needs emulation!
#endif

#ifndef THINK_C
#define CTLC 0x03
#define BEL 0x07
#define BS 0x08
#define LF 0x0A
#define CR 0x0D
#define DEL 0x7F
void beep()
{
    putch(BEL);
}

int getPassphrase(char password[256], boolean echo)
{
    int len = 0;
#if 0
    char c;
    if(echo)
    {
        gets(password);
        return(strlen(password));
    }

    while (len < 255)
    {
        /* read a character without echoing */
        c=(char)getch();
        /* interrupt */
        if(CTLC == c) return -1;
        /* end of line */
        if((CR == c) || (LF == c)) break;
        /* delete - decrement count and guard */
        if((BS == c) || (DEL == c))
        {
            if(len > 0)len--;
            else beep();
            continue;
        }
        /* misc non-printing - skip and warn */
        if(c < ' ')
        {
            beep();
            continue;
        }
        /* something sane at last - store and step on */
        password[len] = c;
        len++;
    }
#endif
    password[len] = 0x00;
    return len;
}
#endif

/* cb_convKey:
**   The message being examined has no public key encrypted parts; it is
**   presumed conventionally encrypted, and a request is made for the
**   (single) algorithm and key from hashed passphrase */
boolean cb_convKey(cv_details *details, void **key, size_t *keylen)
{
    int len = 0;
    char buffer[256];

    printf(
    "A passphrase and algorithm are required for conventional encryption\n"
        "We will assume one of the following choices of algorithm, given that\n"
        "the passphrase is not likely to have more than 128 bits of entropy\n"
#ifndef NO_IDEA
        "    1 - IDEA in CFB mode (default)\n"
#endif
        "    2 - 128-bit Blowfish in CFB mode"
#ifdef NO_IDEA
        " (default)"
#endif
        "\n"
        "    3 - 3-Way in CFB mode\n"
        "    4 - TEA\n"
        "    5 - triple 40-bit Blowfish in CFB mode \n"
        "    6 - forward/reverse/forward 40-bit Blowfish in CFB mode\n"
        );
    gets(buffer);

    switch(buffer[0])
    {
#ifndef NO_IDEA
    default:
    case '1':
        details[0].cv_algor = CEA_IDEAFLEX;
        details[0].cv_mode = CEM_CFB;
        *keylen = cipherKey(details[0].cv_algor);
        break;
#else
    default:
#endif
    case '2':
        details[0].cv_algor = CEA_BLOW16;
        details[0].cv_mode = CEM_CFB;
        *keylen = cipherKey(details[0].cv_algor);
        break;
    case '3':
        details[0].cv_algor = CEA_3WAY;
        details[0].cv_mode = CEM_CFB;
        *keylen = cipherKey(details[0].cv_algor);
        break;
    case '4':
        details[0].cv_algor = CEA_TEA;
        details[0].cv_mode = CEM_CFB;
        *keylen = cipherKey(details[0].cv_algor);
        break;
    case '5':
        details[0].cv_algor = CEA_BLOW5;
        details[0].cv_mode = CEM_CFB|CEM_TRIPLE_FLAG;
        *keylen = 3*cipherKey(details[0].cv_algor);
        break;
    case '6':
        details[0].cv_algor = CEA_BLOW5|CEA_MORE_FLAG;
        details[0].cv_mode = CEM_CFB;
        details[1].cv_algor = CEA_BLOW5|CEA_MORE_FLAG;
        details[1].cv_mode = CEM_CFB|CEM_REVERSE_FLAG;
        details[2].cv_algor = CEA_BLOW5;
        details[2].cv_mode = CEM_CFB;
        *keylen = 3*cipherKey(details[0].cv_algor);
        break;
    }
    printf("Got mode %c.  Now enter your passphrase\n", buffer[0]);

    while(!len)
        len = getPassphrase(buffer, (boolean)FALSE);
    printf("Got the passphrase.  Thank you.\n");
    *key = zmalloc(*keylen);
    hashpass(buffer, *key, *keylen, MDA_MD5);
    gets(buffer); /* flush */
    memset(buffer, 0, 256);

    return TRUE;
}

/* cb_getFile:
**   Case of detached signature - request the file that has been signed
**   from the user to compute and check signature */
DataFileP cb_getFile(cb_filedesc * filedesc)
{
    char filename[256];
    char * prompt;

    switch(filedesc->purpose)
    {
    case SIGNEDFILE:
        prompt = "Enter name of file corresponding to this detached signature\n";
        break;
    case SPLITKEY:
        prompt = "Enter name of file for the split key\n";
        break;
    default:
        prompt = "Enter name of file\n";
    }
    printf(prompt);
    gets(filename);


    /* this is a place-holder - really need to explore the file type! */
    return vf_open(filename, filedesc->mode, filedesc->file_type);
}


int cb_need_key(seckey * keys[], int Nkeys)
{
    int offset = Nkeys;
    char password[256];
    char name[256];

    while(offset-- > 0)
    {
#ifndef THINK_C
        int len;
#endif
        name_from_key(publicKey(keys[offset]), name);
        printf("Passphase required for %s\n", name);
#ifdef THINK_C
        gets(password);
#else
#if defined(__MSDOS__) && defined(_Windows)
        len=4;
        strcpy(password, "test");
#else
        len = getPassphrase(password, FALSE);
#endif

        if(len < 0) return len;
#endif
        if(internalise_seckey(keys[offset], password))
        {
            printf("Passphrase OK\n");
            memset(password, 0, 256);
            return offset;
        }
        else memset(password, 0, 256);
        printf("Passphrase incorrect\n");
    }
    return -1;
}

static void copyBFile(DataFileP from, DataFileP to)
{
    byte buffer[BUFFERSIZE];
    long length;

    vf_setpos(from, 0);
    vf_setpos(to, 0);
    while((length = vf_read(buffer, BUFFERSIZE, from)) > 0)
        vf_write(buffer, length, to);
}

#ifdef __BORLANDC__
#pragma warn -aus
#endif
static void copyTFile(DataFileP from, DataFileP to)
{
    char buffer[256];
    long length;
    vf_setpos(from, 0);
    vf_setpos(to, 0);
    while((length = vf_readline(buffer, 256, from)) >= 0)
        vf_writeline(buffer, to);
}
#ifdef __BORLANDC__
#pragma warn .aus
#endif

static char filename[1024];

void makeFileName(char *name)
{
    strcpy(filename, name);
}


/* This needs fixing */
static char * askUser(void)
{

    /*printf("Output filename:");*/
    strcpy(filename,"plaintxt.out");
    /*return gets(filename);*/
    return filename;
}


void cb_result_file(DataFileP results, cb_details * details)
{
    char * leafname;
    DataFileP output;
    char name[256];
    time_t timeStamp;

    if(details->signatory)
    {
        /* the following addition assumes that the granularity of
                 ** time_t is one second.  I am not sure if this is guaranteed */
        timeStamp = details->timestamp + datum_time();
        name_from_key(details->signatory, name);
        printf("File from: %s\n with %s signature @ %s\n", name,
        details->valid_sig ? "good" : "bad", ctime(&timeStamp));
    }

    if(!results) return;

    if(details->fileName)
    {
        printf("File type \'%c\' name: %s\n", details->typeByte, 
        details->fileName);
        leafname = strrchr(details->fileName, '/');
        if(leafname)
            leafname++;
        else leafname = details->fileName;
        /*else if(0 == (leafname = askUser()))
                   return;*/ /* FIZME*/
    }
    else if(0 == (leafname = askUser()))
        return;
    /* must handle case of CONSOLE_ which is 'eyes-only' mode */
    switch(details->typeByte)
    {
    case 't':
        if((output = vf_open(leafname, WRITE, TEXTPLAIN)) != 0)
        {
            copyTFile(results, output);
            vf_close(output);
        }
        break;

    case 'b':
        if((output = vf_open(leafname, WRITE, BINPLAIN)) != 0)
        {
            copyBFile(results, output);
            vf_close(output);
        }
        break;

    default:
        return;
    }
    return;
}
/* end of file callback.c */




