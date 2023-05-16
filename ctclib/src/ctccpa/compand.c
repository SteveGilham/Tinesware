/* compand.c
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
**  Heimdall <heimdall@bifroest.demon.co.uk>  1996
**  All rights reserved.  For full licence details see file licences.c
**
** Splay tree compression added : this is the file for compression
** algorithm selection; expansion is in decompress() in ziputils.c
**                                   Mr. Tines 24-Mar-1997
**
** Move all compress/decompress algorithm selection into this source file
**                                   Mr. Tines 20-Apr-1997
**
** Move all compress/decompress algorithm selection into this source file
** from ctc.c
**                                   Mr. Tines 14-Sep-1997
*/

#include "splay.h"
#include "utils.h"
#include "ziputils.h"
#include "compand.h"
#include "keyconst.h"

boolean compandAlgAvail(byte alg)
{
    switch (alg)
    {
    case CPA_NONE:
    case CPA_DEFLATE:
    case CPA_SPLAY:
        return TRUE;
    default:
        return FALSE;
    }
}


boolean compress(DataFileP input, byte algorithm, DataFileP result)
{
    if(algorithm == CPA_DEFLATE)
    {
        vf_setpos(input, 0);
        deflate_file(input, result);
        vf_close(input);
        return TRUE;
    }
    else if(algorithm == CPA_SPLAY)
    {
        vf_setpos(input, 0);
        /* choose a 128-bit splay by default : tests have shown that this
                      is near optimum for text (and acceptable for non-compressed images).
                      The optimum is very shallow - with ~90 printable ASCII characters,
                      plus \n, \t and, in raw MSDOS text, \r, anything of about this number
                      will do; and the compression will remain flat out to 255, on empirical
                      testing, with a sudden blip at 256 (not recommended).
                
                      This routine could yield out-of-memory failure
                      - still to be communicated */
        compressSplay((int)128, input, result);
        vf_close(input);
        return TRUE;
    }
    return FALSE;
}

boolean decompress(DataFileP in, DataFileP out, byte algorithm)
{
    switch(algorithm)
    {
    case CPA_DEFLATE:
        return reflate(in, out);
    case CPA_SPLAY:
        return expandSplay(in, out);
    }
    return FALSE;
}

/*------- end  compand.c (de)compression algorithm despatcher -------*/

