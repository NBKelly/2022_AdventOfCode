package com.nbkelly.lib.rev;

import java.util.ArrayList;
import java.util.List;

/**
 * @author NB Kelly <N.B.Kelly@protonmail.com>
 * @date 28 May, 2020
 */
public class ComponentTree {
    private SquareNode root;
    private int square_nodes = 0;
    private int round_nodes = 0;
    private int[] sequence;
    private int len;
    private ArrayList<Integer> breakpoints;
    private ArrayList<RoundNode> leaf_nodes;

    /**
     * Returns true if this ComponentTree has a 'short branch.'
     * <p>
     * A short branch is a defined as an unoriented leaf (all leaves must be unoriented)
     * has no path to any other unoriented leaf that doesn't path through a node of order &ge; 3.
     * @return if this ComponentTree has a 'short branch'
     */
    public boolean has_short_branch() {
	if(leaf_nodes == null)
	    return false;
	for(RoundNode n : leaf_nodes) {
	    //we know the order of this is zero
	    //let's go up a level
	    if(is_short_branch(n)) {
		return true;
	    }
	}

	return false;
    }

    /**
     * determins if a leaf is part of a short branch
     */
    private boolean is_short_branch(RoundNode leaf) {
	assert leaf.order() == 0 : "leaf bad order";
		
	//a node is part of a short branch if,
	//the set of nodes going right up and then up until we hit a node of
	//degree >= 3 contains exactly 1 unoriented component

	//if the thing above us is order >= 3, then this is a short branch
	if(leaf.parent.order() >= 3)
	    return true;
	else {
	    SquareNode parent = leaf.parent;
	    //we know for the fact the next one up will be degree >= 3
	    for(int i = 0; i < parent.nodes.length; i++) {
		if(i == leaf.index)
		    continue;

		RoundNode n = parent.nodes[i];

		if(n.unoriented_children() > 0)
		    return false;
	    }
	}

	return true;
    }
	
    /**
     * Returns the number of leaves in this tree.
     * @return the number of leaves in this tree.
     */
    public int leaves() {
	if(leaf_nodes != null)
	    return leaf_nodes.size();
	    
	if(root == null)
	    return 0;
	return root.leaves();
    }

    /**
     * Returns a modified tree, where no nodes exist but those which have unoriented children.
     * @return a modified tree, where no nodes exist but those which have unoriented children.
     */
    public ComponentTree TreeDash() {
	//we construct a tree from the rhs nodes, only taking nodes which have unoriented children
	ComponentTree newTree = new ComponentTree(breakpoints, sequence, len,
						  root);
	    
	return newTree;
    }

    /**
     * private construct used with treedash
     */
    private ComponentTree(ArrayList<Integer> breakpoints,
			  int[] sequence, int len, SquareNode root) {
	leaf_nodes = new ArrayList<RoundNode>();
	this.sequence = sequence;
	this.len = len;
	this.breakpoints = breakpoints;

	this.root = new SquareNode(root, null);
    }

    /**
     * Constructs a ComponentTree from a list of components, breakpoints, and a sequence
     *
     * @param components the list of root components in the tree
     * @param breakpoints list of all breakpoints over the sequence
     * @param sequence sequence this tree operates over
     * @param len length of the sequence
     */
    public ComponentTree(List<Component> components, ArrayList<Integer> breakpoints,
			 int[] sequence, int len) {
	//get the root element
	root = new SquareNode(components, null);
	this.sequence = sequence;
	this.len = len;
	this.breakpoints = breakpoints;
    }



    /*
     * S Q U A R E  N O D E
     */


    /**
     * Square nodes have sets of round nodes as children. The root of the tree is a square node.
     */
    private class SquareNode {
	RoundNode[] nodes;	    
	RoundNode parent;

	/**
	 * Constructs a modified squarenode recursively from an existing squarenode.
	 * @param original the original squarenode to use
	 * @param parent of this squarenode. It may be null.
	 */
	public SquareNode(SquareNode original, RoundNode parent) {
	    if(original != null && original.unoriented_children() > 0) {
		//we need to
		ArrayList<RoundNode> tmp_nodes = new ArrayList<>();

		for(RoundNode n : original.nodes) {
		    if(n.unoriented_children() > 0)
			tmp_nodes.add(n);
		}

		assert tmp_nodes.size() > 0 : "children missing?";

		nodes = new RoundNode[tmp_nodes.size()];
		for(int i = 0; i < tmp_nodes.size(); i++) {
		    nodes[i] = new RoundNode(tmp_nodes.get(i), this, i);
		}
	    }
	    else {
		++square_nodes;
		nodes = new RoundNode[0];
		//this is essentially null		    
	    }

	    this.parent = parent;
	}

	/**
	 * Constructs a SquareNode from a set of components
	 * @param components components from which to construct this node
	 * @param parent parent of this node (may be null for root)
	 */
	public SquareNode(List<Component> components, RoundNode parent) {
	    ++square_nodes;
	    nodes = new RoundNode[components.size()];
	    for(int i = 0; i < components.size(); i++) {
		nodes[i] = new RoundNode(components.get(i), this, i);
	    }

	    this.parent = parent;
	}

	
	/**
	 * Returns the number of leaves stemming from this node. This node cannot be a leaf.
	 * @return the number of leaves stemming from this node. This node cannot be a leaf.
	 */
	public int leaves() {
	    int leaves = 0;
	    for(RoundNode r : nodes)
		leaves += r.leaves();
		
	    return leaves;
	}

	/**
	 * Returns true if this node has children
	 * @return true if this node has children
	 */
	public boolean hasChildren() {
	    return nodes.length > 0;
	}
	    
	
	/**
	 * Returns the order (total number of children) of this node.
	 * @return the order (total number of children) of this node.
	 */
	public int order() {
	    if(nodes.length == 0)
		return 0;
	    else {
		int order = nodes.length;
		for(RoundNode r : nodes)
		    order += r.order();
		return order;
	    }
	}
	

	/**
	 * returns a formatted string representation of this node
	 * @return a formatted string representation of this node
	 */
	public String format() {
	    String res = "[] -> {\n";
	    for(RoundNode r : nodes) {
		String mid = r.format();
		mid = RevUtil.padlines(mid, 4);
		res = res + mid + "\n";
	    }
	    res = res + "}";

	    return res;
	}


	//TODO: cache values as I count children
	/**
	 * Returns a count of the number of unoriented children this node has.
	 * @return a count of the number of unoriented children this node has.
	 */
	public int unoriented_children() {
	    int uoc = 0;
	    for(RoundNode n : nodes) {
		uoc += n.unoriented_children();
	    }
	    return uoc;
	}
    }


    /*
     * R O U N D  N O D E
     */

    /**
     * A round-node represents an interval, and may have a squarenode child
     */
    private class RoundNode {
	private SquareNode child;
	private SquareNode parent;
	private Component value;
	private int index;

	/**
	 * Constructs a modified roundnode from an already existing roundnode.
	 *
	 * @param origin the roundnode we're  apeing
	 * @param parent the parent of this new node
	 * @param index the index of this new node within the parent
	 */
	public RoundNode(RoundNode original, SquareNode parent, int index) {
	    this.parent = parent;
	    ++round_nodes;
	    value = original.value;
	    this.index = index;
	    if(original.unoriented_children() > 0) {
		child = new SquareNode(original.child, this);
	    }
		
	    //post-prune
	    if(child != null && !child.hasChildren()) {
		child = null;
		square_nodes--;
	    }

	    if(child == null)
		leaf_nodes.add(this);
	}

	/**
	 * Constructs a roundnode from a components, preserving the parent and index.
	 * @param c Component from which to male this node
	 * @param parent parent of this node
	 * @param index index of this node within parent
	 */
	public RoundNode(Component c, SquareNode parent, int index) {
	    this.parent = parent;
	    this.index = index;
	    ++round_nodes;
	    value = c;

	    if(value.children().size() > 0)
		child = new SquareNode(value.children(), this);
	}

	/**
	 * Returns the order (total number of children) of this node.
	 * @return the order (total number of children) of this node.
	 */
	public int order() {
	    if(child != null)
		return child.order() + 1;
	    return 0;
	}

	/**
	 * Returns the total number of leaves belonging to this node, including itself.
	 * @return the total number of leaves belonging to this node, including itself.
	 */
	public int leaves() {
	    if(child == null)
		return 1;
	    return child.leaves();
	}
	    
	/**
	 * Returns true if this node is oriented (TODO: cache)
	 * @return true if this node is oriented
	 */
	public boolean is_oriented() {
	    return value.oriented(breakpoints, sequence);
	}

	/**
	 * Returns the number of unoriented children (including this one) descending from this node.
	 * @return the number of unoriented children (including this one) descending from this node.
	 */
	public int unoriented_children() {
	    //TODO: CACHE
	    int uoc = (child == null) ? 0 : child.unoriented_children();

	    if(!value.oriented(breakpoints, sequence))
		uoc += 1;

	    return uoc;
	}

	/**
	 * Returns a formatted representation of this node
	 * @return a formatted representation of this node
	 */
	public String format() {
	    String res = String.format("(%d .. %d)", value.l_value(), value.r_value());

	    if(value.oriented(breakpoints, sequence))
		res = "+" + res;
	    else
		res = "-" + res;

	    if(child != null) {
		res = res + " -> \n";
		res = res + RevUtil.padlines(child.format(), 4);
	    }

	    return res;
	}
    }

    /**
     * Returns a formatted representation of this tree
     * @return a formatted representation of this tree
     */
    public String toString() {
	//give a nice pretty-printed string
	return format();
    }

    /**
     * Returns a formatted representation of this tree
     * @return a formatted representation of this tree
     */
    public String format() {
	//give a nice pretty-printed string
	return root.format();
    }
}
