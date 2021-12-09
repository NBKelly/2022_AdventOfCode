package com.nbkelly.lib.pathfinder;

import java.util.HashMap;
import com.nbkelly.lib.IntPair;
import com.nbkelly.lib.Util;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.List;

import java.util.function.Function;

public class Map {
    private HashMap<IntPair, Character> map = new HashMap<IntPair, Character>();

    public IntPair findFirst(Character c) {
	for(var entry : map.entrySet())
	    if(entry.getValue().equals(c))
		return entry.getKey();
	return null;
    }
    
    private IntPair key(int x, int y) {
	return new IntPair(x, y);
    }
    
    public Character get(int x, int y) {
	if(map.containsKey(key(x, y)))
	    return map.get(key(x, y));
	return null;
    }

    public void set(int x, int y, Character c) {
	map.put(new IntPair(x,y), c);
    }

    public boolean pathable(int x, int y, Function<Character, Boolean> pathable) {
	var token = get(x, y);
	return token != null && pathable.apply(token);	
    }

    private HashSet<IntPair> pathable(List<IntPair> candidates,
				      Function<Character, Boolean> pathable) {
	HashSet<IntPair> res = new HashSet<>();
	for(IntPair pair : candidates) {
	    if(pathable(pair.X, pair.Y, pathable))
		res.add(pair);
	}

	return res;
    }

    public static ArrayList<IntPair> OrthogonalNeighbors(IntPair pair) {
	ArrayList<IntPair> res = new ArrayList<>();

	res.add(new IntPair(pair.X-1, pair.Y));
	res.add(new IntPair(pair.X+1, pair.Y));
	res.add(new IntPair(pair.X, pair.Y+1));
	res.add(new IntPair(pair.X, pair.Y-1));

	return res;
    }
    
    public Integer distance_orthogonal(IntPair start, IntPair dest,
				       Function<Character, Boolean> pathable,
				       Function<IntPair, List<IntPair>> neighbors) {
	return distance_orthogonal(start.X, start.Y,
				   dest.X, dest.Y,
				   pathable,
				   neighbors);
    }
    
    //diagonal dist is considered as 2
    public Integer distance_orthogonal(int sx, int sy, int dx, int dy,
				       Function<Character, Boolean> pathable,
				       Function<IntPair, List<IntPair>> neighbors) {
 	if(sx == dx
	   && sy == dy)
	    return 0;
	if(!pathable.apply(get(sx, sy)) || !pathable.apply(get(dx, dy)))
	    return null;


 	IntPair destination = new IntPair(dx, dy);
	    
	TreeSet<Metric> metrics = new TreeSet<>();
	metrics.add(new Metric(0, new IntPair(sx, sy), destination));

	HashMap<IntPair, Integer> bestScore = new HashMap<IntPair, Integer>();


	while(metrics.size() > 0) {
	    var metric = metrics.pollFirst();
	    var last_best = bestScore.get(metric.location);
	    if(last_best != null && last_best <= metric.score)
		    continue;
	    
	    bestScore.put(metric.location, metric.score);

	    if(metric.location.equals(destination))
		return metric.moves;

	    var pathable_neighbors = pathable(neighbors.apply(metric.location), pathable);

	    for(var neighbor : pathable_neighbors)
		metrics.add(new Metric(metric.moves + 1, neighbor, destination));
	}
	
	return null;	
    }

    /**
     * Get the set of all characters visited on the shortest path between two nodes.
     */
    public HashSet<Character> visited_orthogonal(IntPair start, IntPair dest,
						 Function<Character, Boolean> pathable,
				       Function<IntPair, List<IntPair>> neighbors) {

	if(start.equals(dest))
	    return new HashSet<>();
	if(!pathable.apply(get(start.X, start.Y)) || !pathable.apply(get(dest.X, dest.Y)))
	    return null;

	TreeSet<Metric2> metrics = new TreeSet<>();
	metrics.add(new Metric2(0, start, dest, new HashSet<Character>()));

	HashMap<IntPair, Integer> bestScore = new HashMap<IntPair, Integer>();
		
	while(metrics.size() > 0) {
	    var metric = metrics.pollFirst();
	    if(bestScore.containsKey(metric.location)) {
		int score = bestScore.get(metric.location);
		if (score < metric.score)
		    continue;
	    }
	    else
		bestScore.put(metric.location, metric.score);

	    if(metric.location.equals(dest))
		return metric.visited;

	    var pathable_neighbors = pathable(neighbors.apply(metric.location), pathable);

	    for(var neighbor : pathable_neighbors)
		metrics.add(new Metric2(metric.moves + 1, neighbor, dest,
					metric.visited, get(neighbor.X, neighbor.Y)));
	}
	
	return null;	
    }

    private class Metric2 implements Comparable<Metric2> {
	int moves;
	IntPair location;
	int score;
	HashSet<Character> visited = new HashSet<Character>();
	
	public Metric2(int moves, IntPair location, IntPair destination,
		       HashSet<Character> visited, Character newChar) {
	    this.moves = moves;
	    this.location = location;
	    score = moves + (int)_dist(location.X, location.Y, destination.X, destination.Y, false);
	    this.visited.addAll(visited);
	    this.visited.add(newChar);
	}

	public Metric2(int moves, IntPair location, IntPair destination,
		       HashSet<Character> visited) {
	    this.moves = moves;
	    this.location = location;
	    score = moves + (int)_dist(location.X, location.Y, destination.X, destination.Y, false);
	    this.visited.addAll(visited);
	}


	public int compareTo(Metric2 m) {
	    return Util.compareTo(score, m.score,
				  location.X, m.location.X,
				  location.Y, m.location.Y);
	}

	public String toString() {
	    return String.format("%d | %s | %d", moves, location.toString(), score);
	}
    }    
    
    private class Metric implements Comparable<Metric> {
	int moves;
	IntPair location;
	int score;
	long dist;
	
	public Metric(int moves, IntPair location, IntPair destination) {
	    this.moves = moves;
	    this.location = location;
	    this.dist = Math.round(_dist(location.X, location.Y,
					 destination.X, destination.Y, false));
	    score = moves + (int)this.dist;
	}

	public int compareTo(Metric m) {
	    return Util.compareTo(score, m.score,
				  dist, m.dist,
				  moves, m.moves,				  
				  location.X, m.location.X,
				  location.Y, m.location.Y);
	}

	public String toString() {
	    return String.format("%d | %s | %d", moves, location.toString(), score);
	}
    }

    public String format(IntPair start, IntPair end,
				 Function<IntPair, Character> overlay) {
	StringBuilder s = new StringBuilder();

	s.append(String.format(">%s -> %s%n", start, end));

	for(int x = start.X; x <= end.X+1; x++)
	    s.append(x == start.X ? '+' : '-');
	s.append("+\n");
	for(int y = start.Y; y <= end.Y; y++) {
	    s.append("|");
	    for(int x = start.X; x <= end.X; x++) {
		
		var ch = get(x, y);
		var repl = overlay.apply(new IntPair(x, y));
		if(repl != null)
		    ch = repl;
		if(ch != null)
		    s.append(ch);
		else
		    s.append(' ');
	    }
	    s.append("|\n");
	}

	for(int x = start.X; x <= end.X+1; x++)
	    s.append(x == start.X ? '+' : '-');
	s.append("+\n");

	return s.toString();
    }
    
    public String format(IntPair start, IntPair end) {
	StringBuilder s = new StringBuilder();

	s.append(String.format(">%s -> %s%n", start, end));

	for(int x = start.X; x <= end.X+1; x++)
	    s.append(x == start.X ? '+' : '-');
	s.append("+\n");
	for(int y = start.Y; y <= end.Y; y++) {
	    s.append("|");
	    for(int x = start.X; x <= end.X; x++) {
		var ch = get(x, y);
		if(ch != null)
		    s.append(ch);
		else
		    s.append(' ');
	    }
	    s.append("|\n");
	}

	for(int x = start.X; x <= end.X+1; x++)
	    s.append(x == start.X ? '+' : '-');
	s.append("+\n");

	return s.toString();
    }
    
    private double _dist(int sx, int sy, int dx, int dy, boolean diag) {
	if(diag)
	    return Math.sqrt((sx-dx)*(sx-dx) + (sy-dy) * (sy-dy));
	else
	    return Math.abs(sx - dx) + Math.abs(sy - dy);
    }

    public Map(ArrayList<String> lines) {
	if(lines == null)
	    return;
	for(int y = 0; y < lines.size(); y++) {
	    String line = lines.get(y);
	    for(int x = 0; x < line.length(); x++) {
		map.put(new IntPair(x, y), line.charAt(x));
	    }
	}
    }
}
