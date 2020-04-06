package uniolunisaar.adam.ds.logics.ltl.flowltl;

import java.util.ArrayList;
import uniolunisaar.adam.ds.logics.FormulaBinary;
import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.ds.logics.ltl.ILTLFormula;
import uniolunisaar.adam.exceptions.logics.NotSubstitutableException;
import uniolunisaar.adam.ds.logics.flowlogics.IRunFormula;
import uniolunisaar.adam.ds.logics.flowlogics.RunOperators;

/**
 *
 * @author Manuel Gieseking
 */
class RunLTLFormulaImplication extends FormulaBinary<ILTLFormula, RunOperators.Implication, IRunFormula> implements IRunFormula {

    public RunLTLFormulaImplication(ILTLFormula phi1, RunOperators.Implication op, IRunFormula phi2) {
        super(phi1, op, phi2);
    }

    @Override
    public RunLTLFormulaImplication createSubstitutedFormula(IFormula subformula, IFormula with) throws NotSubstitutableException {
        IFormula phi1 = getPhi1().substitute(subformula, with);
        IFormula phi2 = getPhi2().substitute(subformula, with);
        if (phi1 instanceof ILTLFormula) {
            if (phi2 instanceof IRunFormula) {
                return new RunLTLFormulaImplication((ILTLFormula) phi1, getOp(), (IRunFormula) phi2);
            } else {
                throw new NotSubstitutableException(
                        "The substituted subformula '" + phi2.toString() + "', created by substituting '"
                        + subformula.toString() + "' with '" + with.toString() + "'"
                        + " is not a Runformula and thus cannot be used for '" + toString() + "'.");
            }
        } else {
            throw new NotSubstitutableException(
                    "The substituted subformula '" + phi1.toString() + "', created by substituting '"
                    + subformula.toString() + "' with '" + with.toString() + "'"
                    + " is not an LTL formula and thus cannot be used for '" + toString() + "'.");
        }
    }

    @Override
    public ArrayList<IFormula> getDirectSubFormulas() {
        ArrayList<IFormula> subformulas = new ArrayList<>();
        subformulas.add(getPhi1());
        subformulas.add(getPhi2());
        return subformulas;
    }
}
