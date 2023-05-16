/*
	Pretty Good(tm) Privacy - RSA public key cryptography for the masses
	Written by Philip Zimmermann, Phil's Pretty Good(tm) Software.
	Version 1.0 - 5 Jun 91, last revised 6 Jul 91 by PRZ

	This file defines the various formats, filenames, and general control
	methods used by PGP, as well as a few global switches which control
	the functioning of the driver code.

** CTClib mods Mr. Tines 23-Feb-98

*/
#ifndef _pgp
#define _pgp
#include "usuals.h"
#include "more.h"
#include "armour.h"

#define KEYFRAGSIZE 8	/* # of bytes in key ID modulus fragment */
#define SIZEOF_TIMESTAMP 4 /* 32-bit timestamp */

/* The maximum length of the file path for this system.  Varies on UNIX
   systems */

#ifndef	MAX_PATH
#ifdef MSDOS
#define MAX_PATH	64
#else
#define MAX_PATH	256
#endif
#endif

#ifdef ATARI
#define sizeof(x) (int)sizeof(x)
#define fread(a,b,c,d)	((int)fread(a,b,c,d))
#endif

/*
**********************************************************************
*/

#include "keyconst.h"

/* Cipher Type Byte (CTB) definitions follow...*/
#define is_ctb(c) (((c) & CTB_DESIGNATOR)==CTB_DESIGNATOR)

/* "length of length" field of packet, in bytes (1, 2, 4, 8 bytes): */
#define ctb_llength(ctb) ((int) 1 << (int) ((ctb) & CTB_LLEN_MASK))

#define is_ctb_type(ctb,type) (((ctb) & CTB_TYPE_MASK)==(4*type))
#define CTB_BYTE(type,llen) (CTB_DESIGNATOR + (4*type) + llen)

#define CTB_PKE_TYPE 			CTB_PKE				/* packet encrypted with RSA public key */
#define CTB_SKE_TYPE 			CTB_SKE				/* packet signed with RSA secret key */
#define CTB_CERT_SECKEY_TYPE 	CTB_CERT_SECKEY 	/* secret key certificate */
#define CTB_CERT_PUBKEY_TYPE 	CTB_CERT_PUBKEY	/* public key certificate */
#define CTB_COMPRESSED_TYPE 	CTB_COMPRESSED		/* compressed data packet */
#define CTB_CKE_TYPE 			CTB_CKE				/* conventional-key-encrypted data */
#define CTB_LITERAL_TYPE 		CTB_LITERAL			/* raw data with filename and mode */
#define CTB_LITERAL2_TYPE 		CTB_LITERAL2		/* Fixed literal packet */
#define CTB_KEYCTRL_TYPE 		CTB_KEYCTRL			/* key control packet */
#define CTB_USERID_TYPE 		CTB_USERID			/* user id packet */
#define CTB_COMMENT_TYPE 		CTB_OLD_COMMENT	/* comment packet */

/* Signature classification bytes. */
#define SB_SIGNATURE_BYTE		SIG_BINARY			/* Signature of a binary msg or doc */
#define SM_SIGNATURE_BYTE		SIG_TEXT				/* Signature of canonical msg or doc */
#define K0_SIGNATURE_BYTE		SIG_KEY_CERT		/* Key certification, generic */
#define K1_SIGNATURE_BYTE		SIG_KEY_PERSONA	/* Key certification, persona */
#define K2_SIGNATURE_BYTE		SIG_KEY_CASUAL		/* Key certification, casual ID */
#define K3_SIGNATURE_BYTE		SIG_KEY_POSITIVE	/* Key certification, positive ID */
#define KC_SIGNATURE_BYTE		SIG_KEY_COMPROM	/* Key compromise */
#define KR_SIGNATURE_BYTE		SIG_KEY_REVOKE		/* Key revocation */
#define TS_SIGNATURE_BYTE		SIG_KEY_TIMESTAMP	/* Timestamp someone else's signature */

/* Public key encrypted data classification bytes. */
#define MD_ENCRYPTED_BYTE	1	/* Message digest is encrypted */
#define CK_ENCRYPTED_BYTE	2	/* Conventional key is encrypted */

/* Version byte for data structures created by this version of PGP */
#define	VERSION_BYTE_MIN		MIN_VERSION			/* PGP2 to 2.5 */
#define	VERSION_BYTE_MAX		VERSION_3			/* PGP2.6 */
#define	VERSION_BYTE_DEFAULT	DEFAULT_VERSION	/* PGP2 */

/* Max length of ASCII version string in armored output */
#define MAX_VERSION_LENGTH	20

#define is_secret_key(ctb) is_ctb_type(ctb,CTB_CERT_SECKEY_TYPE)

#define MPILEN (2+MAX_BYTE_PRECISION)
#define MAX_SIGCERT_LENGTH (1+2+1 +1+7 +KEYFRAGSIZE+2+2+MPILEN)
#define MAX_KEYCERT_LENGTH (1+2+1+4+2+1 +(2*MPILEN) +1+8 +(4*MPILEN) +2)

/* Modes for CTB_LITERAL2 packet */
#define MODE_BINARY	'b'
#define MODE_TEXT	't'
#define MODE_LOCAL	'l'

/* Define CANONICAL_TEXT for any system which normally uses CRLF's
   for text separators */
#ifdef MSDOS
#define	CANONICAL_TEXT
#endif /* MSDOS */

/* Prototype for the 'more' function, which blorts a file to the screen with
   page breaks, intelligent handling of line terminators, truncation of
   overly long lines, and zapping of illegal chars.  Implemented in MORE.C */

int more_file(DataFileP text);

/* Prototypes for the transport armor routines */

boolean is_armor_file(char *infile, long startline);
int armor_file(char *infile, char *outfile, char *filename, char *clearname);
int de_armor_file(char *infile, char *outfile, long *curline);

void user_error(void);

/* Global filenames and system-wide file extensions... */
extern char PGP_EXTENSION[];
extern char ASC_EXTENSION[];
extern char SIG_EXTENSION[];
extern char BAK_EXTENSION[];
extern char CONSOLE_FILENAME[];
extern char rel_version[];

/* These files use the environmental variable PGPPATH as a default path: */
extern char PUBLIC_KEYRING_FILENAME[32];
extern char SECRET_KEYRING_FILENAME[32];
extern char RANDSEED_FILENAME[32];

/* Variables which are global across the driver code */
extern boolean	filter_mode;
extern boolean	moreflag;
extern FILE	*pgpout;	/* FILE structure for routine output */

/* Variables settable by config.pgp and referenced in config.c ... */
extern char language[];	/* foreign language prefix code for language.pgp file */
extern char charset[];
/* my_name is substring of default userid for secret key to make signatures */
extern char my_name[];
extern char floppyring[]; /* for comparing secret keys with backup on floppy */
extern char literal_mode;	/* text or binary mode for literal packet */
extern boolean emit_radix_64;
extern boolean showpass;
extern boolean keepctx;
extern boolean verbose;	/* display maximum information */
extern boolean compress_enabled;	/* attempt compression before encryption */
extern boolean clear_signatures;
extern boolean encrypt_to_self; /* Should I encrypt to myself? */
extern boolean batchmode;	/* for batch processing */
extern boolean quietmode;	/* less verbose */
extern boolean force_flag;	/* overwrite existing file without asking */
extern boolean pkcs_compat;	/* Use PKCS format messages */
/* Ask for each key separately if it should be added to the keyring */
extern boolean interactive_add;
extern long timeshift;	/* seconds from GMT timezone */
extern boolean signature_checked;
extern int pem_lines;
extern int marg_min;	/* number of marginally trusted signatures needed to
						   make a key fully-legit */
extern int compl_min;	/* number of fully trusted signatures needed */
extern int max_cert_depth;
extern char pager[];	/* file lister command */
extern char armor_version[MAX_VERSION_LENGTH];	/* version text in armor output */
extern int version_byte;	/* PGP packet format version */

/* These lists store hashed passwords for future use. */
/* passwds are passwords of as-yet-unknown purpose; keypasswds
   are passwords used to decrypt keys. */
struct hashedpw {
	struct hashedpw *next;
	byte hash[16];
};
extern struct hashedpw *keypasswds, *passwds;

extern boolean strip_spaces;

#ifdef VMS
/*
 * FDL Support Prototypes, Currently Used Only In SYSTEM.C and CRYPTO.C
 */

int fdl_generate(char *in_file, char **fdl, short *len);
VOID *fdl_create( char *fdl, short len, char *outfile, char *preserved_name);
int fdl_copyfile2bin(FILE *f, VOID *rab, word32 longcount); 
void fdl_close( VOID *rab);
#endif /* VMS */

extern int compressSignature(const byte *header);
/* CTC */
extern byte convalg;
extern byte convmode;
extern byte mdalg;
extern byte cpalg;
extern byte pkalg;


#endif /*ndef _pgp*/




