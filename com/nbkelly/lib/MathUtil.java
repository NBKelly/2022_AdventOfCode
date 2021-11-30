package com.nbkelly.lib;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.LinkedList;

/**
 * @author      NB Kelly <nbkelly @ protonmail.com>
 * @version     1.8
 * @since       1.3
 */
public class MathUtil {
    /**
     * Get the fibonacci sequence for value V
     * @param value to get fib for
     * @return the fibonacci sequence, f(n) = (fn-1) + f(n-2), for the input value
     * @since 1.3
     */
    public static BigInteger fib(int value) {	
	boolean neg = (value < 0);
	if(neg)
	    value *= -1;

	BigInteger[] fib = new BigInteger[Math.max(value+1, 3)];	
	    
	fib[0] = BigInteger.ZERO;
	fib[1] = BigInteger.ONE;
	fib[2] = neg ? BigInteger.valueOf(-1) : BigInteger.ONE;
	
	for(int i = 3; i <= value; i++)
	    if(neg)
		fib[i] = fib[i-1].subtract(fib[i-2]);
	    else
		fib[i] = fib[i-1].add(fib[i-2]);

	return fib[value];
    }

    /**
     * Get the fibonnaci sequence for value V with the given expansion factor
     * @param value to get fib for
     * @param factor expansion factor for this sequence
     * @return the fibonacci sequence, f(n) = (fn-1) + facotr * f(n-2), for the input value
     * @since 1.3
     */
    public static BigInteger fib(int value, int factor) {	
	boolean neg = (value < 0);
	if(neg)
	    value *= -1;
	
	BigInteger[] fib = new BigInteger[Math.max(value+1, 3)];	    

	fib[0] = BigInteger.ZERO;
	fib[1] = BigInteger.ONE;
	fib[2] = neg ? BigInteger.valueOf(-1) : BigInteger.ONE;
	var _factor = BigInteger.valueOf(factor);
	
	for(int i = 3; i <= value; i++)
	    if(neg)
		fib[i] = fib[i-1].subtract(_factor.multiply(fib[i-2]));
	    else
		fib[i] = fib[i-1].add(_factor.multiply(fib[i-2]));

	return fib[value];
    }

    /**
     * Gets the fibonacci sequence for a duration with each generation having a set lifetime
     * @param duration number of generations to simulate
     * @param lifetime lifetime of each rabbit
     * @return the number of rabbits alive at the given generation if each rabbit has a set lifetime
     */
    public static BigInteger fibMortal(int duration, int lifetime) {
	LinkedList<BigInteger> alive = new LinkedList<BigInteger>();

	alive.add(BigInteger.ZERO);
	alive.add(BigInteger.ONE);
	BigInteger sum = BigInteger.ONE;
	
	for(int generation = 1; generation < duration; generation++) {
	    //offspring is the sum of all bunnies, other than the first one
	    BigInteger offspring = sum.subtract(alive.peekLast());

	    alive.add(offspring);
	    sum = sum.add(offspring);

	    if(alive.size() > lifetime)
		sum = sum.subtract(alive.removeFirst());
	}
	
	return sum;
    }


    public static BigDecimal binomial(int n, int k, BigDecimal p) {
	return new BigDecimal(binomial(k, n))
	    .multiply(p.pow(k))
	    .multiply(BigDecimal.valueOf(1).subtract(p).pow(n-k));
    }
    
    /**
     * Returns the number of unique k-length combinations in a set of size n.
     * @param k combination size
     * @param n set size
     * @return the number of unique k-length combinations in a set of size n.
     * @since 1.8
     */
    public static BigInteger binomial(int k, int n) {
	if(k < 0 || n < 0)
	    throw new ArithmeticException("K or N less than 0");
	if(n < k)
	    return BigInteger.ZERO;
	if(k == 1)
	    return BigInteger.valueOf(n);
	if(k == 0)
	    return BigInteger.ONE;
		
	BigInteger res = BigInteger.ONE;

	var K = BigInteger.valueOf(k);
	var N = BigInteger.valueOf(n);

	K = K.min(N.subtract(K));
	//k = Math.min(k, n - k);
	for(int i = 0; i < k; i++)
	    res = res.multiply(N.subtract(BigInteger.valueOf(i)))
		   .divide(BigInteger.valueOf(i).add(BigInteger.ONE));
	//for(int i = 0; i < k; i++)
	//    res = (res * (n - i)) / (i + 1);

	return res;
    }

    /**
     * Returns the number of unique k-length combinations in a set of size n.
     * @param K combination size
     * @param N set size
     * @return the number of unique k-length combinations in a set of size n.
     * @since 1.8
     */
    public static BigInteger binomial(BigInteger K, BigInteger N) {
	if(K.compareTo(BigInteger.ZERO) < 0 || N.compareTo(BigInteger.ZERO) < 0)
	    throw new ArithmeticException("K or N less than 0");
	if(N.compareTo(K) < 0)
	    return BigInteger.ZERO;
	if(K.equals(BigInteger.ONE))
	    return N;
	if(K.equals(BigInteger.ZERO))
	    return BigInteger.ONE;
		
	BigInteger res = BigInteger.ONE;

	//var K = BigInteger.valueOf(k);
	//var N = BigInteger.valueOf(n);

	K = K.min(N.subtract(K));
	//k = Math.min(k, n - k);
	for(int i = 0; BigInteger.valueOf(i).compareTo(K) < 0; i++)
	    res = res.multiply(N.subtract(BigInteger.valueOf(i)))
		   .divide(BigInteger.valueOf(i).add(BigInteger.ONE));
	//for(int i = 0; i < k; i++)
	//    res = (res * (n - i)) / (i + 1);

	return res;
    }

    public static BigInteger factorial(int num) {
	return factorial(BigInteger.valueOf(num));
    }
    
    public static BigInteger factorial(BigInteger number) {
	BigInteger result = BigInteger.valueOf(1);

	for (var fac = 2; fac <= number.longValue(); fac++)
	    result = result.multiply(BigInteger.valueOf(fac));

	return result;
    }
}
