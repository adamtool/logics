package uniolunisaar.adam.ds.logics;

import java.util.ArrayList;
import java.util.List;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;

/**
 *
 * @author Manuel Gieseking
 */
public class AtomicProposition implements IAtomicProposition {

    private final String id;
    private final boolean isTransition;
    private final boolean firableProposition;

    public AtomicProposition(Transition t) {
        id = t.getId();
        isTransition = true;
        firableProposition = false;
    }

    public AtomicProposition(Transition t, boolean isFirableProposition) {
        if (isFirableProposition) {
            id = "{" + t.getId() + ">";
        } else {
            id = t.getId();
        }
        this.firableProposition = isFirableProposition;
        isTransition = true;
    }

    public AtomicProposition(Place p) {
        id = p.getId();
        isTransition = false;
        firableProposition = false;
    }

    public String get() {
        return id;
    }

    public boolean isTransition() {
        return isTransition;
    }

    public boolean isPlace() {
        return !isTransition;
    }

    @Override
//    public ILTLFormula substitute(ILTLFormula subformula, ILTLFormula with) {
    public IFormula substitute(IFormula subformula, IFormula with) {
        if (subformula instanceof AtomicProposition) {
            AtomicProposition sub = (AtomicProposition) subformula;
            if (sub.isTransition == isTransition && sub.id.equals(id)) {
                return with;
            }
        }
        return createSubstitutedFormula(subformula, with);
    }

    @Override
    public AtomicProposition createSubstitutedFormula(IFormula subformula, IFormula with) {
        return this;
    }

    @Override
    public List<AtomicProposition> getDirectSubFormulas() {
        return new ArrayList<>();
    }

    @Override
    public Closure getClosure() {
        Closure c = new Closure();
        c.add(this);
        return c;
    }

    @Override
    public int getDepth() {
        return 0;
    }

    @Override
    public int getSize() {
        return getNbFormulas();
    }

    @Override
    public int getNbFormulas() {
        return 1;
    }

    @Override
    public int getNbOperators() {
        return 0;
    }

    @Override
    public int getNbAtomicPropositions() {
        return 1;
    }

    @Override
    public String toPrefixString() {
        return id;
    }

    @Override
    public String toSymbolString() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public String toReplacableString() {
        return "'" + id + "'";
    }

    public boolean isFirableProposition() {
        return firableProposition;
    }

}
