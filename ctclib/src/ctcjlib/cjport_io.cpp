/***************************************************************************
                          cjport_io.cpp  -  description
                             -------------------
    copyright            : (C) 1996 by Mr. Tines
    email                : tines@ravnaandtines.com
 ***************************************************************************/

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
/* port_io.c
 *
 *  This is an <stdio.h> implementation of CTC's port_io module.
 *
 *  There are minimal modifications for Java implementation, to allow
 *  for buffers which are managed as a java byte[] for binary data
 *  and as a String for text data.  Note that support for multi-byte
 *  (mixed ASCII and longer chars only!) is sketched out
 *
 *  Coded & copyright
 *  Heimdall <heimdall@bifroest.demon.co.uk>  1996
 *  Mr. Tines <tines@com/ravnaandtines.demon.co.uk> 1998
 *  All rights reserved.  For full licence details see file licences.c
 */

#include <jni.h>
#include "ctcjlib.h"
#include <ctype.h>
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <assert.h>
#include "port_io.h"
#include "random.h"
#include "utfcode.h"
#include "widechar.h"

/* trap calls to unimplemented routines */
#include "usrbreak.h"

// BC++5.0   VC++5.0  other
#if defined(CTC_NAMESPACE_SUPPORT)
namespace CTClib {
    using CTCjlib::getEnvironment;
#endif


#define MAXDATAFILE 25
#define MAXOSFILE 10

    /* we default to Unix terminators even on DOS because we read ASCII text
     * using fgets(), which returns Unix style line-ends by definition from
     * ASCII files */
#define TERMINATORS "\n\r"
#ifdef THINK_C
#define LOCALMC MAC
#else
#define LOCALMC PC
#endif

#define BUFFERSIZE 1024

    typedef enum { 
        FREE, OSFILE, STATIC, SLICE, COMPOUND, JAVATEMP, JAVATEXT, JAVABUFF     } 
    vf_type;

    typedef struct osFile
    {
        FILE * C_file;
        accessMode mode; 
        int   ref_count;
        DataFileP last_ref;
        struct osFile * next_free;        /* for when in free block list */
    } 
    osFile;
    typedef osFile * osFileP;

    struct DataFile_T
    {
        vf_type type;
        accessMode mode;
        long pos;        /* current file position */
        boolean pos_changed;        /* If position has been externally set */
        long length;        /* -1 if unknown/undefined (MUST be defined for a STATIC or SLICE) */
        char * lineEnd;
        byte *  userBuffer;
        union
        {
            DataFileP next_free;
            osFileP osfile;
            struct {
                byte *  data;
                boolean sensitive;
            }
            buffer;
            struct 
            { 
                DataFileP whole; 
                long offset;
            } 
            slice;
            struct { 
                DataFileP most; 
                long   split;                /* length of 'most' */
                DataFileP last; 
            } 
            compound;
            struct {
                JNIEnv * env;
                jobject file;                /* java file */
                bool trim;          // trim trailing spaces in vf_readline
            } 
            j;
        } 
        u;
    };

#define Environment(file) file->u.j.env
    //static FILE * echo = fopen("I:\\vf_echo.txt", "w");
    //FILE * getEcho() {return echo;}

    static DataFile  dataFiles[MAXDATAFILE];
    static osFile  osFiles[MAXOSFILE];
    static DataFileP freeDataFiles = NULL;
    static osFileP  freeOsFiles = NULL;
    static boolean  initialised = FALSE;
    static char  * lineEnds[5]
        = { 
        "\r\n",         /* CANONICAL  = (PC) */
        NULL,         /* LOCAL Set in initialise() */
        "\r\n",         /* PC     */
        "\n",         /* UNIX     */
        "\r"     };    /* MAC     */

    static char * CopenMode(accessMode access, fileType type)
    {
        switch(type)
        {
        case TEXTCYPHER:
        case TEXTPLAIN:
            switch(access)
            {
            case READ:   
                return "r";
            case READWIPE:  
                return "r+";
            case WRITE:   
                return "w";
            case UPDATE:  
                return "r+";
            case UPDATEWIPE: 
                return "r+";
            case WRITEREAD:  
                return "w+";
            case WRITEREADWIPE: 
                return "w+";
            }
            break;

        case BINCYPHER:
        case BINPLAIN:
        case PUBLICRING:
        case SECRETRING:
            switch(access)
            {
            case READ:   
                return "rb";
            case READWIPE:  
                return "r+b";
            case WRITE:
                return "wb";
            case UPDATE:  
                return "r+b";
            case UPDATEWIPE: 
                return "r+b";
            case WRITEREAD:  
                return "w+b";
            case WRITEREADWIPE: 
                return "w+b";
            }
        }
        return NULL;
    }


    static boolean readable(DataFileP file)
    {
        switch(file->mode)
        {
        case WRITEREAD:  
            file->mode = READ; 
            return TRUE;

        case WRITEREADWIPE: 
            {
                file->mode = READWIPE; 
                if (JAVATEMP == file->type)
                {
                    jclass fileclass = Environment(file)->FindClass("com/ravnaandtines/ctcjava/CJTempfile");
                    jmethodID swap  = Environment(file)->GetMethodID(fileclass, "swap", "()V");
                    Environment(file)->CallVoidMethod(file->u.j.file, swap);
                }
            }
            return TRUE;

        case READ:
        case READWIPE:
        case UPDATE:
        case UPDATEWIPE: 
            return TRUE;

        default:   
            return FALSE;
        }
    }


    static boolean writable(DataFileP file)
    {
        switch(file->mode)
        {
        case WRITEREAD:
        case WRITEREADWIPE:
        case WRITE:
        case UPDATE:
        case UPDATEWIPE: 
            return TRUE;

        default:   
            return FALSE;
        }
    }


    static void checkStack(void)
    {
        DataFileP p = freeDataFiles;
        int k;
        for(k=0;k<MAXDATAFILE;++k)
        {
            int i = (int)(p - dataFiles);
            if(i < 0) bug_check("datafile underflow\n");
            if(i > 24) bug_check("datafile overflow\n");
            p = p->u.next_free;
            if(!p) return;
        }
        bug_check("datafile loop\n");
    }

    static void releaseDataFile(DataFileP dataFile)
    {
        if(!dataFile) return;        /* allow a NULL argument */
        dataFile->u.next_free = freeDataFiles;
        dataFile->type   = FREE;
        freeDataFiles  = dataFile;
    }


    static void releaseOsFile(osFileP osFile)
    {
        if(!osFile) return;        /* allow a NULL argument */
        osFile->next_free = freeOsFiles;
        freeOsFiles   = osFile;
    }


    static void initialise(void)
    {
        int offset = MAXDATAFILE;

        if(!initialised)
        {
            while(offset-- > 0) releaseDataFile(&dataFiles[offset]);
            offset = MAXOSFILE;
            while(offset-- > 0) releaseOsFile(&osFiles[offset]);
            lineEnds[LOCAL] = lineEnds[LOCALMC];
            initialised = TRUE;
        }
    }


    static DataFileP grabDataFile(void)
    {
        DataFileP result;

        if(!initialised) initialise();
        result = freeDataFiles;
        if(freeDataFiles)
        {
            freeDataFiles = freeDataFiles->u.next_free;
        }
        /* set obvious default values */
        result->pos   = 0;
        result->pos_changed = FALSE;
        result->length  = -1;
        result->lineEnd  = lineEnds[LOCAL];
        result->userBuffer = NULL;
        return result;
    }


    static osFileP grabOsFile(void)
    {
        osFileP result;

        if(!initialised) initialise();
        result = freeOsFiles;
        if(freeOsFiles)
        {
            freeOsFiles = freeOsFiles->next_free;
        }
        return result;
    }


    static void setpos(DataFileP file)
    {    /* Only valid to be called on an OS file or Java buffer */
        switch(file->type)
        {
        case OSFILE:
            if(writable(file)) fflush(file->u.osfile->C_file);
            if(file->pos_changed || file->u.osfile->last_ref != file)
            {
                fseek(file->u.osfile->C_file, file->pos, SEEK_SET);
                file->u.osfile->last_ref = file;
                file->pos_changed = FALSE;
            }
            return;
        case JAVATEMP:
            if(file->pos_changed)
            {
                jclass fileclass = Environment(file)->FindClass("com/ravnaandtines/ctcjava/CJTempfile");
                jmethodID setpos  = Environment(file)->GetMethodID(fileclass, "setpos", "(I)V");

                if(file->pos > file->length) file->pos = file->length;

                Environment(file)->CallVoidMethod(file->u.j.file, setpos, file->pos);
                file->pos_changed = FALSE;

                //fprintf(echo, "Java %08x setpos to %d\n", file, file->pos);
                //fflush(echo);
            }
            return;
        default:
            return;
        }
    }

#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (disable: 4706)
#endif
    extern "C" DataFileP vf_open(const char * name, accessMode mode, fileType type)
    {
        DataFileP result = grabDataFile();
        osFileP  osfile = grabOsFile();
#ifdef __BORLANDC__
#pragma warn -pia
#endif
        if( result && osfile &&
            (osfile->C_file = fopen(name, CopenMode(mode,type)))
            )
#ifdef __BORLANDC__
#pragma warn .pia
#endif
        {
            /* ensure I/O unbuffered for security */
            setbuf(osfile->C_file, (char*)0);

            osfile->mode  = mode;
            osfile->ref_count = 1;
            osfile->last_ref = result;

            result->mode  = mode;
            result->type  = OSFILE;
            result->u.osfile = osfile;
            return result;
        }
        releaseDataFile(result);
        releaseOsFile(osfile);
        return NULL;
    }
#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (default: 4706)
#endif

    /* Note - replacement file must vanish if closed
     * before replaceWith is called, so tempfile is simplest mechanism  */
    extern "C" DataFileP vf_toReplace(DataFileP old)
    {
        if(writable(old)) return vf_tempfile(vf_length(old));
        return (DataFileP)0;
    }
    /*  The real spec should be
     * DataFileP vf_toReplace(DataFileP old, accessMode mode);
     * Creates a new potentially permanent but as yet unnamed (or
     * temporary named) file, in the same area (same partition/directory)
     * as 'old'.  The fileType of the result is inherited from 'old'.
     * 
     * A file created with vf_toReplace()  should be deleted if it is
     * closed with vf_close(), and will continue to exist with the old
     * file's name if it is closed as second argument to
     * vf_replaceWith()
     *
     */
    static void wipe(FILE * file)
    {
        long length;
        short block, offset;
        byte buffer[BUFFERSIZE];

        fseek(file, 0, SEEK_END);
        length = ftell(file);
        fseek(file, 0, SEEK_SET);
        while(length > 0)
        {
            block = (short)(min((int)length, BUFFERSIZE));
            offset = block;
            while(offset-- > 0) buffer[offset] = randombyte();
            if((block = (short) fwrite(buffer, 1, block, file)) <= 0) return;
            length -= block;
        }
    }


    extern "C" void vf_close(DataFileP file)
    {
        osFileP osfile;
        checkStack();
        switch(file->type)
        {
        case OSFILE:
            osfile = file->u.osfile;
            osfile->ref_count--;
            if(writable(file))
                fflush(osfile->C_file);
            if(osfile->ref_count <= 0)
            {
                switch(osfile->mode)
                {
                case READWIPE:
                case UPDATEWIPE:
                case WRITEREADWIPE:
                    wipe(osfile->C_file);
                    break;
                default:
                    break;
                }
                fclose(osfile->C_file);
                releaseOsFile(osfile);
            }
            else if(osfile->last_ref == file)
                osfile->last_ref = NULL;
            break;

        case SLICE:
            vf_close(file->u.slice.whole);
            break;

        case JAVATEMP:
            // call the close method of the object
            //   public synchronized void close()
            // and splat it
            {
                jclass fileclass = Environment(file)->FindClass("com/ravnaandtines/ctcjava/CJTempfile");
                jmethodID close  = Environment(file)->GetMethodID(fileclass, "close", "()V");

                // this zeroes the memory
                Environment(file)->CallVoidMethod(file->u.j.file, close);
            } // DROP THRO'
        case JAVATEXT:
        case JAVABUFF:
            {
                // this releases it for the garbos
                Environment(file)->DeleteGlobalRef(file->u.j.file);
                file->u.j.file = NULL;
                //fprintf(echo, "Java %08x close\n", file);
                //fflush(echo);
            }
            break;

        case FREE:
            /*bug_check("Double-freeing a datafile\n");
             *          break;*/ return;

        default:
            break;
        }
        releaseDataFile(file);
    }

    /* implement replacement by overwrite and renaming; assumes free list
     * behaves suitable stck like in the context of vf_close() followed
     * by vf_copyref() */
    extern "C" void  vf_replaceWith(DataFileP oldFile, DataFileP newFile)
    {
        byte buffer[1024];
        DataFileP temp;
        long length;

        /* copy contents of new to overwrite old*/
        vf_setpos(newFile, 0);
        vf_setpos(oldFile,   0);
        while((length = vf_read(buffer, 1024, newFile)) > 0)
            vf_write(buffer, length, oldFile);

        vf_close(newFile);        /* destroy temporary file, putting handle on free-stack*/

        temp = vf_copyRef(oldFile);
        assert(temp == newFile);        /* should just have popped it off the stack */

        if(OSFILE == oldFile->type)
        {
            osFileP osfile = newFile->u.osfile;
            if(oldFile == osfile->last_ref)
                osfile->last_ref = newFile;
        }

        /* relinquish this handle */
        vf_close(oldFile);
    }
    /* The real spec should be
     * void  vf_replaceWith(DataFileP old, DataFileP new);
     * This function closes both files.  It renames 'old' to a backup
     * name (.bak or similar), renames 'new' to the former name of 'old'
     * and closes both - although not necessarily in that order.
     * 
     * vf_replaceWith() can also be applied to files that have been
     * opened vf_open().
     * vf_replaceWith is not guaranteed to work unless called with a
     * former argument to vf_toReplace()and that call's result.
     */




    extern "C" long vf_readline(char * buffer, long buffer_size, DataFileP file)
    {
        long length;
        DataFileP part;
        long result;

        if(!readable(file)) return WRONGMODE;
        if(file->length >= 0 && file->pos >= file->length) return ENDOFFILE;
        switch(file->type)
        {
        case SLICE:
            /* if we're using fgets, we need to allow 1 for the null that
             * will be added to the end of the string and is decremented
             * from the length of the available buffer for input!  This is
             * a long-standing fencepost bug in this routine. */
            length = 1 + file->length - file->pos;
            length = min(buffer_size, length);
            if(length <= 0) return -1;
            part = file->u.slice.whole;
            if(file->pos_changed)
            {
                part->pos = file->pos + file->u.slice.offset;
                part->pos_changed = TRUE;
            }
            result = vf_readline(buffer, length, part);
            file->pos = part->pos - file->u.slice.offset;
            return result;

        case COMPOUND:
            if(file->pos + buffer_size <= file->u.compound.split)
            {            /* Read is restricted to 'most' part */
                vf_setpos(file->u.compound.most, file->pos);
                result = vf_readline(buffer, buffer_size, file->u.compound.most);
            }
            else if(file->pos >= file->u.compound.split)
            {            /* read is restricted to the 'last' part */
                vf_setpos(file->u.compound.last, file->pos - file->u.compound.split);
                result = vf_readline(buffer, buffer_size, file->u.compound.last);
            }
            else
            {            /* read involves both parts */
                long len1 = file->u.compound.split - file->pos;
                long part1;

                vf_setpos(file->u.compound.most, file->pos);
                vf_setpos(file->u.compound.last, 0);
                part1 = vf_readline(buffer, len1, file->u.compound.most);
                if(part1 != len1)
                    result = part1;
                else
                {
                    result = vf_readline(buffer + (size_t)len1, buffer_size - len1,
                    file->u.compound.last);
                    if(result >= 0) result += part1;
                }
            }
            if(result > 0) file->pos += result;
            return result;
        case OSFILE:
            {
                length = 0;
                int eol = 0;
                boolean eof = FALSE;
                boolean takeback = FALSE;
                // set to read position
                setpos(file);

                // note that as tmpfile() operates in binary mode, we have to do this by hand
                // rather than relying on fgetws()

                while(length < (long)(buffer_size-MB_CUR_MAX))
                {
                    char * base = buffer + length;
                    int    cbase = length;
                    long start = ftell(file->u.osfile->C_file);

                    // keep reading until we have a character; or have read a
                    // maximum number of bytes for a character.  This does
                    // not handle broken characters very well.
                    do
                    {
                        result = (long) fread(buffer+length, 1, (size_t)1, file->u.osfile->C_file);
                        if(0==result)
                        {
                            eof = TRUE;
                            break;
                        }
                        ++length;
                        if(mblen(base, length-cbase) < 0) break;
                    }
                    while(length-cbase < (long) MB_CUR_MAX);

                    /* broken character data detected - discard 1 byte, reset and try again */
                    if(length-cbase == (long) MB_CUR_MAX && mblen(base, length-cbase) < 0)
                    {
                        length = cbase;
                        fseek(file->u.osfile->C_file, start+1, SEEK_SET);
                        continue;
                    }

                    // nothing else to do, assuming no broken characters
                    if(eof) break;

                    // this assumes multibytes can't be EOL; there is a Unicode line separator character,
                    // however, which this neglects
                    if(length > cbase + 1) continue;

                    // assume \n, \r or \r\n as possible line breaks as above
                    switch(*base)
                    {
                    case '\n':
                        --length; // discard
                        ++eol;
                        break;
                    case '\r':
                        --length; // discard
                        ++eol;
                        result = (long) fread(buffer+length, 1, (size_t)1, file->u.osfile->C_file);
                        if('\n' == buffer[length]) ++eol;
                        else if(result !=0) takeback = TRUE;
                        break;
                    default:
                        {
                            ;
                        }// nothing
                    }
                    if(eol) break;
                }
                /* add trailing null */
                buffer[length] = 0;

                /* Can't simply say "file->pos += length;" because the action of
                 * opening as ASCII is to collapse the \n\r of a DOS file
                 * to simply \n, so length <= offset change.  So we consult the
                 * function that gives us the true gen! Like this we should be
                 * general for all stdio using systems. */
                file->pos = ftell(file->u.osfile->C_file);
                if(eof && 0==length) return -1;

                /* a non-\n byte must be kosher */
                if(takeback) --file->pos;
                break;
            }
        case JAVATEMP:
            {
                jclass fileclass = Environment(file)->FindClass("com/ravnaandtines/ctcjava/CJTempfile");

                // ensure now in read mode
                jmethodID swap  = Environment(file)->GetMethodID(fileclass, "swap", "()V");
                Environment(file)->CallVoidMethod(file->u.j.file, swap);

                // get the position up to date
                setpos(file);

                // keep reading bytes; assume that \r and \n are't used
                // in multibytes
                jmethodID read  = Environment(file)->GetMethodID(fileclass, "read", "()I");

                length = 0;
                int eol = 0;
                boolean eof = FALSE;
                int discards = 0;
                while(length < (long)(buffer_size-MB_CUR_MAX))
                {
                    char * base = buffer + length;
                    int    cbase = length;
                    jint v;

                    // keep reading until we have a character; or have read a
                    // maximum number of bytes for a character.  This does
                    // not handle broken characters very well.
                    do
                    {
                        v = Environment(file)->CallIntMethod(file->u.j.file, read);
                        if(v<0)
                        {
                            eof = TRUE;
                            break;
                        }
                        buffer[(size_t)length] = (char)(v&0xFF);
                        ++length;
                        if(mblen(base, length-cbase) < 0) break;
                    }
                    while(length-cbase < (long)MB_CUR_MAX);

                    /* broken character data detected - discard 1 byte, reset and try again */
                    if(length-cbase == (long) MB_CUR_MAX && mblen(base, length-cbase) < 0)
                    {
                        length = cbase;
                        ++discards;
                        vf_setpos(file, file->pos+length+discards);
                        continue;   
                    }

                    // nothing else to do, assuming no broken characters
                    if(eof) break;

                    // this assumes multibytes can't be EOL; there is a Unicode line separator character,
                    // however, which this neglects
                    if(length > cbase + 1) continue;

                    // assume \n, \r or \r\n as possible line breaks as above
                    switch(*base)
                    {
                    case '\n':
                        --length; // discard
                        ++eol;
                        break;
                    case '\r':
                        --length; // discard
                        ++eol;
                        v = Environment(file)->CallIntMethod(file->u.j.file, read);
                        if('\n' == v) ++eol;  // nice, as we can take it back easily
                        break;
                    default:
                        {
                            ;
                        }// nothing
                    }
                    if(eol) break;
                }
                /* add trailing null */
                buffer[length] = 0;
                if(eof && 0==length) return -1;

                vf_setpos(file, file->pos+length+discards+eol);
                if(file->u.j.trim)
                {
                    // trim trailing whitespace
                    char * space = (char*) seekTrailingWhitespace(buffer);
                    if(space != 0) 
                    {
                        *space = 0;
                        length = (long)(space-buffer);
                    }
                }

            }
            break;

        default:
            return UNIMPLEMENTED;
        }
        buffer[(size_t)length] = '\0';
        return length;
    }



    static long vfWriteLine(char * buffer, DataFileP file, boolean eol)
    {
        long length;
        long term_len = 0;
        FILE * CFile;

        if(!writable(file)) return WRONGMODE;
        switch(file->type)
        {
        case OSFILE:
            setpos(file);
            length = mbStrlen(buffer);
            CFile  = file->u.osfile->C_file;
            /* This just shoves anonymous bytes into the ether */
            if(fwrite(buffer, 1, (size_t)length, CFile) != (size_t)length)
                return OSERROR;
            if(eol)
            {
                term_len = (long) strlen(file->lineEnd);
                if(fwrite(file->lineEnd, 1, (size_t)term_len, CFile) != (size_t) term_len)
                    return OSERROR;
            }
            file->pos += term_len + length;
            if(file->pos > file->length) file->length = file->pos;
            return term_len + length;


        case JAVATEMP:
            {
                setpos(file);
                length = mbStrlen(buffer);
                jclass fileclass = Environment(file)->FindClass("com/ravnaandtines/ctcjava/CJTempfile");
                jmethodID write  = Environment(file)->GetMethodID(fileclass, "write", "(I)V");

                int i;
                for(i=0; i<length; ++i)
                {
                    Environment(file)->CallVoidMethod(file->u.j.file, write, buffer[i]);
                }
                if(eol)
                {
                    term_len = (long) strlen(file->lineEnd);
                    for(i=0; i<term_len; ++i)
                    {
                        Environment(file)->CallVoidMethod(file->u.j.file, write, file->lineEnd[i]);
                    }
                }
                file->pos += term_len + length;
                if(file->pos > file->length) file->length = file->pos;
                return term_len + length;
            }

        case JAVATEXT:
            {
                length = mbStrlen(buffer);

                long result = (long) (length + (eol?strlen(file->lineEnd):0));
                char * sink = new char[result+1];
                memcpy(sink, buffer, length);
                sink[length] = 0;
                if(eol)
                {
                    term_len = (long) strlen(file->lineEnd);
                    memcpy(sink+length, file->lineEnd, term_len);
                    sink[length+term_len] = 0;
                }

                jstring line = mbsToJstring(Environment(file), sink);
                delete[] sink;

                jclass fileclass = Environment(file)->FindClass(TEXTAREACLASS);
                jmethodID write  = Environment(file)->GetMethodID(fileclass, "append", "(Ljava/lang/String;)V");
                Environment(file)->CallVoidMethod(file->u.j.file, write, line);
                Environment(file)->DeleteLocalRef(line);

                file->pos = file->length+=result;
                return result;
            }

        case JAVABUFF:
            {
                length = mbStrlen(buffer);

                long result = (long) (length + (eol?strlen(file->lineEnd):0));
                char * sink = new char[result+1];
                memcpy(sink, buffer, length);
                sink[length] = 0;
                if(eol)
                {
                    term_len = (long) strlen(file->lineEnd);
                    memcpy(sink+length, file->lineEnd, term_len);
                    sink[length+term_len] = 0;
                }

                jstring line = mbsToJstring(Environment(file), sink);
                delete[] sink;

                jclass fileclass = Environment(file)->FindClass("java/lang/StringBuffer");
                jmethodID write  = Environment(file)->GetMethodID(fileclass, "append", "(Ljava/lang/String;)Ljava/lang/StringBuffer;");
                Environment(file)->CallObjectMethod(file->u.j.file, write, line);
                Environment(file)->DeleteLocalRef(line);

                file->pos = file->length+=result;
                return result;
            }


        case STATIC:
            return UNIMPLEMENTED;
        default:
            return ILLEGAL;
        }
    }

    extern "C" long vf_writeline_xt(char * buffer, DataFileP file)
    {
        return vfWriteLine(buffer, file, FALSE);
    }
    extern "C" long vf_writeline(char * buffer, DataFileP file)
    {
        return vfWriteLine(buffer, file, TRUE);
    }


    extern "C" void vf_CCmode(CCmode mode, DataFileP file)
    {
        file->lineEnd = lineEnds[mode];
    }


    extern "C" long vf_read(void * buffer, long buffer_size, DataFileP file)
    {
        FILE * CFile;
        DataFileP part;
        long result;

        if(!readable(file)) return WRONGMODE;
        switch(file->type)
        {
        case OSFILE:
            setpos(file);
            CFile = file->u.osfile->C_file;
            result = (long) fread(buffer, 1, (size_t)buffer_size, CFile);
            if(result >= 0)
            {
                file->pos += result;
                return result;
            }
            else
                return OSERROR;

        case JAVATEMP:
            {
                setpos(file);
                //fprintf(echo, "Java %08x read %d bytes\n", file, buffer_size);
                //fflush(echo);
                jclass fileclass = Environment(file)->FindClass("com/ravnaandtines/ctcjava/CJTempfile");
                jmethodID read  = Environment(file)->GetMethodID(fileclass, "read", "()I");


                //fprintf(echo, "looping\n", file);
                //fflush(echo);
                for(result=0; result<buffer_size; ++result)
                {
                    jint v = Environment(file)->CallIntMethod(file->u.j.file, read);
                    if(v < 0) break;
                    //fprintf(echo, "%02x\n", v);
                    //fflush(echo);
                    ((byte*)buffer)[result] = (byte)v;
                }
                file->pos += result;
                //fprintf(echo, "\nread %d bytes to position %d\n", result, file->pos);
                //fflush(echo);
                return result;
            }

        case SLICE:
            {
                long length = min(buffer_size, file->length - file->pos);
                if(length <= 0) return -1;
                part = file->u.slice.whole;
                if(file->pos_changed)
                {
                    part->pos = file->pos + file->u.slice.offset;
                    part->pos_changed = TRUE;
                }
                result = vf_read(buffer, length, part);
                file->pos = part->pos - file->u.slice.offset;
            }
            return result;

        case COMPOUND:
            if(file->pos + buffer_size <= file->u.compound.split)
            {            /* Read is restricted to 'most' part */
                vf_setpos(file->u.compound.most, file->pos);
                result = vf_read(buffer, buffer_size, file->u.compound.most);
            }
            else if(file->pos >= file->u.compound.split)
            {            /* read is restricted to the 'last' part */
                vf_setpos(file->u.compound.last, file->pos - file->u.compound.split);
                result = vf_read(buffer, buffer_size, file->u.compound.last);
            }
            else
            {            /* read involves both parts */
                long len1 = file->u.compound.split - file->pos;
                long part1;

                vf_setpos(file->u.compound.most, file->pos);
                vf_setpos(file->u.compound.last, 0);
                part1 = vf_read(buffer, len1, file->u.compound.most);
                if(part1 != len1) 
                    result = part1;
                else
                {
                    result = vf_read((byte*)buffer + (size_t)len1, buffer_size - len1,
                    file->u.compound.last);
                    if(result >= 0) result += part1;
                }
            }
            if(result > 0) file->pos += result;
            return result;

        default:
            return UNIMPLEMENTED;
        }
    }


    extern "C" long vf_write(void * buffer, long buffer_size, DataFileP file)
    {
        FILE * CFile;

        if(!writable(file)) return WRONGMODE;
        switch(file->type)
        {
        case OSFILE:
            setpos(file);
            CFile = file->u.osfile->C_file;
            if(fwrite(buffer, 1, (size_t)buffer_size, CFile) == (size_t) buffer_size)
            {
                /*    long Cpos = ftell(CFile);*/
                file->pos += buffer_size;
                /*    if(file->pos != Cpos)
                 *      Cpos = file->pos;*/
                if(file->pos > file->length) file->length = file->pos;
                return buffer_size;
            }
            else
                return OSERROR;

        case JAVATEMP:
            {
                byte * buf = (byte*) buffer;
                //fprintf(echo, "Java %08x write %d bytes form %d\n", file, buffer_size, file->pos);
                //for(int k=0; k<buffer_size; ++k) fprintf(echo, "%02x", buf[k]);
                //fprintf(echo, "\n", file, file->pos);
                //fflush(echo);
                jclass fileclass = Environment(file)->FindClass("com/ravnaandtines/ctcjava/CJTempfile");
                jmethodID write  = Environment(file)->GetMethodID(fileclass, "write", "(I)V");

                long i;
                for(i=0; i<buffer_size; ++i)
                {
                    Environment(file)->CallVoidMethod(file->u.j.file, write, buf[i]);
                }
                file->pos += buffer_size;
                if(file->pos > file->length) file->length = file->pos;
                return buffer_size;
            }
        default:
            return ILLEGAL;
        }
    }


    extern "C" byte * vf_buffer(long * size, DataFileP file)
    {
        FILE * CFile;

        switch(file->type)
        {
        case OSFILE:
            setpos(file);
            CFile = file->u.osfile->C_file;
            file->userBuffer = (byte *) malloc((size_t)*size);
            if(file->userBuffer)
            {
                if(file->mode == READ || file->mode == UPDATE) 
                    *size = (long) fread(file->userBuffer, 1, (size_t)*size, CFile);
                if(*size > 0) return file->userBuffer;                /* only successful return */
                free(file->userBuffer);
            }
            return NULL;

        default:
            return NULL;
        }
    }


    extern "C" long vf_release(long length, DataFileP file)
    {
        FILE * CFile;
        long result = 0;

        switch(file->type)
        {
        case OSFILE:
            if(writable(file))
            {
                CFile = file->u.osfile->C_file;
                if(fwrite(file->userBuffer, 1, (size_t)length, CFile) != (size_t) length)
                    result = OSERROR;
            }
            /* deliberate drop-through */
        case COMPOUND:
            free(file->userBuffer);
            return result;

        case SLICE:
            return vf_release(length, file->u.slice.whole);   

        default:
            return UNIMPLEMENTED;
        }
    }


    extern "C" boolean vf_movepos(DataFileP file, long offset)
    {
        if(file->type == OSFILE)
        {
            if(writable(file))fflush(file->u.osfile->C_file);
        }

        if(offset >= -file->pos)
        {
            file->pos += offset;
            file->pos_changed = TRUE;
            return TRUE;
        }
        else
            return FALSE;
    }


    extern "C" boolean vf_setpos(DataFileP file, long offset)
    {
        if(file->type == OSFILE)
        {
            if(writable(file))fflush(file->u.osfile->C_file);
        }

        if(offset >= 0)
        {
            file->pos = offset;
            file->pos_changed = TRUE;
            return TRUE;
        }
        else
            return FALSE;
    }


    extern "C" long vf_where(DataFileP file)
    {
        return file->pos;
    }


    extern "C" long vf_length(DataFileP file)
    {
        if(file->length < 0)
        {
            switch(file->type)
            {
                osFileP osfile;
                FILE * Cfile;

            case OSFILE:
                osfile = file->u.osfile;
                Cfile = osfile->C_file;
                osfile->last_ref = file;

                if(fseek(Cfile, 0 , SEEK_END)) return OSERROR;
                file->length = ftell(Cfile);
                if(file->length < 0) return OSERROR;
                file->pos = file->length;
                break;

            case COMPOUND:
                file->length = file->u.compound.split + vf_length(file->u.compound.last);
                break;
            case JAVATEMP:
                {
                    jclass fileclass = Environment(file)->FindClass("com/ravnaandtines/ctcjava/CJTempfile");
                    jmethodID length  = Environment(file)->GetMethodID(fileclass, "length", "()I");

                    file->length = Environment(file)->CallIntMethod(file->u.j.file, length);
                    //fprintf(echo, "Java %08x length %d\n", file, file->length);
                    //fflush(echo);
                    break;
                }

                /* case STATIC: //STATIC always has ->length defined */ 
            default:
                return UNIMPLEMENTED;
            }
        }
        return file->length;
    }


#ifdef __BORLANDC__
#pragma warn -par
#endif
#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (disable: 4100)
#pragma warning (disable: 4706)
#endif

    extern "C" DataFileP vf_tempfile(long expected_size)
    {


        DataFileP result = grabDataFile();
        jclass fileclass = getEnvironment()->FindClass("com/ravnaandtines/ctcjava/CJTempfile");
        jmethodID init  = getEnvironment()->GetMethodID(fileclass, "<init>", "()V");
        jobject jfile = getEnvironment()->NewObject(fileclass, init);

#ifdef __BORLANDC__
#pragma warn -pia
#endif
        if( result &&
            (result->u.j.file = getEnvironment()->NewGlobalRef(jfile))
            )
#ifdef __BORLANDC__
#pragma warn .pia
#endif
        {
            Environment(result) = getEnvironment();
            result->mode  = WRITEREADWIPE;
            result->type  = JAVATEMP;
            result->length  = 0;
            result->lineEnd  = lineEnds[LOCAL];
            result->pos         = 0;
            //fprintf(echo, "Java %08x tempfile\n", result);
            //fflush(echo);
            return result;
        }
        releaseDataFile(result);
        return NULL;
    }
#ifdef __BORLANDC__
#pragma warn .par
#endif
#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (default: 4100)
#pragma warning (default: 4706)
#endif



#ifdef __BORLANDC__
#pragma warn -par
#endif
#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (disable: 4100)
#endif
    /* stub unused variant "file" as local buffer */
    extern "C" DataFileP vf_staticfile(byte * buffer, long length)
    {
        return NULL;
    }
#ifdef __BORLANDC__
#pragma warn .par
#endif
#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (default: 4100)
#endif


    extern "C" DataFileP vf_copyRef(DataFileP file)
    {
        DataFileP result = grabDataFile();

        if(!result) return FALSE;
        memcpy(result, file, sizeof(DataFile));
        switch(file->type)
        { 
        case OSFILE:
            file->u.osfile->ref_count++;
            return result;

        default:
            /* unimplemented */
            releaseDataFile(result);
            return NULL; 
        }
    }


    extern "C" DataFileP vf_part(DataFileP file, long offset, long length)
    {
        DataFileP result = grabDataFile();

        if(!result) return FALSE;
        memcpy(result, file, sizeof(DataFile));
        switch(file->type)
        {
        case OSFILE:
        case JAVATEMP:
            result->type  = SLICE;
            result->pos   = 0;
            result->pos_changed = FALSE;
            result->length  = length;
            result->lineEnd  = file->lineEnd;
            result->userBuffer = NULL;
            result->u.slice.whole = file;
            result->u.slice.offset = offset;
            return result;

        default:
            releaseDataFile(result);
            return NULL;
        }
    }


    extern "C" DataFileP vf_concat(DataFileP file1, DataFileP file2)
    {
        DataFileP result = grabDataFile();

        if(result)
        {
            result->type = COMPOUND;
            result->pos_changed = TRUE;
            result->u.compound.most  = file1;
            result->u.compound.split = vf_length(file1);
            if(file2->type == COMPOUND)
            {
                /* This isn't actually that difficult.  However we need to keep it
                 * left recursive, so file1 is tacked on the end of file2's 'most' chain
                 * with the new (result) DataFile glueing them together.  The value that
                 * the function actually returns is 'file2'.  */
                DataFileP ptr = file2;
                while(COMPOUND == ptr->u.compound.most->type)
                    ptr = ptr->u.compound.most;

                /* ptr is now a compound file pointing to two simple files.
                 * Compound file 1 and the leading element from ptr in
                 * the new datafile, and attach that to the front of ptr */

                result->u.compound.last = ptr->u.compound.most;
                ptr->u.compound.most = result;

                /* and ready the root of the tree for return */
                result = file2;
            }
            else
            {
                result->u.compound.last = file2;
            }
        }
        return result;
    }

#if defined(CTC_NAMESPACE_SUPPORT)
} //end namespace CTClib

namespace CTCjlib {
    using namespace CTClib;
#endif

    extern "C" DataFileP vf_java(JNIEnv * env, jobject jfile, bool trim)
    {
        DataFileP result = grabDataFile();
        if( result )
        {
            result->u.j.file    = env->NewGlobalRef(jfile);
            Environment(result) = env;
            result->u.j.trim    = trim;
            result->mode  = WRITEREADWIPE;
            result->type  = JAVATEMP;
            result->length  = -1;
            result->lineEnd  = lineEnds[LOCAL];
            vf_length(result);
            result->pos         = 0;
            result->pos_changed = TRUE;
            setpos(result);
            return result;
        }
        releaseDataFile(result);
        return NULL;
    }


    extern "C" void vf_unjava(DataFileP file)
    {
        Environment(file)->DeleteGlobalRef(file->u.j.file);
        file->u.j.file = NULL;
        releaseDataFile(file);
    }

    extern "C" DataFileP vf_writeableTextArea(JNIEnv * env, jobject jfile)
    {
        DataFileP result = grabDataFile();
        if( result )
        {
            result->u.j.file    = env->NewGlobalRef(jfile);
            Environment(result) = env;
            result->u.j.trim    = false;
            result->mode  = WRITE;
            result->type  = JAVATEXT;
            result->length  = -1;
            result->lineEnd  = lineEnds[LOCAL];
            result->length      = 0;
            result->pos         = 0;
            result->pos_changed = FALSE;
            return result;
        }
        releaseDataFile(result);
        return NULL;
    }

    extern "C" DataFileP vf_writeableStringBuffer(JNIEnv * env, jobject jfile)
    {
        DataFileP result = grabDataFile();
        if( result )
        {
            result->u.j.file    = env->NewGlobalRef(jfile);
            Environment(result) = env;
            result->u.j.trim    = false;
            result->mode  = WRITE;
            result->type  = JAVABUFF;
            result->length  = -1;
            result->lineEnd  = lineEnds[LOCAL];
            result->length      = 0;
            result->pos         = 0;
            result->pos_changed = FALSE;
            return result;
        }
        releaseDataFile(result);
        return NULL;
    }

#if defined(CTC_NAMESPACE_SUPPORT)
} // end namespace CTCjlib
#endif

/* end of file port_io.c */


