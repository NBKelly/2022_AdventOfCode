package com.nbkelly.advent;

/* imports */
import com.nbkelly.drafter.Drafter;
import com.nbkelly.drafter.Command;
import com.nbkelly.drafter.FileCommand;
import com.nbkelly.drafter.Timer;

/* imports from file */
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
public class Advent2022_18 extends Drafter {
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

	TreeSet<Point> points = new TreeSet<>();
	for(var line : lines) {
	    var sp = line.split(",");
	    Point p = new Point(Integer.parseInt(sp[0]),
				Integer.parseInt(sp[1]),
				Integer.parseInt(sp[2]));
	    points.add(p);
	}

	DEBUGF(1, "PART ONE: ");
	println(unmergedFaces(points));


	/* todo - we can actually partition the set on a given dimension(ie x)
	   if there is no value in plane x+1, the the whole set can be partitioned on x+1 */

	/* the treeset should be sorted primarily on X, then Y, then Z */
	var res = 0l;

	/* note - this destroys points */
	var parted = xyzParted(points);

	for(var part : parted)
	    res += exteriorFaces(part);

	DEBUGF(1, "PART TWO: ");
	println(res);

        /* visualize output here */
        generate_output();

	return DEBUG(1, t.split("Finished Processing"));
    }

    private ArrayList<TreeSet<Point>> xyzParted(TreeSet<Point> points) {
	var xp = xParted(points);
	var yp = xp.parallelStream().map(x -> yParted(x))
	    .flatMap(y -> y.stream())
	    .collect(Collectors.toList());
	var zp = yp.parallelStream().map(y -> zParted(y))
	    .flatMap(z -> z.stream())
	    .collect(Collectors.toList());

	DEBUG(2, zp);

	return new ArrayList<TreeSet<Point>>(zp);
    }

    private ArrayList<TreeSet<Point>> xParted(TreeSet<Point> points) {
	ArrayList<TreeSet<Point>> res = new ArrayList<>();

	TreeSet<Point> part = new TreeSet<>();
	while(points.size() != 0) {
	    if(part.size() == 0 || points.first().x <= (part.last().x + 1))
		part.add(points.pollFirst());
	    else {
		res.add(part);
		part = new TreeSet<>();
	    }
	}

	if(part.size() != 0)
	    res.add(part);

	return res;
    }

    private ArrayList<TreeSet<Point>> yParted(TreeSet<Point> points) {
	ArrayList<TreeSet<Point>> res = new ArrayList<>();

	var yset = new TreeSet<Point>((a, b) -> Util.compareTo(a.y, b.y, a.z, b.z,a.x, b.x));
	yset.addAll(points);
	points = yset;

	TreeSet<Point> part = new TreeSet<>((a, b) -> Util.compareTo(a.y, b.y, a.z, b.z,a.x, b.x));
	while(points.size() != 0) {
	    if(part.size() == 0 || points.first().y <= (part.last().y + 1))
		part.add(points.pollFirst());
	    else {
		res.add(part);
		part = new TreeSet<>();
	    }
	}

	if(part.size() != 0)
	    res.add(part);

	return res;
    }

    private ArrayList<TreeSet<Point>> zParted(TreeSet<Point> points) {
	ArrayList<TreeSet<Point>> res = new ArrayList<>();

	var zset = new TreeSet<Point>((a, b) -> Util.compareTo(a.z, b.z, a.y, b.y, a.x, b.x));
	zset.addAll(points);
	points = zset;

	TreeSet<Point> part = new TreeSet<>((a, b) -> Util.compareTo(a.z, b.z, a.y, b.y, a.x, b.x));
	while(points.size() != 0) {
	    if(part.size() == 0 || points.first().z <= (part.last().z + 1))
		part.add(points.pollFirst());
	    else {
		//flush the current set of points, then clear part
		res.add(part);
		part = new TreeSet<>();
	    }
	}

	if(part.size() != 0)
	    res.add(part);

	return res;
    }

    private long exteriorFaces(TreeSet<Point> points) {
	/* given a slice of the set, count all exterior faces for that slice */

	int mx= points.first().x, sx = points.first().x;
	int my= points.first().x, sy = points.first().y;
	int mz= points.first().x, sz = points.first().z;

	for(var p : points) {
	    mx = Math.max(mx, p.x + 1);
	    my = Math.max(my, p.y + 1);
	    mz = Math.max(mz, p.z + 1);
	    sx = Math.min(sx, p.x-1);
	    sy = Math.min(sy, p.y-1);
	    sz = Math.min(sz, p.z-1);
	}

	DEBUGF("Searching in range: (%d,%d,%d) -> (%d,%d,%d)%n",sx+1,sy+1,sz+1,mx-1,my-1,mz-1);
	TreeSet<Point> steam = new TreeSet<Point>();
	Point start = new Point(sx, sy, sz);
	LinkedList<Point> queue = new LinkedList<Point>();
	queue.add(start);

	int ext_faces = 0;
	while(queue.size() > 0) {
	    var current = queue.pollFirst();

	    if(!withinBounds(sx, mx, sy, my, sz, mz, current))
		continue;
	    if(steam.contains(current))
		continue;
	    steam.add(current);
	    for(var n : neighbors(current)) {
		if(points.contains(n))
		    ext_faces++;
		else if(!steam.contains(n))
		    queue.add(n);
	    }
	}

	return ext_faces;
    }

    private long unmergedFaces(TreeSet<Point> points) {
	return points.parallelStream()
	    .map(p -> (neighbors(p).stream().filter(n -> !points.contains(n)).count()))
	    .reduce(0l, (a,b) -> a + b);
    }

    private ArrayList<Point> neighbors(Point p) {
	ArrayList<Point> neighbors = new ArrayList<>();
	neighbors.add(new Point(p.x-1, p.y, p.z));
	neighbors.add(new Point(p.x+1, p.y, p.z));
	neighbors.add(new Point(p.x, p.y-1, p.z));
	neighbors.add(new Point(p.x, p.y+1, p.z));
	neighbors.add(new Point(p.x, p.y, p.z-1));
	neighbors.add(new Point(p.x, p.y, p.z+1));
	return neighbors;
    }

    private class Point implements Comparable<Point>{
	int x;
	int y;
	int z;

	public String toString() {
	    return String.format("(%d,%d,%d)", x, y, z);
	}

	public Point(int x, int y, int z) {
	    this.x = x;
	    this.y = y;
	    this.z = z;
	}

	public int compareTo(Point p) {
	    return Util.compareTo(x, p.x, y, p.y, z, p.z);
	}

	public boolean equals(Object o) {
	    if(o instanceof Point)
		return compareTo((Point)o) == 0;
	    return false;
	}
    }

    private boolean withinBounds(int sx, int mx,
				 int sy, int my,
				 int sz, int mz,
				 Point p) {
	return p.x >= sx && p.x <= mx &&
	    p.y >= sy && p.y <= my &&
	    p.z >= sz && p.z <= mz;
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
        new Advent2022_18().run(argv);
    }
}
