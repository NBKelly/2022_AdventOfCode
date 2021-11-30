package com.nbkelly.lib.newick;

import java.util.TreeMap;
import java.util.ArrayList;
import java.util.TreeSet;

import com.nbkelly.lib.DiGraph;

/**
 * @author NB Kelly <N.B.Kelly@protonmail.com>
 * @since Jun 03, 2020
 * @last Mar 22, 2021
 *
 * Class representing a 'network', or collection of nodes forming a bijective graph. All
 * links are bijective. That is, A -&gt; B <i>iff<\> B -&gt; A, with both paths having equal length.
 */
public class Network {
    //mappings of name -> id
    private TreeMap<Integer, String> labels = new TreeMap<Integer, String>();
    //given an index, find the target indexes from that index 
    private ArrayList<Targets> targets = new ArrayList<Targets>();
    //contains information about a node (and how it is mapped into our data structure)
    private TreeMap<Integer, Node> nodes = new TreeMap<Integer, Node>();

    public DiGraph toGraph() {
	DiGraph g = new DiGraph();

	for(var entry : nodes.entrySet()) {
	    //mapping of integer key -> node
	    int key = entry.getKey();
	    var node = entry.getValue();
	    String name = labels.get(node.id); //may be null
	    //get the targets associated with this node
	    var node_targets = targets.get(node.index);

	    if(name != null)
		g.setName(node.id, name);

	    for(var target : node_targets.targets) {
		g.link(node.id, target.target, target.length);
		g.link(target.target, node.id, target.length);
	    }
	}

	return g;
    }
    
    /**
     * Returns a string representation of this Network.
     * @return a string representation of this Network.
     */
    public String toString() {
	//header
	String res = "///*** NETWORK ***///\n";
	res += "  LABELS:";
	for(int i : labels.keySet()) {
	    res += String.format("%n    %d ---> '%s'", i, labels.get(i));
	    //res += "\n    " + i + " ---> '" + labels.get(i) + "'";
	}
	res += String.format("%n  NODES:");

	for(int i : nodes.keySet()) {
	    res += String.format("%n    %d ---> %d[%d]", i, nodes.get(i).id, nodes.get(i).index);	    
	    for(Target t : targets.get(nodes.get(i).index).targets) {
		res += String.format("%n      ==> (%d, %d)", t.target, t.length);
	    }
	}
	
	return res;
    }

    /**
     * Given a label, returns the corresponding ID.
     * <p>
     * Given the label of a node within the network, returns the ID of that node.
     * If there is no valid id matching that node, return -1 instead.
     * @param label label of a node within the network
     * @return id of labeled node, or -1 if node does not exist
     */
    public int getID(String label) {
	for(Integer i : labels.keySet()) {
	    if(labels.get(i).equals(label))
		return i;
	}

	return -1;
    }

    /**
     * Gets the distance between two nodes within the network if a path exists.
     * <p>
     * @param from node to route from
     * @param to node to route to
     * @return the distance between the two nodes, or -1 if no link exists.
     */
    public int distance(int from, int to) {
	if(from == to)
	    return 0;
	else {
	    /* if we already visited the node by the shortest path, then we shouldn't process it again */
	    boolean[] visited = new boolean[targets.size()+1];
	    /* sanity double-check for ordering */
	    int[] lowest = new int[targets.size()+1];

	    /* mark that we have visited the initial node */
	    visited[from] = true;

	    /* populate the initial target list with the targets of 'from' node */
	    Node n = nodes.get(from);	    
	    TreeSet<Target> _targets = new TreeSet<Target>();	    
	    for(Target t : targets.get(n.index).targets) {
		_targets.add(t);
		Node current = nodes.get(t.target);
	    }

	    /* so long as we have targets in our list, continue to process them */
	    while(_targets.size() > 0) {
		/* this will be the path with the shortest total distance */
		Target t = _targets.pollFirst();
		/* get the target node's id */
		int new_target = t.target;
		/* get the node out of the target */
		Node current = nodes.get(new_target);

		/* if the current id is 'to', then return length to that node */
		if(current.id == to)
		    return t.length;

		/* if we haven't visited the new target, or we're the lowest score at the new target,
		   or the lowest score is marked as 0 (uninitialized), process this node */
		if(!visited[new_target] || lowest[new_target] == 0 || lowest[new_target] > t.length) {
		    /* set visited/lowest status */
		    lowest[new_target] = t.length;
		    visited[new_target] = true;

		    /* construct a new target for each target the current node has */
		    for(Target t2 : targets.get(current.index).targets) {
			Target nextTarget = new Target(t2.target, t.length + t2.length);
			_targets.add(nextTarget);
		    }
		}
	    }
	}

	return 0;
    }    

    /**
     * Insert a relationship between two nodes into the network.
     * <p>
     * Inserts a bijective path of length 'length' into the network between nodes 'from' and 'to'.
     * If either of the nodes don't exist in the network, it will create them.
     * @param from node to route from
     * @param to nod to route to
     * @param length length of path
     */
    public void putRelationship(int from, int to, int length) {
	if(length < 0)
	    throw new IllegalArgumentException("Cannot have negative path lengths within this network");
	
	Node fromNode = assertNode(from);
	Node toNode = assertNode(to);

	//from -> to
	Targets fromTargets = targets.get(fromNode.index);
	fromTargets.add(new Target(to, length));

	//to -> from
	Targets toTargets = targets.get(toNode.index);
	toTargets.add(new Target(from, length));
    }

    /**
     * Asserts that a node exists. Creates the node if it does not.
     * @param id id of the node to assert
     */
    private Node assertNode(int id) {
	Node n = nodes.get(id);
	if(n == null) {
	    n = new Node(id, targets.size());
	    nodes.put(id, n);
	    targets.add(new Targets());
	}

	return n;
    }

    /**
     * Inserts a label for a node. Creates the node if it does not exist.
     * @param id id of the node to label.
     * @param label label of node being identified
     */
    public void putLabel(int id, String label) {
	if(!nodes.containsKey(id))
	    assertNode(id);
	labels.put(id, label);
    }
    
    private class Targets {
	//contains: node of interest
	ArrayList<Target> targets = new ArrayList<>();
	public void add(Target target) {
	    targets.add(target);
	}
    }

    private class Target implements Comparable<Target>{
	int target;
	int length;

	public Target(int target, int length) {
	    this.target = target;
	    this.length = length;
	}
	
	public boolean equals(Target t) {
	    return (t.target == target && t.length == length);
	}
	
	public boolean equals(Object o) {
	    if(o instanceof Target) return equals((Target)o);
	    return false;
	}

	public int compareTo(Target t) {
	    if(t.length == length)
		return Integer.compare(target, t.target);
	    else
		return Integer.compare(length, t.length);
	}
    }
    
    private class Node {
	public Node(int id, int index) {
	    this.id = id;
	    this.index = index;
	}
	
	int id;
	int index;

	public boolean equals(Node n) {
	    return n.id == id;
	}
	
	public boolean equals(Object o) {
	    if(o instanceof Node) return equals((Node)o);
	    return false;
	}
    }
}
