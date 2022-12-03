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
import java.util.HashSet;
import java.math.BigInteger;


/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2022_03 extends Drafter {
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


	ArrayList<Sack> sacks = new ArrayList<Sack>();
	for(var line : lines)
	    sacks.add(new Sack(line));

        DEBUGF(1, "PART ONE: "); //todo
	int sum = 0;
	for(var sack : sacks)
	    sum += sack.duplicate();
	println(sum);

        DEBUGF(1, "PART TWO: "); //todo

	int csum = 0;
	for(int i = 0; i < sacks.size(); i+=3) {
	    csum += common(sacks.get(i),
			   sacks.get(i+1),
			   sacks.get(i+2));
	}
	println(csum);

        /* visualize output here */
        generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    public int common(Sack a, Sack b, Sack c) {
	TreeSet<Integer> s1 = new TreeSet<Integer>(a.all);
	s1.retainAll(b.all);
	s1.retainAll(c.all);

	return s1.first();
    }

    private class Sack {
	TreeSet<Integer> left = new TreeSet<>();
	TreeSet<Integer> right = new TreeSet<>();
	TreeSet<Integer> all = new TreeSet<>();
	public Sack(String input) {
	    for(int i = 0; i < input.length(); i++) {
		int token = input.charAt(i);
		if(token <= 'Z')
		    token = 27 + (token - 'A');
		else
		    token = 1 + (token - 'a');
		if(i < input.length()/2)
		    left.add(token);
		else
		    right.add(token);
	    }

	    all.addAll(left);
	    all.addAll(right);
	}

	public String toString() {
	    return left.toString() + "\n" + right.toString() + "\n"
		+ left.size() + ", " + right.size();
	}

	public int duplicate() {
	    var set = new TreeSet<Integer>(left);
	    set.retainAll(right);
	    return set.first();
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
        new Advent2022_03().run(argv);
    }
}
