package uniolunisaar.adam.ds.logics.flowlogics;

import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.ds.logics.IOperatorUnary;

/**
 *
 * @author Manuel Gieseking
 */
public enum FlowOperator implements IOperatorUnary<IFormula>{
    A {
        @Override
        public String toSymbol() {
            return "𝔸";//u+1D538 "\u2200";
        }
    },
    E {
        @Override
        public String toSymbol() {
            return "𝔼";//U+1D53C (120124)
        }
    }
};
