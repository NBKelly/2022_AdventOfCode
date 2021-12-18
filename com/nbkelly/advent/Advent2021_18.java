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

/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2021_18 extends Drafter {
    /* WORKFLOW:
     *  set all needed commands with setCommands()
     *  post-processing can be performed with actOnCommands()
     *  the rest of your work should be based around the solveProblem() function
     */
    
    /* params injected from file */
    ArrayList<String> lines;
    
    //generate output
    boolean generate_output = false;

    public String add(String left, String right) {
	return String.format("[%s,%s]",left,right);
    }

    int MAX_DEPTH = 4;
    
    /* solve problem here */
    @Override public int solveProblem() throws Exception {
	Timer t = makeTimer();

	var sum = lines.get(0);

	for(int i = 1; i < lines.size(); i++) {
	    var line = lines.get(i);

	    /* to add, make a pair from both numbers */
	    sum = add(sum, line);

	    /* then reduce */
	    sum = reduce(sum);
	}

	
	println(">Good Morning!");
	
        DEBUGF(1, "PART ONE: "); println(magnitude(sum, 0));
	DEBUG(1, t.split("part 1 complete"));
        
	BigInteger largest = BigInteger.ZERO;
	
	for(int x1 = 0; x1 < lines.size(); x1++) {
	    for(int x2 = x1+1; x2 < lines.size(); x2++) {
		sum = String.format("[%s,%s]", lines.get(x1), lines.get(x2));
		sum = reduce(sum);
		var val = magnitude(sum, 0);

		if(val.compareTo(largest) > 0)
		    largest = val;

		/* do it backwards too */
		sum = String.format("[%s,%s]", lines.get(x2), lines.get(x1));
		sum = reduce(sum);
		val = magnitude(sum, 0);

		if(val.compareTo(largest) > 0)
		    largest = val;
	    }
	}
	
	DEBUGF(1, "PART TWO: "); println(largest); 
	
	/* visualize output here */
        generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    public BigInteger magnitude(String number, int start) {
	if(Character.isDigit(number.charAt(start+1))) {
	    var left = get_number(number, start+1);
	    var mid = start+1 + left.length();
	    
	    if(number.charAt(mid+1) == '[')
		return new BigInteger(left).multiply(new BigInteger("3"))
		    .add(
			 magnitude(number, mid+1).multiply(new BigInteger("2"))
			 );

	    var right = get_number(number, mid+1);
	    return new BigInteger(left).multiply(new BigInteger("3"))
		.add(
		     new BigInteger(right).multiply(new BigInteger("2"))
		     );
	}

	/* find the end of this bracket */
	var end = find_bracket_end(number, start+1);
	var left = magnitude(number, start+1).multiply(new BigInteger("3"));
	
	if(number.charAt(end+1) == '[')
	    return left.add(
			    new BigInteger("2").multiply(magnitude(number, end+1))
			    );
	
	return left.add(
			new BigInteger("2").multiply(new BigInteger(get_number(number,end+1)))
			);
    }

    public String reduce(String number) {
	while(true) {
	    var res = reduce_worker(number);
	    if(res.equals(number))
		return res;
	    else
		number = res;
	}
    }
    
    public String reduce_worker(String number) {
	/* see if a pair is nested inside four numbers */
	int depth = 0;
	for(int i = 0; i < number.length(); i++) {
	    if(number.charAt(i) == '[' && depth == MAX_DEPTH)
		return explode(number, i);
	    else if(number.charAt(i) == '[')
		depth++;
	    else if (number.charAt(i) == ']')
		depth--;	    
	}

	/* see if a number needs to split */
	for(int i = 0; i < number.length(); i++) {
	    if(Character.isDigit(number.charAt(i))) {
		var num = get_number(number, i);
		if(Integer.parseInt(num) > 9)
		    return split(number, i, num);
	    }
	}

	return number;
    }

    public String split(String number, int start, String to_split) {
	var num = Integer.parseInt(to_split);
	var left_num = num/2;
	var right_num = num/2;

	/* sue me */
	if(num %2 != 0)
	    right_num += 1;

	var constructed_pair = String.format("[%d,%d]", left_num, right_num);

	var left = number.substring(0, start);
	var right = number.substring(start + to_split.length());
	var res = left + constructed_pair + right;

	return res;
    }
    
    public int find_bracket_end(String number, int start) {
	int depth = 1;
	int index = start+1;
	for(; depth != 0; index++) {
	    if(number.charAt(index) == '[')
		depth++;
	    else if (number.charAt(index) == ']')
		depth--;
	}

	/* the final right bracket */
	return index;
    }
    
    public String get_number(String number, int start) {
	int index = start;
	while(Character.isDigit(number.charAt(index)))
	    index++;
	
	if(start != index)
	    return number.substring(start, index);
	
	return null;
    }

    public String explode(String number, int start) {
	int depth = 1;

	int x = 0;
	int mid = 0;
	for(x = start + 1; x < number.length(); x++) {
	    if(number.charAt(x) == '[')
		depth++;
	    else if (number.charAt(x) == ']')
		depth--;

	    else if (number.charAt(x) == ',' && depth == 1)
		mid = x;
	    
	    if(depth == 0)
		break;
	}

	var left = number.substring(start+1, mid);
	var right = number.substring(mid+1, x);
	
	/* the content before/after this pair */
	var pre = number.substring(0, start);	
	var post = number.substring(x+1);

	/* left is added to the first regular number to the left of the pair, if any */
	pre = insert_end(pre, Integer.parseInt(left));
	/* right is added to the first rightmost number, if any */
	post = insert_start(post, Integer.parseInt(right));

	return pre + "0" + post;
    }

    public String insert_start(String number, int value) {
	int index = 0;

	Integer start = null;
	for(; index < number.length(); index++)
	    if(Character.isDigit(number.charAt(index))) {
		start = index;
		break;
	    }

	if(start == null)
	    return number;

	for(; index < number.length(); index++)
	    if(!Character.isDigit(number.charAt(index))) {
		var left = number.substring(0, start);
		var prod = value + Integer.parseInt(number.substring(start, index));
		var right = number.substring(index);

		var res = left + prod + right;
		return res;
	    }

	return number;
    }
    
    public String insert_end(String number, int value) {
	int index = number.length() - 1;

	Integer end = null;
	for(; index >= 0; index--)
	    if(Character.isDigit(number.charAt(index))) {
		end = index;
		break;
	    }

	if(end == null)
	    return number;

	for(; index >= 0; index--)
	    if(!Character.isDigit(number.charAt(index))) {
		var left = number.substring(0, index+1);
		var prod = Integer.parseInt(number.substring(index+1, end+1)) + value;
		var right = number.substring(end+1);
		
		var res = left + prod + right;
		return res;
	    }

	return number;
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

	/* max depth */
	IntCommand ic = new IntCommand("Max Depth",
				       "Set the max depth to other than four",
				       1, 100, false, 4, "--max-depth");
				       
	
        return new Command[]{fc, vc, ic};
        
        
    }
    
    /* act after commands processed - userCommands stores all the commands set in setCommands */
    @Override public int actOnCommands(Command[] userCommands) throws Exception {
	//do whatever you want based on the commands you have given
	//at this stage, they should all be resolved
        /* code injected from file */
        lines = readFileLines(((FileCommand)userCommands[0]).getValue());
        setSource(((FileCommand)userCommands[0]).getValue());
        
        generate_output = ((BooleanCommand)userCommands[1]).matched();

	MAX_DEPTH = ((IntCommand)userCommands[2]).getValue();
	
	return 0;
    }

    /**
     * Creates and runs an instance of your class - do not modify
     */
    public static void main(String[] argv) {
        new Advent2021_18().run(argv);
    }
}
