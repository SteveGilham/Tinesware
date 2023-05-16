/* resource, : Defines the icons for the application.
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

#ifndef resource_h
#define resource_h

class FXApp;
class FXIcon;
class IconsImpl;

namespace CTCFox 
{
    class IconsImpl;

    class Icons
    {
    public:
        enum ID {
            IDI_FRAME_ICON = 0,
            IDI_NEWFILE,
            IDI_OPENFILE,
            IDI_MAILIN,
            IDI_SAVE,
            IDI_HELP,
            IDI_TINES,
            IDI_FOLDER,
            IDI_DOCUMENT,
            IDI_COPY,
            IDI_CUT,
            IDI_MAILOUT,
            IDI_PASTE,
            IDI_LOCK,
            IDI_PRINT,
            IDI_UNLOCK,
            IDI_KEY,
            IDI_CROSS,
            IDI_TICK,
            IDI_USER,
        };

        Icons(FXApp *);
        Icons();
        FXIcon * get(ID id);
        virtual ~Icons();

    private:
        IconsImpl * impl;

        Icons(const Icons &);
        Icons& operator=(const Icons &);
    };
}


//#define IDI_ICON                        101

#endif //ndef resource_h
