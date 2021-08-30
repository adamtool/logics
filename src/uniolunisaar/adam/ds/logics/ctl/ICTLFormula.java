package uniolunisaar.adam.ds.logics.ctl;

import java.util.List;
import uniolunisaar.adam.ds.logics.AtomicProposition;
import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.ds.logics.ltl.LoLAConvertable;

/**
 *
 * @author Manuel Gieseking
 */
//public interface ICTLFormula extends IFormula<ILTLFormula> {
public interface ICTLFormula extends IFormula, LoLAConvertable {

    public List<AtomicProposition> getTransitions();

}
