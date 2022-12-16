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
import java.util.LinkedList;
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
public class Advent2022_16 extends Drafter {
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

	HashMap<String,Node> nodes = new HashMap<String,Node>();
	HashMap<String, Node> realNodes = new HashMap<String, Node>();
	ArrayList<String> relevantNodes = new ArrayList<String>();
	for(var line : lines) {
	    //split
	    var components =
		line.split("(Valve )|( has flow rate=)|" +
			   "(; tunnel leads to valve )|(; tunnels lead to valves )|(, )");

	    var node = new Node(components[1], Integer.parseInt(components[2]));
	    for(int i = 3; i < components.length; i++)
		node.addTarget(components[i]);
	    nodes.put(components[1], node);

	    if(!components[2].equals("0") || components[1].equals("AA")) {
		relevantNodes.add(components[1]);
		realNodes.put(components[1], new Node(components[1], Integer.parseInt(components[2])));
	    }
	}

	/* for each relevant node, find the distance to all other relevant nodes */
	for(int i = 0; i < relevantNodes.size(); i++) {
	    for(int j = i + 1; j < relevantNodes.size(); j++) {
		var left = relevantNodes.get(i);
		var right = relevantNodes.get(j);
		var dist = dist(left, right, nodes);
		realNodes.get(left).targets.put(right, dist);
		realNodes.get(right).targets.put(left, dist);
	    }
	}

        DEBUGF(1, "\nPART ONE: ");
	println(bestPath(0, new TreeSet<String>(realNodes.keySet()), realNodes));

        DEBUGF(1, "PART TWO: ");
	println(bestPath2(4, realNodes));

        /* visualize output here */
        generate_output();

	return DEBUG(1, t.split("Finished Processing"));
    }

    private Integer bestPath2(int startTime, HashMap<String, Node> nodes) {
	//do the same as before, but run back through all the best parts of the metric
	State start = new State();
	start.time = startTime;

	TreeSet<State> prio = new TreeSet<>();
	prio.add(start);

	int bestPressure = 0;
	HashMap<Metric,Integer> best = new HashMap<>();

	while(prio.size() > 0) {
	    var ct = prio.pollFirst();

	    bestPressure = Math.max(bestPressure, ct.score);
	    if(ct.time >= 30)
		continue;

	    var metric = ct.metric();
	    var bestScore = best.get(metric);
	    if(bestScore == null || bestScore <= ct.score)
		best.put(metric, ct.score);
	    else continue;

	    if(ct.time >= 30)
		continue;

	    /* additionally, if possible, disable the flow here */
	    var timeleft = ct.timeLeft();
	    var location = ct.location;
	    var node = nodes.get(ct.location);

	    //visit each adjacent link
	    for(var target : node.targets.keySet()) {
		//if there is only one target
		if(ct.active.contains(target))
		    continue;

		var dist = node.targets.get(target) + 1;

		if(dist + ct.time >= 30)
		    continue;

		//try moving there
		State next = new State(ct, ct.time + dist, ct.score);
		next.location = target;

		timeleft = next.timeLeft();
		int rate = nodes.get(target).rate;

		next.score += (timeleft * rate);
		next.active.add(target);

		prio.add(next);
	    }
	}

	int bestcombined = 0;

	HashMap<TreeSet<String>, Integer> bestPartial = new HashMap<>();
	int it = 0;
	int hit = 0;
	int miss = 0;
	for(var set : best.entrySet()) {
	    if(it++ % 1000 == 0)
		DEBUG("it " + it + " of " + best.size() + "(" + hit + "/" + miss + ")");
	    //get the set of allowed states
	    var allowed = new TreeSet<String>(nodes.keySet());
	    allowed.removeAll(set.getKey().active);

	    //memoize based on the treeset
	    if(!bestPartial.containsKey(allowed)) {
		bestPartial.put(allowed, bestPath(4, allowed, nodes));
		miss++;
	    }
	    else
		hit++;

	    bestcombined = Math.max(bestPartial.get(allowed)+ set.getValue(), bestcombined);

	}

	return bestcombined;
    }

    private Integer bestPath(int startTime, TreeSet<String> allowedStates,
			     HashMap<String, Node> nodes) {
	State start = new State();
	start.time = startTime;

	TreeSet<State> prio = new TreeSet<>();
	prio.add(start);

	int bestPressure = 0;
	int iteration = 0;

	HashMap<Metric,Integer> best = new HashMap<>();

	while(prio.size() > 0) {
	    var ct = prio.pollFirst();

	    bestPressure = Math.max(bestPressure, ct.score);
	    if(ct.time >= 29)
		continue;

	    var metric = ct.metric();
	    var bestScore = best.get(metric);
	    if(bestScore == null || bestScore <= ct.score)
		best.put(metric, ct.score);
	    else continue;

	    /* additionally, if possible, disable the flow here */
	    var timeleft = ct.timeLeft();
	    var location = ct.location;
	    var node = nodes.get(ct.location);

	    //visit each adjacent link
	    for(var target : node.targets.keySet()) {
		if(!allowedStates.contains(target))
		    continue;
		//if there is only one target
		if(ct.active.contains(target))
		    continue;

		var dist = node.targets.get(target) + 1;

		if(dist + ct.time >= 30)
		    continue;

		//try moving there
		State next = new State(ct, ct.time + dist, ct.score);
		next.location = target;

		timeleft = next.timeLeft();
		int rate = nodes.get(target).rate;

		next.score += (timeleft * rate);
		next.active.add(target);

		prio.add(next);
	    }
	}

	return bestPressure;
    }

    private class Node {
	String name;
	int rate;
	TreeMap<String, Integer> targets = new TreeMap<>();

	public Node(String name, int rate) {
	    this.name = name;
	    this.rate = rate;
	}

	@Override public int hashCode() {
	    return name.hashCode();
	}

	public void addTarget(String name) {
	    targets.put(name, 1);
	}
    }

    private int dist(String start, String end, HashMap<String, Node> nodes) {
	TreeSet<String> current = new TreeSet<>();
	TreeSet<String> next = new TreeSet<>();
	int dist = 0;

	current.add(start);

	while(true) {
	    while(current.size() > 0) {
		var ct = current.pollFirst();
		if(ct.equals(end))
		    return dist;
		else
		    next.addAll(nodes.get(ct).targets.keySet());
	    }

	    dist++;
	    current = next;
	    next = new TreeSet<>();
	}
    }

    /*private void crawl(Node n, HashMap<String, Node> nodes) {
	if(n.rate == 0) {
	    if(n.targets.size() == 2) {
		println("factoring out node " + n.name);
		//get the things that lead to me
		var left = n.targets.firstEntry().getKey();
		var right = n.targets.firstEntry().getKey();
		var leftnode = nodes.get(left);
		var rightnode = nodes.get(right);
		int dist = leftnode.targets.get(n.name) +
		    rightnode.targets.get(n.name);

		//see if there's a current link between, if any
		var cdist = leftnode.targets.get(right);
		if(cdist != null && cdist <= dist)
		    return;
		leftnode.targets.put(right, dist);
		rightnode.targets.put(left, dist);
	    }
	}
	}*/

    private class Metric implements Comparable<Metric>{
	String location;
	TreeSet<String> active;
	Integer time;

	public int compareTo(Metric m) {
	    return Util.compareTo(location, m.location,
				  active.toString(), m.active.toString(),
				  time, m.time);
	}
    }

    private class State implements Comparable<State> {
	public Metric metric() {
	    var metric = new Metric();
	    metric.location = location;
	    metric.active = new TreeSet(active);
	    metric.time = time;
	    return metric;
	}

	public State(State last, int time, int score) {
	    this.location = last.location;
	    this.active = new TreeSet(last.active);
	    this.time = time;
	    this.score = score;
	}

	public State() {
	    location = "AA";
	    active = new TreeSet();
	    time = 0;
	    score = 0;
	}

	String location;
	TreeSet<String> active = new TreeSet<>();
	Integer score;
	Integer time;

	int timeLeft() {
	    return 30 - time;
	}

	public int compareTo(State s) {
	    return Util.compareTo(time, s.time,
				  score, s.score,
				  active.toString(), s.active.toString(),
				  location, s.location);
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
        new Advent2022_16().run(argv);
    }
}
