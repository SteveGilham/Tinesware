/* port_io.cpp : Defines the randomness abstractions for the application.
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


#ifdef WIN32
#include <windows.h>
#include <wincrypt.h>
#else 
#include <cstdio>
#include <string.h>
#define BYTE uint8_t
#define DWORD uint32_t
#endif

#include "rawrand.h"
#include "usrbreak.h"

namespace CTClib {

static BYTE * buffer = 0;
static DWORD allocated = 0;
static DWORD filled = 0;

#ifdef WIN32
class HCRYPTPROV_Manager {
public:
    HCRYPTPROV hProv;
    HCRYPTPROV_Manager() : hProv(0) {}
    ~HCRYPTPROV_Manager()
    {
        if(hProv)
            CryptReleaseContext(hProv, 0);
    }
};

static HCRYPTPROV_Manager hProv;
#endif

extern "C" void CTCRAN_DLL getRawRandom(unsigned char * data, int length) /* get N bytes of data */
{
    if(length > (int) filled)
        bug_check("Raw random data overdrawn");

    memcpy(data, buffer, length);

    DWORD left = filled - length;
    if(left > 0)
    {
        memmove(buffer, buffer+length, left);
    }
    filled = left;
}

    /* ensureRawRandom Returns true if (and when) the number of bytes requested  */
    /* is available.  Returns false if the user is not prepared to wait, or the  */
    /* data is otherwise not forthcoming.  */
extern "C" boolean CTCRAN_DLL ensureRawRandom(int bytes)
{
    if((int)filled > bytes)
        return TRUE;

    if(bytes > (int)allocated)
    {
        BYTE * newBuff = new BYTE[bytes];
        if(buffer)
        {
            if(filled) memcpy(newBuff, buffer, filled);
            delete[] buffer;
        }
        buffer = newBuff;
    }

#ifdef WIN32
    if(0 == hProv.hProv)
    {
        for(DWORD p = 0; p < 256 && !hProv.hProv; ++p)
        {
            CryptAcquireContext(
                &hProv.hProv,
                NULL,
                NULL,
                p,
                CRYPT_VERIFYCONTEXT
                );
        }
        if(0 == hProv.hProv) return FALSE;

    }

    boolean result = CryptGenRandom(
        hProv.hProv,
        bytes - filled,
        buffer + filled);
    if(result) filled = bytes;

    return result;

#else
    std::FILE * rand = std::fopen("/dev/random", "r");

    while(rand && ((int)filled > bytes) )
    {
        int got = fread(buffer+filled, 1, (size_t)(bytes-filled), rand);
        filled += got;
    }

    std::fclose(rand);

    return TRUE;
#endif

}

    /* getSessionData should return as much session specific data as is available */
    /* or the original value of *length whichever is shorter.  */
extern "C" void CTCRAN_DLL getSessionData(unsigned char * data, int * length)
{
    if(ensureRawRandom(*length))
        getRawRandom(data, *length);
    else
        *length = 0;
}

    /* setSessionData is provided only for the purposes of the demonstration,
           minimal function version of this module; it sets a buffer which
           is returned by getSessionData() */
    //void CTCRAN_DLL setSessionData(unsigned char * data, int *length);

} // namespace
