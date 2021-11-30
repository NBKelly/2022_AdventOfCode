package com.nbkelly.lib;

import java.util.HashMap;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.HashSet;

/**
 * An undirected graph.
 *
 * @author      NB Kelly <nbkelly @ protonmail.com>
 * @version     1.4
 * @since       1.3
 */
public class Graph {
    HashMap<Integer, TreeSet<Edge>> nodes = new HashMap<Integer, TreeSet<Edge>>();

    private class Edge implements Comparable<Edge> {
	int from;
	int target;
	int dist;
	public Edge(int from, int target, int dist) {
	    this.from = from;
	    this.target = target;
	    this.dist = dist;
	}
	
	public int compareTo(Edge e) {
	    return Util.compareTo(dist, e.dist,
				  Math.min(from, target), Math.min(e.from, e.target),
				  Math.max(from, target), Math.max(e.from, e.target));
	}

	public boolean equals(Object o) {
	    if(o.getClass() == Edge.class)
		return compareTo((Edge)o) == 0;
	    return false;
	}
    }
    
    /**
     * Create an edge between two nodes
     * @param from first node to link
     * @param to second node to link
     * @since 1.3
     */
    public void link(int from, int to) {	
	getList(from).add(new Edge(from, to, 1));
	getList(to).add(new Edge(to, from, 1));
    }
    
    private TreeSet<Edge> getList(int node) {
	if(nodes.containsKey(node))
	    return nodes.get(node);

	TreeSet<Edge> li = new TreeSet<Edge>();
	nodes.put(node, li);
	return li;
    }

    /**
     * Get the degrees (number of edges) of each node in a domain
     *
     * @param domain domain of the set
     * @return int[] of all the degrees of nodes between start and end
     * @since 1.3
     */
    public HashMap<Integer, Integer> degrees(HashSet<Integer> domain) {
	HashMap<Integer, Integer> res = new HashMap<Integer, Integer>();

	for(var node : domain) {
	    int sum = 0;
	    for(var edge : getList(node))
		if(domain.contains(edge.target))
		    sum++;
	    res.put(node, sum);
	}

	return res;
    }

    /**
     * Tests wether of not a graph is bipartite.
     * <p>
     * A graph is bipartite if it can be partitioned into 
     * an additional component by severing a single edge.
     * <p>
     * Additionally, a graph is bipartite if and only if it can be colored with exactly two colors,
     * or if and only if it contains no cycles of odd length.
     */
    public boolean bipartite(int start, int end) {
	boolean[] visited = new boolean[end - start + 1];
	int[] color = new int[end - start + 1];
	for(int i = start; i <= end; i++) {
	    if(visited[i - start])
		continue;
	    else {
		LinkedList<Integer> ct_node = new LinkedList<Integer>();
		LinkedList<Integer> ct_color = new LinkedList<Integer>();
		
		ct_node.add(i);
		ct_color.add(1);

		while(ct_node.size() > 0) {
		    int node = ct_node.pop();
		    int col = ct_color.pop();

		    if(node < start || node > end)
			continue;
		    
		    if(visited[node - start]) {			
			if(color[node - start] == col)
			    continue;
			else
			    return false; //color clash!
		    }
		    
		    visited[node - start] = true;
		    color[node - start] = col;
		    
		    TreeSet<Edge> neighbors = getList(node);
		    for(Edge edge : neighbors) {
			ct_node.add(edge.target);
			ct_color.add(col * -1);
		    }
		}
	    }
	}

	return true;
    }

    
    public boolean containsSimpleCycles_length(int start, int end, int length) {
	HashSet<Integer> targets = new HashSet<Integer>();
	while(start <= end)
	    targets.add(start++);
	
	return containsSimpleCycles_length(targets, length);
    }

    /**
     * Determine if a graph is contains (at least one) simple cycle of the given length.
     * <p>
     * A simple cycle is a cycle where no edge is traversed more than once.
     * @param domain the domain of the graph
     * @return true if it contains a simple cycle, false otherwise
     * @since 1.4
     */
    private boolean containsSimpleCycles_length(HashSet<Integer> domain, int length) {
	domain = new HashSet<Integer>(domain);
	//a leaf is a node with no outgoing links
	//first find all leaves
	var leaves = leaves(domain);
	
	outer:
	while(leaves.size() > 0) {
	    int leaf = leaves.iterator().next();
	    leaves.remove(leaf);
	    domain.remove(leaf);

	    //for every node leading to the now removed leaf
	    for(Edge inv : getList(leaf)) {
		//if that graph is in our graph
		if(domain.contains(inv.target))
		    //if that node is now a leaf
		    if(isLeaf(inv.target, domain))
			//add it to the set of leaves
			leaves.add(inv.target);
	    }
	}

	if(domain.size() < 4)
	    return false;
	if(domain.size() == 4)
	    return true;

	for(int node : domain)
	    if(findCycle_length(domain, length, node))
		return true;

	return false;
	//graph size > 4
	//for every node in consideration, attempt an N-path
    }

    private boolean findCycle_length(HashSet<Integer> components, int length, int start) {
	//for every edge from start
	for(Edge e : getList(start)) {
	    //if that edge is valid
	    if(components.contains(e.target)) {
		//see if a length n-1 path exists from that edge
		TreeSet<Edge> visited = new TreeSet<Edge>();
		visited.add(e);
		if(findPath_length(components, length-1, e.target, start, visited))
		    return true;
	    }
	}

	return false;
    }

    private boolean findPath_length(HashSet<Integer> components, int length,
				    int start, int target, TreeSet<Edge> visited) {
	if(length == 0)
	    return start == target;
	for(Edge e : getList(start)) {
	    if(components.contains(e.target) && !visited.contains(e)) {
		TreeSet<Edge> nv = new TreeSet<Edge>(visited);
		nv.add(e);
		if(findPath_length(components, length-1, e.target, target, nv))
		    return true;
	    }
	}

	return false;
    }
    
    //find all the leaves in a graph
    private HashSet<Integer> leaves(HashSet<Integer> graph) {
	HashSet<Integer> leaves = new HashSet<>();

	for(var node : graph) {
	    if(isLeaf(node, graph))
		leaves.add(node);
	}

	return leaves;
    }

    //a node is a leaf if it has no targets in our subgraph
    private boolean isLeaf(int node, HashSet<Integer> graph) {
	Integer visited = null;
	for(Edge target : getList(node))
	    if(graph.contains(target.target) && graph.contains(target.from))
		if(visited == null)
		    visited = target.target;
		else if (visited != target.target)
		    return false;
	return visited != null;
    }
    
    /**
     * Gets a count of all the connected components for all nodes within the range start, end
     * @param start start of interval
     * @param end end of interval
     * @return number of connected components for all nodes within the range
     * @since 1.4
     */
    public int connected_component_count(HashSet<Integer> domain) {
	//find all connected components only including nodes between start and ind
	HashSet<Integer> visited = new HashSet<Integer>();
	int components = 0;
	LinkedList<Integer> vals = new LinkedList<Integer>();
	for(int ctr : domain) {
	    if(!visited.contains(ctr)) {
		vals.add(ctr);
		components++;
	    }

	    while(vals.size() > 0) {
		int node = vals.pop();
		if(domain.contains(node) && !visited.contains(node)) {
		    visited.add(node);
		    var neighbors = getList(node);
		    for(var edge : neighbors)
			vals.add(edge.target);
		}
	    }
	}

	return components;
    }
    
    /**
     * Get the degrees of all the neighbors of each of these values
     *
     * @param domain domain of the graph
     * @since 1.3
     * @return int[] of all the degrees of the neighbors of each node
     */
    public HashMap<Integer, Integer> degrees_double(HashSet<Integer> domain) {
	var degrees = degrees(domain);
	HashMap<Integer, Integer> res = new HashMap<Integer, Integer>();
	for(int node : domain) {
	    int sum = 0;
	    for(var edge : getList(node))
		if(domain.contains(edge.target))
		    sum += degrees.get(edge.target);
	    res.put(node, sum);
	}

	return res;
    }
}
