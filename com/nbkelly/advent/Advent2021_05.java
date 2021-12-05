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
import com.nbkelly.lib.IntPair;
import java.util.HashMap;
import com.nbkelly.drafter.BooleanCommand; //visualize cmd
import com.nbkelly.lib.Image; //visualizer lib

/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2021_05 extends Drafter {
    /* WORKFLOW:
     *  set all needed commands with setCommands()
     *  post-processing can be performed with actOnCommands()
     *  the rest of your work should be based around the solveProblem() function
     */
    
    /* params injected from file */
    ArrayList<String> lines;
    
    //generate output
    boolean generate_output = false;

    private class Line {
	IntPair start;
	IntPair end;

	public Line(int x1, int y1, int x2, int y2) {
	    start = new IntPair(x1, y1);
	    end = new IntPair(x2, y2);
	}
	public boolean straight() {
	    return start.X == end.X || start.Y == end.Y;
	}

	
    }

    public boolean ccw(IntPair A, IntPair B, IntPair C) {
	return (C.Y-A.Y) * (B.X-A.X) > (B.Y-A.Y) * (C.X-A.X);
    }

    //a->b, c->d
    public boolean intersect(IntPair A, IntPair B, IntPair C, IntPair D) {
	return (ccw(A,C,D) != ccw(B,C,D)) && (ccw(A,B,C) != ccw(A,B,D));
    }

    
    /* solve problem here */
    @Override public int solveProblem() throws Exception {
	Timer t = makeTimer();

        /* code injected from file */
        //var ints = Util.toIntList(lines);
	ArrayList<Integer> lines = new ArrayList<Integer>();

	int min_x = 0;
	int min_y = 0;
	int max_x = 0;
	int max_y = 0;

	HashMap<IntPair, Integer> tally = new HashMap<IntPair, Integer>();
	HashSet<IntPair> intersections = new HashSet<IntPair>();
	
	while (hasNextLine()) {
	    var left = next();
	    next();
	    var right = next();
	    flushLine();

	    var ll = left.split(",");
	    var rr = right.split(",");

	    int sx = Integer.parseInt(ll[0]);
	    int sy = Integer.parseInt(ll[1]);

	    int fx = Integer.parseInt(rr[0]);
	    int fy = Integer.parseInt(rr[1]);

	    drawLine(sx, sy, fx, fy, tally, intersections);

	    min_x = Math.min(min_x, Math.min(sx, fx));
	    min_y = Math.min(min_y, Math.min(sy, fy));

	    max_x = Math.max(max_x, Math.max(sx, fx));
	    max_y = Math.max(max_y, Math.max(sy, fy));
	}

	println(intersections.size());
	
	println("Hello World");
        
        DEBUGF(1, "PART ONE: "); //todo
        DEBUGF(1, "PART TWO: "); //todo
        
        /* visualize output here */
        generate_output(min_x, min_y, max_x, max_y, tally);
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    private void mark(IntPair loc, HashMap<IntPair, Integer> space, HashSet<IntPair> intersections) {
	if(space.get(loc) == null)
	    space.put(loc, 1);
	else {
	    space.put(loc, space.get(loc) + 1);
	    intersections.add(loc);
	}
    }
    
    private void drawLine(int x1, int y1, int x2, int y2,
			  HashMap<IntPair, Integer> space, HashSet<IntPair> intersections) {
	/* horizontal line */
	if(y1 == y2) {
	    for(int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
		IntPair loc = new IntPair(x, y1);
		mark(loc, space, intersections);
	    }
	}
	/* vertical line */
	else if (x1 == x2) {
	    for(int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
		IntPair loc = new IntPair(x1, y);
		mark(loc, space, intersections);
	    }
	}
	/* diagonal line */
	else {
	    int x = x1;
	    int y = y1;

	    int d_x = (x2 - x1) / Math.abs(x2 - x1);
	    int d_y = (y2 - y1) / Math.abs(y2 - y1);

	    int dist = Math.abs(x2 - x1);

	    for(int i = 0; i <= dist; i++) {
		int dx = d_x * i;
		int dy = d_y * i;
		IntPair loc = new IntPair(x + dx, y + dy);
		mark(loc, space, intersections);
	    }
	}
    }

    /* code injected from file */
    public void generate_output(int xmin, int ymin,
				int xmax, int ymax,
				HashMap<IntPair, Integer> scores) throws Exception {
    	if(!generate_output)
    	    return;
	
    	println(">generating output");

	int size = 4;
	Image i = new Image(size* (1 + xmax - xmin), size* (1 + ymax - ymin));
	
    	/* output goes here */

	for(int x = xmin; x <= xmax; x++)
	    for(int y = ymin; y <= ymax; y++) {
		int offset_x = x - xmin;
		int offset_y = y - ymin;

		var score = scores.get(new IntPair(x, y));
		if(score != null) {
		    if(score == 1)
			i.rect(Image.C1, size*offset_x, size*offset_y, size, size);
		    else if (score >= 2)
			i.rect(Image.C3, size*offset_x, size*offset_y, size, size);
		}
	    }

	i.savePNG("out1.png");
	println(">out1.png");
	
	i = new Image(size* (1 + xmax - xmin), size* (1 + ymax - ymin));

	for(int x = xmin; x <= xmax; x++)
	    for(int y = ymin; y <= ymax; y++) {
		int offset_x = x - xmin;
		int offset_y = y - ymin;

		var score = scores.get(new IntPair(x, y));
		if(score != null) {
		    if(score == 1) {}
		    //i.rect(Image.C1, size*offset_x, size*offset_y, size, size);
		    else if (score >= 2)
			i.rect(Image.C3, size*offset_x, size*offset_y, size, size);
		}
	    }

	i.savePNG("out2.png");
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
        new Advent2021_05().run(argv);
    }
}
