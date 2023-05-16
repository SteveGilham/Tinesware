/* strings.cpp : Defines the entry point for the localization dll.
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


#include "strings.h"
#include "resource.h"
#include <stdlib.h> /* mbtowc should be defined here */

//===================================================================
//===================================================================
#if defined(WIN32) || defined (__BORLANDC__)
#define WIN32_LEAN_AND_MEAN
#include <windows.h>

BOOL APIENTRY DllMain( HANDLE hModule, 
                       DWORD  ul_reason_for_call, 
                       LPVOID lpReserved
					 )
{
	switch (ul_reason_for_call)
	{
	case DLL_PROCESS_ATTACH:
	case DLL_THREAD_ATTACH:
	case DLL_THREAD_DETACH:
	case DLL_PROCESS_DETACH:
		break;
	}
    return TRUE;
}
#else
#include <string.h>
#endif // def WIN32
//===================================================================
//===================================================================

namespace CTCFox {

const char * Strings::load(ID resourceNumber)
{
    switch(resourceNumber)
    {
    case FILE:
        return _("&File");
    case NEW:
        return _("&New\tCtrl-N\tCreate new document.");
    case NEW_TOOL:
        return _("\tNew\tCreate new document.");
    case OPEN:
        return _("&Open...\tCtrl-O\tOpen document file.");
    case OPEN_TOOL:
        return _("\tOpen\tOpen document file.");
    case READPOP3:
        return _("&Read POP3 Mail...\tCtrl-R\tRead files from POP3 mail.");
    case READPOP3_TOOL:
        return _("\tRead POP3 Mail\tRead files from POP3 mail.");
    case EXIT:
        return _("E&xit");
    case HELP:
        return _("&Help\t\tAbout CTCFox and code it uses.");
    case HELP_TOOL:
        return _("\tHelp\tAbout CTCFox and code it uses.");
    case ABOUT:
        return _("&About...");
    case ABOUTIDEA:
        return _("About &IDEA");
    case KEYS:
        return _("&Keys");
    case GENERATE:
        return _("&Generate...\t\tGenerate new keypair.");
    case LOCKALL:
        return _("&Lock All\t\tLock all key pairs.");
    case SAVE:
        return _("&Save\tCtrl-S\tSave changes to file.");
    case SAVE_TOOL:
        return _("\tSave\tSave changes to file.");
    case SAVEAS:
        return _("Save &As...\t\tSave document to another file.");
    case SEND:
        return _("Send via S&MTP...\t\tSend document via SMTP mail.");
    case PRINT:
        return _("&Print...\t\tPrint document.");
    case ABOUT_CTCFOX:
        return _("About CTCFox");
    case TAB_ABOUT:
        return _("&About");
    case OK:
        return _("&OK");
    case TINES:
        return _("\tA Tines pack\tA Tines pack (art by A. Taylor after V. Vinge)");
    case TAB_GPL:
        return _("&Licence (GPL)");
    case TAB_IDEA:
        return _("&Licence (IDEA)");
    case PUBKEYS:
        return _("Public keys");
    case SECKEYS:
        return _("Private keys");
    case MESSAGES:
        return _("Messages");
    case PUBRING:
        return _("Public key ring");
    case SECRING:
        return _("Private key ring");
    case QUOTE:
        return _("Quote string");
    case ITEM:
        return _("Item");
    case VALUE:
        return _("Value");
    case SMTP_TOOL:
        return _("\tSend SMTP Mail\tSend file via SMTP mail.");
    case PRINT_TOOL:
        return _("\tPrint\tPrint the file.");
    case CUT_TOOL:
        return _("\tCut\tCut selection to clipboard.");
    case COPY_TOOL:
        return _("\tCopy\tCut selection to clipboard.");
    case PASTE_TOOL:
        return _("\tPaste\tPaste from clipboard.");
    case UNLOCK_TOOL:
        return _("\tDecrypt/Validate\tDecrypt and/or check signature.");
    case LOCK_TOOL:
        return _("\tEncrypt/Sign\tEncrypt and/or create signature.");
    case EXTRACT:
        return _("&Extract\t\tExtract copy of key to file.");
    case SIGN:
        return _("&Sign\t\tAdd new signature to user identity.");
    case ENABLE:
        return _("En/Dis&able\t\tToggle key enabled/disable state.");
    case DELKEY:
        return _("&Delete\t\tDelete key from ring.");
    case KEYLOCK:
        return _("Loc&k\t\tLock secret key.");
    case REVOKE:
        return _("&Revoke...\t\tRevoke key.");
    case ADDID:
        return _("Add &ID\t\tAdd new user identity to key.");
    case PASSPHRASE:
        return _("&Change Passphrase\t\tChange passphrase.");
    case EDIT:
        return _("&Edit");
    case CUT:
        return _("Cu&t\tCtrl-X\tCut selection to clipboard.");
    case COPY:
        return _("&Copy\tCtrl-C\tCut selection to clipboard.");
    case PASTE:
        return _("&Paste\tCtrl-V\tPaste from clipboard.");
    case ROT:
        return _("&ROT 13\t\tROT 13 encode selection.");
    case ENQUOTE:
        return _("&Quote\t\tMark selection as quoted.");
    case CRYPTO:
        return _("&Crypto");
    case DECRYPT:
        return _("&Decrypt/Validate...\t\tDecrypt and/or check signature.");
    case EXKEY:
        return _("E&xtract session key...\t\tExtract session key for limited key disclosure.");
    case ENCRYPT:
        return _("&Encrypt/Sign...\t\tEncrypt and/or create signature.");
    case CLEARSIGN:
        return _("&Clearsign...\t\tAttach signature to text.");
    case DETSIG:
        return _("Detached &Signature...\t\tCreate signature separate from file.");
    case SIGNONLY:
        return _("Sign &Only...\t\tCreate signature.");
    case SIG_UNKNOWN:
        return _("Signature by unknown key");
    case SIG_ULTIMATE:
        return _("Ultimately trusted signature");
    case SIG_GOOD:
        return _("Good signature");
    case SIG_BAD:
        return _("Bad signature");
    case KEY_TYPE:
        return _("Key Algorithm");
    case KEY_ID:
        return _("Key Identifer");
    case KEY_DATE:
        return _("Created at");
    case KEY_PRINT:
        return _("Key fingerprint");
    case KEY_BITS:
        return _("Key length in bits");
    case SIG_KEY_ID:
        return _("Signing key identifier");
    case SIG_DATE:
        return _("Signed at");
    case SIG_CLASS:
        return _("Purpose of signature");
    case CLOSE:
        return _("&Close\t\tClose this file.");
    case UNTITLED:
        return _("Untitled");
    case OPEN_FILE:
        return _("Open");
    case BINARY_FILE:
        return _("This is a binary file.\nIt is not shown in this window.");
    case FILE_HAS_CHANGED:
        return _("The file has changed.  Do you wish to save it?");
    case SAVE_FILE:
        return _("Save File");
    case FILE_EXISTS:
        return _("File exists.  Overwrite?");
    case FILE_READONLY:
        return _("File cannot be written");
    case SAVE_MODIFIED:
        return _("Save Modified File");
    case SELECT_ALL:
        return _("Select &All");
    case SELECT_NONE:
        return _("Select &None");
    case CANCEL:
        return _("Cancel");
    case ENTER_PASSPHRASE:
        return _("Enter Passphrase");
    case KEY_COLON:
        return _("Key: ");
    case CHECKSUM_COLON:
        return _("Checksum: ");
    case BADPHRASE:
        return _("Incorrect passphrase.");
    case OUTOFTIME:
        return _("Too many tries.");

    default:
        return "?";
    }
}

//===================================================================

const char * Strings::load2nd(ID resourceNumber)
{
    const char * tmp = load(resourceNumber);
    const char * x = tmp;
    while(*x != '\t' && *x)
    {
        wchar_t charVal=0;
        int clen = mbtowc(&charVal, x, MB_CUR_MAX);
        if(clen < 1) clen = 1;        /* skib a byte on invalid character or NULL */
        x += clen;
    }
    return (x && *x) ? x : tmp;
}

//===================================================================

const char * Strings::getCBInfoCodes(int code, int severity)
{
    switch (code)
    {
    // CTC engine
    case 0x010101:
        return _("Searching for armoured block to process.");
    case 0x010302:
        return _("Armouring_output");
    case 0x010303:
        return _("File error while writing signature.");

    case 0x010500:
        if(severity < 3) return _("While decompressing.");
        return _("Decompressing.");

    case 0x010501:
        return _("Failure while decompressing message.");

    case 0x010600:
    case 0x010601:
    case 0x010602:
        return _("Encrypting message body.");

    case 0x010702:
        return _("Encrypting message body.");

    case 0x010801:
        return _("Decrypting message body.");

    case 0x010902:
        return _("Encrypting message to key ");

    case 0x010b02:
        return _("Signing from key ");

    case 0x010d01:
        return _("No armoured blocks found in text.");

    case 0x010e00:
    case 0x010e01:
    case 0x010e02:
        return _("Unimplemented message digest algorithm expected.");

    case 0x010f00:
    case 0x010f01:
    case 0x010f02:
        return _("Unimplemented compression algorithm expected.");

    case 0x011301:
    case 0x011302:
        return _("Could not open temprary file for decompression.");

    case 0x011601:
        return _("Bad session key.");

    case 0x011700:
    case 0x011701:
    case 0x011702:
        return _("File input error.");

    case 0x011800:
    case 0x011801:
    case 0x011802:
        return _("File output error.");

    case 0x011901:
        return _("Only the public part of the decryption key is available.");

    case 0x011A04:
        return _("Public key unavailable for verification.");

    case 0x011B01:
        return _("Unknown cypher-type byte encountered.");

    case 0x011f00:
    case 0x011f01:
    case 0x011f02:
        return _("No memory for digest calculation.");

    case 0x012001:
        return _("PGP2.6-style comment found : ");

    case 0x012101:
        return _("No secret key is available for decryption.");

    case 0x012304:
        return _("Non-signature data in clearsigned message signature.");

    case 0x020601:
    case 0x020602:
    case 0x020603:
    case 0x020604:
    case 0x020605:
    case 0x020606:
        return _("Armouring of file failed.");

        // Public key operations
    case 0x060101:
        return _("Out of memory while public key decrypting.");

    case 0x060102:
        return _("Out of memory while public key encrypting.");

    case 0x060103:
        return _("Out of memory while signing.");

    case 0x060104:
        return _("Out of memory while verifying signature.");

    case 0x060105:
        return _("Out of memory while generating keypair.");

    case 0x060201:
        return _("User interrupt taken while public key decrypting.");

    case 0x060202:
        return _("User interrupt taken while public key encrypting.");

    case 0x060203:
        return _("User interrupt taken while signing.");

    case 0x060204:
        return _("User interrupt taken while verifying signature.");

    case 0x060205:
        return _("User interrupt taken while generating keypair.");

    case 0x060302:
        return _("File I/O error while public key encrypting.");

    case 0x060305:
        return _("File I/O error while generating keypair.");

    case 0x060401:
        return _("Check data not found in conventional key packet.");

    case 0x060402:
        return _("Unrecognised conventional key packet format.");

    case 0x060403:
    case 0x060804:
        return _("Unrecognised message digest packet format.");

    case 0x060404:
        return _("Message digest does not match check bytes.");

    case 0x060601:
        return _("Required conventional algorithm unavailable for decryption.");

    case 0x060701:
        return _("Required conventional cypher mode unavailable for decryption.");

    case 0x060902:
    case 0x060904:
        return _("Public key too short for required conventional key packet.");

    case 0x060a01:
        return _("Fatal error in public key decryption.");

    case 0x060a02:
        return _("Fatal error in public key encryption.");

    case 0x060a03:
        return _("Fatal error in signature.");

    case 0x060a04:
        return _("Fatal error in verification.");

    case 0x060a05:
        return _("Fatal error in keypair generation.");

    case 0x060b05:
        return _("Key generation begun.");

    case 0x060c05:
        return _("Generating first prime number.");

    case 0x060d05:
        return _("Generating second prime number.");

    case 0x060e05:
        return _("Concluding key generation.");

    case 0x060f01:
        return _("Required public key algorithm unavailable for decryption.");

    case 0x060f02:
        return _("Required public key algorithm unavailable for encryption.");

    case 0x060f03:
        return _("Required public key algorithm unavailable for signature.");

    case 0x060f04:
        return _("Required public key algorithm unavailable for verification.");

        // Key manager
    case 0x070208:
        return _("Unexpected duplicate key IDs (DEADBEEF attack).");

    case 0x070308:
        return _("Key in inconsistent state.");

    case 0x070408:
        return _("Could not read complete key from ring.");

    case 0x070701:
        return _("Public key found.");

    case 0x070801:
        return _("Secret key for decryption found.");

    case 0x070901:
        return _("User ID for decryption found.");

    case 0x070A01:
        return _("Key signature found.");

    case 0x070b01:
        return _("Key without userID found.");

    case 0x070d01:
        return _("Could not validate key revocation (key dubious)e.");

    case 0x070e01:
        return _("Bad key revocation (key dubious).");

    case 0x070f01:
        return _("Improperly revoked key found (wrong key used to revoke).");

    case 0x071001:
        return _("Bad key signature found.");

    case 0x071101:
        return _("Key signature found by unknown key.");

        // I/O processing
    case 0x080101:
    case 0x080107:
        return _("Unsupported version byte detected.");

    case 0x080201:
    case 0x080207:
        return _("Unsupported algorithm byte detected.");

    case 0x080301:
    case 0x080307:
        return _("File I/O error.");

    case 0x080401:
    case 0x080407:
        return _("Bad length value in packet.");

    case 0x080501:
    case 0x080507:
        return _("Unexpected record type encountered.");

    case 0x080601:
    case 0x080607:
        return _("File does not appear to be cyphertext.");

    case 0x080701:
    case 0x080707:
        return _("Secret key not allocated.");

    case 0x080801:
    case 0x080807:
        return _("No memory for I/O.");

    default:
        break;
    }
    return 0;
}

//===================================================================

const char * Strings::getCBExceptCodes(int code, bool revoked)
{
    switch (code)
    {
        // Armouring
    case 0x03020101:
        return _("Line limit exhausted.");
    case 0x03020201:
        return _("User interruption taken.");
    case 0x03020301:
        return _("File I/O error.");
    case 0x03020401:
        return _("CRC check in armour failed.");
    case 0x03020501:
        return _("Format error in armoured block of type ");
    case 0x03020601:
        return _("Unknown format type.");

        // Key management
    case 0x05070501:
        if(revoked)
            return _("Key revocation certificate found ");
        return _("New public key found ");
    case 0x05070601:
        return _("New secret key found ");
    default:
        return 0;
    }
}

//===================================================================

const char * Strings::getCBExceptQuestions(int code)
{
    switch (code)
    {
        // Key management
    case 0x05070501:
        return _("Add to key ring?");
    case 0x05070601:
        return _("Add to key rings?");
    default:
        return _("Continue?");
    }
}

} // namespace

//===================================================================
//===================================================================

/*
// This is an example of an exported variable
STRINGS_API int nstrings=0;

// This is an example of an exported function.
STRINGS_API int fnstrings(void)
{
	return 42;
}

// This is the constructor of a class that has been exported.
// see strings.h for the class definition
Cstrings::Cstrings()
{ 
	return; 
}
*/
