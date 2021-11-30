package com.nbkelly.lib;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.BinaryOperator;

public class MultiSet <T> {
    private HashMap<T, Integer> set = new HashMap<T, Integer>();

    /**
     * Add a value to a multiset
     * @param value element to add to multiset
     */
    public void add(T value) {
	if(!set.containsKey(value))
	    set.put(value, 1);
	else
	    set.put(value, set.get(value) + 1);
    }

    /**
     * Add a value to a multiset a given number of times
     * @param value element to add to multiset
     * @param quantity quantity to add to the multiset
     */
    public void add(T value, int quantity) {
	if(quantity < 1)
	    return;
	
	if(!set.containsKey(value))
	    set.put(value, quantity);
	else
	    set.put(value, set.get(value) + quantity);
    }

    /**
     * Remove an element from a multiset if it exists
     * @param value element to remove
     * @return true if the element existed to be removed
     */
    public boolean remove(T value) {
	Integer res = set.get(value);
	if(res == null)
	    return false;

	if(res == 1) {
	    set.remove(value);
	    return true;
	}

	set.put(value, set.get(value) - 1);
	return true;
    }

    /**
     * Gets the sum of two multisets.
     * @param target target multiset to sum with
     * @param adder function that adds two T's
     * @return the sum of two multisets
     */
    public MultiSet<T> sum(MultiSet<T> target, BinaryOperator<T> adder) {
	MultiSet<T> res = new MultiSet<T>();

	for(var left : set.entrySet()) {
	    T key = left.getKey();
	    int value = left.getValue();

	    for(var right : target.set.entrySet()) {
		T right_key = right.getKey();
		int right_value = right.getValue();

		int quantity = value * right_value;
		T new_key = adder.apply(key, right_key);
		res.add(new_key, quantity);
	    }
	}

	return res;
    }


    /**
     * Gets the differences between two multisets.
     * @param target target multiset to sum with
     * @param diff function that gets the difference between two T's
     * @return the diffs of two multisets
     */
    public HashSet<T> diffs(MultiSet<T> target, BinaryOperator<T> diff) {
	HashSet<T> diffs = new HashSet<T>();

	for(var left : set.keySet()) {
	    for(var right : target.set.keySet()) {
		diffs.add(diff.apply(left, right));
	    }
	}

	return diffs;
    }

    /**
     * Shifts a multiset
     * @param difference value to shift set by
     * @param adder function that adds two T's
     * @return shifted multiset
     */
    public MultiSet<T> shift(T difference, BinaryOperator<T> adder) {
	MultiSet<T> res = new MultiSet<T>();

	for(var left : set.entrySet()) {
	    T key = left.getKey();
	    int value = left.getValue();

	    res.add(adder.apply(key, difference), value);	    
	}

	return res;
    }

    /**
     * Gets the convolution of two multisets (-).
     * @param target target multiset to convolute with
     * @param subtractor function that subtracts one T from another
     * @return the convolution of two multisets
     */
    public MultiSet<T> convolution(MultiSet<T> target, BinaryOperator<T> subtractor) {
	MultiSet<T> res = new MultiSet<T>();

	for(var left : set.entrySet()) {

	    T key_left = left.getKey();
	    int value_left = left.getValue();

	    for(var right : target.set.entrySet()) {
		T right_key = right.getKey();
	        int right_value = right.getValue();

                int quantity = value_left * right_value;
                T new_key = subtractor.apply(key_left, right_key);
		res.add(new_key, quantity);
            }
	}
	
	return res;
    }

    /**
     * Gets the largest multiplicity from a multiset
     * @return the largest multiplicity from a multiset
     */
    public Pair<T, Integer> largest_multiplicity() {
	int max = -1;

	Pair<T, Integer> res = null;
	for(var pair : set.entrySet()) {
	    if(pair.getValue() > max) {
		res = new Pair<T, Integer>(pair.getKey(), pair.getValue());
		max = pair.getValue();
	    }
	}

	return res;
    }
}
