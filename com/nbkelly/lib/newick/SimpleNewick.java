package com.nbkelly.lib.newick;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.TreeMap;
import com.nbkelly.lib.DiGraph;

public class SimpleNewick {
    private int _unnamed_nodes = 0;
    private String tree_unprocessed;
    private String tree_processed;
    private TreeMap<Integer, String> ids = new TreeMap<Integer, String>();
    private TreeMap<Integer, Integer> dists = new TreeMap<Integer, Integer>();
    private Network output_network = new Network();
    private Node resultNode = null;

    public DiGraph toGraph() {
	return output_network.toGraph();
    }
    
    /**
     * Creates an object representation of a newick tree, which can be used to determine distances
     * within the tree of named nodes.
     *
     * @param tree the tree to process
     */
    public SimpleNewick(String tree) {
	this.tree_unprocessed = tree;
	/* pre-process the tree - insert all empty nodes, codify all labels */
	this.tree_processed = process_tree(this.tree_unprocessed);
	/* post-process the tree into an actual tree */
	Result<Node> res = process_processed(this.tree_processed);
	this.resultNode = res.result;

	/* transform the tree into a network */
	init_network(this.resultNode, 0);
    }

    /**
     * Gets the distance between two named nodes within the tree.
     * @param from node to route from
     * @param to node to route to
     * @return distance between from and to, if it exists. Otherwise -1.
     */
    public int distance(String from, String to) {
	int fromId = output_network.getID(from);
	int toId = output_network.getID(to);

	if(fromId == -1 || toId == -1)
	    return -1;

	return output_network.distance(fromId, toId);
    }


    /* process a tree by removing all names and replacing them with identifiers */
    private String process_tree(String tree) {
	String res = "";
	for(int i = 0; i < tree.length(); i++) {
	    char c = tree.charAt(i);
	    switch (c) {		
	    case '(':
		if(tree.charAt(i+1) == '(') {
		    res = res + c;		    
		}
		else {
		    res = res + c;
		    var name_result = name(tree, i+1, 0);
		    res = res + name_result.result;
		    i = name_result.index - 1;
		    continue;
		}
		break;
	    case ',':
		if(tree.charAt(i+1) == '(')
		    res = res + c;
		else {
		    res = res + c;
		    var name_result = name(tree, i+1, 0);
		    res = res + name_result.result;
		    i = name_result.index-1;
		}
		break;
	    case ')':
		res = res + c;
		var name_result2 = name(tree, i+1, 0);
		res = res + name_result2.result;
		i = name_result2.index - 1;
		break;
	    case ';':
		return res + c;
	    }
	}

	return res;
    }

    /* initialize the resultant network - this is what we use to perform calculations */
    private void init_network(Node node, int depth) {
	if(depth == 0) {
	    //this node has no parent, only children
	    //start acting from children
	    for(int i : ids.keySet()) {
		String _full_label = ids.get(i);//output_network.putLabel(i, ids.get(i));
		String[] _label_split = _full_label.split(":");
		if(_label_split.length > 1) {
		    ids.put(i, _label_split[0]);
		    dists.put(i, Integer.parseInt(_label_split[1]));
		}
		else
		    dists.put(i, 1);
	    }
	    
	    for(Node n : node.children) {
		init_network(n, depth+1);
	    }

	    for(int i : ids.keySet()) {
		output_network.putLabel(i, ids.get(i));
	    }

	    //System.out.println(output_network.toString());
	}
	else {
	    //we know the parent node
	    for(Integer i : node.members) {
		//see if we can get a distance		
		output_network.putRelationship(node.label, i, dists.get(i));
	    }
	    for(Node n : node.children) {
		init_network(n, depth+1);
	    }
	}
    }

    
    
    private class Node {	
	ArrayList<Node> children = new ArrayList<Node>();
	TreeSet<Integer> members = new TreeSet<Integer>();
	int label = -1;

	private String lpad(String s) {
	    s = s.replaceAll("\n", "\n    ");
	    s = "    " + s;
	    return s;
	}
	
	public String toString() {
	    String _members = " ";
	    for(int i : members) {
		_members = _members + i + " ";
	    }
	    
	    String res = String.format("NODE LABEL: %d, MEMBERS (%s), {", label, _members);
	    
	    for(Node n : children) {
		res = res + "\n" + lpad(n.toString());
	    }

	    if(children.size() > 0)
		res += "\n}";
	    else
		res += "}";
	    
	    return res;
	}
    }

    private Result<Node> process_processed(String tree) {
	return process_processed(tree, 0, 0);	
    }

    private Result<Node> process_processed(String tree, int index, int depth) {
	String current_digit = "";
	Node currentNode = new Node();
	Node lastNode = null;
	boolean setLabel = false;
	for(int i = index; i < tree.length(); i++) {
	    char c = tree.charAt(i);
	    //we're done
	    if(c == ';') {
		//do we have a number
		
		if(setLabel) {
		    lastNode.label = (Integer.parseInt(current_digit));
		    setLabel = false;
		}
		currentNode.members.add(Integer.parseInt(current_digit));
		
		return new Result<Node>(currentNode, i);
	    }
	    //comma - add character to set
	    else if(c == ',') {
		if(setLabel) {
		    lastNode.label = (Integer.parseInt(current_digit));
		    setLabel = false;
		}
		currentNode.members.add(Integer.parseInt(current_digit));
		current_digit = "";
	    }
	    //expand current digit
	    else if (c >= 0x30 && c <= 0x39) {
		current_digit = current_digit + c;
	    }
	    else if (c == '(') {
		if(!current_digit.equals("")) {
		    if(setLabel) {
			lastNode.label = (Integer.parseInt(current_digit));
			setLabel = false;
		    }
		    currentNode.members.add(Integer.parseInt(current_digit));
		    current_digit = "";
		}

		Result<Node> res = process_processed(tree, i+1, depth+1);
		lastNode = res.result;
		i = res.index;
		currentNode.children.add(lastNode);
		setLabel = true;
	    }
	    else if (c == ')') {
		if(!current_digit.equals("")) {
		    if(setLabel) {
			lastNode.label = (Integer.parseInt(current_digit));
			setLabel = false;
		    }
		    currentNode.members.add(Integer.parseInt(current_digit));
		    current_digit = "";
		}

		return new Result<Node>(currentNode, i);
	    }
	}

	return new Result<Node>(currentNode, 0);
    }
    
    private Result<String> name(String sequence, int index, int depth) {
	++_unnamed_nodes;
	Result<String> nr = _name(sequence, index, depth+1);
	
	if(nr == null)
	    return null;
	else {
	    String out = nr.result;
	    if(nr.result == "")
		out = String.format("UNLABELED_NODE_%03d", _unnamed_nodes);
	    ids.put(_unnamed_nodes, out);
	    nr.result = "" + _unnamed_nodes;
	    return nr;
	}
    }
    
    private Result<String> _name(String sequence, int index, int depth) {
	String str = "";
	
	for(int i = index; i < sequence.length(); i++) {
	    //start with the simple one : quotes do not exist
	    char c = sequence.charAt(i);

	    switch(c) {
	    case ',':
		//case ':':
	    case ';':
	    case '(':
	    case ')':
		return new Result<String>(str, i);
	    default:
		str = str + c;
	    }
	}
	return new Result<String>(str, index);
    }
}
