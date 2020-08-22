package uniolunisaar.adam.ds.logics.flowlogics;

import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.ds.logics.FormulaUnary;
import java.util.ArrayList;
import uniolunisaar.adam.ds.logics.IOperatorUnary;

/**
 *
 * @author Manuel Gieseking
 * @param <F>
 * @param <OP>
 */
//public class FlowFormula extends FormulaUnary<IFlowFormula, ILTLFormula, FlowFormula.FlowOperator> implements IFlowFormula {
public abstract class FlowFormula<F extends IFormula, OP extends IOperatorUnary<F>> extends FormulaUnary<F, OP> implements IFlowFormula {

    protected FlowFormula(OP op, F phi) {
        super(op, phi);
    }

    @Override
    public ArrayList<F> getDirectSubFormulas() {
        ArrayList<F> subformulas = new ArrayList<>();
        subformulas.add(getPhi());
        return subformulas;
    }

}
