package uniolunisaar.adam.ds.logics.ltl.flowltl;

import uniolunisaar.adam.ds.logics.IOperatorUnary;
import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.ds.logics.flowlogics.FlowOperator;
import uniolunisaar.adam.ds.logics.ltl.ILTLFormula;
import uniolunisaar.adam.exceptions.logics.NotSubstitutableException;
import uniolunisaar.adam.ds.logics.flowlogics.FlowFormula;

/**
 *
 * @author Manuel Gieseking
 */
public class FlowLTLFormula extends FlowFormula<ILTLFormula, FlowLTLFormula.FlowLTLOperator> {

    public enum FlowLTLOperator implements IOperatorUnary<ILTLFormula> {
        A {
            @Override
            public String toSymbol() {
                return FlowOperator.A.toSymbol();
            }
        }
    };

    public FlowLTLFormula(ILTLFormula phi) {
        super(FlowLTLOperator.A, phi);
    }

    @Override
    public IFormula createSubstitutedFormula(IFormula subformula, IFormula with) throws NotSubstitutableException {
        IFormula f = getPhi().substitute(subformula, with);
        if (f instanceof ILTLFormula) {
            return new FlowLTLFormula((ILTLFormula) f);
        } else {
            throw new NotSubstitutableException(
                    "The substituted subformula '" + f.toString() + "', created by substituting '"
                    + subformula.toString() + "' with '" + with.toString() + "'"
                    + " is not an LTL formula and thus cannot be used for '" + toString() + "'.");
        }
    }
}
