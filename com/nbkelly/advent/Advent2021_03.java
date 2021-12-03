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

import java.util.HashSet;
import java.util.List;
import java.util.Collections;

/**
 * Extension of Drafter directed towards a general case.
 *
 * @see <a href="https://nbkelly.github.io/Drafter/com/nbkelly/package-summary.html" target="_top">
 * here</a> for the up to date online javadocs
 */
public class Advent2021_03 extends Drafter {
    /* WORKFLOW:
     *  set all needed commands with setCommands()
     *  post-processing can be performed with actOnCommands()
     *  the rest of your work should be based around the solveProblem() function
     */
    
    /* params injected from file */
    ArrayList<String> lines;
    
    //generate output
    boolean generate_output = false;

    /* inverts a binary string */
    private String invert(String s) {
	return s.replaceAll("1", "2").replaceAll("0","1").replaceAll("2","0");
    }

    /* parses a binary string */
    private Integer parse(String s) {
	return Integer.parseInt(s, 2);
    }
    
    /* solve problem here */
    @Override public int solveProblem() throws Exception {
	Timer t = makeTimer();

	//luckily I had a pre-prepared method for finding consensus
	var cons =  Util.consensus(lines.toArray(new String[0]));

	var gamma_rate = parse(cons);
	var epsilon_rate = parse(invert(cons));
	var p1_ans = gamma_rate * epsilon_rate;		

	DEBUGF(1, "PART ONE: "); println(p1_ans);
	
	/* part two */
	Tree tree = new Tree();
	for(String s : lines)
	    tree.add(s);

	//most common, prefer one
	String oxygen = tree.traverse(true, true);
	String carbon = tree.traverse(false, false);
	var p2_ans = parse(oxygen)*parse(carbon);

	DEBUGF(1, "PART TWO: "); println(p2_ans);
		
        /* visualize output here */
        generate_output(lines);
	
	return DEBUG(1, t.split("Finished Processing"));
    }

    private class Tree {
	private int weight = 0;
	private Tree zero = null;
	private Tree one = null;
	
	public Tree() {}
	public Tree(String s) { add(s); }

	public void add(String s) {
	    weight++;
	    
	    if(s.length() == 0)
		return;

	    var child = s.substring(1);
	    if(s.charAt(0) == '0')
		if(zero == null)
		    zero = new Tree(child);
		else
		    zero.add(child);	    
	    else 
		if(one == null)
		    one = new Tree(child);
		else
		    one.add(child);		
	}

	public String traverse(boolean common, boolean ones) {
	    if(one == null && zero == null) return "";
	    if(one == null) return "0" + zero.traverse(common, ones);
	    if(zero == null) return "1" + one.traverse(common, ones);

	    if(one.weight == zero.weight && ones) return "1" + one.traverse(common, ones);
	    if(one.weight == zero.weight && !ones) return "0" + zero.traverse(common, ones);

	    if((one.weight > zero.weight) ^ !common)
		return "1" + one.traverse(common, ones);
	    
	    return "0" + zero.traverse(common, ones);
	}
    }    
    
    /* code injected from file */
    public void generate_output(List<String> lines) throws Exception {
    	if(!generate_output)
    	    return;
	
    	println(">generating output");	
    
    	/* output goes here */
	String[] arr = new String[lines.size()];
	for(int i = 0; i < lines.size(); i++)
	    arr[i] = lines.get(i);
	
	var cons = Util.consensus(arr);
	int width = cons.length() * 12 + 2;
	var height = lines.size() * 12 + 4;

	int ystart = 12;
	int xstart = 2;

	int ch_height = 10;
	
	var image = new Image(width, height);

	for(int y = 0; y < lines.size(); y++) {
	    //for width
	    for(int x = 0; x < cons.length(); x++)
	    {
		int _y = ystart + (y*12);
		int _x = xstart + (x*12); //+...
		char ch = lines.get(y).charAt(x);
		image.text((ch == cons.charAt(x) ? Image.C3 : Image.C2),
			   Image.F1, _x, _y, ch + "");
	    }
	}

	println(">out1.png");
	image.savePNG("out1.png");

	/* out 2 */
	width = 9*cons.length();
	height = 3*lines.size();

	image = new Image(width, height);

	for(int y = 0; y < lines.size(); y++) {
	    //for width
	    for(int x = 0; x < cons.length(); x++)
	    {
		int _y = 3 * y;
		int _x = 9*x;
		char ch = lines.get(y).charAt(x);
		image.rect((ch == cons.charAt(x) ? Image.C3 : Image.C2),
			   _x, _y, 9, 3);
	    }
	}

	image.savePNG("out2.png");
	println(">out2.png");

	/* out 3 */
	width = cons.length() * 12 + 2;
	height = lines.size() * ch_height;

	ystart = 10;
	xstart = 2;

	image = new Image(width, height);
	
	HashSet<String> valid = new HashSet<String>();
	ArrayList<String> next = new ArrayList<String>();
	next.addAll(lines);
	valid.addAll(lines);
	for(int x = 0; x < cons.length(); x++) {
	    /* first determine the state of next */
	    int ct_0= 0, ct_1 = 0;
	    for(String s : next)
		if(s.charAt(x) == '0')
		    ct_0++;
		else
		    ct_1++;
	    
	    ArrayList<String> tmp = new ArrayList<String>();
	    if(ct_1 >= ct_0)
		for(String s : next) {
		    if(s.charAt(x) == '1')
			tmp.add(s);
		}
	    else
		for(String s : next) {
		    if(s.charAt(x) == '0')
			tmp.add(s);
		}

	    next = tmp;

	    /* we draw every string that is valid, and we highlight every string in next */
	    HashSet<String> valid_next = new HashSet<String>();
	    valid_next.addAll(tmp);
	    
	    for(int y = 0; y < lines.size(); y++) {
		String ct_line = lines.get(y);
		int _y = ystart + (y*ch_height);
		int _x = xstart + (x*12); //+...
		char ch = ct_line.charAt(x);
		if(valid.contains(ct_line)) {
		    image.text((valid_next.contains(ct_line) ? Image.C3 : Image.C2),
			       Image.F1, _x, _y, ch + "");
		}
	    }

	    valid = valid_next;
	    next = tmp;
	}

	image.savePNG("out3.png");
	println(">out3.png");

	/* out4 */
	image = new Image(width, height);

	Collections.sort(lines);
	valid = new HashSet<String>();
	next = new ArrayList<String>();
	next.addAll(lines);
	valid.addAll(lines);
	for(int x = 0; x < cons.length(); x++) {
	    /* first determine the state of next */
	    int ct_0= 0, ct_1 = 0;
	    for(String s : next)
		if(s.charAt(x) == '0')
		    ct_0++;
		else
		    ct_1++;
	    
	    ArrayList<String> tmp = new ArrayList<String>();
	    if(ct_1 >= ct_0)
		for(String s : next) {
		    if(s.charAt(x) == '1')
			tmp.add(s);
		}
	    else
		for(String s : next) {
		    if(s.charAt(x) == '0')
			tmp.add(s);
		}

	    next = tmp;

	    /* we draw every string that is valid, and we highlight every string in next */
	    HashSet<String> valid_next = new HashSet<String>();
	    valid_next.addAll(tmp);
	    
	    for(int y = 0; y < lines.size(); y++) {
		String ct_line = lines.get(y);
		int _y = ystart + (y*ch_height);
		int _x = xstart + (x*12); //+...
		char ch = ct_line.charAt(x);
		if(valid.contains(ct_line)) {
		    image.text((valid_next.contains(ct_line) ? Image.C3 : Image.C2),
			       Image.F1, _x, _y, ch + "");
		}
	    }

	    valid = valid_next;
	    next = tmp;
	}

	image.savePNG("out4.png");
	println(">out4.png");

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
        new Advent2021_03().run(argv);
    }
}
