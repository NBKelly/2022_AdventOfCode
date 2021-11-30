package com.nbkelly.lib.rev;

import java.util.ArrayList;

public class Permutation implements Comparable<Permutation>{
    int[] seq;
    int len;
    ArrayList<Integer> breakpoints;
    int[] swaps;

    /**
     * Creates a new permutation with no history (swaps).
     *
     * @param seq permutation sequence
     * @param len length of sequence
     * @param breakpoints breakpoints of this permutation
     */
    public Permutation(int[] seq, int len, ArrayList<Integer> breakpoints) {
	this.seq = seq;
	this.len = len;
	this.breakpoints = breakpoints;

	swaps = new int[0];
    }

    /**
     * Creates a new permutation with history (swaps) based on a pervious permutation
     *
     * @param last the last (parent) permutation
     * @param seq permutation sequence
     * @param len length of sequence
     * @param breakpoints breakpoints of this permutation
     * @param left_swap the left interval of the swap
     * @param right_swap the right interval of the swap
     */
    public Permutation(Permutation last, int[] seq, int len, ArrayList<Integer> breakpoints,
		       int left_swap, int right_swap) {
	this.seq = seq;
	this.len = len;
	this.breakpoints = breakpoints;

	swaps = new int[last.swaps.length + 2];

	for(int i = 0; i < last.swaps.length; i++)
	    swaps[i] = last.swaps[i];

	swaps[swaps.length-2] = left_swap;
	swaps[swaps.length-1] = right_swap;
    }

    /**
     * Compares this permutation to another permutation
     *
     * @param p permutation to compare to
     * @return (this &gt; p)? 1, otherwise 0 or -1
     */
    public int compareTo(Permutation p) {
	for(int i = 0; i < len; i++) {
	    int res = Integer.compare(seq[i], p.seq[i]);
	    if(res != 0)
		return res;
	}
	return 0;
    }

    /**
     * Returns true if given permutation is equal to this permutation. To be equal,
     * the permutations must have identical sequences.
     * 
     * @param p permutation to compare to
     * @return true if given permutation is equal to this permutation
     */
    public boolean equals(Permutation p) {
	if(p.len != len)
	    return false;
	
	for(int i = 0; i < len; i++)
	    if(seq[i] != p.seq[i])
		return false;
	
	return true;
    }
    
    /**
     * Returns true if given object is equal to this permutation
     *
     * @param o Object to compare
     * @return true if given object is equal to this permutation
     */
    @Override public boolean equals(Object o) {
	if(o instanceof Permutation) {
	    ((Permutation) o).equals(this);
	}
	return false;
    }
}
