/* HandledList.c : Defines an FXIconList that can register a double click handler.
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

#include <fx.h>
#include "HandledList.h"

// Message Map for the CTCFox Window class
namespace CTCFox {

FXDEFMAP(HandledList) HandledListMap[]=
{
  //________Message_Type_____________________ID____________Message_Handler_______
  FXMAPFUNC(SEL_DOUBLECLICKED,               0,            HandledList::onDoubleClick),
};


// Macro for the class hierarchy implementation
FXIMPLEMENT(HandledList,FXIconList,HandledListMap,ARRAYNUMBER(HandledListMap))

    HandledList::HandledList(FXComposite *p,FXObject* tgt, unsigned int sel,
        unsigned int opts, int x, int y, int w, int h):
        FXIconList(p, tgt, sel, opts, x,y,w,h),
        theHandler(NULL)
    {
        appendHeader("");
        appendHeader("");
    }

    HandledList::~HandledList()
    {
    }

    HandledList::HandledList():
        FXIconList(),
        theHandler(NULL)
    {
    }

    // Message handlers
    long HandledList::onDoubleClick(FXObject*,FXSelector,void*)
    {
        if(! theHandler)
            return 0;

        int r;
        bool found = false;
        for(r=0; r<getNumRows(); ++r)
        {
            found = isItemSelected(r) != 0;
            if(found) break;
        }
        if(!found)
            return 0;

        return theHandler->onDoubleClick(this, r);
    }

    // Handler adapter
    void HandledList::setDoubleClickHandler(ListDoubleClickHandler * handler)
    {
        theHandler = handler;
    }

}
