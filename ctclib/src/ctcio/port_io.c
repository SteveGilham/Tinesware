/* port_io.c
*
*  This is an <stdio.h> implementation of CTC's port_io module.
*
*  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
*  Heimdall <heimdall@bifroest.demon.co.uk>  1996
*  All rights reserved.  For full licence details see file licences.c
*
*  Multi-byte (mixed ASCII and longer chars only!) suport sketched out
*  Mr. Tines, May 1998
*
*  Position handling in vf_length corrected - A.J. Paterson July 1999
*/
#include <ctype.h>
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <assert.h>
#include "port_io.h"
#include "random.h"
#include "widechar.h"

/* trap calls to unimplemented routines */
#include "usrbreak.h"

#define MAXDATAFILE 25
#define MAXOSFILE 10

/* we default to Unix terminators even on DOS because we read ASCII text
* using fgets(), which returns Unix style line-ends by definition from
* ASCII files */
#define TERMINATORS "\n\r"

#ifdef THINK_C    
#error The Mac version is untested  
#define LOCALMC MAC    
#else
/* yes, UNIX even though this is also for PC use, because fgets() does the
* conversion of line end to Unix style \n as it goes on a non-binary stream.
*
* Note that this assumes that we're not running Unicode or some similar
* encoding in which all characters (and specifically control) characters
* are multi-byte
*/
#define LOCALMC UNIX
#endif

#define BUFFERSIZE 1024

typedef enum { 
    FREE, OSFILE, STATIC, SLICE, COMPOUND }
vf_type;

typedef struct osFile
{
    FILE * C_file;
    accessMode mode; 
    int ref_count;
    DataFileP last_ref;
    struct osFile * next_free;    /* for when in free block list */
}
osFile;
typedef osFile * osFileP;

struct DataFile_T
{
    vf_type type;
    accessMode mode;
    long pos;    /* current file position */
    boolean pos_changed;    /* If position has been externally set */
    long length;    /* -1 if unknown/undefined (MUST be defined for a STATIC or SLICE) */
    char * lineEnd;
    byte * userBuffer;
    union
    {
        DataFileP next_free;
        osFileP osfile;
        struct {
            byte * data;
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
            long split;            /* length of 'most' */
            DataFileP last; 
        }
        compound;
    }
    u;
};

static DataFile dataFiles[MAXDATAFILE];
static osFile osFiles[MAXOSFILE];
static DataFileP freeDataFiles = NULL;
static osFileP freeOsFiles = NULL;
static boolean initialised = FALSE;
static char * lineEnds[5]
= { 
    "\r\n",     /* CANONICAL  = (PC) */
    NULL,     /* LOCAL Set in initialise() */
    "\r\n",     /* PC     */
    "\n",     /* UNIX     */
    "\r" };/* MAC     */

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
        file->mode = READWIPE; 
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
    if(!dataFile) return;    /* allow a NULL argument */
    dataFile->u.next_free = freeDataFiles;
    dataFile->type = FREE;
    freeDataFiles = dataFile;
}


static void releaseOsFile(osFileP osFile)
{
    if(!osFile) return;    /* allow a NULL argument */
    osFile->next_free = freeOsFiles;
    freeOsFiles = osFile;
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
        /* set obvious default values */
        result->pos = 0;
        result->pos_changed = FALSE;
        result->length = -1;
        result->lineEnd = lineEnds[LOCAL];
        result->userBuffer = NULL;
    }
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
{/* Only valid to be called on an OS file */
    if(writable(file)) fflush(file->u.osfile->C_file);
    if(file->pos_changed || file->u.osfile->last_ref != file)
    {
        fseek(file->u.osfile->C_file, file->pos, SEEK_SET);
        file->u.osfile->last_ref = file;
        file->pos_changed = FALSE;
    }
}


DataFileP vf_open(const char * name, accessMode mode, fileType type)
{
    DataFileP result = grabDataFile();
    osFileP osfile = grabOsFile();

    if( result && osfile &&
        (0!= (osfile->C_file = fopen(name, CopenMode(mode,type))))
        )
    {
        /* ensure I/O unbuffered for security */
        setbuf(osfile->C_file, (char*)0);

        osfile->mode = mode;
        osfile->ref_count = 1;
        osfile->last_ref = result;

        result->mode = mode;
        result->type = OSFILE;
        result->u.osfile = osfile;
        return result;
    }
    releaseDataFile(result);
    releaseOsFile(osfile);
    return NULL;
}

/* Note - replacement file must vanish if closed
* before replaceWith is called, so tempfile is simplest mechanism  */
DataFileP vf_toReplace(DataFileP old)
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


void vf_close(DataFileP file)
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

    case FREE:
        /*bug_check("Double-freeing a datafile\n");
         *                  break;*/ return;

    default:
        break;
    }
    releaseDataFile(file);
}

/* implement replacement by overwrite and renaming; assumes free list
* behaves suitable stck like in the context of vf_close() followed
* by vf_copyref() */
void vf_replaceWith(DataFileP oldFile, DataFileP newFile)
{
    byte buffer[1024];
    DataFileP temp;
    long length;

    /* copy contents of new to overwrite old*/
    vf_setpos(newFile, 0);
    vf_setpos(oldFile, 0);
    while((length = vf_read(buffer, 1024, newFile)) > 0)
        vf_write(buffer, length, oldFile);

    vf_close(newFile);    /* destroy temporary file, putting handle on free-stack*/

    temp = vf_copyRef(oldFile);
    assert(temp == newFile);    /* should just have popped it off the stack */

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

long vf_readline(char * buffer, long buffer_size, DataFileP file)
{
    long length;
    DataFileP part;
    long result;
#ifdef THINK_C
    long old_pos;
    char * end;
    char * next;
#endif

    if(!readable(file)) return WRONGMODE;
    if(file->length >= 0 && file->pos >= file->length) return ENDOFFILE;
    switch(file->type)
    {
    case SLICE:
#ifdef THINK_C
        length = file->length - file->pos;
#else
        /* if we're using fgets, we need to allow 1 for the null that
         * will be added to the end of the string and is decremented
         * from the length of the available buffer for input!  This is
         * a long-standing fencepost bug in this routine. */
        length = 1 + file->length - file->pos;
#endif
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
        {        /* Read is restricted to 'most' part */
            vf_setpos(file->u.compound.most, file->pos);
            result = vf_readline(buffer, buffer_size, file->u.compound.most);
        }
        else if(file->pos >= file->u.compound.split)
        {        /* read is restricted to the 'last' part */
            vf_setpos(file->u.compound.last, file->pos - file->u.compound.split);
            result = vf_readline(buffer, buffer_size, file->u.compound.last);
        }
        else
        {        /* read involves both parts */
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
#ifdef THINK_C
        /* Works for Mac only become it is entirely in terms of vf_read */
        /* The Mac version of 'fgets' only works on files opened as 'text'            */
        /* unfortunately it does spurious \n<->\r conversions on all bytes  of        */
        /* text files.  Hence we have to use binary files and fread/fwrite throughout */
        /* What follows will work but it would be much more efficient to maintain a   */
        /* buffer and only call down to vf_read when it was exhausted.                */
        old_pos = file->pos;
        length = vf_read(buffer, buffer_size - 1, file);
        if(length < 0) return length;
        buffer[length] = '\0';        /* add terminator for safety */
        if(length == 0) return -1;        /* end of file */
        if(end = strpbrk(buffer, TERMINATORS))        /* Look for first end-of-line */
        {
            length = end - buffer;            /* calculate characters before e-o-l */
            next = end + 1;
            while(strchr(TERMINATORS, *next) && *next != *end) next++;
            file->pos = old_pos + (next - buffer);            /* advance pointer to beyond e-o-l */
        }
        else
        {        /* no e-o-l => e-o-f or inadequate buffer */
            length = strlen(buffer);
            file->pos = old_pos + length;
        }
        file->pos_changed = TRUE;        /* remember we have to reposition for next read */
        break;
#endif
#ifdef PC_SINGLE_BYTE
        setpos(file);
        if(!fgets(buffer, (size_t)buffer_size, file->u.osfile->C_file))
            return OSERROR;
        length = strlen(buffer);

        /* Can't simply say "file->pos += length;" because the action of
         * fgets() is to collapse the \n\r of a DOS file opened in ASCII
         * to simply \n, so length <= offset change.  So we consult the
         * function that gives us the true gen! Like this we should be
         * general for all stdio using systems. */
        file->pos = ftell(file->u.osfile->C_file);
        while(iscntrl(buffer[(size_t)(length-1)]))
        {
            length--;            /* strip e-o-l characters */
            if(length <= 0) break;
        }
        break;
#endif
        {
            int eol = 0;
            boolean eof = FALSE;
            boolean takeback = FALSE;
            /* set to read position  */

            length = 0;
            setpos(file);

            /* note that as tmpfile() operates in binary mode, we have to do this by hand
             *                   rather than relying on fgetws() */

            while(length < buffer_size-MB_CUR_MAX)
            {
                char * base = buffer + (size_t) length;
                long cbase = length;
                long start = ftell(file->u.osfile->C_file);

                /* keep reading until we have a character; or have read a 
                 * maximum number of bytes for a character.  This does
                 * not handle broken characters very well. */
                do
                {
                    result = fread(buffer+(size_t)length, 1, (size_t)1, file->u.osfile->C_file);
                    if(0==result) 
                    {
                        eof = TRUE;
                        break;
                    }
                    ++length;
                    if(mblen(base, (size_t)(length-cbase)) < 0) break;
                }
                while(length-cbase < MB_CUR_MAX);

                /* broken character data detected - discard 1 byte, reset and try again */
                if(length-cbase == MB_CUR_MAX && mblen(base, (size_t)(length-cbase)) < 0)
                {
                    length = cbase;
                    fseek(file->u.osfile->C_file, start+1, SEEK_SET);
                    continue; 
                }

                /* nothing else to do, assuming no broken characters */
                if(eof) break;

                /* this assumes multibytes can't be EOL; there is a Unicode line separator character,
                 *                                 // however, which this neglects */
                if(length > cbase + 1) continue;

                /* assume \n, \r or \r\n as possible line breaks as above */
                switch(*base)
                {
                case '\n':
                    --length;                    /* discard */
                    ++eol;
                    break;
                case '\r':
                    --length;                    /* discard */
                    ++eol;
                    result = fread(buffer+(size_t)length, 1, (size_t)1, file->u.osfile->C_file);
                    if('\n' == buffer[(size_t)length]) ++eol;
                    else if(result !=0) takeback = TRUE;
                    break;
                default:
                    {
                        ;
                    }                    /* nothing */
                }
                if(eol) break;
            }
            /* add trailing null */
            buffer[(size_t)length] = 0;

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


    default:
        return UNIMPLEMENTED;
    }
    buffer[(size_t)length] = '\0';
    return length;
}


/*
 * A line of text may well contain multi-byte characters; this routine
 * returns the byte length until the ternumating NUL *character*.
 * Because of how we're handling standard control characters for EOL,
 * we assume we're not running with Unicode as our standard encoding,
 * and assume that no valid multi-byte character starts with a zero byte.
 */
static int mbStrlen(const char * buffer)
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
        CFile = file->u.osfile->C_file;
        if(fwrite(buffer, 1, (size_t)length, CFile) != (size_t)length)
            return OSERROR;
        if(eol)
        {
            term_len = strlen(file->lineEnd);
            /* check - does this expand \n to \n\r on DOS?  I think so. */
            if(fwrite(file->lineEnd, 1, (size_t)term_len, CFile) != (size_t) term_len)
                return OSERROR;
        }
        file->pos += term_len + length;
        if(file->pos > file->length) file->length = file->pos;
        return term_len + length;

    case STATIC:
        return UNIMPLEMENTED;
    default:
        return ILLEGAL;
    }
}

long vf_writeline_xt(char * buffer, DataFileP file)
{
    return vfWriteLine(buffer, file, FALSE);
}
long vf_writeline(char * buffer, DataFileP file)
{
    return vfWriteLine(buffer, file, TRUE);
}


void vf_CCmode(CCmode mode, DataFileP file)
{
    file->lineEnd = lineEnds[mode];
}


long vf_read(void * buffer, long buffer_size, DataFileP file)
{
    FILE * CFile;
    DataFileP part;
    long result;
    long length;

    if(!readable(file)) return WRONGMODE;
    switch(file->type)
    {
    case OSFILE:
        setpos(file);
        CFile = file->u.osfile->C_file;
        result = fread(buffer, 1, (size_t)buffer_size, CFile);
        if(result >= 0)
        {
            file->pos += result;
            return result;
        }
        else
            return OSERROR;

    case SLICE:
        length = min(buffer_size, file->length - file->pos);
        if(length <= 0) return -1;
        part = file->u.slice.whole;
        if(file->pos_changed)
        {
            part->pos = file->pos + file->u.slice.offset;
            part->pos_changed = TRUE;
        }
        result = vf_read(buffer, length, part);
        file->pos = part->pos - file->u.slice.offset;
        return result;

    case COMPOUND:
        if(file->pos + buffer_size <= file->u.compound.split)
        {        /* Read is restricted to 'most' part */
            vf_setpos(file->u.compound.most, file->pos);
            result = vf_read(buffer, buffer_size, file->u.compound.most);
        }
        else if(file->pos >= file->u.compound.split)
        {        /* read is restricted to the 'last' part */
            vf_setpos(file->u.compound.last, file->pos - file->u.compound.split);
            result = vf_read(buffer, buffer_size, file->u.compound.last);
        }
        else
        {        /* read involves both parts */
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


long vf_write(void * buffer, long buffer_size, DataFileP file)
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
             *                  Cpos = file->pos;*/
            if(file->pos > file->length) file->length = file->pos;
            return buffer_size;
        }
        else
            return OSERROR;

    default:
        return ILLEGAL;
    }
}


byte * vf_buffer(long * size, DataFileP file)
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
                *size = fread(file->userBuffer, 1, (size_t)*size, CFile);
            if(*size > 0) return file->userBuffer;            /* only successful return */
            free(file->userBuffer);
        }
        return NULL;

    default:
        return NULL;
    }
}


long vf_release(long length, DataFileP file)
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


boolean vf_movepos(DataFileP file, long offset)
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


boolean vf_setpos(DataFileP file, long offset)
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


long vf_where(DataFileP file)
{
    return file->pos;
}


long vf_length(DataFileP file)
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

            /* say that the logical position is no longer at the OS position */
            file->pos_changed = TRUE;
            if(fseek(Cfile, 0 , SEEK_END)) return OSERROR;
            file->length = ftell(Cfile);
            if(file->length < 0) return OSERROR;
            break;

        case COMPOUND:
            file->length = file->u.compound.split + vf_length(file->u.compound.last);
            break;

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
DataFileP vf_tempfile(long expected_size)
{
    DataFileP result = grabDataFile();
    osFileP osfile = grabOsFile();

    if( result && osfile &&
        (0!=(osfile->C_file = tmpfile()))
        )
    {
        /* ensure I/O unbuffered for security */
        setbuf(osfile->C_file, (char*)0);

        osfile->mode = WRITEREADWIPE;
        osfile->ref_count = 1;
        osfile->last_ref = result;

        result->mode = WRITEREADWIPE;
        result->type = OSFILE;
        result->length = 0;
        result->lineEnd = lineEnds[LOCAL];
        result->u.osfile = osfile;
        return result;
    }
    releaseDataFile(result);
    releaseOsFile(osfile);
    return NULL;
}
#ifdef __BORLANDC__
#pragma warn .par
#endif

#ifdef __BORLANDC__
#pragma warn -par
#endif
/* stub unused variant "file" as local buffer */
DataFileP vf_staticfile(byte * buffer, long length)
{
    return NULL;
}
#ifdef __BORLANDC__
#pragma warn .par
#endif


DataFileP vf_copyRef(DataFileP file)
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


DataFileP vf_part(DataFileP file, long offset, long length)
{
    DataFileP result = grabDataFile();

    if(!result) return FALSE;
    memcpy(result, file, sizeof(DataFile));
    switch(file->type)
    {
    case OSFILE:
        result->type = SLICE;
        result->pos = 0;
        result->pos_changed = FALSE;
        result->length = length;
        result->lineEnd = file->lineEnd;
        result->userBuffer = NULL;
        result->u.slice.whole = file;
        result->u.slice.offset = offset;
        return result;

    default:
        releaseDataFile(result);
        return NULL;
    }
}


DataFileP vf_concat(DataFileP file1, DataFileP file2)
{
    DataFileP result = grabDataFile();

    if(result)
    {
        result->type = COMPOUND;
        result->pos_changed = TRUE;
        result->u.compound.most = file1;
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

/* end of file port_io.c */


