package uniolunisaar.adam.ds.logics;

import uniolunisaar.adam.exception.logics.NotSubstitutableException;

/**
 * @author Manuel Gieseking
 * @param <F1>
 * @param <OP>
 * @param <F2>
 */
//public abstract class FormulaBinary<F extends IFormula<F>, F1 extends IFormula<F1>, OP extends IOperatorBinary<F1, F2>, F2 extends IFormula<F2>> implements IFormula<F> {
public abstract class FormulaBinary<F1 extends IFormula, OP extends IOperatorBinary<F1, F2>, F2 extends IFormula> implements IFormula {

    private final F1 phi1;
    private final F2 phi2;
    private final OP op;

    public FormulaBinary(F1 phi1, OP op, F2 phi2) {
        this.phi1 = phi1;
        this.phi2 = phi2;
        this.op = op;
    }

    public F1 getPhi1() {
        return phi1;
    }

    public F2 getPhi2() {
        return phi2;
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
    public Closure getClosure() {
        Closure c = phi1.getClosure();
        c.addAll(phi2.getClosure());
        c.add(this);
        return c;
    }

    @Override
    public int getDepth() {
        return Math.max(phi1.getDepth(), phi2.getDepth()) + 1;
    }

    @Override
    public int getSize() {
        return phi1.getSize() + phi2.getSize();
    }

    @Override
    public int getNbFormulas() {
        return phi1.getNbFormulas() + phi2.getNbFormulas() + 1;
    }

    @Override
    public String toPrefixString() {
        return op.toString() + "(" + phi1.toPrefixString() + "," + phi2.toPrefixString() + ")";
    }

    @Override
    public String toSymbolString() {
        return "(" + phi1.toSymbolString() + " " + op.toSymbol() + " " + phi2.toSymbolString() + ")";
    }

    @Override
    public String toString() {
        return "(" + phi1.toString() + " " + op.toString() + " " + phi2.toString() + ")";
    }

    @Override
    public String toReplacableString() {
        return "(" + phi1.toReplacableString() + " " + op.toString() + " " + phi2.toReplacableString() + ")";
    }
}
