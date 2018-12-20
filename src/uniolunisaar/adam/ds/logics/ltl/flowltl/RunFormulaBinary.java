package uniolunisaar.adam.ds.logics.ltl.flowltl;

import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.ds.logics.FormulaBinary;
import java.util.ArrayList;
import uniolunisaar.adam.exception.logics.NotSubstitutableException;

/**
 *
 * @author Manuel Gieseking
 */
//class RunFormulaBinary extends FormulaBinary<IRunFormula, IRunFormula, RunOperators.Binary, IRunFormula> implements IRunFormula {
class RunFormulaBinary extends FormulaBinary<IRunFormula, RunOperators.Binary, IRunFormula> implements IRunFormula {

    public RunFormulaBinary(IRunFormula phi1, RunOperators.Binary op, IRunFormula phi2) {
        super(phi1, op, phi2);
    }

    @Override
    public RunFormula createSubstitutedFormula(IFormula subformula, IFormula with) throws NotSubstitutableException {
        IFormula phi1 = getPhi1().substitute(subformula, with);
        IFormula phi2 = getPhi2().substitute(subformula, with);
        if (phi1 instanceof IRunFormula) {
            if (phi2 instanceof IRunFormula) {
                return new RunFormula((IRunFormula) phi1, getOp(), (IRunFormula) phi2);
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
                    + " is not a Runformula and thus cannot be used for '" + toString() + "'.");
        }
    }

    @Override
    public ArrayList<IRunFormula> getDirectSubFormulas() {
        ArrayList<IRunFormula> subformulas = new ArrayList<>();
        subformulas.add(getPhi1());
        subformulas.add(getPhi2());
        return subformulas;
    }
}
