/*
 hashpass.c -

** hashpass - Hash pass phrase down by using the given algorithm
** Adjust the result to fit the appropriate key;

 Extracted Mr. Tines <tines@windsong.demon.co.uk> Sep '97, all rights reserved.
        For full licence details see file licences.c

*/
#include <string.h>
#include "hash.h"
#include "hashpass.h"
#include "widechar.h"


void hashpass (char *keystring, byte *key, int keylen, byte hashalg)
{
    byte hash[MAXHASHSIZE];
    int chunk, hashlen;
    md_context hashctx;

    hashlen = hashDigest(hashalg);

    hashctx = hashInit(hashalg);
    if(hashctx) /* check initialisation worked; not clear what to do if it doesn't */
    {
        hashUpdate(hashctx, (byte *)keystring,
        (uint32_t) strlen(keystring));
        hashFinal(&hashctx, hash);
    }
    /*
          shorter keys just take first N bytes; longer keys repeat -
          better than fixed padding.  The shortest keys likely are the
          KD-DES (8 bytes, 7 bits each used, + 6 bytes) = 13 bytes and
          3-WAY 12 bytes; so we are unlikely to lose any of our precious
          entropy with a fallback 12-byte hash based on 3-Way.
         */

    while(0 < (chunk = min(keylen,hashlen)))
    {
        memmove(key, hash, chunk);
        key+=hashlen;
        keylen-=hashlen;
    }
    memset(hash, 0, MAXHASHSIZE);
}

void CTCALG_DLL hashpassEx (char *keystringIn, byte *key,
int keylen, byte * s2k, boolean convertToUTF8)
{
    byte utf8[768]; /* worst case, 3 bytes per character */
    byte * keystring = (byte*) keystringIn;
    uint32_t bytelen;
    byte hash[MAXHASHSIZE];
    int chunk, hashlen;
    md_context hashctx;
    uint32_t block = 0;
    uint32_t count = 0;


    hashlen = hashDigest(s2k[1]);

    if(convertToUTF8)
    {
        bytelen = mbstoUTF8(keystringIn, utf8);
        keystring = utf8;
    }
    else
        {
        bytelen = (uint32_t) strlen(keystringIn);
    }

    if(0x03 == s2k[0])
    {
        count = 16;
        count += (s2k[10]&15);
        count <<= ((s2k[10]>>4) + 6);
        if(count < bytelen+8) count = bytelen+8;
    }

    while(0 < (chunk = min(keylen,hashlen)))
    {
        uint32_t preload;

        hashctx = hashInit(s2k[1]);
        if(!hashctx) /* check initialisation worked; not clear what to do if it doesn't */
        {
            memset(key, 0, keylen);
            return;
        }

        /* preload with N zeroes */
        for(preload = 0; preload < block; ++preload)
        {
            byte zero = 0;
            hashUpdate(hashctx, &zero, (uint32_t) 1);
        }

        if(0x03 == s2k[0])
        {
            /* assume preloading is not included in the count; */
            /* this seems the most plausible reading of the spec */
            uint32_t hashed = 0;
            while(hashed < count)
            {
                uint32_t load;
                load = count-hashed;
                if(load > 8) load = 8;
                /* salt plus the passphrase repeatedly */
                hashUpdate(hashctx, s2k+2, load);
                hashed += load;
                if(hashed >= count)
                {
                    break;
                }
                load = count-hashed;
                if(load > bytelen) load = bytelen;
                hashUpdate(hashctx, keystring, load);
                hashed += load;
            }
        }
        else if(0x01 == s2k[0])
        {
            /* salt plus the passphrase */
            hashUpdate(hashctx, s2k+2, (uint32_t)8);
            hashUpdate(hashctx, keystring, bytelen);
        }
        else
        {
            /* just the passphrase */
            hashUpdate(hashctx, keystring, bytelen);
        }


        hashFinal(&hashctx, hash);



        /*
                      shorter keys just take first N bytes; longer keys repeat -
                      better than fixed padding.  The shortest keys likely are the
                      KD-DES (8 bytes, 7 bits each used, + 6 bytes) = 13 bytes and
                      3-WAY 12 bytes; so we are unlikely to lose any of our precious
                      entropy with a fallback 12-byte hash based on 3-Way.
                     */

        memmove(key, hash, chunk);
        key+=hashlen;
        keylen-=hashlen;
        ++block;
    }
    memset(hash, 0, MAXHASHSIZE);
}


/* end of hashpass.c */

