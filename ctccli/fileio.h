/* fileio.h
** Tidied up 23-Feb-98 by Mr. Tines for CTClib integration
*/
#ifndef _fileio
#define _fileio

#include "port_io.h"

#define DISKBUFSIZE 4096	/* Size of I/O buffers */

#ifndef SEEK_SET
#define SEEK_SET 0
#define	SEEK_CUR 1
#define SEEK_END 2
#endif

#ifdef VMS
#define FOPRBIN		"rb","ctx=stm"
#define FOPRTXT		"r"
#if 0
#define FOPWBIN		"ab","fop=cif"
#define FOPWTXT		"a","fop=cif"
#else
#define FOPWBIN		"wb"
#define FOPWTXT		"w"
#endif
#define FOPRWBIN	"r+b","ctx=stm"
#define FOPWPBIN	"w+b","ctx=stm"
#else
#ifdef UNIX
#define FOPRBIN		"r"
#define FOPRTXT		"r"
#define FOPWBIN		"w"
#define FOPWTXT		"w"
#define FOPRWBIN	"r+"
#define FOPWPBIN	"w+"
#else /* !UNIX && !VMS */
#define FOPRBIN		"rb"
#define FOPRTXT		"r"
#define FOPWBIN		"wb"
#define FOPWTXT		"w"
#define FOPRWBIN	"r+b"
#define FOPWPBIN	"w+b"
#endif /* UNIX */
#endif /* VMS */

#define	TMP_WIPE		1
#define	TMP_TMPDIR		4

#define equal_buffers(buf1,buf2,count)	!memcmp( buf1, buf2, count )

/* Returns TRUE iff file is can be opened for reading. */
boolean file_exists(const char *filename);

/* Returns TRUE iff file can be opened for writing. Does not harm file! */
boolean file_ok_write(char *filename);

/* Completely overwrite and erase file of given name, so that no sensitive
   information is left on the disk */
int wipefile(char *filename);

/* Return the after-slash part of the filename */
const char	*file_tail (const char *filename);
/* Returns TRUE if user left off file extension, allowing default */
boolean no_extension(const char *filename);

/* Deletes trailing ".xxx" file extension after the period */
void drop_extension(char *filename);

/* Append filename extension if there isn't one already */
void default_extension(char *filename, const char *extension);

/* Change the filename extension */
void force_extension(char *filename, const char *extension);

/* Get yes/no answer from user, returns TRUE for yes, FALSE for no */
boolean getyesno(const char default_answer);

/* If luser consents to it, change the filename extension */
char *maybe_force_extension(char *filename, const char *extension);

/* Builds a filename with a complete path specifier from the environmental
   variable PGPPATH */
char *buildfilename(char *result, const char *fname);

/* Build a path for fileName based on origPath */
int build_path(char *path, char *fileName, char *origPath);

/* Convert filename to canonical form, with slashes as separators */
void file_to_canon(char *filename);

/* Convert filename from canonical to local form */
void file_from_canon(char *filename);

/* Copy file f to file g, for longcount bytes */
int copyfile(DataFileP f, DataFileP g, uint32_t longcount);

/* Copy file f to file g, for longcount bytes, positioning f at fpos */
int copyfilepos (DataFileP f, DataFileP g, uint32_t longcount, uint32_t fpos);

/* Copy file f to file g, for longcount bytes.  Convert to canonical form
   as we go.  f is open in text mode.  Canonical form uses crlf's as line
   separators */
int copyfile_to_canon (DataFileP f, DataFileP g, uint32_t longcount);

/* Copy file f to file g, for longcount bytes.  Convert from canonical to
   local form as we go.  g is open in text mode.  Canonical form uses crlf's
   as line separators */
int copyfile_from_canon (DataFileP f, DataFileP g, uint32_t longcount);

/* Copy srcFile to destFile */
int copyfiles_by_name(const char *srcFile, const char *destFile);

/* Copy srcFile to destFile, converting to canonical text form */
int make_canonical(const char *srcFile, const char *destFile);

/* Like rename() but will try to copy the file if the rename fails. This is
   because under OS's with multiple physical volumes if the source and
   destination are on different volumes the rename will fail */
int rename2(const char *srcFile, const char *destFile);

/* Read the data from stdin to the phantom input file */
int readPhantomInput(const char *filename);

/* Write the data from the phantom output file to stdout */
int writePhantomOutput(const char *filename);

/* Return the size from the current position of file f to the end */
uint32_t fsize (DataFileP f);

/* Return TRUE if file filename is a text file */
int is_text_file (const char *filename);

FILE *fopenbin(char *, char *);
FILE *fopentxt(char *, char *);

VOID *xmalloc(unsigned);

const char *tempfile(const int flags);
void rmtemp(const char *);
const char *savetemp(const char *, const char *);
void cleanup_tmpf(void);
int savetempbak(const char *, const char *);

extern int write_error(FILE *f);
extern void settmpdir(const char *path);
extern void setoutdir(const char *filename);
extern boolean is_tempfile(const char *path);
extern boolean has_extension(const char *filename, const char *extension);
extern const char * tmpDirName(void);

#endif /* ndef _fileio */


