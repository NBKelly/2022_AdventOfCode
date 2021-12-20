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

/* my imported libs */
import com.nbkelly.lib.Util;
import com.nbkelly.drafter.BooleanCommand; //visualize cmd
import com.nbkelly.lib.Image; //visualizer lib
import com.nbkelly.lib.HashCounter;

import com.nbkelly.lib.IntPair;
import com.nbkelly.lib.pathfinder.Map;
import com.nbkelly.lib.Pair;

import java.math.BigInteger;

import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import java.util.stream.IntStream;

/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2021_19 extends Drafter {
    /* WORKFLOW:
     *  set all needed commands with setCommands()
     *  post-processing can be performed with actOnCommands()
     *  the rest of your work should be based around the solveProblem() function
     */

	//sample: scanner 0 sees scanner 1 */	
	solved.add(unsolved.pollFirst());

	DEBUG(1, t.split("Input parsed"));
	
	int iteration = 1;

	while(unsolved.size() > 0) {
	    final var remove = new ConcurrentLinkedQueue<Scanner>();
	    final var add = new ConcurrentLinkedQueue<Scanner>();

	    unsolved.parallelStream().forEach(unsolved_cloud -> {
		    var triangles_unsolved = unsolved_cloud.triangles();
		    outer: for(var left : solved) {
			//compare the triangles for left and right
			var triangles_left = left.triangles();
			triangles_left.retainAll(triangles_unsolved);

			if(triangles_left.size() < 200)
			    continue;

			/* this is guaranteed to be a valid matching in at least one orientation */
			/* note that 12 choose 3 is 220 */

			var aligned = IntStream.range(0, 24)
			    .parallel()
			    .mapToObj(basis -> matchingBasis(basis, unsolved_cloud, left))
			    .filter(x -> x != null)
			    .findAny()
			    .orElse(null);
			
			if(aligned != null) {
			    add.add((Scanner)aligned);
			    remove.add(unsolved_cloud);
			    break outer;
			}
		    }
		});

	    unsolved.removeAll(remove);
	    solved.addAll(add);
	    
	    DEBUGF(2, "iteration %d solved %d unsolved %d%n", iteration++,
		   solved.size(), unsolved.size());
	}

	DEBUG(1, t.split("Space mapped"));
	
	/* assemble the solution */
	HashSet<Point> beacons = new HashSet<Point>();
	HashSet<Point> origins = new HashSet<Point>();

	
	for(var scanner : solved) {
	    beacons.addAll(scanner.points);
	    origins.addAll(scanner.origins);
	}
	
	int max_dist = 0;
	for(var left :origins)
	    for(var right : origins)
		max_dist = Math.max(max_dist, left.manhattan(right));
	
	DEBUGF(1, "PART ONE: "); println(beacons.size());
	DEBUGF(1, "PART TWO: "); println(max_dist);
        
        /* visualize output here */
        generate_output();
					 
	return DEBUG(1, t.total());
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
        new Advent2021_19().run(argv);
    }
}
