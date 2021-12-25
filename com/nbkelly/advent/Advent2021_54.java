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
import com.nbkelly.drafter.IntCommand;
import com.nbkelly.lib.Image; //visualizer lib

import com.nbkelly.lib.IntPair;
import com.nbkelly.lib.pathfinder.Map;
import com.nbkelly.lib.Pair;

import java.math.BigInteger;
import java.util.Random;

import java.util.LinkedList;


/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2021_54 extends Drafter {
    /* WORKFLOW:
     *  set all needed commands with setCommands()
     *  post-processing can be performed with actOnCommands()
     *  the rest of your work should be based around the solveProblem() function
     */
    
    /* params injected from file */
    ArrayList<String> lines;
    
    //generate output
    boolean generate_output = false;

    Random rand = new Random();

    int stack_size = 3;
    
    /* solve problem here */
    @Override public int solveProblem() throws Exception {
	Timer t = makeTimer();

        /* code injected from file */
        //var ints = Util.toIntList(lines);
        
        DEBUGF(1, "PART ONE: "); //todo
        DEBUGF(1, "PART TWO: "); //todo


	int len  = stack_size*2;
	int pushes = 0;
	
	var stack = new LinkedList<Integer>();

	
	
	for(int i = 0; i < len; i++) {
	    if(pushes >= len/2)
		println(pop(stack));
	    else if(stack.size() == 0 || rand.nextInt(2) == 1) {
		println(push(stack, 16));
		pushes++;
	    }
	    else {
		println(pop(stack));
	    }
	}
		
	//println(push(stack, 16));

	//println(pop(stack));
	
        /* visualize output here */
        generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    private String pop(LinkedList<Integer> stack) {
	var balance = stack.pop();

	var min = Math.max(balance - 7, 0);
	var num = min + rand.nextInt(14);

	while(num - balance > 8)
	    num--;

	var a = num;
	var c = rand.nextInt(16);

	var res = "inp w\n" +
	    "mul x 0\n" +
	    "add x z\n" +
	    "mod x 26\n" +
	    "div z 26\n" +
	    "add x -" + a + "\n" +
	    "eql x w\n" +
	    "eql x 0\n" +
	    "mul y 0\n" +
	    "add y 25\n" + 
	    "mul y x\n" + 
	    "add y 1\n" +
	    "mul z y\n" +
	    "mul y 0\n" +
	    "add y w\n" +
	    "add y " + c + "\n" +
	    "mul y x\n" +
	    "add z y";

	return res;
    }
    
    private String push(LinkedList<Integer> stack, int max) {
	var val = rand.nextInt(max);

	var garb = rand.nextInt(16);

	var a = garb;
	var c = val;
	
	var res = "inp w\n" +
	    "mul x 0\n" +
	    "add x z\n" +
	    "mod x 26\n" +
	    "div z 1\n" +
	    "add x " + a + "\n" +
	    "eql x w\n" +
	    "eql x 0\n" +
	    "mul y 0\n" +
	    "add y 25\n" + 
	    "mul y x\n" + 
	    "add y 1\n" +
	    "mul z y\n" +
	    "mul y 0\n" +
	    "add y w\n" +
	    "add y " + c + "\n" +
	    "mul y x\n" +
	    "add z y";

	stack.push(val);
	
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

	IntCommand sizeOf = new IntCommand("Size of Program",
					   "The number of stack segments to use",
					   1, 100000, false, 7, "--stack-operations", "--stack-size",
					   "--stack", "--program-size");
	
        return new Command[]{fc, vc, sizeOf};
        
        
    }
    
    /* act after commands processed - userCommands stores all the commands set in setCommands */
    @Override public int actOnCommands(Command[] userCommands) throws Exception {
	//do whatever you want based on the commands you have given
	//at this stage, they should all be resolved
        /* code injected from file */
        lines = readFileLines(((FileCommand)userCommands[0]).getValue());
        setSource(((FileCommand)userCommands[0]).getValue());
        
        generate_output = ((BooleanCommand)userCommands[1]).matched();

	stack_size = ((IntCommand)userCommands[2]).getValue();
	return 0;
    }

    /**
     * Creates and runs an instance of your class - do not modify
     */
    public static void main(String[] argv) {
        new Advent2021_54().run(argv);
    }
}
