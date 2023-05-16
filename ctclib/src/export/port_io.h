/* port_io.h
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
**  Heimdall <heimdall@bifroest.demon.co.uk>  1996
**  All rights reserved.  For full licence details see file licences.c
** DLL support Mr. Tines 14-Sep-1997                  
**  Namespace CTClib + const-ing of file name in vf_open Mr. Tines 5-5-98
*/
#ifndef _portable_io
#define _portable_io

#include "abstract.h"

#ifdef __cplusplus
NAMESPACE_CTCLIB
extern "C" {
#endif

#ifndef CTCIO_DLL
#define CTCIO_DLL
#endif

    typedef enum { 
        CANONICAL, LOCAL, PC, UNIX, MAC     }
    CCmode;

    typedef enum 
        { 
        ILLEGAL = -1, /* Illegal operation attempted */
        UNIMPLEMENTED = -2,
        ENDOFFILE = -3,
        OSERROR = -4,
        WRONGMODE = -5, /* read on write-only or write on read only */
        TOOMANY = -6, /* too many of something, typically files open */
        NO_MEMORY = -7 /* memory allocation refused  */
    }
    vf_error; 

    /* Whereas these routines will be the normal method of opening and
        ** closing files in many environments they need not be the only method
        ** nor necessarily available at all in some environments    */
    DataFileP CTCIO_DLL vf_open(const char * name, accessMode mode, fileType type);
    DataFileP CTCIO_DLL vf_toReplace(DataFileP old);
    void CTCIO_DLL vf_close(DataFileP file);
    void CTCIO_DLL vf_replaceWith(DataFileP oldFile, DataFileP newFile);

    long CTCIO_DLL vf_readline(char * buffer, long buffer_size, DataFileP file);
    long CTCIO_DLL vf_writeline(char * buffer, DataFileP file);
    long CTCIO_DLL vf_writeline_xt(char * buffer, DataFileP file);
    void CTCIO_DLL vf_CCmode(CCmode mode, DataFileP file);

    long CTCIO_DLL vf_read(void * buffer, long buffer_size, DataFileP file);
    long CTCIO_DLL vf_write(void * buffer, long buffer_size, DataFileP file);

    byte * CTCIO_DLL vf_buffer(long * size, DataFileP file);
    long CTCIO_DLL vf_release(long size, DataFileP file);

    boolean CTCIO_DLL vf_movepos(DataFileP file, long offset);
    boolean CTCIO_DLL vf_setpos(DataFileP file, long offset);
    long CTCIO_DLL vf_where(DataFileP file);
    long CTCIO_DLL vf_length(DataFileP file);

    /* The expected_size argument is a hint to the vf_tempfile routine */
    /* It does not matter if it is wrong however ideally it should be  */
    /* a value that is unlikely to be exceeded, but not great in       */
    /* in excess of the requirement.  Use zero if no idea.             */
    DataFileP CTCIO_DLL vf_tempfile(long expected_size);
    DataFileP CTCIO_DLL vf_staticfile(byte * buffer, long length);

    DataFileP CTCIO_DLL vf_copyRef(DataFileP file);
    DataFileP CTCIO_DLL vf_part(DataFileP file, long offset, long length);

    /* vf_concat assumes responsibility for both arguments  */
    DataFileP CTCIO_DLL vf_concat(DataFileP file1, DataFileP file2);

#ifdef __cplusplus
}
END_NAMESPACE
#endif

#endif

/* end of file port_io.h */
