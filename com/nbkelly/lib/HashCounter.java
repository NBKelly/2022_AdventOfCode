package com.nbkelly.lib;

import java.util.HashMap;
import java.util.HashSet;

/**
 * A map that counts values
 * @author      NB Kelly <nbkelly @ protonmail.com>
 * @version     1.3
 * @since       1.3
 */
public class HashCounter<T> {
    HashMap<T, Integer> counter = new HashMap<T, Integer>();

    private int total_count = 0;

    /**
     * Add a value to the counter, or increment it's count if it already exists
     * @param value value to add
     * @since 1.3
     */
    public void add(T value) {
	Integer count = counter.get(value);
	if(count == null)
	    count = 0;
	counter.put(value, count+1);
	total_count++;
    }

    /**
     * Decremet a value on the counter
     * @param value value to sub
     * @since 1.4
     */
    public boolean sub(T value) {
	Integer count = counter.get(value);
	if(count == null)
	    return false;
	
	if(count - 1 == 0)
	    counter.remove(value);
	else
	    counter.put(value, count-1);
	total_count--;

	return false;
    }
    
    /**
     * Returns the number of keys in the map.
     * @return the number of keys in the map.
     * @since 1.3
     */
    public int num_keys() {
	return counter.size();
    }

    /**
     * Returns Sum Count of all elements in the counter.
     * @return Sum Count of all elements in the counter.
     * @since 1.3
     */
    public int count() {
	return total_count;
    }

    public HashMap<T, Integer> toHashMap() {
	return new HashMap<T, Integer>(counter);
    }
    
    /**
     * Returns count of one element.
     * @return count of one element.
     * @since 1.3
     */
    public int count(T value) {
	Integer res = counter.get(value);

	return res != null ? res : 0;
    }

    /**
     * Returns maximum count from this collection
     * @return maximum count from this collection
     * @since 1.3
     */
    public T max() {
	T res = null;
	int max = 0;
	for(var entry : counter.entrySet()) {
	    T key = entry.getKey();
	    Integer value = entry.getValue();

	    if(value > max) {
		res = key;
		max = value;
	    }
	}

	return res;
    }

    /**
     * Returns the set of elements with the maximum count from this collection
     * @return the set of elements with the maximum count from this collection
     * @since 1.4
     */
    public HashSet<T> maxSet() {
	HashSet<T> res = new HashSet<T>();
	int max = 0;
	for(var entry : counter.entrySet()) {
	    T key = entry.getKey();
	    Integer value = entry.getValue();

	    if(value > max) {
		res = new HashSet<T>();
		max = value;
	    }

	    if (value == max)
		res.add(key);		
	}

	return res;
    }
}
