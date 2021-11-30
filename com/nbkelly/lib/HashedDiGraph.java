package com.nbkelly.lib;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Set;

/**
 * A directed graph on a hashable object type.
 *
 * @author      NB Kelly <nbkelly @ protonmail.com>
 * @version     2.2
 * @since       2.2
 */
public class HashedDiGraph<Hashable> {
    DiGraph baseGraph = new DiGraph();

    HashMap<Hashable, Integer> ids = new HashMap<Hashable, Integer>();
    HashMap<Integer, Hashable> rev_ids = new HashMap<>();

    HashSet<Hashable> meta_domain = new HashSet<Hashable>();
    private int __ID = 0;

    private Integer identify(Hashable h) {
	if(!ids.containsKey(h)) {
	    rev_ids.put(__ID, h);
	    ids.put(h, __ID++);
	    meta_domain.add(h);
	}

	return ids.get(h);
    }

    public String toString() {
	var res = ids.toString() + "\n";
	res += baseGraph.toString();

	return res;
    }

    /**
     * Note: this may return null!
     */
    private Hashable recover(Integer id) {
	return rev_ids.get(id);
    }

    /**
     * Create a length one link from one node to another.
     *
     * @param from Node to link from
     * @param to Node to link to
     * @since 2.2
     */
    public void link(Hashable from, Hashable to) {
	baseGraph.link(identify(from), identify(to));
    }

    /**
     * Create a link from one node to another.
     *
     * @param from Node to link from
     * @param to Node to link to
     * @param weight distance of the link
     * @since 2.2
     */
    public void link(Hashable from, Hashable to, int weight) {
	baseGraph.link(identify(from), identify(to), weight);
    }

    /**
     * Create a bijective link between two nodes.
     * @param from first node to link
     * @param to second node to link
     * @param weight weight of the link
     * @since 2.2
     */
    public void join(Hashable left, Hashable right, int weight) {
	baseGraph.join(identify(left), identify(right), weight);
    }

    /**
     * Create a bijective link between two nodes with weight 1.
     * @param from first node to link
     * @param to second node to link
     * @since 2.2
     */
    public void join(Hashable left, Hashable right) {
	baseGraph.join(identify(left), identify(right));
    }

    /**
     * Gets the shortest weight (one link max) from one node to another.
     * @param from the first node
     * @param to the second node
     */
    public Integer weight(Hashable from, Hashable to) {
	return baseGraph.weight(identify(from), identify(to));
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
     * @since 2.2
     */
    public HashMap<Hashable, WrappedBigInt> shortest_path(HashSet<Hashable> considered,
							  Hashable from_node){
	HashSet<Integer> ids = new HashSet<Integer>();
	for(var h : considered)
	    ids.add(identify(h));

	var target_id = identify(from_node);

	var intermediate = baseGraph.shortest_path(ids, target_id);

	HashMap<Hashable, WrappedBigInt> res = new HashMap<>();

	for(var entry : intermediate.entrySet())
	    res.put(recover(entry.getKey()), entry.getValue());

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
     * @since 2.2
     */
    public WrappedBigInt shortest_path(HashSet<Hashable> considered, Hashable from_node,
				       Hashable to_node) {
	HashSet<Integer> ids = new HashSet<Integer>();
	for(var h : considered)
	    ids.add(identify(h));

	var target_id = identify(from_node);
	var target_id_2 = identify(to_node);

	return baseGraph.shortest_path(ids, target_id, target_id_2);
    }

    private HashSet<Integer> conversion(HashSet<Hashable> graph) {
	HashSet<Integer> res = new HashSet<>();

	for(var h : graph)
	    res.add(identify(h));

	return res;
    }

    private HashSet<Hashable> reverse_conversion(HashSet<Integer> graph) {
	HashSet<Hashable> res = new HashSet<>();

	for(var i : graph)
	    res.add(recover(i));

	return res;
    }
    
    /**
     * Determine if a graph is cyclic within a given range of nodes
     * <p>
     * This function has no side-effects
     * @param graph the nodes that make up this graph
     * @return true if it's cyclic, false if it contains no cycles
     * @since 2.2
     */
    public boolean cyclic(HashSet<Hashable> graph) {
	return baseGraph.cyclic(conversion(graph));
    }

    /**
     * Determine if a graph is acyclic within a given set of nodes.
     * <p>
     * This function has no side-effects
     * @param domain the nodes that make up the domain of this graph
     * @return true if it's acyclic, false if it contains cycles
     * @since 2.2
     */
    public boolean acyclic(HashSet<Hashable> domain) {
	return baseGraph.acyclic(conversion(domain));
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
     * @since 2.2
     */
    public ArrayList<HashSet<Hashable>> stronglyConnectedComponents(HashSet<Hashable> domain) {
	var idlist = baseGraph.stronglyConnectedComponents(conversion(domain));

	ArrayList<HashSet<Hashable>> res = new ArrayList<>();

	for(var set : idlist)
	    res.add(reverse_conversion(set));

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
     * @since 2.2
     */
    public HashSet<Hashable> negativeCycleMembers(HashSet<Hashable> domain) {	
	return reverse_conversion(baseGraph.negativeCycleMembers(conversion(domain)));
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
     * @since 2.2
     */
    public boolean containsNegativeCycles(HashSet<Hashable> domain) {
	return negativeCycleMembers(domain).size() > 0;
    }

    /**
     * Uses the Bellman-Ford algorithm to detect the presence of negative length cycles in
     * the domain of this graph.
     * <p>
     * This takes (edges * vertices) time
     * 
     * @param domain the domain of this graph
     * @return true if there is a negative length cycle in the domain, false otherwise
     * @since 2.2
     */
    public boolean bellmanFordNegatives(HashSet<Hashable> domain) {
	return baseGraph.bellmanFordNegatives(conversion(domain));
    }

    /**
     * Counts the number of edges in the graph
     * @param domain the domain of this graph
     * @return the number of edges in the graph
     * @since 2.2
     */
    public int numEdges(HashSet<Hashable> domain) {
	return baseGraph.numEdges(conversion(domain));
    }

    /**
     * Returns the length (weighted) of the shortest cycle through this edge, if one exists.
     * Otherwise, returns null
     * @param graph set of nodes to consider
     * @param from oririgin of edge to find the shortest cycle through
     * @param to destination of edge to find the shortest cycle through
     * @return the length (weighted) of the shortest cycle through this edge if it exists, or null
     * @since 2.2
     */
    public WrappedBigInt shortestCycle(HashSet<Hashable> graph, Hashable from, Hashable to) {
	return baseGraph.shortestCycle(conversion(graph), identify(from), identify(to));
    }

    /**
     * returns the inverse set of this graph
     * @param domain the domain of the given graph
     * @return an inverse of the graph over the given domain
     * @since 2.2
     */
    @SuppressWarnings("unchecked")
    public HashedDiGraph inverse(HashSet<Hashable> domain) {
	HashedDiGraph inv = new HashedDiGraph();
	inv.__ID = __ID;
	inv.ids.putAll(ids);
	inv.rev_ids.putAll(rev_ids);
	inv.meta_domain.addAll(meta_domain);

	inv.baseGraph = baseGraph.inverse(conversion(domain));

	return inv;
    }

    /**
     * returns the set of all leaves in this graph
     * @param domain the domain of the graph
     * @return the set of all leaves in this graph
     * @since 2.2
     */
    public HashSet<Hashable> leaves(HashSet<Hashable> domain) {
	return reverse_conversion(baseGraph.leaves(conversion(domain)));
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
     * @since 2.2
     */
    public boolean semiConnected(HashSet<Hashable> domain) {
	return baseGraph.semiConnected(conversion(domain));
    }

    /**
     * Returns a node which is a universal source, if one exists
     * @param domain domain of the graph
     * @return a node which is a universal source if one exists, or null
     * @since 2.2
     */
    public Hashable universalSource(HashSet<Hashable> domain) {
	var tmp = baseGraph.universalSource(conversion(domain));
	if(tmp == null)
	    return null;
	else
	    return recover(tmp);
    }

    /**
     * returns the set of all sources in this domain
     * @param domain the domain of the graph
     * @return the set of all sources in the domain
     * @since 2.2
     */
    public HashSet<Hashable> sources(HashSet<Hashable> domain) {
	return reverse_conversion(baseGraph.sources(conversion(domain)));
    }

    public Integer longest_complete_cycle(HashSet<Hashable> domain) {
	return baseGraph.longest_complete_cycle(conversion(domain));
    }
    
    /**
     * Helper functions:
     *  Conversion (set(hashable) -- set(integer))
     *  identify(hashable)
     *  recover(id)
     *  reverse_conversion(set(integer) -- set(hashable))
     */

    /*
     * TODO:
     * * isLeaf
     * * hamiltonianPath
     * * longest_Map
     * * removeLeaves
     * removeSources
     * topologicalSort
     * dists(2)
     */ 
}
