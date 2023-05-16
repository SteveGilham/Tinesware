/* cipher.c
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> April 1996
**  All rights reserved.  For full licence details see file licences.c
**
**  The routines xorbuf() and cfbshift() are taken from the original
**  idea.c file that was included in the PGP2.3 source, and to that extent
**  are derived from orignal C code by the not clearly credited programmer
**  who remarked in that file about adding CFB functions.
*/
/*
   cipher.c - main despatcher for cipher algorithms and modes.
   Modal stuff is done here; each algorithm just has to provide a
   key schedule initialiser

   void init<ALG>(byte *key, int triple, void **keysched, size_t *length);

   where key is 1 or 3 key block lengths,
   returning the keyschedule and length

   a do-it routine

   void ecb<ALG>(void *keysched, int triple, int encrypt, byte *in, byte *out);

   **EXCEPTION** DES variants are coded w/o the encrypt parameter
   to perform a single block encryption with the algorithm from in to out

   a keyschedule destructor

   void destroy<ALG>(void **keysched, size_t length)

   to encapsulate memory allocation and deallocation for the key schecule
   within each algorithm file.

   and constants

   <ALG>KEYSIZE  and <ALG>BLOCKSIZE to give key and block size in bytes

   Blowfish added and IDEA-free case guarded more carefully, Oct '96 - Mr. Tines

   ECB, OFB, CBC, Square added Aug '97

   DES added Oct '97 Heimdall

   DES variants and tidying - Tines 6-DEC-1997

*/

#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (disable: 4244) // VC++ bug with +=,-=
#endif

#include <string.h>

#if defined(THINKC) || defined(SYMANTEC_C)
#define assert(okay) { if(!(okay)) Debugger(); }
#else
#include <assert.h>
#endif

#include "utils.h"
#include "keyconst.h"
#include "cipher.h"
#include "cast5.h"
#include "des.h"
#include "idea.h"
#include "3way.h"
#include "blowfish.h"
#include "tea.h"
#include "square.h"
#include "safer.h"
#include "rijndael.h"
#include "twofish.h"
/* etc. etc.*/

typedef struct
{
    cv_details cipher;
    boolean decryp;
    size_t length;
    void *secret;
    short blocklen;
    short keylen;
    byte iv[MAXBLOCKSIZE];
    byte delay[2*MAXBLOCKSIZE];
    short buffered;
}
a_keysched, *keysched;

/* This function returns true for modes that use encryption primitives to encrypt
**  and decryption primitives to decrypt
** It returns false for modes that use encryption for both */
static boolean modeUsesDecrypt(byte cv_mode)
{
    switch (cv_mode & CEM_MASK)
    {
    case CEM_OFB:
    case CEM_CFB:
        return FALSE;
    default:
        return TRUE;
    }
}

/*
 * xorbuf - change buffer via xor with random mask block
 * Used for Cipher Feedback (CFB) or Cipher Block Chaining
 * (CBC) modes of encryption.
 * Can be applied for any block encryption algorithm,
 * with any block size, such as the DES or the IDEA cipher.
 */
static void xorbuf(register byte *buf, register byte *mask,
register int count)
/* count must be > 0 */
{
    if (count)
        do
            *buf++ ^= *mask++;
    while (--count);
}/* xorbuf */

/* ECB encrypt or decrypt 1 block */
static void ECB1block(keysched context, byte *from, byte *to, int encrypt)
{
    int block = cipherBlock(context->cipher.cv_algor);
    int triple = context->cipher.cv_mode & CEM_TRIPLE_FLAG;
    /* encrypt IV into temp */
    switch (context->cipher.cv_algor & CEA_MASK)
    {
#ifndef NO_IDEA
    case CEA_IDEA:
    case CEA_EBP_IDEA:
    case CEA_IDEAFLEX:
        ecbIDEA(context->secret, triple, encrypt, from, to);
        break;
#endif
    case CEA_3WAY:
        ecb3WAY(context->secret, triple, encrypt, from, to);
        break;
    case CEA_GPG_BLOW16:
    case CEA_BLOW16:
        ecbBlow16(context->secret, triple, encrypt, from, to);
        break;
    case CEA_TEA:
        ecbTEA(context->secret, triple, encrypt, from, to);
        break;
    case CEA_BLOW5:
        ecbBlow5(context->secret, triple, encrypt, from, to);
        break;
    case CEA_SQUARE:
        ecbSquare(context->secret, triple, encrypt, from, to);
        break;
    case CEA_3DESFLEX:
    case CEA_3DES:
        triple = TRUE;
    case CEA_DES:
        ecbDES(context->secret, triple, from, to);
        break;
    case CEA_S3DES:
        ecbS3DES(context->secret, triple, from, to);
        break;
    case CEA_KDDES:
        ecbKDDES(context->secret, triple, encrypt, from, to);
        break;
    case CEA_CAST5FLEX:
    case CEA_CAST5:
        ecbCAST5_128(context->secret, triple, encrypt, from, to);
        break;
    case CEA_BLOW20:
    case CEA_GPG_BLOW20:
        ecbBlow20(context->secret, triple, encrypt, from, to);
        break;
    case CEA_OPGP_SAFERSK128:
    case CEA_EBP_SAFER_MIN:
    case CEA_SAFERSK128FLEX:
        ecbSAFER(context->secret, triple, encrypt, from, to);
        break;

    case CEA_OPGP_AES_128:
    case CEA_AES_128:
    case CEA_OPGP_AES_192:
    case CEA_AES_192:
    case CEA_OPGP_AES_256:
    case CEA_AES_256:
        ecbAES(context->secret, triple, encrypt, from, to);
        break;

    case CEA_OPGP_TWOFISH_256:
    case CEA_TWOFISH_256:
    case CEA_TWOFISH_128:
    case CEA_GPG_TWOFISH_128:
        ecbTwofish(context->secret, triple, encrypt, from, to);

    default:
        memmove(to, from, block);
        break;
    }
}
/**Cypher Feedback (CFB) mode support ****************************************/

/*
 * cfbshift - shift bytes into IV for CFB input
 * Used only for Cipher Feedback (CFB) mode of encryption.
 * Can be applied for any block encryption algorithm with any
 * block size, such as the DES or the IDEA cipher.
 */
static void cfbshift(register byte *iv, register byte *buf,
register int count, int blocksize)
/*  iv is the initialization vector.
 * buf is the buffer pointer.
 * count is the number of bytes to shift in...must be > 0.
 * blocksize is 8 bytes for DES or IDEA ciphers.
 */
{
    int retained;
    if (count)
    {
        retained = blocksize-count; /* number bytes in iv to retain */
        /* left-shift retained bytes of IV over by count bytes to make room */
        while (retained--)
        {
            *iv = *(iv+count);
            iv++;
        }
        /* now copy count bytes from buf to shifted tail of IV */
        do *iv++ = *buf++;
        while (--count);
    }
}/* cfbshift */

/* CFB en/decrypt the data */
static void CFB(keysched context, byte *buf, int count)
{
    int block = cipherBlock(context->cipher.cv_algor);
    int chunksize;
    boolean reverse = (boolean)(context->cipher.cv_mode & CEM_REVERSE_FLAG);
    byte *index;

    if(reverse)
        index = buf+count-block;
    else
        index = buf;

    while ((chunksize = min(count, block)) > 0)
    {
        /* don't step off the start of the array */
        if(reverse && (chunksize < block))
            index = buf;

        /* encrypt IV into temp */
        ECB1block(context, context->iv, context->delay, TRUE);

        if(context->decryp) /* buf is cyphertext - shift to IV*/
            cfbshift(context->iv, index, chunksize, block);

        xorbuf(index, context->delay, chunksize); /* enciphered bytes now output */

        if(!context->decryp)
            cfbshift(context->iv, index, chunksize, block);

        count -= chunksize;
        if(reverse) index -= chunksize;
        else index += chunksize;

    }/* end while */
}
/**Electronic Codebook (ECB) mode support ***********************************/
#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (disable: 4701)
#endif
/* ECB en/decrypt the data block by block */
static int ECB(keysched context, byte *buf, int count)
{
    int block = cipherBlock(context->cipher.cv_algor);
    byte temp[MAXBLOCKSIZE];
    int chunksize;
    boolean reverse = (boolean)(context->cipher.cv_mode & CEM_REVERSE_FLAG);
    byte *index, *outdex;
    int retval = 0;


    /* ensure first two blocks - or everything if < 2 blocks
              is buffered.  Then we can start working.  After an initial
              block or so, data comes in 0 or more large chunks, with
              perhaps a short tail.  Expect to have >1 and <= 2 blocks
              left buffered.
           */
    int totalBytes = context->buffered + count;
    int nblocks = totalBytes/block;
    nblocks = (totalBytes == nblocks * block) ?
    nblocks-2 : nblocks - 1;

    if(context->buffered < 2*block)
    {
        int need = 2*block - context->buffered;

        /* not enough left */
        if(count < need)
        {
            /* we expect this to happen for the first block (key protection) */
            /* or block+2 (encrypted message packet); and perhaps also for a */
            /* *very* short message */
            if(reverse)
            {
                memcpy(context->delay+2*block-
                    (context->buffered + count),
                buf, count);
            }
            else
            {
                memcpy(context->delay+context->buffered,
                buf, count);
            }
            context->buffered += (short) count;
            return 0;
        }

        if(reverse)
        {
            memcpy(context->delay, buf+count-need, need);
            index = buf+count-need-block;
            outdex = buf+block*nblocks;
        }
        else
            {
            memcpy(context->delay+context->buffered,
            buf, need);
            index = buf+need;
            outdex = buf;
        }
        context->buffered = (short)(2*block);
        count -= need;
    }
    else
        {
        if(reverse)
            outdex = buf+block*nblocks;
        else
            outdex = index = buf;
    }

    while (count > 0)
    {
        byte * thisBlock = (reverse) ? context->delay+block : context->delay;

        /* en/decrypt thisBlock into temp */
        ECB1block(context, thisBlock, temp, !context->decryp);

        /* shuffle delayed block along */
        if(reverse) memmove(context->delay+block, context->delay, block);
        else memmove(context->delay, context->delay+block, block);

        /* pull next (partial?) block of data from source array */
        chunksize = min(count, block);
        if(reverse)
        {
            memcpy(context->delay+block-chunksize,
            index,
            chunksize);
            if((index-buf) >= chunksize)index -= chunksize;
            else index = buf;
        }
        else
            {
            memcpy(context->delay+block,
            index,
            chunksize);
            index += chunksize;
        }
        count -= chunksize;
        context->buffered += (short)(chunksize - block);

        /* put data into source array - assumes input buffer holds length
                         rounded up to nearest block w/o problems */
        if(reverse)
        {
            memcpy(outdex-block, temp, block);
            outdex -= block;
        }
        else
            {
            memcpy(outdex, temp, block);
            outdex += block;
        }
        retval += block;
    }/* end while */
    return retval;
}
#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (default: 4701)
#endif

/* Finish ECB by using CTS on the final 1-2 blocks */
static int ECB_CTS(keysched context, byte *buf)
{
    int block = cipherBlock(context->cipher.cv_algor);
    byte temp[MAXBLOCKSIZE];
    boolean reverse = (boolean)(context->cipher.cv_mode & CEM_REVERSE_FLAG);
    int leftover = context->buffered - block;

    assert(context->buffered >= block);

    /* first simple case */
    if(context->buffered == block)
    {
        ECB1block(context, context->delay+(reverse?block:0),
        buf, !context->decryp);
        return block;
    }
    else if (context->buffered == 2*block)
    {
        /* No need to worry about reversal as no diffusion here */
        ECB1block(context, context->delay, buf, !context->decryp);
        ECB1block(context, context->delay+block, buf+block, !context->decryp);
        return 2*block;
    }

    if(!context->decryp)
    {
        /* Encrypt whole block at appropriate end into temp... */
        ECB1block(context, context->delay+(reverse?block:0), temp, TRUE);

        /*
                         Put leading leftover bytes into output, and assemble new block
                         with real leftovers
                        */
        if(reverse)
        {
            memcpy(buf+block, temp+block-leftover, leftover);
            memmove(temp+leftover, temp, block-leftover);
            memcpy(temp, context->delay+block-leftover, leftover);
        }
        else
            {
            memcpy(buf, temp, leftover);
            memmove(temp, temp+leftover, block-leftover);
            memcpy(temp+block-leftover, context->delay+block, leftover);
        }

        /* Encrypt that into place for output */
        ECB1block(context, temp, buf+(reverse?0:leftover), TRUE);
    }
    else /* Decryption phase */
    {
        /* Decrypt hybrid block into temp */
        ECB1block(context, context->delay+(reverse?block-leftover:leftover),
        temp, FALSE);

        /* Put true leftover bytes into output and assemble whole block */
        if(reverse)
        {
            memcpy(buf, temp, leftover);
            memmove(temp, temp+leftover, block-leftover);
            memcpy(temp+block-leftover, context->delay+2*block-leftover, leftover);
        }
        else
            {
            memcpy(buf+block, temp+block-leftover, leftover);
            memmove(temp+leftover, temp, block-leftover);
            memcpy(temp, context->delay, leftover);
        }

        /* And decrypt that into place*/
        ECB1block(context, temp, buf+(reverse?leftover:0), FALSE);
    }
    return context->buffered;
}
/**Output Feedback (OFB) mode support ****************************************/

/* OFB en/decrypt the data */
static void OFB(keysched context, byte *buf, int count)
{
    int block = cipherBlock(context->cipher.cv_algor);
    boolean reverse = (boolean)(context->cipher.cv_mode & CEM_REVERSE_FLAG);
    byte *index;

    /* we start with the implicit zero IV originally defined in CipherInit()
              by zmalloc()ing the context; we encrypt this before XORing.  It would
              have been possible to use the first block of junk present in PGP's
              format as an IV, but this would not be meaningful for a stream cipher,
              and E(E(0))^Random should be random enough to avoid the known plaintext
              being used for an attack. */

    if(reverse)
        index = buf+count-1;
    else
        index = buf;

    while (count)
    {
        if(0==context->buffered)
        {
            /* refresh the stream of bytes; for a real stream cypher, the
                                       block size is notional, but should be > 2 for architectural
                                       reasons */
            ECB1block(context, context->iv, context->delay, TRUE);
            memmove(context->iv, context->delay, block);
            context->buffered=(short) block;
        }

        *index ^= context->iv[(reverse)? context->buffered-1
            : block-context->buffered];
        --(context->buffered);
        if(reverse) -- index; 
        else ++index;
        --count;
    }
}
/**Cipher Block Chaining (CBC) mode support ***********************************/

static void CBC1block(keysched context, byte *from, byte *to, int encrypt)
{
    int block = cipherBlock(context->cipher.cv_algor);
    if(encrypt) /* mask and encrypt the current block: */
    {
        xorbuf(from, context->iv, block);
        ECB1block(context, from, to, TRUE);
        /* update the mask: */
        memcpy(context->iv, to, block);
    }
    else
        {
        byte temp[MAXBLOCKSIZE];
        memcpy(temp, from, block);
        ECB1block(context, from, to, FALSE);
        xorbuf(to, context->iv, block);
        memcpy(context->iv, temp, block);
        memset(temp, 0, block);
    }
}

#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (disable: 4701)
#endif
/* CBC en/decrypt the data block by block */
static int CBC(keysched context, byte *buf, int count)
{
    int block = cipherBlock(context->cipher.cv_algor);
    byte temp[MAXBLOCKSIZE];
    int chunksize;
    boolean reverse = (boolean)(context->cipher.cv_mode & CEM_REVERSE_FLAG);
    byte *index, *outdex;
    int retval = 0;


    /* ensure first two blocks - or everything if < 2 blocks
              is buffered.  Then we can start working.  After an initial
              block or so, data comes in 0 or more large chunks, with
              perhaps a short tail.  Expect to have >1 and <= 2 blocks
              left buffered.
           */
    int totalBytes = context->buffered + count;
    int nblocks = totalBytes/block;
    nblocks = (totalBytes == nblocks * block) ?
    nblocks-2 : nblocks - 1;

    if(context->buffered < 2*block)
    {
        int need = 2*block - context->buffered;

        /* not enough left */
        if(count < need)
        {
            /* we expect this to happen for the first block (key protection) */
            /* or block+2 (encrypted message packet); and perhaps also for a */
            /* *very* short message */
            if(reverse)
            {
                memcpy(context->delay+2*block-
                    (context->buffered + count),
                buf, count);
            }
            else
            {
                memcpy(context->delay+context->buffered,
                buf, count);
            }
            context->buffered += (short) count;
            return 0;
        }

        if(reverse)
        {
            memcpy(context->delay, buf+count-need, need);
            index = buf+count-need-block;
            outdex = buf+block*nblocks;
        }
        else
            {
            memcpy(context->delay+context->buffered,
            buf, need);
            index = buf+need;
            outdex = buf;
        }
        context->buffered = (short)(2*block);
        count -= need;
    }
    else
        {
        if(reverse)
            outdex = buf+block*nblocks;
        else
            outdex = index = buf;
    }

    while (count > 0)
    {
        byte * thisBlock = (reverse) ? context->delay+block : context->delay;

        /* en/decrypt thisBlock into temp */
        CBC1block(context, thisBlock, temp, !context->decryp);

        /* shuffle delayed block along */
        if(reverse) memmove(context->delay+block, context->delay, block);
        else memmove(context->delay, context->delay+block, block);

        /* pull next (partial?) block of data from source array */
        chunksize = min(count, block);
        if(reverse)
        {
            memcpy(context->delay+block-chunksize,
            index,
            chunksize);
            if((index-buf) >= chunksize)index -= chunksize;
            else index = buf;
        }
        else
            {
            memcpy(context->delay+block,
            index,
            chunksize);
            index += chunksize;
        }
        count -= chunksize;
        context->buffered += (short)(chunksize - block);

        /* put data into source array - assumes input buffer holds length
                         rounded up to nearest block w/o problems */
        if(reverse)
        {
            memcpy(outdex-block, temp, block);
            outdex -= block;
        }
        else
            {
            memcpy(outdex, temp, block);
            outdex += block;
        }
        retval += block;
    }/* end while */
    return retval;
}
#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (default: 4701)
#endif

/* Finish CBC by using CTS on the final 1-2 blocks */
static int CBC_CTS(keysched context, byte *buf)
{
    int block = cipherBlock(context->cipher.cv_algor);
    byte temp[MAXBLOCKSIZE];
    boolean reverse = (boolean)(context->cipher.cv_mode & CEM_REVERSE_FLAG);
    int leftover = context->buffered - block;

    assert(context->buffered >= block);

    /* first simple case */
    if(context->buffered == block)
    {
        CBC1block(context, context->delay+(reverse?block:0),
        buf, !context->decryp);
        return block;
    }
    else if (context->buffered == 2*block && ! reverse)
    {
        CBC1block(context, context->delay, buf, !context->decryp);
        CBC1block(context, context->delay+block, buf+block, !context->decryp);
        return 2*block;
    }
    else if (context->buffered == 2*block)
    {
        CBC1block(context, context->delay+block, buf+block, !context->decryp);
        CBC1block(context, context->delay, buf, !context->decryp);
        return 2*block;
    }

    if(!context->decryp)
    {
        /* Encrypt whole block at appropriate end into temp... */
        CBC1block(context, context->delay+(reverse?block:0), temp, TRUE);

        /* Put the first leftover bytes at the end of the buffer */
        if(reverse) memcpy(buf, temp+block-leftover, leftover);
        else memcpy(buf+block, temp, leftover);
        /* perform masking of leftover bytes, using temp */
        xorbuf(temp+(reverse?block-leftover:0),
        context->delay+block-(reverse?leftover:0),
        leftover);

        /* Encrypt that into place for output at start of buffer */
        ECB1block(context, temp, buf+(reverse?leftover:0), TRUE);
    }
    else /* Decryption phase */
    {
        int i;
        /* Decrypt last, hybrid, block into buf */
        ECB1block(context, context->delay+(reverse?block:0),
        buf+(reverse?leftover:0), FALSE);
        /* Put leftover bytes into rest of output */
        if(reverse) memcpy(buf, context->delay+block-leftover, leftover);
        else memcpy(buf+block, context->delay+block, leftover);

        for (i = 0; i < leftover; ++i) {
            /* at this point, buf[i + block]  contains a cipherbyte C,
                                    and buf[i] contains the XOR of the same cipherbyte with
                                    the corresponding plainbyte P... And contrariwise for
                                    the reversed case */
            if(reverse) buf[i+block] ^= (buf[i] ^= buf[i+block]);
            else buf[i] ^= (buf[i + block] ^= buf[i]);

            /* ... now buf[i] contains only the cipherbyte C, and */
            /* buf[i + block] contains the plainbyte P, modulo reverse */
        }

        /* Decrypt and unmask that block and replace it */
        ECB1block(context, buf+(reverse?leftover:0), temp, FALSE);
        memcpy(buf+(reverse?leftover:0), temp, block);
        xorbuf(buf+(reverse?leftover:0), context->iv, block);
    }
    return context->buffered;
}
/*****************************************************************************/
/*
Public interface : basic operations
*/
/*****************************************************************************/
cv_keysched cipherInit (cv_details *cipher, byte *key, boolean decryp)
{
    boolean cipherDecrypt = (boolean) (decryp && modeUsesDecrypt(cipher->cv_mode));
    keysched context = zmalloc(sizeof(a_keysched));
    int triple = cipher->cv_mode & CEM_TRIPLE_FLAG;

    if(!context) return (void*)0;

    context->cipher = *cipher;
    context->decryp = decryp;

    /* per algorithm initialisation */
    switch (cipher->cv_algor & CEA_MASK)
    {
#ifndef NO_IDEA
    case CEA_IDEA:
    case CEA_EBP_IDEA:
    case CEA_IDEAFLEX:
        initIDEA(key, triple, &context->secret, &context->length);
        break;
#endif
    case CEA_3WAY:
        init3WAY(key, triple, &context->secret, &context->length);
        break;

    case CEA_GPG_BLOW16:
    case CEA_BLOW16:
        initBlow16(key, triple, &context->secret, &context->length);
        break;

    case CEA_TEA:
        initTEA(key, triple, &context->secret, &context->length);
        break;

    case CEA_BLOW5:
        initBlow5(key, triple, &context->secret, &context->length);
        break;

    case CEA_SQUARE:
        initSquare(key, triple, &context->secret, &context->length);
        break;

    case CEA_3DESFLEX:
    case CEA_3DES:
        triple = TRUE;
    case CEA_DES:
        initDES(key, triple, cipherDecrypt, &context->secret, &context->length);
        break;

    case CEA_S3DES:
        initS3DES(key, triple, cipherDecrypt, &context->secret, &context->length);
        break;

    case CEA_KDDES:
        initKDDES(key, triple, cipherDecrypt, &context->secret, &context->length);
        break;

    case CEA_CAST5:
    case CEA_CAST5FLEX:
        initCAST5_128(key, triple, &context->secret, &context->length);
        break;

    case CEA_BLOW20:
    case CEA_GPG_BLOW20:
        initBlow20(key, triple, &context->secret, &context->length);
        break;

    case CEA_OPGP_SAFERSK128:
    case CEA_EBP_SAFER_MIN:
    case CEA_SAFERSK128FLEX:
        initSAFER(key, triple, &context->secret,
        &context->length, sk128);
        break;

    case CEA_OPGP_AES_128:
    case CEA_AES_128:
        initAES128(key, triple, &context->secret, &context->length);
        break;
    case CEA_OPGP_AES_192:
    case CEA_AES_192:
        initAES192(key, triple, &context->secret, &context->length);
        break;
    case CEA_OPGP_AES_256:
    case CEA_AES_256:
        initAES256(key, triple, &context->secret, &context->length);
        break;

    case CEA_OPGP_TWOFISH_256:
    case CEA_TWOFISH_256:
        initTwofish256(key, triple, &context->secret, &context->length);
        break;

    case CEA_TWOFISH_128:
    case CEA_GPG_TWOFISH_128:
        initTwofish128(key, triple, &context->secret, &context->length);
        break;

    default:/* No-op as context->secret is already zero */
        break;
    }

    if(!context->secret) {
        free (context); 
        context = 0;
    }

    /* if that worked, per mode initialise */
    if(context->secret)
        switch(cipher->cv_mode & CEM_MASK)
        {
        case CEM_CFB:/* No prep needed for these modes */
        case CEM_ECB:
            break;
        case CEM_OFB:/* Encrypt the IV so that possibility of correlation */
        case CEM_CBC:/* with ciphertext is avoided */
            ECB1block(context, context->delay, context->iv, TRUE);
            break;
        }
    return (void*)context;
}


/* encipher or decipher count bytes in buf */
int cipherDo(cv_keysched contextArg, byte *buf, int count)
{
    keysched context = (keysched) contextArg;
    switch (context->cipher.cv_mode & CEM_MASK)
    {
    case CEM_CFB:
        CFB(context, buf, count); 
        return count;
    case CEM_ECB:
        return ECB(context, buf, count);
    case CEM_OFB:
        OFB(context, buf, count); 
        return count;
    case CEM_CBC:
        return CBC(context, buf, count);

    default:
        break;
    }
    return -1; /* compiler silencing */
}

int cipherEnd(cv_keysched *contextArg, byte * buffer)
{
    keysched context = *contextArg;
    int buffered;

    if(!context) return 0; /* cope with null context */

    /* do any ciphertext stealing required by the mode */
    switch (context->cipher.cv_mode & CEM_MASK)
    {
    case CEM_ECB:
        buffered = ECB_CTS(context, buffer);
        break;
    case CEM_CBC:
        buffered = CBC_CTS(context, buffer);
        break;

        /* modes not requiring CTS*/
    default:
        buffered = 0;
        break;
    }

    /*zap algorithm specific keyschedule data */
    if(context->length)
    {
        /* clear sensitive data */
        switch (context->cipher.cv_algor & CEA_MASK)
        {
#ifndef NO_IDEA
        case CEA_IDEA:
        case CEA_EBP_IDEA:
        case CEA_IDEAFLEX:
            destroyIDEA(&context->secret, context->length);
            break;
#endif
        case CEA_3WAY:
            destroy3WAY(&context->secret, context->length);
            break;

        case CEA_GPG_BLOW16:
        case CEA_BLOW16:
            destroyBlow16(&context->secret, context->length);
            break;

        case CEA_TEA:
            destroyTEA(&context->secret, context->length);
            break;

        case CEA_BLOW5:
            destroyBlow5(&context->secret, context->length);
            break;

        case CEA_SQUARE:
            destroySquare(&context->secret, context->length);
            break;

        case CEA_DES:
        case CEA_3DES:
        case CEA_3DESFLEX:
            destroyDES(&context->secret, context->length);
            break;

        case CEA_S3DES:
            destroyS3DES(&context->secret, context->length);
            break;

        case CEA_KDDES:
            destroyKDDES(&context->secret, context->length);
            break;

        case CEA_CAST5:
        case CEA_CAST5FLEX:
            destroyCAST5_128(&context->secret, context->length);
            break;

        case CEA_BLOW20:
        case CEA_GPG_BLOW20:
            destroyBlow20(&context->secret, context->length);
            break;

        case CEA_OPGP_SAFERSK128:
        case CEA_EBP_SAFER_MIN:
        case CEA_SAFERSK128FLEX:
            destroySAFER(&context->secret, context->length);
            break;

        case CEA_OPGP_AES_128:
        case CEA_AES_128:
        case CEA_OPGP_AES_192:
        case CEA_AES_192:
        case CEA_OPGP_AES_256:
        case CEA_AES_256:
            destroyAES(&context->secret, context->length);

        case CEA_OPGP_TWOFISH_256:
        case CEA_TWOFISH_256:
        case CEA_TWOFISH_128:
        case CEA_GPG_TWOFISH_128:
            destroyTwofish(&context->secret, context->length);

        default:/* No-op as context->secret is already zero */
            break;
        }
    }

    /* repeat at next higher level */
    zfree(contextArg, sizeof(a_keysched));

    /* mark unavailable */
    *contextArg = 0;
    return buffered;
}

/* interrogation of input lengths in bytes */
int cipherBlock(byte cv_algor)
{
    switch(cv_algor & CEA_MASK)
    {
#ifndef NO_IDEA
    case CEA_IDEA:
    case CEA_EBP_IDEA:
    case CEA_IDEAFLEX:
        return IDEABLOCKSIZE;
#endif
    case CEA_3WAY:
        return TWAYBLOCKSIZE;
    case CEA_GPG_BLOW16:
    case CEA_BLOW16:
        return BLOW16BLOCKSIZE;
    case CEA_TEA:
        return TEABLOCKSIZE;
    case CEA_BLOW5:
        return BLOW5BLOCKSIZE;
    case CEA_SQUARE:
        return SQUAREBLOCKSIZE;
    case CEA_DES:
    case CEA_S3DES:
    case CEA_KDDES:
    case CEA_3DESFLEX:
    case CEA_3DES:
        return DESBLOCKSIZE;
    case CEA_CAST5FLEX:
    case CEA_CAST5:
        return CAST5128BLOCKSIZE;
    case CEA_BLOW20:
    case CEA_GPG_BLOW20:
        return BLOW20BLOCKSIZE;
    case CEA_OPGP_SAFERSK128:
    case CEA_EBP_SAFER_MIN:
    case CEA_SAFERSK128FLEX:
        return SAFERBLOCKSIZE;
    case CEA_OPGP_AES_128:
    case CEA_AES_128:
    case CEA_OPGP_AES_192:
    case CEA_AES_192:
    case CEA_OPGP_AES_256:
    case CEA_AES_256:
        return AESBLOCKSIZE;

    case CEA_OPGP_TWOFISH_256:
    case CEA_TWOFISH_256:
    case CEA_TWOFISH_128:
    case CEA_GPG_TWOFISH_128:
        return TWOFISHBLOCKSIZE;
    default:
        return 0;
    }
}
int cipherKey(byte cv_algor)
{
    switch(cv_algor & CEA_MASK)
    {
#ifndef NO_IDEA
    case CEA_IDEA:
    case CEA_EBP_IDEA:
    case CEA_IDEAFLEX:
        return IDEAKEYSIZE;
#endif
    case CEA_3WAY:
        return TWAYKEYSIZE;
    case CEA_GPG_BLOW16:
    case CEA_BLOW16:
        return BLOW16KEYSIZE;
    case CEA_TEA:
        return TEAKEYSIZE;
    case CEA_BLOW5:
        return BLOW5KEYSIZE;
    case CEA_SQUARE:
        return SQUAREKEYSIZE;
    case CEA_DES:
        return DESKEYSIZE;
    case CEA_S3DES:
        return DESKEYSIZE;
    case CEA_KDDES:
        return DESKEYSIZE+KDDESEXTRA;
    case CEA_3DESFLEX:
    case CEA_3DES:
        return 3 * DESKEYSIZE;
    case CEA_CAST5FLEX:
    case CEA_CAST5:
        return CAST5128KEYSIZE;
    case CEA_BLOW20:
    case CEA_GPG_BLOW20:
        return BLOW20KEYSIZE;
    case CEA_OPGP_SAFERSK128:
    case CEA_EBP_SAFER_MIN:
    case CEA_SAFERSK128FLEX:
        return SAFERkeySize(sk128);

    case CEA_OPGP_AES_128:
    case CEA_AES_128:
        return AES128KEYSIZE;

    case CEA_OPGP_AES_192:
    case CEA_AES_192:
        return AES192KEYSIZE;

    case CEA_OPGP_AES_256:
    case CEA_AES_256:
        return AES256KEYSIZE;

    case CEA_OPGP_TWOFISH_256:
    case CEA_TWOFISH_256:
        return TWOFISH256KEYSIZE;

    case CEA_TWOFISH_128:
    case CEA_GPG_TWOFISH_128:
        return TWOFISH128KEYSIZE;

    default:
        return 0;
    }
}

/* what do we have to play with ? */

boolean cipherAlgAvail(byte cv_algor)
{
    switch (cv_algor & CEA_MASK)
    {
    case CEA_IDEA:
    case CEA_EBP_IDEA:
    case CEA_IDEAFLEX:
#ifdef NO_IDEA
        return FALSE;
#else
        return TRUE;
#endif
    case CEA_3WAY:
    case CEA_GPG_BLOW16:
    case CEA_BLOW16:
    case CEA_TEA:
    case CEA_BLOW5:
    case CEA_SQUARE:
    case CEA_DES:
    case CEA_3DES:
    case CEA_3DESFLEX:
    case CEA_S3DES:
    case CEA_KDDES:
    case CEA_CAST5:
    case CEA_CAST5FLEX:
    case CEA_BLOW20:
    case CEA_GPG_BLOW20:
    case CEA_OPGP_SAFERSK128:
    case CEA_EBP_SAFER_MIN:
    case CEA_SAFERSK128FLEX:

    case CEA_OPGP_AES_128:
    case CEA_OPGP_AES_192:
    case CEA_OPGP_AES_256:
    case CEA_OPGP_TWOFISH_256:
    case CEA_GPG_TWOFISH_128:

    case CEA_AES_128:
    case CEA_AES_192:
    case CEA_AES_256:
    case CEA_TWOFISH_256:
    case CEA_TWOFISH_128:

        return TRUE;
    default:
        return FALSE;
    }
}

boolean cipherAlgRecognised(byte cv_algor_raw)
{
    byte cv_algor = cv_algor_raw;
    if(cv_algor > CEA_EBP_SAFER_MIN && cv_algor <= CEA_EBP_SAFER_MAX)
        cv_algor = CEA_EBP_SAFER_MIN;
    switch (cv_algor & CEA_MASK)
    {
        /* recognised, even if not implemented */
    case CEA_IDEA:
    case CEA_EBP_IDEA:
    case CEA_IDEAFLEX:

        /*case CEA_GPG_ROT_N:*/
    case CEA_OPGP_DES_SK:
    case CEA_GPG_GOST:
    case CEA_EBP_SAFER_MIN:

        /*case CEA_ROT_NFLEX:*/
    case CEA_DESSKFLEX:
    case CEA_GOSTFLEX:

        return TRUE;

    default:
        return cipherAlgAvail(cv_algor);
    }
}

boolean cipherModeAvail(byte cv_mode, byte cv_algor)
{
    switch (cv_mode & CEM_MASK)
    {
    case CEM_CFB:
        return TRUE;
        /* unless using a hash, in which case only CFB mode will do */
        /* currently there are none, but use the argument to placate the compiler */
    case CEM_ECB:
        return (boolean) (cv_algor != 0);
    case CEM_OFB:
        return TRUE;
    case CEM_CBC:
        return TRUE;
    default:
        return FALSE;
    }
}

byte flexAlg(byte cv_algor)
{
    uint32_t alg = cv_algor & CEA_MASK;
    switch(alg)
    {
    case CEA_IDEAFLEX:
    case CEA_EBP_IDEA:
    case CEA_IDEA:
        return CEA_IDEAFLEX;
    case CEA_3DESFLEX:
    case CEA_3DES:
        return CEA_3DESFLEX;
    case CEA_CAST5FLEX:
    case CEA_CAST5:
        return CEA_CAST5FLEX;
    case CEA_BLOW20:
    case CEA_GPG_BLOW20:
        return CEA_BLOW20;
    case CEA_OPGP_SAFERSK128:
    case CEA_SAFERSK128FLEX:
        return CEA_SAFERSK128FLEX;
    case CEA_BLOW16:
    case CEA_GPG_BLOW16:
        return CEA_BLOW16;

    case CEA_OPGP_AES_128:
    case CEA_AES_128:
        return CEA_AES_128;

    case CEA_OPGP_AES_192:
    case CEA_AES_192:
        return CEA_AES_192;

    case CEA_OPGP_AES_256:
    case CEA_AES_256:
        return CEA_AES_256;

    case CEA_TWOFISH_256:
    case CEA_OPGP_TWOFISH_256:
        return CEA_TWOFISH_256;

    case CEA_GPG_TWOFISH_128:
    case CEA_TWOFISH_128:
        return CEA_TWOFISH_128;

    default:
        return 0;
    }
}

byte unflexAlg(byte cv_algor)
{
    uint32_t alg = cv_algor & CEA_MASK;
    switch(alg)
    {
    case CEA_IDEA:
    case CEA_EBP_IDEA:
    case CEA_IDEAFLEX:
        return CEA_IDEA;
    case CEA_3DES:
    case CEA_3DESFLEX:
        return CEA_3DES;
    case CEA_CAST5:
    case CEA_CAST5FLEX:
        return CEA_CAST5;
    case CEA_BLOW20:
    case CEA_GPG_BLOW20:
        return CEA_GPG_BLOW20;
    case CEA_OPGP_SAFERSK128:
    case CEA_SAFERSK128FLEX:
        return CEA_OPGP_SAFERSK128;
    case CEA_BLOW16:
    case CEA_GPG_BLOW16:
        return CEA_GPG_BLOW16;

    case CEA_OPGP_AES_128:
    case CEA_AES_128:
        return CEA_OPGP_AES_128;

    case CEA_OPGP_AES_192:
    case CEA_AES_192:
        return CEA_OPGP_AES_192;

    case CEA_OPGP_AES_256:
    case CEA_AES_256:
        return CEA_OPGP_AES_256;

    case CEA_TWOFISH_256:
    case CEA_OPGP_TWOFISH_256:
        return CEA_OPGP_TWOFISH_256;
    default:
        return 0;

    case CEA_GPG_TWOFISH_128:
    case CEA_TWOFISH_128:
        return CEA_GPG_TWOFISH_128;

    }
}

/* end of cipher.c */

