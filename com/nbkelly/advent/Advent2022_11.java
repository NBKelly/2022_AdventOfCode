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
import java.util.Collections;
import java.lang.Comparable;


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
public class Advent2022_11 extends Drafter {
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

        DEBUGF(1, "PART ONE: "); //todo

	ArrayList<Monkey> monkeys = new ArrayList<>();

	for (int i = 0; i < lines.size(); i++) {
	    String line = lines.get(i);

	    // Skip empty lines
	    if (line.isEmpty()) {
		continue;
	    }

	    // Parse a monkey when we find a line that starts with "Monkey"
	    if (line.startsWith("Monkey")) {
		// Create a new monkey
		Monkey monkey = new Monkey();

		// Set the ID of the monkey using the number after "Monkey" in the line
		String[] parts = line.split(" ");
		monkey.id = Integer.parseInt(parts[1]);

		// Parse the starting items for the monkey
		line = lines.get(++i);
		parts = line.split(":")[1].trim().split(",");
		monkey.items = new LinkedList<>();
		for (String item : parts) {
		    monkey.items.add(new BigInteger(item.trim()));
		}

		// Parse the operation for the monkey
		line = lines.get(++i);
		parts = line.split(":")[1].trim().split("=");
		monkey.operation = parts[1].trim();

		// Parse the test for the monkey
		line = lines.get(++i);
		parts = line.split(":")[1].trim().split(" ");
		monkey.test = new BigInteger(parts[0]);

		// Parse the "If true" branch of the test
		line = lines.get(++i);
		parts = line.split(":")[1].trim().split(" ");
		monkey.ifTrue = Integer.parseInt(parts[1]);

		// Parse the "If false" branch of the test
		line = lines.get(++i);
		parts = line.split(":")[1].trim().split(" ");
		monkey.ifFalse = Integer.parseInt(parts[1]);

		// Add the monkey to the list of monkeys
		monkeys.add(monkey);
	    }
	}

	int numRounds = 10000;
	boolean reduceWorry = false;

	BigInteger three = BigInteger.valueOf(3);

	BigInteger mod = BigInteger.ONE;
	for(var monkey : monkeys)
	    mod = mod.multiply(monkey.test);

	for(int i = 0; i < numRounds; i++) {
	    for(var monkey : monkeys) {
		var items = monkey.items;

		while(items.size() > 0) {
		    monkey.inspections++;
		    var inspect = items.poll();
		    var second = BigInteger.ZERO;

		    switch(monkey.op[1]) {
		    case "old":
			second = inspect;
			break;
		    default:
			second = BigInteger.valueOf(Integer.parseInt(monkey.op[1]));
		    }

		    switch(monkey.op[0]) {
		    case "*":
			inspect = inspect.multiply(second);
			break;
		    case "+":
			inspect = inspect.add(second);
			break;
		    }

		    BigInteger rounded = reduceWorry? inspect.divide(three) : inspect;
		    rounded = rounded.mod(mod);

		    if(rounded.mod(monkey.test).equals(BigInteger.ZERO))
			monkeys.get(monkey.true_id).items.add(rounded);
		    else
			monkeys.get(monkey.false_id).items.add(rounded);
		}
	    }
	}

	Collections.sort(monkeys);
	for(var monkey : monkeys)
	    println(monkey);
        DEBUGF(1, "PART TWO: "); //todo

        /* visualize output here */
        generate_output();

	return DEBUG(1, t.split("Finished Processing"));
    }

    class Monkey implements Comparable<Monkey>{
	int inspections = 0;
	int id;
	LinkedList<BigInteger> items;
	String operation;
	BigInteger test;
	int ifTrue;
	int ifFalse;

	public int compareTo(Monkey o) {
	    return this.inspections - ((Monkey)o).inspections;
	}

	public String toString() {
	    return "id: " + id +", comps: " + inspections + "  items: " + items;

	}
    }

    /*
      private class Monkey implements Comparable<Monkey>{
      int inspections = 0;
      int index = 0;
      LinkedList<BigInteger> items = new LinkedList<BigInteger>();
      String[] op;
      BigInteger test;
      int true_id;
      int false_id;

      public Monkey() {
      this.index = Integer.parseInt(nextLine().split(" ")[1].charAt(0) + "");
      var si = nextLine().substring("  Starting items: ".length()).split(", ");
      for(int i = 0; i < si.length; i++)
      items.add(BigInteger.valueOf(Integer.parseInt(si[i])));
      this.op = nextLine().substring("  Operation: new = old ".length()).split(" ");
      this.test = BigInteger.valueOf(Integer.parseInt(nextLine().substring("  Test: divisible by ".length())));
      this.true_id = Integer.parseInt(nextLine().split("monkey ")[1]);
      this.false_id = Integer.parseInt(nextLine().split("monkey ")[1]);
      if(hasNextLine())
      nextLine();
      }

      public int compareTo(Monkey o) {
      return this.inspections - ((Monkey)o).inspections;
      }

      public String toString() {
      return "id: " + index +", comps: " + inspections + "  items: " + items;

      }
      }*/

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
        new Advent2022_11().run(argv);
    }
}
