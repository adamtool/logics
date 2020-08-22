package uniolunisaar.adam.ds.logics.ctl.flowctl;

import uniolunisaar.adam.ds.logics.ctl.ICTLFormula;
import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.ds.logics.IOperatorUnary;
import uniolunisaar.adam.ds.logics.ctl.flowctl.FlowCTLFormula.FlowCTLOperator;
import uniolunisaar.adam.ds.logics.flowlogics.FlowFormula;
import uniolunisaar.adam.ds.logics.flowlogics.FlowOperator;
import uniolunisaar.adam.exceptions.logics.NotSubstitutableException;

/**
 *
 * @author Manuel Gieseking
 */
//public class FlowCTLFormula extends FormulaUnary<ICTLFormula, FlowCTLFormula.FlowCTLOperator> implements IFlowCTLFormula {
public class FlowCTLFormula extends FlowFormula<ICTLFormula, FlowCTLOperator> {

    public enum FlowCTLOperator implements IOperatorUnary<ICTLFormula> {
        All {
            @Override
            public String toSymbol() {
                return FlowOperator.A.toSymbol();
            }
        },
        Exists {
            @Override
            public String toSymbol() {
                return FlowOperator.E.toSymbol();
            }
        }
    };

    public FlowCTLFormula(FlowCTLOperator op, ICTLFormula phi) {
        super(op, phi);
    }

    @Override
    public IFormula createSubstitutedFormula(IFormula subformula, IFormula with) throws NotSubstitutableException {
        IFormula f = getPhi().substitute(subformula, with);
        if (f instanceof ICTLFormula) {
            return new FlowCTLFormula(getOp(), (ICTLFormula) f);
        } else {
            throw new NotSubstitutableException(
                    "The substituted subformula '" + f.toString() + "', created by substituting '"
                    + subformula.toString() + "' with '" + with.toString() + "'"
                    + " is not an LTL formula and thus cannot be used for '" + toString() + "'.");
        }
    }
}
