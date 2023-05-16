/* port_io.cpp : Defines the I/O abstractions for the application.
   Copyright 2002 Mr. Tines <Tines@RavnaAndTines.com>

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU Library General Public License as published by the
Free Software Foundation; either version 2, or (at your option) any
later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.  */

#include "ctcfoxio.h"
#include "port_io.h"
#include "AbstractIO.h"

using CTCFox::AbstractIO;

namespace CTClib {

struct DataFile_T
{
    DataFile_T(AbstractIO * client, bool deleteOnDestruction = true);
    bool clear;
    AbstractIO * file;
    ~DataFile_T();
    bool writable();
};

DataFile_T::DataFile_T(AbstractIO * client, bool deleteOnDestruction) :
    clear(deleteOnDestruction), file(client)
    {}

DataFile_T::~DataFile_T()
{
    if(clear) delete file;
    file = 0;
}

bool DataFile_T::writable()
{
    return file->writable();
}

} // namespace

namespace CTCFox {

    DataFileP createDataFile(AbstractIO * client, bool deleteOnDestruction)
    {
        return new CTClib::DataFile_T(client, deleteOnDestruction);
    }


} // namespace

namespace CTClib {

    //DataFileP vf_open(const char * name, accessMode mode, fileType type);

    /* Note - replacement file must vanish if closed
    * before replaceWith is called, so tempfile is simplest mechanism  */
    /*  The real spec should be
    * DataFileP vf_toReplace(DataFileP old, accessMode mode);
    * Creates a new potentially permanent but as yet unnamed (or
    * temporary named) file, in the same area (same partition/directory)
    * as 'old'.  The fileType of the result is inherited from 'old'.
    * 
    * A file created with vf_toReplace()  should be deleted if it is
    * closed with vf_close(), and will continue to exist with the old
    * file's name if it is closed as second argument to
    * vf_replaceWith()
    */
    DataFileP vf_toReplace(DataFileP old)
    {
        if(old->writable()) return vf_tempfile(vf_length(old));
        return (DataFileP)0;
    }


    void vf_close(DataFileP file)
    {
        delete file;
    }

    /* implement replacement by overwrite and renaming */
    /* The real spec should be
    * void  vf_replaceWith(DataFileP old, DataFileP new);
    * This function closes both files.  It renames 'old' to a backup
    * name (.bak or similar), renames 'new' to the former name of 'old'
    * and closes both - although not necessarily in that order.
    * 
    * vf_replaceWith() can also be applied to files that have been

    * opened vf_open().
    * vf_replaceWith is not guaranteed to work unless called with a
    * former argument to vf_toReplace()and that call's result.
    */
// TODO: debug this
    void vf_replaceWith(DataFileP oldFile, DataFileP newFile)
    {
        /* copy contents of new to overwrite old*/
        vf_setpos(newFile, 0);
        vf_setpos(oldFile, 0);

        byte buffer[1024];
        long length;
        while((length = vf_read(buffer, 1024, newFile)) > 0)
            vf_write(buffer, length, oldFile);

        vf_close(newFile);
        vf_close(oldFile);
    }



    long vf_readline(char * buffer, long buffer_size, DataFileP file)
    {
        return file->file->readline(buffer, buffer_size);
    }

    long vf_writeline(char * buffer, DataFileP file)
    {
        return file->file->writeline(buffer);
    }

    long vf_writeline_xt(char * buffer, DataFileP file)
    {
        return file->file->writelineEx(buffer);
    }

    void vf_CCmode(CCmode mode, DataFileP file)
    {
        // TODO
    }


    long vf_read(void * buffer, long buffer_size, DataFileP file)
    {
        return file->file->read(buffer, buffer_size);
    }
    long vf_write(void * buffer, long buffer_size, DataFileP file)
    {
        return file->file->write(buffer, buffer_size);
    }

    //byte * vf_buffer(long * size, DataFileP file);
    //long vf_release(long size, DataFileP file);

    boolean vf_movepos(DataFileP file, long offset)
    {
        return file->file->movePos(offset);
    }
    boolean vf_setpos(DataFileP file, long offset)
    {
        return file->file->setPos(offset);
    }
    long vf_where(DataFileP file)
    {
        return file->file->where();
    }
    long vf_length(DataFileP file)
    {
        return file->file->getLength();
    }

    /* The expected_size argument is a hint to the vf_tempfile routine */
    /* It does not matter if it is wrong however ideally it should be  */
    /* a value that is unlikely to be exceeded, but not great in       */
    /* in excess of the requirement.  Use zero if no idea.             */
    DataFileP vf_tempfile(long expected_size)
    {
        AbstractIO * tempfile = CTCFox::openTemp(expected_size);
        if(!tempfile)
            return NULL;
        return CTCFox::createDataFile(tempfile , true);
    }

    //DataFileP vf_staticfile(byte * buffer, long length);

    //DataFileP vf_copyRef(DataFileP file);
    DataFileP vf_part(DataFileP file, long offset, long length)
    {
        // TODO
        return NULL;
    }

    /* vf_concat assumes responsibility for both arguments  */
    DataFileP vf_concat(DataFileP file1, DataFileP file2)
    {
        // TODO
        return NULL;
    }

} // end namespace
