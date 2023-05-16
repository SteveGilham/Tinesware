/* main.c - test main program
**
**  This is a file that has been used for test purposes in the development
**  of the CTC library.  It contains what may prove to be useful example
**  code.  It is not in current use by the developers.
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
**  Heimdall <heimdall@bifroest.demon.co.uk>  1996
**  All rights reserved.  For full licence details see file licences.c
*/
#include <assert.h>
#include <ctype.h>
#include <errno.h>
#include <stdio.h>
#include <string.h>
#include <time.h>
#include "keyhash.h"
#include "keyio.h"
#include "armour.h"
#include "ctc.h"
#include "callback.h"
#include "port_io.h"
#include "utils.h"
#include "usrbreak.h"
#include "hash.h"
#include "cleave.h"
#include "keyutils.h"

/* Guess a suitable stack size for 16-bit DOS */
#ifdef __BORLANDC__
#ifdef __MSDOS__
#include <dos.h>
unsigned _stklen = 32768u;
#endif
#endif

#ifdef __BORLANDC__
#pragma warn -use
#endif

static hashtable * process_ring(char * ringfile, DataFileP file);
/* normally compiled to call only one of these */
static void armour_test(void);
static void crack(void);
static void encrypt_test(void);
static void keyring_read_test(void);
static void decrypt_test(void);
static decode_context context;

#define WRONG assert(FALSE)
#define BUFFERSIZE 1024
#define EXAMPLE "a_test"

static void testECKeyGen(void);
int main()
{
    /*testECKeyGen();*/
    /* crack();*/
    encrypt_test();
    printf("encrypted -now to decrypt!\n"); 
    fflush(stdout);
    decrypt_test();
    /* keyring_read_test();*/
    /* armour_test();
     crack();*/
    return 1;
}


static int readKeyRings(hashtable * * pubkeys, char * secretName)
{
    DataFileP pubFile;
    DataFileP pubOut;
    DataFileP secFile;
    DataFileP secOut;

    pubFile = vf_open("pubring.pgp", READ, PUBLICRING);
    if(pubFile)
    {
        *pubkeys = init_userhash(pubFile);
        /* note that the public key file is left open to allow access
          ** to the key records  */
        secFile = vf_open(secretName, READ, SECRETRING);
        if(secFile && read_secring(secFile, *pubkeys))
        {
            if((pubOut = vf_open("pubring0.pgp", WRITE, PUBLICRING)) != 0)
            {
                if((secOut = vf_open("secring0.pgp", WRITE, SECRETRING)) != 0)
                {
                    vf_close(secOut);
                }
                vf_close(pubOut);
            }
            vf_close(secFile);
            return TRUE;
        }
        vf_close(pubFile);
    }
    return FALSE;
}

static int try_password(char * password)
{
    seckey * key = firstSecKey(context.keyRings);
    int uncracked = 0;

    while(key)
    {
        if(keyStatus(key) == EXTERNAL)
        {
            if(internalise_seckey(key, password))
            {
                char name[256];

                name_from_key(publicKey(key), name);
                printf("Key %s\nhas pass-phrase \"%s\"\n", name, password);
            }
            else if(keyStatus(key) != EXTERNAL)
            {
                char name[256];

                name_from_key(publicKey(key), name);
                printf("Key %s\nhas been corrupted\n", name);
            }
            else
                uncracked++;
        }
        key = nextSecKey(key);
    }
    if(user_break()) return 0;
    return uncracked;
}

static boolean next_case(char * password)
{
    char * ptr = password;

    while(*ptr)
    {
        if(islower(*ptr))
        {
            *ptr = (char) toupper(*ptr);
            return TRUE;
        }
        else if(isupper(*ptr))
            *ptr = (char) tolower(*ptr);
        ptr++;
    }
    return FALSE;
}

static int try_all_case(char * password)
{
    char * ptr = password;
    int result;


    printf("trying all variants of\"%s\"\n", password);
    /* coerce to up case */
    while(*ptr) { 
        *ptr = (char) tolower(*ptr); 
        ptr++; 
    }
    do
        {
        result = try_password(password);
    }
    while(result > 0 && next_case(password));
    return result;
}


static void crack()
{
    DataFileP crackFile;

    if(!readKeyRings(&context.keyRings, "secring.pgp")) return;
    if((crackFile = vf_open("crack.txt", READ, TEXTCYPHER)) != 0)
    {
        char buffer[256];

        while(vf_readline(buffer, 256, crackFile) > 0) try_password(buffer);
        vf_setpos(crackFile, 0);
        while(vf_readline(buffer, 256, crackFile) > 0) try_all_case(buffer);
        vf_close(crackFile);
    }
}


static void decrypt_test()
{
    DataFileP armour;
    /* char factory[32] = "factory.asc";*/
    memset(&context, 0, sizeof(context));
    printf("ring was %08x\n", context.keyRings);
    context.keyRings = (hashtable*)0;
    printf("ring clear %08x\n", context.keyRings);
    if(!readKeyRings(&context.keyRings, "secring.pgp")) return;
    printf("ring set %08x\n", context.keyRings);
    /*
     if((armour = cleaveTogether(factory))!=0) examine_text(armour, &context);
       else printf("Cleaving together failed\n");
       vf_close(armour);
       unlink(factory);
    */
    /*context.splitkey = 1;*/

    if((armour = vf_open(EXAMPLE ".asc", READ, TEXTCYPHER)) != 0)
    {
        examine_text(armour, &context);
    }

    /*context.splitkey = 0;
     if((armour = vf_open("convkey.asc",  READ, TEXTCYPHER)) != 0)
     {
      examine_text(armour, &context);
     }
     if((armour = vf_open(EXAMPLE ".asc",  READ, TEXTCYPHER)) != 0)
     {
      examine_text(armour, &context);
     }*/
}

static void testECKeyGen(void)
{
    DataFileP newring;
    seckey *s;

    keyType t = {
        PKA_GF2255, 240, 0, 0    };
    strcpy(t.name, "Pegwit test key");
    strcpy(t.passphrase, "test");
    t.selfSignAlg = MDA_MD5;
    t.kpAlg = CEA_BLOW16;


    if(!readKeyRings(&context.keyRings, "secring.pgp")) return;

    s = makePKEkey(context.keyRings, &t);

    insertSeckey(context.keyRings, s);

    /* save the files */
    newring = vf_open("nsecring.pgp", WRITE, SECRETRING);
    if(!newring) return;
    writeSecRing(newring, context.keyRings);
    vf_close(newring);

    newring = vf_open("npubring.pgp", WRITE, PUBLICRING);
    if(!newring) return;
    writePubRing(newring, context.keyRings);
    vf_close(newring);

    if((newring = vf_open("aaa.cpp", WRITE, TEXTPLAIN)) != 0)
    {
        process_ring("npubring.pgp", newring);
        process_ring("nsecring.pgp", newring);
    }
}

static void downCase(char * text)
{
    while(*text)
    {
        if(isupper(*text)) *text = (char) tolower(*text);
        text++;
    }
}

static int key_from_name(hashtable * table, char * name,
pubkey * matchs[], int maxMatchs)
{
    username * nameRec;
    char keyName[256];
    int result = 0;
    pubkey * candidate = firstPubKey(table);

    while(candidate)
    {
        nameRec = firstName(candidate);
        if(nameRec)
        {
            text_from_name(table, nameRec, keyName);
            downCase(keyName);

            if(strstr(keyName, name))
            {
                matchs[result++] = candidate;
                if(result >= maxMatchs) return result;
            }
        }
        candidate = nextPubKey(candidate); /* next in hash alias list */
    }
    return result;
}

static void encrypt_test(void)
{
    DataFileP source, output;
    encryptInsts insts = { 
        "Name", 3, 't',
        MDA_MD5, /*PKA_RSA, PKA_RSA,*/
        CPA_DEFLATE, ARM_PGP, 0,
        /*
                             {{CEA_BLOW5, CEM_CFB|CEM_REVERSE_FLAG},
                              {0, 0},
                              {0,0}, {0,0}, {0,0}},
        */
        {
            {
                CEA_SQUARE, CEM_CBC|CEM_REVERSE_FLAG            }
            ,
            {
                0,0            }
            ,{
                0,0            }
            ,
            /*{CEA_BLOW5|CEA_MORE_FLAG, CEM_CFB|CEM_REVERSE_FLAG},
                                  {CEA_BLOW5, CEM_CFB},*/ {
                0,0            }
            , {
                0,0            }
        }
        ,


        0, /* to*/
        0,0,0    }; /* signatory, comments, maxlines */
    pubkey * recipients[11];
    int Nto;
    if(!readKeyRings(&context.keyRings, "secring.pgp")) return;
    if((Nto = key_from_name(context.keyRings, "standalone", recipients, 10)) != 0)
    {
        recipients[Nto] = NULL;
        while(Nto-- > 0) completeKey(recipients[Nto]);
        insts.to = recipients;
        /* callback here */

        if(internalise_seckey(firstSecKey(context.keyRings)->next_in_file, "test"))
        {
            /* Match the signature instructions to the signing key */
            insts.signatory = firstSecKey(context.keyRings)->next_in_file;
            printf("sign by key 0x");
            {
                int i;
                for(i=0; i<KEYFRAGSIZE; ++i) printf("%02x",
                insts.signatory->publicKey->keyId[i]);
                printf("\n");
                fflush(stdout);
            }
            if((source = vf_open(EXAMPLE, READ, TEXTPLAIN)) != 0)
            {
                if((output = vf_open(EXAMPLE ".asc", WRITE, TEXTCYPHER)) != 0)
                {
                    encrypt /*signOnly*/ (source, output, &insts);
                    if(ARM_PGP_MULTI == insts.armour)
                    {
                        char factory[32] = "factory.asc";
                        vf_close(output);
                        output = vf_open(EXAMPLE ".asc", READ, TEXTCYPHER);
                        if(cleaveApart(output, factory))
                            printf("Cleft OK\n");
                        else
                            printf("cleavage failed\n");
                    }
                    else printf("single part file\n");
                    vf_close(output);
                }
                vf_close(source);
            }
        }
    }
}


static void armour_test()
{
    DataFileP armour;
    DataFileP output;
    DataFileP out2;
    armour_params params = {
        "MESSAGE", "CTCv0.1", 1, 1, 0    };
    armour_info info;

    if((armour = vf_open(EXAMPLE ".asc", READ, TEXTCYPHER)) != 0)
    {
        if((output = vf_open(EXAMPLE ".pgp", WRITEREAD, BINCYPHER)) != 0)
        {
            if(ARM_PGP != next_armour(armour, &info)) 
                WRONG;
            if(unarmour_block(armour, output, &info))
                WRONG;
            vf_close(armour);
            if((out2 = vf_open(EXAMPLE "2.asc", WRITEREAD, TEXTCYPHER)) != 0)
            {
                vf_setpos(output, 0);
                if(armour_block(output, out2, &params))
                    WRONG;
                vf_close(output);
                if((output = vf_open(EXAMPLE "2.pgp", WRITE, BINCYPHER)) != 0)
                {
                    vf_setpos(out2, 0);
                    if(ARM_PGP != next_armour(out2, &info))
                        WRONG;
                    if(unarmour_block(out2, output, &info))
                        WRONG;
                    vf_close(output);
                }
                vf_close(out2);
            }
            else
                vf_close(output);
        }
        else
            vf_close(armour);
    }
}


static char * appendkey(char * buffer, byte key[8]);
static void keyring_read_test()
{
    DataFileP log;
    seckey * sec_key;
    DataFileP sec_ring;
    hashtable * pubring;

    if((log = vf_open("test.out", WRITE, TEXTPLAIN)) != 0)
    {

        /*  process_ring("test.ring", log);  */
        pubring = process_ring("npubring.pgp", log);
        /* This does not set the keys to be complete; so read_secring()
        ** below fails leaving sec_key zero; this is despite the other
        ** tests (encrypt and decrypt) working OK.  Is baffling! */

        /*  process_ring("secring.pgp", log);*/

        sec_ring = vf_open("nsecring.pgp", READ, SECRETRING);
        if(sec_ring && read_secring(sec_ring, pubring))
        {
            sec_key = firstSecKey(pubring);
            if(internalise_seckey(sec_key, "password"))
            {
                vf_writeline("first key \"password\"", log);
                externalise_seckey(sec_key);
            }
            if(internalise_seckey(sec_key, "password"))
                vf_writeline("first key \"password\"", log);
            externalise_seckey(sec_key);
            if(internalise_seckey(sec_key, "xxxxx"))
                vf_writeline("first key \"xxxxx\"", log);
            if(internalise_seckey(sec_key, "password"))
                vf_writeline("first key \"password\"", log);
            sec_key = nextSecKey(sec_key);
            if(internalise_seckey(sec_key, ""))
                vf_writeline("second key <blank>", log);
        }
        vf_close(log);

    }
}

#define LENGTHVALUES "L1\0L2\0L4\0L?"

static hashtable * process_ring(char * ringfile, DataFileP log)
{
    DataFileP file;
    char nametext[256];
    char outBuffer[256];
    /*ushort status = 0;*/
    hashtable * table;
    pubkey * pub_key;
    byte keyId[KEYFRAGSIZE];
    /*char * key_type[16]
      = { "?00?", "PKE",  "SIG", "?03?",
       "?04?", "SKC", "PKC", "?07?",
       "CDP", "CKED", "?10?", "RAW",
       "TRST", "USID", "COMM", "?15?" };*/
    /*char * pkt_len[4] = { "L1", "L2", "L4", "L?" };*/

    file = vf_open(ringfile, READ, PUBLICRING);
    if(file)
    {
        table = init_userhash(file);
        strcpy(outBuffer, "File: ");
        strcat(outBuffer, ringfile);
        vf_writeline("", log);
        vf_writeline(outBuffer, log);
        vf_writeline("", log);

        pub_key = firstPubKey(table);
        while(pub_key)
        {
            username * name;
            char * prefix = "";

            strcpy(outBuffer, "key w/ID ");
            extract_keyID(keyId, pub_key);
            appendkey(outBuffer, keyId);
            vf_writeline(outBuffer, log);
            name = firstName(pub_key);
            while(name)
            {
                signature * sig;

                text_from_name(table, name, nametext);
                strcpy(outBuffer, prefix);
                strcat(outBuffer, nametext);
                vf_writeline(outBuffer, log);
                sig = firstSig(name);
                while(sig)
                {
                    pubkey * signer;

                    signer = signatory(sig);
                    strcpy(outBuffer, "\t\tsigned ");
                    extract_keyID(keyId, signer);
                    name_from_key(signer, appendkey(outBuffer, keyId));
                    vf_writeline(outBuffer, log);
                    sig = nextSig(sig);
                }
                prefix = "\ta.k.a.: ";
                name = nextName(name);
            }

            pub_key = nextPubKey(pub_key);
            vf_writeline(" ", log);
        }
        vf_close(file);
    }
    return table;
}

static char * appendkey(char * buffer, byte key[8])
{
    int offset = -1;
    char * ptr = buffer + strlen(buffer);
    char * digits = "0123456789ABCDEF";

    *ptr++ = '0';
    *ptr++ = 'x';
    while(++offset < 8)
    {
        *ptr++ = digits[key[offset] /16];
        *ptr++ = digits[key[offset] % 16];
    }
    *ptr++=' ';
    *ptr = '\0';
    return ptr;
}
#ifdef __BORLANDC__
#pragma warn .use
#endif

/* end of file main.c */




