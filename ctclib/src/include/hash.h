/*  hash.h
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1996
**  All rights reserved.  For full licence details see file licences.c
** DLL support Mr. Tines 14-Sep-1997                  
**  Namespace CTClib Mr. Tines 5-5-98
*/
#ifndef _hash
#define _hash
#include "abstract.h"

#ifndef CTCALG_DLL
#define CTCALG_DLL
#endif

#ifdef __cplusplus
NAMESPACE_CTCLIB
extern "C" {
#endif

    /* basic operations */
    md_context CTCALG_DLL hashInit (byte md_algor);
    void CTCALG_DLL hashUpdate (md_context context, byte *buf, uint32_t count);
    void CTCALG_DLL hashFinal(md_context *context, byte *digest);


    /* interrogation of output length in bytes */
    int CTCALG_DLL hashDigest(byte md_algor);

    /* what do we have to play with ? */
    boolean CTCALG_DLL hashAlgAvail(byte md_algor);

    /* and what do we know of but haven't implemented? */
    boolean CTCALG_DLL hashAlgRecognised(byte md_algor);
#ifdef __cplusplus
}
END_NAMESPACE
#endif

#endif

/* end of file hash.h */
