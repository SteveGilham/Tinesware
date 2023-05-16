/* licences.h
**
** Copyright (C)  Heimdall <heimdall@bifroest.demon.co.uk> 1997
** All rights reserved.  For full licence details see file licences.c
**  DLL support Mr. Tines 14-Sep-1997
**  Namespace CTClib Mr. Tines 5-5-98
**
*/
#ifndef _licences_h
#define _licences_h
#ifdef __cplusplus
NAMESPACE_CTCLIB
extern "C" {
#endif

#ifndef CTCUTL_DLL
#define CTCUTL_DLL
#endif

    typedef enum {
        GNU_GPL,
        IDEA
    }
    licence_id;

    char * CTCUTL_DLL licence_text(licence_id which);

#ifdef __cplusplus
}
END_NAMESPACE
#endif
#endif

/* end of file licences.h */
