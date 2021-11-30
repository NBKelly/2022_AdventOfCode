package com.nbkelly.lib;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Comparator;
import java.util.function.BiFunction;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

/**
 * @author      NB Kelly <nbkelly @ protonmail.com>
 * @version     1.4
 * @since       1.0
 */
public final class Util {
    /**
     * Obtains the consensus profile of a set of strings.
     * <p>
     * When the consensus is ambigious, gives the string with the lowest lexicographical order
     */
    public static String consensus(String[] args) {
	return consensus(BigInteger.ZERO, args);
    }

    public static byte[] md5sum_bytes(Object object) {
	var str = object.toString();
	try {
	    var bytes = str.getBytes("UTF-8");
	    MessageDigest md = MessageDigest.getInstance("MD5");
	    var res = md.digest(bytes);
	    return res;
	} catch (Exception e) {
	    System.err.println(e);
	    System.err.println("Attempted to get MD5Sum of object " + str + ", but failed!");
	    return null;
	}		
    }

    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);

    public static String md5sum(Object object) {
	return bytesToHex(md5sum_bytes(object));
    }
    
    public static String bytesToHex(byte[] bytes) {
	byte[] hexChars = new byte[bytes.length * 2];
	for (int j = 0; j < bytes.length; j++) {
	    int v = bytes[j] & 0xFF;
	    hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	    hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	}
	return new String(hexChars, StandardCharsets.UTF_8);
    }
    
    public static <T> T max(T left, T right, BiFunction<T, T, T> selector) {
	return selector.apply(left, right);
    }
    
    public boolean interweaveable(String target, String left, String right) {
	int tar_len = left.length() + right.length();

	//TODO: match alphabet to given segment
	//HashCounter alphabet = new HashCounter
	
	for(int i = 0; i <= target.length() - tar_len; i++)
	    if(strict_interweaveable(target.substring(i, i + left.length() + right.length()),
				     left, right))
		return true;
	return false;
    }
		
    private boolean strict_interweaveable(String target, String left, String top) {
	//printf("%s (%s, %s)%n", target, left, top);
	boolean[] last = new boolean[top.length() + 1];
	last[0] = true;

	for(int t = 1; t < top.length(); t++)
	    last[t] = (last[t-1] && top.charAt(t-1) == target.charAt(t-1));
	
	for(int l = 1; l <= left.length(); l++) {
	    boolean[] next = new boolean[top.length() + 1];

	    next[0] = last[0] && left.charAt(l-1) == target.charAt(l-1);

	    for(int t = 1; t <= top.length(); t++) {
		char token = target.charAt(l + t - 1);
		next[t] = (next[t-1] && token == top.charAt(t-1))
		    || (last[t] && token == left.charAt(l - 1));
	    }
	    
	    last = next;
	}
	
	
	
	return last[top.length()];
    }
    
    /**
     * Obtains the nth consensus profile of a set of strings.
     * <p>
     * When the consensus is ambigious, gives the string with the nth lowest lexicographical order,
     * if it exists
     */
    public static String consensus(BigInteger order, String[] args, ArrayList<Character> alphabet) {
	int longest = 0;
	for(int i = 0; i < args.length; i++)
	    longest = Math.max(longest, args[i].length());

	Comparator<Character> comp = new Comparator<>() {
		@Override public int compare(Character a, Character b) {
		    return alphabet.indexOf(a) - alphabet.indexOf(b);
		}
	    };

	//build something approaching a matrix
	ArrayList<Object[]> elements = new ArrayList<Object[]>();
	//ArrayList<TreeSet<Character>> elements = new ArrayList<TreeSet<Character>>();
	for(int i = 0; i < longest; i++) {
	    HashCounter<Character> hc = new HashCounter<Character>();
	    for(int j = 0; j < args.length; j++)
		if(args[j].length() > i)
		    hc.add(args[j].charAt(i));

	    TreeSet<Character> elem = new TreeSet<Character>(comp);
	    elem.addAll(hc.maxSet());
	    elements.add(elem.toArray());
	}

	//count the number of permutations
	BigInteger perm = BigInteger.ONE;
	for(var elem : elements)
	    perm = perm.multiply(BigInteger.valueOf(elem.length));

	//System.out.println("Number of permutations: " + perm);

	order = order.mod(perm);

	StringBuilder res = new StringBuilder();
	BigInteger divisor = BigInteger.ONE;
	for(int i = elements.size() - 1; i >= 0; i--) {
	    var elem = elements.get(i);
	    if(order.equals(BigInteger.ZERO) || elem.length == 1)
		res.insert(0, elem[0]);
	    else {				
		//divide by the divisor
		//modulo by the size
		//multiply divisor by size
		BigInteger c = order.divide(divisor);
		int index = c.mod(BigInteger.valueOf(elem.length)).intValue();
		divisor = divisor.multiply(BigInteger.valueOf(elem.length));
		
		res.insert(0,(Character)elem[index]);
	    }
	}

	return res.toString();
    }
    
    /**
     * Obtains the nth consensus profile of a set of strings.
     * <p>
     * When the consensus is ambigious, gives the string with the nth lowest lexicographical order,
     * if it exists
     */
    public static String consensus(BigInteger order, String[] args) {
	int longest = 0;
	for(int i = 0; i < args.length; i++)
	    longest = Math.max(longest, args[i].length());

	//build something approaching a matrix
	ArrayList<Object[]> elements = new ArrayList<Object[]>();
	//ArrayList<TreeSet<Character>> elements = new ArrayList<TreeSet<Character>>();
	for(int i = 0; i < longest; i++) {
	    HashCounter<Character> hc = new HashCounter<Character>();
	    for(int j = 0; j < args.length; j++)
		if(args[j].length() > i)
		    hc.add(args[j].charAt(i));

	    TreeSet<Character> elem = new TreeSet<Character>(hc.maxSet());
	    elements.add(elem.toArray());
	}

	//count the number of permutations
	BigInteger perm = BigInteger.ONE;
	for(var elem : elements)
	    perm = perm.multiply(BigInteger.valueOf(elem.length));

	//System.out.println("Number of permutations: " + perm);

	order = order.mod(perm);

	StringBuilder res = new StringBuilder();
	BigInteger divisor = BigInteger.ONE;
	for(int i = elements.size() - 1; i >= 0; i--) {
	    var elem = elements.get(i);
	    if(order.equals(BigInteger.ZERO) || elem.length == 1)
		res.insert(0, elem[0]);
	    else {				
		//divide by the divisor
		//modulo by the size
		//multiply divisor by size
		BigInteger c = order.divide(divisor);
		int index = c.mod(BigInteger.valueOf(elem.length)).intValue();
		divisor = divisor.multiply(BigInteger.valueOf(elem.length));
		
		res.insert(0,(Character)elem[index]);
	    }
	}

	return res.toString();
    }
    
    public static HashMap<Character, ArrayList<Integer>>
	profile(String[] args, Character[] monitor) {
	int longest = 0;
	for(int i = 0; i < args.length; i++)
	    longest = Math.max(longest, args[i].length());

	HashMap<Character, ArrayList<Integer>> res = new HashMap<>();

	for(int i = 0; i < monitor.length; i++)
	    res.put(monitor[i], new ArrayList<Integer>());
	
	//build something approaching a matrix
	
	for(int i = 0; i < longest; i++) {
	    HashCounter<Character> hc = new HashCounter<Character>();
	    for(int j = 0; j < args.length; j++)
		if(args[j].length() > i)		    
		    hc.add(args[j].charAt(i));

	    
	    for(int j = 0; j < monitor.length; j++)
		res.get(monitor[j]).add(hc.count(monitor[j]));
	}

	return res;
    }


    /**
     * Determine the hamming distance between two strings.
     * <p>
     * The hamming distance is the raw number of mismatches between two strings of equal length.
     * <p>
     * If one string is longer than the other, the end of the shorter string is considered to be filled with empty
     * characters, adding a distance of abs(s - t) size.
     *
     * @param s first string to compare
     * @param t second string to compare
     * @return Hamm(s, t)
     * @since 1.4
     */
    public static Integer hammingDistance(String s, String t) {
	int dist = 0;
	for(int i = 0; i < Math.min(s.length(), t.length()); i++) {
	    if(s.charAt(i) != t.charAt(i))
		dist++;
	}

	dist += Math.abs(s.length() - t.length());
	return dist;
    }
    
    /**
     * Given an image and key, composes a 2d representation of that image
     * 
     * @param image Mapping from IntPair to VAR composing the 2d space
     * @param key mapping from T to character composing the image legend
     * @param defaultChar default character used to fill in missing spaces in the image
     * @param yPriority wether to draw the image x upon y, or y upon x
     * @return A list of lines containing the 'rendered' image
     * @since 1.3
     */
    public static <T> ArrayList<String> compose(Map<IntPair, T> image, Map<T, Character> key,
						char defaultChar, boolean yPriority) {
	var min = boundary(image.keySet(), true);
	var max = boundary(image.keySet(), false);
	var res = new ArrayList<String>();

	if(!yPriority) {
	    for(int x = min.X; x<= max.X; x++) {
		StringBuilder ct = new StringBuilder("");
		for(int y = min.Y; y <= max.Y; y++) {
		    IntPair currentPair = new IntPair(x, y);
		    char toWrite = defaultChar;
		    if(image.containsKey(currentPair)) {
			//see if the keymap contains the key
			
			T _key = image.get(currentPair);
			if(key.containsKey(_key))
			    toWrite = key.get(_key);
		    }
		    
		    ct.append(toWrite);	    
		}
		res.add(ct.toString());
	    }
	}
	else {	    
	    for(int y = min.Y; y <= max.Y; y++) {
		StringBuilder ct = new StringBuilder("");
		for(int x = min.X; x<= max.X; x++) {
		    IntPair currentPair = new IntPair(x, y);
		    char toWrite = defaultChar;
		    if(image.containsKey(currentPair)) {
			//see if the keymap contains the key
			
			T _key = image.get(currentPair);
			if(key.containsKey(_key))
			    toWrite = key.get(_key);
		    }
		    
		    ct.append(toWrite);	    
		}
		res.add(ct.toString());
	    }
	}

	return res;
    }

    /**
     * Creates a hashset comprising all numbers in the interval [start, end] (inclusive)
     * @param start start of interval
     * @param end end of interval
     * @return set of all numbers in that inverval (including start and end)
     * @since 1.5
     */
    public static HashSet<Integer> set(int start, int end) {
	HashSet<Integer> set = new HashSet<Integer>();
	if(end < start) {
	    int tmp = end;
	    end = start;
	    start = tmp;	   
	}
	while(start <= end)
	    set.add(start++);
	return set;
    }

    /**
     * Creates a hashset comprising all numbers in the interval [start, end] (inclusive) that are
     * not in the given set
     * @param start start of interval
     * @param end end of interval
     * @param set the set to take the complement of
     * @return set of all numbers in that inverval (including start and end)
     * @since 1.5
     */
    public static HashSet<Integer> complement(int start, int end, HashSet<Integer> set) {
	HashSet<Integer> res = new HashSet<Integer>();
	if(end < start) {
	    int tmp = end;
	    end = start;
	    start = tmp;	   
	}
	while(start <= end)
	    if(!set.contains(start))
		res.add(start++);
	    else
		start++;
	return res;
    }

    /**
     * Constricts a 2d grid (comprising a map or image) on a given dimension.
     * <p>
     * This makes use of list iterators to always take place in O(n) time.
     * @param screen image to restrict
     * @param dimensionX if true, restrict on X dimension. Otherwise, on Y.
     * @param tendRightBot if true, keep the right/bottom half. Otherwise, keep the left/top half.
     * @param size maximum size of grid in given dimension
     * @return a 2d grid restricted on a given dimension
     * @since 1.5
     */
    public static ArrayList<String> limit_dimension(List<String> screen, boolean dimensionX, boolean tendRightBot, int size) {
	ArrayList<String> res = new ArrayList<String>();

	if(dimensionX) {
	    //cut things from the front or back
	    if(tendRightBot) {
		for(String s : screen)
		    if(s.length() > size) 
			res.add(s.substring(s.length() - size));
		    else
			res.add(s);		    		
	    }
	    else
		for(String s : screen)
		    res.add(s.substring(0, Math.min(s.length(), size)));
	}
	if(!dimensionX) {
	    if(tendRightBot) {
		var iterator = screen.listIterator(Math.max(0, screen.size() - size));
		while(iterator.hasNext())
		    res.add(iterator.next());				
	    }
	    else {
		for(String val : screen) {
		    if(size-- <= 0)
			break;
		    else
			res.add(val);		    
		}
	    }
	}
	
	return res;
    }
    
    /**
     * Gets the boundary (top left, bottom right) space of an intpair set
     */
    private static IntPair boundary(Set<IntPair> set, boolean min) {
	boolean first = true;
	int x = 0;
	int y = 0;

	for(IntPair p : set) {
	    if(first) {
		x = p.X;
		y = p.Y;
		first = false;
	    }
	    else if (min) {
		x = Math.min(x, p.X);
		y = Math.min(y, p.Y);
	    }
	    else {
		x = Math.max(x, p.X);
		y = Math.max(y, p.Y);
	    }
	}

	return new IntPair(x, y);
    }

    
    /**
     * Sets the bit at a given position of an int
     * @param val value to modify
     * @param index index (0=rightmost) of the bit to set
     * @param active true to set bit to 1, false to set to 0
     * @return A copy of val with the one bit changed
     * @since 1.0
     */
    public static final long setBit(long val, int index, boolean active) {
	long bit = (1l << index);
	if(active)
	    return val | bit;
	else
	    return val & ~bit;
    }

    /**
     * Sets the bit at a given position of an int
     * @param val value to modify
     * @param index index (0=rightmost) of the bit to set
     * @param active true to set bit to 1, false to set to 0
     * @return A copy of val with the one bit changed
     * @since 1.0
     */
    public static final int setBit(int val, int index, boolean active) {
	int bit = (1 << index);
	if(active)
	    return val | bit;
	else
	    return val & ~bit;
    }

    /**
     * Finds the longest common substring of a set of strings
     * @param params set of strings to search
     * @return longest common substring of the set
     * @since 1.8
     */
    public static final TreeSet<String> longestSubString(String... params) {
	Trie t = new Trie();
	for(int i = 0; i < params.length; i++)
	    t.add(params[i]);

	return t.longest();
    }
    
    /**
     * Compares features and returns an integer value.
     * @param params A sequence of parameters to compare, of the form [type(a), type(a)]*
     * @return The first non-zero comparison in the series
     * @since 1.2
     */
    @SuppressWarnings("unchecked")
    public static final int compareTo(Comparable... params) {
	//comparing nothing will always give back no
	if(params.length == 0)
	    return 0;

	if(params.length %2 == 1)
	    throw new IllegalArgumentException("Util.compareTo(params): [comparableTypeA, comparableTypeA]"
					       + " (param count must be an even number (01))");

	int res = 0;
	for(int i = 0; i < params.length; i+= 2) {
	    try {
		//how do we deal with null values?
		//if only the first item is null, we can try (-1 * (+1.compareTo(0)))
		if(params[i] == null)
		    res = -1 * params[i+1].compareTo(params[i]);
		else
		    res = params[i].compareTo(params[i+1]);
		
		if(res != 0)
		    return res;
	    } catch (Exception e) {
		String c1 = (params[i] == null ? "NULL" : params[i].getClass().toString());
		String c2 = (params[i+1] == null ? "NULL" : params[i+1].getClass().toString());
		//if the comparison is invalid, we just need to let it be known
		String exc = String.format("Failed comparison at index (%d/+1), due to an error comparing types"
					   + " '%s' and '%s'. Was the input well formed? Given exception read as:"
					   + " '%s'", i, c1, c2);
		throw new IllegalArgumentException(exc);
	    }
	}
	return res;
    }
    
    /**
     * Reduces a matrix into an identity, if possible. Gives all values which can be singly determined.
     *
     * @param values The values to reduce.
     * @return A list, T, containing (in index) all elements which can be ordered
     * @since 1.0
     */
    public static final <T> ArrayList<T> singleElim(ArrayList<ArrayList<T>> values) {
	ArrayList<T> t = new ArrayList<T>(values.size());

	ArrayList<ArrayList<T>> re = new ArrayList<>();
	for(var v : values)
	    re.add(new ArrayList<T>(v));
	
	for(int i = 0; i < values.size(); i++)
	    t.add(null);
	
	while(true) {
	    T singleton = null;
	    for(int i = 0; i < values.size(); i++) {
		if(values.get(i).size() == 1) {
		    singleton = values.get(i).get(0);
		    t.set(i, values.get(i).get(0));
		    break;
		}
	    }

	    if(singleton == null)
		break;
	    
	    for(var li : values)
		li.remove(singleton);
	}

	values.clear();
	
	for(var v : re)
	    values.add(v);

	return t;
    }
    
    /**
     * Helper interface for combinations function. Given two T, give back a T
     * @author      NB Kelly <nbkelly @ protonmail.com>
     * @version     1.0
     * @since       1.0
     */
    public interface Combinator<T> {
	TreeSet<T> combinations(T val, T component);
    }


    /**
     * Gets all combinations/permutations of a list, based on a combinator and seed value
     *
     * @param seed seed value to base permutations on
     * @param components components to permute
     * @param combinator anonymous class that generates permutations based on a value and component
     * @return TreeSet T of permutations
     * @since 1.0
     */
    public static final <T> TreeSet<T> combinations(T seed, ArrayList<T> components, Combinator<T> combinator) {
	TreeSet<T> vals = new TreeSet<T>();
	vals.add(seed);
	
	for(T component : components) {
	    TreeSet<T> res = new TreeSet<T>();
	    for(T v : vals)
		res.addAll(combinator.combinations(v, component));
	    vals.addAll(res);
	}

	return vals;
    }
    
    /**
     * Converts long[] to BigInteger[]
     *
     * @param a array to convert
     * @return BigInteger[] of input
     * @since 1.0
     */
    public static final BigInteger[] bigIntArray(long[] a) {
	BigInteger[] res  = new BigInteger[a.length];
	for(int i = 0; i < a.length; i++)
	    res[i] = BigInteger.valueOf(a[i]);
	return res;
    }

    /**
     * List[BigInteger] to BigInteger[]
     *
     * @param a list to convert
     * @return BigInteger[] of input
     * @since 1.1
     */
    public static final BigInteger[] bigIntArray(List<BigInteger> a) {
	BigInteger[] res  = new BigInteger[a.size()];
	int counter = 0;
	for(BigInteger b : a) {
	    res[counter++] = b;
	}

	return res;
    }

    /**
     * Converts int[] to BigInteger[]
     *
     * @param a array to convert
     * @return BigInteger[] of input
     * @since 1.0
     */
    public static final BigInteger[] bigIntArray(int[] a) {
	BigInteger[] res  = new BigInteger[a.length];
	for(int i = 0; i < a.length; i++)
	    res[i] = BigInteger.valueOf(a[i]);
	return res;
    }

    /**
     * Finds the 2-sum of a sequence of numbers, if it exists.
     * <p>
     * Finds the 2-sum of a sequence of numbers, if it exists. The 2-sum is two numbers in the list
     * which sum up to the given target. 
     *
     * @param values the values for which to find a 2sum
     * @param target the target sum to find
     * @return Pair(T) containing the first two unique values which to the target, or null
     * @since 1.1
     */
    @SuppressWarnings("unchecked")
    public static final <T extends Number> Pair<T, T> twoSum(T[] values, T target) {
	//assertion: each value in values is unique
	HashSet<T> hashed_values = new HashSet<T>();

	
	for(int index = 0; index < values.length; index++)
	    hashed_values.add(values[index]);

	checking:
	for(int index = 0; index < values.length; index++) {
	    //if hashed_values contains target - value[index], then value[index] + value are the right targets
	    var check_target = add(target, neg(values[index]));
	    if(hashed_values.contains(check_target)) {
		//check that values[index] != check_target
		if(check_target.equals(values[index]))
		    continue checking;

		//assert check_target.getClass() == T.class;
		return new Pair<T, T>(values[index], (T)check_target);
	    }
	}


	//what are the requrements for 2sum? we need to find that target - valueA = valueB
	return null;
    }

    /**
     * Returns Number * -1 for generic classes of type Number
     *
     * @param a number to multiply by -1
     * @return a * -1
     * @since 1.1
     */
    private static final <T extends Number> Number neg(T a)
	throws IllegalArgumentException {
	if(a.getClass() == Short.class)
	    return (Short)a *(Short)(short)(-1);// (Short)b;
	else if(a.getClass() == Long.class)
	    return (Long)a * -1l;
	else if(a.getClass() == Integer.class)
	    return (Integer)a * -1;
	else if(a.getClass() == Float.class)
	    return (Float)a * -1f;
	else if(a.getClass() == Double.class)
	    return (Double)a *1d;
	else if(a.getClass() == Byte.class)
	    return (Byte)a * (Byte)(byte)-1;
	else if(a.getClass() == BigInteger.class)
	    return ((BigInteger)a).multiply(BigInteger.valueOf(-1));
	else if(a.getClass() == BigDecimal.class)
	    return ((BigDecimal)a).multiply(BigDecimal.valueOf(-1));

	throw new IllegalArgumentException("Operands of type " + a.getClass() + " are not supported");
	//	return null;

    }

    /**
     * Returns NumberA * NumberB for generic classes of type Number
     *
     * @param a number to add
     * @param b number to add
     * @return a +b
     * @since 1.1
     */
    private static final <T extends Number> Number add(T a, T b)
	throws IllegalArgumentException  {
	assert a.getClass() == b.getClass(): "Type mismatch with generic add(a, b)";; //I think this is mandatory?

	if(a.getClass() == Short.class)
	    return (Short)a + (Short)b;
	else if(a.getClass() == Long.class)
	    return (Long)a + (Long)b;
	else if(a.getClass() == Integer.class)
	    return (Integer)a + (Integer)b;
	else if(a.getClass() == Float.class)
	    return (Float)a + (Float)b;
	else if(a.getClass() == Double.class)
	    return (Double)a + (Double)b;
	else if(a.getClass() == Byte.class)
	    return (Byte)a + (Byte)b;
	else if(a.getClass() == BigInteger.class)
	    return ((BigInteger)a).add((BigInteger)b);
	else if(a.getClass() == BigDecimal.class)
	    return ((BigDecimal)a).add((BigDecimal)b);

	throw new IllegalArgumentException("Operands of type " + a.getClass() + " are not supported");
	//	return null;
    }
    
    /**
     * Gets the answer to chineseRemainder on input
     *
     * <p>Specifically, finds K such that nom % k = denom
     *
     * @param nom argument numbers
     * @param denom expected denominators
     * @return K such that nom % k = denom for all nom-denom
     * @since 1.0
     */
    public static final BigInteger chineseRemainder(BigInteger[] nom, BigInteger[] denom) {
	BigInteger product = Arrays.stream(nom).reduce(BigInteger.ONE, (i, j) -> i.multiply(j));

	BigInteger p = BigInteger.ZERO;
	BigInteger sm = BigInteger.ZERO;

	for(int i = 0; i < nom.length; i++) {
	    p = product.divide(nom[i]);
	    sm = sm.add(denom[i].multiply(bat_soup(p, nom[i])).multiply(p));
	}

	return sm.mod(product);
    }

    /**
     * Used by chinese remainder
     * @since 1.0
     */
    private static final BigInteger bat_soup(BigInteger a, BigInteger b) {
	BigInteger b0 = b;
	BigInteger x0 = BigInteger.ZERO;
	BigInteger x1 = BigInteger.ONE;

	if(b.equals(BigInteger.ONE))
	    return b;

	while (a.compareTo(BigInteger.ONE) > 0) {
	    BigInteger q = a.divide(b);

	    BigInteger tmp = a.mod(b);
	    a = b;
	    b = tmp;

	    tmp = x1.subtract(q.multiply(x0));
	    x1 = x0;
	    x0 = tmp;
	}

	if(x1.compareTo(BigInteger.ZERO) < 0)
	    x1 = x1.add(b0);

	return x1;
    }

    /**
     * Searches a string for substrings using the Knuth-Morris-Pratt algorithm.
     * @param text text to search
     * @param target sequence to find in the text
     * @return All indices of target in text
     */
    public static ArrayList<Integer> KMP_search(String text, String target) {
	int[] failure = failure(target);
	int index_target = 0;	
	int index_text = 0;
	
	ArrayList<Integer> res = new ArrayList<Integer>();

	while(index_text < text.length()) {
	    if(text.charAt(index_text) == target.charAt(index_target)) {
		index_text++;
		index_target++;
	    }

	    if(index_target ==target.length()) {
		res.add(index_text - index_target);
		index_target = failure[index_target - 1];		
	    }
	    else if (index_text < text.length()
		     && target.charAt(index_target) != text.charAt(index_text)) {
		if(index_target != 0)
		    index_target = failure[index_target - 1];
		else
		    index_text++;
	    }		     
	}

	return res;
    }

    private static class Fragment {
	String value = "";
	int length;
	Fragment last;
	Fragment last_branch;
	
	public Fragment() {}

	public Fragment(Fragment last, String seq) {
	    this.last = last;
	    this.length = last.length + seq.length();
	    this.value = seq;
	}

	public Fragment(Fragment last, Fragment last_two, String seq) {
	    //perform the length comparison here
	    if(last.length > last_two.length)
		this.last = last;
	    else if (last_two.length > last.length)
		this.last = last_two;
	    else {
		//equal
		this.last = last;
		this.last_branch = last_two;
	    }
	    this.length = last.length + seq.length();
	    this.value = seq;
	}
    }

    /**
     * Longest common subsequences of two strings.
     *
     * @param top first string to check
     * @param left second string to check
     * @param max_branches maximum number of strings to return
     * @return Set of the longest common subsequences of the two strings
     */
    public static TreeSet<String> longestCommonSubsequence(String top, String left, int max_branches) {
	Fragment[] last = new Fragment[left.length() + 1];
	for(int i = 0; i <= left.length(); i++)
	    last[i] = new Fragment();

	for(int t = 1; t <= top.length(); t++) {
	    char top_char = top.charAt(t-1);
	    Fragment[] next = new Fragment[left.length() + 1];
	    next[0] = new Fragment();
	    	    
	    for(int l = 1; l <= left.length(); l++) {
		char left_char = left.charAt(l-1);
		if(left_char == top_char)
		    next[l] = new Fragment(last[l-1], String.valueOf(left_char));
		else {
		    var t1 = last[l];
		    var t2 = next[l-1];
		    if(t1.length == t2.length)
			next[l] = new Fragment(t1, t2, "");
		    else if (t1.length > t2.length)
			next[l] = t1;
		    else
			next[l] = t2;
		}
	    }
	    last = next;
	}

	TreeSet<String> res = new TreeSet<String>();
	LinkedList<Pair<Fragment, String>> queue = new LinkedList<>();
	queue.push(new Pair<Fragment, String>(last[left.length()], ""));

	int branches = 0;
	while(!queue.isEmpty()) {
	    var pair = queue.pollFirst();
	    var frag = pair.X;
	    var str = frag.value + pair.Y;

	    if(frag.last != null) {
		queue.push(new Pair<Fragment, String>(frag.last, str));
		if(frag.last_branch != null && branches++ < max_branches)
		    queue.push(new Pair<Fragment, String>(frag.last_branch, str));
	    }
	    else		
		res.add(str);
	}
	
	return res;
    }
    
    /**
     * Gets the KMP failure array for a string
     * @param target string to get failure array for
     * @return failure array for string target
     */
    public static int[] failure(String target) {
	int[] res = new int[target.length()];

	int size = 0;
	int index = 1;
	while(index < target.length())
	    if(target.charAt(index) == target.charAt(size))
		res[index++] = ++size;
	    else if (size > 0)
		size = res[size - 1];
	    else
		res[index++] = size;

	return res;	
    }
}
