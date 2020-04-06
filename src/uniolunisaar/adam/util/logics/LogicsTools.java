package uniolunisaar.adam.util.logics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.logics.FormulaBinary;
import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.ds.logics.IOperatorBinary;
import uniolunisaar.adam.ds.logics.ltl.ILTLFormula;
import uniolunisaar.adam.ds.logics.ltl.LTLFormula;
import uniolunisaar.adam.ds.logics.ltl.LTLOperators;
import uniolunisaar.adam.ds.logics.ltl.flowltl.FlowLTLFormula;
import uniolunisaar.adam.ds.logics.flowlogics.IFlowFormula;
import uniolunisaar.adam.ds.logics.flowlogics.IRunFormula;
import uniolunisaar.adam.ds.logics.ltl.flowltl.RunLTLFormula;
import uniolunisaar.adam.ds.logics.flowlogics.RunOperators;
import uniolunisaar.adam.ds.petrinet.PetriNetExtensionHandler;
import uniolunisaar.adam.exceptions.logics.NotConvertableException;

/**
 *
 * @author Manuel Gieseking
 */
public class LogicsTools {

    public enum TransitionSemantics {
        INGOING,
        OUTGOING
    }

    public static List<FlowLTLFormula> getFlowFormulas(IFormula formula) {
        List<FlowLTLFormula> flowFormulas = new ArrayList<>();
        if (formula instanceof FlowLTLFormula) {
            flowFormulas.add((FlowLTLFormula) formula);
            return flowFormulas;
        } else if (formula instanceof ILTLFormula) {
            return flowFormulas;
        } else if (formula instanceof RunLTLFormula) {
            return getFlowFormulas(((RunLTLFormula) formula).getPhi());
        } else if (formula instanceof FormulaBinary<?, ?, ?>) {
            FormulaBinary<?, ?, ?> binF = (FormulaBinary<?, ?, ?>) formula;
            flowFormulas.addAll(getFlowFormulas(binF.getPhi1()));
            flowFormulas.addAll(getFlowFormulas(binF.getPhi2()));
        }
        return flowFormulas;
    }

    public static ILTLFormula getFairness(PetriNet net) {
        // Add Fairness to the formula
        Collection<ILTLFormula> elements = new ArrayList<>();
        for (Transition t : net.getTransitions()) {
            if (PetriNetExtensionHandler.isStrongFair(t)) {
                elements.add(FormulaCreator.createStrongFairness(t));
            }
            if (PetriNetExtensionHandler.isWeakFair(t)) {
                elements.add(FormulaCreator.createWeakFairness(t));
            }
        }
        return FormulaCreator.bigWedgeOrVeeObject(elements, true);
    }

    public static RunLTLFormula addFairness(PetriNet net, RunLTLFormula formula) {
        ILTLFormula fairness = getFairness(net);
        return (!fairness.toString().equals("TRUE")) ? new RunLTLFormula(fairness, RunOperators.Implication.IMP, formula) : formula;
    }

    public static ILTLFormula addFairness(PetriNet net, ILTLFormula formula) {
        ILTLFormula fairness = getFairness(net);
        return (!fairness.toString().equals("TRUE")) ? new LTLFormula(fairness, LTLOperators.Binary.IMP, formula) : formula;
    }

    public static ILTLFormula convert(IFormula f) throws NotConvertableException {
        if (f instanceof IFlowFormula) {
            throw new NotConvertableException("The formula contains a flow formula '" + f.toSymbolString() + "'. Hence, we cannot transform it to an LTL formula.");
        } else if (f instanceof RunLTLFormula) {
            return new LTLFormula(convert(((RunLTLFormula) f).getPhi()));
        } else if (f instanceof IRunFormula && f instanceof FormulaBinary) {
            FormulaBinary<?, ?, ?> form = ((FormulaBinary<?, ?, ?>) f);
            IOperatorBinary<?, ?> op = form.getOp();
            ILTLFormula f1 = convert(form.getPhi1());
            ILTLFormula f2 = convert(form.getPhi2());
            if (op instanceof RunOperators.Binary) {
                if (op.equals(RunOperators.Binary.AND)) {
                    return new LTLFormula(f1, LTLOperators.Binary.AND, f2);
                } else if (op.equals(RunOperators.Binary.OR)) {
                    return new LTLFormula(f1, LTLOperators.Binary.OR, f2);
                }
            } else if (op instanceof RunOperators.Implication) {
                return new LTLFormula(f1, LTLOperators.Binary.IMP, f2);
            }
            throw new RuntimeException("Not every possible case matched.");
        } else {
            return (ILTLFormula) f;
        }
    }

}
