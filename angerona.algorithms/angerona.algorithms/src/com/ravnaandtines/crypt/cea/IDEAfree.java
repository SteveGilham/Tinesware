/**
 *  Class IDEAfree
 *  <P>
 *  Coded & copyright Mr. Tines &lt;tines@windsong.demon.co.uk&gt; 1998
 *  <P>
 * THIS SOFTWARE IS PROVIDED BY THE AUTHORS ''AS IS'' AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHORS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  <P>
 *  This is a no-op used as a placeholder for the actual algorithm -
 *  the actual IDEA class will be in a separate jar file.
 * <p>
 * @author Mr. Tines
 * @version 1.0 17-Nov-1998
 * @version 2.0 25-Dec-2007
 */
 /* This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details. */


package com.ravnaandtines.crypt.cea;

public final class IDEAfree implements CEA {

    public IDEAfree() {
        destroy();
    }

    /**
     * Signal that the algorithm is included (a skeleton implementation
     * would have this routine return false, return a null licence string
     * and nothing else
     */
    public static boolean isAvailable() {
        return false;
    }

    /**
     * Return the algorithm licencse as a string
     * @return IDEA licence
     */
    public static String getLicence() {
        return "";
    }

    /**
     * Initialise the object with one or three key blocks
     * @param key array of key bytes, 1 or 3 key block lengths
     * @param triple true if three keys for triple application
     */
    public void init(final byte[] key, final int offset, final boolean triple) {
        // empty
    }

    /**
     * Transform one block in ecb mode
     * @param encrypt true if forwards transformation
     * @param in input block
     * @param offin offset into block of input data
     * @param out output block
     * @param offout offset into block of output data
     */
    public void ecb(final boolean encrypt, final byte[] input, final int offin,
            final byte[] out, final int offout) {
        System.arraycopy(input, offin, out, offout, getBlocksize());
    }

    /**
     * Wipe key schedule information
     */
    public void destroy() {
        // empty
    }

    /**
     * Provide infomation of desired key size
     * @return byte length of key
     */
    public int getKeysize() {
        return 16;
    }

    /**
     * Provide infomation of algorithm block size
     * @return byte length of block
     */
    public int getBlocksize() {
        return 8;
    }
}
