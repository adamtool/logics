package uniolunisaar.adam.ds.logics.ltl;

import uniolunisaar.adam.ds.logics.IOperatorUnary;
import uniolunisaar.adam.ds.logics.IOperatorBinary;

/**
 *
 * @author Manuel Gieseking
 */
public class LTLOperators {
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

    public enum Unary implements IOperatorUnary<ILTLFormula> {
        F {
            @Override
            public String toSymbol() {
                return "◇";// "⟡";//"\u27E1" "◇"; //"\u25C7" " \u20DF";
            }
        },
        G {
            @Override
            public String toSymbol() {
                return "⬜"; // \u2B1C "\u25A1" &#9633; ";//&#x25A1;
            }
        },
        X {
            @Override
            public String toSymbol() {
                return "◯";//"\u25EF"; //"X";
            }
        },
        NEG {
            @Override
            public String toSymbol() {
                return "¬";// "\u00AC";
            }
        }
    };

    public enum Binary implements IOperatorBinary<ILTLFormula, ILTLFormula> {
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
        U {
            @Override
            public String toSymbol() {
                return "𝓤"; //u+1D4E4
            }
        },
        W {
            @Override
            public String toSymbol() {
                return "𝓦"; //u+1D4E6
            }
        },
        R {
            @Override
            public String toSymbol() {
                return "𝓡";// u+1D4E1";//" \u211B ";
            }
        }
    };

}
