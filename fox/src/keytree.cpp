/* keytree.cpp : The keyring display for the application.
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

#include <set>
#include <vector>
#include <algorithm>
#include <time.h>
#include <cstdio>
#include <memory>

// Collation support 
#ifdef WIN32
#include <mbstring.h>
#else
#endif

#include "ctcfox.h"
#include "keyhash.h"
#include "ctc.h"

#include <fx.h>
#include <fxkeys.h>
#include "HandledList.h"
#include "Registrar.h"
#include "keytree.h"
#include "icons.h"
#include "ctcfoxio.h"
#include "AbstractIO.h"

#include "../strings/strings.h"

#include "abstract.h"
#include "pkautils.h"
#include "keyhash.h"
#include "ctc.h"
#include "keyhash.h"
#include "port_io.h"
#include "utils.h"

using CTClib::seckey_T;

static bool collate(const char * t1, const char * t2)
{
#ifdef WIN32
        return _mbsicoll(
            reinterpret_cast<const unsigned char *>(t1),
            reinterpret_cast<const unsigned char *>(t2)) < 0;
#else
        return strcoll(t1, t2) < 0;
#endif
}

namespace CTCFox {



    class KeyList {
    public:
        KeyList();
        ~KeyList();
        void logInternalisedKey(seckey_T * key);
        void clearInternalisedKey(seckey_T * key);
        void clearInternalisedKeys(void);
    private:
        typedef std::set<CTClib::seckey *> keylist;
        keylist list;
    };

    //=========================================================

    class PubkeyInserter
    {
    public:
        PubkeyInserter(RootTreeItem *, PubringTreeItem *);
        void operator ( ) (CTClib::pubkey_T *);
    private:
        RootTreeItem * root;
        PubringTreeItem * node;
        CTClib::decode_context_T * context;
        Icons * icons;
    };

    //=========================================================

    class SeckeyInserter
    {
    public:
        SeckeyInserter(RootTreeItem *, SecringTreeItem *);
        void operator ( ) (CTClib::seckey_T *);
    private:
        RootTreeItem * root;
        SecringTreeItem * node;
        CTClib::decode_context_T * context;
        Icons * icons;
    };

    //=========================================================

    class LazyContainer : public FXTreeItem, public ListDoubleClickHandler
    {

        // Macro for class hierarchy declarations
        FXDECLARE(LazyContainer)

    public:
        LazyContainer(RootTreeItem *, const char *, FXIcon*, FXIcon *);
        virtual ~LazyContainer();
    protected:
        LazyContainer();

    public:
        virtual void setExpanded(FXbool expanded);
        virtual long onDoubleClick(HandledList *, int);
        virtual void populate(void);
        virtual void setOpened(FXbool opened);
        virtual void setSelected(FXbool selected);

    protected:
        RootTreeItem * root;
        virtual void reap(void);
        virtual void setListModel(HandledList *);
    };

    //=========================================================

    class PubringTreeItem : public LazyContainer
    {

        // Macro for class hierarchy declarations
        FXDECLARE(PubringTreeItem)

    public:
        PubringTreeItem(RootTreeItem *);
        virtual ~PubringTreeItem();
    protected:
        PubringTreeItem();

    public:
        virtual void setOpened(FXbool opened);
        virtual void populate(void);

        bool keynameSort(CTClib::pubkey *k1, CTClib::pubkey *k2);

        class KeynameSort {
            PubringTreeItem * parent;
        public:
            KeynameSort(PubringTreeItem * p);
            bool operator() (CTClib::pubkey *k1, CTClib::pubkey *k2);
        };
    };

    //=========================================================

    class SecringTreeItem : public LazyContainer
    {

        // Macro for class hierarchy declarations
        FXDECLARE(SecringTreeItem)

    public:
        SecringTreeItem(RootTreeItem *);
        virtual ~SecringTreeItem();
    protected:
        SecringTreeItem();

    public:
        virtual void setOpened(FXbool opened);
        virtual void populate(void);

        bool keynameSort(CTClib::seckey *k1, CTClib::seckey *k2);

        class KeynameSort {
            SecringTreeItem * parent;
        public:
            KeynameSort(SecringTreeItem * p);
            bool operator() (CTClib::seckey *k1, CTClib::seckey *k2);
        };
    };

    //=========================================================

    class TextTreeItem;
    class MessageTreeItem : public LazyContainer
    {
    friend class TextTreeItem;

        // Macro for class hierarchy declarations
        FXDECLARE(MessageTreeItem)

    public:
        MessageTreeItem(RootTreeItem *);
        virtual ~MessageTreeItem();
        void newMessage(FXSwitcher *, FXString * str = NULL);
        bool terminate(void);
        void close(bool save = true);
        void saveItem(bool as = false);

        void cut(void);
        void copy(void);
        void paste(void);
        void quote(const FXString & qstr);
        void rot13(void);
        
        void decrypt(CTClib::decode_operation complete);
        CTClib::DataFileP getWritable(void);

    protected:
        MessageTreeItem();

    private:
        FXTreeItem * current;
        FXIconList * dlgList;

    public:
        virtual void setOpened(FXbool opened);

    public: 
        enum { 
            ID_SELALL=0, 
            ID_SELNONE, 
            ID_SELECT, 
            ID_LAST 
        }; 

        long onSelAll(FXObject*,FXSelector,void*);
        long onSelNone(FXObject*,FXSelector,void*);
        long onSelect(FXObject*,FXSelector,void*);
        long onKey(FXObject*,FXSelector,void*);

    };

    //=========================================================

    class TextTreeItem : public LazyContainer
    {

        // Macro for class hierarchy declarations
        FXDECLARE(TextTreeItem)

    public:
        TextTreeItem(RootTreeItem *, FXSwitcher *,FXString * str = NULL, CTClib::signature * sig = 0);
        TextTreeItem(RootTreeItem *, const char * name, const char * body, FXSwitcher *);
        virtual ~TextTreeItem();

    protected:
        TextTreeItem();

    public:
        virtual void setOpened(FXbool opened);
        virtual void populate(void);
        void loadFile(FXString * filename, FXText * text);
        bool isModified(void);
        void saveItem(bool as = false);
        void close(bool save = true);
        long onModified(FXObject*,FXSelector,void*);

        void cut(void);
        void copy(void);
        void paste(void);
        void quote(const FXString & qstr);
        void rot13(void);

        void decrypt(CTClib::decode_operation complete);
        CTClib::DataFileP getWritable(void);

        void getFirstLine(FXString & line);

    // Messages for our class
    enum{
        ID_TEXT = 0,
        ID_LAST
        };


    private:
        int find(void);
        void loadBinary(const FXString &);

        CTClib::signature * sig;
        FXText * text;
        FXSwitcher * switcher;
        bool binary;
        bool canSave;
        int wasModified;
        FXuchar * fileBuffer;
        FXuint length;
    };

    //=========================================================

    class PubkeyTreeItem : public LazyContainer
    {

        // Macro for class hierarchy declarations
        FXDECLARE(PubkeyTreeItem)

    public:
        PubkeyTreeItem(RootTreeItem *, CTClib::pubkey *);
        virtual ~PubkeyTreeItem();
    protected:
        PubkeyTreeItem();
        virtual void draw(const FXTreeList* list,FXDC& dc,FXint x,FXint y,FXint w,FXint h) const;

    public:
        virtual long onDoubleClick(HandledList *, int);
        virtual void populate(void);
        virtual void setOpened(FXbool opened);

        void extractPkey(void);
        void signPkey(void);
        void enablePkey(void);
        void deletePkey(void);

    private:
        CTClib::pubkey * key;
    };

    //=========================================================

    class SeckeyTreeItem : public LazyContainer
    {

        // Macro for class hierarchy declarations
        FXDECLARE(SeckeyTreeItem)

    public:
        SeckeyTreeItem(RootTreeItem *, CTClib::seckey *);
        virtual ~SeckeyTreeItem();
    protected:
        SeckeyTreeItem();

    public:
        virtual long onDoubleClick(HandledList *, int);
        virtual void populate(void);
        virtual void setOpened(FXbool opened);

    private:
        CTClib::seckey * key;
    };

    //=========================================================

    class UsernameTreeItem : public LazyContainer
    {

        // Macro for class hierarchy declarations
        FXDECLARE(UsernameTreeItem)

    public:
        UsernameTreeItem(RootTreeItem *, CTClib::username *, CTClib::pubkey * pk);
        virtual ~UsernameTreeItem();
    protected:
        UsernameTreeItem();

    public:
        virtual long onDoubleClick(HandledList *, int);
        virtual void populate(void);
        virtual void setOpened(FXbool opened);
        void sign(void);

    private:
        CTClib::username * key;
        CTClib::pubkey * pkey;
    };

    //=========================================================

    class SignatureTreeItem : public LazyContainer
    {

        // Macro for class hierarchy declarations
        FXDECLARE(SignatureTreeItem)

    public:
        SignatureTreeItem(RootTreeItem *, CTClib::signature *, CTClib::pubkey * pk = 0, CTClib::username * name = 0);
        virtual ~SignatureTreeItem();
    protected:
        SignatureTreeItem();

    public:
        virtual long onDoubleClick(HandledList *, int);
        virtual void populate(void);
        virtual void setOpened(FXbool opened);

    private:
        CTClib::signature * key;
        bool fertile;
    };

    //=========================================================

/*
FXDEFMAP(RootTreeItem) RootTreeItemMap[]=
{
  //________Message_Type_____________________ID____________Message_Handler_______
  FXMAPFUNC(SEL_DOUBLECLICKED,               0,            HandledList::onDoubleClick),
};
*/

// Macro for the class hierarchy implementation
FXIMPLEMENT(RootTreeItem,FXTreeItem,NULL, 0);//HandledListMap,ARRAYNUMBER(HandledListMap))

    RootTreeItem::RootTreeItem(FXTreeList * t) :
        FXTreeItem("CTCFox", getIconSource()->get(Icons::OPENFILE),getIconSource()->get(Icons::FOLDER)),
        tree(t),
        pubringName("Keyrings", "Public", FXFile::absolute(FXFile::getCurrentDirectory(),"pubring.pgp").text() ),
        secringName("Keyrings", "Private", FXFile::absolute(FXFile::getCurrentDirectory(),"secring.pgp").text() ),
        quoteString("Editor", "Quote", "> "),
        pubringBacked(false),
        secringBacked(false),
        keylist(new KeyList()),
        currentName(NULL),
        slant(NULL)
    {
        theContext.keyRings = 0;
        getRegistrar()->accept(&pubringName);
        getRegistrar()->accept(&secringName);
        getRegistrar()->accept(&quoteString);
    }

    RootTreeItem::~RootTreeItem()
    {
        closeKeyrings();
        if(slant)
        {
        	slant->detach();
            delete slant;
        }
        delete keylist;
   }

    RootTreeItem::RootTreeItem() :
        FXTreeItem(), tree(NULL),
        pubringName("Keyrings", "Public", FXFile::absolute(FXFile::getCurrentDirectory(),"pubring.pgp").text() ),
        secringName("Keyrings", "Private", FXFile::absolute(FXFile::getCurrentDirectory(),"secring.pgp").text() ),
        quoteString("Editor", "Quote", "> "),
        pubringBacked(true),
        secringBacked(true),
        keylist(0),
        currentName(NULL),
        slant(NULL)
    {
        theContext.keyRings = 0;
    }

    void RootTreeItem::setCurrentName(UsernameTreeItem * name)
    {
    	currentName = name;
    }

    UsernameTreeItem * RootTreeItem::getCurrentName(void) const
    {
        return currentName;
    }

    void RootTreeItem::setSlantFont(FXFont * font)
    {
    	slant = font;
    }

    FXFont * RootTreeItem::getSlantFont(void) const
    {
        return slant;
    }

    long RootTreeItem::onDoubleClick(HandledList *, int row)
    {
        // Cross couple activations
        if(row < 3)
        {
            FXTreeItem * t = getFirst();
            if(row > 0) t = t->getNext();
            if(row > 1) t = t->getNext();

            t->setOpened(true);
            return 1;
        }

        // Update the key ring files
        if(row < 5)
        {
            const int PUBROW = 3;
            FXFileDialog f(getMainWindow(), row==PUBROW ?
                Strings::load(Strings::PUBRING) : Strings::load(Strings::SECRING));
            FXString filename;
            if(PUBROW==row)
                pubringName.getText(filename);
            else
                secringName.getText(filename);
            f.setFilename(filename);

            if(f.execute(PLACEMENT_OWNER))
            {
                FXString result = f.getFilename();

                if(strcmp(filename.text(), result.text()))
                {
                    FXTreeItem::setExpanded(false);
                    reap();
                    closeKeyrings();

                    if(PUBROW==row)
                        pubringName.setText(result.text());
                    else
                        secringName.setText(result.text());

                    setOpened(true);
                }
            }
            return 1;
        }

        // TODO in place quote string editing
        return 0;
    }

    bool RootTreeItem::closeKeyrings(void)
    {
        if(!(theContext.keyRings)) 
            return true;

        if(!backupKeyrings())
        {
            // TODO message box
            return false;
        }

        DataFileP pring, sring, ring;
        bool cp=false, cs=false;

        if(CTClib::publicChanged(theContext.keyRings))
        {
            pring = CTClib::vf_tempfile(0);
            if(!pring) return false;
            if(!CTClib::writePubRing(pring, theContext.keyRings)) return false;
            cp = true;
        }

        if(CTClib::secretChanged(theContext.keyRings))
        {
            sring = CTClib::vf_tempfile(0);
            if(!sring) return false;
            if(!CTClib::writeSecRing(sring, theContext.keyRings)) return false;
            cs = true;
        }

        // shut down old keyring records as file offsets &c. will
        // in general be stale; also close the old keyring handle down
        // must not close this file before this as writePubRing() will read
        // from this file!  Nor, for similar reasons, can we directly
        // write to the old file names.  This is in lieu of the
        // proper implementation of vf_replacewith() and vf_toreplace()

        if(cp)
        {
            FXString pubName;
            pubringName.getText(pubName);
            AbstractIO * outIO = openRing(pubName.text(), false);
            if(!outIO) return false;
            ring = createDataFile(outIO);
            if(!ring) return false;
            copyBFile(pring, ring);
            CTClib::vf_close(pring);
        }

        if(cs)
        {
            FXString secName;
            secringName.getText(secName);
            AbstractIO * outIO = openRing(secName.text(), false);
            if(!outIO) return false;
            ring = createDataFile(outIO);
            if(!ring) return false;
            copyBFile(sring, ring);
            CTClib::vf_close(sring);
        }

        keylist->clearInternalisedKeys();
        CTClib::destroy_userhash(theContext.keyRings);
        theContext.keyRings = NULL;

        return true;
    }

    bool RootTreeItem::backupKeyrings(void)
    {
        if(!theContext.keyRings)
            return true; // no-op

        DataFileP fromring, toring;

        if(CTClib::publicChanged(theContext.keyRings) && !pubringBacked)
        {
            FXString pubName;
            pubringName.getText(pubName);

            FXString backupFile;
            getBackupName(backupFile, pubName);

            AbstractIO * outIO = openRing(backupFile.text(), false);
            if(!outIO) return false;
            toring = createDataFile(outIO);
            if(!toring) return false;

            AbstractIO * inIO = openRing(pubName.text());
            if(!inIO) return false;
            fromring = createDataFile(inIO);
            if(!fromring) return false;

            copyBFile(fromring, toring);

            CTClib::vf_close(toring);
            CTClib::vf_close(fromring);
            pubringBacked = true;
        }

        if(CTClib::secretChanged(theContext.keyRings) && !secringBacked)
        {
            FXString secName;
            secringName.getText(secName);

            FXString backupFile;
            getBackupName(backupFile, secName);

            AbstractIO * outIO = openRing(backupFile.text(), false);
            if(!outIO) return false;
            toring = createDataFile(outIO);
            if(!toring) return false;

            AbstractIO * inIO = openRing(secName.text());
            if(!inIO) return false;
            fromring = createDataFile(inIO);
            if(!fromring) return false;

            copyBFile(fromring, toring);

            CTClib::vf_close(toring);
            CTClib::vf_close(fromring);
            secringBacked = true;
        }
        return true;
    }


    void RootTreeItem::populate(void)
    {
        if(getFirst())
            return;

        tree->addItemLast(this,new PubringTreeItem(this));
        tree->addItemLast(this,new SecringTreeItem(this));
        tree->addItemLast(this,new MessageTreeItem(this));
    }

    void RootTreeItem::reap(void)
    {
        // reap key ring items
        FXTreeItem * child = getFirst();
        child->setExpanded(false);
        tree->removeItems(child->getFirst(),child->getLast());
        child = child->getNext();
        child->setExpanded(false);
        tree->removeItems(child->getFirst(),child->getLast());

        // and any signatures there may be on messages
        child = child->getNext();
        child = child->getFirst();
        if(child)
        {
            child->setExpanded(false);
            tree->removeItems(child->getFirst(),child->getLast());
        }
    }

    void RootTreeItem::setSelected(FXbool selected)
    {
        setOpened(selected);
    }

    void RootTreeItem::setOpened(FXbool opened)
    {
        getTreeResponder()->enablePubkeyActions(false);
        getTreeResponder()->enableSeckeyActions(false);
        getTreeResponder()->enableMessageActions(false);
        getTreeResponder()->enableModifiedMessageActions(false);
        getTreeResponder()->enableTextActions(false);
        getTreeResponder()->enableRingSignActions(false);

        FXTreeItem::setOpened(opened);
        if(opened)
        {
            HandledList * list = getTreeResponder()->getList();

            list->clearItems();

            list->setHeaderText(0, Strings::load(Strings::ITEM));
            list->setHeaderText(1, Strings::load(Strings::VALUE));

            populate();
            
            for(FXTreeItem * child = getFirst();
                child;
                child = child->getNext())
            {
                list->appendItem(child->getText(), child->getClosedIcon(), child->getClosedIcon());
            }
            
            FXString s, t;
            pubringName.getText(s);
            t = Strings::load(Strings::PUBRING);
            s = t+"\t"+s;
            list->appendItem(s.text(), getIconSource()->get(Icons::DOCUMENT), getIconSource()->get(Icons::DOCUMENT));

            secringName.getText(s);
            t = Strings::load(Strings::SECRING);
            s = t+"\t"+s;
            list->appendItem(s.text(), getIconSource()->get(Icons::DOCUMENT), getIconSource()->get(Icons::DOCUMENT));

            quoteString.getText(s);
            t = Strings::load(Strings::QUOTE);
            s = t+"\t"+s;
            list->appendItem(s.text(), getIconSource()->get(Icons::DOCUMENT), getIconSource()->get(Icons::DOCUMENT));

            list->setDoubleClickHandler(this);
            setExpanded(opened);
            getTreeResponder()->showList();
        }
    }

    void RootTreeItem::setExpanded(FXbool expanded)
    {
        if(expanded)
        {
            // populate each child first
            for(FXTreeItem * child = getFirst();
                child;
                child = child->getNext())
            {
                LazyContainer * c = dynamic_cast<LazyContainer *>(child);
                if(c)
                    c->populate();
            }
        }

        // now do the expansion
        FXTreeItem::setExpanded(expanded);

        if(!expanded)
            reap();
    }

    void RootTreeItem::lockAll(void)
    {
        setOpened(true);
        keylist->clearInternalisedKeys();
    }

    void RootTreeItem::logUnlocked(CTClib::seckey_T * key)
    {
        setOpened(true);
        keylist->logInternalisedKey(key);
    }

    void RootTreeItem::lock(CTClib::seckey_T * key)
    {
        setOpened(true);
        keylist->clearInternalisedKey(key);
    }

    void RootTreeItem::newMessage(FXSwitcher * s, FXString * str)
    {
        dynamic_cast<MessageTreeItem*>(getLast())->newMessage(s, str);
    }

    bool RootTreeItem::terminate(void)
    {
        return dynamic_cast<MessageTreeItem*>(getLast())->terminate();
    }

    void RootTreeItem::close(bool save)
    {
        dynamic_cast<MessageTreeItem*>(getLast())->close(save);
    }

    void RootTreeItem::saveItem(bool as)
    {
        dynamic_cast<MessageTreeItem*>(getLast())->saveItem(as);
    }


    void RootTreeItem::cut(void)
    {
        dynamic_cast<MessageTreeItem*>(getLast())->cut();
    }
    void RootTreeItem::copy(void)
    {
        dynamic_cast<MessageTreeItem*>(getLast())->copy();
    }
    void RootTreeItem::paste(void)
    {
        dynamic_cast<MessageTreeItem*>(getLast())->paste();
    }
    void RootTreeItem::quote(void)
    {
    	FXString qstr;
        quoteString.getText(qstr);
        dynamic_cast<MessageTreeItem*>(getLast())->quote(qstr);
    }
    void RootTreeItem::rot13(void)
    {
        dynamic_cast<MessageTreeItem*>(getLast())->rot13();
    }

    void RootTreeItem::decrypt(CTClib::decode_operation complete)
    {
        dynamic_cast<MessageTreeItem*>(getLast())->decrypt(complete);
    }

    CTClib::DataFileP RootTreeItem::getWritable()
    {
        return dynamic_cast<MessageTreeItem*>(getLast())->getWritable();
    }

    void RootTreeItem::extractPkey(void)
    {
        PubkeyTreeItem* current = dynamic_cast<PubkeyTreeItem*>(tree->getCurrentItem());
        if(current) current->extractPkey();
    }
    void RootTreeItem::ringSign(void)
    {
    	if(currentName)
        {
            currentName->sign();
            return;
        }
        PubkeyTreeItem* current = dynamic_cast<PubkeyTreeItem*>(tree->getCurrentItem());
        if(current) current->signPkey();
    }
    void RootTreeItem::enablePkey(void)
    {
        PubkeyTreeItem* current = dynamic_cast<PubkeyTreeItem*>(tree->getCurrentItem());
        if(current) current->enablePkey();
    }
    void RootTreeItem::deletePkey(void)
    {
        PubkeyTreeItem* current = dynamic_cast<PubkeyTreeItem*>(tree->getCurrentItem());
        if(current) 
        {
            current->deletePkey();
            tree->removeItem(current);
            current = NULL;
        }
    }

    //=========================================================
    
/*
FXDEFMAP(LazyContainer) LazyContainerMap[]=
{
  //________Message_Type_____________________ID____________Message_Handler_______
  FXMAPFUNC(SEL_DOUBLECLICKED,               0,            HandledList::onDoubleClick),
};
*/

// Macro for the class hierarchy implementation
FXIMPLEMENT(LazyContainer,FXTreeItem,NULL, 0);//HandledListMap,ARRAYNUMBER(HandledListMap))

    LazyContainer::LazyContainer(RootTreeItem * r, const char * s, FXIcon* o, FXIcon * c) :
        FXTreeItem(s, o, c),
        root(r)
    {
    }

    LazyContainer::~LazyContainer()
    {
    }


    LazyContainer::LazyContainer() :
        FXTreeItem(), root(NULL)
    {
    }

    long LazyContainer::onDoubleClick(HandledList *, int row)
    {
        FXTreeItem * child = getFirst();

        while(row--)
        {
            child = child->getNext();
            if(!child)
                return 0;
        }

        child->setOpened(true);
        return 1;
    }

    void LazyContainer::populate()
    {
    }

    void LazyContainer::setSelected(FXbool selected)
    {
        setOpened(selected);
    }

    void LazyContainer::setExpanded(FXbool expanded)
    {
        if(expanded)
        {
            // populate each child first
            for(FXTreeItem * child = getFirst();
                child;
                child = child->getNext())
            {
                LazyContainer * c = dynamic_cast<LazyContainer *>(child);
                if(c)
                    c->populate();
            }
        }

        // now do the expansion
        FXTreeItem::setExpanded(expanded);

        if(!expanded)
            reap();
    }

   void LazyContainer::reap()
   {
       for(FXTreeItem * child = getFirst();
           child;
           child = child->getNext())
       {
           root->tree->removeItems(child->getFirst(),child->getLast());
           child->setExpanded(false);
       }
   }

   void LazyContainer::setListModel(HandledList * list)
   {
        list->clearItems();

        list->setHeaderText(0, Strings::load(Strings::ITEM));
        list->setHeaderText(1, Strings::load(Strings::VALUE));

        populate();
        
        for(FXTreeItem * child = getFirst();
            child;
            child = child->getNext())
        {
            list->appendItem(child->getText(), child->getClosedIcon(), child->getClosedIcon());
        }

        list->setDoubleClickHandler(this);
        setExpanded(true);
   }

    void LazyContainer::setOpened(FXbool opened)
    {
        getTreeResponder()->enablePubkeyActions(false);
        getTreeResponder()->enableSeckeyActions(false);
        getTreeResponder()->enableMessageActions(false);
        getTreeResponder()->enableModifiedMessageActions(false);
        getTreeResponder()->enableTextActions(false);
        getTreeResponder()->enableRingSignActions(false);
        root->setCurrentName(NULL);
        FXTreeItem::setOpened(opened);
    }

    //=========================================================

/*
FXDEFMAP(PubringTreeItem) PubringTreeItemMap[]=
{
  //________Message_Type_____________________ID____________Message_Handler_______
  FXMAPFUNC(SEL_DOUBLECLICKED,               0,            HandledList::onDoubleClick),
};
*/

// Macro for the class hierarchy implementation
FXIMPLEMENT(PubringTreeItem,LazyContainer,NULL, 0);//HandledListMap,ARRAYNUMBER(HandledListMap))

    PubringTreeItem::PubringTreeItem(RootTreeItem * r) :
        LazyContainer(r, Strings::load(Strings::PUBKEYS),getIconSource()->get(Icons::OPENFILE),getIconSource()->get(Icons::FOLDER))
    {
    }

    PubringTreeItem::~PubringTreeItem()
    {
    }


    PubringTreeItem::PubringTreeItem() :
        LazyContainer()
    {
    }

    bool PubringTreeItem::keynameSort(CTClib::pubkey *k1, CTClib::pubkey *k2)
    {
        char text1[256], text2[256];
        memset(text1, 0, 256);
        memset(text2, 0, 256);

        CTClib::username * name = CTClib::firstName(k1);
        if(name)
        {
            CTClib::text_from_name(root->theContext.keyRings, name, text1);
        }
        name = CTClib::firstName(k2);
        if(name)
        {
            CTClib::text_from_name(root->theContext.keyRings, name, text2);
        }

        return collate(&text1[0], &text2[0]);
    }


    void PubringTreeItem::populate()
    {
        if(getFirst())
            return;

        if(! root->theContext.keyRings)
        {
            FXString filename;
            root->pubringName.getText(filename);

            AbstractIO * pubIO = FXFile::isReadable(filename) ?
    	    openRing(filename.text()) : 0;
        
            if(!pubIO)
                return;

            DataFileP pubFile = createDataFile(pubIO);

            if(!pubFile)
                return;

            root->theContext.keyRings = CTClib::init_userhash(pubFile);
        }

        if(! root->theContext.keyRings)
            return;

        // for each key, add...
        std::vector<CTClib::pubkey*> keys;
        for(CTClib::pubkey * key = CTClib::firstPubKey(root->theContext.keyRings);
            key;
            key = CTClib::nextPubKey(key))
        {
            keys.push_back(key);
        }

        // sort
        std::sort(keys.begin(), keys.end(), PubringTreeItem::KeynameSort(this));

        std::for_each(keys.begin(), keys.end(), PubkeyInserter(root, this));
    }

    void PubringTreeItem::setOpened(FXbool opened)
    {
        LazyContainer::setOpened(opened);
        if(opened)
        {
            HandledList * list = getTreeResponder()->getList();

            setListModel(list);

            getTreeResponder()->showList();
        }
    }

    //=========================================================

   
    PubringTreeItem::KeynameSort::KeynameSort(PubringTreeItem * p) : 
        parent(p) 
    {
    }

    bool PubringTreeItem::KeynameSort::operator() (CTClib::pubkey *k1, CTClib::pubkey *k2)
    {
        return parent->keynameSort(k1, k2);
    }

    PubkeyInserter::PubkeyInserter(RootTreeItem * arg, PubringTreeItem * arg2) :
        root(arg), node(arg2), context(&arg->theContext), icons(getIconSource())
        {}

    void PubkeyInserter::operator ( ) (CTClib::pubkey_T * key)
    {
        CTClib::username * name = CTClib::firstName(key);
        char text[256];
        memset(text, 0, 256);
        if(name)
        {
            CTClib::text_from_name(context->keyRings, name, text);
        }

        /*FXTreeItem * k =*/ root->tree->addItemLast(node, new PubkeyTreeItem(root, key));
    }

    //=========================================================
    
/*
FXDEFMAP(SecringTreeItem) SecringTreeItemMap[]=
{
  //________Message_Type_____________________ID____________Message_Handler_______
  FXMAPFUNC(SEL_DOUBLECLICKED,               0,            HandledList::onDoubleClick),
};
*/

// Macro for the class hierarchy implementation
FXIMPLEMENT(SecringTreeItem,LazyContainer,NULL, 0);//HandledListMap,ARRAYNUMBER(HandledListMap))

    SecringTreeItem::SecringTreeItem(RootTreeItem * r) :
        LazyContainer(r, Strings::load(Strings::SECKEYS),getIconSource()->get(Icons::OPENFILE),getIconSource()->get(Icons::FOLDER))
    {
    }

    SecringTreeItem::~SecringTreeItem()
    {
    }


    SecringTreeItem::SecringTreeItem() :
        LazyContainer()
    {
    }

    bool SecringTreeItem::keynameSort(CTClib::seckey *k1, CTClib::seckey *k2)
    {
        char text1[256], text2[256];
        memset(text1, 0, 256);
        memset(text2, 0, 256);

        CTClib::username * name = CTClib::firstName(CTClib::publicKey(k1));
        if(name)
        {
            CTClib::text_from_name(root->theContext.keyRings, name, text1);
        }
        name = CTClib::firstName(CTClib::publicKey(k2));
        if(name)
        {
            CTClib::text_from_name(root->theContext.keyRings, name, text2);
        }
        return collate(text1, text2);
    }


    void SecringTreeItem::populate()
    {
        if(getFirst())
            return;

        // ensure that the public key reing is set up
        if(! root->theContext.keyRings)
        {
            FXString filename;
            root->pubringName.getText(filename);

            AbstractIO * pubIO = FXFile::isReadable(filename) ?
    	    openRing(filename.text()) : 0;
        
            if(!pubIO)
                return;

            DataFileP pubFile = createDataFile(pubIO);

            if(!pubFile) //TODO close
                return;

            root->theContext.keyRings = CTClib::init_userhash(pubFile);
        }

        if(! root->theContext.keyRings)
            return;

        // now we can look at the secret keys
        if(!CTClib::firstSecKey(root->theContext.keyRings))
        {
            FXString filename;
            root->secringName.getText(filename);

            AbstractIO * secIO = FXFile::isReadable(filename) ?
    	    openRing(filename.text()) : 0;
        
            if(!secIO)
                return;

            DataFileP secFile = createDataFile(secIO);

            if(!secFile) //TODO close
                return;

            bool ok = CTClib::read_secring(secFile, root->theContext.keyRings) != 0;
            CTClib::vf_close(secFile);

            if(!ok)
                return;
        }

        // for each key, add...
        std::vector<CTClib::seckey*> keys;
        for(CTClib::seckey * key = CTClib::firstSecKey(root->theContext.keyRings);
            key;
            key = CTClib::nextSecKey(key))
        {
            keys.push_back(key);
        }

        // sort
        std::sort(keys.begin(), keys.end(), SecringTreeItem::KeynameSort(this));

        std::for_each(keys.begin(), keys.end(), SeckeyInserter(root, this));
    }

    void SecringTreeItem::setOpened(FXbool opened)
    {
        LazyContainer::setOpened(opened);
        if(opened)
        {
            HandledList * list = getTreeResponder()->getList();

            setListModel(list);

            getTreeResponder()->showList();
        }
    }

    //=========================================================

   
    SecringTreeItem::KeynameSort::KeynameSort(SecringTreeItem * p) : 
        parent(p) 
    {
    }

    bool SecringTreeItem::KeynameSort::operator() (CTClib::seckey *k1, CTClib::seckey *k2)
    {
        return parent->keynameSort(k1, k2);
    }

    SeckeyInserter::SeckeyInserter(RootTreeItem * arg, SecringTreeItem * arg2) :
        root(arg), node(arg2), context(&arg->theContext), icons(getIconSource())
        {}

    void SeckeyInserter::operator ( ) (CTClib::seckey_T * key)
    {
        CTClib::username * name = CTClib::firstName(CTClib::publicKey(key));
        char text[256];
        memset(text, 0, 256);
        if(name)
        {
            CTClib::text_from_name(context->keyRings, name, text);
        }

        /*FXTreeItem * k =*/ root->tree->addItemLast(node, new SeckeyTreeItem(root, key));
    }

    //=========================================================
/*
FXDEFMAP(PubkeyTreeItem) PubkeyTreeItemMap[]=
{
  //________Message_Type_____________________ID____________Message_Handler_______
  FXMAPFUNC(SEL_DOUBLECLICKED,               0,            HandledList::onDoubleClick),
};
*/

// Macro for the class hierarchy implementation
FXIMPLEMENT(PubkeyTreeItem,LazyContainer,NULL, 0);//HandledListMap,ARRAYNUMBER(HandledListMap))

    PubkeyTreeItem::PubkeyTreeItem(RootTreeItem * r, CTClib::pubkey * k) :
        LazyContainer(r, "",getIconSource()->get(Icons::UNLOCK),getIconSource()->get(Icons::UNLOCK)),
        key(k)
    {
        char text1[256];
        memset(text1, 0, 256);
        CTClib::username * name = CTClib::firstName(key);
        if(name)
        {
            CTClib::text_from_name(root->theContext.keyRings, name, text1);
        }
        setText(text1);
    }

    PubkeyTreeItem::~PubkeyTreeItem()
    {
    }

    PubkeyTreeItem::PubkeyTreeItem() :
        LazyContainer(),
        key(0)
    {
    }

    long PubkeyTreeItem::onDoubleClick(HandledList * l, int row)
    {
        long result = LazyContainer::onDoubleClick(l, row);
        return result;
    }

    void PubkeyTreeItem::draw(const FXTreeList* list,FXDC& dc,FXint x,FXint y,FXint w,FXint h) const
    {
    	// bit set => disabled key
        if(CTClib::ownTrust(key) & KTB_ENABLE_MASK)
        {
        	if(!root->getSlantFont())
        	{
        		FXFontDesc desc;
				list->getFont()->getFontDesc(desc);
 				desc.slant = FONTSLANT_ITALIC;                    /// Slant [normal, italic, oblique, ...]
        		FXFont * f2 = new FXFont(getApplication(), desc);
        		f2->create();
            	root->setSlantFont(f2);
        	}
        	dc.setFont(root->getSlantFont());
        }

        FXTreeItem::draw(list, dc, x, y, w, h);

        if(CTClib::ownTrust(key) & KTB_ENABLE_MASK)
        {
  			FXIcon *icon=(isOpened())?getOpenIcon():getClosedIcon();
            if(icon)
            {
#define SIDE_SPACING        4         // Spacing between side and item

            	dc.drawIconSunken(icon,
                  x+=SIDE_SPACING/2,
                  y+(h-icon->getHeight())/2);
    		}

        	FXFont * f = list->getFont();
        	dc.setFont(f);
        }
    }

    void PubkeyTreeItem::populate()
    {
        if(getFirst())
            return;

        // sub keys
        for(CTClib::pubkey * sub = CTClib::subKey(key);
            sub;
            sub = CTClib::subKey(key))
        {
            root->tree->addItemLast(this, new PubkeyTreeItem(root, sub));
        }

        // direct signatures
        for(CTClib::signature * sig = CTClib::revocation(key);
            sig;
            sig = CTClib::nextSig(sig))
            root->tree->addItemLast(this, new SignatureTreeItem(root, sig));

        // user names
        for(CTClib::username * name = CTClib::firstName(key);
            name;
            name = CTClib::nextName(name))
        {
            root->tree->addItemLast(this, new UsernameTreeItem(root, name, key));
        }
    }

    void PubkeyTreeItem::setOpened(FXbool opened)
    {
        LazyContainer::setOpened(opened);
        getTreeResponder()->enablePubkeyActions(opened != 0);
        getTreeResponder()->enableRingSignActions(opened != 0);
        if(opened)
        {
            HandledList * list = getTreeResponder()->getList();

            setListModel(list);

            CTClib::completeKey(key);

            FXString s(Strings::load(Strings::KEY_TYPE), "\t");
            byte alg = CTClib::getPubkeyAlg(key);
            s += Strings::libEnumName(CTClib::PKEALGOR, alg, true);
            list->appendItem(s.text(), getIconSource()->get(Icons::DOCUMENT), getIconSource()->get(Icons::DOCUMENT));

            s = Strings::load(Strings::KEY_BITS);
            s += "\t";
            s += FXStringVal(CTClib::keyLength(key));
            list->appendItem(s.text(), getIconSource()->get(Icons::DOCUMENT), getIconSource()->get(Icons::DOCUMENT));

            s = Strings::load(Strings::KEY_ID);
            s += "\t[";
            byte keyID[KEYFRAGSIZE];
            CTClib::extract_keyID(keyID, key);
            for(int iid=0; iid<KEYFRAGSIZE; ++iid)
            {
                if(KEYFRAGSIZE == iid+4)
                    s+= "] ";
                s += FXStringVal(keyID[iid], 16);
            }
            list->appendItem(s.text(), getIconSource()->get(Icons::DOCUMENT), getIconSource()->get(Icons::DOCUMENT));

            s = Strings::load(Strings::KEY_DATE);
            s += "\t";
            time_t created = CTClib::keyDate(key);
            struct tm * local = localtime(&created);
            s += asctime(local);
            s.trim();
            list->appendItem(s.text(), getIconSource()->get(Icons::DOCUMENT), getIconSource()->get(Icons::DOCUMENT));

            s = Strings::load(Strings::KEY_PRINT);
            s += "\t";

            byte md[MAXHASHSIZE];
            CTClib::fingerPrint(md, key); // v3 keyprint

            int limit = (CTClib::getPubkeyVersion(key) < VERSION_3) ? 16 : 20;
            for(int i = 0; i < limit; ++i)
            {
                s += FXStringVal(md[i], 16);
                if(1==i%2) s+= " ";
                if(limit/2 == i+1) s+= " ";
            }
            list->appendItem(s.text(), getIconSource()->get(Icons::DOCUMENT), getIconSource()->get(Icons::DOCUMENT));

            getTreeResponder()->showList();
        }
    }

    void PubkeyTreeItem::extractPkey(void)
    {
    	if(!key)
        	return;

        AbstractIO * a = openTemp(1000);
        if(!a)
            return;

        DataFileP string = createDataFile(a);
        if(!string)
            return;

        if(!CTClib::key_extract(string, key))
            return;

        long total = a->getLength();
        char * new_file = reinterpret_cast<char *>(CTClib::zmalloc(total+1));
        new_file[total] = 0;
        a->read(new_file, total);

        char keyName[260];
        CTClib::name_from_key(key, keyName);
        strcat(keyName, ".asc");

        TextTreeItem * key_asc = new TextTreeItem(
            root,
            keyName,
            new_file,
            getTreeResponder()->getSwitcher());

        root->tree->addItemLast(root->getLast(), key_asc);

        root->getLast()->setExpanded(true);
        root->getLast()->getLast()->setOpened(true);
    }

    void PubkeyTreeItem::signPkey(void)
    {
    }
    void PubkeyTreeItem::enablePkey(void)
    {
	    if(!key)
        	return;
    	CTClib::setTrust(key,
        	(byte)(CTClib::ownTrust(key)^KTB_ENABLE_MASK));
        // repaint this as there'll be a font swap.
		root->tree->update();            
    }
    void PubkeyTreeItem::deletePkey(void)
    {
	    if(!key)
        	return;
    	CTClib::removePubkey(root->theContext.keyRings, key);

    }

    //=========================================================

/*
FXDEFMAP(SeckeyTreeItem) SeckeyTreeItemMap[]=
{
  //________Message_Type_____________________ID____________Message_Handler_______
  FXMAPFUNC(SEL_DOUBLECLICKED,               0,            HandledList::onDoubleClick),
};
*/

// Macro for the class hierarchy implementation
FXIMPLEMENT(SeckeyTreeItem,LazyContainer,NULL, 0);//HandledListMap,ARRAYNUMBER(HandledListMap))

    SeckeyTreeItem::SeckeyTreeItem(RootTreeItem * r, CTClib::seckey * k) :
        LazyContainer(r, "",getIconSource()->get(Icons::KEY),getIconSource()->get(Icons::KEY)),
        key(k)
    {
        char text1[256];
        memset(text1, 0, 256);
        CTClib::username * name = CTClib::firstName(CTClib::publicKey(key));
        if(name)
        {
            CTClib::text_from_name(root->theContext.keyRings, name, text1);
        }
        setText(text1);
    }

    SeckeyTreeItem::~SeckeyTreeItem()
    {
    }

    SeckeyTreeItem::SeckeyTreeItem() :
        LazyContainer(),
        key(0)
    {
    }

    long SeckeyTreeItem::onDoubleClick(HandledList * l, int row)
    {
        long result = LazyContainer::onDoubleClick(l, row);
        return result;
    }

    void SeckeyTreeItem::populate()
    {
        if(getFirst())
            return;

        // sub keys - TODO - support

        // direct signatures - TODO - support

        // public key
        root->tree->addItemLast(this, new PubkeyTreeItem(root, CTClib::publicKey(key)));
    }

    void SeckeyTreeItem::setOpened(FXbool opened)
    {
        LazyContainer::setOpened(opened);
        getTreeResponder()->enableSeckeyActions(opened != 0);
        if(opened)
        {
            HandledList * list = getTreeResponder()->getList();

            setListModel(list);

            getTreeResponder()->showList();
        }
    }

    //=========================================================

/*
FXDEFMAP(UsernameTreeItem) UsernameTreeItemMap[]=
{
  //________Message_Type_____________________ID____________Message_Handler_______
  FXMAPFUNC(SEL_DOUBLECLICKED,               0,            HandledList::onDoubleClick),
};
*/

// Macro for the class hierarchy implementation
FXIMPLEMENT(UsernameTreeItem,LazyContainer,NULL, 0);//HandledListMap,ARRAYNUMBER(HandledListMap))

UsernameTreeItem::UsernameTreeItem(RootTreeItem * r, CTClib::username * k, CTClib::pubkey * pk) :
        LazyContainer(r, "",getIconSource()->get(Icons::USER),getIconSource()->get(Icons::USER)),
        key(k), pkey(pk)
    {
        char text1[256];
        memset(text1, 0, 256);
        CTClib::text_from_name(root->theContext.keyRings, key, text1);
        setText(text1);
    }

    UsernameTreeItem::~UsernameTreeItem()
    {
    }

    UsernameTreeItem::UsernameTreeItem() :
        LazyContainer(),
        key(0), pkey(0)
    {
    }

    long UsernameTreeItem::onDoubleClick(HandledList * l, int row)
    {
        long result = LazyContainer::onDoubleClick(l, row);
        return result;
    }

    void UsernameTreeItem::populate()
    {
        if(getFirst())
            return;

        // direct signatures
        for(CTClib::signature * sig = CTClib::firstSig(key);
            sig;
            sig = CTClib::nextSig(sig))
        {
            root->tree->addItemLast(this, new SignatureTreeItem(root, sig, pkey, key));
        }
    }

    void UsernameTreeItem::setOpened(FXbool opened)
    {
        LazyContainer::setOpened(opened);
        getTreeResponder()->enableRingSignActions(opened != 0);
        root->setCurrentName(this);
        if(opened)
        {
            HandledList * list = getTreeResponder()->getList();

            setListModel(list);

            getTreeResponder()->showList();
        }
    }

    void UsernameTreeItem::sign(void)
    {
        //TODO pub=parent key, fn is this name
        //CTClib::addSignature(pub, fn, SIG_KEY_CERT, sec, alg);
    }
    //=========================================================
    

/*
FXDEFMAP(SignatureTreeItem) SignatureTreeItemMap[]=
{
  //________Message_Type_____________________ID____________Message_Handler_______
  FXMAPFUNC(SEL_DOUBLECLICKED,               0,            HandledList::onDoubleClick),
};
*/

// Macro for the class hierarchy implementation
FXIMPLEMENT(SignatureTreeItem,LazyContainer,NULL, 0);//HandledListMap,ARRAYNUMBER(HandledListMap))

    SignatureTreeItem::SignatureTreeItem(RootTreeItem * r, CTClib::signature * k, CTClib::pubkey * pk, CTClib::username * name) :
LazyContainer(r, Strings::load(Strings::SIG_UNKNOWN),getIconSource()->get(Icons::MINUS),getIconSource()->get(Icons::MINUS)),
        key(k), fertile(true)
    {
        //TODO - output formatting; add the date from the signature
/*
        if(sig.toKey().getUsername().isNull())
        {
            return res.getString("Signature_by_unknown") + sig.getName();
        }
*/
        if(name && pk)
        {
            CTClib::sigValid state = CTClib::checkSignature(pk, name, key);
            if(CTClib::SIG_OKAY == state)
            {
                // TODO check revocations if not already done
                if(CTClib::secretKey(CTClib::signatory(key)))
                {
                    setOpenIcon(getIconSource()->get(Icons::PLUSPLUS));
                    setClosedIcon(getIconSource()->get(Icons::PLUSPLUS));
                    setText(Strings::load(Strings::SIG_ULTIMATE));
                }
                else
                {
                    setOpenIcon(getIconSource()->get(Icons::TICK));
                    setClosedIcon(getIconSource()->get(Icons::TICK));
                    setText(Strings::load(Strings::SIG_GOOD));
                }
            }
            else if(CTClib::SIG_BAD == state)
            {
                setOpenIcon(getIconSource()->get(Icons::CROSS));
                setClosedIcon(getIconSource()->get(Icons::CROSS));
                setText(Strings::load(Strings::SIG_BAD));
            }
            else if(CTClib::SIG_NO_KEY == state)
            {
                fertile = false;
            }
        }
/*
        boolean x = key.checkSignature(name, sig);
        if(x) setImages(good, good);
        else setImages(bad,bad);

        return (x ? res.getString("Good_signature_made") : res.getString("Bad_signature_made_at")) +
            sig.getName();
*/
    }

    SignatureTreeItem::~SignatureTreeItem()
    {
    }

    SignatureTreeItem::SignatureTreeItem() :
        LazyContainer(),
        key(0)
    {
    }

    long SignatureTreeItem::onDoubleClick(HandledList * l, int row)
    {
        long result = LazyContainer::onDoubleClick(l, row);
        return result;
    }

    void SignatureTreeItem::populate()
    {
        if(getFirst())
            return;

        // signing key if available
        if(fertile && CTClib::signatory(key))
            root->tree->addItemLast(this, new PubkeyTreeItem(root, CTClib::signatory(key)));
    }

    void SignatureTreeItem::setOpened(FXbool opened)
    {
        LazyContainer::setOpened(opened);
        if(opened)
        {
            HandledList * list = getTreeResponder()->getList();

            setListModel(list);

            FXString s(Strings::load(Strings::SIG_KEY_ID),"\t[");
            byte keyID[KEYFRAGSIZE];
            CTClib::extract_keyID(keyID, CTClib::signatory(key));
            for(int iid=0; iid<KEYFRAGSIZE; ++iid)
            {
                if(KEYFRAGSIZE == iid+4)
                    s+= "] ";
                s += FXStringVal(keyID[iid], 16);
            }
            list->appendItem(s.text(), getIconSource()->get(Icons::DOCUMENT), getIconSource()->get(Icons::DOCUMENT));

            s = Strings::load(Strings::SIG_DATE);
            s += "\t";
            time_t created = CTClib::getSignatureValue(key, CTClib::SIG_DATETIME, root->theContext.keyRings);

            struct tm * local = localtime(&created);
            s += asctime(local);
            s.trim();
            list->appendItem(s.text(), getIconSource()->get(Icons::DOCUMENT), getIconSource()->get(Icons::DOCUMENT));

            s = Strings::load(Strings::SIG_CLASS);
            s += "\t";
            uint32_t reason = CTClib::getSignatureValue(key, CTClib::SIG_CLASS, root->theContext.keyRings);
            s += Strings::libEnumName(CTClib::SIGCLASS, reason, true);
            list->appendItem(s.text(), getIconSource()->get(Icons::DOCUMENT), getIconSource()->get(Icons::DOCUMENT));

            getTreeResponder()->showList();
        }
    }

    //=========================================================
FXDEFMAP(MessageTreeItem) MessageTreeItemMap[]=
{
  //________Message_Type_____________________ID____________Message_Handler_______
  FXMAPFUNC(SEL_COMMAND,    MessageTreeItem::ID_SELALL,          MessageTreeItem::onSelAll),
  FXMAPFUNC(SEL_COMMAND,    MessageTreeItem::ID_SELNONE,         MessageTreeItem::onSelNone),
  FXMAPFUNC(SEL_SELECTED,   MessageTreeItem::ID_SELECT,          MessageTreeItem::onSelect),
  FXMAPFUNC(SEL_KEYPRESS,   MessageTreeItem::ID_SELECT,          MessageTreeItem::onKey),
};

// Macro for the class hierarchy implementation
FXIMPLEMENT(MessageTreeItem,LazyContainer,MessageTreeItemMap,ARRAYNUMBER(MessageTreeItemMap))

    MessageTreeItem::MessageTreeItem(RootTreeItem * r) :
        LazyContainer(r, Strings::load(Strings::MESSAGES),getIconSource()->get(Icons::OPENFILE),getIconSource()->get(Icons::FOLDER)),
        current(0),dlgList(NULL)
    {
    }

    MessageTreeItem::~MessageTreeItem()
    {
    }


    MessageTreeItem::MessageTreeItem() :
        LazyContainer(),
        current(0),dlgList(NULL)
    {
    }

    void MessageTreeItem::setOpened(FXbool opened)
    {
        LazyContainer::setOpened(opened);
        if(opened)
        {
            HandledList * list = getTreeResponder()->getList();

            setListModel(list);

            getTreeResponder()->showList();
        }
    }

    void MessageTreeItem::newMessage(FXSwitcher * s, FXString * file)
    {
        root->tree->addItemLast(this, new TextTreeItem(root, s, file));
        setExpanded(true);
        getLast()->setOpened(true);
    }

    CTClib::DataFileP MessageTreeItem::getWritable(void)
    {
        TextTreeItem * pad = new TextTreeItem(root, getTreeResponder()->getSwitcher(), NULL);
        root->tree->addItemLast(this, pad);
        setExpanded(true);
        getLast()->setOpened(true);
        return pad->getWritable();
    }

    bool MessageTreeItem::terminate(void)
    {
        FXDialogBox saveDlg(getApplication(),Strings::load(Strings::SAVE_MODIFIED),DECOR_TITLE|DECOR_BORDER,0,0,0,0, 0,0,0,0, 0,0);

        // Contents
        std::auto_ptr<FXVerticalFrame> contents(new 
            FXVerticalFrame(&saveDlg,LAYOUT_SIDE_RIGHT|FRAME_NONE|LAYOUT_FILL_X|LAYOUT_FILL_Y));

        std::auto_ptr<FXIconList> files(new
            FXIconList(contents.get(),this,ID_SELECT,LAYOUT_SIDE_TOP|LAYOUT_FILL_X|LAYOUT_FILL_Y|ICONLIST_SINGLESELECT));
        files->appendHeader(""); files->setHeaderSize(0, 160);
        files->appendHeader(""); files->setHeaderSize(1, 160);
        std::auto_ptr<FXHorizontalFrame> t2_contents(new 
            FXHorizontalFrame(contents.get(),LAYOUT_SIDE_TOP|FRAME_NONE|LAYOUT_FILL_X|LAYOUT_FILL_Y));

        bool save = false;

    	for(
        FXTreeItem * xchild = getFirst();
        xchild;
        xchild = xchild->getNext())
        {
            TextTreeItem * child = dynamic_cast<TextTreeItem*>(xchild);
            if(child->isModified())
            {
                save = true;
                FXString tail;
                child->getFirstLine(tail);
                FXString line = child->getText()+tail;
                files->appendItem(line, getIconSource()->get(Icons::TBLCHKON),
                    getIconSource()->get(Icons::TBLCHKON),child);
            }
        }

        std::auto_ptr<FXButton> cabutton(new FXButton(t2_contents.get(),Strings::load(Strings::CANCEL),
            NULL,&saveDlg,FXDialogBox::ID_CANCEL,BUTTON_INITIAL|BUTTON_DEFAULT|FRAME_RAISED|FRAME_THICK|LAYOUT_RIGHT,
            0,0,0,0,32,32,2,2));
        // OK button
        std::auto_ptr<FXButton> okbutton(new FXButton(t2_contents.get(),Strings::load(Strings::OK),
            NULL,&saveDlg,FXDialogBox::ID_ACCEPT,BUTTON_INITIAL|BUTTON_DEFAULT|FRAME_RAISED|FRAME_THICK|LAYOUT_RIGHT,
            0,0,0,0,32,32,2,2));
        okbutton->setFocus();

        std::auto_ptr<FXButton> snbutton(new FXButton(t2_contents.get(),Strings::load(Strings::SELECT_NONE),
            NULL,this,ID_SELNONE,BUTTON_INITIAL|BUTTON_DEFAULT|FRAME_RAISED|FRAME_THICK|LAYOUT_RIGHT,
            0,0,0,0,32,32,2,2));
        std::auto_ptr<FXButton> sabutton(new FXButton(t2_contents.get(),Strings::load(Strings::SELECT_ALL),
            NULL,this,ID_SELALL,BUTTON_INITIAL|BUTTON_DEFAULT|FRAME_RAISED|FRAME_THICK|LAYOUT_RIGHT,
            0,0,0,0,32,32,2,2));

        // nothing to do, so don't
        if(!save)
            return true;

        // did we cancel?
        dlgList = files.get();
        if(0 == saveDlg.execute(PLACEMENT_OWNER))
            return false;
        dlgList = NULL;

        int toDo = files->getNumItems();
        for(int file = 0; file<toDo; ++file)
        {
            TextTreeItem * child = reinterpret_cast<TextTreeItem*>(files->getItemData(file));
            child->close(files->getItemBigIcon(file)==getIconSource()->get(Icons::TBLCHKON));
        }

        return true;
    }

    void MessageTreeItem::close(bool save)
    {
        if(!current)
            return;

        TextTreeItem * child = dynamic_cast<TextTreeItem*>(current);
        if(!child)
            return;

        if(save && child->isModified())
        {
            // check if we need to save
            FXuint answer = FXMessageBox::question(getMainWindow(), MBOX_QUIT_SAVE_CANCEL,
                child->getText().text(), Strings::load(Strings::FILE_HAS_CHANGED));
            if(MBOX_CLICKED_CANCEL == answer)
                return;
            save = MBOX_CLICKED_SAVE == answer;
        }

        FXTreeItem * replace = current->getPrev();
        if(!replace) replace = current->getNext();

        dynamic_cast<TextTreeItem*>(current)->close(save);
        root->tree->removeItem(current);

        if(replace)
            replace->setSelected(true);
        else
            setSelected(true);
    }

    void MessageTreeItem::saveItem(bool as)
    {
        if(!current)
            return;

        TextTreeItem * child = dynamic_cast<TextTreeItem*>(current);
        if(!child)
            return;

        child->saveItem(as);
    }

    void MessageTreeItem::cut(void)
    {
        if(!current)
            return;

        TextTreeItem * child = dynamic_cast<TextTreeItem*>(current);
        if(!child)
            return;

        child->cut();
    }

    void MessageTreeItem::copy(void)
    {
        if(!current)
            return;

        TextTreeItem * child = dynamic_cast<TextTreeItem*>(current);
        if(!child)
            return;

        child->copy();
    }
    void MessageTreeItem::paste(void)
    {
        if(!current)
            return;

        TextTreeItem * child = dynamic_cast<TextTreeItem*>(current);
        if(!child)
            return;

        child->paste();
    }
    void MessageTreeItem::quote(const FXString & qstr)
    {
        if(!current)
            return;

        TextTreeItem * child = dynamic_cast<TextTreeItem*>(current);
        if(!child)
            return;

        child->quote(qstr);
    }
    void MessageTreeItem::rot13(void)
    {
        if(!current)
            return;

        TextTreeItem * child = dynamic_cast<TextTreeItem*>(current);
        if(!child)
            return;

        child->rot13();
    }

    void MessageTreeItem::decrypt(CTClib::decode_operation complete)
    {
        if(!current)
            return;

        TextTreeItem * child = dynamic_cast<TextTreeItem*>(current);
        if(!child)
            return;

        child->decrypt(complete);
    }

    long MessageTreeItem::onSelAll(FXObject*,FXSelector,void*)
    {
        int toDo = dlgList->getNumItems();
        for(int file = 0; file<toDo; ++file)
        {
            dlgList->setItemBigIcon(file, getIconSource()->get(Icons::TBLCHKON));
            dlgList->setItemMiniIcon(file, getIconSource()->get(Icons::TBLCHKON));
        }
        return 1;
    }
    long MessageTreeItem::onSelNone(FXObject*,FXSelector,void*)
    {
        int toDo = dlgList->getNumItems();
        for(int file = 0; file<toDo; ++file)
        {
            dlgList->setItemBigIcon(file, getIconSource()->get(Icons::TBLCHKOFF));
            dlgList->setItemMiniIcon(file, getIconSource()->get(Icons::TBLCHKOFF));
        }
        return 1;
    }

    long MessageTreeItem::onSelect(FXObject*,FXSelector,void*)
    {
        int toDo = dlgList->getNumItems();
        int nowSel = -1;
        for(int file = 0; file<toDo; ++file)
        {
            if(dlgList->getItem(file)->hasFocus())
            {
                nowSel = file;
                break;
            }
        }

        if(dlgList->getItemBigIcon(nowSel)==getIconSource()->get(Icons::TBLCHKON))
            dlgList->setItemBigIcon(nowSel, getIconSource()->get(Icons::TBLCHKOFF));
        else
            dlgList->setItemBigIcon(nowSel, getIconSource()->get(Icons::TBLCHKON));

        dlgList->setItemMiniIcon(nowSel, dlgList->getItemBigIcon(nowSel));

        return 1;
    }

    long MessageTreeItem::onKey(FXObject*,FXSelector,void*ptr)
    {
        FXEvent* event=(FXEvent*)ptr;
        if(event->code != KEY_Return)
            return 0;

        int toDo = dlgList->getNumItems();
        int nowSel = -1;
        for(int file = 0; file<toDo; ++file)
        {
            if(dlgList->getItem(file)->hasFocus())
            {
                nowSel = file;
                break;
            }
        }

        if(dlgList->getItemBigIcon(nowSel)==getIconSource()->get(Icons::TBLCHKON))
            dlgList->setItemBigIcon(nowSel, getIconSource()->get(Icons::TBLCHKOFF));
        else
            dlgList->setItemBigIcon(nowSel, getIconSource()->get(Icons::TBLCHKON));

        dlgList->setItemMiniIcon(nowSel, dlgList->getItemBigIcon(nowSel));

        return 1;
    }

    //=========================================================


FXDEFMAP(TextTreeItem) TextTreeItemMap[]=
{
  //________Message_Type_____________________ID____________Message_Handler_______
  FXMAPFUNC(SEL_UPDATE, TextTreeItem::ID_TEXT, TextTreeItem::onModified),
};


// Macro for the class hierarchy implementation
FXIMPLEMENT(TextTreeItem,LazyContainer,TextTreeItemMap,ARRAYNUMBER(TextTreeItemMap))


    TextTreeItem::TextTreeItem(RootTreeItem * r, FXSwitcher * sw, FXString * str, CTClib::signature * s) :
        LazyContainer(r, Strings::load(Strings::UNTITLED),getIconSource()->get(Icons::TEXT),getIconSource()->get(Icons::TEXT)),
        sig(s), text(0), switcher(sw), binary(false), canSave(str!=0),
        wasModified(-1),fileBuffer(NULL),length(0)
    {
        text = new FXText(switcher,this,ID_TEXT,LAYOUT_FILL_X|LAYOUT_FILL_Y|TEXT_SHOWACTIVE);
        if(text)
        {
            text->create();
            if(str)
            {
                setText(*str);
                loadFile(str, text);
                canSave = true;
            }
            text->setModified(false);
        }
    }



    TextTreeItem::TextTreeItem(RootTreeItem * r, const char * name, const char * body, FXSwitcher * sw) :
        LazyContainer(r, name,getIconSource()->get(Icons::TEXT),getIconSource()->get(Icons::TEXT)),
        sig(NULL), text(0), switcher(sw), binary(false), canSave(false),
        wasModified(-1),fileBuffer(NULL),length(0)
    {
        text = new FXText(switcher,this,ID_TEXT,LAYOUT_FILL_X|LAYOUT_FILL_Y|TEXT_SHOWACTIVE);
        if(text)
        {
            text->create();
            setText(name);
            text->setText(body);
            canSave = true;
            text->setModified(true);
        }
    }


    TextTreeItem::~TextTreeItem()
    {
    	if(fileBuffer)
        	FXFREE(fileBuffer);
        fileBuffer = NULL;
    }

    TextTreeItem::TextTreeItem() :
        LazyContainer(),
        sig(0), text(0), switcher(0), binary(false),
        wasModified(-1),fileBuffer(NULL),length(0)
    {
    }

    long TextTreeItem::onModified(FXObject*,FXSelector,void*)
    {
    	int isNow = isModified()?1:0;
        if(isNow != wasModified)
        	getTreeResponder()->enableModifiedMessageActions(isModified());
        wasModified = isNow;
        return 1;
    }

    bool TextTreeItem::isModified(void)
    {
        return text->isModified() != 0;
    }

    void TextTreeItem::cut(void)
    {
    	if(binary)
        	return;
        text->onCmdCutSel(NULL,0,NULL);
    }

    void TextTreeItem::copy(void)
    {
    	if(binary)
        	return;
        text->onCmdCopySel(NULL,0,NULL);
    }

    void TextTreeItem::paste(void)
    {
    	if(binary)
        	return;
        text->onCmdPasteSel(NULL,0,NULL);
    }

    void TextTreeItem::quote(const FXString & qstr)
    {
    	if(binary)
        	return;
  		FXint start = text->getSelStartPos();
		FXint end = text->getSelEndPos();
        FXint size = text->getLength();

        if(start == end)
        {
        	start = 0;
            end = size;
        }

        // work backwards so insertions don't affect positions
        start = text->lineStart(start);
       	FXint pos = text->lineStart(end);
        do {
            text->insertText(pos, qstr.text(), qstr.length());
            text->setModified(true);
            if(!pos) break;
            pos = text->prevLine(pos);
        } while(pos >= start);

    }

    void TextTreeItem::rot13(void)
    {
    	if(binary)
        	return;

  		FXint start = text->getSelStartPos();
		FXint end = text->getSelEndPos();
        FXint size = text->getLength();

        if(start == end)
        {
        	start = 0;
            end = size;
        }

        FXint delta = end - start;
        if(0 == delta)
        	return;

        FXString selected = text->getText().mid(start, delta);

		char * xtext = const_cast<char*>(selected.text());
        for(char * ptr = xtext; ptr<xtext+delta; ++ptr)
        {
        	int l = mblen(ptr, xtext+delta-ptr);
            if(l <= 0)
              continue;
            if(l > 1)
            {
            	ptr += l-1;
                continue;
            }
            if('A'<=*ptr && 'M'>=*ptr)
            	*ptr += 13;
            else if('a'<=*ptr && 'm'>=*ptr)
            	*ptr += 13;
            else if('N'<=*ptr && 'Z'>=*ptr)
            	*ptr -= 13;
            else if('n'<=*ptr && 'z'>=*ptr)
            	*ptr -= 13;

        }


        text->replaceText(start, delta, selected.text(),delta);
        text->setModified(true);
    }

    void TextTreeItem::decrypt(CTClib::decode_operation complete)
    {
        AbstractIO * data = NULL;
    	if(binary)
        {
        	if(!fileBuffer)
            	loadBinary(getText());
            data = openTemp(length);
            data->write(fileBuffer, length);
        }
        else
        {
            data = openTemp(text->getText().length());
            data->write(text->getText().text(), text->getText().length());
        }
        DataFileP in = createDataFile(data);
        CTClib::decode_context context = root->theContext;
        context.splitkey = complete;

        if(binary)
            CTClib::examine(in, &context);
        else
            CTClib::examine_text(in, &context);
    }

    void TextTreeItem::loadFile(FXString * filename, FXText * text)
    {
        std::FILE * fp=std::fopen(filename->text(),"rb");
        if(!fp)
            return;

        uint32_t size=FXFile::size(*filename);

        // TODO - need to have a proper test for binary;
        {
            byte buffer[1024];
            uint32_t chunk = sizeof(buffer);
            if(chunk > size) chunk = size;
            chunk = (uint32_t) std::fread(buffer,1,chunk,fp);
            uint32_t nprint = 0;

            for(uint32_t i=0; i<chunk; ++i)
            {
                if(!isprint(buffer[i])&&!isspace(buffer[i]))
                    ++nprint;
            }
            binary = nprint && ((chunk/nprint) < 20);
        }

        if(binary)
        {
            setOpenIcon(getIconSource()->get(Icons::BINARY));
            setClosedIcon(getIconSource()->get(Icons::BINARY));
            text->setText(Strings::load(Strings::BINARY_FILE));
            text->setEditable(false);
            // need to have a buffer ?
            // or read on demand?
        }
        else
        {
            std::fclose(fp);
            fp=std::fopen(filename->text(),"rt");

            char line[80];
            while( std::fgets(line, sizeof(line), fp) )
            {
                text->appendText(line, FXint(strlen(line)));
            }
        }
        std::fclose(fp);
        text->setModified(false);
    }

    void TextTreeItem::setOpened(FXbool opened)
    {
        LazyContainer::setOpened(opened);
        if(!opened)
            return;

        dynamic_cast<MessageTreeItem*>(getParent())->current = this;

        getTreeResponder()->enableMessageActions(true);
        getTreeResponder()->enableModifiedMessageActions(text->isModified() != 0);
        getTreeResponder()->enableTextActions(!binary);

        FXString tmp;
        getTreeResponder()->setTitle(tmp=getText());

        int index = find();
        if(index >= 0)
            switcher->setCurrent(index);

        wasModified = -1;
    }

    int TextTreeItem::find()
    {
        int index = 0;
        FXWindow * w = switcher->getFirst();
        do
        {
            if(w == static_cast<FXWindow *>(text))
            {
                return index;
            }
            ++index;
            w = w->getNext();
        }
        while(w);
        return -1;
    }

    void TextTreeItem::populate()
    {
        // TODO - if there's a signature, display it
    }

    void TextTreeItem::saveItem(bool as)
    {
        std::FILE * fp = 0;
        FXString filename = getText();
        FXString oldname = filename;
        // relay on lazy evaluation of this expression
        if(as || !canSave || (FXFile::exists(filename) && !FXFile::isWritable(filename))
            || 0 == (fp = std::fopen(getText().text(), binary?"wb":"wt")))
        {
            for(;;)
            {
                FXFileDialog f(getMainWindow(), Strings::load(Strings::SAVE_FILE));
                f.setFilename(filename);
                if(!f.execute(PLACEMENT_OWNER))
                    return;
                filename = f.getFilename();

                if(FXFile::exists(filename))
                {
                    FXuint answer = FXMessageBox::question(getMainWindow(), MBOX_YES_NO,
                        filename.text(), Strings::load(Strings::FILE_EXISTS));
                    if(MBOX_CLICKED_NO == answer)
                        continue;
                }

                // lazy again
                if( (FXFile::exists(filename) && !FXFile::isWritable(filename)) 
                    || 0 == (fp = std::fopen(filename.text(), binary?"wb":"wt")))
                {
                    FXMessageBox::information(getMainWindow(), MBOX_OK,
                        filename.text(), Strings::load(Strings::FILE_READONLY));
                    continue;
                }

                break;
            }

            setText(filename);
            canSave = true;
            FXString tmp;
            getTreeResponder()->setTitle(tmp=getText());
            getMainWindow()->update();
            root->tree->update();
        }

        if(!binary)
        {
        	FXString txt = text->getText();
        	std::fputs(txt.text(), fp);
        }
        else
        {
        	if(!fileBuffer)
            	loadBinary(oldname);
            if(length)
            	std::fwrite(fileBuffer, 1, length, fp);
        }
        std::fflush(fp);
        std::fclose(fp);
        text->setModified(false);
        getTreeResponder()->enableModifiedMessageActions(false);
    }


    void TextTreeItem::loadBinary(const FXString & filename)
    {
    	if(fileBuffer || length)
        	return;

        std::FILE * fp=std::fopen(filename.text(),"rb");
        if(!fp)
            return;

        length=FXFile::size(filename);
        FXCALLOC(&fileBuffer,FXuchar,length);

        length = FXuint( std::fread(fileBuffer,1,length,fp) );
        std::fclose(fp);
    }

    void TextTreeItem::close(bool saveMe)
    {
        if(saveMe)
            saveItem();
        delete text;
    }

    void TextTreeItem::getFirstLine(FXString & line)
    {
        line = "";
        if(!text)
            return;
        int length = text->getLength();
        for(int pos =0; pos < length; pos=text->lineEnd(pos)+1)
        {
            FXchar buffer[81];
            int start = text->lineStart(pos);
            int delta = text->lineEnd(pos) - start;
            if(delta < 1) 
                continue;
            if(delta > 80) delta=80;
            buffer[delta]=0;
            text->extractText(buffer, pos, delta);
            line = buffer;
            line.trim();
            if(line.length() > 0)
                break;
        }
        if(line.length() > 0)
            line = "\t"+line;
    }

    CTClib::DataFileP TextTreeItem::getWritable(void)
    {
        if(!text)
            return NULL;
        AbstractIO * wrapper = openFXText(text); // write only
        return createDataFile(wrapper);
    }

    //=========================================================
    //=========================================================
    //=========================================================

    class KeyListDeleter
    {
    public:
        KeyListDeleter();
        void operator ( ) (seckey_T *);
    };

    KeyListDeleter::KeyListDeleter()
    {}
    void KeyListDeleter::operator ( ) (seckey_T * p)
    {
        CTClib::externalise_seckey(p);
    }

    //=========================================================

    KeyList::KeyList()
    {}

    KeyList::~KeyList()
    {}

    void KeyList::logInternalisedKey(seckey_T * key)
    {
        list.insert(list.begin(), key);
    }

    void KeyList::clearInternalisedKey(seckey_T * key)
    {
        keylist::iterator i = list.find(key);
        if(i != list.end())
        {
            CTClib::externalise_seckey(*i);
            list.erase(i);
        }
    }

    void KeyList::clearInternalisedKeys()
    {
        if(list.empty())
            return;
        std::for_each(list.begin(), list.end(), KeyListDeleter());
        list.erase(list.begin(), list.end());
    }
} // end namespace
