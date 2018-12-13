package uniolunisaar.adam.ds.logics.ltl;

import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.logics.AtomicProposition;

/**
 *
 * @author Manuel Gieseking
 */
public class LTLAtomicProposition extends AtomicProposition implements ILTLFormula {

    public LTLAtomicProposition(Transition t) {
        super(t);
    }

    public LTLAtomicProposition(Place p) {
        super(p);
    }
}
