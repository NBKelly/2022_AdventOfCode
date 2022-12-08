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
public class Advent2022_04 extends Drafter {
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
	int count_engulf = 0;
	int count_atall  = 0;
        DEBUGF(1, "PART ONE: "); //todo
	for(var line : lines) {
	    var pair = new Pair(line);
	    if(pair.engulfs())
		count_engulf++;
	    if(pair.atAll())
		count_atall++;
	}
	println(count_engulf);

        DEBUGF(1, "PART TWO: "); //todo
        println(count_atall);
        /* visualize output here */
        generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    private class Range {
	int open, close;
	int size = 0;
	public Range(int open, int close) {
	    this.open = open;
	    this.close = close;
	    this.size = close + 1 - open;
	}

	public Range intersection(Range target) {
	    if((close < target.open) || (open > target.close))
		return null;

	    return new Range(Math.max(open, target.open),
			     Math.min(close, target.close));
	}

	public String toString() {
	    return String.format("%d -> %d", open, close);
	}
    }

    class Pair {
	Range A;
	Range B;

	public boolean engulfs() {
	    Range overlap = A.intersection(B);
	    /*if(overlap != null)
		println(overlap);
	    else
	    println("--");*/
	    return (overlap != null && overlap.size == (Math.min(A.size, B.size)));
	}

	public boolean atAll() {
	    Range overlap = A.intersection(B);
	    return overlap != null;
	}

	public Pair(String line) {
	    var left = line.split(",")[0];
	    var right = line.split(",")[1];
	    A = new Range(Integer.parseInt(left.split("-")[0]),
			  Integer.parseInt(left.split("-")[1]));
	    B = new Range(Integer.parseInt(right.split("-")[0]),
			  Integer.parseInt(right.split("-")[1]));
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
	new Advent2022_04().run(argv);
    }
}
