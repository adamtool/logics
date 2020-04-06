package uniolunisaar.adam.ds.logics.flowlogics;

import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.ds.logics.FormulaUnary;
import java.util.ArrayList;
import uniolunisaar.adam.ds.logics.IOperatorUnary;

/**
 *
 * @author Manuel Gieseking
 * @param <F>
 */
//public class FlowFormula extends FormulaUnary<IFlowFormula, ILTLFormula, FlowFormula.FlowOperator> implements IFlowFormula {
public abstract class FlowFormula<F extends IFormula> extends FormulaUnary<F, IOperatorUnary<F>> implements IFlowFormula {

    protected FlowFormula(IOperatorUnary<F> op, F phi) {
        super(op, phi);
    }

    @Override
    public ArrayList<F> getDirectSubFormulas() {
        ArrayList<F> subformulas = new ArrayList<>();
        subformulas.add(getPhi());
        return subformulas;
    }

}
