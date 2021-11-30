package com.nbkelly.lib.edit;

import java.util.HashMap;
import com.nbkelly.lib.IntPair;

public class Matrix {
    protected HashMap<IntPair, Integer> scores = new HashMap<IntPair, Integer>();
    
    protected void setScore(char from, char to, int score, boolean symmetric) {
	scores.put(new IntPair((int)from, (int)to), score);
	if(symmetric)
	    scores.put(new IntPair((int)to, (int)from), score);
    }

    public Integer getScore(char from, char to) {
	return scores.get(new IntPair((int)from, (int)to));
    }
}
