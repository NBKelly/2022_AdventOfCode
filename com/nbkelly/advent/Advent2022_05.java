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
import java.util.LinkedList;

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
public class Advent2022_05 extends Drafter {
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

	ArrayList<String> stack_strs = new ArrayList<String>();
	int max_num = 0;

	for(var line : lines) {
	    if(!line.contains("[")) {
		var sp = line.split("   ");
		max_num = Integer.parseInt(sp[sp.length - 1].split(" ")[0]);
		break;
	    }
	    stack_strs.add(line);
	}



        DEBUGF(1, "PART ONE: "); //todo
	ArrayList<LinkedList<Character>> stacks = new ArrayList<>();
	for(int i = 0; i < max_num; i++)
	    stacks.add(new LinkedList<Character>());

	for(var line : stack_strs) {
	    for(int x = 1; x < line.length(); x += 4) {
		if(line.charAt(x) != ' ')
		    stacks.get(x/4).addLast(line.charAt(x));
	    }
	}

	//run through the instructions
	for(var line : lines) {
	    var split = line.split(" ");
	    if(!split[0].equals("move"))
		continue;

	    var count = Integer.parseInt(split[1]);
	    var from = Integer.parseInt(split[3]);
	    var to = Integer.parseInt(split[5]);

	    for(int i = 0; i < count; i++) {
		stacks.get(to-1).addFirst(stacks.get(from-1).pollFirst());
	    }
	}

	String res = "";
	for(var stack : stacks)
	    res = res + (stack.get(0));
	println(res);
	println(max_num);


        DEBUGF(1, "PART TWO: "); //todo
	stacks = new ArrayList<>();
	for(int i = 0; i < max_num; i++)
	    stacks.add(new LinkedList<Character>());

	for(var line : stack_strs) {
	    for(int x = 1; x < line.length(); x += 4) {
		if(line.charAt(x) != ' ')
		    stacks.get(x/4).addLast(line.charAt(x));
	    }
	}

	//run through the instructions
	for(var line : lines) {
	    var split = line.split(" ");
	    if(!split[0].equals("move"))
		continue;

	    var count = Integer.parseInt(split[1]);
	    var from = Integer.parseInt(split[3]);
	    var to = Integer.parseInt(split[5]);

	    var newList = new LinkedList<Character>();
	    for(int i = 0; i < count; i++) {
		newList.add(stacks.get(from-1).pollFirst());
	    }
	    stacks.get(to-1).addAll(0, newList);
	}

	res = "";
	for(var stack : stacks)
	    res = res + (stack.get(0));
	println(res);
	println(max_num);

        /* visualize output here */
        generate_output();

	return DEBUG(1, t.split("Finished Processing"));
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
        new Advent2022_05().run(argv);
    }
}
