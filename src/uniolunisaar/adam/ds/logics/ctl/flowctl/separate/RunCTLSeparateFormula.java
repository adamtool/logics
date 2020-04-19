package uniolunisaar.adam.ds.logics.ctl.flowctl.separate;

import uniolunisaar.adam.ds.logics.Formula;
import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.ds.logics.ctl.CTLFormula;
import uniolunisaar.adam.ds.logics.ctl.CTLOperators;
import uniolunisaar.adam.ds.logics.ctl.ICTLFormula;
import uniolunisaar.adam.ds.logics.ctl.flowctl.FlowCTLFormula;
import uniolunisaar.adam.ds.logics.ctl.flowctl.RunCTLOperators;
import uniolunisaar.adam.exceptions.logics.NotSubstitutableException;
import uniolunisaar.adam.ds.logics.flowlogics.IRunFormula;
import uniolunisaar.adam.ds.logics.flowlogics.IFlowFormula;

/**
 *
 * @author Manuel Gieseking
 */
public class RunCTLSeparateFormula extends Formula<IFormula> implements IRunFormula {

    public RunCTLSeparateFormula(IFormula phi) {
        super(phi);
    }

    // CTL Fragment
    public RunCTLSeparateFormula(ICTLFormula phi) {
        super(phi);
    }

    public RunCTLSeparateFormula(CTLOperators.Unary op, ICTLFormula phi) {
        super(new RunCTLSeparateFormula(new CTLFormula(op, phi)));
    }

    public RunCTLSeparateFormula(ICTLFormula phi1, CTLOperators.Binary op, ICTLFormula phi2) {
        super(new RunCTLSeparateFormula(new CTLFormula(phi1, op, phi2)));
    }

    // Flow Fragment    
    public RunCTLSeparateFormula(IFlowFormula phi) {
        super(phi);
    }

    public RunCTLSeparateFormula(FlowCTLFormula.FlowCTLOperator op, ICTLFormula phi) {
        super(new RunCTLSeparateFormula(new FlowCTLFormula(op, phi)));
    }

    // Run Fragment
    public RunCTLSeparateFormula(RunCTLOperators.Unary op, IRunFormula phi) {
        super(new RunCTLSeparateFormulaUnary(op, phi));
    }

    public RunCTLSeparateFormula(RunCTLOperators.Unary op, ICTLFormula phi) {
        super(new RunCTLSeparateFormulaUnary(op, new RunCTLSeparateFormula(phi)));
    }

    public RunCTLSeparateFormula(IRunFormula phi1, RunCTLOperators.Binary op, IRunFormula phi2) {
        super(new RunCTLSeparateFormulaBinary(phi1, op, phi2));
    }

    public RunCTLSeparateFormula(IRunFormula phi1, RunCTLOperators.Binary op, ICTLFormula phi2) {
        super(new RunCTLSeparateFormulaBinary(phi1, op, new RunCTLSeparateFormula(phi2)));
    }

    public RunCTLSeparateFormula(ICTLFormula phi1, RunCTLOperators.Binary op, IRunFormula phi2) {
        super(new RunCTLSeparateFormulaBinary(new RunCTLSeparateFormula(phi1), op, phi2));
    }

    public RunCTLSeparateFormula(ICTLFormula phi1, RunCTLOperators.Binary op, ICTLFormula phi2) {
        super(new RunCTLSeparateFormulaBinary(new RunCTLSeparateFormula(phi1), op, new RunCTLSeparateFormula(phi2)));
    }

    public RunCTLSeparateFormula(IFlowFormula phi1, RunCTLOperators.Binary op, ICTLFormula phi2) {
        super(new RunCTLSeparateFormulaBinary(new RunCTLSeparateFormula(phi1), op, new RunCTLSeparateFormula(phi2)));
    }

    public RunCTLSeparateFormula(ICTLFormula phi1, RunCTLOperators.Binary op, IFlowFormula phi2) {
        super(new RunCTLSeparateFormulaBinary(new RunCTLSeparateFormula(phi1), op, new RunCTLSeparateFormula(phi2)));
    }

    public RunCTLSeparateFormula(IFlowFormula phi1, RunCTLOperators.Binary op, IFlowFormula phi2) {
        super(new RunCTLSeparateFormulaBinary(new RunCTLSeparateFormula(phi1), op, new RunCTLSeparateFormula(phi2)));
    }

    public RunCTLSeparateFormula(IRunFormula phi1, RunCTLOperators.Binary op, IFlowFormula phi2) {
        super(new RunCTLSeparateFormulaBinary(phi1, op, new RunCTLSeparateFormula(phi2)));
    }

    public RunCTLSeparateFormula(IFlowFormula phi1, RunCTLOperators.Binary op, IRunFormula phi2) {
        super(new RunCTLSeparateFormulaBinary(new RunCTLSeparateFormula(phi1), op, phi2));
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
        return new RunCTLSeparateFormula(getPhi().substitute(subformula, with));
    }
}
