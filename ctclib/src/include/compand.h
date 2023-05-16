/* compand.h
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
**  Heimdall <heimdall@bifroest.demon.co.uk>  1997
**  All rights reserved.  For full licence details see file licences.c
**
** 
** DLL support Mr. Tines 14-Sep-1997                  
**  Namespace CTClib Mr. Tines 5-5-98
**
*/
#ifndef _compand_h
#define _compand_h
#include "port_io.h"

#ifndef CTCCPA_DLL
#define CTCCPA_DLL
#endif

#ifdef __cplusplus
NAMESPACE_CTCLIB
extern "C" {
#endif

    boolean CTCCPA_DLL compandAlgAvail(byte alg);
    boolean CTCCPA_DLL compress(DataFileP input, byte algorithm, DataFileP output);
    boolean CTCCPA_DLL decompress(DataFileP in, DataFileP out, byte algorithm);

#ifdef __cplusplus
}
END_NAMESPACE
#endif

#endif       

/* end of file compand.h */
