package com.nbkelly.lib.tile;
import java.util.ArrayList;

public class Image {
    private class Token {
	public Token(int x, int y, char token) {
	    this.x = x; this.y = y; this.token = token;
	}
	int x;
	int y;
	char token;
    }
    private ArrayList<Token> checks = new ArrayList<>();

    /**
     * Defines an image based on a set of strings (comprising the lines of the image),
     * and a set of recognized tokens (all other tokens are treated as whitespace).
     * @param strs set of strings comprising the image lines
     * @param tokens the set of recognized tokens in the image
     */
    public Image(ArrayList<String> strs, ArrayList<Character> tokens) {
	for(int y = 0; y < strs.size(); y++) {
	    String s = strs.get(y);

	    for(int x = 0; x < s.length(); x++)
		if(tokens.contains(s.charAt(x)))
		    checks.add(new Token(x, y, s.charAt(x)));
	}
    }

    /**
     * Checks if this image exists at a point in a tile
     *
     * @param t tile to check
     * @param X x value in the tile to check
     * @param Y y value in the tile to check
     * @return true if the image exists at (X, Y), otherwise false
     */
    public boolean check(Tile t, int X, int Y) {
	for(Token token : checks) {
	    int offset_x = X + token.x;
	    int offset_y = Y + token.y;
		
	    if(offset_x < 0 || offset_x >= t.size)
		return false;
	    if(offset_y < 0 || offset_y >= t.size)
		return false;

	    if(token.token != t.view(offset_x, offset_y))
		return false;
	}

	return true;
    }

    /**
     * Imprints this image onto the given tile using a single character
     *
     * @param t tile to imprint
     * @param X x value in tile to imprint
     * @param Y y value in tile to imprint
     * @param icon character to imprint
     */
    public void imprint(Tile t, int X, int Y, char icon) {
	for(Token token : checks) {
	    int offset_x = X + token.x;
	    int offset_y = Y + token.y;
		
	    if(offset_x < 0 || offset_x >= t.size)
		continue;
	    if(offset_y < 0 || offset_y >= t.size)
		continue;

	    t.set(offset_x, offset_y, icon);		    
	}
    }
}
