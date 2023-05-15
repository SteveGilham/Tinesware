#ifndef FileIcon_h
#define FileIcon_h

namespace FX {
class FXIcon;
class FXString;
}

bool getNameIcons(const FX::FXString & ext, FX::FXIcon ** bigI, FX::FXIcon ** smallI, bool there);

bool getFolderIcons(FX::FXIcon ** bigI, FX::FXIcon ** smallI);
bool getFileTypeIcons(const FX::FXString & ext, FX::FXIcon ** bigI, FX::FXIcon ** smallI);
bool getFileIcons(const FX::FXString & ext, FX::FXIcon ** bigI, FX::FXIcon ** smallI);

#endif
