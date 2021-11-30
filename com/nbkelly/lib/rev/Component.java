package com.nbkelly.lib.rev;

import java.util.ArrayList;
import java.util.List;

/**
 * @author NB Kelly <N.B.Kelly@protonmail.com>
 * @date 28 May, 2020
 */
public class Component {
    private ArrayList<Component> children;
    private Cycle value;
    private int l_value;
    private int r_value;

    /**
     * Given a list of breakpoints and a sequence, determine the orientation of this component.
     * <p>
     * Given a list of breakpoints and a sequence, determine the orientation of this component.
     * A point p.q is positive if both p and q are positive, otherwise it is negative if both p and q are negative.
     * A component is unoriented if it has one or more breakpoints, and all of them have the same sign.
     * Otherwise it is oriented.
     * @param breakpoints a list of breakpoints for the sequence
     * @param sequence the sequence this component operates on
     *
     * @return true if this component is oriented, false otherwise
     */
    public boolean oriented(List<Integer> breakpoints, int[] sequence) {
	//TODO: cache this
	//a point p.q is oriented if sign(p) == sign(q) == 1
	//find all the breakpoints between l_index, r_index
	List<Integer> filtered = RevUtil.filter(breakpoints, l_index(), r_index());
	
	//a component with 0 breakpoints is oriented
	if(filtered.size() == 0) {
	    return true;
	}
	else {
	    //a component with 1 breakpoint must be unoriented
	    if(filtered.size() == 1)
		return false;
	    int first = filtered.get(0);
	    boolean positive_expected = sequence[first] >= 0 && sequence[first+1] >= 0;
	    for (int i : filtered) {
		//is this point positive
		boolean point_positive = sequence[i] >= 0 && sequence[i+1] >= 0;
		if(positive_expected != point_positive)
		    //this component has different signs, so it is orient
		    return true;
		
	    }
	    return false;
	}
    }

    /**
     * Returns the list of all (direct) children of this component.
     * @return the list of all (direct) children of this component.
     */
    public ArrayList<Component> children() {
	return children;
    }

    /**
     * Given a cycle and a sequence, construct a component.
     * @param value the cycle to construct this componet from
     * @param sequence the sequence this component operates on
     */
    public Component(Cycle value, int[] sequence) {
	this.value = value;
	children = new ArrayList<Component>();

	l_value = sequence[l_index()];
	r_value = sequence[r_index()];
    }

    /**
     * Adds a list of child components to this component
     * @param children the list of child components to add to this one
     */
    public void addChildren(List<Component> children) {
	this.children.addAll(children);
    }

    /**
     * Returns the leftmost value of this component
     * @return the leftmost value of this component
     */
    public int l_value() {
	return l_value;
    }

    /**
     * Returns the rightmost value of this component
     * @return the rightmost value of this component
     */
    public int r_value() {
	return r_value;
    }

    /**
     * Returns the left bound of this component
     * @return the left bound of this component
     */
    public int l_index() {
	return value.l_bound();
    }

    /**
     * Returns the right bound of this component
     * @return the right bound of this component
     */
    public int r_index() {
	return value.r_bound()+1;
    }

    /**
     * Given a set of breakpoints and a sequence, returns a string representation of this component.
     * @param breakpoints the set of breakpoints that make up the sequence
     * @param sequence the sequence this component operates on
     * @return a string representation of this component.
     */
    public String toString(ArrayList<Integer> breakpoints, int[] sequence) {
	String orient = oriented(breakpoints, sequence) ? "+" : "-";
	String res = String.format("%s(%d[%d], %d[%d]) :",
				   orient, l_index(), l_value(), r_index(), r_value());

	if(children.size() == 0) {
	    res = res + " { }";
	}
	else {
	    res = res + " {\n";
	    for(Component c : children) {
		String cstr = c.toString(breakpoints, sequence);
		cstr = RevUtil.padlines(cstr, 4);
		res = res + cstr + "\n";
	    }
	    res = res + " }";
	}
	return res;
    }

    /**
     * Returns a (partial) string representation of this component.
     * @return a (partial) string representation of this component.
     */
    public String toString() {
	String res = String.format("(%d[%d], %d[%d]) :", l_index(), l_value(), r_index(), r_value());

	if(children.size() == 0) {
	    res = res + " { }";
	}
	else {
	    res = res + " {\n";
	    for(Component c : children) {
		String cstr = c.toString();
		cstr = RevUtil.padlines(cstr, 4);
		res = res + cstr + "\n";
	    }
	    res = res + " }";
	}
	return res;
    }
}
