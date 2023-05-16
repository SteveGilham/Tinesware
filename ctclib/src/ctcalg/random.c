/* random.c - C source code for random number generation - 2 Mar 96
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
**  Heimdall <heimdall@bifroest.demon.co.uk>  1996
**  All rights reserved.  For full licence details see file licences.c
**
** Plug compatible random number generator for PGP separating 
** true entropy collection from hashing it.
**
** Almost completely rewritten, 31-Dec-99 Mr. Tines
**
***********************************************/

#include <assert.h>
#include <string.h>
#include <time.h>
#include "md5.h"
#include "random.h"
#include "rawrand.h"
#include "utils.h"

#define BUFFERBLOCKS 64    /* Multiple of MD5 block size */
#define BKSZ  MD5HASHSIZE
#define BUFFERSIZE BKSZ * BUFFERBLOCKS  /* Up to 8000 bits */
#define HALF (BKSZ/2)

/* Module globals */

static byte hashed[BUFFERSIZE];
static byte md[BKSZ];
static int start = 0; /* first used byte */
static int finish = 0; /* first invalid byte; -1 indicates running pseudorandom */
static boolean initialised = FALSE;
static MD5_CTX MD5context;
static MD5_CTX* pcontext = &MD5context;
static int j = 0;
static uint32_t md_count = 0;
static int buffptr = 0;

/* local routines */
static void rehash(void);
static boolean hash(int blocks);
static void initialise(void);

#define INITIALISE if(!initialised) { initialise(); }

/* Initialise:
** Initialise hash function and feed in the session specific data.
** We should have a good subsequent pseudo random generation even if
** the O/S's random sources have been nobbled.  */
static void initialise(void)
{
    int length = BUFFERSIZE;
    size_t dummy;
    time_t timeNow = time(NULL);
    MD5_CTX *temp;

    MD5Init(&temp, &dummy);
    MD5context = *temp;
    zfree((void**)&temp, dummy);

    getSessionData(hashed, &length);
    MD5Update(pcontext, hashed, (uint32_t)length);
    MD5Update(pcontext, (byte*)&timeNow, sizeof(time_t));
    rehash();
    initialised = TRUE;
}

/* returns truly random byte, or -1 */
short try_randombyte(void)
{
    INITIALISE;
    if(finish > start) /* set only when "true" random available */
        return (short)randombyte();
    else
        return -1;
}

/*
This had been declared short, with the return value cast to short; but since
it only ever returns a byte - no out-of-band value - there seems no reason
for this other than to cause compilation noise on MSVC++ which notes the
clash of unsigned (destination bytes) vs signed return value.  -- Tines 24-Mar-97
*/
byte randombyte(void)
{
    INITIALISE
        /* raw-random case */
    if(finish > 0)
    {
        if(finish > start)
        {
            return hashed[start++];
        }
        rehash();
    }

    /* simple case - exposes the first half of digest */
    if(j < HALF)
    {
        byte out = md[j];
        ++j;
        return out;
    }

    /* shuffle in the half-buffer not exposed */
    MD5Update(pcontext, md+HALF, (uint32_t)HALF);

    /* any more session random */
    {
        static byte refresh[BUFFERSIZE];
        int length = BUFFERSIZE;
        getSessionData(refresh, &length);
        if(length) MD5Update(pcontext, refresh, (uint32_t)length);
    }

    /* more timing */
    {
        time_t timeNow = time(NULL);
        MD5Update(pcontext, (byte*)&timeNow, sizeof(time_t));
    }
    /* more counting */
    MD5Update(pcontext, (byte*)&md_count, (uint32_t)sizeof md_count);
    ++md_count;

    /* next block of state buffer */
    MD5Update(pcontext, hashed+buffptr, (uint32_t)HALF);

    /* get it all into the out buffer  */
    MD5Final(&pcontext, md, (size_t)0);

    /* mix second half back in to state */
    {
        int i = buffptr;
        int k = HALF;
        for(;k<BKSZ;++k,++i)
        {
            if(i >= BUFFERSIZE) i = 0;
            hashed[i] ^= md[k];
        }
        buffptr = i;
    }
    j = 0;
    return randombyte();
}

/* stir up whole state buffer and the digest output buffer*/
static void rehash()
{
    byte * ptr = hashed;
    int count = 0;

    while (count < BUFFERBLOCKS)
    {
        /* hash in a block, replace the block with the digest */
        MD5Update(pcontext, ptr, (uint32_t)BKSZ);
        MD5Update(pcontext, (byte*)&md_count, (uint32_t)sizeof md_count);
        MD5Final(&pcontext, ptr, (size_t)0);
        /* and go for the next */
        ptr += BKSZ;
        ++count;
        ++md_count;
    }
    /* and shuffle up the output buffer */
    MD5Update(pcontext, md, (uint32_t)BKSZ);
    MD5Update(pcontext, (byte*)&md_count, (uint32_t)sizeof md_count);
    MD5Final(&pcontext, md, (size_t)0);
    ++md_count;
    finish = -1;
    start = 0;
}

/* get and mix up the raw random to spread the entropy */
static boolean hash(int blocks)
{
    byte * ptr = hashed + finish;
    int count = 0;
    byte rawData[BKSZ];

    assert(finish >= 0 && finish + blocks * BKSZ <= BUFFERSIZE);

    if(!ensureRawRandom(blocks * BKSZ)) return FALSE;

    while(count < blocks)
    {
        /* Note that MD5 is NOT reinitialised so it will remain decent
                        ** pseudo-random number generator even if the raw random source
                        ** is sabotaged.  */
        getRawRandom(rawData, BKSZ);
        MD5Update(pcontext, rawData, (uint32_t) BKSZ);
        MD5Final(&pcontext, ptr, (size_t)0);
        ptr += BKSZ;
        count++;
    }
    finish += blocks * BKSZ;
    return TRUE;
}

#define BKBITS (8 * BKSZ)
/* Get fresh load of raw random bits into recyclepool for key generation */
short randload(short bitcount)
{
    INITIALISE;
    if(finish < 0) start = finish = 0;
    {
        int ready = (finish - start) * 8;
        int need = bitcount - ready;

        if(need > 0)
        { /* more bytes needed */
            int blocks = (need + BKBITS - 1) /BKBITS;

            if(blocks > BUFFERBLOCKS)
            {
                assert(blocks <= BUFFERBLOCKS);
            }
            if(finish + blocks * BKSZ > BUFFERSIZE)
            {
                /* Not enough room in the buffer */
                memmove(hashed, hashed + start, finish - start);
                finish -= start;
                start = 0;
            }
            if(!hash(blocks)) return -1;
        }
        return (short)((finish - start) * 8);
    }
}

/* flush recycled random bytes */
void randflush(void) { 
    INITIALISE; 
    start = finish = 0; 
}

/* end of file random.c */
