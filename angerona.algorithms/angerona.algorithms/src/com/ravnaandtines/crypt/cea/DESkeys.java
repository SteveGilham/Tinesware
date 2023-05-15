package com.ravnaandtines.crypt.cea;

/** <pre> Class DESKeys based on keysched.c
 * Graven Imagery 1996
 * v1.1 1996/10/25 20:49:48 EDT
 *
 * Written with Symantec's THINK (Lightspeed) C by Richard Outerbridge.
 * This code inspired by an idea from Peter Trei.
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
 * Modified 6-Dec-1997 by Mr. Tines for CTC, who notes that the Public Domain
 * declaration and copyright assertion above are not self consistent, to
 * standardise the unsigned types.
 *
 * Modified 13-Dec-1998 by Mr.Tines - java port, note that communication
 * with the author showed that the intended licensing terms are essentially
 * "do what you will, but keep the original attiribution"
 * @version 1.0 23-Dec-1998
 * @version 2.0 25-Dec-2007* 
 */
final class DESkeys { //NOPMD complex

    public static final boolean EN0 = false;		/* MODE == encrypt */

    public static final boolean DE1 = true;		/* MODE == decrypt */

    public static final int LONGS = 32;
    public static final int KEYSIZE = 8;
    private static final int[][][] PRECOMP = {
        {{ /* 00 : 8000000000000000 */
1, 3, 4, 7, 9, 10, 12, 17, 18, 20, 23, 25, 26, 30, -1, -1
}, {
0x00100000, 0x04000000, 0x00040000, 0x00010000, 0x10000000,
0x00020000, 0x04000000, 0x02000000, 0x00100000, 0x10000000,
0x08000000, 0x00020000, 0x08000000, 0x00010000, 0xFFFFFFFF
}
},
        {{ /* 01 : 4000000000000000 */
1, 2, 4, 7, 8, 11, 12, 15, 16, 19, 22, 25, 26, 28, 31, -1
}, {
0x08000000, 0x20000000, 0x00080000, 0x01000000, 0x00010000,
0x04000000, 0x00040000, 0x00010000, 0x02000000, 0x00080000,
0x00200000, 0x02000000, 0x00100000, 0x10000000, 0x00200000
}
},
        {{ /* 02 : 2000000000000000 */
0, 5, 9, 10, 12, 15, 19, 20, 23, 24, 27, 30, -1, -1, -1, -1
}, {
0x00200000, 0x00040000, 0x00200000, 0x20000000, 0x00080000,
0x01000000, 0x00100000, 0x01000000, 0x20000000, 0x02000000,
0x00080000, 0x04000000, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF
}
},
        {{ /* 03 : 1000000000000000 */
0, 4, 7, 9, 11, 12, 15, 16, 18, 20, 25, 26, 29, 30, -1, -1
}, {
0x00000100, 0x00001000, 0x00000008, 0x00000100, 0x00000020,
0x00000020, 0x00000010, 0x00002000, 0x00000008, 0x00000400,
0x00000200, 0x00000800, 0x00000002, 0x00000004, 0xFFFFFFFF
}
},
        {{ /* 04 : 0800000000000000 */
1, 3, 5, 7, 8, 11, 13, 16, 21, 22, 25, 26, 28, 30, -1, -1
}, {
0x00000800, 0x00000008, 0x00000100, 0x00000020, 0x00000020,
0x00000010, 0x00001000, 0x00000400, 0x00000200, 0x00000800,
0x00000002, 0x00000100, 0x00000001, 0x00001000, 0xFFFFFFFF
}
},
        {{ /* 05 : 0400000000000000 */
3, 5, 9, 10, 13, 15, 17, 18, 20, 23, 24, 26, 29, 30, -1, -1
}, {
0x00000010, 0x00001000, 0x00000004, 0x00000002, 0x00000001,
0x00002000, 0x00000002, 0x00000100, 0x00000001, 0x00000800,
0x00000010, 0x00000200, 0x00000400, 0x00000020, 0xFFFFFFFF
}
},
        {{ /* 06 : 0200000000000000 */
2, 5, 7, 8, 12, 15, 16, 18, 21, 24, 26, 28, 31, -1, -1, -1
}, {
0x00000002, 0x00000001, 0x00002000, 0x00000004, 0x00001000,
0x00000008, 0x00000010, 0x00000200, 0x00000400, 0x00002000,
0x00000008, 0x00000400, 0x00000004, 0xFFFFFFFF, 0xFFFFFFFF
}
},
        {{ /* 07 : 0080000000000000 */
1, 2, 5, 6, 9, 12, 15, 17, 21, 22, 24, 27, 28, 31, -1, -1
}, {
0x04000000, 0x01000000, 0x20000000, 0x02000000, 0x00080000,
0x00200000, 0x02000000, 0x00040000, 0x00200000, 0x20000000,
0x00080000, 0x01000000, 0x00010000, 0x00100000, 0xFFFFFFFF
}
},
        {{ /* 08 : 0040000000000000 */
0, 3, 4, 9, 10, 13, 14, 17, 18, 20, 25, 29, 31, -1, -1, -1
}, {
0x20000000, 0x00020000, 0x08000000, 0x00100000, 0x01000000,
0x20000000, 0x02000000, 0x10000000, 0x00020000, 0x04000000,
0x00040000, 0x00200000, 0x08000000, 0xFFFFFFFF, 0xFFFFFFFF
}
},
        {{ /* 09 : 0020000000000000 */
3, 4, 6, 9, 11, 12, 16, 19, 20, 23, 25, 26, 28, 30, -1, -1
}, {
0x02000000, 0x00100000, 0x10000000, 0x08000000, 0x00020000,
0x08000000, 0x00010000, 0x04000000, 0x00040000, 0x00010000,
0x10000000, 0x00020000, 0x04000000, 0x00200000, 0xFFFFFFFF
}
},
        {{ /* 10 : 0010000000000000 */
2, 5, 6, 8, 11, 14, 17, 21, 22, 25, 27, 28, 30, -1, -1, -1
}, {
0x00000001, 0x00000800, 0x00000010, 0x00000200, 0x00000400,
0x00002000, 0x00001000, 0x00000004, 0x00000002, 0x00000001,
0x00002000, 0x00000004, 0x00000100, 0xFFFFFFFF, 0xFFFFFFFF
}
},
        {{ /* 11 : 0008000000000000 */
1, 2, 4, 7, 10, 12, 14, 17, 18, 21, 23, 24, 28, 31, -1, -1
}, {
0x00000008, 0x00000010, 0x00000200, 0x00000400, 0x00002000,
0x00000008, 0x00000400, 0x00000004, 0x00000002, 0x00000001,
0x00002000, 0x00000004, 0x00001000, 0x00000800, 0xFFFFFFFF
}
},
        {{ /* 12 : 0004000000000000 */
1, 2, 4, 6, 11, 12, 15, 16, 20, 23, 25, 27, 28, -1, -1, -1
}, {
0x00000010, 0x00002000, 0x00000008, 0x00000400, 0x00000200,
0x00000800, 0x00000002, 0x00000004, 0x00001000, 0x00000008,
0x00000100, 0x00000020, 0x00000020, 0xFFFFFFFF, 0xFFFFFFFF
}
},
        {{ /* 13 : 0002000000000000 */
0, 3, 4, 7, 8, 10, 13, 14, 17, 19, 20, 23, 25, 29, -1, -1
}, {
0x00000002, 0x00000200, 0x00000800, 0x00000002, 0x00000100,
0x00000001, 0x00000800, 0x00000010, 0x00000100, 0x00000020,
0x00000020, 0x00000010, 0x00001000, 0x00000004, 0xFFFFFFFF
}
},
        {{ /* 14 : 0000800000000000 */
0, 2, 5, 7, 8, 10, 15, 16, 18, 21, 23, 24, 29, 31, -1, -1
}, {
0x01000000, 0x00040000, 0x00010000, 0x10000000, 0x00020000,
0x04000000, 0x00040000, 0x00100000, 0x10000000, 0x08000000,
0x00020000, 0x08000000, 0x00100000, 0x04000000, 0xFFFFFFFF
}
},
        {{ /* 15 : 0000400000000000 */
1, 2, 5, 6, 9, 10, 13, 15, 17, 20, 23, 24, 26, 29, 30, -1
}, {
0x00020000, 0x00080000, 0x01000000, 0x00010000, 0x04000000,
0x00040000, 0x00010000, 0x10000000, 0x00080000, 0x00200000,
0x02000000, 0x00100000, 0x10000000, 0x08000000, 0x20000000
}
},
        {{ /* 16 : 0000200000000000 */
1, 3, 7, 8, 10, 13, 14, 17, 18, 21, 22, 25, 28, -1, -1, -1
}, {
0x02000000, 0x00040000, 0x00200000, 0x20000000, 0x00080000,
0x01000000, 0x00010000, 0x00100000, 0x01000000, 0x20000000,
0x02000000, 0x00080000, 0x00200000, 0xFFFFFFFF, 0xFFFFFFFF
}
},
        {{ /* 17 : 0000100000000000 */
0, 2, 5, 7, 9, 10, 13, 15, 16, 18, 23, 24, 27, 28, -1, -1
}, {
0x00000001, 0x00001000, 0x00000008, 0x00000100, 0x00000020,
0x00000020, 0x00000010, 0x00001000, 0x00000008, 0x00000400,
0x00000200, 0x00000800, 0x00000002, 0x00000100, 0xFFFFFFFF
}
},
        {{ /* 18 : 0000080000000000 */
0, 3, 5, 6, 9, 11, 15, 19, 20, 23, 24, 26, 29, 31, -1, -1
}, {
0x00000010, 0x00000100, 0x00000020, 0x00000020, 0x00000010,
0x00001000, 0x00000004, 0x00000200, 0x00000800, 0x00000002,
0x00000100, 0x00000001, 0x00000800, 0x00000008, 0xFFFFFFFF
}
},
        {{ /* 19 : 0000040000000000 */
0, 3, 7, 8, 11, 13, 14, 16, 18, 21, 22, 24, 27, 31, -1, -1
}, {
0x00002000, 0x00001000, 0x00000004, 0x00000002, 0x00000001,
0x00002000, 0x00000004, 0x00000100, 0x00000001, 0x00000800,
0x00000010, 0x00000200, 0x00000400, 0x00000010, 0xFFFFFFFF
}
},
        {{ /* 20 : 0000020000000000 */
1, 3, 5, 6, 10, 13, 15, 16, 19, 22, 24, 26, 30, -1, -1, -1
}, {
0x00000200, 0x00000001, 0x00002000, 0x00000004, 0x00001000,
0x00000008, 0x00000100, 0x00000200, 0x00000400, 0x00002000,
0x00000008, 0x00000400, 0x00000002, 0xFFFFFFFF, 0xFFFFFFFF
}
},
        {{ /* 21 : 0000008000000000 */
0, 3, 4, 7, 10, 13, 14, 19, 20, 22, 25, 26, 29, 30, -1, -1
}, {
0x00040000, 0x20000000, 0x02000000, 0x00080000, 0x00200000,
0x02000000, 0x00100000, 0x00200000, 0x20000000, 0x00080000,
0x01000000, 0x00010000, 0x04000000, 0x01000000, 0xFFFFFFFF
}
},
        {{ /* 22 : 0000004000000000 */
0, 2, 7, 8, 11, 12, 15, 16, 18, 23, 27, 28, 31, -1, -1, -1
}, {
0x00080000, 0x08000000, 0x00100000, 0x01000000, 0x20000000,
0x02000000, 0x00080000, 0x00020000, 0x04000000, 0x00040000,
0x00200000, 0x20000000, 0x00020000, 0xFFFFFFFF, 0xFFFFFFFF
}
},
        {{ /* 23 : 0000002000000000 */
1, 2, 4, 7, 9, 10, 15, 17, 18, 21, 23, 24, 26, 31, -1, -1
}, {
0x00040000, 0x00100000, 0x10000000, 0x08000000, 0x00020000,
0x08000000, 0x00100000, 0x04000000, 0x00040000, 0x00010000,
0x10000000, 0x00020000, 0x04000000, 0x02000000, 0xFFFFFFFF
}
},
        {{ /* 24 : 0000001000000000 */
0, 3, 4, 6, 9, 12, 14, 19, 20, 23, 25, 26, 30, -1, -1, -1
}, {
0x00001000, 0x00000800, 0x00000010, 0x00000200, 0x00000400,
0x00002000, 0x00000008, 0x00000004, 0x00000002, 0x00000001,
0x00002000, 0x00000004, 0x00000001, 0xFFFFFFFF, 0xFFFFFFFF
}
},
        {{ /* 25 : 0000000800000000 */
1, 2, 5, 8, 10, 12, 16, 19, 21, 22, 26, 29, 30, -1, -1, -1
}, {
0x00000100, 0x00000200, 0x00000400, 0x00002000, 0x00000008,
0x00000400, 0x00000002, 0x00000001, 0x00002000, 0x00000004,
0x00001000, 0x00000008, 0x00000010, 0xFFFFFFFF, 0xFFFFFFFF
}
},
        {{ /* 26 : 0000000400000000 */
1, 2, 4, 9, 10, 13, 14, 18, 21, 23, 25, 26, 29, 30, -1, -1
}, {
0x00001000, 0x00000008, 0x00000400, 0x00000200, 0x00000800,
0x00000002, 0x00000100, 0x00001000, 0x00000008, 0x00000100,
0x00000020, 0x00000020, 0x00000010, 0x00002000, 0xFFFFFFFF
}
},
        {{ /* 27 : 0000000200000000 */
1, 2, 5, 6, 8, 11, 12, 14, 17, 18, 21, 23, 27, 28, 31, -1
}, {
0x00000001, 0x00000800, 0x00000002, 0x00000100, 0x00000001,
0x00000800, 0x00000010, 0x00000200, 0x00000020, 0x00000020,
0x00000010, 0x00001000, 0x00000004, 0x00000002, 0x00000200
}
},
        {{ /* 28 : 0000000080000000 */
1, 3, 5, 6, 8, 13, 16, 19, 21, 22, 27, 28, 30, -1, -1, -1
}, {
0x20000000, 0x00010000, 0x10000000, 0x00020000, 0x04000000,
0x00040000, 0x10000000, 0x08000000, 0x00020000, 0x08000000,
0x00100000, 0x01000000, 0x00040000, 0xFFFFFFFF, 0xFFFFFFFF
}
},
        {{ /* 29 : 0000000040000000 */
0, 3, 4, 7, 8, 11, 13, 14, 18, 21, 22, 24, 27, 29, 30, -1
}, {
0x08000000, 0x01000000, 0x00010000, 0x04000000, 0x00040000,
0x00010000, 0x10000000, 0x00020000, 0x00200000, 0x02000000,
0x00100000, 0x10000000, 0x08000000, 0x00020000, 0x00080000
}
},
        {{ /* 30 : 0000000020000000 */
0, 5, 6, 8, 11, 12, 15, 16, 19, 20, 23, 26, 29, 31, -1, -1
}, {
0x00100000, 0x00200000, 0x20000000, 0x00080000, 0x01000000,
0x00010000, 0x04000000, 0x01000000, 0x20000000, 0x02000000,
0x00080000, 0x00200000, 0x02000000, 0x00040000, 0xFFFFFFFF
}
},
        {{ /* 31 : 0000000010000000 */
1, 2, 4, 9, 13, 14, 17, 18, 23, 24, 27, 28, 31, -1, -1, -1
}, {
0x00080000, 0x00020000, 0x04000000, 0x00040000, 0x00200000,
0x20000000, 0x00020000, 0x08000000, 0x00100000, 0x01000000,
0x20000000, 0x02000000, 0x10000000, 0xFFFFFFFF, 0xFFFFFFFF
}
},
        {{ /* 32 : 0000000008000000 */
0, 3, 4, 7, 9, 13, 14, 17, 18, 21, 22, 24, 27, 28, 31, -1
}, {
0x00000200, 0x00000020, 0x00000020, 0x00000010, 0x00001000,
0x00000004, 0x00000002, 0x00000200, 0x00000800, 0x00000002,
0x00000100, 0x00000001, 0x00000800, 0x00000010, 0x00000100
}
},
        {{ /* 33 : 0000000004000000 */
0, 5, 6, 9, 11, 12, 16, 19, 20, 22, 25, 28, 31, -1, -1, -1
}, {
0x00000008, 0x00000004, 0x00000002, 0x00000001, 0x00002000,
0x00000004, 0x00000001, 0x00000800, 0x00000010, 0x00000200,
0x00000400, 0x00002000, 0x00001000, 0xFFFFFFFF, 0xFFFFFFFF
}
},
        {{ /* 34 : 0000000002000000 */
0, 3, 4, 8, 11, 13, 15, 17, 20, 22, 24, 29, 31, -1, -1, -1
}, {
0x00000800, 0x00002000, 0x00000004, 0x00001000, 0x00000008,
0x00000100, 0x00000020, 0x00000400, 0x00002000, 0x00000008,
0x00000400, 0x00000200, 0x00000001, 0xFFFFFFFF, 0xFFFFFFFF
}
},
        {{ /* 35 : 0000000000800000 */
1, 2, 5, 8, 11, 12, 14, 17, 18, 20, 23, 24, 27, 28, 31, -1
}, {
0x00010000, 0x02000000, 0x00080000, 0x00200000, 0x02000000,
0x00100000, 0x10000000, 0x00200000, 0x20000000, 0x00080000,
0x01000000, 0x00010000, 0x04000000, 0x00040000, 0x20000000
}
},
        {{ /* 36 : 0000000000400000 */
1, 5, 6, 9, 10, 13, 16, 21, 25, 26, 28, 30, -1, -1, -1, -1
}, {
0x01000000, 0x00100000, 0x01000000, 0x20000000, 0x02000000,
0x00080000, 0x04000000, 0x00040000, 0x00200000, 0x20000000,
0x00080000, 0x08000000, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF
}
},
        {{ /* 37 : 0000000000200000 */
2, 5, 7, 8, 13, 14, 16, 19, 21, 22, 24, 29, 30, -1, -1, -1
}, {
0x10000000, 0x08000000, 0x00020000, 0x08000000, 0x00100000,
0x01000000, 0x00040000, 0x00010000, 0x10000000, 0x00020000,
0x04000000, 0x00040000, 0x00100000, 0xFFFFFFFF, 0xFFFFFFFF
}
},
        {{ /* 38 : 0000000000100000 */
0, 4, 7, 8, 10, 13, 15, 16, 19, 20, 23, 24, 27, 29, 31, -1
}, {
0x00020000, 0x00200000, 0x02000000, 0x00100000, 0x10000000,
0x08000000, 0x00020000, 0x00080000, 0x01000000, 0x00010000,
0x04000000, 0x00040000, 0x00010000, 0x10000000, 0x00080000
}
},
        {{ /* 39 : 0000000000080000 */
1, 3, 6, 8, 10, 15, 17, 19, 20, 24, 27, 29, 30, -1, -1, -1
}, {
0x00000020, 0x00000400, 0x00002000, 0x00000008, 0x00000400,
0x00000200, 0x00000001, 0x00002000, 0x00000004, 0x00001000,
0x00000008, 0x00000100, 0x00000200, 0xFFFFFFFF, 0xFFFFFFFF
}
},
        {{ /* 40 : 0000000000040000 */
2, 7, 8, 11, 12, 14, 16, 19, 21, 23, 24, 27, 29, 30, -1, -1
}, {
0x00000400, 0x00000200, 0x00000800, 0x00000002, 0x00000100,
0x00000001, 0x00001000, 0x00000008, 0x00000100, 0x00000020,
0x00000020, 0x00000010, 0x00001000, 0x00000008, 0xFFFFFFFF
}
},
        {{ /* 41 : 0000000000020000 */
1, 3, 4, 6, 9, 10, 12, 15, 16, 19, 21, 25, 26, 29, 30, -1
}, {
0x00002000, 0x00000002, 0x00000100, 0x00000001, 0x00000800,
0x00000010, 0x00000200, 0x00000400, 0x00000020, 0x00000010,
0x00001000, 0x00000004, 0x00000002, 0x00000001, 0x00000800
}
},
        {{ /* 42 : 0000000000008000 */
0, 3, 4, 6, 11, 15, 17, 19, 20, 25, 26, 29, 31, -1, -1, -1
}, {
0x02000000, 0x10000000, 0x00020000, 0x04000000, 0x00040000,
0x00200000, 0x08000000, 0x00020000, 0x08000000, 0x00100000,
0x01000000, 0x20000000, 0x00010000, 0xFFFFFFFF, 0xFFFFFFFF
}
},
        {{ /* 43 : 0000000000004000 */
2, 5, 6, 9, 11, 12, 14, 16, 19, 20, 22, 25, 27, 28, 31, -1
}, {
0x00010000, 0x04000000, 0x00040000, 0x00010000, 0x10000000,
0x00020000, 0x04000000, 0x00200000, 0x02000000, 0x00100000,
0x10000000, 0x08000000, 0x00020000, 0x08000000, 0x01000000
}
},
        {{ /* 44 : 0000000000002000 */
0, 3, 4, 6, 9, 10, 13, 14, 17, 18, 21, 24, 27, 28, -1, -1
}, {
0x10000000, 0x00200000, 0x20000000, 0x00080000, 0x01000000,
0x00010000, 0x04000000, 0x00040000, 0x20000000, 0x02000000,
0x00080000, 0x00200000, 0x02000000, 0x00100000, 0xFFFFFFFF
}
},
        {{ /* 45 : 0000000000001000 */
2, 7, 11, 12, 14, 16, 21, 22, 25, 26, 29, 30, -1, -1, -1, -1
}, {
0x04000000, 0x00040000, 0x00200000, 0x20000000, 0x00080000,
0x08000000, 0x00100000, 0x01000000, 0x20000000, 0x02000000,
0x00080000, 0x00020000, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF
}
},
        {{ /* 46 : 0000000000000800 */
1, 2, 5, 7, 11, 12, 15, 16, 19, 20, 22, 25, 26, 28, 31, -1
}, {
0x00000400, 0x00000020, 0x00000010, 0x00001000, 0x00000004,
0x00000002, 0x00000001, 0x00000800, 0x00000002, 0x00000100,
0x00000001, 0x00000800, 0x00000010, 0x00000200, 0x00000020
}
},
        {{ /* 47 : 0000000000000400 */
0, 3, 4, 7, 9, 10, 14, 17, 18, 20, 23, 26, 28, -1, -1, -1
}, {
0x00000400, 0x00000004, 0x00000002, 0x00000001, 0x00002000,
0x00000004, 0x00001000, 0x00000800, 0x00000010, 0x00000200,
0x00000400, 0x00002000, 0x00000008, 0xFFFFFFFF, 0xFFFFFFFF
}
},
        {{ /* 48 : 0000000000000200 */
1, 2, 6, 9, 11, 13, 14, 18, 20, 22, 27, 28, 31, -1, -1, -1
}, {
0x00000002, 0x00000004, 0x00001000, 0x00000008, 0x00000100,
0x00000020, 0x00000020, 0x00002000, 0x00000008, 0x00000400,
0x00000200, 0x00000800, 0x00002000, 0xFFFFFFFF, 0xFFFFFFFF
}
},
        {{ /* 49 : 0000000000000080 */
1, 3, 6, 9, 10, 12, 15, 16, 18, 21, 22, 25, 26, 29, 30, -1
}, {
0x10000000, 0x00080000, 0x00200000, 0x02000000, 0x00100000,
0x10000000, 0x08000000, 0x20000000, 0x00080000, 0x01000000,
0x00010000, 0x04000000, 0x00040000, 0x00010000, 0x02000000
}
},
        {{ /* 50 : 0000000000000040 */
0, 3, 4, 7, 8, 11, 14, 19, 23, 24, 26, 29, -1, -1, -1, -1
}, {
0x00010000, 0x00100000, 0x01000000, 0x20000000, 0x02000000,
0x00080000, 0x00200000, 0x00040000, 0x00200000, 0x20000000,
0x00080000, 0x01000000, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF
}
},
        {{ /* 51 : 0000000000000020 */
1, 3, 5, 6, 11, 12, 15, 17, 19, 20, 22, 27, 30, -1, -1, -1
}, {
0x00200000, 0x08000000, 0x00020000, 0x08000000, 0x00100000,
0x01000000, 0x20000000, 0x00010000, 0x10000000, 0x00020000,
0x04000000, 0x00040000, 0x10000000, 0xFFFFFFFF, 0xFFFFFFFF
}
},
        {{ /* 52 : 0000000000000010 */
0, 2, 5, 6, 8, 11, 13, 14, 17, 18, 21, 22, 25, 27, 28, -1
}, {
0x04000000, 0x00200000, 0x02000000, 0x00100000, 0x10000000,
0x08000000, 0x00020000, 0x08000000, 0x01000000, 0x00010000,
0x04000000, 0x00040000, 0x00010000, 0x10000000, 0x00020000
}
},
        {{ /* 53 : 0000000000000008 */
0, 4, 6, 8, 13, 14, 17, 18, 22, 25, 27, 29, 31, -1, -1, -1
}, {
0x00000020, 0x00002000, 0x00000008, 0x00000400, 0x00000200,
0x00000800, 0x00002000, 0x00000004, 0x00001000, 0x00000008,
0x00000100, 0x00000020, 0x00000400, 0xFFFFFFFF, 0xFFFFFFFF
}
},
        {{ /* 54 : 0000000000000004 */
1, 5, 6, 9, 10, 12, 15, 17, 19, 21, 22, 25, 27, 30, -1, -1
}, {
0x00000004, 0x00000200, 0x00000800, 0x00000002, 0x00000100,
0x00000001, 0x00000800, 0x00000008, 0x00000100, 0x00000020,
0x00000020, 0x00000010, 0x00001000, 0x00000400, 0xFFFFFFFF
}
},
        {{ /* 55 : 0000000000000002 */
0, 2, 4, 7, 8, 10, 13, 17, 19, 23, 24, 27, 29, 31, -1, -1
}, {
0x00000004, 0x00000100, 0x00000001, 0x00000800, 0x00000010,
0x00000200, 0x00000400, 0x00000010, 0x00001000, 0x00000004,
0x00000002, 0x00000001, 0x00002000, 0x00000002, 0xFFFFFFFF
}
}
    };

    public static void single(final byte[] hexkey, int off1, //NOPMD long and complex
            final boolean mode, final int[] keybuf, final int offset) {
        int bbidx = 0;
        int[] bip;
        int[] kip;
        int test;
        int idx;
        int i, j; //NOPMD names

        int ePtr = offset + LONGS;
        int dPtr = offset;
        while (dPtr < ePtr) {
            keybuf[dPtr++] = 0;
        }

        while (bbidx < PRECOMP.length) {
            test = (hexkey[off1++]) & 0xff;
            if ((test & 0x80) != 0) {
                bip = PRECOMP[bbidx][1];
                kip = PRECOMP[bbidx][0];
                i = j = 0;
                while ((idx = kip[i++]) >= 0) { //NOPMD
                    keybuf[idx + offset] |= bip[j++];
                }
            }
            bbidx++;
            if ((test & 0x40) != 0) {
                bip = PRECOMP[bbidx][1];
                kip = PRECOMP[bbidx][0];
                i = j = 0;
                while ((idx = kip[i++]) >= 0) { //NOPMD
                    keybuf[idx + offset] |= bip[j++];
                }
            }
            bbidx++;
            if ((test & 0x20) != 0) {
                bip = PRECOMP[bbidx][1];
                kip = PRECOMP[bbidx][0];
                i = j = 0;
                while ((idx = kip[i++]) >= 0) { //NOPMD
                    keybuf[idx + offset] |= bip[j++];
                }
            }
            bbidx++;
            if ((test & 0x10) != 0) {
                bip = PRECOMP[bbidx][1];
                kip = PRECOMP[bbidx][0];
                i = j = 0;
                while ((idx = kip[i++]) >= 0) { //NOPMD
                    keybuf[idx + offset] |= bip[j++];
                }
            }
            bbidx++;
            if ((test & 0x08) != 0) {
                bip = PRECOMP[bbidx][1];
                kip = PRECOMP[bbidx][0];
                i = j = 0;
                while ((idx = kip[i++]) >= 0) { //NOPMD
                    keybuf[idx + offset] |= bip[j++];
                }
            }
            bbidx++;
            if ((test & 0x04) != 0) {
                bip = PRECOMP[bbidx][1];
                kip = PRECOMP[bbidx][0];
                i = j = 0;
                while ((idx = kip[i++]) >= 0) { //NOPMD
                    keybuf[idx + offset] |= bip[j++];
                }
            }
            bbidx++;
            if ((test & 0x02) != 0) {
                bip = PRECOMP[bbidx][1];
                kip = PRECOMP[bbidx][0];
                i = j = 0;
                while ((idx = kip[i++]) >= 0) { //NOPMD
                    keybuf[idx + offset] |= bip[j++];
                }
            }
            bbidx++;
        // only 7 bits out of each byte dor DES
        }

        /* swap 0.1 with 30.31, 2.3 with 28.29, etc */
        if (mode == DE1) {
            int ccp = offset;//keybuf;
            dPtr = offset + 30;//&keybuf[30];
            ePtr = offset + 16;//&keybuf[16];
            while (ccp < ePtr) {
                test = keybuf[dPtr];
                keybuf[dPtr] = keybuf[ccp];
                keybuf[ccp++] = test;
                test = keybuf[dPtr + 1];
                keybuf[dPtr + 1] = keybuf[ccp];
                keybuf[ccp++] = test;
                dPtr -= 2;
            }
        }
    }

    public static void dbl(final byte[] hexkey, final int off1, final boolean mode,
            final int[] keyout, final int offset) {
        final boolean revmod = !mode;
        single(hexkey, off1 + KEYSIZE, revmod, keyout, offset + LONGS);
        single(hexkey, off1, mode, keyout, offset);
        for (int i = 0; i < LONGS; ++i) {
            keyout[offset + 2 * LONGS + i] = keyout[offset + i];
        }
    }

    public static void triple(final byte[] hexkey, final int off1,
            final boolean mode, final int[] keyout, final int offset) {
        int first = 0;
        int third = 2 * KEYSIZE;
        boolean revmod;

        if (mode == EN0) {
            revmod = DE1;
        } else {
            revmod = EN0;
            first = 2 * KEYSIZE;
            third = 0;
        }
        single(hexkey, off1 + first, mode, keyout, offset);
        single(hexkey, off1 + KEYSIZE, revmod, keyout, offset + LONGS);
        single(hexkey, off1 + third, mode, keyout, offset + 2 * LONGS);
    }

    private DESkeys() {
    }
} 