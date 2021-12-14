package com.nbkelly.lib;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A map that counts values
 * @author      NB Kelly <nbkelly @ protonmail.com>
 * @version     1.3
 * @since       1.3
 */
public class HashCounter<T> implements Iterable<T> {
    HashMap<T, Long> counter = new HashMap<T, Long>();

    private long total_count = 0;

    /**
     * Add a value to the counter, or increment it's count if it already exists
     * @param value value to add
     * @since 1.3
     */
    public void add(T value) {
	var count = counter.get(value);
	if(count == null)
	    count = 0l;
	counter.put(value, count+1l);
	total_count++;
    }

    /**
     * Add a value to the counter, or increment it's count if it already exists
     * @param value value to add
     * @since 22.12
     */
    public void add(T value, long count) {
	var new_count = counter.get(value);
	if(new_count == null)
	    new_count = 0l;
	counter.put(value, new_count+count);
	total_count++;
    }


    /**
     * Decremet a value on the counter
     * @param value value to sub
     * @since 1.4
     */
    public boolean sub(T value) {
	var count = counter.get(value);
	if(count == null)
	    return false;
	
	if(count - 1l == 0l)
	    counter.remove(value);
	else
	    counter.put(value, count-1l);
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
    public Long count() {
	return total_count;
    }

    public HashMap<T, Long> toHashMap() {
	return new HashMap<T, Long>(counter);
    }
    
    /**
     * Returns count of one element.
     * @return count of one element.
     * @since 1.3
     */
    public Long count(T value) {
	Long res = counter.get(value);

	return res != null ? res : 0l;
    }

    public String toString() {
	return counter.toString();
    }
    
    /**
     * Returns maximum count from this collection
     * @return maximum count from this collection
     * @since 1.3
     */
    public T max() {
	T res = null;
	long max = 0;
	for(var entry : counter.entrySet()) {
	    T key = entry.getKey();
	    var value = entry.getValue();

	    if(value > max) {
		res = key;
		max = value;
	    }
	}

	return res;
    }

    /**
     * Returns minimum count from this collection
     * @return minimum count from this collection
     * @since 22.12
     */
    public T min() {
	T res = null;
	Long min = null;
	for(var entry : counter.entrySet()) {
	    T key = entry.getKey();
	    var value = entry.getValue();

	    if(min == null) {
		res = key;
		min = value;
	    }
	    else if(value < min) {
		res = key;
		min = value;
	    }
	}

	return res;
    }

    public Iterator<T> iterator() { return counter.keySet().iterator(); }
    //public void forEach(Consumer<? super T> action) { return counter.forEach(action); }
    //public Iterator<T> spliterator()iterator() { return counter.keySet().iterator(); }
    
    /**
     * Returns the set of elements with the maximum count from this collection
     * @return the set of elements with the maximum count from this collection
     * @since 1.4
     */
    public HashSet<T> maxSet() {
	HashSet<T> res = new HashSet<T>();
	Long max = 0l;
	for(var entry : counter.entrySet()) {
	    T key = entry.getKey();
	    var value = entry.getValue();

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
