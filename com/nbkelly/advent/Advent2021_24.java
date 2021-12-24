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
import java.util.LinkedList;

import java.util.stream.Collectors;
import java.math.BigInteger;


/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2021_24 extends Drafter {
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

	var segments = segment(lines, "inp w");

	LinkedList<String> stack = new LinkedList<String>();
	
	int index = 0;
	for(var segment : segments)
	    DEBUG(2, decode(segment, index++, stack));

	DEBUG(2, "===============");
	DEBUG(2, "     RULES     ");
	
	for(var s : stack)
	    DEBUG(2, s);

	println();
	
	DEBUGF(1, "PART ONE: "); println(generate_key(stack, true));
        DEBUGF(1, "PART TWO: "); println(generate_key(stack, false));
        
        /* visualize output here */
        generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }    
    
    public String generate_key(LinkedList<String> rules, boolean max) {
	var out = new StringBuilder("XXXXXXXXXXXXXX");

	for(var rule : rules) {
	    var split = rule.split("i|\\+|=");

	    var  left_index = Integer.parseInt(split[1]);
	    var right_index = Integer.parseInt(split[4]);

	    var left_offset = Integer.parseInt(split[2]);
	    var right_offset= Integer.parseInt(split[5]);

	    var offset_min = Math.min(left_offset, right_offset);
	    left_offset -= offset_min;
	    right_offset -= offset_min;
	    
	    if(max) {
		if(left_offset > 0) {
		    out = out.replace(left_index, left_index+1, (9-left_offset) + "");
		    out = out.replace(right_index, right_index+1, 9+"");
		}
		else {
		    out = out.replace(left_index, left_index+1, 9+"");
		    out = out.replace(right_index, right_index+1, (9-right_offset) + "");
		}
	    }
	    else {
		if(left_offset > 0) {
		    out = out.replace(left_index, left_index+1, 1 + "");
		    out = out.replace(right_index, right_index+1, (1+left_offset)+"");
		}
		else {
		    out = out.replace(left_index, left_index+1, (1+right_offset)+"");
		    out = out.replace(right_index, right_index+1, 1+"");
		}
	    }
	}
	return out.toString();
    }
    
    public String decode(ArrayList<String> segment, int index, LinkedList<String> stack) {
	var push = segment.get(4).equals("div z 1");

	if(push) {
	    var token = segment.get(15).substring(6);

	    var res = String.format("PUSH INPUT %d OFFSET %s", index, token);
	    stack.push(String.format("i%d+%s", index,token));
	    return res;
	}

	/* pop */
	var old = stack.pop();
	
	var offset = segment.get(5).substring(7);
	
	var res = String.format("POP INPUT %d OFFSET %s", index, offset);
	stack.add(String.format("%s=i%d+%s", old, index, offset));
	
	/* also add a rule to the stack */
	return res;	    
    }

    public ArrayList<ArrayList<String>> segment(ArrayList<String> input, String inst) {
	ArrayList<String> current = new ArrayList<String>();
	var res = new ArrayList<ArrayList<String>>();

	for(var line : input) {
	    if(line.equals(inst)) {
		if(current.size() > 0)
		    res.add(current);
		current = new ArrayList<>();
	    }
	    
	    current.add(line);
	}

	if(current.size() > 0)
	    res.add(current);

	return res;
    }

    private State run(String input, State state, ArrayList<String> program) {
	int input_index = 0;
	
	for(var line : lines) {
	    var instr = line.split(" ");
	    var next_state = state.clone();
	    
	    switch(instr[0]) {
	    case "inp":
		char val = (char)(input.charAt(input_index) - '0');
		input_index++;
		next_state.set(instr[1], val);
		break;
	    case "add":
		var add_val = next_state.get(instr[1]) + getValue(next_state, instr[2]);
		next_state.set(instr[1], add_val);
		break;
	    case "mul":
		var mul_val = next_state.get(instr[1]) * getValue(next_state, instr[2]);
		next_state.set(instr[1], mul_val);
		break;
	    case "div":
		var div_val = next_state.get(instr[1]) / getValue(next_state, instr[2]);
		next_state.set(instr[1], div_val);
		break;
	    case "mod":
		var mod_val = next_state.get(instr[1]) % getValue(next_state, instr[2]);
		next_state.set(instr[1], mod_val);
		break;
	    case "eql":
		var eql_val = next_state.get(instr[1]) == getValue(next_state, instr[2]);
		if(eql_val)
		    next_state.set(instr[1], 1);
		else
		    next_state.set(instr[1], 0);
		break;
	    }

	    state = next_state;
	}

	return state;
    }

    private Integer getValue(State s, String id) {
	var val = s.get(id);
	if(val == null)
	    return Integer.parseInt(id);
	return val;
    }

    class State {	
	public State(int w, int x, int y, int z) {
	    this.w = w;
	    this.x = x;
	    this.y = y;
	    this.z = z;
	}

	public State clone() {
	    return new State(w, x, y, z);
	}

	public Integer get(String c) {
	    switch(c) {
	    case "w": return w;
	    case "x": return x;
	    case "y": return y;
	    case "z": return z;
	    }

	    return null;
	}

	public void set(String c, int val) {
	    switch(c) {
	    case "w": w = val; return;
	    case "x": x = val; return;
	    case "y": y = val; return;
	    case "z": z = val; return;
	    }
	}
	
	int w;
	int x;
	int y;
	int z;

	public String toString() {
	    return w + " " + x + " " + y + " " + z;
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
        new Advent2021_24().run(argv);
    }
}
