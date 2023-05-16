/*  keywords.cpp
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
**  Heimdall <heimdall@bifroest.demon.co.uk>  1999
**  All rights reserved.  For full licence details see file licences.c
**
**  This file contains source for obtaining printable values for the
**  main CTC enumeration types.  This is usable as-is for English-only 
**  implementations and test purposes.  It should be consider example code
**  for multi-language or non-English single-language implementations.
**
**  Ported into CTCFox strings DLL for convenience 
**                 Mr. Tines <tines@windsong.demon.co.uk> 12-Apr-2002
*/

#include "keyconst.h"
#include "pkautils.h"
#include "callback.h"

#include "strings.h"

//===================================================================

#define NOVALUE -9999

typedef struct
{
    int keyValue;
    char * name;
    char * desc;
}
keyWord;

typedef struct
{
    int enumId;
    const keyWord** values;
}
enumDesc;

//===================================================================

static const keyWord enumerationsArray[] =
{
    { 
        CTClib::CB_ENUMERATION, N_("ENUM"), N_("Enumeration type names")     }
    ,
    {
        CTClib::CB_MODULE, N_("MODULE"), N_("CTC module identifiers")     }
    ,
    {
        CTClib::CB_CONTEXT, N_("CONTEXT"), N_("CTC operation context")     }
    ,
    {
        CTClib::CB_SEVERITY, N_("SEVERITY"), N_("CTC condition severity")     }
    ,
    {
        CTClib::CB_PURPOSE, N_("PURPOSE"), N_("Purpose for which a file is required")     }
    ,
    {
        CTClib::CTBVERSION, N_("VERSION"), N_("Version (of PGP) which introduced the packet format")     }
    ,

    {
        CTClib::SKSTAT, N_("SKSTAT"), N_("Status of a secret key")     }
    ,
    {
        CTClib::CTBTYPE, N_("CTCTYPE"), N_("Type of a CTB packet")     }
    ,
    {
        CTClib::SIGCLASS, N_("SIGCLASS"), N_("Class (significance of) a Signature packet")     }
    ,
    {
        CTClib::SIGSUBPKTS, N_("SUBPKT"), N_("Type of a subpacket (in a Signature packet")     }
    ,
    {
        CTClib::PKEALGOR, N_("PKEA"), N_("Public-Key Encryption Algorithm")     }
    ,
    {
        CTClib::MDALGOR, N_("MDA"), N_("Message Digest Algorithm")     }
    ,
    {
        CTClib::CEALGOR, N_("CEA"), N_("Conventional (Symmetric) Encryption Algorithm")     }
    ,
    {
        CTClib::COMPANDALGOR, N_("CMPA"), N_("Compress/expansion algorithm")     }
    ,
    {
        NOVALUE, N_("????"), N_("Unknown enumeration")     }
};

static const keyWord modulesArray[] =
{
    {
        CTClib::CB_CTC_ENGINE, N_("CTC"), N_("CTC Engine")     }
    ,
    {
        CTClib::CB_ARMOUR, N_("ARMOUR"), N_("ASCII-armour")     }
    ,
    {
        CTClib::CB_COMPAND, N_("COMPND"), N_("Compress/Expand")     }
    ,
    {
        CTClib::CB_MSG_DIGEST, N_("DIGEST"), N_("Message Digest")     }
    ,
    {
        CTClib::CB_BULK_CYPHER, N_("CIPHER"), N_("Bulk Cypher")     }
    ,
    {
        CTClib::CB_PKE, N_("PKE"), N_("Public Key")     }
    ,
    {
        CTClib::CB_PK_MANAGE, N_("KEYMAN"), N_("Key Management")     }
    ,
    {
        CTClib::CB_CTB_IO, N_("CTB_IO"), N_("CTB format I/O")     }
    ,
    {
        CTClib::CB_RANDOM, N_("RANDOM"), N_("Random Numbers")     }
    ,
    {
        CTClib::CB_FILING, N_("FILSYS"), N_("File System")     }
    ,
    {
        NOVALUE, N_("????"), N_("Unknown module")     }
};

static const keyWord contextsArray[] =
{
    {
        CTClib::CB_DECRYPTION, N_("DECRYPT"), N_("Decryption")     }
    ,
    {
        CTClib::CB_ENCRYPTION, N_("ENCRYPT"), N_("Encryption")     }
    ,
    {
        CTClib::CB_SIGNING, N_("SIGNING"), N_("Signing")     }
    ,
    {
        CTClib::CB_VERIFYING, N_("VERIFY"), N_("Verifying")     }
    ,
    {
        CTClib::CB_KEYGEN, N_("KEY_GEN"), N_("Key Generation")     }
    ,
    {
        CTClib::CB_KEYMAN, N_("KEY_MAN"), N_("Key Management")     }
    ,
    {
        CTClib::CB_READING_RING, N_("READRNG"), N_("Reading key-ring")     }
    ,
    {
        CTClib::CB_UNKNOWN, N_("UNKNOWN"), N_("Not Specified")     }
    ,
    {
        NOVALUE, N_("????"), N_("Unknown Context")     }
};

static const keyWord severitiesArray[] =
{
    {
        CTClib::CB_CRASH, N_("CRASH"), N_("Program Crash")     }
    ,
    {
        CTClib::CB_FATAL, N_("FATAL"), N_("Fatal Error")     }
    ,
    {
        CTClib::CB_ERROR, N_("ERROR"), N_("Error")     }
    ,
    {
        CTClib::CB_WARNING, N_("WARN"), N_("Warning")     }
    ,
    {
        CTClib::CB_INFO, N_("INFO"), N_("Information")     }
    ,
    {
        CTClib::CB_STATUS, N_("STAT"), N_("Status")     }
    ,
    {
        NOVALUE, N_("????"), N_("Unknown severity")     }
};

static const keyWord purposesArray[] =
{
    {
        CTClib::SIGNEDFILE, N_("SIGNED"), N_("file corresponding to a signature")     }
    ,
    {
        CTClib::SPLITKEY, N_("SPLIT"), N_("file to hold a conventional key")     }
    ,
    {
        CTClib::CYPHERTEXTFILE, N_("CYPHER"), N_("cyphertext file corresponding to a conventional key")     }
    ,
    {
        NOVALUE, N_("????"), N_("Unknown purpose")     }
};

static const keyWord versionsArray[] =
{
    {
        MIN_VERSION, N_("2.3"), N_("PGP2.3 or earlier")     }
    ,
    {
        VERSION_2_6, N_("2.6"), N_("PGP2.6.x")     }
    ,
    {
        VERSION_3, N_("5.x"), N_("PGP3.0 - 5.x")     }
    ,
    {
        NOVALUE, N_("???"), N_("Unknown version code")     }
};

static const keyWord keyStatusesArray[] =
{
    {
        INTERNAL, N_("INT"), N_("Ready for use")     }
    ,
    {
        EXTERNAL, N_("EXT"), N_("Encrypted")     }
    ,
    {
        CORRUPT, N_("CRPT"), N_("Corrupted")     }
    ,
    {
        NOVALUE, N_("????"), N_("Unknown key status")     }
};

static const keyWord SIGClassesArray[] =
{
    {
        SIG_BINARY, N_("BINFILE"), N_("Binary file signature")     }
    ,
    {
        SIG_TEXT, N_("TXTFILE"), N_("Text file signature")     }
    ,
    {
        SIG_KEY_CERT, N_("GENERIC"), N_("Generic key signature")     }
    ,
    {
        SIG_KEY_PERSONA, N_("PERSONA"), N_("Key persona signature")     }
    ,
    {
        SIG_KEY_CASUAL, N_("CASUAL"), N_("Key casual signature")     }
    ,
    {
        SIG_KEY_POSITIVE, N_("POSITIVE"), N_("Key positive signature")     }
    ,
    {
        SIG_KEY_COMPROM, N_("COMPROM"), N_("Key compromised")     }
    ,
    {
        SIG_KEY_REVOKE, N_("REVOKE"), N_("Key Id. revoked")     }
    ,
    {
        SIG_KEY_TIMESTMP, N_("TIMESTMP"), N_("Key timestamp")     }
    ,
    {
        SIG_SUBKEY_CERT, N_("SKEYCERT"), N_("Sub-Key certificate")     }
    ,
    {
        SIG_SUBKEY_REVOKE, N_("SKEYREVK"), N_("Sub-Key revoked")     }
    ,
    {
        NOVALUE, N_("????"), N_("Unknown signature type")     }
};

static const keyWord packetTypesArray[] =
{
    {
        CTB_PKE, N_("PKE"), N_("public key encrypted")     }
    ,
    {
        CTB_SKE, N_("SKE"), N_("secret key signed")     }
    ,
    {
        CTB_CONV_ESK, N_("CESK"), N_("Conventional encryption algorithm details")     }
    ,
    {
        CTB_1PASS_SIG, N_("1PSIG"), N_("\"one-pass signature\"")     }
    ,
    {
        CTB_CERT_SECKEY, N_("SECKEY"), N_("secret key certificate")     }
    ,
    {
        CTB_CERT_PUBKEY, N_("PUBKEY"), N_("public key certificate")     }
    ,
    {
        CTB_SEC_SUBKEY, N_("SECSKEY"), N_("secret sub-key")     }
    ,
    {
        CTB_COMPRESSED, N_("COMPRES"), N_("compressed data")     }
    ,
    {
        CTB_CKE, N_("CKE"), N_("conventionally encrypted data")     }
    ,
    {
        CTB_LITERAL, N_("LITERL"), N_("raw data with filename and mode")     }
    ,
    {
        CTB_LITERAL2, N_("LITERL2"), N_("Fixed literal packet")     }
    ,
    {
        CTB_KEYCTRL, N_("TRUST"), N_("Trust byte")     }
    ,
    {
        CTB_USERID, N_("USERID"), N_("key username(id)")     }
    ,
    {
        CTB_PUB_SUBKEY, N_("PUBSKEY"), N_("public sub-key")     }
    ,
    {
        CTB_COMMENT, N_("COMMENT"), N_("comment")     }
    ,
    {
        NOVALUE, N_("????"), N_("Unknown (CTB) packet type")     }
};

/* Where appropriate the long text for the following enumeration
** type is the precise description from the current OpenPGP draft */
static const keyWord subPacketTypesArray[] =
{
    {
        SUBPKT_VERSION, N_("VERSION"), N_("Version Number")     }
    ,
    {
        SUBPKT_CREATION, N_("CRT_DATE"), N_("signature creation time")     }
    ,
    {
        SUBPKT_EXPIRY, N_("EXPIRY"), N_("signature expiration time")     }
    ,
    {
        SUBPKT_EXPORTABLE_CERT, N_("EXPTCERT"), N_("exportable certification")     }
    ,
    {
        SUPPKT_TRUST_SIGNATURE, N_("TRUSTSIG"), N_("trust signature")     }
    ,
    {
        SUBPKT_REGEXP, N_("REGEXPR"), N_("regular expression")     }
    ,
    {
        SUBPKT_REVOCABLE, N_("REVOCBLE"), N_("revocable")     }
    ,
    {
        SUBPKT_KEY_CAPABILITIES, N_("CAPS"), N_("Key capabilities")     }
    ,
    {
        SUBPKT_KEY_EXPIRY, N_("KEYEXPRY"),  N_("key expiration time")     }
    ,
    /* The OpenPGP description for the next line is
         ** "placeholder for backward compatibility") */
    {
        SUBPKT_KEY_RECOVERY_KEY, N_("KRK"), N_("Key Recovery Key")     }
    ,
    {
        SUBPKT_KEY_PREFERRED_ALGS, N_("PRFALG"), N_("preferred symmetric algorithms")     }
    ,
    {
        SUBPKT_REVOCATION_KEY, N_("RECOVKEY"), N_("revocation key")     }
    ,
    {
        SUBPKT_KEYID, N_("KEYID"), N_("issuer key ID")     }
    ,
    {
        SUBPKT_USERID, N_("USERID"), N_("User Id.")     }
    ,
    {
        SUBPKT_URL, N_("URL"), N_("URL")     }
    ,
    {
        SUBPKT_FINGER, N_("FINGER"), N_("Key Finger-print")     }
    ,
    {
        SUBPKT_NOTATION_DATA, N_("NOTEDATA"), N_("notation data")     }
    ,
    {
        SUBPKT_PREFERRED_MDA, N_("PREF_MDA"), N_("preferred hash algorithms")     }
    ,
    {
        SUBPKT_PREFERRED_CPA, N_("PREF_CPA"), N_("preferred compression algorithms")     }
    ,
    {
        SUBPKT_KEYSERVER_PREFS, N_("SERVPREF"), N_("key server preferences")     }
    ,
    {
        SUBPKT_PREFERRED_KEYSERVER, N_("PREFSERV"), N_("preferred key server")     }
    ,
    {
        SUBPKT_PRIMARY_USERID, N_("PRIMID"), N_("primary user id")     }
    ,
    {
        SUBPKT_POLICY_URL, N_("POL_URL"), N_("policy URL")     }
    ,
    {
        SUBPKT_KEY_FLAGS, N_("KEYFLAGS"), N_("key flags")     }
    ,
    {
        SUBPKT_SIGNER_USERID, N_("SIGNERID"), N_("signer's user id")     }
    ,
    {
        SUBPKT_REVOCATION_REASON, N_("RVREASON"), N_("reason for revocation")     }
    ,
    {
        SUBPKT_MIN_USERDEFINED, N_("USERDEF0"), N_("internal/user-defined:0")     }
    ,
    {
        SUBPKT_MIN_USERDEFINED + 1, N_("USERDEF1"), N_("internal/user-defined:1")     }
    ,
    {
        SUBPKT_MIN_USERDEFINED + 2, N_("USERDEF2"), N_("internal/user-defined:2")     }
    ,
    {
        SUBPKT_MIN_USERDEFINED + 3, N_("USERDEF3"), N_("internal/user-defined:3")     }
    ,
    {
        SUBPKT_MIN_USERDEFINED + 4, N_("USERDEF4"), N_("internal/user-defined:4")     }
    ,
    {
        SUBPKT_MIN_USERDEFINED + 5, N_("USERDEF5"), N_("internal/user-defined:5")     }
    ,
    {
        SUBPKT_MIN_USERDEFINED + 6, N_("USERDEF6"), N_("internal/user-defined:6")     }
    ,
    {
        SUBPKT_MIN_USERDEFINED + 7, N_("USERDEF7"), N_("internal/user-defined:7")     }
    ,
    {
        SUBPKT_MIN_USERDEFINED + 8, N_("USERDEF8"), N_("internal/user-defined:8")     }
    ,
    {
        SUBPKT_MIN_USERDEFINED + 9, N_("USERDEF9"), N_("internal/user-defined:9")     }
    ,
    {
        SUBPKT_MIN_USERDEFINED + 10, N_("USERDEF10"), N_("internal/user-defined:10")     }
    ,
    {
        NOVALUE, N_("????"), N_("Unknown sub-packet type")     }
};

static const keyWord PKEalgorsArray[] =
{
    {
        PKA_RSA, N_("RSA"), N_("RSA (Rivest, Shamir & Adleman)")     }
    ,
    {
        PKA_RSA_ENCRYPT_ONLY, N_("RSA_ENCR"), N_("RSA (encryption only key)")     }
    ,
    {
        PKA_RSA_SIGN_ONLY, N_("RSA_SIGN"), N_("RSA (signature only key)")     }
    ,
    {
        PKA_ELGAMAL, N_("ELGAMAL"), N_("ElGamal/Diffie-Hellman")     }
    ,
    {
        PKA_DSA, N_("DSA"), N_("DSA (Digital Signature Algorithm)")     }
    ,
    {
        PKA_EBP_RSA, N_("EBP_RSA"), N_("EBP's value for RSA")     }
    ,
    {
        PKA_EBP_RABIN, N_("EBP_RABIN"), N_("EBP's value for Rabin")     }
    ,
    {
        PKA_GF2255, N_("GF2_255"), N_("elliptic curve on GF(2^255)")     }
    ,
    {
        PKA_PEGWIT9, N_("PEGWIT9"), N_("elliptic curve as per Pegwit v9")     }
    ,
    {
        NOVALUE, N_("????"), N_("unknown PKE algorithm")     }
};


static const keyWord MDalgorsArray[] =
{
    {
        MDA_MD5, N_("MD5"), N_("MD5 (Message Digest 5)")     }
    ,
    {
        MDA_PGP5_SHA1, N_("SHA1P"), N_("NIST SHA (Secure Hash Algorithm) variant 1 (PGP code)")     }
    ,
    {
        MDA_PGP5_RIPEM160, N_("RIPEM"), N_("RIPEM 160-bit")     }
    ,
    /* { MDA_EBP_HAVAL_MIN, N_("HAVAL1"),"EBP uses this range for 15 varieties of HAVAL") },
         { MDA_EBP_HAVAL_MAX, N_("HAVALN"),") all of them subtly wrong :(") }, */
    {
        MDA_3WAY, N_("3WAY"), N_("3-way used to produce 96 bit hash")     }
    ,
    {
        MDA_SHA, N_("SHA"), N_("NIST SHA original")     }
    ,
    {
        MDA_SHA1, N_("SHA1"), N_("NIST SHA (Secure Hash Algorithm) variant 1 (CTC code)")     }
    ,
    /* #define MDA_HAVAL_MIN (5|MDA_FLEX_FLAG)
        #define MDA_HAVAL_MAX (MDA_HAVAL_RANGE +MDA_HAVAL_MIN) */
    {
        NOVALUE, N_("????"), N_("Unknown message digest")     }
};

static const keyWord CEalgorsArray[] =
{
    {
        CEA_IDEA, N_("IDEAP"), N_("IDEA (International Data Encryption Algorithm) [PGP]")     }
    ,
    {
        CEA_3DES, N_("3DESP"), N_("Triple-DES [PGP]")     }
    ,
    {
        CEA_CAST5, N_("CASTP"), N_("CAST5 [PGP]")     }
    ,
    /* GPG algorithm values */
    {
        CEA_GPG_BLOW16, N_("BLOW16P"), N_("Blowfish with 16-byte key [GPG, OpenPGP]")     }
    ,
    {
        CEA_OPGP_SAFERSK128, N_("SAFERP"), N_("SAFER (Secure And Fast Encryption Routine) SK128 13-rounds [OpenPGP]")     }
    ,
    /*#define CEA_GPG_DES_SK        7*/
    {
        CEA_GPG_BLOW20, N_("BLOW20G"), N_("Blowfish with 20-byte key [GPG]")     }
    ,
    /*#define CEA_GPG_GOST          43 */

    /* #define CEA_EBP_IDEA 86                  // EBP uses this value also - NYI//
        #define CEA_EBP_SAFER_MIN 97             // EBP has 4 varieties of Safer - NYI//
        #define CEA_EBP_SAFER_MAX 100            // but it looks like 97 = SK128 is the
                                            only one used //
        #define CEA_NONE    (0|CEA_FLEX_FLAG)   // explicit no encryption - more
                                                   flag must not be set//
        */
    {
        CEA_IDEAFLEX, N_("IDEA"), N_("IDEA (International Data Encryption Algorithm) [CTC]")     }
    ,
    {
        CEA_3WAY, N_("3WAY"), N_("3-WAY cipher")     }
    ,
    {
        CEA_BLOW16, N_("BLOW16"), N_("Blowfish with 16-byte key")     }
    ,
    {
        CEA_TEA, N_("TEA"), N_("TEA (Tiny Encryption Algorithm)")     }
    ,
    {
        CEA_BLOW5, N_("BLOW5"), N_("Blowfish with 40-bit key")     }
    ,
    {
        CEA_SQUARE, N_("SQUARE"), N_("Square 128 bit key and block")     }
    ,
    {
        CEA_DES, N_("DES"), N_("DES (Data Encryption Standard)")     }
    ,
    {
        CEA_S3DES, N_("S3DES"), N_("s3DES")     }
    ,
    {
        CEA_KDDES, N_("KDES"), N_("key dependent DES")     }
    ,
    {
        CEA_3DESFLEX, N_("3DES"), N_("Triple-DES [CTC]")     }
    ,
    {
        CEA_CAST5FLEX, N_("CAST5"), N_("CAST5 [CTC]")     }
    ,
    /* { CEA_ROT_NFLEX, N_("ROTN"),  "ROT") },*/
    {
        CEA_SAFERSK128FLEX, N_("SAFER"), N_("SAFER (Secure And Fast Encryption Routine)")     }
    ,
    {
        CEA_DESSKFLEX, N_("KDESF"), N_("key dependent DES")     }
    ,
    {
        CEA_BLOW20, N_("BLOW20"), N_("Blowfish with 20-byte key")     }
    ,
    {
        CEA_GOSTFLEX, N_("GOST"), N_("GOST (Gosudarstvennyi Standard)")     }
    ,
    {
        CEA_OPGP_AES_128, N_("AES128"), N_("AES (Advanced Encryption Standard) 128-bit key [OpenPGP]")     }
    ,
    {
        CEA_AES_128, N_("AES128F"), N_("AES (Advanced Encryption Standard) 128-bit key [CTC]")     }
    ,
    {
        CEA_OPGP_AES_192, N_("AES192"), N_("AES (Advanced Encryption Standard) 192-bit key [OpenPGP]")     }
    ,
    {
        CEA_AES_192, N_("AES192F"), N_("AES (Advanced Encryption Standard) 128-bit key [CTC]")     }
    ,
    {
        CEA_OPGP_AES_256, N_("AES256"), N_("AES (Advanced Encryption Standard) 256-bit key [OpenPGP]")     }
    ,
    {
        CEA_TWOFISH_256, N_("AES256F"), N_("AES (Advanced Encryption Standard) 128-bit key [CTC]")     }
    ,
    {
        CEA_AES_256, N_("TWO256"), N_("Twofish 256-bit key [OpenPGP]")     }
    ,
    {
        CEA_OPGP_TWOFISH_256, N_("TWO256"), N_("Twofish 256-bit key [CTC]")     }
    ,
    {
        NOVALUE, N_("????"), N_("Unknown bulk cipher")     }
};

static const keyWord COMPalgorsArray[] =
{
    {
        CPA_NONE, N_("NONE"), N_("no compression")     }
    ,
    {
        CPA_DEFLATE, N_("DEFLATE"), N_("Zip-based deflate compression algorithm")     }
    ,
    {
        CPA_SPLAY, N_("SPLAY"), N_("Splay tree compression")     }
    ,
    {
        NOVALUE, N_("????"), N_("Unknown compression method")     }
};


static const keyWord SIGStatsArray[] =
{
    {
        CTClib::SS_BAD, N_("BAD"), N_("Signature does not match (confirmed)")     }
    ,
    {
        CTClib::SS_SUSPECT, N_("SUSPECT"), N_("Signature does not match")     }
    ,
    {
        CTClib::SS_UNKNOWN, N_("UNKNOWN"), N_("Signature has not been checked")     }
    ,
    {
        CTClib::SS_PROBABLE, N_("PROBABLE"), N_("Signature matches (unconfirmed)")     }
    ,
    {
        CTClib::SS_VERIFIED, N_("VERIFIED"), N_("Signature verified")     }
    ,
    {
        NOVALUE, N_("????"), N_("Unknown signature status")     }
};

static const keyWord * enumerations = &enumerationsArray[0];
static const keyWord * modules = &modulesArray[0];
static const keyWord * contexts = &contextsArray[0];
static const keyWord * severities = &severitiesArray[0];
static const keyWord * purposes = &purposesArray[0];
static const keyWord * versions = &versionsArray[0];
static const keyWord * keyStatuses = &keyStatusesArray[0];
static const keyWord * sigClasses = &SIGClassesArray[0];
static const keyWord * packetTypes = &packetTypesArray[0];
static const keyWord * subPacketTypes = &subPacketTypes[0];
static const keyWord * PKEalgors = &PKEalgorsArray[0];
static const keyWord * MDalgors = &MDalgorsArray[0];
static const keyWord * CEalgors = &CEalgorsArray[0];
static const keyWord * COMPalgors = &COMPalgorsArray[0];
static const keyWord * sigStats = &SIGStatsArray[0];

static enumDesc allEnums[] =
{
    {
        CTClib::CB_ENUMERATION, &enumerations     }
    ,
    {
        CTClib::CB_MODULE, &modules     }
    ,
    {
        CTClib::CB_CONTEXT, &contexts     }
    ,
    {
        CTClib::CB_SEVERITY, &severities     }
    ,
    {
        CTClib::CB_PURPOSE, &purposes     }
    ,
    {
        CTClib::CTBVERSION, &versions     }
    ,
    {
        CTClib::SKSTAT, &keyStatuses     }
    ,
    {
        CTClib::SIGCLASS, &sigClasses     }
    ,
    {
        CTClib::CTBTYPE, &packetTypes     }
    ,
    {
        CTClib::SIGSUBPKTS, &subPacketTypes     }
    ,
    {
        CTClib::PKEALGOR, &PKEalgors     }
    ,
    {
        CTClib::MDALGOR, &MDalgors     }
    ,
    {
        CTClib::CEALGOR, &CEalgors     }
    ,
    {
        CTClib::COMPANDALGOR, &COMPalgors     }
    ,
    {
        CTClib::SIGSTAT, &sigStats     }
    ,
    {
        NOVALUE, NULL     }
};


//===================================================================

#ifdef _MSC_VER
#pragma warning (default : 4048)
#endif
const char * CTCFox::Strings::libEnumName(int enumeration, int value, bool full)
{
    enumDesc * theEnum = allEnums;
    const keyWord * theKeyword;

    while(theEnum->enumId != (CTClib::cb_enumeration)enumeration)
    {
        if(theEnum->enumId == NOVALUE) return NULL;
        theEnum++;
    }
    theKeyword = *theEnum->values;
    while(theKeyword->keyValue != value && theKeyword->keyValue != NOVALUE)
        theKeyword++;
    if(full)
        return _(theKeyword->desc);
    else
        return _(theKeyword->name);
}

/* end of file keywords.c */




