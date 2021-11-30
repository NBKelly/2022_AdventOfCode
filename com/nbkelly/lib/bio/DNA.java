package com.nbkelly.lib.bio;

import java.util.ArrayList;
import java.util.Collections;

public class DNA extends XNA {
    public DNA(String sequence) {
	//TODO: assert sequence is legal
	super(sequence);
    }

    protected DNA(ArrayList<Nucleotide> sequence) {
	super(sequence);
    }
    
    public RNA toRNA() {
	ArrayList<Nucleotide> res = new ArrayList<Nucleotide>();
	for(Nucleotide n : sequence) {
	    switch(n) {
	    case T:
		res.add(Nucleotide.U);
		break;
	    default:
		res.add(n);
	    }
	}

	return new RNA(res);
    }

    public DNA removeAll(String[] sequences) {
	StringBuilder pattern = new StringBuilder();

	//TODO: make stringbuilder
	
	for(int i = 0; i < sequences.length; i++) {
	    if(i > 0)
		pattern.append("|");
	    pattern.append("(");
	    pattern.append(sequences[i]);
	    pattern.append(")");
	}

	return new DNA(toString().replaceAll(pattern.toString(), ""));
    }

    public DNA removeAll(DNA[] sequences) {
	String[] seqs = new String[sequences.length];

	for(int i = 0; i < sequences.length; i++)
	    seqs[i] = sequences[i].toString();
	return removeAll(seqs);
    }
    
    @Override public DNA complement() {
	ArrayList<Nucleotide> res = new ArrayList<Nucleotide>();
	for(Nucleotide n : sequence) {
	    switch(n) {
	    case T:
		res.add(Nucleotide.A);
		break;
	    case A:
		res.add(Nucleotide.T);
		break;
	    case C:
		res.add(Nucleotide.G);
		break;
	    case G:
		res.add(Nucleotide.C);
		break;
	    }
	}

	return new DNA(res);
    }

    @Override public DNA reverse() {
	ArrayList<Nucleotide> res = new ArrayList<>(sequence);
	Collections.reverse(res);
	return new DNA(res);
    }
}
