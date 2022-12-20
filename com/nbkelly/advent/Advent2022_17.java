package com.nbkelly.advent;

/* imports */
import com.nbkelly.drafter.Drafter;
import com.nbkelly.drafter.Command;
import com.nbkelly.drafter.FileCommand;
import com.nbkelly.drafter.Timer;

/* imports from file */
import java.util.ArrayList;
import java.util.Collections;
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
public class Advent2022_17 extends Drafter {
    /* WORKFLOW:
     *  set all needed commands with setCommands()
     *  post-processing can be performed with actOnCommands()
     *  the rest of your work should be based around the solveProblem() function
     */

    /* params injected from file */
    ArrayList<String> lines;

    //generate output
    boolean generate_output = false;

    private class Rock {
	ArrayList<String> shape = new ArrayList<String>();
	int width = 0;
	int height = 0;
	int x = 0;
	int y = 0;
	public Rock(long shape, int x) {
	    switch((int)(shape%5l)) {
	    case 0:
		width = 4;
		height = 1;
		this.shape.add("####");
		break;
	    case 1:
		width = 3;
		height = 3;
		this.shape.add(".#.");
		this.shape.add("###");
		this.shape.add(".#.");
		break;
	    case 2:
		width = 3;
		height = 3;
		this.shape.add("..#");
		this.shape.add("..#");
		this.shape.add("###");
		break;
	    case 3:
		width = 1;
		height = 4;
		this.shape.add("#");
		this.shape.add("#");
		this.shape.add("#");
		this.shape.add("#");
		break;
	    case 4:
		width = 2;
		height = 2;
		this.shape.add("##");
		this.shape.add("##");
		break;
	    }

	    this.x = x;
	}

	public void imprint(HashMap<IntPair, Character> fill) {
	    for(int a = 0; a < width; a++)
		for(int b = 0; b < height; b++)
		    if(shape.get(b).charAt(a) == '#') {
			//check it doesn't collide
			var dx = a + x;
			var dy = (y - b);
			fill.put(new IntPair(dx, dy), '#');
		    }
	}

	public boolean collides(HashMap<IntPair, Character> fill) {
	    for(int a = 0; a < width; a++)
		for(int b = 0; b < height; b++)
		    if(shape.get(b).charAt(a) == '#') {
			//check it doesn't collide
			var dx = a + x;
			var dy = (y - b);
			if(fill.containsKey(new IntPair(dx, dy)))
			    return true;
		    }
	    return false;
	}

	public boolean pushDown(HashMap<IntPair, Character> fill) {
	    y--;
	    if(collides(fill)) {
		y++;
		return false;
	    }
	    return true;
	}

	public boolean pushLeft(HashMap<IntPair, Character> fill) {
	    if(x > 0) {
		x--;
		if(collides(fill))
		    x++;
		else
		    return true;
	    }

	    return false;
	}

	public boolean pushRight(HashMap<IntPair, Character> fill) {
	    if(x < 7 - width) {
		x++;
		if(collides(fill))
		    x--;
		else
		    return true;
	    }
	    return false;
	}
    }
    
    /* solve problem here */
    @Override public int solveProblem() throws Exception {
	Timer t = makeTimer();

        /* code injected from file */
        //var ints = Util.toIntList(lines);

        println(">Good Morning!");


	int cwidth = 7;
	int starting_x = 2;
	int height_bonus = 3;

	HashMap<IntPair, Character> fill = new HashMap<>();
	fill.put(new IntPair(0, 0), '#');
	fill.put(new IntPair(1, 0), '#');
	fill.put(new IntPair(2, 0), '#');
	fill.put(new IntPair(3, 0), '#');
	fill.put(new IntPair(4, 0), '#');
	fill.put(new IntPair(5, 0), '#');
	fill.put(new IntPair(6, 0), '#');

	var input = nextLine();

	int inputval = 0;
	int highest_y = 0;
	int tlim = 2023;
	long rock = -1;
	int check_interval = 1;

	HashMap<Long, Integer> diffs = new HashMap<>();
	HashMap<Long, Integer> sums = new HashMap<>();
	while(true) {
	    rock++;
	    //create a new rock 3 units above the highest rock
	    int x = starting_x;
	    var shape = new Rock(rock%5, 2);
	    shape.y = 3 + highest_y + shape.height;
	    if(rock == tlim) {
		DEBUGF(1, "PART ONE: ");
		println("highest y: " + highest_y);

	    }

	    var index = rock;
	    sums.put(index, highest_y);
	    if(index == 0)
		diffs.put(index, highest_y);
	    else
		diffs.put(index,
			  highest_y - sums.get(index - 1)); // - diffs.get(index - 1));

	    if(index == 500000) {
		/* scan through rocks until we find a matching diffset */
		long start_scan_at = 10000;

		long diff = 50;
		int dval = 0;

		outer: for(; diff < 50000; diff++) {
		    for(var val = start_scan_at; val < index - (2*diff); val+=diff) {
			var next = val + diff;
			var next2 = val + diff + diff;
			var val1 = sums.get(next) - sums.get(val);
			var val2 = sums.get(next2) - sums.get(next);
			dval = val1;

			if(val1 != val2)
			    continue outer;
		    }

		    break outer;
		}

		DEBUG("diff: " + diff);

		/* we know the diff (it's actually a multiple of 5)
		   we start at index 2000 (* 5)
		   we want to get to index 1 trillion */
		var target_number = 1000000000000l;
		var we_start_at = start_scan_at;
		var remaining = target_number - we_start_at;
		var cycles = remaining / (diff);
		var remainder = remaining % (diff);

		DEBUG("cycles: " + cycles);
		DEBUG("remainder: " + remainder);

		long baseline = sums.get(we_start_at + remainder);
		baseline += (cycles * dval);

		DEBUGF(1, "PART TWO: ");
		println("res: " + baseline);
		break;
	    }

	    //int y = height_bonus + highest_y(fill) + shape.height;
	    inner: while(true) {
		//try moving it based on the instruction
		boolean left = input.charAt(inputval) == '<';
		if(left)
		    shape.pushLeft(fill);
		else
		    shape.pushRight(fill);

		inputval = (inputval + 1) % input.length();
		if(!shape.pushDown(fill)) {
		    shape.imprint(fill);
		    highest_y = Math.max(highest_y, shape.y);
		    break inner;
		}
	    }
	}

	HashMap<Character, Character> key = new HashMap<Character, Character>();
	key.put('#', '#');

	/*var screen = Util.compose(fill, key, '.', true);
	Collections.reverse(screen);
	screen = Util.limit_dimension(screen, false, false, 40);
	for(var line : screen)
	println(line);*/






        /* visualize output here */
        generate_output();

	return DEBUG(1, t.split("Finished Processing"));
    }



    public int highest_y(HashMap<IntPair, Character> fill) {
	int max = 0;
	for(var pair : fill.keySet())
	    max = Math.max(max, pair.Y);
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
        new Advent2022_17().run(argv);
    }
}
