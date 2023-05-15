/* ftp.cpp : passive-capable FTP client main class.
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
//

#if defined(WIN32) && defined(PASSIVE_FTP_GNU)
    #include <w32api/winsock2.h>
#endif

#include <clocale>
#include <memory>
#include <vector>
#include <algorithm>
#include <string>
#include <map>

#ifdef WIN32
    #include <io.h>
#else
    #include <unistd.h>
    #define _access access
#endif
#include <cstdio>

#if defined(WIN32) && !defined(PASSIVE_FTP_GNU)
    #include <winsock2.h>
#else
	#include <errno.h>
#endif

// Collation support 
#if defined(WIN32) && !defined(PASSIVE_FTP_GNU)
	#include <mbstring.h>
#else
	#include <ctype.h>
#endif

#include "pftp.h"
#include "licences.h"

//#include "pftp.h"
#ifdef _MSC_VER
#pragma warning (disable : 4244)
#endif
#include <fx.h>
#include <FXMemoryStream.h>
#ifdef _MSC_VER
#pragma warning (default : 4244)
#endif

#include "FileSelector.h"
#include "icons.h"
#include "FileIcon.h"
#include "SetCustomization.h"

static FXApp * theApp;
static FXMainWindow * theMainWindow;

#ifdef _MSC_VER
#pragma warning (disable : 4312 4311)
#endif

std::map<std::string, std::string> resources;
FileSelector * g_here = NULL;

void LoadStrings(void) {

resources["WSAENOTSOCK"] = "Tried to transfer data or commands without a connection";
resources["WSAHOST_NOT_FOUND"] = "Could not resolve the server name";
resources["WSAEHOSTUNREACH"] = "Could not connect to the server";
resources["WSAEHOSTDOWN"] = "Server not functioning";
resources["WSAECONNREFUSED"] = "Server refused connection";
resources["WSAETIMEDOUT"] = "Could not connect to the server";
resources["WSAENOTCONN"] = "No connection to server";
resources["WSAESHUTDOWN"] = "Connect closed by the server";
resources["WSAdefault"] = "Connection failed";
resources["Connection error"] = "Connection error";
resources["WinError"] = "%s.\nWinError.h error number %d\n";
resources["EPROTOTYPE"] = "Protocol wrong type for socket";
resources["ENOPROTOOPT"] = "Protocol not available";
resources["EPROTONOSUPPORT"] = "Protocol not supported";
resources["ESOCKTNOSUPPORT"] = "Socket type not supported";
resources["EOPNOTSUPP"] = "Operation not supported on transport endpoint";
resources["EPFNOSUPPORT"] = "Protocol family not supported";
resources["EAFNOSUPPORT"] = "Address family not supported by protocol";
resources["EADDRINUSE"] = "Address already in use";
resources["EADDRNOTAVAIL"] = "Cannot assign requested address";
resources["ENETDOWN"] = "Network is down";
resources["ENETUNREACH"] = "Network is unreachable";
resources["ENETRESET"] = "Network dropped connection because of reset";
resources["ECONNABORTED"] = "Software caused connection abort";
resources["ECONNRESET"] = "Connection reset by peer";
resources["ENOBUFS"] = "No buffer space available";
resources["EISCONN"] = "Transport endpoint is already connected";
resources["ENOTCONN"] = "Transport endpoint is not connected";
resources["ESHUTDOWN"] = "Cannot send after transport endpoint shutdown";
resources["ETOOMANYREFS"] = "Too many references: cannot splice";
resources["ETIMEDOUT"] = "Connection timed out";
resources["ECONNREFUSED"] = "Connection refused";
resources["EHOSTDOWN"] = "Host is down";
resources["EHOSTUNREACH"] = "No route to host";
resources["Edefault"] = "error number %d\n";
resources["FTP Error"] = "FTP Error";
resources["File"] = "File";
resources["Exit"] = "Exit";
resources["Help"] = "Help";
resources["About..."] = "About...";
resources["FTP Server:"] = "FTP Server:";
resources["Logon..."] = "Logon...";
resources["Create new directory"] = "\tCreate new directory\tCreate new directory.";
resources["ASCII mode"] = "ASCII mode";
resources["Name"] = "Name";
resources["Size"] = "Size";
resources["Date"] = "Date";
resources["User"] = "User";
resources["Group"] = "Group";
resources["Attributes"] = "Attributes";
resources["New..."] = "New...";
resources["About PassiveFTP"] = "About PassiveFTP";
resources["About"] = "About";
resources["OK"] = "OK";
resources["Logon"] = "Logon";
resources["Server:"] = "Server:";
resources["Username:"] = "Username:";
resources["Password:"] = "Password:";
resources["HTTP Proxy host:port:"] = "HTTP Proxy host:port:";
resources["Save password"] = "Save password";
resources["Deleting files"] = "Deleting files";
resources["Are you sure you want to delete the directory"] = "Are you sure you want to delete the directory:\n\n%s";
resources["Are you sure you want to delete the file"] = "Are you sure you want to delete the file:\n\n%s";
resources["Delete..."] = "Delete...";
resources["Directory Name"] = "Directory Name";
resources["Create New Directory"] = "Create New Directory";
resources["Create new directory in:"] = "Create new directory in: %s";
resources["Transferring"] = "Transferring %s";
resources["File Transfer"] = "File Transfer";
resources["Cancel"] = "Cancel";
resources["Throttle:"] = "Max upload rate:";
resources["unlimited"] = "unlimited";
resources["kb/s"] = "%dkb/s";
resources["b/s"] = "%db/s";
resources["choke"] = "The FTP server cannot handle this bandwidth.\nTry logging on again and limiting the upload rate.\nErrors will now ensue.";

    std::string foxResources("~/.passiveftp/passiveftp.stringtable");
#ifdef WIN32
    char module[_MAX_PATH+1];
    DWORD pathlen = GetModuleFileName(NULL, module, _MAX_PATH);
    if(!pathlen)
        return; // failed so default

    char drive[_MAX_DRIVE+1], dir[_MAX_DIR+1];
    _splitpath(module, drive, dir, NULL, NULL);
    _makepath(module, drive, dir, "passiveftp", "stringtable");
    foxResources = module;
#endif

    if(_access(foxResources.c_str(),04))
        return; // failed so default

    std::FILE * foxFile = std::fopen(foxResources.c_str(), "ra");
    if(!foxFile)
        return; // failed so default

    char lineBuffer[0xFFFF]; // surely all lines are shorter than 64k...
    while( fgets(lineBuffer, sizeof(lineBuffer), foxFile))
    {
        char * eq = strchr(lineBuffer, '=');
        if(!eq)
            continue;
        *eq = 0;
        ++eq;
        size_t n = strlen(eq);
        eq[n-1] = 0; // kill EOL character 
        
        // apply transforms for \t and \n
        char * from = eq;
        char * to = eq;
        do {
            if('\\' == from[0] && from[1])
            {
                if('t' == from[1])
                {
                    *to = '\t';
                    ++to;
                    from += 2;
                    continue;
                }
                if('n' == from[1])
                {
                    *to = '\n';
                    ++to;
                    from += 2;
                    continue;
                }
            }
            *to++ = *from++;
        } while(*from);
        *to = 0;

        resources[lineBuffer] = eq;
    }
    std::fclose(foxFile);
}

//=========================================================

class SimpleLogger : public Logger {
public:
    virtual void start(uint32_t bytes, const FXString & )
    {
        if(!progress)
            return;
        if(bytes > 0)
            progress->setTotal(bytes);
        progress->setProgress(0);
    }
    virtual bool update(uint32_t bytes, const FXString & )
    {
        if(!progress)
            return true;
        progress->setProgress(bytes);
        theMainWindow->repaint();
//        if(0 == progress->isCancelled())
            return true;

//        return false;
    }
    virtual void end(uint32_t bytes, const FXString & )
    {
        if(!progress)
            return;
//        if(0 == progress->isCancelled())
            progress->setProgress(bytes);
//        progress->hide();
    }

    virtual void choke()
    {
        FXString message(resources["choke"].c_str());
        error(message);
    }


    virtual void error(int error)
    {
#if defined(WIN32) && !defined(PASSIVE_FTP_GNU)
        char lpBuffer[1024];
        DWORD size = FormatMessageA(
            FORMAT_MESSAGE_FROM_SYSTEM | 
            FORMAT_MESSAGE_IGNORE_INSERTS,
            0,
            (DWORD) error,
            0,
            (LPTSTR) lpBuffer,
            1024,
            NULL 
            );
        lpBuffer[size] = 0;
        if(size == 0)
        {
            switch (error)
            {
//TODO i18n
            case WSAENOTSOCK:
                strcpy(lpBuffer, resources["WSAENOTSOCK"].c_str());
                break;
            case WSAHOST_NOT_FOUND:
                strcpy(lpBuffer, resources["WSAHOST_NOT_FOUND"].c_str());
                break;
            case WSAEHOSTUNREACH:
                strcpy(lpBuffer, resources["WSAEHOSTUNREACH"].c_str());
                break;
            case WSAEHOSTDOWN:
                strcpy(lpBuffer, resources["WSAEHOSTDOWN"].c_str());
                break;
            case WSAECONNREFUSED:
                strcpy(lpBuffer, resources["WSAECONNREFUSED"].c_str());
                break;
            case WSAETIMEDOUT:
                strcpy(lpBuffer, resources["WSAETIMEDOUT"].c_str());
                break;
            case WSAENOTCONN:
                strcpy(lpBuffer, resources["WSAENOTCONN"].c_str());
                break;
            case WSAESHUTDOWN:
                strcpy(lpBuffer, resources["WSAESHUTDOWN"].c_str());
                break;
            default:
                strcpy(lpBuffer, resources["WSAdefault"].c_str());
                break;
			}
            size = DWORD(strlen(lpBuffer));
        }
        --size;
        while(isspace(lpBuffer[size]))
        {
            lpBuffer[size] = 0;
            --size;
        }
        //FXMessageBox::error(theMainWindow,MBOX_OK,resources["Connection error"].c_str(),
        //    resources["WinError"].c_str(),lpBuffer, error);

        logLine(resources["Connection error"].c_str());
        FXString fmt("");
        logLine(fmt.format(resources["WinError"].c_str(),lpBuffer, error));
#else
        FXString message;
        switch(error)
        {   // see /usr/include/asm/errno.h
            case  EPROTOTYPE: message=resources["EPROTOTYPE"].c_str();break;
            case  ENOPROTOOPT: message=resources["ENOPROTOOPT"].c_str();break;
            case  EPROTONOSUPPORT: message=resources["EPROTONOSUPPORT"].c_str();break;
            case  ESOCKTNOSUPPORT: message=resources["ESOCKTNOSUPPORT"].c_str();break;
            case  EOPNOTSUPP: message=resources["EOPNOTSUPP"].c_str();break;
            case  EPFNOSUPPORT: message=resources["EPFNOSUPPORT"].c_str();break;
            case  EAFNOSUPPORT: message=resources["EAFNOSUPPORT"].c_str();break;
            case  EADDRINUSE: message=resources["EADDRINUSE"].c_str();break;
            case  EADDRNOTAVAIL: message=resources["EADDRNOTAVAIL"].c_str();break;
            case  ENETDOWN: message=resources["ENETDOWN"].c_str();break;
            case  ENETUNREACH: message=resources["ENETUNREACH"].c_str();break;
            case  ENETRESET: message=resources["ENETRESET"].c_str();break;
            case  ECONNABORTED: message=resources["ECONNABORTED"].c_str();break;
            case  ECONNRESET: message=resources["ECONNRESET"].c_str();break;
            case  ENOBUFS: message=resources["ENOBUFS"].c_str();break;
            case  EISCONN: message=resources["EISCONN"].c_str();break;
            case  ENOTCONN: message=resources["ENOTCONN"].c_str();break;
            case  ESHUTDOWN: message=resources["ESHUTDOWN"].c_str();break;
            case  ETOOMANYREFS: message=resources["ETOOMANYREFS"].c_str();break;
            case  ETIMEDOUT: message=resources["ETIMEDOUT"].c_str();break;
            case  ECONNREFUSED: message=resources["ECONNREFUSED"].c_str();break;
            case  EHOSTDOWN: message=resources["EHOSTDOWN"].c_str();break;
            case  EHOSTUNREACH: message=resources["EHOSTUNREACH"].c_str();break;
            default:
            message = message.format(resources["Edefault"].c_str(),error);
        }
        //FXMessageBox::error(theMainWindow,MBOX_OK,resources["Connection error"].c_str(), message.text());
        logLine(resources["Connection error"].c_str());
        logLine(message);
#endif
    }
//TODO i18n end
    virtual void logLine(const char * line)
    {
        FXString tmp(line);
        logLine(tmp);
    }

    virtual void logLine(FXString & line)
    {
        FXString n = line+"\n";
        text->appendText(n.text(), n.length());
        text->makePositionVisible(text->getLength());
    }
    virtual void error(FXString & error)
    {
      //FXMessageBox::error(theMainWindow,MBOX_OK,resources["FTP Error"].c_str(),error.text()); 
        logLine(resources["FTP Error"].c_str());
        logLine(error);
    }

    FXProgressBar * progress;
    FXText * text;
};

//=========================================================

// Main Window; this doesn't exactly hew to the Liskov Substitution Principle
class FTPMainWindow : public FXMainWindow, public Downloader
{
// Macro for class hierarchy declarations
FXDECLARE(FTPMainWindow)

private:
    Icons               iconSource;

    FXMenuBar           *menubar;
    FXToolBar           *toolbar;
    FXStatusBar         *statusbar;
    FXProgressBar       *progress;

    std::auto_ptr<FXMenuPane> filemenu;
    std::auto_ptr<FXMenuPane> helpmenu;

    FXSplitter          *splitter;
    FXVerticalFrame     *left;
    FXVerticalFrame     *right;

    FileSelector        *here;
    FXComboBox          *servers;
    FXIconList          *remote;
    FXLabel             *pwd;
    FXText              *feedback;
    FXCheckButton       *asc;
    FXIcon              *up;     

    PFTP                *link;
    SimpleLogger        *logger;

    FXString            dragfiles;             // Dragged files

    FXSlider            *throttle;
    FXLabel             *rateLabel;


protected:
    FTPMainWindow() {}

public:

// Message handlers
    long onCmdExit(FXObject*,FXSelector,void*);
    long onCmdAbout(FXObject*,FXSelector,void*);
    long onCmdLogon(FXObject*,FXSelector,void*);
    long onDoubleClick(FXObject*,FXSelector,void*);
    long onUpDir(FXObject*,FXSelector,void*);
    long onCmdNewFolder(FXObject*,FXSelector,void*);
    long onRightClick(FXObject*,FXSelector,void*);
    long onCmdDelete(FXObject*,FXSelector,void*);
    long onUpdSelected(FXObject*,FXSelector,void*);
    long onDNDEnter(FXObject*,FXSelector,void*);
    long onDNDLeave(FXObject*,FXSelector,void*);
    long onDNDDrop(FXObject*,FXSelector,void*);
    long onDNDMotion(FXObject*,FXSelector,void*);
    long onDNDRequest(FXObject*,FXSelector,void*);
    long onBeginDrag(FXObject*,FXSelector,void*);
    long onEndDrag(FXObject*,FXSelector,void*);
    long onDragged(FXObject*,FXSelector,void*);
    long onFeedbackDNDMotion(FXObject*,FXSelector,void*);
    long onSlide(FXObject*,FXSelector,void*);
    virtual void doDownload(const FXString &details);

public:

// Messages for our class
enum{
    ID_EXIT=FXMainWindow::ID_LAST,
    ID_HELP,
    ID_ABT_TAB,
    ID_LOGON,
    ID_REMOTE,
    ID_NEWFOLDER,
    ID_DELETE,
    ID_FEEDBACK,
    ID_SLIDE,
    ID_UPDIR,
    ID_LAST
    };

public:

    // constructor
    FTPMainWindow(FXApp* a);

    // Initialize
    virtual void create(void);

    //destroy
    virtual ~FTPMainWindow(void);

public:

private:
    void doPWD(void);
    void doList(void);
    void doUpload(FXString & file);
};

//=========================================================

// Message Map for the CTCFox Window class

FXDEFMAP(FTPMainWindow) FTPMainWindowMap[]=
{
  //________Message_Type_____________________ID____________________Message_Handler_______
  FXMAPFUNC(SEL_COMMAND,            FTPMainWindow::ID_EXIT,        FTPMainWindow::onCmdExit),
  FXMAPFUNC(SEL_COMMAND,            FTPMainWindow::ID_HELP,        FTPMainWindow::onCmdAbout),
  FXMAPFUNC(SEL_COMMAND,            FTPMainWindow::ID_LOGON,       FTPMainWindow::onCmdLogon),
  FXMAPFUNC(SEL_COMMAND,            FTPMainWindow::ID_UPDIR,       FTPMainWindow::onUpDir),
  FXMAPFUNC(SEL_DOUBLECLICKED,      FTPMainWindow::ID_REMOTE,      FTPMainWindow::onDoubleClick),
  FXMAPFUNC(SEL_COMMAND,            FTPMainWindow::ID_NEWFOLDER,   FTPMainWindow::onCmdNewFolder),
  FXMAPFUNC(SEL_RIGHTBUTTONRELEASE, FTPMainWindow::ID_REMOTE,      FTPMainWindow::onRightClick),
  FXMAPFUNC(SEL_COMMAND,            FTPMainWindow::ID_DELETE,      FTPMainWindow::onCmdDelete),
  FXMAPFUNC(SEL_UPDATE,             FTPMainWindow::ID_DELETE,      FTPMainWindow::onUpdSelected),
  FXMAPFUNC(SEL_DND_ENTER,          FTPMainWindow::ID_REMOTE,      FTPMainWindow::onDNDEnter),
  FXMAPFUNC(SEL_DND_LEAVE,          FTPMainWindow::ID_REMOTE,      FTPMainWindow::onDNDLeave),
  FXMAPFUNC(SEL_DND_DROP,           FTPMainWindow::ID_REMOTE,      FTPMainWindow::onDNDDrop),
  FXMAPFUNC(SEL_DND_MOTION,         FTPMainWindow::ID_REMOTE,      FTPMainWindow::onDNDMotion),
  FXMAPFUNC(SEL_DND_REQUEST,        0,                             FTPMainWindow::onDNDRequest),
  FXMAPFUNC(SEL_BEGINDRAG,          FTPMainWindow::ID_REMOTE,      FTPMainWindow::onBeginDrag),
  FXMAPFUNC(SEL_ENDDRAG,            FTPMainWindow::ID_REMOTE,      FTPMainWindow::onEndDrag),
  FXMAPFUNC(SEL_DRAGGED,            FTPMainWindow::ID_REMOTE,      FTPMainWindow::onDragged),
  FXMAPFUNC(SEL_DND_MOTION,         FTPMainWindow::ID_FEEDBACK,    FTPMainWindow::onFeedbackDNDMotion),
  FXMAPFUNC(SEL_UPDATE,             FTPMainWindow::ID_SLIDE,       FTPMainWindow::onSlide),

};


char * appTitleString = "PassiveFTP";

// Macro for the class hierarchy implementation
FXIMPLEMENT(FTPMainWindow,FXMainWindow,FTPMainWindowMap,ARRAYNUMBER(FTPMainWindowMap))

//=========================================================



// Destroy a FTPMainWindow
FTPMainWindow::~FTPMainWindow(void)
{
    delete up;
    delete link;
}

// Construct a FTPMainWindow
FTPMainWindow::FTPMainWindow(FXApp *a)
    :   FXMainWindow(a,appTitleString,NULL,NULL,DECOR_ALL,0,0,800,600),
    iconSource(a), link(0)
{
    setIcon(iconSource[Icons::FRAME_ICON]);

   // Make menu bar
    FXToolBarShell* dragshell1=new FXToolBarShell(this,FRAME_RAISED);
    menubar=new FXMenuBar(this,dragshell1,LAYOUT_SIDE_TOP|LAYOUT_FILL_X|FRAME_RAISED);
    new FXToolBarGrip(menubar,menubar,FXMenuBar::ID_TOOLBARGRIP,TOOLBARGRIP_DOUBLE);



    // Status bar
    statusbar=new FXStatusBar(this,LAYOUT_SIDE_BOTTOM|LAYOUT_FILL_X|STATUSBAR_WITH_DRAGCORNER|FRAME_RAISED);

    // File menu
    filemenu.reset(new FXMenuPane(this));
    new FXMenuTitle(menubar,resources["File"].c_str(),NULL,filemenu.get()); 
    // File Menu entries
    new FXMenuSeparator(filemenu.get());
    new FXMenuCommand(filemenu.get(),resources["Exit"].c_str(),NULL,this,ID_EXIT); 

    // Help menu
    helpmenu.reset(new FXMenuPane(this));
    new FXMenuTitle(menubar,resources["Help"].c_str(),NULL,helpmenu.get()); 

    // Help Menu entries
    new FXMenuCommand(helpmenu.get(),resources["About..."].c_str(),NULL,this,ID_HELP); 


    // Toolbar

    // Make a tool tip
    new FXToolTip(getApp(),0);

    // Throttle
    FXVerticalFrame * panel = new FXVerticalFrame(this,LAYOUT_SIDE_TOP|LAYOUT_FILL_X|LAYOUT_FILL_Y);
    FXHorizontalFrame * throttleBar = new FXHorizontalFrame(panel,LAYOUT_SIDE_TOP|LAYOUT_FILL_X,0,0,0,0, DEFAULT_SPACING,DEFAULT_SPACING,DEFAULT_SPACING,DEFAULT_SPACING, 0,0);
    new FXLabel(throttleBar, resources["Throttle:"].c_str(),NULL,LAYOUT_CENTER_Y);

    throttle = new FXSlider(throttleBar, this, ID_SLIDE,LAYOUT_CENTER_Y|LAYOUT_FIX_WIDTH|SLIDER_TICKS_BOTTOM|SLIDER_ARROW_DOWN,
        0,0,400,0, 0,5);
    throttle->setRange(0,10);
    throttle->setIncrement(1);
    throttle->setTickDelta(1);
    throttle->setValue(1 /*7*/);

    rateLabel = new FXLabel(throttleBar, resources["unlimited"].c_str(),NULL,LAYOUT_CENTER_Y);

    // contents
    splitter=new FXSplitter(panel,LAYOUT_SIDE_TOP|LAYOUT_FILL_X|LAYOUT_FILL_Y|SPLITTER_REVERSED|SPLITTER_TRACKING);
    left  = new FXVerticalFrame(splitter,FRAME_SUNKEN|FRAME_THICK|LAYOUT_FILL_X|LAYOUT_FILL_Y, 0,0,0,0, 0,0,0,0);
    right = new FXVerticalFrame(splitter,FRAME_SUNKEN|FRAME_THICK|LAYOUT_FILL_X|LAYOUT_FILL_Y, 0,0,400,0, 0,0,0,0);

    const FXchar * home = getApp()->reg().readStringEntry("local","directory",""); //system

    g_here = here = new FileSelector(left, NULL, 0, LAYOUT_FILL_X|LAYOUT_FILL_Y);
    here->setDirectory(home);

    FXHorizontalFrame * top = new FXHorizontalFrame(right,LAYOUT_SIDE_TOP|LAYOUT_FILL_X,0,0,0,0, DEFAULT_SPACING,DEFAULT_SPACING,DEFAULT_SPACING,DEFAULT_SPACING, 0,0);

    /* FXLabel * l = */ new FXLabel(top, resources["FTP Server:"].c_str(),NULL,LAYOUT_CENTER_Y); 
    servers = new FXComboBox(top, 30, 5, NULL,0,FRAME_SUNKEN|FRAME_THICK|LAYOUT_FIX_WIDTH|LAYOUT_CENTER_Y,0,0,145,0);
    FXButton * b = new FXButton(top, " ", 0, NULL, 0, BUTTON_TOOLBAR|LAYOUT_CENTER_Y,0,0,0,0, 1,1,1,1);
    b->disable();
    b = new FXButton(top, resources["Logon..."].c_str(), 0, this, ID_LOGON,BUTTON_NORMAL|FRAME_RAISED|LAYOUT_CENTER_Y,0,0,0,0, 1,1,1,1); 

    b = new FXButton(top, " ", 0, NULL, 0, BUTTON_TOOLBAR|LAYOUT_CENTER_Y,0,0,0,0, 1,1,1,1);
    b->disable();
    new FXButton(top,resources["Create new directory"].c_str(),iconSource[Icons::NEWFOLDER],this,ID_NEWFOLDER,BUTTON_TOOLBAR|FRAME_RAISED,0,0,0,0, 1,1,1,1); 

    std::string label("\t");
    label += FXFileSelector::UpOneLevel.text();
    up = FXCreateIcon(getApp() , FXFileSelector::Dirupicon);
    new FXButton(top,label.c_str(), up,this,ID_UPDIR,BUTTON_NORMAL|FRAME_RAISED|LAYOUT_CENTER_Y,0,0,0,0, 1,1,1,1); 

    asc = new FXCheckButton(top, resources["ASCII mode"].c_str(), NULL, 0, CHECKBUTTON_NORMAL|LAYOUT_CENTER_Y|LAYOUT_SIDE_RIGHT); 

    pwd = new FXLabel(right, "",NULL,LAYOUT_SIDE_TOP|LAYOUT_FILL_X|LAYOUT_CENTER_X);
    feedback = new FXText(right, this, ID_FEEDBACK, LAYOUT_SIDE_TOP|LAYOUT_FILL_X, 0,0,400,0);
    logger = new SimpleLogger();
    logger->text = feedback;
    logger->progress = NULL;

    FXHorizontalFrame *frame=new FXHorizontalFrame(right,LAYOUT_SIDE_TOP|LAYOUT_FILL_X|LAYOUT_FILL_Y|FRAME_SUNKEN|FRAME_THICK,0,0,0,0,0,0,0,0);
    remote = new FXIconList(frame, this, ID_REMOTE,ICONLIST_MINI_ICONS|ICONLIST_BROWSESELECT|ICONLIST_AUTOSIZE|LAYOUT_FILL_X|LAYOUT_FILL_Y);

    //want name size date user group attributes
    remote->appendHeader(resources["Name"].c_str()); 
    remote->appendHeader(resources["Size"].c_str()); 
    remote->appendHeader(resources["Date"].c_str()); 
    remote->appendHeader(resources["User"].c_str()); 
    remote->appendHeader(resources["Group"].c_str()); 
    remote->appendHeader(resources["Attributes"].c_str()); 

    remote->dropEnable();

    servers->appendItem(resources["New..."].c_str(), reinterpret_cast<void*>(-1)); 

    for(int svr = 0; svr >= 0; ++svr)
    {
        FXString base;
        const FXchar * name = getApp()->reg().readStringEntry("remote",base.format("server.%d",svr).text(),""); //system
        if(0==name[0]) break;
        servers->appendItem(name, reinterpret_cast<void*>(svr));
    }

    // ensure that the number is bounded
    FXint index = getApp()->reg().readIntEntry("remote","current",0);
    if(index >= servers->getNumItems() || index < 0)
        index = 0;
    servers->setCurrentItem(index);
    progress=new FXProgressBar(panel, NULL, 0, PROGRESSBAR_DIAL|PROGRESSBAR_PERCENTAGE, 0, 0,400);//,FXint h=0,FXint pl=DEFAULT_PAD,FXint pr=DEFAULT_PAD,FXint pt=DEFAULT_PAD,FXint pb=DEFAULT_PAD);
}

// Create and initialize
void FTPMainWindow::create(void)
{
    // Create the windows
    FXMainWindow::create();

    // Make the main window appear
    show(PLACEMENT_SCREEN);

    if(!urilistType){urilistType=getApp()->registerDragType(urilistTypeName);}
    if(!textType){textType=getApp()->registerDragType(textTypeName);}
}

long FTPMainWindow::onCmdExit(FXObject*,FXSelector,void*)
{
    getApp()->reg().writeStringEntry("local","directory",here->getDirectory().text());//system
    getApp()->exit(0);
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


long FTPMainWindow::onCmdAbout(FXObject*,FXSelector,void*)
{
    FXDialogBox about(this,resources["About PassiveFTP"].c_str(),DECOR_TITLE|DECOR_BORDER,0,0,0,0, 0,0,0,0, 0,0); 

    // Contents
    std::auto_ptr<FXVerticalFrame> contents(new
      FXVerticalFrame(&about,LAYOUT_SIDE_RIGHT|FRAME_NONE|LAYOUT_FILL_X|LAYOUT_FILL_Y));

    // Switcher
    std::auto_ptr<FXTabBook> tabbook(new
      FXTabBook(contents.get(),&about,ID_ABT_TAB,LAYOUT_FILL_X|LAYOUT_FILL_Y|LAYOUT_RIGHT));

    // First tab
    std::auto_ptr<FXTabItem> tab1(new FXTabItem(tabbook.get(),resources["About"].c_str(),NULL)); 

    std::auto_ptr<FXHorizontalFrame> t1_contents(new
      FXHorizontalFrame(tabbook.get(),LAYOUT_SIDE_TOP|FRAME_NONE|LAYOUT_FILL_X|LAYOUT_FILL_Y|FRAME_RAISED|FRAME_THICK));


    new FXButton(t1_contents.get(),"",iconSource[Icons::TINES],&about,
          ID_ABT_TAB,FRAME_GROOVE|LAYOUT_SIDE_LEFT|LAYOUT_CENTER_Y|JUSTIFY_CENTER_X|JUSTIFY_CENTER_Y);


    FXVerticalFrame* side=new FXVerticalFrame(t1_contents.get(),LAYOUT_SIDE_RIGHT|LAYOUT_FILL_X|LAYOUT_FILL_Y,0,0,0,0, 10,10,10,10, 0,0);
        new FXLabel(side,"PassiveFTP",NULL,JUSTIFY_LEFT|ICON_BEFORE_TEXT|LAYOUT_FILL_X); 
    new FXHorizontalSeparator(side,SEPARATOR_LINE|LAYOUT_FILL_X);
    new FXLabel(side,FXStringFormat( 
      "\nPassive FTP client, version "PASSIVE_FTP_PRODUCT_VERSION"\n\n"
      "Copyright © 2002-2007 Mr. Tines <tines@ravnaandtines.com>\n"
      "Tines logo © 1997 A. Taylor (after V. Vinge)\n\n"
      "PassiveFTP uses the FOX Toolkit version %d.%d.%di.\n"
      "Copyright © 2000-2004 Jeroen van der Zijp <jeroen@fox-toolkit.org>.\n",
      FOX_MAJOR,FOX_MINOR,FOX_LEVEL),
    NULL,JUSTIFY_LEFT|LAYOUT_FILL_X|LAYOUT_FILL_Y);

    // Second tab
    std::auto_ptr<FXTabItem> tab2(new FXTabItem(tabbook.get(),"GPL",NULL)); 

    std::auto_ptr<FXHorizontalFrame> t2_contents(new
      FXHorizontalFrame(tabbook.get(),LAYOUT_SIDE_TOP|FRAME_NONE|LAYOUT_FILL_X|LAYOUT_FILL_Y|FRAME_RAISED|FRAME_THICK));

    std::auto_ptr<FXText> editor(new
      FXText(t2_contents.get(),&about,ID_ABT_TAB,LAYOUT_FILL_X|LAYOUT_FILL_Y|TEXT_SHOWACTIVE|TEXT_READONLY));

    setStripText(editor.get(), licence_text());

    // OK button
    FXButton *button=new FXButton(contents.get(),resources["OK"].c_str(),
        NULL,&about,FXDialogBox::ID_ACCEPT,BUTTON_INITIAL|BUTTON_DEFAULT|FRAME_RAISED|FRAME_THICK|LAYOUT_RIGHT,
        0,0,0,0,32,32,2,2);
    button->setFocus();

    about.execute(PLACEMENT_OWNER);

    return 1;
}

long FTPMainWindow::onCmdLogon(FXObject*,FXSelector,void*)
{
    FXint index = servers->getCurrentItem();
    int i = reinterpret_cast<int>(servers->getItemData(index));

    FXDialogBox logon(this,resources["Logon"].c_str(),DECOR_TITLE|DECOR_BORDER,0,0,0,0, 0,0,0,0, 0,0); 

    // Contents
    std::auto_ptr<FXVerticalFrame> contents(new
      FXVerticalFrame(&logon,LAYOUT_SIDE_RIGHT|FRAME_NONE|LAYOUT_FILL_X|LAYOUT_FILL_Y));

    std::auto_ptr<FXMatrix> matrix(new
        FXMatrix(contents.get(), 5));

    std::auto_ptr<FXLabel> lserver(new
        FXLabel(matrix.get(),resources["Server:"].c_str() ,NULL,LAYOUT_CENTER_Y)); 
    std::auto_ptr<FXLabel> luser(new
        FXLabel(matrix.get(),resources["Username:"].c_str() ,NULL,LAYOUT_CENTER_Y)); 
    std::auto_ptr<FXLabel> lpass(new
        FXLabel(matrix.get(),resources["Password:"].c_str() ,NULL,LAYOUT_CENTER_Y)); 
    std::auto_ptr<FXLabel> lprox(new
		FXLabel(matrix.get(),resources["HTTP Proxy host:port:"].c_str() ,NULL,LAYOUT_CENTER_Y)); 
    std::auto_ptr<FXCheckButton> spass(new
        FXCheckButton(matrix.get(), resources["Save password"].c_str(), NULL, 0, CHECKBUTTON_NORMAL|LAYOUT_CENTER_Y)); 

    std::auto_ptr<FXTextField> tserver(new
        FXTextField(matrix.get(), 40));
    std::auto_ptr<FXTextField> tuser(new
        FXTextField(matrix.get(), 40));
    std::auto_ptr<FXTextField> tpass(new
        FXTextField(matrix.get(), 40, NULL, 0, TEXTFIELD_NORMAL|TEXTFIELD_PASSWD));
    std::auto_ptr<FXTextField> tprox(new
        FXTextField(matrix.get(), 40));

    if(i >= 0)
    {
        FXString base;
        const FXchar * name = getApp()->reg().readStringEntry("remote",base.format("server.%d",i).text(),"");//system
        tserver->setText(name);

        name = getApp()->reg().readStringEntry("remote",base.format("user.%d",i).text(),"");//system
        tuser->setText(name);

        name = getApp()->reg().readStringEntry("remote",base.format("password.%d",i).text(),"");//system
        tpass->setText(name);
        spass->setCheck(name[0] != 0);

        name = getApp()->reg().readStringEntry("remote",base.format("proxy.%d",i).text(),"");// system
        tprox->setText(name);
    }

    bool wasSaved = spass->getCheck() != 0;

    // button bar
    std::auto_ptr<FXHorizontalFrame> buttons(new
      FXHorizontalFrame(contents.get(),LAYOUT_SIDE_BOTTOM|FRAME_NONE|LAYOUT_FILL_X));

    FXButton *button=new FXButton(buttons.get(),resources["OK"].c_str(), 
        NULL,&logon,FXDialogBox::ID_ACCEPT,BUTTON_INITIAL|BUTTON_DEFAULT|FRAME_RAISED|FRAME_THICK|LAYOUT_CENTER_Y|LAYOUT_RIGHT,
        0,0,0,0,32,32,2,2);
    button->setFocus();
    button=new FXButton(buttons.get(),resources["Cancel"].c_str(),
        NULL,&logon,FXDialogBox::ID_CANCEL,FRAME_RAISED|FRAME_THICK|LAYOUT_CENTER_Y|LAYOUT_RIGHT,
        0,0,0,0,32,32,2,2);

    if(logon.execute(PLACEMENT_OWNER))
    {
        if(link)
            delete link;

        if(i < 0)
        {
            i = servers->getNumItems()-1;
            servers->appendItem(tserver->getText(), reinterpret_cast<void*>(i));
            servers->setCurrentItem(i+1);
        }

        getApp()->reg().writeIntEntry("remote","current",servers->getCurrentItem());//system
        FXString base ;
        getApp()->reg().writeStringEntry("remote",base.format("server.%d",i).text(),tserver->getText().text());
        getApp()->reg().writeStringEntry("remote",base.format("user.%d",i).text(),tuser->getText().text());
        getApp()->reg().writeStringEntry("remote",base.format("proxy.%d",i).text(),tprox->getText().text());

        if(spass->getCheck())
        {
            getApp()->reg().writeStringEntry("remote",base.format("password.%d",i).text(),tpass->getText().text());
        }
        else if(wasSaved)
        {
            getApp()->reg().deleteEntry("remote",base.format("password.%d",i).text());
        }

        link = new PFTP(tserver->getText().text(),
            tuser->getText().text(),
            tpass->getText().text(),
            tprox->getText().text(),
            logger);

        if(link->isConnected())
        {
            doPWD();
            doList();
        }
        else
        {
            delete link;
            link = NULL;
        }
    }
    return 1;
}

void FTPMainWindow::doPWD(void)
{
    FXString base;
    link->pwd(base);
    pwd->setText(base);
}

struct lineItem {
    char * name;
    char * size;
    char * date;
    char * user;
    char * group;
    char * attributes;
    FXIcon * icon;
};

static bool collate(const char * t1, const char * t2)
{
#if defined(WIN32) && !defined(PASSIVE_FTP_GNU)
        return _mbsicoll(
            reinterpret_cast<const unsigned char *>(t1),
            reinterpret_cast<const unsigned char *>(t2)) < 0;
#else
        return strcoll(t1, t2) < 0;
#endif
}

static bool filesort(const lineItem & left, const lineItem & right)
{
    if(left.attributes[0] != right.attributes[0])
        return left.attributes[0] == 'd';

    return collate(left.name, right.name);
}

class FileInserter
{
public:
    FileInserter(FXIconList * alist) : list(alist) {}
    void operator ( ) (lineItem & l)
    {
    //want name size date user group attributes
        FXString str(l.name);
        str += '\t';
        str += l.size;
        str += '\t';
        str += l.date;
        str += '\t';
        str += l.user;
        str += '\t';
        str += l.group;
        str += '\t';
        str += l.attributes;

        int i = list->appendItem(str, l.icon, l.icon, reinterpret_cast<void*>(l.attributes[0]));
        list->getItem(i)->setDraggable(l.attributes[0] != 'd');

    }
private:
    FXIconList * list;
};


void FTPMainWindow::doList(void)
{
    FXMemoryStream str;
    str.open(FXStreamSave, NULL);


    link->list(str);

    // nil terminate the buffer
    FXuchar nil = 0;
    str.save(&nil, 1);

    // and get it
    FXuchar* buffer;
    unsigned long sp;
    str.takeBuffer(buffer, sp);

    char * ptr = reinterpret_cast<char *>(buffer);
    char * next = strchr(ptr, '\n');
    char * start = ptr;

    remote->clearItems();

    //want name size date user group attributes

    remote->setHeaderSize(0,100);
    remote->setHeaderSize(1,100);
    remote->setHeaderSize(2,100);
    remote->setHeaderSize(3,100);
    remote->setHeaderSize(4,100);
    remote->setHeaderSize(5,100);
    remote->setListStyle(ICONLIST_DETAILED);

    std::vector<lineItem> files;

    //get attributes # user group size date =(3 or 4? fields) name
    while(next && (ptr-start < (int)sp))
    {
        next[0] = 0;
        if(next[-1] == '\r')
            next[-1] = 0;

        // Some FTP servers send other lines than raw directory
        if(ptr[0] != 'd' && ptr[0] != '-')
        {
            ptr = next+1;
            next = strchr(ptr, '\n');
            continue;
        }

        lineItem item;
        item.icon = ptr[0]=='d'?
            iconSource[Icons::FOLDER]  :
            iconSource[Icons::DOCUMENT];

        FXIcon *bigI, *smallI;
        if(ptr[0]=='d' && getFolderIcons(&bigI, &smallI))
        {
            if(smallI)
                item.icon = smallI;
        }
        item.attributes = ptr;

        char * cursor = strchr(ptr, ' ');
        *cursor = 0;
        ++cursor;
        while(isspace(*cursor))
            ++cursor;
        // skip the group number
        while(!isspace(*cursor))
            ++cursor;
        // go for the user
        while(isspace(*cursor))
            ++cursor;
        item.user = cursor;
        // crawl over it and terminate
        while(!isspace(*cursor))
            ++cursor;
        *cursor = 0;
        ++cursor;
        // go for the group
        while(isspace(*cursor))
            ++cursor;
        item.group = cursor;
        // crawl over it and terminate
        while(!isspace(*cursor))
            ++cursor;
        *cursor = 0;
        ++cursor;
        // go for the size
        while(isspace(*cursor))
            ++cursor;
        item.size = cursor;
        // crawl over it and terminate
        while(!isspace(*cursor))
            ++cursor;
        *cursor = 0;
        ++cursor;
        // go for the date
        while(isspace(*cursor))
            ++cursor;
        item.date = cursor;
        while(!isspace(*cursor))
            ++cursor; // over month
        while(isspace(*cursor))
            ++cursor; // gap
        while(!isspace(*cursor))
            ++cursor; // day
        while(isspace(*cursor))
            ++cursor; // gap
        while(!isspace(*cursor))
            ++cursor; // time or year
        *cursor = 0;
        ++cursor;
        // go for the name
        while(isspace(*cursor))
            ++cursor;
        item.name = cursor;
        if(ptr[0]!='d')
        {
            try {
            FXString name(cursor);
            int pt = name.rfind('.');
            if(pt >= 0)
                name = name.right(name.length()-pt);
            else
                name = "";

            if(getFileTypeIcons(name, &bigI, &smallI))
            {
                if(smallI)
                    item.icon = smallI;
            }
            } catch (...) {
#ifdef _DEBUG
                DebugBreak();
#endif
            }
        }

        files.push_back(item);

        ptr = next+1;
        next = strchr(ptr, '\n');
    }

    // now case-blind sort by name, but folders first
    std::sort(files.begin(), files.end(), filesort);
    std::for_each(files.begin(), files.end(), FileInserter(remote));

    FXFREE(&buffer);
}

#ifdef _MSC_VER
typedef char SAFETYPE;
#else
typedef int SAFETYPE;
#endif

long FTPMainWindow::onUpDir(FXObject*,FXSelector,void*)
{
    if(!link)
        return 0;
    link->setDir("..");
    doPWD();
    doList();

    return 1;
}


long FTPMainWindow::onDoubleClick(FXObject*,FXSelector,void*)
{
    if(!link)
        return 0;

    int r;
    bool found = false;
    for(r=0; r<remote->getNumRows(); ++r)
    {
        found = remote->isItemSelected(r) != 0;
        if(found) break;
    }
    if(!found)
        return 0;

    char tag = reinterpret_cast<SAFETYPE>(remote->getItemData(r));

    if('d' != tag)
        return 0;

    FXString local(remote->getItemText(r));
    char * work = const_cast<char*>(local.text());
    char * end = strchr(work, '\t');
    *end = 0;

    link->setDir(work);
    doPWD();
    doList();

    return 1;
}

// Sensitize when files are selected
long FTPMainWindow::onUpdSelected(FXObject* sender,FXSelector,void*)
{
    for(FXint i=0; i<remote->getNumItems(); i++)
    {
        if(remote->isItemSelected(i))
        {
            sender->handle(this,MKUINT(ID_ENABLE,SEL_COMMAND),NULL);
            return 1;
        }
    }
    sender->handle(this,MKUINT(ID_DISABLE,SEL_COMMAND),NULL);
    return 1;
}

// Delete file or directory
long FTPMainWindow::onCmdDelete(FXObject*,FXSelector,void*)
{
    FXString fullname,name;
    FXuint answer;
    for(FXint i=0; i<remote->getNumItems(); i++)
    {
        if(!remote->isItemSelected(i))
            continue;

        FXString local(remote->getItemText(i));
        char * work = const_cast<char*>(local.text());
        char * end = strchr(work, '\t');
        *end = 0;
        name = work;

        if(name=="..") continue;
        if(name==".") continue;

        bool directory = 'd' == reinterpret_cast<SAFETYPE>(remote->getItemData(i));

        if(directory)
        {
            FXString fmt;
            fmt.format(resources["Are you sure you want to delete the directory"].c_str(),name.text());
            answer=FXMessageBox::warning(this,MBOX_YES_NO_CANCEL,resources["Deleting files"].c_str(), 
                fmt.text());
        }
        else
        {
            FXString fmt;
            fmt.format(resources["Are you sure you want to delete the file"].c_str(),name.text());
            answer=FXMessageBox::warning(this,MBOX_YES_NO_CANCEL,resources["Deleting files"].c_str(), 
                fmt.text());
        }

        if(answer==MBOX_CLICKED_CANCEL) break;
        if(answer==MBOX_CLICKED_NO) continue;

        if(directory)
            link->rmDir(name);
        else
            link->del(name);

        doList();
    }
    return 1;
}


long FTPMainWindow::onRightClick(FXObject*,FXSelector,void*ptr)
{
    if(!link)
        return 0;

    FXEvent *event=(FXEvent*)ptr;
    if(event->moved) return 1;

    FXMenuPane filemenu(this);
    new FXMenuCommand(&filemenu,resources["Delete..."].c_str(),iconSource[Icons::DELETEFILE],this,ID_DELETE); 

    filemenu.create();
    filemenu.popup(NULL,event->root_x,event->root_y);
    getApp()->runModalWhileShown(&filemenu);

    return 1;
}


long FTPMainWindow::onCmdNewFolder(FXObject*,FXSelector,void*)
{
    if(!link)
        return 0;

    FXString dir = pwd->getText();
    FXString name=resources["Directory Name"].c_str(); 

    FXString in;
    in.format(resources["Create new directory in"].c_str(),dir.text());
    if(FXInputDialog::getString(name,this,resources["Create New Directory"].c_str(),in,iconSource[Icons::DLGNEWFOLDER])) 
    {
        link->mkDir(name);
        doList();
    }
    return 1;
}

// handles drags from the file selector
long FTPMainWindow::onDNDEnter(FXObject*,FXSelector ,void* )
{
    if(!link)
        return 0;
    acceptDrop(DRAG_ACCEPT);
    return 1;
}
long FTPMainWindow::onDNDLeave(FXObject*,FXSelector,void*)
{
    if(!link)
        return 0;
    acceptDrop(DRAG_REJECT);
    return 1;
}

// handles drags from the file selector
long FTPMainWindow::onDNDDrop(FXObject*,FXSelector,void*)
{
    if(!link)
        return 0;

    FXuchar * data = NULL;
    FXuint size;
    if(!getDNDData(FROM_DRAGNDROP,urilistType,data,size))
        return 1;

    if(size > 0)
    {
        char * start = reinterpret_cast<char*>(data);
        char last = start[size-1];
        start[size-1] = 0;
        char * end = strchr(start, '\r');

        while(end)
        {
            *end = 0;
            FXString tmp(start);
            doUpload(tmp);
            start = end+2;
            end = strchr(start, '\r');
        }
        size -= FXuint(start - reinterpret_cast<char*>(data));
        start[size-1] = last;
        FXString url(start, size);
        doUpload(url);
    }

    doList();

    if(data)
        FXFREE(&data);
    return 1;
}

static const int BUCKETSIZE = 128;
struct Bucket {
    uint8_t bucket[BUCKETSIZE];
};

void FTPMainWindow::doUpload(FXString & file)
{
    file = FXURL::fileFromURL(file);
    if(FXFile::isDirectory(file))
        return;
    if(FXFile::isReadable(file) == 0)
        return;

    uint32_t size = FXFile::size(file);
    std::vector<Bucket> data;
    Bucket xfer;

    FXFileStream str;
    str.open(file, FXStreamLoad);

    if(str.direction() != FXStreamLoad)
        return;

    uint32_t left = size;
    while(left > 0)
    {
        str.load(reinterpret_cast<FXuchar*>(xfer.bucket), BUCKETSIZE);
        data.push_back(xfer);
        if(left > (uint32_t)BUCKETSIZE)
            left -= BUCKETSIZE;
        else
            left = 0;
    }


    file = FXFile::name(file);

    FXString label;
    label.format(resources["Transferring"].c_str(),file.text()); 
    logger->progress = progress;
    statusbar->getStatusLine()->setText(label);
    link->upload(file, data[0].bucket, size, throttle->getValue(), asc->getCheck()!=0);
    statusbar->getStatusLine()->setText("Ready");
    logger->progress = NULL;
    progress->setProgress(0);
}

void FTPMainWindow::doDownload(const FXString & fileDetails)
{
    const char * text = fileDetails.text();
    char * eon = const_cast<char*>(strchr(text, '\t'));
    char * eos = strchr(eon+1, '\t');

    FXString name(text, FXint(eon-text));
    ++eon;

    FXString size(eon, FXint(eos-eon));
    FXString label;
    label.format(resources["Transferring"].c_str(),name.text()); 
    logger->progress = progress;
    statusbar->getStatusLine()->setText(label);
    progress->setTotal(FXIntVal(size,10));

    FXMemoryStream str;
    str.open(FXStreamSave, NULL);

    link->download(name, str, asc->getCheck()!=0);

    // now write it to a file
    name = FXFile::absolute(here->getDirectory(),name);
    FXuchar* buffer;
    unsigned long sp;
    str.takeBuffer(buffer, sp);

    FXFileStream out;
    out.open(name, FXStreamSave);

    if(out.direction() != FXStreamSave)
        return;

    out.save(buffer, sp);
    out.close();
    statusbar->getStatusLine()->setText("Ready");
    progress->setProgress(0);

    FXFREE(&buffer);
}

long FTPMainWindow::onDNDMotion(FXObject*,FXSelector,void*)
{
    if(!link)
        return 0;
    acceptDrop(DRAG_ACCEPT);
    return 1;
}
long FTPMainWindow::onFeedbackDNDMotion(FXObject*,FXSelector,void*)
{
    acceptDrop(DRAG_REJECT);
    return 1;
}

long FTPMainWindow::onDNDRequest(FXObject*,FXSelector,void*ptr)
{
    if(!link)
        return 0;
    FXEvent *event=(FXEvent*)ptr;
    FXuchar *data; FXuint len;

    // Return list of filenames as a set of text lines
    if(event->target==textType)
    {
        if(!dragfiles.empty())
        {
            len=dragfiles.length();
            FXMEMDUP(&data,dragfiles.text(),FXuchar,len);
            setDNDData(FROM_DRAGNDROP,event->target,data,len);
        }
        return 1;
    }
    return 0;
}

long FTPMainWindow::onBeginDrag(FXObject*,FXSelector,void*)
{
    if(!link)
        return 0;

    register FXint i;
    if(beginDrag(&textType,1))
    {
        dragfiles=FXString::null;
        for(i=0; i<remote->getNumItems(); i++)
        {
            if(!remote->isItemSelected(i))
                continue;
            if(!remote->getItem(i)->isDraggable())
                continue;
            bool directory = 'd' == reinterpret_cast<SAFETYPE>(remote->getItemData(i));
            if(directory)
                continue;
            FXString local(remote->getItemText(i));
            dragfiles+=remote->getItemText(i)+"\r\n";
        }
    }
    return 1;
}
long FTPMainWindow::onDragged(FXObject*,FXSelector,void*ptr)
{
    if(!link)
        return 0;

    FXEvent* event=(FXEvent*)ptr;
    handleDrag(event->root_x,event->root_y);

    // if in remote or filebox 
    if(didAccept()!=DRAG_REJECT)
        remote->setDragCursor(theApp->getDefaultCursor(DEF_DNDCOPY_CURSOR));
    else
        remote->setDragCursor(theApp->getDefaultCursor(DEF_DNDSTOP_CURSOR));

    return 1;
}
long FTPMainWindow::onEndDrag(FXObject*,FXSelector,void*)
{
    if(!link)
        return 0;
    //int i = (SEL_DND_REQUEST<<16)   |    FTPMainWindow::ID_REMOTE;
    endDrag(didAccept()!=DRAG_REJECT);
    setDragCursor(getDefaultCursor());
    dragfiles=FXString::null;
    return 1;
}

long FTPMainWindow::onSlide(FXObject*,FXSelector,void*)
{
    FXString fmt;
    int i = throttle->getValue();
    if(0 == i)
        fmt = resources["unlimited"].c_str();
    else if (i <= 8)
        fmt.format(resources["kb/s"].c_str(), 256>>i);
    else
        fmt.format(resources["b/s"].c_str(), 1024>>(i-8));
    rateLabel->setText(fmt);
    return 1;
}


//=========================================================

Downloader * getDownloader()
{
    FTPMainWindow * m = dynamic_cast<FTPMainWindow *>(theMainWindow);
    return dynamic_cast<Downloader*>(m);
}

//=========================================================
#if defined(WIN32)
class WSACleaner {
	bool state;
public:

	WSACleaner() : state(false) {}
	bool init(void)
	{
		WSADATA wsaData;
		WORD wVersionRequested = MAKEWORD( 2, 2 );
		int err = WSAStartup( wVersionRequested, &wsaData );
		if ( err != 0 ) {
			/* Tell the user that we could not find a usable */
			/* WinSock DLL.                                  */
			return false;
		}
		return (state = true);
	}

    ~WSACleaner() {
		if(state)
			WSACleanup();
    }
};
#endif
//=========================================================

// Here we begin
#ifdef __BORLANDC__
// Borland C++ Builder doesn't allow us to select
// the entry point, so we have to use WinMain and
// grab the command line variables via its globals
#include <dos.h>
int PASCAL WinMain(HINSTANCE , HINSTANCE , LPSTR , int )
#else
int main(int argc,char *argv[])
#endif
{

#ifdef __BORLANDC__
	int argc = _argc;
    char ** argv = _argv;
#endif

#if defined(WIN32)
    WSACleaner safe;
	if(!safe.init())
		return 0;
#endif

    std::setlocale(LC_ALL, "");

    SetIcons();
    SetLanguage();
    LoadStrings();

    // Make application
    FXApp application("FTP","RavnaAndTines");

    // Start app
    application.init(argc,argv);
    theApp = &application;
    theMainWindow = new FTPMainWindow(&application);

    // Create the application's windows
    application.create();

    // Run the application
    return application.run();
}

