/* metal.h : Skin theme class definitions
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

#ifndef METAL_H
#define METAL_H

#include "plaf.h"
#include <fx.h>

namespace FoxSkin {

    // Have to template the base class on the derived
    // so the base can cast the global Plaf to the derived type
    class Metal : public Plaf<Metal>
    {
    public:
        Metal();
        virtual ~Metal();

        // useful method
        static FX::FXFont & getControlFont(void)
        {
            initFonts();
            return *controlFont;
        }

        // member template functions can't be virtual
        // Must have it in place here however
        // before we can override it
        template<class T> bool onSkin(T * t, FX::FXEvent * e);
        
#ifdef _MSC_VER
        // specialisations - Borland doesn't need or want to have them declared here
#define SPECIALIZE(type) template <> bool onSkin<FoxSkin::Skin<type, FoxSkin::Metal> >(FoxSkin::Skin<type, FoxSkin::Metal> * t, FX::FXEvent * ev)
        SPECIALIZE(FX::FXButton);
        SPECIALIZE(FX::FXCheckButton);
        SPECIALIZE(FX::FXMenuButton);
        SPECIALIZE(FX::FXToggleButton);
        SPECIALIZE(FX::FXMenuCascade);
        SPECIALIZE(FX::FXMenuCommand);
        SPECIALIZE(FX::FXMenuSeparator);
        SPECIALIZE(FX::FXMenuTitle);
        SPECIALIZE(FX::FXWindow);
        SPECIALIZE(FX::FXPacker);
        SPECIALIZE(FX::FXLabel);
        SPECIALIZE(FX::FXFrame);
        SPECIALIZE(FX::FXToolTip);
        SPECIALIZE(FX::FXToolBarGrip);
        SPECIALIZE(FX::FXTextField);
        SPECIALIZE(FX::FXStatusLine);
        SPECIALIZE(FX::FXDragCorner);
        SPECIALIZE(FX::FXHeader);
        SPECIALIZE(FX::FXScrollBar);
        SPECIALIZE(FX::FXScrollCorner);
        SPECIALIZE(FX::FXSeparator);
        SPECIALIZE(FX::FXTabBar);
        SPECIALIZE(FX::FXTabBook);
        SPECIALIZE(FX::FXTabItem);
        SPECIALIZE(FX::FXProgressBar);
#endif

    private:
        Metal(const Metal &);
        Metal & operator==(const Metal &);


        void drawFrame(FXDCWindow & dc, int x, int y, int w, int h, int options=FRAME_GROOVE);
        void drawText(FXDCWindow& dc, Skin<FX::FXTextField,Metal> * tgt);
        void drawTextFragment(FXDCWindow& dc,FXint x,FXint y,FXint fm,FXint to,FXString & contents, FXFont * font);
        void drawPWDTextFragment(FXDCWindow& dc,FXint x,FXint y,FXint fm,FXint to, FXFont * font);
        void drawFXHeaderItem(FX::FXHeaderItem * item, const FX::FXHeader* header,FX::FXDC& dc,FXint x,FXint y,FXint w,FXint h);
        void drawThumb(FXDCWindow & dc, int x, int y, int w, int h, bool horizontal=true);
        void drawTrough(FXDCWindow & dc, int x, int y, int w, int h, const FX::FXColor & fill, bool horizontal=true);
        void drawBullet(FXDCWindow& dc,FXint x,FXint y);
        void drawCheck(FXDCWindow& dc,FXint x,FXint y);
        void drawTriangle(FXDCWindow& dc,FXint l,FXint t,FXint r,FXint b);

        static void initFonts();

        static FX::FXColor primary1;
        static FX::FXColor primary2;
        static FX::FXColor primary3;

        static FX::FXColor secondary1;
        static FX::FXColor secondary2;
        static FX::FXColor secondary3;

        static FX::FXColor black;
        static FX::FXColor white;

        static FX::FXFont * controlFont;
        static FX::FXFont * smallFont;
        static FX::FXFont * systemFont;
        static FX::FXFont * userFont;
    };

} // namespace FoxSkin


#endif //THEME_H
