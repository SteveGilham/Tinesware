/* pkops.h
 **
 **  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 **  Heimdall <heimdall@bifroest.demon.co.uk>  1996
 **  All rights reserved.  For full licence details see file licences.c
 */
#ifndef _pkops
#define _pkops
#include "abstract.h"

#define RSAPUB_N 0
#define RSAPUB_E 1

#define RSASEC_D 0
#define RSASEC_P 1
#define RSASEC_Q 2
#define RSASEC_U 3

#define RSASIG 0

#define EGPUB_P 0
#define EGPUB_G 1
#define EGPUB_Y 2

#define EGSEC 0

#define EGCYP_A 0
#define EGCYP_B 1

#define EGSIG_R 0
#define EGSIG_S 1

#define DSAPUB_P 0
#define DSAPUB_Q 1
#define DSAPUB_G 2
#define DSAPUB_Y 3

#define DSASEC 0

#define DSASIG_R 0
#define DSASIG_S 1

#endif

/* end of file pkops.h */
