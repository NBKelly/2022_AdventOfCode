package com.nbkelly.advent;

/* imports */
import com.nbkelly.drafter.Drafter;
import com.nbkelly.drafter.Command;
import com.nbkelly.drafter.FileCommand;
import com.nbkelly.drafter.Timer;

/* imports from file */
import java.util.ArrayList;
import java.math.BigInteger;

/* my imported libs */
import com.nbkelly.lib.Util;
import com.nbkelly.drafter.BooleanCommand; //visualize cmd
import com.nbkelly.lib.Image; //visualizer lib
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.HashMap;

/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2021_12 extends Drafter {
    /* WORKFLOW:
     *  set all needed commands with setCommands()
     *  post-processing can be performed with actOnCommands()
     *  the rest of your work should be based around the solveProblem() function
     */
    
    /* params injected from file */
    ArrayList<String> lines;
    
    //generate output
    boolean generate_output = false;

    //memoize output
    boolean use_memo = false;

    public boolean isSmall(String state) { return Character.isLowerCase(state.charAt(0)); }
    public boolean isEnd(String state) {   return state.equals("end");   }
    public boolean isStart(String state) { return state.equals("start"); }

    /* solve problem here */
    @Override public int solveProblem() throws Exception {
	Timer t = makeTimer();

	TreeMap<String, TreeSet<String>> states = new TreeMap<>();
	
	for(var line : lines) {
	    var left = line.split("-")[0];
	    var right = line.split("-")[1];

	    if(!states.containsKey(left))
		states.put(left, new TreeSet<String>());

	    if(!states.containsKey(right))
		states.put(right, new TreeSet<String>());

	    states.get(left).add(right);
	    states.get(right).add(left);
	}
	
        println(">Good Morning!");

	if(use_memo) {
	    DEBUGF(1, "PART ONE (MEMO): "); println(solve_memo(states, false));
	    DEBUGF(1, "PART TWO (MEMO): "); println("left as exercise to reader :)");
	    //println(solve(states, true));
	}
	else {
	    DEBUGF(1, "PART ONE: "); println(solve(states, false));
	    DEBUGF(1, "PART TWO: "); println(solve(states, true));
	}
        
        /* visualize output here */
        generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    private int memo_iter = 0;
    private BigInteger from_memo(TreeMap<String, HashMap<TreeSet<String>, BigInteger>> memo,
				 String from, TreeSet<String> forbid) {
	if(memo.containsKey(from)) {
	    var val = memo.get(from).get(forbid);	    
	    if(val != null)
		if(++memo_iter % 1000 == 0)
		    DEBUGF(2, "ITER %d from memo VALUE=%s%n", memo_iter, val);
	    return val;
	}

		
	return null;
    }

    private void to_memo(TreeMap<String, HashMap<TreeSet<String>, BigInteger>> memo,
			 String from, TreeSet<String> forbid, BigInteger value) {
	/*if(value == null)
	  value = BigInteger.ZERO;*/
	
	if(!memo.containsKey(from))
	    memo.put(from, new HashMap<TreeSet<String>, BigInteger>());

	memo.get(from).put(forbid, value);
    }
    
    private BigInteger solve_memo(TreeMap<String, TreeSet<String>> states, boolean allow_repeat) {
	//so roughly what our solution looks like will be
	// find number of paths from X to end, not including [...]
	//   to do that, find number of paths from (Xb) to end not including [...]b +
	//                                         (Xa) to end not including [...]a ...
	//   etc
	//we can memoize some of these steps, I hope

	//memo would have to be
	// state -> map -> value
	TreeMap<String, HashMap<TreeSet<String>, BigInteger>> memo = new TreeMap<>();
	
	return numPaths(states, "start", "end", new TreeSet<>(), memo);
    }

    private BigInteger numPaths(TreeMap<String, TreeSet<String>> states,
				String from, String to, TreeSet<String> forbid,
				TreeMap<String, HashMap<TreeSet<String>, BigInteger>> memo) {
	if(from.equals(to))
	    return BigInteger.ONE;

	var _from = from_memo(memo, from, forbid);
	if(_from != null)
	    return _from;

	BigInteger sum = BigInteger.ZERO;
	
	if(isSmall(from)) {
	    //we have to add this to forbid
	    TreeSet<String> next_forbid = new TreeSet<>(forbid);
	    next_forbid.add(from);

	    for(var next : states.get(from))
		if(!forbid.contains(next))
		    sum = sum.add(numPaths(states, next, to, next_forbid, memo));

	    to_memo(memo, from, forbid, sum);
	    return sum;
	}

	for(var next : states.get(from))
	    if(!forbid.contains(next))
		sum = sum.add(numPaths(states, next, to, forbid, memo));

	to_memo(memo, from, forbid, sum);
	return sum;
    }
    
    private BigInteger solve(TreeMap<String, TreeSet<String>> states, boolean allow_repeat) {
	LinkedList<History> active = new LinkedList<History>();
	active.add(new History().extend("start", allow_repeat));

	int iter = 0;
	BigInteger res = BigInteger.ZERO;
	while(active.size() > 0) {
	    var current = active.pollFirst();

	    inner: for(var state : states.get(current.last)) {
		if(isEnd(state)) {
		    res = res.add(BigInteger.ONE);
		    continue inner;
		}
		
		var next = current.extend(state, allow_repeat);

		/* dfs stores the least in memory - we still need to visit every state */
		if(next != null)
		    active.push(next);
	    }

	    if(++iter % 10000 == 0)
		DEBUGF(2, "ITERATION %d SIZE %d CONFIRMED %s%n ", iter,
		       active.size(),
		       res);
	}
	
	return res;
    }

    private class History {
	/* big nodes visited since last small node */
	TreeSet<String> sinceLastProgress = new TreeSet<String>();

	/* sequence of nodes visited */
	//ArrayList<String> sequence = new ArrayList<String>();

	/* set of all visited small nodes */
	TreeSet<String> small_visited = new TreeSet<String>();

	/* whatever small node has been revisted (if any) */
	String revisited = null;

	/* last visited state */
	String last = null;
	
	public History() {}

	private History(String new_state, History last, boolean is_repeat) {
	    if(is_repeat)
		revisited = new_state;
	    else
		revisited = last.revisited;

	    /* keep all last small visited */
	    small_visited.addAll(last.small_visited);
	    
	    /* if it's big, then copy sinceLastProgress */
	    if(isSmall(new_state))
		small_visited.add(new_state);
	    else {
		sinceLastProgress.addAll(last.sinceLastProgress);
		sinceLastProgress.add(new_state);
	    }
	    
	    //sequence.addAll(last.sequence);
	    //sequence.add(new_state);

	    this.last = new_state;
	}
	
	public History extend(String state, boolean allow_repeat) {
	    if(isSmall(state)) {
		if(small_visited.contains(state)) {
		    /* if we can repeat, return a repeat */
		    if(allow_repeat && this.revisited == null
		       && !isStart(state))
			return new History(state, this, true);
		    return null;
		}

		return new History(state, this, false);
	    }
	    
	    /* see if we're stuck in a loop */
	    if(sinceLastProgress.contains(state))
		return null;

	    return new History(state, this, false);
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

	BooleanCommand memo = new BooleanCommand("Memoize Solver",
						 "Use the memoized solver for this program",
						 false, "--use-memo", "--memo");
        return new Command[]{fc, vc, memo};
        
        
    }
    
    /* act after commands processed - userCommands stores all the commands set in setCommands */
    @Override public int actOnCommands(Command[] userCommands) throws Exception {
	//do whatever you want based on the commands you have given
	//at this stage, they should all be resolved
        /* code injected from file */
        lines = readFileLines(((FileCommand)userCommands[0]).getValue());
        setSource(((FileCommand)userCommands[0]).getValue());
        
        generate_output = ((BooleanCommand)userCommands[1]).matched();
	use_memo = ((BooleanCommand)userCommands[2]).matched();
	return 0;
    }

    /**
     * Creates and runs an instance of your class - do not modify
     */
    public static void main(String[] argv) {
        new Advent2021_12().run(argv);
    }
}
