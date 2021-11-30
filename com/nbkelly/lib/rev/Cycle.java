package com.nbkelly.lib.rev;

import java.util.ArrayList;

/**
 * @author NB Kelly <N.B.Kelly@protonmail.com>
 * @date 28 May, 2020
 * A cycle representing a linked set of intervals over a permutation
 */
public class Cycle implements Comparable<Cycle>  {
    ArrayList<Interval> intervals = new ArrayList<>();
    boolean bounds_fresh = false;
    int l_bound_cached = 0;
    int r_bound_cached = 0;

    /**
     * Our constructor is a 'do-nothing' constructor
     */
    public Cycle() {
	
    }


    /**
     * Recalculates and caches bounds
     */
    private void freshen() {
	if(!bounds_fresh)
	    calculate_bounds();
    }

    /**
     * Returns the left bound of this cycle.
     * @return the left bound of this cycle
     */
    public int l_bound() {
	freshen();
	return l_bound_cached;
    }

    /**
     * Returns the right bound of this cycle.
     * @return the right bound of this cycle
     */
    public int r_bound() {
	freshen();
	return r_bound_cached;
    }

    /**
     * calculates the bounds of this cycle and then caches the result
     */
    private void calculate_bounds() {
	//get min value
	int min = intervals.get(0).left_index();
	int max = min;
	for(Interval i : intervals) {
	    min = Math.min(Math.min(i.left_index(), i.right_index()), min);
	    max = Math.max(Math.max(i.left_index(), i.right_index()), max);
	}
	
	l_bound_cached = min;
	r_bound_cached = max;
	bounds_fresh = true;
    }

    /**
     * Adds an interval to this cycle
     * @param interval the interval to add
     */
    public void add(Interval interval) {
	intervals.add(interval);
	bounds_fresh = false;
    }

    /**
     * Returns the size (count of components) of this cycle
     * @return the size (count of components) of this cycle
     */
    public int size() {
	return intervals.size();
    }

    /**
     * Returns a string representation of this interval
     * @return a string representation of this interval
     */
    public String toString() {
	freshen();
	
	return String.format("(%d, %d) : %s", l_bound_cached, r_bound_cached, intervals.toString());
    }
    
    /**
     * Compares this interval to another interval based on size. This is primarily in for sorting purposes.
     * @param c the cycle to compare to
     * @return (c::size compare size)
     * @note: we only compare length. x compare y == 0 !implies x == y
     */
    public int compareTo(Cycle c) {
	int res = Integer.compare(size(), c.size());
	return res * -1;
    }


    /**
     * Determine if this cycle contains an inner cycle
     * <p>
     * Cycle A is said to contain Cycle B when
     * A_Left_bound &lt; B_Left_bound &lt; B_Right_bound &lt; A_Right_bound.
     * @param inner assumed child cycle
     * @return true if this cycle contains the inner cycle, false otherwise
     */
    public boolean contains(Cycle inner) {
	return contains(this, inner);
    }

    /**
     * Determine if an outer cycle contains an inner cycle
     * <p>
     * Cycle A is said to contain Cycle B when
     * A_Left_bound &lt; B_Left_bound &lt; B_Right_bound &lt; A_Right_bound.
     * @param outer assumed parent cycle
     * @param inner assumed child cycle
     * @return true if the outer cycle contains the inner cycle, false otherwise
     */
    public static boolean contains(Cycle outer, Cycle inner) {
	return outer.l_bound() < inner.l_bound() && outer.r_bound() > inner.r_bound();
    }

}
