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
    int offset = 1;
    private class Ascending {
	int x1;
	int x2;
	int y1;
	int y2;
	int id = ++__ID;
	
	public Ascending(int x1, int x2, int y1, int y2) {
	    if(x1 < x2) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	    }
	    else {
		this.x1 = x2;
		this.x2 = x1;
		this.y1 = y2;
		this.y2 = y1;
	    }
	    int id = ++__ID;
	}

	public String toString() {
	    return String.format(" asc ((%d -> %d), (%d -> %d)) %d",// (%d -> %d))",
				 x1+offset, x2+offset, y1+offset, y2+offset,
				 tendency(), z1(), z2());

	}

	public int length() {
	    return 1 + (x2 - x1);
	}

	public int tendency() {
	    return y1 - x1;
	}

	public int z1() {
	    return y1 + x1;
	}

	public int z2() {
	    return y2 + x2;
	}

	public Ascending warp(int x1, int x2) {
	    //tendency = y1 - x1
	    //tendency + x1 = y1;
	    int y1 = tendency() + x1;
	    int y2 = tendency() + x2;
	    return new Ascending(x1, x2, y1, y2);
	}
    }

    private class Descending {
	int x1;
	int x2;
	int y1;
	int y2;
	int id = ++__ID;
	
	public Descending(int x1, int x2, int y1, int y2) {
	    if(x1 < x2) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	    }
	    else {
		this.x1 = x2;
		this.x2 = x1;
		this.y1 = y2;
		this.y2 = y1;
	    }
	    int id = ++__ID;
	}

	public String toString() {
	    return String.format("desc ((%d -> %d), (%d -> %d), %d",// (%d -> %d))",
				 x1+offset, x2+offset, y1+offset, y2+offset,
				 tendency(), z1(), z2());
	}

	public int length() {
	    return 1 + (x2 - x1);
	}

	public int tendency() {
	    return y1 + x1;
	}

	public int z2() {
	    return y1 - x1;
	}

	public int z1() {
	    return y2 - x2;
	}

	public Descending warp(int x1, int x2) {
	    //tendency = x1 + y1
	    //tendency - x1 = y1
	    int y1 = tendency() - x1;
	    int y2 = tendency() - x2;
	    return new Descending(x1, x2, y1, y2);
	}
    }
    
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

	ArrayList<Ascending> alines = new ArrayList<Ascending>();
	ArrayList<Descending> dlines = new ArrayList<Descending>();
	
        while(hasNextLine()) {
	    var line = nextLine();

	    if(line.equals(""))
		break;
	    
	    var nums = line.split(",| -> ");

	    int x1 = Integer.parseInt(nums[0]);
	    int x2 = Integer.parseInt(nums[2]);

	    int y1 = Integer.parseInt(nums[1]);
	    int y2 = Integer.parseInt(nums[3]);

	    if(y1 == y2)
		hlines.add(new Horizontal(y1, x1, x2));
	    else if (x1 == x2)
		vlines.add(new Vertical(x1, y1, y2));
	    else {
		//normalize x, so the line is left->right
		if(x1 > x2) {
		    var tmp = x2;
		    x2 = x1;
		    x1 = tmp;

		    tmp = y2;
		    y2 = y1;
		    y1 = tmp;
		}

		//see if it's asc or desc
		if(y1 > y2)
		    dlines.add(new Descending(x1, x2, y1, y2));
		else
		    alines.add(new Ascending(x1, x2, y1, y2));
	    }
	}


	/*for(var asc : alines)
	    println(asc);

	for(var dsc : dlines)
	println(dsc);*/
		
	println("Hello World");

	int val = 0;
	/*val = solve_orthogonal(hlines, vlines, 0);
	  println(val);*/

	val = solve_octogonal(hlines,
			      vlines,
			      alines,
			      dlines,
			      1);
	println(val);
        DEBUGF(1, "PART ONE: "); //todo
        DEBUGF(1, "PART TWO: "); //todo
        
        /* visualize output here */
        generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    private Integer solve_octogonal(ArrayList<Horizontal> horizontal,
				    ArrayList<Vertical> vertical,
				    ArrayList<Ascending> ascending,
				    ArrayList<Descending> descending,
				    int depth) {
	/**
	 * WORK ON ORTHOGONALS
	 */
	/* sort all horizontal lines */
	var horizontal_sorted_y
	    = new TreeSet<Horizontal>((Horizontal left, Horizontal right) ->
				      Util.compareTo(left.y, right.y,   left.x1, right.x1,
						     left.x2, right.x2, left.id, right.id));	
	horizontal_sorted_y.addAll(horizontal);
	
	/* find the set of colinear horizontal lines */
	var colinear_horizontal = select_colinear_horizontal(horizontal_sorted_y);
	//horizontal_sorted_y = null;
	
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
	/*for(var line : vertical_colinear_overlaps)
	  println("v_segments: " + line);*/

	
	int line_orthogonal_size = 0;
	for(var line : vertical_colinear_overlaps)
	    line_orthogonal_size += line.length();

	for(var line : horizontal_colinear_overlaps)
	    line_orthogonal_size += line.length();

	println("lazy orthogonal space: " + line_orthogonal_size);
	
	/* stratify all horizontal  overlaps */
	var strata_horizontal = select_colinear_horizontal(horizontal_colinear_overlaps);
	var strata_vertical = select_colinear_vertical(vertical_colinear_overlaps);

	/**
	 * WORK ON DIAGONALS
	 */


	
	
	var descending_sorted_xy
	    = new TreeSet<Descending>((Descending left, Descending right) ->
				      Util.compareTo(left.tendency(), right.tendency(),
						     left.x1, right.x1,
						     left.x2, right.x2,
						     left.id, right.id));

	descending_sorted_xy.addAll(descending);

	var ascending_sorted_xy
	    = new TreeSet<Ascending>((Ascending left, Ascending right) ->
				     Util.compareTo(left.tendency(), right.tendency(),
						    left.y1, right.y1,
						    left.y2, right.y1,
						    left.id, right.id));

	ascending_sorted_xy.addAll(ascending);
	
	
	//println(ascending_sorted_xy);

	var colinear_ascending = select_colinear_ascending(ascending_sorted_xy);
	ArrayList<Ascending> ascending_colinear_overlaps = new ArrayList<>();
	
	for(var entry : colinear_ascending.entrySet()) {
	    //find the set of intersections
	    var isect = intersections_ascending(entry.getValue());
	    
	    if(isect.size() == 0)
		continue;
	    
	    //println(isect);

	    var tendency = isect.get(0).tendency();
	    Ranger r = new Ranger();
	    for(var segment : isect)
		r.add(segment);

	    var segments = r.to_ascending(tendency);
	    //println("segments: " + segments);
	    ascending_colinear_overlaps.addAll(segments);
	}


	println();
	
	//println(descending_sorted_xy);
	var colinear_descending = select_colinear_descending(descending_sorted_xy);
	ArrayList<Descending> descending_colinear_overlaps = new ArrayList<>();
	
	for(var entry : colinear_descending.entrySet()) {
	    //println("colinear elements: " + entry.getValue());	    
	    var isect = intersections_descending(entry.getValue());

	    if(isect.size() == 0)
		continue;

	    //println(isect);

	    var tendency = isect.get(0).tendency();
	    Ranger r = new Ranger();
	    for(var segment : isect)
		r.add(segment);
	    
	    var segments = r.to_descending(tendency);
	    //println("segments: " + segments);
	    descending_colinear_overlaps.addAll(segments);
	}

	int line_lazy_size = 0;
	for(var line : ascending_colinear_overlaps)
	    line_lazy_size += line.length();

	for(var line : descending_colinear_overlaps)
	    line_lazy_size += line.length();

	println("lazy line space: " + line_lazy_size);
	
	HashSet<IntPair> collisions = new HashSet<IntPair>();


	/* statified overlaps */
	var strata_descending_colinear = select_colinear_descending(descending_colinear_overlaps);
	var strata_ascending_colinear = select_colinear_ascending(ascending_colinear_overlaps);
	
	//now to find all the points where diagonal lines overlap
	
	ArrayList<Ascending> asc_sorted_x1 = new ArrayList<Ascending>(ascending);
	asc_sorted_x1.sort((Ascending left, Ascending right) ->
			   Util.compareTo(left.x1, right.x1,
					  left.x2, right.x2,
					  left.tendency(), right.tendency(),
					  left.id, right.id));

	ArrayList<Descending> dsc_sorted_x1 = new ArrayList<Descending>(descending);
	dsc_sorted_x1.sort((Descending left, Descending right) ->
			   Util.compareTo(left.x1, right.x1,
					  left.x2, right.x2,
					  left.tendency(), right.tendency(),
					  left.id, right.id));

	TreeSet<Descending> open_set
	    = new TreeSet<Descending>((Descending left, Descending right) ->
				      Util.compareTo(left.x2, right.x2,
						     left.x1, right.x1,
						     left.tendency(), right.tendency(),
						     left.id, right.id));

	println("resolving diagonal collisions");
	
	int index = 0;
	for(var asc_line : asc_sorted_x1) {
	    int x1 = asc_line.x1;

	    while(index < dsc_sorted_x1.size() &&
		  dsc_sorted_x1.get(index).x1 <= x1) {
		open_set.add(dsc_sorted_x1.get(index));
		index++;
	    }

	    while(!open_set.isEmpty() && open_set.first().x2 < x1)
		open_set.pollFirst();

	    //check every line in the open set to see if it collides with the ascending line
	    collider: for(var dsc_line : dsc_sorted_x1) {//open_set) {
		//these lines are guaranteed to collide on the x scale
		//how to find out of they collide on y?
		//find out if there's a point on dsc which equals the tendency of y?
		//if asc.y1 < dsc.y1, no intersection

		var pair = diagonal_collision(asc_line, dsc_line);
		//TODO: also filter against orthogonal lines
		pair = filter_strata(pair, strata_ascending_colinear, strata_descending_colinear,
				     strata_horizontal, strata_vertical);
		if(pair != null)
		    collisions.add(pair);
	    }
	}

	open_set = null;
	println("num collisions (diag x diag): " + collisions.size());

	/**
	 * FIND HORIZONTAL COLLISIONS
	 */
	
	
	//find all the points where horizontal lines intersect eachother
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

	index = 0;
	
	//HashSet<IntPair> collisions = new HashSet<IntPair>();

	/* stratify all colinear overlaps */
	//var strata_horizontal = select_colinear_horizontal(horizontal_colinear_overlaps);
	//var strata_vertical = select_colinear_vertical(vertical_colinear_overlaps);
	
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
		    pair = filter_strata(pair, strata_ascending_colinear, strata_descending_colinear,
				     strata_horizontal, strata_vertical);
		    if(pair != null)
			collisions.add(pair);
		}		   
	    }
	}

	open_interval = null;
	println("num collisions (diag x diag) + (horiz x vert): " + collisions.size());


	/**
	 * Find all the points where an ascending line intersects a vertical line
	 */

	//interval = asc line (x1, x2)
	//need to have: ascending lines sorted on x1, x2
	//vertical lines sorted by x
	//
	/* asc_sorted_x1 */
	/* vertical_sorted_x */
	TreeSet<Ascending> open_av
	    = new TreeSet<Ascending>((Ascending left, Ascending right)->
				     Util.compareTo(left.x2, right.x2,
						    left.y1, right.y1,
						    left.id, right.id));

	index = 0;

	for(var vertical_line : vertical_sorted_x) {
	    int x = vertical_line.x;
	    
	    while(index < asc_sorted_x1.size()
		  && asc_sorted_x1.get(index).x1 <= x) {
		open_av.add(asc_sorted_x1.get(index));
		index++;
	    }
	    
	    while(!open_av.isEmpty() && open_av.first().x2 < x)
		open_av.pollFirst();

	    for(var asc_line : open_av) {
		//does it intersect? we know what the x has to be
		//which means we can calculate where the y has to be
		var tendency = asc_line.tendency();

		//tendency = y - x
		//tendency + x = y
		var y_target = tendency + x;

		if(vertical_line.y1 <= y_target && y_target <= vertical_line.y2) {
		    IntPair pair = new IntPair(x, y_target);
		    pair = filter_strata(pair, strata_ascending_colinear, strata_descending_colinear,
				     strata_horizontal, strata_vertical);
		    if(pair != null)
			collisions.add(pair);		    
		}
	    }
	}

	open_av = null;
	println("num collisions (diag x diag) + (horiz x vert) + (asc x vert): "
		+ collisions.size());

	/**
	 * Find all the points where a descending line intersects a vertical line
	 */
	/* dsc_sorted_x1 */
	/* vertical_sorted_x */
	TreeSet<Descending> open_dv
	    = new TreeSet<Descending>((Descending left, Descending right)->
				     Util.compareTo(left.x2, right.x2,
						    left.y1, right.y1,
						    left.id, right.id));

	index = 0;

	for(var vertical_line : vertical_sorted_x) {
	    int x = vertical_line.x;
	    
	    while(index < dsc_sorted_x1.size()
		  && dsc_sorted_x1.get(index).x1 <= x) {
		open_dv.add(dsc_sorted_x1.get(index));
		index++;
	    }

	    while(!open_dv.isEmpty() && open_dv.first().x2 < x)
		open_dv.pollFirst();

	    for(var dsc_line : open_dv) {
		//does it intersect? we know what the x has to be
		//which means we can calculate where the y has to be
		var tendency = dsc_line.tendency();

		//tendency = y + x
		//tendency - x = y
		var y_target = tendency - x;

		if(vertical_line.y1 <= y_target && y_target <= vertical_line.y2) {
		    IntPair pair = new IntPair(x, y_target);
		    pair = filter_strata(pair, strata_ascending_colinear, strata_descending_colinear,
				     strata_horizontal, strata_vertical);
		    if(pair != null)
			collisions.add(pair);		    
		}
	    }
	}

	open_dv = null;
	println("num collisions (diag x diag) + (horiz x vert) + (asc x vert) +"
		+ " (dsc x vert): "
		+ collisions.size());

	/**
	 * Find all the points where an ascending line intersects with a horizontal line
	 */

	//asc_sorted_y1
	var asc_sorted_y1 = new ArrayList<Ascending>(ascending);
	asc_sorted_y1.sort(((Ascending left, Ascending right)->
			    Util.compareTo(left.y1, right.y1,
					   left.y2, right.y2,
					   left.x1, right.x1,
					   left.id, right.id)));
			   
	var open_ah = new TreeSet<Ascending>((Ascending left, Ascending right)->
					     Util.compareTo(left.y2, right.y2,
							    left.y1, right.y1,
							    left.x1, right.x1,
							    left.id, right.id));

	/*horizontal_sorted_y*/
	index = 0;
	for(var horizontal_line : horizontal_sorted_y) {
	    int y = horizontal_line.y;

	    while(index < asc_sorted_y1.size() &&
		  asc_sorted_y1.get(index).y1 <= y) {
		open_ah.add(asc_sorted_y1.get(index));
		index++;
	    }

	    while(!open_ah.isEmpty() && open_ah.first().y2 < y)
		open_ah.pollFirst();

	    for(var asc_line : open_ah) {
		//does it intersect? we know what the x has to be
		//which means we can calculate where the y has to be
		var tendency = asc_line.tendency();

		//tendency = y - x
		//tendency + x = y
		//x = y - tendency
		//y - tendency = x
		var x_target = y - tendency;

		if(horizontal_line.x1 <= x_target && x_target <= horizontal_line.x2) {
		    IntPair pair = new IntPair(x_target, y);
		    pair = filter_strata(pair, strata_ascending_colinear, strata_descending_colinear,
				     strata_horizontal, strata_vertical);
		    if(pair != null)
			collisions.add(pair);		    
		}
	    }
	}

	open_ah = null;
	println("num collisions (diag x diag) + (horiz x vert) + (asc x vert) +"
		+ " (dsc x vert) + (asc x hori): "
		+ collisions.size());


	/**
	 * Find all descending x horizontal intersections
	 */
	index = 0;

	var dsc_sorted_y2 = new ArrayList<Descending>(descending);
	dsc_sorted_y2.sort(((Descending left, Descending right)->
			    Util.compareTo(left.y2, right.y2,
					   left.y1, right.y1,
					   left.x1, right.x1,
					   left.id, right.id)));
			   
	var open_dh = new TreeSet<Descending>((Descending left, Descending right)->
					     Util.compareTo(left.y1, right.y1,
							    left.y2, right.y2,
							    left.x1, right.x1,
							    left.id, right.id));

	
	for(var horizontal_line : horizontal_sorted_y) {
	    int y = horizontal_line.y;

	    while(index < dsc_sorted_y2.size() &&
		  dsc_sorted_y2.get(index).y2 <= y) {
		open_dh.add(dsc_sorted_y2.get(index));
		index++;
	    }

	    while(!open_dh.isEmpty() && open_dh.first().y1 < y)
		open_dh.pollFirst();

	    for(var dsc_line : open_dh) {
		//does it intersect? we know what the x has to be
		//which means we can calculate where the y has to be
		var tendency = dsc_line.tendency();

		//tendency = y + x
		//tendency - y = x		
		var x_target = tendency - y;

		if(horizontal_line.x1 <= x_target && x_target <= horizontal_line.x2) {
		    IntPair pair = new IntPair(x_target, y);
		    pair = filter_strata(pair, strata_ascending_colinear, strata_descending_colinear,
				     strata_horizontal, strata_vertical);
		    if(pair != null)
			collisions.add(pair);
		}
	    }
	}
	open_dh = null;
	println("num collisions (diag x diag) + (horiz x vert) + (asc x vert) +"
		+ " (dsc x vert) + (asc x hori) + (dsc x hori): "
		+ collisions.size());

	println("cursory sum: " + (collisions.size() + line_lazy_size + line_orthogonal_size));


	//now we need to remove 1 for every time a vertical colinear segment collided with
	//a horizontal colinear segment or diagonal colinear segment, etc
	
	var res = collisions.size() + line_lazy_size + line_orthogonal_size;
	//fortunately, we can do this recursively
	var removal = 0;
	if(horizontal_colinear_overlaps.size() > 0 ||
	   vertical_colinear_overlaps.size() > 0 ||
	   descending_colinear_overlaps.size() > 0 ||
	   ascending_colinear_overlaps.size() > 0) {
	    DEBUG(2, "recursively solving for depth " + (depth + 1) + "\n");

	    //we actually need to seperately do the diagxdiag, vertxhoriz, etc ones
	    //because we could have 3+ intersecting in the same place
	    var empty_horizontal = new ArrayList<Horizontal>();
	    var empty_vertical = new ArrayList<Vertical>();
	    var empty_ascending = new ArrayList<Ascending>();
	    var empty_descending = new ArrayList<Descending>();

	    //diag x diag
	    removal = solve_octogonal(empty_horizontal,
				      empty_vertical,
				      ascending_colinear_overlaps,
				      descending_colinear_overlaps,
				      depth+1);

	    removal += solve_octogonal(empty_horizontal,
				       vertical_colinear_overlaps,
				       empty_ascending,
				       descending_colinear_overlaps,
				       depth+1);

	    removal += solve_octogonal(horizontal_colinear_overlaps,
				       empty_vertical,
				       empty_ascending,
				       descending_colinear_overlaps,
				       depth+1);

	    removal += solve_octogonal(empty_horizontal,
				       vertical_colinear_overlaps,
				       ascending_colinear_overlaps,
				       empty_descending,
				       depth+1);

	    removal += solve_octogonal(horizontal_colinear_overlaps,
				       empty_vertical,
				       ascending_colinear_overlaps,
				       empty_descending,
				       depth+1);

	    removal += solve_octogonal(horizontal_colinear_overlaps,
				       vertical_colinear_overlaps,
				       empty_ascending,
				       empty_descending,
				       depth+1);
	    
	    
	    /*removal = solve_octogonal(horizontal_colinear_overlaps,
				       vertical_colinear_overlaps,
				      ascending_colinear_overlaps,
				      descending_colinear_overlaps,
				      depth + 1);*/
	    DEBUG(2, "finished solving for depth " + (depth + 1) + "\n");
	}

	res = res - removal;
	println("Size of overlap of colinear overlaps: " + removal);
	//println("collision size: " + collisions.size());

	return res;
    }
    
    private IntPair filter_strata(IntPair input,
				  TreeMap<Integer, ArrayList<Ascending>> asc_strata,
				  TreeMap<Integer, ArrayList<Descending>> dsc_strata,
				  TreeMap<Integer, ArrayList<Horizontal>> h_strata,
				  TreeMap<Integer, ArrayList<Vertical>> v_strata) {	
	if(input == null)
	    return input;	
	
	var selected_h = h_strata.get(input.Y);
	if(selected_h != null)
	    for(var hline : selected_h)
		if(input.Y == hline.y)
		    if(hline.x1 <= input.X && input.X <= hline.x2)
			return null;	
	
	var selected_v = v_strata.get(input.X);
	if(selected_v != null)
	    for(var vline : selected_v)
		if(input.X == vline.x)
		    if(vline.y1 <= input.Y && input.Y <= vline.y2)
			return null;
	
	
	int asc_tendency = input.Y - input.X;
	var selected_asc = asc_strata.get(asc_tendency);
	if(selected_asc != null)
	    for(var asc : selected_asc)
		if(asc.x1 <= input.X && input.X <= asc.x2)
		    return null;
	
	int dsc_tendency = input.Y + input.X;
	var selected_dsc = dsc_strata.get(dsc_tendency);
	if(selected_dsc != null)
	    for(var dsc : selected_dsc)
		if(dsc.x1 <= input.X && input.X <= dsc.x2)
		    return null;	
		
	return input;
    }
    
    private IntPair diagonal_collision(Ascending asc, Descending dsc) {	
	//uneven lines can never intersect
	if(Math.abs(asc.tendency() + dsc.tendency())%2 == 1)
	  return null;

	var p0_x = asc.x1;
	var p0_y = asc.y1;

	var p1_x = asc.x2;
	var p1_y = asc.y2;

	var p2_x = dsc.x1;
	var p2_y = dsc.y1;

	var p3_x = dsc.x2;
	var p3_y = dsc.y2;

	
	float s1_x, s1_y, s2_x, s2_y;
	s1_x = p1_x - p0_x;     s1_y = p1_y - p0_y;
	s2_x = p3_x - p2_x;     s2_y = p3_y - p2_y;	
	
	float s, t;
	s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / (-s2_x * s1_y + s1_x * s2_y);
	t = ( s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / (-s2_x * s1_y + s1_x * s2_y);

	if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
	    Integer x = Math.round(p0_x + (t * s1_x));
	    Integer y = Math.round(p0_y + (t * s1_y));
	    //printf("X, Y: %d, %d%n", x, y);

	    return new IntPair((int)x, (int)(y));
	}
			
	return null;
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
	/*for(var line : vertical_colinear_overlaps)
	  println("v_segments: " + line);*/

	
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
	public void add(Ascending a) {
	    add(a.y1, a.y2);
	}
	public void add(Descending d) {
	    add(d.x1, d.x2);
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

	public ArrayList<Descending> to_descending(int tendency) {
	    ArrayList<Descending> components = new ArrayList<>();

	    //tendency = y1 + x1
	    //tendency - y1 = x1
	    int sum = tendency;
	    for(var range: ranges) {
		int x1 = range.open;
		int y1 = tendency - x1;
		int x2 = range.close;
		int y2 = tendency - x2;
		components.add(new Descending(x1, x2, y1, y2));
	    }

	    return components;
	}

	public ArrayList<Ascending> to_ascending(int tendency) {
	    ArrayList<Ascending> components = new ArrayList<>();

	    //tendency = y1 - x1
	    //tendency + x1 = y1
	    //y1 - tendency = x1;
	    //range is the y1 values	    
	    for(var range: ranges) {		
		int y1 = range.open;
		int x1 = y1 - tendency;		
		int y2 = range.close;
		int x2 = y2 - tendency;
		components.add(new Ascending(x1, x2, y1, y2));
	    }

	    return components;
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

    private ArrayList<Descending> intersections_descending(ArrayList<Descending> colinear) {
	if(colinear.size() < 2)
	    return new ArrayList<>();
	
	ArrayList<Descending> intersections = new ArrayList<Descending>();

	int tendency = colinear.get(0).y1 + colinear.get(0).x1;
	//println("tendency: " + tendency);

	for(int a = 0; a < colinear.size(); a++) {
	    var left = colinear.get(a);
	    for(int b = a + 1; b < colinear.size(); b++) {
		var right = colinear.get(b);

		//1, 2, 3
		if(left.x1 <= right.x1) {
		    //1
		    if(left.x2 < right.x1) continue;
		    if(left.x2 < right.x2) intersections.add(new Descending(right.x1, left.x2,
									    right.y1, left.y2));
		    else intersections.add(new Descending(right.x1, right.x2,
							  right.y1, right.y2));
		}
		//4, 5
		else if(left.x1 <= right.x2) {
		    if(left.x2 <= right.x2) intersections.add(new Descending(left.x1, left.x2,
									     left.y1, left.y2));
		    else intersections.add(new Descending(left.x2, right.x2,
							  left.y2, right.y2));
		}
	    }
	}
	
	return intersections;
    }
    
    private ArrayList<Ascending> intersections_ascending(ArrayList<Ascending> colinear) {
	if(colinear.size() < 2)
	    return new ArrayList<>();

	ArrayList<Ascending> intersections = new ArrayList<Ascending>();

	int tendency = colinear.get(0).y1 - colinear.get(0).x1;
	//println("tendency: " + tendency);
	for(int a = 0; a < colinear.size(); a++) {
	    var left = colinear.get(a);
	    for(int b = a + 1; b < colinear.size(); b++) {
		var right = colinear.get(b);

		//1, 2, 3
		if(left.y1 <= right.y1) {
		    //1
		    if(left.y2 < right.y1) continue;
		    if(left.y2 < right.y2) intersections.add(new Ascending(right.x1, left.x2,
									   right.y1, left.y2));
		    else intersections.add(new Ascending(right.x1, right.x2,
							 right.y1, right.y2));
		}
		//4, 5
		else if(left.y1 <= right.y2) {
		    if(left.y2 <= right.y2) intersections.add(new Ascending(left.x1, left.x2,
									    left.y1, left.y2));
		    else intersections.add(new Ascending(left.x2, right.x2,
							 left.y2, right.y2));
		}
	    }
	}
	
	return intersections;
    }
    
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


    private TreeMap<Integer, ArrayList<Descending>>
	select_colinear_descending(AbstractCollection<Descending> descending) {
	TreeMap<Integer, ArrayList<Descending>> colinear = new TreeMap<>();

	for(var line : descending) {
	    var key = line.y1 + line.x1;
	    if(!colinear.containsKey(key))
		colinear.put(key, new ArrayList<>());
	    colinear.get(key).add(line);
	}

	return colinear;
    }
    
    private TreeMap<Integer, ArrayList<Ascending>>
	select_colinear_ascending(AbstractCollection<Ascending> ascending) {
	TreeMap<Integer, ArrayList<Ascending>> colinear = new TreeMap<>();

	for(var line : ascending) {
	    var key = line.y1 - line.x1;
	    if(!colinear.containsKey(key))
		colinear.put(key, new ArrayList<>());
	    colinear.get(key).add(line);
	}

	return colinear;
    }
    
    private TreeMap<Integer, ArrayList<Horizontal>>
	select_colinear_horizontal(AbstractCollection<Horizontal> horizontal) {
	TreeMap<Integer, ArrayList<Horizontal>> colinear = new TreeMap<>();

	for(var line : horizontal) {
	    if(!colinear.containsKey(line.y))
		colinear.put(line.y, new ArrayList<>());
	    colinear.get(line.y).add(line);
	}

	return colinear;
    }

    private TreeMap<Integer, ArrayList<Vertical>>
	select_colinear_vertical(AbstractCollection<Vertical> vertical) {
	TreeMap<Integer, ArrayList<Vertical>> colinear = new TreeMap<>();

	for(var line : vertical) {
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
