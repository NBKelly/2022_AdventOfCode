package com.nbkelly.lib;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.function.BiConsumer;
/**
 * A map of hashsets.
 * @author      NB Kelly <nbkelly @ protonmail.com>
 * @version     2.0
 * @since       2.0
 */
public class HashMapSet<T, U> {
    private HashMap<T, HashSet<U>> set = new HashMap<>();

    public void put(T key, U value) {
	insert(key, value);
    }
    
    /**
     * Inserts a value into the set represented by the key
     *
     * @param key the key which indexes where the value should be put
     */
    private void insert(T key, U value) {
	if(!set.containsKey(key))
	    set.put(key, new HashSet<U>());
	set.get(key).add(value);
    }

    /**
     * Returns the set of values represented by a given key
     *
     * @param key the key which indexes the desired values
     * @return the set of values represented by the given key
     */
    public HashSet<U> getAll(T key) {
	HashSet<U> res = new HashSet<>();

	var tmp = set.get(key);
	if(tmp != null)
	    res.addAll(tmp);

	return res;
    }

    public String toString() {
	StringBuilder s = new StringBuilder();

	s.append("{\n");

	for(var entry : entrySet()) {
	    s.append(String.format("%s = { %s }%n", entry.getKey(), entry.getValue()));
	}

	s.append("}");
	return s.toString();
	
    }

    public Set<Map.Entry<T, HashSet<U>>> entrySet() {
	return set.entrySet();
    }
    
    public void forEach(BiConsumer<? super T, ? super HashSet<U>> action) {
	set.forEach(action);
    }
    
    /**
     * Returns the keyset of this collection
     * @return the keyset of this collection
     */
    public Set<T> keySet() {
	return set.keySet();
    }
}
