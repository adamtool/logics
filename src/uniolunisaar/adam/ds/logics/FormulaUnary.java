package uniolunisaar.adam.ds.logics;

import java.util.ArrayList;
import uniolunisaar.adam.logic.exceptions.NotSubstitutableException;

/**
 *
 * @author Manuel Gieseking
 * @param <F1>
 * @param <OP>
 */
//public abstract class FormulaUnary<F extends IFormula<F>, F1 extends IFormula<F1>, OP extends IOperatorUnary<F1>> implements IFormula<F> {
public abstract class FormulaUnary<F1 extends IFormula, OP extends IOperatorUnary<F1>> implements IFormula {

    private final F1 phi;
    private final OP op;

    public FormulaUnary(OP op, F1 phi) {
        this.phi = phi;
        this.op = op;
    }

    public F1 getPhi() {
        return phi;
    }

    public OP getOp() {
        return op;
    }

    @Override
    public IFormula substitute(IFormula subformula, IFormula with) throws NotSubstitutableException {
        if (subformula.toString().equals(this.toString())) {
            return with;
        } else {
            return createSubstitutedFormula(subformula, with);
        }
    }

    @Override
    public ArrayList<F1> getDirectSubFormulas() {
        ArrayList<F1> subformulas = new ArrayList<>();
        subformulas.add(getPhi());
        return subformulas;
    }

    @Override
    public Closure getClosure() {
        Closure c = phi.getClosure();
        c.add(this);
        return c;
    }

    @Override
    public int getDepth() {
        return phi.getDepth() + 1;
    }

    @Override
    public int getSize() {
        return phi.getSize() + 1;
    }

    @Override
    public int getNbFormulas() {
        return phi.getNbFormulas() + 1;
    }

    @Override
    public String toPrefixString() {
        return op.toString() + "(" + phi.toPrefixString() + ")";
    }

    @Override
    public String toSymbolString() {
        return op.toSymbol() + " " + phi.toSymbolString();
    }

    @Override
    public String toString() {
        return op.toString() + " " + phi.toString();
    }

    @Override
    public String toReplacableString() {
        return op.toString() + " " + phi.toReplacableString();
    }
}
