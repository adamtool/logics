package uniolunisaar.adam.ds.logics;

import java.util.ArrayList;
import java.util.List;
import uniolunisaar.adam.exceptions.logics.NotSubstitutableException;

/**
 *
 * @author Manuel Gieseking
 */
public abstract class Constants implements IAtomicProposition {

    public static class True extends Constants {

        @Override
        public String toPrefixString() {
            return "TRUE";
        }

        @Override
        public String toSymbolString() {
            return "⊤";// "\u22A4";
        }

        @Override
        public String toString() {
            return "TRUE";
        }

        @Override
        public String toReplacableString() {
            return "TRUE";
        }

        public String toLoLA() {
            return "TRUE";
        }
    }

    public static class False extends Constants {

        @Override
        public String toPrefixString() {
            return "FALSE";
        }

        @Override
        public String toSymbolString() {
            return "⊥";// "\u22A5";
        }

        @Override
        public String toString() {
            return "FALSE";
        }

        @Override
        public String toReplacableString() {
            return "FALSE";
        }

        public String toLoLA() {
            return "FALSE";
        }
    }

    public static class Container extends Constants {

        private final String id;

        public Container(String id) {
            this.id = id;
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

        public String toLoLA() {
            return id;
        }
    }

    @Override
    public IFormula substitute(IFormula subformula, IFormula with) throws NotSubstitutableException {
        return createSubstitutedFormula(subformula, with);
    }

    @Override
    public IFormula createSubstitutedFormula(IFormula subformula, IFormula with) throws NotSubstitutableException {
        return this;
    }

    @Override
    public List<Constants> getDirectSubFormulas() {
        // todo: here would also possibly different things when it's an abbreviation
        return new ArrayList<>();
    }

    @Override
    public Closure getClosure() {
        // todo: normally I would expect it as an abbreviation false=p\wedge \neg p
        //       but then I would have to create it with a place or a transition
        Closure c = new Closure();
        c.add(this);
        return c;
    }

    @Override
    public int getNbFormulas() {
        return 1;
    }

    @Override
    public int getDepth() {
        return 0; // todo: normally I expect 2 since it is an abbreviation for false = p\wedge \neg p
    }

    @Override
    public int getSize() {
        return getNbFormulas();
    }

    @Override
    public int getNbOperators() {
        return 0;// todo: normally I expect 1 since it is an abbreviation for false = p\wedge \neg p
    }

    @Override
    public int getNbAtomicPropositions() {
        return 0;// todo: normally I expect 2 since it is an abbreviation for false = p\wedge \neg p
    }
}
