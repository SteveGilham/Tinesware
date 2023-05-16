/* icons.cpp : Defines the icons for the application.
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

#include <map>
#include <algorithm>
#include "ctcfox.h"
#include "icons.h"

#include <fx.h>

#include <FXICOIcon.h>

namespace CTCFox {
#include "ctcicon.h"
#include "newfile.h"

#include "openfile.h"
#include "mailin.h"
#include "save.h"
#include "help.h"
#include "tines.h"
#include "folder.h"
#include "document.h"
#include "copy.h"
#include "cut.h"
#include "mailout.h"
#include "paste.h"
#include "lock.h"
#include "print.h"
#include "unlock.h"
#include "key.h"
#include "cross.h"
#include "tick.h"
#include "user.h"
#include "minus.h"
#include "plusplus.h"
#include "textdoc.h"
#include "datadoc.h"
#include "tblchkon.h"
#include "tblchkoff.h"

}

namespace CTCFox 
{
    class IconsImpl : public Icons {
    public:
        IconsImpl(FXApp * app) :
          Icons(), theApp(app)
          {
          }
        FXIcon * get(Icons::ID id);
        ~IconsImpl();
    private:
        FXApp* theApp;
        std::map<Icons::ID, FXIcon *> lazy;
        FXIcon * getBmpIcon(const unsigned char *);
        static FXColor background;
    };

    //=========================================================

    FXColor IconsImpl::background = FXRGB(128,0,0);

    //=========================================================

    class IconMapDeleter
    {
    public:
        IconMapDeleter();
        void operator ( ) (std::pair<Icons::ID, FXIcon *> p);
    };

    IconMapDeleter::IconMapDeleter()
    {}
    void IconMapDeleter::operator ( ) (std::pair<Icons::ID, FXIcon *> p)
    {
        delete(p.second);
    }

    //=========================================================

    FXIcon * IconsImpl::getBmpIcon(const unsigned char * icon)
    {
        return new FXBMPIcon(theApp, icon, background, IMAGE_ALPHACOLOR);
    }

    FXIcon * IconsImpl::get(Icons::ID id)
    {
        FXIcon * value = lazy[id];
        if(value != NULL)
            return value;

        switch(id)
        {
        case FRAME_ICON:
            value = new FXICOIcon(theApp, CTCFoxIco);
            break;
        case NEWFILE:
            value = getBmpIcon(newfile);
            break;
        case OPENFILE:
            value = getBmpIcon(openfile);
            break;
        case MAILIN:
            value = getBmpIcon(mailin);
            break;
        case SAVE:
            value = getBmpIcon(save);
            break;
        case HELP:
            value = getBmpIcon(help);
            break;
        case TINES:
            value = new FXBMPIcon(theApp, tines);
            break;
        case FOLDER:
            value = getBmpIcon(folder);
            break;
        case DOCUMENT:
            value = getBmpIcon(document);
            break;
        case COPY:
            value = getBmpIcon(copy);
            break;
        case CUT:
            value = getBmpIcon(cut);
            break;
        case MAILOUT:
            value = getBmpIcon(mailout);
            break;
        case PASTE:
            value = getBmpIcon(paste);
            break;
        case LOCK:
            value = getBmpIcon(lock);
            break;
        case PRINT:
            value = getBmpIcon(print);
            break;
        case UNLOCK:
            value = getBmpIcon(unlock);
            break;
        case KEY:
            value = getBmpIcon(key);
            break;
        case CROSS:
            value = getBmpIcon(cross);
            break;
        case TICK:
            value = getBmpIcon(tick);
            break;
        case USER:
            value = getBmpIcon(user);
            break;
        case MINUS:
            value = getBmpIcon(minus);
            break;
        case PLUSPLUS:
            value = getBmpIcon(plusplus);
            break;
        case TEXT:
            value = getBmpIcon(textdoc);
            break;
        case BINARY:
            value = getBmpIcon(datadoc);
            break;
        case TBLCHKON:
            value = getBmpIcon(tblchkon);
            break;
        case TBLCHKOFF:
            value = getBmpIcon(tblchkoff);
            break;
        default:
            value = (FXIcon*)0;
        }
        value->create();
        return (lazy[id] = value);
    }

    IconsImpl::~IconsImpl()
    {
        std::for_each(lazy.begin(), lazy.end(), IconMapDeleter());
    }

    Icons::Icons(FXApp * app) :
        impl(new IconsImpl(app))
    {
    }

    Icons::Icons() :
        impl(NULL)
    {
    }

    FXIcon * Icons::get(Icons::ID id)
    {
        return impl->get(id);
    }

    Icons::~Icons()
    {
        delete impl;
    }
}
