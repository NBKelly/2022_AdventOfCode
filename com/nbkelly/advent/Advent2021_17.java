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

import java.math.BigInteger;

/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2021_17 extends Drafter {
    /* WORKFLOW:
     *  set all needed commands with setCommands()
     *  post-processing can be performed with actOnCommands()
     *  the rest of your work should be based around the solveProblem() function
     */
    
    /* params injected from file */
    ArrayList<String> lines;
    
    //generate output
    boolean generate_output = false;

    private void put(HashMap<Long, HashSet<Long>> map, Long key, Long value) {
	if(!map.containsKey(key))
	    map.put(key, new HashSet<>());
	map.get(key).add(value);
    }
    
    /* solve problem here */
    @Override public int solveProblem() throws Exception {
	Timer t = makeTimer();

	var line = lines.get(0).substring(15).split(", y=");
	DEBUG(2, line[0]);
	DEBUG(2, line[1]);

	long min_x = Integer.parseInt(line[0].split("\\.\\.")[0]);
	long max_x = Integer.parseInt(line[0].split("\\.\\.")[1]);

	long min_y = Integer.parseInt(line[1].split("\\.\\.")[0]);
	long max_y = Integer.parseInt(line[1].split("\\.\\.")[1]);

	DEBUGF(2, "(%d, %d) -> (%d, %d)%n", min_x, min_y, max_x, max_y);
        /* code injected from file */

	HashMap<Long, Pair<Long, Long>> y_ranges = new HashMap<>();
	
	/* solve explicitly for x/y independently */
	for(long init_y = min_y; init_y < (-1 * (min_y -2)); init_y++) {
	    long vel_y = init_y;
	    long y = 0;
	    long step = 0;
	    Long open = null;

	    if(_DEBUG_LEVEL >= 3)
		DEBUG("Y vel: " + init_y);
	    
	    /* skip the entire upwards ascent, where possible */
	    if(vel_y > 0 && max_y < 0) {
		step = 2l*vel_y;
		vel_y *= -1l;
	    }		

	    /* so long as y is above the box, or is ascending */
	    while(y >= min_y || vel_y > 0) {

		/* if the box is above the start, we may need to do some skipping again */
		if(y > max_y && vel_y > 0) {
		    step *= 2l*vel_y;
		    vel_y *= -1l;
		}

		/* step through one token at a time */
		y = step(y, vel_y);
		vel_y -= 1;
		step++;

		/* save values that collide */
		if(collides(y, min_y, max_y))
		    if(open == null)
			open = step;
	    }

	    if(open != null)
		y_ranges.put(init_y, new Pair<Long, Long>(open, step));	    
	}

	HashSet<Pair<Long, Long>> valid_velocities = new HashSet<>();

	int valid_y_ct = 0;
	for(long init_x_vel = Math.min(0, min_x-1); init_x_vel <= Math.max(max_x, 0); init_x_vel++) {
	    /* track every value until it stops */
	    long x_vel = init_x_vel;
	    
	    long step = 0;
	    long x = 0;
	    
	    HashSet<Long> validY = new HashSet<>();
	    
	    if((init_x_vel * (init_x_vel + 1)) / 2 < (min_x - 5))
		continue;

	    if(_DEBUG_LEVEL >= 3)
		DEBUG("X vel: " + init_x_vel);
	    

	    Long open = null;
	    while(x_vel > 0l) {
		x = step(x, x_vel);
		if(x_vel > 0)
		    x_vel -= 1;
		
		step++;
		
		if(collides(x, min_x, max_x))
		    for(var entry : y_ranges.entrySet()) {
			var pair = entry.getValue();
			if(step >= pair.X && step <= pair.Y)			    
			    validY.add(entry.getKey());
		    }
	    }
	    
	    /* stalled */
	    if(collides(x, min_x, max_x)) {
		for(var entry : y_ranges.entrySet()) {
		    var pair = entry.getValue();
			if(step <= pair.X)			    
			    validY.add(entry.getKey());
		}
	    }

	    for(var y : validY) {
		valid_velocities.add(new Pair<Long, Long>(init_x_vel, y));
		valid_y_ct++;
	    }
	}

        println(">Good Morning!");

	int confirmed = 0;
	Long max_height = 0l;
	
	for(var vel : valid_velocities) {
	    var check = checkVel(vel.X, vel.Y,min_x, max_x,min_y, max_y);
	    if(check != null) {
		confirmed++;
		max_height = Math.max(max_height, check);
	    }
	}

	
	println("confirmed: " + confirmed);
	println("rejected:  " + (valid_velocities.size() - confirmed));
	
        DEBUGF(1, "PART ONE: "); println(max_height); //todo
        DEBUGF(1, "PART TWO: "); println(confirmed);//todo
        
        /* visualize output here */
        generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    /* returns the max height achieved during a run, or null if the run was not valid */
    public Long checkVel(long vel_x, long vel_y,
			 long min_x, long max_x,
			 long min_y, long max_y) {
	long x = 0;
	long y = 0;

	long max_height = 0;
	
	for(int iteration = 0; iteration < 30000 && x <= max_x && y >= min_y; iteration++) {
	    x = step(x, vel_x);
	    y = step(y, vel_y);
	    if(vel_x > 0)
		vel_x -= 1;
	    vel_y -= 1;

	    max_height = Math.max(y, max_height);
	    
	    if(collides(x, y, min_x, max_x, min_y, max_y))
		return max_height;
	}

	return null;
    }

    public boolean collides(long val, long min, long max) {
	return val >= Math.min(min, max) && val <= Math.max(min, max);
    }
            
    public boolean collides(long x, long y, long min_x, long max_x, long min_y, long max_y) {
	return x <= max_x && x >= min_x && y <= max_y && y >= min_y;
    }

    public long step(long x, long vel) {
	return x + vel;
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
        new Advent2021_17().run(argv);
    }
}
