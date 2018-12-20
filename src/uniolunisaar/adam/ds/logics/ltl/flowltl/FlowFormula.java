package uniolunisaar.adam.ds.logics.ltl.flowltl;

import uniolunisaar.adam.ds.logics.IOperatorUnary;
import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.ds.logics.FormulaUnary;
import java.util.ArrayList;
import uniolunisaar.adam.ds.logics.ltl.ILTLFormula;
import uniolunisaar.adam.exception.logics.NotSubstitutableException;

/**
 *
 * @author Manuel Gieseking
 */
//public class FlowFormula extends FormulaUnary<IFlowFormula, ILTLFormula, FlowFormula.FlowOperator> implements IFlowFormula {
public class FlowFormula extends FormulaUnary<ILTLFormula, FlowFormula.FlowOperator> implements IFlowFormula {

    public enum FlowOperator implements IOperatorUnary<ILTLFormula> {
        A {
            @Override
            public String toSymbol() {
                return "𝔸";//u+1D538 "\u2200";
            }
        }
    };

    public FlowFormula(ILTLFormula phi) {
        super(FlowOperator.A, phi);
    }

    @Override
    public ArrayList<ILTLFormula> getDirectSubFormulas() {
        ArrayList<ILTLFormula> subformulas = new ArrayList<>();
        subformulas.add(getPhi());
        return subformulas;
    }

    @Override
    public IFormula createSubstitutedFormula(IFormula subformula, IFormula with) throws NotSubstitutableException {
        IFormula f = getPhi().substitute(subformula, with);
        if (f instanceof ILTLFormula) {
            return new FlowFormula((ILTLFormula) f);
        } else {
            throw new NotSubstitutableException(
                    "The substituted subformula '" + f.toString() + "', created by substituting '"
                    + subformula.toString() + "' with '" + with.toString() + "'"
                    + " is not an LTL formula and thus cannot be used for '" + toString() + "'.");
        }
    }
}
