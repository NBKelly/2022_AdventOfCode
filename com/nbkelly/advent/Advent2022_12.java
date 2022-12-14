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
import com.nbkelly.lib.pathfinder.Map;
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
public class Advent2022_12 extends Drafter {
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
        
        println(">Good Morning!");
        
        DEBUGF(1, "PART ONE: "); //todo
	IntPair start = null;
	IntPair target = null;

	HashMap<IntPair, Integer> elevations = new HashMap<>();
	for(int y = 0; y < lines.size(); y++) {
	    var line = lines.get(y);
	    for(int x = 0; x < line.length(); x++) {
		var ch = line.charAt(x);
		switch(ch) {
		case 'S':
		    start = new IntPair(x, y);
		    elevations.put(start, 0);
		    break;
		case 'E':
		    target = new IntPair(x, y);
		    elevations.put(target, (int)('z' - 'a'));
		    break;
		default:
		    elevations.put(new IntPair(x, y), (int)(ch - 'a'));
		}
	    }
	}

	TreeSet<Priority> priorities = new TreeSet<Priority>();
	priorities.add(new Priority(start, 0));

	int finalScore = resolveScore(priorities, elevations, target);
	println(finalScore);

	DEBUGF(1, "PART TWO: "); //todo
	priorities.clear();

	for(var v : elevations.entrySet()) {
	    if(v.getValue() == 0)
		priorities.add(new Priority(v.getKey(), 0));
	}
	finalScore = resolveScore(priorities, elevations, target);
	println(finalScore);
        
        /* visualize output here */
        generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    public int resolveScore(TreeSet<Priority> priorities, HashMap<IntPair, Integer> elevations,
			    IntPair target) {
	HashMap<IntPair, Integer> best = new HashMap<>();
	int finalScore = -1;
	while(priorities.size() > 0) {
	    var ct = priorities.pollFirst();

	    if(best.containsKey(ct.location) && ct.score >= best.get(ct.location))
		continue;
	    best.put(ct.location, ct.score);

	    if(ct.location.equals(target)) {
		finalScore = ct.score;
		break;
	    }

	    for(var neighbor : Map.OrthogonalNeighbors(ct.location)) {
		if(validMove(ct.location, neighbor, elevations))
		    priorities.add(new Priority(neighbor, ct.score+1));
	    }
	}

	return finalScore;
    }

    private class Priority implements Comparable<Priority>{
	int score;
	IntPair location;
	public Priority(IntPair location, int score) {
	    this.score = score;
	    this.location = location;
	}

	public int compareTo(Priority t) {
	    return Util.compareTo(score, t.score, location, t.location);
	}
    }

    public boolean validMove(IntPair start, IntPair end, HashMap<IntPair, Integer> elevations) {
	if (elevations.containsKey(start) && elevations.containsKey(end)) {
	    return (elevations.get(start) + 1 >= elevations.get(end));
	}
	return false;
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
        new Advent2022_12().run(argv);
    }
}
