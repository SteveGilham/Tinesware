/*  usrbreak.h
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
**  Heimdall <heimdall@bifroest.demon.co.uk>  1996
**  All rights reserved.  For full licence details see file licences.c
**
**
**  This module allows an O/S independent way of allowing the user
**  to interrupt long operations.  On non-preemptive O/Ss like MacOS, MS-DOS
**  and Windows 3.x, it should where reasonably possible allow other 
**  applications to run.
** DLL support Mr. Tines 14-Sep-1997                  
**  Namespace CTClib Mr. Tines 5-5-98
**/
#ifndef _userbreak_h
#define _userbreak_h

#ifndef CTCUSR_DLL
#define CTCUSR_DLL
#endif

#ifdef __cplusplus
NAMESPACE_CTCLIB
extern "C" {
#endif

    int CTCUSR_DLL user_break(void); /* returns true if the user has requested operation abort */
    void CTCUSR_DLL bug_check(char *); /* report unrecoverable error */

#ifdef __cplusplus
}
END_NAMESPACE
#endif


#endif

/* end of file usrbreak.h */
