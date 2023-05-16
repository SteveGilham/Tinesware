/* cleave.h
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
**  Heimdall <heimdall@bifroest.demon.co.uk>  1996
**  All rights reserved.  For full licence details see file licences.c
**
***********************************************/

/* armouring has produced a multipart file 'complete'
** factory is a full file name, which should end in .asc for
** backward compatibility.  The last character or two of factory
** are incremented as digits once for each part, to create a
** new file name */
#ifndef CTCLIB_DLL
#define CTCLIB_DLL
#endif

boolean CTCLIB_DLL cleaveApart(DataFileP complete, char *factory);
DataFileP CTCLIB_DLL cleaveTogether(char *factory);



/* end of file cleave.h */
