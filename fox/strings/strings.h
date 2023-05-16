/* ctcfox.cpp : Defines the entry point for the localization hooks.
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

#ifndef ctcfox_strings
#define ctcfox_strings

// The following ifdef block is the standard way of creating macros which make exporting 
// from a DLL simpler. All files within this DLL are compiled with the STRINGS_EXPORTS
// symbol defined on the command line. this symbol should not be defined on any project
// that uses this DLL. This way any other project whose source files include this file see 
// STRINGS_API functions as being imported from a DLL, whereas this DLL sees symbols
// defined with this macro as being exported.

#if defined(WIN32) || defined (__BORLANDC__)
 #ifdef STRINGS_EXPORTS
  #define STRINGS_API __declspec(dllexport)
 #else
  #define STRINGS_API __declspec(dllimport)
 #endif
#else
 #define STRINGS_API
#endif

// These macros are actually internal to the strings shared library
// so ensure that they will not build on consumers on Win32
// (it's easier to make the check there than it is on Linux)

#if defined(WIN32) || defined (__BORLANDC__)
#  ifdef STRINGS_EXPORTS
#    define _(String) (String)
#    define N_(String) (String)
#  else
#    define _(x)  ???
#    define N_(x) ???
#  endif
#else
#    include <libintl.h>
#    define _(String) gettext (String)
#    define N_(String) (String)
#endif

namespace CTCFox {

// This class is exported from the strings.dll
class STRINGS_API Strings {
public:
    enum ID {
        FILE,
        NEW,
        OPEN,
        READPOP3,
        TINES,
        EXIT,
        HELP,
        ABOUT,
        ABOUTIDEA,
        KEYS,
        GENERATE,
        LOCKALL,
        SAVE,
        SAVEAS,
        ABOUT_CTCFOX,
        TAB_ABOUT,
        OK,
        NEW_TOOL,
        OPEN_TOOL,
        SAVE_TOOL,
        READPOP3_TOOL,
        HELP_TOOL,
        TAB_GPL,
        TAB_IDEA,
        PUBKEYS,
        SECKEYS,
        MESSAGES,
        PUBRING,
        SECRING,
        QUOTE,
        ITEM,
        VALUE,
        SMTP_TOOL,
        PRINT_TOOL,
        CUT_TOOL,
        COPY_TOOL,
        PASTE_TOOL,
        UNLOCK_TOOL,
        LOCK_TOOL,
        EXTRACT,
        SIGN,
        ENABLE,
        DELKEY,
        KEYLOCK,
        REVOKE,
        ADDID,
        PASSPHRASE,
        EDIT,
        CUT,
        COPY,
        PASTE,
        ROT,
        ENQUOTE,
        CRYPTO,
        DECRYPT,
        EXKEY,
        ENCRYPT,
        CLEARSIGN,
        DETSIG,
        SIGNONLY,
        SEND,
        PRINT,
        SIG_UNKNOWN,
        SIG_ULTIMATE,
        SIG_GOOD,
        SIG_BAD,
        KEY_TYPE,
        KEY_ID,
        KEY_DATE,
        KEY_PRINT,
        KEY_BITS,
        SIG_KEY_ID,
        SIG_DATE,
        SIG_CLASS,
        CLOSE,
        UNTITLED,
        OPEN_FILE,
        BINARY_FILE,
        FILE_HAS_CHANGED,
        SAVE_FILE,
        FILE_EXISTS,
        FILE_READONLY,
        SAVE_MODIFIED,
        SELECT_ALL,
        SELECT_NONE,
        CANCEL,
        ENTER_PASSPHRASE,
        KEY_COLON,
        CHECKSUM_COLON,
        BADPHRASE,
        OUTOFTIME,
   };
    static const char * load(ID resourceNumber);
    static const char * load2nd(ID resourceNumber);
    static const char * libEnumName(int enumeration, int value, bool full);
    static const char * getCBInfoCodes(int code, int severity);
    static const char * getCBExceptCodes(int code, bool revoked);
    static const char * getCBExceptQuestions(int code);

private:
    Strings(void);
	// TODO: add your methods here.
};

} // namespace

/*
extern STRINGS_API int nstrings;

STRINGS_API int fnstrings(void);
*/

//extern "C" STRINGS_API const char * loadString(int resourceNumber);
//extern "C" STRINGS_API const char * loadString2(int resourceNumber);

#endif // ctcfox_strings
