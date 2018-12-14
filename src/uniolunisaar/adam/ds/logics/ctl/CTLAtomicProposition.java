package uniolunisaar.adam.ds.logics.ctl;

import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.logics.AtomicProposition;

/**
 *
 * @author Manuel Gieseking
 */
public class CTLAtomicProposition extends AtomicProposition implements ICTLFormula {

    public CTLAtomicProposition(Transition t) {
        super(t);
    }

    public CTLAtomicProposition(Place p) {
        super(p);
    }

}
