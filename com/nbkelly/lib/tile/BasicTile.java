package com.nbkelly.lib.tile;
import java.util.ArrayList;

public class BasicTile extends Tile {
    private Character[/*Y*/][/*X*/] matrix;
		
    public BasicTile(int size, ArrayList<String> str, int id) {
	this.ID = id;
	this.size = size;
	    
	matrix = new Character[size][size];

	for(int y = 0; y < str.size(); y++) {
	    var line = str.get(y);
	    for(int x = 0; x < line.length(); x++) {
		matrix[y][x] = line.charAt(x);
	    }
	}
    }

    public Character view(int x, int y) {
	return matrix[y][x];
    }

    public void set(int x, int y, char c) {
	matrix[y][x] = c;
    }
}    
