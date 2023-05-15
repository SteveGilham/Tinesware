/* SetLanguage.cpp : passive-capable FTP client main class.
   Copyright 2003 Mr. Tines <Tines@RavnaAndTines.com>

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
#ifdef WIN32
    #define WIN32_LEAN_AND_MEAN
    #include <windows.h>
    #include <io.h>
#else
    #include <unistd.h>
    #define _access access
#endif

#ifdef _MSC_VER
    #pragma warning (disable : 4244)
#endif
#include <fx.h>
#include <FXRex.h>
#ifdef _MSC_VER
    #pragma warning (default : 4244)
#endif

#include "SetCustomization.h"
#include <string>

#include <cstdio>
#include <map>

#define updateResources(key) \
    if(resources.find(#key) != resources.end())\
        key = resources[#key].c_str();

namespace FX {
    void SetLanguage(void) {

        std::string foxResources("~/.FOX/FOX.stringtable");
#ifdef WIN32
        char module[_MAX_PATH+1];
        DWORD pathlen = GetModuleFileName(NULL, module, _MAX_PATH);
        if(!pathlen)
            return; // failed so default

        char drive[_MAX_DRIVE+1], dir[_MAX_DIR+1];
        _splitpath(module, drive, dir, NULL, NULL);
        _makepath(module, drive, dir, "FOX", "stringtable");
        foxResources = module;
#endif

        if(_access(foxResources.c_str(),04))
            return; // failed so default

        std::FILE * foxFile = std::fopen(foxResources.c_str(), "ra");
        if(!foxFile)
            return; // failed so default

        std::map<std::string, std::string> resources;

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

//FXColorSelector
updateResources(FXColorSelector::Accept)
updateResources(FXColorSelector::Cancel)
updateResources(FXColorSelector::Pick)
updateResources(FXColorSelector::HSVType)
updateResources(FXColorSelector::RGBType)
updateResources(FXColorSelector::Red)
updateResources(FXColorSelector::Green)
updateResources(FXColorSelector::Blue)
updateResources(FXColorSelector::Alpha)
updateResources(FXColorSelector::Hue)
updateResources(FXColorSelector::Saturation)
updateResources(FXColorSelector::Value)
updateResources(FXColorSelector::CMYType)
updateResources(FXColorSelector::Cyan)
updateResources(FXColorSelector::Magenta)
updateResources(FXColorSelector::Yellow)
updateResources(FXColorSelector::ByName)

//FXColorWell
updateResources(FXColorWell::ColorDialog)

//FXDirSelector
updateResources(FXDirSelector::Accept)
updateResources(FXDirSelector::Cancel)
updateResources(FXDirSelector::DirectoryName)

//FXFileList
updateResources(FXFileList::Name)
updateResources(FXFileList::Type)
updateResources(FXFileList::Size)
updateResources(FXFileList::ModifiedDate)
updateResources(FXFileList::User)
updateResources(FXFileList::Group)
updateResources(FXFileList::Attributes)
#ifndef WIN32
updateResources(FXFileList::Link)
#endif
updateResources(FXFileList::FileFolder)
updateResources(FXFileList::Application)
updateResources(FXFileList::ExtensionFile)

//FXFileSelector
updateResources(FXFileSelector::AllFiles)
updateResources(FXFileSelector::FileName)
updateResources(FXFileSelector::OK)
updateResources(FXFileSelector::FileFilter)
updateResources(FXFileSelector::Directory)
updateResources(FXFileSelector::Cancel)
updateResources(FXFileSelector::ReadOnly)
updateResources(FXFileSelector::SetBookmark)
updateResources(FXFileSelector::ClearBookmark);
updateResources(FXFileSelector::GoUp)
updateResources(FXFileSelector::GoHome)
updateResources(FXFileSelector::GoToWork)
updateResources(FXFileSelector::Bookmarks)
updateResources(FXFileSelector::CreateNewDirectory)
updateResources(FXFileSelector::ShowList)
updateResources(FXFileSelector::ShowIcons)
updateResources(FXFileSelector::ShowDetails)
updateResources(FXFileSelector::ShowHidden)
updateResources(FXFileSelector::HideHidden)
updateResources(FXFileSelector::DirectoryName)
updateResources(FXFileSelector::NewDirectoryCaption)
updateResources(FXFileSelector::CreateNewDirectoryIn)
updateResources(FXFileSelector::AlreadyExists)
updateResources(FXFileSelector::FileAlreadyExists)
updateResources(FXFileSelector::CannotCreate)
updateResources(FXFileSelector::CannotCreateDirectory)
updateResources(FXFileSelector::CopyFileCaption)
updateResources(FXFileSelector::CopyOf)
updateResources(FXFileSelector::CopyFileBetween)
updateResources(FXFileSelector::ErrorCopyingFile)
updateResources(FXFileSelector::UnableToCopy)
updateResources(FXFileSelector::MoveFileCaption)
updateResources(FXFileSelector::MoveFileBetween)
updateResources(FXFileSelector::ErrorMovingFile)
updateResources(FXFileSelector::UnableToMove)
updateResources(FXFileSelector::LinkFile)
updateResources(FXFileSelector::LinkTo)
updateResources(FXFileSelector::LinkFileBetween)
updateResources(FXFileSelector::ErrorLinkingFile)
updateResources(FXFileSelector::UnableToLink)
updateResources(FXFileSelector::DeleteFileCaption)
updateResources(FXFileSelector::DeleteFileAt)
updateResources(FXFileSelector::ErrorDeletingFile)
updateResources(FXFileSelector::UnableToDelete)
updateResources(FXFileSelector::UpOneLevel)
updateResources(FXFileSelector::HomeDirectory)
updateResources(FXFileSelector::WorkDirectory)
updateResources(FXFileSelector::SelectAll)
updateResources(FXFileSelector::SortBy)
updateResources(FXFileSelector::Name)
updateResources(FXFileSelector::Type)
updateResources(FXFileSelector::Size)
updateResources(FXFileSelector::Time)
updateResources(FXFileSelector::User)
updateResources(FXFileSelector::Group)
updateResources(FXFileSelector::Reverse)
updateResources(FXFileSelector::IgnoreCase)
updateResources(FXFileSelector::View)
updateResources(FXFileSelector::SmallIcons)
updateResources(FXFileSelector::BigIcons)
updateResources(FXFileSelector::Details)
updateResources(FXFileSelector::Rows)
updateResources(FXFileSelector::Columns)
updateResources(FXFileSelector::HiddenFiles)
updateResources(FXFileSelector::BookmarksMenu)
updateResources(FXFileSelector::SetBookmarkMenu)
updateResources(FXFileSelector::ClearBookmarkMenu)
updateResources(FXFileSelector::NewDirectory)
updateResources(FXFileSelector::Copy)
updateResources(FXFileSelector::Move)
updateResources(FXFileSelector::Link)
updateResources(FXFileSelector::Delete)

//FXFontSelector
updateResources(FXFontSelector::Accept)
updateResources(FXFontSelector::Cancel)
updateResources(FXFontSelector::Family)
updateResources(FXFontSelector::Weight)
updateResources(FXFontSelector::Style)
updateResources(FXFontSelector::Size)
updateResources(FXFontSelector::CharacterSet)
updateResources(FXFontSelector::Any)
updateResources(FXFontSelector::WestEuropean)
updateResources(FXFontSelector::EastEuropean)
updateResources(FXFontSelector::SouthEuropean)
updateResources(FXFontSelector::NorthEuropean)
updateResources(FXFontSelector::Cyrillic)
updateResources(FXFontSelector::Arabic)
updateResources(FXFontSelector::Greek)
updateResources(FXFontSelector::Hebrew)
updateResources(FXFontSelector::Turkish)
updateResources(FXFontSelector::Nordic)
updateResources(FXFontSelector::Thai)
updateResources(FXFontSelector::Baltic)
updateResources(FXFontSelector::Celtic)
updateResources(FXFontSelector::Russian)
updateResources(FXFontSelector::SetWidth)
updateResources(FXFontSelector::UltraCondensed)
updateResources(FXFontSelector::ExtraCondensed)
updateResources(FXFontSelector::Condensed)
updateResources(FXFontSelector::SemiCondensed)
updateResources(FXFontSelector::Normal)
updateResources(FXFontSelector::NormalLC)
updateResources(FXFontSelector::SemiExpanded)
updateResources(FXFontSelector::Expanded)
updateResources(FXFontSelector::ExtraExpanded)
updateResources(FXFontSelector::UltraExpanded)
updateResources(FXFontSelector::Pitch)
updateResources(FXFontSelector::Fixed)
updateResources(FXFontSelector::Variable)
updateResources(FXFontSelector::Scalable)
#ifndef WIN32
updateResources(FXFontSelector::AllFonts)
#endif
updateResources(FXFontSelector::Preview)
updateResources(FXFontSelector::Thin)
updateResources(FXFontSelector::ExtraLight)
updateResources(FXFontSelector::Light)
updateResources(FXFontSelector::Medium)
updateResources(FXFontSelector::Demibold)
updateResources(FXFontSelector::Bold)
updateResources(FXFontSelector::ExtraBold)
updateResources(FXFontSelector::Black)
updateResources(FXFontSelector::Regular)
updateResources(FXFontSelector::Italic)
updateResources(FXFontSelector::Oblique)
updateResources(FXFontSelector::ReverseItalic)
updateResources(FXFontSelector::ReverseOblique)

#if 0
//FXGLViewer
updateResources(FXGLViewer::PrintScene)
updateResources(FXGLViewer::PrinterError)
updateResources(FXGLViewer::UnableToPrint)
#endif

//FXInputDialog
updateResources(FXInputDialog::OK)
updateResources(FXInputDialog::Cancel)

//MDI buttons
updateResources(FXMDIDeleteButton::Close)
updateResources(FXMDIRestoreButton::Restore)
updateResources(FXMDIMaximizeButton::Maximise)
updateResources(FXMDIMinimizeButton::Minimise)
updateResources(FXMDIWindowButton::Menu)
updateResources(FXMDIMenu::Next)
updateResources(FXMDIMenu::Previous)
updateResources(FXMDIMenu::Restore)
updateResources(FXMDIMenu::Minimise)
updateResources(FXMDIMenu::Maximise)
updateResources(FXMDIMenu::Close)

//FXMDIClient
updateResources(FXMDIClient::SelectWindow)
updateResources(FXMDIClient::OK)
updateResources(FXMDIClient::Cancel)

//FXMessageBox
updateResources(FXMessageBox::OK)
updateResources(FXMessageBox::Cancel)
updateResources(FXMessageBox::Yes)
updateResources(FXMessageBox::No)
updateResources(FXMessageBox::Quit)
updateResources(FXMessageBox::Save)
updateResources(FXMessageBox::Skip)
updateResources(FXMessageBox::SkipAll)

//FXPrintDialog
updateResources(FXPrintDialog::Print)
updateResources(FXPrintDialog::Cancel)
#ifdef HAVE_CUPS_H
updateResources(FXPrintDialog::UsingCUPS)
#endif
updateResources(FXPrintDialog::PrintDestination)
updateResources(FXPrintDialog::Printer)
updateResources(FXPrintDialog::Properties)
updateResources(FXPrintDialog::File)
updateResources(FXPrintDialog::Browse)
updateResources(FXPrintDialog::Pages)
updateResources(FXPrintDialog::PrintAll)
updateResources(FXPrintDialog::EvenPages)
updateResources(FXPrintDialog::OddPages)
updateResources(FXPrintDialog::PrintRange)
updateResources(FXPrintDialog::From)
updateResources(FXPrintDialog::To)
updateResources(FXPrintDialog::Colours)
updateResources(FXPrintDialog::PrintInColour)
updateResources(FXPrintDialog::PrintInMonochrome)
updateResources(FXPrintDialog::Copies)
updateResources(FXPrintDialog::NumberOfCopies)
updateResources(FXPrintDialog::CollateOrder)
updateResources(FXPrintDialog::FirstPageFirst)
updateResources(FXPrintDialog::LastPageFirst)
updateResources(FXPrintDialog::Layout)
updateResources(FXPrintDialog::Portrait)
updateResources(FXPrintDialog::Landscape)
updateResources(FXPrintDialog::PaperSize)
updateResources(FXPrintDialog::OverwriteFile)
updateResources(FXPrintDialog::OverwriteExistingFile)
updateResources(FXPrintDialog::SelectOutputFile)
updateResources(FXPrintDialog::FileSelectionPatterns)
#ifndef WIN32
updateResources(FXPrintDialog::PrinterCommand)
updateResources(FXPrintDialog::SpecifyPrinterCommand)
#endif

//FXProgressDialog
updateResources(FXProgressDialog::Cancel)

//FXReplaceDialog
updateResources(FXReplaceDialog::Replace)
updateResources(FXReplaceDialog::ReplaceAll)
updateResources(FXReplaceDialog::Cancel)
updateResources(FXReplaceDialog::SearchFor)
updateResources(FXReplaceDialog::ReplaceWith)
updateResources(FXReplaceDialog::Exact)
updateResources(FXReplaceDialog::IgnoreCase)
updateResources(FXReplaceDialog::Expression)
updateResources(FXReplaceDialog::Backward)
updateResources(FXReplaceDialog::SearchLastCtlBTip)
updateResources(FXReplaceDialog::SearchNextCtlFTip)

updateResources(FXRex::errors[REGERR_OK])
updateResources(FXRex::errors[REGERR_EMPTY])
updateResources(FXRex::errors[REGERR_PAREN])
updateResources(FXRex::errors[REGERR_BRACK])
updateResources(FXRex::errors[REGERR_BRACE])
updateResources(FXRex::errors[REGERR_RANGE])
updateResources(FXRex::errors[REGERR_ESC])
updateResources(FXRex::errors[REGERR_COUNT])
updateResources(FXRex::errors[REGERR_NOATOM])
updateResources(FXRex::errors[REGERR_REPEAT])
updateResources(FXRex::errors[REGERR_BACKREF])
updateResources(FXRex::errors[REGERR_CLASS])
updateResources(FXRex::errors[REGERR_COMPLEX])
updateResources(FXRex::errors[REGERR_MEMORY])
updateResources(FXRex::errors[REGERR_TOKEN])

//FXSearchDialog
updateResources(FXSearchDialog::Search)

//FXStatusLine
updateResources(FXStatusLine::defaultMessage)

//FXText
updateResources(FXText::Search)
updateResources(FXText::Replace)
updateResources(FXText::GotoLine)
updateResources(FXText::GotoLineNumber)

// Undo List support
updateResources(FXCommand::UndoStr)
updateResources(FXCommand::RedoStr)

//FXWizard
updateResources(FXWizard::Next)
updateResources(FXWizard::Finish)
updateResources(FXWizard::Back)
updateResources(FXWizard::Cancel)


    }
}
