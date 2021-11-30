package com.nbkelly.lib;

import java.math.BigInteger;
import java.util.Random;

/**
 * Superclass Wrapper around BigInteger. This supports the "numbers" Infinity and NegativeInfinity,
 * but does not support NaN (use null as a standin for NaN). I may change this in the future if I really
 * have a need to.
 * <p>
 * For the most part, this is literally a wrapper around BigInteger. All arithmetic on infinites generall
 * returns infinites as a result.
 */
public class WrappedBigInt extends BigInteger implements Comparable<BigInteger> {
    private boolean isInfinite = false;
    private boolean isNegativeInfinite = false;
    private boolean isNAN = false;

    /**
     * The WrappedBigInteger constant one.
     */
    public static WrappedBigInt ONE = WrappedBigInt.valueOf(1);
    /**
     * The WrappedBigInteger constant two.
     */
    public static WrappedBigInt TWO = WrappedBigInt.valueOf(2);
    /**
     * The WrappedBigInteger constant ten.
     */
    public static WrappedBigInt TEN = WrappedBigInt.valueOf(10);
    /**
     * The WrappedBigInteger constant zero.
     */
    public static WrappedBigInt ZERO = WrappedBigInt.valueOf(0);

    private static WrappedBigInt infinity() {
	WrappedBigInt res = new WrappedBigInt("0");
	res.isInfinite = true;
	return res;
    }

    private static WrappedBigInt NAN() {
	WrappedBigInt res = new WrappedBigInt("0");
	res.isNAN = true;
	return res;
    }

    private static WrappedBigInt negativeInfinity() {
	WrappedBigInt res = new WrappedBigInt("-1");
	res.isInfinite = true;
	return res;
    }

    /**
     * The WrappedBigInteger constant infinity.
     */
    public static WrappedBigInt INFINITY = infinity();
    /**
     * The WrappedBigInteger constant negative infinity.
     */
    public static WrappedBigInt NEGATIVE_INFINITY = negativeInfinity();
    /**
     * The WrappedBigInteger constant NaN/Not a Number.
     */
    public static WrappedBigInt NOT_A_NUMBER = NAN();

    /**
     * Translates a byte array containing the two's-complement binary representation of a
     * BigInteger into a WrappedBigInt. The input array is assumed to be in big-endian byte-order:
     * the most significant byte is in the zeroth element. The val array is assumed to be
     * unchanged for the duration of the constructor call.
     *
     * @param val big-endian two's-complement binary representation of a BigInteger.
     * @throws NumberFormatException - val is zero bytes long.
     */
    public WrappedBigInt(byte[] val) {
	super(val);
    }

    /**
     * Translates the sign-magnitude representation of a BigInteger into a WrappedBigInt.
     * The sign is represented as an integer signum value: -1 for negative, 0 for zero,
     * or 1 for positive.
     * The magnitude is a byte array in big-endian byte-order:
     * the most significant byte is the zeroth element.
     * A zero-length magnitude array is permissible, and will result in a WrappedBigInt value of 0,
     * whether signum is -1, 0 or 1. The magnitude array is assumed to be unchanged for the
     * duration of the constructor call.
     * @param signum signum of the number (-1 for negative, 0 for zero, 1 for positive).
     * @param magnitude big-endian binary representation of the magnitude of the number.
     * @throws NumberFormatException signum is not one of the three legal values
     * (-1, 0, and 1), or signum is 0 and magnitude contains one or more non-zero bytes.
     */
    public WrappedBigInt(int signum, byte[] magnitude) {
	super(signum, magnitude);
    }

    /**
     * Constructs a randomly generated positive WrappedBigInt that is probably prime,
     * with the specified bitLength.
     *
     * @apiNote It is recommended that the probablePrime method be used in preference to this
     * constructor unless there is a compelling need to specify a certainty.
     *
     * @param bitLength bitLength of the returned WrappedBigInt.
     * @param certainty a measure of the uncertainty that the caller is willing to tolerate.
     * The probability that the new WrappedBigInt represents a prime number will
     * exceed (1 - 1/2certainty). The execution time of this constructor is proportional
     * to the value of this parameter.
     * @param rnd source of random bits used to select candidates to be tested for primality.
     * @throws ArithmeticException bitLength &#60; 2 or bitLength is too large.
     * @see bitLength()
     */
    public WrappedBigInt(int bitLength, int certainty, Random rnd) {
	super(bitLength, certainty, rnd);
    }

    /**
     * Constructs a randomly generated WrappedBigInt, uniformly distributed over the
     * range 0 to (2<sup>numBits - 1</sup>), inclusive.
     * The uniformity of the distribution assumes that a fair source of random bits
     * is provided in rnd.
     * Note that this constructor always constructs a non-negative WrappedBigInt.
     *
     * @param numBits maximum bitLength of the new BigInteger.
     * @param rnd source of randomness to be used in computing the new BigInteger.
     * @throws IllegalArgumentException numBits is negative.
     * @see bitLength() 
     */
    public WrappedBigInt(int numBits, Random rnd) {
	super(numBits, rnd);
    }

    /**
     * Translates the decimal String representation of a BigInteger into a WrappedBigInt.
     * The String representation consists of an optional minus sign followed by a sequence of one
     * or more decimal digits. The character-to-digit mapping is provided by Character.digit.
     * The String may not contain any extraneous characters (whitespace, for example).
     *
     * @param val decimal String representation of WrappedBigInt.
     * @throws NumberFormatException val is not a valid representation of a BigInteger.
     * @see Character.digit(char, int) 
     */
    public WrappedBigInt(String val) {
	/*switch(val.toLowerCase()) {
	case "inf":
	case "infinity":
	case "infinite":
	    return INFINITY;
	case "ninf":
	case "negative-infinity":
	case "neg-inf":
	case "-inf":
	case "-infinity":
	case "-infinite":
	    return  NEGATIVE_INFINITY;
	case "nan":
	case "not a number":
	case "not-a-number":
	case "na":
	case "n/a":
	    return NOT_A_NUMBER;;
	default:
	    break;
	    }*/

	super(val);
    }

    /**
     * Translates the String representation of a BigInteger in the specified radix
     * into a WrappedBigInt.
     * The String representation consists of an optional minus or plus sign followed by a
     * sequence of one or more digits in the specified radix.
     * The character-to-digit mapping is provided by Character.digit.
     * The String may not contain any extraneous characters (whitespace, for example).
     *
     * @param val String representation of BigInteger.
     * @param radix radix to be used in interpreting val.
     * @throws NumberFormatException val is not a valid representation of a BigInteger in the
     * specified radix, or radix is outside the range from Character.MIN_RADIX to
     * Character.MAX_RADIX, inclusive.
     * @see Character.digit(char, int)
     */
    public WrappedBigInt(String val, int radix) {
	super(val, radix);
    }

    /**
     * Convert a bigInt to a wrappedBigInt
     */
    private WrappedBigInt convert(BigInteger value) {
	return new WrappedBigInt(value.toString());
    }

    /**
     * Returns a WrappedBigInt whose value is the absolute value of this WrappedBigInt.
     * <p>
     * Infinite values will return INFINITY, and NaN will return NaN.
     * @returns abs(this)
     */
    public WrappedBigInt abs() {
	if(isAnyInf(this))
	    return INFINITY;

	if(isNaN(this))
	    return NOT_A_NUMBER;
	
	if(compareTo(ZERO) < 0)
	    return multiply(valueOf(-1));
	return this;
    }

    /**
     * Returns a WrappedBigInt whose value is (this + val).
     * <p>
     * Infinite values will return infinite values, with -inf binding more tightly.
     * If any of the values are NaN, NaN will be returned.
     *
     * @param val value to be added to this WrappedBigInt.
     * @returns this + val
     */
    public WrappedBigInt add(WrappedBigInt val) {
	//inf + * is always inf
	//-inf + * is always inf
	if(isNaN(this, val))
	    return NOT_A_NUMBER;
	if(isNegInf(this, val))
	    return NEGATIVE_INFINITY;

	if(isInf(this, val))
	    return INFINITY;

	return convert(super.add(val));
    }

    /**
     * Returns a WrappedBigInt whose value is (this &#38; val).
     * (This method returns a negative WrappedBigInt if and only if this and val are both negative.)
     * <p>
     * Infinite values will return infinite values, with -inf binding more tightly.
     * If any of the values are NaN, NaN will be returned.
     *
     * @param val value to be AND'ed with this WrappedBigInt
     * @return this &#38; val
     */
    public WrappedBigInt and(WrappedBigInt val) {
	//inf + * is always inf
	//-inf + * is always inf
	if(isNaN(this, val))
	    return NOT_A_NUMBER;
	if(isNegInf(this, val))
	    return NEGATIVE_INFINITY;

	if(isInf(this, val))
	    return INFINITY;

	return convert(super.and(val));
    }

    /**
     * Returns a WrappedBigInt whose value is (this &#38; ~val).
     * This method, which is equivalent to and(val.not()), is provided as a convenience for
     * masking operations.
     * (This method returns a negative WrappedBigInt if and only if
     * this is negative and val is positive.)
     * <p>
     * Infinite values will return infinite values, with -inf binding more tightly.
     * If any of the values are NaN, NaN will be returned.
     *
     * @param val the value to be complemented and AND'ed with this WrappedBigInt
     * @return this &#38; ~val
     */
    public WrappedBigInt andNot(WrappedBigInt val) {
	//inf + * is always inf
	//-inf + * is always inf
	if(isNaN(this, val))
	    return NOT_A_NUMBER;
	if(isNegInf(this, val))
	    return NEGATIVE_INFINITY;

	if(isInf(this, val))
	    return INFINITY;

	return convert(super.andNot(val));
    }

    /**
     * Returns a WrappedBigInt whose value is (this / val).
     *
     * @todo 2arg
     * @param val value by which this WrappedBigInt is to be divided.
     * @return this / val
     * @throws ArithmeticException if val is zero
     */
    public WrappedBigInt divide(WrappedBigInt val) {
	//inf + * is always inf
	//-inf + * is always inf
	if(isNaN(this, val))
	    return NOT_A_NUMBER;
	if(isNegInf(this, val))
	    return NEGATIVE_INFINITY;

	if(isInf(this, val))
	    return INFINITY;	

	return convert(super.divide(val));
    }

    /**
     * Returns an array of two WrappedBigInts contains (this / val) followed by (this % val).
     *
     * @param val value by which WrappedBigInt is to be divided, and the remainder computed.
     * @return an array of two WrappedBigInts: the quotient (this / val) is the initial element,
     * and the remainder (this % val) is the final element.
     * @throws ArithmeticException if val is zero.
     */
    public WrappedBigInt[] divideAndRemainder(WrappedBigInt val) {
	return new WrappedBigInt[] {divide(val), mod(val)};
    }
    
    /**
     * Returns a WrappedBigInt whose value is the greatest common divisor of abs(this) and abs(val).
     * Returns 0 if this == 0 &#38;&#38; val == 0.
     *
     * @todo 2arg
     * @param val value with which the GCD is to be computed
     * @returns GCD(abs(this), abs(val))
     */
    public WrappedBigInt gcd(WrappedBigInt val) {
	//inf + * is always inf
	//-inf + * is always inf
	if(isNaN(this, val))
	    return NOT_A_NUMBER;
	if(isNegInf(this, val))
	    return NEGATIVE_INFINITY;

	if(isInf(this, val))
	    return INFINITY;	

	return convert(super.gcd(val));
    }

    /**
     * Returns a WrappedBigInt whose value is (this mod m). This method differs from remainder
     * in that it always returns a <i>non-negative</i> WrappedBigInt.
     *
     * @todo 2arg
     * @param m the modulus
     * @returns this mod m
     * @throws ArithmeticException m &leq; 0
     * @see remainder
     */
    public WrappedBigInt mod(WrappedBigInt m) {
	if(isNaN(this, m))
	    return NOT_A_NUMBER;
	if(isNegInf(this, m))
	    return NEGATIVE_INFINITY;

	if(isInf(this, m))
	    return INFINITY;
	
	return convert(super.mod(m));
    }

    /**
     * Returns a WrappedBigInt whose value is equivalent to this WrappedBigInt with the designated
     * bit flipped. (Computes (this ^ (1 &#60;&#60; n)).)
     *
     * @todo selfParam 
     * @param n index of bit to flip
     * @return this ^ (1 &#60;&#60; n);
     * @throws ArithmeticException n is negative.
     */
    public WrappedBigInt flipBit(int n) {
	//inf + * is always inf
	//-inf + * is always inf
	if(isNaN(this))
	    return NOT_A_NUMBER;
	if(isNegInf(this))
	    return NEGATIVE_INFINITY;

	if(isInf(this))
	    return INFINITY;
	return convert(super.flipBit(n));
    }

    /**
     * Returns a WrappedBigInt whose value is equivalent to this WrappedBigInt with the designated
     * bit cleared. (Computes (this &amp; ~(1 &#60;&#60; n)).)
     *
     * @todo selfParam 
     * @param n index of bit to clear
     * @return this &amp; ~(1 &#60;&#60; n)
     * @throws ArithmeticException n is negative.
     */
    public WrappedBigInt clearBit(int n) {
	//inf + * is always inf
	//-inf + * is always inf
	if(isNaN(this))
	    return NOT_A_NUMBER;
	if(isNegInf(this))
	    return NEGATIVE_INFINITY;

	if(isInf(this))
	    return INFINITY;

	return convert(super.clearBit(n));
    }

    /**
     * Compares this WrappedBigInt with the specified WrappedbigInt. This method is provided in
     * preference to individual methods for each of the six boolean comparison operators
     * (&lt;, ==, &gt;, &gt;=, !=, &lt;=). The suggested idion for performing these comparisons is:
     * (x.compareTo(y) <i>&lt;op&gt;</i> 0, where <i>&lt;op&gt;</i> is one of the six comparison
     * operators.
     *
     * @todo 2arg
     * @param val WrappedBigInt to which this WrappedBigInt is to be compared
     * @returns -1, 0, or 1 as this WrappedBigInt is numerically less than, equal to, or greater
     * than val.
     */
    public int compareTo(WrappedBigInt val) {	
	//order:
	//NaN, NegativeInf, reg, Inf
	if(isNaN(this) && !isNaN(val))
	    return -1;
	else if (isNaN(this, val))
	    return 0;
	else if (isNaN(val))
	    return 1;

	//NaN is out of play, now we only have to deal with inf/negInf
	if((isNegInf(this) && isNegInf(val))
	   || (isInf(this) && isInf(val)))
	    return 0;

	if(isNegInf(this) || isInf(val))
	    return -1;

	if(isInf(this) || isNegInf(val))
	    return 1;
	
	return super.compareTo(val);
    }

    
    /**
     * Compares this WrappedBigInt with the specified BigInteger. This method is provided in
     * preference to individual methods for each of the six boolean comparison operators
     * (&lt;, ==, &gt;, &gt;=, !=, &lt;=). The suggested idion for performing these comparisons is:
     * (x.compareTo(y) <i>&lt;op&gt;</i> 0, where <i>&lt;op&gt;</i> is one of the six comparison
     * operators.
     *
     * If the BigInteger happens to be an instance of WrappedBigInt, the WrappedBigInt comparison
     * is resorted to instead. Curse the lack of multiply-generic java inheritance.
     *
     * @todo 2arg
     * @param val WrappedBigInt to which this WrappedBigInt is to be compared
     * @returns -1, 0, or 1 as this WrappedBigInt is numerically less than, equal to, or greater
     * than val.
     */
    public int compareTo(BigInteger val) {
	if(val instanceof WrappedBigInt)
	    return compareTo((WrappedBigInt)val);
	
	return super.compareTo(val);
    }

    /**
     * Returns the maximum of this WrappedBigInt and val.
     *
     * @todo 2arg
     * @param val value with which the maximum is to be computed.
     * @returns the WrappedBigInt whose value is the greater of this and val. If they are equal,
     * either may be returned.
     */
    public WrappedBigInt max(WrappedBigInt val) {
	//inf + * is always inf
	//-inf + * is always inf
	if(isNaN(this, val))
	    return NOT_A_NUMBER;

	if(isInf(this, val))
	    return INFINITY;

	if(isNegInf(this))
	    return val;
	if(isNegInf(val))
	    return this;

	return convert(super.max(val));
    }

    /**
     * Returns the minimum of this WrappedBigInt and val.
     *
     * @todo 2arg
     * @param val value with which the minimum is to be computed.
     * @returns the WrappedBigInt whose value is the lesser of this and val. If they are equal,
     * either may be returned.
     */
    public WrappedBigInt min(WrappedBigInt val) {
	//inf + * is always inf
	//-inf + * is always inf
	if(isNaN(this, val))
	    return NOT_A_NUMBER;

	if(isNegInf(this, val))
	    return NEGATIVE_INFINITY;

	if(isInf(this))
	    return val;
	if(isInf(val))
	    return this;

	return convert(super.min(val));
    }

    /**
     * Returns a WrappedBigInt whose value is (this<sup>-1</sup> mod m).
     *
     * @todo 2arg
     * @param m the modulus
     * @returns this<sup>-1</sup> mod m.
     * @throws ArithmeticException m &lte; 0, or this WrappedBigInt has no multiplicative inverse
     * mod m (this is, the WrappedBigInt is not <i>relatively prime</i> to m).
     */
    public WrappedBigInt modInverse(WrappedBigInt m) {
	//inf + * is always inf
	//-inf + * is always inf
	if(isNaN(this, m))
	    return NOT_A_NUMBER;
	if(isNegInf(this, m))
	    return NEGATIVE_INFINITY;

	if(isInf(this, m))
	    return INFINITY;

	return convert(super.modInverse(m));
    }

    /**
     * Returns a WrappedBigInt whose value is (this<sup>exponent</sup> mod m). (Unlike pow, this
     * method permits negative exponents).
     *
     * @todo 3arg 
     * @param exp the exponent.
     * @param m the modulus.
     * @returns this<sup>exponent</sup> mod m
     * @throws ArithmeticException m &lte; 0 or the exponent is negative and this WrappedBigInt is
     * not <i>relatively prime</i> to m.
     * @see modInverse
     */
    public WrappedBigInt modPow(WrappedBigInt exp, WrappedBigInt m) {
	//inf + * is always inf
	//-inf + * is always inf
	if(isNaN(this, m, exp))
	    return NOT_A_NUMBER;
	if(isNegInf(this, m, exp))
	    return NEGATIVE_INFINITY;

	if(isInf(this, m, exp))
	    return INFINITY;

	return convert(super.modPow(exp, m));
    }

    /**
     * Returns a WrappedBigInt whose value is (this * val)
     *
     * @todo 2val
     * @note An implementation may offer better algorithmic performance when val == this
     * @param val value to be multiplied by this WrappedBigInt
     * @return this * val
     */
    public WrappedBigInt multiply(WrappedBigInt val) {
	if(isNaN(this, val))
	    return NOT_A_NUMBER;
	if(isNegInf(this, val))
	    return NEGATIVE_INFINITY;
	if(isInf(this, val))
	    return INFINITY;

	return convert(super.multiply(val));
    }

    /**
     * Returns a WrappedBigInt whose value is (-this).
     *
     * @todo 1val
     * @return -this
     */
    public WrappedBigInt negate() {
	if(isNaN(this))
	    return NOT_A_NUMBER;
	if(isNegInf(this))
	    return INFINITY;
	if(isInf(this))
	    return NEGATIVE_INFINITY;

	return convert(super.negate());
    }

    /**
     * Returns a WrappedBigInt whose value is (~this). (This method returns a negative value if and
     * only if this WrappedBigInt is non-negative.)
     *
     * @todo 1val
     * @return ~this
     */
    public WrappedBigInt not() {
	if(isNaN(this))
	    return NOT_A_NUMBER;
	if(isInf(this))
	    return INFINITY;
	if(isNegInf(this))
	    return NEGATIVE_INFINITY;

	return convert(super.not());
    }

    /**
     * Returns a WrappedBigInt whose value is (this | val). (This method returns a negative
     * WrappedBigInt if and only if either this or val is negative.)
     *
     * @note 2val
     * @param val value to be OR'ed with this WrappedBigInt
     * @returns this | val
     */
    public WrappedBigInt or(WrappedBigInt val) {
	if(isNaN(this, val))
	    return NOT_A_NUMBER;
	if(isNegInf(this, val))
	    return NEGATIVE_INFINITY;

	if(isInf(this, val))
	    return INFINITY;

	return convert(super.or(val));
    }

    /**
     * Returns a wrappedBigInt whose value is (this<sup>exponent</sup>). Note that the exponent is
     * an integer rather than a WrappedBitInt.
     *
     * @todo 1arg 
     * @param exponent - exponent to which this WrappedbigInt is to be raised
     * @returns this<sup>exponent</sup>
     * @throws ArithmeticException exponent is negative. (This would cause the operation to yield a
     * non-integer value.)
     */
    public WrappedBigInt pow(int exponent) {
	if(isNaN(this))
	    return NOT_A_NUMBER;
	
	if(exponent == 0)
	    return ONE;

	if(isInf(this))
	    return INFINITY;
	if(isNegInf(this))
	    return NEGATIVE_INFINITY;
	
	return convert(super.pow(exponent));
    }

    /**
     * Returns a WrappedBigInt whose value is (this % val).
     *
     * @param val value by which this WrappedBigInt is to be divided, and the remainder computed.
     * @returns this % val
     * @throws ArithmeticException if val is zero.
     */
    public WrappedBigInt remainder(WrappedBigInt val) {
	return mod(val);
    }

    /**
     * Returns a WrappedBigInt whose value is equivalent to this WrappedBigInt with the designated
     * bit set. (Computes (this | (1 &lt;&lt; n)).)
     *
     * @todo 1arg
     * @param n index of bit to set
     * @returns this | (1 &lt;&lt; n)
     * @throws ArithmeticException n is negative
     */
    public WrappedBigInt setBit(int n) {
	//inf + * is always inf
	//-inf + * is always inf
	if(isNegInf(this))
	    return NEGATIVE_INFINITY;
	if(isInf(this))
	    return INFINITY;
	if(isNaN(this))
	    return NOT_A_NUMBER;

	return convert(super.setBit(n));
    }

    /**
     * Return a WrappedBigInt whose value is (this &lt;&lt; n). The shift distance, n, may be
     * negative, in which case this method performs a right shift. (Computes floor(this * 2<sup>n
     * </sup>).)
     *
     * @todo 1arg
     * @param n shift distance, in bits
     * @return this &lt;&lt; n
     * @see shiftRight(int)
     */
    public WrappedBigInt shiftLeft(int n) {
	//inf + * is always inf
	//-inf + * is always inf
	if(isNaN(this))
	    return NOT_A_NUMBER;
	if(isNegInf(this))
	    return NEGATIVE_INFINITY;
	if(isInf(this))
	    return INFINITY;

	return convert(super.shiftLeft(n));
    }


    /**
     * Return a WrappedBigInt whose value is (this &gt;&gt; n). The shift distance, n, may be
     * negative, in which case this method performs a left shift. (Computes floor(this / 2<sup>n
     * </sup>).)
     *
     * @todo 1arg
     * @param n shift distance, in bits
     * @return this &gt;&gt; n
     * @see shiftLeft(int)
     */
    public WrappedBigInt shiftRight(int n) {
	if(isNaN(this))
	    return NOT_A_NUMBER;
	if(isNegInf(this))
	    return NEGATIVE_INFINITY;
	if(isInf(this))
	    return INFINITY;
	
	return convert(super.shiftRight(n));
    }

    /**
     * Returns a WrappedBigInt whose value is (this - val).
     *
     * @todo 2arg 
     * @param val value to be subtracted from this WrappedBigInt.
     * @returns this - val
     */
    public WrappedBigInt subtract(WrappedBigInt val) {
	//inf + * is always inf
	//-inf + * is always inf
	if(isNaN(this, val))
	    return NOT_A_NUMBER;
	if(isInf(val) || isNegInf(this))
	    return NEGATIVE_INFINITY;
	if(isNegInf(val) || isInf(this))
	    return INFINITY;
	
	return convert(super.subtract(val));
    }

    /**
     * Returns a WrappedBigInt whose value is equal to that of the specified long.
     * @param val value of the the WrappedBigInt to return.
     * @return a WrappedBigInt with the specified value.
     */
    public static WrappedBigInt valueOf(long val) {
	return new WrappedBigInt(val + "");
    }

    /**
     * Returns the decimal String representation of this WrappedBigInt. The digit-to-character
     * mapping provided by Character.forDigit is used, and a minus sign is prepended if appropriate.
     * (This representation is compatible with the (String) constructor, and allows for String
     * concatenation with Java's + operator.)
     * <p>
     * The values NaN, NegativeInfinity, and Infinity will be presented as 'NaN', '-∞' and '∞'
     * respectively.
     *
     * @return decimal String representation of this WrappedBigInt.
     * @see Character.forDigit(int, int)
     * @see java.math.BigInteger#<init>(java.lang.String)
     */
    @Override public String toString() {
	if(isNaN(this))
	    return "NaN";
	if(isNegInf(this))
	    return "-∞";
	if (isInf(this))
	    return "∞";

	return super.toString();
    }

    /**
     * Returns the String representation of this WrappedBigInt in the specified radix.
     * If the radix is outside the range from Character.MIN_RADIX to Character.MAX_RADIX inclusive,
     * it will default to 10 (as is the case for Integer.toString). The digit-to-character
     * mapping provided by Character.forDigit is used, and a minus sign is prepended if appropriate.
     * (This representation is compatible with the (String) constructor).
     * <p>
     * The values NaN, NegativeInfinity, and Infinity will be presented as 'NaN', '-∞' and '∞'
     * respectively.
     *
     * @param radix radix of the String representation. 
     * @return String representation of this WrappedBigInt in the given radix.
     * @see Character.forDigit(int, int)
     * @see java.math.BigInteger#<init>(java.lang.String)
     * @see Integer.toString(int, int)
     */
    @Override public String toString(int radix) {
	if(isNaN(this))
	    return "NaN";
	if(isNegInf(this))
	    return "-∞";
	if (isInf(this))
	    return "∞";

	return super.toString(radix);
    }

    /**
     * Returns a WrappedBigInt whose value is (this ^ val). (This method returns a negative
     * WrappedBigInt if and only if exactly one of this and val are negative.)
     *
     * @todo 2arg
     * @param val value to be XOR'ed with this WrappedBigInt.
     * @return this ^ val
     */
    public WrappedBigInt xor(WrappedBigInt val) {
	//inf + * is always inf
	//-inf + * is always inf
	if(isNaN(this, val))
	    return NOT_A_NUMBER;
	
	if(isNegInf(this, val))
	    return NEGATIVE_INFINITY;

	if(isInf(this, val))
	    return INFINITY;
	
	return convert(super.xor(val));
    }

    /**
     * If a WrappedBigInteger is abnormal (INF, NEG_INF, NAN), then return that WrappedBigInt.
     * Otherwise, return null.
     *
     * @returns Identity of any abnormal WrappedBigInts, otherwise null. 
     */
    protected WrappedBigInt abnormal() {
	if(this == INFINITY ||
	   this == NEGATIVE_INFINITY ||
	   this == NOT_A_NUMBER)
	    return this;
	return null;
    }

    /**
     * Returns true if any WrappedBigInt from a range of WrappedBigInts has an infinite value.
     *
     * @param vals values to check for infinite members
     * @return true if any WrappedBigInt from vals contains an infinite value
     */
    protected boolean isAnyInf(WrappedBigInt... vals) {
	for(int i = 0; i < vals.length; i++)
	    if(vals[i] == INFINITY || vals[i] == NEGATIVE_INFINITY)
		return true;
	return false;
    }

    /**
     * Returns true if any WrappedBigInt from a range of WrappedBigInts has a positive
     * infinite value.
     *
     * @param vals values to check for positive infinite members
     * @return true if any WrappedBigInt from vals contains a positive infinite value
     */
    protected boolean isInf(WrappedBigInt... vals) {
	for(int i = 0; i < vals.length; i++)
	    if(vals[i] == INFINITY)
		return true;
	return false;
    }

    /**
     * Returns true if any WrappedBigInt from a range of WrappedBigInts has a negative
     * infinite value.
     *
     * @param vals values to check for negative infinite members
     * @return true if any WrappedBigInt from vals contains a negative infinite value
     */
    protected boolean isNegInf(WrappedBigInt... vals) {
	for(int i = 0; i < vals.length; i++)
	    if(vals[i] == NEGATIVE_INFINITY)
		return true;
	return false;
    }

    /**
     * Returns true if any WrappedBigInt from a range of WrappedBigInts is not a number.
     *
     * @param vals values to check for non-numbers
     * @return true if any WrappedBigInt from vals contains the value Not a Number/NaN
     */
    protected boolean isNaN(WrappedBigInt... vals) {
	for(int i = 0; i < vals.length; i++)
	    if(vals[i] == NOT_A_NUMBER)
		return true;
	return false;
    }
}
