package com.nbkelly.lib.bio;

import java.math.BigDecimal;

public enum Protien {
    F,
    L,
    S,
    Y,
    STOP,
    C,
    W,
    P,
    H,
    Q,
    R,
    I,
    M,
    T,
    N,
    K,
    V,
    A,
    D,
    E,
    B,
    Z,
    G;

    public int numSources() {
	switch(this) {
	case STOP: return 3;
	case G: return 4;
	case A: return 4;
	case V: return 4;
	case L: return 6;
	case I: return 3;
	case P: return 4;
	case F: return 2;
	case Y: return 2;
	case W: return 1;
	case S: return 6;
	case T: return 4;
	case C: return 2;
	case M: return 1;
	case N: return 2;
	case Q: return 2;
	case K: return 2;
	case R: return 6;
	case H: return 2;
	case D: return 2;
	case E: return 2;
	case B: return 4;
	case Z: return 4;
	}

	return 1;
    }
    
    public BigDecimal monoisotopicMass() {
	switch(this) {
	case A: return BigDecimal.valueOf(71.03711);
	case C: return BigDecimal.valueOf(103.00919);
	case D: return BigDecimal.valueOf(115.02694);
	case E: return BigDecimal.valueOf(129.04259);
	case F: return BigDecimal.valueOf(147.06841);
	case G: return BigDecimal.valueOf(57.02146);	
	case H: return BigDecimal.valueOf(137.05891);
	case I: return BigDecimal.valueOf(113.08406);
	case K: return BigDecimal.valueOf(128.09496);
	case L: return BigDecimal.valueOf(113.08406);
	case M: return BigDecimal.valueOf(131.04049);
	case N: return BigDecimal.valueOf(114.04293);
	case P: return BigDecimal.valueOf(97.05276);
	case Q: return BigDecimal.valueOf(128.05858);
	case R: return BigDecimal.valueOf(156.10111);
	case S: return BigDecimal.valueOf(87.03203);
	case T: return BigDecimal.valueOf(101.04768);
	case V: return BigDecimal.valueOf(99.06841);
	case W: return BigDecimal.valueOf(186.07931);
	case Y: return BigDecimal.valueOf(163.06333);
	}

	return BigDecimal.ZERO;
    }

    public static Protien closest(BigDecimal mass) {
	Protien best = null;
	BigDecimal bestDiff = null;
	for(var protien : values()) {
	    var diff = mass.subtract(protien.monoisotopicMass()).abs();
	    if(bestDiff == null || diff.compareTo(bestDiff) < 0) {
		bestDiff = diff;
		best = protien;
	    }
	}

	if(bestDiff.compareTo(BigDecimal.valueOf(0.01)) > 0)
	    return null;
	return best;
    }
}
