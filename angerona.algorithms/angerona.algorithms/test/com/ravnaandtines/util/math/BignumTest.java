/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ravnaandtines.util.math;

import junit.framework.TestCase;

/**
 *
 * @author Steve
 */
public class BignumTest extends TestCase {

    public BignumTest(final String testName) {
        super(testName);
    }

    /*
    require 'test/unit'
    class TestBignum < Test::Unit::TestCase
    def fact(n)
    return 1 if n == 0
    f = 1
    while n>0
    f *= n
    n -= 1
    end
    return f
    end
    def test_bignum
    $x = fact(40)
    assert_equal($x, $x)
    assert_equal($x, fact(40))
    assert($x < $x+2)
    assert($x > $x-2)
    assert_equal(815915283247897734345611269596115894272000000000, $x)
    assert_not_equal(815915283247897734345611269596115894272000000001, $x)
    assert_equal(815915283247897734345611269596115894272000000001, $x+1)
    assert_equal(335367096786357081410764800000, $x/fact(20))
    $x = -$x
    assert_equal(-815915283247897734345611269596115894272000000000, $x)
    assert_equal(2-(2**32), -(2**32-2))
    assert_equal(2**32 - 5, (2**32-3)-2)
    for i in 1000..1014
    assert_equal(2 ** i, 1 << i)
    end
    n1 = 1 << 1000
    for i in 1000..1014
    assert_equal(n1, 1 << i)
    n1 *= 2
    end
    n2=n1
    for i in 1..10
    n1 = n1 / 2
    n2 = n2 >> 1
    assert_equal(n1, n2)
    end
    for i in 4000..4096
    n1 = 1 << i;
    assert_equal(n1-1, (n1**2-1) / (n1+1))
    end
    end
    def test_calc
    b = 10**80
    a = b * 9 + 7
    assert_equal(7, a.modulo(b))
    assert_equal(-b + 7, a.modulo(-b))
    assert_equal(b + -7, (-a).modulo(b))
    assert_equal(-7, (-a).modulo(-b))
    assert_equal(7, a.remainder(b))
    assert_equal(7, a.remainder(-b))
    assert_equal(-7, (-a).remainder(b))
    assert_equal(-7, (-a).remainder(-b))
    assert_equal(10000000000000000000100000000000000000000, 10**40+10**20)
    assert_equal(100000000000000000000, 10**40/10**20)
    a = 677330545177305025495135714080
    b = 14269972710765292560
    assert_equal(0, a % b)
    assert_equal(0, -a % b)
    end
    def shift_test(a)
    b = a / (2 ** 32)
    c = a >> 32
    assert_equal(b, c)
    b = a * (2 ** 32)
    c = a << 32
    assert_equal(b, c)
    end
    def test_shift
    shift_test(-4518325415524767873)
    shift_test(-0xfffffffffffffffff)
    end
    end
     */

    private Bignum fact(int num)
    {
        Bignum result = new Bignum();
        result.setValue(1);  
        while (num>0)
        {
            final Bignum tmp = new Bignum(); //NOPMD
            tmp.setValue(num);
            final Bignum tmp2 = new Bignum(); //NOPMD
            tmp2.multiply(tmp, result);
            result = tmp2;
            --num;
        }
        return result;
    }
    
    public void testFact40()
    {
        final Bignum f40 = fact(40);
        assertTrue("selfEqual", f40.isEqual(f40));
        final Bignum f40bis = fact(40);
        assertTrue("sameEqual", f40.isEqual(f40bis));
        
        assertFalse("composite",f40.sieve());

        final Bignum two = new Bignum();
        two.setValue(2);
        f40bis.subtract(two);
        assertTrue("subtracted", f40.isGreaterThan(f40bis));
        assertFalse("subtracted 2", f40bis.isGreaterThan(f40));
        assertTrue("prime", two.sieve());
        
        final Bignum f40tre = fact(40);
        f40tre.add(two);
        assertFalse("added", f40.isGreaterThan(f40tre));
        assertTrue("added 2", f40tre.isGreaterThan(f40));
       
        final java.math.BigInteger big = 
                new java.math.BigInteger("815915283247897734345611269596115894272000000000");
        final String hex = big.toString(16);
        final Bignum f40qua = new Bignum();
        f40qua.readHexString(hex);
        assertTrue("literal Equal", f40.isEqual(f40qua));
        final String out = f40.toString().toLowerCase();
        assertEquals("hex form", hex, out);
        
        final Bignum f40p1 = fromDecimalString("815915283247897734345611269596115894272000000001");
        assertFalse("literal unEqual", f40.isEqual(f40p1));
        assertFalse("still composite",f40p1.sieve());
        
        final Bignum one = new Bignum();
        one.setValue(1);
        f40qua.add(one);
        assertTrue("literal Equal 2", f40p1.isEqual(f40qua));
        
        final Bignum f20 = fact(20);        
        final Bignum divi = fromDecimalString("335367096786357081410764800000");
        
        final Bignum[] qAndR = new Bignum[2];
        f40.divide(qAndR, f20);
        assertTrue("literal Equal 3", divi.isEqual(qAndR[0]));
        final Bignum zero = new Bignum();
        zero.setValue(0);
        assertTrue("literal Equal 4", zero.isEqual(qAndR[1]));
    }
    
    /*  Test routine to ensure that topBit always returns the correct answer **/
    public void testTopBit()
    {
        int result = 0;

        assertEquals("Zero", 0, Bignum.topBit(0));
        while (++result < Bignum.UNITBITS) {
            assertEquals("#" + result, result, 
                    Bignum.topBit((1 << result) - 1));
            assertEquals("#" + result, result,
                    Bignum.topBit(1 << (result - 1)));
        }
        assertEquals("MAXUNIT", Bignum.UNITBITS, Bignum.topBit(Bignum.MAXUNIT));
    }

    private static Bignum fromDecimalString(final String value)
    {
        final java.math.BigInteger big = 
                new java.math.BigInteger(value);
        final String hex = big.toString(16);
        final Bignum result = new Bignum();
        result.readHexString(hex);   
        return result;
    }
    
    public static final String[] primes = {
        // from http://primes.utm.edu/lists/small/small.html
        "671998030559713968361666935769",
        "282174488599599500573849980909",
        "521419622856657689423872613771",
        "362736035870515331128527330659",
        "115756986668303657898962467957",
        "590872612825179551336102196593",
        "564819669946735512444543556507",
        "513821217024129243948411056803",
        "416064700201658306196320137931",
        "280829369862134719390036617067",   
    };
    
    class Watch implements com.ravnaandtines.util.Monitor
    {

        public void userBreak() throws InterruptedException {
            //NOPMD
        }
    }

    /*
    private void extendedGCD(Bignum a, Bignum b, final Bignum[] out) //NOPMD names
    {
        Bignum x = new Bignum(0); //NOPMD names
        Bignum lastx = new Bignum(1);
        Bignum y = new Bignum(1); //NOPMD names
        Bignum lasty = new Bignum(0);
        boolean px = true;
        boolean py = true;
        boolean plx = true;
        boolean ply = true;
                
        final Bignum zero = new Bignum(0);
        final Bignum work = new Bignum(); //NOPMD loop
        
        while(!zero.isEqual(b))
        {
            System.out.println("===============================");
            System.out.println("a "+a.toString());
            System.out.println("b "+b.toString());
            System.out.println("-------------------------------");

            Bignum temp = b;
            a.divide(out, b);
            final Bignum quotient = out[0];
            b = out[1];
            a = temp;
            System.out.println("a "+a.toString());
            System.out.println("b "+b.toString());

            temp = new Bignum(); //NOPMD loop
            temp.copy(x);
            work.multiply(quotient, x);
            
            if(!px)
            {
                lastx.add(work);
                x = lastx;
                lastx = temp;
                plx = px;
                px = true;
            }
            else if(work.isGreaterThan(lastx))
            {
                work.subtract(lastx);
                x.copy(work);
                px = false;
            }
            lastx.subtract(work);
            x = lastx;
            lastx = temp;
        
            temp = new Bignum(); //NOPMD loop
            temp.copy(y);
            work.multiply(quotient, y);
            lasty.subtract(work);
            y = lasty;
            lasty = temp;
            
            System.out.println("x "+x.toString());
            System.out.println("y "+y.toString());
            
        }
        out[0] = lastx;
        out[1] = lasty;
        out[2] = a; 
    }

     */
    /*
    class Signum
    {
        public Bignum number;
        public boolean positive;
        Signum(Bignum num, boolean sign)
        {
            number = num;
            positive = sign;
        }
    }
    
    private static int depth = 0;
    private Signum[] extendedGCD(final Bignum a, final Bignum b)
    {
        ++depth;
        Bignum temp = new Bignum(a);
        temp.remainder(b, false);
        final Bignum zero = new Bignum(0);
        Signum[] result = new Signum[2];
        if(temp.isEqual(zero))
        {
            System.out.println("bottomed out "+depth);
            System.out.println(a.toString());
            System.out.println(b.toString());
            System.out.println("=======================");
            
            result[0] = new Signum(zero, true);
            result[1] = new Signum(new Bignum(1), true);
            --depth;
            return result;
        }
        System.out.println("recurse");
        //{x, y} := extended_gcd(b, a mod b)
        Signum[] partial = extendedGCD(b, temp);
        
         System.out.println("unwind "+depth);
         --depth;

        //return {y, x-y*(a div b)}
        result[0] = partial[1];
        result[1] = new Signum(partial[0].number, true);
        System.out.println(partial[0].number.toString());
        System.out.println(partial[1].number.toString());
        
        Bignum[] out = new Bignum[1];
        System.out.println(a.toString());
        System.out.println(b.toString());

        if(a.isGreaterThan(b) || a.isEqual(b))
        {
            a.divide(out, b);
        }
        else out[0] = new Bignum(0);
        Bignum y = new Bignum();
        y.multiply(partial[1].number, out[0]);
                
        if(partial[0].positive)
        {
            if(!partial[1].positive)
            {
                result[1].number.add(y);
                result[1].positive = true;
            }
            else if(result[1].number.isGreaterThan(y))
            {
                result[1].number.subtract(y);
                result[1].positive = true;                
            }
            else
            {
                y.subtract(result[1].number);
                result[1].number = y;
                result[1].positive = false;                
            }
        }
        else
        {
            if(partial[1].positive)
            {
                result[1].number.add(y);
                result[1].positive = false;
            }
            else if(result[1].number.isGreaterThan(y))
            {
                result[1].number.subtract(y);
                result[1].positive = false;                
            }
            else
            {
                y.subtract(result[1].number);
                result[1].number = y;
                result[1].positive = true;                
            }
        }
        System.out.println("=======================");
        return result;
    }
     */
    
    public void testPrimes() throws InterruptedException
    {
        final Bignum pr1 = fromDecimalString(primes[0]);
        final Bignum pr2 = fromDecimalString(primes[5]);
        
        assertTrue("pr1", pr1.sieve());
        assertTrue("pr2", pr2.sieve());
        
        final Bignum common = new Bignum();
        common.hcf(pr1, pr2);
        final Bignum one = new Bignum(1);
        assertTrue("hcf = 1", one.isEqual(common));
        
        common.hcf(pr2, pr1);
        assertTrue("hcf = 1 again", one.isEqual(common));
        
        assertTrue("gt 1", pr1.isGreaterThan(one));
        assertFalse("gt 2", one.isGreaterThan(pr2));
        assertFalse("!= 1", one.isEqual(pr2));
        assertFalse("!= 2", pr2.isEqual(one));
        
        final Bignum fourtwo = new Bignum(24);
        assertFalse("242 is composite", fourtwo.sieve());
        fourtwo.setValue(42);
        assertTrue("42 = 2A", "2A".equals(fourtwo.toString()));
        
        Bignum[] primal = new Bignum[primes.length];
        for(int i=0; i<primes.length; ++i)
        {
            primal[i] = fromDecimalString(primes[i]);
            for(int j=0; j<i; ++j)
            {
                assertTrue("mp "+i+"&"+j, primal[i].areMutuallyPrime(primal[j]));
                assertTrue("mp "+j+"&"+i, primal[j].areMutuallyPrime(primal[i]));
            }
        }
        assertTrue("are all primes", Bignum.arePrimes(primal, new Watch()));

        final Bignum nRSA = new Bignum();
        nRSA.multiply(pr1, pr2);
        pr1.set0thBit(false); // subtract 1
        assertFalse("now even", pr1.sieve());
        pr2.set0thBit(false); // subtract 1
        assertFalse("now even 2", pr2.sieve());
        final Bignum phi = new Bignum();
        phi.multiply(pr1, pr2);
        final Bignum pubkey = new Bignum(65537);
        /*
        xn + yp = gcd(n,p) => xn = 1 mod p if gcd(n, p) = 1
         * n = pubkey
         * p = phi
        */
        final Bignum factor = new Bignum();
        factor.hcf(phi, pubkey);
        assertTrue("hcf is 1", one.isEqual(factor));
        
        //final long now = System.currentTimeMillis();
        final Bignum seckey = new Bignum();              
        seckey.modularInverse(pubkey, phi);
        //Signum[] mod = extendedGCD(pubkey, phi);
        //final Bignum seckey = mod[0].number;
        //System.out.println(mod[0].number.toString());
        //System.out.println(mod[0].positive);
        //final long took = System.currentTimeMillis() - now;
        //System.out.println(took/1000);
        final Bignum cypher = new Bignum();
        cypher.modPower(primal[3], pubkey, nRSA, new Watch());
        final Bignum plain = new Bignum();
        plain.modPower(cypher, seckey, nRSA, new Watch());
        
        assertTrue("RSA magic", primal[3].isEqual(plain));
    }
}
