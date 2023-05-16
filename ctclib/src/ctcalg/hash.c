/*
 hash.c - main despatcher for message digest algorithms.
 Each algorithm just has to provide an initialiser

 void <ALG>Init(void **context, size_t *length);

 returning the hash context and its length

 a do-it routine

 void <ALG>Update(void *context, byte *buff, uint32_t len);

 to add a buffer's worth of new data 

 and a closing routine

 void <ALG>Final(void **context, byte *digest, size_t length);

 and constant

 <ALG>HASHSIZE to give the digest size in bytes

 Coded Mr. Tines <tines@windsong.demon.co.uk> Jun '96, all rights reserved.
        For full licence details see file licences.c

*/
#include <string.h>
#include "utils.h"
#include "keyconst.h"
#include "hash.h"
#include "3way.h"
#include "md5.h"
#include "sha.h"
#include "haval.h"
#include "rmd.h"
/* etc. etc.*/

typedef struct
{
    byte md_alg;
    size_t length;
    void *secret;
}
a_MDcontext, *MDcontext;



/* basic operations */
md_context hashInit (byte md_algor_raw)
{
    byte md_algor = md_algor_raw;
    MDcontext context = zmalloc(sizeof(a_MDcontext));
    if(!context) return (void*)0;

    context->md_alg = md_algor;

    if( (MDA_EBP_HAVAL_MIN <= md_algor) && (md_algor <= MDA_EBP_HAVAL_MAX))
    {
        md_algor = MDA_EBP_HAVAL_MIN;
    }
    if( (MDA_HAVAL_MIN <= md_algor) && (md_algor <= MDA_HAVAL_MAX))
    {
        md_algor = MDA_HAVAL_MIN;
    }

    switch (md_algor)
    {
    case MDA_MD5:
        MD5Init((MD5_CTX**)&context->secret, &context->length);
        break;

    case MDA_3WAY:
        if(!(MDA_FLEX_FLAG & md_algor)) break;
        hash3WayInit((Hash3Way**)&context->secret, &context->length);
        break;

    case MDA_SHA:
        if(!(MDA_FLEX_FLAG & md_algor)) break;
        SHAInit((SHA_INFO**)&context->secret, &context->length);
        break;

    case MDA_SHA1:
        if(!(MDA_FLEX_FLAG & md_algor)) break;
    case MDA_PGP5_SHA1:
        SHA1Init((SHA_INFO**)&context->secret, &context->length);
        break;

    case MDA_EBP_HAVAL_MIN:
    case MDA_HAVAL_MIN:
        havalInit ((havalContext **)&context->secret, &context->length,
        havalGetPasses(md_algor_raw),
        havalGetSize(md_algor_raw),
        (boolean) (MDA_EBP_HAVAL_MIN == md_algor));
        break;

    case MDA_PGP5_RIPEM160:
        rmd160Init((RMD160_CONTEXT**)&context->secret, &context->length);
        break;

    default:
        context->md_alg = md_algor;
    }

    if(!context->secret) {
        free (context); 
        context = 0;
    }

    return (void*)context;
}


/* mix in the first count bytes in buf */
void hashUpdate(md_context contextArg, byte *buf, uint32_t count)
{
    MDcontext context = (MDcontext) contextArg;
    byte md_algor = context->md_alg;

    if( (MDA_EBP_HAVAL_MIN <= md_algor) && (md_algor <= MDA_EBP_HAVAL_MAX))
    {
        md_algor = MDA_EBP_HAVAL_MIN;
    }
    if( (MDA_HAVAL_MIN <= md_algor) && (md_algor <= MDA_HAVAL_MAX))
    {
        md_algor = MDA_HAVAL_MIN;
    }

    switch (md_algor)
    {
    case MDA_MD5:
        MD5Update(context->secret, buf, count);
        break;

    case MDA_3WAY:
        hash3WayUpdate(context->secret, buf, count);
        break;

    case MDA_SHA:
    case MDA_PGP5_SHA1:
    case MDA_SHA1:
        SHAUpdate(context->secret, buf, count);
        break;

    case MDA_EBP_HAVAL_MIN:
    case MDA_HAVAL_MIN:
        havalUpdate(context->secret, buf, count);
        break;

    case MDA_PGP5_RIPEM160:
        rmd160Update(context->secret, buf, count);
        break;
    }

}

void hashFinal(md_context *contextArg, byte *digest)
{
    MDcontext context = *contextArg;
    byte md_algor = context->md_alg;

    if( (MDA_EBP_HAVAL_MIN <= md_algor) && (md_algor <= MDA_EBP_HAVAL_MAX))
    {
        md_algor = MDA_EBP_HAVAL_MIN;
    }
    if( (MDA_HAVAL_MIN <= md_algor) && (md_algor <= MDA_HAVAL_MAX))
    {
        md_algor = MDA_HAVAL_MIN;
    }

    switch (md_algor)
    {
    case MDA_MD5:
        MD5Final((MD5_CTX**)&context->secret, digest, context->length);
        break;

    case MDA_3WAY:
        hash3WayFinal((Hash3Way**)&context->secret, digest, context->length);
        break;

    case MDA_SHA:
    case MDA_PGP5_SHA1:
    case MDA_SHA1:
        SHAFinal((SHA_INFO**)&context->secret, digest, context->length);
        break;

    case MDA_EBP_HAVAL_MIN:
    case MDA_HAVAL_MIN:
        havalFinal((havalContext **)&context->secret, digest, context->length);
        break;

    case MDA_PGP5_RIPEM160:
        rmd160Final((RMD160_CONTEXT**)&context->secret, digest, context->length);
        break;
    }

    /* clear sensitive data */
    zfree(contextArg, sizeof(a_MDcontext));

    /* mark unavailable */
    *contextArg = 0;
}

/* interrogation of digest lengths in bytes */
int hashDigest(byte md_algor_raw)
{
    byte md_algor = md_algor_raw;
    if( (MDA_EBP_HAVAL_MIN <= md_algor) && (md_algor <= MDA_EBP_HAVAL_MAX))
    {
        md_algor = MDA_EBP_HAVAL_MIN;
    }
    if( (MDA_HAVAL_MIN <= md_algor) && (md_algor <= MDA_HAVAL_MAX))
    {
        md_algor = MDA_HAVAL_MIN;
    }

    switch(md_algor)
    {
    case MDA_MD5:
        return MD5HASHSIZE;
    case MDA_3WAY:
        return TWAYHASHSIZE;
    case MDA_SHA:
    case MDA_PGP5_SHA1:
    case MDA_SHA1:
        return SHAHASHSIZE;
    case MDA_EBP_HAVAL_MIN:
    case MDA_HAVAL_MIN:
        return havalGetSize(md_algor_raw);
    case MDA_PGP5_RIPEM160:
        return RMD160HASHSIZE;
    default:
        return 0;
    }
}

/* what do we have to play with ? */

boolean hashAlgAvail(byte md_algor_raw)
{
    byte md_algor = md_algor_raw;
    if( (MDA_EBP_HAVAL_MIN <= md_algor) && (md_algor <= MDA_EBP_HAVAL_MAX))
    {
        md_algor = MDA_EBP_HAVAL_MIN;
    }
    if( (MDA_HAVAL_MIN <= md_algor) && (md_algor <= MDA_HAVAL_MAX))
    {
        md_algor = MDA_HAVAL_MIN;
    }
    switch (md_algor)
    {
    case MDA_MD5:
    case MDA_3WAY:
    case MDA_SHA:
    case MDA_PGP5_SHA1:
    case MDA_SHA1:
    case MDA_EBP_HAVAL_MIN:
    case MDA_HAVAL_MIN:
    case MDA_PGP5_RIPEM160:
        return TRUE;
    default:
        return FALSE;
    }
}

boolean hashAlgRecognised(byte md_algor_raw)
{
    return hashAlgAvail(md_algor_raw);
}

/* end of hash.c */

