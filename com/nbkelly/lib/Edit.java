package com.nbkelly.lib;

import com.nbkelly.lib.edit.Matrix;

/**
 * @author      NB Kelly <nbkelly @ protonmail.com>
 * @version     21/4/11
 * @since       21/4/11
 */
public class Edit {
    /**
     * Determine the minimum edit distance between two strings.
     * <p>
     * The minimum edit distance is the minimum number of edit operations 
     * needed to transform left into right.
     * <p>
     * The maximum space taken is 2 * LEN(MIN(left, right))
     * @param left first string to compare
     * @param right second string to compare
     * @return MED(left, right)
     * @since 21/4/11
     */
    public static int minimumEditDistance(String left, String right) {
	if(right.length() > left.length())
	    return minimumEditDistance(right, left);

	//trivial case
	if(right.length() == 0)
	    return left.length();
	
	int[] last = new int[right.length()+1];

	for(int r = 1; r <= right.length(); r++)
	    last[r] = r;

	for(int l = 1; l <= left.length(); l++) {
	    int[] next = new int[right.length() + 1];
	    next[0] = l;

	    for(int r = 1; r <= right.length(); r++) {
		int subs = last[r-1];
		int subs_bad = last[r-1] + 1;
		int add = last[r] + 1;
		int del = next[r-1] + 1;
		
		if(right.charAt(r-1) == left.charAt(l-1))
		    next[r] = min(subs, add, del);
		else
		    next[r] = min(add, del, subs_bad);
	    }

	    //swap arrays
	    last = next;
	}

	return last[right.length()];
    }

    private static int min(int... args) {
	if(args.length == 0)
	    return 0;

	int min = args[0];
	for(int i = 1; i < args.length; i++)
	    min = Math.min(min, args[i]);
	return min;
    }

    private static int max(int... args) {
	if(args.length == 0)
	    return 0;

	int max = args[0];
	for(int i = 1; i < args.length; i++)
	    max = Math.max(max, args[i]);
	return max;
    }


    public static int alignmentScore_linear(String left, String right,
				     Matrix matrix, int linear_penalty) {
	int[] last = new int[right.length() + 1];
	
	//align first row, as all gaps
	for(int x = 1; x < right.length() + 1; x++) {
	    if(x == 1) {
		var score = linear_penalty;
		last[x] = score;
	    }
	    else {
		var score = last[x-1] + linear_penalty;
		last[x] = score;
	    }
	}

	//for(int i = 0; i <= right.length(); i++)
	//    System.out.printf("%d ", last[i]);
	//System.out.println();

	for(int y = 1; y <= left.length(); y++) {
	    int[] next = new int[right.length() + 1];
	    if(y == 1) {
		var score = linear_penalty;
		next[0] = score;
	    }
	    else {
		var score = last[0] + linear_penalty;
		next[0] = score;
	    }

	    char cy = left.charAt(y-1);
	    for(int x = 1; x <= right.length(); x++) {
		char cx = right.charAt(x-1);

		int match_score = matrix.getScore(cx, cy);
		int val = max(next[x-1], last[x]) + linear_penalty;
		val = max(val, last[x-1] + match_score);

		next[x] = val;
	    }

	    last = next;
	}
	
	return last[right.length()];
    }

    public static Pair<String, String> alignment(String left, String right, boolean favor_diag) {
	//TRIVIAL CASES
	if(left.length() == 0)
	    return new Pair<String, String>(smult("-", right.length()), right);
	if(right.length() == 0)
	    return new Pair<String, String>(left, smult("-", left.length()));

	int[][] scores = new int[left.length()+1][right.length()+1];

	//assign first r-row
	for(int r = 1; r <= right.length(); r++)
	    scores[0][r] = r;
	
	//perform calculation
	for(int l = 1; l <= left.length(); l++) {
	    char lChar = left.charAt(l-1);
	    
	    scores[l][0] = l;
	    for(int r = 1; r <= right.length(); r++) {
		int rChar = right.charAt(r-1);

		//so it is above
		int val = scores[l-1][r]+1;
		//as it is to the left
		val = Math.min(val, scores[l][r-1]+1);
		//and maybe it is diagonally upwards
		val = Math.min(val, scores[l-1][r-1]+1);		
		if(lChar == rChar)
		    val = Math.min(val, scores[l-1][r-1]);

		scores[l][r] = val;
	    }
	}

	int med = scores[left.length()][right.length()];
		
	StringBuilder l_out = new StringBuilder();
	StringBuilder r_out = new StringBuilder();

	for(int l = left.length(), r = right.length(); l > 0 || r > 0 ;) {
	    int c_score = scores[l][r];

	    if(l == 0) {
		prepend(r_out, right.charAt(r-1));
		prepend(l_out, '-');
		r--;
		continue;
	    }
	    else if (r == 0) {
		prepend(r_out, '-');
		prepend(l_out, left.charAt(l-1));
		l--;
		continue;
	    }
	    
	    //MAYBE DIAG DEC
	    if (favor_diag && scores[l-1][r-1] < c_score) {
		prepend(l_out, left.charAt(l-1));
		prepend(r_out, right.charAt(r-1));
		l--;
		r--;
	    }//LEFT_DEC
	    else if (scores[l][r-1] < c_score) {
		prepend(l_out, '-');
		prepend(r_out, right.charAt(r-1));
		r--;	       
	    }
	    //VERT_DEC
	    else if (scores[l-1][r] < c_score) {
		prepend(l_out, left.charAt(l-1));
		prepend(r_out, '-');
		l--;
	    }
	    //DIAG_DEC
	    else if(scores[l-1][r-1] < c_score) {
		prepend(l_out, left.charAt(l-1));
		prepend(r_out, right.charAt(r-1));
		l--;
		r--;
		continue;
	    }//DIAG_EQ
	    else if(scores[l-1][r-1] == c_score) {
		prepend(l_out, left.charAt(l-1));
		prepend(r_out, right.charAt(r-1));
		l--;
		r--;
	    }
	}

	return new Pair<String, String>(l_out.toString(), r_out.toString());
    }

    /**
     * Computes a count of all optimal alignments between two strings.
     * <p>
     * Computes a count of all optimal alignments between two strings where the hamming distance
     * is equal to the minimum edit distance. The answer will be given modulo input.
     * 
     * @param left first string to compare
     * @param right second string to compare
     * @param mod integer to give answer modulo
     *
     * @return count of all optimal alignments between the two strings modulo <i>mod</i>
     */
    public static int alignmentCount(String left, String right, int mod) {
	int[][] oldScores = new int[left.length()+1][right.length()+1];
	
	//assign first r-row
	for(int r = 1; r <= right.length(); r++)
	    oldScores[0][r] = r;
	
	//perform calculation
	for(int l = 1; l <= left.length(); l++) {
	    char lChar = left.charAt(l-1);
	    
	    oldScores[l][0] = l;
	    for(int r = 1; r <= right.length(); r++) {
		int rChar = right.charAt(r-1);
		
		//so it is above
		int val = oldScores[l-1][r]+1;
		//as it is to the left
		val = Math.min(val, oldScores[l][r-1]+1);
		//and maybe it is diagonally upwards
		val = Math.min(val, oldScores[l-1][r-1]+1);		
		if(lChar == rChar)
		    val = Math.min(val, oldScores[l-1][r-1]);
		
		oldScores[l][r] = val;
	    }
	}
	
	int[][] newScores = new int[left.length()+1][right.length()+1];
	
	if(left.length() == 0 || right.length() == 0)
	    return 1;
	
	//all 0 len things are score 1
	for(int r = 0; r <= right.length(); r++)
	    newScores[0][r] = 1;
	
	for(int l = 0; l <= left.length(); l++)
	    newScores[l][0] = 1;
	
	for(int l = 1; l <= left.length(); l++)
	    for(int r = 1; r <= right.length(); r++) {
		int ctScore = 0;
		
		int oldScore = oldScores[l][r];
		if(oldScore == oldScores[l-1][r] +1)
		    ctScore += newScores[l-1][r];
		if(oldScore == oldScores[l][r-1] +1)
		    ctScore += newScores[l][r-1];
		
		if(oldScore == oldScores[l-1][r-1] && left.charAt(l-1) == right.charAt(r-1))
		    ctScore += newScores[l-1][r-1];
		else if (oldScore == oldScores[l-1][r-1] + 1
			 && left.charAt(l-1) != right.charAt(r-1))
		    ctScore += newScores[l-1][r-1];
		
		newScores[l][r] = ctScore  % mod;
	    }
	
	return newScores[left.length()][right.length()];
    }
    
    private static String smult(String s, int period) {
	if(period <= 0)
	    return "";
	
	return new String(new char[period]).replace("\0", s);
    }
    
    private static void prepend(StringBuilder s, char c) {
	s.insert(0, c);
    }
    
    //TODO: this
    private void centralize(String left, String right, String out_left, String out_right) {
	//workflow:
	//1: identify mismatched segments
	//2: shift mismatches so they are furthest away from central axis	
    }
}
