package uniolunisaar.adam.ds.logics.ctl.flowctl;

import uniolunisaar.adam.ds.logics.flowlogics.*;
import uniolunisaar.adam.ds.logics.IOperatorBinary;

/**
 *
 * @author Manuel Gieseking
 */
public class RunCTLOperators {

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
        },
        IMP {
            @Override
            public String toSymbol() {
                return "⇒";//" \u21D2 ";
            }
        },
        BIMP {
            @Override
            public String toSymbol() {
                return "⇔";
            }
        }
    }
}
