package uniolunisaar.adam.ds.logics.ctl.flowctl.nested;

import uniolunisaar.adam.ds.logics.Formula;
import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.ds.logics.ctl.ICTLFormula;
import uniolunisaar.adam.ds.logics.ctl.flowctl.FlowCTLFormula;
import uniolunisaar.adam.ds.logics.ctl.flowctl.RunCTLOperators;
import uniolunisaar.adam.ds.logics.ltl.ILTLFormula;
import uniolunisaar.adam.exceptions.logics.NotSubstitutableException;
import uniolunisaar.adam.ds.logics.flowlogics.IRunFormula;
import uniolunisaar.adam.ds.logics.flowlogics.IFlowFormula;
import uniolunisaar.adam.ds.logics.ltl.LTLFormula;
import uniolunisaar.adam.ds.logics.ltl.LTLOperators;

/**
 *
 * @author Manuel Gieseking
 */
public class RunCTLNestedFormula extends Formula<IFormula> implements IRunFormula {

    public RunCTLNestedFormula(IFormula phi) {
        super(phi);
    }

    // LTL Fragment
    public RunCTLNestedFormula(ILTLFormula phi) {
        super(phi);
    }

    public RunCTLNestedFormula(LTLOperators.Unary op, ILTLFormula phi) {
        super(new RunCTLNestedFormula(new LTLFormula(op, phi)));
    }

    public RunCTLNestedFormula(ILTLFormula phi1, LTLOperators.Binary op, ILTLFormula phi2) {
        super(new RunCTLNestedFormula(new LTLFormula(phi1, op, phi2)));
    }

    // Flow Fragment    
    public RunCTLNestedFormula(IFlowFormula phi) {
        super(phi);
    }

    public RunCTLNestedFormula(FlowCTLFormula.FlowCTLOperator op, ICTLFormula phi) {
        super(new RunCTLNestedFormula(new FlowCTLFormula(op, phi)));
    }

    // Run Fragment
    public RunCTLNestedFormula(RunCTLOperators.Unary op, IRunFormula phi) {
        super(new RunCTLNestedFormulaUnary(op, phi));
    }

    public RunCTLNestedFormula(RunCTLOperators.Unary op, ILTLFormula phi) {
        super(new RunCTLNestedFormulaUnary(op, new RunCTLNestedFormula(phi)));
    }

    public RunCTLNestedFormula(IRunFormula phi1, RunCTLOperators.Binary op, IRunFormula phi2) {
        super(new RunCTLNestedFormulaBinary(phi1, op, phi2));
    }

    public RunCTLNestedFormula(IRunFormula phi1, RunCTLOperators.Binary op, ILTLFormula phi2) {
        super(new RunCTLNestedFormulaBinary(phi1, op, new RunCTLNestedFormula(phi2)));
    }

    public RunCTLNestedFormula(ILTLFormula phi1, RunCTLOperators.Binary op, IRunFormula phi2) {
        super(new RunCTLNestedFormulaBinary(new RunCTLNestedFormula(phi1), op, phi2));
    }

    public RunCTLNestedFormula(ILTLFormula phi1, RunCTLOperators.Binary op, ILTLFormula phi2) {
        super(new RunCTLNestedFormulaBinary(new RunCTLNestedFormula(phi1), op, new RunCTLNestedFormula(phi2)));
    }

    public RunCTLNestedFormula(IFlowFormula phi1, RunCTLOperators.Binary op, ILTLFormula phi2) {
        super(new RunCTLNestedFormulaBinary(new RunCTLNestedFormula(phi1), op, new RunCTLNestedFormula(phi2)));
    }

    public RunCTLNestedFormula(ILTLFormula phi1, RunCTLOperators.Binary op, IFlowFormula phi2) {
        super(new RunCTLNestedFormulaBinary(new RunCTLNestedFormula(phi1), op, new RunCTLNestedFormula(phi2)));
    }

    public RunCTLNestedFormula(IFlowFormula phi1, RunCTLOperators.Binary op, IFlowFormula phi2) {
        super(new RunCTLNestedFormulaBinary(new RunCTLNestedFormula(phi1), op, new RunCTLNestedFormula(phi2)));
    }

    public RunCTLNestedFormula(IRunFormula phi1, RunCTLOperators.Binary op, IFlowFormula phi2) {
        super(new RunCTLNestedFormulaBinary(phi1, op, new RunCTLNestedFormula(phi2)));
    }

    public RunCTLNestedFormula(IFlowFormula phi1, RunCTLOperators.Binary op, IRunFormula phi2) {
        super(new RunCTLNestedFormulaBinary(new RunCTLNestedFormula(phi1), op, phi2));
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
        return new RunCTLNestedFormula(getPhi().substitute(subformula, with));
    }
}
