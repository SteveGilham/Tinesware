/* registrar.h : persistence support for the application.
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

#ifndef registrar_h
#define registrar_h

#ifndef FX_H
#error "Need to include <fx.h> before Registrar.h"
#endif

namespace FX {
class FXApp;
class FXRegistry;
class FXString;
}
using FX::FXApp;
using FX::FXRegistry;
using FX::FXString;

namespace CTCFox {

    class Property
    {
    public:
        enum {
            SIZE = 32
        };
        Property(const char * section, const char * key);
        virtual void load(FXRegistry &) = 0;
        virtual void save(FXRegistry &) const = 0;
    private:
        Property(const Property &);
        void operator=(const Property &);
    protected:
        char section[SIZE];
        char key[SIZE];
    };

    class TextProperty : public Property
    {
    public:
        TextProperty(const char * section, const char * key, const char * def);
        virtual void load(FXRegistry &);
        virtual void save(FXRegistry &) const;
    protected:
        virtual void getText(FXString &) const = 0;
        virtual void setText(const char *) = 0;
        char def[4*SIZE];
    };

    class MutableTextProperty : public TextProperty
    {
    public:
        MutableTextProperty(const char * section, const char * key, const char * def);
        virtual void getText(FXString &) const = 0;
        virtual void setText(const char *) = 0;
    };

    class ConcreteMutableTextProperty : public MutableTextProperty
    {
    public:
        ConcreteMutableTextProperty(const char * section, const char * key, const char * def);
        virtual void getText(FXString &) const;
        virtual void setText(const char *);
        virtual ~ConcreteMutableTextProperty() {}

    private:
        FXString value;
    };

    class IntProperty : public Property
    {
    public:
        IntProperty(const char * section, const char * key, int def);
        virtual void load(FXRegistry &);
        virtual void save(FXRegistry &) const;
    protected:
        virtual void getInt(int &) const = 0;
        virtual void setInt(int) = 0;
        int def;
    };

    class RegistrarImpl;
    class Registrar
    {
    public:
        Registrar(FXApp * a = 0);
        ~Registrar();
        void accept(Property *);
        void finalize() const;
    private:
        Registrar(const Registrar &);
        void operator=(const Registrar &);
        //std::vector<Property*> store;
        //FXApp * app;
        RegistrarImpl * impl;
    };
}

#endif //ndef registrar_h
