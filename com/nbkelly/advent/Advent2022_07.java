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
public class Advent2022_07 extends Drafter {
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


        println(">Good Morning!");
        
        DEBUGF(1, "PART ONE: "); //todo
	var FileSystem = new Dir("/");
	var currentFolder = FileSystem;
	for(int i = 0; i < lines.size(); i++) {
	    var line = lines.get(i);
	    if(line.equals("$ cd /"))
		currentFolder = FileSystem;
	    else if (line.equals("$ ls")) {
		for(int x = i + 1; x < lines.size() && !lines.get(x).startsWith("$"); x++) {
		    var content = lines.get(x).split(" ");
		    if(content[0].equals("dir")) {
			if(currentFolder.containsDir(content[1]) == null)
			    currentFolder.folders.add(new Dir(content[1], currentFolder));
		    }
		    else {
			//println("Checking file");
			if(currentFolder.containsFile(content[1]) == null) {
			    //println("Adding file " + content[1]);
			    currentFolder.files.add(new File(content[1], content[0]));
			}
		    }
		}
	    }
	    else if (line.equals("$ cd ..")) {
		//println("cd ..");
		currentFolder = currentFolder.parent;
	    }
	    else if (line.startsWith("$ cd ")) {
		//println("move to " + line.split(" ")[2]);
		var target = line.split(" ")[2];
		var folder = currentFolder.containsDir(target);
		if(folder != null)
		    currentFolder = folder;
	    }
	}

	ArrayList<Integer> vals = new ArrayList<Integer>();
	FileSystem.walk(vals);
	println(vals);

	int sum = 0;
	for (var val : vals)
	    if(val <= 100000)
		sum += val;
	println(sum);

        DEBUGF(1, "PART TWO: "); //todo

	int totalSpace = 70000000;
	int usedSpace = vals.get(vals.size() - 1);
	int minDelete = -1 * ((totalSpace - 30000000) - usedSpace);
	//int minDelete =


	println("total space: " + totalSpace);
	println("used space: " + usedSpace);
	println("min delete: " + minDelete);

	Collections.sort(vals);
	println(vals);
	for(var val : vals)
	    if(val >= minDelete) {
		println(val);
		break;
	    }

        /* visualize output here */
        generate_output();
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    private class File {
	public File(String name, String size) {
	    this.name = name;
	    this.size = Integer.parseInt(size);
	}

	String name;
	int size;
    }

    private class Dir {
	public Integer walk(ArrayList<Integer> res) {
	    int size = 0;
	    for(var f : this.folders)
		size += f.walk(res);
	    for(var f : this.files) {
		size += f.size;
	    }

	    //(size <= 100000)
	    res.add(size);

	    println("name: " + name + "   size: " + size);
		
	    return size;
	}

	public Dir(String name) {
	    this.name = name;
	    parent = this;
	}
	public Dir(String name, Dir parent) {
	    this.name = name;
	    this.parent = parent;
	}

	public Dir containsDir(String dir) {
	    for(var f : folders)
		if(f.name.equals(dir))
		    return f;
	    return null;
	}

	public File containsFile(String dir) {
	    for(var f : files)
		if(f.name.equals(dir))
		    return f;
	    return null;
	}


	String name;
	Dir parent = null;
	ArrayList<Dir> folders = new ArrayList<>();
	ArrayList<File> files = new ArrayList<>();
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
        new Advent2022_07().run(argv);
    }
}
