package uniolunisaar.adam.ds.logics.ltl.flowltl;

import uniolunisaar.adam.ds.logics.Formula;
import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.ds.logics.ltl.ILTLFormula;
import uniolunisaar.adam.ds.logics.ltl.LTLFormula;
import uniolunisaar.adam.ds.logics.ltl.LTLOperators;
import uniolunisaar.adam.exceptions.logics.NotSubstitutableException;
import uniolunisaar.adam.ds.logics.flowlogics.IRunFormula;
import uniolunisaar.adam.ds.logics.flowlogics.RunOperators;
import uniolunisaar.adam.ds.logics.flowlogics.IFlowFormula;

/**
 *
 * @author Manuel Gieseking
 */
public class RunLTLFormula extends Formula<IFormula> implements IRunFormula {

    public RunLTLFormula(IFormula phi) {
        super(phi);
    }

    // LTL Fragment
    public RunLTLFormula(ILTLFormula phi) {
        super(phi);
    }

    public RunLTLFormula(LTLOperators.Unary op, ILTLFormula phi) {
        super(new RunLTLFormula(new LTLFormula(op, phi)));
    }

    public RunLTLFormula(ILTLFormula phi1, LTLOperators.Binary op, ILTLFormula phi2) {
        super(new RunLTLFormula(new LTLFormula(phi1, op, phi2)));
    }

    // Flow Fragment    
    public RunLTLFormula(IFlowFormula phi) {
        super(phi);
    }

    public RunLTLFormula(FlowLTLFormula.FlowLTLOperator op, ILTLFormula phi) {
        super(new RunLTLFormula(new FlowLTLFormula(phi)));
    }

    // Run Fragment
    public RunLTLFormula(IRunFormula phi1, RunOperators.Binary op, IRunFormula phi2) {
        super(new RunLTLFormulaBinary(phi1, op, phi2));
    }

    public RunLTLFormula(IRunFormula phi1, RunOperators.Binary op, ILTLFormula phi2) {
        super(new RunLTLFormulaBinary(phi1, op, new RunLTLFormula(phi2)));
    }

    public RunLTLFormula(ILTLFormula phi1, RunOperators.Binary op, IRunFormula phi2) {
        super(new RunLTLFormulaBinary(new RunLTLFormula(phi1), op, phi2));
    }

    public RunLTLFormula(ILTLFormula phi1, RunOperators.Binary op, ILTLFormula phi2) {
        super(new RunLTLFormulaBinary(new RunLTLFormula(phi1), op, new RunLTLFormula(phi2)));
    }

    public RunLTLFormula(IFlowFormula phi1, RunOperators.Binary op, ILTLFormula phi2) {
        super(new RunLTLFormulaBinary(new RunLTLFormula(phi1), op, new RunLTLFormula(phi2)));
    }

    public RunLTLFormula(ILTLFormula phi1, RunOperators.Binary op, IFlowFormula phi2) {
        super(new RunLTLFormulaBinary(new RunLTLFormula(phi1), op, new RunLTLFormula(phi2)));
    }

    public RunLTLFormula(IFlowFormula phi1, RunOperators.Binary op, IFlowFormula phi2) {
        super(new RunLTLFormulaBinary(new RunLTLFormula(phi1), op, new RunLTLFormula(phi2)));
    }

    public RunLTLFormula(IRunFormula phi1, RunOperators.Binary op, IFlowFormula phi2) {
        super(new RunLTLFormulaBinary(phi1, op, new RunLTLFormula(phi2)));
    }

    public RunLTLFormula(IFlowFormula phi1, RunOperators.Binary op, IRunFormula phi2) {
        super(new RunLTLFormulaBinary(new RunLTLFormula(phi1), op, phi2));
    }

    public RunLTLFormula(ILTLFormula phi1, RunOperators.Implication op, IRunFormula phi2) {
        super(new RunLTLFormulaImplication(phi1, op, phi2));
    }

    public RunLTLFormula(ILTLFormula phi1, RunOperators.Implication op, ILTLFormula phi2) {
        super(new RunLTLFormulaImplication(phi1, op, new RunLTLFormula(phi2)));
    }

    public RunLTLFormula(ILTLFormula phi1, RunOperators.Implication op, IFlowFormula phi2) {
        super(new RunLTLFormulaImplication(phi1, op, new RunLTLFormula(phi2)));
    }

    public ILTLFormula toLTLFormula() {
        if (getPhi() instanceof ILTLFormula) {
            return (ILTLFormula) getPhi();
        }
        throw new RuntimeException("'" + this.toString() + "' is not an LTL formula");
    }

    public IFlowFormula toFlowFormula() {
        if (getPhi() instanceof IFlowFormula) {
            return (IFlowFormula) getPhi();
        }
        throw new RuntimeException("'" + this.toString() + "' is not a flow formula");
    }

    @Override
    public IFormula createSubstitutedFormula(IFormula subformula, IFormula with) throws NotSubstitutableException {
        return new RunLTLFormula(getPhi().substitute(subformula, with));
    }
}
