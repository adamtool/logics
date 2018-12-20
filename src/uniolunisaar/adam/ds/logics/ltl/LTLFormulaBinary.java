package uniolunisaar.adam.ds.logics.ltl;

import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.ds.logics.FormulaBinary;
import java.util.ArrayList;
import uniolunisaar.adam.exception.logics.NotSubstitutableException;

/**
 *
 * @author Manuel Gieseking
 */
//class LTLFormulaBinary extends FormulaBinary<ILTLFormula, ILTLFormula, LTLOperators.Binary, ILTLFormula> implements ILTLFormula {
class LTLFormulaBinary extends FormulaBinary<ILTLFormula, LTLOperators.Binary, ILTLFormula> implements ILTLFormula {

    public LTLFormulaBinary(ILTLFormula phi1, LTLOperators.Binary op, ILTLFormula phi2) {
        super(phi1, op, phi2);
    }

    @Override
    public ArrayList<ILTLFormula> getDirectSubFormulas() {
        ArrayList<ILTLFormula> subformulas = new ArrayList<>();
        subformulas.add(getPhi1());
        subformulas.add(getPhi2());
        return subformulas;
    }

    @Override
    public LTLFormulaBinary createSubstitutedFormula(IFormula subformula, IFormula with) throws NotSubstitutableException {
        IFormula phi1 = getPhi1().substitute(subformula, with);
        IFormula phi2 = getPhi2().substitute(subformula, with);
        if (phi1 instanceof ILTLFormula) {
            if (phi2 instanceof ILTLFormula) {
                return new LTLFormulaBinary((ILTLFormula) phi1, getOp(), (ILTLFormula) phi2);
            } else {
                throw new NotSubstitutableException(
                        "The substituted subformula '" + phi2.toString() + "', created by substituting '"
                        + subformula.toString() + "' with '" + with.toString() + "'"
                        + " is not an LTL formula and thus cannot be used for '" + toString() + "'.");
            }
        } else {
            throw new NotSubstitutableException(
                    "The substituted subformula '" + phi1.toString() + "', created by substituting '"
                    + subformula.toString() + "' with '" + with.toString() + "'"
                    + " is not an LTL formula and thus cannot be used for '" + toString() + "'.");
        }
    }

}
