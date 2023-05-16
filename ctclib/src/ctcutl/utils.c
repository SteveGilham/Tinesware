/* utils.c
 **
 **  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 **  Heimdall <heimdall@bifroest.demon.co.uk>  1996
 **  All rights reserved.  For full licence details see file licences.c
 */
#include "utils.h"
#include "usrbreak.h"
#include <string.h>
/*#define MEMCHECK*/
/*#define MEMCHAIN*/
#ifdef MEMCHECK
#define MEMCHAIN
#endif

/* g++ needs this */
#ifdef BIG_ENDIAN
#undef BIG_ENDIAN
#endif

#ifdef LITTLE_ENDIAN
#undef LITTLE_ENDIAN
#endif

/* VC++ .NET doesn't like our punning a pointer into a uint32_t 
   as it is 64-bit aware.  Fortunately this doesn't really affect
   our checking of consistency of blocks of memory */
#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (disable: 4311)
#endif


typedef enum {
    UNKNOWN, BIG_ENDIAN, LITTLE_ENDIAN }
endian_type;

typedef struct memblock_T
{
    uint32_t checkword;
#ifdef MEMCHAIN
    struct memblock_T * next;
    struct memblock_T * prev;
#endif
    uint32_t marked;
    uint32_t size;
    uint32_t startguard;
    uint32_t data[1];
}
memblock;

#ifdef MEMCHAIN
static memblock block0 = {
    0, &block0, &block0 };
#endif

static uint32_t nblocks = 0;
static uint32_t totalspace = 0;

static endian_type endianness = UNKNOWN;

static endian_type determine_endianness(void)
{
    byte buffer[2] = {
        1, 2         };
    uint16_t as_short;

    /* avoid storage alignment issues */
    /* ratehr than as_short = *((short*)buffer);, use memcpy */
    memcpy(&as_short, buffer, 2);

    switch(as_short)
    {
    case 0x0102:
        endianness = BIG_ENDIAN;
        break;

    case 0x0201:
        endianness = LITTLE_ENDIAN;
        break;

    default:
        bug_check("Cannot determine byte order");
        exit(1);

    }
    return endianness;
}

boolean little_endian()
{
    switch(endianness)
    {
    case BIG_ENDIAN:
        return FALSE;

    case UNKNOWN:
        /* first call */
        if(determine_endianness() == BIG_ENDIAN) return FALSE;
        /* deliberate drop through */
    case LITTLE_ENDIAN:
        return TRUE;

    default:
        bug_check("illegal stored endianness value");
    }
    return FALSE;
}

/* convert_byteorder converts between file order (bigendian) and */
/* internal machine order                                        */
void convert_byteorder(byte * buffer, uint16_t length)
{
    switch(endianness)
    {
    case BIG_ENDIAN:
        return;        /* no-op */

    case UNKNOWN:
        /* first call */
        if(determine_endianness() == BIG_ENDIAN) return;
        /* deliberate drop through */
    case LITTLE_ENDIAN:
        {
            byte * start = buffer;
            byte * end = buffer + length - 1;
            byte temp;

            while (start < end)
            {
                temp = *start;
                *start++ = *end;
                *end-- = temp;
            }
        }
        return;

    default:
        bug_check("illegal stored endianness value");
    }
}

/* like memcpy only flips each 32-bit section */
/* out and in may be identical */
void flip32(byte * out, byte * in, size_t nbytes)
{
    int i, count = (int)(nbytes/4);
    byte *from = in, tmp[4], *to = out;

    for(i=0; i<count; ++i)
    {
        tmp[0] = from[3];
        tmp[1] = from[2];
        tmp[2] = from[1];
        tmp[3] = from[0];
        memcpy(to, tmp, 4);
        from += 4;
        to += 4;
    }
}

void byte2hex(char hex[2], byte value)
{
    hex[0] = "0123456789ABCDEF"[value /16];
    hex[1] = "0123456789ABCDEF"[value % 16];
}

#if defined(_MSC_VER) && !defined (__BORLANDC__) 
// BUG in VS .NET result += (unsigned short) buffer[length] triggers this
#pragma warning (disable: 4244) // conversion from int to short/uint16_t
#endif

unsigned short calcchecksum(byte * buffer, unsigned short length)
{
    unsigned short result = 0;

    while(length-- > 0) result += buffer[length];
    return result;
}


void * zmalloc(size_t n)
{
    void * result = qmalloc(n);

    if(result) memset(result, 0, n);
    return result;
}


void zfree (void ** buffer, size_t n)
{
    /* don't deallocate nulls */
    if(!*buffer) return;

    memset(*buffer, 0, n);
    qfree(*buffer);
    *buffer = NULL;
}


#ifdef MEMCHAIN
static boolean blockOverlap(memblock * a, memblock * b)
{
    byte * starta = (byte *)a;
    byte * enda = (byte *)starta + sizeof(memblock) + a->size;    /* A slight underestimate */
    byte * startb = (byte *)b;
    byte * endb = (byte *)startb + sizeof(memblock) + b->size;

    /*
     *  Strictly speaking this is naughty 'C', since pointer comparisons
     *  are only necessarily valid within a contiguous allocation; indeed
     *  with DOS segmented memory, without the (uint32_t)s, if you have
     *  starta = 1957:0DF2 enda = 1957:0E1B
     *  startb = 2F8F:0DCE endb = 2F8F:0DF8
     *  this routine would return TRUE!
     */

    return ((uint32_t)starta < (uint32_t)endb
        && (uint32_t)startb < (uint32_t) enda );
}

static boolean anyOverlap(memblock * block)
{
    memblock * other = block0.next;
    uint32_t left = nblocks;

    while(other != &block0 && left-- > 0)
    {
        if(blockOverlap(block, other))
            return TRUE;
        other = other->next;
    }
    return FALSE;
}
#endif

static boolean blockOK(memblock * block)
{
    uint32_t bytes = block->size;
    uint32_t longs = (bytes + sizeof(uint32_t) - 1) /sizeof(uint32_t);

    return (boolean)(block->checkword == ~(uint32_t)block ||
#ifdef MEMCHAIN
        block->next->prev == block ||
        block->prev->next == block ||
#endif
        block->startguard == 0x55555555L ||
        block->data[(size_t)longs] == 0xAAAAAAAAL);
}

#ifdef MEMCHAIN
static boolean allBlocksOK(void)
{
    memblock * block = block0.next;
    uint32_t left = nblocks;

    while(block != &block0)
    {
        if(0 ==left)
            return FALSE;
        left--;
        if(!blockOK(block))
            return FALSE;
        block = block->next;
    }
    if(left)
        return FALSE;
    else
        return TRUE;
}
#endif


void * qmalloc(size_t n)
{
    size_t longs = (n + sizeof(uint32_t) - 1) /sizeof(uint32_t);
    size_t required = longs * sizeof(uint32_t) + sizeof(memblock);
    memblock * block = (memblock *)malloc(required);

    if(!block)
        return NULL;
#ifdef MEMCHECK
    if(!allBlocksOK())
        return NULL;
#endif
    nblocks++;
    totalspace += (uint32_t) n;
    block->checkword = ~(uint32_t)block;
    block->size = (uint32_t) n;
    block->marked = FALSE;

#ifdef MEMCHAIN
#ifdef MEMCHECK
    if(anyOverlap(block))
        return NULL;
#endif
    block->next = &block0;
    block->prev = block0.prev;
    block0.prev->next = block;
    block0.prev = block;
#endif
    block->startguard = 0x55555555L;
    block->data[longs] = 0xAAAAAAAAL;
#ifdef MEMCHECK
    if(!allBlocksOK())
        return NULL;
#endif
    return block->data;
}

void * qcalloc(size_t m, size_t n) {
    return zmalloc(m * n);
}

void * qrealloc(void * ptr, size_t size)
{
    memblock * block = (memblock*)((byte*)ptr - (sizeof(memblock) - sizeof(uint32_t *)));

    if(!blockOK(block))
    {
        bug_check("bad call to qrealloc");
    }
    else
    {
        size_t longs = (size + sizeof(uint32_t) - 1) /sizeof(uint32_t);
        size_t required = longs * sizeof(uint32_t) + sizeof(memblock);
        memblock * newblock = (memblock *)realloc(block, required);

        if(!newblock)
            return NULL;
#ifdef MEMCHECK
        if(!allBlocksOK())
            return NULL;
#endif
        totalspace += (uint32_t) (size - block->size);
        newblock->checkword = ~(uint32_t)newblock;
#ifdef MEMCHAIN
        newblock->prev->next = newblock;
        newblock->next->prev = newblock;
#endif
        newblock->size = (uint32_t) size;
        newblock->startguard = 0x55555555L;
        newblock->data[longs] = 0xAAAAAAAAL;
#ifdef MEMCHECK
        if(!allBlocksOK())
            return NULL;
#endif
        return newblock->data;
    }
    return NULL;
}

void qmark(void * ptr)
{
    memblock * block = (memblock*)((byte*)ptr - (sizeof(memblock) - sizeof(uint32_t *)));

    block->marked = TRUE;
}

void qcheck(void * ptr)
{
    if(ptr)
    {
        memblock * block = (memblock*)((byte*)ptr - (sizeof(memblock) - sizeof(uint32_t *)));
        if(!block) return;
        if(!blockOK(block)) bug_check("bad block");
    }
}

void qfree(void * ptr)
{
    if(ptr)
    {
        memblock * block = (memblock*)((byte*)ptr - (sizeof(memblock) - sizeof(uint32_t *)));

        if(!block) return;

        if(block->marked)
        {
            block->marked = FALSE;
        }
        if(!blockOK(block))
        {
            bug_check("bad call to qfree");
        }
        else
        {
#ifdef MEMCHECK
            if(!allBlocksOK())
                return;
#endif
            nblocks--;
            totalspace -= block->size;
#ifdef MEMCHAIN
            block->prev->next = block->next;
            block->next->prev = block->prev;
#endif
            memset(block, 0, sizeof(memblock));
            free(block);
#ifdef MEMCHECK
            if(!allBlocksOK())
                return;
#endif
        }
    }
}


time_t datum_time()
/* returns PGP's datum time (1 Jan 1970) in time_t format */
{
#ifndef __BORLANDC__
    struct tm time0;

    time0.tm_sec = 0;
    time0.tm_min = 0;
    time0.tm_hour = 0;
    time0.tm_mday = 1;
    time0.tm_mon = 0;
    time0.tm_year = 70;
    time0.tm_isdst = 0;    /* DST not in effect!*/

    return mktime(&time0);
#else
    /* mktime() of the above gives error 0xFFFFFFFF, rather
     * than the 0x00000000 one would have hoped for; setting
     * time0.tm_sec=23 gives 0x00000017, so this is what we
     * want */
    return (time_t)0;
#endif
}

/* end of file utils.c */
