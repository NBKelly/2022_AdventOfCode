package com.nbkelly.advent;

/* imports */
import com.nbkelly.drafter.Drafter;
import com.nbkelly.drafter.Command;
import com.nbkelly.drafter.FileCommand;
import com.nbkelly.drafter.Timer;

/* imports from file */
import java.util.ArrayList;
import java.util.LinkedList;
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
public class Advent2021_20 extends Drafter {
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

	HashSet<IntPair> image = new HashSet<IntPair>();
	var algo = lines.get(0);

	for(int y = 2; y < lines.size(); y++) {
	    var line = lines.get(y);
	    for(int x = 0; x < line.length(); x++)
		if(line.charAt(x) == '#')
		    image.add(new IntPair(x, y));
	}
	
	println(image);

	//int external_
	for(int i = 1; i <= 50; i++) {
	    int min_x = min_x(image);
	    int max_x = max_x(image);
	    int min_y = min_y(image);
	    int max_y = max_y(image);

	    /* note: our thing starts with 1 */	    
	    HashSet<IntPair> newImage = new HashSet<IntPair>();

	    if(algo.startsWith("#")) {
		for(int x = min_x - 3; x <= max_x + 3; x++)
		    for(int y = min_y - 3; y <= max_y + 3; y++) {
			int val = 0;
			if(i%2 == 1)
			    val = getVal(new IntPair(x, y), image);
			else
			    val = getVal(new IntPair(x, y), image,
					 min_x, max_x,
					 min_y, max_y); //*/
			if(algo.charAt(val) == '#')
			    newImage.add(new IntPair(x, y));
			
		    }
	    }
	    else {
		for(int x = min_x - 3; x <= max_x + 3; x++)
		    for(int y = min_y - 3; y <= max_y + 3; y++) {
			int val = getVal(new IntPair(x, y), image);
			if(algo.charAt(val) == '#')
			    newImage.add(new IntPair(x, y));
		    }
	    }

	    

	    image = newImage;
	    //print_image(image);
	    println("iteration " + i + " cells: " + image.size());
	}
	
        /* code injected from file */
        //var ints = Util.toIntList(lines);
        
        println(">Good Morning!");
        
        DEBUGF(1, "PART ONE: "); //todo
        DEBUGF(1, "PART TWO: "); //todo
        
        /* visualize output here */
        generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    public void print_image(HashSet<IntPair> image) {
	int min_x = min_x(image);
	int max_x = max_x(image);
	int min_y = min_y(image);
	int max_y = max_y(image);

	for(int y = min_y; y <= max_y; y++) {
	    for(int x = min_x; x <= max_x; x++)
		if(image.contains(new IntPair(x, y)))
		    print("#");
		else
		    print(".");
	    println();
	}
    }

    public Integer getVal(IntPair pair, HashSet<IntPair> set,
			  int min_x, int max_x,
			  int min_y, int max_y) {
	int val = 0;
	for(int y = -1; y < 2; y++)
	    for(int x = -1; x < 2; x++) {
		val *= 2;
		var dx = pair.X + x;
		var dy = pair.Y + y;

		if(dx < min_x || dx > max_x || dy < min_y || dy > max_y)
		    val++;		
		else if(set.contains(new IntPair(pair.X + x, pair.Y + y)))
		    val++;		
	    }

	return val;
    }
    
    public Integer getVal(IntPair pair, HashSet<IntPair> set) {
	int val = 0;
	for(int y = -1; y < 2; y++)
	    for(int x = -1; x < 2; x++) {
		val *= 2;
		if(set.contains(new IntPair(pair.X + x, pair.Y + y)))
		    val++;		
	    }

	return val;
    }

    int min_y(HashSet<IntPair> pairs) {
	Integer min = 999;
	for(var pair : pairs)
	    if(pair.Y < min)
		min = pair.Y;
	return min;
    }

    int max_y(HashSet<IntPair> pairs) {
	Integer max = -999;
	for(var pair : pairs)
	    if(pair.Y > max)
		max = pair.Y;
	return max;
    }

    
    int min_x(HashSet<IntPair> pairs) {
	Integer min = 999;
	for(var pair : pairs)
	    if(pair.X < min)
		min = pair.X;
	return min;
    }

    int max_x(HashSet<IntPair> pairs) {
	Integer max = -999;
	for(var pair : pairs)
	    if(pair.X > max)
		max = pair.X;
	return max;
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
        new Advent2021_20().run(argv);
    }
}
