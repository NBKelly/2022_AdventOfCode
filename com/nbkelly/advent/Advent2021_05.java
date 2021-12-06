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
import com.nbkelly.lib.IntPair;
import com.nbkelly.drafter.BooleanCommand; //visualize cmd
import com.nbkelly.lib.Image; //visualizer lib

import java.util.HashSet;
import java.util.TreeSet;
import java.util.TreeMap;
import java.util.AbstractCollection;

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

    static int __ID = 0;
    private class Horizontal {
	
	public Horizontal(int y, int x1, int x2) {
	    this.y = y;
	    this.x1 = Math.min(x1, x2);
	    this.x2 = Math.max(x1, x2);
	}
	int y;
	int x1;
	int x2;
	int id = ++__ID;
	
	public String toString() {
	    return String.format("h ((%d -> %d), %d)", x1, x2, y);
	}

	public int length() {
	    return (1 + x2) - x1;
	}
    }

    private class Vertical {
	public Vertical(int x, int y1, int y2) {
	    this.x = x;
	    this.y1 = Math.min(y1, y2);
	    this.y2 = Math.max(y1, y2);
	}
	int x;
	int y1;
	int y2;
	int id = ++__ID;
	
	public String toString() {
	    return String.format("v (%d, (%d -> %d))", x, y1, y2);
	}

	public int length() {
	    return (1 + y2) - y1;
	}
    }

    
    /* solve problem here */
    @Override public int solveProblem() throws Exception {
	Timer t = makeTimer();

	ArrayList<Horizontal> hlines = new ArrayList<Horizontal>();
	ArrayList<Vertical> vlines = new ArrayList<Vertical>();
	
        while(hasNextLine()) {
	    var line = nextLine();
	    var nums = line.split(",| -> ");

	    int x1 = Integer.parseInt(nums[0]);
	    int x2 = Integer.parseInt(nums[2]);

	    int y1 = Integer.parseInt(nums[1]);
	    int y2 = Integer.parseInt(nums[3]);

	    if(y1 == y2)
		hlines.add(new Horizontal(y1, x1, x2));
	    else if (x1 == x2)
		vlines.add(new Vertical(x1, y1, y2));
	}

	println("Hello World");

	int val = solve_orthogonal(hlines, vlines, 0);
	println(val);
	
        DEBUGF(1, "PART ONE: "); //todo
        DEBUGF(1, "PART TWO: "); //todo
        
        /* visualize output here */
        generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    private Integer solve_orthogonal(ArrayList<Horizontal> horizontal,
				     ArrayList<Vertical> vertical, int depth) {	
	/* sort all horizontal lines */
	var horizontal_sorted_y
	    = new TreeSet<Horizontal>((Horizontal left, Horizontal right) ->
				      Util.compareTo(left.y, right.y,   left.x1, right.x1,
						     left.x2, right.x2, left.id, right.id));	
	horizontal_sorted_y.addAll(horizontal);
	
	/* find the set of colinear horizontal lines */
	var colinear_horizontal = select_colinear_horizontal(horizontal_sorted_y);
	horizontal_sorted_y = null;
	
	ArrayList<Horizontal> horizontal_colinear_overlaps = new ArrayList<>();
	
	for(var entry : colinear_horizontal.entrySet()) {
	    //find the set of intersections
	    var isect = intersections_horizontal(entry.getValue());
	    
	    if(isect.size() == 0)
		continue;
	    
	    var y = isect.get(0).y;
	    Ranger r = new Ranger();
	    for(var segment : isect)
		r.add(segment);

	    var segments = r.to_horizontal(y);	    
	    horizontal_colinear_overlaps.addAll(segments);
	}

	/* print these all out */
	for(var line : horizontal_colinear_overlaps)
	  println("h_segments: " + line);

	/* sort all vertical lines */
	var vertical_sorted_x
	    = new TreeSet<Vertical>((Vertical left, Vertical right) ->
				    Util.compareTo(left.x, right.x,   left.y1, right.y1,
						   left.y2, right.y2, left.id, right.id));
	vertical_sorted_x.addAll(vertical);

	/* find the set of colinear vertical lines */
	var colinear_vertical = select_colinear_vertical(vertical_sorted_x);

	ArrayList<Vertical> vertical_colinear_overlaps = new ArrayList<>();

	for(var entry : colinear_vertical.entrySet()) {
	    var isect = intersections_vertical(entry.getValue());

	    if(isect.size() == 0)

		continue;

	    var x = isect.get(0).x;
	    Ranger r = new Ranger();
	    for(var segment : isect)
		r.add(segment);

	    var segments = r.to_vertical(x);
	    vertical_colinear_overlaps.addAll(segments);
	}

	/* print these all out */
	for(var line : vertical_colinear_overlaps)
	    println("v_segments: " + line);

	
	int line_lazy_size = 0;
	for(var line : vertical_colinear_overlaps)
	    line_lazy_size += line.length();

	for(var line : horizontal_colinear_overlaps)
	    line_lazy_size += line.length();

	println("lazy line space: " + line_lazy_size);



	/*
	 * Now, find all the horizontal-vertical intersections!
	 */

	//this is all of the horizontal lines sorted by x1
	ArrayList<Horizontal> horizontal_sorted_x1 = new ArrayList<>();
	horizontal_sorted_x1.addAll(horizontal);
	horizontal_sorted_x1.sort((Horizontal left, Horizontal right)->
				  Util.compareTo(left.x1, right.x1, left.x2, right.x2,
						 left.y, right.y, left.id, right.id));

	//this will only be the horizontal lines that are open, sorted by x2
	TreeSet<Horizontal> open_interval //horizontal_sorted_x2
	    = new TreeSet<>((Horizontal left, Horizontal right)->
			  Util.compareTo(left.x2, right.x2, left.x1, right.x1,
					 left.y, right.y, left.id, right.id));

	//this will be all of the vertical lines, sorted by x - this is already defined
	/*TreeSet<Vertical> vertical_sorted_x
	    = new TreeSet((Vertical left, Vertical right)->
			  Util.compareTo(left.x, right.x,   left.y1, right.y1,
			  left.y2, right.y2, left.id, right.id));*/

	int index = 0;
	
	HashSet<IntPair> collisions = new HashSet<IntPair>();

	/* stratify all colinear overlaps */
	var strata_horizontal = select_colinear_horizontal(horizontal_colinear_overlaps);
	var strata_vertical = select_colinear_vertical(vertical_colinear_overlaps);
	
	for(var vertical_line : vertical_sorted_x) {
	    int x = vertical_line.x;

	    /* pick up all lines that start to the left (or in line) with this one */
	    while(index < horizontal_sorted_x1.size() &&
		  horizontal_sorted_x1.get(index).x1 <= x) {
		open_interval.add(horizontal_sorted_x1.get(index));
		index++;
	    }

	    /* throw away all lines which end to the left of this one */
	    while(!open_interval.isEmpty() && open_interval.first().x2 < x)
		open_interval.pollFirst();

	    /* now we scan throw all lines in the interval, and see which ones intersect 
	       note that these lines are all guaranteed to line up on x - only y is suspect */
	    outer: for(var horizontal_line : open_interval) {
		IntPair pair = new IntPair(x, horizontal_line.y);
		if(vertical_line.y1 <= horizontal_line.y &&
		   horizontal_line.y <= vertical_line.y2) {		    
		    /* see if this is marked by an overlapping line already */
		    var h_strata = strata_horizontal.get(pair.Y);
		    if(h_strata != null)
			for(var hline : h_strata)
			    if(pair.Y == hline.y)
				if(hline.x1 <= pair.X && pair.X <= hline.x2)
				    continue outer;
		    
		    var v_strata = strata_vertical.get(pair.X);
		    if(v_strata != null)
			for(var vline : v_strata)
			    if(pair.X == vline.x)
				if(vline.y1 <= pair.Y && pair.Y <= vline.y2)
				    continue outer;

		    collisions.add(pair);
		}		   
	    }
	}

	horizontal_sorted_x1 = null;
	open_interval = null;
	
	printf("pre-reduction collisions: %d   (%d  +  %d)%n",
	       (collisions.size() + line_lazy_size),
	       collisions.size(), line_lazy_size);
	
	//now we need to remove 1 for every time a vertical colinear segment collided with
	//a horizontal colinear segment

	var res = collisions.size() + line_lazy_size;
	//fortunately, we can do this recursively
	var removal = 0;
	if(horizontal_colinear_overlaps.size() > 0 &&
	   vertical_colinear_overlaps.size() > 0) {
	    DEBUG(2, "recursively solving for depth " + (depth + 1) + "\n");
	    removal = solve_orthogonal(horizontal_colinear_overlaps,
				       vertical_colinear_overlaps, depth + 1);
	    DEBUG(2, "finished solving for depth " + (depth + 1) + "\n");
	}

	res = res - removal;
	println("Size of overlap of colinear overlaps: " + removal);
	//println("collision size: " + collisions.size());

	return res;
    }

    /**
     * RANGE
     */
    
    private class Ranger {
	ArrayList<Range> ranges = new ArrayList<>();

	public void add(Horizontal h) {
	    add(h.x1, h.x2);
	}
	public void add(Vertical h) {
	    add(h.y1, h.y2);
	}
	public void add(int open, int close) {
	    Range range = new Range(open, close);

	    ArrayList<Range> res = new ArrayList<Range>();
	    for(var old : ranges) {
		var next = range.intersection(old);
		if(next == null)
		    res.add(old);
		else
		    range = next;
	    }

	    res.add(range);
	    ranges = res;
	}
	
	private class Range {
	    int open, close;
	    public Range(int open, int close) {
		this.open = open;
		this.close = close;
	    }

	    public Range intersection(Range target) {
		if((close < target.open) || (open > target.close))
		    return null;

		return new Range(Math.min(open, target.open),
				 Math.max(close, target.close));
	    }
	}

	public ArrayList<Horizontal> to_horizontal(int y) {
	    ArrayList<Horizontal> components = new ArrayList<>();
	    for(var range: ranges)
		components.add(new Horizontal(y, range.open, range.close));

	    return components;
	}

	public ArrayList<Vertical> to_vertical(int x) {
	    ArrayList<Vertical> components = new ArrayList<>();
	    for(var range: ranges)
		components.add(new Vertical(x, range.open, range.close));

	    return components;
	}
    }

    /**
     *  INTERSECTIONS
     */
    
    private ArrayList<Vertical> intersections_vertical(ArrayList<Vertical> colinear) {
	if(colinear.size() < 2)
	    return new ArrayList<>();
	/*
	 * Lines to sample:
	 *  1. l1 --------
	 *                  l2 ---------
	 *
	 *  2.  l1 -----------
	 *               l2 ---------
	 *
	 *  3. l1 -------------------++++
	 *               l2----------
	 *
	 *  4.       l1 ----+++
	 *          l2 -----
	 *
	 *  5.       l1-
	 *        l2-----
	 *
	 *  6.            l1-----
	 *        l2-----
	 */

	ArrayList<Vertical> intersections = new ArrayList<Vertical>();

	int x = colinear.get(0).x;
	
	for(int a = 0; a < colinear.size(); a++) {
	    var left = colinear.get(a);
	    for(int b = a + 1; b < colinear.size(); b++) {
		var right = colinear.get(b);

		//1, 2, 3
		if(left.y1 <= right.y1) {
		    //1
		    if(left.y2 < right.y1) continue;
		    if(left.y2 < right.y2) intersections.add(new Vertical(x, right.y1, left.y2));
		    else intersections.add(new Vertical(x, right.y1, right.y2));
		}
		//4, 5
		else if(left.y1 <= right.y2) {
		    if(left.y2 <= right.y2) intersections.add(new Vertical(x, left.y1, left.y2));
		    else intersections.add(new Vertical(x, left.y2, right.y2));
		}
	    }
	}
	
	return intersections;
    }
    
    private ArrayList<Horizontal> intersections_horizontal(ArrayList<Horizontal> colinear) {
	if(colinear.size() < 2)
	    return new ArrayList<>();
	/*
	 * Lines to sample:
	 *  1. l1 --------
	 *                  l2 ---------
	 *
	 *  2.  l1 -----------
	 *               l2 ---------
	 *
	 *  3. l1 -------------------++++
	 *               l2----------
	 *
	 *  4.       l1 ----+++
	 *          l2 -----
	 *
	 *  5.       l1-
	 *        l2-----
	 *
	 *  6.            l1-----
	 *        l2-----
	 */

	ArrayList<Horizontal> intersections = new ArrayList<Horizontal>();

	int y = colinear.get(0).y;
	
	for(int a = 0; a < colinear.size(); a++) {
	    var left = colinear.get(a);
	    for(int b = a + 1; b < colinear.size(); b++) {
		var right = colinear.get(b);

		//1, 2, 3
		if(left.x1 <= right.x1) {
		    //1
		    if(left.x2 < right.x1) continue;
		    if(left.x2 < right.x2) intersections.add(new Horizontal(y, right.x1, left.x2));
		    else intersections.add(new Horizontal(y, right.x1, right.x2));
		}
		//4, 5
		else if(left.x1 <= right.x2) {
		    if(left.x2 <= right.x2) intersections.add(new Horizontal(y, left.x1, left.x2));
		    else intersections.add(new Horizontal(y, left.x2, right.x2));
		}
	    }
	}
	
	return intersections;
    }

    private TreeMap<Integer, ArrayList<Horizontal>>
	select_colinear_horizontal(AbstractCollection<Horizontal> horizontal_sorted) {
	TreeMap<Integer, ArrayList<Horizontal>> colinear = new TreeMap<>();

	for(var line : horizontal_sorted) {
	    if(!colinear.containsKey(line.y))
		colinear.put(line.y, new ArrayList<>());
	    colinear.get(line.y).add(line);
	}

	return colinear;
    }

    private TreeMap<Integer, ArrayList<Vertical>>
	select_colinear_vertical(AbstractCollection<Vertical> vertical_sorted) {
	TreeMap<Integer, ArrayList<Vertical>> colinear = new TreeMap<>();

	for(var line : vertical_sorted) {
	    if(!colinear.containsKey(line.x))
		colinear.put(line.x, new ArrayList<>());
	    colinear.get(line.x).add(line);
	}

	return colinear;
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
        //lines = readFileLines(((FileCommand)userCommands[0]).getValue());
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
