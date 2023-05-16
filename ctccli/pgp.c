/* #define TEMP_VERSION	/ * if defined, temporary experimental version of PGP */
/*	pgp.c -- main module for PGP.

** This source code is drawn from the 2.63ui, unofficial version of PGP(tm)
** code stream, derived ultimately from the 2.3 version distributed under the
** GNU General Public Licence.  It is drawn upon for its user interface -
** how more easily to get a generally compatible one? - for a command line
** interface to CTClib 2.0

	PGP: Pretty Good(tm) Privacy - public key cryptography for the masses.

	Synopsis:  PGP uses public-key encryption to protect E-mail. 
	Communicate securely with people you've never met, with no secure
	channels needed for prior exchange of keys.  PGP is well featured and
	fast, with sophisticated key management, digital signatures, data
	compression, and good ergonomic design.

** CTClib is an independently implemented set of routine to generate and
** parse the PGP message format.  The CTClib code is also distributed under
** the terms of the GNU General Public Licence, and was developed by
** Ian Miller <ian_miller@bifroest.demon.co.uk> and Mr. Tines
** <tines@windsong.demon.co.uk>, with assistance from Robert Guerra (general
** PGP5 interoperation tests) and Richard Outerbridge (DES3 implementation).

	The original PGP version 1.0 was written by Philip Zimmermann, of
	Phil's Pretty Good(tm) Software.  Many parts of later versions of 
	PGP were developed by an international collaborative effort, 
	involving a number of contributors, including major efforts by:
		Branko Lankester <lankeste@fwi.uva.nl> 
		Hal Finney <74076.1041@compuserve.com>
		Peter Gutmann <pgut1@cs.aukuni.ac.nz>
	Other contributors who ported or translated or otherwise helped include:
		Jean-loup Gailly in France
		Hugh Kennedy in Germany
		Lutz Frank in Germany
		Cor Bosman in The Netherlands
		Felipe Rodriquez Svensson in The Netherlands
		Armando Ramos in Spain
		Miguel Angel Gallardo Ortiz in Spain
		Harry Bush and Maris Gabalins in Latvia
		Zygimantas Cepaitis in Lithuania
		Alexander Smishlajev
		Peter Suchkow and Andrew Chernov in Russia
		David Vincenzetti in Italy
		...and others.

	Note that while many PGP source modules bear the copyright notice of
	Philip Zimmermann, some of them may have been revised or in some
	cases entirely written by other members of the PGP development team,
	who often failed to put their names in their code.

	Revisions:
		Version 1.0 - 5 Jun 91
		Version 1.4 - 19 Jan 92
		Version 1.5 - 12 Feb 92
		Version 1.6 - 24 Feb 92
		Version 1.7 - 29 Mar 92
		Version 1.8 - 23 May 92
		Version 2.0 - 2 Sep 92
		Version 2.1 - 6 Dec 92
		Version 2.2 - 6 Mar 93
		Version 2.3 - 13 Jun 93
		Version 2.3a - 1 Jul 93
		Version 2.6ui - 25 May 93
**    =============
** CTClib revisions
**    Conversion begun 23-Feb-98 by Mr. Tines
**       -- disable but not delete passphrase input ab initio or command line
**    1-Aug-98 - minor multi-byte character tweaks

	Note: 2.6ui is an unofficial international release, based on 2.3a
	and put together by mathew <mathew@mantis.co.uk> for compatibility 
	with MIT PGP 2.6.

	(c) Copyright 1990-1993 by Philip Zimmermann.  All rights reserved.
	The author assumes no liability for damages resulting from the use
	of this software, even if the damage results from defects in this
	software.  No warranty is expressed or implied.

	All the source code Philip Zimmermann wrote for PGP is available for
	free under the "Copyleft" General Public License from the Free
	Software Foundation.  A copy of that license agreement is included in
	the source release package of PGP.  Code developed by others for PGP
	is also freely available.  Other code that has been incorporated into
	PGP from other sources was either originally published in the public
	domain or was used with permission from the various authors.  See the
	PGP User's Guide for more complete information about licensing,
	patent restrictions on certain algorithms, trademarks, copyrights,
	and export controls.  

	Philip Zimmermann may be reached at:
		Boulder Software Engineering
		3021 Eleventh Street
		Boulder, Colorado 80304  USA
		(303) 541-0140  (voice or FAX)
		email:  prz@sage.cgd.ucar.edu


	PGP will run on MSDOS, Sun Unix, VAX/VMS, Ultrix, Atari ST, 
	Commodore Amiga, and OS/2.  Note:  Don't try to do anything with 
	this source code without looking at the PGP User's Guide.

** CTClib development is being done on WIN32 (using Borland C++ 5.02
** as the 'C' compiler; I trust I won't leave too many "//" style
** comments about.  I will attempt not to damage the portability
** but can make no promises.

	PGP combines the convenience of the Rivest-Shamir-Adleman (RSA)
	public key cryptosystem with the speed of fast conventional
	cryptographic algorithms, fast message digest algorithms, data
	compression, and sophisticated key management.  And PGP performs 
	the RSA functions faster than most other software implementations.  
	PGP is RSA public key cryptography for the masses.

	Uses RSA Data Security, Inc. MD5 Message Digest Algorithm
	as a hash for signatures.  Uses the ZIP algorithm for compression.
	Uses the ETH IPES/IDEA algorithm for conventional encryption.

	PGP generally zeroes its used stack and memory areas before exiting.
	This avoids leaving sensitive information in RAM where other users
	could find it later.  The RSA library and keygen routines also
	sanitize their own stack areas.  This stack sanitizing has not been
	checked out under all the error exit conditions, when routines exit
	abnormally.  Also, we must find a way to clear the C I/O library
	file buffers, the disk buffers, and and cache buffers.

** This is also the case for CTClib; there are minor differences in the
** scope over which passphrases and decrypted secret keys persist.

	If you modify this code, PLEASE preserve the style of indentation
	used for {begin...end} blocks.  It drives me bats to have to deal
	with more than one style in the same program.

   --  The *lack of* disciplined formatting drove me bats. --Tines
   (who fixed it, and added a tad of structure)

 	Modified: 12-Nov-92 HAJK
 	Add FDL stuff for VAX/VMS local mode. 
*/
#ifdef __BORLANDC__
#define MSDOS
#include <dir.h>
#include <conio.h>
#endif

#include <stdlib.h>
#include <io.h>
#include <ctype.h>
#ifndef AMIGA
#include <signal.h>
#endif
#include <stdio.h>
#include <string.h>
#include <time.h>

#include "basic.h"
#include "hashpass.h"
//#include "system.h"
#include "port_io.h"
#include "fileio.h"
#include "language.h"
#include "pgp.h"
//#include "exitpgp.h"
#include "charset.h"
#include "getopt.h"
#include "config.h"
#include "licences.h"

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
#include "hashpass.h"
#include <locale.h>

static int handled = 0;
#define	CHECK_ALL	0	/* Check all signatures */
#define	MAINT_CHECK				0x01
#define	MAINT_VERBOSE			0x02

#ifdef  M_XENIX
char *strstr();
long time();
#endif

#ifdef MSDOS
#ifdef __ZTC__	/* Extend stack for Zortech C */
unsigned _stack_ = 24*1024;
#endif
#ifdef __TURBOC__
unsigned _stklen = 24*1024;
#endif
#endif
#define	STACK_WIPE	4096

/* Global filenames and system-wide file extensions... */
char rel_version[]                     = "1.0";             /* release version */
char armor_version[MAX_VERSION_LENGTH] = "CTCcli 1.0";      /* Version reported in armor files */
#ifdef RSAREF
#define RSASTRING " (with RSAREF)"
#else
#define RSASTRING ""
#endif
/*static char rel_date[]                 = "## Mmm 98";	     *//* release date */
char PGP_EXTENSION[]                   = ".pgp";
char ASC_EXTENSION[]                   = ".asc";
char SIG_EXTENSION[]                   = ".sig";
char BAK_EXTENSION[]                   = ".bak";
/*static char HLP_EXTENSION[]            = ".hlp";*/
char CONSOLE_FILENAME[]                = "_CONSOLE";
/*static char HELP_FILENAME[]            = "pgp.hlp";*/


static char CONFIG_FILENAME[]          = "config.txt";     /* These files use the */
char PUBLIC_KEYRING_FILENAME[32]       = "pubring.pgp";    /* environment variable */
char SECRET_KEYRING_FILENAME[32]       = "secring.pgp";    /* PGPPATH as a default path: */
char RANDSEED_FILENAME[32]             = "randseed.bin";

/* TRANSFERRED GLOBALS */
char floppyring[MAX_PATH]               = "";
int pem_lines                           = 720;
int marg_min                            = 2;	             /* number of marginally trusted signatures needed for
						                                           a fully legit key (can be set in config.pgp). */
int compl_min                           = 1;	             /* number of fully trusted signatures needed */
int max_cert_depth                      = 4;		          /* maximum nesting of signatures */
int version_byte                        = VERSION_BYTE_DEFAULT;	/* PGP packet format version */

/* Flags which are global across the driver code files */
boolean verbose                         = FALSE;	      /* -l option: display maximum information */
FILE	*pgpout;			                                    /* Place for routine messages */

static void usage(void);
static void key_usage(void);
static void arg_error(void);
static void initsigs(void);
static int do_keyopt(char);
/*static void do_armorfile(char *);*/


/*	Various compression signatures: PKZIP, Zoo, GIF, Arj, and HPACK.
	Lha(rc) is handled specially in the code; it is missing from the
	compressSig structure intentionally.  If more formats are added,
	put them before lharc to keep the code consistent.
*/
static char *compressSig[] =  { "PK\03\04", "ZOO ", "GIF8", "\352\140",
	"HPAK", "\037\213", "\037\235", "\032\013", "\032HP%"
	/* lharc is special, must be last */ };
static char *compressName[] = { "PKZIP",   "Zoo",  "GIF",  "Arj",
	"Hpack", "gzip", "compressed", "PAK", "Hyper",
	"LHarc" };
static char *compressExt[] =  { ".zip",  ".zoo",  ".gif",  ".arj",
	".hpk", ".z", ".Z", ".pak", ".hyp",
	".lzh" };

/* "\032\0??", "ARC", ".arc" */


/* Possible error exit codes - not all of these are used.  Note that we
   don't use the ANSI EXIT_SUCCESS and EXIT_FAILURE.  To make things
   easier for compilers which don't support enum we use #defines */

#define EXIT_OK					0
#define INVALID_FILE_ERROR		1
#define FILE_NOT_FOUND_ERROR	2
#define UNKNOWN_FILE_ERROR		3
#define NO_BATCH	            4
#define BAD_ARG_ERROR			5
#define INTERRUPT				   6
#define OUT_OF_MEM				7

/* Keyring errors: Base value = 10 */
#define KEYGEN_ERROR			   10
#define NONEXIST_KEY_ERROR		11
#define KEYRING_ADD_ERROR		12
#define KEYRING_EXTRACT_ERROR	13
#define KEYRING_EDIT_ERROR		14
#define KEYRING_VIEW_ERROR		15
#define KEYRING_REMOVE_ERROR	16
#define KEYRING_CHECK_ERROR	17
#define KEY_SIGNATURE_ERROR	18
#define KEYSIG_REMOVE_ERROR	19

/* Encode errors: Base value = 20 */
#define SIGNATURE_ERROR			20
#define RSA_ENCR_ERROR			21
#define ENCR_ERROR				22
#define COMPRESS_ERROR			23

/* Decode errors: Base value = 30 */
#define SIGNATURE_CHECK_ERROR	30
#define RSA_DECR_ERROR			31
#define DECR_ERROR				32
#define DECOMPRESS_ERROR		33

//===============================<<<

/*===========================================
* Routine get_header_info_from_file()
* Args
*         const char * infile        name of file to read from
*			 byte *       header        buffer to fill
*         long         count         bytes to move
* Return
*       	 bytes read
* Comment
*         Reads the first count bytes from infile into header
*/
long get_header_info_from_file(const char *infile,  byte *header, long count)
{
	DataFileP f;
	fill0(header,count);
	/* open file f for read, in binary (not text) mode...*/
	if ((f = vf_open(CONST_CAST(char*,infile),READ, BINPLAIN)) == (DataFileP)0)
   {
		return(-1);
   }
	count = vf_read(header,count,f);
	vf_close(f);
	return count;
}

/*===========================================
* Routine get_timestamp()
* Args
*         byte *       timestamp     stamp as byte array in local order
* Return
*       	 timestamp as longword
* Comment
	Note:  On MSDOS, the time() function calculates GMT as the local
	system time plus a built-in timezone correction, which defaults to
	adding 7 hours (PDT) in the summer, or 8 hours (PST) in the winter,
	assuming the center of the universe is on the US west coast. Really--
	I'm not making this up!  The only way to change this is by setting
	the MSDOS environmental variable TZ to reflect your local time zone,
	for example "set TZ=MST7MDT".  This means add 7 hours during standard
	time season, or 6 hours during daylight time season, and use MST and
	MDT for the two names of the time zone.  If you live in a place like
	Arizona with no daylight savings time, use "set TZ=MST7".  See the
	Microsoft C function tzset().  Just in case your local software
	environment is too weird to predict how to set environmental
	variables for this, PGP also uses its own TZFIX variable in
	config.pgp to optionally correct this problem further.  For example,
	set TZFIX=-1 in config.pgp if you live in Colorado and the TZ
	variable is undefined.
*/
uint32_t get_timestamp(byte *timestamp)
{
	uint32_t t;
	t = time(NULL);    /* returns seconds since GMT 00:00 1 Jan 1970 */

#ifdef _MSC_VER
#if (_MSC_VER == 700)
	/*  Under MSDOS and MSC 7.0, time() returns elapsed time since
	 *  GMT 00:00 31 Dec 1899, instead of Unix's base date of 1 Jan 1970.
	 *  So we must subtract 70 years worth of seconds to fix this.
	 *  6/19/92  rgb
	*/
#define	LEAP_DAYS	(((unsigned long)70L/4)+1)
#define CALENDAR_KLUDGE ((unsigned long)86400L * (((unsigned long)365L * 70L) + LEAP_DAYS))
   	t -= CALENDAR_KLUDGE;
#endif
#endif

	t += timeshift; /* timeshift derived from TZFIX in config.pgp */

	if (timestamp != (byte*)0)
	{
   	memcpy(timestamp, &t, sizeof(t));
	}

	return(t);	/* return 32-bit timestamp integer */
}	/* get_timestamp */

/*===========================================
* Routine date_ymd()
* Args
*         const uint32_t *   tstamp        timestamp
*			 int  *       year
*			 int  *       month
*			 int  *       day
* Return
*       	 Day of week 0-6
* Comment
*	Given timestamp as seconds elapsed since 1970 Jan 1 00:00:00,
	returns year (1970-2106), month (1-12), day (1-31).
	Not valid for dates after 2100 Feb 28 (no leap day that year).
   (but the 2**32sec since 1900 & 2**31 since 1970 horizons come first)
	Also returns day of week (0-6) as functional return.
*/
static int date_ymd(const uint32_t *tstamp, int *year, int *month, int *day)
{
	uint32_t days,y;
	int m,d,i;
	static short mdays[12] = {31,28,31,30,31,30,31,31,30,31,30,31};
	days = (*tstamp)/(unsigned long)86400L;	/* day 0 is 1970/1/1 */
	days -= 730L;	                           /* align days relative to 1st leap year, 1972 */
	y = ((days*4)/(unsigned long)1461L);	   /* 1972 is year 0 */
	d = (int) (days - ((y/4)*1461L));         /* reduce to days elapsed since 1/1 last leap year: */
	*year = (int)(y+1972);
	for (i=0; i<48; i++)	                     /* count months 0-47 */
	{	m = i % 12;
		d -= mdays[m] + (i==1);	               /* i==1 is the only leap month */
		if (d < 0)
		{	d += mdays[m] + (i==1);
			break;
		}
	}
	*month = m+1;
	*day = d+1;
	i = (int)((days-2) % (unsigned long)7L);	/* compute day of week 0-6 */
	return(i);	/* returns weekday 0-6; 0=Sunday, 6=Saturday */
}

/*===========================================
* Routine cdate()
* Args
*         const uint32_t *   tstamp        timestamp
* Return
*       	 Return date string
* Comment
*/
char *cdate(const uint32_t *tstamp)

{
	int month,day,year;
	static char datebuf[20];
	if (*tstamp == 0)
		return("          ");
	(void) date_ymd(tstamp,&year,&month,&day);
	sprintf(datebuf,"%4d/%02d/%02d", year, month, day);
	return (datebuf);
}


/*===========================================
* Routine ctdate()
* Args
*         const uint32_t *   tstamp        timestamp
* Return
*       	 Return date and tine string
* Comment
*/
char *ctdate(uint32_t *tstamp)
/*	Return date and time string, given pointer to 32-bit timestamp */
{	int hours,minutes;
	static char tdatebuf[40];
	long seconds;
	seconds = (*tstamp) % (unsigned long)86400L;	/* seconds past midnight today */
	minutes = (int)((seconds+30L) / 60L);	      /* round off to minutes past midnight */
	hours = minutes / 60;			               /* hours past midnight */
	minutes = minutes % 60;			              /* minutes past the hour */
	sprintf(tdatebuf,"%s %02d:%02d GMT", cdate(tstamp), hours, minutes);
	return (tdatebuf);
}

/*===========================================
* Routine legal_ctb()
* Args
*         byte              ctb           alleged Cipher type byte
* Return
*       	 Return date and tine string
* Comment
*         Used to determine if nesting should be allowed.
*/
boolean legal_ctb(byte ctb)
{	/*  */
	boolean legal;
	byte ctbtype;
	if (!is_ctb(ctb))		/* not even a bonafide CTB */
		return(FALSE);
	/* Sure hope CTB internal bit definitions don't change... */
	ctbtype = (byte)((ctb & CTB_TYPE_MASK) >> 2);
	/* Only allow these CTB types to be nested... */
	legal = (boolean)(
			(ctbtype==CTB_PKE_TYPE)
		||	(ctbtype==CTB_SKE_TYPE)
		||	(ctbtype==CTB_CERT_SECKEY_TYPE)
		||	(ctbtype==CTB_CERT_PUBKEY_TYPE)
		||	(ctbtype==CTB_LITERAL_TYPE)
		||	(ctbtype==CTB_LITERAL2_TYPE)
		||	(ctbtype==CTB_COMPRESSED_TYPE)
		||  (ctbtype==CTB_CKE_TYPE)
		 );
	return(legal);
}

/*===========================================
* Routine compressSignature()
* Args
*         const byte *       header        starting bytes of a file
* Return
*       	 Returns file signature type from a number of popular compression formats
*  or -1 if no match
* Comment
*         Used to determine if file should be compressed again.
*/
int compressSignature(const byte *header)
/* Returns file signature type from a number of popular compression formats
   or -1 if no match */
{
	int i;
	for (i=0; i<sizeof(compressSig)/sizeof(*compressSig); i++)
		if (!strncmp((char *)header, compressSig[i], strlen(compressSig[i])))
			return(i);
	/* Special check for lharc files */
	if (header[2]=='-' && header[3]=='l' && (header[4]=='z'||header[4]=='h') &&
		header[6]=='-')
		return(i);
	return(-1);
}

/*===========================================
* Routine file_compressiblee()
* Args
*         const char *       filename      file to inspect
* Return
*       	 Returns TRUE iff file is likely to be compressible
* Comment
*         Used to determine if file should be compressed again.
*/
static boolean file_compressible(const char *filename)
{
	byte header[8];
	get_header_info_from_file( filename, header, 8 );
	if (compressSignature( header ) >= 0)
		return (FALSE);	/* probably not compressible */
	return (TRUE);	/* possibly compressible */
}

/*===========================================
* Routine breakHandler()
* Args
*         vary by platform
* Return
*       	 none
* Comment
*         This function is called if a BREAK signal is sent to the program.  In this
*   case we notify user_break()
*/
#ifdef SIGINT
void breakHandler(
#ifdef UNIX
		int sig
#else
		void
#endif  /*UNIX*/
)
{
#ifdef UNIX
	if (sig == SIGPIPE)
	{	signal(SIGPIPE, SIG_IGN);
		exitPGP(INTERRUPT);
	}
	if (sig != SIGINT)
		fprintf(stderr, "\nreceived signal %d\n", sig);
	else
#endif /* UNIX */
   fprintf(pgpout,PSTR("\nStopped at user request\n"));
	/*exitPGP(INTERRUPT);*/
   handled = TRUE;
}
#endif  /* SIGINT */

/*===========================================
* Routine clearscreen()
* Args
*         none
* Return
*       	 none
* Comment
*         Clears screen and homes the cursor.
*/
static void clearscreen(void)
{	/* Clears screen and homes the cursor. */
	fprintf(pgpout,"\n\033[0;0H\033[J\r           \r");	/* ANSI sequence. */
	fflush(pgpout);
}

/*===========================================
* Routine signon_msg()
* Args
*         none
* Return
*       	 none
* Comment
*         Outputs simple banner.
*         We had to process the config file first to possibly select the
		foreign language to translate the sign-on line that follows...
*/
static void signon_msg(void)
{
	uint32_t tstamp;
	static boolean printed = FALSE; /* display message only once to allow calling multiple times */

	if (quietmode || printed) return;
	printed = TRUE;
	fprintf(stderr,PSTR("CTCcli %s%s - Free World Freeware encryption.\n"),
		rel_version, RSASTRING);
#ifdef TEMP_VERSION
	fprintf(stderr, "Internal development version only - not for general release.\n");
#endif
	fprintf(stderr, PSTR("CTClib copyright Mr. Tines & Ian Miller 15 feb 1998\n"));
	fprintf(stderr, PSTR("Parts (c) 1990-1993 Philip Zimmermann, Phil's Pretty Good Software.\n"));

	get_timestamp((byte *)&tstamp);	/* timestamp points to tstamp */
	fprintf(pgpout,"Date: %s\n",ctdate(&tstamp));
}


#ifdef TEMP_VERSION	                               /* temporary experimental version of PGP */
#include <time.h>
#define CREATION_DATE ((unsigned long) 0x2C18C6BCL) /* CREATION_DATE is Fri Jun 11 17:54:04 1993 UTC */
#define LIFESPAN	((unsigned long) 30L * (unsigned long) 86400L) 	/* LIFESPAN is 30 days */

/*===========================================
* Routine check_expiration_date()
* Args
*         none
* Return
*       	 none
* Comment
*         If this is an experimental version of PGP, cut its life short
*/
void check_expiration_date(void)
{	/* If this is an experimental version of PGP, cut its life short */
	word32 t;
	t = time(NULL);
	if (t > (CREATION_DATE + LIFESPAN))
	{	fprintf(stderr,"\n\007This experimental version of CTC has expired.\n");
		exit(-1);	/* error exit */
	}
}
#else	                                               /* no expiration date */
#define check_expiration_date()                      /* null statement */
#endif	/* def TEMP_VERSION */

//===============================<<<
/* Used by getopt function... */
/* -f means act as a unix-style filter */
/* -i means internalize extended file attribute information, only supported
 *          between like (or very compatible) operating systems. */
/* -l means show longer more descriptive diagnostic messages */
/* -m means display plaintext output on screen, like unix "more" */
/* -d means decrypt only, leaving inner signature wrapping intact */
/* -t means treat as pure text and convert to canonical text format */
#define OPTIONS "abcdefghiklmo:prstu:vwxz:ABCDEFGHIKLMO:PRSTU:VWX?"
extern int optind;
extern char *optarg;

/* GLOBALS - yuk! */
boolean emit_radix_64                            = FALSE;		/* set by config file */
static boolean sign_flag                         = FALSE;
boolean moreflag                                 = FALSE;
boolean filter_mode                              = FALSE;
static boolean preserve_filename                 = FALSE;
static boolean decrypt_only_flag                 = FALSE;
static boolean de_armor_only                     = FALSE;
static boolean strip_sig_flag                    = FALSE;
boolean clear_signatures                         = FALSE;
boolean strip_spaces;
static boolean c_flag                            = FALSE;
boolean encrypt_to_self                          = FALSE;     /* should I encrypt messages to myself? */
boolean batchmode                                = FALSE;     /* if TRUE: don't ask questions */
boolean quietmode                                = FALSE;
boolean force_flag                               = FALSE;	  /* overwrite existing file without asking */
boolean pkcs_compat                              = 1;
#ifdef VMS	/* kludge for those stupid VMS variable-length text records */
char literal_mode                                = MODE_TEXT; /* MODE_TEXT or MODE_BINARY for literal packet */
#else	/* not VMS */
char literal_mode                                = MODE_BINARY;/* MODE_TEXT or MODE_BINARY for literal packet */
#endif	/* not VMS */
char my_name[256]                                = "\0";      /* substring of default userid for signing key; null means take first userid in ring */
boolean keepctx                                  = FALSE;	  /* TRUE means keep .ctx file on decrypt */
boolean interactive_add                          = FALSE;     /* Ask for each key separately if it should be added to the keyring */
boolean compress_enabled                         = TRUE;	     /* attempt compression before encryption */
long timeshift                                   = 0L;	     /* seconds from GMT timezone */
static boolean attempt_compression;                           /* attempt compression before encryption */
static char *outputfile                          = NULL;
static int errorLvl                              = EXIT_OK;
static char mcguffin[256];                                    /* userid search tag */
boolean signature_checked                        = FALSE;
char plainfile[MAX_PATH];
int myArgc                                       = 2;
char **myArgv;
#if 0
struct hashedpw *passwds                         = 0,
                *keypasswds                      = 0;
static struct hashedpw **passwdstail             = &passwds;
#endif

#ifdef NO_IDEA
byte convalg                                     = CEA_CAST5;
#else
byte convalg                                     = CEA_IDEA;
#endif
byte convmode                                    = 0; /* PRZ stuttering CFB */
byte mdalg                                       = MDA_MD5;
byte cpalg                                       = CPA_DEFLATE;
byte pkalg                                       = PKA_RSA;

static decode_context context;
// data cache
#define CACHESIZE 1024
static int	bits= 0x00;
static int 	nbits = 0;
static byte 	cache[CACHESIZE];
static int 	ncache = 0;
static int 	ocache;
#include "md5.h"
static byte hashbuf[MD5HASHSIZE];
static int buffoff = 0;
static MD5_CTX MD5context;
static MD5_CTX* pcontext = &MD5context;

/*===========================================
* Routine exitPGP()
* Args
*         int          returnval     exit status to signal
* Return
*       	 none
* Comment
*         wipes and removes temporary files, also tries to wipe the stack
*/
void exitPGP(int returnval)
{
	char buf[STACK_WIPE];
/*	struct hashedpw *hpw;*/
   if(context.keyRings) destroy_userhash(context.keyRings);

	if (verbose)
		fprintf(pgpout, "exitPGP: exitcode = %d\n", returnval);
#if 0
	for (hpw = passwds; hpw; hpw = hpw->next)
		memset(hpw->hash, 0, sizeof(hpw->hash));
	for (hpw = keypasswds; hpw; hpw = hpw->next)
		memset(hpw->hash, 0, sizeof(hpw->hash));
#endif
	cleanup_tmpf();
#if defined(DEBUG) && defined(linux)
	if (verbose)
	{	struct mstats mstat;
		mstat = mstats();
		printf("%d chunks used (%d bytes)  %d bytes total\n",
			mstat.chunks_used, mstat.bytes_used, mstat.bytes_total);
	}
#endif
	memset(buf, 0, sizeof(buf));	/* wipe stack */
   memset(cache, 0, CACHESIZE);  /* wipe random stuff */
   memset(hashbuf, 0, MD5HASHSIZE);
   memset(pcontext, 0, sizeof(MD5_CTX));
#ifdef VMS
/*
 * Fake VMS style error returns with severity in bottom 3 bits
 */
	if (returnval)
	    returnval = (returnval << 3) | 0x10000002;
	else
	    returnval = 0x10000001;
#endif /* VMS */
	exit(returnval);
}
/*=========================================================================*/

/*                            CTC UTILITIES                                */

/*=========================================================================*/

/*===========================================
* Routine key_from_name()
* Args
*         hashtable *  table         key ring hash
*         char      *  name          user ID tag to match
*         pubkey    *  matchs[]      array of pubkey* to fill
*         int          maxMatchs     free size of array
* Return
*       	 none
* Comment
*         Accumulates public keys based on user ID
*TODO//   Allow for keyID as well as name
*/
static int key_from_name(hashtable * table, char * name,
				  pubkey * matchs[], int maxMatchs)
{
	username * nameRec;
	char 	keyName[256];
	int 	result = 0;
	pubkey * candidate;

	for(candidate = firstPubKey(table);
       candidate;
       candidate = nextPubKey(candidate))
	{
      // skip disabled keys
   	if(ownTrust(candidate) &  KTB_ENABLE_MASK) continue;

		for(nameRec = firstName(candidate); nameRec; nameRec=nextName(nameRec))
		{
			text_from_name(table, nameRec, keyName);
			strlwr(keyName);
			if(strstr(keyName, name))
			{
				matchs[result++] = candidate;
				if(result >= maxMatchs) return result;
			}
		}
	}
	return result;
}

/*===========================================
* Routine makeSessionData()
* Args
*         const char * file          input source of entropy
* Return
*       	 none
* Comment
*         hashes the file into the session data buffer
*/
#define BUFFERSIZE 1024
static byte sessionData[MAXHASHSIZE];
static int  sessionLength = 0;
static void makeSessionData(const char * file)
{
	DataFileP source = vf_open(CONST_CAST(char*, file), READ, BINPLAIN);
   md_context hash;
   byte buffer[BUFFERSIZE], type;

   long length;
   time_t t;

   if(!source)
   {
      printf("Cannot open data file.  Exiting\n");
      abort();
   }

   length = vf_length(source);
   if(length <= 160) /* assume ~1 bit entropy per byte */
   {
      type = MDA_MD5;
   }
   else if (length <= 256)
   {
   	type = MDA_SHA1;
   }
   else type = MDA_HAVAL_MIN;

   hash = hashInit(type);
	while((length = vf_read(buffer, BUFFERSIZE, source)) > 0)
	{
		hashUpdate(hash, buffer, (uint32_t)length);
	}
   time(&t);
   strcpy((char*)buffer,  ctime(&t));

	hashUpdate(hash, buffer, (uint32_t) strlen((char*)buffer) );
	hashFinal(&hash, sessionData);
   sessionLength = hashDigest(type);
   vf_close(source);
}

/*===========================================
* Routine getPassphrase()
* Args
*         char[256]    password      buffer to receive user input
*         boolean      echo          if the test is seen on the screen
* Return
*       	 length of data
* Comment
*         Reads general text from the command line
*/
#define CTLC 0x03
#define BEL 0x07
#define BS 0x08
#define LF 0x0A
#define CR 0x0D
#define DEL 0x7F
int getPassphrase(char password[256], boolean echo)
{
	int len = 0;
	char c;
	if(echo)
	{
		gets(password);
		return(strlen(password));
	}

	while (len < 255)
	{
		c=(char)getch();
		/* interrupt */
		if(CTLC == c) return -1;
		/* end of line */
		if((CR == c) || (LF == c)) break;
		/* delete - decrement count and guard */
		if((BS == c) || (DEL  == c))
		{
			if(len > 0)len--;
			else putch(BEL);
			continue;
		}
		/* misc non-printing - skip and warn */
		if(c < ' ')
		{
			putch(BEL);
			continue;
		}
		/* something sane at last - store and step on */
		password[len] = c;
		len++;
	}
	password[len] = 0x00;
	return len;
}

/*===========================================
* Routine cb_convKey()
* Args
*         cv_details * details       encryption context
*         void       **key           key buffer
*         size_t     * keylen        buffer size
* Return
*       	 none
* Comment
**   The message being examined has no public key encrypted parts; it is
**   presumed conventionally encrypted, and a request is made for the
**   (single) algorithm and key from hashed passphrase
*/
void cb_convKey(cv_details *details, void **key, size_t *keylen)
{
	int len = 0;
   char buffer[256];
	byte hash = MDA_MD5;

	printf(
   "A passphrase and algorithm are required for conventional encryption\n"
   "We will assume one of the following choices of algorithm, given that\n"
   "the passphrase is not likely to have more than 128 bits of entropy\n"
#ifndef NO_IDEA
   "    1 - IDEA in CFB mode (default, PGP2.6, MD5 hash)\n"
#endif
   "    2 - CAST5 in CFB mode ("
#ifdef NO_IDEA
                               "default, "
#endif
                                         "MD5 hash)\n"
	"    3 - 160 bit Blowfish in CFB mode (SHA1 hash)\n"
   "    4 - 3DES in CFB mode (MD5 Hash)\n"
   );
	gets(buffer);

   switch(buffer[0])
   {
#ifndef NO_IDEA
		default:
		case '1':	details[0].cv_algor = CEA_IDEAFLEX;
      				details[0].cv_mode = CEM_CFB;
                  *keylen = cipherKey(details[0].cv_algor);
                  printf("Got IDEA.  Now enter your passphrase\n");
                  break;
#else
		default:
#endif
		case '2':	details[0].cv_algor = CEA_CAST5;
      				details[0].cv_mode = CEM_CFB;
                  *keylen = cipherKey(details[0].cv_algor);
                  printf("Got CAST5.  Now enter your passphrase\n");
                  break;
		case '3':	details[0].cv_algor = CEA_GPG_BLOW20;
      				details[0].cv_mode = CEM_CFB;
                  *keylen = cipherKey(details[0].cv_algor);
                  printf("Got 160-Bit Blowfish.  Now enter your passphrase\n");
                  hash = MDA_SHA1;
                  break;
		case '4':	details[0].cv_algor = CEA_3DES;
      				details[0].cv_mode = CEM_CFB;
                  *keylen = cipherKey(details[0].cv_algor);
                  printf("Got 3DES.  Now enter your passphrase\n");
                  break;
	}

	while(!len)
   	len = getPassphrase(buffer, (boolean)FALSE);
   {
		uint32_t result = 0;
      char *p = buffer;
		for(; *p; ++p) result = result * 104729L + *p;
      printf("Got the passphrase, checksum %04x.  Thank you.\n", result&0xFFFF);
   }
	*key = zmalloc(*keylen);
	hashpass(buffer, *key, *keylen, hash);
   memset(buffer, 0, 256);
}

/*===========================================
* Routine cb_signedFile()
* Args
*         cb_filedesc * filedesc     file information
* Return
*       	 Open file
* Comment
**   Case of detached signature - request the file that has been signed
**   from the user to compute and check signature
*/
DataFileP cb_getFile(cb_filedesc * filedesc)
{
	char filename[256];
	char * prompt;

	switch(filedesc->purpose)
	{
		case SIGNEDFILE:
			prompt = "Enter name of file corresponding to this detached signature\n";
			break;
		case SPLITKEY:
			prompt = "Enter name of file for the split key\n";
			break;
		default:
			prompt = "Enter name of file\n";
	}
   printf(prompt);
	gets(filename);


   /* this is a place-holder - really need to explore the file type! */
   return vf_open(filename, filedesc->mode, filedesc->file_type);
}

/*===========================================
* Routine cb_need_key()
* Args
*         seckey*[]     keys         array of secret key pointers
*         int           Nkeys        length of the array
* Return
*       	 Offset into array of unlocked key, or -1 on failure
* Comment
*/
boolean showpass = FALSE;
int	cb_need_key(seckey * keys[], int Nkeys)
{
	int offset = Nkeys;
	char password[256];
	char name[256];

	while(offset-- > 0)
	{
		int len, i;
		name_from_key(publicKey(keys[offset]), name);
		printf("Passphase required for %s\n", name);

      for(i=0; i<3; ++i)
      {
			uint32_t result = 0;
     		char *p = password;
      	len = getPassphrase(password, showpass);
		   if(len < 0) return len;
		   {
				for(; *p; ++p) result = result * 104729L + *p;
      		printf("Checksum %04x: ", result&0xFFFF);
   		}
		   if(internalise_seckey(keys[offset], password))
         {
            printf("Passphrase OK\n");
      	   memset(password, 0, 256);
      	   return offset;
         }
         else memset(password, 0, 256);
         printf("Passphrase incorrect\n");
      }
	}
   abort();
	return -1;
}

/*===========================================
* Routine copyBFile()
* Args
*         DataFileP     from         source file
*         DataFileP     to           sink file
* Return
*       	 none
* Comment
*         Binary mode assumed
*/
static void copyBFile(DataFileP from, DataFileP to)
{
	byte buffer[BUFFERSIZE];
	long length;

	vf_setpos(from, 0);
	vf_setpos(to,   0);
	while((length = vf_read(buffer, BUFFERSIZE, from)) > 0)
		vf_write(buffer, length, to);
}

/*===========================================
* Routine copyTFile()
* Args
*         DataFileP     from         source file
*         DataFileP     to           sink file
* Return
*       	 none
* Comment
*         Text files
*/
/*
#ifdef __BORLANDC__
#pragma warn -aus
#endif
static void copyTFile(DataFileP from, DataFileP to)
{
	char buffer[256];
	long length;
	vf_setpos(from, 0);
	vf_setpos(to,   0);
	while((length = vf_readline(buffer, 256, from)) >= 0)
		vf_writeline(buffer, to);
}
#ifdef __BORLANDC__
#pragma warn .aus
#endif
*/
static char homedir[MAX_PATH];
static char outpath[MAX_PATH];
/*===========================================
* Routine cb_result_file()
* Args
*         DataFileP     results      file with data
*         cb_details  * details      signature and such context
* Return
*       	 none
* Comment
*/
void	cb_result_file(DataFileP results, cb_details * details)
{
	char	*	leafname;
	DataFileP	output;
	char 		name[256];
	time_t		timeStamp;

	if(details->signatory)
	{
	/* the following addition assumes that the granularity of
	** time_t is one second.  I am not sure if this is guaranteed */
		timeStamp = details->timestamp + datum_time();
		name_from_key(details->signatory, name);
		printf("File from: %s\n with %s signature @ %s\n", name,
				details->valid_sig ? "good" : "bad", ctime(&timeStamp));
	}

   if(!results) return;

	if(details->fileName)
	{
		printf("File type \'%c\' name: %s\n", details->typeByte, 
				details->fileName);
		leafname = strrchr(details->fileName, '/');
		if(leafname)
			leafname++;
		else leafname = details->fileName;
	}
   if(preserve_filename)
   {
   	strcpy(outpath, homedir);
      strcat(outpath, leafname);
      outputfile = outpath;
   }


	switch(details->typeByte)
	{
		case 't':
		if (moreflag || (strcmp(leafname,CONSOLE_FILENAME) == 0))
		{	/* blort to screen */
			if (strcmp(leafname,CONSOLE_FILENAME) == 0)
			{	fprintf(pgpout,
				PSTR("\n\nThis message is marked \"For your eyes only\".  Display now (Y/n)? "));
				if (batchmode || !getyesno('y'))
				{	/* no -- abort display, and clean up */
					return;
				}
			}
			if (!quietmode)
				fprintf(pgpout, PSTR("\n\nPlaintext message follows...\n"));
			else
				putc('\n', pgpout);
			fprintf(pgpout, "------------------------------\n");
			more_file(results);
			/* Disallow saving to disk if outfile is console-only: */
			if (strcmp(leafname,CONSOLE_FILENAME) == 0)
				clearscreen();	/* remove all evidence */
			else if (!quietmode && !batchmode)
			{
				fprintf(pgpout, PSTR("Save this file permanently (y/N)? "));
				if (! getyesno('n')) return;
         }

      }	/* blort to screen */
		if((output = vf_open(outputfile, WRITE, TEXTPLAIN)) != 0)
		{
			UTF8decode(results, output);
			vf_close(output);
		}
		break;
		
		case 'b':
      {
          byte header[8];
          char * newname = NULL;
          int i;
			/*---------------------------------------------------------*/

			/*	One last thing-- let's attempt to classify some of the more
				frequently occurring cases of plaintext output files, as an
				aid to the user.

				For example, if output file is a public key, it should have
				the right extension on the filename.

				Also, it will likely be common to encrypt files created by
				various archivers, so they should be renamed with the archiver
				extension.
			*/
      	vf_setpos(results, 0);
			vf_read(header, 8, results);

			if (header[0] == CTB_CERT_PUBKEY)
			{	/* Special case--may be public key, worth renaming */
				fprintf(pgpout, PSTR("\nPlaintext file '%s' looks like it contains a public key."),
				plainfile );
				newname = maybe_force_extension( plainfile, PGP_EXTENSION );
			}	/* Possible public key output file */
			else if ((i = compressSignature( header )) >= 0)
			{	/*	Special case--may be an archived/compressed file, worth renaming	*/
				fprintf(pgpout, PSTR("\nPlaintext file '%s' looks like a %s file."),
				plainfile, compressName[i] );
				newname = maybe_force_extension( plainfile, compressExt[i] );
			}	/*	Possible archived/compressed output file	*/
         else if (is_ctb(header[0]) &&
           (is_ctb_type (header[0], CTB_PKE_TYPE)
	 		|| is_ctb_type (header[0], CTB_SKE_TYPE)
	      || is_ctb_type (header[0], CTB_CKE_TYPE)))
	     {	/* Special case--may be another ciphertext file, worth renaming */
		      fprintf(pgpout, PSTR("\n\007Output file '%s' may contain more ciphertext or signature."),
			   plainfile );
		      newname = maybe_force_extension( plainfile, PGP_EXTENSION );
	     }	/* Possible ciphertext output file */
        if(newname != NULL) outputfile = newname;
      }
		if((output = vf_open(outputfile, WRITE, BINPLAIN)) != 0)
		{
			copyBFile(results, output);
			vf_close(output);
		}
		break;
		
		default:
			return;
	}
	return;
}

static char secring[MAX_PATH];
static char pubring[MAX_PATH];
DataFileP   pubFile = NULL;

/*=========================================================================*/

/*                            MAIN PROGRAM                                 */

/*=========================================================================*/

int main(int argc, char *argv[])
{
	int status, opt;
	char *inputfile                      = NULL;
 	char **recipient                     = NULL;
	char **mcguffins;
	const char *workfile;
	boolean decrypt_mode                 = FALSE;
	boolean wipeflag                     = FALSE;
	boolean armor_flag                   = FALSE;		/* -a option */
	boolean separate_signature           = FALSE;
	boolean keyflag                      = FALSE;
	boolean encrypt_flag                 = FALSE;
	boolean conventional_flag            = FALSE;
	char literal_file_name[MAX_PATH];
	char cipherfile[MAX_PATH];
   char workpath[MAX_PATH];
   int tmpdrive;
	char keychar                         = '\0';
   char *p;
	byte ctb;
	/*struct hashedpw *hpw;*/
   encryptInsts todo;
	pubkey * recipients[51];
   boolean splitkey = FALSE;

	setlocale(LC_ALL, "");

   memset(&context, 0, sizeof(context));
   context.keyRings = (hashtable*)0;
   todo.signatory = (seckey*)0;

	/* Initial messages to stderr */
	pgpout = stderr;

#ifdef	DEBUG1
	verbose = TRUE;
#endif
#if 0
	/* The various places one can get passwords from.
	 * We accumulate them all into two lists.  One is
	 * to try on keys only, and is stored in no particular
	 * order, while the other is of unknown purpose so
	 * far (they may be used for conventional encryption
	 * or decryption as well), and are kept in a specific
	 * order.  If any password in the general list is found
	 * to decode a key, it is moved to the key list.
	 * The general list is not grown after initialization,
	 * so the tail pointer is not used after this.
	 */

	if ((p = getenv("PGPPASS")) != NULL)
	{
   	hpw = (struct hashedpw *) xmalloc(sizeof(struct hashedpw));
		hashpass(p, hpw->hash, strlen(p), MDA_MD5);
		hpw->next = keypasswds;  /* Add to linked list of key passwords */
		keypasswds = hpw;
	}  /* if PGPPASS */


	/* The -z "password" option should be used instead of PGPPASS if
	 * the environment can be displayed with the ps command (eg. BSD).
	 * If the system does not allow overwriting of the command line
	 * argument list but if it has a "hidden" environment, PGPPASS
	 * should be used.
	 */
	for (opt = 1; opt < argc; ++opt)
	{
   	p = argv[opt];
		if (p[0] != '-' || p[1] != 'z')
      {
			continue;
      }
		/* Accept either "-zpassword" or "-z password" */
		p += 2;
		if (!*p)
      {
			p = argv[++opt];
      }  /* p now points to password */

		hpw = (struct hashedpw *) xmalloc(sizeof(struct hashedpw));
		hashpass(p, hpw->hash, strlen(p), MDA_MD5);

		while (*p) /* Wipe password */
      {
			*p++ = ' ';
      }

		hpw->next = 0;  /* Add to tail of linked list of passwords */
		*passwdstail = hpw;
		passwdstail = &hpw->next;
	} /* scan for -z */


	/*
	 * If PGPPASSFD is set in the environment try to read the password
	 * from this file descriptor.  If you set PGPPASSFD to 0 pgp will
	 * use the first line read from stdin as password.
	 */
	if ((p = getenv("PGPPASSFD")) != NULL)
	{
		int passfd;
		if (*p && (passfd = atoi(p)) >= 0)
		{
			char pwbuf[256];
			p = pwbuf;
			while (read(passfd, p, 1) == 1 && *p != '\n')
         {
				++p;
         }
			hpw = (struct hashedpw *) xmalloc(sizeof(struct hashedpw));
   		hashpass(p, hpw->hash, (size_t)(p-pwbuf), MDA_MD5);
			memset(pwbuf, 0, (size_t) (p-pwbuf) );

			hpw->next = 0;  /* Add to tail of linked list of passwords */
			*passwdstail = hpw;
			passwdstail = &hpw->next;
		}
	} /* if PGPPASSFD */
#endif /* 0 : this security-busting passphrase stuff is not implemented here */

	/* Process the config file first.  Any command-line arguments will
	   override the config file settings */
	buildfilename( mcguffin, CONFIG_FILENAME );
	if ( processConfigFile( mcguffin ) < 0 )
   {
		exit(BAD_ARG_ERROR);
   }
	init_charset();

   /* Timezones */
#ifdef MSDOS	/* only on MSDOS systems */
	if ((p = getenv("TZ")) == NULL || *p == '\0')
	{	fprintf(pgpout,PSTR("\007WARNING: Environmental variable TZ is not defined, so GMT timestamps\n\
may be wrong.  See the PGP User's Guide to properly define TZ\n\
in AUTOEXEC.BAT file.\n"));
	}
#endif	/* MSDOS */

#ifdef VMS
#define TEMP "SYS$SCRATCH"
#else
#define TEMP "TMP"
#endif /* VMS */
	if ((p = getenv(TEMP)) != NULL && *p != '\0')
   {
		settmpdir(p);
   }

	if ((myArgv = (char **) malloc((argc + 2) * sizeof(char **))) == NULL)
   {
		fprintf(stderr, PSTR("\n\007Out of memory.\n"));
		exitPGP(7);
	}
	myArgv[0] = NULL;
	myArgv[1] = NULL;

  	/* Process all the command-line option switches: */
 	while (optind < argc)
 	{	/*
 		 * Allow random order of options and arguments (like GNU getopt)
 		 * NOTE: this does not work with GNU getopt, use getopt.c from
 		 * the PGP distribution.
 		 */
 		if ((opt = getopt(argc, argv, OPTIONS)) == EOF)
 		{	if (optind == argc)		/* -- at end */
 				break;
 			myArgv[myArgc++] = argv[optind++];
 			continue;
 		}
		opt = to_lower(opt);
		if (keyflag && (keychar == '\0' || (keychar == 'v' && opt == 'v')))
		{
			if (keychar == 'v')
				keychar = 'V';
			else
				keychar = (char) opt;
			continue;
		}
		switch (opt)
		{
			case 'a': armor_flag = TRUE;
                   emit_radix_64 = 1;
                   break;
			case 'b': separate_signature = strip_sig_flag = TRUE;
                   break;
			case 'c': encrypt_flag = conventional_flag = TRUE;
					    c_flag = TRUE;
                   break;
			case 'd': decrypt_only_flag = TRUE;
                   break;
			case 'e': encrypt_flag = TRUE;
                   break;
			case 'f': filter_mode = TRUE;
                   break;
			case '?':
			case 'h': usage();
                   break;
#ifdef VMS
			case 'i': literal_mode = MODE_LOCAL;
                   break;
#endif /* VMS */

			case 'k': keyflag = TRUE;
                   break;
			case 'l': verbose = TRUE;
                   break;
			case 'm': moreflag = TRUE;
                   break;
			case 'p': preserve_filename = TRUE;
                   break;
			case 'o': outputfile = optarg;
                   break;
			case 's': sign_flag = TRUE;
                   break;
			case 't': literal_mode = MODE_TEXT;
                   break;
			case 'u': strncpy(my_name, optarg, sizeof(my_name)-1);
				       CONVERT_TO_CANONICAL_CHARSET(my_name);
				       break;
			case 'w': wipeflag = TRUE;
                   break;
			case 'x': splitkey = TRUE;
                   break;
			case 'z': break; /* already done */

			case '+': /* '+' special option: does not require - */
				if (processConfigLine(optarg) == 0) break;
				fprintf(stderr, "\n");
				/* fallthrough */
			default:
				arg_error();
		}
	}
	myArgv[myArgc] = NULL;	/* Just to make it NULL terminated */

	if (keyflag && keychar == '\0') key_usage();

	signon_msg();
	check_expiration_date();	/* hobble any experimental version */

	if (!filter_mode && (outputfile == NULL || strcmp(outputfile, "-")))
   {
		pgpout = stdout;
   }

#if defined(UNIX) || defined(VMS)
	umask(077); /* Make files default to private */
#endif

	initsigs(); /* Initialise signal handler */

   /** CTC - need keyrings opened **/
   (void) buildfilename(secring,SECRET_KEYRING_FILENAME);
   (void) buildfilename(pubring,PUBLIC_KEYRING_FILENAME);

   /* allow first key to be generated! */
   if(keyflag && 'g' == keychar)
   {
   	if(!file_exists(secring))
      {
      	int cstat = creatnew(secring, 0);
         if(-1 == cstat) exitPGP(11);
         close(cstat);
      }
   	if(!file_exists(pubring))
      {
      	int cstat = creatnew(pubring, 0);
         if(-1 == cstat) exitPGP(11);
         close(cstat);
      }
   }

   memset(&context, 0, sizeof(context));
   context.keyRings = (hashtable*)0;
	{
		pubFile = vf_open(pubring, READ, PUBLICRING);
		if(pubFile)
		{
			DataFileP secFile = vf_open(secring, READ, SECRETRING);
			context.keyRings = init_userhash(pubFile);
			/* note that the public key file is left open to allow access
			** to the key records  */
			if(secFile && read_secring(secFile, context.keyRings))
			{
				vf_close(secFile);
			}
         else
         {
				vf_close(pubFile);
            exitPGP(11);
         }
		}
      else exitPGP(11);
	}

	if (keyflag)  /* despatch key operations */
	{
   	status = do_keyopt(keychar);
		if (status < 0) user_error();
		exitPGP(status);
	}

	/* -db means break off signature certificate into separate file */
	if (decrypt_only_flag && strip_sig_flag)   /* not sure CTC can do this */
   {
		decrypt_only_flag = FALSE;
   }

	if (decrypt_only_flag && armor_flag)
   {
		decrypt_mode = de_armor_only = TRUE;
   }

	if (outputfile != NULL)
   {
		preserve_filename = FALSE;
   }

	if (!sign_flag && !encrypt_flag && !conventional_flag && !armor_flag)
	{
      if (wipeflag)	/* wipe only */
		{
         if (myArgc != 3)
				arg_error();	/* need one argument */
         else
         {
         	DataFileP temp = vf_open(myArgv[2], READWIPE, BINPLAIN);
            vf_close(temp);
				fprintf(pgpout,PSTR("\nFile %s wiped and deleted. "),myArgv[2]);
				fprintf(pgpout, "\n");
				exitPGP(EXIT_OK);
			}
		}
		/* decrypt if none of the -s -e -c -a -w options are specified */
		decrypt_mode = TRUE;
	}

 	if (myArgc == 2)		/* no arguments */
	{
#ifdef UNIX
		if (!filter_mode && !isatty(fileno(stdin)))
		{	/* piping to pgp without arguments and no -f:
			 * switch to filter mode but don't write output to stdout
			 * if it's a tty, use the preserved filename */
			if (!moreflag) pgpout = stderr;
			filter_mode = TRUE;
			if (isatty(fileno(stdout)) && !moreflag) 	preserve_filename = TRUE;
		}
#endif
		if (!filter_mode)
		{
			if (quietmode)
			{
				quietmode = FALSE;
				signon_msg();
			}
         if(!verbose)
         {
				fprintf(pgpout,PSTR("\nFor details on licensing and distribution, see licences.c.\n"));
         }
         else
         {
         	fprintf(pgpout, licence_text(GNU_GPL));
#ifndef NO_IDEA
            fprintf(pgpout, "\n");
				fprintf(pgpout, licence_text(IDEA));
#endif
         }
			if (strcmp((p = PSTR("@translator@")), "@translator@")) fprintf(pgpout, p);
			fprintf(pgpout,PSTR("\nFor a usage summary, type:  pgp -h\n"));
			exit(BAD_ARG_ERROR);		/* error exit */
		}
	}
	else
 	{
      if (filter_mode)
      {
 			recipient = &myArgv[2];
      }
 		else
 		{	inputfile = myArgv[2];
 			recipient = &myArgv[3];
 		}
 	}


	if (filter_mode)
   {
		inputfile = "stdin";
   }
	else
	{
      if (decrypt_mode && no_extension(inputfile))
		{
         strcpy(cipherfile, inputfile);
			force_extension( cipherfile, ASC_EXTENSION );
			if (file_exists (cipherfile))
         {
				inputfile = cipherfile;
         }
			else
			{
            force_extension( cipherfile, PGP_EXTENSION );
				if (file_exists (cipherfile))
            {
					inputfile = cipherfile;
            }
				else
				{	force_extension( cipherfile, SIG_EXTENSION );
					if (file_exists (cipherfile))
						inputfile = cipherfile;
				}
			}
		}
		if (! file_exists( inputfile ))
      {
			fprintf(pgpout, PSTR("\007File [%s] does not exist.\n"), inputfile);
			errorLvl = FILE_NOT_FOUND_ERROR;
			user_error();
		}
	} /* filter mode */

	if (strlen(inputfile) >= (unsigned) MAX_PATH-4)
   {
		fprintf(pgpout, PSTR("\007Invalid filename: [%s] too long\n"), inputfile );
		errorLvl = INVALID_FILE_ERROR;
		user_error();
	}
	strcpy(plainfile, inputfile);

	if (filter_mode)
   {
		setoutdir(NULL);	/* NULL means use tmpdir */
   }
	else
	{
    	if (outputfile)
			setoutdir(outputfile);
		else
			setoutdir(inputfile);
	}

	if (filter_mode)
	{
   	workfile = tempfile(TMP_WIPE|TMP_TMPDIR);
		readPhantomInput(workfile);
	}
	else
   {
		workfile = inputfile;
   }

 	if (decrypt_mode && !outputfile && myArgc > 3) outputfile = myArgv[3];

   /** CTC - need directory set **/
#if !defined( __BORLANDC__ ) && !defined( _MSC_VER )
#error "Directory management not ported!"
#endif
   workfile = _fullpath(workpath, workfile, sizeof(workpath));
   if(outputfile && strcmp(outputfile, "-") != 0)
   {
      outputfile = _fullpath(outpath, outputfile, sizeof(outpath));
   }
   /*homedrive = getdisk();*/
   getcwd(homedir, sizeof(homedir));

   if(strlen(tmpDirName()))
   {
       char tempDrvName[_MAX_DRIVE];
       _splitpath(tmpDirName(), tempDrvName, NULL, NULL, NULL);
       tmpdrive = tempDrvName[0] - 'A';
       /* set current directory to temporary, so that transient files
          will be created there by vf_tempfile() */
       setdisk(tmpdrive);
       chdir(tmpDirName());
   }

   /* so let's probe what short of a file it is */
	get_header_info_from_file( workfile, &ctb, 1 );
   makeSessionData(workfile);

   //**********Crypto at last
	if (decrypt_mode)
	{
   	context.splitkey = splitkey;
		if (!is_ctb(ctb))
      {
      	DataFileP workfileP = vf_open(CONST_CAST(char*,workfile), wipeflag?READWIPE:READ, TEXTCYPHER);
         examine_text(workfileP, &context);
      }
      else
      {
      	DataFileP workfileP = vf_open(CONST_CAST(char*,workfile), wipeflag?READWIPE:READ, BINCYPHER);
         examine(workfileP, &context);
      }
      exitPGP(EXIT_OK);
	}

#if 0
	/*	See if plaintext input file was actually created by PGP earlier--
		If it was, maybe we should NOT encapsulate it in a literal packet.
		Otherwise, always encapsulate it.
	*/
	if (force_flag)	/* for use with batchmode, force nesting */
		nestflag = (boolean) legal_ctb(ctb);
	else
		nestflag = FALSE;	/* First assume we will encapsulate it. */


	if (!batchmode && !filter_mode && legal_ctb(ctb))
	{	/*	Special case--may be a PGP-created packet, so
			do we inhibit encapsulation in literal packet? */
		fprintf(pgpout, PSTR("\n\007Input file '%s' looks like it may have been created by PGP. "),
			inputfile );
		fprintf(pgpout, PSTR("\nIs it safe to assume that it was created by PGP (y/N)? "));
		nestflag = getyesno('n');
	}	/* Possible ciphertext input file */
#endif

	if (moreflag)		/* special name to cause printout on decrypt */
	{	strcpy (literal_file_name, CONSOLE_FILENAME);
		literal_mode = MODE_TEXT;	/* will check for text file later */
	}
	else
	{	strcpy (literal_file_name, inputfile);
#ifdef MSDOS
		strlwr (literal_file_name);   /* may not be wchar safe */
#endif
	}
	todo.filename = literal_file_name;


	/*	Make sure non-text files are not accidentally converted
		to canonical text.  This precaution should only be followed
		for US ASCII text files, since European text files may have
		8-bit character codes and still be legitimate text files
		suitable for conversion to canonical (CR/LF-terminated)
		text format. */
	if (literal_mode==MODE_TEXT && !is_text_file(workfile))
	{
   	fprintf(pgpout,
PSTR("\n\007Warning: '%s' is not a pure text file.\nFile will be treated as binary data.\n"),
			workfile);
		literal_mode = MODE_BINARY; /* now expect straight binary */
	}

	if (moreflag && literal_mode==MODE_BINARY)	/* For eyes only?  Can't display binary file. */
	{	fprintf(pgpout, 
		PSTR("\n\007Error: Only text files may be sent as display-only.\n"));
		errorLvl = INVALID_FILE_ERROR;
		user_error();
	}		
   todo.fileType = (char)((literal_mode==MODE_BINARY) ? 'b' : 't');

	/*	See if plainfile looks like it might be incompressible, 
		by examining its contents for compression headers for 
		commonly-used compressed file formats like PKZIP, etc.
		Remember this information for later, when we are deciding
		whether to attempt compression before encryption.
	*/
	attempt_compression = (boolean) (compress_enabled && file_compressible(plainfile));
   todo.cp_algor = (byte)(attempt_compression ? cpalg : 0);

   todo.signatory = (seckey*)0;
   todo.armour = (byte)(emit_radix_64?ARM_PGP:ARM_NONE);
   todo.max_lines = pem_lines;

	if (sign_flag)
	{
    	todo.md_algor = mdalg;

		if (!filter_mode && !quietmode)
			fprintf(pgpout, PSTR("\nA secret key is required to make a signature. "));
		if (!quietmode && my_name[0] == '\0')
		{
			fprintf(pgpout, PSTR("\nYou specified no user ID to select your secret key,\n\
so the default user ID and key will be the most recently\n\
added key on your secret keyring.\n"));
		}

      todo.signatory = firstSecKey(context.keyRings);
      if(my_name[0] != 0)
      {
      	strlwr(my_name);
      	while(todo.signatory)
         {
         	char keyname[256];
            name_from_key(publicKey(todo.signatory), keyname);
            strlwr(keyname);
            if(strstr(keyname, my_name)) break;
            todo.signatory = todo.signatory->next_in_file;
         }
      }
      if(!todo.signatory)
      {
			fprintf(pgpout, PSTR("\n. Cannot find key matching \"%s\" for signature!  Exiting.\n"),
         my_name);
         exitPGP(11);
      }

		if (literal_mode==MODE_TEXT)
		{
			/* +clear means output file with signature in the clear,
			   only in combination with -t and -a, not with -e or -b */
			if (!encrypt_flag && !separate_signature &&
					emit_radix_64 && clear_signatures)
         {
            todo.armour = ARM_PGP_PLAIN;
			}
		}
	}

	todo.cv_algor[0].cv_algor = CEA_NONE;
   todo.cv_algor[0].cv_mode = 0;

	if (encrypt_flag)
	{
		todo.cv_algor[0].cv_algor = convalg;
   	todo.cv_algor[0].cv_mode = convmode;

		if (!conventional_flag)
		{
      	int Nto = 0;
         int delta;

			if (!filter_mode && !quietmode)
				fprintf(pgpout, PSTR("\n\nRecipients' public key(s) will be used to encrypt. \n"));
 			if (recipient == NULL || *recipient == NULL || **recipient == '\0')
			{	/* no recipient specified on command line */
				fprintf(pgpout, PSTR("\nA user ID is required to select the recipient's public key. "));
				fprintf(pgpout, PSTR("\nEnter the recipient's user ID: "));
				getPassphrase( mcguffin, TRUE );	/* echo keyboard */
				if ((mcguffins = (char **) malloc (2 * sizeof(char *))) == NULL) {
					fprintf(stderr, PSTR("\n\007Out of memory.\n"));
					exitPGP(7);
				}
				mcguffins[0] = mcguffin;
				mcguffins[1] = "";
			}
			else
			{	/* recipient specified on command line */
				mcguffins = recipient;
			}

			for (recipient = mcguffins; *recipient != NULL && 
			     **recipient != '\0'; recipient++)
         {
				CONVERT_TO_CANONICAL_CHARSET(*recipient);
            strlwr(*recipient);
            delta = key_from_name(context.keyRings, *recipient,
                    recipients+Nto, 50 - Nto);
                    Nto+=delta;
			}
         recipients[Nto] = NULL;
	      while(Nto-- > 0) completeKey(recipients[Nto]);
	      todo.to = recipients;
		}
	}	/* encrypt file */

	if (outputfile)		/* explicit output file overrides filter mode */
		filter_mode = (boolean) (strcmp(outputfile, "-") == 0);

   if(filter_mode)
   	outputfile = CONST_CAST(char*, tempfile(TMP_WIPE|TMP_TMPDIR));
	else
	{
		if (outputfile)
			strcpy(outpath, outputfile);
		else
		{	strcpy(outpath, workfile);
			drop_extension(outpath);
		}
      outputfile = outpath;
		if (no_extension(outpath))
		{	if (emit_radix_64)
				force_extension(outpath, ASC_EXTENSION);
			else if (sign_flag && separate_signature)
				force_extension(outpath, SIG_EXTENSION);
			else
				force_extension(outpath, PGP_EXTENSION);
      }
	}
   todo.version = (byte) version_byte;
   {
   	DataFileP source = vf_open(CONST_CAST(char*,workfile),
                                 wipeflag?READWIPE:READ,
                                 todo.fileType=='t'?TEXTPLAIN:BINPLAIN);
      DataFileP output = vf_open(outputfile, filter_mode?WRITEREAD:WRITE,
                                 todo.armour==ARM_NONE?BINCYPHER:TEXTCYPHER);

/* callback here */
      if(cb_need_key(&todo.signatory, 1) != 0) exitPGP(EXIT_OK);
      todo.comments = 0; /* until we actually enable this */
		if(separate_signature || todo.armour==ARM_PGP_PLAIN)
   	{
      	signOnly(source, output, &todo);
   	}
      else
      {
      	encrypt(source, output, &todo);
      }
      vf_close(source);

		if (filter_mode)
		{
         char buffer[256];
         long length;
      	vf_setpos(output, 0);

			while((length = vf_readline(buffer, 256, output)) >= 0)
				if (fwrite (buffer, 1, (size_t)length, stdout) != (size_t)length)
      			{
         			errorLvl = UNKNOWN_FILE_ERROR;
						user_error();
      			}
         vf_close(output);
			rmtemp(outputfile);
		}
      else vf_close(output);
   }
	exitPGP(EXIT_OK);
	return(0);	/* to shut up lint and some compilers */
}	/* main */

#if defined( MSDOS ) && (2 == sizeof(int))
#include <dos.h>
static char *dos_errlst[] = {
	"Write protect error",		/* PSTR ("Write protect error") */
	"Unknown unit",
	"Drive not ready",			/* PSTR ("Drive not ready") */
	"3", "4", "5", "6", "7", "8", "9",
	"Write error",				/* PSTR ("Write error") */
	"Read error",				/* PSTR ("Read error") */
	"General failure",
};

/* handler for msdos 'harderrors' */
#ifndef OS2
#ifdef __TURBOC__	/* Turbo C 2.0 */
static int dostrap(int errval)
#else
static void dostrap(unsigned deverr, unsigned errval)
#endif  /* TURBOC*/
{
	char errbuf[64];
	int i;
	sprintf(errbuf, "\r\nDOS error: %s\r\n", dos_errlst[errval]);
	i = 0;
	do
		bdos(2,(unsigned int)errbuf[i],0);
	while (errbuf[++i]);
#ifdef __TURBOC__
	return 0;	/* ignore (fopen will return NULL) */
#else
	return;
#endif   /* TURBOC*/
}
#endif /* not OS2 */
#endif /* MSDOS 16-bit */

static void initsigs()
{
#if defined( MSDOS ) && (2 == sizeof(int))
#ifndef OS2
#ifdef __TURBOC__
	harderr(dostrap);
#else /* MSC */
#ifndef __GNUC__ /* DJGPP's not MSC */
	_harderr(dostrap);
#endif
#endif
#endif
#endif /* MSDOS */
#ifdef SIGINT
#ifdef ATARI
	signal(SIGINT,(sigfunc_t) breakHandler);
#else
	if (signal(SIGINT, SIG_IGN) != SIG_IGN)
		signal(SIGINT,breakHandler);
#if defined(UNIX) || defined(VMS)
	if (signal(SIGHUP, SIG_IGN) != SIG_IGN)
		signal(SIGHUP,breakHandler);
	if (signal(SIGQUIT, SIG_IGN) != SIG_IGN)
		signal(SIGQUIT,breakHandler);
#ifdef UNIX
	signal(SIGPIPE,breakHandler);
#endif
	signal(SIGTERM,breakHandler);
#ifndef DEBUG
	signal(SIGTRAP,breakHandler);
	signal(SIGSEGV,breakHandler);
	signal(SIGILL,breakHandler);
#ifdef SIGBUS
	signal(SIGBUS,breakHandler);
#endif
#endif /* DEBUG */
#endif /* UNIX */
#endif /* not Atari */
#endif /* SIGINT */
}	/* initsigs */


void printKey(pubkey * key, boolean full)
{
}

void checkKey(pubkey * key)
{
}

static int do_keyopt(char keychar)
{
	char keyfile[MAX_PATH];
	char ringfile[MAX_PATH];
	char *workfile;
	int status;

	if ((filter_mode || batchmode)
		&& (keychar == 'g' || keychar == 'e' || keychar == 'd'
			|| (keychar == 'r' && sign_flag)))
	{	errorLvl = NO_BATCH;
		arg_error();	 /* interactive process, no go in batch mode */
	}

	switch (keychar)
	{

		/*-------------------------------------------------------*/
		case 'g':
		{	/*	Key generation
				Arguments: none
			*/

       keyType alg;
       seckey * newKey;
       DataFileP ring;
       char temp[256];

       alg.algorithm = PKA_RSA;
	    fprintf(pgpout,PSTR("\nPick your algorithm and key size:\
			\n	1)	 RSA 1024 bits- Default length\
			\n	2)	 RSA 2000 bits- long, widely useable\
			\n	3)	 RSA 2048 bits- long, some implementations can't handle\
			\n	4)	 RSA 4096 bits- long, possibly CTC only\
			\n	5)	 Elliptic curve 240 bit\
         \nChoose 1-5, or enter desired number of bits for RSA: "));
			getPassphrase( mcguffin, TRUE );

	    fprintf(pgpout,PSTR("\nEnter your user ID:\
       \nThis should contain your e-mail address in the form\
       \n<user@some.domain> by convention: "));
       if(getPassphrase(alg.name, TRUE) <= 0)
       {
       	return -1;
       }

	    fprintf(pgpout,PSTR("\nEnter passphrase:\
       \nThis should be long and contain non-alpha\
       \ncharacters for best security: "));
       if(getPassphrase(alg.passphrase, FALSE) <= 0)
       {
       	return -1;
       }
		 {
       	uint32_t result = 0;
         char *p = alg.passphrase;
		 	for(; *p; ++p) result = result * 104729L + *p;
         printf("Checksum %04x: ", result&0xFFFF);
       }
	    fprintf(pgpout,PSTR("\nEnter passphrase again:\
       \nThis should ensure you've typed it correctly twice: "));
       if(getPassphrase(temp, FALSE) <= 0 || strcmp(alg.passphrase, temp))
       {
       	return -1;
       }
		 memset(temp, 0, 256);


         alg.keyLength = (uint16_t) atoi(mcguffin);
			if (alg.keyLength==0)	/* user entered null response */
				return(-1);	/* error return */
			/* Standard default key sizes: */
			else if (alg.keyLength==1) alg.keyLength=1024;
			else if (alg.keyLength==2) alg.keyLength=2000;
			else if (alg.keyLength==3) alg.keyLength=2048;
			else if (alg.keyLength==4) alg.keyLength=4096;
			else if (alg.keyLength==5) {alg.keyLength=240; alg.algorithm=PKA_GF2255;}
			else if (alg.keyLength<1024) alg.keyLength=1024;

         /* special RSA stuff */
		   if(PKA_RSA == alg.algorithm)
		   {
		      alg.publicExponent = 17;
            alg.method = 3;
		   }
         alg.kpAlg = CEA_CAST5;
         alg.selfSignAlg = MDA_PGP5_SHA1;

         /* back up key rings */
   		{
	         strcpy(plainfile, pubring);
            force_extension(plainfile, BAK_EXTENSION);
      		ring = vf_open(plainfile, WRITE, PUBLICRING);
      		if(!ring) return -1;
      		if(!writePubRing(ring, context.keyRings)) return -1;
      		vf_close(ring);
		   }
   		{
	         strcpy(plainfile, secring);
            force_extension(plainfile, BAK_EXTENSION);
      		ring = vf_open(plainfile, WRITE, SECRETRING);
      		if(!ring) return -1;
      		if(!writeSecRing(ring, context.keyRings)) return -1;
      		vf_close(ring);
		   }
         newKey = makePKEkey(context.keyRings, &alg);
         if(NULL == newKey) return 0;
         insertSeckey(context.keyRings, newKey);

         /* save new  key rings */
   		{
         	DataFileP pring, sring;

		   	pring = vf_tempfile(0);
   			if(!pring) return -1;
   			if(!writePubRing(pring, context.keyRings)) return -1;

		   	sring = vf_tempfile(0);
   			if(!sring) return -1;
   			if(!writeSecRing(sring, context.keyRings)) return -1;

            if(NULL != pubFile) vf_close(pubFile);

	         strcpy(plainfile, pubring);
      		ring = vf_open(plainfile, WRITE, PUBLICRING);
      		if(!ring) return -1;
            copyBFile(pring, ring);
      		vf_close(pring); vf_close(ring);


	         strcpy(plainfile, secring);
      		ring = vf_open(plainfile, WRITE, SECRETRING);
      		if(!ring) return -1;
            copyBFile(sring, ring);
      		vf_close(sring);  vf_close(ring);
		   }

         {
         	DataFileP revoke = vf_open("revoke.asc", WRITE, TEXTCYPHER);
            key_revoke(revoke, newKey, FALSE);
            vf_close(revoke);
         }
         {
         	DataFileP pub = vf_open("pubkey.asc", WRITE, TEXTCYPHER);
            key_extract(pub, publicKey(newKey));
            vf_close(pub);
         }

			return 0;
		}	/* Key generation */

		/*-------------------------------------------------------*/
		case 'c':
		{	/*	Key checking
				Arguments: userid, ringfile
			*/

         DataFileP pubr;
         hashtable * hash;
			pubkey * candidate;
         char keyName[256];

			if (myArgc < 3)		/* Default to all user ID's */
				mcguffin[0] = '\0';
			else
			{	strcpy ( mcguffin, myArgv[2] );
				if (strcmp( mcguffin, "*" ) == 0)
					mcguffin[0] = '\0';
			}
			CONVERT_TO_CANONICAL_CHARSET(mcguffin);
			if (myArgc < 4) /* default key ring filename */
				buildfilename( ringfile, PUBLIC_KEYRING_FILENAME );
			else
				strncpy( ringfile, myArgv[3], sizeof(ringfile)-1 );

			if ((myArgc < 4 && myArgc > 2)	/* Allow just key file as arg */
			&& has_extension( myArgv[2], PGP_EXTENSION ) )
			{	strcpy( ringfile, myArgv[2] );
				mcguffin[0] = '\0';
			}
   		pubr = vf_open(ringfile, READ, PUBLICRING);
         hash = init_userhash(pubr);

			for(candidate = firstPubKey(hash);
       		 candidate;
       		 candidate = nextPubKey(candidate))
			{
            username * nameRec;
				for(nameRec = firstName(candidate); nameRec; nameRec=nextName(nameRec))
				{
					text_from_name(hash, nameRec, keyName);
					strlwr(keyName);

					if(0 == strlen(mcguffin) || strstr(keyName, mcguffin))
					{
               	checkKey(candidate);
					}
				}
			}
         destroy_userhash(hash);
#if 0
			if ((status = maint_check(ringfile, 0)) < 0 && status != -7)
			{	fprintf(pgpout, PSTR("\007Maintenance pass error. ") );
				errorLvl = KEYRING_CHECK_ERROR;
			}

			return (status == -7 ? 0 : status);
#endif
			return 0;
		}	/* Key check */

		/*-------------------------------------------------------*/
		case 'm':
		{	/*	Maintenance pass
				Arguments: ringfile
			*/
#if 0
			if (myArgc < 3) /* default key ring filename */
				buildfilename( ringfile, PUBLIC_KEYRING_FILENAME );
			else
				strcpy( ringfile, myArgv[2] );

#ifdef MSDOS
			strlwr( ringfile );
#endif
			if (! file_exists( ringfile ))
				default_extension( ringfile, PGP_EXTENSION );

			if ((status = maint_check(ringfile,
					MAINT_VERBOSE|(c_flag ? MAINT_CHECK : 0))) < 0)
			{	if (status == -7)
					fprintf(pgpout, PSTR("File '%s' is not a public keyring\n"), ringfile);
				fprintf(pgpout, PSTR("\007Maintenance pass error. ") );
				errorLvl = KEYRING_CHECK_ERROR;
			}
			return status;
#endif
			return 0;
		}	/* Maintenance pass */

		/*-------------------------------------------------------*/
		case 's':
		{	/*	Key signing
				Arguments: her_id, keyfile
			*/

			if (myArgc >= 4)
				strncpy( keyfile, myArgv[3], sizeof(keyfile)-1 );
			else
				buildfilename( keyfile, PUBLIC_KEYRING_FILENAME );

			if (myArgc >= 3)
				strcpy( mcguffin, myArgv[2] );	/* Userid to sign */
			else
			{
				fprintf(pgpout, PSTR("\nA user ID is required to select the public key you want to sign. "));
				if (batchmode)	/* not interactive, userid must be on command line */
					return -1;
				fprintf(pgpout, PSTR("\nEnter the public key's user ID: "));
				getPassphrase( mcguffin, TRUE );	/* echo keyboard */
			}
			CONVERT_TO_CANONICAL_CHARSET(mcguffin);

			if (my_name[0] == '\0')
			{
				fprintf(pgpout, PSTR("\nA secret key is required to make a signature. "));
				fprintf(pgpout, PSTR("\nYou specified no user ID to select your secret key,\n\
so the default user ID and key will be the most recently\n\
added key on your secret keyring.\n"));
			}

			status = signkey ( mcguffin, my_name, keyfile );

			if (status >= 0) {
				status = maint_update(keyfile);
				if (status == -7)	/* ringfile is a keyfile or secret keyring */
				{	fprintf(pgpout, "Warning: '%s' is not a public keyring\n", keyfile);
					return 0;
				}
				if (status < 0)
					fprintf(pgpout, PSTR("\007Maintenance pass error. ") );
			}

			if (status < 0)
			{	fprintf(pgpout, PSTR("\007Key signature error. ") );
				errorLvl = KEY_SIGNATURE_ERROR;
			}
			return status;
		}	/* Key signing */


		/*-------------------------------------------------------*/
		case 'd':
		{	/*	disable/revoke key
				Arguments: userid, keyfile
			*/

			if (myArgc >= 4)
				strncpy( keyfile, myArgv[3], sizeof(keyfile)-1 );
			else
				buildfilename( keyfile, PUBLIC_KEYRING_FILENAME );

			if (myArgc >= 3)
				strcpy( mcguffin, myArgv[2] );	/* Userid to sign */
			else
			{
				fprintf(pgpout, PSTR("\nA user ID is required to select the key you want to revoke or disable. "));
				fprintf(pgpout, PSTR("\nEnter user ID: "));
				getPassphrase( mcguffin, TRUE );	/* echo keyboard */
			}
			CONVERT_TO_CANONICAL_CHARSET(mcguffin);

			status = disable_key ( mcguffin, keyfile );

			if (status >= 0) {
				status = maint_update(keyfile);
				if (status == -7)	/* ringfile is a keyfile or secret keyring */
				{	fprintf(pgpout, "Warning: '%s' is not a public keyring\n", keyfile);
					return 0;
				}
				if (status < 0)
					fprintf(pgpout, PSTR("\007Maintenance pass error. ") );
			}

			if (status < 0)
				errorLvl = KEY_SIGNATURE_ERROR;
			return status;
		}	/* Key compromise */

		/*-------------------------------------------------------*/
		case 'e':
		{	/*	Key editing
				Arguments: userid, ringfile
			*/

			if (myArgc >= 4)
				strncpy( ringfile, myArgv[3], sizeof(ringfile)-1 );
			else	/* default key ring filename */
				buildfilename( ringfile, PUBLIC_KEYRING_FILENAME );

			if (myArgc >= 3)
				strcpy( mcguffin, myArgv[2] );	/* Userid to edit */
			else
			{
				fprintf(pgpout, PSTR("\nA user ID is required to select the key you want to edit. "));
				fprintf(pgpout, PSTR("\nEnter the key's user ID: "));
				getPassphrase( mcguffin, TRUE );	/* echo keyboard */
			}
			CONVERT_TO_CANONICAL_CHARSET(mcguffin);

			status = dokeyedit( mcguffin, ringfile );

			if (status >= 0) {
				status = maint_update(ringfile);
				if (status == -7)
					status = 0;	/* ignore "not a public keyring" error */
				if (status < 0)
					fprintf(pgpout, PSTR("\007Maintenance pass error. ") );
			}

			if (status < 0)
			{	fprintf(pgpout, PSTR("\007Keyring edit error. ") );
				errorLvl = KEYRING_EDIT_ERROR;
			}
			return status;
		}	/* Key edit */

		/*-------------------------------------------------------*/
		case 'a':
		{	/*	Add key to key ring
				Arguments: keyfile, ringfile
			*/

			if (myArgc < 3 && !filter_mode)
				arg_error();

			if (!filter_mode) {	/* Get the keyfile from args */
				strncpy( keyfile, myArgv[2], sizeof(keyfile)-1 );
				
#ifdef MSDOS
				strlwr( keyfile	 );
#endif
				if (! file_exists( keyfile ))
					default_extension( keyfile, PGP_EXTENSION );

				if (! file_exists( keyfile ))
				{	fprintf(pgpout, PSTR("\n\007Key file '%s' does not exist.\n"), keyfile );
					errorLvl = NONEXIST_KEY_ERROR;
					return -1;
				}

				workfile = keyfile;

			} else {
				workfile = tempfile(TMP_WIPE|TMP_TMPDIR);
				readPhantomInput(workfile);
			}

			if (myArgc < (filter_mode ? 3 : 4)) /* default key ring filename */
			{	byte ctb;
				get_header_info_from_file(workfile, &ctb, 1);
				if (ctb == CTB_CERT_SECKEY)
					buildfilename(ringfile,SECRET_KEYRING_FILENAME);
				else
					buildfilename(ringfile,PUBLIC_KEYRING_FILENAME);
			}
			else
			{	strncpy( ringfile, myArgv[(filter_mode ? 2 : 3)], sizeof(ringfile)-1 );
				default_extension( ringfile, PGP_EXTENSION );
			}
#ifdef MSDOS
			strlwr( ringfile );
#endif

			status = addto_keyring( workfile, ringfile);

			if (filter_mode)
				rmtemp(workfile);

			if (status < 0)
			{	fprintf(pgpout, PSTR("\007Keyring add error. ") );
				errorLvl = KEYRING_ADD_ERROR;
			}
			return status;
		}	/* Add key to key ring */

		/*-------------------------------------------------------*/
		case 'x':
		{	/*	Extract key from key ring
				Arguments: mcguffin, keyfile, ringfile
			*/

			if (myArgc >= (filter_mode ? 4 : 5))	/* default key ring filename */
				strncpy( ringfile, myArgv[(filter_mode ? 3 : 4)], sizeof(ringfile)-1 );
			else
				buildfilename( ringfile, PUBLIC_KEYRING_FILENAME );

			if (myArgc >= (filter_mode ? 2 : 3))
			{	if (myArgv[2])
					/* Userid to extract */
					strcpy( mcguffin, myArgv[2] );	
				else
					strcpy( mcguffin, "" );
			}
			else
			{
				fprintf(pgpout, PSTR("\nA user ID is required to select the key you want to extract. "));
				if (batchmode)	/* not interactive, userid must be on command line */
					return -1;
				fprintf(pgpout, PSTR("\nEnter the key's user ID: "));
				getPassphrase( mcguffin, TRUE );	/* echo keyboard */
			}
			CONVERT_TO_CANONICAL_CHARSET(mcguffin);

			if (!filter_mode) {
				if (myArgc >= 4)
				{	strncpy( keyfile, myArgv[3], sizeof(keyfile)-1 );
				}
				else
					keyfile[0] = '\0';

				workfile = keyfile;
			} else {
				workfile = tempfile(TMP_WIPE|TMP_TMPDIR);
			}

#ifdef MSDOS
			strlwr( workfile );
			strlwr( ringfile );
#endif

			default_extension( ringfile, PGP_EXTENSION );

			status = extract_from_keyring( mcguffin, workfile,
					ringfile, (filter_mode ? FALSE :
						   emit_radix_64) );

			if (status < 0)
			{	fprintf(pgpout, PSTR("\007Keyring extract error. ") );
				errorLvl = KEYRING_EXTRACT_ERROR;
				if (filter_mode)
					rmtemp(workfile);
				return status;
			}


			if (filter_mode && !status) {
				if (emit_radix_64)
				{	/* NULL for outputfile means write to stdout */
					if (armor_file(workfile, NULL, NULL, NULL) != 0)
					{	errorLvl = UNKNOWN_FILE_ERROR;
						return -1;
					}
				}
				else
					if (writePhantomOutput(workfile) < 0)
					{	errorLvl = UNKNOWN_FILE_ERROR;
						return -1;
					}
				rmtemp(workfile);
			}

			return 0;
		}	/* Extract key from key ring */

		/*-------------------------------------------------------*/
		case 'r':
		{	/*	Remove keys or selected key signatures from userid keys
				Arguments: userid, ringfile
			*/

			if (myArgc >= 4)
				strcpy( ringfile, myArgv[3] );
			else	/* default key ring filename */
				buildfilename( ringfile, PUBLIC_KEYRING_FILENAME );

			if (myArgc >= 3)
				strcpy( mcguffin, myArgv[2] );	/* Userid to work on */
			else
			{	if (sign_flag)
				{
					fprintf(pgpout, PSTR("\nA user ID is required to select the public key you want to\n\
remove certifying signatures from. "));
				}
				else
				{
					fprintf(pgpout, PSTR("\nA user ID is required to select the key you want to remove. "));
				}
				if (batchmode)	/* not interactive, userid must be on command line */
					return -1;
				fprintf(pgpout, PSTR("\nEnter the key's user ID: "));
				getPassphrase( mcguffin, TRUE );	/* echo keyboard */
			}
			CONVERT_TO_CANONICAL_CHARSET(mcguffin);

#ifdef MSDOS
			strlwr( ringfile );
#endif
			if (! file_exists( ringfile ))
				default_extension( ringfile, PGP_EXTENSION );

			if (sign_flag)		/* Remove signatures */
			{	if (remove_sigs( mcguffin, ringfile ) < 0)
				{	fprintf(pgpout, PSTR("\007Key signature remove error. ") );
					errorLvl = KEYSIG_REMOVE_ERROR;
					return -1;
				}
			}
			else		/* Remove keyring */
			{	if (remove_from_keyring( NULL, mcguffin, ringfile, (boolean) (myArgc < 4) ) < 0)
				{	fprintf(pgpout, PSTR("\007Keyring remove error. ") );
					errorLvl = KEYRING_REMOVE_ERROR;
					return -1;
				}
			}
			return 0;
		}	/* remove key signatures from userid */

		/*-------------------------------------------------------*/
		case 'v':
		case 'V':		/* -kvv */
		{	/*	View or remove key ring entries, with userid match
				Arguments: userid, ringfile
			*/

         DataFileP pubr;
         hashtable * hash;
			pubkey * candidate;
         char keyName[256];

			if (myArgc < 4) /* default key ring filename */
				buildfilename( ringfile, PUBLIC_KEYRING_FILENAME );
			else
				strcpy( ringfile, myArgv[3] );

			if (myArgc > 2)
			{	strcpy( mcguffin, myArgv[2] );
				if (strcmp( mcguffin, "*" ) == 0)
					mcguffin[0] = '\0';
			}
			else
				*mcguffin = '\0';

			if ((myArgc == 3) && has_extension( myArgv[2], PGP_EXTENSION ))
			{	strcpy( ringfile, myArgv[2] );
				mcguffin[0] = '\0';
			}
			CONVERT_TO_CANONICAL_CHARSET(mcguffin);

#ifdef MSDOS
			strlwr( ringfile );
#endif
			if (! file_exists( ringfile ))
				default_extension( ringfile, PGP_EXTENSION );

   		pubr = vf_open(ringfile, READ, PUBLICRING);
         hash = init_userhash(pubr);
			for(candidate = firstPubKey(hash);
       		 candidate;
       		 candidate = nextPubKey(candidate))
			{
            username * nameRec;
				for(nameRec = firstName(candidate); nameRec; nameRec=nextName(nameRec))
				{
					text_from_name(hash, nameRec, keyName);
					strlwr(keyName);

					if(0 == strlen(mcguffin) || strstr(keyName, mcguffin))
					{
               	printKey(candidate,(boolean) (keychar == 'V'));
					}
				}
			}
         destroy_userhash(hash);
			return 0;
		}	/* view key ring entries, with userid match */

		default:
			arg_error();
	}
	return 0;
} /* do_keyopt */



void user_error() /* comes here if user made a boo-boo. */
{
	fprintf(pgpout,PSTR("\nFor a usage summary, type:  pgp -h\n"));
	fprintf(pgpout,PSTR("For more detailed help, consult the PGP User's Guide.\n"));
	exitPGP(errorLvl ? errorLvl : 127);		/* error exit */
}

#if defined(DEBUG) && defined(linux)
#include <malloc.h>
#endif


static void arg_error()
{
	signon_msg();
	fprintf(pgpout,PSTR("\nInvalid arguments.\n"));
	errorLvl = BAD_ARG_ERROR;
	user_error();
}

#if 0
static void build_helpfile(char *helpfile)
{
	if (strcmp(language, "en"))
	{	buildfilename(helpfile, language);
		force_extension(helpfile, HLP_EXTENSION);
		if (!file_exists(helpfile))
			buildfilename(helpfile, HELP_FILENAME);
	}
	else
		buildfilename(helpfile, HELP_FILENAME);
}
#endif

static void usage()
{
	char helpfile[MAX_PATH];
	char *tmphelp = helpfile;
	extern unsigned char *ext_c_ptr;

	signon_msg();
#if 0
	build_helpfile(helpfile);

	if (ext_c_ptr)
	{	/* conversion to external format necessary */
		tmphelp = tempfile(TMP_TMPDIR);
		CONVERSION = EXT_CONV;
		if (copyfiles_by_name(helpfile, tmphelp) < 0)
		{	rmtemp(tmphelp);
			tmphelp = helpfile;
		}
		CONVERSION = NO_CONV;
	}

	  /* built-in help if pgp.hlp is not available */
	if (more_file(tmphelp) < 0)
#endif
		fprintf(pgpout,PSTR("\nUsage summary:\
\nTo encrypt a plaintext file with recipent's public key, type:\
\n   pgp -e textfile her_userid [other userids] (produces textfile.pgp)\
\nTo sign a plaintext file with your secret key:\
\n   pgp -s textfile [-u your_userid]           (produces textfile.pgp)\
\nTo sign a plaintext file with your secret key, and then encrypt it\
\n   with recipent's public key, producing a .pgp file:\
\n   pgp -es textfile her_userid [other userids] [-u your_userid]\
\nTo encrypt with conventional encryption only:\
\n   pgp -c textfile\
\nTo decrypt or check a signature for a ciphertext (.pgp) file:\
\n   pgp ciphertextfile [plaintextfile]\
\nTo produce output in ASCII for email, add the -a option to other options.\
\nTo generate your own unique public/secret key pair:  pgp -kg\
\nFor help on other key management functions, type:   pgp -k\n"));
	if (ext_c_ptr)
		rmtemp(tmphelp);
	exit(BAD_ARG_ERROR);		/* error exit */
}


static void key_usage()
{
	char helpfile[MAX_PATH];

	signon_msg();
	/*build_helpfile(helpfile);*/
	if (file_exists(helpfile))
	{	fprintf(pgpout,PSTR("\nFor a usage summary, type:  pgp -h\n"));
		fprintf(pgpout,PSTR("For more detailed help, consult the PGP User's Guide.\n"));
	}
	else	/* only use built-in help if there is no helpfile */
		fprintf(pgpout,PSTR("\nKey management functions:\
\nTo generate your own unique public/secret key pair:\
\n   pgp -kg\
\nTo add a key file's contents to your public or secret key ring:\
\n   pgp -ka keyfile [keyring]\
\nTo remove a key or a user ID from your public or secret key ring:\
\n   pgp -kr userid [keyring]\
\nTo edit your user ID or pass phrase:\
\n   pgp -ke your_userid [keyring]\
\nTo extract (copy) a key from your public or secret key ring:\
\n   pgp -kx userid keyfile [keyring]\
\nTo view the contents of your public key ring:\
\n   pgp -kv[v] [userid] [keyring]\
\nTo check signatures on your public key ring:\
\n   pgp -kc [userid] [keyring]\
\nTo sign someone else's public key on your public key ring:\
\n   pgp -ks her_userid [-u your_userid] [keyring]\
\nTo remove selected signatures from a userid on a keyring:\
\n   pgp -krs userid [keyring]\
\n"));
	exit(BAD_ARG_ERROR);		/* error exit */
}

/* usrbreak.c - default version (ineffective except on Mac.). 
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
**  Heimdall <heimdall@bifroest.demon.co.uk>  1996
**  All rights reserved.  For full licence details see file licence.c
*/
#include <stdlib.h>
#include <stdio.h>
#include "basic.h"
#include "usrbreak.h"

#if defined(THINK_C) || defined(SYMANTEC_C)
#include <SegLoad.h> /* for ExitToShell() in 68k version */
#include <Events.h>
/*	mDownMask			= 0x0002,	// mouse button pressed 
	mUpMask				= 0x0004,	// mouse button released
	keyDownMask			= 0x0008,	// key pressed 
	keyUpMask			= 0x0010,	// key released 
	autoKeyMask			= 0x0020,	// key repeatedly held down 
	updateMask			= 0x0040,	// window needs updating 
	diskMask			= 0x0080,	// disk inserted
	activMask			= 0x0100,	// activate/deactivate window 
	highLevelEventMask	= 0x0400,	// high-level events (includes AppleEvents) 
	osMask				= 0x8000,	// operating system events (suspend, resume) 
	everyEvent			= 0xFFFF	// all of the above */
#endif

int user_break(void)  /* returns true if the user has requested operation abort */
{
	return handled;
}

static void debug_abort(void) 
{   
#if defined(THINK_C) || defined(SYMANTEC_C)
	Debugger(); 
#endif
	exit(1);
}
void bug_check(char * text)
{
	printf("%s\n",text);
	debug_abort();
}

void getSessionData(unsigned char * data, int *length)
{
 	*length = min(*length, sessionLength);
 	memcpy(data, sessionData, *length);
 	return;
}


void getRawRandom(unsigned char * data, int length)
{
	int size = ncache < length ? ncache : length;
   ncache -= size;
   memcpy(data, cache+ncache, size);
}

static boolean mixinInit = FALSE;
void mixin(byte * data, size_t length)
{
	size_t i;
   if(!mixinInit)
   {
		MD5_CTX *temp;
   	size_t dummy;
		MD5Init(&temp, &dummy);
		MD5context = *temp;
		zfree((void*)&temp, dummy);
      mixinInit = TRUE;
   }

   for(i=0; i<length; ++i)
   {
		hashbuf[buffoff] ^=data[i];
      ++buffoff;
      if(buffoff >= MD5HASHSIZE)
      {
      	buffoff = 0;
    		MD5Update(pcontext, hashbuf, MD5HASHSIZE);
		   MD5Final(&pcontext, hashbuf, (size_t)0);
      }
   }
}


uint32_t getTimerEntropy(void)
{
	static uint32_t prev;
   uint32_t delta;
   time_t t1;
   clock_t c1;
   uint32_t now;

   t1 = time(&t1);
   mixin((byte*)&t1, sizeof(t1));
   c1 = clock();
   mixin((byte*)&c1, sizeof(c1));

#ifdef MSDOS

#if 2 == sizeof(int)
   {  /* 16-bit Dos based on PGP 2.63ui*/
   	unsigned count;
		inp(0x40); /* timer 0 port */
      outp(0x43, 0xc2); /* latch timer and count */
      count = (inp(0x40) & 0x80) << 8;
      outp(0x43, 0x00);
      count |= (inp(0x40) & 0xFF) >> 1;
      count |= (inp(0x40) & 0xFF) << 7;
      now = count;
   }
#else
	{ /* Win 32*/
		typedef struct {
      uint32_t LowPart;
      uint32_t HighPart;
      } LARGE_INTEGER;
      LARGE_INTEGER t0;
      int _stdcall QueryPerformanceCounter(LARGE_INTEGER *);
      QueryPerformanceCounter(&t0);
      now = t0.LowPart ^ t0.HighPart;
   }
#endif

#else
#error entropy cathering needed for other platforms
#endif

	mixin((byte*)&now, sizeof(now));
	delta = now-prev;
   prev = now;
   return delta;
}

void storeBit(int bit)
{
	bits <<= 1;
   if(bit) ++bits;
   if(++nbits >= 8)
	{
   	nbits = 0;
      if(ncache < CACHESIZE)
      {
      	cache[ncache++] ^= (byte) bits;
         if(CACHESIZE == ncache) ocache = CACHESIZE;
      }
      else
      {
      	cache[--ocache] ^= (byte) bits;
         if(0 == ocache) ocache = CACHESIZE;
      }
   }
}

boolean checkKeystroke(int c, int * bitcount)
{
 	static int prev = 0, pprev = 0;
   uint32_t dt = getTimerEntropy();
   int gotbits = 0;

   /* Skip triple-hits */
   if(c == pprev && c == prev) return FALSE;

   mixin((byte*)&c, sizeof(c));
   pprev = prev;
   prev = c;

   for(; dt && gotbits < 8; ++gotbits, dt>>=1)
   {
   	int bitno;
 		MD5Update(pcontext, hashbuf, MD5HASHSIZE);
	   MD5Final(&pcontext, hashbuf, (size_t)0);
      bitno = hashbuf[0] & 0x7F;
      storeBit(hashbuf[bitno/8] & (1 << (bitno%8)) );
   }

   *bitcount -= gotbits;
   return TRUE;
}

void getKeyEntropy(int bitcount)
{
fprintf(pgpout,
PSTR("\nWe need to generate %d random bits.  This is done by measuring the\
\ntime intervals between your keystrokes.  Please enter some random text\
\non your keyboard until you hear the beep:\n"), bitcount);

	do
   {
     int c;

     fprintf(pgpout, "\r%4d", bitcount);
     fflush(pgpout);

     c = getch();
     if(3 == c)
     {
     		breakHandler();
         ncache = CACHESIZE;
     }
     fputc(checkKeystroke(c, &bitcount)? '.':'?', pgpout);
   } while(bitcount > 0);
	fprintf(pgpout,PSTR("\007*\n-Enough, thank you.\n"));
   do{
      time_t one, two;
      one = time(&one);
   	while(kbhit()) getch();
      do{two = time(&two);} while (one == two);
   } while(kbhit());
}

boolean ensureRawRandom(int bytes)
{
	if(bytes > CACHESIZE) bytes = CACHESIZE;
	while(ncache*8 + nbits < bytes*8)
   {
   	int required = bytes*8 - (ncache*8 + nbits);
		getKeyEntropy(required);
   }
   return TRUE;
}

/* end of file usrbreak.c */

/* cbinfo.c
**
** Contains stub code for informationalcallback use
*/
#include "callback.h"
#include <string.h>
#include <stdio.h>

#define BUFFERSIZE 1024


char *cbmodule(int i)
{
	switch(i)
   {
   	case 1: return "CB_CTC_ENGINE";
      case 2: return "CB_ARMOUR";
		case 3: return "CB_COMPAND";
		case 4: return "CB_MSG_DIGEST";
		case 5: return "CB_BULK_CYPHER";
		case 6: return "CB_PKE";
		case 7: return "CB_PK_MANAGE";
		case 8: return "CB_CTB_IO";
		case 9: return "CB_RANDOM";
      case 10: return "CB_FILING";
      default: return "Unknown module";
   }
}

char *cbseverity(int i)
{
	switch(i)
   {
   	case 1: return "CB_CRASH   ";
      case 2: return "CB_FATAL   ";
		case 3: return "CB_ERROR   ";
		case 4: return "CB_WARNING ";
		case 5: return "CB_INFO    ";
		case 6: return "CB_STATUS  ";
      default: return "Unknown status";
   }
}

/* These should both also expand code and context */
continue_action cb_exception(cb_condition * condition)
{
	fprintf(pgpout, "status %s :", cbseverity(condition->severity));
	fprintf(pgpout, "exception %s/%d/%d \"%s\"\n",
		cbmodule(condition->module),
      condition->code, condition->context, condition->text);
   fflush(pgpout);
	return CB_CONTINUE;
}

static void cbunk(int context, char * text)
{
   fprintf(pgpout, " context CB_UNKNOWN : ");
 	switch(context)
   {
   	case KEY_RING_FAILURE:
      	fprintf(pgpout, "Key ring failure ");
         break;
   	case KEY_BAD_STATUS:
      	fprintf(pgpout, "Key bad status ");
         break;
   	case KEY_DEADBEEF:
      	fprintf(pgpout, "Deadbeef ");
         break;
      default:
      	fprintf(pgpout, "state %d ");
   }
   if(text) fprintf(pgpout, "%s\n", text);
   else fprintf(pgpout, "(no message)\n");
}

static void cbdec(int context, char * text)
{
   fprintf(pgpout, " context CB_DECRYPT : ");
 	switch(context)
   {
   	case KEY_PUBKEY_FOUND:
      	fprintf(pgpout, "Public key found ");
         break;
   	case KEY_SECKEY_FOUND:
      	fprintf(pgpout, "Secret key found ");
         break;
   	case KEY_USERID_FOUND:
      	fprintf(pgpout, "User ID found ");
         break;
   	case KEY_NO_USERID:
      	fprintf(pgpout, "NO User ID found ");
         break;
   	case KEY_WRONG_REVOKE: /* = CTC_UNIMP_COMPRESS: */
      	fprintf(pgpout, "Wrong revocation found or Unimplemented compression");
         break;
  	  case KEY_BAD_REVOKE:  /* = CTC_UNIMP_MSG_DIGEST: */
      	fprintf(pgpout, "Bad revocation found or Unimplemented digest");
         break;
   	case CTC_OUT_OF_MEMORY:
      	fprintf(pgpout, "Out of memory ");
         break;
   	case CTC_DIGESTING:
      	fprintf(pgpout, "Performing digest ");
         break;
   	case CTC_READ_FILE_ERR:
      	fprintf(pgpout, "File read error ");
         break;
   	case CTC_WRITE_FILE_ERR:
      	fprintf(pgpout, "File write error ");
         break;
   	case CTC_WRONG_BULK_KEY:
      	fprintf(pgpout, "Session key did not work ");
         break;
   	case CTC_DECOMPRESSING:
      	fprintf(pgpout, "Expanding data ");
         break;
   	case CTC_NO_TEMP_FILE:
      	fprintf(pgpout, "No temporary file ");
         break;
      default:
      	fprintf(pgpout, "state %d ");
   }
   if(text) fprintf(pgpout, "%s\n", text);
   else fprintf(pgpout, "(no message)\n");
}

static void cbenc(int context, char * text)
{
   fprintf(pgpout, " context CB_ENCRYPT : ");
 	switch(context)
   {
   	case CTC_NO_TEMP_FILE:
      	fprintf(pgpout, "No temporary file ");
         break;
   	case CTC_DECOMPRESSING:
      	fprintf(pgpout, "Expanding data ");
         break;
   	case CTC_UNIMP_COMPRESS:
      	fprintf(pgpout, "Unimplemented compression ");
         break;
   	case CTC_WRITE_FILE_ERR:
      	fprintf(pgpout, "File write error ");
         break;
   	case CTC_WRONG_BULK_KEY:
      	fprintf(pgpout, "Session key did not work ");
         break;
   	case CTC_READ_FILE_ERR:
      	fprintf(pgpout, "File read error ");
         break;
   	case CTC_DIGESTING:
      	fprintf(pgpout, "Performing digest ");
         break;
   	case CTC_OUT_OF_MEMORY:
      	fprintf(pgpout, "Out of memory ");
         break;
   	case CTC_UNIMP_MSG_DIGEST:
      	fprintf(pgpout, "Unimplemented digest ");
         break;
   	case PKE_FILE_ERROR:
      	fprintf(pgpout, "PKE file error ");
         break;
   	case PKE_NO_MEMORY:
      	fprintf(pgpout, "PKE out of memory ");
         break;
   	case PKE_USER_BREAK:
      	fprintf(pgpout, "User break taken ");
         break;
   	case PKE_BAD_RETURN_CODE:
      	fprintf(pgpout, "Bad return code ");
         break;
      default:
      	fprintf(pgpout, "state %d ");
   }
   if(text) fprintf(pgpout, "%s\n", text);
   else fprintf(pgpout, "(no message)\n");
}

void cb_information(cb_condition * condition)
{
	fprintf(pgpout, "status %s :", cbseverity(condition->severity));
   fprintf(pgpout, " module %s :", cbmodule(condition->module));

   switch(condition->context)
   {
   	 case CB_UNKNOWN:
       	cbunk(condition->code, condition->text);
   		fflush(pgpout);
         return;
   	 case CB_DECRYPTION:
       	cbdec(condition->code, condition->text);
   		fflush(pgpout);
         return;
   	 case CB_ENCRYPTION:
       	cbenc(condition->code, condition->text);
   		fflush(pgpout);
         return;
	}
	if(condition->text)
	fprintf(pgpout, "information %s/%d/%d \"%s\"\n",
		cbmodule(condition->module),
      condition->code, condition->context, condition->text);
	else
	fprintf(pgpout, "information %s/%d/%d \"%s\"\n",
		cbmodule(condition->module), condition->code, condition->context,
      "cb_information() called with no text");
   fflush(pgpout);
}

/* end of file cbinfo.c */
int dokeycheck() {return 0;}
int maint_check() {return 0;}
int signkey() {return 0;}
int disable_key() {return 0;}
int dokeyedit() {return 0;}
int maint_update() {return 0;}
int addto_keyring() {return 0;}
int extract_from_keyring() {return 0;}
int armor_file() {return 0;}
int remove_sigs() {return 0;}
int remove_from_keyring() {return 0;}
int view_keyring() {return 0;}

/* end of file */

