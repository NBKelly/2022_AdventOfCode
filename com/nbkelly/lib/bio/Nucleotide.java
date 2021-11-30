package com.nbkelly.lib.bio;

public enum Nucleotide {
    A,C,G,
    T,
    U,

    W,S,M,
    K,R,Y,
    B,
    D,
    H,
    V,
    N;

    /*@Override public int hashCode() {
	return 31 * toString().hashCode();
	}*/
    
    public static Nucleotide fromString(String str) {
	switch (str) {
	case "a":
	case "A":
	    return A;
	case "c":
	case "C":
	    return C;
	case "g":
	case "G":
	    return G;
	case "t":
	case "T":
	    return T;
	case "u":
	case "U":
	    return U;

	case "w":
	case "W":
	    return W;
	case "s":
	case "S":
	    return S;
	case "m":
	case "M":
	    return M;
	case "k":
	case "K":
	    return K;
	case "r":
	case "R":
	    return R;
	case "y":
	case "Y":
	    return Y;
	}
	return null;
    }

    public static Nucleotide fromChar(Character c) {
	return fromString("" + c);
    }
    
    public static String fullName(Nucleotide a) {
	switch(a) {
	case A:
	    return "adenine";
	case C:
	    return "cytosine";
	case G:
	    return "guanine";
	case T:
	    return "thymine";
	case U:
	    return "uracil";
	    
	case W:
	    return "weak (AT)";
	case S:
	    return "strong (CG)";
	case M:
	    return "amine (AC)";
	case K:
	    return "keto (GT)";
	case R:
	    return "purine (AG)";
	case Y:
	    return "pyrimidine (CT)";

	case B:
	    return "not A";
	case D:
	    return "not C";
	case H:
	    return "not G";
	case V:
	    return "not T/U";

	case N:
	    return "any (no gap)";
	    
	default:
	    return null;
	}
    }
}
