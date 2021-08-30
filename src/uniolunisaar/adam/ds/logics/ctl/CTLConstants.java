package uniolunisaar.adam.ds.logics.ctl;

import java.util.ArrayList;
import java.util.List;
import uniolunisaar.adam.ds.logics.AtomicProposition;
import uniolunisaar.adam.ds.logics.Constants;

/**
 *
 * @author Manuel Gieseking
 */
public abstract class CTLConstants {

    public static class True extends Constants.True implements ICTLFormula {

        @Override
        public List<AtomicProposition> getTransitions() {
            return new ArrayList<>();
        }

    }

    public static class False extends Constants.False implements ICTLFormula {

        @Override
        public List<AtomicProposition> getTransitions() {
            return new ArrayList<>();
        }
    }

    public static class Container extends Constants.Container implements ICTLFormula {

        public Container(String id) {
            super(id);
        }

        @Override
        public List<AtomicProposition> getTransitions() {
            return new ArrayList<>();
        }
    }
}
