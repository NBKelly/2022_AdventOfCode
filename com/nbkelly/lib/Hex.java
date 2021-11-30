package com.nbkelly.lib;

import com.nbkelly.lib.Pair;

/**
 * A class representing hexagonal directions in a 2d grid.
 * <p>
 * Use either (north/south OR east/west), and not both.
 * 
 * @author      NB Kelly <nbkelly @ protonmail.com>
 * @version     1.2
 * @since       1.2
 */
public final class Hex {
    /**
     * Northern cardinal direction. Do not mix with East/West;
     */
    public final static Pair<Integer, Integer> NORTH = new Pair<Integer, Integer>(0, 2);

    /**
     * Southern cardinal direction. Do not mix with East/West.
     */
    public final static Pair<Integer, Integer> SOUTH = new Pair<Integer, Integer>(0, -2);

    /**
     * North-eastern cardinal direction
     */
    public final static Pair<Integer, Integer> NORTH_EAST = new Pair<Integer, Integer>(1, 1);
    /**
     * North-western cardinal direction
     */
    public final static Pair<Integer, Integer> NORTH_WEST = new Pair<Integer, Integer>(-1, 1);

    /**
     * South-eastern cardinal direction
     */
    public final static Pair<Integer, Integer> SOUTH_EAST = new Pair<Integer, Integer>(1, -1);
    /**
     * South-western cardinal direction
     */
    public final static Pair<Integer, Integer> SOUTH_WEST = new Pair<Integer, Integer>(-1, -1);

    /**
     * Eastern cardinal direction. Do not mix with North/South.
     */
    public final static Pair<Integer, Integer> EAST = new Pair<Integer, Integer>(2, 0);

    /**
     * Western cardinal direction. Do not mix with North/South.
     */
    public final static Pair<Integer, Integer> WEST = new Pair<Integer, Integer>(-2, 0);
}
