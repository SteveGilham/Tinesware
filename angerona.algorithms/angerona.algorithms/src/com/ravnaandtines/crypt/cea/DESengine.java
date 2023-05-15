package com.ravnaandtines.crypt.cea;

/**<pre>
 * Class DESengine - based upon des3port.c
 * Graven Imagery 1996
 * v1.0 1996/10/25 20:49:48 EDT
 *
 * Another portable, public domain, version of the Data Encryption Standard.
 *
 * Written with Symantec's THINK (Lightspeed) C by Richard Outerbridge.
 * Thanks to Dan Hoey for the Initial and Inverse permutation code.
 * This is substantially the same engine that appears in Schneier, after
 * ASCII-fication.  The des3 engine has been split out to speed it up. 
 *
 * THIS SOFTWARE PLACED IN THE PUBLIC DOMAIN BY THE AUTHOR
 * 1996/11/10 18:00:00 EST
 *
 * OBLIGATORY IMPORTANT DISCLAIMERS, WARNINGS AND RESTRICTIONS
 * ===========================================================
 *
 * [1] This software is provided "as is" without warranty of fitness for use
 * or suitability for any purpose, express or implied.  Use at your own risk
 * or not at all.  It does, however, "do" DES.  To check your implementation
 * compare against the validation triples at the end of des3port.c
 *
 * [2] This software is "freeware".  It may be freely used and distributed.
 * My copyright in this software has not been abandoned, and is hereby asserted.
 *
 * [3] Exporting encryption software may require an export licence or permit.
 * Consult the appropriate branch(es) of your federal government.
 *
 * Copyright (c) 1988,1989,1990,1991,1992,1996 by Richard Outerbridge.
 * (outer@interlog.com; CIS : [71755,204]) Graven Imagery, 1996.
 *
 * Ported to compile clean on Borland C++ November 15 1997 --Mr.Tines
 * Added procedural initialisation of the SP-box arrays to allow
 * for eventual use of DES variants.
 *
 * Standardise unsigned integer types; remark that the Public Domain declaration
 * above and note [2] contradict each other -- 6-Dec-1997, Mr. Tines
 *
 * Modified 13-Dec-1998 by Mr.Tines - java port, note that communication
 * with the author showed that the intended licensing terms are essentially
 * "do what you will, but keep the original attiribution"
 * @version 1.0 23-Dec-1998
 * @version 2.0 25-Dec-2007
 */
final class DESengine {

    public static final int BLOCKSIZE = 8;

    private DESengine() {
    }

    public static void single(final byte[] inblock, final int offin,
            final byte[] outblock, final int offout, final int[] keys,
            final DES_SPboxes key) {
        int fval, work, right, leftt;
        int round;


        leftt = ((inblock[0 + offin] & 0xFF) << 24) | ((inblock[1 + offin] & 0xFF) << 16) | ((inblock[2 + offin] & 0xFF) << 8) | (inblock[3 + offin] & 0xFF);
        right = ((inblock[4 + offin] & 0xFF) << 24) | ((inblock[5 + offin] & 0xFF) << 16) | ((inblock[6 + offin] & 0xFF) << 8) | (inblock[7 + offin] & 0xFF);
        work = ((leftt >>> 4) ^ right) & 0x0f0f0f0f;
        right ^= work;
        leftt ^= (work << 4);
        work = ((leftt >>> 16) ^ right) & 0x0000ffff;
        right ^= work;
        leftt ^= (work << 16);
        work = ((right >>> 2) ^ leftt) & 0x33333333;
        leftt ^= work;
        right ^= (work << 2);
        work = ((right >>> 8) ^ leftt) & 0x00ff00ff;
        leftt ^= work;
        right ^= (work << 8);
        right = ((right << 1) | ((right >>> 31) & 1)) & 0xffffffff;
        work = (leftt ^ right) & 0xaaaaaaaa;
        leftt ^= work;
        right ^= work;
        leftt = ((leftt << 1) | ((leftt >>> 31) & 1)) & 0xffffffff;

        int s = 0; //NOPMD name

        for (round = 0; round < 8; round++) {
            work = ((right << 28) | (right >>> 4)) ^ keys[s++];
            fval = key.SP7[(work & 0x3f)];
            fval |= key.SP5[((work >>> 8) & 0x3f)];
            fval |= key.SP3[((work >>> 16) & 0x3f)];
            fval |= key.SP1[((work >>> 24) & 0x3f)];
            work = right ^ keys[s++];
            fval |= key.SP8[(work & 0x3f)];
            fval |= key.SP6[((work >>> 8) & 0x3f)];
            fval |= key.SP4[((work >>> 16) & 0x3f)];
            fval |= key.SP2[((work >>> 24) & 0x3f)];
            leftt ^= fval;
            work = ((leftt << 28) | (leftt >>> 4)) ^ keys[s++];
            fval = key.SP7[(work & 0x3f)];
            fval |= key.SP5[((work >>> 8) & 0x3f)];
            fval |= key.SP3[((work >>> 16) & 0x3f)];
            fval |= key.SP1[((work >>> 24) & 0x3f)];
            work = leftt ^ keys[s++];
            fval |= key.SP8[(work & 0x3f)];
            fval |= key.SP6[((work >>> 8) & 0x3f)];
            fval |= key.SP4[((work >>> 16) & 0x3f)];
            fval |= key.SP2[((work >>> 24) & 0x3f)];
            right ^= fval;
        }

        right = (right << 31) | (right >>> 1);
        work = (leftt ^ right) & 0xaaaaaaaa;
        leftt ^= work;
        right ^= work;
        leftt = (leftt << 31) | (leftt >>> 1);
        work = ((leftt >>> 8) ^ right) & 0x00ff00ff;
        right ^= work;
        leftt ^= (work << 8);
        work = ((leftt >>> 2) ^ right) & 0x33333333;
        right ^= work;
        leftt ^= (work << 2);
        work = ((right >>> 16) ^ leftt) & 0x0000ffff;
        leftt ^= work;
        right ^= (work << 16);
        work = ((right >>> 4) ^ leftt) & 0x0f0f0f0f;
        leftt ^= work;
        right ^= (work << 4);
        outblock[0 + offout] = (byte) ((right >>> 24) & 0xFF);
        outblock[1 + offout] = (byte) ((right >>> 16) & 0xFF);
        outblock[2 + offout] = (byte) ((right >>> 8) & 0xFF);
        outblock[3 + offout] = (byte) ((right) & 0xFF);
        outblock[4 + offout] = (byte) ((leftt >>> 24) & 0xFF);
        outblock[5 + offout] = (byte) ((leftt >>> 16) & 0xFF);
        outblock[6 + offout] = (byte) ((leftt >>> 8) & 0xFF);
        outblock[7 + offout] = (byte) ((leftt) & 0xFF);
    }

    public static void triple(final byte[] inblock, final int offin,
            final byte[] outblock, final int offout, 
            final int[] keys, final DES_SPboxes key) {
        int fval, work, right, leftt;
        int round, iterate;

        leftt = ((inblock[0 + offin] & 0xFF) << 24) | ((inblock[1 + offin] & 0xFF) << 16) | ((inblock[2 + offin] & 0xFF) << 8) | (inblock[3 + offin] & 0xFF);
        right = ((inblock[4 + offin] & 0xFF) << 24) | ((inblock[5 + offin] & 0xFF) << 16) | ((inblock[6 + offin] & 0xFF) << 8) | (inblock[7 + offin] & 0xFF);
        work = ((leftt >>> 4) ^ right) & 0x0f0f0f0f;
        right ^= work;
        leftt ^= (work << 4);
        work = ((leftt >>> 16) ^ right) & 0x0000ffff;
        right ^= work;
        leftt ^= (work << 16);
        work = ((right >>> 2) ^ leftt) & 0x33333333;
        leftt ^= work;
        right ^= (work << 2);
        work = ((right >>> 8) ^ leftt) & 0x00ff00ff;
        leftt ^= work;
        right ^= (work << 8);
        right = ((right << 1) | ((right >>> 31) & 1)) & 0xffffffff;
        work = (leftt ^ right) & 0xaaaaaaaa;
        leftt ^= work;
        right ^= work;
        leftt = ((leftt << 1) | ((leftt >>> 31) & 1)) & 0xffffffff;

        // replace goto in 'C' original.
        iterate = 0;
        work = right;
        right = leftt;
        leftt = work;
        int s = 0; //NOPMD

        while (iterate < 3) {
            work = right;
            right = leftt;
            leftt = work;
            ++iterate;
            for (round = 0; round < 8; round++) {
                work = ((right << 28) | (right >>> 4)) ^ keys[s++];
                fval = key.SP7[(work & 0x3f)];
                fval |= key.SP5[((work >>> 8) & 0x3f)];
                fval |= key.SP3[((work >>> 16) & 0x3f)];
                fval |= key.SP1[((work >>> 24) & 0x3f)];
                work = right ^ keys[s++];
                fval |= key.SP8[(work & 0x3f)];
                fval |= key.SP6[((work >>> 8) & 0x3f)];
                fval |= key.SP4[((work >>> 16) & 0x3f)];
                fval |= key.SP2[((work >>> 24) & 0x3f)];
                leftt ^= fval;
                work = ((leftt << 28) | (leftt >>> 4)) ^ keys[s++];
                fval = key.SP7[(work & 0x3f)];
                fval |= key.SP5[((work >>> 8) & 0x3f)];
                fval |= key.SP3[((work >>> 16) & 0x3f)];
                fval |= key.SP1[((work >>> 24) & 0x3f)];
                work = leftt ^ keys[s++];
                fval |= key.SP8[(work & 0x3f)];
                fval |= key.SP6[((work >>> 8) & 0x3f)];
                fval |= key.SP4[((work >>> 16) & 0x3f)];
                fval |= key.SP2[((work >>> 24) & 0x3f)];
                right ^= fval;
            }
        }

        right = (right << 31) | (right >>> 1);
        work = (leftt ^ right) & 0xaaaaaaaa;
        leftt ^= work;
        right ^= work;
        leftt = (leftt << 31) | (leftt >>> 1);
        work = ((leftt >>> 8) ^ right) & 0x00ff00ff;
        right ^= work;
        leftt ^= (work << 8);
        work = ((leftt >>> 2) ^ right) & 0x33333333;
        right ^= work;
        leftt ^= (work << 2);
        work = ((right >>> 16) ^ leftt) & 0x0000ffff;
        leftt ^= work;
        right ^= (work << 16);
        work = ((right >>> 4) ^ leftt) & 0x0f0f0f0f;
        leftt ^= work;
        right ^= (work << 4);
        outblock[0 + offout] = (byte) ((right >>> 24) & 0xFF);
        outblock[1 + offout] = (byte) ((right >>> 16) & 0xFF);
        outblock[2 + offout] = (byte) ((right >>> 8) & 0xFF);
        outblock[3 + offout] = (byte) ((right) & 0xFF);
        outblock[4 + offout] = (byte) ((leftt >>> 24) & 0xFF);
        outblock[5 + offout] = (byte) ((leftt >>> 16) & 0xFF);
        outblock[6 + offout] = (byte) ((leftt >>> 8) & 0xFF);
        outblock[7 + offout] = (byte) ((leftt) & 0xFF);
    }
}
