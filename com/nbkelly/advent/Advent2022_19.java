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
public class Advent2022_19 extends Drafter {
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
	ArrayList<Factory> factories = new ArrayList<>();
	for(var line : lines)
	    factories.add(new Factory(line));


	int score = 0;
	boolean skip  = false;
	if(!skip) {
	    for(int i = 0; i < factories.size(); i++) {
		var f = factories.get(i);
		DEBUG(f);
		var best = bestGeodes(f, 24);
		DEBUG(best);
		score += (1 + i) * best;
		DEBUG();
	    }
	    DEBUGF(1, "PART ONE: "); //todo
	    println(score);
	}

	score = 1;
	for(int i = 0; i < Math.min(3, factories.size()); i++) {
	    var f = factories.get(i);
	    DEBUG(f);
	    var best = bestGeodes(f, 32);
	    DEBUG(best);
	    score *= best;
	    DEBUG();
	}
        DEBUGF(1, "PART TWO: "); //todo
	println(score);

        /* visualize output here */
        generate_output();

	return DEBUG(1, t.split("Finished Processing"));
    }

    private class Metric implements Comparable<Metric>{
	private int ore;
	private int clay;
	private int obsidian;
	private int geodes;
	private int orebots;
	private int claybots;
	private int obsidianbots;
	private int geodebots;

	public int compareTo(Metric m) {
	    return Util.compareTo(ore, m.ore,
				  clay, m.clay,
				  obsidian, m.obsidian,
				  geodes, m.geodes,
				  orebots, m.orebots,
				  claybots, m.claybots,
				  obsidianbots, m.obsidianbots,
				  geodebots, m.geodebots);
	}
    }

    private const int GEODE = 0;
    private const int CLAY = 1;
    private const int OBSIDIAN = 2;
    private const int GEODE = 3;
    private const int NONE = -1;

    private int bestGeodesDP(Factory f, int time) {
	int maxOreBots =  Math.max(f.claybot.ore,
				   Math.max(f.obsidianbot.ore, f.geodebot.ore));
	int maxClayBots = f.obsidianbot.clay;
	int maxObsidianBots = f.geodebot.obsidian;

	/* there's a DP solution here. It's important to note that we can only build
	   a single robot per minute, so we can actually just simulate build orders instead
	   of anything else */
	HashSet<ArrayList<Integer>> seen = new HashSet<>();

	//todo: complete this
    }

    private boolean possible(ArrayList<Integer> order,

    private int bestGeodes(Factory f, int time) {
	LinkedList<State> states = new LinkedList<State>();
	states.add(new State(0, 0, 0, 0,
			     1, 0, 0, 0,
			     0));

	/* note: don't build more bots than you can spend in a minute */
	int maxOreBots =  Math.max(f.claybot.ore,
				   Math.max(f.obsidianbot.ore, f.geodebot.ore));
	int maxClayBots = f.obsidianbot.clay;
	int maxObsidianBots = f.geodebot.obsidian;

	int bestGeodes = 0;

	TreeSet<Metric> seen = new TreeSet<Metric>();

	while(states.size() != 0) {
	    var current = states.pollFirst();
	    if(current.time == time) {
		bestGeodes = Math.max(bestGeodes, current.geodes);
		continue;
	    }

	    var metric = current.metric();
	    if(seen.contains(metric))
		continue;
	    seen.add(metric);


	    int affordable = 0;

	    /* if we can build any robots, we build them */
	    if (current.canAfford(f.geodebot)) {
		State next = current.advance();
		next.purchase(f.geodebot);
		next.geodebots++;
		affordable++;
		states.add(next);
	    }
	    else {
		if (current.canAfford(f.orebot) && current.orebots < maxOreBots) {
		    State next = current.advance();
		    next.purchase(f.orebot);
		    next.orebots++;
		    affordable++;
		    states.add(next);
		}
		if(current.orebots >= 2 || f.orebot.ore < 4) {
		    if (current.canAfford(f.claybot) && current.claybots < maxClayBots) {
			State next = current.advance();
			next.purchase(f.claybot);
			next.claybots++;
			affordable++;
			states.add(next);
		    }
		    if (current.canAfford(f.obsidianbot) && current.obsidianbots < maxObsidianBots) {
			State next = current.advance();
			next.purchase(f.obsidianbot);
			next.obsidianbots++;
			affordable++;
			states.add(next);
		    }
		}
		//if(affordable < 3)
		states.add(current.advance());
	    }
	}

	return bestGeodes;
    }

    private class State {
	private int ore;
	private int clay;
	private int obsidian;
	private int geodes;
	private int orebots;
	private int claybots;
	private int obsidianbots;
	private int geodebots;
	private int time;

	public boolean canAfford(Cost c) {
	    return (ore >= c.ore &&
		    clay >= c.clay &&
		    obsidian >= c.obsidian);
	}

	public void purchase(Cost c) {
	    ore -= c.ore;
	    clay -= c.clay;
	    obsidian -= c.obsidian;
	}

	public State advance() {
	    return new State(ore + orebots,
			     clay + claybots,
			     obsidian + obsidianbots,
			     geodes + geodebots,
			     orebots,
			     claybots,
			     obsidianbots,
			     geodebots,
			     time + 1);
	}

	public Metric metric() {
	    Metric metric = new Metric();
	    metric.ore = ore;
	    metric.clay = clay;
	    metric.obsidian = obsidian;
	    metric.geodes = geodes;
	    metric.orebots = orebots;
	    metric.claybots = claybots;
	    metric.obsidianbots = obsidianbots;
	    metric.geodebots = geodebots;
	    return metric;
	}

	public State(int ore, int clay, int obsidian, int geodes, int orebots,
		     int claybots, int obsidianbots, int geodebots, int time) {
	    this.ore = ore;
	    this.clay = clay;
	    this.obsidian = obsidian;
	    this.geodes = geodes;
	    this.orebots = orebots;
	    this.claybots = claybots;
	    this.obsidianbots = obsidianbots;
	    this.geodebots = geodebots;
	    this.time = time;
	}
    }

    private class Factory {
	Cost orebot;
	Cost claybot;
	Cost obsidianbot;
	Cost geodebot;
	public Factory(String str) {
	    var split = str.split(" ");
	    orebot = new Cost(Integer.parseInt(split[6]), 0, 0);
	    claybot = new Cost(Integer.parseInt(split[12]), 0, 0);
	    obsidianbot = new Cost(Integer.parseInt(split[18]), Integer.parseInt(split[21]), 0);
	    geodebot = new Cost(Integer.parseInt(split[27]), 0, Integer.parseInt(split[30]));
	}

	public String toString() {
	    return String.format("  ore: %d ore%n  clay: %d ore%n  obsidian: %d ore %d clay%n  "
				 + "geode: %d ore %d obsidian",
				 orebot.ore,
				 claybot.ore,
				 obsidianbot.ore, obsidianbot.clay,
				 geodebot.ore, geodebot.obsidian);
	}

    }

    private class Cost {
	int ore;
	int clay;
	int obsidian;

	public Cost(int ore, int clay, int obsidian) {
	    this.ore = ore;
	    this.clay = clay;
	    this.obsidian = obsidian;
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
        new Advent2022_19().run(argv);
    }
}
