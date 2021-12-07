package com.nbkelly.advent;

/* imports */
import com.nbkelly.drafter.Drafter;
import com.nbkelly.drafter.Command;
import com.nbkelly.drafter.FileCommand;
import com.nbkelly.drafter.Timer;

/* imports from file */
import java.util.ArrayList;

/* my imported libs */
import com.nbkelly.lib.Util;
import com.nbkelly.drafter.BooleanCommand; //visualize cmd
import com.nbkelly.lib.Image; //visualizer lib

import java.util.Collections;

/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2021_07 extends Drafter {
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
	var strs = nextLine().split(",");
	ArrayList<Integer> ints = new ArrayList<>();
	for(int i = 0; i < strs.length; i++)
	    ints.add(Integer.parseInt(strs[i]));

	Collections.sort(ints);

	int medial = ints.get(ints.size()/2);
	long average_dist = distance(ints, medial);
	
	int average = ints.stream().reduce(0, (a,b)-> a+b) / ints.size();
	long average_dist_2 = distance_2(ints, average);
	        
        DEBUGF(1, "PART ONE: "); println(average_dist);//todo
        DEBUGF(1, "PART TWO: "); println(average_dist_2);//todo
        
        /* visualize output here */
        generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    long triangle(int x) {
	/*
	 * 1,  2,     3,    ... , (n-2), (n-1), n
	 * n, (n-1), (n-2)  ...    3,     2,    1
	 * ====
	 * n, n, n, ..., n, n, n (n+1 times)
	 * ===
	 * n(n+1) / 2
	 */ 
	return (x * (x+1))/2;
    }
    
    long distance(ArrayList<Integer> vals, int x) {
	var dist = 0;
	for(var val : vals)
	    dist += Math.abs(x - val);	

	return dist;
    }

    
    
    long distance_2(ArrayList<Integer> vals, int x) {
	long dist = 0;
	for(var val : vals) {
	    int diff = Math.abs(x - val);
	    dist += triangle(diff);
	}

	return dist;
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
        new Advent2021_07().run(argv);
    }
}
