package com.nbkelly.lib;

import java.util.function.Predicate;
import java.util.function.Function;

import java.util.TreeSet;
import java.util.Set;

import java.util.Map;
import java.util.TreeMap;

/**
 * Allows for the quick implementation of Djikstra's algorithm on 2d grids.
 * <p>
 * In general, this can be achieved with the following workflow:<br>
 * Define a neighbors function, which maps an IntPair coordinate to the valid
 * neighbors for the coordinate. <br>
 * Also define a pathable function, which takes as input a Tile T and gives as output
 * true if that tile is pathable.
 * <p>
 * Next, determine the available gain at each tile with simpleGain(screen, f(neighbors), pathable),
 * <br> Use the gain to run either simpleDjikstraMap or segmentDjikstraMap. The latter is intended for the creation of Djikstra Maps, rather than strict pathfinding.
 * @author      NB Kelly <nbkelly @ protonmail.com>
 * @version     1.3
 * @since       1.3
 */
public class Navigation {
    /**
     * Determines the frontiers of unexplored space
     *
     * @param screen the world state
     * @param neighbors a function of the type IntPair to Set(IntPair), which gets all the neighbors of an IntPair
     * @param pathable a predicate operating on T which returns true if a value is considered navigable
     * @return the set of all IntPairs bordering pathable known space
     * @since 1.3
     */
    public static <T> TreeSet<IntPair>
	simpleGain(Map<IntPair, T> screen,
		   Function<IntPair, Set<IntPair>> neighbors,
		   Predicate<T> pathable) {
	//find all unknown spaces neighboring a known empty space
	TreeSet<IntPair> discoverable = new TreeSet<>();

	for(IntPair p : screen.keySet())
	    //if the given thing is pathable, we look at it's neighbors
	    if(pathable.test(screen.get(p)))
		for(IntPair neighbor : neighbors.apply(p))
		    if(!screen.containsKey(neighbor))
			discoverable.add(neighbor);

	return discoverable;
    }

    /**
     * Delivers a ranking on all known spaces reachable from a set of gainful spaces
     *
     * @param gain the set of all spaces from which information can be gained
     * @param screen the world state
     * @param neighbors a function of the type IntPair to Set(IntPair), which gets all the neighbors of an IntPair
     * @param pathable a predicate operating on T which returns true if a value is considered navigable
     * @return A map of all spaces with scores assigned, and the scores assigned to them (low=good)
     * @since 1.3
     */
    public static <T> TreeMap<IntPair, Integer>
	simpleDjikstraMap(Set<IntPair> gain, Map<IntPair, T> screen,
			  Function<IntPair, Set<IntPair>> neighbors,
			  Predicate<T> pathable) {
	//first, fill in all values with gain as 0
	TreeMap<IntPair, Integer> djikstra = new TreeMap<>();

	TreeSet<IntPair> current = new TreeSet<IntPair>();
	for(var v : gain) {
	    djikstra.put(v, 0);
	    current.add(v);
	}

	int score = 1;
	
	while(current.size() > 0) {
	    TreeSet<IntPair> next = new TreeSet<>();

	    for(IntPair p : current) {
		//get neighbors
		var c_neighbors = neighbors.apply(p); //neighbors(p);

		//for all neighbors that are not in the map, and are pathable
		for(var neighbor : c_neighbors) {
		    if(!djikstra.containsKey(neighbor)
		       && screen.containsKey(neighbor)
		       && pathable.test(screen.get(neighbor))) {
			next.add(neighbor);
			djikstra.put(neighbor, score);
		    }
		}
	    }

	    score++;
	    current = next;
	}

	return djikstra;
    }

    /**
     * Delivers a ranking on all known spaces reachable from a set of gainful spaces
     *
     * @param initial the set of all spaces from which information can be gained
     * @param screen the world state
     * @param neighbors a function of the type IntPair to Set(IntPair), which gets all the neighbors of an IntPair
     * @param pathable a predicate operating on T which returns true if a value is considered navigable
     * @param startingScore score of initial states
     * @param scoreChange the quantity of change in the score per iteration (tile stepped)
     * @param finalScore the (inclusive) limit of scores which should be assigned
     *
     * @return A map of all spaces with scores assigned, and the scores assigned to them
     * @since 1.3
     */
    public static <T> TreeMap<IntPair, Integer>
	segmentDjikstraMap(Set<IntPair> initial, Map<IntPair, T> screen,
			   Function<IntPair, Set<IntPair>> neighbors,
			   Predicate<T> pathable,
			   int startingScore,
			   int scoreChange,
			   int finalScore) {
	//first, fill in all values with gain as 0
	TreeMap<IntPair, Integer> djikstra = new TreeMap<>();

	TreeSet<IntPair> current = new TreeSet<IntPair>();
	for(var v : initial) {
	    djikstra.put(v, startingScore);
	    current.add(v);
	}

	int score = startingScore + scoreChange;
	
	while(current.size() > 0 &&
	      !(score < Math.min(startingScore, finalScore))
	      && !(score > Math.max(startingScore, finalScore))) {
	    TreeSet<IntPair> next = new TreeSet<>();

	    for(IntPair p : current) {
		//get neighbors
		var c_neighbors = neighbors.apply(p); //neighbors(p);

		//for all neighbors that are not in the map, and are pathable
		for(var neighbor : c_neighbors) {
		    if(!djikstra.containsKey(neighbor)
		       && screen.containsKey(neighbor)
		       && pathable.test(screen.get(neighbor))) {
			next.add(neighbor);
			djikstra.put(neighbor, score);
		    }
		}
	    }

	    score += scoreChange;;
	    current = next;
	}

	return djikstra;
    }
}
