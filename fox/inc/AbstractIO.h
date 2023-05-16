/***************************************************************************
                               AbstractIO.h
                             -------------------
    copyright            : (C) 2001 by Mr. Tines
    email                : tines@ravnaandtines.com
 ***************************************************************************/

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

/*
* Equivalent interface to CTClib's DataFileP object
*/

#ifndef _AbstractIO
#define _AbstractIO

namespace CTCFox {

class AbstractIOBase {
public:
    enum  CCmode
    {
        CANONICAL, LOCAL, PC, UNIX, MAC
    };

    enum Error{
        ILLEGAL = -1, /* Illegal operation attempted */
        UNIMPLEMENTED = -2,
        ENDOFFILE = -3,
        OSERROR = -4,
        WRONGMODE = -5, /* read on write-only or write on read only */
        TOOMANY = -6, /* too many of something, typically files open */
        NO_MEMORY = -7 /* memory allocation refused  */
    };

    AbstractIOBase ();

    // void CTCIO_DLL vf_close(DataFileP file);
    virtual ~AbstractIOBase();

    //boolean CTCIO_DLL vf_movepos(DataFileP file, long offset);
    virtual bool movePos(long offset)= 0;

    //boolean CTCIO_DLL vf_setpos(DataFileP file, long offset);
    virtual bool setPos(long offset)= 0;

    //long CTCIO_DLL vf_where(DataFileP file);
    virtual long where(void)= 0;

    //long CTCIO_DLL vf_length(DataFileP file);
    virtual long getLength(void)= 0;

    virtual bool writable(void)= 0;
private:
    // no copy or assign
    AbstractIOBase(const AbstractIOBase &);
    AbstractIOBase & operator=(const AbstractIOBase &);
}; // end class AbstractIOBase

class AbstractBinaryIO : public virtual AbstractIOBase {
public:
    AbstractBinaryIO (); // Nothing needed here for this abstract class
    virtual ~AbstractBinaryIO();

    // long CTCIO_DLL vf_read(void * buffer, long buffer_size, DataFileP file);
    virtual long read(void * buffer, long buffer_size)= 0;

    //long CTCIO_DLL vf_write(void * buffer, long buffer_size, DataFileP file);
    virtual long write(const void * buffer, long buffer_size)= 0;
private:
    // no copy or assign
    AbstractBinaryIO(const AbstractBinaryIO &);
    AbstractBinaryIO & operator=(const AbstractBinaryIO &);
}; // end class AbstractBinaryIO

class AbstractTextIO : public virtual AbstractIOBase {
public:
    AbstractTextIO (); // Nothing needed here for this abstract class
    virtual ~AbstractTextIO();

    //long CTCIO_DLL vf_readline(char * buffer, long buffer_size, DataFileP file);
    virtual long readline(char * buffer, long buffer_limit)= 0;

    //long CTCIO_DLL vf_writeline(char * buffer, DataFileP file);
    virtual long writeline(char * buffer)= 0;

    //long CTCIO_DLL vf_writeline_xt(char * buffer, DataFileP file);
    virtual long writelineEx(char * buffer)= 0;

    //void CTCIO_DLL vf_CCmode(CCmode mode, DataFileP file);
    //virtual void setCCmode(CCmode mode)= 0;
private:
    // no copy or assign
    AbstractTextIO(const AbstractTextIO &);
    AbstractTextIO & operator=(const AbstractTextIO &);
}; // end class AbstractTextIO

class AbstractIO : public virtual AbstractTextIO,
                   public virtual AbstractBinaryIO {
public:
    AbstractIO(); // Nothing needed here for this abstract class
    virtual ~AbstractIO();
private:
    // no copy or assign
    AbstractIO(const AbstractIO &);
    AbstractIO & operator=(const AbstractIO &);
}; // end AbstractIO Class

    /* Whereas these routines will be the normal method of opening and
    ** closing files in many environments they need not be the only method
    ** nor necessarily available at all in some environments    */

    //DataFileP CTCIO_DLL vf_open(const char * name, accessMode mode, fileType type);

    //DataFileP CTCIO_DLL vf_toReplace(DataFileP old);
    //never used in CTCjlib/CTCjava

    //void CTCIO_DLL vf_replaceWith(DataFileP oldFile, DataFileP newFile);
    //not very nice

    //byte * CTCIO_DLL vf_buffer(long * size, DataFileP file);
    //long CTCIO_DLL vf_release(long size, DataFileP file);
    //--neither used

    /* The expected_size argument is a hint to the vf_tempfile routine */
    /* It does not matter if it is wrong however ideally it should be  */
    /* a value that is unlikely to be exceeded, but not great in       */
    /* in excess of the requirement.  Use zero if no idea.             */
    //DataFileP CTCIO_DLL vf_tempfile(long expected_size);
    //--This is a specialization

    //DataFileP CTCIO_DLL vf_staticfile(byte * buffer, long length);
    //--not used

    //DataFileP CTCIO_DLL vf_copyRef(DataFileP file);
    //--only used by replaceWith
    //DataFileP CTCIO_DLL vf_part(DataFileP file, long offset, long length);
    //--This is a specialization

    /* vf_concat assumes responsibility for both arguments  */
    //DataFileP CTCIO_DLL vf_concat(DataFileP file1, DataFileP file2);
    //--This is a specialization


} // end namespace
#endif // AbstractIO Header

/* end of file AbstractIO.hpp */
