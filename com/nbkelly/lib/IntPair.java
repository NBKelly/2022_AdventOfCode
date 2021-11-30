package com.nbkelly.lib;

/**
 * An integer pair
 * @author      NB Kelly <nbkelly @ protonmail.com>
 * @version     1.3
 * @since       1.1
 */
public class IntPair implements Comparable<IntPair> {
    public final int X;
    public final int Y;

    /**
     * Creates a new IntPair with values x, y
     */
    public IntPair(Integer x, Integer y) {
	this.X = x;
	this.Y = y;
    }
    
    @Override public int hashCode() {
	return 17 * X + 37 * Y + (X << 16) + Y;
    }
    
    @Override public int compareTo(IntPair p) {
	return Util.compareTo(X, p.X, Y, p.Y);
    }

    @Override public String toString() {
	return String.format("(x,y) = (%d, %d)", X, Y);
    }

    @Override public boolean equals(Object p) {
	if(p.getClass() != getClass())
	    return false;
	return compareTo((IntPair)p) == 0;
    }
}
