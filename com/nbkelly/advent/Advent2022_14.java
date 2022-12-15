package com.nbkelly.advent;

/* imports */
import com.nbkelly.drafter.Drafter;
import com.nbkelly.drafter.Command;
import com.nbkelly.drafter.FileCommand;
import com.nbkelly.drafter.Timer;
import com.nbkelly.lib.Util;

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
public class Advent2022_14 extends Drafter {
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

	DEBUG("PARSING...");
	HashMap<IntPair,Character> tokens = new HashMap<>();

	Integer lowest = 0;
	for(var line : lines) {
	    var aspects = line.split(" -> ");
	    for(int i = 0; i < aspects.length - 1; i++) {
		var left = aspects[i].split(",");
		var right = aspects[i+1].split(",");

		int x1 = Math.min(Integer.parseInt(left[0]), Integer.parseInt(right[0]));
		int x2 = Math.max(Integer.parseInt(left[0]), Integer.parseInt(right[0]));

		int y1 = Math.min(Integer.parseInt(left[1]), Integer.parseInt(right[1]));
		int y2 = Math.max(Integer.parseInt(left[1]), Integer.parseInt(right[1]));

		lowest = Math.max(lowest, y1);
		if(x1 != x2) {
		    for(int x = x1; x <= x2; x++)
			tokens.put(new IntPair(x, y1), '#');
		}
		else {
		    for(int y = y1; y <= y2; y++)
			tokens.put(new IntPair(x1, y), '#');
		}
	    }
	}

	println("lowest: " + lowest);

	DEBUG(t.split("Part One - begins"));

	//simulate the falling sand
	//the sand always spawns at position 500, 0
	/*IntPair spawn = new IntPair(500, 0);
	int settled = 0;
	outer: while(true) {
	    var location = spawn;

	    inner: while(true) {
		//see if the sand needs to stop
		var nextMove = move(location, tokens, lowest+2);
		if(nextMove != null) {
		    location = nextMove;
		    //println(location);
		    if(location.Y > lowest)
			break outer;
		    continue inner;
		}
		settled++;
		tokens.put(location, 'O');
		continue outer;
	    }
	}

	println(settled);


	DEBUGF(1, "PART TWO: "); //todo
	outer2: while(true) {
	    if(tokens.containsKey(spawn))
		break outer2;

	    var location = spawn;

	    inner2: while(true) {
		var nextMove = move(location, tokens, lowest+2);
		if(nextMove != null) {
		    location = nextMove;
		    continue inner2;
		}
		settled++;
		tokens.put(location, 'O');
		continue outer2;
	    }
	}


	println(settled);*/
	//sand always spawns at 500,0
	//y is down

	IntPair spawnPoint = new IntPair(500, 0);
	int tokenStart = tokens.size();

	Sand current = new Sand(null, spawnPoint);

	while(current != null) {
	    current = current.newmove(tokens, lowest+2);
	    if(current.location.Y > lowest)
		break;
	}

	DEBUGF(1, "PART ONE: ");
	println(tokens.size() - tokenStart);
	DEBUG(t.split("part one ends"));
	DEBUG(t.split("part two begins"));

	while(current != null)
	    current = current.newmove(tokens, lowest+2);

	/*HashMap<Character, Character> key = new HashMap<>();
	key.put('#', '#');
	key.put('O', 'O');


	for(var line : Util.compose(tokens, key, ' ', true))
	println(line);*/

	DEBUGF(1, "PART TWO: ");
	println(tokens.size() - tokenStart);
        /* visualize output here */
        generate_output();

	return DEBUG(1, t.split("Finished Processing"));
    }

    public class Sand {
	IntPair location;
	Sand last;

	public Sand(Sand last, IntPair location) {
	    this.last = last;
	    this.location = location;
	}

	@Override public int hashCode() {
	    return location.hashCode();
	}

	public Sand newmove(HashMap<IntPair, Character> set, int floor) {
	    var move = move(location, set, floor);
	    //println("move: " + move);
	    if(move != null)
		return new Sand(this, move);
	    //there's no more move here - fill this block, return last
	    set.put(location, 'O');
	    return last;
	}
    }

    private IntPair move(IntPair location, HashMap<IntPair, Character> set, int floor) {
	var down = new IntPair(location.X, location.Y + 1);

	if(location.Y + 1 == floor)
	    return null;

	if(!set.containsKey(down))
	    return down;

	var dl = new IntPair(location.X-1, location.Y + 1);
	if(!set.containsKey(dl))
	    return dl;

	var dr = new IntPair(location.X+1, location.Y + 1);
	if(!set.containsKey(dr))
	    return dr;

	return null;
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
        new Advent2022_14().run(argv);
    }
}
