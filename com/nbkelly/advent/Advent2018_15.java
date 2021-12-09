package com.nbkelly.advent;

/* imports */
import com.nbkelly.drafter.Drafter;
import com.nbkelly.drafter.Command;
import com.nbkelly.drafter.FileCommand;
import com.nbkelly.drafter.Timer;

/* imports from file */
import java.util.ArrayList;
import java.util.TreeSet;

/* my imported libs */
import com.nbkelly.lib.Util;
import com.nbkelly.drafter.BooleanCommand; //visualize cmd
import com.nbkelly.lib.Image; //visualizer lib
import com.nbkelly.lib.pathfinder.*;
import com.nbkelly.lib.IntPair;
import java.util.stream.Collectors;
import java.util.TreeMap;


/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2018_15 extends Drafter {
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
	ArrayList<String> geo = new ArrayList<String>();	
	ArrayList<Unit> default_units = new ArrayList<Unit>();
	ArrayList<Unit> units = new ArrayList<Unit>();
	int def_a = 3;
	int def_hp = 200;
	
	int y = 0;

	for(var line : lines) {
	    var geo_line = line.replaceAll("G|E", ".");
	    geo.add(geo_line);
	    DEBUG(1, "geo " + geo_line);

	    for(int x = 0; x < line.length(); x++) {
		if(line.charAt(x) == 'G') {		    
		    default_units.add(new Goblin(new IntPair(x, y), def_hp, def_a));
		    units.add(new Goblin(new IntPair(x, y), def_hp, def_a));
		}
		else if (line.charAt(x) == 'E') {
		    default_units.add(new Elf(new IntPair(x, y), def_hp, def_a));
		    units.add(new Elf(new IntPair(x, y), def_hp, def_a));
		}
	    }

	    y++;
	}

	var map = new Map(geo);
	var turn = run_combat(units, map);
	
	//get the remaining score
	int remaining_hp = 0;
	for(var unit : units)
	    if(unit.alive())
		remaining_hp += unit.HP;
	
        DEBUG(2, "HP remaining: " + remaining_hp);
        DEBUGF(1, "PART ONE: "); println(remaining_hp * turn);//todo

	/* now we want to find the lowest integer (between 4 and 200) where not a single elf dies */
	var num_elves = default_units.stream().filter(unit -> unit instanceof Elf).count();

	DEBUG(2, "number of elves: " + num_elves);

	/* do a binary search on the solution space */

	//min_power
	var left = 3;
	//max_power
	var right = 200;

	int last_casualties = 0;

	while(left < right) {
	    int mid = (left + right)/2;
	    int pow = mid;
	    /* check if all elves survive with left */
	    units = new ArrayList<Unit>();
	    for(var unit : default_units)
		if(unit instanceof Elf)
		    units.add(new Elf(unit.location, 200, pow));
		else
		    units.add(new Goblin(unit.location, 200, 3));

	    //println(units);
	    /* run the simulation */
	    DEBUGF(2, "Running sim for elves with %d attack%n", pow);

	    turn = run_combat(units, map);
	    
	    /* see if any elves died */
	    var num_alive_elves = units.stream().filter(unit->
							(unit instanceof Elf) && unit.alive())
		.count();

	    var casualties = num_elves - num_alive_elves;
	    DEBUGF(2, "*** Turn: %d Casualties: %d *** %n", turn, casualties);

	    if(casualties == 0)
		right = mid;
	    else
		left = mid + 1;
	}

	
	/* re-solve the best preforming metric */
	DEBUG(2, "last: " + left);
	int pow = left;
	units = new ArrayList<Unit>();
	for(var unit : default_units)
	    if(unit instanceof Elf)
		units.add(new Elf(unit.location, 200, pow));
	    else
		units.add(new Goblin(unit.location, 200, 3));

	turn = run_combat(units, map);
	remaining_hp = 0;
	for(var unit : units)
	    if(unit.alive())
		remaining_hp += unit.HP;
	
        DEBUGF(1, "PART TWO: "); println(remaining_hp * turn); //todo
        
        /* visualize output here */
        generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    private Integer run_combat(ArrayList<Unit> original_units, Map map) {
	var units = new ArrayList<Unit>(original_units);
	units.addAll(original_units);

	/* define the turn order */
	TreeSet<Unit> turn_order
	    = new TreeSet<Unit>((Unit left, Unit right) ->
				Util.compareTo(left.location.Y, right.location.Y,
					       left.location.X, right.location.X));
	turn_order.addAll(units);
	
	int turn = 0;
	boolean acted = true;
	while(acted) {
	    /* mark the locations of all units */
	    acted = false;
	    TreeMap<IntPair, Unit> occupied = new TreeMap<IntPair, Unit>();
	    for(var unit : turn_order)
		if(unit.alive())
		    occupied.put(unit.location, unit);

	    boolean ended_early = false;
	    while(turn_order.size() > 0) {		
		/* if only goblins or only elves are alive, end the round */
		boolean elves_alive = false;
		boolean goblins_alive = false;
		for(var unit : units)
		    if(unit.alive())
			if(unit instanceof Elf)
			    elves_alive = true;
			else
			    goblins_alive = true;

		if(!elves_alive || !goblins_alive) {
		    ended_early = true;
		    break;
		}
		
		var active_unit = turn_order.pollFirst();

		/* check unit is alive */
		if(!active_unit.alive())
		    continue;

		/* see if the unit needs to move */
		var neighbors = map.OrthogonalNeighbors(active_unit.location);
		boolean needs_to_move = true;
		boolean moved = false;
		for(var neighbor : neighbors) {
		    var target = occupied.get(neighbor);
		    if(target != null && (active_unit instanceof Elf) ^ (target instanceof Elf))
			needs_to_move = false;
		}
		
		if(needs_to_move)
		    acted |= moved = perform_move(active_unit, units, occupied, map);

		/* see if a valid attack exists */
		neighbors = map.OrthogonalNeighbors(active_unit.location);
		var targets = new ArrayList<Unit>();
		for(var neighbor : neighbors) {
		    var target = occupied.get(neighbor);
		    if(target != null && (active_unit instanceof Elf) ^ (target instanceof Elf))
			targets.add(target);
		}
		
		targets.sort((Unit left, Unit right) ->
			     Util.compareTo(left.HP, right.HP,
					    left.location.Y, right.location.Y,
					    left.location.X, right.location.X));

		/* attack the target */
		if(targets.size() != 0) {
		    var target = targets.get(0);
		    active_unit.attack(target);
		    if(!target.alive()) {
			occupied.remove(target.location);
		    }

		    acted = true;	
		}		
	    }

	    //select all living units and run it back
	    if(ended_early)
		break;
	    
	    /* reset the turn order */
	    turn_order
		= new TreeSet<Unit>((Unit left, Unit right) ->
				    Util.compareTo(left.location.Y, right.location.Y,
						   left.location.X, right.location.X));

 	    for(var unit : units)
		if(unit.alive())
		    turn_order.add(unit);

	    if(acted)
		turn++;
	}

	return turn;
    }

    private boolean perform_move(Unit active_unit, ArrayList<Unit> units,
			      TreeMap<IntPair, Unit> occupied, Map map) {
	/* select targets */
	var targets = new ArrayList<Unit>();
	if(active_unit instanceof Elf)
	    targets.addAll(units.stream()
			   .filter(unit -> (unit instanceof Goblin) && unit.alive())
			   .collect(Collectors.toList()));
	else //target instanceof goblin
	    targets.addAll(units.stream()
			   .filter(unit -> (unit instanceof Elf) && unit.alive())
			   .collect(Collectors.toList()));
	
	
	/* get target spaces */
	var spaces = new ArrayList<IntPair>();
	
	for(var target : targets) {
	    var adjacent = Map.OrthogonalNeighbors(target.location);
	    for(var position : adjacent)
		if(map.pathable(position.X, position.Y,
				x -> (x != '#') && occupied.get(position) == null)) {
		    spaces.add(position);
		}		
	}
	
	//println("Valid spaces: " + spaces);
	
	/* filter to only reachable spaces with the best distance */
	var reachable = new ArrayList<IntPair>();
	Integer best_dist = null;
	for(var space : spaces) {
	    var dist = map.distance_orthogonal(active_unit.location, space,
					       c -> (c != '#'),
					       loc -> Map.OrthogonalNeighbors(loc)
					       .stream()
					       .filter(x ->
						       x.equals(active_unit.location)
						       || occupied.get(x) == null)
					       .collect(Collectors.toList()));
	    if(best_dist == null) {
		if(dist != null) {
		    best_dist = dist;
		    reachable.add(space);
		}
		continue;
	    }
	    
	    if(dist != null && dist < best_dist) {
		reachable.clear();
		reachable.add(space);
		best_dist = dist;
	    }
	    else if (dist == best_dist)
		reachable.add(space);
	}
	
	if(reachable.size() == 0)
	    return false;
	
	/* sort reachable by reading order */
	reachable.sort((IntPair left, IntPair right) ->
		       Util.compareTo(left.Y, right.Y,
				      left.X, right.X));
	
	final Integer target_dist = best_dist - 1;
		
	/* pick the best one by reading order, and find the best move that gets there */
	var possible_moves = map.OrthogonalNeighbors(active_unit.location)
	    .stream()
	    .filter(start -> occupied.get(start) == null
		    && map.distance_orthogonal(start, reachable.get(0),
					       c -> (c != '#'),
					       x -> Map.OrthogonalNeighbors(x)
					       .stream()
					       .filter(z -> occupied.get(z) == null)
					       .collect(Collectors.toList()))
		    == target_dist)
	    .collect(Collectors.toList());

	/* sort possible moves by reading order */
	possible_moves.sort((IntPair left, IntPair right) ->
			    Util.compareTo(left.Y, right.Y,
					   left.X, right.X));
	//println("best reachable spaces: " + reachable);
		
	//println("best moves:" + possible_moves);

	/* execute best move */
	var best_move = possible_moves.get(0);
	occupied.remove(active_unit.location);
	active_unit.location = best_move;
	occupied.put(best_move, active_unit);

	return true;
    }

    class Unit {
	IntPair location = null;
	int HP;
	int attack;	

	public void attack(Unit unit) {
	    unit.HP -= this.attack;
	}
	
	public char token() {
	    return '?';
	}
	
	protected String type = "unit";

	boolean alive() {
	    return HP > 0;
	}

	public String toString() {
	    return String.format("{type = %s, location = %s, HP = %d, attack = %d}",
				 type, location, HP, attack);
	}
    }

    class Elf extends Unit {

	@Override public char token() {
	    return 'E';
	}
	
	public Elf(IntPair location, int hp, int attack) {
	    this.location = location;
	    this.HP = hp;
	    this.attack = attack;
	    this.type = "Elf";
	}
    }
    
    class Goblin extends Unit {
	@Override public char token() {
	    return 'G';
	}
	
	public Goblin(IntPair location, int hp, int attack) {
	    this.location = location;
	    this.HP = hp;
	    this.attack = attack;
	    this.type = "Goblin";
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
        new Advent2018_15().run(argv);
    }
}
