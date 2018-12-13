package uniolunisaar.adam.ds.logics.ltl.flowltl;

import uniolunisaar.adam.ds.logics.IOperatorBinary;
import uniolunisaar.adam.ds.logics.ltl.ILTLFormula;

/**
 *
 * @author Manuel Gieseking
 */
public class RunOperators {

    public enum Binary implements IOperatorBinary<IRunFormula, IRunFormula> {
        AND {
            @Override
            public String toSymbol() {
                return "⋀";//" \u22C0 " " \u2227 ";
            }
        },
        OR {
            @Override
            public String toSymbol() {
                return "⋁";//" \u22C1 " " \u2228 ";
            }
        }
    };

    public enum Implication implements IOperatorBinary<ILTLFormula, IRunFormula> {
        IMP {
            @Override
            public String toSymbol() {
                return "⇒";//" \u21D2 ";
            }
        },

    }
}
