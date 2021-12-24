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
import java.util.stream.Collectors;
import java.math.BigInteger;


/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2021_23 extends Drafter {
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
	
	String insert_line_0 = "  #D#C#B#A#  ";
	String insert_line_2 = "  #D#B#A#C#  ";			

        println(">Good Morning!");

	var lines_2 = new ArrayList<String>();
	lines_2.addAll(lines.subList(0, 3));
	lines_2.add(insert_line_0);
	lines_2.add(insert_line_2);
	lines_2.addAll(lines.subList(3, lines.size()));

	var p1_ans = solve(lines, false);
	var p2_ans = solve(lines_2, false);
	
        DEBUGF(1, "PART ONE: "); println(p1_ans); //todo
        DEBUGF(1, "PART TWO: "); println(p2_ans); //todo
        
        /* visualize output here */
        generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    public Integer solve(ArrayList<String> starting_state_str, boolean use_memo) {
	var lines = new ArrayList<String>(starting_state_str);
	lines.set(1, "#OO.O.O.O.OO#");

	Map map = new Map(lines);

	for(int i = 2; i + 1 < lines.size(); i++)
	    lines.set(i, "  #A#B#C#D#  ");

	Map target_map = new Map(lines);

	DEBUG(1, "Initial State:");
	DEBUG(1, map.format(new IntPair(-1, -1), new IntPair(13, lines.size())));
	DEBUG(1, target_map.format(new IntPair(-1, -1), new IntPair(13, lines.size())));

	/* locations we can move out to from a hallway */	
	var out_locations = map.findAll('O');
	TreeSet<Token> state_tokens = new TreeSet<Token>();
	map.findAll('A').stream().forEach(pair -> state_tokens.add(new Token(pair, 'A', 1)));
	map.findAll('B').stream().forEach(pair -> state_tokens.add(new Token(pair, 'B', 10)));
	map.findAll('C').stream().forEach(pair -> state_tokens.add(new Token(pair, 'C', 100)));
	map.findAll('D').stream().forEach(pair -> state_tokens.add(new Token(pair, 'D', 1000)));

	/* find all the target locations */
	TreeSet<Token> targets = new TreeSet<Token>();
	target_map.findAll('A').stream().forEach(pair -> targets.add(new Token(pair, 'A', 1)));
	target_map.findAll('B').stream().forEach(pair -> targets.add(new Token(pair, 'B', 10)));
	target_map.findAll('C').stream().forEach(pair -> targets.add(new Token(pair, 'C', 100)));
	target_map.findAll('D').stream().forEach(pair -> targets.add(new Token(pair, 'D', 1000)));
	
	if(!use_memo)
	    return find_solution_bfs(new State(state_tokens, 0), targets, out_locations,
				     map, target_map);

	return find_solution_memo(new State(state_tokens, 0), targets, out_locations,
				  map, target_map, new HashMap<>(), 0);
    }

    public ArrayList<State> move_in(State state, Map map, Map target_map) {
	ArrayList<State> res = new ArrayList<State>();

	outer: for(var token : state.tokens) {
	    if(token.location.Y > 1 || token.num_moves != 1)
		continue;

	    /* find out the set of occupied states */
	    var occupied = occupied(state.tokens, token);

	    /* find out what states are potential targets */
	    var targets_list = target_map.findAll(token.icon);

	    /* see if any of these are inhabited by another token */
	    for(var target : targets_list) {
		var inhabitant = inhabitant(target, state.tokens);
		if(inhabitant != null &&
		   inhabitant.icon != token.icon)
		    /* there can be no valid move */
		    continue outer;
	    }
	    
	    var targets = targets_list
		.stream()
		.filter(pair -> !occupied.contains(pair) && pair != token.location)
		.collect(Collectors.toList());

	    /* only take the final one */
	    targets.sort((left, right) -> right.compareTo(left)); //Collections.sort(targets);

	    var loc = targets.get(0);
	    var dist = map.distance_orthogonal(token.location.X, token.location.Y,
							   loc.X, loc.Y,
							   ch -> pathable(ch),
							   tile -> neighbors(tile, occupied));

	    if(dist != null) {
		var cost = token.cost(dist);
		var new_token = token.move(loc);
		
		TreeSet<Token> new_set = new TreeSet<>(state.tokens);
		new_set.remove(token);
		new_set.add(new_token);
		State new_state = new State(new_set, cost + state.energy_cost);
		res.add(new_state);
	    }	    
	}
	
	return res;
    }

    public Token inhabitant(IntPair location, TreeSet<Token> tokens) {
	for(var token : tokens)
	    if(token.location.equals(location))
		return token;
	return null;
    }
    
    public ArrayList<State> move_out(State state, TreeSet<IntPair> out_locations, Map map) {
	ArrayList<State> res = new ArrayList<State>();
	
	for(var token : state.tokens) {
	    if(token.location.Y == 1 || token.num_moves != 0)
		continue;

	    /* find out what states are occupied */
	    var occupied = occupied(state.tokens, token);

	    /* find out what states are potential targets */
	    var targets = out_locations.stream()
		.filter(pair -> !occupied.contains(pair)
			&& !pair.equals(token.location))
		.collect(Collectors.toList());

	    var newStates = targets.parallelStream()
		.map(loc -> {
			var dist = map.distance_orthogonal(token.location.X, token.location.Y,
							   loc.X, loc.Y,
							   ch -> pathable(ch),
							   tile -> neighbors(tile, occupied));

			if(dist != null) {
			    var cost = token.cost(dist);
			    var new_token = token.move(loc);
			    
			    TreeSet<Token> new_set = new TreeSet<>(state.tokens);
			    new_set.remove(token);
			    new_set.add(new_token);
			    State new_state = new State(new_set, cost + state.energy_cost);
			    return new_state;
			}

			return null;
		    })
		.filter(s -> s != null)
		.collect(Collectors.toList());

	    res.addAll(newStates);
	
	}
	
	return res;
    }

    int memo_iteration = 0;

    int min_winner = 999999;
    public Integer find_solution_memo(State starting_state, TreeSet<Token> targets,
				      TreeSet<IntPair> out_locations, Map map,
				      Map target_map, HashMap<State, Integer> cost,
				      int depth) {
	if(depth == 0)
	    min_winner = 999999;
	
	if(cost.containsKey(starting_state))	    
	    return cost.get(starting_state);

	if(starting_state.energy_cost >= min_winner)
	    return null;
	
	if(starting_state.solved(targets)) {
	    min_winner = Math.min(min_winner, starting_state.energy_cost);
	    DEBUG(2, "min energy: " + min_winner);
	    return starting_state.energy_cost;
	}	

	memo_iteration++;
	if(memo_iteration%1000 == 0)	    
	    DEBUGF(2, "DEPTH = %d ITERATION = %d%n", depth, memo_iteration);
	
	Integer shortest = null;

	var out_movements = move_out(starting_state, out_locations, map);

	for(var movement : out_movements) {	    
	    var tmp = find_solution_memo(movement, targets, out_locations, map,
					 target_map, cost, depth+1);

	    if(tmp == null)
		continue;

	    shortest = (shortest == null ? tmp : Math.min(tmp, shortest));	    
	}

	var in_movements = move_in(starting_state, map, target_map);

	for(var movement : in_movements) {
	    var tmp = find_solution_memo(movement, targets, out_locations, map,
					 target_map, cost, depth+1);

	    if(tmp == null)
		continue;

	    shortest = (shortest == null ? tmp : Math.min(tmp, shortest));
	}
	in_movements = null;
	
	
	cost.put(starting_state, shortest);
	//println("dist found: " + shortest);
	
	return shortest;
    }
    
    public Integer find_solution_bfs(State starting_state, TreeSet<Token> targets,
				 TreeSet<IntPair> out_locations, Map map,
				 Map target_map) {
	TreeSet<State> current = new TreeSet<State>();
	HashMap<State, Integer> best = new HashMap<State, Integer>();

	current.add(starting_state);
	int iteration = 0;
	while(current.size() > 0) {
	    var active_state = current.pollFirst();
	    
	    iteration++;
	    if(iteration%10000 == 0)
		DEBUGF(2, "ITERATION: %d STATES: %d ENERGY: %d%n",
		       iteration, current.size(), active_state.energy_cost);
	    
	    /* prune */
	    var old_score = best.get(active_state);
	    if(old_score != null && old_score <= active_state.energy_cost) {
		//println("pruned");
		continue;		
	    }
	    
	    best.put(active_state, active_state.energy_cost);
	    
	    /* check if solved */
	    if(active_state.solved(targets))
		return active_state.energy_cost;

	    var out_movements = move_out(active_state, out_locations, map);
	    var in_movements = move_in(active_state, map, target_map);

	    /* add new */
	    current.addAll(out_movements);
	    current.addAll(in_movements);
	}

	return -1;
    }

    /* use for map finding lambda */
    public boolean pathable(char c) { return c != '#'; }

    public ArrayList<IntPair> neighbors(IntPair location, TreeSet<IntPair> occupied) {
	var li =  Map.OrthogonalNeighbors(location)
	.stream().filter(loc -> !occupied.contains(loc))
	.collect(Collectors.toList());

	return new ArrayList<>(li);
    }
    
    public TreeSet<IntPair> occupied(TreeSet<Token> tokens) {
	return new TreeSet<>(tokens.stream()
			     .map(x -> x.location)
			     .collect(Collectors.toList()));
    }

    public TreeSet<IntPair> occupied(TreeSet<Token> tokens, Token exclude) {
	return new TreeSet<>(tokens.stream()
			     .map(x -> x.location)
			     .filter(loc -> !loc.equals(exclude.location))
			     .collect(Collectors.toList()));
    }
    
    
    static int __ID = 0;    
    
    private class State implements Comparable<State> {
	/* id ensures every state is unique for comparison, but still sortable */
	int id = ++__ID;

	int energy_cost;
	TreeSet<Token> tokens = new TreeSet<Token>();

	public boolean solved(TreeSet<Token> targets) {
	    int matched = 0;
	    outer: for(var rhs : targets) {
		for(var lhs : tokens)
		    if(lhs.equals_moveless(rhs)) {
			matched++;
			continue outer;			
		    }		
		return false;
	    }
	
	    return true;
	}
	
	public State(TreeSet<Token> tokens, int energy) {
	    this.tokens.addAll(tokens);
	    this.energy_cost = energy;
	}
	
	public int compareTo(State s) { return Util.compareTo(energy_cost, s.energy_cost,id, s.id); }
	@Override public int hashCode() { return tokens.hashCode(); }
	public boolean equals(Object o) { return (o instanceof State) ? equals((State)o) : false; }
	public boolean equals(State s) { return tokens.equals(s.tokens); }
    }

    private class Token implements Comparable<Token> {
	IntPair location;
	char icon;
	int weight;
	int num_moves;
	public Token(IntPair location, char icon, int weight) {
	    this.location = location;
	    this.icon = icon;
	    this.weight = weight;
	}

	public Token(IntPair location, char icon, int weight, int moves) {
	    this.location = location;
	    this.icon = icon;
	    this.weight = weight;
	    this.num_moves = moves;
	}

	
	public Token move(IntPair destination) {
	    return new Token(destination, icon, weight, num_moves+1);
	}

	public Integer cost(int distance) { return weight * distance; }

	public boolean equals_moveless(Token t) {
	    return t.icon == icon &&
		t.location.equals(location);		
	}
	
	public int compareTo(Token t) {
	    return Util.compareTo(location, t.location,
				  icon, t.icon,
				  weight, t.weight,
				  num_moves, t.num_moves);
	}

	public boolean equals(Object o) {
	    if(o instanceof Token)
		return compareTo((Token)o) == 0;
	    return false;
	}
	
	@Override public int hashCode() { return 100*location.hashCode() + icon*4 + num_moves; }
	
	public String toString() {
	    return String.format("%c: %s - weight=%d", icon, location, weight);
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
        new Advent2021_23().run(argv);
    }
}
