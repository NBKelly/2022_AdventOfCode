package com.nbkelly.advent;

/* imports */
import com.nbkelly.drafter.Drafter;
import com.nbkelly.drafter.Command;
import com.nbkelly.drafter.FileCommand;
import com.nbkelly.drafter.Timer;

/* imports from file */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;
/* my imported libs */
import com.nbkelly.lib.Util;
import com.nbkelly.drafter.BooleanCommand; //visualize cmd
import com.nbkelly.lib.Image; //visualizer lib
import com.nbkelly.lib.IntPair;
import com.nbkelly.lib.HashCounter;

/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2021_14 extends Drafter {
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

	var polymer = lines.get(0);
	var tail = polymer.charAt(polymer.length() - 1);
	
	int ct = 0;
	HashMap<String, String> rules = new HashMap<String, String>();
	
	for(var line : lines)
	    if(ct++ >= 2) {		
		var split = line.split(" -> ");
		rules.put(split[0], split[1]);
	    }

	HashCounter<String> pairs = new HashCounter<String>();
	
	for(int i = 0; i < polymer.length() - 1; i++)
	    pairs.add(pair(polymer.charAt(i), polymer.charAt(i+1)));	 
	

	println(">Good Morning!");
	
	for(int i = 0; i < 10; i++)
	    pairs = expand(pairs, rules);
        
        DEBUGF(1, "PART ONE: "); println(score(pairs, tail));

	for(int i = 10; i < 40; i++)
	    pairs = expand(pairs, rules);
	
        DEBUGF(1, "PART TWO: "); println(score(pairs, tail));
        
        /* visualize output here */
        generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    public Long score(HashCounter<String> poly, Character tail) {
	HashCounter<Character> ct = new HashCounter<Character>();

	for(var pair : poly) {
	    ct.add(pair.charAt(0), poly.count(pair));
	}

	ct.add(tail);

	return ct.count(ct.max()) - ct.count(ct.min());
    }
    
    private String pair(Character a, Character b) { return String.format("%c%c",a,b); }

    public HashCounter<String> expand(HashCounter<String> poly, HashMap<String, String> rules) {
	HashCounter<String> res = new HashCounter<String>();

	for(var pair : poly) {
	    var count = poly.count(pair);

	    if(rules.containsKey(pair)) {
		var left = pair.charAt(0) + rules.get(pair);
		var right = rules.get(pair) + pair.charAt(1);
		res.add(left, count);
		res.add(right, count);
	    }
	    else
		res.add(pair, count);
	}

	return res;
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
        new Advent2021_14().run(argv);
    }
}
