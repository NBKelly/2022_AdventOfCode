package com.nbkelly.advent;

/* imports */
import com.nbkelly.drafter.Drafter;
import com.nbkelly.drafter.Command;
import com.nbkelly.drafter.FileCommand;
import com.nbkelly.drafter.Timer;

/* imports from file */
import java.util.ArrayList;
import java.util.HashSet;

/* my imported libs */
import com.nbkelly.lib.Util;
import com.nbkelly.drafter.BooleanCommand; //visualize cmd
import com.nbkelly.lib.Image; //visualizer lib

/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2021_04 extends Drafter {
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
	var _draws_unfiltered = nextLine().split(",");
	ArrayList<Integer> draws_list = new ArrayList<Integer>();
	for(int i = 0; i < _draws_unfiltered.length; i++)
	    draws_list.add(Integer.parseInt(_draws_unfiltered[i]));
	
	flushLine(1);

	int bingo_size = 5;
	ArrayList<Bingo> boards = new ArrayList<Bingo>();
	while(hasNextLine()) {
	    var bingo = new Bingo(bingo_size);
	    for(int y = 0; y < bingo_size; y++) {
		for(int x = 0; x < bingo_size; x++) {
		    bingo.add(x, y, nextInt());
		}
		flushLine();
	    }

	    boards.add(bingo);
	    flushLine();
	}

	DEBUG(2, t.split(">input processed"));
	
	int p1_ans = 0;
	//linear search
	outer: for(int round = 0; round < draws_list.size(); round++) {
	    var draws = new HashSet<Integer>();
	    draws.addAll(draws_list.subList(0, round+1));

	    //see if any board satisfies
	    for(var board : boards)
		if(board.check(draws)) {
		    
		    p1_ans = draws_list.get(round) * board.score(draws);
		    break outer;
		}
	}

	DEBUG(2, ("p1 (linear): " + p1_ans));
	DEBUG(2, t.split(">linear search (p1)"));
	
	//binary search
	int left = 0;
	int right = draws_list.size();
	int p1_binary_ans = 0;
	
	while(left < right) {
	    int mid = (left + right) / 2;
	    int round = mid;

	    /* check the condition */
	    boolean match = false;
	    
	    var draws = new HashSet<Integer>();
	    draws.addAll(draws_list.subList(0, round+1));

	    //see if any board satisfies
	    for(var board : boards)
		if(board.check(draws)) {
		    match = true;
		    if(mid +1 >= right)
			p1_binary_ans = draws_list.get(round) * board.score(draws);
		    break;
		}

	    if(match)
		right = mid;
	    else
		left = mid + 1;	
	}

	int last_round = left;
	
	DEBUGF(2, "p1 (binary): ");println(p1_binary_ans);

	DEBUG(2, t.split(">binary search (p1)"));

	/* part two : linear search */

	int round = 0;
	
	outer: for(round = 0; round < draws_list.size(); round++) {
	    var draws = new HashSet<Integer>();
	    draws.addAll(draws_list.subList(0, round+1));
	    
	    boolean match = false;
	    //find the smallest element where every board is correct
	    for(var board : boards)
		if(!board.check(draws)) {
		    match = true;		    
		    break;
		}

	    if(!match) {
		round--;
		break;
	    }
	}

	var draws_2 = new HashSet<Integer>();
	draws_2.addAll(draws_list.subList(0, round+1));
	
	//there should only be one board left
	for(var board : boards)
	    if(!board.check(draws_2)) {
		draws_2.add(draws_list.get(round+1));
		var ans = board.score(draws_2) * draws_list.get(round+1);
		DEBUG(2, "p2 (linear): " + ans);
		break;
	    }
		
	DEBUG(2, t.split(">linear search (p2)"));
	
	/* part two : binary search */	
	left = 0;
	right = draws_list.size();

	int p2_binary_ans = 0;
	round = 0;
	while(left < right) {
	    int mid = (left + right) / 2;
	    round = draws_list.size() - mid;

	    /* check the condition */
	    boolean match = false;
	    
	    var draws = new HashSet<Integer>();
	    draws.addAll(draws_list.subList(0, round+1));

	    //find the smallest element where every board is correct
	    for(var board : boards)
		if(!board.check(draws)) {
		    match = true;		    
		    break;
		}

	    if(match)
		right = mid;
	    else
		left = mid + 1;	
	}

	var draws = new HashSet<Integer>();
	draws.addAll(draws_list.subList(0, round+1));
	
	//there should only be one board left
	for(var board : boards)
	    if(!board.check(draws)) {
		draws.add(draws_list.get(round+1));
		p2_binary_ans = board.score(draws) * draws_list.get(round+1);
		break;
	    }

	DEBUGF(2, "p2 (binary): "); println(p2_binary_ans);
	DEBUG(2, t.split(">binary search (p2)"));
		
        /* visualize output here */
        generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    private class Bingo {
	ArrayList<HashSet<Integer>> rows = new ArrayList<>();
	ArrayList<HashSet<Integer>> cols = new ArrayList<>();

	public Bingo(int bingo_size) {
	    for(int i = 0; i < bingo_size; i++) {
		rows.add(new HashSet<Integer>());
		cols.add(new HashSet<Integer>());
	    }		
	}

	public boolean check(HashSet<Integer> draws) {
	    for(var row : rows)
		if(draws.containsAll(row))
		    return true;

	    for(var col : cols)
		if(draws.containsAll(col))
		    return true;

	    return false;
	}

	public int score(HashSet<Integer> draws) {
	    HashSet<Integer> score = new HashSet<>();
	    for(var row : rows)
		score.addAll(row);

	    score.removeAll(draws);

	    int sum = 0;
	    for(var s : score)
		sum += s;
	    //return sum of score
	    return sum;
	}
	
	public void add(int x, int y, int token) {
	    rows.get(y).add(token);
	    cols.get(x).add(token);	    
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
        new Advent2021_04().run(argv);
    }
}
