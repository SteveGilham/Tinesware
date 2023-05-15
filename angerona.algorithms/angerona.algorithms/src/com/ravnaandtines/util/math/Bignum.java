package com.ravnaandtines.util.math;

import java.io.*;
import com.ravnaandtines.util.Monitor;

/**
 *  Class Bignum - Multiple precision unsigned integer arithmetic; unlike
 * java.math.BigInteger, it is a mutable class (so that the numbers
 * can be wiped).
 *<p>
 * <STRONG>ONLY PARTIALLY DEBUGGED AFTER CONVERSION FROM 'C'</STRONG>
 * <p>
 * The BIGNUMS module is a multiple precision arithmetic module intended for
 * the implementation of Public Key Encryption systems.  Note that it is NOT
 * a general purpose multiple precision module.  Specifically:-
 *   1) It handles only non-negative integers.
 *   2) It is primarily concerned with (and optimised for) modular arithmetic.
 *   3) It does not handle short integers efficiently.
 *<p>
 * Many routines return booleans indicating success.  In the case of any of these
 * routines failing it is due to failure to allocated memory.
 * modPower alone can also fail due to a user interrupt. //TODO
 * <p>
 *  Coded & copyright Heimdall <heimdall@bifroest.demon.co.uk>  1996
 * All rights reserved.
 *<p>
 *  Elliptic curve encryption support routines coded and copyright
 *  Mr. Tines <tines@windsong.demon.co.uk> 1997
 * <p>
 * Java port Mr. Tines <tines@windsong.demon.co.uk> Jan-Jun 1999
 *  <P>
 *  This application is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *  <P>
 *  This application is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 *  <P>
 *  You should have received a copy of the GNU General Public
 *  License along with this library (file "COPYING"); if not,
 *  write to the Free Software Foundation, Inc.,
 *  59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  <P>
 * @author Mr. Tines
 * @version 1.0 17-Jan-1999
 * @version 2.0 25-Dec-2007
 */

public final class Bignum // NOPMD complex
{

    public static final int MAXUNIT = (~0);
    // unit is 32-bit int; half-unit is 16-bit short
    public static final int UNITBITS = 32;
    //private static final int HALFBITS = 16;
    // private static final int MAXHALF = ((1<<HALFBITS)-1);
    private static final long MASK = 0xFFFFFFFFL;


    // multiply two unsigned int values to give a 64-bit result
    private static long multUnitxUnit(final int op1, final int op2) {
        return (op1 & MASK) * (op2 & MASK);
    }

    // divide a two-unit quantity by a unit
    private static int divUnitByHalf(long dividend, // NO-PMD mutable 
            final int quotient/*, int[] remainder*/) {
        long times = 0, over = 0;
        final long denom = quotient & MASK;

        while (dividend < 0) {
            times += Long.MAX_VALUE / denom;
            over += Long.MAX_VALUE % denom;
            dividend -= Long.MAX_VALUE;
        }

        if (/*remainder != null ||*/ over != 0) {
            over += dividend % denom;
            while (over >= denom) {
                over -= denom;
                ++times;
            }
            /*
            if (remainder != null) {
                remainder[0] = (int) (over & MASK);
            }
             */
        }

        times += dividend / denom;
        return (int) (times & MASK);
    }

    // The actual data - how many of the array are used, and the array
    private int used;
    private int[] digit;

    /**
     * default construction of empty (implicit zero) number
     */
    public Bignum() {
        used = 0;
        digit = new int[0];
    }

    public Bignum(int value)
    {
        this();
        setValue(value);
    }
    
    public Bignum(final Bignum value)
    {
        this();
        copy(value);
    }
    
    
    // structure used to pass context for modular operations
    private static final class ModulusCache // NOPMD constructor
    {

        public int length;
        public int shift;
        public int topUnit;
    }

    // trace info
    private static int[] stats = new int[20];
    //private static Writer file = null;

    /**
     * Sets output for trace information
     * @param sink to which to write log data
     */
    /*
    public static void setLog(final Writer sink) {
        file = sink;
    }
     */

    /*
     * Schneier recommends sieving with seedPrimes up to 2000; there are just over
     * 300 seedPrimes under 2000
     */
    private static final int NPRIMES = 300;
    private static short[] primes = new short[NPRIMES]; //NOPMD short type

    static {
        primes[0] = 0;
    }

    /*
     * The following function calculates the first NPRIMES prime number for
     * use in the initial prime number sieve screening of candidate seedPrimes.
     */
    private static void calculatePrimes() {
        int[] square = new int[NPRIMES];
        int candidate = 5;
        int index = 1;
        int found = 2;

        primes[0] = 2;
        square[0] = 2 * 2;
        primes[1] = 3;
        square[1] = 3 * 3;
        while (found < NPRIMES) {
            while (candidate >= square[index]) {
                if (candidate % primes[index++] == 0) {
                    candidate += 2; // reject candidate -> next candidate
                    index = 1;
                }
            }
            primes[found] = (short) (candidate & 0xFFFF); //NOPMD	accept candidate 
            square[found++] = (candidate * candidate);
            candidate += 2;
            index = 1;
        }
    }

    /**
     * Computes the value (this % modulus) where modulus is interpreteted as
     * an unsigned value
     */
    private short modShort(final short modulus) { //NOPMD short type
        final int modInt = (modulus & 0xFFFF);
        int accum = 0;
        final int factor = (int) (0x100000000L % modInt);//2^32 mod modInt
        int offset = used;

        while (offset-- > 0) {
            final int delta = (int) ((digit[offset] & MASK) % modInt);
            final int slide = (int) (((accum * factor) & MASK) % modInt);
            accum = delta + slide;
        }
        return (short) ((accum & MASK) % modInt); //NOPMD short type
    }

    /**
     * Sets this value to be the hcf of the two input values
     */
    public void hcf(final Bignum left, final Bignum right) {
        final Bignum temp_left = new Bignum();
        final Bignum temp_right = new Bignum();
        Bignum swap, large, small;
        
        temp_left.copy(left);
        temp_right.copy(right);
        //{
            if (left.isGreaterThan(right)) {
                large = temp_left;
                small = temp_right;
            } else {
                small = temp_left;
                large = temp_right;
            }

            while (small.used > 0) {
                large.remainder(small, false);
                swap = large;
                large = small;
                small = swap;
            }
            copy(large); 
        //}
        temp_left.clear();
        temp_right.clear();
    }

    /**
     * returns true if this value could be a prime
     * Could improve prime sieving as per PGP 2.3 by storing remainders
     */
    public boolean sieve() {
        int offset = 0;

        if (primes[0] == 0) {
            calculatePrimes();
        } /* executed first time only */
        while (offset < NPRIMES) {
            if (modShort(primes[offset++]) == 0) {
                /* candidate is a multiple of a known small prime number;
                 ** to be properly general we test if the candidate IS that prime number */
                return (used == 1 &&
                        digit[0] == primes[--offset]);
            }
        }
        return true;
    }

    /**
     * Reduces the used value to drop leading zeroes
     */
    private void normalise() {
        while (used > 0 &&
                digit[used - 1] == 0) {
            used--;
        }
    }
    private static final char[] HEX_DIGIT = {'0', '1', '2', '3', '4', '5', '6',
        '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };
    private static final String HEX_STRING = "0123456789ABCDEF";

    private static StringBuffer formatNumber(final int[] number, final int length) {
        return formatNumber(number, length, 0);
    }

    private static StringBuffer formatNumber(
            final int[] number, final int length, final int base) {
        final StringBuffer sbuf = new StringBuffer();
        for (int i = 0; i < length; ++i) {
            sbuf.append("00000000");
        }
        int ptr = 8 * length - 1;
        int index = -1;
        int count;
        int value;

        while (++index < length) {
            value = number[index + base];
            count = 8;
            while (count-- > 0) {
                sbuf.setCharAt(ptr--, HEX_DIGIT[(value & 0xF)]);
                value = (value >>> 4);
            }
        }
        ptr = 0;
        while(ptr < sbuf.length() && sbuf.charAt(ptr) == '0') {ptr++;}
        if (ptr > 0) {
            sbuf.reverse();
            sbuf.setLength(sbuf.length() - ptr);
            sbuf.reverse();
        }
        return sbuf;
    }

    /**
     * Returns as a StringBuffer, the hex expansion of the number
     */
    public StringBuffer format() {
        return formatNumber(digit, used);
    }

    /**
     * Returns as a String, the hex expansion of the number
     */
    public String toString() {
        return format().toString();
    }

    // write the number to the log
    /*
    private void outputNumber(final String name) throws java.io.IOException {
        if (file != null) {
            file.write(name + ": #x" + toString() + System.getProperty("line.separator"));
        }
    }
     */

    // allocate space for the number in bytes
    private void setSize(final int size, final boolean preserve) {
        final int oldbytes = null == digit ? 0 : digit.length;
        final int newbytes = size + 1; // allow for leading zero value

        if (oldbytes < newbytes) {
            // allocate the new space -- expect exception on failure
            int[] temp = new int[newbytes];
            for (int i = 0; i < temp.length; ++i) {
                temp[i] = 0;
            }

            // copy values across if required
            if (preserve && used > 0 && digit != null) {
                System.arraycopy(digit, 0, temp, 0, digit.length);
            }
            // clear old array, and swap in new one
            for (int j = 0; digit != null && j < digit.length; ++j) {
                digit[j] = 0;
            }
            digit = temp;
        }
        used = size; // not normalised
    }

    /**
     * initialises a Bignum to an int value (taken as unsigned)
     */
    public void setValue(final int value) {
        setSize(1, false); // dispose of old

        digit[0] = value;
        digit[1] = 0;
        if (value == 0) {
            used = 0;
        }
    }

    /**
     * Interprets the hex string as a Bignum
     */
    public boolean readHexString(final String input) {
        final int charLen = input.length();
        final int wordLen = (charLen + 8 - 1) / 8; // 8 nybbles per unit
        int ptr = charLen;
        int factor = 1;
        int xdigit;
        int index = 0;

        // allocate space
        setSize(wordLen, false);

        while (ptr-- > 0) {
            if (factor == 1) {
                digit[index] = 0;
            }
            // peel reso and interpret the next character, aborting
            // if not recognised
            xdigit = HEX_STRING.indexOf(
                    Character.toUpperCase(
                    input.charAt(ptr)));
            if (xdigit < 0) {
                return false;
            }

            digit[index] += factor * xdigit;
            factor = factor * 16;
            if (factor == 0) // cunning shift trick
            {
                index++;
                factor = 1;
            }
        }
        digit[wordLen] = 0;
        used = wordLen;
        normalise();
        return true;
    }

    /**
     * Zeroes the array and detaches it, restoring to newly allocated state
     */
    public void clear() {
        if (digit != null) {
            for (int i = 0; i < digit.length; ++i) {
                digit[i] = 0;
            }
        }
        digit = null;
        used = 0;
    }

    /**
     * Interprets the byte[] as a Bignum
     */
    public void readByteArray(final byte[] external) { //was get_mpn
        int bytecount = external.length;
        final int wordcount = (bytecount + 3) / 4; // 4 bytes per unit

        //	char		debug[1000];

        setSize(wordcount, false);
        int word = wordcount - 1;
        int ptr = 0;
        used = wordcount;

        for (int i = 0; i < digit.length; ++i) {
            digit[i] = 0;
        }
        while (bytecount > 0) {
            digit[word] = digit[word] * 256 + (external[ptr++] & 0xFF);
            if ((--bytecount % 4) == 0) {
                word--;
            }
        }
        //	formatMPNumber(debug, result);
    }
    private static final int[] LOOKUP = {0, 1, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4};

    // where is the top bit?
    public static int topBit(final int value) {
        int guess = 16;
        int incrm = (guess / 2);

        stats[4]++;
        while (incrm >= 2) {
            if ((value & 0xFFFFFFFFL) >= (1 << guess)) {
                guess += incrm;
            } else {
                guess -= incrm;
            }
            incrm /= 2;
        }
        guess -= 2;
        return (LOOKUP[(value >>> guess)] + guess);
    }


    /**
     * returns the bit-length of the Bignum
     */
    public int bitLength() { //length_mpn
        stats[13]++;
        if (used > 0) {
            return ((used - 1) * UNITBITS + topBit(digit[used - 1]));
        } else {
            return 0;
        }
    }

    /**
     * expands the value as a byte array
     */
    public byte[] toByteArray() { // put _ mpn
        final int numBits = bitLength();
        final int bytecount = ((numBits + 7) / 8);
        int word = 0;
        int value = digit[word++];

        byte[] external = new byte[bytecount];

        int ptr = bytecount;
        int count = 4;

        while (ptr > 0) {
            external[--ptr] = (byte) (value & 0xFF);
            value = value >>> 8;
            if (--count == 0) {
                value = digit[word++];
                count = 4;
            }
        }
        return external;
    }

    /**
     * returns number of bytes in this Bignum
     */
    public int getSize() {  //size _ mpn
        return ((bitLength() + 7) / 8);
    }

    /**
     *  multiplies a multiple precision number by a single precision value.
     * the result and the multiple precision operand may be the same location
     * length is the nominal length, the result must be at least one length
     */
    private static void smMultAdd(
            final int[] result, final int reso,
            final int[] multi, final int moff,
            final int scalar, final int length) {
        long carry = 0;
        int index = 0;

        stats[0]++;
        /* Is basically practical => proceed */
        while (index < length) {
            final long product = multUnitxUnit(scalar, multi[index + moff]) + (result[reso + index] & MASK) + carry;
            result[reso + index++] = (int) (product & MASK);
            carry = product >>> 32;
        }
        final long temp = (result[reso + index] & MASK) + carry;
        result[reso + index] = (int) (temp & MASK);
        if ((temp >>> 32) != 0) {
            result[reso + index + 1]++;
        }
    }

    /**
     * multiplies a multiple precision number by a single precision value
     * the result and the multiple precision operand may be the same location
     * length is the nominal length, the result must be at least one length
     */
    private static void smMultSub(
            final int[] result, final int reso,
            final int[] multi, final int moff,
            final int scalar, final int length) {
        long borrow = 0;
        int index = 0;

        stats[1]++;
        /* Is basically practical => proceed */
        while (index < length) {
            long product = multUnitxUnit(scalar, multi[moff + index]) + borrow;
            borrow = product >>> 32;
            product &= MASK;
            if (product > (result[reso + index] & MASK)) {
                ++borrow;
            }
            result[reso + index] -= (int) product;
            ++index; // rather than [reso + index++] which caused J# to double increment index
        }
        //assert(result[(size_t)index] >= borrow);
        result[reso + index] -= (int) (borrow & MASK);
    }

    private static java.math.BigInteger toBigIntegerImpl(
            final int[] value, final int offset,
            final int length) {
            byte[] tmp = new byte[4 * length];
            for (int i = 0; i < length; ++i) {
                final int k = (length - i) - 1; //NOPMD name
                tmp[4 * k + 0] = (byte) ((value[offset + i] >>> 24) & 0xFF);
                tmp[4 * k + 1] = (byte) ((value[offset + i] >>> 16) & 0xFF);
                tmp[4 * k + 2] = (byte) ((value[offset + i] >>> 8) & 0xFF);
                tmp[4 * k + 3] = (byte) ((value[offset + i]) & 0xFF);
            }
            return new java.math.BigInteger(1, tmp);
    }
    
    public java.math.BigInteger toBigInteger()
    {
        return toBigIntegerImpl(this.digit, 0, this.used);
    }

    /**
     * subtracts one multiple precision number from another, any or all of
     * the arguments may be the same location; returns true if there is overall
     * borrow
     */
    private static boolean mmSub(
            final int[] result, final int reso,
            final int[] minus, final int moff,
            final int length) {
        long difference;
        long positive;
        int index = 0;
//System.out.println("value: "+formatNumber(result, length, reso));

        stats[3]++;
        while (true) {
            /* no borrow sub-loop */
            do {
                positive = MASK & result[index + reso];
                difference = positive - (MASK & minus[index + moff]);
                difference &= MASK;
                result[index + reso] = (int) (difference);
                index++;
                if (index >= length) {
                    return (difference > positive);
                }
            } while (difference <= positive);

            /* borrow sub-loop */
            do {
                positive = MASK & result[index + reso];
                difference = (positive - 1) - (MASK & minus[index + moff]);
                difference &= MASK;
                result[index + reso] = (int) (difference);
                index++;
                if (index >= length) {
                    return (difference >= positive);
                }
            } while (difference >= positive);
        }
    }

    /**
     * which of the two fragments is the larger?
     */
    private static boolean greaterThan( //mp _ gt
            final int[] left,  final int loff,
            final int[] right, final int roff,
                        int offset) {
        stats[6]++;
        while (offset-- > 0) {
            if (left[offset + loff] != right[offset + roff]) {
                return (MASK & left[offset + loff]) > (MASK & right[offset + roff]);
            }
        }
        return false;
    }

    /**
     * basic modular operation
     */
    private static int smMod(
            final int[] operand, final int off,
            final int[] modulus, final int moff,
            final ModulusCache modSummary) {
        final int length = modSummary.length;
        final int shift = modSummary.shift;
        final int topUnit = modSummary.topUnit;
        int scalar;
        boolean first = true;
        long firstUnit;
        int secondUnit;

        if (shift != 0) { //NOPMD
            firstUnit = (operand[off + length] << shift) + (operand[off + length - 1] >>> (UNITBITS - shift));
            secondUnit = (operand[off + length - 1] << shift);

            if (length > 1) {
                secondUnit += (operand[off + length - 2] >>> (UNITBITS - shift));
            }
        } else {	/* This is code path is not merely for efficency.  Some machines
             ** execute shifts by UNITBITS as no-ops rather than set to zero.
             ** e.g. DEC Alpha			*/
            firstUnit = operand[off + length];
            secondUnit = operand[off + length - 1];
        }

        stats[7]++;
        /* The divisor for the approximate single unit factor is
         * (modSummary->topUnit + 1) The +1 could cause overflow.
         * This needs special case treatment. */
        if (topUnit == MAXUNIT) {
            scalar = (int) firstUnit; /* this division in this case is trivial */
        } else {
            final int divisor = topUnit + 1;
            //assert(firstUnit < topUnit); /* required for udiv_qrnnd */
            scalar = divUnitByHalf((firstUnit << 32) | (secondUnit & MASK),
                    divisor/*, null*/);
        }
        if ((scalar != 1) && (scalar != 0)) { //NOPMD ternary
            smMultSub(operand, off, modulus, moff, scalar, length);
        } else {
            scalar = 0;
        }

        while (!greaterThan(modulus, moff, operand, off, length + 1)) {
            stats[8]++;
            if (!first) {
                stats[9]++;
            }
            first = false;
            /*boolean ok =!*/ mmSub(operand, off, modulus, moff, length + 1);
            //assert(ok);
            scalar++;
        }
        //assert(operand[length] == 0);
        return scalar;
    }

    /**
     * modular multiply
     */
    private static void mmMultMod( //NOPMD loads of params
            final int[] result, final int reso,
            final int[] left, final int loff,
            final int[] right, final int roff,
            final int[] modulus, final int moff,
            final int[] work, final int woff, 
            final ModulusCache modSummary) {
        final int length = modSummary.length;
        int index = 0;
        int buffer = length + 2;
        stats[10]++;
        System.arraycopy(left, loff, work, buffer + woff, length + 2);
        //assert(buffer[length] == 0);
        //assert(buffer[length + 1] == 0);
        //assert(right[length] == 0);
        //assert(right[length + 1] == 0);
        for (int i = 0; i < length + 2; ++i) {
            result[i + reso] = 0;
            work[i + woff] = 0;
        }
        smMultAdd(result, reso, work, woff + buffer, right[roff + index], length);
        ++index;
        result[reso + length + 1] = 0;
        /*	smMod(result, modulus, modSummary);	*/
        while (index < length) {
            buffer--;	/* effectively multiplication by 0x100000000  */
            smMod(work, woff + buffer, modulus, moff, modSummary);
            smMultAdd(result, reso, work, woff + buffer, right[roff + index], length);
            index++;
        /*		smMod(result, modulus, modSummary); */
        }
        smMod(result, reso + 1, modulus, moff, modSummary);
        smMod(result, reso + 0, modulus, moff, modSummary);
    }

    /**
     * Copies this value into the buffer
     */
    private void setBuffer(final int[] buffer, final int offset, final int length) {
        int index = length;

        stats[11]++;
        //assert(number.used <= length);
        while (index > used) {
            buffer[--index + offset] = 0;
        }
        while (index-- > 0) {
            buffer[index + offset] = digit[index];
        }
    }

    /**
     * Is the indicated bit set in the array?
     */
    private static boolean isBitSet(final int[] number, final int bitNumber) {
        final int word = (bitNumber / UNITBITS);
        final int bit = (bitNumber % UNITBITS);

        stats[12]++;
        return (number[word] & (1 << bit)) != 0;
    }

    /**
     * set modulus info form this number
     */
    private void summarise(final ModulusCache summary) {
        final int length = used;
        final int modulusShift = (UNITBITS - topBit(digit[length - 1]));

        //assert(modulus->digit[length] == 0);
        summary.length = length;
        summary.shift = modulusShift;
        summary.topUnit = digit[length - 1] << modulusShift;
        if (length > 1 && modulusShift > 0) {
            summary.topUnit += digit[length - 2] >>> (UNITBITS - modulusShift);
        }
    }


    //boolean	divide(	bignump quotient, bignump remainder,
    //				bignum numerator, bignum denominator)
    /**
     * generate the results of dividing this number by the denominator
     */
    public void divide(final Bignum[] qAndR, final Bignum denominator) { // _mpn
        qAndR[0] = new Bignum();
        final ModulusCache modSummary = new ModulusCache();
        int offset = used - denominator.used;
        final int n_bytes = (used + 1);
        int[] result = new int[n_bytes];

        //assert(numerator->digit[numerator->used] == 0);
        System.arraycopy(digit, 0, result, 0, n_bytes);
        denominator.summarise(modSummary);

        final int d_length = modSummary.length;
        if (greaterThan(denominator.digit, 0, result, offset, d_length)) {
            offset--;
        }
        qAndR[0].setSize(offset + 1, false);
        if (qAndR.length > 1)
        {
            qAndR[1] = new Bignum();
            qAndR[1].setSize(d_length, false);
        }
        qAndR[0].digit[offset + 1] = 0;

        while (offset >= 0) {
            qAndR[0].digit[offset] =
                    smMod(result, offset, denominator.digit, 0, modSummary);
            offset--;
        }
        qAndR[0].normalise();
        if (qAndR.length > 1) {            
            System.arraycopy(result, 0, qAndR[1].digit, 0, d_length + 1);
            qAndR[1].normalise();
        }
        for (int i = 0; i < n_bytes; ++i) {
            result[i] = 0;
        }
    }

    /**
     * simple modular remainder
     */
    public void remainder(final Bignum modulus, final boolean minus1) { //_mpn
        final ModulusCache modSummary = new ModulusCache();
        modulus.summarise(modSummary);
        final int length = modSummary.length;
        int offset = (used - modulus.used);

        //assert(result->alloc > result->used);
        //assert(result->digit[result->used] == 0);

        if (minus1) {
            modulus.digit[0]--;
        }
        if (greaterThan(modulus.digit, 0, digit, offset, length)) {
            offset--;
        }
        while (offset >= 0) {
            smMod(digit, offset--, modulus.digit, 0, modSummary);
        }
        normalise();
        if (minus1) {
            modulus.digit[0]++;
        }
    }

    /**
     * Set this value to the product of the two arguments
     */
    public void multiply(final Bignum left, final Bignum right) { // _mpn
        final int length = left.used + right.used;  /* could be 1 shorter */
        int offset = 0;

        setSize(length, false);
        //for(int index=0; index<length+1;++index) digit[index] = 0;

        while (offset < left.used) {
            smMultAdd(digit, offset, right.digit, 0,
                    left.digit[offset], right.used);
            offset++;
        }
        normalise();
    }

    /* This routine is not very well written, especially with respect to
     ** extending the result.  Consider a rewrite.  */
    public void add(final Bignum addend) { //_mpn //NOPMD complex
        boolean carry = false;
        int index = 0;
        int length;

        /* First determine the length of the sum */
        if (used > addend.used) {
            length = used;
            if (digit[used - 1] == MAXUNIT) {
                length++;
            }
        } else {
            length = addend.used;
            if (used < addend.used) {
                if (addend.digit[addend.used - 1] == MAXUNIT) {
                    length++;
                }
            } else {
                /* N.B. Assuming that the numbers are normalised neither top unit
                 **      can be zero. */
                if ((addend.digit[length] & MASK) <
                        ((addend.digit[length] + digit[length] + 1) & MASK)) {
                    length++;
                }
            }
        }
        setSize(length, true);

        while (index < addend.used) {
            carry = (carry && ++digit[index] == 0);
            digit[index] += addend.digit[index];
            if ((digit[index] & MASK) <
                    (addend.digit[index] & MASK)) {
                carry = true;
            }
            index++;
        }

        /* propagate carry as far as necessary */
        if (carry) {
            while (++digit[index++] == 0) {// NOPMD empty                
            }
        }

        //assert(index <= (*result)->alloc);
        normalise();
    }

    /**
     * take the input value from this
     */
    public void subtract(final Bignum minus) { //_mpn
        int offset = minus.used;

        //assert(used >= minus->used);
        if (mmSub(digit, 0, minus.digit, 0, minus.used)) {
            /* overall borrow => propagate it */
            while (digit[offset++]-- == 0) {// NOPMD empty
            }
        }
        //assert(offset <= result->used + 1);
        normalise();
    }

    /**
     * set this value to a copy of the input
     */
    public void copy(final Bignum input) { //_mpn
        final int length = input.used;

        setSize(length, false);
        //for(int index=0; index<digit.length;++index) digit[index] = 0;

        System.arraycopy(input.digit, 0, digit, 0, length);
        //digit[length] = 0;
    }

    /**
     * Is this the larger?
     */
    public boolean isGreaterThan(final Bignum right) { //gt_mpn
        //if(!left)
        //return FALSE;
        //else if(!right)
        //return TRUE;
        //else
        if (used != right.used) { //NOPMD ternary
            return (used > right.used);
        } else {
            return greaterThan(digit, 0, right.digit, 0, used);
        }
    }

    /**
     * Are these equal?
     */
    public boolean isEqual(final Bignum right) { //eq_mpn
        if (/*!left || !right || left->*/used != right.used) { //NOPMD
            return false;
        } else {
            int offset = used;
            while (offset-- > 0) {
                if (digit[offset] != right.digit[offset]) {
                    return false;
                }
            }
            return true;
        }
    }

    //#define SWAP(a, b) { temp = a; a = b; b = temp; }
    /**
     * sets this = (multi**expon)%modulus
     */
    public void modPower( //_mpn //NOPMD complex
            final Bignum multi, final Bignum expon,
            final Bignum modulus, final Monitor watcher) 
            throws InterruptedException
    {
        final int length = modulus.used;
        final ModulusCache modSummary = new ModulusCache();
        int exponBits;
        int exponBit = 0;
        
        int[] workSpace = new int[5 * (length + 2)];

        int power2 = 0;//workSpace;
        int accum = power2 + length + 2;
        int intermed = accum + length + 2;
        final int work = intermed + length + 2;
        // int temp;
/*	testTopBit(); */

        int swap;
        for (int i = 0; i < stats.length; ++i) {
            stats[i] = 0;
        }
        modulus.summarise(modSummary);
        multi.setBuffer(workSpace, power2, length + 1);
        exponBits = expon.bitLength();
        if (isBitSet(expon.digit, 0)) {
            System.arraycopy(workSpace, power2, workSpace, accum, length + 1);
        } else {	/* Even exponent; this isn't really expected (for RSA at least) */
            for (int i = 0; i < length + 1; ++i) {
                workSpace[accum + i] = 0;
            }
            workSpace[accum + 0] = 1;
        }
        while (++exponBit < exponBits) {
            mmMultMod(workSpace, intermed,
                    workSpace, power2,
                    workSpace, power2,
                    modulus.digit, 0,
                    workSpace, work, modSummary);
            //SWAP(intermed, power2);
            swap = intermed;
            intermed = power2;
            power2 = swap;
            /*		formatNumber(debug, power2, length); */
            if (isBitSet(expon.digit, exponBit)) {
                mmMultMod(workSpace, intermed,
                        workSpace, power2,
                        workSpace, accum,
                        modulus.digit, 0,
                        workSpace, work, modSummary);
                //SWAP(intermed, accum);
                swap = intermed;
                intermed = accum;
                accum = swap;
            }
            if (exponBit % 8 == 0) {
                watcher.userBreak();
            }
        }
        //{
            setSize(length, false);
            System.arraycopy(workSpace, accum, digit, 0, length);
            digit[length] = 0;
            normalise();
        //}
        for (int i = 0; i < workSpace.length; ++i) {
            workSpace[i] = 0;
        }
    }

    /* routines developed to support elliptic curve encryption */
    /* The operations are abstracted from what are used in George Barwood's
    public domain Pegwit PKE system, version 8.1
     */
    /**
     * returns true if bitnumth bit is set
     */
    public boolean isNthBitSet(final int bitnum) {  //_mpn
        if (bitnum >= used * UNITBITS) {
            return false;
        } else if (bitnum < 0) {
            return false;
        }
        return isBitSet(digit, bitnum);
    }

    /**
     * makes lowest bit equal the boolean value 0 or 1
     */
    public void set0thBit(final boolean bit) { //_mpn
        if (bit) {
            digit[0] |= 1;
        } else {
            digit[0] &= (MAXUNIT - 1);
        }
    }

    /**
     * pack the lowest bitsUsed bits from datalen short's into a bignum
     * with a number of clear bits below this.  implicitly init_mpn()sink value
     */
    public void pack16(
            final short[] data, final int datalen, //NOPMD short type
            final byte bitsUsed, 
            final byte freeLowBits) {
        final int numbits = (bitsUsed * datalen) + freeLowBits;
        final int numunits = ((numbits + UNITBITS - 1) / UNITBITS);
        final int mask = 0xFFFF & ((1 << bitsUsed) - 1); //NOPMD short type

        /* Allocate space implicitly clearing*/
        setSize(numunits, false);

        /* Copy data across */
        for (int i = 0, ptr = 0, basebits = freeLowBits;
                i < datalen;
                i++, basebits += bitsUsed) {
            int nextBit =  (basebits + bitsUsed); 
            /* coerce to make long enough in 16 bit case! */
            digit[ptr] |= (data[i] & mask) << basebits;

            if (nextBit > UNITBITS) /* have to carry */ {
                /* step along one */
                ++ptr;
                basebits -= UNITBITS;
                nextBit -= UNITBITS;

                digit[ptr] |= ((data[i] & mask) >>>
                        (bitsUsed - nextBit));
            }
        }
        /* allow for empty top bits */
        normalise();
    }

    /**
     * the converse operation - fails if datalen is too small
     */
    public boolean unpack16( //_mpn
            final short[] data, final int[] datalen, //NOPMD short type
            final byte bitsUsed, final byte freeLowBits) {
        final int bitsValid = bitLength() - freeLowBits;
        final int needed = ((bitsValid + bitsUsed - 1) / bitsUsed);
        final int mask = 0xFFFF & ((1 << bitsUsed) - 1);


        if (needed > datalen[0]) {
            return false;
        }
        if (needed < datalen[0]) /* ensure unwanted stuff is false */ {
            for (int i = needed; i < datalen[0]; i++) {
                data[i] = 0;
            }
        }
        for (int i = 0, ptr = 0, basebits = freeLowBits;
                i < needed;
                i++, basebits += bitsUsed) {
            int nextBit = (basebits + bitsUsed);
            data[i] = (short) (((digit[ptr]) >> basebits) & mask); //NOPMD short

            if (nextBit > UNITBITS) {
                /* step along one */
                ++ptr;
                basebits -=  UNITBITS;
                nextBit -= UNITBITS;
                data[i] |= (short) (((digit[ptr]) & ((1 << nextBit) - 1)) << (bitsUsed - nextBit)); //NOPMD short
            }
        }
        datalen[0] = needed;
        return true;
    }

    /* special case operation */
    public void triple(final Bignum input) { //_mpn
        /* compute size needed for result */
        int wordlen = input.used;

        if (input.digit[input.used - 1] >= (MAXUNIT / 3)) {
            wordlen++;
        }

        setSize(wordlen, false);
        digit[used - 1] = 0;

        int carry = 0;
        int index = 0;
        for (; index < input.used; index++) {
            /* probably overkill, but at least I'modInt sure it catches
            all cases of carry for all sizes of unit */
            final long product = multUnitxUnit(3, input.digit[index]) + carry;
            digit[index] = (int) (product & MASK);
            carry = (int) ((product >>> 32) & MASK);
        }
        if (carry != 0) {
            digit[index] = carry;
        }

        normalise();
    }
    
    private static final Bignum PRIME_ORDER_VALUE;

    static {
        final Bignum prime = new Bignum();
        final int[] digit = {0x42bbcd31, 0x5e0d2584, 0x4bf72d8b,
            0x0547840e, 0xed9bbec3, 0x2314691c,
            0xd85081b8, 0x0001026d, 0x00000000
        };
        prime.digit = digit;
        prime.used = 8;
        PRIME_ORDER_VALUE = prime;
    }

    public void getPrimeOrder() {
        copy(PRIME_ORDER_VALUE);
    }

    //private static final int FERMATLIMIT = 10;
    public static byte mixedRandom(final Random rnd) {
        byte result = 0;
        while (result == 0 || result == 0xFF) {
            result = rnd.randombyte();
        }
        return result;
    }

    private static int randomShort(final Random rnd) {
        return (((mixedRandom(rnd) & 0xFF) << 8) | (mixedRandom(rnd) & 0xFF));
    }

    public boolean areMutuallyPrime(final Bignum right) {
        final Bignum hcf = new Bignum(), one = new Bignum();
        boolean result;
        one.setValue(1);
                
        hcf.hcf(this, right);
        result = !hcf.isGreaterThan(one);
        hcf.clear();
        one.clear();
        return result;
    }

    /*
    public void modularInverse( //NOPMD complex
            final Bignum input, final Bignum modulus, final Monitor watcher) throws InterruptedException
    {
        final Bignum remainder = new Bignum(input);
        final Bignum target = new Bignum(0);
        final Bignum trial = new Bignum(modulus);
        final Bignum temp = new Bignum();
        final Bignum one = new Bignum(1);
        final Bignum[] factor = new Bignum[1];
        setValue(1);


        do {
            boolean mutuallyPrime = false;
            do {
                target.add(modulus);
                System.out.println(target.toString());
                
                // (k*modulus)/number to invert (on first loop)
                target.divide(factor, remainder);
                factor[0].add(one);
                System.out.println(factor[0].toString());
                
                // round up to next multiple of the number 
                trial.multiply(factor[0], remainder);
                trial.remainder(modulus, false);
                watcher.userBreak();
                mutuallyPrime = trial.areMutuallyPrime(modulus);
                
                System.out.println(trial.toString());
                System.out.println(mutuallyPrime);
            } while (!remainder.isGreaterThan(trial) ||
                    !mutuallyPrime);

            System.out.println("---------------------");
            temp.multiply(factor[0], this);
            temp.remainder(modulus, false);
            this.copy(temp);
            remainder.copy(trial);
            target.setValue(0);
            System.out.println(remainder.toString());
            System.out.println("======================");

        } while (remainder.isGreaterThan(one));

        remainder.clear();
        target.clear();
        factor[0].clear();
        trial.clear();
        one.clear();
        temp.clear();
    }
     */
    
    private static class Signum
    {
        public Bignum number;
        public boolean positive;
        Signum(final Bignum num, final boolean sign)
        {
            number = num;
            positive = sign;
        }
    }
    //http://en.wikipedia.org/wiki/Extended_Euclidean_algorithm
    public void modularInverse( 
            final Bignum input, final Bignum modulus) 
    {
        final Signum[] partial = extendedGCD(input, modulus);
        partial[1].number.clear();
        if(partial[0].positive)
        {
            this.copy(partial[0].number);
        }
        else
        {
            final Bignum want = partial[0].number;
            while(want.isGreaterThan(modulus))
            {
                want.subtract(modulus);
            }
            this.copy(modulus);
            this.subtract(want);
        }
        partial[0].number.clear();            
    }
    
    
    private static Signum[] extendedGCD(final Bignum a, final Bignum b) //NOPMD
    {
        final Bignum temp = new Bignum(a);
        temp.remainder(b, false);
        final Bignum zero = new Bignum(0);
        Signum[] result = new Signum[2];
        if(temp.isEqual(zero))
        {            result[0] = new Signum(zero, true);
            result[1] = new Signum(new Bignum(1), true);
            return result;
        }
        //{x, y} := extended_gcd(b, a mod b)
        final Signum[] partial = extendedGCD(b, temp);

        //return {y, x-y*(a div b)}
        result[0] = partial[1];
        result[1] = new Signum(partial[0].number, true);
        
        Bignum[] out = new Bignum[1];

        if(a.isGreaterThan(b) || a.isEqual(b))
        {
            a.divide(out, b);
        }
        else 
        {
            out[0] = new Bignum(0);
        }
        final Bignum y = new Bignum(); //NOPMD
        y.multiply(partial[1].number, out[0]);
        out[0].clear();
        
        if(partial[0].positive)
        {
            if(!partial[1].positive) //NOPMD
            {
                result[1].number.add(y);
                result[1].positive = true;
                y.clear();
            }
            else if(result[1].number.isGreaterThan(y))
            {
                result[1].number.subtract(y);
                result[1].positive = true;
                y.clear();
            }
            else
            {
                y.subtract(result[1].number);
                result[1].number.clear();
                result[1].number = y;
                result[1].positive = false;                
            }
        }
        else
        {
            if(partial[1].positive)
            {
                result[1].number.add(y);
                y.clear();
                result[1].positive = false;
            }
            else if(result[1].number.isGreaterThan(y))
            {
                result[1].number.subtract(y);
                y.clear();
                result[1].positive = false;                
            }
            else
            {
                y.subtract(result[1].number);
                result[1].number.clear();
                result[1].number = y;
                result[1].positive = true;                
            }
        }
        return result;
    }
    
    

    public boolean longRandom(final int length, int topBits, final Random rnd) {
        final int Nbytes =  ((length + 7) / 8); /* bytes including length */
        final int bits =  ((length - 1) % 8); /* one less number of bits in top byte */
        final byte mask = (byte) (0xFF >>> (7 - bits)); /* mask for random data in third data byte */
        int offset = 2;

        if (topBits == 0) {
            topBits =  (0x8000 | randomShort(rnd));
        } /* treat topBits==0 as "don't care" */
        if (0 == (topBits & 0x8000) || length < 24) {
            return false;
        }
        byte[] buffer = new byte[Nbytes];

        buffer[0] = (byte) (topBits >>> (15 - bits));
        buffer[1] = (byte) ((topBits >>> (7 - bits)) & 0xFF);
        buffer[2] = (byte) (((topBits << (bits + 1)) & 0xFF) | (rnd.randombyte() & mask));
        while (offset++ < Nbytes - 1) {
            buffer[offset] = rnd.randombyte();
        }

        readByteArray(buffer);
        for (int i = 0; i < Nbytes; ++i) {
            buffer[i] = 0;
        }
        return true;
    }

    public static boolean arePrimes(final Bignum[] candidates, //NOPMD complex
            final Monitor watcher) throws InterruptedException {
        int offset = 0;
        boolean answer = true;
        final Bignum result = new Bignum();
        final Bignum factor = new Bignum();
        int number = 0;
        final int[] seedPrimes = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29};

        while (number < candidates.length) {
            if (!candidates[number++].sieve()) {
                return false;
            }
        }
        while (answer && offset < seedPrimes.length) {
            factor.setValue(seedPrimes[offset]);
            number = 0;
            while (answer && number < candidates.length) {
                /* Test p for primality by applying Fermat's theorem:
                For any x, if ((x**(p-1)) mod p) != 1, then p is not prime.
                We actually test (x**p) mod p against x
                 */
                result.modPower(factor,
                        candidates[number], candidates[number], watcher);
                if (!result.isEqual(factor)) {
                    answer = false;
                }
                number++;
            }
            offset++;
        }
        result.clear();
        factor.clear();
        return answer;
    }

    /* Strictly finds 2N+1 where N is a Sophie Germain prime */
    private boolean nextSophieGermainPrime(final int incrementRequested, //NOPMD complex
            final Monitor watcher) throws InterruptedException {
        long increment = incrementRequested & MASK;
        final Bignum[] candidates = {new Bignum(), new Bignum()};
        final Bignum number = candidates[0];
        final Bignum[] half = {candidates[1]};

        final Bignum temp = new Bignum();
        final Bignum incr = new Bignum();
        final Bignum halfincr = new Bignum();
        final Bignum one = new Bignum();
        boolean errCode = true;

        /* As the result of this routine has to equal 11 modulo 12,
         ** the initial set-up establishes
         ** starting values with the following properties:-
         ** number = 11 [mod 12]
         ** half = (number - 1)/2
         ** increment = 0 [mod 12]
         ** halfincr  = increment / 2
         ** number & increment are mutually prime
         ** half & halfincr are mutually prime
         ** */

        /* first set up number to be 11 modulo 12 by adding the necessary
         ** smaller integer and set up a few simple values */
        number.copy(this);
        temp.setValue((11 - number.modShort((short)12)));  //NOPMD short
        number.add(temp);
        one.setValue(1);
        temp.setValue(2);
        number.divide(half, temp);

        /* adjust increment upwards to be the next multiple of 12 */
        if (increment % 12 != 0) {
            increment = ((increment / 12) + 1) * 12;
        }

        /* Predecrement increment as we increment it at the start of the loop;
         ** this is probably unimportant in most cases, but just in case the caller
         ** had a particular reason for wanting the particular increment value
         ** provided.  */
        increment -= 12;

        /* This nested loop tries potential values of increment until
         * one meeting all criteria is encountered. */
        do {
            do {
                increment += 12;
                if (increment > MASK) {
                    errCode = false;
                } /* Overflow check */
                incr.setValue((int) increment);
                temp.hcf(incr, number);
            } while (errCode && temp.isGreaterThan(one));
            halfincr.setValue((int) (increment / 2));
            temp.hcf(halfincr, half[0]);
        } while (errCode && temp.isGreaterThan(one));
        one.clear();
        temp.clear();

        /* ready to search for candidates */
        while (errCode && !arePrimes(candidates, watcher)) {
            watcher.userBreak();
            number.add(incr);
            half[0].add(halfincr);
        }
        half[0].clear();
        halfincr.clear();
        incr.clear();

        if (errCode) {
            copy(number);
        }
        number.clear();
        return errCode;
    }

    private boolean nextPrime(final int increment_requested, 
            final Monitor watcher) throws InterruptedException { //NOPMD complex
        long increment = increment_requested & MASK;
        final Bignum[] candidates = {new Bignum()};
        final Bignum number = candidates[0];
        final Bignum temp = new Bignum();
        final Bignum incr = new Bignum();
        final Bignum one = new Bignum();
        boolean errCode = true;

        /* The initial set-up that follows establishes
         ** starting values with the following properties:-
         ** number is odd
         ** increment is even
         ** number & increment are mutually prime
         ** */

        /* first set up number to be odd by adding one if necessary */
        number.copy(this);
        one.setValue(1);

        if (number.modShort((short) 2) != 1) { //NOPMD short
            number.add(one);
        }

        /* adjust increment upwards to be the next multiple of 2 */
        if (increment % 2 != 0) {
            increment++;
        }

        /* Predecrement increment as we increment it at the start of the loop;
         ** this is probably unimportant in most cases, but just in case the caller
         ** had a particular reason for wanting the particular increment value
         ** provided.  */
        increment -= 2;

        /* This nested loop tries potential values of increment until one meeting
         ** all criteria is encountered. */
        do {
            increment += 2;
            if (increment > MASK) {
                errCode = false;
            } /* Overflow check */
            incr.setValue((int) increment);
            temp.hcf(incr, number);
        } while (errCode && temp.isGreaterThan(one));
        one.clear();
        temp.clear();

        /* ready to search for candidates */
        while (errCode && !arePrimes(candidates, watcher)) {
            watcher.userBreak();
            number.add(incr);
        }
        incr.clear();

        if (errCode) {
            copy(number);
        }
        number.clear();
        return errCode;
    }

    private boolean areMutuallyPrimeM1(final Bignum right) {
        set0thBit(false);
        final boolean result = areMutuallyPrime(right);
        set0thBit(true);
        return result;
    }
    public static final int SIMPLE_SCAN = 0;
    public static final int JUMP_SCAN = 1;
    public static final int SOPHIE_GERMAIN = 2;

    /* find a prime of length 'length' with most significant bits defined
     *  by topBits using method 'method' where N-1 is mutually prime with
    'mutual' */
    public boolean findPrime( //NOPMD complex
            final short length, final short topBits, //NOPMD short
            final int method,
            final Bignum mutual, final Random rnd, final Monitor watcher)
     throws InterruptedException{
        boolean result = true;

        if (rnd.randload((short) (length + 32)) < 0) { //NOPMD short
            return false;
        }

        int increment = (rnd.randombyte() & 0xFF) +
                ((rnd.randombyte() & 0xFF) * 0x100) +
                ((rnd.randombyte() & 0xFF) * 0x10000) +
                ((rnd.randombyte() & 0x7F) * 0x1000000);
        if (rnd.randload(length) > 0 &&
                longRandom(length, topBits, rnd)) {
            switch (method) {
                case SIMPLE_SCAN:
                    increment = 1;
                /*deliberate drop through to next case */
                case JUMP_SCAN:
                     //{
                        final Bignum two = new Bignum();
                        two.setValue(2);
                        do {
                            while (!areMutuallyPrimeM1(mutual)) {
                                add(two);
                            }
                            result = nextPrime(increment, watcher);
                        } while (result && !areMutuallyPrimeM1(mutual));
                        two.clear();
                    //}
                    break;

                case SOPHIE_GERMAIN:
                    result = nextSophieGermainPrime(increment, watcher);
                    break;

                default:
                    result = false;
            }
        }
        return result;
    }
}
