/* ctc.c
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
 ** Move into separate compand.c -- Mr. Tines 14-Sep-1997
 ** Strip trailing spaces from clearsign canonical text -- Mr. Tines 25-Oct-1997
 **
 ** Tidy up the code for revealed session key use into a more explicit form
 **                                                      -- Mr. Tines 13-May-1998
 **
 ** Local language support for multi-byte character sets; definition of
 ** CTC policy on such issues
 **                                                      -- Mr. Tines 15-May-1998
 **
 */

/*
 ** CTC and local language encoding schemes
 **
 ** Building as it does on the file formats for Phil Zimmerman's PGP2.6 and the
 ** resulting RFPs that publish the design, this program expects to be able
 ** to output armoured cyphertext with a subset of 7-bit ASCII on a one byte
 ** per character basis.
 **
 ** This conditions all file I/O with regard to human-readable text : it is
 ** hard-wired into the architecture that whaterve character set has single-byte
 ** 7-bit ASCII as a subset.  This allows 8-bit character sets like the ISO Latin
 ** family, and character encodings in which characters may occupy 1 or more
 ** bytes.  These latter include the more common Japanese encodings Shift-JIS and
 ** EUC in which a non-ASCII (top bit set) range of characters imply "I am the
 ** first byte of a two-byte character"; and such more general formats such as
 ** the UTF-8 encoding of Unicode&trade;, in which bytes with the top bit clear are
 ** as for ASCII, those beginning 110 indicate the first of a 2-byte charactr and
 ** those beginning 1110 the start of a 3-byte character.
 **
 ** The program definitely cannot, while still remaining compatible with PGP,
 ** handle raw wchar_t format, where ASCII characters are merely the values 0-127
 ** of a 16-bit character.
 **
 ** PGP2.6 has taken the policy of trying to fold 8-bit character sets into the ASCII
 ** variant (cp850) of the IBM personal computer when it encounters text; we
 ** have rejected that behaviour in favour of trying to retain the exact binary
 ** representation of the text as little altered as possible (we do perform some
 ** interpretation in this file, using ANSI standard routines to determine and
 ** remove trailing spaces and to break lines at spaces, or punctuation, or,
 ** failing that, between characters to keep line lengths sensible).
 **
 ** Forunately, OpenPGP 5.0 has opted to bring text to a canonical form by transforming
 ** multi-byte to wchar_t to UTF8 multibyte, which leaves English in its various
 ** dialects unaffected, this would represent a major incompatibility of format wrt 2.6
 ** for users of richer character sets.
 **
 ** We provide a UTF-8 intermediate format which is OpenPGP 5.0 compatible, as the
 ** canonical text upon which we perform signature. This allows people to exchange
 ** data which would sign and verify consistently, but may end up rendered
 ** differently at each end (e.g. Shift-JIS -> EUC, via Unicode), although the
 ** Unicode to local character set translation may be lossy (shift-JIS -> ASCII,
 ** for example).
 **
 ** Note that application code calling this library should first call setlocale() -
 ** probably setlocale(LC_ALL, "") to get the default encoding, which the rest of the
 ** system uses.
 **
 */


#include <assert.h>
#include <string.h>
#include "armour.h"
#include "bignums.h"
#include "compand.h"
#include "hash.h"
#include "keyio.h"
#include "keyutils.h"
#include "port_io.h"
#include "ctc.h"
#include "pkcipher.h"
#include "random.h"
#include "utils.h"
#include "widechar.h"

#define BUFFERSIZE 1024
#define MAXKEYS 10
/* This is the maximum number of _alternative_ secret keys for decrypting
 ** a message.  (i.e. The intersection of the set of keys the message is
 ** readable by and the secret keys available.)  For most purposes 1 is adequate
 ** more than 5 seems implausible or at least bizarre.  */
#define CONVKEY "CONVENTIONAL KEY"


/* Crude classification of CTB blocks */
typedef enum
{
    PKT_CYPHERTEXT,     /* Block types normally found in cyphertext files */
    PKT_KEYRING,     /* Block types normally found in keyrings */
    PKT_EITHER,     /* Block types that could be found in either cyphertext
     ** or keyrings but not an integral part of either */
    PKT_UNKNOWN     /* Unrecognised block type */
}
packet_class;

typedef struct literalPacket_T
{
    char fileType;    /* 't' for text or 'b' for binary */
    char fileName[256];
    long timestamp;
    long fileStart;
}
literalPacket;


static void fullCondition(short severity, short code, cb_context context,
char * text, pubkey * pub_key)
{
    cb_condition condition = {
        0, 0, 0, 0, NULL, NULL         };

    condition.severity = severity;
    condition.module = CB_CTC_ENGINE;
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


static void UTF8encode(const char * mbs, DataFileP output)
{
    byte utf8[768];    /* worst case, 3 bytes per character */
    long bytes = mbstoUTF8(mbs, utf8);

    /* The body is no longer local text, so must be output as binary */
    vf_write(utf8, bytes, output);

    /* but we have to supply the missing line end,
     *            which as the canonical \r\n is UTF8 compatible */
    vf_write("\r\n", 2L, output);
}

static int utfclen(const byte * utf)
{
    if((*utf>>7)==0x0) return 1;
    if((*utf>>5)==0x6) return 2;
    if((*utf>>4)==0xE)return 3;
    return 1;    /* safety - stepping by one should resynch soon */
}

/* This is a utility routine for unpacking a 'u' file from a binary format to
 * a text format.  Note that this has to worry about character lengths */
void UTF8decode(DataFileP from, DataFileP to)
{
    byte inbuffer[BUFFERSIZE];
    char outbuffer[BUFFERSIZE];
    long length;
    int stash = 0;
    char *out=outbuffer;
    /* allow for a \uXXXX expansion to 6 bytes */
    int limit = (MB_CUR_MAX < 6) ? 6 : MB_CUR_MAX;

    vf_setpos(from, 0);
    vf_setpos(to, 0);


    /* get a chunk to process */
    while((length = vf_read(inbuffer+stash, BUFFERSIZE-stash, from)) > 0)
    {
        byte * in = inbuffer;
        wchar_t intermediate;

        /* copy across slowly */
        while(in < inbuffer+(size_t)length+stash)
        {
            int clen = utfclen(in);

            /* flush full buffer */
            if((out-outbuffer) > (BUFFERSIZE-(limit+1)))
            {
                *out='\0';
                vf_writeline(outbuffer, to);
                out = outbuffer;
            }
            /* do we have all the character ?*/
            if(in + clen > inbuffer+(size_t)length+stash)
            {
                int newstash = (int)((length+stash)-(in-inbuffer));
                memmove(inbuffer, inbuffer, newstash);
                stash = newstash;
                break;
            }

            if(1 == clen)
            {
                intermediate = (wchar_t) (*in);
            }
            else if(2 == clen)
            {
                intermediate = (wchar_t) (((in[0]&0x1f)<<6)+(in[1]&0x3f));
            }
            else
            {
                intermediate = (wchar_t) (((in[0]&0xf)<<12)+((in[1]&0x3f)<<6)+(in[2]&0x3f));
            }
            in += clen;

            /* Assume \r\n for line end.  Flush line on \r, swallow \n */
            if('\r' == intermediate)
            {
                *out='\0';
                vf_writeline(outbuffer, to);
                out = outbuffer;
            }
            else if(intermediate && '\n' != intermediate)            /* swallow NULLs too! */
            {
                clen = wctomb(out, intermediate);
                if(clen > 0)                /* conversion possible in this locale */
                {
                    out += clen;
                }
                else                 /* Unicode escape */
                {
                    byte w;
                    out[0] = '\\';
                    out[1] = 'u';
                    w = (byte)((intermediate>>8) & 0xFF );
                    byte2hex(out+2, w);
                    w = (byte)(intermediate & 0xFF);
                    byte2hex(out+4, w);
                }
            }
        }        /* next UTF character */
    }    /* next bufferload */
    /* flush anything remaining */
    if(out != outbuffer)
    {
        *out='\0';
        vf_writeline(outbuffer, to);
    }
}

static DataFileP canonicalise(DataFileP input, int wrapSize,
boolean strim, boolean utf8)
{
    DataFileP result = vf_tempfile(0);
    char buffer[256];
    long length;

    if(result)
    {
        vf_CCmode(CANONICAL, result);
        vf_setpos(input, 0);
        while((length = vf_readline(buffer, 256, input)) >= 0)
        {        /* we have an input line; prepare to wrap to size */
            int remainder = (int) length;
            char *from = buffer;

            /* This is a serious rewrite to allow for multi-byte input */
            /* we require vf_readline to present a whole number of characters */

            /* Stage 1 - trim trailing spaces */
            if(strim)
            {
                char * space = (char*)seekTrailingWhitespace(buffer);
                if(space)
                {
                    *space = '\0';
                    remainder = (int)(space-buffer);
                }
            }

            /* while we still have text to output */
            /* split it at a suitable punctuation break */

            while(remainder > wrapSize)
            {
                char * lineEnd = (char*)breakPoint(from, wrapSize /4, wrapSize);
                char temp = *lineEnd;
                /* force a line-end safely */
                *lineEnd = '\0';

                /* take the opportunity to trim whitespace */
                if(strim)
                {
                    char * space = (char*)seekTrailingWhitespace(from);
                    if(space) *space = '\0';
                }

                /* write trimmed and wrapped region */
                if(!utf8) vf_writeline(from, result);
                else UTF8encode(from, result);

                /* note how much we've covered, restore buffer and move along */
                remainder -= (int)(lineEnd - from);
                *lineEnd = temp;
                from = lineEnd;
            }
            if(!utf8) vf_writeline(from, result);
            else UTF8encode(from, result);
        }
    }
    /*vf_close(input); - don't do this - we end-up double-freeing a file */
    return result;
}

static boolean digest_file(DataFileP file,
byte * extras, long extraLen, byte algorithm,
byte digest[MAXHASHSIZE],
cb_context cbContext)
{
    byte buffer[BUFFERSIZE];
    long length;
    md_context context;

    vf_setpos(file, 0);
    if(!hashAlgAvail(algorithm))
    {
        simpleCondition(CB_ERROR, CTC_UNIMP_MSG_DIGEST, cbContext);
        return FALSE;
    }

    context = hashInit(algorithm);
    if(!context)
    {
        simpleCondition(CB_ERROR, CTC_OUT_OF_MEMORY, cbContext);
        return FALSE;
    }
    simpleCondition(CB_STATUS, CTC_DIGESTING, cbContext);
    /* Some file errors should result in FALSE returns */
    while((length = vf_read(buffer, BUFFERSIZE, file)) > 0)
    {
        hashUpdate(context, buffer, (uint32_t)length);
    }

    hashUpdate(context, extras, (uint32_t) extraLen);
    hashFinal(&context, digest);
    return TRUE;
}


static boolean signFile(DataFileP file, sigDetails * signature,
seckey * signatory)
{
    /* assemble extra bytes */
    signature->digestBytes[0] = signature->sigClass;
    signature->timestamp = (uint32_t)difftime(time(NULL), datum_time());
    memcpy(signature->digestBytes + 1, &signature->timestamp, SIZEOF_TIMESTAMP);
    convert_byteorder(signature->digestBytes + 1, SIZEOF_TIMESTAMP);
    signature->lenDigestBytes = V2DIGESTEXTRAS;

    /* do the message digest itself */
    if(!digest_file(file, signature->digestBytes, (long)signature->lenDigestBytes,
    signature->md_algor, signature->digest,
    CB_ENCRYPTION)) return FALSE;
    /* fill in the rest of the signature packet data */
    memcpy(signature->checkBytes, signature->digest, 2);
    extract_keyID(signature->keyId, publicKey(signatory));
    return TRUE;
}


static boolean readLiteral(DataFileP file, long offset, literalPacket * results)
{
    byte len;

    if( !vf_setpos(file, offset) ||
        vf_read(&results->fileType, 1, file) != 1 ||
        vf_read(&len, 1, file) != 1 ||
        vf_read(results->fileName, (long)len, file) != (long)len ||
        vf_read(&results->timestamp, SIZEOF_TIMESTAMP, file)
        != SIZEOF_TIMESTAMP) return FALSE;
    results->fileName[len] = '\0';
    convert_byteorder((byte*)&results->timestamp, SIZEOF_TIMESTAMP);
    results->fileStart = offset + 1 + 1 + len + SIZEOF_TIMESTAMP;
    return TRUE;
}

/* these are in the order of application - reverse to undo */
static void reversePass(cv_details *algor, byte *cKey)
{
    byte tempKeys[MAXKEYSIZE*MAXCVALGS*3], *base = cKey;
    int klen[MAXCVALGS];
    cv_details temp[MAXCVALGS];
    int passes, i;
    boolean more;

    for(passes=0, more=TRUE; more; ++passes)
    {
        more = (boolean) (algor[passes].cv_algor & CEA_MORE_FLAG);

        if((0==passes) && (!more)) return;        /* no-op if only 1 pass */

        klen[passes] = cipherKey(algor[passes].cv_algor) *
            (algor[passes].cv_mode & CEM_TRIPLE_FLAG ? 3 : 1);
        memcpy(tempKeys+(MAXKEYSIZE*3*passes), base, klen[passes]);
        temp[passes] = algor[passes];
        base += klen[passes];
    }

    /* we now know how much we have to reverse - so do it */
    base = cKey;
    for(i=0; i<passes; ++i)
    {
        algor[i] = temp[passes-(i+1)];
        algor[i].cv_algor |= CEA_MORE_FLAG;
        memcpy(base, tempKeys+(MAXKEYSIZE*3*(passes-(i+1))), klen[passes-(i+1)]);
        base += klen[passes-(i+1)];
    }
    algor[passes-1].cv_algor &= CEA_MASK;
    memset(tempKeys, 0, sizeof(tempKeys));

}

#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (disable: 4701)
#endif
static boolean bulk_cypher(DataFileP input, DataFileP output,
cv_details *algor,
byte *cKey, boolean decrypt)
{
    byte buffer[BUFFERSIZE];
    byte check[MAXBLOCKSIZE+2];
    int caught = 0;

    int pass;
    boolean more = TRUE;

    long length;
    long head;
    long tail;
    cb_context cbContext = decrypt ? CB_DECRYPTION : CB_ENCRYPTION;

    uint32_t bytes;

    DataFileP in, out;
    int delta = decrypt ? 0 : MAXBLOCKSIZE+2;

    if(decrypt) reversePass(algor, cKey);

    for(pass=0; more; ++pass)
    {
        int block = cipherBlock(algor[pass].cv_algor);
        int maxblocks = (BUFFERSIZE/block);
        int chunk = maxblocks*block;
        cv_keysched context = cipherInit(&algor[pass], cKey, decrypt);
        boolean reverse = (boolean) (algor[pass].cv_mode & CEM_REVERSE_FLAG);
        boolean matched;
        /* reversal support */
        long chunksWritten = 0;
        long bytesWritten = 0;
        DataFileP temp = 0;

        more = (boolean) (algor[pass].cv_algor & CEA_MORE_FLAG);

        if(0==pass) in = input;
        else         /* input is what came out of the last pass */
        {
#ifdef __BORLANDC__
#pragma warn -def
#endif
			in = out;
#ifdef __BORLANDC__
#pragma warn .def
#endif
            vf_setpos(in, 0L);
        }

        head = vf_where(in);        /* start of data to process */
        tail = vf_length(in);        /* end of data */
        if(reverse) temp = vf_tempfile(tail-head);

        /* if this is not the final pass, put results in temporary location */
        if(more) out = vf_tempfile(vf_length(in)+delta);
        else out=output;

        cKey+= cipherKey(algor[pass].cv_algor) *
            (algor[pass].cv_mode & CEM_TRIPLE_FLAG ? 3 : 1);

        if(decrypt)
        {
            if(reverse)
            {
                vf_setpos(in, tail-(block+2));
            }
            /* Check the prepended key-checking data at once*/
            /* if possible; needs some thought for CTS modes */
            if(vf_read(check, block+2, in) != block+2)
            {
                simpleCondition(CB_FATAL, CTC_READ_FILE_ERR, cbContext);
                return FALSE;
            }
            caught += cipherDo(context, check, block+2);
            if(caught == block+2)
            {
                if(reverse) matched = (boolean)
                    ((check[0] == check[2]) &&
                        (check[1] == check[3]));
                else matched = (boolean)
                    ((check[block+1] == check[block-1]) &&
                        (check[block] == check[block-2]));

                if(!matched)
                {
                    simpleCondition(CB_FATAL, CTC_WRONG_BULK_KEY, cbContext);
                    return FALSE;
                }
            }            /* got it in first chunk */
        }
        else
        {
            /* Prepend (in the appropriate sense a block of garbage,
             * followed by a duplicate of the last two bytes in the
             * order in which the data are acanned
             */
            int offset = block;

            while(offset-- > 0) buffer[offset] = randombyte();

            if(reverse)
            {
                buffer[block] = buffer[0];
                buffer[block+1] = buffer[1];
                buffer[0] = buffer[2];
                buffer[1] = buffer[3];
            }
            else
            {
                buffer[block+1] = buffer[block-1];
                buffer[block] = buffer[block-2];
            }

            bytes = cipherDo(context, buffer, block+2);

            if(!reverse)
            {
                if((bytes > 0) && (vf_write(buffer, bytes, out) != (long) bytes))
                {
                    simpleCondition(CB_FATAL, CTC_WRITE_FILE_ERR, cbContext);
                    return FALSE;
                }
            }
            else if (bytes > 0)
            {
                if(
                (long) bytes != vf_write(buffer, bytes, temp)
                    ||
                    ((long) sizeof(uint32_t) !=
                    vf_write(&bytes, sizeof(uint32_t), temp) )
                    )
                {
                    simpleCondition(CB_FATAL, CTC_WRITE_FILE_ERR, cbContext);
                    return FALSE;
                }
                bytesWritten += bytes + sizeof(uint32_t);
                ++chunksWritten;
            }
        }

        if(!reverse)
        {
            byte *ptr;

            while((length = vf_read(buffer, chunk, in)) > 0)
            {
                bytes = cipherDo(context, buffer, (int)length);
                ptr = buffer;

                /* need to grab the first block and a bit on decrypt pass */
                if(decrypt && (caught < block + 2))
                {
                    int need = block + 2 - caught;

                    /* this code probably won't be used - not enough */
                    /* to check anything yet; so save and do the next*/
                    /* read */
                    if(bytes < (uint32_t)need)
                    {
                        memcpy(check+caught, buffer, (size_t) bytes);
                        caught += (int)bytes;
                        continue;
                    }
                    memcpy(check+caught, buffer, need);
                    matched = (boolean)
                        ((check[block+1] == check[block-1]) &&
                            (check[block] == check[block-2]));

                    if(!matched)
                    {
                        simpleCondition(CB_FATAL, CTC_WRONG_BULK_KEY, cbContext);
                        return FALSE;
                    }
                    bytes -= need;
                    ptr = buffer+need;
                    caught += need;
                }

                if((long) bytes != vf_write(ptr, (long)bytes, out))
                {
                    simpleCondition(CB_FATAL, CTC_WRITE_FILE_ERR, cbContext);
                    return FALSE;
                }
            }
            /* flush any lurking cyphertext in CTS-required modes */
            bytes = cipherEnd(&context, buffer);
            ptr = buffer;

            /* for a short message we may need to look here for checking */
            if(decrypt && (caught < block + 2))
            {
                int need = block + 2 - caught;

                /* We should have the correct numbre by now unless there's
                 *                                 been a major screw-up - aasume that's from a bad key */
                if(bytes < (uint32_t) need)
                {
                    matched = FALSE;
                }
                else
                {
                    memcpy(check+caught, buffer, need);
                    matched = (boolean)
                        ((check[block+1] == check[block-1]) &&
                            (check[block] == check[block-2]));
                }
                if(!matched)
                {
                    simpleCondition(CB_FATAL, CTC_WRONG_BULK_KEY, cbContext);
                    return FALSE;
                }
                bytes -= need;
                ptr = buffer+need;
            }

            /* now can hand on the remainder */
            if((long) bytes != vf_write(ptr, (long)bytes, out))
            {
                simpleCondition(CB_FATAL, CTC_WRITE_FILE_ERR, cbContext);
                return FALSE;
            }
        }
        else         /* reverse ordering */
        {
            /* this gets a bit more complex! */
            long offset = tail-chunk;
            long chunkNumber;
            boolean final = FALSE;

            if(decrypt) offset -= (block+2);

            /* if matching not yet done, shuffle stuff to the far end */
            if(decrypt && (caught < block + 2) && (caught > 0))
            {
                byte temp[MAXBLOCKSIZE+2];
                memcpy(temp, check, caught);
                memcpy(check+(block+2)-caught, temp, caught);
                memset(temp, 0, MAXBLOCKSIZE+2);
            }

            while(!final)
            {
                long section = chunk;
                if(offset <= head)
                {
                    section = chunk + (offset - head);
                    offset = head;
                    final = TRUE;
                }

                if(section <= 0) break;

                /* read from the end in chunks as big as will fill the buffer */
                vf_setpos(in, offset);
                if(section != vf_read(buffer, section, in))
                {
                    simpleCondition(CB_FATAL, CTC_READ_FILE_ERR, cbContext);
                    return FALSE;
                }

                /* encrypt and get some bytes back */
                bytes = cipherDo(context, buffer, (int)section);

                /* need to grab the first block and a bit on decrypt pass */
                if(decrypt && (caught < block + 2))
                {
                    int need = block + 2 - caught;

                    /* this code probably won't be used - not enough */
                    /* to check anything yet; so save and do the next*/
                    /* read */
                    if(bytes < (uint32_t)need)
                    {
                        memcpy(check+(block+2)-(size_t)(caught+bytes),
                        buffer, (size_t)bytes);
                        caught += (int) bytes;
                        continue;
                    }
                    memcpy(check, buffer+(size_t)bytes-need, need);
                    matched = (boolean)
                        ((check[0] == check[2]) &&
                            (check[1] == check[3]));

                    if(!matched)
                    {
                        simpleCondition(CB_FATAL, CTC_WRONG_BULK_KEY, cbContext);
                        return FALSE;
                    }
                    bytes -= need;
                    caught += need;
                }

                /* write the transformed data and then the count */
                /* to the temporary working file */
                if(
                (long) bytes != vf_write(buffer, bytes, temp)
                    ||
                    ((long) sizeof(uint32_t) !=
                    vf_write(&bytes, sizeof(uint32_t), temp) )
                    )
                {
                    simpleCondition(CB_FATAL, CTC_WRITE_FILE_ERR, cbContext);
                    return FALSE;
                }
                bytesWritten += bytes + sizeof(uint32_t);
                offset -= chunk;
                ++chunksWritten;
            }
            /* flush any data remaining in the cypher */
            bytes = cipherEnd(&context, buffer);
            vf_setpos(in, tail);

            if(decrypt && (caught < block + 2))
            {
                int need = block + 2 - caught;

                /* We should have the correct number by now unless there's
                 * been a major screw-up - assume that's from a bad key */
                if(bytes < (uint32_t)need)
                {
                    matched = FALSE;
                }
                else
                {
                    memcpy(check, buffer+(size_t)bytes-need, need);
                    matched = (boolean)
                        ((check[0] == check[2]) &&
                            (check[1] == check[3]));
                }
                if(!matched)
                {
                    simpleCondition(CB_FATAL, CTC_WRONG_BULK_KEY, cbContext);
                    return FALSE;
                }
                bytes -= need;
                caught += need;
            }
            /* Now we can put the tag-end data if any to the output file */
            if( (bytes > 0) && ((long) bytes != vf_write(buffer, bytes, out)) )
            {
                simpleCondition(CB_FATAL, CTC_WRITE_FILE_ERR, cbContext);
                return FALSE;
            }

            /* Now we can reverse the order of the chunks out of the temporary
             ** file, simulating a write from the end into 'out' */
            for(chunkNumber = 0, offset = bytesWritten - sizeof(uint32_t);
                chunkNumber < chunksWritten;
              ++chunkNumber)
            {
                /* read final byte count*/
                vf_setpos(temp, offset);
                if( (long) sizeof(uint32_t) !=
                    vf_read(&bytes, sizeof(uint32_t), temp) )
                {
                    simpleCondition(CB_FATAL, CTC_READ_FILE_ERR, cbContext);
                    return FALSE;
                }
                vf_setpos(temp, offset - bytes);
                if((long)bytes != vf_read(buffer, bytes, temp))
                {
                    simpleCondition(CB_FATAL, CTC_READ_FILE_ERR, cbContext);
                    return FALSE;
                }
                if((long)bytes != vf_write(buffer, bytes, out))
                {
                    simpleCondition(CB_FATAL, CTC_WRITE_FILE_ERR, cbContext);
                    return FALSE;
                }
                offset -= (bytes + sizeof(uint32_t));
            }
            vf_close(temp);
        }


        /* tidy away any temporary input file now it's done with */
        if(pass > 0) vf_close(in);
    }
    memset(check, 0, MAXBLOCKSIZE+2);
    memset(buffer, 0, BUFFERSIZE);
    return TRUE;
}
#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (default: 4701)
#endif

static DataFileP doCompress(DataFileP input, byte algorithm)
{
    boolean status = FALSE;
    if(algorithm != CPA_NONE)
    {
        DataFileP result = vf_tempfile(0);
        if(compandAlgAvail(algorithm))
        {
            writepacketheader(result, CTB_COMPRESSED, -1L);
            vf_write(&algorithm, 1, result);
            status = compress(input, algorithm, result);
        }

        if(status)
        {
            return result;
        }
        else
        {
            vf_close(result);
            simpleCondition(CB_WARNING, CTC_UNIMP_COMPRESS, CB_ENCRYPTION);
        }
    }
    return input;
}

static void * generateConvKey(cv_details * algorithm, size_t * length)
{
    byte * key = NULL;
    int offset;

    *length = 0;

    if(!(CEA_FLEX_FLAG & algorithm[0].cv_algor))
    {
        *length = cipherKey(algorithm[0].cv_algor);
    }
    else
    {
        int pass = 0;
        int more;
        do
        {
            more = algorithm[pass].cv_algor & CEA_MORE_FLAG;
            *length += cipherKey(algorithm[pass].cv_algor) *
                (algorithm[pass].cv_mode
                & CEM_TRIPLE_FLAG ? 3 : 1);
            pass++;
        }
        while(more);
    }

    if(*length) key = zmalloc(*length);
    if(key)
    {
        offset = (int)*length;
        while(offset-- > 0) key[offset] = randombyte();
    }
    else *length = 0;
    return key;
}


static continue_action exam_decompress(DataFileP input, decode_context * context)
{
    continue_action result = CB_SKIP;
    DataFileP output = vf_tempfile(0);
    byte algor;

    if(output)
    {
        simpleCondition(CB_STATUS, CTC_DECOMPRESSING, context->cbContext);
        vf_setpos(input, context->current.start);
        if(vf_read(&algor, 1, input) == 1)
        {
            if(algor && (algor != CPA_SPLAY) &&
                (algor != CPA_DEFLATE))
                simpleCondition(CB_FATAL, CTC_UNIMP_COMPRESS, CB_DECRYPTION);
            if(!decompress(input, output, algor))
                simpleCondition(CB_ERROR, CTC_DECOMPRESSING, CB_DECRYPTION);
            result = examine(output, context);
        }
        vf_close(output);
    }
    else
        simpleCondition(CB_ERROR, CTC_NO_TEMP_FILE, context->cbContext);
    return result;
}

typedef struct CTCcontext_T
/* Internal context structure */
{
    decode_context * dc;
    DataFileP keyinput;
    seckey * keyToUse;
    seckey * keys[MAXKEYS];
    long keysBlockPos[MAXKEYS];
    int Nkeys;
    pubkey * pub_key;
    byte * convKey;
    cv_details algor[MAXCVALGS];
    size_t keylen;
    sigDetails sig;
    boolean pkefound;
    DataFileP ctxinput;
}
CTCcontext;

/* Routines for desperate circumstances - splitting out the
 ** conventional key to let someone - like the filth - read
 ** a private message of yours.  So don't worry too much about
 ** error conditions. */

static void writeConvKey( CTCcontext *locals)
{
    long packetlen = (long) (sizeof(locals->algor) + locals->keylen+2);
    int i;
    boolean more = TRUE;
    byte b;

    DataFileP binary;
    cb_filedesc filedesc = { 
        TEXTCYPHER, WRITE, SPLITKEY         };
    DataFileP ascii = cb_getFile(&filedesc);
    armour_params p = { 
        CONVKEY, VERSIONNO, ARM_PGP, 1, 1, 0        };
    uint32_t keylen = (uint32_t) locals->keylen;

    if(!ascii) return;
    binary = vf_tempfile(packetlen);
    for(i=0; more; ++i)
    {
        more = (boolean) (locals->algor[i].cv_algor & CEA_MORE_FLAG);
        vf_write(&locals->algor[i].cv_algor, 1L, binary);
        vf_write(&locals->algor[i].cv_mode, 1L, binary);
    }
    /* assume that there will be < 65536 bytes of key ! */
    b = (byte) ((keylen & 0x0000FF00ul) >> 16);    /* high byte*/
    vf_write(&b, 1L, binary);
    b = (byte) (keylen & 0x000000FFul);
    vf_write(&b, 1L, binary);

    vf_write(locals->convKey, (long) keylen, binary);

    vf_setpos(binary, 0);
    armour_block(binary, ascii, &p);
    vf_close(ascii);
    vf_close(binary);
}

static void readConvKey(DataFileP key, decode_context * context)
{
    uint32_t keylen;
    byte b;
    int i;
    boolean more = TRUE;

    vf_setpos(key, 0L);
    for(i=0; more; ++i)
    {
        vf_read(&context->cv_algor[i].cv_algor, 1L, key);
        vf_read(&context->cv_algor[i].cv_mode, 1L, key);
        more = (boolean) (context->cv_algor[i].cv_algor & CEA_MORE_FLAG);
    }

    vf_read(&b, 1L, key);
    keylen = ((uint32_t)b) << 16;
    vf_read(&b, 1L, key);
    keylen += (uint32_t)b;

    context->cv_data = zmalloc((size_t) keylen);
    context->cv_len = (size_t) keylen;
    vf_read(context->cv_data, keylen, key);
}

static continue_action examinePKE(CTCcontext * locals)
{
    seckey * keyFound;

    if(!locals->keyToUse)
    {
        keyFound = seckey_from_keyID( locals->dc->keyRings,
        locals->dc->current.itemID);
        if(keyFound)
        {
            if(keyStatus(keyFound) == INTERNAL)
            {
                /* getConvKey generates it own call-backs */
                if(getConvKey( locals->keyinput, locals->dc->current.position,
                keyFound, locals->algor, &locals->keylen, &locals->convKey))
                    locals->keyToUse = keyFound;
            }
            else if(locals->Nkeys < MAXKEYS)
            {
                locals->keys[locals->Nkeys] = keyFound;
                locals->keysBlockPos[locals->Nkeys++] = locals->dc->current.position;
            }
        }
        else
        {
            /* Tell the application if we recognise the key even if we don't
             ** have it.  (Primarily this is to tell the user if it is key that (s)he
             ** keeps in another ring.  The assumption is that the public ring will
             ** contain the public keys for all of the user's secret keys.)  */
            pubkey * pub_key;

            pub_key = key_from_keyID(locals->dc->keyRings, locals->dc->current.itemID);
            if(pub_key)
                fullCondition( CB_INFO, CTC_SECKEY_UNAVAIL,
                CB_DECRYPTION, NULL, pub_key);
        }
    }
    return CB_CONTINUE;
}

static continue_action examineCKE(CTCcontext * locals)
{
    continue_action status = CB_CONTINUE;
    int keyN;
    boolean doIt = FALSE;

    if(locals->keylen && locals->convKey) doIt = TRUE;    /* supplied key */
    if(!doIt && !locals->pkefound)    /* no public key packets at all */
    {
        if(cb_convKey(locals->algor, (void**)&locals->convKey, &locals->keylen)
           && locals->keylen && locals->convKey) doIt = TRUE;        /* passphrase */
    }

    if( locals->keyToUse || (locals->Nkeys > 0 &&
        (keyN = cb_need_key(locals->keys, locals->Nkeys)) >= 0 &&
        getConvKey( locals->keyinput, locals->keysBlockPos[keyN], locals->keys[keyN],
    locals->algor, &locals->keylen, &locals->convKey)
        ) ) doIt = TRUE;

    /* is a key-packet all that we are after? */
    if(REVEALKEY == locals->dc->splitkey && doIt)
    {
        writeConvKey(locals);
        return CB_ABORT;
    }
    if(doIt)
    {
        DataFileP output = vf_tempfile(0);

        /* Note implicit assumption that the whole of the rest of binary1
         ** is the CTB_CKE packet.  (Virtual file package should solve this.
         **  when fully implemented!)*/
        vf_setpos(locals->ctxinput, locals->dc->current.start);
        bulk_cypher(locals->ctxinput, output, locals->algor, locals->convKey, TRUE);
        status = examine(output, locals->dc);
        vf_close(output);
    }
    else
    {
        vf_setpos(locals->ctxinput, locals->dc->current.start + locals->dc->current.length);
        simpleCondition(CB_ERROR, CTC_NO_KEY_AVAIL, CB_DECRYPTION);
    }
    locals->keyToUse = NULL;
    locals->Nkeys = 0;
    return status;
}

static continue_action examineSKE(CTCcontext * locals)
{
    pubkey * pub_key;

    /* packet signed with some PKA secret key */
    pub_key = key_from_keyID(locals->dc->keyRings, locals->dc->current.itemID);
    if(pub_key && completeKey(pub_key))
    {
        locals->sig.pub_key = pub_key;
        /* Get encrypted conventional key from file */
        if(readSKEpacket(locals->keyinput, locals->dc->current.position,
        &locals->sig) != KIO_OKAY)
            return CB_SKIP;

        if(!getSignature(&locals->sig)) return CB_SKIP;
        return CB_CONTINUE;
    }
    else
    {
        char keyID[IDPRINTSIZE];

        formatKeyID(keyID, locals->dc->current.itemID);
        fullCondition(CB_WARNING, CTC_PUBKEY_UNAVAIL, CB_VERIFYING, keyID, NULL);
        return CB_SKIP;
    }
}

static continue_action examineLIT2(CTCcontext * locals)
{
    cb_details details;
    literalPacket literal;
    DataFileP part;
    long length;

    readLiteral(locals->ctxinput, locals->dc->current.start, &literal);
    length = locals->dc->current.length - (literal.fileStart - locals->dc->current.start);
    part = vf_part(locals->ctxinput, literal.fileStart, length);
    if(locals->sig.version != 0)
    {
        byte digest[MAXHASHSIZE];

        digest_file(part, locals->sig.digestBytes, (long)locals->sig.lenDigestBytes,
        locals->sig.md_algor, digest, CB_DECRYPTION);
        details.valid_sig = verifySignature(&locals->sig, digest);

        details.signatory = locals->sig.pub_key;
        details.timestamp = locals->sig.timestamp;
        locals->sig.version = 0;        /* used signature */
    }
    else
    {
        details.valid_sig = 0;
        details.signatory = NULL;
        details.timestamp = 0;
    }

    details.addressee = NULL;
    details.typeByte = literal.fileType;
    strcpy(details.fileName, literal.fileName);

    /*
     * NB : if this file is text, it should assumed to be in UTF-8
     * because this is the OpenPGP default, and there are no provisions
     * for other character encodings in that document.  Fortunately,
     * 7-bit ASCII is UTF-8 by default.
     *
     * Thus routine UTF8decode should be used in the user presentation
     * within the callback below
     */
    cb_result_file(part, &details);

    vf_setpos(locals->ctxinput, literal.fileStart + length);
    vf_close(part);
    return CB_CONTINUE;
}

static packet_class packetClass(recordSummary * summary)
{
    switch(summary->type)
    {
    case CTB_PKE:
    case CTB_COMPRESSED:
    case CTB_CKE:
    case CTB_LITERAL:
    case CTB_LITERAL2:
        return PKT_CYPHERTEXT;

    case CTB_CERT_SECKEY:
    case CTB_CERT_PUBKEY:
    case CTB_SEC_SUBKEY:
    case CTB_PUB_SUBKEY:
    case CTB_KEYCTRL:
    case CTB_USERID:
        return PKT_KEYRING;

    case CTB_SKE:        /* This could be cyphertext or keyring depending on class*/
        switch(summary->sigClass)
        { 
        case SIG_BINARY:
        case SIG_TEXT:
            return PKT_CYPHERTEXT;

        case SIG_KEY_CERT:
        case SIG_KEY_PERSONA:
        case SIG_KEY_CASUAL:
        case SIG_KEY_POSITIVE:
        case SIG_KEY_COMPROM:
        case SIG_KEY_REVOKE:
        case SIG_SUBKEY_CERT:
        case SIG_SUBKEY_REVOKE:
            return PKT_KEYRING;

            /* Strictly I am not sure how timestamp signatures should be classified*/
        case SIG_KEY_TIMESTMP:
            return PKT_EITHER;

        default:
            return PKT_UNKNOWN;
        }

    case CTB_COMMENT:
        return PKT_EITHER;

    default:
        return PKT_UNKNOWN;
    }
}

static void newKeys(keyringContext * context)
{
    cb_condition condition = { 
        CB_INFO, CB_PK_MANAGE, 0, CB_DECRYPTION, NULL, NULL         };
    pubkey * mainkey = context->last_mainkey;

    if(mainkey) mainkey->subkeys = checkSubkeys(context->keyRings, mainkey->subkeys, mainkey);
    if(context->last_seckey)
    {
        condition.code = KEY_READ_SECKEY;
        condition.pub_key = publicKey(context->last_seckey);
        if(cb_exception(&condition) == CB_CONTINUE)
        {
            insertSeckey(context->keyRings, context->last_seckey);
        }
        context->last_key = NULL;
        context->last_seckey = NULL;
    }
    else if(mainkey)
    {
        condition.code = KEY_READ_PUBKEY;
        condition.pub_key = context->last_mainkey;
        if(cb_exception(&condition) == CB_CONTINUE)
        {
            insertPubkey(context->keyRings, &context->last_mainkey);
        }
        context->last_key = NULL;
        context->last_mainkey = NULL;
    }
}

static continue_action checkDetachedSig(CTCcontext *locals)
{
    cb_filedesc filedesc = { 
        BINPLAIN, READ, SIGNEDFILE         };
    DataFileP plain = cb_getFile(&filedesc);
    byte digest[MAXHASHSIZE];
    cb_details details;

    if(!plain) return CB_SKIP;
    digest_file(plain, locals->sig.digestBytes, (long)locals->sig.lenDigestBytes,
    locals->sig.md_algor, digest, CB_DECRYPTION);
    vf_close(plain);

    details.valid_sig = verifySignature(&locals->sig, digest);

    details.signatory = locals->sig.pub_key;
    details.timestamp = locals->sig.timestamp;
    locals->sig.version = 0;    /* used signature */

    cb_result_file(0, &details);
    return CB_CONTINUE;
}

#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (disable: 4127) // conditional expression is constant
#endif

static continue_action nextReadable(DataFileP file, recordSummary * summary)
{
    cb_condition condition = { 
        CB_ERROR, CB_CTB_IO, 0, CB_DECRYPTION, NULL, NULL         };

    while(TRUE)
    {
        condition.code = (short) readsummary(file, summary);
        switch(condition.code)
        {
        case KIO_OKAY:
            return CB_CONTINUE;

            /* Errors requiring the block to be skipped but not the rest
             ** of the file
             ** N.B. This implementation may give anomalous results where
             ** a block is rejected but subsequent qualifying blocks are
             ** not.  */
        case KIO_BAD_VERSION:
        case KIO_BAD_ALGOR:
            cb_information(&condition);
            break;

        case KIO_EOF:
            return CB_SKIP;

        default:
            cb_information(&condition);
            return CB_ABORT;
        }
    }
}

static continue_action bufferMultipartPacket(DataFileP input, DataFileP proxy, recordSummary * summary)
{
    long header = summary->start - summary->position;
    long left = header + summary->length;
    boolean more = summary->multipart;
    byte buffer[BUFFERSIZE];
    vf_setpos(input, summary->position);

    while(left > 0)
    {
        long block = left;
        long got;

        if(block > BUFFERSIZE) 
            block = BUFFERSIZE;

        got = vf_read(buffer, block, input);
        if(got < 0)
            break;

        vf_write(buffer, got, proxy);

        left -= got;
        if((0 == left) && more)
        {
            // code cut and paste
            byte byte0, byte1;
            more = FALSE;

            if(vf_read(&byte0, 1, input) != 1) return CB_ABORT;
            if(byte0 < 192)
                left = (uint32_t)byte0             /* & 0x7F  + 0 */;
            else if((byte0 & 0xE0) == 0xC0)
            {
                if(vf_read(&byte1, 1, input) != 1) return CB_ABORT;
                left = (uint32_t)(byte0 & 0x1f) * 256L + (uint32_t)byte1 + 192L;
            }
            else
            {
                left = 1 << (byte0 & 0x1F);
                more = TRUE;
            }
        }
    }

    summary->position = 0;
    summary->start = header;
    summary->length = vf_length(proxy) - header;
    vf_setpos(input, summary->next);

    return CB_CONTINUE;
}

static continue_action examinePlainSig(DataFileP input, decode_context * context, sigDetails * details)
{
    /* This is a cut down equivalent of examine for reading plainSignatureblocks only.
     ** There may only be a single CTB block (we only look for one) and it must be a SKE block */
    /*keyio_error  retCode;*/
    CTCcontext locals = { 
        0         };
    continue_action status;

    vf_setpos(input, 0);
    status = nextReadable(input, &context->current);
    if(status != CB_CONTINUE) return status;
    if(context->current.type != CTB_SKE)
    {
        simpleCondition(CB_ERROR, CTC_NOT_SIGNATURE, CB_VERIFYING);
        return CB_SKIP;
    }
    locals.dc = context;
    locals.ctxinput = input;
    locals.keyinput = input;
    status = examineSKE(&locals);
    if(status == CB_CONTINUE) memcpy(details, &locals.sig, sizeof(sigDetails));
    return status;
}

/*
 *  --------------------------------------------------------------------
 *  --------------------------------------------------------------------
 */

continue_action examine(DataFileP input, decode_context * context)
{
    continue_action status = CB_CONTINUE;
    packet_class lastClass = PKT_EITHER;
    packet_class thisClass;
    keyringContext keyring_context = {
        NULL         };
    CTCcontext locals = { 
        0         };
    boolean sigPending = FALSE;
    long nextPacket = 0;
    DataFileP proxy = input;

    keyring_context.keyRings = context->keyRings;
    locals.dc = context;
    locals.keyinput = input;
    locals.sig.version = 0;
    locals.pkefound=FALSE;

    /* set up any supplied DEK values */
    if(USEKEY == context->splitkey)
    {
        unsigned int i;
        locals.keylen = context->cv_len;
        for(i=0; i<MAXCVALGS; ++i) locals.algor[i] = context->cv_algor[i];
        locals.convKey = zmalloc(locals.keylen);
        for(i=0; i<locals.keylen; ++i) locals.convKey[i] = context->cv_data[i];
    }
    else
    {
        locals.keylen = 0;
        locals.convKey = 0;
    }

    while(status == CB_CONTINUE &&
        vf_setpos(input, nextPacket) &&
        (status = nextReadable(input, &context->current)) == CB_CONTINUE)
    {
        proxy = input;

        nextPacket = context->current.next;
        thisClass = packetClass(&context->current);
        if(lastClass == PKT_CYPHERTEXT && thisClass != PKT_CYPHERTEXT)
        {        /* if not longer processing cyphertext; wipe cyphertext context */
            locals.keyToUse = NULL; 
            locals.Nkeys = 0;
            memset(&locals.sig, 0, sizeof(sigDetails));
        }

        // case where we can't use the raw bytestream
        if(context->current.multipart)
        {
            proxy = vf_tempfile(context->current.length);
            status = bufferMultipartPacket(input, proxy, &context->current);
            if(status == CB_ABORT)
            {
                vf_close(proxy);
                break;
            }
        }
        locals.ctxinput = proxy;

        switch(context->current.type)
        {
        case CTB_COMPRESSED:
            status = exam_decompress(proxy, context); 
            break;
        case CTB_PKE:
            /* a supplied key packet overrides this */
            locals.pkefound=TRUE;
            if(USEKEY != context->splitkey)
            {
                status = examinePKE(&locals);
            }
            break;

        case CTB_CKE:
            if(context->cv_len && context->cv_data) locals.Nkeys=0;
            status = examineCKE(&locals);
            break;
        case CTB_SKE:
            if(thisClass == PKT_KEYRING)
                status = keyringPacket(proxy, &context->current, &keyring_context);
            else
            {
                status = examineSKE(&locals);
                if(CB_CONTINUE == status) sigPending = TRUE;
                /* a CB_SKIP return code from examineSKE implies that the signature
                 ** is not good;  however the user may still wish to see the plaintext */
                if(CB_SKIP == status) status = CB_CONTINUE;
                /* Discard signature bignum? */
            }
            break;

        case CTB_LITERAL:
            break;            /* To the best of the author's knowledge only used as
             ** software Id. packets by PGP 3.0; ignore */

        case CTB_LITERAL2:            /* raw data with filename and mode */
            status = examineLIT2(&locals);
            sigPending = FALSE;
            break;

            /* key packet types */
        case CTB_CERT_SECKEY:            /* secret key certificate */
        case CTB_CERT_PUBKEY:            /* public key certificate */
            newKeys(&keyring_context);
            /* DROPTHRO */
        case CTB_PUB_SUBKEY:
        case CTB_SEC_SUBKEY:
        case CTB_USERID:            /* user id packet */
            status = keyringPacket(proxy, &context->current, &keyring_context);
            break;

            /* Ignore remote trust packets */
        case CTB_KEYCTRL:            /* key control packet */
            break;

        case CTB_CONV_ESK:            /* conventionally encrypted packets */
            break;
            /* more by way of a reminder as to what we are missing than anything else
             ** here are the other types cases  */
        case CTB_COMMENT:            /* comment packet */
            {
                /*char buffer[5] = { '0', 'x', '#', '#', '\0' };*/
                /* Let's at least reveal what the packet has to say...*/
                char buffer[256];
                byte length;

                /*byte2hex(buffer+2, context->current.type);*/

                if( vf_setpos(locals.ctxinput, locals.dc->current.start) &&
                    1 == vf_read(&length, 1, locals.ctxinput) &&
                    length == vf_read(buffer, length, locals.ctxinput))
                {
                    /* the 256th character will always be free.  Ensure that */
                    /* the last character always gives an indication of the */
                    /* number of bytes read, and at the same time, that the */
                    /* first free character is a null.  This is to avoid any */
                    /* problems with sneaky comment packets with embedded */
                    /* nulls hidden inside them, against spec. */
                    buffer[255] = (byte) (length+1);
                    buffer[length] = 0;

                    fullCondition(CB_WARNING, CTC_COMMENT_FOUND, CB_DECRYPTION,
                    buffer, NULL);
                }
                else
                    simpleCondition(CB_ERROR, CTC_READ_FILE_ERR, CB_DECRYPTION);
            }
            break;

        default:
            simpleCondition(CB_ERROR, CTC_UNKNWN_CTB, CB_DECRYPTION);
            break;
        }
        lastClass = thisClass;
        if(context->current.multipart)
        {
            vf_close(proxy);
        }
    }
    newKeys(&keyring_context);
    /* free memory, destroying sensitive info */
    /* Borland C++ 4.52 and VAXC both prefer this coercion explicitly made */
    if((locals.convKey!=NULL) && (locals.keylen!=0))
        zfree((void**)&locals.convKey, locals.keylen);

    if(sigPending)    /* poll for plaintext file to digest & check */
        status = checkDetachedSig(&locals);

    /* CB_SKIP is interpreted as stop at this level but continue at the */
    /* next level up so is returned as CB_CONTINUE.  CB_ABORT propagates */
    release_signature(&locals.sig);
    return (status == CB_ABORT) ? CB_ABORT : CB_CONTINUE;
}


/*
 *  --------------------------------------------------------------------
 */

continue_action examine_text(DataFileP input, decode_context * context)
{
    continue_action status = CB_CONTINUE;
    armour_info info;
    cb_condition condition = {
        CB_ERROR, CB_ARMOUR, 0, CB_DECRYPTION, NULL, NULL        };
    DataFileP output;
    boolean blockFound = FALSE;

    if(context->splitkey != (byte)USEKEY)
    {
        context->cv_data = 0;
        context->cv_len = 0;
    }

    simpleCondition(CB_STATUS, CTC_SEARCHING, CB_DECRYPTION);
    while( status == CB_CONTINUE && next_armour(input, &info) != ARM_NONE)
    {
        blockFound = TRUE;

        if((output = vf_tempfile(0)) != 0)
        {
            if((condition.code = (short) unarmour_block(input, output, &info))
                != ARM_SUCCESS)
            {
                condition.text = info.name;
                status = cb_exception(&condition);
            }
            else if(info.style == ARM_PGP_PLAIN)
            {            /* The first unarmour call returns the text; look for the signature */
                /*
                 * Did we ought unwrap Quoted Printable?
                 * I would say "Only if we are unwrapping PGP/MIME", which
                 * we don't yet support, and even them, only when we hit a
                 * QP encoding header line.
                 */
                byte digest[MAXHASHSIZE];
                sigDetails details;
                DataFileP sigBlock;

                if(0 == (sigBlock = vf_tempfile(200))) break;
                if(next_armour(input, &info) != ARM_PGP) break;
                if((condition.code = (short) unarmour_block(input,
                sigBlock, &info)) != ARM_SUCCESS)
                {
                    condition.text = info.name;
                    status = cb_exception(&condition);
                }
                if(examinePlainSig(sigBlock, context, &details) != CB_CONTINUE) break;

                /*
                 * Did we ought bring this text into UTF-8??
                 * OpenPGP is obscure.
                 */

                if(digest_file(output, details.digestBytes,
                (long)details.lenDigestBytes, details.md_algor,
                digest, context->cbContext))
                {
                    cb_details report = {
                        0                                         };

                    report.valid_sig = verifySignature(&details, digest);
                    report.typeByte = 't';
                    report.signatory = details.pub_key;
                    report.timestamp = details.timestamp;
                    cb_result_file(output, &report);
                }
                release_signature(&details);
            }
            else if(0 == strncmp(CONVKEY, info.name, strlen(CONVKEY)))
            {
                cb_filedesc seek;
                DataFileP cypher;

                readConvKey(output, context);
                seek.purpose = CYPHERTEXTFILE;

                /* ask for the file; the file decriptor should tell
                 * us whether this is an ascii or binary file
                 * This is the responsibility of the callback to set
                 */
                cypher = cb_getFile(&seek);
                if(!cypher) return CB_ABORT;

                context->splitkey = (byte)USEKEY;

                if(TEXTCYPHER == seek.file_type)
                {
                    status = examine_text(cypher, context);
                }
                else
                {
                    status = examine(cypher, context);
                }
                vf_close(cypher);
                return status;
            }
            else
            {
                /* an ASCII armoured file;  whereas is software is primarily cryptographic it is
                 ** inevitable that it will encounter some armoured plaintext.  First check if the contents start with
                 ** a plausible PGP byte */

                vf_setpos(output, 0);
                if(readsummary(output, &context->current) == KIO_OKAY)
                    status = examine(output, context);
                else
                {
                    /* assume binary data */
                    cb_details details = {
                        0                                         };

                    details.typeByte = 'b';
                    if(info.style == ARM_UUENCODE)
                        strcpy(details.fileName, info.name);
                    cb_result_file(output, &details);
                }
            }
            vf_close(output);
        }
    }
    if(!blockFound)
        simpleCondition(CB_WARNING, CTC_NOTHING_FOUND, CB_DECRYPTION);
    return status;
}


static boolean writefileheader(DataFileP output, encryptInsts * instructions, long length)
{
    long oal;    /* Over-all-length */
    byte nameLength = (byte) (min(255, strlen(instructions->filename)));
    long timestamp = (long)difftime(time(NULL), datum_time());

    oal = 1 + 1 + nameLength + SIZEOF_TIMESTAMP + length;
    writepacketheader(output, CTB_LITERAL2, oal);
    vf_write(&instructions->fileType, 1, output);
    vf_write(&nameLength, 1, output);
    vf_write(instructions->filename, (long)nameLength, output);
    convert_byteorder((byte*)&timestamp, SIZEOF_TIMESTAMP);
    vf_write((byte*)&timestamp, SIZEOF_TIMESTAMP, output);
    return TRUE;
}

/*
 *  --------------------------------------------------------------------
 *  --------------------------------------------------------------------
 */
#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (disable: 4701)
#endif
boolean encrypt(DataFileP source, DataFileP output, encryptInsts * instructions)
{
    DataFileP current;
    DataFileP headers;
    DataFileP next;
    sigDetails signature = {
        0         };
    pubkey ** recipients;
    int ivlen = 0;
    boolean status;
    void * cv_data = NULL;
    size_t cv_len;

    assert(sizeof(uint32_t) == 4);
    /* 1) If text canonicalise */
    switch(instructions->fileType)
    {
    case 't':        /* bring non-clearsigned text into canonical UTF form*/
        current = canonicalise(source, 72, FALSE, TRUE);
        signature.sigClass = SIG_TEXT;
        break;

    case 'b':
        current = source;
        signature.sigClass = SIG_BINARY;
        break;

    default:
        return FALSE;
    }
    /* create plaintext prefix temporary file */
    headers = vf_tempfile(300);

    /* 2) Generate Message Digest and signed it */
    if(instructions->signatory)
    {
        fullCondition(CB_STATUS, CTC_PKE_SIGNING, CB_ENCRYPTION,
        NULL, publicKey(instructions->signatory));
        signature.version = instructions->version;
        /* N.B. signature.sigClass already set (above) */

        /* Match the signature instructions to the signing key */
        signature.pk_algor = instructions->signatory->publicKey->pkalg;
        /* was instructions->sig_algor; */

        /* ensure non-standard digest is in new format */
        signature.md_algor = instructions->md_algor;
        if(signature.md_algor != MDA_MD5) signature.version = 3;

        signFile(current, &signature, instructions->signatory);
        if(putSignature(&signature, instructions->signatory))
        {
            if(writeSKEpacket(headers, &signature) != KIO_OKAY)
            {
                simpleCondition(CB_ERROR, PKE_FILE_ERROR, CB_SIGNING);
            }
            release_signature(&signature);
        }
    }
    /* 3) Prefix  Literal header */
    writefileheader(headers, instructions, vf_length(current));
    current = vf_concat(headers, current);

    /* 4) Add compression header and compress (optional) */
    current = doCompress(current, instructions->cp_algor);
    /* 5) Generate conventional key if required */
    if(CEA_NONE != instructions->cv_algor[0].cv_algor)
    {
        if(!instructions->to || !instructions->to[0])
        {
            if(!cb_convKey(instructions->cv_algor,
            &cv_data,
            &cv_len))
            {
                return FALSE;
            }
        }
        else
        {
            if(0 == instructions->cv_algor[0].cv_mode)
            {
                instructions->cv_algor[0].cv_mode = CEM_CFB;
            }
            /* end-stop IDEA+0 to ideacfb */
            if((cv_data =
                generateConvKey(instructions->cv_algor, &cv_len))
                == 0)
            {
                simpleCondition(CB_ERROR, CTC_WRONG_BULK_KEY, CB_ENCRYPTION);
                return FALSE;
            }
        }

        /* 6) PKE encrypt session key for each public key according to the algorithm
         **    of the key; if no keys given then this means conventional encrypt */
        if(instructions->armour == ARM_NONE)
            next = output;
        else
            next = vf_tempfile(vf_length(current) + 6 + 10);

        for(recipients = instructions->to;
            recipients && *recipients;
          ++recipients)
            /* Borland 5.0 obscure error if increment in call to putCK */
            /* so make this a for loop */
        {
            fullCondition(CB_STATUS, CTC_PKE_ENCRYPTING, CB_ENCRYPTION,
            NULL, *recipients);
            putConvKey(next, instructions->version,
            *recipients, instructions->cv_algor,
            cv_len, cv_data);
        }

        /* 7) Conventionally Encrypt           */
        simpleCondition(CB_STATUS, CTC_BULK_ENCRYPTING, CB_ENCRYPTION);
        /* allow for IV length + 2 repeated check bytes per algorithm */
        if(!(CEA_FLEX_FLAG & instructions->cv_algor[0].cv_algor))
            ivlen = cipherBlock(instructions->cv_algor[0].cv_algor) + 2;
        else
        {
            int pass = 0;
            boolean more;
            do
            {
                ivlen += cipherBlock(instructions->cv_algor[pass].cv_algor) + 2;
                more = (boolean)(instructions->cv_algor[pass].cv_algor &
                    CEA_MORE_FLAG);
                pass++;
            }
            while (more);
        }

        writepacketheader(next, CTB_CKE, vf_length(current) + ivlen);
        vf_setpos(current, 0);
        status = bulk_cypher(current, next, instructions->cv_algor,
        cv_data, FALSE);
        if(NULL != cv_data) zfree((void **) &cv_data, cv_len);
        vf_close(current);
    }
    else     /* CEA_NONE - just compressed, maybe about to armour */
    {
        /* ensure positioned to read the lot */
        vf_setpos(current, 0);
        status = TRUE;

        /* if no encryption & no armour - just spit it out */
        if(ARM_NONE == instructions->armour)
        {
            byte buffer[BUFFERSIZE];
            long length;
            while((length = vf_read(buffer, BUFFERSIZE, current)) > 0)
            {
                if(length != vf_write(buffer, length, output))
                {
                    simpleCondition(CB_FATAL, CTC_WRITE_FILE_ERR, CB_ENCRYPTION);
                    return FALSE;
                }
            }
            return TRUE;
        }
        else next = current;        /* put into the right variable to zap */
    }    /* end of non-encryption part */

    /* 8) Ascii armour    */
    if(status && (instructions->armour != ARM_NONE))
    {
        armour_params params = {
            "MESSAGE", VERSIONNO, 0, 1, 1, 0                 };
        armour_return result;

        simpleCondition(CB_STATUS, CTC_ARMOURING, CB_ENCRYPTION);
        params.style = instructions->armour;
        params.name = instructions->filename;
        params.comments = instructions->comments;
        params.max_lines = instructions->max_lines;

        vf_setpos(next, 0);
        result = armour_block(next, output, &params);
        vf_close(next);
        if(ARM_LINE_LIMIT == result)
        {
            instructions->armour = ARM_PGP_MULTI;
            result = ARM_SUCCESS;
        }
        return (boolean)(result == ARM_SUCCESS);
    }
    /* clear sensitive data and VM */
    if(NULL != cv_data) zfree((void **) &cv_data, cv_len);
    return status;
}
#if defined(_MSC_VER) && !defined (__BORLANDC__) 
#pragma warning (default: 4701)
#endif

/*
 *  --------------------------------------------------------------------
 */

boolean signOnly(DataFileP source, DataFileP output, encryptInsts * instructions)
{
    DataFileP current;
    DataFileP headers;
    sigDetails signature = {
        0         };

    assert(sizeof(uint32_t) == 4);
    if(!instructions->signatory) return FALSE;

    /* 1) If text canonicalise : this definitely should do nothing to muck
     *    around with character encoding to the plain-text so that is is
     *    still legible as such; but trimming trailing spaces that
     *    are vulnerable to mail clients definitely is sensible */
    switch(instructions->fileType)
    {
    case 't':
        current = canonicalise(source, 72, (boolean)
            (ARM_PGP_PLAIN == instructions->armour), FALSE);
        signature.sigClass = SIG_TEXT;
        if(ARM_PGP_PLAIN == instructions->armour)
        {
            armour_return result;
            armour_params params = {
                "SIGNED MESSAGE", VERSIONNO,
                ARM_PGP_PLAIN, 1, 1, 0                        };
            simpleCondition(CB_STATUS, CTC_ARMOURING, CB_ENCRYPTION);
            vf_setpos(current, 0);
            result = armour_block(current, output, &params);
            if(ARM_SUCCESS != result) return FALSE;
        }
        break;

    case 'b':
        current = source;
        signature.sigClass = SIG_BINARY;
        if(ARM_PGP_PLAIN == instructions->armour) return FALSE;
        break;

    default:
        return FALSE;
    }

    if(ARM_NONE != instructions->armour)
        headers = vf_tempfile(300);
    else
        headers = output;

    /* 2) Generate Message Digest and signed it */

    fullCondition(CB_STATUS, CTC_PKE_SIGNING, CB_ENCRYPTION,
    NULL, publicKey(instructions->signatory));
    signature.version = instructions->version;
    /* N.B. signature.sigClass already set (above) */
    /* Match the signature instructions to the signing key */
    signature.pk_algor = instructions->signatory->publicKey->pkalg;
    /* was instructions->sig_algor; */

    /* ensure non-standard digest is in PGP2.6 format */
    signature.md_algor = instructions->md_algor;
    if(signature.md_algor != MDA_MD5) signature.version = 3;


    /*
     *  The OpenPGP standard is obscure as to whether we should
     *  generate the signature on the local or the default
     *  character encoding.  It is explicit about dropping
     *  trailing whitespace from the signature, but not
     *  about changing the encoding.
     *
     *  PGP5.0 code does not seem to have heard of anything
     *  other than ISO Latin-1, so is no use.
     */

    signFile(current, &signature, instructions->signatory);
    if(putSignature(&signature, instructions->signatory))
    {
        if(writeSKEpacket(headers, &signature) != KIO_OKAY)
        {
            simpleCondition(CB_ERROR, PKE_FILE_ERROR, CB_SIGNING);
        }
        release_signature(&signature);
    }

    if ('t' == instructions->fileType) vf_close(current);

    /* 3) Ascii armour    */
    if(instructions->armour != ARM_NONE)
    {
        armour_params params = {
            0, VERSIONNO, 0, 1, 1, 0                 };
        armour_return result;
        char mess[8] = "MESSAGE";
        char sign[10]= "SIGNATURE";

        simpleCondition(CB_STATUS, CTC_ARMOURING, CB_ENCRYPTION);

        if(ARM_PGP_PLAIN == instructions->armour)
        {
            params.style = ARM_PGP;
            params.block_type = sign;
        }
        else
        {
            params.style = instructions->armour;
            params.block_type = mess;
        }

        params.name = instructions->filename;
        params.comments = instructions->comments;

        vf_setpos(headers, 0);
        result = armour_block(headers, output, &params);
        vf_close(headers);
        return (boolean)(result == ARM_SUCCESS);
    }
    return TRUE;
}

/*
 *  --------------------------------------------------------------------
 *  --------------------------------------------------------------------
 */


/* Until "result" is actually used in the following routines, ignore the
 * warning that it's not used */
#ifdef __BORLANDC__
#pragma warn -aus
#endif
boolean key_extract(DataFileP file, pubkey * pub_key)
{
    boolean success = FALSE;
    DataFileP binary = vf_tempfile(1000);

    if(writePubKey(binary, pub_key, FALSE, FALSE, NULL))
    {
        char keyName[256];
        char * comments[2] = {
            NULL, NULL                 };
        armour_params params = {
            "PUBLIC KEY", VERSIONNO, ARM_PGP, 1, 1, 0, NULL                };
        armour_return result;

        comments[0] = keyName;
        params.comments = comments;
        name_from_key(pub_key, keyName);
        vf_setpos(binary, 0);
        if((result = armour_block(binary, file, &params)) != ARM_SUCCESS)
        {
            cb_condition condition = {
                CB_ERROR, CB_ARMOUR, 0,
                CB_KEYMAN, NULL, NULL                         };

            condition.code = (short) result;
            condition.pub_key = pub_key;
            cb_information(&condition);
        }
        else
            success = TRUE;
    }
    vf_close(binary);
    return success;
}

boolean key_revoke(DataFileP file, seckey * sec_key, boolean permanently)
{
    boolean success = FALSE;

    if(revoke(sec_key))
    {
        DataFileP binary = vf_tempfile(1000);
        pubkey * pub_key = publicKey(sec_key);

        if(writePubKey(binary, pub_key, FALSE, FALSE, NULL))
        {
            char keyName[256];
            char * comments[3] = {
                "Key revocation certificate", NULL, NULL                         };
            armour_params params = {
                "PUBLIC KEY", VERSIONNO, ARM_PGP, 1, 1, 0                        };
            armour_return result;

            comments[1] = keyName;
            params.comments = comments;
            name_from_key(pub_key, keyName);
            vf_setpos(binary, 0);
            if((result = armour_block(binary, file, &params)) != ARM_SUCCESS)
            {
                cb_condition condition = {
                    CB_ERROR, CB_ARMOUR, 0,
                    CB_KEYMAN, NULL, NULL                                 };
                condition.code = (short) result;
                condition.pub_key = pub_key;
                /*status =*/ cb_information(&condition);
            }
            else
                success = TRUE;
        }
        vf_close(binary);

        if(!permanently) unrevoke(publicKey(sec_key));
    }
    return success;
}
#ifdef __BORLANDC__
#pragma warn .aus
#endif


/* end of file ctc.c */

