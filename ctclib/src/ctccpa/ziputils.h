/* ziputils.h
**  ZIP compression/decompression entry points
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
**  Heimdall <heimdall@bifroest.demon.co.uk>  1996
**  All rights reserved.  For full licence details see file licences.c
*/

#ifndef _ziputils_h
#define _ziputils_h
#include "abstract.h"

boolean reflate(DataFileP in, DataFileP out);
int deflate_file(DataFileP in, DataFileP out);
/*int file_read(char * buffer, unsigned len); */

#endif  

/* end of file ziputils.h */
