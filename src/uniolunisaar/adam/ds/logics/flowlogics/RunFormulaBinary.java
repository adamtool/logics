package uniolunisaar.adam.ds.logics.flowlogics;

import java.util.ArrayList;
import uniolunisaar.adam.ds.logics.FormulaBinary;
import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.ds.logics.ltl.flowltl.RunLTLFormula;
import uniolunisaar.adam.exceptions.logics.NotSubstitutableException;

/**
 *
 * Not really used so far. Maybe try to use it for have common things for CTL and LTL
 * @author Manuel Gieseking
 */
//class RunFormulaBinary extends FormulaBinary<IRunFormula, IRunFormula, RunOperators.Binary, IRunFormula> implements IRunFormula {
@Deprecated
abstract class RunFormulaBinary extends FormulaBinary<IRunFormula, RunOperators.Binary, IRunFormula> implements IRunFormula {

    public RunFormulaBinary(IRunFormula phi1, RunOperators.Binary op, IRunFormula phi2) {
        super(phi1, op, phi2);
    }

    @Override
    public ArrayList<IRunFormula> getDirectSubFormulas() {
        ArrayList<IRunFormula> subformulas = new ArrayList<>();
        subformulas.add(getPhi1());
        subformulas.add(getPhi2());
        return subformulas;
    }

}
