package uniolunisaar.adam.ds.logics.ctl;

import uniolunisaar.adam.ds.logics.IOperatorBinary;
import uniolunisaar.adam.ds.logics.IOperatorUnary;
import uniolunisaar.adam.ds.logics.ltl.LoLAConvertable;
import uniolunisaar.adam.exceptions.logics.NotConvertableException;

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
    static final String existsLoLA = "E";
    static final String all = "A";
    static final String allLoLA = "A";

    public enum Path implements IOperatorUnary<ICTLFormula>, LoLAConvertable {
        E {
            @Override
            public String toSymbol() {
                return exists;
            }

            @Override
            public String toLoLA() throws NotConvertableException {
                return existsLoLA; // also EXPATH
            }

        },
        A {
            @Override
            public String toSymbol() {
                return all;
            }

            @Override
            public String toLoLA() throws NotConvertableException {
                return allLoLA; // also ALLPATH
            }

        }
    }

    public enum Unary implements IOperatorUnary<ICTLFormula>, LoLAConvertable {
        EX {
            @Override
            public String toSymbol() {
                return exists + "X";
            }

            @Override
            public String toLoLA() throws NotConvertableException {
                return existsLoLA + " X"; // also: NEXTSTATE
            }
        },
        AX {
            @Override
            public String toSymbol() {
                return all + "X";
            }

            @Override
            public String toLoLA() throws NotConvertableException {
                return allLoLA + " X"; // also: NEXTSTATE
            }
        },
        EF {
            @Override
            public String toSymbol() {
                return exists + "F";
            }

            @Override
            public String toLoLA() throws NotConvertableException {
                return existsLoLA + " F"; // also: EVENTUALLY
            }
        },
        AF {
            @Override
            public String toSymbol() {
                return all + "F";
            }

            @Override
            public String toLoLA() throws NotConvertableException {
                return allLoLA + " F"; // also: EVENTUALLY
            }
        },
        EG {
            @Override
            public String toSymbol() {
                return exists + "G";
            }

            @Override
            public String toLoLA() throws NotConvertableException {
                return existsLoLA + " G"; // also: GLOBALLY
            }
        },
        AG {
            @Override
            public String toSymbol() {
                return all + "G";
            }

            @Override
            public String toLoLA() throws NotConvertableException {
                return allLoLA + " G"; // also: GLOBALLY
            }
        },
        NEG {
            @Override
            public String toSymbol() {
                return "¬";// "\u00AC";
            }

            @Override
            public String toLoLA() throws NotConvertableException {
                return "NOT";
            }
        }
    };

    public enum Binary implements IOperatorBinary<ICTLFormula, ICTLFormula>, LoLAConvertable {
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
        AU {
            @Override
            public String toSymbol() {
                return "𝓤"; //u+1D4E4
            }

            @Override
            public String toLoLA() {
                return "UNTIL";// also UNTIL
            }
        },
        EU {
            @Override
            public String toSymbol() {
                return "𝓤"; //u+1D4E4
            }

            @Override
            public String toLoLA() {
                return "UNTIL"; // also UNTIL
            }
        },
        AR {
            @Override
            public String toSymbol() {
                return "R";
            }

            @Override
            public String toLoLA() {
                return "R"; // also RELEASE
            }
        },
        ER {
            @Override
            public String toSymbol() {
                return "R";
            }

            @Override
            public String toLoLA() {
                return "R";// also RELEASE
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
