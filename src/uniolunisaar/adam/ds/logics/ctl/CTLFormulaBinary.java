package uniolunisaar.adam.ds.logics.ctl;

import java.util.ArrayList;
import java.util.List;
import uniolunisaar.adam.ds.logics.AtomicProposition;
import uniolunisaar.adam.ds.logics.FormulaBinary;
import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.exceptions.logics.NotConvertableException;
import uniolunisaar.adam.exceptions.logics.NotSubstitutableException;

/**
 *
 * @author Manuel Gieseking
 */
//class CTLFormulaBinary extends FormulaBinary<ILTLFormula, ICTLFormula, CTLOperators.Binary, ICTLFormula> implements ICTLFormula {
public class CTLFormulaBinary extends FormulaBinary<ICTLFormula, CTLOperators.Binary, ICTLFormula> implements ICTLFormula {

    CTLFormulaBinary(ICTLFormula phi1, CTLOperators.Binary op, ICTLFormula phi2) {
        super(phi1, op, phi2);
    }

    @Override
    public ArrayList<ICTLFormula> getDirectSubFormulas() {
        ArrayList<ICTLFormula> subformulas = new ArrayList<>();
        subformulas.add(getPhi1());
        subformulas.add(getPhi2());
        return subformulas;
    }

    @Override
    public CTLFormulaBinary createSubstitutedFormula(IFormula subformula, IFormula with) throws NotSubstitutableException {
        IFormula phi1 = getPhi1().substitute(subformula, with);
        IFormula phi2 = getPhi2().substitute(subformula, with);
        if (phi1 instanceof ICTLFormula) {
            if (phi2 instanceof ICTLFormula) {
                return new CTLFormulaBinary((ICTLFormula) phi1, getOp(), (ICTLFormula) phi2);
            } else {
                throw new NotSubstitutableException(
                        "The substituted subformula '" + phi2.toString() + "', created by substituting '"
                        + subformula.toString() + "' with '" + with.toString() + "'"
                        + " is not a CTL formula and thus cannot be used for '" + toString() + "'.");
            }
        } else {
            throw new NotSubstitutableException(
                    "The substituted subformula '" + phi1.toString() + "', created by substituting '"
                    + subformula.toString() + "' with '" + with.toString() + "'"
                    + " is not a CTL formula and thus cannot be used for '" + toString() + "'.");
        }
    }

    @Override
    public List<AtomicProposition> getTransitions() {
        List<AtomicProposition> ret = getPhi1().getTransitions();
        ret.addAll(getPhi2().getTransitions());
        return ret;
    }

    private String getPrefix() {
        return getOp().equals(CTLOperators.Binary.AU) || getOp().equals(CTLOperators.Binary.AUD) || getOp().equals(CTLOperators.Binary.AR) || getOp().equals(CTLOperators.Binary.AW) ? CTLOperators.all
                : (getOp().equals(CTLOperators.Binary.EU) || getOp().equals(CTLOperators.Binary.EUD) || getOp().equals(CTLOperators.Binary.ER) || getOp().equals(CTLOperators.Binary.EW) ? CTLOperators.exists : "");
    }

    @Override
    public String toSymbolString() {
        String pref = getPrefix();
        return pref + "(" + getPhi1().toSymbolString() + " " + getOp().toSymbol() + " " + getPhi2().toSymbolString() + ")";
    }

    @Override
    public String toString() {
        String pref = getPrefix();
        return pref + "(" + getPhi1().toString() + " " + getOp().toString() + " " + getPhi2().toString() + ")";
    }

    @Override
    public String toReplacableString() {
        String pref = getPrefix();
        return pref + "(" + getPhi1().toReplacableString() + " " + getOp().toString() + " " + getPhi2().toReplacableString() + ")";
    }

    @Override
    public String toLoLA() throws NotConvertableException {
        String pref = getOp().equals(CTLOperators.Binary.AU) || getOp().equals(CTLOperators.Binary.AUD) || getOp().equals(CTLOperators.Binary.AR) || getOp().equals(CTLOperators.Binary.AW) ? CTLOperators.allLoLA
                : (getOp().equals(CTLOperators.Binary.EU) || getOp().equals(CTLOperators.Binary.EUD) || getOp().equals(CTLOperators.Binary.ER) || getOp().equals(CTLOperators.Binary.EW) ? CTLOperators.existsLoLA : "");
        return pref + "(" + getPhi1().toLoLA() + " " + getOp().toLoLA() + " " + getPhi2().toLoLA() + ")";
    }

}
