package com.nbkelly.tile;

public class FlipVertical extends Tile {
	private Tile basis;

	public FlipVertical(Tile t) {
	    this.ID = t.ID;
	    this.size = t.size;
	    this.basis = t;
	}

	public Character view(int x, int y) {
	    return basis.view(x, size - 1 - y);
	}

	public void set(int x, int y, char c) {
	    basis.set(x, size - 1 - y, c);
	}
    }
