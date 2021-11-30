package com.nbkelly.lib.trie;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public class SuffixTrieFactory {
    public static SuffixTrie fromNodeIndices(String sequence, List<String> nodes) {
	//format:
	//parent, name, start, stop
	SuffixTrie trie = new SuffixTrie(sequence);
	for(String s : nodes) {
	    String[] seg = s.split(" ");
	    
	    String parent = seg[0];
	    String name = seg[1];
	    int start = Integer.parseInt(seg[2]);
	    int len = Integer.parseInt(seg[3]);

	    //we assume that the start in this is 1-indexed
	    trie.add(parent, name, start-1, len);
	}

	return trie;
    }
    
    public static SuffixTrie fromString(String sequence) {	
	Tmp_Trie_01 tmpTrie = new Tmp_Trie_01();
	
	for(int i = sequence.length()-1; i >= 0; i--) {
	    String suffix = sequence.substring(i);
	    tmpTrie.add(suffix, i+1);
	    
	}

	tmpTrie.identify();
	return fromNodeIndices(sequence, tmpTrie.getNodes());
    }
    
    private static class Tmp_Trie_01 {
	Tmp_Node head = new Tmp_Node();
	int _ID = 0;

	public ArrayList<String> getNodes() {
	    ArrayList<String> nodes = new ArrayList<String>();
	    head.getNodes(nodes);

	    return nodes;
	}
	
	public void identify() {
	    head.identify();
	}
	
	public void add(String s, int start) {
	    head.add(s, start);
	}

	private class Tmp_Node {
	    int id;
	    Tmp_Node parent = null;
	    String tag = "";

	    public void getNodes(ArrayList<String> nodes) {
		for(Edge e : edges.keySet()) {
		    var child = edges.get(e);
		    String res = String.format("node%d node%d %d %d", id, child.id, e.start, e.length);
		    //System.out.println(res);
		    nodes.add(res);
		    child.getNodes(nodes);
		}
	    }
	    
	    private class Edge implements Comparable<Edge>{
		public Edge(int start, String str) {
		    this.start = start;
		    this.str = str;
		    this.length = str.length();
		}
		
		int start;
		int length;
		String str;

		public int compareTo(Edge e) {
		    return e.str.compareTo(str);
		}
	    }
	    
	    public void identify() {		
		this.id = ++_ID;
		//System.out.println(this.id);
		for(Edge e : edges.keySet()) {
		    String s = e.str;
		    edges.get(e).tag = s;
		    edges.get(e).parent = this;
		    edges.get(e).identify();
		}
	    }
	    
	    private HashMap<Edge, Tmp_Node> edges = new HashMap<>();

	    public void print() {
		for(Edge e : edges.keySet()) {
		    String s = e.str;
		    edges.get(s).print();
		}
	    }
	    
	    public void add(String s, int start) {
		//go through all edges and see if one exists that shares some starting characters
		boolean matched = false;
		for(Edge edge : edges.keySet()) {
		    String str = edge.str;
		    //how many characters match?
		    int match_len = match(str, s);
		    //if the first n > 0 characters match, we must reprocess this edge
		    if(match_len > 0) {			
			//get the old node
			Tmp_Node oldNode = edges.get(edge);

			//if the whole swtring is a match (maybe repeated sections of a string),
			//then we just add the substring to the old node
			if(match_len == edge.length) {
			    oldNode.add(s.substring(match_len), start + match_len);
			    edge.start = start;
			}
			//if there is a partial match, then we must segment the old node
			//and insert an intermediate node within the existing edge
			else {
			    //get the prefix and the suffix			    
			    String pre = str.substring(0, match_len);
			    String post = str.substring(match_len);
			    Edge postEdge = new Edge(edge.start + match_len, post);
			    Edge preEdge = new Edge(/*edge.*/start, pre);
			    //create our new node, and populate it with an edge to the old node
			    Tmp_Node newNode = new Tmp_Node();
			    newNode.edges.put(postEdge, oldNode);
			    //also add the suffix to the new node
			    newNode.add(s.substring(match_len), start + match_len);
			    //add our new node to the edge map
			    edges.put(preEdge, newNode);
			    //remove the old node from the edge map (the new node keeps track of it)
			    edges.remove(edge);
			}
			
			//remember that we are done
			matched = true;
			break;
		    }		    
		}
		
		if(!matched) {
		    Tmp_Node n = new Tmp_Node();
		    edges.put(new Edge(start, s), n);
		}
	    }
	}

    }

    private static int match(String left, String right) {
	int val = 0;
	for(int i = 0; i < Math.min(left.length(), right.length()); i++)
	    if(left.charAt(i) == right.charAt(i))
		val++;
	    else
		break;

	return val;
    }
}
