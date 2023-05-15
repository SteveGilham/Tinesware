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

namespace FX {
    class FXApp;
    class FXIcon;
}
using FX::FXApp;
using FX::FXIcon;

class IconsImpl;

    class IconsImpl;

    class Icons
    {
    public:
        enum ID {
            FRAME_ICON = 0,
            TINES,
            FOLDER,
            DOCUMENT,
            NEWFOLDER,
            DLGNEWFOLDER,
            DELETEFILE,
        };

        Icons(FXApp *);
        Icons();
        FXIcon * get(ID id);
        FXIcon * operator[](ID id);
        virtual ~Icons();

    private:
        IconsImpl * impl;

        Icons(const Icons &);
        Icons& operator=(const Icons &);
    };

#endif //ndef resource_h
