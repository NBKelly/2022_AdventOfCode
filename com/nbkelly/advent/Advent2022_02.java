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


/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2022_02 extends Drafter {
    /* WORKFLOW:
     *  set all needed commands with setCommands()
     *  post-processing can be performed with actOnCommands()
     *  the rest of your work should be based around the solveProblem() function
     */
    
    /* params injected from file */
    ArrayList<String> lines;
    
    //generate output
    boolean generate_output = false;
    
    /* solve problem here */
    @Override public int solveProblem() throws Exception {
	Timer t = makeTimer();

        /* code injected from file */
        //var ints = Util.toIntList(lines);

	var running_score_a = 0;
	var running_score_b = 0;
        for(var line : lines) {
	    var opp = line.charAt(0);
	    var me = line.charAt(2);
	    running_score_a += score_round_a(opp, me);
	    running_score_b += score_round_b(opp, me);
	}

        DEBUGF(1, "PART ONE: "); //todo
	println(running_score_a);
        DEBUGF(1, "PART TWO: "); //todo
        println(running_score_b);

        /* visualize output here */
        generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    public int score_round_a(char opp, char me) {
	int score = me - 'W';
	return score + outcome(toMove(opp), toMove(me));
    }

    public int score_round_b(char opp, char me) {
	var score = toOutcome(toMove(opp), me) + (3*(me-'X'));
	return score;
    }

    //return the points of the move
    public int toOutcome(String opp, char me) {
	int target = (3*(me-'X'));

	if(outcome(opp, "ROCK") == target)
	    return to_point("ROCK");
	else if(outcome(opp, "PAPER") == target)
	    return to_point("PAPER");
	return to_point("SCISSORS");
    }

    public int to_point(String move) {
	switch(move) {
	case "ROCK":
	    return 1;
	case "PAPER":
	    return 2;
	case "SCISSORS":
	default:
	    return 3;
	}
    }

    public int outcome(String opp, String me) {
	if(opp.equals(me))
	    return 3;

	switch(me) {
	case "ROCK":
	    if (opp.equals("PAPER"))
		return 0;
	    else
		return 6;
	case "PAPER":
	    if (opp.equals("SCISSORS"))
		return 0;
	    else
		return 6;
	case "SCISSORS":
	default:
	    if (opp.equals("ROCK"))
		return 0;
	    else
		return 6;
	}
    }

    public String toMove(char c) {
	switch(c) {
	case 'A':
	case 'X':
	    return "ROCK";
	case 'B':
	case 'Y':
	    return "PAPER";
	case 'C':
	case 'Z':
	    return "SCISSORS";
	default:
	    return "HUH";
	}
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
        new Advent2022_02().run(argv);
    }
}
