package com.nbkelly.advent;

/* imports */
import com.nbkelly.drafter.Drafter;
import com.nbkelly.drafter.Command;
import com.nbkelly.drafter.FileCommand;
import com.nbkelly.drafter.Timer;

/* imports from file */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.TreeMap;

/* my imported libs */
import com.nbkelly.lib.Util;
import com.nbkelly.drafter.BooleanCommand; //visualize cmd
import com.nbkelly.lib.Image; //visualizer lib

import com.nbkelly.lib.IntPair;
import com.nbkelly.lib.pathfinder.Map;
import com.nbkelly.lib.Pair;

import java.math.BigInteger;
import com.nbkelly.lib.Memo;

/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2021_21 extends Drafter {
    /* WORKFLOW:
     *  set all needed commands with setCommands()
     *  post-processing can be performed with actOnCommands()
     *  the rest of your work should be based around the solveProblem() function
     */
    
    /* params injected from file */
    ArrayList<String> lines;
    
    //generate output
    boolean generate_output = false;

    private class Key {
	int[] scores = new int[2];
	int[] locs = new int[2];
	int turn = 0;
	public Key(int[] scores, int[] locs, int turn){
	    this.scores = scores;
	    this.locs = locs;
	    this.turn = turn;
	}

	@Override public int hashCode() {
	    return scores[0] * 21 + scores[1]*21*21
		+ locs[0]*10 + locs[1]*100
		+ turn;
	}

	public boolean equals(Object o) {
	    if(o instanceof Key) {
		var k = (Key)o;
		return k.turn == turn &&
		    k.scores[0] == scores[0] &&
		    k.scores[1] == scores[1] &&
		    k.locs[0] == locs[0] &&
		    k.locs[1] == locs[1];
	    }

	    return false;
	}
    }
    
    public int deterministic_score(int p1_start, int p2_start,
				   int board_size, int max_score,
				   int dice_size) {
	int[] scores = new int[2];
	int[] locs = new int[]{p1_start-1, p2_start-1};

	int turn = 0;

	int dice = 0;

	while(scores[0] < max_score && scores[1] < max_score) {
	    /* roll three dice */
	    var roll = (dice++%100)+1;
	    roll    += (dice++%100)+1;
	    roll    += (dice++%100)+1;

	    locs[turn%2] = (locs[turn%2]+roll)%board_size;
	    scores[turn%2] = scores[turn%2] + locs[turn%2] + 1;

	    turn++;
	}

	return 3*turn * Math.min(scores[0], scores[1]);
    }

    
    
    public Pair<BigInteger, BigInteger>
	dirac_score(int[] locs, int[] scores, int turn, int board_size, int max_score, int dice_size,
		    HashMap<Key, Pair<BigInteger, BigInteger>> map) {
	Key k = new Key(scores, locs, turn);

	if(map.containsKey(k))
	    return map.get(k);

	if(scores[0] >= max_score)
	    return new Pair<>(BigInteger.ONE, BigInteger.ZERO);
	if (scores[1] >= max_score)
	    return new Pair<>(BigInteger.ZERO, BigInteger.ONE);

	BigInteger left = BigInteger.ZERO;
	BigInteger right = BigInteger.ZERO;
	
	for(int d1 = 0; d1 < dice_size; d1++)
	    for(int d2 = 0; d2 < dice_size; d2++)
		for(int d3 = 0; d3 < dice_size; d3++) {
		    var roll = 3*dice_size -(d1 + d2 + d3);
		    
		    
		    int[] new_locs = new int[]{locs[0], locs[1]};
		    new_locs[turn%2] = (locs[turn%2] + roll)%board_size;
		    int[] new_scores = new int[]{scores[0], scores[1]};
		    new_scores[turn%2] = (new_locs[turn%2]+1) + scores[turn%2];
		    
		    var score = dirac_score(new_locs, new_scores,
					    (turn+1)%2, board_size,
					    max_score, dice_size, map);

		    left = left.add(score.X);
		    right = right.add(score.Y);
		}

	map.put(k, new Pair<>(left, right));
	return new Pair<>(left, right);
    }
    
    /* solve problem here */
    @Override public int solveProblem() throws Exception {
	Timer t = makeTimer();

        /* code injected from file */
        //var ints = Util.toIntList(lines);

	var player_1 = Integer.parseInt(lines.get(0).substring(28));
	var player_2 = Integer.parseInt(lines.get(1).substring(28));

	var p1_ans =deterministic_score(player_1, player_2, 10, 1000, 100);

	HashMap<Key, Pair<BigInteger, BigInteger>> map = new HashMap<>();
	var p2_ans = dirac_score(new int[]{player_1-1, player_2-1},
				 new int[2], 0, 10, 21, 3, map);		
	        
        DEBUGF(1, "PART ONE: "); println(p1_ans);
        DEBUGF(1, "PART TWO: "); println(p2_ans.X.max(p2_ans.Y)); 
	
        /* visualize output here */
        generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }
    

    /* code injected from file */
    public void generate_output() throws Exception {
    	if(!generate_output)
    	    return;
    	
    	println(">generating output");
    
    	/* output goes here */
    }

    /* set commands */
    @Override public Command[] setCommands() {
	//do you want paged input to be optional? This is mainly a debugging thing,
	//or a memory management/speed thing
	_PAGE_OPTIONAL = false; //page does not show up as a user input command
	_PAGE_ENABLED = false;  //page is set to disabled by default
	
        /* code injected from file */
        FileCommand fc = new FileCommand("Input File", "The input file for this program",
        	       	     		 true, "--input-file", "--file");
        
        /* visualizer */
        BooleanCommand vc = new BooleanCommand("Visualize Output",
        				       "The visualized output for this program", 
        				       false, "--out-file", "--output-file", "--out-image");
        
        return new Command[]{fc, vc};
        
        
    }
    
    /* act after commands processed - userCommands stores all the commands set in setCommands */
    @Override public int actOnCommands(Command[] userCommands) throws Exception {
	//do whatever you want based on the commands you have given
	//at this stage, they should all be resolved
        /* code injected from file */
        lines = readFileLines(((FileCommand)userCommands[0]).getValue());
        setSource(((FileCommand)userCommands[0]).getValue());
        
        generate_output = ((BooleanCommand)userCommands[1]).matched();
	return 0;
    }

    /**
     * Creates and runs an instance of your class - do not modify
     */
    public static void main(String[] argv) {
        new Advent2021_21().run(argv);
    }
}
