/* ctcfoxio.h : Defines the I/O abstractions for the application.
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

#include "AbstractIO.h"
#include "ctcfoxio.h"
#include <cstdio>

#include <fx.h>
#include "ctcfox.h"
#include "port_io.h"
#include "utils.h"

namespace CTCFox {

static const int BUFFERSIZE  = 1024;

    class RawIOBase {
    public:
        virtual void flush(void) = 0;
        virtual void close(void) = 0;
        virtual bool valid(void) = 0;
        virtual void seek(long) = 0;
        virtual long read(void * buffer, long buffer_size) = 0;
        virtual long write(const void * buffer, long buffer_size) = 0;
        virtual long length(void) = 0;
    };

    class CommonIO : public AbstractIO
    {
    friend AbstractIO * openRing(const char * ringname, bool readonly);
    friend AbstractIO * openTemp(long expected_length);

    public:
        CommonIO(RawIOBase *);
        virtual ~CommonIO();

        virtual long readline(char * buffer, long buffer_limit);
        virtual long writeline(char * buffer);
        virtual long writelineEx(char * buffer);
        virtual long read(void * buffer, long buffer_size);
        virtual long write(const void * buffer, long buffer_size);

        virtual bool movePos(long offset);
        virtual bool setPos(long offset);
        virtual long where(void);
        virtual long getLength(void);

        bool writable(void);

    protected:
        void setReadonly(bool);
        void forcePos(void);

    private:
        // no copy or assign
        CommonIO(const CommonIO &);
        CommonIO & operator=(const CommonIO &);

        RawIOBase * raw;
        bool noWrite;
        long pos;               /* current file position */
        bool pos_changed;       /* If position has been externally set */
        long length;            /* -1 if unknown/undefined (MUST be defined for a STATIC or SLICE) */

    }; // end AbstractIO Class

    CommonIO::CommonIO(RawIOBase * base) :
        raw(base),
        pos(0), pos_changed(false),
        length(-1)
    {
    }

    CommonIO::~CommonIO()
    {
        if(!raw->valid()) return;
        if(writable()) raw->flush();
        raw->close();
        delete(raw);
    }

    void CommonIO::setReadonly(bool r)
    {
        noWrite = r;
    }


    long CommonIO::readline(char * buffer, long buffer_limit)
    {
        if(!raw->valid())
        {
            FXMessageBox::error(getMainWindow(),MBOX_OK,"I/O debug","Invalid stream in readline()");
            return OSERROR;
        }
        if(writable())
        {
            setPos(0);
            setReadonly(true);
        }
        forcePos();
        bool hitEOL = false;
        long result = 0;
        long top = buffer_limit - 1;
        for (; result < top; ++result)
        {
            long i = raw->read(buffer+result, 1);
            ++pos;
            if(i < 0)
                return OSERROR;
            if(0 == i)
                break;
            if( ('\n' == buffer[result]) || ('\r' == buffer[result]))
            {
                hitEOL = true;
                break;
            }
        }
        if(hitEOL)
        {
            if('\r' == buffer[result]) // assume \r means a \n follows (canonical wire format)
            {
                raw->read(buffer+result, 1);
                ++pos;
            }
        }
        buffer[result] = 0;
        return result;
    }

    long CommonIO::writeline(char * buffer)
    {
        if(!raw->valid())
        {
            FXMessageBox::error(getMainWindow(),MBOX_OK,"I/O debug","Invalid stream in writeline()");
            return OSERROR;
        }

        long part = write(buffer, (long)strlen(buffer));
        if(part < 0)
            return part;

        long tail = write("\n", 1);
        if(tail < 0)
            return tail;

        return tail+part;
    }

    long CommonIO::writelineEx(char * buffer)
    {
        if(!raw->valid())
        {
            FXMessageBox::error(getMainWindow(),MBOX_OK,"I/O debug","Invalid stream in writelineEx()");
            return OSERROR;
        }
        return  write(buffer, (long)strlen(buffer));
    }

    void CommonIO::forcePos(void)
    {
        if(!raw->valid()) return;
        if(writable()) raw->flush();
        if(pos_changed)
        {
            raw->seek(pos);
            pos_changed = false;
        }
    }

    long CommonIO::read(void * buffer, long buffer_size)
    {
        if(!raw->valid()) return OSERROR;
        if(writable())
        {
            setPos(0);
            setReadonly(true);
        }
        forcePos();
        long result = raw->read(buffer, buffer_size);
        if(result >= 0)
        {
            pos += result;
            return result;
        }
        else
            return OSERROR;
    }

    long CommonIO::write(const void * buffer, long buffer_size)
    {
        if(!raw->valid()) return OSERROR;
        if(!writable()) return WRONGMODE;
        forcePos();
        if(raw->write(buffer, buffer_size) == buffer_size)
        {
            pos += buffer_size;
            if(pos > length) length = pos;
            return buffer_size;
        }
        else
            return OSERROR;
    }

    bool CommonIO::movePos(long offset)
    {
        if(!raw->valid()) return false;
        if(writable()) raw->flush();
        if(offset >= -pos)
        {
            pos += offset;
            pos_changed = true;
            return true;
        }
        else
            return false;
    }

    bool CommonIO::setPos(long offset)
    {
        if(!raw->valid()) return false;
        if(writable()) raw->flush();
        if(offset >= 0)
        {
            pos = offset;
            pos_changed = true;
            return true;
        }
        else
            return false;
    }

    long CommonIO::where(void)
    {
        return pos;
    }

    long CommonIO::getLength(void)
    {
        if(!raw->valid()) return OSERROR;
        if(length < 0)
        {
            /* say that the logical position is no longer at the OS position */
            pos_changed = true;
            length = raw->length();
            if(length < 0) return OSERROR;
        }
        return length;
    }

    bool CommonIO::writable(void)
    {
        return !noWrite;
    }

    //==========================================

    class PureBinaryFile : public RawIOBase
    {
    friend AbstractIO * openRing(const char * ringname, bool readonly);
    public:
        PureBinaryFile(const char * ringname, bool readonly = true);
        virtual ~PureBinaryFile();

    protected:
        virtual void flush(void);
        virtual void close(void);
        virtual bool valid(void);
        virtual void seek(long);
        virtual long read(void * buffer, long buffer_size);
        virtual long write(const void * buffer, long buffer_size);
        virtual long length();

    private:
        // no copy or assign
        PureBinaryFile(const PureBinaryFile &);
        PureBinaryFile & operator=(const PureBinaryFile &);

        // Use STDIO facilities because C++ IOStreams are
        // 1) for formatted I/O
        // 2) at different versions of the C++ standard in g++ 2.95 (classic)
        //    and VC++ and C++Builder (modern), so the binary capabilities
        //    are not really portable
        std::FILE* c_file;
    };

    AbstractIO * openRing(const char * ringname, bool readonly)
    {
        PureBinaryFile * f = new PureBinaryFile(ringname, readonly);
        if(!f)
            return 0;
        if(!f->c_file) // couldn't open the file
        {
        	delete f;
            return 0;
        }
        CommonIO * c = new CommonIO(f);
        if(c)
            c->setReadonly(readonly);
        else
            delete f;
        return c;
    }

    PureBinaryFile::PureBinaryFile(const char * ringname, bool readonly)
    {
        c_file = std::fopen(ringname, readonly ? "rb" : "wb");
    }

    PureBinaryFile::~PureBinaryFile()
    {
    }

    void PureBinaryFile::flush(void)
    {
        std::fflush(c_file);
    }

    void PureBinaryFile::close(void)
    {
        std::fclose(c_file);
    }

    bool PureBinaryFile::valid(void)
    {
        return !(!c_file);
    }

    void PureBinaryFile::seek(long position)
    {
        fseek(c_file, position, SEEK_SET);
    }

    long PureBinaryFile::length()
    {
        if(std::fseek(c_file, 0 , SEEK_END)) return CTClib::OSERROR;
        return ftell(c_file);
    }

    long PureBinaryFile::read(void * buffer, long buffer_size)
    {
        return (long) fread(buffer, 1, (size_t)buffer_size, c_file);
    }

    long PureBinaryFile::write(const void * buffer, long buffer_size)
    {
        return (long) fwrite(buffer, 1, (size_t)buffer_size, c_file);
    }

    //==========================================

    void copyBFile(DataFileP from, DataFileP to)
    {
        byte buffer[BUFFERSIZE];
        long length;

        CTClib::vf_setpos(from, 0);
        CTClib::vf_setpos(to,   0);
        while((length = CTClib::vf_read(buffer, BUFFERSIZE, from)) > 0)
            CTClib::vf_write(buffer, length, to);
    }

    void getBackupName(FXString & backupFile, FXString & pubName)
    {
        FXString workspace(pubName.text(), ".bak");
        if(!FXFile::exists(workspace))
        {
            backupFile = workspace;
            return;
        }

        for(FXuint num = 1; num; ++num)
        {
            FXString temp(workspace.text(), FXStringVal(num).text());
            if(!FXFile::exists(temp))
            {
                backupFile = temp;
                return;
            }
        }
    }

    //==========================================

    class TempFile : public RawIOBase
    {
    public:
        TempFile(long expected);
        virtual ~TempFile();
        void setParent(CommonIO*);

    protected:
        virtual void flush(void);
        virtual void close(void);
        virtual bool valid(void);
        virtual void seek(long);
        virtual long read(void * buffer, long buffer_size);
        virtual long write(const void * buffer, long buffer_size);
        virtual long length();

    private:
        // no copy or assign
        TempFile(const TempFile &);
        TempFile & operator=(const TempFile &);

        // Use STDIO facilities because C++ IOStreams are
        // 1) for formatted I/O
        // 2) at different versions of the C++ standard in g++ 2.95 (classic)
        //    and VC++ and C++Builder (modern), so the binary capabilities
        //    are not really portable
        byte * file;
        long allocated;
        CommonIO * parent;
    };

    AbstractIO * openTemp(long expected_length)
    {
        TempFile * f = new TempFile(expected_length);
        if(!f)
            return 0;
        CommonIO * c = new CommonIO(f);
        if(c)
        {
            c->setReadonly(false);
            f->setParent(c);
        }
        else
            delete f;
        return c;
    }

    TempFile::TempFile(long expected_length)
        : parent(0)
    {
        allocated = expected_length;
        if(allocated < BUFFERSIZE)
            allocated = BUFFERSIZE;

        file = reinterpret_cast<byte *>(CTClib::zmalloc(allocated));
    }

    TempFile::~TempFile()
    {
        if(file)
            CTClib::zfree(reinterpret_cast<void **>(&file), allocated);
    }

    void TempFile::setParent(CommonIO * p)
    {
        parent = p;
    }

    void TempFile::flush(void)
    {
    }

    void TempFile::close(void)
    {
    }

    bool TempFile::valid(void)
    {
        return !(!file);
    }

    void TempFile::seek(long)
    {
    }

    long TempFile::length()
    {
        return 0;
    }

    long TempFile::read(void * buffer, long buffer_size)
    {
        long top = parent->getLength();
        long limit = top - parent->where();
        if(limit > buffer_size)
            limit = buffer_size;

        if(limit <= 0)
            return 0;

        memcpy(buffer, file+parent->where(), limit);
        return limit;
    }

    long TempFile::write(const void * buffer, long buffer_size)
    {
        if(parent->where()+buffer_size > allocated)
        {
            long new_size = (parent->where()+buffer_size)/allocated;
            new_size++;
            new_size *= allocated;
            byte * new_file = reinterpret_cast<byte *>(CTClib::zmalloc(new_size));
            if(! new_file)
                return CTClib::OSERROR;

            memcpy(new_file, file, parent->where());
            CTClib::zfree(reinterpret_cast<void **>(&file), allocated);

            file = new_file;
            allocated = new_size;
        }
        memcpy(file+parent->where(), buffer, buffer_size);
        return (long) buffer_size;
    }

    class FXTextIO : public AbstractIO
    {
    public:
        FXTextIO(FX::FXText * text) : sink(text) {}
        virtual ~FXTextIO() {}

        virtual bool movePos(long offset)
        {
            return false;
        }
        virtual bool setPos(long offset)
        {
            return false;
        }

        virtual long where(void)
        {
            return -1;
        }

        virtual long getLength(void)
        {
            return -1;
        }

        virtual bool writable(void)
        {
            return true;
        }

        virtual long read(void * buffer, long buffer_size)
        {
            return -1;
        }

        virtual long write(const void * buffer, long buffer_size)
        {
            sink->appendText(reinterpret_cast<const FX::FXchar*>(buffer), buffer_size);
            return buffer_size;
        }

        virtual long readline(char * buffer, long buffer_limit)
        {
            return -1;
        }

        virtual long writeline(char * buffer)
        {
            return writelineEx(buffer) +
                writelineEx("\n");
        }

        virtual long writelineEx(char * buffer)
        {
            return write(buffer, long(strlen(buffer)));
        }


    private:
        FX::FXText * sink;

        FXTextIO(const FXTextIO & rhs);
        FXTextIO & operator=(const FXTextIO & rhs);
    };

    AbstractIO * openFXText(FX::FXText * text) // write only
    {
        return new FXTextIO(text);
    }

} // namespace
