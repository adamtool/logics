package uniolunisaar.adam.ds.logics;

import java.util.ArrayList;
import uniolunisaar.adam.exception.logics.NotSubstitutableException;

/**
 *
 * @author Manuel Gieseking
 * @param <F1>
 */
//public abstract class Formula<F1 extends IFormula> implements IFormula<F1> {
public abstract class Formula<F1 extends IFormula> implements IFormula {

    private final F1 phi;

    public Formula(F1 phi) {
        this.phi = phi;
    }

    public F1 getPhi() {
        return phi;
    }

    @Override
    public IFormula substitute(IFormula subformula, IFormula with) throws NotSubstitutableException {
        if (subformula.toString().equals(this.toString())) {
            return with;
        } else {
            return createSubstitutedFormula(subformula, with);
        }
    }

    @Override
    public ArrayList<F1> getDirectSubFormulas() {
        ArrayList<F1> subformulas = new ArrayList<>();
        subformulas.add(getPhi());
        return subformulas;
    }

    @Override
    public Closure getClosure() {
        return phi.getClosure();
    }

    @Override
    public int getDepth() {
        return phi.getDepth();
    }

    @Override
    public int getSize() {
        return phi.getSize();
    }

    @Override
    public int getNbFormulas() {
        return phi.getNbFormulas();
    }

    @Override
    public String toPrefixString() {
        return phi.toPrefixString();
    }

    @Override
    public String toSymbolString() {
        return phi.toSymbolString();
    }

    @Override
    public String toString() {
        return phi.toString();
    }

    @Override
    public String toReplacableString() {
        return phi.toReplacableString();
    }

}
