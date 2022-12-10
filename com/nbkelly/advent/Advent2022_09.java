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
public class Advent2022_09 extends Drafter {
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

	HashSet<IntPair> set = new HashSet<IntPair>();
	IntPair[] pairs = new IntPair[10];
	for(var i = 0; i < 10; i++)
	    pairs[i] = new IntPair(0,0);

	for(var line : lines) {
	    var cmd = line.split(" ");
	    var dir = cmd[0];
	    var reps = Integer.parseInt(cmd[1]);
	    for(int i = 0; i < reps; i++) {
		pairs[0] = updatePosition(pairs[0], dir);
		pairs[1] = updateChain(1, pairs);
		set.add(pairs[1]);
		//printRope(2, pairs, 10);
	    }
	}

	println(set.size());
	DEBUG(t.split("part 1"));



	DEBUGF(1, "PART TWO: "); //todo

	set = new HashSet<>();
	pairs = new IntPair[10];
	for(var i = 0; i < 10; i++)
	    pairs[i] = new IntPair(0,0);

	for(var line : lines) {
	    var cmd = line.split(" ");
	    var dir = cmd[0];
	    var reps = Integer.parseInt(cmd[1]);
	    for(int i = 0; i < reps; i++) {
		pairs[0] = updatePosition(pairs[0], dir);
		for(int j = 1; j < 10; j++)
		    pairs[j] = updateChain(j, pairs);
		set.add(pairs[9]);
		//printRope(10, pairs, 15);
	    }
	}

	println(set.size());
        /* visualize output here */
        generate_output();

	return DEBUG(1, t.split("Finished Processing"));
    }

    public void printRope(int num, IntPair[] chain, int size) {
	String out = "";

	for(int y = -size; y <= size; y++){
	    x: for(int x = -size; x <= size; x++) {
		for(int c = 0; c < num; c++)
		    if(chain[c].X == x && chain[c].Y == y) {
			out = out + c;
			continue x;
		    }
		out = out + ".";
	    }
	    out += "\n";
	}

	println(out);
    }

    public IntPair updateChain(int index, IntPair[] chain) {
	var current = chain[index];
	var last = chain[index-1];

	int dx = current.X - last.X;
	int dy = current.Y - last.Y;

	//straight lines only
	if(dy == 0 && Math.abs(dx) > 1)
	    current = new IntPair(current.X - (int)(Math.signum(dx)), current.Y);
	else if (dx == 0 && Math.abs(dy) > 1)
	    current = new IntPair(current.X, current.Y - (int)(Math.signum(dy)));

	//diag
	else if(Math.abs(dx) == 2 || Math.abs(dy) == 2) {
	    current = new IntPair(current.X - (int)(Math.signum(dx)),
				  current.Y - (int)(Math.signum(dy)));
	}
	//else if (math.abs(dy) == 2 && Math.abs(dx) != 0)

	//println("before: " + last + " -->  " + current);

	//println("after: " + last + " -->  " + current);
	//println();

	return current;
    }

    public IntPair updatePosition(IntPair pair, String direction) {
	switch(direction){
	case "U": return new IntPair(pair.X, pair.Y+1);
	case "D": return new IntPair(pair.X, pair.Y-1);
	case "R": return new IntPair(pair.X+1, pair.Y);
	case "L": return new IntPair(pair.X-1, pair.Y);
	default: return null;
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
        new Advent2022_09().run(argv);
    }
}
