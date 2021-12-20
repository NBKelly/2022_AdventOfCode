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

/* my imported libs */
import com.nbkelly.lib.Util;
import com.nbkelly.drafter.BooleanCommand; //visualize cmd
import com.nbkelly.lib.Image; //visualizer lib
import com.nbkelly.lib.HashCounter;

import com.nbkelly.lib.IntPair;
import com.nbkelly.lib.pathfinder.Map;
import com.nbkelly.lib.Pair;

import java.math.BigInteger;

import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import java.util.stream.IntStream;

/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2021_19 extends Drafter {
    /* WORKFLOW:
     *  set all needed commands with setCommands()
     *  post-processing can be performed with actOnCommands()
     *  the rest of your work should be based around the solveProblem() function
     */
    /* params injected from file */
    ArrayList<String> lines;
    
    //generate output
    boolean generate_output = false;

    class Scanner {
	HashSet<Point> origins = new HashSet<Point>();
	String id = null;
	private HashSet<Point> triangles = null;	

	HashSet<Point> points = new HashSet<Point>();

	public synchronized HashSet<Point> triangles() {
	    if(triangles == null) {
		triangles = new HashSet<>();

		ArrayList<Point> li = new ArrayList<Point>(points);
		for(int x = 0; x < li.size(); x++)
		    for(int y = x+1; y < li.size(); y++)
			for(int z = y+1; z < li.size(); z++) {
			    var p1 = li.get(x);
			    var p2 = li.get(y);
			    var p3 = li.get(z);

			    var d1 = p1.manhattan(p2);
			    var d2 = p1.manhattan(p3);
			    var d3 = p2.manhattan(p3);

			    triangles.add(sorted(d1, d2, d3));
			}
	    }

	    return new HashSet<Point>(triangles);
	}	
	
	public Scanner (String id) {
	    this.id = id;
	    origins.add(new Point(0, 0, 0));
	}	
	
	public void add(Point p) {
	    points.add(p);
	    triangles = null;
	}

	public Scanner rotated(int orient) {
	    Scanner rot = new Scanner("rot " + orient + " " + id);
	    for(var point : points)		
		rot.add(point.getSystem(orient));

	    /* rotate all origins */
	    for(var point : origins)
		rot.origins.add(point.getSystem(orient));

	    rot.triangles = triangles;
	    
	    return rot;
	}

	public Scanner displaced(Point p) {
	    Scanner disp = new Scanner("disp " + p + " " + id);
	    for(var point : points)
		disp.add(point.displace(p));

	    for(var origin : origins)
		disp.origins.add(origin.displace(p));

	    disp.triangles = triangles;
	    
	    return disp;
	}
    }

    public Point sorted(int x, int y, int z) {
	ArrayList<Integer> li = new ArrayList<Integer>();

	li.add(x);
	li.add(y);
	li.add(z);
	Collections.sort(li);

	return new Point(li.get(0), li.get(1), li.get(2));
    }
    
    private class Point implements Comparable<Point> {
	int x;
	int y;
	int z;
	
	public int compareTo(Point p) {
	    return Util.compareTo(x, p.x,
				  y, p.y,
				  z, p.z);
	}
	
	public Integer manhattan(Point p) {
	    return
		Math.abs(x-p.x) +
		Math.abs(y-p.y) +
		Math.abs(z-p.z);
	}

	public Point(int x, int y, int z) {
	    this.x = x;
	    this.y = y;
	    this.z = z;
	}

	public Point difference(Point p) {
	    return new Point(x - p.x, y - p.y, z - p.z);
	}

	public Point displace(Point p) {
	    return displace(p.x, p.y, p.z);
	}
	
	public Point displace(int x, int y, int z) {
	    return new Point(this.x - x, this.y - y, this.z - z);
	}

	public String toString() {
	    return String.format("%d,%d,%d", x, y, z);
	}

	public Integer manhattan() {
	    return
		Math.abs(x) +
		Math.abs(y) +
		Math.abs(z) ;
	}

	public boolean equals(Point p) {
	    return p.x == x
		&& p.y == y
		&& p.z == z;
	}
	
	public Point getSystem(int i) {
	    switch(i) {
	    case 0: return new Point(x,y,z);
	    case 1: return new Point(-x,-y,z);
	    case 2: return new Point(-x,y,-z);
	    case 3: return new Point(x,-y,-z);

	    case 4: return new Point(-x,z,y);
	    case 5: return new Point(x,-z,y);
	    case 6: return new Point(x,z,-y);
	    case 7: return new Point(-x,-z,-y);

	    case 8: return new Point(-y,x,z);
	    case 9: return new Point(y,-x,z);
	    case 10: return new Point(y,x,-z);
	    case 11: return new Point(-y,-x,-z);
	    
	    case 12: return new Point(y,z,x);
	    case 13: return new Point(-y,-z,x);
	    case 14: return new Point(-y,z,-x);
	    case 15: return new Point(y,-z,-x);
		
	    case 16: return new Point(z,x,y);
	    case 17: return new Point(-z,-x,y);
	    case 18: return new Point(-z,x,-y);
	    case 19: return new Point(z,-x,-y);
		
	    case 20: return new Point(-z,y,x);
	    case 21: return new Point(z,-y,x);
	    case 22: return new Point(z,y,-x);
	    case 23: return new Point(-z,-y,-x);
	    }	
	    
	    return null;
	}

	public boolean equals(Object o) {
	    if(o instanceof Point)
		return equals((Point)o);
	    return false;
	}
	
	@Override public int hashCode() {
	    return (1000*1000*x) + (1000*y) + (z);
	}
    }

    private LinkedList<Scanner> readScanners(ArrayList<String> lines) {
	LinkedList<Scanner> scanners = new LinkedList<>();

	Scanner ct = null;
	
	for(var line : lines) {
	    if(line.length() == 0) {
		ct = null;
	    }
	    else if(line.startsWith("---")) {
		/* get the specific scanner */
		ct = new Scanner(line);
		scanners.add(ct);
		DEBUGF(2, "added scanner %s%n", ct.id);
	    }
	    else {
		var split = line.split(",");
		var point = new Point(Integer.parseInt(split[0]),
				      Integer.parseInt(split[1]),
				      Integer.parseInt(split[2]));
		DEBUGF(3, "  with point %s%n", point);
		ct.add(point);
	    }
	}

	return scanners;
    }

    public Scanner matchingBasis(int basis, Scanner unsolved_cloud, Scanner left) {
	var rotated = unsolved_cloud.rotated(basis);

	HashCounter<Point> points = new HashCounter<Point>();
			    
	for(var leftpoint : left.points)
	    for(var rightpoint : rotated.points)
		points.add(rightpoint.difference(leftpoint));
	
	var max = points.max();
	if(points.count(max) >= 12)
	    return rotated.displaced(max);

	return null;	
    }
    
    /* solve problem here */
    @Override public int solveProblem() throws Exception {
	Timer t = makeTimer();

	var unsolved = readScanners(lines);
	LinkedList<Scanner> solved = new LinkedList<Scanner>();
	solved.add(unsolved.pollFirst());

	DEBUG(1, t.split("Input parsed"));
	
	int iteration = 1;

	while(unsolved.size() > 0) {
	    final var remove = new ConcurrentLinkedQueue<Scanner>();
	    final var add = new ConcurrentLinkedQueue<Scanner>();

	    unsolved.parallelStream().forEach(unsolved_cloud -> {
		    var triangles_unsolved = unsolved_cloud.triangles();
		    outer: for(var left : solved) {
			//compare the triangles for left and right
			var triangles_left = left.triangles();
			triangles_left.retainAll(triangles_unsolved);

			if(triangles_left.size() < 200)
			    continue;

			/* this is guaranteed to be a valid matching in at least one orientation */
			/* note that 12 choose 3 is 220 */

			var aligned = IntStream.range(0, 24)
			    .parallel()
			    .mapToObj(basis -> matchingBasis(basis, unsolved_cloud, left))
			    .filter(x -> x != null)
			    .findAny()
			    .orElse(null);
			
			if(aligned != null) {
			    add.add((Scanner)aligned);
			    remove.add(unsolved_cloud);
			    break outer;
			}
		    }
		});
	    
	    unsolved.removeAll(remove);
	    solved.addAll(add);
	    
	    DEBUGF(2, "iteration %d solved %d unsolved %d%n", iteration++,
		   solved.size(), unsolved.size());
	}
 
	DEBUG(1, t.split("Space mapped"));
    
	/* assemble the solution */
	HashSet<Point> beacons = new HashSet<Point>();
	HashSet<Point> origins = new HashSet<Point>();

	
	for(var scanner : solved) {
	    beacons.addAll(scanner.points);
	    origins.addAll(scanner.origins);
	}
	
	int max_dist = 0;
	for(var left :origins)
	    for(var right : origins)
		max_dist = Math.max(max_dist, left.manhattan(right));
	
	DEBUGF(1, "PART ONE: "); println(beacons.size());
	DEBUGF(1, "PART TWO: "); println(max_dist);
        
        /* visualize output here */
        generate_output(beacons, origins);
					 
	return DEBUG(1, t.total());
    }

    
    /* code injected from file */
    public void generate_output(HashSet<Point> beacons, HashSet<Point> origins) throws Exception {
	if(!generate_output)
	    return;
	
	println(">generating output");
		
	/* find min_x, min_y, min_z,
	   max_x, max_y, max_z */
	int min_x = 99999;
	int min_y = 99999;
	int min_z = 99999;
	int max_x = -99999;
	int max_y = -99999;
	int max_z = -99999;

	for(var point : beacons) {
	    if(point.x < min_x)
		min_x = point.x;
	    if(point.y < min_y)
		min_y = point.y;
	    if(point.z < min_z)
		min_z = point.z;
	    if(point.x > max_x)
		max_x = point.x;
	    if(point.y > max_y)
		max_y = point.y;
	    if(point.z > max_z)
		max_z = point.z;
	}

	for(var point : origins) {
	    if(point.x < min_x)
		min_x = point.x;
	    if(point.y < min_y)
		min_y = point.y;
	    if(point.z < min_z)
		min_z = point.z;
	    if(point.x > max_x)
		max_x = point.x;
	    if(point.y > max_y)
		max_y = point.y;
	    if(point.z > max_z)
		max_z = point.z;
	}

	printf(">Range: (%d,%d,%d) -> (%d,%d,%d)%n",
	       min_x, min_y, min_z,
	       max_x, max_y, max_z);

	int scale = 4;

	int width = (max_x-min_x)/scale;
	int height = (max_y-min_y)/scale;

	var z_thresh_1 = (max_z-min_z);
	var z_thresh_2 = z_thresh_1/2;
	var z_thresh_3 = z_thresh_2/2;
	var z_thresh_4 = z_thresh_2 + z_thresh_3;
	
	Image i = new Image(width+250, height+250);

	for(var point : beacons) {
	    var z = 1 + (point.z - min_z) / (3*scale);
	    var x = z + (point.x - min_x) / (scale);
	    var y = z + (point.y - min_y) / (scale);

	    var y2 = y + z;
	    var x2 = x + z;

	    var z_check = (point.z - min_z);
	    if(z_check < z_thresh_3)
		i.rect(Image.H7, x, y, 6, 6);
	    else if(z_check < z_thresh_2)
		i.rect(Image.H5, x, y, 6, 6);
	    else if(z_check < z_thresh_4)
		i.rect(Image.H3, x, y, 6, 6);
	    else
		i.rect(Image.H1, x, y, 6, 6);
	}

	for(var point : origins) {
	    var z = 1 + (point.z - min_z) / (3*scale);
	    var x = z + (point.x - min_x) / (scale);
	    var y = z + (point.y - min_y) / (scale);

	    var y2 = y + z;
	    var x2 = x + z;

	    var z_check = (point.z - min_z);
	    i.rect(Image.H11, x, y, 12, 12);
	    /*else if(z_check < z_thresh_2)
		i.rect(Image.H5, x, y, 6, 6);
	    else if(z_check < z_thresh_4)
		i.rect(Image.H3, x, y, 6, 6);
	    else
	    i.rect(Image.H1, x, y, 6, 6);*/
	}

	i.savePNG("out1.png");
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
	new Advent2021_19().run(argv);
    }
}
