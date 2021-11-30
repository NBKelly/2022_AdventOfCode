package com.nbkelly.tile;

public class CounterClockwise extends Tile {
    private Tile basis;
    public CounterClockwise(Tile t) {
	this.ID = t.ID;
	this.size = t.size;
	this.basis = t;
    }
    
    public Character view(int x, int y) {
	//y = x
	//x -> size - 1 - y	    
	return basis.view(size - 1 - y, x);
    }
    
    public void set(int x, int y, char c) {
	basis.set(size - 1 - y, x, c);
    }
}
