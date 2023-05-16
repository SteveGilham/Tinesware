/* HandledList.h : Defines an FXIconList that can register a double click handler.
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

#ifndef HandledList_h
#define HandledList_h

#ifndef FX_H
#error "Need to include <fx.h> before HandledList.h"
#endif


namespace CTCFox {

    class HandledList;

    class ListDoubleClickHandler {
    public:
        virtual long onDoubleClick(HandledList *, int) = 0;
    };

    class HandledList : public FXIconList {

    // Macro for class hierarchy declarations
    FXDECLARE(HandledList)

    public:
        HandledList(FXComposite *p,FXObject* tgt=NULL, unsigned int sel=0,
            unsigned int opts=LAYOUT_FILL_X|LAYOUT_FILL_Y|ICONLIST_DETAILED|ICONLIST_AUTOSIZE, int x=0, int y=0, int w=0, int h=0);
        virtual ~HandledList();
    protected:
        HandledList();

    public:

        // Message handlers
        long onDoubleClick(FXObject*,FXSelector,void*);

        // Handler adapter
        void setDoubleClickHandler(ListDoubleClickHandler * handler = NULL);

    private:
        ListDoubleClickHandler * theHandler;
    };

} // namespace

#endif //ndef HandledList_h
