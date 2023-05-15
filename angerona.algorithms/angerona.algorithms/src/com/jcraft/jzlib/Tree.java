/* -*-mode:java; c-basic-offset:2; -*- */
/*
Copyright (c) 2000,2001,2002,2003 ymnk, JCraft,Inc. All rights reserved.
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright 
notice, this list of conditions and the following disclaimer in 
the documentation and/or other materials provided with the distribution.
3. The names of the authors may not be used to endorse or promote products
derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JCRAFT,
INC. OR ANY CONTRIBUTORS TO THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/*
 * This program is based on zlib-1.1.3, so all credit should go authors
 * Jean-loup Gailly(jloup@gzip.org) and Mark Adler(madler@alumni.caltech.edu)
 * and contributors of zlib.
 */
package com.jcraft.jzlib;

final class Tree { // NOPMD complexity

    static final private int MAX_BITS = 15;
    // static final private int BL_CODES = 19;
    // static final private int D_CODES = 30;
    static final private int LITERALS = 256;
    static final private int LENGTH_CODES = 29;
    static final private int L_CODES = (LITERALS + 1 + LENGTH_CODES);
    static final private int HEAP_SIZE = (2 * L_CODES + 1);

    // Bit length codes must not exceed MAX_BL_BITS bits
    public static final int MAX_BL_BITS = 7;

    // end of block literal code
    public static final int END_BLOCK = 256;

    // repeat previous bit length 3-6 times (2 bits of repeat count)
    public static final int REP_3_6 = 16;

    // repeat a zero length 3-10 times  (3 bits of repeat count)
    public static final int REPZ_3_10 = 17;

    // repeat a zero length 11-138 times  (7 bits of repeat count)
    public static final int REPZ_11_138 = 18;

    // extra bits for each length code
    public static final int[] EXTRA_LBITS = {
        0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 0
    };

    // extra bits for each distance code
    public static final int[] EXTRA_DBITS = {
        0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13
    };

    // extra bits for each bit length code
    public static final int[] EXTRA_BLBITS = {
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 3, 7
    };
    public static final byte[] BL_ORDER = {
        16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15
    };
    // The lengths of the bit length codes are sent in order of decreasing
    // probability, to avoid transmitting the lengths for unused bit
    // length codes.
    public static final int BUF_SIZE = 8 * 2;

    // see definition of array dist_code below
    public static final int DIST_CODE_LEN = 512;
    public static final byte[] DIST_CODE = {
        0, 1, 2, 3, 4, 4, 5, 5, 6, 6, 6, 6, 7, 7, 7, 7, 8, 8, 8, 8,
        8, 8, 8, 8, 9, 9, 9, 9, 9, 9, 9, 9, 10, 10, 10, 10, 10, 10, 10, 10,
        10, 10, 10, 10, 10, 10, 10, 10, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11,
        11, 11, 11, 11, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12,
        12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 13, 13, 13, 13,
        13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13,
        13, 13, 13, 13, 13, 13, 13, 13, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14,
        14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14,
        14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14,
        14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 15, 15, 15, 15, 15, 15, 15, 15,
        15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
        15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
        15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 0, 0, 16, 17,
        18, 18, 19, 19, 20, 20, 20, 20, 21, 21, 21, 21, 22, 22, 22, 22, 22, 22, 22, 22,
        23, 23, 23, 23, 23, 23, 23, 23, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
        24, 24, 24, 24, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25,
        26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
        26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 27, 27, 27, 27, 27, 27, 27, 27,
        27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27,
        27, 27, 27, 27, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28,
        28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28,
        28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28,
        28, 28, 28, 28, 28, 28, 28, 28, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29,
        29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29,
        29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29,
        29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29
    };
    public static final byte[] LENGTH_CODE = {
        0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 12, 12,
        13, 13, 13, 13, 14, 14, 14, 14, 15, 15, 15, 15, 16, 16, 16, 16, 16, 16, 16, 16,
        17, 17, 17, 17, 17, 17, 17, 17, 18, 18, 18, 18, 18, 18, 18, 18, 19, 19, 19, 19,
        19, 19, 19, 19, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
        21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 22, 22, 22, 22,
        22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 23, 23, 23, 23, 23, 23, 23, 23,
        23, 23, 23, 23, 23, 23, 23, 23, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
        24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
        25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25,
        25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 26, 26, 26, 26, 26, 26, 26, 26,
        26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
        26, 26, 26, 26, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27,
        27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 28
    };
    public static final int[] BASE_LENGTH = {
        0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 12, 14, 16, 20, 24, 28, 32, 40, 48, 56,
        64, 80, 96, 112, 128, 160, 192, 224, 0
    };
    public static final int[] BASE_DIST = {
        0, 1, 2, 3, 4, 6, 8, 12, 16, 24,
        32, 48, 64, 96, 128, 192, 256, 384, 512, 768,
        1024, 1536, 2048, 3072, 4096, 6144, 8192, 12288, 16384, 24576
    };

    // Mapping from a distance to a distance code. dist is the distance - 1 and
    // must not have side effects. DIST_CODE[256] and DIST_CODE[257] are never
    // used.
    public static int dCode(final int dist) {
        return ((dist) < 256 ? DIST_CODE[dist] : DIST_CODE[256 + ((dist) >>> 7)]);
    }
    public int[] dynTree; // the dynamic tree
    public int maxCode;      // largest code with non zero frequency
    public StaticTree statDesc;  // the corresponding static tree

    // Compute the optimal bit lengths for a tree and update the total bit length
    // for the current block.
    // IN assertion: the fields freq and dad are set, heap[heapMax] and
    //    above are the tree nodes sorted by increasing frequency.
    // OUT assertions: the field len is set to the optimal bit length, the
    //     array blCount contains the frequencies for each bit length.
    //     The length optLen is updated; staticLen is also updated if stree is
    //     not null.
    public void genBitlen(final Deflate state) { // NOPMD complexity
        int[] tree = dynTree; 
        final int[] stree = statDesc.staticTreeData;
        final int[] extra = statDesc.extraBits;
        final int base = statDesc.extraBase;
        final int max_length = statDesc.maxLength;
        int heapIndex;              // heap index
        int indexN, indexM;           // iterate over the tree elements
        int bits;           // bit length
        int xbits;          // extra bits
        int frequency;            // frequency
        int overflow = 0;  // number of elements with bit length too large

        for (bits = 0; bits <= MAX_BITS; bits++) {
            state.blCount[bits] = 0;
        }

        // In a first pass, compute the optimal bit lengths (which may
        // overflow in the case of the bit length tree).
        tree[state.heap[state.heapMax] * 2 + 1] = 0; // root of the heap

        for (heapIndex = state.heapMax + 1; heapIndex < HEAP_SIZE; heapIndex++) {
            indexN = state.heap[heapIndex];
            bits = tree[tree[indexN * 2 + 1] * 2 + 1] + 1;
            if (bits > max_length) {
                bits = max_length;
                overflow++;  //NO-PMD DD dataflow lolwut?
            }
            tree[indexN * 2 + 1] = bits;
            // We overwrite tree[n*2+1] which is no longer needed

            if (indexN > maxCode) {
                continue;
            }  // not a leaf node

            state.blCount[bits]++;
            xbits = 0;  //NO-PMD DD dataflow lolwut?
            if (indexN >= base) {
                xbits = extra[indexN - base];
            }
            frequency = tree[indexN * 2];
            state.optLen += frequency * (bits + xbits);
            if (stree != null) {
                state.staticLen += frequency * (stree[indexN * 2 + 1] + xbits);
            }
        }
        if (overflow == 0) {
            return;
        }

        // This happens for example on obj2 and pic of the Calgary corpus
        // Find the first bit length which could increase:
        do {
            bits = max_length - 1;
            while (state.blCount[bits] == 0) {
                bits--;
            }
            state.blCount[bits]--;      // move one leaf down the tree
            state.blCount[bits + 1] += 2;   // move one overflow item as its brother
            state.blCount[max_length]--;
            // The brother of the overflow item also moves one step up,
            // but this does not affect blCount[max_length]
            overflow -= 2;
        } while (overflow > 0);

        for (bits = max_length; bits != 0; bits--) {
            indexN = state.blCount[bits];
            while (indexN != 0) {
                indexM = state.heap[--heapIndex];
                if (indexM > maxCode) {
                    continue;
                }
                if (tree[indexM * 2 + 1] != bits) {
                    state.optLen += ((long) bits - (long) tree[indexM * 2 + 1]) * (long) tree[indexM * 2];
                    tree[indexM * 2 + 1] = bits;  
                }
                indexN--;
            }
        }
    }

    // Construct one Huffman tree and assigns the code bit strings and lengths.
    // Update the total bit length for the current block.
    // IN assertion: the field freq is set for all tree elements.
    // OUT assertions: the fields len and code are set to the optimal bit length
    //     and corresponding code. The length optLen is updated; staticLen is
    //     also updated if stree is not null. The field maxCode is set.
    public void buildTree(final Deflate state) {
        int[] tree = dynTree;  //NO-PMD DD dataflow lolwut?
        final int[] stree = statDesc.staticTreeData;
        final int elems = statDesc.elems;
        int indexN, indexM;          // iterate over heap elements
        int max_code = -1;  //NO-PMD DD dataflow lolwut?  // largest code with non zero frequency
        int node;          // new node being created

        // Construct the initial heap, with least frequent element in
        // heap[1]. The sons of heap[n] are heap[2*n] and heap[2*n+1].
        // heap[0] is not used.
        state.heapLen = 0;
        state.heapMax = HEAP_SIZE;

        for (indexN = 0; indexN < elems; indexN++) {
            if (tree[indexN * 2] != 0) {  // NOPMD 'C'-ism
                state.heap[++state.heapLen] = max_code = indexN; 
                state.depth[indexN] = 0;
            } else {
                tree[indexN * 2 + 1] = 0;
            }
        }

        // The pkzip format requires that at least one distance code exists,
        // and that at least one bit should be sent even if there is only one
        // possible code. So to avoid special checks later on we force at least
        // two codes of non zero frequency.
        while (state.heapLen < 2) {
            node = state.heap[++state.heapLen] = (max_code < 2 ? ++max_code : 0);
            tree[node * 2] = 1;  //NO-PMD DD dataflow lolwut?
            state.depth[node] = 0;
            state.optLen--;
            if (stree != null) {
                state.staticLen -= stree[node * 2 + 1];
            }
        // node is 0 or 1 so it does not have extra bits
        }
        this.maxCode = max_code;

        // The elements heap[heapLen/2+1 .. heapLen] are leaves of the tree,
        // establish sub-heaps of increasing lengths:

        for (indexN = state.heapLen / 2; indexN >= 1; indexN--) {
            state.pqdownheap(tree, indexN);
        }

        // Construct the Huffman tree by repeatedly combining the least two
        // frequent nodes.

        node = elems;                 // next internal node of the tree
        do {
            // n = node of least frequency
            indexN = state.heap[1];
            state.heap[1] = state.heap[state.heapLen--];
            state.pqdownheap(tree, 1);
            indexM = state.heap[1];                // m = node of next least frequency

            state.heap[--state.heapMax] = indexN; // keep the nodes sorted by frequency
            state.heap[--state.heapMax] = indexM;

            // Create a new node father of n and m
            tree[node * 2] =  (tree[indexN * 2] + tree[indexM * 2]);
            state.depth[node] = (byte) (Math.max(state.depth[indexN], state.depth[indexM]) + 1);
            tree[indexN * 2 + 1] = tree[indexM * 2 + 1] =  node;  //NOPMD DD dataflow lolwut?

            // and insert the new node in the heap
            state.heap[1] = node++;
            state.pqdownheap(tree, 1);
        } while (state.heapLen >= 2);

        state.heap[--state.heapMax] = state.heap[1];

        // At this point, the fields freq and dad are set. We can now
        // generate the bit lengths.

        genBitlen(state);

        // The field len is now set, we can generate the bit codes
        genCodes(tree, max_code, state.blCount);
    }

    // Generate the codes for a given tree and bit counts (which need not be
    // optimal).
    // IN assertion: the array blCount contains the bit length statistics for
    // the given tree and the field len is set for all tree elements.
    // OUT assertion: the field code is set for all tree elements of non
    //     zero code length.
    public static void genCodes(final int[] tree, // the tree to decorate
            final int maxCode, // largest code with non zero frequency
            final int[] blCount // number of codes at each bit length
             // number of codes at each bit length
             // number of codes at each bit length
             // number of codes at each bit length
            ) {
        int[] next_code = new int[MAX_BITS + 1];  // next code value for each bit length
        int code = 0;            // running code value  
        int bits;                  // bit index
        int codeIndex;                     // code index

        // The distribution counts are first used to generate the code values
        // without bit reversal.
        for (bits = 1; bits <= MAX_BITS; bits++) {
            next_code[bits] = code =  ((code + blCount[bits - 1]) << 1);  
        }

        // Check that the bit counts in blCount are consistent. The last code
        // must be all ones.
        //Assert (code + blCount[MAX_BITS]-1 == (1<<MAX_BITS)-1,
        //        "inconsistent bit counts");
        //Tracev((stderr,"\ngen_codes: maxCode %d ", maxCode));

        for (codeIndex = 0; codeIndex <= maxCode; codeIndex++) {
            final int len = tree[codeIndex * 2 + 1];
            if (len == 0) {
                continue;
            }
            // Now reverse the bits
            tree[codeIndex * 2] = (biReverse(next_code[len]++, len));
        }
    }

    // Reverse the first len bits of a code, using straightforward code (a faster
    // method would use a table)
    // IN assertion: 1 <= len <= 15
    public static int biReverse(int code, // the value to invert
            int len // its bit length 
            ) {
        int res = 0;
        do {
            res |= code & 1;
            code >>>= 1;
            res <<= 1;
        } while (--len > 0);
        return res >>> 1;
    }
}

