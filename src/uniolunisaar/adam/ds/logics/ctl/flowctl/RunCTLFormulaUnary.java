package uniolunisaar.adam.ds.logics.ctl.flowctl;

import uniolunisaar.adam.ds.logics.FormulaUnary;
import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.exceptions.logics.NotSubstitutableException;
import uniolunisaar.adam.ds.logics.flowlogics.IRunFormula;

/**
 *
 * @author Manuel Gieseking
 */
class RunCTLFormulaUnary extends FormulaUnary<IRunFormula, RunCTLOperators.Unary> implements IRunFormula {

    public RunCTLFormulaUnary(RunCTLOperators.Unary op, IRunFormula phi) {
        super(op, phi);
    }

    @Override
    public RunCTLFormula createSubstitutedFormula(IFormula subformula, IFormula with) throws NotSubstitutableException {
        IFormula phi = getPhi().substitute(subformula, with);
        if (phi instanceof IRunFormula) {
            return new RunCTLFormula(getOp(), (IRunFormula) phi);
        } else {
            throw new NotSubstitutableException(
                    "The substituted subformula '" + phi.toString() + "', created by substituting '"
                    + subformula.toString() + "' with '" + with.toString() + "'"
                    + " is not a Runformula and thus cannot be used for '" + toString() + "'.");
        }
    }

}
