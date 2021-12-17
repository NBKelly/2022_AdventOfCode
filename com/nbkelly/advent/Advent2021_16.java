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
public class Advent2021_16 extends Drafter {
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

	HashMap<Character, String> map = new HashMap<Character, String>();

	map.put('0', "0000");
	map.put('1', "0001");
	map.put('2', "0010");
	map.put('3', "0011");
	map.put('4', "0100");
	map.put('5', "0101");
	map.put('6', "0110");
	map.put('7', "0111");
	map.put('8', "1000");
	map.put('9', "1001");
	map.put('A', "1010");
	map.put('B', "1011");
	map.put('C', "1100");
	map.put('D', "1101");
	map.put('E', "1110");
	map.put('F', "1111");	
	
        /* code injected from file */
        //var ints = Util.toIntList(lines);

	var line = lines.get(0);
	var conv = toBits(line, map);
	
	/*println(line);
	println(conv);
	println(version(conv, 0));
	println(typeID(conv, 3));*/
        println(">Good Morning!");


	var parsed_ans = parse(conv, map, 0, 0);	
	DEBUG(2, "");
	
	int sum_ver = 0;
	for(var v : versions)
	    sum_ver += toInt(v);
	
        DEBUGF(1, "PART ONE: "); println(sum_ver); //todo
        DEBUGF(1, "PART TWO: "); println(parsed_ans.X); //todo
        
        /* visualize output here */
        generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    public ArrayList<String> versions = new ArrayList<String>();
    
    public Pair<BigInteger, Integer> parse(String s, HashMap<Character, String> map,
					   int start, int depth) {
	int index = start;

	//var version = version(s, index);
	var type = typeID(s, start + 3);
	switch(type) {
	case "100":
	    DEBUGF(3, "PARSE SELECTS LITERAL AT INDEX = %d DEPTH %d%n", start, depth);	    
	    var pair = literal(s, map, start, depth+1);
	    index = pair.Y;

	    DEBUGF(2, "%s ", pair.X);
	    
	    return pair;
	default:
	    DEBUGF(3, "PARSE SELECTS OPERATOR AT INDEX = %d DEPTH %d%n", start, depth);
	    var pair2 = operator(s, map, index, depth+1);
	    index = pair2.Y;
	    //println(pair2.X);
	    return pair2;
	}
    }

    public Pair<BigInteger, Integer> operator(String s, HashMap<Character, String> map,
					      int start, int depth) {
	int index = start;
	var version = version(s, index);
	index += 3;

	var type = typeID(s, index);
	index += 3;

	var mode = get(s, index, 1);
	index += 1;
	
	versions.add(version);
	
	DEBUGF(3, "OPERATOR AT INDEX %d DEPTH %d - VER %s TYPE %s MODE %s%n",
	       start, depth, version, type, mode);	

	String len = "";
	if(mode.equals("0")) {
	    len = get(s, index, 15);
	    index += 15;
	}
	else {
	    len = get(s, index, 11);
	    index += 11;
	}
			
	int decoded_len = toInt(len);
	int start_index = index;


	DEBUG(3, "OPERATOR LEN (INT): " + decoded_len);
	/* debug output */
	if(_DEBUG_LEVEL >= 2) {
	    /* select the symbol to use */
	    var token = "?";

	    switch(type) {
	    case "000": token = "+";   break;
	    case "001": token = "*";   break;
	    case "010": token = "MIN"; break;
	    case "011": token = "MAX"; break;
	    case "100": token = "LIT"; break;
	    case "101": token = "<";   break;
	    case "110": token = ">";   break;
	    case "111": token = "==";  break;
	    }

	    DEBUGF(2, "( %s ", token);
	}

	ArrayList<BigInteger> subvalues = new ArrayList<>();
	
	if(mode.equals("1"))
	    for(int i = 0; i < decoded_len; i++) {
		var pair = parse(s, map, index, depth + 1);
		
		index = pair.Y;
		subvalues.add(pair.X);
	    }
	else
	    while(index < start_index + decoded_len) {
		var pair = parse(s, map, index, depth + 1);

		index = pair.Y;
		subvalues.add(pair.X);
	    }

	DEBUGF(2, ")");
	
	switch(type) {
	case "000": return new Pair<BigInteger, Integer>(sum(subvalues), index);	   
	case "001": return new Pair<BigInteger, Integer>(product(subvalues), index);	    
	case "010": return new Pair<BigInteger, Integer>(min(subvalues), index);
	case "011": return new Pair<BigInteger, Integer>(max(subvalues), index);
	case "101": return new Pair<BigInteger, Integer>(gt(subvalues), index);
	case "110": return new Pair<BigInteger, Integer>(lt(subvalues), index);
	case "111": return new Pair<BigInteger, Integer>(eq(subvalues), index);
	}

	
	
	return new Pair<BigInteger, Integer>(null, index);
    }

    private BigInteger eq(ArrayList<BigInteger> vals) {
	if(vals.get(0).equals(vals.get(1)))
	    return BigInteger.ONE;

	return BigInteger.ZERO;
    }

    
    private BigInteger lt(ArrayList<BigInteger> strs) {
	if(strs.get(0).compareTo(strs.get(1)) < 0)
	    return BigInteger.ONE;

	return BigInteger.ZERO;
    }

    private BigInteger gt(ArrayList<BigInteger> strs) {
	if(strs.get(0).compareTo(strs.get(1)) > 0)
	    return BigInteger.ONE;

	return BigInteger.ZERO;
    }    
    
    private BigInteger max(ArrayList<BigInteger> strs) {
	strs.sort((x, y) -> y.compareTo(x));

	return strs.get(0);
    }

    
    private BigInteger min(ArrayList<BigInteger> strs) {
	strs.sort((x, y) -> x.compareTo(y));

	return strs.get(0);
    }
    
    private BigInteger product(ArrayList<BigInteger> strs) {
	BigInteger s = BigInteger.ONE;
	
	for(var str : strs) {
	    s = s.multiply(str);
	}

	return s;
    }

    
    private BigInteger sum(ArrayList<BigInteger> strs) {
	BigInteger s = BigInteger.ZERO;
	for(var str : strs) {
	    s = s.add(str);
	}

	return s;
    }

    public int toInt(String s) {
	return Integer.parseInt(s, 2);
    }
    
    public Pair<BigInteger, Integer> literal(String s, HashMap<Character, String> map,
					     int start, int depth) {
	
	var version = version(s, start);
	var type = typeID(s, start + 3);
	
	versions.add(version);
	
	var index = start + 6;
	String current = "";
	while(true) {
	    String ext = get(s, index, 5);
	    index += 5;
	    current += ext.substring(1);
	    if(ext.startsWith("0"))
		break;	    
	}

	/* get the actual value of the literal */
	DEBUGF(3, "LITERAL WITH VALUE %s STARTING AT %d DEPTH %d%n",
	       new  BigInteger(current, 2), depth, start);
	
	return new Pair<BigInteger, Integer>(new BigInteger(current, 2), index);
    }

    public String toBits(String s, HashMap<Character, String> str) {
	String res = "";

	for(int i = 0; i < s.length(); i++)
	    res += str.get(s.charAt(i));

	return res;
    }

    public String get(String s, int start, int len) {
	return s.substring(start, start + len);
    }

    public String typeID(String s, int start) {
	return get(s, start, 3);
    }
    
    public String version(String s, int start) {
	return get(s, start, 3);
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
        new Advent2021_16().run(argv);
    }
}
