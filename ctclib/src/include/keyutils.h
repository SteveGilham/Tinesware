/* keyutils.h
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
**  Heimdall <heimdall@bifroest.demon.co.uk>  1996
**  All rights reserved.  For full licence details see file licences.c
*/
#ifndef _keyutils
#define _keyutils

#ifndef CTCKEY_DLL
#define CTCKEY_DLL
#endif

#include <ctype.h>
#include "cipher.h" /* need full definition of cv_details */
#include "pkautils.h"
#include "keyconst.h"
#include "keyhash.h"

#ifdef __cplusplus
NAMESPACE_CTCLIB
extern "C" {
#endif

    /*void compute_legit(username *id); */
    /*short keyLength(pubkey * pub_key); */
    /*uint32_t keyDate(pubkey * pub_key); */
    void formatKeyID(char text[IDPRINTSIZE], byte keyID[KEYFRAGSIZE]);
    void extract_keyID(byte keyID[KEYFRAGSIZE], pubkey * pub_key);
    boolean CTCKEY_DLL set_passphrase(seckey * sec_key, char * passphrase);
    boolean CTCKEY_DLL set_passphrase_UTF8(seckey * sec_key, char * passphrase,
    boolean convertToUTF8);
    void publicRingDirty(hashtable * key_ring);
    void secretRingDirty(hashtable * key_ring);

#ifdef __cplusplus
}
END_NAMESPACE
#endif
/*
** Convert to or from external byte order.
** Note that convert_byteorder does nothing if the external byteorder
**  is the same as the internal byteorder.
*/
#define CONVERT(x) (convert_byteorder((byte *)&(x), sizeof(x)))


#endif
