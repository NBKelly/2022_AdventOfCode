package com.nbkelly.advent;

/* imports */
import com.nbkelly.drafter.Drafter;
import com.nbkelly.drafter.Command;
import com.nbkelly.drafter.FileCommand;
import com.nbkelly.drafter.Timer;
import com.nbkelly.lib.HashedDiGraph;
import java.util.HashSet;
/* imports from file */
import java.util.ArrayList;

/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2021_00 extends Drafter {
    /* WORKFLOW:
     *  set all needed commands with setCommands()
     *  post-processing can be performed with actOnCommands()
     *  the rest of your work should be based around the solveProblem() function
     */
    
    /* params injected from file */
    ArrayList<String> lines;
    
    /* solve problem here */
    @Override public int solveProblem() throws Exception {
	Timer t = makeTimer();

	HashedDiGraph<String> lazy = new HashedDiGraph<String>();
	HashSet<String> names = new HashSet<String>();
	
	//find out all the relationships
	while(hasNextLine()) {
	    var name = next();
	    next();
	    var score = (next().equals("lose") ? -1 : 1) * nextInt();
	    
	    next(); next(); next();
	    next(); next(); next();
	    
	    var target = next();
	    target = target.substring(0, target.length()-1);
	    
	    flushLine();

	    lazy.link(name, target, score);
	    names.add(name);
	    names.add(target);
	}

	HashedDiGraph<String> proper = new HashedDiGraph<String>();

	var li = new ArrayList<String>();
	li.addAll(names);

	//build a graph through the whole network
	HashedDiGraph<String> meta = new HashedDiGraph<String>();
	for(int i = 0; i < li.size(); i++) {
	    var from = li.get(i);
	    for(int j = i+1; j < li.size(); j++) {		
		var to = li.get(j);
		//printf("From %s to %s%n", from, to);
		var dist = lazy.weight(from, to);
		dist += lazy.weight(to, from);
		//println("Distance: " + dist);
		meta.join(from, to, dist);
	    }
	}

	//part one
	DEBUGF("Part One: ");
	println(meta.longest_complete_cycle(names));

	//part two requires us to include ourself
	for(var name : li) {
	    meta.join("self", name, 0);
	}
	names.add("self");
	
	DEBUGF("Part Two: ");
	println(meta.longest_complete_cycle(names));
	
	
	
	
	return DEBUG(1, t.split("Finished Processing"));
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
        return new Command[]{fc};
    }
    
    /* act after commands processed - userCommands stores all the commands set in setCommands */
    @Override public int actOnCommands(Command[] userCommands) throws Exception {
	//do whatever you want based on the commands you have given
	//at this stage, they should all be resolved
        /* code injected from file */
        lines = readFileLines(((FileCommand)userCommands[0]).getValue());
        setSource(((FileCommand)userCommands[0]).getValue());
	return 0;
    }

    /**
     * Creates and runs an instance of your class - do not modify
     */
    public static void main(String[] argv) {
        new Advent2021_00().run(argv);
    }
}
