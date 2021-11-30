package com.nbkelly.lib.trie;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;

public class Node implements Comparable<Node> {
    public String name;
    public Node parent;
    public ArrayList<Node> children = new ArrayList<Node>();
    public int index_start;
    public int index_length;
    public String tag;
    public int length = -1;
    public int order = -1;

    public void edge(ArrayList<String> edges, String sequence) {
	//this just means we get all the tags?
	for(Node n : children) {
	    edges.add(n.tag(sequence));
	    n.edge(edges, sequence);
	}	
    }
    
    public int compareTo(Node n) {
	//we want to maximize length and order
	//that is, we want a node of order n or greater with the highest length
	int res = -1 * Integer.compare(order, n.order);
	if(res == 0)
	    res = -1 * Integer.compare(length, n.length);
	if(res == 0)
	    res = name.compareTo(n.name);
	
	return res;
    }
    
    //header node
    public Node(HashMap<String, Node> nodesByName) {
	this.name = "node1";
	nodesByName.put(this.name, this);
    }

    public Node(String name, String parent, String val, HashMap<String, Node> nodesByName) {
	Node _parent = nodesByName.get(parent);
	this.parent = _parent;
	this.name = name;
	nodesByName.put(name, this);

	tag = val;

	_parent.children.add(this);
	
    }
    
    public Node(String name, String parent, int start, int end, HashMap<String, Node> nodesByName) {
	Node _parent = nodesByName.get(parent);
	this.parent = _parent;
	this.name = name;
	nodesByName.put(name, this);

	this.index_start = start;
	this.index_length = end;

	_parent.children.add(this);
    }

    public String tag(String sequence) {
	if(tag != null)
	    return tag;

	tag = sequence.substring(index_start, index_start + index_length);
	for(Node n : children)
	    n.tag(sequence);

	return tag;
    }
    
    public int order() {
	if(order > -1)
	    return order;
	
	order = 0;
	if(children.size() == 0)		    
	    order = 1;
	else		    
	    for(Node n : children)
		order = order + n.order();
	
	return order;
    }

    public int len() {
	if(length > -1)
	    return length;
		
	return len(0);
    }

    public void collect(AbstractCollection<Node> c) {
	c.add(this);

	for(Node n : children)
	    n.collect(c);
    }
    
    public int len(int parlen) {
	if(length > -1)
	    return length;

	if(tag == null)
	    length = parlen + index_length;
	else
	    length = parlen + tag.length();
	
	for(Node n : children)
	    n.len(length);		
	
	return length;
    }    
}
