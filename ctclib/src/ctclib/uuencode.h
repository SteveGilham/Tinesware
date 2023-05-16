/* uuencode.h
 **
 **  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 **  Heimdall <heimdall@bifroest.demon.co.uk>  1996
 **  All rights reserved.  For full licence details see file licences.c
 **
 ** ASCII-armouring module.  This module provides routines to armour files and recover
 ** them from armoured files.  The routines support both many armoured blocks in one
 ** text file and one binary file split into many armour blocks.  However, it has NO
 ** file handling.  The open/creating/closing of files is the responsibility of
 ** calling program.  */

#ifndef _uuencode
#define _uuencode
#include "abstract.h"

armour_return uudecode_block(DataFileP in, DataFileP out);
armour_return uuencode_block(DataFileP in, DataFileP out,
armour_params * params);

#endif /* #ifdef _uuencode */

/*end of file uuencode.h */
