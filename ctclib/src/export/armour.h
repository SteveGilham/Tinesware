/* Armour.h
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
**  Heimdall <heimdall@bifroest.demon.co.uk>  1996
**  All rights reserved.  For full licence details see file licences.c
**
** ASCII-armouring module.  This module provides routines to armour files and recover
** them from armoured files.  The routines support both many armoured blocks in one
** text file and one binary file split into many armour blocks.  However, it has NO
** file handling.  The open/creating/closing of files is the responsibility of
** calling program.
**  Namespace CTClib Mr. Tines 5-5-98
*/

#ifndef _armour
#define _armour 
#include "abstract.h"

/* ARM_FIRST_ERROR may vary with O/S to avoid ranges of numbers used for O/S   */
/* errors.  Accordingly error codes should not be stored beyond a program run. */
/* -- is this still valid?  Tines*/

#ifndef CTCLIB_DLL
#define CTCLIB_DLL
#endif

#define MAX_BLOCK_NAME 64

NAMESPACE_CTCLIB

typedef enum
{
    ARM_NONE = 0, /* No armouring or no armoured block found */
    ARM_PGP_PLAIN, /* Not armoured but a PGP plain-signed block */
    ARM_PGP, /* PGP-classic armouring */
    ARM_PGP_MULTI, /* concatenated multi-part armour */
    ARM_MIME, 
    ARM_UUENCODE,
    ARM_BINHEX
}
armour_style;

typedef enum
{
    ARM_SUCCESS = 0,
    ARM_LINE_LIMIT = 1, /* hit # of lines limit; not truely an error*/
    ARM_USER_BREAK = 2, /* user requested abort */
    ARM_FILE_ERROR = 3, /* premature EOF, etc. */
    ARM_CRC_FAILURE = 4,
    ARM_FORMAT_ERR = 5,
    ARM_UNIMPLEMENTED = 6
}
armour_return;


/*typedef*/ struct armour_params_T
{
    char * block_type; /* E.g. "MESSAGE" */
    char * version; /* e.g. "2.6.3ui" */
    armour_style style; /* see enum type above */
    int number; /* section number */
    int of; /* number of sections */
    int max_lines; /* maximum number of lines; recommend 480 or 0 (no limit) */
    char * name; /* handle for a file name to pass to e.g. UUencode */
    char * * comments; /* pointer to a NULL terminated array of comment strings;
                ** a Comment: line to be generated for each */
}/*armour_params*/
;

/*typedef*/ struct armour_info_T
{
    char name[MAX_BLOCK_NAME]; /* For formats like UUencode that include a file name */
    /* for PGP takes the block_type text */
    int number; /* section number */
    int of; /* number of sections */
    armour_style style;
}/*armour_info*/
;

boolean CTCLIB_DLL armour_mode_avail(armour_style style);
/* Reports whether a particular form of armour is implemented */

armour_style CTCLIB_DLL next_armour(DataFileP inFile, armour_info * info);
/* Finds the next armoured block in 'inFile' and returns the type of the block */
/* and the section numbering details.   The file pointer is left prepositioned */
/* for unarmour_block.  If no block is found the values in info are undefined. */

armour_return CTCLIB_DLL unarmour_block(DataFileP in, DataFileP out, armour_info *info);
/* Unarmours a block.  This should normally be called after next_armour when */
/* destination filing decisions have been made.  The armour_style should be  */
/* the value returned by next_armour.  The file pointers of 'in' and 'out'   */
/* should be prepositioned prior to calling.                                 */

armour_return CTCLIB_DLL armour_block(DataFileP in, DataFileP out, armour_params * params);
/* Armours a block according to the parameters in params. */

END_NAMESPACE

#endif /* #ifdef _armour */

/*end of file armour.h */

