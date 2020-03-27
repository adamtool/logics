package uniolunisaar.adam.ds.logics.ltl;

import uniolunisaar.adam.ds.logics.IOperatorUnary;
import uniolunisaar.adam.ds.logics.IOperatorBinary;
import uniolunisaar.adam.exceptions.logics.NotConvertableException;

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

    public enum Unary implements IOperatorUnary<ILTLFormula>, LoLAConvertable {
        F {
            @Override
            public String toSymbol() {
                return "◇";// "⟡";//"\u27E1" "◇"; //"\u25C7" " \u20DF";
            }

            @Override
            public String toLoLA() {
                return "EVENTUALLY";
            }
        },
        G {
            @Override
            public String toSymbol() {
                return "⬜"; // \u2B1C "\u25A1" &#9633; ";//&#x25A1;
            }

            @Override
            public String toLoLA() {
                return "ALWAYS";
            }
        },
        X {
            @Override
            public String toSymbol() {
                return "◯";//"\u25EF"; //"X";
            }

            @Override
            public String toLoLA() {
                return "NEXTSTATE";
            }
        },
        NEG {
            @Override
            public String toSymbol() {
                return "¬";// "\u00AC";
            }

            @Override
            public String toLoLA() {
                return "NOT";
            }
        }
    };

    public enum Binary implements IOperatorBinary<ILTLFormula, ILTLFormula>, LoLAConvertable {
        AND {
            @Override
            public String toSymbol() {
                return "⋏"; //" \u22CF " " \u2227 ";
            }

            @Override
            public String toLoLA() {
                return "AND";
            }
        },
        OR {
            @Override
            public String toSymbol() {
                return "⋎"; //" \u22CE " " \u2228 "
            }

            @Override
            public String toLoLA() {
                return "OR";
            }
        },
        IMP {
            @Override
            public String toSymbol() {
                return "→";//" \u2192 ";//" -> ";
            }

            @Override
            public String toLoLA() {
                return "->";
            }
        },
        BIMP {
            @Override
            public String toSymbol() {
                return "↔";//" \u2194 ";//" <-> ";
            }

            @Override
            public String toLoLA() {
                return "<->";
            }
        },
        U {
            @Override
            public String toSymbol() {
                return "𝓤"; //u+1D4E4
            }

            @Override
            public String toLoLA() {
                return "UNTIL";
            }
        },
        W {
            @Override
            public String toSymbol() {
                return "𝓦"; //u+1D4E6
            }

            @Override
            public String toLoLA() throws NotConvertableException {
                throw new NotConvertableException("LoLA does not support a weak until.");
            }
        },
        R {
            @Override
            public String toSymbol() {
                return "𝓡";// u+1D4E1";//" \u211B ";
            }

            @Override
            public String toLoLA() {
                return "RELEASE";
            }
        }
    };

}
