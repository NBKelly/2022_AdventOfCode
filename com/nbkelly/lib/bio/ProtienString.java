package com.nbkelly.lib.bio;

import java.util.ArrayList;
import java.util.Collections;
import java.math.BigInteger;
import java.math.BigDecimal;

public class ProtienString extends Sequence implements Comparable<ProtienString> {
    ArrayList<Protien> sequence = new ArrayList<Protien>();

    public ProtienString(String sequence) {
	for(int i = 0; i < sequence.length(); i++)
	    this.sequence.add(Protien.valueOf("" + sequence.charAt(i)));
    }
    
    public ProtienString(ArrayList<Protien> sequence) {
	//todo: asset sequence is valid?
	this.sequence = new ArrayList<>(sequence);
    }

    @Override public ProtienString reverse() {
	ArrayList<Protien> res = new ArrayList<>(sequence);
	Collections.reverse(res);
	return new ProtienString(res);
    }

    public String toString() {
	StringBuilder sb = new StringBuilder();
	for(Protien p : sequence)
	    sb.append(p.toString());

	return sb.toString();
    }

    public int compareTo(ProtienString p) {
	return toString().compareTo(p.toString());
    }
    
    /**
     * returns the list of 0-indexed matches against a pattern
     * <p>
     * a pattern of the form (x|[xx]|{xx})*
     * where [xx] is OR, and {xx} is anything but
     * 
     * @param pattern pattern to match against
     * @return the list of 0-indexed matches against a pattern
     */
    public ArrayList<Integer> matches(String pattern) {
	ArrayList<Integer> res = new ArrayList<Integer>();

	String sequence = toString();
	
	for(int i = 0; i < sequence.length(); i++) {
	    int index = 0;
	    int len = 0;
	    while(index < pattern.length() && index != -1) {
		index = match_index(sequence, i + len++,
				    pattern, index);
		//if(index != -1)
		//    System.out.println("I: " + i + ", index: " + index);
	    }
	    
	    if(index != -1)
		res.add(i);
	}

	return res;
    }

    private int match_index(String sequence, int index,
			    String pattern, int pattern_index) {
	if(index >= sequence.length())
	    return -1;

	char current = sequence.charAt(index);

	if(pattern.charAt(pattern_index) == '{') {
	    pattern_index++;
	    
	    while(pattern.charAt(pattern_index) != '}') {
		if(current == pattern.charAt(pattern_index))
		    return -1;
		pattern_index++;
	    }

	    return pattern_index + 1;
	}

	if(pattern.charAt(pattern_index) == '[') {
	    boolean matched = false;
	    pattern_index++;

	    while(pattern.charAt(pattern_index) != ']') {
		if(current == pattern.charAt(pattern_index))
		    matched = true;
		pattern_index++;
	    }

	    
	    return matched ? pattern_index + 1 : - 1;
	}

	return current == pattern.charAt(pattern_index) ? pattern_index + 1 : -1;
    }
    
    public BigDecimal monoisotopicMass() {
	BigDecimal sum = BigDecimal.ZERO;

	for(Protien p : sequence)
	    sum = sum.add(p.monoisotopicMass());

	return sum;
    }

    public ArrayList<BigDecimal> prefixes() {
	BigDecimal sum = BigDecimal.ZERO;
	ArrayList<BigDecimal> res = new ArrayList<BigDecimal>();
	for(int i = 0; i < sequence.size() - 1; i++) {
	    sum = sum.add(sequence.get(i).monoisotopicMass());
	    res.add(sum);
	}

	return res;
    }

    public ArrayList<BigDecimal> suffixes() {
	BigDecimal sum = BigDecimal.ZERO;
	ArrayList<BigDecimal> res = new ArrayList<BigDecimal>();
	for(int i = sequence.size() - 1; i > 0; i--) {
	    sum = sum.add(sequence.get(i).monoisotopicMass());
	    res.add(sum);
	}

	return res;
    }
    
    public BigInteger numCombinations(int mod) {
	BigInteger res = BigInteger.ONE;

	for(Protien p : sequence) {
	    res = res.multiply(BigInteger.valueOf(p.numSources()))
		.mod(BigInteger.valueOf(mod));
	    if(p == Protien.STOP)
		return res;
	}

	//stop codon
	res = res.multiply(BigInteger.valueOf(Protien.STOP.numSources()))
	    .mod(BigInteger.valueOf(mod));

	return res;
    }
}
