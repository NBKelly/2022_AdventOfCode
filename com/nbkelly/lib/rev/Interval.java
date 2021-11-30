package com.nbkelly.lib.rev;

/**
 * @author NB Kelly <N.B.Kelly@protonmail.com>
 * @date 28 May, 2020
 * An Interval represents a string of numbers within a permutation.
 */
public class Interval {
    int left_index;
    int right_index;
    boolean oriented = false;
    //extremities have different signs == oriented

    /**
     * Returns the left index of this interval
     * @return the left index of this interval
     */
    public int left_index() {
	return left_index;
    }

    /**
     * Returns the right index of the interval
     * @return the right index of this interval
     */
    public int right_index() {
	return right_index;
    }

    /**
     * Returns true if this interval is oriented.
     * An interval is considered to be oriented when the extremities have different signs.
     * @return true if this interval is oriented
     */
    public boolean oriented() {
	return oriented;
    }

    /**
     * Creates an interval from a pair of indices and knowledge of orientation.
     *
     * @param left_index the left index of this interval
     * @param right_index the right index of this interval
     * @param oriented the orientation status of this interval
     */
    public Interval(int left_index, int right_index, boolean oriented) {
	this.left_index = left_index;
	this.right_index = right_index;
	this.oriented = oriented;
    }

    /**
     * Returns true if this interval lines up with a given index.
     *
     * @param index the index to check
     * @return true if this interval lines up with the given index
     */
    public boolean matches(int index) {
	return index == left_index || index == right_index;
    }

    /**
     * Returns the string representation of this interval
     * @return the string representation of this interval
     */
    public String toString() {
	if(oriented)
	    return String.format("+i[%d, %d]", left_index, right_index);
	else
	    return String.format("-i[%d, %d]", left_index, right_index);
    }
}
