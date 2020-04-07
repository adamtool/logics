package uniolunisaar.adam.ds.logics.ctl.flowctl;

import uniolunisaar.adam.ds.logics.Formula;
import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.ds.logics.ctl.CTLFormula;
import uniolunisaar.adam.ds.logics.ctl.CTLOperators;
import uniolunisaar.adam.ds.logics.ctl.ICTLFormula;
import uniolunisaar.adam.ds.logics.ltl.ILTLFormula;
import uniolunisaar.adam.exceptions.logics.NotSubstitutableException;
import uniolunisaar.adam.ds.logics.flowlogics.IRunFormula;
import uniolunisaar.adam.ds.logics.flowlogics.IFlowFormula;

/**
 *
 * @author Manuel Gieseking
 */
public class RunCTLFormula extends Formula<IFormula> implements IRunFormula {

    public RunCTLFormula(IFormula phi) {
        super(phi);
    }

    // CTL Fragment
    public RunCTLFormula(ICTLFormula phi) {
        super(phi);
    }

    public RunCTLFormula(CTLOperators.Unary op, ICTLFormula phi) {
        super(new RunCTLFormula(new CTLFormula(op, phi)));
    }

    public RunCTLFormula(ICTLFormula phi1, CTLOperators.Binary op, ICTLFormula phi2) {
        super(new RunCTLFormula(new CTLFormula(phi1, op, phi2)));
    }

    // Flow Fragment    
    public RunCTLFormula(IFlowFormula phi) {
        super(phi);
    }

    public RunCTLFormula(FlowCTLFormula.FlowCTLOperator op, ICTLFormula phi) {
        super(new RunCTLFormula(new FlowCTLFormula(op, phi)));
    }

    // Run Fragment
    public RunCTLFormula(RunCTLOperators.Unary op, IRunFormula phi) {
        super(new RunCTLFormulaUnary(op, phi));
    }

    public RunCTLFormula(RunCTLOperators.Unary op, ICTLFormula phi) {
        super(new RunCTLFormulaUnary(op, new RunCTLFormula(phi)));
    }

    public RunCTLFormula(IRunFormula phi1, RunCTLOperators.Binary op, IRunFormula phi2) {
        super(new RunCTLFormulaBinary(phi1, op, phi2));
    }

    public RunCTLFormula(IRunFormula phi1, RunCTLOperators.Binary op, ICTLFormula phi2) {
        super(new RunCTLFormulaBinary(phi1, op, new RunCTLFormula(phi2)));
    }

    public RunCTLFormula(ICTLFormula phi1, RunCTLOperators.Binary op, IRunFormula phi2) {
        super(new RunCTLFormulaBinary(new RunCTLFormula(phi1), op, phi2));
    }

    public RunCTLFormula(ICTLFormula phi1, RunCTLOperators.Binary op, ICTLFormula phi2) {
        super(new RunCTLFormulaBinary(new RunCTLFormula(phi1), op, new RunCTLFormula(phi2)));
    }

    public RunCTLFormula(IFlowFormula phi1, RunCTLOperators.Binary op, ICTLFormula phi2) {
        super(new RunCTLFormulaBinary(new RunCTLFormula(phi1), op, new RunCTLFormula(phi2)));
    }

    public RunCTLFormula(ILTLFormula phi1, RunCTLOperators.Binary op, IFlowFormula phi2) {
        super(new RunCTLFormulaBinary(new RunCTLFormula(phi1), op, new RunCTLFormula(phi2)));
    }

    public RunCTLFormula(IFlowFormula phi1, RunCTLOperators.Binary op, IFlowFormula phi2) {
        super(new RunCTLFormulaBinary(new RunCTLFormula(phi1), op, new RunCTLFormula(phi2)));
    }

    public RunCTLFormula(IRunFormula phi1, RunCTLOperators.Binary op, IFlowFormula phi2) {
        super(new RunCTLFormulaBinary(phi1, op, new RunCTLFormula(phi2)));
    }

    public RunCTLFormula(IFlowFormula phi1, RunCTLOperators.Binary op, IRunFormula phi2) {
        super(new RunCTLFormulaBinary(new RunCTLFormula(phi1), op, phi2));
    }

    public ICTLFormula toCTLFormula() {
        if (getPhi() instanceof ICTLFormula) {
            return (ICTLFormula) getPhi();
        }
        throw new RuntimeException("'" + this.toString() + "' is not a CTL formula");
    }

    public IFlowFormula toFlowFormula() {
        if (getPhi() instanceof IFlowFormula) {
            return (IFlowFormula) getPhi();
        }
        throw new RuntimeException("'" + this.toString() + "' is not a flow formula");
    }

    @Override
    public IFormula createSubstitutedFormula(IFormula subformula, IFormula with) throws NotSubstitutableException {
        return new RunCTLFormula(getPhi().substitute(subformula, with));
    }
}
