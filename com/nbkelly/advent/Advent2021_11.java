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
import java.util.TreeMap;
import java.util.TreeSet;
import com.nbkelly.lib.IntPair;

/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2021_11 extends Drafter {
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
        //var ints = Util.toIntList(lines);
	TreeMap<IntPair, Integer> space = new TreeMap<IntPair, Integer>();

	int y = 0;
	for(var line : lines) {
	    for(int x = 0; x < line.length(); x++)
		space.put(new IntPair(x, y), Integer.parseInt("" + line.charAt(x)));
	    y++;
	}

	var sum = 0;
	var synchro = -1;
	int i = 0;
	while(i < 100 || synchro < 0) {
	    var ct_step = 0;
	    if(i < 100)
		sum += step(space);
	    else
		ct_step = step(space);

	    i++;
	    
	    /* see if it lines up */
	    if(synchro < 0 && ct_step == 100) //empty_space(space))
		synchro = i;

	    if(i % 100 == 0)
		DEBUG(2, "iteration " + i);

	    //this is just for making output webms
	    /*if(i == 25) {
		synchro = i;
		break;
	    }*/
	}
	
        DEBUGF(1, "PART ONE: "); println(sum); 
        DEBUGF(1, "PART TWO: "); println(synchro);
        
        /* visualize output here */
        generate_output(lines, synchro, 10);
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    public void printSpace(TreeMap<IntPair, Integer> space) {
	for(int y = 0; y < 10; y++) {
	    for(int x = 0; x < 10; x++)
		print(space.get(new IntPair(x, y)));
	    println();
	}

	println();
    }
    
    public Integer step(TreeMap<IntPair, Integer> space) {
 	TreeSet<IntPair> visited = new TreeSet<IntPair>();
	
	//select all tiles with a 9
	TreeSet<IntPair> active = new TreeSet<IntPair>();
	
	for(var loc : space.keySet()) {
	    if(space.get(loc) >= 9)
		active.add(loc);
	}

	/* active is every unit that should pop this round */
	while(active.size() > 0) {    
	    var current_active = active.pollFirst();
	    if(visited.contains(current_active))
		continue;
	    
	    space.put(current_active, 0);

	    visited.add(current_active);
	    inner: for(var neighbor : neighbors(current_active)) {
		if(visited.contains(neighbor))
		    continue;
		else if (space.containsKey(neighbor)) {
		    var score = space.get(neighbor) + 1;
		    if(score >= 9)
			active.add(neighbor);
		    else
			space.put(neighbor, score);
		}		    
	    }
	}

	var set = space.keySet();
	for(var pair : new TreeSet<>(space.keySet())) {
	    if(visited.contains(pair))
		space.put(pair, 0);
	    else
		space.put(pair, space.get(pair) + 1);
	}

	return visited.size();
    }

    public static ArrayList<IntPair> neighbors(IntPair pair) {
	ArrayList<IntPair> res = new ArrayList<>();

	res.add(new IntPair(pair.X-1, pair.Y));
	res.add(new IntPair(pair.X-1, pair.Y - 1));
	res.add(new IntPair(pair.X-1, pair.Y + 1));
	res.add(new IntPair(pair.X+1, pair.Y));
	res.add(new IntPair(pair.X+1, pair.Y - 1));
	res.add(new IntPair(pair.X+1, pair.Y + 1));
	res.add(new IntPair(pair.X, pair.Y+1));
	res.add(new IntPair(pair.X, pair.Y-1));

	return res;
    }

	
    /* code injected from file */
    public void generate_output(ArrayList<String> lines, int iterations, int scale) throws Exception {
    	if(!generate_output)
    	    return;
    	
    	println(">generating output");
	
    	/* output goes here */

	/* first, construct the space */
	TreeMap<IntPair, Integer> space = new TreeMap<IntPair, Integer>();

	int y = 0;
	int len = 0;
	for(var line : lines) {
	    for(int x = 0; x < line.length(); x++)
		space.put(new IntPair(x, y), Integer.parseInt("" + line.charAt(x)));
	    y++;
	    len = line.length();
	}
	
	printf("Image dimensions: %d * %d, %d * %d%n", scale, len, scale, y);

	/* run through iterations one at a time, and produce files */
	for(int i = 0; i <= iterations; i++) {
	    step(space);

	    Image image = new Image((2 + len)*scale, (2 +y)*scale);

	    for(var loc : space.keySet()) {
		switch(space.get(loc)) {		    		
		case 0:
		    image.rect(Image.H11,(loc.X + 1)*scale, (loc.Y + 1)*scale, scale, scale);
		    break;
		case 9:
		    image.rect(Image.H10,(loc.X + 1)*scale, (loc.Y + 1)*scale, scale, scale);
		    break;
		case 8:
		    image.rect(Image.H8,(loc.X + 1)*scale, (loc.Y + 1)*scale, scale, scale);
		    break;
		case 7:
		    image.rect(Image.H7,(loc.X + 1)*scale, (loc.Y + 1)*scale, scale, scale);
		    break;
		case 6:
		    image.rect(Image.H6,(loc.X + 1)*scale, (loc.Y + 1)*scale, scale, scale);
		    break;
		case 5:
		    image.rect(Image.H5,(loc.X + 1)*scale, (loc.Y + 1)*scale, scale, scale);
		    break;
		case 4:
		    image.rect(Image.H4,(loc.X + 1)*scale, (loc.Y + 1)*scale, scale, scale);
		    break;
		case 3:
		    image.rect(Image.H3,(loc.X + 1)*scale, (loc.Y + 1)*scale, scale, scale);
		    break;
		case 2:
		    image.rect(Image.H2,(loc.X + 1)*scale, (loc.Y + 1)*scale, scale, scale);
		    break;
		case 1:
		    image.rect(Image.H1,(loc.X + 1)*scale, (loc.Y + 1)*scale, scale, scale);
		    break;
		}
	    }

	    var name = String.format("animation/%04d_out.png", i);
	    image.savePNG(name);
	    println(">" + name);
	    name = String.format("animation/%04d_out.png", 2*iterations - i);
	    image.savePNG(name);
	    println(">" + name);	    
	}
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
        new Advent2021_11().run(argv);
    }
}
