/* splay.c
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1997
**  All rights reserved.  For full licence details see file licences.c
**
**  Compression via splay tree using Markov processes.  Algorithm coded from
**  "Applications of Splay Trees to Data Compression", D.W. Jones, Comm of ACM
**  Aug 1988 pp 996-1007
**
*/
#include "splay.h"
#include "port_io.h"
#include "utils.h"
#include "usrbreak.h"
#include <string.h>

/* A number of constant values */
#define BYTEMAX  256 /* number of distinct values input */
#define OUTOFBAND 256 /* a value that is not a byte */
#define STATEMAX  255 /* maximum number of states - actually 256 will also work*/
#define DOUBLEMAX 513 /* twice BYTEMAX, plus 1 */
#define STATES 32     /* a good enough value for text and image */
#define BITSPERBYTE 8
#define BUFFERLEN 1024
#define BUFFERBITS BUFFERLEN*BITSPERBYTE

/* static arrays for speed */
static uint16_t *sinister[STATEMAX];
static uint16_t *dexter[STATEMAX];
static uint16_t *trunk[STATEMAX];
static boolean stack[BYTEMAX+1];
static byte *buffer;
static byte state;
static byte statenumber;
static const byte bits[BITSPERBYTE] =
{
    0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01};
static uint16_t bitnumber;
static short bufferbytes;
static long bufferbase;
static boolean catastrophe;

/* At some point we can revisit this and make it less nasty */
#define OUT_OF_MEMORY bug_check("Out of memory in splay tree set-up");

/*-----------------------------------------------------------*/
/* Housekeeping routines */

static void initSplay(int states)
{
    int i,j;

    catastrophe = (boolean)FALSE;

    statenumber = (byte) states;
    if(states < 1 || states > STATEMAX) statenumber = STATES;

    for(i=0; i<statenumber; i++)
    {
        sinister[i] = qcalloc((size_t) BYTEMAX, sizeof(uint16_t));
        if(!sinister[i]) OUT_OF_MEMORY
            dexter[i] = qcalloc((size_t) BYTEMAX, sizeof(uint16_t));
        if(!dexter[i]) OUT_OF_MEMORY
            trunk[i] = qcalloc((size_t) DOUBLEMAX, sizeof(uint16_t));
        if(!trunk[i]) OUT_OF_MEMORY
            buffer = zmalloc((size_t)BUFFERLEN);
        if(!buffer) OUT_OF_MEMORY
    }
    for(i=0; i<DOUBLEMAX; i++)
    {
        for(j=0; j<statenumber; j++) trunk[j][i]=(uint16_t)((i-1)/2);
    }
    for(i=0; i<BYTEMAX; i++)
    {
        for(j=0; j<statenumber; j++)
            dexter[j][i] = (uint16_t) ((sinister[j][i] = (uint16_t)(2*i+1)) + 1 );
    }
    bufferbytes = bitnumber = state = 0;
    memset(stack, state, (size_t)(BYTEMAX+1));
}

static void endSplay(void) /* Zap any storage allocated */
{
    int i;

    for(i=0; i<statenumber; i++)
    {
        zfree((void**)&sinister[i], (size_t) (BYTEMAX*sizeof(uint16_t)) );
        zfree((void**)&dexter[i], (size_t) (BYTEMAX*sizeof(uint16_t)) );
        zfree((void**)&trunk[i], (size_t) (DOUBLEMAX*sizeof(uint16_t)) );
    }
    zfree((void**)&buffer, (size_t)BUFFERLEN);
    state = statenumber = 0;
}

/*-----------------------------------------------------------*/
/* local utility routines*/

static void splayCore(uint16_t byteval) /* 0-255 byte value or OUTOFBAND to end */
{
    register uint16_t *sin, *dex, *core, a,b,c,d;

    sin = sinister[state];
    dex = dexter[state];
    core = trunk[state];

    a = (uint16_t)(byteval+BYTEMAX);

    do{ /* pair rotate down to the base of the tree */
        c=core[a];
        if(c)
        {
            d=core[c];
            b=sin[d];
            if(b == c)
            {
                b = dex[d];
                dex[d] = a;
            }
            else sin[d] = a;

            if(a == sin[c]) sin[c] = b;
            else dex[c] = b;

            core[a] = d;
            core[b] = c;

            a = d;
        }
        else a = c;
    }
    while(a);

    state = (byte)(byteval % statenumber);
}

static void pushBit(boolean bit, DataFileP outfile)
{
    byte bitindex = (byte)(bitnumber % BITSPERBYTE);
    if(bit) buffer[bitnumber/BITSPERBYTE] |= bits[bitindex];
    ++bitnumber;
    if(bitnumber >= BUFFERBITS)
    {
        vf_write(buffer, BUFFERLEN, outfile);
        memset(buffer, 0, BUFFERLEN);
        bitnumber = 0;
    }
}

static void flushBuffer(DataFileP outfile)
{
    if(bitnumber > 0)
    {
        while(bitnumber % BITSPERBYTE)
        { /* fill last byte with zeroes */
            pushBit(FALSE, outfile);
        }
        vf_write(buffer, bitnumber/BITSPERBYTE, outfile);
    }
}

static void downSplay(uint16_t byteval, DataFileP outfile)
{
    uint16_t *core = trunk[state];
    uint16_t *dex = dexter[state];
    uint16_t index = 0;
    uint16_t a= (uint16_t)(byteval+BYTEMAX);

    do { /* record info for this value on the stack */
        stack[index] = (boolean)(dex[core[a]]==a);
        ++index;
        a = core[a];
    }
    while(a);

    do { /* and write it */
        pushBit(stack[--index], outfile);
    }
    while(index);

    splayCore(byteval); /* step state on one */
}

static boolean pullBit(DataFileP infile)
{
    byte bitindex;
    if(bitnumber >= (bufferbytes*BITSPERBYTE))
    {
        bufferbase = vf_where(infile); /* to allow backtracking */
        bufferbytes = (short) vf_read(buffer, BUFFERLEN, infile);
        if(bufferbytes <= 0) catastrophe=(boolean)TRUE;
        bitnumber=0;
    }

    bitindex = (byte)(bitnumber % BITSPERBYTE);
    /* macho C - index with ++ to avoid save, increment and resturn */
    return (boolean) (buffer[(bitnumber++)/BITSPERBYTE] & bits[bitindex]);
}

static uint16_t upSplay(DataFileP infile)
{
    uint16_t *dex = dexter[state];
    uint16_t *sin = sinister[state];
    uint16_t a=0;

    do {
        if(pullBit(infile)) a = dex[a];
        else a = sin[a];
        if(catastrophe) return (uint16_t)(BYTEMAX+1);
    }
    while(a < BYTEMAX);

    a -= (uint16_t) BYTEMAX;
    splayCore(a);
    return a;
}

/*-----------------------------------------------------------*/
/* external interface routines */

void compressSplay(int states, DataFileP input, DataFileP output)
{
    byte raw[BUFFERLEN];
    uint16_t index, top;

    initSplay(states);

    vf_write(&statenumber, 1, output); /*record states used */

    while((top = (uint16_t) vf_read(raw, BUFFERLEN, input)) > 0)
    {
        for(index = 0; index < top; index++)
            downSplay( (uint16_t)raw[index], output );
    }
    downSplay( (uint16_t)OUTOFBAND, output );
    flushBuffer(output);

    endSplay();
}

boolean expandSplay(DataFileP input, DataFileP output)
{
    byte result[BUFFERLEN], states;
    uint16_t index=0, value;

    vf_read(&states, 1, input);
    initSplay((int) states);
    bitnumber = BUFFERBITS+1; /* buffer exhausted */

    while((value=upSplay(input)) < BYTEMAX)
    {
        result[index++] = (byte) value;
        if(index >= BUFFERLEN)
        {
            vf_write(result, BUFFERLEN, output);
            index = 0;
        }
    }
    /* premature EOF - bail out and fail */
    if(catastrophe) return (boolean)FALSE;

    /* have hit stop value - flush output, and rejig input */
    if(index) vf_write(result, index, output);

    while(bitnumber % BITSPERBYTE) ++bitnumber;
    bufferbase += bitnumber/BITSPERBYTE;

    /* push back the data we grabbed too hastily */
    vf_setpos(input, bufferbase);

    endSplay();
    return TRUE;
}
/* End of file splay.c */

