package com.nbkelly.lib;

import java.util.HashMap;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import com.nbkelly.lib.Util;

public class Trie {
    Node head = new Node('^', null, 0);
    boolean first = true;
    boolean prune = true;

    public TreeSet<String> longest() {
	int max = 0;

	LinkedList<Node> res_nodes = new LinkedList<Node>();
	LinkedList<Node> considered = new LinkedList<Node>();
	considered.add(head);

	while(considered.size() > 0) {
	    var current = considered.pollFirst();
	    if(current.height > max) {
		res_nodes = new LinkedList<Node>();
		max = current.height;
	    }

	    if(current.height == max)
		res_nodes.add(current);
	    
	    for(var entry : current.children.entrySet())
		considered.push(entry.getValue());
	}

	TreeSet<String> res = new TreeSet<String>();
	for(Node n : res_nodes) {
	    StringBuilder str = new StringBuilder();
	    while(!n.equals(head)) {
		str.insert(0, n.token);
		n = n.parent;
	    }
	    res.add(str.toString());
	}

	return res;
    }

    public TreeSet<String> longest(int min_occurances) {
	int max = 0;

	LinkedList<Node> res_nodes = new LinkedList<Node>();
	LinkedList<Node> considered = new LinkedList<Node>();
	considered.add(head);

	while(considered.size() > 0) {
	    var current = considered.pollFirst();
	    if(current.occurances >= min_occurances) {
		if(current.height > max) {
		    res_nodes = new LinkedList<Node>();
		    max = current.height;
		}
		
		if(current.height == max)
		    res_nodes.add(current);
		
		for(var entry : current.children.entrySet())
		    considered.push(entry.getValue());
	    }
	}

	TreeSet<String> res = new TreeSet<String>();
	for(Node n : res_nodes) {
	    StringBuilder str = new StringBuilder();
	    while(!n.equals(head)) {
		str.insert(0, n.token);
		n = n.parent;
	    }
	    res.add(str.toString());
	}

	return res;
    }
    
    public ArrayList<String> toList() {
	LinkedList<Node> considered = new LinkedList<Node>();
	considered.add(head);

	ArrayList<String> res = new ArrayList<String>();
	
	while(considered.size() > 0) {
	    var ct = considered.poll();

	    for(var child : ct.children.entrySet()) {
		res.add(String.format("%d %d %c", ct.id, child.getValue().id, child.getKey()));
		considered.push(child.getValue());
	    }
	}


	Collections.sort(res, new Comparator<String>() {
		public int compare(String left, String right) {
		    return Util.compareTo((Integer)Integer.parseInt(left.split(" ")[1]),
					  (Integer)Integer.parseInt(right.split(" ")[1]));
		}
	    });
	
	return res;
    }
    
    public void add(String source) {
	head.visit();

	for(int i = 0; i < source.length() - 1; i++)
	    add_suffix(source.substring(i));

	first = false;

	if(prune)
	    prune(head);
    }

    public void add_full(String source) {
	head.visit();

	add_suffix(source);

	if(prune)
	    prune(head);
    }

    private void prune(Node ct) {
	HashMap<Character, Node> output_set = new HashMap<Character, Node>();
	ct.fresh = false;
	
	for(var entry : ct.children.entrySet()) {
	    var child = entry.getValue();
	    if(child.fresh) {
		output_set.put(entry.getKey(), child);
		prune(child);
	    }
	}

	ct.children = output_set;
    }    
    
    private void add_suffix(String str) {
	Node ct = head;
	
	for(int i = 0; i < str.length(); i++) {
	    ct.visit();
	    char c = str.charAt(i);

	    if(!ct.children.containsKey(c)) {
		if(!first)
		    break;
		Node newNode = new Node(c, ct, ct.height + 1);
		ct.children.put(c, newNode);
		ct = newNode;
	    }
	    else
		ct = ct.children.get(c);
	}

	ct.visit();
    }

    public Trie() {
    }

    public Trie(boolean prune) {
	this.prune = prune;
    }

    static int __ID = 0;
    private class Node {
	public Node(char token, Node parent, int height) {
	    this.token = token;
	    this.fresh = true;
	    this.parent = parent;
	    this.height = height;
	}

	public void visit() {
	    this.fresh = true;
	    occurances++;
	}
	
	char token;
	boolean fresh = false;
	Node parent;
	int height = 0;
	int id = ++__ID;
	int occurances = 1;
	
	HashMap<Character, Node> children = new HashMap<Character, Node>();
    }
}
