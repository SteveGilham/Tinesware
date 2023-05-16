/* cleave.c
 **
 **  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 **  Heimdall <heimdall@bifroest.demon.co.uk>  1996
 **  All rights reserved.  For full licence details see file licences.c
 **
 ***********************************************/
#include "abstract.h"
#include "port_io.h"
#include "cleave.h"
#include <string.h>


/* armouring has produced a multipart file 'complete'
 ** factory is a full file name, which should end in .asc for
 ** backward compatibility.  The last character or two of factory
 ** are incremented as digits once for each part, to create a
 ** new file name.  Little checking is done here - it is left
 ** to the armouring routines */
#define STARTLINE "-----BEGIN PGP "
#define ENDLINE  "-----END PGP "

boolean cleaveApart(DataFileP complete, char *factory)
{
    char *last = factory + (strlen(factory)-1);
    char *lb1 = last - 1;
    int index;
    char buffer[128];

    buffer[0] = '\0';
    vf_setpos(complete, 0);

    for(index = 1;index < 100;++index)
    {
        DataFileP next;

        *last = (char)((index%10)+'0');
        if(index>9) *lb1=(char)((index/10)+'0');

        while(strncmp(buffer, STARTLINE, strlen(STARTLINE)))
        {
            if( vf_readline(buffer, 128, complete) < 0)
                return TRUE;            /* EOF */
        }
        next = vf_open(factory, WRITE, TEXTCYPHER);
        if(!next) return FALSE;

        if(vf_writeline(buffer, next) < 0) return FALSE;        /* write start line */

        while(strncmp(buffer, ENDLINE, strlen(ENDLINE)))
        {
            if(vf_readline(buffer, 128, complete) < 0) return FALSE;
            if(vf_writeline(buffer, next) < 0) return FALSE;
        }
        vf_close(next);
    }
    return FALSE;
}

DataFileP cleaveTogether(char *factory)
{
    DataFileP complete;
    char *last = factory + (strlen(factory)-1);
    char *lb1 = last - 1;
    int index;
    char buffer[128];
    char save = *lb1;

    buffer[0] = '\0';
    *last = '0';
    complete = vf_open(factory, WRITE, TEXTCYPHER);
    if(!complete) return 0;

    for(index = 1;index < 100;++index)
    {
        DataFileP next;

        *last = (char)((index%10)+'0');
        if(index>9) *lb1=(char)((index/10)+'0');

        next = vf_open(factory, READ, TEXTCYPHER);
        if(!next)
        {
            if(index > 1) break;            /* assume we have them all */
            else *lb1 = '0';            /* perhaps starts 01 */
            next = vf_open(factory, READ, TEXTCYPHER);
            if(!next)            /* that failed also - give up */
            {
                vf_close(complete);
                return 0;
            }
        }

        while(strncmp(buffer, STARTLINE, strlen(STARTLINE)))
        {
            if( vf_readline(buffer, 128, next) < 0)
            {
                vf_close(complete);                /* premature EOF */
                return 0;
            }
        }
        if(vf_writeline(buffer, complete) < 0)        /* write start line */
        {
            vf_close(complete);
            return 0;
        }

        while(strncmp(buffer, ENDLINE, strlen(ENDLINE)))
        {
            if((vf_readline(buffer, 128, next) < 0) ||
                (vf_writeline(buffer, complete) < 0))
            {
                vf_close(complete);
                return 0;
            }
        }

        vf_close(next);        /* ignore trailing rubbish */
    }
    /*vf_setpos(complete, 0L);*/
    vf_close(complete);
    *last = '0';
    *lb1 = save;
    complete = vf_open(factory, READWIPE, TEXTCYPHER);
    return complete;
}

/* end of file cleave.c */
