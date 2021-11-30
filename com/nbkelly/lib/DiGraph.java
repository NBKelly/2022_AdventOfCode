package com.nbkelly.lib;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Set;

/**
 * A directed graph.
 *
 * @author      NB Kelly <nbkelly @ protonmail.com>
 * @version     1.7
 * @since       1.3
 */
public class DiGraph {
    private HashMap<Integer, TreeSet<Edge>> nodes = new HashMap<Integer, TreeSet<Edge>>();
    private HashSet<Integer> meta_domain = new HashSet<Integer>();
    private HashSet<Integer> domain_copy = null;
    //keeping track of this lets us find shortest paths much faster in some cases
    private boolean containsNegatives = false;
    private HashMap<Integer, String> names = new HashMap<Integer, String>();
    private HashMap<String, Integer> rev_names = new HashMap<String, Integer>();
    /**
     * returns a copy of the domain of this graph
     * @return a copy of the domain of this graph
     */
    public HashSet<Integer> domain() {
	if(domain_copy == null)
	    domain_copy = new HashSet<Integer>(meta_domain);
	return domain_copy;
    }
    
    /**
     * Sets the name for a node
     * @param node node to set name for
     * @param name name to give node
     * @since 1.8
     */
    public void setName(int node, String name) {
	names.put(node, name);
	rev_names.put(name, node);
    }

    /**
     * Gets an id from a name, if possible
     * @param name
     * @since 1.9
     */
    public Integer fromName(String name) {
	return rev_names.get(name);
    }

    /**
     * Returns true if the given node is named
     */
    private boolean isNamed(int i) {
	return names.containsKey(i);
    }

    /**
     * Gets a name from an id, or returns null
     * @param id
     * @since 2.0
     */
    public String getName(int id) {
	return names.get(id);
    }
    
    /**
     * Gets an adjacency list corresponding to this graph
     * @param domain domain of the adjacency list
     * @return the integer adjacency list of this graph
     */
    public ArrayList<IntPair> adjacencyList(HashSet<Integer> domain) {
	ArrayList<IntPair> res = new ArrayList<>();
	for(int from : domain)
	    for(Edge edge : getList_conservative(from))
		if(domain.contains(edge.target))
		    res.add(new IntPair(from, edge.target));

	return res;
    }

    /**
     * Gets an adjacency list corresponding to this graph, using names where possible
     * @param domain domain of the adjacency list
     * @return the named adjacency list of this graph
     */
    public ArrayList<Pair<String, String>> adjacencyListNamed(HashSet<Integer> domain) {
	ArrayList<Pair<String, String>> res = new ArrayList<>();
	for(Integer from : domain)
	    for(Edge edge : getList_conservative(from))
		if(domain.contains(edge.target)) {
		    String fs = names.containsKey(from) ? names.get(from) : from.toString();
		    String ts = names.containsKey(edge.target) ? names.get(edge.target) :
			edge.target + "";
		    res.add(new Pair<String, String>(fs, ts));
		}

	return res;
    }

    private class Edge implements Comparable<Edge> {
	int from;
	int target;
	int dist;
	public Edge(int from, int target, int dist) {
	    this.target = target;
	    this.dist = dist;
	    this.from = from;
	}
	
	public int compareTo(Edge e) {
	    return Util.compareTo(dist, e.dist, target, e.target, from, e.from);
	}

	public boolean equals(Object o) {
	    if(o.getClass() == Edge.class)
		return compareTo((Edge)o) == 0;
	    return false;
	}
    }    
    
    /**
     * Create an edge between two nodes. The distance of that edge is 1.
     * @param from first node to link
     * @param to second node to link
     * @since 1.3
     */
    public void link(int from, int to) {
	getList(from).add(new Edge(from, to, 1));
	meta_domain.add(from);
	meta_domain.add(to);
	domain_copy = null;
    }

    /**
     * Create an edge between two nodes.
     * @param from first node to link
     * @param to second node to link
     * @param weight weight of the link
     * @since 1.4
     */
    public void link(int from, int to, int weight) {
	if(weight < 0)
	    containsNegatives = true;
	getList(from).add(new Edge(from, to, weight));
	meta_domain.add(from);
	meta_domain.add(to);
	domain_copy = null;
    }
    
    /**
     * Create a bijective link between two nodes.
     * @param from first node to link
     * @param to second node to link
     * @param weight weight of the link
     * @since 1.4
     */
    public void join(int left, int right, int weight) {
	link(left, right, weight);
	link(right, left, weight);
    }

    /**
     * Create a bijective link between two nodes with weight 1.
     * @param from first node to link
     * @param to second node to link
     * @since 1.4
     */
    public void join(int left, int right) {
	link(left, right, 1);
	link(right, left, 1);
    }

    /**
     * Gets the shortest weight from one node to another.
     * @param from the first node
     * @param to the second node
     */
    public Integer weight(int from, int to) {
	Integer min = null;
	var edges = getList_conservative(from);
	for(var edge : edges) {
	    if(edge.target == to)
		if(min == null || edge.dist < min)
		    min = edge.dist;
	}

	return min;
    }

    /* gets the list of edges at a node */
    private TreeSet<Edge> getList(int node) {
	if(nodes.containsKey(node))
	    return nodes.get(node);

	TreeSet<Edge> li = new TreeSet<Edge>();
	nodes.put(node, li);
	return li;
    }

    /* gets the list of edges at a node, without making a new list */
    private TreeSet<Edge> getList_conservative(int node) {
	if(nodes.containsKey(node))
	    return nodes.get(node);

	TreeSet<Edge> li = new TreeSet<Edge>();
	return li;
    }

    /**
     * Gets the shortest path from a node to all other nodes within an interval, supporting negative
     * edge weights and negative weight cycles.
     * <p>
     * If negative weight cycles are detected, all distances nodes reachable through a negative
     * weight
     * cycle will be returned as negativeInfinity, and all other paths will be returned as normal.
     * <p>
     * For an acyclic graph or a graph without negative edges, the time complexity is O(N). <br>
     * For all other graphs, the time complexity is O(nodes . edges).
     * @param considered all nodes considered to be in the graph
     * @param from_node node to search from
     * @return the shortest path from that node to all other nodes within range
     * @since 1.7
     */
    public HashMap<Integer, WrappedBigInt> shortest_path(HashSet<Integer> considered,int from_node){
	//if it's acyclic, we can do it in linear time 100% of the time
	if(acyclic(considered)) {
	    //first we topologically sort the array
	    LinkedList<Integer> sorted = topologicSort(considered);
	    
	    HashMap<Integer, WrappedBigInt> dists = new HashMap<Integer, WrappedBigInt>();
	    
	    while(sorted.size() > 0 && sorted.peekFirst() != from_node) {
		sorted.pollFirst();
	    }

	    if(sorted.size() > 0 && sorted.peekFirst() == from_node) {		
		dists.put(from_node, WrappedBigInt.ZERO);
	    }

	    while(sorted.size() > 0) {
		int node = sorted.pollFirst();
		var baseScore = dists.get(node);
		if(baseScore == null)
		    continue;
		
		for(Edge e : getList_conservative(node)) {
		    int target = e.target;
		    WrappedBigInt newScore = WrappedBigInt.valueOf(e.dist).add(baseScore);
		    if(considered.contains(target)) //if the target is in our set
			//if we have no value, or we have an improved score
			if(!dists.containsKey(target) || newScore.compareTo(dists.get(target)) < 0)
			    //insert it into our distances map
			    dists.put(target, newScore);
		}
	    }

	    return dists;
	}

	if(containsNegatives && containsNegativeCycles(considered)) {
	    //every member of a negative weight cycle
	    HashSet<Integer> negative_members = negativeCycleMembers(considered);

	    //every member reachable from the starting loncation
	    HashMap<Integer, Integer> reachable = dists(considered, from_node);

	    //every member reachable from a negative weight cycle member
	    var reachableNegs = new HashSet<Integer>(dists(considered, negative_members).keySet());
	    reachableNegs.retainAll(reachable.keySet());	    
	    
	    //every node reachable from a negative weight cycle that we can reach
	    HashMap<Integer, Integer> reachableFromNegs = dists(considered, reachableNegs);
	    
	    var res = new HashMap<Integer, WrappedBigInt>();

	    //mark every value in reachableFromNegs as -infinity
	    for(int node : reachableFromNegs.keySet()) {
		res.put(node, WrappedBigInt.NEGATIVE_INFINITY);
		considered.remove(node);
	    }

	    //we can't reach this, so it's gone
	    for(int node : negative_members) {
		considered.remove(node);
	    }

	    res.putAll(shortest_path(considered, from_node));
	    
	    return res;
	}

	if(containsNegatives) {
	    var _edges = validEdges(considered);
	    int num_edges = _edges.size();
	    int num_nodes = considered.size();
	    
	    //node -> index
	    HashMap<Integer, Integer> mapping = new HashMap<Integer, Integer>();
	
	    {
		int i = 0;
		for(int node : considered) {
		    mapping.put(node, i++);
		}
	    }

	    Long[] dists = new Long[considered.size()];
	    boolean[] reachable = new boolean[considered.size()];
	    
	    for(int i = 1; i < dists.length; i++)
		dists[i] = ((long)Integer.MAX_VALUE) * 2; //null;

	    dists[mapping.get(from_node)] = 0l;
	    reachable[mapping.get(from_node)] = true;

	    //stage 2: relax edges
	    for(int i = 1; i < num_nodes; i++) {
		for (Edge edge : _edges) {
		    int u = mapping.get(edge.from);
		    int v = mapping.get(edge.target);
		    int weight = edge.dist;
		    if(dists[u] + weight < dists[v]) {
			dists[v] = dists[u] + weight;
			if(reachable[u])
			    reachable[v] = true;
		    }
		}
	    }

	    //stage 3: check for NWC
	    for(Edge edge : _edges) {
		int u = mapping.get(edge.from);
		int v = mapping.get(edge.target);
		int weight = edge.dist;

		if(dists[u] + weight < dists[v])
		    return null;
	    }

	    HashMap<Integer, WrappedBigInt> res = new HashMap<Integer, WrappedBigInt>();
	    for(var key : mapping.keySet()) {
		if(reachable[mapping.get(key)])
		    res.put(key, WrappedBigInt.valueOf(dists[mapping.get(key)]));
	    }

	    return res;
	}
	
	

	/* at this point, we contain cycles but not negative cycles */
	TreeSet<Edge> edges = new TreeSet<>(getList_conservative(from_node));
	HashMap<Integer, WrappedBigInt> res = new HashMap<Integer, WrappedBigInt>();
	res.put(from_node, WrappedBigInt.ZERO);
	while(edges.size() > 0) {
	    Edge edge = edges.pollFirst();
	    if(considered.contains(edge.target))
		if(!res.containsKey(edge.target)) {
		    res.put(edge.target, WrappedBigInt.valueOf(edge.dist));
		    
		    for(Edge new_edge : getList_conservative(edge.target))
			edges.add(new Edge(0, new_edge.target, new_edge.dist + edge.dist));
		}
	}
	
	return res;
    }        

    /**
     * Gets the shortest path from one node to another node, if it exists.
     * <p>
     * In general, this is O(n). If bellmanford is needed, it will be O(edges.nodes).
     * <p>
     * If there exists a negative-length cycle in this graph,
     * the return result may be negativeInfinity if a path exists that contains a negative
     * weight cycle.
     * 
     * @param considered the set of nodes to consider
     * @param from_node source node
     * @param to_node destination node
     * @return the shortest path from the source to the destination, if it exists
     * @since 1.7
     */
    public WrappedBigInt shortest_path(HashSet<Integer> considered, int from_node, int to_node) {
	var map = shortest_path(considered, from_node);
	if(map != null && map.containsKey(to_node))
	    return map.get(to_node);
	return null;
    }


    
    /**
     * converts this graph to a string using all nodes as the domain
     * @return A string representation of this graph using all nodes as the domain
     * @since 1.7
     */
    public String toString() {
	return toString(meta_domain);
    }
	
    /**
     * converts this graph to a string using a given domain
     * @param domain the domain of this graph
     * @return A string representation of this graph using the given domain
     * @since 1.7
     */
    public String toString(HashSet<Integer> domain) {
	StringBuilder sb = new StringBuilder("");

	for(int i : domain) {
	    for(Edge e : getList_conservative(i)) {
		if(domain.contains(e.target)) {
		    if(sb.length() > 0)
			sb.append("\n");
		    sb.append(String.format("%d %d %d", e.from, e.target, e.dist));
		}
	    }
	}

	return sb.toString();
    }
    
    /**
     * Determine if a graph is cyclic within a given range of nodes
     * <p>
     * This function has no side-effects
     * @param graph the nodes that make up this graph
     * @return true if it's cyclic, false if it contains no cycles
     * @since 1.4
     */
    public boolean cyclic(HashSet<Integer> graph) {
	return !acyclic(graph);
    }

    /**
     * Determine if a graph is acyclic within a given set of nodes.
     * <p>
     * This function has no side-effects
     * @param domain the nodes that make up the domain of this graph
     * @return true if it's acyclic, false if it contains cycles
     * @since 1.4
     */
    public boolean acyclic(HashSet<Integer> domain) {
	domain = removeLeaves(domain);
	domain = removeSources(domain);	

	return domain.size() == 0;
    }

    /**
     * Find the set of strongly connected components that make up this graph.
     * <p>
     * For every pair of nodes within a strongly connected component, (a, b),
     * there exists a path (a.b) and a path (b.a). <br> For every pair (a,b) where
     * a and b are within different components, there does not exist both a path
     * (a.b) and a path (b.a).
     * <p>
     * This runs in linear time and does not have side-effects.
     * 
     * @param domain the domain of the graph
     * @return A list of the domains of each strongly connected component
     * @since 1.7
     */
    public ArrayList<HashSet<Integer>> stronglyConnectedComponents(HashSet<Integer> domain) {
	//here's the process:
	//any leaf or source happens to be a strongly connected component
	domain = new HashSet<Integer>(domain); //no side-effects

	ArrayList<HashSet<Integer>> res = new ArrayList<HashSet<Integer>>();
	
	//every leaf and source node is its own strongly connected component
	var leaves = leaves(domain);
	var inverse = inverse(domain);
	while(leaves.size() > 0) {
	    int leaf = leaves.iterator().next();
	    
	    HashSet<Integer> component = new HashSet<Integer>();
	    component.add(leaf);
	    res.add(component);
	    
	    domain.remove(leaf);
	    leaves.remove(leaf);
	    
	    for(Edge inv : inverse.getList_conservative(leaf))
		//if that node is in our graph
		if(domain.contains(inv.target))
		    //if that node is now a leaf
		    if(isLeaf(inv.target, domain))
			//add it to the set of leaves
			leaves.add(inv.target);
	}

	//every source node is it's own strongly connected component too
	var sources = inverse.leaves(domain);

	while(sources.size() > 0) {
	    int source = sources.iterator().next();

	    HashSet<Integer> component = new HashSet<Integer>();
	    component.add(source);
	    res.add(component);
	    
	    domain.remove(source);
	    sources.remove(source);

	    for(Edge inv : getList_conservative(source))
		//if that node is in our graph
		if(domain.contains(inv.target))
		    //if that node is now a source
		    if(inverse.isLeaf(inv.target, domain))
			//add it to the set of sources
			sources.add(inv.target);
	}

	//pick any remaining node
	//find the set of all elements that it can reach
	//find the set of all elements that can reach it
	//find the intersection of these sets
	
	//they are all a strongly connected component
	while(domain.size() >  0) {
	    int node = domain.iterator().next();
	    HashSet<Integer> component = new HashSet<Integer>();
	    component.add(node);

	    //find all elements which we can reach which can also reach us
	    var reachable = dists(domain, node).keySet();
	    var returnable = inverse(domain).dists(domain, node).keySet();

	    reachable.retainAll(returnable);
	    	    
	    component.addAll(reachable);
	    domain.removeAll(reachable);

	    res.add(component);
	}

	return res;
    }

    /**
     * Returns the set of all nodes which are members of a negative cycle within the domain.
     * <p>
     * This splits the graph up into individual components and searches each
     * component in order, using the notion that any strongly connected component containing
     * a negative weight cycle is itself a negative weight cycle as the driver
     * for it's algorithm solution.
     * <p>
     * This runs in (nodes*edges) time, and does not have side-effects.
     *
     * @param domain the domain of this graph
     * @return true if the graph contains a negative weight cycle over this domain
     * @since 1.7
     */
    public HashSet<Integer> negativeCycleMembers(HashSet<Integer> domain) {	
	var stronglyConnectedComponents = stronglyConnectedComponents(domain);

	return stronglyConnectedComponents
	    //.parallelStream()
	    .stream()
	    .filter(component -> bellmanFordNegatives(component))	    
	    .reduce(new HashSet<Integer>(),
		    (res, el) -> { res.addAll(el); return res; });
	
	/*HashSet<Integer> res = new HashSet<Integer>();
	for(HashSet<Integer> component : stronglyConnectedComponents) {
	    if(bellmanFordNegatives(component)) //containsNegativeCycles(component))
		res.addAll(component);
	}
		
	return res;*/
    }

    /**
     * Returns true if the graph contains negative weight cycles.
     * This splits the graph up into individual components and searches each
     * component in parallel, so it is preferred to the bellmanFordNegatives
     * solution.
     *
     * This runs in (nodes * edges) time for each component.
     *
     * @param domain
     * @return true if the graph contains a negative weight cycle over this domain
     * @since 1.7
     */
    public boolean containsNegativeCycles(HashSet<Integer> domain) {
	//first, get all of the strongly connected components of G
	var stronglyConnectedComponents = stronglyConnectedComponents(domain);

	return stronglyConnectedComponents.parallelStream()
	    .filter(component -> bellmanFordNegatives(component))
	    .findAny().orElse(null) != null;

	/*for(HashSet<Integer> component : stronglyConnectedComponents) {
	    if(bellmanFordNegatives(component)) //containsNegativeCycles(component))
		return true;
		}*/

	//return false;
    }

    /**
     * Uses the Bellman-Ford algorithm to detect the presence of negative length cycles in
     * the domain of this graph.
     * <p>
     * This takes (edges * vertices) time
     * 
     * @param domain the domain of this graph
     * @return true if there is a negative length cycle in the domain, false otherwise
     * @since 1.7
     */
    public boolean bellmanFordNegatives(HashSet<Integer> domain) {
	var _edges = validEdges(domain);
	int num_edges = _edges.size();
	int num_nodes = domain.size();
	
	//node -> index
	HashMap<Integer, Integer> mapping = new HashMap<Integer, Integer>();	

	{
	    int i = 0;
	    for(int node : domain) {
		mapping.put(node, i++);
	    }
	}

	Long[] dists = new Long[domain.size()];
	
	for(int i = 1; i < dists.length; i++)
	    dists[i] = ((long)Integer.MAX_VALUE) * 2; //null;
	    
	dists[0] = 0l;

	//stage 2: relax edges
	for(int i = 1; i < num_nodes; i++) {
	    HashSet<Edge> nc = new HashSet<Edge>();
	    for (Edge edge : _edges) {
		int u = mapping.get(edge.from);
		int v = mapping.get(edge.target);
		int weight = edge.dist;
		if(dists[u] + weight < dists[v]) {
		    dists[v] = dists[u] + weight;
		}
	    }
	}

	//stage 3: check for NWC
	for(Edge edge : _edges) {
	    int u = mapping.get(edge.from);
	    int v = mapping.get(edge.target);
	    int weight = edge.dist;

	    if(dists[u] + weight < dists[v])
		return true;
	}

	return false;
    }

    /**
     * returns the set of valid edges for this graph within a domain
     */
    private ArrayList<Edge> validEdges(HashSet<Integer> graph) {
	ArrayList<Edge> res = new ArrayList<Edge>();
	for(int node : graph)
	    for(var edge : getList_conservative(node))
		if(graph.contains(edge.target))
		    res.add(edge);
	//else
	//	    System.err.println("Target: " + edge.target + " not in graph");
	return res;
    }

    /**
     * Counts the number of edges in the graph
     * @param domain the domain of this graph
     * @return the number of edges in the graph
     * @since 1.7
     */
    public int numEdges(HashSet<Integer> domain) {
	int ct = 0;
	for(int node : domain)
	    for(var edge : getList_conservative(node))
		if(domain.contains(edge.target))
		    ct++;

	return ct;
    }
        
    /**
     * Returns the length (weighted) of the shortest cycle through this edge, if one exists.
     * Otherwise, returns null
     * @param graph set of nodes to consider
     * @param from oririgin of edge to find the shortest cycle through
     * @param to destination of edge to find the shortest cycle through
     * @return the length (weighted) of the shortest cycle through this edge if it exists, or null
     * @since 1.5
     */
    public WrappedBigInt shortestCycle(HashSet<Integer> graph, int from, int to) {
	//first, remove all leaves
	graph = removeLeaves(graph);
	graph = removeSources(graph);

	if(!graph.contains(from) || !graph.contains(to))
	    return null;

	//get the distance from (to) to (from)
	var dist = shortest_path(graph, to, from);

	//get the distance from (from to (to)
	Integer minDist = null;
	for(var edge : getList_conservative(from)) {
	    if(edge.target == to)
		if(minDist == null || edge.dist < minDist)
		    minDist = edge.dist;
	}

	if(dist != null && minDist != null)
	    return dist.add(WrappedBigInt.valueOf(minDist));

	return null;
    }

    /**
     * returns the inverse set of this graph
     * @param domain the domain of the given graph
     * @return an inverse of the graph over the given domain
     * @since 1.7
     */
    public DiGraph inverse(HashSet<Integer> domain) {
	DiGraph inverse = new DiGraph();
	for(var node : domain)
	    for(var edge : getList_conservative(node))
		if(domain.contains(edge.target))
		    inverse.link(edge.target, node);

	return inverse;
    }

    
    
    /**
     * returns the set of all leaves in this graph
     * @param domain the domain of the graph
     * @return the set of all leaves in this graph
     * @since 1.7
     */
    public HashSet<Integer> leaves(HashSet<Integer> domain) {
	HashSet<Integer> leaves = new HashSet<>();
	
	for(var node : domain) {
	    if(isLeaf(node, domain))
		leaves.add(node);
	}

	return leaves;
    }

    /**
     * returns whether or not the graph is semi-connected.
     * <p>
     * A graph is semi-connected if for each pair of nodes, a path exists
     * from node a to the node b, or from b to a.
     * <p>
     * A node is semi-connected IFF a hamilton path exists through the condensation of the graph
     * @param domain the domain of the graph
     * @return whether or not the graph is semi-connected
     * @since 1.7
     */
    public boolean semiConnected(HashSet<Integer> domain) {
	var components = stronglyConnectedComponents(domain);

	Meta meta = new Meta(domain);
	var hamilton = meta.g.hamiltonianPath(meta.domain);
	
	return hamilton != null;
    }

    /**
     * Meta-condensation of a given graph
     */
    private class Meta {
	DiGraph g = new DiGraph();
	HashMap<HashSet<Integer>, Integer> index = new HashMap<>();
	HashMap<Integer, HashSet<Integer>> lookup = new HashMap<>();
	int size = 0;
	HashSet<Integer> domain;
	public Meta(HashSet<Integer> graph) {
	    var components = stronglyConnectedComponents(graph);

	    //it's possible to construct a graph from the strongly connected components.
	    //to do this, perform the following:
	    //pick any strongly connected component
	    //determine all nodes reachable from that node which are not a part of that node
	    HashMap<Integer, HashSet<Integer>> node_to_component = new HashMap<>();
	    HashMap<HashSet<Integer>, Integer> meta_component = new HashMap<>();

	    index = meta_component;
	    
	    for(var component : components) {
		//map each component to the meta-component via integer
		meta_component.put(component, ++size);
		lookup.put(size, component);
		
		//map each node to a component
		for(var node : component)
		    node_to_component.put(node, component);
	    }

	    for(Integer node : graph) {
		//get the 
		int from = meta_component.get(node_to_component.get(node));
		for(Edge e : getList_conservative(node)) {
		    if(graph.contains(e.target)) {
			//get the to-node
			int to = meta_component.get(node_to_component.get(e.target));
			//duplicates get sorted out for "free" by link
			g.link(from, to);
		    }
		}
	    }

	    domain = Util.set(1, size);
	}
    }

    /**
     * Returns a valid arrangement for the 2-satisfiability of the domain, if one exists.
     * <p>
     * The domain is expected to contain the skew-symmetric implication graph of the given 2sat problem.
     * <p>
     * @param domain a set of nodes pertaining to the possible boolean states.
     * Do not include 0, because -0 is not supported.
     *
     * @return a valid arrangement for the 2-satisfiability of the domain, otherwise null
     */
    public HashMap<Integer, Integer> sat2(HashSet<Integer> domain) {
	//get the strongly connected components
	var connected =  stronglyConnectedComponents(domain);

	for(var component : connected) {
	    //System.out.println("Component: " + component);
	    //trivial check: if a component contains it's complement, then there can not be a valid pairing
	    for(int val : component)
		if(component.contains(-1 * val))
		    return null;
	}

	var meta = new Meta(domain);

	//topologically sort the meta
	var top = meta.g.topologicSort(meta.domain);

	HashMap<Integer, Integer> mapped = new HashMap<Integer, Integer>();

	var iterator = top.descendingIterator();
	while(iterator.hasNext()) {
	    int topo_node = iterator.next();
	    //get the component mapped to this node
	    
	    var component = meta.lookup.get(topo_node);
	    for(var node : component) {
		if(!mapped.containsKey(Math.abs(node))) {
		    mapped.put(Math.abs(node), node);
		}
	    }
	}

	return mapped;
    }
    
    /**
     * Returns a node which is a universal source, if one exists
     * @param domain domain of the graph
     * @return a node which is a universal source if one exists, or null
     * @since 1.7
     */
    public Integer universalSource(HashSet<Integer> domain) {
	var sorted = topologicSort(domain);

	if(sorted != null) {
	    //get whatever item is at the start - it's a universal source
	    int highest_post_node = sorted.get(0);
	    var reachable = dists(domain, highest_post_node);

	    if(reachable.size() == domain.size())
		return highest_post_node;

	    return null;
	}
	
	Meta meta = new Meta(domain);
	HashSet<Integer> meta_sources = meta.g.sources(meta.domain);
	
	if(meta_sources.size() == 1) {
	    int src = meta_sources.iterator().next();
	    for(var comp : meta.index.keySet())
		//we can take literally any element from this set because it's strongly connected
		if(meta.index.get(comp) == src)
		    return comp.iterator().next();
	}
        	    
	return null;
    }
    
    /**
     * returns the set of all sources in this domain
     * @param domain the domain of the graph
     * @return the set of all sources in the domain
     * @since 1.7
     */
    public HashSet<Integer> sources(HashSet<Integer> domain) {
	return inverse(domain).leaves(domain);	
    }

    /**
     * returns true if the node is a leaf
     * <p>
     * A node can be a leaf even if it is not within the domain,
     * and a node can be a leaf even if it has an edge leading to itself
     * 
     * @param node node to check
     * @param domain domain of this graph
     * @return true is the given node is a leaf
     * @since 1.7
     */
    public boolean isLeaf(int node, HashSet<Integer> domain) {
	for(var target : getList_conservative(node))
	    if(domain.contains(target.target) && target.target != node)
		return false;
	return true;
    }

    /**
     * Finds the hamiltonian path through a digraph if one exists, otherwise returns null
     * 
     * @param domain set of nodes considered to be the domain of this graph
     * @return the hamiltonian path through a digraph if one exists, otherwise null
     * @since 1.7
     */
    public LinkedList<Integer> hamiltonianPath(HashSet<Integer> domain) {
	var expected = topologicSort(domain);

	if(expected == null)
	    return null;

	Integer last = null;
	for(int node : expected) {
	    //find a path between the last element and the current element
	    if(last != null) {
		var edges = getList_conservative(last);
		boolean found = false;
		for(Edge e : edges)
		    if(e.target == node) {
			found = true;
			break;
		    }
		if(!found)
		    return null;
	    }
		    
	    last = node;
	}

	return expected;
    }


    /**
     * Find the longest map through this graph, starting from a given point of origin.
     * <p>
     * This requires that the graph be acyclic.
     *
     * @param domain the domain to consider
     * @return a list of nodes comprising the longest path (map) through this DAG
     * @since 2.0
     */
    public ArrayList<Integer> longest_map(HashSet<Integer> domain) {
	domain = new HashSet<Integer>(domain);

	if(!acyclic(domain))
	    return null;

	var sorted = topologicSort(domain);

	HashMap<Integer, IntPair> scores = new HashMap<Integer, IntPair>();

	for(int node : sorted) {
	    if(!scores.containsKey(node))
		scores.put(node, new IntPair(node, 0));		
	    /*
	     * IntPair: origin(X), score(Y)
	     */ 
	    int score = scores.get(node).Y;

	    //get all children of this node
	    var children = getList(node);
	    for(var child : children)
		if(domain.contains(child.target)) {
		    var old = scores.get(child.target);
		    if(old == null || old.Y <= score + 1)
			scores.put(child.target, new IntPair(node, score + 1));
		}	    
	}

	//now we just pick whichever one has the highest score
	int score_max = 0;
	IntPair max = null;
	int target = 0;
	
	for(var entry : scores.entrySet()) {
	    int score = entry.getValue().Y;
	    
	    if(score >= score_max) {
		score_max = score;
		target = entry.getKey();
		max = entry.getValue();
	    }
	}

	LinkedList<Integer> res = new LinkedList<>();
	res.add(target);
	while(max.Y != 0) {
	    res.push(max.X);
	    max = scores.get(max.X);
	}
		
	return new ArrayList<Integer>(res);
    }
    
    /**
     * Removes all leaves from a DiGraph
     * <p>
     * A leaf is a node which does not contain outgoing edges,
     * but may contain edges leading towards from itself.
     * <p>
     * Because removing leaves may create more leaves,
     * this continues until no leaves remain. The remaining graph
     * is either empty, or cyclic.
     *
     * @param domain domain to remove sources from
     * @return a restricted domain which contains no leaves
     * @since 1.7
     */
    public HashSet<Integer> removeLeaves(HashSet<Integer> domain) {
	domain = new HashSet<Integer>(domain);
	var leaves = leaves(domain);
	var inverse= inverse(domain);

	while(leaves.size() > 0) {
	    int leaf = leaves.iterator().next();
	    leaves.remove(leaf);
	    domain.remove(leaf);

	    for(Edge edge : inverse.getList_conservative(leaf)) {
		if(domain.contains(edge.target))
		    if(isLeaf(edge.target, domain))
			leaves.add(edge.target);
	    }
	}

	return domain;
    }

    /**
     * Removes all sources from a DiGraph
     * <p>
     * A source is a node which does not contain incoming edges,
     * but may contain edges leading away from itself.
     * <p>
     * Because removing sources may create more sources,
     * this continues until no sources remain. The remaining graph
     * is either empty, or cyclic.
     * 
     * @param domain domain to remove sources from
     * @return a restricted domain which contains no sources
     * @since 1.7
     */
    public HashSet<Integer> removeSources(HashSet<Integer> domain) {
	var inv = inverse(domain);
	domain = inv.removeLeaves(domain);
	return domain;
    }
    
    /**
     * Gets a topological sorting on a set of nodes.
     * <p>
     * It is assumed that the graph is acyclic. If it is not, return null;
     * <p>
     * This function has no side-effects.
     * @param considered set of nodes to consider
     * @return a topological ordering of the given graph, if it exists. Otherwise, null.
     * @since 1.5
     */
    public LinkedList<Integer> topologicSort(HashSet<Integer> considered) {	
	if(!acyclic(considered))
	    return null;

	considered = new HashSet<Integer>(considered);

	//get the inverse mapping of the graph
	DiGraph inverse = inverse(considered);

	LinkedList<Integer> res = new LinkedList<Integer>();
	var leaves = leaves(considered);

	while(leaves.size() > 0) {
	    int leaf = leaves.iterator().next();
	    leaves.remove(leaf);
	    considered.remove(leaf);

	    res.push(leaf);

	    //see if we made any more leaves
	    for(Edge inv : inverse.getList_conservative(leaf))
		//if that node is in our graph
		if(considered.contains(inv.target))
		    //if that node is now a leaf
		    if(isLeaf(inv.target, considered))
			//add it to the set of leaves
			leaves.add(inv.target);		    
	    
	}

	return res;
    }

    /**
     * Gets the distance from a node to all other reachable nodes in the domain
     * <p>
     * This distance is the raw 'number of hops', rather than any weighting which may be attached to an edge.
     * This is also a method to get all nodes that are reachable from a source node, which is the
     * keyset of the result.
     *
     * @param domain the domain of this graph
     * @param startNode the node to start exploring from
     *
     * @return The distance from a node to all other reachable nodes in the domain
     * @since 1.7
     */
    public HashMap<Integer, Integer> dists(HashSet<Integer> domain, int startNode) {
	HashMap<Integer, Integer> res = new HashMap<Integer, Integer>();

	HashSet<Integer> visited = new HashSet<>();
	LinkedList<Edge> edges = new LinkedList<Edge>();

	if(domain.contains(startNode)) {
	    for(var edge : getList_conservative(startNode)) {
		if(domain.contains(edge.target))
		    /* note - source = 0 filters out duplicates for free */
		    edges.add(new Edge(0, edge.target, 1));
	    }
	    
	    res.put(startNode, 0);
	    visited.add(startNode);
	}

	while(edges.size() > 0) {
	    Edge edge = edges.pop();	    
	    
	    int node = edge.target;
	    if(!visited.contains(node)) {
		res.put(node, edge.dist);
		visited.add(node);
		for(Edge e : getList_conservative(node))
		    if(domain.contains(edge.target))
			/* note - source = 0 filters out duplicates for free */
			edges.add(new Edge(0, e.target, edge.dist+1));
	    }
	}

	return res;
    }

    /**
     * Gets the distance from any member of a set to all other reachable nodes in the domain
     * <p>
     * This distance is the raw 'number of hops', rather than any weighting which may be attached to an edge.
     * This is also a method to get all nodes that are reachable from the source collection, which is the
     * keyset of the result.
     *
     * @param domain the domain of this graph
     * @param startNodes the set of nodes to start exploring from
     *
     * @return The distance from any member of a set to all other reachable nodes in the domain
     * @since 1.7
     */
    public HashMap<Integer, Integer> dists(HashSet<Integer> domain, Set<Integer> startNodes) {
	HashMap<Integer, Integer> res = new HashMap<Integer, Integer>();

	HashSet<Integer> visited = new HashSet<>();
	LinkedList<Edge> edges = new LinkedList<Edge>();

	for(Integer startNode : startNodes) {
	    if(domain.contains(startNode)) {
		for(var edge : getList_conservative(startNode)) {
		    if(domain.contains(edge.target))
			/* note - source = 0 filters out duplicates for free */
			edges.add(new Edge(0 /*startNode*/, edge.target, 1));
		}

		res.put(startNode, 0);
		visited.add(startNode);
	    }
	}

	while(edges.size() > 0) {
	    Edge edge = edges.pop();	    
	    
	    int node = edge.target;
	    if(!visited.contains(node)) {
		res.put(node, edge.dist);
		visited.add(node);
		for(Edge e : getList_conservative(node))
		    if(domain.contains(edge.target))
			/* note - source = 0 filters out duplicates for free */
			edges.add(new Edge(0 /*node*/, e.target, edge.dist+1));
	    }
	}
	
	return res;
    }

    /**
     * Finds the longest complete cycle through the given domain.
     * This requires that the domain is entirely connected.
     * 
     */    
    public Integer longest_complete_cycle(HashSet<Integer> domain) {
	if(stronglyConnectedComponents(domain).size() > 1)
	    return -1;
	
	//pick a random member to start at
	var start = domain.iterator().next();

	//System.out.println(domain);
	Path p = new Path(start);

	LinkedList<Path> paths = new LinkedList<Path>();
	paths.add(p);

	ArrayList<Path> complete = new ArrayList<Path>();

	while(paths.size() > 0) {
	    var ct = paths.poll();
	    var available_domain = new HashSet<Integer>();
	    available_domain.addAll(domain);
	    available_domain.removeAll(ct.visited);

	    if(available_domain.size() == 0)
		complete.add(new Path(ct, start));
	    else
		for(var next : available_domain)		    
		    paths.add(new Path(ct, next));
	}

	int best = complete.get(0).dist;
	for(var path : complete)
	    best = Math.max(best, path.dist);

	return best;
    }

    private class Path implements Comparable<Path> {
	public Path(int start) {
	    visited.add(start);
	    dist = 0;
	    last = start;
	}

	public Path(Path p, int next) {
	    this.visited.addAll(p.visited);
	    this.visited.add(next);
	    dist = weight(p.last, next) + p.dist;
	    last = next;
	}
	
	HashSet<Integer> visited = new HashSet<Integer>();
	int dist;
	int last;

	public int compareTo(Path p) {
	    return dist - p.dist;
	}
    }
}
