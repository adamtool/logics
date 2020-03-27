package uniolunisaar.adam.ds.logics.ctl.flowctl;

import uniolunisaar.adam.ds.logics.ctl.ICTLFormula;
import java.util.ArrayList;
import uniolunisaar.adam.ds.logics.FormulaUnary;
import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.ds.logics.IOperatorUnary;
import uniolunisaar.adam.exceptions.logics.NotSubstitutableException;

/**
 *
 * @author Manuel Gieseking
 */
//public class FlowCTLFormula extends FormulaUnary<IFlowFormula, ICTLFormula, FlowCTLFormula.FlowCTLOperator> implements IFlowCTLFormula {
public class FlowCTLFormula extends FormulaUnary<ICTLFormula, FlowCTLFormula.FlowCTLOperator> implements IFlowCTLFormula {

    public enum FlowCTLOperator implements IOperatorUnary<ICTLFormula> {
        All {
            @Override
            public String toSymbol() {
                return "𝔸";//u+1D538 "\u2200";
            }
        }
    };

    public FlowCTLFormula(ICTLFormula phi) {
        super(FlowCTLOperator.All, phi);
    }

    @Override
    public ArrayList<ICTLFormula> getDirectSubFormulas() {
        ArrayList<ICTLFormula> subformulas = new ArrayList<>();
        subformulas.add(getPhi());
        return subformulas;
    }

    @Override
    public IFormula createSubstitutedFormula(IFormula subformula, IFormula with) throws NotSubstitutableException {
        IFormula f = getPhi().substitute(subformula, with);
        if (f instanceof ICTLFormula) {
            return new FlowCTLFormula((ICTLFormula) f);
        } else {
            throw new NotSubstitutableException(
                    "The substituted subformula '" + f.toString() + "', created by substituting '"
                    + subformula.toString() + "' with '" + with.toString() + "'"
                    + " is not an LTL formula and thus cannot be used for '" + toString() + "'.");
        }
    }
}
