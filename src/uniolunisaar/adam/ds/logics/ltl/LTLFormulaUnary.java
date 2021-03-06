package uniolunisaar.adam.ds.logics.ltl;

import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.ds.logics.FormulaUnary;
import uniolunisaar.adam.exceptions.logics.NotConvertableException;
import uniolunisaar.adam.exceptions.logics.NotSubstitutableException;

/**
 *
 * @author Manuel Gieseking
 */
//class LTLFormulaUnary extends FormulaUnary<ILTLFormula, ILTLFormula, LTLOperators.Unary> implements ILTLFormula {
class LTLFormulaUnary extends FormulaUnary<ILTLFormula, LTLOperators.Unary> implements ILTLFormula, LoLAConvertable {

    public LTLFormulaUnary(LTLOperators.Unary op, ILTLFormula phi) {
        super(op, phi);
    }

    @Override
    public LTLFormulaUnary createSubstitutedFormula(IFormula subformula, IFormula with) throws NotSubstitutableException {
        IFormula phi1 = getPhi().substitute(subformula, with);
        if (phi1 instanceof ILTLFormula) {
            return new LTLFormulaUnary(getOp(), (ILTLFormula) phi1);
        } else {
            throw new NotSubstitutableException(
                    "The substituted subformula '" + phi1.toString() + "', created by substituting '"
                    + subformula.toString() + "' with '" + with.toString() + "'"
                    + " is not an LTL formula and thus cannot be used for '" + toString() + "'.");
        }
    }

    @Override
    public String toLoLA() throws NotConvertableException {
        return getOp().toLoLA() + " " + getPhi().toLoLA();
    }
}
