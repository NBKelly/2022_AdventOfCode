package com.nbkelly.lib.rev;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;
import java.util.TreeSet;

/**
 * @author NB Kelly <N.B.Kelly@protonmail.com>
 * @date 28 May, 2020
 */
public class RevUtil {
    //dissalow instanciation
    private RevUtil() {}

    /*
     * M E A T
     */ 
    
    public static int[] calculate_unsigned_reversal_sequence(int[] sequence, int[] target, int len) {
	/* we may need to do no work at all */
	if(RevUtil.eq(sequence, target, len))
	    return new int[0];
	
	/* this should give us a sequence with length == len +2 */
	int[] m_sequence = map2_signed(sequence, target, len);	
	int m_len = len+2;

	/* give us the identity of our target */
	int[] id = id(m_len);

	/* find all of the breakpoints */
	ArrayList<Integer> breakpoints = RevUtil.breakpoints_unsigned(m_sequence, m_len);

	/* get the starting permutation */
	Permutation perm = new Permutation(m_sequence, m_len, breakpoints);

	/* populate the set of current permutations */
	TreeSet<Permutation> current_perms = new TreeSet<>();
	current_perms.add(perm);

	/* keep track of permutation we have visited */
	TreeSet<Permutation> visited = new TreeSet<>();

	/* keep track of permutation for the next iteration */
	TreeSet<Permutation> next_perms = new TreeSet<>();

	/* track distance */
	int dist = 0;
	
	/* continue until certain conditions have been met */
	while(true) {
	    int lowest = 99;

	    /* consume every element in the current set */
	    while(current_perms.size() > 0) {
		Permutation p = current_perms.pollFirst();
		if(visited.contains(p))
		    continue;
		visited.add(p);
		
		/* if we have the id, then we have finished */
		if(RevUtil.eq(p.seq, id, m_len)) {
		    return p.swaps;
		}

		int break_size = p.breakpoints.size();

		if(break_size == 0)
		    return p.swaps;

		if(break_size < lowest)
		    lowest = break_size;
		
		next_perms.addAll(good_permutations(p));
	    }
	    
	    dist++;

	    current_perms = next_perms;
	    next_perms = new TreeSet<>();
	}
    }
    
    public static int calculate_unsigned_reversal_distance(int[] sequence, int[] target, int len) {
	/* we may need to do no work at all */
	if(RevUtil.eq(sequence, target, len))
	    return 0;
	
	/* this should give us a sequence with length == len +2 */
	int[] m_sequence = map2_signed(sequence, target, len);	
	int m_len = len+2;

	/* give us the identity of our target */
	int[] id = id(m_len);

	/* find all of the breakpoints */
	ArrayList<Integer> breakpoints = RevUtil.breakpoints_unsigned(m_sequence, m_len);

	/* get the starting permutation */
	Permutation perm = new Permutation(m_sequence, m_len, breakpoints);

	/* populate the set of current permutations */
	TreeSet<Permutation> current_perms = new TreeSet<>();
	current_perms.add(perm);

	/* keep track of permutation we have visited */
	TreeSet<Permutation> visited = new TreeSet<>();

	/* keep track of permutation for the next iteration */
	TreeSet<Permutation> next_perms = new TreeSet<>();

	/* track distance */
	int dist = 0;
	
	/* continue until certain conditions have been met */
	while(true) {
	    int lowest = 99;

	    /* consume every element in the current set */
	    while(current_perms.size() > 0) {
		Permutation p = current_perms.pollFirst();
		if(visited.contains(p))
		    continue;
		visited.add(p);

		/* if we have the id, then we have finished */
		if(RevUtil.eq(p.seq, id, m_len)) {
		    return dist;
		}

		int break_size = p.breakpoints.size();

		if(break_size == 0)
		    return dist;

		if(break_size < lowest)
		    lowest = break_size;
		
		next_perms.addAll(good_permutations(p));
	    }
	    
	    dist++;

	    current_perms = next_perms;
	    next_perms = new TreeSet<>();
	}
    }

    /**
     * Finds the set of all permutations which reduce the breakpoint count of the current permutation.
     *
     * @param perm the starting permutation
     * @return a set of all better permutations
     */
    private static TreeSet<Permutation> good_permutations(Permutation perm) {
	//TODO: make it actually be greedy, rather than optimistic
	int[] seq = perm.seq;
	int len = perm.len;

	TreeSet<Permutation> perms = new TreeSet<Permutation>();
	for(int left = 0; left < len; left++) {
	    for(int right = left + 1; right < len; right++) {
		
		int[] np = reversal_unsigned(perm.seq, left, right, len);
		var breakpoints = breakpoints_unsigned(np, len);
		if(breakpoints.size() < perm.breakpoints.size()) {
		    Permutation newPerm = new Permutation(perm, np, len, breakpoints,
							  left, right);
		    perms.add(newPerm);
		}
	    }		
	}

	return perms;
    }
    
    /**
     * Finds the (signed) reversal distance between two permutations
     *
     * @param sequence sequence to search from
     * @param target sequence to discover the path to
     * @param len length of the sequences
     *
     * @return the (signed) reversal distance between the sequence
     */
    public static int calculate_signed_reversal_distance(int[] sequence, int[] target, int len) {		
	/* Step One: m_sequence needs to be 'standardized' for our algorithm (we calculate against identity) */	
	/* this should give us a sequence with length == len +2 */
	int[] m_sequence = map2_signed(sequence, target, len);
	int m_len = len+2;
	//printArr(m_sequence, m_len);

	/* give us the identity of our target */
	int[] id = id(m_len);

	/* find all of the breakpoints */
	ArrayList<Integer> breakpoints = breakpoints_signed(m_sequence, m_len);
	
	/* find all the intervals based on the breakpoints we calculated */
	ArrayList<Interval> inter = intervals(m_sequence, m_len);
	
	/* based on the intervals we calculated, find all our cycles */
	var cycles = cycles(inter);
	
	/* based on the cycles we've created, we can create distinct components */
	var components = RevUtil.components(cycles, m_sequence, m_len);
	
	/* having components lets us build a component tree */
	ComponentTree ct = new ComponentTree(components, breakpoints, m_sequence, m_len);
	
	/* we reduce the component tree to the minimal tree containing all unoriented nodes */
	ComponentTree reduced = ct.TreeDash();
	
	/* based on the leaf count and wether there is a short branch, we can determine reversal distance */
	int leaves = reduced.leaves();
	int tr_score = 0;

	if(leaves %2 == 0)
	    tr_score = leaves;
	else {
	    /* detect if the tree has a short branch and use that to assign the reversal score*/
	    if(reduced.has_short_branch())
		tr_score = leaves + 1;
	    else
		tr_score = leaves + 2;
	}
	
	/* score = size of alphabet (len-1) - num_cycles + tr_score */
	int reversal_score = ((m_len - 1) - cycles.size() + tr_score);

	return reversal_score;
    }
  
    /*
     * M I S C
     */
    
    /**
     * Determine if two sequences are equal.
     * <p>
     * @param sequence first sequence to compare
     * @param target second sequence to compare
     * @param len the length of the sequences
     *
     * @return true if sequences match, false otherwise
     */
    public static boolean eq(int[] sequence, int[] target, int len) {
	return eq(sequence, target, 0, len);
    }

    /**
     * Determine if two sequences are equal within an interval.
     * <p>
     * @param sequence first sequence to compare
     * @param target second sequence to compare
     * @param start beginning (inclusive) of comparison
     * @param end the end (exclusive) of comparison
     *
     * @return true if sequences match within interval, false otherwise
     */
    public static boolean eq(int[] sequence, int[] target, int start, int end) {
	for(int i = start; i < end; i++) {
	    if(sequence[i] != target[i])
		return false;
	}

	return true;
    }

    /**
     * Returns all items from a list between (excluisive) an interval
     * @param li the list to filter
     * @param left left index to filter
     * @param right right index to filter
     */
    public static LinkedList<Integer> filter(List<Integer> li, int left, int right) {
	LinkedList<Integer> res = new LinkedList<>();
	for(int i : li) {
	    if(i > left && i < right)
		res.add(i);
	}

	return res;
    }

    /**
     * Returns the identity sequence of the given length.
     * <p>
     * Returns the identity sequence of the given length. The identity sequence is defined as
     * [0,1,2,...,n] for n &ge; 0.
     * @param len lenth of sequence
     * @return identity sequence of length n
     */
    private static int[] id(int len) {
	int[] id = new int[len];
	for(int i = 0; i < len; i++)
	    id[i]=i;
	return id;
    }

    
    /**
     * Pad every line of a string with a quantity of whitespace
     * @param s string to pad
     * @param count quantity of whitespace to pad block of text with
     * @return string block padded with whitespace
     */
    public static String padlines(String s, int count) {
	String ct = "";
	for(int i = 0; i < count; i++) ct += " ";
	s = s.replaceAll("\n", "\n" + ct);
	s = ct + s;
	return s;
    }


    /*
     * C O M P O N E N T S
     */

    /**
     * Constructs components from a list of cycles.
     * <p>
     * Constructs components from a list of cycles and a sequence. A component is a parented metatree structure
     * of nested cycles.
     *
     * @param cycles list of cycles to process
     * @param sequence the sequence to process
     * @param len length of the sequence
     * @return a list of parent components produced from composition of the given set of cycles
     */
    public static ArrayList<Component> components(List<Cycle> cycles, int[] sequence, int len) {
	LinkedList<Cycle> _cycles = new LinkedList<>();
	_cycles.addAll(cycles);

	//now, we assert that our cycles are sorted by size
	Collections.sort(_cycles);
	ArrayList<Component> components = new ArrayList<>();
	while(_cycles.size() > 0) {
	    Cycle c_head = _cycles.poll();
	    ArrayList<Cycle> children = new ArrayList<>();
	    
	    for(int i = 0; i < _cycles.size(); i++) {
		Cycle ct = _cycles.get(i);
		if(Cycle.contains(c_head, ct)) {
		    _cycles.remove(i);
		    i--;
		    children.add(ct);
		}
	    }

	    Component comp = new Component(c_head, sequence);
	    
	    
	    if(children != null)
		comp.addChildren(components(children, sequence, len));

	    components.add(comp);
	}
	
	return components;
    }

    
    /*
     * C Y C L E S
     */
    
    /**
     * Arranges a list of component intervals into a set of cycles.
     * <p>
     * A cycle is a closed loop of intervals, where each interval maps to two other intervals
     * within the cycle. A 1-cycle maps to itself twice, A 2-cycle has each element map to the other twice,
     * and any other N-cycle has each element map to two distinct elements.
     *
     * @param intervals the list of intervals to compose into cycles
     */
    public static ArrayList<Cycle> cycles(List<Interval> intervals) {
	ArrayList<Cycle> cycles = new ArrayList<>();

	LinkedList<Interval> _intervals = new LinkedList<>(intervals);

	
	while(_intervals.size() > 0) {
	    Cycle cycle = new Cycle();
	    cycles.add(cycle);
	    
	    Interval first = _intervals.poll();
	    cycle.add(first);

	    if(first.left_index() == first.right_index()) {
		continue;
	    }
	    else {
		//find all r-linked elements
		Interval current = first;
		Interval last = current;
		//because we removed the interval we have just seen, there should only be one result to find
		//the element
		while((current  = RevUtil.find_interval(_intervals, last.right_index())) != null
		      || (current = RevUtil.find_interval(_intervals, last.left_index())) != null) {
		    cycle.add(current);
		    _intervals.remove(current);
		    last = current;
		}
	    }
	}

	return cycles;
    }    


    /*
     * I N T E R V A L S
     */
    
    /**
     * Find all of the intervals within a sequence.
     * <p>
     * An interval is defined as the path between two (expected) consecutive elements
     * within the permutation (with respect to the identity).
     * <p>
     * From "Reversal Distance without Hurdles and Fortresses", <i>For each pair of unsigned elements (k, k+1),
     *  0 &le; k &le; n, define the elementary interval I<sub>k</sub> associated to the pair to be the interval
     *  whose endpoints are:<p>
     *  1) The right point of k, if k is positive, otherwise its left point <br>
     *  2) The left point of k + 1, if k + 1 is positive, otherwise its right point.</i>
     *  <p>
     *  @param sequence the sequence for which to find intervals
     *  @param len the length of the sequence
     *  @return all of the intervals within the sequence
     */ 
    public static ArrayList<Interval> intervals(int[] sequence, int len) {
	ArrayList<Interval> intervals = new ArrayList<Interval>();
	
	//an interval exists for each point. let us begin:
	//for each pair of unsigned elements
	int left = -1;
	int right = -1;
	for(int j = 0; j +1 < len; j++) {
	    int i = RevUtil.find_signed(sequence, j, len);
	    int k = sequence[i];

	    //find abs(abs(i) + 1)
	    int target = Math.abs(k) + 1;
	    int k2 = RevUtil.find_signed(sequence, target, len);

	    //now define points
	    if (k >= 0)
		//the interval goes from here, right of k
		left = i;
	    else
		//interval is on the left of k
		left = i-1;

	    //if s(k2) is positive, we take it's left point
	    if(sequence[k2] >= 0)
		right = k2 - 1;
	    //otherwise we take the right point
	    else
		right = k2;

	    boolean oriented = ((k >= 0 && k2 < 0) || (k2 >= 0 && k < 0));
	    Interval interval = new Interval(Math.min(left, right), Math.max(left, right), oriented);
	    intervals.add(interval);	    
	}

	return intervals;	    
    }

    /**
     * Finds the first interval within a set of intervals which matches the target index.
     * <p>
     * @param intervals list of intervals to search
     * @param target_index the target index to match with
     *
     * @return Interval the matched interval, or null if none was found
     */
    public static Interval find_interval(List<Interval> intervals, int target_index) {
	for(Interval i : intervals) {
	    if(i.matches(target_index))
		return i;
	}

	return null;
    }

    /**
     * Finds all interval within a set of intervals which match the target index.
     * <p>
     * @param intervals list of intervals to search
     * @param target_index the target index to match with
     *
     * @return List of intervals which match the target index
     */
    public static ArrayList<Interval> findAll(List<Interval> intervals, int target_index) {
	ArrayList<Interval> res = new ArrayList<>();

	for(Interval i : intervals) {
	    if(i.matches(target_index))
		res.add(i);
	}

	return res;
    }


    /*
     * B R E A K  P O I N T S - A D J A C E N C I E S
     */

    /**
     * Finds all the adjacencies in a sequence of numbers.
     * <p>
     * Finds all the adjacencies in a sequence of numbers. An breakpoint
     * exists at a point p.q when q - p == 1. Adjacencies are identical for signed and unsigned
     * permutations.
     *
     * @param sequence the sequence for which to find adjacencies
     * @param len the length of the sequence
     * @return all of the adjacencies in the given sequence
     */
    public static ArrayList<Integer> adjacencies(int[] sequence, int len) {
	ArrayList<Integer> adjacencies = new ArrayList<Integer>();

	//with the new modified alphabet, we need only check diff (i, i+1) > 1
	for(int i = 0; i + 1 < len; i++) {
	    int left = sequence[i];
	    int right= sequence[i+1];
	    int diff = right - left;
	    if(diff == 1) {
		adjacencies.add(i);
	    }
	}

	return adjacencies;

    }
    
    /**
     * Finds all the (signed) breakpoints in a sequence of numbers.
     * <p>
     * Finds all the (signed) breakpoints in a sequence of numbers. A signed breakpoint
     * exists at a point p.q when q - p != 1.
     *
     * @param sequence the sequence for which to find breakpoints
     * @param len the length of the sequence
     * @return all of the (signed) breakpoints in the given sequence
     */
    public static ArrayList<Integer> breakpoints_signed(int[] sequence, int len) {
	ArrayList<Integer> breakpoints = new ArrayList<Integer>();
	
	//with the new modified alphabet, we need only check diff (i, i+1) > 1
	for(int i = 0; i + 1 < len; i++) {
	    int left = sequence[i];
	    int right= sequence[i+1];
	    int diff = right - left;
	    if(diff != 1) {
		breakpoints.add(i);
	    }
	}

	return breakpoints;
    }

    /**
     * Finds all the (unsigned) breakpoints in a sequence of numbers.
     * <p>
     * Finds all the (unsigned) breakpoints in a sequence of numbers. An unsigned breakpoint
     * exists at a point p.q when (q - p)(q - p) != 1.
     *
     * @param sequence the sequence for which to find breakpoints
     * @param len the length of the sequence
     * @return all of the (unsigned) breakpoints in the given sequence
     */
    public static ArrayList<Integer> breakpoints_unsigned(int[] sequence, int len) {
	ArrayList<Integer> breakpoints = new ArrayList<Integer>();
	
	//with the new modified alphabet, we need only check diff (i, i+1) > 1
	for(int i = 0; i + 1 < len; i++) {
	    int left = sequence[i];
	    int right= sequence[i+1];
	    int diff = (right - left)*(right - left);
	    if(diff != 1) {
		breakpoints.add(i);
	    }
	}

	return breakpoints;
    }


    /*
     * O U T P U T
     */

    /**
     * Formats an array as a string.
     * <p>
     * Given an array and a length, gives all array elements up to that length as a space seperated string.
     *
     * @param arr array to format
     * @param len apparent length of array
     * @return A string representing all emements up to the length of that string
     */
    private String formatArray(int[] arr, int len) {
	StringBuilder str = new StringBuilder();
	for(int i = 0; i < len; i++)
	    str.append(String.format("%3d ", arr[i]));
	return str.toString();
    }

    /*
     * M A P S + F I N D
     */

    /**
     * Maps a signed sequence to an alphabet and expands it's edges.
     *
     * @param sequence The sequence to be mapped
     * @param alpha The alphabet to map the sequence to
     * @param len the length of the sequence
     * @return and int[] of size 2 greater than len representing the signed sequence mapped to the alphabet
     */
    public static int[] map2_signed(int[] sequence, int[] alpha, int len) {
	int[] res = new int[len+2];

	for(int i = 0; i < len; i++) {
	    int k = alpha[i];
	    int loc = find3(sequence, k, len);
	    res[loc+1] = (i+1) * (int)Math.signum(sequence[loc]);
	}

	res[len+1] = len+1;

	return res;
    }

    /**
     * Finds an occurance of a signed character within a sequence based on it's unsigned symbol
     * @param sequence the sequence to search
     * @param target symbol to find
     * @param len length of sequence
     * @return index of signed element in sequence, or -1 if it does not exist
     */
    private static int find3(int[] sequence, int target, int len) {
	for(int i = 0; i < len; i++) {
	    if(sequence[i] == target || sequence[i] == target * -1)
		return i;;
	}
	return -1;
    }

    
    /**
     * Maps a sequence to an alphabet and expands it's edges.
     *
     * @param sequence The sequence to be mapped
     * @param alpha The alphabet to map the sequence to
     * @param len the length of the sequence
     * @return and int[] of size 2 greater than len representing the original sequence mapped to the alphabet
     */
    public static int[] map2(int[] sequence, int[] alpha, int len) {
	int[] res = new int[len+2];
	
	for(int i = 0; i < len; i++) {
	    int k = alpha[i];
	    int loc = find(sequence, k, len);
	    res[loc+1] = i+1;
	}

	res[len+1] = len+1;

	return res;
    }
    
    /**
     * Maps a sequence to an alphabet.
     *
     * @param sequence The sequence to be mapped
     * @param alpha The alphabet to map the sequence to
     * @param len the length of the sequence
     * @return and int[] of equal size to the sequence representing the original sequence mapped to the alphabet
     */
    public static int[] map(int[] sequence, int[] alpha, int len) {
	int[] res = new int[len];
	for(int i = 0; i < len; i++) {
	    int k = alpha[i];
	    int loc = find(sequence, k, len);
	    res[loc] = i+1;
	}

	return res;
    }

    /**
     * finds an occurance of the target int in the given sequence
     *
     * @param sequence the sequence to search
     * @param target the int to find in the sequence
     * @param len the length of the sequence
     * 
     * @return the index of the target, or -1 if it does not exist
     */
    public static int find(int[] sequence, int target, int len) {
	for(int i = 0; i < len; i++)
	    if(sequence[i] == target)
		return i;
	return -1;
    }

    /**
     * finds a signed occurance of the target int in the given sequence.
     * <p>
     * finds a signed occurance of the target int in the given sequence.
     * This may by the target, or it's negative.
     *
     * @param sequence the sequence to search
     * @param target the int to find in the sequence
     * @param len the length of the sequence
     * 
     * @return the index of the target, or -1 if it does not exist
     */
    public static int find_signed(int[] sequence, int target, int len) {	
	for(int i = 0; i < len; i++)
	    if(sequence[i] == target || sequence[i] == target * -1)
		return i;
	return -1;
    }


    /*
     * R E V E R S A L S
     */

    //TODO: javadoc these
    //return a new int[] where a reversal occurs over start, end (signed)
    public static int[] reversal_signed(int[] sequence, int start, int end, int len, boolean in_place) {
	if(!in_place)
	    return reversal_signed_new_array(sequence, start, end, len);
	else //TODO: reversal_signed_in_place
	    return null;
    }
    
    private static int[] reversal_signed_new_array(int[] sequence, int start, int end, int len) {
	int[] rev = new int[len];

	for(int i = 0; i < len; i++)
	    rev[i] = sequence[i];
	for(int i = start, j=end; i <= end; i++, j-- ) {
	    rev[j] = sequence[i] * -1;
	}

	return rev;
    }

    public static int[] reversal_signed(int[] sequence, int start, int end, int len) {
	return reversal_signed(sequence, start, end, len, false);
    }
    
    /**
     * Given a sequence of numbers and an interval, reverses the numbers between the interval.
     * <p>
     * Given a sequence of numbers and an interval, performs an unsigned reversal on
     * the numbers between the interval. A sequence of length(len) is returned,
     * where the reversal has occured.
     * <p>
     * @param sequence the sequence to perform the reversal on
     * @param start start of the interval (inclusive) to reverse
     * @param end the end of the interval (inclusive) to reverse
     * @param len the length of the interval being operated on
     * @param in_place does this reversal occur in place, or result in a new int[]?
     * 
     * @return A sequence, being a permutation on the original on which the reversal has occured
     **/
    public static int[] reversal_unsigned(int[] sequence, int start, int end, int len, boolean in_place) {
	if(!in_place)
	    return reversal_unsigned_new_array(sequence, start, end, len);
	else //TODO: reversal_signed_in_place
	    return null;			 
    }

    private static int[] reversal_unsigned_new_array(int[] sequence, int start, int end, int len) {
	int[] rev = new int[len];
	
	//TODO: see if we can speed this up a little bit
	/*for(int i = 0, j = end; i < len; i++) {
	    if(i < start || i > end) {
		
	    }
	    }*/
	
	for(int i = 0; i < len; i++)
	    rev[i] = sequence[i];

	for(int i = start, j=end; i <= end; i++, j-- ) {
	    rev[j] = sequence[i];
	}

	return rev;
    }

    /**
     * Given a sequence of numbers and an interval, reverses the numbers between the interval.
     * <p>
     * Given a sequence of numbers and an interval, performs an unsigned reversal on
     * the numbers between the interval. A new sequence of length(len) is returned,
     * where the reversal has occured.
     * <p>
     * @param sequence the sequence to perform the reversal on
     * @param start start of the interval (inclusive) to reverse
     * @param end the end of the interval (inclusive) to reverse
     * @param len the length of the interval being operated on
     * 
     * @return a new sequence, being a permutation on the original on which the reversal has occured
     **/
    public static int[] reversal_unsigned(int[] sequence, int start, int end, int len) {
	return reversal_unsigned(sequence, start, end, len, false);
    }
}
