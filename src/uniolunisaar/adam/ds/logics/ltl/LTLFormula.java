package uniolunisaar.adam.ds.logics.ltl;

import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.exception.logics.NotSubstitutableException;
import uniolunisaar.adam.ds.logics.Formula;
import uniolunisaar.adam.ds.logics.ltl.ILTLFormula;

/**
 *
 * @author Manuel Gieseking
 */
//public class LTLFormula extends Formula<ILTLFormula> implements ILTLFormula {
public class LTLFormula extends Formula<ILTLFormula> implements ILTLFormula {

    public LTLFormula(ILTLFormula phi) {
        super(phi);
    }

    public LTLFormula(LTLOperators.Unary op, ILTLFormula phi) {
        super(new LTLFormulaUnary(op, phi));
    }

    public LTLFormula(ILTLFormula phi1, LTLOperators.Binary op, ILTLFormula phi2) {
        super(new LTLFormulaBinary(phi1, op, phi2));
    }

    @Override
    public LTLFormula createSubstitutedFormula(IFormula subformula, IFormula with) throws NotSubstitutableException {
        IFormula phisubs = getPhi().substitute(subformula, with);
        if (phisubs instanceof ILTLFormula) {           
            return new LTLFormula((ILTLFormula) phisubs);
        } else {
            throw new NotSubstitutableException("Error creating LTL formula. Substituting '" + subformula.toString() + "' with '" + with.toString() + "' "
                    + "does not result in an LTL formula.");
        }
    }

//    @Override
//    public LTLFormula createSubstitutedFormula(IFormula subformula, IFormula with) throws NotSubstitutableException {
//        return new LTLFormula(getPhi().substitute(subformula, with));
//    }
//    @Override
//    public ILTLFormula createSubstitutedFormula(ILTLFormula subformula, ILTLFormula with) throws NotSubstitutableException {
//        ILTLFormula phisubs = getPhi().substitute(subformula, with);
//        if (phisubs instanceof LTLFormulaUnary) {
//            LTLFormulaUnary f = (LTLFormulaUnary) phisubs;
//            return new LTLFormula(f.getOp(), f.getPhi());
//        } else if (phisubs instanceof LTLFormulaBinary) {
//            LTLFormulaBinary f = (LTLFormulaBinary) phisubs;
//            return new LTLFormula(f.getPhi1(), f.getOp(), f.getPhi2());
//        } else {
//            throw new NotSubstitutableException("Error creating LTL formula.");
//        }
//    }
}
