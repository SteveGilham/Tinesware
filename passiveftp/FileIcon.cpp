#ifdef _MSC_VER
#pragma warning (disable : 4800)
#endif

#include "FileIcon.h"

#ifdef WIN32
#include <winsock2.h>
#endif
#include "pftp.h"

#ifdef _MSC_VER
#pragma warning (disable : 4244)
#pragma warning (disable : 4310)
#endif
#include <fx.h>
#ifdef _MSC_VER
#pragma warning (default : 4244)
#endif
#include <map>
#include <algorithm>
#include <string>

#ifdef WIN32
#include <windows.h>
#endif

class IconMap2Deleter
{
    public:
        IconMap2Deleter();
        void operator ( ) (std::pair<std::string, FXIcon *> p);
};

IconMap2Deleter::IconMap2Deleter()
{}
void IconMap2Deleter::operator ( ) (std::pair<std::string, FXIcon *> p)
{
    delete(p.second);
}

class IconHolder {
    public:
        FXIcon * bigFolder;
        FXIcon * smallFolder;
        std::map<std::string, FXIcon*> bigExt;
        std::map<std::string, FXIcon*> smallExt;
        std::map<std::string, FXIcon*> bigName;
        std::map<std::string, FXIcon*> smallName;
        IconHolder() : bigFolder(NULL), smallFolder(NULL) {}
        ~IconHolder()
        {
            if(bigFolder)
                delete bigFolder;
            if(smallFolder)
                delete smallFolder;
            std::for_each(bigExt.begin(), bigExt.end(), IconMap2Deleter());
            std::for_each(smallExt.begin(), smallExt.end(), IconMap2Deleter());
            std::for_each(bigName.begin(), bigName.end(), IconMap2Deleter());
            std::for_each(smallName.begin(), smallName.end(), IconMap2Deleter());
        }
};

static IconHolder holder;

#ifdef WIN32
FXIcon * getIcon(HICON h)
{
    ICONINFO info;
    if(!GetIconInfo(h, &info))
        return NULL;

    HDC screen = ::GetDC(NULL);
    HDC mem = CreateCompatibleDC(screen);

    BITMAPINFO * bmi;
    char bmic[sizeof(BITMAPINFOHEADER)+256*sizeof(RGBQUAD)];
    memset(bmic, 0, sizeof(bmic));
    bmi = (BITMAPINFO *)bmic;
    bmi->bmiHeader.biSize = sizeof(BITMAPINFOHEADER);

    int stat = GetDIBits(
        mem, info.hbmMask,
        0, 0, NULL, bmi, DIB_RGB_COLORS);
    if(!stat)
    {
        DeleteDC(mem);
        return NULL;
    }

    int rowlength = 4*((bmi->bmiHeader.biWidth * bmi->bmiHeader.biBitCount + 31) / 32);
    char * mask = new char[rowlength * bmi->bmiHeader.biHeight];

    stat = GetDIBits(
        mem, info.hbmMask,
        0, bmi->bmiHeader.biHeight, mask, bmi, DIB_RGB_COLORS);
    if(!stat)
    {
        delete [] mask;
        DeleteDC(mem);
        return NULL;
    }

    memset(bmic, 0, sizeof(bmic));
    bmi->bmiHeader.biSize = sizeof(BITMAPINFOHEADER);
    stat = GetDIBits(
        mem, info.hbmColor,
        0, 0, NULL, bmi, DIB_RGB_COLORS);
    if(!stat)
    {
        delete [] mask;
        DeleteDC(mem);
        return NULL;
    }

    int rowlength2 = 4 * ((bmi->bmiHeader.biWidth * bmi->bmiHeader.biBitCount + 31) / 32);
    char * icon = new char[rowlength2 * bmi->bmiHeader.biHeight];
    stat = GetDIBits(
        mem, info.hbmColor,
        0, bmi->bmiHeader.biHeight, icon, bmi, DIB_RGB_COLORS);
    DeleteDC(mem);
    if(!stat)
    {
        delete [] icon;
        delete [] mask;
        return NULL;
    }

    int redmask = *((int*)(&bmi->bmiColors[0]));
    int greenmask = *((int*)(&bmi->bmiColors[1]));
    int bluemask = *((int*)(&bmi->bmiColors[1]));

    int redshift = 0;
    int redsize = 0;
    while(!((redmask>>redshift)%2)) {++redshift;}
    while((redmask>>(redsize+redshift))%2) {++redsize;}
    redshift -= (8-redsize);

    int greenshift = 0;
    int greensize = 0;
    while(!((greenmask>>greenshift)%2)) {++greenshift;}
    while((greenmask>>(greensize+greenshift))%2) {++greensize;}
    greenshift -= (8-greensize);

    int blueshift = 0;
    int bluesize = 0;
    while(!((bluemask>>blueshift)%2)) {++blueshift;}
    while((bluemask>>(bluesize+blueshift))%2) {++bluesize;}
    blueshift -= (8-bluesize);


    RGBQUAD * icon2 = new RGBQUAD[bmi->bmiHeader.biHeight * bmi->bmiHeader.biWidth];
    memset(icon2, 0, 4*bmi->bmiHeader.biHeight * bmi->bmiHeader.biWidth);
    for(int row=0; row<bmi->bmiHeader.biHeight; ++row)
    {
        int urow = bmi->bmiHeader.biHeight-(row+1);
        int roff = row * rowlength2;
        int uoff = urow * bmi->bmiHeader.biWidth;

        for(int i=0; i<bmi->bmiHeader.biWidth; ++i)
        {
            int boff = i/8;
            int over = i%8;

            int xoff = uoff+i;
            int coff;
             

            if(mask[boff+row*rowlength]<<over & 0x80)
            {
                icon2[xoff].rgbReserved = (BYTE)0;
            }
            else
            {
                icon2[xoff].rgbReserved = (BYTE)255;
                switch(bmi->bmiHeader.biBitCount)
                {
                default: break;
                case 32: 
                    {
                        coff = roff + 4*i;
                        icon2[xoff].rgbRed = icon[coff];
                        icon2[xoff].rgbGreen = icon[coff+1];
                        icon2[xoff].rgbBlue = icon[coff+2];
                        break;
                    }
                case 24: 
                    {
                        coff = roff + 3*i;
                        icon2[xoff].rgbRed = icon[coff];
                        icon2[xoff].rgbGreen = icon[coff+1];
                        icon2[xoff].rgbBlue = icon[coff+2];
                        break;
                    }
                case 16: 
                    {
                        coff = roff + 2*i;
                        unsigned short rgb16 = icon[coff] + 256*icon[coff+1];
                        switch(bmi->bmiHeader.biCompression)
                        {
                        default: break;
                        case BI_RGB:
                            icon2[xoff].rgbRed = (BYTE) ((rgb16>>10)&31)*8;  // Red
                            icon2[xoff].rgbGreen = (BYTE) ((rgb16>>5)&31)*8;  // Green
                            icon2[xoff].rgbBlue = (BYTE) (rgb16&31)*8;  // Blue
                            break;
                        case BI_BITFIELDS:
                            int red = (rgb16 & redmask);
                            if(redshift>0) red>>=redshift;
                            else red<<=(-redshift);

                            int green = (rgb16 & greenmask);
                            if(greenshift>0) green>>=greenshift;
                            else green<<=(-greenshift);

                            int blue = (rgb16 & bluemask);
                            if(blueshift>0) blue>>=blueshift;
                            else blue<<=(-blueshift);


                            icon2[xoff].rgbBlue = (BYTE) red;  // Red
                            icon2[xoff].rgbGreen = (BYTE) green;  // Green
                            icon2[xoff].rgbRed = (BYTE) blue;  // Blue
                            break;
                        }
                        break;
                    }
                }

            }
        }
    }

    FXColor transp  = FXRGB(204, 204, 204);
	FXColor * punned = reinterpret_cast<FXColor*>(icon2);
	transp = punned[0];
    FXIcon * result = new FXIcon(FXApp::instance(), punned, transp, 
        IMAGE_ALPHACOLOR, bmi->bmiHeader.biWidth, bmi->bmiHeader.biHeight);
    result->create();

    delete [] icon;
    delete [] mask;
    delete [] icon2;
    return result;
}
#endif

#if 0
bool getNameIcons(const FXString & ext, FX::FXIcon ** bigI, FX::FXIcon ** smallI, bool there)
{
    SHFILEINFO sfi;
    *bigI = NULL;
    *smallI = NULL;

    if(holder.bigExt.find(ext) != holder.bigExt.end())
    {
        *bigI = holder.bigExt[ext];
        *smallI = holder.smallExt[ext];
        return true;
    }

    DWORD_PTR v = SHGetFileInfo( ext.text(), FILE_ATTRIBUTE_NORMAL, &sfi, sizeof( SHFILEINFO ), 
               SHGFI_ICON | SHGFI_SHELLICONSIZE | SHGFI_USEFILEATTRIBUTES );
    if(v)
    {
        *bigI = getIcon(sfi.hIcon);
        DestroyIcon(sfi.hIcon);

        if(*bigI)
            v = SHGetFileInfo( ext.text(), FILE_ATTRIBUTE_NORMAL, &sfi, sizeof( SHFILEINFO ), 
               SHGFI_ICON | SHGFI_SHELLICONSIZE | SHGFI_SMALLICON | SHGFI_USEFILEATTRIBUTES);
        else
            v = 0;

        if(v)
        {
            *smallI = getIcon(sfi.hIcon);
            if(!*smallI)
            {
                delete *bigI;
                *bigI = NULL;
            }
            DestroyIcon(sfi.hIcon);
        }
        else
        {
            delete *bigI;
            *bigI = NULL;
        }
    }

    if(*bigI)
    {
        holder.bigExt[ext] = *bigI;
        holder.smallExt[ext] = *smallI;
    }
    return (*bigI) != 0;
}
#endif

bool getNameIcons(const FXString &
#ifdef WIN32
 extIn
#endif
, FX::FXIcon ** bigI, FX::FXIcon ** smallI, bool 
#ifdef WIN32
there
#endif
)
{
    *bigI = NULL;
    *smallI = NULL;

#ifdef WIN32
#ifdef _DEBUG
    char message[1024];
#endif

    DWORD attributes = there ? 0 : FILE_ATTRIBUTE_NORMAL;
    UINT attrFlag = there ? 0 : SHGFI_USEFILEATTRIBUTES;
    SHFILEINFO sfi;
    std::string ext(extIn.text());
    std::string key;
    {
        char drive[_MAX_DRIVE];
        char dir[_MAX_DIR];
        char fname[_MAX_FNAME];
        char extbuff[_MAX_EXT];

        _splitpath( ext.c_str(), drive, dir, fname, extbuff );

        key = extbuff;
    }


    if(there)
    {

        DWORD_PTR what = SHGetFileInfo( ext.c_str(), attributes, &sfi, sizeof( SHFILEINFO ), 
               SHGFI_EXETYPE);

        if(what) // an executable
        {
            key = ext;
        }

#ifdef _DEBUG
        sprintf(message, "(there) looking for icon for %s\n", ext.c_str());
        OutputDebugString(message);
#endif
        if(holder.bigName.find(key) != holder.bigName.end())
        {
#ifdef _DEBUG
        sprintf(message, "(there) found icon for %s\n", ext.c_str());
        OutputDebugString(message);
#endif
            *bigI = holder.bigName[key];
            *smallI = holder.smallName[key];
            return true;
        }
    }
    else
    {
#ifdef _DEBUG
        sprintf(message, "looking for icon for %s\n", ext.c_str());
        OutputDebugString(message);
#endif
        if(holder.bigExt.find(key) != holder.bigExt.end())
        {
#ifdef _DEBUG
        sprintf(message, "found icon for %s\n", ext.c_str());
        OutputDebugString(message);
#endif
            *bigI = holder.bigExt[key];
            *smallI = holder.smallExt[key];
            return true;
        }
    }

    DWORD_PTR v = SHGetFileInfo( ext.c_str(), attributes, &sfi, sizeof( SHFILEINFO ), 
               SHGFI_ICON | SHGFI_SHELLICONSIZE | attrFlag);
    if(v)
    {
#ifdef _DEBUG
        sprintf(message, "new wanted icon for %s\n", ext.c_str());
        OutputDebugString(message);
#endif


        *bigI = getIcon(sfi.hIcon);
        DestroyIcon(sfi.hIcon);

        if(*bigI)
            v = SHGetFileInfo( ext.c_str(), attributes, &sfi, sizeof( SHFILEINFO ), 
               SHGFI_ICON | SHGFI_SHELLICONSIZE | SHGFI_SMALLICON | attrFlag);
        else
            v = 0;

        if(v)
        {
            *smallI = getIcon(sfi.hIcon);
            if(!*smallI)
            {
                delete *bigI;
                *bigI = NULL;
            }
            DestroyIcon(sfi.hIcon);
        }
        else
        {
            delete *bigI;
            *bigI = NULL;
        }
    }

    if(*bigI)
    {
        if(there)
        {
            holder.bigName[key] = *bigI;
            holder.smallName[key] = *smallI;
        }
        else
        {
#ifdef _DEBUG
            sprintf(message, "saving for icon for %s\n", ext.c_str());
            OutputDebugString(message);
#endif
            holder.bigExt[key] = *bigI;
            holder.smallExt[key] = *smallI;
        }
    }
#endif
    return (*bigI) != 0;
}

bool getFolderIcons(FX::FXIcon ** bigI, FX::FXIcon ** smallI)
{
    return getFileIcons(".", bigI, smallI); // file delimiter
}

bool getFileTypeIcons(const FXString & ext, FX::FXIcon ** bigI, FX::FXIcon ** smallI)
{
    return getNameIcons(ext, bigI, smallI, false);
}

bool getFileIcons(const FXString & ext, FX::FXIcon ** bigI, FX::FXIcon ** smallI)
{
    return getNameIcons(ext, bigI, smallI, true);
}
