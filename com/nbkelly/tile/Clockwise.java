package com.nbkelly.tile;

public class Clockwise extends Tile {
    private Tile basis;
    public Clockwise(Tile t) {
	this.ID = t.ID;
	this.size = t.size;
	this.basis = t;
    }

    public Character view(int x, int y) {
	//y -> size - 1 - x
	//x = y
	return basis.view(y, size - 1 - x);
    }

    public void set(int x, int y, char c) {
	basis.set(y, size - 1 - x, c);
    }
}
