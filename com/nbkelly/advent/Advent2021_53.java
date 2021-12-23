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
import com.nbkelly.drafter.IntCommand; //visualize cmd
import com.nbkelly.lib.Image; //visualizer lib

import com.nbkelly.lib.IntPair;
import com.nbkelly.lib.pathfinder.Map;
import com.nbkelly.lib.Pair;

import java.math.BigInteger;
import java.util.Random;

/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2021_53 extends Drafter {
    /* WORKFLOW:
     *  set all needed commands with setCommands()
     *  post-processing can be performed with actOnCommands()
     *  the rest of your work should be based around the solveProblem() function
     */
    
    /* params injected from file */
    ArrayList<String> lines;
    
    //generate output
    boolean generate_output = false;

    int bound = 0;
    int lc = 0;
    int kdim = 3;
    Random rand = new Random();
    
    /* solve problem here */
    @Override public int solveProblem() throws Exception {
	Timer t = makeTimer();

        /* code injected from file */
        //var ints = Util.toIntList(lines);
        
        DEBUGF("BOUND: %d%n", bound);
	DEBUGF("DIMENSION: %d%n", kdim);
	DEBUGF("LENGTH: %d%n", lc);

	
	char base = (char)('z' - kdim + 1);
        for(int line = 0; line < lc; line++) {
	    var cline = String.format("%s ", onOff());
	    for(int dim = 0; dim < kdim; dim++) {
		if(dim != 0)
		    cline += ",";
		cline = String.format("%s%s", cline, pair(((char)(base+dim)+""), bound));
	    }
	    println(cline);		
	}
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    private String onOff() {
	if(rand.nextInt(2) == 0)
	    return "on";
	return "off";
    }
    
    private String pair(String id, int bound) {
	var left = rand.nextInt(bound+1)  * (rand.nextInt(2)*2 - 1);
	var right = rand.nextInt(bound+1) * (rand.nextInt(2)*2 - 1);
	
	return String.format("%s=%d..%d", id, Math.min(left, right), Math.max(left, right));
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

	IntCommand num_dimensions = new IntCommand("Nimber of Dimensions",
						   "The number of dimensions to generate output for",
						   1, 26, false, 3, "--k-dim", "--dimensions",
						   "--num-dimensions");

	IntCommand bound = new IntCommand("Bounding area",
					  "The bounds to generate within",
					  5, 400000000, false, 50000, "--bounds");

	IntCommand length = new IntCommand("length of output",
					   "The number of output lines to produce",
					   1, 100000000, false, 420, "--out-size");
					   
					  
        return new Command[]{fc, vc, num_dimensions, bound, length};
        
        
    }
    
    /* act after commands processed - userCommands stores all the commands set in setCommands */
    @Override public int actOnCommands(Command[] userCommands) throws Exception {
	//do whatever you want based on the commands you have given
	//at this stage, they should all be resolved
        /* code injected from file */
        lines = readFileLines(((FileCommand)userCommands[0]).getValue());
        setSource(((FileCommand)userCommands[0]).getValue());
        
        generate_output = ((BooleanCommand)userCommands[1]).matched();

	bound = ((IntCommand)userCommands[3]).getValue();
	kdim = ((IntCommand)userCommands[2]).getValue();
	lc = ((IntCommand)userCommands[4]).getValue();
	
	return 0;
    }

    /**
     * Creates and runs an instance of your class - do not modify
     */
    public static void main(String[] argv) {
        new Advent2021_53().run(argv);
    }
}
