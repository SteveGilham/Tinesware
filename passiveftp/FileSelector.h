/* FileSekector.h : restricted sub-class FTP class.
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

#ifndef FileSelector_h
#define FileSelector_h

class Downloader
{
public:
    virtual void doDownload(const FXString &details) = 0;
};

Downloader * getDownloader();

class FileSelector : public FXFileSelector 
{
    FXDECLARE(FileSelector)

public:
    FileSelector(FXComposite *p,FXObject* tgt=NULL,FXSelector sel=0,FXuint opts=0,FXint x=0,FXint y=0,FXint w=0,FXint h=0);
    FXFileList * getFilebox(void);

    long onDNDEnter(FXObject*,FXSelector,void*);
    long onDNDLeave(FXObject*,FXSelector,void*);
    long onDNDDrop(FXObject*,FXSelector,void*);
    long onDNDMotion(FXObject*,FXSelector,void*);
    long onDNDRequest(FXObject*,FXSelector,void*);
    long onBeginDrag(FXObject*,FXSelector,void*);
    long onEndDrag(FXObject*,FXSelector,void*);
    long onDragged(FXObject*,FXSelector,void*);

    void refresh(void);

protected:
    FileSelector(void);
};

#endif //ndef FileSelector_h
