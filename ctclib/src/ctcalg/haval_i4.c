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

#ifdef unix
  #include "haval_i.h_ux"
#else 
  #include "haval_i.h" 
#endif
#include "utils.h"


void havalTransform4 (uint32_t E[8], const byte D[128], uint32_t T[8])
{
#ifndef HARDWARE_ROTATIONS
    register uint32_t rot_tmp;
#endif /* ?HARDWARE_ROTATIONS */
    uint32_t W[32];

    if(!little_endian()) flip32((byte*)W, (byte*)D, 128);
    else memcpy(W, D, 128);

    /* PASS 1: */

    T[7] = ROTR (F1 (E[2], E[6], E[1], E[4], E[5], E[3], E[0]), 7) + ROTR (E[7], 11) + W[ 0];
    T[6] = ROTR (F1 (E[1], E[5], E[0], E[3], E[4], E[2], T[7]), 7) + ROTR (E[6], 11) + W[ 1];
    T[5] = ROTR (F1 (E[0], E[4], T[7], E[2], E[3], E[1], T[6]), 7) + ROTR (E[5], 11) + W[ 2];
    T[4] = ROTR (F1 (T[7], E[3], T[6], E[1], E[2], E[0], T[5]), 7) + ROTR (E[4], 11) + W[ 3];
    T[3] = ROTR (F1 (T[6], E[2], T[5], E[0], E[1], T[7], T[4]), 7) + ROTR (E[3], 11) + W[ 4];
    T[2] = ROTR (F1 (T[5], E[1], T[4], T[7], E[0], T[6], T[3]), 7) + ROTR (E[2], 11) + W[ 5];
    T[1] = ROTR (F1 (T[4], E[0], T[3], T[6], T[7], T[5], T[2]), 7) + ROTR (E[1], 11) + W[ 6];
    T[0] = ROTR (F1 (T[3], T[7], T[2], T[5], T[6], T[4], T[1]), 7) + ROTR (E[0], 11) + W[ 7];

    T[7] = ROTR (F1 (T[2], T[6], T[1], T[4], T[5], T[3], T[0]), 7) + ROTR (T[7], 11) + W[ 8];
    T[6] = ROTR (F1 (T[1], T[5], T[0], T[3], T[4], T[2], T[7]), 7) + ROTR (T[6], 11) + W[ 9];
    T[5] = ROTR (F1 (T[0], T[4], T[7], T[2], T[3], T[1], T[6]), 7) + ROTR (T[5], 11) + W[10];
    T[4] = ROTR (F1 (T[7], T[3], T[6], T[1], T[2], T[0], T[5]), 7) + ROTR (T[4], 11) + W[11];
    T[3] = ROTR (F1 (T[6], T[2], T[5], T[0], T[1], T[7], T[4]), 7) + ROTR (T[3], 11) + W[12];
    T[2] = ROTR (F1 (T[5], T[1], T[4], T[7], T[0], T[6], T[3]), 7) + ROTR (T[2], 11) + W[13];
    T[1] = ROTR (F1 (T[4], T[0], T[3], T[6], T[7], T[5], T[2]), 7) + ROTR (T[1], 11) + W[14];
    T[0] = ROTR (F1 (T[3], T[7], T[2], T[5], T[6], T[4], T[1]), 7) + ROTR (T[0], 11) + W[15];

    T[7] = ROTR (F1 (T[2], T[6], T[1], T[4], T[5], T[3], T[0]), 7) + ROTR (T[7], 11) + W[16];
    T[6] = ROTR (F1 (T[1], T[5], T[0], T[3], T[4], T[2], T[7]), 7) + ROTR (T[6], 11) + W[17];
    T[5] = ROTR (F1 (T[0], T[4], T[7], T[2], T[3], T[1], T[6]), 7) + ROTR (T[5], 11) + W[18];
    T[4] = ROTR (F1 (T[7], T[3], T[6], T[1], T[2], T[0], T[5]), 7) + ROTR (T[4], 11) + W[19];
    T[3] = ROTR (F1 (T[6], T[2], T[5], T[0], T[1], T[7], T[4]), 7) + ROTR (T[3], 11) + W[20];
    T[2] = ROTR (F1 (T[5], T[1], T[4], T[7], T[0], T[6], T[3]), 7) + ROTR (T[2], 11) + W[21];
    T[1] = ROTR (F1 (T[4], T[0], T[3], T[6], T[7], T[5], T[2]), 7) + ROTR (T[1], 11) + W[22];
    T[0] = ROTR (F1 (T[3], T[7], T[2], T[5], T[6], T[4], T[1]), 7) + ROTR (T[0], 11) + W[23];

    T[7] = ROTR (F1 (T[2], T[6], T[1], T[4], T[5], T[3], T[0]), 7) + ROTR (T[7], 11) + W[24];
    T[6] = ROTR (F1 (T[1], T[5], T[0], T[3], T[4], T[2], T[7]), 7) + ROTR (T[6], 11) + W[25];
    T[5] = ROTR (F1 (T[0], T[4], T[7], T[2], T[3], T[1], T[6]), 7) + ROTR (T[5], 11) + W[26];
    T[4] = ROTR (F1 (T[7], T[3], T[6], T[1], T[2], T[0], T[5]), 7) + ROTR (T[4], 11) + W[27];
    T[3] = ROTR (F1 (T[6], T[2], T[5], T[0], T[1], T[7], T[4]), 7) + ROTR (T[3], 11) + W[28];
    T[2] = ROTR (F1 (T[5], T[1], T[4], T[7], T[0], T[6], T[3]), 7) + ROTR (T[2], 11) + W[29];
    T[1] = ROTR (F1 (T[4], T[0], T[3], T[6], T[7], T[5], T[2]), 7) + ROTR (T[1], 11) + W[30];
    T[0] = ROTR (F1 (T[3], T[7], T[2], T[5], T[6], T[4], T[1]), 7) + ROTR (T[0], 11) + W[31];

    /* PASS 2: */

    T[7] = ROTR (F2 (T[3], T[5], T[2], T[0], T[1], T[6], T[4]), 7) + ROTR (T[7], 11) + W[ 5] + 0x452821E6UL;
    T[6] = ROTR (F2 (T[2], T[4], T[1], T[7], T[0], T[5], T[3]), 7) + ROTR (T[6], 11) + W[14] + 0x38D01377UL;
    T[5] = ROTR (F2 (T[1], T[3], T[0], T[6], T[7], T[4], T[2]), 7) + ROTR (T[5], 11) + W[26] + 0xBE5466CFUL;
    T[4] = ROTR (F2 (T[0], T[2], T[7], T[5], T[6], T[3], T[1]), 7) + ROTR (T[4], 11) + W[18] + 0x34E90C6CUL;
    T[3] = ROTR (F2 (T[7], T[1], T[6], T[4], T[5], T[2], T[0]), 7) + ROTR (T[3], 11) + W[11] + 0xC0AC29B7UL;
    T[2] = ROTR (F2 (T[6], T[0], T[5], T[3], T[4], T[1], T[7]), 7) + ROTR (T[2], 11) + W[28] + 0xC97C50DDUL;
    T[1] = ROTR (F2 (T[5], T[7], T[4], T[2], T[3], T[0], T[6]), 7) + ROTR (T[1], 11) + W[ 7] + 0x3F84D5B5UL;
    T[0] = ROTR (F2 (T[4], T[6], T[3], T[1], T[2], T[7], T[5]), 7) + ROTR (T[0], 11) + W[16] + 0xB5470917UL;

    T[7] = ROTR (F2 (T[3], T[5], T[2], T[0], T[1], T[6], T[4]), 7) + ROTR (T[7], 11) + W[ 0] + 0x9216D5D9UL;
    T[6] = ROTR (F2 (T[2], T[4], T[1], T[7], T[0], T[5], T[3]), 7) + ROTR (T[6], 11) + W[23] + 0x8979FB1BUL;
    T[5] = ROTR (F2 (T[1], T[3], T[0], T[6], T[7], T[4], T[2]), 7) + ROTR (T[5], 11) + W[20] + 0xD1310BA6UL;
    T[4] = ROTR (F2 (T[0], T[2], T[7], T[5], T[6], T[3], T[1]), 7) + ROTR (T[4], 11) + W[22] + 0x98DFB5ACUL;
    T[3] = ROTR (F2 (T[7], T[1], T[6], T[4], T[5], T[2], T[0]), 7) + ROTR (T[3], 11) + W[ 1] + 0x2FFD72DBUL;
    T[2] = ROTR (F2 (T[6], T[0], T[5], T[3], T[4], T[1], T[7]), 7) + ROTR (T[2], 11) + W[10] + 0xD01ADFB7UL;
    T[1] = ROTR (F2 (T[5], T[7], T[4], T[2], T[3], T[0], T[6]), 7) + ROTR (T[1], 11) + W[ 4] + 0xB8E1AFEDUL;
    T[0] = ROTR (F2 (T[4], T[6], T[3], T[1], T[2], T[7], T[5]), 7) + ROTR (T[0], 11) + W[ 8] + 0x6A267E96UL;

    T[7] = ROTR (F2 (T[3], T[5], T[2], T[0], T[1], T[6], T[4]), 7) + ROTR (T[7], 11) + W[30] + 0xBA7C9045UL;
    T[6] = ROTR (F2 (T[2], T[4], T[1], T[7], T[0], T[5], T[3]), 7) + ROTR (T[6], 11) + W[ 3] + 0xF12C7F99UL;
    T[5] = ROTR (F2 (T[1], T[3], T[0], T[6], T[7], T[4], T[2]), 7) + ROTR (T[5], 11) + W[21] + 0x24A19947UL;
    T[4] = ROTR (F2 (T[0], T[2], T[7], T[5], T[6], T[3], T[1]), 7) + ROTR (T[4], 11) + W[ 9] + 0xB3916CF7UL;
    T[3] = ROTR (F2 (T[7], T[1], T[6], T[4], T[5], T[2], T[0]), 7) + ROTR (T[3], 11) + W[17] + 0x0801F2E2UL;
    T[2] = ROTR (F2 (T[6], T[0], T[5], T[3], T[4], T[1], T[7]), 7) + ROTR (T[2], 11) + W[24] + 0x858EFC16UL;
    T[1] = ROTR (F2 (T[5], T[7], T[4], T[2], T[3], T[0], T[6]), 7) + ROTR (T[1], 11) + W[29] + 0x636920D8UL;
    T[0] = ROTR (F2 (T[4], T[6], T[3], T[1], T[2], T[7], T[5]), 7) + ROTR (T[0], 11) + W[ 6] + 0x71574E69UL;

    T[7] = ROTR (F2 (T[3], T[5], T[2], T[0], T[1], T[6], T[4]), 7) + ROTR (T[7], 11) + W[19] + 0xA458FEA3UL;
    T[6] = ROTR (F2 (T[2], T[4], T[1], T[7], T[0], T[5], T[3]), 7) + ROTR (T[6], 11) + W[12] + 0xF4933D7EUL;
    T[5] = ROTR (F2 (T[1], T[3], T[0], T[6], T[7], T[4], T[2]), 7) + ROTR (T[5], 11) + W[15] + 0x0D95748FUL;
    T[4] = ROTR (F2 (T[0], T[2], T[7], T[5], T[6], T[3], T[1]), 7) + ROTR (T[4], 11) + W[13] + 0x728EB658UL;
    T[3] = ROTR (F2 (T[7], T[1], T[6], T[4], T[5], T[2], T[0]), 7) + ROTR (T[3], 11) + W[ 2] + 0x718BCD58UL;
    T[2] = ROTR (F2 (T[6], T[0], T[5], T[3], T[4], T[1], T[7]), 7) + ROTR (T[2], 11) + W[25] + 0x82154AEEUL;
    T[1] = ROTR (F2 (T[5], T[7], T[4], T[2], T[3], T[0], T[6]), 7) + ROTR (T[1], 11) + W[31] + 0x7B54A41DUL;
    T[0] = ROTR (F2 (T[4], T[6], T[3], T[1], T[2], T[7], T[5]), 7) + ROTR (T[0], 11) + W[27] + 0xC25A59B5UL;

    /* PASS 3: */

    T[7] = ROTR (F3 (T[1], T[4], T[3], T[6], T[0], T[2], T[5]), 7) + ROTR (T[7], 11) + W[19] + 0x9C30D539UL;
    T[6] = ROTR (F3 (T[0], T[3], T[2], T[5], T[7], T[1], T[4]), 7) + ROTR (T[6], 11) + W[ 9] + 0x2AF26013UL;
    T[5] = ROTR (F3 (T[7], T[2], T[1], T[4], T[6], T[0], T[3]), 7) + ROTR (T[5], 11) + W[ 4] + 0xC5D1B023UL;
    T[4] = ROTR (F3 (T[6], T[1], T[0], T[3], T[5], T[7], T[2]), 7) + ROTR (T[4], 11) + W[20] + 0x286085F0UL;
    T[3] = ROTR (F3 (T[5], T[0], T[7], T[2], T[4], T[6], T[1]), 7) + ROTR (T[3], 11) + W[28] + 0xCA417918UL;
    T[2] = ROTR (F3 (T[4], T[7], T[6], T[1], T[3], T[5], T[0]), 7) + ROTR (T[2], 11) + W[17] + 0xB8DB38EFUL;
    T[1] = ROTR (F3 (T[3], T[6], T[5], T[0], T[2], T[4], T[7]), 7) + ROTR (T[1], 11) + W[ 8] + 0x8E79DCB0UL;
    T[0] = ROTR (F3 (T[2], T[5], T[4], T[7], T[1], T[3], T[6]), 7) + ROTR (T[0], 11) + W[22] + 0x603A180EUL;

    T[7] = ROTR (F3 (T[1], T[4], T[3], T[6], T[0], T[2], T[5]), 7) + ROTR (T[7], 11) + W[29] + 0x6C9E0E8BUL;
    T[6] = ROTR (F3 (T[0], T[3], T[2], T[5], T[7], T[1], T[4]), 7) + ROTR (T[6], 11) + W[14] + 0xB01E8A3EUL;
    T[5] = ROTR (F3 (T[7], T[2], T[1], T[4], T[6], T[0], T[3]), 7) + ROTR (T[5], 11) + W[25] + 0xD71577C1UL;
    T[4] = ROTR (F3 (T[6], T[1], T[0], T[3], T[5], T[7], T[2]), 7) + ROTR (T[4], 11) + W[12] + 0xBD314B27UL;
    T[3] = ROTR (F3 (T[5], T[0], T[7], T[2], T[4], T[6], T[1]), 7) + ROTR (T[3], 11) + W[24] + 0x78AF2FDAUL;
    T[2] = ROTR (F3 (T[4], T[7], T[6], T[1], T[3], T[5], T[0]), 7) + ROTR (T[2], 11) + W[30] + 0x55605C60UL;
    T[1] = ROTR (F3 (T[3], T[6], T[5], T[0], T[2], T[4], T[7]), 7) + ROTR (T[1], 11) + W[16] + 0xE65525F3UL;
    T[0] = ROTR (F3 (T[2], T[5], T[4], T[7], T[1], T[3], T[6]), 7) + ROTR (T[0], 11) + W[26] + 0xAA55AB94UL;

    T[7] = ROTR (F3 (T[1], T[4], T[3], T[6], T[0], T[2], T[5]), 7) + ROTR (T[7], 11) + W[31] + 0x57489862UL;
    T[6] = ROTR (F3 (T[0], T[3], T[2], T[5], T[7], T[1], T[4]), 7) + ROTR (T[6], 11) + W[15] + 0x63E81440UL;
    T[5] = ROTR (F3 (T[7], T[2], T[1], T[4], T[6], T[0], T[3]), 7) + ROTR (T[5], 11) + W[ 7] + 0x55CA396AUL;
    T[4] = ROTR (F3 (T[6], T[1], T[0], T[3], T[5], T[7], T[2]), 7) + ROTR (T[4], 11) + W[ 3] + 0x2AAB10B6UL;
    T[3] = ROTR (F3 (T[5], T[0], T[7], T[2], T[4], T[6], T[1]), 7) + ROTR (T[3], 11) + W[ 1] + 0xB4CC5C34UL;
    T[2] = ROTR (F3 (T[4], T[7], T[6], T[1], T[3], T[5], T[0]), 7) + ROTR (T[2], 11) + W[ 0] + 0x1141E8CEUL;
    T[1] = ROTR (F3 (T[3], T[6], T[5], T[0], T[2], T[4], T[7]), 7) + ROTR (T[1], 11) + W[18] + 0xA15486AFUL;
    T[0] = ROTR (F3 (T[2], T[5], T[4], T[7], T[1], T[3], T[6]), 7) + ROTR (T[0], 11) + W[27] + 0x7C72E993UL;

    T[7] = ROTR (F3 (T[1], T[4], T[3], T[6], T[0], T[2], T[5]), 7) + ROTR (T[7], 11) + W[13] + 0xB3EE1411UL;
    T[6] = ROTR (F3 (T[0], T[3], T[2], T[5], T[7], T[1], T[4]), 7) + ROTR (T[6], 11) + W[ 6] + 0x636FBC2AUL;
    T[5] = ROTR (F3 (T[7], T[2], T[1], T[4], T[6], T[0], T[3]), 7) + ROTR (T[5], 11) + W[21] + 0x2BA9C55DUL;
    T[4] = ROTR (F3 (T[6], T[1], T[0], T[3], T[5], T[7], T[2]), 7) + ROTR (T[4], 11) + W[10] + 0x741831F6UL;
    T[3] = ROTR (F3 (T[5], T[0], T[7], T[2], T[4], T[6], T[1]), 7) + ROTR (T[3], 11) + W[23] + 0xCE5C3E16UL;
    T[2] = ROTR (F3 (T[4], T[7], T[6], T[1], T[3], T[5], T[0]), 7) + ROTR (T[2], 11) + W[11] + 0x9B87931EUL;
    T[1] = ROTR (F3 (T[3], T[6], T[5], T[0], T[2], T[4], T[7]), 7) + ROTR (T[1], 11) + W[ 5] + 0xAFD6BA33UL;
    T[0] = ROTR (F3 (T[2], T[5], T[4], T[7], T[1], T[3], T[6]), 7) + ROTR (T[0], 11) + W[ 2] + 0x6C24CF5CUL;

    /* PASS 4: */

    T[7] = ROTR (F4 (T[6], T[4], T[0], T[5], T[2], T[1], T[3]), 7) + ROTR (T[7], 11) + W[24] + 0x7A325381UL;
    T[6] = ROTR (F4 (T[5], T[3], T[7], T[4], T[1], T[0], T[2]), 7) + ROTR (T[6], 11) + W[ 4] + 0x28958677UL;
    T[5] = ROTR (F4 (T[4], T[2], T[6], T[3], T[0], T[7], T[1]), 7) + ROTR (T[5], 11) + W[ 0] + 0x3B8F4898UL;
    T[4] = ROTR (F4 (T[3], T[1], T[5], T[2], T[7], T[6], T[0]), 7) + ROTR (T[4], 11) + W[14] + 0x6B4BB9AFUL;
    T[3] = ROTR (F4 (T[2], T[0], T[4], T[1], T[6], T[5], T[7]), 7) + ROTR (T[3], 11) + W[ 2] + 0xC4BFE81BUL;
    T[2] = ROTR (F4 (T[1], T[7], T[3], T[0], T[5], T[4], T[6]), 7) + ROTR (T[2], 11) + W[ 7] + 0x66282193UL;
    T[1] = ROTR (F4 (T[0], T[6], T[2], T[7], T[4], T[3], T[5]), 7) + ROTR (T[1], 11) + W[28] + 0x61D809CCUL;
    T[0] = ROTR (F4 (T[7], T[5], T[1], T[6], T[3], T[2], T[4]), 7) + ROTR (T[0], 11) + W[23] + 0xFB21A991UL;

    T[7] = ROTR (F4 (T[6], T[4], T[0], T[5], T[2], T[1], T[3]), 7) + ROTR (T[7], 11) + W[26] + 0x487CAC60UL;
    T[6] = ROTR (F4 (T[5], T[3], T[7], T[4], T[1], T[0], T[2]), 7) + ROTR (T[6], 11) + W[ 6] + 0x5DEC8032UL;
    T[5] = ROTR (F4 (T[4], T[2], T[6], T[3], T[0], T[7], T[1]), 7) + ROTR (T[5], 11) + W[30] + 0xEF845D5DUL;
    T[4] = ROTR (F4 (T[3], T[1], T[5], T[2], T[7], T[6], T[0]), 7) + ROTR (T[4], 11) + W[20] + 0xE98575B1UL;
    T[3] = ROTR (F4 (T[2], T[0], T[4], T[1], T[6], T[5], T[7]), 7) + ROTR (T[3], 11) + W[18] + 0xDC262302UL;
    T[2] = ROTR (F4 (T[1], T[7], T[3], T[0], T[5], T[4], T[6]), 7) + ROTR (T[2], 11) + W[25] + 0xEB651B88UL;
    T[1] = ROTR (F4 (T[0], T[6], T[2], T[7], T[4], T[3], T[5]), 7) + ROTR (T[1], 11) + W[19] + 0x23893E81UL;
    T[0] = ROTR (F4 (T[7], T[5], T[1], T[6], T[3], T[2], T[4]), 7) + ROTR (T[0], 11) + W[ 3] + 0xD396ACC5UL;

    T[7] = ROTR (F4 (T[6], T[4], T[0], T[5], T[2], T[1], T[3]), 7) + ROTR (T[7], 11) + W[22] + 0x0F6D6FF3UL;
    T[6] = ROTR (F4 (T[5], T[3], T[7], T[4], T[1], T[0], T[2]), 7) + ROTR (T[6], 11) + W[11] + 0x83F44239UL;
    T[5] = ROTR (F4 (T[4], T[2], T[6], T[3], T[0], T[7], T[1]), 7) + ROTR (T[5], 11) + W[31] + 0x2E0B4482UL;
    T[4] = ROTR (F4 (T[3], T[1], T[5], T[2], T[7], T[6], T[0]), 7) + ROTR (T[4], 11) + W[21] + 0xA4842004UL;
    T[3] = ROTR (F4 (T[2], T[0], T[4], T[1], T[6], T[5], T[7]), 7) + ROTR (T[3], 11) + W[ 8] + 0x69C8F04AUL;
    T[2] = ROTR (F4 (T[1], T[7], T[3], T[0], T[5], T[4], T[6]), 7) + ROTR (T[2], 11) + W[27] + 0x9E1F9B5EUL;
    T[1] = ROTR (F4 (T[0], T[6], T[2], T[7], T[4], T[3], T[5]), 7) + ROTR (T[1], 11) + W[12] + 0x21C66842UL;
    T[0] = ROTR (F4 (T[7], T[5], T[1], T[6], T[3], T[2], T[4]), 7) + ROTR (T[0], 11) + W[ 9] + 0xF6E96C9AUL;

    E[7] += T[7] = ROTR (F4 (T[6], T[4], T[0], T[5], T[2], T[1], T[3]), 7) + ROTR (T[7], 11) + W[ 1] + 0x670C9C61UL;
    E[6] += T[6] = ROTR (F4 (T[5], T[3], T[7], T[4], T[1], T[0], T[2]), 7) + ROTR (T[6], 11) + W[29] + 0xABD388F0UL;
    E[5] += T[5] = ROTR (F4 (T[4], T[2], T[6], T[3], T[0], T[7], T[1]), 7) + ROTR (T[5], 11) + W[ 5] + 0x6A51A0D2UL;
    E[4] += T[4] = ROTR (F4 (T[3], T[1], T[5], T[2], T[7], T[6], T[0]), 7) + ROTR (T[4], 11) + W[15] + 0xD8542F68UL;
    E[3] += T[3] = ROTR (F4 (T[2], T[0], T[4], T[1], T[6], T[5], T[7]), 7) + ROTR (T[3], 11) + W[17] + 0x960FA728UL;
    E[2] += T[2] = ROTR (F4 (T[1], T[7], T[3], T[0], T[5], T[4], T[6]), 7) + ROTR (T[2], 11) + W[10] + 0xAB5133A3UL;
    E[1] += T[1] = ROTR (F4 (T[0], T[6], T[2], T[7], T[4], T[3], T[5]), 7) + ROTR (T[1], 11) + W[16] + 0x6EEF0B6CUL;
    E[0] += T[0] = ROTR (F4 (T[7], T[5], T[1], T[6], T[3], T[2], T[4]), 7) + ROTR (T[0], 11) + W[13] + 0x137A3BE4UL;

    memset (W, 0, sizeof (W));
}/* havalTransform4 */

