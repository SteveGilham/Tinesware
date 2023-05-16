/* ctcfox.cpp : Defines the entry point for the application.
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

#include <memory>
#include <algorithm>
#include <clocale>
#include <vector>

#include "ctcfox.h"
#include "../strings/strings.h"

#include "abstract.h"
#include "licences.h"
#include "ctc.h"

#include <fx.h>

#include "icons.h"
#include "splash.h"

#include "Registrar.h"
#include "HandledList.h"
#include "keytree.h"

using std::auto_ptr;

static FXApp * theApp;
static FXMainWindow * theMainWindow;
static CTCFox::Icons * theIconSource;
static CTCFox::Registrar * registrar;

namespace CTCFox
{
    //=========================================================

    // Main Window; this doesn't exactly hew to the Liskov Substitution Principle
    class CTCFoxWindow : public FXMainWindow, public KeyTreeResponder
    {

    // Macro for class hierarchy declarations
    FXDECLARE(CTCFoxWindow)

    private:

        FXMenuBar           *menubar;
        FXToolBar           *toolbar;
        FXStatusBar         *statusbar;

        auto_ptr<FXMenuPane> filemenu;  
        auto_ptr<FXMenuPane> keysmenu;  
        auto_ptr<FXMenuPane> helpmenu;  
        auto_ptr<FXMenuPane> editmenu;  
        auto_ptr<FXMenuPane> cryptomenu;

        FXSplitter          *splitter;
        FXVerticalFrame     *left;
        FXVerticalFrame     *right;

        FXSwitcher          *switcher;
        HandledList         *list;

        Icons             & iconSource;

        std::vector<FXWindow *>  messageActions;
        std::vector<FXWindow *>  textActions;
        std::vector<FXWindow *>  pubkeyActions;
        std::vector<FXWindow *>  seckeyActions;
        std::vector<FXWindow *>  modifiedMessageActions;
        std::vector<FXWindow *>  ringsignActions;

        MutableTextProperty * pubringName;
        MutableTextProperty * secringName;

        FXTreeList *        tree;

    protected:
        CTCFoxWindow() : iconSource(*(new Icons())) {}

    public:

    // Message handlers
    long onCmdExit(FXObject*,FXSelector,void*);
    long onCmdAbout(FXObject*,FXSelector,void*);
    long onCmdLockAll(FXObject*,FXSelector,void*);
    long onCmdGenerate(FXObject*,FXSelector,void*);

    long onCmdNewDoc(FXObject*,FXSelector,void*);
    long onCmdOpenDoc(FXObject*,FXSelector,void*);
    long onCmdCloseDoc(FXObject*,FXSelector,void*);
    long onCmdSaveDoc(FXObject*,FXSelector,void*);
    long onCmdSaveDocAs(FXObject*,FXSelector,void*);

    long onCmdCut(FXObject*,FXSelector,void*);
    long onCmdCopy(FXObject*,FXSelector,void*);
    long onCmdPaste(FXObject*,FXSelector,void*);
    long onCmdQuote(FXObject*,FXSelector,void*);
    long onCmdRot13(FXObject*,FXSelector,void*);

    long onCmdExtractPkey(FXObject*,FXSelector,void*);
    long onCmdSignPkey(FXObject*,FXSelector,void*);
    long onCmdEnablePkey(FXObject*,FXSelector,void*);
    long onCmdDeletePkey(FXObject*,FXSelector,void*);

    long onCmdDecrypt(FXObject*,FXSelector,void*);
    long onCmdDecryptKey(FXObject*,FXSelector,void*);

    public:

    // Messages for our class
    enum{
        ID_NEW=FXMainWindow::ID_LAST,
        ID_OPEN,
        ID_READ_POP3,
        ID_SAVE,
        ID_SAVE_AS,
        ID_EXIT,
        ID_MAILOUT,
        ID_PRINT,
        ID_CUT,
        ID_COPY,
        ID_PASTE,
        ID_UNLOCK,
        ID_LOCK,
        ID_HELP,
        ID_GENERATE,
        ID_LOCKALL,
        ID_ABT_TAB,
        ID_TABLE,
        ID_EXTRACT,
        ID_SIGN,
        ID_ENABLE,
        ID_DELKEY,
        ID_KEYLOCK,
        ID_REVOKE,
        ID_ADDID,
        ID_PASSPHRASE,
        ID_ROT13,
        ID_QUOTE,
        ID_EXKEY,
        ID_CLEARSIGN,
        ID_DETSIG,
        ID_SIGNONLY,
        ID_CLOSE,
        ID_LAST
        };
        
    public:

        // CTCFoxWindow's constructor
        CTCFoxWindow(Icons & i, FXApp* a);
          
        // Initialize
        virtual void create(void);

        //destroy
        virtual ~CTCFoxWindow(void);

    public:

        FXStatusBar * getStatusBar(void);

        void enableMessageActions(bool enable = true);
        void enableModifiedMessageActions(bool enable = true);
        void enablePubkeyActions(bool enable = true);
        void enableRingSignActions(bool enable = true);
        void enableSeckeyActions(bool enable = true);
        void enableTextActions(bool enable = true);
        HandledList * getList(void);
        void showList(void);
        void setTitle(FXString & s);
        FXSwitcher * getSwitcher(void);
        RootTreeItem * getTreeRoot(void);

    private:
        void markToolbar(void);
    };

    //=========================================================

    class WindowEnabler
    {
    public:
        WindowEnabler(bool value = true);
        void operator ( ) (FXWindow * w);
    private:
        bool able;
    };

    WindowEnabler::WindowEnabler(bool value)
        :able(value)
    {}
    void WindowEnabler::operator ( ) (FXWindow * w)
    {
        if(w)
        {
            if(able) w->enable();
            else w->disable();
        }
    }

    //=========================================================

    class ListWidthProperty : public IntProperty
    {
    private:
        HandledList * table;
        int  c;
    public:
        ListWidthProperty(const char * section, const char * key, int def,
            HandledList* t, int col)
            : IntProperty(section, key, def), table(t), c(col)
        {}
    protected:
        virtual void getInt(int & value) const
        {
            value = table->getHeaderSize(c);
        }
        virtual void setInt(int value)
        {
            table->setHeaderSize(c,value);
        }
    };

    //=========================================================


// Message Map for the CTCFox Window class

FXDEFMAP(CTCFoxWindow) CTCFoxWindowMap[]=
{
  //________Message_Type_____________________ID____________Message_Handler_______
  FXMAPFUNC(SEL_COMMAND,        CTCFoxWindow::ID_EXIT,     CTCFoxWindow::onCmdExit),
  FXMAPFUNC(SEL_COMMAND,        CTCFoxWindow::ID_HELP,     CTCFoxWindow::onCmdAbout),
  FXMAPFUNC(SEL_COMMAND,        CTCFoxWindow::ID_LOCKALL,  CTCFoxWindow::onCmdLockAll),
  FXMAPFUNC(SEL_COMMAND,        CTCFoxWindow::ID_GENERATE, CTCFoxWindow::onCmdGenerate),
  FXMAPFUNC(SEL_COMMAND,        CTCFoxWindow::ID_NEW,      CTCFoxWindow::onCmdNewDoc),
  FXMAPFUNC(SEL_COMMAND,        CTCFoxWindow::ID_OPEN,     CTCFoxWindow::onCmdOpenDoc),
  FXMAPFUNC(SEL_COMMAND,        CTCFoxWindow::ID_CLOSE,    CTCFoxWindow::onCmdCloseDoc),
  FXMAPFUNC(SEL_COMMAND,        CTCFoxWindow::ID_SAVE,     CTCFoxWindow::onCmdSaveDoc),
  FXMAPFUNC(SEL_COMMAND,        CTCFoxWindow::ID_SAVE_AS,  CTCFoxWindow::onCmdSaveDocAs),
  FXMAPFUNC(SEL_COMMAND,        CTCFoxWindow::ID_CUT,      CTCFoxWindow::onCmdCut),
  FXMAPFUNC(SEL_COMMAND,        CTCFoxWindow::ID_COPY,     CTCFoxWindow::onCmdCopy),
  FXMAPFUNC(SEL_COMMAND,        CTCFoxWindow::ID_PASTE,    CTCFoxWindow::onCmdPaste),
  FXMAPFUNC(SEL_COMMAND,        CTCFoxWindow::ID_QUOTE,    CTCFoxWindow::onCmdQuote),
  FXMAPFUNC(SEL_COMMAND,        CTCFoxWindow::ID_ROT13,    CTCFoxWindow::onCmdRot13),
  FXMAPFUNC(SEL_COMMAND,        CTCFoxWindow::ID_EXTRACT,  CTCFoxWindow::onCmdExtractPkey),
  FXMAPFUNC(SEL_COMMAND,        CTCFoxWindow::ID_SIGN,     CTCFoxWindow::onCmdSignPkey),
  FXMAPFUNC(SEL_COMMAND,        CTCFoxWindow::ID_ENABLE,   CTCFoxWindow::onCmdEnablePkey),
  FXMAPFUNC(SEL_COMMAND,        CTCFoxWindow::ID_DELKEY,   CTCFoxWindow::onCmdDeletePkey),
  FXMAPFUNC(SEL_COMMAND,        CTCFoxWindow::ID_UNLOCK,   CTCFoxWindow::onCmdDecrypt),
  FXMAPFUNC(SEL_COMMAND,        CTCFoxWindow::ID_EXKEY,    CTCFoxWindow::onCmdDecryptKey),
};

} // end namespace

char * appTitleString =
#ifndef NO_IDEA
"CTCFox (IDEA enabled)"
#else
"CTCFox (IDEA free)"
#endif
;


    //=========================================================


namespace CTCFox 
{

// Macro for the class hierarchy implementation
FXIMPLEMENT(CTCFoxWindow,FXMainWindow,CTCFoxWindowMap,ARRAYNUMBER(CTCFoxWindowMap))


// Destroy a CTCFoxWindow
CTCFoxWindow::~CTCFoxWindow(void)
{
}

// Construct a CTCFoxWindow
CTCFoxWindow::CTCFoxWindow(Icons & i, FXApp *a)
    :   FXMainWindow(a,appTitleString,NULL,NULL,DECOR_ALL,0,0,800,600)
    , iconSource(i)
{

    setIcon(iconSource.get(Icons::FRAME_ICON));

   // Make menu bar
    FXToolBarShell* dragshell1=new FXToolBarShell(this,FRAME_RAISED);
    menubar=new FXMenuBar(this,dragshell1,LAYOUT_SIDE_TOP|LAYOUT_FILL_X|FRAME_RAISED);
    new FXToolBarGrip(menubar,menubar,FXMenuBar::ID_TOOLBARGRIP,TOOLBARGRIP_DOUBLE);

    // Tool bar
    FXToolBarShell* dragshell2=new FXToolBarShell(this,FRAME_RAISED);
    toolbar=new FXToolBar(this,dragshell2,LAYOUT_SIDE_TOP|LAYOUT_FILL_X|FRAME_RAISED);
    new FXToolBarGrip(toolbar,toolbar,FXToolBar::ID_TOOLBARGRIP,TOOLBARGRIP_DOUBLE);

    // Status bar
    statusbar=new FXStatusBar(this,LAYOUT_SIDE_BOTTOM|LAYOUT_FILL_X|STATUSBAR_WITH_DRAGCORNER|FRAME_RAISED);

    // File menu
    filemenu.reset(new FXMenuPane(this));
    new FXMenuTitle(menubar,Strings::load(Strings::FILE),NULL,filemenu.get());
    // File Menu entries
    new FXMenuCommand(filemenu.get(),Strings::load(Strings::NEW),iconSource.get(Icons::NEWFILE),this,ID_NEW);
    new FXMenuCommand(filemenu.get(),Strings::load(Strings::OPEN),iconSource.get(Icons::OPENFILE),this,ID_OPEN);
    FXMenuCommand * mc =
    new FXMenuCommand(filemenu.get(),Strings::load(Strings::READPOP3),iconSource.get(Icons::MAILIN),this,ID_READ_POP3);
    mc->disable();
    new FXMenuSeparator(filemenu.get());

    FXWindow * tmp;
    modifiedMessageActions.push_back(tmp=
    new FXMenuCommand(filemenu.get(),Strings::load(Strings::SAVE),iconSource.get(Icons::SAVE),this,ID_SAVE));
    messageActions.push_back(tmp=
    new FXMenuCommand(filemenu.get(),Strings::load(Strings::SAVEAS),NULL,this,ID_SAVE_AS));
    new FXMenuSeparator(filemenu.get());
    //textActions.push_back(tmp=
    mc =
    new FXMenuCommand(filemenu.get(),Strings::load(Strings::SEND),iconSource.get(Icons::MAILOUT),this,ID_MAILOUT)/*)*/;
    mc->disable();
    //textActions.push_back(tmp=
    mc =
    new FXMenuCommand(filemenu.get(),Strings::load(Strings::PRINT),iconSource.get(Icons::PRINT),this,ID_PRINT)/*)*/;
    mc->disable();
    new FXMenuSeparator(filemenu.get());
    messageActions.push_back(tmp=
    new FXMenuCommand(filemenu.get(),Strings::load(Strings::CLOSE),NULL,this,ID_CLOSE));
    new FXMenuSeparator(filemenu.get());
    new FXMenuCommand(filemenu.get(),Strings::load(Strings::EXIT),NULL,this,ID_EXIT);

    //Edit menu
    editmenu.reset(new FXMenuPane(this));
    textActions.push_back(tmp=
    new FXMenuTitle(menubar,Strings::load(Strings::EDIT),NULL,editmenu.get()));
    textActions.push_back(tmp=
    new FXMenuCommand(editmenu.get(),Strings::load(Strings::CUT),iconSource.get(Icons::CUT),this,ID_CUT));
    textActions.push_back(tmp=
    new FXMenuCommand(editmenu.get(),Strings::load(Strings::COPY),iconSource.get(Icons::COPY),this,ID_COPY));
    textActions.push_back(tmp=
    new FXMenuCommand(editmenu.get(),Strings::load(Strings::PASTE),iconSource.get(Icons::PASTE),this,ID_PASTE));
    new FXMenuSeparator(editmenu.get());
    textActions.push_back(tmp=
    new FXMenuCommand(editmenu.get(),Strings::load(Strings::ROT),NULL,this,ID_ROT13));
    textActions.push_back(tmp=
    new FXMenuCommand(editmenu.get(),Strings::load(Strings::ENQUOTE),NULL,this,ID_QUOTE));


    //Crypto menu
    cryptomenu.reset(new FXMenuPane(this));
    messageActions.push_back(tmp=
    new FXMenuTitle(menubar,Strings::load(Strings::CRYPTO),NULL,cryptomenu.get()));
    messageActions.push_back(tmp=
    new FXMenuCommand(cryptomenu.get(),Strings::load(Strings::DECRYPT),iconSource.get(Icons::UNLOCK),this,ID_UNLOCK));
    messageActions.push_back(tmp=
    new FXMenuCommand(cryptomenu.get(),Strings::load(Strings::EXKEY),iconSource.get(Icons::KEY),this,ID_EXKEY));
    new FXMenuSeparator(cryptomenu.get());
    messageActions.push_back(tmp=
    new FXMenuCommand(cryptomenu.get(),Strings::load(Strings::ENCRYPT),iconSource.get(Icons::LOCK),this,ID_LOCK));
    textActions.push_back(tmp=
    new FXMenuCommand(cryptomenu.get(),Strings::load(Strings::CLEARSIGN),NULL,this,ID_CLEARSIGN));
    messageActions.push_back(tmp=
    new FXMenuCommand(cryptomenu.get(),Strings::load(Strings::DETSIG),NULL,this,ID_DETSIG));
    messageActions.push_back(tmp=
    new FXMenuCommand(cryptomenu.get(),Strings::load(Strings::SIGNONLY),NULL,this,ID_SIGNONLY));

    // Keys menu
    keysmenu.reset(new FXMenuPane(this));
    new FXMenuTitle(menubar,Strings::load(Strings::KEYS),NULL,keysmenu.get());
    // Keys Menu entries
    new FXMenuCommand(keysmenu.get(),Strings::load(Strings::GENERATE),NULL,this,ID_GENERATE);
    new FXMenuCommand(keysmenu.get(),Strings::load(Strings::LOCKALL),NULL,this,ID_LOCKALL);

    new FXMenuSeparator(keysmenu.get());

    pubkeyActions.push_back(tmp=
    new FXMenuCommand(keysmenu.get(),Strings::load(Strings::EXTRACT),NULL,this,ID_EXTRACT));
    ringsignActions.push_back(tmp=
    new FXMenuCommand(keysmenu.get(),Strings::load(Strings::SIGN),NULL,this,ID_SIGN));
    pubkeyActions.push_back(tmp=
    new FXMenuCommand(keysmenu.get(),Strings::load(Strings::ENABLE),NULL,this,ID_ENABLE));
    pubkeyActions.push_back(tmp=
    new FXMenuCommand(keysmenu.get(),Strings::load(Strings::DELKEY),NULL,this,ID_DELKEY));

    new FXMenuSeparator(keysmenu.get());

    seckeyActions.push_back(tmp=
    new FXMenuCommand(keysmenu.get(),Strings::load(Strings::KEYLOCK),NULL,this,ID_KEYLOCK));
    seckeyActions.push_back(tmp=
    new FXMenuCommand(keysmenu.get(),Strings::load(Strings::REVOKE),NULL,this,ID_REVOKE));
    seckeyActions.push_back(tmp=
    new FXMenuCommand(keysmenu.get(),Strings::load(Strings::ADDID),NULL,this,ID_ADDID));
    seckeyActions.push_back(tmp=
    new FXMenuCommand(keysmenu.get(),Strings::load(Strings::PASSPHRASE),NULL,this,ID_PASSPHRASE));

    // Keys menu
    helpmenu.reset(new FXMenuPane(this));
    new FXMenuTitle(menubar,Strings::load(Strings::HELP),NULL,helpmenu.get());
    // Keys Menu entries
    new FXMenuCommand(helpmenu.get(),Strings::load(Strings::ABOUT),NULL,this,ID_HELP);


    // Toolbar
    new FXButton(toolbar,Strings::load2nd(Strings::NEW_TOOL),iconSource.get(Icons::NEWFILE),this,
        ID_NEW,ICON_ABOVE_TEXT|BUTTON_TOOLBAR|FRAME_RAISED|LAYOUT_TOP|LAYOUT_LEFT);
    new FXButton(toolbar,Strings::load2nd(Strings::OPEN_TOOL),iconSource.get(Icons::OPENFILE),this,
        ID_OPEN,ICON_ABOVE_TEXT|BUTTON_TOOLBAR|FRAME_RAISED|LAYOUT_TOP|LAYOUT_LEFT);

    FXButton * b =
    new FXButton(toolbar,Strings::load2nd(Strings::READPOP3_TOOL),iconSource.get(Icons::MAILIN),this,
        ID_READ_POP3,ICON_ABOVE_TEXT|BUTTON_TOOLBAR|FRAME_RAISED|LAYOUT_TOP|LAYOUT_LEFT);
    b->disable();

    markToolbar();

    modifiedMessageActions.push_back(tmp=
    new FXButton(toolbar,Strings::load2nd(Strings::SAVE_TOOL),iconSource.get(Icons::SAVE),this,
        ID_SAVE,ICON_ABOVE_TEXT|BUTTON_TOOLBAR|FRAME_RAISED|LAYOUT_TOP|LAYOUT_LEFT));
    //messageActions.push_back(tmp=
    b =
    new FXButton(toolbar,Strings::load2nd(Strings::SMTP_TOOL),iconSource.get(Icons::MAILOUT),this,
        ID_MAILOUT,ICON_ABOVE_TEXT|BUTTON_TOOLBAR|FRAME_RAISED|LAYOUT_TOP|LAYOUT_LEFT)/*)*/;
    b->disable();

    markToolbar();

    //textActions.push_back(tmp=
    b =
    new FXButton(toolbar,Strings::load2nd(Strings::PRINT_TOOL),iconSource.get(Icons::PRINT),this,
        ID_PRINT,ICON_ABOVE_TEXT|BUTTON_TOOLBAR|FRAME_RAISED|LAYOUT_TOP|LAYOUT_LEFT)/*)*/;
    b->disable();

    markToolbar();

    textActions.push_back(tmp=
    new FXButton(toolbar,Strings::load2nd(Strings::CUT_TOOL),iconSource.get(Icons::CUT),this,
        ID_CUT,ICON_ABOVE_TEXT|BUTTON_TOOLBAR|FRAME_RAISED|LAYOUT_TOP|LAYOUT_LEFT));
    textActions.push_back(tmp=
    new FXButton(toolbar,Strings::load2nd(Strings::COPY_TOOL),iconSource.get(Icons::COPY),this,
        ID_COPY,ICON_ABOVE_TEXT|BUTTON_TOOLBAR|FRAME_RAISED|LAYOUT_TOP|LAYOUT_LEFT));
    textActions.push_back(tmp=
    new FXButton(toolbar,Strings::load2nd(Strings::PASTE_TOOL),iconSource.get(Icons::PASTE),this,
        ID_PASTE,ICON_ABOVE_TEXT|BUTTON_TOOLBAR|FRAME_RAISED|LAYOUT_TOP|LAYOUT_LEFT));

    markToolbar();

    messageActions.push_back(tmp=
    new FXButton(toolbar,Strings::load2nd(Strings::UNLOCK_TOOL),iconSource.get(Icons::UNLOCK),this,
        ID_UNLOCK,ICON_ABOVE_TEXT|BUTTON_TOOLBAR|FRAME_RAISED|LAYOUT_TOP|LAYOUT_LEFT));
    messageActions.push_back(tmp=
    new FXButton(toolbar,Strings::load2nd(Strings::LOCK_TOOL),iconSource.get(Icons::LOCK),this,
        ID_LOCK,ICON_ABOVE_TEXT|BUTTON_TOOLBAR|FRAME_RAISED|LAYOUT_TOP|LAYOUT_LEFT));

    markToolbar();

    new FXButton(toolbar,Strings::load2nd(Strings::HELP_TOOL),iconSource.get(Icons::HELP),this,
        ID_HELP,ICON_ABOVE_TEXT|BUTTON_TOOLBAR|FRAME_RAISED|LAYOUT_TOP|LAYOUT_LEFT);

    // Default disable state
    enableTextActions(false);
    enableMessageActions(false);
    enableModifiedMessageActions(false);
    enablePubkeyActions(false);
    enableSeckeyActions(false);
    enableRingSignActions(false);

    // Make a tool tip
    new FXToolTip(getApp(),0);

    // contents
    splitter=new FXSplitter(this,LAYOUT_SIDE_TOP|LAYOUT_FILL_X|LAYOUT_FILL_Y|SPLITTER_REVERSED|SPLITTER_TRACKING);
    left  = new FXVerticalFrame(splitter,FRAME_SUNKEN|FRAME_THICK|LAYOUT_FILL_X|LAYOUT_FILL_Y, 0,0,0,0, 0,0,0,0);
    right = new FXVerticalFrame(splitter,FRAME_SUNKEN|FRAME_THICK|LAYOUT_FILL_X|LAYOUT_FILL_Y, 0,0,620,0, 0,0,0,0);

    tree=new FXTreeList(left,0,NULL,0,
        FRAME_SUNKEN|FRAME_THICK|LAYOUT_FILL_X|LAYOUT_FILL_Y|LAYOUT_TOP|LAYOUT_RIGHT|
        TREELIST_SHOWS_LINES|TREELIST_SHOWS_BOXES|TREELIST_ROOT_BOXES|TREELIST_EXTENDEDSELECT);

    RootTreeItem * root = new RootTreeItem(tree);
    tree->addItemLast(0,root);
    root->populate();

    // Switcher
    switcher=new FXSwitcher(right,LAYOUT_FILL_X|LAYOUT_FILL_Y|LAYOUT_RIGHT);

    list = new HandledList(switcher);

    registrar->accept(new ListWidthProperty("Settings", "Item", 150,
            list, 0));
    registrar->accept(new ListWidthProperty("Settings", "Value", 450,
            list, 1));

}
    
void CTCFoxWindow::markToolbar(void)
{
    new FXFrame(toolbar,LAYOUT_CENTER_Y|LAYOUT_FIX_HEIGHT|LAYOUT_FIX_WIDTH,0,0,5,22);
    new FXFrame(toolbar,FRAME_SUNKEN|LAYOUT_CENTER_Y|LAYOUT_FIX_HEIGHT|LAYOUT_FIX_WIDTH,0,0,2,22);
    new FXFrame(toolbar,LAYOUT_CENTER_Y|LAYOUT_FIX_HEIGHT|LAYOUT_FIX_WIDTH,0,0,5,22);
}
 

// Create and initialize 
void CTCFoxWindow::create(void)
{
    // Create the windows
    FXMainWindow::create();
    tree->getFirstItem()->setOpened(true);
  
    // Make the main window appear
    show(PLACEMENT_SCREEN);
}

long CTCFoxWindow::onCmdExit(FXObject*,FXSelector,void*)
{
    if(!
        dynamic_cast<RootTreeItem*>(tree->getFirstItem())->terminate()
        )
        return 0;

    getApp()->exit(0);
    registrar->finalize();
    return 1;
}

static void setStripText(FXText * editor, const char * text)
{
    std::vector<char> hold;
    for(int i=0; text[i]; ++i)
        if(text[i] != '\r')
            hold.push_back(text[i]);

    editor->setText(&hold[0],FXint(hold.size()));
}


long CTCFoxWindow::onCmdAbout(FXObject*,FXSelector,void*)
{
    FXDialogBox about(this,Strings::load(Strings::ABOUT_CTCFOX),DECOR_TITLE|DECOR_BORDER,0,0,0,0, 0,0,0,0, 0,0);

    // Contents
    std::auto_ptr<FXVerticalFrame> contents(new 
      FXVerticalFrame(&about,LAYOUT_SIDE_RIGHT|FRAME_NONE|LAYOUT_FILL_X|LAYOUT_FILL_Y));
  
    // Switcher
    std::auto_ptr<FXTabBook> tabbook(new 
      FXTabBook(contents.get(),&about,ID_ABT_TAB,LAYOUT_FILL_X|LAYOUT_FILL_Y|LAYOUT_RIGHT));

    // First tab
    std::auto_ptr<FXTabItem> tab1(new FXTabItem(tabbook.get(),Strings::load(Strings::TAB_ABOUT),NULL));

    std::auto_ptr<FXHorizontalFrame> t1_contents(new 
      FXHorizontalFrame(tabbook.get(),LAYOUT_SIDE_TOP|FRAME_NONE|LAYOUT_FILL_X|LAYOUT_FILL_Y|FRAME_RAISED|FRAME_THICK));

    new FXButton(t1_contents.get(),Strings::load(Strings::TINES),iconSource.get(Icons::TINES),&about,
          ID_ABT_TAB,FRAME_GROOVE|LAYOUT_SIDE_LEFT|LAYOUT_CENTER_Y|JUSTIFY_CENTER_X|JUSTIFY_CENTER_Y);
  
    FXVerticalFrame* side=new FXVerticalFrame(t1_contents.get(),LAYOUT_SIDE_RIGHT|LAYOUT_FILL_X|LAYOUT_FILL_Y,0,0,0,0, 10,10,10,10, 0,0);
    new FXLabel(side,"C . T . C . F . o . x",NULL,JUSTIFY_LEFT|ICON_BEFORE_TEXT|LAYOUT_FILL_X);
    new FXHorizontalSeparator(side,SEPARATOR_LINE|LAYOUT_FILL_X);
    new FXLabel(side,FXStringFormat(
      "\nFreeware mail-oriented encryption, version %s.\n\n"
      "CTCFox User Interface %s <tines@ravnaandtines.com>\n\n"
      "CTCFox is based on CTClib version %s, Copyright © 1996-2002\n"
      "Mr. Tines <tines@ravnaandtines.com>\n"
      "&& Ian Miller <ian_miller@bifroest.demon.co.uk>\n\n"
      "Tines logo © 1997 A. Taylor (after V. Vinge)\n\n"
      "20x20 icons derived from those Copyright © 1998 Dean S. Jones <dean@gallant.com>\n"
      "http://www.javalobby.org/jfa/projects/icons/index.html\n\n"
      "CTCFox uses the FOX Toolkit version %d.%d.%d.\n"
      "Copyright © 2000,2002 Jeroen van der Zijp <jeroen@fox-toolkit.org>.\n ",
      CTCFOX_PRODUCT_VERSION, CTCFOX_PRODUCT_COPYRIGHT,
      CTCLIB_PRODUCT_VERSION,FOX_MAJOR,FOX_MINOR,FOX_LEVEL),
    NULL,JUSTIFY_LEFT|LAYOUT_FILL_X|LAYOUT_FILL_Y);

    // Second tab
    std::auto_ptr<FXTabItem> tab2(new FXTabItem(tabbook.get(),Strings::load(Strings::TAB_GPL),NULL));

    std::auto_ptr<FXHorizontalFrame> t2_contents(new 
      FXHorizontalFrame(tabbook.get(),LAYOUT_SIDE_TOP|FRAME_NONE|LAYOUT_FILL_X|LAYOUT_FILL_Y|FRAME_RAISED|FRAME_THICK));

    std::auto_ptr<FXText> editor(new 
      FXText(t2_contents.get(),&about,ID_ABT_TAB,LAYOUT_FILL_X|LAYOUT_FILL_Y|TEXT_SHOWACTIVE|TEXT_READONLY));

    setStripText(editor.get(), CTClib::licence_text(CTClib::GNU_GPL));

#ifndef NO_IDEA
    // Possible 3rd tab
    std::auto_ptr<FXTabItem> tab3(new FXTabItem(tabbook.get(),Strings::load(Strings::TAB_IDEA),NULL));

    std::auto_ptr<FXHorizontalFrame> t3_contents(new 
      FXHorizontalFrame(tabbook.get(),LAYOUT_SIDE_TOP|FRAME_NONE|LAYOUT_FILL_X|LAYOUT_FILL_Y|FRAME_RAISED|FRAME_THICK));

    std::auto_ptr<FXText> editor3(new 
      FXText(t3_contents.get(),&about,ID_ABT_TAB,LAYOUT_FILL_X|LAYOUT_FILL_Y|TEXT_SHOWACTIVE|TEXT_READONLY));

    setStripText(editor3.get(), CTClib::licence_text(CTClib::IDEA));
#endif

    // OK button
    FXButton *button=new FXButton(contents.get(),Strings::load(Strings::OK),
        NULL,&about,FXDialogBox::ID_ACCEPT,BUTTON_INITIAL|BUTTON_DEFAULT|FRAME_RAISED|FRAME_THICK|LAYOUT_RIGHT,
        0,0,0,0,32,32,2,2);
    button->setFocus();
    about.execute(PLACEMENT_OWNER);

    return 1;
}

long CTCFoxWindow::onCmdLockAll(FXObject*,FXSelector,void*)
{
    dynamic_cast<RootTreeItem*>(tree->getFirstItem())->lockAll();
    return 1;
}

long CTCFoxWindow::onCmdGenerate(FXObject*,FXSelector,void*)
{
    // need to build a wizard here.
    // TODO
    return 1;
}

long CTCFoxWindow::onCmdNewDoc(FXObject*,FXSelector,void*)
{
    dynamic_cast<RootTreeItem*>(tree->getFirstItem())->newMessage(switcher);
    return 1;
}

long CTCFoxWindow::onCmdOpenDoc(FXObject*,FXSelector,void*)
{
    FXFileDialog f(this, Strings::load(Strings::OPEN_FILE));
    if(f.execute(PLACEMENT_OWNER))
    {
        FXString file = f.getFilename();
        dynamic_cast<RootTreeItem*>(tree->getFirstItem())->newMessage(switcher, &file);
        return 1;
    }
    return 0;
}

long CTCFoxWindow::onCmdCloseDoc(FXObject*,FXSelector,void*)
{
    dynamic_cast<RootTreeItem*>(tree->getFirstItem())->close(true);
    return 1;
}

long CTCFoxWindow::onCmdSaveDoc(FXObject*,FXSelector,void*)
{
    dynamic_cast<RootTreeItem*>(tree->getFirstItem())->saveItem(false);
    return 1;
}
long CTCFoxWindow::onCmdSaveDocAs(FXObject*,FXSelector,void*)
{
    dynamic_cast<RootTreeItem*>(tree->getFirstItem())->saveItem(true);
    return 1;
}
long CTCFoxWindow::onCmdCut(FXObject*,FXSelector,void*)
{
    dynamic_cast<RootTreeItem*>(tree->getFirstItem())->cut();
    return 1;
}
long CTCFoxWindow::onCmdCopy(FXObject*,FXSelector,void*)
{
    dynamic_cast<RootTreeItem*>(tree->getFirstItem())->copy();
    return 1;
}
long CTCFoxWindow::onCmdPaste(FXObject*,FXSelector,void*)
{
    dynamic_cast<RootTreeItem*>(tree->getFirstItem())->paste();
    return 1;
}
long CTCFoxWindow::onCmdQuote(FXObject*,FXSelector,void*)
{
    dynamic_cast<RootTreeItem*>(tree->getFirstItem())->quote();
    return 1;
}
long CTCFoxWindow::onCmdRot13(FXObject*,FXSelector,void*)
{
    dynamic_cast<RootTreeItem*>(tree->getFirstItem())->rot13();
    return 1;
}
long CTCFoxWindow::onCmdExtractPkey(FXObject*,FXSelector,void*)
{
    dynamic_cast<RootTreeItem*>(tree->getFirstItem())->extractPkey();
    return 1;
}
long CTCFoxWindow::onCmdSignPkey(FXObject*,FXSelector,void*)
{
    dynamic_cast<RootTreeItem*>(tree->getFirstItem())->ringSign();
    return 1;
}
long CTCFoxWindow::onCmdEnablePkey(FXObject*,FXSelector,void*)
{
    dynamic_cast<RootTreeItem*>(tree->getFirstItem())->enablePkey();
    return 1;
}
long CTCFoxWindow::onCmdDeletePkey(FXObject*,FXSelector,void*)
{
    dynamic_cast<RootTreeItem*>(tree->getFirstItem())->deletePkey();
    return 1;
}
long CTCFoxWindow::onCmdDecrypt(FXObject*,FXSelector,void*)
{
    dynamic_cast<RootTreeItem*>(tree->getFirstItem())->decrypt(CTClib::DECODE);
    return 1;
}
long CTCFoxWindow::onCmdDecryptKey(FXObject*,FXSelector,void*)
{
    dynamic_cast<RootTreeItem*>(tree->getFirstItem())->decrypt(CTClib::REVEALKEY);
    return 1;
}




FXStatusBar * CTCFoxWindow::getStatusBar(void)
{
    return statusbar;
}

void CTCFoxWindow::enableModifiedMessageActions(bool enable)
{
    std::for_each(modifiedMessageActions.begin(),
        modifiedMessageActions.end(), WindowEnabler(enable));
}
void CTCFoxWindow::enableMessageActions(bool enable)
{
    std::for_each(messageActions.begin(),
        messageActions.end(), WindowEnabler(enable));
    if(!enable)
        FXTopWindow::setTitle(appTitleString);
}
void CTCFoxWindow::enableTextActions(bool enable)
{
    std::for_each(textActions.begin(),
        textActions.end(), WindowEnabler(enable));
}
void CTCFoxWindow::enablePubkeyActions(bool enable)
{
    std::for_each(pubkeyActions.begin(),
        pubkeyActions.end(), WindowEnabler(enable));
}
void CTCFoxWindow::enableRingSignActions(bool enable)
{
    std::for_each(ringsignActions.begin(),
        ringsignActions.end(), WindowEnabler(enable));
}
void CTCFoxWindow::enableSeckeyActions(bool enable)
{
    std::for_each(seckeyActions.begin(),
        seckeyActions.end(), WindowEnabler(enable));
}
HandledList * CTCFoxWindow::getList(void)
{
    return list;
}
void CTCFoxWindow::showList()
{
    switcher->setCurrent(0);
}

void CTCFoxWindow::setTitle(FXString & s)
{
    FXString t(appTitleString, " - ");
    t += s;
    FXTopWindow::setTitle(t.text());
}

FXSwitcher * CTCFoxWindow::getSwitcher(void)
{
    return switcher;
}

RootTreeItem * CTCFoxWindow::getTreeRoot(void)
{
    return dynamic_cast<RootTreeItem*>(tree->getFirstItem());
}


} //end namespace


FXApp * getApplication(void)
{
    return theApp;
}

CTCFox::CTCFoxWindow * getFoxWindow(void)
{
    return dynamic_cast<CTCFox::CTCFoxWindow *>(theMainWindow);
}

FXMainWindow * getMainWindow(void)
{
    return theMainWindow;
}

CTCFox::RootTreeItem * getTreeRoot(void)
{
    return getFoxWindow()->getTreeRoot();
}

FXStatusLine * getStatusLine(void)
{
    return dynamic_cast<CTCFox::CTCFoxWindow*>(theMainWindow)->getStatusBar()->getStatusLine();
}

CTCFox::Icons * getIconSource(void)
{
    return theIconSource;
}

CTCFox::Registrar * getRegistrar(void)
{
    return registrar;
}

CTCFox::KeyTreeResponder * getTreeResponder(void)
{
    return static_cast<CTCFox::KeyTreeResponder *>(getFoxWindow());
}

// Here we begin
#ifdef __BORLANDC__
// Borland C++ Builder doesn't allow us to select
// the entry point, so we have to use WinMain and
// grab the command line variables via its globals
#include <dos.h>
int PASCAL WinMain(HINSTANCE hCurInstance, HINSTANCE hPrevInstance, LPSTR lpCmdLine, int nCmdShow)
#elif defined WIN32
// Visual C++.NET is the simplest case
int main(int argc,char *argv[])
#else
// KDevelop objects to main programs in sub-directories
// well below the project root, so we feed it a main()
// that simply calls this one
int proxy_main(int argc,char *argv[])
#endif
{
#ifdef __BORLANDC__
	int argc = _argc;
    char ** argv = _argv;
#endif

    std::setlocale(LC_ALL, "");

    // Make application
    FXApp application("CTCFox","RavnaAndTines");

    // Start app
    application.init(argc,argv);
    theApp = &application;
    CTCFox::Icons i(theApp);
    theIconSource = &i;

    CTCFox::Registrar r(theApp);
    registrar = &r;

    // Create the application's windows
    application.create();

    {
        CTCFox::SplashWindow splash;

        // get splash screen painted
        application.runWhileEvents(splash.getWindow());

        // Main window
        theMainWindow = new CTCFox::CTCFoxWindow(i, &application);

    }
    getFoxWindow()->create();

    // Run the application
    return application.run();
}




