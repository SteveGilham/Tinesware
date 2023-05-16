/* keyhash.c
 **
 **  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 **  Heimdall <heimdall@bifroest.demon.co.uk>  1996
 **  All rights reserved.  For full licence details see file licences.c
 */
#include <assert.h>
#include <string.h>
#include "bignums.h"
#include "callback.h"
#include "hash.h"
#include "keyio.h"
#include "keyutils.h"
#include "keyhash.h"
#include "port_io.h"
#include "utils.h"
#include "pkcipher.h"
#include "usrbreak.h"

#define HASHSIZE 256 /* N.B. Before changing the hash table size if is essential
** to write a proper hash function. (Rather than the current
** just-take the first byte) */

/*typedef*/ struct hashtable_T
{
    DataFileP file;
    pubkey * lookup[HASHSIZE];
    seckey * secretKeys;
    boolean publicChanged;
    boolean secretChanged;
}/*hashtable*/
;

static pubkey * firstKey(hashtable * table, short offset)
{
    while(offset < HASHSIZE)
    {
        if(table->lookup[offset])
            return table->lookup[offset];
        else
            offset++;
    }
    return NULL;
}


pubkey * firstPubKey(hashtable * table)
{
    pubkey * result = firstKey(table, 0);

    if(result && result->status == KS_MISSING)
        return nextPubKey(result);
    else
        return result;
}


pubkey * nextPubKey(pubkey * last)
{
    pubkey * result = last;

    do
    {
        if(result->next_alias)
            result = result->next_alias;
        else
            result = firstKey(result->keyRing, (short)(result->keyId[0] + 1));
    }
    while(result && result->status == KS_MISSING);
    return result;
}

seckey * secretKey(pubkey * pub_key)
{
    seckey * sec_key = firstSecKey(pub_key->keyRing);

    while(sec_key)
    {
        if(sec_key->publicKey == pub_key) return sec_key;
        sec_key = nextSecKey(sec_key);
    }
    return NULL;
}

void setTrust(pubkey * pub_key, byte trust)
{
    pub_key->trust = trust;
    if(pub_key->keyRing) pub_key->keyRing->publicChanged = TRUE;
}

byte ownTrust(pubkey * pub_key) {
    return pub_key->trust;
}
seckey * firstSecKey(hashtable * table) {
    return table->secretKeys;
}
seckey * nextSecKey(seckey * last) {
    return last->next_in_file;
}
signature * revocation(pubkey * pub_key) {
    return pub_key->directSig;
}
pubkey * publicKey(seckey * sec_key) {
    return sec_key->publicKey;
}
Skstat keyStatus(seckey * sec_key) {
    return sec_key->skstat;
}
username * firstName(pubkey * pub_key) {
    return pub_key->userids;
}
username * nextName(username * name) {
    return name->next;
}
signature * firstSig(username * name) {
    return name->signatures;
}
signature * nextSig(signature * sig) {
    return sig->next;
}
boolean publicChanged(hashtable * table){
    return table->publicChanged;
}
boolean secretChanged(hashtable * table){
    return table->secretChanged;
}
pubkey * signatory(signature * sig) {
    return sig->from;
}
/*uint32_t creationDate(pubkey * pub_key) { return *(uint32_t*)pub_key->timestamp; } */
uint16_t validity(pubkey * pub_key) {
    return *(uint16_t*)pub_key->validity;
}
void publicRingDirty(hashtable * key_ring) {
    if(key_ring) key_ring->publicChanged = TRUE;
}
void secretRingDirty(hashtable * key_ring) {
    if(key_ring) key_ring->secretChanged = TRUE;
}
byte getPubkeyAlg(pubkey * pub_key) {
    return pub_key->pkalg;
}
byte getPubkeyVersion(pubkey * pub_key) {
    return pub_key->version;
}
pubkey * subKey(pubkey * pub_key) {
    return pub_key->subkeys;
}

static void fullCondition(short severity, short code, cb_context context,
char * text, pubkey * pub_key)
{
    cb_condition condition = {
        0, 0, 0, 0, NULL, NULL         };

    condition.severity = severity;
    condition.module = CB_PK_MANAGE;
    condition.code = code;
    condition.context = (short) context;
    condition.text = text;
    condition.pub_key = pub_key;
    cb_information(&condition);
}


static void simpleCondition(short severity, short code, cb_context context)
{
    fullCondition(severity, code, context, NULL, NULL);
}


boolean completeKey(pubkey * pub_key)
{
    byte type;
    hashtable * table = pub_key->keyRing;

    switch(pub_key->status)
    {
    case KS_MISSING:
        return FALSE;
    case KS_COMPLETE:
        return TRUE;
    case KS_ON_FILE:
        if(!table && pub_key->superkey) table = pub_key->superkey->keyRing;
        if(table && pub_key->fileOffset >= 0)
        {
            vf_setpos(table->file, pub_key->fileOffset);
            if(readkeypacket(table->file, &type, pub_key, NULL) == KIO_OKAY)
            {
                pub_key->status = KS_COMPLETE;
                return TRUE;
            }
            else
            {
                simpleCondition(CB_FATAL, KEY_RING_FAILURE, CB_UNKNOWN);
                return FALSE;
            }
        }

    default:
        simpleCondition(CB_FATAL, KEY_BAD_STATUS, CB_UNKNOWN);
    }
    return FALSE;
}


void incompleteKey(pubkey * pub_key)
{
    if(pub_key->fileOffset >= 0 && pub_key->status == KS_COMPLETE)
    {
        release_pubkey(pub_key);
        pub_key->status = KS_ON_FILE;
    }
}

static boolean completeSig(hashtable * keyRings, signature * sig)
{
    if(!sig->details)
    {
        sig->details = (sigDetails*)zmalloc(sizeof(sigDetails));
        if(readSKEpacket(keyRings->file, sig->fileOffset, sig->details) != KIO_OKAY)
        {
            qfree(sig->details);
            sig->details = NULL;
            return FALSE;
        }
    }
    sig->details->pub_key = sig->from;
    return TRUE;
}

static void incompleteSig(signature * sig)
{
    if(sig->fileOffset > 0 && sig->details)
    {
        release_signature(sig->details);
        qfree(sig->details);
        sig->details = NULL;
    }
}


/* only returns true if sure the keys are the same */
boolean sameKey(pubkey * right, pubkey * left)
{
    return (boolean)(right->status == KS_COMPLETE &&
        left->status == KS_COMPLETE &&
        equate_pubkey(right, left) );
}


pubkey * key_from_keyID(hashtable * table, byte * keyID)
{
    pubkey * pub_key;

    pub_key = table->lookup[*keyID];
    while(pub_key)
    {
        if(!memcmp(pub_key->keyId, keyID, KEYFRAGSIZE))
            return pub_key;
        pub_key = pub_key->next_alias;
    }
    return NULL;
}


seckey * seckey_from_keyID(hashtable * keyRings, byte * keyID)
{
    seckey * sec_key = keyRings->secretKeys;

    while(sec_key)
    {
        if(!memcmp(sec_key->publicKey->keyId, keyID, KEYFRAGSIZE))
            return sec_key;
        sec_key = sec_key->next_in_file;
    }
    return NULL;
}


void name_from_key(pubkey * key, char name[256])
{
    if(key->userids)
        text_from_name(key->keyRing, key->userids, name);
    else
        formatKeyID(name, key->keyId);
}


void text_from_name(hashtable * table, username * nameRec, char name[256])
{
    if(nameRec)
    {
        if(nameRec->fileOffset > 0 && table)
        {
            readusername(table->file, nameRec->fileOffset, name);
            return;
        }
        else if(nameRec->text)
        {
            strcpy(name, nameRec->text);
            return;
        }
    }
    strcpy(name, "<unknown>");
}


static signature * new_signature(pubkey * pub_key, username * name, long where)
{/* was uint32_t but is called with -1 ^^^^ */
    signature * result;

    result = (signature *)zmalloc(sizeof(signature));
    if(result)
    {
        if(name)
        {
            result->next = name->signatures;
            name->signatures = result;
        }
        result->from = pub_key;
        result->trust = MAX_DEPTH;
        result->fileOffset = where;
    }
    return result;
}

static void free_signature(signature * sig)
{
    if(sig)
    {
        if(sig->next) free_signature(sig->next);
        if(sig->details)
        {
            release_signature(sig->details);
            qfree(sig->details);
        }
        qfree(sig);
    }
}


static username * new_username(pubkey * pub_key, username * previous,
long where)/* was uint32_t but is called with -1 */
{
    username * result;

    result = (username *)zmalloc(sizeof(username));
    if(result)
    {
        /* add to end of list of names to maintain order */
        if(previous)
            previous->next = result;
        else
            pub_key->userids = result;        /* first in chain */
        result->fileOffset = where;
        result->trust = MAX_DEPTH;
    }
    return result;
}

static void free_username(username * name)
{
    if(name)
    {
        if(name->next) free_username(name->next);
        free_signature(name->signatures);
        if(name->text) qfree(name->text);
        qfree(name);
    }
}

static pubkey * alloc_pubkey(byte keyID[KEYFRAGSIZE])
{
    pubkey * result;

    result = (pubkey *)zmalloc(sizeof(pubkey));
    if(result)
    {
        /* N.B. zmalloc used so only non-zero fields need explicit initialisation */
        result->version = DEFAULT_VERSION;

        /* we don't have anywhere to read from (and may not, if this is entered
         *              in response to a signature by a key we cam't get and so isn't in the ring */
        result->fileOffset = -1;
        result->status = KS_MISSING;

        /* field initialisation needed in case NULL is not zero */
        /*init_mpn(&result->pkdata.nums[RSAPUB_N].);
         *                  init_mpn(&result->pkdata.nums[RSAPUB_E]);*/
        /* Use prepare_pubkey() later when algorithm is known */

        result->depth = MAX_DEPTH;
        memcpy(result->keyId, keyID, KEYFRAGSIZE);
    }
    return result;
}

/* 1999-08-30 ISM : To allow safe export of this function;
 *     modified to remove the key from a key ring if it is in one.*/
void free_pubkey(pubkey * * pub)
{
    if(*pub)    /* allow NULL pointer calls */
    {
        pubkey * key = *pub;
        if(key->keyRing) removePubkey(key->keyRing, key);
        free_username(key->userids);
        free_signature(key->directSig);
        free_pubkey(&key->subkeys);
        release_pubkey(key);
        qfree(key);
        *pub = NULL;
    }
}

#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (disable: 4127) // conditional expression is constant
#endif

/* Note this routine returns true if it has modified the keyRing */
static boolean mergeSig(username * baseName, signature * newSig)
{
    signature * baseSig = baseName->signatures;

    if(!baseSig)
    {    /* no existing signatures so this has to be the first; just add it */
        baseName->signatures = newSig;
        return TRUE;
    }
    else while(TRUE)
    {
        if(baseSig->from == newSig->from)
        {
            free_signature(newSig);
            return FALSE;            /* already got this one */
        }
        else if(!baseSig->next)
        {
            baseSig->next = newSig;
            return TRUE;
        }
        else
        {
            baseSig = baseSig->next;
        }
    }
}

#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (default: 4127) // conditional expression is constant
#endif

static boolean mergeSigs(username * baseName, username * addName)
{
    signature * curr = addName->signatures;
    signature * next;
    boolean changed = FALSE;

    while(curr)
    {
        next = curr->next;
        curr->next = NULL;        /* Remove from old chain */
        if(mergeSig(baseName, curr)) changed = TRUE;
        curr = next;
    }
    return changed;
}


#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (disable: 4127) // conditional expression is constant
#endif

/* Note this routine is responsible for setting ->publicChanged if appropriate */
static void mergeName(hashtable * keyRings, pubkey * basekey, username * addName)
{
    username * baseName = basekey->userids;

    if(!baseName)
    {    /* No existing names => new name is whole list */
        basekey->userids = addName;
        keyRings->publicChanged = TRUE;
    }
    else
    {
        char addText[256];
        char baseText[256];

        text_from_name(keyRings, addName, addText);
        while(TRUE)
        {
            text_from_name(keyRings, baseName, baseText);
            if(!strcmp(addText, baseText))
            {            /* pre-existing name => merge signature */
                if(mergeSigs(baseName, addName)) keyRings->publicChanged = TRUE;
                addName->signatures = NULL;
                free_username(addName);
                return;
            }
            else if(!baseName->next)
            {
                baseName->next = addName;
                keyRings->publicChanged = TRUE;
                return;
            }
            else
                baseName = baseName->next;
        }
    }
}
#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (default: 4127) // conditional expression is constant
#endif


static void mergeNames(hashtable * keyRings, pubkey * basekey, pubkey * addkey)
{
    username * curr = addkey->userids;
    username * next;

    while(curr)
    {
        next = curr->next;
        curr->next = NULL;        /* Remove from old chain */
        mergeName(keyRings, basekey, curr);
        curr = next;
    }
}


boolean insertPubkey(hashtable * keyRings, pubkey * * pub_key)
{
    pubkey * oldkey = key_from_keyID(keyRings, (*pub_key)->keyId);

    if(*pub_key == oldkey)
        return TRUE;    /* attempt to insert a key already there; ignore */
    else if((*pub_key)->keyRing)
        return FALSE;    /* Already in a different key ring */
    else if(*pub_key == NULL)
        return FALSE;    /* attempt to insert a null key; ignore */
    else if(!oldkey)
    {    /* new key; simple insertion */
        byte offset = (*pub_key)->keyId[0];        /* trivial hash function; taken the first byte! */

        (*pub_key)->next_alias = keyRings->lookup[offset];
        (*pub_key)->keyRing = keyRings;
        keyRings->lookup[offset] = *pub_key;
        keyRings->publicChanged = TRUE;
        return TRUE;
    }
    else if(oldkey->status == KS_MISSING)
    {    /* we must keep the 'oldkey' record as there are pointers to it */

        oldkey->version = (*pub_key)->version;
        oldkey->userids = (*pub_key)->userids; 
        (*pub_key)->userids = NULL;
        oldkey->directSig = ((*pub_key)->directSig);
        oldkey->subkeys = ((*pub_key)->subkeys);
        oldkey->size = (*pub_key)->size;
        oldkey->trust = (*pub_key)->trust;
        oldkey->depth = (*pub_key)->depth;
        oldkey->fileOffset = (*pub_key)->fileOffset;
        oldkey->status = (*pub_key)->status;
        oldkey->pkalg = (*pub_key)->pkalg;
        move_pubkey(oldkey, (*pub_key));
        memcpy(oldkey->timestamp, (*pub_key)->timestamp, SIZEOF_TIMESTAMP);
        free_pubkey(pub_key);
        *pub_key = oldkey;
        keyRings->publicChanged = TRUE;
        return TRUE;
    }
    else
        /* two real keys; check they really are the same */
    {
        if(!completeKey(oldkey))
        {
            simpleCondition(CB_FATAL, KEY_RING_FAILURE, CB_UNKNOWN);
            return FALSE;
        }
        if(!sameKey(oldkey, *pub_key))
        {
            char text[256];

            name_from_key(oldkey, text);
            fullCondition(CB_ERROR, KEY_DEADBEEF, CB_UNKNOWN, text, *pub_key);
            return FALSE;
        }
        /* The following assumes (and maintains) only a single signature.
         **  This is probably not the correct behaviour. */
        if((*pub_key)->directSig != NULL)
        {
            oldkey->directSig = (*pub_key)->directSig;
            (*pub_key)->directSig = NULL;
            keyRings->publicChanged = TRUE;
        }
        /* The following assumes (and maintains) only a single sub-key.
         **  This is probably not the correct behaviour. */
        if((*pub_key)->subkeys != NULL)
        {
            oldkey->subkeys = (*pub_key)->subkeys;
            oldkey->subkeys->superkey = oldkey;
            (*pub_key)->subkeys = NULL;            /* prevent deallocation of subkeys with pub_key */
            keyRings->publicChanged = TRUE;
        }
        mergeNames(keyRings, oldkey, *pub_key);        /* sets ->publicChanged if appropriate */
        (*pub_key)->userids = NULL;
        free_pubkey(pub_key);
        *pub_key = oldkey;
        return TRUE;
    }
}


void removePubkey(hashtable * keyRings, pubkey * pub_key)
{
    byte offset = pub_key->keyId[0];

    /* remove from alias chain */
    if(keyRings->lookup[offset] == pub_key)
    {
        keyRings->lookup[offset] = pub_key->next_alias;
        pub_key->keyRing = NULL;
        pub_key->next_alias = NULL;
        keyRings->publicChanged = TRUE;
    }
    else
    {
        pubkey * previous = keyRings->lookup[offset];

        while(previous)
        {
            if(previous->next_alias == pub_key)
            {
                previous->next_alias = pub_key->next_alias;
                previous = NULL;                /* to terminate loop */
                pub_key->keyRing = NULL;
                pub_key->next_alias = NULL;
                keyRings->publicChanged = TRUE;
            }
            else
                previous = previous->next_alias;
        }
    }
}


boolean insertSeckey(hashtable * keyRings, seckey * sec_key)
{
    /* first ensure that the public key is in the public ring */
    if(insertPubkey(keyRings, &sec_key->publicKey))
    {    /* Then proceed only if the secret key is new */
        if(!seckey_from_keyID(keyRings, sec_key->publicKey->keyId))
        {
            sec_key->next_in_file = keyRings->secretKeys;
            keyRings->secretKeys = sec_key;
            keyRings->secretChanged = TRUE;
        }
        return TRUE;
    }
    else
        return FALSE;
}


void removeSeckey(hashtable * keyRings, seckey * sec_key)
{
    /* remove from alias chain */
    if(keyRings->secretKeys == sec_key)
    {
        keyRings->secretKeys = sec_key->next_in_file;
        sec_key->next_in_file = NULL;
        keyRings->secretChanged = TRUE;
    }
    else
    {
        seckey * previous = keyRings->secretKeys;

        while(previous)
        {
            if(previous->next_in_file == sec_key)
            {
                previous->next_in_file = sec_key->next_in_file;
                sec_key->next_in_file = NULL;
                keyRings->secretChanged = TRUE;
                previous = NULL;                /* to terminate loop */
            }
            else
                previous = previous->next_in_file;
        }
    }
}


username * addUsername(pubkey * key, char * text)
{
    username * result;
    username * previous = key->userids;

    if(strlen(text) > 255) return NULL;
    if(previous)
        while(previous->next) previous = previous->next;
    result = new_username(key, previous, -1);
    if(result)
    {
        if(key->keyRing) key->keyRing->publicChanged = TRUE;
        result->text = qmalloc(strlen(text) + 1);
        if(result->text)
            strcpy(result->text, text);
        else
        {
            removeUsername(key, result);
            return NULL;
        }
    }
    return result;
}


void removeUsername(pubkey * key, username * userid)
{
    username * previous = key->userids;

    if(previous == userid)
    {    /* first username => remove from head of chain */
        key->userids = userid->next;
    }
    else
    {
        while(previous && previous->next != userid) previous = previous->next;
        if(previous)
        {
            previous->next = userid->next;
        }
        else
            return;        /* wrong pubkey?? */
    }
    if(key->keyRing) key->keyRing->publicChanged = TRUE;
    userid->next = NULL;    /* prevent free_username from deleting rest of chain */
    free_username(userid);
}


static void keyNameHash(byte * digest, sigDetails * details, pubkey * pub_key, username * name)
{
    byte buffer[256];
    md_context context;

    context = hashInit(details->md_algor);
    if(context && details->sigClass == SIG_SUBKEY_CERT && pub_key->superkey)
        keyHashUpdate(pub_key->superkey, context);
    if(context && keyHashUpdate(pub_key, context))
    {
        if(name)
        {
            hashtable * keyRings = pub_key->keyRing;
            size_t length;

            text_from_name(keyRings, name, (char*)buffer);
            length = strlen((char*)buffer);
            if(details->version > VERSION_2_6)
            {
                byte lenField[5] = { 
                    CTB_DESIGNATOR + (CTB_USERID << 2), 0,0,0,0                                };

                lenField[3] = (byte)(length >> 8);
                lenField[4] = (byte)(length & 0xff);
                hashUpdate(context, lenField, sizeof(lenField));
            }
            hashUpdate(context, buffer, (uint32_t) length);
        }
        hashUpdate(context, details->digestBytes, (uint32_t)details->lenDigestBytes);
        hashFinal(&context, digest);
    }
}

/* For a revocation signature userid should be NULL */
/* N.B. This cannot add a subkey signature (yet).  This release does not
 ** handle secret keys for algorithms requiring sub-keys. */
signature * addSignature(pubkey * key, username * userid, byte sigType,
seckey * signing, byte hashAlg)
{
    signature * result;
    pubkey * verify;

    /* ensure we have the key we're going to sign with all to hand */
    if(!signing) return NULL;
    if(signing->skstat != INTERNAL) return NULL;
    verify = publicKey(signing);
    if(!completeKey(verify)) return NULL;

    if(!completeKey(key)) return NULL;
    if(!userid && key->directSig) return NULL;
    /* new_signature will add a username signature to the right list
     ** however it lacks the key being revoked for a revocation signature */
    result = new_signature(verify, userid, -1);
    if(result)
    {
        sigDetails * details = (sigDetails *)zmalloc(sizeof(sigDetails));
        uint32_t timestamp = (long)difftime(time(NULL), datum_time());

        if(!userid) key->directSig = result;
        result->details = details;
        details->version = DEFAULT_VERSION;
        details->sigClass = sigType;
        details->pk_algor = signing->publicKey->pkalg;
        details->md_algor = hashAlg;
        details->timestamp = timestamp;
        details->digestBytes[0] = sigType;
        CONVERT(timestamp);
        memcpy(&details->digestBytes[1], &timestamp, SIZEOF_TIMESTAMP);
        details->lenDigestBytes = V2DIGESTEXTRAS;
        memcpy(details->keyId, verify->keyId, KEYFRAGSIZE);
        details->pub_key = key;
        keyNameHash(details->digest, details, key, userid);
        memcpy(details->checkBytes, details->digest, sizeof(details->checkBytes));
        putSignature(details, signing);
    }
    switch(checkSignature(key, userid, result))
    {
    case SIG_OKAY:
        break;
    default:
        bug_check("Cannot check new signature");
    }
    if(key->keyRing) key->keyRing->publicChanged = TRUE;
    return result;
}


boolean revoke(seckey * sec_key)
{
    pubkey * pub_key = publicKey(sec_key);
    signature * sig = addSignature(pub_key, NULL,
    SIG_KEY_COMPROM, sec_key, MDA_MD5);
    /* so if the signing key is RSA, then PGP-classic users should be able */
    /* to comprehend this vital message ! If we were fancy, we could make  */
    /* SHA-1 the default for non-RSA keys */

    if(pub_key->keyRing) pub_key->keyRing->publicChanged = TRUE;
    return (boolean)(sig != NULL);
}


void unrevoke(pubkey * pub_key)
{
    if(pub_key->directSig)
    {
        if(pub_key->keyRing) pub_key->keyRing->publicChanged = TRUE;
        free_signature(pub_key->directSig);
        pub_key->directSig = NULL;
    }
}

void removeSignature(pubkey * key, username * userid, signature * sig)
{
    if(key && sig == key->directSig)
        key->directSig = NULL;
    else if(userid && sig == userid->signatures)
        userid->signatures = sig->next;
    else if(userid)
    {
        signature * previous = userid->signatures;

        while(previous && previous->next != sig) previous = previous->next;
        if(previous)
        {
            previous->next = sig->next;
        }
        else
            return;        /* signature not correctly identified */
    }
    else
        return;    /* signature not correctly identified */

    if(key->keyRing) key->keyRing->publicChanged = TRUE;
    sig->next = NULL;
    free_signature(sig);
}


static seckey * alloc_seckey(byte keyID[KEYFRAGSIZE])
{
    seckey * result = NULL;
    pubkey * pub_key = alloc_pubkey(keyID);

    if(pub_key)
    {
        result = (seckey *)zmalloc(sizeof(seckey));
        if(result)
        {
            /* do prepare_seckey() when algorithm known */
            /*init_mpn(&result->pkdata.nums[RSASEC_D].plain);
             *                  init_mpn(&result->pkdata.nums[RSASEC_P].plain);
             *                  init_mpn(&result->pkdata.nums[RSASEC_Q].plain);
             *                  init_mpn(&result->pkdata.nums[RSASEC_U].plain); */
            result->publicKey = pub_key;
        }
        else
            free_pubkey(&pub_key);
    }
    return result;
}


static size_t findSubpacket(byte target, byte * buffer, long length,
byte * result, size_t maxResult)
{
    byte * ptr = buffer;
    byte * end = buffer + (size_t) length;
    size_t len;

    while(ptr < end)
    {
        len = *ptr - 1;        /* length includes type byte but not length byte */
        if(*(ptr + 1) == target)
        {
            if(len > maxResult) len = maxResult;
            memcpy(result, ptr + 2, len);
            return len;
        }
        ptr += len + 2;
    }
    return 0;
}


uint32_t CTCKEY_DLL getSignatureValue(signature * sig, int valueKey, hashtable * keyRings)
{
    if(!sig)
        return ~((uint32_t)0);
    else if(valueKey == SIG_STATUS)
        return sig->sigStat;
    else if(valueKey == SIG_TRUST)
        return sig->trust;
    else if(!sig->details && !completeSig(keyRings, sig))
        return ~((uint32_t)0);
    else
    {
        switch(valueKey)
        {
        case SIG_VERSION:
            return sig->details->version;
        case SIG_CLASS:
            return sig->details->sigClass;
        case SIG_PKALG:
            return sig->details->pk_algor;
        case SIG_MDALG:
            return sig->details->md_algor;
        case SIG_DATETIME:
            return sig->details->timestamp;
        }
        return ~((uint32_t)0);
    }
}


size_t CTCKEY_DLL getSignatureArray(signature * sig, int valueKey,
byte * value, size_t maxSize, hashtable * keyRings)
{
    if(sig && completeSig(keyRings, sig))
    {
        sigDetails * detail = sig->details;
        if(detail)
            return findSubpacket((byte) valueKey,
            &detail->digestBytes[6], detail->lenDigestBytes - 12L,
            value, maxSize);
    }
    return 0;
}


void free_seckey(seckey * * sec)
{
    if(*sec)
    {
        seckey * key = *sec;
        if(!key || !key->publicKey) return;
        if(key->publicKey->keyRing) removeSeckey(key->publicKey->keyRing, key);
        release_seckey(key);
        if(!key->publicKey->keyRing)        /* Free public key iff it is not in a hash-table */
            free_pubkey(&key->publicKey);
        qfree(key);
        *sec = NULL;
    }
}

static void free_secring(seckey * * sec)
{
    /* recursively wipe a secring chain */
    if(*sec)
    {
        free_secring(&(*sec)->next_in_file);
        free_seckey(sec);
    }
}

static void free_aliases(pubkey * * pub)
{
    if(*pub)
    {
        free_aliases(&(*pub)->next_alias);
        free_pubkey(pub);
    }
}


static void free_userhash(hashtable * keyRings)
{
    short offset = 0;

    free_secring(&keyRings->secretKeys);
    while(offset < HASHSIZE)
        free_aliases(&keyRings->lookup[offset++]);
    qfree(keyRings);
}

void destroy_userhash(hashtable * keyRings)
{
    if(NULL != keyRings->file)
    {
        vf_close(keyRings->file);
    }
    free_userhash(keyRings);
}


/* checkSigs checks if any of the signatures in the chain started by 'sig'
 ** are self-signatures on 'mainkey' identifying 'subkey' as a key-recovery key.
 **  If so, it destroys 'subkey' and returns FALSE.
 **  Otherwise it returns TRUE without side-effects. */
static boolean checkSigs(hashtable * keyRings, pubkey * subkey, pubkey * mainkey, signature * sig)
{
    while(sig)
    {
        completeSig(keyRings, sig);
        if(signatory(sig) == mainkey)        /* check if it is a self-signature */
        {
            /* for every self-signature search for a key-recovery subpacket */
            sigDetails * details = sig->details;
            byte * ptr = details->digestBytes + 6;
            byte * end = ptr + details->lenDigestBytes - 12;
            int len;

            while(ptr < end)
            {
                len = *ptr - 1;                /* length includes type byte but not length byte */
                if(*(ptr+1) == SUBPKT_KEY_RECOVERY_KEY)
                {
                    if(memcmp(ptr+2, subkey->keyId, KEYFRAGSIZE))
                    {
                        char text[IDPRINTSIZE];

                        formatKeyID(text, subkey->keyId);
                        fullCondition(CB_WARNING, KEY_RECOVERY_KEY, CB_DECRYPTION, text, mainkey);
                        qfree(subkey);                        /* This isn't really the right function to do this but nothing
                         ** better is exported from the lower levels.  */
                        return FALSE;
                    }
                }
                ptr += len + 2;
            }
        }
        incompleteSig(sig);
        sig = nextSig(sig);
    }
    return TRUE;
}

/* checkSubkeys checks if the subkey is a keyRecoveryKey of mainkey
 **   if it is returns the next non-keyRecoveryKey subkey or NULL if none.
 **   If the subkey is non-keyRecovery it returns it.
 **   If subkey is NULL, it returns NULL.
 */
pubkey * checkSubkeys(hashtable * keyRings, pubkey * subkey, pubkey * mainkey)
{
    username * userid;
    pubkey * fallback;    /* result if subkey is for key-recovery */

    if(!subkey) return NULL;
    /* recursively call self to eliminate any following keyRecoveryKeys */
    fallback = subkey->subkeys = checkSubkeys(keyRings, subkey->subkeys, mainkey);
    if(!checkSigs(keyRings, subkey, mainkey, revocation(subkey)))
        return fallback;
    if(!checkSigs(keyRings, subkey, mainkey, revocation(mainkey)))
        return fallback;
    userid = firstName(mainkey);
    while(userid)
    {
        if(!checkSigs(keyRings, subkey, mainkey, firstSig(userid)))
            return fallback;
        userid = nextName(userid);
    }
    return subkey;
}


static void processFinished(keyringContext * context)
{
    pubkey * mainkey = context->last_mainkey;

    if(mainkey) mainkey->subkeys = checkSubkeys(context->keyRings, mainkey->subkeys, mainkey);
    if(context->last_seckey)
    {
        insertSeckey(context->keyRings, context->last_seckey);
        context->last_seckey = NULL;
        context->last_key = NULL;
        context->last_mainkey = NULL;
    }
    else if(context->last_mainkey)
    {
        insertPubkey(context->keyRings, &context->last_mainkey);
        context->last_key = NULL;
        context->last_mainkey = NULL;
    }
}

/* Read a complete public keyring and return it as a hash table */
/* N.B. This routine takes over responsibility for the DataFile */
/*      object.                                                 */
#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (disable: 4701)
#endif
hashtable * init_userhash(DataFileP file)
{
    keyringContext context = { 
        NULL         };
    recordSummary summary;
    keyio_error retCode;
    long nextRecord = 0;

    context.keyRings = (hashtable *)zmalloc(sizeof(hashtable));
    context.keyRings->file = file;
    while(vf_setpos(file, nextRecord) &&
        (retCode = readsummary(file, &summary)) == KIO_OKAY)
    {
        nextRecord = summary.next;
        if(summary.type == CTB_CERT_PUBKEY || summary.type == CTB_CERT_SECKEY)
            processFinished(&context);
        if(CB_CONTINUE != keyringPacket(file, &summary, &context))
        {
            free_userhash(context.keyRings);
            return NULL;
        }
    }
    if(retCode != KIO_OKAY && retCode != KIO_EOF)
    {
        cb_condition condition = { 
            CB_ERROR, CB_CTB_IO, 0, CB_READING_RING, NULL, NULL                 };
        condition.code = (short)retCode;
        cb_information(&condition);
    }
    processFinished(&context);
    context.keyRings->publicChanged = FALSE;
    return context.keyRings;
}
#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (default: 4701)
#endif


boolean read_secring(DataFileP file, hashtable * keyRings)
{
    keyringContext context = { 
        NULL         };
    recordSummary summary;
    long nextRecord = 0;

    context.keyRings = keyRings;
    while(vf_setpos(file, nextRecord) && !readsummary(file, &summary))
    {
        nextRecord = summary.next;
        if(summary.type == CTB_CERT_PUBKEY || summary.type == CTB_CERT_SECKEY)
            processFinished(&context);
        if(CB_CONTINUE != keyringPacket(file, &summary, &context))
            return FALSE;
    }
    processFinished(&context);
    keyRings->secretChanged = FALSE;
    return TRUE;
}


/* Note that keyRings->file may or may not equal input depending on whether
 **   we are reading the public key ring or some other file (secret ring included).
 **   The action may accordingly vary slightly */
continue_action keyringPacket( DataFileP input, recordSummary * summary,
keyringContext * context)
{
    long fileOffset = (input == context->keyRings->file) ? summary->position : -1;

    switch(summary->type)
    {
    case CTB_CERT_PUBKEY:
    case CTB_PUB_SUBKEY:
        {
            pubkey * key = alloc_pubkey(summary->itemID);
            byte ignored;

            if(!key) return CB_ABORT;            /*!! ought to report an error */
            if(input != context->keyRings->file || summary->version > VERSION_2_6)
            {
                /* It is not in the key-ring or it is a version3 packet; in either case
                 ** we must read it in its entirety now */
                vf_setpos(input, summary->position);
                if(KIO_OKAY != readkeypacket(input, &ignored, key, NULL))
                    return CB_SKIP;

                if(input != context->keyRings->file)
                {
                    char hexId[IDPRINTSIZE];

                    formatKeyID(hexId, key->keyId);
                    fullCondition(CB_INFO, KEY_PUBKEY_FOUND, CB_DECRYPTION, hexId, NULL);
                }
                else
                    /* if we were reading it only to obtain the keyId. we may now discard the rest */
                    incompleteKey(key);
            }
            else
                key->status = KS_ON_FILE;
            key->fileOffset = fileOffset;
            key->size = summary->size;
            if(summary->type == CTB_CERT_PUBKEY)
            {
                context->last_key = key;
                context->last_mainkey = key;
            }
            else if(summary->type == CTB_PUB_SUBKEY)
            {
                context->last_mainkey->subkeys = key;
                context->last_key = key;
                key->superkey = context->last_mainkey;
            }
            context->last_name = NULL;
            context->last_trust = &key->trust;
            break;
        }

    case CTB_CERT_SECKEY:
        {
            seckey * sec_key = alloc_seckey(summary->itemID);
            byte type;

            if(!sec_key) break;
            vf_setpos(input, summary->position);
            if(KIO_OKAY == readkeypacket(input, &type, sec_key->publicKey, sec_key))
            {
                if(input != context->keyRings->file)
                {
                    char hexId[IDPRINTSIZE];

                    formatKeyID(hexId, sec_key->publicKey->keyId);
                    fullCondition(CB_INFO, KEY_SECKEY_FOUND, CB_DECRYPTION, hexId, NULL);
                }
                context->last_seckey = sec_key;
                context->last_key = context->last_mainkey = sec_key->publicKey;
                context->last_name = NULL;
                context->last_trust = &context->last_mainkey->trust;

            }
            else
                free_seckey(&sec_key);

            break;
        }

    case CTB_USERID:
        if(context->last_mainkey)
        {
            username * nameRec = new_username(context->last_mainkey,
            context->last_name,
            fileOffset);
            if(nameRec)
            {
                if(input != context->keyRings->file)
                {
                    nameRec->text = qmalloc((size_t)summary->length + 1);

                    if(!nameRec) return CB_SKIP;
                    readusername(input, summary->position, nameRec->text);
                    fullCondition(CB_INFO, KEY_USERID_FOUND, CB_DECRYPTION, nameRec->text, NULL);
                }
                context->last_name = nameRec;
                context->last_trust = &context->last_name->trust;
            }
        }
        break;

    case CTB_SKE:
        {
            signature * sig;

            if(context->last_key)            /* We can only sensibly process a key signature if we have
             ** a key for it to refer to. */
            {
                pubkey * signing_key = key_from_keyID(context->keyRings, summary->itemID);

                if(!signing_key || !completeKey(signing_key))
                {
                    /* Signature key not (yet) in key-ring; check if its the key being signed */
                    if(!memcmp(context->last_mainkey->keyId, summary->itemID, KEYFRAGSIZE))
                    {
                        signing_key = context->last_mainkey;                        /* Self-signature */
                    }
                    else if(!signing_key)
                    {
                        signing_key = alloc_pubkey(summary->itemID);
                        if(!signing_key) return CB_SKIP;
                        /* empty placeholder are ALWAYS put in the hash table */
                        insertPubkey(context->keyRings, &signing_key);
                    }
                }

                sig = new_signature(signing_key, context->last_name, fileOffset);
                if(input != context->keyRings->file)
                {
                    char * sigType;
                    sigValid validity;

                    sig->details = (sigDetails*)zmalloc(sizeof(sigDetails));
                    if(readSKEpacket(input, summary->position, sig->details) != KIO_OKAY)
                    {
                        qfree(sig->details); 
                        sig->details = NULL; 
                        return CB_SKIP;
                    }

                    sigType = enumName(SIGCLASS, sig->details->sigClass, TRUE);
                    validity = checkSignature(context->last_key, context->last_name, sig);
                    if(!context->last_name)
                    {                    /* No name so it is a revocation or a subkey certificate (or we don't recognise it) */
                        if(sig->details->sigClass != SIG_KEY_COMPROM &&
                            sig->details->sigClass != SIG_SUBKEY_CERT)
                        {
                            fullCondition(CB_ERROR, KEY_NO_USERID, CB_DECRYPTION, sigType, signing_key);
                            free_signature(sig);
                            break;
                        }
                        /* Revocation (self-signature); the key will probably not yet be in the keyring
                         ** however it should be in context->last_key */
                        else if(memcmp(summary->itemID, signing_key->keyId, KEYFRAGSIZE))
                        {
                            /* revoked with the wrong key! */
                            fullCondition(CB_WARNING, KEY_WRONG_REVOKE, CB_DECRYPTION, sigType, signing_key);
                        }
                        switch(validity)
                        {
                        case SIG_OKAY:
                            context->last_key->directSig = sig;
                            context->last_key->trust = KTB_ENABLE_DISABLE;
                            fullCondition(CB_INFO, KEY_KEYSIG_FOUND, CB_DECRYPTION, sigType, signing_key);
                            break;

                        case SIG_ERROR:                            /* Process of checking failed (status of key dubious) */
                            context->last_key->trust = KTB_ENABLE_DISABLE;
                            fullCondition(CB_WARNING, KEY_UNCK_REVOKE, CB_DECRYPTION, sigType, signing_key);
                            free_signature(sig);
                            break;

                        default:
                            fullCondition(CB_ERROR, KEY_BAD_REVOKE, CB_DECRYPTION,
                            sigType, signing_key);
                            free_signature(sig);
                            break;
                        }
                    }
                    else
                    {
                        switch(validity)
                        {
                        case SIG_OKAY:
                            fullCondition((short) CB_INFO, (short) KEY_KEYSIG_FOUND,
                            CB_DECRYPTION, sigType, signing_key);
                            break;

                        case SIG_BAD:
                            fullCondition((short) CB_ERROR, (short) KEY_BADSIG_FOUND,
                            CB_DECRYPTION, sigType, signing_key);
                            break;

                        default:
                            fullCondition((short) CB_WARNING, (short) KEY_NOKEY_SIG_FND,
                            CB_DECRYPTION, sigType, signing_key);
                        }
                    }
                }
                context->last_trust = &sig->trust;
                /*
                 * Handle direct signatures i.e. revoke
                 * This assumes only 1 signature; new 7-jan-01
                 */
                if(!context->last_name)
                {
                    if(context->last_key &&
                        !context->last_key->directSig)
                    {
                        context->last_key->directSig = sig;
                    }
                    else if(context->last_key &&
                        context->last_key->directSig == sig)
                    {
                        /* no-op - case of revocation being read from a
                           separate file passes through here */
                        ;
                    }
                    else
                    {
                        free_signature(sig);
                        sig = NULL;
                        context->last_trust = NULL;
                    }
                }
            }
            break;
        }

    case CTB_KEYCTRL:
        if(context->last_trust)
        {
            *context->last_trust = summary->trust;
            context->last_trust = NULL;
        }
        break;
    }
    return CB_CONTINUE;
}

/* parameter "table" not used - suppress message */
#ifdef __BORLANDC__
#pragma warn -par
#endif
#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (disable: 4100) // unref formal parameter
#endif

seckey * makePKEkey(hashtable * table, keyType * keyType)
{
    long timestamp = (long)difftime(time(NULL), datum_time());
    seckey sec_key;
    pubkey pub_key;
    pubkey * hPub_key;
    seckey * hSec_key;
    username * user_name;
    /*signature * sig;*/

    /* Clear - not needed but makes debugging easier */
    memset(&sec_key, 0, sizeof(sec_key));
    memset(&pub_key, 0, sizeof(pub_key));

    /* Set the key format to generate
     * Is this the right place??  Certainly
     * it is the only generator format we support as yet!
     */
    pub_key.version = VERSION_2_6;
    if(!newPKAkey(keyType, &sec_key, &pub_key)) return NULL;

    /* make connections at once for public key algorithm */
    pub_key.pkalg = keyType->algorithm;
    sec_key.publicKey = &pub_key;

    extractKeyfrag(&pub_key);
    hSec_key = alloc_seckey(pub_key.keyId);
    hPub_key = hSec_key->publicKey;

    hPub_key->pkalg = keyType->algorithm;
    hPub_key->trust = KTB_BUCKSTOP_TRUE;
    hPub_key->depth = 0;
    hPub_key->fileOffset = -1;
    hPub_key->status = KS_COMPLETE;
    hPub_key->size = pub_key.size;
    assign_pubkey(hPub_key, &pub_key);
    memcpy(hPub_key->timestamp, &timestamp, SIZEOF_TIMESTAMP);

    assign_seckey(hSec_key, &sec_key);
    hSec_key->skstat = INTERNAL;

    sec_key.kpalg.cv_algor = keyType->kpAlg;
    sec_key.kpalg.cv_mode = CEM_CFB;

    hSec_key->kpalg = sec_key.kpalg;
    hSec_key->hashalg = sec_key.hashalg;    /* default MDA_MD5 */

    set_passphrase(hSec_key, keyType->passphrase);
    user_name = addUsername(hPub_key, keyType->name);

    if(keyType->selfSignAlg)
        /*sig =*/(void) addSignature(hPub_key, user_name, SIG_KEY_CERT,
        hSec_key, keyType->selfSignAlg);

    return hSec_key;
}
#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (default: 4100) // unref formal parameter
#endif

#ifdef __BORLANDC__
#pragma warn .par
#endif

sigValid checkSignature(pubkey * pub_key, username * name, signature * sig)
{
    hashtable * keyRings = pub_key->keyRing;
    /* byte  algor = sig->details->md_algor;  not hardcoded MDA_MD5! */
    sigValid result = SIG_ERROR;

    /* First check we have the keys for this operation */
    if(!completeKey(sig->from) || !completeKey(pub_key))
        return SIG_NO_KEY;
    completeSig(keyRings, sig);
    if(getSignature(sig->details))
    {
        byte digest[MAXHASHSIZE];

        keyNameHash(digest, sig->details, pub_key, name);
        if(verifySignature(sig->details, digest))
        {
            result = SIG_OKAY;
            sig->sigStat = SS_VERIFIED;
        }
        else
        {
            result = SIG_BAD;
            sig->sigStat = SS_BAD;
        }
    }
    incompleteSig(sig);
    return result;
}

static boolean writeSignature(DataFileP file, signature * sig,
hashtable * keyRings, boolean overwriting)
{
    long position;

    if(!completeSig(keyRings, sig)) return FALSE;
    position = vf_length(file);
    writeSKEpacket(file, sig->details);
    incompleteSig(sig);
    if(overwriting) sig->fileOffset = position;
    return TRUE;
}

boolean writePubKey(DataFileP file, pubkey * pub_key,
boolean overwriting, boolean writeTrust, hashtable * keyringDefault)
{
    username * user_name = firstName(pub_key);
    hashtable * keyRings = pub_key->keyRing;
    long position;

    if(!keyRings) keyRings = keyringDefault;
    if(!keyRings) return FALSE;

    if(!completeKey(pub_key))
        return FALSE;
    position = vf_length(file);
    writekeypacket(file, pub_key, NULL);
    if(writeTrust) write_trust(file, pub_key->trust);
    if(overwriting) pub_key->fileOffset = position;
    /* need to discard duplicate packets! */
    if(pub_key->directSig)
        writeSignature(file, pub_key->directSig, keyRings, overwriting);
    while(user_name)
    {
        signature * sig = firstSig(user_name);
        char text[256];

        position = vf_length(file);
        text_from_name(keyRings, user_name, text);
        writeuserpacket(file, text);
        if(overwriting) user_name->fileOffset = position;
        if(writeTrust) write_trust(file, user_name->trust);
        /* need to discard duplicate packets! */
        while(sig)
        {
            writeSignature(file, sig, keyRings, overwriting);
            if(writeTrust) write_trust(file, sig->trust);
            sig = nextSig(sig);
        }
        user_name = nextName(user_name);
    }
    if(pub_key->subkeys)
        writePubKey(file, pub_key->subkeys, overwriting, writeTrust, keyRings);
    return TRUE;
}


boolean writeSecKey(DataFileP file, seckey * sec_key)
{
    pubkey * pub_key = sec_key->publicKey;
    hashtable * keyRings = pub_key->keyRing;
    username * user_name;

    if(!pub_key || !completeKey(pub_key)) return FALSE;
    writekeypacket(file, pub_key, sec_key);
    /* 9-jan-01 - want to write out signatures to skr, but need input support
     *     if(pub_key->directSig)
     *         writeSignature(file, pub_key->directSig, keyRings, FALSE);*/
    user_name = firstName(pub_key);
    while(user_name)
    {
        /* 9-jan-01 
         *         signature * sig = firstSig(user_name);*/
        char text[256];

        text_from_name(keyRings, user_name, text);
        writeuserpacket(file, text);
        /* 9-jan-01 
         *         while(sig)
         *         {
         *             writeSignature(file, sig, keyRings, FALSE);
         *             sig = nextSig(sig);
         *         }*/
        user_name = nextName(user_name);
    }
    return TRUE;
}


boolean writePubRing(DataFileP file, hashtable * keyRings)
{
    pubkey * pub_key = firstPubKey(keyRings);
    DataFileP output = file ? file : vf_toReplace(keyRings->file);
    boolean overwrite = (boolean)(!file);

    while(pub_key)
    {
        if(!writePubKey(output, pub_key, overwrite, TRUE, NULL))
        {
            if(overwrite) vf_close(output);
            return FALSE;
        }
        pub_key = nextPubKey(pub_key);
    }
    if(overwrite)
    {
        vf_replaceWith(keyRings->file, output);
        keyRings->file = output;
    }
    keyRings->publicChanged = FALSE;
    return TRUE;
}

boolean writeSecRing(DataFileP file, hashtable * keyRings)
{
    seckey * sec_key = keyRings->secretKeys;

    while(sec_key)
    {
        if(!writeSecKey(file, sec_key)) return FALSE;
        sec_key = sec_key->next_in_file;
    }
    keyRings->secretChanged = FALSE;
    return TRUE;
}

/* end of file keyhash.c */
