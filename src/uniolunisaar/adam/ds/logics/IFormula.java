package uniolunisaar.adam.ds.logics;

import java.util.List;
import uniolunisaar.adam.exception.logics.NotSubstitutableException;

/**
 *
 * @author Manuel Gieseking // * @param <F>
 */
//public interface IFormula<F extends IFormula> {
//    public F substitute(F subformula, F with) throws NotSubstitutableException;
//    public F createSubstitutedFormula(F subformula, F with) throws NotSubstitutableException;
public interface IFormula {

    public IFormula substitute(IFormula subformula, IFormula with) throws NotSubstitutableException;

    public IFormula createSubstitutedFormula(IFormula subformula, IFormula with) throws NotSubstitutableException;

    public List<? extends IFormula> getDirectSubFormulas();

    public Closure getClosure();

    public int getNbFormulas();

    public int getNbAtomicPropositions();

    public int getNbOperators();

    public int getDepth();

    public int getSize();

    public String toPrefixString();

    public String toSymbolString();

    public String toReplacableString();

}
