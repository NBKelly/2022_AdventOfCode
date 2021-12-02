package com.nbkelly.advent;

/* imports */
import com.nbkelly.drafter.Drafter;
import com.nbkelly.drafter.Command;
import com.nbkelly.drafter.FileCommand;
import com.nbkelly.drafter.BooleanCommand;
import com.nbkelly.drafter.Timer;

/* imports from file */
import java.util.ArrayList;
import com.nbkelly.lib.Util;
import com.nbkelly.lib.Image;

import java.util.LinkedList;
import java.util.TreeSet;

/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2021_01 extends Drafter {
    /* WORKFLOW:
     *  set all needed commands with setCommands()
     *  post-processing can be performed with actOnCommands()
     *  the rest of your work should be based around the solveProblem() function
     */
    
    /* params injected from file */
    ArrayList<String> lines;
    boolean generate_output = false;
    String filename = "out.png";
    
    /* solve problem here */
    @Override public int solveProblem() throws Exception {
	Timer t = makeTimer();

	int ct = 0;
	int ct_2 = 0;

	int WIN_SIZE = 3;
	
	var ints = Util.toIntList(lines);
	for(int i = 1; i < ints.size(); i++) {
	    int cline = ints.get(i);
	    //check part one
	    if(ints.get(i-1) < cline)
	       ct++;
	    //check part two - this is a sliding window with size WIN_SIZE
	    if(i >= WIN_SIZE && ints.get(i-WIN_SIZE) < cline)
		ct_2++;
	}

	DEBUGF(2, "PART ONE: "); println(ct);
	DEBUGF(2, "PART TWO: "); println(ct_2);
	
	generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    public void generate_output() throws Exception {
	if(!generate_output)
	    return;
	
	println(">generating output");
	
	var ints = Util.toIntList(lines);
	int width = ints.size();

	//find the biggest element
	var set = new TreeSet<Integer>();
	set.addAll(ints);
	var max = set.last();

	printf(">width: %d height: %d%n", width, max);

	var image = new Image(width, max);

	for(int i = 0; i < ints.size(); i++)
	    image.rect(Image.C1, i, 0, i+1, ints.get(i));

	image.savePNG(filename);
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
        new Advent2021_01().run(argv);
    }
}
