/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.  See licences.c                                *
 *                                                                         *
 ***************************************************************************/
/*   callback.h
**
**   This file includes a number of machine dependent (or application dependent) 
**   callback functions that main encryption/decryption engine requires.  Typically 
**   they are called where the cypher-engine requires important feedback.
**
** DLL support Mr. Tines 14-Sep-1997                  
**  Namespace CTClib Mr. Tines 5-5-98
*/

#ifndef _callback_h
#define _callback_h
#include "abstract.h"

#ifndef CTCCBK_DLL
#define CTCCBK_DLL
#endif
#ifndef CTCFYI_DLL
#define CTCFYI_DLL
#endif

#ifdef __cplusplus
NAMESPACE_CTCLIB
extern "C" {
#endif
    boolean CTCCBK_DLL completeKeys(pubkey ** keys);

    /* continue_action is actually declared in abstract.h */
    /*typedef enum { CB_CONTINUE = 1, CB_SKIP = 2, CB_ABORT = 4} continue_action;*/
    typedef enum { 
        CB_CRASH = 1, /* program should exit (e.g. assert failure) */
        CB_FATAL = 2, /* Operation should be aborted (e.g. out of memory) */
        CB_ERROR = 3, /* Serious but continuable error (e.g. bad signature) */
        CB_WARNING=4, /* Warning   (e.g. No public key to check signature) */
        CB_INFO = 5, /* Nothing wrong; merely for information (e.g. good signature)*/
        CB_STATUS =6 /* Status; purely transitory information 
                        (e.g. currently checking signature)  */
    }
    cb_severity;
    typedef enum { 
        CB_DECRYPTION = 1, 
        CB_ENCRYPTION = 2, 
        CB_SIGNING = 3, 
        CB_VERIFYING = 4,
        CB_KEYGEN = 5,
        CB_KEYMAN = 6,
        CB_READING_RING = 7, /* reading key-ring */
        CB_UNKNOWN = 8
    }
    cb_context;

    /*  Defines range of error codes, there is an enumeration type for each. */
    /*           Description    Enum type */
    typedef enum { 
        CB_CTC_ENGINE = 1, /* control engine   ctc_codes */
        CB_ARMOUR = 2, /* ASCII-Armour module  armour_return */
        CB_COMPAND = 3, /* Compression/Decompression */
        CB_MSG_DIGEST = 4, /* Message Digest   */
        CB_BULK_CYPHER = 5, /* Bulk (symmetric) cypher */
        CB_PKE = 6, /* Public key cypher  */
        CB_PK_MANAGE = 7, /* PKE key management  key_man_conds */
        CB_CTB_IO = 8, /* CTB file i/o    keyio_error */
        CB_RANDOM = 9, /* Random number generator */
        CB_FILING = 10 /* Filing system   */
    }
    cb_module;
    typedef enum { 
        CB_BAD_SIGNATURE = 1     }
    cb_engine_error;

    typedef struct 
        {
        short severity;
        short module;
        short code;
        short context;
        char * text;
        pubkey * pub_key;
    }
    cb_condition;

    typedef struct
        {
        pubkey * signatory;
        boolean valid_sig;
        seckey * addressee; /* secret key used to decrypt */
        uint32_t timestamp;
        char typeByte;
        char fileName[256];
    }
    cb_details;

    /* cb_purpose is used in cb_filedesc and is used to specify for 
        ** what purpose the CTC engine is requiring an open file. */
    typedef enum
        {
        SIGNEDFILE, /* the file corresponding to a signature */
        SPLITKEY, /* A file to hold a conventional key when revealing
                    ** the content of a single message */
        CYPHERTEXTFILE /* the file corresponding to a conventional key */
    }
    cb_purpose;


    /* cb_enumeration is an enumeration of enum types.  It is used in
        ** obtaining printable values associated with enumeration values. */
    typedef enum
        {
        CB_ENUMERATION, /* Self-reference for completeness */
        CB_MODULE,
        CB_CONTEXT,
        CB_SEVERITY,
        CB_PURPOSE,
        CTBVERSION, /* not strictly an enum; merely a set of #defines */
        SKSTAT,
        CTBTYPE,
        SIGCLASS,
        SIGSUBPKTS,
        PKEALGOR,
        MDALGOR,
        CEALGOR,
        COMPANDALGOR,
        SIGSTAT
    }
    cb_enumeration;


    typedef struct
        {
        fileType file_type;
        accessMode mode;
        cb_purpose purpose; 
    }
    cb_filedesc;

    /* cb_need_key:  
        **    The engine requires an internalised secret key.  It provides a list 
        **    of possible keys.  Typically there is only one key in the list.   
        **    The routine should return the offset to the key internalised if successful,
        **    or a negative number if unsuccessful    */
    int CTCCBK_DLL cb_need_key(seckey * keys[], int Nkeys);

    /* cb_exception:
        **   The engine reports an exception.  The calling program has the option of 
        **   (1) continuing (ignoring the error), (2) skipping (the operation in question) but 
        **    coutinuing with further processing, or (3) aborting.   */
    continue_action CTCFYI_DLL cb_exception(cb_condition * condition);

    /* cb_information:
        **    Similar to cb_exception, except that the user is merely informed and the routine
        **    should ideally return immediately.  This is used where there is no choice.
        **    However in the case of very serious errors (especially CB_CRASH) where the program
        **    exiting with cease the display of the message implementors should consider awaiting
        **    user acknowledgement before returning. */
    void CTCFYI_DLL cb_information(cb_condition * condition);


    /* cb_result_file:
        **   The engine has a final output file.  It is being returned to the calling 
        **    program for display or storage.   A zero file pointer means that no
        **    output file is produced (case of detached signature) */
    void CTCCBK_DLL cb_result_file(DataFileP results, cb_details * details);

    /* cb_convKey:
        **   The message being examined has no public key encrypted parts; it is
        **   presumed conventionally encrypted, and a request is made for the
        **   (single) algorithm and key from hashed passphrase */
    boolean CTCCBK_DLL cb_convKey(cv_details *details, void **key, size_t *keylen);

    /* cb_getFile:
        **   Requests the application to provide an open file.  The details of the file
        **  required are in filedesc.  The application may refuse to provide such a file
        **  by returning a NULL.  However this will be interpreted as the operation being
        **  abandoned.   It is CTC's responsibility to close the file. */
    DataFileP CTCCBK_DLL cb_getFile(cb_filedesc * filedesc);

    /* enumName
        ** returns (as a static C string) the short name (if full is FALSE) or longer description
        ** (if full is TRUE) of the 'value' in the 'enumeration' enumeration type.  
        ** It returns NULL if 'enumeration' is not valid.
        ** It returns an appropriate "unknown" text if 'value' is not recognised.
        */
    char * CTCCBK_DLL enumName(cb_enumeration enumeration, int value, boolean full);

#ifdef __cplusplus
}
END_NAMESPACE
#endif
#endif
