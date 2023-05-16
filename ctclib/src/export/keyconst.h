/* keyconst.h - Constants for PKE key management
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
**  Heimdall <heimdall@bifroest.demon.co.uk>  1996
**  All rights reserved.  For full licence details see file licences.c
*/
#ifndef _key_consts
#define _key_consts
#include "basic.h"

#define MAXBUFFERSIZE 2048

typedef enum Skstat_t {
    INTERNAL, EXTERNAL, CORRUPT }
Skstat;

/* Cipher Type Byte (CTB) fields */
#define CTB_DESIGNATOR 0x80  /* Version PGP 2.x style CTB */
#define CTB_PGP3   0xc0   /* Version PGP 3 style CTB */
#define CTB_DESG_MASK 0xc0
#define CTB_TYPE_MASK 0x3c
#define CTB_TYPE_MSK3 0x3f
#define CTB_LLEN_MASK 0x03

#define CTB_PKE    1  /* packet encrypted with RSA public key */
#define CTB_SKE      2  /* packet signed with RSA secret key */
#define CTB_CONV_ESK   3  /* PGP3: new packet type */
#define CTB_1PASS_SIG  4  /* PGP3: new packet type */
#define CTB_CERT_SECKEY  5   /* secret key certificate */
#define CTB_CERT_PUBKEY  6  /* public key certificate */
#define CTB_SEC_SUBKEY  7  /* PGP3: new packet type */
#define CTB_COMPRESSED  8  /* compressed data packet */
#define CTB_CKE      9  /* conventional-key-encrypted data */
#define CTB_LITERAL  10  /* raw data with filename and mode */
#define CTB_LITERAL2  11  /* Fixed literal packet */
#define CTB_KEYCTRL   12  /* key control packet */
#define CTB_USERID  13  /* user id packet */
#define CTB_PUB_SUBKEY 14  /* PGP3: new packet type
** N.B. THIS IS AN INCOMPATIBLE CHANGE AT PGP5.0
** THIS VALUE WAS PREVIOUS A COMMENT PACKET. */
#define CTB_OLD_COMMENT 14  /* PGP2.6 comment packet; now superceded */
#define CTB_COMMENT  16

#define SUBPKT_VERSION    1 /* Not in OpenPGP */
#define SUBPKT_CREATION    2
#define SUBPKT_EXPIRY    3
#define SUBPKT_EXPORTABLE_CERT      4
#define SUPPKT_TRUST_SIGNATURE      5
#define SUBPKT_REGEXP               6
#define SUBPKT_REVOCABLE            7
/* this next is not in OpenPGP */
#define SUBPKT_KEY_CAPABILITIES  8
#define SUBPKT_KEY_EXPIRY           9
/* OpenPGP has this as a placeholder for backwards compatibility */
#define SUBPKT_KEY_RECOVERY_KEY  10
#define SUBPKT_KEY_PREFERRED_ALGS 11
#define SUBPKT_REVOCATION_KEY  12
#define SUBPKT_KEYID    16
/* 17-19 are not in Open PGP */
#define SUBPKT_USERID    17
#define SUBPKT_URL     18
#define SUBPKT_FINGER     19
#define SUBPKT_NOTATION_DATA        20
#define SUBPKT_PREFERRED_MDA        21
#define SUBPKT_PREFERRED_CPA        22
#define SUBPKT_KEYSERVER_PREFS      23
#define SUBPKT_PREFERRED_KEYSERVER  24
#define SUBPKT_PRIMARY_USERID       25
#define SUBPKT_POLICY_URL           26
#define SUBPKT_KEY_FLAGS            27
#define SUBPKT_SIGNER_USERID        28
#define SUBPKT_REVOCATION_REASON    29
#define SUBPKT_MIN_USERDEFINED      100
#define SUBPKT_MAX_USERDEFINED      110


#define SIG_BINARY    0x00
#define SIG_TEXT     0x01
#define SIG_KEY_CERT   0x10
#define SIG_KEY_PERSONA  0x11
#define SIG_KEY_CASUAL  0x12
#define SIG_KEY_POSITIVE  0x13
#define SIG_SUBKEY_CERT  0x18
#define SIG_SUBKEY_REVOKE 0x28
#define SIG_KEY_COMPROM  0x20
#define SIG_KEY_REVOKE  0x30
#define SIG_KEY_TIMESTMP  0x40

/* Trust Byte fields */
/* KTB = Key Trust Byte */
#define KTB_OWN_MASK 0x07
#define KTB_OWN_UNDEFINED 0x00
#define KTB_OWN_UNKNOWN  0x01
#define KTB_OWN_UNTRUSTED 0x02
#define KTB_OWN_USUALLY  0x05
#define KTB_OWN_TRUSTED  0x06
#define KTB_OWN_OWNKEY  0x07
#define KTB_ENABLE_MASK  0x20
#define KTB_ENABLE_ENABLE 0x00
#define KTB_ENABLE_DISABLE KTB_ENABLE_MASK
#define KTB_BUCKSTOP_MASK 0x80
#define KTB_BUCKSTOP_TRUE KTB_BUCKSTOP_MASK
#define KTB_BUCKSTOP_FALSE 0x00

/* Public key encryption algorithm selector bytes. */
/*  This is probably wrong.  There should probably be separate selectors
**  for key exchange and authentication algorithm (many cannot be used for both) */
/* perhaps should have PKE_<alg> and PKS_<alg> for encrypt and sign */
#define PKA_FLEX_FLAG 128

#define PKA_RSA 1 /* use RSA */
#define PKA_RSA_ENCRYPT_ONLY 2
#define PKA_RSA_SIGN_ONLY 3
#define PKA_ELGAMAL   16 /* PGP5 code for ElGamal/Diffie-Hellman */
#define PKA_DSA    17 /* PGP5 code for DSA */
#define PKA_EBP_RSA 86               /* EBP also uses this value - NYI */
#define PKA_EBP_RABIN 97             /* EBP uses 97 to indicate Rabin - NYI*/

#define PKA_GF2255 (1|PKA_FLEX_FLAG) /* use elliptic curve on GF(2^255) as per Pegwit v8*/
#define PKA_PEGWIT9 (2|PKA_FLEX_FLAG) /* ditto, but as per Pegwit v9 */

/* Conventional encryption algorithm selector bytes. */
#define CEA_FLEX_FLAG 128 /* Not a PGP-classic imitator */
#define CEA_MORE_FLAG 64  /* Another cipher key follows */
#define CEA_MASK    191    /* removes the "more" flag */

#define CEA_IDEA 1                    /* use  the IDEA cipher */
#define CEA_3DES 2  /* PGP5 code for Triple-DES -
note that we cannot use CEM flags with this */
#define CEA_CAST5 3  /* PGP5 code for CAST */

/* OpenPGP algorithm values - common with GPG */
#define CEA_GPG_BLOW16    4

/* Open PGP values - given priority over GPG in this range */
/* they omit the silly GPG ROT-N */
#define CEA_OPGP_SAFERSK128  5  /* 13 rounds */
#define CEA_OPGP_DES_SK        6
#define CEA_OPGP_AES_128       7
#define CEA_OPGP_AES_192       8
#define CEA_OPGP_AES_256       9
#define CEA_OPGP_TWOFISH_256   10 /* according to Jon Callas <jon@pgp.com> */

/* Implemented by GPG */
#define CEA_GPG_BLOW20        42
#define CEA_GPG_GOST          43  /* which S-boxes? last I saw, the routine by this name  was IDEA */

#define CEA_EBP_IDEA 86                  /* EBP uses this value also - NYI*/
#define CEA_EBP_SAFER_MIN 97             /* EBP has 4 varieties of Safer - NYI*/
#define CEA_EBP_SAFER_MAX 100            /* but it looks like 97 = SK128 is the
only one used */
#define CEA_GPG_TWOFISH_128  102

#define CEA_NONE    (0|CEA_FLEX_FLAG)   /* explicit no encryption - more
flag must not be set*/
#define CEA_IDEAFLEX (CEA_IDEA|CEA_FLEX_FLAG) /* systematic IDEA*/
#define CEA_3WAY    (2|CEA_FLEX_FLAG)   /*  use the 3-way cipher */
#define CEA_BLOW16  (3|CEA_FLEX_FLAG)   /* Blowfish with 16 byte key */
#define CEA_TEA     (4|CEA_FLEX_FLAG)   /* Tiny Encryption Algorithm */
#define CEA_BLOW5   (5|CEA_FLEX_FLAG)   /* Blowfish with 40-bit key */
#define CEA_SQUARE  (6|CEA_FLEX_FLAG)   /* Square 128 bit key and block */
#define CEA_DES   (7|CEA_FLEX_FLAG)   /* Single DES */
#define CEA_S3DES   (8|CEA_FLEX_FLAG)   /* s3DES */
#define CEA_KDDES   (9|CEA_FLEX_FLAG)   /* key dependent DES */
#define CEA_3DESFLEX (10|CEA_FLEX_FLAG)
#define CEA_CAST5FLEX (11|CEA_FLEX_FLAG)
/*#define CEA_ROT_NFLEX (12|CEA_FLEX_FLAG)*/
#define CEA_SAFERSK128FLEX (13|CEA_FLEX_FLAG)
#define CEA_DESSKFLEX (14|CEA_FLEX_FLAG)
#define CEA_BLOW20   (15|CEA_FLEX_FLAG)
#define CEA_GOSTFLEX (16|CEA_FLEX_FLAG)
#define CEA_AES_128       (17|CEA_FLEX_FLAG)
#define CEA_AES_192       (18|CEA_FLEX_FLAG)
#define CEA_AES_256       (19|CEA_FLEX_FLAG)
#define CEA_TWOFISH_256   (20|CEA_FLEX_FLAG)
#define CEA_TWOFISH_128   (21|CEA_FLEX_FLAG)

#define CEA_ESCAPE (0|CEA_MORE_FLAG|CEA_FLEX_FLAG)
/* other data is present if only CEA_MORE_FLAG set */

/* Conventional encryption mode selector bytes */
#define CEM_REVERSE_FLAG 128 /* work from the end of the file */
#define CEM_TRIPLE_FLAG  64  /* three keys follow for outer chaining */
#define CEM_MASK    63  /* as above */

#define CEM_CFB  1  /* CFB mode - assumed for PGP-classic */
#define CEM_ECB      2  /* ECB mode - with ciphertext stealing */
#define CEM_OFB      3  /* OFB mode */
#define CEM_CBC      4  /* CFB mode - with ciphertext stealing */

/* Message digest algorithm selector bytes. */
#define MDA_FLEX_FLAG 128 /* as CEA_FLEX_FLAG */
#define MDA_MASK 127   /* as CEA_MASK */

#define MDA_HAVAL_RANGE 14

#define MDA_MD5 1 /* MD5 message digest algorithm */
#define MDA_PGP5_SHA1 2 /* PGP5 code for SHA-1 */
#define MDA_PGP5_RIPEM160 3 /* PGP5 code for RIPEM 160 */
#define MDA_EBP_HAVAL_MIN 97   /* EBP uses this range for 15 varieties of HAVAL, */
#define MDA_EBP_HAVAL_MAX (MDA_EBP_HAVAL_MIN+MDA_HAVAL_RANGE)   /* all of them subtly wrong :( */
#define MDA_3WAY (2|MDA_FLEX_FLAG) /* 3-way used to produce 96 bit hash */
#define MDA_SHA (3|MDA_FLEX_FLAG) /* the NIST SHA 160bit hash */
#define MDA_SHA1 (4|MDA_FLEX_FLAG) /* SHA-1 modification */
#define MDA_HAVAL_MIN (5|MDA_FLEX_FLAG)
#define MDA_HAVAL_MAX (MDA_HAVAL_RANGE +MDA_HAVAL_MIN)

/* Data compression algorithm selector bytes. */
#define CPA_FLEX_FLAG 128

#define CPA_NONE 0 /* do no compression */
#define CPA_DEFLATE  1 /* Zip-based deflate compression algorithm */
#define CPA_SPLAY (1|CPA_FLEX_FLAG) /* Splay tree based ditto */

/* Version byte for data structures created by this version of PGP */
#define MIN_VERSION  2 /* PGP2 to 2.5 */
#define VERSION_2_6  3 /* PGP2.6 */
#define VERSION_3  4 /* PGP3 (PGP5 and up, OpenPGP) */
/* Note that there is not global Maximum version; this may vary from packet type to packet type
** according to what has been implemented */
#define DEFAULT_VERSION 3 /* PGP2 */


#endif
