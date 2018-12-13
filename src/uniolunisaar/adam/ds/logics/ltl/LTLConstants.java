package uniolunisaar.adam.ds.logics.ltl;

import uniolunisaar.adam.ds.logics.Constants;

/**
 *
 * @author Manuel Gieseking
 */
public abstract class LTLConstants {

    public static class True extends Constants.True implements ILTLFormula {
    }

    public static class False extends Constants.False implements ILTLFormula {
    }

    public static class Container extends Constants.Container implements ILTLFormula {

        public Container(String id) {
            super(id);
        }
    }

}
