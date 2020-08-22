package uniolunisaar.adam.ds.logics.ctl.flowctl.forall;

import uniolunisaar.adam.ds.logics.Formula;
import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.ds.logics.ctl.ICTLFormula;
import uniolunisaar.adam.ds.logics.ctl.flowctl.FlowCTLFormula;
import uniolunisaar.adam.ds.logics.ltl.ILTLFormula;
import uniolunisaar.adam.exceptions.logics.NotSubstitutableException;
import uniolunisaar.adam.ds.logics.flowlogics.IRunFormula;
import uniolunisaar.adam.ds.logics.flowlogics.IFlowFormula;
import uniolunisaar.adam.ds.logics.flowlogics.RunOperators;
import uniolunisaar.adam.ds.logics.ltl.LTLFormula;
import uniolunisaar.adam.ds.logics.ltl.LTLOperators;

/**
 *
 * @author Manuel Gieseking
 */
public class RunCTLForAllFormula extends Formula<IFormula> implements IRunFormula {

    public RunCTLForAllFormula(IFormula phi) {
        super(phi);
    }

    // LTL Fragment
    public RunCTLForAllFormula(ILTLFormula phi) {
        super(phi);
    }

    public RunCTLForAllFormula(LTLOperators.Unary op, ILTLFormula phi) {
        super(new RunCTLForAllFormula(new LTLFormula(op, phi)));
    }

    public RunCTLForAllFormula(ILTLFormula phi1, LTLOperators.Binary op, ILTLFormula phi2) {
        super(new RunCTLForAllFormula(new LTLFormula(phi1, op, phi2)));
    }

    // Flow Fragment    
    public RunCTLForAllFormula(IFlowFormula phi) {
        super(phi);
    }

    public RunCTLForAllFormula(ICTLFormula phi) {
        super(new RunCTLForAllFormula(new FlowCTLFormula(FlowCTLFormula.FlowCTLOperator.All, phi)));
    }

    // Run Fragment
    public RunCTLForAllFormula(ILTLFormula phi1, RunOperators.Implication op, IRunFormula phi2) {
        super(new RunCTLForAllFormulaImplication(phi1, op, phi2));
    }

    public RunCTLForAllFormula(IRunFormula phi1, RunOperators.Binary op, IRunFormula phi2) {
        super(new RunCTLForAllFormulaBinary(phi1, op, phi2));
    }

    public RunCTLForAllFormula(IRunFormula phi1, RunOperators.Binary op, ILTLFormula phi2) {
        super(new RunCTLForAllFormulaBinary(phi1, op, new RunCTLForAllFormula(phi2)));
    }

    public RunCTLForAllFormula(ILTLFormula phi1, RunOperators.Binary op, IRunFormula phi2) {
        super(new RunCTLForAllFormulaBinary(new RunCTLForAllFormula(phi1), op, phi2));
    }

    public RunCTLForAllFormula(ILTLFormula phi1, RunOperators.Binary op, ILTLFormula phi2) {
        super(new RunCTLForAllFormulaBinary(new RunCTLForAllFormula(phi1), op, new RunCTLForAllFormula(phi2)));
    }

    public RunCTLForAllFormula(IFlowFormula phi1, RunOperators.Binary op, ILTLFormula phi2) {
        super(new RunCTLForAllFormulaBinary(new RunCTLForAllFormula(phi1), op, new RunCTLForAllFormula(phi2)));
    }

    public RunCTLForAllFormula(ILTLFormula phi1, RunOperators.Binary op, IFlowFormula phi2) {
        super(new RunCTLForAllFormulaBinary(new RunCTLForAllFormula(phi1), op, new RunCTLForAllFormula(phi2)));
    }

    public RunCTLForAllFormula(IFlowFormula phi1, RunOperators.Binary op, IFlowFormula phi2) {
        super(new RunCTLForAllFormulaBinary(new RunCTLForAllFormula(phi1), op, new RunCTLForAllFormula(phi2)));
    }

    public RunCTLForAllFormula(IRunFormula phi1, RunOperators.Binary op, IFlowFormula phi2) {
        super(new RunCTLForAllFormulaBinary(phi1, op, new RunCTLForAllFormula(phi2)));
    }

    public RunCTLForAllFormula(IFlowFormula phi1, RunOperators.Binary op, IRunFormula phi2) {
        super(new RunCTLForAllFormulaBinary(new RunCTLForAllFormula(phi1), op, phi2));
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
        return new RunCTLForAllFormula(getPhi().substitute(subformula, with));
    }
}
