/*  keywords.c
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
**  Heimdall <heimdall@bifroest.demon.co.uk>  1999
**  All rights reserved.  For full licence details see file licences.c
**
**  This file contains source for obtaining printable values for the
**  main CTC enumeration types.  This is usable as-is for English-only 
**  implementations and test purposes.  It should be consider example code
**  for multi-language or non-English single-language implementations.
*/
#include "keyconst.h"
#include "callback.h"
#include "pkautils.h"

#define NOVALUE -9999

typedef struct
{
    int keyValue;
    char * name;
    char * desc;
}
keyWord;
typedef const keyWord keyWords[];

typedef struct
{
    int enumId;
    keyWords * values;
}
enumDesc;

static const keyWord enumerations[] =
{
    { 
        CB_ENUMERATION, "ENUM", "Enumeration type names"     }
    ,
    { 
        CB_MODULE, "MODULE", "CTC module identifiers"     }
    ,
    { 
        CB_CONTEXT, "CONTEXT", "CTC operation context"     }
    ,
    { 
        CB_SEVERITY, "SEVERITY", "CTC condition severity"     }
    ,
    { 
        CB_PURPOSE, "PURPOSE", "Purpose for which a file is required"     }
    ,
    { 
        CTBVERSION, "VERSION", "Version (of PGP) which introduced the packet format"     }
    ,
    { 
        SKSTAT, "SKSTAT", "Status of a secret key"     }
    ,
    { 
        CTBTYPE, "CTCTYPE", "Type of a CTB packet"     }
    ,
    { 
        SIGCLASS, "SIGCLASS", "Class (significance of) a signature packet"     }
    ,
    { 
        SIGSUBPKTS, "SUBPKT", "Type of a subpacket (in a signature packet"     }
    ,
    { 
        PKEALGOR, "PKEA", "Public-Key Encryption Algorithm"     }
    ,
    { 
        MDALGOR, "MDA", "Message Digest Algorithm"     }
    ,
    { 
        CEALGOR, "CEA", "Conventional (Symmetric) Encryption Algorithm"     }
    ,
    { 
        COMPANDALGOR, "CMPA", "Compress/expansion algorithm"     }
    ,
    { 
        NOVALUE, "????", "Unknown enumeration"     }
};

static const keyWord modules[] =
{
    { 
        CB_CTC_ENGINE, "CTC", "CTC Engine"     }
    ,
    { 
        CB_ARMOUR, "ARMOUR", "ASCII-armour"     }
    ,
    { 
        CB_COMPAND, "COMPND", "Compress/Expand"     }
    ,
    { 
        CB_MSG_DIGEST, "DIGEST", "Message Digest"     }
    ,
    { 
        CB_BULK_CYPHER, "CIPHER", "Bulk Cypher"     }
    ,
    { 
        CB_PKE, "PKE", "Public Key"     }
    ,
    { 
        CB_PK_MANAGE, "KEYMAN", "Key Management"     }
    ,
    { 
        CB_CTB_IO, "CTB_IO", "CTB format I/O"     }
    ,
    { 
        CB_RANDOM, "RANDOM", "Random Numbers"     }
    ,
    { 
        CB_FILING, "FILSYS", "File System"     }
    ,
    { 
        NOVALUE, "????", "Unknown module"     }
};

static const keyWord contexts[] =
{
    { 
        CB_DECRYPTION, "DECRYPT", "Decryption"     }
    ,
    { 
        CB_ENCRYPTION, "ENCRYPT", "Encryption"     }
    ,
    { 
        CB_SIGNING, "SIGNING", "Signing"     }
    ,
    { 
        CB_VERIFYING, "VERIFY", "Verifying"     }
    ,
    { 
        CB_KEYGEN, "KEY_GEN", "Key Generation"     }
    ,
    { 
        CB_KEYMAN, "KEY_MAN", "Key Management"     }
    ,
    { 
        CB_READING_RING,"READRNG", "Reading key-ring"     }
    ,
    { 
        CB_UNKNOWN, "UNKNOWN", "Not Specified"     }
    ,
    { 
        NOVALUE, "????", "Unknown Context"     }
};

static const keyWord severities[] =
{
    { 
        CB_CRASH, "CRASH", "Prog-Crash"     }
    ,
    { 
        CB_FATAL, "FATAL", "Fatal Error"     }
    ,
    { 
        CB_ERROR, "ERROR", "Error"     }
    ,
    { 
        CB_WARNING, "WARN", "Warning"     }
    ,
    { 
        CB_INFO, "INFO", "Information"     }
    ,
    { 
        CB_STATUS, "STAT", "Status"     }
    ,
    { 
        NOVALUE, "????", "Unknown severity"     }
};

static const keyWord purposes[] =
{
    { 
        SIGNEDFILE, "SIGNED", "file corresponding to a signature"     }
    ,
    { 
        SPLITKEY, "SPLIT", "file to hold a conventional key"     }
    ,
    { 
        CYPHERTEXTFILE, "CYPHER", "cyphertext file corresponding to a conventional key"     }
    ,
    { 
        NOVALUE, "????", "Unknown purpose"     }
};

static const keyWord versions[] =
{
    { 
        MIN_VERSION, "2.3", "PGP2.3 or earlier"     }
    ,
    { 
        VERSION_2_6, "2.6", "PGP2.6.x"     }
    ,
    { 
        VERSION_3, "5.x", "PGP3.0 - 5.x"     }
    ,
    { 
        NOVALUE, "???", "Unknown version code"     }
};

static const keyWord keyStatuses[] =
{
    { 
        INTERNAL, "INT", "Ready for use"     }
    , 
    { 
        EXTERNAL, "EXT", "Encrypted"     }
    ,
    { 
        CORRUPT, "CRPT", "Corrupted"     }
    ,
    { 
        NOVALUE, "????", "Unknown key status"     }
};

static const keyWord sigClasses[] =
{
    { 
        SIG_BINARY, "BINFILE", "Binary file signature"     }
    , 
    { 
        SIG_TEXT, "TXTFILE", "Text file signature"     }
    , 
    { 
        SIG_KEY_CERT, "GENERIC", "Generic key signature"     }
    , 
    { 
        SIG_KEY_PERSONA, "PERSONA", "Key persona signature"     }
    , 
    { 
        SIG_KEY_CASUAL, "CASUAL", "Key casual signature"     }
    , 
    { 
        SIG_KEY_POSITIVE, "POSITIVE", "Key positive signature"     }
    , 
    { 
        SIG_KEY_COMPROM, "COMPROM", "Key compromised"     }
    , 
    { 
        SIG_KEY_REVOKE, "REVOKE", "Key Id. revoked"     }
    , 
    { 
        SIG_KEY_TIMESTMP, "TIMESTMP", "Key timestamp"     }
    , 
    { 
        SIG_SUBKEY_CERT, "SKEYCERT", "Sub-Key certificate"     }
    , 
    { 
        SIG_SUBKEY_REVOKE, "SKEYREVK", "Sub-Key revoked"     }
    , 
    { 
        NOVALUE, "????", "Unknown signature type"     }
};

static const keyWord packetTypes[] =
{
    { 
        CTB_PKE, "PKE", "public key encrypted"     }
    ,
    { 
        CTB_SKE, "SKE", "secret key signed"     }
    ,
    { 
        CTB_CONV_ESK, "CESK", "Conventional encryption algorithm details"     }
    ,
    { 
        CTB_1PASS_SIG, "1PSIG", "\"one-pass signature\""     }
    ,
    { 
        CTB_CERT_SECKEY,"SECKEY", "secret key certificate"     }
    ,
    { 
        CTB_CERT_PUBKEY,"PUBKEY", "public key certificate"     }
    ,
    { 
        CTB_SEC_SUBKEY, "SECSKEY", "secret sub-key"     }
    ,
    { 
        CTB_COMPRESSED, "COMPRES", "compressed data"     }
    ,
    { 
        CTB_CKE, "CKE", "conventionally encrypted data"     }
    ,
    { 
        CTB_LITERAL, "LITERL", "raw data with filename and mode"     }
    ,
    { 
        CTB_LITERAL2, "LITERL2", "Fixed literal packet"     }
    ,
    { 
        CTB_KEYCTRL, "TRUST", "Trust byte"     }
    ,
    { 
        CTB_USERID, "USERID", "key username(id)"     }
    ,
    { 
        CTB_PUB_SUBKEY, "PUBSKEY", "public sub-key"     }
    ,
    { 
        CTB_COMMENT, "COMMENT", "comment"     }
    ,
    { 
        NOVALUE, "????", "Unknown (CTB) packet type"     }
};

/* Where appropriate the long text for the following enumeration
** type is the precise description from the current OpenPGP draft */
static const keyWord subPacketTypes[] =
{
    { 
        SUBPKT_VERSION, "VERSION", "Version Number"     }
    ,
    { 
        SUBPKT_CREATION, "CRT_DATE", "signature creation time"     }
    ,
    { 
        SUBPKT_EXPIRY, "EXPIRY", "signature expiration time"     }
    ,
    { 
        SUBPKT_EXPORTABLE_CERT, "EXPTCERT", "exportable certification"     }
    ,
    { 
        SUPPKT_TRUST_SIGNATURE, "TRUSTSIG", "trust signature"     }
    ,
    { 
        SUBPKT_REGEXP, "REGEXPR", "regular expression"     }
    ,
    { 
        SUBPKT_REVOCABLE, "REVOCBLE", "revocable"     }
    ,
    { 
        SUBPKT_KEY_CAPABILITIES, "CAPS", "Key capabilities"     }
    ,
    { 
        SUBPKT_KEY_EXPIRY, "KEYEXPRY" "key expiration time"     }
    ,
    /* The OpenPGP description for the next line is 
         ** "placeholder for backward compatibility" */
    { 
        SUBPKT_KEY_RECOVERY_KEY, "KRK", "Key Recovery Key"     }
    ,
    { 
        SUBPKT_KEY_PREFERRED_ALGS, "PRFALG", "preferred symmetric algorithms"     }
    ,
    { 
        SUBPKT_REVOCATION_KEY, "RECOVKEY", "revocation key"     }
    ,
    { 
        SUBPKT_KEYID, "KEYID", "issuer key ID"     }
    ,
    { 
        SUBPKT_USERID, "USERID", "User Id."     }
    ,
    { 
        SUBPKT_URL, "URL", "URL"     }
    ,
    { 
        SUBPKT_FINGER, "FINGER", "Key Finger-print"     }
    ,
    { 
        SUBPKT_NOTATION_DATA, "NOTEDATA", "notation data"     }
    ,
    { 
        SUBPKT_PREFERRED_MDA, "PREF_MDA", "preferred hash algorithms"     }
    ,
    { 
        SUBPKT_PREFERRED_CPA, "PREF_CPA", "preferred compression algorithms"     }
    ,
    { 
        SUBPKT_KEYSERVER_PREFS, "SERVPREF", "key server preferences"     }
    ,
    { 
        SUBPKT_PREFERRED_KEYSERVER, "PREFSERV", "preferred key server"     }
    ,
    { 
        SUBPKT_PRIMARY_USERID, "PRIMID", "primary user id"     }
    ,
    { 
        SUBPKT_POLICY_URL, "POL_URL", "policy URL"     }
    ,
    { 
        SUBPKT_KEY_FLAGS, "KEYFLAGS", "key flags"     }
    ,
    { 
        SUBPKT_SIGNER_USERID, "SIGNERID", "signer's user id"     }
    ,
    { 
        SUBPKT_REVOCATION_REASON, "RVREASON", "reason for revocation"     }
    ,
    { 
        SUBPKT_MIN_USERDEFINED, "USERDEF0", "internal/user-defined:0"     }
    ,
    { 
        SUBPKT_MIN_USERDEFINED + 1, "USERDEF1", "internal/user-defined:1"     }
    ,
    { 
        SUBPKT_MIN_USERDEFINED + 2, "USERDEF2", "internal/user-defined:2"     }
    ,
    { 
        SUBPKT_MIN_USERDEFINED + 3, "USERDEF3", "internal/user-defined:3"     }
    ,
    { 
        SUBPKT_MIN_USERDEFINED + 4, "USERDEF4", "internal/user-defined:4"     }
    ,
    { 
        SUBPKT_MIN_USERDEFINED + 5, "USERDEF5", "internal/user-defined:5"     }
    ,
    { 
        SUBPKT_MIN_USERDEFINED + 6, "USERDEF6", "internal/user-defined:6"     }
    ,
    { 
        SUBPKT_MIN_USERDEFINED + 7, "USERDEF7", "internal/user-defined:7"     }
    ,
    { 
        SUBPKT_MIN_USERDEFINED + 8, "USERDEF8", "internal/user-defined:8"     }
    ,
    { 
        SUBPKT_MIN_USERDEFINED + 9, "USERDEF9", "internal/user-defined:9"     }
    ,
    { 
        SUBPKT_MIN_USERDEFINED + 10, "USERDEF10", "internal/user-defined:10"     }
    ,
    { 
        NOVALUE, "????", "Unknown sub-packet type"     }
};

static const keyWord PKEalgors[] =
{
    { 
        PKA_RSA, "RSA", "RSA (Rivest, Shamir & Adleman)"     }
    ,
    { 
        PKA_RSA_ENCRYPT_ONLY, "RSA_ENCR", "RSA (encryption only key)"     }
    ,
    { 
        PKA_RSA_SIGN_ONLY, "RSA_SIGN", "RSA (signature only key)"     }
    ,
    { 
        PKA_ELGAMAL, "ELGAMAL", "ElGamal/Diffie-Hellman"     }
    ,
    { 
        PKA_DSA, "DSA", "DSA (Digital Signature Algorithm)"     }
    ,
    { 
        PKA_EBP_RSA, "EBP_RSA", "EBP's value for RSA"     }
    ,
    { 
        PKA_EBP_RABIN, "EBP_RABIN","EBP's value for Rabin"     }
    ,
    { 
        PKA_GF2255, "GF2_255", "elliptic curve on GF(2^255)"     }
    ,
    { 
        PKA_PEGWIT9, "PEGWIT9", "elliptic curve as per Pegwit v9"     }
    ,
    { 
        NOVALUE, "????", "unknown PKE algorithm"     }
};


static const keyWord MDalgors[] =
{
    { 
        MDA_MD5, "MD5", "MD5 (Message Digest 5)"     }
    ,
    { 
        MDA_PGP5_SHA1, "SHA1P","NIST SHA (Secure Hash Algorithm) variant 1 (PGP code)"     }
    ,
    { 
        MDA_PGP5_RIPEM160, "RIPEM","RIPEM 160-bit"     }
    ,
    /* { MDA_EBP_HAVAL_MIN, "HAVAL1","EBP uses this range for 15 varieties of HAVAL" },
         { MDA_EBP_HAVAL_MAX, "HAVALN"," all of them subtly wrong :(" }, */
    { 
        MDA_3WAY, "3WAY", "3-way used to produce 96 bit hash"     }
    ,
    { 
        MDA_SHA, "SHA", "NIST SHA original"     }
    ,
    { 
        MDA_SHA1, "SHA1", "NIST SHA (Secure Hash Algorithm) variant 1 (CTC code)"     }
    ,
    /* #define MDA_HAVAL_MIN (5|MDA_FLEX_FLAG)
        #define MDA_HAVAL_MAX (MDA_HAVAL_RANGE +MDA_HAVAL_MIN) */
    { 
        NOVALUE, "????", "Unknown message digest"     }
};

static const keyWord CEalgors[] =
{
    { 
        CEA_IDEA, "IDEAP", "IDEA (International Data Encryption Algorithm) [PGP]"     }
    ,
    { 
        CEA_3DES, "3DESP", "Triple-DES [PGP]"     }
    ,
    { 
        CEA_CAST5, "CASTP", "CAST5 [PGP]"     }
    ,
    /* GPG algorithm values */
    {
        CEA_GPG_BLOW16, "BLOW16P", "Blowfish with 16-byte key [GPG, OpenPGP]"     }
    ,
    {
        CEA_OPGP_SAFERSK128, "SAFERP", "SAFER (Secure And Fast Encryption Routine) SK128 13-rounds [OpenPGP]"     }
    ,
    /*#define CEA_GPG_DES_SK        7*/
    {
        CEA_GPG_BLOW20, "BLOW20G", "Blowfish with 20-byte key [GPG]"     }
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
        CEA_IDEAFLEX, "IDEA", "IDEA (International Data Encryption Algorithm) [CTC]"     }
    ,
    { 
        CEA_3WAY, "3WAY", "3-WAY cipher"     }
    ,
    { 
        CEA_BLOW16, "BLOW16", "Blowfish with 16-byte key"     }
    ,
    { 
        CEA_TEA, "TEA", "TEA (Tiny Encryption Algorithm)"     }
    ,
    { 
        CEA_BLOW5, "BLOW5", "Blowfish with 40-bit key"     }
    ,
    { 
        CEA_SQUARE, "SQUARE", "Square 128 bit key and block"     }
    ,
    { 
        CEA_DES, "DES", "DES (Data Encryption Standard)"     }
    ,
    { 
        CEA_S3DES, "S3DES", "s3DES"     }
    ,
    { 
        CEA_KDDES, "KDES", "key dependent DES"     }
    ,
    { 
        CEA_3DESFLEX, "3DES", "Triple-DES [CTC]"     }
    ,
    { 
        CEA_CAST5FLEX, "CAST5", "CAST5 [CTC]"     }
    ,
    /* { CEA_ROT_NFLEX, "ROTN",  "ROT" },*/
    { 
        CEA_SAFERSK128FLEX, "SAFER","SAFER (Secure And Fast Encryption Routine)"     }
    ,
    { 
        CEA_DESSKFLEX, "KDESF", "key dependent DES"     }
    ,
    { 
        CEA_BLOW20, "BLOW20", "Blowfish with 20-byte key"     }
    ,
    { 
        CEA_GOSTFLEX, "GOST", "GOST (Gosudarstvennyi Standard)"     }
    ,
    { 
        CEA_OPGP_AES_128, "AES128", "AES (Advanced Encryption Standard) 128-bit key [OpenPGP]"     }
    ,
    { 
        CEA_AES_128, "AES128F", "AES (Advanced Encryption Standard) 128-bit key [CTC]"     }
    ,
    { 
        CEA_OPGP_AES_192, "AES192", "AES (Advanced Encryption Standard) 192-bit key [OpenPGP]"     }
    ,
    { 
        CEA_AES_192, "AES192F", "AES (Advanced Encryption Standard) 128-bit key [CTC]"     }
    ,
    { 
        CEA_OPGP_AES_256, "AES256", "AES (Advanced Encryption Standard) 256-bit key [OpenPGP]"     }
    ,
    { 
        CEA_TWOFISH_256, "AES256F", "AES (Advanced Encryption Standard) 128-bit key [CTC]"     }
    ,
    { 
        CEA_AES_256, "TWO256", "Twofish 256-bit key [OpenPGP]"     }
    ,
    { 
        CEA_OPGP_TWOFISH_256, "TWO256", "Twofish 256-bit key [CTC]"     }
    ,
    { 
        NOVALUE, "????", "Unknown bulk cipher"     }
};

static const keyWord COMPalgors[] =
{
    { 
        CPA_NONE, "NONE", "no compression"     }
    , 
    { 
        CPA_DEFLATE, "DEFLATE", "Zip-based deflate compression algorithm"     }
    , 
    { 
        CPA_SPLAY, "SPLAY", "Splay tree compression"     }
    , 
    { 
        NOVALUE, "????", "Unknown compression method"     }
};


static const keyWord sigStats[] =
{
    { 
        SS_BAD, "BAD", "Signature does not match (confirmed)"     }
    ,
    { 
        SS_SUSPECT, "SUSPECT", "Signature does not match"     }
    ,
    { 
        SS_UNKNOWN, "UNKNOWN", "Signature has not been checked"     }
    ,
    { 
        SS_PROBABLE,"PROBABLE", "Signature matchs (unconfirmed)"     }
    ,
    { 
        SS_VERIFIED,"VERIFIED", "Signature verified"     }
    ,
    { 
        NOVALUE, "????", "Unknown signature status"     }
};

#ifdef _MSC_VER
/* disable warning C4048: different array subscripts : 
'const struct keyWord (*)[]' and 'const struct keyWord (*)[15]
etc.
*/
#pragma warning ( disable : 4048)
#endif

static enumDesc allEnums[] =
{
    { 
        CB_ENUMERATION, &enumerations     }
    ,
    { 
        CB_MODULE, &modules     }
    ,
    { 
        CB_CONTEXT, &contexts     }
    ,
    { 
        CB_SEVERITY, &severities     }
    ,
    { 
        CB_PURPOSE, &purposes     }
    ,
    { 
        CTBVERSION, &versions     }
    ,
    { 
        SKSTAT, &keyStatuses     }
    ,
    { 
        SIGCLASS, &sigClasses     }
    ,
    { 
        CTBTYPE, &packetTypes     }
    ,
    { 
        SIGSUBPKTS, &subPacketTypes     }
    ,
    { 
        PKEALGOR, &PKEalgors     }
    ,
    { 
        MDALGOR, &MDalgors     }
    ,
    { 
        CEALGOR, &CEalgors     }
    ,
    { 
        COMPANDALGOR, &COMPalgors     }
    ,
    { 
        SIGSTAT, &sigStats     }
    ,
    { 
        NOVALUE, NULL     }
};

#ifdef _MSC_VER
#pragma warning ( default : 4048)
#endif

char * enumName(cb_enumeration enumeration, int value, boolean full)
{
    enumDesc * theEnum = allEnums;
    const keyWord * theKeyword;

    while(theEnum->enumId != enumeration)
    {
        if(theEnum->enumId == NOVALUE) return NULL;
        theEnum++;
    }
    theKeyword = *theEnum->values;
    while(theKeyword->keyValue != value && theKeyword->keyValue != NOVALUE)
        theKeyword++;
    if(full)
        return theKeyword->desc;
    else
        return theKeyword->name;
}

/* end of file keywords.c */




