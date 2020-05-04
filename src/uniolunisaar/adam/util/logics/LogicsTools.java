package uniolunisaar.adam.util.logics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.logics.Formula;
import uniolunisaar.adam.ds.logics.FormulaBinary;
import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.ds.logics.IOperatorBinary;
import uniolunisaar.adam.ds.logics.ctl.CTLFormula;
import uniolunisaar.adam.ds.logics.ctl.CTLOperators;
import uniolunisaar.adam.ds.logics.ctl.ICTLFormula;
import uniolunisaar.adam.ds.logics.ctl.flowctl.FlowCTLFormula;
import uniolunisaar.adam.ds.logics.ctl.flowctl.separate.RunCTLSeparateFormula;
import uniolunisaar.adam.ds.logics.ctl.flowctl.RunCTLOperators;
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

    public static List<FlowLTLFormula> getFlowLTLFormulas(IFormula formula) {
        List<FlowLTLFormula> flowFormulas = new ArrayList<>();
        if (formula instanceof FlowLTLFormula) {
            flowFormulas.add((FlowLTLFormula) formula);
            return flowFormulas;
        } else if (formula instanceof ILTLFormula) {
            return flowFormulas;
        } else if (formula instanceof RunLTLFormula) {
            return getFlowLTLFormulas(((RunLTLFormula) formula).getPhi());
        } else if (formula instanceof FormulaBinary<?, ?, ?>) {
            FormulaBinary<?, ?, ?> binF = (FormulaBinary<?, ?, ?>) formula;
            flowFormulas.addAll(getFlowLTLFormulas(binF.getPhi1()));
            flowFormulas.addAll(getFlowLTLFormulas(binF.getPhi2()));
        }
        return flowFormulas;
    }

    public static List<FlowCTLFormula> getFlowCTLFormulas(IFormula formula) {
        List<FlowCTLFormula> flowFormulas = new ArrayList<>();
        if (formula instanceof FlowCTLFormula) {
            flowFormulas.add((FlowCTLFormula) formula);
            return flowFormulas;
        } else if (formula instanceof ICTLFormula) {
            return flowFormulas;
        } else if (formula instanceof Formula<?>) {
            return getFlowCTLFormulas(((Formula<?>) formula).getPhi());
        } else if (formula instanceof FormulaBinary<?, ?, ?>) {
            FormulaBinary<?, ?, ?> binF = (FormulaBinary<?, ?, ?>) formula;
            flowFormulas.addAll(getFlowCTLFormulas(binF.getPhi1()));
            flowFormulas.addAll(getFlowCTLFormulas(binF.getPhi2()));
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

    public static ILTLFormula convert2LTL(IFormula f) throws NotConvertableException {
        if (f instanceof IFlowFormula) {
            throw new NotConvertableException("The formula contains a flow formula '" + f.toSymbolString() + "'. Hence, we cannot transform it to an LTL formula.");
        } else if (f instanceof RunLTLFormula) {
            return new LTLFormula(convert2LTL(((RunLTLFormula) f).getPhi()));
        } else if (f instanceof IRunFormula && f instanceof FormulaBinary) {
            FormulaBinary<?, ?, ?> form = ((FormulaBinary<?, ?, ?>) f);
            IOperatorBinary<?, ?> op = form.getOp();
            ILTLFormula f1 = convert2LTL(form.getPhi1());
            ILTLFormula f2 = convert2LTL(form.getPhi2());
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

    public static ICTLFormula convert2CTL(IFormula f) throws NotConvertableException {
        if (f instanceof IFlowFormula) {
            throw new NotConvertableException("The formula contains a flow formula '" + f.toSymbolString() + "'. Hence, we cannot transform it to a CTL formula.");
        } else if (f instanceof RunCTLSeparateFormula) {
            return new CTLFormula(convert2CTL(((RunCTLSeparateFormula) f).getPhi()));
        } else if (f instanceof IRunFormula && f instanceof FormulaBinary) {
            FormulaBinary<?, ?, ?> form = ((FormulaBinary<?, ?, ?>) f);
            IOperatorBinary<?, ?> op = form.getOp();
            ICTLFormula f1 = convert2CTL(form.getPhi1());
            ICTLFormula f2 = convert2CTL(form.getPhi2());
            if (op instanceof RunCTLOperators.Binary) {
                if (op.equals(RunCTLOperators.Binary.AND)) {
                    return new CTLFormula(f1, CTLOperators.Binary.AND, f2);
                } else if (op.equals(RunCTLOperators.Binary.OR)) {
                    return new CTLFormula(f1, CTLOperators.Binary.OR, f2);
                } else if (op.equals(RunCTLOperators.Binary.IMP)) {
                    return new CTLFormula(f1, CTLOperators.Binary.IMP, f2);
                } else if (op.equals(RunCTLOperators.Binary.BIMP)) {
                    return new CTLFormula(f1, CTLOperators.Binary.BIMP, f2);
                }
            }
            throw new RuntimeException("Not every possible case matched.");
        } else {
            return (ICTLFormula) f;
        }
    }

}
