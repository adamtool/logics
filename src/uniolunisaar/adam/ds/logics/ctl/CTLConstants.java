package uniolunisaar.adam.ds.logics.ctl;

import uniolunisaar.adam.ds.logics.Constants;

/**
 *
 * @author Manuel Gieseking
 */
public abstract class CTLConstants {

    public static class True extends Constants.True implements ICTLFormula {
    }

    public static class False extends Constants.False implements ICTLFormula {
    }

    public static class Container extends Constants.Container implements ICTLFormula {

        public Container(String id) {
            super(id);
        }
    }
}
