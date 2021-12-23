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
import java.util.Collections;

/* my imported libs */
import com.nbkelly.lib.Util;
import com.nbkelly.drafter.BooleanCommand; //visualize cmd
import com.nbkelly.lib.Image; //visualizer lib
import com.nbkelly.drafter.IntCommand; //visualize cmd

import com.nbkelly.lib.IntPair;
import com.nbkelly.lib.pathfinder.Map;
import com.nbkelly.lib.Pair;

import java.math.BigInteger;
import java.util.stream.Collectors;

/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2021_22 extends Drafter {
    /* WORKFLOW:
     *  set all needed commands with setCommands()
     *  post-processing can be performed with actOnCommands()
     *  the rest of your work should be based around the solveProblem() function
     */
    
    /* params injected from file */
    ArrayList<String> lines;
    
    //generate output
    boolean generate_output = false;

    int num_dimensions = 3;
    
    private class K_Oblique {
	boolean active;
	ArrayList<IntPair> edges = new ArrayList<IntPair>();

	public K_Oblique(ArrayList<IntPair> edges) {
	    this.edges.addAll(edges);
	}
	
	public K_Oblique(boolean active, ArrayList<IntPair> edges) {
	    this.edges.addAll(edges);
	    this.active = active;
	}

	public BigInteger volume() {
	    BigInteger res = BigInteger.ONE;
	    for(var edge: edges)
		res = res.multiply(BigInteger.valueOf(edge.Y - edge.X + 1));

	    return res;
	}

	public K_Oblique confined(int min, int max) {
	    ArrayList<IntPair> confined = new ArrayList<>();
	    for(var edge : edges) {
		if(edge.X > max || edge.Y < min)
		    return null;
		confined.add(new IntPair(Math.max(min, edge.X), Math.min(max, edge.Y)));
	    }

	    return new K_Oblique(active, confined);
	}

	public ArrayList<K_Oblique> resultant(K_Oblique sample) {
	    if(sample.edges.size() != edges.size())
		return null;

	    ArrayList<K_Oblique> res = new ArrayList<>();
	    
	    /* check for non-overlapping obliques */
	    for(int i = 0; i < edges.size(); i++) {
		var left = edges.get(i);
		var right = sample.edges.get(i);

		if(left.Y < right.X ||
		   right.Y < left.X) {
		    res.add(sample);
		    return res;
		}
	    }

	    for(int i = 0; i < edges.size(); i++) {
		/* note : confine every edge before this one */
		var left = edges.get(i);
		var right = sample.edges.get(i);

		if(right.X < left.X) {
		    ArrayList<IntPair> new_edges = new ArrayList<IntPair>();
		    for(int pre = 0; pre < i; pre++) {
			var ll = edges.get(pre);
			var rr = sample.edges.get(pre);
			
			new_edges.add(new IntPair(Math.max(ll.X, rr.X), Math.min(ll.Y, rr.Y)));
		    }
		    
		    IntPair new_edge = new IntPair(right.X, left.X - 1);
		    new_edges.add(new_edge);

		    for(int post = i+1; post < edges.size(); post++)
			new_edges.add(sample.edges.get(post));

		    res.add(new K_Oblique(new_edges));
		}
	    

		if(right.Y > left.Y) {
		    ArrayList<IntPair> new_edges = new ArrayList<IntPair>();
		    for(int pre = 0; pre < i; pre++) {
			var ll = edges.get(pre);
			var rr = sample.edges.get(pre);
			
			new_edges.add(new IntPair(Math.max(ll.X, rr.X), Math.min(ll.Y, rr.Y)));
		    }
		    
		    IntPair new_edge = new IntPair(left.Y+1, right.Y);
		    
		    new_edges.add(new_edge);
		    
		    for(int post = i+1; post < edges.size(); post++)
			new_edges.add(sample.edges.get(post));
		    
		    res.add(new K_Oblique(new_edges));
		}
	    }
	    
	    return res;
	}
    }
    
    private class Cube {
	int x1;	int x2;
	int y1;	int y2;
	int z1;	int z2;
	boolean active;

	/* FUCK YOU JAVA FOR NOT HAVING AN OPTIONAL SYNTAX */
	public Cube(int x1, int x2, int y1, int y2, int z1, int z2) {
	    this.x1 = x1; this.x2 = x2; this.y1 = y1;
	    this.y2 = y2; this.z1 = z1; this.z2 = z2;
	}

	public Cube(int x1, int x2, int y1, int y2, int z1, int z2, boolean active) {
	    this.x1 = x1; this.x2 = x2; this.y1 = y1;
	    this.y2 = y2; this.z1 = z1; this.z2 = z2;
	    this.active = active;
	}

	/* confine a cube to another cube */
	public Cube confined(int xmin, int xmax, int ymin, int ymax, int zmin, int zmax) {
	    if(x2 < xmin || y2 < ymin || z2 < zmin ||
	       x1 > xmax || y1 > ymax || z1 > zmax)
		return null;

	    return new Cube(Math.max(xmin, x1), Math.min(xmax, x2),
			    Math.max(ymin, y1), Math.min(ymax, y2),
			    Math.max(zmin, z1), Math.min(zmax, z2),
			    active);
	}

	/* find the resultant sample cubes that do not intersect this cube */
	public ArrayList<Cube> resultantCubes(Cube sample) {
	    ArrayList<Cube> res = new ArrayList<Cube>();

	    /* all cases where the cubes are NOT intersecting */
	    if(sample.x2 < x1 || sample.y2 < y1 || sample.z2 < z1 ||
	       x2 < sample.x1 || y2 < sample.y1 || z2 < sample.z1) {
		res.add(sample);
		return res;
	    }

	    /* top zone - note top and bot will be full slices */
	    if(sample.z1 < z1)
		res.add(new Cube(sample.x1, sample.x2,
				 sample.y1, sample.y2,
				 sample.z1, z1 - 1));

	    /* bot zone */
	    if(sample.z2 > z2)
		res.add(new Cube(sample.x1, sample.x2,
				 sample.y1, sample.y2,
				 z2+1, sample.z2));

	    /* front zone: not we can't include the z overlap */
	    if(sample.y1 < y1)
		res.add(new Cube(sample.x1, sample.x2,
				 sample.y1, y1-1,
				 Math.max(z1, sample.z1), Math.min(z2, sample.z2)));

	    /* rear zone */
	    if(sample.y2 > y2)
		res.add(new Cube(sample.x1, sample.x2,
				 y2+1, sample.y2,
				 Math.max(z1, sample.z1), Math.min(z2, sample.z2)));

	    /* left zone : note we can't include z or y overlap */
	    if(sample.x1 < x1)
		res.add(new Cube(sample.x1, x1 - 1,
				 Math.max(y1, sample.y1), Math.min(y2, sample.y2),
				 Math.max(z1, sample.z1), Math.min(z2, sample.z2)));

	    /* right zone */
	    if(sample.x2 > x2)
		res.add(new Cube(x2+1, sample.x2,
				 Math.max(y1, sample.y1), Math.min(y2, sample.y2),
				 Math.max(z1, sample.z1), Math.min(z2, sample.z2)));

		
	    return res;
	}

	/* +1 because the range is inclusive */
	public BigInteger size() {
	    return BigInteger.valueOf(x2 - x1 + 1)
		.multiply(BigInteger.valueOf(y2 - y1 + 1))
		.multiply(BigInteger.valueOf(z2 - z1 + 1));
	}
    }    

    /* sue me */
    private ArrayList<K_Oblique> getCubes(ArrayList<String> lines, int num_dimensions) {
	ArrayList<K_Oblique> cubes = new ArrayList<>();
	
	for(var line : lines) {
	    /* gross */
	    var split = line.split(" [a-zA-Z]=|,[a-zA-Z]=|\\.\\.");
	    ArrayList<IntPair> edges = new ArrayList<IntPair>();
	    for(int i = 0; i < num_dimensions; i++) {
		var x1 = Math.min(Integer.parseInt(split[2*i + 1]),
				  Integer.parseInt(split[2*i + 2]));

		var x2 = Math.max(Integer.parseInt(split[2*i + 1]),
				  Integer.parseInt(split[2*i + 2]));

		edges.add(new IntPair(x1, x2));
	    }
	    
	    var enable = split[0].equals("on");

	    cubes.add(new K_Oblique(enable, edges));
	}

	return cubes;
    }

    
    /* sue me */
    private ArrayList<Cube> getCubes(ArrayList<String> lines) {
	ArrayList<Cube> cubes = new ArrayList<>();
	
	for(var line : lines) {
	    /* gross */
	    var split = line.split(" x=|,y=|,z=|\\.\\.");
	    /* the input is presumably ordered, but we choose to play it safe */
	    var x1 = Math.min(Integer.parseInt(split[1]), Integer.parseInt(split[2]));
	    var x2 = Math.max(Integer.parseInt(split[1]), Integer.parseInt(split[2]));

	    var y1 = Math.min(Integer.parseInt(split[3]), Integer.parseInt(split[4]));
	    var y2 = Math.max(Integer.parseInt(split[3]), Integer.parseInt(split[4]));

	    var z1 = Math.min(Integer.parseInt(split[5]), Integer.parseInt(split[6]));
	    var z2 = Math.max(Integer.parseInt(split[5]), Integer.parseInt(split[6]));

	    var enable = split[0].equals("on");

	    cubes.add(new Cube(x1, x2,
			       y1, y2,
			       z1, z2,
			       enable));
	}

	return cubes;
    }
    
    private BigInteger p1_ans(ArrayList<Cube> real_cubes) {
	ArrayList<Cube> confined = new ArrayList<Cube>();
	confined.addAll(real_cubes.stream()
			.map(cube -> cube.confined(-50, 50, -50, 50, -50, 50))
			.filter(cube -> cube != null)
			.collect(Collectors.toList()));
	
	return p2_ans(confined);
    }

    private BigInteger p2_ans_oblique(ArrayList<K_Oblique> obliques) {
	ArrayList<K_Oblique> reversed = new ArrayList<>(obliques);
	Collections.reverse(reversed);

	ArrayList<K_Oblique> inactive = new ArrayList<>();
	ArrayList<K_Oblique> active = new ArrayList<>();

	int iteration = 0;
	for(var cube : reversed) {
	    if(++iteration %10 == 0)
		DEBUG(2, "ITERATION " + iteration);
	    ArrayList<K_Oblique> considered = new ArrayList<>();
	    considered.add(cube);

	    considered = decompose_oblique(inactive, considered);
	    
	    if(!cube.active)
		inactive.addAll(considered);
	    else {	    
		considered = decompose_oblique(active, considered);
		active.addAll(considered);
	    }
	}

	return active.stream().map(cube -> cube.volume())
	    .reduce(BigInteger.ZERO, (left, right) -> left.add(right));
	
    }

    private BigInteger p2_ans(ArrayList<Cube> real_cubes) {
	ArrayList<Cube> reversed = new ArrayList<>(real_cubes);
	Collections.reverse(reversed);

	ArrayList<Cube> inactive = new ArrayList<Cube>();
	ArrayList<Cube> active = new ArrayList<Cube>();
	
	for(var cube : reversed) {
	    ArrayList<Cube> considered = new ArrayList<Cube>();
	    considered.add(cube);
	    considered = decompose(inactive, considered);
	    
	    
	    if(!cube.active)
		inactive.addAll(considered);
	    else {	    
		considered = decompose(active, considered);
		active.addAll(considered);
	    }
	}

	return active.stream().map(cube -> cube.size())
	    .reduce(BigInteger.ZERO, (left, right) -> left.add(right));
    }

    public ArrayList<K_Oblique>
	decompose_oblique(ArrayList<K_Oblique> colliders, ArrayList<K_Oblique> in) {
	for(var collider : colliders) {
	    var in_2 = in.parallelStream()
		.map(sample -> collider.resultant(sample))
		.filter(x -> x != null && !x.isEmpty())
		.collect(Collectors.toList());
	    ArrayList<K_Oblique> res = new ArrayList<>();
	    for(var li : in_2)
		res.addAll(li);
	    /*.reduce(new ArrayList<>(),
	      (left, right) ->  { left.addAll(right); return left; });*/
	    in = res;
	}

	return in;
    }

    
    public ArrayList<Cube> decompose(ArrayList<Cube> colliders, ArrayList<Cube> in) {
	for(var collider : colliders)
	    in = in.stream()
		.map(sample -> collider.resultantCubes(sample))
		.filter(x -> x != null && !x.isEmpty())		
		.reduce(new ArrayList<Cube>(),
			(left, right) ->  { left.addAll(right); return left; });

	return in;
    }
    
    /* solve problem here */
    @Override public int solveProblem() throws Exception {
	Timer t = makeTimer();

	println(">Good Morning!");

	if(num_dimensions == 3) {
	    var cubes_list = getCubes(lines);
	    DEBUG(2, t.split("Input processed"));
	    var p1 = p1_ans(cubes_list);
	    DEBUG(2, t.split("Part 1 complete"));
	    var p2 = p2_ans(cubes_list);
		
	    DEBUGF(1, "PART ONE: "); println(p1);
	    DEBUGF(1, "PART TWO: "); println(p2);
	}
	else {
	    DEBUGF(1, "SOLVING FOR %d DIMENSIONS%n", num_dimensions);

	    var obliques = getCubes(lines, num_dimensions);

	    DEBUG(2, t.split("Part 1 complete"));
	    var p2 = p2_ans_oblique(obliques);
		
	    //DEBUGF(1, "PART ONE: "); println(p1);
	    DEBUGF(1, "PART TWO: "); println(p2);
	}
        
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

	IntCommand num_dimensions = new IntCommand("Number of Dimensions",
						   "The number of dimensions in the input",
						   1, 26, false, 3, "--k-dim", "--dimensions",
						   "--num-dimensions");

        return new Command[]{fc, vc, num_dimensions};
        
        
    }
    
    /* act after commands processed - userCommands stores all the commands set in setCommands */
    @Override public int actOnCommands(Command[] userCommands) throws Exception {
	//do whatever you want based on the commands you have given
	//at this stage, they should all be resolved
        /* code injected from file */
        lines = readFileLines(((FileCommand)userCommands[0]).getValue());
        setSource(((FileCommand)userCommands[0]).getValue());
        
        generate_output = ((BooleanCommand)userCommands[1]).matched();

	num_dimensions = ((IntCommand)userCommands[2]).getValue();
	
	return 0;
    }

    /**
     * Creates and runs an instance of your class - do not modify
     */
    public static void main(String[] argv) {
        new Advent2021_22().run(argv);
    }
}
