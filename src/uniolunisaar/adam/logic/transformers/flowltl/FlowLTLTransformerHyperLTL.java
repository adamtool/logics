package uniolunisaar.adam.logic.transformers.flowltl;

import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniol.apt.io.parser.ParseException;
import uniolunisaar.adam.ds.circuits.AigerFile;
import uniolunisaar.adam.ds.logics.AtomicProposition;
import uniolunisaar.adam.ds.logics.Constants;
import uniolunisaar.adam.ds.logics.Constants.Container;
import uniolunisaar.adam.ds.logics.Formula;
import uniolunisaar.adam.ds.logics.FormulaBinary;
import uniolunisaar.adam.ds.logics.FormulaUnary;
import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.ds.logics.IOperatorBinary;
import uniolunisaar.adam.ds.logics.IOperatorUnary;
import uniolunisaar.adam.ds.logics.ltl.ILTLFormula;
import uniolunisaar.adam.ds.logics.ltl.LTLOperators;
import static uniolunisaar.adam.ds.logics.ltl.LTLOperators.Binary.AND;
import static uniolunisaar.adam.ds.logics.ltl.LTLOperators.Binary.BIMP;
import static uniolunisaar.adam.ds.logics.ltl.LTLOperators.Binary.IMP;
import static uniolunisaar.adam.ds.logics.ltl.LTLOperators.Binary.OR;
import static uniolunisaar.adam.ds.logics.ltl.LTLOperators.Binary.R;
import static uniolunisaar.adam.ds.logics.ltl.LTLOperators.Binary.U;
import static uniolunisaar.adam.ds.logics.ltl.LTLOperators.Binary.W;
import static uniolunisaar.adam.ds.logics.ltl.LTLOperators.Unary.F;
import static uniolunisaar.adam.ds.logics.ltl.LTLOperators.Unary.G;
import static uniolunisaar.adam.ds.logics.ltl.LTLOperators.Unary.NEG;
import static uniolunisaar.adam.ds.logics.ltl.LTLOperators.Unary.X;
import uniolunisaar.adam.ds.logics.ltl.flowltl.RunOperators;
import uniolunisaar.adam.logic.parser.logics.flowltl.FlowLTLParser;
import uniolunisaar.adam.logic.transformers.pn2aiger.AigerRenderer;

/**
 *
 * @author Manuel Gieseking
 */
public class FlowLTLTransformerHyperLTL {

    public static String toMCHyperFormat(IOperatorUnary<?> op) {
        if (op instanceof LTLOperators.Unary) {
            switch ((LTLOperators.Unary) op) {
                case F:
                    return "F";
                case G:
                    return "G";
                case NEG:
                    return "Neg";
                case X:
                    return "X";
            }
        }
        throw new RuntimeException("Only LTL operators are supported.");
    }

    public static String toMCHyperFormat(IOperatorBinary<?, ?> op) {
        if (op instanceof LTLOperators.Binary) {
            switch ((LTLOperators.Binary) op) {
                case AND:
                    return "And";
                case OR:
                    return "Or";
                case IMP:
                    return "Implies";
                case BIMP:
                    return "Eq";
                case U:
                    return "Until";
                case W:
                    return "WUntil";
                case R:
                    return "Release";
            }
            throw new RuntimeException("Forgotten to provide a case for transforming operator " + op + ".");
        } else if (op instanceof RunOperators.Binary) {
            if (op == RunOperators.Binary.AND) {
                return "And";
            } else {
                return "Or";
            }
        } else if (op instanceof RunOperators.Implication) {
            return "Implies";
        } else {
            throw new RuntimeException("Only LTL operators and run operators are supported.");
        }
    }

    private static String subformulaToMCHyperFormat(IFormula formula) {
        if (formula instanceof Constants.True) {
            return "(Const True)";
        } else if (formula instanceof Constants.False) {
            return "(Const False)";
        } else if (formula instanceof Constants.Container) {
            return "(AP \"" + ((Container) formula).toString() + "\" 0)";
        } else if (formula instanceof AtomicProposition) {
            return "(AP \"" + AigerRenderer.OUTPUT_PREFIX + ((AtomicProposition) formula).toString() + "\" 0)";
        } else if (formula instanceof Formula) {
            return subformulaToMCHyperFormat(((Formula) formula).getPhi());
        } else if (formula instanceof FormulaUnary<?, ?>) {
//            FormulaUnary<ILTLFormula, IOperatorUnary<ILTLFormula>> f = (FormulaUnary<ILTLFormula, IOperatorUnary<ILTLFormula>>) formula;
            FormulaUnary<?, ?> f = (FormulaUnary<?, ?>) formula;
            return "(" + toMCHyperFormat(f.getOp()) + " " + subformulaToMCHyperFormat(f.getPhi()) + ")";
        } else if (formula instanceof FormulaBinary<?, ?, ?>) {
//            FormulaBinary<IFormula, IOperatorBinary<IFormula, IFormula>, IFormula> f = (FormulaBinary<IFormula, IOperatorBinary<IFormula, IFormula>, IFormula>) formula;
            FormulaBinary<?, ?, ?> f = (FormulaBinary<?, ?, ?>) formula;
            return "(" + toMCHyperFormat(f.getOp()) + " " + subformulaToMCHyperFormat(f.getPhi1()) + " " + subformulaToMCHyperFormat(f.getPhi2()) + ")";
        } else {
            throw new RuntimeException("Forgotten to handle is subformula in the transformation to HyperLTL: '" + formula + "'.");
        }
    }

    public static String toMCHyperFormat(IFormula formula) {
        return "Forall " + subformulaToMCHyperFormat(formula);
    }

    /**
     * The replacement of atomic propositions is not that nice and fault
     * tolerant.
     *
     * @param net
     * @param formula
     * @return
     * @throws ParseException
     * @deprecated
     */
    @Deprecated
    public static String toMCHyperFormat(PetriNet net, String formula) throws ParseException {
        // convert to prefix
        formula = FlowLTLParser.parse(net, formula).toPrefixString();
//        System.out.println(formula);
        formula = formula.replace("IMP(", "(Implies ");
        formula = formula.replace("AND(", "(And ");
        formula = formula.replace("OR(", "(Or ");
        formula = formula.replace("NEG(", "(Neg ");
        formula = formula.replace("G(", "(G ");
        formula = formula.replace("F(", "(F ");
        formula = formula.replace("X(", "(X ");
        formula = formula.replace("TRUE", "(Const True)");
        formula = formula.replace("FALSE", "(Const False)");
        formula = formula.replace(",", " ");

        for (Place p : net.getPlaces()) {
            formula = formula.replace(" " + p.getId() + " ", " @#" + p.getId() + "#@ "); //todo; make it better with regular expressions
            formula = formula.replace(" " + p.getId() + ")", " @#" + p.getId() + "#@)");
        }
        for (Transition t : net.getTransitions()) {
            formula = formula.replace(" " + t.getId() + " ", " @#" + t.getId() + "#@ ");//todo; make it better with regular expressions
            formula = formula.replace(" " + t.getId() + ")", " @#" + t.getId() + "#@)");
        }
        for (Place p : net.getPlaces()) {
            formula = formula.replace("@#" + p.getId() + "#@", "(AP \"#out#_" + p.getId() + "\" 0)");
        }
        for (Transition t : net.getTransitions()) {
            formula = formula.replace("@#" + t.getId() + "#@", "(AP \"#out#_" + t.getId() + "\" 0)");
        }

        formula = "Forall " + formula;
        return formula;
    }

}
