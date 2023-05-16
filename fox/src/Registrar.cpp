/* registrar.cpp : persistence support for the application.
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

#include <vector>
#include <algorithm>

#include <fx.h>

#include "Registrar.h"

namespace CTCFox {

    class Saver
    {
    public:
        Saver(FXApp * a) : app(a) {}
        void operator ( ) (Property * p)
        {
            p->save(app->reg());
        }
    private:
        FXApp * app;
    };

    //=========================================================

    Property::Property(const char * sectionI, const char * keyI)
    {
        strncpy(section, sectionI, SIZE);
        section[SIZE-1] = 0;
        strncpy(key, keyI, SIZE);
        key[SIZE-1] = 0;
    }

    //=========================================================

    TextProperty::TextProperty(const char * sectionI, const char * keyI, const char * defI)
        : Property(sectionI, keyI)
    {
        strncpy(def, defI, sizeof(def));
        def[sizeof(def)-1] = 0;
    }

    void TextProperty::load(FXRegistry & reg)
    {
        const char * text = reg.readStringEntry(section, key, def);
        setText(text);
    }
    void TextProperty::save(FXRegistry & reg) const
    {
        FXString text;
        getText(text);
        reg.writeStringEntry(section, key, text.text());      
    }

    IntProperty::IntProperty(const char * sectionI, const char * keyI, int defI)
        : Property(sectionI, keyI)
    {
        def = defI;
    }

    //=========================================================

    MutableTextProperty::MutableTextProperty(const char * sectionI, const char * keyI, const char * defI)
        : TextProperty(sectionI, keyI, defI)
    {
    }
    //=========================================================

    void IntProperty::load(FXRegistry & reg)
    {
        int value = reg.readIntEntry(section, key, def);
        setInt(value);
    }
    void IntProperty::save(FXRegistry & reg) const
    {
        int value;
        getInt(value);
        reg.writeIntEntry(section, key, value);      
    }

    //=========================================================

    class RegistrarImpl
    {
    public:
        RegistrarImpl(FXApp * a = NULL);
        ~RegistrarImpl();
        void accept(Property *);
        void finalize() const;
    private:
        std::vector<Property*> store;
        FXApp * app;
    };

    RegistrarImpl::RegistrarImpl(FXApp * a)
        : app(a)
    {
        app->reg().read();
    }


    void RegistrarImpl::accept(Property *p)
    {
        store.push_back(p);
        p->load(app->reg());
    }

    void RegistrarImpl::finalize() const
    {
        std::for_each(store.begin(), store.end(), Saver(app));
        app->reg().write();
    }

    RegistrarImpl::~RegistrarImpl()
    {
    }

    //=========================================================

    Registrar::Registrar(FXApp * a)
        : impl(new RegistrarImpl(a))
    {
    }


    void Registrar::accept(Property *p)
    {
        impl->accept(p);
    }


    void Registrar::finalize() const
    {
        impl->finalize();
    }

    Registrar::~Registrar()
    {
        delete impl;
    }

    //=========================================================

    ConcreteMutableTextProperty::ConcreteMutableTextProperty(const char * section, const char * key, const char * def)
        : MutableTextProperty(section, key, def)

    {
    }

    void ConcreteMutableTextProperty::getText(FXString & v) const
    {
        v = value;
    }

    void ConcreteMutableTextProperty::setText(const char * v)
    {
        value = v;
    }
}

