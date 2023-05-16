/*
 * The HAVAL hashing function
 *
 * Public domain implementation by Paulo S.L.M. Barreto <pbarreto@uninet.com.br>
 *
 * Version 1.1 (1997.04.07)
 * Modified for CTC compatibility Mr. Tines, Feb. 1998
 * Simplify ROTR; more brackets in the F#() macros to satisfy BorlandC++5.02
 * assume incoming data mis-aligned so have to fix up by hand, either endian
 *
 * =============================================================================
 *
 * Differences from version 1.0 (1997.04.03):
 *
 * - Replaced function F5 by an optimized version (saving a boolean operation).
 *   Thanks to Wei Dai <weidai@eskimo.com> for this improvement.
 *
 * =============================================================================
 *
 * Reference: Zheng, Y., Pieprzyk, J., Seberry, J.:
 * "HAVAL - a one-way hashing algorithm with variable length of output",
 * Advances in Cryptology (AusCrypt'92), LNCS 718 (1993), 83-104, Springer-Verlag.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#include <string.h>

#include "keyconst.h"
#ifdef unix
  #include "haval_i.h_ux"
#else 
  #include "haval_i.h"
#endif
#include "haval.h"
#include "utils.h"


int havalGetPasses(byte ebpType)
{
    int p;
    if(ebpType < MDA_EBP_HAVAL_MIN) return 0;
    else if(ebpType <= MDA_EBP_HAVAL_MAX) p = (ebpType - MDA_EBP_HAVAL_MIN);
    else if (ebpType < MDA_HAVAL_MIN) return 0;
    else if(ebpType <= MDA_HAVAL_MAX) p = (ebpType - MDA_HAVAL_MIN);
    else return 0;

    return 5 -(p/5);
}
int havalGetSize(byte ebpType)
{
    int p;
    if(ebpType < MDA_EBP_HAVAL_MIN) return 0;
    else if(ebpType <= MDA_EBP_HAVAL_MAX) p = (ebpType - MDA_EBP_HAVAL_MIN)%5;
    else if (ebpType < MDA_HAVAL_MIN) return 0;
    else if(ebpType <= MDA_HAVAL_MAX) p = (ebpType - MDA_HAVAL_MIN)%5;
    else return 0;

    switch (p)
    {
    case 0:
        return 256;
    case 1:
        return 224;
    case 2:
        return 192;
    case 3:
        return 160;
    case 4:
        return 128;
    }
    return 0;
}

void havalInit (havalContext **phcp, size_t *length,
int passes, int hashLength, boolean ebp)
{
    havalContext *hcp;
    *phcp = (havalContext *)0;
    /* check number of passes: */
    if (passes != 3 && passes != 4 && passes != 5) {
        return; /* invalid number of passes */
    }
    /* check hash length: */
    if (hashLength != 128 &&
        hashLength != 160 &&
        hashLength != 192 &&
        hashLength != 224 &&
        hashLength != 256) {
        return; /* invalid hash length */
    }

    *length = sizeof(havalContext);
    hcp = *phcp = zmalloc(*length);
    if(!(hcp)) return;

    /* properly initialize HAVAL context: */
    memset (hcp, 0, sizeof (havalContext));
    hcp->passes = (uint16_t) passes;
    hcp->hashLength = (uint16_t) hashLength;
    hcp->digest[0] = 0x243F6A88UL;
    hcp->digest[1] = 0x85A308D3UL;
    hcp->digest[2] = 0x13198A2EUL;
    hcp->digest[3] = 0x03707344UL;
    hcp->digest[4] = 0xA4093822UL;
    hcp->digest[5] = 0x299F31D0UL;
    hcp->digest[6] = 0x082EFA98UL;
    hcp->digest[7] = 0xEC4E6C89UL;

    hcp->ebp = ebp;
    return; /* OK */
}/* havalInit */


void havalUpdate (havalContext *hcp, byte *dataBuffer, uint32_t dataLength)
{
    if (dataBuffer == NULL || dataLength == 0) {
        return; /* nothing to do */
    }

    /* update bit count: */
    if (((uint32_t)dataLength << 3) > (0xFFFFFFFFUL - hcp->bitCount[0]) )
    {
        hcp->bitCount[1]++;
    }
    hcp->bitCount[0] += (uint32_t)dataLength << 3;

    /* if the data buffer is not enough to complete */
    /* the context data block, just append it: */
    if (hcp->occupied + (uint32_t)dataLength < 128) { /* caveat: typecast avoids 16-bit overflow */
        memcpy (&hcp->block[hcp->occupied], dataBuffer, (size_t) dataLength);
        hcp->occupied += (uint16_t) dataLength;
        return; /* delay processing */
    }

    /* complete the context data block: */
    memcpy (&hcp->block[hcp->occupied], dataBuffer, 128 - hcp->occupied);
    dataBuffer += 128 - hcp->occupied;
    dataLength -= (uint32_t) (128 - hcp->occupied);

    switch (hcp->passes) {
    case 3:
        /* process the completed context data block: */
        havalTransform3 (hcp->digest, hcp->block, hcp->temp);
        /* process data in chunks of 128 bytes: */
        while (dataLength >= 128) {
            havalTransform3 (hcp->digest, dataBuffer, hcp->temp);
            dataBuffer += 128;
            dataLength -= 128;
        }
        break;
    case 4:
        /* process the completed context data block: */
        havalTransform4 (hcp->digest, hcp->block, hcp->temp);
        /* process data in chunks of 128 bytes: */
        while (dataLength >= 128) {
            havalTransform4 (hcp->digest, dataBuffer, hcp->temp);
            dataBuffer += 128;
            dataLength -= 128;
        }
        break;
    case 5:
        /* process the completed context data block: */
        havalTransform5 (hcp->digest, hcp->block, hcp->temp);
        /* process data in chunks of 128 bytes: */
        while (dataLength >= 128) {
            havalTransform5 (hcp->digest, dataBuffer, hcp->temp);
            dataBuffer += 128;
            dataLength -= 128;
        }
        break;
    }

    /* delay processing of remaining data: */
    memcpy (hcp->block, dataBuffer, (size_t) dataLength);
    hcp->occupied = (uint16_t) dataLength; /* < 128 */

    return; /* OK */
}/* havalUpdate */


void havalFinal (havalContext **phcp, byte *digest, size_t length)
{
#ifndef HARDWARE_ROTATIONS
    register uint32_t rot_tmp;
#endif /* ?HARDWARE_ROTATIONS */
    uint32_t w;
    havalContext *hcp = *phcp;

    /* append toggle to the context data block: */
    if(hcp->ebp)
    {
        hcp->block[hcp->occupied] = 0x80; /* EBP gets this wrong */
    }
    else
    {
        hcp->block[hcp->occupied] = 0x01; /* corrected from 0x80 */
    }

    /* pad the message with null bytes to make it 944 (mod 1024) bits long: */
    if (hcp->occupied++ >= 118) {
        /* no room for tail data on the current context block */
        memset (&hcp->block[hcp->occupied], 0, 128 - hcp->occupied);
        /* process the completed context data block: */
        switch (hcp->passes) {
        case 3:
            havalTransform3 (hcp->digest, hcp->block, hcp->temp);
            break;
        case 4:
            havalTransform4 (hcp->digest, hcp->block, hcp->temp);
            break;
        case 5:
            havalTransform5 (hcp->digest, hcp->block, hcp->temp);
            break;
        }
        memset (hcp->block, 0, 118);
    }
    else {
        memset (&hcp->block[hcp->occupied], 0, 118 - hcp->occupied);
    }
    /* append tail data and process last (padded) message block: */
    if(hcp->ebp) /* EBP got this wrong too!, hardcoding it at the max */
    {
        hcp->block[118] = (byte) (
        ((256 & 0x03U) << 6) |
            ((5 & 0x07U) << 3) |
            (HAVAL_VERSION & 0x07U));
        hcp->block[119] = (byte)(256 >> 2);
    }
    else
    {
        hcp->block[118] = (byte) (
        ((hcp->hashLength & 0x03U) << 6) |
            ((hcp->passes & 0x07U) << 3) |
            (HAVAL_VERSION & 0x07U));
        hcp->block[119] = (byte)(hcp->hashLength >> 2);
    }
    w = hcp->bitCount[0];
    hcp->block[120] = (byte)(w);
    hcp->block[121] = (byte)(w >> 8);
    hcp->block[122] = (byte)(w >> 16);
    hcp->block[123] = (byte)(w >> 24);
    w = hcp->bitCount[1];
    hcp->block[124] = (byte)(w);
    hcp->block[125] = (byte)(w >> 8);
    hcp->block[126] = (byte)(w >> 16);
    hcp->block[127] = (byte)(w >> 24);
    switch (hcp->passes) {
    case 3:
        havalTransform3 (hcp->digest, hcp->block, hcp->temp);
        break;
    case 4:
        havalTransform4 (hcp->digest, hcp->block, hcp->temp);
        break;
    case 5:
        havalTransform5 (hcp->digest, hcp->block, hcp->temp);
        break;
    }

    /* fold 256-bit digest to fit the desired hash length (blaargh!): */
    switch (hcp->hashLength) {
    case 128:
        hcp->digest[3] +=
            ( (hcp->digest[7] & 0xFF000000UL)
            | (hcp->digest[6] & 0x00FF0000UL)
            | (hcp->digest[5] & 0x0000FF00UL)
            | (hcp->digest[4] & 0x000000FFUL)
            );
        hcp->digest[2] +=
            (((hcp->digest[7] & 0x00FF0000UL)
            | (hcp->digest[6] & 0x0000FF00UL)
            | (hcp->digest[5] & 0x000000FFUL)
            ) << 8) |
            ( (hcp->digest[4] & 0xFF000000UL) >> 24);
        hcp->digest[1] +=
            (((hcp->digest[7] & 0x0000FF00UL)
            | (hcp->digest[6] & 0x000000FFUL)) << 16) |
            (((hcp->digest[5] & 0xFF000000UL)
            | (hcp->digest[4] & 0x00FF0000UL)) >> 16);
        hcp->digest[0] +=
            (((hcp->digest[6] & 0xFF000000UL)
            | (hcp->digest[5] & 0x00FF0000UL)
            | (hcp->digest[4] & 0x0000FF00UL)
            ) >> 8) |
            ( (hcp->digest[7] & 0x000000FFUL) << 24);
        break;
    case 160:
        hcp->digest[4] +=
            ((hcp->digest[7] & 0xFE000000UL) | (hcp->digest[6] & 0x01F80000UL) | (hcp->digest[5] & 0x0007F000UL)) >> 12;
        hcp->digest[3] +=
            ((hcp->digest[7] & 0x01F80000UL) | (hcp->digest[6] & 0x0007F000UL) | (hcp->digest[5] & 0x00000FC0UL)) >> 6;
        hcp->digest[2] +=
            ((hcp->digest[7] & 0x0007F000UL) | (hcp->digest[6] & 0x00000FC0UL) | (hcp->digest[5] & 0x0000003FUL));
        hcp->digest[1] +=
            ROTR
            (((hcp->digest[7] & 0x00000FC0UL) | (hcp->digest[6] & 0x0000003FUL) |
            (hcp->digest[5] & 0xFE000000UL)), 25);
        hcp->digest[0] +=
            ROTR
            ((hcp->digest[7] & 0x0000003FUL) | (hcp->digest[6] & 0xFE000000UL) | (hcp->digest[5] & 0x01F80000UL), 19);
        break;
    case 192:
        hcp->digest[5] +=
            ((hcp->digest[7] & 0xFC000000UL) | (hcp->digest[6] & 0x03E00000UL)) >> 21;
        hcp->digest[4] +=
            ((hcp->digest[7] & 0x03E00000UL) | (hcp->digest[6] & 0x001F0000UL)) >> 16;
        hcp->digest[3] +=
            ((hcp->digest[7] & 0x001F0000UL) | (hcp->digest[6] & 0x0000FC00UL)) >> 10;
        hcp->digest[2] +=
            ((hcp->digest[7] & 0x0000FC00UL) | (hcp->digest[6] & 0x000003E0UL)) >> 5;
        hcp->digest[1] +=
            ((hcp->digest[7] & 0x000003E0UL) | (hcp->digest[6] & 0x0000001FUL));
        hcp->digest[0] +=
            ROTR
            ((hcp->digest[7] & 0x0000001FUL) | (hcp->digest[6] & 0xFC000000UL), 26);
        break;
    case 224:
        hcp->digest[6] += (hcp->digest[7] ) & 0x0000000FUL;
        hcp->digest[5] += (hcp->digest[7] >> 4) & 0x0000001FUL;
        hcp->digest[4] += (hcp->digest[7] >> 9) & 0x0000000FUL;
        hcp->digest[3] += (hcp->digest[7] >> 13) & 0x0000001FUL;
        hcp->digest[2] += (hcp->digest[7] >> 18) & 0x0000000FUL;
        hcp->digest[1] += (hcp->digest[7] >> 22) & 0x0000001FUL;
        hcp->digest[0] += (hcp->digest[7] >> 27) & 0x0000001FUL;
        break;
    case 256:
        break;
    }
    if(!little_endian()) flip32(digest, (byte*) hcp->digest, hcp->hashLength/8);
    else memcpy (digest, hcp->digest, hcp->hashLength/8);

    /* destroy sensitive information: */
    if(length > 0) zfree((void**)phcp, length);
}/* havalFinal */


#ifdef SELF_TESTING

#include <stdio.h>
void * zmalloc(size_t n)
{
    void * result = malloc(n);

    if(result) memset(result, 0, n);
    return result;
}

void zfree (void ** buffer, size_t n)
{
    memset(*buffer, 0, n);
    free(*buffer);
    *buffer = 0;
}

boolean little_endian()
{
#if !defined(__BORLANDC__) && !defined( _MSC_VER)
#error "ensure little_endian() correct!"
#endif
    return TRUE;
}

static void printDigest (const char *tag, const byte *digest, size_t length)
{
    size_t i;

    length >>= 3; /* convert bit length to byte length */
    printf ("%s = ", tag);
    for (i = 0; i < length; i++) {
        printf ("%02X", digest [i]);
    }
    printf ("\n");
}/* printDigest */


int main (int argc, char *argv[])
{
    havalContext *hc;
    byte digest [32];
    size_t length;

    printf ("HAVAL test -- compiled on " __DATE__ " " __TIME__".\n\n");

    switch (argc == 2 ? atoi (argv[1]) : 0) {
    case 0:
    case 3:
        havalInit (&hc, &length, 3, 128, FALSE);
        havalUpdate (hc, (byte*)"", 0);
        havalFinal (&hc, digest, length);
        printf ("HAVAL(3,128,\"\")\n");
        printDigest ("evaluated", digest, 128);
        printf ("expected  = C68F39913F901F3DDF44C707357A7D70\n");

        havalInit (&hc, &length, 3, 160, FALSE);
        havalUpdate (hc, (byte*)"a", 1);
        havalFinal (&hc, digest, length);
        printf ("HAVAL(3,160,\"a\")\n");
        printDigest ("evaluated", digest, 160);
        printf ("expected  = 4DA08F514A7275DBC4CECE4A347385983983A830\n");

        havalInit (&hc, &length, 3, 192, FALSE);
        havalUpdate (hc, (byte*)"HAVAL", strlen ("HAVAL"));
        havalFinal (&hc, digest, length);
        printf ("HAVAL(3,192,\"HAVAL\")\n");
        printDigest ("evaluated", digest, 192);
        printf ("expected  = 8DA26DDAB4317B392B22B638998FE65B0FBE4610D345CF89\n");

        havalInit (&hc, &length, 3, 224, FALSE);
        havalUpdate (hc, (byte*)"0123456789", strlen ("0123456789"));
        havalFinal (&hc, digest, length);
        printf ("HAVAL(3,224,\"0123456789\")\n");
        printDigest ("evaluated", digest, 224);
        printf ("expected  = EE345C97A58190BF0F38BF7CE890231AA5FCF9862BF8E7BEBBF76789\n");

        havalInit (&hc, &length, 3, 256, FALSE);
        havalUpdate (hc, (byte*)"abcdefghijklmnopqrstuvwxyz", strlen ("abcdefghijklmnopqrstuvwxyz"));
        havalFinal (&hc, digest, length);
        printf ("HAVAL(3,256,\"abcdefghijklmnopqrstuvwxyz\")\n");
        printDigest ("evaluated", digest, 256);
        printf ("expected  = 72FAD4BDE1DA8C8332FB60561A780E7F504F21547B98686824FC33FC796AFA76\n");

        havalInit (&hc, &length, 3, 256, FALSE);
        havalUpdate (hc, (byte*)"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789", strlen ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"));
        havalFinal (&hc, digest, length);
        printf ("HAVAL(3,256,\"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789\")\n");
        printDigest ("evaluated", digest, 256);
        printf ("expected  = 899397D96489281E9E76D5E65ABAB751F312E06C06C07C9C1D42ABD31BB6A404\n");
        break;

    case 4:
        havalInit (&hc, &length, 4, 128, FALSE);
        havalUpdate (hc, (byte*)"", 0);
        havalFinal (&hc, digest, length);
        printf ("HAVAL(4,128,\"\")\n");
        printDigest ("evaluated", digest, 128);
        printf ("expected  = EE6BBF4D6A46A679B3A856C88538BB98\n");

        havalInit (&hc, &length, 4, 160, FALSE);
        havalUpdate (hc, (byte*)"a", 1);
        havalFinal (&hc, digest, length);
        printf ("HAVAL(4,160,\"a\")\n");
        printDigest ("evaluated", digest, 160);
        printf ("expected  = E0A5BE29627332034D4DD8A910A1A0E6FE04084D\n");

        havalInit (&hc, &length, 4, 192, FALSE);
        havalUpdate (hc, (byte*)"HAVAL", strlen ("HAVAL"));
        havalFinal (&hc, digest, length);
        printf ("HAVAL(4,192,\"HAVAL\")\n");
        printDigest ("evaluated", digest, 192);
        printf ("expected  = 0C1396D7772689C46773F3DAACA4EFA982ADBFB2F1467EEA\n");

        havalInit (&hc, &length, 4, 224, FALSE);
        havalUpdate (hc, (byte*)"0123456789", strlen ("0123456789"));
        havalFinal (&hc, digest, length);
        printf ("HAVAL(4,224,\"0123456789\")\n");
        printDigest ("evaluated", digest, 224);
        printf ("expected  = BEBD7816F09BAEECF8903B1B9BC672D9FA428E462BA699F814841529\n");

        havalInit (&hc, &length, 4, 256, FALSE);
        havalUpdate (hc, (byte*)"abcdefghijklmnopqrstuvwxyz", strlen ("abcdefghijklmnopqrstuvwxyz"));
        havalFinal (&hc, digest, length);
        printf ("HAVAL(4,256,\"abcdefghijklmnopqrstuvwxyz\")\n");
        printDigest ("evaluated", digest, 256);
        printf ("expected  = 124F6EB645DC407637F8F719CC31250089C89903BF1DB8FAC21EA4614DF4E99A\n");

        havalInit (&hc, &length, 4, 256, FALSE);
        havalUpdate (hc, (byte*)"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789", strlen ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"));
        havalFinal (&hc, digest, length);
        printf ("HAVAL(4,256,\"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789\")\n");
        printDigest ("evaluated", digest, 256);
        printf ("expected  = 46A3A1DFE867EDE652425CCD7FE8006537EAD26372251686BEA286DA152DC35A\n");
        break;

    case 5:
        havalInit (&hc, &length, 5, 128, FALSE);
        havalUpdate (hc, (byte*)"", 0);
        havalFinal (&hc, digest, length);
        printf ("HAVAL(5,128,\"\")\n");
        printDigest ("evaluated", digest, 128);
        printf ("expected  = 184B8482A0C050DCA54B59C7F05BF5DD\n");

        havalInit (&hc, &length, 5, 160, FALSE);
        havalUpdate (hc, (byte*)"a", 1);
        havalFinal (&hc, digest, length);
        printf ("HAVAL(5,160,\"a\")\n");
        printDigest ("evaluated", digest, 160);
        printf ("expected  = F5147DF7ABC5E3C81B031268927C2B5761B5A2B5\n");

        havalInit (&hc, &length, 5, 192, FALSE);
        havalUpdate (hc, (byte*)"HAVAL", strlen ("HAVAL"));
        havalFinal (&hc, digest, length);
        printf ("HAVAL(5,192,\"HAVAL\")\n");
        printDigest ("evaluated", digest, 192);
        printf ("expected  = 794A896D1780B76E2767CC4011BAD8885D5CE6BD835A71B8\n");

        havalInit (&hc, &length, 5, 224, FALSE);
        havalUpdate (hc, (byte*)"0123456789", strlen ("0123456789"));
        havalFinal (&hc, digest, length);
        printf ("HAVAL(5,224,\"0123456789\")\n");
        printDigest ("evaluated", digest, 224);
        printf ("expected  = 59836D19269135BC815F37B2AEB15F894B5435F2C698D57716760F2B\n");

        havalInit (&hc, &length, 5, 256, FALSE);
        havalUpdate (hc, (byte*)"abcdefghijklmnopqrstuvwxyz", strlen ("abcdefghijklmnopqrstuvwxyz"));
        havalFinal (&hc, digest, length);
        printf ("HAVAL(5,256,\"abcdefghijklmnopqrstuvwxyz\")\n");
        printDigest ("evaluated", digest, 256);
        printf ("expected  = C9C7D8AFA159FD9E965CB83FF5EE6F58AEDA352C0EFF005548153A61551C38EE\n");

        havalInit (&hc, &length, 5, 256, FALSE);
        havalUpdate (hc, (byte*)"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789", strlen ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"));
        havalFinal (&hc, digest, length);
        printf ("HAVAL(5,256,\"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789\")\n");
        printDigest ("evaluated", digest, 256);
        printf ("expected  = B45CB6E62F2B1320E4F8F1B0B273D45ADD47C321FD23999DCF403AC37636D963\n");
        break;

    default:
        printf ("usage: haval <passes>\n");
        break;
    }
    return 0;
}/* main */

#endif /* ?SELF_TESTING */
