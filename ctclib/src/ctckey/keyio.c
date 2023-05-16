/* keyio.c
 **
 **  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> &
 **  Heimdall <heimdall@bifroest.demon.co.uk>  1996
 **  All rights reserved.  For full licence details see file licences.c
 **
 **  This file includes some modified code from
 **  PGP: Pretty Good(tm) Privacy - public key cryptography for the masses.
 ** (c) Copyright 1990-1992 by Philip Zimmermann.
 */
#include <assert.h>
#include <string.h>
#include "bignums.h"
#include "callback.h"
#include "hash.h"
#include "keyio.h"
#include "keyhash.h"
#include "keyutils.h"
#include "port_io.h"
#include "utils.h"
#include "pkcipher.h"

static boolean parseSubpackets(byte * buffer, long length, byte * keyID, uint32_t * timestamp)
{
    byte * ptr = buffer;
    byte * end = buffer + (size_t) length;
    int len;

    while(ptr < end)
    {
        len = *ptr - 1;        /* length includes type byte but not length byte */
        switch(*(ptr+1))
        {
        case SUBPKT_CREATION:
            if(len != SIZEOF_TIMESTAMP) return FALSE;
            memcpy((byte*)timestamp, ptr + 2, SIZEOF_TIMESTAMP);
            CONVERT(*timestamp);
            break;

        case SUBPKT_KEYID:
            if(len != KEYFRAGSIZE) return FALSE;
            memcpy(keyID, ptr + 2, KEYFRAGSIZE);
        }
        ptr += len + 2;
    }
    return (boolean)(ptr == end);
}

static keyio_error readOpenPGPLength(DataFileP file, long * length, boolean * multipart, int * hbytes)
{
    byte byte0, byte1;
    *hbytes = 0;
    *multipart = FALSE;

    if(vf_read(&byte0, 1, file) != 1) return KIO_FILE_ERROR;
    ++(*hbytes);
    if(byte0 < 192)
        *length = (uint32_t)byte0             /* & 0x7F  + 0 */;
    else if((byte0 & 0xE0) == 0xC0)
    {
        if(vf_read(&byte1, 1, file) != 1) return KIO_FILE_ERROR;
        ++(*hbytes);
        *length = (uint32_t)(byte0 & 0x1f) * 256L + (uint32_t)byte1 + 192L;
    }
    else
    {
        *length = 1 << (byte0 & 0x1F);
        *multipart = TRUE;
    }
    return KIO_OKAY;
}


/* Routine to read the type and length of the packet */
static keyio_error readpacketheader(DataFileP file, byte *type, long * length, boolean * multipart)
{
    int llength;    /* length of length */
    byte buf[8] = { 
        0         };
    byte ctb;
    int hbytes = 0;

    *multipart = FALSE;
    if(vf_read(&ctb, 1, file) != 1)
    {
        if(vf_length(file) == vf_where(file))
            /* Note this is a special case;  EOF in the middle of a block is a file error*/
            return KIO_EOF;
        else
            return KIO_FILE_ERROR;
    }
    switch((ctb & CTB_DESG_MASK))
    {
    case CTB_DESIGNATOR:
        *type = (byte)((CTB_TYPE_MASK & ctb) >> 2);
        llength = 1 << (CTB_LLEN_MASK & ctb);        /* either 1, 2, 4, or 8 */
        if(llength == 8)
        {
            *length = -1;            /* undefined length */
        }
        else
        {
            *length = 0;
            if(vf_read((byte *) buf, llength, file) < llength)
                return KIO_FILE_ERROR;
            if(llength==1)
                *length = (uint32_t)buf[0];
            else if(llength==2)
                *length = EXTRACT_SHORT(buf);
            else if(llength==4)
                *length = EXTRACT_LONG(buf);
        }
        return KIO_OKAY;

    case CTB_PGP3:
        *type = (byte)(CTB_TYPE_MSK3 & ctb);
        return readOpenPGPLength(file, length, multipart, &hbytes);
    default:
        return KIO_NOT_CTB;
    }
}


boolean writepacketheader(DataFileP file, byte type, long length)
{
    byte buffer[5];
    long length_bytes;

    buffer[0] = (byte)(CTB_DESIGNATOR + type * 4);
    /* First determine how many length bytes there should be */
    length_bytes = (length > 255) ?
    ((length > 0xFFFFL) ? 4 : 2 ) :
    ((length >= 0) ? 1 : 0 );
    /* Then implement bodges to get round bugs in PGP where it  */
    /* makes invalid assumptions about size of length fields    */
    if( type == CTB_SKE ||
        type == CTB_CERT_PUBKEY ||
        type == CTB_CERT_SECKEY) length_bytes = 2;

    switch(length_bytes)
    {
    case 4:
        buffer[0] += (byte)2;
        buffer[1] = (byte)((length >> 24) & 0xFF);
        buffer[2] = (byte)((length >> 16) & 0xFF);
        buffer[3] = (byte)((length >> 8) & 0xFF);
        buffer[4] = (byte)(length & 0xFF);
        break;

    case 2:
        buffer[0] += (byte)1;
        buffer[1] = (byte)(length /256);
        buffer[2] = (byte)(length % 256);
        break;

    case 1:
        /*  buffer[0] += 0; */
        buffer[1] = (byte)length;
        break;

    case 0:
        buffer[0] += (byte)3;
    }
    return (boolean) (vf_write(buffer, length_bytes + 1, file)
        == length_bytes + 1);
}



keyio_error readusername(DataFileP file, long position, char username[256])
{
    byte type;
    int status;
    long length;
    boolean multipart;

    username[0] = '\0';    /* set null result for failures */
    if(!vf_setpos(file, position)) return -1;
    if((status = readpacketheader(file, &type, &length, &multipart)) != 0) return status;
    if(type != CTB_USERID) return KIO_WRONG_REC_TYPE;
    if(length > 255 || length < 0 || multipart) return KIO_BAD_LENGTH; //TODO??

    vf_read(username, length, file);
    username[(size_t)length] = '\0';
    return 0;
}

/* Read summary information from a packet; often all that is needed
 ** Note that in the case of keys will hash derived Id.s, this does NOT return the ID.*/
#define SKIP_RETURN(x) { vf_setpos(file, end); return(x);}
#define MAXSUBPACKETS 200
keyio_error readsummary(DataFileP file, recordSummary * summary)
{
    int status;
    byte mdlen;
    long subpktsLen;
    byte buffer[MAXSUBPACKETS];
    long end;
    byte alg;

    summary->multipart = FALSE;
    summary->position = vf_where(file);
    if((status = readpacketheader(file, &summary->type, &summary->length, &summary->multipart)) != 0)
        return status;
    summary->start = vf_where(file);
    if(summary->length > 0)
    {
        end = summary->start + summary->length;
        if(summary->multipart)
        {
            boolean more = TRUE;
            while(more)
            {
                int hbytes = 0;
                long length = 0;
                vf_setpos(file, end);
                status = readOpenPGPLength(file, &length, &more, &hbytes);
                if(status != KIO_OKAY)
                    return status;
                end += (length+hbytes);
            }
            vf_setpos(file, summary->start);
        }
    }
    else
        end = vf_length(file);

    summary->next = end;

    // assume that all these bits are within the first multipart block
    switch(summary->type)
    {
    case CTB_KEYCTRL:
        if(summary->length != 1) SKIP_RETURN(KIO_BAD_LENGTH);
        if(vf_read(&summary->trust, 1, file) != 1)
            SKIP_RETURN(KIO_FILE_ERROR);
        return 0;

    case CTB_USERID:
        if(summary->length > 255) SKIP_RETURN(KIO_BAD_LENGTH);
        break;

    case CTB_PKE:
        vf_read(&summary->version, 1, file);
        if(summary->version < MIN_VERSION ||
            summary->version > VERSION_2_6)
            SKIP_RETURN(KIO_BAD_VERSION);
        /* Read and return KEY ID */
        vf_read(summary->itemID, KEYFRAGSIZE, file);
        break;

    case CTB_SKE:
        vf_read(&summary->version, 1, file);
        if(summary->version < MIN_VERSION ||
            summary->version > VERSION_3)
        { 
            SKIP_RETURN(KIO_BAD_VERSION); 
        }
        else if(summary->version != VERSION_3)
        {
            /* Skip timestamp, validity period, and type byte */
            vf_read(&mdlen, 1, file);
            vf_read(&summary->sigClass, 1, file);
            vf_movepos(file, (long) mdlen - 1);
            /* Read and return KEY ID */
            vf_read(summary->itemID, KEYFRAGSIZE, file);
        }
        else
        {
            vf_read(&summary->sigClass, 1, file);
            vf_movepos(file, 2L);            /* Skip algorithm bytes */
            vf_read(buffer, 2, file);            /* Read length of (hashed) subpackets */
            subpktsLen = (long)EXTRACT_SHORT(buffer);
            vf_movepos(file, subpktsLen);            /* Skip subpackets */
            vf_read(buffer, 2, file);            /* Read length of (unhashed) subpackets */
            subpktsLen = (long)EXTRACT_SHORT(buffer);
            if(subpktsLen > MAXSUBPACKETS) SKIP_RETURN(KIO_FILE_ERROR);
            vf_read(buffer, subpktsLen, file);
            if(!parseSubpackets(buffer, subpktsLen, summary->itemID, NULL)) SKIP_RETURN(KIO_FILE_ERROR);
        }
        break;
    case CTB_CERT_PUBKEY:
    case CTB_CERT_SECKEY:
    case CTB_SEC_SUBKEY:
    case CTB_OLD_COMMENT:
        vf_read(&summary->version, 1, file);
        if(summary->version < MIN_VERSION || summary->version > VERSION_3)
        {
            if(summary->type == CTB_OLD_COMMENT && summary->version < VERSION_3)
            {
                /* At 2.6 CTB_OLD_COMMENT was a comment packet;
                 ** at 5.0 (possibly earlier) it is a Public subkey packet;
                 ** decide which on version number
                 ** Version invalid => assume a 2.6 comment packet
                 ** We handle this internally with the PGP5.0 type value */
                summary->type = CTB_COMMENT;
                break;
            }
            else
                SKIP_RETURN(KIO_BAD_VERSION);
        }        /* skip the timestamp and validity */
        vf_movepos(file, (long)SIZEOF_TIMESTAMP);
        if(summary->version < VERSION_3)
            vf_movepos(file, sizeof(short));

        vf_read(&alg, 1, file);
        if(!valid_PKE_algor(alg)) SKIP_RETURN(KIO_BAD_ALGOR);

        if(!read_mpn_summary(alg, summary, file)) SKIP_RETURN(KIO_FILE_ERROR);

        break;
    }
    vf_setpos(file, end);
    return(0);
}/* readkpacket */


/* Read a full key packet (and only a key packet)    */
/* Unlike the PGPclassic version there is no attempt */
/* to decrypt secret keys on the fly.                  */
keyio_error readkeypacket(DataFileP file, byte * type, pubkey * pub_key, seckey * sec_key)
{
    uint16_t status;
    long end;
    long length;
    boolean multipart;

    if((status = (uint16_t) readpacketheader(file, type, &length, &multipart)) != 0) return status;

    end = vf_where(file) + length;
    if(multipart)
    {
        boolean more = TRUE;
        while(more)
        {
            int hbytes = 0;
            long length = 0;
            vf_setpos(file, end);
            status = readOpenPGPLength(file, &length, &more, &hbytes);
            if(status != KIO_OKAY)
                return status;
            end += (length+hbytes);
        }
    }
    if(multipart)
        SKIP_RETURN(KIO_BAD_LENGTH); // TODO

    /* N.B. We are not yet coping with secret subkeys */
    if(*type != CTB_CERT_PUBKEY &&
        *type != CTB_CERT_SECKEY &&
        *type != CTB_PUB_SUBKEY) SKIP_RETURN(KIO_WRONG_REC_TYPE);

    vf_read(&pub_key->version, 1, file);
    if( pub_key->version < MIN_VERSION ||
        pub_key->version > VERSION_3)
        SKIP_RETURN(KIO_BAD_VERSION);

    vf_read(pub_key->timestamp, SIZEOF_TIMESTAMP, file);
    convert_byteorder(pub_key->timestamp, SIZEOF_TIMESTAMP);

    if(pub_key->version < VERSION_3)
        vf_read(pub_key->validity, SIZEOF_VALIDITY, file);
    vf_read(&pub_key->pkalg, 1, file);
    if(!valid_PKE_algor(pub_key->pkalg)) SKIP_RETURN(KIO_BAD_ALGOR);

    if(!read_mpn_pubkey(pub_key, file)) return KIO_FILE_ERROR;
    sizePubkey(pub_key);
    extractKeyfrag(pub_key);
    if(*type == CTB_CERT_SECKEY)
    {
        int block;
        if(!sec_key) return KIO_NO_POINTER;
        vf_read(&sec_key->kpalg.cv_algor, 1, file);
        block = cipherBlock(sec_key->kpalg.cv_algor);

        /* begin by disposing of unencrypted secret keys,
         *                         not previously handled */
        if(sec_key->kpalg.cv_algor == 0)        /*not if(!(sec_key->kpalg.cv_algor & (~CEA_FLEX_FLAG)))*/
        {
            sec_key->kpalg.cv_algor = CEA_NONE;
            sec_key->kpalg.cv_mode = 0;
            sec_key->hashalg = 0;
        }
        else if(!(sec_key->kpalg.cv_algor & CEA_FLEX_FLAG))
        {
            sec_key->kpalg.cv_mode = CEM_CFB;
            vf_read(sec_key->iv, block, file);
            sec_key->hashalg = MDA_MD5;
        }
        else if(CEA_FLEX_FLAG & sec_key->kpalg.cv_algor)
        {
            vf_read(&sec_key->kpalg.cv_mode, 1, file);

            /* we barely have enough entropy in most pass-phrases for
             *                         worthwhile single encryption - so squelch multiple encryption
             *                         at this point (2-key triple DES perhaps; but I don't think
             *                         we're likely to offer single-DES) */

            /* Interpret the "more" flag on cv_algor as meaning that a byte
             *                         follows to determine the passphrase hashing algorithm */

            sec_key->kpalg.cv_mode &= CEM_MASK;            /* no triple or reverse */
            if(sec_key->kpalg.cv_algor & CEA_MORE_FLAG)
            {
                vf_read(&sec_key->hashalg, 1, file);
            }
            else sec_key->hashalg = MDA_MD5;

            /* now read the IV for the encryption */
            /* this is mode/alg dependent */
            vf_read(sec_key->iv, block, file);
        }
        else sec_key->kpalg.cv_mode = 0;

        if(!read_mpn_seckey(sec_key, file))
            return KIO_FILE_ERROR;
        vf_read(&sec_key->checksum, sizeof(sec_key->checksum), file);
        CONVERT(sec_key->checksum);
        sec_key->skstat = EXTERNAL;
    }    /* secret key */

    if(end != vf_where(file)) return KIO_FILE_ERROR;    /* ??? is this the right code? */
    pub_key->status = KS_COMPLETE;
    return KIO_OKAY;
}


keyio_error readPKEpacket(DataFileP file, long offset, bignump cypher,
byte *algor)
{
    byte type;
    long length;
    byte version;
    keyio_error status;
    boolean multipart;

    // This should be already preprocessed if it is multipart
    // so we should be able to ignore multipart just as we
    // ignore the length field!
    vf_setpos(file, offset);
    status = readpacketheader(file, &type, &length, &multipart);
    if(status != KIO_OKAY) return status;
    if(type != CTB_PKE) return KIO_WRONG_REC_TYPE;
    if(vf_read(&version, 1, file) != 1) return KIO_FILE_ERROR;
    if(version < MIN_VERSION || version > VERSION_2_6) return KIO_BAD_VERSION;
    if(!vf_movepos(file, 8) ||     /* skip key Id. */
    vf_read(algor, 1, file) != 1) return KIO_FILE_ERROR;
    if(!valid_PKE_algor(*algor)) return KIO_BAD_ALGOR;
    /* This bit is contingent on the PK algorithm */
    return read_CKcypher(cypher, file, *algor) ? KIO_OKAY : KIO_FILE_ERROR;
}


keyio_error writePKEpacket(DataFileP file, keyDetails * details)
{
    /*write mpn pub section must change with PKALG*/
    long length = 1 + KEYFRAGSIZE + 1;

    length += length_CKcypher(details);
    if(!writepacketheader(file, CTB_PKE, length) ||
        vf_write(&details->version, 1, file) != 1 ||
        vf_write(&details->keyId, KEYFRAGSIZE, file) != KEYFRAGSIZE ||
        vf_write(&details->pk_algor, 1, file) != 1 ||
        !write_CKcypher(details, file)) return KIO_FILE_ERROR;
    return KIO_OKAY;
}


keyio_error readSKEpacket(DataFileP file, long offset, sigDetails * sig)
{
    byte type;
    byte incLen;
    long length;
    keyio_error status;
    boolean multipart;

    /* N.B. sig->pub_key is NOT overwritten.  It may contain a pre-loaded value */
    sig->version = 0;    /* In case of failure */

    // This should be already preprocessed if it is multipart
    // so we should be able to ignore multipart just as we
    // ignore the length field!
    vf_setpos(file, offset);
    status = readpacketheader(file, &type, &length, &multipart);
    if(status != KIO_OKAY) return status;
    if(type != CTB_SKE) return KIO_WRONG_REC_TYPE;
    if(vf_read(&sig->version, 1, file) != 1) return KIO_FILE_ERROR;
    if(sig->version < MIN_VERSION || sig->version > VERSION_3) return KIO_BAD_VERSION;
    if(sig->version < VERSION_3)
    {
        if(vf_read(&incLen, 1, file) != 1) return KIO_FILE_ERROR;
        if(incLen != V2DIGESTEXTRAS) return KIO_BAD_LENGTH;
        if(vf_read(&sig->digestBytes, V2DIGESTEXTRAS, file) != V2DIGESTEXTRAS ||
            vf_read(sig->keyId, KEYFRAGSIZE, file) != KEYFRAGSIZE ||
            vf_read(&sig->pk_algor, 1, file) != 1 ||
            vf_read(&sig->md_algor, 1, file) != 1 ||
            vf_read(&sig->checkBytes, 2, file) != 2) return KIO_FILE_ERROR;
        sig->sigClass = sig->digestBytes[0];
        memcpy((byte*)&sig->timestamp, sig->digestBytes + 1, SIZEOF_TIMESTAMP);
        CONVERT(sig->timestamp);
        sig->lenDigestBytes = V2DIGESTEXTRAS;
    }
    else
    {
        byte length[2];
        byte buffer[MAXSUBPACKETS];
        uint16_t subPktsLen;
        long storeLen;

        /* read the fixed format start */
        if(vf_read(&sig->sigClass, 1, file) != 1 ||
            vf_read(&sig->pk_algor, 1, file) != 1 ||
            vf_read(&sig->md_algor, 1, file) != 1 ||
            vf_read(length, 2, file) != 2) return KIO_FILE_ERROR;
        subPktsLen = EXTRACT_SHORT(length);
        if(subPktsLen + 12 > MAXDIGESTEXTRAS) return KIO_FILE_ERROR;
        vf_movepos(file, -6);        /* return to start of packet */
        /* Re-read the portion to be included in the hash */
        if(vf_read(&sig->digestBytes, subPktsLen + 6, file) != subPktsLen + 6)
            return KIO_FILE_ERROR;
        /* Added the extra 6 bytes */
        sig->digestBytes[subPktsLen + 6] = VERSION_3;
        sig->digestBytes[subPktsLen + 7] = 0xFF;
        storeLen = subPktsLen + 6;
        CONVERT(storeLen);
        memcpy(&sig->digestBytes[subPktsLen + 8], (byte*)&storeLen, sizeof(uint32_t));
        /* recorded the total length of extra bytes */
        sig->lenDigestBytes = (uint16_t) (subPktsLen + 12);
        /* look for a timestamp sub-packet */
        if(!parseSubpackets(&sig->digestBytes[6], subPktsLen, sig->keyId, &sig->timestamp))
            return KIO_FILE_ERROR;
        if(vf_read(length, 2, file) != 2) return KIO_FILE_ERROR;
        subPktsLen = EXTRACT_SHORT(length);
        if(subPktsLen > MAXSUBPACKETS) return KIO_FILE_ERROR;
        if(vf_read(buffer, subPktsLen, file) != subPktsLen) return KIO_FILE_ERROR;
        if(!parseSubpackets(buffer, subPktsLen, sig->keyId, &sig->timestamp))
            return KIO_FILE_ERROR;
        if(vf_read(&sig->checkBytes, 2, file) != 2) return KIO_FILE_ERROR;
    }
    prepare_signature(sig);
    if(!read_mpn_signature(sig, file))
    {
        release_signature(sig);
        return KIO_FILE_ERROR;
    }
    return KIO_OKAY;
}


keyio_error writeSKEpacket(DataFileP file, sigDetails * sig)
{
    long length = length_signature(sig) + sig->lenDigestBytes + KEYFRAGSIZE;
    byte incLen = V2DIGESTEXTRAS;

    if(sig->version <= VERSION_2_6)
    {
        assert(sig->lenDigestBytes == V2DIGESTEXTRAS);
        length += 1 + 1 + 1 + 1 + 2;
        if(!writepacketheader(file, CTB_SKE, length) ||
            vf_write(&sig->version, 1, file) != 1 ||
            vf_write(&incLen, 1, file) != 1 ||
            vf_write(&sig->digestBytes, V2DIGESTEXTRAS, file) != V2DIGESTEXTRAS ||
            vf_write(&sig->keyId, KEYFRAGSIZE, file) != KEYFRAGSIZE ||
            vf_write(&sig->pk_algor, 1, file) != 1 ||
            vf_write(&sig->md_algor, 1, file) != 1 ||
            vf_write(&sig->checkBytes, 2, file) != 2 ||
            !write_signature(sig, file)) return KIO_FILE_ERROR;
        return KIO_OKAY;
    }
    else if(sig->version == VERSION_3)
    {
        /* A version3 signature is much easier to write (albeit harder to construct)
         ** as most of the complication is stored in the 'extra' digest bytes */

        /* Non-hashed subpackets header.
         ** 1st two bytes are overall length of all subpackets (there only is one)
         ** 3rd byte is subpacket length (inclusive of type byte)
         ** 4th byte is subpacket type  */
        byte nHH[] = { 
            0, KEYFRAGSIZE + 2, KEYFRAGSIZE + 1, SUBPKT_KEYID                 };
        long Hlength = sig->lenDigestBytes - 6;        /* -6 for unstored digestBytes */

        /*length += 4 - 6 + 2;*/
        /* +4 for fixed header; -6 for unstored digestBytes; +2 for check bytes */
        if(!writepacketheader(file, CTB_SKE, length) ||
            vf_write(&sig->digestBytes, Hlength, file) != Hlength ||
            vf_write(nHH, sizeof(nHH), file) != (long) sizeof(nHH) ||
            vf_write(&sig->keyId, KEYFRAGSIZE, file) != KEYFRAGSIZE ||
            vf_write(&sig->checkBytes, 2, file) != 2 ||
            !write_signature(sig, file)) return KIO_FILE_ERROR;

        return KIO_OKAY;
    }
    else
        return KIO_BAD_VERSION;
}


keyio_error writekeypacket(DataFileP file, pubkey * pub_key, seckey * sec_key)
{
    byte type;
    size_t pub_length;
    size_t cert_length;
    uint16_t mpn_checksum=0;
    byte * buffer;
    int mode = 0;
    int more = 0;
    int block = 0;
    byte fileAlgorByte;

    cert_length = pub_length = formatKeySize(pub_key);
    if(!sec_key)
    {
        type = (byte)( pub_key->superkey ? CTB_PUB_SUBKEY : CTB_CERT_PUBKEY);
    }
    else
    {
        byte simple = unflexAlg(sec_key->kpalg.cv_algor);
        mode = sec_key->kpalg.cv_mode ? 1 : 0;
        more = sec_key->kpalg.cv_algor & CEA_MORE_FLAG ? 1 : 0;

        /* force backwards compatibility for MD5 passphrase hash */
        if(MDA_MD5 == sec_key->hashalg) more = 0;

        block = cipherBlock(sec_key->kpalg.cv_algor);

        /* force backwards compatibility for IDEA/3DES/CAST5 CFB */
        if(
        (CEM_CFB == (sec_key->kpalg.cv_mode & CEM_MASK))
            && simple
            && (!more)
            )
        {
            mode = 0;
            fileAlgorByte = simple;            /*Not sec_key->kpalg.cv_algor = simple;*/
        }
        /* force backwards compatibility for cleartext keys */
        else if (CEA_NONE == sec_key->kpalg.cv_algor)
        {
            assert(sec_key->kpalg.cv_mode == 0);            /*NOT mode = 0;*/
            fileAlgorByte = 0;            /*NOT sec_key->kpalg.cv_algor = 0;*/
        }
        else if(
        (CEM_CFB != (sec_key->kpalg.cv_mode & CEM_MASK)) &&
            (0 != (sec_key->kpalg.cv_mode & CEM_MASK))
            && simple
            )
        {
            mode = 1;
            fileAlgorByte = unflexAlg(simple);            /*NOT sec_key->kpalg.cv_algor = unflexAlg(simple);*/
        }

        /* however this is not the place to do any other checks on the
         *                  algorithm details - reading/creating shold stop rotters */
        /* N.B. We do not currently handle secret sub-keys
         **      any secret keys of the PGP5.0 additional PKE algorithms */
        type = CTB_CERT_SECKEY;
        cert_length +=
            + 1 + mode + more + block         /* cipher type + ?hash? + ?mode? + ?IV? */
        + length_seckey(sec_key)
            + sizeof(mpn_checksum);
    }
    writepacketheader(file, type, (long)cert_length);
    buffer = qmalloc(pub_length);
    if(!buffer) return KIO_NO_MEMORY;

    formatPubkey(pub_key, buffer);
    vf_write(buffer, (long) pub_length, file);
    qfree(buffer);

    if(sec_key)    /* secret key */
    {
        /* Write byte for following algorithm */
        vf_write(&fileAlgorByte, 1, file);        /*NOT vf_write(&sec_key->kpalg.cv_algor, 1, file);*/
        if(mode) vf_write(&sec_key->kpalg.cv_mode, mode, file);
        if(more) vf_write(&sec_key->hashalg, more, file);
        if(block) vf_write(sec_key->iv, block, file);        /* write out the IV */

        if(!write_seckey(sec_key, file)) return KIO_FILE_ERROR;
        /* Write checksum here - based on plaintext values */
        mpn_checksum = sec_key->checksum;
        CONVERT(mpn_checksum);
        vf_write(&mpn_checksum, sizeof(mpn_checksum), file);
    }
    return KIO_OKAY;
}

keyio_error writeuserpacket(DataFileP file, char userid[256])
{
    long length = (long) strlen(userid);

    writepacketheader(file, CTB_USERID, length);
    vf_write(userid, length, file);
    return KIO_OKAY;
}


keyio_error write_trust(DataFileP file, byte trustbyte)
{
    if( writepacketheader(file, CTB_KEYCTRL, 1) &&
        vf_write(&trustbyte, 1, file))
        return KIO_OKAY;
    else
        return KIO_FILE_ERROR;
}

/*
 * This is a stray bit which really ought be in a separate module
 */

/*---------------------- Housekeeping ----------------------*/

static void simpleCondition(short severity, short code, cb_context context)
{
    cb_condition condition = { 
        0, 0, 0, 0, NULL, NULL         };

    condition.severity = severity;
    condition.module = CB_PKE;
    condition.code = code;
    condition.context = (short) context;
    cb_information(&condition);
}

boolean getConvKey(DataFileP file, long offset,
seckey * sec_key,
cv_details * algor, size_t *keylen, byte * * convKey)
{
    bignum cyphertextkey[MAXCYPHERMPIS];
    byte pk_algor;
    int j;

    for(j=0; j<MAXCYPHERMPIS; j++) init_mpn(&cyphertextkey[j]);

    /* Get encrypted conventional key from file */
    if((readPKEpacket(file, offset, cyphertextkey, &pk_algor) != KIO_OKAY) ||
        (pk_algor != sec_key->publicKey->pkalg) ||     /* Consistent PK algorithms ?*/
    !valid_PKE_algor(pk_algor) )    /* sane PK alg ? */
    {
        for(j=0; j<MAXCYPHERMPIS; j++) clear_mpn(&cyphertextkey[j]);
        return FALSE;
    }
    return unpackConvKey(cyphertextkey, pk_algor, sec_key, algor,
    keylen, convKey);
}

boolean putConvKey(DataFileP file, byte version,
pubkey * pub_key, cv_details * algor, size_t keylen, byte *convKey)
{
    common_error result;
    keyDetails details;
    pubkey * encryption_key = pub_key;    /* default encryption IS the selected public key */
    int j;

    if(pub_key->pkalg == PKA_RSA_SIGN_ONLY || pub_key->pkalg == PKA_DSA)
    {
        if(pub_key->subkeys)
        {
            encryption_key = pub_key->subkeys;
            if(!completeKey(encryption_key)) return FALSE;
        }
        else if(pub_key->pkalg == PKA_DSA)
            /* N.B. We are failing only if the key is _unusable_ not if the owner requested that
             **      is not used for encryption.  We consider enforcement of the latter is a matter
             **      for the application.  However this is very contraversial. */
            return FALSE;
    }
    extract_keyID(details.keyId, encryption_key);

    result = packConvKey(&details, version, encryption_key->pkalg, encryption_key,
    algor, keylen, convKey);

    switch(result)
    {
    case CE_OKAY:
        if(writePKEpacket(file, &details) != KIO_OKAY)        /*Alg sensitive*/
        {
            simpleCondition(CB_ERROR, PKE_FILE_ERROR, CB_ENCRYPTION);
            result = CE_OTHER;
        }
        break;

    case CE_NO_MEMORY:
        simpleCondition(CB_ERROR, PKE_NO_MEMORY, CB_ENCRYPTION);
        break;

    case CE_USER_BREAK:
        simpleCondition(CB_ERROR, PKE_USER_BREAK, CB_ENCRYPTION);
        break;

    default:
        simpleCondition(CB_CRASH, PKE_BAD_RETURN_CODE, CB_ENCRYPTION);
    }
    for(j=0; j<MAXCYPHERMPIS; j++) clear_mpn(&details.cypherKey[j]);
    return (boolean) (result == CE_OKAY);    /* was just "result" --Tines */
}

/* end of file keyio.c */

