package uniolunisaar.adam.ds.logics.ctl;

import java.util.ArrayList;
import java.util.List;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.logics.AtomicProposition;
import uniolunisaar.adam.exceptions.logics.NotConvertableException;

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

    @Override
    public String toLoLA() throws NotConvertableException {
        if (isTransition()) {
            throw new NotConvertableException("LoLA does not support transitions as atomic propositions (only for firability).");
        }
        return '(' + getId() + " = 1)";
    }

    @Override
    public List<AtomicProposition> getTransitions() {
        List<AtomicProposition> ret = new ArrayList<>();
        if (isTransition()) {
            ret.add(this);
        }
        return ret;
    }

}
