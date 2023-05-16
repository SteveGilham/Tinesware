/* ctcfox.h : application global includes
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

#ifndef ctcfox_h
#define ctcfox_h

namespace FX {
class FXMainWindow;
class FXApp;
class FXStatusLine;
class FXString;
class FXSwitcher;
}
using FX::FXMainWindow;
using FX::FXApp;
using FX::FXStatusLine;
using FX::FXString;
using FX::FXSwitcher;

namespace CTCFox {
    class Icons;
    class Registrar;
    class HandledList;
    class RootTreeItem;

    class KeyTreeResponder {
    public:
        // TODO - text message actions vs generic
        virtual void enableMessageActions(bool enable = true) = 0;
        virtual void enableModifiedMessageActions(bool enable = true) = 0;
        virtual void enablePubkeyActions(bool enable = true) = 0;
        virtual void enableRingSignActions(bool enable = true) = 0;
        virtual void enableSeckeyActions(bool enable = true) = 0;
        virtual void enableTextActions(bool enable = true) = 0;

        virtual HandledList * getList(void) = 0;
        virtual void showList(void) = 0;

        virtual void setTitle(FXString & s) = 0;

        virtual FX::FXSwitcher * getSwitcher(void) = 0;
    };
}

FXApp * getApplication(void);
FXMainWindow * getMainWindow(void);
FXStatusLine * getStatusLine(void);
CTCFox::Icons * getIconSource(void);
CTCFox::Registrar * getRegistrar(void);
CTCFox::KeyTreeResponder * getTreeResponder(void);
CTCFox::RootTreeItem * getTreeRoot(void);

#define CTCFOX_FILE_VERSION "1.0.1044.28037"
#define CTCFOX_PRODUCT_VERSION "1.0.1044.28037"
#define CTCFOX_PRODUCT_NAME "CTCFox"
#define CTCFOX_PRODUCT_COPYRIGHT "Copyright © 2002 Mr.Tines"

#endif //ndef ctcfox_h
