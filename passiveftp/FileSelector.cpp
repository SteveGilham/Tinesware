/* FileSekector.cpp : restricted sub-class FTP class.
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

#ifdef WIN32
#include <winsock2.h>
#endif
#include "pftp.h"

#ifdef _MSC_VER
#pragma warning (disable : 4244)
#endif
#include <fx.h>
#ifdef _MSC_VER
#pragma warning (default : 4244)
#endif

#include "FileSelector.h"
#include "FileIcon.h"

extern FileSelector * g_here;

class FileIconItem : public FXFileItem {
  FXDECLARE(FileIconItem)

public:
    FileIconItem(const FXString& text,FXIcon* bi=NULL,FXIcon* mi=NULL,void* ptr=NULL):FXFileItem(text,bi,mi,ptr){}
    long dummy(FXObject* ,FXSelector ,void* )
    {
        return 0;
    }
#ifdef WIN32
    virtual void setText(const FXString& txt)
    { 
        FXFileItem::setText(txt); 
        FXIcon *b, *s;
        if(getFileIcons(txt, &b, &s))
        {
            if(s)
                FXFileItem::setMiniIcon(s);
        }
    }
    virtual void setBigIcon(FXIcon* ){  }
    virtual void setMiniIcon(FXIcon* ){  }
    virtual void create()
    {
        FXFileItem::create(); 
        FXIcon *b, *s;
        FXString name = getText().before('\t');
        bool real = g_here != NULL && name != "." && name != "..";


        if(real)
        {
            name = g_here->getDirectory()+"\\"+name;
        }
        if(name == ".." && g_here != NULL)
        {
            real = true;
            name = g_here->getDirectory();
        }

        if(getNameIcons(name, &b, &s, real))
        {
            if(s)
                FXFileItem::setMiniIcon(s);
        }
    }
#endif

protected:
    FileIconItem() : FXFileItem() {}

};

FXDEFMAP(FileIconItem) FileIconItemMap[]=

{
  //________Message_Type_____________________ID____________________Message_Handler_______
  FXMAPFUNC(SEL_DND_MOTION,         FileSelector::ID_FILELIST,    FileIconItem::dummy),
};


// Macro for the class hierarchy implementation
FXIMPLEMENT(FileIconItem,FXFileItem,FileIconItemMap,0)//ARRAYNUMBER(FileIconListMap))


class FileIconList : public FXFileList 
{
    FXDECLARE(FileIconList)

public:
    FileIconList(FXComposite *p,FXObject* tgt=NULL,FXSelector sel=0,FXuint opts=0,FXint x=0,FXint y=0,FXint w=0,FXint h=0)
        : FXFileList(p, tgt, sel, opts, x, y, w, h)
    {
    }
    long dummy(FXObject* ,FXSelector ,void* )
    {
        return 0;
    }
    virtual FXIconItem *createItem(const FXString& text,FXIcon *big,FXIcon* mini,void* ptr);

protected:
    FileIconList()
        : FXFileList()
    {
    }

};


FXDEFMAP(FileIconList) FileIconListMap[]=

{
  //________Message_Type_____________________ID____________________Message_Handler_______
  FXMAPFUNC(SEL_DND_MOTION,         FileSelector::ID_FILELIST,    FileIconList::dummy),
};


// Macro for the class hierarchy implementation
FXIMPLEMENT(FileIconList,FXFileList,FileIconListMap,0)//ARRAYNUMBER(FileIconListMap))

FXIconItem *FileIconList::createItem(const FXString& text,FXIcon *big,FXIcon* mini,void* ptr)
{
    return new FileIconItem(text,big,mini,ptr);
}

FXDEFMAP(FileSelector) FileSelectorMap[]=
{
  //________Message_Type_____________________ID____________________Message_Handler_______
  FXMAPFUNC(SEL_DND_ENTER,          FileSelector::ID_FILELIST,    FileSelector::onDNDEnter),
  FXMAPFUNC(SEL_DND_LEAVE,          FileSelector::ID_FILELIST,    FileSelector::onDNDLeave),
  FXMAPFUNC(SEL_DND_DROP,           FileSelector::ID_FILELIST,    FileSelector::onDNDDrop),
  FXMAPFUNC(SEL_DND_MOTION,         FileSelector::ID_FILELIST,    FileSelector::onDNDMotion),
};


// Macro for the class hierarchy implementation
FXIMPLEMENT(FileSelector,FXFileSelector,FileSelectorMap,ARRAYNUMBER(FileSelectorMap))

FileSelector::FileSelector(FXComposite *p,FXObject* tgt,FXSelector sel,FXuint opts,FXint x,FXint y,FXint w,FXint h)
: FXFileSelector(p,tgt,sel,opts,x,y, w,h)
{
    accept->hide();
    cancel->hide();

    // no buttons or such wanted
    getFirst()->getNext()->hide();

    // yes, select many files
    setSelectMode(SELECTFILE_MULTIPLE);

    filebox->hide();
    filebox=new FileIconList(
        (FXHorizontalFrame*)(filebox->getParent())
        ,this,ID_FILELIST,ICONLIST_MINI_ICONS|ICONLIST_BROWSESELECT|ICONLIST_AUTOSIZE|LAYOUT_FILL_X|LAYOUT_FILL_Y);
       
    filebox->setListStyle(ICONLIST_DETAILED);
    filebox->setHeaderSize(1,0);

    // rupture encapsulation here...
    FXWindow *buttons= getFirst();
    FXWindow* b = buttons->getLast();
    b->hide();
    b = b->getPrev();
    b->hide();
    b = b->getPrev();
    b->hide();
    b = b->getPrev();
    b->hide();
}

FileSelector::FileSelector()
: FXFileSelector()
{}

FXFileList * FileSelector::getFilebox()
{
    return filebox;
}

// handles drags from the file selector
long FileSelector::onDNDEnter(FXObject* ,FXSelector ,void* )
{
    acceptDrop(DRAG_ACCEPT);
    return 1;
}
long FileSelector::onDNDMotion(FXObject* ,FXSelector ,void*)
{
    acceptDrop(DRAG_ACCEPT);
    return 1;
}
long FileSelector::onDNDLeave(FXObject*,FXSelector,void*)
{
    acceptDrop(DRAG_REJECT);
    return 1;
}
long FileSelector::onDNDDrop(FXObject*,FXSelector,void*)
{
    FXuchar * data = NULL;
    FXuint size;
    if(!getDNDData(FROM_DRAGNDROP,textType,data,size))
        return 1;

    if(size > 0)
    {
        char * start = reinterpret_cast<char*>(data);
        char * end = strchr(start, '\r');

        while(end && (start - reinterpret_cast<char*>(data)) < (int)size)
        {
            *end = 0;
            getDownloader()->doDownload(FXString(start));

            start = end+2;
            end = strchr(start, '\r');
        }
    }

    if(data)
        FXFREE(&data);

    refresh();
    return 1;
}

void FileSelector::refresh(void)
{
    //filebox->onRefreshTimer(NULL,0,NULL);
    FXString here = getDirectory();
    setDirectory("."); // file name (this directory)
    setDirectory(here);
}

