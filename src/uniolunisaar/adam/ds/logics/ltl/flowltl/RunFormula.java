package uniolunisaar.adam.ds.logics.ltl.flowltl;

import uniolunisaar.adam.ds.logics.ltl.LTLOperators;
import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.logic.exceptions.NotSubstitutableException;
import uniolunisaar.adam.ds.logics.Formula;
import uniolunisaar.adam.ds.logics.ltl.ILTLFormula;
import uniolunisaar.adam.ds.logics.ltl.LTLFormula;

/**
 *
 * @author Manuel Gieseking
 */
public class RunFormula extends Formula<IFormula> implements IRunFormula {

    public RunFormula(IFormula phi) {
        super(phi);
    }

    // LTL Fragment
    public RunFormula(ILTLFormula phi) {
        super(phi);
    }

    public RunFormula(LTLOperators.Unary op, ILTLFormula phi) {
        super(new RunFormula(new LTLFormula(op, phi)));
    }

    public RunFormula(ILTLFormula phi1, LTLOperators.Binary op, ILTLFormula phi2) {
        super(new RunFormula(new LTLFormula(phi1, op, phi2)));
    }

    // Flow Fragment    
    public RunFormula(IFlowFormula phi) {
        super(phi);
    }

    public RunFormula(FlowFormula.FlowOperator op, ILTLFormula phi) {
        super(new RunFormula(new FlowFormula(phi)));
    }

    // Run Fragment
    public RunFormula(IRunFormula phi1, RunOperators.Binary op, IRunFormula phi2) {
        super(new RunFormulaBinary(phi1, op, phi2));
    }

    public RunFormula(IRunFormula phi1, RunOperators.Binary op, ILTLFormula phi2) {
        super(new RunFormulaBinary(phi1, op, new RunFormula(phi2)));
    }

    public RunFormula(ILTLFormula phi1, RunOperators.Binary op, IRunFormula phi2) {
        super(new RunFormulaBinary(new RunFormula(phi1), op, phi2));
    }

    public RunFormula(ILTLFormula phi1, RunOperators.Binary op, ILTLFormula phi2) {
        super(new RunFormulaBinary(new RunFormula(phi1), op, new RunFormula(phi2)));
    }

    public RunFormula(IFlowFormula phi1, RunOperators.Binary op, ILTLFormula phi2) {
        super(new RunFormulaBinary(new RunFormula(phi1), op, new RunFormula(phi2)));
    }

    public RunFormula(ILTLFormula phi1, RunOperators.Binary op, IFlowFormula phi2) {
        super(new RunFormulaBinary(new RunFormula(phi1), op, new RunFormula(phi2)));
    }

    public RunFormula(IFlowFormula phi1, RunOperators.Binary op, IFlowFormula phi2) {
        super(new RunFormulaBinary(new RunFormula(phi1), op, new RunFormula(phi2)));
    }

    public RunFormula(IRunFormula phi1, RunOperators.Binary op, IFlowFormula phi2) {
        super(new RunFormulaBinary(phi1, op, new RunFormula(phi2)));
    }

    public RunFormula(IFlowFormula phi1, RunOperators.Binary op, IRunFormula phi2) {
        super(new RunFormulaBinary(new RunFormula(phi1), op, phi2));
    }

    public RunFormula(ILTLFormula phi1, RunOperators.Implication op, IRunFormula phi2) {
        super(new RunFormulaImplication(phi1, op, phi2));
    }

    public RunFormula(ILTLFormula phi1, RunOperators.Implication op, ILTLFormula phi2) {
        super(new RunFormulaImplication(phi1, op, new RunFormula(phi2)));
    }

    public RunFormula(ILTLFormula phi1, RunOperators.Implication op, IFlowFormula phi2) {
        super(new RunFormulaImplication(phi1, op, new RunFormula(phi2)));
    }

    public ILTLFormula toLTLFormula() {
        if (getPhi() instanceof ILTLFormula) {
            return (ILTLFormula) getPhi();
        }
        throw new RuntimeException("'" + this.toString() + "' is not a LTL formula");
    }

    public IFlowFormula toFlowFormula() {
        if (getPhi() instanceof IFlowFormula) {
            return (IFlowFormula) getPhi();
        }
        throw new RuntimeException("'" + this.toString() + "' is not a flow formula");
    }

    @Override
    public IFormula createSubstitutedFormula(IFormula subformula, IFormula with) throws NotSubstitutableException {
        return new RunFormula(getPhi().substitute(subformula, with));
    }
}
