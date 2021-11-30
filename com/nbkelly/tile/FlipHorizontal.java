package com.nbkelly.tile;

public class FlipHorizontal extends Tile {
    private Tile basis;
    public FlipHorizontal(Tile t) {
	this.ID = t.ID;
	this.size = t.size;
	this.basis = t;
    }

    public Character view(int x, int y) {
	return basis.view(size - 1 - x, y);
    }

    public void set(int x, int y, char c) {
	basis.set(size - 1 - x, y, c);
    }
}
