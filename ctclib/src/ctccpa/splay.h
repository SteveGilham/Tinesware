/* splay.h
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1997
**  All rights reserved.  For full licence details see file licences.c
**
**  Compression via splay tree using Markov processes.
**
*/
#ifndef _splay
#define _splay

#include "abstract.h"

void compressSplay(int states, DataFileP input, DataFileP output);
boolean expandSplay(DataFileP input, DataFileP output);


#endif
/* End of file splay.h */

