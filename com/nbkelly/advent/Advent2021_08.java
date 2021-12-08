package com.nbkelly.advent;

/* imports */
import com.nbkelly.drafter.Drafter;
import com.nbkelly.drafter.Command;
import com.nbkelly.drafter.FileCommand;
import com.nbkelly.drafter.Timer;

/* imports from file */
import java.util.ArrayList;

/* my imported libs */
import com.nbkelly.lib.Util;
import com.nbkelly.drafter.BooleanCommand; //visualize cmd
import com.nbkelly.lib.Image; //visualizer lib
import com.nbkelly.lib.pathfinder.Map;
import com.nbkelly.lib.IntPair;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.TreeSet;
/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2021_08 extends Drafter {
    /* WORKFLOW:
     *  set all needed commands with setCommands()
     *  post-processing can be performed with actOnCommands()
     *  the rest of your work should be based around the solveProblem() function
     */
    
    /* params injected from file */
    ArrayList<String> lines;
    
    //generate output
    boolean generate_output = false;

    private class StringSet implements Comparable<StringSet> {
	HashSet<Character> chars = new HashSet<Character>();
	public String symbol = "-";

	public StringSet(String s) {
	    for (int x = 0; x < s.length(); x++)
		chars.add(s.charAt(x));
	}

	public StringSet(String s, String symbol) {
	    for (int x = 0; x < s.length(); x++)
		chars.add(s.charAt(x));
	    this.symbol = symbol;
	}

	private StringSet(HashSet<Character> c) {
	    chars.addAll(c);
	}
	
	public boolean equals(StringSet st) {
	    return chars.equals(st.chars);
	}

	public boolean equals(String s) {
	    return equals(new StringSet(s));
	}	

	public String toString() {
	    return chars.toString();
	}

	public void setSymbol(String s) {
	    this.symbol = s;
	}

	public Integer size() {
	    return chars.size();
	}
	
	public int compareTo(StringSet s) {
	    return toString().compareTo(s.toString());
	}
    }

    public StringSet subtract(StringSet a, StringSet b) {
	var set = new StringSet(a.chars);
	set.chars.removeAll(b.chars);
	return set;
    }

    public StringSet union(StringSet a, StringSet b) {
	var set = new StringSet(a.chars);
	set.chars.retainAll(b.chars);
	return set;
    }
    
    
    /* solve problem here */
    @Override public int solveProblem() throws Exception {
	Timer t = makeTimer();

        /* code injected from file */
        //var ints = Util.toIntList(lines);
        
        println("Hello World");

	/* find all occurances of one, four, seven, eight */
	int simple_count = 0;
	for(var line : lines) {
	    //split into left + right
	    var tokens = Arrays.asList(line.split(" \\| ")[1].split(" "));
	    for(var token : tokens)
		switch(token.length()) {
		case 2:
		case 4:
		case 3:
		case 7:
		    simple_count++;		    
		}	    
	}

	DEBUGF(1, "PART ONE: "); println(simple_count);


	/* part two */

	int tally = 0;

	for(var line : lines) {
	    HashMap<Integer, StringSet> mappings = new HashMap<>();
	    
	    var input = new HashSet<String>(Arrays.asList(line.replace(" | ", " ").split(" ")));

	    TreeSet<StringSet> unresolved = new TreeSet<StringSet>();
	    
	    for(var digit : input) {
		switch(digit.length()) {
		case 2:
		    mappings.put(1, new StringSet(digit, "1"));
		    break;
		case 4:
		    mappings.put(4, new StringSet(digit, "4"));
		    break;
		case 3:
		    mappings.put(7, new StringSet(digit, "7"));
		    break;
		case 7:
		    mappings.put(8, new StringSet(digit, "8"));
		    break;
		default:
		    unresolved.add(new StringSet(digit));
		}
	    }

	    /* we can get 2 by selecting the size 5 set that matches 8 - 4 */
	    var reduction_two = subtract(mappings.get(8), mappings.get(4));

	    var two = unresolved.stream().
		filter(set -> set.size() == 5 && subtract(set, reduction_two).size() == 2)
		.findAny().get();
	    mappings.put(2, two);
	    
	    unresolved.remove(two);
	    two.setSymbol("2");
	    
	    /* knowing 2 lets us isolate 0 */
	    var mid = subtract(subtract(mappings.get(2), mappings.get(1)),
			       reduction_two);

	    var zero = subtract(mappings.get(8), mid);
	    mappings.put(0, zero);

	    unresolved.remove(zero);
	    zero.setSymbol("0");

	    /* knowing 2 and 1 lets us know 6 */
	    var six = subtract(mappings.get(8),
			       union(mappings.get(2), mappings.get(1)));

	    mappings.put(6, six);
	    unresolved.remove(six);
	    six.setSymbol("6");

	    /* the only one left with 6 segments is nine */
	    var nine = unresolved.stream().
		filter(set -> set.size() == 6).findAny().get();

	    mappings.put(9, nine);
	    unresolved.remove(nine);
	    nine.setSymbol("9");

	    /* if it contains the top right segment, it's 3 */
	    var three = unresolved.stream().
		filter(set -> union(set, union(mappings.get(2), mappings.get(1))).size() == 1)
		.findAny().get();

	    mappings.put(3, three);
	    unresolved.remove(three);
	    three.setSymbol("3");

	    /* the last one left is 5 */
	    
	    var five = unresolved.pollFirst();
	    mappings.put(5, five);
	    five.setSymbol("5");

	    /* decode the sequence */
	    var decoder = new TreeSet<>(mappings.values());
	    
	    var right = Arrays.asList(line.split(" \\| ")[1].split(" "));
	    String decoded = "";
	    for(var token : right)
		decoded += decode(token, decoder);

	    tally += Integer.parseInt(decoded);	    
	}
	
        DEBUGF(1, "PART TWO: "); println(tally);//todo
        
        /* visualize output here */
        generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    private String decode(String s, TreeSet<StringSet> set) {
	for(var mapping : set)
	    if(mapping.compareTo(new StringSet(s)) == 0)
		return mapping.symbol;

	return "-";
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
        new Advent2021_08().run(argv);
    }
}
