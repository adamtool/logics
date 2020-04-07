package uniolunisaar.adam.ds.logics.ctl;

import uniolunisaar.adam.ds.logics.Formula;
import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.exceptions.logics.NotConvertableException;
import uniolunisaar.adam.exceptions.logics.NotSubstitutableException;

/**
 *
 * @author Manuel Gieseking
 */
//public class CTLFormula extends Formula<ILTLFormula> implements ICTLFormula {
public class CTLFormula extends Formula<ICTLFormula> implements ICTLFormula {

    public CTLFormula(ICTLFormula phi) {
        super(phi);
    }

    public CTLFormula(CTLOperators.Unary op, ICTLFormula phi) {
        super(new CTLFormulaUnary(op, phi));
    }

    public CTLFormula(ICTLFormula phi1, CTLOperators.Binary op, ICTLFormula phi2) {
        super(new CTLFormulaBinary(phi1, op, phi2));
    }

    @Override
    public CTLFormula createSubstitutedFormula(IFormula subformula, IFormula with) throws NotSubstitutableException {
        IFormula phisubs = getPhi().substitute(subformula, with);
        if (phisubs instanceof ICTLFormula) {
            return new CTLFormula((ICTLFormula) phisubs);
        } else {
            throw new NotSubstitutableException("Error creating LTL formula. Substituting '" + subformula.toString() + "' with '" + with.toString() + "' "
                    + "does not result in an LTL formula.");
        }
    }

//    @Override
//    public CTLFormula createSubstitutedFormula(IFormula subformula, IFormula with) throws NotSubstitutableException {
//        return new CTLFormula(getPhi().substitute(subformula, with));
//    }
//    @Override
//    public ICTLFormula createSubstitutedFormula(ICTLFormula subformula, ICTLFormula with) throws NotSubstitutableException {
//        ICTLFormula phisubs = getPhi().substitute(subformula, with);
//        if (phisubs instanceof CTLFormulaUnary) {
//            CTLFormulaUnary f = (CTLFormulaUnary) phisubs;
//            return new CTLFormula(f.getOp(), f.getPhi());
//        } else if (phisubs instanceof CTLFormulaBinary) {
//            CTLFormulaBinary f = (CTLFormulaBinary) phisubs;
//            return new CTLFormula(f.getPhi1(), f.getOp(), f.getPhi2());
//        } else {
//            throw new NotSubstitutableException("Error creating LTL formula.");
//        }
//    }
    @Override
    public String toLoLA() throws NotConvertableException {
        return getPhi().toLoLA();
    }
}
