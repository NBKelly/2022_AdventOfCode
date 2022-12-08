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
public class Advent2022_08 extends Drafter {
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
        
        println(">Good Morning!");
        
        DEBUGF(1, "PART ONE: "); //todo
	DEBUG(t.split("started"));
	int width = lines.get(0).length();
	int height = lines.size();

	boolean[] visible = new boolean[width*height];
	for(int x = 0; x < width; x++) {
	    visible[index(x, 0, width)] = true;
	    visible[index(x, height-1, width)] = true;

	    int maxHeight = get(x, 0, lines);
	    for(int y = 1; y < height-1; y++) {
		if(get(x,y,lines) > maxHeight) {
		    visible[index(x, y, width)] = true;
		    maxHeight = get(x,y,lines);
		}
	    }

	    maxHeight = get(x, width-1, lines);
	    for(int y = height-1; y > 0; y--) {
		if(get(x,y,lines) > maxHeight) {
		    visible[index(x, y, width)] = true;
		    maxHeight = get(x,y,lines);
		}
	    }
	}

	for(int y = 0; y < height; y++) {
	    visible[index(0, y, width)] = true;
	    visible[index(width-1, y, width)] = true;

	    int maxHeight = get(0, y, lines);
	    for(int x = 1; x < width-1; x++) {
		if(get(x,y,lines) > maxHeight) {
		    visible[index(x, y, width)] = true;
		    maxHeight = get(x,y,lines);
		}
	    }

	    maxHeight = get(width-1, y, lines);
	    for(int x = width-1; x >0; x--) {
		if(get(x,y,lines) > maxHeight) {
		    visible[index(x, y, width)] = true;
		    maxHeight = get(x,y,lines);
		}
	    }

	}

	int sum = 0;
	for(int x = 0; x < width*height; x++)
	    if(visible[x])
		sum++;

	println("visible: " + sum);

	DEBUG(t.split("part 1 complete"));
        DEBUGF(1, "PART TWO: "); //todo
	int bestScore = 0;
	for(int x = 0; x < width; x++)
	    for(int y = 0; y < height; y++)
		if(visible[index(x,y,width)])
		    bestScore = Math.max(bestScore, getScenicScore(x, y, width, height, lines));

	println(bestScore);
	DEBUG(t.split("part 2 complete"));

        /* visualize output here */
        generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    public int getScenicScore(int x, int y, int width, int height, ArrayList<String> lines) {
	int maxheight = get(x, y, lines);

	int tscore = 1;
	int score = 0;

	for(int _x = x+1; _x < width; _x++) {
	    score++;
	    if(get(_x,y,lines) >= maxheight) break;

	}
	tscore *= score;
	score = 0;
	for(int _x = x-1; _x >=0; _x--) {
	    score++;
	    if(get(_x,y,lines) >= maxheight) break;
	}
	tscore *= score;
	score = 0;
	for(int _y = y+1; _y < height; _y++) {
	    score++;
	    if(get(x,_y,lines) >= maxheight) break;
	}
	tscore *= score;
	score = 0;
	for(int _y = y-1; _y >= 0; _y--) {
	    score++;
	    if(get(x,_y,lines) >= maxheight) break;
	}
	tscore *= score;

	return tscore;
    }

    public int get(int x, int y, ArrayList<String> lines) {
	return Integer.parseInt(""+lines.get(y).charAt(x));
    }

    public int index(int x, int y, int width) {
	return x + (y*width);
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
        new Advent2022_08().run(argv);
    }
}
