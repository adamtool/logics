package uniolunisaar.adam.ds.logics.ctl;

import uniolunisaar.adam.ds.logics.FormulaUnary;
import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.exceptions.logics.NotSubstitutableException;

/**
 *
 * @author Manuel Gieseking
 */
//class CTLFormulaUnary extends FormulaUnary<ILTLFormula, ICTLFormula, CTLOperators.Unary> implements ICTLFormula {
class CTLFormulaUnary extends FormulaUnary<ICTLFormula, CTLOperators.Unary> implements ICTLFormula {

    public CTLFormulaUnary(CTLOperators.Unary op, ICTLFormula phi) {
        super(op, phi);
    }

    @Override
    public CTLFormulaUnary createSubstitutedFormula(IFormula subformula, IFormula with) throws NotSubstitutableException {
        IFormula phi1 = getPhi().substitute(subformula, with);
        if (phi1 instanceof ICTLFormula) {
            return new CTLFormulaUnary(getOp(), (ICTLFormula) phi1);
        } else {
            throw new NotSubstitutableException(
                    "The substituted subformula '" + phi1.toString() + "', created by substituting '"
                    + subformula.toString() + "' with '" + with.toString() + "'"
                    + " is not an LTL formula and thus cannot be used for '" + toString() + "'.");
        }
    }
}
