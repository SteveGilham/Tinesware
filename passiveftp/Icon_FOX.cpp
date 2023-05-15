
#include "fx.h"
#include "icons.h"

void SetIcons(void)
{
//FXColorSelector
FXColorSelector::Eyedropper = {GIF, eyedrop};
FXColorSelector::DialMode = {GIF, dialmode};
FXColorSelector::RGBMode = {GIF, rgbmode};
FXColorSelector::HSVMode = {GIF, hsvmode};
FXColorSelector::CMYMode = {GIF, cmymode};
FXColorSelector::TXTMode = {GIF, listmode};

//FXDirBox
FXDirBox::Folder = {GIF, minifolder};
FXDirBox::CDRom = {GIF, minicdrom};
FXDirBox::HardDisk = {GIF, miniharddisk};
FXDirBox::NetDrive = {GIF, mininetdrive};
FXDirBox::Floppy = {GIF, minifloppy};
FXDirBox::NetHood = {GIF, mininethood};
FXDirBox::ZipDrive = {GIF, minizipdrive};

//FXDriveBox
FXDriveBox::Folder = {GIF, minifolder};
FXDriveBox::CDRom = {GIF, minicdrom};
FXDriveBox::HardDisk = {GIF, miniharddisk};
FXDriveBox::NetDrive = {GIF, mininetdrive};
FXDriveBox::Floppy = {GIF, minifloppy};
FXDriveBox::NetHood = {GIF, mininethood};
FXDriveBox::ZipDrive = {GIF, minizipdrive};

//FXFileList
FXFileList::BigFolder = {GIF,bigfolder};
FXFileList::MiniFolder = {GIF,minifolder};
FXFileList::BigDoc = {GIF,bigdoc};
FXFileList::MiniDoc = {GIF,minidoc};
FXFileList::BigApp = {GIF,bigapp};
FXFileList::MiniApp = {GIF,miniapp};

//FXFileSelector
FXFileSelector::Dirupicon = {GIF, dirupicon};
FXFileSelector::Foldernew = {GIF, foldernew};
FXFileSelector::Bigfolder = {GIF, bigfolder};
FXFileSelector::Showsmallicons = {GIF, showsmallicons};
FXFileSelector::Showbigicons = {GIF, showbigicons};
FXFileSelector::Showdetails = {GIF, showdetails};
FXFileSelector::Gotohome = {GIF, gotohome};
FXFileSelector::Gotowork = {GIF, gotowork};
FXFileSelector::Fileshown = {GIF, fileshown};
FXFileSelector::Filehidden = {GIF, filehidden};
FXFileSelector::Bookset = {GIF, bookset};
FXFileSelector::Bookclr = {GIF, bookclr};
FXFileSelector::Filedelete = {GIF, filedelete};
FXFileSelector::Filemove = {GIF, filemove};
FXFileSelector::Filecopy = {GIF, filecopy};
FXFileSelector::Filelink = {GIF, filelink};

//MDI buttons
FXMDIMenu::MaxIcon = {GIF, winmaximize};
FXMDIMenu::MinIcon = {GIF, winminimize};
FXMDIMenu::ResIcon = {GIF, winrestore};
FXMDIMenu::CloIcon = {GIF, winclose};

//FXMessageBox
FXMessageBox::Error = {GIF, erroricon};
FXMessageBox::Info = {GIF, infoicon};
FXMessageBox::Question = {GIF, questionicon};
FXMessageBox::Warning = {GIF, warningicon};

//FXPrintDialog
FXPrintDialog::IPortrait = {GIF, portrait};
FXPrintDialog::ILandscape = {GIF, landscape};

//FXText
FXText::ISearch = {GIF, searchicon};
FXText::IGoto = {GIF, gotoicon};

//FXWizard
FXWizard::INext = {GIF, arrownext};
FXWizard::IPrevious = {GIF, arrowprev};
FXWizard::IEnter = {GIF, entericon};

}
