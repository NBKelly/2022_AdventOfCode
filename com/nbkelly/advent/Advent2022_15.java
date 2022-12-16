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
public class Advent2022_15 extends Drafter {
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

	boolean practice = lines.size() == 14;

        DEBUGF(1, "PART ONE: "); //todo
	ArrayList<Sensor> sensors = new ArrayList<Sensor>();

	for(var line : lines) {
	    var parts = line.split("=|(, )|(\\: )");
	    sensors.add(new Sensor
			(new IntPair(Integer.parseInt(parts[1]), Integer.parseInt(parts[3])),
			 new IntPair(Integer.parseInt(parts[5]), Integer.parseInt(parts[7]))));
	}

	//find the leftmost and the biggest offset
	var left = sensors.get(0).location.X;
	var right = sensors.get(0).location.X;
	var off = manhattan(sensors.get(0).location, sensors.get(0).closestBeacon);

	for(var s : sensors) {
	    left = Math.min(left, s.location.X);
	    right = Math.max(right, s.location.X);
	    off = Math.max(off, s.size());
	}

	int y = practice ? 10 : 2000000;
	int hits = 0;
	outer: for(int x = left - off; x <= right + off; x++) {
	    for(var s : sensors)
		if(s.closestBeacon.equals(new IntPair(x, y)))
		    continue outer;
	    for(var s : sensors) {
		if(manhattan(new IntPair(x, y), s.location) <= s.size()) {
		    hits++;
		    continue outer;
		}
	    }
	}



	println(hits);
        DEBUGF(1, "PART TWO: "); //todo

	int range = practice ? 20 : 4000000;
	for(y = 0; y < range; y++) {
	    var res = scanLine(y, 0, range, sensors);
	    if(res != null) {
		long score = (4000000l * res.X) + res.Y;
		println(score);
		break;
	    }
	}

        /* visualize output here */
        generate_output();

	return DEBUG(1, t.split("Finished Processing"));
    }

    public IntPair scanLine(int y, int xmin, int xmax, ArrayList<Sensor> sensors) {
	//first, find any beacons on this y
	ArrayList<IntPair> relevantBeacons = new ArrayList<>();
	for(var s : sensors)
	    if(s.closestBeacon.Y == y && s.closestBeacon.X >= xmin && s.closestBeacon.X <= xmax)
		relevantBeacons.add(s.closestBeacon);

	//get all of the intervals
	ArrayList<Interval> intervals = new ArrayList<>();
	for(var sensor : sensors) {
	    var dy = Math.abs(y - sensor.location.Y);
	    //println(dy);
	    var flex = sensor.size() - dy;
	    //println(flex);
	    if(flex < 0)
		continue;
	    Interval i = new Interval(Math.max(xmin-1,sensor.location.X - flex),
				      Math.min(xmax+1,sensor.location.X + flex));
	    intervals.add(i);
	}
	for(var s : relevantBeacons)
	    intervals.add(new Interval(s.X, s.X));

	intervals.sort((a, b) -> a.open - b.open);

	//merge all intervals if possible
	outer: for(int i = 0; i < intervals.size(); i++) {
	    var ct = intervals.get(i);
	    for(int j = i + 1; j < intervals.size(); j++) {
		if(ct.merge(intervals.get(j))) {
		    i--;
		    intervals.remove(j);
		    continue outer;
		}
	    }
	}

	if(intervals.size() == 2)
	    return new IntPair(intervals.get(0).close + 1, y);
	return null;
    }

    public class Interval {
	int open;
	int close;
	public Interval(int open, int close) {
	    this.open = open;
	    this.close = close;
	}

	public boolean merge(Interval i) {
	    //the interval is sequentially next
	    /*if(i.open == close + 1) {
		this.close = i.close;
		return true;
		}*/
	    //this overlaps at all
	    if(open -1 <= i.close && close+1 >= i.open) {
		open = Math.min(open, i.open);
		close = Math.max(close, i.close);
		return true;
	    }
	    return false;
	}

	public String toString() {
	    return "(" + open + " -> " + close + ")";
	}
    }

    public Integer manhattan(IntPair a, IntPair b) {
	return Math.abs(a.X-b.X) + Math.abs(a.Y-b.Y);
    }

    private class Sensor {
	IntPair location;
	IntPair closestBeacon;

	public int size() {
	    return manhattan(location, closestBeacon);
	}

	public Sensor (IntPair location, IntPair closestBeacon) {
	    this.location = location;
	    this.closestBeacon = closestBeacon;
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
        new Advent2022_15().run(argv);
    }
}
