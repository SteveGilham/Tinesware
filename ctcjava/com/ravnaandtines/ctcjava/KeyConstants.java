
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

public class KeyConstants {
    public static final int MAXBUFFERSIZE = 2048;
    public static final int INTERNAL = 0;
    public static final int EXTERNAL = 1;
    public static final int CORRUPT  = 2;

    /* Cipher Type Byte (CTB) fields */
    public enum CTB
    {
        DESIGNATOR(0x80),    /* Version PGP 2.x style CTB */
        PGP3(0xc0),          /* Version PGP 3 style CTB */
        DESG_MASK(0xc0),
        TYPE_MASK(0x3c),
        TYPE_MSK3(0x3f),
        LLEN_MASK(0x03),
        

        PKE(1),             // packet encrypted with RSA public key
        SKE(2),             // packet signed with RSA secret key
        ESK(3),             // PGP3: new packet type
        ONEPASS_SIG(4),     // PGP3: new packet type
        CERT_SECKEY(5),     // secret key certificate
        CERT_PUBKEY(6),     // public key certificate
        SEC_SUBKEY(7),      // PGP3: new packet type
        COMPRESSED(8),      // compressed data packet
        CTB_CKE(9),         // conventional-key-encrypted data
        LITERAL(10),        // raw data with filename and mode
        LITERAL2(11),       // Fixed literal packet
        KEYCTRL(12),        // key control packet
        USERID(13),         // user id packet
        PUB_SUBKEY(14),     // PGP3: new packet type
                            // N.B. THIS IS AN INCOMPATIBLE CHANGE AT PGP5.0
                            //  THIS VALUE WAS PREVIOUSLY A COMMENT PACKET.
        OLD_COMMENT(14),    // PGP2.6 comment packet; now superceded
        COMMENT(16);

        private byte b;
        CTB(int b) {this.b = (byte)(b&0xff);}
        byte value() {return b;}
    }
    
    
    public enum SUBPKT
    {
        VERSION(1),
        CREATION(2),
        EXPIRY(3),
        KEY_CAPABILITIES(8),
        KEY_RECOVERY_KEY(9),
        KEY_PREFERRED_ALGS(10),
        KEYID(16),
        USERID(17),
        URL(18),
        FINGER(19);
        
        private int i;
        SUBPKT(int i) {this.i = i;}
        int value() {return i;}
    }

    public enum SIG
    {
        BINARY((byte)0x00),
        TEXT((byte)0x01),
        KEY_CERT((byte)0x10),
        KEY_PERSONA((byte)0x11),
        KEY_CASUAL((byte)0x12),
        KEY_POSITIVE((byte)0x13),
        SUBKEY_CERT((byte)0x18),
        SUBKEY_REVOKE((byte)0x28),
        KEY_COMPROM((byte)0x20),
        KEY_REVOKE((byte)0x30),
        KEY_TIMESTMP((byte)0x40);

        private byte b;
        SIG(byte b) {this.b = b;}
        byte value() {return b;}
    }
        
    /* Trust Byte fields */
    /* KTB = Key Trust Byte */
    public enum KTB 
    {
        OWN_MASK((byte)0x07),
        OWN_UNDEFINED((byte)0x00),
        OWN_UNKNOWN((byte)0x01),
        OWN_UNTRUSTED((byte)0x02),
        OWN_USUALLY((byte)0x05),
        OWN_TRUSTED((byte)0x06),
        OWN_OWNKEY((byte)0x07),
        ENABLE_MASK((byte)0x20),
        ENABLE_ENABLE((byte)0x00),
        ENABLE_DISABLE(ENABLE_MASK.value()),
        BUCKSTOP_MASK((byte)0x80),
        BUCKSTOP_TRUE(BUCKSTOP_MASK.value()),
        BUCKSTOP_FALSE((byte)0x00);
        
        private byte b;
        KTB(byte b) {this.b = b;}
        byte value() {return b;}
    }

        
    /* Public key encryption algorithm selector bytes. */
    /*  This is probably wrong.  There should probably be separate selectors
     **  for key exchange and authentication algorithm (many cannot be used for both) */     
    /* perhaps should have PKE_<alg> and PKS_<alg> for encrypt and sign */
    
    public enum PKA 
    {
        FLEX_FLAG((byte)0x80),

        RSA((byte)1),                       /* use RSA */
        RSA_ENCRYPT_ONLY((byte)2),
        RSA_SIGN_ONLY((byte)3),
        ELGAMAL((byte)16),                  // PGP5 code for ElGamal/Diffie-Hellman
        DSA((byte)17),                      // PGP5 code for DSA
        GF2255((byte)(1|FLEX_FLAG.value())),    // use elliptic curve on GF(2^255) (Deprecated)
        EBP_RSA((byte)86),                  // EBP also uses this value
        EBP_RABIN((byte)97);                // EBP uses 97 to indicate Rabin
        
        private byte b;
        PKA(byte b) {this.b = b;}
        byte value() {return b;}                
    }

    public static PKA PKAfromByte(byte b)
    {
        for (PKA pka : PKA.values())
        {
            if(pka.value() == b)
                return pka;
        }
        return null;
    }
    

    /* Conventional encryption algorithm selector bytes. */
    public enum CEA {
        FLEX_FLAG((byte)0x80),      // Not a PGP-classic imitator
        MORE_FLAG((byte)64),        // Another cipher key follows
        MASK((byte)0xAF),           // removes the "more" flag

        IDEA((byte)1),              // use  the IDEA cipher
        TRIPLEDES((byte)2),         // PGP5 code for Triple-DES
                                    // note that we cannot use CEM flags with this
        CAST5((byte)3),             // PGP5 code for CAST

    /* OpenPGP algorithm values - common with GPG */
        GPG_BLOW16((byte)4),

    /* Open PGP values - given priority over GPG in this range */
    /* they omit the silly GPG ROT-N */
        OPGP_SAFERSK128((byte)5),
        OPGP_DES_SK((byte)6),
        OPGP_AES_128((byte)7),
        OPGP_AES_192((byte)8),
        OPGP_AES_256((byte)9),
        OPGP_TWOFISH_256((byte)10), // according to Jon Callas <jon@pgp.com>

        GPG_BLOW20((byte)42),
        GPG_GOST((byte)43),

        EBP_IDEA((byte)86),                         // EBP uses this value also
        EBP_SAFER_MIN((byte)97),                    // EBP has 4 varieties of Safer
        EBP_SAFER_MAX((byte)100),
        NONE((byte)(0|FLEX_FLAG.value())),              // explicit no encryption
                                                    // "more" flag must not be set
        IDEAFLEX((byte)(IDEA.value()|FLEX_FLAG.value())),  // systematic IDEA
        THREEWAY((byte)(2|FLEX_FLAG.value())),          //  use the 3-way cipher
        BLOW16((byte)(3|FLEX_FLAG.value())),            // Blowfish with 16 byte key
        TEA((byte)(4|FLEX_FLAG.value())),               // Tiny Encryption Algorithm
        BLOW5((byte)(5|FLEX_FLAG.value())),             // Blowfish with 40-bit key
        SQUARE((byte)(6|FLEX_FLAG.value())),            // Square 128 bit key and block
        DES((byte)(7|FLEX_FLAG.value())),               // Single DES
        S3DES((byte)(8|FLEX_FLAG.value())),             // s3DES
        KDDES((byte)(9|FLEX_FLAG.value())),             // key dependent DES
        TRIPLEDESFLEX((byte)(10|FLEX_FLAG.value())),
        CAST5FLEX((byte)(11|FLEX_FLAG.value())),
        ESCAPE((byte)(0|MORE_FLAG.value()|FLEX_FLAG.value()));
                                                 // other data is present if only CEA_MORE_FLAG set
        private byte b;
        CEA(byte b) {this.b = b;}
        byte value() {return b;}                        
    }

    /* Conventional encryption mode selector bytes */
    public enum CEM 
    {
        REVERSE_FLAG((byte)0x80),      // work from the end of the file
        TRIPLE_FLAG((byte)64),              // three keys follow for outer chaining
        MASK((byte)63),              // as above

        PLAINTEXT((byte)0),               // CFB mode - assumed for PGP-classic
        CFB((byte)1),               // CFB mode - assumed for PGP-classic
        ECB((byte)2),               // ECB mode - with ciphertext stealing
        OFB((byte)3),               // OFB mode
        CBC((byte)4);               // CFB mode - with ciphertext stealing
                                                 // other data is present if only CEA_MORE_FLAG set
        private byte b;
        CEM(byte b) {this.b = b;}
        byte value() {return b;}                        
    }

    /* Message digest algorithm selector bytes. */
    public enum MDA
    {
        FLEX_FLAG((byte)0x80),                  // as CEA_FLEX_FLAG
        MASK((byte)127),                        // as CEA_MASK

        MD5((byte)1),                           // MD5 message digest algorithm
        PGP5_SHA1((byte)2),                     // PGP5 code for SHA-1
        PGP5_RIPEM160((byte)3),                 // PGP5 code for RIPEM 160
        EBP_HAVAL_MIN((byte)97),                // EBP uses this range for 15 varieties of HAVAL
        EBP_HAVAL_MAX((byte)111),
        THREEWAY((byte)(2|FLEX_FLAG.value())),  // 3-way used to produce 96 bit hash
        SHA((byte)(3|FLEX_FLAG.value())),       // the NIST SHA 160bit hash
        SHA1((byte)(4|FLEX_FLAG.value()));      // SHA-1 modification
                                                // other data is present if only CEA_MORE_FLAG set
        private byte b;
        MDA(byte b) {this.b = b;}
        byte value() {return b;}                        
    }

    /* Data compression algorithm selector bytes. */
    public enum CPA 
    {
        FLEX_FLAG((byte)0x80),

        NONE((byte)0),                          // do no compression
        DEFLATE((byte)1),                       // Zip-based deflate compression algorithm
        SPLAY((byte)(1|FLEX_FLAG.value()));   // Splay tree based ditto
                                                // other data is present if only CEA_MORE_FLAG set
        private byte b;
        CPA(byte b) {this.b = b;}
        byte value() {return b;}                        
    }

    /* Version byte for data structures created by this version of PGP */
    public enum VERSION
    {
        MIN(2),             // PGP2 to 2.5
        PGP2_6(3),          // PGP2.6
        PGP3(4),            // PGP3 - PGP5.0

    /* Note that there is no global Maximum version; this may vary from packet type to packet type */
        DEFAULT(3);         // PGP2
        
        private int i;
        VERSION(int i) {this.i = i;}
        int value() {return i;}
    }

    public enum ARM 
    {           
        NONE((byte)0),          // No armouring or no armoured block found
        PGP_PLAIN((byte)1),     // Not armoured but a PGP plain-signed block
        PGP((byte)2),           // PGP-classic armouring
        PGP_MULTI((byte)3),     // concatenated multi-part armour
        MIME((byte)4),  
        UUENCODE((byte)5),
        BINHEX((byte)6);
                                                // other data is present if only CEA_MORE_FLAG set
        private byte b;
        ARM(byte b) {this.b = b;}
        byte value() {return b;}                        
    }

    public enum PRIME 
    {           
        SIMPLE((byte)1),     // Not armoured but a PGP plain-signed block
        JUMP((byte)2),           // PGP-classic armouring
        SOPHIE_GERMAIN((byte)3);     // concatenated multi-part armour
                                                // other data is present if only CEA_MORE_FLAG set
        private byte b;
        PRIME(byte b) {this.b = b;}
        byte value() {return b;}                        
    }
    
    
    // native call
    public native static boolean isIDEAenabled();
}
