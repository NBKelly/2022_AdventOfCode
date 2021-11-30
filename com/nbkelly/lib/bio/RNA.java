package com.nbkelly.lib.bio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * An RNA sequence
 * @author NB Kelly <N.B.Kelly@protonmail.com>
 * @since 1.8
 */
public class RNA extends XNA {
    /**
     * Constructs an RNA sequence from a string, asserting that it is a legal RNA sequence
     */
    public RNA(String sequence) {
	//TODO: assert sequence is legal
	super(sequence);
    }

    /**
     * Constructs an RNA sequence from an already existing sequence without any sanity checks
     */
    protected RNA(ArrayList<Nucleotide> sequence) {
	super(sequence);
    }

    /**
     * Converts an RNA sequence to a DNA sequence
     * @returns a DNA sequence that is the transcription of this RNA sequence
     */
    public DNA toDNA() {
	ArrayList<Nucleotide> res = new ArrayList<Nucleotide>();
	for(Nucleotide n : sequence) {
	    switch(n) {
	    case U:
		res.add(Nucleotide.T);
		break;
	    default:
		res.add(n);
	    }
	}

	return new DNA(res);
    }


    /**
     * Gets the complement of the RNA sequence
     * @return the complement of this RNA sequence
     */
    @Override public RNA complement() {
	ArrayList<Nucleotide> res = new ArrayList<Nucleotide>();
	for(Nucleotide n : sequence) {
	    switch(n) {
	    case U:
		res.add(Nucleotide.A);
		break;
	    case A:
		res.add(Nucleotide.U);
		break;
	    case C:
		res.add(Nucleotide.G);
		break;
	    case G:
		res.add(Nucleotide.C);
		break;
	    }
	}

	return new RNA(res);
    }    
    
    @Override public RNA reverse() {
	ArrayList<Nucleotide> res = new ArrayList<>(sequence);
	Collections.reverse(res);
	return new RNA(res);
    }


    /*UUU F      CUU L      AUU I      GUU V
      UUC F      CUC L      AUC I      GUC V
      UUA L      CUA L      AUA I      GUA V
      UUG L      CUG L      AUG M      GUG V

      UCU S      CCU P      ACU T      GCU A
      UCC S      CCC P      ACC T      GCC A
      UCA S      CCA P      ACA T      GCA A
      UCG S      CCG P      ACG T      GCG A

      UAU Y      CAU H      AAU N      GAU D
      UAC Y      CAC H      AAC N      GAC D
      UAA Stop   CAA Q      AAA K      GAA E
      UAG Stop   CAG Q      AAG K      GAG E

      UGU C      CGU R      AGU S      GGU G
      UGC C      CGC R      AGC S      GGC G
      UGA Stop   CGA R      AGA R      GGA G
      UGG W      CGG R      AGG R      GGG G */

    /**
     * Gets the protien encoded by this RNA strand in the trivial reading frame.
     * No check is made as to wether the protien is valid,
     * but reading will end up sight of a STOP codon
     *
     * @return the protien encoded by this RNA strand in the trivial reading frame
     */
    public ProtienString toProtien() {
	return toProtien(1);
    }

    /**
     * Gets the protien encoded by this RNA strand in the given reading frame.
     * No check is made as to wether the protien is valid,
     * but reading will end up sight of a STOP codon.
     * <p>
     * The valid frames are 1-3 (for offsets 0, 1, 2) and 4-6 (which are those frames on the
     * reverse complement of this sequence.)
     * 
     * @return the protien encoded by this RNA strand in the given reading frame
     */
    public ProtienString toProtien(int frame) {
	if(frame < 1 || frame > 6)
	    throw new ArithmeticException("Invalid protien frame");
    
	if(frame > 3)
	    return reverse().complement().toProtien(frame - 3);
	
	//first, seperate this RNA into triplets
	ArrayList<RNA> triplets = new ArrayList<RNA>();
	for(int i = frame-1; i + 2 < sequence.size(); i+= 3)
	    triplets.add(new RNA(new ArrayList<>(sequence.subList(i, i + 3))));

	ArrayList<Protien> prtty = new ArrayList<Protien>();
	for(RNA rna : triplets) {
	    var prt = codons().get(rna);
	    if(prt == Protien.STOP)
		break;
	    else
		prtty.add(prt);
	}

	return new ProtienString(prtty);
    }

    public TreeSet<ProtienString> toValidProtiens() {
	TreeSet<ProtienString> res = new TreeSet<ProtienString>();
	for(int i = 1; i <= 6; i++)
	    res.addAll(toValidProtiens(i));

	return res;
    }
    
    /**
     * Gets the protien encoded by this RNA strand in the given reading frame.
     * No check is made as to wether the protien is valid,
     * but reading will end up sight of a STOP codon.
     * <p>
     * The valid frames are 1-3 (for offsets 0, 1, 2) and 4-6 (which are those frames on the
     * reverse complement of this sequence.)
     * 
     * @return the protien encoded by this RNA strand in the given reading frame
     */
    public ArrayList<ProtienString> toValidProtiens(int frame) {
	if(frame < 1 || frame > 6)
	    throw new ArithmeticException("Invalid protien frame");
    
	if(frame > 3)
	    return reverse().complement().toValidProtiens(frame - 3);

	ArrayList<ProtienString> res = new ArrayList<>();
	
	//first, seperate this RNA into triplets
	ArrayList<RNA> triplets = new ArrayList<RNA>();
	for(int i = frame-1; i + 2 < sequence.size(); i+= 3)
	    triplets.add(new RNA(new ArrayList<>(sequence.subList(i, i + 3))));
	
	int index = 0;
	for(RNA rna : triplets) {
	    if(codons().get(rna) == Protien.M) { //start
		var string = toValidProtien(index, triplets);
		if(string != null)
		    res.add(string);
	    }
	    index++;
	}

	return res;
    }

    private ProtienString toValidProtien(int startAt, ArrayList<RNA> triplets) {
	ArrayList<Protien> prtty = new ArrayList<Protien>();
	boolean stopped = false;

	for(int i = startAt; i < triplets.size(); i++) {
	    var prt = codons().get(triplets.get(i));
	    if(stopped = (prt == Protien.STOP))
		break;
	    else
		prtty.add(prt);
	}

	if(stopped)
	    return new ProtienString(prtty);
	return null;
    }


    private static HashMap<RNA, Protien> _CODONS;
    private HashMap<RNA, Protien> codons() {
	if(_CODONS != null)
	    return _CODONS;
	
	HashMap<RNA, Protien> res = new HashMap<>();

	res.put(new RNA("UUU"),  Protien.F);
	res.put(new RNA("UUC"),  Protien.F);
	res.put(new RNA("UUA"),  Protien.L);
	res.put(new RNA("UUG"),  Protien.L);

	res.put(new RNA("UCU"),  Protien.S);
	res.put(new RNA("UCC"),  Protien.S);
	res.put(new RNA("UCA"),  Protien.S);
	res.put(new RNA("UCG"),  Protien.S);

	res.put(new RNA("UAU"),  Protien.Y);
	res.put(new RNA("UAC"),  Protien.Y);
	res.put(new RNA("UAA"),  Protien.STOP);
	res.put(new RNA("UAG"),  Protien.STOP);

	res.put(new RNA("UGU"),  Protien.C);
	res.put(new RNA("UGC"),  Protien.C);
	res.put(new RNA("UGA"),  Protien.STOP);
	res.put(new RNA("UGG"),  Protien.W);
	

	res.put(new RNA("CUU"),  Protien.L);
	res.put(new RNA("CUC"),  Protien.L);
	res.put(new RNA("CUA"),  Protien.L);
	res.put(new RNA("CUG"),  Protien.L);

	res.put(new RNA("CCU"),  Protien.P);
	res.put(new RNA("CCC"),  Protien.P);
	res.put(new RNA("CCA"),  Protien.P);
	res.put(new RNA("CCG"),  Protien.P);

	res.put(new RNA("CAU"),  Protien.H);
	res.put(new RNA("CAC"),  Protien.H);
	res.put(new RNA("CAA"),  Protien.Q);
	res.put(new RNA("CAG"),  Protien.Q);

	res.put(new RNA("CGU"),  Protien.R);
	res.put(new RNA("CGC"),  Protien.R);
	res.put(new RNA("CGA"),  Protien.R);
	res.put(new RNA("CGG"),  Protien.R);


	res.put(new RNA("AUU"),  Protien.I);
	res.put(new RNA("AUC"),  Protien.I);
	res.put(new RNA("AUA"),  Protien.I);
	res.put(new RNA("AUG"),  Protien.M);

	res.put(new RNA("ACU"),  Protien.T);
	res.put(new RNA("ACC"),  Protien.T);
	res.put(new RNA("ACA"),  Protien.T);
	res.put(new RNA("ACG"),  Protien.T);

	res.put(new RNA("AAU"),  Protien.N);
	res.put(new RNA("AAC"),  Protien.N);
	res.put(new RNA("AAA"),  Protien.K);
	res.put(new RNA("AAG"),  Protien.K);

	res.put(new RNA("AGU"),  Protien.S);
	res.put(new RNA("AGC"),  Protien.S);
	res.put(new RNA("AGA"),  Protien.R);
	res.put(new RNA("AGG"),  Protien.R);

	
	res.put(new RNA("GUU"),  Protien.V);
	res.put(new RNA("GUC"),  Protien.V);
	res.put(new RNA("GUA"),  Protien.V);
	res.put(new RNA("GUG"),  Protien.V);

	res.put(new RNA("GCU"),  Protien.A);
	res.put(new RNA("GCC"),  Protien.A);
	res.put(new RNA("GCA"),  Protien.A);
	res.put(new RNA("GCG"),  Protien.A);

	res.put(new RNA("GAU"),  Protien.D);
	res.put(new RNA("GAC"),  Protien.D);
	res.put(new RNA("GAA"),  Protien.E);
	res.put(new RNA("GAG"),  Protien.E);

	res.put(new RNA("GGU"),  Protien.G);
	res.put(new RNA("GGC"),  Protien.G);
	res.put(new RNA("GGA"),  Protien.G);
	res.put(new RNA("GGG"),  Protien.G);	
	return _CODONS = res;
    }    
}
