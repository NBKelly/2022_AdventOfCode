package com.nbkelly.lib.tile;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.TreeSet;

public class Assembler {
    private Assembler() {}

    public static HashMap<Integer, Tile> tileMap(ArrayList<Tile> tiles) {
	HashMap<Integer, Tile> tileMap = new HashMap<>();
	
	for(Tile tile : tiles)
	    tileMap.put(tile.ID, tile);

	return tileMap;
    }

    /* map of edge -> [tileIDS] */
    public static HashMap<String, TreeSet<Integer>> associateEdges(ArrayList<Tile> tiles) {
	HashMap<String, TreeSet<Integer>> edgeIDS = new HashMap<>();
	for(Tile tile : tiles) {
	    var edges = tile.edges();
	    for(var edge : edges) {
		if(!edgeIDS.containsKey(edge))
		    edgeIDS.put(edge, new TreeSet<Integer>());
		edgeIDS.get(edge).add(tile.ID);
	    }	
	}

	return edgeIDS;
    }

    public static Tile assembledMap(Tile[][] grid, int grid_size) {
	ArrayList<String> map = new ArrayList<String>();
	//construct the map
	for(int y = 0; y < grid_size; y++) {
	    var row = join(grid[y][0].inset(1), grid[y][1].inset(1));
	    
	    for(int x = 2; x < grid_size; x++) {
		row = join(row, grid[y][x].inset(1));
	    }

	    map.addAll(row);
	}

	Tile assembled = new BasicTile(map.size(), map, 1488);
	return assembled;
    }

    private static ArrayList<String> join(ArrayList<String> left, ArrayList<String> right) {
	ArrayList<String> res = new ArrayList<String>();

	for(int i = 0; i < left.size(); i++)
	    res.add(left.get(i) + right.get(i));

	return res;
    }
    
    public static HashMap<Integer, TreeSet<Integer>> neighbors(ArrayList<Tile> tiles) {
	HashMap<Integer, TreeSet<Integer>> neighbors = new HashMap<>();

	var edgeIDS = associateEdges(tiles);
	
	for(Tile tile : tiles) {
	    var n_set = new TreeSet<Integer>();
	    neighbors.put(tile.ID, n_set);
	    
	    var edges = tile.edges();
	    for(var edge : edges) {
		//get all the tiles with this edgeID
		var _neighbors = edgeIDS.get(edge);
		for(var neighbor_id : _neighbors)
		    if(neighbor_id != tile.ID)
			n_set.add(neighbor_id);		
	    }
	}

	return neighbors;
    }

    public static TreeSet<Integer> corners(ArrayList<Tile> tiles) {
	var neighbors = neighbors(tiles);

	TreeSet<Integer> res = new TreeSet<Integer>();
	for(var pair : neighbors.entrySet()) {
	    int neighbors_count = pair.getValue().size();

	    if(neighbors_count == 2)
		res.add(pair.getKey());
	}

	return res;
    }

    public static TreeSet<Integer> edges(ArrayList<Tile> tiles) {
	var neighbors = neighbors(tiles);

	TreeSet<Integer> res = new TreeSet<Integer>();
	for(var pair : neighbors.entrySet()) {
	    int neighbors_count = pair.getValue().size();

	    if(neighbors_count == 3)
		res.add(pair.getKey());
	}

	return res;
    }

    public static TreeSet<Integer> standards(ArrayList<Tile> tiles) {
	var neighbors = neighbors(tiles);

	TreeSet<Integer> res = new TreeSet<Integer>();
	for(var pair : neighbors.entrySet()) {
	    int neighbors_count = pair.getValue().size();

	    if(neighbors_count == 4)
		res.add(pair.getKey());
	}

	return res;
    }

    public static Tile[][] constructGrid(int grid_size, TreeSet<Integer> corners,
				  TreeSet<Integer> edges, TreeSet<Integer> remainder,
				  HashMap<Integer, Tile> tiles,
				  HashMap<String, TreeSet<Integer>> edgeIDS) {    
	TreeSet<Integer> available = new TreeSet<Integer>();
	available.addAll(corners);
	available.addAll(edges);
	available.addAll(remainder);
	
	
	Tile[/*y*/][/*x*/] grid = new Tile[grid_size][grid_size];
	//select the top left tile
	var topLeft = tiles.get(corners.first());
	
	//rotate the tile until both the top and the left edge have no neighbors
	while(edgeIDS.get(topLeft.top()).size() != 1 || edgeIDS.get(topLeft.left()).size() != 1)
	    topLeft = new Clockwise(topLeft);
	
	grid[0][0] = topLeft;
	available.remove(topLeft.ID);

	int y = 0;
	for(int x = 1; x < grid_size; x++) {
	    var tile = selectStd(grid[0][x-1].right(), null,
				 available, tiles, edgeIDS);
	    grid[y][x] = tile;
	    available.remove(tile.ID);
	}
	
	for(y = 1; y < grid_size; y++) {
	    {
		var tile = selectStd(null, grid[y-1][0].bottom(),
				     available, tiles, edgeIDS);
		grid[y][0] = tile;
		available.remove(tile.ID);
	    }
	    
	    for(int x = 1; x < grid_size; x++) {
		var tile = selectStd(grid[y][x-1].right(), grid[y-1][x].bottom(),
				     available, tiles, edgeIDS);
		grid[y][x] = tile;
		available.remove(tile.ID);
	    }
	}

	return grid;
    }
        
    private static Tile selectStd(String matchLeft, String matchTop, TreeSet<Integer> available,
			   HashMap<Integer, Tile> tiles, HashMap<String, TreeSet<Integer>> edgeIDS) {
	TreeSet<Integer> selection = new TreeSet<>(available);
	if(matchLeft != null)
	    selection.retainAll(edgeIDS.get(matchLeft));
	if(matchTop != null)
	    selection.retainAll(edgeIDS.get(matchTop));
	
	var tile = tiles.get(selection.first());

	//find an orientation which matches left and top
	tile = tile.orient(matchLeft, matchTop, null, null);
	
	//given a left edge to match, find a tile which matches the left edge and the top edge
	//if an entry is null, there should be nothing there (no element to match)

	return tile;
    }
}
