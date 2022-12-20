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
public class Advent2022_20 extends Drafter {
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
        var ints = Util.toIntList(lines);





	var p1 = mix(ints, 1, 1);
        DEBUGF(1, "PART ONE: "); println(p1);
	var p2 = mix(ints, 10, 811589153l);
        DEBUGF(1, "PART TWO: "); println(p2);

        /* visualize output here */
        generate_output();

	return DEBUG(1, t.split("Finished Processing"));
    }

    public Long mix(ArrayList<Integer> ints, int times, long key) {
	ArrayList<Node> ordering = new ArrayList<Node>();

	Node magic = null;
	for(var j : ints) {
	    var i = j * key;
	    if(ordering.isEmpty())
		ordering.add(new Node(i, null));
	    else {
		var last = ordering.get(ordering.size() - 1);
		var n = new Node(i, last);
		last.next = n;
		ordering.add(n);
		if(n.value == 0)
		    magic = n;
	    }
	}

	int count = ordering.size();
	var last = ordering.get(count - 1);
	var first = ordering.get(0);

	merge(last, first);

	for(int iteration = 0; iteration < times; iteration++) {
	    for(var n : ordering) {
		n.seek(count);
		/* print from the first value */

		if(_DEBUG_LEVEL >= 2) {
		    var ct = first;
		    for(int i = 0; i < count; i++) {
			DEBUGF(2, "%s ", ct);
			ct = ct.next;
		    }
		    DEBUG(2, "");
		}
	    }
	}

	var one = magic.peek(1000, count);
	var two = magic.peek(2000, count);
	var three = magic.peek(3000, count);

	var score = one + two + three;
	DEBUGF("%d, %d, %d%n", one, two, three);

	return score;
    }

    private void merge(Node first, Node last) {
	first.next = last;
	last.last = first;
    }

    private class Node {
	long value;
	Node next;
	Node last;
	public Node (long value, Node last) {
	    this.value = value;
	    this.last = last;
	}

	public String toString() { return "" + value;}

	public long peek(int dist, int size) {
	    dist = dist % size;
	    var target = this;
	    for(int i = 0; i < dist; i++)
		target = target.next;
	    return target.value;
	}

	public void seek(int size) {
	    var dist = (Math.abs(value) % (size-1)) * (int)Math.signum(value);

	    if(dist == 0)
		return;

	    /* we can shave time by flipping the sign if it's greater than half of size */
	    if(dist > size/2)
		dist -= (size - 1);
	    if(dist < (-1 * size/2))
		dist += (size - 1);

	    DEBUG(2, dist);
	    var target = this;
	    /* rotate right */
	    if(dist > 0) {
		/* find the node we want to be infront of */
		for(int x = 0; x < dist; x++)
		    target = target.next;
		/* stitch the node before/after us together */
		merge(last, next);
		/* stitch ourselves inbetween the target and the next node */
		merge(this, target.next);
		merge(target, this);
		return;
	    }

	    /* rotate left */
	    for(int x = 0; x > dist; x--)
		target = target.last;

	    /* stitch the node before/after us together */
	    merge(last, next);

	    /* stitch ourselves inbetween the target and the last node */
	    merge(target.last, this);
	    merge(this, target);
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
        new Advent2022_20().run(argv);
    }
}
