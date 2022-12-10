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
public class Advent2022_10 extends Drafter {
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
        DEBUGF(1, "PART TWO: "); //todo

	long cycle = 1;
	long X = 1;

	long sum = 0;

	sum += checkIfInteresting(cycle, X);

	for(var line : lines) {
	    var cmd = line.split(" ");
	    var old_cycle = cycle;
	    sw: switch(cmd[0]) {
	    case "addx":
		cycle++;
		sum += checkIfInteresting(cycle, X);
		X += Integer.parseInt(cmd[1]);
		break sw;
	    case "noop":
		break sw;
	    }

	    cycle++;

	    sum += checkIfInteresting(cycle, X);
	}

	println(sum);

        /* visualize output here */
        generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    public boolean matchesHorizontal(long X, long cycle) {
	//X-1, X, X+1
	var left = (40 + X - 1) % 40;
	var mid = (X % 40);
	var right = (X + 1)%40;

	cycle -= 1;
	if ((cycle%40 == left) ||
	    (cycle%40 == mid) ||
	    (cycle%40 == right))
	    return true;
	return false;

    }

    public void draw(long cycle, long X) {
	if(matchesHorizontal(X, cycle))
	    print("#");
	else
	    print(".");
	if((cycle) % 40 == 0)
	    println();
    }

    public long checkIfInteresting(long cycle, long X) {
	if(interesting(cycle)) {
	    //println("register: " + X + "  cycle: " + cycle);
	    return cycle * X;
	}
	draw(cycle, X);
	return 0;
    }

    public boolean interesting(long x) {
	return (x == 20 || (x > 20 && ((x-20)%40) == 0));
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
        new Advent2022_10().run(argv);
    }
}
