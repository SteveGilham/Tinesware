/*	fileio.c  - I/O routines for PGP.
	PGP: Pretty Good(tm) Privacy - public key cryptography for the masses.

	(c) Copyright 1990-1992 by Philip Zimmermann.  All rights reserved.
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

	Modified 16 Apr 92 - HAJK
	Mods for support of VAX/VMS file system

	Modified 17 Nov 92 - HAJK
	Change to temp file stuff for VMS.

	Modified 17 Nov 92 - apb
	Wipe file with random data.

   Modified 23-Feb-98 - Mr. Tines
   Begin CTClib integration
*/

#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#ifdef UNIX
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#ifdef _BSD
#include <sys/param.h>
#endif
extern int errno;
#endif /* UNIX */
#ifdef VMS
#include <file.h>
#endif
//#include "pgrandom.h"
#include "fileio.h"
#include "language.h"
#include "pgp.h"
//#include "exitpgp.h"
#include "charset.h"
//#include "system.h"
#if defined(MSDOS) || defined(OS2) || defined (__BORLANDC__)
#include <io.h>
#include <fcntl.h>
#endif


/* 1st character of temporary file extension */
#define	TMP_EXT	'$'		/* extensions are '.$##' */

/* The PGPPATH environment variable */

static char PGPPATH[] = "PGPPATH";

/* Disk buffers, used here and in crypto.c */
byte textbuf[DISKBUFSIZE];
static byte textbuf2[2*DISKBUFSIZE];

/*===========================================
* Routine file_exists()
* Args
*         const char *filename		Name of file to test
* Return
*       	 boolean if so
* Comment
*         uses stdio as fallback; access() is defined on Wintel
*         at least for Borland C, so use that
*/
boolean file_exists(const char *filename)
{
#if defined(UNIX) || defined(VMS) || defined (__BORLANDC__)
#ifndef F_OK
#define F_OK	0
#define X_OK	1
#define W_OK	2
#define R_OK	4
#endif /* !F_OK */
	return(boolean) (access(filename, F_OK) == 0);
#else
	FILE *f;
	/* open file f for read, in binary (not text) mode...*/
	if ((f = fopen(filename,FOPRBIN)) == NULL)
		return(FALSE);
	fclose(f);
	return(TRUE);
#endif
}	/* file_exists */

/*===========================================
* Static Routine is_regular_file()
* Args
*         const char *filename		Name of file to test
* Return
*       	 boolean if so
* Comment
*         Returns TRUE on most systems
*/
#ifdef __BORLANDC__
#pragma warn -par
#endif
static boolean is_regular_file(const char *filename)
{
#ifdef S_ISREG
	struct stat st;
	return(stat(filename, &st) != -1 && S_ISREG(st.st_mode));
#else
	return TRUE;
#endif
}
#ifdef __BORLANDC__
#pragma warn .par
#endif

/*===========================================
** Probably don't need these for CTC
*/
#if 0
static void init_buffrand(void)
{	/*	Initialise for future calls to buffrand().
		This random number generator is intended to be used
		to provide data to overwrite a disk file.  It does
		not have to be cryptographically strong, but should
		generate data that is unlikely to be easily compressible.
		If a file is overwritten with compressible data, a
		compressing file system is likely to leave some of the
		original data intact.  Overwriting a file with random
		data does not guarantee that the original data will be
		destroyed, but is better than overwriting the file
		with zeros.
	*/

	byte ideakey[24]; /* must be big enough for make_random_ideakey */

	/* We don't want the ideakey itself here, we just want the side
	   effect of initialising lots of stuff so that buffrand()
	   can later call idearand(). */
	(void) make_random_ideakey(ideakey);
	burn(ideakey);
}

static void buffrand(char *buf, int n)
{	/*	Fill a buffer with random data.  See open_buffrand()
		and close_buffrand().
	*/

	while (n-- > 0) {
		*buf++ = idearand();
	}
}

static void close_buffrand(void)
{	/*	Finish using buffrand().
	*/

	close_idearand();
}

static int wipeout(FILE *f)
{	/*	Completely overwrite and erase file, so that no sensitive
		information is left on the disk.
		NOTE:  File MUST be open for read/write.
	*/

	long flength;
	int count = 0;

	fseek(f, 0L, SEEK_END);
	flength = ftell(f);
	rewind(f);

	init_buffrand();
	while (flength > 0L)
	{	/* write random data to the whole file... */
		buffrand(textbuf, DISKBUFSIZE);
		if (flength < (word32) DISKBUFSIZE)
			count = (int)flength;
		else
			count = DISKBUFSIZE;
		fwrite(textbuf,1,count,f);
		flength -= count;
	}
	close_buffrand();
	burn(textbuf);
	rewind(f);	/* maybe this isn't necessary */
	return(0);	/* normal return */
}	/* wipeout */


int wipefile(char *filename)
{	/*	Completely overwrite and erase file, so that no sensitive
		information is left on the disk.
	*/
	FILE *f;
	/* open file f for read/write, in binary (not text) mode...*/
	if ((f = fopen(filename,FOPRWBIN)) == NULL)
		return(-1);	/* error - file can't be opened */
	wipeout(f);
	fclose(f);
	return(0);	/* normal return */
}	/* wipefile */
#endif /* 0 */
/*===========================================
* End of exclusion
*/

/*===========================================
* Routine file_tail()
* Args
*         const char *filename		Name of file to test
* Return
*       	 const char * pointer into filename for directory-free part
* Comment
*          Returns the after-slash part of filename.  Also skips backslashes,
*  colons, and right brackets.
*/
const char	*file_tail (const char *filename)
{
	char *bslashPos = strrchr(filename, '\\');/* Find last bslash in filename */
	char *slashPos = strrchr(filename, '/');/* Find last slash in filename */
	char *colonPos = strrchr(filename, ':');
	char *rbrakPos = strrchr(filename, ']');

	if (!slashPos  ||  bslashPos > slashPos)
		slashPos = bslashPos;
	if (!slashPos  ||  colonPos > slashPos)
		slashPos = colonPos;
	if (!slashPos  ||  rbrakPos > slashPos)
		slashPos = rbrakPos;
	return( slashPos?(slashPos+1):filename );
}
/* Define BSLASH for machines that use backslash, FSLASH for machines
 * that use forward slash as separators.  Use noted compilers on windows
 * machines as backstop.  Note we do not attempt to distinguish Win32s on
 * Windows 3.1 from Win95/NT with long file names
 */
#ifdef MSDOS
#define	BSLASH
#endif
#if defined( __BORLANDC__ ) || defined (_MSC_VER) || defined ( __MSDOS__ )
#define	BSLASH
#endif
#ifdef ATARI
#define BSLASH
#endif
#ifdef UNIX
#define FSLASH
#define MULTIPLE_DOTS
#endif
#ifdef AMIGA
#define FSLASH
#define MULTIPLE_DOTS
#endif

/*===========================================
* Routine has_extension()
* Args
*         const char *filename		Name of file to test
*         const char *extension     Extension to check
* Return
*       	 boolean if the last bytes of filename match extension
* Comment
*         Returns TRUE on most systems
*/
boolean has_extension(const char *filename, const char *extension)
{
	int lf = strlen(filename);
	int lx = strlen(extension);

	if (lf <= lx)
		return(FALSE);
	return (boolean) (!strcmp(filename + lf - lx, extension));
}

/*===========================================
* Routine is_tempfile()
* Args
*         const char *path		Name o to test
* Return
*       	 boolean if so (file name ends '.$##')
* Comment
*
*/
boolean is_tempfile(const char *path)
{
	char *p;
	
	return(boolean)((p = strrchr(path, '.')) != NULL &&
			p[1] == TMP_EXT && strlen(p) == 4);
}

/*===========================================
* Routine no_extension()
* Args
*         const char *filename		Name of file to test
* Return
*       	 boolean if none (or no recognised one in multi-dot case)
* Comment
*
*/
boolean no_extension(const char *filename)
{
#ifdef MULTIPLE_DOTS	/* filename can have more than one dot */
	if (has_extension(filename, ASC_EXTENSION) ||     /* Win95/NT not catered for*/
		has_extension(filename, PGP_EXTENSION) ||
		has_extension(filename, SIG_EXTENSION) ||
		is_tempfile(filename))
		return(FALSE);
	else
		return(TRUE);
#else
#ifdef BSLASH
	char *slashPos = strrchr(filename, '\\');	/* Find last slash in filename */

	/* Look for the filename after the last slash if there is one */
	return(boolean) (strchr((slashPos != NULL ) ? slashPos : filename, '.') == NULL);
#else
#ifdef FSLASH
	char *slashPos = strrchr(filename, '/');	/* Find last slash in filename */

	/* Look for the filename after the last slash if there is one */
	return(boolean)(strchr((slashPos != NULL ) ? slashPos : filename, '.') == NULL);
#else
#ifdef VMS
	char *slashPos = strrchr(filename,']');		/* Locate end of directory spec */

	/* Look for last period in filename */
	return(boolean)(strrchr((slashPos != NULL) ? slashPos : filename, '.') == NULL );
#else
	return (boolean) ( (strrchr(filename,'.')==NULL) ? TRUE : FALSE );
#endif /* VMS */
#endif /* FSLASH */
#endif /* BSLASH */
#endif /* MULTIPLE_DOTS */
}	/* no_extension */


/*===========================================
* Routine drop_extension()
* Args
*         char *filename		Name of file to edit
* Return
*       	 nothing
* Comment
*         deletes trailing ".xxx" file extension after the period.
*/
void drop_extension(char *filename)
{
	if (!no_extension(filename))
		*strrchr(filename,'.') = '\0';
}	/* drop_extension */


/*===========================================
* Routine default_extension()
* Args
*         char *filename			Name of file to edit
*         const char *extension 	Extension to add if no extension
* Return
*       	 nothing
* Comment
*         append filename extension if there isn't one already.
*/
void default_extension(char *filename, const char *extension)
{
	if (no_extension(filename))
		strcat(filename,extension);
}	/* default_extension */

/*===========================================
* Static Routine truncate_name()
* Args
*         char *path					Path to shorten as required
*         const int ext_len		length of extension to add
* Return
*       	 nothing
* Comment
*         This looks like it might be coded more simply for Wintel
*         truncate the filename so that an extension can be tacked on.
*/
#ifndef MAX_NAMELEN
#if defined(AMIGA) || defined(NeXT) || (defined(BSD) && BSD > 41)
#define	MAX_NAMELEN	255
#else
#include <limits.h>
#endif
#endif

#ifdef __BORLANDC__
#pragma warn -par
#endif
static void truncate_name(char *path, int ext_len)
{
#ifdef UNIX		/* for other systems this is a no-op */
	char *p;
#ifdef MAX_NAMELEN	/* overrides the use of pathconf() */
	int namemax = MAX_NAMELEN;
#else
	int namemax;
#ifdef _PC_NAME_MAX
	char dir[MAX_PATH];

	strcpy(dir, path);
	if ((p = strrchr(dir, '/')) == NULL)
		strcpy(dir, ".");
	else
	{	if (p == dir)
			++p;
		*p = '\0';
	}
	if ((namemax = pathconf(dir, _PC_NAME_MAX)) <= ext_len)
		return;
#else
#ifdef NAME_MAX
	namemax = NAME_MAX;
#else
	namemax = 14;
#endif /* NAME_MAX */
#endif /* _PC_NAME_MAX */
#endif /* MAX_NAMELEN */

	if ((p = strrchr(path, '/')) == NULL)
		p = path;
	else
		++p;
	if (strlen(p) > namemax - ext_len)
	{
		if (verbose)
			fprintf(pgpout, "Truncating filename '%s' ", path);
		p[namemax - ext_len] = '\0';
		if (verbose)
			fprintf(pgpout, "to '%s'\n", path);
	}
#endif /* UNIX */
}
#ifdef __BORLANDC__
#pragma warn .par
#endif


/*===========================================
* Routine force_extension()
* Args
*         char *filename			Name of file to edit
*         const char *extension 	Extension to force
* Return
*       	 nothing
* Comment
*         change the filename extension.
*/
void force_extension(char *filename, const char *extension)
{
	drop_extension(filename);	/* out with the old */
	truncate_name(filename, strlen(extension));
	strcat(filename,extension);	/* in with the new */
}	/* force_extension */


/*===========================================
* Routine getyesno()
* Args
*         const char default_answer (y or n)
* Return
*       	 boolean TRUE for yes
* Comment
*         Get yes/no answer from user, returns TRUE for yes, FALSE for no.
*         First the translations are checked, if they don't match 'y' and 'n'
*         are tried.
*/
boolean getyesno(const char default_answer)
/*	Get yes/no answer from user, returns TRUE for yes, FALSE for no.
	First the translations are checked, if they don't match 'y' and 'n'
	are tried.
*/
{	char buf[8];
	static char yes[8], no[8];

	if (yes[0] == '\0')
	{	strncpy(yes, PSTR("y"), 7);
		strncpy(no, PSTR("n"), 7);
	}
	if (!batchmode) /* return default answer in batchmode */
	{
		fgets(buf, 8, stdin);  /* echo keyboard input */
		strlwr(buf);
		if (!strncmp(buf, no, strlen(no)))
			return(FALSE);
		if (!strncmp(buf, yes, strlen(yes)))
			return(TRUE);
		if (buf[0] == 'n')
			return(FALSE);
		if (buf[0] == 'y')
			return(TRUE);
	}
	return(boolean) (default_answer == 'y' ? TRUE : FALSE);
}	/* getyesno */



/*===========================================
* Routine maybe_force_extension()
* Args
*         char * filename 				file in question
*         const char * extension    appendage to add
* Return
*       	 char* result
* Comment
*         if user consents to it, change the filename extension.
*/
char *maybe_force_extension(char *filename, const char *extension)
{
	static char newname[MAX_PATH];
	if (!has_extension(filename,extension))
	{	strcpy(newname,filename);
		force_extension(newname,extension);
		if (!file_exists(newname))
		{	fprintf(pgpout,PSTR("\nShould '%s' be renamed to '%s' [Y/n]? "),
				filename,newname);
			if (getyesno('y'))
				return(newname);
		}
	}
	return(NULL);
}	/* maybe_force_extension */

/*===========================================
* Routine buildfilename()
* Args
*         char * result 				file in question
*         const char * fname        appendage to add
* Return
*       	 char* result
* Comment
*         what systematic naming conventions!
*         Builds a filename with a complete path specifier from the environmental
*          variable PGPPATH
*/
char *buildfilename(char *result, const char *fname)
{
	char *s = getenv(PGPPATH);

	if ( s==NULL || strlen(s) > 50) /* undefined, or too long to use */
		s="";
	strcpy(result,s);
	if (strlen(result) != 0)
#ifdef BSLASH
		if (result[strlen(result)-1] != '\\')
			strcat(result,"\\");
#else
#ifdef FSLASH
		if (result[strlen(result)-1] != '/')
			strcat(result,"/");
#else
#ifdef VMS
		if (result[strlen(result)-1] != ']')
			strcat(result,"]");
#endif
#endif
#endif /* Various OS-specific defines */
	strcat(result,fname);
	return(result);
}	/* buildfilename */


/*===========================================
* Routine file_to_canon()
* Args
*         char * filename 				filename to amend
* Return
*       	 nothing
* Comment
*         Convert filename to canonical form, with slashes as separators
*/
void file_to_canon(char *filename)
{
#ifdef BSLASH
	while (*filename) {
		if (*filename == '\\')
			*filename = '/';
		++filename;
	}
#endif
}

#if 0
int write_error(FILE *f)
{
	fflush(f);
	if (ferror(f))
	{
#ifdef ENOSPC
		if (errno == ENOSPC)
			fprintf(pgpout, PSTR("\nDisk full.\n"));
		else
#endif
			fprintf(pgpout, PSTR("\nFile write error.\n"));
		return -1;
	}
	return 0;
}
#endif

/*===========================================
* Routine copyfile()
* Args
*         DataFileP	f	        from
*			 DataFileP  g          to
*         uint32_t   longcount  bytes to move
* Return
*       	 int -1 on fail, 0 on success
* Comment
*         Rewritten for port_io; assumes both files opened as binary
*         copy file f to file g, for longcount bytes
*/
int copyfile(DataFileP f, DataFileP g, uint32_t longcount)
{
	int count, status = 0;
	do	/* read and write the whole file... */
	{
		if (longcount < (uint32_t) DISKBUFSIZE)
			count = (int)longcount;
		else
			count = DISKBUFSIZE;
		count = (int) vf_read(textbuf, (long)count, f);
		if (count>0)
		{
			if (CONVERSION != NO_CONV)
			{       int i;
				for (i = 0; i < count; i++)
					textbuf[i] = (CONVERSION == EXT_CONV) ?
						     EXT_C(textbuf[i]) :
						     INT_C(textbuf[i]);
			}
			if (vf_write(textbuf,(long)count,g) != count )
			{   /* Problem: return error value */
				status = -1;
				break;
			}
			longcount -= count;
		}
		/* if text block was short, exit loop */
	} while (count==DISKBUFSIZE);
	burn(textbuf);	/* burn sensitive data on stack */
	return(status);
}	/* copyfile */


/*===========================================
* Routine copyfilepos()
* Args
*         DataFileP	f	        from
*			 DataFileP  g          to
*         uint32_t   longcount  bytes to move
*         uint32_t   fpos       offset into f to start
* Return
*       	 int -1 on fail, 0 on success
* Comment
*         Rewritten for port_io; assumes both files opened in binary mode
*         Like copyfile, but takes a position for file f.  Returns with
*         f and g pointing just past the copied data.
*/
int copyfilepos (DataFileP f, DataFileP g, uint32_t longcount, uint32_t fpos)
{
	vf_setpos(f, fpos);
	return copyfile (f, g, longcount);
}


/*===========================================
* Routine copyfile_to_canon()
* Args
*         DataFileP	f	        from
*			 DataFileP  g          to
*         uint32_t   longcount  bytes to move
* Return
*       	 int -1 on fail, 0 on success
* Comment
*         Rewritten for port_io; assumes both files opened in binary mode
*         copy file f to file g, for longcount bytes.  Convert to
*	   canonical form as we go.  f is open in text mode.  Canonical
*	   form uses crlf's as line separators.
*/
int copyfile_to_canon (DataFileP f, DataFileP g, uint32_t longcount)
{
	int count, status = 0;
	byte c, *tb1, *tb2;
	int i, nbytes;
	int nspaces = 0;
	do	/* read and write the whole file... */
	{
		if (longcount < (uint32_t) DISKBUFSIZE)
			count = (int)longcount;
		else
			count = DISKBUFSIZE;
		count = (int) vf_read(textbuf,(long)count,f);
		if (count>0)
		{	/* Convert by adding CR before LF */
			tb1 = textbuf;
			tb2 = textbuf2;
			for (i=0; i<count; ++i)
			{       switch (CONVERSION) {
				case EXT_CONV:
				    c = EXT_C(*tb1++);
				    break;
				case INT_CONV:
				    c = INT_C(*tb1++);
				    break;
				default:
				    c = *tb1++;
				}
				if (strip_spaces)
				{
					if (c == ' ')	/* Don't output spaces yet */
						nspaces += 1;
					else
					{	if (c == '\n')
						{	*tb2++ = '\r';
							nspaces = 0;	/* Delete trailing spaces */
						}
						if (nspaces)	/* Put out spaces here */
						{	do
								*tb2++ = ' ';
							while (--nspaces);
						}
						*tb2++ = c;
					}
				}
				else
				{	if (c == '\n')
						*tb2++ = '\r';
					*tb2++ = c;
				}
			}
			nbytes = (int) (tb2 - textbuf2);
			if (vf_write(textbuf2,(long)nbytes,g) != nbytes )
			{   /* Problem: return error value */
				status = -1;
				break;
			}
			longcount -= count;
		}
		/* if text block was short, exit loop */
	} while (count==DISKBUFSIZE);
	burn(textbuf);	/* burn sensitive data on stack */
	burn(textbuf2);
	return(status);
}	/* copyfile_to_canon */


/*===========================================
* Routine copyfile_to_canon()
* Args
*         DataFileP	f	        from
*			 DataFileP  g          to
*         uint32_t   longcount  bytes to move
* Return
*       	 int -1 on fail, 0 on success
* Comment
*         Rewritten for port_io; assumes both files opened in binary mode
*         copy file f to file g, for longcount bytes.  Convert from
*	   canonical to local form as we go.  g is open in text mode.  Canonical
*	   form uses crlf's as line separators.
*/
int copyfile_from_canon (DataFileP f, DataFileP g, uint32_t longcount)
{
	int count, status = 0;
	byte c, *tb1, *tb2;
	int i, nbytes;
	do	/* read and write the whole file... */
	{
		if (longcount < (uint32_t) DISKBUFSIZE)
			count = (int)longcount;
		else
			count = DISKBUFSIZE;
		count = (int) vf_read(textbuf, (long) count,f);
		if (count>0)
		{	/* Convert by removing CR's */
			tb1 = textbuf;
			tb2 = textbuf2;
			for (i=0; i<count; ++i)
			{       switch (CONVERSION) {
				case EXT_CONV:
				    c = EXT_C(*tb1++);
				    break;
				case INT_CONV:
				    c = INT_C(*tb1++);
				    break;
				default:
				    c = *tb1++;
				}
				if (c != '\r')
					*tb2++ = c;
			}
			nbytes = (int)(tb2 - textbuf2);
			if (vf_write(textbuf2,(long)nbytes,g) != nbytes )
			{   /* Problem: return error value */
				status = -1;
				break;
			}
			longcount -= count;
		}
		/* if text block was short, exit loop */
	} while (count==DISKBUFSIZE);
	burn(textbuf);	/* burn sensitive data on stack */
	burn(textbuf2);
	return(status);
}	/* copyfile_from_canon */

/*===========================================
* Routine copyfiles_by_name()
* Args
*         const char* srcFile   from
*			 const char* dstFile   to
* Return
*       	 int -1 on fail, 0 on success
* Comment
*         Rewritten for port_io;
*         Copy srcFile to destFile
*/
int copyfiles_by_name(const char *srcFile, const char *destFile)
{
	DataFileP f, g;
	int status;
	long fileLength;

	if (((f=vf_open(CONST_CAST(char*,srcFile),READ,BINPLAIN)) == NULL) ||
		((g=vf_open(CONST_CAST(char*,destFile),WRITE,BINPLAIN)) == NULL))
		/* Can't open files */
		return(-1);

	/* Get file length and copy it */
	fileLength = vf_length(f);
   vf_setpos(f, 0l);
	status = copyfile(f,g,fileLength);
	vf_close(f);
#if 0
	if (write_error(g))
		status = -1;
#endif
	vf_close(g);
	return(status);
}	/* copyfiles_by_name */

/*===========================================
* Routine make_canonical()
* Args
*         const char* srcFile   from
*			 const char* dstFile   to
* Return
*       	 int -1 on fail, 0 on success
* Comment
*         Rewritten for port_io
*         Copy srcFile to destFile, converting to canonical text form
*/
int make_canonical(const char *srcFile, const char *destFile)
/*	  */
{
	DataFileP f, g;
	int status ;

	if (((f=vf_open(CONST_CAST(char*,srcFile),READ,TEXTPLAIN)) == NULL) ||
		((g=vf_open(CONST_CAST(char*,destFile),WRITE,BINPLAIN)) == NULL))
		/* Can't open files */
		return(-1);

	/* Get file length and copy it */
	CONVERSION = INT_CONV;
	status = copyfile_to_canon(f,g,vf_length(f));
	CONVERSION = NO_CONV;
	vf_close(f);
#if 0
	if (write_error(g))
		status = -1;
#endif
	vf_close(g);
	return(status);
}	/* make_canonical */


/*===========================================
* Routine rename2()
* Args
*         const char* srcFile   from
*			 const char* dstFile   to
* Return
*       	 int -1 on fail, 0 on success
* Comment
*         Rewritten for port_io
*         Like rename() but will try to copy the file if the rename fails.
*	This is because under OS's with multiple physical volumes if the
*	source and destination are on different volumes the rename will fail
*/
int rename2(const char *srcFile, const char *destFile)
/*	 */
{
	DataFileP f, g;
	int status = 0;

#ifdef VMS
	if (rename(srcFile,destFile) != 0)
#else
	if (rename(srcFile,destFile) == -1)
#endif
	{	/* Rename failed, try a copy */
	if (((f=vf_open(CONST_CAST(char*,srcFile),READ,BINPLAIN)) == NULL) ||
		((g=vf_open(CONST_CAST(char*,destFile),WRITE,BINPLAIN)) == NULL))
			/* Can't open files */
			return(-1);

		/* Get file length and copy it */
		status = copyfile(f,g,vf_length(f));
#if 0
		if (write_error(g))
			status = -1;
#endif

		/* Zap source file if the copy went OK, otherwise zap the (possibly
		   incomplete) destination file */
		if (status >= 0)
//***WIPEOUT
		{
      	vf_close(f);
         vf_open(CONST_CAST(char*,srcFile),READWIPE, BINPLAIN);
         vf_close(f);
			remove(srcFile);
			vf_close(g);
		}
		else
		{	if (is_regular_file(destFile))
//***WIPEOUT
			{
      		vf_close(g);
         	vf_open(CONST_CAST(char*,destFile),READWIPE, BINPLAIN);
         	vf_close(g);
				vf_close(f);
				remove(destFile);
			} else
				vf_close(g);
			vf_close(f);
		}
	}
	return(status);
}


/*===========================================
* Routine readPhantomInput()
* Args
*         const char* filename  file to read from
* Return
*       	 int -1 on fail, 0 on success
* Comment
*         Rewritten for port_io
*         read the data from stdin to the phantom input file
*/
int readPhantomInput(const char *filename)
{
	DataFileP outFilePtr;
	byte buffer[ 512 ];
	int bytesRead, status = 0;

	if (verbose)
		fprintf(pgpout, "writing stdin to file %s\n", filename);
	if ((outFilePtr = vf_open(CONST_CAST(char*,filename),WRITE,BINPLAIN)) == NULL)
		return(-1);

#if defined(MSDOS) || defined(OS2) || defined(__BORLANDC__) || defined (_MSC_VER)
	/* Under DOS must set input stream to binary mode to avoid data mangling */
	setmode(fileno(stdin), O_BINARY);
#endif /* MSDOS || OS2 */
	while ((bytesRead = fread (buffer, 1, 512, stdin)) > 0)
		if (vf_write (buffer, bytesRead, outFilePtr) != bytesRead)
		{	status = -1;
			break;
		}
#if 0
	if (write_error(outFilePtr))
		status = -1;
#endif
	vf_close (outFilePtr);
#if defined(MSDOS) || defined(OS2)  || defined(__BORLANDC__) || defined (_MSC_VER)
	setmode(fileno(stdin), O_TEXT);	/* Reset stream */
#endif /* MSDOS || OS2 */
	return(status);
}

/*===========================================
* Routine writePhantomOtput()
* Args
*         const char* filename  file to read from
* Return
*       	 int -1 on fail, 0 on success
* Comment
*         Rewritten for port_io
*         write the data from the phantom output file to stdout
*/
int writePhantomOutput(const char *filename)
{
	DataFileP outFilePtr;
	byte buffer[ 512 ];
	int bytesRead, status = 0;

	if (verbose)
		fprintf(pgpout, "writing file %s to stdout\n", filename);
	/* this can't fail since we just created the file */
	outFilePtr = vf_open(CONST_CAST(char*,filename),READ,BINPLAIN);

#if defined(MSDOS) || defined(OS2)  || defined(__BORLANDC__) || defined (_MSC_VER)
	setmode(fileno(stdout), O_BINARY);
#endif /* MSDOS || OS2 */
	while ((bytesRead = (int)vf_read (buffer, 512, outFilePtr)) > 0)
		if (fwrite (buffer, 1, bytesRead, stdout) != (size_t)bytesRead)
		{	status = -1;
			break;
		}
	vf_close (outFilePtr);
	fflush(stdout);
	if (ferror(stdout))
	{	status = -1;
		fprintf(pgpout, PSTR("\007Write error on stdout.\n"));
	}
#if defined(MSDOS) || defined(OS2)  || defined(__BORLANDC__) || defined (_MSC_VER)
	setmode(fileno(stdout), O_TEXT);
#endif /* MSDOS || OS2 */

	return(status);
}
#if 0
/*===========================================
* Routine fsize()
* Args
*         DataFilep  f  file handle to test
* Return
*       	 length of tail of file
* Comment
*         Rewritten for port_io
*         Return the size from the current position of file f to the end
*/
uint32_t fsize (DataFileP f)
{
	return (uint32_t)(vf_length(f) - vf_where(f));
}
#endif

/*===========================================
* Routine is_text_file()
* Args
*         const char* filename  file to read from
* Return
*         true if looks like
* Comment
*         NOT rewritten for port_io
*         Return TRUE if file filename looks like a pure text file
*/
int is_text_file (const char *filename)
{
	FILE	*f = fopen(filename,"r");	/* FOPRBIN gives problem with VMS */
	int		i, n, bit8 = 0;
	unsigned char buf[512];
	unsigned char *bufptr = buf;
	unsigned char c;

	if (!f)
		return(FALSE);	/* error opening it, so not a text file */
	i = n = fread (buf, 1, sizeof(buf), f);
	fclose(f);
	if (n <= 0)
		return(FALSE);	/* empty file or error, not a text file */
	if (compressSignature(buf) >= 0)
		return(FALSE);
	while (i--)
	{	c = *bufptr++;
		if (c & 0x80)
			++bit8;
		else /* allow BEL BS HT LF VT FF CR EOF control characters */
			if (c < '\007' || (c > '\r' && c < ' ' && c != '\032'))
				return(FALSE);	/* not a text file */
	}
	if (strcmp(language, "ru") == 0)
		return(TRUE);
	/* assume binary if more than 1/4 bytes have 8th bit set */
	return(bit8 < n / 4);
} /* is_text_file */

#if 0
/*===========================================
* Routine xmalloc()
* Args
*         unsigned size        buffer length to allocate
* Return
*         handle to buffer
* Comment
*/
VOID *xmalloc(unsigned size)
{	VOID *p;
	if (size == 0)
		++size;
	if ((p = malloc(size)) == NULL)
	{	fprintf(stderr, PSTR("\n\007Out of memory.\n"));
		exitPGP(1);
	}
	return(p);
}
#endif
/*----------------------------------------------------------------------
 *	temporary file routines
 */


#define MAXTMPF 8

#define	TMP_INUSE	2

static struct
{	char path[MAX_PATH];
	int flags;
	int num;
} tmpf[MAXTMPF];

static char tmpdir[256];	/* temporary file directory */
static char outdir[256];	/* output directory */
static char tmpbasename[64] = "pgptemp";	/* basename for temporary files */

/*===========================================
* Routine settmpdir()
* Args
*         const char* path       path for temporary directory
* Return
*         none
* Comment
*         set directory for temporary files.  path will be stored in
* tmpdir[] with an appropriate trailing path separator.
*/
void settmpdir(const char *path)
{
	char *p;

	if (path == NULL || *path == '\0')
	{	tmpdir[0] = '\0';
		return;
	}
	strcpy(tmpdir, path);
	p = tmpdir + strlen(tmpdir)-1;
	if (*p != '/' && *p != '\\' && *p != ']' && *p != ':')
	{	/* append path separator, either / or \ */
		if ((p = strchr(tmpdir, '/')) == NULL &&
			(p = strchr(tmpdir, '\\')) == NULL)
			p = "/";	/* path did not contain / or \, use / */
		strncat(tmpdir, p, 1);
	}
}

/*===========================================
* Routine tmpDirName()
* Args
*         none
* Return
*         const char* path for temporary directory
* Comment
*/
const char * tmpDirName(void)
{
	return tmpdir;
}

/*===========================================
* Routine setoutdir()
* Args
*         const char* filename   path for temporary file
* Return
*         none
* Comment
* set output directory to avoid a file copy when temp file is renamed to
* output file.  the argument filename must be a valid path for a file, not
* a directory.
*/
void setoutdir(const char *filename)
{
	char *p;

	if (filename == NULL)
	{	strcpy(outdir, tmpdir);
		return;
	}
	strcpy(outdir, filename);
	p = CONST_CAST(char*, file_tail(outdir));
	strcpy(tmpbasename, p);
	*p = '\0';
	drop_extension(tmpbasename);
#if !defined(BSD42) && !defined(BSD43) && !defined(sun)
	/* we don't depend on pathconf here, if it returns an incorrect value
	 * for NAME_MAX (like Linux 0.97 with minix FS) finding a unique name
	 * for temp files can fail.
	 */
	tmpbasename[10] = '\0';	/* 14 char limit */
#endif
}

/*===========================================
* Routine tempfile()
* Args
*         int                    flags
* Return
*         char*                  unique file name
* Comment
*         return a unique temporary file name.
*/
const char *tempfile(const int flagsi)
{
	int i, j, flags = flagsi;
	int num;
	int fd;
#ifndef UNIX
	FILE *fp;
#endif

	for (i = 0; i < MAXTMPF; ++i)
		if (tmpf[i].flags == 0)
			break;

	if (i == MAXTMPF)
	{	/* message only for debugging, no need for PSTR */
		fprintf(stderr, "\n\007Out of temporary files\n");
		return(NULL);
	}

again:
	num = 0;
	do {
		for (j = 0; j < MAXTMPF; ++j)
			if (tmpf[j].flags && tmpf[j].num == num)
				break;
		if (j < MAXTMPF)
			continue;	/* sequence number already in use */
		sprintf(tmpf[i].path, "%s%s.%c%02d",
			((flags & TMP_TMPDIR) && *tmpdir ? tmpdir : outdir),
			tmpbasename, TMP_EXT, num);
		if (!file_exists(tmpf[i].path))
			break;
	}
	while (++num < 100);

	if (num == 100)
	{	fprintf(pgpout, "\n\007tempfile: cannot find unique name\n");
		return(NULL);
	}

#if defined(UNIX) || defined(VMS)
	if ((fd = open(tmpf[i].path, O_EXCL|O_RDWR|O_CREAT, 0600)) != -1)
		close(fd);
#else
	if ((fp = fopen(tmpf[i].path, "w")) != NULL)
		fclose(fp);
	fd = (fp == NULL ? -1 : 0);
#endif

	if (fd == -1)
	{	if (!(flags & TMP_TMPDIR))
		{	flags |= TMP_TMPDIR;
			goto again;
		}
#ifdef UNIX
		else if (tmpdir[0] == '\0')
		{	strcpy(tmpdir, "/tmp/");
			goto again;
		}
#endif
	}
	if (fd == -1)
	{	fprintf(pgpout, PSTR("\n\007Cannot create temporary file '%s'\n"), tmpf[i].path);
		user_error();
	}
#ifdef VMS
	remove(tmpf[i].path);
#endif

	tmpf[i].num = num;
	tmpf[i].flags = flags | TMP_INUSE;
	if (verbose)
		fprintf(pgpout, "tempfile: created '%s'\n", tmpf[i].path);
	return(tmpf[i].path);
}	/* tempfile */

/*===========================================
* Routine rmtemp()
* Args
*         const char* name       name of temporary file
* Return
*         none
* Comment
* remove temporary file, wipe if necessary.
*/
void rmtemp(const char *name)
{
	int i;

	for (i = 0; i < MAXTMPF; ++i)
		if (tmpf[i].flags && strcmp(tmpf[i].path, name) == 0)
			break;

	if (i < MAXTMPF)
	{	if (strlen(name) > 3 && name[strlen(name)-3] == TMP_EXT)
		{	/* only remove file if name hasn't changed */
			if (verbose)
				fprintf(pgpout, "rmtemp: removing '%s'\n", name);
			if (tmpf[i].flags & TMP_WIPE)
         {
         	DataFileP w = vf_open(CONST_CAST(char*,name),READWIPE,BINPLAIN);
            vf_close(w);
         }
			if (!remove(name)) {
			    tmpf[i].flags = 0;
			} else if (verbose) {
			    fprintf(stderr,"\nrmtemp: Failed to remove %s",name);			    
			    perror ("\nError");
			}
		} else if (verbose)
			fprintf(pgpout, "rmtemp: not removing '%s'\n", name);
	}
}	/* rmtemp */

#if 0
/*===========================================
* Routine savetemp()
* Args
*         const char* name       name of temporary file
*         const char* newname    new name to apply
* Return
*         const char * new name
* Comment
* make temporary file permanent, returns the new name.
*/
const char *savetemp(const char *name, const char *newname)
{
	int i, overwrite;
	static char buf[MAX_PATH];

	if (strcmp(name, newname) == 0)
		return(name);

	for (i = 0; i < MAXTMPF; ++i)
		if (tmpf[i].flags && strcmp(tmpf[i].path, name) == 0)
			break;

	if (i < MAXTMPF)
	{	if (strlen(name) < 4 || name[strlen(name)-3] != TMP_EXT)
		{	if (verbose)
				fprintf(pgpout, "savetemp: not renaming '%s' to '%s'\n",
						name, newname);
			return(name);	/* return original file name */
		}
	}

	while (file_exists(newname))
	{
		if (batchmode && !force_flag)
		{	fprintf(pgpout,PSTR("\n\007Output file '%s' already exists.\n"),newname);
			return NULL;
		}
		if (is_regular_file(newname))
		{	
			if (force_flag)
			{	/* remove without asking */
				remove(newname);
				break;
			}
			fprintf(pgpout,PSTR("\n\007Output file '%s' already exists.  Overwrite (y/N)? "),
				newname);
			overwrite = getyesno('n');
		}
		else
		{	fprintf(pgpout,PSTR("\n\007Output file '%s' already exists.\n"),newname);
			if (force_flag)	/* never remove special file */
				return NULL;
			overwrite = FALSE;
		}

		if (!overwrite)
		{	fprintf(pgpout, PSTR("\nEnter new file name: "));
			getstring(buf, MAX_PATH - 1, TRUE);
			if (buf[0] == '\0')
				return(NULL);
			newname = buf;
		}
		else
			remove(newname);
	}
	if (verbose)
		fprintf(pgpout, "savetemp: renaming '%s' to '%s'\n", name, newname);
	if (rename2(name, newname) < 0)
	{	/* errorLvl = UNKNOWN_FILE_ERROR; */
		fprintf(pgpout, PSTR("Can't create output file '%s'\n"), newname);
		return(NULL);
	}
	if (i < MAXTMPF)
		tmpf[i].flags = 0;
	return(newname);
}	/* savetemp */
#endif
#if 0
/*===========================================
* Routine savetemp()
* Args
*         const char* tmpname    name of temporary file
*         const char* destname   new name to apply
* Return
*         0 on success, -1 on fail
* Comment
* like savetemp(), only make backup of destname if it exists
*/
int savetempbak(const char *tmpname, const char *destname)
{
	char bakpath[MAX_PATH];
#ifdef UNIX
	int mode = -1;
#endif

	if (is_tempfile(destname))
		remove(destname);
	else
	{	if (file_exists(destname))
		{
#ifdef UNIX
			struct stat st;
			if (stat(destname, &st) != -1)
				mode = st.st_mode & 07777;
#endif
			strcpy(bakpath, destname);
			force_extension(bakpath, BAK_EXTENSION);
			remove(bakpath);
#ifdef VMS
			if (rename(destname, bakpath) != 0)
#else
			if (rename(destname, bakpath) == -1)
#endif
				return(-1);
		}
	}
	if (savetemp(tmpname, destname) == NULL)
		return(-1);
#ifdef UNIX
	if (mode != -1)
		chmod(destname, mode);
#endif
	return(0);
}
#endif
/*===========================================
* Routine cleanup_tmpf()
* Args
*         none
* Return
*         none
* Comment
* remove all temporary files and wipe them if necessary
*/
void cleanup_tmpf(void)
{
	int i;

	for (i = 0; i < MAXTMPF; ++i)
		if (tmpf[i].flags)
			rmtemp(tmpf[i].path);
}
/* end of file */
