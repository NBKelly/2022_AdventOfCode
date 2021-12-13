package com.nbkelly.advent;

/* imports */
import com.nbkelly.drafter.Drafter;
import com.nbkelly.drafter.Command;
import com.nbkelly.drafter.FileCommand;
import com.nbkelly.drafter.Timer;

/* imports from file */
import java.util.ArrayList;
import java.util.Random;

/* my imported libs */
import com.nbkelly.lib.Util;
import com.nbkelly.drafter.BooleanCommand; //visualize cmd
import com.nbkelly.lib.Image; //visualizer lib
import com.nbkelly.lib.IntPair;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2021_51 extends Drafter {
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

	int max_width = 0;

	ArrayList<String> new_lines = new ArrayList<String>();
	Random rand = new Random();

	HashSet<IntPair> map = new HashSet<IntPair>();

	int y = 0;
	int x_max = 0;
	for(var line : lines) {
	    for(int i = 0; i < line.length(); i++) {
		if(line.charAt(i) == ' ') {}
		else
		    map.add(new IntPair(i, y));

		x_max = Math.max(x_max, i);
	    }

	    y++;
	}

	LinkedList<String> instructions = new LinkedList<String>();

	for(int i = 0; i < 41; i++) {
	    DEBUG("Fold number " + i);
	    if(rand.nextInt(2) == 1)
		instructions.add(unfold_y(map));
	    else
		instructions.add(unfold_x(map));
	}
	
	
	//println(map);

	print_points(map);
	println();

	while(instructions.size() > 0)
	    println(instructions.pollLast());
	
        /* visualize output here */
        generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    public void print_points(HashSet<IntPair> set) {
	for(var point : set)
	    printf("%d,%d%n", point.X, point.Y);
    }
    
    public int max_x(HashSet<IntPair> set) {
	int max_x = 0;
	int max_y = 0;

	for(var pair : set) {
	    max_y = Math.max(pair.Y, max_y);
	    max_x = Math.max(pair.X, max_x);
	}

	return max_x;
    }

    Random rand = new Random();
    
    public int max_y(HashSet<IntPair> set) {
	int max_x = 0;
	int max_y = 0;

	for(var pair : set) {
	    max_y = Math.max(pair.Y, max_y);
	    max_x = Math.max(pair.X, max_x);
	}

	return max_y;
    }

    int rand_fac = 9;
    public String unfold_x(HashSet<IntPair> map) {
	var max_x = max_x(map) + 1;
	
	var select = rand.nextInt(max_x);

	var new_pairs = new HashSet<IntPair>();
	var remove = new HashSet<IntPair>();

	for(var pair : map)
	    if(pair.X >= select) {
		//calculate the new position
		var diff = max_x - pair.X;
		var new_pair = new IntPair(max_x + diff, pair.Y);

		if(new_pair.X < 0)
		    continue;
		
		if(rand.nextInt(rand_fac) >= 1)
		    remove.add(pair);
		new_pairs.add(new_pair);
	    }

	map.addAll(new_pairs);
	map.removeAll(remove);

	return "fold along x=" + max_x;
    }
    
    public String unfold_y(HashSet<IntPair> map) {
	var max_y = max_y(map) + 1;
	
	var select = rand.nextInt(max_y);

	/* find the distance between select and y_max + 1 */
	//int diff = max_y + 1 - select;

	HashSet<IntPair> new_pairs = new HashSet<IntPair>();
	HashSet<IntPair> remove = new HashSet<IntPair>();
	for(var pair : map)
	    if(pair.Y >= select) {
		//calculate the new position
		var diff = max_y - pair.Y;

		var new_pair = new IntPair(pair.X, max_y + diff);

		if(new_pair.Y < 0)
		    continue;
		
		if(rand.nextInt(rand_fac) >= 1)
		    remove.add(pair);

		new_pairs.add(new_pair);
	    }

	map.addAll(new_pairs);
	map.removeAll(remove);

	return "fold along y=" + max_y;
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
        new Advent2021_51().run(argv);
    }
}
