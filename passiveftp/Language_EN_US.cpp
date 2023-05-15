
#include "fx.h"

void SetLanguage(void)
{
//FXColorSelector
FXColorSelector::Accept = "&Accept";
FXColorSelector::Cancel = "&Cancel";
FXColorSelector::Pick = "\tPick color";
FXColorSelector::HSVType = "\tHue, Saturation, Value";
FXColorSelector::RGBType = "\tRed, Green, Blue";
FXColorSelector::Red = "&Red:";
FXColorSelector::Green = "&Green:";
FXColorSelector::Blue = "&Blue:";
FXColorSelector::Alpha = "&Alpha:";
FXColorSelector::Hue = "Hue:";
FXColorSelector::Saturation = "Saturation:";
FXColorSelector::Value = "Value:";
FXColorSelector::CMYType = "\tCyan, Magenta, Yellow";
FXColorSelector::Cyan = "Cyan:";
FXColorSelector::Magenta = "Magenta:";
FXColorSelector::Yellow = "Yellow:";
FXColorSelector::ByName = "\tBy Name";

//FXColorWell
FXString FXColorWell::ColorDialog = "Color Dialog";

//FXDirSelector
FXDirSelector::Accept = "&Accept";
FXDirSelector::Cancel = "&Cancel";
FXDirSelector::DirectoryName = "&Directory name:";

//FXFileList
FXFileList::Name = "Name";
FXFileList::Type = "Type";
FXFileList::Size = "Size";
FXFileList::ModifiedDate = "Modified Date";
FXFileList::User = "User";
FXFileList::Group = "Group";
FXFileList::Attributes = "Attributes";
#ifndef WIN32
FXFileList::Link = "Link";
#endif
FXFileList::FileFolder = "File Folder";
FXFileList::Application = "Application";
FXFileList::ExtensionFile = "%s File";

//FXFileSelector
FXFileSelector::AllFiles = "All Files  = *)";
FXFileSelector::FileName = "&File Name:";
FXFileSelector::OK = "&OK";
FXFileSelector::FileFilter = "File F&ilter:";
FXFileSelector::Directory = "Directory:";
FXFileSelector::Cancel = "&Cancel";
FXFileSelector::ReadOnly = "Read Only";
FXFileSelector::SetBookmark = "&Set bookmark\t\tBookmark current directory.";
FXFileSelector::ClearBookmark = "&Clear bookmarks\t\tClear bookmarks.";
FXFileSelector::GoUp = "\tGo up one directory\tMove up to higher directory.";
FXFileSelector::GoHome = "\tGo to home directory\tBack to home directory.";
FXFileSelector::GoToWork = "\tGo to work directory\tBack to working directory.";
FXFileSelector::Bookmarks = "\tBookmarks\tVisit bookmarked directories.";
FXFileSelector::CreateNewDirectory = "\tCreate new directory\tCreate new directory.";
FXFileSelector::ShowList = "\tShow list\tDisplay directory with small icons.";
FXFileSelector::ShowIcons = "\tShow icons\tDisplay directory with big icons.";
FXFileSelector::ShowDetails = "\tShow details\tDisplay detailed directory listing.";
FXFileSelector::ShowHidden = "\tShow hidden files\tShow hidden files and directories.";
FXFileSelector::HideHidden = "\tHide Hidden Files\tHide hidden files and directories.";
FXFileSelector::DirectoryName = "DirectoryName";
FXFileSelector::NewDirectoryCaption = "Create New Directory";
FXFileSelector::CreateNewDirectoryIn = "Create new directory in: %s";
FXFileSelector::AlreadyExists = "Already Exists";
FXFileSelector::FileAlreadyExists = "File or directory %s already exists.\n";
FXFileSelector::CannotCreate = "Cannot Create";
FXFileSelector::CannotCreateDirectory = "Cannot create directory %s.\n";
FXFileSelector::CopyFile = "Copy File";
FXFileSelector::CopyOf = "CopyOf";
FXFileSelector::CopyFileBetween = "Copy file from location:\n\n%s\n\nto location:";
FXFileSelector::ErrorCopyingFile = "Error Copying File";
FXFileSelector::UnableToCopy = "Unable to copy file:\n\n%s  to:  %s\n\nContinue with operation?";
FXFileSelector::MoveFile = "Move File";
FXFileSelector::MoveFileBetween = "Move file from location:\n\n%s\n\nto location:";
FXFileSelector::ErrorMovingFile = "Error Moving File";
FXFileSelector::UnableToMove = "Unable to move file:\n\n%s  to:  %s\n\nContinue with operation?";
FXFileSelector::LinkFile = "Link File";
FXFileSelector::LinkTo = "LinkTo";
FXFileSelector::LinkFileBetween = "Link file from location:\n\n%s\n\nto location:";
FXFileSelector::ErrorLinkingFile = "Error Linking File";
FXFileSelector::UnableToLink = "Unable to link file:\n\n%s  to:  %s\n\nContinue with operation?";
FXFileSelector::DeleteFile = "Deleting files";
FXFileSelector::DeleteFileAt = "Are you sure you want to delete the file:\n\n%s";
FXFileSelector::ErrorDeletingFile = "Error Deleting File";
FXFileSelector::UnableToDelete = "Unable to delete file:\n\n%s\n\nContinue with operation?";
FXFileSelector::UpOneLevel = "Up one level";
FXFileSelector::HomeDirectory = "Home directory";
FXFileSelector::WorkDirectory = "Work directory";
FXFileSelector::SelectAll = "Select all";
FXFileSelector::SortBy = "Sort by";
FXFileSelector::Name = "Name";
FXFileSelector::Type = "Type";
FXFileSelector::Size = "Size";
FXFileSelector::Time = "Time";
FXFileSelector::User = "User";
FXFileSelector::Group = "Group";
FXFileSelector::Reverse = "Reverse";
FXFileSelector::IgnoreCase = "Ignore case";
FXFileSelector::View = "View";
FXFileSelector::SmallIcons = "Small icons";
FXFileSelector::BigIcons = "Big icons";
FXFileSelector::Details = "Details";
FXFileSelector::Rows = "Rows";
FXFileSelector::Columns = "Columns";
FXFileSelector::HiddenFiles = "Hidden files";
FXFileSelector::BookmarksMenu = "Bookmarks";
FXFileSelector::SetBookmarkMenu = "Set bookmark";
FXFileSelector::ClearBookmarkMenu = "Clear bookmarks";
FXFileSelector::NewDirectory = "New directory...";
FXFileSelector::Copy = "Copy...";
FXFileSelector::Move = "Move...";
FXFileSelector::Link = "Link...";
FXFileSelector::Delete = "Delete...";

//FXFontSelector
FXFontSelector::Accept = "&Accept";
FXFontSelector::Cancel = "&Cancel";
FXFontSelector::Family = "&Family:";
FXFontSelector::Weight = "&Weight:";
FXFontSelector::Style = "&Style:";
FXFontSelector::Size = "Si&ze:";
FXFontSelector::CharacterSet = "Character Set:";
FXFontSelector::Any = "Any";
FXFontSelector::WestEuropean = "West European";
FXFontSelector::EastEuropean = "East European";
FXFontSelector::SouthEuropean = "South European";
FXFontSelector::NorthEuropean = "North European";
FXFontSelector::Cyrillic = "Cyrillic";
FXFontSelector::Arabic = "Arabic";
FXFontSelector::Greek = "Greek";
FXFontSelector::Hebrew = "Hebrew";
FXFontSelector::Turkish = "Turkish";
FXFontSelector::Nordic = "Nordic";
FXFontSelector::Thai = "Thai";
FXFontSelector::Baltic = "Baltic";
FXFontSelector::Celtic = "Celtic";
FXFontSelector::Russian = "Russian";
FXFontSelector::SetWidth = "Set Width:";
FXFontSelector::UltraCondensed = "Ultra condensed";
FXFontSelector::ExtraCondensed = "Extra condensed";
FXFontSelector::Condensed = "Condensed";
FXFontSelector::SemiCondensed = "Semi condensed";
FXFontSelector::Normal = "Normal";
FXFontSelector::NormalLC = "normal";
FXFontSelector::SemiExpanded = "Semi expanded";
FXFontSelector::Expanded = "Expanded";
FXFontSelector::ExtraExpanded = "Extra expanded";
FXFontSelector::UltraExpanded = "Ultra expanded";
FXFontSelector::Pitch = "Pitch:";
FXFontSelector::Fixed = "Fixed";
FXFontSelector::Variable = "Variable";
FXFontSelector::Scalable = "Scalable:";
#ifndef WIN32
FXFontSelector::AllFonts = "All Fonts:";
#endif
FXFontSelector::Preview = "Preview:";
FXFontSelector::Thin = "thin";
FXFontSelector::ExtraLight = "extra light";
FXFontSelector::Light = "light";
FXFontSelector::Medium = "medium";
FXFontSelector::Demibold = "demibold";
FXFontSelector::Bold = "bold";
FXFontSelector::ExtraBold = "extra bold";
FXFontSelector::Black = "black";
FXFontSelector::Regular = "regular";
FXFontSelector::Italic = "italic";
FXFontSelector::Oblique = "oblique";
FXFontSelector::ReverseItalic = "reverse italic";
FXFontSelector::ReverseOblique = "reverse oblique";

//FXGLViewer
FXGLViewer::PrintScene = "Print Scene";
FXGLViewer::PrinterError = "Printer Error";
FXGLViewer::UnableToPrint = "Unable to print";

//FXInputDialog
FXInputDialog::OK = "&OK";
FXInputDialog::Cancel = "&Cancel";

//MDI buttons
FXMDIDeleteButton::Close = "\tClose\tClose Window.";
FXMDIRestoreButton::Restore = "\tRestore\tRestore Window.";
FXMDIMaximizeButton::Maximise = "\tMaximize\tMaximize Window.";
FXMDIMinimizeButton::Minimise = "\tMinimize\tMinimize Window.";
FXMDIWindowButton::Menu = "Menu";
FXMDIMenu::Next = "&Next\t\tNext window.";
FXMDIMenu::Previous = "&Previous\t\tPrevious window.";
FXMDIMenu::Restore = "&Restore\t\tRestore window.";
FXMDIMenu::Minimise = "&Minimize\t\tMinimize window.";
FXMDIMenu::Maximise = "&Maximize\t\tMaximize window.";
FXMDIMenu::Close = "&Close\t\tClose window.";

//FXMDIClient
FXMDIClient::SelectWindow = "Select Window";
FXMDIClient::OK = "&OK";
FXMDIClient::Cancel = "&Cancel";

//FXMessageBox
FXMessageBox::OK = "&OK";
FXMessageBox::Cancel = "&Cancel";
FXMessageBox::Yes = "&Yes";
FXMessageBox::No = "&No";
FXMessageBox::Quit = "&Quit";
FXMessageBox::Save = "&Save";
FXMessageBox::Skip = "&Skip";
FXMessageBox::SkipAll = "Skip &All";

//FXPrintDialog
FXPrintDialog::Print = "&Print";
FXPrintDialog::Cancel = "&Cancel";
#ifdef HAVE_CUPS_H
FXPrintDialog::UsingCUPS = "Using CUPS";
#endif
FXPrintDialog::PrintDestination = "Print Destination";
FXPrintDialog::Printer = "Pr&inter:";
FXPrintDialog::Properties = "Properties...";
FXPrintDialog::File = "&File:";
FXPrintDialog::Browse = "&Browse...";
FXPrintDialog::Pages = "Pages";
FXPrintDialog::PrintAll = "Print &All";
FXPrintDialog::EvenPages = "&Even Pages";
FXPrintDialog::OddPages = "&Odd Pages";
FXPrintDialog::PrintRange = "Print &Range:";
FXPrintDialog::From = "From:";
FXPrintDialog::To = "To:";
FXPrintDialog::Colours = "Colors";
FXPrintDialog::PrintInColour = "Print in Color";
FXPrintDialog::PrintInMonochrome = "Print in Black and White";
FXPrintDialog::Copies = "Copies";
FXPrintDialog::NumberOfCopies = "Number of copies to print:";
FXPrintDialog::CollateOrder = "Collate Order";
FXPrintDialog::FirstPageFirst = "First Page First";
FXPrintDialog::LastPageFirst = "Last Page First";
FXPrintDialog::Layout = "Layout";
FXPrintDialog::Portrait = "Portrait";
FXPrintDialog::Landscape = "Landscape";
FXPrintDialog::PaperSize = "Paper Size";
FXPrintDialog::OverwriteFile = "Overwrite file?";
FXPrintDialog::OverwriteExistingFile = "Overwrite existing file %s?";
FXPrintDialog::SelectOutputFile = "Select Output File";
FXPrintDialog::FileSelectionPatterns = "All Files  = *)\nPostscript Files  = *.ps,*.eps)";
#ifndef WIN32
FXPrintDialog::PrinterCommand = "Printer Command";
FXPrintDialog::SpecifyPrinterCommand = "Specify the printer command, for example:\n\n  \"lpr -P%s -#%d\" or \"lp -d%s -n%d\"\n\nThis will print \"%d\" copies to printer \"%s\".";
#endif

//FXProgressDialog
FXProgressDialog::Cancel = "&Cancel";

//FXReplaceDialog
FXReplaceDialog::Replace = "&Replace";
FXReplaceDialog::ReplaceAll = "Re&place All";
FXReplaceDialog::Cancel = "&Cancel";
FXReplaceDialog::SearchFor = "S&earch for:";
FXReplaceDialog::ReplaceWith = "Replace &with:";
FXReplaceDialog::Exact = "Ex&act";
FXReplaceDialog::IgnoreCase = "&Ignore Case";
FXReplaceDialog::Expression = "E&xpression";
FXReplaceDialog::Backward = "&Backward";
FXReplaceDialog::SearchLastCtlBTip = "Ctl-B";
FXReplaceDialog::SearchNextCtlFTip = "Ctl-F";

FXRex::errors[REGERR_OK]= "OK";
FXRex::errors[REGERR_EMPTY] = "Empty pattern";
FXRex::errors[REGERR_PAREN] = "Unmatched parenthesis";
FXRex::errors[REGERR_BRACK] = "Unmatched bracket";
FXRex::errors[REGERR_BRACE] = "Unmatched brace";
FXRex::errors[REGERR_RANGE] = "Bad character range";
FXRex::errors[REGERR_ESC] = "Bad escape sequence";
FXRex::errors[REGERR_COUNT] = "Bad counted repeat";
FXRex::errors[REGERR_NOATOM] = "No atom preceding repetition";
FXRex::errors[REGERR_REPEAT] = "Repeat following repeat";
FXRex::errors[REGERR_BACKREF] = "Bad backward reference";
FXRex::errors[REGERR_CLASS] = "Bad character class";
FXRex::errors[REGERR_COMPLEX] = "Expression too complex";
FXRex::errors[REGERR_MEMORY] = "Out of memory";
FXRex::errors[REGERR_TOKEN] = "Illegal token"

//FXSearchDialog
FXSearchDialog::Search = "&Search";

//FXStatusLine
FXStatusLine::defaultMessage = "Ready.";

//FXText
FXText::Search = "Search";
FXText::Replace = "Replace";
FXText::GotoLine = "Goto Line";
FXText::GotoLineNumber = "&Goto line number:";

// Undo List support
FXCommand::UndoStr = "Undo";
FXCommand::RedoStr = "Redo";

//FXWizard
FXWizard::Next = "&Next";
FXWizard::Finish = "&Finish";
FXWizard::Back = "&Back";
FXWizard::Cancel = "&Cancel";

}
