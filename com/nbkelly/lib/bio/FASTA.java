package com.nbkelly.lib.bio;

import java.util.ArrayList;
import java.util.Scanner;
import java.net.URL;
import java.util.Arrays;
import java.net.MalformedURLException;
import java.io.IOException;

public class FASTA {
    private ArrayList<String> comments = new ArrayList<>();
    private String title;
    private ArrayList<String> sequence = new ArrayList<>();
    private String _sequence = null;

    //hide the constructor
    private FASTA() {}

    public static ArrayList<FASTA> allFromURL(String URL) throws MalformedURLException, IOException{
	String out = new Scanner(new URL(URL).openStream(), "UTF-8").useDelimiter("\\A").next();
	ArrayList<String> lines = new ArrayList<String>(Arrays.asList(out.split("\n")));
	return fromFileLines(lines);
    }

    public static FASTA fromURL(String URL) throws MalformedURLException, IOException{
	String out = new Scanner(new URL(URL).openStream(), "UTF-8").useDelimiter("\\A").next();
	ArrayList<String> lines = new ArrayList<String>(Arrays.asList(out.split("\n")));
	return fromFileAtLine(lines, 0);
    }
    
    public static ArrayList<FASTA> fromFileLines(ArrayList<String> lines) {
	ArrayList<FASTA> fastas = new ArrayList<FASTA>();

	ArrayList<Integer> starts = new ArrayList<Integer>();
	
	boolean active = false;

	int line = 0;

	while(line < lines.size()) {
	    if (active && !lines.get(line).matches("^;"))
		active = false;
	    
	    if((!active && lines.get(line).startsWith(";"))
	       || lines.get(line).startsWith(">")) {
		starts.add(line);
		fastas.add(fromFileAtLine(lines, line));
		active = true;
	    }
	    
	    line++;
	}
	
	return fastas;
    }

    public String getTitle() {
	return title;
    }
    
    public String getSequence() {
	if(_sequence != null)
	    return _sequence;
	
	StringBuilder sb = new StringBuilder();
	for(String s : sequence)
	    sb.append(s);

	return _sequence = sb.toString();
    }
    
    public String toString() {
	StringBuilder sb = new StringBuilder();

	sb.append(">" + title);
	for(String s : comments)
	    sb.append("\n;" + s);

	for(String s : sequence)
	    sb.append("\n" + s);

	return sb.toString();
    }
    
    private static final int UNBOUND = 0;
    private static final int HEADER = 1;
    private static final int COMMENT = 2;
    private static final int SEQUENCE = 3;
    private static final int COMPLETED = 4;
    private static final int INVALID = 5;
    
    private static FASTA fromFileAtLine(ArrayList<String> lines, int line) {
	int state = UNBOUND;

	FASTA res = new FASTA();
	
	outer:
	while(line < lines.size() && state < COMPLETED) {
	    String ln = lines.get(line);

	    selection:
	    switch(state) {		
	    case UNBOUND:
		if(ln.startsWith(">")) {
		    res.title = ln.substring(1);
		    state = HEADER;
		    break selection;
		}

		if(ln.startsWith(";")) {
		    res.title = ln.substring(1);
		    res.comments.add(ln.substring(1));
		    state = COMMENT;
		    break selection;
		}

		state = INVALID;
		break outer;
	    case HEADER:
	    case COMMENT:
		if(ln.startsWith(">")) { //an empty fasta header
		    state = COMPLETED;
		    break outer;
		}

		if(ln.startsWith(";")) {
		    state = COMMENT;
		    res.comments.add(ln.substring(1));
		    break selection;
		}

		if(ln.isEmpty()) {
		    state = COMPLETED;
		    break outer;
		}

		state = SEQUENCE;
		continue outer;
	    case SEQUENCE:
		if(ln.isEmpty() || ln.startsWith(";") || ln.startsWith(">")) {
		    state = COMPLETED;
		    break outer;
		}

		if(ln.contains("*")) {
		    int fin = ln.indexOf('*');
		    res.sequence.add(ln.substring(0, fin));
		    state = COMPLETED;
		    break outer;
		}

		res.sequence.add(ln);
		break selection;
	    default:
		break selection;
	    }

	    line++;
	}

	if(state == INVALID)
	    return null;
	return res;
    }
}
