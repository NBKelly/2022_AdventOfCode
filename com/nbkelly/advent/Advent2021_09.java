package com.nbkelly.advent;

/* imports */
import com.nbkelly.drafter.Drafter;
import com.nbkelly.drafter.Command;
import com.nbkelly.drafter.FileCommand;
import com.nbkelly.drafter.Timer;

/* imports from file */
import java.util.ArrayList;

/* my imported libs */
import com.nbkelly.lib.Util;
import com.nbkelly.drafter.BooleanCommand; //visualize cmd
import com.nbkelly.lib.Image; //visualizer lib
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import com.nbkelly.lib.IntPair;
import com.nbkelly.lib.pathfinder.Map;
import java.util.stream.Collectors;

import com.nbkelly.lib.HashedDiGraph;

/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2021_09 extends Drafter {
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

	int _y = 0;
	HashMap<IntPair, Integer> space = new HashMap<>();
	int len = 0;
	int height = 0;
	for(var line : lines) {
	    len = line.length();
	    for(int i = 0; i < len; i++)
		space.put(new IntPair(i, _y), line.charAt(i) - '0');
	    _y++;

	    if(_y % 50 == 0)
		DEBUGF(3, "LINE %d READ%n", _y);
	}

	height = _y;
	
	int sum_danger = 0;
	ArrayList<IntPair> low_points = new ArrayList<IntPair>();
	for(int y = 0; y < height; y++) {
	    for(int x = 0; x < len; x++) {
		IntPair loc = new IntPair(x, y);
		if(is_danger(loc, space)) {
		    sum_danger += 1 + space.get(loc);
		    low_points.add(loc);
		}
	    }
	    if(y % 50 == 0)
		DEBUGF(3, "LINE %d CHECKED (p1)%n", y);
	}

	DEBUGF(1, "PART ONE: "); println(sum_danger);
	
	
	var score = find_basins(space, low_points);
        
        DEBUGF(1, "PART TWO: "); println(score); 
        
        /* visualize output here */
        generate_output(len, height, 5, space);
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    public Integer find_basins(HashMap<IntPair, Integer> space, ArrayList<IntPair> lows) {
	HashMap<IntPair, Integer> pool_size = new HashMap<IntPair, Integer>();

	HashSet<IntPair> lows_set = new HashSet<IntPair>();
	lows_set.addAll(lows);

	HashMap<IntPair, Integer> best = new HashMap<IntPair, Integer>();

	HashSet<IntPair> visited = new HashSet<>();

	for(var pair : lows) {
	    DEBUGF(3, "FIND BASIN FOR PAIR %s%n", pair);
	    //for(var pair : space.keySet()) {
	    if(space.get(pair) != 9 && !visited.contains(pair)) {
		/* find the pool size for this time */
		var pool = find_basin(space, pair);
		var diff = new TreeSet<>(lows);
		diff.retainAll(pool);

		if(diff.size() != 1) {
		    println("diff size too large");
		    continue;
		}

		var best_prev = best.get(diff.first());
		if(best_prev != null && best_prev > pool.size())
		    continue;

		best.put(new ArrayList<>(diff).get(0), pool.size());
		visited.addAll(pool);
	    }
	}
	
	//println("pools: " + best);

	ArrayList<Integer> pools_score = new ArrayList<Integer>();

	for(var entry : best.entrySet())
	    pools_score.add(entry.getValue());

	pools_score.sort((Integer left, Integer right) -> right - left);

	var score = 1;
	for(int i = 0; i < 3; i++)
	    score *= pools_score.get(i);

	return score;
    }

    public HashSet<IntPair> find_basin(HashMap<IntPair, Integer> space, IntPair start) {
	//make a considered set
	//find all points adjacent to the considered set which are not part of it
	//if any of these points are lower than thefind the smallest point higher than the considered
	//for each of these points, floodfill down : if they reach a node which is lower than/
	int height = 8;//space.get(start);

	HashSet<IntPair> visited = new HashSet<IntPair>();
	TreeSet<IntPair> considered = new TreeSet<IntPair>();

	considered.add(start);
	
	while(considered.size() > 0) {
	    var pair = considered.pollFirst();
	    var h = space.get(pair);

	    if(visited.contains(pair))
		continue;
	    
	    if(h == null)
		continue;

	    if(h > height)
		continue;

	    visited.add(pair);

	    var neighbors = Map.OrthogonalNeighbors(pair);
	    considered.addAll(neighbors);	    
	}

	//println(start + "   -   pool size: " + visited.size());

	return visited;
    }
    
    public boolean is_danger(IntPair loc, HashMap<IntPair, Integer> space) {
	int score = space.get(loc);
	var neighbors = Map.OrthogonalNeighbors(loc).stream()
	    .filter(key -> space.containsKey(key)).collect(Collectors.toList());

	for(var neighbor : neighbors)
	    if(space.get(neighbor) <= score)
		return false;
	
	return true;
    }

    /* code injected from file */
    public void generate_output(int width, int height,
				int scale, HashMap<IntPair, Integer> space) throws Exception {
    	if(!generate_output)
    	    return;
    	
    	println(">generating output");

	/* output goes here */
	Image i = new Image((2+width)*scale, (2+height)*scale);
	
	for(int y = 0; y < height; y++)
	    for(int x = 0; x < width; x++) {
		switch(space.get(new IntPair(x, y))) {		    
		    /*case 9:
		    i.rect(Image.C3, (1+x)*scale, (1+y)*scale, scale, scale);
		    break;*/
		case 8:
		case 7:
		    i.rect(Image.C3, (1+x)*scale, (1+y)*scale, scale, scale);
		    break;
		case 6:
		case 5:
		case 4:
		    i.rect(Image.C4, (1+x)*scale, (1+y)*scale, scale, scale);
		    break;
		    
		case 3:
		case 2:
		    i.rect(Image.C2, (1+x)*scale, (1+y)*scale, scale, scale);
		    break;
		case 1:
		    i.rect(Image.C1, (1+x)*scale, (1+y)*scale, scale, scale);
		    break;
		}
	    }

	i.savePNG("out.png");
	println(">out.png");
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
        new Advent2021_09().run(argv);
    }
}
