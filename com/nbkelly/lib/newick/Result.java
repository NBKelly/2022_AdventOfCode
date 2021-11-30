package com.nbkelly.lib.newick;

/**
 * @author NB Kelly <N.B.Kelly@protonmail.com>
 * @since June 02, 2020
 * @last June 02, 2020
 *
 * Result - integer wrapper for function output.
 */
public class Result<T> {
    /**
     * Wraps a value (result) in an object containing the result and a secondary integer value.
     *
     * @param result the result to wrap
     * @param index the secondary value
     */
    public Result(T result, int index) {
	this.result = result;
	this.index = index;
    }
    
    public T result;
    public int index;

    /**
     * Returns the string representation of this result
     * @return the string representation of this result
     */
    public String toString() {
	return String.format("%d : '%s'", index, result);
    }
}
