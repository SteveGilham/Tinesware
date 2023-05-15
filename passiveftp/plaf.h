/* plaf.h : Pluggable look and feel class definition
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


#ifndef PLAF_H
#define PLAF_H

namespace FX {
    class FXObject;
    class FXString;
    class FXIcon;
    class FXFont;
    class FXDCWindow;
    struct FXEvent;
}

namespace FoxSkin {

    class PlafBase
    {
        static PlafBase * thePlaf;
    protected:
        PlafBase() {}
    public:
        virtual ~PlafBase() {}
        static void apply(PlafBase * lookAndFeel);
        static PlafBase * getPlaf();
    };

    template<class X> class Plaf;

    template<class T, class X> class Skin : public T
    {
        friend class Plaf<X>;
    private:
        // Never instantiate
        Skin();
        ~Skin();

        // only available to the Plaf
        long skin(FX::FXObject*o,unsigned int sel,void*ptr);
        static void override(bool set);
    };


    template<class X> class Plaf : public PlafBase
    {
    public:
        Plaf(void);
        virtual ~Plaf(void);

        template<class T> unsigned int getOptions(T * t)
        {
            return t->options;
        }

        template<class T> FX::FXString & getLabel(T * t)
        {
            return t->label;
        }

        template<class T> int getLabelWidth(T * t, FX::FXString & s)
        {
            return t->labelWidth(s);
        }

        template<class T> int getLabelHeight(T * t, FX::FXString & s)
        {
            return t->labelHeight(s);
        }

        template<class T> FX::FXIcon* getIcon(T * t)
        {
            return t->icon;
        }

        template<class T> FX::FXFont* getFont(T * t)
        {
            return t->font;
        }

        template<class T> void just_x(T * t, int& tx,int& ix,int tw,int iw)
        {
            t->just_x(tx,ix,tw,iw);
        }

        template<class T> void just_y(T * t, int& ty,int& iy,int th,int ih)
        {
            t->just_y(ty,iy,th,ih);
        }  

        template<class T> void drawLabel(T * t, FX::FXDCWindow& dc,const FX::FXString& text,int hot,int tx,int ty,int tw,int th)
        {
            t->drawLabel(dc,text,hot,tx,ty,tw,th);
        }

        template<class T> int getHotoff(T * t)
        {
            return t->hotoff;
        }

        template<class T> int getState(T * t)
        {
            return t->state;
        }

        template<class T> unsigned int getFlags(T * t)
        {
            return t->flags;
        }

        template<class T> unsigned int getShift(T * t)
        {
            return t->shift;
        }

        template<class T> unsigned int getActive(T * t)
        {
            return t->active;
        }

        template<class T> bool getBoolState(T * t)
        {
            return t->state != 0;
        }

        template<class T> int getThumbSize(T * t)
        {
            return t->thumbsize;
        }

        template<class T> int getThumbPos(T * t)
        {
            return t->thumbpos;
        }

        template<class T> int getPressed(T * t)
        {
            return t->pressed;
        }

    };

} // namespace


#endif // ndef PLAF_H

