package com.nbkelly.lib.bio;

/**
 * A genetic sequence.
 */
public abstract class Sequence {
    /**
     * Gets the reverse of this sequence strand
     * @return the reverse of this sequence strand
     */
    public abstract Sequence reverse();
}
