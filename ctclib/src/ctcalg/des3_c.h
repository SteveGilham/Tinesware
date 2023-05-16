/* des3_c.h - header file for des3*.c as used in CTC
**
** Original code by R. Outerbridge
** Modified by Mr. Tines 6-Dec-1997 to CTC coding standards
*/
#ifndef _des3_c
#define _des3_c

#include "basic.h"
#include "des.h"

#define DESKEYSCHEDLONGS 32
#define BOXLEN 64
typedef struct {
    uint32_t SP1[BOXLEN];
    uint32_t SP2[BOXLEN];
    uint32_t SP3[BOXLEN];
    uint32_t SP4[BOXLEN];
    uint32_t SP5[BOXLEN];
    uint32_t SP6[BOXLEN];
    uint32_t SP7[BOXLEN];
    uint32_t SP8[BOXLEN];
    uint32_t key[DESKEYSCHEDLONGS];
}
DES, *pDES;

#define DESKEYSCHEDLEN sizeof(DES);

typedef struct {
    uint32_t SP1[BOXLEN];
    uint32_t SP2[BOXLEN];
    uint32_t SP3[BOXLEN];
    uint32_t SP4[BOXLEN];
    uint32_t SP5[BOXLEN];
    uint32_t SP6[BOXLEN];
    uint32_t SP7[BOXLEN];
    uint32_t SP8[BOXLEN];
    uint32_t key[3*DESKEYSCHEDLONGS];
}
DES3, *pDES3;

#define DES3KEYSCHEDLEN sizeof(DES3);

/* the following relies on a DES3 punning onto a DES */
void initDesSPboxes(pDES boxes);
void inits3DesSPboxes(pDES boxes);
void initKDdesSPboxes(byte key[KDDESEXTRA],pDES boxes);


/* [8], [8], [32] */
extern void des( byte *inblock, byte *outblock, pDES key );

/* [8], [8], [96] */
extern void des3( byte *inblock, byte *outblock, pDES3 key );

/* [8], 1, [32] */
extern void deskey(byte *hexkey, short mode, uint32_t *keybuf);

/* [16], 1, [96] */
extern void des2key( byte *hexkey, short mode, uint32_t *keyout );

/* [24], 1, [96] */
extern void des3key( byte *hexkey, short mode, uint32_t *keyout );

#define EN0 0  /* MODE == encrypt */
#define DE1 1  /* MODE == decrypt */

#endif  /* ndef _des3_c */

