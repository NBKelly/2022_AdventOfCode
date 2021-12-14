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
import com.nbkelly.lib.IntPair;
import java.util.HashSet;

/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2021_13 extends Drafter {
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

	HashSet<IntPair> points = new HashSet<IntPair>();

	int index = 0;
	var line = "";
	while(!(line = lines.get(index)).equals("")) {
	    var split = line.split(",");
	    points.add(new IntPair(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
	    index++;
	}

	index++;
	boolean first = true;
	int ans = 0;
	while(index < lines.size()) {
	    line = lines.get(index);
	    /* fold the paper */
	    var sub = line.substring(11).split("=");

	    if(sub[0].equals("y")) {
		/* fold along y */
		points = foldY(points, Integer.parseInt(sub[1]));
		if(first) {
		    ans = points.size();
		    first = false;
		}
	    }
	    else {// if (sub[0].equals("x")) {
		points = foldX(points, Integer.parseInt(sub[1]));
		if(first) {
		    ans = points.size();
		    first = false;
		}
	    }
	    index++;
	}
	
	println(">Good Morning!");

        DEBUGF(1, "PART ONE: "); println(ans);//todo
        DEBUGF(1, "PART TWO:\n "); print_set(points);//todo
        
        /* visualize output here */
        generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    public void print_set(HashSet<IntPair> set) {
	int max_x = 0;
	int max_y = 0;

	for(var pair : set) {
	    max_y = Math.max(pair.Y, max_y);
	    max_x = Math.max(pair.X, max_x);
	}
	
	for(int y = -1; y <= max_y + 1; y++) {
	    for(int x = -1; x <= max_x + 1; x++)
		if(set.contains(new IntPair(x, y)))
		    print("█");
		else
		    print(".");
	    println();
	}
    }

    public HashSet<IntPair> foldX(HashSet<IntPair> set, int x) {
	HashSet<IntPair> res = new HashSet<IntPair>();

	for(var pair : set) {
	    if(pair.X < x)
		res.add(pair);
	    else {
		var diff = pair.X - x;
		res.add(new IntPair(x - diff, pair.Y));
	    }
	}

	return res;

    }
    
    public HashSet<IntPair> foldY(HashSet<IntPair> set, int y) {
	HashSet<IntPair> res = new HashSet<IntPair>();

	for(var pair : set) {
	    if(pair.Y < y)
		res.add(pair);
	    else {
		var diff = pair.Y - y;
		//println("diff: " + diff);
		//println("y: " + y);
		res.add(new IntPair(pair.X, y - diff));
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
        new Advent2021_13().run(argv);
    }
}
