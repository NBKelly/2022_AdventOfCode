package com.nbkelly.lib;

/**
 * A generic pair type.
 * @author      NB Kelly <nbkelly @ protonmail.com>
 * @version     1.3
 * @since       1.1
 */
public class Pair<T, U> {    
    public final T X;
    public final U Y;

    @Override public int hashCode() {
	return 17*X.hashCode() + 37*Y.hashCode();
    }

    @Override public boolean equals(Object e) {
	//if(e.getClass() == Pair<T, U>.class) {
	if(e instanceof Pair<?, ?>)
	    try {
		Pair p = (Pair)e;
		return X.equals(p.X) && Y.equals(p.Y);
	    } catch(Exception f) {
		return false;
	    }
	return false;
    }

    public String toString() {
	return String.format("%s %s", X.toString(), Y.toString());
    }

    /**
     * Creates a pair based on two values
     */
    public Pair(T X, U Y) {
	this.X = X;
	this.Y = Y;
    }
}
