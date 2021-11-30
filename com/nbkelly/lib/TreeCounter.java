package com.nbkelly.lib;

import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A map that counts values
 * @author      NB Kelly <nbkelly @ protonmail.com>
 * @version     1.8
 * @since       1.8
 */
public class TreeCounter<T> {
    TreeMap<T, Integer> counter = new TreeMap<T, Integer>();

    private int total_count = 0;

    /**
     * Add a value to the counter, or increment it's count if it already exists
     * @param value value to add
     * @since 1.8
     */
    public void add(T value) {
	Integer count = counter.get(value);
	if(count == null)
	    count = 0;
	counter.put(value, count+1);
	total_count++;
    }

    /**
     * Returns the number of keys in the map.
     * @return the number of keys in the map.
     * @since 1.8
     */
    public int num_keys() {
	return counter.size();
    }

    /**
     * Returns Sum Count of all elements in the counter.
     * @return Sum Count of all elements in the counter.
     * @since 1.8
     */
    public int count() {
	return total_count;
    }

    public TreeMap<T, Integer> toTreeMap() {
	return new TreeMap<T, Integer>(counter);
    }
    
    /**
     * Returns count of one element.
     * @return count of one element.
     * @since 1.8
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
    public TreeSet<T> maxSet() {
	TreeSet<T> res = new TreeSet<T>();
	int max = 0;
	for(var entry : counter.entrySet()) {
	    T key = entry.getKey();
	    Integer value = entry.getValue();

	    if(value > max) {
		res = new TreeSet<T>();
		max = value;
	    }

	    if (value == max)
		res.add(key);		
	}

	return res;
    }
}
