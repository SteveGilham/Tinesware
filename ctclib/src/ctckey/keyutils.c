/* keyutils.c
 **
 **  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 **  Heimdall <heimdall@bifroest.demon.co.uk>  1996
 **  All rights reserved.  For full licence details see file licences.c
 **
 **  This file includes some modified code from
 **  PGP: Pretty Good(tm) Privacy - public key cryptography for the masses.
 ** (c) Copyright 1990-1992 by Philip Zimmermann.
 **
 **  Note that this file along with pkcipher.c contains some branches
 **  conditioned on the public key algorithm in use.  -- Mr. Tines 25-Mar-1997
 */
#include <assert.h>
#include <string.h>
#include "bignums.h"
#include "keyutils.h"
#include "keyhash.h"
#include "hash.h"
#include "hashpass.h"
#include "pkcipher.h"
#include "random.h"
#include "usrbreak.h"
#include "utils.h"
#include "ctc.h"

boolean set_passphrase(seckey * sec_key, char * passphrase)
{
    return set_passphrase_UTF8(sec_key, passphrase, TRUE);
}
boolean set_passphrase_UTF8(seckey * sec_key, char * passphrase, boolean convertToUTF8)
{
    pubkey * pub_key;

    if(sec_key->skstat != INTERNAL) return FALSE;

    if(!passphrase || strlen(passphrase) == 0)
    {
        sec_key->kpalg.cv_algor = CEA_NONE;
        sec_key->kpalg.cv_mode = 0;
    }
    else if(sec_key->kpalg.cv_algor != CEA_NONE)
    {
        int count = cipherBlock(sec_key->kpalg.cv_algor);
        int key = cipherKey(sec_key->kpalg.cv_algor);

        /* If non-default hash, signal this by the 'more' flag */
        if(sec_key->hashalg != MDA_MD5) sec_key->kpalg.cv_algor |= CEA_MORE_FLAG;

        /* Now we know what we're about we can go passphrase => key */
        {
            byte s2k[2];
            s2k[0] = 0;
            s2k[1] = sec_key->hashalg;
            hashpassEx(passphrase, sec_key->cea_key,
            key, s2k, convertToUTF8);
        }
        /* if the mode & algorithm demand, as they usually should,
         *                  set up the encryption IV */
        while(count-- > 0) sec_key->iv[count] = randombyte();
    }
    encrypt_seckey(sec_key);
    pub_key = publicKey(sec_key);
    secretRingDirty(pub_key->keyRing);
    return TRUE;
}


void extract_keyID(byte keyID[KEYFRAGSIZE], pubkey * pub_key)
{
    memcpy(keyID, pub_key->keyId, KEYFRAGSIZE);
}



void formatKeyID(char text[IDPRINTSIZE], byte keyID[KEYFRAGSIZE])
{
    byte * keyptr = keyID + KEYFRAGSIZE - KEYPRINTFRAGSIZE;
    char * outptr = text;

    *outptr++ = '0';
    *outptr++ = 'x';
    while(keyptr < keyID + KEYFRAGSIZE)
    {
        byte2hex(outptr, *keyptr++);
        outptr += 2;
    }
    *outptr = '\0';
}

uint16_t keyLength(pubkey * pub_key)
{
    return pub_key->size;
}

uint32_t keyDate(pubkey * pub_key)
{
    uint32_t time;
    memcpy(&time, pub_key->timestamp, sizeof(time));
    return time;
}

boolean keySecret(pubkey * pub_key)
{
    return (boolean) (secretKey(pub_key) != NULL);
}
/* end of file keyutils.c */
