package com.nbkelly.lib.tile;
import java.util.ArrayList;
import java.util.TreeSet;

public abstract class Tile {
    public int ID;
    public int size;

    public TreeSet<String> edges() {
	TreeSet<String> edges = new TreeSet<String>();
	edges.add(top());
	edges.add(bottom());
	edges.add(left());
	edges.add(right());

	var adj = new FlipHorizontal(new FlipVertical(this));
	edges.add(adj.top());
	edges.add(adj.bottom());
	edges.add(adj.left());
	edges.add(adj.right());

	return edges;
    }
	
    public abstract Character view(int x, int y);

    public String top()    { return viewLine(0); }
    public String bottom() { return viewLine(size - 1); }
    public String left()   { return viewColumn(0); }
    public String right()  { return viewColumn(size - 1); }
	
    public String viewLine(int y) {
	StringBuilder line = new StringBuilder();
	for(int x = 0; x < size; x++)
	    line.append(view(x, y));
	return line.toString();
    }

    public String viewColumn(int x) {
	StringBuilder line = new StringBuilder();
	for(int y = 0; y < size; y++)
	    line.append(view(x, y));
	return line.toString();
    }
	
    public ArrayList<String> view() {
	ArrayList<String> res = new ArrayList<String>();
	for(int y = 0; y < size; y++)
	    res.add(viewLine(y));
	return res;
    }

    public ArrayList<String> inset(int dist) {
	ArrayList<String> res = new ArrayList<String>();
	for(int y = dist; y < size - dist; y++) {
	    var line = viewLine(y);
	    line = line.substring(dist, size - dist);
	    res.add(line);
	}
	return res;
    }

    public String toString() {
	var replacement =">Tile %d:" + "%n";
	String res = String.format(replacement, ID);

	for(String s: view())
	    res = res + s + "\n";

	return res;
    }

    public abstract void set(int x, int y, char c);

    public Tile orient(String left, String top, String right, String bot) {
	Tile t = this;
	
	for(int i = 0; i <= 4; i++) {
	    if((left == null || left.equals(t.left())) &&
	       (top == null || top.equals(t.top())) &&
	       (right == null || right.equals(t.right())) &&
	       (bot == null || bot.equals(t.bottom())))
		return t;
	    t = new Clockwise(t);
	}

	t = new FlipVertical(t);

	for(int i = 0; i <= 4; i++) {
	    if((left == null || left.equals(t.left())) &&
	       (top == null || top.equals(t.top())) &&
	       (right == null || right.equals(t.right())) &&
	       (bot == null || bot.equals(t.bottom())))
		return t;
	    t = new Clockwise(t);
	}

	return null;
    }
}
