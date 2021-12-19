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

import java.util.Collections;

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
	TreeSet<Point> origins = new TreeSet<Point>((left, right) ->
						    Util.compareTo(left.x, right.x,
								   left.y, right.y,
								   left.z, right.z));
	
	String id = null;
	public Scanner (String id) {
	    this.id = id;
	    origins.add(new Point(0, 0, 0));
	}
	
	TreeSet<Point> points = new TreeSet<Point>((left, right) ->
						   Util.compareTo(left.x, right.x,
								  left.y, right.y,
								  left.z, right.z));
	public void add(Point p) {
	    points.add(p);
	}

	public Scanner rotated(int orient) {
	    Scanner rot = new Scanner("rot " + orient + " " + id);
	    for(var point : points)		
		rot.add(point.getSystem(orient));

	    /* rotate all origins */
	    for(var point : origins)
		rot.origins.add(point.getSystem(orient));
	    
	    return rot;
	}

	public Scanner displaced(Point p) {
	    Scanner disp = new Scanner("disp " + p + " " + id);
	    for(var point : points)
		disp.add(point.displace(p));

	    for(var origin : origins)
		disp.origins.add(origin.displace(p));
	    
	    return disp;
	}
    }

    private class Point {
	int x;
	int y;
	int z;
	
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
	    case 0: return new Point(x, y, z);
	    case 1: return new Point(x, -y, -z);
	    case 2: return new Point(x, -z, y);
	    case 3: return new Point(x, -z, -y);

	    case 4: return new Point(y, x, -z);
	    case 5: return new Point(y, z, x);
	    case 6: return new Point(y, -x, z);
	    case 7: return new Point(y, -z, -x);

	    case 8: return new Point(z, x, y);
	    case 9: return new Point(z, x, -y);
	    case 10: return new Point(z, y, -x);
	    case 11: return new Point(z, -y, x);

	    case 12: return new Point(-x, y, -z);
	    case 13: return new Point(-x, z, y);
	    case 14: return new Point(-x, z, -y);
	    case 15: return new Point(-x, -y, z);

	    case 16: return new Point(-y, x, z);
	    case 17: return new Point(-y, z, -x);
	    case 18: return new Point(-y, -x, -z);
	    case 19: return new Point(-y, -z, x);

	    case 20: return new Point(-z, y, x);
	    case 21: return new Point(-z, -x, y);
	    case 22: return new Point(-z, -x, -y);
	    case 23: return new Point(-z, -y, -x);
	    }

	    return null;
	}
    }

    private ArrayList<Scanner> readScanners(ArrayList<String> lines) {
	ArrayList<Scanner> scanners = new ArrayList<>();

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
    
    /* solve problem here */
    @Override public int solveProblem() throws Exception {
	Timer t = makeTimer();

        /* code injected from file */
        //var ints = Util.toIntList(lines);
	ArrayList<Scanner> scanners = readScanners(lines);
		
        println(">Good Morning!");

	while(scanners.size() > 1) {
	    /* pick a scanner - see how it lines up */
	    outer: for(var from: scanners)
		/* for every other scanner, see if we can find an alignment that overlaps */
		for(var to : scanners) {
		    if(from == to)
			continue;

		    /* this will need to be computed for each orientation */
		    for(int orient = 0; orient < 24; orient++) {
			var oriented_scanner = to.rotated(orient);
			
			/* for each point in this scanner, choose a point in the other scanner */
			/* note that this time complexity is NOG^2, so fuck you for the final one */
			//there needs to be 12 overlaps, so only n-11 origins need to be checked
			for(var origin_point : from.points) {
			    var search = new ArrayList<>(oriented_scanner.points)
				.subList(0, oriented_scanner.points.size() - 11);
			    for(var destination_point : search) {
				/* get the distance to displace */
				var diff = destination_point.difference(origin_point);
				var displaced = oriented_scanner.displaced(diff);
				
				/* now do a pointwise comparison to see if we can make it work */
				int matches = 0;		    
				for(var left : from.points)
				    for(var right: displaced.points) {
					if(left.equals(right))
					    matches++;
					if(matches == 12)
					    break;
				    }
				
				
				if(matches >= 12) {
				    scanners.remove(from);
				    scanners.remove(to);
				    Scanner newScanner =
					new Scanner(String.format("new scanner(%s, %s)",
								  from.id,
								  to.id));

				    newScanner.points.addAll(displaced.points);
				    newScanner.points.addAll(from.points);
				    newScanner.origins.addAll(displaced.origins);
				    newScanner.origins.addAll(from.origins);
				    scanners.add(newScanner);

				    /* find the difference between these two points */
				    break outer;
				}
			    }
			}
		    }
		}

	    /* size of final one */
	    DEBUG(2, "remaining sets: " + scanners.size());
	}
	
	var last = scanners.get(0);
	int max_dist = 0;
	var origins = last.origins;
	for(var left : origins)
	    for(var right : origins) {
		var dist = left.manhattan(right);
		max_dist = Math.max(dist, max_dist);		
	    }
	
	DEBUGF(1, "PART ONE: "); println(scanners.get(0).points.size());
	DEBUGF(1, "PART TWO: "); println(max_dist);//todo
        
        /* visualize output here */
        generate_output();

					 
	return DEBUG(1, t.split("Finished Processing"));
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
        new Advent2021_19().run(argv);
    }
}
