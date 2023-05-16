/* keytree.h : The keyring display for the application.
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

#ifndef keytree_h
#define keytree_h

#ifndef _ctc_h
#error "Need to include ctc.h before keytree.h"
#endif
#ifndef HandledList_h
#error "Need to include HandledList.h before keytree.h"
#endif
#ifndef registrar_h
#error "Need to include registrar.h before keytree.h"
#endif

namespace CTClib {
    struct seckey_T; 
    struct pubkey_T; 
    struct decode_context_T;
}

namespace FX {
class FXTreeItem;
class FXTreeList;
class FXSwitcher;
class FXFont;
}
using FX::FXTreeItem;
using FX::FXTreeList;
using FX::FXSwitcher;
using FX::FXFont;

namespace CTCFox {

    class KeyList;

    class LazyContainer;

    class PubringTreeItem;
    class PubkeyInserter;
    class PubkeyTreeItem;

    class SecringTreeItem;
    class SeckeyInserter;
    class SeckeyTreeItem;

    class UsernameTreeItem;
    class SignatureTreeItem;
    class MessageTreeItem;
    class TextTreeItem;

    class RootTreeItem : public FXTreeItem, public ListDoubleClickHandler
    {
    friend class LazyContainer;

    friend class PubringTreeItem;
    friend class PubkeyInserter;
    friend class PubkeyTreeItem;

    friend class SecringTreeItem;
    friend class SeckeyInserter;
    friend class SeckeyTreeItem;

    friend class UsernameTreeItem;
    friend class SignatureTreeItem;
    friend class MessageTreeItem;
    friend class TextTreeItem;

    // Macro for class hierarchy declarations
        FXDECLARE(RootTreeItem)

    public:
        RootTreeItem(FXTreeList *);
        virtual ~RootTreeItem();
    protected:
        RootTreeItem(void);

    public:
        virtual long onDoubleClick(HandledList *, int);
        virtual void setOpened(FXbool opened);
        virtual void setExpanded(FXbool expanded);
        virtual void setSelected(FXbool selected);
        void populate(void);
        void lockAll(void);
        void logUnlocked(CTClib::seckey_T * key);
        void lock(CTClib::seckey_T * key);
        void newMessage(FXSwitcher *, FXString * file = NULL);
        bool terminate(void);
        void close(bool save = true);
        void saveItem(bool as = false);

        void cut(void);
        void copy(void);
        void paste(void);
        void quote(void);
        void rot13(void);

        void extractPkey(void);
        void ringSign(void);
        void enablePkey(void);
        void deletePkey(void);

        void decrypt(CTClib::decode_operation complete);
        CTClib::DataFileP getWritable(void);

        void setCurrentName(UsernameTreeItem * name);
        UsernameTreeItem * getCurrentName(void) const;

        void setSlantFont(FXFont * font);
        FXFont * getSlantFont(void) const;

    private:
        void reap(void);
        bool closeKeyrings(void);
        bool backupKeyrings(void);

        FXTreeList * tree;

        ConcreteMutableTextProperty    pubringName;
        ConcreteMutableTextProperty    secringName;
        ConcreteMutableTextProperty    quoteString;

        CTClib::decode_context theContext;

        bool pubringBacked;
        bool secringBacked;

        KeyList * keylist;
        UsernameTreeItem * currentName;
        FXFont * slant;
    };

} //end namespace


#endif //ndef keytree_h
