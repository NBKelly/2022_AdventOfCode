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
import com.nbkelly.lib.IntPair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.nbkelly.lib.pathfinder.Map;

/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2021_15 extends Drafter {
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
	Map map = new Map(null);

	int sx = 0;
	int sy = 0;

	int fx = rx = lines.get(0).length() - 1;
	int fy = ry = lines.size() - 1;

	var p1_cost = map.cost_orthogonal(sx, sy, fx, fy,
					  c -> Character.isDigit(c),
					  x -> Map.OrthogonalNeighbors(x),
					  x -> (x - '0'),
					  x -> get_char(lines, x.X, x.Y, 0));	
	fx = rx = fx*5 + 4; 
	fy = ry = fy*5 + 4; 

	/* track times in debug mode */
	best_dist = 0;	
	var p2_cost = map.cost_orthogonal(sx, sy, fx, fy,
					  c -> Character.isDigit(c),
					  x -> Map.OrthogonalNeighbors(x),
					  x -> (x - '0'),
					  x -> get_char(lines, x.X, x.Y, 4));
	
        DEBUGF(1, "PART ONE: "); println(p1_cost); 
        DEBUGF(1, "PART TWO: "); println(p2_cost);
        
        /* visualize output here */
        generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    int best_dist = 0;
    int rx = 0;
    int ry = 0;
    
    public Character get_char(ArrayList<String> lines, int x, int y,
			      int rot_lim) {
	if(x+y > best_dist && (x != rx || y != ry)) {
	    best_dist = x+y;	    
	    DEBUGF(2, "BEST DIST: %d%n", best_dist);
	}
	
	if(x < 0 || y < 0)
	    return null;
	
	//calculate displacement
	var rot_x =  x / lines.get(0).length();
	var rot_y =  y / lines.size();

	if(rot_x > rot_lim || rot_y > rot_lim)
	    return null;	
	
	if(rot_x == 0 && rot_y == 0)
	    return lines.get(y).charAt(x);	
	
	var index_x = x % lines.get(0).length();
	var index_y = y % lines.size();
	
	return modulate(lines.get(index_y).charAt(index_x), rot_x + rot_y);
    }
    
    public Character modulate(Character c, int mod) {
	if(c == null)
	    return null;
	var ch = c + mod;
	while(ch > '9')
	    ch = (char)((int)ch - 9);

	return (char)ch;
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
        new Advent2021_15().run(argv);
    }
}
