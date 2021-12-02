package com.nbkelly.advent;

/* imports */
import com.nbkelly.drafter.Drafter;
import com.nbkelly.drafter.Command;
import com.nbkelly.drafter.FileCommand;
import com.nbkelly.drafter.Timer;

/* imports from file */
import java.util.ArrayList;
import com.nbkelly.lib.Util;
import com.nbkelly.lib.Image;
import com.nbkelly.drafter.BooleanCommand; //visualize cmd

/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2021_02 extends Drafter {
    /* WORKFLOW:
     *  set all needed commands with setCommands()
     *  post-processing can be performed with actOnCommands()
     *  the rest of your work should be based around the solveProblem() function
     */
    
    /* params injected from file */
    ArrayList<String> lines;
    
    //generate output
    boolean generate_output = false;
    String filename = "out.png";
    
    /* solve problem here */
    @Override public int solveProblem() throws Exception {
	Timer t = makeTimer();

        
	/* part one */
	//note -  we need to save these for image generation purposes
	ArrayList<String> directions = new ArrayList<String>();
	ArrayList<Integer> magnitude = new ArrayList<Integer>();
	
	while(hasNextLine()) {
	    directions.add(next());
	    magnitude.add(nextInt());
	    flushLine();
	}

	int depth = 0;
	int loc = 0;	
	for(int z = 0; z < directions.size(); z++) {
	    int mag = magnitude.get(z);
	    switch(directions.get(z)) {
	    case "forward": loc += mag;	break;
	    case "up":    depth -= mag;	break;
	    case "down":  depth += mag;	break;
	    }
	}

	int p1_ans = depth*loc;

	/* part two */
	depth = 0;
	loc = 0;
	int aim = 0;
	for(int z = 0; z < directions.size(); z++) {
	    int mag = magnitude.get(z);
	    switch(directions.get(z)) {
	    case "forward":
		loc += mag;
		depth += aim*mag;
		break;
	    case "up":    aim -= mag;	break;
	    case "down":  aim += mag;	break;
	    }
	}

	int p2_ans = depth * loc;        
        
        DEBUGF(2, "PART ONE: "); println(p1_ans);
        DEBUGF(2, "PART TWO: "); println(p2_ans);

	generate_output(directions, magnitude);
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    /* code injected from file */
    public void generate_output(ArrayList<String> directions,
				ArrayList<Integer> magnitude) throws Exception {
    	if(!generate_output)
    	    return;
    	
    	println(">generating output...");

	//first we need to find out the max and min depth achieved
	/* part one */
	int minDepth = 0;
	int maxDepth = 0;
	int depth = 0;
	int loc = 0;	
	for(int z = 0; z < directions.size(); z++) {
	    int mag = magnitude.get(z);
	    switch(directions.get(z)) {
	    case "forward": loc += mag;	break;
	    case "up":    depth -= mag;	break;
	    case "down":  depth += mag;	break;
	    }

	    minDepth = Math.min(depth, minDepth);
	    maxDepth = Math.max(depth, maxDepth);
	}

	depth = 0;
	loc = 0;
	int aim = 0;
	int maxDepth2 = 0;
	int minDepth2 = 0;
	
	for(int z = 0; z < directions.size(); z++) {
	    int mag = magnitude.get(z);
	    switch(directions.get(z)) {
	    case "forward":
		loc += mag;
		depth += aim*mag;
		break;
	    case "up":    aim -= mag;	break;
	    case "down":  aim += mag;	break;
	    }
	    maxDepth2 = Math.max(depth, maxDepth2);
	    minDepth2 = Math.min(depth, minDepth2);
	}

	int vscale = 100;
	int vscale2 = 5;
	println("MaxDepth: " + maxDepth2);
	Image i = new Image(loc, maxDepth-minDepth);
	Image i2 = new Image(loc, (maxDepth2-minDepth2)/vscale);
	Image i3 = new Image(loc, Math.max((maxDepth2-minDepth2), (maxDepth-minDepth))/vscale);
	
	depth = 0;
	loc = 0;
	for(int z = 0; z < directions.size(); z++) {
	    int d_last = depth;
	    int l_last = loc;
	    
	    int mag = magnitude.get(z);
	    switch(directions.get(z)) {
	    case "forward": loc += mag;	break;
	    case "up":    depth -= mag;	break;
	    case "down":  depth += mag;	break;
	    }
	    
	    if(d_last != depth) {
		i.line(Image.C1, loc, d_last, loc, depth, 5);
		i3.line(Image.C1, loc, d_last/vscale2, loc, depth/vscale2, 3);
	    }
	    if (l_last != loc) {
		i.line(Image.C1, l_last, depth, loc, depth, 5);
		i3.line(Image.C1, l_last, depth/vscale2, loc, depth/vscale2, 3);
	    }
	}

	i.savePNG("out1.png");
	println(">out1.png");

	depth = 0;
	loc = 0;
	aim = 0;
	
	for(int z = 0; z < directions.size(); z++) {
	    int lastLoc = loc;
	    int lastDepth = depth;
	    int mag = magnitude.get(z);
	    switch(directions.get(z)) {
	    case "forward":
		loc += mag;
		depth += aim*mag;
		break;
	    case "up":    aim -= mag;	break;
	    case "down":  aim += mag;	break;
	    }

	    if(loc != lastLoc) {
		i2.line(Image.C3, lastLoc, lastDepth/vscale, loc, depth/vscale, 15);
		i3.line(Image.C3, lastLoc, lastDepth/vscale, loc, depth/vscale, 15);
	    }
	}

	i2.savePNG("out2.png");
	println(">out2.png");

	i3.savePNG("out3.png");
	println(">out3.png");	
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
        				       false, "--out-file");
        
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
        new Advent2021_02().run(argv);
    }
}
