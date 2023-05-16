/* cipher.h
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> April 1996
**  All rights reserved.  For full licence details see file licences.c
*/
#ifndef _cipher
#define _cipher
#include "abstract.h"

/*
Duplication of effort removed 1-Jan-1997 Mr. Tines 
DLL support Mr. Tines 14-Sep-1997       
**  Namespace CTClib Mr. Tines 5-5-98
*/

#ifndef CTCALG_DLL
#define CTCALG_DLL
#endif

#ifdef __cplusplus
NAMESPACE_CTCLIB
extern "C" {
#endif

    /*typedef*/
    struct cv_details_T
        {
        byte cv_algor; /* algorithm */
        byte cv_mode; /* mode of application */
    }/*cv_details*/
    ;

    /* typedef void * cv_keysched; */ /* key schedule structure is private */
    /* end of items defined in abstract.h */

    /* basic operations */
    cv_keysched CTCALG_DLL cipherInit (cv_details *cipher, byte *key, boolean decryp);
    int CTCALG_DLL cipherDo(cv_keysched context, byte *buf, int count);
    int CTCALG_DLL cipherEnd(cv_keysched *context, byte *buf);

    /* interrogation of input lengths in bytes */
    int CTCALG_DLL cipherBlock(byte cv_algor);
    int CTCALG_DLL cipherKey(byte cv_algor);

    /* what do we have to play with ? */

    boolean CTCALG_DLL cipherAlgAvail(byte cv_algor);
    boolean CTCALG_DLL cipherAlgRecognised(byte cv_algor);
    boolean CTCALG_DLL cipherModeAvail(byte cv_mode, byte cv_algor);

    byte CTCALG_DLL flexAlg(byte cv_algor);
    byte CTCALG_DLL unflexAlg(byte cv_algor);

#ifdef __cplusplus
}
END_NAMESPACE
#endif

#endif
/* end of file cipher.h */
