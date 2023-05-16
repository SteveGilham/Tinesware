
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. Tines <tines@windsong.demon.co.uk>
//Company:      Ravna&Tines
//Description:  A Java[tm]1.1-based portable GUI to CTClib2.0
/* keyconst.h - Constants for PKE key management
 **
 **  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 **  Heimdall <heimdall@bifroest.demon.co.uk>  1996
 **  Java port Mr. Tines 1998
 **  All rights reserved.  For full licence details see file licence.c
 */


package com.ravnaandtines.ctcjava;

public class CTCIKeyConst {
    public static final int MAXBUFFERSIZE = 2048;
    public static final int INTERNAL = 0;
    public static final int EXTERNAL = 1;
    public static final int CORRUPT  = 2;

    /* Cipher Type Byte (CTB) fields */
    public static final byte CTB_DESIGNATOR = (byte)0x80;    /* Version PGP 2.x style CTB */
    public static final byte CTB_PGP3       = (byte)0xc0;    /* Version PGP 3 style CTB */
    public static final byte CTB_DESG_MASK  = (byte)0xc0;
    public static final byte CTB_TYPE_MASK  = (byte)0x3c;
    public static final byte CTB_TYPE_MSK3  = (byte)0x3f;
    public static final byte CTB_LLEN_MASK  = (byte)0x03;

    public static final int CTB_PKE =             1;    // packet encrypted with RSA public key
    public static final int CTB_SKE =             2;    // packet signed with RSA secret key
    public static final int CTB_CONV_ESK =        3;    // PGP3: new packet type
    public static final int CTB_1PASS_SIG =       4;    // PGP3: new packet type
    public static final int CTB_CERT_SECKEY =     5;    // secret key certificate
    public static final int CTB_CERT_PUBKEY =     6;    // public key certificate
    public static final int CTB_SEC_SUBKEY =      7;    // PGP3: new packet type
    public static final int CTB_COMPRESSED =      8;    // compressed data packet
    public static final int CTB_CKE =             9;    // conventional-key-encrypted data
    public static final int CTB_LITERAL =        10;    // raw data with filename and mode
    public static final int CTB_LITERAL2 =       11;    // Fixed literal packet
    public static final int CTB_KEYCTRL =        12;    // key control packet
    public static final int CTB_USERID =         13;    // user id packet
    public static final int CTB_PUB_SUBKEY =     14;    // PGP3: new packet type
                                          // N.B. THIS IS AN INCOMPATIBLE CHANGE AT PGP5.0
                                          //  THIS VALUE WAS PREVIOUS A COMMENT PACKET. */
    public static final int CTB_OLD_COMMENT =    14;    // PGP2.6 comment packet; now superceded
    public static final int CTB_COMMENT =        16;

    public static final int SUBPKT_VERSION =              1;
    public static final int SUBPKT_CREATION =             2;
    public static final int SUBPKT_EXPIRY =               3;
    public static final int SUBPKT_KEY_CAPABILITIES =     8;
    public static final int SUBPKT_KEY_RECOVERY_KEY =     9;
    public static final int SUBPKT_KEY_PREFERRED_ALGS =  10;
    public static final int SUBPKT_KEYID =               16;
    public static final int SUBPKT_USERID =              17;
    public static final int SUBPKT_URL =                 18;
    public static final int SUBPKT_FINGER =              19;

    public static final byte SIG_BINARY =        (byte)0x00;
    public static final byte SIG_TEXT =          (byte)0x01;
    public static final byte SIG_KEY_CERT =      (byte)0x10;
    public static final byte SIG_KEY_PERSONA =   (byte)0x11;
    public static final byte SIG_KEY_CASUAL =    (byte)0x12;
    public static final byte SIG_KEY_POSITIVE =  (byte)0x13;
    public static final byte SIG_SUBKEY_CERT =   (byte)0x18;
    public static final byte SIG_SUBKEY_REVOKE = (byte)0x28;
    public static final byte SIG_KEY_COMPROM =   (byte)0x20;
    public static final byte SIG_KEY_REVOKE =    (byte)0x30;
    public static final byte SIG_KEY_TIMESTMP =  (byte)0x40;

    /* Trust Byte fields */
    /* KTB = Key Trust Byte */
    public static final byte KTB_OWN_MASK =       (byte)0x07;
    public static final byte KTB_OWN_UNDEFINED =  (byte)0x00;
    public static final byte KTB_OWN_UNKNOWN =    (byte)0x01;
    public static final byte KTB_OWN_UNTRUSTED =  (byte)0x02;
    public static final byte KTB_OWN_USUALLY =    (byte)0x05;
    public static final byte KTB_OWN_TRUSTED =    (byte)0x06;
    public static final byte KTB_OWN_OWNKEY =     (byte)0x07;
    public static final byte KTB_ENABLE_MASK =    (byte)0x20;
    public static final byte KTB_ENABLE_ENABLE =  (byte)0x00;
    public static final byte KTB_ENABLE_DISABLE = KTB_ENABLE_MASK;
    public static final byte KTB_BUCKSTOP_MASK =  (byte)0x80;
    public static final byte KTB_BUCKSTOP_TRUE =  KTB_BUCKSTOP_MASK;
    public static final byte KTB_BUCKSTOP_FALSE = (byte)0x00;

    /* Public key encryption algorithm selector bytes. */
    /*  This is probably wrong.  There should probably be separate selectors
     **  for key exchange and authentication algorithm (many cannot be used for both) */     
    /* perhaps should have PKE_<alg> and PKS_<alg> for encrypt and sign */
    public static final byte PKA_FLEX_FLAG =      (byte)0x80;

    public static final byte PKA_RSA = 1;     
    /* use RSA */
    public static final byte PKA_RSA_ENCRYPT_ONLY = 2;
    public static final byte PKA_RSA_SIGN_ONLY =    3;
    public static final byte PKA_ELGAMAL =         16; // PGP5 code for ElGamal/Diffie-Hellman
    public static final byte PKA_DSA =             17; // PGP5 code for DSA
    public static final byte PKA_GF2255 = (byte)(1|PKA_FLEX_FLAG);  // use elliptic curve on GF(2^255)
    public static final byte PKA_EBP_RSA =         86;    // EBP also uses this value
    public static final byte PKA_EBP_RABIN =       97;    // EBP uses 97 to indicate Rabin


    /* Conventional encryption algorithm selector bytes. */
    public static final byte CEA_FLEX_FLAG       = (byte)0x80;   // Not a PGP-classic imitator
    public static final byte CEA_MORE_FLAG       =         64;   // Another cipher key follows
    public static final byte CEA_MASK            = (byte)0xAF;   // removes the "more" flag

    public static final byte CEA_IDEA            =          1;   // use  the IDEA cipher
    public static final byte CEA_3DES            =          2;   // PGP5 code for Triple-DES
                                        // note that we cannot use CEM flags with this
    public static final byte CEA_CAST5           =          3;   // PGP5 code for CAST

    /* OpenPGP algorithm values - common with GPG */
    public static final byte CEA_GPG_BLOW16      =          4;

    /* Open PGP values - given priority over GPG in this range */
    /* they omit the silly GPG ROT-N */
    public static final byte CEA_OPGP_SAFERSK128 =          5;
    public static final byte CEA_OPGP_DES_SK     =          6;
    public static final byte CEA_OPGP_AES_128    =          7;
    public static final byte CEA_OPGP_AES_192    =          8;
    public static final byte CEA_OPGP_AES_256    =          9;
    public static final byte CEA_OPGP_TWOFISH_256 =        10;   // according to Jon Callas <jon@pgp.com>

    public static final byte CEA_GPG_BLOW20      =         42;
    public static final byte CEA_GPG_GOST        =         43;

    public static final byte CEA_EBP_IDEA        =         86;   // EBP uses this value also
    public static final byte CEA_EBP_SAFER_MIN   =         97;   // EBP has 4 varieties of Safer
    public static final byte CEA_EBP_SAFE_MAX    =        100;
    public static final byte CEA_NONE            = (byte)(0|CEA_FLEX_FLAG); // explicit no encryption
                                                 // "more" flag must not be set
    public static final byte CEA_IDEAFLEX        = (byte)(CEA_IDEA|CEA_FLEX_FLAG); // systematic IDEA
    public static final byte CEA_3WAY            = (byte)(2|CEA_FLEX_FLAG);        //  use the 3-way cipher
    public static final byte CEA_BLOW16          = (byte)(3|CEA_FLEX_FLAG);        // Blowfish with 16 byte key
    public static final byte CEA_TEA             = (byte)(4|CEA_FLEX_FLAG);        // Tiny Encryption Algorithm
    public static final byte CEA_BLOW5           = (byte)(5|CEA_FLEX_FLAG);        // Blowfish with 40-bit key
    public static final byte CEA_SQUARE          = (byte)(6|CEA_FLEX_FLAG);        // Square 128 bit key and block
    public static final byte CEA_DES             = (byte)(7|CEA_FLEX_FLAG);        // Single DES
    public static final byte CEA_S3DES           = (byte)(8|CEA_FLEX_FLAG);        // s3DES
    public static final byte CEA_KDDES           = (byte)(9|CEA_FLEX_FLAG);        // key dependent DES
    public static final byte CEA_3DESFLEX        = (byte)(10|CEA_FLEX_FLAG);
    public static final byte CEA_CAST5FLEX       = (byte)(11|CEA_FLEX_FLAG);
    public static final byte CEA_ESCAPE          = (byte)(0|CEA_MORE_FLAG|CEA_FLEX_FLAG);
                                                 // other data is present if only CEA_MORE_FLAG set

    /* Conventional encryption mode selector bytes */
    public static final byte CEM_REVERSE_FLAG    = (byte)0x80;      // work from the end of the file
    public static final byte CEM_TRIPLE_FLAG     = 64;              // three keys follow for outer chaining
    public static final byte CEM_MASK            = 63;              // as above

    public static final byte CEM_CFB             = 1;               // CFB mode - assumed for PGP-classic
    public static final byte CEM_ECB             = 2;               // ECB mode - with ciphertext stealing
    public static final byte CEM_OFB             = 3;               // OFB mode
    public static final byte CEM_CBC             = 4;               // CFB mode - with ciphertext stealing

    /* Message digest algorithm selector bytes. */
    public static final byte MDA_FLEX_FLAG       = (byte)0x80;      // as CEA_FLEX_FLAG
    public static final byte MDA_MASK            = 127;             // as CEA_MASK

    public static final byte MDA_MD5             = 1;               // MD5 message digest algorithm
    public static final byte MDA_PGP5_SHA1       = 2;               // PGP5 code for SHA-1
    public static final byte MDA_PGP5_RIPEM160   = 3;               // PGP5 code for RIPEM 160
    public static final byte MDA_EBP_HAVAL_MIN   = 97;              // EBP uses this range for 15 varieties of HAVAL
    public static final byte MDA_EBP_HAVAL_MAX   = 111;
    public static final byte MDA_3WAY            = (byte)(2|MDA_FLEX_FLAG);  // 3-way used to produce 96 bit hash
    public static final byte MDA_SHA             =  (byte)(3|MDA_FLEX_FLAG); // the NIST SHA 160bit hash
    public static final byte MDA_SHA1            = (byte)(4|MDA_FLEX_FLAG);  // SHA-1 modification

    /* Data compression algorithm selector bytes. */
    public static final byte CPA_FLEX_FLAG       = (byte)0x80;

    public static final byte CPA_NONE            = 0;                // do no compression
    public static final byte CPA_DEFLATE         = 1;                // Zip-based deflate compression algorithm
    public static final byte CPA_SPLAY           = (byte)(1|CPA_FLEX_FLAG); // Splay tree based ditto

    /* Version byte for data structures created by this version of PGP */
    public static final int MIN_VERSION          = 2;                // PGP2 to 2.5
    public static final int VERSION_2_6          = 3;                // PGP2.6
    public static final int VERSION_3            = 4;                // PGP3 - PGP5.0

    /* Note that there is no global Maximum version; this may vary from packet type to packet type */
    public static final int DEFAULT_VERSION      = 3;                // PGP2

    public static final int ARM_NONE             = 0;  // No armouring or no armoured block found
    public static final int ARM_PGP_PLAIN        = 1;  // Not armoured but a PGP plain-signed block
    public static final int ARM_PGP              = 2;  // PGP-classic armouring
    public static final int ARM_PGP_MULTI        = 3;  // concatenated multi-part armour
    public static final int ARM_MIME             = 4;  
    public static final int ARM_UUENCODE         = 5;
    public static final int ARM_BINHEX           = 6;

    // will be native call eventually
    public native static boolean isIDEAenabled();
}
