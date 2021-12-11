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
import com.nbkelly.drafter.BooleanCommand; //visualize cmd
import com.nbkelly.lib.Image; //visualizer lib

import java.util.HashMap;
import com.nbkelly.lib.HashMapSet;
import com.nbkelly.lib.DiGraph;
import com.nbkelly.lib.pathfinder.Map;
import com.nbkelly.lib.IntPair;
import com.nbkelly.lib.Pair;
import java.util.HashSet;

/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2019_20 extends Drafter {
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
        println(">Good Morning!");

	/* identify all portals */
        var line0 = lines.get(0);
	HashMapSet<Pair<Character, Character>, IntPair> portals = new HashMapSet<>();
	HashSet<IntPair> interior = new HashSet<IntPair>();
	HashSet<IntPair> exterior = new HashSet<IntPair>();

	/* exterior top/bot */
	for(int x = 0; x < line0.length(); x++) {
	    char left = lines.get(0).charAt(x);
            char right = lines.get(1).charAt(x);

            if(left != ' ') {
                var tag = new Pair<Character, Character>(left, right);
                portals.put(tag, new IntPair(x, 2));
                exterior.add(new IntPair(x, 2));
            }


            left = lines.get(lines.size() - 1).charAt(x);
            right = lines.get(lines.size() - 2).charAt(x);

            if(left != ' ') {
                var tag = new Pair<Character, Character>(right, left);
                portals.put(tag, new IntPair(x, lines.size() - 3));
                exterior.add(new IntPair(x, lines.size() - 3));
            }
	}

	/* exterior left/right */
        for(int y = 0; y < lines.size(); y++) {
            var line = lines.get(y);

            char left = line.charAt(0);
            char right = line.charAt(1);

            if(left != ' ') {
                var tag = new Pair<Character, Character>(left, right);
                portals.put(tag, new IntPair(2, y));
                exterior.add(new IntPair(2, y));
            }

            left = line.charAt(line.length()-1);
            right = line.charAt(line.length()-2);

            if(left != ' ') {
                var tag = new Pair<Character, Character>(right, left);
                portals.put(tag, new IntPair(line.length() - 3, y));
                exterior.add(new IntPair(line.length() - 3, y));
            }
        }

	/* find torus depth */
	int depth = 2;
        for( ; ; depth++) {
            var line = lines.get(depth);
            var x = line.length() / 2;
            var token = line.charAt(x);

            if(token != '.' && token != '#') {
                DEBUG(2, ">Torus interior depth is " + depth);
                break;
            }
        }

	/* interior top/bot */
	for(int x = depth; x < line0.length() - depth; x++) {
            char left  = lines.get(depth).charAt(x);
            char right = lines.get(depth+1).charAt(x);

            if(left != ' ') {
                var tag = new Pair<Character, Character>(left, right);
		portals.put(tag, new IntPair(x, depth - 1));
                interior.add(new IntPair(x, depth - 1));
            }


            left = lines.get(lines.size() - depth - 1).charAt(x);
	    right = lines.get(lines.size() - depth - 2).charAt(x);

            if(left != ' ') {
                var tag = new Pair<Character, Character>(right, left);
		portals.put(tag, new IntPair(x, lines.size() - depth));
                interior.add(new IntPair(x, lines.size() - depth));
            }
        }

	/* interior left/right */
        for(int y = depth; y < lines.size() - depth; y++) {
            char left = lines.get(y).charAt(depth);
            char right = lines.get(y).charAt(depth+1);

            if(left != ' ') {
                var tag = new Pair<Character, Character>(left, right);
                portals.put(tag, new IntPair(depth-1, y));
		interior.add(new IntPair(depth-1, y));
	    }


            left = lines.get(y).charAt(line0.length() - 1 - depth);
            right = lines.get(y).charAt(line0.length() - 2 - depth);

	    if(left != ' ') {
                var tag = new Pair<Character, Character>(right, left);
                portals.put(tag, new IntPair(line0.length() - depth, y));
                interior.add(new IntPair(line0.length() - depth, y));
            }
	}

	DEBUG(3, "portals: " + portals.toString());

	var p1_ans = p1_ans(portals);
        DEBUGF(1, "PART ONE: "); println(p1_ans);
	var p2_ans = p2_ans(portals, interior, exterior, 150);
        DEBUGF(1, "PART TWO: "); println(p2_ans);
        
        /* visualize output here */
        generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    private Integer p2_ans(HashMapSet<Pair<Character, Character>, IntPair> portals,
			   HashSet<IntPair> interior, HashSet<IntPair> exterior,
			   int maxDepth) {
	/* first, we need to know the locations of START and END (AA, ZZ) */
        Triple start = null;
        Triple end = null;

        for(var entry : portals.entrySet()) {
            var tag = entry.getKey();
            for(var point : entry.getValue()) {
                if(tag.equals(new Pair<Character, Character>('A', 'A')))
                    start = new Triple(point, 0);
                else if(tag.equals(new Pair<Character, Character>('Z', 'Z')))
                    end = new Triple(point, 0);
            }
        }

	DiGraph g1 = new DiGraph();

	/* map every point to a graph node */
        HashMap<IntPair, Pair<Character, Character>> pointsOfInterest = new HashMap<>();
	HashMap<IntPair, Integer> ids = new HashMap<>();

        ArrayList<IntPair> pairs = new ArrayList<IntPair>();

	int index = 0;

	for(var entry : portals.entrySet()) {
            var tag = entry.getKey();

            for(var point : entry.getValue()) {
                pointsOfInterest.put(point, tag);
                ids.put(point, index++);
		pairs.add(point);
            }

            var set = entry.getValue();
            for(var p1 : entry.getValue()) {
                int id1 = ids.get(p1);
                for(var p2 : entry.getValue()) {
                    if(p1.equals(p2))
                        continue;
                    int id2 = ids.get(p2);
		}
	    }
	}

	/* compute every pairwise distance, disregarding teleportation (we don't need it yet) */
        Map map = new Map(lines);
	for(int i = 0; i < pairs.size(); i++) {
            var p1 = pairs.get(i);
            int id1 = ids.get(p1);

            for(int j = i+1; j< pairs.size(); j++) {
                var p2 = pairs.get(j);

		var dist = map.distance_orthogonal(p1, p2,
                                                   x -> x == '.',
                                                   x -> Map.OrthogonalNeighbors(x));
                if(dist != null) {
                    int id2 = ids.get(p2);

                    g1.join(id1, id2, dist);

                    DEBUGF(2, "joined %d and %d with length %d%n", id1, id2, dist);
                }
            }
        }

	/* now we start on doing the recursive graph */
        DiGraph g2 = new DiGraph();

        /* we give every triple and ID so we can ind it in our graph */
	HashMap<Triple, Integer> tID = new HashMap<Triple, Integer>();
	index = 0;
	tID.put(start, index++);
	tID.put(end, index++);

        ArrayList<Triple> triples_outside = new ArrayList<Triple>();
	triples_outside.add(start);
        triples_outside.add(end);

	/* add every internal node - this exist on depth 0 */
        for(var node : interior) {
            var point = new Triple(node, 0);
            triples_outside.add(point);
	    tID.put(point, index++);
        }

        for(int i = 0; i < triples_outside.size(); i++) {
            for(int j = i+1; j < triples_outside.size(); j++) {
            //calculate all the distances between the outer nodes                                     
                var p1 = triples_outside.get(i);
		var p2 = triples_outside.get(j);

                /* get the distance between these two points, without crossing any nodes */
                var dist = g1.weight(ids.get(p1.XY()), ids.get(p2.XY()));
                if(dist != null) {
                    g2.join(tID.get(p1), tID.get(p2), dist);
                    DEBUGF(2, "Linked nodes %s and %s with distance %d (stage 1) %n", p1, p2, dist);
                }
            }
        }

	for(int depth = 0; depth < maxDepth; depth++) {
            var dist_best = g2.shortest_path(g2.domain(), 0, 1);
            if(dist_best != null) {
                /*printf("Best recursive distance is %d, achieved at depth %d%n",
                       dist_best,
		       depth+1);*/
                return dist_best.intValue();
            }
            
            ArrayList<Triple> interior_last = new ArrayList<Triple>();
            ArrayList<Triple> interior_next = new ArrayList<Triple>();
            ArrayList<Triple> exterior_next = new ArrayList<Triple>();

            for(var node : interior) {
                interior_last.add(new Triple(node, depth));
                var trip = new Triple(node, depth+1);
                tID.put(trip, index++);
                interior_next.add(trip);
            }

            for(var node : exterior) {
                var trip = new Triple(node, depth+1);
                tID.put(trip, index++);
                exterior_next.add(trip);
            }

	    /* join every interior node at depth to every exterior node at depth + 1 */
            for(var last : interior_last) {
                /* find the tag we belong to */
                var tag = pointsOfInterest.get(last.XY());

                Triple ext = null;
                /*find the other point that belongs to this tag */
		for(var point : portals.getAll(tag)) {
                    if(!point.equals(last.XY()))
                       ext = new Triple(point, depth + 1);
                }
		
                g2.join(tID.get(last), tID.get(ext), 1);
                DEBUGF(2, "Linked nodes %s and %s (stage %d layer) with dist 1 %n",last,ext,1+depth);
            }

	    /* join every node at depth + 1 */
            var next_all = new ArrayList<Triple>(interior_next);
            next_all.addAll(exterior_next);
            for(int i = 0; i < next_all.size(); i++) {
                var p1 = next_all.get(i);
                for(int j = 1; j < next_all.size(); j++) {
                    var p2 = next_all.get(j);
                    /* get the distance between these two points, without crossing any nodes */
                    var _dist = g1.weight(ids.get(p1.XY()), ids.get(p2.XY()));
                    if(_dist != null) {
                        g2.join(tID.get(p1), tID.get(p2), _dist);
                        DEBUGF(3, "Linked nodes %s and %s with distance %d%n", p1, p2, _dist);
                    }
            	}
            }
        }	    
	
	return -1;
    }
    
    private Integer p1_ans(HashMapSet<Pair<Character, Character>, IntPair> portals) {
	DiGraph g = new DiGraph();

	HashMap<IntPair, Pair<Character, Character>> pointsOfInterest = new HashMap<>();
	HashMap<IntPair, Integer> ids = new HashMap<>();

	ArrayList<IntPair> pairs = new ArrayList<IntPair>();

	IntPair start = null;
        IntPair end = null;

        /* map each point to an id, and each point to a tag */
        int index = 0;
        for(var entry : portals.entrySet()) {
            var tag = entry.getKey();

            for(var point : entry.getValue()) {
                pointsOfInterest.put(point, tag);
                ids.put(point, index++);

                if(tag.equals(new Pair<Character, Character>('A', 'A')))
                    start = point;
                else if(tag.equals(new Pair<Character, Character>('Z', 'Z')))
                    end = point;

                pairs.add(point);
            }

	    var set = entry.getValue();
            for(var p1 : entry.getValue()) {
                int id1 = ids.get(p1);
		for(var p2 : entry.getValue()) {
                    if(p1.equals(p2))
                        continue;
                    int id2 = ids.get(p2);
                    g.join(id1, id2, 1);

                    DEBUGF(2, "joined %d and %d with length %d%n", id1, id2, 1);
                }
            }
	}

	Map map = new Map(lines);

        for(int i = 0; i < pairs.size(); i++) {
            var p1 = pairs.get(i);
            int id1 = ids.get(p1);

            for(int j = i+1; j< pairs.size(); j++) {
		var p2 = pairs.get(j);

                var dist = map.distance_orthogonal(p1, p2,
                                                   x -> x == '.',
                                                   x -> Map.OrthogonalNeighbors(x));

		if(dist != null) {
                    int id2 = ids.get(p2);

		    g.join(id1, id2, dist);

                    DEBUGF(2, "joined %d and %d with length %d%n", id1, id2, dist);
                }
            }
	}

        int sid = ids.get(start);
        int eid = ids.get(end);
	
	return g.shortest_path(g.domain(), sid, eid).intValue();
    }
    
    private class Triple {
	IntPair xy;
	int z;

	public Triple(IntPair xy, int z) {
	    this.xy = xy;
	    this.z = z;
	}

	public IntPair XY() {
	    return xy;
	}

	@Override public int hashCode() {
	    return 257*xy.hashCode() + z;
	}

	public boolean equals(Object o) {
	    if(o instanceof Triple)
		return ((Triple)o).hashCode() == hashCode();

	    return false;
	}

	public String toString() {
	    return String.format("(%d, %d, %d)", xy.X, xy.Y, z);
	}
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
        new Advent2019_20().run(argv);
    }
}
