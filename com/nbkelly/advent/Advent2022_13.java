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
import java.util.Collections;

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
public class Advent2022_13 extends Drafter {
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

	ArrayList<String> packets = new ArrayList<String>();

	int isum = 0;
	for(int i = 0; i < lines.size(); i+=3) {
	    var left = lines.get(i);
	    var right = lines.get(i+1);
	    packets.add(left);
	    packets.add(right);

	    if(ordered(left, right)) {
		isum += ((i/3)+1);
	    }
	}

	println(isum);

        DEBUGF(1, "PART TWO: "); //todo
	var two = "[[2]]";
	var nine = "[[6]]";

	packets.add(two);
	packets.add(nine);

        packets.sort((left, right) -> ordered(left, right) ? -1 : 1);

	/*for(var line : packets)
	  println(line);*/

	var res = (1 + packets.indexOf(two)) * (1 + packets.indexOf(nine));
	println(res);
        /* visualize output here */
        generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    public Boolean ordered(String left, String right) {
	if(isList(left) && isList(right)) {
	    //break the list into component lists
	    var lc = getListContent(left, 1);
	    var rc = getListContent(right, 1);

	    //println(lc);
	    //compare the first value of each list, then the second value, and so on.
	    int index = 0;
	    while(index < lc.size() && index < rc.size()) {
		var res = ordered(lc.get(index), rc.get(index));
		if(res == null) {
		    index++;
		    continue;
		}
		return res;
	    }

	    //if the left list runs out first, the items are correct
	    if(lc.size() < rc.size())
		return true;
	    //if the right list runs out of items first, the inputs are wrong
	    else if (lc.size() > rc.size())
		return false;
	    //otherwise inconclusive
	    return null;
	}
	else if (!isList(left) && !isList(right)) {
	    //both values are integers
	    var lval = Integer.parseInt(left);
	    var rval = Integer.parseInt(right);

	    //lower val should be on the left
	    if(lval < rval)
		return true;
	    else if (lval > rval)
		return false;
	    return null;
	}

	//one value is an integer
	else if (isList(left))
	    return ordered(left, "[" + right + "]");

	return ordered("[" + left + "]", right);
    }

    public boolean isList(String s) {
	return s.startsWith("[");
    }

    public ArrayList<String> getListContent(String s, int index) {
	ArrayList<String> res = new ArrayList<String>();

	String ct = "";
	outer: while(index < s.length()) {
	    switch(s.charAt(index)) {
	    case '[':
		int bracket = 1;
		ct += s.charAt(index);
		while(bracket > 0) {
		    index++;
		    if(s.charAt(index) == ']')
			bracket--;
		    else if (s.charAt(index) == '[')
			bracket++;
		    ct += s.charAt(index);
		}
		index++;

		res.add(ct);
		ct = "";
		continue outer;
	    case ']':
		if(ct.length() > 0)
		    res.add(ct);
		break outer;
	    case ',':
		if(ct.length() > 0)
		    res.add(ct);
		ct = "";
		index++;
		continue outer;
	    default:
		ct += s.charAt(index);
		index++;
	    }
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
        new Advent2022_13().run(argv);
    }
}
