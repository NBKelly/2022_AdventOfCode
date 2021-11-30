package com.nbkelly.lib.trie;

import java.util.HashMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.math.BigInteger;

//a trie containing all nodes by name and a searchable collection of nodes 
public class SuffixTrie {
    private HashMap<String, Node> nodesByName = new HashMap<>();
    private TreeSet<Node> search = new TreeSet<Node>();	
    private Node head = new Node(nodesByName);
    private String sequence;

    public SuffixTrie(String sequence) {
	this.sequence = sequence;
    }
    
    public void add(String parent, String name, int start, int end) {
	new Node(name, parent, start, end, nodesByName);
    }

    public void add(String parent, String name, String val) {
	new Node(name, parent, val, nodesByName);
    }

    /**
     * Gets a list of all edges (including duplicates) contained within this trie.
     */
    public ArrayList<String> edges() {
	ArrayList<String> str = new ArrayList<>();
	head.edge(str, sequence);
	return str;
    }

    public BigInteger substring_count_2(String seq) {
	return null;
    }
    
    /**
     * Gets a list of all unique suffixes in the trie
     */
    public static BigInteger substring_count(String seq) {
	//gets a list of all substrings than
	//String[] mid_results = new String[seq.length()];
	int length = seq.length();
	Integer[] mid_results = new Integer[length];
	
	for(int i = 0; i < length; i++) {
	    mid_results[i] = length - 1 - i;
	    //mid_results[i] = seq.substring(seq.length() - 1 - i, seq.length());
	}

	//System.out.println("Sorting");
	    
	Arrays.sort(mid_results, new Comparator<Integer>() {
		public int compare(Integer a, Integer b) {
		    String left = seq.substring(a, length);
		    String right = seq.substring(b, length);
		    return left.compareTo(right);
		}
	    });
	
	String tmp_a = seq.substring(mid_results[0], length);
	BigInteger num_substring = BigInteger.valueOf(tmp_a.length());
	//String tmp_b = null;//seq.substring(mid_results[1], length);
	
	//gets the number of unique substrings somewhere between O(nlogn) and O(n^2)
	for(int i = 0; i < length - 1; i++) {
	    //System.out.printf("%d of %d%n", i, length-1);
	    int j = 0;
	    //String tmp_a = seq.substring(mid_results[i], length);
	    String tmp_b = seq.substring(mid_results[i+1], length);
	    for(; j < tmp_a.length(); j++) {
		if(!(tmp_a.substring(0, j+1)).equals(tmp_b.substring(0, j+1))) {
		    break;
		}
	    }
	    num_substring = num_substring.add(BigInteger.valueOf(tmp_b.length() - j));
	    tmp_a = tmp_b;
	}
	
	//o(n^2), limit of 2^32 for count
	/*TreeSet<String> unique = new TreeSet<String>();
	for(String s : mid_results) {
	    //get all prefixes of s
	    for(int i = 1; i <= s.length(); i++)
		unique.add(s.substring(0, i));	
	}

	for(String s : unique)
	System.out.println(s);*/
	return num_substring;
	//return BigInteger.valueOf(unique.size());
    }
    
    public String[] maximalRepeats(int order, int len) {
	head.order();
	head.len();
	search = new TreeSet<Node>();	
	head.collect(search);

	ArrayList<Node> goodNodes = new ArrayList<Node>();

	for(Node n : search) {
	    if(n.order() >= order) {
		if(n.len() >= len)
		    goodNodes.add(n);
	    }
	    //else break;
	}

	String[] results = new String[goodNodes.size()];
	for(int i = 0; i < goodNodes.size(); i++) {
	    Node bestNode = goodNodes.get(i);
	    String res = "";
	    while(bestNode != head && bestNode != null) {
		String seg = bestNode.tag(sequence);
		//sequence.substring(bestNode.index_start, bestNode.index_start + bestNode.index_length);
		bestNode = bestNode.parent;
		res = seg + res;
	    }
	    results[i] = res;
	}

	return results;
    }
    
    /**
     * Finds the longest repeat of order n (that occurs n times).
     */
    public String longestRepeat(int order) {
	head.order();
	head.len();
	head.collect(search);
	Node bestNode = head;
	
	for(Node n : search) {
	    //println(n.order() -1 + ", " + n.len());
	    if(n.order() >= order) {		    
		if(n.len() > bestNode.len())
		    bestNode = n;
	    }
	}
	
	String res = "";
	
	while(bestNode != head && bestNode != null) {
	    String seg = bestNode.tag(sequence);
	    //sequence.substring(bestNode.index_start, bestNode.index_start + bestNode.index_length);
	    bestNode = bestNode.parent;
	    res = seg + res;
	}
	
	return res;
    }
}
