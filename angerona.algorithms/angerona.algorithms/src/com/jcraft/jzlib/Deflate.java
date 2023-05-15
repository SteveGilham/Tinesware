/* -*-mode:java; value-basic-offset:2; -*- */
/*
Copyright (value) 2000,2001,2002,2003 ymnk, JCraft,Inc. All rights reserved.
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

final class Deflate { // NOPMD long and complex


    static class Config {

        public int goodLength; // reduce lazy search above this match length
        public int maxLazy;    // do not perform lazy search above this match length
        public int niceLength; // quit search above this match length
        public int maxChain;
        public int func;

        Config(final int goodLength, final int maxLazy,
                final int niceLength, final int maxChain, final int func) {
            this.goodLength = goodLength;
            this.maxLazy = maxLazy;
            this.niceLength = niceLength;
            this.maxChain = maxChain;
            this.func = func;
        }
    }
    static final private int STORED = 0;
    static final private int FAST = 1;
    static final private int SLOW = 2;
    static final private Config[] CONFIG_TABLE;

    static {
        CONFIG_TABLE = new Config[10];
        //                         good  lazy  nice  chain
        CONFIG_TABLE[0] = new Config(0, 0, 0, 0, STORED);
        CONFIG_TABLE[1] = new Config(4, 4, 8, 4, FAST);
        CONFIG_TABLE[2] = new Config(4, 5, 16, 8, FAST);
        CONFIG_TABLE[3] = new Config(4, 6, 32, 32, FAST);

        CONFIG_TABLE[4] = new Config(4, 4, 16, 16, SLOW);
        CONFIG_TABLE[5] = new Config(8, 16, 32, 32, SLOW);
        CONFIG_TABLE[6] = new Config(8, 16, 128, 128, SLOW);
        CONFIG_TABLE[7] = new Config(8, 32, 128, 256, SLOW);
        CONFIG_TABLE[8] = new Config(32, 128, 258, 1024, SLOW);
        CONFIG_TABLE[9] = new Config(32, 258, 258, 4096, SLOW);
    }
    static final private String[] Z_ERRMSG = {
        "need dictionary", // Z_NEED_DICT       2
        "stream end", // Z_STREAM_END      1
        "", // Z_OK              0
        "file error", // Z_ERRNO         (-1)
        "stream error", // Z_STREAM_ERROR  (-2)
        "data error", // Z_DATA_ERROR    (-3)
        "insufficient memory", // Z_MEM_ERROR     (-4)
        "buffer error", // Z_BUF_ERROR     (-5)
        "incompatible version",// Z_VERSION_ERROR (-6)
        ""
    };

    // block not completed, need more input or more output
    static final private int NEED_MORE = 0;

    // block flush performed
    static final private int BLOCK_DONE = 1;

    // finish started, need only more output at next deflate
    static final private int FINISH_STARTED = 2;

    // finish done, accept no more input or output
    static final private int FINISH_DONE = 3;

    // preset dictionary flag in zlib header
    static final private int PRESET_DICT = 0x20;
    static final private int Z_FILTERED = 1;
    static final private int Z_HUFFMAN_ONLY = 2;
    static final private int Z_DEFAULT_STRATEGY = 0;
    static final private int Z_NO_FLUSH = 0;
    static final private int Z_PARTIAL_FLUSH = 1;
    //static final private int Z_SYNC_FLUSH = 2;
    static final private int Z_FULL_FLUSH = 3;
    static final private int Z_FINISH = 4;
    static final private int Z_OK = 0;
    static final private int Z_STREAM_END = 1;
    static final private int Z_NEED_DICT = 2;
    //static final private int Z_ERRNO = -1;
    static final private int Z_STREAM_ERROR = -2;
    static final private int Z_DATA_ERROR = -3;
    //static final private int Z_MEM_ERROR = -4;
    static final private int Z_BUF_ERROR = -5;
    //static final private int Z_VERSION_ERROR = -6;
    static final private int INIT_STATE = 42;
    static final private int BUSY_STATE = 113;
    static final private int FINISH_STATE = 666;

    // The deflate compression method
    static final private int Z_DEFLATED = 8;
    static final private int STORED_BLOCK = 0;
    static final private int STATIC_TREES = 1;
    static final private int DYN_TREES = 2;

    // The three kinds of block type
    static final private int Z_BINARY = 0;
    static final private int Z_ASCII = 1;
    static final private int Z_UNKNOWN = 2;
    static final private int BUF_SIZE = 8 * 2;

    // repeat previous bit length 3-6 times (2 bits of repeat count)
    static final private int REP_3_6 = 16;

    // repeat a zero length 3-10 times  (3 bits of repeat count)
    static final private int REPZ_3_10 = 17;

    // repeat a zero length 11-138 times  (7 bits of repeat count)
    static final private int REPZ_11_138 = 18;
    static final private int MIN_MATCH = 3;
    static final private int MAX_MATCH = 258;
    static final private int MIN_LOOKAHEAD = (MAX_MATCH + MIN_MATCH + 1);
    static final private int MAX_BITS = 15;
    static final private int D_CODES = 30;
    static final private int BL_CODES = 19;
    static final private int LENGTH_CODES = 29;
    static final private int LITERALS = 256;
    static final private int L_CODES = (LITERALS + 1 + LENGTH_CODES);
    static final private int HEAP_SIZE = (2 * L_CODES + 1);
    static final private int END_BLOCK = 256;
    
    private ZStream strm;         // pointer back to this zlib stream
    private int status;           // as the name implies
    public byte[] pendingBuf;   // output still pending
    private int pendingBufSize; // size of pending_buf
    public int pendingOut;      // next pending byte to output to the stream
    public int pending;          // nb of bytes in the pending buffer
    public int noheader;         // suppress zlib header and adler32
    private byte dataType;       // UNKNOWN, BINARY or ASCII
    //private byte method;          // STORED (for zip only) or DEFLATED
    private int lastFlush;       // value of flush param for previous deflate call
    private int windowSize;           // LZ77 window size (32K by default)
    private int windowBits;           // log2(w_size)  (8..16)
    private int windowMask;           // w_size - 1
    private byte[] window;
    // Sliding window. Input bytes are read into the second half of the window,
    // and move to the first half later to keep a dictionary of at least wSize
    // bytes. With this organization, matches are limited to a distance of
    // wSize-MAX_MATCH bytes, but this ensures that IO is always
    // performed with a length multiple of the block size. Also, it limits
    // the window size to 64K, which is quite useful on MSDOS.
    // To do: use the user input buffer as sliding window.
    private int actualWindowSize;
    // Actual size of window: 2*wSize, except when the user input buffer
    // is directly used as sliding window.
    private int[] prev; 
    // Link to older string with same hash index. To limit the size of this
    // array to 64K, this link is maintained only for the last 32K strings.
    // An index in this array is thus a window index modulo 32K.
    private int[] head; // Heads of the hash chains or NIL.
    private int insH;          // hash index of string to be inserted
    private int hashSize;      // number of elements in hash table
    private int hashMask;      // hashSize-1

    // Number of bits by which insH must be shifted at each input
    // step. It must be such that after MIN_MATCH steps, the oldest
    // byte no longer takes part in the hash key, that is:
    // hashShift * MIN_MATCH >= hashBits
    private int hashShift;

    // Window position at the beginning of the current output block. Gets
    // negative when the window is moved backwards.
    public int blockStart;
    public int matchLength;           // length of best match
    public int prevMatch;             // previous match
    public int matchAvailable;        // set if previous match exists
    public int strstart;               // start of string to insert
    public int matchStart;            // start of matching string
    public int lookahead;              // number of valid bytes ahead in window

    // Length of the best match at previous step. Matches not greater than this
    // are discarded. This is used in the lazy match evaluation.
    private int prevLength;

    // To speed up deflation, hash chains are never searched beyond this
    // length.  A higher limit improves compression ratio but degrades the speed.
    private int maxChainLength;

    // Attempt to find a better match only when the current match is strictly
    // smaller than this value. This mechanism is used only for compression
    // levels >= 4.
    private int maxLazyMatch;

    // Insert new strings in the hash table only if the match length is not
    // greater than this length. This saves time but degrades compression.
    // max_insert_length is used only for compression levels <= 3.
    private int level;    // compression level (1..9)
    private int strategy; // favor or force Huffman coding

    // Use a faster search when the previous match is longer than this
    private int goodMatch;

    // Stop searching when current match exceeds this
    private int niceMatch;
    private int[] dynLtree;       // literal and length tree
    private int[] dynDtree;       // distance tree
    private int[] blTree;         // Huffman tree for bit lengths
    private final Tree lDesc = new Tree();  // desc for literal tree
    private final Tree dDesc = new Tree();  // desc for distance tree
    private final Tree blDesc = new Tree(); // desc for bit length tree

    // number of codes at each bit length for an optimal tree
    public int[] blCount = new int[MAX_BITS + 1];

    // heap used to build the Huffman trees
    public int[] heap = new int[2 * L_CODES + 1];
    public int heapLen;               // number of elements in the heap
    public int heapMax;               // element of largest frequency
    // The sons of heap[n] are heap[2*n] and heap[2*n+1]. heap[0] is not used.
    // The same heap array is used to build all trees.

    // Depth of each subtree used as tie breaker for trees of equal frequency
    public byte[] depth = new byte[2 * L_CODES + 1];
    private int lBuf;               // index for literals or lengths */

    // Size of match buffer for literals/lengths.  There are 4 reasons for
    // limiting litBufsize to 64K:
    //   - frequencies can be kept in 16 bit counters
    //   - if compression is not successful for the first block, all input
    //     data is still in the window so we can still emit a stored block even
    //     when input comes from standard input.  (This can also be done for
    //     all blocks if litBufsize is not greater than 32K.)
    //   - if compression is not successful for a file smaller than 64K, we can
    //     even emit a stored file instead of a stored block (saving 5 bytes).
    //     This is applicable only for zip (not gzip or zlib).
    //   - creating new Huffman trees less frequently may not provide fast
    //     adaptation to changes in the input data statistics. (Take for
    //     example a binary file with poorly compressible code followed by
    //     a highly compressible string table.) Smaller buffer sizes give
    //     fast adaptation but have of course the overhead of transmitting
    //     trees more frequently.
    //   - I can't count above 4
    private int litBufsize;
    private int lastLit;      // running index in lBuf

    // Buffer for distances. To simplify the code, dBuf and lBuf have
    // the same number of elements. To use different lengths, an extra flag
    // array would be necessary.
    private int dBuf;         // index of pendig_buf
    public int optLen;        // bit length of current block with optimal trees
    public int staticLen;     // bit length of current block with static trees
    private int matches;        // number of string matches in current block
    private int lastEobLen;   // bit length of EOB code for last block

    // Output buffer. bits are inserted starting at the bottom (least
    // significant bits).
    private int biBuf;

    // Number of valid bits in biBuf.  All bits above the last valid bit
    // are always zero.
    private int biValid;

    Deflate() {
        dynLtree = new int[HEAP_SIZE * 2];
        dynDtree = new int[(2 * D_CODES + 1) * 2]; // distance tree
        blTree = new int[(2 * BL_CODES + 1) * 2];  // Huffman tree for bit lengths
    }

    private void lmInit() {
        actualWindowSize = 2 * windowSize;

        head[hashSize - 1] = 0;
        for (int i = 0; i < hashSize - 1; i++) {
            head[i] = 0;
        }

        // Set the default configuration parameters:
        maxLazyMatch = Deflate.CONFIG_TABLE[level].maxLazy;
        goodMatch = Deflate.CONFIG_TABLE[level].goodLength;
        niceMatch = Deflate.CONFIG_TABLE[level].niceLength;
        maxChainLength = Deflate.CONFIG_TABLE[level].maxChain;

        strstart = 0;
        blockStart = 0;
        lookahead = 0;
        matchLength = prevLength = MIN_MATCH - 1;
        matchAvailable = 0;
        insH = 0;
    }

    // Initialize the tree data structures for a new zlib stream.
    private void trInit() {

        lDesc.dynTree = dynLtree;
        lDesc.statDesc = StaticTree.static_l_desc;

        dDesc.dynTree = dynDtree;
        dDesc.statDesc = StaticTree.static_d_desc;

        blDesc.dynTree = blTree;
        blDesc.statDesc = StaticTree.static_bl_desc;

        biBuf = 0;
        biValid = 0;
        lastEobLen = 8; // enough lookahead for inflate

        // Initialize the first block of the first file:
        initBlock();
    }

    private void initBlock() {
        // Initialize the trees.
        for (int i = 0; i < L_CODES; i++) {
            dynLtree[i * 2] = 0;
        }
        for (int i = 0; i < D_CODES; i++) {
            dynDtree[i * 2] = 0;
        }
        for (int i = 0; i < BL_CODES; i++) {
            blTree[i * 2] = 0;
        }

        dynLtree[END_BLOCK * 2] = 1;
        optLen = staticLen = 0;
        lastLit = matches = 0;
    }

    // Restore the heap property by moving down the tree starting at node k,
    // exchanging a node with the smallest of its two sons if necessary, stopping
    // when the heap property is re-established (each father smaller than its
    // two sons).
    public void pqdownheap(final int[] tree, // the tree to restore
            int k //NOPMD node to move down
            ) {
        final int v = heap[k]; //NOPMD
        int j = k << 1;  //NOPMD left son of k
        while (j <= heapLen) {
            // Set j to the smallest of the two sons:
            if (j < heapLen &&
                    smaller(tree, heap[j + 1], heap[j], depth)) {
                j++;
            }
            // Exit if v is smaller than both sons
            if (smaller(tree, v, heap[j], depth)) {
                break;
            }

            // Exchange v with the smallest son
            heap[k] = heap[j];
            k = j;
            // And continue down the tree, setting j to the left son of k
            j <<= 1;
        }
        heap[k] = v;
    }

    private static boolean smaller(final int[] tree, 
            final int n, final int m, final byte[] depth) { //NOPMD
        final int tn2 = tree[n * 2];
        final int tm2 = tree[m * 2];
        return (tn2 < tm2 ||
                (tn2 == tm2 && depth[n] <= depth[m]));
    }

    // Scan a literal or distance tree to determine the frequencies of the codes
    // in the bit length tree.
    private void scanTree( //NOPMD complex
            final int[] tree,// the tree to be scanned
            final int maxCode // and its largest code of non zero frequency
             // and its largest code of non zero frequency
            ) {
        int n;                     //NOPMD iterates over all tree elements
        int prevlen = -1;          // last emitted length
        int curlen;                // length of current code
        int nextlen = tree[0 * 2 + 1]; // length of next code
        int count = 0;             // repeat count of the current code
        int max_count = 7;         // max repeat count
        int min_count = 4;         // min repeat count

        if (nextlen == 0) {
            max_count = 138;
            min_count = 3;
        }
        tree[(maxCode + 1) * 2 + 1] = 0xffff; // guard

        for (n = 0; n <= maxCode; n++) {
            curlen = nextlen;
            nextlen = tree[(n + 1) * 2 + 1];
            if (++count < max_count && curlen == nextlen) {
                continue;
            } else if (count < min_count) {
                blTree[curlen * 2] += count;
            } else if (curlen != 0) { //NOPMD C-ism
                if (curlen != prevlen) {
                    blTree[curlen * 2]++;
                }
                blTree[REP_3_6 * 2]++;
            } else if (count <= 10) {
                blTree[REPZ_3_10 * 2]++;
            } else {
                blTree[REPZ_11_138 * 2]++;
            }
            count = 0;
            prevlen = curlen;
            if (nextlen == 0) {
                max_count = 138;
                min_count = 3;
            } else if (curlen == nextlen) {
                max_count = 6;
                min_count = 3;
            } else {
                max_count = 7;
                min_count = 4;
            }
        }
    }

    // Construct the Huffman tree for the bit lengths and return the index in
    // BL_ORDER of the last bit length code to send.
    private int buildBlTree() {
        int max_blindex;  // index of last bit length code of non zero freq

        // Determine the bit length frequencies for literal and distance trees
        scanTree(dynLtree, lDesc.maxCode);
        scanTree(dynDtree, dDesc.maxCode);

        // Build the bit length tree:
        blDesc.buildTree(this);
        // optLen now includes the length of the tree representations, except
        // the lengths of the bit lengths codes and the 5+5+4 bits for the counts.

        // Determine the number of bit length codes to send. The pkzip format
        // requires that at least 4 bit length codes be sent. (appnote.txt says
        // 3 but the actual value used is 4.)
        for (max_blindex = BL_CODES - 1; max_blindex >= 3; max_blindex--) {
            if (blTree[Tree.BL_ORDER[max_blindex] * 2 + 1] != 0) {
                break;
            }
        }
        // Update optLen to include the bit length tree and counts
        optLen += 3 * (max_blindex + 1) + 5 + 5 + 4;

        return max_blindex;
    }


    // Send the header for a block using dynamic Huffman trees: the counts, the
    // lengths of the bit length codes, the literal tree and the distance tree.
    // IN assertion: lcodes >= 257, dcodes >= 1, blcodes >= 4.
    private void sendAllTrees(final int lcodes, final int dcodes, final int blcodes) {
        int rank;                    // index in BL_ORDER

        sendBits(lcodes - 257, 5); // not +255 as stated in appnote.txt
        sendBits(dcodes - 1, 5);
        sendBits(blcodes - 4, 4); // not -3 as stated in appnote.txt
        for (rank = 0; rank < blcodes; rank++) {
            sendBits(blTree[Tree.BL_ORDER[rank] * 2 + 1], 3);
        }
        sendTree(dynLtree, lcodes - 1); // literal tree
        sendTree(dynDtree, dcodes - 1); // distance tree
    }

    // Send a literal or distance tree in compressed form, using the codes in
    // blTree.
    private void sendTree( //NOPMD complex
            final int[] tree,// the tree to be sent
            final int maxCode // and its largest code of non zero frequency
             // and its largest code of non zero frequency
            ) {
        int n;                     //NOPMD iterates over all tree elements
        int prevlen = -1;          // last emitted length
        int curlen;                // length of current code
        int nextlen = tree[0 * 2 + 1]; // length of next code
        int count = 0;             // repeat count of the current code
        int max_count = 7;         // max repeat count
        int min_count = 4;         // min repeat count

        if (nextlen == 0) {
            max_count = 138;
            min_count = 3;
        }

        for (n = 0; n <= maxCode; n++) {
            curlen = nextlen;
            nextlen = tree[(n + 1) * 2 + 1];
            if (++count < max_count && curlen == nextlen) {
                continue;
            } else if (count < min_count) {
                do {
                    sendCode(curlen, blTree);
                } while (--count != 0);
            } else if (curlen != 0) { //NOPMD C-ism
                if (curlen != prevlen) {
                    sendCode(curlen, blTree);
                    count--;
                }
                sendCode(REP_3_6, blTree);
                sendBits(count - 3, 2);
            } else if (count <= 10) {
                sendCode(REPZ_3_10, blTree);
                sendBits(count - 3, 3);
            } else {
                sendCode(REPZ_11_138, blTree);
                sendBits(count - 11, 7);
            }
            count = 0;
            prevlen = curlen;
            if (nextlen == 0) {
                max_count = 138;
                min_count = 3;
            } else if (curlen == nextlen) {
                max_count = 6;
                min_count = 3;
            } else {
                max_count = 7;
                min_count = 4;
            }
        }
    }

    // Output a byte on the stream.
    // IN assertion: there is enough room in pending_buf.
    private void putByte(final byte[] buf, final int start, final int len) {
        System.arraycopy(buf, start, pendingBuf, pending, len);
        pending += len;
    }

    private void putByte(final byte value) {
        pendingBuf[pending++] = value;
    }

    private void putShort(final int value) {
        putByte((byte) (value&0xff));
        putByte((byte) ((value >>> 8)&0xff));
    }

    private void putShortMSB(final int value) {
        putByte((byte) ((value >> 8)&0xff));
        putByte((byte) (value&0xff));
    }

    private void sendCode(final int value, final int[] tree) {
        final int value2 = value * 2;
        sendBits((tree[value2] & 0xffff), (tree[value2 + 1] & 0xffff));
    }

    private void sendBits(final int value, final int length) {
        final int len = length;
        if (biValid > (int) BUF_SIZE - len) {
            final int val = value;
//      biBuf |= (val << biValid);
            biBuf |= ((val << biValid) & 0xffff);
            putShort(biBuf);
            biBuf = 0xFFFF & (val >>> (BUF_SIZE - biValid));
            biValid += len - BUF_SIZE;
        } else {
//      biBuf |= (value) << biValid;
            biBuf |= (((value) << biValid) & 0xffff);
            biValid += len;
        }
    }

    // Send one empty static block to give enough lookahead for inflate.
    // This takes 10 bits, of which 7 may remain in the bit buffer.
    // The current inflate code requires 9 bits of lookahead. If the
    // last two codes for the previous block (real code plus EOB) were coded
    // on 5 bits or less, inflate may have only 5+3 bits of lookahead to decode
    // the last real code. In this case we send two empty static blocks instead
    // of one. (There are no problems if the previous block is stored or fixed.)
    // To simplify the code, we assume the worst case of last real code encoded
    // on one bit only.
    private void trAlign() {
        sendBits(STATIC_TREES << 1, 3);
        sendCode(END_BLOCK, StaticTree.STATIC_LTREE);

        biFlush();

        // Of the 10 bits for the empty block, we have already sent
        // (10 - biValid) bits. The lookahead for the last real code (before
        // the EOB of the previous block) was thus at least one plus the length
        // of the EOB plus what we have just sent of the empty static block.
        if (1 + lastEobLen + 10 - biValid < 9) {
            sendBits(STATIC_TREES << 1, 3);
            sendCode(END_BLOCK, StaticTree.STATIC_LTREE);
            biFlush();
        }
        lastEobLen = 7;
    }


    // Save the match info and tally the frequency counts. Return true if
    // the current block must be flushed.
    private boolean trTally(
            int dist, // distance of matched string
            final int lcount // match length-MIN_MATCH or unmatched char (if dist==0)
             // match length-MIN_MATCH or unmatched char (if dist==0)
            ) {

        pendingBuf[dBuf + lastLit * 2] = (byte) (dist >>> 8);
        pendingBuf[dBuf + lastLit * 2 + 1] = (byte) dist;

        pendingBuf[lBuf + lastLit] = (byte) lcount;
        lastLit++;

        if (dist == 0) {
            // lcount is the unmatched char
            dynLtree[lcount * 2]++;
        } else {
            matches++;
            // Here, lcount is the match length - MIN_MATCH
            dist--;             // dist = match distance - 1
            dynLtree[(Tree.LENGTH_CODE[lcount] + LITERALS + 1) * 2]++;
            dynDtree[Tree.dCode(dist) * 2]++;
        }

        if ((lastLit & 0x1fff) == 0 && level > 2) {
            // Compute an upper bound for the compressed length
            int out_length = lastLit * 8;
            final int in_length = strstart - blockStart;
            int dcode;
            for (dcode = 0; dcode < D_CODES; dcode++) {
                out_length += (int) dynDtree[dcode * 2] *
                        (5L + Tree.EXTRA_DBITS[dcode]);
            }
            out_length >>>= 3;
            if ((matches < (lastLit / 2)) && out_length < in_length / 2) {
                return true;
            }
        }

        return (lastLit == litBufsize - 1);
    // We avoid equality with litBufsize because of wraparound at 64K
    // on 16 bit machines and because stored blocks are restricted to
    // 64K-1 bytes.
    }

    // Send the block data compressed using the given Huffman trees
    private void compressBlock(final int[] ltree, final int[] dtree) {
        int dist;      // distance of matched string
        int lc;         //NOPMD match length or unmatched char (if dist == 0)
        int lx = 0;     //NOPMD running index in lBuf
        int code;       // the code to send
        int extra;      // number of extra bits to send

        if (lastLit != 0) {
            do {
                dist = ((pendingBuf[dBuf + lx * 2] << 8) & 0xff00) |
                        (pendingBuf[dBuf + lx * 2 + 1] & 0xff);
                lc = (pendingBuf[lBuf + lx]) & 0xff;
                lx++;

                if (dist == 0) {
                    sendCode(lc, ltree); // send a literal byte
                } else {
                    // Here, lcount is the match length - MIN_MATCH
                    code = Tree.LENGTH_CODE[lc];

                    sendCode(code + LITERALS + 1, ltree); // send the length code
                    extra = Tree.EXTRA_LBITS[code];
                    if (extra != 0) {
                        lc -= Tree.BASE_LENGTH[code];
                        sendBits(lc, extra);       // send the extra length bits
                    }
                    dist--; // dist is now the match distance - 1
                    code = Tree.dCode(dist);

                    sendCode(code, dtree);       // send the distance code
                    extra = Tree.EXTRA_DBITS[code];
                    if (extra != 0) {
                        dist -= Tree.BASE_DIST[code];
                        sendBits(dist, extra);   // send the extra distance bits
                    }
                } // literal or match pair ?

            // Check that the overlay between pending_buf and dBuf+lBuf is ok:
            } while (lx < lastLit);
        }

        sendCode(END_BLOCK, ltree);
        lastEobLen = ltree[END_BLOCK * 2 + 1];
    }

    // Set the data type to ASCII or BINARY, using a crude approximation:
    // binary if more than 20% of the bytes are <= 6 or >= 128, ascii otherwise.
    // IN assertion: the fields freq of dynLtree are set and the total of all
    // frequencies does not exceed 64K (to fit in an int on 16 bit machines).
    private void setDataType() {
        int n = 0; //NOPMD
        int ascii_freq = 0;
        int bin_freq = 0;
        while (n < 7) {
            bin_freq += dynLtree[n * 2];
            n++;
        }
        while (n < 128) {
            ascii_freq += dynLtree[n * 2];
            n++;
        }
        while (n < LITERALS) {
            bin_freq += dynLtree[n * 2];
            n++;
        }
        dataType = (byte) (bin_freq > (ascii_freq >>> 2) ? Z_BINARY : Z_ASCII);
    }

    // Flush the bit buffer, keeping at most 7 bits in it.
    private void biFlush() {
        if (biValid == 16) {
            putShort(biBuf);
            biBuf = 0;
            biValid = 0;
        } else if (biValid >= 8) {
            putByte((byte) biBuf);
            biBuf >>>= 8;
            biValid -= 8;
        }
    }

    // Flush the bit buffer and align the output on a byte boundary
    private void biWindup() {
        if (biValid > 8) {
            putShort(biBuf);
        } else if (biValid > 0) {
            putByte((byte) biBuf);
        }
        biBuf = 0;
        biValid = 0;
    }

    // Copy a stored block, storing first the length and its
    // one's complement if requested.
    private void copyBlock(final int buf, // the input data
            final int len, // its length
            final boolean header // true if block header must be written
            ) {
        //int index = 0;
        biWindup();      // align on byte boundary
        lastEobLen = 8; // enough lookahead for inflate

        if (header) {
            putShort(0xffff & len);
            putShort(0xffff & ~len);
        }

        //  while(len--!=0) {
        //    putByte(window[buf+index]);
        //    index++;
        //  }
        putByte(window, buf, len);
    }

    private void flushBlockOnly(final boolean eof) {
        trFlushBlock(blockStart >= 0 ? blockStart : -1,
                strstart - blockStart,
                eof);
        blockStart = strstart;
        strm.flushPending();
    }

    // Copy without compression as much as possible from the input stream, return
    // the current block state.
    // This function does not insert new strings in the dictionary since
    // uncompressible data is probably not useful. This function is used
    // only for the level=0 compression option.
    // NOTE: this function should be optimized to avoid extra copying from
    // window to pending_buf.
    private int deflateStored(final int flush) { //NOPMD complex
        // Stored blocks are limited to 0xffff bytes, pending_buf is limited
        // to pending_buf_size, and each stored block has a 5 byte header:

        int max_block_size = 0xffff;
        int max_start;

        if (max_block_size > pendingBufSize - 5) {
            max_block_size = pendingBufSize - 5;
        }

        // Copy as much as possible from input to output:
        while (true) {
            // Fill the window as much as possible:
            if (lookahead <= 1) {
                fillWindow();
                if (lookahead == 0 && flush == Z_NO_FLUSH) {
                    return NEED_MORE;
                }
                if (lookahead == 0) {
                    break;
                } // flush the current block
            }

            strstart += lookahead;
            lookahead = 0;

            // Emit a stored block if pending_buf will be full:
            max_start = blockStart + max_block_size;
            if (strstart == 0 || strstart >= max_start) {
                // strstart == 0 is possible when wraparound on 16-bit machine
                lookahead = (int) (strstart - max_start);
                strstart = (int) max_start;

                flushBlockOnly(false);
                if (strm.availOut == 0) {
                    return NEED_MORE;
                }

            }

            // Flush if we may have to slide, otherwise blockStart may become
            // negative and the data will be gone:
            if (strstart - blockStart >= windowSize - MIN_LOOKAHEAD) {
                flushBlockOnly(false);
                if (strm.availOut == 0) {
                    return NEED_MORE;
                }
            }
        }

        flushBlockOnly(flush == Z_FINISH);
        if (strm.availOut == 0) {
            return (flush == Z_FINISH) ? FINISH_STARTED : NEED_MORE;
        }

        return flush == Z_FINISH ? FINISH_DONE : BLOCK_DONE;
    }

    // Send a stored block
    private void trStoredBlock(final int buf, // input block
            final int storedLen, // length of input block
            final boolean eof // true if this is the last block for a file
             // true if this is the last block for a file
            ) {
        sendBits((STORED_BLOCK << 1) + (eof ? 1 : 0), 3);  // send block type
        copyBlock(buf, storedLen, true);          // with header
    }

    // Determine the best encoding for the current block: dynamic trees, static
    // trees or store, and output the encoded block to the zip file.
    private void trFlushBlock(//NOPMD complex
            final int buf, // input block, or NULL if too old
            final int storedLen, // length of input block
            final boolean eof // true if this is the last block for a file
             // true if this is the last block for a file
            ) {
        int opt_lenb, static_lenb;// optLen and staticLen in bytes
        int max_blindex = 0;      // index of last bit length code of non zero freq

        // Build the Huffman trees unless a stored block is forced
        if (level > 0) {
            // Check if the file is ascii or binary
            if (dataType == Z_UNKNOWN) {
                setDataType();
            }

            // Construct the literal and distance trees
            lDesc.buildTree(this);

            dDesc.buildTree(this);

            // At this point, optLen and staticLen are the total bit lengths of
            // the compressed block data, excluding the tree representations.

            // Build the bit length tree for the above two trees, and get the index
            // in BL_ORDER of the last bit length code to send.
            max_blindex = buildBlTree();

            // Determine the best encoding. Compute first the block length in bytes
            opt_lenb = (optLen + 3 + 7) >>> 3;
            static_lenb = (staticLen + 3 + 7) >>> 3;

            if (static_lenb <= opt_lenb) {
                opt_lenb = static_lenb;
            }
        } else {
            opt_lenb = static_lenb = storedLen + 5; // force a stored block
        }

        if (storedLen + 4 <= opt_lenb && buf != -1) {
            // 4: two words for the lengths
            // The test buf != NULL is only necessary if LIT_BUFSIZE > WSIZE.
            // Otherwise we can't have processed more than WSIZE input bytes since
            // the last block flush, because compression would have been
            // successful. If LIT_BUFSIZE <= WSIZE, it is never too late to
            // transform a block into a stored block.
            trStoredBlock(buf, storedLen, eof);
        } else if (static_lenb == opt_lenb) {
            sendBits((STATIC_TREES << 1) + (eof ? 1 : 0), 3);
            compressBlock(StaticTree.STATIC_LTREE, StaticTree.STATIC_DTREE);
        } else {
            sendBits((DYN_TREES << 1) + (eof ? 1 : 0), 3);
            sendAllTrees(lDesc.maxCode + 1, dDesc.maxCode + 1, max_blindex + 1);
            compressBlock(dynLtree, dynDtree);
        }

        // The above check is made mod 2^32, for files larger than 512 MB
        // and uLong implemented on 32 bits.

        initBlock();

        if (eof) {
            biWindup();
        }
    }

    // Fill the window when the lookahead becomes insufficient.
    // Updates strstart and lookahead.
    //
    // IN assertion: lookahead < MIN_LOOKAHEAD
    // OUT assertions: strstart <= window_size-MIN_LOOKAHEAD
    //    At least one byte has been read, or avail_in == 0; reads are
    //    performed for at least two bytes (required for the zip translate_eol
    //    option -- not supported here).
    private void fillWindow() { //NOPMD complex
        int n, m; //NOPMD
        int p;    //NOPMD
        int more;    // Amount of free space at the end of the window.

        do {
            more = (actualWindowSize - lookahead - strstart);

            // Deal with !@#$% 64K limit:
            if (more == 0 && strstart == 0 && lookahead == 0) {
                more = windowSize;
            } else if (more == -1) {
                // Very unlikely, but possible on 16 bit machine if strstart == 0
                // and lookahead == 1 (input done one byte at time)
                more--;

            // If the window is almost full and there is insufficient lookahead,
            // move the upper half to the lower one to make room in the upper half.
            } else if (strstart >= windowSize + windowSize - MIN_LOOKAHEAD) {
                System.arraycopy(window, windowSize, window, 0, windowSize);
                matchStart -= windowSize;
                strstart -= windowSize; // we now have strstart >= MAX_DIST
                blockStart -= windowSize;

                // Slide the hash table (could be avoided with 32 bit values
                // at the expense of memory usage). We slide even when level == 0
                // to keep the hash table consistent if we switch back to level > 0
                // later. (Using level 0 permanently is not an optimal usage of
                // zlib, so we don't care about this pathological case.)

                n = hashSize;
                p = n;
                do {
                    m = (head[--p] & 0xffff);
                    head[p] = (m >= windowSize ?  (m - windowSize) : 0);
                } while (--n != 0);

                n = windowSize;
                p = n;
                do {
                    m = (prev[--p] & 0xffff);
                    prev[p] = (m >= windowSize ?  (m - windowSize) : 0);
                // If n is not on any hash chain, prev[n] is garbage but
                // its value will never be used.
                } while (--n != 0);
                more += windowSize;
            }

            if (strm.availIn == 0) {
                return;
            }

            // If there was no sliding:
            //    strstart <= WSIZE+MAX_DIST-1 && lookahead <= MIN_LOOKAHEAD - 1 &&
            //    more == window_size - lookahead - strstart
            // => more >= window_size - (MIN_LOOKAHEAD-1 + WSIZE + MAX_DIST-1)
            // => more >= window_size - 2*WSIZE + 2
            // In the BIG_MEM or MMAP case (not yet supported),
            //   window_size == input_size + MIN_LOOKAHEAD  &&
            //   strstart + s->lookahead <= input_size => more >= MIN_LOOKAHEAD.
            // Otherwise, window_size == 2*WSIZE so more >= 2.
            // If there was sliding, more >= WSIZE. So in all cases, more >= 2.

            n = strm.readBuf(window, strstart + lookahead, more);
            lookahead += n;

            // Initialize the hash value now that we have some input:
            if (lookahead >= MIN_MATCH) {
                insH = window[strstart] & 0xff;
                insH = (((insH) << hashShift) ^ (window[strstart + 1] & 0xff)) & hashMask;
            }
        // If the whole input has less than MIN_MATCH bytes, insH is garbage,
        // but this is not important since only literal bytes will be emitted.
        } while (lookahead < MIN_LOOKAHEAD && strm.availIn != 0);
    }

    // Compress as much as possible from the input stream, return the current
    // block state.
    // This function does not perform lazy evaluation of matches and inserts
    // new strings in the dictionary only for unmatched strings or for short
    // matches. It is used only for the fast compression options.
    private int deflateFast(final int flush) { //NOPMD long and complex
//    short hash_head = 0; // head of the hash chain
        int hash_head = 0; // head of the hash chain
        boolean bflush;      // set if current block must be flushed

        while (true) {
            // Make sure that we always have enough lookahead, except
            // at the end of the input file. We need MAX_MATCH bytes
            // for the next match, plus MIN_MATCH bytes to insert the
            // string following the next match.
            if (lookahead < MIN_LOOKAHEAD) {
                fillWindow();
                if (lookahead < MIN_LOOKAHEAD && flush == Z_NO_FLUSH) {
                    return NEED_MORE;
                }
                if (lookahead == 0) {
                    break;
                } // flush the current block
            }

            // Insert the string window[strstart .. strstart+2] in the
            // dictionary, and set hash_head to the head of the hash chain:
            if (lookahead >= MIN_MATCH) {
                insH = (((insH) << hashShift) ^ (window[(strstart) + (MIN_MATCH - 1)] & 0xff)) & hashMask;

//	prev[strstart&w_mask]=hash_head=head[insH];
                hash_head = (head[insH] & 0xffff);
                prev[strstart & windowMask] = head[insH];
                head[insH] = strstart;
            }

            // Find the longest match, discarding those <= prevLength.
            // At this point we have always matchLength < MIN_MATCH

            if (hash_head != 0L &&
                    ((strstart - hash_head) & 0xffff) <= windowSize - MIN_LOOKAHEAD) {
                // To simplify the code, we prevent matches with the string
                // of window index 0 (in particular we have to avoid a match
                // of the string with itself at the start of the input file).
                if (strategy != Z_HUFFMAN_ONLY) { //NOPMD C-ism
                    matchLength = longestMatch(hash_head);
                }
            // longestMatch() sets matchStart
            }
            if (matchLength >= MIN_MATCH) {
                //        check_match(strstart, matchStart, matchLength);

                bflush = trTally(strstart - matchStart, matchLength - MIN_MATCH);

                lookahead -= matchLength;

                // Insert new strings in the hash table only if the match length
                // is not too large. This saves time but degrades compression.
                if (matchLength <= maxLazyMatch &&
                        lookahead >= MIN_MATCH) {
                    matchLength--; // string at strstart already in hash table
                    do {
                        strstart++;

                        insH = ((insH << hashShift) ^ (window[(strstart) + (MIN_MATCH - 1)] & 0xff)) & hashMask;
//	    prev[strstart&w_mask]=hash_head=head[insH];
                        hash_head = (head[insH] & 0xffff);
                        prev[strstart & windowMask] = head[insH];
                        head[insH] = strstart;

                    // strstart never exceeds WSIZE-MAX_MATCH, so there are
                    // always MIN_MATCH bytes ahead.
                    } while (--matchLength != 0);
                    strstart++;
                } else {
                    strstart += matchLength;
                    matchLength = 0;
                    insH = window[strstart] & 0xff;

                    insH = (((insH) << hashShift) ^ (window[strstart + 1] & 0xff)) & hashMask;
                // If lookahead < MIN_MATCH, insH is garbage, but it does not
                // matter since it will be recomputed at next deflate call.
                }
            } else {
                // No match, output a literal byte

                bflush = trTally(0, window[strstart] & 0xff);
                lookahead--;
                strstart++;
            }
            if (bflush) {

                flushBlockOnly(false);
                if (strm.availOut == 0) {
                    return NEED_MORE;
                }
            }
        }

        flushBlockOnly(flush == Z_FINISH);
        if (strm.availOut == 0) {
            if (flush == Z_FINISH) {
                return FINISH_STARTED;
            } else {
                return NEED_MORE;
            }
        }
        return flush == Z_FINISH ? FINISH_DONE : BLOCK_DONE;
    }

    // Same as above, but achieves better compression. We use a lazy
    // evaluation for matches: a match is finally adopted only if there is
    // no better match at the next window position.
    private int deflateSlow(final int flush) { //NOPMD long complex
//    short hash_head = 0;    // head of hash chain
        int hash_head = 0;    // head of hash chain
        boolean bflush;         // set if current block must be flushed

        // Process the input block.
        while (true) {
            // Make sure that we always have enough lookahead, except
            // at the end of the input file. We need MAX_MATCH bytes
            // for the next match, plus MIN_MATCH bytes to insert the
            // string following the next match.

            if (lookahead < MIN_LOOKAHEAD) {
                fillWindow();
                if (lookahead < MIN_LOOKAHEAD && flush == Z_NO_FLUSH) {
                    return NEED_MORE;
                }
                if (lookahead == 0) {
                    break;
                } // flush the current block
            }

            // Insert the string window[strstart .. strstart+2] in the
            // dictionary, and set hash_head to the head of the hash chain:

            if (lookahead >= MIN_MATCH) {
                insH = (((insH) << hashShift) ^ (window[(strstart) + (MIN_MATCH - 1)] & 0xff)) & hashMask;
//	prev[strstart&w_mask]=hash_head=head[insH];
                hash_head = (head[insH] & 0xffff);
                prev[strstart & windowMask] = head[insH];
                head[insH] = strstart;
            }

            // Find the longest match, discarding those <= prevLength.
            prevLength = matchLength;
            prevMatch = matchStart;
            matchLength = MIN_MATCH - 1;

            if (hash_head != 0 && prevLength < maxLazyMatch &&
                    ((strstart - hash_head) & 0xffff) <= windowSize - MIN_LOOKAHEAD) {
                // To simplify the code, we prevent matches with the string
                // of window index 0 (in particular we have to avoid a match
                // of the string with itself at the start of the input file).

                if (strategy != Z_HUFFMAN_ONLY) {
                    matchLength = longestMatch(hash_head);
                }
                // longestMatch() sets matchStart

                if (matchLength <= 5 && (strategy == Z_FILTERED ||
                        (matchLength == MIN_MATCH &&
                        strstart - matchStart > 4096))) {

                    // If prevMatch is also MIN_MATCH, matchStart is garbage
                    // but we will ignore the current match anyway.
                    matchLength = MIN_MATCH - 1;
                }
            }

            // If there was a match at the previous step and the current
            // match is not better, output the previous match:
            if (prevLength >= MIN_MATCH && matchLength <= prevLength) {
                final int max_insert = strstart + lookahead - MIN_MATCH;
                // Do not insert strings in hash table beyond this.

                //          check_match(strstart-1, prevMatch, prevLength);

                bflush = trTally(strstart - 1 - prevMatch, prevLength - MIN_MATCH);

                // Insert in hash table all strings up to the end of the match.
                // strstart-1 and strstart are already inserted. If there is not
                // enough lookahead, the last two strings are not inserted in
                // the hash table.
                lookahead -= prevLength - 1;
                prevLength -= 2;
                do {
                    if (++strstart <= max_insert) {
                        insH = (((insH) << hashShift) ^ (window[(strstart) + (MIN_MATCH - 1)] & 0xff)) & hashMask;
                        //prev[strstart&w_mask]=hash_head=head[insH];
                        hash_head = (head[insH] & 0xffff);
                        prev[strstart & windowMask] = head[insH];
                        head[insH] = strstart;
                    }
                } while (--prevLength != 0);
                matchAvailable = 0;
                matchLength = MIN_MATCH - 1;
                strstart++;

                if (bflush) {
                    flushBlockOnly(false);
                    if (strm.availOut == 0) {
                        return NEED_MORE;
                    }
                }
            } else if (matchAvailable != 0) { //NOPMD C-ism

                // If there was no match at the previous position, output a
                // single literal. If there was a match but the current match
                // is longer, truncate the previous match to a single literal.

                bflush = trTally(0, window[strstart - 1] & 0xff);

                if (bflush) {
                    flushBlockOnly(false);
                }
                strstart++;
                lookahead--;
                if (strm.availOut == 0) {
                    return NEED_MORE;
                }
            } else {
                // There is no previous match to compare with, wait for
                // the next step to decide.

                matchAvailable = 1;
                strstart++;
                lookahead--;
            }
        }

        if (matchAvailable != 0) {
            bflush = trTally(0, window[strstart - 1] & 0xff);
            matchAvailable = 0;
        }
        flushBlockOnly(flush == Z_FINISH);

        if (strm.availOut == 0) {
            if (flush == Z_FINISH) {
                return FINISH_STARTED;
            } else {
                return NEED_MORE;
            }
        }

        return flush == Z_FINISH ? FINISH_DONE : BLOCK_DONE;
    }

    private int longestMatch(int curMatch) { //NOPMD complex
        int chain_length = maxChainLength; // max hash chain length
        int scan = strstart;                 // current string
        int match;                           // matched string
        int len;                             // length of current match
        int best_len = prevLength;          // best match length so far
        final int limit = strstart > (windowSize - MIN_LOOKAHEAD) ? 
            strstart - (windowSize - MIN_LOOKAHEAD) : 0;
        int nice_match = this.niceMatch;

        // Stop when curMatch becomes <= limit. To simplify the code,
        // we prevent matches with the string of window index 0.

        final int wmask = windowMask;

        final int strend = strstart + MAX_MATCH;
        byte scan_end1 = window[scan + best_len - 1];
        byte scan_end = window[scan + best_len];

        // The code is optimized for HASH_BITS >= 8 and MAX_MATCH-2 multiple of 16.
        // It is easy to get rid of this optimization if necessary.

        // Do not waste too much time if we already have a good match:
        if (prevLength >= goodMatch) {
            chain_length >>= 2;
        }

        // Do not look for matches beyond the end of the input. This is necessary
        // to make deflate deterministic.
        if (nice_match > lookahead) {
            nice_match = lookahead;
        }

        do {
            match = curMatch;

            // Skip to next match if the match length cannot increase
            // or if the match length is less than 2:
            if (window[match + best_len] != scan_end ||
                    window[match + best_len - 1] != scan_end1 ||
                    window[match] != window[scan] ||
                    window[++match] != window[scan + 1]) {
                continue;
            }

            // The check at best_len-1 can be removed because it will be made
            // again later. (This heuristic is not always a win.)
            // It is not necessary to compare scan[2] and match[2] since they
            // are always equal when the other bytes match, given that
            // the hash keys are equal and that HASH_BITS >= 8.
            scan += 2;
            match++;

            // We check for insufficient lookahead only every 8th comparison;
            // the 256th check will be made at strstart+258.
            do {
            } while (window[++scan] == window[++match] &&
                    window[++scan] == window[++match] &&
                    window[++scan] == window[++match] &&
                    window[++scan] == window[++match] &&
                    window[++scan] == window[++match] &&
                    window[++scan] == window[++match] &&
                    window[++scan] == window[++match] &&
                    window[++scan] == window[++match] &&
                    scan < strend);

            len = MAX_MATCH - (int) (strend - scan);
            scan = strend - MAX_MATCH;

            if (len > best_len) {
                matchStart = curMatch;
                best_len = len;
                if (len >= nice_match) {
                    break;
                }
                scan_end1 = window[scan + best_len - 1];
                scan_end = window[scan + best_len];
            }

        } while ((curMatch = (prev[curMatch & wmask] & 0xffff)) > limit && --chain_length != 0);

        if (best_len <= lookahead) {
            return best_len;
        }
        return lookahead;
    }

    public int deflateInit(final ZStream strm, final int level, final int bits) {
        return deflateInit2(strm, level, Z_DEFLATED, bits, JZlib.DEF_MEM_LEVEL,
                Z_DEFAULT_STRATEGY);
    }

    public int deflateInit(final ZStream strm, final int level) {
        return deflateInit(strm, level, JZlib.MAX_WBITS);
    }

    private int deflateInit2(//NOPMD complex
            final ZStream strm, int level, final int method, int windowBits,
            final int memLevel, final int strategy) {
        int noheaderTmp = 0;
        //    byte[] my_version=ZLIB_VERSION;

        //
        //  if (version == null || version[0] != my_version[0]
        //  || stream_size != sizeof(z_stream)) {
        //  return Z_VERSION_ERROR;
        //  }

        strm.msg = null;

        if (level == JZlib.Z_DEFAULT_COMPRESSION) {
            level = 6;
        }

        if (windowBits < 0) { // undocumented feature: suppress zlib header
            noheaderTmp = 1;
            windowBits = -windowBits;
        }

        if (memLevel < 1 || memLevel > JZlib.MAX_MEM_LEVEL ||
                method != Z_DEFLATED ||
                windowBits < 9 || windowBits > 15 || level < 0 || level > 9 ||
                strategy < 0 || strategy > Z_HUFFMAN_ONLY) {
            return Z_STREAM_ERROR;
        }

        strm.dstate = (Deflate) this;

        this.noheader = noheaderTmp;
        this.windowBits = windowBits;
        windowSize = 1 << this.windowBits;
        windowMask = windowSize - 1;
   
        final int hashBits = memLevel + 7; // log2(hashSize)
        hashSize = 1 << hashBits;
        hashMask = hashSize - 1;
        hashShift = ((hashBits + MIN_MATCH - 1) / MIN_MATCH);

        window = new byte[windowSize * 2];
        prev = new int[windowSize];
        head = new int[hashSize];

        litBufsize = 1 << (memLevel + 6); // 16K elements by default

        // We overlay pending_buf and dBuf+lBuf. This works since the average
        // output size for (length,distance) codes is <= 24 bits.
        pendingBuf = new byte[litBufsize * 4];
        pendingBufSize = litBufsize * 4;

        dBuf = litBufsize / 2;
        lBuf = (1 + 2) * litBufsize;

        this.level = level;

//System.out.println("level="+level);

        this.strategy = strategy;
        //this.method = (byte) method;

        return deflateReset(strm);
    }

    private int deflateReset(final ZStream strm) {
        strm.totalIn = strm.totalOut = 0;
        strm.msg = null; //
        //strm.dataType = Z_UNKNOWN;

        pending = 0;
        pendingOut = 0;

        if (noheader < 0) {
            noheader = 0; // was set to -1 by deflate(..., Z_FINISH);
        }
        status = (noheader != 0) ? BUSY_STATE : INIT_STATE; //NOPMD C-ism
        strm.adler = strm.adlerObj.adler32(0, null, 0, 0);

        lastFlush = Z_NO_FLUSH;

        trInit();
        lmInit();
        return Z_OK;
    }

    public int deflateEnd() {
        if (status != INIT_STATE && status != BUSY_STATE && status != FINISH_STATE) {
            return Z_STREAM_ERROR;
        }
        // Deallocate in reverse order of allocations:
        pendingBuf = null;
        head = null;
        prev = null;
        window = null;
        // free
        // dstate=null;
        return status == BUSY_STATE ? Z_DATA_ERROR : Z_OK;
    }

    public int deflateParams(final ZStream strm, int _level, final int _strategy) {
        int err = Z_OK;

        if (_level == JZlib.Z_DEFAULT_COMPRESSION) {
            _level = 6;
        }
        if (_level < 0 || _level > 9 ||
                _strategy < 0 || _strategy > Z_HUFFMAN_ONLY) {
            return Z_STREAM_ERROR;
        }

        if (CONFIG_TABLE[level].func != CONFIG_TABLE[_level].func &&
                strm.totalIn != 0) {
            // Flush the last buffer:
            err = strm.deflate(Z_PARTIAL_FLUSH);
        }

        if (level != _level) {
            level = _level;
            maxLazyMatch = CONFIG_TABLE[level].maxLazy;
            goodMatch = CONFIG_TABLE[level].goodLength;
            niceMatch = CONFIG_TABLE[level].niceLength;
            maxChainLength = CONFIG_TABLE[level].maxChain;
        }
        strategy = _strategy;
        return err;
    }

    public int deflateSetDictionary(final ZStream strm, final byte[] dictionary, final int dictLength) {
        int length = dictLength;
        int index = 0;

        if (dictionary == null || status != INIT_STATE) {
            return Z_STREAM_ERROR;
        }

        strm.adler = strm.adlerObj.adler32(strm.adler, dictionary, 0, dictLength);

        if (length < MIN_MATCH) {
            return Z_OK;
        }
        if (length > windowSize - MIN_LOOKAHEAD) {
            length = windowSize - MIN_LOOKAHEAD;
            index = dictLength - length; // use the tail of the dictionary
        }
        System.arraycopy(dictionary, index, window, 0, length);
        strstart = length;
        blockStart = length;

        // Insert all strings in the hash table (except for the last two bytes).
        // s->lookahead stays null, so s->insH will be recomputed at the next
        // call of fillWindow.

        insH = window[0] & 0xff;
        insH = (((insH) << hashShift) ^ (window[1] & 0xff)) & hashMask;

        for (int n = 0; n <= length - MIN_MATCH; n++) {
            insH = (((insH) << hashShift) ^ (window[(n) + (MIN_MATCH - 1)] & 0xff)) & hashMask;
            prev[n & windowMask] = head[insH];
            head[insH] = n;
        }
        return Z_OK;
    }

    public int deflate(final ZStream strm, final int flush) { //NOPMD long, complex
        int old_flush;

        if (flush > Z_FINISH || flush < 0) {
            return Z_STREAM_ERROR;
        }

        if (strm.nextOut == null ||
                (strm.nextIn == null && strm.availIn != 0) ||
                (status == FINISH_STATE && flush != Z_FINISH)) {
            strm.msg = Z_ERRMSG[Z_NEED_DICT - (Z_STREAM_ERROR)];
            return Z_STREAM_ERROR;
        }
        if (strm.availOut == 0) {
            strm.msg = Z_ERRMSG[Z_NEED_DICT - (Z_BUF_ERROR)];
            return Z_BUF_ERROR;
        }

        this.strm = strm; // just in case
        old_flush = lastFlush;
        lastFlush = flush;

        // Write the zlib header
        if (status == INIT_STATE) {
            int header = (Z_DEFLATED + ((windowBits - 8) << 4)) << 8;
            int level_flags = ((level - 1) & 0xff) >> 1;

            if (level_flags > 3) {
                level_flags = 3;
            }
            header |= (level_flags << 6);
            if (strstart != 0) {
                header |= PRESET_DICT;
            }
            header += 31 - (header % 31);

            status = BUSY_STATE;
            putShortMSB(header);


            // Save the adler32 of the preset dictionary:
            if (strstart != 0) {
                putShortMSB((int) (strm.adler >>> 16));
                putShortMSB((int) (strm.adler & 0xffff));
            }
            strm.adler = strm.adlerObj.adler32(0, null, 0, 0);
        }

        // Flush as much pending output as possible
        if (pending != 0) { //NOPMD C-ism
            strm.flushPending();
            if (strm.availOut == 0) {
                //System.out.println("  avail_out==0");
                // Since avail_out is 0, deflate will be called again with
                // more output space, but possibly with both pending and
                // avail_in equal to zero. There won't be anything to do,
                // but this is not an error situation so make sure we
                // return OK instead of BUF_ERROR at next call of deflate:
                lastFlush = -1;
                return Z_OK;
            }

        // Make sure there is something to do and avoid duplicate consecutive
        // flushes. For repeated and useless calls with Z_FINISH, we keep
        // returning Z_STREAM_END instead of Z_BUFF_ERROR.
        } else if (strm.availIn == 0 && flush <= old_flush &&
                flush != Z_FINISH) {
            strm.msg = Z_ERRMSG[Z_NEED_DICT - (Z_BUF_ERROR)];
            return Z_BUF_ERROR;
        }

        // User must not provide more input after the first FINISH:
        if (status == FINISH_STATE && strm.availIn != 0) {
            strm.msg = Z_ERRMSG[Z_NEED_DICT - (Z_BUF_ERROR)];
            return Z_BUF_ERROR;
        }

        // Start a new block or continue the current one.
        if (strm.availIn != 0 || lookahead != 0 ||
                (flush != Z_NO_FLUSH && status != FINISH_STATE)) {
            int bstate = -1;
            switch (CONFIG_TABLE[level].func) {
                case STORED:
                    bstate = deflateStored(flush);
                    break;
                case FAST:
                    bstate = deflateFast(flush);
                    break;
                case SLOW:
                    bstate = deflateSlow(flush);
                    break;
                default:
            }

            if (bstate == FINISH_STARTED || bstate == FINISH_DONE) {
                status = FINISH_STATE;
            }
            if (bstate == NEED_MORE || bstate == FINISH_STARTED) {
                if (strm.availOut == 0) {
                    lastFlush = -1; // avoid BUF_ERROR next call, see above
                }
                return Z_OK;
            // If flush != Z_NO_FLUSH && avail_out == 0, the next call
            // of deflate should use the same flush parameter to make sure
            // that the flush is complete. So we don't have to output an
            // empty block here, this will be done at next call. This also
            // ensures that for a very small output buffer, we emit at most
            // one empty block.
            }

            if (bstate == BLOCK_DONE) {
                if (flush == Z_PARTIAL_FLUSH) {
                    trAlign();
                } else { // FULL_FLUSH or SYNC_FLUSH
                    trStoredBlock(0, 0, false);
                    // For a full flush, this empty block will be recognized
                    // as a special marker by inflate_sync().
                    if (flush == Z_FULL_FLUSH) {
                        //state.head[s.hashSize-1]=0;
                        for (int i = 0; i < hashSize/*-1*/; i++) // forget history
                        {
                            head[i] = 0;
                        }
                    }
                }
                strm.flushPending();
                if (strm.availOut == 0) {
                    lastFlush = -1; // avoid BUF_ERROR at next call, see above
                    return Z_OK;
                }
            }
        }

        if (flush != Z_FINISH) {
            return Z_OK;
        }
        if (noheader != 0) {
            return Z_STREAM_END;
        }

        // Write the zlib trailer (adler32)
        putShortMSB((int) (strm.adler >>> 16));
        putShortMSB((int) (strm.adler & 0xffff));
        strm.flushPending();

        // If avail_out is zero, the application will call deflate again
        // to flush the rest.
        noheader = -1; // write the trailer only once!
        return pending != 0 ? Z_OK : Z_STREAM_END; //NOPMD C-ism
    }
}
