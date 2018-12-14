package uniolunisaar.adam.ds.logics.ctl;

import uniolunisaar.adam.ds.logics.IOperatorBinary;
import uniolunisaar.adam.ds.logics.IOperatorUnary;

/**
 *
 * @author Manuel Gieseking
 */
public class CTLOperators {
//
//    public enum Nullary implements IOperatorNullary {
//        TRUE {
//            @Override
//            public String toSymbol() {
//                return "⊤";// "\u22A4";
//            }
//        },
//        FALSE {
//            @Override
//            public String toSymbol() {
//                return "⊥";// "\u22A5";
//            }
//        }
//    }

    static final String exists = "E";
    static final String all = "A";

    public enum Unary implements IOperatorUnary<ICTLFormula> {
        EX {
            @Override
            public String toSymbol() {
                return exists + "X";
            }
        },
        AX {
            @Override
            public String toSymbol() {
                return all + "X";
            }
        },
        EF {
            @Override
            public String toSymbol() {
                return exists + "F";
            }
        },
        AF {
            @Override
            public String toSymbol() {
                return all + "F";
            }
        },
        EG {
            @Override
            public String toSymbol() {
                return exists + "G";
            }
        },
        AG {
            @Override
            public String toSymbol() {
                return all + "G";
            }
        },
        NEG {
            @Override
            public String toSymbol() {
                return "¬";// "\u00AC";
            }
        }
    };

    public enum Binary implements IOperatorBinary<ICTLFormula, ICTLFormula> {
        AND {
            @Override
            public String toSymbol() {
                return "⋏"; //" \u22CF " " \u2227 ";
            }
        },
        OR {
            @Override
            public String toSymbol() {
                return "⋎"; //" \u22CE " " \u2228 "
            }
        },
        IMP {
            @Override
            public String toSymbol() {
                return "→";//" \u2192 ";//" -> ";
            }
        },
        BIMP {
            @Override
            public String toSymbol() {
                return "↔";//" \u2194 ";//" <-> ";
            }
        },
        AU {
            @Override
            public String toSymbol() {
                return "𝓤"; //u+1D4E4
            }
        },
        EU {
            @Override
            public String toSymbol() {
                return "𝓤"; //u+1D4E4
            }
        }
    };
//
//    public enum EmbracingBinary implements IOperatorBinary<ICTLFormula, ICTLFormula> {
//        AU {
//            @Override
//            public String toSymbol() {
//                return "𝓤"; //u+1D4E4
//            }
//        },
//        EU {
//            @Override
//            public String toSymbol() {
//                return "𝓤"; //u+1D4E4
//            }
//        }
//    };

}
