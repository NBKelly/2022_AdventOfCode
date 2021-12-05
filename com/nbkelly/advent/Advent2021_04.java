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
import java.util.TreeMap;
import com.nbkelly.drafter.BooleanCommand; //visualize cmd
import com.nbkelly.drafter.IntCommand; //visualize cmd
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

    //set bingo board size
    int bingo_size = 5;

    int p1_round_complete = 0;
    int p2_round_complete = 0;
    Bingo p2_loser = null;

    /* solve problem here */
    @Override public int solveProblem() throws Exception {
	Timer t = makeTimer();

        /* code injected from file */
	var _draws_unfiltered = nextLine().split(",");
	ArrayList<Integer> draws_list = new ArrayList<Integer>();
	for(int i = 0; i < _draws_unfiltered.length; i++)
	    draws_list.add(Integer.parseInt(_draws_unfiltered[i]));
	
	flushLine(1);	
	
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

	/* INPUT PROCESSED */
	DEBUG(2, t.split(">input processed"));

	/* part one: linear search */
	if(GET_DEBUG_LEVEL() >= 2) {
	    int p1_ans = p1_linear(boards, draws_list);
	    DEBUG(2, ("p1 (linear): " + p1_ans));
	    DEBUG(2, t.split(">linear search (p1)"));
	}
	
	/* part one: binary search */
	int p1_binary_ans = p1_binary(boards, draws_list);	
	DEBUGF(2, "p1 (binary): ");println(p1_binary_ans);	
	DEBUG(2, t.split(">binary search (p1)"));

	/* part two : linear search */
	if(GET_DEBUG_LEVEL() >= 2) {
	    int p2_ans = p2_linear(boards, draws_list);
	    DEBUG(2, "p2 (linear): " + p2_ans);
	    DEBUG(2, t.split(">linear search (p2)"));
	}
	
	/* part two : binary search */	
	int p2_binary_ans = p2_binary(boards, draws_list);
	DEBUGF(2, "p2 (binary): "); println(p2_binary_ans);
	DEBUG(2, t.split(">binary search (p2)"));

	/* fast mode with condensations */
	if(GET_DEBUG_LEVEL() >= 2) {
	    solve_fast(boards, draws_list);	
	    DEBUG(2, t.split(">condensed (p1+p2)"));
	}
        /* visualize output here */
        generate_output(boards, bingo_size, draws_list);
	
	return DEBUG(1, t.total());
    }

    public void solve_fast(ArrayList<Bingo> boards, ArrayList<Integer> draws) {
	TreeMap<Integer, Bingo> condensations = new TreeMap<>();

	TreeMap<Integer, Integer> grades = new TreeMap<Integer, Integer>();
	for(int i = 0; i < draws.size(); i++)
	    grades.put(draws.get(i), i);
	
	for(var board : boards) {
	    var con = board.condensation(grades);
	    condensations.put(con, board);
	}

	//get the first item/round
	var first_pair = condensations.firstEntry();
	var first_index = first_pair.getKey();
	var first_draw = draws.get(first_index);	
	var first_valid_draws = new HashSet<Integer>(draws.subList(0, first_index+1));
	var first_score = first_pair.getValue().score(first_valid_draws) * first_draw;
	DEBUGF(2, "Part One (CONDENSATION): %d%n", first_score);

	//get the last item/round
	var last_pair = condensations.lastEntry();
	var last_index = last_pair.getKey();
	var last_draw = draws.get(last_index);
	var last_valid_draws = new HashSet<Integer>(draws.subList(0, last_index+1));
	var last_score = last_pair.getValue().score(last_valid_draws) * last_draw;
	DEBUGF(2, "Part Two (CONDENSATION): %d%n", last_score);
    }
    
    public Integer p2_binary(ArrayList<Bingo> boards, ArrayList<Integer> draws_list) {
	int left = 0;
	int right = draws_list.size();
	
	Bingo last_match = null;
	int last_matching_round = 0;
	while(left < right) {
	    int mid = (left + right) / 2;
	    int round = mid;

	    var draws = new HashSet<Integer>();
	    draws.addAll(draws_list.subList(0, round+1));

	    //see if any board satisfies - we want to find a state where every board is correct
	    var match = boards.parallelStream().filter(board->!board.check(draws))
		.findAny();

	    if(match.isPresent()) {
		last_match = match.get();
		last_matching_round = round +1;
		    
		left = mid+1;
	    }
	    else
		right = mid;	    
	}

	p2_loser = last_match;
	p2_round_complete = last_matching_round;
	var draws = new HashSet<Integer>();
	draws.addAll(draws_list.subList(0, last_matching_round+1));
	return draws_list.get(last_matching_round) * last_match.score(draws);
    }
    
    public Integer p1_binary(ArrayList<Bingo> boards, ArrayList<Integer> draws_list) {
	int left = 0;
	int right = draws_list.size();
	int p1_binary_ans = 0;

	Bingo last_match = null;
	int last_matching_round = 0;
	while(left < right) {
	    int mid = (left + right) / 2;
	    int round = mid;

	    var draws = new HashSet<Integer>();
	    draws.addAll(draws_list.subList(0, round+1));

	    //see if any board satisfies
	    var match = boards.parallelStream().filter(board->board.check(draws))
		.findAny();

	    if(match.isPresent()) {
		last_match = match.get();
		last_matching_round = round;
		    
		right = mid;
	    }
	    else
		left = mid+1;
	}

	p1_round_complete = last_matching_round;
	
	var draws = new HashSet<Integer>();
	draws.addAll(draws_list.subList(0, last_matching_round+1));
	return draws_list.get(last_matching_round) * last_match.score(draws);
    }
    
    public Integer p1_linear(ArrayList<Bingo> boards, ArrayList<Integer> draws_list) {
	for(int round = 0; round < draws_list.size(); round++) {
	    var draws = new HashSet<Integer>();
	    draws.addAll(draws_list.subList(0, round+1));

	    //see if any board satisfies
	    var res = boards.parallelStream().filter(board -> board.check(draws)).findAny();
	    if(res.isPresent()) {
		return draws_list.get(round) * res.get().score(draws);
	    }
	}
	return -1;
    }

    public Integer p2_linear(ArrayList<Bingo> boards, ArrayList<Integer> draws_list) {
	int round = 0;

	Bingo last_unmatched = null;
	for(round = 0; round < draws_list.size(); round++) {
	    var draws = new HashSet<Integer>();
	    draws.addAll(draws_list.subList(0, round+1));
	    
	    var match = boards.parallelStream().filter(board -> !board.check(draws))
		.findAny();
	    
	    if(match.isPresent())
		last_unmatched = match.get();
	    else {
		return last_unmatched.score(draws) * draws_list.get(round);
	    }
	}

	return -1;
    }
    
    private class Bingo {
	public ArrayList<HashSet<Integer>> rows = new ArrayList<>();
	public ArrayList<HashSet<Integer>> cols = new ArrayList<>();

	public Bingo(int bingo_size) {
	    for(int i = 0; i < bingo_size; i++) {
		rows.add(new HashSet<Integer>());
		cols.add(new HashSet<Integer>());
	    }		
	}

	public Integer condensation(TreeMap<Integer, Integer> grades) {
	    Integer best = null;

	    row: for(var row: rows) {
		int highest = 0;
		for(var token : row) {
		    var key = grades.get(token);
		    if(key != null)
			highest = Math.max(key, highest);
		    else
			continue row;
		}

		if(best == null)
		    best = highest;
		else
		    best = Math.min(best, highest);
	    }

	    col: for(var col: cols) {
		int highest = 0;
		for(var token : col) {
		    var key = grades.get(token);
		    if(key != null)
			highest = Math.max(key, highest);
		    else
			continue col;
		}

		if(best == null)
		    best = highest;
		else
		    best = Math.min(best, highest);
	    }


	    return best;
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
    public void generate_output(ArrayList<Bingo> boards, int bingo_size, ArrayList<Integer> draws_list) throws Exception {
    	if(!generate_output)
    	    return;

	/* output goes here */
    	println(">generating output");
	
	int bingo_width = bingo_size*40;
	int height = bingo_size * 12 * 2;

	Image image = new Image(bingo_width*boards.size() + 4, height);

	var draws = new HashSet<Integer>();
	draws.addAll(draws_list.subList(0, p1_round_complete+2));
	
	for(int _board = 0; _board < boards.size(); _board++) {
	    //we know when each round is complete, and who the ultimate loser is
	    //draw every bingo board, and highlight matched values
	    int x_start = 2 + (40*bingo_size*_board);
	    int y_start = 10;

	    var lines = new ArrayList<HashSet<Integer>>();
	    lines.addAll(boards.get(_board).rows);
	    lines.addAll(boards.get(_board).cols);

	    for(int i = 0; i < lines.size(); i++) {
		int y = y_start + (i*10);

		ArrayList<Integer> cline = new ArrayList<Integer>();
		cline.addAll(lines.get(i));

		for(int j = 0; j < cline.size(); j++) {
		    var val = cline.get(j);
		    
		    var color = draws.contains(val) ? Image.W1 : Image.C1;

		    color = draws.containsAll(cline) ? Image.C3 : color;
		    int x = x_start + 30*j;
		    image.text(color, Image.F1, x, y, "" + val);
		}
	    }
	}

	image.savePNG("out1.png");
	println(">out1.png");

	image = new Image(bingo_width*boards.size() + 4, height);
	draws = new HashSet<Integer>();
	draws.addAll(draws_list.subList(0, p2_round_complete+2));
	
	for(int _board = 0; _board < boards.size(); _board++) {
	    //we know when each round is complete, and who the ultimate loser is
	    //draw every bingo board, and highlight matched values
	    int x_start = 2 + (40*bingo_size*_board);
	    int y_start = 10;

	    if(boards.get(_board) == p2_loser)
		image.text(Image.C3, Image.F1, x_start, y_start, "l o s e r");

	    y_start += 12;
	    
	    var lines = new ArrayList<HashSet<Integer>>();
	    lines.addAll(boards.get(_board).rows);
	    lines.addAll(boards.get(_board).cols);

	    for(int i = 0; i < lines.size(); i++) {
		int y = y_start + (i*10);

		ArrayList<Integer> cline = new ArrayList<Integer>();
		cline.addAll(lines.get(i));

		for(int j = 0; j < cline.size(); j++) {
		    var val = cline.get(j);
		    
		    var color = draws.contains(val) ? Image.W1 : Image.C1;
		    color = draws.containsAll(cline) ? Image.C3 : color;
		    
		    int x = x_start + 30*j;
		    image.text(color, Image.F1, x, y, "" + val);
		}
	    }
	}

	image.savePNG("out2.png");
	println(">out2.png");
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

	/* bingo size */
	IntCommand bs = new IntCommand(5, 50, false, 5, "--bingo-size", "--board-size");
        return new Command[]{fc, vc, bs};
        
        
    }
    
    /* act after commands processed - userCommands stores all the commands set in setCommands */
    @Override public int actOnCommands(Command[] userCommands) throws Exception {
	//do whatever you want based on the commands you have given
	//at this stage, they should all be resolved
        /* code injected from file */
        lines = readFileLines(((FileCommand)userCommands[0]).getValue());
        setSource(((FileCommand)userCommands[0]).getValue());
        
        generate_output = ((BooleanCommand)userCommands[1]).matched();

	bingo_size = ((IntCommand)userCommands[2]).getValue();
	return 0;
    }

    /**
     * Creates and runs an instance of your class - do not modify
     */
    public static void main(String[] argv) {
        new Advent2021_04().run(argv);
    }
}
